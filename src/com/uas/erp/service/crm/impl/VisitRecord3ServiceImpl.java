package com.uas.erp.service.crm.impl;

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
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.VisitRecord3Service;

@Service
public class VisitRecord3ServiceImpl implements VisitRecord3Service {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveVisitRecord(String formStore, String[] gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VisitRecord", "vr_code='"
				+ store.get("vr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("VisitRecord!Vender", "save", "before",
				new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByMap(store, "VisitRecord");
		baseDao.execute(formSql);

		// 保存Players
		List<Map<Object, Object>> grid1 = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		for (Map<Object, Object> map : grid1) {
			map.put("pl_id", baseDao.getSeqId("Players_SEQ"));
		}
		List<String> gridSql1 = SqlUtil.getInsertSqlbyGridStore(grid1,
				"Players");
		baseDao.execute(gridSql1);

		// 保存CuPlayers
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		for (Map<Object, Object> map : grid2) {
			map.put("cup_id", baseDao.getSeqId("CuPlayers_SEQ"));
			int count = baseDao.getCountByCondition("venderCONTACT",
					"vt_vecode='" + store.get("vr_cuuu") + "' and vt_name='"
							+ map.get("cup_name") + "'");
			if (count < 1) {// 如果在原厂联系人表中不存在此联系人，则添加进去
				int ct_id = baseDao.getSeqId("venderCONTACT_seq");
				String contactSql = "insert into venderCONTACT(vt_id,vt_name,vt_age,vt_sex,vt_job,vt_mobile,vt_officeemail,vt_contactmark,vt_vecode,vt_vename) values ("
						+ ct_id
						+ ",'"
						+ map.get("cup_name")
						+ "','"
						+ map.get("cup_age")
						+ "','"
						+ map.get("cup_sex")
						+ "','"
						+ map.get("cup_position")
						+ "','"
						+ map.get("cup_tel")
						+ "','"
						+ map.get("cup_email")
						+ "','"
						+ map.get("cup_remark")
						+ "','"
						+ store.get("vr_cuuu")
						+ "','"
						+ store.get("vr_cuname")
						+ "')";
				baseDao.execute(contactSql);
			}
		}
		List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2,
				"CuPlayers");
		baseDao.execute(gridSql2);

		// 保存VenderMaketing
		List<Map<Object, Object>> grid3 = BaseUtil
				.parseGridStoreToMaps(gridStore[3]);
		for (Map<Object, Object> map : grid3) {
			map.put("vm_id", baseDao.getSeqId("VenderMaketing_SEQ"));
		}
		List<String> gridSql3 = SqlUtil.getInsertSqlbyGridStore(grid3,
				"VenderMaketing");
		baseDao.execute(gridSql3);

		// 保存Price
		List<Map<Object, Object>> grid4 = BaseUtil
				.parseGridStoreToMaps(gridStore[4]);
		for (Map<Object, Object> map : grid4) {
			map.put("pr_id", baseDao.getSeqId("Price_SEQ"));
		}
		List<String> gridSql4 = SqlUtil.getInsertSqlbyGridStore(grid4, "Price");
		baseDao.execute(gridSql4);

		// 保存VisitRecordDetail
		List<Map<Object, Object>> grid0 = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		for (Map<Object, Object> map : grid0) {
			map.put("vrd_id", baseDao.getSeqId("VisitRecordDetail_SEQ"));
		}
		List<String> gridSql0 = SqlUtil.getInsertSqlbyGridStore(grid0,
				"VisitRecordDetail");
		baseDao.execute(gridSql0);

		try {
			// 记录操作
			baseDao.logger.save(caller, "vr_id", store.get("vr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler("VisitRecord!Vender", "save", "after",
				new Object[] { store });
		
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
				baseDao.setReader("VisitRecord!Vender", vr_id, vr_groupid);	
			}else{
				String vr_groupid="employee#"+vremid;
				baseDao.setReader("VisitRecord!Vender", vr_id, vr_groupid);	
			}
		}
		

	}

