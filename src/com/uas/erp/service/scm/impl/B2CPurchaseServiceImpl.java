package com.uas.erp.service.scm.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.b2c.service.common.GetGoodsReserveService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.service.scm.B2CPurchaseService;
import com.uas.pda.dao.PdaCommonDao;

@Service("B2CPurchaseService")
public class B2CPurchaseServiceImpl implements B2CPurchaseService{
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private GetGoodsReserveService getGoodsReserveService;
    @Autowired
    private PdaCommonDao pdaCommonDao;
    @Autowired
	private ApplicationDao applicationDao;
    @Override
	public Map<Object, List<Map<String, Object>>> getReserveByUUid(String pr_code) {
    	String codes = SqlUtil.splitToSqlString(pr_code,",");
    	SqlRowList rs = baseDao.queryForRowSet("select wm_concat(distinct pr_uuid) uuid from product where pr_code in ("+codes+")");
		if(rs.next() && rs.getObject("uuid") != null){
			  getGoodsReserveService.getGoodsOnhand(rs.getString("uuid"));
			  getGoodsReserveService.getGoodsBatch(rs.getString("uuid"));
			rs = baseDao.queryForRowSet("select gb_b2bbatchcode,gb_price,nvl(gb_deliverytime,0)gb_deliverytime,nvl(gb_hkdeliverytime,0) gb_hkdeliverytime,gb_madedate,gb_onsaleqty,gb_minbuyqty,gb_minpackqty,go_prodcode,go_onsaleqty,gb_currency, 0 \"buyQty\" from B2C$GoodsOnhand left join B2C$GoodsBatch on gb_uuid=go_uuid where go_prodcode in("+codes+") and nvl(go_onsaleqty,0)>0");
			if(rs.next()){
				Map<Object, List<Map<String, Object>>> set = new HashMap<Object, List<Map<String, Object>>>();
				List<Map<String, Object>> list = null;
				for (Map<String, Object> map : pdaCommonDao.changeKeyToLowerCase(rs.getResultList())) {
					Object key = map.get("go_prodcode");
					if (StringUtil.hasText(key) && set.containsKey(key)) {
						list = set.get(key);
					} else {
						list = new ArrayList<Map<String, Object>>();
					}
					list.add(map);
					set.put(key, list);
				}
				return set;
			}
		}
		return null;
	}
	
