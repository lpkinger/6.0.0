package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.Attach;
import com.uas.b2b.model.InquiryMould;
import com.uas.b2b.model.InquiryMouldDecide;
import com.uas.b2b.model.InquiryMouldDet;
import com.uas.b2b.model.InquiryMouldDetail;
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;
import com.uas.erp.service.oa.PagingReleaseService;
import com.uas.erp.service.pm.InquiryMouldService;

/***
 * 作为买家ERP，将模具报价单上传到平台，获取平台上询价单**
 * 
 * @author hejq
 **/
@Component
@EnableAsync
@EnableScheduling
public class InquiryMouldTask extends AbstractTask {

	// @Autowired
	// private EmployeeService employeeService;

	@Autowired
	private InquiryMouldService inquiryMouldService;

	@Autowired
	private PagingReleaseService pagingReleaseService;
	/**
	 * 最大允许的明细条数<br>
	 * 数据量过大会出现url过长，服务器拒绝访问，返回400
	 */
	// private static final int max_size = 500;

	@Override
	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadProduct start");
			if (uploadProduct(master)) {
				logger.info(this.getClass() + " uploadProduct end");
				logger.info(this.getClass() + " uploadInquiryMould start");
				uploadInquiryMould(master);
				logger.info(this.getClass() + " uploadInquiryMould end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(this.getClass() + " uploadReply start");

		// 下载报价回复
		downloadMouldReply(master);
		logger.info(this.getClass() + " uploadReply end");
		logger.info(this.getClass() + " uploadCheckInquiryMould start");

		// 询价单提交后，上传平台
		uploadCheckInquiryMould(master);
		logger.info(this.getClass() + " uploadCheckInquiryMould end");

		logger.info(this.getClass() + " uploadReCheckInquiryMould start");

		// 询价单反提交后，上传平台
		uploadReCheckInquiryMould(master);
		logger.info(this.getClass() + " uploadReCheckInquiryMould end");
		logger.info(this.getClass() + " uploadInquiryMouldDecide start");

		// 对买家的报价审核后上传至平台
		uploadInquiryMouldDecide(master);
		logger.info(this.getClass() + " uploadInquiryMouldDecide end");

		logger.info(this.getClass() + " invalidateInquiryMould start");
		// 买家作废模具询价单之后，上传至平台
		invalidateInquiryMould(master);
		logger.info(this.getClass() + " invalidateInquiryMould end");
	}

	/**
	 * 上传物料资料
	 * 
	 * @param en
	 * 
	 * @return
	 */
	private boolean uploadProduct(Master master) {
		List<Prod> prods = getProducts();
		if (!CollectionUtil.isEmpty(prods)) {
			String idStr = CollectionUtil.getKeyString(prods, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(prods));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeProductUpload(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/product?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductUploadSuccess(idStr);
					return uploadProduct(master);// 递归传所有物料
				} else
					onProductUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)模具询价单-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onProductUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 更新上传中的物料资料
	 * 
	 * @param idStr
	 *            已上传的物料资料
	 */
	public void beforeProductUpload(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='上传中' where pr_id in (" + idStr + ")");
	}

	/**
	 * 更新已上传的物料资料
	 * 
	 * @param idStr
	 *            已上传的物料资料
	 */
	public void onProductUploadSuccess(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='已上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的物料资料
	 * 
	 * @param idStr
	 *            上传失败的物料资料
	 */
	public void onProductUploadFail(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='待上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 上传物料
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where exists(select 1 from inquirymoulddetail left join inquirymoulddet on ind_iddid=idd_id left join inquirymould on idd_inid=in_id left join vendor on in_vendcode=ve_code  where (IN_SENDSTATUS='待上传' or IN_SENDSTATUS='上传中') and nvl(in_sourcetype,' ')<>'模具报价单'and ve_uu is not null and pr_code=ind_prodcode and pr_sendstatus<>'已上传')) where rownum <= 500",
					new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (

		EmptyResultDataAccessException e)

		{
			return null;
		}

	}

	/**
	 * 上传模具询价单
	 * 
	 * @return
	 */
	private boolean uploadInquiryMould(Master master) {
		List<InquiryMould> inquiries = getInquiryMouldUpload();
		if (!CollectionUtil.isEmpty(inquiries)) {
			String idStr = CollectionUtil.getKeyString(inquiries, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(inquiries));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeInquiryMouldUpload(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/inquiryMould?access_id=" + master.getMa_uu(), params,
						true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInquiryMouldUploadSuccess(idStr);
				} else
					onInquiryMouldUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)模具询价单-上传模具询价单", inquiries.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onInquiryMouldUploadFail(idStr);
				return false;
			} finally {
				checkInquiryMouldUpload(idStr);
			}
		}
		return true;
	}

