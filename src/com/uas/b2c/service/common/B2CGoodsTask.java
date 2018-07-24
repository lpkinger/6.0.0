package com.uas.b2c.service.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.b2c_erp.seller.model.GoodsFUas;
import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;
import com.uas.b2c.service.seller.B2CGoodsUpAndDownService;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2c.GoodsChangeService;
import com.uas.erp.service.b2c.GoodsUpApplicationService;
import com.uas.erp.service.common.EnterpriseService;

/**
 * erp端上传到上，下架，变更单至商城
 * @author XiaoST 
 * @date 2016年9月12日 下午2:38:45
 */
@Component("b2cgoodstask")
@EnableAsync
@EnableScheduling
public class B2CGoodsTask {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
    private B2CGoodsUpAndDownService b2cGoodsUpAndDownService;
    @Autowired
    private GoodsChangeService goodsChangeService;
    @Autowired
    private GoodsUpApplicationService goodsUpApplicationService;
    
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
	 * 上传上架申请单前，修改上架单状态
	 * @param id
	 * @param code
	 */
	public List<GoodsSimpleUas> beforeGoodsUploadTask(Long id,String code) {
		SqlRowList rs = baseDao.queryForRowSet("select gd_id from goodsup left join goodsdetail on gd_guid=gu_id where gu_id=? and gu_code=? and gu_statuscode='AUDITED' and gd_sendstatus in('已上传','上传中')",id,code);
		if(rs.next()){//该任务为异常任务无法执行
			baseDao.execute("update b2c$task set ta_finishstatus='2' where ta_docaller='GoodsUp' and ta_doid=?",id);
		}else{
			List<GoodsSimpleUas> simpleGoodses = goodsUpApplicationService.sendData(Integer.valueOf(id.toString()));
			baseDao.execute("update goodsdetail set gd_sendstatus='上传中' where gd_guid=?",id);
			return simpleGoodses;
		}
		return null;
	}
	/**
	 * 上传上架申请单成功后，修改规则表的状态为'已上传'
	 * @param idS
	 */
	public void onUploadedGoodsSuccess(Long id) {
		/**
		 * @Tip 将上架成功的数据写入到商城批次数据表goodspwonhand中,并记录日志
		 */
		Object ob = baseDao.getFieldDataByCondition("goodsup", "gu_code",
				"gu_id=" + id);
		baseDao.callProcedure("SP_GOODSPWONHAND_UP",
				new Object[] { ob });
		baseDao.execute("update b2c$task set ta_finishstatus='1', ta_finishtime=sysdate where ta_docaller='GoodsUp' and ta_doid=?",id);
	}
	
	/**
	 * 上传上架申请单失败后，修改规则表的状态为'待上传'
	 * @param id
	 */
	public void onUploadedGoodsFailed (Long id,String msg) {
		baseDao.execute("update goodsdetail set gd_sendstatus='待上传' where gd_guid=?",id);
		baseDao.execute("update b2c$task set ta_errlog='"+msg+"', ta_errnum=nvl(ta_errnum,0)+1 where ta_docaller='GoodsUp' and ta_doid=?",id);
	}
	
	/**
	 * 上传上架变更，下架单前，修改上架变更单状态
	 * @param id
	 * @param code
	 */
	public List<GoodsSimpleUas> beforeGoodsChangeUploadTask(Long id,String code) {// 变更
		//判断单据状态
		SqlRowList rs = baseDao.queryForRowSet("select gcd_id from goodschange left join goodschangedetail on gcd_gcid=gc_id where gc_id=? and gc_code=? and gc_statuscode='AUDITED' and gcd_sendstatus in('已上传','上传中')",id,code);
		if(rs.next()){//该任务为异常任务无法执行
			baseDao.execute("update b2c$task set ta_finishstatus='2' where ta_docaller='GoodsChange' and ta_doid=?",id);
		}else{
			List<GoodsSimpleUas> simpleGoodses = goodsChangeService.sendData(Integer.valueOf(id.toString()));
			baseDao.execute("update goodschangedetail set gcd_sendstatus='上传中' where gcd_gcid=?",id);
			return simpleGoodses;
		}
		return null;
	}
	
