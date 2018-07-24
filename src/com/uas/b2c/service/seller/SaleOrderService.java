package com.uas.b2c.service.seller;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.uas.api.b2c_erp.seller.model.Order;
import com.uas.api.b2c_erp.seller.model.OrderDetail;
import com.uas.api.b2c_erp.seller.service.OrderService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * 获取平台下达过来的采购单，并转为销售订单
 * 
 * @author yingp
 *
 */
@Service
public class SaleOrderService {

	@Resource(name = "api.orderService")
	private OrderService orderService;
	
	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
	private BaseDao baseDao;
	static final String INSERTSALE = "insert into sale (sa_date,sa_recorddate,sa_id,sa_code,sa_status,sa_statuscode,sa_currency,sa_rate, sa_toplace,sa_printstatus,sa_printstatuscode,sa_b2ccode,sa_ordertype,sa_kind,sa_b2cpucode,sa_fare,sa_confirmstatus) values(sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTSALEDETAIL = "insert into saledetail(sd_id,sd_said,sd_detno,sd_prodcode,sd_qty,sd_price,sd_total,sd_code,sd_statuscode,sd_status,sd_taxrate,sd_costprice,sd_taxtotal)values(SALEDETAIL_SEQ.nextval,";
	static final String INSERTSALEKIND ="Insert into salekind (SK_ID,SK_CODE,SK_NAME,SK_ENGNAME,SK_NUMCALLER,SK_EXCODE,SK_MRP,SK_CLASHFOR,SK_CLASHOPTION,SK_SALECATECODE,SK_COSTCATECODE,SK_BONDED,SK_COP,SK_SALECATENAME,SK_COSTCATENAME,SK_CLASHKIND,SK_PRICEKIND,SK_ALLOWZERO,SK_OUTTYPE,SK_GETPRICE,SK_STATUS,SK_STATUSCODE,SK_ISSAMPLE,SK_ISSALEPRICE,SK_CREATEBILL,SK_NOBOMLEVEL,SK_IFB2C) values (?,'优软商城','优软商城',null,null,null,-1,'无','不冲销',null,null,0,null,null,null,'无','NG',-1,'TURNOUT',null,'已审核','AUDITED',0,0,0,0,-1)";
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	/** 
	 * 获取并保存新下达的销售订单(锁定所有的出货单，不允许任何操作)
	 * 只有 502、406 状态码单据会回传到UAS
	 * 504：已付款待平台确认        --> 生成已审核的销售订单
	 * 502：平台已确认        --> 生成已审核出货单，没有该平台单号的销售订单则视为错误订单，不做操作
	 * 406: 待发货
	 * 501/603/606：付款失败   -->  不做操作
	 * @param masterName 
	 */
	public void getAllUnReceived(String masterName) {
		if(checkYes()){
			List<Order> orders = getSaleOrder(masterName);
			if (!CollectionUtils.isEmpty(orders)) {
				try{
					long[] orderids = new long[orders.size()];
					int n = 0;//总计
					for (Order or : orders) {
						long id = or.getId();
						orderids[n] = id;
						n++;
						String currency = or.getCurrency();
						SqlRowList rs = baseDao.queryForRowSet(
								"select sa_id,sa_code,sa_b2cstatus from sale where sa_ordertype='B2C' and nvl(sa_statuscode,' ')<>'FINISH' and sa_b2ccode='" + or.getId() + "'");
							if (!rs.next()) {// 未生成销售订单
								if(or.getStatus()== 502 || or.getStatus()== 406 ){
									int sa_id = baseDao.getSeqId("SALE_SEQ");
									int sk_id = baseDao.getSeqId("SALEKIND_SEQ");
									String sa_code = baseDao.sGetMaxNumber("Sale",2);
									// 获取销售类型
									Object salekind = baseDao.getFieldDataByCondition("salekind", "sk_name",
											"nvl(sk_ifb2c,0)<>0 and sk_statuscode='AUDITED'");
									if (salekind == null) {
										try{
											boolean isSucess = baseDao.execute(INSERTSALEKIND,new Object[] {sk_id});
											salekind = "优软商城";
											if(isSucess){
												b2cUtil.insertB2CScmTaskLog("Salekind", sk_id, "insert", "sucess", "自动生成销售类型[优软商城]",masterName);
											}
										}catch (Exception e) {
											b2cUtil.insertB2CScmTaskLog("Salekind", sk_id, "insert", "error", "插入销售类型[优软商城]失败",masterName);
										}
									}
									List<String> sqls = new ArrayList<String>();
									int detno = 1;
									double rate = 1;
									String recipient = "";
									for (OrderDetail od : or.getDetails()) {
										Object[] obs = baseDao.getFieldsDataByCondition("product",
												new String[] { "pr_code", "pr_unit" }, "pr_code='" + od.getCode() + "'");
										if (obs != null) {										
											if (String.valueOf(obs[1]).equals("KG") || String.valueOf(obs[1]).equals("KPCS")) {
												rate = 0.001;
											}
											Double sdCostprice = (od.getTaxunitprice() / rate)/(1+od.getTax()*0.01);
											sqls.add(INSERTSALEDETAIL + sa_id + "," + detno + ",'" + obs[0] + "',"
													+ od.getQty() * rate + "," + od.getTaxunitprice() / rate + ","
													+ od.getTotal() + ",'" + sa_code + "','AUDITED','"
													+ BaseUtil.getLocalMessage("AUDITED") + "','" + od.getTax() +"', "
													+sdCostprice+", "+sdCostprice*od.getQty() * rate+")");
											detno++;
										}else{
											orderids[n]=0;
										}
										if(od.getPublisheruu()!=null){
											recipient = recipient + od.getPublisheruu().toString()+",";
										}
									}
									//处理收任务人员
									recipient = recipient.substring(0, recipient.length()-1);
									String[] recipientArray = recipient.split(",");
									//去重
									Arrays.sort(recipientArray);  
									List<String> recipientList = new ArrayList<>();  
									recipientList.add(recipientArray[0]);  
							        for(int i=1;i<recipientArray.length;i++){  
							            if(!recipientArray[i].equals(recipientList.get(recipientList.size()-1))){  
							            	recipientList.add(recipientArray[i]);  
							            }  
							        }  
							         recipientArray = (String[]) recipientList.toArray(new String[recipientList.size()]); 
									if (detno > 1) {
										Map<String, Object> map = FlexJsonUtil.fromJson(or.getShipAddress());
										String address = String.valueOf(map.get("area"))
												+ String.valueOf(map.get("detailAddress"));
										String phone = String.valueOf(map.get("tel"));
										Object currencyrate = baseDao.getFieldDataByCondition("currencysmonth left join Currencys on cr_name=cm_crname", "cm_crrate", " nvl(cr_status,' ')='可使用' and rownum= 1 and cm_crname ='"+currency+"' order by CM_YEARMONTH desc");
										try{//生成销售订单
											baseDao.execute(INSERTSALE,
														new Object[] { sa_id, sa_code,
																BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
																currency,currencyrate==null?1.0:Double.parseDouble(currencyrate.toString()),
														address, BaseUtil.getLocalMessage("UNPRINT"),"UNPRINT", 
														or.getId(),"B2C",salekind,or.getOrderid(),or.getFare(),"确认"});
											b2cUtil.insertB2CScmTaskLog("Sale", sa_id,sa_code, "insert", "sucess", "插入销售订单【"+sa_code+"】成功 ");
										}catch (Exception e) {
											b2cUtil.insertB2CScmTaskLog("Sale", sa_id,sa_code, "insert", "error", "插入销售订单失败");
										}
										//明细插入
										try{
											baseDao.execute(sqls);
											b2cUtil.insertB2CScmTaskLog("Saledetail", 0, "insert", "sucess", "插入销售订单明细 sacode:【"+sa_code+"】sa_id: "+sa_id+" 成功 ",masterName);
										}catch (Exception e) {
											b2cUtil.insertB2CScmTaskLog("Saledetail", 0, "insert", "error", "插入销售订单明细失败 sacode:【"+sa_code+"】said: "+sa_id,masterName);
										}
										
										//发送任务通知
										String content = null;
										List<Object[]> adminCode = null;
										Long originatoruu = orderService.findAdminuu();
										for (int i = 0; i < recipientArray.length; i++) {
											adminCode = baseDao.getFieldsDatasByCondition("employee", new String[] {"em_code","em_name"}, "em_uu="+recipientArray[i]);
											if(!CollectionUtil.isEmpty(adminCode)){
												for(int j = 0;j<adminCode.size();i++){
													content = getContent(adminCode.get(j)[1].toString(),address,phone,sa_code,or);
													String res = baseDao.callProcedure("SP_CREATETASK", new Object[] { sa_id,sa_code,"Sale","优软商城销售订单通知",content,"jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS"+sa_id+"&gridCondition=sd_saidIS"+sa_id,adminCode.get(i)[0].toString(),originatoruu });
													if(!res.equals("OK")){
														b2cUtil.insertB2CActionTaskLog("sendSaleReport", "error", "发送给"+adminCode.get(i)[0].toString()+" 订单号【"+sa_code+"】通知失败："+b2cUtil.saveError(res),masterName);
													}else{
														b2cUtil.insertB2CActionTaskLog("sendSaleReport", "sucess", "发送"+adminCode.get(i)[0].toString()+" 订单号【"+sa_code+"】通知"+b2cUtil.saveError(res),masterName);
													}
												}
											}
										}
									}
								}
							}
					}
					if (n > 0) {
						long[] b = Arrays.copyOf(orderids, n);
						// 请求通知B2C，将B2C中的获取的数据改为已下载状态，防止重复请求
						//返回发货单
						replayDownLoadSuccess(b,masterName);
					}
				}catch(Exception e){
					b2cUtil.insertB2CActionTaskLog("销售订单处理", "error", e.getMessage(),masterName);
				}
			}
			
		}
	}
	public List<Order> getSaleOrder(String masterName){
		try{
			List<Order> orders = orderService.findAllUnReceived();
			return orders;
		}catch(Exception e){
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				logger.info(this.getClass() + "销售订单获取失败");
				b2cUtil.insertB2CActionTaskLog("销售订单获取", "error", "原因："+e.getMessage(),masterName);
			}
			return null;
		}
	}
	/**
	 * 同意订单
	 * 
	 * @param orderId
	 * @param agree
	 *            是否同意
	 */
	public void agree(int saleId, boolean agree) {
		Long orderId = baseDao.queryForObject("select sa_b2ccode from sale where sa_id=? and sa_ordertype='B2C'", Long.class,saleId);
		if (orderId != null) {
			Master master = SystemSession.getUser().getCurrentMaster();
			if (master.b2bEnable()) {
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				if (agree)
					orderService.agree(orderId);
				else
					orderService.disagree(orderId);
			}
			baseDao.execute("update sale set sa_confirmstatus=? where sa_id=?", agree ? "确认" : "不同意", saleId);
		}
	}

