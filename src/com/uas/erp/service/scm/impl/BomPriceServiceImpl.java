package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.uas.b2c.model.Bom;
import com.uas.b2c.model.BomDetail;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.BomPriceService;
import com.uas.erp.core.HttpUtil;
@Service("BomPriceService")
public class BomPriceServiceImpl implements BomPriceService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	/*private static String GETBOMDETAIL = "select pr_detail,pr_spec,pr_unit,pr_orispeccode,bs_soncode,bs_actqty,nvl(bs_purcpricermb,0) bs_purcpricermb,nvl(bs_osprice,0) bs_osprice,pr_id,pr_manutype,bs_level,bs_ifrep,bs_standardprice,nvl(bs_rate,0) bs_rate,bs_m,bs_l,bs_vendid,bs_vendname,ve_uu "
			+ "from bomstruct,product,vendor where pr_code=bs_soncode and bs_vendid = ve_id and bs_topbomid=? and bs_topmothercode=? order by bs_idcode ";
	*/
	private static String GETBOMDETAIL = "select pr_detail, pr_spec, pr_unit, pr_orispeccode, bs_soncode, bs_actqty, bs_purcpricermb, bs_osprice, pr_id, pr_manutype, bs_level, bs_ifrep, bs_standardprice, bs_rate, bs_m, bs_l ,bs_vendname,ve_id,ve_uu "
			+ "from ( select pr_detail,pr_spec,pr_unit,pr_orispeccode,bs_soncode,bs_actqty,nvl(bs_purcpricermb,0) bs_purcpricermb,nvl(bs_osprice,0) bs_osprice,pr_id, pr_manutype,bs_level,bs_ifrep,bs_standardprice,nvl(bs_rate,0) bs_rate,bs_m,bs_l,bs_vendname from bomstruct,product where pr_code=bs_soncode and bs_topbomid=? and bs_topmothercode=? order by bs_idcode ) left join vendor on BS_VENDNAME = ve_name ";
	
	private static String INSERTBOMPRICEDETAIL ="insert into BOMPRICEDETAIL "
			+ "(BPD_ID,BPD_BPID,BPD_DETNO,BPD_LEVEL,BPD_IFREP,BPD_PRODID,BPD_PRODCODE,BPD_PRODNAME,BPD_PRODSPEC,BPD_PRODORISPEC,BPD_PRODBRAND,BPD_PRODUNIT,BPD_BASEQTY,BPD_PRICE,BPD_AMOUNT,BPD_VENDID, BPD_VENDNAME, BPD_VENDUU)"
			+ " values (BOMPRICEDETAIL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String INSERTINQUIRY ="insert into Inquiry (IN_ID, IN_CODE,IN_DATE, IN_DELIVERY,IN_RECORDDATE,IN_APPLDATE,IN_ENDDATE,IN_STATUS,IN_STATUSCODE,IN_RECORDERID, IN_RECORDER,IN_KIND,IN_PRICETYPE,IN_VENDID,IN_BATCHVENDOR) "
			+ "select ?, ?,sysdate,sysdate,sysdate,sysdate,sysdate+90,'在录入','ENTERING',?, ?,'采购','标准',ve_id,ve_code from vendor where nvl(ve_uu,'0') = '10042875'";
	private String INSERTINQUIRYDETAIL="insert into Inquirydetail (ID_ID, ID_CODE, ID_INID, ID_DETNO, ID_PRODID, ID_PRODCODE,ID_DELIVERY, ID_PPDATE, ID_MYFROMDATE, ID_MYTODATE, ID_QUDETNO, ID_VENDCODE, ID_VENDNAME, ID_VENDUU, ID_VENDYYZZH,ID_PPDID,ID_CURRENCY,ID_RATE,ID_PURCTAXRATE,ID_PURCCURRENCY,ID_PURCVENDCODE,ID_PURCVENDNAME ) "
			+ "select Inquirydetail_seq.nextval,";
	// vendor
	@Override
	public void saveBomPrice(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BomPrice", "bp_code='" + store.get("bp_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//保存BOMPRICE
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BomPrice", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "bp_id", store.get("bp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}

	@Override
	public void deleteBomPrice(String caller,int id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//删除Make
		baseDao.deleteById("BomPrice", "bp_id", id);
		//删除MakeMaterial
		baseDao.deleteById("BomPriceDetail", "bpd_bpid", id);
		//记录操作
		baseDao.logger.delete(caller, "bp_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public void updateBomPrice(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + store.get("bp_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改Make
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BomPrice", "bp_id");
		baseDao.execute(formSql);
		//修改BomPriceDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BomPriceDetail", "bpd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bpd_id") == null || s.get("bpd_id").equals("") || s.get("bpd_id").equals("0") ||
					Integer.parseInt(s.get("bpd_id").toString()) == 0){//新添加的数据，id不存在
				
				int id = baseDao.getSeqId("BomPriceDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BomPriceDetail", new String[]{"bpd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bp_id", store.get("bp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public void submitBomPrice(String caller,int bp_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + bp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bp_id);
		//执行提交操作
		baseDao.submit("BomPrice", "bp_id=" + bp_id, "bp_status", "bp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bp_id", bp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bp_id);
	}
	
	@Override
	public void resSubmitBomPrice(String caller,int bp_id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + bp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bp_id);
		//执行反提交操作
		baseDao.resOperate("BomPrice", "bp_id=" + bp_id, "bp_status", "bp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bp_id", bp_id);
		handlerService.afterResSubmit(caller, bp_id);
	}

	@Override
	public void auditBomPrice(String caller,int bp_id) {

		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + bp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bp_id);
		//执行审核操作
		baseDao.audit("BomPrice", "bp_id=" + bp_id, "bp_status", "bp_statuscode", "bp_auditdate", "bp_auditman");
		//记录操作
		baseDao.logger.audit(caller, "bp_id", bp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, bp_id);
	
	}
	
	@Override
	public void resAuditBomPrice(String caller,int bp_id) {

		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + bp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BomPrice", "bp_id=" + bp_id, "bp_status", "bp_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bp_id", bp_id);
			
	}

	@Override
	public void evlBomCostPrice(String caller,int bp_id) {
		// 只能对状态为[在录入]的订单进行成本计算操作!
		Object status = baseDao.getFieldDataByCondition("BomPrice", "bp_statuscode", "bp_id=" + bp_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能对[在录入]的单据进行成本计算操作!");
		}
		baseDao.deleteById("BomPriceDetail", "bpd_bpid", bp_id);
		Object[] BomPrice = baseDao.getFieldsDataByCondition("BomPrice", "BP_MONPRODCODE,BP_BOMID", "bp_id = "+bp_id);
		Object prspecvalue = null;
		Object prrefno = null;
		Object bp_monprodcode = BomPrice[0];
		Object bp_bomid = BomPrice[1];
		if (!BomPrice[0].equals("") && BomPrice[1].equals("0")) {
			prspecvalue = baseDao.getFieldDataByCondition("product", "pr_specvalue", "pr_code='" + bp_monprodcode + "'");
			try {
				if (!prspecvalue.equals("")) {
					if (prspecvalue.equals("SPECIFIC")) {
						prrefno = baseDao.getFieldDataByCondition("product", "pr_refno", "pr_code='" + bp_monprodcode + "'");
						bp_bomid = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + prrefno + "'")
								.toString());
					} else {
						bp_bomid = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + bp_monprodcode + "'")
								.toString());
					}
				}
			} catch (Exception ex) {
				BaseUtil.showError(ex.getMessage());
			}
		}
	try {
		baseDao.procedure("SP_COSTCOUNT", new Object[] { bp_bomid, bp_monprodcode, "最新采购单价" });
		Double evrate = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select nvl(cr_rate,1) from BomPrice left join Currencys on bp_currency=cr_name where bp_id=? and nvl(cr_status,' ')<>'已禁用'",
						Double.class, bp_id);
		// 估价单计算采集
		baseDao.execute("merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_rate  END) "
				+ "where bs_topbomid="+ bp_bomid
				+ " and bs_topmothercode='"+ bp_monprodcode
				+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ");
		baseDao.execute("update BomStruct set bs_m=bs_l*bs_actqty where bs_topbomid=" + bp_bomid
				+ " and bs_topmothercode='" + bp_monprodcode
				+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))");
		baseDao.execute("update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
				+ bp_bomid + " and bs_topmothercode='" + bp_monprodcode
				+ "' and (nvl(bs_sonbomid,0)>0 or bs_soncode='" + bp_monprodcode
				+ "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ");

		String SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid="
				+ bp_bomid
				+ " and bs_topmothercode='"+ bp_monprodcode
				+ "' and nvl(bs_sonbomid,0)>0 "
				+ "and nvl(bs_sonbomid,0) not in "
				+ "(select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {// bs_osprice 在存储过程中计算出来的值是含税的委外单价
			SQLStr = "SELECT sum(nvl(bs_m,0)) from BomStruct WHERE "
					+ "bs_topbomid=" + bp_bomid + " and bs_topmothercode='" + bp_monprodcode
					+ "' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
			SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
			if (rsthis.next()) {
				SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1)
						+ "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty " + " where bs_topbomid=" + bp_bomid
						+ " and bs_idcode=" + rs.getString("bs_idcode");
				baseDao.execute(SQLStr);
			}
		}
		//BOM明细
		rs = baseDao.queryForRowSet(GETBOMDETAIL, bp_bomid, bp_monprodcode);
		int count = 1;
		while (rs.next()) {// 委外单价（除税）+下阶成本 
			baseDao.getJdbcTemplate().update(
					INSERTBOMPRICEDETAIL,
					new Object[] { bp_id, count++, rs.getString("bs_level"),rs.getGeneralInt("bs_ifrep"),
							rs.getInt("pr_id"),rs.getString("bs_soncode"),rs.getString("pr_detail"),
							rs.getString("pr_spec"),rs.getString("pr_orispeccode"),rs.getString("pr_brand"),
							rs.getString("pr_unit"),rs.getGeneralDouble("bs_actqty"),
							NumberUtil.formatDouble(rs.getGeneralDouble("bs_l") / evrate, 6),NumberUtil.formatDouble(rs.getGeneralDouble("bs_m") / evrate, 6),
							rs.getString("ve_id"),rs.getString("bs_vendname"),rs.getString("ve_uu")});
		}
		//判断取价原则取出价格是否含税，不含税则计算含税价
		Object type = baseDao.getFieldDataByCondition("configs", "substr(nvl(replace(nvl(data,',N'),',N',''),'A'),0,1)", "caller='sys' and code='bomCostPrinciple'");
		if("J,K,G,H".indexOf(String.valueOf(type))<0){//不含税
			Double evtaxrate = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(cr_taxrate,16) from BomPrice left join Currencys on bp_currency=cr_name where bp_id=? and nvl(cr_status,' ')<>'已禁用'",
							Double.class, bp_id);
			baseDao.execute("update BOMPRICEDETAIL set BPD_PRICE = BPD_PRICE*(1+"+evtaxrate+"*0.01) where bpd_bpid = "+bp_id);
		}
		//更新主表估价金额 BP_PRICE
		baseDao.execute("update bomprice set bp_price = (select round(sum(BPD_PRICE*BPD_BASEQTY),2) from bompricedetail where bpd_bpid = bp_id) where bp_id = "+bp_id);
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public void b2cBomPrice(String caller, int id) {
		Master master =  SystemSession.getUser().getCurrentMaster();
		String b2bUrl = "https://api-pricing.usoftmall.com";
		Object enUU = master.getMa_uu();
		if(!StringUtil.hasText(enUU)){
			BaseUtil.showError("您的企业还未注册优软云，请联系管理员注册企业优软云！");
		}
		String getBomPrice ="select BP_ID \"mid\",BP_MONPRODCODE \"motherCode\",BP_BOMID \"bomId\",BP_MONPRODNAME \"title\",BP_BOUNIT \"unit\",BP_MONPRODSEPC \"spec\",BP_PRICE \"uasEstimatePrice\","
				+ " BP_CURRENCY \"currency\",BP_B2CPRICE \"refPrice\",BP_BOFLOWSTYLE \"bomClassify\",BP_BOLEVEL \"bomLevel\",nvl((select em_uu from employee where em_code = bp_recordercode ),0) \"inputUU\", "
				+ "nvl((select em_uu from employee where em_code = bp_auditman ),0) \"checkUU\", BP_STATUS bomStatus,nvl((select en_uu from enterprise),0) \"enuu\", "
				+ "to_char(BP_DATE,'yyyy-mm-dd') \"inputDate\" "
				+ "from bomprice where bp_id = "+id;
		String getBomPriceDetail ="select  BPD_PRODCODE productCode , BPD_PRODNAME productTitle  ,BPD_PRODBRAND brand , BPD_PRODORISPEC cmpCode , BPD_PRODBRAND inbrand , BPD_PRODSPEC spec ,"
				+ "BPD_BASEQTY unitConsumption , BPD_PRICE uasEstimatePrice ,BPD_PRODUNIT unit , BP_CURRENCY currency , "
				+ "BP_CURRATE taxRate ,BPD_PRICE taxUnitPrice,BPD_VENDNAME vendName, BPD_VENDUU vendUU "
				+ "from bompricedetail inner join bomprice on bpd_bpid = bp_id inner join product on pr_code = BPD_PRODCODE where pr_manutype='PURCHASE' and pr_supplytype<>'VIRTUAL' and BPD_LEVEL <> '0' and bpd_bpid = "+id;
		Map<String, Object> bom = baseDao.getJdbcTemplate().queryForMap(getBomPrice);
		List<BomDetail> BomDetailList = baseDao.getJdbcTemplate().query(getBomPriceDetail,new BeanPropertyRowMapper<BomDetail>(BomDetail.class));
		Set<BomDetail> bomDetails = new HashSet<BomDetail>(BomDetailList);
		bom.put("bomDetails", bomDetails);
		if(!master.getEnv().equals("prod")){
			b2bUrl ="http://192.168.253.12:23969";
		}
		b2bUrl += "/pricing/bomPricing/";
		try {
			com.uas.erp.core.HttpUtil.Response response = HttpUtil.doPost(b2bUrl, JSON.toJSONString(bom),false,null);
			if(response.getStatusCode() == HttpStatus.OK.value()){
				String resData = response.getResponseText();
				if(StringUtil.hasText(resData)){
					Map<Object, Object>  resDateMap = BaseUtil.parseFormStoreToMap(resData);
					if((boolean) resDateMap.get("success")){
						String BomData = JSONArray.toJSONString(resDateMap.get("data"));
						if(StringUtil.hasText(BomData)&&!BomData.equals("null")){
							Bom b2cBom = com.alibaba.fastjson.JSONObject.parseObject(BomData, Bom.class);
							long mid = b2cBom.getMid();
							List<String> sqls = new ArrayList<String>();
							Set<BomDetail> b2cBomDetail = b2cBom.getBomDetails();
							for (Iterator iterator = b2cBomDetail.iterator(); iterator.hasNext();) {
								BomDetail bomDetail = (BomDetail) iterator.next();
								sqls.add(" update BOMPRICEDETAIL set BPD_B2CPRICE = "+bomDetail.getPricingPrice()+" where BPD_PRODCODE ='"+bomDetail.getProductCode()+"' and BPD_BPID="+mid);
							}
							baseDao.execute(sqls);
							if(!baseDao.checkIf("BOMPRICEDETAIL", "BPD_BPID ="+mid+" and nvl(BPD_B2CPRICE,0)=0")){
								baseDao.execute("update bomprice set BP_B2CPRICE = (select sum(BPD_BASEQTY*BPD_B2CPRICE) from BOMPRICEDETAIL where BPD_BPID = bp_id and BPD_LEVEL <> '0') where bp_id = "+mid);
							}
							baseDao.execute("update bompricedetail set BPD_DIFFPRICE = (BPD_B2CPRICE - BPD_PRICE) where BPD_B2CPRICE > 0 and bpd_bpid = "+mid);
						}
					}else{
						BaseUtil.showError(String.valueOf(resDateMap.get("message")));
					}
				}
			}else{
				if(StringUtil.hasText(response.getResponseText())){
					BaseUtil.showError(response.getResponseText());
				}else{
					BaseUtil.showError("系统错误，错误码："+response.getStatusCode());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public String turnB2cInquiry(String caller, int id, String gridId) {
		Employee employee  = SystemSession.getUser();
		Master master =  employee.getCurrentMaster();
		Object enUU = master.getMa_uu();
		Object emUU = employee.getEm_uu();
		if(!StringUtil.hasText(emUU)){
			BaseUtil.showError("您还不是优软云的个人用户，请联系管理员开通！");
		}
		if(!StringUtil.hasText(enUU)){
			BaseUtil.showError("您的企业还未注册优软云，请联系管理员注册企业优软云！");
		}
		Object emId = employee.getEm_id();
		Object emName = employee.getEm_name();
		//插入供应商明细表 税率 联系人 uu号是否需要可配置
		if(!baseDao.checkIf("vendor", "nvl(ve_uu,'0') = '10042875'")){
			String sql ="Insert into vendor (VE_ID,VE_CODE,VE_NAME,VE_SHORTNAME,VE_APVENDCODE,VE_APVENDNAME,VE_CONTACT,VE_TEL,VE_EMAIL,VE_WEBSERVER,VE_ADD1,VE_BANK,VE_BANKACCOUNT,VE_CURRENCY,VE_TAXRATE,VE_INITDATE,VE_TRANSDATE,VE_RECORDID,VE_RECORDNAME,VE_AUDITSTATUS,VE_EMAILKF,VE_AUDITSTATUSCODE,VE_UU,VE_RATE,VE_AUDITDATE,VE_ONECURR,VE_B2BCHECK,VE_RELATEDPARTY,VE_IFDELIVERYONB2B,VE_B2BENABLE) "
					+ " values (?,?,'深圳市优软商城科技有限公司','优软商城','VUAS','深圳市优软商城科技有限公司','刘俊娟','0755-26994800','yrsc@usoftchina.com','91440300MA5DC1WL1W','深圳市前海深港合作区前湾一路1号A栋201室','中国工商银行深圳侨香支行','400005130900150003','RMB',17,sysdate,sysdate,0,null,'已审核','已获取','AUDITED','10042875',0,sysdate,0,-1,-1,-1,-1)";
			int ve_id = baseDao.getSeqId("VENDOR_SEQ");
			String ve_code = baseDao.sGetMaxNumber("Vendor", 2);
			baseDao.execute(sql, ve_id,ve_code);
		}
		//插入询价单主表
		int in_id = baseDao.getSeqId("INQUIRY_SEQ");
		String in_code = baseDao.sGetMaxNumber("Inquiry", 2);
		List<String> sqls = new ArrayList<String>();
		baseDao.execute(INSERTINQUIRY, in_id,in_code,emId,emName);
		//插入询价单物料明细表
		String sql ="";
		if(StringUtil.hasText(gridId)){
			String[] gridIdList = gridId.split(",");
			for(int i = 0;i<gridIdList.length;i++){
				sql = INSERTINQUIRYDETAIL+"'"+in_code+"',"+in_id+","+(i+1)+",BPD_PRODID, BPD_PRODCODE,SYSDATE,SYSDATE,SYSDATE,SYSDATE,BPD_DETNO,VE_CODE,VE_NAME,VE_UU,VE_WEBSERVER,BPD_BPID,VE_CURRENCY,VE_TAXRATE,VE_TAXRATE VE_TAXRATE1,VE_CURRENCY VE_CURRENCY1,VE_CODE VE_CODE1,VE_NAME VE_NAME1 "
						+ "FROM BOMPRICEDETAIL,VENDOR where bpd_id = "+gridIdList[i]+" and nvl(ve_uu,' ')='10042875'";
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		return "转询价单成功！单号： <a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS"+in_id+"&gridCondition=id_inidIS"+in_id+"')\">["+in_code+"]</a>";
	}
}
