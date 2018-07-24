package com.uas.b2b.service.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.Attach;
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.Purchase;
import com.uas.b2b.model.PurchaseDetail;
import com.uas.b2b.model.PurchaseDetailEnd;
import com.uas.b2b.model.PurchaseReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为买家ERP，将采购单传入平台、获取平台上的供应商的回复记录
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseTask extends AbstractTask {
	// @Autowired
	// private EmployeeService employeeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	/**
	 * 最大允许的明细条数<br>
	 * 数据量过大会出现url过长，服务器拒绝访问，返回400
	 */
	private static final int max_size = 500;
	
	private Logger logger = Logger.getLogger(PurchaseTask.class);

	@Override
	protected void onExecute(Master master) {
		// if
		// (master.getMa_accesssecret().equals("2c1ea0898780796fe050007f01002ea6"))
		// {
		try {
			logger.info(this.getClass() + " uploadProduct start");
			if (uploadProduct(master)) {
				logger.info(this.getClass() + " uploadProduct end");
				logger.info(this.getClass() + " uploadPurchase start");
				uploadPurchase(master);
				logger.info(this.getClass() + " uploadPurchase end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(this.getClass() + " downloadReply start");
		downloadReply(master);
		logger.info(this.getClass() + " downloadReply end");
		logger.info(this.getClass() + " uploadPurchaseReply start");
		uploadPurchaseReply(master);
		logger.info(this.getClass() + " uploadPurchaseReply end");
		logger.info(this.getClass() + " uploadPurchaseEnd start");
		uploadPurchaseEnd(master);
		logger.info(this.getClass() + " uploadPurchaseEnd end");
		logger.info(this.getClass() + " downloadPrintLog start");
		downloadPrintLog(master);
		logger.info(this.getClass() + " downloadPrintLog end");
	}

	// }

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
				baseDao.save(new TaskLog("(买家)采购单-上传物料资料", prods.size(), response));
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
	 * 上传采购订单
	 * 
	 * @return
	 */
	private boolean uploadPurchase(Master master) {
		List<Purchase> purchases = getPurchasesUpload();
		if (!CollectionUtil.isEmpty(purchases)) {
			String idStr = CollectionUtil.getKeyString(purchases, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchases));
			try {
				beforePurchaseUpload(idStr);
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onPurchaseUploadSuccess(idStr);
				else
					onPurchaseUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)采购单-上传采购订单", purchases.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onPurchaseUploadFail(idStr);
				return false;
			} finally {
				checkPurchaseUpload(idStr);
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的采购单
	 * 
	 * @return
	 */
	public List<Purchase> getPurchasesUpload() {
		try {
			List<Purchase> purchases = baseDao
					.getJdbcTemplate()
					.query("select * from (select purchase.*,em_uu,em_name,em_sex,em_mobile,em_email,vendor.ve_uu,pu_vendcontact ve_contact,purchase.pu_vendcontactuu ve_contactuu from purchase left join vendor on pu_vendcode=ve_code left join employee on pu_buyerid=em_id where (PU_SENDSTATUS='待上传' or PU_SENDSTATUS='上传中') and pu_statuscode='AUDITED' and nvl(pu_ordertype,' ')<>'B2C' and ve_uu is not null and nvl(ve_b2benable,0)=1 and not exists (select 1 from purchasedetail,product where pd_puid=pu_id and pr_code=pd_prodcode and pr_sendstatus<>'已上传') order by pu_code) where rownum < 100",
							new BeanPropertyRowMapper<Purchase>(Purchase.class));
			List<Purchase> thisPost = new ArrayList<Purchase>();
			int count = 0;
			for (Purchase purchase : purchases) {
				try {
					/**
					 * @author wsy
					 * 双单位
					 */
					List<PurchaseDetail> purchaseDetails = baseDao
							.getJdbcTemplate()
							.query("select * from (select pd_code,pd_prodcode,pd_prattach,case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end pd_qty,pd_price,pd_delivery,pd_remark,pd_rate,pd_detno,pd_factory,pd_beipin,(select max(pv_vendprodspec) from productvendor where pv_prodcode=pd_prodcode and pv_vendcode=?) as pd_vendspec from purchasedetail p where pd_puid=?)",
									new BeanPropertyRowMapper<PurchaseDetail>(PurchaseDetail.class), purchase.getPu_vendcode(),
									purchase.getPu_id());
					if (!CollectionUtils.isEmpty(purchaseDetails)) {
						for(PurchaseDetail detail : purchaseDetails) {
							if (StringUtil.hasText(detail.getPd_prattach())) {
								String[] fileIds = detail.getPd_prattach().split(";");
								String erpUrl = getEnterpriseErpUrl();
								List<Attach> attaches = baseDao.getJdbcTemplate().query(
										"select fp_id, fp_size, fp_name from filepath where fp_id in ("
												+ StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
										new BeanPropertyRowMapper<Attach>(Attach.class));
								if (!CollectionUtil.isEmpty(attaches)) {
									for (Attach attach : attaches) {
										attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
									}
									detail.setAttaches(attaches);
								}
							}
						}
					}
					purchase.setPurchaseDetails(purchaseDetails);
					thisPost.add(purchase);
					if ((count = count + purchaseDetails.size()) >= max_size)
						break;
				} catch (EmptyResultDataAccessException e) {
					logger.info("why: " + e);
				}
			}
			return thisPost;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

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
	 * 上传结案、反结案采购订单
	 * 
	 * @return
	 */
	private boolean uploadPurchaseEnd(Master master) {
		List<PurchaseDetailEnd> details = getPurchaseEndUpload();
		if (!CollectionUtil.isEmpty(details)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(details));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/end?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedPurchaseEnd(details);
				}
				baseDao.save(new TaskLog("(买家)采购单-上传结案、反结案状态", details.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的结案、反结案采购单明细
	 * 
	 * @return
	 */
	public List<PurchaseDetailEnd> getPurchaseEndUpload() {
		try {
			List<PurchaseDetailEnd> details = baseDao
					.getJdbcTemplate()
					.query("select pd_code,pd_detno,case when pd_mrpstatuscode='FINISH' then 1 else 0 end pd_ended from PurchaseDetail left join purchase on pd_puid=pu_id left join vendor on pu_vendcode=ve_code where PU_SENDSTATUS='已上传' and nvl(pu_ordertype,' ')<>'B2C' and pd_endstatus='待上传' and ve_uu is not null and nvl(ve_b2benable,0)=1 order by pd_code,pd_detno",
							new BeanPropertyRowMapper<PurchaseDetailEnd>(PurchaseDetailEnd.class));
			return details;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 上传物料
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			List<Prod> prods = baseDao
					.getJdbcTemplate()
					.query("select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where exists (select 1 from purchase left join vendor on pu_vendcode=ve_code left join purchasedetail on pd_puid=pu_id where (PU_SENDSTATUS='待上传' or PU_SENDSTATUS='上传中' or pu_sendstatus='已上传') and nvl(pu_ordertype,' ')<>'B2C' and pu_statuscode='AUDITED' and ve_uu is not null and nvl(ve_b2benable,0)=1 and pd_prodcode=pr_code) and nvl(pr_sendstatus,' ')<>'已上传' order by pr_id) where rownum <= 500",
							new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新待上传的采购单状态
	 * 
	 * @param idStr
	 *            待上传的采购单
	 */
	public void beforePurchaseUpload(String idStr) {
		baseDao.execute("update purchase set PU_SENDSTATUS='上传中' where pu_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的采购单状态
	 * 
	 * @param idStr
	 *            上传成功的采购单
	 */
	public void onPurchaseUploadSuccess(String idStr) {
		baseDao.execute("update purchase set PU_SENDSTATUS='已上传' where pu_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的采购单状态
	 * 
	 * @param idStr
	 *            上传失败的采购单
	 */
	public void onPurchaseUploadFail(String idStr) {
		baseDao.execute("update purchase set PU_SENDSTATUS='待上传' where pu_id in (" + idStr + ") and PU_SENDSTATUS='上传中'");
	}

	public void checkPurchaseUpload(String idStr) {
		baseDao.execute("update purchase set PU_SENDSTATUS='待上传' where pu_id in (" + idStr + ") and PU_SENDSTATUS='上传中'");
	}

	/**
	 * 更新已上传的结案、反结案采购单明细状态
	 * 
	 * @param details
	 *            已上传的结案、反结案采购单明细
	 */
	public void updateUploadedPurchaseEnd(List<PurchaseDetailEnd> details) {
		for (PurchaseDetailEnd detail : details) {
			baseDao.execute("update purchasedetail set PD_ENDSTATUS='已上传' where pd_code = ? and pd_detno = ?", detail.getPd_code(),
					detail.getPd_detno());
		}
	}

	/**
	 * 从平台下载供应商的回复记录
	 * 
	 * @return
	 */
	private boolean downloadReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/purchase/reply?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<PurchaseReply> replies = FlexJsonUtil.fromJsonArray(data, PurchaseReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						savePurchaseReply(replies, master);
						baseDao.save(new TaskLog("(买家)采购单-下载回复", replies.size(), response));
						saveB2BMessage(replies);
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
	 * 回复成功保存消息通知
	 * 
	 * @param replies
	 */
	private void saveB2BMessage(List<PurchaseReply> replies) {
		Set<Long> idSet = new HashSet<Long>();
		for (PurchaseReply reply : replies) {
			Long pu_id = baseDao.getFieldValue("purchase", "pu_id", "pu_code = '" + reply.getPr_pucode() + "'", Long.class);
			if (null != pu_id && !idSet.contains(pu_id)) { // 一张采购单，一条消息
				pagingReleaseService.B2BMsg("Purchase", String.valueOf(pu_id), "reply");
				idSet.add(pu_id);
			}
		}
	}

	/**
	 * 修改平台里面的回复记录的状态
	 * 
	 * @return
	 */
	private boolean onReplySuccess(List<PurchaseReply> replies, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (PurchaseReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getB2b_pr_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/reply/back?access_id=" + master.getMa_uu(), params, true,
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
	public void savePurchaseReply(List<PurchaseReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> psql = new ArrayList<String>();
			for (PurchaseReply pr : replies) {
				String sql = "update PurchaseReply set pr_ifoverdate=-1 where pr_pucode='" + pr.getPr_pucode() + "' and pr_pddetno='"
						+ pr.getPr_pddetno() + "' and nvl(pr_ifoverdate,0)<>-1";
				psql.add(sql);
			}
			baseDao.execute(psql);
			List<String> sqls = new ArrayList<String>();
			for (PurchaseReply reply : replies) {
				sqls.add("MERGE into PurchaseDetail a using (select count(1) c from PurchaseReply where b2b_pr_id=" + reply.getB2b_pr_id()
						+ ") b on (b.c=0) when MATCHED THEN update set pd_qtyreply = nvl(pd_qtyreply, 0) + " + reply.getPr_qty()
						+ ", pd_deliveryReply = '" + DateUtil.format(reply.getPr_delivery(), Constant.YMD) + "', pd_replydetail='"
						+ StringUtil.nvl(reply.getPr_remark(), "") + "',pd_vendoreplydate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + " where pd_code='" + reply.getPr_pucode()
						+ "' and pd_detno=" + reply.getPr_pddetno());

				// 采购回复每次写的数量不做叠加处理，仅仅只是更新，回复数量只写本次最新的回复数量
				sqls.add("update PurchaseDetail set pd_qtyreply = " + reply.getPr_qty() + " where pd_code='" + reply.getPr_pucode()
						+ "' and pd_detno=" + reply.getPr_pddetno());

				sqls.add("MERGE into PurchaseReply a using (select count(1) c from PurchaseReply where b2b_pr_id="
						+ reply.getB2b_pr_id()
						+ ") b on (b.c>0) when NOT MATCHED THEN insert (b2b_pr_id,pr_id,pr_qty,pr_delivery,pr_remark,pr_pucode,pr_pddetno,pr_date,pr_recorder,pr_type,pr_sendstatus) values ("
						+ reply.getB2b_pr_id() + ",PURCHASEREPLY_SEQ.nextval," + +reply.getPr_qty() + ","
						+ DateUtil.parseDateToOracleString(null, reply.getPr_delivery()) + ",'" + StringUtil.nvl(reply.getPr_remark(), "")
						+ "','" + reply.getPr_pucode() + "'," + reply.getPr_pddetno() + ","
						+ DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", reply.getPr_date()) + ",'" + reply.getPr_recorder()
						+ "','" + StringUtil.nvl(reply.getPr_type(), "") + "','已下载')");
			}
			baseDao.execute(sqls);
			/*
			 * try { List<String> os = new ArrayList<String>(); List<String>
			 * sqlsdets = new ArrayList<String>();
			 * 
			 * for (PurchaseReply reply : replies) { if (os != null &&
			 * os.contains(reply.getPr_pucode()) && reply.getPr_qty() > 0) {
			 * 
			 * } else { os.add(reply.getPr_pucode()); if (reply.getPr_qty() < 0)
			 * { Object[] nos = baseDao.getFieldsDataByCondition(
			 * "BTOB$NOTICEMAN left join purchase on manid=pu_id Left join BTOB$NOTICESET ON caller=bn_caller"
			 * ,
			 * "MANS,bn_url,BN_MAINID,bn_gridmainid,bn_title,pu_id,bn_formcaller"
			 * , "caller='NOPurchaseReply' and pu_code='" + reply.getPr_pucode()
			 * + "'"); if (nos != null && nos[0] != null) { String formCondition
			 * = nos[2] + "=" + nos[5]; String gridCondition = nos[3] + "=" +
			 * nos[5]; String otherContent = nos[4] + ":" +
			 * reply.getPr_pucode(); String condition = ""; if (nos[3] != null)
			 * { condition = "formCondition=" + nos[2] + "IS" + nos[5] +
			 * "&gridCondition=" + nos[3] + "IS" + nos[5]; } else { condition =
			 * "formCondition=" + nos[2] + "IS" + nos[5] + ""; }
			 * 
			 * StringBuffer context = new StringBuffer();
			 * context.append("采购回复变更&nbsp;[" +
			 * DateUtil.parseDateToString(DateUtil.parseStringToDate(null,
			 * Constant.YMD_HMS), "MM-dd HH:mm") + "]");
			 * context.append("<a href=\"javascript:openUrl(''" + nos[1] +
			 * "?formCondition=" + formCondition + "&gridCondition=" +
			 * gridCondition + "'')\" style=\"font-size:18px; color:red;\">" +
			 * otherContent + "</a></br>"); List<String> sqlsdet =
			 * baseDao.beatchNotices(context.toString(), "b2b",
			 * nos[0].toString(), condition, nos[6].toString());
			 * sqlsdets.addAll(sqlsdet); } else {
			 * 
			 * } } else { Object[] nos = baseDao.getFieldsDataByCondition(
			 * "BTOB$NOTICEMAN left join purchase on manid=pu_id Left join BTOB$NOTICESET ON caller=bn_caller"
			 * ,
			 * "MANS,bn_url,BN_MAINID,bn_gridmainid,bn_title,pu_id,bn_formcaller"
			 * , "caller='PurchaseReply' and pu_code='" + reply.getPr_pucode() +
			 * "'"); if (nos != null && nos[0] != null) { String formCondition =
			 * nos[2] + "=" + nos[5]; String gridCondition = nos[3] + "=" +
			 * nos[5]; String otherContent = nos[4] + ":" +
			 * reply.getPr_pucode(); String condition = ""; if (nos[3] != null)
			 * { condition = "formCondition=" + nos[2] + "IS" + nos[5] +
			 * "&gridCondition=" + nos[3] + "IS" + nos[5]; } else { condition =
			 * "formCondition=" + nos[2] + "IS" + nos[5] + ""; } StringBuffer
			 * context = new StringBuffer(); context.append("采购回复&nbsp;[" +
			 * DateUtil.parseDateToString(DateUtil.parseStringToDate(null,
			 * Constant.YMD_HMS), "MM-dd HH:mm") + "]");
			 * context.append("<a href=\"javascript:openUrl(''" + nos[1] +
			 * "?formCondition=" + formCondition + "&gridCondition=" +
			 * gridCondition + "'')\" style=\"font-size:18px; color:red;\">" +
			 * otherContent + "</a></br>"); List<String> sqlsdet =
			 * baseDao.beatchNotices(context.toString(), "b2b",
			 * nos[0].toString(), condition, nos[6].toString());
			 * sqlsdets.addAll(sqlsdet); } else {
			 * 
			 * } } } } if (!sqlsdets.isEmpty()) { baseDao.execute(sqlsdets); } }
			 * catch (Exception e) {
			 * 
			 * }
			 */
			onReplySuccess(replies, master);
		}
	}

	/**
	 * 根据采购单号获取采购单
	 * 
	 * @param pu_code
	 * @return
	 */
	// private Purchase getPurchaseByCode(String pu_code) {
	// return
	// baseDao.getJdbcTemplate().queryForObject("select * from purchase where pu_code='"
	// + pu_code + "'",
	// new BeanPropertyRowMapper<Purchase>(Purchase.class));
	// }

	/**
	 * 上传主动回复的记录到平台
	 * 
	 * @return
	 */
	private boolean uploadPurchaseReply(Master master) {
		List<PurchaseReply> replies = getPurchaseReply();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(买家)采购单-上传主动回复", replies.size(), response));
				if (response.getStatusCode() == HttpStatus.OK.value())
					return uploadPurchaseReply(master);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的主动回复记录
	 * 
	 * @return
	 */
	private List<PurchaseReply> getPurchaseReply() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select * from (select pr_id,pr_qty,pr_delivery,pr_remark,pr_pucode,pr_pddetno,pr_date,pr_recorder,b2b_pr_id,pr_type from PurchaseReply left join PurchaseDetail on pr_pucode=pd_code and pr_pddetno=pd_detno left join purchase on pd_puid=pu_id left join vendor on pu_vendcode=ve_code where PR_SENDSTATUS='待上传' and ve_uu is not null and nvl(ve_b2benable,0)=1 and nvl(b2b_pr_id,0)=0 order by pr_date) where rownum <= 500",
							new BeanPropertyRowMapper<PurchaseReply>(PurchaseReply.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 主动回复记录成功传到平台之后
	 */
	private void onReplySuccess(List<PurchaseReply> replies) {
		StringBuffer idStr = new StringBuffer();
		for (PurchaseReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getPr_id());
		}
		baseDao.execute("update PurchaseReply set pr_sendstatus='已下载' where pr_id in (" + idStr.toString() + ")");
	}

	/**
	 * 从平台下载打印日志
	 * 
	 * @return
	 */
	private boolean downloadPrintLog(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/purchase/print?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<MessageLog> replies = FlexJsonUtil.fromJsonArray(data, MessageLog.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveMessageReply(replies, master);
						baseDao.save(new TaskLog("(买家)采购单-下载打印记录", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)采购单-下载打印记录", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void saveMessageReply(List<MessageLog> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (MessageLog reply : replies) {
				sqls.add("insert into MessageLog values(" + "messagelog_seq.nextval, to_date('"
						+ simpleDateFormat.format(reply.getMl_date()) + "', 'yyyy-mm-dd hh24:mi:ss'),'" + reply.getMl_man() + "','"
						+ reply.getMl_content() + "','" + reply.getMl_result() + "','" + reply.getMl_search() + "','" + reply.getCode()
						+ "')");
				String search = reply.getMl_search();
				String[] where = search.split("\\|");
				sqls.add("update " + where[0] + " set pu_printstatus = '已打印' where " + where[1]);
			}
			baseDao.execute(sqls);
			uploadPrintLogSuccess(replies, master);
		}
	}

	/**
	 * 修改平台里面的打印记录的状态
	 * 
	 * @return
	 */
	private boolean uploadPrintLogSuccess(List<MessageLog> replies, Master master) {
		StringBuffer codeStr = new StringBuffer();
		for (MessageLog reply : replies) {
			if (codeStr.length() > 0) {
				codeStr.append(",");
			}

			codeStr.append(reply.getCode());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", codeStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/print/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}