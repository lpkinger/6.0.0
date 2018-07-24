package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ExamSchemeService;

@Service
public class ExamSchemeServiceImpl implements ExamSchemeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveExamScheme(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ExamScheme
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ExamScheme",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存明细
		for (Map<Object, Object> s : grid) {
			s.put("esd_id", baseDao.getSeqId("ExamSchemedet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"ExamSchemedet");
		baseDao.execute(gridSql);
		baseDao.execute("update ExamScheme set es_score=(select sum(esd_number*esd_score) from ExamSchemedet where esd_esid="+store.get("es_id")+") where es_id = "+store.get("es_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "es_id", store.get("es_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void deleteExamScheme(int es_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + es_id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("ExamScheme", es_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { es_id });
		// 删除ExamScheme
		baseDao.deleteById("ExamScheme", "es_id", es_id);
		// 删除明细
		baseDao.deleteById("ExamSchemedet", "esd_esid", es_id);
		// 记录操作
		baseDao.logger.delete(caller, "es_id", es_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { es_id });

	}

	@Override
	public void updateExamSchemeById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + store.get("es_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ExamScheme
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ExamScheme",
				"es_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ExamSchemedet", "esd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("esd_id") == null || s.get("esd_id").equals("")
					|| s.get("esd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ExamSchemedet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ExamSchemedet",
						new String[] { "esd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update ExamScheme set es_score=(select sum(esd_number*esd_score) from ExamSchemedet where esd_esid="+store.get("es_id")+") where es_id = "+store.get("es_id"));
		// 记录操作
		baseDao.logger.update(caller, "es_id", store.get("es_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void submitExamScheme(int es_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + es_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(esd_eqcode) from (select esd_eqcode,esd_esid from ExamSchemedet where esd_esid=? and nvl(esd_eqcode,' ')<>' ' group by esd_eqcode,esd_esid having count(*) >1)",
						String.class, es_id);
		if (dets != null) {
			BaseUtil.showError("指定题目重复！题目编号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(distinct esd_eqtype) from (select esd_eqtype from ExamSchemedet where esd_esid=? and nvl(esd_eqtype,' ')<>' ' and esd_eqtype not in (select distinct eq_type from ExamQuest where nvl(eq_status,' ') ='已审核'))",
						String.class, es_id);
		if (dets != null) {
			BaseUtil.showError("指定题目类型在题目库中不存在！题目类型：" + dets);
		}
		Object es_score=baseDao.getFieldDataByCondition("ExamScheme", "es_score", "es_id=" + es_id);
		List<Object[]> data=baseDao.getFieldsDatasByCondition("ExamSchemedet", new String[]{"nvl(esd_number,0)",
				"esd_score","ESD_EQBELONG","ESD_EQTYPE","ESD_EQLEVEL","esd_eqcode","ESD_DETNO"}, "esd_esid="+es_id);
		double score=0;
		StringBuffer errBuffer = new StringBuffer();
		for(Object[] d : data){
			if(d[0].toString().equals("0")){//数量为0,默认为1
				score+=Double.parseDouble(d[1].toString());
			}else{
				score+=Double.parseDouble(d[0].toString())*Double.parseDouble(d[1].toString());
			}
			if(d[3]!=null&&d[4]!=null){
				int count = baseDao.getCount("select count(*) from examquest where nvl(eq_status,' ') ='已审核' and  eq_type='"+d[3]+"' and eq_level='"+d[4]+"'");
				if(count < Integer.parseInt(d[0].toString())){
					errBuffer.append("第"+d[6]+"行题库里面题目不够,请核对后重试！").append("<hr>");
				}
			}else if(d[3]!=null){
				int count = baseDao.getCount("select count(*) from examquest where nvl(eq_status,' ') ='已审核' and eq_type='"+d[3]+"'");
				if(count < Integer.parseInt(d[0].toString())){
					errBuffer.append("第"+d[6]+"行题库里面题目不够,请核对后重试！").append("<hr>");
				}
			}else if(d[4]!=null){
				int count = baseDao.getCount("select count(*) from examquest where  nvl(eq_status,' ') ='已审核' and eq_level='"+d[4]+"'");
				if(count < Integer.parseInt(d[0].toString())){
					errBuffer.append("第"+d[6]+"行题库里面题目不够,请核对后重试！").append("<hr>");
				}
			}else{
				int count = baseDao.getCount("select count(*) from examquest where nvl(eq_status,' ') ='已审核'");
				if(count < Integer.parseInt(d[0].toString())){
					errBuffer.append("第"+d[6]+"行题库里面题目不够,请核对后重试！").append("<hr>");
				}
			}
			
		}
		if (errBuffer.length() > 0){
			BaseUtil.showError(errBuffer.toString());
		}
		if(Double.parseDouble(es_score.toString())!=score){
			BaseUtil.showError("题目分值和总分不相等!");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller, new Object[] { es_id });
		// 执行提交操作
		baseDao.submit("ExamScheme", "es_id=" + es_id, "es_status",
				"es_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "es_id", es_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, new Object[] { es_id });

	}

	@Override
	public void resSubmitExamScheme(int es_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + es_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("ExamScheme", "es_id=" + es_id, "es_status",
				"es_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "es_id", es_id);

	}

	@Override
	public void auditExamScheme(int es_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + es_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { es_id });
		// 执行审核操作
		baseDao.audit("ExamScheme", "es_id=" + es_id, "es_status",
				"es_statuscode", "es_auditdate", "es_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "es_id", es_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { es_id });

	}

	@Override
	public void resAuditExamScheme(int es_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ExamScheme",
				"es_statuscode", "es_id=" + es_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ExamScheme", "es_id=" + es_id, "es_status",
				"es_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "es_id", es_id);

	}

}
