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

import com.uas.b2b.model.SaleDown;
import com.uas.b2b.model.SaleDownDetail;
import com.uas.b2b.model.SaleDownDetailEnd;
import com.uas.b2b.model.SaleReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.model.Master;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为卖家ERP，获取客户下达到平台的采购订单、将回复记录上传到平台
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleDownTask extends AbstractTask {

	@Autowired
	private PagingReleaseService pagingReleaseService;

	/**
	 * 最大允许的明细条数
	 */
	private static final int max_size = 2000;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleDown start");
		downloadSaleDown(master);
		logger.info(this.getClass() + " downloadSaleDown end");
		logger.info(this.getClass() + " downloadReply start");
		downloadReply(master);
		logger.info(this.getClass() + " downloadReply end");
		logger.info(this.getClass() + " uploadSaleReply start");
		uploadSaleReply(master);
		logger.info(this.getClass() + " uploadSaleReply end");
		logger.info(this.getClass() + " downloadSaleDownEnd start");
		downloadSaleDownEnd(master);
		logger.info(this.getClass() + " downloadSaleDownEnd end");
	}

	/**
	 * 从平台下载客户下达到平台的采购订单
	 * 
	 * @return
	 */
	private boolean downloadSaleDown(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDown> sales = FlexJsonUtil.fromJsonArray(data, SaleDown.class);
					if (!CollectionUtil.isEmpty(sales)) {
						saveSaleDown(sales, master);
						baseDao.save(new TaskLog("(卖家)客户采购-下载客户采购单", sales.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购-下载客户采购单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saledown
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleDown(List<SaleDown> sales, Master master) {
		if (!CollectionUtil.isEmpty(sales)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_pu_id
			
			List<Long> idList = new ArrayList<Long>();
			for (SaleDown sale : sales) {
				int count = baseDao.getCount("select count(1) from SaleDown where sa_type='purchase' and b2b_pu_id=" + sale.getB2b_pu_id());
				if (count == 0) {
					int saId = baseDao.getSeqId("SaleDown_SEQ");
					Float rate = baseDao.queryForObject("select cm_crrate from currencysmonth left join Currencys on cr_name=cm_crname where cm_yearmonth = ? and cm_crname = ? and nvl(cr_statuscode,' ')='CANUSE'", Float.class, DateUtil.getYearmonth(sale.getSa_date()),sale.getSa_currency());
					sale.setSa_rate(rate);
					sqls.add(sale.toSqlString(saId));
					idList.add(Long.valueOf(saId));
					if (!CollectionUtil.isEmpty(sale.getSaleDownDetails())) {
						for (SaleDownDetail detail : sale.getSaleDownDetails()) {
							sqls.add(detail.toSqlString(saId));
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(saId);
					b2bStr.append(sale.getB2b_pu_id());
					if (sqls.size() > max_size) {// 阶段保存
						baseDao.execute(sqls);
						sqls = new ArrayList<String>();
					}
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update SaleDown set (sa_custid,sa_custcode,sa_custname,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_sellerid,sa_seller,sa_sellercode)=(select cu_id,cu_code,cu_name,cu_arcode,cu_arname,cu_shcustcode,cu_shcustname,cu_sellerid,cu_sellername,em_code from customer left join employee on em_id=cu_sellerid where cu_uu=sa_customeruu) where sa_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownDetail set (sd_prodid,sd_prodcode,sd_custproddetail,sd_prodcustcode)=(select  pc_prodid,pc_prodcode,pc_custproddetail,pc_custprodspec from productcustomer where pc_custcode=(select sa_custcode from SaleDown where sd_said=sa_id)  and pc_custprodcode=sd_custprodcode and nvl(pc_custprodspec,' ')=nvl(sd_custprodspec,' ')) where sd_said in ("
						+ idStr.toString() + ")");
				// 更新客户采购订单明细中的物料，客户物料编号与我司编码一致的料号
				sqls.add("update SaleDownDetail set (sd_prodid,sd_prodcode)=(select pr_id,pr_code from product where pr_code=sd_custprodcode) where nvl(sd_prodcode,' ')=' ' and sd_said in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				onSaleDownSuccess(b2bStr.toString(), master);
			}
			if (idList.size() > 0) { // 产生消息
				List<Long> ids = new ArrayList<Long>();
				for (Long quId : idList) {
					if (!ids.contains(quId)) { // 去重
						pagingReleaseService.B2BMsg("Sale!download", String.valueOf(quId), "save");
						ids.add(quId);
					}
				}
			}
		}
	}

	/**
	 * 成功写到saledown之后，修改平台的采购订单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale?access_id=" + master.getMa_uu(), params,
					true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传SaleDown回复记录到平台
	 * 
	 * @return
	 */
	private boolean uploadSaleReply(Master master) {
		List<SaleReply> replies = getSaleReply();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/reply?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(卖家)客户采购-上传回复", replies.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的回复记录
	 * 
	 * @return
	 */
	private List<SaleReply> getSaleReply() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select SaleReply.*,SaleDown.sa_customeruu cu_uu from SaleReply left join SaleDown on sr_sacode=sa_code where SR_SENDSTATUS='待上传' and sa_type='purchase' and sa_customeruu is not null",
							new BeanPropertyRowMapper<SaleReply>(SaleReply.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 回复记录成功传到平台之后
	 */
	private void onReplySuccess(List<SaleReply> replies) {
		StringBuffer idStr = new StringBuffer();
		for (SaleReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getSr_id());
		}
		baseDao.execute("update SaleReply set sr_sendstatus='已下载' where sr_id in (" + idStr.toString() + ")");
	}

	/**
	 * 直接在平台回复的记录，传到卖家ERP的SaleReply
	 */
	private boolean downloadReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/reply?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleReply> replies = FlexJsonUtil.fromJsonArray(data, SaleReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveReply(replies, master);
						baseDao.save(new TaskLog("(卖家)客户采购-下载回复", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的回复记录
	 */
	private void saveReply(List<SaleReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			for (SaleReply reply : replies) {
				sqls.add("MERGE into SaleDownDetail a using (select count(1) c from SaleReply where b2b_pr_id=" + reply.getB2b_pr_id()
						+ ") b on (b.c=0) when MATCHED THEN update set sd_replyqty = nvl(sd_replyqty, 0) + " + reply.getSr_qty()
						+ ", sd_replydate = " + DateUtil.parseDateToOracleString(null, reply.getSr_delivery()) + ", sd_replydetail='"
						+ StringUtil.nvl(reply.getSr_remark(), "") + "' where b2b_pd_id=" + reply.getB2b_pd_id() + " and sd_code='"
						+ reply.getSr_sacode() + "' and sd_detno=" + reply.getSr_sddetno());
				sqls.add("MERGE into SaleReply a using (select count(1) c from SaleReply where b2b_pr_id="
						+ reply.getB2b_pr_id()
						+ ") b on (b.c>0) when NOT MATCHED THEN insert (b2b_pr_id,sr_qty,sr_delivery,sr_remark,sr_sacode,sr_sddetno,sr_date,sr_recorder,sr_type,sr_sendstatus) values ("
						+ reply.getB2b_pr_id() + "," + +reply.getSr_qty() + ","
						+ DateUtil.parseDateToOracleString(null, reply.getSr_delivery()) + ",'" + StringUtil.nvl(reply.getSr_remark(), "")
						+ "','" + reply.getSr_sacode() + "'," + reply.getSr_sddetno() + ","
						+ DateUtil.parseDateToOracleString(null, reply.getSr_date()) + ",'" + reply.getSr_recorder() + "','"
						+ StringUtil.nvl(reply.getSr_type(), "") + "','已下载')");
			}
			baseDao.execute(sqls);
			onDownloadReplySuccess(replies, master);
		}
	}

	/**
	 * 将从平台下载的回复记录保存成功之后，回执给平台
	 * 
	 * @param replies
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplySuccess(List<SaleReply> replies, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (SaleReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getB2b_pr_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/reply/back?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的结案、反结案采购订单
	 * 
	 * @return
	 */
	private boolean downloadSaleDownEnd(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/end?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDownDetailEnd> ends = FlexJsonUtil.fromJsonArray(data, SaleDownDetailEnd.class);
					if (!CollectionUtil.isEmpty(ends)) {
						saveSaleDownEnd(ends, master);
						baseDao.save(new TaskLog("(卖家)客户采购-下载结案、反结案状态", ends.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购-下载结案、反结案状态", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 结案、反结案saledown
	 * 
	 * @param ends
	 * @param enterprise
	 */
	private void saveSaleDownEnd(List<SaleDownDetailEnd> ends, Master master) {
		if (!CollectionUtil.isEmpty(ends)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bStr = new StringBuffer();// b2b_pd_id
			Set<Long> idSet = new HashSet<Long>();
			for (SaleDownDetailEnd end : ends) {
				Status status = end.isEnded() ? Status.FINISH : Status.AUDITED;
				sqls.add("update SaleDownDetail set sd_mrpstatus='" + status.display() + "',sd_mrpstatuscode='" + status.code()
						+ "' where b2b_pd_id=" + end.getB2b_pd_id() + " and sd_code='" + end.getSd_code() + "' and sd_detno="
						+ end.getSd_detno());
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(end.getB2b_pd_id());
				// 这里sd_code = sa_code
				// List<SaleDown> saleDowns = baseDao.getJdbcTemplate().query(
				// "select SaleDown.* from SaleDown where sa_code = '" +
				// end.getSd_code() + "'",
				// new BeanPropertyRowMapper<SaleDown>(SaleDown.class));
				List<SaleDown> saleDowns = new ArrayList<SaleDown>();
				try {
					saleDowns = baseDao.getJdbcTemplate().query(
							"select SaleDown.* from SaleDown where sa_code = '" + end.getSd_code() + "'",
							new BeanPropertyRowMapper<SaleDown>(SaleDown.class));
				} catch (EmptyResultDataAccessException e) {

				}
				// 根据操作产生消息
				if (saleDowns.size() > 0) {
					SaleDown saleDown = saleDowns.get(0);
					if (!idSet.contains(saleDown.getSa_id())) {
						// 结案产生消息
						// Float replyQty =
						// baseDao.getFieldValue("saledowndetail",
						// "sd_replyqty",
						// "b2b_pd_id=" + end.getB2b_pd_id() + " and sd_code='"
						// + end.getSd_code()
						// + "' and sd_detno=" + end.getSd_detno(),
						// Float.class);
						// Float qty = baseDao
						// .getFieldValue("saledowndetail",
						// "sd_qty",
						// "b2b_pd_id=" + end.getB2b_pd_id() + " and sd_code='"
						// + end.getSd_code()
						// + "' and sd_detno=" + end.getSd_detno(),
						// Float.class);
						if (/* replyQty < qty && */status.equals(Status.FINISH)) {
							pagingReleaseService.B2BMsg("Sale!download", String.valueOf(saleDown.getSa_id()), "end");
						} else if (/* replyQty < qty && */status.equals(Status.AUDITED)) {
							pagingReleaseService.B2BMsg("Sale!download", String.valueOf(saleDown.getSa_id()), "resend");
						}
						idSet.add(saleDown.getSa_id());
					}

				}
			}
			baseDao.execute(sqls);
			onSaleDownEndSuccess(b2bStr.toString(), master);
		}
	}

	/**
	 * 将从平台下载的结案、反结案采购单操作完成之后，回执给平台
	 * 
	 * @param idStr
	 * @param enterprise
	 * @return
	 */
	private boolean onSaleDownEndSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/end/back?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	public String getSellerCodeByB2BPuid(Long b2bPuid) {
		Long said = baseDao.getFieldValue("saledowndetail", "sd_said", "b2b_pd_id=" + b2bPuid, Long.class);
		String sellerCode = baseDao.getFieldValue("saledown", "sa_sellercode", "sa_id=" + said, String.class);
		return sellerCode;
	}

}
