package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.ProductSample;
import com.uas.b2b.model.ProductSampleApproval;
import com.uas.b2b.model.ProductSampleDetail;
import com.uas.b2b.model.RemoteFile;
import com.uas.b2b.model.SaleSampleSend;
import com.uas.b2b.model.TaskLog;
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
 * 作为买家ERP，将打样申请单传入平台、获取平台上的供应商的送样单
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseSampleTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProduct start");
		if (uploadProduct(master)) {
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadProductSample start");
			uploadProductSample(master);
			logger.info(this.getClass() + " uploadProductSample end");
		}
		logger.info(this.getClass() + " downloadSaleSampleSend start");
		downloadSaleSampleSend(master);
		logger.info(this.getClass() + " downloadSaleSampleSend end");
		logger.info(this.getClass() + " uploadSampleApproval start");
		uploadSampleApproval(master);
		logger.info(this.getClass() + " uploadSampleApproval end");
		logger.info(this.getClass() + " invalidateSaleSampleSend start");
		// erp作废打样申请单之后，上传至平台
		invalidateSaleSampleSend(master);
		logger.info(this.getClass() + " invalidateSaleSampleSend end");
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
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/product?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductUploadSuccess(idStr);
					return uploadProduct(master);// 递归传所有物料
				} else
					onProductUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)打样-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				onProductUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 更新已上传的物料资料
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
					"select * from (select distinct product.* from product where exists "
							+ " (select 1 from productsample left join productsampledetail on"
							+ " productsample.ps_id=productsampledetail.pd_psid left join vendor on "
							+ " productsampledetail.pd_vendcode=vendor.ve_code where productsample.ps_sendstatus='待上传'"
							+ " and ve_uu is not null and nvl(ve_b2benable,0)=1 and productsample.ps_prodcode=pr_code)"
							+ " and nvl(pr_sendstatus,' ')<>'已上传') where rownum <= 500 ", new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 上传打样申请单
	 * 
	 * @return
	 */
	private boolean uploadProductSample(Master master) {
		List<ProductSample> productSamples = getProductSampleUpload();
		if (!CollectionUtil.isEmpty(productSamples)) {
			String idStr = CollectionUtil.getKeyString(productSamples, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(productSamples));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeProductSampleUpload(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/sample?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductSampleUploadSuccess(idStr);
				} else
					onProductSampleUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)打样-上传打样申请单", productSamples.size(), response));
			} catch (Exception e) {
				onProductSampleUploadFail(idStr);
				return false;
			} finally {
				checkProductSampleUpload(idStr);
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的打样申请单
	 * 
	 * @return
	 */
	public List<ProductSample> getProductSampleUpload() {
		try {
			List<ProductSample> productSamples = baseDao
					.getJdbcTemplate()
					.query("select productsample.*,em_uu ps_appmanuu from productsample left join employee"
							+ " on productsample.ps_appmanid = em_id where (ps_sendstatus='待上传' or"
							+ " ps_sendstatus='上传中') and ps_status='已审核' and exists (select 1 from productsampledetail left"
							+ " join vendor on pd_vendcode=ve_code where pd_psid=ps_id and ve_uu is not null and nvl(ve_b2benable,0)=1) order by ps_code",
							new BeanPropertyRowMapper<ProductSample>(ProductSample.class));
			for (ProductSample productSample : productSamples) {
				List<ProductSampleDetail> details = baseDao
						.getJdbcTemplate()
						.query("select productsampledetail.*,ve_contactuu pd_contactuu from productsampledetail"
								+ " left join vendor on pd_vendcode=ve_code where pd_psid=? and ve_uu is not null and nvl(ve_b2benable,0)=1",
								new BeanPropertyRowMapper<ProductSampleDetail>(ProductSampleDetail.class), productSample.getPs_id());
				productSample.setDetails(details);

				// 获取询价单的附件信息
				if (StringUtil.hasText(productSample.getPs_attach())) {
					String[] fileIds = productSample.getPs_attach().split(";");
					String erpUrl = getEnterpriseErpUrl();
					List<Attach> attaches = baseDao.getJdbcTemplate().query(
							"select fp_id, fp_size, fp_name from filepath where fp_id in ("
									+ StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
							new BeanPropertyRowMapper<Attach>(Attach.class));
					if (!CollectionUtil.isEmpty(attaches)) {
						for (Attach attach : attaches) {
							attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
						}
						productSample.setAttaches(attaches);
					}
				}
			}
			return productSamples;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新待上传的打样申请单状态
	 * 
	 * @param idStr
	 *            待上传的打样申请单
	 */
	public void beforeProductSampleUpload(String idStr) {
		baseDao.execute("update productsample set ps_sendstatus='上传中' where ps_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的打样申请单状态
	 * 
	 * @param idStr
	 *            上传成功的打样申请单
	 */
	public void onProductSampleUploadSuccess(String idStr) {
		baseDao.execute("update productsample set ps_sendstatus='已上传' where ps_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的打样申请单状态
	 * 
	 * @param idStr
	 *            上传失败的打样申请单
	 */
	public void onProductSampleUploadFail(String idStr) {
		baseDao.execute("update productsample set ps_sendstatus='待上传' where ps_id in (" + idStr + ") and ps_sendstatus='上传中'");
	}

	/**
	 * 防止意外情况，状态一直处于上传中
	 * 
	 * @param idStr
	 */
	public void checkProductSampleUpload(String idStr) {
		baseDao.execute("update productsample set ps_sendstatus='待上传' where ps_id in (" + idStr + ") and ps_sendstatus='上传中'");
	}

	/**
	 * 从平台下载在平台上送样的送样单
	 * 
	 * @return
	 */
	private boolean downloadSaleSampleSend(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/sample/sampleSend?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleSampleSend> sampleSends = FlexJsonUtil.fromJsonArray(data, SaleSampleSend.class);
					if (!CollectionUtil.isEmpty(sampleSends)) {
						saveSaleSampleSend(sampleSends, master);
						baseDao.save(new TaskLog("(买家)打样-下载送样单", sampleSends.size(), response));
						saveB2BMessage(sampleSends);
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)打样-下载送样单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存成功，产生消息
	 * 
	 * @param replies
	 */
	private void saveB2BMessage(List<SaleSampleSend> sampleSends) {
		StringBuffer idStringBuffer = new StringBuffer();
		for (SaleSampleSend send : sampleSends) {
			if (idStringBuffer.length() > 0) {
				idStringBuffer.append(",");
			}
			idStringBuffer.append(send.getSs_id());
		}
		if (idStringBuffer.length() > 0) {
			// 产生消息
			pagingReleaseService.B2BMsg("SendSample", String.valueOf(idStringBuffer), "confirm");
		}
	}

	/**
	 * 保存平台上传过来的送样单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleSampleSend(List<SaleSampleSend> sampleSends, Master master) {
		if (!CollectionUtil.isEmpty(sampleSends)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();
			List<SaleSampleSend> validSampleSend = new ArrayList<SaleSampleSend>();// 有效的,需要保存的
			for (SaleSampleSend sampleSend : sampleSends) {
				int count = baseDao.getCount("select count(1) from SendSample where b2b_ss_id =" + sampleSend.getB2b_ss_id());
				if (count == 0) {
					int ssId = baseDao.getSeqId("sendsample_seq");
					String ssCode = baseDao.sGetMaxNumber("SendSample", 2);// 供应商送样单系统编号
					sqls.add(sampleSend.toPurcSqlString(ssId, ssCode));
					if (idStr.length() > 0) {
						idStr.append(",");
					}
					idStr.append(ssId);
					validSampleSend.add(sampleSend);
				}
				if (b2bStr.length() > 0) {
					b2bStr.append(",");
				}
				b2bStr.append(sampleSend.getB2b_ss_id());
			}
			if (idStr.length() > 0) {
				// 完善记录数据
				sqls.add("update SendSample set (ss_pscode, ss_prodcode, ss_prodname, ss_prodspec, ss_unit, ss_isfree, ss_allmoney, ss_vendcode, ss_vendname, ss_venduu, ss_contact, ss_contactuu, ss_sampleprice, ss_currency, ss_rate) = "
						+ "(select ps_code, ps_prodcode, ps_prodname, ps_prodspec, ps_unit, ps_isfree, pd_totalmon, "
						+ "pd_vendcode, pd_vendname, ve_uu, ve_contact, ve_contactuu, pd_price, pd_currency, pd_tax from "
						+ "productSampledetail left join productsample on productsampledetail.pd_psid = productsample.ps_id left join "
						+ "vendor on productsampledetail.pd_vendcode = vendor.ve_code where productSample.ps_code = SendSample.ss_pscode and productSampleDetail.pd_detno = SendSample.ss_pddetno) where ss_id in ( "
						+ idStr.toString() + ") ");
				baseDao.execute(sqls);
				// 更新供应商信息
				baseDao.execute("update sendsample set (ss_providecode,ss_provide,ss_otherenid)=(select ve_code,ve_name,ve_uu from vendor where ve_uu=to_char(nvl(ss_venduu,0))) where ss_id in ("
						+ idStr.toString() + ") ");
				// 推送
				for (SaleSampleSend send : validSampleSend) {
					if (send.getSs_useruu() != null) {
						Employee employee = employeeService.getEmployeeByUu(send.getSs_useruu());
						if (employee != null && employee.getEm_code() != null) {
							// String userCode = employee.getEm_code();
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
							// userCode, "收到供应商送样单", "送样单号:" +
							// send.getSs_code(),
							// null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(),
							// userCode, "收到供应商送样单  " + "送样单号:" +
							// send.getSs_code());
						}
					}
				}
				try {
					saveSendAttach(validSampleSend);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (b2bStr.length() > 0) {
				onSaleSampleSendDownSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到送样单之后，修改平台的送样单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleSampleSendDownSuccess(String idStr, Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/sample/sampleSend/back?access_id="
					+ master.getMa_uu(), params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 保存来自平台的送样单附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveSendAttach(List<SaleSampleSend> sampleSends) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleSend sampleSend : sampleSends) {
			if (StringUtil.hasText(sampleSend.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : sampleSend.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update SendSample set ss_attach='" + attachIds.toString() + "' where b2b_ss_id=" + sampleSend.getB2b_ss_id());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 将(买方)ERP的认定单上传到平台
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadSampleApproval(Master master) {
		List<ProductSampleApproval> sampleApprovals = getUploadSampleApprovals();
		if (!CollectionUtil.isEmpty(sampleApprovals)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(sampleApprovals));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/sample/approval?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedSampleApprovals(sampleApprovals);
				}
				baseDao.save(new TaskLog("(买家)打样-上传认定单", sampleApprovals.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private List<ProductSampleApproval> getUploadSampleApprovals() {
		try {
			List<ProductSampleApproval> saleSampleSends = baseDao
					.getJdbcTemplate()
					.query("select * from (select productapproval.*, "
							+ "to_number(ve_uu) pa_venduu, pr_detail, pr_spec, pr_unit from productapproval left join vendor on "
							+ "productapproval.pa_providecode = vendor.ve_code left join product on productapproval.pa_prodcode = "
							+ "product.pr_code where pa_status='已审核' and pa_sendstatus='待上传'  and ve_uu is not null and nvl(ve_b2benable,0)=1 and rownum<10)",
							new BeanPropertyRowMapper<ProductSampleApproval>(ProductSampleApproval.class));
			if (!CollectionUtil.isEmpty(saleSampleSends)) {
				for (ProductSampleApproval approval : saleSampleSends) {
					// 获取UAS外网url
					String erpUrl = getEnterpriseErpUrl();
					// 获取附件信息
					List<Attach> attaches = getAttachesByIdString(approval.getPa_attach(), erpUrl);
					approval.setAttaches(attaches);

					// 获取prd附件信息
					List<Attach> prdAttaches = getAttachesByIdString(approval.getPa_prdattach(), erpUrl);
					approval.setPrdAttaches(prdAttaches);

					// 获取pad附件信息
					List<Attach> padAttaches = getAttachesByIdString(approval.getPa_padattach(), erpUrl);
					approval.setPadAttaches(padAttaches);

					// 获取ppd附件信息
					List<Attach> ppdAttaches = getAttachesByIdString(approval.getPa_padattach(), erpUrl);
					approval.setPpdAttaches(ppdAttaches);
				}
			}
			return saleSampleSends;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateUploadedSampleApprovals(List<ProductSampleApproval> sampleApprovals) {
		for (ProductSampleApproval sampleApproval : sampleApprovals) {
			baseDao.execute("update productapproval set pa_sendstatus = '已上传' where pa_id = " + sampleApproval.getPa_id());
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
	 * 根据附件ID字符串、企业UAS外网地址获取附件列表
	 * 
	 * @param idStr
	 * @param erpUrl
	 * @return
	 */
	private List<Attach> getAttachesByIdString(String idStr, String erpUrl) {
		if (StringUtils.isEmpty(idStr))
			return null;
		String[] fileIds = idStr.split(";");
		List<Attach> attaches = baseDao.getJdbcTemplate().query(
				"select fp_id, fp_size, fp_name from filepath where fp_id in (" + StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
				new BeanPropertyRowMapper<Attach>(Attach.class));
		if (!CollectionUtil.isEmpty(attaches)) {
			for (Attach attach : attaches) {
				attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
			}
			return attaches;
		} else {
			return null;
		}
	}

	/**
	 * 作废打样申请单信息传到平台
	 */
	private boolean invalidateSaleSampleSend(Master master) {
		List<Long> sampleIds = getInvalidateSaleSampleSend();
		if (!CollectionUtil.isEmpty(sampleIds)) {
			StringBuffer ids = new StringBuffer();
			for (Long sampleId : sampleIds) {
				if (ids.length() > 0) {
					ids.append(",");
				}
				ids.append(sampleId);
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", ids.toString());
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/sample/invalidate?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInvalidate(ids);
				}
				baseDao.save(new TaskLog("(买家)模具询价单-作废询价单", sampleIds.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	/**
	 * 获取作废的打样申请单的id List
	 * 
	 * @return
	 */
	private List<Long> getInvalidateSaleSampleSend() {
		try {
			return baseDao.getJdbcTemplate()
					.queryForList("select productsample.ps_id from productsample left join employee"
							+ " on productsample.ps_appmanid = em_id where ps_sendstatus = '已上传' and ps_status='已作废' and exists (select 1 from productsampledetail left"
							+ " join vendor on pd_vendcode=ve_code where pd_psid=ps_id and ve_uu is not null and nvl(ve_b2benable,0)=1) order by ps_code",
							Long.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 已作废信息传到平台之后
	 * 
	 * @param sampleIds
	 */
	private void onInvalidate(StringBuffer sampleIds) {
		baseDao.execute("update ProductSample set ps_sendstatus='作废已上传' where ps_id in (" + sampleIds.toString() + ")");
	}

}
