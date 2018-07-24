package com.uas.b2b.service.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.b2b.model.DeOrderDeleteLog;
import com.uas.b2b.model.DeputyOrder;
import com.uas.b2b.model.DeputyOrderItem;
import com.uas.b2b.model.DeputyOrdersLog;
import com.uas.b2b.model.SaleDown;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;

/**
 * 获取平台下达的代采订单转成ERP的销售订单
 * 
 * @author hejq
 * @time 创建时间：2017年3月24日
 */
@Component
@EnableAsync
@EnableScheduling
public class DeputyOrderTask extends AbstractTask {
	protected static final Logger logger = Logger.getLogger("SchedualTask");

	@Autowired
	private BaseDao baseDao;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		try {
			logger.info(this.getClass() + " downloadDeOrder start");
			if (downloadDeOrder(master)) {// 下载代采订单
				logger.info(this.getClass() + " downloadDeOrder end");
			}
			logger.info(this.getClass() + " uploadNotAgreedDeOrder start");
			uploadNotAgreedDeOrder(master);// 上传未同意的代采订单到平台
			logger.info(this.getClass() + " uploadNotAgreedDeOrder end");
			logger.info(this.getClass() + " uploadAgreedDeOrder start");
			uploadAgreedDeOrder(master);// 上传同意的代采订单到平台
			logger.info(this.getClass() + " uploadAgreedDeOrder end");
			logger.info(this.getClass() + " downloadDeleteLogs start");
			downloadDeleteLogs(master);// 下载平台的删除记录
			logger.info(this.getClass() + " downloadDeleteLogs end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载平台代采订单删除记录，同步到ERP进行删除操作
	 * @param master
	 */
	private boolean downloadDeleteLogs(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/deletelogs?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<DeOrderDeleteLog> logs = FlexJsonUtil.fromJsonArray(data, DeOrderDeleteLog.class);
					if (!CollectionUtil.isEmpty(logs)) {
						excuteLogs(logs, master);
						baseDao.save(new TaskLog("(卖家)代采订单--下载代采订单删除记录", logs.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)代采订单--下载代采订单删除记录", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

	/**
	 * 下载记录进行同步删除操作
	 * 
	 * @param logs
	 * @param master
	 */
	private void excuteLogs(List<DeOrderDeleteLog> logs, Master master) {
		StringBuffer idStr = new StringBuffer();
		if(idStr.length() > 0) {
			idStr.append(",");
		}
		if(!CollectionUtils.isEmpty(logs)) {
			for(DeOrderDeleteLog log : logs) {
				// 先删除明细
				baseDao.execute("delete from saledetail where sd_said in (select sa_id from sale where sa_fromcode = '" + log.getCode() + "' and sa_statuscode = 'ENTERING')");
				baseDao.execute("delete sale where sa_fromcode = '" + log.getCode() + "' and sa_statuscode = 'ENTERING'");
				idStr.append(log.getId());
			}
			uploadDelete(idStr.toString(), master);
		}
		
	}

	/**
	 * 删除之后将id数据传回平台进行更新操作
	 * 
	 * @param idStr
	 * @param master
	 * @return
	 */
	private boolean uploadDelete(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/deletelogs/back?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 上传已经同意的单据到平台
	 * 
	 * @param master
	 */
	private boolean uploadAgreedDeOrder(Master master) {
		List<DeputyOrder> orders = getAgreedDeputyOrder();
		if (!CollectionUtils.isEmpty(orders)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonDeep(orders));
			String codeStr = CollectionUtil.getKeyString(orders, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/agreed/back?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedAgreedOrderSuccess(codeStr);
				}
				baseDao.save(new TaskLog("(卖家)代采订单--上传已同意订单到平台", 0, response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	/**
	 * 已同意单据上传后更新状态
	 * 
	 * @param codeStr
	 */
	private void onUploadedAgreedOrderSuccess(String codeStr) {
		codeStr = codeStr.replaceAll(",", "','");
		baseDao.execute("update sale set sa_backstatus = '已同意已上传' where sa_code in ('" + codeStr + "')");
	}

	/**
	 * 获取已经同意的单据上传到平台
	 * 
	 * @return
	 */
	private List<DeputyOrder> getAgreedDeputyOrder() {
		List<DeputyOrder> orders = new ArrayList<DeputyOrder>();
		String sql = "select sa_fromcode code, sa_code salecode from sale where sa_status = '已审核' and sa_kind = '代采订单' and nvl(sa_backstatus,' ')<>'已同意已上传'  and rownum < 200 ";
		orders = baseDao.query(sql, DeputyOrder.class);
		return orders;
	}

	/**
	 * 上传回复不同意的代采订单
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadNotAgreedDeOrder(Master master) {
		List<DeputyOrder> orders = getTodoDeputyOrder();
		if (!CollectionUtils.isEmpty(orders)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonDeep(orders));
			String codeStr = CollectionUtil.getKeyString(orders, ",");
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/notAgreed/back?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedOrderSuccess(codeStr);
				}
				baseDao.save(new TaskLog("(卖家)代采订单--上传不同意订单到平台", 0, response));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传成功更新上传状态
	 * 
	 * @param idStr
	 */
	private void onUploadedOrderSuccess(String codeStr) {
		codeStr = codeStr.replaceAll(",", "','");
		baseDao.execute("update sale set sa_backstatus = '已驳回已上传' where sa_code in ('" + codeStr + "')");
	}

	/**
	 * 获取需要上传的代采订单（不同意）
	 * 
	 * @param master
	 * @return
	 */
	private List<DeputyOrder> getTodoDeputyOrder() {
		List<DeputyOrder> orders = new ArrayList<DeputyOrder>();
		String sql = "select sa_fromcode code, sa_code salecode, sa_backreason remark from sale where sa_backstatus = '已驳回待上传' and rownum < 200";
		orders = baseDao.query(sql, DeputyOrder.class);
		return orders;
	}

	/**
	 * 发出轮询，从平台获取未下载的代采订单
	 * 
	 * @param master
	 * @return
	 */
	private boolean downloadDeOrder(Master master) {
		try {
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder?access_id=" + master.getMa_uu(),
					null, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					List<DeputyOrder> deOrders = FlexJsonUtil.fromJsonArray(data, DeputyOrder.class);
					if (!CollectionUtil.isEmpty(deOrders)) {
						saveSaleDown(deOrders, master);
						baseDao.save(new TaskLog("(卖家)代采订单--下载代采订单", deOrders.size(), response));
					}
				}
			} else
				baseDao.save(new TaskLog("(卖家)代采订单--下载代采订单", 0, response));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 将获取到的代采订单存入生成销售单
	 * 
	 * @param deOrders
	 * @param master
	 */
	private void saveSaleDown(List<DeputyOrder> deOrders, Master master) {
		if (!CollectionUtil.isEmpty(deOrders)) {
			StringBuffer b2bIdStr = new StringBuffer();// deo_id
			for (DeputyOrder deOrder : deOrders) {
				String custCodeSql = "select cu_code from customer where cu_uu = " + deOrder.getEnuu();
				String custNameSql = "select cu_name from customer where cu_uu = " + deOrder.getEnuu();
				String custCode = baseDao.queryForObject(custCodeSql, String.class);
				String custName = baseDao.queryForObject(custNameSql, String.class);
				
				// 根据信扬新需求，如果改了采购单号，需要把以前的单据给删除，采用客户uu,来源单号，一起做判断
				String orSql = "select sa_code,sa_id from sale where sa_custcode = '" + custCode + "' and sa_fromcode = '" + deOrder.getCode() + "'";
				List<SaleDown> orders = baseDao.query(orSql, SaleDown.class);
				if(!CollectionUtils.isEmpty(orders)) {
					SaleDown order = orders.get(0);
					if(!order.getSa_code().equals(deOrder.getSalecode())) {
						baseDao.execute("delete saledetail where sd_said = " + order.getSa_id());
						baseDao.execute("delete sale where sa_id = " + order.getSa_id());
						baseDao.save(new TaskLog("(卖家)代采订单--删除销售单:" + order.getSa_id(), 1, null));
					}
				}
				String paymentscode = baseDao.queryForObject("select cu_paymentscode from customer where cu_code = ' " + custCode + "'",
						String.class);
				String payments = baseDao.queryForObject("select cu_payments from customer where cu_code = ' " + custCode + "'",
						String.class);
				String add = baseDao.queryForObject("select cu_add1 from customer where cu_code = ' " + custCode + "'", String.class);
				String sellercode = baseDao.queryForObject("select cu_sellercode from customer where cu_code = ' " + custCode + "'",
						String.class);
				// String seller = baseDao.queryForObject(
				// "select cu_sellername from customer where cu_code = ' " +
				// custCode + "'", String.class);
				String departmentcode = baseDao.queryForObject("select EM_DEPARTMENTCODE from employee where em_code = '" + sellercode
						+ "'", String.class);
				String departmentname = baseDao.queryForObject("select em_depart from employee where em_code = '" + sellercode + "'",
						String.class);
				String vendorcode = baseDao.queryForObject("select ve_code from vendor where ve_name = '" + deOrder.getTervendor() + "'",
						String.class);
				if (deOrder.getTervendaddress() != null) {
					deOrder.setTervendaddress(deOrder.getTervendaddress().replace("'", "’"));
				}
				if (deOrder.getPaycomname() != null) {
					deOrder.setPaycomname(deOrder.getPaycomname().replace("'", "’"));
				}
				if (deOrder.getTervendor() != null) {
					deOrder.setTervendor(StringUtil.nvl(deOrder.getTervendor().replace("'", "’"), ""));
				}
				if (deOrder.getBankaddress() != null) {
					deOrder.setBankaddress(deOrder.getBankaddress().replace("'", "’"));
				}
				if (deOrder.getCompanyname() != null) {
					deOrder.setCompanyname(deOrder.getCompanyname().replace("'", "’"));
				}
				if (custCode != null) {
					custCode = StringUtil.nvl(custCode, "").replace("'", "’");
				}
				if (custName != null) {
					custName = StringUtil.nvl(custName, "").replace("'", "’");
				}
				if (paymentscode != null) {
					paymentscode = StringUtil.nvl(paymentscode, "").replace("'", "’");
				}
				Long said = baseDao.queryForObject("select sale_seq.nextval from dual", Long.class);
				String oldOrder = "select count(*) from sale where sa_code ='" + StringUtil.nvl(deOrder.getSalecode(), "") + "'";
				Integer count = baseDao.queryForObject(oldOrder, Integer.class);
				List<DeputyOrdersLog> logs = new ArrayList<DeputyOrdersLog>();
				if (count == 0) {// 如果订单号不存在才进行存储
					String saleSql = "insert into sale (SA_ID, SA_CODE, SA_POCODE, SA_DATE, SA_KIND, SA_CUSTCODE, SA_CUSTNAME, SA_CURRENCY, SA_RATE, sa_apcustcode, "
							+ "sa_apcustname, sa_shcustcode, sa_shcustname, sa_paymentscode, sa_payments, sa_toplace, sa_fromcode, sa_sellercode, "
							+ " sa_departmentcode, sa_departmentname, sa_recorder, sa_recorddate, Sa_statuscode, "
							+ "Sa_status, sa_vendcode, sa_vendname,sa_a20_user,sa_a19_user,sa_a18_user,sa_a17_user,sa_a14_user,sa_a16_user,"
							+ "sa_a15_user,sa_a11_user,sa_a9_user,sa_a10_user,sa_a8_user,sa_a6_user,sa_a7_user,sa_a5_user,sa_a4_user,sa_a3_user,sa_a24_user,"
							+ "sa_a22_user,sa_payername,sa_payeraddress, sa_a13_user,sa_a12_user,sa_a2_user,sa_backstatus,sa_a21_user,sa_a25_user,sa_a10_user_2) values ("
							+ said
							+ ",'"
							+ StringUtil.nvl(deOrder.getSalecode(), "")
							+ "', '"
							+ StringUtil.nvl(deOrder.getSalepocode(), "")
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getEntrydate())
							+ ", '代采订单', '"
							+ custCode
							+ "', '"
							+ custName
							+ "', '"
							+ deOrder.getCurrency()
							+ "', null, '"
							+ StringUtil.nvl(custCode, "")
							+ "', '"
							+ custName
							+ "', '"
							+ custCode
							+ "', '"
							+ StringUtil.nvl(custName, null)
							+ "', '"
							+ paymentscode
							+ "', '"
							+ StringUtil.nvl(payments, "")
							+ "', '"
							+ StringUtil.nvl(add, "").replace("'", "’")
							+ "', '"
							+ StringUtil.nvl(deOrder.getCode(), "")
							+ "', '"
							+ StringUtil.nvl(sellercode, "").replace("'", "’")
							+ "', '"
							+ StringUtil.nvl(departmentcode, "").replace("'", "’")
							+ "', '"
							+ StringUtil.nvl(departmentname, "").replace("'", "’")
							+ "', '系统管理员', "
							+ DateUtil.parseDateToOracleString(null, new Date())
							+ ", 'ENTERING', '在录入', '"
							+ vendorcode
							+ "', '"
							+ deOrder.getTervendor()
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getMadealdate())
							+ ", '"
							+ deOrder.getMadealcode()
							+ "', '"
							+ deOrder.getVendtel()
							+ "', '"
							+ deOrder.getVenduser()
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getShipdate())
							+ ", '"
							+ deOrder.getDeliverymethod()
							+ "', '"
							+ deOrder.getPickupmethod()
							+ "', '"
							+ StringUtil.nvl(deOrder.getTervendaddress(), "")
							+ "', '"
							+ deOrder.getCompanyname()
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getRequirepaydate())
							+ ", '"
							+ deOrder.getBankname()
							+ "', '"
							+ deOrder.getBankaccount()
							+ "', '"
							+ deOrder.getBankaddress()
							+ "', '"
							+ deOrder.getBankcode()
							+ "', '"
							+ deOrder.getOtherdata()
							+ "', '"
							+ deOrder.getLegalrepresent()
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getDeliverydate())
							+ ", "
							+ DateUtil.parseDateToOracleString(null, deOrder.getPaydate())
							+ ", '"
							+ StringUtil.nvl(deOrder.getPaycomname(), "")
							+ "', '"
							+ StringUtil.nvl(deOrder.getPaycomaddress(), "")
							+ "', '"
							+ StringUtil.nvl(deOrder.getRiskmethod(), "")
							+ "', '"
							+ StringUtil.nvl(deOrder.getTaxpaymentmethod(), "")
							+ "', '" 
							+ StringUtil.nvl(deOrder.getEnfax(), "")
							+ "', '', '" 
							+ StringUtil.nvl(deOrder.getPaycomname(), "")
							+ "', '" 
							+ StringUtil.nvl(deOrder.getPaycomaddress(), "")
							+ "', "
							+ DateUtil.parseDateToOracleString(null, deOrder.getActualpaydate())
							+ ")";
					baseDao.execute(saleSql);
					if (b2bIdStr.length() > 0) {
						b2bIdStr.append(",");
					}
					b2bIdStr.append(deOrder.getId());
					if (!CollectionUtil.isEmpty(deOrder.getDeputyOrderItems())) {
						for (DeputyOrderItem item : deOrder.getDeputyOrderItems()) {
							Long deid = baseDao.queryForObject("select SALEDETAIL_SEQ.nextval from dual", Long.class);
							String detailCountSql = "select count(*) from saledetail where sd_id = " + deid;
							Integer detailCount = baseDao.queryForObject(detailCountSql, Integer.class);
							if (detailCount > 0) {
								deid = baseDao.queryForObject("select SALEDETAIL_SEQ.nextval from dual", Long.class);
							}
							SimpleDateFormat sdfo = new SimpleDateFormat("yyMMdd");
							String prodCode = "PROD" + sdfo.format(new Date()) + deid;
							// 保存明细
							String detailSql = "insert into saledetail (sd_id, sd_said, sd_detno, sd_code, sd_prodcode, sd_qty, sd_price, sd_total, sd_bgprice, "
									+ "sd_remark, sd_delivery, SD_COSTINGPRICE) values ("
									+ deid
									+ ", "
									+ said
									+ ","
									+ item.getDetno()
									+ ", '"
									+ StringUtil.nvl(deOrder.getSalecode(), "")
									+ "', '"
									+ StringUtil.nvl(prodCode, "")
									+ "', "
									+ item.getAmount()
									+ ", "
									+ item.getUnitprice()
									+ ", "
									+ item.getTotalprice()
									+ ", null, '"
									+ StringUtil.nvl(item.getRemark(), "")
									+ "', "
									+ DateUtil.parseDateToOracleString(null, deOrder.getDeliverydate()) + "," + item.getPurcprice() + ")";
							baseDao.execute(detailSql);
							// 插入物料信息
							// 先判断物料是否存在
							Long prodid = baseDao.queryForObject("select PRODUCT_SEQ.nextval from dual", Long.class);
							String prodCountSql = "select count(*) from product where pr_code = '" + prodCode + "'";
							Integer prodCount = baseDao.queryForObject(prodCountSql, Integer.class);
							if (prodCount == 0) {
								String prodSql = "insert into product (pr_id, pr_detail, pr_spec, pr_speccs, pr_unit, pr_recordman, pr_docdate, pr_status, pr_statuscode, pr_remark, pr_code, pr_orispeccode, pr_brand) values ("
										+ prodid
										+ ", '"
										+ StringUtil.nvl(item.getProdname(), "")
										+ "', '"
										+ StringUtil.nvl(item.getProdcode(), "")
										+ "', '"
										+ StringUtil.nvl(item.getProdspec(), "")
										+ "', 'PCS', '系统管理员', "
										+ DateUtil.parseDateToOracleString(null, new Date())
										+ ", '已审核', 'AUDITED', '"
										+ StringUtil.nvl(deOrder.getSalecode(), "")
										+ "平台代采订单产生 ', '"
										+ StringUtil.nvl(prodCode, "")
										+ "', '"
										+ StringUtil.nvl(item.getProdcode(), "")
										+ "', '"
										+ StringUtil.nvl(item.getProdbrand(), "") + "')";
								baseDao.execute(prodSql);
							}
						}
					}
					uploadSaved(b2bIdStr.toString(), master);
				} else if (count > 0 && deOrder.getStatuscode().equals(222)) { // 数量大于零并且是拒绝的单据才进行更新操作，其他的填写重复的单据不能进行操作
					// 更新操作
					if(deOrder.getRate() != null && deOrder.getRate().equals("null")) {
						deOrder.setRate((double)0);
					}
					String updatesql = "update sale set sa_pocode='" + StringUtil.nvl(deOrder.getSalepocode(), "")
					+ "', sa_date= " + DateUtil.parseDateToOracleString(null, deOrder.getEntrydate())
					+ ","
					+ "sa_kind= '代采订单', sa_custcode= '" + custCode 
					+ "', sa_custname='" + custName
					+ "', sa_currency='" + deOrder.getCurrency() 
					+ "', sa_rate= '" + StringUtil.nvl(deOrder.getRate(), "")
					+ "', "
					+ "sa_apcustcode= '" + StringUtil.nvl(custCode, "") 
					+ "', " 
					+ "sa_apcustname='" + custName 
					+ "', sa_shcustcode='" + custCode 
					+ "', sa_shcustname='" + StringUtil.nvl(custName, null)
					+ "', sa_paymentscode='" + paymentscode 
					+ "', "
					+ "sa_payments='" + StringUtil.nvl(payments, "") 
					+ "', sa_toplace='"+ StringUtil.nvl(add, "").replace("'", "’") 
					+ "', sa_fromcode='" + StringUtil.nvl(deOrder.getCode(), "")
					+ "'," 
					+ " sa_sellercode='" + StringUtil.nvl(sellercode, "").replace("'", "’")
					+ "', " 
					+ " sa_departmentcode='" + StringUtil.nvl(departmentcode, "").replace("'", "’")
					+ "', sa_departmentname='"
					+ StringUtil.nvl(departmentname, "").replace("'", "’") 
					+ "', sa_recorder='系统管理员', "
					+ "sa_recorddate=" + DateUtil.parseDateToOracleString(null, new Date())
					+ ", sa_statuscode='ENTERING', sa_status='在录入',"
					+ " sa_vendcode='" + vendorcode
					+ "', "
					+ "sa_vendname='" + deOrder.getTervendor() 
					+ "', sa_a20_user=" + DateUtil.parseDateToOracleString(null, deOrder.getMadealdate())
					+ ","
					+ " sa_a19_user='" + deOrder.getMadealcode() 
					+ "',sa_a18_user='" + deOrder.getVendtel()
					+ "',sa_a17_user='" + deOrder.getVenduser() 
					+ "',sa_a14_user=" + DateUtil.parseDateToOracleString(null, deOrder.getShipdate()) 
					+ "," 
					+ "sa_a16_user='" + deOrder.getDeliverymethod() + "'," 
					+ "sa_a15_user='" + deOrder.getPickupmethod()
					+ "', "
					+ "sa_a11_user='" + StringUtil.nvl(deOrder.getTervendaddress(), "")
					+ "', "
					+ "sa_a9_user='" + deOrder.getCompanyname() 
					+ "', "
					+ "sa_a10_user=" + DateUtil.parseDateToOracleString(null, deOrder.getRequirepaydate()) 
					+ ", "
					+ "sa_a8_user='" + deOrder.getBankname() 
					+ "',sa_a6_user='" + deOrder.getBankaccount()
					+ "',sa_a7_user='" + deOrder.getBankaddress() 
					+ "'," 
					+ "sa_a5_user='" + deOrder.getBankcode() 
					+ "',sa_a4_user='" + deOrder.getOtherdata()
					+ "',"
					+ "sa_a3_user='" + deOrder.getLegalrepresent() 
					+ "',sa_a24_user=" + DateUtil.parseDateToOracleString(null, deOrder.getDeliverydate()) 
					+ ","
					+ "sa_a22_user=" + DateUtil.parseDateToOracleString(null, deOrder.getPaydate()) 
					+ ", "
					+ "sa_payername='" + StringUtil.nvl(deOrder.getPaycomname(), "")
					+ "', sa_payeraddress='" + StringUtil.nvl(deOrder.getPaycomaddress(), "") 
					+ "', "
					+ "sa_a13_user='" + StringUtil.nvl(deOrder.getRiskmethod(), "")
					+ "',"
					+ " sa_a12_user='" + StringUtil.nvl(deOrder.getTaxpaymentmethod(), "") 
					+ "', "
					+ "sa_a2_user='" + StringUtil.nvl(deOrder.getEnfax(), "")
					+ "',"
					+ "sa_backstatus='已下载', "
					+ "sa_a21_user='" + StringUtil.nvl(deOrder.getPaycomname(), "")
					+ "', "
					+ "sa_a25_user='" + StringUtil.nvl(deOrder.getPaycomaddress(), "")
					+ "', "
					+ "sa_a10_user_2=" + DateUtil.parseDateToOracleString(null, deOrder.getActualpaydate()) 
					+ " where sa_code='" + deOrder.getSalecode() + "'";
					try {
						baseDao.execute(updatesql);			
						b2bIdStr.append(deOrder.getId());
					} catch (DataAccessException e) {
						e.printStackTrace();
					}
					if (!CollectionUtil.isEmpty(deOrder.getDeputyOrderItems())) {
						// 先删除明细
						String sql = "select sa_id from sale where sa_code = '" + deOrder.getSalecode() + "'";
						Long id = baseDao.queryForObject(sql, Long.class);
						if (id != null) {
							String deSql = "delete saledetail where sd_said = " + id;
							baseDao.execute(deSql);
						}
						for (DeputyOrderItem item : deOrder.getDeputyOrderItems()) {
							Long deid = baseDao.queryForObject("select SALEDETAIL_SEQ.nextval from dual", Long.class);
							String detailCountSql = "select count(*) from saledetail where sd_id = " + deid;
							Integer detailCount = baseDao.queryForObject(detailCountSql, Integer.class);
							if (detailCount > 0) {
								deid = baseDao.queryForObject("select SALEDETAIL_SEQ.nextval from dual", Long.class);
							}
							SimpleDateFormat sdfo = new SimpleDateFormat("yyMMdd");
							String prodCode = "PROD" + sdfo.format(new Date()) + deid;
							// 保存明细
							String detailSql = "insert into saledetail (sd_id, sd_said, sd_detno, sd_code, sd_prodcode, sd_qty, sd_price, sd_total, sd_bgprice, "
									+ "sd_remark, sd_delivery, SD_COSTINGPRICE) values ("
									+ deid + ", " 
									+ id + ","
									+ item.getDetno() + ", '" 
									+ StringUtil.nvl(deOrder.getSalecode(), "") + "', '"
									+ StringUtil.nvl(prodCode, "") + "', " 
									+ item.getAmount() + ", "
									+ item.getUnitprice() + ", " 
									+ item.getTotalprice() + ", null, '"
									+ StringUtil.nvl(item.getRemark(), "") + "', "
									+ DateUtil.parseDateToOracleString(null, deOrder.getDeliverydate()) + ","
									+ item.getPurcprice() + ")";
							baseDao.execute(detailSql);
							// 插入物料信息
							// 先判断物料是否存在
							Long prodid = baseDao.queryForObject("select PRODUCT_SEQ.nextval from dual", Long.class);
							String prodCountSql = "select count(*) from product where pr_code = '" + prodCode + "'";
							Integer prodCount = baseDao.queryForObject(prodCountSql, Integer.class);
							if (prodCount == 0) {
								String prodSql = "insert into product (pr_id, pr_detail, pr_spec, pr_speccs, pr_unit, pr_recordman, pr_docdate, pr_status, pr_statuscode, pr_remark, pr_code, pr_orispeccode, pr_brand) values ("
										+ prodid + ", '" 
										+ StringUtil.nvl(item.getProdname(), "") + "', '"
										+ StringUtil.nvl(item.getProdcode(), "") + "', '"
										+ StringUtil.nvl(item.getProdspec(), "") + "', 'PCS', '系统管理员', "
										+ DateUtil.parseDateToOracleString(null, new Date()) + ", '已审核', 'AUDITED', '"
										+ StringUtil.nvl(deOrder.getSalecode(), "") + "平台代采订单产生 ', '"
										+ StringUtil.nvl(prodCode, "") + "', '"
										+ StringUtil.nvl(item.getProdcode(), "") + "', '" 
										+ StringUtil.nvl(item.getProdbrand(), "") + "')";
								baseDao.execute(prodSql);
							}
						}
					}
					uploadSaved(b2bIdStr.toString(), master);
				} else {// 如果存在而且状态不是审核不同意的状态
					DeputyOrdersLog log = new DeputyOrdersLog();
					log.setSourceid(deOrder.getId());
					log.setEnuu(deOrder.getEnuu());
					log.setCode(deOrder.getSalecode());
					logs.add(log);
					uploadRepetitions(logs, master);
				}
			}
			uploadLogs(deOrders, master);// 传输日志
		}

	}

	/**
	 * 传递传输日志到平台，方便查询
	 * 
	 * @param salecode
	 * @param master
	 */
	private boolean uploadLogs(List<DeputyOrder> deOrders, Master master) {
		List<DeputyOrdersLog> logs = getDownLoadSize(deOrders);
		if (!CollectionUtil.isEmpty(logs)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(logs));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/transferLogs?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				return response.getStatusCode() == HttpStatus.OK.value();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 查询传输的数据大小
	 * 
	 * @return
	 */
	private List<DeputyOrdersLog> getDownLoadSize(List<DeputyOrder> deOrders) {
		List<DeputyOrdersLog> logs = new ArrayList<DeputyOrdersLog>();
		if (!CollectionUtil.isEmpty(deOrders)) {
			DeputyOrdersLog log = new DeputyOrdersLog();
			for (DeputyOrder deOrder : deOrders) {
				String sql = "select count(*) from saledetail d left join sale s on d.sd_said = s.sa_id where s.sa_code = '"
						+ deOrder.getSalecode() + "'";
				Integer size = baseDao.queryForObject(sql, Integer.class);
				log.setDownloadSize(size);
				log.setCode(deOrder.getSalecode());
				log.setSourceid(deOrder.getId());
				logs.add(log);
			}
		}
		return logs;
	}

	/**
	 * 回传以保存的单据，改变下载状态
	 * 
	 * @param idStr
	 * @param master
	 * @return
	 */
	private boolean uploadSaved(String idStr, Master master) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", idStr);
		try {
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/sale/deputyOrder?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			return response.getStatusCode() == HttpStatus.OK.value();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 如果订单号重复了，给出提示，方便查询
	 * 
	 * @param master
	 * @return
	 */
	private boolean uploadRepetitions(List<DeputyOrdersLog> logs, Master master) {
		if (!CollectionUtil.isEmpty(logs)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArray(logs));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/repetitions?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				return response.getStatusCode() == HttpStatus.OK.value();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
