package com.uas.erp.service.scm.impl;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.SendSampleService;

@Service
public class SendSampleServiceImpl implements SendSampleService {
	final static String SelectSendSample = "select ss_pscode,ss_code,ss_prodcode,ss_prodname,ss_spec,ss_unit,ss_isfree,ss_height,ss_material,ss_materialquality,ss_providecode,"
			+ "ss_provide,ss_address,ss_addressmark,ss_sendnum,ss_attach,ss_brand,ss_vendspec from sendSample where ss_id=?";
	static final String turnProductApproval = "insert into ProductApproval(pa_id,pa_code,pa_pscode,pa_sscode,pa_statuscode,"
			+ "pa_status,pa_prodcode,pa_prodname,pa_spec,pa_unit,pa_freeable,pa_height,pa_material,pa_materialquality,pa_providecode,"
			+ "pa_provide,pa_address,pa_addressmark,pa_recordorid,pa_recordor,pa_indate,pa_isturn,pa_sampleqty,pa_attach,"
			+ "pa_provideprodcode,pa_brand,pa_factoryspec)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String update = " update SendSample set ss_isturn = '1',ss_approstatus='已转认定' where ss_id=?";
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public String turnProductApproval(int id, String caller) {
		String log="";
		String sql = "update sendsample set (ss_providecode,ss_provide)=(select ve_code,ve_name from vendor where ve_uu=to_char(ss_venduu)) where ss_venduu is not null  and ss_id="
				+ id;
		baseDao.execute(sql);
		Object os=baseDao.getFieldDataByCondition("sendsample", "ss_code", "ss_id="+id);
		int countnum=baseDao.getCount("select count(*) from productApproval where pa_sscode='"+os+"'");
		if(countnum>0){
			log="已经转过认定单,不能重复转！";
		}else{
			SqlRowList rs = baseDao.queryForRowSet(SelectSendSample, new Object[] { id });
			if (rs.next()) {
				String code = baseDao.sGetMaxNumber("ProductApproval", 2);
				int paid=baseDao.getSeqId("ProductApproval_SEQ");
				Employee employee = SystemSession.getUser();
				try {
					baseDao.execute(
							turnProductApproval,
							new Object[] { paid, code, rs.getString("ss_pscode"), rs.getString("ss_code"),
									"ENTERING", BaseUtil.getLocalMessage("ENTERING"), rs.getString("ss_prodcode"),
									rs.getString("ss_prodname"), rs.getString("ss_spec"), rs.getString("ss_unit"), rs.getString("ss_isfree"),
									rs.getString("ss_height"), rs.getString("ss_material"), rs.getString("ss_materialquality"),
									rs.getString("ss_providecode"), rs.getString("ss_provide"), rs.getString("ss_address"),
									rs.getString("ss_addressmark"), employee.getEm_id(), employee.getEm_name(),
									Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), "0", rs.getInt("ss_sendnum"),
									rs.getString("ss_attach"),rs.getString("ss_vendspec"),rs.getString("ss_brand"), rs.getString("ss_vendspec")});
					baseDao.execute(update, new Object[] { id });
					baseDao.logger.turn("转认定操作", "SendSample", "ss_id", id);
					 log="转入成功,认定单号:<a href=\"javascript:openUrl('jsps/scm/product/ProductApproval.jsp?formCondition=pa_idIS" + paid
								+ "&gridCondition=null')\">" + code + "</a>&nbsp;"+"<hr/>";
				} catch (Exception e) {
					BaseUtil.showError("数据异常,转单失败");
				}
			}
		}		
		return log;
	}

	@Override
	public void saveSendSample(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.put("ss_yfsaveman", SystemSession.getUser().getEm_name());
		store.remove("ss_yfdate");
		formStore = BaseUtil.parseMap2Str(store);
		handlerService.handler("SendSample", "save", "before", new Object[] { formStore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SendSample", "ss_id");
		baseDao.execute(formSql);
		String sqlstr = "update sendsample set ss_yfdate=sysdate, (ss_providecode,ss_provide)=(select ve_code,ve_name from vendor where ve_uu=to_char(ss_otherenid)) where nvl(ss_providecode,' ')=' ' and ss_otherenid is not null and  ss_id="
				+ store.get("ss_id");
		baseDao.execute(sqlstr);
	}

	@Override
	public int sendToProdInout(String formStore, String param, String caller) {
		int piid = 0;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String sql = "update sendsample set (ss_providecode,ss_provide)=(select ve_code,ve_name from vendor where ve_uu=to_char(ss_otherenid)) where  nvl(ss_providecode,' ')=' ' and ss_otherenid is not null and ss_id="
				+ store.get("ss_id");
		baseDao.execute(sql);
		String isfree = store.get("ss_isfree") == null ? "" : store.get("ss_isfree").toString();
		// maz 送样单转其它入库单时进行检查不允许多次转入 2017080696
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno from prodinout where pi_class='其它入库单' and pi_sourcecode='"+store.get("ss_id")+"'");
		if(rs.next()){
			BaseUtil.showError("该单据已经转入了其它入库单,入库单号:"+rs.getObject("pi_inoutno")+"");
		}
		if (isfree.equals("是")) {
			BaseUtil.showError("收费的样品请转采购验收单！");
		} else {
			String vendcode = store.get("ss_providecode").toString();
			String prodcode = store.get("ss_prodcode").toString();
			String appcode = store.get("ss_appcode").toString();
			/*int countnum = baseDao.getCount("select count(*) from productapproval where nvl(pa_code,' ')='" + appcode
					+ "' and nvl(pa_providecode,' ')='" + vendcode + "' and nvl(pa_prodcode,' ')='" + prodcode + "'");
			if (countnum > 0) {*/
				Object code = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_sourcecode='" + store.get("ss_id")
						+ "' and nvl(pi_remark,' ')='%送样单直接生成%'");
				if (code != null && !code.equals("")) {
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.product.approval.haveturn"));
				} else {
					piid = baseDao.getSeqId("PRODINOUT_SEQ");
					String formsql = "INSERT INTO prodinout (pi_id,pi_cardcode,pi_title,pi_recorddate,"
							+ "pi_recordman,pi_invostatuscode,pi_invostatus,pi_class,pi_sourcecode,pi_inoutno,pi_statuscode,"
							+ "pi_status,pi_printstatuscode,pi_printstatus,pi_remark) VALUES (" + piid + ",'"
							+ store.get("ss_providecode") + "','" + store.get("ss_provide") + "',to_date('"
							+ new java.sql.Date(new java.util.Date().getTime()) + "','yyyy-mm-dd'),'" + SystemSession.getUser().getEm_name()
							+ "','ENTERING','在录入','其它入库单','" + store.get("ss_id") + "','" + baseDao.sGetMaxNumber("ProdInOut!OtherIn", 2)
							+ "','UNPOST','" + BaseUtil.getLocalMessage("UNPOST") + "','UNPRINT','"
							+ BaseUtil.getLocalMessage("UNPRINT") + "','" + store.get("ss_code") + "送样单直接生成')";
					baseDao.execute(formsql);
					Double price = store.get("ss_sampleprice") == null ? 0 : Double.parseDouble(store.get("ss_sampleprice").toString());
					String gridsql = "INSERT INTO ProdIODetail (pd_pdno,pd_prodcode,pd_inqty,pd_orderprice,pd_ordertotal,pd_piid,pd_id,pd_piclass,"
							+ "pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_status,pd_taxrate) VALUES (1,'"
							+ store.get("ss_prodcode")
							+ "','"
							+ store.get("ss_sendnum")
							+ "','"
							+ price
							+ "','"
							+ store.get("ss_allmoney")
							+ "',"
							+ piid
							+ ","
							+ baseDao.getSeqId("ProdIODetail_Seq")
							+ ",'其它入库单','ENTERING','UNACCOUNT','"
							+ BaseUtil.getLocalMessage("UNACCOUNT") + "','0','" + store.get("ss_rate") + "')";
					baseDao.execute(gridsql);					
					//更新转单后明细物料ID
					baseDao.execute("update prodiodetail set pd_prodid=(select pr_id from product where pr_code=pd_prodcode) where pd_piid="+piid);
					baseDao.execute("update sendsample set ss_appcode='" + appcode + "',ss_condition='已转其它入库' where ss_id=" + store.get("ss_id"));
				}
			/*} else {
				BaseUtil.showError("请确认关联的认定单结果是合格的，并且物料和供应商与送样单一致！");
			}*/
		}
		return piid;
	}

	@Override
	public int sendToPurInout(String formStore, String param, String caller) {

		int piid = 0;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String sql = "update sendsample set (ss_providecode,ss_provide)=(select ve_code,ve_name from vendor where ve_uu=to_char(ss_otherenid)) where  nvl(ss_providecode,' ')=' ' and ss_otherenid is not null and  ss_id="
				+ store.get("ss_id");
		baseDao.execute(sql);
		String isfree = store.get("ss_isfree") == null ? "" : store.get("ss_isfree").toString();
		double puprice = Double.parseDouble(store.get("ss_sampleprice").toString()) == 0 ? 0 : Double.parseDouble(store.get(
				"ss_sampleprice").toString());
		if (puprice == 0) {
			BaseUtil.showError("收费的样品采购单价不能为0！");
		}
		if (isfree.equals("是")) {
			String vendcode = store.get("ss_providecode").toString();
			String prodcode = store.get("ss_prodcode").toString();
			String appcode = store.get("ss_appcode").toString();
			/*int countnum = baseDao.getCount("select count(*) from productapproval where nvl(pa_code,' ')='" + appcode
					+ "' and nvl(pa_providecode,' ')='" + vendcode + "' and nvl(pa_prodcode,' ')='" + prodcode + "'");
			if (countnum > 0) {*/
				Object code = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_sourcecode='" + store.get("ss_id")
						+ "'  and nvl(pi_remark,' ') like '%送样单直接生成%'");
				if (code != null && !code.equals("")) {
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.product.approval.haveturn"));
				} else {
					piid = baseDao.getSeqId("PRODINOUT_SEQ");
					String formsql = "INSERT INTO prodinout (pi_id,pi_cardcode,pi_title,pi_recorddate,"
							+ "pi_recordman,pi_invostatuscode,pi_invostatus,pi_class,pi_sourcecode,pi_inoutno,pi_statuscode,"
							+ "pi_status,pi_printstatuscode,pi_printstatus,pi_remark) VALUES (" + piid + ",'"
							+ store.get("ss_providecode") + "','" + store.get("ss_provide") + "',to_date('"
							+ new java.sql.Date(new java.util.Date().getTime()) + "','yyyy-mm-dd'),'" + SystemSession.getUser().getEm_name()
							+ "','ENTERING','在录入','采购验收单','" + store.get("ss_id") + "','"
							+ baseDao.sGetMaxNumber("ProdInOut!PurcCheckin", 2) + "','UNPOST','"
							+ BaseUtil.getLocalMessage("UNPOST") + "','UNPRINT','"
							+ BaseUtil.getLocalMessage("UNPRINT") + "','" + store.get("ss_code") + "送样单直接生成')";
					baseDao.execute(formsql);
					Double price = store.get("ss_sampleprice") == null ? 0 : Double.parseDouble(store.get("ss_sampleprice").toString());
					String gridsql = "INSERT INTO ProdIODetail (pd_pdno,pd_prodcode,pd_inqty,pd_orderprice,pd_ordertotal,pd_piid,pd_id,pd_piclass,"
							+ "pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_status,pd_taxrate) VALUES (1,'"
							+ store.get("ss_prodcode")
							+ "','"
							+ store.get("ss_sendnum")
							+ "','"
							+ price
							+ "','"
							+ store.get("ss_allmoney")
							+ "',"
							+ piid
							+ ","
							+ baseDao.getSeqId("ProdIODetail_Seq")
							+ ",'采购验收单','ENTERING','UNACCOUNT','"
							+ BaseUtil.getLocalMessage("UNACCOUNT") + "',0,'" + store.get("ss_rate") + "')";
					baseDao.execute(gridsql);
					//更新转单后明细物料ID
					baseDao.execute("update prodiodetail set pd_prodid=(select pr_id from product where pr_code=pd_prodcode) where pd_piid="+piid);
					//更新应付信息
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename,pi_paymentcode,pi_payment,pi_currency)=(select ve_apvendcode,ve_apvendname,ve_paymentcode,ve_payment,ve_currency from vendor where ve_code=pi_cardcode and pi_id="+piid+") where pi_id="+piid);
					//更新汇率
					baseDao.execute("update prodinout set pi_rate=(select cr_rate from currencys where cr_name=pi_currency and  pi_id="+piid+") where pi_id="+piid);
					baseDao.execute("update sendsample set ss_appcode='" + appcode + "',ss_condition='已转采购验收单' where ss_id=" + store.get("ss_id"));
				}
			} else {
				BaseUtil.showError("请确认关联的认定单结果是合格的，并且物料和供应商与送样单一致！");
			}
		/*} else {
			BaseUtil.showError("收费的样品才能转采购验收单！");
		}*/

		return piid;
	}
}