	static final String INSERTPURC = "INSERT INTO purchase(pu_id,pu_code,pu_status,pu_statuscode,pu_recordid,pu_recordman,pu_indate,"
			+ "pu_type,pu_kind,pu_isinit,pu_printstatus,pu_printstatuscode,pu_ordertype,pu_currency,pu_rate)" +
			" VALUES (?,?,?,?,?,?,?,?,?,'0',?,'UNPRINT','B2C',?,'1')";
	static final String INSERTB2CPURCB = "insert into B2C$PurchaseBatch(pb_id,pb_puid,pb_puid,pb_prodcode,pb_uuid,pb_b2bbatchcode,pb_qty,pb_price,pb_erpunit)" +
							"values(B2C$PURCHASEBATCH_SEQ.nextval,?,?,?,?,?,?,?)";
	@Override
	public String comfirmB2CPurchase(String param, String data, String caller,String currency) {
		Map<Object,Object> mapB = BaseUtil.parseFormStoreToMap(param);//批次数据信息
		List<Map<Object,Object>> grid = BaseUtil.parseGridStoreToMaps(data);	
		// 判断本次数量
		applicationDao.checkAdYqty(grid);
		// 整批转采购单
		StringBuffer sb = new StringBuffer();
		int puid = 0;
		String pu_code = null;
		String adidstr = "";
		for (Map<Object, Object> map : grid) {
			adidstr += "," + map.get("ad_id").toString();
		}
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct NVL(pk_mrp,0) kind from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
							+ adidstr + ") and ap_kind=pk_name)");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("参与MRP运算的请购必须与不参与的请购分开下达!");
				}
			}
			rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct NVL(pk_iflack,0) kind from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
							+ adidstr + ") and ap_kind=pk_name)");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("参与缺料运算的请购必须与不参与的请购分开下达!");
				}
			}
			if (baseDao.isDBSetting("CopCheck")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ap_cop from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的请购单不允许合并下达到一张采购单中!");
					}
				}
			}
			if (baseDao.isDBSetting(caller, "allowDifferentKind")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct NVL(pk_mrp,0),nvl(pk_iflack,0) from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
						+ adidstr + ") and ap_kind=pk_name)");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("采购类型中的[参与MRP运算]+[参与缺料运算]同时一致的情况下才可以合并下达到一张采购单中!");
					}
				}
			} else {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ap_kind from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("不同请购类型不能下达到一张采购单中!");
					}
				}
			}
			if (baseDao.isDBSetting(caller, "mrpSeparateFactory")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ad_factory from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("不同的所属工厂不能下达到一张采购单中!");
					}
				}
			}
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('请购单号：'||ad_code||',行：'||ad_detno||',明细状态：'||ad_status) from ApplicationDetail where nvl(ad_statuscode,' ') in ('FINISH','NULLIFIED','FREEZE') and ad_id in ("
									+ adidstr + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("选中的明细行已结案、已冻结、已作废，不允许转入采购单!" + dets);
			}
		}
		// 指定了采购单
		if (grid.size() > 0) {
			if (StringUtil.hasText(grid.get(0).get("pu_code"))) {
				//不允许追加至采购单
			} else {
				StringBuffer err = new StringBuffer();				
				List<String>sql = new ArrayList<String>();
				Iterator<Map.Entry<Object, Object>> it = mapB.entrySet().iterator();
				Map<Object,List<Map<Object,Object>>> maplistB = new HashMap<Object, List<Map<Object,Object>>>();
				while (it.hasNext()) {//物料
				   Map.Entry<Object, Object> entry = it.next();
				   List<Map<Object,Object>> maps1 = BaseUtil.parseGridStoreToMaps(entry.getValue().toString());
				   for(Map<Object,Object> da:maps1){//批次数据
						Double price = 0.0;
						//从后台获取数据
						SqlRowList rs = baseDao.queryForRowSet("select * from B2C$GOODSBATCH where gb_b2bbatchcode='"+da.get("gb_b2bbatchcode")+"'");
						if(!rs.next()){
							err.append("批次号不存在");
						}
						//判断库存是否足够
						if(Double.valueOf(da.get("buyQty").toString()) > rs.getDouble("gb_onsaleqty")){
							err.append("批次库存不足");
						}
						if(StringUtil.hasText(rs.getObject("gb_price"))){
							List<Map<Object,Object>> li = BaseUtil.parseGridStoreToMaps(rs.getObject("gb_price").toString());
							for(Map<Object,Object> pr:li){
								double buyQty = Double.valueOf(da.get("buyQty").toString()),
										start = Double.valueOf(pr.get("start").toString());
								if( buyQty > start || start == buyQty){
									if("USD".equals(currency)){
									    price = Double.valueOf(pr.get("uSDNTPrice").toString());
									}else{
										price = Double.valueOf(pr.get("rMBNTPrice").toString());
									}
								}
							}	
							da.put("price", price);
						}else{
							err.append(da.get("gb_b2bbatchcode") + "没有分段价格信息");
						}										
					}	
				   maplistB.put(entry.getKey(), maps1);
				}
				if(err.toString().length()>0){
					BaseUtil.showError(err.toString());
				}
                //转明细表
				puid = baseDao.getSeqId("PURCHASE_SEQ");
				pu_code = baseDao.sGetMaxNumber("Purchase", 2);
                it = mapB.entrySet().iterator();
                Map<Object, List<Map<Object, Object>>> li = BaseUtil.groupMap(BaseUtil.parseGridStoreToMaps(data),"ad_prodcode");
            	int detno = 1;
            	Iterator<Map<Object, Object>> iterg ;
				Map<Object, Object> da = null;
				Map<Object, Object> map = null;
                while (it.hasNext()){
					   Map.Entry<Object, Object> entry = it.next();
					   String pr_code = entry.getKey().toString();
					   Object ob = baseDao.getFieldDataByCondition("product", "pr_id", "pr_code='"+pr_code+"'");
		               int pr_id = 0;
		               if(ob != null){
		                   pr_id = Integer.valueOf(ob.toString());
		               }
					   List<Map<Object,Object>> maps1 = maplistB.get(pr_code);//批次数据
					   List<Map<Object,Object>> appmap = li.get(pr_code);//采购数据
					   Iterator<Map<Object, Object>> iterm = maps1.iterator();
					  while(iterm.hasNext()){//分配请购数量
						    da = iterm.next();
						    //根据批次号获取 erpunit 单位
							Object unit = baseDao.getFieldDataByCondition("B2C$GoodsBatch left join B2C$GoodsOnHand on go_uuid=gb_uuid", "go_erpunit", "gb_b2bbatchcode='"+da.get("gb_b2bbatchcode")+"'");
							//生成表B2C$PurchaseBatch
		                    sql.add("insert into B2C$PurchaseBatch(pb_id,pb_puid,pb_pucode,pb_prodcode,pb_uuid,pb_b2bbatchcode,pb_qty,pb_price,pb_erpunit)" +
						  	   "values(B2C$PURCHASEBATCH_SEQ.nextval,"+puid+",'"+pu_code+"','"+pr_code+"','"+da.get("gb_uuid")+"','"+da.get("gb_b2bbatchcode")+"',"+da.get("buyQty")+","+da.get("price")+",'"+unit+"')");
		                   iterg = appmap.iterator();
		                   while(iterg.hasNext()){
								map = iterg.next();
								double buyQty = Double.valueOf(da.get("buyQty").toString());
								double tqty = Double.valueOf(map.get("ad_tqty").toString());
								double pd_qty = 0.0;
								String batchcode = da.get("gb_b2bbatchcode").toString();
								double price = Double.valueOf(da.get("price").toString());
								int ad_id = Integer.valueOf(map.get("ad_id").toString());						
								if(buyQty > tqty){
									pd_qty = tqty;
									da.put("buyQty", NumberUtil.sub(buyQty, tqty));
									iterg.remove();
								}else if(buyQty == tqty){
									pd_qty = buyQty;
									iterg.remove();
									iterm.remove();
								}else{							
									pd_qty = buyQty;
									iterm.remove();
									map.put("ad_tqty", NumberUtil.sub(tqty, buyQty));
								}		
								//转采购单明细表
								sql.add("insert into purchaseDetail(pd_id,pd_puid,pd_prodcode,pd_detno,pd_qty,pd_source,pd_status," +
										"pd_auditstatus,pd_sourcecode,pd_sourcedetail,pd_turnman,pd_apremark,pd_bonded,pd_delivery,pd_b2cbatchcode," +
										"pd_factory,pd_mark,pd_remark,pd_price,pd_prodid,pd_code)" +
										" select PURCHASEDETAIL_SEQ.nextval,"+puid+",ad_prodcode,"+detno+","+pd_qty+",ad_apid,'ENTERING','" +
										BaseUtil.getLocalMessage("ENTERING")+"',ap_code,ad_id,'"+SystemSession.getUser().getEm_name()+"',ap_remark,ad_bonded,ad_delivery,'"+batchcode+"'," +
										"ad_factory,ad_use,ad_remark,"+price+","+pr_id+",'"+pu_code+"' from Application left join Applicationdetail on ad_apid=ap_id where ad_id="+ad_id);
								detno++;
								sql.add("update applicationDetail set ad_yqty=nvl(ad_yqty,0)+"+pd_qty+" where ad_id="+ad_id);
								sql.add("INSERT INTO messagelog(ml_id,ml_date,ml_man,ml_content,ml_result,ml_search,code) " +
										"select MESSAGELOG_SEQ.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','请购单转采购单','行'||ad_detno||',数量'||"+pd_qty+",'Application|ap_id='||ad_apid, ap_code from applicationdetail left join application on ap_id=ad_apid where ad_id="+ad_id);
							}
					   }
				}
				baseDao.execute(sql);
                //转采购单主表
				String conKind = String.valueOf(grid.get(0).get("ap_kind"));
				String pu_type = grid.get(0).get("ap_type") == null ? null : grid.get(0).get("ap_type").toString();
				baseDao.execute(INSERTPURC, 
						new Object[]{puid,pu_code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", SystemSession.getUser().getEm_id(),
						SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
						pu_type,conKind,BaseUtil.getLocalMessage("UNPRINT"),currency});
				
				// 检查是否有超请购数量下达采购的
				SqlRowList rs0 = baseDao
						.queryForRowSet("select ad_prodcode from purchase left join purchasedetail on pu_id=pd_puid left join applicationdetail on ad_id=pd_sourcedetail where pu_code='"+pu_code+"' and ad_qty<ad_yqty");
				if (rs0.next()) {
					BaseUtil.showError("物料：" + rs0.getString("ad_prodcode") + "超请购数量下达");
				}
			}				
		}		
		// 修改请购单状态
		for (Map<Object, Object> map : grid) {
			int adid = Integer.parseInt(map.get("ad_id").toString());
			applicationDao.checkAdQty(adid);
		}
		//判断是否配置优软商城供应商编号
		String venderCode = baseDao.getDBSetting("B2CSetting", "B2CVendor");
		if(venderCode!= null){
			SqlRowList rs = baseDao.queryForRowSet("select ve_id,ve_code,ve_name, "+
					" nvl(ve_buyerid,0)ve_buyerid,em_code,ve_buyername, NVl(ve_paymentid,0)ve_paymentid,ve_paymentcode ,ve_payment ,ve_apvendcode ,ve_apvendname ,ve_shipment from"
					+ "  VENDOR LEFT JOIN EMPLOYEE ON VE_BUYERID = EM_ID where ve_code='"+venderCode+ "' and ve_auditstatuscode='AUDITED'");
		    if(rs.next()){
		    	//根据商城配置更新供应商
				baseDao.execute("update purchase set pu_vendid='"+rs.getGeneralInt("ve_id")+"',pu_vendcode='"+rs.getString("ve_code")+"',pu_vendname='"+rs.getString("ve_name")+"',"+
							" pu_buyerid='"+rs.getGeneralInt("ve_buyerid")+"',pu_buyercode='"+rs.getGeneralString("em_code")+"',pu_buyername='"+rs.getString("ve_buyername")+"',pu_paymentsid='"+rs.getGeneralInt("ve_paymentid")+"',pu_paymentscode='"+rs.getGeneralString("ve_paymentcode")+"',"+
							" pu_payments='"+rs.getGeneralString("ve_payment")+"',pu_receivecode='"+rs.getGeneralString("ve_apvendcode")+"',pu_receivename='"+rs.getGeneralString("ve_apvendname")+"',pu_transport='"+rs.getGeneralString("ve_shipment")+"' where pu_id="+puid);
		    }else{
		    	sb.append("参数配置中的优软商城供应商编号["+venderCode+"]不存在或未审核<hr>");
		    }
		}else{ 
			sb.append("未维护参数配置中的优软商城供应商编号<hr>");
		}
		// 更新交货地址
		baseDao.execute("update purchase set pu_shipaddresscode=(select en_deliveraddr from enterprise where nvl(en_deliveraddr,' ')<>' ') where pu_id="+puid+" and nvl(pu_shipaddresscode,' ')=' '");
		// 按物料的采购员信息更新采购单
		baseDao.execute("update purchase set (pu_buyerid,pu_buyercode,pu_buyername)=(select max(em_id),max(em_code),max(em_name) from product left join purchasedetail on pd_prodcode=pr_code left join employee on em_name=pr_buyername where pd_puid=pu_id and nvl(pr_buyername,' ')<>' ') where pu_id='"+puid+"'");
		// 所属公司默认其中一张请购单的所属公司
		baseDao.execute("update purchase set pu_cop=(select max(ap_cop) from purchasedetail,application where pd_puid=pu_id and pd_source=ap_id) where pu_code='"+pu_code+"'");
		// 按请购单的采购员信息更新采购单
		baseDao.execute("update purchase set (pu_buyerid,pu_buyercode,pu_buyername)=(select max(em_id),max(em_code),max(em_name) from application left join purchasedetail on pd_source=ap_id left join employee on em_code=ap_buyercode where pd_puid=pu_id and nvl(ap_buyercode,' ')<>' ') where pu_code='"+pu_code+"'"+
				 " and pu_id in (select pd_puid from purchasedetail where pd_source in (select ap_id from application where nvl(ap_buyercode,' ')<>' '))");
		baseDao.execute("update purchase set pu_buyerid="
				+ SystemSession.getUser().getEm_id() + ",pu_buyername='"
				+ SystemSession.getUser().getEm_name() + "',pu_buyercode='"
				+ SystemSession.getUser().getEm_code() + "' where pu_code='"+pu_code+"' and pu_buyercode is null");
		baseDao.execute("update purchasedetail set pd_appdate=(select ap_auditdate from application where pd_sourcecode=ap_code) where pd_code='"+pu_code+"'");
		baseDao.execute("update purchasedetail set pd_netprice=round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8) where pd_id="+puid);
		baseDao.execute("update purchasedetail set pd_taxtotal=round(nvl(pd_netprice,0)*nvl(pd_qty,0),2) where pd_id="+puid );		
		sb.append("转入成功,采购单号:"
				+ "<a href=\"javascript:openUrl('jsps/b2c/purchase/b2cPurchase.jsp?formCondition=pu_idIS"
				+ puid + "&gridCondition=pd_puidIS" + puid
				+ "')\">" + pu_code + "</a>&nbsp;");
		return sb.toString();
	}
 
	
}
