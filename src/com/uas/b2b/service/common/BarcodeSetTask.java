package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.BarcodeSet;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

/**
 * erp端上传到平台条码产生规则表
 * 
 * @author aof
 * @date 2015年9月11日
 */
@Component
@EnableAsync
@EnableScheduling
public class BarcodeSetTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadBarcodeSet start");
		uploadBarcodeSet(master);// 上传待上传的条码产生规则表
		logger.info(this.getClass() + " uploadBarcodeSet end");
	}

	/**
	 * 上传前，修改规则表的状态位'上传中'
	 * 
	 * @param idStr
	 */
	public void beforeUploadBarcodeSet(String idStr) {
		baseDao.execute("update BarcodeSet set bs_sendstatus= '上传中' where bs_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改规则表的状态为'待上传'
	 * 
	 * @param idStr
	 */
	public void onUploadedBarcodeSetFailed(String idStr) {
		baseDao.execute("update BarcodeSet set bs_sendstatus='待上传' where bs_id  in (" + idStr + ")" + " and bs_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改规则表的状态为'已上传'
	 * 
	 * @param idStr
	 */
	public void onUploadedBarcodeSetSuccess(String idStr) {
		baseDao.execute("update BarcodeSet set bs_sendstatus='已上传' where bs_id  in (" + idStr + ")");
	}

	/**
	 * 从数据库查询需要上传的规则表
	 * 
	 * @return
	 */
	public List<BarcodeSet> getBarcodeSetUpload() {
		try {
			List<BarcodeSet> codeSets = baseDao.getJdbcTemplate().query(
					"select * from( select * from BarcodeSet where bs_sendstatus='待上传' "
							+ "and bs_type in ('BATCH','PACK') and bs_status='已审核'" + " ) where rownum<=200",
					new BeanPropertyRowMapper<BarcodeSet>(BarcodeSet.class));
			return codeSets;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 上传待上传的条码产生规则表
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	public boolean uploadBarcodeSet(Master master) {
		List<BarcodeSet> codeSet = getBarcodeSetUpload();
		if (!CollectionUtil.isEmpty(codeSet)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(codeSet));
			String idStr = CollectionUtil.getKeyString(codeSet, ",");
			try {
				beforeUploadBarcodeSet(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/Barcode/codeSet?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedBarcodeSetSuccess(idStr);
				} else {
					onUploadedBarcodeSetFailed(idStr);
				}
			} catch (Exception e) {
				onUploadedBarcodeSetFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
