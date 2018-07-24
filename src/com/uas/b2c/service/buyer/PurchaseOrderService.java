
package com.uas.b2c.service.buyer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.api.b2c_erp.baisc.model.EnterpriseUas;
import com.uas.api.b2c_erp.buyer.model.B2cOrder;
import com.uas.api.b2c_erp.buyer.model.B2cOrderDetail;
import com.uas.api.b2c_erp.buyer.model.Payment;
import com.uas.api.b2c_erp.buyer.service.B2cOrderService;
import com.uas.api.b2c_erp.seller.model.Order;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.b2c.model.JsonPament;
import com.uas.b2c.service.common.FileUploadB2CService;

import javax.annotation.Resource;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.FormAttachService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 获取平台下达过来的采购单，并转为销售订单
 * 
 * @author yingp
 *
 */
@Service
public class PurchaseOrderService {

	@Resource(name = "api.B2cOrderService")
	private B2cOrderService b2cOrderService;

	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired
	private FileUploadB2CService fileUploadB2CService;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private B2CUtil b2cUtil;
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	static final String INSERTPURCHASE = "insert into purchase (pu_date,pu_indate,pu_id,pu_code,pu_status,pu_statuscode,pu_currency,pu_buyerid,pu_buyercode,pu_buyername,pu_recordid,pu_recordman,pu_shipaddresscode,pu_getprice,pu_b2ccode,pu_pocode,pu_ordertype,pu_freight,pu_taxtotal,pu_total,pu_printstatus,pu_printstatuscode,pu_rate) values(sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	/*static final String INSERTPAYPLEASE = "insert into PayPlease (pp_paydate,pp_thispaydate,pp_id,pp_code,pp_type,pp_apply,pp_status,pp_statuscode)values(sysdate,sysdate,?,?,?,?,?,?)";
	static final String INSERTPAYPLEASEDETAIL = "insert into PayPleaseDetail (ppd_ppid,ppd_detno,ppd_id)values(?,?,?)";
	static final String INSERTPAYPLEASEDETAILDET = "insert into PayPleaseDetailDet (ppdd_billdate,ppdd_ppid,ppdd_detno,ppdd_pucode,ppdd_currency,ppdd_billamount,ppdd_thisapplyamount,ppdd_type,ppdd_id) values (sysdate,?,?,?,?,?,?,?,?)";
	*/
	static final String PAYPLEASE = "INSERT INTO PAYPLEASE(pp_id,pp_code,pp_date,pp_applyid,pp_apply,pp_status,pp_statuscode,pp_total,"
			+ "pp_paystatus,pp_paystatuscode,pp_type,pp_printstatus,pp_printstatuscode,pp_thispayamount) values (?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?)";
	static final String PAYPLEASEDETAIL = "INSERT INTO PAYPLEASEDETAIL(ppd_id ,ppd_ppid,ppd_detno,ppd_vendcode,ppd_vendname,ppd_paymethod,"
			+ "ppd_bankname,ppd_bankaccount,ppd_vendid,ppd_currency,ppd_applyamount,ppd_paymethodid,ppd_paymethodcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String PAYPLEASEDETAILDET = "INSERT INTO PAYPLEASEDETAILDET(ppdd_id,ppdd_ppdid,ppdd_ppid,ppdd_detno,ppdd_currency,ppdd_pucode,"
			+ "ppdd_billdate, ppdd_billamount,ppdd_account, ppdd_paymethodid,ppdd_thisapplyamount,ppdd_paymethod,"
			+ "ppdd_type) values (?,?,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?,?,?,?,?)";
	
	static final String INSERTB2C$PURCHASEORDER = "Insert into B2C$PURCHASEORDER (b2cpu_creattime,b2cpu_inserttime,b2cpu_id,b2cporderid,b2cpu_orderid,b2cpu_buyeruu,b2cpu_buyerenuu,b2cpu_sellerenuu,b2cpu_paytype,b2cpu_deliverytype,b2cpu_jsonaddress,b2cpu_lgtid,b2cpu_status,b2cpu_taxes,b2cpu_price,b2cpu_fare,b2cpu_currency,b2cpu_type,b2cpu_qty,b2cpu_b2cstatus) values (sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTB2C$PURCHASEORDERDETAIL = "Insert into B2C$PURCHASEORDERDETAIL (B2CPD_ID,B2CPD_PUID,B2CPORDERDTID,B2CPD_DETNO,B2CPD_DETAILID,B2CPD_BATCHCODE,B2CPD_UUID,B2CPD_PRODUCTCODE,B2CPD_CMPCODE,B2CPD_IMG,B2CPD_UNITPRICE,B2CPD_POE_NUMBER,B2CPD_TAXES,B2CPD_PRICE,B2CPD_TAX,B2CPD_PUCODE,B2CPD_INSERTTIME,B2CPD_BRANDNAMECN,B2CPD_BRNAME) values (";
	static final String INSERTB2C$PORDEREN = "Insert into B2C$PORDEREN  (B2CPE_INSERTDATE,B2CPE_ID,B2CPE_PUCODE,B2CPE_PUID,B2CPE_ENUU,B2CPE_ENNAME,B2CPE_ENSHORTNAME,B2CPE_ENTYPE,B2CPE_ENAREA,B2CPE_ENADDRESS,B2CPE_ENDELIVERADDR,B2CPE_ENTEL,B2CPE_ENFAX,B2CPE_ENEMAIL,B2CPE_ENBUSSINESSCODE,B2CPE_ENINDUSTRY) values (sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String VERIFYAPPLY = "insert into VerifyApply  (VA_CURRENCY,VA_RATE,VA_TRANSPORT,va_emcode,va_emname,va_departmentcode,va_department, va_vendcode,va_vendname,va_receivecode,va_receivename,va_cop,va_paymentscode,va_payments, va_class,va_statuscode,va_status,va_recorder,va_date,va_indate,va_id,va_code) select  pu_currency,pu_rate,pu_transport,pu_buyercode,pu_buyername,pu_departmentcode, pu_departmentname,pu_vendcode,pu_vendname,pu_receivecode,pu_receivename,pu_cop,pu_paymentscode,  pu_payments,'采购收料单','ENTERING','在录入','钟勇斌',sysdate,sysdate,?,? FROM PURCHASE WHERE pu_id=?";
	 /**  
	 * 获取并保存新下达的采购单
	 * 503：待付款        --> 生成在录入的采购单和在录入的预付款申请单
	 * 404：平台已确认发货 待收货        --> 生成在录入的采购收料单，没有该平台单号的采购单则视为错误订单，不做操作
	 * 603/606：付款失败   --> 发送付款失败通知，没有该平台单号的采购单则视为错误订单，不做操作
	 * @param masterName 
	 */
	public void getAllUnReceived(String masterName) {
		if (checkYes()) {
			List<B2cOrder> orders = getSaleOrder( masterName);
			if (!CollectionUtils.isEmpty(orders)) {
				try{
					long[] orderids = new long[orders.size()];
					int n = 0;// 总计
					String content ="";
					for (B2cOrder or : orders) {
						long id = or.getId();
						String orderCode = or.getOrderid();
						Integer orderStatus = or.getStatus();
						String buyerCode = "";
						String buyerName = "";
						int buyerId = 0;
						Object[] buyer = baseDao.getFieldsDataByCondition("employee", new String[]{"em_id","em_code","em_name"}, "em_uu = "+String.valueOf(or.getBuyeruu()));
						String url = "";
						if(buyer!=null){
							buyerId = Integer.valueOf(String.valueOf(buyer[0]));
							buyerCode = String.valueOf(buyer[1]);
							buyerName = String.valueOf(buyer[2]);
						}
						//查询是否插入过该订单
						SqlRowList b2cpuorder = baseDao.queryForRowSet("select * from b2C$purchaseorder where b2cporderid=?",id);
						boolean b2cPuOrderExist = baseDao.checkIf("b2C$purchaseorder", "b2cporderid="+id);
						if(!b2cPuOrderExist){//不存在 插入新的商城订单
							//将订单信息存入数据库中
							//插入表数据
							insertb2cpurchaseorder(or,"0",masterName);
							boolean autoAddPurchase = baseDao.isDBSetting("B2CSetting", "autoAddPurchase");
						    if (autoAddPurchase) {
						    	//自动生成采购单 待定
						    }
						}else{ //存在，判断订单类型决定处理方式
							SqlRowList b2cpurchase = baseDao.queryForRowSet("select * from purchase where pu_ordertype='B2C' and pu_status <> '已结案' and pu_statuscode <> 'FINISH' and pu_pocode=?",orderCode);
							//and pu_status <> '已审核' and pu_statuscode <> 'AUDITED'
							while(b2cpurchase.next()){
								String pu_code =  String.valueOf(b2cpurchase.getObject("pu_code"));
								String pu_status = String.valueOf(b2cpurchase.getObject("pu_statuscode"));
								int pu_id = Integer.valueOf(String.valueOf(b2cpurchase.getObject("pu_id")));
								Object pu_b2ccode = b2cpurchase.getObject("pu_b2ccode");
								String pp_b2csendstatus = String.valueOf(baseDao.getFieldDataByCondition("prepay", "pp_b2csendstatus", "pp_pocode='"+pu_code+"'"));
								url = "jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS"+pu_id+"&gridCondition=pd_puidIS"+pu_id;
								//更新商城订单id字段
								if(pu_b2ccode == null){
									baseDao.updateByCondition("purchase", "pu_b2ccode = "+or.getId(), "pu_id = "+pu_id);
								}
								//503 情况：①、订单付款中  ②、付款失败、商城付款审核不通过 条件：订单存在，付款通知完成
								if (orderStatus==503&&pu_status.equals("AUDITED")&&pp_b2csendstatus.equals("success")){ 
									//对应采购订单已审核且确认付款成功：平台付款审核不通过 付款失败
									//更新预付款单通知状态->付款失败
									baseDao.updateByCondition("prepay", "pp_b2csendstatus='付款失败'", "pp_pocode in ('"+pu_code+"')");
									//付款失败任务通知
									content = buyerName+"，您好！ \n    您商城采购订单【"+pu_code+"】（优软商城单号："+orderCode+"）付款审核未通过，请重新付款并确认。详情请联系商城客服。 ";
									b2cUtil.sendB2cTask("Purchase", url, pu_code, pu_id,orderCode, "优软商城采购订单付款失败通知", content, buyerCode, "0",masterName);
								}
								boolean exist = baseDao.checkIf("b2C$purchaseorder", "B2CPU_ORDERID='"+or.getOrderid()+"' and B2CPU_STATUS='"+or.getStatus()+"'");
								if(!exist){
									if(orderStatus==603){//603 情况： 商城订单被取消 （未按时付款）
										//未按时付款失败任务通知 (用户自己反过账预付账款单、结案申请单、采购单 重新去商城下单)
										insertb2cpurchaseorder(or,"-1",masterName);
										content = buyerName+"，您好！ \n    您商城采购订单【"+pu_code+"】（优软商城单号："+orderCode+"）已超过支付期限，订单已失效，请结案相关订单，并重新下订单。";
										b2cUtil.sendB2cTask("Purchase", url, pu_code, pu_id,orderCode, "优软商城采购订单取消通知", content, buyerCode, "0",masterName);
									}
									if(orderStatus==606){//606 情况： 商城订单被取消 （用户本人取消）
										//订单取消任务通知 (用户自己反过账预付账款单、删除银行登记、结案申请单、采购单 重新去商城下单)
										insertb2cpurchaseorder(or,"-1",masterName);
										content = buyerName+"，您好！ \n    您已取消在商城的采购订单【"+orderCode+"】 ，请结案相关UAS采购订单【"+pu_code+"】及相关单据。";
										b2cUtil.sendB2cTask("Purchase", url, pu_code, pu_id,orderCode, "优软商城采购订单取消通知", content, buyerCode, "0",masterName);
									}
									if(orderStatus==404){//404  情况： 平台已确认发货 待收货  自动录入采购收料单(在录入) 采购单转收料单
										insertb2cpurchaseorder(or,"1",masterName);
										//自动录入采购收料单(在录入) 采购单转收料单
										Key key = B2CPTurnAccept("Purchase!ToAccept!Deal",pu_id,or);
										//发送收料任务通知 !!!
										if(key!=null){
											int va_id = key.getId();
											String va_code = key.getCode();
											content = buyerName+"，您好！ \n    您有新的商城采购收料订单【"+key.getCode()+"】（优软商城单号："+orderCode+"）可以完善。";
											url = "jsps/scm/purchase/verifyApply.jsp?whoami=VerifyApply&formCondition=va_idIS"+va_id+"&gridCondition=vad_vaidIS"+va_id;
											b2cUtil.sendB2cTask("VerifyApply", url, va_code, va_id,orderCode, "优软商城采购收料通知", content, buyerCode, "0",masterName);
										}
									}
								}
								
							}
							
						}
						orderids[n] = id;//记录操作完成的对应商城订单id
						n++;
					}
					
					//通知商城已付款 
					if (n > 0) {
						long[] orderDownLoad = Arrays.copyOf(orderids, n);
						// 请求通知B2C，将B2C中的获取的数据改为已下载状态，防止重复请求
						//通知商城订单已下载 pu_b2corderdlstatus->1  // pu_b2corderdlstatus <> 1
						replayDownLoadSuccess(orderDownLoad,masterName);
					}
					
				}catch(Exception e){
					b2cUtil.insertB2CActionTaskLog("采购订单处理", "error",e.getMessage(),masterName);
				}
			}
		}
	}
	/**
	 * 获取商城的采购订单
	 */
	public List<B2cOrder>  getSaleOrder(String masterName){
		try{
			List<B2cOrder> orders = b2cOrderService.getAllOrder();
			return orders;
		}catch(Exception e){
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				logger.info(this.getClass() + "采购订单获取失败");
				b2cUtil.insertB2CActionTaskLog("采购订单获取", "error", "原因："+e.getMessage(),masterName);
			}
			return null;
		}
	}
	/**
	 * 反馈已下载订单至B2C端
	 * 
	 * @param b
	 */
	public void replayDownLoadSuccess(long[] orderDownLoad,String masterName) {
		//获取上传失败的订单 重新传送已下载状态   ！！
		//通知商城订单已下载 pu_b2corderdlstatus->1  // pu_b2corderdlstatus <> 1
		String orderDownLoadStr ="";
		try{
			List<Object> timeout = baseDao.getFieldDatasByCondition("b2C$purchaseorder", "b2cporderid", "nvl(b2cpu_downreport,' ') = 'timeout'");
			if(!CollectionUtil.isEmpty(timeout)){
				long[] timeoutList = b2cUtil.TransObjToLong(timeout);
				orderDownLoad = ArrayUtils.addAll(orderDownLoad, timeoutList);
			}
			boolean res = b2cOrderService.backOrder(orderDownLoad);//发起通知
			for (int i = 0; i < orderDownLoad.length; i++) {
				orderDownLoadStr+=orderDownLoad[i]+",";
			}
			orderDownLoadStr= orderDownLoadStr.substring(0, orderDownLoadStr.length()-1);
			if(res){
				baseDao.execute("update b2C$purchaseorder set b2cpu_downreport='success' where b2cporderid in (" +orderDownLoadStr +")");
			}
		}catch(Exception e){
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				baseDao.execute("update b2C$purchaseorder set b2cpu_downreport='error' where  b2cporderid in (" + orderDownLoadStr +")");
				b2cUtil.insertB2CActionTaskLog("通知商城已下载", "error", b2cUtil.saveError(e.getMessage()),masterName);
				e.printStackTrace();
			}
		}
	}
	private boolean checkYes() {//true 为存在   false 为不存在或者未勾选
		return baseDao.checkIf("configs",  "code='purchaseDataCon' and CALLER='Mall' and data<>0");
	}
	/**
	 * 插入商城订单
	 * */
	public void insertb2cpurchaseorder(B2cOrder or,String status,String masterName){
		int b2cpu_id = baseDao.getSeqId("B2C$PURCHASEORDER_SEQ");
		int detno = 1;
		Long id = or.getId();
		String orderCode = or.getOrderid();
		//订单明细
		List<String> sqls = new ArrayList<String>();
		for (B2cOrderDetail od : or.getOrderDetails()) {
			int b2cpd_id = baseDao.getSeqId("B2C$PURCHASEORDERDETAIL_SEQ");
			sqls.add(INSERTB2C$PURCHASEORDERDETAIL+b2cpd_id+" , "+b2cpu_id+" , "+od.getId()+" , "+od.getDetno()
				+" , '"+od.getDetailid()+"' , '"+od.getBatchCode()+"' , '"+od.getUuid()+ "' , '"+od.getProductCode()
				+"' , '"+od.getCmpCode()+ "' , '"+od.getImg()+ "' , "+od.getUnitprice()+" , "+od.getNumber()+ " , "+od.getTaxes()+" , "+od.getPrice()+ " , "
				+od.getTax()+" , '"+orderCode+"' , sysdate,'"+od.getBrandNameCn()+"', '"+od.getBrName()+"')");
			detno++;
		}
		//供应商明细表
		int b2cpe_id = baseDao.getSeqId("B2C$PORDEREN_SEQ");
		boolean insertstatus = false;
		if (detno > 1) {
			//主表
			try{
				insertstatus = baseDao.execute(INSERTB2C$PURCHASEORDER,
						new Object[]{b2cpu_id,id,orderCode,
					or.getBuyeruu(),or.getBuyerenuu(),or.getSellerenuu(),or.getPaytype(),or.getDeliverytype(),or.getJsonAddress(),or.getLgtId(),or.getStatus()
					,or.getTaxes(),or.getPrice(),or.getFare(),or.getCurrency(),or.getType(),or.getQty(),status});
				
				b2cUtil.insertB2CScmTaskLog("B2C$PURCHASEORDER", b2cpu_id, orderCode,"insert", "sucess", "插入商城订单"+orderCode,masterName);
			}catch (Exception e) {
				b2cUtil.insertB2CScmTaskLog("B2C$PURCHASEORDER", b2cpu_id, orderCode,"insert", "error", "插入商城订单号："+orderCode,masterName);
			}
			if(insertstatus){
				//明细1
				baseDao.execute(sqls);
				b2cUtil.insertB2CScmTaskLog("B2C$PURCHASEORDERDETAIL", 0,orderCode, "insert", "sucess", "插入商城明细 单号"+orderCode,masterName);
				//明细2
				EnterpriseUas sellerEnter = or.getSellerEnter(); 
				if(sellerEnter!=null){
					baseDao.execute(INSERTB2C$PORDEREN,new Object[]{b2cpe_id,orderCode,b2cpu_id,sellerEnter.getUu(),
							sellerEnter.getEnName(),sellerEnter.getEnShortname(),sellerEnter.getEnType(),sellerEnter.getEnArea()
							,sellerEnter.getEnAddress(),sellerEnter.getEnDeliverAddr(),sellerEnter.getEnTel(),sellerEnter.getEnFax(),
							sellerEnter.getEnEmail(),sellerEnter.getEnBussinessCode(),sellerEnter.getEnIndustry()});
					b2cUtil.insertB2CScmTaskLog("B2C$PORDEREN", 0,orderCode, "insert", "sucess", "插入商城供应商明细 单号"+orderCode,masterName);
				}
			}
		}
			
	}
	/**
	 * 自动生成预付款申请单
	 * */
	public void createprepay(int pu_id){
		boolean  puInsertStatus = false;
		Employee employeeNow = SystemSession.getUser();
		if(employeeNow!=null){
			String masterName =employeeNow.getCurrentMaster().getMa_name();
			Object[] purchase = baseDao.getFieldsDataByCondition("purchase",
					new String[]{"pu_pocode","pu_code","pu_total",
					"pu_vendcode","pu_vendname",
					"pu_currency","pu_date","nvl(pu_prepayamount,0)","pu_paymentsid",
					"nvl(pu_total,0)-nvl(pu_prepayamount,0) as amount","pu_payments",
					"pu_type","pu_buyercode","pu_paymentscode","pu_freight"}, " pu_id="+pu_id);
			if(purchase!=null){
				String orderCode = String.valueOf(purchase[0]);
				Object[] employee = baseDao.getFieldsDataByCondition("employee", new String[]{"em_id","em_name"},
						"em_code='"+String.valueOf(purchase[12])+"'");
				String pu_code = String.valueOf(purchase[1]);
				String pp_code = baseDao.sGetMaxNumber("PayPlease!YF", 2);
				int pp_id = baseDao.getSeqId("PAYPLEASE_SEQ");
				Double pu_total = Double.valueOf(String.valueOf(purchase[2]));
				//运费
				// 保存PayPlease
				puInsertStatus = baseDao.execute(PAYPLEASE, new Object[] { pp_id, pp_code, String.valueOf(employee[0]),
						String.valueOf(employee[1]), "在录入", "ENTERING", pu_total, "未付款", "UNPAYMENT","预付款","未打印","UNPRINT",pu_total });
				if(puInsertStatus){
					// 保存PayPleaseDetail
					b2cUtil.insertB2CScmTaskLog("PayPlease", pp_id,pp_code, "insert", "sucess", "成功 插入商城预付款申请订单【"+pp_code+"】 对应采购单【"+pu_code+"】 商城订单号："+orderCode);
					int ppd_id = baseDao.getSeqId("PayPleaseDetail_SEQ");
					Object[] ve = baseDao.getFieldsDataByCondition("vendor", 
							new String[]{"ve_name","ve_payment","ve_bank","ve_bankaccount","ve_id"}, "ve_code = '"+purchase[3]+"'");
					Object[] Payments = baseDao.getFieldsDataByCondition("Payments", 
							new String[]{"pa_id","pa_name"}, "pa_code = '"+purchase[13]+"'");
					puInsertStatus = baseDao.execute(PAYPLEASEDETAIL,
							new Object[]{ppd_id,pp_id,1,purchase[3],ve[0],Payments[1],ve[2],ve[3],ve[4],purchase[5],purchase[2],Payments[0],purchase[13]});		
					if(puInsertStatus) {
						b2cUtil.insertB2CScmTaskLog("PayPleasedetail", ppd_id, "insert", "sucess", "成功 插入商城预付款申请订单明细PayPleasedetail  ppd_id【"+ppd_id+"】 pp_code【"+pp_code+"】 商城订单号："+orderCode,masterName);
					}else{
						b2cUtil.insertB2CScmTaskLog("PayPleasedetail", ppd_id, "insert", "error", "失败 插入商城预付款申请订单明细PayPleasedetail  ppd_id【"+ppd_id+"】 pp_code【"+pp_code+"】 商城订单号："+orderCode,masterName);
					}
					// 保存PayPleaseDetailDet
					int ppdd_id =  baseDao.getSeqId("PayPleaseDetailDet_SEQ");
					puInsertStatus = baseDao.execute(PAYPLEASEDETAILDET, 
							new Object[]{ppdd_id,ppd_id,pp_id,1,purchase[5],
							pu_code,purchase[6],purchase[2],purchase[7],purchase[8],purchase[9],purchase[10],"采购单"});	
					if(puInsertStatus) {
						b2cUtil.insertB2CScmTaskLog("PayPleasedetailDet", ppdd_id, "insert", "sucess", "成功 插入商城预付款申请订单明细PayPleasedetailDet  ppdd_id【"+ppdd_id+"】 pp_code【"+pp_code+"】 商城订单号："+orderCode,masterName);
					}else{
						b2cUtil.insertB2CScmTaskLog("PayPleasedetailDet", ppdd_id, "insert", "error", "失败 插入商城预付款申请订单明细PayPleasedetailDet  ppdd_id【"+ppdd_id+"】 pp_code【"+pp_code+"】 商城订单号："+orderCode,masterName);
					}
				}else{
					b2cUtil.insertB2CScmTaskLog("PayPlease", pp_id,pp_code, "insert", "error", "失败 插入商城预付款申请订单【"+pp_code+"】 对应采购单【"+pu_code+"】 商城订单号："+orderCode,masterName);
				}
				//发送采购通知 
				senpurchasereport(pu_code, pp_code,masterName);
			}
		}else BaseUtil.showError("当前登录状态异常，请重新登录。");
	}
	/**
	 * 发送预付款通知 
	 * */
	private void senpurchasereport(String pu_code,String pp_code,String masterName){
		//发送采购通知 ！！
		String orderCode = baseDao.queryForObject("select pu_pocode from purchase where pu_ordertype='B2C' and pu_code = ?",String.class,pu_code);
		if(orderCode!=null){
			Object[] buyer = baseDao.getFieldsDataByCondition("employee left join b2C$purchaseorder on em_uu = b2cpu_buyeruu ", new String[] {"em_name","em_code"}, 
					"b2cpu_orderid = '"+orderCode+"'");
			int pp_id = baseDao.queryForObject("select pp_id from PayPlease where pp_code = ?", Integer.class, pp_code);
			String content = String.valueOf(buyer[0])+"，您好！ \n    您有新的预付款申请单【"+pp_code+"】需要完善。订单相关信息如下：\n"
					+"优软商城订单号："+orderCode+";\n"
					+ "采购订单号："+pu_code+";";
			String url = "jsps/fa/arp/payplease2.jsp?formCondition=pp_idIS"+pp_id+"&gridCondition=ppd_ppidIS"+pp_id;
			b2cUtil.sendB2cTask("PayPlease", url, pp_code, pp_id, orderCode,"优软商城采购订单预付款申请通知", content, String.valueOf(buyer[1]), "0",masterName);
		}
	}
	/**
	 * 商城确认收货
	 * */
	public void ensury(int pi_id,Master master,Employee employee) {
		if (b2cUtil.isB2CMAll(master)) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			Object[] ensuryMsg = baseDao.getFieldsDataByCondition("prodinout left join ProdIODetail on pi_id = pd_piid left join purchase on pu_code = pd_ordercode", new String[] {"pu_pocode","pi_statuscode"}, "pd_piid = "+pi_id);
			try{
				String status = String.valueOf(ensuryMsg[1]);
				if(status.equals("POSTED")){
					b2cOrderService.ensury(String.valueOf(ensuryMsg[0]));
					baseDao.execute("update prodinout  set pi_prodinreport ='success' where pi_id = "+pi_id);
					b2cUtil.insertB2CActionTaskLog("商城确认收货", "confirm prod in ", "商城确认收货,操作人："+employee.getEm_name()+"("+employee.getEm_code()+") ",master.getMa_name());
				}else{
					BaseUtil.showError("当前单据尚未过账，请过账后再确认收货！");
				}
			}catch(Exception e){
				if(e instanceof SocketTimeoutException){
					BaseUtil.showError("商城确认收货操作超时,请重新确认");
				}else if(e instanceof RuntimeException){
					String error = e.getMessage();
					BaseUtil.showError(error.substring(error.lastIndexOf(":")+1).trim());
				}else{
					//记录失败状态
					BaseUtil.showError(e.getMessage());
				}	
			}
			
		}
	}
	/**
	 * 商城采购订单转收料单
	 * */
	public Key B2CPTurnAccept(String caller,Integer pu_id,B2cOrder or) {
		List<Map<Object, Object>> maps = new ArrayList<Map<Object, Object>> ();
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object[] formdata = baseDao.getFieldsDataByCondition("purchase", new String []{"pu_vendcode","pu_paymentscode","pu_currency","pu_receivecode","pu_code"}, "pu_id = "+pu_id);
		map.put("pu_vendcode", formdata[0]);
		map.put("pu_paymentscode", formdata[1]);
		map.put("pu_currency", formdata[2]);
		map.put("pu_receivecode", formdata[3]);
		map.put("pd_tqty", 0);
		map.put("pd_id", 0);
		// 收料单主表
		int id = baseDao.getSeqId("VerifyApply_SEQ");
		String code = baseDao.sGetMaxNumber("VerifyApply", 2);
		Key key = new Key(id, code);
		boolean res = baseDao.execute(VERIFYAPPLY,new Object[]{id,code,pu_id});
		final String VERIFYAPPLYDETAIL = "insert into VERIFYAPPLYDETAIL (vad_sellercode,vad_seller,vad_orderprice,vad_taxrate, vad_class,vad_prodcode,vad_pudate,vad_sourcecode,vad_pucode,vad_pudetno,vad_status,vad_statuscode, vad_vendcode,vad_vendname,"
				+" ve_status,vad_qty,vad_id,vad_detno,vad_vaid,vad_code) select PurchaseDetail.PD_SELLERCODE,PurchaseDetail.PD_SELLER, PurchaseDetail.pd_price,PurchaseDetail.pd_rate, '采购入库申请单',PurchaseDetail.pd_prodcode,sysdate,PurchaseDetail.pd_code,"
				+" PurchaseDetail.pd_code,PurchaseDetail.pd_detno,'在录入','ENTERING', Purchase.pu_vendcode,Purchase.pu_vendname , '未审核',"
				+" PurchaseDetail.PD_qty,VERIFYAPPLYDETAIL_SEQ.NEXTVAL,PurchaseDetail.pd_detno,"+id+",'"+code+"'"
				+" FROM PURCHASE LEFT JOIN PURCHASEDETAIL ON PU_ID=PD_PUID LEFT JOIN PRODUCT ON PD_PRODCODE=PR_CODE WHERE pd_id=";

		b2cUtil.insertB2CScmTaskLog(caller, key.getId(),key.getCode(), "transfer", "sucess", "商城采购订单【"+String.valueOf(formdata[4])+"】自动转收料单【"+key.getCode()+"】 商城订单号："+or.getOrderid());
		if (key != null) {
			int va_id = key.getId();
			List<Object[]> detaildata = baseDao.getFieldsDatasByCondition("purchasedetail", new String[]{"pd_id","pd_tqty"}, "pd_puid = "+pu_id+" order by pd_detno");
			if(res){
				if(!CollectionUtil.isEmpty(detaildata)){
					String[] VerifyApplyDetailSqls = new String[detaildata.size()];
					for (int i = 0; i < detaildata.size(); i++) {
						VerifyApplyDetailSqls[i] = VERIFYAPPLYDETAIL+detaildata.get(i)[0];
						map.put("pd_id", detaildata.get(i)[0]);
						map.put("pd_tqty", detaildata.get(i)[1]);
						
					}
					baseDao.execute(VerifyApplyDetailSqls);
				}
			}
			// 转入明细  
			b2cUtil.insertB2CScmTaskLog(caller, key.getId(),key.getCode(), "transferdetail", "sucess", "明细：商城采购订单【"+String.valueOf(formdata[4])+"】自动转收料单【"+key.getCode()+"】 商城订单号："+or.getOrderid());
			baseDao.execute("UPDATE PURCHASEDETAIL SET pd_yqty=nvl(pd_qty,0) WHERE pd_code='" + String.valueOf(formdata[4])+"'");
			baseDao.execute("update VerifyApplyDetail set vad_unitpackage=vad_qty where vad_vaid=" + va_id);
			baseDao.execute(
					"update VerifyApply set va_rate=(SELECT nvl(cm_crrate,0) from Currencysmonth where va_currency=cm_crname and cm_yearmonth=to_char(va_date,'yyyymm')) where va_id=?",
					va_id);
			baseDao.execute(
					"update VerifyApplydetail set vad_price=(select round(price+price*amount/case when total=0 then 1 else total end,8) from (select vad_id,(vad_orderprice*va_rate/(1+vad_taxrate/100)) price,(select sum(vad_qty*vad_orderprice*va_rate*(1+nvl(vad_taxrate,0)/100)) from VERIFYAPPLYDetail pp1 left join VERIFYAPPLY p1 on pp1.vad_vaid=p1.va_id where p1.va_id=VERIFYAPPLYdetail.vad_vaid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetailAN A where A.pd_anid=VERIFYAPPLYdetail.vad_vaid),0) amount from VERIFYAPPLYDetail left join VERIFYAPPLY on vad_vaid=va_id where vad_vaid=?) B where B.vad_id=VERIFYAPPLYdetail.vad_id) where vad_vaid=? and nvl(vad_price,0)=0",
					va_id, va_id);
			baseDao.execute(
					"update VerifyApplydetail set vad_total=round(vad_price*vad_qty,2),vad_ordertotal=round(vad_orderprice*vad_qty,2),vad_plancode=round(vad_orderprice*vad_qty*(select va_rate from VERIFYAPPLY where va_id=vad_vaid),2) where vad_vaid=?",
					va_id);
			baseDao.execute("update VerifyApplydetail set vad_barcode=round(vad_total-nvl(vad_plancode,0),2) where vad_vaid=?",
					va_id);
			baseDao.execute(
					"update VerifyApply set va_total=round((select sum(vad_orderprice*vad_qty) from VERIFYAPPLYdetail where va_id=vad_vaid),2) where va_id=?",
					va_id);
			//记录物流编号 va_deliverycode 
			baseDao.execute( "update VerifyApply set va_deliverycode = ? where va_id = ?", new Object [] {String.valueOf(or.getLgtId()),va_id});
			/**
			 * @author wsy 双单位
			 */
			baseDao.execute(
					"update VerifyApplydetail set vad_purcqty=round(vad_qty/(select pd_qty from purchasedetail where pd_code=vad_pucode and pd_detno=vad_pudetno)*(select case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end from purchasedetail where pd_code=vad_pucode and pd_detno=vad_pudetno),2) where vad_vaid=?",
					va_id);
			baseDao.execute("update purchasedetail set pd_ypurcqty=nvl(pd_ypurcqty,0)+(select nvl(vad_purcqty,0) from VerifyApplydetail where vad_pucode=pd_code and vad_pudetno=pd_detno and vad_vaid="
					+ va_id + ") where pd_puid=" + pu_id);
		}
		// 修改采购单状态
		for (Map<Object, Object> mapItem : maps) {
			int pdid = Integer.parseInt(mapItem.get("pd_id").toString());
			purchaseDao.udpatestatus(pdid);
		}
		return key;
	}
	/**
	 * 商城采购通知已付款
	 * */
	public void pay(int pp_id,Master master,Employee employee) {
		if (b2cUtil.isB2CMAll(master)) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			Payment payment = new Payment();
			SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			JsonPament jsonPament = new JsonPament();//付款方银行信息
			//根据预付账款id 获取 币别 就录入uu 采购单号 账户编号  来源类型 来源单号
			
			if(baseDao.checkIf("PrePay left join PrePayDetail on ppd_ppid = pp_id left join purchase on pu_code = pp_sourcecode", "nvl(pp_b2csendstatus,' ') = 'success' and pp_id = "+pp_id)){
				BaseUtil.showError("列表采购单据已确认，请勿重复操作");
			}else{
				List<Object[]> obj = baseDao.getFieldsDatasByCondition("PrePay left join employee on PP_RECORDERID = em_id left join PrePayDetail on ppd_ppid = pp_id left join purchase on pu_code = ppd_ordercode ", 
						new String[]{"pp_currency","em_uu","ppd_ordercode","pp_accountcode","pp_source","pp_sourcecode","pp_date","pp_attach","pp_code","pp_amount"}, "upper(nvl(pu_ordertype,' ')) = 'B2C' and nvl(ppd_ordertype,' ') = '采购单' and nvl(pp_b2csendstatus,' ') <> 'success' and pp_id = "+pp_id);
				if(!CollectionUtil.isEmpty(obj)){
					for (int i = 0; i < obj.size(); i++) {
						//根据账户编号 获取银行科目信息：银行名、描述、录入时间、账户
						Object[] bankMsg = baseDao.getFieldsDataByCondition("Category", new String[]{"ca_name","ca_description","ca_recorddate","ca_bankaccount"},
								 " ca_code = '"+obj.get(i)[3]+"'");
						String pu_code = String.valueOf(obj.get(i)[2]);
						//付款日期 采购单号 申请人
						//根据采购单号取商城单号
						Object b2cOrderCode = baseDao.getFieldDataByCondition("Purchase", "pu_pocode", "pu_code = '"+pu_code+"'");
						Date createTime = new Date();
						try {//录入日期
							createTime = String.valueOf(bankMsg[2]) == "null"?null:format.parse(String.valueOf(bankMsg[2]));
							jsonPament.setBankname(String.valueOf(bankMsg[0]));//银行名
							jsonPament.setAccountname(String.valueOf(bankMsg[1]));//科目描述
							jsonPament.setNumber(String.valueOf(bankMsg[3]));//银行账号
							jsonPament.setCreateTime(createTime);//录入日期
							jsonPament.setCurrency(String.valueOf(obj.get(i)[0]));//币别
							jsonPament.setEnuu(String.valueOf(master.getMa_uu()));//企业UU
							jsonPament.setDissociative("1602");//是否个人用户 1062：企业行为
							jsonPament.setOpraterUserType("1061");//操作人类型  1061：个人行为
							jsonPament.setUseruu(String.valueOf(obj.get(i)[1]));
							List<JsonPament> jsonPamentlist = new ArrayList<JsonPament>();  
							jsonPamentlist.add(jsonPament);
							JSONArray jsonPamentJson = JSONArray.fromObject(jsonPamentlist);
							payment.setJsonPament(String.valueOf(jsonPamentJson));
							payment.setTransferTime(format.parse(String.valueOf(obj.get(i)[6])));//付款日期 
							payment.setOrderid(String.valueOf(b2cOrderCode));//商城单号
							payment.setTotal(Double.parseDouble(String.valueOf(obj.get(i)[9])));//付款总金额	
							String url = getInputStream(String.valueOf(obj.get(i)[7]));
							payment.setImgUrl(url);
							boolean res = b2cOrderService.ensuryPay(payment);
							if(res){
								//记录成功状态
								baseDao.execute("update PrePay  set pp_b2csendstatus ='success' where pp_id = "+pp_id);
							}else{
								baseDao.execute("update PrePay  set pp_b2csendstatus ='error' where pp_id = "+pp_id);
							}
							b2cUtil.insertB2CScmTaskLog("PrePay!Arp!PAMT",pp_id ,String.valueOf(obj.get(i)[8]), "confirm pay", "success", "商城确认 操作人："+employee.getEm_name()+"("+employee.getEm_code()+") ");
						}catch(Exception e){ 
							if(e instanceof SocketTimeoutException||e instanceof RemoteConnectFailureException){
								BaseUtil.showError("商城确认超时,请重新操作。");
							}else if(e instanceof RuntimeException){
								baseDao.execute("update PrePay  set pp_b2csendstatus ='error' where pp_id = "+pp_id);
								String error = e.getMessage();
								BaseUtil.showError(error.substring(error.lastIndexOf(":")+1).trim());
							}else{
								//记录失败状态
								baseDao.execute("update PrePay  set pp_b2csendstatus ='error' where pp_id = "+pp_id);
								BaseUtil.showError(e.getMessage());
							}	
						}
					}
				}else{
					BaseUtil.showError("采购单非商城订单，不需要进行确认操作！");
				}
			}
		}
	}
	/**
	 * 获取凭证图片路径
	 * */
	public String getInputStream(String id){
		if(!id.equals("null")){
			JSONObject obj = formAttachService.getFiles(id).getJSONObject(0);
			String path = obj.getString("fp_path");
			String name = obj.getString("fp_name");
			InputStream in = null;	
			try {
				if (path.startsWith("http:")||path.startsWith("https:")) {
					in = HttpUtil.download(path);
				}else if (path.startsWith("ftp:") || path.startsWith("sftp:")) {
					// 存放在其他网络资源中，直接跳转至链接地址
					//response.sendRedirect(path);
					return null;
				}else if (path.startsWith("B2B://")) {// 文件在云平台
					path = SystemSession.getUser().getCurrentMaster().getMa_b2bwebsite() + "/" + path.substring(6);
					in = HttpUtil.download(path);
				} else {
					File file = new File(path);
					in = new FileInputStream(file);
				}
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
				byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
				int rc = 0;
				while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
				}
				byte[] bt = swapStream.toByteArray();
				String url = fileUploadB2CService.saveImage(name,bt);
				return url;
			} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			} finally {  
		        try {  
		            in.close();  
		        } catch(IOException e) {  
		        }  
		    }
		}else{
			BaseUtil.showError("含商城类型采购订单的预付账款需要附上银行付款水单扫描件！");
		}
		return null; 
	}
}
