package com.uas.erp.service.b2b.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.b2b.CustSendSampleService;

@Service("custSendSampleService")
public class CustSendSampleServiceImpl implements CustSendSampleService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateCustSendSample(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 查询对应的客户送样申请单号
		Object ss_pscode = baseDao.getFieldDataByCondition("CustSendSample", "ss_pscode", "ss_id=" + store.get("ss_id"));
		Object ps_qty = baseDao.getFieldDataByCondition("Productsampledown", "ps_qty", "ps_code=" + Long.parseLong(ss_pscode.toString()));
		if (Long.parseLong(ps_qty.toString()) != 0
				&& Long.parseLong(store.get("ss_sendnum").toString()) > Long.parseLong(ps_qty.toString())) {
			BaseUtil.showError("送样数量不能大于来源申请单 的要求样品数量！必须小于" + Long.parseLong(ps_qty.toString()));
		} else {
			// 只能修改[在录入]的采购单资料!
			Object status = baseDao.getFieldDataByCondition("CustSendSample", "ss_statuscode", "ss_id=" + store.get("ss_id"));
			StateAssert.updateOnlyEntering(status);
			// 执行修改前的其它逻辑
			handlerService.handler("CustSendSample", "save", "before", new Object[] { store });
			Object ssid = store.get("ss_id");
			baseDao.execute("update CustSendSample set (ss_custcode,ss_custname)=(select cu_code,cu_name from customer where cu_uu=ss_custuu)"
					+ " where nvl(ss_custcode,' ')=' '");
			Object uu = baseDao.getFieldDataByCondition("CustSendSample", "ss_custuu", "ss_id=" + ssid);
			if (uu != null) {
				if (!baseDao.checkIf("customer", "cu_uu='" + uu + "'")) {
					BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
				}
			}
			Object cucode = store.get("ss_custcode");
			if (!StringUtil.hasText(cucode)) {
				BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
			}
			Object custprodcode = store.get("ss_custprodcode");
			Object prodcode = store.get("ss_prodcode");
			String sql = null;
			if (StringUtil.hasText(custprodcode)) {
				if (StringUtil.hasText(prodcode)) {
					if (baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_prodcode='" + prodcode + "'")) {
						BaseUtil.showError("物料[" + prodcode + "]已经有对应的客户产品料号，请更改或删除原来数据！");
					}
					if (!baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_custprodcode='" + custprodcode
							+ "' and pc_prodcode='" + prodcode + "'")) {
						Object i = baseDao.getFieldDataByCondition("productcustomer", "max(nvl(pc_detno,0))", "PC_CUSTCODE='" + cucode
								+ "'");
						i = i == null ? 0 : i;
						sql = "Insert into productcustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
								+ "PC_CUSTPRODDETAIL,PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) "
								+ " select ProductCustomer_seq.nextval, cu_id," + (Integer.parseInt(i.toString()) + 1)
								+ ",pr_id,ss_custprodcode,ss_custproddetail,ss_custspec,ss_custunit,ss_custcode,ss_custname,"
								+ "ss_prodcode from CustSendSample,customer,product where ss_custcode=cu_code and ss_prodcode=pr_code"
								+ " AND ss_id=" + ssid;
					}
				} else {
					Object prcode = baseDao.getFieldDataByCondition("productcustomer", "pc_prodcode", "pc_custcode='" + cucode
							+ "' and pc_custprodcode='" + custprodcode + "'");
					if (StringUtil.hasText(prcode)) {
						store.put("ss_prodcode", prcode);
					} else {
						BaseUtil.showError("没有客户物料对照资料，请手工填写物料编号！");
					}
				}
			}
			// 修改
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustSendSample", "ss_id");
			baseDao.execute(formSql);
			if (sql != null) {
				baseDao.execute(sql);
			}
			baseDao.execute("update CustSendSample set ss_prodcode=(select max(pc_prodcode) from productcustomer where pc_custcode='"
					+ cucode + "' and pc_custprodcode=ss_custprodcode) where nvl(ss_prodcode,' ')=' ' and nvl(ss_custprodcode,' ')<>' ' "
					+ "and ss_custcode='" + cucode + "'");
			baseDao.execute("update CustSendSample set (ss_prodname,ss_prodspec,ss_unit)=(select pr_detail,pr_spec,pr_unit from Product where ss_prodcode=pr_code) where nvl(ss_prodcode,' ')<>' ' and ss_id="
					+ ssid);
			baseDao.execute("update CustSendSample set ss_allmoney=nvl(ss_sampleprice,0)*nvl(ss_sendnum,0) where ss_id=" + ssid);
			// 记录操作
			baseDao.logger.update("CustSendSample", "ss_id", ssid);
			// 执行修改后的其它逻辑
			handlerService.handler("CustSendSample", "save", "after", new Object[] { store });
		}

	}

	@Override
	public void submitCustSendSample(int ss_id, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustSendSample", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.submitOnlyEntering(status);
		checkProduct(ss_id);
		baseDao.execute("update CustSendSample set ss_allmoney=nvl(ss_sampleprice,0)*nvl(ss_sendnum,0) where ss_id=" + ss_id);
		// 执行提交前的其它逻辑
		handlerService.handler("CustSendSample", "commit", "before", new Object[] { ss_id });
		// 执行提交操作
		baseDao.submit("CustSendSample", "ss_id=" + ss_id, "ss_status", "ss_statuscode");
		// 记录操作
		baseDao.logger.submit("CustSendSample", "ss_id", ss_id);
		// 执行提交后的其它逻辑
		handlerService.handler("CustSendSample", "commit", "after", new Object[] { ss_id });

	}

	@Override
	public void resSubmitCustSendSample(int ss_id, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustSendSample", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("CustSendSample", "resCommit", "before", new Object[] { ss_id });
		// 执行反提交操作
		baseDao.resOperate("CustSendSample", "ss_id=" + ss_id, "ss_status", "ss_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("CustSendSample", "ss_id", ss_id);
		handlerService.handler("CustSendSample", "resCommit", "after", new Object[] { ss_id });
	}

	@Override
	public void auditCustSendSample(int ss_id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("CustSendSample", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("CustSendSample", "audit", "before", new Object[] { ss_id });
		// 执行审核操作
		baseDao.audit("CustSendSample", "ss_id=" + ss_id, "ss_status", "ss_statuscode", "ss_auditdate", "ss_auditman");
		baseDao.execute("update CustSendSample set ss_sendstatus='待上传' where ss_id=" + ss_id);
		// 记录操作
		baseDao.logger.audit("CustSendSample", "ss_id", ss_id);
		// 执行审核后的其它逻辑
		handlerService.handler("CustSendSample", "audit", "after", new Object[] { ss_id });

	}

	@Override
	public void resAuditCustSendSample(int ss_id, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustSendSample", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("CustSendSample", "ss_id=" + ss_id, "ss_status", "ss_statuscode");
		// 记录操作
		baseDao.logger.resAudit("CustSendSample", "ss_id", ss_id);
	}

	@Override
	public int CustSendToProdInout(String formStore, String param, String caller) {
		int piid = 0;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String isfree = store.get("ss_isfree") == null ? "" : store.get("ss_isfree").toString();
		if (isfree.equals("是")) {
			BaseUtil.showError("收费的样品请转出货单！");
		} else {
			Object code = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_sourcecode='" + store.get("ss_id")
					+ "' and nvl(pi_type,' ')='样品出库'");
			if (code != null && !code.equals("")) {
				BaseUtil.showError("已经转过其它出库单了，不能重复转！");
			} else {
				piid = baseDao.getSeqId("PRODINOUT_SEQ");
				String picode = baseDao.sGetMaxNumber("ProdInOut!OtherOut", 2);
				String formsql = "INSERT INTO prodinout (pi_id,pi_cardcode,pi_title,pi_recorddate,"
						+ "pi_recordman,pi_invostatuscode,pi_invostatus,pi_class,pi_sourcecode,pi_inoutno,pi_statuscode,"
						+ "pi_status,pi_printstatuscode,pi_printstatus,pi_remark,pi_type,pi_currency) VALUES ("
						+ piid
						+ ",'"
						+ store.get("ss_custcode")
						+ "','"
						+ store.get("ss_custname")
						+ "',to_date('"
						+ new java.sql.Date(new java.util.Date().getTime())
						+ "','yyyy-mm-dd'),'"
						+ SystemSession.getUser().getEm_name()
						+ "','ENTERING','在录入','其它出库单','"
						+ store.get("ss_id")
						+ "','"
						+ picode
						+ "','UNPOST','"
						+ BaseUtil.getLocalMessage("UNPOST")
						+ "','UNPRINT','"
						+ BaseUtil.getLocalMessage("UNPRINT")
						+ "','"
						+ store.get("ss_remark") + "','样品出库','" + store.get("ss_currency") + "')";
				baseDao.execute(formsql);
				Double price = store.get("ss_sampleprice") == null ? 0 : Double.parseDouble(store.get("ss_sampleprice").toString());
				String gridsql = "INSERT INTO ProdIODetail (pd_pdno,pd_prodcode,pd_outqty,pd_orderprice,pd_ordertotal,pd_piid,pd_id,pd_piclass,"
						+ "pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_status,pd_taxrate,pd_custprodcode,pd_custprodspec) VALUES (1,'"
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
						+ ",'其它出库单','ENTERING','UNACCOUNT','"
						+ BaseUtil.getLocalMessage("UNACCOUNT")
						+ "','0','"
						+ store.get("ss_rate")
						+ "','"
						+ store.get("ss_custprodcode")
						+ "','" + store.get("ss_custspec") + "')";
				baseDao.execute(gridsql);
				String updateSql = "update CUSTSENDSAMPLE set ss_ProdInOutCode = '" + picode + "'" + " where ss_id =" + store.get("ss_id");
				baseDao.execute(updateSql);
				/*
				 * baseDao.execute("update sendsample set ss_appcode='" +
				 * appcode + "' where ss_id=" + store.get("ss_id"));
				 */
			}
		}
		return piid;
	}

	@Override
	public int CustSendToPurInout(String formStore, String param, String caller) {
		int piid = 0;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/*
		 * String sql =
		 * "update sendsample set (ss_providecode,ss_provide)=(select ve_code,ve_name from vendor where ve_uu=ss_otherenid) where ss_id="
		 * + store.get("ss_id"); baseDao.execute(sql);
		 */
		String isfree = store.get("ss_isfree") == null ? "" : store.get("ss_isfree").toString();
		double puprice = Double.parseDouble(store.get("ss_sampleprice").toString()) == 0 ? 0 : Double.parseDouble(store.get(
				"ss_sampleprice").toString());
		if (puprice == 0) {
			BaseUtil.showError("收费的样品单价不能为0！");
		}
		if (isfree.equals("是")) {
			Object code = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_sourcecode='" + store.get("ss_id")
					+ "'  and nvl(pi_type,' ')='样品出库'");
			if (code != null && !code.equals("")) {
				BaseUtil.showError("已经转过出货单了，不能重复转！");
			} else {
				piid = baseDao.getSeqId("PRODINOUT_SEQ");
				String picode = baseDao.sGetMaxNumber("ProdInOut!Sale", 2);
				String formsql = "INSERT INTO prodinout (pi_id,pi_cardcode,pi_title,pi_recorddate,"
						+ "pi_recordman,pi_invostatuscode,pi_invostatus,pi_class,pi_sourcecode,pi_inoutno,pi_statuscode,"
						+ "pi_status,pi_printstatuscode,pi_printstatus,pi_remark,pi_type,pi_currency) VALUES ("
						+ piid
						+ ",'"
						+ store.get("ss_custcode")
						+ "','"
						+ store.get("ss_custname")
						+ "',to_date('"
						+ new java.sql.Date(new java.util.Date().getTime())
						+ "','yyyy-mm-dd'),'"
						+ SystemSession.getUser().getEm_name()
						+ "','ENTERING','在录入','出货单','"
						+ store.get("ss_id")
						+ "','"
						+ picode
						+ "','UNPOST','"
						+ BaseUtil.getLocalMessage("UNPOST")
						+ "','UNPRINT','"
						+ BaseUtil.getLocalMessage("UNPRINT")
						+ "','"
						+ store.get("ss_remark") + "','样品出库','" + store.get("ss_currency") + "')";
				baseDao.execute(formsql);
				Double price = store.get("ss_sampleprice") == null ? 0 : Double.parseDouble(store.get("ss_sampleprice").toString());
				String gridsql = "INSERT INTO ProdIODetail (pd_pdno,pd_prodcode,pd_outqty,pd_sendprice,pd_ordertotal,pd_piid,pd_id,pd_piclass,"
						+ "pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_status,pd_taxrate,pd_custprodcode,pd_custprodspec) VALUES (1,'"
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
						+ ",'出货单','ENTERING','UNACCOUNT','"
						+ BaseUtil.getLocalMessage("UNACCOUNT")
						+ "',0,'"
						+ store.get("ss_rate")
						+ "','"
						+ store.get("ss_custprodcode")
						+ "','" + store.get("ss_custspec") + "')";
				baseDao.execute(gridsql);
				String updateSql = "update CUSTSENDSAMPLE set ss_ProdInOutCode = '" + picode + "'" + " where ss_id =" + store.get("ss_id");
				baseDao.execute(updateSql);
				/*
				 * baseDao.execute("update sendsample set ss_appcode='" +
				 * appcode + "' where ss_id=" + store.get("ss_id"));
				 */
			}

		} else {
			BaseUtil.showError("收费的样品才能转出货单！");
		}

		return piid;
	}

	private void checkProduct(int ss_id) {
		baseDao.execute(
				"update custsendsample sd1 set (ss_prodid,ss_prodcode)=(select max(pc_prodid),max(pc_prodcode) from productcustomer,custsendsample sd2 where  pc_custcode=ss_custcode and pc_custprodcode=sd2.ss_custprodcode and pc_custproddetail=sd2.ss_custproddetail and pc_custprodspec=sd2.ss_custspec and sd1.ss_id=sd2.ss_id) where sd1.ss_id = ?",
				ss_id);
		int noneProduct = baseDao.getCount("select count(*) from custsendsample where nvl(ss_prodid,0)>0  and  ss_id=" + ss_id);
		if (noneProduct > 0) {
			baseDao.execute("update custsendsample set (ss_prodname,ss_prodspec,ss_unit)=(select pr_detail,pr_spec,pr_unit from product where pr_id=ss_prodid) where ss_id="
					+ ss_id);
		} else {
			BaseUtil.showError("物料还未建立【客户物料对照关系】");
		}

	}
}
