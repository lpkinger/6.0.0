package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Prod;
import com.uas.b2b.model.Quotation;
import com.uas.b2b.model.QuotationDecide;
import com.uas.b2b.model.QuotationDetail;
import com.uas.b2b.model.QuotationDetailDet;
import com.uas.b2b.model.QuotationDown;
import com.uas.b2b.model.QuotationDownDecide;
import com.uas.b2b.model.QuotationDownDetail;
import com.uas.b2b.model.RemoteFile;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.crm.CustomerService;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为卖家ERP，获取客户下达到平台的询价单、将报价信息上传到平台
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class QuotationDownTask extends AbstractTask {

	@Autowired
	private CustomerService customerService;
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
		logger.info(this.getClass() + " downloadQuotationDown start");
		downloadQuotationDown(master);
		logger.info(this.getClass() + " downloadQuotationDown end");
		logger.info(this.getClass() + " downloadQuotationReply start");
		downloadQuotationReply(master);
		logger.info(this.getClass() + " downloadQuotationReply end");
		logger.info(this.getClass() + " uploadQuotationReply start");
		uploadQuotationReply(master);
		logger.info(this.getClass() + " uploadQuotationReply end");
		logger.info(this.getClass() + " downloadQuotationReplyDecide start");
		downloadQuotationReplyDecide(master);
		logger.info(this.getClass() + " downloadQuotationReplyDecide end");
		logger.info(this.getClass() + " downloadQuotationReplyInvalid start");
		downloadQuotationReplyInvalid(master);
		logger.info(this.getClass() + " downloadQuotationReplyInvalid end");
		logger.info(this.getClass() + " uploadProduct start");
		// 主动报价
		if (uploadProduct(master)) {
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadQuotation start");
			uploadQuotation(master);
			logger.info(this.getClass() + " uploadQuotation end");
		}
		logger.info(this.getClass() + " downloadQuotation start");
		// 下载在平台上新增的主动报价
		downloadQuotation(master);
		logger.info(this.getClass() + " downloadQuotation end");
		logger.info(this.getClass() + " downloadQuotationInvalid start");
		// 主动报价作废
		downloadQuotationInvalid(master);
		logger.info(this.getClass() + " downloadQuotationInvalid end");
		logger.info(this.getClass() + " downloadQuotationDecide start");
		// 主动报价采纳结果
		downloadQuotationDecide(master);
		logger.info(this.getClass() + " downloadQuotationDecide end");
	}

	/**
	 * 从平台下载客户下达到平台的询价单
	 * 
	 * @return
	 */
	private boolean downloadQuotationDown(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/inquiry?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<QuotationDown> sales = FlexJsonUtil.fromJsonArray(data, QuotationDown.class);
					if (!CollectionUtil.isEmpty(sales)) {
						saveQuotationDown(sales, master);
						baseDao.save(new TaskLog("(卖家)客户询价-下载客户询价单", sales.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户询价-下载客户询价单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存客户询价单
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveQuotationDown(List<QuotationDown> downs, Master master) {
		if (!CollectionUtil.isEmpty(downs)) {
			List<String> sqls = new ArrayList<String>();
			Set<String> codeAndUU = new HashSet<String>();
			Set<Long> idSet = new HashSet<Long>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_pu_id

			for (QuotationDown down : downs) {
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(down.getB2b_id_id());
				int count = baseDao.getCount("select count(1) from QuotationDown where b2b_id_id=" + down.getB2b_id_id());
				if (count == 0) {
					int quId = baseDao.getSeqId("QuotationDown_SEQ");
					sqls.addAll(down.toCascadedSqlString(quId));
					if (idStr.length() > 0) {
						idStr.append(",");
					}
					idStr.append(quId);
					// 需要持久化单据再新增消息，所以将id存起来，持久化之后，再产生消息
					// 根据单号 和 接收人 去重 （因为单条明细，可能对应不同联系人）
					if (!codeAndUU.contains(down.getQu_code() + down.getQu_selleruu())) {
						codeAndUU.add(down.getQu_code() + down.getQu_selleruu());
						idSet.add(Long.valueOf(quId));
					}
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update QuotationDown set (qu_sellercode,qu_seller)=(select em_code,em_name from employee where em_uu=qu_selleruu) where qu_id in ("
						+ idStr.toString() + ") and qu_selleruu is not null");
				sqls.add("update QuotationDown set (qu_custcode,qu_custname,qu_sellercode,qu_seller)=(select cu_code,cu_name,nvl(qu_sellercode,cu_sellercode),nvl(qu_seller,em_name) from customer left join employee on cu_sellercode=em_code where cu_uu=qu_custuu) where qu_id in ("
						+ idStr.toString() + ")");
				sqls.add("update QuotationDown set qu_prodcode=(select max(pc_prodcode) from productcustomer where pc_custcode=qu_custcode and pc_custprodcode=qu_custprodcode and pc_custproddetail=qu_custproddetail and nvl(pc_custprodspec,' ')=nvl(qu_custprodspec,' ')) where qu_id in ("
						+ idStr.toString() + ")");
				sqls.add("update QuotationDown set qu_prodcode=(select pr_code from product where pr_code=qu_custprodcode) where nvl(qu_prodcode,' ')=' ' and qu_id in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				// 产生消息
				for (Long id : idSet) {
					pagingReleaseService.B2BMsg("QuotationDown", String.valueOf(id), "save");
				}
				onQuotationDownSuccess(b2bStr.toString(), master);
				try {
					saveAttach(downs);
				} catch (Exception e) {

				}
			} else {
				// 无需保存的情况下，只要无错误，仍然回执成功
				onQuotationDownSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 保存来自平台的附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveAttach(List<QuotationDown> downs) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (QuotationDown down : downs) {
			if (StringUtil.hasText(down.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : down.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update QuotationDown set qu_attach='" + attachIds.toString() + "' where b2b_id_id=" + down.getB2b_id_id());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 取业务员
	 * 
	 * @param down
	 * @return
	 */
	private String getSellerCode(QuotationDown down) {
		if (down.getQu_selleruu() != null) {
			Employee employee = employeeService.getEmployeeByUu(down.getQu_selleruu());
			if (employee != null)
				return employee.getEm_code();
		}
		return customerService.getSallerCodeByCustomerUU(down.getQu_custuu());
	}

	/**
	 * 成功写到quotationdown之后，修改平台的询价单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onQuotationDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/inquiry?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 直接在平台报价的记录，传到卖家ERP
	 */
	private boolean downloadQuotationReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<QuotationDown> downs = FlexJsonUtil.fromJsonArray(data, QuotationDown.class);
					if (!CollectionUtil.isEmpty(downs)) {
						saveQuotationReply(downs, master);
						baseDao.save(new TaskLog("(卖家)客户询价-下载平台报价", downs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户询价-下载平台报价", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的报价记录
	 */
	private void saveQuotationReply(List<QuotationDown> downs, Master master) {
		if (!CollectionUtil.isEmpty(downs)) {
			List<String> sqls = new ArrayList<String>();
			for (QuotationDown down : downs) {
				Integer qu_id = baseDao.getFieldValue("QuotationDown", "qu_id", "b2b_id_id=" + down.getB2b_id_id(), Integer.class);
				if (qu_id != null) {
					sqls.add("update QuotationDown set qu_sendstatus='已下载',qu_fromdate="
							+ (down.getQu_fromdate() == null ? "null" : DateUtil.parseDateToOracleString(null, down.getQu_fromdate()))
							+ ",qu_todate="
							+ (down.getQu_todate() == null ? "null" : DateUtil.parseDateToOracleString(null, down.getQu_todate()))
							+ ",qu_minbuyqty=" + down.getQu_minbuyqty() + ",qu_minqty=" + down.getQu_minqty() + " where qu_id=" + qu_id);
					if (!CollectionUtil.isEmpty(down.getDetails())) {
						sqls.add("delete from QuotationDownDetail where qd_quid=" + qu_id);
						for (QuotationDownDetail detail : down.getDetails()) {
							sqls.add(detail.toSqlString(qu_id));
						}
					}
				}
			}
			baseDao.execute(sqls);
			onDownloadReplySuccess(downs, master);
		}
	}

	/**
	 * 将从平台下载的报价记录保存成功之后，回执给平台
	 * 
	 * @param downs
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplySuccess(List<QuotationDown> downs, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (QuotationDown down : downs) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(down.getB2b_id_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传报价信息到平台
	 * 
	 * @return
	 */
	private boolean uploadQuotationReply(Master master) {
		List<QuotationDown> downs = getQuotationReply();
		if (!CollectionUtil.isEmpty(downs)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(downs));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(downs);
				baseDao.save(new TaskLog("(卖家)客户询价-上传报价信息", downs.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的报价记录
	 * 
	 * @return
	 */
	private List<QuotationDown> getQuotationReply() {
		try {
			List<QuotationDown> downs = baseDao
					.getJdbcTemplate()
					.query("select b2b_id_id,qu_id,qu_custuu,qu_fromdate,qu_todate,qu_minbuyqty,qu_minqty,qu_leadtime,qu_brand,qu_prodcode from QuotationDown where QU_SENDSTATUS='待上传' order by qu_code,qu_detno",
							new BeanPropertyRowMapper<QuotationDown>(QuotationDown.class));
			for (QuotationDown down : downs) {
				List<QuotationDownDetail> details = baseDao.getJdbcTemplate().query("select * from QuotationDownDetail where qd_quid=?",
						new BeanPropertyRowMapper<QuotationDownDetail>(QuotationDownDetail.class), down.getQu_id());
				down.setDetails(details);
			}
			return downs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 报价记录成功传到平台之后
	 */
	private void onReplySuccess(List<QuotationDown> downs) {
		StringBuffer idStr = new StringBuffer();
		for (QuotationDown down : downs) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(down.getQu_id());
		}
		baseDao.execute("update QuotationDown set qu_sendstatus='已下载' where qu_id in (" + idStr.toString() + ")");
	}

	/**
	 * 平台的报价信息采纳结果，传到卖家ERP
	 */
	private boolean downloadQuotationReplyDecide(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply/decide?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<QuotationDownDecide> downs = FlexJsonUtil.fromJsonArray(data, QuotationDownDecide.class);
					if (!CollectionUtil.isEmpty(downs)) {
						saveQuotationReplyDecide(downs, master);
						baseDao.save(new TaskLog("(卖家)客户询价-下载采纳结果", downs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户询价-下载采纳结果", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的采纳结果
	 */
	private void saveQuotationReplyDecide(List<QuotationDownDecide> decides, Master master) {
		if (!CollectionUtil.isEmpty(decides)) {
			List<String> sqls = new ArrayList<String>();
			Set<Long> idSet = new HashSet<Long>();
			for (QuotationDownDecide decide : decides) {
				Integer qu_id = baseDao.getFieldValue("QuotationDown", "qu_id", "b2b_id_id=" + decide.getB2b_id_id(), Integer.class);
				if (qu_id != null) {
					sqls.add("update QuotationDown set qu_agreed=" + decide.getQu_agreed() + " where qu_id=" + qu_id);
					// 因为单号相同的两条记录采纳结果也可能不一样，所以这里按id区分
					if (!idSet.contains(Long.valueOf(qu_id))) {
						if (decide.getQu_agreed() == 1) {
							pagingReleaseService.B2BMsg("QuotationDown", String.valueOf(qu_id), "accept");
						} else if (decide.getQu_agreed() == 0) {
							pagingReleaseService.B2BMsg("QuotationDown", String.valueOf(qu_id), "refuse");
						}
						idSet.add(Long.valueOf(qu_id));
					}
					// 将采纳结果通知采购单供应商联系人
					// 在quotationDown表中已做处理，qu_sellerCode字段存的是qu_selleruu对应的code
					// || customer表中对应的业务员的code
					String sellerCode = baseDao.getFieldValue("QuotationDown", "qu_sellercode", "b2b_id_id=" + decide.getB2b_id_id(),
							String.class);
					if (sellerCode != null && !sellerCode.equals("")) {
						// String tittle = (decide.getQu_agreed() == 1) ?
						// "客户同意了您的报价" : "客户不同意您的报价";
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
						// sellerCode, tittle,
						// decide.getQu_code() + " 第" + decide.getQu_detno() +
						// "行", null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(),
						// sellerCode, tittle + " - " + decide.getQu_code() +
						// " 第"
						// + decide.getQu_detno() + "行");
					}
				}
			}
			baseDao.execute(sqls);
			onDownloadReplyDecideSuccess(decides, master);
		}
	}

	/**
	 * 将从平台下载的采纳结果保存成功之后，回执给平台
	 * 
	 * @param downs
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplyDecideSuccess(List<QuotationDownDecide> decides, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (QuotationDownDecide decide : decides) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(decide.getB2b_id_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply/decide/back?access_id="
					+ master.getMa_uu(), params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 平台的报价信息作废信息，传到卖家ERP
	 */
	private boolean downloadQuotationReplyInvalid(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply/invalid?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<QuotationDown> downs = FlexJsonUtil.fromJsonArray(data, QuotationDown.class);
					if (!CollectionUtil.isEmpty(downs)) {
						saveQuotationReplyInvalid(downs, master);
						baseDao.save(new TaskLog("(卖家)客户询价-下载作废结果", downs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户询价-下载作废结果", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的作废结果
	 */
	private void saveQuotationReplyInvalid(List<QuotationDown> downs, Master master) {
		if (!CollectionUtil.isEmpty(downs)) {
			List<String> sqls = new ArrayList<String>();
			Set<String> codeAndUU = new HashSet<String>();
			for (QuotationDown down : downs) {
				Integer qu_id = baseDao.getFieldValue("QuotationDown", "qu_id", "b2b_id_id=" + down.getB2b_id_id(), Integer.class);
				String qu_code = baseDao.getFieldValue("QuotationDown", "qu_code", "b2b_id_id=" + down.getB2b_id_id(),
						String.class);
				String qu_selleruu = baseDao.getFieldValue("QuotationDown", "qu_selleruu",
						"b2b_id_id=" + down.getB2b_id_id(), String.class);
				if (qu_id != null) {
					sqls.add("update QuotationDown set qu_status= '已作废' where qu_id=" + qu_id);
					if (null != qu_code && null != qu_selleruu && !codeAndUU.contains(qu_code + qu_selleruu)) { // 产生消息,作废以主表为单位，所以根据单号和联系人判断。
						pagingReleaseService.B2BMsg("QuotationDown", String.valueOf(qu_id), "cancel");
						codeAndUU.add(qu_code + qu_selleruu);
					}
					// 在quotationDown表中已做处理，qu_sellerCode字段存的是qu_selleruu对应的code
					// || customer表中对应的业务员的code
					String sellerCode = baseDao.getFieldValue("QuotationDown", "qu_sellercode", "b2b_id_id=" + down.getB2b_id_id(),
							String.class);
					if (sellerCode != null && !sellerCode.equals("")) {
						// String tittle = (decide.getQu_agreed() == 1) ?
						// "客户同意了您的报价" : "客户不同意您的报价";
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(),
						// sellerCode, tittle,
						// decide.getQu_code() + " 第" + decide.getQu_detno() +
						// "行", null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(),
						// sellerCode, tittle + " - " + decide.getQu_code() +
						// " 第"
						// + decide.getQu_detno() + "行");
					}
				}
			}
			baseDao.execute(sqls);
			onDownloadReplyInvalidSuccess(downs, master);
		}
	}

	/**
	 * 将从平台下载的作废结果保存成功之后，回执给平台
	 * 
	 * @param downs
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplyInvalidSuccess(List<QuotationDown> downs, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (QuotationDown down : downs) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(down.getB2b_id_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/inquiry/reply/invalid/back?access_id="
					+ master.getMa_uu(), params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
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
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(prods));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/product?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedProduct(prods);
				}
				baseDao.save(new TaskLog("(卖家)客户询价-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 主动报价单待上传的物料
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select distinct product.* from product where exists (select 1 from quotation left join customer on qu_custcode=cu_code left join quotationdetail on qd_quid=qu_id where QU_SENDSTATUS='待上传' and cu_uu is not null and qd_prodcode=pr_code) and pr_statuscode='AUDITED' and nvl(pr_sendstatus,' ')<>'已上传'",
							new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新已上传的物料资料
	 * 
	 * @param prods
	 *            已上传的物料资料
	 */
	public void updateUploadedProduct(List<Prod> prods) {
		for (Prod prod : prods) {
			baseDao.execute("update product set PR_SENDSTATUS='已上传' where pr_id = ?", prod.getPr_id());
		}
	}

	/**
	 * 上传主动报价
	 * 
	 * @return
	 */
	private boolean uploadQuotation(Master master) {
		List<Quotation> quotations = getQuotationsUpload();
		if (!CollectionUtil.isEmpty(quotations)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(quotations));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/quotation?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					String data = response.getResponseText();
					if (StringUtil.hasText(data)) {
						List<HashMap<String, Object>> saveFailureInfo = FlexJsonUtil.fromJsonArray(data, HashMap.class);
						if (!CollectionUtil.isEmpty(saveFailureInfo)) {
							updateQuotationWarn(saveFailureInfo);
						}
					}
					onUploadQuotationSuccess(quotations);
				} else {
					System.out.println("系统错误：" + response.getResponseText());
				}
				baseDao.save(new TaskLog("(卖家)客户询价-上传主动报价", quotations.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @Description: 平台不存在对应的物料对照关系，主动报价单上传失败，错误提示显示在主动报价单备注上。
	 * @date 2015年12月3日
	 */
	private void updateQuotationWarn(List<HashMap<String, Object>> listMap) {
		List<String> sqls = new ArrayList<String>();
		String code = "";
		String errorStr = "";
		for (HashMap<String, Object> map : listMap) {
			if (!code.equals((String) map.get("code"))) {
				if (!code.isEmpty()) {
					sqls.add("update Quotation set qu_remark = '第" + errorStr + " 行 物料对照关系 不存在' where qu_code='" + code + "'");
				}
				code = (String) map.get("code");
				errorStr += map.get("detno");
			} else {
				errorStr += ", " + map.get("detno");
			}
		}
		if (StringUtil.hasText(code)) {
			sqls.add("update Quotation set qu_remark = '第" + errorStr + " 行  物料对照关系   不存在' where qu_code='" + code + "'");
		}
		baseDao.execute(sqls);

	}

	/**
	 * 获取需要上传的主动报价
	 * 
	 * @return
	 */
	public List<Quotation> getQuotationsUpload() {
		try {
			List<Quotation> quotations = baseDao
					.getJdbcTemplate()
					.query("select Quotation.*,cu_uu,qu_custcontact cu_contact,qu_custcontactuu cu_contactuu,em_uu qu_useruu from Quotation left join customer on qu_custcode=cu_code left join employee on qu_recorderid=em_id where QU_SENDSTATUS='待上传' and qu_kind='主动报价' and cu_uu is not null order by qu_code",
							new BeanPropertyRowMapper<Quotation>(Quotation.class));
			for (Quotation quotation : quotations) {
				List<QuotationDetail> details = baseDao.getJdbcTemplate().query(
						"select QuotationDetail.* from QuotationDetail where qd_quid=?",
						new BeanPropertyRowMapper<QuotationDetail>(QuotationDetail.class), quotation.getQu_id());
				for (QuotationDetail detail : details) {
					try {
						List<QuotationDetailDet> dets = baseDao.getJdbcTemplate().query(
								"select * from QuotationDetailDet where qdd_qdid=?",
								new BeanPropertyRowMapper<QuotationDetailDet>(QuotationDetailDet.class), detail.getQd_id());
						if (dets.size() == 0)
							throw new EmptyResultDataAccessException(1);
						detail.setDets(dets);
					} catch (EmptyResultDataAccessException e) {
						baseDao.execute("insert into QuotationDetailDet(qdd_id,qdd_qdid,qdd_lapqty,qdd_price) values (QuotationDetailDet_seq.nextval,"
								+ detail.getQd_id()
								+ ","
								+ (detail.getQd_lapqty() == null ? 0 : detail.getQd_lapqty())
								+ ","
								+ (detail.getQd_price() == null ? 0 : detail.getQd_price()) + ")");
						List<QuotationDetailDet> dets = baseDao.getJdbcTemplate().query(
								"select * from QuotationDetailDet where qdd_qdid=?",
								new BeanPropertyRowMapper<QuotationDetailDet>(QuotationDetailDet.class), detail.getQd_id());
						detail.setDets(dets);
					}
				}
				quotation.setDetails(details);
			}
			return quotations;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 主动报价成功上传到平台之后
	 * 
	 * @param quotations
	 */
	private void onUploadQuotationSuccess(List<Quotation> quotations) {
		for (Quotation quotation : quotations) {
			baseDao.execute("update quotation set QU_SENDSTATUS='已上传' where qu_id = ?", quotation.getQu_id());
		}
	}

	/**
	 * 平台的主动报价单作废信息，传到卖家ERP
	 */
	private boolean downloadQuotationInvalid(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/reply/invalid?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Quotation> quotations = FlexJsonUtil.fromJsonArray(data, QuotationDown.class);
					if (!CollectionUtil.isEmpty(quotations)) {
						saveQuotationInvalid(quotations, master);
						baseDao.save(new TaskLog("(卖家)主动报价-下载作废结果", quotations.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)主动报价-下载作废结果", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的主动报价单作废结果
	 */
	private void saveQuotationInvalid(List<Quotation> quotations, Master master) {
		if (!CollectionUtil.isEmpty(quotations)) {
			List<String> sqls = new ArrayList<String>();
			// Set<String> codeAndUU = new HashSet<String>();
			for (Quotation quotation : quotations) {
				Integer qu_id = baseDao.getFieldValue("Quotation", "qu_id", "b2b_qu_id=" + quotation.getB2b_qu_id(),
						Integer.class);
				// String qu_code = baseDao.getFieldValue("Quotation",
				// "qu_code", "b2b_qu_id=" + quotation.getB2b_qu_id(),
				// String.class);
				// String qu_selleruu = baseDao.getFieldValue("Quotation",
				// "qu_useruu",
				// "b2b_qu_id=" + quotation.getB2b_qu_id(), String.class);
				if (qu_id != null) {
					sqls.add("update Quotation set qu_status= '已作废' where qu_id=" + qu_id);
					// 主动报价单作废暂无模版，先注掉
					// if (null != qu_code && null != qu_selleruu &&
					// !codeAndUU.contains(qu_code + qu_selleruu)) { //
					// 产生消息,作废以主表为单位，所以根据单号和联系人判断。
					// pagingReleaseService.B2BMsg("Quotation",
					// String.valueOf(qu_id), "cancel");
					// codeAndUU.add(qu_code + qu_selleruu);
					// }
				}
			}
			baseDao.execute(sqls);
			onDownloadQuotationInvalidSuccess(quotations, master);
		}
	}

	/**
	 * 将从平台下载的主动报价单作废结果保存成功之后，回执给平台
	 * 
	 * @param downs
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadQuotationInvalidSuccess(List<Quotation> quotations, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (Quotation quotation : quotations) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(quotation.getB2b_qu_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/reply/invalid/back?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 平台的主动报价信息采纳结果，传到卖家ERP
	 */
	private boolean downloadQuotationDecide(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/decide?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<QuotationDecide> downs = FlexJsonUtil.fromJsonArray(data, QuotationDecide.class);
					if (!CollectionUtil.isEmpty(downs)) {
						saveQuotationDecide(downs, master);
						baseDao.save(new TaskLog("(卖家)客户询价-下载主动报价采纳结果", downs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户询价-下载主动报价采纳结果", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的主动报价的采纳结果
	 */
	private void saveQuotationDecide(List<QuotationDecide> decides, Master master) {
		if (!CollectionUtil.isEmpty(decides)) {
			List<String> sqls = new ArrayList<String>();
			for (QuotationDecide decide : decides) {
				sqls.add("update QuotationDetail set qd_agreed=" + decide.getQd_agreed()
						+ " where qd_quid=(select qu_id from quotation where qu_code='" + decide.getQu_code() + "') and qd_detno="
						+ decide.getQd_detno());
			}
			baseDao.execute(sqls);
			onDownloadQuotationDecideSuccess(decides, master);
		}
	}

	/**
	 * 将从平台下载的主动报价采纳结果保存成功之后，回执给平台
	 * 
	 * @param downs
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadQuotationDecideSuccess(List<QuotationDecide> decides, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (QuotationDecide decide : decides) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(decide.getB2b_qd_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/decide/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 下载平台上新增的主动报价单
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean downloadQuotation(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/back?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Quotation> quotations = FlexJsonUtil.fromJsonArray(data, Quotation.class);
					if (!CollectionUtil.isEmpty(quotations)) {
						saveQuotationBack(quotations, master);
						baseDao.save(new TaskLog("(卖家)主动报价单-下载平台上新增的主动报价单", quotations.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)主动报价单-下载平台上新增的主动报价单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台上下载的新增主动报价单
	 * 
	 * @param quotations
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean saveQuotationBack(List<Quotation> quotations, Master master) {
		if (!CollectionUtil.isEmpty(quotations)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_qu_id
			for (Quotation quotation : quotations) {
				int count = baseDao.getCount("select count(1) from quotation where b2b_qu_id=" + quotation.getB2b_qu_id());
				if (count == 0) {
					int quId = baseDao.getSeqId("quotation_SEQ");
					sqls.add(quotation.toSqlString(quId));
					if (!CollectionUtil.isEmpty(quotation.getDetails())) {
						for (QuotationDetail detail : quotation.getDetails()) {
							int qdId = baseDao.getSeqId("quotationDetail_SEQ");
							sqls.add(detail.toSqlString(qdId, quId));
							if (!CollectionUtil.isEmpty(detail.getDets())) {
								for (QuotationDetailDet det : detail.getDets()) {
									sqls.add(det.toSqlString(qdId));
								}
							}
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(quId);
					b2bStr.append(quotation.getB2b_qu_id());
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update quotation set (qu_custid, qu_custcode, qu_custname, QU_SELLERID, QU_SELLERCODE, QU_SELLER) = (select cu_id, cu_code, cu_name, CU_SELLERID, CU_SERVICECODE, CU_SELLERNAME from customer where customer.cu_uu = quotation.qu_custuu) where qu_id in ("
						+ idStr.toString() + ")");
				sqls.add("update quotation set (QU_RECORDERID, QU_RECORDER) = (select em_id, em_name from employee where employee.em_uu = quotation.qu_useruu) where qu_id in ("
						+ idStr.toString() + ")");
				sqls.add("update quotationdetail set QD_LAPQTY=0,QD_PRICE=nvl((select qdd_price from QUOTATIONDETAILDET where qdd_qdid=qd_id and qdd_lapqty=0),0)  where qd_quid in ("
						+ idStr.toString() + ")");
				sqls.add("update quotationdetail set (QD_PRODID, qd_prodcode, qd_prodname, QD_PRODSPEC, QD_PRODUNIT, QD_CUSTPRODDETAIL, QD_CUSTPRODSPEC) "
						+ "= (select pc_prodid, PC_PRODCODE, pr_detail, pr_spec, pr_unit, pc_custproddetail, pc_custprodspec "
						+ "from productcustomer left join product on productcustomer.pc_prodid = product.pr_id left join quotation on productcustomer.pc_custid = quotation.qu_custid "
						+ "where qu_id = qd_quid and pc_custprodcode = qd_custprodcode) where qd_quid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				onQuotationBackSuccess(b2bStr.toString(), master);
			}
		}
		return true;
	}

	/**
	 * 成功写到Quotation之后，修改平台的主动报价单回传状态为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onQuotationBackSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/quotation/back/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
