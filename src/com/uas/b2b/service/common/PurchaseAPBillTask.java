package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Prod;
import com.uas.b2b.model.PurchaseAPBill;
import com.uas.b2b.model.PurchaseAPBillDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

/**
 * 作为买家ERP，将应付发票传入平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseAPBillTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProduct start");
		if (uploadProduct(master)) {// 上传物料
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadAPBill start");
			uploadAPBill(master);// 上传已过账的应付发票
			logger.info(this.getClass() + " uploadAPBill end");
		}
		logger.info(this.getClass() + " uploadNonpostAPBill start");
		uploadNonpostAPBill(master);// 上传反过账的应付发票
		logger.info(this.getClass() + " uploadNonpostAPBill end");

		logger.info(this.getClass() + " uploadApbillAdjustment start");
		uploadApbillAdjustment(master);// 上传货款调账
		logger.info(this.getClass() + " uploadApbillAdjustment end");

		logger.info(this.getClass() + " uploadNonpostAdjustment start");
		uploadNonpostAdjustment(master);// 上传货款调账反过账单据
		logger.info(this.getClass() + " uploadNonpostAdjustment end");
	}

	/**
	 * 上传货款调账反过账的单据
	 * 
	 * @param master
	 */
	private boolean uploadNonpostAdjustment(Master master) {
		List<PurchaseAPBill> purchaseAPBills = getNonpostAdjustmentUpload();
		if (!CollectionUtil.isEmpty(purchaseAPBills)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchaseAPBills));
			String idStr = CollectionUtil.getKeyString(purchaseAPBills, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite()
						+ "/erp/purchase/APBill/nonPostAdjustment?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAPBill(idStr);
				}
				baseDao.save(new TaskLog("(买家)应付发票-上传反过账的货款调账", purchaseAPBills.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取已反过账的货款调账
	 * 
	 * @return
	 */
	private List<PurchaseAPBill> getNonpostAdjustmentUpload() {
		try {
			List<PurchaseAPBill> prodInOuts = baseDao.getJdbcTemplate().query(
					"select ab_id, ab_code from apbill where ab_class = '其它应付单' and ab_status = '未过账' and ab_sendstatus = '上传中' ",
					new BeanPropertyRowMapper<PurchaseAPBill>(PurchaseAPBill.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 货款调帐
	 * 
	 * @param master
	 */
	private boolean uploadApbillAdjustment(Master master) {
		List<PurchaseAPBill> purchaseAPBills = getApbillAdjustmentUpload();
		if (!CollectionUtil.isEmpty(purchaseAPBills)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchaseAPBills));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite()
						+ "/erp/purchase/APBill/APBillAdjustment?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedAPBill(purchaseAPBills);
				}
				baseDao.save(new TaskLog("(买家)应付发票-上传货款调帐", purchaseAPBills.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的货款调帐
	 * 
	 * @return
	 */
	private List<PurchaseAPBill> getApbillAdjustmentUpload() {
		try {
			List<PurchaseAPBill> purchaseAPBills = baseDao.getJdbcTemplate()
					.query("select * from (select apbill.*, ve_uu ab_vendoruu from apbill left join vendor on apbill.ab_vendcode = vendor.ve_code where vendor.ve_b2bcheck = -1 and ab_class = '其它应付单' and ab_status = '已过账'"
							+ " and ab_sendstatus = '待上传' and ve_uu is not null  and nvl(ab_kind,' ')='货款调账' and nvl(ve_b2benable,0)=1 ) where rownum <= 200",
					new BeanPropertyRowMapper<PurchaseAPBill>(PurchaseAPBill.class));
			for (PurchaseAPBill purchaseAPBill : purchaseAPBills) {
				List<PurchaseAPBillDetail> apBillDetails = baseDao.getJdbcTemplate().query(
						"select * from apbilldetail where abd_abid = ? ",
						new BeanPropertyRowMapper<PurchaseAPBillDetail>(PurchaseAPBillDetail.class),
						purchaseAPBill.getAb_id());
				purchaseAPBill.setDetails(apBillDetails);
			}
			return purchaseAPBills;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取需要上传的应付发票
	 * 
	 * @return
	 */
	public List<PurchaseAPBill> getAPBillUpload() {
		try {
			List<PurchaseAPBill> purchaseAPBills = baseDao.getJdbcTemplate().query(
					"select * from (select apbill.*, ve_uu ab_vendoruu from apbill"
							+ " left join vendor on apbill.ab_vendcode = vendor.ve_code where ab_class = '应付发票' and ab_status = '已过账'"
							+ " and ab_sendstatus = '待上传' and ve_uu is not null and nvl(ab_kind,' ')<>'货款调账' and nvl(ve_b2benable,0)=1 ) where rownum <= 50",
					new BeanPropertyRowMapper<PurchaseAPBill>(PurchaseAPBill.class));
			for (PurchaseAPBill purchaseAPBill : purchaseAPBills) {
				List<PurchaseAPBillDetail> apBillDetails = baseDao.getJdbcTemplate().query(
						"select * from apbilldetail where abd_abid = ? ",
						new BeanPropertyRowMapper<PurchaseAPBillDetail>(PurchaseAPBillDetail.class), purchaseAPBill.getAb_id());
				purchaseAPBill.setDetails(apBillDetails);
			}
			return purchaseAPBills;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传成功后，修改采购验收单/采购验退单的状态
	 */
	public void updateUploadedAPBill(List<PurchaseAPBill> purchaseAPBills) {
		for (PurchaseAPBill purchaseAPBill : purchaseAPBills) {
			baseDao.execute("update apbill set ab_sendstatus = '已上传' where ab_id = " + purchaseAPBill.getAb_id());
		}
	}

	/**
	 * 上传应付发票
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadAPBill(Master master) {
		List<PurchaseAPBill> purchaseAPBills = getAPBillUpload();
		if (!CollectionUtil.isEmpty(purchaseAPBills)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchaseAPBills));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/APBill?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedAPBill(purchaseAPBills);
				}
				baseDao.save(new TaskLog("(买家)应付发票-上传应付发票", purchaseAPBills.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的已反过账的应付发票
	 * 
	 * @return
	 */
	public List<PurchaseAPBill> getNonPostingAPBillUpload() {
		try {
			List<PurchaseAPBill> prodInOuts = baseDao.getJdbcTemplate().query(
					"select ab_id, ab_code from apbill where ab_class = '应付发票' and ab_status = '未过账' and ab_sendstatus = '上传中' ",
					new BeanPropertyRowMapper<PurchaseAPBill>(PurchaseAPBill.class));
			return prodInOuts;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 上传成功后，修改已反过账的应付发票
	 */
	public void updateUploadedNonPostingAPBill(String idStr) {
		baseDao.execute("update apbill set ab_sendstatus = '已上传' where ab_id in (" + idStr + ")");
	}

	/**
	 * 上传已反过账的应付发票
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadNonpostAPBill(Master master) {
		List<PurchaseAPBill> apBills = getNonPostingAPBillUpload();
		if (!CollectionUtil.isEmpty(apBills)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(apBills));
			String idStr = CollectionUtil.getKeyString(apBills, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/purchase/APBill/nonPosting?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedNonPostingAPBill(idStr);
				}
				baseDao.save(new TaskLog("(买家)应付发票-上传反过账的应付发票", apBills.size(), response));
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
				baseDao.save(new TaskLog("(买家)应付发票-上传物料资料", prods.size(), response));
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
					.query("select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where exists (select 1 from apbill left join vendor on ab_vendcode=ve_code left join apbilldetail on abd_abid=ab_id where (ab_SENDSTATUS='待上传' or ab_SENDSTATUS='上传中') and ab_statuscode='POSTED' and ve_uu is not null and nvl(ve_b2benable,0)=1 and abd_prodcode=pr_code) and nvl(pr_sendstatus,' ')<>'已上传' order by pr_id) where rownum <= 500",
							new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}