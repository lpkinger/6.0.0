package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.LendApplyService;

@Service
public class LendApplyServiceImpl implements LendApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveLendApply(String caller,String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "LendApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("ldd_id") == null || s.get("ldd_id").equals("") || s.get("ldd_id").equals("0")){//新添加的数据，id不存在
				int ldd_id = baseDao.getSeqId("LENDAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "LENDAPPLYDETAIL", new String[]{"ldd_id"}, new Object[]{ldd_id});
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		baseDao.logger.save(caller, "ld_id", store.get("ld_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateLendApplyById(String caller,String formStore,String param) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LendApply", "ld_id");
		baseDao.execute(formSql);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "LENDAPPLYDETAIL", "ldd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ldd_id") == null || s.get("ldd_id").equals("") || s.get("ldd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("LENDAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "LENDAPPLYDETAIL", new String[]{"ldd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ld_id", store.get("ld_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteLendApply(int ld_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ld_id});
		//删除主表内容
		baseDao.deleteById("LendApply", "ld_id", ld_id);
		baseDao.logger.delete(caller, "ld_id", ld_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ld_id});
	}

	@Override
	public void auditLendApply(int ld_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("LendApply", "ld_statuscode", "ld_id=" + ld_id);
		StateAssert.auditOnlyCommited(status);
		//判断输入数是否大于可借调数量
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("LendApplyDetail", new String[] { "ldd_obid", "ldd_detno","ldd_qty","ldd_property" }, "ldd_ldid="+ld_id);
		if (objs != null) {
			for (Object[] os : objs) {
				if("在途".equals(os[3])){
					SqlRowList rs = baseDao.queryForRowSet("select * from onorderbooking where ob_qty<"+os[2]+" and ob_id="+os[0]);
					if(rs.next()){
						BaseUtil.showError("借调数量大于可借调数量,行号为:"+os[1]);
					}
				}
				if("在库".equals(os[3])){
					SqlRowList rs = baseDao.queryForRowSet("select * from onhandbooking where ob_qty<"+os[2]+" and ob_id="+os[0]);
					if(rs.next()){
						BaseUtil.showError("借调数量大于可借调数量,行号为:"+os[1]);
					}
				}
			}
		}
		baseDao.procedure("USER_LENDAPPLY_AUDIT",new Object[] { ld_id });
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ld_id});
		//执行审核操作,待写
		baseDao.audit("LendApply", "ld_id=" + ld_id, "ld_status", "ld_statuscode", "ld_auditdate", "ld_auditor");
		//记录操作
		baseDao.logger.audit(caller, "ld_id", ld_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ld_id});
	}
	@Override
	public void submitLendApply(int ld_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("LendApply", "ld_statuscode", "ld_id=" + ld_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ld_id });
		// 执行提交操作
		baseDao.submit("LendApply", "ld_id=" + ld_id, "ld_status", "ld_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ld_id", ld_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ld_id });
	}
	@Override
	public void resSubmitLendApply(int ld_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("LendApply", "ld_statuscode", "ld_id=" + ld_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ld_id });
		// 执行反提交操作
		baseDao.resOperate("LendApply", "ld_id=" + ld_id, "ld_status", "ld_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ld_id", ld_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ld_id });
	}
	@Override
	public void resAuditLendApply(int ld_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("LendApply", "ld_statuscode", "ld_id=" + ld_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("LendApply", "ld_id=" + ld_id, "ld_status", "ld_statuscode", "ld_auditdate", "ld_auditor");
		baseDao.resOperate("LendApply", "ld_id=" + ld_id, "ld_status", "ld_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ld_id", ld_id);
	}
	@Override
	public String addLendApply(String formdata, String data ,String caller) {
		List<Map<Object, Object>>form = BaseUtil.parseGridStoreToMaps(formdata);
		List<Map<Object, Object>>grid = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb=new StringBuffer();
		Employee employee = SystemSession.getUser();
		String formsql = "";
		String gridsql = "";
		int ld_id = baseDao.getSeqId("LENDAPPLY_SEQ");
		Object ld_code = baseDao.sGetMaxNumber("LENDAPPLY", 2);
		Object ld_sacode = form.get(0).get("sa_code");
		Object ld_sadetno = form.get(0).get("ob_sadetno");
		Object ld_custcode = grid.get(0).get("sa_custcode");
		Object ld_custname = grid.get(0).get("sa_custname");
		Object ld_prcode = grid.get(0).get("pr_code");
		Object ldd_facode = grid.get(0).get("sa_code");
		if("".equals(grid.get(0).get("sa_code")) || grid.get(0).get("sa_code")==null){
			ldd_facode = grid.get(0).get("sf_code");
		}
		String sql = "Insert into lendapply (LD_ID,LD_CODE,LD_RECORD,LD_RECORDDATE,LD_AUDITOR,LD_AUDITDATE,LD_STATUS,LD_STATUSCODE,LD_LENDSTATUS,LD_SACODE,LD_SADETNO,LD_CUSTCODE,LD_CUSTNAME,LD_TEXT1,LD_TEXT2,LD_TEXT3,LD_PRCODE) values "
				+ "("+ld_id+",'"+ld_code+"','"+employee.getEm_name()+"',sysdate,null,null,'在录入','ENTERING',null,'"+ld_sacode+"',"+ld_sadetno+",'"+ld_custcode+"','"+ld_custname+"',null,null,null,'"+ld_prcode+"')";
		baseDao.execute(sql);
		int i = 1;
		for(Map<Object, Object>formmap : form){
			int ldd_id = baseDao.getSeqId("LENDAPPLYDETAIL_SEQ");
			formsql="Insert into LENDAPPLYDETAIL (LDD_ID,LDD_LDID,LDD_TYPE,LDD_PROPERTY,LDD_OBID,LDD_CODE,LDD_SADETNO,LDD_QTY,LDD_DETNO,LDD_FACODE,LDD_SELLER,LDD_CUSTNAME,LDD_WHNAME) values "
					+ "("+ldd_id+","+ld_id+",'借方','"+formmap.get("type")+"',"+formmap.get("ob_id")+",'"+formmap.get("pu_code")+"',"+formmap.get("pu_detno")+","+formmap.get("tqty")+","+i+",null,null,null,null)";
			baseDao.execute(formsql);
			i++;
		}
		for(Map<Object, Object>gridmap : grid){
			int ldd_id = baseDao.getSeqId("LENDAPPLYDETAIL_SEQ");
			gridsql = "Insert into LENDAPPLYDETAIL (LDD_ID,LDD_LDID,LDD_TYPE,LDD_PROPERTY,LDD_OBID,LDD_CODE,LDD_SADETNO,LDD_QTY,LDD_DETNO,LDD_FACODE,LDD_SELLER,LDD_CUSTNAME,LDD_WHNAME) values "
					+ "("+ldd_id+","+ld_id+",'被借方','"+gridmap.get("type")+"',"+gridmap.get("ob_id")+",'"+gridmap.get("pu_code")+"',"+gridmap.get("pu_detno")+","+gridmap.get("ob_tqty")+","+i+",'"+ldd_facode+"','"+gridmap.get("sa_seller")+"','"+gridmap.get("sa_custname")+"','"+gridmap.get("sd_whname")+"')";
			baseDao.execute(gridsql);
			i++;
		}
	    sb.append("借调申请成功，借调申请单号:<a href=\"javascript:openUrl('jsps/scm/sale/lendapply.jsp?formCondition=ld_idIS"+ld_id+"&gridCondition=ldd_ldidIS"+ld_id+""
				+ "&whoami=LendApply')\">"+ld_code+ "</a>&nbsp;");
		return sb.toString();
	}
}
