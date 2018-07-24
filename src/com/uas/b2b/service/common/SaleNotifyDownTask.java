package com.uas.b2b.service.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.SaleNotifyDown;
import com.uas.b2b.model.SaleNotifyDownEnd;
import com.uas.b2b.model.SaleOut;
import com.uas.b2b.model.SaleOutDetail;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.crm.CustomerService;

/**
 * 作为卖家ERP，获取客户下达到平台的送货提醒、将发货单上传到平台
 * 
 * @author yingp
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleNotifyDownTask extends AbstractTask {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CustomerService customerService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleNotifyDown start");
		downloadSaleNotifyDown(master);
		logger.info(this.getClass() + " downloadSaleNotifyDown end");
		logger.info(this.getClass() + " downloadSaleOut start");
		downloadSaleOut(master);
		logger.info(this.getClass() + " downloadSaleOut end");
		logger.info(this.getClass() + " uploadSaleOut start");
		uploadSaleOut(master);
		logger.info(this.getClass() + " uploadSaleOut end");
		logger.info(this.getClass() + " downloadNotifyDownEnd start");
		downloadNotifyDownEnd(master);
		logger.info(this.getClass() + " downloadNotifyDownEnd end");
	}

	/**
	 * 从平台下载客户下达到平台的送货提醒
	 * 
	 * @return
	 */
	private boolean downloadSaleNotifyDown(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/notice?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleNotifyDown> downs = FlexJsonUtil.fromJsonArray(data, SaleNotifyDown.class);
					if (!CollectionUtil.isEmpty(downs)) {
						saveSaleNotifyDown(downs, master);
						baseDao.save(new TaskLog("(卖家)客户送货提醒-下载客户送货提醒", downs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户送货提醒-下载客户送货提醒", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saleNotifyDown
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleNotifyDown(List<SaleNotifyDown> downs, Master master) {
		if (!CollectionUtil.isEmpty(downs)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();
			for (SaleNotifyDown down : downs) {
				// 获取推送对应的用户编号
				Long selleruu = baseDao.getFieldValue("saledown", "sa_selleruu", "sa_customeruu = " + down.getSn_custuu()
						+ " and sa_code = '" + down.getSn_pocode() + "'", Long.class);
				String sellerCode = null;
				String customerName = customerService.getNameByCustomerUU(down.getSn_custuu());
				if (selleruu != null && selleruu != 0 && employeeService.getEmployeeByUu(selleruu) != null) {
					sellerCode = employeeService.getEmployeeByUu(selleruu).getEm_code();
				}
				if (sellerCode == null)
					sellerCode = customerService.getSallerCodeByCustomerUU(down.getSn_custuu());
				int count = baseDao.getCount("select count(1) from SaleNotifyDown where b2b_pn_id=" + down.getB2b_pn_id());
				if (count == 0) {
					int sdId = baseDao.getSeqId("SaleNotifyDown_SEQ");
					sqls.add(down.toSqlString(sdId));
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(sdId);
					b2bStr.append(down.getB2b_pn_id());
					down.setSn_id(sdId);
					// 发起任务
					if (sellerCode != null) {
						int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
						sqls.add("insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
								+ "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) " + " values ("
								+ taskId + ",'发货提醒->" + down.getSn_pocode() + "','normal','已启动','DOING',"
								+ "'已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','" + customerName + "','" + sellerCode + "','','','"
								+ baseDao.sGetMaxNumber("ProjectTask", 2) + "','','jsps/b2b/sale/zdQu.jsp" + "')");
						sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
								+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values (resourceassignment_seq.nextval," + taskId
								+ ",'','" + sellerCode + "','',1,'进行中','START',100,'billtask','发货提醒->" + down.getSn_pocode() + "')");
						// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一个客户送货提醒 ", customerName + " "
						// + timeFormat.format(new Date()), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
						// "新增一个客户送货提醒  " + customerName + " " + timeFormat.format(new Date()));
					}
				} else {// 修改送货提醒的交期和数量
					if (b2bStr.length() > 0) {
						b2bStr.append(",");
					}
					b2bStr.append(down.getB2b_pn_id());
					sqls.add("update SaleNotifyDown set sn_delivery="
							+ (down.getSn_delivery() != null ? DateUtil.parseDateToOracleString(null, down.getSn_delivery()) : "null")
							+ ", sn_qty=" + down.getSn_qty() + " where b2b_pn_id=" + down.getB2b_pn_id());
					// 推送语句
					if (sellerCode != null) {
						// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "客户修改送货提醒 ", "采购订单编号："
						// + down.getSn_pocode(), null);
						// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
						// "客户修改送货提醒  " + "采购订单编号：" + down.getSn_pocode());
					}
				}
			}
			if (idStr.length() > 0) {
				sqls.add("update SaleNotifyDown set (sn_custcode,sn_custname)=(select cu_code,cu_name from customer where cu_uu=sn_custuu) where sn_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleNotifyDown sn set (SN_CUSTPRODCODE,SN_CUSTPRODDETAIL,SN_CUSTPRODSPEC)=(select sd.SD_CUSTPRODCODE,sd.SD_CUSTPRODDETAIL,sd.SD_CUSTPRODSPEC from SaleDownDetail sd left join SaleDown on sa_id=sd_said where sa_code=sn.sn_pocode and sd_detno=sn.sn_podetno) where sn_id in ("
						+ idStr.toString() + ")");
				sqls.add("update SaleNotifyDown set (sn_ordercode,sn_orderdetno,sn_orderqty,sn_prodcode)=(select sale.sa_code,saledetail.sd_detno,saledetail.sd_qty,saledowndetail.sd_prodcode from saledowndetail left join saledown on saledowndetail.sd_said=saledown.sa_id left join saledetail on saledetail.sd_sourceid=saledowndetail.sd_id left join sale on sale.sa_id=saledetail.sd_said and sale.sa_sourcetype='CUSTPO' and sale.sa_pocode=saledown.sa_code where saledown.sa_code=SaleNotifyDown.sn_pocode and saledowndetail.sd_detno=SaleNotifyDown.sn_podetno) where sn_id in ("
						+ idStr.toString() + ") and sn_pocode is not null and sn_podetno is not null");
			}
			baseDao.execute(sqls);
			onSaleNotifyDownSuccess(b2bStr.toString(), master);
		}
	}

	/**
	 * 成功写到客户送货提醒之后，修改平台的送货提醒为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleNotifyDownSuccess(String idStr, Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/notice?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 直接在平台做的发货单，传到卖家ERP修改客户送货提醒的发货数
	 */
	private boolean downloadSaleOut(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/notice/send?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleOut> outs = FlexJsonUtil.fromJsonArray(data, SaleOut.class);
					if (!CollectionUtil.isEmpty(outs)) {
						saveSaleOut(outs, master);
						baseDao.save(new TaskLog("(卖家)客户送货提醒-下载发货单", outs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户送货提醒-下载发货单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据平台做的发货单，修改客户送货提醒的发货数
	 */
	private void saveSaleOut(List<SaleOut> outs, Master master) {
		if (!CollectionUtil.isEmpty(outs)) {
			List<String> sqls = new ArrayList<String>();
			for (SaleOut out : outs) {
				if (!CollectionUtil.isEmpty(out.getDetails())) {
					for (SaleOutDetail detail : out.getDetails()) {
						sqls.add("update salenotifydown set sn_sendqty=nvl(sn_sendqty,0) + " + detail.getPd_outqty() + " where b2b_pn_id="
								+ detail.getB2b_pn_id());
					}
				}
			}
			baseDao.execute(sqls);
			onDownloadSaleOutSuccess(outs, master);
		}
	}

	/**
	 * 发货数修改成功之后，回执给平台
	 * 
	 * @param replies
	 * @param enterprise
	 * @return
	 */
	private boolean onDownloadSaleOutSuccess(List<SaleOut> outs, Master master) {
		StringBuffer idStr = new StringBuffer();
		for (SaleOut out : outs) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(out.getB2b_ss_id());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr.toString());
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/notice/send/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 上传发货单到平台
	 * 
	 * @return
	 */
	private boolean uploadSaleOut(Master master) {
		List<SaleOut> outs = getSaleOut();
		if (!CollectionUtil.isEmpty(outs)) {
			String idStr = CollectionUtil.getKeyString(outs, ",");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(outs));
			try {
				// 加"上传中"过渡状态，防止上传过程未完成情况下，单据被反过账、反审核等情况
				beforeSaleOutUpload(idStr);
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/notice/send?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value())
					onSaleOutUploadSuccess(outs);
				else
					onSaleOutUploadFail(idStr);
				baseDao.save(new TaskLog("(卖家)客户送货提醒-上传发货单", outs.size(), response));
			} catch (Exception e) {
				onSaleOutUploadFail(idStr);
				return false;
			} finally {
				checkSaleOutUpload(idStr);
			}
		}
		return true;
	}

	/**
	 * 未上传到平台的发货单
	 * 
	 * @return
	 */
	private List<SaleOut> getSaleOut() {
		try {
			List<SaleOut> outs = baseDao
					.getJdbcTemplate()
					.query("select pi_id,pi_inoutno,pi_currency,pi_rate,pi_payment,pi_remark,pi_recordman,pi_auditman,cu_uu from ProdInOut left join customer on pi_cardcode=cu_code where pi_class='出货单' and pi_statuscode='POSTED' and (PI_SENDSTATUS='待上传' or PI_SENDSTATUS='上传中') and cu_uu is not null and exists (select 1 from ProdIoDetail left join sale on pd_ordercode=sa_code where pd_piid=pi_id and sa_sourcetype='CUSTPO') order by pi_inoutno",
							new BeanPropertyRowMapper<SaleOut>(SaleOut.class));
			List<SaleOut> validOuts = new ArrayList<SaleOut>();
			for (SaleOut out : outs) {
				List<SaleOutDetail> details = new ArrayList<SaleOutDetail>();
				try {
					// 客户送货提醒单转的出货单
					List<SaleOutDetail> thisDetails = baseDao
							.getJdbcTemplate()
							.query("select pd_pdno,pd_sendprice,pd_outqty,pd_ordercode,pd_orderdetno,b2b_pn_id,pd_noticeid from prodiodetail left join SaleNotifyDown on pd_noticeid=sn_id where pd_piid=? and nvl(pd_noticeid,0)<>0",
									new BeanPropertyRowMapper<SaleOutDetail>(SaleOutDetail.class), out.getPi_id());
					details.addAll(thisDetails);
				} catch (EmptyResultDataAccessException e) {
				}
				try {
					// 客户送货提醒单转的通知单，通知单转的出货单
					List<SaleOutDetail> thisDetails = baseDao
							.getJdbcTemplate()
							.query("select pd_pdno,pd_sendprice,pd_outqty,pd_ordercode,pd_orderdetno,b2b_pn_id,snd_noticeid pd_noticeid from prodiodetail left join SendNotifyDetail on pd_orderid=snd_id left join SaleNotifyDown on snd_noticeid=sn_id where pd_piid=? and nvl(pd_snid,0)<>0 and nvl(pd_noticeid,0)=0 and nvl(snd_noticeid,0)<>0",
									new BeanPropertyRowMapper<SaleOutDetail>(SaleOutDetail.class), out.getPi_id());
					details.addAll(thisDetails);
				} catch (EmptyResultDataAccessException e) {
				}
				try {
					// 客户采购单转的销售订单，直接转出货单，无关联的送货提醒
					List<SaleOutDetail> thisDetails = baseDao
							.getJdbcTemplate()
							.query("select pd_pdno,pd_sendprice,pd_outqty,pd_ordercode,pd_orderdetno,sdd.b2b_pd_id pd_orderid from prodiodetail left join SaleDetail sd on pd_ordercode=sd.sd_code and pd_orderdetno=sd.sd_detno left join SaleDownDetail sdd on sd.sd_sourceid=sdd.sd_id where pd_piid=? and nvl(pd_snid,0)=0 and nvl(pd_noticeid,0)=0",
									new BeanPropertyRowMapper<SaleOutDetail>(SaleOutDetail.class), out.getPi_id());
					details.addAll(thisDetails);
				} catch (EmptyResultDataAccessException e) {
				}
				try {
					// 客户采购单转的销售订单，直接转通知单，然后转出货单，无关联的送货提醒
					List<SaleOutDetail> thisDetails = baseDao
							.getJdbcTemplate()
							.query("select pd_pdno,pd_sendprice,pd_outqty,pd_ordercode,pd_orderdetno,sdd.b2b_pd_id pd_orderid from prodiodetail left join SendNotifyDetail on pd_orderid=snd_id left join SaleDetail sd on pd_ordercode=sd.sd_code and pd_orderdetno=sd.sd_detno left join SaleDownDetail sdd on sd.sd_sourceid=sdd.sd_id where pd_piid=? and nvl(pd_snid,0)<>0 and nvl(pd_noticeid,0)=0 and nvl(snd_noticeid,0)=0",
									new BeanPropertyRowMapper<SaleOutDetail>(SaleOutDetail.class), out.getPi_id());
					details.addAll(thisDetails);
				} catch (EmptyResultDataAccessException e) {
				}
				if (details.size() > 0) {
					out.setDetails(details);
					validOuts.add(out);
				}
			}
			return validOuts;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 更新待上传的发货单状态
	 * 
	 * @param idStr
	 *            待上传的发货单
	 */
	public void beforeSaleOutUpload(String idStr) {
		baseDao.execute("update ProdInOut set pi_sendstatus='上传中' where pi_id in (" + idStr + ")");
	}

	/**
	 * 更新上传成功的发货单状态
	 * 
	 * @param idStr
	 *            上传成功的发货单
	 */
	public void onSaleOutUploadSuccess(List<SaleOut> outs) {
		StringBuffer idStr = new StringBuffer();
		List<String> sqls = new ArrayList<String>();
		for (SaleOut out : outs) {
			if (idStr.length() > 0)
				idStr.append(",");
			idStr.append(out.getPi_id());
			if (!CollectionUtil.isEmpty(out.getDetails())) {
				for (SaleOutDetail detail : out.getDetails()) {
					if (detail.getPd_noticeid() != null)
						sqls.add("update SaleNotifyDown set sn_sendqty=nvl(sn_sendqty,0) + " + detail.getPd_outqty() + " where sn_id="
								+ detail.getPd_noticeid());
				}
			}
		}
		sqls.add("update ProdInOut set pi_sendstatus='已上传' where pi_id in (" + idStr.toString() + ")");
		baseDao.execute(sqls);
	}

	/**
	 * 更新上传失败的发货单状态
	 * 
	 * @param idStr
	 *            上传失败的发货单
	 */
	public void onSaleOutUploadFail(String idStr) {
		baseDao.execute("update ProdInOut set pi_sendstatus='待上传' where pi_id in (" + idStr + ") and pi_sendstatus='上传中'");
	}

	public void checkSaleOutUpload(String idStr) {
		baseDao.execute("update ProdInOut set pi_sendstatus='待上传' where pi_id in (" + idStr + ") and pi_sendstatus='上传中'");
	}

	/**
	 * 从平台下载客户下达到平台的结案、反结案送货提醒
	 * 
	 * @return
	 */
	private boolean downloadNotifyDownEnd(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/notice/end?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleNotifyDownEnd> ends = FlexJsonUtil.fromJsonArray(data, SaleNotifyDownEnd.class);
					if (!CollectionUtil.isEmpty(ends)) {
						saveNotifyDownEnd(ends, master);
						baseDao.save(new TaskLog("(卖家)客户送货提醒-下载结案、反结案状态", ends.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户送货提醒-下载结案、反结案状态", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 结案、反结案客户送货提醒单
	 * 
	 * @param ends
	 * @param enterprise
	 */
	private void saveNotifyDownEnd(List<SaleNotifyDownEnd> ends, Master master) {
		if (!CollectionUtil.isEmpty(ends)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer b2bStr = new StringBuffer();// b2b_pd_id
			for (SaleNotifyDownEnd end : ends) {
				sqls.add("update SaleNotifyDown set sn_status='" + Status.FINISH.display() + "',sn_statuscode='" + Status.FINISH.code()
						+ "' where b2b_pn_id=" + end.getB2b_pn_id());
				if (b2bStr.length() > 0)
					b2bStr.append(",");
				b2bStr.append(end.getB2b_pn_id());
				// 消息推送
				String sellerCode = null;
				String pocode = baseDao.getFieldValue("salenotifydown", "sn_pocode", "b2b_pn_id = " + end.getB2b_pn_id(), String.class);
				Long selleruu = baseDao.getFieldValue("saledown", "sa_selleruu", "sa_customeruu = " + end.getCu_uu() + " and sa_code = '"
						+ pocode + "'", Long.class);
				if (selleruu != null && selleruu != 0 && employeeService.getEmployeeByUu(selleruu) != null) {
					sellerCode = employeeService.getEmployeeByUu(selleruu).getEm_code();
				}
				if (sellerCode == null || sellerCode.equals(""))
					sellerCode = customerService.getSallerCodeByCustomerUU(end.getCu_uu());
				String customerName = customerService.getNameByCustomerUU(end.getCu_uu());
				// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
				// XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "客户送货提醒结案提醒",
				// customerName + " " + timeFormat.format(new Date()), null);
				// XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
				// "客户送货提醒结案提醒  " + customerName + " " + timeFormat.format(new Date()));
				// 发起任务
				int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
				if (sellerCode != null) {
					sqls.add("insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,"
							+ "class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) " + " values ("
							+ taskId + ",'发货提醒被取消->" + pocode + "','normal','已启动','DOING'," + "'已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','"
							+ customerName + "','" + sellerCode + "','','','" + baseDao.sGetMaxNumber("ProjectTask", 2)
							+ "','','jsps/b2b/sale/zdQu.jsp" + "')");
					sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,"
							+ "ra_status,ra_statuscode,ra_units,ra_type,ra_taskname) values (resourceassignment_seq.nextval," + taskId
							+ ",'','" + sellerCode + "','',1,'进行中','START',100,'billtask','发货提醒被取消->" + pocode + "')");
				}
			}
			baseDao.execute(sqls);
			onNotifyEndSuccess(b2bStr.toString(), master);
		}
	}

	/**
	 * 将从平台下载的结案、反结案客户提醒单操作完成之后，回执给平台
	 * 
	 * @param idStr
	 * @param enterprise
	 * @return
	 */
	private boolean onNotifyEndSuccess(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/notice/end/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

}
