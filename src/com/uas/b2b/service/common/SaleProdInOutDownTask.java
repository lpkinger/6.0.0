package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.uas.erp.service.b2b.SaleDownChangeService;
import com.uas.erp.service.crm.CustomerService;

/**
 * 作为卖家ERP，获取客户各种出入库单到平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleProdInOutDownTask extends AbstractTask {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SaleDownChangeService saleDownChangeService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleProdInOut start");
		downloadSaleProdInOut(master);// 下载客户采购验收(已过账)
		logger.info(this.getClass() + " downloadSaleProdInOut end");
		logger.info(this.getClass() + " downloadNonPostingProdInOut start");
		downloadNonPostingProdInOut(master);// 下载客户采购验收(反过账)
		logger.info(this.getClass() + " downloadNonPostingProdInOut end");
		logger.info(this.getClass() + " downloadSaleProdReturn start");
		downloadSaleProdReturn(master);// 下载客户采购验退(已过账)
		logger.info(this.getClass() + " downloadSaleProdReturn end");
		logger.info(this.getClass() + " downloadNonPostingReturns start");
		downloadNonPostingReturns(master);// 下载客户采购验退(反过账)
		logger.info(this.getClass() + " downloadNonPostingReturns end");
		logger.info(this.getClass() + " downloadSaleProdBadIn start");
		downloadSaleProdBadIn(master);// 下载客户不良品入库单(已过账)
		logger.info(this.getClass() + " downloadSaleProdBadIn end");
		logger.info(this.getClass() + " downloadNonPostingProdBadIns start");
		downloadNonPostingProdBadIns(master);// 下载客户不良品入库单(反过账)
		logger.info(this.getClass() + " downloadNonPostingProdBadIns end");
		logger.info(this.getClass() + " downloadSaleProdBadOut start");
		downloadSaleProdBadOut(master);// 下载客户不良品出库单(已过账)
		logger.info(this.getClass() + " downloadSaleProdBadOut end");
		logger.info(this.getClass() + " downloadNonPostingProdBadOuts start");
		downloadNonPostingProdBadOuts(master);// 下载客户不良品出库单(反过账)
		logger.info(this.getClass() + " downloadNonPostingProdBadOuts end");
	}

	/**
	 * 从平台下载客户下达到平台的客户验收单
	 * 
	 * @return
	 */
	private boolean downloadSaleProdInOut(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/prodInOut?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdInOutDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户采购验收单", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户采购验收单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saleProdInOut
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
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户采购验收单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toSqlString(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toSqlString(sioId));// 插入明细记录
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
				sqls.add("update prodiodowndetail set (pd_prodcode) = (select sd_prodcode from  saledowndetail left join  saledown on sd_said = sa_id  where"
						+ " sa_code = pd_ordercode  and sd_detno = pd_orderdetno) where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// 消息推送
				Set<String> orderCodes = new HashSet<String>();
				for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
					for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
						if (!orderCodes.contains(detail.getPd_ordercode())) {
							// String sellerCode = getSellerCodeByOrderCodeAndUU(detail.getPd_ordercode(), prodInOutDown.getPi_customeruu());
							// String custName = getCustNameByCustomerUU(prodInOutDown.getPi_customeruu());
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户验收入库", detail.getPd_ordercode()
							// + " - " + custName, null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "物料已被客户验收入库  " + detail.getPd_ordercode()
							// + " - " + custName);
							orderCodes.add(detail.getPd_ordercode());
						}
					}
				}
				onSaleProdInOutDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的客户采购验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleProdInOutDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/prodInOut?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的验收单
	 * 
	 * @return
	 */
	private boolean downloadNonPostingProdInOut(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodInOut/nonPosting?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdInOutDown(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账采购验收单", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账采购验收单", 0, response));
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
					+ "where pi_class='客户采购验收单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户采购验收单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdInOutDownSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的采购验收单之后，修改平台的客户采购验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdInOutDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodInOut/nonPosting?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的采购验退
	 * 
	 * @return
	 */
	private boolean downloadSaleProdReturn(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/prodReturn?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdReturnDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户采购验退单", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户采购验退单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存采购验退单
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
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户采购验退单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toReturnSql(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toReturnSql(sioId));// 插入明细记录
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
				sqls.add("update prodiodowndetail set (pd_prodcode) = (select sd_prodcode from  saledowndetail left join  saledown on sd_said = sa_id  where"
						+ " sa_code = pd_ordercode  and sd_detno = pd_orderdetno) where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// 消息推送
				Set<String> orderCodes = new HashSet<String>();
				for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
					for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
						if (!orderCodes.contains(detail.getPd_ordercode())) {
							// String sellerCode = getSellerCodeByOrderCodeAndUU(detail.getPd_ordercode(), prodInOutDown.getPi_customeruu());
							// String custName = getCustNameByCustomerUU(prodInOutDown.getPi_customeruu());
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户采购验退", detail.getPd_ordercode()
							// + " - " + custName, null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "物料已被客户采购验退  " + detail.getPd_ordercode()
							// + " - " + custName);
							orderCodes.add(detail.getPd_ordercode());
						}
					}
				}
				onSaleProdReturnDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到saledownchange之后，修改平台的采购验退单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleProdReturnDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/prodReturn?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
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
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodReturn/nonPosting?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdReturn(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账采购验退单", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账采购验退单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理反过账的采购验退单单据
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveNonPostingProdReturn(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete prodiodowndetail where PD_PIID in (select pi_id from prodiodown "
					+ "where pi_class='客户采购验退单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户采购验退单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdReturnSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的采购验收单之后，修改平台的客户采购验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdReturnSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodReturn/nonPosting?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的不良品入库
	 * 
	 * @return
	 */
	private boolean downloadSaleProdBadIn(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/prodBadIn?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdBadInDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户不良品入库", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户不良品入库", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存不良品入库单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleProdBadInDown(List<SaleProdInOutDown> prodInOutDowns, Master master) {
		if (!CollectionUtil.isEmpty(prodInOutDowns)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户不良品入库单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toBadInSql(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toBadInSql(sioId));// 插入明细记录
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
				sqls.add("update prodiodowndetail set (pd_prodcode) = (select sd_prodcode from  saledowndetail left join  saledown on sd_said = sa_id  where"
						+ " sa_code = pd_ordercode  and sd_detno = pd_orderdetno) where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// 消息推送
				Set<String> orderCodes = new HashSet<String>();
				for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
					for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
						// 每个单发一条
						if (!orderCodes.contains(detail.getPd_ordercode())) {
							// String sellerCode = getSellerCodeByOrderCodeAndUU(detail.getPd_ordercode(), prodInOutDown.getPi_customeruu());
							// String custName = getCustNameByCustomerUU(prodInOutDown.getPi_customeruu());
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户不良品入库", detail.getPd_ordercode()
							// + " - " + custName, null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "物料已被客户不良品入库  " + detail.getPd_ordercode()
							// + " - " + custName);
							orderCodes.add(detail.getPd_ordercode());
						}
					}
				}
				onSaleProdBadInDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的不良品入库单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleProdBadInDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/prodBadIn?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的不良品入库单
	 * 
	 * @return
	 */
	private boolean downloadNonPostingProdBadIns(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodBadIn/nonPosting?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdBadIns(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账不良品入库", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账不良品入库", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理反过账的不良品入库单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveNonPostingProdBadIns(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete prodiodowndetail where PD_PIID in (select pi_id from prodiodown "
					+ "where pi_class='客户不良品入库单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户不良品入库单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdBadInsSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的不良品入库单之后，修改平台的客户不良品入库单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdBadInsSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodBadIn/nonPosting?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的不良品出库
	 * 
	 * @return
	 */
	private boolean downloadSaleProdBadOut(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/prodBadOut?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleProdInOutDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, SaleProdInOutDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveSaleProdBadOutDown(prodInOutDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户不良品出库", prodInOutDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户不良品出库", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存不良品出库单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleProdBadOutDown(List<SaleProdInOutDown> prodInOutDowns, Master master) {
		if (!CollectionUtil.isEmpty(prodInOutDowns)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
				int count = baseDao.getCount("select count(1) from prodiodown where pi_class='客户不良品出库单' and pi_b2bid="
						+ prodInOutDown.getPi_b2bid());
				if (count == 0) {
					int sioId = baseDao.getSeqId("prodiodown_SEQ");// 获取主键序列
					sqls.add(prodInOutDown.toBadOutSql(sioId));// 插入主记录
					if (!CollectionUtil.isEmpty(prodInOutDown.getDetails())) {
						for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
							sqls.add(detail.toBadOutSql(sioId));// 插入明细记录
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
				sqls.add("update prodiodowndetail set (pd_prodcode) = (select sd_prodcode from  saledowndetail left join  saledown on sd_said = sa_id  where"
						+ " sa_code = pd_ordercode  and sd_detno = pd_orderdetno) where pd_piid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// 消息推送
				Set<String> orderCodes = new HashSet<String>();
				for (SaleProdInOutDown prodInOutDown : prodInOutDowns) {
					for (SaleProdInOutDownDetail detail : prodInOutDown.getDetails()) {
						// 每个单发一条
						if (!orderCodes.contains(detail.getPd_ordercode())) {
							// String sellerCode = getSellerCodeByOrderCodeAndUU(detail.getPd_ordercode(), prodInOutDown.getPi_customeruu());
							// String custName = getCustNameByCustomerUU(prodInOutDown.getPi_customeruu());
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户不良品出库", detail.getPd_ordercode()
							// + " - " + custName, null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "物料已被客户不良品出库  " + detail.getPd_ordercode()
							// + " - " + custName);
							orderCodes.add(detail.getPd_ordercode());
						}
					}
				}
				onSaleProdBadOutDownSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的不良品出库单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleProdBadOutDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/prodBadOut?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的不良品出库单
	 * 
	 * @return
	 */
	private boolean downloadNonPostingProdBadOuts(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodBadOut/nonPosting?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingProdBadOuts(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账不良品出库", 0, response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载客户反过账不良品出库", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理反过账的客户不良品出库单单据
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveNonPostingProdBadOuts(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete prodiodowndetail where PD_PIID in (select pi_id from prodiodown "
					+ "where pi_class='客户不良品出库单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete prodiodown where pi_class='客户不良品出库单' and pi_status='已过账' and PI_B2BID in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingProdBadOutsSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的客户不良品出库单之后，修改平台的客户不良品出库单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingProdBadOutsSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/prodBadOut/nonPosting?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
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
	// sellerCode = baseDao.getFieldValue(" saledown left join customer on sa_custid = cu_id left join employee on sa_sellerid = em_id ",
	// "em_code", "cu_uu = " + customeruu + " and sa_pocode = '" + orderCode + "'", String.class);
	// if (sellerCode == null || sellerCode.equals("")) {// 根据单据获取不到
	// sellerCode = baseDao.getFieldValue(" customer left join employee on cu_sellerid = em_id ", "em_code", "cu_uu = " + customeruu,
	// String.class);
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
