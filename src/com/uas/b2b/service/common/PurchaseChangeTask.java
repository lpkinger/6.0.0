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
import com.uas.b2b.model.PurchaseChange;
import com.uas.b2b.model.PurchaseChangeDetail;
import com.uas.b2b.model.PurchaseChangeReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.model.Master;
import com.uas.erp.service.oa.PagingReleaseService;
import com.uas.erp.service.scm.PurchaseChangeService;

@Component
@EnableAsync
@EnableScheduling
public class PurchaseChangeTask extends AbstractTask {
	@Autowired
	private PurchaseChangeService purchaseChangeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadProduct start");
		if (uploadProduct(master)) {
			logger.info(this.getClass() + " uploadProduct end");
			logger.info(this.getClass() + " uploadPurchaseChange start");
			uploadPurchaseChange(master);
			logger.info(this.getClass() + " uploadPurchaseChange end");
		}
		logger.info(this.getClass() + " downloadChangeReply start");
		downloadChangeReply(master);
		logger.info(this.getClass() + " downloadChangeReply end");
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
				baseDao.save(new TaskLog("(买家)采购变更-上传物料资料", prods.size(), response));
			} catch (Exception e) {
				onProductUploadFail(idStr);
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传物料
	 * 
	 * @return
	 */
	public List<Prod> getProducts() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select * from (select distinct product.* from product where exists (select 1 from purchasechange left join purchase on pc_purccode=pu_code left join vendor on pu_vendcode=ve_code left join purchasechangedetail on pcd_pcid=pc_id where PC_SENDSTATUS='待上传' and nvl(pu_ordertype,' ')<>'B2C' and nvl(pc_agreed,1)<>0 and pc_statuscode in ('AUDITED','TO_CONFIRM') and ve_uu is not null and nvl(ve_b2benable,0)=1 and pcd_newprodcode=pr_code) and pr_statuscode='AUDITED' and nvl(pr_sendstatus,' ')<>'已上传') where rownum <= 500",
							new BeanPropertyRowMapper<Prod>(Prod.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
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
	 * 上传采购变更单
	 * 
	 * @return
	 */
	private boolean uploadPurchaseChange(Master master) {
		List<PurchaseChange> changes = getPurchaseChangeUpload();
		if (!CollectionUtil.isEmpty(changes)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(changes));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/change?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedPurchaseChange(changes);
				}
				baseDao.save(new TaskLog("(买家)采购变更-上传采购变更单", changes.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的采购变更单
	 * 
	 * @return
	 */
	public List<PurchaseChange> getPurchaseChangeUpload() {
		try {
			List<PurchaseChange> purchaseChanges = baseDao
					.getJdbcTemplate()
					.query("select purchasechange.* from purchasechange left join Purchase on pc_purccode=pu_code left join vendor on ve_code=pu_vendcode where PC_SENDSTATUS='待上传' and pu_sendstatus='已上传' and nvl(pu_ordertype,' ')<>'B2C' and nvl(pc_agreed,1)<>0 and pc_statuscode in ('AUDITED','TO_CONFIRM') and ve_uu is not null and nvl(ve_b2benable,0)=1 order by pc_code",
							new BeanPropertyRowMapper<PurchaseChange>(PurchaseChange.class));
			for (PurchaseChange purchaseChange : purchaseChanges) {
				List<PurchaseChangeDetail> changeDetails = baseDao.getJdbcTemplate().query(
						"select * from purchasechangedetail where pcd_pcid=?",
						new BeanPropertyRowMapper<PurchaseChangeDetail>(PurchaseChangeDetail.class), purchaseChange.getPc_id());
				purchaseChange.setChangeDetails(changeDetails);
			}
			return purchaseChanges;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新已上传的采购订单变更单状态
	 * 
	 * @param changes
	 *            已上传的采购订单变更单
	 */
	public void updateUploadedPurchaseChange(List<PurchaseChange> changes) {
		for (PurchaseChange change : changes) {
			baseDao.execute("update purchasechange set PC_SENDSTATUS='已上传' where pc_id = ?", change.getPc_id());
		}
	}

	/**
	 * 从平台下载供应商对变更单的回复信息
	 * 
	 * @return
	 */
	private boolean downloadChangeReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/change/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<PurchaseChangeReply> replies = FlexJsonUtil.fromJsonArray(data, PurchaseChangeReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						savePurchaseChangeReply(replies, master);
						baseDao.save(new TaskLog("(买家)采购变更-下载回复", replies.size(), response));
						saveB2BMessage(replies);
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)采购变更-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存成功，保存消息通知
	 *
	 * @param replies
	 */
	private void saveB2BMessage(List<PurchaseChangeReply> replies) {
		for (PurchaseChangeReply reply : replies) {
			// 产生消息
			Long pc_id = baseDao.getFieldValue("purchasechange", "pc_id", "pc_code='" + reply.getPc_code(), Long.class);
			pagingReleaseService.B2BMsg("PurchaseChange", String.valueOf(pc_id), "confirm");
		}

	}

	/**
	 * 保存回复记录，并更新到采购单明细
	 * 
	 * @param replies
	 */
	public void savePurchaseChangeReply(List<PurchaseChangeReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			for (PurchaseChangeReply reply : replies) {
				sqls.add("update purchasechange set pc_agreed=" + NumberUtil.nvl(reply.getPc_agreed(), 0) + ",pc_replyremark='"
						+ StringUtil.nvl(reply.getPc_replyremark(), "") + "' where pc_code='" + reply.getPc_code() + "'");
				if (reply.isAgreed())
					purchaseChangeService.onChangeAgreed(reply.getPc_code());
				else
					sqls.add("update purchasechange set pc_agreed=0,pc_status='" + Status.CONFIRMED.display() + "',pc_statuscode='"
							+ Status.CONFIRMED.code() + "' where pc_code='" + reply.getPc_code() + "'");
			}
			baseDao.execute(sqls);
			/*
			 * try { for (PurchaseChangeReply reply : replies) { // Purchase purchase =
			 * getPurchaseByChangeCode(reply.getPc_code()); // Employee employee =
			 * employeeService.getEmployeeById(purchase.getPu_buyerid()); //
			 * XingePusher.pushSingleAccountAndroid(master.getMa_user(), employee.getEm_code(), "采购变更单收到回复 ", // "供应商:"
			 * + purchase.getPu_vendname() + ",单号:" + purchase.getPu_code(), null); //
			 * XingePusher.pushSingleAccountIOS(master.getMa_user(), employee.getEm_code(), // "采购变更单收到回复  " + "供应商:" +
			 * purchase.getPu_vendname()); } } catch (Exception e) {
			 * 
			 * }
			 */
			onChangeReplySuccess(replies, master);
		}
	}

	/**
	 * 根据采购变更单号获取采购单
	 * 
	 * @param pc_code
	 *            变更单号
	 * @return
	 */
	// private Purchase getPurchaseByChangeCode(String pc_code) {
	// return baseDao.getJdbcTemplate().queryForObject(
	// "select * from purchase where pu_code=(select pc_purccode from purchasechange where pc_code=?)",
	// new BeanPropertyRowMapper<Purchase>(Purchase.class), pc_code);
	// }

	/**
	 * 修改平台里面的采购变更单的回复状态
	 * 
	 * @return
	 */
	private boolean onChangeReplySuccess(List<PurchaseChangeReply> replies, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (PurchaseChangeReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getB2b_pc_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/purchase/change/reply?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
