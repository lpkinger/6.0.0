package com.uas.erp.service.oa.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;

@Service("OAHandler")
public class OAHandler {
	@Autowired
	private BaseDao baseDao;

	/**
	 * 差旅费报销单 提交前：来源单据为出差申请单时限制主表报销金额小于等于出差申请单实际报销金额（fp_pleaseamount）
	 */
	public void feeplease_commit_pleaseamountCheck(Integer fp_id) {
		Object[] ob = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_sourcekind", "fp_sourcecode",
				"nvl(Fp_Pleaseamount,0)" }, "fp_id=" + fp_id);
		if ("出差申请单".equals(ob[0])) {
			int f1 = Integer.parseInt(ob[2].toString());// 差旅费报销单报销金额
			Object f2 = baseDao.getFieldDataByCondition("feeplease", "nvl(Fp_Pleaseamount,0)", "fp_code='" + ob[1]
					+ "' and fp_kind='出差申请单'");// 出差申请单 实际报销金额
			if (f1 > Integer.parseInt(f2.toString())) {
				BaseUtil.showError("报销金额大于来源出差申请单的申请金额，不能提交！申请金额:" + f2);
			}
		}
	}

	/**
	 * 出差申请单保存时更新明细表合计到主表 明细表：fpd_n1交通费 fpd_n2 膳食费 fpd_n3 住宿费 fpd_n4 补贴费
	 * fpd_n7其他费用 主表 ： fp_n2 交通费 fp_n4膳食费 fp_n3住宿费 fp_n1 补贴费 fp_n5其他费用
	 * fp_pleaseamount合计
	 */
	public void feeplease_save_updatetotal(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		int fp_id = Integer.parseInt(store.get("fp_id") + "");
		String sql = "update FeePlease set (fp_n2,fp_n4,fp_n3,fp_n1,fp_n5)=(select nvl(sum(fpd_n1),0),nvl(sum(fpd_n2),0),nvl(sum(fpd_n3),0),nvl(sum(fpd_n4),0),nvl(sum(fpd_n7),0) from FeePleaseDetail where fpd_fpid="
				+ fp_id + ") where fp_id=" + fp_id;
		baseDao.execute(sql);
		String sql1 = "update FeePlease set fp_pleaseamount=nvl(fp_n2,0)+nvl(fp_n4,0)+nvl(fp_n3,0)+nvl(fp_n1,0)+nvl(fp_n5,0) where fp_id="
				+ fp_id;
		baseDao.execute(sql1);
	}

	/**
	 * 行车目的地在使用中，限制删除
	 */
	public void vehicleDestination_delete_vaarea(Integer vd_id) {
		int count = baseDao
				.getCount("select count(1) from Vehicleapply where va_area=(select vd_area from VehicleDestination where vd_id='" + vd_id
						+ "')");
		if (count != 0) {
			BaseUtil.showError("此目的地已在派车申请单中被使用，不能删除！");
		}
	}

	/**
	 * 新闻类型在使用中，限制删除
	 */
	public void newsKind_delete_before(Integer id) {
		int count = baseDao.getCount("select count(1) from News where ne_nkid=" + id);
		if (count != 0) {
			BaseUtil.showError("新闻类型已被使用，不能删除！");
		}
	}

	/**
	 * Sale->sale->commit 销售政策不存在!
	 */
	public void feeClaim_commit_checkSdcode(Integer fc_id) {

	}

	/**
	 * feeplease 审核后才能打印!
	 */
	public void feePlease_print(Integer fp_id) {
		Object statusCode = baseDao.getFieldDataByCondition("feePlease", "fp_statuscode", "fp_id=" + fp_id);
		if (!"AUDITED".equals(statusCode.toString())) {
			BaseUtil.showError("单据审核后才能打印!");
		}
	}

	/**
	 * oa->meeting->saveorupdate 会议室申请保存或更新时发寻呼给与会成员
	 */
	public void meetingroomapply_saveorupdate_notify(HashMap<Object, Object> store) {
		List<Object[]> gridDate = baseDao.getFieldsDatasByCondition("MeetingDetail", new String[] { "md_participantsid", "md_participants",
				"md_isnoticed" }, "md_maid=" + store.get("ma_id"));
		// 发布寻呼信息
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = null;
		Object[] meetings = baseDao.getFieldsDataByCondition("Meetingroomapply", new String[] { "ma_recorder", "ma_theme" }, "ma_id="
				+ store.get("ma_id"));
		String url = "jsps/oa/meeting/meetingroomapply.jsp";
		String formCondition = "ma_id=" + store.get("ma_id");
		String gridCondition = "md_maid=" + store.get("ma_id");
		if (meetings != null && meetings[0] != null && meetings[1] != null) {
			sb = new StringBuffer();
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			sb.append(meetings[0] + "发起了<a href=\"javascript:openUrl(''" + url + "?formCondition=" + formCondition + "&gridCondition="
					+ gridCondition + "'')\" style=\"font-size:14px; color:blue;\">" + meetings[1] + "</a>会议</br>");
			sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_istop,pr_from)values('" + pr_id
					+ "','" + SystemSession.getUser().getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
					+ ",'" + SystemSession.getUser().getEm_id() + "','" + sb.toString() + "',1,'meeting')");
			for (Object[] s : gridDate) {
				if ("-1".equals(s[2] + "")) {// 需要发通知才发送
					sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(PAGINGRELEASEDETAIL_SEQ.NEXTVAL,'"
							+ pr_id + "','" + s[0] + "','" + s[1] + "')");
				}
			}
			// 保存到历史消息表
			int IH_ID = baseDao.getSeqId("ICQHISTORY_SEQ");
			sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "
					+ IH_ID
					+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id=" + pr_id);
			sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
					+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + pr_id + "and (" + IH_ID
					+ ",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			baseDao.execute(sqls);
		}
	}

	/**
	 * oa->meeting->audit 会议室申请审核时发寻呼给与会成员
	 * 
	 */
	public void meetingroomapply_auditafter_notify(Integer id) {
		List<Object[]> gridDate = baseDao.getFieldsDatasByCondition("MeetingDetail", new String[] { "md_participantsid", "md_participants",
				"md_isnoticed" }, "md_maid=" + id);
		// 发布寻呼信息
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = null;
		Object[] meetings = baseDao.getFieldsDataByCondition("Meetingroomapply", new String[] { "ma_recorder", "ma_theme" }, "ma_id=" + id);
		String url = "jsps/oa/meeting/meetingroomapply.jsp";
		String formCondition = "ma_id=" + id;
		String gridCondition = "md_maid=" + id;
		if (meetings != null && meetings[0] != null && meetings[1] != null) {
			sb = new StringBuffer();
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			sb.append(meetings[0] + "发起了<a href=\"javascript:openUrl(''" + url + "?formCondition=" + formCondition + "&gridCondition="
					+ gridCondition + "'')\" style=\"font-size:14px; color:blue;\">" + meetings[1] + "</a>会议</br>");
			sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_istop,pr_from)values('" + pr_id
					+ "','" + SystemSession.getUser().getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
					+ ",'" + SystemSession.getUser().getEm_id() + "','" + sb.toString() + "',1,'meeting')");
			for (Object[] s : gridDate) {
				if ("-1".equals(s[2] + "")) {// 需要发通知才发送
					sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(PAGINGRELEASEDETAIL_SEQ.NEXTVAL,'"
							+ pr_id + "','" + s[0] + "','" + s[1] + "')");
				}
			}
			// 保存到历史消息表
			int IH_ID = baseDao.getSeqId("ICQHISTORY_SEQ");
			sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "
					+ IH_ID
					+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id=" + pr_id);
			sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
					+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + pr_id + "and (" + IH_ID
					+ ",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			baseDao.execute(sqls);
		}
	}

	/**
	 * oa->meeting->audit 会议室申请保存或更新时以任务的形式通知成员（恒晨）
	 * 
	 */
	public void meetingroomapply_saveorupdate_notify_hcn(HashMap<Object, Object> store) {
		Object id = store.get("ma_id");
		Object ma_code = baseDao.getFieldDataByCondition("Meetingroomapply", "ma_code", "ma_id=" + id);
		List<Object[]> gridDate = baseDao.getFieldsDatasByCondition("MeetingDetail", new String[] { "md_participantsid", "md_participants",
				"md_isnoticed" }, "md_meid=" + id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] s : gridDate) {
			if ("-1".equals(s[2] + "")) {// 需要发通知才发送
				int taskid = baseDao.getSeqId("PROJECTTASK_SEQ");
				String taskcode = baseDao.sGetMaxNumber("ProjectTask", 2);
				String link = "jsps/oa/meeting/meetingroomapply.jsp?formCondition=ma_idIS" + id + "&gridCondition=mad_maidIS" + id;
				Object[] emp = baseDao
						.getFieldsDataByCondition("Employee", new String[] { "em_id", "em_code", "em_name" }, "em_id=" + s[0]);
				String sql1 = "insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,class,"
						+ "recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink)" + " values (" + taskid
						+ ",'会议通知','normal','已启动','DOING','已审核','AUDITED',"
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'billtask','"
						+ SystemSession.getUser().getEm_name() + "','" + emp[1] + "','" + emp[2] + "','" + emp[0] + "','" + taskcode
						+ "','" + ma_code + "','" + link + "')";
				int ra_id = baseDao.getSeqId("resourceassignment_seq");
				String sql2 = "insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
						+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values " + "(" + ra_id + "," + taskid + ",'" + emp[0]
						+ "','" + emp[1] + "','" + emp[2] + "',1,'进行中','START',100,'billtask','会议通知')";
				sqls.add(sql2);
				sqls.add(sql1);
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * oa->Vehicle->saveorupdate 返车单填写时，默认车辆为使用状态(恒晨)
	 */
	public void Vehiclereturn_saveorupdate_status(HashMap<Object, Object> store) {
		baseDao.updateByCondition("Vehiclearchives", "va_isused='使用中'", "va_code='" + store.get("vr_vecard") + "'");
	}

	/**
	 * oa->Vehicle->auditafter 返车单填写时，默认车辆为空闲状态(恒晨)
	 * 
	 */
	public void Vehiclereturn_auditafter_status(Integer id) {
		Object carcode = baseDao.getFieldDataByCondition("Vehiclereturn", "vr_vecard", "vr_id=" + id);
		baseDao.updateByCondition("Vehiclearchives", "va_isused='空闲中'", "va_code='" + carcode + "'");

	}

	/**
	 * oa->fee->feeplease 差旅费报销单,费用报销单：提交之前判断费用科目是否正确,如果正确则更新fpd_catecode字段
	 */
	public void feePlease_commit_before_correct(Integer fp_id) {
		int count = baseDao.getCount("select count(1) from feeplease where fp_pleaseamount=0 and fp_id=" + fp_id);
		if (count != 0) {
			BaseUtil.showError("申请金额为0，不能提交");
		}
		StringBuffer sb = new StringBuffer();
		Object dept = baseDao.getFieldDataByCondition("feeplease", "fp_department", "fp_id=" + fp_id);
		SqlRowList rs1 = baseDao.queryForRowSet(
				"select fpd_detno,fpd_d1,fpd_id,fp_department from FeePleaseDetail,feeplease where fp_id=fpd_fpid and fpd_fpid=?", fp_id);
		while (rs1.next()) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fcs_departmentname from FeeCategorySet where fcs_departmentname=? and fcs_itemname=?", dept,
					rs1.getObject("fpd_d1"));
			if (!rs.hasNext()) {
				sb.append("第" + rs1.getObject("fpd_detno") + "行部门[" + dept + "]费用用途[" + rs1.getObject("fpd_d1") + "]在费用申请科目没有设置，不能提交");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute("update FeePleaseDetail set fpd_catecode=(select max(fcs_catecode) from FeeCategorySet where fcs_departmentname='"
				+ dept + "' and fcs_itemname=fpd_d1 and nvl(fcs_nature,' ')=nvl(fpd_nature,' ')) where fpd_fpid=" + fp_id);
	}

	/**
	 * oa->fee->feeplease 差旅费报销单:如果截止日期fp_enddate大于fp_recorddate
	 * 15天则提示“出差超过15天不能报销“，不允许提交
	 */
	public void feePlease_commit_before_countdate(Integer fp_id) {
		int count = baseDao.getCount("select count(1) from feeplease where fp_enddate-fp_recorddate>15 and fp_id=" + fp_id);
		if (count != 0) {
			BaseUtil.showError("出差超过15天不能报销");
		}
	}

	/**
	 * oa->fee->feeplease 费用报销修改不能超过来源申请易方专用
	 */
	public void feePlease_commit_before_amount(Integer fp_id) {
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_sourcekind", "fp_sourcecode", "fp_pleaseamount" },
				"fp_id=" + fp_id);
		Object kind = data[0];
		Object sourcecode = data[1];
		Object amount = null;
		if ("客户拜访记录".equals(kind)) {

		} else if ("资产维修".equals(kind)) {
			amount = baseDao.getFieldDataByCondition("Propertyrepair", "pr_amount", "pr_code='" + sourcecode + "'");
		} else if ("车辆维修".equals(kind)) {
			amount = baseDao.getFieldDataByCondition("vehiclearchivesdetail", "vd_cost+vd_upkeep", "vd_code='" + sourcecode + "'");
		} else if ("费用申请".equals(kind)) {
			amount = baseDao.getFieldDataByCondition("preFeePlease", "fp_pleaseamount", "fp_code='" + sourcecode + "'");
		}
		if (Double.parseDouble(amount.toString()) > 0 && Double.parseDouble(amount.toString()) < Double.parseDouble(data[2].toString())) {
			BaseUtil.showError("有来源的费用报销,金额不能超过来源单金额!");
		}
	}

	/**
	 * oa->fee->feeplease 费用报销单:是否超额度
	 */
	public void feePlease_commit_before_countlimit(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_pleaseman", "fp_class", "fp_departmentcode", "nvl(fp_pleaseamount,0)", "to_char(fp_recorddate,'yyyymm')", "fp_v13" },
				"fp_id=" + fp_id);
		Object[] datas = null;
		String errorString = null;
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		String master = SystemSession.getUser().getEm_master();
		if (defaultSource != null && !defaultSource.equals("")) {
			boolean bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				datas = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind", new String[] { "fk_controlway", "fk_iscontrol" },
						"fk_name='" + data[3] + "'");
			} else {
				datas = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='" + data[3]
						+ "'");
			}
			String yd = data[1] == null ? data[6].toString() : data[1].toString();
			if (datas != null) {
				if ("是".equals(datas[1])) {
					if ("EMP".equals(datas[0]) || "个人".equals(datas[0])) {
						if (bool) {
							SqlRowList rs = baseDao
									.queryForRowSet(
											"select nvl(fld_amount,0) fld_amount, nvl(fld_actamount,0) fld_actamount from "
													+ defaultSource
													+ ".FeeLimitDetail left join "
													+ defaultSource
													+ ".FeeLimit on fl_id=fld_flid where fl_yearmonth=? and fld_emcode=? and fld_class=? and fl_statuscode='AUDITED' ",
											yd, data[0], data[3]);
							if (rs.next()) {// 如果有设置额度则检查是否超额度
								Double amount = Double.parseDouble(data[5].toString());
								if (!defaultCurrency.equals(data[7])) {
									Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
											+ "' and cm_crname='" + data[7] + "'");
									if (cm_crrate != null && !cm_crrate.equals("")) {
										amount = amount * Double.parseDouble(cm_crrate.toString());
									}
								}
								if (rs.getGeneralDouble("fld_amount") < rs.getGeneralDouble("fld_actamount") + amount) {
									errorString = "额度类型<" + data[3] + ">申请金额已超本月额度！余额为："
											+ (rs.getGeneralDouble("fld_amount") - rs.getGeneralDouble("fld_actamount"));
								}
							} else {
								errorString = "额度表没有相应记录，不予提交!";
							}
						} else {
							SqlRowList rs = baseDao
									.queryForRowSet(
											"select nvl(fld_amount,0) fld_amount, nvl(fld_actamount,0) fld_actamount from FeeLimitDetail left join FeeLimit on fl_id=fld_flid where fl_yearmonth=? and fld_emcode=? and fld_class=? and fl_statuscode='AUDITED' ",
											yd, data[0], data[3]);
							if (rs.next()) {// 如果有设置额度则检查是否超额度
								Double amount = Double.parseDouble(data[5].toString());
								/*
								 * if(!defaultCurrency.equals(data[7])){//去掉
								 * Object cm_crrate =
								 * baseDao.getFieldDataByCondition
								 * ("CurrencysMonth", "cm_crrate",
								 * "cm_yearmonth='"
								 * +yd+"' and cm_crname='"+data[7]+"'");
								 * if(cm_crrate!=null && !cm_crrate.equals("")){
								 * amount =
								 * amount*Double.parseDouble(cm_crrate.toString
								 * ()); } }
								 */
								if (rs.getGeneralDouble("fld_amount") < rs.getGeneralDouble("fld_actamount") + amount) {
									errorString = "额度类型<" + data[3] + ">申请金额已超本月额度！余额为："
											+ (rs.getGeneralDouble("fld_amount") - rs.getGeneralDouble("fld_actamount"));
								}
							} else {
								errorString = "额度表没有相应记录，不予提交!";
							}
						}
					}
					if ("DEP".equals(datas[0]) || "部门".equals(datas[0])) {
						if (bool) {
							SqlRowList rs1 = baseDao
									.queryForRowSet(
											// wsy code
											"select nvl(fld_amount,0) fld_amount, nvl(fld_actamount,0) fld_actamount from "
													+ defaultSource
													+ ".FeeLimitDetail left join "
													+ defaultSource
													+ ".FeeLimit on fl_id=fld_flid where fl_yearmonth=? and fld_departmentcode=? and fld_class=? and fl_statuscode='AUDITED' ",
											yd, data[4], data[3]);
							if (rs1.next()) {
								Double amount = Double.parseDouble(data[5].toString());
								if (!defaultCurrency.equals(data[7])) {
									Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
											+ "' and cm_crname='" + data[7] + "'");
									if (cm_crrate != null && !cm_crrate.equals("")) {
										amount = amount * Double.parseDouble(cm_crrate.toString());
									}
								}
								if (rs1.getGeneralDouble("fld_amount") < rs1.getGeneralDouble("fld_actamount") + amount) {
									errorString = "额度类型<" + data[3] + ">申请金额已超本月额度！余额为："
											+ (rs1.getGeneralDouble("fld_amount") - rs1.getGeneralDouble("fld_actamount"));
								}
							} else {
								errorString = "额度表没有相应记录，不予提交!";
							}
						} else {
							SqlRowList rs1 = baseDao
									.queryForRowSet(
											"select nvl(fld_amount,0) fld_amount, nvl(fld_actamount,0) fld_actamount from FeeLimitDetail left join FeeLimit on fl_id=fld_flid where fl_yearmonth=? and fld_departmentcode=? and fld_class=? and fl_statuscode='AUDITED' ",
											yd, data[4], data[3]);
							if (rs1.next()) {
								Double amount = Double.parseDouble(data[5].toString());
								/*
								 * if(!defaultCurrency.equals(data[7])){ Object
								 * cm_crrate =
								 * baseDao.getFieldDataByCondition("CurrencysMonth"
								 * , "cm_crrate",
								 * "cm_yearmonth='"+yd+"' and cm_crname='"
								 * +data[7]+"'"); if(cm_crrate!=null &&
								 * !cm_crrate.equals("")){ amount =
								 * amount*Double
								 * .parseDouble(cm_crrate.toString()); } }
								 */
								if (rs1.getGeneralDouble("fld_amount") < rs1.getGeneralDouble("fld_actamount") + amount) {
									errorString = "额度类型<" + data[3] + ">申请金额已超本月额度！余额为："
											+ (rs1.getGeneralDouble("fld_amount") - rs1.getGeneralDouble("fld_actamount"));
								}
							} else {
								errorString = "额度表没有相应记录，不予提交!";
							}
						}
					}
					if (errorString != null) {
						BaseUtil.showError(errorString);
					}
				}
			} else {
				BaseUtil.showError("请在费用类型设置中维护费用类型！类型为：" + data[3]);
			}

		}
	}

	/**
	 * oa->fee->feeplease 差旅费报销单:是否超额度
	 */
	public void feePleaseCLFBX_commit_before_countlimit(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_class", "fp_departmentcode", "fp_pleaseamount", "fp_v13" }, "fp_id=" + fp_id);
		String yd = data[1].toString();
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		List<Object[]> datas = baseDao.getFieldsDatasByCondition(
				// 计算该单据各种费用类型的总金额
				"feepleasedetail", new String[] { "fpd_type", "nvl(sum(fpd_n8),0)" },
				"fpd_type=(select fk_name from feekind where fpd_type=fk_name and fk_iscontrol='是') and fpd_fpid=" + fp_id
						+ " group by fpd_type");
		if (defaultSource != null && !defaultSource.equals("")) {
			// boolean bool = baseDao.isDBSetting(defaultSource+".FeeLimit",
			// "UnionChargeAmount");
			boolean bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				for (Object[] d : datas) {
					Object[] date = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind", new String[] { "fk_controlway",
							"fk_iscontrol" }, "fk_name='" + d[0] + "'");
					if (date != null) {
						if ("EMP".equals(date[0]) || "个人".equals(date[0])) {
							Object[] limit = baseDao.getFieldsDataByCondition(
									// 抓取该人员当月这种类型的额度记录
									" " + defaultSource + ".FeeLimitDetail left join " + defaultSource + ".FeeLimit on fl_id=fld_flid",
									new String[] { "nvl(fld_amount,0)", "nvl(fld_actamount,0)" }, "fl_yearmonth=" + yd
											+ " and fld_emcode='" + data[0] + "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
							if (limit != null) {// 如果有设置额度则检查是否超额度
								Double amount = Double.parseDouble(d[1] + "");
								if (!defaultCurrency.equals(data[5])) {
									Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
											+ "' and cm_crname='" + data[5] + "'");
									if (cm_crrate != null && !cm_crrate.equals("")) {
										amount = amount * Double.parseDouble(cm_crrate.toString());
									}
								}
								if (Double.parseDouble(limit[0] + "") < Double.parseDouble(limit[1] + "") + amount) {
									BaseUtil.showError("额度类型<" + d[0] + ">申请金额已超本月额度！余额为："
											+ (Double.parseDouble(limit[0] + "") - Double.parseDouble(limit[1] + "")));
								}
							} else {
								BaseUtil.showError("额度类型为：" + d[0] + "的当前月份没有额度记录，请先申请额度");
							}
						} else if ("DEP".equals(date[0]) || "部门".equals(date[0])) {
							Object[] limit = baseDao.getFieldsDataByCondition(
									// 抓取该人员当月这种类型的额度记录
									" " + defaultSource + ".FeeLimitDetail left join " + defaultSource + ".FeeLimit on fl_id=fld_flid",
									new String[] { "nvl(fld_amount,0)", "nvl(fld_actamount,0)" }, "fl_yearmonth=" + yd
											+ " and fld_departmentcode='" + data[3] + "' and fld_class='" + d[0]
											+ "' and fl_statuscode='AUDITED'");
							if (limit != null) {// 如果有设置额度则检查是否超额度
								Double amount = Double.parseDouble(d[1] + "");
								if (!defaultCurrency.equals(data[5])) {
									Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
											+ "' and cm_crname='" + data[5] + "'");
									if (cm_crrate != null && !cm_crrate.equals("")) {
										amount = amount * Double.parseDouble(cm_crrate.toString());
									}
								}
								if (Double.parseDouble(limit[0] + "") < Double.parseDouble(limit[1] + "") + amount) {
									BaseUtil.showError("额度类型<" + d[0] + ">申请金额已超本月额度！余额为："
											+ (Double.parseDouble(limit[0] + "") - Double.parseDouble(limit[1] + "")));
								}
							} else {
								BaseUtil.showError("额度类型为：" + d[0] + "的当前月份没有额度记录，请先申请额度");
							}
						}
					} else {
						BaseUtil.showError("请在集团中心费用类型设置中维护费用类型！类型为：" + d[0]);
					}
				}
			} else {
				for (Object[] d : datas) {
					Object[] date = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" },
							"fk_name='" + d[0] + "'");
					if ("EMP".equals(date[0]) || "个人".equals(date[0])) {
						Object[] limit = baseDao.getFieldsDataByCondition(
						// 抓取该人员当月这种类型的额度记录
								"FeeLimitDetail left join FeeLimit on fl_id=fld_flid", new String[] { "nvl(fld_amount,0)",
										"nvl(fld_actamount,0)" }, "fl_yearmonth=" + yd + " and fld_emcode='" + data[0]
										+ "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
						if (limit != null) {// 如果有设置额度则检查是否超额度
							Double amount = Double.parseDouble(d[1] + "");
							/*
							 * if(!defaultCurrency.equals(data[5])){ Object
							 * cm_crrate =
							 * baseDao.getFieldDataByCondition("CurrencysMonth",
							 * "cm_crrate",
							 * "cm_yearmonth='"+yd+"' and cm_crname='"
							 * +data[5]+"'"); if(cm_crrate!=null &&
							 * !cm_crrate.equals("")){ amount =
							 * amount*Double.parseDouble(cm_crrate.toString());
							 * } }
							 */
							if (Double.parseDouble(limit[0] + "") < Double.parseDouble(limit[1] + "") + amount) {
								BaseUtil.showError("额度类型<" + d[0] + ">申请金额已超本月额度！余额为："
										+ (Double.parseDouble(limit[0] + "") - Double.parseDouble(limit[1] + "")));
							}
						} else {
							BaseUtil.showError("额度类型为：" + d[0] + "的当前月份没有额度记录，请先申请额度");
						}
					} else if ("DEP".equals(date[0]) || "部门".equals(date[0])) {
						Object[] limit = baseDao.getFieldsDataByCondition(
						// 抓取该人员当月这种类型的额度记录
								"FeeLimitDetail left join FeeLimit on fl_id=fld_flid", new String[] { "nvl(fld_amount,0)",
										"nvl(fld_actamount,0)" }, "fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3]
										+ "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
						if (limit != null) {// 如果有设置额度则检查是否超额度
							Double amount = Double.parseDouble(d[1] + "");
							/*
							 * if(!defaultCurrency.equals(data[5])){ Object
							 * cm_crrate =
							 * baseDao.getFieldDataByCondition("CurrencysMonth",
							 * "cm_crrate",
							 * "cm_yearmonth='"+yd+"' and cm_crname='"
							 * +data[5]+"'"); if(cm_crrate!=null &&
							 * !cm_crrate.equals("")){ amount =
							 * amount*Double.parseDouble(cm_crrate.toString());
							 * } }
							 */
							if (Double.parseDouble(limit[0] + "") < Double.parseDouble(limit[1] + "") + amount) {
								BaseUtil.showError("额度类型<" + d[0] + ">申请金额已超本月额度！余额为："
										+ (Double.parseDouble(limit[0] + "") - Double.parseDouble(limit[1] + "")));
							}
						} else {
							BaseUtil.showError("额度类型为：" + d[0] + "的当前月份没有额度记录，请先申请额度");
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * oa->fee->feeplease 费用报销单:提交后添加都相应额度记录
	 */
	public void feePlease_audit_after_updatelimit(Integer fp_id) {
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_class", "fp_departmentcode", "fp_pleaseamount", "fp_v13", "fp_code" }, "fp_id=" + fp_id);
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		Object[] datas = null;
		boolean bool = false;
		Object currentmonth = null;
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		if (defaultSource != null && !defaultSource.equals("")) {
			// bool = baseDao.isDBSetting(defaultSource+".FeeLimit",
			// "UnionChargeAmount");
			bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				datas = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind", new String[] { "fk_controlway", "fk_iscontrol" },
						"fk_name='" + data[2] + "'");
				currentmonth = baseDao.getFieldValue(defaultSource + ".FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
			} else {
				datas = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='" + data[2]
						+ "'");
			}
		}
		Object yd = data[1];
		if (StringUtil.hasText(currentmonth) && !yd.equals(currentmonth))
			yd = currentmonth;
		if (datas != null) {
			if ("是".equals(datas[1])) {
				int fl_id = baseDao.getSeqId(defaultSource + ".FEELIMITLOG_SEQ");
				String master = SystemSession.getUser().getEm_master();
				if (bool) {
					baseDao.execute("insert into "
							+ defaultSource
							+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) select "
							+ fl_id
							+ ",'"
							+ master
							+ "',fp_code,fp_pleaseamount,fp_pleaseamount*(select cm_crrate from CurrencysMonth where cm_crname='"
							+ data[5]
							+ "' and cm_yearmonth='"
							+ yd
							+ "'),to_char(fp_billdate,'yyyymm'),fp_class,fp_departmentcode,fp_pleasemancode,'FeePlease!FYBX' from feeplease where fp_id="
							+ fp_id + "");
				}
				if ("EMP".equals(datas[0]) || "个人".equals(datas[0])) {
					if (bool) {
						Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
								+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_emcode='" + data[0]
								+ "' and fld_class='" + data[2] + "' and fl_statuscode='AUDITED'");
						Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
						if (!defaultCurrency.equals(data[5])) {
							Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
									+ "' and cm_crname='" + data[5] + "'");
							if (cm_crrate != null && !cm_crrate.equals("")) {
								count = Double.parseDouble(count.toString()) * Double.parseDouble(cm_crrate.toString());
							}
						}
						baseDao.execute("update " + defaultSource + ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from "
								+ defaultSource + ".feelimitlog where FL_CLASS=FLD_CLASS and FL_EMCODE=FLD_EMCODE and FL_YEARMONTH=" + yd
								+ ") where fld_id=" + id);
					} else {
						Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
								"fl_yearmonth=" + yd + " and fld_emcode='" + data[0] + "' and fld_class='" + data[2]
										+ "' and fl_statuscode='AUDITED'");
						Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
						/*
						 * if(!defaultCurrency.equals(data[5])){ Object
						 * cm_crrate =
						 * baseDao.getFieldDataByCondition("CurrencysMonth",
						 * "cm_crrate",
						 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
						 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
						 * Double
						 * .parseDouble(count.toString())*Double.parseDouble
						 * (cm_crrate.toString()); } }
						 */
						baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)+" + count + " where fld_id=" + id);
					}
				} else if ("DEP".equals(datas[0]) || "部门".equals(datas[0])) {
					if (bool) {
						Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
								+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3]
								+ "' and fld_class='" + data[2] + "' and fl_statuscode='AUDITED'");
						Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
						if (!defaultCurrency.equals(data[5])) {
							Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
									+ "' and cm_crname='" + data[5] + "'");
							if (cm_crrate != null && !cm_crrate.equals("")) {
								count = Double.parseDouble(count.toString()) * Double.parseDouble(cm_crrate.toString());
							}
						}
						baseDao.execute("update " + defaultSource + ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from "
								+ defaultSource
								+ ".feelimitlog where FL_CLASS=FLD_CLASS and FL_DPCODE=FLD_DEPARTMENTCODE and FL_YEARMONTH=" + yd
								+ ") where fld_id=" + id);
					} else {
						Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
								"fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3] + "' and fld_class='" + data[2]
										+ "' and fl_statuscode='AUDITED'");
						Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
						/*
						 * if(!defaultCurrency.equals(data[5])){ Object
						 * cm_crrate =
						 * baseDao.getFieldDataByCondition("CurrencysMonth",
						 * "cm_crrate",
						 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
						 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
						 * Double
						 * .parseDouble(count.toString())*Double.parseDouble
						 * (cm_crrate.toString()); } }
						 */
						baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)+" + count + " where fld_id=" + id);
					}
				}
			}
		}

	}

	/**
	 * oa->fee->feeplease 费用报销单:反审核后添加都相应额度记录
	 */
	public void feePlease_resAudit_after_updatelimit(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = false;
		Object currentmonth = null;
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_class", "fp_departmentcode", "fp_pleaseamount", "fp_v13", "fp_code" }, "fp_id=" + fp_id);
		Object[] datas = null;
		Object yd = data[1];
		if (defaultSource != null && !defaultSource.equals("")) {
			// bool = baseDao.isDBSetting(defaultSource+".FeeLimit",
			// "UnionChargeAmount");
			bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				currentmonth = baseDao.getFieldValue(defaultSource + ".FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
				datas = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind", new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='"
						+ data[2] + "'");
			} else {
				currentmonth = baseDao.getFieldValue("FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
				datas = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='"
							+ data[2] + "'");
			}
		}
		if (StringUtil.hasText(currentmonth) && !yd.equals(currentmonth))
			yd = currentmonth;
		if ("是".equals(datas[1])) {
			int fl_id = baseDao.getSeqId(defaultSource + ".FEELIMITLOG_SEQ");
			String master = SystemSession.getUser().getEm_master();
			Object thisAmount = data[4];
			Object sumAmount = null;
			if ("EMP".equals(datas[0]) || "个人".equals(datas[0])) {
				if (bool) {
					Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
							+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_emcode='" + data[0]
							+ "' and fld_class='" + data[2] + "' and fl_statuscode='AUDITED'");
					Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
					if (!defaultCurrency.equals(data[5])) {
						Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
								+ "' and cm_crname='" + data[5] + "'");
						if (cm_crrate != null && !cm_crrate.equals("")) {
							count = Double.parseDouble(count.toString()) * Double.parseDouble(cm_crrate.toString());
						}
					}
					sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_PLEASEAMOUNT)", "FL_OWNER='" + master
							+ "' and FL_CODE='" + data[6] + "' and fl_caller='FeePlease!FYBX' ");
					// sumAmount =
					// baseDao.getFieldDataByCondition(defaultSource+".FEELIMITLOG",
					// "sum(FL_AMOUNT)",
					// "FL_CLASS='"+data[2]+"' and FL_EMCODE='"+data[0]+"' and FL_YEARMONTH="+yd+" ");
					if (thisAmount != null && sumAmount != null) {
						if ((Double.parseDouble(sumAmount.toString()) - Double.parseDouble(thisAmount.toString()) == Double.valueOf(0))) {
							baseDao.execute("insert into "
									+ defaultSource
									+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) select "
									+ fl_id + ",'" + master
									+ "',fp_code,-fp_pleaseamount,-fp_pleaseamount*(select cm_crrate from CurrencysMonth where cm_crname='"
									+ data[5] + "' and cm_yearmonth='" + yd + "'),to_char(fp_billdate,'yyyymm'),fp_class,'" + data[3]
									+ "','" + data[0] + "','FeePlease!FYBX' from feeplease where fp_id=" + fp_id + "");
							baseDao.execute("update " + defaultSource
									+ ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from " + defaultSource
									+ ".feelimitlog where FL_CLASS=FLD_CLASS and FL_EMCODE=FLD_EMCODE and FL_YEARMONTH=" + yd
									+ ") where fld_id=" + id);
						}
					}
				} else {
					Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
							"fl_yearmonth=" + yd + " and fld_emcode='" + data[0] + "' and fld_class='" + data[2]
									+ "' and fl_statuscode='AUDITED'");
					Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
					/*
					 * if(!defaultCurrency.equals(data[5])){ Object cm_crrate =
					 * baseDao.getFieldDataByCondition("CurrencysMonth",
					 * "cm_crrate",
					 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
					 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
					 * Double
					 * .parseDouble(count.toString())*Double.parseDouble(cm_crrate
					 * .toString()); } }
					 */
					baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)-" + count + " where fld_id=" + id);
				}
			} else if ("DEP".equals(datas[0]) || "部门".equals(datas[0])) {
				if (bool) {
					Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
							+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3]
							+ "' and fld_class='" + data[2] + "' and fl_statuscode='AUDITED'");
					Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
					if (!defaultCurrency.equals(data[5])) {
						Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
								+ "' and cm_crname='" + data[5] + "'");
						if (cm_crrate != null && !cm_crrate.equals("")) {
							count = Double.parseDouble(count.toString()) * Double.parseDouble(cm_crrate.toString());
						}
					}
					sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_PLEASEAMOUNT)", "FL_OWNER='" + master
							+ "' and FL_CODE='" + data[6] + "' and fl_caller='FeePlease!FYBX'");
					if (thisAmount != null && sumAmount != null) {
						if ((Double.parseDouble(sumAmount.toString()) - Double.parseDouble(sumAmount.toString()) == Double.valueOf(0))) {
							baseDao.execute("insert into "
									+ defaultSource
									+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) select "
									+ fl_id + ",'" + master
									+ "',fp_code,-fp_pleaseamount,-fp_pleaseamount*(select cm_crrate from CurrencysMonth where cm_crname='"
									+ data[5] + "' and cm_yearmonth='" + yd + "'),to_char(fp_billdate,'yyyymm'),fp_class,'" + data[3]
									+ "','" + data[0] + "','FeePlease!FYBX' from feeplease where fp_id=" + fp_id + "");
							baseDao.execute("update " + defaultSource
									+ ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from " + defaultSource
									+ ".feelimitlog where FL_CLASS=FLD_CLASS and FL_DPCODE=FLD_DEPARTMENTCODE and FL_YEARMONTH=" + yd
									+ ") where fld_id=" + id);
						}
					}
					// baseDao.execute("update "+defaultSource+".FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)-"
					// + count + " where fld_id=" + id);
				} else {
					Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
							"fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3] + "' and fld_class='" + data[2]
									+ "' and fl_statuscode='AUDITED'");
					Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
					/*
					 * if(!defaultCurrency.equals(data[5])){ Object cm_crrate =
					 * baseDao.getFieldDataByCondition("CurrencysMonth",
					 * "cm_crrate",
					 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
					 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
					 * Double
					 * .parseDouble(count.toString())*Double.parseDouble(cm_crrate
					 * .toString()); } }
					 */
					baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)-" + count + " where fld_id=" + id);
				}
			}
		}
	}

	/**
	 * oa->fee->feeplease 差旅费报销单:审核后添加到相应额度记录
	 */
	public void feePleaseCLFBX_audit_after_updatelimit(Integer fp_id) {
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_class", "fp_departmentcode", "fp_pleaseamount", "fp_v13", "fp_code" }, "fp_id=" + fp_id);
		String yd = data[1].toString();
		String defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = false;
		String master = SystemSession.getUser().getEm_master();
		// 默认本位币
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		List<Object[]> datas = null;
		if (defaultSource != null && !defaultSource.equals("")) {
			// bool = baseDao.isDBSetting(defaultSource+".FeeLimit",
			// "UnionChargeAmount");
			bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				datas = baseDao.getFieldsDatasByCondition(
				// 计算该单据各种费用类型的总金额
				// fpd_n8 财务核准金额
						"feepleasedetail", new String[] { "fpd_type", "sum(fpd_n8)" }, "fpd_type=(select fk_name from " + defaultSource
								+ ".feekind where fpd_type=fk_name and fk_iscontrol='是') and fpd_fpid=" + fp_id + " group by fpd_type");
				for (Object[] d : datas) {
					int fl_id = baseDao.getSeqId(defaultSource + ".FEELIMITLOG_SEQ");
					baseDao.execute("insert into "
							+ defaultSource
							+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) "
							+ "select " + fl_id + ",'" + master + "',fp_code," + d[1] + "," + d[1]
							+ "*(select cm_crrate from CurrencysMonth where cm_crname='" + data[5] + "' and cm_yearmonth='" + yd
							+ "'),to_char(fp_billdate,'yyyymm'),'" + d[0] + "','" + data[3] + "','" + data[0]
							+ "','FeePlease!CLFBX' from feeplease where fp_id=" + fp_id + "");
					Object[] date = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind", new String[] { "fk_controlway",
							"fk_iscontrol" }, "fk_name='" + d[0] + "'");
					Object count = d[1];
					Object sumAmount = null;
					if (!defaultCurrency.equals(data[5])) {
						Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
								+ "' and cm_crname='" + data[5] + "'");
						if (cm_crrate != null && !cm_crrate.equals("")) {
							count = Double.parseDouble(count.toString()) * Double.parseDouble(cm_crrate.toString());
						}
					}
					if ("EMP".equals(date[0])) {
						Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
								+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_emcode='" + data[0]
								+ "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
						sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_AMOUNT)", "FL_OWNER='" + master
								+ "' and FL_CALLER='FeePlease!CLFBX' and FL_CODE='" + data[6] + "' and fl_class='" + d[0] + "'");
						sumAmount = sumAmount == null ? "0" : sumAmount;
						if (Double.parseDouble(sumAmount.toString()) + Double.parseDouble(count.toString()) > 0) {
							baseDao.execute("update " + defaultSource
									+ ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from " + defaultSource
									+ ".feelimitlog where FL_YEARMONTH=" + yd
									+ " and FL_CLASS=FLD_CLASS and FL_EMCODE=FLD_EMCODE) where fld_id=" + id);
						}
					} else if ("DEP".equals(date[0])) {
						Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
								+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3]
								+ "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
						sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_AMOUNT)", "FL_OWNER='" + master
								+ "' and FL_CALLER='FeePlease!CLFBX' and FL_CODE='" + data[6] + "' and fl_class='" + d[0] + "'");
						sumAmount = sumAmount == null ? "0" : sumAmount;
						if (Double.parseDouble(sumAmount.toString()) + Double.parseDouble(count.toString()) > 0) {
							baseDao.execute("update " + defaultSource
									+ ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from " + defaultSource
									+ ".feelimitlog where FL_YEARMONTH=" + yd
									+ " and FL_CLASS=FLD_CLASS and FL_DPCODE=FLD_DEPARTMENTCODE) where fld_id=" + id);
						}
					}
				}
			} else {
				datas = baseDao.getFieldsDatasByCondition(
						// 计算该单据各种费用类型的总金额
						// fpd_n8 财务核准金额
						"feepleasedetail", new String[] { "fpd_type", "sum(fpd_n8)" },
						"fpd_type=(select fk_name from feekind where fpd_type=fk_name and fk_iscontrol='是') and fpd_fpid=" + fp_id
								+ " group by fpd_type");
				for (Object[] d : datas) {
					Object[] date = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" },
							"fk_name='" + d[0] + "'");
					Object count = d[1];
					/*
					 * if(!defaultCurrency.equals(data[5])){ Object cm_crrate =
					 * baseDao.getFieldDataByCondition("CurrencysMonth",
					 * "cm_crrate",
					 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
					 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
					 * Double
					 * .parseDouble(count.toString())*Double.parseDouble(cm_crrate
					 * .toString()); } }
					 */
					if ("EMP".equals(date[0])) {
						Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
								"fl_yearmonth=" + yd + " and fld_emcode='" + data[0] + "' and fld_class='" + d[0]
										+ "' and fl_statuscode='AUDITED'");
						baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)+" + count + " where fld_id=" + id);
					} else if ("DEP".equals(date[0])) {
						Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
								"fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3] + "' and fld_class='" + d[0]
										+ "' and fl_statuscode='AUDITED'");
						baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)+" + count + " where fld_id=" + id);
					}
				}
			}
		}
	}

	/**
	 * oa->fee->feeplease 差旅费报销单:反审核后添加都相应额度记录
	 */
	public void feePleaseCLFBX_resAudit_after_updatelimit(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = false;
		Object currentmonth = null;
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		String master = SystemSession.getUser().getEm_master();
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleasemancode", "to_char(fp_billdate,'yyyymm')",
				"fp_class", "fp_departmentcode", "fp_pleaseamount", "fp_v13", "fp_code" }, "fp_id=" + fp_id);
		Object yd = data[1];
		if (defaultSource != null && !defaultSource.equals("")) {
			// bool = baseDao.isDBSetting(defaultSource+".FeeLimit",
			// "UnionChargeAmount");
			bool = baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				currentmonth = baseDao.getFieldValue(defaultSource + ".FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
			} else {
				currentmonth = baseDao.getFieldValue("FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
			}
		}
		if (StringUtil.hasText(currentmonth) && !yd.equals(currentmonth))
			yd = currentmonth;
		List<Object[]> datas = baseDao.getFieldsDatasByCondition(
				// 计算该单据各种费用类型的总金额
				"feepleasedetail", new String[] { "fpd_type", "sum(fpd_n8)" },
				"fpd_type=(select fk_name from feekind where fpd_type=fk_name and fk_iscontrol='是') and fpd_fpid=" + fp_id
						+ " group by fpd_type");
		if (bool) {
			for (Object[] d : datas) {
				int fl_id = baseDao.getSeqId(defaultSource + ".FEELIMITLOG_SEQ");
				Object[] date = baseDao.getFieldsDataByCondition(defaultSource + ".FeeKind",
						new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='" + d[0] + "'");
				Object sumAmount = null;
				Object thisAmount = d[1];
				if (!defaultCurrency.equals(data[5])) {
					Object cm_crrate = baseDao.getFieldDataByCondition("CurrencysMonth", "cm_crrate", "cm_yearmonth='" + yd
							+ "' and cm_crname='" + data[5] + "'");
					if (cm_crrate != null && !cm_crrate.equals("")) {
						thisAmount = Double.parseDouble(thisAmount.toString()) * Double.parseDouble(cm_crrate.toString());
					}
				}
				if ("EMP".equals(date[0])) {
					Object id = baseDao.getFieldDataByCondition("" + defaultSource + ".FeeLimitDetail left join " + defaultSource
							+ ".FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd + " and fld_emcode='" + data[0]
							+ "' and fld_class='" + d[0] + "' and fl_statuscode='AUDITED'");
					sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_AMOUNT)", "FL_OWNER='" + master
							+ "' and FL_CALLER='FeePlease!CLFBX' and FL_CODE='" + data[6] + "' and fl_class='" + d[0] + "'");
					sumAmount = sumAmount == null ? "0" : sumAmount;
					if ((Double.parseDouble(sumAmount.toString()) - Double.parseDouble(thisAmount.toString()) == Double.valueOf(0))) {
						baseDao.execute("insert into "
								+ defaultSource
								+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) "
								+ "select " + fl_id + ",'" + master + "',fp_code,-" + d[1] + ",-" + thisAmount
								+ ",to_char(fp_billdate,'yyyymm'),'" + d[0] + "','" + data[3] + "','" + data[0]
								+ "','FeePlease!CLFBX' from feeplease where fp_id=" + fp_id + "");
						baseDao.execute("update " + defaultSource + ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from "
								+ defaultSource + ".feelimitlog where FL_CLASS=FLD_CLASS and FL_EMCODE=FLD_EMCODE and FL_YEARMONTH=" + yd
								+ ") where fld_id=" + id);
					}
				} else if ("DEP".equals(date[0])) {
					Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
							"fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3] + "' and fld_class='" + d[0]
									+ "' and fl_statuscode='AUDITED'");
					sumAmount = baseDao.getFieldDataByCondition(defaultSource + ".FEELIMITLOG", "sum(FL_AMOUNT)", "FL_OWNER='" + master
							+ "' and FL_CALLER='FeePlease!CLFBX' and FL_CODE='" + data[6] + "' and fl_class='" + d[0] + "'");
					sumAmount = sumAmount == null ? "0" : sumAmount;
					if ((Double.parseDouble(sumAmount.toString()) - Double.parseDouble(thisAmount.toString()) == Double.valueOf(0))) {
						baseDao.execute("insert into "
								+ defaultSource
								+ ".FEELIMITLOG(fl_id,fl_owner,fl_code,FL_PLEASEAMOUNT,FL_AMOUNT,FL_YEARMONTH,FL_CLASS,FL_DPCODE,FL_EMCODE,FL_CALLER) "
								+ "select " + fl_id + ",'" + master + "',fp_code,-" + d[1] + ",-" + thisAmount
								+ ",to_char(fp_billdate,'yyyymm'),'" + d[0] + "','" + data[3] + "','" + data[0]
								+ "','FeePlease!CLFBX' from feeplease where fp_id=" + fp_id + "");
						baseDao.execute("update " + defaultSource + ".FeeLimitDetail set fld_actamount=(select nvl(sum(FL_AMOUNT),0) from "
								+ defaultSource
								+ ".feelimitlog where FL_CLASS=FLD_CLASS and FL_DPCODE=FLD_DEPARTMENTCODE and FL_YEARMONTH=" + yd
								+ ") where fld_id=" + id);
					}
				}
			}
		} else {
			for (Object[] d : datas) {
				Object[] date = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_controlway", "fk_iscontrol" }, "fk_name='"
						+ d[0] + "'");
				Object count = d[1];
				/*
				 * if(!defaultCurrency.equals(data[5])){ Object cm_crrate =
				 * baseDao.getFieldDataByCondition("CurrencysMonth",
				 * "cm_crrate",
				 * "cm_yearmonth='"+yd+"' and cm_crname='"+data[5]+"'");
				 * if(cm_crrate!=null && !cm_crrate.equals("")){ count =
				 * Double.parseDouble
				 * (count.toString())*Double.parseDouble(cm_crrate.toString());
				 * } }
				 */
				if ("EMP".equals(date[0])) {
					Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
							"fl_yearmonth=" + yd + " and fld_emcode='" + data[0] + "' and fld_class='" + d[0]
									+ "' and fl_statuscode='AUDITED'");
					baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)-" + count + " where fld_id=" + id);
				} else if ("DEP".equals(date[0])) {
					Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
							"fl_yearmonth=" + yd + " and fld_departmentcode='" + data[3] + "' and fld_class='" + d[0]
									+ "' and fl_statuscode='AUDITED'");
					baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)-" + count + " where fld_id=" + id);
				}
			}
		}
	}

	/**
	 * @author hx 2018-7-19
	 * 差旅费报销单/费用报销单:删除后将集团账套的费用发生明细删除
	 */
	public void feePlease_delete_after_deletelimit(Integer fp_id) {
		Object[] data = baseDao.getFieldsDataByCondition("FeePlease", "fp_kind,fp_code", "fp_id=" + fp_id);
		String master = SystemSession.getUser().getEm_master();
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		String caller="";
		if(data!=null) {
			if(String.valueOf(data[0]).equals("费用报销单")) {
				caller="FeePlease!FYBX";
			}else if(String.valueOf(data[0]).equals("差旅费报销单")) {
				caller="FeePlease!CLFBX";
			}
			//当额度数据存在参数配置：中心账套统一管控费用额度，就会产生费用明细数据
			if(baseDao.checkIf("" + defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1")) {
				baseDao.deleteByCondition(defaultSource+".Feelimitlog", "fl_owner=? and fl_code=? and fl_caller=?", new Object[] {master,data[1],caller});
			}
		}
	}
	/**
	 * feePleaseCLFBX->deleteDetail-> 删除明细时计算金额
	 */
	public void feePleaseCLFBX_deletede_countfee(Integer fpd_id) {
		Object fp_id = baseDao.getFieldDataByCondition("FeePleaseDetail", "fpd_fpid", "fpd_id=" + fpd_id);
		// 计算明细中的所有费用
		String sql1 = "update FeePleaseDetail set fpd_n8=fpd_n7 where fpd_n8=0 and fpd_fpid=" + fp_id;
		baseDao.execute(sql1);
		baseDao.updateByCondition("FeePlease",
				"fp_pleaseamount=" + baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n8)", "fpd_fpid=" + fp_id), "fp_id="
						+ fp_id);
	}

	/**
	 * feePleaseCLFBX->deleteDetail-> 更新时计算金额
	 * 
	 */
	public void feePleaseCLFBX_update_countfee(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object fp_id = store.get("fp_id");
		// 计算明细中的所有费用
		String sql1 = "update FeePleaseDetail set fpd_n8=fpd_n7 where fpd_n8=0 and fpd_fpid=" + fp_id;
		baseDao.execute(sql1);
		baseDao.updateByCondition("FeePlease",
				"fp_pleaseamount=" + baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n8)", "fpd_fpid=" + fp_id), "fp_id="
						+ fp_id);
	}

	/**
	 * oa->fee->feeplease 费用报销单,差旅费报销单:报销日期所在的月份是否小于当前额度表的最大月份，小于不能提交
	 */
	public void feePlease_commit_before_date(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = false;
		Object month = null;
		if (defaultSource != null && !defaultSource.equals("")) {
			bool = baseDao.checkIf(defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				month = baseDao.getFieldDataByCondition(defaultSource + ".feelimit", "max(FL_YEARMONTH)", "1=1");
			} else {
				month = baseDao.getFieldValue("FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
			}
		}
		String sql = "select to_char(fp_billdate,'yyyymm') YD from feeplease where fp_id=" + fp_id + "";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(sql);
		while (rs.next()) {
			String yd = rs.getString("YD");
			if (Integer.parseInt(yd.toString()) < Integer.parseInt(month.toString())) {
				BaseUtil.showError("报销日期所在的月份小于当前额度表的最大月份,不能提交!");
			}
		}
	}

	/**
	 * oa->fee->feeplease 费用报销单,差旅费报销单:报销日期所在的月份是否小于当前额度表的最大月份，小于不能反提交
	 */
	public void feePlease_rescommit_before_date(Integer fp_id) {
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = false;
		Object month = null;
		if (defaultSource != null && !defaultSource.equals("")) {
			bool = baseDao.checkIf(defaultSource + ".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if (bool) {
				month = baseDao.getFieldDataByCondition(defaultSource + ".feelimit", "max(FL_YEARMONTH)", "1=1");
			} else {
				month = baseDao.getFieldValue("FeeLimit", "max(FL_YEARMONTH)", "1=1", String.class);
			}
		}
		String sql = "select to_char(fp_billdate,'yyyymm') YD from feeplease where fp_id=" + fp_id + "";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(sql);
		while (rs.next()) {
			String yd = rs.getString("YD");
			if (Integer.parseInt(yd.toString()) < Integer.parseInt(month.toString())) {
				BaseUtil.showError("报销日期所在的月份小于当前额度表的最大月份,不能反提交!");
			}
		}
	}

	/**
	 * 用品采购单：明细行删除时，还原用品申请单明细已转数
	 */
	public void oapurc_deletedetail(Integer id) {
		// 还原请购明细
		Object[] objs = baseDao.getFieldsDataByCondition("oapurchasedetail", new String[] { "od_oacode", "od_oadetno",
				"nvl(od_neednumber,0)" }, "od_id=" + id + " and nvl(od_oacode,' ')<>' ' and nvl(od_oadetno,0)<>0");
		if (objs != null) {
			Object apid = baseDao.getFieldDataByCondition("Oaapplication", "oa_id", "oa_code='" + objs[0] + "'");
			if (apid != null) {
				baseDao.updateByCondition("oaapplicationdetail", "od_yqty=nvl(od_yqty,0)-" + objs[2], "od_oaid=" + apid + " and od_detno="
						+ objs[1]);
			}
		}
	}

	/**
	 * 合同类型：保存更新时自动更新层级
	 * 
	 * @param id
	 */
	public void contractType_saveorupdate_updatelevel(HashMap<Object, Object> store) {
		baseDao.execute("update contracttype A set ct_level=(nvl((select ct_level from contracttype B where A.ct_subof=B.ct_id),0) + 1) where ct_id="
				+ store.get("ct_id"));
	}

	/**
	 * 派车单删除时，更新派车申请单派车转态
	 */
	public void Vehiclereturn_delete_status(Integer id) {
		baseDao.execute("update Vehicleapply set va_turnstatus=null,va_vrcode=null,va_vrid=null where va_vrid=" + id);

	}

	/**
	 * 英唐-印章停用/废止申请单审核后，对应的印章状态要更新
	 */
	public void YZ_audit_after_updatestatus(Integer ct_id) {
		Object[] data = baseDao.getFieldsDataByCondition("CUSTOMTABLE", new String[] { "CT_VARCHAR50_5", "CT_VARCHAR50_14",
				"CT_VARCHAR50_12", "CT_VARCHAR50_2" }, "ct_id=" + ct_id);// 申请类型，印章状态，公司名称,印章名称
		String status = (data[1] != null) ? data[1].toString() : data[0].toString();
		String sql = "update CUSTOMTABLE set CT_VARCHAR50_16='" + status + "' where CT_VARCHAR50_4='" + data[2].toString()
				+ "' and CT_VARCHAR50_1='" + data[3].toString() + "'";
		baseDao.execute(sql);
	}

	/**
	 * 费用报销单：删除明细前， 还原来源单据已转金额
	 */
	public void feePleaseFYBX_deletede_before_updateamount(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("FeePleaseDetail", new String[] { "fpd_code", "fpd_d9", "fpd_total" }, "fpd_id="
				+ id);
		// 来源单据是印章申请单
		if (objs[0] != null && objs[1] != null) {
			if (objs[1].equals("FeePlease!YZSYSQ")) {
				baseDao.updateByCondition("FeePlease", "fp_n4=nvl(fp_n4,0)-" + objs[2], "fp_code='" + objs[0] + "' and fp_kind='印章申请单'");
			} else {
				// 来源单据是OA单据批量转
				boolean boolOA = baseDao
						.checkIf(
								"FEEPLEASEDETAIL",
								"fpd_d9 IN (select FO_CALLER FROM FORM left join FORMDETAIL on fd_foid=fo_id  WHERE nvl(FD_TABLE,' ')='CUSTOMTABLE' and nvl(FD_FIELD,' ')='CT_SOURCEKIND' "
										+ "and nvl(FD_DEFAULTVALUE,' ')<>' ') AND FPD_ID =" + id);
				if (boolOA) {
					baseDao.updateByCondition("customtable", "ct_turnamount=nvl(ct_turnamount,0)-" + objs[2], "ct_code='" + objs[0]
							+ "' and ct_caller='" + objs[1] + "'");
				}
			}
		}
	}

	/**
	 * 费用报销单：删除明细后， 更新主表报销总额
	 */
	public void feePleaseFYBX_deletede_after_updateamount(Integer id) {
		Object fp_id = baseDao.getFieldDataByCondition("FeePleaseDetail", "fpd_fpid", "fpd_id=" + id);
		baseDao.updateByCondition("FeePlease",
				"fp_pleaseamount=" + baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n1)", "fpd_fpid=" + fp_id), "fp_id="
						+ fp_id);
	}

	/**
	 * 费用报销单：提交之前，本次还款总金额小于主表总的未归还金额，不允许提交
	 * 
	 * @author madan 2016-07-15 17:02:03
	 */
	public void feePleaseFYBX_commit_amountcheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select FP_SUMWGH,fp_pleaseamount,fp_thishkamount from FeePlease where fp_id=? and nvl(fp_pleaseamount,0)>=nvl(FP_SUMWGH,0) and nvl(fp_thishkamount,0)<nvl(FP_SUMWGH,0)",
						id);
		if (rs.next()) {
			BaseUtil.showError("报销总额大于等于主表总的未归还金额，本次还款总金额小于主表总的未归还金额，不允许提交！报销总额(本位币)[" + rs.getGeneralDouble("fp_pleaseamount")
					+ "],本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额[" + rs.getGeneralDouble("FP_SUMWGH") + "]");
		} else {
			rs = baseDao
					.queryForRowSet(
							"select fp_pleaseamount,fp_thishkamount,FP_SUMWGH from FeePlease where fp_id=? and nvl(fp_pleaseamount,0)<nvl(FP_SUMWGH,0) and nvl(fp_thishkamount,0)<nvl(fp_pleaseamount,0)",
							id);
			if (rs.next()) {
				BaseUtil.showError("报销总额小于主表总的未归还金额，本次还款总额小于主表报销总额，不允许提交！本次还款总额(本位币)[" + rs.getGeneralDouble("fp_thishkamount")
						+ "],总的未归还金额(本位币)[" + rs.getGeneralDouble("FP_SUMWGH") + "],报销总额(本位币)[" + rs.getGeneralDouble("fp_pleaseamount")
						+ "]");
			}
		}
	}
	/**
	 * 费用报销单：流程中修改明细，计算主表金额
	 * 
	 * @author huangx 2018-07-17 19:07:03
	 */
	public void feePleaseFYBX_processUpdate_updateamount(HashMap<Object, Object> store,String grid,Employee employee) {
		Object id = store.get("fp_id");
		baseDao.execute("update FeePleaseDetail set fpd_n1=fpd_total where (nvl(fpd_n1,0)=0 or nvl(fpd_n1,0)<>fpd_total)  and fpd_fpid="
				+ id);
		baseDao.execute("update feeplease set fp_pleaseamount=nvl((select sum(nvl(fpd_n1,0)) from feepleasedetail where fpd_fpid=fp_id),0) where fp_id="
				+ id);
		baseDao.execute("update feeplease set fp_amount=round(nvl(fp_pleaseamount,0)*nvl((select nvl(cm_crrate,0) from currencysmonth where fp_v13=cm_crname and to_char(fp_recorddate,'yyyymm')=cm_yearmonth),0),2) where fp_id="
				+ id);
		baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + id);
		baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + id);
		baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + id);
		baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_kind='费用报销单' and fp_id="
				+ id);
		baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_n1,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED')),0),2) where fp_kind='费用报销单' and fp_id="
				+ id);
		baseDao.execute("update feeplease a set FP_THISHKAMOUNT=round(nvl((select sum(nvl(fb_back,0)*NVL(cm_crrate,0)) from FeeBack,feeplease b,currencysmonth where fb_fpid=a.fp_id and fb_jksqcode=b.fp_code and b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单'),0),2) where fp_kind='费用报销单' and fp_id="
				+ id);
	}
	/**
	 * 程序打包记录：保存更新后，根据打包前版本和打包后版本的区间，为程序打包记录明细插入区间内所有版本的数据
	 * 采用15行一页向管理平台发送请求
	 * @param store
	 * @param list
	 * @author hx
	 */
	public void ProgramPackageRecord_saveOrUpdate_getSvnLog(HashMap<Object, Object> store,ArrayList<Map<Object, Object>> grid) {
		Object[] version = baseDao.getFieldsDataByCondition("CUSTOMTABLE", "CT_VARCHAR50_1,CT_VARCHAR50_2", "CT_ID="+store.get("CT_ID"));
		if(version!=null && version.length>0) {
			List<Object[]> oldDataList = baseDao.getFieldsDatasByCondition("CUSTOMTABLEDETAIL", new String[] {"cd_varchar50_2",
					"cd_varchar50_5","cd_varchar50_4"},  "cd_ctid="+store.get("CT_ID"));
			
			baseDao.deleteByCondition("CUSTOMTABLEDETAIL", "CD_CTID=?", store.get("CT_ID"));
			List<String> sql = new ArrayList<String>();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//获取打包前版本，打包后版本
			int beforeVersionNum = Integer.valueOf(String.valueOf(version[0]));
			int afterVersionNum = Integer.valueOf(String.valueOf(version[1]));
			if(beforeVersionNum<=afterVersionNum) {
				int count = afterVersionNum-beforeVersionNum;
				double a = count+1;
				double b = 15;
				double page = Math.ceil(a/b);
				int detno = 1;
				for(int i=0;i<page;i++) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("count", "15");
					params.put("page",String.valueOf(i+1));
					params.put("filter", "{\"version\":{\"gte\":"+beforeVersionNum+",\"lte\":"+afterVersionNum+"}}");
					params.put("sorting", "{\"version\":\"DESC\"}");
					try {
						Response response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/svnlog/uas", params, true);
						if (response.getStatusCode() == HttpStatus.OK.value()) {
							Map<Object, Object> map = FlexJsonUtil.fromJson(response.getResponseText());
							List<Map<String,Object>> dataList  =(List<Map<String,Object>>) map.get("content");
							for (Map<String,Object> data:dataList) {
								StringBuilder sb = new StringBuilder();
								sb.append("INSERT INTO CUSTOMTABLEDETAIL(CD_ID,CD_CTID,CD_DETNO,CD_VARCHAR50_1,CD_DATE_1,CD_VARCHAR50_2,CD_VARCHAR50_3) VALUES(");
								sb.append("customtabledetail_seq.nextval,"+store.get("CT_ID")+","+detno+",");
								detno++;
								sb.append("'"+data.get("man")+"',");//提出人
								String date = sdf.format(new Date(Long.valueOf(String.valueOf(data.get("date")))));
								sb.append("to_date('"+date+"','yyyy-MM-dd HH24:MI:ss'),");//提出时间
								sb.append("'"+data.get("version")+"',");//版本
								sb.append("'"+String.valueOf(data.get("remark")).replace("'", "''")+"')");//注释
								sql.add(sb.toString());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				baseDao.execute(sql);
				
				List<String> updateOldGrid = new ArrayList<String>();
				for(Object[] oldData:oldDataList) {
					if(oldData!=null) {
						String versionNew = String.valueOf(oldData[0]);
						String testField = String.valueOf(oldData[1]);
						String testManField = String.valueOf(oldData[2]);
						String sqlNew = "update CUSTOMTABLEDETAIL set cd_varchar50_5='"+testField+"',cd_varchar50_4='"+testManField+"' where CD_CTID="+store.get("CT_ID")+
								" and cd_varchar50_2='"+versionNew+"'";
						updateOldGrid.add(sqlNew);
					}
				}
				baseDao.execute(updateOldGrid);
				List<String> updateNewGrid = new ArrayList<String>();
		        for (Map<Object, Object> string : grid) {
					String versionNew = String.valueOf(string.get("cd_varchar50_2"));
					String testField = String.valueOf(string.get("cd_varchar50_5"));
					String testManField = String.valueOf(string.get("cd_varchar50_4"));
					String sqlNew = "update CUSTOMTABLEDETAIL set cd_varchar50_5='"+testField+"',cd_varchar50_4='"+testManField+"' where CD_CTID="+store.get("CT_ID")+
							" and cd_varchar50_2='"+versionNew+"'";
					updateNewGrid.add(sqlNew);
				}
		        baseDao.execute(updateNewGrid);
			}
		}
	}
}
