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

import com.uas.b2b.model.SaleDownChange;
import com.uas.b2b.model.SaleDownChangeDetail;
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
 * 作为卖家ERP，获取客户下达到平台的委外变更单
 * 
 * @author suntg
 * @since 2015年8月11日15:15:50
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class OutsourceChangeTask extends AbstractTask {

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
		logger.info(this.getClass() + " downloadSaleDownChange start");
		downloadSaleDownChange(master);
		logger.info(this.getClass() + " downloadSaleDownChange end");
	}

	/**
	 * 从平台下载客户下达到平台的委外变更单
	 * 
	 * @return
	 */
	private boolean downloadSaleDownChange(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outsource/change?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDownChange> changes = FlexJsonUtil.fromJsonArray(data, SaleDownChange.class);
					if (!CollectionUtil.isEmpty(changes)) {
						saveSaleDownChange(changes, master);
						baseDao.save(new TaskLog("(卖家)客户委外变更-下载客户委外变更单", changes.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户委外变更-下载客户委外变更单", 0, response));
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
	private void saveSaleDownChange(List<SaleDownChange> changes, Master master) {
		if (!CollectionUtil.isEmpty(changes)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_pc_id
			for (SaleDownChange change : changes) {
				int count = baseDao.getCount("select count(1) from SaleDownChange where sc_type='outsource' and b2b_pc_id="
						+ change.getB2b_pc_id());
				if (count == 0) {
					int scId = baseDao.getSeqId("SaleDownChange_SEQ");
					sqls.add(change.toOutSourceSqlString(scId));
					if (!CollectionUtil.isEmpty(change.getChangeDetails())) {
						for (SaleDownChangeDetail detail : change.getChangeDetails()) {
							sqls.add(detail.toSqlString(scId));
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(scId);
					b2bStr.append(change.getB2b_pc_id());
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update SaleDownChange set (sc_custcode,sc_paymentscode,sc_payments,sc_currency,sc_rate)=(select max(sa_custcode),max(sa_paymentscode),max(sa_payments),max(sc_currency),max(sc_rate) from saledown where sa_code=sc_sacode and sa_type='oustsource') where sc_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail sd1 set scd_newprodcode=(select max(pc_prodcode) from productcustomer,saledownchange,saledownchangedetail sd2 where sc_id=scd_scid and pc_custcode=sc_custcode and pc_custprodcode=sd2.scd_newcustprodcode and pc_custproddetail=sd2.scd_newcustproddetail and pc_custprodspec=sd2.scd_newcustprodspec and sd1.scd_id=sd2.scd_id) where scd_scid in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail set scd_sacode=(select sc_sacode from SaleDownChange where sc_id=scd_scid) where scd_scid in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail set (scd_prodcode,scd_custprodcode,scd_qty,scd_price,scd_taxrate,scd_delivery)=(select max(sd_prodcode),max(sd_custprodcode),max(sd_qty),max(sd_price),max(sd_taxrate),max(sd_delivery) from saledowndetail left join saledown on sd_said=sa_id where sa_code=scd_sacode and sd_detno=scd_sddetno) where scd_scid in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				try {
					for (SaleDownChange change : changes) {
						// 将变更反应到saleDown & sale
						saleDownChangeService.onChangeAgreed(change.getSc_code());
						// String sellerCode = customerService.getSallerCodeByCustomerUU(change.getSc_custuu());
						// String customerName = customerService.getNameByCustomerUU(change.getSc_custuu());
						// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一条客户采购变更单  ", customerName + " "
						// + timeFormat.format(change.getSc_indate()), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
						// "新增一条客户采购变更单  " + customerName + " " + timeFormat.format(change.getSc_indate()));
					}
				} catch (Exception e) {

				}
				onSaleDownChangeSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到saledownchange之后，修改平台的委外变更单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleDownChangeSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/outsource/change?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
