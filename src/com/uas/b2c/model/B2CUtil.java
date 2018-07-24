package com.uas.b2c.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.Prod;
import com.uas.api.b2c_erp.seller.model.ProductDetailERP;
import com.uas.b2c.service.common.B2CStoreService;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
@Service
public class B2CUtil {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private B2CStoreService b2CStoreService;
	private static final String INSERTB2CTASKLOG = "insert into b2c$task (ta_id,ta_actiontime,ta_operation,ta_finishstatus) values(?,sysdate,?,?) ";
	private static final String INSERTB2CTASKLOGWITHMASTER = "insert into b2c$task (ta_id,ta_actiontime,ta_operation,ta_finishstatus,ta_master) values(?,sysdate,?,?,?) ";
	private static final String INSERTB2CSCMTASKLOG = "insert into b2c$task (ta_id,ta_actiontime,ta_docaller,ta_doid,ta_operation,ta_finishstatus,ta_master) values (?,sysdate,?,?,?,?,?) ";
	private static final String INSERTB2CSCMCODETASKLOG = "insert into b2c$task (ta_id,ta_actiontime,ta_docaller,ta_doid,ta_docode,ta_operation,ta_finishstatus,ta_master) values (?,sysdate,?,?,?,?,?,?) ";
	
	static final String UPDATEINITSTATUS ="update configs set data = 1 where CODE='b2cInitStatus' AND CALLER='sys'";
	static final String INITCHECK ="update warehouse set wh_ismallstore = 1 where wh_statuscode='AUDITED' and wh_status='已审核' and wh_code in ( ";
	/**
	 * 物料信息视图 B2C_PRODUCTINIT_VIEW
	 * 交易信息视图 B2C_PRODDETAIL_VIEW
	 * 更新关键信息视图 B2C_PRODUCTRPD_VIEW （库存、价格、生产日期）
	 * 
	 * b2c空闲库存 B2C_PRODRESERVE_VIEW
	 * b2c最后一个月销售平均价格 B2C_PRODPRICE_LASTMONTH_VIEW
	 * 已勾选物料 B2C_PRODUCTSELECTED_VIEW
	 * */
	//初始化状态为空的总数量
	static final String PRODUCTALLCOUNT = "select count(1) FROM B2C_PRODRESERVE_VIEW  LEFT JOIN B2C_PRODPRICE_LASTMONTH_VIEW ON SALECODE = PR_CODE where pr_b2csendstatus  = ' ')";
	//品牌或型号为空、库存小于最小订购量数量 （不通过数）
	static final String PRODUCTERRORCOUNT = "select count(1) FROM B2C_PRODRESERVE_VIEW  LEFT JOIN B2C_PRODPRICE_LASTMONTH_VIEW ON SALECODE = PR_CODE where pr_b2csendstatus  = ' ' and (reserve < minBuyQty or pr_brand is null or pr_orispeccode is null) ";
	//物料信息获取 pr_id,pr_code,pr_detail,pr_brand, pr_orispeccode, pr_uuid
	//condition: pr_b2csendstatus  = ' ' and reserve > minBuyQty and nvl(pr_brand,' ') <> ' ' null and nvl(pr_orispeccode,' ') <> ' ' order by pr_code
	static final String GETPRODUCTSQL = "SELECT * FROM B2C_PRODUCTINIT_VIEW";
	
	//交易信息 pr_code code, minPackQty,maxDelivery,minDelivery,SALEPRICE price , produceDate
	//condition: reserve > minBuyQty and pr_b2csendstatus = '0' and pr_b2cinitproddtstatus = ' '  and  pr_b2cinitstatus <>'已上传' order by pr_code
	static final String GETPRODUCTDETAILSQL ="SELECT * FROM B2C_PRODDETAIL_VIEW";
	
