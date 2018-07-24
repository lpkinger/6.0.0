package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.b2c_erp.seller.model.Invoice;
import com.uas.api.b2c_erp.seller.model.InvoiceDetail;
import com.uas.b2c.service.seller.SaleInvoiceService;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.scm.ProductBatchUUIdService;

/**
 * erp端商城销售出货单过账 上传至商城
 * @author XiaoST 
 * @data  2016年9月14日 下午2:21:29
 */
@Component("b2csaleouttask")
@EnableAsync
@EnableScheduling
public class B2CSaleOutTask {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
    @Autowired
    private SaleInvoiceService saleInvoiceService;
	
	private static List<Master> masters = null;

	public void execute() {
		if (masters == null) {
			masters = enterpriseService.getMasters();
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {
				SpObserver.putSp(master.getMa_name());
				saleOutUploadTask(master);//上传任务
			}
		}
		SpObserver.putSp(sob);
	}

	/**
	 * 上传前，判断是否符合销售出货单上传条件
	 * @param idStr
	 */
	public Invoice beforeUploadTask(Long id,String code) {
		/**
		 * 出货单已过帐，未上传，
		 */
		SqlRowList rs = baseDao.queryForRowSet("select sa_pocode orderId,sysdate createTime, 0 total,sa_toplace jsonSdAddress,pd_inoutno sendcode,"
					+"pi_logisticscompany,pi_logisticscode,pd_ordercode from prodinout "
					+"left join prodiodetail on pd_piid=pi_id left join sale on sa_code=pd_ordercode left join saledetail on sd_said=sa_id "
					+"and sd_detno=pd_orderdetno where pi_id=? and pi_inountno=? and nvl(pi_sendstatus,' ') not in('已上传','上传中')"
					+"and pi_statuscode='POSTED' and sa_ordertype='B2C' and nvl(sa_pocode,' ')<>' ' ",id);
		if(rs.next()){
			Invoice invoice = new Invoice();
			invoice.setJsonSdAddress(rs.getString("jsonSdAddress"));
			invoice.setOrderId(rs.getLong("orderId"));
			invoice.setSendcode(rs.getString("sendcode"));
			invoice.setCompanyName(rs.getString("pi_logisticscompany"));
			invoice.setCompanyNumber(rs.getString("pi_logisticscode"));
			List<InvoiceDetail> details = new ArrayList<InvoiceDetail>();
			SqlRowList rs2 = baseDao.queryForRowSet(
					"select pd_pdno,sd_price,sd_qty,sd_total,go_unit,gd_erpunit from prodiodetail left join "
							+ "saledetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno "
							+ "left join goodsdetail on gd_b2bbatchcode=sd_b2cbatchcode left join b2c$goodsonhand "
							+ "on gd_uuid=go_uuid and gd_prodcode=go_prodcode where sd_code=?", rs.getString("pd_ordercode"));
			while (rs2.next()) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				double rate = productBatchUUIdService.getUnitRate(rs2.getString("gd_erpunit"), rs2.getString("go_unit"));
				invoiceDetail.setDetno(Short.valueOf(rs.getString("pd_pdno")));
				invoiceDetail.setPrice(rs.getDouble("sd_price") / rate);
				invoiceDetail.setQty(rs.getDouble("sd_price") * rate);
				invoiceDetail.setTotal(rs.getDouble("sd_total"));
				details.add(invoiceDetail);
			}
			invoice.setDetails(details);
			baseDao.execute("update prodinout set pi_sendstatus= '上传中' where pi_id=?",id);
			return invoice;
		}else{//该任务为异常任务无法执行
			baseDao.execute("update b2c$task set ta_finishstatus='2' where ta_docaller='ProdInOut!Sale' and ta_doid=?",id);
		}
		return null;
	}
	/**
	 * 上传失败后，
	 * @param idStr
	 */
	public void onUploadedFailed (Long id,String msg) {
		baseDao.execute("update prodinout set pi_sendstatus= '待上传' where pi_id=?",id);
		baseDao.execute("update b2c$task set ta_errlog='"+msg+"', ta_errnum=nvl(ta_errnum,0)+1 where ta_docaller='ProdInOut!Sale' and ta_doid=?",id);
	}
	/**
	 * 上传成功后，修改规则表的状态为'已上传'
	 * @param idStr
	 */
	public void onUploadedTaskSuccess(Long id) {
		baseDao.execute("update prodinout set pi_sendstatus= '已上传' where pi_id=?",id);
		baseDao.execute("update b2c$task set ta_finishstatus='1', ta_finishtime=sysdate where ta_docaller='ProdInOut!Sale' and ta_doid=?",id);
	}
	
	/**
	 * @param master
	 * @return
	 */
	public void saleOutUploadTask(Master master){
		SqlRowList rs  = baseDao.queryForRowSet("select * from (select ta_id ,ta_docaller ,ta_docode ,ta_doid ,ta_actiontime ,"+
				"ta_finishstatus ,ta_finishtime ,ta_errlog ,ta_errnum "+
				"from b2c$task where ta_docaller='ProdInOut!Sale' and NVL(ta_finishstatus,'0')='0' and nvl(ta_errnum,0)<5) where rownum<=20");
		while(rs.next()){
			Long id = rs.getLong("ta_doid");
			String code =rs.getString("ta_docode");
		    try {
				Invoice invoice = beforeUploadTask(id,code);
				if(invoice != null){
					saleInvoiceService.send(invoice,master);
					onUploadedTaskSuccess(id);
				}
			} catch (Exception e) {
				onUploadedFailed(id,e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
