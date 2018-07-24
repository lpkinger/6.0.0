package com.uas.erp.service.b2b.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.SaleReply;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2b.SaleDownService;

@Service("saleDownService")
public class SaleDownServiceImpl implements SaleDownService {
	@Autowired
	private BaseDao baseDao;

	/*
	 * 默认回复
	 */
	@Override
	public void replyAll(int id, String caller) {
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("SaleDownDetail left join saledown on sd_said=sa_id", new String[] {
				"sa_code", "sd_detno", "sd_qty", "sd_replyqty", "sd_delivery", "sd_id" }, "sd_said=" + id);
		String instrsql = "insert into saleReply (sr_qty,sr_delivery,sr_date,sr_sendstatus,sr_sacode,sr_sddetno,sr_recorder,sr_remark,sr_type)values(?,?,?,?,?,?,?,?,?)";
		for (final Object[] o : gridData) {
			if (Double.parseDouble(o[2] + "") > Double.parseDouble(o[3] + "")) {
				baseDao.getJdbcTemplate().update(instrsql, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setDouble(1, Double.parseDouble(o[2] + "") - Double.parseDouble(o[3] + ""));
						ps.setDate(2, new java.sql.Date(DateUtil.parse(o[4].toString(), null).getTime()));
						ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
						ps.setString(4, "待上传");
						ps.setString(5, o[0] + "");
						ps.setInt(6, Integer.parseInt(o[1] + ""));
						ps.setString(7, SystemSession.getUser().getEm_name());
						ps.setString(8, "默认回复");
						ps.setString(9, "供应商ERP回复");
					}
				});
				String formSql = "update saleDownDetail set sd_replydate=sd_delivery,sd_replyqty=sd_qty where sd_id=" + o[5];
				baseDao.execute(formSql);
			} else {

			}
		}
		// 记录操作
		baseDao.logger.update(caller, "sa_id", id);
		// 执行修改后的其它逻辑

	}

	@Override
	public void updateReplyInfo(String data, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data);
		Object[] datas = baseDao.getFieldsDataByCondition("saleDownDetail left join saledown on sd_said=sa_id ", new String[] { "sd_detno",
				"sd_qty", "sd_said", "sa_code", "sd_replyqty" }, "sd_id=" + store.get("sd_id"));
		boolean bool = Double.parseDouble(datas[1].toString()) < Double.parseDouble(datas[4].toString())
				+ Double.parseDouble(store.get("sd_replyqty").toString());
		if (bool) {
			BaseUtil.showError("回复数量不能大于客户采购数!");
		} else {
			baseDao.execute("update saleDownDetail set sd_replyqty=sd_replyqty+" + store.get("sd_replyqty") + ",sd_replydate=to_date('"
					+ store.get("sd_replydate") + "','yyyy-mm-dd'),sd_replydetail='" + StringUtil.nvl(store.get("sd_replydetail"), "")
					+ "' where sd_id=" + store.get("sd_id"));
			baseDao.execute("insert into saleReply (sr_qty,sr_delivery,sr_date,sr_recorder,sr_sacode,sr_sddetno,sr_remark,sr_sendstatus,sr_type)values("
					+ store.get("sd_replyqty")
					+ ",to_date('"
					+ store.get("sd_replydate")
					+ "','yyyy-mm-dd'),sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ datas[3]
					+ "',"
					+ datas[0]
					+ ",'"
					+ store.get("sd_replydetail")
					+ "','待上传','供应商ERP回复')");
		}
	}
	
	/*
	 * 打印
	 */
	@Override
	public String printSaleDown(int id, String caller) {
		String printUrl = "http://print.ubtob.com/report/print?userName=B2B";
		Object [] saleDown = baseDao.getFieldsDataByCondition("SaleDown", "sa_customeruu,b2b_pu_id", "sa_id="+id);
		Master master = SystemSession.getUser().getCurrentMaster();
		String envir = master.getMa_env();
		if (StringUtils.isEmpty(master.getMa_env())) {
			envir = "test";
		}
		try {
			printUrl += URLEncoder.encode("/" + saleDown[0].toString(), "UTF-8")+"&profile="+envir+"&reportName=PURCLIST&whereCondition="+URLEncoder.encode("where+purc$orders.pu_id="+saleDown[1].toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		//更新打印状态
		baseDao.updateByCondition("SaleDown", "sa_printstatuscode='PRINTED',sa_printstatus='"+BaseUtil.getLocalMessage("PRINTED")+"'", "sa_id="+id);
		// 记录操作
		baseDao.logger.print(caller,"sa_id", id);		
		return printUrl;
	}

	@Override
	public int turnSale(int id, String caller) {
		// TODO
		int piid = 0;
		String strsql = "update SaleDown set (sa_custid,sa_custcode,sa_custname,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_sellerid,sa_seller,sa_sellercode)=(select cu_id,cu_code,cu_name,cu_arcode,cu_arname,cu_shcustcode,cu_shcustname,cu_sellerid,cu_sellername,em_code from customer left join employee on em_id=cu_sellerid where cu_uu=sa_customeruu) where sa_id ="
				+ id;
		baseDao.execute(strsql);
		Object[] sa_custcode = baseDao.getFieldsDataByCondition("SaleDown", new String[] { "sa_custcode", "sa_custname" }, "sa_id=" + id);
		if (sa_custcode[0] == null) {
			BaseUtil.showError("客户资料不存在,请先维护客户资料!");
		} else {
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("SaleDownDetail", new String[] { "sd_prodcode", "sd_custprodcode" },
					"sd_said=" + id);
			if (objs != null) {
				for (Object[] os : objs) {
					if (!"".equals(os[0]) && os[0] != null) {
						String sql = "select * from productcustomer where pc_custcode='" + sa_custcode[0] + "' and pc_prodcode='" + os[0]
								+ "' and pc_custprodcode='" + os[1] + "'";
						SqlRowList rs = baseDao.queryForRowSet(sql);
						if (!rs.next()) {
							int pc_id = baseDao.getSeqId("PRODUCTCUSTOMER_SEQ");
							Object cu = baseDao.getFieldDataByCondition("Customer", "cu_id", "cu_code='" + sa_custcode[0] + "'");
							Object[] pr = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_id", "pr_detail", "pr_spec",
									"pr_unit" }, "pr_code='" + os[0] + "'");
							if(pr!=null){
								String str2 = "insert into ProductCustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
										+ "PC_CUSTPRODDETAIL,PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) " + "values("
										+ pc_id + "," + cu + ",1,'" + pr[0] + "'" + ",'" + os[1] + "','" + pr[1] + "','" + pr[2] + "','"
										+ pr[3] + "','" + sa_custcode[0] + "','" + sa_custcode[1] + "','" + os[0] + "')";
								baseDao.execute(str2);
							}else{
								BaseUtil.showError("物料："+os[0]+"不存在！");
							}
						}
					}
				}
			}
			checkProduct(id);
			Object code = baseDao.getFieldDataByCondition("SaleDown", "sa_code", "sa_id=" + id);
			code = baseDao.getFieldDataByCondition("Sale", "sa_id", "sa_sourcecode='" + code + "'");
			if (code != null && !code.equals("")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.Sales.haveturn"));
			} else {
				final Object[] formdata = baseDao.getFieldsDataByCondition("SaleDown", new String[] { "sa_code", "sa_date", "sa_currency",
						"sa_custcode", "sa_custname", "sa_sellercode", "sa_seller", "sa_rate","sa_payments","sa_paymentscode","sa_custid" }, "sa_id=" + id);
				if(StringUtil.hasText(formdata[2])&&!StringUtil.hasText(formdata[7])){
					Object rate = baseDao.getFieldDataByCondition("currencysmonth left join Currencys on cr_name=cm_crname", "cm_crrate", "cm_yearmonth = "+DateUtil.getYearmonth(formdata[1].toString())+" and cm_crname = '"+formdata[2]+"' and nvl(cr_statuscode,' ')='CANUSE'");
					if (rate==null||"".equals(rate.toString().trim())) {
						BaseUtil.showError("币别没有设置月度汇率，不允许转销售订单！");
					}else{
						baseDao.execute("update SaleDown set sa_rate = ? where sa_id = ?",rate,id);
						formdata[7] = rate;
					}
				}
				final Object[] payments = baseDao.getFieldsDataByCondition("Customer", new String[]{"cu_paymentscode","cu_payments"}, "cu_code='"+formdata[3]+"'");
				final Object[] cu = baseDao.getFieldsDataByCondition("Customer", new String[]{"cu_arcode","cu_arname","cu_shcustcode","cu_shcustname","cu_add1"}, "cu_code='"+formdata[3]+"'");
				final Object[] depart = baseDao.getFieldsDataByCondition("Employee", new String[]{"em_departmentcode","em_depart"}, "em_code='"+formdata[5]+"'");
				String formSql = "INSERT INTO sale (sa_code,sa_date,sa_currency,sa_custcode,sa_custname,sa_sellercode,sa_seller,sa_rate,"
						+ "sa_recorder,sa_recorddate,sa_id,sa_sourcecode,sa_pocode,sa_status,sa_statuscode,sa_sourcetype,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_toplace,sa_payments,sa_paymentscode,sa_departmentcode,sa_departmentname,sa_parentorname,sa_custid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				piid = baseDao.getSeqId("sale_SEQ");
				final int sa_id = piid;
				final String sacode = baseDao.sGetMaxNumber("Sale", 2);
				baseDao.getJdbcTemplate().update(formSql, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, sacode);
						ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
						ps.setString(3, formdata[2] + "");
						ps.setString(4, formdata[3] + "");
						ps.setString(5, formdata[4] + "");
						ps.setString(6, formdata[5] + "");
						ps.setString(7, formdata[6] + "");// pi_departmentname
						ps.setDouble(8, Double.parseDouble(formdata[7].toString()));
						ps.setString(9, SystemSession.getUser().getEm_name());
						ps.setDate(10, new java.sql.Date(new java.util.Date().getTime()));
						ps.setInt(11, sa_id);
						ps.setString(12, formdata[0] + "");
						ps.setString(13, formdata[0] + "");
						ps.setString(14, BaseUtil.getLocalMessage("ENTERING"));
						ps.setString(15, "ENTERING");
						ps.setString(16, "B2B商务");
						ps.setString(17, cu[0]+"");
						ps.setString(18, cu[1]+"");
						ps.setString(19, cu[2]+"");
						ps.setString(20, cu[3]+"");
						ps.setString(21, cu[4]+"");
						ps.setString(22, payments[1]+"");
						ps.setString(23, payments[0]+"");
						ps.setString(24, (depart!=null?depart[0]:"")+"");
						ps.setString(25, (depart!=null?depart[1]:"")+"");
						ps.setString(26, (depart!=null?depart[1]:"")+"");
						ps.setString(27, formdata[10] + "");
					}
				});
				List<Object[]> gridData = baseDao.getFieldsDatasByCondition("SaleDownDetail", new String[] { "sd_detno", "sd_prodcode",
						"sd_price", "sd_qty", "sd_custprodcode", "sd_id", "sd_taxrate", "sd_delivery" }, "sd_said=" + id);
				String gridSql = "INSERT INTO SaleDetail (sd_code,sd_id,sd_prodcode,sd_price,sd_qty,sd_custprodcode,sd_sourceid,sd_said,sd_taxrate,sd_detno,sd_delivery) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
				for (final Object[] o : gridData) {
					baseDao.getJdbcTemplate().update(gridSql, new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, sacode + "");
							ps.setInt(2, baseDao.getSeqId("SaleDetail_Seq"));
							ps.setString(3, o[1] + "");
							ps.setDouble(4, Double.parseDouble(o[2] + ""));
							ps.setDouble(5, Double.parseDouble(o[3] + ""));
							ps.setString(6, o[4] + "");
							ps.setInt(7, Integer.parseInt(o[5] + ""));
							ps.setInt(8, sa_id);
							ps.setDouble(9, Double.parseDouble(o[6] + ""));
							ps.setInt(10, Integer.parseInt(o[0] + ""));
							ps.setDate(11, new java.sql.Date(DateUtil.parse(o[7].toString(), null).getTime()));
						}
					});
				}
				// 更新转后订单的物料编号
				baseDao.updateByCondition("SaleDown", "sa_status='已转销售',sa_statuscode='TURNSA',sa_confirmstatus='已确认'", "sa_id=" + id);
				// 记录操作
				baseDao.logger.turn("转销售单", caller, "sa_id", id);
				baseDao.logger.turn("B2B平台订单转销售单", "Sale", "sa_id", sa_id);
			}

		}
		return piid;
	}

	@Override
	public List<SaleReply> findReplyBySaid(int id) {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select SaleReply.* from SaleReply left join SaleDownDetail on sr_sacode=sd_code and sr_sddetno=sd_detno where sd_said=? order by sd_detno,sr_date",
							new BeanPropertyRowMapper<SaleReply>(SaleReply.class), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 判断物料资料是否已按客户物料关系更新进去
	 */
	private void checkProduct(int sa_id) {
		baseDao.execute(
				"update SaleDown set (sa_custid,sa_custcode,sa_custname,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_sellerid,sa_seller)=(select cu_id,cu_code,cu_name,cu_arcode,cu_arname,cu_shcustcode,cu_shcustname,cu_sellerid,cu_sellername from customer where cu_uu=sa_customeruu) where sa_id = ?",
				sa_id);
		baseDao.execute(
				"update SaleDownDetail sd1 set (sd_prodid,sd_prodcode)=(select max(pc_prodid),max(pc_prodcode) from productcustomer,saledown,saledowndetail sd2 where sa_id=sd_said and pc_custcode=sa_custcode and pc_custprodcode=sd2.sd_custprodcode and sd1.sd_id=sd2.sd_id) where sd_said = ?",
				sa_id);
		String noneProduct = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sd_detno) from saledowndetail where sd_said=? and sd_prodid is null", String.class, sa_id);
		if (noneProduct != null)
			BaseUtil.showError("行：" + noneProduct + " 的物料还未建立【客户物料对照关系】");
	}

	@Override
	public String vastReplyInfo(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// StringBuffer sb = new StringBuffer();// 修改采购单状态
		for (Map<Object, Object> map : maps) {
			int sdid = Integer.parseInt(map.get("sd_id").toString());
			Object[] datas = baseDao.getFieldsDataByCondition("saleDownDetail left join saledown on sd_said=sa_id ", new String[] {
					"sd_detno", "sd_qty", "sd_said", "sa_code", "sd_replyqty" }, "sd_id=" + sdid);
			boolean bool = Double.parseDouble(datas[1].toString()) < Double.parseDouble(datas[4].toString())
					+ Double.parseDouble(map.get("sd_thisreplyqty").toString());
			if (bool) {
				BaseUtil.showError("回复数量不能大于客户采购数!");
			} else {
				baseDao.execute("update saleDownDetail set sd_replyqty=sd_replyqty+" + map.get("sd_thisreplyqty")
						+ ",sd_replydate=to_date('" + map.get("sd_replydate") + "','yyyy-mm-dd'),sd_replydetail='"
						+ StringUtil.nvl(map.get("sd_replydetail"), "") + "' where sd_id=" + map.get("sd_id"));
				baseDao.execute("insert into saleReply (sr_qty,sr_delivery,sr_date,sr_recorder,sr_sacode,sr_sddetno,sr_remark,sr_sendstatus,sr_type)values("
						+ map.get("sd_thisreplyqty")
						+ ",to_date('"
						+ map.get("sd_replydate")
						+ "','yyyy-mm-dd'),sysdate,'"
						+ SystemSession.getUser().getEm_name()
						+ "','"
						+ datas[3]
						+ "',"
						+ datas[0]
						+ ",'"
						+ map.get("sd_replydetail")
						+ "','待上传','供应商ERP回复')");
			}
		}
		return null;

	}

	@Override
	public void updateSaleDownById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object said = store.get("sa_id");
		baseDao.execute("update SaleDown set (sa_custcode,sa_custname)=(select max(cu_code),max(cu_name) from customer where cu_uu=sa_customeruu)"
				+ " where nvl(sa_custcode,' ')=' ' and sa_id = "+said);
		Object uu = baseDao.getFieldDataByCondition("SaleDown", "sa_customeruu", "sa_id=" + said);
		if (uu != null) {
			if (!baseDao.checkIf("customer", "cu_uu='" + uu + "'")) {
				BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
			}
		}
		Object cucode = store.get("sa_custcode");
		if (!StringUtil.hasText(cucode)) {
			BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "SaleDown", "sa_id"));
		List<String> updateSqls = new ArrayList<String>();
		List<String> updateSqls2 = new ArrayList<String>();
		StringBuffer errBuff = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			Object prodcode = s.get("sd_prodcode");
			Object custprodcode = s.get("sd_custprodcode");
			Object detno = s.get("sd_detno");
			if (!NumberUtil.isEmpty(s.get("sd_id")) && StringUtil.hasText(custprodcode)) {
				if (StringUtil.hasText(prodcode)) {
					if (baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "'  and pc_custprodcode='" + custprodcode + "'")) {
						if (baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "'  and pc_custprodcode='" + custprodcode + "' and pc_prodcode<>'"+prodcode+"'")) {
							errBuff.append("行[" + detno + "]物料[" + prodcode + "]已经有对应的客户产品料号，请更改或删除原来数据！<br>");
						}
					}else{
						if (baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_prodcode='"+prodcode+"'")) {
							errBuff.append("行[" + detno + "]物料[" + prodcode + "]已经有对应的客户产品料号，请更改或删除原来数据！<br>");
						}
					}
					if (!baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_custprodcode='" + custprodcode
							+ "' and pc_prodcode='" + prodcode + "'")) {
						Object i = baseDao.getFieldDataByCondition("productcustomer", "max(nvl(pc_detno,0))", "PC_CUSTCODE='" + cucode
								+ "'");
						i = i == null ? 0 : i;
						updateSqls2
								.add("Insert into productcustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
										+ "PC_CUSTPRODDETAIL,PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) "
										+ " select ProductCustomer_seq.nextval, cu_id,"
										+ (Integer.parseInt(i.toString()) + 1)
										+ ",pr_id,sd_custprodcode,sd_custproddetail,sd_prodcustcode,pr_unit,sa_custcode,sa_custname,"
										+ "sd_prodcode from saledown,saledownDetail,customer,product where sa_id=sd_said and sa_custcode=cu_code and sd_prodcode=pr_code"
										+ " AND sd_id=" + s.get("sd_id"));
					}
				} else {
					Object prcode = baseDao.getFieldDataByCondition("productcustomer", "pc_prodcode", "pc_custcode='" + cucode
							+ "' and pc_custprodcode='" + custprodcode + "'");
					if (StringUtil.hasText(prcode)) {
						s.put("sd_prodcode", prcode);
					} else {
						errBuff.append("行[" + detno + "]没有客户物料对照资料，请手工填写物料编号！");
					}
				}
				updateSqls.add(SqlUtil.getUpdateSqlByFormStore(s, "SaleDownDetail", "sd_id"));
			}
		}
		if (errBuff.length() > 0) {
			BaseUtil.showError(errBuff.toString());
		}
		baseDao.execute(updateSqls);
		if (updateSqls2 != null) {
			baseDao.execute(updateSqls2);
		}
		baseDao.execute("update SaleDownDetail set sd_prodcode=(select max(pc_prodcode) from productcustomer where pc_custcode='" + cucode
				+ "' and pc_custprodcode=sd_custprodcode) where nvl(sd_prodcode,' ')=' ' and nvl(sd_custprodcode,' ')<>' ' "
				+ "and exists (select 1 from SaleDown where sd_said=sa_id and sa_custcode='" + cucode + "')");
		Object rate = store.get("sa_rate");
		Object currency = store.get("sa_currency");
		Object date = store.get("sa_date");
		//记录日志
		baseDao.logger.update(caller, "sa_id", said);
		//检查汇率
		if(StringUtil.hasText(currency)&&!StringUtil.hasText(rate)){
			rate = baseDao.getFieldDataByCondition("currencysmonth left join Currencys on cr_name=cm_crname", "cm_crrate", "cm_yearmonth = "+DateUtil.getYearmonth(date.toString())+" and cm_crname = '"+currency+"' and nvl(cr_statuscode,' ')='CANUSE'");
			if (rate==null||"".equals(rate.toString().trim())) {
				BaseUtil.showError("币别没有设置月度汇率，请维护币别为["+currency+"]的月度汇率！");
			}else{
				baseDao.execute("update SaleDown set sa_rate = ? where sa_id = ?",rate,said);
			}
		}
	}

}
