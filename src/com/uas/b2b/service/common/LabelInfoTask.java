package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Label;
import com.uas.b2b.model.LabelParameter;
import com.uas.b2b.model.LabelPrintSetting;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

/**
 * erp端上传到平台label信息表(Label LabelPrintSetting LabelParameter三张表)
 * 
 * @author aof
 * @date 2015年10月28日
 */
@Component
@EnableAsync
@EnableScheduling
public class LabelInfoTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadLabel start");
		uploadLabel(master);// 上传label表到平台
		logger.info(this.getClass() + " uploadLabel end");
		logger.info(this.getClass() + " uploadLabelParameter start");
		uploadLabelParameter(master);// 上传LabelParameter表到平台
		logger.info(this.getClass() + " uploadLabelParameter end");
		logger.info(this.getClass() + " uploadLabelPrintSetting start");
		uploadLabelPrintSetting(master);// 上传LabelPrintSetting表到平台
		logger.info(this.getClass() + " uploadLabelPrintSetting end");
	}

	/**
	 * 上传前，修改Label表的状态位'上传中'
	 * 
	 * @param idStr
	 */
	public void beforeUploadLabel(String idStr) {
		baseDao.execute("update Label set la_sendstatus= '上传中' where la_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改Label表的状态为'待上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedLabelFailed(String idStr) {
		baseDao.execute("update Label set la_sendstatus='待上传' where la_id  in (" + idStr + ")" + " and la_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改Label表的状态为'已上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedLabelSuccess(String idStr) {
		baseDao.execute("update Label set la_sendstatus='已上传' where la_id  in (" + idStr + ")");
	}

	/**
	 * 获取待上传的Label表
	 * 
	 * @return
	 */
	private List<Label> getLabelUpload() {
		try {
			List<Label> labels = baseDao.getJdbcTemplate().query(
					"select * from ( select * from Label where la_sendstatus='待上传' and la_status ='已审核' ) where rownum<=200",
					new BeanPropertyRowMapper<Label>(Label.class));
			return labels;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传Label表
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean uploadLabel(Master master) {
		List<Label> label = getLabelUpload();
		if (!CollectionUtil.isEmpty(label)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(label));
			String idStr = CollectionUtil.getKeyString(label, ",");
			try {
				beforeUploadLabel(idStr);
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/label/label?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedLabelSuccess(idStr);
				} else {
					onUploadedLabelFailed(idStr);
				}
			} catch (Exception e) {
				onUploadedLabelFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传前，修改LabelParameter表的状态位'上传中'
	 * 
	 * @param idStr
	 */
	public void beforeUploadLParameter(String idStr) {
		baseDao.execute("update LabelParameter set lp_sendstatus= '上传中' where lp_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改LabelParameter表的状态为'待上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedLParameterFailed(String idStr) {
		baseDao.execute("update LabelParameter set lp_sendstatus='待上传' where lp_id  in (" + idStr + ")" + " and lp_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改LabelParameter表的状态为'已上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedLParameterSuccess(String idStr) {
		baseDao.execute("update LabelParameter set lp_sendstatus='已上传' where lp_id  in (" + idStr + ")");
	}

	/**
	 * 获取待上传的Label表
	 * 
	 * @return
	 */
	private List<LabelParameter> getLabelParameterUpload() {
		try {
			List<LabelParameter> lParameters = baseDao.getJdbcTemplate().query(
					"select * from ( select * from LabelParameter where lp_sendstatus='待上传') where rownum<=200",
					new BeanPropertyRowMapper<LabelParameter>(LabelParameter.class));
			return lParameters;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传LabelParameter表
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean uploadLabelParameter(Master master) {
		List<LabelParameter> lps = getLabelParameterUpload();
		if (!CollectionUtil.isEmpty(lps)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(lps));
			String idStr = CollectionUtil.getKeyString(lps, ",");
			try {
				beforeUploadLParameter(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/label/labelparameter?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedLParameterSuccess(idStr);
				} else {
					onUploadedLParameterFailed(idStr);
				}
			} catch (Exception e) {
				onUploadedLParameterFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传前，修改LabelPrintSetting表的状态位'上传中'
	 * 
	 * @param idStr
	 */
	public void beforeUploadlPSeting(String idStr) {
		baseDao.execute("update LabelPrintSetting set lps_sendstatus= '上传中' where lps_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改LabelPrintSetting表的状态为'待上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedlPSetingFailed(String idStr) {
		baseDao.execute("update LabelPrintSetting set lps_sendstatus='待上传' where lps_id  in (" + idStr + ")"
				+ " and lps_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改LabelPrintSetting表的状态为'已上传'
	 * 
	 * @param idStr
	 */
	private void onUploadedlPSetingSuccess(String idStr) {
		baseDao.execute("update LabelPrintSetting set lps_sendstatus='已上传' where lps_id  in (" + idStr + ")");
	}

	/**
	 * 获取待上传的Label表
	 * 
	 * @return
	 */
	private List<LabelPrintSetting> getLabelPSetingUpload() {
		try {
			List<LabelPrintSetting> lPSetings = baseDao.getJdbcTemplate().query(
					"select * from ( select * from LabelPrintSetting where lps_sendstatus='待上传') where rownum<=200",
					new BeanPropertyRowMapper<LabelPrintSetting>(LabelPrintSetting.class));
			return lPSetings;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传LabelPrintSetting表
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean uploadLabelPrintSetting(Master master) {
		List<LabelPrintSetting> lpSetings = getLabelPSetingUpload();
		if (!CollectionUtil.isEmpty(lpSetings)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(lpSetings));
			String idStr = CollectionUtil.getKeyString(lpSetings, ",");
			try {
				beforeUploadlPSeting(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/label/lpSeting?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedlPSetingSuccess(idStr);
				} else {
					onUploadedlPSetingFailed(idStr);
				}
			} catch (Exception e) {
				onUploadedlPSetingFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
