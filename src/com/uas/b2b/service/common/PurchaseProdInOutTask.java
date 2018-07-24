package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Prod;
import com.uas.b2b.model.ProdInOutRefreshPrice;
import com.uas.b2b.model.PurchaseProdInOut;
import com.uas.b2b.model.PurchaseProdInOutDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

/**
 * 作为卖家ERP，将采购入库检验单传入平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseProdInOutTask extends AbstractTask {
	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProdInOut start");
		uploadProdInOut(master);// 上传已过账的采购验收
		logger.info(this.getClass() + " uploadProdInOut end");
		logger.info(this.getClass() + " uploadNonpostAccept start");
		uploadNonpostAccept(master);// 上传反过账的采购验收单
		logger.info(this.getClass() + " uploadNonpostAccept end");
		logger.info(this.getClass() + " uploadProduct start");
		if (uploadProduct(master)) {
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadProdReturn start");
			// 上传已过账的采购验退
			uploadProdReturn(master);
			logger.info(this.getClass() + " uploadProdReturn end");
		}
		logger.info(this.getClass() + " uploadNonpostReturn start");
		uploadNonpostReturn(master);// 上传反过账的采购验退单
		logger.info(this.getClass() + " uploadNonpostReturn end");
		logger.info(this.getClass() + " uploadProdBadIn start");
		uploadProdBadIn(master);// 上传已过账的不良品入库
		logger.info(this.getClass() + " uploadProdBadIn end");
		logger.info(this.getClass() + " uploadNonpostBadIn start");
		uploadNonpostBadIn(master);// 上传反过账的不良品入库单
		logger.info(this.getClass() + " uploadNonpostBadIn end");
		logger.info(this.getClass() + " uploadProdBadOut start");
		uploadProdBadOut(master);// 上传已过账的不良品出库单
		logger.info(this.getClass() + " uploadProdBadOut end");
		logger.info(this.getClass() + " uploadNonpostBadOut start");
		uploadNonpostBadOut(master);// 上传反过账的不良品出库单
		logger.info(this.getClass() + " uploadNonpostBadOut end");
		// 批量更新采购验收单单价
		logger.info(this.getClass() + "uploadUpdatePriceByBatch start");
		uploadUpdatePriceByBatch(master);
		logger.info(this.getClass() + "uploadUpdatePriceByBatch end");
		// 批量更新采购验退单单价
		logger.info(this.getClass() + "uploadPurcReturnUpdatePriceByBatch start");
		uploadPurcReturnUpdatePriceByBatch(master);
		logger.info(this.getClass() + "uploadPurcReturnUpdatePriceByBatch end");
		// 批量更新委外验收单单价
		logger.info(this.getClass() + "uploadMakeAcceptUpdatePriceByBatch start");
		uploadMakeAcceptUpdatePriceByBatch(master);
		logger.info(this.getClass() + "uploadMakeAcceptUpdatePriceByBatch end");
		// 批量更新委外验退单单价
		logger.info(this.getClass() + "uploadMakeReturnUpdatePriceByBatch start");
		uploadMakeReturnUpdatePriceByBatch(master);
		logger.info(this.getClass() + "uploadMakeReturnUpdatePriceByBatch end");
	}
	
	private final String TYPE_PURC_ACCETP = "采购验收单";
	private final String TYPE_PURC_RETURN = "采购验退单";
	private final String TYPE_MAKE_ACCETP = "委外验收单";
	private final String TYPE_MAKE_RETURN = "委外验退单";
	
	/**
	 * 批量更新采购验收单明细单价
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadUpdatePriceByBatch(Master master) {
		List<ProdInOutRefreshPrice> prods = getPriceBatchUpdate(TYPE_PURC_ACCETP);
		if(!CollectionUtils.isEmpty(prods)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prods));
			String idStr = CollectionUtil.getKeyString(prods, ",");
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodInOut/refreshPrice?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					uploadUpdatePriceByBatch(idStr);
				}
				baseDao.save(new TaskLog("(买家)采购验收单-批量更新采购验收单单价", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/** 
	 * 上传成功更新状态
	 * 
	 * @param idStr
	 */
	private void uploadUpdatePriceByBatch(String idStr) {
		baseDao.execute("update PriceBatchUpdate set pbu_sendstatus='已上传' where pbu_id in (" + idStr + ")");
	}

	/**
	 * 获取需要批量更改的单价的数据
	 * 
	 * @return
	 */
	private List<ProdInOutRefreshPrice> getPriceBatchUpdate(String type) {
		try {
			List<ProdInOutRefreshPrice> prods = baseDao.query("select pbu_pdno,pbu_inoutno,pbu_orderprice,pbu_id,pbu_taxrate,pd_prodcode,pd_orderdetno from PriceBatchUpdate left join prodiodetail on pbu_pdid = pd_id where pbu_piclass = '" 
					+ type + "' and pbu_status = '99'and pbu_sendstatus = '待上传' and rownum < 200", ProdInOutRefreshPrice.class);
			return prods;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 批量更新采购验退单明细单价
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadPurcReturnUpdatePriceByBatch(Master master) {
		List<ProdInOutRefreshPrice> prods = getPriceBatchUpdate(TYPE_PURC_RETURN);
		if(!CollectionUtils.isEmpty(prods)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prods));
			String idStr = CollectionUtil.getKeyString(prods, ",");
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodInOut/refreshPrice/purc/return?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					uploadUpdatePriceByBatch(idStr);
				}
				baseDao.save(new TaskLog("(买家)采购验退单-批量更新采购验退单单价", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 批量更新委外验收单明细单价
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadMakeAcceptUpdatePriceByBatch(Master master) {
		List<ProdInOutRefreshPrice> prods = getPriceBatchUpdate(TYPE_MAKE_ACCETP);
		if(!CollectionUtils.isEmpty(prods)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prods));
			String idStr = CollectionUtil.getKeyString(prods, ",");
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodInOut/refreshPrice/make/accept?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					uploadUpdatePriceByBatch(idStr);
				}
				baseDao.save(new TaskLog("(买家)委外验收单-批量更新委外验收单单价", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 批量更新委外验退单明细单价
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadMakeReturnUpdatePriceByBatch(Master master) {
		List<ProdInOutRefreshPrice> prods = getPriceBatchUpdate(TYPE_MAKE_RETURN);
		if(!CollectionUtils.isEmpty(prods)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prods));
			String idStr = CollectionUtil.getKeyString(prods, ",");
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodInOut/refreshPrice/make/return?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					uploadUpdatePriceByBatch(idStr);
				}
				baseDao.save(new TaskLog("(买家)委外验退单-批量更新委外验退单单价", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}


	/**
	 * 获取需要上传的采购验收单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getProInOutUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao.getJdbcTemplate().query(
					"select * from (select prodinout.*, ve_uu pi_vendoruu from prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where pi_class='采购验收单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and ve_uu is not null and nvl(ve_b2benable,0)=1 ) where rownum <= 200 and exists (select 1 from purchase,prodiodetail where pd_piid=pi_id and pu_sendstatus='已上传')",
					new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			for (PurchaseProdInOut prodInOut : prodInOuts) {
				List<PurchaseProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<PurchaseProdInOutDetail>(PurchaseProdInOutDetail.class),
						prodInOut.getPi_id());
				prodInOut.setDetails(details);
			}
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传成功后，修改采购验收单/采购验退单的状态
	 */
	public void onUploadedProdInOutSuccess(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '已上传' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传成功后，修改采购验收单/采购验退单的状态
	 */
	public void beforeUploadedProdInOut(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '上传中' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传成功后，修改采购验收单/采购验退单的状态
	 */
	public void onUploadedProdInOutFailed(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '待上传' where pi_id in (" + idStr + ")" + " and pi_sendstatus = '上传中'");
	}

	/**
	 * 上传采购验收单（已过账）
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadProdInOut(Master master) {
		List<PurchaseProdInOut> prodInOuts = getProInOutUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodInOut?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传采购验收单", prodInOuts.size(), response));
			} catch (Exception e) {
				onUploadedProdInOutFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的已反过账的采购验收单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getNonPostingAcceptUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='采购验收单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=500 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传成功后，修改已反过账的采购验收单的状态
	 */
	public void updateUploadedNonPostingAccept(String idStr) {
		baseDao.execute("update prodinout set pi_sendstatus = '已上传' where pi_id in (" + idStr + ")");
	}

	/**
	 * 上传已反过账的采购验收单
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostAccept(Master master) {
		List<PurchaseProdInOut> prodInOuts = getNonPostingAcceptUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/prodInOut/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账采购验收单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传采购验退单
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadProdReturn(Master master) {
		List<PurchaseProdInOut> prodReturns = getProdReturnUpload();
		if (!CollectionUtil.isEmpty(prodReturns)) {
			String idStr = CollectionUtil.getKeyString(prodReturns, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodReturns));
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodReturn?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传采购验退单", prodReturns.size(), response));
			} catch (Exception e) {
				onUploadedProdInOutFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的采购验退单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getProdReturnUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select prodinout.*, ve_uu pi_vendoruu from prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where pi_class='采购验退单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and exists (select 1 from prodiodetail left join purchase on pu_code=pd_ordercode where pd_piid=pi_id and (pd_ordercode is null or pu_sendstatus='已上传')) and ve_uu is not null and nvl(ve_b2benable,0)=1) where rownum <= 200 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			for (PurchaseProdInOut prodInOut : prodInOuts) {
				List<PurchaseProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<PurchaseProdInOutDetail>(PurchaseProdInOutDetail.class), prodInOut.getPi_id());
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
	public List<PurchaseProdInOut> getNonPostingReturnUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='采购验退单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=500 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传已反过账的采购验退单
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostReturn(Master master) {
		List<PurchaseProdInOut> prodInOuts = getNonPostingReturnUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/prodReturn/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账采购验退单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传不良品入库单
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadProdBadIn(Master master) {
		List<PurchaseProdInOut> prodBadIns = getProdBadInUpload();
		if (!CollectionUtil.isEmpty(prodBadIns)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodBadIns));
			String idStr = CollectionUtil.getKeyString(prodBadIns, ",");
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodBadIn?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传不良品入库单", prodBadIns.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的不良品入库单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getProdBadInUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select prodinout.*, ve_uu pi_vendoruu from prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where pi_class='不良品入库单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and ve_uu is not null and nvl(ve_b2benable,0)=1) where rownum<=200 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			for (PurchaseProdInOut prodInOut : prodInOuts) {
				List<PurchaseProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<PurchaseProdInOutDetail>(PurchaseProdInOutDetail.class), prodInOut.getPi_id());
				prodInOut.setDetails(details);
			}
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 获取需要上传的已反过账的不良入库单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getNonPostingBadInUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='不良品入库单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=500 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传已反过账的不良品入库单
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostBadIn(Master master) {
		List<PurchaseProdInOut> prodInOuts = getNonPostingBadInUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodBadIn/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账不良品入库单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传不良品出库单
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadProdBadOut(Master master) {
		List<PurchaseProdInOut> prodBadOuts = getProdBadOutUpload();
		if (!CollectionUtil.isEmpty(prodBadOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodBadOuts));
			String idStr = CollectionUtil.getKeyString(prodBadOuts, ",");
			try {
				beforeUploadedProdInOut(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/prodBadOut?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedProdInOutSuccess(idStr);
				} else {
					onUploadedProdInOutFailed(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传不良品出库单", prodBadOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的不良品出库单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getProdBadOutUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select prodinout.*, ve_uu pi_vendoruu from prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code where pi_class='不良品出库单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and ve_uu is not null and nvl(ve_b2benable,0)=1) where rownum <= 200 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			for (PurchaseProdInOut prodInOut : prodInOuts) {
				List<PurchaseProdInOutDetail> details = baseDao.getJdbcTemplate().query(
						"select prodiodetail.* , pd_pdno pd_detno from prodiodetail where pd_piid = ? ",
						new BeanPropertyRowMapper<PurchaseProdInOutDetail>(PurchaseProdInOutDetail.class), prodInOut.getPi_id());
				prodInOut.setDetails(details);
			}
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 获取需要上传的已反过账的不良出库单
	 * 
	 * @return
	 */
	public List<PurchaseProdInOut> getNonPostingBadOutUpload() {
		try {
			List<PurchaseProdInOut> prodInOuts = baseDao
					.getJdbcTemplate()
					.query("select * from (select pi_id , pi_inoutno from prodinout where pi_class='不良品出库单' and pi_status='未过账' and pi_sendstatus='上传中') where rownum<=500 ",
							new BeanPropertyRowMapper<PurchaseProdInOut>(PurchaseProdInOut.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传已反过账的不良品出库单
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostBadOut(Master master) {
		List<PurchaseProdInOut> prodInOuts = getNonPostingBadOutUpload();
		if (!CollectionUtil.isEmpty(prodInOuts)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(prodInOuts));
			String idStr = CollectionUtil.getKeyString(prodInOuts, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/prodBadOut/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAccept(idStr);
				}
				baseDao.save(new TaskLog("(买家)出入库-上传反过账不良品出库单", prodInOuts.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传物料资料
	 * 
	 * @param en
	 * 
	 * @return
	 */
	private boolean uploadProduct(Master master) {
		List<Prod> prods = getProducts();
		if (!CollectionUtil.isEmpty(prods)) {
			String idStr = CollectionUtil.getKeyString(prods, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(prods));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeProductUpload(idStr);
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/product?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductUploadSuccess(idStr);
					return uploadProduct(master);// 递归传所有物料
				} else
					onProductUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)采购单-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onProductUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 更新已上传的物料资料
	 * 
	 * @param idStr
	 *            已上传的物料资料
	 */
	public void beforeProductUpload(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='上传中' where pr_id in (" + idStr + ")");
	}

	/**
	 * 更新已上传的物料资料
	 * 
	 * @param idStr
	 *            已上传的物料资料
	 */
	public void onProductUploadSuccess(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='已上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的物料资料
	 * 
	 * @param idStr
	 *            上传失败的物料资料
	 */
	public void onProductUploadFail(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='待上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 上传物料
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			List<Prod> prods = baseDao
					.getJdbcTemplate()
					.query("select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where  exists (select 1 from prodinout left join vendor on prodinout.pi_cardcode = vendor.ve_code left join prodiodetail on pi_id=pd_piid where pi_class='采购验退单' and pi_status = '已过账' and (pi_sendstatus = '待上传' or pi_sendstatus='上传中') and ve_uu is not null and nvl(ve_b2benable,0)=1 and pd_prodcode=pr_code) and nvl(pr_sendstatus,' ')<>'已上传' order by pr_id)  where rownum <= 200 ",
							new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}