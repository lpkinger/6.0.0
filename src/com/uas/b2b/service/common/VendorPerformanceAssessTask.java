package com.uas.b2b.service.common;

import com.uas.b2b.model.TaskLog;
import com.uas.b2b.model.VendorPerformanceAssess;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 设置轮询，定时更新供应商绩效考核
 * 
 * @author huangct
 * @time 创建时间：2017年10月12日
 */
@Component
@EnableAsync
@EnableScheduling
public class VendorPerformanceAssessTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadVendorPerformanceAssess start");
			uploadVendorPerformanceAssess(master);
			logger.info(this.getClass() + " uploadVendorPerformanceAssess end");
//			logger.info(this.getClass() + " downloadVendorPerformanceAssess start");
//			downloadVendorPerformanceAssess(master);
//			logger.info(this.getClass() + " downloadVendorPerformanceAssess end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传已审核待上传的供应商绩效表
	 * @param master
	 * @return
	 */
	private boolean uploadVendorPerformanceAssess(Master master) {
		List<VendorPerformanceAssess> vpas = getVendorPerformanceAssesss();
		if (!CollectionUtil.isEmpty(vpas)) {
			String idStr = CollectionUtil.getKeyString(vpas, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(vpas));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/vendorPerformanceAssess/?access_id=" + master.getMa_uu(), params,
						true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onVendorPerformanceAssessUploadSuccess(idStr);
					return uploadVendorPerformanceAssess(master);
				} else
					onVendorPerformanceAssessUploadFail(idStr);
				baseDao.save(new TaskLog("(供应商)绩效表-上传已审核待上传的供应商绩效表", vpas.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onVendorPerformanceAssessUploadFail(idStr);
				return false;
			}
		}
		return true;
	}
	
	

	/**
	 * 下载更新供应商绩效考核（获取供应商回复后的认可记录）
	 * @param master
	 * @return
	 */
	private boolean downloadVendorPerformanceAssess(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/vendorPerformanceAssess/backtouas?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<VendorPerformanceAssess> vpas = FlexJsonUtil.fromJsonArray(data, VendorPerformanceAssess.class);
					if (!CollectionUtil.isEmpty(vpas)) {
						saveProdDown(vpas, master);
						baseDao.save(new TaskLog("供应商绩效考核-下载更新供应商绩效考核（获取供应商回复后的认可记录）", vpas.size(), response));
					}
				}
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存更新物料信息
	 * 
	 * @param vpas
	 * @param master
	 */
	private void saveProdDown(List<VendorPerformanceAssess> vpas, Master master) {
		if(!CollectionUtil.isEmpty(vpas)){
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bStr = new StringBuffer();// b2b_id
			for(VendorPerformanceAssess vpa:vpas) {
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(vpa.getVpa_id());
				
				//TODO id 字符串???
				//已认可/不认可、并说明原因（不认可时必填）、回复人、回复时间
				String sql = "update VendorPerformanceAssess set vpa_gysqrdf = '" + vpa.getVpa_gysqrdf() + ",vpa_brkyy = '" + vpa.getVpa_brkyy() + ",vpa_recordman = '" + vpa.getVpa_recordman() + ",vpa_recorddate = '" + vpa.getVpa_recorddate()   + "' where vpa_id = '" + vpa.getVpa_id() +"'";	
				sqls.add(sql);
			}
			if(!CollectionUtils.isEmpty(sqls)){
				baseDao.execute(sqls);
			}
			if(b2bStr.length() > 0) {
				downloadSuccess(b2bStr.toString(), master);
			}
		}
		
	}

	/**
	 * 更新平台供应商回复的下载状态为已下载
	 * 
	 * @param b2bStr
	 * @param master
	 */
	private boolean downloadSuccess(String b2bStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", b2bStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendorPerformanceAssess/refreshDownloadstatus?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	

	/**
	 * 供应商绩效考核上传失败
	 * 
	 * @param idStr
	 */
	private void onVendorPerformanceAssessUploadFail(String idStr) {
		baseDao.execute("update VendorPerformanceAssess set VPA_SENDSTATUS='待上传' where vpa_id in (" + idStr + ")");
	}

	/**
	 * 供应商绩效考核上传成功
	 * 
	 * @param idStr
	 */
	private void onVendorPerformanceAssessUploadSuccess(String idStr) {
		baseDao.execute("update VendorPerformanceAssess set VPA_SENDSTATUS='已上传' where vpa_id in (" + idStr + ")");
	}

	/**
	 * 获取待上传的供应商绩效考核
	 * 
	 * @return
	 */
	private List<VendorPerformanceAssess> getVendorPerformanceAssesss() {
		try {
			List<VendorPerformanceAssess> vpas = baseDao.getJdbcTemplate().query(
					"select * from (select * from VendorPerformanceAssess  LEFT JOIN vendor  on vpa_vendorcode = ve_code where  nvl(vpa_sendstatus,' ')<>'已上传' and  nvl(vpa_status, ' ') = '已审核' and nvl(ve_b2benable, 0) = 1 and ve_uu is not null order by vpa_id) where rownum <= 500",
					new BeanPropertyRowMapper<VendorPerformanceAssess>(VendorPerformanceAssess.class));
			return vpas;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
