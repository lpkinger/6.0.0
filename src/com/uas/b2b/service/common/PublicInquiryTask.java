package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.Attach;
import com.uas.b2b.model.BatchInProduct;
import com.uas.b2b.model.BatchInquiry;
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

/**
 * 公共询价轮询接口
 * 
 * @author hejq
 * @time 创建时间：2017年9月15日
 */
@Component
@EnableAsync
@EnableScheduling
public class PublicInquiryTask extends AbstractTask{

	@Async
	public void execute() {
		super.execute();
	}
	
	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadProduct start");
			if(uploadProduct(master)) {
				logger.info(this.getClass() + " uploadProduct end");
				logger.info(this.getClass() + " uploadInquiry start");
				// 上传公共询价单
				uploadInquiry(master);
				logger.info(this.getClass() + " uploadInquiry end");
				logger.info(this.getClass() + " uploadInquiryAfterCheck start");
				// 上传公共询价单（已审核过后，传回平台更新状态）
				uploadInquiryAfterCheck(master);
				logger.info(this.getClass() + " uploadInquiryAfterCheck end");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传已提交审核的公共询价单（更新平台状态，不能再报价）
	 * 
	 * @param master
	 */
	private boolean uploadInquiryAfterCheck(Master master) {
		List<BatchInquiry> inquiries = getInquiryAfterCheck();
		if (!CollectionUtil.isEmpty(inquiries)) {
			String idStr = CollectionUtil.getKeyString(inquiries, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(inquiries));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/publicinquiry/check?access_id=" + master.getMa_uu(), params,
						true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInquiryAfterCheckUploadSuccess(idStr);
				}
				baseDao.save(new TaskLog("(买家)公共询价单-上传询价单（已提交审核）", inquiries.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return true;
	}

	/**
	 * 上传成功后更新上传状态
	 * 
	 * @param ids
	 */
	private void onInquiryAfterCheckUploadSuccess(String ids) {
		baseDao.execute("update batchinquiry set bi_checksendstatus = '已上传' where bi_id in (" + ids + ")");
	}

	/**
	 * 获取已提交审核的公共询价单（根据erp需求逻辑，这边提交主表，所以只用单号去维护平台就可以）
	 * 
	 * @return
	 */
	private List<BatchInquiry> getInquiryAfterCheck() {
		List<BatchInquiry> inquiries = baseDao.getJdbcTemplate().query("select bi_id,bi_code from batchinquiry where bi_checksendstatus = '待上传'", new BeanPropertyRowMapper<BatchInquiry>(BatchInquiry.class));
		return inquiries;
	}

	/**
	 * 上传公共询价单
	 * 
	 * @param master
	 */
	private boolean uploadInquiry(Master master) {
		List<BatchInquiry> inquiries = getInquiryUpload();
		if (!CollectionUtil.isEmpty(inquiries)) {
			String idStr = CollectionUtil.getKeyString(inquiries, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(inquiries));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/publicinquiry?access_id=" + master.getMa_uu(), params,
						true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onInquiryUploadSuccess(idStr);
				}
				baseDao.save(new TaskLog("(买家)公共询价单-上传询价单", inquiries.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return true;
	}

	/**
	 * 公共询价单上传成功后更新状态
	 * 
	 * @param idStr
	 */
	private void onInquiryUploadSuccess(String ids) {
		baseDao.execute("update batchinquiry set bi_sendstatus='已上传' where bi_id in (" + ids + ")");		
	}

	/**
	 * 获取需要上传的公共询价单
	 * 
	 * @return
	 */
	private List<BatchInquiry> getInquiryUpload() {
		String sql = "select bi_id,bi_code,bi_date,bi_kind,bi_environment,bi_purpose,bi_pricekind,bi_recorder,bi_recorddate,bi_enddate,bi_remark,bi_pricetype,bi_recorduu,bi_attach from batchinquiry where bi_sendstatus = '待上传' and bi_status = '已审核' and bi_kind = '公开询价' and rownum <= 10";
		List<BatchInquiry> inquiries = baseDao.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<BatchInquiry>(BatchInquiry.class));
		if(!CollectionUtil.isEmpty(inquiries)) {
			for (BatchInquiry inquiry : inquiries) {
				List<BatchInProduct> prods = baseDao.getJdbcTemplate().query(
						"select * from batchinprod where bip_biid = ?",
						new BeanPropertyRowMapper<BatchInProduct>(BatchInProduct.class), inquiry.getBi_id());
				inquiry.setInProducts(prods);
				// 获取询价单的附件信息
				if (StringUtil.hasText(inquiry.getIn_attach())) {
					String[] fileIds = inquiry.getIn_attach().split(";");
					String erpUrl = getEnterpriseErpUrl();
					List<Attach> attaches = baseDao.getJdbcTemplate()
							.query("select fp_id, fp_size, fp_name from filepath where fp_id in ("
									+ StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
							new BeanPropertyRowMapper<Attach>(Attach.class));
					if (!CollectionUtil.isEmpty(attaches)) {
						for (Attach attach : attaches) {
							attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
						}
						inquiry.setAttaches(attaches);
					}
				}
			}
		}
		return inquiries;
	}

	/**
	 * 获取企业UAS外网地址
	 * @return
	 */
	private String getEnterpriseErpUrl() {
		String erpUrl = baseDao.getJdbcTemplate().queryForObject("select max(en_erpurl) from enterprise", String.class);
		if (!StringUtils.isEmpty(erpUrl) && erpUrl.endsWith("/")) {
			erpUrl = erpUrl.substring(0, erpUrl.length() - 1);
		}
		return erpUrl;
	}

	/**
	 * 上传物料资料
	 * 
	 * @param master
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
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/product?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductUploadSuccess(idStr);
					return uploadProduct(master);// 递归传所有物料
				} else
					onProductUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)公共询价单-上传物料资料", prods.size(), response));
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
	 * 上传物料;这里是查询所有物料信息（已审核）；更新平台物料库的状态
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			return baseDao.getJdbcTemplate().query(
					"select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where pr_code in "
					+ "(select distinct(pr_code) from product p left join batchinprod b on p.pr_code = b.bip_prodcode left join batchinquiry i on b.bip_biid = i.bi_id where p.pr_sendstatus = '待上传' and i.bi_sendstatus = '待上传' and rownum < 500)",
					new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
