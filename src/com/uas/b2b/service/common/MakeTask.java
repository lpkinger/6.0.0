package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.Make;
import com.uas.b2b.model.MakeEnd;
import com.uas.b2b.model.MakeReply;
import com.uas.b2b.model.Prod;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.oa.PagingReleaseService;

/**
 * 作为买家ERP，将委外加工单传到平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class MakeTask extends AbstractTask {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PagingReleaseService pagingReleaseService;

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " uploadProduct start");
			if (uploadProduct(master)) {
				logger.info(this.getClass() + " uploadProduct end");
				logger.info(this.getClass() + " uploadMake start");
				uploadMake(master);
				logger.info(this.getClass() + " uploadMake end");
			}
			logger.info(this.getClass() + " downloadReply start");
			downloadReply(master);
			logger.info(this.getClass() + " downloadReply end");
			logger.info(this.getClass() + " uploadMakeEnd start");
			uploadMakeEnd(master); // 上传结案、反结案的委外加工单
			logger.info(this.getClass() + " uploadMakeEnd end");
		} catch (Exception e) {
			e.printStackTrace();
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
				baseDao.save(new TaskLog("(买家)委外加工单-上传物料资料", prods.size(), response));
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
	 * 上传委外加工单
	 * 
	 * @return
	 */
	private boolean uploadMake(Master master) {
		List<Make> makes = getMakesUpload();
		if (!CollectionUtil.isEmpty(makes)) {
			String idStr = CollectionUtil.getKeyString(makes, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(makes));
			try {
				beforeMakeUpload(idStr);
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onMakeUploadSuccess(idStr);
				else
					onMakeUploadFail(idStr);
				baseDao.save(new TaskLog("(买家)委外加工单-上传采购订单", makes.size(), response));
			} catch (Exception e) {
				e.printStackTrace();
				onMakeUploadFail(idStr);
				return false;
			} finally {
				checkMakeUpload(idStr);
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的委外加工单
	 * 
	 * @return
	 */
	public List<Make> getMakesUpload() {
		try {
			List<Make> makes = baseDao
					.getJdbcTemplate()
					.query("select * from (select make.*, ve_uu from make left join vendor on ma_vendcode=ve_code where nvl(ve_b2benable,0)=1 and ve_uu is not null and nvl(ma_tasktype, '')='OS' and ma_checkstatuscode='APPROVE' and (ma_sendstatus='待上传' or ma_sendstatus='上传中')) where rownum <= 100",
							new BeanPropertyRowMapper<Make>(Make.class));
			return makes;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 上传结案、反结案采购订单
	 * 
	 * @return
	 */
	private boolean uploadMakeEnd(Master master) {
		List<MakeEnd> ends = getMakeEndUpload();
		if (!CollectionUtil.isEmpty(ends)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(ends));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make/end?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					updateUploadedMakeEnd(ends);
				}
				baseDao.save(new TaskLog("(买家)委外加工单-上传结案、反结案状态", ends.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取需要上传的结案、反结案采购单明细
	 * 
	 * @return
	 */
	private List<MakeEnd> getMakeEndUpload() {
		try {
			List<MakeEnd> ends = baseDao
					.getJdbcTemplate()
					.query("select * from (select ma_id, ma_code, case when ma_statuscode='FINISH' then 1 else 0 end ma_ended from make where ma_tasktype='OS' and ma_endstatus='待上传' and ma_sendstatus='已上传') where rownum<=200",
							new BeanPropertyRowMapper<MakeEnd>(MakeEnd.class));
			return ends;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
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
					.query("select * from (select pr_id,pr_code,pr_detail,pr_spec,pr_unit,pr_zxbzs,pr_zxdhl,pr_leadtime,pr_ltinstock,pr_brand from product where exists (select 1 from make left join vendor on ve_code = ma_vendcode where ma_tasktype='OS'  and ve_uu is not null and nvl(ve_b2benable,0)=1 and make.ma_prodcode = product.pr_code) and nvl(pr_sendstatus,' ')<>'已上传' order by pr_id) where rownum <= 500",
							new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新待上传的委外加工单状态
	 * 
	 * @param idStr
	 *            待上传的委外加工单
	 */
	public void beforeMakeUpload(String idStr) {
		baseDao.execute("update make set ma_SENDSTATUS='上传中' where ma_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的委外加工单状态
	 * 
	 * @param idStr
	 *            上传成功的委外加工单
	 */
	public void onMakeUploadSuccess(String idStr) {
		baseDao.execute("update make set ma_SENDSTATUS='已上传' where ma_id in (" + idStr + ")");
	}

	/**
	 * 更新上传失败的委外加工单状态
	 * 
	 * @param idStr
	 *            上传失败的委外加工单
	 */
	public void onMakeUploadFail(String idStr) {
		baseDao.execute("update make set ma_SENDSTATUS='待上传' where ma_id in (" + idStr + ") and ma_SENDSTATUS='上传中'");
	}

	public void checkMakeUpload(String idStr) {
		baseDao.execute("update make set ma_SENDSTATUS='待上传' where ma_id in (" + idStr + ") and ma_SENDSTATUS='上传中'");
	}

	/**
	 * 更新已上传的结案、反结案委外加工单状态
	 * 
	 * @param details
	 *            已上传的结案、反结案委外加工单
	 */
	public void updateUploadedMakeEnd(List<MakeEnd> ends) {
		String ids = CollectionUtil.getKeyString(ends, ",");
		String sql = "update make set ma_endstatus='已上传' where ma_id in (" + ids + ")";
		baseDao.execute(sql);
	}

	/**
	 * 从平台下载供应商的回复记录
	 * 
	 * @return
	 */
	private boolean downloadReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/make/reply?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<MakeReply> replies = FlexJsonUtil.fromJsonArray(data, MakeReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveMakeReply(replies, master);
						baseDao.save(new TaskLog("(买家)委外加工单-下载回复", replies.size(), response));
						saveB2BMessage(replies);
					}
				}
			} else
				baseDao.save(new TaskLog("(买家)委外加工单-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 回复成功，保存消息通知
	 *
	 * @param replies
	 */
	private void saveB2BMessage(List<MakeReply> replies) {
		StringBuffer idStringBuffer = new StringBuffer();
		for (MakeReply reply : replies) {
			if (idStringBuffer.length() > 0) {
				idStringBuffer.append(",");
			}
			idStringBuffer.append(reply.getMr_id());
		}
		if (idStringBuffer.length() > 0) {
			// 产生消息
			pagingReleaseService.B2BMsg("Make", String.valueOf(idStringBuffer), "reply");
		}
	}

	/**
	 * 从平台下载供应商回复成功后修改平台里面的回复记录的状态
	 * 
	 * @return
	 */
	private boolean onReplyDownloadSuccess(List<MakeReply> replies, Master master) {
		StringBuffer b2bIdStr = new StringBuffer();
		for (MakeReply reply : replies) {
			if (b2bIdStr.length() > 0)
				b2bIdStr.append(",");
			b2bIdStr.append(reply.getB2b_mr_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", b2bIdStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/make/reply/back?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存回复记录，并更新到委外加工单
	 * 
	 * @param replies
	 */
	public void saveMakeReply(List<MakeReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			for (MakeReply reply : replies) {
				int count = baseDao.getCount("select count(1) from makereply where b2b_mr_id=" + reply.getB2b_mr_id());
				if (count == 0) {
					sqls.add(reply.toSqlString());
					sqls.add("update make set ma_replyqty = nvl(ma_replyqty, 0) + " + reply.getMr_qty() + ", ma_replydelive = "
							+ DateUtil.parseDateToOracleString(null, reply.getMr_delivery()) + ", ma_replydate = "
							+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, reply.getMr_date()) + ", ma_replyremark= '"
							+ StringUtil.nvl(reply.getMr_remark(), "") + "' where ma_code='" + reply.getMr_macode() + "'");
				}
			}
			baseDao.execute(sqls);
			try {
				for (MakeReply reply : replies) {
					Make make = getMakeByCode(reply.getMr_macode());
					if (make != null && make.getMa_recorderid() != null) {
						Employee employee = employeeService.getEmployeeById(make.getMa_recorderid());
						if (employee != null) {
							// XingePusher.pushSingleAccountAndroid(master.getMa_user(), employee.getEm_code(), "委外加工单收到新的回复",
							// "供应商:" + make.getMa_vendname() + ",单号:" + make.getMa_code(), null);
							// XingePusher.pushSingleAccountIOS(master.getMa_user(), employee.getEm_code(),
							// "委外加工单收到新的回复  " + "供应商:" + make.getMa_vendname());
						}
					}
				}
			} catch (Exception e) {

			}
			onReplyDownloadSuccess(replies, master);
		}
	}

	/**
	 * 根据采购单号获取采购单
	 * 
	 * @param pu_code
	 * @return
	 */
	private Make getMakeByCode(String ma_code) {
		return baseDao.getJdbcTemplate().queryForObject("select * from make where ma_code='" + ma_code + "'",
				new BeanPropertyRowMapper<Make>(Make.class));
	}

}