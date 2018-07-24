package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Prod;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;

/**
 * 设置轮询，定时更新物料信息
 * 
 * @author hejq
 * @time 创建时间：2017年5月27日
 */
@Component
@EnableAsync
@EnableScheduling
public class ProductTask extends AbstractTask {

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadProduct start");
			uploadProduct(master);
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " downloadProduct start");
			downloadProduct(master);
			logger.info(this.getClass() + " downloadProduct end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载更新物料信息
	 * @param master
	 * @return
	 */
	private boolean downloadProduct(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/product/backtouas?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<Prod> prods = FlexJsonUtil.fromJsonArray(data, Prod.class);
					if (!CollectionUtil.isEmpty(prods)) {
						saveProdDown(prods, master);
						baseDao.save(new TaskLog("物料资料-下载更新物料资料", prods.size(), response));
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
	 * @param prods
	 * @param master
	 */
	private void saveProdDown(List<Prod> prods, Master master) {
		if(!CollectionUtil.isEmpty(prods)){
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bStr = new StringBuffer();// b2b_id
			for(Prod prod:prods) {
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(prod.getB2b_id());
				String sql = "update product set pr_uuid = '" + prod.getPr_uuid() + "' where pr_id = " +prod.getPr_id();
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
	 * 更新平台的下载状态为已下载
	 * 
	 * @param b2bStr
	 * @param master
	 */
	private boolean downloadSuccess(String b2bStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", b2bStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/product/refreshDownloadstatus?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传物料
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
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/product/cycleupdate?access_id=" + master.getMa_uu(), params,
						true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					onProductUploadSuccess(idStr);
					return uploadProduct(master);// 递归传所有物料
				} else
					onProductUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)物料定时更新-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onProductUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 物料上传失败
	 * 
	 * @param idStr
	 */
	private void onProductUploadFail(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='待上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 物料上传成功
	 * 
	 * @param idStr
	 */
	private void onProductUploadSuccess(String idStr) {
		baseDao.execute("update product set PR_SENDSTATUS='已上传' where pr_id in (" + idStr + ")");
	}

	/**
	 * 获取待上传的物料信息
	 * 
	 * @return
	 */
	private List<Prod> getProducts() {
		try {
			List<Prod> prods = baseDao.getJdbcTemplate().query(
					"select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand,pr_issale,pr_ispurchase,pr_isshow,pr_ispubsale,pr_uuid,pr_orispeccode from product where  nvl(pr_sendstatus,' ')<>'已上传' and  nvl(pr_status, ' ') = '已审核' order by pr_id) where rownum <= 500",
					new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
