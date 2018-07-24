package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.CustTurnService;

@Service
public class CustTurnServiceImpl implements CustTurnService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;	

	@Override
	public void saveCustTurn(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存detail
		for (Map<Object, Object> s : grid) {
			s.put("cd_id", baseDao.getSeqId("CustTurnDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CustTurnDetail");
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(cd_custcode) from  CustTurndetail where cd_ctid=" + store.get("ct_id")
						+ "  group  by  cd_custcode  having  count(cd_custcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行客户编号重复");
		}
		// 保存CustTurn
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustTurn",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "bda_id", store.get("bda_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	@Override
	public void deleteCustTurn(int ct_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ct_id });
		// 删除CustTurn
		baseDao.deleteById("CustTurn", "ct_id", ct_id);
		// 删除detail
		baseDao.deleteById("CustTurndetail", "cd_ctid", ct_id);
		// 记录操作
		baseDao.logger.delete(caller, "ct_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ct_id);

	}
	@Override
	public void updateCustTurn(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + store.get("ct_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		//update detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "CustTurndetail", "cd_id");
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(cd_custcode) from  CustTurndetail where cd_ctid=" + store.get("ct_id")
						+ "  group  by  cd_custcode  having  count(cd_custcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行客户编号重复");
		}
		// 修改CustTurn
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustTurn",
				"ct_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ct_id", store.get("ct_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}
	@Override
	public void submitCustTurn(int ct_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		//提交时判断：如果类型=移交或复制时，如果新业务员为空，不允许提交
		Object[] value=baseDao.getFieldsDataByCondition("CustTurn", "ct_newsellercode,ct_issave", "ct_id="+ct_id);
		if("移交".equals(value[1])||"复制".equals(value[1]) || "客服代表移交".equals(value[1])){
			if(value[0]==null||"".equals(value[0])){
				BaseUtil.showError("当类型为移交、复制时必须选择新业务员！");
			}	
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ct_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CustTurn",
				"ct_statuscode='COMMITED',ct_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"ct_id=" + ct_id);
		// 记录操作
		baseDao.logger.submit(caller, "ct_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ct_id);

	}
	@Override
	public void resSubmitCustTurn(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ct_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CustTurn",
				"ct_statuscode='ENTERING',ct_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"ct_id=" + ct_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ct_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ct_id);

	}
	@Override
	public void auditCustTurn(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ct_id);
		Object gxtype = baseDao.getFieldDataByCondition("CustTurn", "ct_issave", "ct_id=" + ct_id);	
		Object[] datas = baseDao.getFieldsDataByCondition("CustTurn", "ct_newsellercode,ct_newsellername,ct_ysellercode,ct_ysellername", "ct_id=" + ct_id);
		List<Object[]> data = baseDao.getFieldsDatasByCondition("CustTurndetail  left join CustTurn on cd_ctid=ct_id",
				new String[] { "cd_detno", "cd_custcode", "cd_custname","cd_id"}, "cd_ctid=" + ct_id);
		if(gxtype.equals("复制")){
			for(Object[] os:data){
				boolean isExist1 = baseDao.checkIf("CustomerDistr  left join customer a on cd_cuid=cu_id", "cu_code='"+os[1]+"' and '"+datas[0]+"' in (select cd_sellercode from CustomerDistr  left join customer b on cd_cuid=cu_id where a.cu_code=b.cu_code)");
				if(!isExist1){
					String sql="select max(cd_detno) from CustomerDistr where cd_cuid=(select cu_id from Customer where cu_code=?)";
					Object deptno=baseDao.queryForObject(sql, String.class, os[1]);
					String  strsql="insert into customerdistr (cd_id,cd_cuid,cd_detno,cd_sellercode,cd_seller,cd_custcode) select customerdistr_seq.nextval,cu_id,"+(Integer.parseInt(deptno.toString())+1)+",ct_newsellercode,ct_newsellername,cu_code from custturn left join custturndetail a on ct_id=a.cd_ctid left join customer on a.cd_custcode=cu_code where ct_id="+ct_id+" and a.cd_id='"+os[3]+"'";
					baseDao.execute(strsql);
				}
			}
		}else if(gxtype.equals("移交")){
			for(Object[] os : data){
				boolean isExist1 = baseDao.checkIf("CustomerDistr  left join customer a on cd_cuid=cu_id", "cu_code='"+os[1]+"' and '"+datas[0]+"' in (select cd_sellercode from CustomerDistr  left join customer b on cd_cuid=cu_id where a.cu_code=b.cu_code)");
				if(!isExist1){
					String sql2= "update customerdistr set cd_sellercode='" + datas[0] + "',cd_seller='" + datas[1] + "' where cd_sellercode='" +datas[2]+ "' and cd_custcode='"+os[1] +"'";
					baseDao.execute(sql2);
				}
	            String sql3="delete from customerdistr where cd_sellercode='"+datas[2]+"' and nvl(cd_custcode, ' ') in (select nvl(cd_custcode, ' ') from customerdistr left join customer on cd_cuid = cu_id where cu_code='"+os[1]+"')";
	            baseDao.execute(sql3);
	            boolean isExist = baseDao.checkIf("CustomerDistr left join customer a on cd_cuid=cu_id", "cu_code='"+os[1]+"' and '"+datas[2]+"'  in (select cd_sellercode from CustomerDistr  left join customer b on cd_cuid=cu_id where a.cu_code=b.cu_code)");
				if(!isExist){
					String sql1="update customer set cu_sellercode='" + datas[0] + "',cu_sellername='" + datas[1] + "' where cu_code='"+os[1]+"' and cu_sellercode='"+datas[2]+"'";
					baseDao.execute(sql1);
					String updatesql1="update customer set cu_sellerid=(select em_id from employee where em_code=customer.cu_sellercode) where cu_sellercode='"+ datas[0] +"'";
					baseDao.execute(updatesql1);
				}
			}
		}else if("删除".equals(gxtype)){
			for(Object[] da:data){
				Object cuid=baseDao.getFieldDataByCondition("Customer", "cu_id", "cu_code='"+da[1]+"'");
				String sql="delete from customerdistr where cd_sellercode='"+datas[2]+"' and cd_cuid='"+cuid+"'";
				baseDao.execute(sql);
			}
		}else if ("客服代表移交".equals(gxtype)) {
			// maz 客户移交类型增加“客服代表移交” 审核后只修改客服代表 2017070391
			for(Object[] os : data){
	            boolean isExist = baseDao.checkIf("customer", "cu_code='"+os[1]+"' and cu_servicecode='"+datas[2]+"'");
				if(isExist){
					boolean isExist1 = baseDao.checkIf("customer", "cu_code='"+os[1]+"' and cu_servicecode='"+datas[2]+"' and cu_servicecode=cu_sellercode");
					String sql1="update customer set cu_servicecode='" + datas[0] + "',cu_servicename='" + datas[1] + "' where cu_code='"+os[1]+"' and cu_servicecode='"+datas[2]+"'";
					baseDao.execute(sql1);
					if(isExist1){
						boolean isExist2 = baseDao.checkIf("CustomerDistr  left join customer a on cd_cuid=cu_id", "cu_code='"+os[1]+"' and '"+datas[0]+"' in (select cd_sellercode from CustomerDistr  left join customer b on cd_cuid=cu_id where a.cu_code=b.cu_code)");
						if(!isExist2){
							Object[] cd = baseDao.getFieldsDataByCondition("customerdistr", new String[]{"max(cd_detno)","cd_cuid"}, "cd_custcode='"+os[1]+"' group by cd_cuid");
							baseDao.execute("Insert into customerdistr (CD_ID,CD_CUID,CD_DETNO,CD_SELLERCODE,CD_SELLER,CD_CUSTCODE) values (customerdistr_seq.nextval,"+cd[1]+","+(Integer.parseInt(cd[0].toString())+1)+",'"+datas[0]+"','"+datas[1]+"','"+os[1]+"')");
						}
					}else{
						boolean isExist3 = baseDao.checkIf("CustomerDistr  left join customer a on cd_cuid=cu_id", "cu_code='"+os[1]+"' and '"+datas[0]+"' in (select cd_sellercode from CustomerDistr  left join customer b on cd_cuid=cu_id where a.cu_code=b.cu_code)");
						if(isExist3){
							String sql3="delete from customerdistr where cd_sellercode='"+datas[2]+"' and nvl(cd_custcode, ' ') in (select nvl(cd_custcode, ' ') from customerdistr left join customer on cd_cuid = cu_id where cu_code='"+os[1]+"')";
							baseDao.execute(sql3);
						}else{
							String sql3="update  customerdistr set cd_sellercode='"+datas[0]+"',cd_seller='"+datas[1]+"' where cd_sellercode='"+datas[2]+"' and nvl(cd_custcode, ' ') in (select nvl(cd_custcode, ' ') from customerdistr left join customer on cd_cuid = cu_id where cu_code='"+os[1]+"')";
							baseDao.execute(sql3);
						}
					}
				}
			}
		}
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"CustTurn",
				"ct_statuscode='AUDITED',ct_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ct_auditer='" + employee.getEm_name()
						+ "',ct_auditdate=sysdate", "ct_id=" + ct_id);
		// 记录操作
		baseDao.logger.audit(caller, "ct_id", ct_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ct_id);

	}
	@Override
	public void resAuditCustTurn(int ct_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ct_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustTurn",
				"ct_statuscode", "ct_id=" + ct_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"CustTurn",
				"ct_statuscode='ENTERING',ct_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ct_auditer='',ct_auditdate=null", "ct_id=" + ct_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ct_id", ct_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ct_id);

	}
}
