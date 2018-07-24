package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.MakeProdInOut;
import com.uas.b2b.model.MakeProdInOutDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

/**
 * 作为买家EPR,将委外验收验退单传入平台
 * 
 * @author aof
 *
 */
@Component
@EnableAsync
@EnableScheduling
public class MakeProdInOutTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProdInOut start");
		uploadProdInOut(master);// 上传已过账的委外验收
		logger.info(this.getClass() + " uploadProdInOut end");
		logger.info(this.getClass() + " uploadNonpostAccept start");
		uploadNonpostAccept(master);// 上传反过账的委外验收单
		logger.info(this.getClass() + " uploadNonpostAccept end");
		logger.info(this.getClass() + " uploadProdReturn start");
		uploadProdReturn(master);// 上传已过账的采购委外验退单
		logger.info(this.getClass() + " uploadProdReturn end");
		logger.info(this.getClass() + " uploadNonpostReturn start");
		uploadNonpostReturn(master);// 上传反过账的委外验退单
		logger.info(this.getClass() + " uploadNonpostReturn end");
	}

	/**
	 * 获取需要上传的委外验收单
	 * 
	 * @return
	 */
	public List<MakeProdInOut> getProInOutUpload() {
		try {
			List<MakeProdInOut> prodInOuts = baseDao.getJdbcTemplate().query(
					"select * from (select prodinout.*, ve_uu pi_vendoruu from "
							+ " prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where"
							+ " pi_class='委外验收单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中')"
							+ "and ve_uu is not null and nvl(ve_b2benable,0)=1 ) where rownum<=200",
					new BeanPropertyRowMapper<MakeProdInOut>(MakeProdInOut.class));
			for (MakeProdInOut prodInOut : prodInOuts) {
				List<MakeProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<MakeProdInOutDetail>(MakeProdInOutDetail.class), prodInOut.getPi_id());
				prodInOut.setDetails(details);
			}
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传之前，修改委外验收单/委外验退单的状态
	 */
	public void beforeUploadedProdInOut(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '上传中' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改采购验收单/采购验退单的状态
	 */
	public void onUploadedProdInOutFailed(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '待上传' where pi_id in (" + idStr + ")" + " and pi_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改委外验收单/委外验退单的状态
	 */
	public void onUploadedProdInOutSuccess(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '已上传' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传委外验收单（已过账）
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadProdInOut(Master master) {
		List<MakeProdInOut> prodInOuts = getProInOutUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/make/prodInOut?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传委外验收单", prodInOuts.size(), response));
			} catch (Exception e) {
				onUploadedProdInOutFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的已反过账的委外验收单
	 * 
	 * @return
	 */
	public List<MakeProdInOut> getNonPostingAcceptUpload() {
		try {
			List<MakeProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='委外验收单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=200 ",
							new BeanPropertyRowMapper<MakeProdInOut>(MakeProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传成功后，修改已反过账的委外验收单的状态
	 */
	public void updateUploadedNonPostingAccept(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '已上传' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传已反过账的委外验收单
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostAccept(Master master) {
		List<MakeProdInOut> prodInOuts = getNonPostingAcceptUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make/prodInOut/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账委外验收单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传委外验退单
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	public boolean uploadProdReturn(Master master) {
		List<MakeProdInOut> prodReturns = getProdReturnUpload();
		if (!CollectionUtil.isEmpty(prodReturns)) {
			String idStr = CollectionUtil.getKeyString(prodReturns, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodReturns));
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/make/prodReturn?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传委外验退单", prodReturns.size(), response));
			} catch (Exception e) {
				onUploadedProdInOutFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的委外验退单
	 * 
	 * @return
	 */
	public List<MakeProdInOut> getProdReturnUpload() {
		try {
			List<MakeProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select prodinout.*, ve_uu pi_vendoruu from"
							+ " prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where"
							+ " pi_class='委外验退单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and ve_uu is not null and nvl(ve_b2benable,0)=1) where rownum <= 200 ",
							new BeanPropertyRowMapper<MakeProdInOut>(MakeProdInOut.class));
			for (MakeProdInOut prodInOut : prodInOuts) {
				List<MakeProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<MakeProdInOutDetail>(MakeProdInOutDetail.class), prodInOut.getPi_id());
				prodInOut.setDetails(details);
			}
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 获取需要上传的已反过账的采购验退单
	 * 
	 * @return
	 */
	public List<MakeProdInOut> getNonPostingReturnUpload() {
		try {
			List<MakeProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='委外验退单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=200 ",
							new BeanPropertyRowMapper<MakeProdInOut>(MakeProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传已反过账的委外验退单
	 * 
	 * @param enterprise
	 * @param master
	 * @return
	 */
	private boolean uploadNonpostReturn(Master master) {
		List<MakeProdInOut> prodInOuts = getNonPostingReturnUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make/prodReturn/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账委外验退单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

}