	/**
	 * 上传上架申请单成功后，修改规则表的状态为'已上传'
	 * @param id
	 */
	public void onUploadedGoodsChangeSuccess(Long id) {
		// 更新上传状态为上传成功
		baseDao.execute("update goodschangedetail set gcd_sendstatus='已上传' where gcd_gcid=?",id);
		Object ob = baseDao.getFieldDataByCondition("goodschange", "gc_code",
				"gc_id=" + id);
		baseDao.callProcedure("SP_GOODSPWONHAND_OFF",
				new Object[] { ob });
		// 上传成功修改原来数据goodsdetail 表中数据
		baseDao.execute("update goodsdetail set gd_qty=gd_qty-(select nvl(gcd_offqty,0) from goodschangedetail where"
				+ " gcd_gcid=? and gcd_barcode=gd_barcode),gd_sendstatus='已上传'"
				+ " where exists (select 1 from goodschangedetail where gcd_gcid=? and gcd_barcode=gd_barcode )",id,id);
		baseDao.execute("update goodsdetail set (gd_madedate,gd_minbuyqty,gd_minpackqty,gd_price,gd_remark,gd_usdprice,gd_deliverytime,gd_hkdeliverytime)=(select gcd_madedate,gcd_minbuyqty,gcd_minpackqty,gcd_price,gcd_remark,gcd_usdprice,gcd_deliverytime,gcd_hkdeliverytime from goodschangedetail where"
				+ " gcd_gcid=? and gcd_barcode=gd_barcode "
				+ ")"
				+ " where exists(select 1 from goodschangedetail where gcd_gcid=? and gcd_barcode=gd_barcode)",id,id);
		//更新上架申请单中的未含税单价
		baseDao.execute("update Goodsdetail set gd_costprice=round(nvl(gd_price,0)/(1+nvl(gd_taxrate,0)/100),6) where gd_barcode in(select gcd_barcode from goodschangedetail where gcd_gcid=?)",id);
		baseDao.execute("update b2c$task set ta_finishstatus='1', ta_finishtime=sysdate where ta_docaller in('GoodsChange','GoodsOff') and ta_doid=?",id);
	}
	
	/**
	 * 上传上架变更单失败后，修改规则表的状态为'待上传'
	 * @param id
	 */
	public void onUploadedGoodsChangeFailed (Long id,String msg) {
		baseDao.execute("update goodschangedetail set gcd_sendstatus='待上传' where gcd_gcid=?",id);
		baseDao.execute("update b2c$task set ta_errlog='"+msg+"', ta_errnum=nvl(ta_errnum,0)+1 where ta_docaller in('GoodsChange','GoodsOff') and ta_doid=?",id);
	}
	