	/**
	 * 模具询价单对应的报价单提交后上传平台
	 * 
	 * @param enterprise
	 * @param master
	 */
	private void uploadCheckInquiryMould(Master master) {
		String inCheck = getCheckInquiryMould();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite()
						+ "/erp/purchase/inquiryMould/checking?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onCheckInquiryUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)模具询价单-上传模具询价单提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 模具询价单对应的报价单反提交后上传平台
	 * 
	 * @param enterprise
	 * @param master
	 */
	private void uploadReCheckInquiryMould(Master master) {
		String inCheck = getReCheckInquiryMould();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/inquiryMould/reCheck?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onReCheckInquiryUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)模具询价单-上传模具询价单提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 找出报价单状态为已提交的对应询价单
	 */
	private String getCheckInquiryMould() {
		return baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(b2b_im_id) from (select b2b_im_id from inquirymould where in_checksendstatus = '待上传' and in_sendstatus='已上传' and in_sourceid in (select pd_id from pricemould where pd_status ='已提交') order by in_id) where rownum < 100",
				String.class);
	}

	/**
	 * 提交成功后将checksendstatus更新为已上传
	 * 
	 * @param inIds
	 */
	private void onCheckInquiryUploadSuccess(String inIds) {
		baseDao.execute("update inquirymould set in_checksendstatus='已上传' where b2b_im_id in (" + inIds + ")");
	}

