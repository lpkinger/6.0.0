package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.SaleProdInOutDown;
import com.uas.b2b.model.SaleProdInOutDownDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

/**
 * 作为卖家ERP，获取客户各种出入库单到平台
 * 
 * @author aof
 * @date 2015年8月28日
 */
@Component
@EnableAsync
@EnableScheduling
public class OutSourceProdInOutDownTask extends AbstractTask {

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadOutSourceProdInout start");
		downloadOutSourceProdInout(master);// 下载客户委外验收单(已过账)
		logger.info(this.getClass() + " downloadOutSourceProdInout end");
		logger.info(this.getClass() + " downloadNonPostingProdInOut start");
		downloadNonPostingProdInOut(master);// 下载客户委外验收(反过账)
		logger.info(this.getClass() + " downloadNonPostingProdInOut end");
		logger.info(this.getClass() + " downloadOutSourceProdReturn start");
		downloadOutSourceProdReturn(master);// 下载客户委外验退(已过账)
		logger.info(this.getClass() + " downloadOutSourceProdReturn end");
		logger.info(this.getClass() + " downloadNonPostingReturns start");
		downloadNonPostingReturns(master);// 下载客户委外验退(反过账)
		logger.info(this.getClass() + " downloadNonPostingReturns end");
	}

	/**
	 * 从平台下载客户下达到平台的委外验收单
	 * 
	 * @return
	 */
	private boolean downloadOutSourceProdInout(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/outSource/prodInOut?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdInOutDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户委外验收单", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户委外验收单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saleProdInOut(委外)
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleProdInOutDown(List<SaleProdInOutDown> prodInOutDowns, Master master) {
		if (!CollectionUtil.isEmpty(prodInOutDowns)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户委外验收单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toSqlOutSource(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toSqlOutSource(sioId));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(sioId);
					b2bIdStr.append(prodInOutDown.getPi_b2bid());// b2bid用于回传到平台修改下载状态
				}
			}
			if (idStr.length() > 0) {
				// 完善主记录
				sqls.add("update prodiodown set (pi_cardcode, pi_title) = (select cu_code, cu_name from customer where cu_uu = pi_customeruu) where pi_id in ("
						+ idStr.toString() + ")");
				// 完善明细记录
				sqls.add("update prodiodowndetail pd1 set (pd_prodcode) = "
						+ " (select max(sd_prodcode) from prodiodowndetail pd2, saledown, saledowndetail where"
						+ " sa_code = pd_ordercode and sd_said = sa_id and sd_detno = pd_orderdetno and pd2.pd_piid = pd1.pd_piid)"
						+ " where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				outSourceProdInOutDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的客户委外验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean outSourceProdInOutDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/outSource/prodInOut?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的委外验收单
	 * 
	 * @return
	 */
	private boolean downloadNonPostingProdInOut(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outSource/prodInOut/nonPosting?access_id="
					+ master.getMa_uu(), null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdInOutDown(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账委外验收单", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账委外验收单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理反过账的单据
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveNonPostingProdInOutDown(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete prodiodowndetail where PD_PIID in (select pi_id from prodiodown "
					+ "where pi_class='客户委外验收单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户委外验收单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdInOutDownSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的委外验收单之后，修改平台的客户委外验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdInOutDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/outSource/prodInOut/nonPosting?access_id="
					+ master.getMa_uu(), params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的委外验退
	 * 
	 * @return
	 */
	private boolean downloadOutSourceProdReturn(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/outSource/prodReturn?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdReturnDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户委外验退单", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户委外验退单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存委外验退单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleProdReturnDown(List<SaleProdInOutDown> prodInOutDowns, Master master) {
		if (!CollectionUtil.isEmpty(prodInOutDowns)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户委外验退单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toReturnSqlOutSource(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toReturnSqlOutSource(sioId));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(sioId);
					b2bIdStr.append(prodInOutDown.getPi_b2bid());// b2bid用于回传到平台修改下载状态
				}
			}
			if (idStr.length() > 0) {
				// 完善主记录
				sqls.add("update prodiodown set (pi_cardcode, pi_title) = (select cu_code, cu_name from customer where cu_uu = pi_customeruu) where pi_id in ("
						+ idStr.toString() + ")");
				// 完善明细记录
				sqls.add("update prodiodowndetail pd1 set (pd_prodcode) = "
						+ " (select max(sd_prodcode) from prodiodowndetail pd2, saledown, saledowndetail where"
						+ " sa_code = pd_ordercode and sd_said = sa_id and sd_detno = pd_orderdetno and pd2.pd_piid = pd1.pd_piid)"
						+ " where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// // 消息推送
				// Set<String> orderCodes = new HashSet<String>();
				// for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
				// for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
				// if (!orderCodes.contains(detail.getPd_ordercode())) {
				// String sellerCode = getSellerCodeByOrderCodeAndUU(detail.getPd_ordercode(),
				// prodInOutDown.getPi_customeruu());
				// String custName = getCustNameByCustomerUU(prodInOutDown.getPi_customeruu());
				// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户委外验退",
				// detail.getPd_ordercode() + " - " + custName, null, null, null);
				// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
				// "物料已被客户委外验退  " + detail.getPd_ordercode() + " - " + custName, null, null, null, null);
				// orderCodes.add(detail.getPd_ordercode());
				// }
				// }
				// }
				outSourceProdReturnDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到saledownchange之后，修改平台的委外验退单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean outSourceProdReturnDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/outSource/prodReturn?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的客户验退单
	 * 
	 * @return
	 */
	private boolean downloadNonPostingReturns(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outSource/prodReturn/nonPosting?access_id="
					+ master.getMa_uu(), null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdReturn(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账委外验退单", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账委外验退单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理反过账的委外验退单单据
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveNonPostingProdReturn(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete prodiodowndetail where PD_PIID in (select pi_id from prodiodown "
					+ "where pi_class='客户委外验退单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户委外验退单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdReturnSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的委外验收单之后，修改平台的客户委外验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdReturnSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/outSource/prodReturn/nonPosting?access_id="
					+ master.getMa_uu(), params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据单据编号和客户UU号获取对应销售人员编号
	 * 
	 * @param orderCode
	 * @param customeruu
	 * @return
	 */
	// private String getSellerCodeByOrderCodeAndUU(String orderCode, Long customeruu) {
	// String sellerCode = null;
	// sellerCode = baseDao.getFieldValue(
	// " saledown left join customer on sa_custid = cu_id left join employee on sa_sellerid = em_id ",
	// "em_code", "cu_uu = " + customeruu + " and sa_pocode = '" + orderCode + "'", String.class);
	// if (sellerCode == null || sellerCode.equals("")) {// 根据单据获取不到
	// sellerCode = baseDao.getFieldValue(" customer left join employee on cu_sellerid = em_id ", "em_code",
	// "cu_uu = " + customeruu, String.class);
	// }
	// return sellerCode;
	// }

	/**
	 * 根据客户uu号获取客户名称
	 * 
	 * @param customeruu
	 * @return
	 */
	// private String getCustNameByCustomerUU(Long customeruu) {
	// String custName = null;
	// custName = baseDao.getFieldValue("customer", "cu_name", "cu_uu = " + customeruu, String.class);
	// return custName;
	// }

}
