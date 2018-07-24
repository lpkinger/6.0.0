package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.b2c_erp.buyer.model.B2cOrder;
import com.uas.api.b2c_erp.buyer.model.B2cOrderDetail;
import com.uas.api.b2c_erp.buyer.model.Purchase;
import com.uas.api.b2c_erp.buyer.model.PurchaseDetail;
import com.uas.b2c.service.seller.SendPurchaseToB2CService;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.scm.ProductBatchUUIdService;
import com.uas.erp.service.scm.PurchaseService;

/**
 * erp端上传采购单至商城
 * @author XiaoST 
 * @data  2016年9月18日 上午10:19:46
 */
@Component("b2cpurchasetask")
@EnableAsync
@EnableScheduling
public class B2CPurchaseTask {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
    @Autowired
    private SendPurchaseToB2CService sendPurchaseToB2CService;
    @Autowired
    private PurchaseService purchaseService;
	
	private static List<Master> masters = null;

	public void execute() {
		if (masters == null) {
			masters = enterpriseService.getMasters();
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {
				SpObserver.putSp(master.getMa_name());
				uploadTask(master);//上传任务
			}
		}
		SpObserver.putSp(sob);
	}

	/**
	 * 上传前，判断采购单类型，状态
	 * @param idStr
	 */
	public Purchase beforeUploadTask(Long id,String code) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select pu_code,pu_id ,pu_shipaddresscode,pu_buyername from purchase where pu_id=? and pu_code=? and pu_ordertype='B2C' and nvl(pu_sendstatus,' ')not in('已上传','上传中')", id,code);
		if (rs.next()) {
			Purchase purchase = new Purchase();
			purchase.setId(rs.getLong("pu_id"));
			// 获取采购员姓名，采购员电话employee em_tel,em_mobile, enuu 企业UU,email,name,tel
			SqlRowList rs2 = baseDao.queryForRowSet(
					"select em_tel,em_mobile,em_email,en_uu from employee left join enterprise on en_id=em_enid where em_id=?",
					SystemSession.getUser().getEm_id());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("area", rs.getString("pu_shipaddresscode"));
			map.put("name", rs.getString("pu_buyername"));
			map.put("email", rs2.getString("em_email"));
			map.put("tel", rs2.getString("em_mobile"));
			map.put("enuu", rs2.getString("en_uu"));
			purchase.setShipAddress(FlexJsonUtil.toJson(map));
			purchase.setTotal(0.0);
			purchase.setCreateTime(new Date());
			List<PurchaseDetail> purchaseDetail = new ArrayList<PurchaseDetail>();
			SqlRowList rs3 = baseDao
					.queryForRowSet("select pb_b2bbatchcode,pb_price,pb_qty,pb_price*pb_qty total,go_unit,pb_erpunit,pb_id from B2C$PURCHASEBATCH left join B2C$GOODSONHAND on go_uuid=gu_uuid and go_prodcode=gu_prodcode where pb_puid=?",
							 id);
			while (rs3.next()){
				PurchaseDetail detail = new PurchaseDetail();
				double rate = productBatchUUIdService.getUnitRate(rs3.getString("pb_erpunit"), rs3.getString("go_unit"));
				detail.setBatchCode(rs3.getString("pb_b2bbatchcode"));
				detail.setDetno((short) 0);
				detail.setId(rs3.getLong("pb_id"));
				detail.setPrice(rs3.getDouble("pb_price")/rate);
				detail.setTotal(rs3.getDouble("total"));
				detail.setQty(rs3.getDouble("pb_qty")*rate);
				purchaseDetail.add(detail);
			}
			purchase.setDetails(purchaseDetail);
			baseDao.execute("update purchase set pu_sendstatus='上传中' where pu_id=?",id);
			return purchase;
		}else{//该任务为异常任务无法执行
			baseDao.execute("update b2c$task set ta_finishstatus='2' where ta_docaller='Purchase' and ta_doid=?",id);
		}
		return null;
	}
	/**
	 * 上传失败后，修改采购单的状态为'待上传'
	 * @param id
	 */
	public void onUploadedFailed (Long id,String msg) {
		baseDao.execute("update purchase set pu_sendstatus= '待上传' where pu_id=?",id);
		baseDao.execute("update b2c$task set ta_errlog='"+msg+"', ta_errnum=nvl(ta_errnum,0)+1 where ta_docaller='Purchase' and ta_doid=?",id);
	}
	/**
	 * 上传成功后，修改采购单的状态为'已上传'
	 * @param id
	 */
	public boolean onUploadedSuccess(Long id,double total,double tax) {
		boolean bool = baseDao.execute("update purchase set pu_sendstatus= '已上传',pu_total="+total+",pu_taxtotal="+(total-tax)+" where pu_id=?",id);
		boolean bool1 = baseDao.execute("update b2c$task set ta_finishstatus='1', ta_finishtime=sysdate where ta_docaller='Purchase' and ta_doid=?",id);
	
		return bool&&bool1;
	}
	
	/**
	 * @param master
	 * @return
	 */
	public void uploadTask(Master master){
		//完成状态用数值，0的待完成，1的已完成，2的异常不能执行，-1 已作废
		SqlRowList rs = baseDao.queryForRowSet("select * from (select ta_id ,ta_docaller ,ta_docode ,ta_doid ,ta_actiontime ,"+
				"ta_finishstatus ,ta_finishtime ,ta_errlog ,ta_errnum "+
				"from b2c$task where NVL(ta_finishstatus,'0')='0' and nvl(ta_errnum,0)<5 and ta_docaller='Purchase') where rownum<=20");
		while(rs.next()){
			Long id = rs.getLong("ta_doid");
			String code =rs.getString("ta_docode");
			 try {
			    	Purchase purchase = beforeUploadTask(id,code);
					if(purchase != null){
						B2cOrder b2cOrder = sendPurchaseToB2CService.save(purchase,master);
						final Set<B2cOrderDetail> details = b2cOrder.getOrderDetails();
						for(B2cOrderDetail detail : details) {
							baseDao.execute("update purchasedetail set pd_b2ccode=?,pd_price=? where pd_puid=? and pd_b2cbatchcode=?", b2cOrder.getId(),
									detail.getPrice(),id, detail.getBatchCode());
						}
						boolean bool = onUploadedSuccess(id,b2cOrder.getPrice(),b2cOrder.getTaxes());
						if (bool) {
							//根据配置选择是否生成预付款申请单
						    boolean autoTurn = baseDao.isDBSetting("B2CSetting", "autoTurnToPayPlease");
						    if (autoTurn) {
						    	purchaseService.turnToPayPlease(id.intValue());
							}
						}
					}
				} catch (Exception e) {
					onUploadedFailed(id,e.getMessage());
					e.printStackTrace();
				}
		}
	}
	
}