	/**
	 * 反馈已下载订单至B2C端
	 * 
	 * @param orderids
	 */
	public void replayDownLoadSuccess(long[] orderids,String masterName) {
		try{
			orderService.backOrder(orderids);
		}catch(Exception e){
			logger.info(this.getClass() + "反馈已下载订单至B2C端失败");
			b2cUtil.insertB2CActionTaskLog("反馈已下载订单至B2C端", "error", b2cUtil.saveError(e.getMessage()),masterName);
		}
		
	}

	/**
	 * 自动扣减平台仓库存数
	 * @param sa_id
	 */
	private void goodspwOnhandOff(int sa_id){
		SqlRowList rs = baseDao.queryForRowSet("select gd_id,sd_qty,gd_whcode,sd_prodcode,sd_detno,sd_code from saledetail left join goodsdetail on gd_b2bbatchcode=sd_b2cbarcode where sd_said=? and gd_id is not null ",sa_id);
		while(rs.next()){
			double qty = rs.getDouble("sd_qty");
			baseDao.execute("update goodsdetail set gd_qty=gd_qty-"+qty +",gd_saleqty=gd_saleqty+"+qty+" where gd_id=?",rs.getLong("gd_id"));
			Object [] obs = baseDao.getFieldsDataByCondition("goodspwonhand", new String []{"max(go_id)","NVL(max(go_onhand),0)"}, "go_prodcode='"+rs.getString("sd_prodcode")+"' and go_whcode='"+rs.getString("gd_whcode")+"'");
			if(obs != null){
				baseDao.execute("update goodspwonhand set go_onhand=go_onhand-"+qty+",go_saleqty=go_saleqty+"+qty+" where go_id=?",obs[0]);
				//库存异动记录日志
				baseDao.execute("insert into goodspwbook(gb_id,gb_source,gb_sourcecode,gb_sourcedetno,gb_action,gb_barcode,gb_b2bbatchcode,gb_whcode,gb_prodcode,gb_uuid,gb_qty,gb_onhand,gb_madedate,gb_date) "
						+ "select goodspwbook_seq.nextval,'销售下架','"+rs.getString("sd_code")+"','"+rs.getString("sd_detno")+"','获取销售单',gd_barcode,gd_b2bbatchcode,gd_whcode,gd_prodcode,gd_uuid,'"+qty+"','"
						+ (Double.valueOf(obs[1].toString()).doubleValue()-qty)+"',gd_madedate,sysdate from goodsdetail where gd_id=?",rs.getLong("gd_id"));
			}
		}	
	}
	
