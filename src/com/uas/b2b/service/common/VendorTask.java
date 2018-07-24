package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.uas.api.crypto.util.FlexJsonUtils;
import com.uas.b2b.model.InquiryVendorInfo;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;

@Component
@EnableAsync
@EnableScheduling
public class VendorTask extends AbstractTask {

	@Autowired
	private BaseDao baseDao;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		try {
			// 根据自动询价中缺失的供应商相关信息，发送到平台进行邀请注册处理
			logger.info(this.getClass() + " uploadVendorInfo start");
			uploadVendorInfo(master);
			logger.info(this.getClass() + " uploadVendorInfo end");
			logger.info(this.getClass() + " downloadVendorInfo start");
			// 下载注册平台的供应商UU同步到ERP
			downloadVendorInfo(master);
			logger.info(this.getClass() + " downloadVendorInfo end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询erp系统自动询价未注册平台的供应商信息传递至平台进行邀请处理
	 * 
	 * @param master
	 * @return
	 */
	private boolean downloadVendorInfo(Master master) {
		List<InquiryVendorInfo> infos = getVendorInfo();
		if (!CollectionUtils.isEmpty(infos)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtils.toJsonDeep(infos));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/vendor/invite?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == 200)
					uploadSuccess(infos);
				baseDao.save(new TaskLog("(买家)询价单-上传未注册平台供应商信息", infos.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	/**
	 * 上传成功后更新状态
	 * 
	 * @param infos
	 */
	private void uploadSuccess(List<InquiryVendorInfo> infos) {
		List<String> sqls = new ArrayList<String>();
		for (InquiryVendorInfo info : infos) {
			if (null != info.getVe_code()) {
				String sql = "update vendor set ve_sendstatus = '已上传' where ve_code = '" + info.getVe_code() + "'";
				sqls.add(sql);
			}
		}
		if (!CollectionUtils.isEmpty(sqls)) {
			baseDao.execute(sqls);
		}

	}

	/**
	 * 获取需要提醒供应商注册的供应商信息
	 * 
	 * @return
	 */
	private List<InquiryVendorInfo> getVendorInfo() {
		String sql = "select distinct ve_code,ve_name,en_name,ve_email,ve_contact,ve_mobile,ve_webserver,enterprise.en_businesscode,en_adminname,en_adminuu from inquirydetail left join inquiry on in_id=id_inid left join vendor on id_vendcode=ve_code,enterprise where trunc(in_date)=trunc(sysdate) and inquiry.in_remark='自动询价' and id_venduu is null and ve_uu is null and nvl(ve_sendstatus, ' ')= '待上传'";
		return baseDao.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<InquiryVendorInfo>(InquiryVendorInfo.class));
	}

	/**
	 * 供应商注册平台后，下载UU号同步到ERP
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadVendorInfo(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/vendor/downloadUU?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<InquiryVendorInfo> infos = FlexJsonUtil.fromJsonArray(data, InquiryVendorInfo.class);
					if (!CollectionUtil.isEmpty(infos)) {
						updateVend(infos, master);
						baseDao.save(new TaskLog("(买家)供应商--下载供应商注册平台的uu号", infos.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)供应商--下载供应商注册平台的uu号", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获取到UU号之后，更新erp供应商的uu号
	 * 
	 * @param infos
	 */
	private void updateVend(List<InquiryVendorInfo> infos, Master master) {
		if (!CollectionUtils.isEmpty(infos)) {
			List<String> sqls = new ArrayList<String>();
			String idStr = CollectionUtil.getKeyString(infos, ",");
			for (InquiryVendorInfo info : infos) {
				String sql = "update vendor set ve_b2benable = 1,ve_uu = " + info.getVe_uu()
						+ " where nvl(ve_uu,' ')=' ' and ve_webserver = '" + info.getVe_webserver() + "'";
				sqls.add(sql);
			}
			try {
				baseDao.execute(sqls);
				onUpdateSuccess(idStr, master);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新成功后将状态传回平台，更新下载状态
	 * 
	 * @param string
	 * @param master
	 */
	private boolean onUpdateSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendor/check?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
