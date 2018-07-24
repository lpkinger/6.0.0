package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Prod;
import com.uas.b2b.model.PurchaseForecast;
import com.uas.b2b.model.PurchaseForecastDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;

/**
 * 作为买家ERP，将采购预测单传入平台
 * 
 * @author shenj
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseForecastTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProduct start");
		if (uploadProduct(master)) {
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadPurchaseForecasts start");
			uploadPurchaseForecasts(master);
			logger.info(this.getClass() + " uploadPurchaseForecasts end");
		}
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
				baseDao.save(new TaskLog("(买家)采购预测-上传物料资料", prods.size(), response));
			} catch (Exception e) {
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
			return baseDao.getJdbcTemplate().query(
					"select * from (select distinct product.* from product where exists "
							+ " (select 1 from purchaseforecastdetail left join vendor on pfd_vendcode=ve_code "
							+ " left join purchaseForecast on pfd_pfid=pf_id "
							+ " where PF_SENDSTATUS='待上传' and ve_uu is not null and nvl(ve_b2benable,0)=1 and pfd_prodcode=pr_code) "
							+ " and pr_statuscode='AUDITED' and nvl(pr_sendstatus,' ')<>'已上传') where rownum <= 500 ",
					new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 获取需要上传的采购预测
	 * 
	 * @return
	 */
	public List<PurchaseForecast> getPurchaseForecastsUpload() {
		try {
			List<PurchaseForecast> forecasts = new ArrayList<PurchaseForecast>();
			List<PurchaseForecast> purchaseForecasts = baseDao.getJdbcTemplate().query(
					"select purchaseForecast.*,em_uu from purchaseforecast left join employee "
							+ "on pf_buyerid=em_id where Pf_SENDSTATUS='待上传' order by pf_code",
					new BeanPropertyRowMapper<PurchaseForecast>(PurchaseForecast.class));
			for (PurchaseForecast purchaseForecast : purchaseForecasts) {
				List<PurchaseForecastDetail> purchaseForecastDetails = baseDao
						.getJdbcTemplate()
						.query("select purchaseforecastdetail.*, ve_uu, ve_contact, ve_contactuu from purchaseforecastDetail "
								+ "left join vendor on pfd_vendcode=ve_code where pfd_pfid=? and ve_uu is not null and nvl(ve_b2benable,0)=1 ",
								new BeanPropertyRowMapper<PurchaseForecastDetail>(PurchaseForecastDetail.class),
								purchaseForecast.getPf_id());
				if (purchaseForecastDetails.size() > 0) {// 存在有效的明细的单上传，明细都无效的不上传
					purchaseForecast.setPurchaseForecastDetails(purchaseForecastDetails);
					forecasts.add(purchaseForecast);
				}
			}
			return forecasts;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 上传采购预测单
	 * 
	 * @return
	 */
	private boolean uploadPurchaseForecasts(Master master) {
		List<PurchaseForecast> purchaseForecasts = getPurchaseForecastsUpload();
		if (!CollectionUtil.isEmpty(purchaseForecasts)) {
			String idStr = CollectionUtil.getKeyString(purchaseForecasts, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchaseForecasts));
			try {
				beforePurchaseForecastUpload(idStr);// 修改状态为上传中
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/forecast?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onPurchaseForecastUploadSuccess(idStr);// 修改状态为已上传
				else
					onPurchaseForecastUploadFail(idStr);// 修改状态为待上传
				baseDao.save(new TaskLog("(买家)采购预测-上传采购预测单", purchaseForecasts.size(), response));
			} catch (Exception e) {
				onPurchaseForecastUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 更新正在上传的采购预测单状态
	 * 
	 * @param idStr
	 *            待上传的采购单
	 */
	public void beforePurchaseForecastUpload(String idStr) {
		baseDao.execute("update purchaseforecast set PF_SENDSTATUS='上传中' where pf_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的采购预测单状态
	 * 
	 * @param idStr
	 *            上传成功的采购单
	 */
	public void onPurchaseForecastUploadSuccess(String idStr) {
		baseDao.execute("update purchaseforecast set PF_SENDSTATUS='已上传' where pf_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的采购预测单状态
	 * 
	 * @param idStr
	 *            上传失败的采购单
	 */
	public void onPurchaseForecastUploadFail(String idStr) {
		baseDao.execute("update purchaseforecast set PF_SENDSTATUS='待上传' where pf_id in (" + idStr + ")");
	}

}
