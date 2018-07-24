package com.uas.erp.service.common.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.CommonService;

/**
 * 标准界面的基本逻辑
 */
@Service
public class CommonSeriveImpl implements CommonService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Override
	public int saveCommon(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 补卡申请保存限制
		if ("MobileSignCard".equals(caller)) {
			Employee employee = SystemSession.getUser();
			String ms_emcode = employee.getEm_code();
			String tablename = "mobile_signcard";
			String condition = "ms_emcode='" + ms_emcode + "' and ms_statuscode !='AUDITED'";
//			if (baseDao.checkIf(tablename, condition) == true) {
//				BaseUtil.showError("您的补卡申请正在审核中，请勿重复提交！");
//			}
		}
		//客户拜访计划限制
		if("visitplan".equals(caller)){
			String start=store.get("vp_startdate").toString();
			String end=store.get("vp_enddate").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			try {
				if(sdf.parse(start).getTime()-sdf.parse(end).getTime()>=0){
					BaseUtil.showError("会议时间输入有误，截止时间不能早于开始时间！");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 采购类型增加限制
		if ("PurchaseKind".equals(caller) && store.get("pk_ifmrpkind") != null
				&& Integer.parseInt(store.get("pk_ifmrpkind").toString()) != 0) {
			SqlRowList rs = baseDao.queryForRowSet("select pk_code from PurchaseKind where pk_ifmrpkind=-1 and rownum=1");
			if (rs.next()) {
				BaseUtil.showError("只能有一个采购类型是MRP默认类型,类型编号:" + rs.getString("pk_code"));
			}
		}
		//项目结案申请限制
		if("ProjectClose".equals(caller)) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(prj_code) from Project where prj_statuscode = 'FINISHED' and prj_code = ?", String.class,store.get("pc_prjcode"));
			if (dets != null) {
				BaseUtil.showError("当前项目已结案，无需再次结案！");
			}
			
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(pc_prjcode) from ProjectClose where pc_prjcode = ?", String.class,store.get("pc_prjcode"));
			if (dets != null) {
				BaseUtil.showError("当前项目已进行项目结案申请，不允许重复申请！");
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
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
				String formSql = SqlUtil.getInsertSqlByFormStore(store, (String) tab, new String[] {}, new Object[] {});
				baseDao.execute(formSql);
			}
		}
		// 保存detailgrid
		if (gridStore != null && gridStore.length() > 2) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : objects[0];
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				for (Map<Object, Object> map : grid) {
					map.put(objects[1], baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ"));
					if (id != -1) {
						map.put(objs[2], id);
					}
				}
				List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, (String) tab);
				baseDao.execute(gridSql);
			}
		}
		// 记录操作
		try {
			if (objs != null) {
				baseDao.logger.save(caller, objs[1].toString(), store.get(objs[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		return id;
	}

	@Override
	public void updateCommonById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 采购类型增加限制
		if ("PurchaseKind".equals(caller) && store.get("pk_ifmrpkind") != null
				&& Integer.parseInt(store.get("pk_ifmrpkind").toString()) != 0) {
			SqlRowList rs = baseDao.queryForRowSet("select pk_code from PurchaseKind where pk_ifmrpkind=-1 and pk_id <> ? and rownum=1",
					store.get("pk_id"));
			if (rs.next()) {
				BaseUtil.showError("只能有一个采购类型是MRP默认类型,类型编号:" + rs.getString("pk_code"));
			}
		}
		//项目结案申请限制
		if("ProjectClose".equals(caller)) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(prj_code) from Project where prj_statuscode = 'FINISHED' and prj_code = ?", String.class,store.get("pc_prjcode"));
			if (dets != null) {
				BaseUtil.showError("当前项目已结案，无需再次结案！");
			}
			
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(pc_prjcode) from ProjectClose where pc_id <> ? and pc_prjcode = ?", String.class,store.get("pc_id"),store.get("pc_prjcode"));
			if (dets != null) {
				BaseUtil.showError("当前项目已进行项目结案申请，不允许重复申请！");
			}
		}
		Object[] ob=baseDao.getFieldsDataByCondition("Product", new String[]{"pr_sendstatus","pr_detail","pr_spec","pr_unit","pr_zxbzs","pr_zxdhl","pr_leadtime","pr_ltinstock","pr_orispeccode","pr_uuid","pr_brand"},"pr_id="+(store.get("pr_id")==null?0:store.get("pr_id")));	
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
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
		if (gridStore != null && gridStore.length() > 2) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : objects[0];
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, tab.toString(), (String) objects[1]);
				for (Map<Object, Object> map : grid) {
					Object id = map.get(objects[1].toString());
					if (id == null || "".equals(id.toString()) || Integer.parseInt(id.toString()) == 0) {
						map.put(objects[1], baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ"));
						gridSql.add(SqlUtil.getInsertSqlByMap(map, tab.toString()));
					}
				}
				baseDao.execute(gridSql);
			}
		}
		/**
		 * @author wsy
		 * 物料信息更新，修改物料名称规格等信息，更新物料上传状态为待上传
		 */
		if("Product!Sale".equals(caller)||"Product!Purchase".equals(caller)||"Product!Plan".equals(caller)||
				"Product!Base".equals(caller)||"Product!Finance".equals(caller)||"Product!Reserve".equals(caller)){
			if(ob!=null&&"已上传".equals(ob[0])&&	((store.get("pr_detail")!=null&&!store.get("pr_detail").equals(ob[1]==null?"":ob[1]))||
					(store.get("pr_spec")!=null&&!store.get("pr_spec").equals(ob[2]==null?"":ob[2]))||
					(store.get("pr_unit")!=null&&!store.get("pr_unit").equals(ob[3]==null?"":ob[3]))||
					(store.get("pr_zxbzs")!=null&&!store.get("pr_zxbzs").equals(ob[4]==null?"":ob[4]))||
					(store.get("pr_zxdhl")!=null&&!store.get("pr_zxdhl").equals(ob[5]==null?"":ob[5]))||
					(store.get("pr_leadtime")!=null&&!store.get("pr_leadtime").equals(ob[6]==null?"":ob[6]))||
					(store.get("pr_ltinstock")!=null&&!store.get("pr_ltinstock").equals(ob[7]==null?"":ob[7]))||
					(store.get("pr_orispeccode")!=null&&!store.get("pr_orispeccode").equals(ob[8]==null?"":ob[8]))||
					(store.get("pr_uuid")!=null&&!store.get("pr_uuid").equals(ob[9]==null?"":ob[9]))||
					(store.get("pr_brand")!=null&&!store.get("pr_brand").equals(ob[10]==null?"":ob[10])))){
				baseDao.updateByCondition("Product","pr_sendstatus='待上传'","pr_id="+store.get("pr_id")+" and nvl(pr_groupcode,' ')<>'用品'");
			}
		}
		// 记录操作
		try {
			baseDao.logger.update(caller, objs[1].toString(), store.get(objs[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteCommon(String caller, int id) {
		if ("YSReport!Mould".equals(caller)) {
			Object[] status = baseDao
					.getFieldsDataByCondition("MOD_YSREPORT", new String[] { "mo_statuscode", "mo_source" }, "mo_id=" + id);
			StateAssert.delOnlyEntering(status[0]);
			// 执行删除前的其它逻辑
			handlerService.beforeDel(caller, new Object[] { id });
			// 删除PurcMould
			baseDao.deleteById("MOD_YSREPORT", "mo_id", id);
			// 删除PurcMouldDetail
			baseDao.deleteById("MOD_YSBGDETAIL", "yd_moid", id);
			// 删除之后还原模具核价单状态
			int i = baseDao.getCountByCondition("MOD_YSREPORT", "mo_source='" + status[1] + "'");
			if (i == 0) {
				baseDao.updateByCondition("PURMOULD", "pm_statuscode='AUDITED',pm_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
						"pm_code='" + status[1] + "'");
			}
			// 记录操作
			baseDao.logger.delete(caller, "mo_id", id);
			// 执行删除后的其它逻辑
			handlerService.afterDel(caller, new Object[] { id });
		} else {
			// 删除前，关联表数据检查
			baseDao.delCheck(caller, id);
			// 执行删除前的其它逻辑
			handlerService.beforeDel(caller, new Object[] { id });
			// 删除form
			Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_detailmainkeyfield" },
					"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
			if (objs != null) {
				baseDao.deleteById(objs[0].toString().split(" ")[0], (String) objs[1], id);
			}
			// 删除DetailGrid
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table" }, "dg_caller='" + caller
					+ "' AND dg_logictype='keyField'");
			if (objects != null) {
				baseDao.deleteByCondition(objects[0].toString().split(" ")[0], (String) objs[2] + "=" + id);
			}
			// 记录操作
			try {
				baseDao.logger.delete(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	public void printCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 执行打印前的其它逻辑
			handlerService.beforePrint(caller, new Object[] { id });
			// 执行打印操作
			// 记录操作
			try {
				baseDao.logger.print(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行打印后的其它逻辑
			handlerService.afterPrint(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("print_tableisnull"));
		}
	}

	@Override
	public void auditCommon(int id, String caller) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[已提交]的单据进行审核操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			StateAssert.auditOnlyCommited(status);
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, new Object[] { id });
			// 执行审核操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='AUDITED'," + objs[2] + "='" + BaseUtil.getLocalMessage("AUDITED") + "'", objs[1] + "=" + id);
			if ("Warehouse!Base".equals(caller)) {
				// 更新物料的不良品仓库存数
				baseDao.execute("update productonhand set po_defectonhand=(select NVL(sum(pw_onhand),0) from productwh inner join warehouse on pw_whcode=wh_code where pw_onhand>0 and wh_ifdefect<>0 and pw_prodcode=po_prodcode) where po_defectonhand<>NVL((select  NVL(sum(pw_onhand),0)from productwh inner join warehouse on pw_whcode=wh_code where  pw_onhand>0 and wh_ifdefect<>0 and pw_prodcode=po_prodcode),0)");
				// 更新物料的MRP仓库存数
				baseDao.execute("update productonhand set po_mrponhand=(select NVL(sum(pw_onhand),0) from productwh inner join warehouse on pw_whcode=wh_code where pw_onhand>0 and wh_ifmrp<>0 and pw_prodcode=po_prodcode) where po_mrponhand<>NVL((select  NVL(sum(pw_onhand),0)from productwh inner join warehouse on pw_whcode=wh_code where  pw_onhand>0 and wh_ifmrp<>0 and pw_prodcode=po_prodcode),0)");
			}
			// 手机Mac地址变更
			if ("MobileMacChange".equals(caller)) {
				// 更新员工的mac地址
				baseDao.execute("update employee set em_macaddress=(select mm_macaddress from mobile_macchange where mm_id=" + id
						+ ") where em_code=(select mm_emcode from mobile_macchange where mm_id=" + id + ")");
				// cardlog记录一条信息
				baseDao.execute("insert into cardlog(cl_id,cl_time,cl_emcode,cl_emname,cl_address) select cardlog_seq.nextval,mm_date,mm_emcode,mm_emname,mm_address from mobile_macchange where mm_id="
						+ id);
			}
			// 补卡申请
			if ("MobileSignCard".equals(caller)) {
				// cardlog记录一条信息
				baseDao.execute("insert into cardlog(cl_id,cl_time,cl_emcode,cl_emname,cl_address,cl_phone) select mobile_signcard_seq.nextval,ms_signtime,ms_emcode,ms_emname,ms_address,ms_mobile from mobile_signcard where ms_id="
						+ id);
			}
			//项目结案申请
			if("ProjectClose".equals(caller)) {
				Object prjcode = baseDao.getFieldDataByCondition("projectclose", "pc_prjcode", "pc_id=" + id);
				//更新项目状态为已结案
				baseDao.updateByCondition("Project", "prj_status='已结案',prj_statuscode='FINISHED',prj_closedate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "prj_code='"+prjcode+"'");
				//更新任务状态为已结案		
				int prjid = baseDao.getFieldValue("Project", "prj_id", "prj_code='" + prjcode + "'", Integer.class);
				baseDao.updateByCondition("resourceassignment", "ra_status='已结案',ra_statuscode='ENDED'", "ra_taskid in (select id from projecttask where prjplanid="+prjid+")");
				baseDao.updateByCondition("ProjectTask", "status='已结案',statuscode='FINISHED'","prjplanid="+prjid);
				//更新子项目为已结案
				List<Object[]> prjIds = baseDao.getFieldsDatasByCondition("Project", new String[]{"prj_id"}, "PRJ_MAINPROID="+prjid);
		  		List<String> updateSqls=new LinkedList<String>();
		  		for(Object[] prj_id : prjIds){  		//更新子项目状态	
		  			updateSqls.add("update Project set prj_status='已结案',prj_statuscode='FINISHED',prj_closedate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+" where prj_id='"+prj_id[0]+"'");
		  			//更新任务状态为已结案		
		  			updateSqls.add("update resourceassignment set ra_status='已结案',ra_statuscode='ENDED' where ra_taskid in (select id from projecttask where prjplanid="+prj_id[0]+")");
		  			updateSqls.add("update ProjectTask set status='已结案',statuscode='FINISHED' where prjplanid="+prj_id[0]);
		  		}
		  		baseDao.execute(updateSqls);
			}
			//内部物流对照表更新审核人
			if ("ProdInnerRelation".equals(caller)) {
				Employee emp = SystemSession.getUser();
				baseDao.execute("update ProdInnerRelation set pir_auditman='" + emp.getEm_name() + "',pir_auditdate=sysdate where PIR_ID ="  + id);
			}
			//知识归档单更新审核人
			if ("Knowarchive".equals(caller)) {
				Employee emp = SystemSession.getUser();
				baseDao.execute("update Knowarchive set K_AUDITMAN='" + emp.getEm_name() + "',K_AUDITDATE=sysdate where k_id ="  + id);
			}
			//消息模板配置
			Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='" + caller + "'");
				//调用生成消息的存储过程
			if (mmid != null) {
				baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,"sys", id,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
				}		
			
			// 记录操作
			try {
				baseDao.logger.audit(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("audit_tableisnull"));
		}
	}

	@Override
	public void resAuditCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[已审核]的单据进行反审核操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			StateAssert.resAuditOnlyAudit(status);
			handlerService.beforeResAudit(caller, new Object[] { id });
			//项目结案申请反审核业务逻辑
			if ("ProjectClose".equals(caller)) {
				List<String> updateSqls=new LinkedList<String>();
				Object[] data = baseDao.getFieldsDataByCondition("ProjectClose left join project on pc_prjcode=prj_code", "pc_statuscode,pc_prjcode,prj_id", "pc_id=" + id);
				String prjcode=data[1].toString();
				//更新任务明细
				List<Object[]> tasks = baseDao.getFieldsDatasByCondition("projecttask", new String[]{"handstatus","handstatuscode","id"}, "prjplanid="+data[2]);
				for(Object[] task:tasks){
					if("FINISHED".equals(task[1]))
						updateSqls.add("update RESOURCEASSIGNMENT set ra_status='已完成',ra_statuscode='FINISHED' where ra_taskid ="+task[2]);
					else if("DOING".equals(task[1])){	
						List<Object[]> percent = baseDao.getFieldsDatasByCondition("RESOURCEASSIGNMENT",new String[]{"ra_id","ra_taskpercentdone"}, "ra_taskid="+task[2]);
						for(Object[] per:percent){
							if("100".equals(per[1].toString()))
								updateSqls.add("update RESOURCEASSIGNMENT set ra_status='已完成',ra_statuscode='FINISHED' where ra_id ="+per[0]);
							else
								updateSqls.add("update RESOURCEASSIGNMENT set ra_status='进行中',ra_statuscode='START' where ra_id="+per[0]);
						}
					}
					else
						updateSqls.add("update RESOURCEASSIGNMENT set ra_status='未激活',ra_statuscode='UNACTIVE' where ra_taskid ="+task[2]);			
				}
				updateSqls.add("update projecttask set status='已审核',statuscode='AUDITED' where prjplanid="+data[2]);
						
				//更新项目状态
				Object status_ = baseDao.getFieldDataByCondition("projectmaintask","pt_statuscode", "pt_prjcode='"+prjcode+"'");
				if(status_!=null&&"DOING".equals(status_.toString()))
					updateSqls.add("update project set prj_status='已启动',prj_statuscode='DOING',prj_closedate=to_date('','yyyy-mm-dd hh24:mi:ss') where prj_code='"+prjcode+"'");
				else
					updateSqls.add("update project set prj_status='未启动',prj_statuscode='UNDOING',prj_closedate=to_date('','yyyy-mm-dd hh24:mi:ss') where prj_code='"+prjcode+"'");
				baseDao.execute(updateSqls);
			}
			// 执行反审核操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='ENTERING'," + objs[2] + "='" + BaseUtil.getLocalMessage("ENTERING") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.resAudit(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("resaudit_tableisnull"));
		}
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	@Override
	public void submitCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[在录入]的单据进行提交操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			StateAssert.submitOnlyEntering(status);
			// 执行提交前的其它逻辑
			handlerService.beforeSubmit(caller, new Object[] { id });
			//项目结案申请限制
			if("ProjectClose".equals(caller)) {
				//如果是正常结案，则要判断项目阶段计划及项目任务是否都已完成
				checkPhaseAndTaskComplete(id);
			}
			// 执行提交操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='COMMITED'," + objs[2] + "='" + BaseUtil.getLocalMessage("COMMITED") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.submit(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("submit_tableisnull"));
		}
	}
		
	@Override
	public void resSubmitCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[已提交]的单据进行反提交操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			handlerService.beforeResSubmit(caller, new Object[] { id });
			StateAssert.resSubmitOnlyCommited(status);
			// 执行反提交操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='ENTERING'," + objs[2] + "='" + BaseUtil.getLocalMessage("ENTERING") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.resSubmit(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			handlerService.afterResSubmit(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("ressubmit_tableisnull"));
		}
	}

	@Override
	public int getId(String caller) {
		Object table = baseDao.getFieldDataByCondition("form", "fo_table", "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		return baseDao.getSeqId(table.toString().toUpperCase().split(" ")[0] + "_SEQ");
	}

	@Override
	public int getSequenceid(String seqname) {
		return baseDao.getSeqId(seqname);
	}

	@Override
	public void bannedCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "' and nvl(fo_statuscodefield,' ')<>' ' and nvl(fo_statusfield,' ')<>' '");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[在录入]的单据进行提交操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			if (!status.equals("CANUSE") && !status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("common.banned_onlyCanuse"));
			}
			// 执行禁用前的其它逻辑
			handlerService.handler(caller, "banned", "before", new Object[] { id });
			// 执行禁用操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='DISABLE'," + objs[2] + "='" + BaseUtil.getLocalMessage("DISABLE") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.banned(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行禁用后的其它逻辑
			handlerService.handler(caller, "banned", "after", new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("banned_tableisnull"));
		}

	}

	@Override
	public void resBannedCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 只能对状态为[已禁用]的单据进行反禁用操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			if (!status.equals("DISABLE")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
			}
			// 执行禁用前的其它逻辑
			handlerService.handler(caller, "resBanned", "before", new Object[] { id });
			// 执行反禁用操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='ENTERING'," + objs[2] + "='" + BaseUtil.getLocalMessage("ENTERING") + "'", objs[1] + "=" + id);
			// 币别维护：反禁用之后状态改为可使用
			if ("Currencys".equals(caller)) {
				baseDao.execute("update Currencys set cr_statuscode='CANUSE',cr_status='" + BaseUtil.getLocalMessage("CANUSE")
						+ "' where cr_id=" + id);
			}
			// 记录操作
			try {
				baseDao.logger.resBanned(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行禁用后的其它逻辑
			handlerService.handler(caller, "resBanned", "after", new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("resbanned_tableisnull"));
		}
	}

	@Override
	public void endCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "' and nvl(fo_statuscodefield,' ')<>' ' and nvl(fo_statusfield,' ')<>' '");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 执行禁用前的其它逻辑
			handlerService.handler(caller, "end", "before", new Object[] { id });
			// 执行禁用操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='FINISH'," + objs[2] + "='" + BaseUtil.getLocalMessage("FINISH") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.end(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行禁用后的其它逻辑
			handlerService.handler(caller, "end", "after", new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("end_tableisnull"));
		}

	}

	@Override
	public void resEndCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			// 执行反禁用操作
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='ENTERING'," + objs[2] + "='" + BaseUtil.getLocalMessage("ENTERING") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.resEnd(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("resend_tableisnull"));
		}
	}

	@Override
	public void postCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			Employee employee = SystemSession.getUser();
			// 只能对状态为[已审核]的单据进行过账操作!
			Object status = baseDao
					.getFieldDataByCondition(objs[0].toString().split(" ")[0], (String) objs[3], (String) objs[1] + "=" + id);
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyEntering"));
			}
			// 执行过账前的其它逻辑
			handlerService.beforePost(caller, new Object[] { id });
			// 执行过账操作
			// 存储过程
			Object[] vals = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_inoutno" }, "pi_id=" + id);
			String res = baseDao.callProcedure("Sp_CommitProdInout",
					new Object[] { vals[0].toString(), vals[1].toString(), String.valueOf(employee.getEm_id()) });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			baseDao.updateByCondition(objs[0].toString().split(" ")[0],
					objs[3] + "='POSTED'," + objs[2] + "='" + BaseUtil.getLocalMessage("POSTED") + "'", objs[1] + "=" + id);
			// 记录操作
			try {
				baseDao.logger.post(caller, objs[1].toString(), id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行过账后的其它逻辑
			handlerService.afterPost(caller, new Object[] { id });
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("post_tableisnull"));
		}

	}

	static final String SALEDOWN = "SELECT * FROM SaleDown WHERE sa_id=?";
	static final String SALEDOWNDETAIL = "SELECT * FROM SaledownDetail  WHERE sd_said=?";

	@Override
	public void confirmCommon(String caller, int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		// 先判断物料对照关系，如果没有建立对照关系，则不允许确认操作
		String prodcode = "";
		String detno = "";
		String prodcustcode = "";
		int sdid = 0;
		int pdid = 0;
		String prodvendcode = "";
		if (objs != null) {
			if (objs[0].equals("SaleDown")) {
				String selectSQL = "select sd_prodcode,sd_detno,sd_prodcustcode,sd_id from SaleDownDetail where sd_said='" + id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectSQL);
				while (rs.next()) {
					prodcode = rs.getString("sd_prodcode");
					detno = rs.getString("sd_detno").toString();
					prodcustcode = rs.getString("sd_prodcustcode");
					sdid = rs.getInt("sd_id");
					// 去物料资料对照表判断是否建立对应关系
					String matchSQL = "select pm_prodcustcode from ProductMatch where pm_prodcode='" + prodcode + "'";
					SqlRowList rss = baseDao.queryForRowSet(matchSQL);
					if (!rss.next()) {
						// 给出提示信息
						BaseUtil.showError("序号" + detno + "物料" + prodcode + "没有建立物料对应关系！");
						return;
					} else {
						// 先判断ERP本身的料号是否为空
						if (prodcode == null || prodcode.equals("")) {
							// 更新ERP本身的料号，根据客户物料号去ProductMatch查找物料编号
							String erpprodcode = baseDao.getFieldDataByCondition("ProductMatch", "pm_prodcode",
									"pm_prodcustcode='" + prodcustcode + "'").toString();
							String updateSQL = "update SaleDownDetail set sd_prodcode='" + erpprodcode + "' where sd_id='" + sdid + "'";
							baseDao.execute(updateSQL);
						}
					}
				}
			}
			if (objs[0].equals("Purchase")) {
				String selectSQL = "select pd_prodcode,pd_detno,pd_prodvendcode,pd_id from PurchaseDetail where pd_puid='" + id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectSQL);
				while (rs.next()) {
					prodcode = rs.getString("pd_prodcode");
					detno = rs.getString("pd_detno");
					pdid = rs.getInt("pd_id");
					prodvendcode = rs.getString("sd_prodvendcode");
					// 去物料资料对照判断是否建立对应关系
					String matchSQL = "select pm_prodvendcode from ProductMatch where pm_prodcode='" + prodcode + "'";
					SqlRowList rss = baseDao.queryForRowSet(matchSQL);
					if (!rss.next()) {
						// 给出提示信息
						BaseUtil.showError("序号" + detno + "物料" + prodcode + "没有建立物料对应关系！");
						return;
					} else {
						// 先判断ERP本身的料号是否为空
						if (prodcode == null || prodcode.equals("")) {
							// 更新ERP本身的料号，根据客户物料号去ProductMatch查找物料编号
							String erpprodcode = baseDao.getFieldDataByCondition("ProductMatch", "pm_prodcode",
									"pm_prodvendcode='" + prodvendcode + "'").toString();
							String updateSQL = "update PurchaseDetail set pd_prodcode='" + erpprodcode + "' where pd_id='" + pdid + "'";
							baseDao.execute(updateSQL);
						}
					}
				}
			}
		}
		if (objs != null) {
			// 更新单据状态，录入人，客户/供应商编号,更改上传状态(从B2B下载下来的PO就不需要再上传)
			if (objs[0].equals("SaleDown")) {
				String updateSQL = "update SaleDown set sa_confirmstatus='已确认',sa_status='在录入',sa_recorder='"
						+ SystemSession.getUser().getEm_name() + "',sa_uploadstatus='B2BDownLoad' where sa_id='" + id + "'";
				try {
					baseDao.execute(updateSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 根据客户UU号更新对应的客户编号
				try {
					String uu = String.valueOf(baseDao.getFieldDataByCondition("SaleDown", "sa_customeruu", "sa_id='" + id + "'"));
					String custcode = String.valueOf(baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_uu='" + uu + "'"));
					String thesql = "update SaleDown set sa_custcode='" + custcode + "' where sa_id='" + id + "'";
					baseDao.execute(thesql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (objs[0].equals("Purchase")) {
				String updateSQL = "update Purchase set pu_confirmstatus='已确认',pu_status='在录入',pu_recordman='"
						+ SystemSession.getUser().getEm_name() + "',pu_sendstatus='B2BDownLoad' where pu_id='" + id + "'";
				try {
					baseDao.execute(updateSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 根据供应商UU号更新对应的供应商编号
				try {
					String uu = String.valueOf(baseDao.getFieldDataByCondition("Purchase", "pu_vendoruu", "pu_id='" + id + "'"));
					String custcode = String.valueOf(baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_uu='" + uu + "'"));
					String thesql = "update Purchase set pu_vendcode='" + custcode + "' where pu_id='" + id + "'";
					baseDao.execute(thesql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			SqlRowList rs = baseDao.queryForRowSet(SALEDOWN, id);
			Object rate = rs.getObject("sa_rate");
			Object currency = rs.getObject("sa_currency");
			if(StringUtil.hasText(currency)&&!StringUtil.hasText(rate)){
				rate = baseDao.getFieldDataByCondition("currencysmonth left join Currencys on cr_name=cm_crname", "cm_crrate", "cm_yearmonth = "+DateUtil.getYearmonth(rs.getDate("sa_date"))+" and cm_crname = '"+currency+"' and nvl(cr_statuscode,' ')='CANUSE'");
				if (rate==null||"".equals(rate.toString().trim())) {
					BaseUtil.showError("币别没有设置月度汇率，不能确认平台订单！");
				}else{
					baseDao.execute("update SaleDown set sa_rate = ? where sa_id = ?",rate,id);
				}
			}
			int said = 0;
			SqlMap map = null;
			if (rs.next()) {
				said = baseDao.getSeqId("SALE_SEQ");
				String code = baseDao.sGetMaxNumber("Sale", 2);
				map = new SqlMap("Sale");
				map.set("sa_id", said);
				map.set("sa_code", code);
				map.set("sa_date", rs.getObject("sa_date"));
				map.set("sa_kind", rs.getObject("sa_kind"));
				map.set("sa_currency", rs.getObject("sa_currency"));
				map.set("sa_rate", rate);
				map.set("sa_custcode", rs.getObject("sa_custcode"));
				map.set("sa_custname", rs.getObject("sa_custname"));
				map.set("sa_sellercode", rs.getObject("sa_sellercode"));
				map.set("sa_seller", rs.getObject("sa_seller"));
				map.set("sa_pocode", rs.getObject("sa_pocode"));
				map.set("sa_apcustcode", rs.getObject("sa_apcustcode"));
				map.set("sa_apcustname", rs.getObject("sa_apcustname"));
				map.set("sa_paymentscode", rs.getString("sa_paymentscode"));
				map.set("sa_payments", rs.getObject("sa_payments"));
				map.set("sa_shcustcode", rs.getObject("sa_shcustcode"));
				map.set("sa_shcustname", rs.getObject("sa_shcustname"));
				map.set("sa_transport", rs.getObject("sa_transport"));
				map.set("sa_salemethod", rs.getObject("sa_salemethod"));
				map.set("sa_toplace", rs.getObject("sa_toplace"));
				map.set("sa_source", "正常");
				map.set("sa_getprice", rs.getObject("sa_getprice"));
				map.set("sa_status", "在录入");
				map.set("sa_recorder", SystemSession.getUser().getEm_name());
				map.set("sa_printstatus", "未打印");
				map.set("sa_mrpclosed", rs.getObject("sa_mrpclosed"));
				map.set("sa_custid", rs.getObject("sa_custid"));
				map.set("sa_statuscode", "ENTERING");
				map.set("sa_printstatuscode", "UNPRINT");
				map.set("sa_sourceid", rs.getObject("sa_id"));
				map.set("sa_paymentsid", rs.getObject("sa_paymentsid"));
				map.execute();
				rs = baseDao.queryForRowSet(SALEDOWNDETAIL, id);
				int count = 1;
				while (rs.next()) {
					Object prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + rs.getObject("sd_prodcode") + "'");
					map = new SqlMap("SaleDetail");
					map.set("sd_id", baseDao.getSeqId("SALEDETAIL_SEQ"));
					map.set("sd_said", said);
					map.set("sd_code", code);
					map.set("sd_detno", count++);
					map.set("sd_prodid", prid);
					map.set("sd_prodcode", rs.getObject("sd_prodcode"));
					map.set("sd_prodcustcode", rs.getObject("sd_prodcustcode"));
					map.set("sd_qty", rs.getObject("sd_qty"));
					map.set("sd_costprice", rs.getObject("sd_costprice"));
					map.set("sd_price", rs.getObject("sd_price"));
					map.set("sd_taxtotal", rs.getObject("sd_taxtotal"));
					map.set("sd_total", rs.getObject("sd_total"));
					map.set("sd_taxrate", rs.getObject("sd_taxrate"));
					map.set("sd_forecastcode", rs.getObject("sd_forecastcode"));
					map.set("sd_forecastdetno", rs.getObject("sd_forecastdetno"));
					map.set("sd_yqty", rs.getObject("sd_yqty"));
					map.set("sd_sendqty", rs.getObject("sd_sendqty"));
					map.set("sd_leaveassign", rs.getObject("sd_leaveassign"));
					map.set("sd_readyqty", rs.getObject("sd_readyqty"));
					map.set("sd_delivery", rs.getObject("sd_delivery"));
					map.set("sd_pmcdate", rs.getObject("sd_pmcdate"));
					map.set("sd_leadtime", rs.getObject("sd_leadtime"));
					map.set("sd_pmcremark", rs.getObject("sd_pmcremark"));
					map.set("sd_custprodcode", rs.getObject("sd_custprodcode"));
					map.set("sd_bomid", rs.getObject("sd_bomid"));
					map.set("sd_statuscode", "ENTERING");
					map.set("sd_status", "在录入");
					map.set("sd_remark", rs.getObject("sd_remark"));
					map.set("sd_vendcode", rs.getObject("sd_vendcode"));
					map.set("sd_sourceid", rs.getObject("sd_id"));
					map.set("sd_prodkind", rs.getObject("sd_prodkind"));
					map.set("sd_description", rs.getObject("sd_description"));
					map.set("sd_bonded", rs.getObject("sd_bonded"));
					map.execute();
				}
				baseDao.execute("update SaleDown set sa_confirmstatus='已确认' where sa_id=" + id);
			}
			// 记录操作
			try {
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.onConfirm"), BaseUtil
						.getLocalMessage("msg.onConfirmSuccess"), caller + "|" + objs[1] + "=" + id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getCountByTable(String condition, String tablename) {
		return baseDao.getCountByCondition(tablename, condition);
	}

	@Override
	public String[] printCommon(int id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		if (caller == "FeePlease!Mould") {
			baseDao.print("MOULDFEEPLEASE", "mp_id=" + id, "mp_printstatus", "mp_printstatuscode");
			// 记录操作
			baseDao.logger.print(caller, "mp_id", id);
		}
		// 客户信用额度 修改打印状态
		if (caller == "CustomerCredit") {
			baseDao.print("CustomerCredit", "cuc_id=" + id, "cuc_printstatus", "cuc_printstatuscode");
			// 记录操作
			baseDao.logger.print(caller, "cuc_id", id);
		}
		// 客户信用额度变更 修改打印状态
		if (caller == "CreditChange") {
			baseDao.print("CreditChange", "cc_id=" + id, "cc_printstatus", "cc_printstatuscode");
			// 记录操作
			baseDao.logger.print(caller, "cc_id", id);
		}
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { id });
		return keys;
	}

	@Override
	public void modify(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改数据前的其它逻辑
		handlerService.handler(caller, "modify", "before", new Object[] { store });
		// 修改form
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		Object id = 0;
		if (objs != null) {
			String tab = (String) objs[0];
			String keyF = (String) objs[1];
			if (tab != null && keyF != null) {
				id = store.get(keyF);
				if (tab.contains(" ")) {
					tab = tab.substring(0, tab.indexOf(" "));
				}
				String formSql = SqlUtil.getUpdateSqlByFormStore(store, tab, keyF);
				baseDao.execute(formSql);
				// 执行修改数据后的其它逻辑
				handlerService.handler(caller, "modify", "after", new Object[] { store });
			}
		}
		// 记录操作
		try {
			Set<Object> keys = store.keySet();
			Object field = null;
			for(Object key : keys){
				if(!key.toString().equals(objs[1].toString())){
					field = baseDao.getFieldDataByCondition("formdetail left join form on fo_id = fd_foid", "fd_caption", "fo_caller='"+caller+"' and fd_field='"+key+"'");
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.modify"), "更新字段("+field+")为:"+store.get(key), caller + "|" + objs[1] + "=" + id));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void modifyDetail(String caller, String gridStore, String log) {
		// 修改明细数据之前逻辑
		handlerService.handler(caller, "modifyDetail", "before", new Object[] { gridStore });
		// 修改Grid
		StringBuffer detnos = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		if (gridStore != null && gridStore.length() > 2) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			List<Object[]> modify = baseDao.getFieldsDatasByCondition("detailgrid", new String[] { "dg_field", "dg_caption" }, "dg_caller='"
					+ caller + "' AND dg_modify='T'");
			Object detnofield = baseDao.getFieldDataByCondition("detailgrid", "dg_field", "dg_caller='" + caller
					+ "' and dg_logictype='detno'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : objects[0];
				if (tab.toString().contains(" ")) tab=tab.toString().split(" ")[0];
				String keyField = objects[1].toString().contains(" ") ? objects[1].toString().split(" ")[1] : objects[1].toString();
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, tab.toString(), keyField);
				for (Map<Object, Object> map : grid) {
					Object id = map.get(keyField);
					if(modify.size()>0){
						for(Object[] modi : modify){
							Object[] field = baseDao.getFieldsDataByCondition(tab.toString(), new String[]{ modi[0].toString(),detnofield.toString()}, ""+keyField+"="+id);
							if(field[0]!=null && !field[0].equals(map.get(modi[0].toString()))){
								StringBuffer sb1 = new StringBuffer();
								sb1.append("行号"+field[1]+":"+modi[1]+"由"+field[0]+"变更为"+map.get(modi[0].toString())+";<br> ");
								sb.append(sb1);
							}
						}
					}
					if (detnofield != null) {
						detnos.append(map.get(detnofield)).append(",");
					}
					if (id == null || "".equals(id.toString()) || Integer.parseInt(id.toString()) == 0) {
						map.put(keyField, baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ"));
						gridSql.add(SqlUtil.getInsertSqlByMap(map, tab.toString()));
					}
				}
				baseDao.execute(gridSql);
				// 修改明细数据之后逻辑
				handlerService.handler(caller, "modifyDetail", "after", new Object[] { gridStore });
			}
		}
		// 记录操作
		try {
			if (detnos.length() > 0) {
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.modifyDetail"),
						sb.toString() , log));
			} else {
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.modifyDetail"),
						BaseUtil.getLocalMessage("msg.modifyDetailSuccess"), log));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 客户服务确认 confirmres：确认结果：同意、不同意 confirmdesc：确认描述
	 */
	@Override
	public void openSysConfirmCommon(String caller, int id, String confirmres, String confirmdesc) {
		// 获得表名 主键字段
		Object[] ob = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");
		String tablename = ob[0].toString().split(" ")[0];
		// 更新确认结果
		baseDao.updateByCondition(tablename, "CNOFIRMRESULT='" + confirmres + "',confirmdesc='" + confirmdesc + "',CONFIRMFIELD=-1", ob[1]
				+ "=" + id);
		// 获得客户UU 联系人UU 联系人
		Object[] custInfo = baseDao.getFieldsDataByCondition(tablename, new String[] { "Custuu", "custlinkmanUU", "custlinkman" }, ob[1]
				+ "=" + id);
		// 更新客户服务通知状态
		baseDao.execute("update CURNOTIFY set CN_STATUS='已确认' where CN_CALLER='" + caller + "' and cn_keyvalue=" + id + " and cn_emuu="
				+ custInfo[1] + " and CN_ENUU=" + custInfo[0]);
		// 获得UAS端caller
		Object uascaller = baseDao.getFieldDataByCondition("CURNAVIGATION", "cn_uascaller", "cn_caller='" + caller + "'");
		// 获得要录入人id，姓名
		Object[] notify = baseDao.getFieldsDataByCondition("CURNOTIFY left join employee on cn_man=em_name", new String[] { "cn_desc",
				"em_id", "cn_man" }, "CN_CALLER='" + caller + "' and cn_keyvalue=" + id + " and cn_emuu=" + custInfo[1] + " and CN_ENUU="
				+ custInfo[0]);
		if (notify != null) {
			Object url_ob = baseDao.getFieldDataByCondition("datalist", "dl_lockpage", "dl_caller='" + uascaller + "'");
			String url = url_ob.toString();
			if (url_ob.toString().contains("?")) {
				url += "&formCondition=" + ob[1] + "IS" + id;
			} else {
				url += "?formCondition=" + ob[1] + "IS" + id;
			}
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			String str = "通知&nbsp;[" + notify[0].toString() + "]已确认<a href=\"javascript:openUrl(''" + url
					+ "'')\" style=\"font-size:14px; color:blue;\">" + "查看详情</a></br>";
			List<String> sqls = new ArrayList<String>();
			sqls.add("insert into pagingrelease (pr_id,pr_releaser,pr_date,PR_FROM,pr_context) values(" + pr_id + ",'" + custInfo[2]
					+ "',sysdate,'opensys','" + str + "')");
			int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
			sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(" + prd_id + "," + pr_id + ","
					+ notify[1] + ",'" + notify[2] + "')");
			sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE "
					+ "where pr_id="+pr_id);
			sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id);
		}
	}

	@Override
	public Map<String, Object> getPrintSet() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", "");
		map.put("bottom", "");
		map.put("img", "");
		Object[] ob = baseDao.getFieldsDataByCondition("PrintSet", new String[] { "header", "bottom", "img" }, "1=1");
		if (ob != null) {
			map.put("header", ob[0]);
			map.put("bottom", ob[1]);
			map.put("img", ob[2]);
		}
		return map;
	}
	@Override
	public String turnCommon(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[]{"fo_table","fo_keyfield","fo_detailtable","fo_detailkeyfield","fo_detailmainkeyfield"}, "fo_caller='"+caller.split("!transfer")[0]+"'");
		Key key = null;
		if(objs!=null){
			key = transferRepository.transfer(caller, maps.get(0).get(objs[4]));
			if(key==null){
				BaseUtil.showError("请正确配置转单表！");
			}
			transferRepository.transfer(caller, maps, key);
		}else{
			BaseUtil.showError("请正确配置Form表单！");
		}
		return "转单成功";
	}

	@Override
	public Map<String, Object> getButtonconfigs(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = baseDao.queryForList("select * from buttonconfigs where bc_caller='"+caller+"'");
		if(list.size()>0){
			map.put("log", list);
			return map;
		}
		return null;
	}

	@Override
	public String turnAllCommon(String caller, int id, String name) {
		Object procedure = baseDao.getFieldDataByCondition("buttonconfigs", "BC_PROCEDURE", "BC_BUTTONNAME='"+name+"' and BC_CALLER='"+caller+"'");
		if(procedure!=null){
			String result = baseDao.callProcedure(procedure.toString(),id,SystemSession.getUser().getEm_name());
			if (result != null && !result.trim().equals("")) {
				return result;
			}
		}else{
			BaseUtil.showError("请正确配置需要调用的存储过程！");
		}
		return null;
	}
	@Override
	public Map<String, Object> getqueryConfigs(String caller,String xtype) {
		Object[] config = baseDao.getFieldsDataByCondition("queryConfigs", new String[]{"type","querycaller","buttondesc","fixedcondition","fieldcondition","buttonlength","winheight"}, "pagecaller='"+caller+"' and xtype='"+xtype+"'");
		Map<String, Object> map = new HashMap<String,Object>();
		if(config!=null){
			map.put("type", config[0]);
			map.put("querycaller", config[1]);
			map.put("buttondesc", config[2]);
			map.put("fixedcondition", config[3]);
			map.put("fieldcondition", config[4]);
			map.put("buttonlength", config[5]);
			map.put("winheight", config[6]);
			return map;
		}
		return null;
	}

	@Override
	public Map<String, Object> getBankName(String condition, Integer start,
			Integer end) {
		int count=0;
		Map<String,Object> data=null;
		Map <String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>>datas =new LinkedList<Map<String,Object>>();
		if(condition.contains("(空)")){
			condition=condition.replace("(空)", "");
		}
		if (condition!=null&&!"".equals(condition)) {
			condition=condition.replace("'", "''");
			count = baseDao.getCount("select count(1) from bank$code where bankname like '"+condition+"'");
			List<Map<String,Object>> maps = baseDao.queryForList("select * from (select rownum rn ,a.* from (select * from bank$code order by  bankway desc,bankcode) a where bankname like '"+condition+"' and rownum <"+end+")  where rn>"+start);
			for(Map<String,Object> m:maps){
				data=new HashMap<String,Object>();
				data.put("bankname",m.get("BANKNAME"));
				data.put("bankway", m.get("BANKWAY"));
				data.put("bankcode", m.get("BANKCODE"));
				datas.add(data);
			} 
		}
		map.put("num",count);
		map.put("data", datas);
		return map;
		}
	
	private void checkPhaseAndTaskComplete(int pc_id){
		SqlRowList rs = baseDao.queryForRowSet("select * from projectclose left join project on prj_code=pc_prjcode where pc_id=" + pc_id);
		if(rs.next()){
			if("正常结案".equals(rs.getString("pc_closetype"))){
				//检查项目是否已启动
				if(!"DOING".equals(rs.getString("prj_statuscode"))){
					BaseUtil.showError("当前项目处于非已启动状态，不能正常结案!");
				}
				int prjId = rs.getInt("prj_id");
				checkTask(prjId);
				//判断子项目
				List<Object[]> prjIds = baseDao.getFieldsDatasByCondition("Project", new String[]{"prj_id"}, "PRJ_MAINPROID="+prjId);
				for(Object[] id:prjIds){ 
					checkTask(Integer.parseInt(id[0].toString()));
				}
			}
		}
	}
	
	private void checkTask(int prjId){
		//检查项目阶段计划是否已完成
		boolean bool = baseDao.checkIf("projectphase", "pp_prjid=" + prjId + " and nvl(pp_status,' ')<>'已完成'");
		if(bool){
			BaseUtil.showError("当前项目的阶段计划未全部完成，不能正常结案！");
		}else{
			//检查项目任务是否全部完成
			boolean bl = baseDao.checkIf("projecttask", "nvl(parentid,0)<>0 and parentid in (select id from projecttask where prjplanid="+prjId+") and nvl(handstatuscode,' ')<>'FINISHED' and id not in (select parentid from projecttask where prjplanid="+prjId+")");
			if(bl){
				BaseUtil.showError("当前项目的任务未全部完成，不能正常结案！");
			}
		}
	}

	@Override
	public void abate(Integer id, String caller, String remark) {
		handlerService.handler(caller, "abate", "before", new Object[] { id });
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_detailtable", "fo_keyfield", "fo_detailmainkeyfield", "FO_DETAILDETNOFIELD", "fo_detailkeyfield"},
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		remark = remark == null ? "" : remark.replace("'", "''");
		if ("ProdInnerRelation".equals(caller)) {
			// 执行明细行失效操作
			baseDao.updateByCondition("PRODINNERRELATIONDET", "prd_status='无效',prd_statuscode='UNVALID',prd_remark='" + remark + "'", "prd_id=" + id);
		}
		// 记录操作
		if (objs != null) {
			String tab = (String) objs[0];
			if (tab != null && objs[2] != null && objs[4] != null && objs[3] != null) {
				if (tab.contains(" ")) {
					tab = tab.substring(0, tab.indexOf(" "));
				}
				Object[] data = baseDao.getFieldsDataByCondition(tab, new String []{String.valueOf(objs[2]), String.valueOf( objs[3])}, objs[4] +"=" + id);
				if (data != null && objs[1] !=null) {
					baseDao.logger.others("转无效", "转无效成功！序号：" + data[1], caller, String.valueOf(objs[1]), data[0]);
				}
			}
		}
		handlerService.handler(caller, "abate", "after", new Object[] { id });
	}

	@Override
	public void resAbate(Integer id, String caller, String remark) {
		handlerService.handler(caller, "resAbate", "before", new Object[] { id });
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_detailtable", "fo_keyfield", "fo_detailmainkeyfield", "FO_DETAILDETNOFIELD", "fo_detailkeyfield"},
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		remark = remark == null ? "" : remark.replace("'", "''");
		if ("ProdInnerRelation".equals(caller)) {
			// 执行明细行有效操作
			baseDao.updateByCondition("PRODINNERRELATIONDET", "prd_status='有效',prd_statuscode='VALID',prd_remark='" + remark + "'", "prd_id=" + id);
		}
		// 记录操作
		if (objs != null) {
			String tab = (String) objs[0];
			if (tab != null && objs[2] != null && objs[4] != null && objs[3] != null) {
				if (tab.contains(" ")) {
					tab = tab.substring(0, tab.indexOf(" "));
				}
				Object[] data = baseDao.getFieldsDataByCondition(tab, new String []{String.valueOf(objs[2]), String.valueOf( objs[3])}, objs[4] +"=" + id);
				if (data != null && objs[1] !=null) {
					baseDao.logger.others("转有效", "转有效成功！序号：" + data[1], caller, String.valueOf(objs[1]), data[0]);
				}
			}
		}
		handlerService.handler(caller, "resAbate", "after", new Object[] { id });
	}
}