	//空闲库存、价格、生产日期更新  pr_code code, reserve,SALEPRICE price,produceDate
	//condition:  pr_b2cstorestatus  = ' ' and pr_b2csendstatus ='1' and PR_B2CINITPRODDTSTATUS ='已上传' order by pr_code 
	static final String UPDATERESERVESQL = "SELECT * FROM B2C_PRODUCTRPD_VIEW";		
	
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	/**	
	 *	上传成功
	 *  pr_b2csendstatus '' 未上传   '0' 已上传，待匹配   '1' 已通知匹配  '-1' 报错物料，不上传
	 *  @param ArrayList<String> idStrList
	 *  @param String type
	 *  @param String masterName
	 */
	public void onUploadSuccess(ArrayList<String> idStrList,String type,String masterName) {
		if(!CollectionUtil.isEmpty(idStrList)){
			String condition = "1=0";
			String tableName="b2c$task";
			String update = " TA_ID = TA_ID ";
			String operation ="";
			switch (type) {
				case "prod":
					tableName="product";
					update = " pr_b2csendstatus= 1 ,pr_b2cinitstatus = '已上传' ";
					condition = " to_char(pr_b2csendstatus)='0' and pr_id ";
					operation ="更新物料信息状态pr_b2csendstatus->已上传";
					break;
				case "proddetail":
					tableName="product";
					update = " pr_b2cinitproddtstatus = '已上传' ";
					condition = " pr_b2cinitproddtstatus = '待上传' and pr_code ";
					operation ="更新物料交易信息状态pr_b2cinitproddtstatus->已上传";
					break;
				case "prodreverse":
					tableName="product";
					update = " pr_b2cstorestatus='' ";
					condition = "  pr_b2cstorestatus='待上传' and pr_code";
					operation ="更新物料空闲库存状态pr_b2cstorestatus->“”";
					break;
				default:
					break;
			}
			for (int i = 0; i < idStrList.size(); i++) {
				try{
					baseDao.updateByCondition(tableName, update, condition+" in ("+ idStrList.get(i) + ")");
				}catch (Exception e){
					insertB2CActionTaskLog(operation, "error", saveError(e.getMessage())+"编号："+saveError(idStrList.get(i)),masterName);
				}
			}
		}
	}
	/**	
	 *	上传中状态
	 *	@param conStr
	 *  @param type
	 *  @param masterName
	 */
	public void onUploading(String conStr,String type,String masterName) {
		if(conStr!=null){
			String condition = "1=0";
			String tableName="b2c$task";
			String update = " TA_ID = TA_ID ";
			String operation ="";
			switch (type) {
				case "prod":
					tableName="product";
					update = " pr_b2csendstatus= 0 ";
					condition = " pr_id ";
					operation ="更新物料信息状态pr_b2csendstatus->待上传";
					break;
				case "proddetail":	
					tableName="product";
					update = " pr_b2cinitproddtstatus='待上传' ";
					condition = " pr_code ";
					operation ="更新物料交易信息状态pr_b2cinitproddtstatus->待上传";
					break;
				case "prodreverse":	
					tableName="product";
					update = " pr_b2cstorestatus='待上传' ";
					condition = " pr_code ";
					operation ="更新物料库存信息状态pr_b2cstorestatus->待上传";
					break;
				case "purchaseOrderDownLoad":	
					tableName="b2C$purchaseorder";
					update = " b2cpu_downreport = 'timeout' ";
					condition = "  b2cpu_downreport <> 'success' and b2cporderid ";
					operation ="更新采购单下载通知状态 pu_b2corderdlstatus->“timeout”";
					break;
				default:
					break;
			}
			try{
				baseDao.updateByCondition(tableName, update, condition+" in ("+ conStr + ")");
			}catch (Exception e){
				insertB2CActionTaskLog(operation, "error", saveError(e.getMessage())+" 编号："+conStr,masterName);
			}
		}
	}
	/**	
	 *	还原上传失败待上传物料状态
	 *  @param type
	 *  @param masterName
	 */
	public void onUploadFail(String type,String masterName) {
		String condition = "1=0";
		String tableName="b2c$task";
		String update = " TA_ID = TA_ID ";
		String operation ="";
		switch (type) {
		case "prod":
			tableName="product";
			update = " pr_b2csendstatus='' ";
			condition = "  to_char(pr_b2csendstatus) = '0' ";
			operation ="更新物料信息状态pr_b2csendstatus->“”";
			break;
		case "proddetail":
			tableName="product";
			update = " pr_b2cinitproddtstatus = '' ";
			condition = "  pr_b2cinitproddtstatus='待上传' ";
			operation ="更新物料交易信息状态pr_b2cinitproddtstatus->“”";
			break;
		case "prodreverse":	
			tableName="product";
			update = " pr_b2cStorestatus='' ";
			condition = "  pr_b2cStorestatus='待上传' ";
			operation ="更新物料库存信息状态pr_b2cStorestatus->“”";
			break;
		case "purchaseOrderDownLoad":	
			tableName="b2C$purchaseorder";
			update = " b2cpu_downreport = '' ";
			condition = "  b2cpu_downreport = 'timeout' ";
			operation ="更新采购单下载通知状态 b2cpu_downreport->“”";
			break;
		default:
			break;
		}
		try{
			baseDao.updateByCondition(tableName, update, condition);
		}catch (Exception e){
			insertB2CActionTaskLog(operation, "error", saveError(e.getMessage()),masterName);
		}
	}
	/**	
	 *	上传失败  更改物料状态，不上传 或者 -1
	 *  @param conStr
	 *  @param type
	 *  @param masterName
	 */
	public void onUploadFailClose(String conStr,String type,String masterName) {
		String condition = "1=0";
		String tableName="b"
				+ "2c$task";
		String update = " TA_ID = TA_ID ";
		String operation ="";
		switch (type) {
		case "prod":
			tableName="product";
			update = " pr_b2csendstatus=-1 ";
			condition = "  pr_id ";
			operation ="更新物料信息状态pr_b2csendstatus->“-1” 不上传";
			break;
		case "proddetail":
			tableName="product";
			update = " pr_b2cinitproddtstatus = '不上传' ";
			condition = " pr_code ";
			operation ="更新物料交易信息状态pr_b2cinitproddtstatus->“不上传”";
			break;
		case "prodreverse":	
			tableName="product";
			update = " pr_b2cStorestatus='不上传' ";
			condition = " pr_code ";
			operation ="更新物料库存信息状态pr_b2cStorestatus->“”";
			break;
		default:
			break;
		}
		try{
			baseDao.updateByCondition(tableName, update, condition+" in ("+ conStr + ")");
		}catch (Exception e){
			insertB2CActionTaskLog(operation, "error", saveError(e.getMessage())+" 编号： "+conStr,masterName);
		}
	}
	/**
	 * 初始化 仓库勾选
	 * @param masterName
	 * */
	public void initWarehouseStatus(String masterName){
		//判断是否初始化过  b2cInitStatus
		if(!baseDao.checkIf("warehouse","wh_ismallstore = 1 and wh_statuscode='AUDITED' and wh_status='已审核'")){//没有勾选勾选了
			if(!baseDao.checkIf("configs","CODE='b2cInitStatus' AND CALLER='sys' AND DATA<>0")){
				//取出历史出货仓库
				List<Object> whHistory = baseDao.getFieldDatasByCondition("prodiodetail left join productwh on pd_whcode = pw_whcode left join warehouse on wh_code = pd_whcode","distinct pd_whcode"," wh_statuscode='AUDITED' and wh_status='已审核' order by pd_whcode");
				if(!CollectionUtil.isEmpty(whHistory)){
					logger.info(this.getClass() + " start b2cInit");
					baseDao.execute(INITCHECK+getWhCode(whHistory)+") ");//自动勾选
					baseDao.execute(UPDATEINITSTATUS);
					insertB2CActionTaskLog("b2cInitStatus", "end", "更新初始化状态：b2cInitStatus",masterName);
					logger.info(this.getClass() + " end b2cInit");
				}
			}
		}
	}
		/**
	 *	单位转换：KPCS->PCS  KG->G
	 *  涉及：空闲库存（reserve）、单价（price）、最小包装量（minPackQty）、最小起订量（minBuyQty）、
	 *  @param List<ProductDetailERP> prod
	 *  @param masterName
	 */
	public List<ProductDetailERP> transferProductDetailERP(List<ProductDetailERP> prod,String masterName){
		String unit = "";
		if(!CollectionUtil.isEmpty(prod)){
			String code = "";
			try{
				Object currency = b2CStoreService.getCurrency(masterName);
				Object rate = baseDao.getFieldDataByCondition(masterName+".currencysmonth","CM_CRRATE","CM_CRname='"+currency+"' and rownum = 1  order by cm_yearmonth desc");
				DecimalFormat    df   = new DecimalFormat("######0.000000");  
				for(ProductDetailERP p :prod){
					code = p.getCode();
					unit = String.valueOf(baseDao.getFieldDataByCondition(masterName+".product", "pr_unit", "pr_code="
							+ "'"+p.getCode()+"'")).toUpperCase();
					Double price = ifnull(p.getPrice());
					p.setPrice(price/(rate==null?1.0:Double.parseDouble(rate.toString())));
					if(unit.equals("KPCS")||unit.toString().equals("KG")){//统一单位
						p.setMinPackQty(ifnull(p.getMinPackQty())*1000);
						p.setMinBuyQty(ifnull(p.getMinBuyQty())*1000);
						p.setReserve(ifnull(p.getReserve())*1000);
						p.setPrice(Double.valueOf(df.format((price)/1000)));
					}
				}
			}catch (Exception e){
				insertB2CActionTaskLog("单位转换", "error",saveError(e.getMessage())+"编号： "+code,masterName);
			}
		}
		return prod;
	}
	/**
	 * 获取仓库编号
	 * */
	public String getWhCode(List<Object> whHistory){
		String whCodeStr ="'";
		if(!CollectionUtil.isEmpty(whHistory)){
			for (int i = 0; i < whHistory.size(); i++) {
				whCodeStr = whCodeStr + whHistory.get(i).toString() +"','";
			}
		}
		return whCodeStr==null?"":whCodeStr.substring(0,whCodeStr.length() - 2);
	}
	/**	
	 *	得到交易信息 codeStr
	 */
	public String getDetailCodeStr(List<ProductDetailERP> productDetail){
		String codeStr = "'";
		if(!CollectionUtil.isEmpty(productDetail)){
			for (ProductDetailERP prod : productDetail) {
				codeStr+=prod.getCode().toString()+"','";
			}
		}
		return codeStr=="'" ? "" : codeStr.substring(0,codeStr.length() - 2);
	}
	/**	
	 *	得到交易信息 codeStr
	 */
	public String getProdCodeStr(List<Prod> prods){
		String codeStr = "'";
		if(!CollectionUtil.isEmpty(prods)){
			for (Prod prod : prods) {
				codeStr+=prod.getPr_code().toString()+"','";
			}
		}
		return codeStr=="'" ? "" : codeStr.substring(0,codeStr.length() - 2);
	}
	/**	
	 *	得到idStr
	 */
	public String getIdStr(List<Prod> prods){
		String idStr = "";
		if(!CollectionUtil.isEmpty(prods)){
			for (Prod prod : prods) {
				idStr+=prod.getPr_id().toString()+",";
			}
		}
		return idStr == "" ? "":idStr.substring(0,idStr.length() - 1);
	}
	
