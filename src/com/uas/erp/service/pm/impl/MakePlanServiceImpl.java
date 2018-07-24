package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakePlanService;


@Service
public class MakePlanServiceImpl implements MakePlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	

	@Override
	public int save(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore); //将form转换成Map格式的
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		String code = store.get("mp_code").toString();
		baseDao.asserts.nonExistCode("makeplan", "mp_code", code);
		
		int cn=0;int cn1=0;int cn2=0;int cn3=0;
		if(store.get("mp_wccode") != null && !"".equals(store.get("mp_wccode"))){//判断工作中心+计划类型+计划期间是否重复
			String wc=store.get("mp_wccode").toString();
			Object kind=store.get("mp_kind"),
				   period=store.get("mp_period"),
				   period1=store.get("mp_period1"),
				   period2=store.get("mp_period2");
			if(period != null && !"".equals(period)){
			 cn = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
					+ "and mp_period='"+period+"'");
			}else if(period1 != null && !"".equals(period1)){
				 cn1 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
						+ "and mp_period1='"+period1+"'");
			}else if(period2 != null && !"".equals(period2)){
				 cn2 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
						+ "and mp_period2='"+period2+"'");
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存form
		String formSql = SqlUtil.getInsertSqlByMap(store, "makeplan");
		baseDao.execute(formSql);
		// 保存detailgrid，保存明细表也就是从表
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "makeplandetail","mpd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "mp_id", store.get("mp_id"));//关于右上角的记录操作按钮
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		if(cn>0 || cn1>0 || cn2>0){
			BaseUtil.showErrorOnSuccess("主表工作中心+计划类型+计划期间存在重复记录");
		}
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(mpd_detno)detno,count(1)cn from makeplandetail where mpd_mpid=? group by mpd_wccode,mpd_datenum having count(1)>1",store.get("mp_id"));
		if(rs.next() && rs.getInt("cn")>0){
			BaseUtil.showErrorOnSuccess("在一份计划里面的从表工作中心+月份存在重复数据,不允许提交,序号：["+rs.getString("detno")+"]");
		}
		return 0;
	}

	@Override
	public void update(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("makeplan", "mp_statuscode", "mp_id=" + store.get("mp_id"));
		StateAssert.updateOnlyEntering(status);
		int cn=0;int cn1=0;int cn2=0;int cn3=0;
		if(store.get("mp_wccode") != null && !"".equals(store.get("mp_wccode"))){//判断工作中心+计划类型+计划期间是否重复
			String wc=store.get("mp_wccode").toString();
			Object kind=store.get("mp_kind"),
				   period=store.get("mp_period"),
				   period1=store.get("mp_period1"),
				   period2=store.get("mp_period2");
			if(period != null && !"".equals(period)){
			 cn = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
					+ "and mp_period='"+period+"'");
			}else if(period1 != null && !"".equals(period1)){
				 cn1 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
						+ "and mp_period1='"+period1+"'");
			}else if(period2 != null && !"".equals(period2)){
				 cn2 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+wc+"' and mp_kind='"+kind+"'"
						+ "and mp_period2='"+period2+"'");
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store ,grid});//因为要判定在它之前所对应的所以必须得写两个参数
		//更新主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "makeplan", "mp_id");
		baseDao.execute(formSql);
        //更新从表
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "makeplandetail","mpd_id");
		baseDao.execute(gridSql);
		//日志的记录作用
		baseDao.logger.update(caller, "mp_id", store.get("mp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store ,grid});
		if(cn>0 || cn1>0 || cn2>0){
			BaseUtil.showErrorOnSuccess("主表工作中心+计划类型+计划期间存在重复记录");
		}
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(mpd_detno)detno,count(1)cn from makeplandetail where mpd_mpid=? group by mpd_wccode,mpd_datenum having count(1)>1",store.get("mp_id"));
		if(rs.next() && rs.getInt("cn")>0){
			BaseUtil.showErrorOnSuccess("在一份计划里面的从表工作中心+月份存在重复数据,不允许提交,序号：["+rs.getString("detno")+"]");
		}
	}

	@Override
	public void delete(String caller, int id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("makeplan", "mp_statuscode", "mp_id=" + id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{id});
		//删除Dispatch
		baseDao.deleteById("makeplan", "mp_id", id);
		//删除DispatchDetail
		baseDao.deleteById("makeplandetail", "mpd_mpid", id);
		//记录操作
		baseDao.logger.delete(caller, "mp_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{id});
	}

	@Override
	public void audit(int id, String caller) {
		// 只能对状态为[已提交]的单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("MakePlan", "mp_statuscode", "mp_id=" +id);
				StateAssert.auditOnlyCommited(status);
				// 执行审核前的其它逻辑
				handlerService.beforeAudit(caller, id);
				/*// 带入审核时间
				baseDao.updateByCondition("MakePlan", "mp_auditdate="+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "mp_id=" +id);*/
				// 执行审核操作
				baseDao.audit("MakePlan", "mp_id=" + id, "mp_status", "mp_statuscode","mp_auditdate","mp_auditman");
				// 记录操作
				baseDao.logger.audit(caller, "mp_id", id);
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller, id);	
	}

	@Override
	public void resAudit(String caller, int id) {// 只能对状态为[已审核]的单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakePlan", "mp_statuscode", "mp_id=" +id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, id);
		// 反审核操作
		/*baseDao.resOperate("MakePlan", "mp_id=" + id, "mp_status", "mp_statuscode");
		baseDao.updateByCondition("MakePlan", "mp_statuscode='ENTERING'", "mp_id=" + id);*/
		baseDao.resAudit("MakePlan", "mp_id=" + id, "mp_status", "mp_statuscode", "mp_auditman", "mp_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "mp_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, id);}

	@Override
	public void submit(String caller, int id) {// 只能提交状态为[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("MakePlan", "mp_statuscode", "mp_id=" +id);
		StateAssert.submitOnlyEntering(status);	
		handlerService.beforeSubmit(caller, id);
		Object[] obj;
		int cn=0;int cn1=0;int cn2=0;
		obj = baseDao.getFieldsDataByCondition("makeplan", "mp_kind,mp_period1,mp_period,mp_period2,mp_wccode", "mp_id=" + id);
		if(obj!= null && obj[4]!= null && !"".equals("obj[4]")){
			if(obj[2] != null && !"".equals(obj[2])){
				 cn = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+obj[4]+"' and mp_kind='"+obj[0]+"'"
						    + " and mp_period='"+obj[2]+"' and mp_id<>"+id);
			}else if(obj[1] != null && !"".equals(obj[1])){
				 cn1 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+obj[4]+"' and mp_kind='"+obj[0]+"'"
						+ " and mp_period1='"+obj[1]+"' and mp_id<>"+id);
			}else if(obj[3] != null && !"".equals(obj[3])){
				 cn2 = baseDao.getCount("select count(1) from makeplan where mp_wccode='"+obj[4]+"' and mp_kind='"+obj[0]+"'"
						+ " and mp_period2='"+obj[3]+"' and mp_id<>"+id);
			}
		}
		if(cn>0 || cn1>0 || cn2>0){
			BaseUtil.showError("主表工作中心+计划类型+计划期间存在重复记录,不允许提交");
		}
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(mpd_detno)detno,count(1)cn from makeplandetail where mpd_mpid=? group by mpd_wccode,mpd_datenum having count(1)>1",id);
		if(rs.next() && rs.getInt("cn")>0){
			BaseUtil.showError("在一份计划里面的从表工作中心+月份存在重复数据,不允许提交,序号：["+rs.getString("detno")+"]");
		}
		// 执行提交操作
		baseDao.submit("MakePlan", "mp_id=" + id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mp_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);		
	}

	@Override
	public void resSubmit(String caller, int id) {// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakePlan", "mp_statuscode", "mp_id=" +id);		
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,id);
		// 执行反提交操作
		baseDao.resOperate("MakePlan", "mp_id=" + id, "mp_status", "mp_statuscode");	
		// 记录操作		
		baseDao.logger.resSubmit(caller,"mp_id", id);
		//提交后	
		handlerService.afterResSubmit(caller,id);		}	
}
