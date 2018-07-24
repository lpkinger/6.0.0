package com.uas.erp.service.scm.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.service.scm.MakeExpService;
import com.uas.erp.service.scm.MakeService;
import com.uas.erp.service.scm.PurchaseService;

@Service("makeExpService")
public class MakeExpServiceImpl implements MakeExpService{
	
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private PurchaseDao purchaseDao;
	
	@Override
	public void saveMakeExp(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeExp", "me_code='" + store.get("me_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[]{store});
		//保存Make
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeExp", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存MakeMaterial
		/*for(Map<Object, Object> map:gstore){
			map.put("mm_id", baseDao.getSeqId("MAKEMATERIAL_SEQ"));
			map.put("mm_status", "ENTERING");
		}*/
		/*List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "MakeMaterial");
		baseDao.execute(gridSql);*/
		baseDao.logger.save(caller, "me_id", store.get("me_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deleteMakeExp(int me_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + me_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, me_id);
		//删除Make
		baseDao.deleteById("MakeExp", "me_id", me_id);
		//记录操作
		baseDao.logger.delete(caller, "me_id", me_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, me_id);
	}
	
	@Override
	public void updateMakeExpById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/*List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);*/
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + store.get("me_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改Make
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeExp", "me_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "me_id", store.get("me_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public String[] printMakeExp(int ma_id, String caller, String reportName, String condition) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, ma_id);
		//执行打印操作
		String key = "12345678";
		String[] keys = new String[4];
		Des de = new Des();
		try {
			String name = URLEncoder.encode(reportName, "utf-8").toLowerCase();
	        keys[0] = de.toHexString(de.encrypt(name, key)).toUpperCase(); 
	        
	        String skey = URLEncoder.encode(key, "utf-8").toLowerCase(); 
	        keys[1] = de.toHexString(de.encrypt(skey, key)).toUpperCase();
	        
	        String cond = java.net.URLEncoder.encode(condition, "utf-8").toLowerCase(); 
			keys[2] = de.toHexString(de.encrypt(cond, key)).toUpperCase();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
	        String lyTime = sdf.format(new java.util.Date());  
	        String time = java.net.URLEncoder.encode(lyTime, "utf-8").toLowerCase(); 
	        keys[3] = de.toHexString(de.encrypt(time, key)).toUpperCase();
	        
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		baseDao.print("Make", "ma_id=" + ma_id, "MA_PRINTSTATUS", "MA_PRINTSTATUSCODE");
		//记录操作
		baseDao.logger.print(caller, "ma_id", ma_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, ma_id);
	    return keys;
	}
	
	@Override
	public void auditMakeExp(int me_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + me_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, me_id);
		//执行审核操作
		baseDao.audit("MakeExp", "me_id=" + me_id, "me_status", "me_statuscode", "me_auditdate", "me_auditman");
		//记录操作
		baseDao.logger.audit(caller, "me_id", me_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, me_id);
	}
	
	@Override
	public void resAuditMakeExp(int me_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + me_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("MakeExp", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "me_id", me_id);
	}
	
	@Override
	public void submitMakeExp(int me_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + me_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, me_id);
		//执行提交操作
		baseDao.submit("MakeExp", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "me_id", me_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, me_id);
	}
	
	@Override
	public void resSubmitMakeExp(int me_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeExp", "me_statuscode", "me_id=" + me_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, me_id);
		//执行反提交操作
		baseDao.resOperate("MakeExp", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "me_id", me_id);
		handlerService.afterResSubmit(caller, me_id);
	}

	@Override
	public void bannedMakeExp(int ma_id, String caller) {
		//执行禁用操作
		baseDao.banned("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='DISABLE", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.banned(caller, "ma_id", ma_id);
	}

	@Override
	public void resBannedMakeExp(int ma_id, String caller) {
		//执行反禁用操作
		baseDao.resOperate("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resBanned(caller, "ma_id", ma_id);
	}

	@Override
	public void getPOPrice(String me_code) {
		Object[] vendcode = baseDao.getFieldsDataByCondition("makeexp", new String[]{"me_vendcode","me_currency"}, "me_code='"+me_code+"'");
		if(vendcode!=null && vendcode[0]!=null && vendcode[1]!=null){
			SqlRowList rs = baseDao.queryForRowSet("select * from make where ma_tasktype='OS' and ma_expcode='"+me_code+"'");
			while(rs.next()){
				Object ma_id = rs.getObject("ma_id");
				double taxrate = 0;
				if (rs.getString("ma_statuscode").equals("FINISH")) {
					BaseUtil.showError("已经结案工单不能更新委外商");
				}
				if (rs.getObject("ma_madeqty") != null && rs.getDouble("ma_madeqty") > 0) {
					BaseUtil.showError("已有验收数量的委外单不能更新委外商信息");
				}
				// 到物料核价单取单价
				JSONObject obj = null;
				obj = purchaseDao.getPurchasePrice(vendcode[0].toString(), rs.getString("ma_prodcode"), vendcode[1].toString(), "委外", rs.getDouble("ma_qty"),"sysdate");
				if (obj == null) {
					BaseUtil.showError("当前供应商："+vendcode[0].toString()+",物料："+rs.getString("ma_prodcode")+"获取不到有效单价！");
				} else {
					double price = obj.getDouble("pd_price");
					taxrate = obj.getDouble("pd_rate");
					baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode[0].toString() + "', ma_currency='" + vendcode[1].toString() + "', ma_price=round(" + price + ",8), ma_total=round(" + price + "*ma_qty,2) ", "ma_id =" + ma_id);
					baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname,ma_rate)=(select ve_paymentcode,ve_payment,ve_name,cm_crrate from vendor left join currencysmonth on cm_crname=ve_currency where ve_code=ma_vendcode and cm_yearmonth=to_char(sysdate,'yyyymm')) where ma_id="
							+ ma_id);
					int argCount = baseDao.getCountByCondition("user_tab_columns",
							"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
					if (argCount == 2) {
						baseDao.execute("update make set (MA_APVENDCODE,MA_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=ma_vendcode) where ma_id="
								+ ma_id);
					}
					baseDao.execute("update make set ma_pricetype='按供应商取价' where ma_id=" + ma_id);
					// 记录操作
					//baseDao.logger.others("委外信息变更-按供应商取价", "msg.saveSuccess", "Make", "ma_id", ma_id);
				}
				/*String ma_prodcode = rs.getString("ma_prodcode");
				String ma_currency = rs.getString("ma_currency");
				//Object ppd_price = baseDao.getFieldDataByCondition("", field, condition)
				int ppd_price = baseDao.getJdbcTemplate().queryForObject("select nvl(wm_concat(ppd_price),0) from (select ppd_price from purchasepricedetail left join purchaseprice on ppd_ppid=pp_id where pp_kind='委外' and ppd_status='有效' and ppd_prodcode='"+ma_prodcode+"' and ppd_vendcode='"+vendcode+"' and ppd_currency='"+ma_currency+"' order by ppd_fromdate desc) where rownum=1", Integer.class);
				if(ppd_price>0){
					baseDao.updateByCondition("make", "ma_price='"+ppd_price+"'", "ma_id='"+rs.getInt("ma_id")+"'");
				}else{
					BaseUtil.showErrorOnSuccess("在物料核价单中未找到价格，委外单号："+rs.getString("ma_code"));
				}*/
			}
		}
		
	}
}