	public String beforeGoodsOffUploadTask(Long id,String code) {// 下架
		//判断单据状态
		SqlRowList rs = baseDao.queryForRowSet("select gcd_id from goodschange left join goodschangedetail on gcd_gcid=gc_id where gc_id=? and gc_code=? and gc_statuscode='AUDITED' and gcd_sendstatus in('已上传','上传中')",id,code);
		if(rs.next()){//该任务为异常任务无法执行
			baseDao.execute("update b2c$task set ta_finishstatus='2' where ta_docaller='GoodsOff' and ta_doid=?",id);
		}else{
			rs = baseDao.queryForRowSet("select gcd_b2bbatchcode from goodschangedetail where gcd_gcid=" + id);
			StringBuffer strs = new StringBuffer();
			if (rs.next()) {
				for (Map<String, Object> map : rs.getResultList()) {
					if (map.get("gcd_b2bbatchcode") != null) {
						strs.append(map.get("gcd_b2bbatchcode") + ",");
					}
				}
				return strs.substring(0, strs.length() - 1);
			}
		}
		return null;
	}
	/**
	 * @param master
	 * @return
	 */
	public void uploadTask(Master master){
		//完成状态用数值，0的待完成，1的已完成，2的异常不能执行
		SqlRowList rs = baseDao.queryForRowSet("select * from (select ta_id ,ta_docaller ,ta_docode ,ta_doid ,ta_actiontime ,"+
				"ta_finishstatus ,ta_finishtime ,ta_errlog ,ta_errnum "+
				"from b2c$task where NVL(ta_finishstatus,'0')='0' and nvl(ta_errnum,0)<5 and ta_docaller in('GoodsUp','GoodsChange','GoodsOff') order by ta_actiontime asc) where rownum<=20");
		while(rs.next()){
			Object caller = rs.getObject("ta_docaller");
			Long id = rs.getLong("ta_doid");
			String code =rs.getString("ta_docode");
			if("GoodsUp".equals(caller)){//上架单
			    try {
			    	List<GoodsSimpleUas> goods = beforeGoodsUploadTask(id,code);
					if(goods != null){
						final List<GoodsFUas> goodsesFUas = b2cGoodsUpAndDownService.upToB2C(goods,master);
						if(!CollectionUtil.isEmpty(goods)){
							baseDao.getJdbcTemplate().batchUpdate(
									"update goodsdetail set gd_sendstatus='已上传',gd_b2bbatchcode=? where gd_id=?",
									new BatchPreparedStatementSetter() {
	
										@Override
										public void setValues(PreparedStatement ps,
												int index) throws SQLException {
											GoodsFUas goods = goodsesFUas.get(index);
											ps.setObject(1, goods.getBatchCode());
											ps.setObject(2, goods.getSourceId());
										}
	
										@Override
										public int getBatchSize() {
											return goodsesFUas.size();
										}
									});
							onUploadedGoodsSuccess(id);
						}
					}
				} catch (Exception e) {
					onUploadedGoodsFailed(id,e.getMessage());
					e.printStackTrace();
				}
			}else if("GoodsChange".equals(caller)){//变更单
				try {
			    	List<GoodsSimpleUas> goods = beforeGoodsChangeUploadTask(id,code);
					if(!CollectionUtil.isEmpty(goods)){
						b2cGoodsUpAndDownService.updateGoodses(goods,master);
						onUploadedGoodsChangeSuccess(id);
					}
				} catch (Exception e) {
					onUploadedGoodsChangeFailed(id,e.getMessage());
					e.printStackTrace();
				}
			}else if("GoodsOff".equals(caller)){//下架
				try {
			    	String batchCodes = beforeGoodsOffUploadTask(id,code);
					if(StringUtil.hasText(batchCodes)){
						Map<String, List<String>> map = b2cGoodsUpAndDownService.pulloff(batchCodes,master);
						Object ob = baseDao.getFieldDataByCondition("goodschange", "gc_code",
								"gc_id=" + id);
						baseDao.callProcedure("SP_GOODSPWONHAND_OFF",
								new Object[] { ob });
						baseDao.execute("update goodschangedetail set gcd_sendstatus='已上传' where gcd_gcid=?",id);
						// 上传成功修改原来数据goodsdetail 表中数据
						baseDao.execute("update goodsdetail set gd_qty=0 ,gd_sendstatus='已上传'"
								+ " where exists (select 1 from goodschangedetail where gcd_barcode=gd_barcode and gcd_gcid=" + id + ")");
						baseDao.execute("update b2c$task set ta_finishstatus='1', ta_finishtime=sysdate where ta_docaller='GoodsOff' and ta_doid=?",id);
					}
				} catch (Exception e) {
					onUploadedGoodsChangeFailed(id,e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
}
