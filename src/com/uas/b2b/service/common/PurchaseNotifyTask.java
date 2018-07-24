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
import org.springframework.util.StringUtils;

import com.uas.b2b.model.AcceptNotify;
import com.uas.b2b.model.AcceptNotifyConfirm;
import com.uas.b2b.model.AcceptNotifyDetail;
import com.uas.b2b.model.AcceptNotifyVerify;
import com.uas.b2b.model.PurchaseNotify;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为买家ERP，将送货提醒传入平台、获取平台上的发货单
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseNotifyTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	/**
	 * 最大允许的明细条数<br>
	 * 数据量过大会出现url过长，服务器拒绝访问，返回400
	 */
	private static final int max_size = 500;

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadPurchaseNotify start");
			uploadPurchaseNotify(master);
			logger.info(this.getClass() + " uploadPurchaseNotify end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(this.getClass() + " downloadAcceptNotify start");
		// 下载客户发货单
		downloadAcceptNotify(master);
		logger.info(this.getClass() + " downloadAcceptNotify end");
		logger.info(this.getClass() + " uploadPurchaseNotifyEnd start");
		// 结案送货提醒(取消送货通知)
		uploadPurchaseNotifyEnd(master);
		logger.info(this.getClass() + " uploadPurchaseNotifyEnd end");
		logger.info(this.getClass() + " uploadAcceptNotify start");
		uploadAcceptNotify(master);
		logger.info(this.getClass() + " uploadAcceptNotify end");
		logger.info(this.getClass() + " uploadAcceptNotifyConfirm start");
		// 修改收料通知确认数
		uploadAcceptNotifyConfirm(master);
		logger.info(this.getClass() + " uploadAcceptNotifyConfirm end");
		logger.info(this.getClass() + " uploadAcceptNotifyVerify start");
		// 上传收料单的收料数量
		uploadAcceptNotifyVerify(master);
		logger.info(this.getClass() + " uploadAcceptNotifyVerify end");
		logger.info(this.getClass() + " uploadAcceptUnauditVerify start");
		// 上传反审核的收料单
		uploadAcceptUnauditVerify(master);
		logger.info(this.getClass() + " uploadAcceptUnauditVerify end");
	}

	/**
	 * 上传送货提醒
	 * 
	 * @return
	 */
	private boolean uploadPurchaseNotify(Master master) {
		List<PurchaseNotify> notifies = getPurchaseNotifyUpload();
		if (!CollectionUtil.isEmpty(notifies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(notifies));
			try {
				beforeUploadedPurchaseNotify(notifies);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/notice?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedPurchaseNotify(notifies);
					String responseText = response.getResponseText();
					if (StringUtils.hasText(responseText)) {
						List<PurchaseNotify> modifiedPurchaseNotify = FlexJsonUtil.fromJsonArray(responseText, PurchaseNotify.class);
						updateModifiedPurchaseNotify(modifiedPurchaseNotify);
					}
					baseDao.save(new TaskLog("(买家)送货提醒-上传送货提醒", notifies.size(), response));
					return uploadPurchaseNotify(master);
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的送货提醒
	 * 
	 * @return
	 */
	public List<PurchaseNotify> getPurchaseNotifyUpload() {
		try {
			List<PurchaseNotify> notifies = baseDao
					.getJdbcTemplate()
					.query("select * from (select purchasenotify.*,ve_uu,pr_zxbzs from purchasenotify left join purchase on pn_ordercode=pu_code left join vendor on pu_vendcode=ve_code left join product on pr_code=pn_prodcode where (nvl(PN_SENDSTATUS,' ')='待上传' or nvl(pn_sendstatus,' ')='上传中') and pn_status<>'已取消' and pu_sendstatus='已上传' and nvl(pu_ordertype,' ')<>'B2C' and ve_uu is not null and nvl(ve_b2benable,0)=1 order by pn_indate) where rownum <= ?",
							new BeanPropertyRowMapper<PurchaseNotify>(PurchaseNotify.class), max_size);
			return notifies;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 更新上传修改之后的单据返回的结果（修改发货提醒的需求数）
	 * 
	 * @param str
	 */
	public void updateModifiedPurchaseNotify(List<PurchaseNotify> notifies) {
		List<String> sqls = new ArrayList<String>();
		for (PurchaseNotify notify : notifies) {
			sqls.add("update purchasenotify set pn_qty=" + notify.getPn_qty() + ", pn_remark='修改需求数异常，供应商已发货" + notify.getPn_qty()
					+ "' where pn_id=" + notify.getPn_id());
		}
		baseDao.execute(sqls);
	}

	/**
	 * 更新已上传的送货提醒单状态
	 * 
	 * @param notifies
	 *            已上传的送货提醒
	 */
	public void updateUploadedPurchaseNotify(List<PurchaseNotify> notifies) {
		for (PurchaseNotify notify : notifies) {
			baseDao.execute("update PurchaseNotify set PN_SENDSTATUS='已上传' where pn_id = ? and pn_sendstatus='上传中'", notify.getPn_id());
		}
	}

	/**
	 * 上传前，修改送货提醒的状态
	 */
	public void beforeUploadedPurchaseNotify(List<PurchaseNotify> notifies) {
		for (PurchaseNotify notify : notifies) {
			baseDao.execute("update PurchaseNotify set PN_SENDSTATUS='上传中' where pn_id = ?", notify.getPn_id());
		}
	}

	/**
	 * 从平台下载供应商下达的发货单
	 * 
	 * @return
	 */
	private boolean downloadAcceptNotify(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/notice/accept?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<AcceptNotify> accepts = FlexJsonUtil.fromJsonArray(data, AcceptNotify.class);
					if (!CollectionUtil.isEmpty(accepts)) {
						saveAcceptNotify(accepts, master);
						baseDao.save(new TaskLog("(买家)送货提醒-下载发货单", accepts.size(), response));
						saveB2BMessage(accepts);
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)送货提醒-下载发货单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存成功，保存消息通知
	 *
	 * @param details
	 */
	private void saveB2BMessage(List<AcceptNotify> accepts) {
		StringBuffer idStringBuffer = new StringBuffer();
		for (AcceptNotify accept : accepts) {
			if (idStringBuffer.length() > 0) {
				idStringBuffer.append(",");
			}
			idStringBuffer.append(accept.getAn_id());
		}
		if (idStringBuffer.length() > 0) {
			// 产生消息
			pagingReleaseService.B2BMsg("AcceptNotify", String.valueOf(idStringBuffer), "confirm");
		}
	}

	/**
	 * 保存收料通知
	 * 
	 * @param accepts
	 */
	public void saveAcceptNotify(List<AcceptNotify> accepts, Master master) {
		if (!CollectionUtil.isEmpty(accepts)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer anStr = new StringBuffer();
			StringBuffer pnStr = new StringBuffer();
			for (AcceptNotify accept : accepts) {
				boolean isExist = baseDao.checkIf("AcceptNotify", "b2b_ss_id=" + accept.getB2b_ss_id());
				if (!isExist) {
					int anId = baseDao.getSeqId("AcceptNotify_seq");
					if (anStr.length() > 0)
						anStr.append(",");
					anStr.append(anId);
					sqls.addAll(accept.toCascadedSqlString(anId, baseDao.sGetMaxNumber("AcceptNotify", 2)));
					if (!CollectionUtil.isEmpty(accept.getDetails())) {
						for (AcceptNotifyDetail detail : accept.getDetails()) {
							if (detail.getAnd_pnid() != null) {
								if (pnStr.length() > 0)
									pnStr.append(",");
								pnStr.append(detail.getAnd_pnid());
							}
						}
					}
				}
			}
			if (anStr.length() > 0) {
				sqls.add("update AcceptNotify set (an_currency,an_rate,an_paymentcode,an_payment,an_buyer,an_buyerid)=(select pu_currency,pu_rate,pu_paymentscode,pu_payments,pu_buyername,em_id from purchase left join acceptnotifydetail on pu_code=and_ordercode left join employee on pu_buyercode=em_code where and_anid=an_id and rownum=1) where an_id in ("
						+ anStr.toString() + ")");
				sqls.add("update AcceptNotify set (an_vendcode,an_vendname)=(select ve_code,ve_name from vendor where ve_uu=to_char(an_venduu)) where an_id in ("
						+ anStr.toString() + ")");
				sqls.add("update AcceptNotify set (an_vendcode,an_vendname)=(select pu_vendcode,pu_vendname from purchase left join acceptnotifydetail on pu_code=and_ordercode where and_anid=an_id and rownum=1) where an_id in ("
						+ anStr.toString() + ") and an_vendcode is null");
				sqls.add("update AcceptNotifyDetail set (and_orderid,and_prodcode,and_taxrate)=(select pd_id,pd_prodcode,nvl(pd_rate,0) from purchasedetail left join purchase on pd_puid=pu_id where pu_code=and_ordercode and pd_detno=and_orderdetno) where and_anid in ("
						+ anStr.toString() + ")");
				baseDao.execute(sqls);
				baseDao.execute("update purchasenotify set pn_endqty=(select sum(and_inqty) from AcceptNotifyDetail where and_pnid=pn_id) where pn_id in ("
						+ pnStr.toString() + ")");
				baseDao.execute("update purchasenotify set pn_status='部分发货' where nvl(pn_endqty,0)>0 and nvl(pn_endqty,0)<nvl(pn_qty,0) and pn_id in ("
						+ pnStr.toString() + ")");
				baseDao.execute("update purchasenotify set pn_status='已发货' where nvl(pn_endqty,0)=nvl(pn_qty,0) and pn_id in ("
						+ pnStr.toString() + ")");
				try {
					for (AcceptNotify accept : accepts) {
						if (accept.getAn_buyeruu() != null) {
							Employee employee = employeeService.getEmployeeByUu(accept.getAn_buyeruu());
							if (employee != null) {
								// String veName =
								// getVendorByUU(accept.getAn_venduu());
								// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
								// employee.getEm_code(), "收到新的收料通知 ", "供应商:"
								// + veName + ",送货单号:" +
								// accept.getAn_sendcode(), null);
								// XingePusher
								// .pushSingleAccountIOS(master.getMa_user(),
								// employee.getEm_code(), "收到新的收料通知  " + "供应商:"
								// + veName);
							}
						}
					}
				} catch (Exception e) {

				}
				onNotifySuccess(accepts, master);
			}
		}
	}

	/**
	 * 修改平台里面的发货单的上传状态
	 * 
	 * @return
	 */
	private boolean onNotifySuccess(List<AcceptNotify> accepts, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotify accept : accepts) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(accept.getB2b_ss_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/notice/accept/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传主动维护的收料通知到平台
	 * 
	 * @return
	 */
	private boolean uploadAcceptNotify(Master master) {
		List<AcceptNotify> accepts = getAcceptNotify();
		if (!CollectionUtil.isEmpty(accepts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(accepts));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/notice/accept?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onNoticeSuccess(accepts);
				baseDao.save(new TaskLog("(买家)送货提醒-上传主动收料", accepts.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传收料通知修改的确认数
	 * 
	 * @return
	 */
	private boolean uploadAcceptNotifyConfirm(Master master) {
		List<AcceptNotifyConfirm> confirms = getAcceptNotifyConfirm();
		if (!CollectionUtil.isEmpty(confirms)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(confirms));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/notice/accept/confirm?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onConfirmSuccess(confirms);
				baseDao.save(new TaskLog("(买家)送货提醒-上传收料通知确认数", confirms.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的主动维护的收料通知
	 * 
	 * @return
	 */
	private List<AcceptNotify> getAcceptNotify() {
		try {
			List<AcceptNotify> accepts = baseDao
					.getJdbcTemplate()
					.query("select AcceptNotify.* from AcceptNotify left join vendor on an_vendcode=ve_code where AN_SENDSTATUS='待上传' and an_statuscode='AUDITED' and ve_uu is not null and nvl(ve_b2benable,0)=1 order by an_date",
							new BeanPropertyRowMapper<AcceptNotify>(AcceptNotify.class));
			for (AcceptNotify accept : accepts) {
				List<AcceptNotifyDetail> details = baseDao.getJdbcTemplate().query("select * from AcceptNotifyDetail where and_anid=?",
						new BeanPropertyRowMapper<AcceptNotifyDetail>(AcceptNotifyDetail.class), accept.getAn_id());
				accept.setDetails(details);
			}
			return accepts;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 未上传到平台的收料通知确认信息
	 * 
	 * @return
	 */
	private List<AcceptNotifyConfirm> getAcceptNotifyConfirm() {
		try {
			List<AcceptNotifyConfirm> confirms = baseDao
					.getJdbcTemplate()
					.query("select b2b_ss_id,and_id,and_detno,and_inqty from AcceptNotify left join vendor on an_vendcode=ve_code left join AcceptNotifyDetail on and_anid=an_id where b2b_ss_id is not null and an_statuscode in ('AUDITED','TURNVA') and and_sendstatus='待上传' and and_inqty<nvl(and_b2bqty,and_inqty) and ve_uu is not null and nvl(ve_b2benable,0)=1 order by b2b_ss_id,and_detno",
							new BeanPropertyRowMapper<AcceptNotifyConfirm>(AcceptNotifyConfirm.class));
			return confirms;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 主动维护的收料通知成功传到平台之后
	 */
	private void onNoticeSuccess(List<AcceptNotify> accepts) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotify accept : accepts) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(accept.getAn_id());
		}
		baseDao.execute("update AcceptNotify set an_sendstatus='已下载' where an_id in (" + idStr.toString() + ")");
	}

	/**
	 * 收料通知确认信息成功传到平台之后
	 */
	private void onConfirmSuccess(List<AcceptNotifyConfirm> confirms) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotifyConfirm confirm : confirms) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(confirm.getAnd_id());
		}
		baseDao.execute("update AcceptNotifyDetail set and_sendstatus='已下载',and_b2bqty=and_inqty where and_id in (" + idStr.toString()
				+ ")");
		// 更新主记录的收料状态
		baseDao.execute("update AcceptNotify set an_statuscode='TURNVA',an_status='"
				+ BaseUtil.getLocalMessage("TURNVA")
				+ "' where exists (select 1 from AcceptNotifydetail where and_id in ("
				+ idStr.toString()
				+ ") and and_anid=an_id) and not exists (select 1 from AcceptNotifydetail where and_anid=an_id and and_inqty>NVL(and_yqty,0))");
	}

	/**
	 * 上传结案送货提醒
	 * 
	 * @return
	 */
	private boolean uploadPurchaseNotifyEnd(Master master) {
		List<PurchaseNotify> notifies = getPurchaseNotifyEndUpload();
		if (!CollectionUtil.isEmpty(notifies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(notifies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/notice/end?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedPurchaseEndNotify(notifies);
					baseDao.save(new TaskLog("(买家)送货提醒-上传结案状态", notifies.size(), response));
					return uploadPurchaseNotifyEnd(master);// 递归传所有结案送货提醒
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的已取消送货提醒
	 * 
	 * @return
	 */
	public List<PurchaseNotify> getPurchaseNotifyEndUpload() {
		try {
			List<PurchaseNotify> notifies = baseDao
					.getJdbcTemplate()
					.query("select * from (select purchasenotify.pn_id,ve_uu from purchasenotify left join purchase on pn_ordercode=pu_code left join vendor on pu_vendcode=ve_code where PN_SENDSTATUS='待上传' and pn_statuscode='CANCELED' and pu_sendstatus='已上传' and nvl(pu_ordertype,' ')<>'B2C' and ve_uu is not null and nvl(ve_b2benable,0)=1 order by pn_indate) where rownum<=500",
							new BeanPropertyRowMapper<PurchaseNotify>(PurchaseNotify.class));
			return notifies;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 更新已上传的送货提醒单状态
	 * 
	 * @param notifies
	 *            已上传的送货提醒
	 */
	public void updateUploadedPurchaseEndNotify(List<PurchaseNotify> notifies) {
		for (PurchaseNotify notify : notifies) {
			baseDao.execute("update PurchaseNotify set PN_SENDSTATUS='已上传' where pn_id = ?", notify.getPn_id());
		}
	}

	/**
	 * 上传已审核的收料单收料数
	 * 
	 * @return
	 */
	private boolean uploadAcceptNotifyVerify(Master master) {
		List<AcceptNotifyVerify> verifies = getAcceptNotifyVerify();
		if (!CollectionUtil.isEmpty(verifies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(verifies));
			try {
				beforeVerifyUpload(verifies);// 上传前修改状态为上传中
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/notice/accept/verify?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onVerifySuccess(verifies);// 上传成功修改状态为已上传
				else
					failedVerifyUpload(verifies);// 上传失败修改状态为待上传
			} catch (Exception e) {
				failedVerifyUpload(verifies);// 上传失败修改状态为待上传
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传到的收料单
	 * 
	 * @return
	 */
	private List<AcceptNotifyVerify> getAcceptNotifyVerify() {
		try {
			List<AcceptNotifyVerify> verifies = baseDao
					.getJdbcTemplate()
					.query("select vad_id, acceptnotify.b2b_ss_id, ACCEPTNOTIFYDETAIL.and_detno, ve_code, vad_jyqty, ve_okqty, ve_notokqty,ve_testman, ve_date from VERIFYAPPLYDETAIL left join VERIFYAPPLY on vad_vaid = va_id left join ACCEPTNOTIFYDETAIL on vad_andid=and_id  left join acceptnotify on and_anid=an_id where VERIFYAPPLYDETAIL.ve_status='已审核' and VERIFYAPPLYDETAIL.vad_sendstatus='待上传' and acceptnotify.b2b_ss_id is not null and rownum <= 200 ",
							new BeanPropertyRowMapper<AcceptNotifyVerify>(AcceptNotifyVerify.class));
			return verifies;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 上传反审核的收料单收料数
	 * 
	 * @return
	 */
	private boolean uploadAcceptUnauditVerify(Master master) {
		List<AcceptNotifyVerify> verifies = getAcceptUnauidtVerify();
		if (!CollectionUtil.isEmpty(verifies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(verifies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/notice/accept/unverify?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onVerifySuccess(verifies);// 上传成功修改状态为已上传
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取已反审核的收料单
	 * 
	 * @return
	 */
	private List<AcceptNotifyVerify> getAcceptUnauidtVerify() {
		try {
			List<AcceptNotifyVerify> verifies = baseDao
					.getJdbcTemplate()
					.query("select vad_id, acceptnotify.b2b_ss_id, ACCEPTNOTIFYDETAIL.and_detno from VERIFYAPPLYDETAIL left join VERIFYAPPLY on vad_vaid = va_id left join ACCEPTNOTIFYDETAIL on vad_andid=and_id left join acceptnotify on and_anid=an_id where VERIFYAPPLYDETAIL.ve_status<>'已审核' and VERIFYAPPLYDETAIL.vad_sendstatus='上传中' and acceptnotify.b2b_ss_id is not null and rownum <= 50",
							new BeanPropertyRowMapper<AcceptNotifyVerify>(AcceptNotifyVerify.class));
			return verifies;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 收料单收料数成功传到平台之后
	 */
	private void onVerifySuccess(List<AcceptNotifyVerify> verifies) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotifyVerify verify : verifies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(verify.getVad_id());
		}
		baseDao.execute("update verifyapplydetail set vad_sendstatus='已上传' where vad_id in (" + idStr.toString() + ")");
	}

	/**
	 * 收料单收料数传到平台之前
	 */
	private void beforeVerifyUpload(List<AcceptNotifyVerify> verifies) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotifyVerify verify : verifies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(verify.getVad_id());
		}
		baseDao.execute("update verifyapplydetail set vad_sendstatus='上传中' where vad_id in (" + idStr.toString() + ")");
	}

	/**
	 * 收料单收料数传到平台失败
	 */
	private void failedVerifyUpload(List<AcceptNotifyVerify> verifies) {
		StringBuffer idStr = new StringBuffer();
		for (AcceptNotifyVerify verify : verifies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(verify.getVad_id());
		}
		baseDao.execute("update verifyapplydetail set vad_sendstatus='待上传' where vad_id in (" + idStr.toString() + ")");
	}

}
