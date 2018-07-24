package com.uas.erp.service.common.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;

@Service("CommonHandler")
public class CommonHandler { 

	@Autowired
	private BaseDao baseDao;

	/**
	 * 公共interceptor，通过执行sql语句得到结果
	 * @param <E>
	 * @param keyvalue 单据id
	 * @param id  interceptor的id
	 */
	public void exec_handler(Integer keyvalue,Integer id){
		String result = baseDao.callProcedure("SP_COMMONHANDLER", keyvalue,id);
		if(StringUtil.hasText(result)){
			if(result.startsWith("AFTERSUCCESS:")){
				result = result.replace("AFTERSUCCESS:", "");
				BaseUtil.appendError(result); 
			} else {
				BaseUtil.showError(result);
			}
		}
	}
	
	/**
	 * 公共interceptor,通过执行SQL语句得到结果
	 * @param formStore		单据主表store
	 * @param id			interceptor的ID
	 */
	public void exec_handler(HashMap<Object, Object> formStore, Integer id){
		String store = JSONObject.toJSONString(formStore);
		String result = baseDao.callProcedure("SP_COMMONHANDLER_SAVE", store, "", id);
		if(StringUtil.hasText(result)){
			if(result.startsWith("AFTERSUCCESS:")){
				result = result.replace("AFTERSUCCESS:", "");
				BaseUtil.appendError(result);
			} else {
				BaseUtil.appendError(result);
			}
		}
	}
	
	/**
	 * 公共interceptor,通过执行SQL语句得到结果
	 * @param formStore		单据主表store
	 * @param gridStore		单据从表store
	 * @param id			interceptor的ID
	 */
	public void exec_handler(HashMap<Object, Object> formStore, ArrayList<Map<Object, Object>> gridStore, Integer id){
		String store = JSONObject.toJSONString(formStore);
		String gsStore = JSONArray.toJSONString(gridStore);
		String result = baseDao.callProcedure("SP_COMMONHANDLER_SAVE", store, gsStore, id);
		if(StringUtil.hasText(result)){
			if(result.startsWith("AFTERSUCCESS:")){
				result = result.replace("AFTERSUCCESS:", "");
				BaseUtil.appendError(result);
			} else {
				BaseUtil.appendError(result);
			}
		}
	}
	
	/**
	 * Caller: CardLog 初始化导入--考勤管理--考勤数据导入(导入之后逻辑)
	 */
	public void init_import_cardlog() {
		baseDao.execute("UPDATE CARDLOG SET cl_emid=(select em_id from employee where em_code=cl_emcode)");
		baseDao.execute("UPDATE CARDLOG SET (cl_emid,cl_emcode,cl_emname)=(select em_id,em_code,em_name from employee where em_cardcode=cl_cardcode)"
				+ " where cl_cardcode is not null and cl_emid is null and cl_emcode is null");
	}

	/**
	 * Caller: GoodsSend 初始化导入--财务数据--发出商品(导入之后逻辑)
	 * 
	 * public void init_import_goodssend () { baseDao.execute("UPDATE GoodsSend set gs_custid=(SELECT cu_id FROM Customer WHERE cu_code=gs_custcode)"); baseDao.execute("UPDATE GoodsSend set gs_sellerid=(SELECT em_id FROM Employee WHERE em_code=gs_sellercode)"); }
	 */

	/**
	 * Caller: ARBill 初始化导入--财务数据--应收开票(导入之后逻辑)
	 * 
	 * public void init_import_arbill () { baseDao.execute("UPDATE ArBill set ab_custid=(SELECT cu_id FROM Customer WHERE cu_code=ab_custcode)"); baseDao.execute("UPDATE ArBill set ab_sellerid=(SELECT em_id FROM Employee WHERE em_name=ab_seller)"); baseDao.execute("UPDATE ArBill set ab_rate=(SELECT cr_rate FROM Currencys WHERE cr_name=ab_currency)"); baseDao.execute("UPDATE ArBill set ab_payments=(SELECT cu_payments FROM Customer WHERE cu_code=ab_custcode)");
	 * baseDao.execute("UPDATE ArBill set ab_paymentcode=(SELECT pa_code FROM Payments WHERE pa_name=ab_payments and pa_class='收款方式')"); baseDao.execute("UPDATE ArBill set ab_recorder='admin',ab_cop='" + baseDao.getDBSetting("ENID") + "',ab_paydate=ab_date,ab_aramount=round(ab_aramount,2) WHERE ab_class='初始化'"); }
	 */

