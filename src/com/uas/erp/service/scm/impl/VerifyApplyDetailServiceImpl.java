package com.uas.erp.service.scm.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.QUAVerifyApplyDetailDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.scm.VerifyApplyDetailService;
@Service
public class VerifyApplyDetailServiceImpl implements VerifyApplyDetailService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private QUAVerifyApplyDetailDao quaVerifyApplyDetailDao;
	@Autowired
	private ProcessService processService;

	@Override
	public void updateVerifyApplyDetailById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[未审核]的单据资料!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_statuscode", "ve_testman" },
				"ve_id=" + store.get("ve_id"));
		if (!"UNAUDIT".equals(status[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.update_onlyEntering"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改QUA_VerifyApplyDetail
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_VerifyApplyDetail", "ve_id");
		baseDao.execute(formSql);
		// 修改QUA_VerifyApplyDetailDet
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "QUA_VerifyApplyDetailDet", "ved_id");
		for (Map<Object, Object> s : gstore) {
			Object[] ve_code = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[]{"ve_code","ve_class"}, "ve_id=" + store.get("ve_id"));
			SqlRowList copy = baseDao.queryForRowSet("select ve_code from QUA_VerifyApplyDetail where ve_code like '%"+ve_code[0]+"%' and ve_class='"+ve_code[1]+"' and ve_id<>"+store.get("ve_id"));
			if(copy.next()){
				BaseUtil.showError("该检验单已进行过再次检验,不能修改明细行,单号:"+copy.getString("ve_code")+"");
			}
			if (s.get("ved_id") == null || s.get("ved_id").equals("") || s.get("ved_id").equals("0")
					|| Integer.parseInt(s.get("ved_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUA_VERIFYAPPLYDETAILDET_SEQ");
				s.put("ved_statuscode", "UNAUDIT");
				s.put("ved_status", BaseUtil.getLocalMessage("UNAUDIT"));
				s.put("ved_date", store.get("ve_date"));
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_VerifyApplyDetailDet", new String[] { "ved_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update qua_verifyapplydetaildet set ved_code=(select ve_code from qua_verifyapplydetail where ved_veid=ve_id) where ved_veid="
				+ store.get("ve_id") + " and not exists (select 1 from qua_verifyapplydetail where ved_code=ve_code)");
		baseDao.execute(
				"update Qua_VerifyApplyDetailDet set ved_samplingqty=nvl(ved_samplingokqty,0)+nvl(ved_samplingngqty,0) where ved_veid=? and nvl(ved_samplingqty,0)=0",
				store.get("ve_id"));
		Object testman = store.get("ve_testman");
		if (!"".equals(testman) && testman != null) {
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_testman='" + testman + "'", "ved_veid=" + store.get("ve_id")
					+ " and nvl(ved_testman,' ')=' '");
		} else {
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetail",
					"ve_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
							+ store.get("ve_id") + ") order by ve_date desc) where rownum<2)", "ve_id=" + store.get("ve_id")
							+ " and nvl(ve_testman,' ')=' '");
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet",
					"ved_testman=(select ve_testman from qua_verifyapplydetail where ved_veid=ve_id)", "ved_veid=" + store.get("ve_id")
							+ " and nvl(ved_testman,' ')=' '");
		}
		baseDao.execute("update QUA_VerifyApplyDetail set (ve_brand,ve_oldfactory,ve_factoryspec)=(select pa_brand,pa_addressmark,pa_factoryspec from (select pa_brand,pa_addressmark,pa_factoryspec from ProductApproval where nvl(pa_statuscode,' ')='AUDITED' AND NVL(pa_finalresult,' ')='合格' and (pa_prodcode,pa_providecode) IN (select vad_prodcode,vad_vendcode from qua_verifyapplydetail where ve_id="
				+ store.get("ve_id") + ") order by pa_auditdate desc) where rownum<2) where ve_id=" + store.get("ve_id"));
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(ve_code) from Qua_verifyApplyDetail where nvl(vad_qty,0) >(select sum(nvl(ved_checkqty,0)) from Qua_verifyApplyDetailDet where ved_veid=ve_id) and ve_id="
								+ store.get("ve_id"), String.class);
		if (dets != null) {
			BaseUtil.showErrorOnSuccess("良品数+不良品数小于收料数!");
		}
		// 记录操作
		baseDao.logger.update(caller, "ve_id", store.get("ve_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteVerifyApplyDetail(int ve_id, String caller) {
		// 只能删除未审核的检验单!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_statuscode", "ve_code" }, "ve_id="
				+ ve_id);
		if (!status[0].equals("UNAUDIT") && !status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.delete_onlyEntering"));
		}
		int count = baseDao.getCountByCondition("ProdIODetail", "pd_qcid in (select ved_id from qua_verifyapplydetaildet where ved_veid="
				+ ve_id + ")");
		if (count > 0) {
			BaseUtil.showError("明细行已入库，不允许删除!");
		}
		Object object = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_code", "mq_sourcecode='" + status[1] + "'");
		if (object != null) {
			BaseUtil.showError("该检验单已转生产品质异常单，不允许删除!单号是:" + object);
		}
		// 针对锤子科技的再次送检功能  限制  再次送检过的单据不能删除   17-11-09 maz
		Object[] ve_code = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[]{"ve_code","ve_class"}, "ve_id=" + ve_id);
		SqlRowList copy = baseDao.queryForRowSet("select ve_code from QUA_VerifyApplyDetail where ve_code like '%"+ve_code[0]+"%' and ve_class='"+ve_code[1]+"' and ve_id<>"+ve_id);
		if(copy.next()){
			BaseUtil.showError("该检验单已进行过再次检验,请先删除再次检验的单据,单号:"+copy.getString("ve_code")+"");
		}
		// IQC检验单反审核后把相关数据反写回收料单中
		Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code" }, "ve_id="
				+ ve_id);
		// 收料单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("VerifyApplyDetail", "vad_sendstatus", "vad_code='" + objs[1] + "' and vad_detno ="
				+ objs[0], String.class);
		StateAssert.onSendingLimit(sendStatus);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ve_id });
		// 删除QUA_VerifyApplyDetailDet
		baseDao.deleteById("QUA_VerifyApplyDetailDet", "ved_veid", ve_id);
		// 删除QUA_VerifyApplyDetail
		quaVerifyApplyDetailDao.deleteQC(ve_id,caller);
		// 记录操作
		baseDao.logger.delete(caller, "ve_id", ve_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ve_id });
	}

	@Override
	public String[] printVerifyApplyDetail(int ve_id, String reportName, String condition, String caller) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { ve_id });
		// 执行打印操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'", "ve_id=" + ve_id);
		// 记录操作
		baseDao.logger.print(caller, "ve_id", ve_id);
		// 记录打印次数
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_count=nvl(ve_count,0)+1", "ve_id=" + ve_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { ve_id });
		return keys;
	}

	@Override
	public void auditVerifyApplyDetail(int ve_id, String caller) {
		baseDao.execute("update qua_verifyapplydetaildet set ved_code=(select ve_code from qua_verifyapplydetail where ved_veid=ve_id) where ved_veid="
				+ ve_id + " and not exists (select 1 from qua_verifyapplydetail where ved_code=ve_code)");
		// 只能对状态为[未审核]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail",
				new String[] { "ve_statuscode", "ve_code", "ve_testman" }, "ve_id=" + ve_id);
		if (!"COMMITED".equals(status[0])&&!"UNAUDIT".equals(status[0])) 
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.audit_onlyCommited"));
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pd_inoutno) from prodiodetail left join QUA_VerifyApplyDetailDet on pd_qcid=ved_id where ved_veid="
						+ ve_id + " and pd_status<>'99' AND PD_PICLASS='不良品入库单'", String.class);
		if (dets != null) {
			BaseUtil.showError("存在未过账的不良品入库单，不允许进行当前操作!不良品入库单号：<br>" + dets);
		}

		String result = baseDao.getJdbcTemplate().queryForObject("select ve_result from qua_verifyapplydetail where ve_id = " + ve_id,
				String.class);
		if (baseDao.isDBSetting("VerifyApplyDetail","upQualifiedLimit") && "不合格".equals(result)) {
			int count = baseDao.getCountByCondition("QUA_VerifyApplyDetailDet", "nvl(ved_ngqty,0)>0 and ved_veid = " + ve_id);
			if (count == 0) {
				BaseUtil.showError("不允许审核！检验结果是不合格，明细的检验结果里必须有不合格数");
			}
		}

		baseDao.execute("delete from QUA_VerifyApplyDetailDet where ved_veid=" + ve_id + " and nvl(ved_okqty,0)=0 and nvl(ved_ngqty,0)=0");
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ve_id);
		// 读取配置项“是否强制检验方式”， 如果配置值“是”，审核前判断检验方式的检验程度是否低于建议的检验方式
		if (baseDao.isDBSetting(caller, "mustProductVendorMethod")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select ve_method,nvl(pv_method,'正常抽检') pv_method from QUA_VerifyApplyDetail left join product on vad_prodcode=pr_code left join ProductVendorIQC on pv_prodid=pr_id and pv_vendcode=vad_vendcode where ve_id=? and nvl(pv_method, '正常抽检')<>nvl(ve_method,' ')",
							ve_id);
			if (rs.next()) {
				BaseUtil.appendError("检验方式(" + rs.getGeneralString("ve_method") + ")与建议的检验方式(" + rs.getString("pv_method") + ")不一致");
			}
		}
		// 检验上传状态
		String sendStatus = baseDao.getFieldValue("VerifyApplyDetail", "vad_sendstatus", "ve_code='" + status[1] + "'", String.class);
		StateAssert.onSendingLimit(sendStatus);
		// 2017120167  明细为空时 不允许审核  maz
		SqlRowList rs = baseDao.queryForRowSet("select * from QUA_VerifyApplyDetail a where exists(select 1 from QUA_VerifyApplyDetailDet where ved_veid=a.ve_id) and ve_id="+ve_id);
		if(!rs.next()){
			BaseUtil.showError("请先更新明细检验信息再审核!");
		}
		// 执行审核操作
		baseDao.audit("QUA_VerifyApplyDetail", "ve_id=" + ve_id, "ve_status", "ve_statuscode", "ve_auditdate", "ve_auditman");
		baseDao.audit("VerifyApplyDetail", "ve_code='" + status[1] + "'", "ve_status", "ve_statuscode", "ve_auditdate", "ve_auditman");

		baseDao.audit("QUA_VerifyApplyDetailDet", "ved_veid=" + ve_id + " AND nvl(ved_statuscode,' ')<>'TURNIN'", "ved_status",
				"ved_statuscode");
		if (!"".equals(status[2]) && status[2] != null) {
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_testman='" + status[2] + "'", "ved_veid=" + ve_id
					+ " and nvl(ved_testman,' ')=' '");
		} else {
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetailDet",
					"ved_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
							+ ve_id + ") order by ve_date desc) where rownum<2)", "ved_veid=" + ve_id + " and nvl(ved_testman,' ')=' '");
		}
		baseDao.execute("update QUA_VerifyApplyDetail set (ve_brand,ve_oldfactory,ve_factoryspec)=(select pa_brand,pa_addressmark,pa_factoryspec from (select pa_brand,pa_addressmark,pa_factoryspec from ProductApproval where nvl(pa_statuscode,' ')='AUDITED' AND NVL(pa_finalresult,' ')='合格' and (pa_prodcode,pa_providecode) IN (select vad_prodcode,vad_vendcode from qua_verifyapplydetail where ve_id="
				+ ve_id + ") order by pa_auditdate desc) where rownum<2) where ve_id=" + ve_id);
		// 记录操作
		baseDao.logger.audit(caller, "ve_id", ve_id);
		
		// IQC检验单审核后把相关数据反写回收料单中
		Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code", "nvl(vad_qty,0)",
				"ve_samplingngjgqty", "ve_testman" }, "ve_id=" + ve_id);
		Object[] qty = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "nvl(sum(nvl(ved_checkqty,0)),0)",
				"nvl(sum(nvl(ved_okqty,0)),0)", "nvl(sum(nvl(ved_ngqty,0)),0)", "nvl(sum(nvl(ved_samplingqty,0)),0)",
				"nvl(sum(nvl(ved_samplingokqty,0)),0)", "nvl(sum(nvl(ved_samplingngqty,0)),0)" }, "ved_veid=" + ve_id);
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_samplingqty=" + qty[3] + ",ve_samplingokqty=" + qty[4]
				+ ",ve_samplingngqty=" + qty[5], "ve_id=" + ve_id);
		baseDao.updateByCondition("VerifyApplyDetail", "ve_status='" + BaseUtil.getLocalMessage("AUDITED") + "',vad_jyqty=" + qty[0]
				+ ",ve_okqty=" + qty[1] + ",ve_notokqty=" + qty[2] + ", ve_auditdate=sysdate, ve_auditman='"
				+ SystemSession.getUser().getEm_name() + "'" + ",vad_sendstatus='待上传',ve_testman='" + objs[4] + "'", "vad_code='" + objs[1]
				+ "' and vad_detno =" + objs[0]);
		// 明细行的送检数量汇总[sum(ved_checkqty)]已经达到整单来料数[vad_qty]量
		if (Double.parseDouble(objs[2].toString()) == Double.parseDouble(qty[0].toString())) {
			// 判断此次检验结果是否合格，直接更新到检验单主表的检验结果字段
			// ved_samplingngqty qty[5]
			// 更新合格不合格的，根据配置中心配置值
			if (baseDao.isDBSetting(caller, "upQualifiedOrNot")) {
				if (StringUtil.hasText(qty[5]) && StringUtil.hasText(objs[3]) && Double.parseDouble(qty[5].toString()) > Double.parseDouble(objs[3].toString())) {
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_result= '不合格' ", "ve_id=" + ve_id);
				} else {
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_result= '合格' ", "ve_id=" + ve_id);
				}
			}
		}
		if ("VerifyApplyDetail".equals(caller)) {
			// 取配置中心CONFIGS，判断后续的检验方式
			setVerifyMethod(ve_id, caller);
		}
		if ("VerifyApplyDetail!FQC".equals(caller)) {
			// 取配置中心CONFIGS，判断后续的检验方式
			setVerifyMethodFQC(ve_id, caller);
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ve_id);
	}

	@Override
	public void resAuditVerifyApplyDetail(int ve_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		Object detailstatus = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetailDet", "ved_statuscode", "ved_veid=" + ve_id);
		if (!status.equals("AUDITED") && !detailstatus.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// IQC检验单反审核后把相关数据反写回收料单中
		Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code" }, "ve_id="
				+ ve_id);
		// 收料单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("VerifyApplyDetail", "vad_sendstatus", "vad_code='" + objs[1] + "' and vad_detno ="
				+ objs[0], String.class);
		StateAssert.onSendingLimit(sendStatus);
		// 执行反审核操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_statuscode='UNAUDIT',ve_status='" + BaseUtil.getLocalMessage("UNAUDIT")
		+ "', ve_auditdate=null, ve_auditman=null", "ve_id=" + ve_id);
		baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_statuscode='UNAUDIT',ved_status='" + BaseUtil.getLocalMessage("UNAUDIT")
		+ "'", "ved_veid=" + ve_id + " and ved_statuscode='AUDITED'");
		// 记录操作
		baseDao.logger.resAudit(caller, "ve_id", ve_id);

		Object[] qty = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)", "sum(ved_okqty)",
				"sum(ved_ngqty)" }, "ved_veid=" + ve_id + " AND ved_statuscode='TURNIN'");
		Object[] samplingqty = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_samplingqty)",
				"sum(ved_samplingokqty)", "sum(ved_samplingngqty)" }, "ved_veid=" + ve_id + " AND ved_statuscode IN ('TURNIN','AUDITED')");
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_samplingqty=" + samplingqty[0] + ",ve_samplingokqty=" + samplingqty[1]
				+ ",ve_samplingngqty=" + samplingqty[2], "ve_id=" + ve_id);
		baseDao.updateByCondition("VerifyApplyDetail",
				"ve_auditdate=null, ve_auditman=null, ve_status='" + BaseUtil.getLocalMessage("UNAUDIT") + "',vad_jyqty=" + qty[0]
						+ ",ve_okqty=" + qty[1] + ",ve_notokqty=" + qty[2], "vad_code='" + objs[1] + "' and vad_detno =" + objs[0]);
		baseDao.updateByCondition("VerifyApplyDetail", "vad_sendstatus='上传中'", "vad_code='" + objs[1]
				+ "' and vad_sendstatus='已上传' and vad_detno =" + objs[0]);
	}

	@Override
	public void turnMrb(int id, String Qua_code, String caller) {
		Object object = baseDao.getFieldDataByCondition("QUA_MRB", "mr_code", "mr_vecode='" + Qua_code + "'");
		if (object != null) {
			BaseUtil.showError("该检验单已转Mrb单,Mrb单号是:" + object);
		}
		int mr_id = baseDao.getSeqId("QUA_MRB_SEQ");
		String code = baseDao.sGetMaxNumber("MRB", 2);
		String sql = "select ve_code,ve_date,vad_qty,vad_prodcode,vad_vendcode,vad_vendname,ve_ordercode,ve_orderdetno,ve_remark from QUA_VerifyApplyDetail where ve_id="
				+ id;
		String insert_sql = "insert into QUA_MRB(mr_id,mr_code,mr_date,mr_prodcode,mr_vecode,mr_vendcode,mr_vendname,mr_pucode,mr_statuscode,mr_status,mr_inqty,mr_recorder,mr_indate,mr_pudetno,ve_remark)values(?,?,sysdate,?,?,?,?,?,?,?,?,?,sysdate,?,?)";
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			baseDao.execute(insert_sql, new Object[] { mr_id, code, sqlRowList.getString("vad_prodcode"), sqlRowList.getString("ve_code"),
					sqlRowList.getString("vad_vendcode"), sqlRowList.getString("vad_vendname"), sqlRowList.getString("ve_ordercode"),
					"ENTERING", BaseUtil.getLocalMessage("ENTERING"), sqlRowList.getString("vad_qty"),
					SystemSession.getUser().getEm_name(), sqlRowList.getObject("ve_orderdetno"), sqlRowList.getObject("ve_remark") });
			baseDao.execute(
					"update QUA_MRB set (mr_checkqty,mr_ngqty)=(select sum(nvl(ved_samplingqty,0)), sum(nvl(ved_samplingngqty,0)) from QUA_VerifyApplyDetailDet where ved_veid=?) where mr_id=?",
					id, mr_id);
		}
		// 记录操作
		baseDao.logger.turn("转MRB单", caller, "ve_id", id);
	}

	@Override
	public int turnMakeQualityYC(int id, String qua_code, String caller) {
		int mq_id = 0;
		Object object = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_code", "mq_sourcecode='" + qua_code + "'");
		if (object != null) {
			BaseUtil.showError("该检验单已转生产品质异常单,单号是:" + object);
		}
		mq_id = baseDao.getSeqId("MAKEQUALITYYC_SEQ");
		String code = baseDao.sGetMaxNumber("MakeQualityYC", 2);
		String sql = "select * from QUA_VerifyApplyDetail left join Make on ve_ordercode=ma_code where ve_id=" + id;
		String insert_sql = "insert into MakeQualityYC(mq_id,mq_code,mq_checkman,mq_checkdate,mq_ordercode,mq_batch,mq_centre,"
				+ "mq_workcode,mq_checknumb,mq_customer,mq_ngnumb,mq_prodcode,mq_ngrate,mq_recorder,mq_sourcecode,mq_sourceid,"
				+ "mq_status,mq_statuscode,mq_recorddate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ENTERING',sysdate)";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			baseDao.execute(
					insert_sql,
					new Object[] { mq_id, code, rs.getObject("ve_testman"), rs.getObject("ve_date"), rs.getObject("ma_salecode"),
							rs.getObject("ve_batchcode"), rs.getObject("ma_wccode"), rs.getObject("ve_ordercode"), 0,
							rs.getObject("ma_custname"), 0, rs.getObject("vad_prodcode"), 0, SystemSession.getUser().getEm_name(),
							qua_code, id, BaseUtil.getLocalMessage("ENTERING") });
			baseDao.getJdbcTemplate().update(
					"update MakeQualityYC set mq_model=(SELECT pr_spec from product where pr_code=mq_prodcode) where mq_id=?", mq_id);
			baseDao.getJdbcTemplate()
					.update("update MakeQualityYC set (mq_checknumb,mq_ngnumb)=(select sum(nvl(ved_checkqty,0)), sum(nvl(ved_samplingngqty,0)) from qua_verifyapplydetaildet where ved_veid=?) where mq_id=?",
							id, mq_id);
			baseDao.getJdbcTemplate().update(
					"update MakeQualityYC set mq_ngrate=round(mq_ngnumb/mq_checknumb,8) where nvl(mq_checknumb,0) <>0 and mq_id=?", mq_id);
		}
		// 记录操作
		baseDao.logger.turn("转生产品质异常单", caller, "ve_id", id);
		return mq_id;
	}

	@Override
	public void updateVerifyApplyDetailById2(String formStore, String gridStore1, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 只能修改[未审核]的单据资料!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail",
				new String[] { "ve_statuscode", "ve_id", "ve_testman" }, "ve_id=" + store.get("ve_id"));
		if (!"UNAUDIT".equals(status[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore1 });
		// 修改QUA_VerifyApplyDetail
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_VerifyApplyDetail", "ve_id");
		baseDao.execute(formSql);
		// 修改QUA_VerifyApplyDetailDet
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore1, "QUA_VerifyApplyDetailDet", "ved_id");
		for (Map<Object, Object> s : gstore1) {
			if (s.get("ved_id") == null || s.get("ved_id").equals("") || s.get("ved_id").equals("0")
					|| Integer.parseInt(s.get("ved_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUA_VERIFYAPPLYDETAILDET_SEQ");
				s.put("ved_statuscode", "UNAUDIT");
				s.put("ved_status", BaseUtil.getLocalMessage("UNAUDIT"));
				s.put("ved_date", store.get("ve_date"));
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_VerifyApplyDetailDet", new String[] { "ved_id" }, new Object[] { id });
				gridSql.add(sql);
			}else{
				if(baseDao.checkIf("QUA_VerifyApplyDetailDet", "nvl(ved_isok,0)<>0 and nvl(ved_okqty,0)<>"+s.get("ved_okqty")+" and ved_id="+s.get("ved_id"))){
					BaseUtil.showError("明细行"+s.get("ved_detno")+"合格数量入库，不能修改合格数量");
				};
				if(baseDao.checkIf("QUA_VerifyApplyDetailDet", "nvl(ved_isng,0)<>0 and nvl(ved_ngqty,0)<>"+s.get("ved_ngqty")+" and ved_id="+s.get("ved_id"))){
					BaseUtil.showError("明细行"+s.get("ved_detno")+"不合格数量入库，不能修改不合格数量");
				};
			}
		}
		baseDao.execute(gridSql);
		Object testman = store.get("ve_testman");
		if (!"".equals(testman) && testman != null) {
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_testman='" + testman + "'", "ved_veid=" + store.get("ve_id")
					+ " and nvl(ved_testman,' ')=' '");
		} else {
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_testman='" + SystemSession.getUser().getEm_name() + "',ved_testcode='"+SystemSession.getUser().getEm_code()+"'", "ved_veid="
					+ store.get("ve_id") + " and nvl(ved_testman,' ')=' '");
		}
		// 修改QUA_ProjectDet
		gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore2, "QUA_ProjectDet", "vd_id");
		for (Map<Object, Object> s : gstore2) {
			if (s.get("vd_id") == null || s.get("vd_id").equals("") || s.get("vd_id").equals("0")
					|| Integer.parseInt(s.get("vd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUAPROJECTDET_SEQ");
				s.put("vd_class", store.get("ve_class"));
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_ProjectDet", new String[] { "vd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//检验单更新时，要判断数量或检验标准有修改，则重新根据检验单标准更新抽样数、最大不合格允收数
		baseDao.execute("update QUA_VerifyApplyDetail set (ve_samplingaqty, ve_samplingngjgqty)=(select max(nvl(ad_qty,0)), max(nvl(ad_maxngacceptqty,0)) "
				+ "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid AND al_statuscode='AUDITED' and al_code=ve_aql and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
				+ "where ve_id = " + store.get("ve_id"));
		baseDao.execute("update QUA_VerifyApplyDetail set ve_samplingaqty=nvl(vad_qty,0) where nvl(ve_samplingaqty,0)>nvl(vad_qty,0) and ve_id = " + store.get("ve_id"));
		
		// 记录操作
		baseDao.logger.update(caller, "ve_id", store.get("ve_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore1 });
	}

	@Override
	public void deleteVerifyApplyDetail2(int ve_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		if (!status.equals("UNAUDIT") && !status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.delete_onlyEntering"));
		}
		SqlRowList rs = baseDao.queryForRowSet("select ved_statuscode from QUA_VerifyApplyDetailDet where ved_veid=?", ve_id);
		while (rs.next()) {
			if (rs.getString("ved_statuscode").equals("TURNIN")) {
				BaseUtil.showError("明细行已入库，不允许删除!");
			}
		}
		// 转过物料品质异常联络单和转过8D报告单不允许删除
		Object re_vecode = baseDao.getFieldDataByCondition("T8DReport", "re_vecode", "re_veid=" + ve_id);
		Object pa_vecode = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_vecode", "pa_veid=" + ve_id);
		if (StringUtil.hasText(re_vecode) || StringUtil.hasText(pa_vecode)) {
			BaseUtil.showError("该单已转过物料品质异常联络单或者转过8D报告单，不允许删除！");
		}
		// IQC检验单反审核后把相关数据反写回收料单中
		Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code" }, "ve_id="
				+ ve_id);
		// 收料单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("VerifyApplyDetail", "vad_sendstatus", "vad_code='" + objs[1] + "' and vad_detno ="
				+ objs[0], String.class);
		StateAssert.onSendingLimit(sendStatus);
		
		//2018010306号问题反馈   检验单已转入库单后不允许删除     xzx  2018/1/22
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(ved_detno) from ProdIODetail  left join QUA_VerifyApplyDetailDet on ved_id=pd_qcid where ved_veid="+ ve_id, String.class);
		if (dets != null) {
			BaseUtil.showError("检验单第"+dets+"明细行已入库，不允许删除!");
		}
		
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ve_id });
		// 删除QUA_VerifyApplyDetailDet
		baseDao.deleteById("QUA_VerifyApplyDetailDet", "ved_veid", ve_id);
		// 删除QUA_ProjectDet
		baseDao.deleteById("QUA_ProjectDet", "vd_veid", ve_id);
		// 删除QUA_VerifyApplyDetail
		quaVerifyApplyDetailDao.deleteQC(ve_id,caller);
		// 记录操作
		baseDao.logger.delete(caller, "ve_id", ve_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ve_id });
	}

	@Override
	public void catchProject(int veid, int prid) {
		SqlMap map = null;
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_statuscode", "ve_class" }, "ve_id="
				+ veid);
		if (!status[0].equals("UNAUDIT")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.update_onlyEntering"));
		}
		int count = 1;
		SqlRowList rs = baseDao.queryForRowSet("select * from QUA_ProjectDetail left join QUA_CheckItem on pd_ciid=ci_id where pd_prid=?",
				prid);
		while (rs.next()) {
			map = new SqlMap("QUA_ProjectDet");
			map.set("vd_id", baseDao.getSeqId("QUAPROJECTDET_SEQ"));
			map.set("vd_veid", veid);
			map.set("vd_class", status[1]);
			map.set("vd_detno", count++);
			map.set("vd_item", rs.getString("pd_itemcode"));
			map.set("vd_itemname", rs.getObject("pd_itemname"));
			map.set("vd_mrjyyj", rs.getObject("ci_bases"));
			map.set("vd_jyyq", rs.getObject("ci_checkdevice"));
			map.set("vd_jyff", rs.getObject("ci_checkmethod"));
			map.set("vd_unit", rs.getObject("ci_unit"));
			map.execute();
		}
	}

	@Override
	public void cleanProject(int veid) {
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + veid);
		if (!status.equals("UNAUDIT")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.delete_onlyEntering"));
		}
		baseDao.deleteByCondition("QUA_ProjectDet", "vd_veid=" + veid);
	}

	@Override
	public void approveVerifyApplyDetail(int ve_id, String caller) {
		// 只能对状态为[已提交]的单进行批准操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_checkstatuscode", "ve_id=" + ve_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.approve_onlycommited"));
		}
		// 执行批准前的其它逻辑
		handlerService.handler(caller, "approve", "before", new Object[] { ve_id });
		// 执行批准操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail",
				"ve_checkstatuscode='APPROVE',ve_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")
						+ "',ve_checkdate=sysdate, ve_checkman='" + SystemSession.getUser().getEm_name() + "'", "ve_id=" + ve_id);
		// 记录操作
		baseDao.logger.approve(caller, "ve_id", ve_id);
		// 清除 批准流程
		String flowcaller = processService.getFlowCaller(caller);
		if (flowcaller != null) {
			processService.deletePInstance(ve_id, caller, "approve");
		}
		// 执行批准后的其它逻辑
		handlerService.handler(caller, "approve", "after", new Object[] { ve_id });
	}

	@Override
	public void resApproveVerifyApplyDetail(int ve_id, String caller) {
		// 只能对状态为[已提交]的单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_checkstatuscode", "ve_id=" + ve_id);
		if (!status.equals("APPROVE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.statuswrong"));
		}
		// 执行反批准操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail",
				"ve_checkstatuscode='UNAPPROVED',ve_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED") + "'", "ve_id=" + ve_id);
		// 记录操作
		baseDao.logger.resApprove(caller, "ve_id", ve_id);
		handlerService.handler(caller, "resApprove", "after", new Object[] { ve_id });
	}

	@Override
	public void submitVerifyApplyDetail(int ve_id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ve_id });
		// 执行提交操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail",
				"ve_statuscode='COMMITED',ve_status='" + BaseUtil.getLocalMessage("COMMITED")
				+ "',ve_checkdate=sysdate,ve_checkman='" + SystemSession.getUser().getEm_name() + "'", "ve_id=" + ve_id);
		
		// 记录操作
		baseDao.logger.submit(caller, "ve_id", ve_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ve_id });
	}

	@Override
	public void resSubmitVerifyApplyDetail(int ve_id, String caller) {
		// 只能对状态为[已提交]的单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ve_id });
		// 执行反提交操作
		baseDao.updateByCondition("QUA_VerifyApplyDetail",
				"ve_statuscode='UNAUDIT',ve_status='" + BaseUtil.getLocalMessage("UNAUDIT") + "'", "ve_id=" + ve_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ve_id", ve_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ve_id });
	}

	static final String INSERTVERIFYAPPLYDETAILP = "INSERT INTO VerifyApplyDetailP(vadp_id,vadp_vadid,vadp_vacode,vadp_vaddetno,vadp_detno,"
			+ "vadp_qty,vadp_vendcode,vadp_vendname,vadp_prodcode,vadp_batchcode)" + " values (?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String SubpackageDetail(int vad_id, double qty) {
		int barNum = 0;
		double vadqty = 0;
		double remainQty = 0;
		double tqty = 0;
		// 只能删除在录入的采购收料单!
		Object status = baseDao.getFieldDataByCondition("VerifyApply left join VerifyApplyDetail on va_id=vad_vaid", "va_statuscode",
				"vad_id=" + vad_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("只能对已审核的收料单进行分装确认!");
		}
		int count = baseDao.getCountByCondition("VerifyApplyDetailP", "vadp_vadid=" + vad_id);
		if (count > 0) {
			BaseUtil.showError("已经有过分装明细,如果需要重新分装请通过[清除分装明细]按钮先清除后再进行分装!");
		}
		baseDao.execute("update VerifyApplyDetail set vad_unitpackage=? where vad_id=?", qty, vad_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT va_code,va_vendcode,va_vendname,vad_detno,vad_qty,vad_unitpackage,vad_batchcode,vad_prodcode FROM VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_id=?",
						vad_id);
		if (rs.next()) {
			vadqty = rs.getDouble("vad_qty");
			if (qty > 0 && vadqty > 0) {
				barNum = (int) (Math.ceil(vadqty / qty));
				remainQty = vadqty;
				for (int i = 1; i <= barNum; i++) {
					if (remainQty >= qty) {
						tqty = qty;
					} else {
						tqty = remainQty;
					}
					baseDao.execute(
							INSERTVERIFYAPPLYDETAILP,
							new Object[] { baseDao.getSeqId("VERIFYAPPLYDETAILP_SEQ"), vad_id, rs.getObject("va_code"),
									rs.getGeneralInt("vad_detno"), i, tqty, rs.getObject("va_vendcode"), rs.getObject("va_vendname"),
									rs.getObject("vad_prodcode"), rs.getObject("vad_batchcode") });
					remainQty = remainQty - tqty;
					if (remainQty <= 0) {
						break;
					}
				}
			}
		}
		return "分装确认成功!";
	}

	@Override
	public String ClearSubpackageDetail(int vad_id) {
		baseDao.execute("delete from VerifyApplyDetailP where vadp_vadid=" + vad_id);
		return "清除分装明细成功!";
	}

	@Override
	public String PrintBarDetail(int vad_id) {
		double vadpsumqty = 0;
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT vad_code,vad_detno,round(vad_qty,2),vad_unitpackage FROM VerifyApplyDetail WHERE where vad_id=?", vad_id);
		if (rs.next()) {
			vadpsumqty = Double.parseDouble(baseDao.getFieldDataByCondition("VerifyApplyDetailP", "round(sum(vadp_qty),2)",
					"vadp_vadid=" + vad_id).toString());
			if (rs.getDouble(3) != vadpsumqty) {
				BaseUtil.showError("当前序号" + rs.getObject("vad_detno") + "的收料数量与分装明细总数不等,不能打印条码!");
			}
		}
		return "打印条码成功!";
	}

	/**
	 * 
	 * @param ve_id
	 *            当前的AQL
	 * @param caller
	 */
	private void setVerifyMethod(int ve_id, String caller) {
		SqlRowList ve = baseDao
				.queryForRowSet(
						"select ve_method,vad_prodcode,vad_vendcode,pr_id from qua_verifyapplydetail left join product on pr_code=vad_prodcode where ve_id=? and ve_method is not null",
						ve_id);
		if (ve.next()) {
			String ve_method = ve.getString("ve_method");
			String vad_prodcode = ve.getString("vad_prodcode");
			String vad_vendcode = ve.getString("vad_vendcode");
			int pr_id = ve.getGeneralInt("pr_id");
			// 1、免检 ，2、放宽，3 、正常，4、加严，5、全检。
			SqlRowList rs;
			int count = 0;
			String mod;
			boolean turn = true;
			Object pv_method = baseDao.getFieldDataByCondition("ProductVendorIQC", "pv_method", " pv_vendcode='" + vad_vendcode
					+ "' and pv_prodcode='" + vad_prodcode + "'");
			if (pv_method == null) {
				baseDao.execute("insert into ProductVendorIQC(pv_id,pv_prodcode,pv_vendcode,pv_method)values(ProductVendorIQC_seq.nextval,'"
						+ vad_prodcode + "','" + vad_vendcode + "','正常抽检')");
				baseDao.execute("update ProductVendorIQC set pv_prodid=(select pr_id from product where pr_code=pv_prodcode) where pv_prodcode='"
						+ vad_prodcode + "'");
				baseDao.execute("update ProductVendorIQC set (pv_vendid,pv_vendname)=(select ve_id,ve_name from vendor where ve_code=pv_vendcode) where pv_prodcode='"
						+ vad_prodcode + "'");
			}
			// 是否连续超过n批的判断语句
			String sql = "select count(1) allqty,sum(isok) okqty from "
					+ " (select ve_code,ve_date, (case when ve_result='合格' then 1 else 0 end )isok ,rank() over (order by ve_id desc) detno "
					+ " from qua_verifyapplydetail " + " where vad_prodcode=? and vad_vendcode=? " + " and ve_statuscode='AUDITED' )"
					+ " where detno<=?";
			// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名
			String sql3 = "update ProductVendorIQC set pv_method=?,pv_ifdelete=? where PV_VENDCODE=? and pv_prodid=?";
			if (ve_method.equals("放宽抽检")) {
				// 放宽转正常条件[连续n批不合格]
				mod = baseDao.getDBSetting(caller, "relaxToNormal");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				// 再判断结果里面allqty是否=3，如果达到三次了，再判断okqty的值，合格的1，不合格的0
				rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralInt("okqty") == 0) {
							// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
							baseDao.execute(sql3, "正常抽检", "0", vad_vendcode, pr_id);
							turn = true;
						}
					}
				}
				if (!turn) {
					// 放宽转免检条件：连续n批合格；
					mod = baseDao.getDBSetting(caller, "relaxToExemption");
					if (mod != null) {
						count = Integer.parseInt(mod);
					}
					rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
					if (rs.next()) {
						if (rs.getGeneralInt("allqty") == count) {
							if (rs.getGeneralInt("okqty") == count) {
								// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
								baseDao.execute(sql3, "免检", "0", vad_vendcode, pr_id);
							}
						}
					}
				}
			} else if (ve_method.equals("正常抽检")) {
				// 正常转加严条件：连续n批不合格；
				mod = baseDao.getDBSetting(caller, "normalToStrict");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralInt("okqty") == 0) {
							// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
							baseDao.execute(sql3, "加严抽检", "0", vad_vendcode, pr_id);
							turn = true;
						}
					}
				}
				if (!turn) {
					// 正常转放宽条件：连续n批合格；
					mod = baseDao.getDBSetting(caller, "normalToRelax");
					if (mod != null) {
						count = Integer.parseInt(mod);
					}
					rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
					if (rs.next()) {
						if (rs.getGeneralInt("allqty") == count) {
							if (rs.getGeneralInt("okqty") == count) {
								// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
								baseDao.execute(sql3, "放宽抽检", "0", vad_vendcode, pr_id);
							}
						}
					}
				}
			} else if (ve_method.equals("加严抽检") || ve_method.equals("全检")) {
				// 除名条件：连续n批加严或全检不合格；
				mod = baseDao.getDBSetting(caller, "delete");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
				// 如果设置n=5，那么OKQTY=0 ，并且allqty=5，就可以判定要除名
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralDouble("okqty") == 0) {
							// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
							baseDao.execute(sql3, ve_method, "-1", vad_vendcode, pr_id);
						}
					}
				}
				if (!turn) {
					// 加严转正常条件：连续n批合格；
					mod = baseDao.getDBSetting(caller, "strictToNormal");
					if (mod != null) {
						count = Integer.parseInt(mod);
					}
					rs = baseDao.queryForRowSet(sql, vad_prodcode, vad_vendcode, count);
					if (rs.next()) {
						if (rs.getGeneralInt("allqty") == count) {
							if (rs.getGeneralInt("okqty") == count) {
								// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
								baseDao.execute(sql3, "正常抽检", "0", vad_vendcode, pr_id);
							}
						}
					}
				}
			}
		}
	}

	static final String CHECKQTY = "SELECT ve_code,vad_qty FROM Qua_verifyApplyDetail WHERE ve_id=? and vad_qty<?";

	@Override
	public void updateQty(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		Object ved_okqty = formdata.get("ved_okqty");
		Object ved_ngqty = formdata.get("ved_ngqty");
		Object caller = formdata.get("caller");
		Object ve_id = formdata.get("ved_veid");
		SqlRowList rs = null;
		Object checkqty = 0;
		checkqty = baseDao.getFieldDataByCondition("Qua_verifyApplyDetailDet", "sum(ved_checkqty)", "ved_veid=" + ve_id + " AND ved_id <>"
				+ formdata.get("ved_id"));
		checkqty = checkqty == null ? 0 : checkqty;
		rs = baseDao.queryForRowSet(CHECKQTY, ve_id, Double.parseDouble(checkqty.toString()) + Double.parseDouble(ved_okqty.toString())
				+ Double.parseDouble(ved_ngqty.toString()));
		if (rs.next()) {
			BaseUtil.showError("送检数量之和不能大于收料数量!");
		}
		rs = baseDao.queryForRowSet("select ved_detno,ved_okqty,ved_ngqty,ved_veid from Qua_verifyApplyDetailDet where ved_id=?",
				formdata.get("ved_id"));
		if (rs.next()) {
			baseDao.execute("update Qua_verifyApplyDetailDet set ved_okqty=" + ved_okqty + ",ved_ngqty=" + ved_ngqty + ",ved_checkqty="
					+ (Double.parseDouble(ved_okqty.toString()) + Double.parseDouble(ved_ngqty.toString())) + ", ved_testman='"
					+ SystemSession.getUser().getEm_name() + "',ved_testcode='" + SystemSession.getUser().getEm_code() + "', ved_nrcode='"
					+ formdata.get("ved_nrcode") + "', ved_ngdeal='" + formdata.get("ved_ngdeal") + "', ved_remark='"
					+ formdata.get("ved_remark") + "',ved_checkdate=sysdate where ved_id=" + formdata.get("ved_id"));
			baseDao.execute("update Qua_verifyApplyDetailDet set ved_date=(select ve_date from Qua_verifyApplyDetail where ved_veid=ve_id) where ved_id="
					+ formdata.get("ved_id"));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新明细数量", "行" + rs.getObject("ved_detno") + ":"
					+ rs.getObject("ved_okqty") + "=>" + ved_okqty + "," + rs.getObject("ved_ngqty") + "=>" + ved_ngqty, caller + "|ve_id="
					+ rs.getGeneralInt("ved_veid")));
		}
		// IQC检验单数量更新后把相关数据反写回收料单中
		Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code", "vad_qty",
				"ve_samplingngjgqty", "ve_testman" }, "ve_id=" + ve_id);
		Object[] qty = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)", "sum(ved_okqty)",
				"sum(ved_ngqty)", "sum(ved_samplingqty)", "sum(ved_samplingokqty)", "sum(ved_samplingngqty)" }, "ved_veid=" + ve_id);
		baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_samplingqty=" + qty[3] + ",ve_samplingokqty=" + qty[4]
				+ ",ve_samplingngqty=" + qty[5], "ve_id=" + ve_id);
		baseDao.updateByCondition("VerifyApplyDetail", "ve_status='" + BaseUtil.getLocalMessage("AUDITED") + "',vad_jyqty=" + qty[0]
				+ ",ve_okqty=" + qty[1] + ",ve_notokqty=" + qty[2] + ", ve_auditdate=sysdate, ve_auditman='"
				+ SystemSession.getUser().getEm_name() + "'" + ",ve_testman='" + objs[4] + "'", "vad_code='" + objs[1]
				+ "' and vad_detno =" + objs[0]);
		// 明细行的送检数量汇总[sum(ved_checkqty)]已经达到整单来料数[vad_qty]量
		if (Integer.parseInt(objs[2].toString()) == Integer.parseInt(qty[0].toString())) {
			// 则根据本单的AQL方案，和对应的抽样标准,
			// 判断此次检验结果是否合格，直接更新到检验单主表的检验结果字段
			// ved_samplingngqty qty[5]
			// 更新合格不合格的，根据配置中心配置值
			boolean upQ = baseDao.isDBSetting("VerifyApplyDetail", "upQualifiedOrNot");
			if (upQ) {
				if (Integer.parseInt(qty[5].toString()) > Integer.parseInt(objs[3].toString())) {
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_result= '不合格' ", "ve_id=" + ve_id);
				} else {
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_result= '合格' ", "ve_id=" + ve_id);
				}
			}
		}
	}

	@Override
	public void updateWhCodeInfo(String caller, String data) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		String updatecondition = "vad_id=" + map.get("vad_id");
		if (map.get("isalldetail").equals(true)) {
			updatecondition = "vad_vaid=" + map.get("vad_vaid");
		}
		baseDao.updateByCondition("VERIFYAPPLYDetail", "vad_whcode='" + map.get("whcode") + "',vad_whname='" + map.get("whname") + "'",
				updatecondition);
		baseDao.logger.others("修改仓库", "修改成功", caller, "vad_id", map.get("vad_vaid"));
	}

	@Override
	public int turnProdAbnormal(int id, String caller) {
		int pa_id = 0;
		// 判断是否已经转入过物料品质异常联络单
		Object code = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_vecode", "pa_veid=" + id);
		if (StringUtil.hasText(code)) {
			BaseUtil.showError("已转入过物料品质异常联络单！单号：" + code);
		} else {
			// 转物料品质异常联络单
			pa_id = quaVerifyApplyDetailDao.turnProdAbnormal(id);
			baseDao.logger.others("转物料品质异常联络单", "转入成功", caller, "ve_id", id);
		}
		return pa_id;
	}

	@Override
	public int turnT8DReport(int id, String caller) {
		int re_id = 0;
		// 判断是否已经转入过8D报告
		Object code = baseDao.getFieldDataByCondition("T8DReport", "re_vecode", "re_veid=" + id);
		if (StringUtil.hasText(code)) {
			BaseUtil.showError("已转入过8D报告！单号：" + code);
		} else {
			// 转物料品质异常联络单
			re_id = quaVerifyApplyDetailDao.turnT8DReport(id);
			baseDao.logger.others("转8D报告", "转入成功", caller, "ve_id", id);
		}
		return re_id;
	}
	/**
	 * FQC再次送检 maz 锤子科技
	 */
	@Override
	public String InspectAgain(String ve_code,int ve_id){
		Map<String, Object> diffence = new HashMap<String, Object>();
		Object sign = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_criqty", "ve_id="+ve_id);
		if("-1".equals(sign)){
			BaseUtil.showError("该张检验单设置为不良品入库,不能再次送检");
		}
		Employee employee = SystemSession.getUser();
		Object ve_inspect = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_inspect", "ve_id="+ve_id);
		String code = ve_code+"-R"+(Integer.parseInt(ve_inspect.toString())+1);
		Object ngqty = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetailDet", "sum(ved_ngqty)", "ved_veid="+ve_id);
		int veid = baseDao.getSeqId("QUA_VerifyApplyDetail_SEQ");
		diffence.put("ve_code", "'" + code + "'");
		diffence.put("ve_id", veid);
		diffence.put("vad_qty", ngqty);
		diffence.put("ve_inspect", 0);
		diffence.put("ve_indate", "sysdate");
		diffence.put("ve_recorder", "'" + employee.getEm_name() + "'");
		diffence.put("ve_statuscode", "'UNAUDIT'");
		diffence.put("ve_status", "'" + BaseUtil.getLocalMessage("UNAUDIT") + "'");
		diffence.put("ve_auditman", null);
		diffence.put("ve_auditdate", null);
		diffence.put("ve_result", null);
		diffence.put("vad_sourcecode", "'" + ve_code + "'");
		baseDao.execute("update QUA_VerifyApplyDetail set ve_inspect=ve_inspect+1 where ve_id="+ve_id);
		baseDao.copyRecord("QUA_VerifyApplyDetail", "QUA_VerifyApplyDetail", "ve_id=" + ve_id, diffence);
		return "再次送检成功,检验单号:" + "<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetail.jsp?whoami=VerifyApplyDetail!FQC&formCondition=" + "ve_idIS" + veid
				+ "&gridCondition=ved_veidIS" + veid + "')\">" + code + "</a>&nbsp;<hr> ";
	}
	/*
	 * 天派FQC检验方式  2018040263  maz 
	 */
	private void setVerifyMethodFQC(int ve_id, String caller) {
		SqlRowList ve = baseDao
				.queryForRowSet(
						"select ve_method,vad_prodcode,ve_ordercode,pr_id from qua_verifyapplydetail left join product on pr_code=vad_prodcode where ve_id=? and ve_method is not null",
						ve_id);
		if (ve.next()) {
			String ve_method = ve.getString("ve_method");
			String vad_prodcode = ve.getString("vad_prodcode");
			String ve_ordercode = ve.getString("ve_ordercode");
			// 1、正常检验，2、加严检验，3 、对策后检验
			SqlRowList rs;
			int count = 0;
			String mod;
			boolean turn = true;
			// 是否连续超过n批的判断语句
			String sql = "select count(1) allqty,sum(isok) okqty from "
					+ " (select ve_code,ve_date, (case when ve_result='合格' then 1 else 0 end )isok ,rank() over (order by ve_id desc) detno "
					+ " from qua_verifyapplydetail " + " where vad_prodcode=? and ve_ordercode=? " + " and ve_statuscode='AUDITED' )"
					+ " where detno<=?";
			// 更新FQC的ve_method检验方式
			String sql3 = "update QUA_VerifyApplyDetail set ve_method=? where ve_id=?";
			if (ve_method.equals("正常抽检")) {
				// 正常转加严条件：连续n批不合格；
				mod = baseDao.getDBSetting(caller, "normalToStrict");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				rs = baseDao.queryForRowSet(sql, vad_prodcode, ve_ordercode, count);
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralInt("okqty") == 0) {
							baseDao.execute(sql3, "加严抽检", ve_id);
						}
					}
				}
			} else if (ve_method.equals("加严抽检")) {
				// 除名条件：连续n批加严直接转对策
				mod = baseDao.getDBSetting(caller, "strictToHard");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				rs = baseDao.queryForRowSet(sql, vad_prodcode, ve_ordercode, count);
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralDouble("okqty") == 0) {
							baseDao.execute(sql3, "对策后检验",ve_id);
							turn = false;
						}
					}
				}
				if (turn) {
					// 加严转正常条件：连续n批合格；
					mod = baseDao.getDBSetting(caller, "strictToNormal");
					if (mod != null) {
						count = Integer.parseInt(mod);
					}
					rs = baseDao.queryForRowSet(sql, vad_prodcode, ve_ordercode, count);
					if (rs.next()) {
						if (rs.getGeneralInt("allqty") == count) {
							if (rs.getGeneralInt("okqty") == count) {
								// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
								baseDao.execute(sql3, "正常抽检", ve_id);
							}
						}
					}
				}
			}else if (ve_method.equals("对策后检验")) {
				// 对策转加严条件：连续n批合格；
				mod = baseDao.getDBSetting(caller, "hardToStrict");
				if (mod != null) {
					count = Integer.parseInt(mod);
				}
				rs = baseDao.queryForRowSet(sql, vad_prodcode, ve_ordercode, count);
				if (rs.next()) {
					if (rs.getGeneralInt("allqty") == count) {
						if (rs.getGeneralInt("okqty") == count) {
							// 更新ProductVendorIQC表的pv_method建议检验方式和pv_ifdelete是否除名。
							baseDao.execute(sql3, "加严抽检",ve_id);
						}
					}
				}
			}
		}
	}
}
