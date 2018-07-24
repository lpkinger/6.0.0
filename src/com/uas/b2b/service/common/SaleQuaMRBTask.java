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

import com.uas.b2b.model.SaleQuaMRB;
import com.uas.b2b.model.SaleQuaMRBCheckItem;
import com.uas.b2b.model.SaleQuaMRBProjectItem;
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
 * 作为卖家ERP，获取客户的MRB
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleQuaMRBTask extends AbstractTask {

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
		logger.info(this.getClass() + " downloadSaleQuaMRB start");
		downloadSaleQuaMRB(master);// 下载客户MRB
		logger.info(this.getClass() + " downloadSaleQuaMRB end");
	}

	/**
	 * 从平台下载客户下达到平台的MRB
	 * 
	 * @return
	 */
	private boolean downloadSaleQuaMRB(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/MRB?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleQuaMRB> saleQuaMRBs = FlexJsonUtil.fromJsonArray(data, SaleQuaMRB.class);
					if (!CollectionUtil.isEmpty(saleQuaMRBs)) {
						saveSaleQuaMRB(saleQuaMRBs, master);
						baseDao.save(new TaskLog("(卖家)客户MRB-下载客户MRB", saleQuaMRBs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户MRB-下载客户MRB", 0, response));
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
	private void saveSaleQuaMRB(List<SaleQuaMRB> saleQuaMRBs, Master master) {
		if (!CollectionUtil.isEmpty(saleQuaMRBs)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (SaleQuaMRB saleQuaMRB : saleQuaMRBs) {
				int count = baseDao.getCount("select count(1) from qua_mrbdown where mr_b2bid = " + saleQuaMRB.getMr_b2bid());
				if (count == 0) {
					int saleMRBId = baseDao.getSeqId("qua_mrbdown_seq");// 获取主键序列
					sqls.add(saleQuaMRB.toSqlString(saleMRBId));// 插入主记录
					if (!CollectionUtil.isEmpty(saleQuaMRB.getCheckItems())) {
						for (SaleQuaMRBCheckItem checkItem : saleQuaMRB.getCheckItems()) {
							sqls.add(checkItem.toSqlString(saleMRBId));// 插入明细记录
						}
					}
					if (!CollectionUtil.isEmpty(saleQuaMRB.getProjectItems())) {
						for (SaleQuaMRBProjectItem projectItem : saleQuaMRB.getProjectItems()) {
							sqls.add(projectItem.toSqlString(saleMRBId));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(saleMRBId);
					b2bIdStr.append(saleQuaMRB.getMr_b2bid());// b2bid用于回传到平台修改下载状态
				}
			}
			if (idStr.length() > 0) {
				// 完善主记录
				baseDao.execute(sqls);
				// 消息推送
				Set<String> orderCodes = new HashSet<String>();
				for (SaleQuaMRB saleQuaMRB : saleQuaMRBs) {
					if (!orderCodes.contains(saleQuaMRB.getMr_pucode())) {
						// String sellerCode = getSellerCodeByOrderCodeAndUU(saleQuaMRB.getMr_pucode(), saleQuaMRB.getMr_coustomeruu());
						// String custName = getCustNameByCustomerUU(saleQuaMRB.getMr_coustomeruu());
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "物料已被客户MRB入库", saleQuaMRB.getMr_pucode()
						// + " - " + custName, null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "物料已被客户MRB入库  " + saleQuaMRB.getMr_pucode()
						// + " - " + custName);
						orderCodes.add(saleQuaMRB.getMr_pucode());
					}
				}
				onSaleQuaMRBSuccess(b2bIdStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的客户采购验收单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleQuaMRBSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/MRB?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
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