	/**
	 * Caller: PreRec 初始化导入--财务数据--预收(导入之后逻辑)
	 */
	public void init_import_prerec() {
		baseDao.execute("UPDATE PreRec set pr_recorder='admin',pr_cop='" + baseDao.getDBSetting("ENID")
				+ "',pr_cmamount=pr_amount,pr_cmcurrency=pr_currency,pr_amount=round(pr_amount,2) WHERE pr_kind='初始化'");
		baseDao.execute("UPDATE PreRec set pr_jsamount=pr_amount WHERE pr_kind='初始化'");
		baseDao.execute("UPDATE PreRec set pr_rate=(SELECT cr_rate FROM Currencys WHERE cr_name=pr_currency) WHERE pr_kind='初始化'");
	}

	/**
	 * Caller: Estimate 初始化导入--财务数据--暂估(导入之后逻辑)
	 * 
	 * public void init_import_estimate () { baseDao.execute("UPDATE Estimate set es_vendid=(SELECT ve_id FROM Vendor WHERE ve_code=es_vendcode)"); baseDao.execute("UPDATE Estimate set es_buyerid=(SELECT em_id FROM Employee WHERE em_code=es_buyercode)"); }
	 */

	/**
	 * Caller: ApBill 初始化导入--财务数据--应付开票(导入之后逻辑)
	 * 
	 * public void init_import_apbill () { baseDao.execute("UPDATE ApBill set ab_vendid=(SELECT ve_id FROM Vendor WHERE ve_code=ab_vendcode)"); baseDao.execute("UPDATE ApBill set ab_buyerid=(SELECT em_id FROM Employee WHERE em_name=ab_buyer)"); baseDao.execute("UPDATE ApBill set ab_rate=(SELECT cr_rate FROM Currencys WHERE cr_name=ab_currency)"); baseDao.execute("UPDATE ApBill set ab_payments=(SELECT ve_payment FROM Vendor WHERE ve_code=ab_vendcode)");
	 * baseDao.execute("UPDATE ApBill set ab_paymentcode=(SELECT pa_code FROM Payments WHERE pa_name=ab_payments and pa_class='付款方式')"); baseDao.execute("UPDATE ApBill set ab_recorder='admin',ab_cop='" + baseDao.getDBSetting("ENID") + "',ab_paydate=ab_date,ab_apamount=round(ab_apamount,2) WHERE ab_class='初始化'"); }
	 */

	/**
	 * Caller: PrePay 初始化导入--财务数据--预付(导入之后逻辑)
	 */
	public void init_import_prepay() {
		baseDao.execute("UPDATE PrePay set pp_vendid=(SELECT ve_id FROM Vendor WHERE ve_code=pp_vendcode)");
		baseDao.execute("UPDATE PrePay set pp_jsamount=pp_amount WHERE pp_type='初始化'");
		baseDao.execute("UPDATE PrePay set pp_recorder='admin',pp_cop='" + baseDao.getDBSetting("ENID")
				+ "',pp_vmamount=pp_amount,pp_vmcurrency=pp_currency,pp_amount=round(pp_amount,2) WHERE pp_type='初始化'");
	}

	/**
	 * Caller: Vendor 供应商资料导入之后
	 */
	public void init_import_vendor() {
		baseDao.execute("Update Vendor set ve_recordname='admin',ve_auditstatus='已审核',ve_auditstatuscode='AUDITED'");
		baseDao.updateByCondition("Vendor", "ve_apvendcode=ve_code,ve_apvendname=ve_name", "nvl(ve_apvendcode,' ')=' '");
		baseDao.updateByCondition("Vendor",
				"(ve_paymentid,ve_paymentcode)=(select pa_id,pa_code from Payments where pa_name=ve_payment and pa_class='付款方式')",
				"nvl(ve_payment,' ')<>' '");
		baseDao.updateByCondition(
				"Vendor",
				"(ve_buyerid,ve_buyeruu,ve_buyercode)=(select max(em_id),max(em_uu),max(em_code) from Employee where em_name=ve_buyername)",
				"nvl(ve_buyername,' ')<>' '");
		//插入到供应商银行资料表
		baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ");
		baseDao.execute("insert into VePaymentsDetail(vpd_id,vpd_veid,vpd_bank,vpd_bankaccount,vpd_contact,vpd_currency,vpd_taxrate,vpd_bankman,vpd_bankaddress,vpd_vecode,vpd_remark,vpd_detno) select VePaymentsDetail_seq.nextval,ve_id,ve_bank,ve_bankaccount,ve_contact,ve_currency,ve_taxrate,ve_bankman,ve_bankaddress,ve_code,'是',1 from Vendor where (ve_id,ve_code,ve_bank,ve_bankaccount) not in (select vpd_veid,vpd_vecode,vpd_bank,vpd_bankaccount from VePaymentsDetail) and (ve_code is not null and ve_bank is not null and ve_bankaddress is not null and ve_bankaccount is not null)");
		baseDao.execute("insert into VENDORCONTACT(vc_id,vc_vecode,vc_vename,vc_name,vc_mobile,vc_officephone,vc_officeemail,vc_isvendor,vc_job,vc_veid) select VENDORCONTACT_seq.nextval,ve_code,ve_name,ve_contact,ve_mobile,ve_tel,ve_email,-1,ve_degree,ve_id from vendor where (ve_contact,ve_code) not in(select vc_name,vc_vecode from VENDORCONTACT) and (ve_contact is not null and ve_code is not null)");
	}

