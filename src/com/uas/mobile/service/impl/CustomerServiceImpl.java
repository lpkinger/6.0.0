package com.uas.mobile.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import bsh.ParseException;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.mobile.service.CustomerService;

@Service("mobileCustomerService")
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public List<Map<String, Object>> getCustomerbySeller(String sellercode) {
		String sql = "select distinct cu_code,cu_name,cu_contact,cu_add1,cu_nichestep,cu_tel from Customer left join Customerdistr on cu_id=cd_cuid where cd_sellercode='"
				+ sellercode + "' or cu_sellercode='" + sellercode + "'"
				+ "UNION "
				+ " select bc_custcode,bc_custname,bc_contact,bc_address,bc_currentprocess,bc_tel from businesschance where bc_domancode='"+sellercode+"'";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getCustomerDetail(String emcode, int start, int end, int type, int kind, int isSelected, String emplist) {
		String emName = employeeDao.getEmployeeByEmcode(emcode).getEm_name();

		// type 0-未成交，1-已成交，2-全部
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String condition = "";
		Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + emcode + "'");
		if (bool && kind == 1) {
			// 加一个参数isSelected判断，isSelected=1，则代表已选下属，取已选下属员工编号，否则只取自己
			List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(emcode);
			String emString = "";
			if (isSelected == 1) {
				emString = "'" + emplist + "',";
			} else {
				// 取所有下属
				/*
				 * for (Employee em : employeeList) { emString += "'" +
				 * em.getEm_code() + "',"; }
				 */
				// 未选择下属，则只取自己
				emString = "'" + emcode + "',";
			}
			emString = emString.substring(0, emString.lastIndexOf(","));
			condition = "where  (cd_sellercode in (" + emString + ") or cu_sellercode in (" + emString + "))";
		} else
			condition = "where  (cd_sellercode='" + emcode + "' or cu_sellercode='" + emcode + "')";
		if (type == 0)
			condition += " and not exists (select 1 from sale where sa_custcode=cu_code)";
		else if (type == 1)
			condition += " and exists (select 1 from sale where sa_custcode=cu_code)";

		Object count = baseDao
				.getCountByTable("(select distinct cu_code,cu_name,cu_contact,cu_lastdate,cu_nichecode,cu_nichestep from customer left join Customerdistr on cu_id=cd_cuid "
						+ condition + " )");
		List<Object[]> objs = baseDao
				.getFieldsDatasByCondition(
						"(select A.*,rownum rn "
								+ "from(select distinct cu_code,cu_name,cu_sellername,cu_lastdate,cu_nichecode,cu_nichestep,cu_auditstatus,cu_id from customer left join Customerdistr on cu_id=cd_cuid "
								+ condition + " order by cu_lastdate desc) A " + "where rownum<=" + end + ")", new String[] { "cu_code",
								"cu_name", "cu_sellername", "cu_lastdate", "cu_nichecode", "cu_nichestep", "cu_auditstatus", "cu_id" },
						"rn>" + start);
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("cu_code", obj[0]);
			map.put("cu_name", obj[1]);
			// map.put("cu_sellername", obj[2]);
			map.put("cu_sellername", emName);
			map.put("cu_lastdate", obj[3]);
			map.put("cu_nichecode", obj[4]);
			map.put("cu_nichestep", obj[5]);
			map.put("cu_auditstatus", obj[6]);
			map.put("cu_id", obj[7]);
			map.put("count", count);
			lists.add(map);
		}
		return lists;
	}

	// @Override
	// public Map<String, Object> getCustomerbycode(String cu_code) {
	// Employee employee = SystemSession.getUser();
	// String name = employee.getEm_name();
	//
	// Map<String, Object> modelMap = new HashMap<String, Object>();
	// Object[] data = baseDao.getFieldsDataByCondition("customer",
	// new String[] { "cu_code", "cu_name", "cu_contact", "cu_kind",
	// "cu_lastdate", "cu_source", "cu_add1", "cu_mobile",
	// "cu_tel", "cu_nichecode", "cu_nichestep",
	// "cu_shortname", "cu_kind", "cu_district",
	// "cu_payments", "cu_sellername", "cu_degree",
	// "cu_email", "cu_businesscode", "cu_currency",
	// "cu_taxrate", "cu_remark","cu_sellercode","cu_id","cu_paymentscode"},
	// "cu_code='" + cu_code
	// + "'");
	// Boolean bool = baseDao.checkIf("sale", "sa_custcode='" + cu_code + "'");
	// Object countcontact = baseDao.getCountByCondition("Contact",
	// "ct_cucode='" + cu_code + "'");
	// List<Object[]> contacts = baseDao.getFieldsDatasByCondition("Contact",
	// new String[] { "ct_name", "ct_sex", "ct_job", "ct_mobile",
	// "ct_personemail", "ct_officephone", "ct_officeemail" },
	// "ct_cucode='" + cu_code + "'");
	// List<Map<String, Object>> listContacts = new ArrayList<Map<String,
	// Object>>();
	// for (Object[] obj : contacts) {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("ct_name", obj[0]);
	// map.put("ct_sex", obj[1]);
	// map.put("ct_job", obj[2]);
	// map.put("ct_mobile", obj[3]);
	// map.put("ct_personemail", obj[4]);
	// map.put("ct_officephone", obj[5]);
	// map.put("ct_officeemail", obj[6]);
	// listContacts.add(map);
	//
	// }
	// modelMap.put("cu_code", data[0]);
	// modelMap.put("cu_name", data[1]);
	// modelMap.put("cu_contact", data[2]);
	// modelMap.put("cu_kind", data[3]);
	// modelMap.put("cu_lastdate", data[4]);
	// modelMap.put("cu_source", data[5]);
	// modelMap.put("cu_add1", data[6]);
	// modelMap.put("cu_mobile", data[7]);
	// modelMap.put("cu_tel", data[8]);
	// modelMap.put("cu_nichecode", data[9]);
	// modelMap.put("cu_nichestep", data[10]);
	// modelMap.put("cu_dealstatus", bool ? "成交" : "未成交");
	// modelMap.put("cu_countcontact", countcontact);
	// modelMap.put("cu_contacts", listContacts);
	//
	// Object visitrecord = baseDao
	// .getFieldDataByCondition(
	// "(visitrecord left join players on pl_vrid=vr_id left join customer on vr_cuname=cu_name)",
	// "count(*)",
	// "cu_code='"
	// + cu_code
	// + "' and vr_statuscode='AUDITED' and pl_name='"+name+"'");
	//
	// Object expense = baseDao
	// .getFieldDataByCondition(
	// "(visitrecord left join customer cu on vr_cuname=cu_name left join feeplease on vr_code=fp_sourcecode)",
	// "sum(fp_pleaseamount)",
	// "cu_code='"
	// + cu_code
	// +
	// "' and vr_statuscode='AUDITED' and fp_statuscode='AUDITED' and fp_pleaseman='"+name+"'");
	//
	// modelMap.put(
	// "bfcount",
	// visitrecord == null ? 0 : Integer
	// .parseInt(visitrecord.toString()));
	// modelMap.put("bxamount", expense == null ? 0
	// : Integer.parseInt(expense.toString()));
	//
	// modelMap.put("cu_shortname", data[11]);
	// modelMap.put("cu_kind", data[12]);
	// modelMap.put("cu_district", data[13]);
	// modelMap.put("cu_payments", data[14]);
	// modelMap.put("cu_sellername", data[15]);
	// modelMap.put("cu_degree", data[16]);
	// modelMap.put("cu_email", data[17]);
	// modelMap.put("cu_businesscode", data[18]);
	// modelMap.put("cu_currency", data[19]);
	// modelMap.put("cu_taxrate", data[20]);
	// modelMap.put("cu_remark", data[21]);
	// modelMap.put("cu_sellercode", data[22]);
	// modelMap.put("cu_id", data[23]);
	// modelMap.put("cu_paymentscode", data[24]);
	// return modelMap;
	//
	// }

	@Override
	public Map<String, Object> getCustomerbycode(String cu_code) {
		Employee employee = SystemSession.getUser();
		String name = employee.getEm_name();

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object[] data = baseDao.getFieldsDataByCondition("customer", new String[] { "cu_code", "cu_name", "cu_contact", "cu_kind",
				"cu_lastdate", "cu_source", "cu_add1", "cu_mobile", "cu_tel", "cu_nichecode", "cu_nichestep", "cu_shortname", "cu_kind",
				"cu_district", "cu_payments", "cu_sellername", "cu_degree", "cu_email", "cu_businesscode", "cu_currency", "cu_taxrate",
				"cu_remark", "cu_sellercode", "cu_id", "cu_paymentscode","cu_rate" }, "cu_code='" + cu_code + "'");

		// 取客户业务员,包括客户分配的不同业务员
		List<Object> seller = baseDao.getFieldDatasByCondition("(customerdistr left join customer customerdistr on cu_id=cd_cuid)",
				"cd_seller", "cu_code='" + cu_code + "'");
		String sellerName = "";
		for (Object selr : seller) {
			if (selr != null) {
				sellerName += selr.toString() + " ";
			}
		}

		Boolean bool = baseDao.checkIf("sale", "sa_custcode='" + cu_code + "'");
		Object countcontact = baseDao.getCountByCondition("Contact", "ct_cucode='" + cu_code + "'");
		List<Object[]> contacts = baseDao.getFieldsDatasByCondition("Contact", new String[] { "ct_name", "ct_sex", "ct_job", "ct_mobile",
				"ct_personemail", "ct_officephone", "ct_officeemail" }, "ct_cucode='" + cu_code + "'");
		List<Map<String, Object>> listContacts = new ArrayList<Map<String, Object>>();
		for (Object[] obj : contacts) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ct_name", obj[0]);
			map.put("ct_sex", obj[1]);
			map.put("ct_job", obj[2]);
			map.put("ct_mobile", obj[3]);
			map.put("ct_personemail", obj[4]);
			map.put("ct_officephone", obj[5]);
			map.put("ct_officeemail", obj[6]);
			listContacts.add(map);

		}
		modelMap.put("cu_code", data[0]);
		modelMap.put("cu_name", data[1]);
		modelMap.put("cu_contact", data[2]);
		modelMap.put("cu_kind", data[3]);
		modelMap.put("cu_lastdate", data[4]);
		modelMap.put("cu_source", data[5]);
		modelMap.put("cu_add1", data[6]);
		modelMap.put("cu_mobile", data[7]);
		modelMap.put("cu_tel", data[8]);
		modelMap.put("cu_nichecode", data[9]);
		modelMap.put("cu_nichestep", data[10]);
		modelMap.put("cu_dealstatus", bool ? "成交" : "未成交");
		modelMap.put("cu_countcontact", countcontact);
		modelMap.put("cu_contacts", listContacts);

		Object visitrecord = baseDao.getFieldDataByCondition(
				"(visitrecord left join players on pl_vrid=vr_id left join customer on vr_cuname=cu_name)", "count(*)", "cu_code='"
						+ cu_code + "' and vr_statuscode='AUDITED' and pl_name='" + name + "'");

		Object expense = baseDao.getFieldDataByCondition(
				"(visitrecord left join customer cu on vr_cuname=cu_name left join feeplease on vr_code=fp_sourcecode)",
				"sum(fp_pleaseamount)", "cu_code='" + cu_code
						+ "' and vr_statuscode='AUDITED' and fp_statuscode='AUDITED' and fp_pleaseman='" + name + "'");

		modelMap.put("bfcount", visitrecord == null ? 0 : Integer.parseInt(visitrecord.toString()));
		modelMap.put("bxamount", expense == null ? 0 : Integer.parseInt(expense.toString()));

		modelMap.put("cu_shortname", data[11]);
		modelMap.put("cu_kind", data[12]);
		modelMap.put("cu_district", data[13]);
		modelMap.put("cu_payments", data[14]);
		modelMap.put("cu_sellername", seller == null ? data[15] : sellerName);
		modelMap.put("cu_degree", data[16]);
		modelMap.put("cu_email", data[17]);
		modelMap.put("cu_businesscode", data[18]);
		modelMap.put("cu_currency", data[19]);
		modelMap.put("cu_taxrate", data[20]);
		modelMap.put("cu_remark", data[21]);
		modelMap.put("cu_sellercode", data[22]);
		modelMap.put("cu_id", data[23]);
		modelMap.put("cu_paymentscode", data[24]);
		return modelMap;

	}
	@Override
	public Map<String,Object> getDatasbycode(String custcode){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		Map<String,Object> Map=null;
		int nichecount=baseDao.getCountByCondition("businesschance", "bc_custcode='"+custcode+"'");
		int visitcount = baseDao.getCountByCondition("VisitRecord", "vr_cuuu='"+custcode+"'");
		List<Object[]> bccurrentprocess = baseDao.getFieldsDatasByCondition("businesschance", new String[]{"bc_currentprocess","BC_DESCRIPTION"}, "bc_custcode='"+custcode+"'");
		for(Object[] bp:bccurrentprocess){
			Map=new HashMap<String, Object>();
			Map.put("businessName", bp[1]);
			Map.put("businessProcess", bp[0]);
			list.add(Map);
		}
		modelMap.put("nichecount", nichecount);
		modelMap.put("visitcount", visitcount);
		modelMap.put("bccurrentprocess", list);
		return modelMap;
	}
	@Override
	public void saveVisitPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VisitPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save("VisitPlan", "vp_id", store.get("vp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public Map<String, Object> getRankList(String condition,Employee employee) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Master master=employee.getCurrentMaster();		
		List<Object> masters=new ArrayList<Object>();
		if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())){
			masters = baseDao.getFieldDatasByCondition("master", "ma_name", "ma_name is not null");
		}
		else masters.add(master.getMa_name());
		Object[] formula = baseDao.getFieldsDataByCondition("mobile_formula", new String[] { "mf_saleamount", "mf_profit" }, "rownum=1");		
		String saleFormula = (formula != null && formula[0] != null) ? formula[0].toString()
				: "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100))/10000";
		String profitFormula = (formula != null && formula[1] != null) ? formula[1].toString()
				: "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100))/10000";
				
		StringBuffer sb = new StringBuffer();		
		for (Object o : masters){
			if (sb.length() > 0)//"select sales,seller,position,depart,imid from "
				sb.append(" UNION ALL ");
			sb.append("select pi_sellername,em_position,em_depart,em_imid,pd_outqty,pd_inqty,pd_sendprice,pd_discount,pi_rate,pd_taxrate,pr_cost,pd_price,pd_vendorrate from "+o+".ProdInout,"+o+".ProdIoDetail,"+o+".Product,"+o+".employee where pi_id=pd_piid and pd_prodcode=pr_code and pi_sellercode=em_code and pi_class in ('出货单','销售退货单') and pi_status='已过账'  "
					+ condition + "  and pi_sellername is not null ");			
		}		
		Object[] sales = baseDao
				.getFieldsDataByCondition(
						"(select round( sum("
								+ saleFormula
								+ "),2) sales,pi_sellername seller,em_position position,em_depart depart,em_imid imid, row_number()  over(order by round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2) desc) rn   from (" + sb.toString() + ") group by pi_sellername,em_position,em_depart,em_imid)",
						new String[] { "sales", "seller", "position", "depart", "imid" }, "rn=1");
		Object[] profits = baseDao
				.getFieldsDataByCondition(
						"(select round( sum("
								+ profitFormula
								+ "),2) profits,pi_sellername seller,em_position position,em_depart depart,em_imid imid, row_number()  over(order by round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100)),2) desc) rn  from  (" + sb.toString() + ") group by pi_sellername,em_position,em_depart,em_imid)",
						new String[] { "profits", "seller", "position", "depart", "imid" }, "rn=1");
		modelMap.put("sales", sales);
		//modelMap.put("profits", profits);
		if (!(baseDao.isDBSetting("BusinessChance", "profits"))) {
			profits[0]=null;		
		}
		modelMap.put("profits", profits);
		return modelMap;
	}

	/*
	 * 获取拜访计划
	 */
	@Override
	public List<Map<String, Object>> getVisitPlan(Employee employee, String date, int start, int end) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + employee.getEm_code() + "'");
		String condition = "";
		if (bool) {
			List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(employee.getEm_code());
			condition += " and vp_visitman in (";
			for (Employee em : employeeList) {
				condition += "'" + em.getEm_name() + "',";
			}
			condition = condition.substring(0, condition.lastIndexOf(",")) + ")";
			condition += " and rownum>=" + start + " and rownum<" + end;
		} else {
			condition = " and vp_visitman='" + employee.getEm_name() + "'";
		}
		List<Object[]> listall = baseDao.getFieldsDatasByCondition("visitplan left join employee on vp_visitman=em_name", new String[] {
				"vp_custname", "vp_address", "vp_visitman", "em_depart", "vp_contact", "vp_nichestep", "vp_status", "vp_id" },
				"to_char(vp_date,'yyyymm')='" + date + "'" + condition);
		for (Object[] obj : listall) {
			map = new HashMap<String, Object>();
			map.put("visitman", obj[2]);
			map.put("depart", obj[3]);
			map.put("custname", obj[0]);
			map.put("address", obj[1]);
			map.put("vp_contact", obj[4]);
			map.put("vp_nichestep", obj[5]);
			map.put("vp_status", obj[6]);
			map.put("vp_id", obj[7]);
			lists.add(map);
		}
		return lists;
	}
	public List<Map<String, Object>> getContactPerson(String condition, int start,int end){
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if(condition!=null&&!("".equals(condition))){
			String sql="select distinct ct_name,ct_job,ct_mobile,ct_cucode,ct_cuname,ct_address FROM BusinessChanceContact WHERE "+condition+" and ct_name is not null and rownum>=" + start + " and rownum<" + end+"";
			SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(sql);
			while(rs.next()){
				map = new HashMap<String, Object>();
				map.put("ct_name", rs.getString("ct_name"));
				map.put("ct_job", rs.getString("ct_job"));
				map.put("ct_mobile", rs.getString("ct_mobile"));
				map.put("ct_cucode", rs.getString("ct_cucode"));
				map.put("ct_cuname", rs.getString("ct_cuname"));
				map.put("ct_address", rs.getString("ct_address"));
				lists.add(map);
			}
		}else{
			condition="1=1";
			String sql="select distinct ct_name,ct_job,ct_mobile,ct_cucode,ct_cuname,ct_address FROM BusinessChanceContact WHERE "+condition+" and ct_name is not null and rownum>=" + start + " and rownum<" + end+"";
			SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(sql);
			while(rs.next()){
				map = new HashMap<String, Object>();
				map.put("ct_name", rs.getString("ct_name"));
				map.put("ct_job", rs.getString("ct_job"));
				map.put("ct_mobile", rs.getString("ct_mobile"));
				map.put("ct_cucode", rs.getString("ct_cucode"));
				map.put("ct_cuname", rs.getString("ct_cuname"));
				map.put("ct_address", rs.getString("ct_address"));
				lists.add(map);
			}
		
		}
		
		return lists;
	}
	/*
	 * 获取客户商机描述
	 */
	public List<Map<String,Object>> getBusinesschanceBewrite(String custcode,String custname,int start,int end){
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
		Map<String,Object> map=null;
		List<Object[]> mdatas=null;
		if(custcode!=null){
			mdatas=baseDao.getFieldsDatasByCondition("ProductCustomer left join Product on pc_prodcode=pr_code", new String[] {"pr_detail","pr_spec","pr_brand"},"pc_custcode='"+custcode+"' and rownum<"+end+" and rownum>="+start+"");
		}else{
			mdatas=baseDao.getFieldsDatasByCondition("ProductCustomer left join Product on pc_prodcode=pr_code", new String[] {"pr_detail","pr_spec","pr_brand"},"pc_custname='"+custname+"' and rownum<"+end+" and rownum>="+start+"");
		}		
		for(Object[] mdata:mdatas){
			map=new HashMap<String,Object>();
			if(mdata[0]!=null&&mdata[1]!=null&&mdata[2]!=null){
				map.put("result", mdata[0]+"-"+mdata[1]+"-"+mdata[2]);
			}else if(mdata[0]!=null&&mdata[1]!=null&&mdata[2]==null){
				map.put("result", mdata[0]+"-"+mdata[1]);
			}else if(mdata[0]!=null&&mdata[1]==null&&mdata[2]==null){
				map.put("result", mdata[0]);
			}else if(mdata[0]==null&&mdata[1]!=null&&mdata[2]==null){
				map.put("result", mdata[1]);
			}
			list.add(map);
		}
		
		return list;
	}
	/*
	 * 获取拜访类型
	 */
	public List<Map<String,Object>> getVisitType(String custname,String custcode){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map=new HashMap<String,Object>();
		Boolean cbool=false;
		Boolean vbool=false;
		if(custcode!=null&&!("".equals(custcode))){			
				 cbool = baseDao.checkIf("customer", "cu_code='" + custcode + "'");
				 vbool = baseDao.checkIf("vendor", "ve_code='"+custcode+"'");	
				
		}else{
			 cbool = baseDao.checkIf("customer", "cu_name='" + custname + "'");
			 vbool = baseDao.checkIf("vendor", "ve_name='"+custname+"'");
			
		}
		if(cbool&&!vbool){
			map.put("type","客户拜访");
			map.put("typecode", "OfficeClerk");
		}else if(vbool&&!cbool){
			map.put("type", "原厂拜访");
			map.put("typecode", "VisitRecord!Vender");
		}else{
			map.put("type", false);
			map.put("typecode",null);
		}
		list.add(map);
		return list;
	}
	/*
	 * 获取任务列表
	 */
	@Override
	public List<Map<String, Object>> getTaskPlan(String emcode, String date, String status) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<Object[]> listdata = null;
		if (status.equals("1")) {
			listdata = baseDao.getFieldsDatasByCondition("projecttask", new String[] { "name", "enddate", "resourcename", "description",
					"handstatus" }, "resourcecode='" + emcode + "' and to_char(startdate,'yyyymm')<='" + date
					+ "' and to_char(enddate,'yyyymm')>='" + date + "' and taskorschedule='Task'");
		} else {
			listdata = baseDao.getFieldsDatasByCondition("projecttask", new String[] { "name", "enddate", "resourcename", "description",
					"handstatus" }, "resourcecode='" + emcode + "' and to_char(startdate,'yyyymm')<='" + date
					+ "' and to_char(enddate,'yyyymm')>='" + date + "' and taskorschedule='Task' and handstatuscode='" + status + "'");
		}

		Map<String, Object> map = null;
		for (Object[] obj : listdata) {
			map = new HashMap<String, Object>();
			map.put("taskname", obj[0]);
			map.put("enddate", obj[1]);
			map.put("handler", obj[2]);
			map.put("description", obj[3]);
			map.put("status", obj[4]);
			lists.add(map);
		}
		return lists;
	}

	/*
	 * 获取日程信息
	 */
	@Override
	public List<Map<String, Object>> getScheduleMsg(String emcode, String date, String status) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<Object[]> listdata = null;
		if (status.equals("1")) {
			listdata = baseDao.getFieldsDatasByCondition("projecttask", new String[] { "name", "enddate", "resourcename", "description",
					"handstatus" }, "resourcecode='" + emcode + "' and to_char(startdate,'yyyymm')<='" + date
					+ "' and to_char(enddate,'yyyymm')>='" + date + "' and taskorschedule='Schedule'");
		} else {
			listdata = baseDao.getFieldsDatasByCondition("projecttask", new String[] { "name", "enddate", "resourcename", "description",
					"handstatus" }, "resourcecode='" + emcode + "' and to_char(startdate,'yyyymm')<='" + date
					+ "' and to_char(enddate,'yyyymm')>='" + date + "' and taskorschedule='Schedule' and handstatuscode='" + status + "' ");
		}
		Map<String, Object> map = null;
		for (Object[] obj : listdata) {
			map = new HashMap<String, Object>();
			map.put("schetitle", obj[0]);
			map.put("enddate", obj[1]);
			map.put("handler", obj[2]);
			map.put("description", obj[3]);
			map.put("status", obj[4]);
			lists.add(map);
		}
		return lists;
	}

	/*
	 * 获取任务日程信息
	 */
	@Override
	public Map<String, Object> getTaskAndScheduleMsg(String emcode, String date, String type) {
		Object[] listdata = baseDao.getFieldsDataByCondition(
				"(select count(case when statuscode='FINISHED' then 1 else null end) countCompl,count(*) countAll from projecttask where resourcecode='"
						+ emcode + "' and to_char(startdate,'yyyymm')<='" + date + "' and to_char(enddate,'yyyymm')>='" + date
						+ "' and taskorschedule='" + type + "')", new String[] { "countAll", "countCompl", "countAll-countCompl",
						"round(countCompl/countAll,2)" }, "countAll>0");
		Map<String, Object> map = new HashMap<String, Object>();
		if (listdata != null) {
			map.put("taskandschenum", listdata[0]);
			map.put("completenum", listdata[1]);
			map.put("notcomplnum", listdata[2]);
			map.put("complrate", listdata[3]);
		}
		return map;
	}

	public Map<String, Object> getTaskAndScheduleAndVisitPlanMsg(String emcode, String date) {
		String emName = employeeDao.getEmployeeByEmcode(emcode).getEm_name();

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> listTasks = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSchedules = new ArrayList<Map<String, Object>>();
		// 获取拜访计划（如果有下属，则下属拜访计划一起返回）
		List<Map<String, Object>> listsVisitPlan = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listVisitRecord = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listOutSign = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> listTasksMine = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSchedulesMine = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listVisitPlanMine = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listVisitRecordMine = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listOutSignMine = new ArrayList<Map<String, Object>>();
		
		
		Map<String, Object> mapOther = new HashMap<String, Object>();
		Map<String, Object> mapMine = new HashMap<String, Object>();

		Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + emcode + "'");
		String condition = "";
		String taskAndScheCondition = "";
		String visitRecordCondition = "";
		String outSignCondition = "";

		if (bool) {
			//优化条件sql语句，处理下属过多的情况，取消原来的for循。
			condition += " and vp_visitman in (select em_name from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"
					+ emcode
					+ "') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职') ";
			visitRecordCondition += " and pl_name in(select em_name from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"
					+ emcode
					+ "') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职')";
			taskAndScheCondition += "resourcecode in (select em_code from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"
					+ emcode
					+ "') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职')";
			outSignCondition += "mp_recordercode in (select em_code from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"
					+ emcode
					+ "') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职')";
		} else {
			condition = " and vp_visitmancode='" + emcode + "'";
			taskAndScheCondition = "resourcecode='" + emcode + "'";
			visitRecordCondition = " and pl_name='" + emName + "'";
			outSignCondition = " mp_recordercode='" + emcode + "'";
		}
		List<Object[]> listall = baseDao.getFieldsDatasByCondition("visitplan", new String[] { "vp_id", "vp_status", "vp_date",
				"vp_startdate", "vp_enddate", "vp_address", "vp_visitman", "vp_custdept", "vp_custname", "vp_remark", "vp_custcode",
				"vp_nichestep", "vp_contact", "vp_visitmancode" }, "to_char(vp_startdate,'yyyymm')='" + date + "'" + condition
				+ " and nvl(vp_status,' ')<>'已拜访' order by vp_date desc");
		List<Object[]> visitRecordAll = baseDao.getFieldsDatasByCondition("visitrecord left join players on vr_id=pl_vrid", new String[] {
				"vr_id", "vr_cuname", "vr_visitplace", "pl_name", "vr_visittime", "vr_visitend", "vr_title", "vr_nichestep",
				"vr_nichecode", "vr_detail", "vr_cucontact", "vr_nichename","vr_class","vr_recorddate","vr_code","vr_tel","vr_status" }, "(to_char(vr_visittime,'yyyymm')='" + date + "' or to_char(VR_RECORDDATE,'yyyymm')='"+date+"')"
				+ visitRecordCondition + " order by vr_recorddate desc");
		/*String sql="select to_char(vr_recorddate,'yyyy-MM-dd HH:mm:ss') recorddate from visitrecord left join players on vr_id=pl_vrid where (to_char(vr_visittime,'yyyymm')='" + date + "' or to_char(VR_RECORDDATE,'yyyymm')='"+date+"')"
				+ visitRecordCondition + " order by vr_visittime desc";
		SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(sql);
		while(rs.next()){
			String recorddate=rs.getString("recorddate");
		}*/
		//外勤计划
		List<Object[]> outSignAll = baseDao.getFieldsDatasByCondition("mobile_outplan left join mobile_outplandetail on mp_id=mpd_mpid", 
					new String[]{"mp_code","mpd_company","mpd_address","mpd_distance","mpd_arrivedate","mpd_actdate","mpd_outdate","mp_recorder","mp_recordercode","mpd_id","mpd_status","mpd_remark","mpd_kind"}, 
					outSignCondition + " and to_char(mp_visittime,'yyyymm')=" + date + " order by mp_visittime desc");
		
		for(Object[] obj:outSignAll){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mp_code",obj[0]);
			map.put("mpd_company", obj[1]);
			map.put("mpd_address",obj[2]);
			map.put("mpd_distance",obj[3]);
			map.put("mpd_arrivedate",obj[4]);
			map.put("mpd_actdate",obj[5]);
			map.put("mpd_outdate",obj[6]);
			map.put("mp_recorder",obj[7]);
			map.put("mp_recordercode", obj[8]);
			map.put("mpd_id", obj[9]);
			map.put("mpd_status", obj[10]);
			map.put("mpd_remark", obj[11]);
			map.put("mpd_kind", obj[12]);
			if(emcode.equals(obj[8])){
				listOutSignMine.add(map);
			}else{
				listOutSign.add(map);
			}
		}
		
		
		for (Object[] obj : listall) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("vp_id", obj[0]);
			map.put("status", obj[1]);
			map.put("visitdate", obj[2]);
			map.put("startdate", obj[3]);
			map.put("enddate", obj[4]);
			map.put("address", obj[5]);
			map.put("recordman", obj[6]);
			map.put("department", obj[7]);
			map.put("client", obj[8]);
			map.put("remark", obj[9]);
			map.put("vp_custcode", obj[10]);
			map.put("nichestep", obj[11]);
			map.put("vp_contact", obj[12]);
			map.put("vp_visitmancode", obj[13]);
			if (emcode.equals(obj[13])) {
				listVisitPlanMine.add(map);
			} else {
				listsVisitPlan.add(map);
			}
		}

		for (Object[] objvr : visitRecordAll) {
			Map<String, Object> map = new HashMap<String, Object>();
			if(objvr[13]!=null){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 String time =objvr[13].toString();
				 try {
					Date a=sdf.parse(time);
					 String dateString = sdf.format(a);
					 map.put("vr_recorddate",dateString);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				map.put("vr_recorddate",null);
			}
			map.put("id", objvr[0]);
			map.put("custname", objvr[1]);
			map.put("address", objvr[2]);
			map.put("visitman", objvr[3]);
			map.put("vr_visittime", objvr[4]);
			map.put("vr_visitend", objvr[5]);
			map.put("vr_title", objvr[6]);
			map.put("vr_nichestep", objvr[7]);
			map.put("vr_nichecode", objvr[8]);
			map.put("vr_detail", objvr[9]);
			map.put("vr_cucontact", objvr[10]);
			map.put("vr_nichename", objvr[11]);
			map.put("vr_code", objvr[14]);
			map.put("vr_tel", objvr[15]);
			map.put("vr_status", objvr[16]);
			if(objvr[12]!=null){
				if("VisitRecord!Vender".equals(objvr[12].toString())){
					map.put("vr_class", "原厂拜访");
				}else if("OfficeClerk".equals(objvr[12].toString())){
					map.put("vr_class", "客户拜访");
				}
			}else{
				map.put("vr_class", null);
			}
			if (emName != null & objvr[3] != null) {
				if (emName.equals(objvr[3].toString())) {
					listVisitRecordMine.add(map);
				} else {
					listVisitRecord.add(map);
				}
			}
		}

		// 获取任务日程
		List<Object[]> tasksAndSchedule = baseDao.getFieldsDatasByCondition(
				"projecttask left join resourceassignment on projecttask.id= resourceassignment.ra_taskid", new String[] {
						"taskorschedule", "taskcode", "handstatus", "name", "startdate", "recorder", "description", "enddate",
						"resourcename", "resourcecode", "ra_id", "ra_resourcecode", "ra_taskid", "ra_statuscode" }, taskAndScheCondition
						+ " and to_char(startdate,'yyyymm')<='" + date + "' and to_char(enddate,'yyyymm')>='" + date
						+ "' order by recorddate desc");
		for (Object obj[] : tasksAndSchedule) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taskcode", obj[1]);
			map.put("status", obj[2]);
			map.put("name", obj[3]);
			map.put("startdate", obj[4]);
			map.put("recorder", obj[5]);
			map.put("description", obj[6]);
			map.put("enddate", obj[7]);
			map.put("handler", obj[8]);
			map.put("resourcecode", obj[9]);
			map.put("ra_id", obj[10]);
			map.put("ra_resourcecode", obj[11]);
			map.put("ra_taskid", obj[12]);
			map.put("ra_statuscode", obj[13]);
			if ("Task".equals(obj[0]) || "MTask".equals(obj[0])) {
				if (emcode.equals(obj[9])) {
					listTasksMine.add(map);
				} else {
					listTasks.add(map);
				}

			} else if ("Schedule".equals(obj[0])) {
				if (emcode.equals(obj[9])) {
					listSchedulesMine.add(map);
				} else {
					listSchedules.add(map);
				}
			}

		}

		mapOther.put("task", listTasks);
		mapOther.put("schedule", listSchedules);
		mapOther.put("visitPlan", listsVisitPlan);
		mapOther.put("visitRecord", listVisitRecord);
		mapOther.put("outplan", listOutSign);
		
		mapMine.put("task", listTasksMine);
		mapMine.put("schedule", listSchedulesMine);
		mapMine.put("visitPlan", listVisitPlanMine);
		mapMine.put("visitRecord", listVisitRecordMine);
		mapMine.put("outplan", listOutSignMine);

		modelMap.put("other", mapOther);
		modelMap.put("me", mapMine);
		return modelMap;
	}

	/**
	 * 我的排名(ver1.1)
	 */
	@Override
	public Map<String, Object> getPersonalRank(String emcode, String yearmonth,Employee employee) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		/*String isGroup = BaseUtil.getXmlSetting("group");//判断是否是集团账套
		String currentMaster=employee.getCurrentMaster().getMa_name();
		String defaultSob = ("true".equals(isGroup))?BaseUtil.getXmlSetting("defaultSob"):currentMaster;
		List<Object> masters=new ArrayList<Object>();
		if("true".equals(isGroup)){
			masters = baseDao.getFieldDatasByCondition(defaultSob + ".master", "ma_name", "ma_name is not null");
		}
		else masters.add(currentMaster);*/
		Master master=employee.getCurrentMaster();		
		List<Object> masters=new ArrayList<Object>();
		if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())){
			masters = baseDao.getFieldDatasByCondition("master", "ma_name", "ma_name is not null");
		}
		else masters.add(master.getMa_name());
		//集团版统一取集团中心公式
		Object[] formula = baseDao.getFieldsDataByCondition("mobile_formula", new String[] { "mf_saleamount", "mf_profit" }, "rownum=1");
		String saleFormula = null;
		String profitFormula = null;
		if (formula == null) {
			saleFormula = "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100))/10000";
			profitFormula = "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100))/10000";
		} else if (formula[0] == null && formula[1] != null) {
			saleFormula = "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100))/10000";
			profitFormula = formula[1].toString();
		} else if (formula[1] == null && formula[0] != null) {
			profitFormula = "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100))/10000";
			saleFormula = formula[0].toString();
		} else {
			saleFormula = formula[0].toString();
			profitFormula = formula[1].toString();
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer rb = new StringBuffer();
		StringBuffer vb = new StringBuffer();
		for (Object o : masters){
			if (sb.length() > 0)//"select sales,seller,position,depart,imid from "
				sb.append(" UNION ALL ");
			sb.append("select pi_sellername,em_code,pd_outqty,pd_inqty,pd_discount,pi_rate,pd_taxrate,pd_price,pr_cost,pd_vendorrate,pd_sendprice from "+o+".ProdInout left join "+o+".ProdIoDetail on pi_id=pd_piid left join "+o+".employee on pi_sellercode=em_code,"+o+".Product  where pi_id=pd_piid and pd_prodcode=pr_code and pi_sellercode=em_code and pi_class in ('出货单','销售退货单') and pi_status='已过账'  and to_char(pi_date,'yyyymm')='"
					+ yearmonth + "' and pi_sellername is not null ");
			
			if(rb.length()>0)
				rb.append(" UNION ALL ");
				rb.append("select SM_OVER,CR_RATE,em_name,em_code from "+o+".sellermonth left join "+o+".currencys on sm_currency=cr_name left join "+o+".customer on sm_custcode=cu_code left join "+o+".employee on sm_sellercode=em_code where to_char(sm_yearmonth) ='"
								+ yearmonth + "' and em_code is not null and em_name is not null ");
			
			if(vb.length()>0)
				vb.append(" UNION ALL ");
				vb.append("select pl_name,em_name,em_code,vr_cuuu from "+o+".players left join "+o+".employee on pl_name=em_name left join "+o+".visitrecord on pl_vrid=vr_id where vr_statuscode='AUDITED' and to_char(vr_visittime,'yyyymm')='"
								+ yearmonth + "' and em_code is not null and em_name is not null ");
			
			
		}	
		List<Object[]> sales = baseDao
				.getFieldsDatasByCondition(
						"(select round( sum("
								+ saleFormula
								+ "),2) sales,pi_sellername emname, row_number()over(order by round( sum("
								+ saleFormula
								+ "),2) desc) rn,em_code emcode from ("+ sb.toString() +") group by pi_sellername,em_code)",
								new String[] { "sales","emname", "emcode", "rn" }, "rn=1 or emcode='" + emcode + "'");
		List<Object[]> profits = baseDao
				.getFieldsDatasByCondition(
						"(select round( sum("
								+ profitFormula
								+ "),2) profits,pi_sellername emname,em_code emcode, row_number()  over(order by round(sum("
								+ profitFormula
								+ "),2) desc) rn  from ("+ sb.toString() +")group by pi_sellername,em_code)",
								new String[] { "profits","emname", "emcode", "rn" }, "rn=1 or emcode='" + emcode + "'");
		List<Object[]> receivables = baseDao
				.getFieldsDatasByCondition(
						"(select round(sum(SM_OVER*CR_RATE)/10000,2) as receivables ,em_name emname,em_code emcode, row_number() over(order by round(sum(SM_OVER*CR_RATE),2) desc) rn  from ("+ rb.toString() +")group by  em_code,em_name)", new String[] {
								"receivables", "emname", "emcode", "rn" }, "rn=1 or emcode='" + emcode + "'");
		List<Object[]> visits = baseDao
				.getFieldsDatasByCondition(
						"(select pl_name,em_name emname,em_code emcode,count(pl_name) countrecordfre,count(distinct vr_cuuu) countdisnum,rank() over(order by count(distinct vr_cuuu) desc) rank from ("+ vb.toString() +")group by pl_name,em_code,em_name)",
						new String[] { "emname", "emcode", "countrecordfre", "countdisnum", "rank" }, "emcode='" + emcode + "' or rownum=1");
		modelMap.put("sales", sales);
		// 判断是否需要显示毛利润
		if (!(baseDao.isDBSetting("BusinessChance", "profits"))) {
			modelMap.put("profits", profits);
		}
		//modelMap.put("profits", profits);
		modelMap.put("receivables", receivables);
		modelMap.put("visits", visits);
		return modelMap;
	}

	/**
	 * 月销售简报
	 */
	@Override
	public Map<String, Object> getSalesKit(String emcode, String yearmonth,Employee employee) {
		Employee emp = employeeDao.getEmployeeByEmcode(emcode);
		String name = emp.getEm_name();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Master master=employee.getCurrentMaster();		
		List<Object> masters=new ArrayList<Object>();
		if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())){
			masters = baseDao.getFieldDatasByCondition("master", "ma_name", "ma_name is not null");
		}
		else masters.add(master.getMa_name());
		StringBuffer a=new StringBuffer();
		StringBuffer b =new StringBuffer();
		StringBuffer c=new StringBuffer();
		StringBuffer d=new StringBuffer();
		StringBuffer e =new StringBuffer();
		StringBuffer f=new StringBuffer();
		StringBuffer g=new StringBuffer();
		StringBuffer h=new StringBuffer();
		for(Object o : masters){
			if(a.length()>0) a.append(" UNION ALL ");
			a.append("select distinct cu_code from "+o+".Customer left join "+o+".Customerdistr on cu_id=cd_cuid where (cd_sellercode='"
						+ emcode
						+ "' or cu_sellercode='"
						+ emcode
						+ "') and to_char(cu_auditdate,'yyyymm')='"
						+ yearmonth
						+ "' and cu_auditstatuscode='AUDITED'");
			if(b.length()>0) b.append(" UNION ALL ");
			b.append("select count(1) from "+ o +".Contact left join "+ o +".Customer on ct_cucode=cu_code left join "+ o +".Customerdistr on cu_id=cd_cuid where to_char(cu_auditdate,'yyyymm')='"
					+ yearmonth + "' and (cd_sellercode='" + emcode
					+ "' or cu_sellercode='" + emcode + "')");
			if(c.length()>0) c.append(" UNION ALL ");
			c.append("select count(1) from "+ o +".BusinessChance where bc_domancode='" + emcode + "' and to_char(bc_lastdate,'yyyymm')='"
				+ yearmonth + "'");
			if(d.length()>0) d.append(" UNION ALL ");
			d.append("select count(1) from "+o+".sale where to_char(sa_auditdate,'yyyymm')='" + yearmonth + "' and sa_sellercode='"
				+ emcode + "'");
			if(e.length()>0) e.append(" UNION ALL ");
			e.append("select vr_cuuu from "+o+".Players left join "+o+".visitrecord on vr_id=pl_vrid left join "+o+".employee on em_name=pl_name where to_char(vr_visittime,'yyyymm')='"
								+ yearmonth + "' and vr_statuscode='AUDITED' and em_code='" + emcode + "'");
			if(f.length()>0) f.append(" UNION ALL ");
			f.append("select pd_outqty,pd_inqty,pd_sendprice,pd_discount,pi_rate,pd_taxrate from "+o+".ProdInout,"+o+".ProdIoDetail where pi_id=pd_piid and pi_sellercode='" + emcode
				+ "' and pi_class in ('出货单') and pi_status='已过账'  and to_char(pi_date,'yyyymm')='" + yearmonth + "'");
			if(g.length()>0) g.append(" UNION ALL ");
			g.append("select hcd_n4 from "+o+".HKCZ,"+o+".HKCZdet where hc_id=hcd_hcid and hcd_emcode='" + emcode
						+ "' and to_char(hcd_date2,'yyyymm')='" + yearmonth + "'");
			if(h.length()>0) h.append(" UNION ALL ");
			h.append("select count(1) from "+o+".businesschancedata left join "+o+".businesschance on bc_code=bcd_bccode where to_char(bc_recorddate,'yyyymm')='" + yearmonth + "' and bc_recorder='" + name + "'");
		}
		Object formula = baseDao.getFieldDataByCondition("mobile_formula", "mf_saleamount", "rownum=1");
		String saleFormula = null;
		if (formula == null) {
			saleFormula = "((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100))/10000";
		} else {
			saleFormula = formula.toString();
		}
		int customercount = baseDao.getCount("select count(1) from ("+ a.toString() +")");
		int contactcount = baseDao.getCount(""+b.toString()+"");
		int nichecount = baseDao.getCount(""+c.toString()+"");
		int salecount = baseDao.getCount(""+d.toString()+"");
		Object[] visit = baseDao
				.getFieldsDataByCondition(
						"(SELECT count(*) vistercount,count(distinct vr_cuuu) topbfcount FROM ("+e.toString()+"))", new String[] { "vistercount",
								"topbfcount" }, "1=1");
		Object saleamount = baseDao.getFieldDataByCondition("(select round( sum(" + saleFormula
				+ "),2) saleamount from ("+f.toString()+"))", "saleamount", "1=1");
		Object backamount = baseDao.getFieldDataByCondition(
				"(select round (sum(hcd_n4/10000),2) backamount from ("+g.toString()+"))", "backamount", "1=1");

		int nichechangecount = baseDao.getCount(""+h.toString()+"");
		modelMap.put("customercount", customercount);
		modelMap.put("contactcount", contactcount);
		modelMap.put("nichecount", nichecount);
		modelMap.put("nichechangecount", nichechangecount);
		modelMap.put("salecount", salecount);
		modelMap.put("visit", visit);
		modelMap.put("saleamount", saleamount);
		modelMap.put("backamount", backamount);
		return modelMap;
	}

	/**
	 * 我的下属和我的指标
	 */
	@Override
	public Map<String, Object> getTargets(String emcode, String yearmonth, int start, int end,Employee employee) {
		String year = yearmonth.substring(0, 4);
		String month = yearmonth.substring(4);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> lists1 = new ArrayList<Map<String, Object>>();
		Master master=employee.getCurrentMaster();		
		List<Object> masters=new ArrayList<Object>();
		if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())){
			masters = baseDao.getFieldDatasByCondition("master", "ma_name", "ma_name is not null");
		}
		else masters.add(master.getMa_name());
		StringBuffer sb=new StringBuffer();
		StringBuffer cb=new StringBuffer();
		Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + emcode + "'");
		String condition = "";
		if (bool) {
			List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(emcode);
			condition += " (";
			for (Employee em : employeeList) {
				if (!emcode.equals(em.getEm_code())) {
					condition += "'" + em.getEm_code() + "',";
				}
			}
			condition = condition.substring(0, condition.lastIndexOf(",")) + ")";
			for(Object o : masters){
				if(sb.length()>0) sb.append(" UNION ALL ");
				sb.append("select  em_code,em_name,em_depart,pd_outqty,pd_inqty,pd_sendprice,pd_discount,pi_rate,pd_taxrate,pd_price,pr_cost,PD_VENDORRATE,pi_sellercode from "+o+".ProdInout left join "+o+".ProdIoDetail on pi_id=pd_piid left join "+o+".employee on pi_sellercode=em_code,"+o+".Product  where pi_id=pd_piid and pd_prodcode=pr_code and pi_class in ('出货单','销售退货单') and pi_status='已过账'  and to_char(pi_date,'yyyymm')='"
					+ yearmonth
					+ "' and pi_sellercode in "
					+ condition
					+ " ");
			}
			String sql = "select * from (select em_code,em_name customercount,em_depart bfcount,rank,firstbfcount,topcount,actualprofit, rn from (select round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2)/10000 firstbfcount,round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100)),2)/10000 actualprofit,pi_sellercode, row_number()over(order by round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2) desc) rn from ("+sb.toString()+") group by pi_sellercode) full join (select round(sum(case when 01="
					+ month
					+ " then mhd_total1 when 02="
					+ month
					+ " then mhd_total2 when 03="
					+ month
					+ " then mhd_total3 when 04="
					+ month
					+ " then mhd_total4 when 05="
					+ month
					+ " then mhd_total5 when 06="
					+ month
					+ " then mhd_total6 when 07="
					+ month
					+ " then mhd_total7 when 08="
					+ month
					+ " then mhd_total8 when 09="
					+ month
					+ " then mhd_total9 when 10="
					+ month
					+ " then mhd_total10 when 11="
					+ month
					+ " then mhd_total11 when 12="
					+ month
					+ " then mhd_total12 end)/10000,2) rank,round(sum(case when 01="
					+ month
					+ " then mhd_qty1 when 02="
					+ month
					+ " then mhd_qty2 when 03="
					+ month
					+ " then mhd_qty3 when 04="
					+ month
					+ " then mhd_qty4 when 05="
					+ month
					+ " then mhd_qty5 when 06="
					+ month
					+ " then mhd_qty6 when 07="
					+ month
					+ " then mhd_qty7 when 08="
					+ month
					+ " then mhd_qty8 when 09="
					+ month
					+ " then mhd_qty9 when 10="
					+ month
					+ " then mhd_qty10 when 11="
					+ month
					+ " then mhd_qty11 when 12="
					+ month
					+ " then mhd_qty12 end)/10000,2)topcount,mh_sellercode from Merchandising,MERCHANDISINGDETAIL where mhd_mhid=mh_id and mh_year='"
					+ year
					+ "' and mh_sellercode in "
					+ condition
					+ " group by mh_sellercode) on mh_sellercode=pi_sellercode left join employee on (em_code=mh_sellercode or em_code=pi_sellercode))where rn>="
					+ start + " and rn<" + end;
			String sqlgetdbseting="select * from (select em_code,em_name customercount,em_depart bfcount,rank,firstbfcount,topcount, rn from (select round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2)/10000 firstbfcount,round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100)),2)/10000 actualprofit,pi_sellercode, row_number()over(order by round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2) desc) rn from ("+sb.toString()+") group by pi_sellercode) full join (select round(sum(case when 01="
					+ month
					+ " then mhd_total1 when 02="
					+ month
					+ " then mhd_total2 when 03="
					+ month
					+ " then mhd_total3 when 04="
					+ month
					+ " then mhd_total4 when 05="
					+ month
					+ " then mhd_total5 when 06="
					+ month
					+ " then mhd_total6 when 07="
					+ month
					+ " then mhd_total7 when 08="
					+ month
					+ " then mhd_total8 when 09="
					+ month
					+ " then mhd_total9 when 10="
					+ month
					+ " then mhd_total10 when 11="
					+ month
					+ " then mhd_total11 when 12="
					+ month
					+ " then mhd_total12 end)/10000,2) rank,round(sum(case when 01="
					+ month
					+ " then mhd_qty1 when 02="
					+ month
					+ " then mhd_qty2 when 03="
					+ month
					+ " then mhd_qty3 when 04="
					+ month
					+ " then mhd_qty4 when 05="
					+ month
					+ " then mhd_qty5 when 06="
					+ month
					+ " then mhd_qty6 when 07="
					+ month
					+ " then mhd_qty7 when 08="
					+ month
					+ " then mhd_qty8 when 09="
					+ month
					+ " then mhd_qty9 when 10="
					+ month
					+ " then mhd_qty10 when 11="
					+ month
					+ " then mhd_qty11 when 12="
					+ month
					+ " then mhd_qty12 end)/10000,2)topcount,mh_sellercode from Merchandising,MERCHANDISINGDETAIL where mhd_mhid=mh_id and mh_year='"
					+ year
					+ "' and mh_sellercode in "
					+ condition
					+ " group by mh_sellercode) on mh_sellercode=pi_sellercode left join employee on (em_code=mh_sellercode or em_code=pi_sellercode))where rn>="
					+ start + " and rn<" + end;
			// 判断是否需要显示实际毛利润
			if (baseDao.isDBSetting("BusinessChance", "actualprofit")) {
				lists = baseDao.getJdbcTemplate().queryForList(sqlgetdbseting);
			}else{
				lists = baseDao.getJdbcTemplate().queryForList(sql);
				}
		}
		for(Object o : masters){
			if(cb.length()>0) cb.append(" UNION ALL ");
			cb.append("select  em_code,em_name,em_depart,pd_outqty,pd_inqty,pd_sendprice,pd_discount,pi_rate,pd_taxrate,pd_price,pr_cost,PD_VENDORRATE,pi_sellercode from "+o+".ProdInout left join "+o+".ProdIoDetail on pi_id=pd_piid left join "+o+".employee on pi_sellercode=em_code,"+o+".Product  where pi_id=pd_piid and pd_prodcode=pr_code and pi_class in ('出货单','销售退货单') and pi_status='已过账'  and to_char(pi_date,'yyyymm')='"
				+ yearmonth
				+ "' and pi_sellercode ='"
				+ emcode
				+ "'");
		}
		String sql1 = "select em_code,em_name customercount,em_depart bfcount,rank,firstbfcount,topcount,actualprofit from (select round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2)/10000 firstbfcount,round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100)),2)/10000 actualprofit,pi_sellercode from ("+cb.toString()+") group by pi_sellercode) full join (select round(sum(case when 01="
				+ month
				+ " then mhd_total1 when 02="
				+ month
				+ " then mhd_total2 when 03="
				+ month
				+ " then mhd_total3 when 04="
				+ month
				+ " then mhd_total4 when 05="
				+ month
				+ " then mhd_total5 when 06="
				+ month
				+ " then mhd_total6 when 07="
				+ month
				+ " then mhd_total7 when 08="
				+ month
				+ " then mhd_total8 when 09="
				+ month
				+ " then mhd_total9 when 10="
				+ month
				+ " then mhd_total10 when 11="
				+ month
				+ " then mhd_total11 when 12="
				+ month
				+ " then mhd_total12 end)/10000,2) rank,round(sum(case when 01="
				+ month
				+ " then mhd_qty1 when 02="
				+ month
				+ " then mhd_qty2 when 03="
				+ month
				+ " then mhd_qty3 when 04="
				+ month
				+ " then mhd_qty4 when 05="
				+ month
				+ " then mhd_qty5 when 06="
				+ month
				+ " then mhd_qty6 when 07="
				+ month
				+ " then mhd_qty7 when 08="
				+ month
				+ " then mhd_qty8 when 09="
				+ month
				+ " then mhd_qty9 when 10="
				+ month
				+ " then mhd_qty10 when 11="
				+ month
				+ " then mhd_qty11 when 12="
				+ month
				+ " then mhd_qty12 end)/10000,2)topcount,mh_sellercode from Merchandising,MERCHANDISINGDETAIL where mhd_mhid=mh_id and mh_year='"
				+ year
				+ "' and mh_sellercode='"
				+ emcode
				+ "' group by mh_sellercode) on mh_sellercode=pi_sellercode left join employee on (em_code=mh_sellercode or em_code=pi_sellercode)";
		String sqlgetdbseting1="select em_code,em_name customercount,em_depart bfcount,rank,firstbfcount,topcount from (select round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)),2)/10000 firstbfcount,round( sum((pd_outqty-pd_inqty)*pd_sendprice*(1-nvl(pd_discount,0)/100)*pi_rate/(1+nvl(pd_taxrate,0)/100)-(case when pd_price > 0 then pd_price else pr_cost end)*(pd_outqty-pd_inqty)*(1+nvl(PD_VENDORRATE,0)/100)),2)/10000 actualprofit,pi_sellercode from ("+cb.toString()+") group by pi_sellercode) full join (select round(sum(case when 01="
				+ month
				+ " then mhd_total1 when 02="
				+ month
				+ " then mhd_total2 when 03="
				+ month
				+ " then mhd_total3 when 04="
				+ month
				+ " then mhd_total4 when 05="
				+ month
				+ " then mhd_total5 when 06="
				+ month
				+ " then mhd_total6 when 07="
				+ month
				+ " then mhd_total7 when 08="
				+ month
				+ " then mhd_total8 when 09="
				+ month
				+ " then mhd_total9 when 10="
				+ month
				+ " then mhd_total10 when 11="
				+ month
				+ " then mhd_total11 when 12="
				+ month
				+ " then mhd_total12 end)/10000,2) rank,round(sum(case when 01="
				+ month
				+ " then mhd_qty1 when 02="
				+ month
				+ " then mhd_qty2 when 03="
				+ month
				+ " then mhd_qty3 when 04="
				+ month
				+ " then mhd_qty4 when 05="
				+ month
				+ " then mhd_qty5 when 06="
				+ month
				+ " then mhd_qty6 when 07="
				+ month
				+ " then mhd_qty7 when 08="
				+ month
				+ " then mhd_qty8 when 09="
				+ month
				+ " then mhd_qty9 when 10="
				+ month
				+ " then mhd_qty10 when 11="
				+ month
				+ " then mhd_qty11 when 12="
				+ month
				+ " then mhd_qty12 end)/10000,2)topcount,mh_sellercode from Merchandising,MERCHANDISINGDETAIL where mhd_mhid=mh_id and mh_year='"
				+ year
				+ "' and mh_sellercode='"
				+ emcode
				+ "' group by mh_sellercode) on mh_sellercode=pi_sellercode left join employee on (em_code=mh_sellercode or em_code=pi_sellercode)";
		// 判断是否需要显示实际毛利润
		if (baseDao.isDBSetting("BusinessChance", "actualprofit")) {
			lists1 = baseDao.getJdbcTemplate().queryForList(sqlgetdbseting1);
		}else{
			lists1 = baseDao.getJdbcTemplate().queryForList(sql1);
		}
		
			modelMap.put("subs", lists);	
			modelMap.put("target", lists1);
		
		return modelMap;
	}

	@Override
	public Map<String, Object> getInactionCusts(String emcode, int start, int end,Employee employee) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Master master=employee.getCurrentMaster();		
		List<Object> masters=new ArrayList<Object>();
		if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())){
			masters = baseDao.getFieldDatasByCondition("master", "ma_name", "ma_name is not null");
		}
		else masters.add(master.getMa_name());
		Object cusnum = baseDao.getFieldDataByCondition("customer left join customerdistr on cu_id=cd_cuid", "count(distinct cu_name)",
				"cd_sellercode='" + emcode + "' and to_char(sysdate,'yyyymmdd')-to_char(cu_lastdate,'yyyymmdd')>cu_standarddate");
		List<Object[]> cusdatas = baseDao.getFieldsDatasByCondition("customer left join customerdistr on cu_id=cd_cuid", new String[] {
				"cu_name", "to_char(cu_lastdate,'yyyy-mm-dd')" }, "cd_sellercode='" + emcode
				+ "' and to_char(sysdate,'yyyymmdd')-to_char(cu_lastdate,'yyyymmdd')>cu_standarddate and rownum>=" + start
				+ " and rownum<=" + end + " order by to_char(cu_lastdate,'yyyymmdd') desc");
		modelMap.put("customernum", cusnum);
		modelMap.put("cusdatas", cusdatas);
		return modelMap;

	}

	@Override
	public String updateMeeting(int ma_id) {
		String updateSQL = "update Meetingroomapply set ma_stage='已结束' where ma_id=" + ma_id + "";
		try {
			baseDao.execute(updateSQL);
			return "success";
		} catch (Exception ex) {
			return "fail";
		}

	}

	@Override
	public String updateVistPlan(int vp_id, String cu_nichestep, String cu_code, String nichecode, int vr_id) {
		String updateSQL = "update visitplan set vp_status='已拜访',vp_vrid=" + vr_id + " where vp_id=" + vp_id + "";

		String sql = "update customer set cu_lastdate=sysdate,cu_nichestep='" + cu_nichestep + "' where cu_code='" + cu_code + "'";

		String sql2 = "update BusinessChance set bc_currentprocess='" + cu_nichestep + "',bc_lastdate=sysdate where bc_code='" + nichecode
				+ "'";

		// String sql = "update customer set cu_lastdate=sysdate,cu_nichestep='"
		// + cu_nichestep + "' where cu_code='" + cu_code + "'";

		try {
			baseDao.execute(updateSQL);
			// baseDao.execute(sql);
			// if (nichecode != "" && !nichecode.equals("")) {
			// baseDao.execute(sql2);
			// }

			// 如果拜访报告商机阶段大于客户商机阶段，则更新客户商机阶段为拜访报告商机阶段
			Object stageDetno = baseDao.getFieldDataByCondition("(customer left join businesschancestage on cu_nichestep=bs_name)",
					"bs_detno", "cu_code='" + cu_code + "'");
			Object bsDetno = baseDao.getFieldDataByCondition("businesschancestage", "bs_detno", "bs_name='" + cu_nichestep + "'");
			if (stageDetno != null && bsDetno != null) {
				if (Integer.parseInt(stageDetno.toString()) < Integer.parseInt(bsDetno.toString())) {
					baseDao.execute(sql);
				} else {
					baseDao.execute("update customer set cu_lastdate=sysdate where cu_code='" + cu_code + "'");
				}
			}

			Object bcDetno = baseDao.getFieldDataByCondition("(businesschance left join businesschancestage on bc_currentprocess=bs_name)",
					"bs_detno", "bc_code='" + nichecode + "'");
			if (bsDetno != null && bcDetno != null) {
				if (Integer.parseInt(bsDetno.toString()) > Integer.parseInt(bcDetno.toString())) {
					baseDao.execute(sql2);
				} else {
					baseDao.execute("update businesschance set bc_lastdate=sysdate where bc_code='" + nichecode + "'");
				}
			}

			baseDao.execute("update mobile_outplandetail set mpd_status='已完成' where mpd_id="+vp_id+"");
			
			return "success";
		} catch (Exception ex) {
			return "fail";
		}
	}

	@Override
	public List<Map<String, Object>> getStaffMsg(String emcode) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + emcode + "'");
		if (bool) {
			List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(emcode);
			for (Employee e : employeeList) {
				if (!emcode.equals(e.getEm_code())) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("em_code", e.getEm_code());
					map.put("em_name", e.getEm_name());
					map.put("em_department", e.getEm_depart());
					map.put("em_organization", e.getEm_defaultorname());
					map.put("em_post", e.getEm_position());
					map.put("em_imid", e.getEm_imid());
					lists.add(map);
				}
			}
		}
		return lists;
	}

	@Override
	public Map<String, Object> getheadmanmsg(String emcode) {
		Map<String, Object> map = new HashMap<String, Object>();
		Object[] data = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "em_name", "em_depart", "Em_defaultorname",
				"Em_position" },
				"em_code=(select OR_HEADMANCODE from hrorg where or_id=(select em_defaultorid from employee where em_code='" + emcode
						+ "'))");
		if (data != null) {
			map.put("em_code", data[0]);
			map.put("em_name", data[1]);
			map.put("em_department", data[2]);
			map.put("em_organization", data[3]);
			map.put("em_post", data[4]);
		}
		return map;
	}

	@Override
	public boolean ifConfigs(String caller, String code) {
		if (caller == null && code == null)
			return baseDao.isDBSetting("usePreCustomer");
		else if (caller == null) {
			return baseDao.isDBSetting(code);
		} else {
			return baseDao.isDBSetting(caller, code);
		}
	}

	@Override
	public boolean ifOverRecv(String emcode) {
		// Object maxRecv = baseDao.getFieldDataByCondition("businessbasis",
		// "bb_maxrecvnum", "rownum=1");
		// Object emRecv =
		// baseDao.getFieldDataByCondition("(businesschance left join employee on bc_doman=em_name)",
		// "count(*)","em_code='"+emcode+"'" );
		// return
		// Integer.parseInt(maxRecv.toString())<Integer.parseInt(emRecv.toString())?false:true;

		Object maxnum = baseDao.getFieldDataByCondition("BusinessBasis", "bb_maxrecvnum", "1=1");
		if (maxnum != null) {
			Object count = baseDao.getCountByCondition("businesschance left join BUSINESSDATABASE on bc_nichehouse=bd_name",
					"bd_prop='可领取可分配' and bc_domancode='" + emcode + "' and bc_custname not in (select cu_name "
							+ "from customer where cu_auditstatuscode='AUDITED')");
			return Integer.parseInt(maxnum.toString()) < Integer.parseInt(count.toString()) ? false : true;
		}
		return false;
	}

	@Override
	public Map<String, Object> ifBusinessDataBaseAdmin(String emcode) {
		List<Object> obj = baseDao.getFieldDatasByCondition("BUSINESSDATABASE", "bd_name", "bd_admincode='" + emcode + "'");
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		for (Object ot : obj) {
			list.add(ot.toString());
		}
		map.put("nichehouse", list);
		if (!obj.isEmpty()) {
			map.put("isAdmin", "1");
		} else {
			map.put("isAdmin", "0");
		}
		return map;
	}

	@Override
	public String updateMatype(String ma_code) {
		String updateSQL = "update Meetingroomapply set ma_type='已填写' where ma_code='" + ma_code + "'";
		try {
			baseDao.execute(updateSQL);
			return "success";
		} catch (Exception ex) {
			return "fail";
		}
	}

	@Override
	public List<Map<String, Object>> openVersion() {
		String sql = "select nvl(en_status,' ') status,nvl(en_appversion,' ') appversion from enterprise";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public String updateSchedule(String code) {
		String updateSQL = "update projecttask set handstatus='已完成',handstatuscode='FINISHED' where sourcecode='" + code + "'";
		try {
			baseDao.execute(updateSQL);
			return "success";
		} catch (Exception ex) {
			return "fail";
		}
	}

	@Override
	public List<Map<String, Object>> getSchedule(String bccode, String emname) {
		String sql = "select * from (select name,resourcename,startdate,enddate,taskorschedule,tasklevel,description from projecttask where taskorschedule='Schedule' and sourcecode='"
				+ bccode + "' and RECORDER='" + emname + "') where rownum=1";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	// 获取商机编号
	@Override
	public List<Map<String, Object>> getNichecode(String cu_code, int start, int end,String custname) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<Object[]> codes = baseDao.getFieldsDatasByCondition("(select * from businesschance order by bc_recorddate desc)",
				new String[] { "bc_code", "bc_description" }, "bc_custcode='" + cu_code + "' or bc_custname='"+custname+"' and rownum>" + start + " and rownum<=" + end);
		if (codes != null) {
			for (Object[] obj : codes) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (obj != null) {
					if (obj[0] != null) {
						map.put("code", obj[0].toString());
					}
					if (obj[1] != null) {
						map.put("name", obj[1].toString());
					}
					lists.add(map);
				}
			}
		}
		return lists;
	}
	
	@Override
	public List<String> searchCustomer(String likestr, int start, int end){
		if(likestr==null){
			likestr = "";
		}
		likestr = likestr.replace("'", "''");
		List<String> customers = new ArrayList<String>();
		SqlRowList rs = baseDao.queryForRowSet("select cu_name from (select cu_name,rownum rn from (select cu_name from (select distinct cu_name from (select cu_name,cu_code from customer where nvl(cu_name,' ') like '%"+likestr+"%' union all select cu_name,cu_code from  precustomer where nvl(cu_name,' ') like '%"+likestr+"%' order by cu_code )) )) where rn>="+start+" and rn<="+end);
		while(rs.next()){
			customers.add(rs.getString("cu_name"));
		}
		return customers;
	}
}