	/**
	 * 找出报价单状态为在录入，且提交上传状态为已上传的对应询价单
	 */
	private String getReCheckInquiryMould() {
		return baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(b2b_im_id) from (select b2b_im_id from inquirymould where in_checksendstatus = '已上传' and in_sendstatus='已上传' and in_sourceid in (select pd_id from pricemould where pd_status ='在录入') order by in_id) where rownum < 100",
				String.class);
	}

	/**
	 * 反提交成功后重新将checkstatus更新为待上传
	 * 
	 * @param inIds
	 */
	private void onReCheckInquiryUploadSuccess(String inIds) {
		baseDao.execute("update inquirymould set in_checksendstatus='待上传' where b2b_im_id in (" + inIds + ")");
	}

	/**
	 * 获取需要上传的模具询价单
	 * 
	 * @return
	 */
	public List<InquiryMould> getInquiryMouldUpload() {
		try {
			List<InquiryMould> inquiries = baseDao.getJdbcTemplate().query(
					"select InquiryMould.*,ve_uu in_veuu,em_uu in_recorderuu from InquiryMould left join employee on in_recorderid = em_id left join vendor on ve_code=in_vendcode where (in_statuscode='AUDITED' and (IN_SENDSTATUS='待上传' or IN_SENDSTATUS='上传中')) and nvl(in_sourcetype,' ')='模具报价单' and exists (select 1 from InquiryMould left join vendor on in_vendcode=ve_code and ve_uu is not null and nvl(ve_b2benable,0)=1) order by in_code",
					new BeanPropertyRowMapper<InquiryMould>(InquiryMould.class));
			for (InquiryMould inquiry : inquiries) {
				List<InquiryMouldDet> dets = baseDao.getJdbcTemplate().query(
						"select InquiryMouldDet.* from InquiryMouldDet where idd_inid = ? ",
						new BeanPropertyRowMapper<InquiryMouldDet>(InquiryMouldDet.class), inquiry.getIn_id());
				for (InquiryMouldDet det : dets) {
					List<InquiryMouldDetail> detail = baseDao.getJdbcTemplate().query(
							"select InquiryMouldDetail.* from InquiryMouldDetail where ind_iddid = ?",
							new BeanPropertyRowMapper<InquiryMouldDetail>(InquiryMouldDetail.class), det.getIdd_id());
					det.setDetails(detail);
				}
				inquiry.setItems(dets);

				// 获取模具询价单的附件信息
				if (StringUtil.hasText(inquiry.getIn_attach())) {
					String[] fileIds = inquiry.getIn_attach().split(";");
					String erpUrl = getEnterpriseErpUrl();
					List<Attach> attaches = baseDao.getJdbcTemplate()
							.query("select fp_id, fp_size, fp_name from filepath where fp_id in ("
									+ StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
									new BeanPropertyRowMapper<Attach>(Attach.class));
					if (!CollectionUtil.isEmpty(attaches)) {
						for (Attach attach : attaches) {
							attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
						}
						inquiry.setAttaches(attaches);
					}
				}
			}
			return inquiries;
		} catch (

		EmptyResultDataAccessException e)

		{
			return null;
		}

	}

	/**
	 * 获取企业UAS外网地址
	 * 
	 * @return
	 */
	private String getEnterpriseErpUrl() {
		String erpUrl = baseDao.getJdbcTemplate().queryForObject("select max(en_erpurl) from enterprise", String.class);
		if (!StringUtils.isEmpty(erpUrl) && erpUrl.endsWith("/")) {
			erpUrl = erpUrl.substring(0, erpUrl.length() - 1);
		}
		return erpUrl;
	}

	/**
	 * 更新待上传的模具询价单状态
	 * 
	 * @param idStr
	 *            待上传的模具询价单
	 */
	public void beforeInquiryMouldUpload(String idStr) {
		baseDao.execute("update inquiryMould set IN_SENDSTATUS='上传中' where in_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的模具询价单状态
	 * 
	 * @param idStr
	 *            上传成功的模具询价单
	 */
	public void onInquiryMouldUploadSuccess(String idStr) {
		baseDao.execute("update inquiryMould set IN_SENDSTATUS='已上传' where in_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的模具询价单状态
	 * 
	 * @param idStr
	 *            上传失败的模具询价单
	 */
	public void onInquiryMouldUploadFail(String idStr) {
		baseDao.execute(
				"update inquiryMould set IN_SENDSTATUS='待上传' where in_id in (" + idStr + ") and IN_SENDSTATUS='上传中'");
	}

	/**
	 * 防止意外情况，状态一直处于上传中
	 * 
	 * @param idStr
	 */
	public void checkInquiryMouldUpload(String idStr) {
		baseDao.execute(
				"update inquiryMould set IN_SENDSTATUS='待上传' where in_id in (" + idStr + ") and IN_SENDSTATUS='上传中'");
	}

	/**
	 * 从平台下载供应商的报价
	 * 
	 * @return
	 */
	private boolean downloadMouldReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/inquiryMould/reply?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<InquiryMould> details = FlexJsonUtil.fromJsonArray(data, InquiryMould.class);
					if (!CollectionUtil.isEmpty(details)) {
						details = saveInquiryMouldReply(details, master);
						baseDao.save(new TaskLog("(买家)模具询价单-下载模具报价", details.size(), response));
						saveB2BMessage(details);
					}
				}
			} else {
				baseDao.save(new TaskLog("(买家)模具询价单-下载模具报价", 0, response));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 报价成功，保存消息通知
	 *
	 * @param details
	 */
	private void saveB2BMessage(List<InquiryMould> details) {
		for (InquiryMould mould : details) {
			// 产生消息
			if (mould.isSave()) {
				pagingReleaseService.B2BMsg("Inquiry!Mould", String.valueOf(mould.getIn_id()), "save");
			} else {
				pagingReleaseService.B2BMsg("Inquiry!Mould", String.valueOf(mould.getIn_id()), "update");
			}
		}

	}

	/**
	 * 保存来自供应商的报价信息
	 * 
	 * @param replies
	 */
	public List<InquiryMould> saveInquiryMouldReply(List<InquiryMould> moulds, Master master) {
		if (!CollectionUtils.isEmpty(moulds)) {
			List<String> sqls = new ArrayList<String>();
			for (InquiryMould mould : moulds) {
				// 判断是否第一次报价
				Long b2b_im_id = baseDao.getFieldValue("inquirymould", "b2b_im_id", "in_id = " + mould.getIn_id(),
						Long.class);
				if (null == b2b_im_id) {
					mould.setSave(true);
				} else { // b2b_im_id不为空的为修改报价
					mould.setSave(false);
				}
				for (InquiryMouldDet det : mould.getItems()) {
					if (det.getIdd_price() != null && mould.getIn_adoptstatus() == null && det.getIdd_price() != 0) {
						sqls.add("update InquiryMould set in_checksendstatus = '待上传', b2b_im_id = "
								+ mould.getB2b_im_id() + " where in_id = " + mould.getIn_id()
								+ " and nvl(in_status,' ')<>'已作废'");
						sqls.add("update InquiryMouldDet set idd_price = " + det.getIdd_price()
								+ " where nvl((select in_status from InquiryMould where InquiryMould.in_id = InquiryMouldDet.idd_inid),' ')<>'已作废' and idd_id = "
								+ det.getIdd_id());
						if (!CollectionUtil.isEmpty(det.getDetails())) {
							for (InquiryMouldDetail detail : det.getDetails()) {
								sqls.add("update InquiryMouldDetail set ind_price = " + detail.getInd_price()
										+ " where nvl((select in_status from InquiryMould where InquiryMould.in_id = InquiryMouldDetail.ind_inid ),' ')<>'已作废' and ind_id = "
										+ detail.getInd_id());
							}
						}
					}
				}

			}
			baseDao.execute(sqls);
			for (InquiryMould mould : moulds) {
				for (InquiryMouldDet det : mould.getItems()) {
					if (det.getIdd_price() != null && mould.getIn_adoptstatus() == null && det.getIdd_price() != 0) {
						inquiryMouldService.returnPriceMouldDet(Integer.parseInt(mould.getIn_id().toString()),
								Integer.parseInt(det.getIdd_id().toString()));
					}
					if (!CollectionUtil.isEmpty(det.getDetails())) {
						for (InquiryMouldDetail detail : det.getDetails()) {
							inquiryMouldService.returnPriceMould(Integer.parseInt(mould.getIn_id().toString()),
									Integer.parseInt(det.getIdd_id().toString()),
									Integer.parseInt(detail.getInd_id().toString()));
						}
					}
				}
			}
			// for (InquiryMould mould : moulds) {
			// List<InquiryMouldDet> mouldDets = mould.getItems();
			// for (InquiryMouldDet mouldDet : mouldDets) {
			// List<InquiryMouldDetail> mouldDetails = mouldDet.getDetails();
			// inquiryMouldService.returnPriceMouldDet(Integer.parseInt(mould.getIn_id().toString()),
			// Integer.parseInt(mouldDet.getIdd_id().toString()));
			// for (InquiryMouldDetail mouldDetail : mouldDetails) {
			// inquiryMouldService.returnPriceMould(Integer.parseInt(mould.getIn_id().toString()),
			// Integer.parseInt(mouldDet.getIdd_id().toString()),
			// Integer.parseInt(mouldDetail.getInd_id().toString()));
			// }
			// }
			// }
			onReplySuccess(moulds, master);
		}
		return moulds;
	}

	/**
	 * 修改平台里面的报价信息的传输状态
	 * 
	 * @return
	 */
	private boolean onReplySuccess(List<InquiryMould> moulds, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryMould mould : moulds) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(mould.getB2b_im_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/inquiryMould/reply/back?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 作废模具询价单信息传到平台
	 * 
	 * 
	 */
	private boolean invalidateInquiryMould(Master master) {
		List<InquiryMould> moulds = getInvalidateInquiryMould();
		if (!CollectionUtil.isEmpty(moulds)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(moulds));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite()
						+ "/erp/purchase/inquiryMould/invalidate?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInvalidate(moulds);
				}
				baseDao.save(new TaskLog("(买家)模具询价单-作废询价单", moulds.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	/**
	 * 获取作废的模具询价单
	 * 
	 * @return
	 */
	private List<InquiryMould> getInvalidateInquiryMould() {
		try {
			return baseDao.getJdbcTemplate().query(
					"select InquiryMould.* from InquiryMould left join vendor on in_vendcode=ve_code where IN_SENDSTATUS='已上传' and nvl(in_sourcetype,' ')='模具报价单' and nvl(in_checksendstatus,' ')<>'作废已上传' and in_status = '已作废' and ve_uu is not null order by in_code,in_id",
					new BeanPropertyRowMapper<InquiryMould>(InquiryMould.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 已作废信息传到平台之后
	 * 
	 * @param moulds
	 */
	private void onInvalidate(List<InquiryMould> moulds) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryMould mould : moulds) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(mould.getIn_id());
		}
		baseDao.execute("update InquiryMould set in_checksendstatus='作废已上传' where in_id in (" + idStr.toString() + ")");

	}

	/**
	 * 上传买家对报价信息是否采纳的决策到平台
	 * 
	 * @return
	 */
	private boolean uploadInquiryMouldDecide(Master master) {
		List<InquiryMouldDecide> replies = getInquiryMouldDecide();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite()
						+ "/erp/purchase/inquiryMould/reply/decide?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(买家)模具询价单-上传是否采纳的决策", replies.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的买家采纳决策
	 * 
	 * @return
	 */
	private List<InquiryMouldDecide> getInquiryMouldDecide() {
		try {
			return baseDao.getJdbcTemplate().query(
					"select in_id,in_code,in_adoptstatus,b2b_im_id,in_checksendstatus from InquiryMould left join vendor on in_vendcode=ve_code where IN_SENDSTATUS='已上传' and nvl(in_sourcetype,' ')='模具报价单' and IN_CHECKSENDSTATUS='待上传' and (in_adoptstatus is not null and nvl (in_adoptstatus,'')<>'待审核') and ve_uu is not null and nvl(ve_b2benable,0)=1 order by in_code,in_id",
					new BeanPropertyRowMapper<InquiryMouldDecide>(InquiryMouldDecide.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 买家是否采纳的决策成功传到平台之后
	 */
	private void onReplySuccess(List<InquiryMouldDecide> decides) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryMouldDecide decide : decides) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(decide.getIn_id());
		}
		baseDao.execute("update InquiryMould set in_checksendstatus='已下载' where in_id in (" + idStr.toString() + ")");
	}
}