	/**
	 * Caller: Customer 客户资料导入之后
	 */
	public void init_import_customer() {
		//baseDao.execute("Update Customer set cu_recordman='admin',cu_auditstatus='已审核',cu_auditstatuscode='AUDITED'");
		/**
		 * 插入到收货地址1
		 * */
		baseDao.getSeqId("CUSTOMERADDRESS_SEQ");
		//插入客户收货地址时电话不为空时取电话，电话为空时取手机号
		baseDao.execute("insert into CustomerAddress(ca_id,ca_cuid,ca_address,ca_remark,ca_detno,CA_PERSON,CA_PHONE,CA_FAX) select CustomerAddress_seq.nextval,cu_id,cu_add1,'是',1,cu_contact,nvl(cu_tel,cu_mobile),cu_fax from customer where (cu_id,cu_add1) not in (select ca_cuid,ca_address from Customeraddress) and cu_add1 is not null");
		//收款方式
		baseDao.getSeqId("CUSTOMERPAYMENTS_SEQ");
		baseDao.execute("insert into CustomerPayments(cp_id,cp_cuid,cp_paymentcode,cp_payment,cp_detno,cp_isdefault,cp_cucode) select  CustomerPayments_seq.nextval,cu_id,cu_paymentscode,cu_payments,1,'是',cu_code from customer where (cu_id,cu_paymentscode) not in (select cp_cuid,cp_paymentcode from CustomerPayments) and cu_paymentscode is not null");
		//插入到客户分配表
		baseDao.getSeqId("CUSTOMERDISTR_SEQ");
		baseDao.execute("insert into CustomerDistr(cd_id,cd_cuid,cd_seller,cd_sellercode,cd_detno,cd_custcode,cd_remark) select CustomerDistr_seq.nextval,cu_id,cu_sellername,cu_sellercode,1,cu_code,'是' from customer where (cu_id,cu_sellercode) not in (select cd_cuid,cd_sellercode from CustomerDistr) and cu_sellercode is not null");
		//客服代表也插入客户分配表
		baseDao.execute("insert into CustomerDistr(cd_id,cd_cuid,cd_seller,cd_sellercode,cd_detno,cd_custcode,cd_remark) select CustomerDistr_seq.nextval,cu_id,cu_servicename,cu_servicecode,2,cu_code,'否' from customer where (cu_id,cu_servicecode) not in (select cd_cuid,cd_sellercode from CustomerDistr) and cu_servicecode is not null");
		// 插入客户信用额度
		baseDao.getSeqId("CustomerCredit_seq");
		baseDao.execute("insert into CustomerCredit (cuc_id,cuc_custcode,cuc_custname,cuc_credit,cuc_recorder,cuc_indate,cuc_status,cuc_statuscode) select CustomerCredit_seq.nextval,cu_code,cu_name,Cu_Credit,'admin',sysdate,'已审核','AUDITED' from customer where cu_code not in (select  Cuc_Custcode from CustomerCredit) and nvl(Cu_Credit,0)<>0");	
		baseDao.updateByCondition("Customer", "cu_arcode=cu_code,cu_arname=cu_name", "nvl(cu_arcode,' ')=' '");
		baseDao.updateByCondition("Customer", "cu_shcustcode=cu_code,cu_shcustname=cu_name", "nvl(cu_shcustcode,' ')=' '");
		baseDao.updateByCondition("Customer",
				"(cu_paymentid,cu_paymentscode)=(select pa_id,pa_code from Payments where pa_name=cu_payments and pa_class='收款方式')",
				"nvl(cu_payments,' ')<>' '");
		baseDao.updateByCondition("Customer",
				"(cu_sellerid,cu_sellercode)=(select em_id,em_code from Employee where em_name=cu_sellername)",
				"nvl(cu_sellername,' ')<>' '");
		//插入客户资料
		baseDao.getSeqId("Contact_seq");
		baseDao.execute("Insert into contact(ct_id,ct_cuid,ct_detno,ct_remark,ct_name,ct_mobile,ct_officephone,ct_position,ct_personemail) select Contact_seq.nextval,cu_id,'1','是',cu_contact,cu_mobile,cu_tel,cu_degree,cu_email from customer where (cu_id,cu_contact) not in (select ct_cuid,ct_name from contact)");
	}

