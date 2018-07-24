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

import com.uas.b2b.model.SaleForecastDown;
import com.uas.b2b.model.SaleForecastDownDet;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.crm.CustomerService;

@Component
@EnableAsync
@EnableScheduling
public class SaleForecastDownTask extends AbstractTask {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private EmployeeService employeeService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleForecastDown start");
		downloadSaleForecastDown(master);
		logger.info(this.getClass() + " downloadSaleForecastDown end");
	}

	/**
	 * 从平台下载客户下达到平台的采购预测
	 * 
	 * @return
	 */

	private boolean downloadSaleForecastDown(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/forecastDown?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleForecastDown> saleForecasts = FlexJsonUtil.fromJsonArray(data, SaleForecastDown.class);
					if (!CollectionUtil.isEmpty(saleForecasts)) {
						saveSaleForecastDown(saleForecasts, master);
						baseDao.save(new TaskLog("(卖家)客户采购预测-下载客户采购预测单", saleForecasts.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购预测-下载客户采购预测单", 0, response));
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
	private void saveSaleForecastDown(List<SaleForecastDown> saleForecasts, Master master) {
		if (!CollectionUtil.isEmpty(saleForecasts)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// 明细 b2b_id
			List<SaleForecastDown> validSales = new ArrayList<SaleForecastDown>();// 需要保存的单
			for (SaleForecastDown saleForecast : saleForecasts) {
				int count = baseDao.getCount("select count(1) from PurchaseForecastDown where b2b_pf_id=" + saleForecast.getB2b_pf_id());
				if (count == 0) {
					int pfId = baseDao.getSeqId("PurchaseForecastDown_SEQ");
					sqls.add(saleForecast.toSqlString(pfId));
					if (!CollectionUtil.isEmpty(saleForecast.getSaleForecastDownDets())) {
						for (SaleForecastDownDet detail : saleForecast.getSaleForecastDownDets()) {
							sqls.add(detail.toSqlString(pfId));
							if (b2bStr.length() > 0) {
								b2bStr.append(",");
							}
							b2bStr.append(detail.getB2b_id());
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
					}
					idStr.append(pfId);
					validSales.add(saleForecast);
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update PurchaseForecastDown set (pf_custcode,pf_custname)=(select cu_code,cu_name from customer where cu_uu=pf_customeruu) where pf_id in ("
						+ idStr.toString() + ")");
				sqls.add("update PurchaseForecastDownDet sd1 set (pfd_prodid,pfd_prodcode)=(select max(pc_prodid),max(pc_prodcode) from productcustomer,purchaseforecastdown,purchaseforecastdowndet sd2 where pf_id=pfd_pfid and pc_custcode=pf_custcode and pc_custprodcode=sd2.pfd_custprodcode and pc_custproddetail=sd2.pfd_custproddetail and pc_custprodspec=sd2.pfd_custprodspec and sd1.pfd_id=sd2.pfd_id) where pfd_pfid in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				for (SaleForecastDown saleForecast : validSales) {
					// 直接取客户表(customer)中客户对应业务员的code
					// 每张采购预测单只发送一条推送，推送给客户表中客户对应的业务员
					String sellerCode = customerService.getSallerCodeByCustomerUU(saleForecast.getPf_customeruu());
					// String customerName = customerService.getNameByCustomerUU(saleForecast.getPf_customeruu());// 根据客户uu获取客户名称
					// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
					if (sellerCode != null) {
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一条客户采购预测单", customerName + " "
						// + timeFormat.format(saleForecast.getPf_date()), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
						// "新增一条客户采购预测单  " + customerName + " " + timeFormat.format(saleForecast.getPf_date()));
					}
				}
				onSaleForecastDownSuccess(b2bStr.toString(), master);
			}
		}
	}

	private boolean onSaleForecastDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/forecastDown?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
