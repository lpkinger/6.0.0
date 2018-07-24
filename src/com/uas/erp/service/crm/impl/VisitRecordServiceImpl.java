package com.uas.erp.service.crm.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.VisitRecordService;

@Service("visitRecordService")
public class VisitRecordServiceImpl implements VisitRecordService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveVisitRecord(String formStore, String[] gridStore,
			String caller) {
		//Map<Object, Object> store = JSONUtil.toMap(formStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object vr_visitend=store.get("vr_visitend");
		if(!vr_visitend.equals("")){			
			Date nowtime = new Date();
			Date visitend=null;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				 visitend=dateFormat.parse(vr_visitend.toString());
				
			} catch (Exception e) {
				BaseUtil.showError("时间格式转换错误");
			}
			if (visitend.after(nowtime)) {
				
				BaseUtil.showError("结束时间不能大于当前系统时间");
			}
			
		}
		
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VisitRecord", "vr_code='"
				+ store.get("vr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		String detail = store.get("vr_detail") + "";
		Object vrid = store.get("vr_id");
		store.remove("vr_detail");
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByMap(store, "VisitRecord");
		baseDao.execute(formSql);
		if(detail!=null) detail=detail.replaceAll("%n", "\n");
		baseDao.saveClob("VisitRecord", "vr_detail", detail, "vr_id=" + vrid);
		// 保存Players
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		for (Map<Object, Object> map : grid2) {
			map.put("pl_id", baseDao.getSeqId("PLAYERS_SEQ"));
		}
		List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2,
				"Players");
		baseDao.execute(gridSql2);

		// 保存CuPlayers
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		for (Map<Object, Object> map : grid) {
			map.put("cup_id", baseDao.getSeqId("CUPLAYERS_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CuPlayers");
		baseDao.execute(gridSql);

		// 保存ProductInfo
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(gridStore[3]);
		for (Map<Object, Object> map : grid4) {
			List<String> sqls = new ArrayList<String>();
			boolean newChange = false;
			if (map.get("pi_bccode") != null) {
				int count = baseDao.getCountByCondition(
						"BusinessChance",
						"bc_custcode='"
								+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
								+ map.get("pi_vendor") + "'");
				newChange = count == 0;
			} else {
				newChange = map.get("pi_bcdescription") != null;
			}
			if (newChange) {// 如果在商机表中不存在此商机，则添加进去
				int bc_id = baseDao.getSeqId("BusinessChance_seq");
				String bcCode = baseDao.sGetMaxNumber("BusinessChance", 2); 
				String format = Constant.ORACLE_YMD_HMS;
				Object recorddate = store.get("vr_recorddate");
				if(recorddate!=null){
					boolean bol = DateUtil.isValidDate(recorddate.toString().trim(),Constant.YMD_HMS);
					if(!bol){
						format = Constant.ORACLE_YMD;;
					}
				}else{
					recorddate = DateUtil.currentDateString(Constant.YMD_HMS);
				}
				String contactSql = "insert into BusinessChance(bc_id,bc_code,bc_description,bc_recorddate,bc_recorder,bc_statuscode,bc_status,bc_custcode,bc_custname,bc_prjcode,bc_currentprocess,bc_brand,bc_tgxinghao,bc_model) values ("
						+ bc_id
						+ ",'"
						+ bcCode
						+ "','"
						+ map.get("pi_bcdescription")
						+ "',"
						+ "to_date('"
						+ recorddate
						+ "','"+format+"')"
						+ ",'"
						+ store.get("vr_recorder")
						+ "','"
						+ "ENTERING"
						+ "','"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "','"
						+ store.get("vr_cuuu")
						+ "','"
						+ store.get("vr_cuname")
						+ "','"
						+ map.get("pi_vendor") 
						+ "','" 
						+ map.get("pi_projprogress")
						+ "','" 
						+ map.get("pi_brand") 
						+ "','" 
						+ map.get("pi_model") 
						+ "','" 
						+ map.get("pi_materialcode") 
						+ "')";
				sqls.add(contactSql);
				map.put("pi_bccode", bcCode);
				map.put("pi_bcid", bc_id);
			}else{
				Object bc_id = baseDao.getFieldDataByCondition("businesschance", "bc_id", "bc_custcode='"
						+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
						+ map.get("pi_vendor") + "'");
				Object bcCode = baseDao.getFieldDataByCondition("businesschance", "bc_code", "bc_custcode='"
						+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
						+ map.get("pi_vendor") + "'");
				map.put("pi_bccode", bcCode);
				map.put("pi_bcid", bc_id);
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid4, "ProductInfo", "pi_id"));
			baseDao.execute(sqls);
		}
		
		// 保存VisitFeedBack
		List<Map<Object, Object>> grid6 = BaseUtil
				.parseGridStoreToMaps(gridStore[5]);
		for (Map<Object, Object> map : grid6) {
			map.put("fb_id", baseDao.getSeqId("VisitFeedBack_SEQ"));
		}
		List<String> gridSql6 = SqlUtil.getInsertSqlbyGridStore(grid6,
				"VisitFeedBack");
		baseDao.execute(gridSql6);

		// 保存VisitRecordDetail
		List<Map<Object, Object>> assgrid = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		for (Map<Object, Object> map : assgrid) {
			map.put("vrd_id", baseDao.getSeqId("VISITRECORDDETAIL_SEQ"));
		}
		List<String> gridSql1 = SqlUtil.getInsertSqlbyGridStore(assgrid,
				"VisitRecordDetail");
		baseDao.execute(gridSql1);

		// 记录操作
		baseDao.logger.save("VisitRecord", "vr_id", store.get("vr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	
		Object vgid=store.get("vr_groupid");
		Object vremid=store.get("vr_recorderid");
		if(vgid!=null&&vremid!=null&&!"".equals(vremid)){
			int vr_id=Integer.parseInt(store.get("vr_id").toString());
			if (vgid!=null&&!"".equals(vgid)) {
				String vr_groupid=vgid.toString();			
				String iString="employee#"+vremid;			
				if(vr_groupid.indexOf(iString)==-1){
					vr_groupid=vr_groupid+';'+iString;
				}				
				baseDao.setReader("VisitRecord", vr_id, vr_groupid);	
			}else{
				String vr_groupid="employee#"+vremid;
				baseDao.setReader("VisitRecord", vr_id, vr_groupid);	
			}
		}
		
		
	}

	@Override
	public void deleteVisitRecord(int vr_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { vr_id });
		// 删除VisitRecord
		baseDao.deleteById("VisitRecord", "vr_id", vr_id);
		// 删除Players
		baseDao.deleteById("Players", "pl_vrid", vr_id);
		// 删除CuPlayers
		baseDao.deleteById("CuPlayers", "cup_vrid", vr_id);
		// 删除ProductInfo
		baseDao.deleteById("ProductInfo", "pi_vrid", vr_id);
		// 删除VisitFeedBack
		baseDao.deleteById("VisitFeedBack", "fb_vrid", vr_id);
		// 删除
		baseDao.deleteById("VisitRecordDetail", "vrd_vrid", vr_id);
		// 记录操作
		baseDao.logger.delete(caller, "vr_id", vr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, vr_id);
	}

	@Override
	@Transactional
	public void updateVisitRecordById(String formStore, String[] gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + store.get("vr_id"));
		StateAssert.updateOnlyEntering(status);
		
		Object vr_visitend=store.get("vr_visitend");
		if(!vr_visitend.equals("")){			
			Date nowtime = new Date();
			nowtime = new Date(nowtime.getTime() + 1000*60*30);
			Date visitend=null;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				 visitend=dateFormat.parse(vr_visitend.toString());
				
			} catch (Exception e) {
				BaseUtil.showError("时间格式转换错误");
			}
			if (visitend.after(nowtime)) {
				
				BaseUtil.showError("结束时间不能大于当前系统时间");
			}
			
		}

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改主表
		String detail = store.get("vr_detail") + "";
		Object vrid = store.get("vr_id");
		store.remove("vr_detail");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VisitRecord",
				"vr_id");
		baseDao.execute(formSql);
		if(detail!=null) detail=detail.replaceAll("%n", "\n");
		baseDao.saveClob("VisitRecord", "vr_detail", detail, "vr_id=" + vrid);

		// 修改Players
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		List<String> gridSql2 = null;
		if (grid2.size() > 0) {
			gridSql2 = SqlUtil.getUpdateSqlbyGridStore(grid2, "Players",
					"pl_id");
			for (Map<Object, Object> s : grid2) {
				Object aid = s.get("pl_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("PLAYERS_SEQ");
					Object a=baseDao.getFieldDataByCondition("Players", "count(1) a", "pl_name='"+s.get("pl_name")+"' and pl_vrid="+s.get("pl_vrid")+"");
					if(Integer.parseInt(a.toString())==0){
						String sql = SqlUtil.getInsertSqlByMap(s, "Players",
								new String[] { "pl_id" }, new Object[] { id });
						gridSql2.add(sql);	
					}					
				}
			}
			baseDao.execute(gridSql2);

		}

		// 修改CuPlayers
		List<Map<Object, Object>> grid1 = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		List<String> gridSql1 = null;
		if (grid1.size() > 0) {
			gridSql1 = SqlUtil.getUpdateSqlbyGridStore(grid1, "CuPlayers",
					"cup_id");
			for (Map<Object, Object> s : grid1) {
				Object aid = s.get("cup_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("CUPLAYERS_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "CuPlayers",
							new String[] { "cup_id" }, new Object[] { id });
					gridSql1.add(sql);
				}
			}
			baseDao.execute(gridSql1);
		}

		// 修改ProductInfo
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(gridStore[3]);
		if (grid3.size() > 0) {
			List<String> sqls = new ArrayList<String>();
			for (Map<Object, Object> s : grid3) {
				boolean newChange = false;
				if (s.get("pi_bccode") != null) {
					int count = baseDao.getCountByCondition(
							"BusinessChance",
							"bc_custcode='"
									+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
									+ s.get("pi_vendor") + "'");
					newChange = count == 0;
				} else {
					newChange = s.get("pi_bcdescription") != null;
				}
				if (newChange) {// 如果在商机表中不存在此商机，则添加进去
					int bc_id = baseDao.getSeqId("BusinessChance_seq");
					String bcCode = baseDao.sGetMaxNumber("BusinessChance", 2);
					String format = Constant.ORACLE_YMD_HMS;
					Object recorddate = store.get("vr_recorddate");
					if(recorddate!=null){
						boolean bol = DateUtil.isValidDate(recorddate.toString().trim(),Constant.YMD_HMS);
						if(!bol){
							format = Constant.ORACLE_YMD;;
						}
					}else{
						recorddate = DateUtil.currentDateString(Constant.YMD_HMS);
					}
					String contactSql = "insert into BusinessChance(bc_id,bc_code,bc_description,bc_recorddate,bc_recorder,bc_statuscode,bc_status,bc_custcode,bc_custname,bc_prjcode,bc_currentprocess,bc_brand,bc_tgxinghao,bc_model) values ("
							+ bc_id
							+ ",'"
							+ bcCode
							+ "','"
							+ s.get("pi_bcdescription")
							+ "',"
							+ "to_date('"
							+ recorddate
							+ "','"+format+"')"
							+ ",'"
							+ store.get("vr_recorder")
							+ "','"
							+ "ENTERING"
							+ "','"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "','"
							+ store.get("vr_cuuu")
							+ "','"
							+ store.get("vr_cuname")
							+ "','"
							+ s.get("pi_vendor") 
							+ "','" 
							+ s.get("pi_projprogress")
							+ "','" 
							+ s.get("pi_brand") 
							+ "','" 
							+ s.get("pi_model") 
							+ "','" 
							+ s.get("pi_materialcode") 
							+ "')";
					sqls.add(contactSql);
					s.put("pi_bccode", bcCode);
					s.put("pi_bcid", bc_id);
				}else{
					Object bc_id = baseDao.getFieldDataByCondition("businesschance", "bc_id", "bc_custcode='"
							+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
							+ s.get("pi_vendor") + "'");
					Object bcCode = baseDao.getFieldDataByCondition("businesschance", "bc_code", "bc_custcode='"
							+ store.get("vr_cuuu") + "' and bc_custname='" + store.get("vr_cuname") + "' and bc_prjcode='"
							+ s.get("pi_vendor") + "'");
					s.put("pi_bccode", bcCode);
					s.put("pi_bcid", bc_id);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid3, "ProductInfo", "pi_id"));
			baseDao.execute(sqls);
		}


		// 修改VisitFeedBack
		List<Map<Object, Object>> grid6 = BaseUtil
				.parseGridStoreToMaps(gridStore[5]);
		List<String> gridSql6 = null;
		if (grid6.size() > 0) {
			gridSql6 = SqlUtil.getUpdateSqlbyGridStore(grid6, "VisitFeedBack",
					"fb_id");
			for (Map<Object, Object> s : grid6) {
				Object aid = s.get("fb_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VisitFeedBack_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "VisitFeedBack",
							new String[] { "fb_id" }, new Object[] { id });
					gridSql6.add(sql);
				}
			}
			baseDao.execute(gridSql6);
		}

		// 修改VisitRecordDetail
		List<Map<Object, Object>> assgrid = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		List<String> gridSql = null;
		if (assgrid.size() > 0) {
			gridSql = SqlUtil.getUpdateSqlbyGridStore(assgrid,
					"VisitRecordDetail", "vrd_id");
			for (Map<Object, Object> s : assgrid) {
				Object aid = s.get("vrd_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VISITRECORDDETAIL_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s,
							"VisitRecordDetail", new String[] { "vrd_id" },
							new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}
		// 记录操作
		baseDao.logger.update("VisitRecord", "vr_id", store.get("vr_id"));
		// 执行修改后的其它逻辑
		
		Object vgid=store.get("vr_groupid");
		Object vremid=store.get("vr_recorderid");
		if(vgid!=null&&vremid!=null&&!"".equals(vremid)){
			int vr_id=Integer.parseInt(store.get("vr_id").toString());
			if (vgid!=null&&!"".equals(vgid)) {
				String vr_groupid=vgid.toString();			
				String iString="employee#"+vremid;			
				if(vr_groupid.indexOf(iString)==-1){
					vr_groupid=vr_groupid+';'+iString;
				}				
				baseDao.setReader("VisitRecord", vr_id, vr_groupid);	
			}else{
				String vr_groupid="employee#"+vremid;
				baseDao.setReader("VisitRecord", vr_id, vr_groupid);	
			}
		}

}

	@Override
	@Transactional
	public void auditVisitRecord(int vr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		Object[] vr_newtitle = baseDao.getFieldsDataByCondition("VisitRecord left join Employee on em_name=vr_recorder", new String[]{"vr_newtitle","vr_purpose","em_id","vr_recorder","vr_code"}, "vr_id=" + vr_id);

		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, vr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='AUDITED',vr_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',vr_auditor='"
						+ SystemSession.getUser().getEm_name()
						+ "',vr_audittime="
						+ DateUtil.parseDateToOracleString(null, new Date()),
				"vr_id=" + vr_id);
		
		//添加客户联系人
		String sql = "insert into contact(ct_id,ct_name,ct_age,ct_sex,ct_job,ct_mobile,ct_officeemail,ct_contactmark,ct_cucode,ct_cuname) select CONTACT_seq.nextval,cup_name,cup_age,cup_sex,cup_position,cup_tel,cup_email,cup_remark,vr_cuuu,vr_cuname from cuplayers left join visitrecord on cup_vrid=vr_id where vr_id=" + vr_id + " and vr_cuuu is not null and cup_name is not null and (vr_cuuu,cup_name) not in(select ct_cucode,ct_name from contact where ct_cucode is not null and ct_name is not null)";
		baseDao.execute(sql);
		
		if (baseDao.isDBSetting("updateBusinessChance")) {
			// 商机
			Object date = baseDao.getFieldDataByCondition("VisitRecord",
					"to_char(vr_recorddate,'yyyy-MM-dd')", "vr_id=" + vr_id);
			List<Object[]> gridDatas = baseDao.getFieldsDatasByCondition(
					"ProductInfo", new String[] { "pi_vendor", "pi_brand",
							"pi_model", "pi_projprogress", "pi_projprogressno",
							"pi_bcid", "pi_projprogresscode" }, "pi_vrid="
							+ vr_id);
			Object[] datas = baseDao.getFieldsDataByCondition("VisitRecord",
					"vr_cuuu,vr_cuname,vr_recorder,vr_code", "vr_id=" + vr_id);

			for (Object[] O : gridDatas) {
				// 更新商机阶段
				if (O[5] != null && Integer.parseInt(O[5].toString()) > 0) {
					Integer bs_detno1 = baseDao
							.getFieldValue(
									"businesschance left join businesschancestage on bs_name=bc_currentprocess",
									"nvl(bs_detno,0)", "bc_id=" + O[5],
									Integer.class);
					Integer bs_detno = baseDao.getFieldValue(
							"businesschancestage", "nvl(bs_detno,0)", "bs_name='"
									+ O[3] + "'", Integer.class);
					if (bs_detno != null && bs_detno1 != null && bs_detno >= bs_detno1) {
						baseDao.updateByCondition(
								"BusinessChance",
								"bc_currentprocess='" + O[3] + "',bc_desc"
										+ bs_detno + "='" + O[3] + "',bc_date"
										+ bs_detno + "=to_date('"
										+ date.toString() + "','yyyy-MM-dd')",
								"bc_id=" + O[5]);
					}
				}
				// 插入一条记录到商机动态表中
				int bcd_id = baseDao.getSeqId("BusinessChanceData_seq");
				String link = "jsps/crm/customermgr/customervisit/visitRecord.jsp?formCondition=vr_idIS"
						+ vr_id + "&gridCondition=vrd_vridIS" + vr_id;
				String contactSql = "insert into BusinessChanceData (bcd_id,bcd_bcid,bcd_code,bcd_bscode,bcd_bsname,bcd_date,bcd_man,bcd_statuscode,bcd_status,bcd_sourcecode,bcd_sourcelink) values ("
						+ bcd_id
						+ ","
						+ O[5]
						+ ",'"
						+ baseDao.sGetMaxNumber("BusinessChanceData", 2)
						+ "','"
						+ O[6]
						+ "','"
						+ O[3]
						+ "',"
						+ "to_date('"
						+ date.toString()
						+ "','yyyy-MM-dd')"
						+ ",'"
						+ datas[2]
						+ "','"
						+ "ENTERING"
						+ "','"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "','"
						+ datas[3] + "','" + link + "')";
				baseDao.execute(contactSql);
			}
		}
		List<Object> taskids=baseDao.getFieldDatasByCondition("ProductInfo", "pi_taskid", "PI_VRID="+vr_id);
		List<String> sqls=new ArrayList<String>();
		Employee employee = SystemSession.getUser();
		if(taskids!=null){
			for(Object taskId:taskids){
				if(vr_newtitle[0]!=null && ("".equals(vr_newtitle[0].toString())||"差".equals(vr_newtitle[0].toString()))){
					
				}else{					
					sqls.add("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
							+ taskId);
					sqls.add("update ProjectTask set handstatus='已完成',handstatuscode='FINISHED',percentdone=100 where id="+taskId);
				}
			}
		}
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.audit(caller, "vr_id", vr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, vr_id);
		if(vr_newtitle[0]!=null && ("".equals(vr_newtitle[0].toString())||"差".equals(vr_newtitle[0].toString()))){
			int pr_id = baseDao.getSeqId("pagingrelease_seq");
			StringBuffer sb = new StringBuffer();
			sb.append("知会 "+vr_newtitle[2]+"[" + vr_newtitle[4] + "]，评分[" + vr_newtitle[0] + "]，评语[" + vr_newtitle[1]
				    + ",请关联任务重新创建报告！]&nbsp;&nbsp;&nbsp;&nbsp;["
					+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, "yyyy-MM-dd HH:mm:ss"),"MM-dd HH:mm") + "]");
			sb.append("<a href=\"javascript:openUrl(''jsps/crm/customermgr/customervisit/visitRecord.jsp?formCondition=vr_idIS" + vr_id
				+ "&gridCondition=vrd_vridIS" + vr_id + "&whoami=VisitRecord'')\" style=\"font-size:14px; color:blue;\">查看业务单据</a></br>");
			baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
					+ employee.getEm_name() + "',sysdate,'" + employee.getEm_id() + "','" + sb.toString() + "','crm')");
			baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval," + pr_id + "," + vr_newtitle[2] + ",'" + vr_newtitle[3] + "')");
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		}
	}

	@Override
	@Transactional
	public void resAuditVisitRecord(int vr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		StateAssert.resAuditOnlyAudit(status);
		Object vr_isturnfeeplease = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_isturnfeeplease", "vr_id=" + vr_id);
		if(vr_isturnfeeplease !=null && vr_isturnfeeplease.toString().equals("1")){
			BaseUtil.showError("已转费用报销单，不能反审核");
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, vr_id);
		// 执行反审核操作，把审核时添加的评语和评分清空
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='ENTERING',vr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',vr_newtitle='',vr_purpose='',vr_auditor='',vr_audittime=null",
				"vr_id=" + vr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vr_id", vr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, vr_id);
	}

	@Override
	public void submitVisitRecord(int vr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, vr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='COMMITED',vr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "vr_id="
						+ vr_id);

		// 记录操作
		baseDao.logger.submit(caller, "vr_id", vr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, vr_id);

	}

	@Override
	public void resSubmitVisitRecord(int vr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, vr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='ENTERING',vr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "vr_id="
						+ vr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "vr_id", vr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, vr_id);
	}

	@Override
	public int autoSave(String caller, String vr_cuuu) {
		// 要插入主记录的ID
		int vr_id = baseDao.getSeqId("VisitRecord_SEQ");
		int id = 0;// 符合条件的最新记录的ID
		try {
			id = Integer
					.parseInt(baseDao
							.getFieldDataByCondition(
									"(select vr_id from VisitRecord where vr_cuuu='"
											+ vr_cuuu
											+ "' and vr_recorder='"
											+ SystemSession.getUser()
													.getEm_name()
											+ "' and vr_class='OfficeClerk' order by vr_visittime desc)",
									"vr_id", "rownum<=1")
							+ "");
		} catch (Exception e) {
			BaseUtil.showError("没有此客户的拜访记录，请核对后重试！");
		}
		// 先插入明细行id,关联id,一些主要字段
		String[] keys = new String[] { "pl_id", "cup_id", "pi_id", "fb_id" };

		String[] mainKeys = new String[] { "pl_vrid", "cup_vrid", "pi_vrid",
				"fb_vrid" };

		String[] tables = new String[] { "Players", "CuPlayers", "ProductInfo",
				"VisitFeedBack" };

		String[] fields = new String[] {
				"pl_detno,pl_name,pl_sex,pl_age,pl_position,pl_tel,pl_email,pl_remark",
				"cup_detno,cup_name,cup_sex,cup_age,cup_position,cup_tel,cup_email,cup_remark",
				"pi_detno,pi_vendor,pi_prodname,pi_bccode,pi_bcdescription,pi_brand,pi_model,pi_materialcode,pi_plansupply,pi_prodhq,pi_inventory,pi_cost,pi_price,pi_supply,pi_projprogress,pi_advantage,pi_week,pi_rivalbrand,pi_rivalmodel,pi_rivalprice,pi_taskid,pi_taskname",
				"fb_detno,fb_projname,fb_product,fb_cpuplatform,fb_cpumodel,fb_dosage,fb_starttime,fb_mptime,fb_output,fb_cost,fb_price,fb_salesarea,fb_client,fb_eptime,fb_dvttime,fb_sctime,fb_lctime" };
		// 执行各个明细表的插入语句
		for (int i = 0; i <= mainKeys.length - 1; i++) {
			if (baseDao.getCountByCondition(tables[i], mainKeys[i] + "=" + id) > 0) {
				String sql = "insert into " + tables[i] + " (" + fields[i]
						+ "," + keys[i] + "," + mainKeys[i] + ") select "
						+ fields[i] + "," + tables[i] + "_SEQ.nextval," + vr_id
						+ " from " + tables[i] + " where " + mainKeys[i] + "="
						+ id;
				baseDao.execute(sql);
			}
		}

		// 执行主表的插入语句，拜访时间默认为当前时间
		String code = baseDao.sGetMaxNumber("VisitRecord", 2);
		String sql = "insert into VisitRecord (vr_detail,vr_defaultorname,vr_cuid,vr_class,vr_type,vr_way,vr_cuuu,vr_cuname,vr_title,vr_visitplace,vr_recorder,vr_cucapital,vr_custaff,vr_cuclient,vr_cusetuptime,vr_cuproduct,"
				+ "vr_attach,vr_status,vr_statuscode,vr_code,vr_id,vr_recorddate,vr_visittime) select vr_detail,vr_defaultorname,vr_cuid,vr_class,vr_type,vr_way,vr_cuuu,vr_cuname,vr_title,vr_visitplace,vr_recorder,vr_cucapital,vr_custaff,vr_cuclient,vr_cusetuptime,vr_cuproduct,"
				+ "vr_attach,'在录入','ENTERING','"
				+ code
				+ "',"
				+ vr_id
				+ ","
				+ DateUtil.parseDateToOracleString(null, new Date())
				+ ","
				+ DateUtil
						.parseDateToOracleString(Constant.YMD_HMS, new Date())
				+ " from VisitRecord where vr_id=" + id;
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.save(caller, "vr_id", vr_id);
		return vr_id;
	}

	@Transactional
	@Override
	public String turnFeePlease(int vr_id, String caller) {
		Object[]  data=baseDao.getFieldsDataByCondition("VisitRecord left join employee on vr_recorder=em_name", new String[]{"em_name","em_depart","vr_code","vr_cuuu","vr_cuname","vr_turnmaster"}, "vr_id="+vr_id);//取原表单的录入人作为出差费用申请的申请人
		if(data[5]!=null && !data[5].equals("")){
			Object sob = baseDao.getFieldDataByCondition("master", "ma_user", "ma_function='"+data[5]+"'");
			if(sob == null){
				BaseUtil.showError("没有该帐套!请核对后重试!");
			}
			Object localSobName = baseDao.getFieldDataByCondition("master", "ma_function", "ma_user='"+SpObserver.getSp()+"'");
			if(localSobName==null){
				localSobName="";
			}
			Object[] feedata=baseDao.getFieldsDataByCondition(sob+"."+"FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='客户拜访记录' and fp_sourcecode='"+data[2]+"("+localSobName+")"+"'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在于"+data[5]+"的差旅费报销,单号为:" + feedata[0]);
			}
			int id=baseDao.getSeqId(sob+"."+"FeePlease_seq");
			String code=baseDao.callProcedure(sob + ".Sp_GetMaxNumber", new Object[] { "FeePlease!CLFBX", 2 });
			String insertSql="insert into "+sob +"."+"FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_billdate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_cucode,fp_cuname)" +
					" values(?,?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",SystemSession.getUser().getEm_name(),"差旅费报销单",data[2]+"("+localSobName+")","客户拜访记录",id,"ENTERING",data[3],data[4]});
			String insertDetSql="insert into "+sob +"."+"FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid) " +
					"select vrd_detno,vrd_d1,vrd_n7,vrd_n7,vrd_d3,"+sob +"."+"FeePleasedetail_seq.nextval,"+id+" from VisitRecorddetail where vrd_vrid="+vr_id;
			baseDao.execute(insertDetSql);
			baseDao.execute("update "+sob +"."+"FeePlease set fp_startdate='',fp_enddate='' where fp_id="+id);
			baseDao.updateByCondition("VisitRecord", "vr_isturnfeeplease='1'", "vr_id="+vr_id);
			String log = "转入成功,"+data[5]+"的差旅费报销单号:" + code;
			return log;
		}else{
			Object[] feedata=baseDao.getFieldsDataByCondition("FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='客户拜访记录' and fp_sourcecode='"+data[2]+"'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在差旅费报销,单号为:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
						+ feedata[1] + "&gridCondition=fpd_fpidIS" + feedata[1] + "')\">" + feedata[0] + "</a>");
			}
			int id=baseDao.getSeqId("FeePlease_seq");
			String code=baseDao.sGetMaxNumber("FeePlease!CLFBX", 2);
			String insertSql="insert into FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_billdate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_cucode,fp_cuname)" +
					" values(?,?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",SystemSession.getUser().getEm_name(),"差旅费报销单",data[2],"客户拜访记录",id,"ENTERING",data[3],data[4]});
			String insertDetSql="insert into FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid) " +
					"select vrd_detno,vrd_d1,vrd_n7,vrd_n7,vrd_d3,FeePleasedetail_seq.nextval,"+id+" from VisitRecorddetail where vrd_vrid="+vr_id;
			baseDao.execute(insertDetSql);
			baseDao.execute("update FeePlease set fp_startdate='',fp_enddate='' where fp_id="+id);
			baseDao.updateByCondition("VisitRecord", "vr_isturnfeeplease='1'", "vr_id="+vr_id);
			String log = "转入成功,差旅费报销单号:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
					+ id + "&gridCondition=fpd_fpidIS" + id + "')\">" + code + "</a>";
			return log;
		}
	}

	@Override
	public void updateGood(int vr_id, String vr_good, String caller) {
		baseDao.updateByCondition("VisitRecord", "vr_good='" + vr_good + "'",
				"vr_id=" + vr_id);
		// 记录操作
		baseDao.logger.update(caller, "vr_id", vr_id);
	}

	@Override
	public void updatePingjia(int id, String vr_newtitle, String vr_purpose,
			String caller) {
		baseDao.updateByCondition("VisitRecord", "vr_newtitle='" + vr_newtitle
				+ "',vr_purpose='" + vr_purpose + "',vr_auditor='"
				+ SystemSession.getUser().getEm_name()
				+ "',vr_audittime=sysdate", "vr_id=" + id);
		// 记录操作
		baseDao.logger.update(caller, "vr_id", id);
	}
}
