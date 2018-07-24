package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.KpidesignService;;
@Service
public class KpidesignServiceImpl implements KpidesignService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveKpidesign(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from Kpidesign where kd_theme='"+store.get("kd_theme")+"'");
		if(count!=0){
			BaseUtil.showError("此主题已存在！");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store});
		if("manual".equals(store.get("kd_starttype"))){
			store.put("kd_startkind", "manual");
		}
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,"Kpidesign", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kd_id", store.get("kd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store});
	}
	@Override
	public void updateKpidesign(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);	
		int count=baseDao.getCount("select count(1) from Kpidesign where kd_id<> "+store.get("kd_id")+" and kd_theme='"+store.get("kd_theme")+"'");
		if(count!=0){
			BaseUtil.showError("此主题已存在！");
		}
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		if("manual".equals(store.get("kd_starttype"))){
			store.put("kd_startkind", "manual");
		}
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Kpidesign", "kd_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "kd_id", store.get("kd_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteKpidesign(int kd_id, String caller) {
			//执行删除前的其它逻辑
			handlerService.handler(caller, "delete", "before", new Object[]{kd_id});
			//删除主表
			baseDao.deleteById("Kpidesign", "kd_id", kd_id);
			//删除考核项目
			baseDao.deleteById("KpidesignItem", "ki_kdid", kd_id);
			//删除评分设计
			String sql="DELETE from KPIDESIGNPOINT_ITEM where kpi_kpid in (select kp_id FROM KPIDESIGNPOINT where kp_kdid="+kd_id+")";
			baseDao.execute(sql);
			baseDao.deleteById("Kpidesignpoint", "kp_kdid", kd_id);
			
			//删除评分等级
			baseDao.deleteById("KpidesigngradeLevel", "kl_kdid", kd_id);
			//记录操作
			baseDao.logger.delete(caller, "kd_id", kd_id);
			//执行删除后的其它逻辑
			handlerService.handler(caller, "delete", "after", new Object[]{kd_id});	
	}
	@Override
	public int saveDetail(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		int detno=0;
		String d="";
		if("KpidesignItem".equals(caller)){
			int count=baseDao.getCount("select count(1) from KpidesignItem where ki_krcode='"+store.get("ki_krcode")+"' and ki_kdid="+store.get("ki_kdid"));
			if(count!=0){
				BaseUtil.showError("此规则已存在！");
			}
			String sql="select sum(ki_score_to) from KpidesignItem where ki_kdid="+store.get("ki_kdid");
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				double score= rs.getGeneralDouble(1, 2);
				if(score+Double.parseDouble(store.get("ki_score_to").toString())>100){
					BaseUtil.showError("考核项目最高分之和不能大于100");
				}
			}
			d="ki_detno";
			detno = Integer.parseInt(baseDao.getFieldDataByCondition("KpidesignItem", "nvl(max(ki_detno),0)", "ki_kdid=" + store.get("ki_kdid")).toString())+1;
		}
		if("Kpidesignpoint_F".equals(caller)){
			if("".equals(store.get("kp_manid"))){
				BaseUtil.showError("请选择评分人");
			}
			int count=baseDao.getCount("select count(1) from Kpidesignpoint where kp_kpcode='"+store.get("kp_kpcode")+"' and kp_kdid="+store.get("kp_kdid"));
			if(count!=0){
				BaseUtil.showError("此评分类型已存在！");
			}
			String sql="select sum(kp_percent) from Kpidesignpoint where kp_kdid="+store.get("kp_kdid");
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				double percent= rs.getGeneralDouble(1, 2);
				if(percent+Double.parseDouble(store.get("kp_percent").toString())>100){
					BaseUtil.showError("权重之和不能大于100%");
				}
			}
			d="kp_detno";
			detno = Integer.parseInt(baseDao.getFieldDataByCondition("Kpidesignpoint", "nvl(max(kp_detno),0)", "kp_kdid=" + store.get("kp_kdid")).toString())+1;
		}
		if("KpidesigngradeLevel_F".equals(caller)){
			int count=baseDao.getCount("select count(1) from KpidesigngradeLevel where kl_name='"+store.get("kl_name")+"' and kl_kdid="+store.get("kl_kdid"));
			if(count!=0){
				BaseUtil.showError("此等级名称已存在！");
			}
			//检查区间是否重叠
			String dets = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(kl_name) from KPIDESIGNGRADELEVEL where KL_KDID=? and ((?>KL_SCORE_FROM and ?<=KL_SCORE_TO) or (?<=kl_score_from and ?>kl_score_to))",
							String.class, store.get("kl_kdid"),store.get("kl_score_to"),store.get("kl_score_to"),store.get("kl_score_from"),store.get("kl_score_to"));
			if (dets != null) {
				BaseUtil.showError("分数区间设置与" + dets+"重复，请重新设置。");
			}
			d="kl_detno";
			detno = Integer.parseInt(baseDao.getFieldDataByCondition("KpidesigngradeLevel", "nvl(max(kl_detno),0)", "kl_kdid=" + store.get("kl_kdid")).toString())+1;
		}
		// 保存form
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_detailmainkeyfield" },
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		int id = -1;
		if (objs != null) {
			String tab = (String) objs[0];
			if (tab != null) {
				if (tab.contains(" ")) {
					tab = tab.substring(0, tab.indexOf(" "));
				}
				if (store.get(objs[1]) == null || store.get(objs[1]).equals("") || Integer.parseInt(store.get(objs[1]).toString()) == 0) {
					id = baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ");
					store.put(objs[1], id);
				} else {
					id = Integer.parseInt(store.get(objs[1]).toString());
				}
				store.put(d, detno);
				String formSql = SqlUtil.getInsertSqlByFormStore(store, (String) tab, new String[] {}, new Object[] {});
				baseDao.execute(formSql);
			}
		}
		// 保存detailgrid
		if (gridStore != null  && gridStore.length() > 2) {
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				for (Map<Object, Object> map : grid) {
					map.put("kpi_id", baseDao.getSeqId("KPIDESIGNPOINT_ITEM_SEQ"));
					if (id != -1) {
						map.put("kpi_kpid", id);
					}
					map.put("kpi_kiid",map.get("ki_id"));
					map.remove("ki_id");
				}
				List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,"KPIDESIGNPOINT_ITEM");
				baseDao.execute(gridSql);
				String sql="update Kpidesignpoint set kp_kidetno=(select wmsys.wm_concat(ki_detno) from (select ki_detno from KpidesignItem left join "
						+ "KPIDESIGNPOINT_ITEM on kpi_kiid=ki_id where kpi_kpid=" +id + " order by ki_detno)) where kp_id="+id;
				baseDao.execute(sql);
			}
		// 记录操作
		try {
			if (objs != null) {
				baseDao.logger.save(caller, objs[1].toString() , store.get(objs[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		return id;
	
	}
	@Override
	public void updateDetail(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if("KpidesignItem".equals(caller)){
			int count=baseDao.getCount("select count(1) from KpidesignItem where ki_id<> "+store.get("ki_id")+" and ki_krcode='"+store.get("ki_krcode")+"' and ki_kdid="+store.get("ki_kdid"));
			if(count!=0){
				BaseUtil.showError("此考核规则已存在！");
			}
			String sql="select sum(ki_score_to) from KpidesignItem where ki_kdid="+store.get("ki_kdid")+" and ki_id<>"+store.get("ki_id");
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				double score= rs.getGeneralDouble(1, 2);
				if(score+Double.parseDouble(store.get("ki_score_to").toString())>100){
					BaseUtil.showError("考核项目最高分之和不能大于100");
				}
			}
		}
		if("Kpidesignpoint_F".equals(caller)){
			int count=baseDao.getCount("select count(1) from Kpidesignpoint where kp_id<> "+store.get("kp_id")+" and kp_kpcode='"+store.get("kp_kpcode")+"' and kp_kdid="+store.get("kp_kdid"));
			if(count!=0){
				BaseUtil.showError("此评分类型已存在！");
			}
			String sql="select sum(kp_percent) from Kpidesignpoint where kp_kdid="+store.get("kp_kdid")+" and kp_id<>"+store.get("kp_id");
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				double percent= rs.getGeneralDouble(1, 2);
				if(percent+Double.parseDouble(store.get("kp_percent").toString())>100){
					BaseUtil.showError("权重之和不能大于100%");
				}
			}
		}
		if("KpidesigngradeLevel_F".equals(caller)){
			if(Double.parseDouble(store.get("kl_score_to").toString())<=Double.parseDouble(store.get("kl_score_from").toString())){
				BaseUtil.showError("最高分应大于最低分！");
			}
			int count=baseDao.getCount("select count(1) from KpidesigngradeLevel where kl_id<> "+store.get("kl_id")+" and kl_name='"+store.get("kl_name")+"' and kl_kdid="+store.get("kl_kdid"));
			if(count!=0){
				BaseUtil.showError("此等级名称已存在！");
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,  new Object[] { store });
		// 修改form
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statuscodefield" },
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			String tab = (String) objs[0];
			String keyF = (String) objs[1];
			String sF = (String) objs[2];
			if (tab != null && keyF != null) {
				if (tab.contains(" ")) {
					tab = tab.substring(0, tab.indexOf(" "));
				}
				if (sF != null) {
					Object status = baseDao.getFieldDataByCondition(tab, sF, keyF + "=" + store.get(keyF));
					StateAssert.updateOnlyEntering(status);		
				}
				String formSql = SqlUtil.getUpdateSqlByFormStore(store, tab, keyF);
				baseDao.execute(formSql);
			}
		}
		// 修改Grid
		if (gridStore != null  && gridStore.length() > 2) {
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
			StringBuffer ids=new StringBuffer();
			for ( int i=grid.size()-1;i>=0;i--) {
				Map<Object, Object> map = grid.get(i);
				int count=baseDao.getCount("select count(1) from KPIDESIGNPOINT_ITEM where kpi_kiid="+map.get("ki_id")+" and kpi_kpid="+store.get("kp_id"));	
				ids.append(map.get("ki_id")+",");
				if(count!=0){
					grid.remove(i);
				}else{
					map.put("kpi_id", baseDao.getSeqId("KPIDESIGNPOINT_ITEM_SEQ"));
					map.put("kpi_kpid", store.get("kp_id"));
					map.put("kpi_kiid",map.get("ki_id"));
					map.remove("ki_id");
				}				
			}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,"KPIDESIGNPOINT_ITEM");
			baseDao.execute(gridSql);
			if(ids.length()>0){
				String con=ids.toString().substring(0, ids.length()-1);
				String delSql="delete from KPIDESIGNPOINT_ITEM WHERE kpi_kpid="+store.get("kp_id")+" and KPI_KIID not IN ("+con+")";
				baseDao.execute(delSql);
			}
			String sql="update Kpidesignpoint set kp_kidetno=(select wmsys.wm_concat(ki_detno) from (select ki_detno from KpidesignItem left join "
					+ "KPIDESIGNPOINT_ITEM on kpi_kiid=ki_id where kpi_kpid=" +store.get("kp_id") + " order by ki_detno)) where kp_id="+store.get("kp_id");
			baseDao.execute(sql);
		}
		// 记录操作
		try {
			baseDao.logger.update(caller, objs[1].toString(), store.get(objs[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[] { store });
	
		
	}
	@Override
	public void submitKpidesign(int kd_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Kpidesign","kd_statuscode", "kd_id=" + kd_id);
		String sql="select sum(ki_score_to) from KpidesignItem where ki_kdid="+kd_id+" and ki_id<>"+kd_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			double score= rs.getGeneralDouble(1, 2);
			if(score!=100){
				BaseUtil.showError("考核项目最高分之和应为100分");
			}
		}else{
			BaseUtil.showError("请添加考核项目");
		}
		String sql1="select sum(kp_percent) from Kpidesignpoint where kp_kdid="+kd_id+" and kp_id<>"+kd_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql1);
		if (rs1.next()) {
			double percent= rs.getGeneralDouble(1, 2);
			if(percent!=100){
				BaseUtil.showError("权重之和应为100%");
			}
		}else{
			BaseUtil.showError("请添加评分设计");
		}
		String sql2="select KL_SCORE_FROM,KL_SCORE_TO from KPIDESIGNGRADELEVEL where kl_kdid="+kd_id+" ORDER BY KL_SCORE_FROM";
		SqlRowList rs2 = baseDao.queryForRowSet(sql2);
		double d=0;
		if(rs2.hasNext()){
			while(rs2.next()) {
				double s=rs2.getDouble(1);
				if(d!=s){
					BaseUtil.showError("评分等级中"+d+"分~"+s+"分等级未设置");
				}else{
					d=rs2.getDouble(2);
				}
			}
			if(d!=100){
				BaseUtil.showError("评分等级中"+d+"分~100分等级未设置");
			}
		}else{
			BaseUtil.showError("请添加评分等级");
		}
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { kd_id});
		// 执行提交操作
		baseDao.submit("Kpidesign", "kd_id=" + kd_id, "kd_status", "kd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "kd_id", kd_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { kd_id});
		
	}
	@Override
	public void resSubmitKpidesign(int kd_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Kpidesign","kd_statuscode", "kd_id=" + kd_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[] { kd_id});
		// 执行反提交操作
		baseDao.resOperate("Kpidesign", "kd_id=" + kd_id, "kd_status", "kd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "kd_id", kd_id);
		handlerService.afterResSubmit(caller,new Object[] { kd_id});
		
	}
	@Override
	public void auditKpidesign(int kd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("Kpidesign","kd_statuscode", "kd_id=" + kd_id);
				StateAssert.auditOnlyCommited(status);
				// 执行审核前的其它逻辑
				handlerService.beforeAudit(caller, new Object[] { kd_id});
				// 执行审核操作
				baseDao.audit("Kpidesign", "kd_id=" + kd_id, "kd_status", "kd_statuscode");
				// 记录操作
				baseDao.logger.audit(caller, "kd_id", kd_id);
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller, new Object[] { kd_id});
		
	}
	@Override
	public void resAuditKpidesign(int kd_id, String caller) {
				// 执行反审核前的其它逻辑
				handlerService.beforeResAudit(caller, new Object[] { kd_id});
				// 只能对状态为[已审核]的订单进行反审核操作!
				Object status = baseDao.getFieldDataByCondition("Kpidesign","kd_statuscode", "kd_id=" + kd_id);
				StateAssert.resAuditOnlyAudit(status);
				Object using = baseDao.getFieldDataByCondition("Kpidesign","kd_using", "kd_id=" + kd_id);
				System.out.println(using);
				if(!"0".equals(using.toString())){
					BaseUtil.showError("此考核项目正在使用，不能反审核");
				}
				// 执行反审核操作
				baseDao.resOperate("Kpidesign", "kd_id=" + kd_id, "kd_status", "kd_statuscode");
				// 记录操作
				baseDao.logger.resAudit(caller, "kd_id", kd_id);
				// 执行反审核后的其它逻辑
				handlerService.afterResAudit(caller, new Object[] { kd_id});
		
	}
	@Override
	public void deleteDetail(String caller, int id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] { id });
		if("KpidesignItem".equals(caller)){
			int count=baseDao.getCount("select count(1) from KPIDESIGNPOINT_ITEM where kpi_kiid="+id);
			if(count!=0){
				BaseUtil.showError("此考核项目在评分设计中被使用，不能删除");
			}
		}
		// 删除form
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_detailmainkeyfield" },
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			baseDao.deleteById(objs[0].toString().split(" ")[0], (String) objs[1], id);
		}		
		if("Kpidesignpoint_F".equals(caller)){
			baseDao.deleteByCondition("KPIDESIGNPOINT_ITEM", "kpi_kpid="+id);
		}
		// 记录操作
		try {
			baseDao.logger.delete(caller, objs[1].toString(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
		
	}
	@Override
	public String kpidesignLaunch(String caller, String data,String time_from,String time_to,String period) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "kd_id");
		if(ids!=null){
			ids=ids.replaceAll("\\'", "");
			baseDao.callProcedure("KPI.launch_kpi", new Object[] {ids,time_from,time_to,period});
		}		
		return "处理成功";
	}
	@Override
	public String kpidSummary(String caller, String data) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "kdb_id");
		if(ids!=null){
			ids=ids.replaceAll("\\'", "");
			baseDao.callProcedure("KPI.sum_kpi", new Object[] {ids});
		}		
		
		return "处理成功";
	}
}
