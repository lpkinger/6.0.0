package com.uas.erp.service.pm.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.ECNService;

@Service("ECNService")
public class ECNServiceImpl implements ECNService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveECN(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		store.put("ecn_didstatus", BaseUtil.getLocalMessage("OPEN"));
		store.put("ecn_didstatuscode", "OPEN");
		boolean bool = baseDao.checkByCondition("ECN", "ecn_code='" + store.get("ecn_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave("ECN", new Object[] { store, gstore }); // 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECN", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存ECNDetail
		Object[] ed_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ed_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				ed_id[i] = baseDao.getSeqId("ECNDETAIL_SEQ");
			}
		} else {
			ed_id[0] = baseDao.getSeqId("ECNDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ECNDetail", "ed_id", ed_id);
		baseDao.execute(gridSql);
		Object ecn_id = store.get("ecn_id");
		// 更新明细行编号
		baseDao.execute("update ECNDetail set ed_code='" + store.get("ecn_code") + "',ed_didstatus='"
				+ BaseUtil.getLocalMessage("OPEN")
				+ "',ed_didstatuscode='OPEN',ed_location=replace(replace(ed_location,'，',','),' ',''),ed_oldlocation=replace(replace(ed_oldlocation,'，',','),' ','') where ed_ecnid="
				+ ecn_id);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ecn_id", ecn_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ECN保存、更新，如果替换的行新用量0，默认等于原用量
		baseDao.execute(
				"update ECNDetail set ed_newbaseqty=ed_oldbaseqty where ed_ecnid=? and ed_type in('替换','SWITCH') and nvl(ed_newbaseqty,0)=0 and ed_oldbaseqty is not null",
				ecn_id);
		// 更新明细字段是否升级BOM，如果主表填写的是"是"，则明细表全部更新为是
		baseDao.execute(
				"update ecndetail set ed_upbomversion=-1 where ed_ecnid=? and exists(select 1 from ecn where ecn_id=? and nvl(ecn_upbomversion,0)<>0)",
				ecn_id, ecn_id);
		//@add 20180117如果是禁用类型，自动将BOM明细中的位号写入到禁用位号字段中去
		baseDao.execute("update ecndetail set ed_oldlocation=(select bd_location from bomdetail where bd_bomid=ed_boid and ed_bddetno=bd_detno) where ed_ecnid=? and ed_type in('禁用','DISABLE')",ecn_id);
		
		// 根据参数配置ifDCNChangePR， 是否启用BOM阶段等于DCN的，单据前缀设为DN
		bool = baseDao.isDBSetting(caller, "ifDCNChangePR");
		if (bool) {
			Object[] data = baseDao.getFieldsDataByCondition("ecn", "ecn_prodstage,ecn_code",
					"ecn_id=" + store.get("ecn_id"));
			if (data[0] != null && "DCN".equals(data[0])) {
				Object ob = baseDao.getFieldDataByCondition("MAXNUMBERS", "MN_LEADCODE", "mn_tablename='ECN'");
				if (ob != null) {
					baseDao.updateByCondition("ECN",
							"ecn_code='" + data[1].toString().replaceAll(ob.toString(), "DN") + "'",
							"ecn_id='" + store.get("ecn_id") + "'");
					baseDao.updateByCondition("ECNDETAIL",
							"ed_code='" + data[1].toString().replaceAll(ob.toString(), "DN") + "'",
							"ed_ecnid='" + store.get("ecn_id") + "'");
				} else {
					baseDao.updateByCondition("ecn", "ecn_code='DN'||'" + data[1].toString() + "'",
							"ecn_id='" + store.get("ecn_id") + "'");
					baseDao.updateByCondition("ECNDETAIL", "ed_code='DN'||'" + data[1].toString() + "'",
							"ed_ecnid='" + store.get("ecn_id") + "'");
				}
			}
		}
		//@编号 2018030078 
		baseDao.execute("update ecndetail A set A.ed_oldbdremark2 = (select bd_remark2  from ecndetail B"
                        +" left join bom on B.ed_boid = bo_id left join bomdetail on bo_id = bd_bomid and B.ed_bddetno = bd_detno"
                        +" where A.ed_id = B.ed_id )"
                        +" where A.ed_ecnid =?",ecn_id);
		baseDao.execute("update ecndetail A set A.ed_newbdremark2 = (select bd_remark2  from ecndetail B"
                +" left join bom on B.ed_boid = bo_id left join bomdetail on bo_id = bd_bomid and B.ed_bddetno = bd_detno"
                +" where A.ed_id = B.ed_id )"
                +" where A.ed_ecnid =? and A.ed_newbdremark2 is null",ecn_id);
		// 执行保存后的其它逻辑
		handlerService.afterSave("ECN", new Object[] { store, gstore });
	}

	@Override
	public void deleteECN(int ecn_id, String caller) {
		// 只能删除在录入的ECN
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.delOnlyEntering(status);
		// 如果有来源ECR单号ecn_ecrcode ，则更新update ecr set ecr_turnstatus=null where
		// ecr_code=?
		Object ecn_ecrcode = baseDao.getFieldDataByCondition("ECN", "ecn_ecrcode", "ecn_id=" + ecn_id);
		if (ecn_ecrcode != null && !"".equals(ecn_ecrcode)) {
			baseDao.execute("update ecr set ecr_turnstatus=null where ecr_code=?", ecn_ecrcode);
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel("ECN", new Object[] { ecn_id }); // 删除ECN
		baseDao.deleteById("ECN", "ecn_id", ecn_id);
		// 删除ECNdetail
		baseDao.deleteById("ECNdetail", "ed_ecnid", ecn_id);
		// 删除位号表
		baseDao.deleteById("ECNdetailLocation", "edl_ecnid", ecn_id);
		// 记录操作
		baseDao.logger.delete(caller, "ecn_id", ecn_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("ECN", new Object[] { ecn_id });
	}

	@Override
	public void updateECNById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的ECN!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + store.get("ecn_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave("ECN", new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECN", "ecn_id");
		baseDao.execute(formSql);
		// 修改ECNDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECNDetail", "ed_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ed_id") == null || s.get("ed_id").equals("") || s.get("ed_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ECNDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ECNDetail", new String[] { "ed_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object ecn_id = store.get("ecn_id");
		// 更新明细行编号
		baseDao.execute("update ECNDetail set ed_code='" + store.get("ecn_code") + "',ed_didstatus='"
				+ BaseUtil.getLocalMessage("OPEN")
				+ "',ed_didstatuscode='OPEN',ed_location=replace(replace(ed_location,'，',','),' ',''),ed_oldlocation=replace(replace(ed_oldlocation,'，',','),' ','')  where ed_ecnid="
				+ ecn_id);
		// ECN保存、更新，如果替换的行新用量0，默认等于原用量
		baseDao.execute(
				"update ECNDetail set ed_newbaseqty=ed_oldbaseqty where ed_ecnid=?"
						+ " and ed_type in('替换','SWITCH') and nvl(ed_newbaseqty,0)=0 and ed_oldbaseqty is not null",
				ecn_id);
		// 更新明细字段是否升级BOM，如果主表填写的是"是"，则明细表全部更新为是
		baseDao.execute(
				"update ecndetail set ed_upbomversion=-1 where ed_ecnid=? and exists(select 1 from ecn where ecn_id=? and nvl(ecn_upbomversion,0)<>0)",
				ecn_id, ecn_id);
		//@add 20180117如果是禁用类型，自动将BOM明细中的位号写入到禁用位号字段中去
		baseDao.execute("update ecndetail set ed_oldlocation=(select bd_location from bomdetail where bd_bomid=ed_boid and ed_bddetno=bd_detno) where ed_ecnid=? and ed_type in('禁用','DISABLE')",ecn_id);
		//@编号 2018030078 
		baseDao.execute("update ecndetail A set A.ed_oldbdremark2 = (select bd_remark2  from ecndetail B"
                        +" left join bom on B.ed_boid = bo_id left join bomdetail on bo_id = bd_bomid and B.ed_bddetno = bd_detno"
                        +" where A.ed_id = B.ed_id )"
                        +" where A.ed_ecnid =?",ecn_id);
		baseDao.execute("update ecndetail A set A.ed_newbdremark2 = (select bd_remark2  from ecndetail B"
                +" left join bom on B.ed_boid = bo_id left join bomdetail on bo_id = bd_bomid and B.ed_bddetno = bd_detno"
                +" where A.ed_id = B.ed_id )"
                +" where A.ed_ecnid =? and A.ed_newbdremark2 is null",ecn_id);		
		// 记录操作
		baseDao.logger.update(caller, "ecn_id", ecn_id);
		// 执行修改后的其它逻辑
		handlerService.afterSave("ECN", new Object[] { store, gstore });
	}

	@Override
	public void auditECN(int ecn_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] datas = baseDao.getFieldsDataByCondition("ECN", "ecn_checkstatuscode,ecn_type,ecn_code",
				"ecn_id=" + ecn_id);
		StateAssert.auditOnlyCommited(datas[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("ECN", new Object[] { ecn_id }); // 执行审核操作
		baseDao.audit("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus", "ecn_checkstatuscode", "ecn_auditdate",
				"ecn_auditman");
		Object type = datas[1];
		// 执行立即变更存储过程
		if (type.equals("NOW")) {
			String str = baseDao.callProcedure("SP_DOECN_NOW", new String[] { datas[2].toString() });
			if (str != null && !str.trim().equals("")) {
				BaseUtil.showError(str);
			}
			if (baseDao.isDBSetting(caller, "createBomStepChange")) {
				str = baseDao.callProcedure("SP_DOECN_TOSTEPCHANGE", new String[] { datas[2].toString() });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}
		} else {
			baseDao.execute("update bomdetail set bd_ecncode='待" + datas[2].toString()
					+ "' where (bd_bomid,bd_detno) in (select ed_boid,ed_bddetno from ecndetail where  ed_ecnid="
					+ ecn_id + ")");
		}
		// 记录操作
		baseDao.logger.audit(caller, "ecn_id", ecn_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("ECN", new Object[] { ecn_id });
	}

	@Override
	public void resAuditECN(int ecn_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.resAuditOnlyAudit(status);
		int didcount = baseDao
				.getCount("select * from ECNDetail where  ed_didstatuscode='EXECUTED' and ed_ecnid=" + ecn_id);
		if (didcount > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyUnexecuted"));
		}
		// 执行反审核操作
		baseDao.updateByCondition("ECN",
				"ecn_checkstatuscode='ENTERING',ecn_checkstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',ecn_didstatus='" + BaseUtil.getLocalMessage("OPEN") + "',ecn_didstatuscode='OPEN' ",
				"ecn_id=" + ecn_id);
		// 执行失败的明细设为打开
		baseDao.updateByCondition("ECNDetail",
				"ed_didstatuscode='OPEN',ed_didstatus='" + BaseUtil.getLocalMessage("OPEN") + "'",
				" ed_didstatuscode='FAIL' and ed_ecnid=" + ecn_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ecn_id", ecn_id);
	}

	@Override
	public void submitECN(int ecn_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		String ecn_type = "", ecn_code = "";
		Object obs[] = baseDao.getFieldsDataByCondition("ECN",
				new String[] { "ecn_checkstatuscode", "ecn_type", "ecn_code" }, "ecn_id=" + ecn_id);
		if (obs != null) {
			StateAssert.submitOnlyEntering(obs[0]);
			ecn_type = String.valueOf(obs[1]);
			ecn_code = String.valueOf(obs[2]);
		} else {
			BaseUtil.showError("单据不存在或者已删除！");
		}
		UpdateECNTypeCode(ecn_id, caller);
		// 更新ed_stepcode为bd_stepcode
		baseDao.execute(
				"update ecndetail set ed_stepcode=(select bd_stepcode from bomdetail where bd_bomid=ed_boid and bd_detno=ed_bddetno) where ed_ecnid=? and exists (select 1 from bomdetail where bd_bomid=ed_boid and bd_detno=ed_bddetno and nvl(bd_stepcode,' ')<> NVL(ed_stepcode,' ')) and nvl(ed_didstatuscode,' ')<>'CLOSE'",
				ecn_id);
		
		//更新ECNDETAIL中BOM 变更前的bom 版本号
		baseDao.execute("update ecndetail set ed_oldbomversion=(select bo_version from bom where bo_id=ed_boid) "
				+" where ed_ecnid=? and exists(select 1 from bom where bo_id=ed_boid and nvl(bo_version,' ')<>' ' and nvl(bo_version,' ')<>nvl(ed_oldbomversion,' ')) and nvl(ed_didstatuscode,' ')<>'CLOSE'",ecn_id);
		
		// 检测合法性
		CheckECN_Commit_before_ALLcheck(ecn_id, caller);
		SqlRowList rs;
		rs = baseDao.queryForRowSet(
				"select count(1)cn,wm_concat('行号['||T.ed_detno||']已经存在未执行的ECN['||B.ecn_code||']序号:'||A.ed_detno||'!<br>')error from "
						+ " (select ed_detno,ed_boid,ed_soncode,ed_type,ed_repcode from "
						+ " ecn left join ecndetail on ecn_id=ed_ecnid where ed_ecnid=?) T left join ecndetail A on A.ed_boid=T.ed_boid "
						+ " and A.ED_ECNID<>? AND NVL(A.ed_didstatus,' ') in ('打开',' ') LEFT JOIN ECN B ON B.ecn_id=A.ed_ecnid "
						+ " WHERE (B.ecn_checkstatuscode='COMMITED' and B.ecn_type='NOW' or (B.ecn_checkstatuscode in('AUDITED','COMMITED')and B.ecn_type='AUTO')) "
						+ " AND((T.ed_type='SWITCH' AND (A.ed_soncode=T.ed_repcode or (A.ed_type='SWITCH' AND A.ed_repcode=T.ed_repcode))) OR (T.ed_type<>'SWITCH' AND (A.ed_soncode=T.ed_soncode or (A.ed_type='SWITCH' AND A.ed_repcode=T.ed_soncode)))) "
						+ " and rownum<30",
				ecn_id, ecn_id);
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError(rs.getString("error"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("ECN", new Object[] { ecn_id }); // 执行提交操作
		baseDao.updateByCondition("ECN",
				"ecn_checkstatuscode='COMMITED',ecn_checkstatus='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ecn_id=" + ecn_id);
		// 记录操作
		baseDao.logger.submit(caller, "ecn_id", ecn_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("ECN", new Object[] { ecn_id });
		if (ecn_type.equals("NOW")) {// 立即变更
			// 根据配置表的设置需要走制造ECN审批的，在提交后产生制造ECN
			Boolean IfAuditMakeECN = baseDao.isDBSetting(caller, "IfAuditMakeECN");
			if (IfAuditMakeECN) {
				String str = baseDao.callProcedure("SP_DOECN_TOMAKEMATERIAL", new String[] { ecn_code });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError("制造ECN产生失败!" + str);
				}
			}
		}
	}

	@Override
	public void resSubmitECN(int ecn_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 增加限制判断是否有产生过制造ECN，判断状态 如果是在录入删除该制造ecn，
		Object code = baseDao.getFieldDataByCondition("ecn left join MakeMaterialChange on ecn_code=mc_ecncode",
				"mc_code", "ecn_id=" + ecn_id + " and mc_statuscode<>'ENTERING'");
		if (code != null) {// 如果不是在录入，不允许反提交ECN。
			BaseUtil.showError("不允许反提交，存在关联的制造ECN：" + code);
		}
		handlerService.beforeResSubmit("ECN", new Object[] { ecn_id }); // 执行反提交操作
		baseDao.updateByCondition("ECN",
				"ecn_checkstatuscode='ENTERING',ecn_checkstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ecn_id=" + ecn_id);
		// 删除在录入关联的制造ECN
		baseDao.execute(
				"delete from MakeMaterialChange where mc_id=(select MMC.mc_id from ecn left join MakeMaterialChange MMC on ecn_code=MMC.mc_ecncode where ecn_id="
						+ ecn_id + " and MMC.mc_statuscode='ENTERING')");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ecn_id", ecn_id);
		handlerService.afterResSubmit("ECN", new Object[] { ecn_id });
		Object[] obj;
		obj = baseDao.getFieldsDataByCondition("ECN", "ecn_type,ecn_code", "ecn_id=" + ecn_id);
		if (obj[0].toString().equals("NOW")) {// 立即变更
			// 根据配置表的设置需要走制造ECN审批的，反提交要删除之前产生的制造ECN
			Boolean IfAuditMakeECN = baseDao.isDBSetting("IfAuditMakeECN");
			if (IfAuditMakeECN) {
				baseDao.execute(
						"delete from makematerialchangedet where md_mcid in (select mc_id from makematerialchange where mc_ecncode='"
								+ obj[1] + "' and mc_statuscode<>'AUDITED' ) ");
				baseDao.execute("delete from makematerialchange where mc_ecncode='" + obj[1]
						+ "'  and mc_statuscode<>'AUDITED' ");
			}
		}
	}

	@Override
	public String[] printECN(int ecn_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("ECN", new Object[] { ecn_id }); // 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("ECN",
				"ecn_printstatuscode='PRINTED',ecn_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"ecn_id=" + ecn_id);
		// 记录操作
		baseDao.logger.print(caller, "ecn_id", ecn_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint("ECN", new Object[] { ecn_id });
		return keys;
	}

	public void UpdateECNTypeCode(int ecn_id, String caller) {
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'增加','ADD') where ed_ecnid='" + ecn_id
				+ "' and ed_type='增加'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'修改','UPDATE') where ed_ecnid='" + ecn_id
				+ "' and ed_type='修改'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'禁用','DISABLE') where ed_ecnid='" + ecn_id
				+ "' and ed_type='禁用'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'替换','SWITCH') where ed_ecnid='" + ecn_id
				+ "' and ed_type='替换'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'增加替代料','REPADD') where ed_ecnid='" + ecn_id
				+ "' and ed_type='增加替代料'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'禁用替代料','REPDISABLE') where ed_ecnid='" + ecn_id
				+ "' and ed_type='禁用替代料'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'增加通用替代料','ADDCOMMONREP') where ed_ecnid='"
				+ ecn_id + "' and ed_type='增加通用替代料'");
		baseDao.execute("update ECNDetail set ed_type=replace(ed_type,'禁用通用替代料','DISABLECOMMONREP') where ed_ecnid='"
				+ ecn_id + "' and ed_type='禁用通用替代料'");
	}

	/**
	 * 关闭ECN明细行
	 */
	@Override
	public void closeECNDetail(int ed_id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		int ecn_id = 0;
		SQLStr = "SELECT ed_id,ed_didstatus,ed_didstatuscode,ed_ecnid,ecn_code,ed_detno,ecn_type,ed_type,ed_boid,ed_bddetno from ecn,ecndetail where ecn_id=ed_ecnid and ed_id="
				+ ed_id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			ecn_id = rs.getInt("ed_ecnid");
			if (rs.getObject("ed_didstatuscode") != null && !rs.getObject("ed_didstatuscode").equals("OPEN")) {
				BaseUtil.showError("只能关闭当前状态【打开】的明细行");
			}
			baseDao.updateByCondition("ECNDetail",
					"ed_didstatuscode='CLOSE',ed_didstatus='" + BaseUtil.getLocalMessage("CLOSE") + "'",
					"ed_id=" + ed_id);
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
					"关闭ECN单号:" + rs.getString("ecn_code") + "行号:" + rs.getInt("ed_detno"), "明细行关闭成功",
					"ECN|ecn_id=" + ecn_id));
			if (rs.getString("ecn_type").equals("AUTO") && rs.getString("ed_type").equals("SWITCH")) {
				baseDao.updateByCondition("BOMDETAIL", "bd_ecncode=null",
						"bd_bomid=" + rs.getInt("ed_boid") + " and bd_detno=" + rs.getInt("ed_bddetno")
								+ " and bd_ecncode='待" + rs.getString("ecn_code") + "'");
			}
			int cn = baseDao.getCount("select count(1) from ecn left join ecnDetail on ed_ecnid=ecn_id where ecn_id="
					+ rs.getInt("ed_ecnid") + " and ed_didstatuscode='OPEN'");
			if (cn == 0) {
				// 如果所有明细行都已关闭更新ecn_didstatuscode='CLOSE'
				baseDao.updateByCondition("ECN", "ecn_didstatuscode='CLOSE',ecn_didstatus='关闭'", "ecn_id=" + ecn_id);
			}
		}
	}

	/**
	 * 打开ECN明细行
	 */
	@Override
	public void openECNDetail(int ed_id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		int ecn_id = 0;
		SQLStr = "SELECT ed_id,ed_didstatus,ed_didstatuscode,ed_ecnid,ecn_code,ed_detno,ecn_type,ed_type,ed_boid,ed_bddetno from ecn,ecndetail where ecn_id=ed_ecnid and ed_id="
				+ ed_id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			ecn_id = rs.getInt("ed_ecnid");
			if (rs.getObject("ed_didstatuscode") != null && !rs.getObject("ed_didstatuscode").equals("CLOSE")) {
				BaseUtil.showError("只能打开当前状态【关闭】的明细行");
			}
			baseDao.updateByCondition("ECNDetail",
					"ed_didstatuscode='OPEN',ed_didstatus='" + BaseUtil.getLocalMessage("OPEN") + "'",
					"ed_id=" + ed_id);
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
					"打开ECN单号:" + rs.getString("ecn_code") + "行号:" + rs.getInt("ed_detno"), "明细行打开成功",
					"ECN|ecn_id=" + ecn_id));
			if (rs.getString("ecn_type").equals("AUTO") && rs.getString("ed_type").equals("SWITCH")) {
				baseDao.updateByCondition("BOMDETAIL", "bd_ecncode='待" + rs.getString("ecn_code") + "'",
						"bd_bomid=" + rs.getInt("ed_boid") + " and bd_detno=" + rs.getInt("ed_bddetno") + " ");
			}
			int cn = baseDao.getCount("select count(1) from ecn left join ecnDetail on ed_ecnid=ecn_id where ecn_id="
					+ rs.getInt("ed_ecnid") + " and ed_didstatuscode='CLOSE'");
			if (cn == 0) {
				// 如果所有明细行都已打开更新ecn_didstatuscode='OPEN'
				baseDao.updateByCondition("ECN", "ecn_didstatuscode='OPEN',ecn_didstatus='打开'", " ecn_id=" + ecn_id);
			}
		}
	}

	public void CheckECN_Commit_before_ALLcheck(Integer ecn_id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select  1 from ecn  where ecn_id=? and ecn_type not in ('AUTO','NOW') ";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			BaseUtil.showError("变更类型不能为空，必须是[自然变更]或者[立即变更]!'");
		}
		SQLStr = "select count(1)c,wm_concat(ed_detno)detno from  ecndetail left join ecn on ed_ecnid=ecn_id left join "
				+ "bom on ed_soncode=bo_mothercode left join bomlevel on bo_level=bl_code left join product on pr_code=bo_mothercode where ed_ecnid=? and ecn_type='AUTO' and bl_ifmrp<>0 and (bo_ispast=-1 OR pr_supplytype='VIRTUAL') and nvl(ed_didstatuscode,' ')<>'CLOSE' and rownum<20";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next() && rs.getInt("c") > 0) {
			BaseUtil.showError("序号[" + rs.getString("detno") + "]是虚拟子件BOM或跳层BOM且参与MRP运算，不允许执行自然变更!");
		}
		// 根据配置表的设置标准BOM是否允许直接新增ECN变更
		Boolean allowMrpBomWithoutECR = baseDao.isDBSetting(caller, "allowMrpBomWithoutECR");
		if (!allowMrpBomWithoutECR) {
			SQLStr = "select count(1)c,wm_concat(ed_detno) detno from  ecndetail left join ecn on ed_ecnid=ecn_id left join bom on ed_boid=bo_id left join bomlevel on bo_level=bl_code  where ed_ecnid=? and NVL(ecn_ecrcode,' ')=' ' and bl_ifmrp<>0 and nvl(ed_didstatuscode,' ')<>'CLOSE'";
			rs = baseDao.queryForRowSet(SQLStr, ecn_id);
			if (rs.next()) {
				if (rs.getInt("c") > 0) {
					BaseUtil.showError("序号[" + rs.getString("detno") + "]变更的BOM为参与MRP的BOM，必须走ECR评审流程!'");
				}
			}
		}
		// 自然变更类型的操作只能是替换和禁用
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail,ecn  where ecn_id=ed_ecnid and ed_ecnid=? and ed_type<>'DISABLE' and ed_type<>'SWITCH' and ecn_type='AUTO' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]操作类型不正确，自然变更类型的操作只能是替换和禁用!'");
			}
		}
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from ecndetail where ed_ecnid=? and NVL(ed_type,' ')=' ' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]必须填写操作类型!'");
			}
		}
		// 如果是自然切换类型，替换的变更必须新旧用量一致
		SQLStr = "select count(1) c,wm_concat(ed_detno)ed_detno from  ecndetail,ecn  where ecn_id=ed_ecnid and ed_ecnid=? and ed_newbaseqty<>ed_oldbaseqty and ed_type='SWITCH' and ecn_type='AUTO' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]替换变更必须新旧用量一致!'");
			}
		}
		// 指定的主料物料和序号是否一致   先判断该ECN单据中是否存在对应的新增主料，在增加替代料问题  而且增加序号要小于增加替代料
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail left join bomdetail on bd_bomid=ed_boid and bd_soncode=ed_soncode and ed_bddetno=bd_detno where ed_ecnid=? and (bd_soncode is null and ed_type in('DISABLE','UPDATE','REPDISABLE')) and nvl(ed_didstatuscode,' ')<>'CLOSE' ";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]指定的主料和序号不对应!'");
			}
		}
		// 指定的替换的物料和序号是否一致
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail left join bomdetail on bd_bomid=ed_boid and bd_soncode=ed_repcode and ed_bddetno=bd_detno where ed_ecnid=? and (bd_soncode is null and ed_type='SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]要替换的主料和序号不对应!'");
			}
		}
		// 增加操作不能录入BOM序号
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail where ed_ecnid=? and ed_bddetno>0 and ed_type='ADD' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("操作类型是[增加]不能录入BOM序号!行号[" + rs.getString("ed_detno") + "]'");
			}
		}
		// 判断变更的序号是否已经被禁用
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from ecndetail left join bomdetail on bd_bomid=ed_boid and bd_detno=ed_bddetno where ed_ecnid=? and (bd_soncode<>' ' and bd_usestatus='DISABLE' and ed_type in ('UPDATE','DISABLE','REPADD','REPDISABLE')) and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]指定BOM的序号子件已经禁用，不能变更!'");
			}
		}
		// 指定子件存在判断   先判断是否改ecn单据是否存在增加类型的主料相同，若不存在在判断下面的
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail left join bomdetail on bd_bomid=ed_boid and bd_soncode=ed_soncode and ed_bddetno=bd_detno where ed_ecnid='"
				+ ecn_id + "' and (bd_soncode is null and ed_type='REPADD') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(ed_bddetno,0)<>0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]指定的主料不存在!'");
			}
		}
		//BOM+主料料号是否在当前ECN中是增加的类型，并且增加类型的ECN明细行号ed_detno小于增加替代料的行号ed_detno 
		SQLStr="select count(1) c,wm_concat(B.ed_detno) ed_detno from ecndetail A left join (select * from ecndetail where ed_type='REPADD') B on A.ed_boid = B.ed_boid and A.ed_soncode = B.ed_soncode"
				+" and A.ed_ecnid = B.ed_ecnid where A.ed_type='ADD' and A.ed_ecnid='"+ecn_id+"' and A.ed_detno>B.ed_detno";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]指定的主料的增加替代料类型在当前ECN单据里面序号应小于增加类型的序号!");
			}
		}
		// 指定替代料存在判断
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail left join bomdetail on bd_bomid=ed_boid and bd_detno=ed_bddetno and bd_soncode=ed_soncode left join prodreplace on pre_bdid=bd_id and pre_repcode=ed_repcode  where ed_ecnid='"
				+ ecn_id
				+ "' and ((pre_repcode is null or pre_statuscode='DISABLE') and ed_type='REPDISABLE') and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]指定的替代料不存在或该替代料已禁用!'");
			}
		}
		// 新增主料 物料状态判断
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail left join product on pr_code=ed_soncode where ed_ecnid='"
				+ ecn_id
				+ "' and  ed_type in ('ADD','SWITCH') and NVL(pr_statuscode,' ')<>'AUDITED' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]物料不存在或已禁用!'");
			}
		}
		// 新增替代料 物料状态判断
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail left join product on pr_code=ed_repcode where ed_ecnid='"
				+ ecn_id
				+ "' and  ed_type='REPADD' and NVL(pr_statuscode,' ')<>'AUDITED' and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]【要替代的物料】不存在或已禁用!'");
			}
		}
		// 新增主料重复判断，@add 20161229 如果是批量替换则不限制 @new20171128新增料号+工序编号不存在即可
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail  where ed_ecnid=? and (ed_type='ADD' or (ed_type='SWITCH' and NVL(ed_isbatch,0)=0 )) and ed_boid>0 and nvl(ed_didstatuscode,' ')<>'CLOSE' and exists (select 1 from bomdetail where bd_soncode=ed_soncode and bd_bomid=ed_boid and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(bd_stepcode,' ')=NVL(ed_stepcode,' ')) and rownum<30 ";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新增的料号+工序编号已经在BOM中存在!'");
			}
		}
		// 新增替代料重复判断
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail left join product on pr_code=ed_repcode where ed_ecnid='"
				+ ecn_id
				+ "' and ed_type='REPADD' and ed_repcode<>' ' and nvl(ed_didstatuscode,' ')<>'CLOSE' and ed_repcode in (select pre_repcode from prodreplace,bomdetail where bd_id=pre_bdid and bd_bomid=ed_boid and bd_detno=ed_bddetno and NVL(pre_statuscode,' ')<>'DISABLE') ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]【要替代的物料】已经在BOM替代料中存在!'");
			}
		}
		// 新增半成品子件判断是否存在有效BOM
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail left join product on pr_code=ed_soncode left join bom on ed_soncode=bo_mothercode  where ed_ecnid='"
				+ ecn_id
				+ "' and ed_type in ('ADD','SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE' and pr_manutype in ('OSMAKE','MAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新增的物料是制造件，但是未建立有效的BOM!'");
			}
		}
		// 增加半成品替代料判断是否存在有效BOM
		SQLStr = "select count(1)c,wm_concat(ed_detno)ed_detno from  ECNDetail left join product on pr_code=ed_repcode left join bom on ed_repcode=bo_mothercode  where ed_ecnid='"
				+ ecn_id
				+ "' and ed_type='REPADD' and nvl(ed_didstatuscode,' ')<>'CLOSE' and pr_manutype in ('OSMAKE','MAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新增的物料是制造件，但是未建立有效的BOM!'");
			}
		}
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from  ecndetail left join product on pr_code=ed_soncode where ed_ecnid='"
				+ ecn_id
				+ "' and nvl(ed_didstatuscode,' ')<>'CLOSE' and (  ed_type='ADD' or ed_type='BATCHSWITCH') and  NVL(pr_specvalue,' ') in('SPECIFIC')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新增物料不能是特征件!'");
			}
		}
		// 信息完整判断
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from ecndetail  where ed_ecnid='" + ecn_id
				+ "' and nvl(ed_didstatuscode,' ')<>'CLOSE' and (NVL(ed_soncode,' ')=' '  or (NVL(ed_boid,0)=0 and ed_type not like '%COMMON%')) ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]必须填写[子件编号]和BOMID!'");
			}
		}
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from ecndetail left join product on pr_code=ed_soncode left join productkind on pr_kind=pk_name where ed_ecnid='"
				+ ecn_id
				+ "' and ed_type in ('ADD','UPDATE') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(ed_newbaseqty,0)=0 "
				+"and ((nvl(pr_xikind,' ')<>' ' and exists (select 1 from productkind pk4 left join productkind pk_sub on pk4.pk_subof = pk_sub.pk_id where pk4.pk_name = pr_xikind and NVL(pk4.pk_ifzeroqty,0)=0 and pk4.pk_level= 4 and pk_sub.pk_name=pr_kind3 and pk_sub.pk_level=3 ) ) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')<>' ' and exists(select 1 from productkind pk3 left join productkind pk_sub on pk3.pk_subof = pk_sub.pk_id where pk3.pk_name =pr_kind3 and NVL(pk3.pk_ifzeroqty,0)=0 and pk3.pk_level= 3 and pk_sub.pk_name=pr_kind2 and pk_sub.pk_level=2)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')<>' ' and exists(select 1 from productkind pk2 left join productkind pk_sub on pk2.pk_subof = pk_sub.pk_id where pk2.pk_name =pr_kind2 and NVL(pk2.pk_ifzeroqty,0)=0 and pk2.pk_level= 2 and pk_sub.pk_name=pr_kind and pk_sub.pk_level=1))"
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')=' ' and nvl(pr_kind,' ')<> ' ' and exists (select 1 from productkind pk1 where pk1.pk_name=pr_kind and NVL(pk1.pk_ifzeroqty,0)=0 and pk1.pk_level= 1)))"
				+ " and ed_boid not in (select bo_id from bom where bo_refbomid>0)";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新单位用量不能为0!");
			}
		}
		SQLStr = "select  count(1)c,wm_concat(ed_detno)ed_detno from ecndetail left join product on pr_code=ed_soncode where ed_ecnid='"
				+ ecn_id
				+ "' and ed_type in ('ADD','UPDATE','SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(ed_newbaseqty,0)<0 and ed_boid not in (select bo_id from bom where bo_refbomid>0) and NVL(pr_putouttoint,0)=0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]新单位用量不能为负数!");
			}
		}
		// 位号个数判断
		SQLStr = "select ed_id,ed_ecnid,ed_boid,ed_detno,ed_newbaseqty,ed_newbaseqty-bd_baseqty addqty,NVL(bd_location,' ')bd_location,NVL(ed_oldlocation,' ') ed_oldlocation,NVL(ed_location,' ')ed_location from ecndetail left join bomdetail on ed_boid=bd_bomid and ed_bddetno=bd_detno where ed_ecnid="
				+ ecn_id
				+ " and nvl(ed_didstatuscode,' ')<>'CLOSE' and ed_newbaseqty=round(ed_newbaseqty) and ((ed_type='UPDATE' and bd_baseqty=round(bd_baseqty) and NVL(bd_location||ed_oldlocation||ed_location,' ')<>' ') OR (ed_type='ADD' and ed_location<>' ') OR ((ed_type='SWITCH' and bd_baseqty=round(bd_baseqty) and bd_baseqty=round(bd_baseqty) and NVL(bd_location||ed_oldlocation||ed_location,' ')<>' ')))  ";
		rs = baseDao.queryForRowSet(SQLStr);
		// NoShelvesNumber表示需要位号不存在
		// ExistShelvesNumber表示新增位号已存在
		// ErrorShelvesNumber表示位号错误
		String NoShelvesNumber = "";
		String ErrorShelvesNumber = "";
		String ExistShelvesNumber = "";
		while (rs.next()) {
			int oldlocqty = rs.getString("bd_location").trim().split(",").length;
			int addqty = rs.getString("ed_location").trim().split(",").length;
			int disableqty = rs.getString("ed_oldlocation").trim().split(",").length;
			int newqty = rs.getInt("ed_newbaseqty");
			String bdlocation = rs.getString("bd_location").replace(" ", "");
			if (rs.getString("bd_location") == null || rs.getString("bd_location").trim().equals("")) {
				oldlocqty = 0;
			}
			if (rs.getString("ed_location") == null || rs.getString("ed_location").trim().equals("")) {
				addqty = 0;
			}
			if (rs.getString("ed_oldlocation") == null || rs.getString("ed_oldlocation").trim().equals("")) {
				disableqty = 0;
			}
			if (rs.getString("ed_oldlocation") != null && !rs.getString("ed_oldlocation").equals(" ")) {
				String[] Arraydisable = rs.getString("ed_oldlocation").replace(" ", "").split(",");
				bdlocation = "," + bdlocation + ",";
				for (String c : Arraydisable) {
					if (!bdlocation.contains("," + c + ",")) {
						NoShelvesNumber += "序号[" + rs.getString("ed_detno") + "]禁用的位号:" + c + "在BOM位号中不存在<br>";
					}
				}
			} else {
				disableqty = 0;
			}
			if (rs.getString("ed_location") != null && !rs.getString("ed_location").equals(" ")) {
				String[] Arrayadd = rs.getString("ed_location").replace(" ", "").split(",");
				bdlocation = "," + bdlocation + ",";
				for (String c : Arrayadd) {
					if (bdlocation.contains("," + c + ",")) {
						ExistShelvesNumber += "序号[" + rs.getString("ed_detno") + "]新增的位号:" + c + " 在BOM位号已经存在<br>";
					}
					// 判断是否跟现有位号重复 先判断是否在本次ECN中禁用，如果不是要禁用的位号则判断是否在BOM中已经存在
					Object obj1 = baseDao.getFieldDataByCondition(
							"ecndetail left join bomdetail on ed_boid=bd_bomid and ed_bddetno=bd_detno", "ed_detno",
							"ed_ecnid=" + rs.getString("ed_ecnid") + " and ed_boid=" + rs.getString("ed_boid")
									+ "and nvl(ed_didstatuscode,' ')<>'CLOSE' and ((ed_type='DISABLE' and NVL(bd_usestatus,' ')<>'DISABLE' and ','||bd_location||',' like '%,"
									+ c.trim() + ",%') OR(','||ed_oldlocation||',' like '%," + c.trim() + ",%' ))");
					if (obj1 == null) {
						obj1 = baseDao.getFieldDataByCondition("BOMDetail", "bd_detno",
								"bd_bomid=" + rs.getString("ed_boid")
										+ " and NVL(bd_usestatus,' ')<>'DISABLE' and ','||bd_location||',' like '%,"
										+ c.trim() + ",%' ");
						if (obj1 != null) {
							ExistShelvesNumber += "序号[" + rs.getString("ed_detno") + "]新增的位号:" + c + " 在BOM中已经存在<br>";
						}
					}
				}
			} else {
				addqty = 0;
			}
			// 记录所有位号有错误的信息的detno
			if (oldlocqty + addqty - disableqty != newqty) {
				ErrorShelvesNumber += rs.getString("ed_detno") + ",";
			}
		}
		if (ErrorShelvesNumber != "") {
			// 去掉多余的逗号
			ErrorShelvesNumber = ErrorShelvesNumber.substring(0, ErrorShelvesNumber.length() - 1);
			ErrorShelvesNumber = "序号[" + ErrorShelvesNumber + "]新单位用量跟变更后的位号数量不一致";
		}
		if (ErrorShelvesNumber != "" || NoShelvesNumber != "" || ExistShelvesNumber != "") {
			BaseUtil.showError(NoShelvesNumber + ExistShelvesNumber + ErrorShelvesNumber);
		}
		// 同一个ECN中，母件编号相同且序号相同的的限制；
		SQLStr = "select count(1) cn,wmsys.wm_concat(ed_detno)ed_detno from (select ed_boid,ed_bddetno,count(0) cn,wmsys.wm_concat(ed_detno)ed_detno from ecndetail where ed_ecnid="
				+ ecn_id
				+ " and ed_bddetno>0 and ed_type in('DISABLE','UPDATE','SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE' group by ed_boid,ed_bddetno) where cn>1";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]，操作为：修改、禁用、替换时，母件编号相同且序号相同，不允许提交!");
			}
		}
		// 检查嵌套
		SQLStr = "select count(1) cn,wmsys.wm_concat(ed_detno) ed_detno from ecndetail where ed_ecnid=" + ecn_id
				+ " and ed_boid>0 and ed_soncode=ed_mothercode and ed_type in('ADD','SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]，新增的子件料号不能与母件料号相同， 不允许提交!");
			}
		}

		// 同一个ECN中都为增加操作时，同一母件中同新料相同的也需要限制；
		SQLStr = "select count(1) cn,wmsys.wm_concat(ed_detno) ed_detno from (select ed_boid,ed_soncode,count(0)c,wmsys.wm_concat(ed_detno)ed_detno from ecndetail where ed_ecnid=?"
				+ "  and ed_type in('ADD','SWITCH') and nvl(ed_didstatuscode,' ')<>'CLOSE' and NVL(ed_isbatch,0)=0 group by ed_boid,ed_soncode,ed_stepcode) where c>1";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ed_detno") + "]同一母件中新料+工序编号相同，不允许提交");
			}
		}

		// 判断子件BOM等级和物料等级是否符合母件的BOM等级要求
		ECN_CheckBomLevel(ecn_id);
		// 子件物料如果管控附件,则附件必须存在 反馈 2017100142
		String errProds;
		errProds = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ed_detno) from ecnDetail left join product on ed_soncode=pr_code where ed_type in('ADD','SWITCH') and ed_ecnid=? and nvl(pr_needattach,0) = -1 and nvl(pr_attach,' ') = ' '",
				String.class, ecn_id);
		if (errProds != null) {
			BaseUtil.showError("明细行:" + errProds + "新增物料没有上传附件资料，不允许提交!");
		}
		errProds = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ed_detno) from ecnDetail left join product on ed_repcode=pr_code where ed_type = 'REPADD' and ed_ecnid=? and nvl(pr_needattach,0) = -1 and nvl(pr_attach,' ') = ' '",
				String.class, ecn_id);
		if (errProds != null) {
			BaseUtil.showError("明细行:" + errProds + "新增物料没有上传附件资料，不允许提交!");
		}
		boolean bo = baseDao.isDBSetting("BOM", "ECNChangeBomversion");
		if(bo){
			//同一个BOM在一个ECN中Bom新版本号不能存在多个
			errProds = baseDao.getJdbcTemplate().queryForObject(
					        "select LOB_CONCAT(a) from (select '明细行：'||wmsys.wm_concat(detno)||', BOMID:'||ed_boid||'<br>' as a from "
						     +" (select wmsys.wm_concat(ed_detno)detno,ed_boid from ecndetail where ed_ecnid=? and nvl(ed_didstatuscode,' ')<>'CLOSE'  AND NVL(ed_newbomversion,' ')<>' ' group by ed_boid,ed_newbomversion) group by ed_boid having count(1)>1)",
					String.class, ecn_id);
			if (errProds != null) {
				BaseUtil.showError(errProds +"同一个BOM在一个ECN中BOM新版号不能填写多个不同值!");
			}
		}
	}

	/**
	 * 判断子件BOM等级和物料等级是否符合母件的BOM等级要求
	 * 
	 * @param ecn_id
	 */
	public void ECN_CheckBomLevel(Integer ecn_id) {
		String SQLStr = "";
		SqlRowList rs;
		// 判断BOM等级在BOM等级表中是否存在
		String strs = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct bo_level) from ecndetail left join bom on ed_boid=bo_id left join "
						+ " bomdetail on bd_bomid=ed_boid and bd_detno=ed_bddetno left join prodreplace on pre_bdid=bd_id and "
						+ " pre_repcode=ed_repcode left join bomlevel on bl_code=bo_level "
						+ " where ed_ecnid=? and ed_type in ('SWITCH','ADD','REPADD') "
						+ " and nvl(bo_level,' ')<>' ' and nvl(bl_id,0)=0 and nvl(ed_didstatuscode,' ')<>'CLOSE' and rownum<20",
				String.class, ecn_id);
		if (strs != null) {
			BaseUtil.showError("BOM等级[" + strs + "]在BOM等级表中不存在");
		}
		// 新料的BOM等级不能低于母件的BOM等级
		SQLStr = "select count(1)cn,wm_concat(ed_detno) detno from ecndetail left join bom A on A.bo_id=ed_boid left join bomlevel B on B.bl_code=A.bo_level "
				+ " left join bom MB on (MB.bo_mothercode=ed_soncode or MB.bo_mothercode=ed_repcode) left join bomlevel ML on ML.bl_code=MB.bo_level "
				+ " where ed_ecnid=? and ed_type in ('SWITCH','ADD','REPADD') and nvl(A.bo_level,' ')<>' ' and NVL(MB.bo_level,' ')<>' ' "
				+ " and nvl(ML.bl_grade,0)<nvl(B.bl_grade,0) and nvl(ed_didstatuscode,' ')<>'CLOSE' and rownum<20";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError("新增的子件的BOM等级不能低于母件的等级，序号：" + rs.getString("detno") + "");
		}

		// 优化语句将循环改成一句话 2016/12/22
		SQLStr = "SELECT count(1) cn , wm_concat(ed_detno)detno from ecndetail left join bom on bo_id=ed_boid left join bomlevel on bl_code=bo_level  LEFT JOIN Productleveldetail ON pd_blid=bl_id where ed_ecnid=? and ed_type in ('SWITCH','ADD','REPADD') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(bo_level,' ')<>' ' and nvl(pd_blid,0)<>0 and NVL(pd_useable,0)=0 and exists(select 1  from product where pr_code=(case when ed_type='REPADD' then ed_repcode else ed_soncode end) and pr_level=pd_plcode) and rownum<30 order by bl_id ";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError("物料优选等级在BOM等级定义里被禁用,序号：" + rs.getString("detno") + "");
		}

		SQLStr = "select count(1)cn,wm_concat(ed_detno)detno from (SELECT bl_id,case when ed_type='REPADD' then ed_repcode else ed_soncode end soncode,ed_detno from ecndetail left join bom on bo_id=ed_boid left join bomlevel on bl_code=bo_level  LEFT JOIN Productleveldetail ON pd_blid=bl_id where ed_ecnid=? and ed_type in ('SWITCH','ADD','REPADD') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(bo_level,' ')<>' ' and nvl(pd_blid,0)<>0  and NVL(pd_useable,0)<>0 )T left join product on pr_code=T.soncode where  nvl(pr_level,' ')<>' ' and  pr_level not in (select pd_plcode from Productleveldetail where pd_blid=T.bl_id and NVL(pd_useable,0)<>0 ) and rownum<30";
		rs = baseDao.queryForRowSet(SQLStr, ecn_id);
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError("物料优选等级还没有到达BOM等级要求,序号：" + rs.getString("detno") + "");
		}

		// 物料优选等级
		/*
		 * long bl_tempid = 0; StringBuffer forBiddenS = new StringBuffer();
		 * StringBuffer lowS = new StringBuffer(); // 优选等级 SQLStr =
		 * "SELECT bl_id,case when ed_type='REPADD' then ed_repcode else ed_soncode end soncode,ed_detno"
		 * +
		 * " from ecndetail left join bom on bo_id=ed_boid left join bomlevel on bl_code=bo_level"
		 * +
		 * " LEFT JOIN Productleveldetail ON pd_blid=bl_id where ed_ecnid=? and ed_type in ('SWITCH','ADD','REPADD') and nvl(ed_didstatuscode,' ')<>'CLOSE' and nvl(bo_level,' ')<>' ' and nvl(pd_blid,0)<>0 order by bl_id"
		 * ; rs = baseDao.queryForRowSet(SQLStr, ecn_id); while (rs.next()) {
		 * long bl_id = rs.getLong("bl_id"); if (bl_id != bl_tempid) { SQLStr =
		 * "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_id=?"
		 * ; rs1 = baseDao.queryForRowSet(SQLStr, bl_id); bl_tempid = bl_id; }
		 * if (rs1 != null) { SqlRowList rs2 = new SqlRowList();
		 * rs2.setResultList(rs1.getResultList()); if (rs2.next()) { if
		 * (rs2.getInt("disnum") > 0) { // 判断是否有禁用的物料等级 rs0 = baseDao.
		 * queryForRowSet("select count(1) num from product where pr_code=? and pr_level<>' ' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_id=?)"
		 * , rs.getString("soncode"), bl_id); if (rs0.next() &&
		 * rs0.getInt("num") > 0) {
		 * forBiddenS.append(rs.getString("ed_detno")).append(","); } } else if
		 * (rs2.getInt("allnum") > 0 && rs2.getInt("disnum") == 0) { //
		 * 判断是否有物料等级达到要求等级 rs0 = baseDao.
		 * queryForRowSet("select count(1) num from product where pr_code=? and pr_level<>' ' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_id=?)"
		 * , rs.getString("soncode"), bl_id); if (rs0.next() &&
		 * rs0.getInt("num") > 0) {
		 * lowS.append(rs.getString("ed_detno")).append(","); } } } } } if
		 * (forBiddenS.length() > 0) { forBiddenS.insert(0, "序号：");
		 * forBiddenS.append("的物料优选等级在BOM等级定义里被禁用"); } if (lowS.length() > 0) {
		 * lowS.insert(0, "序号："); lowS.append("的物料优选等级还没有到达BOM等级要求");
		 * forBiddenS.append("<hr>").append(lowS); } if (forBiddenS.length() >
		 * 0) { BaseUtil.showError(forBiddenS.toString()); }
		 */
	}

	/**
	 * 执行自然切换ECN
	 */
	@Override
	public void executeAutoECN() {
		String str = baseDao.callProcedure("SP_DOECN_AUTO", new String[] {});
		if (str != null && !str.trim().equals("")) {
			BaseUtil.showError(str);
		}
	}

	@Override
	public void closeECNAllDetail(int id, String caller) {
		// 判断单据执行状态不允许为已执行“EXECUTED”
		SqlRowList rs = baseDao
				.queryForRowSet("select ecn_didstatuscode,ecn_code,ecn_type from ECN where ecn_id=" + id);
		if (rs.next()) {
			int cn = baseDao.getCount("select count(1) cn from ECNDetail where ed_ecnid=" + id);
			if (cn == 0) {
				BaseUtil.showError("明细行没有数据");
			}
			if (rs.getObject("ecn_didstatuscode") != null && !rs.getObject("ecn_didstatuscode").equals("OPEN")) {
				BaseUtil.showError("只能关闭当前执行状态【打开】的单据");
			}
			baseDao.updateByCondition("ECN", "ecn_didstatuscode='CLOSE' , ecn_didstatus='关闭'", "ecn_id=" + id);
			baseDao.updateByCondition("ECNDetail",
					"ed_didstatuscode='CLOSE',ed_didstatus='" + BaseUtil.getLocalMessage("CLOSE") + "'",
					"ed_ecnid=" + id + " and ed_didstatuscode='OPEN'");
			// 记录操作
			baseDao.logger.others("关闭所有明细行", "所有打开状态的明细行关闭成功", caller, "ecn_id", id);
			if (rs.getString("ecn_type").equals("AUTO")) {
				baseDao.execute(
						"update bomdetail set bd_ecncode=null where exists(select 1 from ecn left join ecndetail on ed_ecnid=ecn_id where "
								+ "ecn_id=" + id
								+ " and bd_bomid=ed_boid and bd_detno=ed_bddetno and bd_ecncode='待'||ecn_code and ed_type='SWITCH')");
			}
		}
	}

	@Override
	public void openECNAllDetail(int id, String caller) {
		// 判断单据执行状态不允许为已执行“EXECUTED”
		SqlRowList rs = baseDao
				.queryForRowSet("select ecn_didstatuscode,ecn_code,ecn_type from ECN where ecn_id=" + id);
		if (rs.next()) {
			int cn = baseDao.getCount("select count(1) cn from ECNDetail where ed_ecnid=" + id);
			if (cn == 0) {
				BaseUtil.showError("明细行没有数据");
			}
			if (rs.getObject("ecn_didstatuscode") != null && !rs.getObject("ecn_didstatuscode").equals("CLOSE")) {
				BaseUtil.showError("只能打开当前执行状态【关闭】的单据");
			}
			baseDao.updateByCondition("ECN", "ecn_didstatuscode='OPEN', ecn_didstatus='打开'", "ecn_id=" + id);
			baseDao.updateByCondition("ECNDetail",
					"ed_didstatuscode='OPEN',ed_didstatus='" + BaseUtil.getLocalMessage("OPEN") + "'",
					"ed_ecnid=" + id + " and ed_didstatuscode='CLOSE'");
			// 记录操作
			baseDao.logger.others("打开所有明细行", "所有关闭状态的明细行打开成功", caller, "ecn_id", id);
			if (rs.getString("ecn_type").equals("AUTO")) {
				baseDao.execute(
						"update bomdetail set bd_ecncode='待'||(select ecn_code from ecn left join ecndetail on ed_ecnid=ecn_id where ecn_id="
								+ id + " and bd_bomid=ed_boid and bd_detno=ed_bddetno and ed_type='SWITCH')"
								+ " where exists(select 1 from ecn left join ecndetail on ed_ecnid=ecn_id where "
								+ " ecn_id=" + id
								+ " and bd_bomid=ed_boid and bd_detno=ed_bddetno and ed_type='SWITCH')");
			}
		}
	}

	@Override
	@Transactional
	public void turnAutoECN(int id, String caller) {
		// 判断必须是自然变更 并且已审核，打开状态
		Object obs[] = baseDao.getFieldsDataByCondition("ecn",
				new String[] { "ecn_type", "ecn_didstatuscode", "ecn_checkstatuscode", "ecn_code" }, "ecn_id=" + id);
		if (obs != null) {
			if (!"AUTO".equals(obs[0])) {
				BaseUtil.showError("只能将自然变更ECN转为立即执行");
			} else if (!"AUDITED".equals(obs[2])) {
				BaseUtil.showError("只能将已审核的自然变更转为立即执行");
			} else if (!"OPEN".equals(obs[1])) {
				BaseUtil.showError("只能将打开状态的自然变更转为立即执行");
			}
			// 转更新ecn_type为NOW
			baseDao.updateByCondition("ecn", "ecn_type='NOW',ecn_remark='手动转为立即执行'", "ecn_id=" + id);
			String str = baseDao.callProcedure("SP_DOECN_NOW", new String[] { obs[3].toString() });
			if (str != null && !str.trim().equals("")) {
				BaseUtil.showError(str);
			}
			// 记录操作
			baseDao.logger.others("手动转立即执行", "成功", caller, "ecn_id", id);
		} else {
			BaseUtil.showError("该ECN不存在或已删除");
		}
	}

	/**
	 * 问题反馈编号：2017110062 针对欧盛，ECN新增或替代 类型中的ed_soncode 子件料号在物料资料表中不存在
	 * 
	 * @author XiaoST 2017年11月8日 下午4:51:46
	 * @param id
	 * @param caller
	 */
	@Override
	public void autoNewProdECN(int id, String caller) {
		/*
		 * baseDao.execute(
		 * "insert into product(pr_id,pr_code,pr_manutype,pr_dhzc,pr_supplytype,pr_status,pr_statuscode,pr_docdate,pr_detail,pr_spec,pr_acceptmethod,pr_unit,pr_kind,pr_whcode,pr_whname) "
		 * +
		 * " select product_seq.nextval,ed_soncode,'PURCHASE','MRP','PUSH','已审核','AUDITED',sysdate ,sonname,sonspec,0,'PCS','加工件','YCL',(select max(wh_description) from warehouse where wh_code='YCL') from "
		 * +
		 * " (select ed_soncode,max(ed_sonname)sonname,max(ed_sonspec)sonspec from ecndetail where ed_ecnid=? and ed_type in('ADD','SWITCH') and instr(ed_soncode,'528')<>1 and instr(ed_soncode,'529')<>1"
		 * +
		 * "and (select count(1) from product where pr_code=ed_soncode) = 0 group by ed_soncode ) A"
		 * , id);
		 */
		String procedure = baseDao.callProcedure("USER_ECNAUTONEWPROD", id);
		if (procedure != null && !procedure.trim().equals("")) {
			BaseUtil.showError(procedure);
		}
	}

	/**
	 * 导入ECN
	 * 
	 * @throws Exception
	 */
	@Override
	@Transactional
	public String importECN(String caller, FileUpload uploadItem) {
		InputStream is = null;
		CommonsMultipartFile file = uploadItem.getFile();
		String ft = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		try {
			is = uploadItem.getFile().getInputStream();
			String ecn_id = null;
			if (ft.equals("xls")) {
				HSSFWorkbook wbs = new HSSFWorkbook(is);
				HSSFSheet sheet = wbs.getSheetAt(0);
				ecn_id = uploadExcel(sheet);
			} else if (ft.equals("xlsx")) {
				Workbook wb = null;
				wb = WorkbookFactory.create(is);
				Sheet sheet = wb.getSheetAt(0);
				ecn_id = uploadExcel(sheet);
			}
			String r = "{success: true,ecn_id: " + ecn_id + "}";
			return new String(r.getBytes("utf-8"), "iso8859-1");
		} catch (Exception e) {
			e.printStackTrace();
			return "{success: false}";
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public String uploadExcel(Sheet sheet) {
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		ArrayList<Object> fieldLsit = null;
		Object textValue = "";
		ArrayList<String> list_String = new ArrayList<String>();// 记录字符串类型的单元格内容，用来获取变更原因
		int startNum = 0;// 记录行数
		int startCol = 0;// 记录列
		String ecncode = null;
		String error = null;
		Map<String, Integer> mergeMap = new HashMap<String, Integer>();// 记录合并单元格的字符串数据集合
		Integer error_row = 0;// 记录出现错误的行号
		// 获取ecn_id
		int ecn_id = baseDao.getSeqId("ECN_SEQ");
		// 遍历工作薄中的所有行
		for (Row row : sheet) {
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:// String类型单元格
					list_String.add(cell.getRichStringCellValue().getString());
					//mergeMap.put(cell.getRichStringCellValue().getString(), row.getRowNum());
					break;
				case Cell.CELL_TYPE_NUMERIC:// 数字类型
					break;
				case Cell.CELL_TYPE_BOOLEAN:// Boolean类型
					break;
				case Cell.CELL_TYPE_FORMULA:// 公式
					// 输出公式
					break;
				default:
					break;
				}
			}
		}
		// 需求日期
		int sheetMergeCount = sheet.getNumMergedRegions();
		String format = null;
		// 变更原因及变更原因行号
		String ECN_reason = "";
		int ECN_reason_row = 0;
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();
			Row fRow1 = sheet.getRow(firstRow);
			Cell fCell1 = fRow1.getCell(firstColumn);
			if(fCell1.getCellType()==Cell.CELL_TYPE_STRING){
				mergeMap.put(fCell1.getRichStringCellValue().getString(), fRow1.getRowNum());
			}
			if (3 == firstRow && 3 == lastRow) {
				if (12 >= firstColumn && 14 <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					if(fCell!=null){
						if(fCell.getCellType()==0){
							if(HSSFDateUtil.isCellDateFormatted(fCell)){
								format = "to_date('" + sdf.format(fCell.getDateCellValue()) + "','yyyy-mm-dd')";
							}
						}else{
							format = "to_date('" + fCell.getStringCellValue().trim() + "','yyyy-mm-dd')";
						}
					}else{
						format = null;
					}
				}
			}
			if (firstColumn == 1 && lastColumn == 14) {
				Row fRow = sheet.getRow(firstRow);
				Cell fCell = fRow.getCell(firstColumn);
				String reason_val = fCell.getStringCellValue();
				if ("".equals(reason_val)) {
					ECN_reason = null;
					ECN_reason_row = fRow.getRowNum();
				}
			}
		}
		Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
		int temp_index = 0;
		for (int i = 0; i < list_String.size(); i++) {
			String empt = list_String.get(i);
			if (empt.contains("变更原因及简要描述")) {
				temp_index = i + 1;
			}
		}
		// 变更原因及变更原因行号,行号用来控制导入数据的区域范围
		if (ECN_reason != null && "".equals(ECN_reason)) {
			ECN_reason = list_String.get(temp_index).trim();
			// 获取变更原因的行号
			for (String key : mergeMap.keySet()) {
				if (key.trim().equals(ECN_reason.trim())) {
					ECN_reason_row = mergeMap.get(key);
				}
			}
		}

		if (ECN_reason_row == 0) {
			error = "请检查变更原因及简要描述的内容是否含有非法字符";
			return error = "0,deptno:" + "\"" + error + "\"";
		}
		//获取申请人，项目工程师，装配知会人，业务知会人，其他知会     
		Row fRow = sheet.getRow(ECN_reason_row+1);
		Cell fCell = fRow.getCell(2);
		String ECN_UPMAN = fCell.getStringCellValue();
		SqlRowList rowList = null;
		String ecn_xmgcs =null;
		String ecn_zp =null;
		String ecn_yw =null;
		String ecn_other =null;
		String ecn_xmgcs_code =null;
		String ecn_zp_code =null;
		String ecn_yw_code =null;
		String ecn_other_code =null;
		
		fRow = sheet.getRow(ECN_reason_row+3);
		fCell = fRow.getCell(2);
		ecn_xmgcs = fCell.getStringCellValue();
		rowList = baseDao.queryForRowSet("select em_code,em_name from employee where em_name=?", ecn_xmgcs.trim());
		while(rowList.next()){
			ecn_xmgcs_code=rowList.getString("em_code");
		}
		fCell = fRow.getCell(4);
		ecn_zp = fCell.getStringCellValue();
		rowList = baseDao.queryForRowSet("select em_code,em_name from employee where em_name=?", ecn_zp.trim());
		while(rowList.next()){
			ecn_zp_code=rowList.getString("em_code");
		}
		fCell = fRow.getCell(6);
		ecn_yw = fCell.getStringCellValue();
		rowList = baseDao.queryForRowSet("select em_code,em_name from employee where em_name=?", ecn_yw.trim());
		while(rowList.next()){
			ecn_yw_code=rowList.getString("em_code");
		}
		fCell = fRow.getCell(8);
		ecn_other = fCell.getStringCellValue();
		rowList = baseDao.queryForRowSet("select em_code,em_name from employee where em_name=?", ecn_other.trim());
		while(rowList.next()){
			ecn_other_code=rowList.getString("em_code");
		}
		// 得到总行数
		String ecn_changereason = null;
		while (rows.hasNext()) {
			// 前7行不用读取
			startNum++;
			// 读取的数据，判断不能超过变更原因的行数
			if (startNum > 6 && startNum < ECN_reason_row
					&& (sheet.getRow(startNum).getCell(1).getCellType() != HSSFCell.CELL_TYPE_BLANK)) {
//				boolean a =sheet.getRow(startNum).getCell(1).CELL_TYPE_BLANK.equals("");
				Row row = sheet.getRow(startNum); // 获得行数据
				startCol = 0;
				fieldLsit = new ArrayList<Object>();
				map = new HashMap<String, Object>();
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
				while (cells.hasNext() && startCol < 15) {
					startCol++;
					if (startCol > 0 && startCol < 15) {
						Cell cell = row.getCell(startCol);
						textValue = "";
						if(cell!=null){
							switch (cell.getCellType()) { // 根据cell中的类型来输出数据
							case HSSFCell.CELL_TYPE_NUMERIC:
								textValue = new DecimalFormat("#.##").format(cell.getNumericCellValue());
								break;
							case HSSFCell.CELL_TYPE_STRING:
								textValue = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								textValue = cell.getBooleanCellValue();
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								textValue = cell.getCellFormula();
								break;
							default:
								textValue = "";
								break;
							}
						}
						fieldLsit.add(textValue);
					}
				}
				map.put("ed_detno", fieldLsit.get(0));
				map.put("ed_mothercode", fieldLsit.get(1));
				map.put("ed_stepcode", fieldLsit.get(2));
				map.put("ed_soncode", fieldLsit.get(3));
				map.put("ed_sonname", fieldLsit.get(4));
				map.put("ed_sonspec", fieldLsit.get(5));
				map.put("ed_brand_user", fieldLsit.get(6));
				map.put("ed_newbaseqty", fieldLsit.get(7));
				map.put("ed_orderqty_user", fieldLsit.get(8));
				map.put("ed_type", fieldLsit.get(9));
				map.put("ed_bglx_user", fieldLsit.get(10));
				map.put("ed_bdremark", fieldLsit.get(11));
				map.put("ed_repcode", fieldLsit.get(12));
				// map.put("", fieldLsit.get(13));
				result.add(map);
			} else if (startNum < 6) {
				startNum++;
			} else {
				break;
			}
		}
		// 主表申请人、 变更类型、BOM阶段默认为当前用户，立即变更、ECN,以及变更原因，当前时间ecn_indate，审核状态
		// ecn_checkstatus
		try {
			Employee employee = SystemSession.getUser();
			ArrayList<Integer> st_ids = new ArrayList<Integer>();
			ecncode = baseDao.sGetMaxNumber("ECN", 2);
			String sql = "insert into ECN (ecn_code,ecn_id,ecn_type,ecn_prodstage,ecn_indate,ecn_checkstatus,ECN_CHECKSTATUSCODE,ECN_DIDSTATUSCODE,ECN_DIDSTATUS,ecn_changereason,ecn_xqrq_user,ecn_recordman,ecn_recorderid,ecn_upman,ecn_xmgcs_user,ecn_zp_user,ecn_yw_user,ecn_other_user,ecn_xmgcscode_user,ecn_zpcode_user,ecn_ywcode_user,ecn_othercode_user) values ('"
					+ ecncode + "'," + ecn_id + ",'NOW','ECN',sysdate,'" + BaseUtil.getLocalMessage("ENTERING")
					+ "','ENTERING','OPEN','" + BaseUtil.getLocalMessage("OPEN") + "','" + ECN_reason + "'," + format
					+ ",'"+employee.getEm_name()+"','"+employee.getEm_id()+"','"+ECN_UPMAN+"','"+ecn_xmgcs+"','"+ecn_zp+"','"+ecn_yw+"','"+ecn_other+"','"+ecn_xmgcs_code+"','"+ecn_zp_code+"','"+ecn_yw_code+"','"+ecn_other_code+"')";
			baseDao.execute(sql);

			for (Map<String, Object> m : result) {
				// 记录出错行数
				error_row++;
				String ed_type = (String) m.get("ed_type");// 获取操作类型
				Object ed_type_object = baseDao.getFieldDataByCondition("datalistcombo", "dlc_display",
						" dlc_caller ='ECN' and dlc_fieldname='ed_type' and dlc_value='" + ed_type + "'");
				if (ed_type_object == null) {
					baseDao.execute("delete from ECN where ecn_id ='" + ecn_id + "'");
					baseDao.execute("delete from ECNDetail  where ed_ecnid ='" + ecn_id + "'");
					error = "行：" + m.get("ed_detno") + "操作类型错误!";
					return error = "0,deptno:" + "\"" + error + "\"";
				}

				switch (ed_type_object.toString()) {
				case "ADD":// 等价增加
					// 根据母件编号，获取BOMID 母件名称、规格
					Object[] add_ed_mothercode = baseDao.getFieldsDataByCondition(
							"BOM left join product on bo_mothercode=pr_code", "bo_id,pr_detail,pr_spec",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "'");
					// 获取ed_sonname，ed_sonspec,ed_newbaseqty，ed_bdremark
					// 子件名称，子件规格.新单位用量,备注
					String add_ed_newbaseqty = m.get("ed_newbaseqty").toString().trim();
					String add_ed_bdremark = m.get("ed_bdremark").toString().trim();
					String add_ed_mzmc_user = m.get("ed_stepcode").toString().trim();
					int a = baseDao.getCount("select count(1) no from step where st_code = '" + add_ed_mzmc_user
							+ "' and st_name ='" + add_ed_mzmc_user + "'");
					if (a == 0 && add_ed_mzmc_user != null && !add_ed_mzmc_user.equals("")) {
						int st_id = baseDao.getSeqId("STEP_SEQ");
						st_ids.add(st_id);
						baseDao.execute(
								"insert into step(st_id,st_code,st_name,st_status,st_statuscode,st_indate,st_inman,st_auditdate,st_auditman) values ("
										+ st_id + ",'" + add_ed_mzmc_user + "','" + add_ed_mzmc_user + "','"
										+ BaseUtil.getLocalMessage("AUDITED") + "','AUDITED',sysdate,'"
										+ employee.getEm_name() + "',sysdate,'" + employee.getEm_name() + "')");
					}
					ed_type = "ADD";
					// 根据旧料或替代料号，获取对应的名称和规格
					String SQL = "insert into ecndetail(ed_id,ed_code,ed_ecnid,ed_detno,ed_type,ed_boid,ed_mothercode,ed_mothername,ed_motherspec,ed_soncode,ed_sonname,ed_sonspec"
							+ ",ed_oldforreplace,ed_newbaseqty,ed_remark,ed_didstatus,ed_didstatuscode,ed_stepcode,ed_orderqty_user,ed_bglx_user,ed_brand_user) values (ecndetail_seq.nextval,'"
							+ ecncode + "','" + ecn_id + "','" + m.get("ed_detno").toString() + "','" + ed_type + "',"
							+ (add_ed_mothercode == null ? null : add_ed_mothercode[0]) + ",'"
							+ m.get("ed_mothercode").toString().trim() + "','"
							+ (add_ed_mothercode == null ? null : add_ed_mothercode[1]) + "','"
							+ (add_ed_mothercode == null ? null : add_ed_mothercode[2]) + "','"
							+ m.get("ed_soncode").toString().trim() + "','" + m.get("ed_sonname").toString().trim()
							+ "','" + m.get("ed_sonspec").toString().trim() + "','0','" + add_ed_newbaseqty + "','"
							+ add_ed_bdremark + "','" + BaseUtil.getLocalMessage("OPEN") + "','OPEN','"
							+ m.get("ed_stepcode") + "','" + m.get("ed_orderqty_user") + "','" + m.get("ed_bglx_user")
							+ "','" + m.get("ed_brand_user").toString().trim() + "')";
					baseDao.execute(SQL);
					break;
				case "DISABLE":// 等价于禁用
					// 根据母件编号，获取BOMID 母件名称、规格
					Object[] disable_ed_mothercode = baseDao.getFieldsDataByCondition(
							"BOM left join product on bo_mothercode=pr_code", "bo_id,pr_detail,pr_spec",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "'");
					// 根据物料编码，获取ed_bddetno，pr_detail，pr_spec，ed_oldbaseqty
					Object[] disable_ed_soncode = baseDao.getFieldsDataByCondition(
							"bom left join bomDetail on bo_id =bd_bomid left join product on bd_soncode= pr_code",
							"bd_detno,bd_baseqty",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "' and bd_soncode='"
									+ m.get("ed_soncode").toString().trim() + "' and bd_stepcode ='"
									+ m.get("ed_stepcode") + "'");
					// 子件名称，子件规格.新单位用量,备注
					String disable_ed_bdremark = m.get("ed_bdremark").toString().trim();
					String disable_ed_mzmc_user = m.get("ed_stepcode").toString().trim();
					// 插入工序表
					int disable_a = baseDao.getCount("select count(1) no from step where st_code = '"
							+ disable_ed_mzmc_user + "' and st_name ='" + disable_ed_mzmc_user + "'");
					if (disable_a == 0 && disable_ed_mzmc_user != null && !disable_ed_mzmc_user.equals("")) {
						int st_id = baseDao.getSeqId("STEP_SEQ");
						st_ids.add(st_id);
						baseDao.execute(
								"insert into step(st_id,st_code,st_name,st_status,st_statuscode,st_indate,st_inman,st_auditdate,st_auditman) values ("
										+ st_id + ",'" + disable_ed_mzmc_user + "','" + disable_ed_mzmc_user + "','"
										+ BaseUtil.getLocalMessage("AUDITED") + "','AUDITED',sysdate,'"
										+ employee.getEm_name() + "',sysdate,'" + employee.getEm_name() + "')");
					}
					// 操作类型
					ed_type = "DISABLE";
					// 根据旧料或替代料号，获取对应的名称和规格
					String dis_SQL = "insert into ecndetail (ed_id,ed_code,ed_ecnid,ed_detno,ed_type,ed_boid,ed_mothercode,ed_mothername,ed_motherspec,ed_bddetno,ed_soncode,ed_sonname,ed_sonspec"
							+ ",ed_oldforreplace,ed_oldbaseqty,ed_newbaseqty,ed_remark,ed_didstatus,ed_didstatuscode,ed_stepcode,ed_orderqty_user,ed_bglx_user,ed_brand_user) values (ecndetail_seq.nextval,'"
							+ ecncode + "','" + ecn_id + "','" + m.get("ed_detno").toString() + "','" + ed_type + "',"
							+ (disable_ed_mothercode == null ? null : disable_ed_mothercode[0]) + ",'"
							+ m.get("ed_mothercode").toString().trim() + "','"
							+ (disable_ed_mothercode == null ? null : disable_ed_mothercode[1]) + "','"
							+ (disable_ed_mothercode == null ? null : disable_ed_mothercode[2]) + "',"
							+ (disable_ed_soncode == null ? null : disable_ed_soncode[0]) + ",'"
							+ m.get("ed_soncode").toString().trim() + "','" + m.get("ed_sonname").toString().trim()
							+ "','" + m.get("ed_sonspec").toString().trim() + "','0',"
							+ (disable_ed_soncode == null ? null : disable_ed_soncode[0]) + ",'"
							+ m.get("ed_newbaseqty").toString() + "','" + disable_ed_bdremark + "','"
							+ BaseUtil.getLocalMessage("OPEN") + "','OPEN','" + m.get("ed_stepcode") + "','"
							+ m.get("ed_orderqty_user") + "','" + m.get("ed_bglx_user") + "','"
							+ m.get("ed_brand_user").toString().trim() + "')";
					baseDao.execute(dis_SQL);
					break;
				case "SWITCH":// 等价于替换
					String switch_ed_repcode = m.get("ed_repcode").toString().trim();
					// 根据母件编号，获取BOMID 母件名称、规格
					Object[] switch_ed_mothercode = baseDao.getFieldsDataByCondition(
							"BOM left join product on bo_mothercode=pr_code", "bo_id,pr_detail,pr_spec",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "'");
					// 根据旧物料编码，获取序号和旧单位用量
					Object[] switch_ed_soncode = baseDao.getFieldsDataByCondition(
							"bom left join bomDetail on bo_id =bd_bomid left join product on bd_soncode= pr_code",
							"bd_detno,bd_baseqty",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "' and bd_soncode='"
									+ switch_ed_repcode.trim() + "' and bd_stepcode = '" + m.get("ed_stepcode") + "'");
					// 获取ed_sonname，ed_sonspec,ed_newbaseqty，ed_bdremark
					// 子件名称，子件规格.新单位用量,备注
					String switch_ed_newbaseqty = m.get("ed_newbaseqty").toString();
					String switch_ed_bdremark = m.get("ed_bdremark").toString();
					String switch_ed_mzmc_user = m.get("ed_stepcode").toString();
					int switch_a = baseDao.getCount("select count(1) no from step where st_code = '"
							+ switch_ed_mzmc_user + "' and st_name ='" + switch_ed_mzmc_user + "'");
					if (switch_a == 0 && switch_ed_mzmc_user != null && !switch_ed_mzmc_user.equals("")) {
						int st_id = baseDao.getSeqId("STEP_SEQ");
						st_ids.add(st_id);
						baseDao.execute(
								"insert into step(st_id,st_code,st_name,st_status,st_statuscode,st_indate,st_inman,st_auditdate,st_auditman) values ("
										+ st_id + ",'" + switch_ed_mzmc_user + "','" + switch_ed_mzmc_user + "','"
										+ BaseUtil.getLocalMessage("AUDITED") + "','AUDITED',sysdate,'"
										+ employee.getEm_name() + "',sysdate,'" + employee.getEm_name() + "')");
					}
					// 操作类型旧料
					ed_type = "SWITCH";
					String switchSQL = "insert into ecndetail(ed_id,ed_code,ed_ecnid,ed_detno,ed_type,ed_boid,ed_mothercode,ed_mothername,ed_motherspec,ed_bddetno,ed_soncode,ed_sonname,ed_sonspec"
							+ ",ed_oldforreplace,ed_oldbaseqty,ed_newbaseqty,ed_repcode,ed_remark,ed_didstatus,ed_didstatuscode,ed_stepcode,ed_orderqty_user,ed_bglx_user,ed_brand_user) values (ecndetail_seq.nextval,'"
							+ ecncode + "','" + ecn_id + "','" + m.get("ed_detno").toString() + "','" + ed_type + "',"
							+ (switch_ed_mothercode == null ? null : switch_ed_mothercode[0]) + ",'"
							+ m.get("ed_mothercode").toString().trim() + "','"
							+ (switch_ed_mothercode == null ? null : switch_ed_mothercode[1]) + "','"
							+ (switch_ed_mothercode == null ? null : switch_ed_mothercode[2]) + "',"
							+ (switch_ed_soncode == null ? null : switch_ed_soncode[0]) + ",'"
							+ m.get("ed_soncode").toString().trim() + "','" + m.get("ed_sonname").toString().trim()
							+ "','" + m.get("ed_sonspec").toString().trim() + "','0',"
							+ (switch_ed_soncode == null ? null : switch_ed_soncode[0]) + ",'"
							+ m.get("ed_newbaseqty").toString() + "','" + m.get("ed_repcode").toString().trim() + "','"
							+ m.get("ed_bdremark").toString() + "','" + BaseUtil.getLocalMessage("OPEN") + "','OPEN','"
							+ m.get("ed_stepcode") + "','" + m.get("ed_orderqty_user") + "','" + m.get("ed_bglx_user")
							+ "','" + m.get("ed_brand_user").toString().trim() + "')";

					baseDao.execute(switchSQL);
					break;
				case "UPDATE":// 等价于修改
					// 根据母件编号，获取BOMID 母件名称、规格
					Object[] update_ed_mothercode = baseDao.getFieldsDataByCondition(
							"BOM left join product on bo_mothercode=pr_code", "bo_id,pr_detail,pr_spec",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "'");
					// 根据物料编码，获取BOM序号, 旧单位用量ed_bddetno ,ed_oldbaseqty
					Object[] update_ed_soncode = baseDao.getFieldsDataByCondition(
							"bom left join bomDetail on bo_id =bd_bomid left join product on bd_soncode= pr_code",
							"bd_detno,bd_baseqty",
							"bo_mothercode='" + m.get("ed_mothercode").toString().trim() + "' and bd_soncode='"
									+ m.get("ed_soncode").toString().trim() + "' and bd_stepcode = '"
									+ m.get("ed_stepcode") + "'");
					// 新单位用量 备注 变更类型
					ed_type = "UPDATE";
					String update_ed_newbaseqty = m.get("ed_newbaseqty").toString();
					String update_ed_bdremark = m.get("ed_bdremark").toString();
					String update_ed_mzmc_user = m.get("ed_stepcode").toString();
					int update_a = baseDao.getCount("select count(1) no from step where st_code = '"
							+ update_ed_mzmc_user + "' and st_name ='" + update_ed_mzmc_user + "'");
					if (update_a == 0 && update_ed_mzmc_user != null && !update_ed_mzmc_user.equals("")) {
						int st_id = baseDao.getSeqId("STEP_SEQ");
						st_ids.add(st_id);
						baseDao.execute(
								"insert into step(st_id,st_code,st_name,st_status,st_statuscode,st_indate,st_inman,st_auditdate,st_auditman) values ("
										+ st_id + ",'" + update_ed_mzmc_user + "','" + update_ed_mzmc_user + "','"
										+ BaseUtil.getLocalMessage("AUDITED") + "','AUDITED',sysdate,'"
										+ employee.getEm_name() + "',sysdate,'" + employee.getEm_name() + "')");
					}
					String update_sql = "insert into ecnDetail(ed_id,ed_code,ed_ecnid,ed_detno,ed_type,ed_boid,ed_mothercode,ed_mothername,ed_motherspec,ed_bddetno,ed_soncode,ed_sonname,ed_sonspec"
							+ ",ed_oldforreplace,ed_oldbaseqty,ed_newbaseqty,ed_remark,ed_didstatus,ed_didstatuscode,ed_stepcode,ed_orderqty_user,ed_bglx_user,ed_brand_user) values (ecndetail_seq.nextval,'"
							+ ecncode + "','" + ecn_id + "','" + m.get("ed_detno").toString() + "','" + ed_type + "',"
							+ (update_ed_mothercode == null ? null : update_ed_mothercode[0]) + ",'"
							+ m.get("ed_mothercode").toString().trim() + "','"
							+ (update_ed_mothercode == null ? null : update_ed_mothercode[1]) + "','"
							+ (update_ed_mothercode == null ? null : update_ed_mothercode[2]) + "',"
							+ (update_ed_soncode == null ? null : update_ed_soncode[0]) + ",'"
							+ m.get("ed_soncode").toString().trim() + "','" + m.get("ed_sonname").toString().trim()
							+ "','" + m.get("ed_sonspec").toString().trim() + "','0',"
							+ (update_ed_soncode == null ? null : update_ed_soncode[1]) + "," + update_ed_newbaseqty
							+ ",'" + update_ed_bdremark + "','" + BaseUtil.getLocalMessage("OPEN") + "','OPEN','"
							+ m.get("ed_stepcode") + "','" + m.get("ed_orderqty_user") + "','" + m.get("ed_bglx_user")
							+ "','" + m.get("ed_brand_user").toString().trim() + "')";
					baseDao.execute(update_sql);
					break;
				}
			}
		} catch (Exception e) {
			baseDao.execute("delete from ecndetail where ed_ecnid='" + ecn_id + "'");
			baseDao.execute("delete from ecn where ecn_id='" + ecn_id + "'");
			e.printStackTrace();
			error = "行号：" + error_row + ",数据有误(导入数据不能含有公式以及非法字符)!";
			return error = "0,deptno:" + "\"" + error + "\"";
		}
		return String.valueOf(ecn_id) + ",deptno:" + "\"" + error + "\"";
	}

	@Override
	public String turnApplication(int id, String caller) {
		List<String> list = baseDao.callProcedureWithOut("USER_ECN_TURNAPPLICATION", new Object[]{id,SystemSession.getUser().getEm_name()}, new Integer[]{1,2}, new Integer[] {3,4,5});
		if (list.size()!= 0) {
			if(list.get(2) != null && !"".equals(list.get(2))){
			    BaseUtil.showError(list.get(2));
			}else{
				return "转入成功，请购单号:<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?formCondition=ap_idIS" + list.get(0)
						+ "&gridCondition=ad_apidIS" + list.get(0) + "&whoami=Application')\">" + list.get(1) + "</a>&nbsp";
			}
		}else{
			BaseUtil.showError("转请购单失败");
		}
		return null;
	}
}