	@Override
	@Transactional
	public void updateVisitRecordById(String formStore, String[] gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + store.get("vr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VisitRecord",
				"vr_id");
		baseDao.execute(formSql);

		// 修改Players
		List<Map<Object, Object>> grid1 = BaseUtil
				.parseGridStoreToMaps(gridStore[1]);
		List<String> gridSql1 = null;
		if (grid1.size() > 0) {
			gridSql1 = SqlUtil.getUpdateSqlbyGridStore(grid1, "Players",
					"pl_id");
			for (Map<Object, Object> s : grid1) {
				Object aid = s.get("pl_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("PLAYERS_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Players",
							new String[] { "pl_id" }, new Object[] { id });
					gridSql1.add(sql);
				}
			}
			baseDao.execute(gridSql1);
		}

		// 修改CuPlayers
		List<Map<Object, Object>> grid2 = BaseUtil
				.parseGridStoreToMaps(gridStore[2]);
		List<String> gridSql2 = null;
		if (grid2.size() > 0) {
			gridSql2 = SqlUtil.getUpdateSqlbyGridStore(grid2, "CuPlayers",
					"cup_id");
			for (Map<Object, Object> s : grid2) {
				int count = baseDao.getCountByCondition("venderCONTACT",
						"vt_vecode='" + store.get("vr_cuuu")
								+ "' and vt_name='" + s.get("cup_name") + "'");
				if (count < 1) {// 如果在原厂联系人表中不存在此联系人，则添加进去
					int ct_id = baseDao.getSeqId("venderCONTACT_seq");
					String contactSql = "insert into venderCONTACT(vt_id,vt_name,vt_age,vt_sex,vt_job,vt_mobile,vt_officeemail,vt_contactmark,vt_vecode,vt_vename) values ("
							+ ct_id
							+ ",'"
							+ s.get("cup_name")
							+ "','"
							+ s.get("cup_age")
							+ "','"
							+ s.get("cup_sex")
							+ "','"
							+ s.get("cup_position")
							+ "','"
							+ s.get("cup_tel")
							+ "','"
							+ s.get("cup_email")
							+ "','"
							+ s.get("cup_remark")
							+ "','"
							+ store.get("vr_cuuu")
							+ "','"
							+ store.get("vr_cuname") + "')";
					baseDao.execute(contactSql);
				}
				Object aid = s.get("cup_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("CuPlayers_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "CuPlayers",
							new String[] { "cup_id" }, new Object[] { id });
					gridSql2.add(sql);
				}
			}
			baseDao.execute(gridSql2);
		}

		// 修改VenderMaketing
		List<Map<Object, Object>> grid3 = BaseUtil
				.parseGridStoreToMaps(gridStore[3]);
		List<String> gridSql3 = null;
		if (grid3.size() > 0) {
			gridSql3 = SqlUtil.getUpdateSqlbyGridStore(grid3, "VenderMaketing",
					"vm_id");
			for (Map<Object, Object> s : grid3) {
				Object aid = s.get("vm_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VenderMaketing_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "VenderMaketing",
							new String[] { "vm_id" }, new Object[] { id });
					gridSql3.add(sql);
				}
			}
			baseDao.execute(gridSql3);
		}

		// 修改Price
		List<Map<Object, Object>> grid4 = BaseUtil
				.parseGridStoreToMaps(gridStore[4]);
		List<String> gridSql4 = null;
		if (grid4.size() > 0) {
			gridSql4 = SqlUtil.getUpdateSqlbyGridStore(grid4, "Price", "pr_id");
			for (Map<Object, Object> s : grid4) {
				Object aid = s.get("pr_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("Price_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "Price",
							new String[] { "pr_id" }, new Object[] { id });
					gridSql4.add(sql);
				}
			}
			baseDao.execute(gridSql4);
		}

		// 修改VisitRecordDetail
		List<Map<Object, Object>> grid0 = BaseUtil
				.parseGridStoreToMaps(gridStore[0]);
		List<String> gridSql0 = null;
		if (grid0.size() > 0) {
			gridSql0 = SqlUtil.getUpdateSqlbyGridStore(grid0,
					"VisitRecordDetail", "vrd_id");
			for (Map<Object, Object> s : grid0) {
				Object aid = s.get("vrd_id");
				if (aid == null || "".equals(aid.toString())
						|| Integer.parseInt(aid.toString()) == 0) {
					int id = baseDao.getSeqId("VisitRecordDetail_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s,
							"VisitRecordDetail", new String[] { "vrd_id" },
							new Object[] { id });
					gridSql0.add(sql);
				}
			}
			baseDao.execute(gridSql0);
		}
		// 记录操作
		baseDao.logger.update(caller, "vr_id", store.get("vr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("VisitRecord!Vender", "save", "after",
				new Object[] { store });
		
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
				baseDao.setReader("VisitRecord!Vender", vr_id, vr_groupid);	
			}else{
				String vr_groupid="employee#"+vremid;
				baseDao.setReader("VisitRecord!Vender", vr_id, vr_groupid);	
			}
		}
	}

	@Override
	@Transactional
	public void deleteVisitRecord(int vr_id, String caller) {
		// 删除主表
		baseDao.deleteById("VisitRecord", "vr_id", vr_id);
		// 删除CuPlayers
		baseDao.deleteById("CuPlayers", "cup_vrid", vr_id);
		// 删除Players
		baseDao.deleteById("Players", "pl_vrid", vr_id);
		// 删除VenderMaketing
		baseDao.deleteById("VenderMaketing", "vm_vrid", vr_id);
		// 删除Price
		baseDao.deleteById("Price", "pr_vrid", vr_id);
		// 删除VisitRecordDetail
		baseDao.deleteById("VisitRecordDetail", "vrd_vrid", vr_id);
		// 记录操作
		baseDao.logger.delete(caller, "vr_id", vr_id);
		// 执行删除后的其它逻辑
		handlerService.handler("VisitRecord!Vender", "delete", "after",
				new Object[] { vr_id });
	}

	@Override
	public void auditVisitRecord(int vr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, vr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='AUDITED',vr_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',vr_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',vr_auditdate=sysdate", "vr_id=" + vr_id);
		// 记录操作
		baseDao.logger.audit(caller, "vr_id", vr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, vr_id);
	}

	@Override
	public void resAuditVisitRecord(int vr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		StateAssert.resAuditOnlyAudit(status);
		Object vr_isturnfeeplease = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_isturnfeeplease", "vr_id=" + vr_id);
		if(vr_isturnfeeplease.toString().equals("1")){
			BaseUtil.showError("已转费用报销单，不能反审核");
		}
		handlerService.beforeResAudit(caller, vr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='ENTERING',vr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',vr_auditer='',vr_auditdate=null", "vr_id=" + vr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vr_id", vr_id);
		handlerService.afterResAudit(caller, vr_id);
	}

	@Override
	public void submitVisitRecord(int vr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VisitRecord",
				"vr_statuscode", "vr_id=" + vr_id);
		StateAssert.submitOnlyEntering(status);
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
		handlerService.beforeResSubmit(caller, vr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"VisitRecord",
				"vr_statuscode='ENTERING',vr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "vr_id="
						+ vr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "vr_id", vr_id);
		handlerService.afterResSubmit(caller, vr_id);
	}

	@Override
	@Transactional
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
											+ "' and vr_class='VisitRecord!Vender' order by vr_visittime desc)",
									"vr_id", "rownum<=1")
							+ "");
		} catch (Exception e) {
			BaseUtil.showError("没有此客户的拜访记录，请核对后重试！");
		}

		String[] keys = new String[] { "pl_id", "cup_id", "vm_id", "pr_id" };
		String[] mainKeys = new String[] { "pl_vrid", "cup_vrid", "vm_vrid",
				"pr_vrid" };
		String[] tables = new String[] { "Players", "CuPlayers",
				"VenderMaketing", "Price" };
		String[] fields = new String[] {
				"pl_detno,pl_name,pl_sex,pl_age,pl_position,pl_tel,pl_email,pl_remark",
				"cup_detno,cup_name,cup_sex,cup_age,cup_position,cup_tel,cup_email,cup_remark",
				"vm_detno,vm_model,vm_rival,vm_marketshare,vm_marketdw,vm_focusgroup,vm_advantage,vm_weak",
				"pr_detno,pr_model,pr_deliverytime,pr_originalprice,pr_originaldate,pr_goalprice,pr_price,pr_date,pr_originalpayment,pr_goalpayment,pr_longtime" };
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
		String sql = "insert into VisitRecord (vr_class,vr_type,vr_way,vr_cuuu,vr_cuname,vr_title,vr_visitplace,vr_recorder,vr_cucapital,vr_custaff,vr_cuclient,vr_cusetuptime,vr_cuproduct,"
				+ "vr_attach,vr_status,vr_statuscode,vr_code,vr_id,vr_recorddate,vr_visittime) select vr_class,vr_type,vr_way,vr_cuuu,vr_cuname,vr_title,vr_visitplace,vr_recorder,vr_cucapital,vr_custaff,vr_cuclient,vr_cusetuptime,vr_cuproduct,"
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
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.save"), BaseUtil
						.getLocalMessage("msg.saveSuccess"),
				"VisitRecord|vr_id=" + vr_id));
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
			Object[] feedata=baseDao.getFieldsDataByCondition(sob+"."+"FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='原厂拜访记录' and fp_sourcecode='"+data[2]+"("+localSobName+")'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在于"+data[5]+"的差旅费报销,单号为:" + feedata[0]);
			}
			int id=baseDao.getSeqId(sob+"."+"FeePlease_seq");
			String code=baseDao.callProcedure(sob + ".Sp_GetMaxNumber", new Object[] { "FeePlease!CLFBX", 2 });
			String insertSql="insert into "+sob+"."+"FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_billdate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_vendcode,fp_vendname)" +
					" values(?,?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",SystemSession.getUser().getEm_name(),"差旅费报销单",data[2]+"("+localSobName+")","原厂拜访记录",id,"ENTERING",data[3],data[4]});
			String insertDetSql="insert into "+sob+"."+"FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid) " +
					"select vrd_detno,vrd_d1,vrd_n7,vrd_n7,vrd_d3,"+sob+"."+"FeePleasedetail_seq.nextval,"+id+" from VisitRecorddetail where vrd_vrid="+vr_id;
			baseDao.execute(insertDetSql);
			baseDao.execute("update "+sob+"."+"FeePlease set fp_startdate='',fp_enddate='' where fp_id="+id);
			baseDao.updateByCondition("VisitRecord", "vr_isturnfeeplease='1'", "vr_id="+vr_id);
			String log = "转入成功,"+data[5]+"的差旅费报销单号:" + code;
			return log;
		}else{
			Object[] feedata=baseDao.getFieldsDataByCondition("FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='原厂拜访记录' and fp_sourcecode='"+data[2]+"'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在差旅费报销,单号为:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
						+ feedata[1] + "&gridCondition=fpd_fpidIS" + feedata[1] + "')\">" + feedata[0] + "</a>");
			}
			int id=baseDao.getSeqId("FeePlease_seq");
			String code=baseDao.sGetMaxNumber("FeePlease!CLFBX", 2);
			String insertSql="insert into FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_billdate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_vendcode,fp_vendname)" +
					" values(?,?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",SystemSession.getUser().getEm_name(),"差旅费报销单",data[2],"原厂拜访记录",id,"ENTERING",data[3],data[4]});
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

}
