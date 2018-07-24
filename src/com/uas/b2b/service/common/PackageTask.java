package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.PackageDown;
import com.uas.b2b.model.PackageDownDetail;
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
public class PackageTask extends AbstractTask {

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadPackage start");
		downloadPackage(master);
		logger.info(this.getClass() + " downloadPackage end");
	}

	/**
	 * 从平台下载客户下达到平台的委外验收单
	 * 
	 * @return
	 */
	private boolean downloadPackage(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/bar/package?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<PackageDown> packageDowns = FlexJsonUtil.fromJsonArray(data, PackageDown.class);
					if (!CollectionUtil.isEmpty(packageDowns)) {
						savePackageDown(packageDowns, master);
						baseDao.save(new TaskLog("(卖家)客户出入库-下载条码外箱", packageDowns.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户出入库-下载条码外箱", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 将数据保存至ERP
	 * 
	 * @param packageDowns
	 * @param enterprise
	 * @param master
	 */
	private void savePackageDown(List<PackageDown> packageDowns, Master master) {
		if (!CollectionUtil.isEmpty(packageDowns)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bIdStr = new StringBuffer();// b2b_id
			for (PackageDown packageDown : packageDowns) {
				int and_Count = 0;
				Long and_yqty = (long) 0;
				int count = baseDao.getCount("select count(1) from baracceptnotify where ban_outboxid = " + packageDown.getPa_id());// 查询是否存在外箱表
				if (count > 0) {
					and_Count = baseDao
							.getCount("select count(1) from acceptnotifydetail left join baracceptnotify on and_anid=ban_anid and and_detno=ban_anddetno where ban_outboxid="
									+ packageDown.getPa_id());
					if (and_Count > 0) {
						and_yqty = baseDao
								.queryForObject(
										"select Max(and_yqty) from acceptnotifydetail left join baracceptnotify on and_anid=ban_anid and and_detno=ban_anddetno where ban_outboxid="
												+ packageDown.getPa_id(), Long.class);
						if (and_yqty > 0) {
							break;
						}
					}
					int paId = baseDao.getSeqId("package_seq");// 获取主键序列
					sqls.add(packageDown.toSqlString(paId));
					if (!CollectionUtil.isEmpty(packageDown.getDetails())) {
						for (PackageDownDetail detail : packageDown.getDetails()) {
							sqls.add(detail.toSqlString(paId));// 插入明细记录
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bIdStr.append(",");
					}
					idStr.append(paId);
					b2bIdStr.append(packageDown.getPa_id());// b2bid用于回传到平台修改下载状态
				}
			}
			baseDao.execute(sqls);
			packageDownSuccess(b2bIdStr.toString(), master);
		}
	}

	/**
	 * 成功写到packageDownSuccess之后，修改平台的表状态为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean packageDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/bar/packagesuccess?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
