package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.APCheck;
import com.uas.b2b.model.APCheckDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

/**
 * 作为买家ERP，下载应付对账单
 * 
 * @author aof
 * @date 2015年12月7日
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleAPCheckTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleARCheck start");
		downloadSaleARCheck(master); // 下载应付对账单
		logger.info(this.getClass() + " downloadSaleARCheck end");
		logger.info(this.getClass() + " uploadARCheckReply start");
		uploadARCheckReply(master); // 上传回复账单
		logger.info(this.getClass() + " uploadARCheckReply end");
	}

	/**
	 * 上传ERP不同意/已确认的对账单
	 * 
	 * @return
	 */
	private List<APCheck> getAPCheckReply() {
		try {
			List<APCheck> apChecks = baseDao
					.getJdbcTemplate()
					.query("select apcheck.* from apcheck where ac_confirmstatus in ('已确认','不同意') and  ac_b2bid is not null and ac_sendstatus = '待上传'",
							new BeanPropertyRowMapper<APCheck>(APCheck.class));
			for (APCheck aPCheck : apChecks) {
				List<APCheckDetail> details = baseDao.getJdbcTemplate().query("select * from apcheckdetail where ad_acid = ?",
						new BeanPropertyRowMapper<APCheckDetail>(APCheckDetail.class), aPCheck.getAc_id());
				aPCheck.setDetails(details);
			}
			return apChecks;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 从平台下载客户下达到平台的应付对账单
	 * 
	 * @return
	 */
	private boolean uploadARCheckReply(Master master) {
		// if(enterprise !=null && master != null && master.getMa_id() != 4){
		List<APCheck> replies = getAPCheckReply();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/APCheck/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(买家家)已回复应付-上传在应付对账单", replies.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		// }
		return true;
	}

	/**
	 * 客户回复记录成功传到平台之后
	 */
	private void onReplySuccess(List<APCheck> replies) {
		StringBuffer idStr = new StringBuffer();
		for (APCheck reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getAc_b2bid());
		}
		baseDao.execute("update apcheck set ac_sendstatus='已上传' where ac_b2bid in (" + idStr.toString() + ")");
	}

	/**
	 * 从平台下载客户下达到平台的应付对账单
	 * 
	 * @return
	 */
	private boolean downloadSaleARCheck(Master master) {
		// if(enterprise !=null && master != null && master.getMa_id() != 4){
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/APCheck?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<APCheck> APChecks = FlexJsonUtil.fromJsonArray(data, APCheck.class);
					if (!CollectionUtil.isEmpty(APChecks)) {
						saveSaleAPChecks(APChecks, master);
						baseDao.save(new TaskLog("(买家)客户应付对账-下载客户应付对账", APChecks.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户发票-下载客户应付发票", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// }
		return true;
	}

	/**
	 * 保存saveSaleAPChecks
	 * 
	 * @param APChecks
	 * @param enterprise
	 */
	private void saveSaleAPChecks(List<APCheck> APChecks, Master master) {
		if (!CollectionUtil.isEmpty(APChecks)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (APCheck aPCheck : APChecks) {
				int count = baseDao.getCount("select count(1) from APCheck where ac_b2bid = " + aPCheck.getAc_b2bid());
				if (count == 0) {
					int id = baseDao.getSeqId("APCheck_seq");// 获取主键序列
					sqls.add(aPCheck.toSqlString(id));// 插入主记录
					if (!CollectionUtil.isEmpty(aPCheck.getDetails())) {
						for (APCheckDetail aPCheckDetail : aPCheck.getDetails()) {
							String orderClass = null;
							if (aPCheckDetail.getAd_orderclass().equals("货款调账")) {
								orderClass = "APBILL";
							} else {
								orderClass = "PRODINOUT";
							}
							sqls.add(aPCheckDetail.toSqlString(id, orderClass));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(id);
					b2bIdStr.append(aPCheck.getAc_b2bid());// b2bid用于回传到平台修改下载状态
				}
			}
			if (idStr.length() > 0) {
				// 完善明细
				sqls.add("update ApcheckDetail set (ad_pdid,ad_taxrate) = (select pd_id,pd_taxrate from prodinout left join prodiodetail on pi_id=pd_piid  "
								+ " where pi_inoutno=ad_sourcecode and pd_pdno = ad_sourcedetno) where nvl(ad_sourcetype, ' ') = 'PRODINOUT' and ad_acid in ("
								+ idStr.toString() + ")");
				// 完善主记录
				sqls.add("update apcheck set (ac_vendcode,ac_vendname,ac_buyercode,ac_buyername,ac_paymentcode,ac_paymentname)=(select ve_code,ve_name,ve_buyercode,ve_buyername,ve_paymentcode,ve_payment from vendor where ac_venduu=ve_uu) where ac_id in ("
						+ idStr.toString() + ")");
				try {
					baseDao.execute(sqls);
					onSaleAPCheckSuccess(b2bIdStr.toString(), master);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 成功写到APCheck之后，修改平台的客户应收票据为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleAPCheckSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/APCheck?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}