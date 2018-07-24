package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.Date;
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

import com.uas.b2b.model.SaleDownChange;
import com.uas.b2b.model.SaleDownChangeDetail;
import com.uas.b2b.model.SaleDownChangeReply;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2b.SaleDownChangeService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.crm.CustomerService;

/**
 * 作为卖家ERP，获取客户下达到平台的采购变更单、将回复记录上传到平台
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleDownChangeTask extends AbstractTask {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private SaleDownChangeService saleDownChangeService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleDownChange start");
		downloadSaleDownChange(master);
		logger.info(this.getClass() + " downloadSaleDownChange end");
		logger.info(this.getClass() + " downloadChangeReply start");
		downloadChangeReply(master);
		logger.info(this.getClass() + " downloadChangeReply end");
		logger.info(this.getClass() + " uploadSaleChangeReply start");
		uploadSaleChangeReply(master);
		logger.info(this.getClass() + " uploadSaleChangeReply end");
	}

	/**
	 * 从平台下载客户下达到平台的采购变更单
	 * 
	 * @return
	 */
	private boolean downloadSaleDownChange(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/change?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDownChange> changes = FlexJsonUtil.fromJsonArray(data, SaleDownChange.class);
					if (!CollectionUtil.isEmpty(changes)) {
						saveSaleDownChange(changes, master);
						baseDao.save(new TaskLog("(卖家)客户采购变更-下载客户采购变更单", changes.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购变更-下载客户采购变更单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saledownchange
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleDownChange(List<SaleDownChange> changes, Master master) {
		if (!CollectionUtil.isEmpty(changes)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();// b2b_pc_id
			for (SaleDownChange change : changes) {
				int count = baseDao.getCount("select count(1) from SaleDownChange where sc_type='purchase' and b2b_pc_id="
						+ change.getB2b_pc_id());
				if (count == 0) {
					int scId = baseDao.getSeqId("SaleDownChange_SEQ");
					sqls.add(change.toSqlString(scId));
					if (!CollectionUtil.isEmpty(change.getChangeDetails())) {
						for (SaleDownChangeDetail detail : change.getChangeDetails()) {
							sqls.add(detail.toSqlString(scId));
						}
					}
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(scId);
					b2bStr.append(change.getB2b_pc_id());
					// 发起任务
					int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
					String sallerCode = customerService.getSallerCodeByCustomerUU(change.getSc_custuu());
					if (sallerCode != null && !sallerCode.equals("")) {
						Employee employee = employeeService.getEmployeeByEmcode(sallerCode);
						String customerName = customerService.getNameByCustomerUU(change.getSc_custuu());
						if (employee != null) {
							sqls.add("insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
									+ "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) "
									+ " values (" + taskId + ",'客户采购变更单->" + change.getSc_code() + "','normal','已启动','DOING',"
									+ "'已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','" + customerName + "','" + sallerCode + "','"
									+ employee.getEm_name() + "','" + employee.getEm_id() + "','" + baseDao.sGetMaxNumber("ProjectTask", 2)
									+ "','','jsps/b2b/sale/saleDownChange.jsp?formCondition=sc_idIS" + scId + "&gridCondition=sd_saidIS"
									+ scId + "')");
							sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
									+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname,ra_startdate,ra_enddate) values (resourceassignment_seq.nextval,"
									+ taskId + ",'" + employee.getEm_id() + "','" + sallerCode + "','" + employee.getEm_name()
									+ "',1,'进行中','START',100,'billtask','客户采购变更单->" + change.getSc_code() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS,DateUtil.overDate(new Date(),3))+")");

						}
					}
					if (sqls.size() > 0) {// 阶段保存
						baseDao.execute(sqls);
						sqls = new ArrayList<String>();
					}
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update SaleDownChange set (sc_custcode,sc_paymentscode,sc_payments,sc_currency,sc_rate)=(select max(sa_custcode),max(sa_paymentscode),max(sa_payments),max(sa_currency),max(sa_rate) from saledown where sa_code=sc_sacode) where sc_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail sd1 set scd_newprodcode=(select max(pc_prodcode) from productcustomer,saledownchange,saledownchangedetail sd2 where sc_id=scd_scid and pc_custcode=sc_custcode and pc_custprodcode=sd2.scd_newcustprodcode and pc_custproddetail=sd2.scd_newcustproddetail and pc_custprodspec=sd2.scd_newcustprodspec and sd1.scd_id=sd2.scd_id) where scd_scid in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail set scd_sacode=(select sc_sacode from SaleDownChange where sc_id=scd_scid) where scd_scid in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleDownChangeDetail set (scd_prodcode,scd_custprodcode,scd_qty,scd_price,scd_taxrate,scd_delivery)=(select max(sd_prodcode),max(sd_custprodcode),max(sd_qty),max(sd_price),max(sd_taxrate),max(sd_delivery) from saledowndetail left join saledown on sd_said=sa_id where sa_code=scd_sacode and sd_detno=scd_sddetno) where scd_scid in ("
						+ idStr.toString() + ")");
				baseDao.execute(sqls);
				try {
					// for (SaleDownChange change : changes) {
					// String sellerCode = customerService.getSallerCodeByCustomerUU(change.getSc_custuu());
					// String customerName = customerService.getNameByCustomerUU(change.getSc_custuu());
					// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
					// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一条客户采购变更单  ", customerName + " "
					// + timeFormat.format(change.getSc_indate()), null);
					// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
					// "新增一条客户采购变更单  " + customerName + " " + timeFormat.format(change.getSc_indate()));
					// }
				} catch (Exception e) {

				}
				onSaleDownChangeSuccess(b2bStr.toString(), master);
			}
		}
	}

	/**
	 * 成功写到saledownchange之后，修改平台的采购变更单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleDownChangeSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/change?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 直接在平台回复的信息，修改到卖家ERP的客户采购变更单、客户采购单
	 */
	private boolean downloadChangeReply(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/change/reply?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleDownChangeReply> replies = FlexJsonUtil.fromJsonArray(data, SaleDownChangeReply.class);
					if (!CollectionUtil.isEmpty(replies)) {
						saveChangeReply(replies, master);
						baseDao.save(new TaskLog("(卖家)客户采购变更-下载回复", replies.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户采购变更-下载回复", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 从平台下载的回复信息，更新到客户采购变更单里面
	 */
	private void saveChangeReply(List<SaleDownChangeReply> replies, Master master) {
		if (!CollectionUtil.isEmpty(replies)) {
			List<String> sqls = new ArrayList<String>();
			for (SaleDownChangeReply reply : replies) {
				SqlRowList rs = baseDao
						.queryForRowSet("select sc_id,sc_statuscode from saledownchange where sc_code=?", reply.getSc_code());
				if (rs.next() && !Status.AUDITED.code().equals(rs.getString("sc_statuscode"))) {
					sqls.add("update saledownchange set sc_agreed=" + NumberUtil.nvl(reply.getSc_agreed(), 0) + ",sc_replyremark='"
							+ StringUtil.nvl(reply.getSc_replyremark(), "") + "',sc_status='" + Status.AUDITED.display()
							+ "',sc_statuscode='" + Status.AUDITED.code() + "',sc_sendstatus='已下载' where sc_id=" + rs.getObject("sc_id"));
					if (reply.isAgreed())
						saleDownChangeService.onChangeAgreed(reply.getSc_code());
				}
			}
			baseDao.execute(sqls);
			onDownloadReplySuccess(replies, master);
		}
	}

	/**
	 * 将从平台下载的回复记录保存成功之后，回执给平台
	 * 
	 * @param replies
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadReplySuccess(List<SaleDownChangeReply> replies, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (SaleDownChangeReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getB2b_pc_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/change/reply/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传SaleDownChange回复记录到平台
	 * 
	 * @return
	 */
	private boolean uploadSaleChangeReply(Master master) {
		List<SaleDownChangeReply> replies = getSaleChangeReply();
		if (!CollectionUtil.isEmpty(replies)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(replies));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/change/reply?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) // 上传成功修改回复记录状态
					onChangeReplySuccess(replies);
				baseDao.save(new TaskLog("(卖家)客户采购变更-上传回复", replies.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的回复信息
	 * 
	 * @return
	 */
	private List<SaleDownChangeReply> getSaleChangeReply() {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select b2b_pc_id,sc_id,sc_code,sc_agreed,sc_replyremark from SaleDownChange where SC_SENDSTATUS='待上传' and sc_agreed is not null and sc_custuu is not null",
							new BeanPropertyRowMapper<SaleDownChangeReply>(SaleDownChangeReply.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 回复信息成功传到平台之后
	 */
	private void onChangeReplySuccess(List<SaleDownChangeReply> replies) {
		StringBuffer idStr = new StringBuffer();
		for (SaleDownChangeReply reply : replies) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(reply.getSc_id());
		}
		baseDao.execute("update SaleDownChange set sc_sendstatus='已下载' where sc_id in (" + idStr.toString() + ")");
	}

}
