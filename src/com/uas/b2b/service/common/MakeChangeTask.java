package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Make;
import com.uas.b2b.model.MakeChange;
import com.uas.b2b.model.MakeChangeDetail;
import com.uas.b2b.model.MakeChangeReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;

@Component
@EnableAsync
@EnableScheduling
public class MakeChangeTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;
	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadMakeChange start");
		uploadMakeChange(master);// 上传委外变更单
		logger.info(this.getClass() + " uploadMakeChange end");
	}

	/**
	 * 上传委外变更单
	 * 
	 * @return
	 */
	private boolean uploadMakeChange(Master master) {
		List<MakeChange> changes = getMakeChangeUpload();
		if (!CollectionUtil.isEmpty(changes)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(changes));
			String idStr = CollectionUtil.getKeyString(changes, ",");
			try {
				beforeUploadedMakeChanges(idStr);
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make/change?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedMakeChanges(idStr);
				} else {
					onMakeChangesUploadedFaild(idStr);
				}
				baseDao.save(new TaskLog("(买家)委外变更-上传委外变更单", changes.size(), response));
			} catch (Exception e) {
				onMakeChangesUploadedFaild(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的委外变更单
	 * 
	 * @return
	 */
	public List<MakeChange> getMakeChangeUpload() {
		try {
			List<MakeChange> makeChanges = baseDao
					.getJdbcTemplate()
					.query("select * from (select mc.* from makechange mc where MC_SENDSTATUS='待上传' and exists (select 1 from makechangedetail mcd left join make m on mcd.md_makecode=m.ma_code where ma_sendstatus='已上传')) where rownum<=100",
							new BeanPropertyRowMapper<MakeChange>(MakeChange.class));
			for (MakeChange makeChange : makeChanges) {
				List<MakeChangeDetail> changeDetails = baseDao.getJdbcTemplate().query(
						"select * from makechangedetail left join make on md_makecode=ma_code where md_mcid=? and ma_sendstatus='已上传'",
						new BeanPropertyRowMapper<MakeChangeDetail>(MakeChangeDetail.class), makeChange.getMc_id());
				makeChange.setChangeDetails(changeDetails);
			}
			return makeChanges;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 上传前修改委外加工单变更单状态未上传中
	 * 
	 * @param changes
	 *            待上传的委外加工单变更单
	 */
	public void beforeUploadedMakeChanges(String idStr) {
		baseDao.execute("update makechange set mc_sendstatus='上传中' where mc_id in (" + idStr + ")");
	}

	/**
	 * 更新已上传的委外加工单变更单状态为已上传
	 * 
	 * @param changes
	 *            已上传的委外加工单变更单
	 */
	public void updateUploadedMakeChanges(String idStr) {
		baseDao.execute("update makechange set mc_sendstatus='已上传' where mc_id in (" + idStr + ")");
	}

	/**
	 * 上传失败的委外加工单变更单状态修改为待上传
	 * 
	 * @param changes
	 *            上传失败的委外加工单变更单
	 */
	public void onMakeChangesUploadedFaild(String idStr) {
		baseDao.execute("update makechange set mc_sendstatus='待上传' where mc_id in (" + idStr + ") and mc_sendstatus='上传中'");
	}

	/**
	 * 从平台下载供应商对变更单的回复信息<br>
	 * <b>后经确认，委外变更单无需供应商确认--2015年8月11日09:01:13</b>
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean downloadChangeReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/make/change/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<MakeChangeReply> replies = FlexJsonUtil.fromJsonArray(data, MakeChangeReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveMakeChangeReply(replies, master);
						baseDao.save(new TaskLog("(买家)委外变更变更-下载回复", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)委外变更-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存回复记录，并更新到采购单明细
	 * 
	 * @param replies
	 */
	public void saveMakeChangeReply(List<MakeChangeReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bIdStr = new StringBuffer();
			for (MakeChangeReply reply : replies) {
				if (b2bIdStr.length() > 0)
					b2bIdStr.append(",");
				b2bIdStr.append(reply.getB2b_md_id());
				Long md_mcid = baseDao.getFieldValue("makechange", "mc_id", "mc_code='" + reply.getMc_code() + "'", Long.class);
				sqls.add("update makechangedetail set b2b_md_id=" + reply.getB2b_md_id() + ", md_agreed="
						+ NumberUtil.nvl(reply.getMd_agreed(), 0) + ",md_replyremark='" + StringUtil.nvl(reply.getMd_replyremark(), "")
						+ "', md_replyman='" + StringUtil.nvl(reply.getMd_replyman(), "") + "', md_replydate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, reply.getMd_replydate()) + " where md_mcid=" + md_mcid
						+ " and md_detno=" + reply.getMd_detno());
				if (reply.isAgreed()) {
					// 变更委外加工单信息
					sqls.add("update make set (ma_qty, ma_price, ma_planbegindate, ma_planenddate)=(select md_newqty, md_newprice, md_newplanbegindate, md_newplanenddate from makechangedetail where md_mcid="
							+ md_mcid + " and md_detno=" + reply.getMd_detno() + ") where ma_code='" + reply.getMa_code() + "'");
				} else {
					// 不同意的情况
					sqls.add("update makechangedetail set md_agreed=0, md_status='" + Status.CONFIRMED.display() + "', md_statuscode='"
							+ Status.CONFIRMED.code() + "' where md_mcid=" + md_mcid + " and md_detno=" + reply.getMd_detno() + "");
				}
			}
			baseDao.execute(sqls);

			// 消息推送
			try {
				for (MakeChangeReply reply : replies) {
					Make make = getMakeByChangeCode(reply.getMa_code());
					if (make != null) {
						Employee employee = employeeService.getEmployeeById(make.getMa_recorderid());
						if (employee != null && employee.getEm_code() != null) {
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), employee.getEm_code(), "委外变更单收到回复 ",
							// "供应商:" + make.getMa_vendname() + ",单号:" + make.getMa_code(), null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), employee.getEm_code(),
							// "委外变更单收到回复  " + "供应商:" + make.getMa_vendname());
						}
					}
				}
			} catch (Exception e) {

			}
			// 保存成功反馈平台修改传输状态
			onChangeReplySuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 根据委外加工单号获取委外加工单
	 * 
	 * @param pc_code
	 *            变更单号
	 * @return
	 */
	private Make getMakeByChangeCode(String pc_code) {
		return baseDao.getJdbcTemplate().queryForObject("select * from make where ma_code='" + pc_code + "'",
				new BeanPropertyRowMapper<Make>(Make.class));
	}

	/**
	 * 修改平台里面的委外变更单的回复状态
	 * 
	 * @return
	 */
	private boolean onChangeReplySuccess(String b2bIdStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", b2bIdStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/make/change/reply?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
