package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.SaleAPBill;
import com.uas.b2b.model.SaleAPBillDetail;
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
 * 作为卖家ERP，获取客户的应付票据
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleAPBillDownTask extends AbstractTask {

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
		logger.info(this.getClass() + " downloadSaleAPBill start");
		downloadSaleAPBill(master);// 下载客户应付票据
		logger.info(this.getClass() + " downloadSaleAPBill end");
		logger.info(this.getClass() + " downloadNonPostingApBills start");
		downloadNonPostingApBills(master);// 下载客户采购验收(反过账)
		logger.info(this.getClass() + " downloadNonPostingApBills end");
	}

	/**
	 * 从平台下载客户下达到平台的发票
	 * 
	 * @return
	 */
	private boolean downloadSaleAPBill(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/APBill?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleAPBill> saleAPBills = FlexJsonUtil.fromJsonArray(data, SaleAPBill.class);
					if (!CollectionUtil.isEmpty(saleAPBills)) {
						saveSaleAPBills(saleAPBills, master);
						baseDao.save(new TaskLog("(卖家)客户发票-下载客户应付发票", saleAPBills.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户发票-下载客户应付发票", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saledownchange
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleAPBills(List<SaleAPBill> saleAPBills, Master master) {
		if (!CollectionUtil.isEmpty(saleAPBills)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleAPBill saleAPBill : saleAPBills) {
				int count = baseDao.getCount("select count(1) from apbilldown where ab_b2bid = " + saleAPBill.getAb_b2bid());
				if (count == 0) {
					int id = baseDao.getSeqId("apbilldown_seq");// 获取主键序列
					sqls.add(saleAPBill.toSqlString(id));// 插入主记录
					if (!CollectionUtil.isEmpty(saleAPBill.getDetails())) {
						for (SaleAPBillDetail apBillDetail : saleAPBill.getDetails()) {
							sqls.add(apBillDetail.toSqlString(id));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(id);
					b2bIdStr.append(saleAPBill.getAb_b2bid());// b2bid用于回传到平台修改下载状态
				}
			}
			if (idStr.length() > 0) {
				// 完善明细
				sqls.add("update apbilldowndetail set (abd_prodcode) = (select pc_prodcode from productcustomer "
						+ "where pc_custprodcode = apbilldowndetail.abd_custprodcode and "
						+ "pc_custproddetail = apbilldowndetail.abd_custproddetail and "
						+ "pc_custprodspec = apbilldowndetail.abd_custprodspec)  where abd_abid in (" + idStr.toString() + ")");
				baseDao.execute(sqls);
				// 消息推送
				// 每一张应付票据推送一条
				// for (SaleAPBill saleAPBill : saleAPBills) {
				// String sellerCode = getSellerCodeByOrderCodeAndUU("", saleAPBill.getAb_customeruu());
				// String custName = getCustNameByCustomerUU(saleAPBill.getAb_customeruu());
				// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "收到一张客户应付票据", "客户：" + custName, null);
				// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "收到一张客户应付票据  | " + "客户：" + custName);
				// }
				onSaleAPBillSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到apbilldown之后，修改平台的客户应付票据为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleAPBillSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/APBill?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户已反过账的客户应发发票
	 * 
	 * @return
	 */
	private boolean downloadNonPostingApBills(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/APBill/nonPosting?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Long> b2bIds = FlexJsonUtil.fromJsonArray(data, Long.class);
					if (!CollectionUtil.isEmpty(b2bIds)) {
						String b2bIdStr = StringUtils.collectionToDelimitedString(b2bIds, ",");
						saveNonPostingApBillsDown(b2bIdStr, master);
						baseDao.save(new TaskLog("(卖家)客户应付发票-下载客户反过账应付发票", b2bIds.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户应付发票-下载客户反过账应付发票", 0, response));
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
	private void saveNonPostingApBillsDown(String b2bIdStr, Master master) {
		if (StringUtils.hasText(b2bIdStr)) {
			List<String> sqls = new ArrayList<String>();
			// 删除明细记录
			sqls.add("delete apbilldowndetail where abd_abid in (select ab_id from apbilldown where ab_b2bid in (" + b2bIdStr + ")) ");
			// 删除主记录
			sqls.add("delete apbilldown where ab_b2bid in (" + b2bIdStr + ")");
			baseDao.execute(sqls);
			onNonPostingApBillDownSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功处理反过账的采购验收单之后，修改平台的客户采购验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onNonPostingApBillDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/APBill/nonPosting?access_id=" + master.getMa_uu(), params, true,
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
	// // 先根据单据获取单据业务员
	// sellerCode = baseDao.getFieldValue(" saledown left join customer on sa_custid = cu_id left join employee on sa_sellerid = em_id ",
	// "em_code", "cu_uu = " + customeruu + " and sa_pocode = '" + orderCode + "'", String.class);
	// if (sellerCode == null || sellerCode.equals("")) {// 根据单据获取不到，再根据客户表获取对应的客户业务员
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
