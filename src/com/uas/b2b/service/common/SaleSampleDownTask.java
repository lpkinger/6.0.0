package com.uas.b2b.service.common;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
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

import com.uas.b2b.model.RemoteFile;
import com.uas.b2b.model.SaleSampleApproval;
import com.uas.b2b.model.SaleSampleDown;
import com.uas.b2b.model.SaleSampleSend;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.crm.CustomerService;

//import com.uas.erp.core.XingePusher;

/**
 * 作为卖家ERP，获取客户下达到平台的打样申请
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class SaleSampleDownTask extends AbstractTask {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private FormAttachService formAttachService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " downloadSaleSampleDown start");
		downloadSaleSampleDown(master);
		logger.info(this.getClass() + " downloadSaleSampleDown end");
		logger.info(this.getClass() + " downloadSaleSampleSend start");
		downloadSaleSampleSend(master);
		logger.info(this.getClass() + " downloadSaleSampleSend end");
		logger.info(this.getClass() + " uploadSampleSend start");
		uploadSampleSend(master);
		logger.info(this.getClass() + " uploadSampleSend end");
		logger.info(this.getClass() + " downloadSaleSampleApproval start");
		downloadSaleSampleApproval(master);
		logger.info(this.getClass() + " downloadSaleSampleApproval end");
	}

	/**
	 * 从平台下载客户下达到平台的客户打样申请
	 * 
	 * @return
	 */
	private boolean downloadSaleSampleDown(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/sample?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleSampleDown> samples = FlexJsonUtil.fromJsonArray(data, SaleSampleDown.class);
					if (!CollectionUtil.isEmpty(samples)) {
						saveSaleSampleDown(samples, master);
						baseDao.save(new TaskLog("(卖家)客户打样-下载客户打样申请", samples.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户打样-下载客户打样申请", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存saleSampleDown
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleSampleDown(List<SaleSampleDown> samples, Master master) {
		if (!CollectionUtil.isEmpty(samples)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();
			for (SaleSampleDown sample : samples) {
				int count = baseDao.getCount("select count(1) from productsampledown where b2b_ps_id =" + sample.getB2b_ps_id());
				if (count == 0) {
					int sdId = baseDao.getSeqId("productsampledown_seq");
					sqls.add(sample.toSqlString(sdId));
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(sdId);
					b2bStr.append(sample.getB2b_ps_id());
					// 消息推送
					// 给卖方ERP中客户资料列表中的客户对应业务员
					// String customerName = customerService.getNameByCustomerUU(sample.getPs_custuu());
					// String sellerCode = customerService.getSallerCodeByCustomerUU(sample.getPs_custuu());
					// if (sellerCode != null) {
					// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm");
					// // XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一个客户打样申请 ", customerName + " "
					// // + timeFormat.format(new Date()), null);
					// // XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
					// // "新增一个客户打样申请  " + customerName + " " + timeFormat.format(new Date()));
					// }
				}
			}
			if (idStr.length() > 0) {
				// 完善记录数据
				sqls.add("update productsampledown set (ps_custcode, ps_custname) = (select cu_code, cu_name from customer where cu_uu = ps_custuu ) where ps_id in ( "
						+ idStr.toString() + ") ");
				baseDao.execute(sqls);
				onSaleSampleDownSuccess(b2bStr.toString(), master);
				try {
					saveAttach(samples);
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 成功写到客户打样申请之后，修改平台的客户打样申请为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleSampleDownSuccess(String idStr, Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/sample?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存来自平台的附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveAttach(List<SaleSampleDown> downs) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleDown down : downs) {
			if (StringUtil.hasText(down.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : down.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update productsampledown set ps_attach='" + attachIds.toString() + "' where b2b_ps_id=" + down.getB2b_ps_id());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 从平台下载在平台上送样的送样单
	 * 
	 * @return
	 */
	private boolean downloadSaleSampleSend(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/sample/sampleSend?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleSampleSend> sampleSends = FlexJsonUtil.fromJsonArray(data, SaleSampleSend.class);
					if (!CollectionUtil.isEmpty(sampleSends)) {
						saveSaleSampleSend(sampleSends, master);
						baseDao.save(new TaskLog("(卖家)客户打样-下载送样单", sampleSends.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户打样-下载送样单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存平台上传过来的送样单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleSampleSend(List<SaleSampleSend> sampleSends, Master master) {
		if (!CollectionUtil.isEmpty(sampleSends)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();
			for (SaleSampleSend sampleSend : sampleSends) {
				int count = baseDao.getCount("select count(1) from CustSendSample where b2b_ss_id =" + sampleSend.getB2b_ss_id());
				if (count == 0) {
					int ssId = baseDao.getSeqId("custsendsample_seq");
					sqls.add(sampleSend.toSaleSqlString(ssId));
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(ssId);
					b2bStr.append(sampleSend.getB2b_ss_id());
				}
			}
			if (idStr.length() > 0) {
				// 完善记录数据
				sqls.add("update CustSendSample set (ss_pscode, ss_custprodcode, ss_custproddetail, ss_custspec, ss_custunit, ss_isfree, ss_allmoney, ss_custcode, ss_custname, ss_custuu, ss_contact, ss_contactuu, ss_sampleprice, ss_currency, ss_rate) = "
						+ "(select ps_code, ps_custprodcode, ps_custproddetail, ps_custspec, ps_custunit, ps_isfree, ps_total, ps_custcode, ps_custname, ps_custuu, ps_contact, ps_contactuu, ps_price, ps_currency, ps_rate from "
						+ "productSampleDown where productSampleDown.b2b_ps_id = CustSendSample.ss_b2b_ps_id ) where ss_id in ( "
						+ idStr.toString() + ") ");
				baseDao.execute(sqls);
				onSaleSampleSendDownSuccess(b2bStr.toString(), master);
				try {
					saveSendAttach(sampleSends);
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 成功写到送样单之后，修改平台的送样单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleSampleSendDownSuccess(String idStr, Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/sample/sampleSend/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存来自平台的送样单附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveSendAttach(List<SaleSampleSend> sampleSends) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleSend sampleSend : sampleSends) {
			if (StringUtil.hasText(sampleSend.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : sampleSend.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update CustSendSample set ss_attach='" + attachIds.toString() + "' where b2b_ss_id=" + sampleSend.getB2b_ss_id());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 将(卖方)ERP的送样单上传到平台
	 * 
	 * @param en
	 * @return
	 */
	private boolean uploadSampleSend(Master master) {
		List<SaleSampleSend> saleSampleSends = getUploadSampleSend();
		if (!CollectionUtil.isEmpty(saleSampleSends)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(saleSampleSends));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/sale/sample/sampleSend?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedSampleSend(saleSampleSends);
					try {
						uploadSendAttach(saleSampleSends, master);
					} catch (Exception e) {

					}
				}
				baseDao.save(new TaskLog("(卖家)客户打样-上传送样单", saleSampleSends.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private List<SaleSampleSend> getUploadSampleSend() {
		try {
			List<SaleSampleSend> saleSampleSends = baseDao.getJdbcTemplate().query(
					"select custsendsample.*, productsampledown.b2b_ps_id "
							+ "from custsendsample left join productsampledown on ps_code=ss_pscode where ss_sendstatus='待上传' and "
							+ "ss_status='已审核' and productsampledown.b2b_ps_id is not null ",
					new BeanPropertyRowMapper<SaleSampleSend>(SaleSampleSend.class));
			return saleSampleSends;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private void updateUploadedSampleSend(List<SaleSampleSend> saleSampleSends) {
		for (SaleSampleSend saleSampleSend : saleSampleSends) {
			baseDao.execute("update custsendsample set ss_sendstatus = '已上传' where ss_id = " + saleSampleSend.getSs_id());
		}
	}

	/**
	 * 打样申请单附件
	 * 
	 * @param inquiries
	 * @param enterprise
	 */
	private void uploadSendAttach(List<SaleSampleSend> sampleSends, Master master) {
		formAttachService.uploadAttachs(sampleSends,
				master.getMa_b2bwebsite() + "/erp/sale/sample/sampleSend/attach?access_id=" + master.getMa_uu(), "sourceId", true,
				master.getMa_accesssecret());// sourceId是因为B2b项目中的attach处理是根据sourceId来获取关联属性
	}

	/**
	 * 从平台下载在平台上客户认定单
	 * 
	 * @return
	 */
	private boolean downloadSaleSampleApproval(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(
					master.getMa_b2bwebsite() + "/erp/sale/sample/sampleApproval?access_id=" + master.getMa_uu(), null, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<SaleSampleApproval> sampleApprovals = FlexJsonUtil.fromJsonArray(data, SaleSampleApproval.class);
					if (!CollectionUtil.isEmpty(sampleApprovals)) {
						saveSaleSampleApproval(sampleApprovals, master);
						baseDao.save(new TaskLog("(卖家)客户打样-下载客户认定单", sampleApprovals.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)客户打样-下载客户认定单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存平台上传过来的客户认定单
	 * 
	 * @param sales
	 * @param enterprise
	 */
	private void saveSaleSampleApproval(List<SaleSampleApproval> sampleApprovals, Master master) {
		if (!CollectionUtil.isEmpty(sampleApprovals)) {
			List<String> sqls = new ArrayList<String>();
			StringBuffer idStr = new StringBuffer();
			StringBuffer b2bStr = new StringBuffer();
			for (SaleSampleApproval sampleApproval : sampleApprovals) {
				int count = baseDao.getCount("select count(1) from productapprovaldown where pa_b2bid =" + sampleApproval.getPa_b2bid());
				if (count == 0) {
					int saId = baseDao.getSeqId("productapprovaldown_seq");
					sqls.add(sampleApproval.toSqlString(saId));
					if (idStr.length() > 0) {
						idStr.append(",");
						b2bStr.append(",");
					}
					idStr.append(saId);
					b2bStr.append(sampleApproval.getPa_b2bid());
				}
			}
			if (idStr.length() > 0) {
				// 完善记录数据
				sqls.add("update productapprovaldown set (pa_custcode, pa_custname) = "
						+ "(select cu_code, cu_name from customer where customer.cu_uu = productapprovaldown.pa_custuu) where pa_id in ( "
						+ idStr.toString() + ") ");
				sqls.add("update productapprovaldown set (pa_prodcode) = "
						+ "(select max(pc_prodcode) from productcustomer where productcustomer.pc_custproddetail = productapprovaldown.pa_custproddetail "
						+ " and productcustomer.pc_custprodcode = productapprovaldown.pa_custprodcode "
						+ " and productcustomer.pc_custprodspec = productapprovaldown.pa_custprodspec "
						+ " and productcustomer.pc_custcode = productapprovaldown.pa_custcode) where pa_id in (" + idStr.toString() + ") ");
				baseDao.execute(sqls);
				// for (SaleSampleApproval approval : sampleApprovals) {
				// // 消息推送
				// // 给卖方ERP中客户资料列表中的客户对应业务员
				// String customerName = customerService.getNameByCustomerUU(approval.getPa_custuu());
				// String sellerCode = customerService.getSallerCodeByCustomerUU(approval.getPa_custuu());
				// if (sellerCode != null) {
				// SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm");
				// // XingePusher.pushSingleAccountAndroid(master.getMa_user(), sellerCode, "新增一个客户认定单 ",
				// // customerName + " " + timeFormat.format(new Date()), null);
				// // XingePusher.pushSingleAccountIOS(master.getMa_user(), sellerCode,
				// // "新增一个客户认定单  " + customerName + " " + timeFormat.format(new Date()));
				// }
				// }
				onSaleSampleApprovalDownSuccess(b2bStr.toString(), master);
				// 保存四个附件信息
				try {
					saveApprovalAttach(sampleApprovals);
					saveApprovalprdAttach(sampleApprovals);
					saveApprovalpadAttach(sampleApprovals);
					saveApprovalppdAttach(sampleApprovals);
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 成功写到认定单之后，修改平台的认定单为已下载
	 * 
	 * @param idStr
	 * @param enterprise
	 */
	private boolean onSaleSampleApprovalDownSuccess(String idStr, Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/sample/sampleApproval?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存来自平台的认定单附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveApprovalAttach(List<SaleSampleApproval> approvals) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleApproval approval : approvals) {
			if (StringUtil.hasText(approval.getFiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : approval.getFiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update productapprovaldown set pa_attach='" + attachIds.toString() + "' where pa_b2bid=" + approval.getPa_b2bid());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 保存来自平台的认定单prd附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveApprovalprdAttach(List<SaleSampleApproval> approvals) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleApproval approval : approvals) {
			if (StringUtil.hasText(approval.getPrdfiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : approval.getPrdfiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update productapprovaldown set pa_prdattach='" + attachIds.toString() + "' where pa_b2bid="
						+ approval.getPa_b2bid());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 保存来自平台的认定单pad附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveApprovalpadAttach(List<SaleSampleApproval> approvals) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleApproval approval : approvals) {
			if (StringUtil.hasText(approval.getPadfiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : approval.getPadfiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update productapprovaldown set pa_padattach='" + attachIds.toString() + "' where pa_b2bid="
						+ approval.getPa_b2bid());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * 保存来自平台的认定单ppd附件信息
	 * 
	 * @param downs
	 * @param enterprise
	 */
	private void saveApprovalppdAttach(List<SaleSampleApproval> approvals) {
		List<String> sqls = new ArrayList<String>();
		String remoteUrl = "B2B://file";
		for (SaleSampleApproval approval : approvals) {
			if (StringUtil.hasText(approval.getPpdfiles())) {
				StringBuffer attachIds = new StringBuffer();
				for (RemoteFile file : approval.getPpdfiles()) {
					int id = baseDao.getSeqId("filepath_seq");
					sqls.add(file.toSqlString(id, remoteUrl));
					attachIds.append(id).append(";");
				}
				sqls.add("update productapprovaldown set pa_ppdattach='" + attachIds.toString() + "' where pa_b2bid="
						+ approval.getPa_b2bid());
			}
		}
		baseDao.execute(sqls);
	}

}