	/**
	 * Caller: Employee 员工资料导入后
	 */
	public void init_import_employee() {
		baseDao.updateByCondition("Employee", "em_enid=nvl((select min(en_id) from enterprise),0)", "1=1");
		baseDao.updateByCondition("Employee",
				"(em_defaultorid,em_defaultorname)=(select min(or_id),min(or_name) from HrOrg where or_code=em_defaultorcode)",
				"em_defaultorcode is not null");
		baseDao.updateByCondition("Employee",
				"(em_defaulthsid,em_defaulthsname)=(select min(jo_id),min(jo_name) from Job where jo_code=em_defaulthscode)",
				"em_defaulthscode is not null");
	}

	/**
	 * Caller: Department 部门资料导入后
	 */
	public void init_import_department() {
		baseDao.execute("update Department A set (dp_subof,dp_parentdpname)=(select dp_id,dp_name from Department B where A.dp_pcode=B.dp_code) where dp_pcode is not null");
		baseDao.execute("update Department set dp_subof=0 where dp_pcode=dp_code or dp_pcode is null or dp_subof is null");
		baseDao.execute("update Department a set a.dp_level=nvl((select b.olevel from (select dp_id,dp_subof,level olevel from Department start with dp_subof=0 connect by prior dp_id=dp_subof)b where b.dp_id=a.dp_id),1)");
		baseDao.execute("update Department set dp_isleaf=0");
		baseDao.execute("update Department set dp_isleaf=-1 where dp_id not in (select distinct dp_subof from Department)");
	}

	/**
	 * Caller: HrOrg 组织资料导入后
	 */
	public void init_import_hrorg() {
		baseDao.execute("update hrorg A set or_subof=nvl((select or_id from hrorg B where A.or_pcode=B.or_code),0)");
		baseDao.execute("update hrorg set or_subof=0 where or_pcode=or_code or or_subof is null");
		baseDao.execute("update hrorg a set a.or_level=nvl((select b.olevel from (select or_id,or_subof,level olevel from hrorg start with or_subof=0 connect by prior or_id=or_subof)b where b.or_id=a.or_id),1)");
		baseDao.execute("update hrorg set or_isleaf=0");
		baseDao.execute("update hrorg set or_isleaf=-1 where or_id not in (select distinct or_subof from hrorg)");
	}

	/**
	 * Caller: Job 岗位资料导入后
	 */
	public void init_import_job() {
		baseDao.execute("update Job A set (jo_subof,jo_parentname)=(select jo_id,jo_name from Job B where A.jo_pcode=B.jo_code) where jo_pcode is not null");
		baseDao.execute("update Job set jo_subof=0 where jo_pcode=jo_code or jo_pcode is null or jo_subof is null");
		baseDao.execute("update job a set a.jo_level=nvl((select b.olevel from (select jo_id,jo_subof,level olevel from job start with jo_subof=0 connect by prior jo_id=jo_subof)b where b.jo_id=a.jo_id),1)");		
		baseDao.updateByCondition("Job", "jo_orgid=nvl((select or_id from HrOrg where or_code=jo_orgcode),0)", "1=1");
		baseDao.updateByCondition("Job", "jo_status='已审核',jo_statuscode='AUDITED'", "1=1");
		// baseDao.updateByCondition("Job", "jo_headid=nvl((select hs_id from HrHeadShip where hs_name=jo_headname),0)", "1=1");
	}