	private boolean checkYes() {//销售互通有勾选同时已初始化
		return baseDao.checkIf("configs",  "code='saleDataCon' and CALLER='Mall' and data<>0");	
	}
	
	public void catchBatch(String caller, int id) {
		Object[] o = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_class", "pi_inoutno" }, "pi_id="
				+ id);
		if (o.length == 3) {
			if (o[0].equals("POSTED")) {
				BaseUtil.appendError("已过账的单据不能抓取批号");
				return;
			}
			String type = baseDao.getDBSetting("BarCodeSetting", "ProdOutType");
			// 【BUG】【反馈编号:2017020630】【生产领料单】【修改了对于空数据的判断方式】
			if ("byBatch".equals(type) || "byProdcode".equals(type)) {
				int cn = baseDao.getCount("select count(1) from barcodeio where bi_piid=" + id);
				if (cn > 0) {
					BaseUtil.appendError("已有采集的条码，不允许抓取批号，如需操作请撤销已采集的数据");
					return;
				}
			}
			String res = baseDao.callProcedure("SP_SPLITPRODOUT_MALL",
					new Object[] { o[1].toString(), o[2].toString(), "1" });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.appendError("失败，原因是："+res);
				return;
			}
			logger.info(this.getClass() + "抓取批号："+id);
			b2cUtil.insertB2CActionTaskLog("抓取批号", "end", "抓取批号："+id);
			// 出库类型单据更新出库单备料状态
			updatePdaStatus(caller, id);
		}
	}
	
	// @add 20170524 出库类型的单据，保存，更新，获取批号之后更新备料状态
	private void updatePdaStatus(String caller, Object pi_id) {
		if (prodInOutDao.isOut(caller)) { // 出库类型
			// 如果所有的明细都没有条码则更新为空
			SqlRowList rs = baseDao.queryForRowSet("select count(1)cn from prodiodetail where pd_piid=? and nvl(pd_batchcode,' ')<>' '",
					pi_id);
			if (rs.next() && rs.getInt("cn") == 0) {
				baseDao.execute("update prodinout set pi_pdastatus='' where pi_id=? and nvl(pi_pdastatus,' ')<>' '", pi_id);
			} else {
				rs = baseDao.queryForRowSet("select count(1)cn from barcodeio where bi_piid=? and bi_outqty>0", pi_id);
				if (rs.next() && rs.getInt("cn") > 0) { // 有一行以上备料记录则是备料中
					baseDao.execute("update prodinout set pi_pdastatus ='备料中' where pi_id=?", pi_id);
					rs = baseDao
							.queryForRowSet(
									"select count(1) cn from (select pd_prodcode,pd_whcode,sum(pd_outqty)qty from prodiodetail left join batch on pd_batchid=ba_id "
											+ " where pd_piid=? and ba_hasbarcode<>0 group by pd_prodcode,pd_whcode)A left join (select bi_prodcode,bi_whcode,sum(bi_outqty)qty "
											+ " from barcodeio where bi_piid=? group by bi_prodcode,bi_whcode)B on (pd_prodcode=bi_prodcode and pd_whcode=bi_whcode) where A.qty>NVL(B.qty,0)",
									pi_id, pi_id);
					if (rs.next() && rs.getInt("cn") == 0) {
						baseDao.execute("update prodinout set pi_pdastatus ='已备料' where pi_id=?", pi_id);
					}
				} else {
					// --存在有条码的批号则更新成未备料
					baseDao.execute(
							"update prodinout set pi_pdastatus ='未备料' where pi_id=?  and exists(select 1 from prodiodetail,batch where pd_piid=? and pd_batchid=ba_id and ba_hasbarcode<>0)",
							pi_id, pi_id);
					// --所有批号都无条码则更新成无条码
					baseDao.execute(
							"update prodinout set pi_pdastatus ='无条码' where pi_id=? and not exists(select 1 from prodiodetail left join batch on ba_id=pd_batchid where pd_piid=?  and ba_hasbarcode=-1)",
							pi_id, pi_id);
				}
			}
		}
	}
	private String getContent(String admin,String address,String phone,String sa_code,Order or){
		String content = "";
		content = admin+"，您好！    \n    您有新的销售订单["+sa_code+"]需要完善。订单相关信息如下：\n"
				+"优软商城订单号："+b2cUtil.b2cContentUnkonw(or.getOrderid())+";\n";
		if(or.getBuyerEn()!=null){
			content += "客户企业名称："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnName())+";\n"
					+"客户企业简称："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnShortname())+";\n"
					+"客户企业类型："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnType())+";\n"
					+"营业执照号    ："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnBussinessCode())+";\n"
					+"客户企业地区："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnArea())+";\n"
					+"客户企业地址："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnAddress())+";\n"
					+"客户企业传真号："+b2cUtil.b2cContentUnkonw(or.getBuyerEn().getEnFax())+";\n";
		}	
		if(or.getBuyer()!=null){
			content +="下单人："+b2cUtil.b2cContentUnkonw(or.getBuyer().getUserName())+";\n"
					+"联系电话："+b2cUtil.b2cContentUnkonw(or.getBuyer().getUserTel())+";\n"
					+"邮箱："+b2cUtil.b2cContentUnkonw(or.getBuyer().getUserEmail())+";\n";
		}
		content +="收货地址："+b2cUtil.b2cContentUnkonw(address)	+";";
		return content;
	}
}
