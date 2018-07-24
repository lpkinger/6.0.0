package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.BarAcceptNotify;
import com.uas.b2b.model.BarAcceptNotifyDown;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

/**
 * 作为卖家ERP，获取客户条码收料通知
 * 
 * @author aof
 * @date 2015年10月30日
 */
@Component
@EnableAsync
@EnableScheduling
public class BarAcceptNotifyTask extends AbstractTask {

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadBarSendNotify start");
		downloadBarSendNotify(master);
		logger.info(this.getClass() + " downloadBarSendNotify end");
	}

	/**
	 * 从平台下载客户下达到平台的BarSendNotify
	 * 
	 * @return
	 */
	private boolean downloadBarSendNotify(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/bar/sendnotify?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<BarAcceptNotifyDown> prodInOutDowns = FlexJsonUtil.fromJsonArray(data, BarAcceptNotifyDown.class);
					if (!CollectionUtil.isEmpty(prodInOutDowns)) {
						saveBarSendNotify(prodInOutDowns, master);
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
	 * 保存BarSendNotify
	 * 
	 * @param downs
	 * @param enterprise
	 * @param master
	 */
	public void saveBarSendNotify(List<BarAcceptNotifyDown> downs, Master master) {
		if (!CollectionUtil.isEmpty(downs)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			StringBuffer idStr = new StringBuffer();
			for (BarAcceptNotifyDown barAcceptNotify : downs) {
				BarAcceptNotify barNotify = barAcceptNotify.conver();
				int count = baseDao.getCount("select count(1) from acceptnotify where B2B_SS_ID = " + barAcceptNotify.getBad_ssid());
				if (count > 0) { // 发货单已收料
					// barCount判断是否已经存在条码
					int barCount = baseDao.getCount("select count(1) from barAcceptNotify where ban_anid ="
							+ "(select an_id from acceptnotify where B2B_SS_ID = " + barAcceptNotify.getBad_ssid() + ")");
					String[] fields = { "an_id", "an_code" };
					String[] fields2 = { "and_id", "and_detno", "and_yqty" };
					Object[] data = baseDao.getFieldsDataByCondition("acceptnotify", fields, "B2B_SS_ID=" + barAcceptNotify.getBad_ssid());
					if (data != null && data.length == 2) {
						barNotify.setBcn_anid(Long.parseLong(data[0].toString()));
						barNotify.setBcn_ancode(data[1].toString());
						Object[] data2 = baseDao.getFieldsDataByCondition("acceptnotifydetail", fields2,
								"and_anid=" + barNotify.getBcn_anid() + " and and_detno=" + barAcceptNotify.getBad_sinumber());
						if (data2 != null && data2.length == 3) {
							barNotify.setBcn_andid(Long.parseLong(data2[0].toString()));
							barNotify.setBcn_anddetno(Double.parseDouble(data2[1].toString()));
						}
						if (idStr.length() > 0) {
							idStr.append(",");
							b2bIdStr.append(",");
						}
						int sosid = baseDao.getSeqId("Baracceptnotify_seq");
						idStr.append(sosid);
						if (data2 != null && data2.length == 3) {
							if (Long.parseLong(data2[2].toString()) > 0) {
							} else {
								if (barCount > 0) {
									String sql = "delete from package where pa_b2bid = "
											+ "(select distinct ban_outboxid from baracceptnotify ban join acceptnotify an on ban.ban_anid=an.an_id where an.b2b_ss_id="
											+ barAcceptNotify.getBad_ssid() + ")";
									String sql2 = "delete from packagedetail where pd_paid ="
											+ "(select distinct pd.pd_paid from packagedetail pd join package pa on pd.pd_paid=pa.pa_id where pa.pa_b2bid = "
											+ "(select distinct ban_outboxid from baracceptnotify ban join acceptnotify an on ban.ban_anid=an.an_id where an.b2b_ss_id="
											+ barAcceptNotify.getBad_ssid() + "))";
									sqls.add(sql2);
									sqls.add(sql);
									sqls.add(barNotify.toDelete());
									// baseDao.execute(barNotify.toDelete());
								}
								sqls.add(barNotify.toSqlOutSource(sosid));
								// baseDao.execute(barNotify.toSqlOutSource(sosid));
								b2bIdStr.append(barAcceptNotify.getBad_ssid());
							}
						}
					}
				}
			}
			baseDao.execute(sqls);
			barSendNotifySuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功写到prodiodown之后，修改平台的条码表为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean barSendNotifySuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/bar/sendnotify?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}
}