	/**
	 * Caller: Product 物料资料导入后
	 */
	public void init_import_product() {
		baseDao.updateByCondition("Product", "pr_wccode=(select wc_code from workcenter where wc_name=pr_wcname)",
				"nvl(pr_wccode,' ')=' ' and nvl(pr_wcname,' ')<>' '");
		baseDao.updateByCondition("Product", "pr_planner=(select em_name from employee where em_code=pr_planercode)",
				"nvl(pr_planner,' ')=' ' and nvl(pr_planercode,' ')<>' '");
		baseDao.updateByCondition("Product", "pr_whmanname=(select em_name from employee where em_code=pr_whmancode)",
				"nvl(pr_whmanname,' ')=' ' and nvl(pr_whmancode,' ')<>' '");
		baseDao.updateByCondition("Product", "pr_buyername=(select em_name from employee where em_code=pr_buyercode)",
				"nvl(pr_buyername,' ')=' ' and nvl(pr_buyercode,' ')<>' '");
	}

	/**
	 * Caller: Category 科目资料导入后
	 */
	public void init_import_category() {
		baseDao.execute("Update Category A Set ca_subof=nvl((select ca_id from category B where B.ca_code=A.ca_pcode),0)");
	    baseDao.execute("update Category a set a.ca_level=(select b.olevel from(select ca_id,ca_subof,level olevel from Category start with ca_subof=0 connect by prior ca_id=ca_subof) b where a.ca_id=b.ca_id)");
		baseDao.execute("Update Category Set ca_type=case when ca_typename='借' then 0 when ca_typename='贷'then 1 when ca_typename='借或贷' then 2 end where ca_typename is not null");
		baseDao.execute("Update Category Set ca_asstype=(select ak_code from asskind where ak_name=ca_assname) where ca_assname in (select ak_name from asskind)");
		baseDao.execute("declare v_ass str_table_type; v_ca_asstype category.ca_asstype%type; v_code varchar2(30); v_name varchar2(30); begin for rs in (select parsestring(ca_assname, '#') strs,ca_id from category where instr(ca_assname,'#')>1) loop v_ass := rs.strs; v_ca_asstype := null; for i in v_ass.first()..v_ass.last() loop v_name := v_ass(i); begin select ak_code into v_code from asskind where ak_name=v_name; exception when others then v_code := null; end; if nvl(v_code,' ')<>' ' then if v_ca_asstype is null then v_ca_asstype := v_code; else v_ca_asstype := v_ca_asstype || '#' || v_code; end if; end if; end loop; if v_ca_asstype is not null then update category set ca_asstype=v_ca_asstype where ca_id=rs.ca_id; end if; end loop; end;");
		baseDao.execute("update category A set ca_description=ca_name where ca_level=1");	
		for (int i = 2; i < 8; i++) {
		baseDao.execute("update category A set ca_description=(select ca_description from category B where B.ca_id=A.ca_subof)||'：'||ca_name where ca_level="+i);
		}		
		baseDao.execute("update category set ca_isleaf= case when ca_id not in (select distinct ca_subof from category) then 1 else 0 end");
	}

	/**
	 * CALLLER : JPROCESS
	 * */
	public void before_exportJprocess(String condition) {
		condition = condition.replaceAll("%3D", "=");
		baseDao.updateByCondition("Jprocess", "jp_stayMinutes = round(to_number(sysdate-jp_launchtime)*1440)", condition);
	}
	//上料采集导入后
	public void init_import_CraftMaterialPicking() {						
		baseDao.execute("declare v_count number ;begin for rs in (select cm_sncode from CRAFTMATERIAL where nvl(cm_maid,0)=0 group by cm_sncode) loop v_count := 0; select count(1)  into v_count from MAKECRAFTDETAIL left join makecraft on mc_id=mcd_mcid  where mc_code in (select cm_mccode from CRAFTMATERIAL where cm_sncode=rs.cm_sncode) and mcd_stepcode not in (select cm_stepcode from CRAFTMATERIAL where cm_sncode=rs.cm_sncode);update makeserial set ms_status=(select case when v_count=0 then 2 else 1 end from dual) where ms_sncode=rs.cm_sncode;end loop;end;");
		baseDao.execute("update CRAFTMATERIAL set cm_maid=(select ma_id from make where ma_code=cm_makecode) where nvl(cm_maid,0)=0");
	}
	
	public void afterQuery(String caller,String condition){
		String returnStr = baseDao.callProcedure("SP_AFTERQUERY", caller,condition);
		if(returnStr!=null){
			BaseUtil.showError(returnStr);
		}
	}
	
	/**
	 * RDM流程统计
	 */
	public void FlowStatistics(String condition) {
		 baseDao.execute("BEGIN " + 
		 		"    FLOWSTATISTICS(); " + 
		 		"END;");
	}
}
