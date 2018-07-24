package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.uas.api.crypto.util.FlexJsonUtils;
import com.uas.b2b.model.Attach;
import com.uas.b2b.model.Inquiry;
import com.uas.b2b.model.InquiryDecide;
import com.uas.b2b.model.InquiryDetail;
import com.uas.b2b.model.InquiryDetailDet;
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.RemoteFile;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SplitArray;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为买家ERP，将询价单传入平台、获取平台上的供应商的报价
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class InquiryTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	/**
	 * 最大值1000，数据库能更新的最大值为1000
	 */
	private final int max_size = 1000;

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
				/*
				 * logger.info(this.getClass() + " uploadInquiry start");
				 * uploadInquiry(master); logger.info(this.getClass() +
				 * " uploadInquiry end");
				 */
				// 新的方法传输数据
				logger.info(this.getClass() + " uploadInquiryV2 start");
				uploadInquiryV2(master);
				logger.info(this.getClass() + " uploadInquiryV2 end");

				logger.info(this.getClass() + " downloadReply start");
				// 下载报价回复
				downloadReply(master);
				logger.info(this.getClass() + " downloadReply end");
				logger.info(this.getClass() + " uploadCheckInquiry start");
				// 询价单提交后，上传平台
				uploadCheckInquiry(master);
				logger.info(this.getClass() + " uploadCheckInquiry end");
				logger.info(this.getClass() + " uploadReCheckInquiry start");

				// 询价单反提交后，上传平台
				uploadReCheckInquiry(master);
				logger.info(this.getClass() + " uploadReCheckInquiry end");
				logger.info(this.getClass() + " uploadInquiryDecide start");
				// 上传采纳结果
				uploadInquiryDecide(master);
				logger.info(this.getClass() + " uploadInquiryDecide end");
				logger.info(this.getClass() + " uploadInquiryInvalid start");
				// 上传作废
				uploadInquiryInvalid(master);
				logger.info(this.getClass() + " uploadInquiryInvalid end");
				logger.info(this.getClass() + " downloadInquiries start");
				// 下载主动报价
				downloadInquiries(master);
				logger.info(this.getClass() + " downloadInquiries end");
				logger.info(this.getClass() + " uploadQuotationInvalid start");
				// 上传主动报价询价单作废
				uploadQuotationInvalid(master);
				logger.info(this.getClass() + " uploadQuotationInvalid end");
				logger.info(this.getClass() + " uploadQuotationDecide start");
				// 主动报价的采纳结果
				uploadQuotationDecide(master);
				logger.info(this.getClass() + " uploadQuotationDecide end");
				logger.info(this.getClass() + " uploadCheckQuotation start");
				// 主动报价提交状态上传到平台
				uploadCheckQuotation(master);
				logger.info(this.getClass() + " uploadCheckQuotation end");
				logger.info(this.getClass() + " uploadReCheckQuotation start");
				// 主动报价反提交状态上传到平台
				uploadReCheckQuotation(master);
				logger.info(this.getClass() + " uploadReCheckQuotation end");
				logger.info(this.getClass() + " uplodQutoInquiry start");
				// 自动询价，明细上传（供应商未注册平台后来又注册的，重新将单据上传到商务平台）
				uplodQutoInquiry(master);
				logger.info(this.getClass() + " uplodQutoInquiry end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增方法传输数据，以前的方法存在传输数据问题
	 * 
	 * @param master
	 */
	private boolean uploadInquiryV2(Master master) {
		ModelMap map = getInquiryUpload();
		List<Inquiry> inquiries = new ArrayList<Inquiry>();
		String ids = null;
		Object object = map.get("inquires");
		Object object2 = map.get("idStr");
		if (null != object) {
			inquiries = FlexJsonUtil.fromJsonArray(object.toString(), Inquiry.class);
		}
		if (null != object2) {
			ids = object2.toString();
		}
		if (!CollectionUtil.isEmpty(inquiries)) {
			/* System.out.println(JSON.toJSON(inquiries)); */
			String idStr = CollectionUtil.getKeyString(inquiries, ",");
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeInquiryUpload(idStr);
				Response response = HttpUtil.doPost(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/v2?access_id=" + master.getMa_uu(),
						JSON.toJSONString(inquiries), true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInquiryUploadSuccess(idStr);
					onInquiryDetailUploadSuccess(ids);
				} else
					onInquiryUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)询价单-上传询价单(v2)", inquiries.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onInquiryUploadFail(idStr);
				return false;
			} finally {
				checkInquiryUpload(idStr);
			}
		}
		return true;
	}

	private void uplodQutoInquiry(Master master) {
		List<InquiryDetail> details = getQutoInquiryDetail();
		if (!CollectionUtils.isEmpty(details)) {
			String idStr = CollectionUtil.getKeyString(details, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtils.toJsonDeep(details));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/qutoInquiry?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInquiryDetailUploadSuccess(idStr);
				}
				baseDao.save(new TaskLog("(买家)询价单-上传自动询价明细（供应商注册平台后自动上传）", idStr.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取自动询价未上传的明细数据
	 * 
	 * @return
	 */
	private List<InquiryDetail> getQutoInquiryDetail() {
		try {
			String sql = "select InquiryDetail.*,ve_uu,in_code,nvl(id_vendcontact,ve_contact) ve_contact,nvl(id_vendcontactuu,ve_contactuu) ve_contactuu,em_uu ve_buyeruu from InquiryDetail left join vendor on id_vendcode=ve_code left join employee on ve_buyercode=em_code left join Inquiry on id_inid = in_id where nvl(in_sendstatus, ' ') = '已上传' and nvl(id_backstatus, ' ')='待上传' and ve_uu is not null and nvl(ve_b2benable,0)=1 and nvl(in_auto,0)=1 and rownum < = 200";
			List<InquiryDetail> details = baseDao.getJdbcTemplate().query(sql,
					new BeanPropertyRowMapper<InquiryDetail>(InquiryDetail.class));
			for (InquiryDetail detail : details) {
				try {
					// 获取询价单明细的价格分段
					List<InquiryDetailDet> dets = baseDao.getJdbcTemplate().query("select * from InquiryDetailDet where idd_idid=?",
							new BeanPropertyRowMapper<InquiryDetailDet>(InquiryDetailDet.class), detail.getId_id());
					if (dets.size() == 0)
						throw new EmptyResultDataAccessException(1);
					detail.setDets(dets);
				} catch (EmptyResultDataAccessException e) {
					// 如果不存在价格分段，插入一条以0为最小值的分段
					baseDao.execute("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty) values (InquiryDetailDet_seq.nextval,"
							+ detail.getId_id() + ",0)");
					List<InquiryDetailDet> dets = baseDao.getJdbcTemplate().query("select * from InquiryDetailDet where idd_idid=?",
							new BeanPropertyRowMapper<InquiryDetailDet>(InquiryDetailDet.class), detail.getId_id());
					detail.setDets(dets);
				}
			}
			return details;
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * 主动报价反提交状态上传到平台
	 * 
	 * @param master
	 */
	private void uploadReCheckQuotation(Master master) {
		String inCheck = getReCheckQuotation();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/quotation/rechecking?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onReCheckQuotationUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)询价单-上传主动报价单反提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上传成功，更新上传状态
	 * 
	 * @param inCheck
	 */
	private void onReCheckQuotationUploadSuccess(String quIds) {
		baseDao.execute("update inquiry set in_checksendstatus='待上传' where b2b_qu_id in (" + quIds + ")");
	}

	/**
	 * 获取反提交的主动报价单
	 * 
	 * @return
	 */
	private String getReCheckQuotation() {
		return baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(b2b_qu_id) from (select b2b_qu_id from inquiry where in_checkstatus = '在录入' and in_checksendstatus='已上传' and nvl(in_class,' ')='主动报价' and in_sendstatus='已上传' order by b2b_qu_id) where rownum < 100",
						String.class);
	}

	/**
	 * 主动报价的提交状态上传到平台
	 * 
	 * @param master
	 */
	private void uploadCheckQuotation(Master master) {
		String inCheck = getCheckQuotation();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/quotation/checking?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onCheckQuotationUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)询价单-上传询主动报价单提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 已提交状态上传成，更新上传状态
	 * 
	 * @param inCheck
	 */
	private void onCheckQuotationUploadSuccess(String quIds) {
		baseDao.execute("update inquiry set in_checksendstatus='已上传' where b2b_qu_id in (" + quIds + ")");
	}

	/**
	 * 获取已提交的主动报价单
	 * 
	 * @return
	 */
	private String getCheckQuotation() {
		return baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(b2b_qu_id) from (select b2b_qu_id from inquiry where in_checkstatus = '已提交' and in_checksendstatus='待上传' and nvl(in_class,' ')='主动报价' and in_sendstatus='已上传' order by b2b_qu_id) where rownum < 100",
						String.class);
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
				baseDao.save(new TaskLog("(买家)询价单-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
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
	 * 上传物料;这里是查询所有物料信息（已审核）；更新平台物料库的状态
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where exists (select 1 from inquirydetail left join vendor on id_vendcode=ve_code left join inquiry on in_id=id_inid where (IN_SENDSTATUS='待上传' or IN_SENDSTATUS='上传中') and nvl(in_class,' ')<>'主动报价' and ve_uu is not null and nvl(ve_b2benable,0)=1 and id_prodcode=pr_code) and nvl(pr_sendstatus,' ')<>'已上传') where rownum <= 500",
							new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 上传询价单
	 * 
	 * @return
	 */
	private boolean uploadInquiry(Master master) {
		ModelMap map = getInquiryUpload();
		String ids = null;
		Object inquires = map.get("inquires");
		Object detailIdStr = map.get("idStr");
		Object inquiryStr = map.get("inquiryStr");
		Object size = map.get("size");
		if (null != detailIdStr) {
			ids = detailIdStr.toString();
		}
		if (null != inquires && !StringUtils.isEmpty(inquiryStr)) {
			String idStr = inquiryStr.toString();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inquires.toString());
			System.out.println("data" + inquires.toString());
			/*
			 * try { // 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
			 * beforeInquiryUpload(idStr); Response response =
			 * HttpUtil.sendPostRequest( master.getMa_b2bwebsite() +
			 * "/erp/purchase/inquiry?access_id=" + master.getMa_uu(), params,
			 * true, master.getMa_accesssecret()); if (response.getStatusCode()
			 * == HttpStatus.OK.value()) { onInquiryUploadSuccess(idStr);
			 * onInquiryDetailUploadSuccess(ids); } else
			 * onInquiryUploadFail(idStr); baseDao.save(new
			 * TaskLog("(买家)询价单-上传询价单", Integer.valueOf(size.toString()),
			 * response)); } catch (Exception e) { e.printStackTrace();
			 * onInquiryUploadFail(idStr); return false; } finally {
			 * checkInquiryUpload(idStr); }
			 */
		}
		return true;
	}

	/**
	 * 询价单提交后，上传平台
	 * 
	 * @param enterprise
	 * @param master
	 */
	private void uploadCheckInquiry(Master master) {
		String inCheck = getCheckInquiry();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/checking?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onCheckInquiryUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)询价单-上传询价单提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getCheckInquiry() {
		return baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(in_id) from (select in_id from inquiry where in_checkstatus = '已提交' and in_checksendstatus='待上传' and nvl(in_class,' ')<>'主动报价' and in_sendstatus='已上传' order by in_id) where rownum < 100",
						String.class);
	}

	private void onCheckInquiryUploadSuccess(String inIds) {
		baseDao.execute("update inquiry set in_checksendstatus='已上传' where in_id in (" + inIds + ")");
	}

	/**
	 * 询价单反提交后，上传平台
	 * 
	 * @param enterprise
	 * @param master
	 */
	private void uploadReCheckInquiry(Master master) {
		String inCheck = getReCheckInquiry();
		if (inCheck != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", inCheck);
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/reCheck?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onReCheckInquiryUploadSuccess(inCheck);
				}
				baseDao.save(new TaskLog("(买家)询价单-上传询价单反提交状态", inCheck.split(",").length, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getReCheckInquiry() {
		return baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(in_id) from (select in_id from inquiry where in_checkstatus = '在录入' and in_checksendstatus='已上传' and nvl(in_class,' ')<>'主动报价' and in_sendstatus='已上传' order by in_id) where rownum < 100",
						String.class);
	}

	private void onReCheckInquiryUploadSuccess(String inIds) {
		baseDao.execute("update inquiry set in_checksendstatus= null where in_id in (" + inIds + ")");
	}

	/**
	 * 获取需要上传的询价单
	 * 
	 * @return
	 */
	public ModelMap getInquiryUpload() {
		ModelMap map = new ModelMap();
		try {
			List<Inquiry> inquiries = baseDao
					.getJdbcTemplate()
					.query("select Inquiry.*,em_uu in_recorderuu from Inquiry left join employee on in_recorderid = em_id where in_statuscode='AUDITED' and (IN_SENDSTATUS='待上传' or IN_SENDSTATUS='上传中') and nvl(in_class,' ')<>'主动报价' and exists (select 1 from InquiryDetail left join vendor on id_vendcode=ve_code where id_inid=in_id and ve_uu is not null and nvl(ve_b2benable,0)=1) and rownum <=100 order by in_code",
							new BeanPropertyRowMapper<Inquiry>(Inquiry.class));
			String idStr = "";
			for (Inquiry inquiry : inquiries) {
				// 获取询价单明细
				List<InquiryDetail> details = baseDao
						.getJdbcTemplate()
						.query("select InquiryDetail.*,ve_uu,nvl(id_vendcontact,ve_contact) ve_contact,nvl(id_vendcontactuu,ve_contactuu) ve_contactuu,em_uu ve_buyeruu from InquiryDetail left join vendor on id_vendcode=ve_code left join employee on ve_buyercode=em_code where id_inid=? and ve_uu is not null and nvl(ve_b2benable,0)=1",
								new BeanPropertyRowMapper<InquiryDetail>(InquiryDetail.class), inquiry.getIn_id());
				idStr = idStr + CollectionUtil.getKeyString(details, ",");
				for (InquiryDetail detail : details) {
					try {
						// 获取询价单明细的价格分段
						List<InquiryDetailDet> dets = baseDao.getJdbcTemplate().query("select * from InquiryDetailDet where idd_idid=?",
								new BeanPropertyRowMapper<InquiryDetailDet>(InquiryDetailDet.class), detail.getId_id());
						if (dets.size() == 0)
							throw new EmptyResultDataAccessException(1);
						detail.setDets(dets);
					} catch (EmptyResultDataAccessException e) {
						// 如果不存在价格分段，插入一条以0为最小值的分段
						baseDao.execute("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty) values (InquiryDetailDet_seq.nextval,"
								+ detail.getId_id() + ",0)");
						List<InquiryDetailDet> dets = baseDao.getJdbcTemplate().query("select * from InquiryDetailDet where idd_idid=?",
								new BeanPropertyRowMapper<InquiryDetailDet>(InquiryDetailDet.class), detail.getId_id());
						detail.setDets(dets);
					}
				}
				inquiry.setDetails(details);

				// 获取询价单的附件信息
				if (StringUtil.hasText(inquiry.getIn_attach())) {
					String[] fileIds = inquiry.getIn_attach().split(";");
					String erpUrl = getEnterpriseErpUrl();
					List<Attach> attaches = baseDao.getJdbcTemplate().query(
							"select fp_id, fp_size, fp_name from filepath where fp_id in ("
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
			String inquiryStr = CollectionUtil.getKeyString(inquiries, ",");
			map.put("inquiryStr", inquiryStr);
			map.put("inquires", FlexJsonUtils.toJsonDeep(inquiries));
			map.put("idStr", idStr);
			map.put("size", inquiries.size());
			return map;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新待上传的询价单状态
	 * 
	 * @param idStr
	 *            待上传的询价单
	 */
	public void beforeInquiryUpload(String idStr) {
		baseDao.execute("update inquiry set IN_SENDSTATUS='上传中' where in_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的询价单状态
	 * 
	 * @param idStr
	 *            上传成功的询价单
	 */
	public void onInquiryUploadSuccess(String idStr) {
		baseDao.execute("update inquiry set IN_SENDSTATUS='已上传' where in_id in (" + idStr + ")");
	}

	/**
	 * 询价单上传成功，更新明细上传状态
	 * 
	 * @param ids
	 */
	private void onInquiryDetailUploadSuccess(String ids) {
		// 可能存在id数量超长的情况，所以进行拆分处理
		List<String> idList = Arrays.asList(ids.split(","));
		if (!CollectionUtils.isEmpty(idList))
			updateStatus(idList);
	}

	/**
	 * 更新明细上传状态
	 * 
	 * @param idList
	 */
	private void updateStatus(List<String> idList) {
		if (idList.size() > max_size) {// 数组太大时进行拆分
			List<List<String>> ids = SplitArray.splitAry(idList, max_size);
			for (List<String> idStr : ids) {
				MapSqlParameterSource parameters = new MapSqlParameterSource("ids", idStr);
				// 不要直接拼SQL，防参数过多出错
				NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(baseDao.getJdbcTemplate());
				namedParameterJdbcTemplate.update("update inquirydetail set id_backstatus='已上传' where id_id in (:ids)", parameters);
			}
		} else {
			MapSqlParameterSource parameters = new MapSqlParameterSource("ids", idList);
			// 不要直接拼SQL，防参数过多出错
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(baseDao.getJdbcTemplate());
			namedParameterJdbcTemplate.update("update inquirydetail set id_backstatus='已上传' where id_id in (:ids)", parameters);
		}
	}

	/**
	 * 更新上传失败的询价单状态
	 * 
	 * @param idStr
	 *            上传失败的询价单
	 */
	public void onInquiryUploadFail(String idStr) {
		baseDao.execute("update inquiry set IN_SENDSTATUS='待上传' where in_id in (" + idStr + ") and IN_SENDSTATUS='上传中'");
	}

	/**
	 * 防止意外情况，状态一直处于上传中
	 * 
	 * @param idStr
	 */
	public void checkInquiryUpload(String idStr) {
		baseDao.execute("update inquiry set IN_SENDSTATUS='待上传' where in_id in (" + idStr + ") and IN_SENDSTATUS='上传中'");
	}

	/**
	 * 从平台下载供应商的报价
	 * 
	 * @return
	 */
	private boolean downloadReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/inquiry/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			List<InquiryDetail> inquiryDetails = new ArrayList<InquiryDetail>();
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<InquiryDetail> details = FlexJsonUtil.fromJsonArray(data, InquiryDetail.class);
					if (!CollectionUtil.isEmpty(details)) {
						details = saveInquiryReply(details, master);
						baseDao.save(new TaskLog("(买家)询价单-下载报价", details.size(), response));
						saveB2BMessage(details);// 单据保存完才产生消息
						inquiryDetails.addAll(details);
					}
				}
			} else {
				baseDao.save(new TaskLog("(买家)询价单-下载报价", 0, response));
			}
			// try {
			saveInquiryDetailAttach(inquiryDetails);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
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
	private void saveB2BMessage(List<InquiryDetail> details) {
		for (InquiryDetail detail : details) {
			// 产生消息
			if (!"已报价".equals(detail.getId_status())) {
				pagingReleaseService.B2BMsg("Inquiry", String.valueOf(detail.getId_id()), "save");// 这里传的明细id
			} else { // 已报价的为修改报价
				pagingReleaseService.B2BMsg("Inquiry", String.valueOf(detail.getId_id()), "update");// 这里传的明细id
			}
		}

	}

	/**
	 * 保存来自供应商的报价信息
	 * 
	 * @param replies
	 */
	public List<InquiryDetail> saveInquiryReply(List<InquiryDetail> details, Master master) {
		if (!CollectionUtil.isEmpty(details)) {
			List<String> sqls = new ArrayList<String>();
			for (InquiryDetail detail : details) {
				if (detail.getIn_inquirytype() != null && detail.getIn_inquirytype().equals("公共询价")) {// 目前只有公共询价才会在明细保存询价单号，其他的不会
					Long id = CheckInquiry(detail);
					detail.setId_id(id);
				}
				// 获取存储之前询价单状态，确认是报价还是修改报价
				String status = baseDao.getFieldValue("inquirydetail", "id_status", "id_id = " + detail.getId_id(), String.class);
				detail.setId_status(status);

				if (detail.getId_rate() == null) {
					sqls.add("update InquiryDetail set id_fromdate="
							+ (detail.getId_fromdate() == null ? "id_myfromdate" : DateUtil.parseDateToOracleString(null,
									detail.getId_fromdate()))
							+ ",id_todate="
							+ (detail.getId_todate() == null ? "id_mytodate"
									: DateUtil.parseDateToOracleString(null, detail.getId_todate())) + ",id_minbuyqty="
							+ detail.getId_minbuyqty() + ",id_minqty=" + detail.getId_minqty() + ",id_brand='" + detail.getId_brand()
							+ "',id_vendorprodcode='" + detail.getId_vendorprodcode() + "'" + ",id_leadtime="
							+ NumberUtil.nvl(detail.getId_leadtime(), 0) + " where id_id=" + detail.getId_id());
				} else {
					sqls.add("update InquiryDetail set id_fromdate="
							+ (detail.getId_fromdate() == null ? "id_myfromdate" : DateUtil.parseDateToOracleString(null,
									detail.getId_fromdate()))
							+ ",id_todate="
							+ (detail.getId_todate() == null ? "id_mytodate"
									: DateUtil.parseDateToOracleString(null, detail.getId_todate())) + ",id_minbuyqty="
							+ detail.getId_minbuyqty() + ",id_minqty=" + detail.getId_minqty() + ",id_brand='" + detail.getId_brand()
							+ "',id_vendorprodcode='" + detail.getId_vendorprodcode() + "'" + ",id_leadtime="
							+ NumberUtil.nvl(detail.getId_leadtime(), 0) + ",id_rate=" + detail.getId_rate() + " where id_id="
							+ detail.getId_id());
				}

				if (!CollectionUtil.isEmpty(detail.getDets())) {
					sqls.add("delete from InquiryDetailDet where idd_idid=" + detail.getId_id());
					for (InquiryDetailDet det : detail.getDets()) {
						sqls.add("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty,idd_price) values (InquiryDetailDet_SEQ.nextval,"
								+ detail.getId_id() + "," + det.getIdd_lapqty() + "," + det.getIdd_price() + ")");
						if (det.getIdd_lapqty() == null || 0 == det.getIdd_lapqty()) {
							sqls.add("update InquiryDetail set id_status='已报价', id_lapqty=0,id_price=" + det.getIdd_price()
									+ " where id_id=" + detail.getId_id());
						}
					}
				}
			}
			baseDao.execute(sqls);
			try {
				for (InquiryDetail detail : details) {
					SqlRowList rs = baseDao
							.queryForRowSet(
									"select em_code,ve_name,in_code from InquiryDetail left join Inquiry on id_inid=in_id left join vendor on ve_code=id_vendcode left join employee on em_id=in_recorderid where id_id=?",
									detail.getId_id());
					if (StringUtil.hasText(rs.getString("em_code"))) {
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
						// rs.getString("em_code"), "询价单收到新的报价",
						// "供应商:" + rs.getString("ve_name") + ",单号:" +
						// rs.getString("in_code"), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(),
						// rs.getString("em_code"),
						// "询价单收到新的报价 " + "供应商:" + rs.getString("ve_name"));
					}
				}
			} catch (Exception e) {

			}
			onReplySuccess(details, master);
		}
		return details;
	}

	/**
	 * 判断单据存在与否，并且根据明细id更新报价信息
	 * 
	 * @param detail
	 * @return
	 */
	private Long CheckInquiry(InquiryDetail detail) {
		Long id = 0L;
		// erp存在公共询价的原因，现在需要判断处理
		Prod prod = baseDao.query("select pr_id from product where pr_code ='" + detail.getId_prodcode() + "'", Prod.class).get(0);
		Inquiry inquiry = checkInquiryisExist(detail.getIn_code());
		if (inquiry == null) {
			// 先进行主表插入数据
			insertInquiry(detail.getIn_code());
			// 得到返回的信息
			inquiry = checkInquiryisExist(detail.getIn_code());
			// 再插入明细
			insertInquiryDetail(detail, prod.getPr_id(), inquiry.getIn_id(), 1);
			// 得到结果
			InquiryDetail inquiryDetail = checkInquiryDetailisExist(detail.getVe_uu(), inquiry.getIn_id(), detail.getId_prodcode());
			id = inquiryDetail.getId_id();
		} else {
			// 判断明细是否存在，这个条单据需要跟供应商进行关联
			InquiryDetail inquiryDetail = checkInquiryDetailisExist(detail.getVe_uu(), inquiry.getIn_id(), detail.getId_prodcode());
			if (inquiryDetail == null) {
				// 获取序列号
				Integer detno = getDetno(inquiry.getIn_id());
				insertInquiryDetail(detail, prod.getPr_id(), inquiry.getIn_id(), detno);
				inquiryDetail = checkInquiryDetailisExist(detail.getVe_uu(), inquiry.getIn_id(), detail.getId_prodcode());
				id = inquiryDetail.getId_id();
			} else {
				id = detail.getId_id();
			}

		}
		return id;
	}

	/**
	 * 获取明细的序列号，erp明细需要序列与主表相对应
	 * 
	 * @param in_id
	 * @return
	 */
	private Integer getDetno(Long in_id) {
		Integer detno = baseDao.query("select max(id_detno) id_detno from inquirydetail where id_inid = " + in_id, InquiryDetail.class)
				.get(0).getId_detno() + 1;
		return detno;
	}

	/**
	 * 插入询价主表
	 * 
	 * @param in_code
	 */
	private void insertInquiry(String in_code) {
		String insertSql = "insert into inquiry(in_id,in_code,in_date,in_recorddate,in_class,in_enddate,in_kind,in_recorder,in_prodtype,in_pricetype,in_attach,in_cop,in_environment,in_purpose,in_source,in_sendstatus,in_status,in_statuscode) select inquiry_seq.nextval, bi_code,bi_date,bi_recorddate,'采购询价',bi_enddate,bi_pricekind,bi_recorder,bi_kind,bi_pricetype,bi_attach,bi_cop,bi_environment,bi_purpose,'公开询价单','已上传','已审核','AUDITED' from batchinquiry where bi_code = '"
				+ in_code + "'";
		baseDao.execute(insertSql);
	}

	/**
	 * 插入明细
	 * 
	 * @param detail
	 * @param pr_id
	 * @param in_id
	 */
	private void insertInquiryDetail(InquiryDetail detail, Long pr_id, Long in_id, Integer detno) {
		String insertDetailSql = "insert into inquirydetail(id_id,id_inid,id_detno,id_prodid,id_prodcode,id_vendname,id_vendyyzzh,id_currency,id_venduu,id_quto) values(inquirydetail_seq.nextval,"
				+ in_id
				+ ","
				+ detno
				+ ","
				+ pr_id
				+ ",'"
				+ detail.getId_prodcode()
				+ "','"
				+ detail.getId_vendname()
				+ "','"
				+ detail.getId_vendyyzzh() + "','" + detail.getId_currency() + "'," + detail.getVe_uu() + "," + detail.getId_quto() + ")";
		baseDao.execute(insertDetailSql);
	}

	/**
	 * 判断当前供应商对应的单号是否存在
	 * 
	 * @param id_vendyyzzh
	 * @param in_id
	 * @return
	 */
	private InquiryDetail checkInquiryDetailisExist(Long id_venduu, Long in_id, String id_prodcode) {
		String sql = "select * from inquirydetail where id_venduu = '" + id_venduu + "' and id_inid = " + in_id + " and id_prodcode ='"
				+ id_prodcode + "'";
		List<InquiryDetail> details = baseDao.query(sql, InquiryDetail.class);
		return details.size() > 0 ? details.get(0) : null;
	}

	/**
	 * 判断询价单是否已经存在
	 * 
	 * @param in_code
	 */
	private Inquiry checkInquiryisExist(String in_code) {
		String sql = "select * from inquiry where in_code = '" + in_code + "'";
		List<Inquiry> inquiries = baseDao.query(sql, Inquiry.class);
		return inquiries.size() > 0 ? inquiries.get(0) : null;
	}

	/**
	 * 保存来自平台的采购询价单附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveInquiryDetailAttach(List<InquiryDetail> details) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (InquiryDetail detail : details) {
			if (StringUtil.hasText(detail.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : detail.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update InquiryDetail set id_attach='" + attachIds.toString() + "' where id_id=" + detail.getId_id());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 修改平台里面的报价信息的传输状态
	 * 
	 * @return
	 */
	private boolean onReplySuccess(List<InquiryDetail> details, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryDetail detail : details) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(detail.getB2b_id_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/inquiry/reply/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传买家对报价信息是否采纳的决策到平台
	 * 
	 * @return
	 */
	private boolean uploadInquiryDecide(Master master) {
		List<InquiryDecide> replies = getInquiryDecide();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/reply/decide?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(买家)询价单-上传是否采纳的决策", replies.size(), response));
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
	private List<InquiryDecide> getInquiryDecide() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select id_id,in_code,id_detno,id_isagreed id_agreed from InquiryDetail left join Inquiry on id_inid=in_id left join vendor on id_vendcode=ve_code where IN_SENDSTATUS='已上传' and nvl(in_class,' ')<>'主动报价' and ID_SENDSTATUS='待上传' and id_isagreed is not null and ve_uu is not null and nvl(ve_b2benable,0)=1 and rownum < 100 order by in_code,id_detno",
							new BeanPropertyRowMapper<InquiryDecide>(InquiryDecide.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 买家是否采纳的决策成功传到平台之后
	 */
	private void onReplySuccess(List<InquiryDecide> decides) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryDecide decide : decides) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(decide.getId_id());
		}
		baseDao.execute("update InquiryDetail set id_sendstatus='已下载' where id_id in (" + idStr.toString() + ")");
	}

	/**
	 * 上传买家对报价信息作废的决策到平台
	 * 
	 * @return
	 */
	private boolean uploadInquiryInvalid(Master master) {
		List<Inquiry> replies = getInquiryInvalid();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/inquiry/invalid?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onInvalidSuccess(replies);
				baseDao.save(new TaskLog("(买家)询价单-上传作废的决策", replies.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的买家作废
	 * 
	 * @return
	 */
	private List<Inquiry> getInquiryInvalid() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select * from Inquiry left join InquiryDetail on id_inid=in_id left join vendor on id_vendcode=ve_code where in_status = '已作废' and in_sendstatus <> '作废已上传' and nvl(in_class,' ')<>'主动报价'  and ve_uu is not null and nvl(ve_b2benable,0)=1 and rownum <= 100 order by in_code",
							new BeanPropertyRowMapper<Inquiry>(Inquiry.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 买家作废询价单成功传到平台之后
	 */
	private void onInvalidSuccess(List<Inquiry> inquiries) {
		StringBuffer idStr = new StringBuffer();
		for (Inquiry inquiry : inquiries) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(inquiry.getIn_id());
		}
		baseDao.execute("update Inquiry set in_sendstatus='作废已上传' where in_id in (" + idStr.toString() + ")");
	}

	/**
	 * 从平台下载供应商下达到UAS的主动报价
	 * 
	 * @return
	 */
	private boolean downloadInquiries(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/quotation?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Inquiry> inquiries = FlexJsonUtil.fromJsonArray(data, Inquiry.class);
					if (!CollectionUtil.isEmpty(inquiries)) {
						saveSaleDown(inquiries, master);
						baseDao.save(new TaskLog("(买家)询价单-下载供应商主动报价", inquiries.size(), response));
						// saveQuotationMessage(inquiries);
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)询价单-下载供应商主动报价", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// /**
	// * 主动报价单下载成功，记录消息
	// *
	// * @param inquiries
	// */
	// private void saveQuotationMessage(List<Inquiry> inquiries) {
	// for (Inquiry inquiry : inquiries) {
	// pagingReleaseService.B2BMsg("Inquiry",
	// String.valueOf(inquiry.getIn_id()), "new");
	// }
	// }

	/**
	 * 保存主动报价单
	 * 
	 * @param inquiries
	 * @param enterprise
	 */
	private void saveSaleDown(List<Inquiry> inquiries, Master master) {
		if (!CollectionUtil.isEmpty(inquiries)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_qu_id
			for (Inquiry inquiry : inquiries) {
				int count = baseDao.getCount("select count(1) from Inquiry where b2b_qu_id=" + inquiry.getB2b_qu_id());
				if (count == 0) {
					String code = baseDao.sGetMaxNumber("Inquiry", 2);
					int inId = baseDao.getSeqId("Inquiry_SEQ");
					sqls.add(inquiry.toSqlString(code, inId));
					if (!CollectionUtil.isEmpty(inquiry.getDetails())) {
						for (InquiryDetail detail : inquiry.getDetails()) {
							int idId = baseDao.getSeqId("InquiryDetail_SEQ");
							sqls.add(detail.toSqlString(idId, inId));
							if (!CollectionUtil.isEmpty(detail.getDets())) {
								for (InquiryDetailDet det : detail.getDets()) {
									sqls.add(det.toSqlString(idId));
								}
							}
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(inId);
					b2bStr.append(inquiry.getB2b_qu_id());
					// 发起任务
					/*
					 * int taskId = baseDao.getSeqId("PROJECTTASK_SEQ"); String
					 * buyer = getBuyerCode(inquiry); if
					 * (StringUtil.hasText(buyer)) { String vendor =
					 * getVendNameByUU(inquiry.getDetails().get(0).getVe_uu());
					 * sqls.add(
					 * "insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
					 * +
					 * "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) "
					 * + " values (" + taskId + ",'供应商主动报价单->" +
					 * inquiry.getIn_code() + "','normal','已启动','DOING'," +
					 * "'已审核','AUDITED',sysdate,'billtask','" + buyer + "','" +
					 * vendor + "','','','" +
					 * baseDao.sGetMaxNumber("ProjectTask", 2) +
					 * "','','jsps/b2b/sale/zdQu.jsp?formCondition=in_idIS" +
					 * inId + "&gridCondition=id_inidIS" + inId + "')");
					 * sqls.add(
					 * "insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
					 * +
					 * "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values (resourceassignment_seq.nextval,"
					 * + taskId + ",'','" + buyer +
					 * "','',1,'进行中','START',100,'billtask','供应商主动报价单->" +
					 * inquiry.getIn_code() + "')"); }
					 */
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update InquiryDetail set (id_vendcode,id_vendname)=(select ve_code,ve_name from vendor where ve_uu=id_venduu) where id_inid in ("
						+ idStr.toString() + ")");
				sqls.add("update InquiryDetail set id_lapqty=0,id_price=nvl((select idd_price from InquiryDetailDet where idd_idid=id_id and idd_lapqty=0),0) where id_inid in ("
						+ idStr.toString() + ")");
				sqls.add("update inquirydetail set (ID_PRODID, id_prodname, id_prodspec, id_produnit)=(select pr_id, pr_detail, pr_spec, pr_unit from product where pr_code=id_prodcode) where id_inid in ("
						+ idStr.toString() + ")");
				// 更新主动报价状态类型为采购
				sqls.add("update Inquiry set in_kind='采购',in_pricetype='标准',IN_AUDITDATE=sysdate where in_id in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				JSONObject js = null;
				for (Inquiry inquiry : inquiries) {
					String buyer = getBuyerCode(inquiry);
					js = getBuyer(inquiry);
					if (js != null) {
						// String
						// sqlstr="update inquiry set
						// in_recorder='"+js.getString("emname")+"',in_recorderid='"+js.getInt("emid")+"'
						// where in_source='"+inquiry.getIn_code()+"'";
						baseDao.execute("update inquiry set in_recorder='" + js.getString("emname") + "',in_recorderid='"
								+ js.getInt("emid") + "' where in_source='" + inquiry.getIn_code() + "'");
					}
					if (StringUtil.hasText(buyer)) {
						// String vendorName =
						// getVendNameByUU(inquiry.getDetails().get(0).getVe_uu());
						// SimpleDateFormat timeFormat = new
						// SimpleDateFormat("MM月dd日 HH:mm:ss");
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
						// buyer, "新增一条供应商主动报价",
						// vendorName + " " +
						// timeFormat.format(inquiry.getIn_date()), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(),
						// buyer,
						// "新增一条供应商主动报价 " + vendorName + " " +
						// timeFormat.format(inquiry.getIn_date()));
					}
				}
				onInquiryDownSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 取采购方联系人
	 * 
	 * @param down
	 * @return
	 */
	private String getBuyerCode(Inquiry inquiry) {
		if (inquiry.getIn_buyeruu() != null) {
			Employee employee = employeeService.getEmployeeByUu(inquiry.getIn_buyeruu());
			if (employee != null)
				return employee.getEm_code();
		}
		try {
			return baseDao.getJdbcTemplate().queryForObject(
					"select em_code from employee left join vendor on ve_buyerid=em_id where ve_uu=?", String.class,
					inquiry.getDetails().get(0).getVe_uu());
		} catch (EmptyResultDataAccessException e) {
			// 鉴于em_uu、ve_buyerid均未维护的情况
			return null;
		}
	}

	/**
	 * 取采购方联系人
	 * 
	 * @param down
	 * @return
	 */
	private JSONObject getBuyer(Inquiry inquiry) {
		try {
			SqlRowList rs = baseDao
					.queryForRowSet("select em_code,em_name,em_id from employee left join vendor on ve_buyerid=em_id where ve_uu="
							+ inquiry.getDetails().get(0).getVe_uu());
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("emcode", rs.getGeneralString("em_code"));
				obj.put("emname", rs.getGeneralString("em_name"));
				obj.put("emid", rs.getGeneralInt("em_id"));
				return obj;
			} else {
				return null;
			}
		} catch (EmptyResultDataAccessException e) {
			// 鉴于em_uu、ve_buyerid均未维护的情况
			return null;
		}
	}

	/**
	 * 成功写到inquiry之后，修改平台的主动报价单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onInquiryDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/quotation/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传买家对主动报价询价单的报价信息作废的决策到平台
	 * 
	 * @return
	 */
	private boolean uploadQuotationInvalid(Master master) {
		List<Inquiry> replies = getQuotationInvalid();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/quotation/invalid?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onInvalidQuotationSuccess(replies);
				baseDao.save(new TaskLog("(买家)主动报价单-上传作废的决策", replies.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的买家作废的主动报价询价单
	 * 
	 * @return
	 */
	private List<Inquiry> getQuotationInvalid() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select * from Inquiry left join InquiryDetail on id_inid=in_id left join vendor on id_vendcode=ve_code where in_status = '已作废' and in_sendstatus <> '作废已上传' and nvl(in_class,' ')='主动报价'  and ve_uu is not null and nvl(ve_b2benable,0)=1 and rownum <= 100 order by in_code",
							new BeanPropertyRowMapper<Inquiry>(Inquiry.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 买家作废主动报价询价单成功传到平台之后
	 */
	private void onInvalidQuotationSuccess(List<Inquiry> inquiries) {
		StringBuffer idStr = new StringBuffer();
		for (Inquiry inquiry : inquiries) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(inquiry.getIn_id());
		}
		baseDao.execute("update Inquiry set in_sendstatus='作废已上传' where in_id in (" + idStr.toString() + ")");
	}

	/**
	 * 上传买家对主动报价信息是否采纳的决策到平台
	 * 
	 * @return
	 */
	private boolean uploadQuotationDecide(Master master) {
		List<InquiryDecide> decides = getQuotationDecide();
		if (!CollectionUtil.isEmpty(decides)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(decides));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/quotation/decide?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onQuotationDecideSuccess(decides);
				baseDao.save(new TaskLog("(买家)询价单-上传主动报价是否采纳的决策", decides.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的买家对主动报价的采纳决策
	 * 
	 * @return
	 */
	private List<InquiryDecide> getQuotationDecide() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select id_id,id_detno,b2b_qu_id,id_isagreed id_agreed from InquiryDetail left join Inquiry on id_inid=in_id left join vendor on id_vendcode=ve_code where IN_SENDSTATUS='已上传' and in_class='主动报价' and ID_SENDSTATUS='待上传' and id_isagreed is not null and ve_uu is not null and nvl(ve_b2benable,0)=1 and b2b_qu_id is not null order by in_code,id_detno",
							new BeanPropertyRowMapper<InquiryDecide>(InquiryDecide.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 买家对主动报价是否采纳的决策成功传到平台之后
	 */
	private void onQuotationDecideSuccess(List<InquiryDecide> decides) {
		StringBuffer idStr = new StringBuffer();
		for (InquiryDecide decide : decides) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(decide.getId_id());
		}
		baseDao.execute("update InquiryDetail set id_sendstatus='已下载' where id_id in (" + idStr.toString() + ")");
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

}
