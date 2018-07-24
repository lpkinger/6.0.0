package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.ARCheck;
import com.uas.b2b.model.ARCheckDetail;
import com.uas.b2b.model.ARCheckReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;

/**
 * 作为卖家ERP，应收对账单
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleARCheckTask extends AbstractTask {
	@Autowired
	private EmployeeService employeeService;

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadARCheck start");
		uploadARCheck(master);
		logger.info(this.getClass() + " uploadARCheck end");
		logger.info(this.getClass() + " downloadReply start");
		downloadReply(master);
		logger.info(this.getClass() + " downloadReply end");
		logger.info(this.getClass() + " uploadReply start");
		uploadReply(master);
		logger.info(this.getClass() + " uploadReply end");
	}

	/**
	 * 上传应收对账单
	 * 
	 * @param enterprise
	 * @param master
	 */
	private boolean uploadARCheck(Master master) {
		List<ARCheck> arChecks = getARCheckUpload();
		if (!CollectionUtil.isEmpty(arChecks)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(arChecks));
			try {
				beforeUploadARCheck(arChecks);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/ARCheck?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					uploadARCheckSuccessed(arChecks);
				} else {
					uploadARCheckFailed(arChecks);
				}
				baseDao.save(new TaskLog("(买家)应收对账-上传应收对账单", arChecks.size(), response));
			} catch (Exception e) {
				uploadARCheckFailed(arChecks);
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传到应收对账单
	 * 
	 * @return
	 */
	private List<ARCheck> getARCheckUpload() {
		try {
			// 获取条件：状态码=AUDITED && 客户企业UU不为null && 上传状态为待上传 && 每次小于200条
			List<ARCheck> arChecks = baseDao
					.getJdbcTemplate()
					.query("select * from (select ARCHECK.*, customer.cu_uu ac_custuu from ARCHECK left join customer on ac_custcode=cu_code where ac_statuscode='AUDITED' and ac_sendstatus='待上传' and cu_uu is not null) where rownum <= 200",
							new BeanPropertyRowMapper<ARCheck>(ARCheck.class));
			for (ARCheck arCheck : arChecks) {
				List<ARCheckDetail> arCheckDetails = baseDao.getJdbcTemplate().query("select * from archeckdetail where AD_ACID = ? ",
						new BeanPropertyRowMapper<ARCheckDetail>(ARCheckDetail.class), arCheck.getAc_id());
				arCheck.setDetails(arCheckDetails);
			}
			return arChecks;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 修改已成功上传到平台的应收对账单的上传状态为已上传
	 * 
	 * @param arChecks
	 */
	private void uploadARCheckSuccessed(List<ARCheck> arChecks) {
		String idStr = CollectionUtil.getKeyString(arChecks, ",");
		String sql = "update archeck set ac_sendstatus='已上传' where ac_id in (" + idStr + ")";
		baseDao.execute(sql);
	}

	/**
	 * 上传到平台的应收对账单，上传之前上传状态为上传中
	 * 
	 * @param arChecks
	 */
	private void beforeUploadARCheck(List<ARCheck> arChecks) {
		String idStr = CollectionUtil.getKeyString(arChecks, ",");
		String sql = "update archeck set ac_sendstatus='上传中' where ac_id in (" + idStr + ")";
		baseDao.execute(sql);
	}

	/**
	 * 上传到平台的应收对账单，上传失败上传状态为待上传
	 * 
	 * @param arChecks
	 */
	private void uploadARCheckFailed(List<ARCheck> arChecks) {
		String idStr = CollectionUtil.getKeyString(arChecks, ",");
		String sql = "update archeck set ac_sendstatus='待上传' where ac_id in (" + idStr + ")";
		baseDao.execute(sql);
	}

	/**
	 * 从平台下载客户的对账回复记录
	 * 
	 * @return
	 */
	private boolean downloadReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/ARCheck/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<ARCheckReply> replies = FlexJsonUtil.fromJsonArray(data, ARCheckReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveArCheckReply(replies, master);
						baseDao.save(new TaskLog("(卖家)应付对账单-下载客户回复", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)采购单-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 修改平台里面的客户对账回复记录的状态
	 * 
	 * @return
	 */
	private boolean onReplySuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/ARCheck/reply/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存回复记录，并更新到采购单明细
	 * 
	 * @param replies
	 */
	public void saveArCheckReply(List<ARCheckReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			for (ARCheckReply reply : replies) {
				// 1、把reply的数量、备注、时间保存到对应对账明细
				sqls.add("update archeckdetail set ad_confirmqty=" + NumberUtil.nvl(reply.getReplyQty(), 0) + ", ad_confirmremark='"
						+ StringUtil.nvl(reply.getReplyRemark(), "") + "', ad_confirmdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, reply.getReplyDate()) + " where ad_id=" + reply.getSourceId());
				if (idStr.length() > 0)
					idStr.append(",");
				idStr.append(reply.getId());
			}
			// 2、修改所有涉及的明细的主表确认状态
			sqls.add("update archeck set ac_confirmstatus='已确认' where not exists (select 1 from archeckdetail where ad_qty<>nvl(ad_confirmqty,0) and archeckdetail.ad_acid=archeck.ac_id) ");
			baseDao.execute(sqls);
			// 反馈给平台修改传输状态
			onReplySuccess(idStr.toString(), master);
		}
	}

	/**
	 * 上传供应商回复的结果到平台
	 * 
	 * @return
	 */
	private boolean uploadReply(Master master) {
		List<ARCheckReply> replies = getReplies();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/ARCheck/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(买家)客户应付对账单-上传供应商回复", replies.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的供应商回复记录
	 * 
	 * @return
	 */
	private List<ARCheckReply> getReplies() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select ad_id sourceId, ad_qty replyQty, ad_date replyDate, ad_remark replyRemark from archeckdetail where ad_sendstatus='待上传'",
							new BeanPropertyRowMapper<ARCheckReply>(ARCheckReply.class));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 供应商回复记录成功传到平台之后
	 */
	private void onReplySuccess(List<ARCheckReply> replies) {
		StringBuffer idStr = new StringBuffer();
		for (ARCheckReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getSourceId());
		}
		baseDao.execute("update archeckdetail set ad_sendstatus='已上传' where ad_id in (" + idStr.toString() + ")");
	}

}