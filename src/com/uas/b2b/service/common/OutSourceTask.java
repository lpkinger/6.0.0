package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.SaleDown;
import com.uas.b2b.model.SaleDownDetail;
import com.uas.b2b.model.SaleDownDetailEnd;
import com.uas.b2b.model.SaleReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.crm.CustomerService;

/**
 * 作为卖家ERP，获取客户下达到平台的委外加工单、将回复记录上传到平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class OutSourceTask extends AbstractTask {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private EmployeeService employeeService;

	/**
	 * 最大允许的明细条数
	 */
	private static final int max_size = 2000;

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadOutsource start");
		downloadOutsource(master);
		logger.info(this.getClass() + " downloadOutsource end");
		logger.info(this.getClass() + " uploadMakeReply start");
		uploadMakeReply(master);
		logger.info(this.getClass() + " uploadMakeReply end");
		logger.info(this.getClass() + " downloadMakeReply start");
		downloadMakeReply(master);
		logger.info(this.getClass() + " downloadMakeReply end");
		logger.info(this.getClass() + " downloadMakeDownEnd start");
		downloadMakeDownEnd(master);
		logger.info(this.getClass() + " downloadMakeDownEnd end");
	}

	/**
	 * 从平台下载客户下达到平台的委外加工单
	 * 
	 * @return
	 */
	private boolean downloadOutsource(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outsource?access_id=" + master.getMa_uu(), null,
					true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDown> sales = FlexJsonUtil.fromJsonArray(data, SaleDown.class);
					if (!CollectionUtil.isEmpty(sales)) {
						saveOutsource(sales, master);
						baseDao.save(new TaskLog("(卖家)客户委外-下载客户委外加工单", sales.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户委外-下载客户委外加工单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存委外销售单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveOutsource(List<SaleDown> sales, Master master) {
		if (!CollectionUtil.isEmpty(sales)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_pu_id
			List<SaleDown> validSales = new ArrayList<SaleDown>();
			for (SaleDown sale : sales) {
				int count = baseDao
						.getCount("select count(1) from SaleDown where sa_type='outsource' and b2b_pu_id=" + sale.getB2b_pu_id());
				if (count == 0) {
					int saId = baseDao.getSeqId("SaleDown_SEQ");
					sqls.add(sale.toOutsourceSqlString(saId));
					if (!CollectionUtil.isEmpty(sale.getSaleDownDetails())) {
						for (SaleDownDetail detail : sale.getSaleDownDetails()) {
							sqls.add(detail.toSqlString(saId));
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(saId);
					b2bStr.append(sale.getB2b_pu_id());
					// 发起任务
					int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
					String sallerCode = null;
					if (sale.getSa_useruu() != 0) {
						Employee saller = employeeService.getEmployeeByUu(sale.getSa_useruu());
						if (saller != null) {
							sallerCode = saller.getEm_code();
						}
					}
					if (sallerCode == null)
						sallerCode = customerService.getSallerCodeByCustomerUU(sale.getSa_customeruu());
					if (sallerCode != null && !sallerCode.equals("")) {
						String customerName = customerService.getNameByCustomerUU(sale.getSa_customeruu());
						sqls.add("insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
								+ "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) " + " values ("
								+ taskId + ",'客户委外加工单->" + sale.getSa_code() + "','normal','已启动','DOING',"
								+ "'已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','" + customerName + "','" + sallerCode + "','','','"
								+ baseDao.sGetMaxNumber("ProjectTask", 2) + "','','jsps/b2b/sale/saleDown.jsp?formCondition=sa_idIS" + saId
								+ "&gridCondition=sd_saidIS" + saId + "')");
						sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
								+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values (resourceassignment_seq.nextval," + taskId
								+ ",'','" + sallerCode + "','',1,'进行中','START',100,'billtask','客户委外加工单->" + sale.getSa_code() + "')");
					}
					validSales.add(sale);
					if (sqls.size() > max_size) {// 阶段保存
						baseDao.execute(sqls);
						sqls = new ArrayList<String>();
					}
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update SaleDown set (sa_custid,sa_custcode,sa_custname,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_sellerid,sa_seller,sa_sellercode)=(select cu_id,cu_code,cu_name,cu_arcode,cu_arname,cu_shcustcode,cu_shcustname,cu_sellerid,cu_sellername,em_code from customer left join employee on em_id=cu_sellerid where cu_uu=sa_customeruu) where sa_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownDetail sd1 set (sd_prodid,sd_prodcode)=(select max(pc_prodid),max(pc_prodcode) from productcustomer,saledown,saledowndetail sd2 where sa_id=sd_said and pc_custcode=sa_custcode and pc_custprodcode=sd2.sd_custprodcode and pc_custproddetail=sd2.sd_custproddetail and pc_custprodspec=sd2.sd_custprodspec and sd1.sd_id=sd2.sd_id) where sd_said in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				/*
				 * for (SaleDown sale : validSales) { String sellerCode = null; // 单据存在供应商联系人uu，根据uu从Employee表获取对应的code if (sale.getSa_useruu() != 0) { Employee employee = employeeService.getEmployeeByUu(sale.getSa_useruu()); if (employee != null) { sellerCode = employee.getEm_code(); } } // 单据中不存在供应商联系人uu或根据uu获取code失败，取客户表(customer)中客户对应业务员的code if (sellerCode == null || sellerCode.equals("")) sellerCode = customerService.getSallerCodeByCustomerUU(sale.getSa_customeruu()); String customerName =
				 * customerService.getNameByCustomerUU(sale.getSa_customeruu());// 根据客户uu获取客户名称 SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss"); XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一条客户委外订单", customerName + " " + timeFormat.format(sale.getSa_date()), null); XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode, "新增一条客户委外订单  " + customerName + " " + timeFormat.format(sale.getSa_date())); }
				 */
				onSaleDownSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到saledown之后，修改平台的委外加工单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleDownSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/outsource?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传SaleDown 加工单在ERP上的回复记录到平台
	 * 
	 * @return
	 */
	private boolean uploadMakeReply(Master master) {
		List<SaleReply> replies = getSaleReply();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/outsource/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onReplySuccess(replies);
				baseDao.save(new TaskLog("(卖家)客户委外-上传在ERP上的回复", replies.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的回复记录
	 * 
	 * @return
	 */
	private List<SaleReply> getSaleReply() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select SaleReply.*,SaleDown.sa_customeruu cu_uu from SaleReply left join SaleDown on sr_sacode=sa_code where SR_SENDSTATUS='待上传' and sa_type='outsource' and sa_customeruu is not null",
							new BeanPropertyRowMapper<SaleReply>(SaleReply.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 回复记录成功传到平台之后
	 */
	private void onReplySuccess(List<SaleReply> replies) {
		StringBuffer idStr = new StringBuffer();
		for (SaleReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getSr_id());
		}
		baseDao.execute("update SaleReply set sr_sendstatus='已下载' where sr_id in (" + idStr.toString() + ")");
	}

	/**
	 * 下载在平台回复的委外加工单回复记录，保存到卖家ERP
	 */
	private boolean downloadMakeReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outsource/reply?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleReply> replies = FlexJsonUtil.fromJsonArray(data, SaleReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveReply(replies, master);
						baseDao.save(new TaskLog("(卖家)客户委外-下载平台上的回复", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户委外-下载平台上的回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存从平台下载的委外加工单回复记录
	 */
	private void saveReply(List<SaleReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			for (SaleReply reply : replies) {
				sqls.add("MERGE into SaleDownDetail a using (select count(1) c from SaleReply where b2b_pr_id=" + reply.getB2b_pr_id()
						+ ") b on (b.c=0) when MATCHED THEN update set sd_replyqty = nvl(sd_replyqty, 0) + " + reply.getSr_qty()
						+ ", sd_replydate = " + DateUtil.parseDateToOracleString(null, reply.getSr_delivery()) + ", sd_replydetail='"
						+ StringUtil.nvl(reply.getSr_remark(), "") + "' where b2b_pd_id=" + reply.getB2b_pd_id() + " and sd_code='"
						+ reply.getSr_sacode() + "' and sd_detno=" + reply.getSr_sddetno());
				sqls.add("MERGE into SaleReply a using (select count(1) c from SaleReply where b2b_pr_id="
						+ reply.getB2b_pr_id()
						+ ") b on (b.c>0) when NOT MATCHED THEN insert (b2b_pr_id,sr_qty,sr_delivery,sr_remark,sr_sacode,sr_sddetno,sr_date,sr_recorder,sr_type,sr_sendstatus) values ("
						+ reply.getB2b_pr_id() + "," + +reply.getSr_qty() + ","
						+ DateUtil.parseDateToOracleString(null, reply.getSr_delivery()) + ",'" + StringUtil.nvl(reply.getSr_remark(), "")
						+ "','" + reply.getSr_sacode() + "'," + reply.getSr_sddetno() + ","
						+ DateUtil.parseDateToOracleString(null, reply.getSr_date()) + ",'" + reply.getSr_recorder() + "','"
						+ StringUtil.nvl(reply.getSr_type(), "") + "','已下载')");
			}
			baseDao.execute(sqls);
			onDownloadReplySuccess(replies, master);
		}
	}

	/**
	 * 将从平台下载的委外加工单回复记录保存成功之后，回执给平台
	 * 
	 * @param replies
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplySuccess(List<SaleReply> replies, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (SaleReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getB2b_pr_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/outsource/reply/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从平台下载客户下达到平台的结案、反结案委外加工单
	 * 
	 * @return
	 */
	private boolean downloadMakeDownEnd(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/outsource/end?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDownDetailEnd> ends = FlexJsonUtil.fromJsonArray(data, SaleDownDetailEnd.class);
					if (!CollectionUtil.isEmpty(ends)) {
						saveSaleDownEnd(ends, master);
						baseDao.save(new TaskLog("(卖家)客户委外-下载结案、反结案状态", ends.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户委外-下载结案、反结案状态", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 结案、反结案委外加工单
	 * 
	 * @param ends
	 * @param enterprise
	 */
	private void saveSaleDownEnd(List<SaleDownDetailEnd> ends, Master master) {
		if (!CollectionUtil.isEmpty(ends)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bStr = new StringBuffer();// b2b_pd_id
			for (SaleDownDetailEnd end : ends) {
				Status status = end.isEnded() ? Status.FINISH : Status.AUDITED;
				sqls.add("update SaleDownDetail set sd_mrpstatus='" + status.display() + "',sd_mrpstatuscode='" + status.code()
						+ "' where b2b_pd_id=" + end.getB2b_pd_id() + " and sd_code='" + end.getSd_code() + "' and sd_detno="
						+ end.getSd_detno());
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(end.getB2b_pd_id());
				try {
					String sellerCode = null;
					sellerCode = this.getSellerCodeByB2BPuid(end.getB2b_pd_id());
					if (sellerCode == null || sellerCode.endsWith(""))
						sellerCode = customerService.getSallerCodeByCustomerUU(end.getCu_uu());
					String customerName = customerService.getNameByCustomerUU(end.getCu_uu());
					// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
					// TODO 添加客户结案委外加工单到知会信息
					// 发起任务提醒业务员去结案销售订单
					// 发起任务
					int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
					if (sellerCode != null && !sellerCode.equals("")) {
						sqls.add("insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
								+ "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) " + " values ("
								+ taskId + ",'客户委外加工单结案->" + end.getSd_code() + "','normal','已启动','DOING',"
								+ "'已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','" + customerName + "','" + sellerCode + "','','','"
								+ baseDao.sGetMaxNumber("ProjectTask", 2) + "','','jsps/b2b/sale/saleDown.jsp')");
						sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
								+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values (resourceassignment_seq.nextval," + taskId
								+ ",'','" + sellerCode + "','',1,'进行中','START',100,'billtask','客户委外加工单结案->" + end.getSd_code() + "')");
					}
				} catch (Exception e) {

				}
			}
			baseDao.execute(sqls);
			onSaleDownEndSuccess(b2bStr.toString(), master);
		}
	}

	/**
	 * 将从平台下载的结案、反结案采购单操作完成之后，回执给平台
	 * 
	 * @param idStr
	 * @param enterprise
	 * @return
	 */
	private boolean onSaleDownEndSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/outsource/end/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	public String getSellerCodeByB2BPuid(Long b2bPuid) {
		String sellerCode = baseDao
				.getFieldValue("saledown", "sa_sellercode", "sa_type='outsource' and b2b_pu_id=" + b2bPuid, String.class);
		return sellerCode;
	}

}
