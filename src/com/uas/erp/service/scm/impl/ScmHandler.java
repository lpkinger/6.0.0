package com.uas.erp.service.scm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.uas.b2b.model.VendorRate;
import com.uas.b2c.service.common.GetGoodsReserveService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.JwtUtil;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AcceptNotifyDao;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.dao.common.BorrowApplyDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.OtherExplistDao;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.QUAVerifyApplyDetailDao;
import com.uas.erp.dao.common.ReturnApplyDao;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.dao.common.SaleForecastDao;
import com.uas.erp.dao.common.SendNotifyDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.ApplicationService;
import com.uas.erp.service.scm.PreProductService;
import com.uas.erp.service.scm.ProductKindService;
import com.uas.erp.service.scm.SaleClashService;
import com.uas.erp.service.scm.SaleDetailDetService;
import com.uas.erp.service.scm.VerifyApplyService;

/**
 * scm模块的所有业务逻辑 单据执行的业务逻辑需先从documentSetup表得到对应的配置，根据配置再调用scmHandler中对用的方法 注意:
 * 该类里面的方法所带的参数类型一定严格对应传递过来的参数类型 如Integer不能写成int，HashMap不能写成Map
 * 需传递参数:保存时,HashMap<Object, Object> store, ArrayList<Map<Object, Object>>
 * gstore 删除时,Integer id 删除明细时,Integer id 提交时,Integer id, String language,
 * Employee employee 传递的参数需统一，以方便扩展
 * 
 * @author yingp
 */
@Service("ScmHandler")
public class ScmHandler {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private SendNotifyDao sendNotifyDao;
	@Autowired
	private AcceptNotifyDao acceptNotifyDao;
	@Autowired
	private SaleClashService saleClashService;
	@Autowired
	private QUAVerifyApplyDetailDao quaVerifyApplyDetailDao;
	@Autowired
	private BorrowApplyDao borrowApplyDao;
	@Autowired
	private SaleDetailDetService saleDetailDetService;
	@Autowired
	private ProductKindService productKindService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ReturnApplyDao returnApplyDao;
	@Autowired
	private SaleForecastDao saleForecastDao;
	@Autowired
	private GetGoodsReserveService getGoodsReserveService;
	@Autowired
	private PreProductService preProductService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private OtherExplistDao otherExplistDao;
	@Autowired
	private VerifyApplyDao verifyApplyDao;
	@Autowired
	private VerifyApplyService verifyApplyService;
	@Autowired
	private ApplicationService applicationService;

	// 贝腾 正式客户审核后，将客户编号反写回商机
	public void auditCustomerToBusinessChance(Integer cu_id) {

		Object cu_nichecode = baseDao.getFieldDataByCondition("Customer", "cu_nichecode", "cu_id='" + cu_id + "'");
		Object cu_code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_id='" + cu_id + "'");
		if (cu_nichecode != null) {
			baseDao.updateByCondition("BusinessChance", "bc_custcode='" + cu_code + "'", "bc_code='" + cu_nichecode + "'");
		}
	}

	// 贝腾 销售订单审核后，则将商机阶段改为成交客户
	public void sale_audit_after_updateBusinessChance(Integer sa_id) {
		Object sa_custname = baseDao.getFieldDataByCondition("Sale", "sa_custname", "sa_id='" + sa_id + "'");
		if (sa_custname != null) {
			baseDao.updateByCondition("BusinessChance", "bc_currentprocess='成交客户'", "bc_custname='" + sa_custname + "'");
		}
	}

	/**
	 * Purchase->purchase->delete 已经生成了收料单,不能删除!
	 */
	public void purchase_delete_vadcode(Integer pu_id) {
		Object code = baseDao.getFieldDataByCondition("Purchase", "pu_code", "pu_id=" + pu_id);
		boolean bool = baseDao.checkByCondition("VerifyApplyDetail", "vad_pucode='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.delete_vadcodeHasExist"));
		}
	}

	public void purchase_commit_before_checkprod(Integer pu_id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("purchasedetail left join applicationdetail on ad_id=pd_sourcedetail",
				new String[] { "pd_sourcedetail", "pd_detno" }, "pd_puid=" + pu_id
						+ " and pd_sourcedetail is not  null and pd_sourcedetail<>0 and pd_prodcode<>ad_prodcode");
		StringBuffer sb = new StringBuffer("明细行物料编号必须与来源请购单一致，行号：");
		for (Object[] obj : objs) {
			int count = baseDao
					.getCount("select count(1) from ApplicationReplace left join purchasedetail on ar_adid=pd_sourcedetail where ar_repcode=pd_prodcode and ar_adid="
							+ obj[0]);
			if (count == 0) {
				sb.append(obj[1].toString()).append(",");
			}
		}
		if (sb.length() > 21) {
			BaseUtil.showError(sb.substring(0, sb.length() - 1));
		}
	}

	/**
	 * 更新物料采购资料时,状态改为待上传
	 * 
	 * @param store
	 */
	public void Product_Purchase_update(HashMap<Object, Object> store) {
		store.put("pr_sendstatus", "待上传");
	}

	/**
	 * ProdInOut!GoodsIn 用品验收删除明细时还原到用品采购
	 */
	public void ProdInOutGoodsIn_deletedetail(Integer pd_id) {
		Object[] data = baseDao.getFieldsDataByCondition("prodiodetail", new String[] { "pd_inqty", "pd_orderid" }, "pd_id=" + pd_id);
		if (data != null) {
			baseDao.execute("update OAPurchaseDetail set od_yqty=nvl(od_yqty,0)-" + data[0] + " where od_id=" + data[1]);
		}
	}

	/**
	 * ProdInOut!GoodsPicking 用品领用删除明细时还原到用品申请单
	 */
	public void ProdInOutGoodsPicking_deletedetail(Integer pd_id) {
		Object[] data = baseDao.getFieldsDataByCondition("prodiodetail", new String[] { "nvl(pd_outqty,0)", "pd_orderid" },
				"nvl(pd_orderid,0)<>0 and pd_id=" + pd_id);
		if (data != null) {
			baseDao.execute("update Oaapplicationdetail set od_turnlyqty=nvl(od_turnlyqty,0)-" + data[0] + " where od_id=" + data[1]);
		}
	}

	/**
	 * productapproval_saveorupadte 物料认定保存或者更新时，如果新物料 名称和规格为空则默认为原来的(万利达)
	 */
	public void productapproval_saveorupadte_newname(HashMap<Object, Object> store) {
		if (store.get("pa_newprodname") == null || "".equals(store.get("pa_newprodname") + "")) {
			store.put("pa_newprodname", store.get("pa_prodname"));
		}
		if (store.get("pa_newspec") == null || "".equals(store.get("pa_newspec") + "")) {
			store.put("pa_newspec", store.get("pa_spec"));
		}
		if (store.get("pa_newspeccs") == null || "".equals(store.get("pa_newspeccs") + "")) {
			store.put("pa_newspeccs", store.get("pa_speccs"));
		}
		if (store.get("pa_attach") == null || "".equals(store.get("pa_attach") + "")) {
			Object pa_attach = baseDao.getFieldDataByCondition("product", "pr_attach", " pr_code='" + store.get("pa_prodcode") + "'");
			store.put("pa_attach", pa_attach);
		}
	}

	/**
	 * 如果认定单上有物料的新名称和规格且认定结果为合格，则更新到物料资料上(万利达)
	 * 
	 */
	public void productapproval_auditafter_updatename(Integer id) {
		Object result = baseDao.getFieldDataByCondition("ProductApproval", "pa_finalresult", "pa_id=" + id);
		if ("合格".equals(result + "")) {
			Object[] data = baseDao.getFieldsDataByCondition("ProductApproval", new String[] { "pa_prodcode", "pa_newprodname",
					"pa_newspec", "pa_newspeccs" }, "pa_id=" + id);
			StringBuffer sb = new StringBuffer();
			// 为空则不修改
			if (!(data[1] == null || "".equals(data[1]))) {
				sb.append("pr_detail='" + data[1] + "',");
			}
			if (!(data[2] == null || "".equals(data[2]))) {
				sb.append("pr_spec='" + data[2] + "',");
			}
			if (!(data[3] == null || "".equals(data[3]))) {
				sb.append("pr_speccs='" + data[3] + "',");
			}
			if (sb.length() > 0) {
				baseDao.execute("update Product set " + sb.toString().substring(0, sb.length() - 1) + " where pr_code='" + data[0] + "'");
				baseDao.execute("update Product set pr_sendstatus='待上传' where pr_code='" + data[0] + "'");
			}
		}
	}

	/**
	 * 如果认定单上有物料的新名称和规格且认定结果为合格，则更新到物料资料上(附件)
	 * 
	 */
	public void productapproval_auditafter_updateattch(Integer id) {
		Object result = baseDao.getFieldDataByCondition("ProductApproval", "pa_finalresult", "pa_id=" + id);
		if ("合格".equals(result + "")) {
			Object[] attach = baseDao.getFieldsDataByCondition("ProductApproval", new String[] { "pa_attach", "pa_prodcode" }, "pa_id="
					+ id);
			if (!(attach[0] == null || "".equals(attach[0]))) {
				baseDao.execute("update product set pr_attach='" + attach[0] + "' where pr_code='" + attach[1] + "'");
			}
		}
	}

	/**
	 * Purchase->purchase->save 下达数不能超计划数量检测
	 */
	public void purchase_save_planqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String check = purchaseDao.checkPlanQty(Integer.parseInt((String) store.get("pu_id")));
		if (check != null && check.length() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> map : BaseUtil.parseGridStoreToMaps(check)) {
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_prodcode"));
				sb.append(map.get("pp_prodcode"));
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_endqty"));
				sb.append(map.get("pp_endqty"));
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_planqty"));
				sb.append(map.get("pp_planqty"));
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_ppid"));
				sb.append(map.get("pp_id"));
				sb.append("<br/>");
			}
			BaseUtil.showError(sb.toString());
		}
	}

	/**
	 * 自动更新明细行的仓库
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void sendnotify_saveWarehouse(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sn_id = store.get("sn_id");
		if (sn_id != null) {
			baseDao.execute(
					"update sendnotifydetail set (snd_warehousecode,snd_warehouse)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code left join sendnotifydetail on snd_prodcode=pr_code) where snd_snid=? and nvl(snd_warehousecode,' ')=' ' and nvl(snd_prodcode,' ')<>' '",
					sn_id);
		}
	}

	/**
	 * Purchase->purchase->save 计划ID料号错位检测
	 */
	public void purchase_save_pddetno(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String check = purchaseDao.checkPPcode(Integer.parseInt((String) store.get("pu_id")));
		if (check != null && check.length() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> map : BaseUtil.parseGridStoreToMaps(check)) {
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_pddetno"));
				sb.append(map.get("pd_detno"));
				sb.append(BaseUtil.getLocalMessage("scm.purchase.purchase.update_prodcode"));
				sb.append(map.get("pp_prodcode"));
				sb.append("]!<br/>");
			}
			BaseUtil.showError(sb.toString());
		}
	}

	/**
	 * Purchase->purchase->print 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品!不能打印
	 */
	public void purchase_print_prodcode(Integer pu_id) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code",
				"pr_code IN (SELECT pd_prodcode FROM purchasedetail WHERE " + "pd_puid=" + pu_id
						+ ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.print_prodcode") + sb.toString());
		}
	}

	/**
	 * 更新采购明细价格
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void purchase_update_price(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		boolean bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		if (bool) {
			purchaseDao.getPrice(Integer.parseInt(store.get("pu_id").toString()));
		}
	}

	/**
	 * 更新采购供应商
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void purchase_commitbefore_putype(Integer id) {
		Object putype = baseDao.getFieldDataByCondition("purchase", "pu_mainmark", "pu_id=" + id);
		boolean bool = putype.toString().equals("标准");// 是否自动获取单价
		if (bool) {
			purchaseDao.getPutype(id);
		}
	}

	/**
	 * 更新采购供应商
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void purchase_commitbefore_puvendoruu(Integer id) {
		Object puvendor = baseDao.getFieldDataByCondition("purchase", "pu_vendcode", "pu_id=" + id);
		Object vendoruu = baseDao.getFieldDataByCondition("vendor", "ve_uu", "ve_code='" + puvendor.toString() + "'");
		if (vendoruu != null && !vendoruu.equals("")) {

		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.notvendoruu"));
		}
	}

	/**
	 * 采购变更单审核后，更新原单据的总金额
	 * 
	 */
	public void purchasechange_auditafter_updateprice(Integer id) {
		Object pu_id = baseDao.getFieldDataByCondition("Purchase left join Purchasechange on pu_code=pc_purccode", "pu_id", "pc_id=" + id);
		SqlRowList srs = baseDao.queryForRowSet("select sum(pd_total) from PurchaseDetail group by pd_puid having pd_puid=" + pu_id);
		srs.next();
		Object count = srs.getObject(1);
		String money = MoneyUtil.toChinese(count.toString());
		baseDao.updateByCondition("Purchase", "pu_total='" + count + "',pu_totalupper='" + money + "'", "pu_id=" + pu_id);
	}

	/**
	 * 采购单：更新时，如果采购单上供应商有变化，则采购订单类型改变为非标准类型
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void purchase_update_putype(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		if (StringUtil.hasText(store.get("pu_vendcode"))) {
			Object vendcode = baseDao.getFieldDataByCondition("purchase", "pu_vendcode", "pu_id=" + store.get("pu_id"));
			if (StringUtil.hasText(vendcode)) {
				if (!store.get("pu_vendcode").toString().equals(vendcode.toString())) {// 判断供应商是否有变动
					store.put("pu_mainmark", "非标准");
				}
			}
		}
	}

	/**
	 * 判断送样数量是否等于分配数量
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void productapp_sampleqty(Integer id) {
		int count = 0;
		count = baseDao
				.getCount("select count(*) from productapproval where nvl(pa_sampleqty,0)<>nvl(pa_qtya,0)+nvl(pa_qtyb,0)+nvl(pa_qtyc,0) and pa_id="
						+ id);
		if (count > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.product.productapp_sampleqty"));
		} else {

		}
	}

	/**
	 * 审核后打印的判断
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void purchase_printCheckStatus(Integer id) {
		Object status = baseDao.getFieldDataByCondition("purchase", "pu_statuscode", "pu_id=" + id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError("只能打印已审核的单据！");
		}
	}

	/**
	 * 13-12-19 yaozx
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */

	public void purchase_checkcurrencyRMB(Integer id) {
		Object currncy = baseDao.getFieldDataByCondition("purchase", "pu_currency", "pu_id=" + id);
		if (currncy != null && !currncy.toString().equals("RMB")) {
			BaseUtil.showError("该账套只能使用RMB,单据提交失败！");
		}
	}

	public void purchase_checkcurrencyUSD(Integer id) {
		Object currncy = baseDao.getFieldDataByCondition("purchase", "pu_currency", "pu_id=" + id);
		if (currncy != null && currncy.toString().equals("RMB")) {
			BaseUtil.showError("该账套只能使用外币，单据提交失败！");
		}
	}

	public void sale_checkcurrencyUSD(Integer id) {
		Object currncy = baseDao.getFieldDataByCondition("sale", "sa_currency", "sa_id=" + id);
		if (currncy != null && currncy.toString().equals("RMB")) {
			BaseUtil.showError("该账套只能使用外币，单据提交失败！");
		}
	}

	public void sale_checkcurrencyRMB(Integer id) {
		Object currncy = baseDao.getFieldDataByCondition("sale", "sa_currency", "sa_id=" + id);
		if (currncy != null && !currncy.toString().equals("RMB")) {
			BaseUtil.showError("该账套只能使用RMB，单据提交失败！");
		}
	}

	/**
	 * 宇声数码帐套采购单币种 除去WORLDSHINE供应商外，均为RMB
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void purchase_currency_eshinedigit(Integer id) {
		boolean isOth = baseDao.checkIf("Purchase", "pu_id=" + id + " and pu_vendcode<>'WORLDSHINE' and nvl(pu_currency,' ')<>'RMB'");
		if (isOth) {
			BaseUtil.showError("宇声数码账套只能使用RMB，单据提交失败！");
		}
	}

	/**
	 * 华商龙资料中心PM产品线申请表审核后自动同步到所有子账套
	 */
	public void LineApply_audit_after_sync(Integer id) {
		Object ma_soncode = baseDao.getFieldDataByCondition("master", "ma_soncode", "ma_user='" + SystemSession.getUser().getEm_master()
				+ "'");
		baseDao.callProcedure("SYS_POST", new Object[] { "LineApply!Post", SpObserver.getSp(), ma_soncode, String.valueOf(id),
				SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
	}

	/**
	 * 华商龙资料中心SPM产品线申请表审核后自动同步到所有子账套
	 */
	public void BigLineApply_audit_after_sync(Integer id) {
		Object ma_soncode = baseDao.getFieldDataByCondition("master", "ma_soncode", "ma_user='" + SystemSession.getUser().getEm_master()
				+ "'");
		baseDao.callProcedure("SYS_POST", new Object[] { "BigLineApply!Post", SpObserver.getSp(), ma_soncode, String.valueOf(id),
				SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
	}

	/**
	 * 采购单提交之前更新采购明细价格 Purchase->commit->after
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void purchase_commit_update_price(Integer id) {
		boolean bool = baseDao.checkByCondition("Purchase", "pu_getprice='-1' and pu_id=" + id);
		if (!bool) {
			purchaseDao.getPrice(id);
		}
	}

	/**
	 * 采购单提交之前更新上次采购明细价格 Purchase->commit->after
	 * 
	 * yaozx@14-01-07 modify @2015-07-3
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void purchase_commit_update_preprice(Integer id) {
		SqlRowList sqlRowList = baseDao.queryForRowSet("select * from purchasedetail left join purchase on pd_puid=pu_id where pd_puid="
				+ id + " and nvl(pu_ordertype,' ')<>'B2C'");
		SqlRowList sList = null;		
		while (sqlRowList.next()) {
			String sql, sql2 = "";
			sql = "SELECT PurchaseDetail.pd_price*Purchase.pu_rate as pd_oriprice,nvl(ve_shortname,ve_name) ve_shortname,pd_code from Purchase,PurchaseDetail,Vendor WHERE pu_id=pd_puid and pu_vendcode=ve_code and pu_id<'"
					+ sqlRowList.getInt("pd_puid")
					+ "' and pd_prodcode='"
					+ sqlRowList.getString("pd_prodcode")
					+ "' and (pu_status='已审核' or pu_status='已收货' or pu_status='部分收货') and nvl(pd_mark,' ')<>'备品' order by pu_id desc ";
			sList = baseDao.queryForRowSet(sql);
			while (sList.next()) {
				if (sql2.equals("")) {
					sql2 = "UPDATE purchaseDetail SET pd_preprice=" + sList.getDouble("pd_oriprice") + ",pd_prevendor='"
							+ sList.getString("ve_shortname") + "',pd_precode='" + sList.getString("pd_code") + "' where pd_id='"
							+ sqlRowList.getString("pd_id") + "' ";
					baseDao.execute(sql2);
				}
			}
		}
	}

	/**
	 * Sale->sale->save 客户PO号在销售订单**中已经存在!
	 */
	public void sale_save_pocode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object code = baseDao.getFieldDataByCondition("Sale", "sa_code",
				"sa_code<>'" + store.get("sa_code") + "' AND sa_pocode='" + store.get("sa_pocode") + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.save_pocodeHasExist1") + code
					+ BaseUtil.getLocalMessage("scm.sale.sale.save_pocodeHasExist2"));
		}
	}

	/**
	 * Sale->sale->save 客户PO号在销售订单**中已经存在!
	 */
	public void prodiout_save_custcode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object code = baseDao.getFieldDataByCondition("prodiodetail", "pd_ordercode", "pd_piid='" + store.get("pi_id") + "'");
		if (code != null && !code.equals("")) {
			String sqlstr = "update prodiodetail set (pd_custprodcode,pd_original,pd_detail)=(select sd_original,sd_custprodcode,sd_detail from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno) where pd_piid='"
					+ store.get("pi_id") + "'";
			baseDao.execute(sqlstr);
		}
	}

	/**
	 * Sale->sale->save 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品!产品编号:
	 */
	public void sale_save_prodcode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code", "pr_code IN (SELECT sd_prodcode FROM saledetail WHERE "
				+ "sd_said=" + store.get("sa_id") + ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.update_prodcode") + sb.toString());
		}
	}

	/**
	 * Sale->sale->delete 已有发货通知单!
	 */
	public void sale_delete_checkSndCode(Integer sa_id) {
		Object code = baseDao.getFieldDataByCondition("Sale", "sa_code", "sa_id=" + sa_id);
		boolean bool = baseDao.checkByCondition("SendNotifyDetail", "snd_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.delete_sndcodeHasExist"));
		}
	}

	/**
	 * Sale->sale->delete 删除销售订单 还原订单评审!
	 */
	public void sale_delete_rePreSale(Integer sa_id) {

		Object ps_id = baseDao.getFieldDataByCondition("PreSale", "ps_id", "ps_source=(select sa_code from sale where sa_id='" + sa_id
				+ "')");
		// 删除saleDetail
		if (ps_id != null && !ps_id.equals("")) {
			baseDao.updateByCondition("PreSale", "ps_status='" + BaseUtil.getLocalMessage("AUDITED") + "',ps_statuscode='AUDITED'",
					"ps_id='" + ps_id + "'");
		}
	}

	/**
	 * saleForecast->sale->delete 删除销售预测还原订单评审!
	 */
	public void saleForecast_delete_rePreSale(Integer sf_id) {

		Object ps_id = baseDao.getFieldDataByCondition("PreSale", "ps_id", "ps_source=(select sf_code from saleForecast where sf_id='"
				+ sf_id + "')");
		if (ps_id != null && !ps_id.equals("")) {
			baseDao.updateByCondition("PreSale", "ps_status='" + BaseUtil.getLocalMessage("AUDITED") + "',ps_statuscode='AUDITED'",
					"ps_id='" + ps_id + "'");
		}

	}

	/**
	 * saleForecast->delete-> 删除销售预测业务员预测!
	 */
	public void saleForecast_delete_rePreSaleForcast(Integer sf_id) {

		List<Object> ids = baseDao.getFieldDatasByCondition("saleForecast left join saleForecastDetail on sf_id=sd_sfid", "sd_id",
				"sf_id='" + sf_id + "'");

		for (Object id : ids) {
			baseDao.updateByCondition("PreSaleForecastDetail", "sd_status='已审核',sd_statuscode='AUDITED',sd_source=null", "sd_sourceid='"
					+ id + "'");
		}
	}

	/**
	 * saleForecast->deleteDetail-> 删除销售预测业务员预测!
	 */
	public void saleForecast_deletede_rePreSaleForcast(Integer sd_id) {
		baseDao.updateByCondition("PreSaleForecastDetail", "sd_status='已审核',sd_statuscode='AUDITED',sd_source=null", "sd_sourceid='"
				+ sd_id + "'");
	}

	/**
	 * Sale->sale->delete 已有发货单!
	 */
	public void sale_delete_checkPdCode(Integer sa_id) {
		Object code = baseDao.getFieldDataByCondition("Sale", "sa_code", "sa_id=" + sa_id);
		boolean bool = baseDao.checkByCondition("ProdIODetail", "pd_piclass='发货单' AND pd_ordercode='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.delete_pdcodeHasExist"));
		}
	}

	/**
	 * Sale->sale->delete 已有制造单!
	 */
	public void sale_delete_checkMaCode(Integer sa_id) {
		Object code = baseDao.getFieldDataByCondition("Sale", "sa_code", "sa_id=" + sa_id);
		boolean bool = baseDao.checkByCondition("Make", "ma_statuscode<>'DELETED' AND ma_salecode='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.delete_macodeHasExist"));
		}
	}

	/**
	 * Sale->sale->commit 销售政策不存在!
	 */
	public void sale_commit_checkSdcode(Integer sa_id) {
		Object no = baseDao.getFieldDataByCondition("SaleDetail", "sd_detno",
				"nvl(sd_spcode,' ')<>' ' AND sd_spcode NOT IN (SELECT sp_code FROM SalePolicy) AND sd_said=" + sa_id);
		if (no != null && !no.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.submit_sdcodeNotExist1") + no
					+ BaseUtil.getLocalMessage("scm.sale.sale.submit_sdcodeNotExist2"));
		}
	}

	/**
	 * 采购验收单提交时,判断数量有没有超发!
	 * 
	 * @param sa_id
	 * @param language
	 * @param employee
	 */
	public void prodinout_commit_checkYqty(Integer sa_id) {
		List<Object> ids = baseDao.getFieldDatasByCondition("ProdIODetail", "pd_id", "pd_piid=" + sa_id);
		Object[] datas;
		Object[] yqty;
		for (Object id : ids) {
			datas = baseDao.getFieldsDataByCondition("ProdIODetail", new String[] { "pd_ordercode", "pd_orderdetno", "pd_inqty" }, "pd_id="
					+ id);
			yqty = baseDao.getFieldsDataByCondition("purchasedetail left join purchase on pd_puid=pu_id", new String[] { "pd_yqty",
					"pd_qty" }, "pu_code='" + datas[0] + "' and pd_detno=" + datas[1]);
			if (Double.valueOf(yqty[0].toString()) + Double.valueOf(datas[2].toString()) > Double.valueOf(yqty[1].toString())) {
				BaseUtil.showError("采购单号为:" + datas[0] + ",订单序号为:" + datas[1] + "的总验收数超过了采购数.采购数:" + yqty[1] + ",已转验收单数:" + yqty[0]);
			}
		}
	}

	/**
	 * sendnotify->sendnotify->commit 出货通知单：提交前，判断需求数量和批号的数量
	 */
	public void sendnotify_commit_checkbatchcode(Integer sn_id) {
		String errBatch = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from sendnotifydetail where snd_snid=? and nvl(snd_batchid, 0)<>0 and nvl(snd_batchid, 0) not in (select ba_id from batch where ba_prodcode=snd_prodcode and ba_whcode=snd_warehousecode)",
						String.class, sn_id);
		if (errBatch != null) {
			BaseUtil.showError("批号无效,提交失败,明细:" + errBatch);
		}
		String notEnoughBatch = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('<br>行:'||snd_pdno||',出货单未过账数:'||nvl(pd_batchqty, 0)||',通知单数:'||nvl(snd_batchqty,0)||',当前库存:'||nvl(ba_remain, 0)) from (select snd_pdno,(select sum(nvl(snd_outqty,0)-nvl(snd_yqty,0)) from sendnotifydetail left join sendnotify on sn_id=snd_snid where snd_batchid=A.snd_batchid and nvl(snd_statuscode,' ')<>'FINISH') snd_batchqty,(select sum(pd_outqty) from ProdIODetail left join ProdInOut on pi_id=pd_piid where pd_batchid=A.snd_batchid and nvl(pi_statuscode,' ')<>'POSTED') pd_batchqty,ba_remain from sendnotifydetail A left join batch on ba_id=snd_batchid where snd_snid=? and snd_batchid<>0) where nvl(snd_batchqty, 0) + nvl(pd_batchqty, 0) > nvl(ba_remain, 0)",
						String.class, sn_id);
		if (notEnoughBatch != null) {
			BaseUtil.showError("数量超过了该批次的剩余数量,提交失败:<br>" + notEnoughBatch);
		}
		String noBatch = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('<br>行:'||snd_pdno||',出货单未过账数:'||nvl(pd_batchqty, 0)||',通知单数:'||nvl(snd_batchqty,0)||',当前库存:'||nvl(ba_remain, 0)) from (select snd_pdno,(select sum(nvl(snd_outqty,0)-nvl(snd_yqty,0)) from sendnotifydetail left join sendnotify on sn_id=snd_snid where snd_prodcode=A.snd_prodcode and snd_warehousecode=A.snd_warehousecode and snd_statuscode<>'FINISH') snd_batchqty,(select sum(pd_outqty) from ProdIODetail left join ProdInOut on pi_id=pd_piid where pd_prodcode=A.snd_prodcode and pd_whcode=A.snd_warehousecode and pi_statuscode<>'POSTED') pd_batchqty,ba_remain from sendnotifydetail A left join batch on ba_prodcode=snd_prodcode and ba_whcode=snd_warehousecode where snd_snid=? and nvl(snd_batchid,0)=0) where nvl(snd_batchqty, 0) + nvl(pd_batchqty, 0) < nvl(ba_remain, 0)",
						String.class, sn_id);
		if (noBatch != null) {
			BaseUtil.showError("提交失败！库存数量充足,却没填写批号:<br>" + noBatch);
		}
		String notEnoughSale = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('<br>行:'||snd_pdno||',订单数:'||nvl(sd_qty,0)||',通知单已提交数:'||nvl(snd_commitedqty,0)||',本次数:'||nvl(snd_outqty,0)) from (select snd_pdno,snd_outqty,(select sum(nvl(snd_outqty,0)) from sendnotifydetail left join sendnotify on sn_id=snd_snid where snd_ordercode=A.snd_ordercode and snd_orderdetno=A.snd_orderdetno and snd_statuscode<>'FINISH' and sn_statuscode<>'ENTERING') snd_commitedqty,nvl(sd_qty,0)+nvl(sd_returnqty,0) sd_qty from sendnotifydetail A left join saledetail on sd_code=snd_ordercode and sd_detno=snd_orderdetno where snd_snid=?) where nvl(sd_qty,0)<nvl(snd_commitedqty,0)+nvl(snd_outqty,0)",
						String.class, sn_id);
		if (notEnoughSale != null) {
			BaseUtil.showError("数量超过了订单数量,提交失败:<br>" + notEnoughSale);
		}
	}

	/**
	 * 判断业务员和客户是否有关联
	 * 
	 * @param sa_id
	 * @param language
	 * @param employee
	 */
	public void sale_commit_checkSalecoder(Integer sa_id) {
		Object[] objects = baseDao.getFieldsDataByCondition("sale", new String[] { "sa_custcode", "sa_sellercode" }, " sa_id=" + sa_id);
		if (objects != null) {
			Object cu_id = baseDao.getFieldDataByCondition("customer", "cu_id", "cu_code='" + objects[0] + "'");
			Object bool = baseDao.getFieldDataByCondition("customer", "cu_id", "cu_code='" + objects[0] + "' and cu_sellercode='"
					+ objects[1] + "'");
			if (bool == null) {
				SqlRowList rs = baseDao.queryForRowSet("select cd_seller from CustomerDistr where cd_sellercode=? and cd_cuid=?",
						objects[1], cu_id);
				if (rs.next()) {

				} else {
					BaseUtil.showError("该单据客户和业务员不是一个分组,提交失败!");
				}
			}
		}
	}

	/**
	 * Sale->salePrice->audit 销售价审核后传到平台! 天派电子专用
	 */
	public void salePrice_audit_updateb2b(Integer sp_id) {
		int nos = baseDao.getCount("select count(*) from saledetail where nvl(spd_arcustcode,' ')=' ' and spd_spid=" + sp_id);
		if (nos == 0) {
			nos = baseDao
					.getCount("select count(*) from salepricedetail left join customer on spd_arcustcode=cu_code where nvl(cu_uu,0)=0 and spd_spid="
							+ sp_id);
			if (nos == 0) {
				baseDao.execute("update saleprice set sp_sendstatus='待上传' where sp_id=" + sp_id);
			} else {

			}
		} else {

		}
	}

	/**
	 * Sale->sale->commit 销售价不能低于定价!善岭专用
	 */
	public void sale_commit_checkSdprice_ZL(Integer sa_id) {
		Object[] nos = baseDao.getFieldsDataByCondition("SaleDetail", new String[] { "sd_detno", "sd_prodcode" },
				"sd_price<nvl(sd_purcprice,0) and nvl(sd_saletype,' ')<>'赠送' AND sd_said=" + sa_id);
		if (nos != null && !nos[0].equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.submit_sdprice1") + nos[0]
					+ BaseUtil.getLocalMessage("scm.sale.sale.submit_sdprice2"));
		}
	}
	
	/**
	 * Sale->sale->commit 销售订单中的类型是否定价为是时，单价不能低于定价!  2018060063  maz
	 */
	public void sale_commit_checkSdprice(Integer sa_id) {
		Object[] nos = baseDao.getFieldsDataByCondition("saledetail left join sale on sd_said=sa_id left join SaleKind on sk_code=sk_name", new String[] { "sd_detno", "sd_prodcode" },
				"sd_price<nvl(sd_purcprice,0) and nvl(sk_issaleprice,0)='-1' AND sd_said=" + sa_id);
		if (nos != null && !nos[0].equals("")) {
			BaseUtil.showError("该订单销售类型是否定价为是,不允许单价低于定价");
		}
	}

	/**
	 * Sale->sale->commit 后 宇声 根据ProductRate 更新比例
	 */
	public void sale_commit_updateDiscount(Integer sa_id) {
		Object[] conf = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_custid", "sa_date" }, "sa_id=" + sa_id);

		List<Object[]> list = baseDao.getFieldsDatasByCondition("SaleDetail left join Product on sd_prodcode=pr_code", new String[] {
				"sd_id", "pr_id", "sd_price" }, "sd_said=" + sa_id);

		for (Object[] o : list) {
			baseDao.updateByCondition(
					"SaleDetail",
					"sd_discount=(select pdrd_rate from (select pdrd_rate from ProductRateDetail left join ProductRate on pdrd_pdrid=pdr_id where pdrd_prodid='"
							+ o[1].toString()
							+ "' and pdr_custid='"
							+ conf[0].toString()
							+ "' and pdrd_startdate - 1 < to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') and pdrd_enddate+1>to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') "
							+ " and pdrd_saleprice="
							+ o[2]
							+ "and pdr_statuscode='AUDITED' and pdrd_statuscode='VALID' order by pdr_auditdate desc,pdrd_id desc) where rownum=1)",
					"sd_id=" + o[0].toString());
		}

	}

	/**
	 * Sale->sale->update 后 宇声 根据ProductRate 更新比例
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 * @author jiang
	 */
	public void sale_update_updateDiscount(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object[] conf = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_custid", "sa_date" }, "sa_id=" + store.get("sa_id"));

		List<Object[]> list = baseDao.getFieldsDatasByCondition("SaleDetail left join Product on sd_prodcode=pr_code", new String[] {
				"sd_id", "pr_id", "sd_discount" }, "sd_said=" + store.get("sa_id"));

		for (Object[] o : list) {
			baseDao.updateByCondition(
					"SaleDetail",
					"sd_discount=(select pdrd_rate from (select pdrd_rate from ProductRateDetail left join ProductRate on pdrd_pdrid=pdr_id where pdrd_prodid='"
							+ o[1].toString()
							+ "' and pdr_custid='"
							+ conf[0].toString()
							+ "' and pdrd_startdate - 1 < to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') and pdrd_enddate+1>to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') "
							+ "and pdr_statuscode='AUDITED' and pdrd_statuscode='VALID' order by pdr_auditdate desc,pdrd_id desc) where rownum=1)",
					"sd_id=" + o[0].toString());

		}
	}

	/**
	 * Sale->sale->update 后 宇声 根据ProductRate 更新比例价格也要考虑
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 * @author shenj
	 */
	public void sale_update_updateDiscountByPrice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object[] conf = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_custid", "sa_date" }, "sa_id=" + store.get("sa_id"));

		List<Object[]> list = baseDao.getFieldsDatasByCondition("SaleDetail left join Product on sd_prodcode=pr_code", new String[] {
				"sd_id", "pr_id", "sd_discount", "sd_price" }, "sd_said=" + store.get("sa_id"));

		for (Object[] o : list) {
			baseDao.updateByCondition(
					"SaleDetail",
					"sd_discount=(select pdrd_rate from (select pdrd_rate from ProductRateDetail left join ProductRate on pdrd_pdrid=pdr_id where pdrd_prodid='"
							+ o[1].toString()
							+ "' and pdr_custid='"
							+ conf[0].toString()
							+ "' and pdrd_startdate - 1 < to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') and pdrd_enddate+1>to_date('"
							+ conf[1].toString()
							+ "','yyyy-mm-dd hh24:mi:ss') "
							+ " and pdrd_saleprice="
							+ o[3]
							+ " and pdr_statuscode='AUDITED' and pdrd_statuscode='VALID' order by pdr_auditdate desc,pdrd_id desc) where rownum=1)",
					"sd_id=" + o[0].toString());

		}
	}

	/**
	 * maz 2017100234 销售变更单审核后也根据费用比例更新销售订单明细比例
	 * 
	 * @param id
	 */
	public void saleChange_update_updateDiscountByPrice(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sa_id from sale a where EXISTS(select 1 from salechange left join salechangedetail on sc_id=scd_scid where scd_sacode=a.sa_code and sc_id="
						+ id + ")");
		while (rs.next()) {
			Object[] conf = baseDao
					.getFieldsDataByCondition("Sale", new String[] { "sa_custid", "sa_date" }, "sa_id=" + rs.getInt("sa_id"));
			List<Object[]> list = baseDao.getFieldsDatasByCondition("SaleDetail left join Product on sd_prodcode=pr_code", new String[] {
					"sd_id", "pr_id", "sd_discount", "sd_price" }, "sd_said=" + rs.getInt("sa_id"));
			for (Object[] o : list) {
				baseDao.updateByCondition(
						"SaleDetail",
						"sd_discount=(select pdrd_rate from (select pdrd_rate from ProductRateDetail left join ProductRate on pdrd_pdrid=pdr_id where pdrd_prodid='"
								+ o[1].toString()
								+ "' and pdr_custid='"
								+ conf[0].toString()
								+ "' and pdrd_startdate - 1 < to_date('"
								+ conf[1].toString()
								+ "','yyyy-mm-dd hh24:mi:ss') and pdrd_enddate+1>to_date('"
								+ conf[1].toString()
								+ "','yyyy-mm-dd hh24:mi:ss') "
								+ " and pdrd_saleprice="
								+ o[3]
								+ " and pdr_statuscode='AUDITED' and pdrd_statuscode='VALID' order by pdr_auditdate desc,pdrd_id desc) where rownum=1)",
						"sd_id=" + o[0].toString());
			}
		}
	}

	public void sale_commit_taxratecheck(Integer id) {
		baseDao.execute("UPDATE SALEDETAIL SET SD_TAXRATE=0 WHERE SD_SAID IN (SELECT SA_ID FROM SALE WHERE SA_CURRENCY<>'RMB' and sa_id="
				+ id + ")");
	}

	/**
	 * reserve->prodinout->commit 采购验退单：单价为0,不能提交和审核!
	 */
	public void ProdInOut_commit_checkpdprice(Integer pi_id) {
		Object[] objects = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_orderprice", "pd_piid=" + pi_id
				+ " and nvl(pd_orderprice,0)=0");
		if (objects != null) {
			BaseUtil.showError("该单据明细行有采购单价为0的情况，不允许进行当前操作!");
		}
	}

	/**
	 * Sale->sale->commit 更新库存数,可发货数!sd_canuseqty,sd_cansendqty
	 */
	public void sale_commit_updateCanuseQty(Integer sa_id) {
		List<Object[]> nos = baseDao.getFieldsDatasByCondition("SaleDetail", new String[] { "sd_id", "sd_prodcode" }, "sd_said=" + sa_id);
		Object pw_onhand = null;
		Object sa_qty = null;
		Object ma_qty = null;
		Object pd_qty = null;
		for (Object[] object : nos) {
			pw_onhand = baseDao.getFieldDataByCondition("productWH", "pw_onhand", "pw_prodcode='" + object[1] + "'");
			sa_qty = baseDao.getFieldDataByCondition("sale left join SaleDetail on sd_said=sa_id", "sum(nvl(sd_qty,0)-nvl(sd_yqty,0))",
					"sa_statuscode='COMMITED' or sa_statuscode='AUDITED' and sd_prodcode='" + object[1] + "'");
			ma_qty = baseDao.getFieldDataByCondition("make", "sum(nvl(ma_qty,0)-nvl(ma_madeqty,0))",
					"ma_statuscode='AUDITED' and nvl(ma_qty,0)-nvl(ma_madeqty,0)>0 and ma_prodcode='" + object[1] + "'");
			pd_qty = baseDao.getFieldDataByCondition("purchasedetail left join purchase on pu_id=pd_puid",
					"sum(nvl(pd_qty,0)-nvl(pd_acceptqty,0))", "pu_statuscode='AUDITED' and pd_prodcode='" + object[1] + "'");
			sa_qty = sa_qty == null ? 0 : sa_qty;
			ma_qty = ma_qty == null ? 0 : ma_qty;
			pd_qty = pd_qty == null ? 0 : pd_qty;
			baseDao.updateByCondition(
					"SaleDetail",
					"sd_canuseqty="
							+ (Integer.valueOf(pw_onhand.toString()) - Integer.valueOf(sa_qty.toString())
									+ Integer.valueOf(ma_qty.toString()) + Integer.valueOf(pd_qty.toString())), "sd_id=" + object[0]);
			baseDao.updateByCondition("SaleDetail",
					"sd_cansendqty=" + (Integer.valueOf(pw_onhand.toString()) - Integer.valueOf(sa_qty.toString())), "sd_id=" + object[0]);
		}
	}

	/**
	 * Sale->sale->commit 订单数大于预测单数量，不能提交!
	 */
	public void sale_commit_checkSdqty(Integer sa_id) {
		Object no = saleDao.checkQty(sa_id);
		if (no != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.submit_sdqty1") + no
					+ BaseUtil.getLocalMessage("scm.sale.sale.submit_sdqty2"));
		}
	}

	/**
	 * Sale->sale->commit 销售订单：客户已挂起，不允许进行当前操作
	 */
	public void sale_commit_custatus(Integer sa_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sa_custcode) from sale left join customer on sa_custcode=cu_code where sa_id = ? and cu_status='挂起'",
				String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作！客户号：" + dets);
		}
		/**
		 * 客户挂起 提交的时候限制，通过审批流程过来的不用限制
		 * */
		if (!baseDao.checkIf("JPROCESS", "JP_CALLER in ('Sale','Sale!PLM') and jp_keyValue=" + sa_id)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sa_apcustcode) from sale left join customer on sa_apcustcode=cu_code where sa_id = ? and cu_status='挂起'",
							String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("应收客户资料状态为挂起，不允许进行当前操作！客户号：" + dets);
			}
		}
	}

	/**
	 * Sale->SendNotify->print 出货通知单：客户已挂起，不允许进行当前操作
	 */
	public void sendnotify_print_custatus(Integer sn_id) {
		if (!baseDao.checkIf("JPROCESS", "JP_CALLER='SendNotify' and jp_keyValue=" + sn_id)) {
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sn_custcode) from sendnotify left join customer on sn_custcode=cu_code where sn_id = ? and cu_status='挂起'",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作!客户号：" + err);
			}
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sn_arcustcode) from sendnotify left join customer on sn_arcustcode=cu_code where sn_id = ? and cu_status='挂起'",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("应收客户资料状态为挂起，不允许进行当前操作!客户号：" + err);
			}
		}
	}

	/**
	 * Sale->sale->save 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品,不能提交!产品编号:
	 */
	public void sale_commit_prodcode(Integer sa_id) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code", "pr_code IN (SELECT sd_prodcode FROM saledetail WHERE "
				+ "sd_said=" + sa_id + ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('caller=Product&pr_code=" + c + "')\">" + c + "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.update_prodcode") + sb.toString());
		}
	}

	/**
	 * Reserve->ProdInOut->save 拨出拨入单不能对废品仓操作!
	 */
	public void prodInOut_save_whcode1(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		boolean bool = baseDao.checkByCondition("WareHouse",
				"(wh_code='" + store.get("pi_whcode") + "' OR wh_code='" + store.get("pi_purpose") + "') AND wh_type='废品仓'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.save_whcode1"));
		}
	}

	/**
	 * Reserve->ProdInOut->save 拨出拨入单不能对客来仓操作!
	 */
	public void prodInOut_save_whcode2(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		boolean bool = baseDao.checkByCondition("WareHouse",
				"(wh_code='" + store.get("pi_whcode") + "' OR wh_code='" + store.get("pi_purpose") + "') AND wh_type='客来仓'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.save_whcode2"));
		}
	}

	/**
	 * Reserve->ProdInOut->save 部门编号不能为空
	 */
	public void prodInOut_save_wccode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object[] objs = baseDao.getFieldsDataByCondition("WorkCenter,Make,ProdioDetail", new String[] { "wc_department",
				"wc_departmentname" }, "ma_code=pd_ordercode AND ma_factory=wc_code AND pd_piid=" + store.get("pi_id"));
		Object wc_department = objs[0], wc_departmentname = objs[1];
		if (wc_department != null || wc_departmentname != null) {
			baseDao.updateByCondition("ProdInOut", "pi_departmentcode='" + wc_department + "',pi_departmentname='" + wc_departmentname
					+ "'", "pi_id=" + store.get("pi_id"));
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.save_department"));
		}
	}

	/**
	 * Reserve->ProdInOut->delete 只能删除[未过账]的单据
	 */
	public void prodInOut_delete_unpost(Integer pi_id) {
		Object status = baseDao.getFieldDataByCondition("ProdInOut", "pi_statuscode", "pi_id=" + pi_id);
		if (!status.equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.delete_onlyUnPost"));
		}
	}

	/**
	 * Reserve->ProdInOut->delete(生产领料单) 完工入库自动产生的领料单不能删除!
	 */
	public void prodInOut_delete_relativeplace(Integer pi_id) {
		boolean bool = baseDao.checkByCondition("ProdInOut", "pi_relativeplace<>' ' AND pi_id=" + pi_id);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.delete_relativeplace"));
		}
	}

	/**
	 * Reserve->ProdInOut->print(结余退料单/生产耗料单) 车间或线别不能为空
	 */
	public void prodInOut_print_sellercode(Integer pi_id) {
		boolean bool = baseDao.checkByCondition("ProdIODetail", "(nvl(pd_wccode,' ')=' ' OR nvl(pd_sellercode,' ')=' ') AND pd_piid="
				+ pi_id);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_sellercode"));
		}
	}

	/**
	 * Reserve->ProdInOut->print(发货单) 出入库单：客户已挂起，不允许进行当前操作
	 */
	public void prodInOut_print_custatus(Integer pi_id) {
		Object pi_cardcode = baseDao.getFieldDataByCondition("ProdInOut", "pi_cardcode", "pi_id=" + pi_id);
		Object status = baseDao.getFieldDataByCondition("Customer", "cu_statuscode", "cu_code='" + pi_cardcode + "'");
		if (status != null && status.equals("HUNG")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_custatus"));
		}
	}

	/**
	 * Reserve->ProdInOut->print(生产补料单) 补料数量不能大于报废数
	 */
	public void prodInOut_print_outqty(Integer pi_id) {
		String check = prodInOutDao.checkOutqty(pi_id);
		if (check != null) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> map : BaseUtil.parseGridStoreToMaps(check)) {
				sb.append(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_pdno"));
				sb.append(map.get("pd_pdno"));
				sb.append(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_scrapqty"));
				sb.append(map.get("mm_scrapqty"));
				sb.append(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_addqty"));
				sb.append(map.get("mm_addqty"));
				sb.append(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_outqty"));
				sb.append(map.get("pd_outqty"));
				sb.append("]!<br/>");
			}
			BaseUtil.showError(sb.toString());
		}
	}

	/**
	 * Reserve->ProdInOut->print(生产退料单/拆件入库单) 入仓原因必须一致
	 */
	public void prodInOut_print_description(Integer pi_id) {
		List<Object> pd_description = baseDao.getFieldDatasByCondition("ProdIoDetail", "distinct pd_description", "pd_piid=" + pi_id);
		if (pd_description.size() > 1) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_description"));
		}
	}

	/**
	 * Reserve->ProdInOut->print(生产退料单/拆件入库单) 责任部门和入仓原因必须填写
	 */
	public void prodInOut_print_depAndDesc(Integer pi_id) {
		boolean bool = baseDao.checkByCondition("ProdIoDetail",
				"(nvl(pd_description,' ')=' ' OR nvl(pd_departmentcode,' ')=' ' ) AND pd_piid=" + pi_id);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_depAndDesc"));
		}
	}

	/**
	 * Purchase->Inquiry 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品,不能提交!产品编号:
	 */
	public void Inquiry_commit_prodcode(Integer in_id) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code",
				"pr_code IN (SELECT id_prodcode FROM inquirydetail WHERE " + "id_inid=" + in_id
						+ ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('caller=Product&pr_code=" + c + "')\">" + c + "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.update_prodcode") + sb.toString());
		}
	}

	/**
	 * Purchase->Inquiry 资料有[未审核]、[已禁用]、[已删除]或不存在的供应商,不能提交!供应商编号:
	 */
	public void Inquiry_commit_vendcurrency(Integer in_id) {
		List<Object[]> codes = baseDao.getFieldsDatasByCondition("inquirydetail", new String[] { "id_vendcode", "id_currency", "id_rate" },
				"id_inid=" + in_id);
		for (Object[] os : codes) {
			String str = "select count(*) from vendor where ve_code='" + os[0] + "' and ve_currency='" + os[1] + "' and ve_taxrate='"
					+ os[2] + "'";
			int countnum = baseDao.getCount(str);
			if (countnum == 0) {
				BaseUtil.showError("明细供应商编号为" + os[0] + "的供应商与供应商资料里面的币别税率不一致不能提交!");
			}
		}
	}

	/**
	 * Purchase->Inquiry 易方数码专用，询价明细发出去前有效日期段的判断:
	 */
	public void Inquiry_audit_from_todate(Integer in_id) {
		List<Object[]> codes = baseDao.getFieldsDatasByCondition("inquirydetail", new String[] { "id_fromdate", "id_todate", "id_detno" },
				"id_inid=" + in_id);
		Object obj = baseDao.getFieldDataByCondition("inquiry", "in_prodtype", "in_id=" + in_id);
		for (Object[] os : codes) {
			int i = 0, s = 0;
			try {
				i = DateUtil.compare(os[0].toString(), os[1].toString());
				s = DateUtil.countDates(os[0].toString(), os[1].toString());
			} catch (Exception e) {

			}
			if (obj.equals("结构与包材")) {
				if (i == 1 || s > 185) {
					BaseUtil.showError("明细序号" + os[2] + "的有效起始日期不对或者间隔超过185天!");
				}
				;
			} else {
				if (i == 1 || s > 95) {
					BaseUtil.showError("明细序号" + os[2] + "的有效起始日期不对或者间隔超过95天!");
				}
				;
			}
		}
	}

	/**
	 * Purchase->Inquiry 询价明细发出去前有效日期段的判断:
	 */
	public void Inquiry_auditsave_todate(Integer in_id) {
		// 天派电子审核之后更新
		baseDao.execute("update inquirydetail set id_mytodate=to_date('2099-12-21','yyyy-mm-dd') where id_inid=" + in_id);

	}

	/**
	 * Purchase->PurchasePrice 易方数码专用，物料核价单审核前有效日期段的判断:
	 */
	public void Purchasprice_commit_from_todate(Integer in_id) {
		List<Object[]> codes = baseDao.getFieldsDatasByCondition("PurchasePricedetail", new String[] { "ppd_fromdate", "ppd_todate",
				"ppd_detno" }, "ppd_ppid=" + in_id);
		Object obj = baseDao.getFieldDataByCondition("PurchasePrice", "pp_prodtype", "pp_id=" + in_id);
		for (Object[] os : codes) {
			int i = 0, s = 0;
			try {
				i = DateUtil.compare(os[0].toString(), os[1].toString());
				s = DateUtil.countDates(os[0].toString(), os[1].toString());
			} catch (Exception e) {

			}
			if (obj.equals("结构与包材")) {
				if (i == 1 || s > 185) {
					BaseUtil.showError("明细序号" + os[2] + "的有效起始日期不对或者间隔超过185天!");
				}
				;
			} else {
				if (i == 1 || s > 95) {
					BaseUtil.showError("明细序号" + os[2] + "的有效起始日期不对或者间隔超过95天!");
				}
				;
			}
		}
	}

	/**
	 * ProcessTransfer_disable_check 国扬专用，流程转移设置禁用前的判断:
	 */
	public void ProcessTransfer_disable_check(Integer jt_id) {
		if ("admin".equals(SystemSession.getUser().getEm_type())) {

		} else {
			Object emcode = baseDao.getFieldDatasByCondition("JprocessTransfer", "jt_recorder", "jt_id=" + jt_id);
			if (emcode.equals(SystemSession.getUser().getEm_name())) {

			} else {
				BaseUtil.showError("只能禁用或者反禁用自己录的流程转移！");
			}
		}
	}

	/**
	 * Purchase->Inquiry 资料有[未审核]、[已禁用]、[已删除]或不存在的供应商,不能提交!供应商编号:
	 */
	public void Inquiry_commit_vendcode(Integer in_id) {
		Object code = baseDao.getFieldDataByCondition("Vendor", "ve_code", "ve_code IN (SELECT in_vendcode FROM inquiry WHERE " + "in_id="
				+ in_id + ") AND ve_auditstatuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (code != null && !code.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href=\"javascript:openUrl('caller=Vendor&ve_code=" + code + "')\">" + code + "</a>&nbsp;");
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.update_vendcode") + sb.toString());
		}
	}

	/**
	 * Purchase->Quotation 资料有[未审核]、[已禁用]、[已删除]或不存在的客户,不能提交!客户编号:
	 */
	public void Quotation_commit_vendcode(Integer qu_id) {
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_code IN (SELECT qu_custcode FROM quotation WHERE "
				+ "qu_id=" + qu_id + ") AND cu_auditstatuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (code != null && !code.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href=\"javascript:openUrl('caller=Customer&cu_code=" + code + "')\">" + code + "</a>&nbsp;");
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.quotation.update_custcode") + sb.toString());
		}
	}

	/**
	 * Purchase->Quotation 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品,不能提交!产品编号:
	 */
	public void Quotation_commit_prodcode(Integer qu_id) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code",
				"pr_code IN (SELECT qd_prodcode FROM quotationdetail WHERE " + "qd_quid=" + qu_id
						+ ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('caller=Product&pr_code=" + c + "')\">" + c + "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.quotation.update_prodcode") + sb.toString());
		}
	}

	/**
	 * Purchase->Quotation 资料有不存在的业务员,不能提交!业务员id:
	 */
	public void Quotation_commit_employeeid(Integer qu_id) {
		Object code = baseDao.getFieldDataByCondition("Employee", "em_id", "em_id IN (SELECT qu_sellerid FROM quotation WHERE " + "qu_id="
				+ qu_id + ") AND cu_auditstatuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (code != null && !code.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href=\"javascript:openUrl('caller=Employee&em_id=" + code + "')\">" + code + "</a>&nbsp;");
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.quotation.update_employeeid") + sb.toString());
		}
	}

	/**
	 * Purchase->Quotation 资料有不存在的币别,不能提交!币别:
	 */
	public void Quotation_commit_currency(Integer qu_id) {
		Object code = baseDao.getFieldDataByCondition("Currencys", "cr_name", "cr_name IN (SELECT qu_currency FROM quotation WHERE "
				+ "qu_id=" + qu_id + ") AND cu_auditstatuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (code != null && !code.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href=\"javascript:openUrl('caller=Currencys&cr_name=" + code + "')\">" + code + "</a>&nbsp;");
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.quotation.update_currency") + sb.toString());
		}
	}

	/**
	 * Purchase->Quotation 资料有不存在的付款方式,不能提交!付款方式id:
	 */
	public void Quotation_commit_paymentsid(Integer qu_id) {
		Object code = baseDao.getFieldDataByCondition("Payments", "pa_id", "pa_id IN (SELECT qu_payments FROM quotation WHERE " + "qu_id="
				+ qu_id + ") AND cu_auditstatuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (code != null && !code.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href=\"javascript:openUrl('caller=Payments!Purchase&cr_name=" + code + "')\">" + code + "</a>&nbsp;");
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.quotation.update_paymentsid") + sb.toString());
		}
	}

	/**
	 * 完工入库单、采购验收单、委外验收单：删除前，还原检验单数据 reserve->prodInOut->CheckIn->delete->befor
	 */
	public void prodInOut_checkin_delete_qty(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_id from prodiodetail where pd_piid=?", id);
		while (rs.next()) {
			prodInOut_checkin_deletedetail(rs.getInt(1));
		}
	}

	/**
	 * reserve->prodInOut->NoGoodIn->delete->befor 不良品入库单删除后，还原检验单数据
	 */
	public void prodInOut_nogood_delete_qty(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_id from prodiodetail where pd_piid=?", id);
		while (rs.next()) {
			prodInOut_nogood_deletedetail(rs.getInt(1));
		}
	}

	/**
	 * reserve->prodInOut->print_checkbatchcode 出入库单：批号为空，不允许进行当前操作
	 */
	public void prodInOut_print_checkbatchcode(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from prodiodetail where pd_piid=?", id);
		while (rs.next()) {
			if (rs.getObject("pd_batchcode") == null || rs.getString("pd_batchcode").equals("")) {
				BaseUtil.showError("该单据明细行第" + rs.getObject("pd_pdno") + "行的批号为空，不允许进行当前操作！");
			}
		}
	}

	/**
	 * reserve->prodInOut->CheckIn->deletedetail->before
	 * 完工入库单、采购验收单、委外验收单：明细删除前，还原检验单数据
	 * 
	 * @author madan
	 */
	public void prodInOut_checkin_deletedetail(Integer id) {
		Object obj = baseDao.getFieldDataByCondition("ProdIoDetail", "pd_qcid", "pd_id=" + id + " and nvl(pd_qcid, 0)<>0");
		if (obj != null) {
			Object[] veid = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet left join QUA_VerifyApplyDetail on ved_veid=ve_id",
					new String[] { "ved_veid", "nvl(ved_okqty,0)", "nvl(ved_ngqty,0)", "nvl(ve_criqty,0)" }, "ved_id=" + obj);
			if (veid != null) {
				if (Integer.parseInt(veid[3].toString()) == 0) {
					baseDao.getJdbcTemplate()
							.execute(
									"update QUA_VerifyApplyDetailDet set ved_isok=0,(ved_status,ved_statuscode)=(select ve_status,ve_statuscode from QUA_VerifyApplyDetail where ve_id=ved_veid) where ved_id="
											+ obj);
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=ve_ingoodqty-" + veid[1], "ve_id=" + veid[0]);
				} else {
					baseDao.getJdbcTemplate()
							.execute(
									"update QUA_VerifyApplyDetailDet set ved_isng=0,(ved_status,ved_statuscode)=(select ve_status,ve_statuscode from QUA_VerifyApplyDetail where ve_id=ved_veid) where ved_id="
											+ obj);
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=ve_inbadqty-" + veid[2], "ve_id=" + veid[0]);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->NoGoodIn->deletedetail->before 不良品入库单明细删除后，还原检验单数据
	 * 
	 * @author madan
	 */
	public void prodInOut_nogood_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_qcid", "pd_inqty" }, "pd_id=" + id
				+ " and nvl(pd_qcid, 0)<>0");
		int vedid = Integer.parseInt(objs[0].toString());
		Object[] veid = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "ved_veid", "ved_ngqty" }, "ved_id="
				+ vedid);
		Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_statuscode", "ve_status" }, "ve_id="
				+ veid[0]);
		if (objs != null) {
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_statuscode='" + status[0] + "',ved_status='" + status[1]
					+ "',ved_isng=0", "ved_id=" + vedid);
			baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=ve_inbadqty-" + Integer.parseInt(veid[1].toString()), "ve_id="
					+ veid[0]);
		}
	}

	/**
	 * reserve->prodInOut->CheckIn->delete->before
	 * 出货单，其它出库单，换货出库单：删除前，还原出货通知单以及销售订单的数据
	 */
	public void prodInOut_sale_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_outqty,0)",
				"nvl(pd_snid,0)", "nvl(pd_beipinoutqty,0)", "nvl(pd_sdid,0)", "pd_ordercode", "nvl(pd_noticeid,0)" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
				if (obj[2] != null && Integer.parseInt(String.valueOf(obj[2])) > 0) {// 通知单
					baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=nvl(snd_yqty,0)-" + Double.parseDouble(String.valueOf(obj[1])),
							"snd_id=" + obj[0]);
					baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=0", "nvl(snd_yqty,0)<=0 and snd_id=" + obj[0]);
					int total = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + obj[2]);
					int aud = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + obj[2] + " AND nvl(snd_yqty,0)=0");
					int turn = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + obj[2]
							+ " AND nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>0");
					String status = "PARTOUT";
					if (aud == total) {
						status = "";
					} else if (turn == total) {
						status = "TURNOUT";
					}
					baseDao.updateByCondition("SendNotify",
							"SN_SENDSTATUSCODE='" + status + "',SN_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'", "sn_id="
									+ obj[2]);
				} else {// 销售订单
					baseDao.updateByCondition("SaleDetail", "sd_yqty=nvl(sd_yqty,0)-" + obj[1], "sd_id=" + obj[4]);
					baseDao.updateByCondition("SaleDetail", "sd_yqty=0", "nvl(sd_yqty,0)<=0 and sd_id=" + obj[4]);
					// 去掉更新出货单出库状态代码
					// 客户送货提醒直接转发货单
					if (Integer.parseInt(String.valueOf(obj[6])) != 0) {
						baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=greatest((nvl(sn_yqty,0)-" + obj[1] + "),0)", "sn_id="
								+ obj[6]);
					}
				}
			}
		}
	}

	/**
	 * 出货单，其它出库单，换货出库单：明细删除后，还原出货通知单以及销售订单的数据
	 * reserve->prodInOut->sale->deletedetail->before
	 */
	public void prodInOut_sale_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_outqty,0)",
				"nvl(pd_snid,0)", "nvl(pd_beipinoutqty,0)", "nvl(pd_sdid,0)", "pd_ordercode", "nvl(pd_noticeid,0)" }, "pd_id=" + id);
		if (objs != null && objs[0] != null) {
			if (objs[2] != null && Integer.parseInt(objs[2].toString()) > 0) {// 通知单
				baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=nvl(snd_yqty,0)-" + objs[1], "snd_id=" + objs[0]);
				baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=0", "nvl(snd_yqty,0)<=0 and snd_id=" + objs[0]);
				int total = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + objs[2]);
				int aud = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + objs[2] + " AND nvl(snd_yqty,0)=0");
				int turn = baseDao.getCountByCondition("SendNotifyDetail", "snd_snid=" + objs[2]
						+ " AND nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>0");
				String status = "PARTOUT";
				if (aud == total) {
					status = "";
				} else if (turn == total) {
					status = "TURNOUT";
				}
				baseDao.updateByCondition("SendNotify",
						"SN_SENDSTATUSCODE='" + status + "',SN_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'", "sn_id=" + objs[2]);
			} else {// 订单
				baseDao.updateByCondition("SaleDetail", "sd_yqty=nvl(sd_yqty,0)-" + objs[1], "sd_id=" + objs[4]);
				baseDao.updateByCondition("SaleDetail", "sd_yqty=0", "nvl(sd_yqty,0)<=0 and sd_id=" + objs[4]);
				// 客户送货提醒直接转发货单
				if (Integer.parseInt(String.valueOf(objs[6])) != 0) {
					baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=greatest((nvl(sn_yqty,0)-" + objs[1] + "),0)", "sn_id=" + objs[6]);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->deletedetail->after 出入库单明细删除后，更新主表总金额
	 */
	public void prodInOut_deletedetail_after(Integer id) {
		Object[] pi = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_piid", "pd_piclass" }, "pd_id=" + id);
		if (pi != null) {
			if ("采购验收单".equals(pi[1]) || "采购验退单".equals(pi[1]) || "委外验收单".equals(pi[1]) || "委外验退单".equals(pi[1])) {
				baseDao.updateByCondition("ProdInOut",
						"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
								+ pi[0] + ")", "pi_id=" + pi[0]);
			} else if ("出货单".equals(pi[1]) || "销售退货单".equals(pi[1])) {
				baseDao.updateByCondition("ProdInOut",
						"pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
								+ pi[0] + ")", "pi_id=" + pi[0]);

			} else {
				baseDao.updateByCondition("ProdInOut",
						"pi_total=(SELECT round(sum(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
								+ pi[0] + ")", "pi_id=" + pi[0]);
			}
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi[0]);
		}
	}

	/**
	 * 出货单，其它出库单，换货出库单：明细数量修改前，更新通知单已转出货单数量
	 * reserve->prodInOut->sale->save->before
	 */
	static final String CHECK_NOTICEYQTY = "SELECT sn_qty FROM SaleNotifyDown WHERE sn_id=? and sn_qty<?";

	public void prodInOut_sale_savedetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			Object pdid = s.get("pd_id");
			Object orderid = s.get("pd_orderid");
			Object sdid = s.get("pd_sdid");
			Object snid = s.get("pd_snid");
			Object noticeid = s.get("pd_noticeid");
			Double tQty = Double.parseDouble(s.get("pd_outqty").toString());
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				s.put("pd_inoutno", store.get("pi_inoutno").toString());
				s.put("pd_auditstatus", "ENTERING");
				s.put("pd_accountstatuscode", "UNACCOUNT");
				s.put("pd_accountstatus", BaseUtil.getLocalMessage("UNACCOUNT"));
				// 0表示未过账;99表示已过帐
				s.put("pd_status", 0);
				if (snid != null && !"0".equals(snid)) {
					if (orderid == null || orderid.equals("") || orderid.equals("0")) {
						BaseUtil.showError("没有关联相关通知单明细,拆分行请复制相关明细行号!");
					} else {
						if (StringUtil.hasText(s.get("pd_ordercode"))) {
							sdid = baseDao.getFieldDataByCondition("saledetail left join sale on sa_id=sd_said", "sd_id",
									"sa_code='" + s.get("pd_ordercode") + "' and sd_detno=" + s.get("pd_orderdetno"));
							s.put("pd_sdid", sdid);
						}
						prodInOutDao.restoreSNYqty(tQty, orderid);
					}
				} else {
					if (orderid == null || orderid.equals("") || orderid.equals("0")) {
						if (StringUtil.hasText(s.get("pd_ordercode"))) {
							orderid = baseDao.getFieldDataByCondition("saledetail left join sale on sa_id=sd_said", "sd_id", "sa_code='"
									+ s.get("pd_ordercode") + "' and sd_detno=" + s.get("pd_orderdetno"));
							s.put("pd_sdid", orderid);
							s.put("pd_orderid", orderid);
						}
					}
					if (StringUtil.hasText(s.get("pd_ordercode"))) {
						prodInOutDao.restoreSaleYqty(tQty, s.get("pd_ordercode").toString(),
								Integer.valueOf(s.get("pd_orderdetno").toString()));
					}
				}
				baseDao.execute(SqlUtil.getInsertSql(s, "ProdIODetail", "pd_id"));
				// 客户送货提醒单直接转出货单
				if (noticeid != null && Integer.parseInt(noticeid.toString()) != 0) {
					Double yqty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(nvl(pd_outqty,0)),0)",
							"pd_noticeid=" + s.get("pd_noticeid") + " AND pd_id <>" + pdid + " AND pd_piclass='出货单'", Double.class);
					rs = baseDao.queryForRowSet(CHECK_NOTICEYQTY, s.get("pd_noticeid"), yqty + tQty);
					if (rs.next()) {
						BaseUtil.showError("行 " + s.get("pd_pdno") + " 填写的数量超出客户送货提醒需求数！<br>需求数：" + rs.getObject("sn_qty") + " < 已转发货数："
								+ yqty + " + 本次填写：" + tQty);
					} else {
						baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=" + (yqty + tQty), "sn_id=" + s.get("pd_noticeid"));
					}
				}
			} else {
				if (snid != null && !"0".equals(snid)) {
					// 通知单
					prodInOutDao.restoreSNWithQty(Integer.parseInt(pdid.toString()), tQty, orderid, s.get("pd_ordercode"));
				} else {
					if (s.get("pd_ordercode") != null) {
						// 销售单
						prodInOutDao.restoreSaleWithQty(Integer.parseInt(pdid.toString()), tQty, s.get("pd_ordercode"),
								s.get("pd_orderdetno"));
					}
				}
				if (noticeid != null && Integer.parseInt(noticeid.toString()) != 0) {
					Double yqty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(nvl(pd_outqty,0)),0)", "pd_noticeid=" + noticeid
							+ " AND pd_id <>" + pdid + " AND pd_piclass='出货单'", Double.class);
					rs = baseDao.queryForRowSet(CHECK_NOTICEYQTY, noticeid, yqty + tQty);
					if (rs.next()) {
						BaseUtil.showError("行 " + s.get("pd_pdno") + " 填写的数量超出客户送货提醒需求数！<br>需求数：" + rs.getObject("sn_qty") + " < 已转发货数："
								+ yqty + " + 本次填写：" + tQty);
					} else {
						baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=" + (yqty + tQty), "sn_id=" + noticeid);
					}
				}
			}
		}
	}

	/*
	 * static final String CHECK_SAYQTY =
	 * "SELECT sd_code,sd_detno,sd_qty FROM SaleDetail WHERE sd_id=? and sd_qty<?"
	 * ; static final String CHECK_YQTY =
	 * "SELECT snd_code,snd_pdno,snd_outqty FROM SendNotifyDetail WHERE snd_id=? and snd_outqty<?"
	 * ; static final String CHECK_NOTICEYQTY =
	 * "SELECT sn_qty FROM SaleNotifyDown WHERE sn_id=? and sn_qty<?";
	 * 
	 * public void prodInOut_sale_savedetail(HashMap<Object, Object> store,
	 * ArrayList<Map<Object, Object>> gstore) { Object pdid = null; Object qty =
	 * null; Integer snd_id = null; Integer sd_id = null; double tQty = 0;
	 * Object r = 0; Object bpr = 0; Object[] sas = null; Object[] sns = null;
	 * SqlRowList rs = null; for (Map<Object, Object> s : gstore) { pdid =
	 * s.get("pd_id"); tQty =
	 * Double.parseDouble(String.valueOf(s.get("pd_outqty"))); Object[] objs =
	 * baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] {
	 * "pd_sdid", "pd_outqty", "pd_orderid", "pd_snid", "pd_beipinoutqty",
	 * "pd_ordercode", "pd_noticeid" }, "pd_id=" + pdid); if (objs != null) { if
	 * (objs[3] != null && !String.valueOf(objs[3]).equals("0")) {// 走通知单 snd_id
	 * = Integer.parseInt(String.valueOf(objs[2])); if (snd_id != null && snd_id
	 * > 0) { sns = baseDao .getFieldsDataByCondition(
	 * "SendNotifyDetail left join SendNotify on snd_snid=sn_id",
	 * "sn_code,snd_pdno,snd_ordercode,snd_orderdetno", "snd_id=" + snd_id); qty
	 * = baseDao.getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)",
	 * "pd_orderid=" + snd_id + " AND pd_id <>" + pdid +
	 * " AND pd_piclass='出货单'"); if (sns[2] == null ||
	 * sns[2].toString().trim().length() == 0) { r = baseDao
	 * .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_orderid=" +
	 * snd_id); } else { r = baseDao .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" +
	 * sns[2] + "' and pd_orderdetno=" + sns[3]); } qty = qty == null ? 0 : qty;
	 * r = r == null ? 0 : r; bpr = bpr == null ? 0 : bpr; rs =
	 * baseDao.queryForRowSet(CHECK_YQTY, snd_id,
	 * Double.parseDouble(qty.toString()) + tQty -
	 * Double.parseDouble(r.toString())); if (rs.next()) { StringBuffer sb = new
	 * StringBuffer( "[本次数量填写超出可转数量],出货通知单号:") .append(rs.getString("snd_code"))
	 * .append(",行号:") .append(rs.getInt("snd_pdno")) .append(",通知单数:")
	 * .append(rs.getDouble("snd_outqty")) .append(",已转 出货单数:").append(qty)
	 * .append(",销售退货单数:").append(r) .append(",本次数:").append(tQty);
	 * BaseUtil.showError(sb.toString()); } // 通知单 baseDao.updateByCondition(
	 * "SendNotifyDetail", "snd_yqty=" + (Double.parseDouble(String
	 * .valueOf(qty)) + tQty), "snd_id=" + snd_id);
	 * baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=0",
	 * "snd_yqty<=0 and snd_id=" + snd_id); int count =
	 * baseDao.getCountByCondition( "SendNotifyDetail", "snd_snid=" + objs[3]);
	 * int yCount = baseDao .getCountByCondition( "SendNotifyDetail",
	 * "snd_snid=" + objs[3] +
	 * " and nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>0"); int
	 * xCount = baseDao.getCountByCondition( "SendNotifyDetail", "snd_snid=" +
	 * objs[3] + " and nvl(snd_yqty,0)=0"); String status = "PARTOUT"; if
	 * (yCount == count) { status = "TURNOUT"; } if (xCount == count) { status =
	 * ""; } baseDao.updateByCondition( "SendNotify", "SN_SENDSTATUSCODE='" +
	 * status + "',SN_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'",
	 * "sn_id=" + objs[3]); if (objs[5] != null && !"".equals(objs[5])) {
	 * baseDao.updateByCondition( "Sale", "sa_sendstatuscode='" + status +
	 * "',sa_sendstatus='" + BaseUtil.getLocalMessage(status) + "'", "sa_code='"
	 * + objs[5] + "'"); } } } else {// 直接走出货单 sd_id =
	 * Integer.parseInt(String.valueOf(objs[0])); sas =
	 * baseDao.getFieldsDataByCondition(
	 * "SaleDetail left join Sale on sd_said=sa_id", "sa_code,sd_detno,sa_id",
	 * "sd_id=" + sd_id); qty = baseDao.getFieldDataByCondition("ProdIODetail",
	 * "nvl(sum(nvl(pd_outqty,0)),0)", "pd_sdid=" + objs[0] + " AND pd_id <>" +
	 * pdid + " AND pd_piclass='出货单'"); if(sas != null ){ r = baseDao
	 * .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id",
	 * "nvl(sum(nvl(pd_inqty,0)),0)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" +
	 * sas[0] + "' and pd_orderdetno=" + sas[1]); } qty = qty == null ? 0 : qty;
	 * r = r == null ? 0 : r; rs = baseDao.queryForRowSet( CHECK_SAYQTY, sd_id,
	 * Double.parseDouble(qty.toString()) + tQty -
	 * Double.parseDouble(r.toString())); if (rs.next()) { StringBuffer sb = new
	 * StringBuffer( "[本次数量填写超出可转数量],销售单号:")
	 * .append(rs.getString("sd_code")).append(",行号:")
	 * .append(rs.getInt("sd_detno")).append(",订单数:")
	 * .append(rs.getDouble("sd_qty"))
	 * .append(",出货单数:").append(qty).append(",销售退货数:")
	 * .append(r).append(",本次数:").append(tQty);
	 * BaseUtil.showError(sb.toString()); } baseDao.updateByCondition(
	 * "SaleDetail", "sd_yqty=" + (Double.parseDouble(String.valueOf(qty)) +
	 * tQty - Double.parseDouble(r .toString())), "sd_id=" + sd_id);
	 * 
	 * baseDao.updateByCondition("SaleDetail", "sd_yqty=0",
	 * "sd_yqty<=0 and sd_said=" + sas[2]); int count =
	 * baseDao.getCountByCondition("SaleDetail", "sd_said=" + sas[2]); int
	 * yCount = baseDao .getCountByCondition( "SaleDetail", "sd_said=" + sas[2]
	 * + " and nvl(sd_yqty,0)=nvl(sd_qty,0) and nvl(sd_yqty,0)>0"); int xCount =
	 * baseDao.getCountByCondition("SaleDetail", "sd_said=" + sas[2] +
	 * " and nvl(sd_yqty,0)=0"); String status = "PARTOUT"; if (yCount == count)
	 * { status = "TURNOUT"; } if (xCount == count) { status = ""; }
	 * baseDao.updateByCondition( "Sale", "Sa_SENDSTATUSCODE='" + status +
	 * "',Sa_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'", "sa_id=" +
	 * sas[2]); // 客户送货提醒单直接转出货单 if (objs[6] != null &&
	 * Integer.parseInt(objs[6].toString()) != 0) { Double yqty = baseDao
	 * .getFieldValue("ProdIODetail", "nvl(sum(nvl(pd_outqty,0)),0)",
	 * "pd_noticeid=" + objs[6] + " AND pd_id <>" + pdid +
	 * " AND pd_piclass='出货单'", Double.class); rs =
	 * baseDao.queryForRowSet(CHECK_NOTICEYQTY, objs[6], yqty + tQty); if
	 * (rs.next()) { BaseUtil.showError("行 " + s.get("pd_pdno") +
	 * " 填写的数量超出客户送货提醒需求数！<br>需求数：" + rs.getObject("sn_qty") + " < 已转发货数：" +
	 * yqty + " + 本次填写：" + tQty); } else {
	 * baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=" + (yqty + tQty),
	 * "sn_id=" + objs[6]); } } } } else { if(s.get("pd_snid") != null &&
	 * Integer.parseInt(s.get("pd_snid").toString()) != 0){
	 * if(s.get("pd_orderid") != null &&
	 * Integer.parseInt(s.get("pd_orderid").toString()) != 0){ snd_id =
	 * Integer.parseInt(s.get("pd_orderid").toString()); } if (snd_id != null &&
	 * snd_id > 0) { sns = baseDao .getFieldsDataByCondition(
	 * "SendNotifyDetail left join SendNotify on snd_snid=sn_id",
	 * "sn_code,snd_pdno,snd_ordercode,snd_orderdetno", "snd_id=" + snd_id); qty
	 * = baseDao.getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)",
	 * "pd_orderid=" + snd_id + " AND pd_id <>" + pdid +
	 * " AND pd_piclass='出货单'"); if (sns[2] == null ||
	 * sns[2].toString().trim().length() == 0) { r = baseDao
	 * .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_orderid=" +
	 * snd_id); } else { r = baseDao .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" +
	 * sns[2] + "' and pd_orderdetno=" + sns[3]); } qty = qty == null ? 0 : qty;
	 * r = r == null ? 0 : r; bpr = bpr == null ? 0 : bpr; rs =
	 * baseDao.queryForRowSet(CHECK_YQTY, snd_id,
	 * Double.parseDouble(qty.toString()) + tQty -
	 * Double.parseDouble(r.toString())); if (rs.next()) { StringBuffer sb = new
	 * StringBuffer( "[本次数量填写超出可转数量],出货通知单号:") .append(rs.getString("snd_code"))
	 * .append(",行号:") .append(rs.getInt("snd_pdno")) .append(",通知单数:")
	 * .append(rs.getDouble("snd_outqty")) .append(",已转 出货单数:").append(qty)
	 * .append(",销售退货单数:").append(r) .append(",本次数:").append(tQty);
	 * BaseUtil.showError(sb.toString()); } // 通知单 baseDao.updateByCondition(
	 * "SendNotifyDetail", "snd_yqty=" + (Double.parseDouble(String
	 * .valueOf(qty)) + tQty), "snd_id=" + snd_id);
	 * baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=0",
	 * "snd_yqty<=0 and snd_id=" + snd_id); int count =
	 * baseDao.getCountByCondition( "SendNotifyDetail", "snd_snid=" +
	 * s.get("pd_snid")); int yCount = baseDao .getCountByCondition(
	 * "SendNotifyDetail", "snd_snid=" + s.get("pd_snid") +
	 * " and nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>0"); int
	 * xCount = baseDao.getCountByCondition( "SendNotifyDetail", "snd_snid=" +
	 * s.get("pd_snid") + " and nvl(snd_yqty,0)=0"); String status = "PARTOUT";
	 * if (yCount == count) { status = "TURNOUT"; } if (xCount == count) {
	 * status = ""; } baseDao.updateByCondition( "SendNotify",
	 * "SN_SENDSTATUSCODE='" + status + "',SN_SENDSTATUS='" +
	 * BaseUtil.getLocalMessage(status) + "'", "sn_id=" + s.get("pd_snid")); if
	 * (s.get("pd_ordercode") != null && !"".equals(s.get("pd_ordercode"))) {
	 * baseDao.updateByCondition( "Sale", "sa_sendstatuscode='" + status +
	 * "',sa_sendstatus='" + BaseUtil.getLocalMessage(status) + "'", "sa_code='"
	 * + s.get("pd_ordercode") + "'"); } } } else {// 直接走出货单 sd_id =
	 * Integer.parseInt(s.get("pd_sdid").toString()); sas =
	 * baseDao.getFieldsDataByCondition(
	 * "SaleDetail left join Sale on sd_said=sa_id", "sa_code,sd_detno,sa_id",
	 * "sd_id=" + sd_id); qty = baseDao.getFieldDataByCondition("ProdIODetail",
	 * "nvl(sum(nvl(pd_outqty,0)),0)", "pd_sdid=" + sd_id + " AND pd_id <>" +
	 * pdid + " AND pd_piclass='出货单'"); r = baseDao .getFieldDataByCondition(
	 * "ProdIODetail left join ProdInOut on pd_piid=pi_id",
	 * "nvl(sum(nvl(pd_inqty,0)),0)",
	 * "pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" +
	 * sas[0] + "' and pd_orderdetno=" + sas[1]); qty = qty == null ? 0 : qty; r
	 * = r == null ? 0 : r; rs = baseDao.queryForRowSet( CHECK_SAYQTY, sd_id,
	 * Double.parseDouble(qty.toString()) + tQty -
	 * Double.parseDouble(r.toString())); if (rs.next()) { StringBuffer sb = new
	 * StringBuffer( "[本次数量填写超出可转数量],销售单号:")
	 * .append(rs.getString("sd_code")).append(",行号:")
	 * .append(rs.getInt("sd_detno")).append(",订单数:")
	 * .append(rs.getDouble("sd_qty"))
	 * .append(",出货单数:").append(qty).append(",销售退货数:")
	 * .append(r).append(",本次数:").append(tQty);
	 * BaseUtil.showError(sb.toString()); } baseDao.updateByCondition(
	 * "SaleDetail", "sd_yqty=" + (Double.parseDouble(String.valueOf(qty)) +
	 * tQty - Double.parseDouble(r .toString())), "sd_id=" + sd_id);
	 * 
	 * baseDao.updateByCondition("SaleDetail", "sd_yqty=0",
	 * "sd_yqty<=0 and sd_said=" + sas[2]); int count =
	 * baseDao.getCountByCondition("SaleDetail", "sd_said=" + sas[2]); int
	 * yCount = baseDao .getCountByCondition( "SaleDetail", "sd_said=" + sas[2]
	 * + " and nvl(sd_yqty,0)=nvl(sd_qty,0) and nvl(sd_yqty,0)>0"); int xCount =
	 * baseDao.getCountByCondition("SaleDetail", "sd_said=" + sas[2] +
	 * " and nvl(sd_yqty,0)=0"); String status = "PARTOUT"; if (yCount == count)
	 * { status = "TURNOUT"; } if (xCount == count) { status = ""; }
	 * baseDao.updateByCondition( "Sale", "Sa_SENDSTATUSCODE='" + status +
	 * "',Sa_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'", "sa_id=" +
	 * sas[2]); // 客户送货提醒单直接转出货单 if (s.get("pd_noticeid") != null &&
	 * Integer.parseInt(s.get("pd_noticeid").toString()) != 0) { Double yqty =
	 * baseDao .getFieldValue("ProdIODetail", "nvl(sum(nvl(pd_outqty,0)),0)",
	 * "pd_noticeid=" + s.get("pd_noticeid") + " AND pd_id <>" + pdid +
	 * " AND pd_piclass='出货单'", Double.class); rs =
	 * baseDao.queryForRowSet(CHECK_NOTICEYQTY, s.get("pd_noticeid"), yqty +
	 * tQty); if (rs.next()) { BaseUtil.showError("行 " + s.get("pd_pdno") +
	 * " 填写的数量超出客户送货提醒需求数！<br>需求数：" + rs.getObject("sn_qty") + " < 已转发货数：" +
	 * yqty + " + 本次填写：" + tQty); } else {
	 * baseDao.updateByCondition("SaleNotifyDown", "sn_yqty=" + (yqty + tQty),
	 * "sn_id=" + s.get("pd_noticeid")); } } } } } }
	 */

	/**
	 * purchase->purc->deletedetail 采购单明细行删除时，对应请购单及明细数据恢复
	 */
	public void purc_deletedetail(Integer id) {
		// 还原请购明细及请购单
		Object[] objs = baseDao.getFieldsDataByCondition("PurchaseDetail", new String[] { "pd_sourcedetail", "pd_qty", "NVL(pd_mmid,0)",
				"nvl(pd_mtid,0)" }, "pd_id=" + id + " and pd_sourcedetail is not null and pd_sourcedetail<>0");
		if (objs != null) {
			baseDao.updateByCondition("ApplicationDetail", "ad_statuscode='AUDITED',ad_status='" + BaseUtil.getLocalMessage("AUDITED")
					+ "',ad_yqty=ad_yqty-" + Float.parseFloat(String.valueOf(objs[1])), "ad_id=" + objs[0]);
			applicationDao.checkAdQty(Integer.valueOf(objs[0].toString()));
			baseDao.updateByCondition("MRPReplace", "mr_purcqty=nvl(mr_purcqty,0)-" + Float.parseFloat(objs[1].toString()), "mr_id="
					+ objs[2]);
			// applicationreplace
			baseDao.updateByCondition("ApplicationReplace", "ar_purcqty=nvl(ar_purcqty,0)-" + Float.parseFloat(objs[1].toString()),
					"ar_id=" + objs[3]);
		}
		// 更新采购单含税金额与不含税金额
		baseDao.updateByCondition("purchase", "pu_total=pu_total-(select nvl(pd_total,0) from purchasedetail where pd_id=" + id
				+ ") , pu_taxtotal=pu_taxtotal -" + "(select nvl(pd_taxtotal,0) from purchasedetail where pd_id=" + id + ")",
				"pu_id=( select pd_puid from purchasedetail where pd_id=" + id + ")");
	}

	/**
	 * sale->sendnotify->deletedetail 发货通知单明细行删除时，根据订单明细ID更新已转数，已转备品数，通知单状态，发货状态
	 */
	public void sendNotify_deletedetail(Integer id) {
		// 还原销售明细及销售单
		sendNotifyDao.restoreSale(id);
	}

	/**
	 * 收料单删除前，还原收料通知单，采购单数据
	 * 
	 * @author madan,yingp
	 */
	public void verifyapply_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("VerifyApplyDetail", new String[] { "vad_pucode", "nvl(vad_pudetno,0)",
				"nvl(vad_qty,0)", "nvl(vad_andid,0)" }, "vad_vaid=" + id + " and nvl(vad_pucode,' ')<> ' '");
		for (Object[] obj : objs) {
			if (obj != null) {
				if (Integer.parseInt(obj[3].toString()) > 0) {
					baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=and_yqty-" + Double.parseDouble(obj[2].toString()), "and_id="
							+ obj[3]);
					baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + obj[3]);
					Integer anid = baseDao.getJdbcTemplate().queryForObject(
							"select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?", Integer.class, obj[3]);
					int total = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid);
					int aud = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid='" + anid + "' AND and_yqty=0");
					int turn = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid='" + anid + "' AND and_yqty=and_inqty");
					String status = "PART2VA";
					if (aud == total) {
						status = "AUDITED";
					} else if (turn == total) {
						status = "TURNVA";
					}
					baseDao.updateByCondition("AcceptNotify",
							"an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "',AN_SENDSTATUS='待上传'",
							"an_id=" + anid);
				}
				baseDao.updateByCondition("PurchaseDetail", "pd_yqty=NVL(pd_yqty,0)-" + Double.parseDouble(obj[2].toString()), "pd_code='"
						+ obj[0] + "' and pd_detno=" + obj[1]);
				baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_code='" + obj[0] + "' and pd_detno="
						+ obj[1]);
				int total = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + obj[0] + "'");
				int aud = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + obj[0] + "' AND NVL(pd_yqty,0)=0");
				int turn = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + obj[0] + "' AND NVL(pd_yqty,0)=pd_qty");
				String status = "PART2VA";
				if (aud == total) {
					status = "";
				} else if (turn == total) {
					status = "TURNVA";
				}
				baseDao.updateByCondition("Purchase",
						"pu_acceptstatuscode='" + status + "',pu_acceptstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_code='"
								+ obj[0] + "'");
			}
		}
	}

	/**
	 * 收料单明细删除后，还原采购单及明细数据 收料单明细删除前，还原收料通知单，采购单及明细数据
	 * 
	 * @author madan,yingp
	 */
	public void verifyapply_detatedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("VerifyApplyDetail", new String[] { "vad_pucode", "nvl(vad_pudetno,0)",
				"nvl(vad_qty,0)", "nvl(vad_andid,0)", "nvl(vad_purcqty,0)" }, "vad_id=" + id + " and trim(vad_pucode) is not null");
		if (objs != null) {
			if (Integer.parseInt(objs[3].toString()) > 0) {
				baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=and_yqty-" + Double.parseDouble(objs[2].toString()), "and_id="
						+ objs[3]);
				baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + objs[3]);
				Integer anid = baseDao.getJdbcTemplate().queryForObject("select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?",
						Integer.class, objs[3]);
				int total = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid);
				int aud = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid='" + anid + "' AND and_yqty=0");
				int turn = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid='" + anid + "' AND and_yqty=and_inqty");
				String status = "PART2VA";
				if (aud == total) {
					status = "AUDITED";
				} else if (turn == total) {
					status = "TURNVA";
				}
				baseDao.updateByCondition("AcceptNotify", "an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status)
						+ "',AN_SENDSTATUS='待上传'", "an_id=" + anid);
			}
			baseDao.updateByCondition("PurchaseDetail", "pd_yqty=NVL(pd_yqty,0)-" + Double.parseDouble(objs[2].toString()), "pd_code='"
					+ objs[0] + "' and pd_detno=" + objs[1]);
			baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_code='" + objs[0] + "' and pd_detno="
					+ objs[1]);
			/**
			 * 双单位 采购收料单：删除明细【采购单位收料数量】没有更新到采购单的【采购单位已转数】
			 */
			baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=NVL(pd_ypurcqty,0)-" + Double.parseDouble(objs[4].toString()),
					"pd_code='" + objs[0] + "' and pd_detno=" + objs[1]);
			baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=0", "NVL(pd_ypurcqty,0)<=0 and pd_code='" + objs[0]
					+ "' and pd_detno=" + objs[1]);
			int total = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + objs[0] + "'");
			int aud = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + objs[0] + "' AND NVL(pd_yqty,0)=0");
			int turn = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + objs[0] + "' AND NVL(pd_yqty,0)=pd_qty");
			String status = "PART2VA";
			if (aud == total) {
				status = "";
			} else if (turn == total) {
				status = "TURNVA";
			}
			baseDao.updateByCondition("Purchase",
					"pu_acceptstatuscode='" + status + "',pu_acceptstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_code='"
							+ objs[0] + "'");
		}
	}

	/**
	 * 采购验收单：修改前，修改数量要反馈到采购单,收料单,收料通知单
	 * 
	 * @author madan,yingp
	 */
	static final String CHECK_PU_YQTY = "SELECT pd_code,pd_detno,pd_qty FROM PurchaseDetail WHERE pd_id=? and pd_qty<?";
	static final String CHECK_AN_YQTY = "SELECT an_code,and_detno,and_inqty FROM AcceptNotifyDetail left join AcceptNotify on and_anid=an_id WHERE and_id=? and and_inqty<?";

	public void prodInOut_checkin_save_qty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Set<String> vacodes = new HashSet<String>();
		Object pdid = null;// 采购验收单明细ID
		Object qty = null;
		Integer pd_id = null;// 采购单明细ID
		double tQty = 0;// 采购验收单修改数量
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(s.get("pd_inqty").toString());
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_inqty,0)",
						"nvl(pd_qcid,0)", "pd_vacode", "pd_ordercode", "nvl(pd_anid,0)", "nvl(pd_orderdetno,0)" }, "pd_id=" + pdid
						+ " and nvl(pd_orderid,0)<>0");
				if (objs != null && objs[0] != null) {
					pd_id = Integer.parseInt(String.valueOf(objs[0]));
					if ((objs[2] == null || Integer.parseInt(String.valueOf(objs[2])) == 0)
							&& (objs[5] == null || Integer.parseInt(String.valueOf(objs[5])) == 0)
							&& (objs[3] == null || "".equals(objs[3]))) {// 来源采购单
						if (pd_id != null && pd_id > 0) {
							qty = baseDao.getFieldDataByCondition("ProdIODetail",
									"sum(nvl(pd_inqty,0)-case when pd_status=99 then nvl(pd_outqty,0) else 0 end)", "pd_ordercode='"
											+ objs[4] + "' and pd_orderdetno='" + objs[6]
											+ "' and pd_piclass in ('采购验收单','采购验退单') AND pd_id <>" + pdid);
							qty = qty == null ? 0 : qty;
							rs = baseDao.queryForRowSet(CHECK_PU_YQTY, pd_id, Double.parseDouble(qty.toString()) + tQty);
							if (rs.next()) {
								StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],采购单号:").append(rs.getString("pd_code")).append(",行号:")
										.append(rs.getInt("pd_detno")).append(",采购数量:").append(rs.getDouble("pd_qty")).append(",已转数量:")
										.append(qty).append(",本次数量:").append(tQty);
								BaseUtil.showError(sb.toString());
							}
							// 采购单
							baseDao.updateByCondition("PurchaseDetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
									"pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "NVL(pd_yqty,0)=0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'",
									"NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0) <> 0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'", "pd_id=" + pd_id
									+ " and nvl(pd_yqty,0)<nvl(pd_qty,0) and nvl(pd_yqty,0)<>0");
							Integer puid = baseDao.queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class,
									objs[0]);
							if (puid != null && puid > 0) {
								int count = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
								int yCount = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid
										+ " and nvl(pd_acceptqty,0)=pd_qty");
								int xCount = baseDao
										.getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and nvl(pd_acceptqty,0)=0");
								String status = "PART2IN";
								if (yCount == count) {
									status = "TURNIN";
								}
								if (xCount == count) {
									status = "";
								}
								baseDao.updateByCondition("Purchase",
										"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
										"pu_id=" + puid);
							}
						}
					} else if (objs[5] != null && Integer.parseInt(String.valueOf(objs[5])) != 0) {// 来源收料通知单
						int and_id = Integer.parseInt(String.valueOf(objs[5]));
						if (and_id > 0) {
							qty = baseDao.getFieldDataByCondition("ProdIODetail",
									"sum(nvl(pd_inqty,0)-case when pd_status=99 then nvl(pd_outqty,0) else 0 end)", "pd_anid=" + and_id
											+ "and pd_ordercode='" + objs[4] + "' and pd_piclass in ('采购验收单','采购验退单') AND pd_id <>" + pdid);
							qty = qty == null ? 0 : qty;
							rs = baseDao.queryForRowSet(CHECK_AN_YQTY, and_id, Double.parseDouble(qty.toString()) + tQty);
							if (rs.next()) {
								StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],通知单号:").append(rs.getString("an_code")).append(",行号:")
										.append(rs.getInt("and_detno")).append(",采购数量:").append(rs.getDouble("and_inqty")).append(",已转数量:")
										.append(qty).append(",本次数量:").append(tQty);
								BaseUtil.showError(sb.toString());
							}
							// 收料通知单
							baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
									"and_id=" + and_id);
							baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + and_id);
							Integer anid = baseDao.queryForObject("select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?",
									Integer.class, objs[5]);
							if (anid != null && anid > 0) {
								int count = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid);
								int yCount = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid
										+ " and and_yqty=and_inqty");
								int xCount = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid + " and and_yqty=0");
								String status = "PART2IN";
								if (yCount == count) {
									status = "TURNIN";
								}
								if (xCount == count) {
									status = "AUDITED";
								}
								baseDao.updateByCondition("AcceptNotify",
										"an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "'", "an_id="
												+ anid);
							}
						}
					} else {
						Double uQty = Double.parseDouble(objs[1].toString()) - tQty;
						if (uQty > 0) {
							// 修改收料明细的已入库数及状态
							baseDao.updateByCondition("VerifyApplyDetail", "vad_yqty=vad_yqty-" + uQty + ",vad_status='PART2IN'", "vad_id="
									+ objs[0]);
							baseDao.updateByCondition("VerifyApplyDetail", "vad_status='AUDITED'", "vad_id=" + objs[0] + " AND vad_yqty=0");
							if (!vacodes.contains(objs[3])) {
								vacodes.add(objs[3].toString());
							}
						}
						// 修改收料单状态
						Iterator<String> codes = vacodes.iterator();
						String code = null;
						while (codes.hasNext()) {
							code = codes.next();
							int vaid = Integer.parseInt(baseDao.getFieldDataByCondition("VerifyApply", "va_id", "va_code='" + code + "'")
									.toString());
							int total = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid);
							int aud = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid + "AND vad_yqty=0");
							int turn = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid + " AND AND vad_yqty=vad_qty");
							String status = "PART2VA";
							if (aud == total) {
								status = "AUDITED";
							} else if (turn == total) {
								status = "TURNVA";
							}
							baseDao.updateByCondition("VerifyApply",
									"va_turnstatuscode='" + status + "',va_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "va_id="
											+ vaid);
						}
					}
				}
			}
		}
	}

	public void prodInOut_checkin_save_qty2(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Set<String> vacodes = new HashSet<String>();
		Object pdid = null;// 采购验收单明细ID
		Object qty = null;
		Integer pd_id = null;// 采购单明细ID
		Integer oldpdid = null;// 原采购单明细ID
		double tQty = 0;// 采购验收单修改数量
		SqlRowList rs = null;
		Object ordercode = null;
		Object orderdetno = null;
		boolean bool = false;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			ordercode = s.get("pd_ordercode");
			orderdetno = s.get("pd_orderdetno");
			tQty = Double.parseDouble(s.get("pd_inqty").toString());
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_inqty,0)",
						"nvl(pd_qcid,0)", "pd_vacode", "pd_ordercode", "nvl(pd_anid,0)", "nvl(pd_orderdetno,0)", "nvl(pd_purcinqty,0)" },
						"pd_id=" + pdid + " and nvl(pd_orderid,0)<>0");
				if (objs != null && objs[0] != null) {
					pd_id = Integer.parseInt(String.valueOf(objs[0]));
					if ((objs[2] == null || Integer.parseInt(String.valueOf(objs[2])) == 0)
							&& (objs[5] == null || Integer.parseInt(String.valueOf(objs[5])) == 0)
							&& (objs[3] == null || "".equals(objs[3]))) {// 来源采购单
						if (pd_id != null && pd_id > 0) {
							if (StringUtil.hasText(ordercode) && ordercode.equals(objs[4]) && orderdetno.equals(objs[6])) {
								pd_id = Integer.parseInt(String.valueOf(objs[0]));
								qty = baseDao.getFieldDataByCondition("ProdIODetail",
										"sum(nvl(pd_inqty,0)-case when pd_status=99 then nvl(pd_outqty,0) else 0 end)", "pd_ordercode='"
												+ objs[4] + "' and pd_orderdetno='" + objs[6]
												+ "' and pd_piclass in ('采购验收单','采购验退单') AND pd_id <>" + pdid);
							} else {
								/**
								 * wusy 问题反馈单号：2017010060
								 * 描述：MRB结果批量处理产生的采购验收单，明细行更新仓库的时候提示
								 * 【不允许直接入库，不允许直接在明细修改采购单行号！】 去掉【类型】为“特采”的限制。
								 */
								if (!"特采".equals(store.get("pi_type"))) {
									if (ordercode.equals(objs[4]) && !orderdetno.equals(objs[6])) {
										SqlRowList pus = baseDao
												.queryForRowSet(
														"select pk_allowin,pu_kind,pu_id from Purchase left join PurchaseKind on pu_kind=pk_name where pu_code=? and nvl(pk_allowin,0)=0",
														objs[4]);
										if (pus.next()) {
											BaseUtil.showError("采购单[" + objs[4] + "]采购类型为[" + pus.getObject("pu_kind")
													+ "]不允许直接入库，不允许直接在明细修改采购单行号！");
										}
									}
									if (!ordercode.equals(objs[4])) {
										SqlRowList pus = baseDao
												.queryForRowSet(
														"select pk_allowin,pu_kind,pu_id from Purchase left join PurchaseKind on pu_kind=pk_name where pu_code=? and nvl(pk_allowin,0)=0",
														ordercode);
										if (pus.next()) {
											BaseUtil.showError("采购单[" + ordercode + "]采购类型为[" + pus.getObject("pu_kind")
													+ "]不允许直接入库，不允许直接在明细修改采购单号！");
										}
										pus = baseDao
												.queryForRowSet(
														"select pk_allowin,pu_kind,pu_id from Purchase left join PurchaseKind on pu_kind=pk_name where pu_code=? and nvl(pk_allowin,0)=0",
														objs[4]);
										if (pus.next()) {
											BaseUtil.showError("采购单[" + objs[4] + "]采购类型为[" + pus.getObject("pu_kind")
													+ "]不允许直接入库，不允许直接在明细修改采购单号！");
										}
									}
								}
								oldpdid = pd_id;
								pd_id = baseDao.getFieldValue("PurchaseDetail", "nvl(pd_id,0)", "pd_code='" + ordercode + "' and pd_detno="
										+ orderdetno, Integer.class);
								qty = baseDao.getFieldDataByCondition("ProdIODetail",
										"sum(nvl(pd_inqty,0)-case when pd_status=99 then nvl(pd_outqty,0) else 0 end)", "pd_ordercode='"
												+ ordercode + "' and pd_orderdetno='" + orderdetno
												+ "' and pd_piclass in ('采购验收单','采购验退单') AND pd_id <>" + pdid);
								bool = true;
							}
							qty = qty == null ? 0 : qty;
							rs = baseDao.queryForRowSet(CHECK_PU_YQTY, pd_id, Double.parseDouble(qty.toString()) + tQty);
							if (rs.next()) {
								StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],采购单号:").append(rs.getString("pd_code")).append(",行号:")
										.append(rs.getInt("pd_detno")).append(",采购数量:").append(rs.getDouble("pd_qty")).append(",已转数量:")
										.append(qty).append(",本次数量:").append(tQty);
								BaseUtil.showError(sb.toString());
							}
							if (bool) {
								baseDao.updateByCondition("PurchaseDetail", "pd_yqty=nvl(pd_yqty,0)-" + objs[1], "pd_id=" + oldpdid);
								baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + oldpdid);
								/**
								 * 双单位
								 */
								baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=nvl(pd_ypurcqty,0)-" + objs[7], "pd_id=" + oldpdid);
								baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=0", "NVL(pd_ypurcqty,0)<=0 and pd_id=" + oldpdid);
								baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "NVL(pd_yqty,0)=0 and pd_id=" + oldpdid);
								baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'",
										"NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0) <> 0 and pd_id=" + oldpdid);
								baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'", "pd_id=" + oldpdid
										+ " and nvl(pd_yqty,0)<nvl(pd_qty,0) and nvl(pd_yqty,0)<>0");
							}
							// 采购单
							baseDao.updateByCondition("PurchaseDetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
									"pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "NVL(pd_yqty,0)=0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'",
									"NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0) <> 0 and pd_id=" + pd_id);
							baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'", "pd_id=" + pd_id
									+ " and nvl(pd_yqty,0)<nvl(pd_qty,0) and nvl(pd_yqty,0)<>0");
							Integer puid = baseDao.queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class,
									objs[0]);
							if (puid != null && puid > 0) {
								int count = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
								int yCount = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid
										+ " and nvl(pd_acceptqty,0)=pd_qty");
								int xCount = baseDao
										.getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and nvl(pd_acceptqty,0)=0");
								String status = "PART2IN";
								if (yCount == count) {
									status = "TURNIN";
								}
								if (xCount == count) {
									status = "";
								}
								baseDao.updateByCondition("Purchase",
										"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
										"pu_id=" + puid);
							}
						}
					} else if (objs[5] != null && Integer.parseInt(String.valueOf(objs[5])) != 0) {// 来源收料通知单
						if (StringUtil.hasText(ordercode) && !ordercode.equals(objs[4]) && !orderdetno.equals(objs[6])) {
							BaseUtil.showError("来源收料通知单的不允许修改采购单号或者采购行号！");
						}
						int and_id = Integer.parseInt(String.valueOf(objs[5]));
						if (and_id > 0) {
							qty = baseDao.getFieldDataByCondition("ProdIODetail",
									"sum(nvl(pd_inqty,0)-case when pd_status=99 then nvl(pd_outqty,0) else 0 end)", "pd_anid=" + and_id
											+ "and pd_ordercode='" + objs[4] + "' and pd_piclass in ('采购验收单','采购验退单') AND pd_id <>" + pdid);
							qty = qty == null ? 0 : qty;
							rs = baseDao.queryForRowSet(CHECK_AN_YQTY, and_id, Double.parseDouble(qty.toString()) + tQty);
							if (rs.next()) {
								StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],通知单号:").append(rs.getString("an_code")).append(",行号:")
										.append(rs.getInt("and_detno")).append(",采购数量:").append(rs.getDouble("and_inqty")).append(",已转数量:")
										.append(qty).append(",本次数量:").append(tQty);
								BaseUtil.showError(sb.toString());
							}
							// 收料通知单
							baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
									"and_id=" + and_id);
							baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + and_id);
							Integer anid = baseDao.queryForObject("select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?",
									Integer.class, objs[5]);
							if (anid != null && anid > 0) {
								int count = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid);
								int yCount = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid
										+ " and and_yqty=and_inqty");
								int xCount = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + anid + " and and_yqty=0");
								String status = "PART2IN";
								if (yCount == count) {
									status = "TURNIN";
								}
								if (xCount == count) {
									status = "AUDITED";
								}
								baseDao.updateByCondition("AcceptNotify",
										"an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "'", "an_id="
												+ anid);
							}
						}
					} else {
						if (StringUtil.hasText(ordercode) && StringUtil.hasText(orderdetno) && !ordercode.equals(objs[4])
								&& !orderdetno.equals(objs[6])) {
							BaseUtil.showError("来源检验单的不允许修改采购单号或者采购行号！");
						}
						double uQty = Double.parseDouble(objs[1].toString()) - tQty;
						if (uQty > 0) {
							// 修改收料明细的已入库数及状态
							baseDao.updateByCondition("VerifyApplyDetail", "vad_yqty=vad_yqty-" + uQty + ",vad_status='PART2IN'", "vad_id="
									+ objs[0]);
							baseDao.updateByCondition("VerifyApplyDetail", "vad_status='AUDITED'", "vad_id=" + objs[0] + " AND vad_yqty=0");
							if (!vacodes.contains(objs[3])) {
								vacodes.add(objs[3].toString());
							}
						}
						// 修改收料单状态
						Iterator<String> codes = vacodes.iterator();
						String code = null;
						while (codes.hasNext()) {
							code = codes.next();
							int vaid = Integer.parseInt(baseDao.getFieldDataByCondition("VerifyApply", "va_id", "va_code='" + code + "'")
									.toString());
							int total = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid);
							int aud = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid + "AND vad_yqty=0");
							int turn = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + vaid + " AND AND vad_yqty=vad_qty");
							String status = "PART2VA";
							if (aud == total) {
								status = "AUDITED";
							} else if (turn == total) {
								status = "TURNVA";
							}
							baseDao.updateByCondition("VerifyApply",
									"va_turnstatuscode='" + status + "',va_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "va_id="
											+ vaid);
						}
					}
				}
			} else {
				Object orderid = s.get("pd_orderid");
				if (s.get("pd_qcid") != null && !"0".equals(s.get("pd_qcid"))) {
					BaseUtil.showError("来源检验单的不允许新增采购单号或者采购行号！");
				} else if (s.get("pd_anid") != null && !"0".equals(s.get("pd_anid"))) {
					BaseUtil.showError("来源收料通知单的不允许新增采购单号或者采购行号！");
				} else {
					if (StringUtil.hasText(ordercode)) {
						if (!"特采".equals(store.get("pi_type"))) {
							SqlRowList pus = baseDao
									.queryForRowSet(
											"select pk_allowin,pu_kind,pu_id from Purchase left join PurchaseKind on pu_kind=pk_name where pu_code=? and nvl(pk_allowin,0)=0",
											ordercode);
							if (pus.next()) {
								BaseUtil.showError("采购单[" + ordercode + "]采购类型为[" + pus.getObject("pu_kind") + "]不允许直接入库，不允许直接在明细修改采购单行号！");
							}
						}
						if (orderid == null || orderid.equals("") || orderid.equals("0")) {
							orderid = baseDao.getFieldDataByCondition("PurchaseDetail", "pd_id", "pd_code='" + ordercode
									+ "' and sd_detno=" + orderdetno);
							s.put("pd_orderid", orderid);
						}
						prodInOutDao.restorePurcYqty(tQty, ordercode.toString(), Integer.valueOf(orderdetno.toString()));
					}
				}
			}
		}
	}

	/**
	 * 生产领料单修改前，修改数量要反馈到制造单
	 */
	public void prodInOut_make_save_outqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		List<Integer> maids = new ArrayList<Integer>();
		for (Map<Object, Object> s : gstore) {
			int pd_id = Integer.valueOf(s.get("pd_id").toString());
			if (pd_id != 0) {
				Object[] objects = baseDao.getFieldsDataByCondition(
						" ProdIoDetail left join make on ma_code=pd_ordercode left join makematerial on mm_maid=ma_id and"
								+ " pd_orderdetno=mm_detno", new String[] { "pd_pdno", "pd_ordercode", "pd_outqty", "mm_id", "ma_id",
								"mm_qty" }, "pd_id=" + pd_id);
				if (objects != null) {
					int mm_id = Integer.parseInt(objects[3].toString());
					Double mm_qty = Double.parseDouble(objects[5].toString());
					Integer ma_id = Integer.parseInt(objects[4].toString());
					Double pd_outqty = Double.parseDouble(objects[2].toString());
					Double newQty = Double.parseDouble(s.get("pd_outqty").toString());
					if (String.valueOf(objects[0]).equals(String.valueOf(s.get("pd_pdno")))) {
						if (objects[1].toString().equals(s.get("pd_ordercode").toString())) {
							Double uQty = pd_outqty - newQty;
							// 修改制造明细的已转数及状态
							baseDao.updateByCondition("MakeMaterial", "mm_totaluseqty=mm_totaluseqty-(" + uQty + "),mm_status='PARTGET'",
									"mm_id=" + mm_id);
							baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED'", "mm_id=" + mm_id + " AND mm_havegetqty=0");
							if (!maids.contains(ma_id)) {
								maids.add(ma_id);
							}
						} else {
							// 修改原有制造明细的已转数及状态
							baseDao.updateByCondition("MakeMaterial", "mm_totaluseqty=mm_totaluseqty-(" + pd_outqty
									+ "),mm_status='PARTGET'", "mm_id=" + mm_id);
							baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED'", "mm_id=" + mm_id + " AND mm_havegetqty=0");
							if (!maids.contains(ma_id)) {
								maids.add(ma_id);
							}
							// 修改现在制造明细的已转数及状态
							Object[] ob = baseDao.getFieldsDataByCondition("MakeMaterial left join Make on ma_id=mm_maid",
									"mm_qty,mm_id,ma_id",
									"ma_code='" + s.get("pd_ordercode").toString() + "' and mm_detno='" + s.get("pd_orderdetno") + "'");
							baseDao.updateByCondition("MakeMaterial", "mm_totaluseqty=mm_totaluseqty+(" + newQty + "),mm_status='PARTGET'",
									"mm_id=" + ob[1]);
							baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED'", "mm_id=" + Integer.parseInt(ob[2].toString())
									+ " AND mm_havegetqty=0");
							if (!maids.contains(Integer.parseInt(ob[2].toString()))) {
								maids.add(Integer.parseInt(ob[2].toString()));
							}
						}
					}
				}
			} else {
				Double newQty = Double.parseDouble(s.get("pd_outqty").toString());
				if (newQty > 0) {
					// 修改制造明细的已转数及状态
					Object[] ob = baseDao.getFieldsDataByCondition("MakeMaterial left join Make on ma_id=mm_maid", "ma_id,mm_id",
							"ma_code='" + s.get("pd_ordercode") + "' and mm_detno='" + s.get("pd_orderdetno") + "'");
					baseDao.updateByCondition("MakeMaterial", "mm_totaluseqty=mm_totaluseqty+" + newQty + ",mm_status='PARTGET'",
							"mm_code='" + s.get("pd_ordercode") + "' AND mm_detno=" + s.get("pd_orderdetno"));
					baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED'", "mm_id=" + Integer.parseInt(ob[1].toString())
							+ " AND mm_totaluseqty=0");
					if (!maids.contains(Integer.parseInt(ob[0].toString()))) {
						maids.add(Integer.parseInt(ob[0].toString()));
					}
				}
			}
		}
		if(maids.size()>0){
			// 修改制造单状态
			makeDao.updateMakeGetStatus(BaseUtil.parseList2Str(maids, ",", true));
		}
	}

	/**
	 * reserve->prodInOut->make->deletedetail->before 生产领料单明细删除前，还原制造单数据
	 */
	public void prodInOut_make_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_orderid", "pd_outqty", "pd_ordercode" },
				"pd_id=" + id + " and nvl(pd_orderid,0)<>0");
		if (objs != null && objs[0] != null) {
			Object idx = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='" + objs[2] + "'");
			if (idx == null) {
				BaseUtil.showError("工单" + objs[2] + "不存在，工单或已被删除.");
			}
			baseDao.updateByCondition("MakeMaterial", "mm_status='PARTGET',mm_totaluseqty=nvl(mm_totaluseqty,0)-" + objs[1], "mm_id="
					+ objs[0]);
			baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED',mm_totaluseqty=0", "nvl(mm_totaluseqty,0)<=0 and mm_id="
					+ objs[0]);
			makeDao.updateMakeGetStatus(idx.toString());
		}
	}

	/**
	 * reserve->prodInOut->make->delete->after 生产领料单删除前，还原制造单数据
	 */
	public void prodInOut_make_delete_restoreMa(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition(
				"ProdIoDetail left join makematerial on pd_ordercode=mm_code and pd_orderdetno=mm_detno", new String[] { "mm_id",
						"pd_outqty", "pd_ordercode" }, "pd_piid=" + id);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Object[] obj : objs) {
			baseDao.updateByCondition("MakeMaterial", "mm_status='PARTGET',mm_totaluseqty=nvl(mm_totaluseqty,0)-" + obj[1], "mm_id="
					+ obj[0]);
			baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED',mm_totaluseqty=0", "nvl(mm_totaluseqty,0)<=0 and mm_id="
					+ obj[0]);
			int ma_id = baseDao.getFieldValue("Make", "ma_id", "ma_code='" + obj[2] + "'", Integer.class);
			ids.add(ma_id);
		}
		makeDao.updateMakeGetStatus(BaseUtil.parseList2Str(ids, ",", true));
	}

	/**
	 * 生产退料单、委外退料单修改前，修改数量要反馈到制造单
	 */
	public void prodInOut_return_save_inqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		for (Map<Object, Object> s : gstore) {
			Object id = s.get("pd_id");
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Double newQty = Double.parseDouble(s.get("pd_inqty").toString());
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIOdetail", new String[] { "pd_inqty", "pd_orderid" }, "pd_id=" + id
						+ " AND nvl(pd_orderid,0)<>0");
				if (objs != null) {
					Double uQty = Double.parseDouble(objs[0].toString()) - newQty;
					String str = uQty < 0 ? "+" : "-";
					// 修改制造明细的已转数及状态
					baseDao.updateByCondition("MakeMaterial", "mm_backqty=mm_backqty" + str + Math.abs(uQty), "mm_id=" + objs[1]);
					baseDao.updateByCondition("MakeMaterial", "mm_backqty=0", "mm_id=" + objs[1] + " AND mm_backqty<0");
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->return->deletedetail->before 生产退料单、委外退料单明细删除前，还原制造单数据
	 */
	public void prodInOut_return_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_orderid", "pd_inqty" }, "pd_id=" + id
				+ " and nvl(pd_orderid,0)<>0");
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("MakeMaterial", "mm_backqty=mm_backqty-" + Double.parseDouble(objs[1].toString()), "mm_id=" + objs[0]);
			baseDao.updateByCondition("MakeMaterial", "mm_backqty=0", "mm_id=" + objs[0] + " AND mm_backqty<0");
		}
	}

	/**
	 * reserve->prodInOut->return->delete->after 生产退料单、委外退料单删除前，还原制造单数据
	 */
	public void prodInOut_return_delete_restoreMa(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_orderid", "pd_inqty" }, "pd_piid=" + id
				+ " and nvl(pd_orderid,0)<>0");
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
				baseDao.updateByCondition("MakeMaterial", "mm_backqty=mm_backqty-" + Double.parseDouble(obj[1].toString()), "mm_id="
						+ obj[0]);
				baseDao.updateByCondition("MakeMaterial", "mm_backqty=0", "mm_id=" + obj[0] + " AND mm_backqty<0");
			}
		}
	}

	/**
	 * reserve->prodInOut->turn->after author:madan 拨出单过账后过账拨入单
	 */
	public void prodInOut_turn_after_turnProdIO(Integer id) {
		// 过账拨入单
		prodInOutDao.turnProdIO(id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.turnProdIO"), BaseUtil
				.getLocalMessage("msg.turnSuccess"), "ProdInOut!AppropriationOut|pi_id=" + id));
	}

	/**
	 * reserve->prodInOut->turn->after author:madan 销售拨出单过账后过账销售拨入单
	 */
	public void prodInOut_turn_after_turnSaleProdIO(Integer id) {
		// 过账销售拨入单
		prodInOutDao.turnProdIO(id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.turnProdIO"), BaseUtil
				.getLocalMessage("msg.turnSuccess"), "ProdInOut!SaleAppropriationOut|pi_id=" + id));
	}

	/**
	 * 出货单过账后 重新排程
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void SetSaleDelivery_afterpost(Integer id) {
		String sql = "select pd_ordercode,pd_orderdetno from ProdIODetail where pd_piid=" + id;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		String code = null, Stringcon = null;
		int detno = 0;
		while (sqlRowList.next()) {
			code = sqlRowList.getString("pd_ordercode");
			detno = sqlRowList.getInt("pd_orderdetno");
			Stringcon = "sa_code='" + code + "' and sd_detno=" + detno;
			// 更新排程
			// baseDao.procedure("SCM_SALE_SETSALEDELIVERY", new Object[] {
			// Stringcon });
			saleDetailDetService.SetSaleDelivery(Stringcon);
		}
	}

	/**
	 * 销售变更单：审核之后执行排程变更操作
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */

	public void SetSaleDelivery_afteraudit(Integer id) {
		String updateSQL = "select scd_newprodcode,scd_newqty,scd_newprice,scd_sddetno,scd_sacode from SaleChangeDetail where scd_scid="
				+ id + "";
		SqlRowList uprs = baseDao.queryForRowSet(updateSQL);
		String ordercode = null, whereString = null;
		int sddetno = 0;
		if (uprs.next()) {
			sddetno = uprs.getInt("scd_sddetno");
			ordercode = uprs.getString("scd_sacode").toString();
			whereString = "sa_code='" + ordercode + "' and sd_detno=" + sddetno;
			// 更新排程
			// baseDao.procedure("SCM_SALE_SETSALEDELIVERY", new Object[] {
			// whereString });
			saleDetailDetService.SetSaleDelivery(whereString);
		}
	}

	public void prodinout_checkProdIOCateSet_beforeaudit(Integer id) {
		String sql = " select pi_type,pi_departmentcode,pi_class from ProdInOut where pi_id=" + id;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			sql = "select * from ProdIOCateSet where pc_departmentcode='" + sqlRowList.getString("pi_departmentcode") + "' and pc_type='"
					+ sqlRowList.getString("pi_type") + "' and pc_class='" + sqlRowList.getString("pi_class")
					+ "' and nvl(pc_statuscode,' ')='AUDITED'";
			sqlRowList = baseDao.queryForRowSet(sql);
			if (!sqlRowList.hasNext()) {
				BaseUtil.showError("在其他出入库科目中没有设置，不能操作!");
			}
		}
	}

	/**
	 * 出货单反过账后 重新排程
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void SetSaleDelivery_afterunepost(Integer id) {
		String sql = "select pd_ordercode,pd_orderdetno from ProdIODetail where pd_piid=" + id;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		String code = null, Stringcon = null;
		int detno = 0;
		while (sqlRowList.next()) {
			code = sqlRowList.getString("pd_ordercode");
			detno = sqlRowList.getInt("pd_orderdetno");
			Stringcon = "sa_code='" + code + "' and sd_detno=" + detno;
			// 更新排程
			// baseDao.procedure("SCM_SALE_SETSALEDELIVERY", new Object[] {
			// Stringcon });
			saleDetailDetService.SetSaleDelivery(Stringcon);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 生产领、退、补、委外领、退、补：工单号+用料序号对应的物料必须一致
	 */
	public void prodinout_pirnt_prodcodeCheck(Integer id) {
		String message = prodInOutDao.checkProduct(id);
		if (message != null && message.length() > 0) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 检查关联的采购订单+序号是否存在，状态是否有效(作废、结案的不能操作)
	 */
	public void prodinout_pirnt_pudetailCheck(Integer id) {
		String message = prodInOutDao.checkPurcDetail(id);
		if (message != null && message.length() > 0) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 检查关联的订单+序号是否存在，状态是否有效(作废、结案的不能操作)
	 */
	public void prodinout_pirnt_sadetailCheck(Integer id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('订单号:'||pd_ordercode||'订单序号:'||pd_orderdetno) from (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piid=?  and pd_piclass in ('出货单','销售退货单') and  not exists (select sd_code,sd_detno from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno) )",
						String.class, id);
		if (err != null) {
			BaseUtil.showError("订单号+序号不存在!" + err);
		}
		err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('订单号:'||pd_ordercode||'订单序号:'||pd_orderdetno||'物料编号:'||pd_prodcode) from (select pd_ordercode,pd_orderdetno,pd_prodcode from prodiodetail left join saledetail on pd_ordercode=sd_code and pd_orderdetno=sd_detno where pd_piclass in ('出货单','销售退货单') and pd_prodcode <> sd_prodcode and pd_piid=?)",
						String.class, id);
		if (err != null) {
			BaseUtil.showError("料号与订单+序号料号不一致!" + err);
		}
		String message = prodInOutDao.checkSaleDetail(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 生产领料单、委外领料单：退料数量不能大于结存可退料数
	 */
	public void prodinout_pirnt_expbackqtyCheck(Integer id) {
		String message = prodInOutDao.checkexpbackqty(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 生产补料单：补料数量不能大于报废数
	 */
	public void prodinout_pirnt_addqtyCheck(Integer id) {
		String message = null;
		boolean bool = baseDao.isDBSetting("Make!Base", "allowAddDirectly");
		if (bool) {
			message = prodInOutDao.checkaddqty(id);
		}
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 生产领料单：对于未过账状态的单据要判断当前领料数是否超过工单剩余允许领料数
	 */
	public void prodinout_pirnt_getqtyCheck(Integer id) {
		String message = prodInOutDao.checkgetqty(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 完工入库单、完工不良入库单：对于未过账状态的单据要判断是否领足料,未领足料，不能完工
	 */
	public void prodinout_pirnt_kitsCheck(Integer id) {
		String message = prodInOutDao.checkkits(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 已删除状态的单据不能打印
	 */
	public void prodinout_print_delletednotAllowPrint(Integer id) {
		String message = prodInOutDao.delletednotAllowPrint(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 委外验收、委外验退单：币别与关联委外订单不一致不能过账
	 */
	public void prodinout_post_expcurrencyCheck(Integer id) {
		String message = prodInOutDao.expcurrencyCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 发货单、退货单：币别与关联销售订单是否一致
	 */
	public void prodinout_post_sacurrencyCheck(Integer id) {
		String message = prodInOutDao.sacurrencyCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 采购验收、验退：币别与关联采购订单是否一致
	 */
	public void prodinout_post_pucurrencyCheck(Integer id) {
		String message = prodInOutDao.pucurrencyCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 生产领料单、生产退料单、生产补料单：工单未审核、未批准，不允许出入库操作
	 */
	public void prodinout_common_makestatusCheck(Integer id) {
		String message = prodInOutDao.makestatusCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 销售拨出单：不能同时存在销售合同和预测单；预测单号或序号不存在
	 * ；不允许超预测单数量拨出；拨出物料必须与预测单物料一致；不能同时存在销售合同序号和预测单序号;
	 */
	public void prodinout_post_orderinfoCheck(Integer id) {
		String message = prodInOutDao.orderinfoCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 拨出单、销售拨出单：有值仓与无值仓之间不能相互调拨!
	 */
	public void prodinout_post_whcostCheck(Integer id) {
		String message = prodInOutDao.whcostCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 委外验收、验退：根据ordercode\orderdetno强行抓取委外单单价、税率
	 */
	public void prodinout_common_getexpPrice(Integer id) {
		prodInOutDao.getexpPrice(id);
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 采购验收、验退：根据ordercode\orderdetno强行抓取采购单单价、税率
	 */
	public void prodinout_common_getpuPrice(Integer id) {
		prodInOutDao.getpuPrice(id);
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 发货单、退货单：根据ordercode\orderdetno强行抓取订单税率(客户选用)
	 */
	public void prodinout_common_getsdTaxrate(Integer id) {
		prodInOutDao.getsdTaxrate(id);
	}

	/**
	 * reserve->prodInOut->print->before [提价、打印]--出库类单据:检测分仓库存是否足够
	 * 
	 * @author madan
	 */
	public void prodinout_common_qtyonhandCheck(Integer id) {
		String message = prodInOutDao.qtyonhandCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before [保存、打印]--结余退料单、生产耗料单：序号xx线别不能为空
	 * 
	 * @author madan
	 */
	public void prodinout_common_lineCheck(Integer id) {
		String message = prodInOutDao.lineCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before [保存、打印]--结余退料单、生产耗料单：序号xx车间不能为空
	 * 
	 * @author madan
	 */
	public void prodinout_common_plantCheck(Integer id) {
		String message = prodInOutDao.plantCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、打印]--销售拨出单：如果产品编号为空默认根据ordercode
	 * \orderdetno抓取；如果拨出数量为0默认根据ordercode\orderdetno抓取剩余未发货数
	 */
	public void prodinout_common_getqtyfromorder(Integer id) {
		prodInOutDao.getqtyfromorder(id);
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、打印]--生产退料单、拆件入库单：同一张单据中明细的责任部门必须一致，并且有效(在部门库中有效&存在)
	 */
	public void prodinout_common_departmentvalidCheck(Integer id) {
		String message = prodInOutDao.departmentvalidCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、打印]--生产退料单、拆件入库单：同一张单据中明细的退料原因必须一致，并且有效(在退料原因基础设置中存在)
	 */
	public void prodinout_common_descriptionvalidCheck(Integer id) {
		String message = prodInOutDao.descriptionvalidCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、打印]--生产退料单、拆件入库单：责任部门为空的明细行默认=当前单据明细中有填写的
	 */
	public void prodinout_save_departmentUpdate(Integer id) {
		prodInOutDao.departmentUpdate(id);
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、打印]--生产退料单、拆件入库单：退料原因为空的明细行默认=当前单据明细中有填写的
	 */
	public void prodinout_common_descriptionUpdate(Integer id) {
		prodInOutDao.descriptionUpdate(id);
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * [保存、提交、审核、过账]--其它出库、其它入库：业务类型不能为空
	 */
	public void prodinout_common_pitypeCheck(Integer id) {
		String message = prodInOutDao.pitypeCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 明细行中物料状态的检测
	 */
	public void prodinout_common_prstatusCheck(Integer id) {
		String message = prodInOutDao.prstatusCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan [保存、过账]--根据月度汇率表更新单据上的汇率
	 */
	public void prodinout_common_getcmrate(Integer id) {
		prodInOutDao.getcmrate(id);
	}

	/**
	 * reserve->prodInOut->delete->before author:madan 发货单：更新关联发货检验单的转入库状态(ADD)
	 */
	public void prodinout_delete_Sale_updateQCstatus(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail",
				new String[] { "pd_orderdetno", "pd_outqty", "pd_ordercode" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetail",
					"ve_statuscode='PARTOUT',ve_madeqty=ve_madeqty+" + Double.parseDouble(obj[1].toString()) + ", ve_status ='"
							+ BaseUtil.getLocalMessage("PARTOUT") + "'",
					"ve_ordercode='" + obj[2] + "' and ve_orderdetno = " + Integer.parseInt(obj[0].toString()));
			baseDao.updateByCondition("QUA_VerifyApplyDetail",
					"ve_statuscode='TURNOUT',ve_madeqty=vad_qty,ve_status ='" + BaseUtil.getLocalMessage("TURNOUT") + "'",
					"ve_madeqty>=vad_qty and ve_ordercode='" + obj[2] + "' and ve_orderdetno = " + Integer.parseInt(obj[0].toString()));
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 委外验收单、完工入库单：更新关联生产检验单的转入库状态
	 */
	public void prodinout_delete_MakeIn_updateQCstatus(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail",
				new String[] { "pd_orderdetno", "pd_inqty", "pd_ordercode" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetail",
					"ve_statuscode='PART2IN',ve_madeqty=ve_madeqty+" + Double.parseDouble(obj[1].toString()) + ", ve_status ='"
							+ BaseUtil.getLocalMessage("PART2IN") + "'", "ve_ordercode='" + obj[2] + "'");
			baseDao.updateByCondition("QUA_VerifyApplyDetail",
					"ve_statuscode='TURNIN',ve_madeqty=vad_qty,ve_status ='" + BaseUtil.getLocalMessage("TURNIN") + "'",
					"ve_madeqty>=vad_qty and ve_ordercode='" + obj[2] + "'");
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan 不良品入库单、采购验收单：更新关联检验单的转入库状态
	 */
	public void prodinout_delete_PurcCheckin_updateQCstatus(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail",
				new String[] { "pd_orderdetno", "pd_inqty", "pd_ordercode" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetail",
					"ve_statuscode='PART2IN',ve_madeqty=ve_madeqty+" + Double.parseDouble(obj[1].toString()) + ", ve_status ='"
							+ BaseUtil.getLocalMessage("PART2IN") + "'",
					"ve_ordercode='" + obj[2] + "' and ve_orderdetno = " + Integer.parseInt(obj[0].toString()));
			baseDao.updateByCondition("QUA_VerifyApplyDetail",
					"ve_statuscode='TURNIN',ve_madeqty=vad_qty,ve_status ='" + BaseUtil.getLocalMessage("TURNIN") + "'",
					"ve_madeqty>=vad_qty and ve_ordercode='" + obj[2] + "' and ve_orderdetno = " + Integer.parseInt(obj[0].toString()));
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan [打印、过账]--明细行中出+入库数量不能为0
	 */
	public void prodinout_post_pdqtyCheck(Integer id) {
		String message = prodInOutDao.pdqtyCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->print->before author:madan
	 * 生产退料单：(工单外退料)--1、对于明细行中工单号不为空，用料序号为0 &&
	 * 物料在工单号的用料表以及替代明细中不存在的数据,插入到ordercode用料表的后面maxdetno；
	 * 2、插入后更新makematerial(供应类型、工作中心、默认仓库)，同时更新当前pd_orderdetno; 3、记录日志
	 */
	public void prodinout_pirnt_addMaterial(Integer id) {
		prodInOutDao.addMaterial(id, "ProdInOut!Make!Return");
	}

	/**
	 * reserve->prodInOut->print->before author:madan 拨入单、销售拨入单：不能直接反过账
	 */
	public void prodinout_unpost_piclassCheck(Integer id) {
		String message = prodInOutDao.piclassCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * reserve->prodInOut->post->after 委外验收单过账后修改委外单完工状态
	 * 
	 * @author madan 修改：该方法已经不需要，已经将更新完工状态写在过账，反过账中调用
	 */
	public void prodinout_post_updatemakestatus(Integer id) {
		/*
		 * SqlRowList rs = baseDao.queryForRowSet(
		 * "SELECT pd_ordercode FROM ProdIODetail WHERE pd_piid=?", id); String
		 * ma_code = null; while (rs.next()) { ma_code =
		 * rs.getString("pd_ordercode"); baseDao.updateByCondition("Make",
		 * "ma_finishstatuscode='PARTFI', ma_finishstatus ='" +
		 * BaseUtil.getLocalMessage("PARTFI") + "'", "ma_code='" + ma_code +
		 * "' and ma_madeqty>0"); baseDao.updateByCondition("Make",
		 * "ma_finishstatuscode='COMPLETED', ma_finishstatus ='" +
		 * BaseUtil.getLocalMessage("COMPLETED") + "'", "ma_code='" + ma_code +
		 * "' AND ma_qty = ma_madeqty"); }
		 */
	}

	/**
	 * reserve->prodInOut->resPost->after 委外验收单反过账后修改委外单完工状态
	 * 
	 * @author madan 修改：该方法已经不需要，已经将更新完工状态写在过账，反过账中调用
	 */
	public void prodinout_respost_updatemakestatus(Integer id) {
		/*
		 * SqlRowList rs = baseDao.queryForRowSet(
		 * "SELECT pd_ordercode FROM ProdIODetail WHERE pd_piid=?", id); String
		 * ma_code = null; while (rs.next()) { ma_code =
		 * rs.getString("pd_ordercode"); baseDao.updateByCondition("Make",
		 * "ma_finishstatuscode='PARTFI', ma_finishstatus ='" +
		 * BaseUtil.getLocalMessage("PARTFI") + "'", "ma_code='" + ma_code +
		 * "' and nvl(ma_madeqty,0) > 0 and ma_qty <> ma_madeqty");
		 * baseDao.updateByCondition("Make",
		 * "ma_finishstatuscode='UNCOMPLET', ma_finishstatus ='" +
		 * BaseUtil.getLocalMessage("UNCOMPLET") + "',ma_madeqty=0", "ma_code='"
		 * + ma_code + "' AND ma_madeqty <= 0"); }
		 */
	}

	/**
	 * qc->iqc->audit->before IQC检验单明细送检数量之大于收料数量时，不允许审核
	 * 
	 * @author madan
	 */
	public void iqc_audit_checkqtyCheck(Integer id) {
		String message = quaVerifyApplyDetailDao.checkqtyCheck(id);
		if (message != null) {
			BaseUtil.showError(message);
		}
	}

	/**
	 * 出入库单：审核前， 成本科目的判断
	 * 
	 * @param id
	 */
	public void prodInout_beforeAudit_ProdIOCateSet(Integer id) {
		Object[] objects = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_type", "pi_departmentcode" },
				"pi_id=" + id);
		int count = baseDao.getCountByCondition("ProdIOCateSet", "pc_type='" + objects[1] + "' and pc_class='" + objects[0] + "'"
				+ " and pc_departmentcode='" + objects[2] + "' and pc_statuscode='AUDITED'");
		if (count != 1) {
			BaseUtil.showError("该部门不存在成本科目，不能进行当前操作！");
		}
	}

	/**
	 * reserve->warehouse->delete->before 已经有发生出入库的仓库资料，不能删除
	 * 
	 * @author madan
	 */
	public void warehouse_delete_isused(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("Warehouse", new String[] { "wh_code" }, "wh_id=" + id);
		boolean bool = baseDao.checkIf("ProdIODetail left join ProdInOut on pd_piid=pi_id", "(pd_whcode='" + objs[0] + "' or pd_inwhcode='"
				+ objs[0] + "' or pi_whcode='" + objs[0] + "')");
		if (bool) {
			BaseUtil.showError("已发生出入库的仓库资料，不能删除！");
		}
	}

	/**
	 * 更新销售明细价格
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 * @author madan
	 */
	public void sale_update_price(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		boolean bool = store.get("sa_getprice").toString().equals("-1");// 是否自动获取单价
		if (bool) {
			saleDao.getPrice(Integer.parseInt(store.get("sa_id").toString()));
		}
	}

	/**
	 * 销售单提交之前更新销售明细价格 sale->commit->before
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 * @author madan
	 */
	public void sale_commit_update_price(Integer id) {
		Object getprice = baseDao.getFieldDataByCondition("Sale", "sa_getprice", "sa_id=" + id);
		boolean bool = getprice.toString().equals("-1");// 是否自动获取单价
		if (bool) {
			saleDao.getPrice(id);
		}
	}

	/**
	 * 销售单提交之前更新销售明细价格 sale->commit->before
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 * @author madan
	 */
	public void sale_save_after_purprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object cukind = baseDao.getFieldDataByCondition("Sale left join customer on sa_custcode=cu_code", "cu_kind",
				"sa_id=" + store.get("sa_id"));
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("saledetail left join sale on sa_id=sd_said", new String[] {
				"sd_prodcode", "sa_currency", "sd_id" }, " sd_said=" + store.get("sa_id"));
		JSONObject js = null;
		for (Object[] obj : objects) {
			Object oqty = baseDao.getFieldDataByCondition("SaleDetail", "sum(sd_qty)", " sd_said=" + store.get("sa_id")
					+ " and sd_prodcode='" + String.valueOf(obj[0]) + "'");
			js = saleDao.getSalePuPrice(String.valueOf(cukind), String.valueOf(obj[0]), String.valueOf(obj[1]),
					Double.parseDouble(oqty.toString()));
			double price = 0;
			double ratio = 0;
			double p = 0;
			if (js != null) {
				price = js.getDouble("sd_purcprice");
				ratio = js.getDouble("sd_ratio");
			}
			if (price != 0) {
				p = price;
			} else {
				BaseUtil.showError("根据 产品编号:[" + obj[0] + "],定价类型:[" + cukind + "],币别:[" + obj[1] + "] 在销售定价未找到对应单价，或单价为空值、0等!<BR/>");
			}
			baseDao.updateByCondition("Saledetail", "sd_purcprice=" + p + ",sd_ratio=" + ratio, "sd_id=" + obj[2]);
		}
	}

	/**
	 * 销售单定价与销售类型不能为空！ scm-sale->commit->before
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 * @author madan
	 */
	public void sale_commit_salepricecheck(Integer id) {
		int countnum = baseDao
				.getCount("select count(*) from saledetail left join Product on sd_prodcode=pr_code where (nvl(sd_purcprice,0)=0 or nvl(sd_saletype,' ')=' ') and nvl(pr_dhzc,' ')='MPS' and sd_said="
						+ id);
		if (countnum > 0) {
			BaseUtil.showError("销售订单明细有为空的定价或者销售类型没有填写或者物料资料表计划类型不等于MPS！");
		}
	}

	/**
	 * 不良品入库单：反过账的数量不能超过不良品出库单数量！ scm-prodinout->resPost->before
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 * @author madan
	 */
	public void prodinout_resPost_qtyCheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("PordInOut", new String[] { "pi_sourcecode" }, "pi_id=" + id);
		SqlRowList rs = baseDao.queryForRowSet("select pi_id from ProdIODetail left join ProdInOut on pd_piid=pi_id where (pd_whcode='"
				+ objs[0] + "' or pd_inwhcode='" + objs[0] + "' or pi_whcode='" + objs[0] + "')");
		if (rs.next()) {
			BaseUtil.showError("已发生出入库的仓库资料，不能删除！");
		}
	}

	/**
	 * 采购/用品验退单：保存。更新时限制验退数量不能超过验收数量 scm-prodinout->save->before
	 * 
	 * @author madan
	 */
	public void prodinout_save_outqtyCheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		for (Map<Object, Object> s : gstore) {
			prodInOutDao.outqtyCheck(s.get("pd_id"), s.get("pd_ordercode"), s.get("pd_orderdetno"),
					Double.parseDouble(s.get("pd_outqty").toString()), s.get("pd_piclass"));
		}
	}

	public void prodiout_savebefore_checkdetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		for (Map<Object, Object> s : gstore) {
			prodInOutDao.checkProductcode(s.get("pd_ordercode"), s.get("pd_orderdetno"), s.get("pd_prodcode"));
		}
	}

	/**
	 * 采购/用品验退单：提交、过账时限制验退数量不能超过验收数量 scm-prodinout->commit->before
	 * 
	 * @author madan
	 */
	public void prodinout_commit_outqtyCheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT pd_id,pd_ordercode,pd_orderdetno,pd_outqty,pd_piclass FROM ProdIODetail WHERE pd_piid=?", id);
		while (rs.next()) {
			prodInOutDao.outqtyCheck(rs.getObject("pd_id"), rs.getObject("pd_ordercode"), rs.getObject("pd_orderdetno"),
					rs.getGeneralDouble("pd_outqty"), rs.getObject("pd_piclass"));
		}
	}

	/**
	 * 采购验退单：提交、过账时限制验退数量不能超过验收数量 scm-prodinout->commit->before
	 * 
	 * @author shenj
	 */
	public void prodinout_commit_currencyCheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("PordInOut", new String[] { "pi_currency", "pi_cardcode" }, "pi_id=" + id);
		Object vecurrency = baseDao.getFieldDataByCondition("vendor", "ve_currency", "ve_code='" + objs[1] + "'");
		if (!objs[0].equals(vecurrency)) {
			BaseUtil.showError("验退币别与供应商资料不一致！");
		}

	}

	/**
	 * 采购验退单：提交、过账采购验收明细要有采购单 scm-prodinout->commit->before
	 * 
	 * @author shenj
	 */
	public void prodinout_commit_orderCheck(Integer id) {

		int countnum = baseDao
				.getCount("select count(*) from prodinout left join prodiodetail on pi_id=pd_piid where nvl(pi_type,' ')<>'送样关联入库' and nvl(pi_class,' ')='采购验收单' and nvl(pd_ordercode,' ')=' ' and pi_id="
						+ id);
		if (countnum > 0) {
			BaseUtil.showError("没有采购单，不能验收！");
		}
	}

	/**
	 * 销售变更单新单价不能为0 scm-salechange->commit->before
	 * 
	 * @author madan
	 */
	public void salechange_commit_priceCheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT scd_detno FROM salechangedetail WHERE scd_scid=? and nvl(scd_newprice,0) = 0 and nvl(SCD_PRICE,0)<>0", id);
		while (rs.next()) {
			BaseUtil.showError("行号[" + rs.getInt("scd_detno") + "]中的新单价不能为0！");
		}
	}

	/**
	 * 费用报销实际金额不能为0 scm-salechange->commit->before
	 * 
	 * @author madan
	 */
	public void feeplease_commit_totalCheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT fpd_detno FROM feepleasedetail WHERE fpd_fpid=? and nvl(fpd_total,0) = 0", id);
		while (rs.next()) {
			BaseUtil.showError("行号[" + rs.getInt("fpd_detno") + "]中的实际金额不能为0！");
		}
	}

	/**
	 * 判断拜访报告的录入是否被分配给报告上的客户了
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void visitRecord_cust_recordor(Integer id) {
		Object custcode = baseDao.getFieldDataByCondition("VisitRecord", "vr_cuuu", "vr_id=" + id);
		Object recordman = baseDao.getFieldDataByCondition("VisitRecord", "vr_recorder", "vr_id=" + id);
		int count = 0;
		count = baseDao.getCount("select count(*) from CustomerDistr left join customer on cd_cuid=cu_id where cu_code='"
				+ custcode.toString() + "' and cd_seller='" + recordman.toString() + "'");
		if (count > 0) {

		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.cust_recordor"));
		}
	}

	/**
	 * 费用报销实际金额不能为0 scm-salechange->commit->before
	 * 
	 * @author madan
	 */
	public void feeplease_commit_cltotalCheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT fpd_detno FROM feepleasedetail WHERE fpd_fpid=? and nvl(fpd_n7,0) = 0", id);
		while (rs.next()) {
			BaseUtil.showError("行号[" + rs.getInt("fpd_detno") + "]中的金额不能为0！");
		}
	}

	/**
	 * 过账后扣除待检数量、收料数量 scm->prodinout->post->after
	 * 
	 * @author madan
	 */
	public void prodinout_post_updatesourceqty(Integer id) {
		Object[] piclass = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_type" }, "pi_id=" + id);
		if (!(piclass[0].toString().equals("采购验收单") || piclass[0].toString().equals("委外验收单") || piclass[0].toString().equals("不良品入库单"))) {
			return;
		}
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("prodioDetail", new String[] { "pd_orderdetno", "pd_ordercode",
				"sum(pd_inqty)", "pd_prodcode" }, "pd_piid=" + id + " and pd_qcid>0 group by pd_ordercode, pd_orderdetno, pd_prodcode");
		for (Object[] obj : objs) {
			if ("采购验收单".equals(piclass[0].toString())) {
				baseDao.updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)-" + obj[2]
						+ ",pd_totested=NVL(pd_totested,0)-" + obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
			} else if ("委外验收单".equals(piclass[0].toString()) || "完工入库单".equals(piclass[0].toString())) {
				baseDao.updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)-" + obj[2] + ",ma_totested=NVL(ma_totested,0)-"
						+ obj[2], "ma_code='" + obj[1] + "'");
			} else if ("不良品入库单".equals(piclass[0].toString())) {
				if ("PURC".equals(piclass[1].toString()) || "采购不良品入库".equals(piclass[1].toString())) {
					baseDao.updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)-" + obj[2]
							+ ",pd_totested=NVL(pd_totested,0)-" + obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
				} else {
					baseDao.updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)-" + obj[2] + ",ma_totested=NVL(ma_totested,0)-"
							+ obj[2], "ma_code='" + obj[1] + "'");
				}
			}
			baseDao.updateByCondition("Product", "pr_reconhand = nvl(pr_reconhand,0)-" + obj[2] + ",pr_totested=NVL(pr_totested,0)-"
					+ obj[2], "pr_code='" + obj[3] + "'");
		}
	}

	/**
	 * 反过账后返回待检数量、收料数量 scm->prodinout->resPost->after
	 * 
	 * @author madan
	 */
	public void prodinout_resPost_updatesourceqty(Integer id) {
		Object[] piclass = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_type" }, "pi_id=" + id);
		if (!(piclass[0].toString().equals("采购验收单") || piclass[0].toString().equals("委外验收单") || piclass[0].toString().equals("不良品入库单"))) {
			return;
		}
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("prodioDetail", new String[] { "pd_orderdetno", "pd_ordercode",
				"sum(pd_inqty)", "pd_prodcode" }, "pd_piid=" + id + " and pd_qcid>0 group by pd_ordercode, pd_orderdetno, pd_prodcode");
		for (Object[] obj : objs) {
			if ("采购验收单".equals(piclass[0].toString())) {
				baseDao.updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)+" + obj[2]
						+ ",pd_totested=NVL(pd_totested,0)+" + obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
			} else if ("委外验收单".equals(piclass[0].toString()) || "完工入库单".equals(piclass[0].toString())) {
				baseDao.updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)+" + obj[2] + ",ma_totested=NVL(ma_totested,0)+"
						+ obj[2], "ma_code='" + obj[1] + "'");
			} else if ("不良品入库单".equals(piclass[0].toString())) {
				if ("采购不良品入库".equals(piclass[1].toString())) {
					baseDao.updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)+" + obj[2]
							+ ",pd_totested=NVL(pd_totested,0)+" + obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
				} else {
					baseDao.updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)+" + obj[2] + ",ma_totested=NVL(ma_totested,0)+"
							+ obj[2], "ma_code='" + obj[1] + "'");
				}
			}
			baseDao.updateByCondition("Product", "pr_reconhand = nvl(pr_reconhand,0)+" + obj[2] + ",pr_totested=NVL(pr_totested,0)+"
					+ obj[2], "pr_code='" + obj[3] + "'");
		}
	}

	/**
	 * 出货单，其它出库单，换货出库单：审核之后自动冲销 reserve->prodInOut->audit->after
	 * 
	 * @author madan
	 */
	public void prodinout_audit_CreateSaleClash(Integer id) {
		saleClashService.createSaleClash(id, "ProdInOut");
	}

	/**
	 * 出货单，其它出库单，换货出库单：反审核之后自动删除生成的冲销 reserve->prodInOut->resAudit->after
	 * 
	 * @author madan
	 */
	public void prodinout_resAudit_CancelSaleClash(Integer id) {
		saleClashService.cancelSaleClash(id, "ProdInOut");
	}

	/**
	 * Qua_verifyApplyDetailDet->deletedetail->before 明细资料有[已入库]的单据,不能删除!
	 */
	public void quaverifyapplydetail_deletedetail_before(Integer ved_id) {
		int count = baseDao.getCountByCondition("ProdIODetail", "pd_qcid in (select ved_id from qua_verifyapplydetaildet where ved_id="
				+ ved_id + ")");
		if (count > 0) {
			BaseUtil.showError("明细行已入库，不允许删除!");
		}
	}

	/**
	 * 销售退货单：删除后，还原退货申请单状态 reserve->prodInOut->saleReturn->delete->after
	 */
	public void prodInOut_saleReturn_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_radid", "nvl(pd_inqty,0)" }, "pd_piid="
				+ id);
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
				baseDao.updateByCondition("ReturnApplyDetail", "rad_yqty=nvl(rad_yqty,0)-" + obj[1], "rad_id=" + obj[0]);
				returnApplyDao.checkRADQty(Integer.parseInt(obj[0].toString()));
			}
		}
	}

	/**
	 * 销售退货单：删除明细后，还原退货申请单状态 reserve->prodInOut->saleReturn->delete->after
	 */
	public void prodInOut_saleReturn_deletedetail(Integer id) {
		Object[] obj = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_radid", "nvl(pd_inqty,0)" }, "pd_id=" + id);
		if (obj != null && obj[0] != null) {
			baseDao.updateByCondition("ReturnApplyDetail", "rad_yqty=nvl(rad_yqty,0)-" + obj[1], "rad_id=" + obj[0]);
			returnApplyDao.checkRADQty(Integer.parseInt(obj[0].toString()));
		}
	}

	/**
	 * 销售退货单：修改后，修改数量要反馈到退货申请单
	 * 
	 * @author madan 2015-1-5 09:34:28
	 */
	static final String CHECK_RA_YQTY = "SELECT ra_code,rad_detno,rad_qty FROM ReturnApplyDetail left join ReturnApply on rad_raid=ra_id WHERE rad_id=? and rad_qty<?";

	public void salereturn_update_before_replayqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;// 销售退货单明细ID
		Object qty = null;
		Integer rad_id = null;// 销售退货单明细ID
		double tQty = 0;// 销售退货单修改数量
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(s.get("pd_inqty").toString());
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_radid,0)", "nvl(pd_inqty,0)",
						"pd_ordercode" }, "pd_id=" + pdid + " and nvl(pd_radid,0)<>0 and pd_piclass='销售退货单'");
				if (objs != null && objs[0] != null) {
					rad_id = Integer.parseInt(String.valueOf(objs[0]));
					if (rad_id != null && rad_id > 0) {
						qty = baseDao.getFieldDataByCondition("ProdIoDetail", "sum(nvl(pd_inqty,0))", "pd_radid=" + rad_id
								+ " AND pd_piclass='销售退货单' and pd_id <>" + pdid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(CHECK_RA_YQTY, rad_id, Double.parseDouble(qty.toString()) + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],申请单号:").append(rs.getString("ra_code")).append(",行号:")
									.append(rs.getInt("rad_pdno")).append(",数量:").append(rs.getDouble("rad_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						// 申请单
						baseDao.updateByCondition("ReturnApplyDetail", "rad_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"rad_id=" + rad_id);
						baseDao.updateByCondition("ReturnApplyDetail", "rad_yqty=0", "rad_yqty<=0 and rad_id=" + rad_id);
						Integer raid = baseDao.queryForObject("select nvl(rad_raid,0) from ReturnApplyDetail where rad_id=?",
								Integer.class, objs[0]);
						if (raid != null && raid > 0) {
							int count = baseDao.getCountByCondition("ReturnApplyDetail", "rad_raid=" + raid);
							int yCount = baseDao.getCountByCondition("ReturnApplyDetail", "rad_raid=" + raid
									+ " and nvl(rad_yqty,0)=rad_qty");
							int xCount = baseDao.getCountByCondition("ReturnApplyDetail", "rad_raid=" + raid + " and nvl(rad_yqty,0)=0");
							String status = "PARTSR";
							if (yCount == count) {
								status = "TURNSR";
							}
							if (xCount == count) {
								status = "";
							}
							baseDao.updateByCondition("ReturnApply",
									"ra_turnstatuscode='" + status + "',ra_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "ra_id="
											+ raid);
						}
					}
				}
			}
		}
	}

	/**
	 * @KBT 出货单过账后，只允许超级账号打印该单据
	 * @author yingp
	 */
	public void prodio_print_onlyadmin(Integer id) {
		boolean isPosted = baseDao.checkIf("ProdInOut", "pi_id=" + id + " AND pi_statuscode='POSTED'");
		if (isPosted && !"admin".equals(SystemSession.getUser().getEm_type())) {
			BaseUtil.showError("该单据已过账，只允许管理员打印!");
		}
	}

	/**
	 * @KBT 出货单库存不够，不允许操作/拨出仓库存数<拨出数量，不允许操作
	 *      个别pi_type不限制的在setting表配置项PiTypeNotCheckOnHand
	 * @author yingp
	 */
	public void prodio_commit_onhand(Integer id) {
		// 按明细物料、仓库分组
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select WM_CONCAT('<br>明细行'||pdno||'物料'||pd_prodcode) from (select WM_CONCAT(pd_pdno) pdno,pd_prodcode,pd_whcode,sum(pd_outqty) qty from prodiodetail left join prodinout on pd_piid=pi_id where pd_piid=? and NVL(pi_type,' ') not in (select se_value from setting where se_what='PiTypeNotCheckOnHand') group by pd_prodcode,pd_whcode) where qty > nvl((select pw_onhand from productwh where pw_prodcode=pd_prodcode and pw_whcode=pd_whcode),0)",
						id);
		if (rs.next()) {
			String result = rs.getString(1);
			if (result != null && result.length() > 0) {
				BaseUtil.showError("库存不够，不允许执行操作:" + result);
			}
		}
	}

	/**
	 * 采购验收单：提交时,限制pi_whcode和pd_whcode都必须是采购类型里面设置的默认仓库
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void prodio_purcin_commit_whcode(Integer id) {
		String sql = "select WM_CONCAT('明细行:'||pd_pdno||'仓库'||pd_whcode||'与采购类型默认仓库'||wh_code||'不一致  ') str from prodiodetail left join purchase on pu_code=prodiodetail.pd_ordercode left join PurchaseKind on pu_kind=pk_name left join Warehouse on wh_id=pk_whid where pd_piid=? AND pd_piclass='采购验收单' and nvl(pk_whid, 0)<>0 and pd_whcode<>wh_code";
		SqlRowList rs = baseDao.queryForRowSet(sql, id);
		if (rs.next() && rs.getString(1) != null) {
			BaseUtil.showError(rs.getString(1));
		}
	}

	/**
	 * 检查料号重复、规格名称重复
	 * 
	 * @param pre_id
	 * @param language
	 * @param employee
	 * @author zhongyl
	 */
	public void PreProduct_prodcodecheck(Integer pre_id) {
		Object[] objs = baseDao.getFieldsDataByCondition("preproduct", "pre_code,pre_detail||pre_spec,pre_thisid", "pre_id='" + pre_id
				+ "'");
		if (objs != null && !objs[0].equals("")) {
			String sql = "select pre_thisid from preproduct where pre_id<>" + pre_id + " and pre_code='" + objs[0].toString() + "' ";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料编号已经被其它申请单使用，申请单号：" + rs.getString(1));
			}
			sql = "select pre_thisid from preproduct where pre_id<>" + pre_id + " and pre_detail||pre_spec='" + objs[1].toString()
					+ "' and (pre_spec<>' ' or pre_detail<>' ') ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料名称规格已经被其它申请单使用，申请单号：" + rs.getString(1));
			}
			sql = "select pr_code from product where NVL(pr_sourcecode,' ')<>'" + objs[2].toString() + "' and pr_code='"
					+ objs[0].toString() + "' ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料编号已经存在于物料库");
			}
			sql = "select pr_code from product where pr_sourcecode<>'" + objs[2].toString() + "' and pr_detail||pr_spec='"
					+ objs[1].toString() + "' and  (pr_spec<>' ' or pr_detail<>' ') ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("此物料名称规格已经存在于物料库");
			}
		}
	}

	/**
	 * 新物料保存限制物料号不能重复
	 * */
	public void PreProduct_checkProduct(HashMap<String, Object> map, Employee employee) {
		Object pre_id = map.get("pre_id");
		Object[] objs = baseDao.getFieldsDataByCondition("preproduct", "pre_code,pre_detail||pre_spec,pre_thisid", "pre_id='" + pre_id
				+ "'");
		if (objs != null && !objs[0].equals("")) {
			String sql = "select pre_thisid from preproduct where pre_id<>" + pre_id + " and pre_code='" + objs[0].toString() + "' ";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料编号已经被其它申请单使用，申请单号：" + rs.getString(1));
			}
			sql = "select pre_thisid from preproduct where pre_id<>" + pre_id + " and pre_detail||pre_spec='" + objs[1].toString()
					+ "' and (pre_spec<>' ' or pre_detail<>' ') ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料名称规格已经被其它申请单使用，申请单号：" + rs.getString(1));
			}
			sql = "select pr_code from product where NVL(pr_sourcecode,' ')<>'" + objs[2].toString() + "' and pr_code='"
					+ objs[0].toString() + "' ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("物料编号已经存在于物料库");
			}
			sql = "select pr_code from product where pr_sourcecode<>'" + objs[2].toString() + "' and pr_detail||pr_spec='"
					+ objs[1].toString() + "' and  (pr_spec<>' ' or pr_detail<>' ') ";
			rs = baseDao.queryForRowSet(sql);
			if (rs.next() && rs.getString(1) != null) {
				BaseUtil.showError("此物料名称规格已经存在于物料库");
			}
		}
	}

	/**
	 * reserve->prodInOut->DefectOut->delete->before 入库转出库：删除时还原入库单数量
	 * 
	 * @author mad
	 */
	public void prodInOut_DefectOut_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_outqty" }, "pd_piid=" + id
				+ " and nvl(pd_ioid,0)<>0");
		if (objs != null) {
			for (Object[] obj : objs) {
				if (obj != null && obj[0] != null) {
					baseDao.updateByCondition("ProdIoDetail", "pd_yqty=NVL(pd_yqty,0)-" + Double.parseDouble(String.valueOf(obj[1])),
							"pd_id=" + obj[0]);
					baseDao.updateByCondition("ProdIoDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + obj[0]);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->DefectOut->deletedetail->before 入库转出库：删除明细时还原入库单明细数量
	 * 
	 * @author mad
	 */
	public void prodInOut_DefectOut_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_outqty" }, "pd_id=" + id
				+ " and nvl(pd_ioid,0)<>0");
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("ProdIoDetail", "pd_yqty=nvl(pd_yqty,0)-(" + objs[1] + ")", "pd_id=" + objs[0]);
			baseDao.updateByCondition("ProdIoDetail", "pd_yqty=0", "nvl(pd_yqty,0)<=0 and pd_id=" + objs[0]);
		}
	}

	/**
	 * reserve->prodInOut->delete->before 出库转入库：删除时还原出库单数量
	 * 
	 * @author mad 2014-10-14 15:21:31
	 */
	public void prodInOut_OutIn_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_inqty" }, "pd_piid=" + id
				+ " and nvl(pd_ioid,0)<>0");
		if (objs != null) {
			for (Object[] obj : objs) {
				if (obj != null && obj[0] != null) {
					baseDao.updateByCondition("ProdIoDetail",
							"pd_yqty=nvl(pd_yqty,0)-(" + Double.parseDouble(String.valueOf(obj[1])) + ")", "pd_id=" + obj[0]);
					baseDao.updateByCondition("ProdIoDetail", "pd_yqty=0", "nvl(pd_yqty,0)<=0 and pd_id=" + obj[0]);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->deletedetail->before 出库转入库：删除明细时还原出库单明细数量
	 * 
	 * @author mad 2014-10-14 15:21:34
	 */
	public void prodInOut_OutIn_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_inqty" }, "pd_id=" + id
				+ " and nvl(pd_ioid,0)<>0");
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("ProdIoDetail", "pd_yqty=nvl(pd_yqty,0)-(" + objs[1] + ")", "pd_id=" + objs[0]);
			baseDao.updateByCondition("ProdIoDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + objs[0]);
		}
	}

	/**
	 * 入库转出库：出库单明细数量修改前，更新来源入库单已转出数量 reserve->prodInOut->DefectOut->save->before
	 * 
	 * @author mad
	 */
	public void prodInOut_DefectOut_savedetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;
		Object qty = null;
		Object aq = 0;
		double tQty = 0;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(String.valueOf(s.get("pd_outqty")));
			Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_outqty" }, "pd_id=" + pdid
					+ " and nvl(pd_ioid,0) <>0");
			if (objs != null) {
				qty = baseDao.getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)", "pd_ioid=" + objs[0] + " AND pd_id <>" + pdid);
				aq = baseDao.getFieldDataByCondition("ProdIODetail", "pd_inqty", "pd_id=" + objs[0]);
				qty = qty == null ? 0 : qty;
				aq = aq == null ? 0 : aq;
				if (Double.parseDouble(String.valueOf(aq)) < Double.parseDouble(String.valueOf(qty)) + tQty) {
					BaseUtil.showError("出库数量超出入库数,超出数量:"
							+ (Double.parseDouble(String.valueOf(qty)) + tQty - Double.parseDouble(String.valueOf(aq))));
				}
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty), "pd_id=" + objs[0]);
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + objs[0]);
			}
		}
	}

	/**
	 * 出库转入库：入库单明细数量修改前，更新来源出库单已转出数量 reserve->prodInOut->DefectIn->save->before
	 * 
	 * @author mad
	 */
	public void prodInOut_DefectIn_savedetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;
		Object qty = null;
		Object aq = 0;
		double tQty = 0;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(String.valueOf(s.get("pd_inqty")));
			Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_ioid", "pd_inqty" }, "pd_id=" + pdid
					+ " and nvl(pd_ioid,0) <>0");
			if (objs != null) {
				qty = baseDao.getFieldDataByCondition("ProdIODetail", "sum(pd_inqty)", "pd_ioid=" + objs[0] + " AND pd_id <>" + pdid);
				aq = baseDao.getFieldDataByCondition("ProdIODetail", "pd_outqty", "pd_id=" + objs[0]);
				qty = qty == null ? 0 : qty;
				aq = aq == null ? 0 : aq;
				if (Double.parseDouble(String.valueOf(aq)) < Double.parseDouble(String.valueOf(qty)) + tQty) {
					BaseUtil.showError("入库数量超出出库数,超出数量:"
							+ (Double.parseDouble(String.valueOf(qty)) + tQty - Double.parseDouble(String.valueOf(aq))));
				}
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty), "pd_id=" + objs[0]);
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + objs[0]);
			}
		}
	}

	/**
	 * Sale->sale->commit 销售订单提交时中PMC回复日期赋值为交货日期!
	 */
	public void sale_commit_updatePMCDate(Integer id) {
		baseDao.updateByCondition("SaleDetail", "sd_pmcdate=sd_delivery", "sd_said=" + id);
	}

	/**
	 * Purchase->vendor->banned->after 供应商禁用之后，将价格库中使用该供应商价格失效
	 */
	public void vendor_banned_after_abatePurchasePrice(Integer id) {
		baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_statuscode='UNVALID',ppd_remark='供应商失效',ppd_status='"
				+ BaseUtil.getLocalMessage("UNVALID") + "' where ppd_vendcode=(select ve_code from vendor where ve_id=" + id
				+ ") and nvl(ppd_statuscode,' ')='VALID'");
		if (baseDao.isDBSetting("vendorRate")) {
			baseDao.execute("update productvendorrate set pv_action='无效' where pv_vendcode in (select ve_code from vendor where ve_id="
					+ id + ")");
		}
	}

	/**
	 * 客户资料维护录入之后去平台匹配获取UU号
	 */
	public void updateCustomerGetUU(HashMap<Object, Object> store) {
		String res = baseDao.getEnterpriseUU(String.valueOf(store.get("cu_name")));
		if (res != null && res.length() > 0) {
			baseDao.updateByCondition("customer", "cu_uu='" + res + "'", " cu_code='" + String.valueOf(store.get("cu_code")) + "'");
		}
	}

	/**
	 * 供应商基本资料维护之后去平台匹配获取UU号
	 */
	public void updateVendorgetUU(HashMap<Object, Object> store) {
		String res = baseDao.getEnterpriseUU(String.valueOf(store.get("ve_name")));
		if (res != null && res.length() > 0) {
			baseDao.updateByCondition("vendor", "ve_uu='" + res + "'", " ve_code='" + String.valueOf(store.get("ve_code")) + "'");
		}
	}

	/**
	 * 供应商引进更新资料之后去平台匹配获取UU号
	 */
	public void updateVendorCheckUU(HashMap<Object, Object> store) {
		String res = baseDao.getEnterpriseUU(String.valueOf(store.get("ve_name")));
		if (res != null && res.length() > 0) {
			baseDao.updateByCondition("prevendor", "ve_uu='" + res + "'", " ve_code='" + String.valueOf(store.get("ve_code")) + "'");
		}
	}

	/**
	 * 客户预录入更新资料之后去平台匹配获取UU号
	 */
	public void updateCustCheckUU(HashMap<Object, Object> store) {
		String res = baseDao.getEnterpriseUU(String.valueOf(store.get("cu_name")));
		if (res != null && res.length() > 0) {
			baseDao.updateByCondition("precustomer", "cu_uu=" + res + " ", " cu_code='" + String.valueOf(store.get("cu_code")) + "'");
		}
	}

	/**
	 * 询价单审核之前逻辑限制 限制 必须有这个操作 不然不让提交
	 * */
	public void Inquiry_check_CanExecute(Integer id) {
		int count = baseDao.getCountByCondition("InquiryDetail", " nvl(id_price,0)>0 and id_isagreed=1 and id_inid=" + id);
		if (count > 0) {
			// 没做任何操作 不允许执行任务
			BaseUtil.showError("请判定是否转入价格库!");
		}

	}

	/**
	 * 报价公共询价单审核之前逻辑限制 限制 必须有这个操作 不然不让提交
	 * */
	public void InquiryAuto_check_CanExecute(Integer id) {
		Object in_pdstatus = baseDao.getFieldDataByCondition("InquiryAuto", "in_pdstatus", "in_id=" + id);
		if (!StringUtil.hasText(in_pdstatus)) {
			// 没做任何操作 不允许执行任务
			BaseUtil.showError("请先进行最终判定(点击最终采纳判定按钮)!");
		}

	}

	/**
	 * 报价询价单审核之前逻辑限制 限制 必须有这个操作 不然不让提交
	 * */
	public void InquiryOther_check_CanExecute(Integer id) {
		Object in_pdstatus = baseDao.getFieldDataByCondition("Inquiry", "in_pdstatus", "in_id=" + id);
		if (!StringUtil.hasText(in_pdstatus)) {
			// 没做任何操作 不允许执行任务
			BaseUtil.showError("请先进行最终判定(点击最终采纳判定按钮)!");
		}
	}

	/**
	 * 料号审批之前判断 料号是否重复
	 * */
	public void Prcode_check_CanExecute(Integer id) {
		Object prcode = baseDao.getFieldDataByCondition("PreProduct", "pre_code", "pre_id=" + id);
		if (prcode != null) {
			boolean bool = baseDao.checkIf("Product", "pr_code='" + prcode + "'");
			if (bool)
				BaseUtil.showError("当前料号已转入过物料库!");
			else {
				bool = baseDao.checkIf("PreProduct", "pre_code='" + prcode + "' and pre_id<>" + id);
				if (bool)
					BaseUtil.showError("当前申请的料号在其他申请单号中已存在!");
			}
		}
		/* 取setting表查看取价取限制原则，看是否需不需剔除某些字段 */
		String prcodenocotain = baseDao.getDBSetting("PrePrcodeContain");
		if (prcodenocotain.length() > 0) {
			String sqlstr = null;
			String arr[] = prcodenocotain.split(",");
			if (arr.length > 1) {
				for (int i = 0; i <= arr.length - 1; i++) {
					if (i == 0) {
						sqlstr = "(pre_code like '%" + arr[i] + "%' or";
					} else if (i == arr.length - 1) {
						sqlstr = sqlstr + " pre_code like '%" + arr[i] + "%')";
					} else {
						sqlstr = sqlstr + " pre_code like '%" + arr[i] + "%' or";
					}
				}
			} else {
				sqlstr = "(pre_code like '%" + arr[0] + "%' ) ";
			}
			sqlstr = "select count(*) from preproduct where " + sqlstr + " and pre_code='" + prcode + "'";
			int countnum = baseDao.getCount(sqlstr);
			if (countnum > 0) {
				BaseUtil.showError("当前申请的料号中包含了剔除了的字符" + prcodenocotain + "!");
			}
		}

	}

	/**
	 * 华强联合 物料资料审批 通过审批流走完的 状态标示已认定 页面审核的智能是未认定的
	 * */
	public void Affirm_Product(Integer id) {
		baseDao.updateByCondition("Product", "pr_affirm='已认定'", "pr_id=" + id);
	}

	/**
	 * 出入库单更新，过账，反过账之前判断仓管员是否有效
	 * 
	 * @author madan
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void prodinout_check_warehouseman(Integer id) {
		boolean bool = baseDao.checkIf(
				"WarehouseMan left join Warehouse on wm_whid=wh_id left join prodinout on pi_whcode=wh_code and pi_cgycode=wm_cgycode",
				"pi_id=" + id);
		if (!bool) {
			BaseUtil.showError("仓库仓管员不对应!");
		}
	}

	/**
	 * 委外退料原因不能为空 zhongyl 2013 10 23
	 * */
	public void ProdIO_MakeReturnReasonNotNull(Integer id) {
		int count = baseDao.getCountByCondition("prodiodetail", "pd_piid=" + id
				+ " and NVL(pd_description,' ') not in ('制程不良','来料不良','良品退仓') ");
		if (count > 0) {
			BaseUtil.showError("退料原因填写错误!");
		}
	}

	/**
	 * 退料原因默认为和有填写原因的行一致 zhongyl 2013 10 23
	 * */
	public void ProdIO_MakeReturnReasonDefault(Integer id) {
		baseDao.execute("update prodiodetail set pd_description=(select max(NVL(pd_description,' ')) from prodiodetail where pd_piid='"
				+ id + "')where pd_piid='" + id + "' and NVL(pd_description,' ')not in ('制程不良','来料不良','良品退仓')  ");
	}

	/**
	 * 采购验收单：删除前，还原采购单,收料通知单的数据 reserve->prodInOut->CheckIn->delete->before
	 * 
	 * @author madan
	 */
	public void prodInOut_purchase_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_inqty,0)",
				"nvl(pd_qcid,0)", "pd_vacode", "nvl(pd_anid,0)", "nvl(pd_purcinqty,0)" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
				if ((obj[2] == null || Integer.parseInt(String.valueOf(obj[2])) == 0)
						&& (obj[4] == null || Integer.parseInt(String.valueOf(obj[4])) == 0) && (obj[3] == null || "".equals(obj[3]))) {// 来源不是检验单，收料单和收料通知单
					baseDao.updateByCondition("PurchaseDetail", "pd_yqty=NVL(pd_yqty,0)-" + obj[1], "pd_id=" + obj[0]);
					baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + obj[0]);
					/**
					 * 双单位 采购验收单删除明细更新采购单的采购单位已转数
					 */
					baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=NVL(pd_ypurcqty,0)-" + obj[5], "pd_id=" + obj[0]);
					baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=0", "NVL(pd_ypurcqty,0)<=0 and pd_id=" + obj[0]);

					baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "pd_yqty=0 and pd_id=" + obj[0]);
					baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'",
							"NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0) <> 0 and pd_id=" + obj[0]);
					baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'",
							"NVL(pd_yqty,0) < pd_qty and NVL(pd_yqty,0) <> 0 and pd_id=" + obj[0]);
					Integer pu_id = baseDao
							.queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class, obj[0]);
					if (pu_id != null && pu_id > 0) {
						int total = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id);
						int aud = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id + " AND NVL(pd_yqty,0)=0");
						int turn = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id + " AND NVL(pd_yqty,0)=pd_qty");
						String status = "PART2IN";
						if (aud == total) {
							status = "";
						} else if (turn == total) {
							status = "TURNIN";
						}
						baseDao.updateByCondition("Purchase",
								"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_id="
										+ pu_id);
					}
				} else if (obj[4] != null && Integer.parseInt(String.valueOf(obj[4])) != 0) {// 来源收料通知单
					baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=and_yqty-" + obj[1], "and_id=" + obj[4]);
					baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + obj[4]);
					Integer an_id = baseDao.queryForObject("select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?", Integer.class,
							obj[4]);
					if (an_id != null && an_id > 0) {
						int total = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id);
						int aud = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id + " AND and_yqty=0");
						int turn = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id + " AND and_yqty=and_inqty");
						String status = "PART2IN";
						if (aud == total) {
							status = "AUDITED";
						} else if (turn == total) {
							status = "TURNIN";
						}
						baseDao.updateByCondition("AcceptNotify",
								"an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "'", "an_id=" + an_id);
					}
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->purchase->deletedetail->before
	 * 采购验收单：明细删除前，还原采购单,收料通知单数据
	 * 
	 * @author madan
	 */
	public void prodInOut_purchase_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_inqty,0)",
				"nvl(pd_qcid,0)", "pd_vacode", "nvl(pd_anid,0)", "nvl(pd_purcinqty,0)" }, "pd_id=" + id + " and nvl(pd_orderid,0)<>0");
		if (objs != null && objs[0] != null) {
			if ((objs[2] == null || Integer.parseInt(String.valueOf(objs[2])) == 0)
					&& (objs[4] == null || Integer.parseInt(String.valueOf(objs[4])) == 0) && (objs[3] == null || "".equals(objs[3]))) {// 来源不是检验单和收料单
				baseDao.updateByCondition("PurchaseDetail", "pd_yqty=NVL(pd_yqty,0)-" + objs[1], "pd_id=" + objs[0]);
				baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + objs[0]);
				/**
				 * 双单位 采购验收单删除明细更新采购单的采购单位已转数
				 */
				baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=NVL(pd_ypurcqty,0)-" + objs[5], "pd_id=" + objs[0]);
				baseDao.updateByCondition("PurchaseDetail", "pd_ypurcqty=0", "NVL(pd_ypurcqty,0)<=0 and pd_id=" + objs[0]);

				baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "NVL(pd_yqty,0)=0 and pd_id=" + objs[0]);
				baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'",
						"NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0) <> 0 and pd_id=" + objs[0]);
				baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'",
						"NVL(pd_yqty,0) < pd_qty and NVL(pd_yqty,0) <> 0 and pd_id=" + objs[0]);
				Integer pu_id = baseDao.queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class, objs[0]);
				if (pu_id != null && pu_id > 0) {
					int total = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id);
					int aud = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id + " AND NVL(pd_yqty,0)=0");
					int turn = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + pu_id + " AND NVL(pd_yqty,0)=pd_qty");
					String status = "PART2IN";
					if (aud == total) {
						status = "";
					} else if (turn == total) {
						status = "TURNIN";
					}
					baseDao.updateByCondition("Purchase",
							"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_id=" + pu_id);
				}
			} else if (objs[4] != null && Integer.parseInt(String.valueOf(objs[4])) != 0) {// 来源是收料通知单
				baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=and_yqty-" + objs[1], "and_id=" + objs[4]);
				baseDao.updateByCondition("AcceptNotifyDetail", "and_yqty=0", "and_yqty<=0 and and_id=" + objs[4]);
				Integer an_id = baseDao.queryForObject("select nvl(and_anid,0) from AcceptNotifyDetail where and_id=?", Integer.class,
						objs[4]);
				if (an_id != null && an_id > 0) {
					int total = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id);
					int aud = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id + " AND and_yqty=0");
					int turn = baseDao.getCountByCondition("AcceptNotifyDetail", "and_anid=" + an_id + " AND and_yqty=and_inqty");
					String status = "PART2IN";
					if (aud == total) {
						status = "AUDITED";
					} else if (turn == total) {
						status = "TURNIN";
					}
					baseDao.updateByCondition("AcceptNotify",
							"an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "'", "an_id=" + an_id);
				}
			}
		}
	}

	/**
	 * 采购验收单：提交过账之前：比较当前单据总的成本单价*数量是否等于采购单价*数量*汇率+总的费用*汇率
	 * reserve->prodInOut->post->before
	 * 
	 * @author madan
	 */
	public void prodInOut_post_before_amountcheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select round(sum(round(pd_price * (nvl(pd_inqty,0)+nvl(pd_outqty,0)),4)),4),round(sum(round(round(round(pd_orderprice /(1+nvl(pd_taxrate,0)/100)*nvl(pi_rate,0),8),8)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),4)),4) from prodinout left join ProdIODetail on pi_id=pd_piid where pi_id=?",
						id);
		if (rs.next()) {
			double cbtotal = rs.getGeneralDouble(1);
			double cgtotal = rs.getGeneralDouble(2);
			double detailtotal = baseDao.getJdbcTemplate().queryForObject(
					"select nvl(round(sum(round(nvl(pd_amount,0)*nvl(pd_rate,0),4)),4),0) from ProdChargeDetail where pd_piid=?",
					Double.class, id);
			if (Math.abs(NumberUtil.formatDouble(cgtotal + detailtotal, 2) - NumberUtil.formatDouble(cbtotal, 2)) > 0.01) {
				BaseUtil.showError("采购金额[" + cgtotal + "]加费用明细[" + detailtotal + "]共[" + NumberUtil.formatDouble(cgtotal + detailtotal, 2)
						+ "]与成本金额[" + NumberUtil.formatDouble(cbtotal, 2) + "]不符,不能提交和过账!");
			}
		}
	}

	/**
	 * 收料单：提交之前：比较当前单据总的成本单价*数量是否等于采购单价*数量*汇率+总的费用*汇率
	 * purchase->acceptnotify->commit->before
	 * 
	 * @author madan
	 */
	public void verify_commit_before_amountcheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select round(sum(round(vad_price * vad_qty,4)),4),round(sum(round(round(vad_orderprice*va_rate/(1+nvl(vad_taxrate,0)/100),8)* vad_qty,4)),4) from verifyapply left join verifyapplyDetail on va_id=vad_vaid where va_id=?",
						id);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000");
		if (rs.next()) {
			double cbtotal = rs.getGeneralDouble(1);
			double cgtotal = rs.getGeneralDouble(2);
			double detailtotal = baseDao.getJdbcTemplate().queryForObject(
					"select nvl(round(sum(pd_amount*pd_rate),4),0) from ProdChargeDetailAN where pd_anid=?", Double.class, id);
			if (Math.abs(NumberUtil.formatDouble(cgtotal + detailtotal, 4) - NumberUtil.formatDouble(cbtotal, 4)) > 0.005) {
				BaseUtil.showError("采购金额[" + df.format(cgtotal) + "]加费用明细[" + df.format(detailtotal) + "]共["
						+ df.format(NumberUtil.formatDouble(cgtotal + detailtotal, 4)) + "]与成本金额["
						+ df.format(NumberUtil.formatDouble(cbtotal, 4)) + "]不符,不能提交!");
			}
		}
	}

	/**
	 * reserve->sale->commit->before
	 * 
	 * @author madan 销售订单提交之前：判断收款方式编号是否存在
	 */
	public void sale_commit_before_paymentcheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("sale left join customer on sa_custcode=cu_code", new String[] {
				"sa_paymentscode", "cu_id" }, "sa_id=" + id);
		int count = baseDao.getCountByCondition("sale", "sa_id=" + id
				+ " and sa_paymentscode in (select nvl(cp_paymentcode,' ') from CustomerPayments where cp_cuid =" + objs[1] + ")");
		if (count == 0) {
			count = baseDao.getCountByCondition("Customer", "cu_paymentscode='" + objs[0] + "'");
			if (count == 0)
				// 收款方式不存在，不允许提交
				BaseUtil.showError("收款方式不存在，不允许提交!");
		}
	}

	/**
	 * reserve->sendnotify->commit->before
	 * 
	 * @author madan 出货通知单提交之前：判断收款方式编号是否存在
	 */
	public void sendnotify_commit_before_paymentcheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("sendnotify left join customer on sn_custcode=cu_code", new String[] {
				"sn_paymentscode", "cu_id" }, "sn_id=" + id);
		int count = baseDao.getCountByCondition("sendnotify", "sn_id=" + id
				+ " and sn_paymentscode in (select nvl(cp_paymentcode,' ') from CustomerPayments where cp_cuid =" + objs[1] + ")");
		if (count == 0) {
			count = baseDao.getCountByCondition("Customer", "cu_paymentscode='" + objs[0] + "'");
			if (count == 0)
				// 收款方式不存在，不允许提交
				BaseUtil.showError("收款方式不存在，不允许提交!");
		}
	}

	/**
	 * reserve->prodinout->commit->before
	 * 
	 * @author madan 出货单提交之前：判断收款方式编号是否存在
	 */
	public void prodinout_commit_before_paymentcheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("prodinout left join customer on pi_cardcode=cu_code", new String[] {
				"pi_paymentcode", "cu_id" }, "pi_id=" + id);
		int count = baseDao.getCountByCondition("prodinout", "pi_id=" + id
				+ " and pi_paymentcode in (select nvl(cp_paymentcode,' ') from CustomerPayments where cp_cuid =" + objs[1] + ")");
		if (count == 0) {
			count = baseDao.getCountByCondition("Customer", "cu_paymentscode='" + objs[0] + "'");
			if (count == 0)
				// 收款方式不存在，不允许提交
				BaseUtil.showError("收款方式不存在，不允许提交!");
		}
	}

	/**
	 * 批量更新销售出货排程数量和交期，调用存储过程sp_batchsetdelivery_sale
	 */
	public void batchsetdelivery_sale() {
		String res = baseDao.callProcedure("SP_BATCHSETDELIVERY_SALE", new Object[] {});
		if (res != null && res.length() > 0) {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 更新员工的假期以及调休时间，调用存储过程SP_CACVACATION
	 */
	public void updateEmployee_holiday(HashMap<Object, Object> store) {
		String res = baseDao.callProcedure("SP_CACVACATION", new Object[] {});
		if (res != null && res.length() > 0) {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 更新员工的假期以及调休时间，调用存储过程SP_CACVACATION
	 */
	public void updateEmployee_Checkholiday(Integer id) {
		String res = baseDao.callProcedure("SP_CACVACATION", new Object[] {});
		if (res != null && res.length() > 0) {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 批量更新预测出货排程数量和交期，调用存储过程sp_batchsetdelivery_forecast
	 */
	public void batchsetdelivery_forecast() {
		String res = baseDao.callProcedure("SP_BATCHSETDELIVERY_FORECAT", new Object[] {});
		if (res != null && res.length() > 0) {
			BaseUtil.showError(res);
		}
	}

	/**
	 * sale->sale->delete->before 销售订单删除前，还原销售预测单以及报价单的数据
	 * 
	 * @author madan
	 */
	public void sale_sale_delete_before(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("SaleDetail", new String[] { "nvl(sd_saleforecastdetailid,0)",
				"nvl(sd_qty,0)", "sd_forecastcode", "nvl(sd_forecastdetno,0)", "sd_source", "nvl(sd_qdid,0)" }, "sd_said=" + id);
		for (Object[] obj : objs) {
			if (obj != null) {
				if (obj[4] != null && Integer.parseInt(String.valueOf(obj[5])) > 0) {// 报价单
					baseDao.updateByCondition("QuotationDetail", "qd_yqty=qd_yqty-" + obj[1], "qd_id=" + obj[5]);
					baseDao.updateByCondition("QuotationDetail", "qd_yqty=0", "qd_yqty<=0 and qd_id=" + obj[5]);
					Integer quid = baseDao
							.queryForObject("select nvl(qd_quid,0) from QuotationDetail where qd_id=?", Integer.class, obj[5]);
					if (quid != null && quid > 0) {
						int total = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid);
						int aud = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND qd_yqty=0");
						int turn = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND qd_yqty=qd_qty");
						String status = "PART2SA";
						if (aud == total) {
							status = "";
						} else if (turn == total) {
							status = "TURNSA";
						}
						baseDao.updateByCondition("Quotation",
								"qu_turnstatuscode='" + status + "',qu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "qu_id="
										+ quid);
					}
				} else if (obj[2] != null && Integer.parseInt(String.valueOf(obj[3])) > 0 && Integer.parseInt(String.valueOf(obj[0])) > 0) {// 销售预测单
					int sdid = Integer.parseInt(String.valueOf(obj[0]));
					baseDao.updateByCondition("SaleForecastDetail", "sd_yqty=sd_yqty-" + obj[1], "sd_id=" + sdid);
					saleForecastDao.udpatestatus(sdid);
				}
			}
		}
	}

	/**
	 * sale->sale->sale->deletedetail->before 销售单明细删除前，还原报价单或者销售预测单数据
	 * 
	 * @author madan
	 */
	public void sale_sale_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("SaleDetail", new String[] { "nvl(sd_saleforecastdetailid,0)", "nvl(sd_qty,0)",
				"sd_forecastcode", "nvl(sd_forecastdetno,0)", "sd_source", "nvl(sd_qdid,0)" }, "sd_id=" + id);
		if (objs != null) {
			if (objs[4] != null && Integer.parseInt(objs[5].toString()) > 0) {// 报价单
				baseDao.updateByCondition("QuotationDetail", "qd_yqty=qd_yqty-" + objs[1], "qd_id=" + objs[5]);
				baseDao.updateByCondition("QuotationDetail", "qd_yqty=0", "qd_yqty<=0 and qd_id=" + objs[5]);
				Integer quid = baseDao.queryForObject("select nvl(qd_quid,0) from QuotationDetail where qd_id=?", Integer.class, objs[5]);
				if (quid != null && quid > 0) {
					int total = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid);
					int aud = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND qd_yqty=0");
					int turn = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND qd_yqty=qd_qty");
					String status = "PART2SA";
					if (aud == total) {
						status = "";
					} else if (turn == total) {
						status = "TURNSA";
					}
					baseDao.updateByCondition("Quotation",
							"qu_turnstatuscode='" + status + "',qu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "qu_id=" + quid);
				}
			} else if (objs[2] != null && Integer.parseInt(objs[3].toString()) > 0 && Integer.parseInt(String.valueOf(objs[0])) > 0) {// 销售预测单
				int sdid = Integer.parseInt(String.valueOf(objs[0]));
				baseDao.updateByCondition("SaleForecastDetail", "sd_yqty=sd_yqty-" + objs[1], "sd_id=" + sdid);
				saleForecastDao.udpatestatus(sdid);
			}
		}
	}

	/**
	 * sale->sale->save->before 销售订单明细数量修改前，更新报价单或者销售预测单已转数量
	 * 
	 * @author madan
	 */
	static final String CHECK_QU_YQTY = "SELECT qu_code,qd_detno,qd_qty FROM QuotationDetail left join Quotation on qd_quid=qu_id WHERE qd_id=? and qd_qty<?";
	static final String CHECK_SF_YQTY = "SELECT sf_code,sd_detno,sd_qty,sd_clashsaleqty,sd_id FROM SaleForecastDetail left join SaleForecast on sd_sfid=sf_id WHERE sf_code=? and sd_detno=? and sd_qty+sd_clashsaleqty<?";

	public void sale_sale_updatedetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sdid = null;
		Object sfcode = null;
		Object sfdetno = null;
		Object qty = null;
		Integer qd_id = null;
		double tQty = 0;
		SqlRowList rs = null;
		Object noforecast = null;
		for (Map<Object, Object> s : gstore) {
			sdid = s.get("sd_id");
			sfcode = s.get("sd_forecastcode");
			sfdetno = s.get("sd_forecastdetno");
			noforecast = s.get("sd_noforecast");
			tQty = Double.parseDouble(String.valueOf(s.get("sd_qty")));
			Object[] objs = baseDao.getFieldsDataByCondition("SaleDetail", new String[] { "nvl(sd_saleforecastdetailid,0)", "sd_qty",
					"sd_forecastcode", "sd_forecastdetno", "sd_source", "nvl(sd_qdid,0)", "NVL(sd_noforecast,0)" }, "sd_id=" + sdid);
			if (objs != null) {
				if (objs[4] != null && Integer.parseInt(objs[5].toString()) > 0) {// 报价单
					qd_id = Integer.parseInt(String.valueOf(objs[5]));
					if (qd_id != null && qd_id > 0) {
						qty = baseDao.getFieldDataByCondition("SaleDetail", "sum(sd_qty)", "sd_qdid=" + qd_id + "and sd_code='" + objs[4]
								+ "' AND sd_id <>" + sdid);
						qty = qty == null ? 0 : qty;
						/*
						 * rs = baseDao.queryForRowSet(CHECK_QU_YQTY, qd_id,
						 * Double.parseDouble(qty.toString()) + tQty); if
						 * (rs.next()) { StringBuffer sb = new
						 * StringBuffer("[修改后订单数量超出报价单数量],行号:"
						 * ).append(s.get("sd_detno")).append(",报价单转订单总数:")
						 * .append(Double.parseDouble(qty.toString()) +
						 * tQty).append
						 * ("，原报价单数量:").append(rs.getDouble("qd_qty"));
						 * BaseUtil.showError(sb.toString()); }
						 */
						// 报价单
						baseDao.updateByCondition("QuotationDetail", "qd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"qd_id=" + qd_id);
						baseDao.updateByCondition("QuotationDetail", "qd_yqty=0", "qd_yqty<=0 and qd_id=" + qd_id);
						Integer quid = baseDao.queryForObject("select nvl(qd_quid,0) from QuotationDetail where qd_id=?", Integer.class,
								objs[5]);
						if (quid != null && quid > 0) {
							int count = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid);
							int yCount = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " and qd_yqty>=qd_qty");
							int xCount = baseDao.getCountByCondition("QuotationDetail", "qd_quid=" + quid + " and qd_yqty=0");
							String status = "PART2SA";
							if (yCount == count) {
								status = "TURNSA";
							}
							if (xCount == count) {
								status = "";
							}
							baseDao.updateByCondition("Quotation",
									"qu_turnstatuscode='" + status + "',qu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "qu_id="
											+ quid);
						}
					}
				} else if (sfcode != null && sfdetno != null) {// 销售预测单
					int sfdid = 0;
					Object ob = baseDao.getFieldDataByCondition("SaleForecastDetail left join SaleForecast on sd_sfid=sf_id", "sd_id",
							"sf_code='" + sfcode + "' and sd_detno=" + sfdetno);
					if (ob != null) {
						sfdid = Integer.parseInt(ob.toString());
					}
					qty = baseDao.getFieldDataByCondition("SaleDetail", "sum(sd_qty)", "sd_forecastcode='" + sfcode
							+ "' and sd_forecastdetno=" + sfdetno + " AND sd_id <>" + sdid);
					qty = qty == null ? 0 : qty;
					rs = baseDao.queryForRowSet(CHECK_SF_YQTY, sfcode, sfdetno, Double.parseDouble(qty.toString()) + tQty);
					if (rs.next()) {
						sfdid = rs.getGeneralInt("sd_id");
						// 如果明细行的NVL(sd_noforecast,0)<>0 则不限制，因为这个是属于超预测数的
						if (Integer.parseInt(noforecast.toString()) == 0) {
							StringBuffer sb = new StringBuffer("[修改后订单数量超出销售预测单预测数量+冲销数量],行号:").append(s.get("sd_detno"))
									.append(",预测冲销单转订单总数:").append(Double.parseDouble(qty.toString()) + tQty).append("，原预测冲销数:")
									.append(rs.getDouble("sd_qty") + rs.getDouble("sd_clashsaleqty"));
							BaseUtil.showError(sb.toString());
						}
					}
					String sdforecastcode = objs[2] == null ? "" : objs[2].toString();
					String sdforecastdetno = objs[3] == null ? "" : objs[3].toString();
					// 销售预测单号修改，更新原销售预测单
					if (objs[2] != null && (!sdforecastcode.equals(sfcode.toString()) || !sdforecastdetno.equals(sfdetno.toString()))) {
						Object ob1 = baseDao.getFieldDataByCondition("SaleForecastDetail left join SaleForecast on sd_sfid=sf_id", "sd_id",
								"sf_code='" + sdforecastcode + "' and sd_detno=" + sdforecastdetno);
						int sfdid_old = 0;
						if (ob1 != null) {
							sfdid_old = Integer.parseInt(ob1.toString());
						}
						qty = baseDao.getFieldDataByCondition("SaleDetail", "sum(sd_qty)", "sd_forecastcode='" + sdforecastcode
								+ "' and sd_forecastdetno=" + sdforecastdetno + " AND sd_id <>" + sdid);
						qty = qty == null ? 0 : qty;
						baseDao.updateByCondition("SaleForecastDetail", "sd_yqty=" + Double.parseDouble(String.valueOf(qty))
								+ "-NVL(sd_clashsaleqty,0)", "sd_id=" + sfdid_old);
						saleForecastDao.udpatestatus(sfdid_old);
					}
					baseDao.updateByCondition("SaleForecastDetail", "sd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty)
							+ "-NVL(sd_clashsaleqty,0)", "sd_id=" + sfdid);
					saleForecastDao.udpatestatus(sfdid);
				}
			}
		}
	}

	/**
	 * 采购单：如果采购单价不是最低单价，不允许提交
	 */
	public void check_product_price(Integer id) {
		String errorPrice = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('<br>行:'||pd_detno||',物料:'||pd_prodcode||',当前价:'||pd_price||',最低价:'||ppd_price) from (select pd_detno,pd_prodcode,round(nvl(pd_price,0)*NVL(PU_RATE,0)/(1+nvl(pd_rate,0)/100),8) pd_price,(select round(min(ppd_price*NVL(CR_RATE,0)/(1+nvl(ppd_rate,0)/100)),8) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join Currencys on ppd_currency=cr_name where ppd_prodcode=pd_prodcode and ppd_vendcode=pu_vendcode and pp_kind='采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(pu_date,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(pu_date,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=pd_qty) ppd_price from purchasedetail left join purchase on pu_id=pd_puid where pu_id=? and nvl(pu_ordertype,' ')<>'B2C') where nvl(ppd_price,0)<pd_price",
						String.class, id);
		if (errorPrice != null) {
			BaseUtil.showError("不是最低采购单价,不允许提交!" + errorPrice);
		}
	}

	/**
	 * 出货通知单：没有来源订单，不允许提交
	 * 
	 * @author madan
	 */
	public void sale_sendnotify_commit_source(Integer id) {
		String errRows = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(snd_pdno) from sendnotifydetail where snd_snid=? and nvl(snd_sdid,0)=0", String.class, id);
		if (errRows != null) {
			BaseUtil.showError("通知单限制只能从订单转入,不允许手工维护,行:" + errRows);
		}
	}

	/**
	 * 判断订单的物料特征选值是否有处于研发状态的特征值
	 * 
	 * @author zhongyl
	 */
	public void sale_feature_ifRD(Integer id) {
		// 判断是否有研发状态的特征值
		String feature = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(distinct fd_code) from saledetail left join product on pr_code=sd_prodcode left join featuredetail on ( '|' || sd_specdescription||pr_specdescription || '|' LIKE '%|' || fd_code || ':' || fd_valuecode || '|%') where sd_said=? and nvl(fd_style,' ')='研发'",
						String.class, id);
		if (feature != null) {
			BaseUtil.showError("特征ID " + feature + " 所选择的特征值处于研发状态!");
		}
	}

	/**
	 * 判断订单的OEM类型明细预测号和序号都不能为空 善岭专用
	 * 
	 * @author shenj
	 */
	public void sale_commit_oem(Integer id) {
		// 判断销售类型是否为oem
		String feature = baseDao.getFieldDatasByCondition("Sale", "sa_kind", "sa_id=" + id).toString();
		feature = feature.replace("[", "");
		feature = feature.replace("]", "");
		if (feature.equals("OEM")) {
			int countnum = baseDao.getCount("select count(*) from saledetail where sd_said=" + id
					+ " and (nvl(sd_forecastcode,' ')=' ' or nvl(sd_forecastcode,' ')=' ')");
			if (countnum > 0) {
				BaseUtil.showError("销售订单销售类型为OEM的订单，明细预测号与序号都不能为空!");
			}
		}
	}

	/**
	 * reserve->prodInOut->sale->deletedetail->before 收料通知单：明细删除前，还原采购单的数据
	 * 
	 * @author madan
	 */
	public void purchase_acceptnotify_deletedetail(Integer id) {
		acceptNotifyDao.restorePurc(id);
	}

	/**
	 * 收料通知单：修改后，修改数量要反馈到采购单
	 * 
	 * @author madan
	 */
	public void acceptnotify_checkin_save_qty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object andid = null;// 收料通知单明细ID
		Object qty = null;
		Integer pd_id = null;// 采购单明细ID
		double tQty = 0;// 收料通知单修改数量
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			andid = s.get("and_id");
			tQty = Double.parseDouble(s.get("and_inqty").toString());
			if (s.get("and_id") != null && Integer.parseInt(s.get("and_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("AcceptNotifyDetail", new String[] { "nvl(and_orderid,0)",
						"nvl(and_inqty,0)", "and_ordercode", "nvl(and_orderdetno,0)" }, "and_id=" + andid + " and nvl(and_orderid,0)<>0");
				if (objs != null && objs[0] != null) {
					pd_id = Integer.parseInt(String.valueOf(objs[0]));
					if (pd_id != null && pd_id > 0) {
						qty = baseDao.getFieldDataByCondition("AcceptNotifyDetail", "sum(nvl(and_inqty,0))", "and_orderid=" + pd_id
								+ "and and_ordercode='" + objs[2] + "' AND and_id <>" + andid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(CHECK_PU_YQTY, pd_id, Double.parseDouble(qty.toString()) + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],采购单号:").append(rs.getString("pd_code")).append(",行号:")
									.append(rs.getInt("pd_pdno")).append(",采购数量:").append(rs.getDouble("pd_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						// 采购单
						baseDao.updateByCondition("PurchaseDetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty), "pd_id="
								+ pd_id);
						baseDao.updateByCondition("PurchaseDetail", "pd_yqty=0", "pd_yqty<=0 and pd_id=" + pd_id);
						baseDao.updateByCondition("PurchaseDetail", "pd_status='AUDITED'", "pd_yqty=0 and pd_id=" + pd_id);
						baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNSN'",
								"pd_yqty=pd_qty AND nvl(pd_yqty,0) <> 0 and pd_id=" + pd_id);
						baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2SN'", "pd_id=" + pd_id
								+ " and nvl(pd_yqty,0)<nvl(pd_qty,0) and nvl(pd_yqty,0)<>0");
						Integer puid = baseDao.queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class,
								objs[0]);
						if (puid != null && puid > 0) {
							int count = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
							int yCount = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and NVL(pd_yqty,0)=pd_qty");
							int xCount = baseDao.getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and NVL(pd_yqty,0)=0");
							String status = "PART2SN";
							if (yCount == count) {
								status = "TURNSN";
							}
							if (xCount == count) {
								status = "";
							}
							baseDao.updateByCondition("Purchase",
									"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_id="
											+ puid);
						}
					}
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->saleborrow->delete->before 借货出货单：删除前，还原借货申请单的数据
	 * 
	 * @author madan 2014-8-21 09:43:22
	 */
	public void saleborrow_delete_before_replayqty(Integer id) {
		borrowApplyDao.deleteBorrowApply(id);
	}

	/**
	 * reserve->prodInOut->saleborrow->deletedetail->before
	 * 借货出货单：明细删除前，还原借货申请单的数据
	 * 
	 * @author madan 2014-8-21 10:23:13
	 */
	public void saleborrow_deletedetail_before_replayqty(Integer id) {
		borrowApplyDao.restoreBorrowApply(id);
	}

	/**
	 * 借货出货单：修改后，修改数量要反馈到借货申请单
	 * 
	 * @author madan 2014-8-21 10:28:34
	 */
	static final String CHECK_BA_YQTY = "SELECT ba_code,bad_detno,bad_qty FROM BorrowApplyDetail left join BorrowApply on bad_baid=ba_id WHERE bad_id=? and bad_qty<?";
	static final String CHECK_PD_YQTY = "SELECT pd_inoutno,pd_pdno,pd_outqty FROM ProdIODetail WHERE pd_id=? and pd_outqty<?";

	public void saleborrow_update_before_replayqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;// 借货出货单明细ID
		Object qty = null;
		Integer bad_id = null;// 借货申请单明细ID
		double tQty = 0;// 借货出货单修改数量
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(s.get("pd_outqty").toString());
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_outqty,0)",
						"pd_ordercode", "nvl(pd_orderdetno,0)" }, "pd_id=" + pdid + " and nvl(pd_orderid,0)<>0 and pd_piclass='借货出货单'");
				if (objs != null && objs[0] != null) {
					bad_id = Integer.parseInt(String.valueOf(objs[0]));
					if (bad_id != null && bad_id > 0) {
						qty = baseDao.getFieldDataByCondition("ProdIoDetail", "nvl(sum(nvl(pd_outqty,0)),0)", "pd_orderid=" + bad_id
								+ " AND pd_piclass='借货出货单' and pd_id <>" + pdid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(CHECK_BA_YQTY, bad_id, Double.parseDouble(qty.toString()) + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],申请单号:").append(rs.getString("ba_code")).append(",行号:")
									.append(rs.getInt("bad_pdno")).append(",数量:").append(rs.getDouble("bad_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						// 申请单
						baseDao.updateByCondition("BorrowApplyDetail", "bad_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"bad_id=" + bad_id);
						baseDao.updateByCondition("BorrowApplyDetail", "bad_yqty=0", "bad_yqty<=0 and bad_id=" + bad_id);
						baseDao.updateByCondition("BorrowApplyDetail", "bad_status=null,bad_statuscode=null", "bad_yqty=0 and bad_id="
								+ bad_id);
						baseDao.updateByCondition("BorrowApplyDetail", "bad_status='已转出货单',bad_statuscode='TURNPRODIO'",
								"bad_yqty=bad_qty AND nvl(bad_yqty,0) <> 0 and bad_id=" + bad_id);
						baseDao.updateByCondition("BorrowApplyDetail", "bad_status='部分出货',bad_statuscode='PARTOUT'", "bad_id=" + bad_id
								+ " and nvl(bad_yqty,0)<nvl(bad_qty,0) and nvl(bad_yqty,0)<>0");
						Integer baid = baseDao.queryForObject("select nvl(bad_baid,0) from BorrowApplyDetail where bad_id=?",
								Integer.class, objs[0]);
						if (baid != null && baid > 0) {
							int count = baseDao.getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid);
							int yCount = baseDao.getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid + " and bad_yqty=bad_qty");
							int xCount = baseDao.getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid + " and bad_yqty=0");
							String status = "PARTOUT";
							if (yCount == count) {
								status = "TURNPRODIO";
							}
							if (xCount == count) {
								status = "";
							}
							baseDao.updateByCondition("BorrowApply",
									"ba_turnstatuscode='" + status + "',ba_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "ba_id="
											+ baid);
						}
					}
				}
			}
		}
	}

	/**
	 * 1.是否启用供应商比例分配功能 2.取价是否只取合格的供应商价格
	 * 
	 * @author zhongyl
	 */
	public void application_getvendor(Integer id) {
		String sqlstr = null;
		if (baseDao.isDBSetting("vendorRate")) {
			sqlstr = "MERGE INTO ApplicationDetail USING (select * from (SELECT ad_id,ad_prodcode,ad_vendor,pv_vendcode,ve_name,pv_currency,ve_id"
					+ ",rank() over (PARTITION BY ad_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM applicationdetail "
					+ " left join ProductVendorRate on ad_prodcode=pv_prodcode and NVL(pv_action,' ')<>'无效' left join vendor on pv_vendcode=ve_code where ad_apid="
					+ id
					+ ") where mm=1)src "
					+ " on (src.ad_id=applicationdetail.ad_id) when matched then update set ad_ifvendrate=-1,ad_vendor=src.pv_vendcode,ad_vendname=src.ve_name,ad_currency=src.pv_currency,ad_vendid=src.ve_id "
					+ " WHERE ad_apid=" + id + " and NVL(ad_yqty,0)=0";
			baseDao.execute(sqlstr);
			// 获取替代料供应商
			sqlstr = "MERGE INTO mrpreplace USING (select * from (SELECT mr_id,mr_repcode,mr_vendor,pv_vendcode,ve_name,pv_currency,ve_id"
					+ ",rank() over (PARTITION BY mr_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM mrpreplace left join applicationdetail on mr_mdid=ad_mdid "
					+ " left join ProductVendorRate on mr_repcode=pv_prodcode and NVL(pv_action,' ')<>'无效' left join vendor on pv_vendcode=ve_code where mr_mdid in (select ad_mdid "
					+ " from applicationdetail where  ad_apid="
					+ id
					+ " and ad_mdid>0) ) where mm=1)src "
					+ " on (src.mr_id=mrpreplace.mr_id) when matched then update set mr_ifvendrate=-1,mr_vendor=src.pv_vendcode,mr_vendname=src.ve_name,mr_currency=src.pv_currency,mr_veid=src.ve_id "
					+ " WHERE mr_mdid in (select ad_mdid from applicationdetail where  ad_apid=" + id + " and ad_mdid>0)";
			baseDao.execute(sqlstr);

			// 原本更新到MRPReplace表的业务,也加到ApplicationReplace表 2016-12-13
			sqlstr = "MERGE INTO ApplicationReplace USING (select * from (SELECT ar_id,ar_repcode,ar_vendor,pv_vendcode,ve_name,pv_currency,ve_id"
					+ ",rank() over (PARTITION BY ar_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM ApplicationReplace left join applicationdetail on ar_mdid=ad_mdid "
					+ " left join ProductVendorRate on ar_repcode=pv_prodcode and NVL(pv_action,' ')<>'无效' left join vendor on pv_vendcode=ve_code where ar_mdid in (select ad_mdid "
					+ " from applicationdetail where ad_apid="
					+ id
					+ " and ad_mdid>0) ) where mm=1)src "
					+ " on (src.ar_id=ApplicationReplace.ar_id) when matched then update set ar_ifvendrate=-1,ar_vendor=src.pv_vendcode,ar_vendname=src.ve_name "
					+ " WHERE ar_mdid in (select ad_mdid from applicationdetail where ad_apid=" + id + " and ad_mdid>0)";
			baseDao.execute(sqlstr);
		}
		// 最低价供应商
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		if (baseDao.isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = "MERGE INTO ApplicationDetail USING (select * from (SELECT ad_id,ad_prodcode,ad_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
					+ ",rank() over (PARTITION BY ad_prodcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc ,ppd_id desc) mm FROM applicationdetail "
					+ " left join PurchasePriceDetail on ad_prodcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
					+ " left join  Currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code where ad_apid="
					+ id
					+ " and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate) where mm=1)src "
					+ " on (src.ad_id=applicationdetail.ad_id) when matched then update set ad_ppdid=src.ppd_id,ad_vendor=src.ppd_vendcode,ad_vendname=src.ppd_vendname,ad_currency=src.ppd_currency,ad_vendid=src.ppd_vendid "
					+ " WHERE ad_apid=" + id + " and NVL(ad_yqty,0)=0 and NVL(ad_vendor,' ')=' '";
			baseDao.execute(sqlstr);
			// 获取替代料供应商
			sqlstr = "MERGE INTO MrpReplace USING (select * from (SELECT mr_id,mr_repcode,mr_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
					+ ",rank() over (PARTITION BY mr_repcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc,ppd_id desc) mm FROM applicationdetail left join mrpreplace on ad_mdid=mr_mdid "
					+ " left join PurchasePriceDetail on mr_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
					+ " left join  Currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code where ad_apid="
					+ id
					+ " and mr_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
					+ " on (src.mr_id=MrpReplace.mr_id) when matched then update set mr_ppdid=src.ppd_id,mr_vendor=src.ppd_vendcode,mr_vendname=src.ppd_vendname,mr_currency=src.ppd_currency,mr_veid=src.ppd_vendid "
					+ " WHERE mr_mdid in (select ad_mdid from applicationdetail where ad_apid="
					+ id
					+ " and ad_mdid>0) and NVL(mr_vendor,' ')=' '";
			baseDao.execute(sqlstr);

			// 原本更新到MRPReplace表的业务,也加到ApplicationReplace表 2016-12-13
			sqlstr = "MERGE INTO ApplicationReplace USING (select * from (SELECT ar_id,ar_repcode,ar_vendor,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
					+ ",rank() over (PARTITION BY ar_repcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc,ppd_id desc) mm FROM ApplicationReplace left join applicationdetail on ar_mdid=ad_mdid "
					+ " left join PurchasePriceDetail on ar_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid"
					+ " left join  Currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code where ad_apid="
					+ id
					+ " and ar_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
					+ " on (src.ar_id=ApplicationReplace.ar_id) when matched then update set ar_vendor=src.ppd_vendcode,ar_vendname=src.ppd_vendname,ar_currency=src.ppd_currency,ar_veid=src.ppd_vendid "
					+ " WHERE ar_mdid in (select ad_mdid from applicationdetail where ad_apid=" + id + " and ad_mdid>0)";
			baseDao.execute(sqlstr);
		} else {
			/**
			 * @author wsy 双单位
			 */
			sqlstr = "MERGE INTO ApplicationDetail USING (select * from (SELECT ad_id,ad_prodcode,ad_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate"
					+ ",rank() over (PARTITION BY ad_prodcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc,ppd_id desc) mm FROM applicationdetail "
					+ " left join PurchasePriceDetail on ad_prodcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
					+ " left join  Currencys on ppd_currency=cr_name left join product on ad_prodcode=pr_code where ad_apid="
					+ id
					+ " and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
					+ " on (src.ad_id=applicationdetail.ad_id) when matched then update set ad_ppdid=src.ppd_id,ad_vendor=src.ppd_vendcode,ad_vendname=src.ppd_vendname,ad_currency=src.ppd_currency,ad_vendid=src.ppd_vendid,ad_purcprice=src.ppd_price,ad_rate=src.ppd_rate "
					+ " WHERE ad_apid=" + id + " and NVL(ad_yqty,0)=0 and NVL(ad_vendor,' ')=' '";
			baseDao.execute(sqlstr);
			// 获取替代料供应商
			sqlstr = "MERGE INTO MrpReplace USING (select * from (SELECT mr_id,mr_repcode,mr_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
					+ ",rank() over (PARTITION BY mr_repcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc,ppd_id desc) mm FROM applicationdetail left join mrpreplace on ad_mdid=mr_mdid "
					+ " left join PurchasePriceDetail on mr_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
					+ " left join Currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code where ad_apid="
					+ id
					+ " and mr_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
					+ " on (src.mr_id=MrpReplace.mr_id) when matched then update set mr_ppdid=src.ppd_id,mr_vendor=src.ppd_vendcode,mr_vendname=src.ppd_vendname,mr_currency=src.ppd_currency,mr_veid=src.ppd_vendid "
					+ " WHERE mr_mdid in (select ad_mdid from applicationdetail where ad_apid="
					+ id
					+ " and ad_mdid>0) and NVL(mr_vendor,' ')=' '";
			baseDao.execute(sqlstr);

			// 原本更新到MRPReplace表的业务,也加到ApplicationReplace表 2016-12-13
			sqlstr = "MERGE INTO ApplicationReplace USING (select * from (SELECT ar_id,ar_repcode,ar_vendor,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
					+ ",rank() over (PARTITION BY ar_repcode order by ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) asc,ppd_id desc) mm FROM ApplicationReplace left join applicationdetail on ar_mdid=ad_mdid "
					+ " left join PurchasePriceDetail on ar_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid"
					+ " left join  Currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code where ad_apid="
					+ id
					+ " and ar_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
					+ " and pp_kind='采购' and ppd_lapqty<=round(ad_qty/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
					+ " on (src.ar_id=ApplicationReplace.ar_id) when matched then update set ar_vendor=src.ppd_vendcode,ar_vendname=src.ppd_vendname,ar_currency=src.ppd_currency,ar_veid=src.ppd_vendid "
					+ " WHERE ar_mdid in (select ad_mdid from applicationdetail where ad_apid="
					+ id
					+ " and ad_mdid>0) and nvl(ar_vendor,' ')=' '";
			baseDao.execute(sqlstr);
		}
		sqlstr = "update ApplicationDetail set ad_barcode=(case when NVL(ad_vendor,' ')=' ' then '采购开发' else '采购员' end) WHERE ad_apid="
				+ id + " and NVL(ad_yqty,0)=0";
		baseDao.execute(sqlstr);
	}

	/**
	 * application->commit->before 存在未执行的自然切换ecn不能请购
	 * 
	 * @author zhongyl
	 */
	public void application_haveEcnCheck(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from applicationdetail left join product on ad_prodcode=pr_code where ad_apid="
						+ id
						+ " AND ad_prodcode in (SELECT  ed_repcode from ecndetail,ecn where ecn_id=ed_ecnid and ecn_type='AUTO' and ecn_checkstatuscode='AUDITED' and ecn_didstatuscode='OPEN' and ed_type='SWITCH'  and ed_didstatuscode='OPEN'  ) ");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("以下物料存在未执行的自然切换ecn，不能请购:" + sl.getString("prcode") + "");
			}
		}
		sl = baseDao
				.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from applicationdetail left join product on ad_prodcode=pr_code where ad_apid="
						+ id
						+ " AND ad_prodcode in (SELECT  ed_soncode from ecndetail,ecn where ecn_id=ed_ecnid and ecn_type='AUTO' and ecn_checkstatuscode='AUDITED' and ecn_didstatuscode='OPEN' and ed_type='DISABLE'  and ed_didstatuscode='OPEN'  ) ");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("以下物料存在未执行的自然切换ecn，不能请购:" + sl.getString("prcode") + "");
			}
		}
	}

	/**
	 * 物料描述更新
	 * 
	 * @author zhongyl
	 */
	public void product_updateprodname(Integer id) {
		String prcode = "";
		SqlRowList sl = baseDao.queryForRowSet("select pr_code from product where pr_id='" + id + "'");
		if (sl.next()) {
			prcode = sl.getString("pr_code");
			// 更新特征实体物料的物料名称规格
			/*
			 * baseDao.execute(
			 * "merge into featureproduct using(select pr_code,pr_detail,pr_spec  from  product)src on(fp_prodcode=pr_code) when matched then update set fp_prodname=pr_detail,fp_prodspec=pr_spec where fp_prodcode='"
			 * + prcode + "'"); // 更新特征虚拟物料的物料名称规格 baseDao.execute(
			 * "merge into featureproduct using(select pr_code,pr_detail,pr_spec  from  product)src on(fP_refno=pr_code) when matched then update set fp_prodname=pr_detail,fp_prodspec=pr_spec where fp_refno='"
			 * + prcode + "'");
			 */
			baseDao.execute(
					"update featureproduct set (fp_prodname,fp_prodspec)=(select  pr_detail,pr_spec from product where pr_id=?) where fp_prodcode =? or  fp_refno=?",
					new Object[] { id, prcode, prcode });
			// 更新BOM母件物料的名称规格
			// baseDao.execute("merge into BOM using(select pr_code,pr_detail,pr_spec  from  product)src on(bo_mothercode=pr_code) when matched then update set bo_prodname=pr_detail,bo_spec=pr_spec where bo_mothercode='"+
			// prcode + "'");
			baseDao.execute(
					"update BOM set (bo_prodname,bo_spec)=(select  pr_detail,pr_spec from product where pr_id=?) where bo_mothercode =?",
					new Object[] { id, prcode });
			// 更新制造单的物料名称规格
			// baseDao.execute("merge into make using(select pr_code,pr_detail,pr_spec  from  product)src on(ma_prodcode=pr_code) when matched then update set ma_prodname=pr_detail,ma_prodspec=pr_spec where ma_prodcode='"+
			// prcode + "'");
			baseDao.execute(
					"update make set (ma_prodname,ma_prodspec)=(select pr_detail,pr_spec from product where pr_id=?) where ma_prodcode=? ",
					new Object[] { id, prcode });
		}
	}

	/**
	 * 收料通知单：供应商编号为空时，根据供应商UU号更新供应商号
	 * 
	 * @author madan
	 */
	public void acceptnotify_query_updatevend(String condition) {
		baseDao.execute("update AcceptNotify set (an_vendcode,an_vendname)=(select ve_code,ve_name from vendor where ve_uu=an_venduu) where nvl(an_vendcode,' ')=' ' and nvl(an_venduu,0)<>0 and "
				+ condition);
	}

	/**
	 * 更新未完结PO的已收料数量 zhongyl
	 */
	public void updatePurchaseYQTY(String condition) {
		purchaseDao.updatePurcYQTY(-1, null);
	}

	/**
	 * 更新未完结PO的已通知收料数量 zhongyl
	 */
	public void updatePurchaseDetailTurnQTY(String condition) {
		// purchaseDao.updatePurcYNotifyQTY(-1, null);
		// //不使用，已改用view获取pd_turnqty作为判断超数 2015 05 25 zhongyl
	}

	/**
	 * saleforecast->audit->after 预测单审核后根据单号产生冲销单，预测冲预测
	 * 
	 * @author zhongyl
	 */
	public void saleforecast_clash(Integer id) {
		saleClashService.createForeCastClash(id);
	}

	/**
	 * saleforecast->resAudit->after取消冲销
	 * 
	 * @author zhongyl
	 */
	public void saleforecast_unclash(Integer id) {
		saleClashService.cancelSaleClash(id, "SaleForecast");
	}

	/**
	 * saleforecast->commit->before 销售预测的明细物料，如果是是制造或委外的物料必须存在已审核的BOM
	 * 
	 * @author zhongyl
	 */
	public void saleforecast_BOMAudited(Integer id) {
		String SqlStr = "SELECT count(1) num,wm_concat(sd_detno) detno from saleforecastdetail left join product on sd_prodcode=pr_code left join bom on sd_prodcode=bo_Mothercode where sd_sfid="
				+ id + " and pr_manutype in ('MAKE','OSMAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("序号:" + rs.getString("detno") + "的物料BOM未建立或未审核！");
			}
		}
	}

	/**
	 * saleforecast->commit->before 销售单的明细物料，如果是是制造或委外的物料必须存在已审核的BOM
	 * 
	 * @author zhongyl
	 */
	public void sale_BOMAudited(Integer id) {
		String SqlStr = "SELECT count(1) num,wm_concat(sd_detno) detno from sale left join saledetail on sa_id=sd_said left join SaleKind on sa_kind=sk_name left join product on sd_prodcode=pr_code left join bom on (sd_prodcode=bo_Mothercode or pr_refno=bo_Mothercode) where sd_said="
				+ id + " and nvl(sk_nobomlevel,0)=0 and pr_manutype in ('MAKE','OSMAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("序号:" + rs.getString("detno") + "的物料BOM未建立或未审核！");
			}
		}
	}

	/**
	 * saleforecast->commit->before 销售预测的明细物料，如果是是制造或委外的物料必须存在已审核的BOM
	 * 
	 * @author zhongyl
	 */
	public void saleforecast_MRPBOMAudited(Integer id) {
		String SqlStr = "SELECT count(1) num,wm_concat(sd_detno) detno from saleforecastdetail left join product on sd_prodcode=pr_code left join bom on sd_prodcode=bo_Mothercode left join bomlevel on bl_code=bo_level where sd_sfid="
				+ id + " and pr_manutype in ('MAKE','OSMAKE') and NOT(NVL(bo_statuscode,' ')='AUDITED' and bl_ifmrp<>0) ";
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("序号:" + rs.getString("detno") + "的物料未建立参与MRP的已审核BOM！");
			}
		}
	}

	/**
	 * yaozx@14-02-13 过账前判断其他入库单备注里的其它出库单pd_total是不是和该入库单金额+费用明细相等
	 */
	public void judgeProdinoutPdtotal(Integer id) {
		Object pi_remark = baseDao.getFieldDataByCondition("ProdInOut", "pi_remark", "pi_id=" + id);
		Double pd_total = baseDao.getJdbcTemplate().queryForObject(
				"select sum(round(pd_outqty*pd_price,2)) from prodiodetail where pd_inoutno=? and pd_piclass='其它出库单'",
				new Object[] { pi_remark }, Double.class);
		Double pd_sum = baseDao.getJdbcTemplate().queryForObject(
				"select sum(round(pd_inqty*pd_price,2)) from prodiodetail where pd_piid=?", new Object[] { id }, Double.class);
		Double pd_sumdetail = baseDao.getJdbcTemplate().queryForObject(
				"select sum(round(pd_amount,2)) from prodchargedetail where pd_piid=?", new Object[] { id }, Double.class);
		Double diversity;
		try {
			diversity = pd_sum - pd_total - pd_sumdetail;
			if (Math.abs(diversity) > 0.1) {
				BaseUtil.showError("该其他出入库单和其他入库单费用不等,过账失败！");
			}
		} catch (NullPointerException e) {
			pd_sumdetail = (double) 0;
			diversity = pd_sum - pd_total - pd_sumdetail;
			if (Math.abs(diversity) > 0.1) {
				BaseUtil.showError("该其他出入库单和其他入库单费用不等,过账失败！");
			}
		} catch (Exception e) {
			BaseUtil.showError("该其他出入库单金额有问题,请和管理员联系！");
		}
	}

	/**
	 * 出货通知单：保存、更新之后折扣单价、折扣后单价金额的计算、根据是否报关缺省设置税率、默认抓取最近的客户型号、销售价跟报关/折扣价都为0的提示
	 * 
	 * @author madan
	 * @param store
	 * @param gstore
	 */
	public void sendnotify_save_discount(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sn_id = store.get("sn_id");
		if (sn_id != null) {
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=0,snd_discountamount=round(snd_outqty*snd_sendprice-nvl(snd_discountamount2,0),2) where snd_snid=? and nvl(snd_discountamount2,0)>0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=round(snd_discountamount/snd_outqty,8) where snd_snid=? and nvl(snd_outqty,0)>0 and nvl(snd_discountprice,0)=0 and nvl(snd_discountamount,0)>0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountamount=round(snd_discountprice*snd_outqty,2) where snd_snid=? and nvl(snd_discountprice,0)>0 and nvl(snd_discountamount,0)=0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discount=round((snd_sendprice-snd_discountprice)/snd_sendprice,8) where snd_snid=? and nvl(snd_sendprice,0)>0",
					sn_id);
			baseDao.execute("update SendNotifyDetail set snd_discount=0 where snd_snid=? and nvl(snd_discountamount2,0)=0", sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=snd_sendprice,snd_discountamount=snd_sendprice * snd_outqty where snd_snid=? and nvl(snd_discountamount2,0)=0 and nvl(snd_discountprice,0)=0 and nvl(snd_discountamount,0)=0 ",
					sn_id);
			// 根据是否报关默认税率
			baseDao.execute("update SendNotifyDetail set snd_taxrate=0 where snd_snid=? and abs(nvl(snd_bonded,0))=1", sn_id);
			baseDao.execute("update SendNotifyDetail set snd_taxrate=16 where snd_snid=? and abs(nvl(snd_bonded,0))=0", sn_id);
			// 默认抓取最近的客户型号
			SqlRowList rs = baseDao.queryForRowSet("SELECT * from SendNotifyDetail WHERE snd_snid=? and nvl(snd_custprodcode,' ')=' ' ",
					sn_id);
			String sndcustprodcode = null;
			while (rs.next()) {
				sndcustprodcode = baseDao.queryForObject(
						"Select snd_custprodcode from (SELECT snd_custprodcode FROM SendNotify,SendNotifyDetail where sn_id=snd_snid and snd_prodcode='"
								+ rs.getObject("snd_prodcode")
								+ "' and nvl(snd_custprodcode,' ')<>' ' order by sn_date desc) where rownum<2", String.class);
				if (sndcustprodcode != null) {
					baseDao.execute("update SendNotifyDetail set snd_custprodcode=? where snd_snid=? and snd_pdno=?", sndcustprodcode,
							sn_id, rs.getObject("snd_pdno"));
				}
			}
			// 销售价跟报关/折扣价都为0的提示
			rs = baseDao.queryForRowSet(
					"SELECT snd_pdno from SendNotifyDetail WHERE snd_snid=? and nvl(snd_sendprice,0)=0 and nvl(snd_discountprice,0)=0",
					sn_id);
			if (rs.next()) {
				BaseUtil.appendError("序号" + rs.getObject("snd_pdno") + "销售价、报关/折扣后单价都为0!");
			}
		}
	}

	/**
	 * 采购验退，不良品出库单：过账后自动投放送货提醒 prodinout->post->after
	 * 
	 * @author zhongyl
	 */
	public void ProdInout_PostPurcNotify(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT pd_inoutno, prodiodetail.pd_ordercode,prodiodetail.pd_orderdetno,pu_vendcode,pu_vendname,prodiodetail.pd_prodcode,pd_outqty,prodiodetail.pd_prodid,purchasedetail.pd_id,pd_piclass from prodiodetail,purchasedetail,purchase,vendor,prodinout WHERE pd_piid="
						+ id
						+ " and prodiodetail.pd_ordercode=purchasedetail.pd_code And prodiodetail.pd_orderdetno=purchasedetail.pd_detno and prodiodetail.pd_ordercode=pu_code "
						+ "and prodiodetail.pd_piid=prodinout.pi_id and (nvl(pi_type,' ')<>'特采' or (nvl(pi_type,' ')='特采' and nvl(pd_mrok,0)<>1)) "// 特采有对应验收单的不产生送货提醒
						+ "and purchase.pu_vendcode=vendor.ve_code and prodiodetail.pd_status=99 and nvl(vendor.VE_IFDELIVERYONB2B,0)<>0");
		while (rs.next()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("pn_id", baseDao.getSeqId("PURCHASENOTIFY_SEQ"));
			map.put("pn_mrpcode", null);
			map.put("pn_mdid", id);
			map.put("pn_ordercode", rs.getObject("pd_ordercode"));
			map.put("pn_orderdetno", rs.getObject("pd_orderdetno"));
			map.put("pn_vendcode", rs.getObject("pu_vendcode"));
			map.put("pn_vendname", rs.getObject("pu_vendname"));
			map.put("pn_prodcode", rs.getObject("pd_prodcode"));
			map.put("pn_qty", rs.getObject("pd_outqty"));
			map.put("pn_delivery", DateUtil.currentDateString(null));
			map.put("pn_prodid", rs.getObject("pd_prodid"));
			map.put("pn_pdid", rs.getObject("pd_id"));
			map.put("pn_status", "未确认");
			map.put("pn_statuscode", "UNCONFIRM");
			map.put("pn_indate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			map.put("pn_inman", rs.getObject("pd_inoutno"));
			map.put("pn_thisqty", 0);
			map.put("pn_endqty", 0);
			map.put("pn_thisbpqty", 0);
			map.put("pn_remark", rs.getObject("pd_piclass"));
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "PURCHASENOTIFY"));
		}

	}

	/**
	 * prodinout->post->after 采购验退，不良品出库单：反过账后，自动取消送货提醒
	 * 
	 * @author zhongyl
	 */
	public void ProdInout_UnpostPurcNotify(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT pd_inoutno,pd_piclass  from prodiodetail,purchasedetail,purchase WHERE pd_piid="
						+ id
						+ " and prodiodetail.pd_ordercode=purchasedetail.pd_code And prodiodetail.pd_orderdetno=purchasedetail.pd_detno and prodiodetail.pd_ordercode=pu_code and prodiodetail.pd_status=0 ");
		if (rs.next()) {
			baseDao.execute("update PurchaseNotify set pn_status='已取消',pn_statuscode='CANCELED',pn_sendstatus='待上传',pn_cmdremark='反过账出库单 '|| "
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
					+ " where pn_mdid="
					+ id
					+ " and NVL(pn_endqty,0)=0 and pn_status<>'已取消' and pn_remark='"
					+ rs.getString("pd_piclass")
					+ "' and pn_inman='"
					+ rs.getString("pd_inoutno") + "'  ");
		}
	}

	/**
	 * 从收料通知单转的收料单不能删除单一明细行
	 * 
	 * @author zhongyl
	 */
	public void verifyapply_deletedetailFromNotify(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from verifyapplydetail  WHERE vad_id=" + id + " and vad_andid>0");
		if (rs.next()) {
			BaseUtil.showError("来源于从收料通知转单，不允许删除明细行!");
		}
	}

	/**
	 * 出货单：按照出货通知单更新报关价及报关金额
	 * 
	 * @author madan
	 */
	public void prodio_save_before_updatebg(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(gstore, "snd_id"), ",");
		if (StringUtil.hasText(ids))
			baseDao.execute("update ProdIODetail set (pd_customprice,pd_taxamount)=(SELECT snd_discountprice,snd_discountamount FROM SendNotifyDetail WHERE pd_orderid=snd_id) where pd_piclass='出货单' and nvl(pd_snid,0)<>0 and pd_orderid in ("
					+ ids + ")");
	}

	/**
	 * 万利达科技采购单审核同时抛转
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 */
	public void purc_audit_testposttohk(Integer id) {
		boolean needToPost = baseDao.checkIf("purchase", "pu_id=" + id + " and pu_statuscode='AUDITED' and pu_receivecode='02.01.028'");
		if (needToPost)
			purchaseDao.syncPurcToSqlServer(id);
	}

	/**
	 * purchase->purchasechange->audit->before
	 * 采购变更单，审核之后更新对应的销售单的物料信息（易方香港：针对抛转过来的采购变更单）
	 */
	static final String TURNPURCDETAIL = "SELECT pcd_newprodcode,pcd_pddetno,pc_purccode"
			+ " FROM purchasechangedetail left join purchasechange on pcd_pcid=pc_id WHERE pcd_pcid=?";
	static final String UPDATESALEDETAIL = "UPDATE SaleDetail SET sd_prodcode=?" + " WHERE sd_code=? AND sd_detno=?";

	public void purchasechange_audit_updateSale(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(TURNPURCDETAIL, new Object[] { id });
		while (rs.next()) {
			baseDao.execute(UPDATESALEDETAIL,
					new Object[] { rs.getObject("pcd_newprodcode"), rs.getObject("pc_purccode"), rs.getObject("pcd_pddetno") });
		}
	}

	/**
	 * 采购单：提交时去价格库找是否有限购量的设置，如果有，并且本次数量加上历史的采购总数超过限购数量，则提交后无效采购核价单的价格
	 * purchase->purchasemaxlimit->commit->after
	 */
	static final String selectpurstr = "SELECT pu_id,pu_vendcode,pu_currency,pd_price,pd_prodcode,pd_qty,pd_rate,pu_code,pd_detno"
			+ " FROM purchasedetail left join purchase on pd_puid=pu_id WHERE pd_puid=? and nvl(pu_ordertype,' ')<>'B2C'";

	public void purchase_commit_checkmaxlimit(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(selectpurstr, new Object[] { id });
		boolean flag = false;
		while (rs.next()) {
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_maxlimit,ppd_id from purchasepricedetail left join purchaseprice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and ppd_price=? and ppd_rate=? and ppd_statuscode='VALID' and pp_kind='采购' and nvl(ppd_maxlimit,0)>0",
							new Object[] { rs.getObject("pu_vendcode"), rs.getObject("pd_prodcode"), rs.getObject("pu_currency"),
									rs.getObject("pd_price"), rs.getObject("pd_rate") });
			if (rs1.next()) {
				double pursumqty = baseDao.getSummaryByField("purchasedetail left join purchase on pd_puid=pu_id",
						"case when pd_mrpstatuscode='FINISH' then pd_acceptqty else pd_qty end",
						"pu_vendcode='" + rs.getObject("pu_vendcode") + "' and pu_currency='" + rs.getObject("pu_currency")
								+ "' and pd_prodcode='" + rs.getObject("pd_prodcode") + "' and pd_price='" + rs.getObject("pd_price")
								+ "' and pd_rate=" + rs.getObject("pd_rate")
								+ " and trunc(pu_date)>=(select trunc(NVL(ppd_fromdate,sysdate)) from purchasepricedetail where ppd_id="
								+ rs1.getGeneralInt("ppd_id") + ") and nvl(pu_ordertype,' ')<>'B2C'");
				/**
				 * @author wsy 双单位
				 */
				Object pr_purcrate = baseDao.getFieldDataByCondition("product",
						"case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end", "pr_code='" + rs.getObject("pd_prodcode") + "'");
				// pursumqty/case when nvl(pr_purcrate,0)=0 then 1 else
				// pr_purcrate end
				if (rs1.getGeneralDouble("ppd_maxlimit") <= (pursumqty / Double.parseDouble(pr_purcrate.toString()))) {
					baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'超采购限量'||'采购单号"
							+ rs.getObject("pu_code") + "' where ppd_id=" + rs1.getGeneralInt("ppd_id"));
					flag = true;
				}
			}
		}
		// 执行比例分配表的异动更新 可能有部分报价达到限量无效
		// 有失效价格才需要更新操作
		if (flag && baseDao.isDBSetting("vendorRate")) {
			try {
				baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { "异动更新" });
			} catch (Exception e) {
			}
		}
	}

	public void purchase_commit_mobcheckmaxlimit2(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(selectpurstr, new Object[] { id });
		while (rs.next()) {
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_maxlimit,ppd_id from purchasepricedetail left join purchaseprice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and ppd_price=? and ppd_rate=? and ppd_statuscode='VALID' and pp_kind='采购' and nvl(ppd_maxlimit,0)>0",
							new Object[] { rs.getObject("pu_vendcode"), rs.getObject("pd_prodcode"), rs.getObject("pu_currency"),
									rs.getObject("pd_price"), rs.getObject("pd_rate") });
			if (rs1.next()) {
				double pursumqty = baseDao.getSummaryByField("purchasedetail left join purchase on pd_puid=pu_id",
						"case when pd_mrpstatuscode='FINISH' then pd_acceptqty else pd_qty end",
						"pu_vendcode='" + rs.getObject("pu_vendcode") + "' and pu_currency='" + rs.getObject("pu_currency")
								+ "' and pd_prodcode='" + rs.getObject("pd_prodcode") + "' and pd_price='" + rs.getObject("pd_price")
								+ "' and pd_rate=" + rs.getObject("pd_rate") + " and (pu_status<>'在录入' or pu_id=" + id
								+ ") and trunc(pu_date)>=(select trunc(NVL(ppd_fromdate,sysdate)) from purchasepricedetail where ppd_id="
								+ rs1.getGeneralInt("ppd_id") + ")");
				if (rs1.getGeneralDouble("ppd_maxlimit") < pursumqty) {
					baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='失效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'超采购限量'||'采购单号"
							+ rs.getObject("pu_code") + "' where ppd_id=" + rs1.getGeneralInt("ppd_id"));
					BaseUtil.showError("明细序号" + rs.getObject("pd_detno") + "累计下单数[" + pursumqty + "]不能超过限购量["
							+ rs1.getGeneralDouble("ppd_maxlimit") + "]！");
				}
				if (rs1.getGeneralDouble("ppd_maxlimit") == pursumqty) {
					baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='失效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'超采购限量'||'采购单号"
							+ rs.getObject("pu_code") + "' where ppd_id=" + rs1.getGeneralInt("ppd_id"));
				}
			}
		}
	}

	/**
	 * 复制料号 自动跳流水号
	 **/
	public void copy_product_after(Integer oldid, Integer newid) {
		// 万利达 物料种类 4类
		Object[] data = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_code", "pr_kind", "pr_kind2", "pr_kind3",
				"pr_xikind" }, "pr_id=" + oldid);
		if (data[0] != null && data[1] != null && !data[1].equals("") && data[2] != null && !data[2].equals("") && data[3] != null
				&& !data[3].equals("") && data[4] != null && !data[4].equals("")) {
			String code = data[0].toString();
			Object kindid = baseDao
					.getFieldDataByCondition("ProductKind", "pk_id", "pk_code='" + code.substring(0, 2) + "' and pk_subof=0");
			Object kind2id = baseDao.getFieldDataByCondition("ProductKind", "pk_id", "pk_code='" + code.substring(2, 4) + "' and pk_subof="
					+ kindid);
			Object kind3id = baseDao.getFieldDataByCondition("ProductKind", "pk_id", "pk_code='" + code.substring(4, 6) + "' and pk_subof="
					+ kind2id);
			Object kind4d = baseDao.getFieldDataByCondition("ProductKind", "pk_id", "pk_code='" + code.substring(6, 8) + "' and pk_subof="
					+ kind3id);
			try {
				String num = productKindService.getProductKindNum(Integer.parseInt(kind4d.toString()), "");
				num = code.substring(0, 8) + "" + num;
				if (num != null) {
					baseDao.updateByCondition("Product", "pr_code='" + num + "'", "pr_id=" + newid);
				}
			} catch (Exception exe) {
				BaseUtil.showErrorOnSuccess("获取物料流水出错!" + exe.getMessage());
			}
		}

	}

	/**
	 * 正常订单默认交货日期(易方)
	 **/
	public void sale_save_delivery(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sa_id = store.get("sa_id");
		if (sa_id != null) {
			baseDao.execute(
					"update saledetail set sd_delivery=(select sa_recorddate+35 from sale where sa_id=sd_said) where sd_said=? and sd_delivery is null",
					sa_id);
		}
	}

	/**
	 * 非正常订单默认交货日期(易方)
	 **/
	public void SaleAbnormal_save_delivery(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sa_id = store.get("sa_id");
		if (sa_id != null) {
			baseDao.execute(
					"update saledetail set sd_delivery=(select sa_recorddate+40 from sale where sa_id=sd_said) where sd_said=? and sd_delivery is null",
					sa_id);
		}
	}

	/**
	 * Sale->sale->commit 销售订单：订单类型为深圳试产订单，单价为0不允许提交!(万利达)
	 */
	public void sale_commit_salekind_checkprice(Integer sa_id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sd_detno) from SaleDetail left join Sale on sd_said=sa_id left join SaleKind on sa_kind=sk_name where nvl(sk_allowzero,0)<>0 AND nvl(sd_price,0)=0 AND sd_said=?",
						String.class, sa_id);
		if (err != null) {
			BaseUtil.showError("该订单类型不允许0单价! 明细行号：" + err);
		}
	}

	/**
	 * 销售排程筛选之前执行SetSaleDelivery冲减已发货数量
	 */
	public void SaleDetailDet_QueryBefore(String condition) {
		condition = "";// 强制更新所有的
		if (condition == null || condition.equals("")) {
			condition = " 1=1";
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select * from (select sd_id,sd_code,sd_detno,sd_prodcode,sd_qty,sd_sendqty,sum(sdd_qty)qty from sale inner join salekind on (sa_kind=sk_name and sk_mrp<>0) inner join saledetail on sa_id=sd_said left join saledetaildet on sd_id=sdd_sdid where sa_statuscode='AUDITED'  and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and "
						+ condition
						+ " group by sd_id,sd_code,sd_detno,sd_prodcode,sd_qty,sd_sendqty) where sd_qty-NVL(sd_sendqty,0)<>NVL(qty,0)");
		while (rs.next()) {
			// 更新排程
			// baseDao.procedure("SCM_SALE_SETSALEDELIVERY", new Object[] {
			// "sd_id=" + rs.getString("sd_id") });
			saleDetailDetService.SetSaleDelivery("sd_id=" + rs.getString("sd_id"));
		}
	}

	/**
	 * sale->sendnotify->commit
	 * 出货通知单：检查当前业务员最近几天内紧急程度为“紧急”的是否有超过3笔（包括当前单据），如果有提示已经超过3笔紧急的单据，不能提交
	 */
	public void sale_sennotify_commit_emergency(Integer sn_id) {
		int count = baseDao.getCount("select count(*) from sendnotify where sn_recorder=(select sn_recorder from sendnotify where sn_id="
				+ sn_id + ") and sn_emergency='特急' and sysdate-sn_date <= 7");
		if (count > 3) {
			BaseUtil.showError("当前录入人最近几天内紧急程度为“特急”的超过3笔,不允许提交!");
		}
	}

	/**
	 * 出货通知单：保存更新之后，如果明细行存在订单，抓取订单单价(易方)
	 **/
	public void sendnotify_save_before_getprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sdid = null;
		Object sndid = null;
		for (Map<Object, Object> s : gstore) {
			sdid = s.get("snd_sdid");
			sndid = s.get("snd_id");
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				baseDao.execute("update sendnotifydetail set snd_sendprice=(select sd_price from saledetail where sd_id=?) where snd_id=?",
						sdid, sndid);
				baseDao.execute(
						"update sendnotifydetail set snd_total=round(snd_sendprice*snd_outqty,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6) where snd_id=?",
						sndid);
				baseDao.execute("update sendnotifydetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_id=?", sndid);
			}
		}
	}

	/**
	 * 反馈编号：2017050359
	 * 
	 * @author wsy 出货通知单：明细行存在订单且当客户和应收客户一致时，才抓取订单单价
	 **/
	public void sendnotify_save_after_getprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object sdid = null;
		Object sndid = null;
		if (store.get("sn_custcode") != null && store.get("sn_arcustcode") != null
				&& store.get("sn_custcode").equals(store.get("sn_arcustcode"))) {
			SqlRowList rs = baseDao.queryForRowSet("select * from sendnotifydetail where snd_snid=" + store.get("sn_id"));
			while (rs.next()) {
				sdid = rs.getInt("snd_sdid");
				sndid = rs.getInt("snd_id");
				if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
					baseDao.execute(
							"update sendnotifydetail set snd_sendprice=(select sd_price from saledetail where sd_id=?) where snd_id=?",
							sdid, sndid);
					baseDao.execute(
							"update sendnotifydetail set snd_total=round(snd_sendprice*snd_outqty,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6) where snd_id=?",
							sndid);
					baseDao.execute("update sendnotifydetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_id=?", sndid);
				}
			}
		}
	}

	/**
	 * 反馈编号：2017050359
	 * 
	 * @author wsy 出货单：明细行存在订单且当客户和应收客户一致时，才抓取订单单价
	 **/
	public void sale_save_after_getprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pd_ordercode = null;
		Object pd_id = null;
		Object pd_orderdetno = null;
		if (store.get("pi_cardcode") != null && store.get("pi_arcode") != null && store.get("pi_cardcode").equals(store.get("pi_arcode"))) {
			SqlRowList rs = baseDao.queryForRowSet("select * from prodiodetail where pd_piid=" + store.get("pi_id"));
			while (rs.next()) {
				pd_ordercode = rs.getString("pd_ordercode");
				pd_orderdetno = rs.getString("pd_orderdetno");
				pd_id = rs.getInt("pd_id");
				if (pd_ordercode != null && !pd_ordercode.equals("")) {
					baseDao.execute(
							"update ProdIODetail set pd_sendprice=(select sd_price from saledetail where sd_code=? and sd_detno=?) where pd_id=?",
							pd_ordercode, pd_orderdetno, pd_id);
					baseDao.execute(
							"update ProdIODetail set pd_ordertotal=round(pd_sendprice*pd_outqty,2),pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),6) where pd_id=?",
							pd_id);
					baseDao.execute("update ProdIODetail set pd_nettotal=round(pd_netprice*pd_outqty,2) where pd_id=?", pd_id);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->CheckIn->delete->before 出货单，销售退货单：删除前，判断已转入发票箱单，不允许删除
	 */
	public void prodInOut_sale_delete_PaInCheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select  wm_concat(pi_inoutno) from prodinout where pi_invoicecode in (select pi_code from packing) and pi_id=" + id,
				String.class);
		if (dets != null) {
			BaseUtil.showError("当前单据有关联的发票箱单,不允许删除操作！");
		}
	}

	/**
	 * scm->sale->save->after 销售订单：保存更新之后，从表税率强行跟客户资料一致(善领)
	 */
	public void sale_save_after_updatetaxrate(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.execute("update SaleDetail set sd_taxrate=(select nvl(cu_taxrate,0) from customer left join sale on sa_custcode=cu_code where sd_said=sa_id) where sd_said="
				+ store.get("sa_id"));
	}

	/**
	 * scm->save->saleforecast->commit->before 销售预测单提交前，判断出货日期必须大于等于当天+30
	 */
	public void saleforecast_commit_before_needdateCheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sd_detno) from SaleForecastDetail left join SaleForecast on sd_sfid=sf_id where sd_needdate-date<30 and sf_needkind='预测订单' and sd_sfid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("预测类型为预测订单的出货日期必须小于当天+30,不允许提交操作！行号：" + dets);
		}
	}

	/**
	 * scm->Sale->audit->>after 销售订单审核后，收款情况中插入一条记录,帕诺迪专用
	 */
	public void sale_audit_after_insertSalePayment(Integer id) {
		int countnum = baseDao.getCount("Select count(*) from salepayment where sp_saleid=" + id);
		if (countnum > 0) {
			Object[] objs = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_currency", "sa_pocode" }, "sa_id=" + id);
			Object[] objsdetail = baseDao.getFieldsDataByCondition("saledetail", new String[] { "sum(sd_qty)", "sum(sd_total)" },
					" sd_said=" + id);
			String sqlstr = "update SalePayment set sp_pocode='" + objs[1] + "',sp_currency='" + objs[0] + "',sp_qty='" + objsdetail[0]
					+ "',sp_total='" + objsdetail[1] + "' where sp_saleid=" + id;
			baseDao.execute(sqlstr);
		} else {
			Object[] objs = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_currency", "sa_pocode" }, "sa_id=" + id);
			Object[] objsdetail = baseDao.getFieldsDataByCondition("saledetail", new String[] { "sum(sd_qty)", "sum(sd_total)" },
					" sd_said=" + id);
			int sp_id = baseDao.getSeqId("SalePayment_seq");
			String sqlstr = "insert into SalePayment(sp_id,sp_saleid,sp_pocode,sp_currency,sp_qty,sp_total)values(" + sp_id + "," + id
					+ ",'" + objs[1] + "','" + objs[0] + "','" + objsdetail[0] + "','" + objsdetail[1] + "')";
			baseDao.execute(sqlstr);
		}
	}

	/**
	 * scm->Inquiry->commit->>before 询价单提交前，判断供应商币别税率是否一致
	 */
	public void inquiry_commit_before_checkvendor(Integer id) {
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("Inquirydetail left join inquiry on in_id=id_inid", new String[] {
				"id_vendcode", "id_currency", "id_rate" }, "id_inid=" + id);
		for (Object[] obj : objects) {
			int count = baseDao.getCount("select count(*) from vendor where ve_code='" + obj[0] + "' and ve_currency='" + obj[1]
					+ "' and ve_taxrate='" + obj[2] + "'");
			if (count == 0) {
				BaseUtil.showError("供应商编号:[" + obj[0] + "]币别或税率不一致!<BR/>");
			}
		}
	}

	/**
	 * scm->PurchasedetaiPricecheck->commit->>after 采购单提交后去失效定价为临时的核价单
	 */
	public void purchasedetail_commit_pricestatus(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pu_vendcode,pu_currency,pd_rate,pd_price,pd_prodcode,pu_code from Purchasedetail left join purchase on pd_puid=pu_id where pd_puid=? and nvl(pu_ordertype,' ')<>'B2C'",
						id);
		boolean flag = false;
		while (rs.next()) {
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_id,pp_currency from purchasepricedetail left join purchaseprice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_currency=? and ppd_rate=? and ppd_price=? and ppd_prodcode=? and ppd_statuscode='VALID'",
							rs.getObject("pu_vendcode"), rs.getObject("pu_currency"), rs.getObject("pd_rate"), rs.getObject("pd_price"),
							rs.getObject("pd_prodcode"));
			if (rs1.next()) {
				if ("临时".equals(rs1.getGeneralString("pp_currency"))) {
					baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark='临时物料购买一次无效，采购单号["
							+ rs.getObject("pu_code") + "]' where ppd_id=" + rs1.getGeneralInt("ppd_id"));
					flag = true;
				}
			}
		}
		// 执行比例分配表的异动更新 可能有部分报价达到限量失效
		if (flag && baseDao.isDBSetting("vendorRate")) {
			try {
				baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { "异动更新" });
			} catch (Exception e) {
			}
		}
	}

	/**
	 * scm->PurchasePrice->commit->>before 核价单提交前，更新供应商币别税率一致
	 */
	public void purchaseprice_commit_before_updatevendor(Integer id) {
		String sqlstr = "update purchasepricedetail set (ppd_currency,ppd_rate)=(select ve_currency,ve_taxrate from vendor where ve_code=ppd_vendcode) where ppd_ppid="
				+ id;
		baseDao.execute(sqlstr);
	}

	/**
	 * scm->PurchasePrice->save->after 采购核价单:保存和更新时强行按供应商资料，币别、税率(善领)
	 */
	public void PurchasePrice_save_after_updateothers(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.execute("update PurchasePriceDetail set (ppd_currency,ppd_rate)=(select ve_currency,nvl(ve_taxrate,0) from vendor where ve_code=ppd_vendcode) where ppd_ppid="
				+ store.get("pp_id"));
	}

	/**
	 * 善领转入通知单 限制不能超过预约数
	 * */
	public void Sale_turnSendNotify_checkAssignQty(ArrayList<Map<Object, Object>> maps) {
		Object whcode = null;
		Object prodcode = null;
		float turnqty = 0;
		float onhand = 0;
		float assignqty = 0;
		int index = 0;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			index++;
			whcode = map.get("pw_whcode");
			turnqty = Float.parseFloat(map.get("sd_tqty").toString());
			if (whcode == null) {
				sb.append("[" + index + "] 行仓库编号为空");
			} else {
				prodcode = map.get("sd_prodcode");
				baseDao.updateByCondition("ProductWH",
						"pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail  where  snd_prodcode='"
								+ prodcode + "' and snd_warehousecode='" + whcode + "' and nvl(snd_statuscode,' ')<>'FINISH' ),0)",
						"pw_whcode='" + whcode + "' and pw_prodcode='" + prodcode + "'");
				// 当前预约数
				Object[] data = baseDao.getFieldsDataByCondition("ProductWH", "pw_onhand,pw_assignqty", "pw_prodcode='" + prodcode
						+ "' and pw_whcode='" + whcode + "'");
				onhand = Float.parseFloat(data[0].toString());
				assignqty = Float.parseFloat(data[1].toString());
				if (turnqty > onhand - assignqty) {
					sb.append("物料:[" + prodcode + "] 当前仓库库存 :" + onhand + " 预约数:" + assignqty + "剩余数量:" + (onhand - assignqty)
							+ " 小于本次转数量:" + turnqty + "</br>");
				}

			}
		}
		if (sb.length() > 2) {
			BaseUtil.showError(sb.toString());
		}
	}

	/**
	 * 生成通知单更新预约数
	 * */
	public void Sale_turnSendNotify_UpdateAssignQty(List<Map<Object, Object>> maps) {
		Object whcode = null;
		Object prodcode = null;
		for (Map<Object, Object> map : maps) {
			whcode = map.get("pw_whcode");
			prodcode = map.get("sd_prodcode");
			baseDao.updateByCondition("ProductWH",
					"pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail  where  snd_prodcode='" + prodcode
							+ "' and snd_warehouse='" + whcode + "' and nvl(snd_statuscode,' ')<>'FINISH' ),0)", "pw_whcode='" + whcode
							+ "' and pw_prodcode='" + prodcode + "'");
		}
	}

	/**
	 * 更新/保存 更新通知的那预约数
	 * */
	public void Save_UpdateAssignQty(HashMap<Object, Object> map, ArrayList<Map<Object, Object>> maps) {
		for (Map<Object, Object> gridmap : maps) {
			if (gridmap.get("snd_warehousecode") != null) {
				baseDao.updateByCondition("ProductWH",
						"pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail  where  snd_prodcode='"
								+ gridmap.get("snd_prodcode") + "' and snd_warehousecode='" + gridmap.get("snd_warehousecode")
								+ "' and nvl(snd_statuscode,' ')<>'FINISH' ),0)", "pw_whcode='" + gridmap.get("snd_warehousecode")
								+ "' and pw_prodcode='" + gridmap.get("snd_prodcode") + "'");
			}
		}
	}

	/**
	 * 挂起/反挂起 预约数
	 * */
	public void Huang_UpdateAssignQty(Integer id) {
		baseDao.execute("update productWh set pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail where (snd_warehousecode,snd_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_snid="
				+ id
				+ ")),0) where (pw_whcode,pw_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_snid="
				+ id + ")");
	}

	/**
	 * 删除通知
	 * */
	public void Delete_UpdateAssignQty(Integer id) {
		float qty = 0;
		String prodcode = "";
		String whcode = "";
		baseDao.execute("update productWh set pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail where (snd_warehousecode,snd_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_snid="
				+ id
				+ ")),0) where (pw_whcode,pw_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_snid="
				+ id + "  and nvl(snd_statuscode,' ')<>'FINISH')");
		SqlRowList sl = baseDao
				.queryForRowSet("select snd_prodcode,snd_outqty,nvl(snd_havesend,0),snd_warehousecode from  sendnotifydetail where snd_snid="
						+ id);
		while (sl.next()) {
			prodcode = sl.getString(1);
			whcode = sl.getString(4);
			qty = sl.getFloat(2) - sl.getFloat(3);
			baseDao.execute("update productWh set pw_assignqty=pw_assignqty-" + qty + " where pw_whcode='" + whcode + "' and pw_prodcode='"
					+ prodcode + "'");
		}
	}

	/**
	 * 删除明细更新预约数
	 * */
	public void DeleteDetail_UpdateAssignQty(Integer id) {
		float qty = 0;
		String prodcode = "";
		String whcode = "";
		baseDao.execute("update productWh set pw_assignqty=nvl((select sum(snd_outqty-nvl(snd_havesend,0)) from sendnotifydetail where where (snd_warehousecode,snd_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_id="
				+ id
				+ ")),0) where (pw_whcode,pw_prodcode) in (select snd_warehousecode,snd_prodcode from sendnotifydetail where snd_id="
				+ id + "  and nvl(snd_statuscode,' ')<>'FINISH')");
		SqlRowList sl = baseDao
				.queryForRowSet("select snd_prodcode,snd_outqty,nvl(snd_havesend,0),snd_warehousecode from  sendnotifydetail where snd_snid="
						+ id);
		while (sl.next()) {
			prodcode = sl.getString(1);
			whcode = sl.getString(4);
			qty = sl.getFloat(2) - sl.getFloat(3);
			baseDao.execute("update productWh set pw_assignqty=pw_assignqty-" + qty + " where pw_whcode='" + whcode + "' and pw_prodcode='"
					+ prodcode + "'");
		}
	}

	/**
	 * 收料单：保存更新之前根据物料大类，批号需要手工录入(国扬)
	 * 
	 * @author madan 2014-5-4 18:48:17
	 * */
	public void verifyapply_save_before_batchcheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object prodcode = null;
		Object vaddetno = null;
		Object batchcode = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			prodcode = s.get("vad_prodcode");
			vaddetno = s.get("vad_detno");
			batchcode = s.get("vad_batchcode");
			if (prodcode != null && !"".equals(prodcode.toString()) && (batchcode == null || "".equals(batchcode))) {
				String kind = baseDao.getFieldValue("Product", "pr_kind",
						"pr_kind in ('CWDM模块','PCBA板','PCB板','光开关','结构件','模拟光模块','模拟光器件','数字光模块','数字光器件','数字器件材料','无源半成品','无源器件材料') and pr_code='"
								+ prodcode + "'", String.class);
				if (kind != null) {
					sb.append("[" + vaddetno + "] 行物料[" + prodcode + "]大类为" + kind + ",批号需要手工录入！");
				}
			}
		}
		if (sb.length() > 2) {
			BaseUtil.showError(sb.toString());
		}
		String batchCodesString = CollectionUtil.pluckSqlString(gstore, "vad_batchcode");
		String existCodes = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(ba_code) from batch where ba_code in(" + batchCodesString + ")", String.class);
		if (existCodes != null) {
			BaseUtil.showError("批号已经存在:" + existCodes);
		}
		existCodes = CollectionUtil.getRepeats(gstore, "vad_batchcode");
		if (existCodes != null) {
			BaseUtil.showError("批号填写重复:" + existCodes);
		}
	}

	/**
	 * 收料单：提交之前根据物料大类，批号需要手工录入(国扬)
	 * 
	 * @author madan 2014-5-4 19:14:03
	 * */
	public void verifyapply_commit_before_batchcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vad_detno) from VerifyApplyDetail left join Product on vad_prodcode=pr_code where pr_kind in ('CWDM模块','PCBA板','PCB板','光开关','结构件','模拟光模块','模拟光器件','数字光模块','数字光器件','数字器件材料','无源半成品','无源器件材料') and nvl(vad_batchcode,' ')=' ' and vad_vaid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("物料大类为CWDM模块,PCBA板,PCB板,光开关,结构件,模拟光模块,模拟光器件,数字光模块,数字光器件,数字器件材料,无源半成品,无源器件材料,批号需要手工录入！行号：" + dets);
		}
		// 判断批号是否重复
		String errRows = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(vad_detno) from VerifyApplydetail where vad_vaid=? and vad_batchcode in (select ba_code from batch)",
				String.class, id);
		if (errRows != null) {
			BaseUtil.showError("批号重复,行:" + errRows);
		}
	}

	/**
	 * 请购单：当需求日期小于录入日期+采购提前期时，将需求日期更新成录入日期+采购提前期
	 * 
	 * @author madan 2014-5-5 19:22:50 zhongyl 2015-1-27 修改
	 **/
	public void application_save_delivery(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.execute("UPDATE APPLICATIONDETAIL SET AD_DELIVERY=NVL((SELECT case when AD_DELIVERY>sysdate+NVL(PR_LEADTIME,0) then AD_DELIVERY else sysdate+NVL(PR_LEADTIME,0) end  FROM PRODUCT WHERE PR_CODE=AD_PRODCODE),AD_DELIVERY) WHERE ad_apid="
				+ store.get("ap_id"));
	}

	/**
	 * scm->prodinout->commit->before 拨出单提交前，类型为外借、外借返还的，明细行外借人和部门编号没有填写的限制不让提交
	 * 
	 * @author madan 2014-5-8 15:30:21
	 */
	public void AppropriationOut_commit_before_typecheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_pdno) from ProdInOut left join ProdIODetail on pi_id=pd_piid where pi_type in ('外借','外借返还') and nvl(pd_sellercode,' ')=' ' and pd_piid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("拨出单类型为外借、外借返还，明细行外借人为空,不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_pdno) from ProdInOut left join ProdIODetail on pi_id=pd_piid where pi_type in ('外借','外借返还') and nvl(pd_departmentcode,' ')=' ' and pd_piid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("拨出单类型为外借、外借返还，明细行部门为空,不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_pdno) from ProdInOut left join ProdIODetail on pi_id=pd_piid where pi_type='外借返还' and nvl(pd_batchcode,' ')=' ' and pd_piid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("拨出单类型为外借返还，明细行批号为空,不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_pdno) from ProdInOut left join ProdIODetail on pi_id=pd_piid where pi_type='电商发货' and nvl(pd_remark,' ')=' ' and pd_piid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("拨出单类型为电商发货，明细行备注为空,不允许提交!行号：" + dets);
		}
	}

	/**
	 * scm->prodinout->commit->before 拨出单提交前，类型为外借返还时，当批号和外借人、料号不一致时，不允许提交
	 * 
	 * @author madan 2014-5-8 15:37:53
	 */
	public void AppropriationOut_commit_before_batchcheck(Integer id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号:'||pd_pdno||'批号:'||pd_batchcode||'外借人:'||pd_sellercode||'物料编号:'||pd_prodcode) from(select pd_pdno,pd_batchcode,pd_sellercode,pd_prodcode from prodiodetail left join Prodinout on pd_piid=pi_id where pd_piid=? and pi_type='外借返还' and  not exists (select ba_code,ba_sellercode,ba_prodcode from batch where ba_code=pd_batchcode and ba_prodcode=pd_prodcode and ba_sellercode=pd_sellercode) )",
						String.class, id);
		if (err != null) {
			BaseUtil.showError("批号+外借人+料号不一致,不允许提交!" + err);
		}
	}

	/**
	 * scm->sale->resAudit->before 销售订单、非正常销售订单:若订单已经开出工单,不允许进行反审核操作!
	 */
	public void sale_resAudit_before_checkmake(Integer sa_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(ma_code) from make left join saledetail on sd_code=ma_salecode and sd_detno=ma_saledetno where sd_said="
						+ sa_id, String.class);
		if (dets != null) {
			BaseUtil.showError("订单已经开出工单,不允许进行反审核操作!工单号：" + dets);
		}
	}

	/**
	 * scm->sale->Finish->before 销售订单、非正常销售订单:若订单已经开出工单未完工,不允许进行结案操作!
	 */
	public void sale_finish_before_makefinish(Integer sa_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(ma_code) from make left join saledetail on sd_code=ma_salecode and sd_detno=ma_saledetno where ma_finishstatus<>'COMPLETED' and sd_said="
								+ sa_id, String.class);
		if (dets != null) {
			BaseUtil.showError("订单开出的工单未完工,不允许进行结案操作!工单号：" + dets);
		}
	}

	/**
	 * scm->sale->commit->before 销售订单:检查物料里的制造件和委外件不再BOM表里存在那就提示，不允许提交!
	 * 
	 * @author madan 2014-5-14 09:32:33
	 */
	public void sale_commit_before_checkbom(Integer sa_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select WM_CONCAT(sd_detno) detno,count(1) num from  saledetail left join product on pr_code=sd_prodcode left join bom on (bo_mothercode=pr_code or bo_mothercode=pr_refno) where sd_said=? and pr_manutype in ('MAKE','OSMAKE') and pr_dhzc='MPS' and (NVL(bo_id,0)=0 or bo_statuscode<>'AUDITED')",
						sa_id);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("制造件和委外件无有效BOM,不允许提交!行号：" + rs.getString("detno") + "");
			}
		}
	}

	/**
	 * scm->saleforecast->commit->before 预测订单:检查物料里的制造件和委外件不再BOM表里存在那就提示，不允许提交!
	 * 
	 * @author zhongyl 2014-5-20 14:49:33
	 */
	public void saleforecast_commit_before_checkbom(Integer sf_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select WM_CONCAT(sd_detno) detno,count(1) num from  saleforecastdetail left join product on pr_code=sd_prodcode left join bom on (bo_mothercode=pr_code or bo_mothercode=pr_refno) where sd_sfid=? and pr_manutype in ('MAKE','OSMAKE') and pr_dhzc='MPS' and (NVL(bo_id,0)=0 or bo_statuscode<>'AUDITED')",
						sf_id);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("制造件和委外件无有效BOM,不允许提交!行号：" + rs.getString("detno") + "");
			}
		}
	}

	/**
	 * 完工入库单过账前，判断入库仓库的冲销属性与工单的销售预测单冲销属性是否匹配
	 * */
	public void ProdIO_Make_CheckClash(Integer id) {
		String err = "";
		// 判断是否有不需要完工冲销的工单入库到完工冲销的仓库
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_ordercode,saleforecastkind.sf_name,wh_code from prodiodetail left join prodinout on pd_piid=pi_id left join warehouse on NVL(pd_whcode,pi_whcode)=wh_code left join make on pd_ordercode=ma_code left join saleforecast on ma_salecode=saleforecast.sf_code left join saleforecastkind on saleforecastkind.sf_name=saleforecast.sf_kind where pd_piid=? and wh_ifclash<>0 and saleforecast.sf_code<>' ' and sf_clashoption<>'FINISH' and sf_clashoption<>'完工冲销' ",
						id);
		while (rs.next()) {
			err += "工单【" + rs.getString("pd_ordercode") + "】对应的预测单类型是【" + rs.getString("sf_name") + "】，不允许完工入库到仓库["
					+ rs.getString("wh_code") + "]";
		}
		if (!err.equals("")) {
			BaseUtil.showError(err);
		}
		// 判断是否有需要冲销的完工入库入到不需要冲销的仓库
		err = "";
		rs = baseDao
				.queryForRowSet(
						"select pd_ordercode,saleforecastkind.sf_name,wh_code from prodiodetail left join prodinout on pd_piid=pi_id left join warehouse on NVL(pd_whcode,pi_whcode)=wh_code left join make on pd_ordercode=ma_code  left join saleforecast on ma_salecode=saleforecast.sf_code left join saleforecastdetail on (saleforecast.sf_code=ma_salecode and sd_detno=ma_saledetno and sd_prodcode=ma_prodcode) left join saleforecastkind on saleforecastkind.sf_name=saleforecast.sf_kind  where pd_piid=? and NVL(wh_ifclash,0)=0 and sd_detno<>0 and (sf_clashoption='FINISH' OR sf_clashoption='完工冲销') ",
						id);
		while (rs.next()) {
			err += "工单【" + rs.getString("pd_ordercode") + "】对应的预测单类型是【" + rs.getString("sf_name") + "】，不允许完工入库到仓库["
					+ rs.getString("wh_code") + "]";
		}
		if (!err.equals("")) {
			BaseUtil.showError(err);
		}
	}

	/**
	 * 完工入库单过账前，判断入库仓库与工单指定的仓库是否一致
	 * */
	public void ProdIO_Make_CheckWareHouse(Integer id) {
		String err = "";
		// 判断是否有不需要完工冲销的工单入库到完工冲销的仓库
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_ordercode,ma_inwhcode,pi_whcode from prodiodetail left join prodinout on pd_piid=pi_id left join make on pd_ordercode=ma_code where pd_piid=? and ma_inwhcode<>' ' and NVL(pd_whcode,pi_whcode)<>ma_inwhcode ",
						id);
		while (rs.next()) {
			err += "工单【" + rs.getString("pd_ordercode") + "】指定的入库仓库是【" + rs.getString("ma_inwhcode") + "】";
		}
		if (!err.equals("")) {
			BaseUtil.showError(err);
		}
	}

	/**
	 * scm->sale->commit->before 销售单提交判断预测单号是否填写正确 针对“单号冲销”类型的销售单，不正确则提示错误
	 * 
	 * @author zhongyl 2014-5-27
	 * 
	 */
	public void sale_commit_CheckForecastCodeOnsuccess(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sa_code,sa_custcode,saledetail.sd_detno sadetno,saledetail.sd_prodcode saprodcode,sk_name,sk_clashoption,sk_clashfor,NVL(sk_clashkind,' ') sk_clashkind,sf_code,sf_custcode,saleforecastdetail.sd_detno sfdetno,saleforecastdetail.sd_prodcode sfprodcode from saledetail left join sale on sa_id=sd_said left join salekind on sk_name=sa_kind left join saleforecast on sf_code=saledetail.sd_forecastcode left join saleforecastdetail on saleforecastdetail.sd_sfid=sf_id and saleforecastdetail.sd_detno=sd_forecastcode where sd_said="
						+ id + " and sk_clashfor in('单号冲销','SALE') ");
		while (rs.next()) {
			if (rs.getObject("sf_code").equals(null)) {
				BaseUtil.showErrorOnSuccess("序号【" + rs.getString("sadetno") + "】预测单号未指定或单号不存在");
			}
			if (rs.getObject("sfdetno").equals(null)) {
				BaseUtil.showErrorOnSuccess("序号【" + rs.getString("sadetno") + "】预测单序号填写不正确，找不到对应的数据");
			}
			if (rs.getObject("sk_clashkind").equals("客户匹配") && !rs.getObject("sf_custcode").equals(null)
					&& !rs.getString("sf_custcode").equals(rs.getString("sa_custcode"))) {
				BaseUtil.showErrorOnSuccess("序号【" + rs.getString("sadetno") + "】合同客户与与指定的预测订单的客户不一致");
			}
		}
	}

	/**
	 * scm->sale->commit->before 销售单提交判断预测单号是否填写正确 针对“单号冲销”类型的销售单，不正确则不允许提交、审核
	 * 
	 * @author zhongyl 2014-5-27
	 */
	public void sale_commit_CheckForecastCode(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sa_code,sa_custcode,saledetail.sd_detno sadetno,saledetail.sd_prodcode saprodcode,sk_name,sk_clashoption,sk_clashfor,NVL(sk_clashkind,' ') sk_clashkind,sf_code,sf_custcode,saleforecastdetail.sd_detno sfdetno,saleforecastdetail.sd_prodcode sfprodcode from saledetail left join sale on sa_id=sd_said left join salekind on sk_name=sa_kind left join saleforecast on sf_code=saledetail.sd_forecastcode left join saleforecastdetail on saleforecastdetail.sd_sfid=sf_id and saleforecastdetail.sd_detno=sd_forecastcode where sd_said="
						+ id + " and sk_clashfor in('单号冲销','SALE') ");
		while (rs.next()) {
			if (rs.getObject("sf_code").equals(null)) {
				BaseUtil.showError("序号【" + rs.getString("sadetno") + "】预测单号未指定或单号不存在");
			}
			if (rs.getObject("sfdetno").equals(null)) {
				BaseUtil.showError("序号【" + rs.getString("sadetno") + "】预测单序号填写不正确，找不到对应的数据");
			}
			if (rs.getObject("sk_clashkind").equals("客户匹配") && !rs.getObject("sf_custcode").equals(null)
					&& !rs.getString("sf_custcode").equals(rs.getString("sa_custcode"))) {
				BaseUtil.showError("序号【" + rs.getString("sadetno") + "】合同客户与与指定的预测订单的客户不一致");
			}
		}
	}

	/**
	 * ProductApproval->audit->before->checkresult 物料认证，在审核前必须填写认证结果(易方)
	 * */
	public void ProductApproval_audit_before_checkresult(Integer id) {
		Object pa_finalresult = baseDao.getFieldDataByCondition("ProductApproval", "pa_finalresult", "pa_id=" + id);
		if (pa_finalresult == null || "".equals(pa_finalresult)) {
			BaseUtil.showError("请保存认证结果再审核！");
		}
	}

	/**
	 * 帕诺蒂采购单审核之后自动跳流水
	 * */
	public void SetPUNO_audit_after(Integer id) {
		String querySql = "select count(1) from purchase where pu_vendcode=(select pu_vendcode from purchase where pu_id=" + id
				+ ") and nvl(pu_no,0)>0";
		int count = baseDao.getCount(querySql) + 1;
		baseDao.updateByCondition("purchase", "pu_no=" + count, "pu_id=" + id);
	}

	/**
	 * 帕诺蒂采购单删除时候判断采购流水号是否为空或为0
	 * */
	public void checkpuno_beforedelete(Integer id) {
		boolean bool = baseDao.checkIf("purchase", "pu_id=" + id + " AND nvl(pu_no,0)>0");
		if (bool) {
			BaseUtil.showError("当前采购单已产生流水号,不能删除!");
		}
	}

	/**
	 * 销售订单：提交之前判断单价是否是2小数(善领)
	 * 
	 * @author madan 2014-6-6 14:58:34
	 **/
	public void sale_commit_before_price(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(sd_detno) from saledetail where sd_said=" + id + " and nvl(sd_price,0)<> nvl(round(sd_price,2),0)",
				String.class);
		if (dets != null) {
			BaseUtil.showError("单价只能填2位小数,不允许提交!明细行号:[" + dets + "]");
		}
	}

	/**
	 * 销售变更单：保存更新之前，判断订单+行号是否开出工单
	 * 
	 * @author madan 2014-6-9 20:15:29
	 **/
	public void salechange_save_before_makecheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		StringBuffer sb = new StringBuffer();
		String err = null;
		for (Map<Object, Object> gridmap : grid) {
			if (gridmap.get("scd_sacode") != null) {
				err = baseDao.getJdbcTemplate().queryForObject(
						"select ma_code from make where ma_salecode=? and ma_saledetno=? and ma_prodcode=?", String.class,
						gridmap.get("scd_sacode"), gridmap.get("scd_sddetno"), gridmap.get("scd_prodcode"));
				if (err != null) {// 反馈2017010565 当明细行存在数量修改时才限制对应是否有开出工单
					if (!gridmap.get("scd_qty").equals(gridmap.get("scd_newqty"))) {
						sb.append("行:").append(gridmap.get("scd_detno"));
						sb.append("工单:").append(err).append("<br>");
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError("销售变更单中存在已开工单的订单，不允许保存！<br>" + sb.toString());
		}
	}

	/**
	 * 销售变更单：提价之前，判断订单+行号是否开出工单
	 * 
	 * @author madan 2014-6-9 20:15:29
	 **/
	public void salechange_commit_before_makecheck(Integer id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号:'||scd_detno||'工单号:'||ma_code) from (select  scd_detno,ma_code from SaleChangeDetail,make  where ma_salecode=scd_sacode and ma_saledetno=scd_sddetno and scd_scid=?)",
						String.class, id);
		int qty = baseDao.getCountByCondition("SaleChangeDetail", "scd_qty<>scd_newqty and scd_scid=" + id);
		if (err != null) {// 反馈2017010565 当明细行存在数量修改时才限制对应是否有开出工单
			if (qty != 0) {
				BaseUtil.showErrorOnSuccess("销售变更单中存在已开工单的订单，不允许提交！<br>" + err);
			}
		}
	}

	/**
	 * 销售订单：提交之前判断信用额度（包含销售订单占用额度）
	 * 
	 * @author madan 2017-05-26 14:21:10
	 **/
	public void sale_commit_before_checkcredit(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select sa_custcode,case when nvl(GETCONFIG('Customer!Base','paymControl'),0)=1 then nvl(pa_creditcontrol,'否') else nvl(cu_enablecredit,'否') end cu_enablecredit from sale,Customer,payments where sa_custcode=cu_code and sa_id=? and SA_PAYMENTScode=pa_code and pa_class='收款方式' and nvl(sa_custcode,' ')<>' ' and nvl(cu_nocreditorder,0)=0",// 2018050334
																																																																																									// maz
																																																																																									// 增加判断新加的cu_nocreditorder【额度包含订单】字段
						id);
		if (rs.next()) {
			String custcode = rs.getString("sa_custcode");
			if ("是".equals(rs.getObject("cu_enablecredit"))) {
				SqlRowList credit = null;
				Master master = SystemSession.getUser().getCurrentMaster();
				Master parentMaster = null;
				// 帐套默认币别
				if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
					parentMaster = master;
				} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
					parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
				}
				if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
						&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
					baseDao.procedure("SP_CALCREDIT", new Object[] { parentMaster.getMa_user(), parentMaster.getMa_soncode(), custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from "
									+ parentMaster.getMa_user()
									+ ".customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				} else {
					baseDao.procedure("SP_CALCREDIT", new Object[] { "", "", custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				}
				if (credit.next()) {
					Object tempcreditdate = credit.getObject("cuc_tempcreditdate");
					double A = credit.getGeneralDouble("cuc_credit");
					double B = credit.getGeneralDouble("cuc_ar"); // 取出这个客户的应收-预收的余额折算为本位币
					double C = credit.getGeneralDouble("cuc_unpost"); // 未过账发货单的金额(变量C)
					double D = credit.getGeneralDouble("cuc_notify"); // 未结案的通知单未转出货单部分(变量D)
					double E = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select nvl(round(nvl(sum((sd_qty)*sd_price*sa_rate),0),2),0) from sale,saledetail,currencys where sa_id=sd_said and sa_currency=cr_name and sa_id=?",
									new Object[] { id }, Double.class);// 本次订单本币金额
					double F = credit.getGeneralDouble("cuc_salecredit");// 已提交审核未结案的订单未转出的订单本币金额
					if (tempcreditdate != null
							&& (DateUtil.compare(tempcreditdate.toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
									tempcreditdate.toString(), DateUtil.getCurrentDate()) == 0)) {
						A = credit.getGeneralDouble("cuc_credit") + credit.getGeneralDouble("cuc_tempcredit");
					}
					if (A < B + C + D + E + F) {
						BaseUtil.showError("已超额度，不允许进行当前操作！客户额度[" + A + "],客户的应收-预收的余额[" + B + "],未过账发货单的金额[" + C + "],未结案的通知单未转出货单部分[" + D
								+ "],本次订单本币金额[" + E + "],已提交审核未结案的订单未转出的订单本币金额[" + F + "]");
					}
				} else {
					BaseUtil.showError("该客户没有设置信用额度！");
				}
			}
		}
	}

	/**
	 * 出货通知单：提交之前判断信用额度（包含销售订单占用额度）
	 * 
	 * @author madan 2014-6-10 15:11:49
	 **/
	public void sendnotify_commit_before_checkcredit(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select sn_custcode,case when nvl(GETCONFIG('Customer!Base','paymControl'),0)=1 then nvl(pa_creditcontrol,'否') else nvl(cu_enablecredit,'否') end cu_enablecredit from sendnotify,Customer,payments where sn_custcode=cu_code and sn_id=? and Sn_PAYMENTSCODE=pa_code and pa_class='收款方式' and nvl(sn_custcode,' ')<>' '",
						id);
		if (rs.next()) {
			String custcode = rs.getString("sn_custcode");
			if ("是".equals(rs.getObject("cu_enablecredit"))) {
				SqlRowList credit = null;
				Master master = SystemSession.getUser().getCurrentMaster();
				Master parentMaster = null;
				if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
					parentMaster = master;
				} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
					parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
					if (parentMaster != null && parentMaster.getMa_soncode() == null) {
						parentMaster = enterpriseService.getMasterByName(BaseUtil.getXmlSetting("defaultSob"));
					}
				}
				if (parentMaster == null) {
					parentMaster = enterpriseService.getMasterByName(BaseUtil.getXmlSetting("defaultSob"));
				}
				if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
						&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
					baseDao.procedure("SP_CALCREDIT", new Object[] { parentMaster.getMa_user(), parentMaster.getMa_soncode(), custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from "
									+ parentMaster.getMa_user()
									+ ".customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				} else {
					baseDao.procedure("SP_CALCREDIT", new Object[] { "", "", custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				}
				if (credit.next()) {
					Object tempcreditdate = credit.getObject("cuc_tempcreditdate");
					double A = credit.getGeneralDouble("cuc_credit");
					double B = credit.getGeneralDouble("cuc_ar"); // 取出这个客户的应收-预收的余额折算为本位币
					double C = credit.getGeneralDouble("cuc_unpost"); // 未过账发货单的金额(变量C)
					double D = credit.getGeneralDouble("cuc_notify"); // 未结案的通知单未转出货单部分(变量D)
					double E = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select nvl(round(nvl(sum((snd_outqty)*snd_sendprice*sn_rate),0),2),0) from sendnotify,sendnotifydetail,currencys where sn_id=snd_snid and sn_currency=cr_name and sn_id=?",
									new Object[] { id }, Double.class);// 本次通知单本币金额
					double F = credit.getGeneralDouble("cuc_salecredit");// 已提交审核未结案的订单未转出的订单本币金额
					if (tempcreditdate != null
							&& (DateUtil.compare(tempcreditdate.toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
									tempcreditdate.toString(), DateUtil.getCurrentDate()) == 0)) {
						A = credit.getGeneralDouble("cuc_credit") + credit.getGeneralDouble("cuc_tempcredit");
					}
					if (baseDao.isDBSetting("creditNoSale")) {
						if (A < B + C + D + E) {
							baseDao.execute("update SENDNOTIFY set sn_outcredit='是', sn_outamount=" + (B + C + D + E - A) + " where sn_id="
									+ id);
						} else {
							baseDao.execute("update SENDNOTIFY set sn_outcredit='否', sn_outamount=0 where sn_id=" + id);
						}
					} else {
						if (A < B + C + D + E + F) {
							baseDao.execute("update SENDNOTIFY set sn_outcredit='是', sn_outamount=" + (B + C + D + E + F - A)
									+ " where sn_id=" + id);
						} else {
							baseDao.execute("update SENDNOTIFY set sn_outcredit='否', sn_outamount=0 where sn_id=" + id);
						}
					}
				} else {
					BaseUtil.showError("该客户没有设置信用额度！");
				}
			}
		}
	}

	/**
	 * 出货通知单：提交之前判断信用额度，是否超额度为是时，不允许进行当前操作
	 * 
	 * @author madan 2014-6-10 15:11:49
	 **/
	public void sendnotify_commit_before_outcredit(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sn_code) from sendnotify,Customer where sn_id=? and sn_custcode=cu_code and nvl(sn_outcredit,' ')='是' and nvl(cu_enablecredit,'否')='是'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已超额度，不允许进行当前操作!");
		}
	}

	/**
	 * 出货单：提交之前判断信用额度
	 * 
	 * @author madan 2015-3-4 14:13:45
	 **/
	public void prodinout_commit_before_checkcredit(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pi_cardcode,case when nvl(GETCONFIG('Customer!Base','paymControl'),0)=1 then nvl(pa_creditcontrol,'否') else nvl(cu_enablecredit,'否') end cu_enablecredit from ProdInOut,Customer,payments where pi_cardcode=cu_code and pi_id=? and pi_PAYMENTCODE=pa_code and pa_class='收款方式' and nvl(pi_cardcode,' ')<>' '",
						id);
		if (rs.next()) {
			String custcode = rs.getString("pi_cardcode");
			if ("是".equals(rs.getObject("cu_enablecredit"))) {
				SqlRowList credit = null;
				Master master = SystemSession.getUser().getCurrentMaster();
				Master parentMaster = null;
				if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
					parentMaster = master;
				} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
					parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
				}
				if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
						&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
					baseDao.procedure("SP_CALCREDIT", new Object[] { parentMaster.getMa_user(), parentMaster.getMa_soncode(), custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from "
									+ parentMaster.getMa_user()
									+ ".customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				} else {
					baseDao.procedure("SP_CALCREDIT", new Object[] { "", "", custcode });
					credit = baseDao
							.queryForRowSet("select cuc_credit,cuc_tempcredit,cuc_tempcreditdate,cuc_ar,cuc_unpost,cuc_notify,cuc_salecredit from customercredit where cuc_statuscode='AUDITED' and cuc_custcode='"
									+ custcode + "'");
				}
				if (credit.next()) {
					Object tempcreditdate = credit.getObject("cuc_tempcreditdate");
					double A = credit.getGeneralDouble("cuc_credit");
					double B = credit.getGeneralDouble("cuc_ar"); // 取出这个客户的应收-预收的余额折算为本位币
					double C = credit.getGeneralDouble("cuc_unpost"); // 未过账发货单的金额(变量C)
					if (tempcreditdate != null
							&& (DateUtil.compare(tempcreditdate.toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
									tempcreditdate.toString(), DateUtil.getCurrentDate()) == 0)) {
						A = credit.getGeneralDouble("cuc_credit") + credit.getGeneralDouble("cuc_tempcredit");
					}
					if (A < B + C) {
						baseDao.execute("update ProdInOut set pi_outcredit='是', pi_outamount=" + (B + C - A) + " where pi_id=" + id);
					} else {
						baseDao.execute("update ProdInOut set pi_outcredit='否', pi_outamount=0 where pi_id=" + id);
					}
				} else {
					BaseUtil.showError("该客户没有设置信用额度！");
				}
			}
		}
	}

	/**
	 * 出货单：提交之前判断信用额度，是否超额度为是时，不允许进行当前操作
	 * 
	 * @author madan 2015-3-4 14:13:54
	 **/
	public void prodinout_commit_before_outcredit(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pi_inoutno) from ProdInOut,Customer where pi_id=? and pi_cardcode=cu_code and nvl(pi_outcredit,' ')='是' and nvl(cu_enablecredit,'否')='是'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已超额度，不允许进行当前操作!");
		}
	}

	/**
	 * 采购单：提交之前判断信用额度
	 * 
	 * @author madan 2015-04-14 09:12:59
	 **/
	public void purchase_commit_before_checkcredit(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select vec_credit,pu_vendcode from purchase left join vendorcredit on pu_vendcode=vec_vendcode where vec_statuscode='AUDITED' and pu_id="
						+ id);
		if (rs.next()) {
			double A = Double.parseDouble(rs.getObject("vec_credit").toString());
			if (A > 0) {
				// 取出这个供应商的应付-预付的余额折算为本位币
				Double B = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select nvl(round(sum(nvl((nvl(va_amount,0)-nvl(va_prepayamount,0))*nvl(cr_vorate,0),0)),0),0) from vendap,currencys where va_currency=cr_name and va_vendcode=?",
								new Object[] { rs.getObject("sn_custcode") }, Double.class);
				// 未过账采购验收单的金额(变量C)
				Double C = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select nvl(round(sum(nvl(pd_inqty*pd_orderprice,0)),0),0) from prodinout,prodiodetail,currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='采购验收单' and pi_cardcode=?",
								new Object[] { rs.getObject("pu_vendcode") }, Double.class);
				// 已提交未结案的采购单未转入库部分(变量D)
				Double D = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select nvl(round(nvl(sum((pd_qty-nvl(pd_yqty,0))*pd_price*cr_vorate),0),0),0) from purchase,purchasedetail,currencys where pu_id=pd_puid and pu_currency=cr_name and pu_vendcode=? and pu_statuscode in ('COMMITED','AUDITED') and nvl(pd_yqty,0)>0",
								new Object[] { rs.getObject("pu_vendcode") }, Double.class);
				Double E = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select nvl(round(nvl(sum((pd_qty)*pd_price*cr_vorate),0),0),0) from purchase,purchasedetail,currencys where pu_id=pd_puid and pu_currency=cr_name and pu_id=?",
								new Object[] { id }, Double.class);
				if (A < B + C + D + E) {
					baseDao.execute("update purchase set pu_outcredit=-1, pu_outamount=" + (B + C + D + E - A) + " where pu_id=" + id);
				} else {
					baseDao.execute("update purchase set pu_outcredit=0, pu_outamount=0 where pu_id=" + id);
				}
			}
		}
	}

	/**
	 * 采购单：提交之前判断信用额度，是否超额度为是时，不允许进行当前操作
	 * 
	 * @author madan 2015-04-14 09:20:00
	 **/
	public void purchase_commit_before_outcredit(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pu_code) from purchase where pu_id=? and nvl(pu_outcredit,0)=-1", String.class, id);
		if (dets != null) {
			BaseUtil.showError("已超额度，不允许进行当前操作!");
		}
	}

	/**
	 * 询价单：提交之前更新有效期为客户报价有效期
	 * 
	 * @author shenj 2015-05-26 09:20:00
	 **/
	public void inquiry_commit_before_checkdate(Integer id) {
		baseDao.execute("update inquirydetail set id_myfromdate=id_fromdate,id_mytodate=id_todate where id_todate is not null and id_inid="
				+ id);
	}

	/**
	 * sale->commit->before-> ATPDelivery
	 * 订单提交前判断是否已经有atp运算的承诺交期sd_atpdelivery（宇声）
	 * */
	public void sale_commit_haveRunATP(Integer id) {
		// 判断是否有不需要完工冲销的工单入库到完工冲销的仓库
		SqlRowList rs = baseDao.queryForRowSet("select * from saledetail where sd_said=? and sd_atpdelivery is null ", id);
		if (rs.next()) {
			BaseUtil.showError("没有运算出ATP交期，不能提交订单!");
		}
	}

	/**
	 * 采购验收单，采购验退单：提交过账之前判断供应商是否禁用
	 * 
	 * @author madan 2014-6-14 09:17:32
	 **/
	public void prodinout_commit_before_vendorcheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pi_cardcode) from prodinout left join vendor where pi_cardcode=ve_code where pi_id=" + id
						+ " and ve_auditstatuscode='DISABLE'", String.class);
		if (dets != null) {
			BaseUtil.showError("供应商[" + dets + "]已禁用，不允许进行当前操作!");
		}
	}

	/**
	 * 生产退料单提交时判定退料原因必须一致
	 * 
	 * @author zhongyl 2014-6-14 11:57:32
	 */
	public void ProdIO_commit_MakeReturnOneReson(Integer pi_id) {
		// 判断是否有不需要完工冲销的工单入库到完工冲销的仓库
		SqlRowList rs = baseDao.queryForRowSet("select distinct pd_description from prodiodetail where pd_piid=? and rownum>1 ", pi_id);
		if (rs.next()) {
			BaseUtil.showError("退料原因必须一致!");
		}
	}

	/**
	 * 采购单转收料单前，判断采购收料单的日期只能比采购单中的需求日期早2天(乔晶)
	 * 
	 * @author madan 2014-6-16 15:50:57
	 */
	public void purchase_turnVerifyApply_before(ArrayList<HashMap<Object, Object>> maps) {
		String ids = CollectionUtil.pluckSqlString(maps, "pd_id");
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pd_code) from PurchaseDetail where pd_id in(" + ids + ") and pd_delivery-date>2", String.class);
		if (dets != null) {
			BaseUtil.showError("转采购收料单的日期只能比采购单中的需求日期早2天!<br>采购单号：" + dets);
		}
	}

	/**
	 * 销售预测需要冲销预测的，被冲销的预测剩余数量必须大于等于本次预测数量
	 * 
	 * @author zhongyl
	 **/
	public void SaleForecast_commit_checkcanclashqty(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select A.sd_detno,A.sd_qty,A.sd_forecastcode,A.sd_forecastdetno,B.sd_qty,B.sd_clashsaleqty from SaleForecastDetail A left join SaleForecast on A.sd_forecastcode=sf_code left join  SaleForecastDetail B on sf_id=B.sd_sfid and A.sd_forecastdetno=B.sd_detno where A.sd_sfid="
						+ id + " and A.sd_forecastcode<>' ' and A.sd_forecastdetno>0 and A.sd_qty>NVL(B.sd_qty,0)");
		if (sl.next()) {
			BaseUtil.showError("序号:" + sl.getString("sd_detno") + "本单数量大于该预测未冲销数量");
		}
	}

	/**
	 * 采购验退单：税率强制取供应商资料里面的税率(善领) scm-prodinout->commit->before
	 * 
	 * @author madan 2014-7-8 10:01:01
	 */
	public void prodinout_commit_before_taxrate(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pi_currency) from vendor left join Prodinout on ve_code=pi_cardcode where ve_currency <> pi_currency and nvl(pi_currency,' ')<>' ' and nvl(ve_currency,' ')<>' ' and pi_id="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("单据的币别与供应商资料里面币别不相同，不允许提交!");
		} else {
			baseDao.execute(
					"update ProdIODetail set pd_taxrate=(select ve_taxrate from vendor where ve_code=(select pi_cardcode from prodinout where pi_id=?) and nvl(ve_taxrate,0) <> 0) where pd_piid=? and nvl(pd_taxrate,0)=0",
					id, id);
		}
	}

	/**
	 * 出入库单：明细行仓库不能为空 scm-prodinout->commit->before
	 * 
	 * @author madan 2014-7-8 14:37:32
	 */
	public void prodinout_commit_before_warehouse(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pd_pdno) from ProdIODetail where nvl(pd_whcode,' ')=' ' and nvl(pd_prodcode,' ')<>' ' and pd_piid=" + id,
				String.class);
		if (dets != null) {
			BaseUtil.showError("仓库编号为空，不允许提交!行号：" + dets);
		}
	}

	/**
	 * 采购单：提交前判断模具是否成套 scm-purchase->commit->before
	 * 
	 * @author zhongyl
	 */
	public void purchase_commit_productset(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select ps_code,count(1)sn from (select distinct psd_psid,pqty/psd_qty as setqty  from productsetdetail left join (select pd_prodcode,sum(pd_qty) pqty from purchasedetail "
						+ " where pd_puid="
						+ id
						+ " group by pd_prodcode)A on productsetdetail.psd_prodcode=A.pd_prodcode "
						+ " where psd_psid in (select psd_psid from productsetdetail,purchasedetail where pd_puid="
						+ id
						+ " and psd_prodcode=pd_prodcode)) B left join productset on psd_psid=ps_id group by ps_code order by sn desc  ");
		if (sl.next()) {
			if (sl.getInt("sn") > 1) {
				BaseUtil.showError("采购模具不配套，模具编号[" + sl.getString("ps_code") + "]");
			}
		}
		sl = baseDao
				.queryForRowSet("select wm_concat(psd_prodcode) prcode,count(1) sn from productsetdetail left join (select pd_prodcode,sum(pd_qty) pqty from purchasedetail"
						+ " where pd_puid="
						+ id
						+ " group by pd_prodcode)A on productsetdetail.psd_prodcode=A.pd_prodcode "
						+ " where psd_prodcode in (select pd_prodcode from purchasedetail where pd_puid="
						+ id
						+ ")"
						+ " and (pqty/psd_qty)<>ceil(pqty/psd_qty) ");
		if (sl.next()) {
			if (sl.getInt("sn") > 0) {
				BaseUtil.showError("采购模具套数不是整数，料号:" + sl.getString("prcode"));
			}
		}

	}

	/**
	 * 不良品入库单、不良品出库单：提交过账之前，判断明细行仓库必选为IQC判退仓(国扬) scm-prodinout->commit->before
	 * 
	 * @author madan 2014-7-14 11:39:48
	 */
	public void prodinout_commit_before_IQCwarehouse(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pd_pdno) from ProdIODetail where nvl(pd_whcode,' ')<>'IQC判退仓' and nvl(pd_prodcode,' ')<>' ' and pd_piid="
						+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("仓库不是IQC判退仓，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 销售订单：保存时如果销售订单明细行的客户料号为空，根据客户+ERP料号2个条件到PRODUCTCUSTOMER表抓取客户料号、客户型号到明细行里
	 * scm-sale->save->after
	 * 
	 * @author madan 2014-7-28 11:07:39
	 */
	public void sale_save_after_prodcust(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cucode = store.get("sa_custcode");
		Object said = store.get("sa_id");
		baseDao.execute(
				"update saledetail set (sd_prodcustcode,sd_custprodcode,sd_custproddetail)=(select  pc_custprodcode , pc_custprodspec , pc_custproddetail  "
						+ "from (select * from (select cu_code,pr_code,pc_custprodcode,pc_custproddetail,PC_CUSTPRODSPEC,ROW_NUMBER() over( partition by PC_CUSTID,PC_PRODCODE order by pc_custprodcode ) rn "
						+ "from ProductCustomer left join customer on pc_custid=cu_id left join product on pr_id=pc_prodid) where  rn=1) where cu_code=? and sd_prodcode=pr_code)"
						+ " where nvl(sd_custprodcode,' ')=' ' and sd_said=?", cucode, said);
	}

	/**
	 * 销售订单：保存时如果销售订单明细行的客户料号为空，根据客户+ERP料号2个条件到PRODUCTCUSTOMER表抓取客户料号、客户型号到明细行里
	 * scm-sale->save->after
	 * 
	 * @author madan 2014-7-28 11:07:39
	 */
	public void sale_save_after_prodcust2(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cucode = store.get("sa_custcode");
		Object said = store.get("sa_id");
		baseDao.execute(
				"update saledetail set (sd_prodcustcode,sd_custprodcode,sd_custproddetail)=(select  PC_CUSTPRODSPEC , PC_CUSTPRODCODE , pc_custproddetail  "
						+ "				from (select * from (select cu_code,pr_code,pc_custprodcode,pc_custproddetail,PC_CUSTPRODSPEC,ROW_NUMBER() over( partition by PC_CUSTID,PC_PRODCODE order by pc_custprodcode ) rn "
						+ "from ProductCustomer left join customer on pc_custid=cu_id left join product on pr_id=pc_prodid) where  rn=1) where cu_code=? and sd_prodcode=pr_code"
						+ ") where nvl(sd_custprodcode,' ')=' ' and sd_said=?", cucode, said);
	}

	/**
	 * 销售预测单：保存时如果预测单明细行的客户料号为空，根据客户+ERP料号2个条件到PRODUCTCUSTOMER表抓取客户料号、客户型号到明细行里
	 * scm-sale->save->after
	 * 
	 * @author madan 2014-7-28 11:07:39
	 */
	public void saleforecast_save_after_prodcust(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cucode = store.get("sf_custcode");
		Object sfid = store.get("sf_id");
		baseDao.execute(
				"update saleforecastdetail set (sd_custprodcode,sd_custtype)=(select max(pc_custprodcode),max(pc_custprodspec) from ProductCustomer left join Product on pc_prodid=pr_id left join customer on pc_custid=cu_id where cu_code=? and sd_prodcode=pr_code) where nvl(sd_custprodcode,' ')=' ' and sd_sfid=?",
				cucode, sfid);
	}

	/**
	 * 销售订单：提交之前，判断同客户同料号在预测单里预测数量>0 scm-sale->commit->before
	 * 
	 * @author madan 2014-7-28 19:08:23
	 */
	public void sale_commit_before_saleforecastqty(Integer id) {
		StringBuffer sb = new StringBuffer();
		Double err = null;
		SqlRowList rs = baseDao
				.queryForRowSet("select sd_detno,sd_prodcode,sa_custcode from SaleDetail,Sale WHERE sd_said=sa_id AND sa_id = " + id
						+ " and nvl(sd_statuscode,' ')<>'FINISH'");
		while (rs.next()) {
			if (rs.getObject("sd_prodcode") != null) {
				err = baseDao.getJdbcTemplate().queryForObject(
						"select nvl(sum(sd_qty),0) qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id where sd_prodcode=? and sf_custcode=?"
								+ "and nvl(sf_statuscode,' ')<>'FINISH'", Double.class, rs.getObject("sd_prodcode"),
						rs.getObject("sa_custcode"));
				if (err > 0) {
					sb.append("行:").append(rs.getObject("sd_detno"));
					sb.append("预测数量:").append(err).append("<br>");
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("同客户同料号在预测单里预测数量大于0!<br>" + sb.toString());
	}

	/**
	 * reserve->prodinout->出入库单限制主表仓库和明细必须一致，可在提交、打印、审核、过账等操作前配置
	 */
	public void ProdInOut_MustSameWhcode(Integer pi_id) {
		SqlRowList sl = baseDao.queryForRowSet("select distinct pd_whcode from prodiodetail where pd_piid=" + pi_id + " and rownum>1");
		if (sl.next()) {
			BaseUtil.showError("此单据限制明细行仓库必须一致");
		}
		sl = baseDao.queryForRowSet("select  pd_pdno from prodiodetail,prodinout where pd_piid=" + pi_id
				+ " and pd_piid=pi_id and pd_whcode<>pi_whcode and pd_whcode<>' '");
		if (sl.next()) {
			BaseUtil.showError("此单据限制明细仓库必须与主表仓库一致，不一致行号：" + sl.getInt("pd_pdno"));
		}
	}

	/**
	 * reserve->prodinout->malata:出入库单限制界面的所属公司和仓库的所属公司必须一致，可在提交、打印、审核、过账等操作前配置
	 */
	public void ProdInOut_MuseSameCop(Integer pi_id) {
		SqlRowList sl = baseDao.queryForRowSet("select pd_pdno from prodiodetail,prodinout,warehouse  where pd_piid=" + pi_id
				+ " and pd_piid=pi_id and pd_whcode=wh_code and wh_cop<>pi_cop  ");
		if (sl.next()) {
			BaseUtil.showError("此单据限制仓库所属公司必须与界面所属公司一致，不一致行号：" + sl.getInt("pd_pdno"));
		}
		sl = baseDao.queryForRowSet("select  pi_cop from prodinout,warehouse where pi_id=" + pi_id
				+ " and pi_whcode=wh_code and wh_cop<>pi_cop  ");
		if (sl.next()) {
			BaseUtil.showError("此单据限制仓库所属公司必须与界面所属公司一致");
		}
	}

	/**
	 * reserve->prodinout->malata:出入库单限制仓库的所属公司和销售订单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
	 */
	public void ProdInOut_CheckSaleCop(Integer pi_id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select pd_pdno from prodiodetail left join sale on pd_ordercode=sa_code left join prodinout on pd_piid=pi_id left join warehouse on wh_code=NVL(pd_whcode,pi_Whcode)  where pd_piid="
						+ pi_id + " and sa_code<>' ' and sa_cop<>wh_cop ");
		if (sl.next()) {
			BaseUtil.showError("此单据限制仓库所属公司必须与销售订单所属公司一致，不一致行号：" + sl.getInt("pd_pdno"));
		}
	}

	/**
	 * reserve->prodinout->malata:出入库单限制仓库的所属公司和采购订单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
	 */
	public void ProdInOut_CheckPurchaseCop(Integer pi_id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select pd_pdno from prodiodetail left join purchase on pd_ordercode=pu_code left join prodinout on pd_piid=pi_id left join warehouse on wh_code=NVL(pd_whcode,pi_Whcode)  where pd_piid="
						+ pi_id + " and pu_code<>' ' and pu_cop<>wh_cop ");
		if (sl.next()) {
			BaseUtil.showError("此单据限制仓库所属公司必须与采购订单所属公司一致，不一致行号：" + sl.getInt("pd_pdno"));
		}
	}

	/**
	 * reserve->prodinout->malata:出入库单限制仓库的所属公司和制造单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
	 */
	public void ProdInOut_CheckMakeCop(Integer pi_id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select pd_pdno from prodiodetail left join make on pd_ordercode=ma_code left join prodinout on pd_piid=pi_id left join warehouse on wh_code=NVL(pd_whcode,pi_Whcode)  where pd_piid="
						+ pi_id + " and ma_code<>' ' and ma_cop<>wh_cop ");
		if (sl.next()) {
			BaseUtil.showError("此单据限制仓库所属公司必须与制造单所属公司一致，不一致行号：" + sl.getInt("pd_pdno"));
		}
	}

	/**
	 * 信用额度:保存之前，判断同一客户不能存在多个信用额度 scm-sale->customercredit->save->before
	 * 
	 * @author madan 2014-8-1 14:32:38
	 */
	public void customercredit_save_before_customercheck(HashMap<Object, Object> store) {
		Object cucode = store.get("cuc_custcode");
		boolean hasNext = baseDao.checkIf("CustomerCredit", "cuc_custcode='" + cucode + "'");
		if (hasNext) {
			BaseUtil.showError("客户编号为[" + cucode + "]已经存在信用额度申清单!");
		}
	}

	/**
	 * 信用额度：审核之后，更新客户资料中的信用额度 scm-sale->customercredit->audit->after
	 * 
	 * @author madan 2014-8-1 14:22:05
	 */
	public void customercredit_audit_after_customer(Integer cuc_id) {
		SqlRowList sl = baseDao.queryForRowSet("select cuc_credit, cuc_custcode from CustomerCredit  where cuc_id=?", cuc_id);
		if (sl.next()) {
			baseDao.execute("update customer set cu_credit=? where cu_code=?", sl.getGeneralDouble("cuc_credit"),
					sl.getObject("cuc_custcode"));
		}
	}

	/**
	 * 供应商信用额度:保存之前，判断同一供应商不能存在多个信用额度
	 * 
	 * @author madan 2015-04-13 18:09:58
	 */
	public void vendorcredit_save_before_customercheck(HashMap<Object, Object> store) {
		Object vecode = store.get("vec_vendcode");
		boolean hasNext = baseDao.checkIf("VendorCredit", "vec_vendcode='" + vecode + "'");
		if (hasNext) {
			BaseUtil.showError("供应商编号为[" + vecode + "]已经存在信用额度申清单!");
		}
	}

	/**
	 * 供应商信用额度：审核之后，更新供应商资料中的信用额度
	 * 
	 * @author madan 2014-8-1 14:22:05
	 */
	public void vendorcredit_audit_after_customer(Integer id) {
		SqlRowList sl = baseDao.queryForRowSet("select nvl(vec_credit,0), vec_vendcode from VendorCredit  where vec_id=?", id);
		if (sl.next()) {
			baseDao.execute("update vendor set ve_credit=? where ve_code=?", sl.getObject(1), sl.getObject("vec_vendcode"));
		}
	}

	/**
	 * 客户解挂申请：更新之前计算 剩余额度
	 * 
	 **/
	public void CustomerRelive_update_after_remaincredit(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cucode = null;
		Object id = null;

		List<String> allMasters = new ArrayList<String>();
		Master master = SystemSession.getUser().getCurrentMaster();
		Master parentMaster = null;
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			parentMaster = master;
		} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
				&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
			allMasters.addAll(Arrays.asList(parentMaster.getMa_soncode().split(",")));
			for (Map<Object, Object> gridmap : grid) {
				cucode = gridmap.get("crd_custcode");
				id = gridmap.get("crd_id");
				if (cucode != null) {
					Object[] objs = baseDao.getFieldsDataByCondition(parentMaster.getMa_user() + ".CustomerCredit",
							"cuc_credit,cuc_tempcredit,cuc_tempcreditdate", "cuc_statuscode='AUDITED' and cuc_custcode='" + cucode + "'");
					double A = 0.0;
					if (objs != null) {
						if (objs[2] != null
								&& (DateUtil.compare(objs[2].toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
										objs[2].toString(), DateUtil.getCurrentDate()) == 0)) {
							A = Double.parseDouble(objs[0].toString()) + Double.parseDouble(objs[1].toString());
						} else {
							A = Double.parseDouble(objs[0].toString());
						}
					}
					if (A > 0) {
						Double B1 = 0.0;
						Double C1 = 0.0;
						Double D1 = 0.0;
						for (String son : allMasters) {
							// 取出这个客户的应收-预收的余额折算为本位币
							B1 = B1
									+ baseDao.getJdbcTemplate().queryForObject(
											"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from "
													+ son + ".custar," + son + ".currencys where ca_currency=cr_name and ca_custcode=?",
											new Object[] { cucode }, Double.class);
							// 未过账发货单的金额(变量C)
							C1 = C1
									+ baseDao
											.getJdbcTemplate()
											.queryForObject(
													"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from "
															+ son
															+ ".prodinout,"
															+ son
															+ ".prodiodetail,"
															+ son
															+ ".currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
													new Object[] { cucode }, Double.class);
							// 已提交未结案的通知单未转出货单部分(变量D)
							D1 = D1
									+ baseDao
											.getJdbcTemplate()
											.queryForObject(
													"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from "
															+ son
															+ ".sendnotify,"
															+ son
															+ ".sendnotifydetail,"
															+ son
															+ ".currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('COMMITED','AUDITED') and SN_SENDSTATUSCODE='PARTOUT'",
													new Object[] { cucode }, Double.class);
						}
						Double F = A - (B1 + C1 + D1);
						baseDao.execute("update CUSTOMERRELIVEDETAIL set crd_remaincredit=" + F + " where crd_id=" + id);
					}
				}
			}
		} else {
			for (Map<Object, Object> gridmap : grid) {
				cucode = gridmap.get("crd_custcode");
				id = gridmap.get("crd_id");
				if (cucode != null) {
					Object[] objs = baseDao.getFieldsDataByCondition("CustomerCredit", "cuc_credit,cuc_tempcredit,cuc_tempcreditdate",
							"cuc_statuscode='AUDITED' and cuc_custcode='" + cucode + "'");
					double A = 0.0;
					if (objs != null) {
						if (objs[2] != null
								&& (DateUtil.compare(objs[2].toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
										objs[2].toString(), DateUtil.getCurrentDate()) == 0)) {
							A = Double.parseDouble(objs[0].toString()) + Double.parseDouble(objs[1].toString());
						} else {
							A = Double.parseDouble(objs[0].toString());
						}
					}
					if (A > 0) {
						// 取出这个客户的应收-预收的余额折算为本位币
						Double B1 = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from custar,currencys where ca_currency=cr_name and ca_custcode=?",
										new Object[] { cucode }, Double.class);
						// 未过账发货单的金额(变量C)
						Double C1 = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from prodinout,prodiodetail,currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
										new Object[] { cucode }, Double.class);
						// 已提交未结案的通知单未转出货单部分(变量D)
						Double D1 = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from sendnotify,sendnotifydetail,currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('COMMITED','AUDITED') and SN_SENDSTATUSCODE='PARTOUT'",
										new Object[] { cucode }, Double.class);

						Double F = A - (B1 + C1 + D1);
						baseDao.execute("update CUSTOMERRELIVEDETAIL set  crd_remaincredit=" + F + " where crd_id=" + id);
					}
				}
			}
		}
	}

	/**
	 * 客户信额度用申请：提交之前计算剩余信用额度
	 * 
	 * @author madan 2017-05-22 14:30:27
	 **/
	public void CustomerCredit_commit_before_checkcreditys(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select cuc_custcode from CustomerCredit where cuc_id=" + id);
		if (rs.next()) {
			List<String> allMasters = new ArrayList<String>();
			Master master = SystemSession.getUser().getCurrentMaster();
			Master parentMaster = null;
			if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
				parentMaster = master;
			} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
					&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
				allMasters.addAll(Arrays.asList(parentMaster.getMa_soncode().split(",")));
				Object[] objs = baseDao.getFieldsDataByCondition(parentMaster.getMa_user() + ".customercredit",
						"cuc_credit,cuc_tempcredit,cuc_tempcreditdate", " cuc_custcode='" + rs.getObject("cuc_custcode") + "'");
				double A = 0.0;
				if (objs != null) {
					if (objs[2] != null
							&& (DateUtil.compare(objs[2].toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
									objs[2].toString(), DateUtil.getCurrentDate()) == 0)) {
						A = Double.parseDouble(objs[0].toString()) + Double.parseDouble(objs[1].toString());
					} else {
						A = Double.parseDouble(objs[0].toString());
					}
				}
				if (A > 0) {
					Double B = 0.0;// 取出这个客户的应收-预收的余额折算为本位币
					Double C = 0.0;// 未过账发货单的金额(变量C)
					Double D = 0.0;// 已提交未结案的通知单未转出货单部分(变量D)
					for (String son : allMasters) {
						B = B
								+ baseDao.getJdbcTemplate().queryForObject(
										"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from "
												+ son + ".custar," + son + ".currencys where ca_currency=cr_name and ca_custcode=?",
										new Object[] { rs.getObject("cuc_custcode") }, Double.class);
						C = C
								+ baseDao
										.getJdbcTemplate()
										.queryForObject(
												"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from "
														+ son
														+ ".prodinout,"
														+ son
														+ ".prodiodetail,"
														+ son
														+ ".currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
												new Object[] { rs.getObject("cuc_custcode") }, Double.class);
						D = D
								+ baseDao
										.getJdbcTemplate()
										.queryForObject(
												"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from "
														+ son
														+ ".sendnotify,"
														+ son
														+ ".sendnotifydetail,"
														+ son
														+ ".currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('COMMITED','AUDITED') and SN_SENDSTATUSCODE='PARTOUT'",
												new Object[] { rs.getObject("cuc_custcode") }, Double.class);
					}
					baseDao.execute("update " + parentMaster.getMa_user() + ".CustomerCredit set cuc_remaincredit=" + (A - B - C - D)
							+ "where cuc_custcode='" + rs.getObject("cuc_custcode") + "'");
				}
			} else {
				Object[] objs = baseDao.getFieldsDataByCondition("customercredit", "cuc_credit,cuc_tempcredit,cuc_tempcreditdate",
						" cuc_custcode='" + rs.getObject("cuc_custcode") + "'");
				double A = 0.0;
				if (objs != null) {
					if (objs[2] != null
							&& (DateUtil.compare(objs[2].toString(), DateUtil.getCurrentDate()) == 1 || DateUtil.compare(
									objs[2].toString(), DateUtil.getCurrentDate()) == 0)) {
						A = Double.parseDouble(objs[0].toString()) + Double.parseDouble(objs[1].toString());
					} else {
						A = Double.parseDouble(objs[0].toString());
					}
				}
				if (A > 0) {
					// 取出这个客户的应收-预收的余额折算为本位币
					Double B = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from custar,currencys where ca_currency=cr_name and ca_custcode=?",
									new Object[] { rs.getObject("cuc_custcode") }, Double.class);
					// 未过账发货单的金额(变量C)
					Double C = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from prodinout,prodiodetail,currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
									new Object[] { rs.getObject("cuc_custcode") }, Double.class);
					// 已提交未结案的通知单未转出货单部分(变量D)
					Double D = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from sendnotify,sendnotifydetail,currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('COMMITED','AUDITED') and SN_SENDSTATUSCODE='PARTOUT'",
									new Object[] { rs.getObject("cuc_custcode") }, Double.class);
					baseDao.execute("update CustomerCredit set cuc_remaincredit=" + (A - B - C - D) + "where cuc_custcode='"
							+ rs.getObject("cuc_custcode") + "'");
				}
			}
		}
	}

	/**
	 * 恒晨审批过程更新操作 限制不能更新已存在的客户编号
	 * */
	public void customer_save_before(HashMap<Object, Object> store) {
		boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + store.get("cu_code") + "' and cu_id<>" + store.get("cu_id"));
		if (!bool) {
			BaseUtil.showError("当前客户编号已存在!");
		} else {
			// 判断应收客户和收货客户是否与客户资料原客户编号一致
			SqlRowList sl = baseDao
					.queryForRowSet("select (case when cu_shcustcode=cu_code then 1 else 0 end)  ,(case when cu_arcode=cu_code then 1 else 0 end)  from Customer where cu_id="
							+ store.get("cu_id"));
			String updateStr = "";
			if (sl.next()) {
				String cu_name = store.get("cu_name") != null ? "'" + store.get("cu_name") + "'" : "cu_name";
				if (sl.getInt(1) == 1) {
					updateStr += "cu_shcustname=" + cu_name + ",cu_shcustcode='" + store.get("cu_code") + "'";
				}
				if (sl.getInt(2) == 1) {
					updateStr += "".equals(updateStr) ? "cu_arname=" + cu_name + ",cu_arcode='" + store.get("cu_code") + "'"
							: ",cu_arname=" + cu_name + ",cu_arcode='" + store.get("cu_code") + "'";
				}
			}
			if (!"".equals(updateStr)) {
				baseDao.execute("update customer set " + updateStr + " where cu_id=" + store.get("cu_id"));
			}
		}
	}

	/**
	 * reserve->prodInOut->return->commit->before 生产退料单：提交之前，明细行对应有数量时，退料原因必须要填写
	 * 
	 * @author madan 2014-8-2 10:24:45
	 */
	public void prodInOut_return_commit_before_TLYY(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pd_pdno) from ProdIODetail where nvl(pd_description,' ')=' ' and nvl(pd_inqty,0) > 0 and pd_piid=?",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("退料数量大于0，退料原因必须要填写!行号：" + dets);
		}
	}

	/**
	 * 销售退货单：保存时如果明细行的客户料号为空，根据客户+ERP料号2个条件到PRODUCTCUSTOMER表抓取客户料号、客户型号到明细行里(帝显)
	 * scm-prodInOut->save->after
	 * 
	 * @author madan 2014-8-5 12:46:45
	 */
	public void prodInOut_save_after_prodcust(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cucode = store.get("pi_cardcode");
		Object piid = store.get("pi_id");
		baseDao.execute(
				"update prodiodetail set (pd_barcode,pd_custprodcode)=(select max(pc_custprodcode),max(pc_custprodspec) from ProductCustomer left join Product on pc_prodid=pr_id left join customer on pc_custid=cu_id where cu_code=? and pd_prodcode=pr_code) where nvl(pd_custprodcode,' ')=' ' and pd_piid=?",
				cucode, piid);
	}

	/**
	 * 委外验收单、完工入库单：如果工单保税只能入库到保税仓库
	 * */
	public void prodInOut_checkbonded(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select count(1) num,wm_concat(pd_pdno) pdno from ProdIODetail left join prodinout on pi_id=pd_piid left join warehouse on wh_code=NVL(pd_whcode,pi_whcode) left join make on ma_code=pd_ordercode where pi_id="
						+ id + " and ma_bonded<>0 and NVL(wh_bonded,0)=0");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("保税工单必须入保税仓库!行号：" + sl.getString("pdno"));
			}
		}
	}

	/**
	 * 请购单：如果明细行的数量不是最小包装量的倍数，不允许提交(凯瑞德) scm-application->commit->before
	 * 
	 * @author madan 2014-8-9 10:43:01
	 */
	public void application_commit_before_minpack(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ad_detno) from applicationdetail left join Application on ad_apid=ap_id where ap_id=? and nvl(ad_minpack,0)<>0 and nvl(ad_qty,0)<>0 and mod(nvl(ad_qty,0),nvl(ad_minpack,0))>0",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("请购数量不是最小包装量的倍数，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 采购单审核完后发送邮件给供应商(恒晨)
	 * */
	public void purchase_audit_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("Purchase left join Vendor on pu_vendcode=ve_code", "ve_email", "pu_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String encop = baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1").toString();
		Object[] objs = baseDao.getFieldsDataByCondition("purchase", new String[] { "pu_code", "pu_vendname" }, "pu_id=" + id);
		String title = "请查看采购订单，订单编号：" + objs[0];
		String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
				+ objs[1]
				+ "，您好！：<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
				+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您有一张来自于<SPAN style='COLOR: blue'>公司名称（<SPAN lang=EN-US>"
				+ encop
				+ "</SPAN>）</SPAN>的新订单<SPAN lang=EN-US>(</SPAN>订单编号：<SPAN lang=EN-US style='COLOR: blue'>"
				+ objs[0]
				+ ")</SPAN>"
				+ "<SPAN lang=EN-US>,</SPAN>及时登入优软商务平台查取您的订单<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.usoftchina.com/'><FONT color=#0000ff>www.usoftchina.com</FONT></A>"
				+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>如在使用平台过程中，遇到任何操作问题，请及时与深圳市优软科技有限公司客服人员（连小姐）联系，联系电话：<SPAN lang=EN-US>0755-26996828<o:p></o:p></SPAN></SPAN></P>"
				+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
		sendMailService.sendSysMail(title, contextdetail, email.toString());
	}

	/**
	 * 采购变更单审核完后发送邮件给'原'供应商(恒晨)
	 * */
	public void purchaseChange_audit_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("PurchaseChange left join vendor on pc_vendcode=ve_code", "ve_email", "pc_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String title = "请查看采购变更单，变更单号：" + baseDao.getFieldDataByCondition("PurchaseChange", "pc_code", "pc_id=" + id);
		sendMailService.sendSysMail(title, title, email.toString());
	}

	/**
	 * 物料核价单：保存更新之前，类型为采购时，价格不允许为0(易方)
	 * 
	 * @author madan 2014-9-1 17:53:50
	 */
	public void purchaseprice_save_before_pricecheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object pp_id = store.get("pp_id");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where pp_id=? and pp_kind='采购' and nvl(ppd_price,0)=0 ",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("类型为采购时，单价不允许为0!行号：" + dets);
		}
	}

	/*
	 * (万里达科技)试产订单变更单明细行的新数量不大于原数量。
	 */
	public void salechange_commit_compare(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select nvl(scd_qty,0),nvl(scd_newqty,0),scd_sacode from SaleChangeDetail where scd_scid=?",
				id);
		while (rs.next()) {
			if (rs.getInt(1) < rs.getInt(2)) {
				BaseUtil.showError("订单号为:" + rs.getString(3) + "的明细行新数量大于原数量!");
			}
		}

	}

	/**
	 * 销售变更单：保存之前，如果明细行订单已同步到香港帐套，不允许进行料号变更(易方) scm-salechange->save->bafore
	 * 
	 * @author madan 2014-9-4 16:57:18
	 */
	public void salechange_save_bafore_sysnc(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		StringBuffer sb = new StringBuffer();
		String err = null;
		for (Map<Object, Object> gridmap : grid) {
			if (gridmap.get("scd_sacode") != null) {
				err = baseDao.getJdbcTemplate().queryForObject("select sa_code from sale where sa_code=? and nvl(sa_sync,' ')<>' '",
						String.class, gridmap.get("scd_sacode"));
				if (err != null) {
					sb.append("行:").append(gridmap.get("scd_detno")).append("<br>");
					sb.append("订单:").append(err).append("<br>");
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("销售变更单中订单已同步到香港帐套，不允许进行料号变更！<br>" + sb.toString());
	}

	/**
	 * 小于采购周期，不能提交
	 */
	public void preforecast_commit_checkdays(Integer id) {
		String sql = "select count(1) from PreSaleForecastDetail left join Product on sd_prodcode=pr_code left join presaleforecast on sd_sfid=sf_id where nvl(sd_qty,0)>0 and sd_enddate<sf_date+pr_leadtime+5 and sf_id='"
				+ id + "'";
		int count = baseDao.getCount(sql);

		if (count > 0) {
			Object[] o = baseDao.getFieldsDataByCondition(
					"PreSaleForecastDetail left join Product on sd_prodcode=pr_code left join presaleforecast on sd_sfid=sf_id",
					new String[] { "to_char(sd_startdate,'yyyy-mm-dd')", "to_char(sd_enddate,'yyyy-mm-dd')" },
					"nvl(sd_qty,0)>0 and sd_enddate<sf_date+pr_leadtime+5 and sf_id='" + id + "'");

			BaseUtil.showError("小于采购周期，不能提交!<br>开始日期:" + o[0] + "<br>结束日期:" + o[1]);
		}
	}

	/**
	 * 新物料认证（试产）单：审核之后更新审核人，审核日期（易方）
	 * 
	 * @author madan 2014-9-22 15:07:36
	 */
	public void productAttestation_audit_update(Integer id) {
		baseDao.execute("update ProductAttestation set pa_auditdate=sysdate,pa_auditman='" + SystemSession.getUser().getEm_name()
				+ "' where pa_id=" + id);
	}

	/**
	 * reserve->prodInOut->NoGoodOut->delete->befor 不良品出库单删除后，还原MRB单数据
	 * 
	 * @author madan 2014-9-23 17:41:18
	 */
	public void prodInOut_nogoodout_delete_qty(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_id from prodiodetail where pd_piid=?", id);
		while (rs.next()) {
			prodInOut_nogoodout_deletedetail(rs.getInt(1));
		}
	}

	/**
	 * reserve->prodInOut->NoGoodIn->deletedetail->before 不良品出库单明细删除后，还原MRB数据
	 * 
	 * @author madan 2014-9-23 17:41:32
	 */
	public void prodInOut_nogoodout_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_mrid", "pd_outqty", "pd_mrok" }, "pd_id=" + id
				+ " and nvl(pd_mrid, 0)<>0");
		if (objs != null) {
			int mdid = Integer.parseInt(objs[0].toString());
			Object[] mrid = baseDao.getFieldsDataByCondition("QUA_MRBDet", new String[] { "md_mrid", "md_ngqty" }, "md_id=" + mdid);
			Object[] status = baseDao
					.getFieldsDataByCondition("QUA_MRB", new String[] { "mr_statuscode", "mr_status" }, "mr_id=" + mrid[0]);
			baseDao.updateByCondition("QUA_MRBDet", "md_statuscode='" + status[0] + "',md_status='" + status[1] + "'", "md_id=" + mdid);
			if (objs[2] == null || Integer.parseInt(objs[2].toString()) == 0) {
				baseDao.updateByCondition("QUA_MRBDet", "md_isng=0", "md_id=" + mdid);
			} else {
				baseDao.updateByCondition("QUA_MRBDet", "md_isok=0", "md_id=" + mdid);
			}
			baseDao.updateByCondition("QUA_MRBDet", "md_statuscode='TURNIN',md_status='已入库'", "md_id=" + mdid
					+ " and (md_isng<>0 or md_isok<>0)");
		}
	}

	/**
	 * 发货单，销售退货单:提交之前判断主被动器件不能在同一张出货单、销售退货单!(宇声)
	 * reserve->prodInOut->commit_before madan 2014-9-25 17:06:32
	 */
	public void prodinout_commit_before_pr_jitype(Integer id) {
		int count = baseDao.getCount("SELECT count(distinct pr_jitype) from prodiodetail,product,prodinout where pd_piid=" + id
				+ " and pd_prodcode=pr_code and pd_piid=pi_id and nvl(pi_sellercode,' ') not like 'POPO%'");
		if (count > 1) {
			BaseUtil.showError("主被动器件不能在同一张出货单、销售退货单,不能提交!");
		}
	}

	/**
	 * 出入库单可配置(提交前、过账前)<br>
	 * 美金单据 没有填写报关价不能提交、过账
	 * 
	 * @param id
	 * @param language
	 * @param employee
	 * @author yingp
	 * @date 2014-10-9 18:04:28
	 */
	public void customprice_required(Integer id) {
		// 未填写报关价的行
		String nums = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id=? and pi_currency='USD' and nvl(pd_customprice,0)=0 order by pd_pdno",
						String.class, id);
		if (nums != null)
			BaseUtil.showError("美金单据，请填写报关价！行：" + nums);
	}

	/**
	 * 采购变更单：采购订单开始存在关联采购验收单后，不允许进行采购单价变更（仍可进行数量变更）
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void purchasechange_commitbefore_noprice(Integer id) {
		int count = 0;
		count = baseDao
				.getCount("select * from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where nvl(pcd_oldprice,0)<>nvl(pcd_newprice,0) and (pc_purccode,pcd_pddetno) in (select pd_ordercode, pd_orderdetno from prodiodetail where pd_piclass='采购验收单') and pcd_pcid="
						+ id);
		if (count > 0) {
			BaseUtil.showError("存在关联的采购验收单，不允许进行单价变更");
		}
	}

	/**
	 * scm->prodinout->post->before 生产领料单：过账之前判断，部门,领料人,仓管员没填写 (万利达)
	 * 
	 * @author madan 2014-10-22 15:29:38
	 */
	public void Picking_post_before_fieldcheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pi_inoutno) from ProdInOut where nvl(pi_departmentcode,' ')=' ' and pi_id=" + id, String.class);
		if (dets != null) {
			BaseUtil.showError("部门未填写!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pi_inoutno) from ProdInOut where nvl(pi_emname,' ')=' ' and pi_id=" + id, String.class);
		if (dets != null) {
			BaseUtil.showError("领料人未填写!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pi_inoutno) from ProdInOut where nvl(pi_cgycode,' ')=' ' and pi_id=" + id, String.class);
		if (dets != null) {
			BaseUtil.showError("仓管员未填写!");
		}
	}

	/**
	 * 客户申请单,客户资料维护：保存更新，判断客户名称在客户资料表中是否重复
	 * 
	 * @author madan 2014-10-27 10:26:29
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void customer_save_namecheck_customer(HashMap<Object, Object> store) {
		String code = baseDao.getFieldValue("Customer", "cu_code",
				"cu_code <> '" + store.get("cu_code") + "' AND cu_name='" + store.get("cu_name") + "'", String.class);
		if (code != null) {
			BaseUtil.showError("客户全称在客户资料表中已经存在，客户编号：" + code);
		}
	}

	/**
	 * 客户申请单,客户资料维护：保存更新，判断客户名称在客户资料申请表中是否重复
	 * 
	 * @author madan 2014-10-27 10:26:40
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void customer_save_namecheck_precustomer(HashMap<Object, Object> store) {
		String code = baseDao.getFieldValue("PreCustomer", "cu_code",
				"cu_code <> '" + store.get("cu_code") + "' AND cu_name='" + store.get("cu_name") + "'", String.class);
		if (code != null) {
			BaseUtil.showError("客户全称在客户申请资料表中已经存在，客户编号：" + code);
		}
	}

	/**
	 * 客户申请单,客户资料维护：保存更新，判断客户简称在客户资料表中是否重复
	 * 
	 * @author madan 2014-10-27 10:26:29
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void customer_save_shortnamecheck_customer(HashMap<Object, Object> store) {
		String code = baseDao.getFieldValue("Customer", "cu_code",
				"cu_code <> '" + store.get("cu_code") + "' AND CU_SHORTNAME='" + store.get("cu_shortname") + "'", String.class);
		if (code != null) {
			BaseUtil.showError("客户简称在客户资料表中已经存在，客户编号：" + code);
		}
	}

	/**
	 * 客户申请单,客户资料维护：保存更新，判断客户简称在客户资料申请表中是否重复
	 * 
	 * @author madan 2014-10-27 10:26:40
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void customer_save_shortnamecheck_precustomer(HashMap<Object, Object> store) {
		String code = baseDao.getFieldValue("PreCustomer", "cu_code", "cu_code <> '" + store.get("cu_code") + "' AND CU_SHORTNAME='"
				+ store.get("cu_shortname") + "'", String.class);
		if (code != null) {
			BaseUtil.showError("客户简称在客户申请资料表中已经存在，客户编号：" + code);
		}
	}

	/**
	 * 仓库资料维护：仓库库存大于0，不允许禁用
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void warehouse_bannedbefore_ohhandqty(Integer id) {
		Double sum = baseDao
				.getSummaryByField("ProductWh", "pw_onhand", "pw_whcode=(select wh_code from warehouse where wh_id=" + id + ")");
		if (sum > 0) {
			BaseUtil.showError("仓库库存大于0,不允许禁用!");
		}
	}

	/**
	 * 出入库单：单据部门编号+类型不存在于其它出入库科目设置中或不等于已审核，不允许进行当前操作 2015-1-19 11:08:17
	 * 
	 * @param id
	 * @author madan
	 */
	public void prodinout_post_before_catecheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pi_class||'号：'||pi_inoutno) from ProdINOUT where (pi_departmentcode, pi_class, pi_type) not in (select pc_departmentcode, pc_class, pc_type from ProdIOCateSet where nvl(pc_statuscode,' ')='AUDITED') and pi_id=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("单据部门编号+类型不存在于其它出入库科目设置中或不等于已审核，不允许进行当前操作!" + dets);
		}
	}

	/**
	 * 采购类型维护：已被使用不允删除
	 * 
	 * @param id
	 */
	public void purchasekind_deletebefore_usecheck(Integer id) {
		baseDao.delCheck("PurchaseKind", id);
	}

	/**
	 * 仓库资料维护：已被使用不允删除
	 * 
	 * @param id
	 */
	public void warehouse_deletebefore_usecheck(Integer id) {
		baseDao.delCheck("Warehouse", id);
	}

	/**
	 * 储位资料维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void location_delbefore_usecheck(Integer id) {
		baseDao.delCheck("ProductLocation", id);
	}

	/**
	 * 包装方式维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void packageStandard_delbefore_usecheck(Integer id) {
		baseDao.delCheck("PackageStandard", id);
	}

	/**
	 * 包装方式维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void packageStandard_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("PackageStandard", id);
	}

	/**
	 * 储位资料维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void location_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("ProductLocation", id);
	}

	/**
	 * 借货类型维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void borrowtype_delbefore_usecheck(Integer id) {
		baseDao.delCheck("BorrowCargoType", id);
	}

	/**
	 * 退货原因码维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void NGReason_delbefore_usecheck(Integer id) {
		baseDao.delCheck("QUA_NGReason", id);
	}

	/**
	 * 客户类型维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void customerkind_delbefore_usecheck(Integer id) {
		baseDao.delCheck("CustomerKind", id);
	}

	/**
	 * 销售价格条件维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void priceterm_delbefore_usecheck(Integer id) {
		baseDao.delCheck("PriceTerm", id);
	}

	/**
	 * 客户类型维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void customerkind_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("CustomerKind", id);
	}

	/**
	 * 预测类型维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void saleforecastkind_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("SaleForecastKind", id);
	}

	/**
	 * 销售类型维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void salekind_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("SaleKind", id);
	}

	/**
	 * 供应商类型维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void vendorkind_delbefore_usecheck(Integer id) {
		baseDao.delCheck("VendorKind", id);
	}

	/**
	 * 供应商类型维护：已被使用不允许反审核
	 * 
	 * @param id
	 */
	public void vendorkind_resAuditbefore_usecheck(Integer id) {
		baseDao.resAuditCheck("VendorKind", id);
	}

	/**
	 * 请购单：当需求日期小于当前日期+采购提前期，不允许更新
	 * 
	 * @author madan 2014-5-5 19:22:50
	 **/
	public void application_save_before_delivery(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		StringBuffer sb = new StringBuffer();
		String err = null;
		for (Map<Object, Object> gridmap : gstore) {
			if (gridmap.get("ad_prodcode") != null) {
				err = baseDao.getJdbcTemplate().queryForObject(
						"select ad_detno from ApplicationDetail where to_date('" + gridmap.get("ad_delivery")
								+ "','yyyy-mm-dd')<sysdate+ad_leadtime and ad_id=?", String.class, gridmap.get("ad_id"));
				if (err != null) {
					sb.append("行:").append(gridmap.get("ad_detno")).append("<br>");
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("需求日期小于当前日期+采购提前期，不允许进行当前操作！<br>" + sb.toString());
	}

	/**
	 * 请购单：当需求日期小于当前日期+采购提前期，不允许提交
	 * 
	 * @author madan 2014-5-5 19:22:50
	 **/
	public void application_commit_before_delivery(Integer ap_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from ApplicationDetail where ad_delivery<sysdate+ad_leadtime and ad_apid=?", String.class,
				ap_id);
		if (dets != null) {
			BaseUtil.showError("需求日期小于当前日期+采购提前期，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 请购单：当数量小于最小订购量，不允许提交
	 * 
	 * @author madan 2014-5-5 19:22:50
	 **/
	public void application_save_before_qtycheck(Integer ap_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from ApplicationDetail where nvl(ad_qty,0)<nvl(ad_minorder,0) and ad_apid=?", String.class,
				ap_id);
		if (dets != null) {
			BaseUtil.showError("单据数量小于最小订购量，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 采购变更单：新单价大于采购单原单价，不允许进行当前操作 purchasechange->commit->before
	 * 
	 * @author madan 2015-1-28 10:17:30
	 */
	public void purchasechange_before_newpricecheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pcd_detno) from PurchaseChangeDetail where nvl(pcd_newprice,0)>nvl(pcd_oldprice,0) and pcd_pcid=?",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("新单价大于采购单原单价，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 采购变更单：新数量大于采购单原数量，不允许进行当前操作 purchasechange->commit->before
	 * 
	 * @author madan 2015-1-28 11:40:28
	 */
	public void purchasechange_before_newqtycheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pcd_detno) from PurchaseChangeDetail where nvl(pcd_newqty,0)>nvl(pcd_oldqty,0) and pcd_pcid=?",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("新数量大于采购单原数量，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 销售变更单：新数量大于销售单原数量，不允许进行当前操作 salechange->commit->before
	 * 
	 * @author madan 2015-2-2 16:11:22
	 */
	public void salechange_before_newqtycheck(Integer id) {
		String detnos = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(scd_detno) from salechangedetail left join saledetail on scd_sacode=sd_code"
						+ " and sd_detno=scd_sddetno where scd_scid=? and nvl(scd_qty,0) <> nvl(sd_qty,0)", String.class, id);
		if (detnos != null) {
			BaseUtil.showError("原数量与订单中数量不一致，不允许执行当前操作!行号：" + detnos);
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(scd_detno) from salechangedetail where scd_scid=? and nvl(scd_newqty,0) > nvl(scd_qty,0)", String.class,
				id);
		if (dets != null) {
			BaseUtil.showError("新数量不能大于原数量，不允许提交!行号：" + dets);
		}
	}

	/**
	 * 采购验收单：明细行采购单所属公司更验收单所属公司不一致，不允许进行当前操作 verifyapply->commit->before
	 * 
	 * @author madan 2015-1-28 17:16:59
	 */
	public void prodinout_before_purccopcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Purchase on pd_ordercode=pu_code where nvl(pu_cop,' ')<>nvl(pi_cop,' ') and pd_piid=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单所属公司与验收单所属公司不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 其它出库单：明细行数量大于预测单数量，不允许进行当前操作 prodinout->commit->before
	 * 
	 * @author madan 2015-1-29 20:13:20
	 */
	public void prodinout_before_sfqtycheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join SaleForecastDetail on pd_plancode=sd_code and pd_sdid=sd_detno where nvl(pd_outqty,0)<>nvl(sd_qty,0) and pd_piid=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行数量大于预测单数量，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 拆件完工入库单：明细行制造单所属公司和主表所属公司不一致，不允许进行当前操作
	 * ProdInOut!PartitionStockIn->[commit,post]->before
	 * 
	 * @author XiaoST
	 */
	public void prodinout_post_before_makecheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join make on pd_ordercode=ma_code where nvl(ma_cop,' ')<>nvl(pi_cop,' ') and pd_piid=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行制造单所属公司与表头所属公司不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 生产退料单:单据明细行退料原因必须相同 ProdInOut!Make!Return->[commit,print]->before
	 * 
	 * @author XiaoST
	 * @param id
	 */
	public void prodinout_prsm_before_reasoncheck(Integer id) {
		String sql = "select count(0) from (select pd_description, count (pi_id)　from ProdIODetail  left join ProdInOut on pi_id=pd_piid 　where pi_id="
				+ id + "group by pd_description) tt";
		int count = baseDao.getCount(sql);
		if (count > 1) {
			BaseUtil.showError("明细行退料原因不一致");
		}
	}

	/**
	 * qua_mrb->deleteDetail-> MRB单：已转其它出库单，不允许删除明细行!
	 */
	public void MRB_deletede_turncheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(pd_inoutno) from ProdIODetail where nvl(pd_mrid,0)=?",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("已转其它出库单，不允许删除明细行!出库单号：" + dets);
		}
	}

	/*
	 * 限制出入库单多次打印
	 */
	public void ProdInout_print_count(Integer id) {
		Object printcount = baseDao.getFieldDataByCondition("ProdInout", "pi_count", "pi_id=" + id);
		// 当打印次数大于1,不允许打印
		if (Integer.parseInt(printcount.toString()) > 0) {
			BaseUtil.showError("该单据不允许多次打印!");
		}
	}

	/*
	 * 判断供应商是否维护UU号
	 */
	public void Purchase_commit_uu(Integer id) {
		Object vendcode = baseDao.getFieldDataByCondition("Purchase", "pu_vendcode", "pu_id=" + id);
		Object uu = baseDao.getFieldDataByCondition("Vendor", "ve_uu", "ve_code='" + vendcode + "'");
		if (uu == null || uu.equals("0") || uu.equals("") || uu.equals(" ")) {
			BaseUtil.showError("该供应商没有维护UU号!");
		}
	}

	/**
	 * 采购变更单：新单价不存在于价格库中，不允许提交 2015/6/29 增加限制，如果新单价不等于旧单价才去判断，如果相等则不考虑
	 * 
	 * @author madan 2015-04-13 16:18:41
	 */
	public void purchasechange_before_newpricecheck2(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pcd_detno) from PurchaseChangeDetail where pcd_pcid=? and nvl(pcd_newprice,0)<>nvl(pcd_oldprice,0) and nvl(pcd_newprice,0) not in (select nvl(ppd_price,0) from PurchasePriceDetail where (ppd_prodcode,ppd_currency,ppd_vendcode) in (select pcd_newprodcode,pc_newcurrency,pu_vendcode from purchasechangedetail,PurchaseChange,Purchase where pcd_pcid=pc_id and pc_purccode=pu_code and pcd_pcid=?) and ppd_statuscode='VALID')",
						String.class, id, id);
		if (dets != null) {
			BaseUtil.showError("明细行填写的新单价不存在于价格库中，不允许进行当前操作!行号：" + dets);
		}
	}

	/*
	 * 判断物料认定单物料加供应商是否存在于价格库中
	 */
	public void ProductApproval_audit_checkvendor(Integer id) {
		Object[] os = baseDao.getFieldsDataByCondition("productApproval", new String[] { "pa_prodcode", "pa_providecode" }, "pa_id=" + id);
		if (os != null) {
			int countnum = baseDao.getCount("select count(*) from purchasepricedetail where ppd_prodcode='" + os[0]
					+ "' and ppd_vendcode='" + os[1] + "'");
			if (countnum > 0) {

			} else {
				BaseUtil.showError("认定单物料编号与供应商资料在系统采购核价单里面不存在,不能审核！");
			}
		} else {
			BaseUtil.showError("认定单物料编与供应商必填！");
		}
	}

	/**
	 * 销售订单：销售定价为0，不允许提交
	 * 
	 * @author madan 2015-04-13 16:18:41
	 */
	public void sale_commitbefore_purcprice(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleDetail left join sale on sd_said=sa_id left join SaleKind on sa_kind=sk_name where nvl(sk_issaleprice,0)<>0 and nvl(sd_purcprice,0)=0 and sa_id=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("该订单类型不允许定价为0！行号：" + dets);
		}
	}

	/**
	 * 借货申请单：单价为0，不允许提交
	 * 
	 * @author madan 2015-05-19 08:43:48
	 */
	public void borrowapply_commitbefore_price(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(bad_detno) from BorrowApplyDetail where bad_baid=? and nvl(bad_price,0)=0", String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行单价为0，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 采购单：保存更新之后，报关价按照加价率重新计算
	 * 
	 * @author madan 2015-06-03 09:03:33
	 */
	public void purchase_saveafter_bgprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			if (pdid != null && Integer.parseInt(pdid.toString()) != 0) {
				baseDao.execute("update purchasedetail set pd_bgprice=round((1+pd_addrate/100)*pd_price,8) where pd_id=" + pdid);
			}
		}
	}

	/**
	 * 采购单：报关价按照加价率重新计算
	 * 
	 * @author madan 2015-08-12 15:16:18
	 */
	public void purchase_commitafter_bgprice(Integer id) {
		baseDao.execute("update purchasedetail set pd_bgprice=round((1+pd_addrate/100)*pd_price,8) where pd_puid=" + id);
	}

	/**
	 * 出入库单：明细行仓库与主表仓库不一致，不允许进行当前操作
	 * 
	 * @author madan 2015-06-25 16:14:12
	 */
	public void prodio_commitbefore_whcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail, ProdInOut where pd_piid=pi_id and pi_id=? and nvl(pi_whcode,' ')<> nvl(pd_whcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行仓库与主表仓库不一致，不允许进行当前操作！行号：" + dets);
		}
	}

	/**
	 * 拨出单：删除， 还原库存检验单数量
	 */
	public void ProdInOut_bochu_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_qbdid", "pd_outqty" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
				baseDao.updateByCondition("QUABATCHDETAIL", "qbd_yngqty=nvl(qbd_yngqty,0)-" + Double.valueOf(String.valueOf(obj[1])),
						"qbd_id=" + obj[0]);
			}
		}
	}

	/**
	 * 拨出单：删除明细， 还原库存检验单数量
	 */
	public void ProdInOut_bochu_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_qbdid", "pd_outqty" }, "pd_id=" + id);
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("QUABATCHDETAIL", "qbd_yngqty=nvl(qbd_yngqty,0)-" + Double.valueOf(String.valueOf(objs[1])),
					"qbd_id=" + objs[0]);
		}
	}

	/**
	 * 拨出单：更新， 还原库存检验单数量
	 */

	static final String CHECK_QBD_YQTY = "SELECT qba_code,qbd_detno,qbd_ngqty FROM QUABATCHDETAIL left join QUABATCH on qbd_qbaid=qba_id WHERE qbd_id=? and qbd_ngqty<?";

	public void ProdInOut_bochu_update(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;// 拨出单明细ID
		Object qty = null;
		Integer qbd_id = null;// 拨出单来源明细ID
		double tQty = 0;// 拨出单修改数量
		SqlRowList rs = null;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(s.get("pd_outqty").toString());
			if (s.get("pd_id") != null && Integer.parseInt(s.get("pd_id").toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_qbdid,0)", "nvl(pd_outqty,0)",
						"pd_ordercode" }, "pd_id=" + pdid + " and nvl(pd_qbdid,0)<>0 and pd_piclass='拨出单'");
				if (objs != null && objs[0] != null) {
					qbd_id = Integer.parseInt(String.valueOf(objs[0]));
					if (qbd_id != null && qbd_id > 0) {
						qty = baseDao.getFieldDataByCondition("ProdIoDetail", "sum(nvl(pd_outqty,0))", "pd_qbdid=" + qbd_id
								+ " AND pd_piclass='拨出单' and pd_id <>" + pdid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(CHECK_QBD_YQTY, qbd_id, Double.parseDouble(qty.toString()) + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],检验单号:").append(rs.getString("qba_code")).append(",行号:")
									.append(rs.getInt("qbd_detno")).append(",数量:").append(rs.getDouble("qbd_ngqty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						// 检验单
						baseDao.updateByCondition("QUABATCHDETAIL", "qbd_yngqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"qbd_id=" + qbd_id);
						baseDao.updateByCondition("QUABATCHDETAIL", "qbd_yngqty=0", "qbd_yngqty<=0 and qbd_id=" + qbd_id);
					}
				}
			}
		}
	}

	/**
	 * 条码维护明细行删除前，判断明细行是从PDA中采集还是ERP中生成的,PDA采集的不允许在ERP中删除
	 */
	public void barcode_deletedetail_before(Integer id) {
		Object obj = baseDao.getFieldDataByCondition("barcodeio", "nvl(bi_pdaget,0)", "bi_id=" + id);
		if (obj.toString().equals("1")) {
			BaseUtil.showError("该明细行是从PDA中采集，不允许再ERP中删除，请在PDA中删除!");
		}
		// 更新出入库单中明细行中的条码数量
		baseDao.execute("update ProdIODetail set pd_barcodeinqty=pd_barcodeinqty-(select nvl(bi_inqty,0) from barcodeio where bi_pdid=pd_id and bi_id="
				+ id + ")" + " where  pd_id=(select bi_pdid from barcodeio where bi_id=" + id + ")");
	}

	/**
	 * 用品领用单：明细删除前，还原用品申请单的数量
	 * reserve->prodInOut->oaapplication->deletedetail->before
	 */
	public void prodInOut_oaapplication_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_outqty,0)" }, "pd_id="
				+ id);
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("oaapplicationdetail", "od_turnlyqty=nvl(od_turnlyqty,0)-" + objs[1], "od_id=" + objs[0]);
			baseDao.updateByCondition("oaapplicationdetail", "od_turnlyqty=0", "nvl(od_turnlyqty,0)<=0 and od_id=" + objs[0]);
		}
	}

	/**
	 * 用品验收单：明细删除前，还原用品采购单的数量
	 * reserve->prodInOut->oapurchase->deletedetail->before
	 */
	public void prodInOut_oapurchase_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_inqty,0)" }, "pd_id="
				+ id);
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("oapurchasedetail", "od_yqty=nvl(od_yqty,0)-" + objs[1], "od_id=" + objs[0]);
			baseDao.updateByCondition("oapurchasedetail", "od_yqty=0", "nvl(od_yqty,0)<=0 and od_id=" + objs[0]);
		}
	}

	/**
	 * 借货归还单：提交过账前，判断主表的使用人必须与明细行来源借机出货单使用人一致
	 */
	public void prodInOut_OutReturn_commit_before(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail PD1 left join Prodinout P1 on PD1.pd_piid=P1.pi_id left join ProdInOut P2 on P2.pi_inoutno=PD1.pd_ordercode where "
								+ "PD1.pd_piid=? and nvl(P1.pi_sellercode,' ')<>nvl(P2.pi_sellercode,' ') and P1.pi_class = '借货归还单' and P2.pi_class = '借货出货单'"
								+ "and nvl(PD1.pd_ordercode,' ')<>' '", String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细来源单使用人与当前单使用人不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 采购变更单：明细行有已转数，不允许供应商变更
	 * 
	 * @author madan 2015-06-25 16:14:12
	 */
	public void purchaseChange_commitbefore_yqtycheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pcd_pddetno) from purchaseChangeDetail, purchaseChange, PurchaseDetail where pcd_pcid=pc_id and pc_purccode=pd_code and pcd_pddetno=pd_detno and pc_id=? and nvl(pc_apvendcode,' ')<> nvl(pc_newapvendcode,' ') and nvl(pd_yqty,0)>0",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("采购订单有已转数，不能变更供应商！行号：" + dets);
		}
	}

	/**
	 * MRB单：明细行只有一行的时候，按照主表数量更新明细行数量
	 * 
	 * @author madan 2015-10-27 09:09:32
	 */
	public void MRB_updateDetQty(HashMap<Object, Object> store, String language, Employee employee) {
		int count = baseDao.getCountByCondition("QUA_MRBDET", "md_mrid=" + store.get("mr_id"));
		if (count == 1) {
			baseDao.execute("update QUA_MRB set mr_checkqty=nvl(mr_ngqty,0) + nvl(mr_okqty,0) where mr_id=?", store.get("mr_id"));
			baseDao.execute("update QUA_MRBDET set (md_okqty,md_ngqty,md_checkqty)=(select mr_okqty,mr_ngqty,mr_checkqty from QUA_MRB where md_mrid=mr_id) where md_mrid="
					+ store.get("mr_id"));
		}
	}

	/**
	 * 销售订单：提交更新明细的供应商比例
	 * 
	 */
	public void sale_commit_updateVondorrate(Integer sa_id) {
		List<Object[]> detail = baseDao.getFieldsDatasByCondition("saledetail left join sale on sd_said=sa_id", new String[] { "sd_id",
				"sd_detno", "sd_prodcode", "sa_date" }, "sd_said=" + sa_id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] os : detail) {
			String sql = "update SaleDetail  set sd_vendorrate=nvl((select vrd_rate from "
					+ "(select vrd_rate from VendorRatedet  left join VendorRate on vrd_vrid=vr_id where " + "(to_date('" + os[3]
					+ "','yyyy-mm-dd HH24:mi:ss') between vr_startdate and vr_enddate) and vrd_prcode='" + os[2]
					+ "' and vr_statuscode='AUDITED'  order by vr_id desc) where rownum<=1),0) where  sd_id=" + os[0];
			sqls.add(sql);
		}
		baseDao.execute(sqls);
	}

	/**
	 * reserve->prodInOut->CheckIn->deletedetail->before 采购验收单：来源为采购收料单，不允许删除明细行
	 * 
	 * @author madan
	 */
	public void prodInOut_checkin_nodeletedetail(Integer id) {
		int count = baseDao.getCountByCondition("ProdInOut left join ProdIoDetail on pi_id=pd_piid",
				"PI_REFNO='采购收料单' and pi_class='采购验收单' and pd_id=" + id);
		if (count > 0) {
			BaseUtil.showError("该单来源为采购收料单，不允许删除明细行！");
		}
	}

	/**
	 * reserve->prodInOut->commit_checkreturnDate
	 * 借货出货单：提交前，判断明细归还日期大于当前日期的30天，不能进行当前操作
	 * 
	 * @throws ParseException
	 */
	public void prodInOut_commit_checkreturnDate(Integer id) throws ParseException {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from prodiodetail where pd_piid=?", id);
		while (rs.next()) {
			int i = DateUtil.countDates(DateUtil.getCurrentDate(), rs.getObject("pd_vendorreplydate").toString());
			if (i > 30) {
				BaseUtil.showError("该单据明细行第" + rs.getObject("pd_pdno") + "行的归还日期大于当前日期的30天！");
			}
		}
	}

	/**
	 * SendNotify->commit-before 出货通知单：提交更新明细的供应商比例(取自销售订单)
	 * 
	 */
	public void SendNotify_commit_updateVondorrate(Integer sn_id) {
		String sql = "update SendNotifydetail set snd_vendorrate=nvl((select sd_vendorrate from saledetail where sd_id=snd_sdid),0) where snd_snid="
				+ sn_id;
		baseDao.execute(sql);
	}

	/**
	 * 发货通知单:提交之前判断主被动器件不能在同一张发货通知单!(宇声) sale->sendnotify->commit_before madan
	 * 2014-9-25 20:35:12
	 */
	public void sendnotify_commit_before_pr_jitype(Integer id) {
		int count = baseDao.getCount("SELECT count(distinct pr_coefficient) from sendnotifydetail,product,sendnotify where snd_snid=" + id
				+ " and snd_prodcode=pr_code and snd_snid=sn_id and nvl(sn_sellercode,' ') not like 'POPO%'");
		if (count > 1) {
			BaseUtil.showError("不同提成系数的产品不能在同一张发货通知单,不能进行当前操作!");
		}
	}

	/**
	 * 发货单，销售退货单:提交之前判断RockChip、汉天下与其它品牌不能在同一张出货单、销售退货单(香港华商龙)
	 * reserve->prodInOut->commit_before madan 2015-10-29 14:06:45
	 */
	public void prodinout_commit_before_brandcheck(Integer id) {
		int count1 = baseDao.getCountByCondition("prodiodetail left join product on pd_prodcode=pr_code", "pd_piid=" + id
				+ " and (pr_brand='RockChip' or pr_brand='汉天下')");
		if (count1 > 0) {
			int count2 = baseDao
					.getCount("SELECT count(distinct pr_brand) from prodiodetail left join product on pd_prodcode=pr_code where pd_piid="
							+ id);
			if (count2 > 1) {
				BaseUtil.showError("RockChip、汉天下与其它品牌不能在同一张出货单、销售退货单！");
			}
		}
	}

	/**
	 * 发货通知单:提交之前判断RockChip、汉天下与其它品牌不能在同一张发货通知单(香港华商龙)
	 * sale->sendnotify->commit_before madan 2015-10-29 14:06:50
	 */
	public void sendnotify_commit_before_brandcheck(Integer id) {
		int count1 = baseDao.getCountByCondition("sendnotifydetail left join product on snd_prodcode=pr_code", "snd_snid=" + id
				+ " and (pr_brand='RockChip' or pr_brand='汉天下')");
		if (count1 > 0) {
			int count2 = baseDao
					.getCount("SELECT count(distinct pr_brand) from sendnotifydetail left join product on snd_prodcode=pr_code where snd_snid="
							+ id);
			if (count2 > 1) {
				BaseUtil.showError("RockChip、汉天下与其它品牌不能在同一张出货通知单！");
			}
		}
	}

	/**
	 * reserve->prodInOut->ProdCheckOut->delete->before 采购验退单/委外验退单：删除时还原验退申请单数量
	 * 
	 * @author mad
	 */
	public void prodInOut_ProdCheckOut_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_padid", "pd_outqty" }, "pd_piid=" + id
				+ " and nvl(pd_padid,0)<>0");
		if (objs != null) {
			for (Object[] obj : objs) {
				if (obj != null && obj[0] != null) {
					baseDao.updateByCondition("ProdIoApplyDetail", "pd_yqty=nvl(pd_yqty,0)-" + Double.parseDouble(String.valueOf(obj[1])),
							"pd_id=" + obj[0]);
					baseDao.updateByCondition("ProdIoApplyDetail", "pd_yqty=0", "nvl(pd_yqty,0)<=0 and pd_id=" + obj[0]);
				}
			}
		}
	}

	/**
	 * reserve->prodInOut->ProdCheckOut->deletedetail->before
	 * 采购验退单/委外验退单：删除明细时还原验退申请单明细数量
	 * 
	 * @author mad
	 */
	public void prodInOut_ProdCheckOut_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_padid", "pd_outqty" }, "pd_id=" + id
				+ " and nvl(pd_padid,0)<>0");
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("ProdIoApplyDetail", "pd_yqty=nvl(pd_yqty,0)-(" + objs[1] + ")", "pd_id=" + objs[0]);
			baseDao.updateByCondition("ProdIoApplyDetail", "pd_yqty=0", "nvl(pd_yqty,0)<=0 and pd_id=" + objs[0]);
		}
	}

	/**
	 * reserve->prodInOut->ProdCheckOut->save->before
	 * 采购验退单/委外验退单：明细数量修改前，更新来源验退申请单已转出数量
	 * 
	 * @author mad
	 */
	public void prodInOut_ProdCheckOut_savedetail(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pdid = null;
		Object qty = null;
		Object aq = 0;
		double tQty = 0;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			tQty = Double.parseDouble(String.valueOf(s.get("pd_outqty")));
			Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_padid", "pd_outqty" }, "pd_id=" + pdid
					+ " and nvl(pd_padid,0) <>0");
			if (objs != null) {
				qty = baseDao.getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)", "pd_padid=" + objs[0] + " AND pd_id <>" + pdid);
				aq = baseDao.getFieldDataByCondition("ProdIOApplyDetail", "pd_outqty", "pd_id=" + objs[0]);
				qty = qty == null ? 0 : qty;
				aq = aq == null ? 0 : aq;
				if (Double.parseDouble(String.valueOf(aq)) < Double.parseDouble(String.valueOf(qty)) + tQty) {
					BaseUtil.showError("出库数量超出申请数,超出数量:"
							+ (Double.parseDouble(String.valueOf(qty)) + tQty - Double.parseDouble(String.valueOf(aq))));
				}
				baseDao.updateByCondition("ProdIOApplyDetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty), "pd_id="
						+ objs[0]);
				baseDao.updateByCondition("ProdIOApplyDetail", "pd_yqty=0", "nvl(pd_yqty,0)<=0 and pd_id=" + objs[0]);
			}
		}
	}

	/**
	 * 出货单/销售退货单：明细销售单与单据收款方式不一致，不允许进行当前操作
	 * 
	 * @author mad
	 */
	public void prodInOut_ProdOut_paymentscheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_paymentcode,' ')<>nvl(sa_paymentscode,' ')"
								+ " and pd_piclass in ('出货单','销售退货单') and nvl(pd_ordercode,' ')<>' '", String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细销售单与单据收款方式不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 估价单：明细行物料存在有物料无单价（MRP无采购单价、MPS无加工单单价），限制提交
	 * 
	 * @author mad
	 */
	public void evaluation_commit_pricecheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(evd_detno) from EvaluationDetail,Product where evd_evid=? and evd_prodcode=pr_code and nvl(evd_price,0)=0 and nvl(evd_ifrep,0)=0 and nvl(evd_ifrep,0)=0 and pr_manutype not in ('CUSTOFFER','MAKE')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细物料中存在单价为0的情况，不允许进行当前操作！行号：" + dets);
		}
	}

	/**
	 * 出货通知单：明细销售订单业务员与单据业务员不一致，不允许进行当前操作
	 * 
	 * @author mad
	 */
	public void sendnotify_commit_sellercheck(Integer id) {
		String dets = baseDao.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from SendNotifyDetail left join Sendnotify on snd_snid=sn_id left join Sale on sa_code=snd_ordercode where "
								+ "snd_snid=? and nvl(sn_sellercode,' ')<>nvl(sa_sellercode,' ') and nvl(snd_ordercode,' ')<>' '",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细销售订单业务员与单据业务员不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 出货单、销售退货单：明细销售订单业务员与单据业务员不一致，不允许进行当前操作
	 * 
	 * @author mad
	 */
	public void prodinout_commit_sellercheck(Integer id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where "
						+ "pd_piid=? and nvl(pi_sellercode,' ')<>nvl(sa_sellercode,' ') and nvl(pd_ordercode,' ')<>' '", String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细销售订单业务员与单据业务员不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 报价单：保存、更新时，抓取最新有效的采购定价作为成本单价
	 * 
	 * @author mad
	 */
	public void quotation_save_getPrice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.execute("update quotation set qu_rate=(select cm_crrate from currencysmonth where qu_currency=cm_crname and to_char(qu_recorddate,'yyyymm')=cm_yearmonth) where qu_id="
				+ store.get("qu_id") + " and nvl(qu_rate,0)=0");
		StringBuffer error = new StringBuffer();
		int yearmonth = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select qd_prodcode,qd_id, nvl(qu_rate,1) qu_rate, to_char(qu_recorddate,'yyyymm') yearmonth from quotationdetail,quotation where qd_quid=qu_id and qd_quid=? ",
						store.get("qu_id"));
		while (rs.next()) {
			yearmonth = rs.getGeneralInt("yearmonth");
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_price, ppd_currency, ppd_rate from (select ppd_price, ppd_currency, ppd_rate from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where ppd_prodcode=? and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' order by pp_auditdate desc) where rownum<2",
							rs.getObject("qd_prodcode"));
			if (rs1.next()) {
				Object cmcrrate = baseDao.getFieldDataByCondition("currencysmonth", "cm_crrate",
						"cm_crname='" + rs1.getObject("ppd_currency") + "' and cm_yearmonth=" + yearmonth);
				cmcrrate = cmcrrate == null ? 1 : cmcrrate;
				Double price = rs1.getGeneralDouble("ppd_price") * Double.parseDouble(cmcrrate.toString())
						/ (1 + rs1.getGeneralDouble("ppd_rate") / 100) / rs.getGeneralDouble("qu_rate");
				baseDao.execute("update quotationdetail set qd_factprice=round(" + price + ",8) where qd_id=" + rs.getInt("qd_id"));
			} else {
				error.append("根据物料编号:[" + rs.getObject("qd_prodcode") + "]在物料核价单中未找到对应单价！<BR/>");
			}
		}
		if (error.length() > 0) {
			BaseUtil.appendError(error.toString());
		}
	}

	/**
	 * 销售订单：保存、更新时，抓取最新有效的采购定价作为成本单价
	 * 
	 * @author xiongzx
	 */
	public void Sale_save_getPrice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.execute("update sale set sa_rate=(select cm_crrate from currencysmonth where sa_currency=cm_crname and to_char(sa_recorddate,'yyyymm')=cm_yearmonth) where sa_id="
				+ store.get("sa_id") + " and nvl(sa_rate,0)=0");
		StringBuffer error = new StringBuffer();
		int yearmonth = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select sd_prodcode,sd_id, nvl(sa_rate,1) sa_rate, to_char(sa_recorddate,'yyyymm') yearmonth from saledetail,sale where sd_said=sa_id and sd_said=? ",
						store.get("sa_id"));
		while (rs.next()) {
			yearmonth = rs.getGeneralInt("yearmonth");
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_price, ppd_currency, ppd_rate from (select ppd_price, ppd_currency, ppd_rate from PurchasePriceDetail "
							+ "left join PurchasePrice on ppd_ppid=pp_id left join CURRENCYSMONTH on cm_crname=ppd_currency and to_char(sysdate,'yyyymm')=cm_yearmonth"
							+ "  where ppd_prodcode=? and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') "
							+ "and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' "
							+ "order by pp_auditdate desc,nvl(ppd_price,0)*nvl(CM_CRRATE,0)/(1+nvl(ppd_rate,0)/100)) where rownum<2",
							rs.getObject("sd_prodcode"));
			if (rs1.next()) {
				Object cmcrrate = baseDao.getFieldDataByCondition("currencysmonth", "cm_crrate",
						"cm_crname='" + rs1.getObject("ppd_currency") + "' and cm_yearmonth=" + yearmonth);
				cmcrrate = cmcrrate == null ? 1 : cmcrrate;
				Double price = rs1.getGeneralDouble("ppd_price") * Double.parseDouble(cmcrrate.toString())
						/ (1 + rs1.getGeneralDouble("ppd_rate") / 100) / rs.getGeneralDouble("sa_rate");
				baseDao.execute("update saledetail set sd_costingprice=round(" + price + ",8) where sd_id=" + rs.getInt("sd_id"));
			} else {
				error.append("根据物料编号:[" + rs.getObject("sd_prodcode") + "]在物料核价单中未找到对应单价！<BR/>");
			}
		}
		if (error.length() > 0) {
			BaseUtil.appendError(error.toString());
		}
	}

	/**
	 * 销售订单:保存、更新后，提示未按照最小订购量下单
	 * 
	 * @author maz
	 */
	public void sale_save_zxdgcheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select sd_prodcode,sd_detno from (select sd_prodcode,sum(sd_qty) qty,sd_detno from saledetail where sd_said="+store.get("sa_id")+" group by sd_prodcode,sd_detno)A left join product on A.sd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小订购量下单！物料编号：" + dets.getString("sd_prodcode") + ",行号:"+dets.getInt("sd_detno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 销售订单:保存、更新后，提示未按照最小包装量下单
	 * 
	 * @author maz
	 */
	public void sale_save_zxbzcheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select sd_prodcode,sd_detno from (select sd_prodcode,sum(sd_qty) qty,sd_detno from saledetail where sd_said="+store.get("sa_id")+" group by sd_prodcode,sd_detno)A left join product on A.sd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小包装量下单！物料编号：" + dets.getString("sd_prodcode") + ",行号:"+dets.getString("sd_detno")+"<br>");
		}	
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 销售订单:提交后，提示未按照最小订购量下单
	 * 
	 * @author maz
	 */
	public void sale_commit_zxdgcheck(Integer sa_id) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select sd_prodcode,sd_detno from (select sd_prodcode,sum(sd_qty) qty,sd_detno from saledetail where sd_said="+sa_id+" group by sd_prodcode,sd_detno)A left join product on A.sd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小订购量下单！物料编号：" + dets.getString("sd_prodcode") + ",行号:"+dets.getInt("sd_detno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 销售订单:提交后，提示未按照最小包装量下单
	 * 
	 * @author maz
	 */
	public void sale_commit_zxbzcheck(Integer sa_id) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select sd_prodcode,sd_detno from (select sd_prodcode,sum(sd_qty) qty,sd_detno from saledetail where sd_said="+sa_id+" group by sd_prodcode,sd_detno)A left join product on A.sd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小包装量下单！物料编号：" + dets.getString("sd_prodcode") + ",行号:"+dets.getString("sd_detno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 出货单:保存、更新后，提示未按照最小包装量下单
	 * 
	 * @author maz
	 */
	public void prod_saveupdate_zxdgcheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select pd_prodcode,pd_pdno from (select pd_prodcode,sum(pd_outqty) qty,pd_pdno from ProdIODetail where pd_piid="+store.get("pi_id")+" group by pd_prodcode,pd_pdno)A left join product on A.pd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小包装量下单！物料编号：" + dets.getString("pd_prodcode") + ",行号:"+dets.getString("pd_pdno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}
	
	/**
	 * 出货单:保存、更新后，提示未按照最小订购量下单
	 * 
	 * @author maz
	 */
	public void prod_saveupdate_zxdhlcheck(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select pd_prodcode,pd_pdno from (select pd_prodcode,sum(pd_outqty) qty,pd_pdno from ProdIODetail where pd_piid="+store.get("pi_id")+" group by pd_prodcode,pd_pdno)A left join product on A.pd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小订购量下单！物料编号：" + dets.getString("pd_prodcode") + ",行号:"+dets.getString("pd_pdno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}
	
	/**
	 * 出货单:提交后，提示未按照最小订购量下单
	 * 
	 * @author maz
	 */
	public void prod_commit_zxdhlcheck(Integer pi_id) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select pd_prodcode,pd_pdno from (select pd_prodcode,sum(pd_outqty) qty,pd_pdno from ProdIODetail where pd_piid="+pi_id+" group by pd_prodcode,pd_pdno)A left join product on A.pd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小包装量下单！物料编号：" + dets.getString("pd_prodcode") + ",行号:"+dets.getString("pd_pdno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 出货单:提交后，提示未按照最小包装量下单
	 * 
	 * @author maz
	 */
	public void prod_commit_zxdgcheck(Integer pi_id) {
		SqlRowList dets = baseDao
				.queryForRowSet(
						"select pd_prodcode,pd_pdno from (select pd_prodcode,sum(pd_outqty) qty,pd_pdno from ProdIODetail where pd_piid="+pi_id+" group by pd_prodcode,pd_pdno)A left join product on A.pd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))");
		StringBuffer sb = new StringBuffer();
		while (dets.next()) {
			sb.append("未按照最小包装量下单！物料编号：" + dets.getString("pd_prodcode") + ",行号:"+dets.getString("pd_pdno")+"<br>");
		}
		if (sb.length()>0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 在请购批量转采购筛选之前，获取标准料号一小时内的平台库存
	 * 
	 * @author XiaoST
	 * 
	 */
	public void applicationToSale_QueryBefore(String condition) {
		if (condition == null || condition.equals("")) {
			condition = " 1=1";
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select distinct pr_uuid from Application left join scm_ApplicationDetail_view on ap_id=ad_apid "
						+ "left join Product on ad_prodcode=pr_code where nvl(pr_uuid,' ')<>' ' and ad_qty>nvl(ad_yqty,0) and ap_statuscode = 'AUDITED' and nvl(ad_statuscode,' ') not in ('FINISH','NULLIFIED','FREEZE')  and "
						+ condition + " "
						+ "and pr_uuid not in (select go_uuid from B2C$GoodsOnhand where ROUND(TO_NUMBER(sysdate-go_synctime) * 24)<1)");
		if (rs.next()) {
			StringBuffer strs = new StringBuffer();
			for (Map<String, Object> map : rs.getResultList()) {// 分多次进行传送
				strs.append(map.get("pr_uuid") + ",");
			}
			String uuids = strs.substring(0, strs.length() - 1);
			getGoodsReserveService.getGoodsOnhand(uuids);
			getGoodsReserveService.getGoodsBatch(uuids);
		}
	}

	/**
	 * 采购单：提交之后，针对应付供应商是02.01.028强行更新报关价（万利达科技）
	 * */
	public void malata_purchase_update_bgprice(Integer id) {
		String code = baseDao.getFieldValue("Purchase", "pu_receivecode", "pu_id=" + id, String.class);
		if ("02.01.028".equals(code)) {
			baseDao.execute("update purchasedetail set pd_bgprice=pd_price*case when pd_price>=10 then 1.01 else 1.02 end where pd_puid="
					+ id);
		}
	}

	/**
	 * 采购单：提交之前，限制累计下单数不能超过限购量（万利达科技）
	 */
	public void purchase_commit_mobcheckmaxlimit(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet(selectpurstr, new Object[] { id });
		while (rs.next()) {
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select ppd_maxlimit,ppd_id from purchasepricedetail left join purchaseprice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and ppd_price=? and ppd_rate=? and ppd_statuscode='VALID' and pp_kind='采购' and nvl(ppd_maxlimit,0)>0",
							new Object[] { rs.getObject("pu_vendcode"), rs.getObject("pd_prodcode"), rs.getObject("pu_currency"),
									rs.getObject("pd_price"), rs.getObject("pd_rate") });
			if (rs1.next()) {
				/**
				 * 双单位 最大限购量转换成按照采购单位数量计算
				 */
				double pursumqty = baseDao
						.getSummaryByField(
								"purchasedetail left join purchase on pd_puid=pu_id",
								"case when pd_mrpstatuscode='FINISH' then pd_acceptqty else (case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end) end",
								"pu_vendcode='"
										+ rs.getObject("pu_vendcode")
										+ "' and pu_currency='"
										+ rs.getObject("pu_currency")
										+ "' and pd_prodcode='"
										+ rs.getObject("pd_prodcode")
										+ "' and pd_price='"
										+ rs.getObject("pd_price")
										+ "' and pd_rate="
										+ rs.getObject("pd_rate")
										+ " and trunc(pu_date)>=(select trunc(NVL(ppd_fromdate,sysdate)) from purchasepricedetail where ppd_id="
										+ rs1.getGeneralInt("ppd_id") + ")");
				if (rs1.getGeneralDouble("ppd_maxlimit") < pursumqty) {
					BaseUtil.showError("明细序号" + rs.getObject("pd_detno") + "累计下单数[" + pursumqty + "]不能超过限购量["
							+ rs1.getGeneralDouble("ppd_maxlimit") + "]！");
				}
			}
		}
	}

	/**
	 * 采购变更单：提交之前， 针对应付供应商是02.01.028强行更新报关价(万利达科技)
	 * */
	public void purchasechange_update_bgprice(Integer id) {
		String code = baseDao.getFieldValue("PurchaseChange", "pc_newapvendcode", "pc_id=" + id, String.class);
		if ("02.01.028".equals(code)) {
			baseDao.execute("update purchasechangedetail set pcd_newbgprice=pcd_newprice*case when pcd_newprice>=10 then 1.01 else 1.02 end where pcd_pcid="
					+ id);
		}
	}

	/**
	 * 采购变更单：审核之后，将新报关单价返回到采购单中(万利达科技)
	 * */
	public void purchasechange_updatepurchase_bgprice(Integer id) {
		String code = baseDao.getFieldValue("PurchaseChange", "pc_newapvendcode", "pc_id=" + id, String.class);
		if ("02.01.028".equals(code)) {
			baseDao.execute("update purchasedetail set pd_bgprice=(select pcd_newbgprice from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pcd_newbgprice,0)<>nvl(pcd_bgprice,0)) where (pd_code, pd_detno) in (select pc_purccode,pcd_pddetno from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id="
					+ id + ")");
		}
	}

	/**
	 * 出货通知单： 提交币别为USD，需填写未到账金额，余款到期日 （万利达科技）
	 */
	public void sendnotify_commit_before_check(Integer id) {
		boolean neednt = baseDao.checkIf("SendNotify", "sn_id=" + id
				+ " and sn_currency='USD' and (sn_ntbamount is null or sn_tduedate is null)");
		if (neednt) {
			BaseUtil.showError("币别为USD需填写未到账金额和余款到期日期!");
		}
	}

	/**
	 * 出货单： 提交 币别为USD，需要填写收款状态 （万利达科技）
	 */
	public void prodinout_commit_before_check(Integer id) {
		boolean neednt = baseDao.checkIf("prodiodetail left join prodinout on pi_id = pd_piid", "pd_piid=" + id
				+ " and pi_currency='USD' and pd_skstatus is null");
		if (neednt) {
			BaseUtil.showError("币别为USD需填写收款状态!");
		}
	}

	/**
	 * 出货通知单： 提交之前，物料+仓库，当前通知单数量大于库存数量-通知单预约数-出货预约数，不能提交
	 */
	public void sendnotify_commit_before_yyscheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select snd_prodcode,snd_warehousecode,qty,pwd_onhand,pwd_leftqty,pwd_sndyys,pwd_pdyys  from (select snd_prodcode,snd_warehousecode, sum(snd_outqty) qty from sendnotifydetail where snd_snid=? and nvl(snd_warehousecode,' ')<>' ' group by snd_prodcode,snd_warehousecode) left join SCM_PW_OUTQTY_VIEW on snd_prodcode=pwd_prodcode and snd_warehousecode=pwd_whcode where nvl(qty,0)>nvl(pwd_leftqty,0)",
						new Object[] { id });
		StringBuffer error = new StringBuffer();
		while (rs.next()) {
			error.append("物料编号[" + rs.getObject("snd_prodcode") + "]仓库[" + rs.getObject("snd_warehousecode") + "]，当前通知单数量["
					+ rs.getGeneralDouble("qty") + "]大于库存数量[" + rs.getGeneralDouble("pwd_onhand") + "]-通知单预约数["
					+ rs.getGeneralDouble("pwd_sndyys") + "]-出货预约数[" + rs.getGeneralDouble("pwd_pdyys") + "]，不能提交！<BR/>");
		}
		if (error.length() > 0) {
			BaseUtil.showError(error.toString());
		}
	}

	/**
	 * 装箱单：物料编号+合同号对应总数量与出货单物料编号+合同号数量不一致，限制提交
	 * 
	 * @author mad
	 */
	public void packing_commit_qtycheck(Integer id) {
		Object relativecode = baseDao.getFieldDataByCondition("Packing", "pi_relativecode", "pi_id=" + id);
		if (relativecode != null) {
			relativecode = "'" + relativecode.toString().replaceAll(",", "','") + "'";
		}
		Object prcode = null;
		Object ordercode = null;
		double qty = 0.0;
		SqlRowList pa = null;
		StringBuffer error = new StringBuffer();
		SqlRowList sl = baseDao
				.queryForRowSet("select sum(nvl(pd_outqty,0)+nvl(pd_inqty,0)) qty, pd_prodcode, pd_pocode from prodiodetail where pd_inoutno in ("
						+ relativecode + ") group by pd_prodcode, pd_pocode");
		while (sl.next()) {
			prcode = sl.getObject("pd_prodcode");
			ordercode = sl.getObject("pd_pocode");
			qty = sl.getGeneralDouble("qty");
			pa = baseDao
					.queryForRowSet(
							"select sum(nvl(pd_qty,0)*NVL(pd_cartons,0)) qty from packingdetail where pd_piid = ? and pd_prodcode=? and pd_pocode=?",
							id, prcode, ordercode);
			if (pa.next()) {
				if (qty != pa.getGeneralDouble("qty")) {
					error.append("物料编号[" + prcode + "]+客户PO号[" + ordercode + "]对应总数量[" + pa.getGeneralDouble("qty") + "]与出货单物料编号+客户PO号数量["
							+ qty + "]不一致");
				}
			} else {
				BaseUtil.showError("物料编号[" + prcode + "]和合同号[" + ordercode + "]没有对应的装箱单！");
			}
		}
		if (error.length() > 0) {
			BaseUtil.showError(error.toString());
		}
	}

	/**
	 * 出货发票：物料编号+合同号对应总数量与出货单物料编号+合同号数量不一致，限制提交
	 * 
	 * @author mad
	 */
	public void invoice_commit_qtycheck(Integer id) {
		Object relativecode = baseDao.getFieldDataByCondition("invoice", "in_relativecode", "in_id=" + id);
		if (relativecode != null) {
			relativecode = "'" + relativecode.toString().replaceAll(",", "','") + "'";
		}
		Object prcode = null;
		Object ordercode = null;
		double qty = 0.0;
		SqlRowList pa = null;
		StringBuffer error = new StringBuffer();
		SqlRowList sl = baseDao
				.queryForRowSet("select sum(nvl(pd_outqty,0)+nvl(pd_inqty,0)) qty, pd_prodcode, pd_pocode from prodiodetail where pd_inoutno in ("
						+ relativecode + ") group by pd_prodcode, pd_pocode");
		while (sl.next()) {
			prcode = sl.getObject("pd_prodcode");
			ordercode = sl.getObject("pd_pocode");
			qty = sl.getGeneralDouble("qty");
			pa = baseDao.queryForRowSet(
					"select sum(nvl(id_qty,0)) qty from invoicedetail where id_inid = ? and id_prodcode=? and id_pocode=?", id, prcode,
					ordercode);
			if (pa.next()) {
				if (qty != pa.getGeneralDouble("qty")) {
					error.append("物料编号[" + prcode + "]+客户PO号[" + ordercode + "]对应总数量[" + pa.getGeneralDouble("qty") + "]与出货单物料编号+客户PO号数量["
							+ qty + "]不一致");
				}
			} else {
				BaseUtil.showError("物料编号[" + prcode + "]和客户PO号[" + ordercode + "]没有对应的出货发票！");
			}
		}
		if (error.length() > 0) {
			BaseUtil.showError(error.toString());
		}
	}

	/**
	 * 销售报价单:保存、更新后，根据销售利润率设置数据抓取目标利润率
	 * 
	 * @author mad
	 */
	public void quotation_save_getprofit(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		quotation_commit_getprofit(Integer.parseInt(store.get("qu_id").toString()));
	}

	/**
	 * 销售报价单:提交之前，根据销售利润率设置数据抓取目标利润率
	 * 
	 * @author mad
	 */
	public void quotation_commit_getprofit(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from quotationdetail,quotation where qd_quid=qu_id and qd_quid=?", id);
		while (rs.next()) {
			baseDao.updateByCondition(
					"Quotationdetail",
					"qd_profit=nvl((select spd_profit from (select spd_profit from saleProfit left join saleProfitDetail on spd_spid=sp_id where spd_district='"
							+ rs.getObject("qd_district")
							+ "' and spd_step='"
							+ rs.getObject("qd_step")
							+ "' and spd_kind='"
							+ rs.getObject("qd_kind")
							+ "'  and to_char(sp_startdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(sp_enddate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND sp_statuscode='AUDITED' and nvl(spd_profit,0)<>0 order by sp_date desc) where rownum=1),0)",
					"qd_id=" + rs.getObject("qd_id") + " and nvl(qd_profit,0)=0");
		}
	}

	/**
	 * 物料核价单:提交之前，核价类型为采购的，明细行不能选择生产类型为委外的物料
	 * 
	 * @author mad
	 */
	public void purchaseprice_commit_checkkind(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join Product on ppd_prodcode=pr_code where ppd_ppid=? and pp_kind='采购' and pr_manutype='OSMAKE' ",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("核价类型为采购的，明细行不能选择生产类型为委外的物料!行号：" + dets);
		}
	}

	/**
	 * 物料核价单:提交之前，核价类型为委外的，明细行不能选择生产类型为采购的物料
	 * 
	 * @author mad
	 */
	public void purchaseprice_commit_checkkindos(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join Product on ppd_prodcode=pr_code where ppd_ppid=? and pp_kind='委外' and pr_manutype='PURCHASE' ",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("核价类型为委外的，明细行不能选择生产类型为采购的物料!行号：" + dets);
		}
	}

	/**
	 * 销售订单：审核后更新明细PMC回复日期等于审核日期
	 */
	public void sale_audit_after_updatePmcdate(Integer id) {
		baseDao.updateByCondition("SaleDetail", "sd_pmcdate=sysdate", "sd_said=" + id);
	}

	/**
	 * 销售订单：审核后更新明细PMC回复日期等于审核日期
	 */
	public void saleForecast_audit_after_updateNeeddate(Integer id) {
		baseDao.updateByCondition("SaleForecastDetail", "sd_needdate=sysdate", "sd_sfid=" + id);
	}

	/**
	 * 销售订单批量转通知单时,同一客户,同一币别不允许不同的税率。
	 * 
	 * @param gstore
	 */
	public void sale_SometurnSendNotify_checktaxrate(ArrayList<Map<Object, Object>> gstore) {
		StringBuffer sb1 = new StringBuffer("(");
		for (Map<Object, Object> store : gstore) {
			sb1.append("'");
			sb1.append(store.get("sd_id"));
			sb1.append("',");
		}
		String sdid1 = sb1.substring(0, sb1.length() - 1) + ")";
		String sql = "select count(distinct a1.sd_taxrate) from saledetail a1 left join sale a3 on sd_said=sa_id ,saledetail a2 left join sale a4 on sd_said=sa_id where a1.sd_id<>a2.sd_id and a1.sd_taxrate<>a2.sd_taxrate and a1.sd_id in "
				+ sdid1 + " and a2.sd_id in" + sdid1 + " and a3.SA_CUSTCODE=a4.SA_CUSTCODE and a3.SA_CURRENCY=a4.SA_CURRENCY and 1=?";
		int count = baseDao.queryForObject(sql, Integer.class, 1);
		if (count > 1) {
			BaseUtil.showError("当前选择的数据，存在同一客户,同一币别,多个税率的情况，不能转入!");
		}
	}

	/**
	 * 销售订单批量转出货单时,同一客户,同一币别不允许不同的税率。
	 * 
	 * @param gstore
	 */
	public void sale_SomeTurnProdIN2_checktaxrate(ArrayList<Map<Object, Object>> gstore) {
		StringBuffer sb1 = new StringBuffer("(");
		for (Map<Object, Object> store : gstore) {
			sb1.append("'");
			sb1.append(store.get("sd_id"));
			sb1.append("',");
		}
		String sdid1 = sb1.substring(0, sb1.length() - 1) + ")";
		String sql = "select count(distinct a1.sd_taxrate) from saledetail a1 left join sale a3 on sd_said=sa_id ,saledetail a2 left join sale a4 on sd_said=sa_id where a1.sd_id<>a2.sd_id and a1.sd_taxrate<>a2.sd_taxrate and a1.sd_id in "
				+ sdid1 + " and a2.sd_id in" + sdid1 + " and a3.SA_CUSTCODE=a4.SA_CUSTCODE and a3.SA_CURRENCY=a4.SA_CURRENCY and 1=?";
		int count = baseDao.queryForObject(sql, Integer.class, 1);
		if (count > 1) {
			BaseUtil.showError("当前选择的数据，存在同一客户,同一币别,多个税率的情况，不能转入!");
		}
	}

	/**
	 * 欣康宁：指定仓库或不指定仓库时，本次转数量超出仓库可用量（库存数量 - 未过账出库单已锁定批号数量）则会限制转 maz
	 * 
	 * @param gstore
	 */
	public void sale_SomeTurnProdIN2_checkqty(ArrayList<Map<Object, Object>> gstore) {
		Object wh_code = gstore.get(0).get("wh_code");
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(gstore, new Object[] { "sd_prodcode" });
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		StringBuffer sb = new StringBuffer();
		for (Object s : mapSet) {
			int allqty = 0;
			int tqty = 0;
			items = groups.get(s);
			if (StringUtil.hasText(wh_code)) { // 指定仓库
				for (Map<Object, Object> map : items) {
					Object pw_onhand = baseDao.getFieldDataByCondition("PM_PWONHAND_VIEW", "nvl(v_pw_onhand,0)onhand", "v_pw_prodcode='"
							+ map.get("sd_prodcode") + "' and v_pw_whcode='" + wh_code + "'");
					Object outqty = baseDao.getFieldDataByCondition("ProdIOdetail left join Prodinout on pd_piid=pi_id",
							"nvl(sum(pd_outqty),0)outqty",
							"(pd_piclass='出货单' or pd_piclass='生产领料单') and pi_status='未过账' and pd_prodcode='" + map.get("sd_prodcode") + "' and pd_whcode='"
									+ wh_code + "'");
					pw_onhand = pw_onhand == null ? 0 : pw_onhand;
					outqty = outqty == null ? 0 : outqty;
					allqty = Integer.parseInt(pw_onhand.toString()) - Integer.parseInt(outqty.toString());	
					tqty = tqty + Integer.parseInt(map.get("sd_tqty").toString());
				}
				if (tqty > allqty) {
					sb.append("" + items.get(0).get("sd_prodcode") + "本次转数量" + tqty + "超出该仓库可用量" + allqty + ",不允许转单");
				}
			} else { // 没有指定仓库
				for (Map<Object, Object> map : items) {
					tqty = tqty + Integer.parseInt(map.get("sd_tqty").toString());
				}
				SqlRowList rs = baseDao
						.queryForRowSet("select * from (select (sum(onhand)-sum(outqty))allowqty from (select nvl(v_pw_onhand,0)onhand,v_pw_whcode,"
								+ "v_pw_prodcode,nvl(outqty,0)outqty from PM_PWONHAND_VIEW left join warehouse on v_pw_whcode=wh_code left join "
								+ "(select nvl(sum(pd_outqty),0)outqty,pd_prodcode,pd_whcode from prodiodetail left join prodinout on pd_piid=pi_id where "
								+ "pd_piclass='出货单' and pi_status='未过账' group by pd_prodcode,pd_whcode) on  "
								+ "v_pw_prodcode=pd_prodcode where v_pw_prodcode='"
								+ items.get(0).get("sd_prodcode")
								+ "')) where nvl(allowqty,0)-" + tqty + "<0");
				if (rs.next()) {
					Object allqty1 = rs.getObject("allowqty") == null ? 0 : rs.getObject("allowqty");
					sb.append("" + items.get(0).get("sd_prodcode") + "本次转数量" + tqty + "超出仓库可用量" + allqty1 + ",不允许转单");
				}
			}
		}
		BaseUtil.showError(sb.toString());
	}

	/**
	 * 信扬国际 销售订单转出货单
	 */
	public void sale_TurnProd_checkdeposit(ArrayList<Map<Object, Object>> gstore) {
		if (gstore != null && gstore.size() > 0) {
			double sa_deposit = gstore.get(0).get("sa_deposit") == null ? 0 : Double
					.parseDouble(gstore.get(0).get("sa_deposit").toString());
			if (sa_deposit > 0) {// 如果本次冲金额大于0
				for (Map<Object, Object> store : gstore) {
					Object sa_code = store.get("sa_code");
					String dets = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(pi_inoutno) from prodinout where pi_status<>'已过账' and nvl(pi_thisdeposit,0)>0 and pi_sourcecode='"
									+ sa_code + "'", String.class);
					if (dets != null) {
						BaseUtil.showError("销售订单：" + sa_code + "已转出货单：" + dets + "且出货单未过账、本次冲定金大于0，不允许转出货单！");
					}
				}
			}
		}
	}

	/**
	 * 信扬国际 销售订单转出货单
	 */
	public void sale_TurnProd_updatedeposit(ArrayList<Map<Object, Object>> gstore) {
		if (gstore != null && gstore.size() > 0) {
			for (Map<Object, Object> store : gstore) {
				Object sa_code = store.get("sa_code");
				baseDao.execute("update sale set sa_alldeposit=nvl(sa_deposit,0)-(select sum(nvl(pi_thisdeposit,0)) from prodinout where pi_sourcecode=sa_code) where sa_code='"
						+ sa_code + "'");
			}
		}
	}

	/**
	 * 出货通知单转出货单时，同一客户，同一币别不允许不同税率
	 * 
	 * @param gstore
	 */
	public void sendnotify_SometurnProdIO_checktaxrate(ArrayList<Map<Object, Object>> gstore) {
		StringBuffer sb1 = new StringBuffer("(");
		for (Map<Object, Object> store : gstore) {
			sb1.append("'");
			sb1.append(store.get("snd_id"));
			sb1.append("',");
		}
		String sdid1 = sb1.substring(0, sb1.length() - 1) + ")";
		String sql = "select count(distinct a1.snd_taxrate) from SendNotifyDetail a1 left join SendNotify a3 on snd_snid=sn_id ,SendNotifyDetail a2 left join SendNotify a4 on snd_snid=sn_id where a1.snd_id<>a2.snd_id and a1.snd_taxrate<>a2.snd_taxrate and a1.snd_id in "
				+ sdid1 + " and a2.snd_id in" + sdid1 + " and a3.SN_CUSTCODE=a4.SN_CUSTCODE and a3.SN_CURRENCY=a4.SN_CURRENCY and 1=?";
		int count = baseDao.queryForObject(sql, Integer.class, 1);
		if (count > 1) {
			BaseUtil.showError("当前选择的数据，存在同一客户,同一币别,多个税率的情况，不能转入!");
		}
	}

	/**
	 * 业务员销售预测审核自动生成已审核的销售预测(富为)
	 */
	public void preSaleForecast_audit_after_turnSaleForecast(Integer sf_id) {
		int id = baseDao.getSeqId("SaleForecast_SEQ");
		String code = baseDao.sGetMaxNumber("SaleForecast", 2);
		int detailid = 0;
		int detno = 1;
		List<String> sqlList = new ArrayList<String>();
		Employee employee = SystemSession.getUser();
		/**
		 * 富为 系统问题反馈单号:2016120362 修改:业务员预测审核后
		 * 自动产生的销售预测主表日期sf_date、录入日期sf_tilldate默认为来源业务员预测的录入日期sf_date
		 * 
		 * @author wsy
		 */
		SqlRowList rs = baseDao
				.queryForRowSet("select sd_id,sd_prodcode,sd_qty,sd_person,sf_sellercode,sf_sellername,"
						+ "to_char(sd_startdate,'yyyy-mm-dd') sd_startdate,to_char(sd_enddate,'yyyy-mm-dd') sd_enddate,"
						+ "sf_code,nvl(sd_custcode,' ') sd_custcode,nvl(sd_custname,' ') sd_custname,to_char(sf_date,'yyyy-mm-dd') sf_date from PreSaleForecastDetail"
						+ " left join PreSaleForecast on sf_id=sd_sfid where nvl(sd_statuscode,' ')<>'TURNSF' and sd_qty<>0"
						+ " and sf_id=" + sf_id);
		while (rs.next()) {
			detailid = baseDao.getSeqId("SaleForecastDetail_seq");
			sqlList.add("insert into SaleForecastDetail(sd_id,sd_sfid,sd_detno,sd_prodcode,sd_qty,sd_person,"
					+ "sd_sellercode,sd_seller,sd_needdate,sd_startdate,sd_enddate,sd_source,sd_custcode,sd_custname,sd_sourceqty) "
					+ "values("
					+ detailid
					+ ","
					+ id
					+ ","
					+ detno++
					+ ",'"
					+ rs.getString("sd_prodcode")
					+ "','"
					+ rs.getDouble("sd_qty")
					+ "','"
					+ rs.getString("sd_person")
					+ "',"
					+ "'"
					+ rs.getString("sf_sellercode")
					+ "','"
					+ rs.getString("sf_sellername")
					+ "',to_date('"
					+ rs.getString("sd_startdate")
					+ "','yyyy-mm-dd'),"
					+ "to_date('"
					+ rs.getString("sd_startdate")
					+ "','yyyy-mm-dd'),to_date('"
					+ rs.getString("sd_enddate")
					+ "','yyyy-mm-dd'),'"
					+ rs.getString("sf_code")
					+ "','"
					+ rs.getString("sd_custcode") + "','" + rs.getString("sd_custname") + "','" + rs.getDouble("sd_qty") + "')");
			sqlList.add("update PreSaleForecastDetail set sd_statuscode='TURNSF',sd_status='已转销售预测', sd_sourceid='" + detailid
					+ "',sd_source='" + code + "' where sd_id=" + rs.getInt("sd_id"));
		}
		sqlList.add("insert into saleforecast (sf_id,sf_code,sf_date,sf_status,sf_statuscode,sf_userid,sf_username,sf_department,sf_name,sf_tilldate) "
				+ "select "
				+ id
				+ ",'"
				+ code
				+ "',to_date('"
				+ rs.getString("sf_date")
				+ "','yyyy-mm-dd'),'已审核','AUDITED','"
				+ employee.getEm_id()
				+ "','"
				+ employee.getEm_name()
				+ "' , sf_department,sf_sellername,to_date('"
				+ rs.getString("sf_date") + "','yyyy-mm-dd')  from PreSaleForecast where sf_id=" + sf_id);
		baseDao.execute(sqlList);
		baseDao.execute("update SaleForecast set (sf_custcode,sf_custname) =(select max(sd_custcode) sd_custcode,max(sd_custname) sd_custname "
				+ "from SaleForecastDetail where nvl(sd_custcode,' ')<>' ' and sd_sfid=" + id + ") where sf_id=" + id);
	}

	/**
	 * wusy
	 */
	public void preSaleForecast_commit_limit(Integer id) {

		List<Object[]> objects = baseDao.getFieldsDatasByCondition("PreSaleForecast left join PreSaleForecastDetail on sf_id=sd_sfid",
				new String[] { "sf_sellercode", "sd_prodcode", "sd_custcode", "to_char(sd_startdate,'yyyy-mm-dd')",
						"to_char(sd_enddate,'yyyy-mm-dd')" }, "sf_id=" + id);
		for (Object[] os : objects) {
			Object sfcode = baseDao.getFieldDataByCondition("PreSaleForecast left join PreSaleForecastDetail on sf_id=sd_sfid",
					"WMSYS.WM_CONCAT(distinct PRESALEFORECAST.SF_CODE)", " sf_sellercode='" + os[0] + "' and sd_prodcode='" + os[1]
							+ "' and nvl(sd_custcode,' ')='" + (os[2] == null ? ' ' : os[2].toString()) + "' and (((to_date('" + os[3]
							+ "','yyyy-mm-dd')<=sd_enddate) and to_date('" + os[4] + "','yyyy-mm-dd')>=sd_startdate) or((to_date('" + os[3]
							+ "','yyyy-mm-dd')<=sd_startdate) and to_date('" + os[4]
							+ "','yyyy-mm-dd')<=sd_enddate)) and sf_status<>'在录入' and sf_id<>" + id + " ");
			if (sfcode != null) {
				BaseUtil.showError("相同" + os[1] + "+客户" + os[2] + "在期间" + os[3].toString().substring(0, 10) + "到"
						+ os[4].toString().substring(0, 10) + "内已经存在预测，不能重复预测    ");
			}
		}
	}

	/**
	 * 销售报价单保存，更新时：自动去客户物料对照表将该用户和物料编号的客户料号赋值过来
	 * 
	 * @param store
	 * @param gstore
	 */
	public void Quotation_save_after_updateCustprodcode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String sql = "select qd_id,qd_prodcode,qd_custprodcode from QuotationDetail where qd_quid=" + store.get("qu_id") + "";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			String sql1 = "update QuotationDetail set qd_custprodcode=(select pc_custprodcode from ProductCustomer left join Product on pc_prodcode=pr_code where pc_custcode='"
					+ store.get("qu_custcode")
					+ "' and pc_prodcode='"
					+ rs.getString("qd_prodcode")
					+ "') where qd_id="
					+ rs.getString("qd_id") + " and qd_quid=" + store.get("qu_id") + "";
			baseDao.execute(sql1);
		}
	}

	/**
	 * 客户预录入：提交之前提示应收客户编号与客户编号不一致
	 * 
	 * @author madan 2016-09-14 17:09:59
	 */
	public void customer_commit_arcustcheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select cu_arcode,cu_shcustcode,cu_code from precustomer where cu_id=?", id);
		if (rs.next()) {
			if (!rs.getGeneralString("cu_arcode").equals(rs.getGeneralString("cu_code"))) {
				BaseUtil.appendError("应收客户编号[" + rs.getGeneralString("cu_arcode") + "]与客户编号[" + rs.getGeneralString("cu_code")
						+ "]不一致！<br>");
			}
			if (!rs.getGeneralString("cu_shcustcode").equals(rs.getGeneralString("cu_code"))) {
				BaseUtil.appendError("收货客户编号[" + rs.getGeneralString("cu_shcustcode") + "]与客户编号[" + rs.getGeneralString("cu_code")
						+ "]不一致！<br>");
			}
		}
	}

	/**
	 * 采购单：保存，更新之后，采购单位数量为0、物料转换率不为0则计算采购单位数量=数量*转换率
	 * 
	 * @param store
	 * @param gstore
	 */
	public void purchase_save_after_assqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_id,pr_purcrate from purchasedetail left join product on pd_prodcode=pr_code where pd_puid="
						+ store.get("pu_id") + " and nvl(pd_assqty,0)=0 and nvl(pr_purcrate,0)<>0");
		while (rs.next()) {
			baseDao.execute("update purchasedetail set pd_assqty=round(nvl(pd_qty,0)*" + rs.getGeneralDouble("pr_purcrate")
					+ ",2) WHERE PD_ID=" + rs.getInt("pd_id"));
		}
	}

	/**
	 * 采购单：提交之前，采购单位数量为0、物料转换率不为0则计算采购单位数量=数量*转换率
	 * 
	 * @param store
	 * @param gstore
	 */
	public void purchase_commit_assqty(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_id,pr_purcrate from purchasedetail left join product on pd_prodcode=pr_code where pd_puid=" + id
						+ " and nvl(pd_assqty,0)=0 and nvl(pr_purcrate,0)<>0");
		while (rs.next()) {
			baseDao.execute("update purchasedetail set pd_assqty=round(nvl(pd_qty,0)*" + rs.getGeneralDouble("pr_purcrate")
					+ ",2) WHERE PD_ID=" + rs.getInt("pd_id"));
		}
	}

	/**
	 * 采购验收单：提交过账之前，按照物料转换率更新采购单位数量、成本单价
	 * 
	 * @param gstore
	 */
	public void purchase_turnProdIO_purcRate(Integer id) {
		if (!baseDao.isDBSetting("ProdInOut!PurcCheckin", "allowUpdatetRate")) {
			baseDao.execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where pi_currency=cm_crname and cm_yearmonth=to_char(pi_date,'yyyymm')),1) where pi_id="
					+ id);
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_id,pr_purcrate,pi_rate from prodiodetail left join prodinout on pi_id=pd_piid left join product on pd_prodcode=pr_code where pi_id="
						+ id + " and nvl(pr_purcrate,0)<>0");
		while (rs.next()) {
			baseDao.execute("update prodiodetail set pd_purcqty=round((nvl(pd_inqty,0)+nvl(pd_outqty,0))*"
					+ rs.getGeneralDouble("pr_purcrate") + ",2), pd_price=round(nvl(pd_orderprice,0)*" + rs.getGeneralDouble("pi_rate")
					+ "/(1+nvl(pd_taxrate,0)/100),8) WHERE PD_ID=" + rs.getInt("pd_id"));
		}
		baseDao.execute("update prodiodetail set pd_customprice=nvl((select nvl(pd_purcprice,0) from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_ordercode),0) where pi_id="
				+ id);
	}

	/**
	 * 采购验收单：保存更新之后，根据采购单位数量计算成本单价
	 * 
	 * @param store
	 * @param gstore
	 */
	public void prodcheckin_save_updateprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		if (!baseDao.isDBSetting("ProdInOut!PurcCheckin", "allowUpdatetRate")) {
			baseDao.execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where pi_currency=cm_crname and cm_yearmonth=to_char(pi_date,'yyyymm')),1) where pi_id="
					+ store.get("pi_id"));
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_id,pi_rate from prodiodetail left join prodinout on pi_id=pd_piid left join product on pd_prodcode=pr_code where pi_id="
						+ store.get("pi_id") + " and nvl(pd_purcqty,0)<>0 and nvl(pd_inqty,0)<>0");
		while (rs.next()) {
			baseDao.execute("update prodiodetail set pd_price=round(nvl(pd_customprice,0)*nvl(pd_purcqty,0)/nvl(pd_inqty,0)*"
					+ rs.getGeneralDouble("pi_rate") + "/(1+nvl(pd_taxrate,0)/100),8) WHERE PD_ID=" + rs.getInt("pd_id"));
		}
	}

	/**
	 * 采购应收单：保存更新后，根据采购单位数量计算采购单价
	 * 
	 * @param store
	 * @param gstore
	 */
	public void prodcheckin_save_updatePd_orderprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_purcqty,pd_customprice,pd_inqty,pi_rate,pd_id from ProdIODetail left join Product on pd_prodid=pr_id left join VerifyApplyDetail on pd_orderid=vad_id left join ProdInOut on pd_piid=pi_id where pd_piid='"
						+ store.get("pi_id") + "'");
		while (rs.next()) {
			if (rs.getGeneralInt("pd_purcqty") != 0 && rs.getGeneralInt("pd_inqty") != 0) {
				baseDao.execute("update prodiodetail set pd_orderprice=round(nvl(pd_customprice,0)*nvl(pd_purcqty,0)/(nvl(pd_inqty,0)*"
						+ rs.getGeneralDouble("pi_rate") + "),8) WHERE PD_ID=" + rs.getInt("pd_id"));
			}
		}
	}

	/**
	 * 业务员预测单保存后：自动获取上月销量，上三月平均销量，客户上月销量，客户上三月平均销量
	 * 
	 * @param store
	 * @param gstore
	 */
	public void preSaleForeCast_save_updateSaleqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sd_id,sd_custcode,sd_prodcode from PreSaleForecastDetail left join Product on sd_prodcode=pr_code left join (select pr_code prodcode,po_onhand,poqty,saqty from ma_prod_balance) on sd_prodcode=prodcode left join SCM_PRODQTY_CUST_VIEW on PreSaleForecastDetail.sd_custcode=SCM_PRODQTY_CUST_VIEW.custcode  and PreSaleForecastDetail.sd_prodcode=SCM_PRODQTY_CUST_VIEW.prodcode left join SCM_PRODQTY_VIEW on PreSaleForecastDetail.sd_prodcode=SCM_PRODQTY_VIEW.prcode where sd_sfid='"
						+ store.get("sf_id") + "'");
		while (rs.next()) {
			String sql = "update PreSaleForecastDetail set sd_saleqty1_cust_user=(select saleqty1_cust from scm_prodqty_cust_view where custcode='"
					+ rs.getGeneralString("sd_custcode")
					+ "' "
					+ "and prodcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "'),sd_saleqty_avg_cust_user=(select saleqty_avg_cust from scm_prodqty_cust_view where custcode='"
					+ rs.getGeneralString("sd_custcode")
					+ "' "
					+ "and prodcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "'),sd_saleqty1_user=(select saleqty1 from scm_prodqty_view where prcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "') "
					+ ",sd_saleqty_avg_user=(select saleqty_avg from scm_prodqty_view where prcode='"
					+ rs.getGeneralString("sd_prodcode") + "') where sd_id='" + rs.getInt("sd_id") + "'";
			baseDao.execute(sql);
		}
	}

	/**
	 * 业务员预测单更新后：自动获取上月销量，上三月平均销量，客户上月销量，客户上三月平均销量
	 * 
	 * @param store
	 * @param gstore
	 */
	public void preSaleForeCast_update_updateSaleqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sd_id,sd_custcode,sd_prodcode from PreSaleForecastDetail left join Product on sd_prodcode=pr_code left join (select pr_code prodcode,po_onhand,poqty,saqty from ma_prod_balance) on sd_prodcode=prodcode left join SCM_PRODQTY_CUST_VIEW on PreSaleForecastDetail.sd_custcode=SCM_PRODQTY_CUST_VIEW.custcode  and PreSaleForecastDetail.sd_prodcode=SCM_PRODQTY_CUST_VIEW.prodcode left join SCM_PRODQTY_VIEW on PreSaleForecastDetail.sd_prodcode=SCM_PRODQTY_VIEW.prcode where sd_sfid='"
						+ store.get("sf_id") + "'");
		while (rs.next()) {
			String sql = "update PreSaleForecastDetail set sd_saleqty1_cust_user=(select saleqty1_cust from scm_prodqty_cust_view where custcode='"
					+ rs.getGeneralString("sd_custcode")
					+ "' "
					+ "and prodcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "'),sd_saleqty_avg_cust_user=(select saleqty_avg_cust from scm_prodqty_cust_view where custcode='"
					+ rs.getGeneralString("sd_custcode")
					+ "' "
					+ "and prodcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "'),sd_saleqty1_user=(select saleqty1 from scm_prodqty_view where prcode='"
					+ rs.getGeneralString("sd_prodcode")
					+ "') "
					+ ",sd_saleqty_avg_user=(select saleqty_avg from scm_prodqty_view where prcode='"
					+ rs.getGeneralString("sd_prodcode") + "') where sd_id='" + rs.getInt("sd_id") + "'";
			baseDao.execute(sql);
		}
	}

	/**
	 * 出货通知单：提交之前，折扣单价、折扣后单价金额的计算、根据是否报关缺省设置税率、默认抓取最近的客户型号
	 * 
	 * @param gstore
	 */
	public void SendNotify_commit_savediscount(Integer id) {
		Object sn_id = id;
		if (sn_id != null) {
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=0,snd_discountamount=round(snd_outqty*snd_sendprice-nvl(snd_discountamount2,0),2) where snd_snid=? and nvl(snd_discountamount2,0)>0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=round(snd_discountamount/snd_outqty,8) where snd_snid=? and nvl(snd_outqty,0)>0 and nvl(snd_discountprice,0)=0 and nvl(snd_discountamount,0)>0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountamount=round(snd_discountprice*snd_outqty,2) where snd_snid=? and nvl(snd_discountprice,0)>0 and nvl(snd_discountamount,0)=0",
					sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discount=round((snd_sendprice-snd_discountprice)/snd_sendprice,8) where snd_snid=? and nvl(snd_sendprice,0)>0",
					sn_id);
			baseDao.execute("update SendNotifyDetail set snd_discount=0 where snd_snid=? and nvl(snd_discountamount2,0)=0", sn_id);
			baseDao.execute(
					"update SendNotifyDetail set snd_discountprice=snd_sendprice,snd_discountamount=snd_sendprice * snd_outqty where snd_snid=? and nvl(snd_discountamount2,0)=0 and nvl(snd_discountprice,0)=0 and nvl(snd_discountamount,0)=0 ",
					sn_id);
			// 根据是否报关默认税率
			baseDao.execute("update SendNotifyDetail set snd_taxrate=0 where snd_snid=? and abs(nvl(snd_bonded,0))=1", sn_id);
			baseDao.execute("update SendNotifyDetail set snd_taxrate=16 where snd_snid=? and abs(nvl(snd_bonded,0))=0", sn_id);
			// 默认抓取最近的客户型号
			SqlRowList rs = baseDao.queryForRowSet("SELECT * from SendNotifyDetail WHERE snd_snid=? and nvl(snd_custprodcode,' ')=' ' ",
					sn_id);
			String sndcustprodcode = null;
			while (rs.next()) {
				sndcustprodcode = baseDao.queryForObject(
						"Select snd_custprodcode from (SELECT snd_custprodcode FROM SendNotify,SendNotifyDetail where sn_id=snd_snid and snd_prodcode='"
								+ rs.getObject("snd_prodcode")
								+ "' and nvl(snd_custprodcode,' ')<>' ' order by sn_date desc) where rownum<2", String.class);
				if (sndcustprodcode != null) {
					baseDao.execute("update SendNotifyDetail set snd_custprodcode=? where snd_snid=? and snd_pdno=?", sndcustprodcode,
							sn_id, rs.getObject("snd_pdno"));
				}
			}
		}
	}

	/**
	 * 出货单、销售退货单：过账之前，限制如果没有产生发票箱单不允许过账
	 * 
	 * @param gstore
	 */
	public void prodInOut_post_invoiceCheck(Integer id) {
		int count = baseDao
				.getCount("select count(1) from prodinout where nvl(pi_packingcode,' ')=' ' and nvl(pi_invoicecode,' ')=' ' and pi_id="
						+ id);
		if (count > 0) {
			BaseUtil.showError("没有生成发票箱单，不允许进行过账操作！");
		}
	}

	/**
	 * 新物料申请审批流中保存物料编码时校验是否重复
	 * 
	 * @param store
	 */
	public void preproduct_processupdate_check(HashMap<Object, Object> store, String language, Employee employee) {
		if (store.get("pre_code") != null && store.get("pre_code") != "" && store.get("pre_id") != null && store.get("pre_id") != "")
			preProductService.checkProdCode(store.get("pre_id"), store.get("pre_code"));
	}

	/**
	 * 捷达通 采购验收单，出货单提交反提交审核反审核过账时更新货物状态
	 */
	public void prodInOut_updateProductStatus_Capres(Integer pi_id) {
		Object pi[] = baseDao.getFieldsDataByCondition("PRODINOUT", "pi_class,pi_fw2_user,pi_invostatuscode,pi_statuscode", "pi_id="
				+ pi_id);
		if (pi[0].equals("采购验收单")) {
			if ("POSTED".equals(pi[3])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='已入库' where pi_id=" + pi_id);
			} else {
				if ("COMMITED".equals(pi[2])) {
					if ("供应商送货".equals(pi[1])) {
						baseDao.execute("update PRODINOUT set pi_fw10_user='待收货' where pi_id=" + pi_id);
					} else {
						baseDao.execute("update PRODINOUT set pi_fw10_user='待提货' where pi_id=" + pi_id);
					}
				} else if ("AUDITED".equals(pi[2])) {
					if ("委托方提货".equals(pi[1])) {
						baseDao.execute("update PRODINOUT set pi_fw10_user='提货中' where pi_id=" + pi_id);
					}
				} else if ("ENTERING".equals(pi[2])) {
					baseDao.execute("update PRODINOUT set pi_fw10_user='' where pi_id=" + pi_id);
				}
			}
		} else if ("出货单".equals(pi[0])) {
			if ("POSTED".equals(pi[3])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='已出库' where pi_id=" + pi_id);
			} else {
				if ("COMMITED".equals(pi[2])) {
					baseDao.execute("update PRODINOUT set pi_fw10_user='待拣货' where pi_id=" + pi_id);
				} else if ("AUDITED".equals(pi[2])) {
					baseDao.execute("update PRODINOUT set pi_fw10_user='已拣货' where pi_id=" + pi_id);
				} else if ("ENTERING".equals(pi[2])) {
					baseDao.execute("update PRODINOUT set pi_fw10_user='' where pi_id=" + pi_id);
				}
			}
		}
	}

	/**
	 * 捷达通 出货单生成其它应收单时更新货物状态
	 */
	public void prodInOut_updateProductStatus_TurnOtherBill(Integer id) {
		Object pi[] = baseDao.getFieldsDataByCondition("PRODINOUT", "pi_class,pi_fw2_user,pi_inoutno", "pi_id=" + id);
		if ("出货单".equals(pi[0])) {
			if ("出车派送".equals(pi[1])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='已送达' where pi_id=" + id);
			} else if ("客户自提".equals(pi[1])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='已自提' where pi_id=" + id);
			} else if ("物流快递".equals(pi[1])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='已快递' where pi_id=" + id);
			}
		}
	}

	/**
	 * 捷达通 出货单打印时更新货物状态
	 */
	public void prodInOut_updateProductStatus_Print(Integer pi_id) {
		Object pi[] = baseDao.getFieldsDataByCondition("PRODINOUT", "pi_class,pi_printstatuscode", "pi_id=" + pi_id);
		if ("出货单".equals(pi[0])) {
			if ("PRINTED".equals(pi[1])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='拣货中' where pi_id=" + pi_id);
			}
		}
	}

	/**
	 * 捷达通 采购验收单，出货单反过账时更新货物状态
	 */
	public void prodInOut_updateProductStatus_ResPost(Integer pi_id) {
		Object pi[] = baseDao.getFieldsDataByCondition("PRODINOUT", "pi_class,pi_fw2_user,pi_printstatuscode", "pi_id=" + pi_id);
		if ("采购验收单".equals(pi[0])) {
			if ("供应商送货".equals(pi[1])) {
				baseDao.execute("update PRODINOUT set pi_fw10_user='待收货' where pi_id=" + pi_id);
			} else {
				baseDao.execute("update PRODINOUT set pi_fw10_user='待提货' where pi_id=" + pi_id);
			}
		} else if ("出货单".equals(pi[0])) {
			baseDao.execute("update PRODINOUT set pi_fw10_user='已拣货' where pi_id=" + pi_id);
		}
	}

	/**
	 * 新物料申请审批流中保存物料编码时校验长度
	 * 
	 * @param store
	 */
	public void checkCodeLength(HashMap<Object, Object> store, String language, Employee employee) {
		Object pre_code = store.get("pre_code");
		if (pre_code != null && !"".equals(pre_code)) {
			Object pk_codelength = baseDao.getFieldDataByCondition("ProductKind", "pk_codelength",
					"pk_name=(select pre_kind from preProduct where pre_id=" + store.get("pre_id") + ") and pk_level=1");
			int length = Integer.parseInt(String.valueOf(pk_codelength));
			if (length != 0) {
				if (pre_code.toString().getBytes().length != length) {
					BaseUtil.showError("物料编码长度不符合大类中的长度规则设置，不能保存!");
				}
			}
		}
	}

	/**
	 * 富为捷达通
	 * 
	 * @author wsy
	 */
	public void prodInOut_checkin_CountFare(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String emp = SystemSession.getUser().getEm_name();
		baseDao.callProcedure("PI_CountFare", new Object[] { store.get("pi_id"), store.get("pi_class"), emp });
	}

	/**
	 * 富为捷达通 出货单出仓标签打印 将明细按物料分组分析第几箱插入临时表PRODINOUT_SALE_TEMP中
	 * 
	 * @author wsy
	 */
	public void prodInOut_print_split(Integer pi_id) {
		baseDao.execute("delete PRODINOUT_SALE_TEMP where piid=" + pi_id);
		baseDao.execute("INSERT INTO PRODINOUT_SALE_TEMP( PIID,INOUTNO,VENDPRODCODE,OUTQTY,BOXSUM,BRAND,MADEIN,XIANGMAI,BOXNUM) "
				+ "SELECT A.* , level BOXSUM  FROM (select max(pd_piid) PIID,max(pd_inoutno) INOUTNO,max(PR_VENDPRODCODE) VENDPRODCODE,"
				+ "sum(nvl(pd_outqty,0)) OUTQTY,sum(nvl(pd_cartons,0)) BOXSUM,max(PR_BRAND) BRAND,max(pr_madein) MADEIN ,max(pi_xinagmai_user) XIANGMAI"
				+ " from prodiodetail left join product on pd_prodcode=pr_code left join prodinout on pd_piid=pi_id where pd_piid=" + pi_id
				+ " group by pd_prodcode) A"
				+ "  CONNECT BY VENDPRODCODE = PRIOR VENDPRODCODE AND LEVEL <= BOXSUM AND PRIOR DBMS_RANDOM.VALUE IS NOT NULL");
	}

	/**
	 * 华商龙 出货单：收款方式为CBD，预收余额-应收余额小于当前发货金额，不允许进行当前操作
	 */
	public void prodInOut_commit_leftAmountCheck(Integer id) {
		int count = baseDao.getCount("select count(1) from prodiodetail left join sale on pd_ordercode=sa_code where pd_piid=" + id
				+ " and sa_kind='样品订单'");
		if (count == 0) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select pi_currency,pi_cardcode,pi_total,pi_paymentcode from prodinout left join payments on pi_paymentcode=pa_code and pa_class='收款方式' where pi_id=? and nvl(pa_monthadd,0)=0 and nvl(pa_dayadd,0)=0 and nvl(pa_beginby,0)=0",
							id);
			if (rs.next()) {
				double pi = rs.getGeneralDouble("pi_total");
				Double ca = baseDao.getFieldValue("custar", "nvl(CA_PREPAYAMOUNT,0)", "CA_CUSTCODE='" + rs.getGeneralString("pi_cardcode")
						+ "' and CA_CURRENCY='" + rs.getGeneralString("pi_currency") + "'", Double.class);
				double ab = baseDao.getSummaryByField("arbill", "nvl(ab_aramount,0)-nvl(ab_payamount,0)",
						"ab_custcode='" + rs.getGeneralString("pi_cardcode") + "' and ab_currency='" + rs.getGeneralString("pi_currency")
								+ "' and ab_paymentcode='" + rs.getGeneralString("pi_paymentcode") + "'");
				if (ca - ab + 1 < pi) {
					BaseUtil.showError("收款方式为CBD，预收余额-应收余额[" + (ca - ab + 1) + "]小于当前发货金额[" + pi + "]，不允许进行当前操作！");
				}
			}
		}
	}

	/**
	 * 反馈编号2017030371 明细行的退单单价为0时才允许进入无值仓
	 */
	public void salereturn_commit_before_checksendprice(Integer id) {
		String detno = "wm_concat(pd_pdno)";
		String sql = "select " + detno + " from ProdIODetail left join warehouse on pd_whcode=wh_code where pd_piid=" + id
				+ " and pd_sendprice>0 and wh_nocost='-1'";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			if (rs.getString(detno) != null) {
				BaseUtil.showError("行号：" + rs.getString(detno) + " 的单价不为0不能入无值仓");
			}
		}
	}

	/**
	 * 华商龙 出货通知单：收款方式为CBD，预收余额-应收余额小于当前发货金额，不允许进行当前操作
	 */
	public void sendnotify_commit_leftAmountCheck(Integer id) {
		int count = baseDao.getCount("select count(1) from sendnotifydetail left join sale on snd_ordercode=sa_code where snd_snid=" + id
				+ " and sa_kind='样品订单'");
		if (count == 0) {
			baseDao.execute("update sendnotify set sn_total=round(nvl((select sum(round(snd_outqty*snd_sendprice,2)) from sendnotifydetail where sn_id=snd_snid),0),2) where sn_id="
					+ id);
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select sn_currency,sn_custcode,sn_total,sn_paymentscode from sendnotify left join payments on sn_paymentscode=pa_code and pa_class='收款方式' where sn_id=? and nvl(pa_monthadd,0)=0 and nvl(pa_dayadd,0)=0 and nvl(pa_beginby,0)=0",
							id);
			if (rs.next()) {
				double pi = rs.getGeneralDouble("sn_total");
				Double ca = baseDao.getFieldValue("custar", "nvl(CA_PREPAYAMOUNT,0)", "CA_CUSTCODE='" + rs.getGeneralString("sn_custcode")
						+ "' and CA_CURRENCY='" + rs.getGeneralString("sn_currency") + "'", Double.class);
				double ab = baseDao.getSummaryByField("arbill", "nvl(ab_aramount,0)-nvl(ab_payamount,0)",
						"ab_custcode='" + rs.getGeneralString("sn_custcode") + "' and ab_currency='" + rs.getGeneralString("sn_currency")
								+ "' and ab_paymentcode='" + rs.getGeneralString("sn_paymentscode") + "'");
				if (ca - ab + 1 < pi) {
					BaseUtil.showError("收款方式为CBD，预收余额-应收余额[" + (ca - ab + 1) + "]小于当前发货金额[" + pi + "]，不允许进行当前操作！");
				}
			}
		}
	}

	/**
	 * 问题反馈编号：2017030143
	 * 采购单提交增加业务逻辑配置:提交时检查采购明细物料资料中的品牌+(主表)供应商，在品牌采购申请表中是否存在【已审核】的记录，如果不存在限制提交
	 * 
	 * @author wsy
	 * 
	 */
	public void purchase_commit_before_checkbrandvendor(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pu_vendcode,pr_brand,pd_detno from purchasedetail left join Product on pd_prodcode = pr_code left join purchase on pd_puid=pu_id where pd_puid=? and nvl(pr_brand,' ')<>' '",
						id);
		while (rs.next()) {
			boolean bool = baseDao.checkIf("BrandVendor",
					"bv_brand='" + rs.getString("pr_brand") + "' and bv_vendorcode='" + rs.getString("pu_vendcode")
							+ "' and bv_status='已审核'");
			if (!bool) {
				BaseUtil.showError("序号:" + rs.getInt("pd_detno") + "，品牌:'" + rs.getString("pr_brand") + "'  供应商：'"
						+ (rs.getString("pu_vendcode") == null ? "无" : rs.getString("pu_vendcode")) + "'在品牌采购申请表中不存在或状态不是【已审核】，不能提交!");
			}
		}
	}

	/**
	 * 报价单：保存、更新时，计算折扣比例
	 * 
	 * @2017030038 2017-03-15
	 */
	public void quotation_save_discount(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object rate = baseDao.getFieldDataByCondition("currencySmonth", "cm_crrate", "cm_crname='" + store.get("qu_currency")
				+ "' and cm_yearmonth=to_char(sysdate,'yyyyMM')");
		Object quid = store.get("qu_id");
		if (rate == null)
			rate = 0;
		baseDao.execute("update QuotationDetail set qd_discountrate_user=case when nvl(qd_salepricehkd_user,0)=0 then 0 "
				+ "else round((qd_price*?)/(nvl(qd_salepricehkd_user,0)),4) end where qd_quid=?", rate, quid);
	}

	/**
	 * 客户资料-费用率变更后上传到平台
	 */
	public void customerChange_audit_after_sendB2B(Integer cl_id) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(baseDao.getFieldDataByCondition(
				"Customer left join commonchangelog on cu_id=cl_keyvalue", "cl_data", "cl_id=" + cl_id).toString());
		Object cu_uu = baseDao.getFieldDataByCondition("customer", "cu_uu", "cu_code='" + formdata.get("cu_code") + "'");
		// 判断是否变更费用率
		if (cu_uu != null && formdata.get("cu_chargerate_user") != null && formdata.get("cu_chargerate_user-new") != null
				&& !formdata.get("cu_chargerate_user").toString().equals(formdata.get("cu_chargerate_user-new").toString())) {
			Master master = SystemSession.getUser().getCurrentMaster();
			VendorRate rate = new VendorRate();
			Object venduu = baseDao.getFieldDataByCondition("Enterprise", "en_uu", "1=1");
			rate.setCustuu(Long.parseLong(cu_uu.toString()));
			rate.setVenduu(Long.parseLong(venduu.toString()));
			rate.setRate(Double.parseDouble(formdata.get("cu_chargerate_user-new").toString()));
			List<VendorRate> rates = new ArrayList<VendorRate>();
			rates.add(rate);
			if (!CollectionUtils.isEmpty(rates)) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("data", FlexJsonUtil.toJsonArrayDeep(rates));
				try {
					HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/vendor/rate?access_id=" + master.getMa_uu(), params, true,
							master.getMa_accesssecret());
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 出货单：根据费用比例表，更新明细行比例
	 * 
	 * @author madan 2017-04-10 11:37:05
	 */
	public void prodinout_commit_updateDiscount(Integer pi_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_id,pr_id,pd_sendprice,cu_id,pi_date from prodinout lefft join prodiodetail on pi_id=pd_piid left join customer on pi_cardcode=cu_code left join product on pd_prodcode=pr_code where pi_id=?",
						pi_id);
		while (rs.next()) {
			baseDao.updateByCondition(
					"prodiodetail",
					"pd_discount=(select pdrd_rate from (select pdrd_rate from ProductRateDetail left join ProductRate on pdrd_pdrid=pdr_id where pdrd_prodid="
							+ rs.getGeneralInt("pr_id")
							+ " and pdr_custid='"
							+ rs.getGeneralInt("cu_id")
							+ "' and pdrd_startdate - 1 < to_date('"
							+ rs.getString("pi_date")
							+ "','yyyy-mm-dd hh24:mi:ss') and pdrd_enddate+1>to_date('"
							+ rs.getString("pi_date")
							+ "','yyyy-mm-dd hh24:mi:ss') "
							+ " and pdrd_saleprice="
							+ rs.getGeneralDouble("pd_sendprice")
							+ "and pdr_statuscode='AUDITED' and pdrd_statuscode='VALID' order by pdr_auditdate desc,pdrd_id desc) where rownum=1)",
					"pd_id=" + rs.getGeneralInt("pd_id"));
		}
	}

	/**
	 * 采购单：提交更新明细的供应商比例
	 * 
	 * @author dingyl 2017-04-24
	 */
	public void purchase_commit_updateVondorrate(Integer pu_id) {
		List<Object[]> detail = baseDao.getFieldsDatasByCondition("purchasedetail left join purchase on pd_puid=pu_id", new String[] {
				"pd_id", "pd_detno", "pd_prodcode", "pu_date" }, "pd_puid=" + pu_id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] os : detail) {
			String sql = "update PurchaseDetail  set pd_vendorrate=nvl((select vrd_rate from "
					+ "(select vrd_rate from VendorRatedet  left join VendorRate on vrd_vrid=vr_id where " + "(to_date('" + os[3]
					+ "','yyyy-mm-dd HH24:mi:ss') between vr_startdate and vr_enddate) and vrd_prcode='" + os[2]
					+ "' and vr_statuscode='AUDITED'  order by vr_id desc) where rownum<=1),0) where  pd_id=" + os[0];
			sqls.add(sql);
		}
		baseDao.execute(sqls);
	}

	/**
	 * 采购询价单：明细行有未报价的单据不允许提交
	 * 
	 * @author marzon 2017-04-28
	 */
	public void inquiry_commit_before_checkprice(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from inquiry left join inquirydetail on in_id=id_inid where in_id=" + id
				+ " and nvl(id_price,0)=0 and sysdate<=in_enddate");
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			sb.append("、" + rs.getInt("id_detno"));
		}
		String sp = sb.substring(1);
		if (sp != null) {
			BaseUtil.showError("报价截止日期未到，明细行还有未报价的物料明细行" + sp + "，不允许提交！");
		}
	}

	/**
	 * 采购单：请购转采购单自动更新币别和汇率为RMB和1
	 * 
	 * @author maz 2017-05-16
	 */
	public void purchase_turn_updateCurrency(String pucodes) {
		baseDao.updateByCondition("Purchase", "pu_currency='RMB',pu_rate='1'", "pu_code in (" + pucodes + ")");
	}

	/**
	 * 怡海能达采购验收单保存更新后更新采购单价
	 * 
	 * @author maz 2017-05-16
	 */
	public void prodinout_updatePrice(Integer pi_id) {
		// 保留六位小数
		String sql = "update PRODIODETAIL set pd_sendprice=(select trunc(nvl(pd_price,0)/pi_rate*nvl(pi_xishu_user,0),6)  from Prodinout  where pi_id="
				+ pi_id + "  and pi_xishu_user is not null ) where   pd_piid=" + pi_id + "";
		baseDao.execute(sql);
		baseDao.updateByCondition("PRODIODETAIL", "pd_ordertotal=trunc(nvl(pd_sendprice,0)*nvl(pd_outqty,0),2)", "pd_piid=" + pi_id + "");
	}

	/**
	 * @author wsy 反馈编号：2017050375 怡海能达：销售价格表中不存在有效价格，不允许提交
	 */
	public void saleforecast_before_commit(Integer id) {
		Object sf_currency = baseDao.getFieldDataByCondition("SaleForecast", "sf_currency", "sf_id=" + id);
		String sql = "select sd_prodcode,'" + sf_currency
				+ "',sd_custcode,sd_cost,sd_detno from SaleForecastDetail left join SaleForecast on sd_sfid=sf_id where sd_sfid=?";
		SqlRowList rs = baseDao.queryForRowSet(sql, id);
		while (rs.next()) {
			SqlRowList res = baseDao
					.queryForRowSet("select spd_price from (select spd_price from SalePriceDetail left join SalePrice on spd_spid=sp_id where spd_arcustcode='"
							+ rs.getString("sd_custcode")
							+ "' and spd_prodcode='"
							+ rs.getString("sd_prodcode")
							+ "' and spd_currency='"
							+ sf_currency
							+ "' and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' AND sp_status='已审核' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2");
			if (res.next()) {
				if (Double.parseDouble(rs.getString("sd_cost")) != Double.parseDouble(res.getString("spd_price"))) {
					BaseUtil.showError("物料编号：" + rs.getString("sd_prodcode") + ",客户编号：" + rs.getString("sd_custcode") + ",币别："
							+ sf_currency + "在对应的销售价格表中不存在或者价格不相等！");
				}
			} else {
				BaseUtil.showError("明细行：" + rs.getString("sd_detno") + "单价在价格表中不是有效价格禁止提交");
			}
		}
	}

	/**
	 * 怡海能达深圳 采购入库更新时更新采购单价
	 * 
	 * @maz 2017-05-18
	 */
	public void prodinout_save_updateorderprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object cu_rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='RMB'");
		double rate = cu_rate == null ? 0.17 : Double.parseDouble(cu_rate.toString()) / 100;
		String sql = "update PRODIODETAIL a set pd_orderprice=(select trunc(((nvl(PD_ORDERPRICEY_USER,0)*"
				+ "nvl(pi_hghl_user,0)*(1+nvl(pr_customprice,0)))*(1+" + rate + "))*(1+nvl(pi_dlf_user,0)),6) from ProdInOut "
				+ "left join prodiodetail b on pi_id=b.pd_piid left join product on pd_prodcode=pr_code where "
				+ "a.pd_id=b.pd_id) where nvl(PD_ORDERPRICEY_USER,0)<>0 and  pd_piid=" + store.get("pi_id");
		baseDao.execute(sql);
	}

	/**
	 * @author wsy 反馈编号：2017050581 特殊考勤申请：提交前判断考勤时间是否大于当前时间，是则不允许提交
	 */
	public void speattend_before_commit(Integer id) {
		boolean bool = baseDao
				.checkByCondition(
						"SpeAttendance",
						"to_char(sa_appdate,'yyyy-mm-dd')>to_char(sysdate,'yyyy-mm-dd') and to_char(sa_enddate,'yyyy-mm-dd')>to_char(sysdate,'yyyy-mm-dd') and sa_id="
								+ id);
		if (!bool) {
			BaseUtil.showError("特殊考勤日期不能超过当前日期!");
		}
	}

	/**
	 * 反馈编号：2017050740 请购单提交，增加业务逻辑配置“风险请购自动产生新单据”
	 * 
	 * @author wsy
	 */
	public void application_risk_AutoGenerate(Integer id) {
		Object ap_kind = baseDao.getFieldDataByCondition("Application", "ap_kind", "ap_id=" + id);
		List<Integer> ids = new ArrayList<Integer>();
		if (!"风险请购".equals(ap_kind) && !"调货请购".equals(ap_kind)) {
			// 查不到库存周转率>标准库存周转率，即count==0 则明细全为风险请购
			int count = baseDao
					.getCount("select count(*) from APPLICATIONDETAIL a left join product b on ad_prodcode=pr_code where nvl(PR_KCZZL,0)>=nvl(PR_BZKCZZL,0) and a.ad_apid="
							+ id);
			if (count == 0) {
				baseDao.execute("update application set ap_kind='风险请购' where ap_id=" + id);
			} else {
				SqlRowList rs = baseDao.queryForRowSet("select * from ApplicationDetail  where ad_apid=" + id);
				while (rs.next()) {
					String pr_code = rs.getString("ad_prodcode");
					if (pr_code != null && !"".equals(pr_code)) {// PR_KCZZL平均周转率
																	// pr_bzkczzl基准值
						boolean bool = baseDao.checkIf("product", "nvl(PR_KCZZL,0)<nvl(PR_BZKCZZL,0) and pr_code='" + pr_code + "'");
						if (bool) {
							ids.add(rs.getInt("ad_id"));
						}
						/*
						 * Object PR_JKGSL =
						 * baseDao.getFieldDataByCondition("product",
						 * "PR_JKGSL", "pr_code='" + pr_code + "'"); if
						 * (Double.parseDouble((PR_JKGSL == null ? "0" :
						 * PR_JKGSL).toString()) > 0) {
						 * ids.add(rs.getInt("ad_id")); }
						 */
					}
				}
			}
		}
		if (ids.size() > 0) {
			int ap_id = baseDao.getSeqId("APPLICATION_SEQ");
			int detno = 1;
			List<String> list = new ArrayList<String>();
			String ap_code = baseDao.sGetMaxNumber("Application", 2);
			String sql = "insert into application(AP_PRJNAME,AP_MRPREMARK,AP_XIJ,AP_ID,AP_CODE,AP_SOURCEID,AP_SOURCE,AP_REFCODE,AP_DATE,AP_DELIVERY,AP_COSTCENTER,AP_VENDID,AP_VENDCODE,AP_TOTAL,AP_PAYMENTS,AP_CURRENCY,AP_RATE,AP_PURPOSE,AP_REMARK,AP_STATUSCODE,AP_STATUS,AP_RECORDERID,AP_RECORDDATE,AP_DEPARTMENT,AP_PLEAMANID,AP_PLEAMANNAME,AP_COP,AP_OLDSTATUSCODE,AP_OLDSTATUS,AP_AUDITMAN,AP_AUDITDATE,AP_DATASOURCE,AP_TEL,AP_ACCEPTDEPARTMENT,FIN_CODE,AP_DEPARTCODE,AP_VENDNAME,AP_KIND,AP_DEPARTNAME,AP_CONTACT,AP_RECORDER,AP_TYPE,AP_TURNSTATUSCODE,AP_TURNSTATUS,AP_PRINTSTATUSCODE,AP_PRINTSTATUS,AP_BUYERCODE,AP_BUYERNAME,AP_PARENTORNAME,AP_BONDED,AP_SYNC,AP_PRJCODE)"
					+ "select AP_PRJNAME,AP_MRPREMARK,AP_XIJ,"
					+ ap_id
					+ ",'"
					+ ap_code
					+ "',AP_SOURCEID,AP_SOURCE,AP_REFCODE,sysdate,sysdate,AP_COSTCENTER,AP_VENDID,AP_VENDCODE,AP_TOTAL,AP_PAYMENTS,AP_CURRENCY,AP_RATE,AP_PURPOSE,AP_REMARK,'ENTERING','在录入',AP_RECORDERID,AP_RECORDDATE,AP_DEPARTMENT,AP_PLEAMANID,AP_PLEAMANNAME,AP_COP,AP_OLDSTATUSCODE,AP_OLDSTATUS,AP_AUDITMAN,AP_AUDITDATE,AP_DATASOURCE,AP_TEL,AP_ACCEPTDEPARTMENT,FIN_CODE,AP_DEPARTCODE,AP_VENDNAME,'风险请购',AP_DEPARTNAME,AP_CONTACT,AP_RECORDER,AP_TYPE,AP_TURNSTATUSCODE,AP_TURNSTATUS,'UNPRINT','未打印',AP_BUYERCODE,AP_BUYERNAME,AP_PARENTORNAME,AP_BONDED,AP_SYNC,AP_PRJCODE from application where ap_id="
					+ id + "";
			baseDao.execute(sql);
			for (Integer ad_id : ids) {
				int newad_id = baseDao.getSeqId("APPLICATIONDETAIL_SEQ");
				String s = "insert into applicationdetail(AD_DETNO,AD_PRODID,AD_PRODCODE,AD_QTY,AD_PRICE,AD_TOTAL,AD_DELIVERY,AD_WAREHOUSEID,AD_BARCODE,AD_VENDID,AD_SOURCE,AD_SOURCEID,AD_REMARK,AD_STATUSCODE,AD_STATUS,AD_JYPUDATE,AD_YT,AD_VENDOR,AD_SOURCECODE,AD_VENDNAME,AD_MRPCODE,AD_MDID,AD_YQTY,AD_ID,AD_APID,AD_CURRENCY,AD_TQTY,AD_PHRASE,AD_MINPACK,AD_MINORDER,AD_MRPQTY,AD_USE,AD_LEADTIME,AD_CODE,AD_BONDED,AD_IFREP,AD_MRPSTATUSCODE,AD_MRPSTATUS,AD_PLANCODE,AD_PLANDETNO,AD_B,AD_C,AD_D,AD_F,AD_IFVENDRATE,AD_FACTORY,AD_CUSTCODE,AD_CUSTNAME,AD_SELLERCODE,AD_SELLER,AD_PRJCODE,AD_PRJNAME)"
						+ "select "
						+ detno
						+ ",AD_PRODID,AD_PRODCODE,AD_QTY,AD_PRICE,AD_TOTAL,AD_DELIVERY,AD_WAREHOUSEID,AD_BARCODE,AD_VENDID,AD_SOURCE,AD_SOURCEID,AD_REMARK,'ENTERING','在录入',AD_JYPUDATE,AD_YT,AD_VENDOR,AD_SOURCECODE,AD_VENDNAME,AD_MRPCODE,AD_MDID,AD_YQTY,"
						+ newad_id
						+ ","
						+ ap_id
						+ ",AD_CURRENCY,AD_TQTY,AD_PHRASE,AD_MINPACK,AD_MINORDER,AD_MRPQTY,AD_USE,AD_LEADTIME,'"
						+ ap_code
						+ "',AD_BONDED,AD_IFREP,AD_MRPSTATUSCODE,AD_MRPSTATUS,AD_PLANCODE,AD_PLANDETNO,AD_B,AD_C,AD_D,AD_F,AD_IFVENDRATE,AD_FACTORY,AD_CUSTCODE,AD_CUSTNAME,AD_SELLERCODE,AD_SELLER,AD_PRJCODE,AD_PRJNAME from applicationdetail where ad_id="
						+ ad_id + "";
				baseDao.execute(s);
				list.add("delete from APPLICATIONDETAIL where ad_id=" + ad_id);
				detno++;
			}
			baseDao.execute(list);
			BaseUtil.appendError("当前请购单中存在风险请购物料，系统自动将这部分物料产生到新的请购单"
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?formCondition=ap_codeIS" + ap_code
					+ "&gridCondition=ad_codeIS" + ap_code + "')\">" + ap_code + "</a>&nbsp;" + "中，请手工提交这张请购单");
		}
	}

	/**
	 * 请购单：按最新有效定价自动获取供应商
	 * 
	 * @author dingyl
	 */
	public void application_getvendorBydate(Integer id) {
		String sqlstr = null;
		sqlstr = "MERGE INTO ApplicationDetail USING (select * from (SELECT ad_id,ad_prodcode,ad_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate"
				+ ",rank() over (PARTITION BY ad_prodcode order by pp_indate desc,ppd_id desc) mm FROM applicationdetail "
				+ " left join PurchasePriceDetail on ad_prodcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
				+ " left join  Currencys on ppd_currency=cr_name where ad_apid="
				+ id
				+ " and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
				+ " and pp_kind='采购' and ppd_lapqty<=ad_qty and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
				+ " on (src.ad_id=applicationdetail.ad_id) when matched then update set ad_ppdid=src.ppd_id,ad_vendor=src.ppd_vendcode,ad_vendname=src.ppd_vendname,ad_currency=src.ppd_currency,ad_vendid=src.ppd_vendid,ad_purcprice=src.ppd_price,ad_rate=src.ppd_rate "
				+ " WHERE ad_apid=" + id + " and NVL(ad_yqty,0)=0 and NVL(ad_vendor,' ')=' '";
		baseDao.execute(sqlstr);
		// 获取替代料供应商
		sqlstr = "MERGE INTO MrpReplace USING (select * from (SELECT mr_id,mr_repcode,mr_vendor,ppd_id,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
				+ ",rank() over (PARTITION BY mr_repcode order by pp_indate desc,ppd_id desc) mm FROM applicationdetail left join mrpreplace on ad_mdid=mr_mdid "
				+ " left join PurchasePriceDetail on mr_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid "
				+ " left join Currencys on ppd_currency=cr_name where ad_apid="
				+ id
				+ " and mr_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
				+ " and pp_kind='采购' and ppd_lapqty<=ad_qty and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
				+ " on (src.mr_id=MrpReplace.mr_id) when matched then update set mr_ppdid=src.ppd_id,mr_vendor=src.ppd_vendcode,mr_vendname=src.ppd_vendname,mr_currency=src.ppd_currency,mr_veid=src.ppd_vendid "
				+ " WHERE mr_mdid in (select ad_mdid from applicationdetail where ad_apid="
				+ id
				+ " and ad_mdid>0) and NVL(mr_vendor,' ')=' '";
		baseDao.execute(sqlstr);

		sqlstr = "MERGE INTO ApplicationReplace USING (select * from (SELECT ar_id,ar_repcode,ar_vendor,ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid"
				+ ",rank() over (PARTITION BY ar_repcode order by pp_indate desc,ppd_id desc) mm FROM ApplicationReplace left join applicationdetail on ar_mdid=ad_mdid "
				+ " left join PurchasePriceDetail on ar_repcode=ppd_prodcode left join PurchasePrice on pp_id=ppd_ppid"
				+ " left join  Currencys on ppd_currency=cr_name where ad_apid="
				+ id
				+ " and ar_id>0 and pp_statuscode='AUDITED' and ppd_statuscode='VALID' "
				+ " and pp_kind='采购' and ppd_lapqty<=ad_qty and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1> sysdate) where mm=1)src "
				+ " on (src.ar_id=ApplicationReplace.ar_id) when matched then update set ar_vendor=src.ppd_vendcode,ar_vendname=src.ppd_vendname,ar_currency=src.ppd_currency,ar_veid=src.ppd_vendid "
				+ " WHERE ar_mdid in (select ad_mdid from applicationdetail where ad_apid="
				+ id
				+ " and ad_mdid>0) and nvl(ar_vendor,' ')=' '";
		baseDao.execute(sqlstr);
		sqlstr = "update ApplicationDetail set ad_barcode=(case when NVL(ad_vendor,' ')=' ' then '采购开发' else '采购员' end) WHERE ad_apid="
				+ id + " and NVL(ad_yqty,0)=0";
		baseDao.execute(sqlstr);
	}

	/**
	 * 物料禁用单:审核前判断库存数量 maz 2017060900
	 */
	public void changeWLJY_audit_checkonhand(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select csd_detno from changestatusdetail left join Productonhand on csd_prodcode=po_prodcode left join Product on csd_prodcode=pr_code where po_onhand>0 and csd_csid="
						+ id);
		if (rs.next()) {
			BaseUtil.showError("行号:" + rs.getInt("csd_detno") + "物料库存大于0,不允许审核");
		}
	}

	/**
	 * @author wsy 采购验收单 费用明细删除后更新主表总金额
	 * @param id
	 */
	public void updateChargeamount(Integer id) {
		Object pi_id = baseDao.getFieldDataByCondition("ProdChargeDetail", "pd_piid", "pd_id=" + id);
		baseDao.execute("update prodinout set pi_chargeamount=nvl((select sum(round(nvl(pd_doubleamount,0),2)) from ProdChargeDetail where pd_piid=pi_id and pd_id<>"
				+ id + "),0) where pi_id=" + pi_id);
	}

	/*
	 * maz 驳回订单后，勾选参数的代采订单不允许提交 2017080129
	 */
	public void back_Prevent_Commit(Integer id) {
		Object[] sa_kind = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_kind", "sa_backstatus" }, "sa_id=" + id);
		if (sa_kind[0] != null && sa_kind[1] != null && "代采订单".equals(sa_kind[0]) && "已驳回待上传".equals(sa_kind[1])) {
			BaseUtil.showError("驳回的代采订单不允许提交");
		}
	}

	/**
	 * @author dingyl 采购单明细拆分还分对应交期回复
	 */
	public void splitPurchaseReply(HashMap<Object, Object> store) {
		Object puid = store.get("pu_id");
		Object pucode = baseDao.getFieldDataByCondition("purchase", "pu_code", "pu_id=" + puid);
		Object basedetno = store.get("basedetno");
		Object newdetnos = store.get("newdetnos");
		baseDao.procedure("SP_SPLITPURCHASE_AFTER",
				new Object[] { pucode, basedetno, newdetnos.toString().substring(1, newdetnos.toString().length()),
						SystemSession.getUser().getEm_name() });

	}

	/*
	 * maz 维誉：请购单转采购单更新采购单
	 */
	public void purchase_turn_update(String pu_code) {
		baseDao.procedure("SP_PURCHASE_TURN_UPDATE", new Object[] { pu_code });
	}

	/**
	 * 加工验收单、加工验退单：明细删除后，还原加工委外单数据
	 */
	public void prodInOut_OtherExplist_deletedetail(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_orderdetno,pd_ordercode,pd_id,nvl(pd_inqty,0)-nvl(pd_outqty,0) pd_qty,md_id,md_maid from ProdIODetail,OtherExplistDetail where pd_id=? and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0 and pd_ordercode=md_code and pd_orderdetno=md_detno",
						id);
		if (rs.next()) {
			int mdid = rs.getGeneralInt("md_id");
			int maid = rs.getGeneralInt("md_maid");
			baseDao.updateByCondition("OtherExplistDetail", "md_yqty=nvl(md_yqty,0)-" + rs.getGeneralDouble("pd_qty"), "md_id=" + mdid
					+ " AND nvl(md_yqty,0)>0");
			baseDao.updateByCondition("OtherExplistDetail", "md_yqty=0", "md_id=" + mdid + " AND nvl(md_yqty,0)<0");
			otherExplistDao.updateStatus(mdid);
			baseDao.execute("UPDATE OtherExplistDetail SET md_total=round(md_price*md_qty,2),md_nettotal=round(md_qty*md_netprice,2) WHERE md_maid="
					+ maid);
		}
	}

	/**
	 * @author wuyx 下达采购单（保存/更新）时根据供应商+料号自动获取所有明细行供应商料号并更新到采购单明细的供应商料号
	 * @param store
	 * @param grid
	 */
	public void purchasedetail_save_after(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object puId = store.get("pu_id");
		Object puVendcode = store.get("pu_vendcode");
		List<Object> pdIdList = baseDao.getFieldDatasByCondition("purchase left join purchasedetail on pu_id = pd_puid", "pd_id",
				"pu_id = '" + puId + "' ");
		for (Object pdId : pdIdList) {
			Object pdProdcode = baseDao.getFieldDataByCondition("purchasedetail", "pd_prodcode", "pd_id ='" + pdId + "' ");
			// System.out.println("pdProdcode: "+pdProdcode+" ,puVendcode: "+puVendcode+" ,pdId="+pdId);
			baseDao.execute(" update purchasedetail set pd_prodvendcode =nvl((select PV_VENDPRODCODE  from ProductVendor "
					+ " where PV_PRODCODE=? and PV_VENDCODE=? and PV_VENDPRODCODE is not null and rownum = 1),'') where pd_id=?",
					pdProdcode, puVendcode, pdId);
		}
	}

	/**
	 * @author wsy 通达：采购验收单：保存修改前，物料资料转换率不为1，采购单位入库数量和入库数量不能相等
	 * @param store
	 * @param gstore
	 */
	public void prodinout_checkpurcqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		for (Map<Object, Object> map : grid) {
			Object pr_purcrate = baseDao.getFieldDataByCondition("product", "nvl(pr_purcrate,0)", "pr_code='" + map.get("pd_prodcode")
					+ "'");
			if (pr_purcrate != null && Double.valueOf(pr_purcrate.toString()) != 1) {
				double pd_inqty = map.get("pd_inqty") == null ? 0 : Double.parseDouble(map.get("pd_inqty").toString());
				double pd_purcinqty = map.get("pd_purcinqty") == null ? 0 : Double.parseDouble(map.get("pd_purcinqty").toString());
				if (pd_inqty > 0 && pd_inqty == pd_purcinqty) {
					BaseUtil.showError("物料资料转换率不为1，采购单位入库数量和入库数量数量不能相等。行号：" + map.get("pd_pdno"));
				}
			}
		}
	}

	/**
	 * //根据物料资料中参数配置：允许物料名称+规格+规格参数重复 检查存在名称、规格一样的物料。 反馈编号：2017120082
	 * 
	 * @author:lidy
	 * 
	 * @param id
	 */
	public void checkProdName(Integer id, String language, Employee employee) {
		checkProdName(id);
	}

	public void checkProdName(Integer id) {
		Object[] data = baseDao.getFieldsDataByCondition("commonchangelog", new String[] { "cl_data", "cl_keyfield", "cl_keyvalue",
				"cl_caller" }, "cl_id=" + id);
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(String.valueOf(data[0]));
		Map<Object, Object> updatemap = new HashMap<Object, Object>();
		String[] fields = null;
		SqlRowList sl = baseDao
				.queryForRowSet("select  wmsys.wm_concat(fd_field) from formdetail  left join form on fd_foid=fo_id where fo_caller='"
						+ data[3]
						+ "' and upper(nvl(fd_table,' '))<>'COMMONCHANGELOG' and nvl(fd_logictype,' ')<>'changeCodeField' and nvl(fd_logictype,' ')<>'changeKeyField'");
		if (sl.next()) {
			fields = sl.getString(1).split(",");
		}
		for (String field : fields) {
			updatemap.put(field, map.get(field + "-new"));
		}
		updatemap.put("pr_code", map.get("pr_code"));
		updatemap.put(data[1], data[2]);
		// 获取物料资料中的参数配置: 允许物料名称+规格+规格参数重复。 反馈编号：2017120082 @author:lidy
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		String code = baseDao.getFieldValue(
				"Product",
				"pr_code",
				"pr_code <> '" + StringUtil.nvl(updatemap.get("pr_code"), " ") + "' AND nvl(pr_detail,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_detail"), " ") + "' and nvl(pr_spec,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_spec"), " ") + "' and nvl(pr_speccs,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_speccs"), " ") + "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			}
		}
		// 判断名称规格是否存在重复
		code = baseDao.getFieldValue(
				"PreProduct",
				"pre_thisid",
				"nvl(pre_code,' ') <> '" + StringUtil.nvl(updatemap.get("pr_code"), " ") + "' AND nvl(pre_detail,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_detail"), " ") + "' and nvl(pre_spec,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_spec"), " ") + "' and nvl(pre_parameter,' ')='"
						+ StringUtil.nvl(updatemap.get("pr_speccs"), " ") + "'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			}
		}
	}

	public void checkProdinout(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object id = store.get("sc_id");
		String log = "该销售订单存在出货通知单和出货单，确认是否需要变更对应出货通知单和出货单";
		Object sc_withprodio = store.get("sc_withprodio");
		Object sc_withnotify = store.get("sc_withnotify");
		if (sc_withprodio != null && sc_withnotify != null) {
			// 变更出货单和变更通知单checkbox是否同时勾选
			boolean is = sc_withprodio.toString().equals("1") && sc_withnotify.toString().equals("1");
			if (!is) {
				SqlRowList rs = baseDao.queryForRowSet("select scd_sacode,scd_sddetno from SaleChangeDetail where scd_scid=" + id);
				while (rs.next()) {
					// 出货单和通知单至少存在一个 否则不提醒
					boolean bool1 = baseDao.checkByCondition("SendNotifyDetail ", "snd_ordercode='" + rs.getObject("scd_sacode")
							+ "' and snd_orderdetno=" + rs.getObject("scd_sddetno") + "");
					boolean bool2 = baseDao.checkByCondition("ProdIODetail left join prodinout on pd_piid=pi_id",
							"pd_ordercode='" + rs.getObject("scd_sacode") + "' and pd_orderdetno=" + rs.getObject("scd_sddetno")
									+ " and nvl(pi_status,' ')<>'已过账'");
					if (!bool1 || !bool2) {
						List<Map<String, Object>> g = baseDao.queryForList("select * from SaleChangeDetail where scd_scid=" + id);
						for (Map<String, Object> map : g) {
							// 单价 币别 税率 付款方式至少有一个不同 否则不提醒
							String price = map.get("scd_price") != null ? map.get("scd_price").toString() : "null";
							String newprice = map.get("scd_newprice") != null ? map.get("scd_newprice").toString() : "null";
							String currency = map.get("scd_currency") != null ? map.get("scd_currency").toString() : "null";
							String newcurrency = map.get("scd_newcurrency") != null ? map.get("scd_newcurrency").toString() : "null";
							String tax = map.get("scd_taxrate") != null ? map.get("scd_taxrate").toString() : "null";
							String newtax = map.get("scd_newtaxrate") != null ? map.get("scd_newtaxrate").toString() : "null";
							String pay = map.get("scd_payments") != null ? map.get("scd_payments").toString() : "null";
							String newpay = map.get("scd_newpayments") != null ? map.get("scd_newpayments").toString() : "null";
							if (!(price.equals(newprice) && currency.equals(newcurrency) && tax.equals(newtax) && pay.equals(newpay))) {
								BaseUtil.showErrorOnSuccess(log);
							}
						}
					}
				}

			}
		}
	}

	/**
	 * @author wsy 反馈编号：2017110433 生产领料单、生产补料单、委外领料单、委外补料单
	 *         提交、过账、打印增加限制：如果工单类型设置的允许发料仓库不为空，则必须发料仓库再设定仓库范围；
	 */
	public void check_prodinout_outwhcodes(Integer pi_id) {
		String log = null;
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet("select pd_ordercode,pd_whcode,pd_pdno from PRODIODETAIL where pd_piid=" + pi_id
				+ " order by pd_pdno asc");
		while (rs.next()) {
			String ma_code = rs.getString("pd_ordercode");
			String pd_whcode = rs.getString("pd_whcode");
			Object mk_outwhcodes = baseDao.getFieldDataByCondition("MakeKind left join Make on mk_name=ma_kind", "mk_outwhcodes",
					"ma_code='" + ma_code + "'");
			if (mk_outwhcodes != null && !mk_outwhcodes.equals("")) {
				mk_outwhcodes = "'" + mk_outwhcodes.toString().replaceAll("#", "','") + "'";
				boolean bool = baseDao.checkIf("dual", " '" + pd_whcode + "' in (" + mk_outwhcodes + ") ");
				if (!bool) {
					sb.append("、").append(rs.getInt("pd_pdno"));
				}
			}
		}
		if (sb != null && sb.length() > 0) {
			log = "工单类型设置的允许发料仓库不为空，发料仓库必须在设定仓库范围内！行号：";
			BaseUtil.showError(log + sb.toString().substring(1));
		}
	}

	/**
	 * @author wsy 单据编号：2017120330 采购单转收料单界面增加配置逻辑：转出的收料单自动审核
	 *         2、收料通知单转收料单界面加配置逻辑：转出的收料单自动审核 勾选后，转出的收料单自动审核并转IQC检验单
	 */
	public void purchaseToVerifyApply_AutoAudit(ArrayList<Map<Object, Object>> maps, String ids) {
		if (ids != null && !"".equals(ids)) {
			ids = ids.substring(1);
			baseDao.execute("update VerifyApplyDetail set vad_code=(select va_code from VerifyApply where vad_vaid=va_id) where vad_vaid in ("
					+ ids + ")" + " and not exists (select 1 from VerifyApply where vad_code=va_code)");
			baseDao.updateByCondition("VerifyApply", "va_status='已审核',va_statuscode='AUDITED',va_auditdate=sysdate,va_auditman='"
					+ SystemSession.getUser().getEm_name() + "'", "va_id in (" + ids + ")");
			baseDao.updateByCondition("VerifyApplyDetail", "VAD_STATUS='已审核',VAD_STATUSCODE='AUTITED'", "vad_vaid in (" + ids + ")");
			String va_id[] = ids.split(",");
			for (int i = 0; i < va_id.length; i++) {
				verifyApplyDao.updatesourceqty(Integer.parseInt(va_id[i]));
				List<Map<String, Object>> list = baseDao.queryForList("select vad_id \"vad_id\" from VerifyApplyDetail where vad_vaid="
						+ Integer.parseInt(va_id[i]) + "");
				String vad_ids = BaseUtil.parseGridStore2Str(list);
				verifyApplyService.detailTurnIQC(vad_ids);
			}
		}
	}

	public void qua_vastAudit_checkmsd(String ids) {
		if (ids != null) {
			SqlRowList rs = baseDao
					.queryForRowSet("select distinct pr_code code from product where pr_code in (select vad_prodcode from QUA_VerifyApplyDetail where  ve_id in ("
							+ ids + ") ) and pr_msdlevel is null");
			String log = "";
			while (rs.next()) {
				log = log + rs.getString("code") + ",";
			}
			if (log != "") {
				log = log.substring(0, log.length() - 1);
				BaseUtil.showError("物料" + log + "没有湿敏等级,不允许审核操作");
			}
		}
	}

	/**
	 * 天派电子 不良品入库单只有仓库A03-1才可以转MRB单
	 * 
	 * @param id
	 */
	public void DefectIn_turnMRB_Ware(Integer id) {
		Object wh = baseDao.getFieldDataByCondition("ProdIODetail", "pd_whcode", "pd_id=" + id);
		if (!"A03-1".equals(wh)) {
			BaseUtil.showError("只有仓库为A03-1的才允许转MRB单");
		}
	}

	/**
	 * @author guq 2018010243 限制出入库单的部门与明细仓库中的允许部门相同
	 */
	public void allow_prodinout_department(Integer id) {
		Object dept = baseDao.getFieldDataByCondition("prodinout ", "pi_departmentcode", "pi_id=" + id);
		if (dept != null) {
			// 明细行有2种拨出拨入仓库都需要满足条件
			String[] whcodes = new String[] { "pd_whcode", "pd_inwhcode" };
			for (String whcode : whcodes) {
				SqlRowList rowSet = baseDao.queryForRowSet("select wh_departmentcode,pd_pdno  from prodinout left join"
						+ " prodiodetail on pi_id = pd_piid left join warehouse on wh_code=" + whcode + " where pi_id=? and " + whcode
						+ " is not null order by pd_pdno", id);
				while (rowSet.next()) {
					Object depts = rowSet.getObject(1);
					if (depts != null) {
						boolean flag = true;
						String[] deptCode = depts.toString().split("#");
						for (String code : deptCode) {
							if (code.equals(dept)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							BaseUtil.showError("部门不在仓库允许出库的部门中,限制操作! 行号" + rowSet.getObject(2));
						}
					}
				}
			}
		}
	}

	/**
	 * 物料核价单提交前如果系统本位币是RMB，核价单币别是外币时税率必须为0 勾选后，提交时进行限制
	 */
	public void checkRateBycurrency(Integer pp_id) {
		String currency = baseDao.getDBSetting("sys", "defaultCurrency");
		if (currency != null && "RMB".equals(currency)) {
			String det = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ppd_detno) from purchasepricedetail where ppd_ppid=" + pp_id
							+ " and nvl(ppd_currency,' ')<>'RMB' and nvl(ppd_rate,0)>0", String.class);
			if (det != null && !"".equals(det)) {
				BaseUtil.showError("系统本位币是RMB，核价单币别是外币时税率必须为0，行号:" + det);
			}
		}
	}

	/**
	 * @author maz 采购单（保存/更新）时更新上次采购单价 2018010407 标准需求
	 * @param store
	 * @param grid
	 */
	public void purchase_save_update_preprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object id = store.get("pu_id");
		SqlRowList sqlRowList = baseDao.queryForRowSet("select * from purchasedetail left join purchase on pd_puid=pu_id where pd_puid="
				+ id + " and nvl(pu_ordertype,' ')<>'B2C'");
		SqlRowList sList = null;
		while (sqlRowList.next()) {
			String sql, sql2 = "";
			sql = "SELECT PurchaseDetail.pd_price*Purchase.pu_rate as pd_oriprice,nvl(ve_shortname,ve_name) ve_shortname,pd_code from Purchase,PurchaseDetail,Vendor WHERE pu_id=pd_puid and pu_vendcode=ve_code and pu_id<'"
					+ sqlRowList.getInt("pd_puid")
					+ "' and pd_prodcode='"
					+ sqlRowList.getString("pd_prodcode")
					+ "' and (pu_status='已审核' or pu_status='已收货' or pu_status='部分收货') and nvl(pd_mark,' ')<>'备品' order by pu_id desc ";
			sList = baseDao.queryForRowSet(sql);
			while (sList.next()) {
				if (sql2.equals("")) {
					sql2 = "UPDATE purchaseDetail SET pd_preprice=" + sList.getDouble("pd_oriprice") + ",pd_prevendor='"
							+ sList.getString("ve_shortname") + "',pd_precode='" + sList.getString("pd_code") + "' where pd_id='"
							+ sqlRowList.getString("pd_id") + "' ";
					baseDao.execute(sql2);
				}
			}
		}
	}

	/**
	 * @author maz 采购单：保存更新后根据市场价*折扣率更新采购单价 2018010407 珠海高凌非标准需求
	 * @param store
	 * @param grid
	 */
	public void purchase_save_update_userprice(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		for (Map<Object, Object> map : gstore) {
			if (StringUtil.hasText(map.get("pd_market_user")) && StringUtil.hasText(map.get("pd_deposit_user"))
					&& StringUtil.hasText(map.get("pd_id")) && !"0".equals(map.get("pd_id").toString())) {
				baseDao.execute("update purchasedetail set pd_price=(" + map.get("pd_market_user") + "*" + map.get("pd_deposit_user")
						+ ") where pd_id=" + map.get("pd_id"));
			} else if (StringUtil.hasText(map.get("pd_market_user")) && StringUtil.hasText(map.get("pd_deposit_user"))) {
				baseDao.execute("update purchasedetail set pd_price=(" + map.get("pd_market_user") + "*" + map.get("pd_deposit_user")
						+ ") where pd_puid=" + store.get("pu_id") + " and pd_detno=" + map.get("pd_detno"));
			}
		}
	}

	/**
	 * 信扬 销售发票提交后，将预收明细费用分摊到销售单价中 将预收金额和费用更新到预收账款明细中
	 */
	public void commit_after_fee(Integer id) {
		// 发票号
		Object[] objs = baseDao.getFieldsDataByCondition("invoice", new String[] { "in_code", "in_saleprice" }, "in_id=" + id + "");
		Double fee = baseDao.getSummaryByField("preinvoice left join invoice on pi_inid=in_id", "pi_fee", "pi_inid=" + id
				+ " and in_status in ('已提交','已审核')");
		baseDao.execute("update InvoiceDetail set id_xyfy_user=" + fee + " where id_inid=" + id + "");
		if (objs != null && (objs[1] == null || "".equals(objs[1].toString()))) {
			// 费用 fee
			baseDao.callProcedure("USER_SP_COUNTPRICE", new Object[] { id });
		}
		SqlRowList rs = baseDao.queryForRowSet("select pi_prcode,pi_prddetno from preinvoice where pi_inid=" + id
				+ " group by pi_prcode,pi_prddetno");
		while (rs.next()) {
			String pi_prcode = rs.getString("pi_prcode");// 预收单号
			int pi_prddetno = rs.getInt("pi_prddetno");// 预收明细序号
			Object[] obs = baseDao.getFieldsDataByCondition("preinvoice left join invoice on pi_inid=in_id", new String[] {
					"sum(pi_amount)", "sum(pi_fee)" }, "in_status in ('已提交','已审核') and pi_prcode='" + pi_prcode + "' and pi_prddetno="
					+ pi_prddetno + "");
			// double pi_amount = Double.parseDouble(obs[0].toString());//预收金额
			// double pi_fee = Double.parseDouble(obs[1].toString());//费用
			// 预收账款明细费用
			Object prd_fee = baseDao.getFieldDataByCondition("PreRecDetail", "prd_fee", "prd_code='" + pi_prcode + "' and prd_detno="
					+ pi_prddetno + "");
			prd_fee = prd_fee == null ? "0" : prd_fee;
			if (Double.parseDouble((obs[1] == null ? "0" : obs[1]).toString()) > Double.parseDouble(prd_fee.toString())) {
				BaseUtil.showError("预收明细总费用：" + obs[1] + "不能超过预收账款单：" + pi_prcode + " 费用：" + prd_fee + "");
			}
			// 销售发票号
			Object prd_sxinvoice_user = baseDao.getFieldDataByCondition("PreRecDetail left join PreRec on prd_prid=pr_id",
					"prd_sxinvoice_user", "pr_code='" + pi_prcode + "' and prd_detno=" + pi_prddetno + "");
			if (prd_sxinvoice_user != null && !"".equals(prd_sxinvoice_user)) {
				if (!prd_sxinvoice_user.toString().contains(objs[0].toString())) {
					prd_sxinvoice_user = prd_sxinvoice_user + "," + objs[0];
				}
			} else {
				prd_sxinvoice_user = objs[0];
			}
			baseDao.execute("update PreRecDetail set prd_xynkpxy_user=" + obs[1] + ",prd_openamount_user=" + obs[0]
					+ ",prd_sxinvoice_user='" + prd_sxinvoice_user + "' where prd_code='" + pi_prcode + "' and prd_detno=" + pi_prddetno
					+ "");
			baseDao.execute("update PreRecDetail set prd_wkpfy_user=prd_fee-prd_xynkpxy_user where prd_code='" + pi_prcode
					+ "' and prd_detno=" + pi_prddetno + "");
		}

	}

	/**
	 * 信扬 销售发票反提交后,重新计算已开票金额与费用,销售发票号
	 */
	public void rescommit_after_fee(Integer id) {
		// 发票号
		Object[] objs = baseDao.getFieldsDataByCondition("invoice", new String[] { "in_code", "in_saleprice" }, "in_id=" + id + "");
		Double fee = baseDao.getSummaryByField("preinvoice left join invoice on pi_inid=in_id", "pi_fee", "pi_inid=" + id
				+ " and in_status in ('已提交','已审核')");
		baseDao.execute("update InvoiceDetail set id_xyfy_user=" + fee + " where id_inid=" + id + "");
		SqlRowList rs = baseDao.queryForRowSet("select pi_prcode,pi_prddetno from preinvoice where pi_inid=" + id
				+ " group by pi_prcode,pi_prddetno");
		while (rs.next()) {
			String pi_prcode = rs.getString("pi_prcode");// 预收单号
			int pi_prddetno = rs.getInt("pi_prddetno");// 预收明细序号
			// 发票明细金额
			Object[] obs = baseDao.getFieldsDataByCondition("preinvoice left join invoice on pi_inid=in_id", new String[] {
					"sum(pi_amount)", "sum(pi_fee)" }, "in_status in ('已提交','已审核') and pi_prcode='" + pi_prcode + "' and pi_prddetno="
					+ pi_prddetno + "");
			// 其它应付单已开票金额
			Object arbill = baseDao.getFieldDataByCondition("ARBill left join ARBillDetail on ab_id = abd_abid", "sum(abd_aramount)",
					"ab_statuscode='POSTED' and abd_prcode='" + pi_prcode + "' and abd_prddetno='" + pi_prddetno + "'");
			arbill = arbill == null ? 0 : arbill;
			// double pi_amount = Double.parseDouble(obs[0].toString());//预收金额
			// double pi_fee = Double.parseDouble(obs[1].toString());//费用
			// 预收账款明细费用
			Object prd_fee = baseDao.getFieldDataByCondition("PreRecDetail", "prd_fee", "prd_code='" + pi_prcode + "' and prd_detno="
					+ pi_prddetno + "");
			prd_fee = prd_fee == null ? "0" : prd_fee;
			if (Double.parseDouble((obs[1] == null ? "0" : obs[1]).toString()) > Double.parseDouble(prd_fee.toString())) {
				BaseUtil.showError("预收明细总费用：" + obs[1] + "不能超过预收账款单：" + pi_prcode + " 费用：" + prd_fee + "");
			}
			// 销售发票号
			String sxinvoice_user = baseDao
					.queryForObject(
							"select wm_concat(code) from (select in_code code,in_date from preinvoice left join invoice on pi_inid=in_id  where"
									+ " in_statuscode in('COMMITED','AUDITED') and pi_prcode='"
									+ pi_prcode
									+ "' and pi_prddetno="
									+ pi_prddetno
									+ " group by in_code,in_date  union "
									+ "select ab_code code,ab_indate from ARBill left join ARBillDetail  on abd_abid=ab_id where ab_statuscode='POSTED' and abd_prcode='"
									+ pi_prcode + "' and abd_prddetno='" + pi_prddetno + "' group by ab_code,ab_indate)", String.class);
			sxinvoice_user = sxinvoice_user == null ? "" : sxinvoice_user;
			/*
			 * Object prd_sxinvoice_user = baseDao.getFieldDataByCondition(
			 * "PreRecDetail left join PreRec on prd_prid=pr_id",
			 * "prd_sxinvoice_user",
			 * "pr_code='"+pi_prcode+"' and prd_detno="+pi_prddetno+"");
			 * if(prd_sxinvoice_user!=null && !"".equals(prd_sxinvoice_user)){
			 * if(!prd_sxinvoice_user.toString().contains(objs[0].toString())){
			 * prd_sxinvoice_user = prd_sxinvoice_user+","+objs[0]; } }else{
			 * prd_sxinvoice_user = objs[0]; }
			 */
			baseDao.execute("update PreRecDetail set prd_xynkpxy_user=" + obs[1] + ",prd_openamount_user=(" + obs[0] + "+ " + arbill
					+ "),prd_sxinvoice_user='" + sxinvoice_user + "' where prd_code='" + pi_prcode + "' and prd_detno=" + pi_prddetno + "");
			baseDao.execute("update PreRecDetail set prd_wkpfy_user=prd_fee-prd_xynkpxy_user where prd_code='" + pi_prcode
					+ "' and prd_detno=" + pi_prddetno + "");
		}
	}

	/**
	 * 华商龙请购单自动提交
	 */
	public void auto_commit(Integer ap_id) {
		Object ap_code = baseDao.getFieldDataByCondition("Application", "ap_code", "ap_id=" + ap_id + "");
		List<Integer> list = baseDao.queryForList("select ap_id from Application where ap_oldstatuscode='" + ap_code
				+ "' and ap_statuscode ='ENTERING'", Integer.class);
		for (Integer id : list) {
			try {
				applicationService.submitApplication(id, "Application");
			} catch (Exception e) {
				BaseUtil.appendError(e.getMessage());
			}

		}
	}

	/**
	 * 出入库单审核后上传到云顶 云顶
	 */
	public void auditafter_post_yunding(Integer id) {
		String baseUrl = "http://127.0.0.1:8008";
		if (StringUtil.hasText(baseDao.getDBSetting("sys", "baseUrl"))) {
			baseUrl = baseDao.getDBSetting("sys", "baseUrl");
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", id.toString());
		params.put("token", JwtUtil.createJWT("uas", "123"));

		String piClass = baseDao.getFieldValue("prodinout", "pi_class", "pi_id=" + id, String.class);
		String caller = getCallerByPiClass(piClass);

		try {
			Response response = HttpUtil.sendPostRequest(baseUrl + "/yunding/stock", params);

			String res = response.getResponseText();
			String failRemark = null;
			Map<Object, Object> resMap = BaseUtil.parseFormStoreToMap(res);
			if ("true".equals(String.valueOf(resMap.get("success")))) {
				baseDao.logger.others("发送单据信息到云顶", "发送成功", caller, "pi_id", id);
			} else {
				if (resMap.get("errCode") != null) {
					failRemark = String.valueOf(resMap.get("errDesc"));
				} else {
					failRemark = String.valueOf(resMap.get("remark"));
				}
				BaseUtil.showError("发送单据信息到云顶失败 原因： " + failRemark);
			}

		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
	}

	/**
	 * 针对通达生产退料单、委外退料单保存、修改前，将主表的退料类型更新到明细表中 pi_intype 的更新下来。其中表为
	 * 水口料入库和制程报废时，更新为：制程不良。
	 */
	public void prodInOut_return_save_description(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		baseDao.procedure("USER_SP_MAKEREUPDES", new Object[] { store.get("pi_id") });
	}

	/**
	 * 物料审核后上传到云顶 云顶
	 */
	public void prodauditafter_post_yunding(Integer id) {
		String baseUrl = "http://127.0.0.1:8008";
		if (StringUtil.hasText(baseDao.getDBSetting("sys", "baseUrl"))) {
			baseUrl = baseDao.getDBSetting("sys", "baseUrl");
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", id.toString());
		params.put("token", JwtUtil.createJWT("uas", "123"));

		try {
			Response response = HttpUtil.sendPostRequest(baseUrl + "/yunding/product", params);

			String res = response.getResponseText();
			String failRemark = null;
			Map<Object, Object> resMap = BaseUtil.parseFormStoreToMap(res);
			if ("true".equals(String.valueOf(resMap.get("success")))) {
				baseDao.logger.others("发送单据信息到云顶", "发送成功", "Product", "pr_id", id);
			} else {
				if (resMap.get("errCode") != null) {
					failRemark = String.valueOf(resMap.get("errDesc"));
				} else {
					failRemark = String.valueOf(resMap.get("remark"));
				}
				BaseUtil.showError("发送单据信息到云顶失败 原因： " + failRemark);
			}

		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
	}

	private String getCallerByPiClass(String piClass) {
		String caller = null;
		switch (piClass) {
		case "采购验收单":
			caller = "ProdInOut!PurcCheckin";
			break;
		case "采购验退单":
			caller = "ProdInOut!PurcCheckout";
			break;
		case "委外验收单":
			caller = "ProdInOut!OutsideCheckIn";
			break;
		case "委外验退单":
			caller = "ProdInOut!OutesideCheckReturn";
			break;
		case "其它入库单":
			caller = "ProdInOut!OtherIn";
			break;
		case "其它出库单":
			caller = "ProdInOut!OtherOut";
			break;
		case "拨出单":
			caller = "ProdInOut!AppropriationOut";
			break;
		default:
			break;
		}
		return caller;
	}

	/**
	 * 2018050211 采购订单提交增加逻辑配置，当下单数量不为最小包装量整数倍时提示 提示不限制。
	 */
	public void purchase_commit_checkzxbqty(Integer id) {
		List<Object[]> gridstore = baseDao.getFieldsDatasByCondition("PurchaseDetail left join product on pd_prodcode=pr_code",
				new String[] { "pd_qty", "pr_zxbzs", "pd_detno", "pd_prodcode" }, "pd_puid=" + id);
		for (Object[] grid : gridstore) {
			double remainder = (Double.parseDouble(grid[0].toString())) % (Double.parseDouble(grid[1].toString()));
			if (remainder != 0) {
				BaseUtil.showErrorOnSuccess("行" + grid[2] + "，物料" + grid[3] + "最小包装为" + grid[1] + "，当前下单数量为非最小包装整数倍，请确认");
			}
		}
	}

	/**
	 * 鼎智请购单提交增加逻辑配置，提示不限制。
	 */
	public void application_commit_checkdzkc(Integer id) {
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("applicationdetail", new String[] { "nvl(ad_standarddzkc,0)", "ad_detno",
				"ad_prodcode" }, "ad_apid=" + id);
		StringBuffer sb1 = new StringBuffer("集团有多余供应，请购单行号: ");
		StringBuffer sb2 = new StringBuffer();
		for (Object[] data : datas) {
			if (Integer.valueOf(data[0].toString()) > 0) {
				sb2.append(data[1] + ",");
			}
		}
		if (sb2.length() > 0) {
			sb1.append(sb2.substring(0, sb2.length() - 1));
			BaseUtil.showErrorOnSuccess(sb1.toString());
		}
	}
	/**
	 * wuyx 反馈编号 2018050693
	 * 怡海能达 根据原物料资料认可状态更新复制生成的物料的认可状态
	 * */
	public void product_after_copy(Integer id_old,Integer id_new) {
		baseDao.procedure("SP_PRODUCT_AFTER_COPY", new Object[] { id_old, id_new });
	}
	
	
	
	/**
	 * @author hx
	 * 2018050503  欧盛报工单提交限制：
	 * 1.如果该项目存在成本预估表，但是没有发包确认，需要限制报工单提交；如果没有成本预估表，则放开工时的限制，不做任何限制（有效才能限制）
	 * 2.当合计工时>发包确认单对应的工时，则限制提交，且提示
	 */
	public void BGD_commit_TimeLimit_Project(Integer id){
		//判断该项目是否存在成本预估表,没有发包确认
		boolean checkExitsCBYG = baseDao.checkIf("CUSTOMTABLE", "ct_caller='CBYGD' and ct_varchar50_6='有效' and ct_varchar50_2 in " + 
				" (select distinct cd_varchar50_8 from customtabledetail where cd_ctid="+id+")");
		if(checkExitsCBYG) {
			List<Object> prjcode = baseDao.getFieldDatasByCondition("CUSTOMTABLEDETAIL", "DISTINCT CD_VARCHAR50_8", "cd_ctid="+id);
			for (Object sbObject : prjcode) {
				boolean checkExitsFBQR = baseDao.checkIf("CUSTOMTABLE",  "ct_caller='FBQR' and ct_varchar50_1 ='"+sbObject+"'");
				if(!checkExitsFBQR) {
					BaseUtil.showError("该项目"+sbObject+"存在成本预估表，但是没有发包确认，限制报工单提交");
				}else {
					
					String type = String.valueOf(baseDao.getFieldDataByCondition("CUSTOMTABLE", "ct_varchar50_1", 
							"CT_CALLER='BGD' AND CT_ID="+id));
					String timeField = null;
					switch (type) {
					case "装配"://对应发包确认单-组织调试工时
						timeField = "SUM(ct_number_3)";
						break;
					case "电控"://对应发包确认单-电控设计工时
						timeField = "SUM(ct_number_2)";
						break;
					case "机械"://对应发包确认单-机械设计工时
						timeField = "SUM(ct_number_1)";
						break;
					default:
						break;
					}
					Object prjname = baseDao.getFieldDataByCondition("PROJECT", "PRJ_NAME", "PRJ_CODE='"+sbObject+"'");
					Object time_FBQR = baseDao.getFieldDataByCondition("CUSTOMTABLE", timeField, "CT_CALLER='FBQR' and ct_statuscode='AUDITED' AND "
							+ "ct_varchar50_1='"+sbObject+"'");
					Double timeFBQRNum = Double.valueOf(String.valueOf(time_FBQR==null?0:time_FBQR));
					Object time_BGD = baseDao.getFieldDataByCondition("customtabledetail", "sum(cd_number_1)", 
							"cd_ctid="+id+" and cd_varchar50_8='"+sbObject+"'");
					Double timeBGDNum = Double.valueOf(String.valueOf(time_BGD==null?0:time_BGD));
					if(timeBGDNum>timeFBQRNum) {
						BaseUtil.showError("项目:"+sbObject+" "+prjname+" 类型:"+type+" 所报工时:"+time_BGD+" 已超过预估工时:"+String.valueOf(time_FBQR==null?0:time_FBQR));
					}else if(timeBGDNum<timeFBQRNum && timeBGDNum>(timeFBQRNum*0.9)){
						BaseUtil.appendError("项目:"+sbObject+" "+prjname+" 所报工时已超过预估工时的90%");
					}
				}
			}
		}
	}
	
	/**
	 * 信扬 更新销售订单的剩余定金
	 */
	public void update_sabonduser(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_prcode,pi_prddetno from preinvoice where pi_inid=" + id
				+ " group by pi_prcode,pi_prddetno");
		while (rs.next()) {
			String pi_prcode = rs.getString("pi_prcode");// 预收单号
			int pi_prddetno = rs.getInt("pi_prddetno");// 预收明细序号
			// 发票明细金额
			Object[] obs = baseDao.getFieldsDataByCondition("preinvoice left join invoice on pi_inid=in_id", new String[] {
					"sum(pi_amount)", "sum(pi_fee)" }, "in_status in ('已提交','已审核') and pi_prcode='" + pi_prcode + "' and pi_prddetno="
					+ pi_prddetno + "");
			// 预收账款明细费用
			Object[]  data = baseDao.getFieldsDataByCondition("PreRecDetail", "prd_remarkbeiz_user,prd_ordercode", "prd_code='" + pi_prcode + "' and prd_detno="
					+ pi_prddetno + "");
			//反写销售订单界面 剩余定金
			if ("保证金".equals(data[0])) {
				baseDao.execute("update sale set sa_bond_user =sa_deposit - " + (obs[0] == null ? "0" : obs[0]) + " where sa_code='" + data[1] + "'" );
			}
		}
	}
	
	/**
	 * @author hx
	 * @param id
	 * 反馈编号：2018050513
	 * 报工人员的工时必须小于考勤时间(考勤时间将加班工时加进来)
	 */
	public void BGD_commit_TimeLimit_Attenddata(Integer id){
		//限制报工人员的工时(包括加班工时)
		List<Object[]> checkEmcodeTime = baseDao.getFieldsDatasByCondition("CUSTOMTABLEDETAIL LEFT JOIN  ATTENDDATA  ON CD_VARCHAR50_1=AD_EMCODE AND AD_INDATE=CD_DATE_1 "
				+ "left join (select * from Workovertimedet left join Workovertime   on wod_woid=wo_id where wo_statuscode='AUDITED') on wod_empcode=cd_varchar50_1 and to_char(cd_date_1,'yyyymmdd')=to_char(wod_startdate,'yyyymmdd') "
				+ "",
				new String[] {"DISTINCT CD_VARCHAR50_1","CD_VARCHAR50_2","nvl(AD_INTIMEMIN,0)","nvl(AD_ONOVERTIMEDK,0)","sum(CD_NUMBER_1)","CD_DATE_1","nvl(wod_cardcount,0)"},
				"cd_ctid="+id+" group by CD_VARCHAR50_1,CD_VARCHAR50_2,AD_INTIMEMIN,AD_ONOVERTIMEDK,CD_DATE_1,wod_cardcount");
		
		StringBuffer error = new StringBuffer();
		if(checkEmcodeTime.size()>0) {
			for (Object[] objects : checkEmcodeTime) {
				if(Double.valueOf(String.valueOf(objects[4]))>((Double.valueOf(String.valueOf(objects[2])))+Double.valueOf(String.valueOf(objects[3]))+(Double.valueOf(String.valueOf(objects[6]))))) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						error.append("员工:"+objects[1]+"在"+sdf.format(sdf.parse(String.valueOf(objects[5])))+"的出勤时间为"+
								(Double.valueOf(String.valueOf(objects[2]))+Double.valueOf(String.valueOf(objects[3]))+(Double.valueOf(String.valueOf(objects[6]))))+
								"，所报工时"+objects[4]+"超过出勤时间，不允许提交");
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(error.length()>0) {
			BaseUtil.showError(error.toString());
		}
	}
	
	/**
	 * 请购单:保存时,根据明细物料更新集团料号与呆滞库存数
	 * 
	 * @author guq 2018-06-25
	 **/
	public void application_save_updatePrcode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		List<String> update = new ArrayList<String>();
		update.add("update applicationdetail set ad_standardprcode=(select PRD_STANDARDPRCODE from PRODINNERRELATIONDET where ad_prodcode=PRD_BOMPRCODE) "
				+ "where exists (select 1 from PRODINNERRELATIONDET where PRD_BOMPRCODE=ad_prodcode and PRD_STATUS='有效') and ad_apid=" +store.get("ap_id"));
		update.add(" update applicationdetail set ad_standarddzkc= nvl((select sum(ba_remain) from batch,warehouse where ba_whcode=wh_code and ba_prodcode=ad_standardprcode and nvl(wh_ifmrp,0)=-1 and ba_date<sysdate-60),0) where ad_apid=" + store.get("ap_id"));
		baseDao.execute(update);
	}
	
	/**
	 * 出货单:保存、更新时,更新物料对应的BOM替代料号+库存
	 * 
	 * @author guq 2018-06-25
	 **/
	public void prodinout_updateRepcode(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pi_id = store.get("pi_id");
		int i = baseDao.getCount("select count(1) from  ProdInOut where nvl(PI_TOCODE,'')='售后出货' and pi_id=" + pi_id);
		if (i == 1) {
			String sql = "UPDATE PRODIODETAIL SET pd_repcode_user=(SELECT WM_CONCAT(pre_repcode||'('||onhand||')') from ( select pd_prodcode,pre_repcode,NVL(po_onhand,0)-NVL(po_defectonhand,0)onhand,(Row_number() over(partition by pd_prodcode order by NVL(po_onhand,0)-NVL(po_defectonhand,0) desc)) rn From ("
				       + " select pd_prodcode,pre_repcode from prodiodetail left join prodreplace on pd_prodcode=pre_soncode left join product on pr_code=pre_repcode "
				       +" where pd_piid=" + pi_id + " and nvl(pre_status,' ')<>'DISABLE' and nvl(pre_repcode,' ')<>' ' and pr_statuscode='AUDITED' "
				       +"  group by pd_prodcode,pre_repcode) left join productonhand on po_prodcode=pre_repcode) B "
				       + " where rn < 6 AND B.PD_PRODCODE=PRODIODETAIL.PD_PRODCODE group by B.pd_prodcode)  WHERE pd_piid=" + pi_id;
			baseDao.execute(sql);
		}
	}
	
	/**
	 * 
	 * @author goua 出货单提交之前更新备料状态
	 */
	public void prodinout_commit_before_judgeStatus(Integer id) {
		Double pd_outqty;
		Double bi_outqty;
		SqlRowList rs = baseDao.queryForRowSet("select nvl(sum(pd_outqty),0) pd_outqty from prodiodetail where pd_piid = ? ",id);
		if(rs.next()){
			pd_outqty = rs.getDouble("pd_outqty");
			rs = baseDao.queryForRowSet("select nvl(sum(bi_outqty),0) bi_outqty from barcodeio where bi_piid = ? ",id);
			if(rs.next()){
				 bi_outqty = rs.getDouble("bi_outqty");
				 rs = baseDao.queryForRowSet("select wm_concat(pd_prodcode)  pd_prodcode from (select nvl(sum(pd_outqty),0) pd_outqty,pd_prodcode from prodiodetail where pd_piid = ? group by pd_prodcode) left join "+
						" (select nvl(sum(bi_outqty),0) bi_outqty,bi_prodcode from barcodeio where bi_piid = ? group by bi_prodcode) on pd_prodcode=bi_prodcode "+
						" where pd_outqty<nvl(bi_outqty,0)",id,id);
				 if(rs.next() && rs.getString("pd_prodcode")!= null){
					 BaseUtil.showError("物料: "+rs.getString("pd_prodcode")+"已采集数量大于出库数量");
				 }
				 if(NumberUtil.compare(pd_outqty, bi_outqty) == -1){
					 BaseUtil.showError("已采集条码数量"+bi_outqty+"大于出库数"+pd_outqty);
				 }else if(NumberUtil.compare(pd_outqty, bi_outqty) == 0){
					 baseDao.execute("update prodinout set pi_pdastatus='已备料' where pi_id = ?",id);
				 }else if(NumberUtil.compare(pd_outqty, bi_outqty) == 1 && bi_outqty > 0){
					 baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id = ?",id);
				 }
				 if(bi_outqty == 0){
					 baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id = ?",id);
				 }
			}
		}
	}
	/**
	 *  维修出库单保存、更新反馈到对应的维修入库单 maz 2018060513
	 */
	public void prodInOut_maintain_save_qty(HashMap<Object, Object>store,ArrayList<Map<Object,Object>> gstore){
		Object pdid = null; //维修出库明细id
		Object pd_id = null; //维修入库明细id
		Object ordercode = null; //维修入库单号
		Object orderdetno = null; //维修入库行号.
		Object oldpdid = null; //修改前的维修入库ID
		double tQty = 0; // 本次修改的维修出库数量
		Object qty = null; // 查询该维修入库关联的其它维修出库数量总和。
		boolean bool = false; //判断是否修改了维修入库单号的标记
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			ordercode = s.get("pd_ordercode");
			orderdetno = s.get("pd_orderdetno");
			tQty = Double.parseDouble(s.get("pd_outqty").toString());
			if(pdid != null && Integer.parseInt(pdid.toString()) != 0){
				Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "nvl(pd_orderid,0)", "nvl(pd_outqty,0)",
						"pd_ordercode", "nvl(pd_orderdetno,0)" },
						"pd_id=" + pdid + " and nvl(pd_orderid,0)<>0");
				pd_id = Integer.parseInt(String.valueOf(objs[0]));
				if(objs !=null && Integer.parseInt(objs[0].toString()) != 0 && pd_id != null && Integer.parseInt(pd_id.toString()) != 0 ){
					if (StringUtil.hasText(ordercode) && ordercode.toString().equals(objs[2].toString()) && orderdetno.toString().equals(objs[3].toString())) { //判断本次转数量是否超额
						qty = baseDao.getFieldDataByCondition("ProdIODetail",
								"sum(nvl(pd_outqty,0))", "pd_ordercode='"
										+ objs[2] + "' and pd_orderdetno='" + objs[3]
										+ "' and pd_piclass in ('维修出库单') AND pd_id <>" + pdid);
					}else{ //有可能修改了维修出库单号
						oldpdid = pd_id;
						pd_id = baseDao.getFieldValue("ProdIODetail", "nvl(pd_id,0)", "pd_inoutno='" + ordercode + "' and pd_pdno="
								+ orderdetno, Integer.class);
						qty = baseDao.getFieldDataByCondition("ProdIODetail",
								"sum(nvl(pd_outqty,0))", "pd_ordercode='"
										+ objs[2] + "' and pd_orderdetno='" + objs[3]
										+ "' and pd_piclass in ('维修出库单') AND pd_id <>" + pdid);
						bool = true;
					}
					qty = qty == null ? 0 : qty;
					SqlRowList rs = baseDao.queryForRowSet("select pd_inoutno,pd_pdno,pd_inqty from ProdIODetail where pd_id="+pd_id+" and pd_inqty<"+Double.parseDouble(qty.toString()) +"+" + tQty +"");
					if (rs.next()) {
						StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],维修入库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
								.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_qty")).append(",已转数量:")
								.append(qty).append(",本次数量:").append(tQty);
						BaseUtil.showError(sb.toString());
					}
					//开始反写数量回维修入库.
					if(bool){
						baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)-" + objs[1], "pd_id=" + oldpdid);
						baseDao.updateByCondition("ProdIODetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + oldpdid);
					}
					baseDao.updateByCondition("ProdIODetail", "pd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
							"pd_id=" + pd_id);
					baseDao.updateByCondition("ProdIODetail", "pd_yqty=0", "NVL(pd_yqty,0)<=0 and pd_id=" + pd_id);
				}
			}else{ //保存时没有pd_id,也要计算yqty才行
				Object[] id = baseDao.getFieldsDataByCondition(
						"ProdIODetail", new String[] { "pd_id","pd_yqty", "pd_inqty"}, "pd_inoutno='" + ordercode + "' and pd_pdno=" + orderdetno);
//				Object r = baseDao.getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_outqty,0))",
//						"pd_piclass='维修出库单' and pd_ordercode='" + ordercode + "' and pd_orderdetno=" + orderdetno);
//				r = r == null ? 0 : r;
				if(id != null){
					if(NumberUtil.formatDouble(Double.parseDouble(id[1].toString()) + tQty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()), 2)){
						BaseUtil.showError("维修入库单号[" + ordercode + "]序号[" + orderdetno + "]数量超发,原数量为:" + id[2].toString() + ",已转出货数[" + id[1].toString() + "]本次数量[" + tQty + "].请修改数量!");
					}else{
						baseDao.execute("update prodiodetail set pd_yqty=nvl(pd_yqty,0)+"+tQty+" where pd_id="+id[0]);
					}
				}
			}
		}
	}
	/**
	 * @author wuyx
	 * 南靖采购验退单:明细的销售订单号+序号存在时,修改明细行数量时，根据明细行的销售单号+序号来更新对应销售订单的已转数
	 * 字段：销售订单号 pd_salecode_user
	 * 		 销售订单序号 pd_saledetno_user
	 * */
	
	public void purreturn_update_after_replayqty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object pd_salecode_user = null;//销售订单号
		Object pd_saledetno_user = null;//订单序号
		Object pdid = null;
		Double yqty = null;
		Double qty = null;
		Double oqty = null;
		double tQty = 0;
		for (Map<Object, Object> s : gstore) {
			pdid = s.get("pd_id");
			//新已转数
			tQty = Double.parseDouble(String.valueOf(s.get("pd_outqty")));
			Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_salecode_user", "pd_saledetno_user","pd_outqty" }, "pd_id=" + pdid
					+ " and nvl(pd_salecode_user,' ') <>' ' and nvl(pd_saledetno_user,0)<>0");
			if(objs!=null){
				pd_salecode_user = objs[0];
				pd_saledetno_user = objs[1];
				Object[] qtys = baseDao.getFieldsDataByCondition("SaleDetail", new String[] { "sd_yqty","sd_qty"}, "sd_code='" + pd_salecode_user+"' and sd_detno = "+pd_saledetno_user);
				if(qtys != null){
					//销售已转数
					yqty = qtys[0]== null ? 0.0 : Double.parseDouble(String.valueOf(qtys[0]));
					//销售明细数量
					qty = qtys[1] == null ? 0.0 : Double.parseDouble(String.valueOf(qtys[1]));
					//销售转采购验退数
					oqty = objs[2] == null ? 0.0 : Double.parseDouble(String.valueOf(objs[2]));
					//新已转数 < 销售数量-（销售已转数-原验退数）
					if (tQty > NumberUtil.sub(qty,NumberUtil.sub(yqty, oqty))) {
						BaseUtil.showError("验退数量超出对应销售订单可退数量:"+ (NumberUtil.sub(yqty,NumberUtil.sub(yqty, oqty))));
					}else{
						//新销售已转数 = 原销售已转数 - 原验退数 + 新验退数
						baseDao.updateByCondition("SaleDetail", "sd_yqty="+NumberUtil.add(NumberUtil.sub(yqty, oqty),tQty) ,"sd_code='" + pd_salecode_user+"' and sd_detno = "+pd_saledetno_user);
					}
				}
			}
		}
	}
}