	/**
	 *	获取未上传的物料信息
	 */
	public List<Prod> getProducts(String masterNamr) {
		try {
			List<Prod> prods = baseDao.query(GETPRODUCTSQL.replace("MASTERNAME", masterNamr), Prod.class);
			return prods;
		} catch (Exception e) {
			insertB2CActionTaskLog("物料信息获取", "error",saveError(e.getMessage()),masterNamr);
			return null;
		}
	}
	/**
	 *	获取未上传的物料信息总物料数量和不符合标准的物料数量
	 */
	public Map<String, Object>  getProductsCount(String masterName) {
		Map<String, Object>  res = new HashMap<String, Object>();
		try {
			int scount = baseDao.getCount(PRODUCTALLCOUNT);
			int ecount = baseDao.getCount(PRODUCTERRORCOUNT);
			res.put("acount", scount);
			res.put("ecount", ecount);
			return res;
		} catch (Exception e) {
			insertB2CActionTaskLog("统计仓库物料信息数量", "error", saveError(e.getMessage()),masterName);
			return res;
		}
	}
	/**
	 *	获取初始化物料交易信息
	 */
	public List<ProductDetailERP> getProductDetail(String masterName) {
		try {
			List<ProductDetailERP> prods = baseDao.query(GETPRODUCTDETAILSQL, ProductDetailERP.class);
			return transferProductDetailERP(prods, masterName);
		} catch (Exception e) {
			insertB2CActionTaskLog("物料交易信息获取", "error", saveError(e.getMessage()),masterName);
			return null;
		}
	}
	/**
	 *	获取空闲库存信息
	 */
	public List<ProductDetailERP> getProductReserve(String masterName) {
		try {
			List<ProductDetailERP> prods = baseDao.getJdbcTemplate().query(UPDATERESERVESQL,
					new BeanPropertyRowMapper<ProductDetailERP>(ProductDetailERP.class));
			return transferProductDetailERP(prods, masterName);
		} catch (Exception e) {
			insertB2CActionTaskLog("空闲库存获取", "error", saveError(e.getMessage()),masterName);
			return null;
		}
	}
	/** 
	 * 是否支持开通商城轮询功能
	 * */
	public boolean isB2CMAll(Master master){
		return master.getMa_uu()!=null&&master.getMa_accesssecret()!=null&&master.getMa_function().indexOf("资料中心")<0&&master.getEnv()!=null&&master.getEnv().equals("prod")&&master.getMa_user().toUpperCase().indexOf("DATACENTER")<0;
	}
	/**
	 * 记录操作日志
	 * @param ta_operation 操作
	 * @param ta_finishstatus 操作状态  start；ongoing；end；error
	 * @param ta_errlog 操作说明
	 * */
	public void insertB2CActionTaskLog(String ta_operation,String ta_finishstatus, String ta_errlog){
		try{
			int id = baseDao.getSeqId("B2C$TASK_SEQ");
			baseDao.getJdbcTemplate().update(INSERTB2CTASKLOG,new Object[] {id,ta_operation,ta_finishstatus});
			baseDao.saveClob("B2C$TASK", "TA_ERRLOG2", ta_errlog, "ta_id="+id);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 记录操作日志
	 * @param ta_operation 操作
	 * @param ta_finishstatus 操作状态  start；ongoing；end；error
	 * @param ta_errlog 操作说明
	 * @param ta_master 账套名
	 * */
	public void insertB2CActionTaskLog(String ta_operation,String ta_finishstatus, String ta_errlog,String masterName){
		try{
			int id = baseDao.getSeqId("B2C$TASK_SEQ");
			baseDao.getJdbcTemplate().update(INSERTB2CTASKLOGWITHMASTER,new Object[] {id,ta_operation,ta_finishstatus,masterName});
			baseDao.saveClob("B2C$TASK", "TA_ERRLOG2", ta_errlog, "ta_id="+id);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 记录单据相关操作日志
	 * @param ta_docaller 单据caller或表名
	 * @param ta_doid 单据id
	 * @param ta_operation 操作
	 * @param ta_finishstatus 操作状态 start；ongoing；end；error
	 * @param ta_errlog 操作说明
	 * @param masterName 账套名
	 * */
	public void insertB2CScmTaskLog(String ta_docaller, Integer ta_doid, String ta_operation, String ta_finishstatus, String ta_errlog,String masterName){
		try{
			int id = baseDao.getSeqId("B2C$TASK_SEQ");
			baseDao.getJdbcTemplate().update(INSERTB2CSCMTASKLOG,new Object[] {id,ta_docaller,ta_doid,ta_operation,ta_finishstatus,masterName});
			baseDao.saveClob("B2C$TASK", "TA_ERRLOG2", ta_errlog, "ta_id="+id);
		}catch(Exception e){
				e.printStackTrace();
			}
		}
	/**
	 * 记录单据相关操作日志
	 * @param ta_docaller 单据caller或表名
	 * @param ta_doid 单据id
	 * @param ta_docode 单据code
	 * @param ta_operation 操作
	 * @param ta_finishstatus 操作状态 start；ongoing；end；error
	 * @param ta_errlog 操作说明
	 * @param masterName 账套名
	 * */
	public void insertB2CScmTaskLog(String ta_docaller, Integer ta_doid, String ta_docode,String ta_operation, String ta_finishstatus, String ta_errlog,String masterName){
		try{
			int id = baseDao.getSeqId("B2C$TASK_SEQ");
			baseDao.getJdbcTemplate().update(INSERTB2CSCMCODETASKLOG,new Object[] {id,ta_docaller,ta_doid,ta_docode,ta_operation,ta_finishstatus,masterName});
			baseDao.saveClob("B2C$TASK", "TA_ERRLOG2", ta_errlog, "ta_id="+id);
		}catch(Exception e){
				return;
			}
		}
	/**
	 * 发送任务
	 * @param caller 单据caller或表名
	 * @param url 单据id
	 * @param code 单据code
	 * @param id 操作
	 * @param orderCode 操作状态 start；ongoing；end；error
	 * @param title 操作说明
	 * @param content 操作说明
	 * @param receivercode 操作说明
	 * @param senderuu 操作说明
	 * @param masterName 账套名
	 * */
	public void sendB2cTask (String caller,String url,String code,int id,String orderCode,String title,String content,String receivercode,String senderuu,String masterName){
		String res = baseDao.callProcedure("SP_CREATETASK", new Object[] { id,code,caller,title,content,url,receivercode,senderuu });
		if(!res.equals("OK")){
			insertB2CActionTaskLog(caller+"||发送任务通知", "error", "发送给"+receivercode+" 订单号【"+code+"】 商城单号："+orderCode+" 通知失败："+res,masterName);
		}else{
			insertB2CActionTaskLog(caller+"||发送任务通知", "sucess", "发送"+receivercode+" 订单号【"+code+"】商城单号："+orderCode+" 通知"+res,masterName);
		}
	}
	/**
	 * 记录报错前2000
	 * */
	public String saveError(String error){
		return error.length()>2000?error.substring(0,2000):error;
	}
	/**
	 * 记录报错前2000
	 * */
	public String saveError(Exception e){
		String error = e.getMessage();
		if(error==null){
			return "运行异常";
		}else {
			return error.length()>2000?error.substring(0,2000):error;
		}
	}
	/**
	 * 
	 * */
	public  String b2cContentUnkonw(Object v){
		return v == null? "不详":v.toString();
	}
	/**
	 * List<Object>数组转换long数组
	 * */
	public  long[] TransObjToLong(List<Object> lo){
		long[] l = new long[lo.size()];
		if(lo.size()>0){
			for (int i = 0; i < lo.size(); i++) {
				l[i] = Long.valueOf(lo.get(i).toString());
			}
		}
		return l;
	}
	/**
	 * 空字段处理
	 * */
	private Double ifnull(Double d){
		if (d!=null) return d;
		else return 0.0;
	}
	
	
}
