package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.SysPrintSetDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Page;
import com.uas.erp.model.SysPrintSet;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaCommonService;

@Service("pdaCommonServiceImpl")
public class PdaCommonServiceImpl implements PdaCommonService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Autowired
	private SysPrintSetDao sysPrintSetDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	
	@Override
	public Page<Map<String, Object>> getProdInData(String inoutNo,String whcode,int pi_id) {
		return pdaCommonDao.getInOutData("pd_inqty", inoutNo, whcode,pi_id);		
	}
	
	public String saveBarcode1(String data){
		//提交校验：1、料号正确，存在于该入库单
		//2、barcode不能重复
		//3、总数必须等于pd_inqty
		SqlRowList rs;
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);		
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+map.get("bi_piid")+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据已过账或不在录入状态");
		}	
		//判断总数不能超过入库数量
		rs = baseDao.queryForRowSet("select count(1) cn from ProdIODetail where pd_id ="+map.get("bi_pdid")+" and NVL(pd_inqty,0)<NVL(pd_barcodeinqty,0)+"+Double.valueOf(map.get("bi_barcodeinqty").toString()));
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"采集条码数量超过入库数");
		}		
		if(map.get("bi_barcode") != null && !"".equals(map.get("bi_barcode"))){
			cn =  baseDao.getCount("select count(1) cn from barcodeio where bi_piid="+map.get("bi_piid")+" and bi_barcode ='"+map.get("bi_barcode")+"'");
			if(cn > 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码编号：["+rs.getString("bi_barcode")+"]重复");
			}
		}
		if(map.get("bi_outboxcode") != null && !"".equals(map.get("bi_outboxcode"))){
			cn =  baseDao.getCount("select count(0) cn from barcodeio where bi_piid="+map.get("bi_piid")+" and bi_barcode ='"+map.get("bi_outboxcode")+"'");
			if(cn > 0){	
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号条码：["+rs.getString("bi_outboxcode")+"]编号重复");
			}
			rs = baseDao.queryForRowSet("select pa_id from package where pa_outboxcode='"+map.get("bi_outboxcode")+"'");
			if(rs.next()){
				map.put("bi_outboxid", rs.getString("pa_id"));
			}
		}
		rs = baseDao.queryForRowSet("select pd_prodcode,count(0) cn from ProdIODetail left join ProdInOut on pi_id=pd_piid where pd_prodcode <> '" +map.get("bi_prodcode").toString()+"' and pd_id="+map.get("bi_pdid")+" and pi_id ="+ map.get("bi_piid"));
		if(rs.next() && rs.getInt("cn")>0){	
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"物料["+rs.getString(0)+"],不存在该单据中");
		}		   								
		map.remove("bi_prodname");
		map.put("bi_id",baseDao.getSeqId("BARCODEIO_SEQ"));	
		Object [] obs = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid left join batch on ba_code=pd_batchcode",  new String []{"pi_class","ba_id","PD_BATCHCODE"}, "pi_id="+map.get("bi_piid")+" and pd_id="+map.get("bi_pdid"));
		map.put("bi_piclass",obs[0].toString()) ;
		map.put("bi_PDAget", 1);
		if(obs[1] != null ){
			map.put("bi_batchid", obs[1].toString());
			map.put("bi_batchcode", obs[2].toString());
		}	
		//插入采集的条码
		String gridSql = SqlUtil.getInsertSqlByMap(map,"BARCODEIO");
		baseDao.execute(gridSql);
		baseDao.updateByCondition("ProdIODetail", "pd_barcodeinqty=NVL(pd_barcodeinqty,0)+"+Double.valueOf(map.get("bi_inqty").toString()), "pd_id="+map.get("bi_pdid"));	
		return null;	
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String saveBarcode(String data) {
		//提交校验：1、料号正确，存在于该入库单
		//2、barcode不能重复
		//3、总数必须等于pd_inqty
		SqlRowList rs;
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);		
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+gstore.get(0).get("bi_piid")+" and (pd_status= 99 OR pd_auditstatus in('ENTERING','在录入')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或在录入状态");
		}	
		for (Map<Object, Object> map : gstore) {
			if(map.get("bi_barcode") != null && !"".equals(map.get("bi_barcode"))){
				rs =  baseDao.queryForRowSet("select  bi_barcode ,count(0) cn from barcodeio where bi_piid="+map.get("bi_piid")+" and bi_barcode ='"+map.get("bi_barcode")+"' group by bi_barcode");
				if(rs.next()){
					if(rs.getInt("cn")>0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码编号：["+rs.getString("bi_barcode")+"]重复");
				}
			}
			if(map.get("bi_outboxcode") != null && !"".equals(map.get("bi_outboxcode"))){
				rs =  baseDao.queryForRowSet("select  bi_outboxcode ,count(0) cn from barcodeio where bi_piid='"+map.get("bi_piid")+"'and bi_barcode ='"+map.get("bi_outboxcode")+"' group by bi_outboxcode");
				if(rs.next()){
					if(rs.getInt("cn")>0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号条码：["+rs.getString("bi_outboxcode")+"]编号重复");
				}
				rs = baseDao.queryForRowSet("select　pa_id from package where pa_outboxcode='"+map.get("bi_outboxcode")+"'");
				if(rs.next()){
					map.put("bi_outboxid", rs.getString("pa_id"));
				}
			}
		   rs = baseDao.queryForRowSet("select pd_prodcode,count(0) cn from ProdIODetail left join  ProdInOut on pi_id=pd_piid where pd_prodcode <> '" +map.get("bi_prodcode").toString()+"' and pd_id='"+map.get("bi_pdid")+"' and  pi_inoutno ='"+ map.get("bi_inoutno").toString()+"' group by pd_prodcode");
		    if(rs.next()){	
		    	if(rs.getInt("cn")>0)
		    		throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"物料["+rs.getString(0)+"],不存在该单据中");
		    }		   
		}								
		for (Map<Object, Object> map : gstore) {
			map.remove("bi_prodname");
			map.put("bi_id",baseDao.getSeqId("BARCODEIO_SEQ"));	
			Object [] obs = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid left join batch on ba_code=pd_batchcode",  new String []{"pi_class","ba_id","PD_BATCHCODE"}, "pi_id='"+map.get("bi_piid").toString()+"' and pd_id='"+map.get("bi_pdid")+"'");
			map.put("bi_piclass",obs[0].toString()) ;
			map.put("bi_PDAget", 1);
			if(obs[1] != null ){
				map.put("bi_batchid", obs[1].toString());
				map.put("bi_batchcode", obs[2].toString());
			}
			baseDao.updateByCondition("ProdIODetail", "pd_barcodeinqty=NVL(pd_barcodeinqty,0)+"+Double.valueOf(map.get("bi_inqty").toString()), "pd_piid="+map.get("bi_piid") +" and pd_id='"+map.get("bi_pdid")+"'");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,"BARCODEIO");
		baseDao.execute(gridSql);
		rs  = baseDao.queryForRowSet("select count(0) cn from ProdIODetail left join  ProdInOut on pi_id=pd_piid  where pi_id ='"+gstore.get(0).get("bi_piid").toString()+"' and NVL(pd_inqty,0)<NVL(pd_barcodeinqty,0)");
		if(rs.next()){
			if(rs.getInt("cn")>0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"总数超过入库数");	   
			}
		}		
		return null;
	}

	@Override
	public void checkMakeSerial(String ms_sncode,String makeCode,String whcode) {
		//如果有设定的归属工单，必须序列号归属于选定的工单（关联MakeSerial表判断）pd_ordercode
		//如果该序列号已经存在库存(barcode表remain>0 )，不允许重复入库。
		SqlRowList rs ;
		if("".equals(makeCode) || makeCode !=null ){
			rs = baseDao.queryForRowSet("select count(0) cn from MakeSerial  where ms_sncode='"+ms_sncode+"' and ms_makecode='"+makeCode+"'");
			if(rs.next()){
				if(rs.getInt("cn") == 0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号不属于选定的工单,输入错误");	   
				}
			}
		}
		rs = baseDao.queryForRowSet("select count(0) cn  from Barcode where bar_code='"+ms_sncode+"' and bar_remain>0 and bar_whcode='"+whcode+"'") ;
		if(rs.next()){
			if(rs.getInt("cn") >0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该序列号已经存在库存，不允许重复入库");	   
			}
		}
	}

	@Override
	public void getPackageCode(String pr_fqty,String pa_outboxcode ) {
		//包装箱号，则根据包装表packageDetail获取所有序列号和总数，
		//如果外包装不止一层可能存在递归，如果总数不超过剩余数则执行插入到grid。
		SqlRowList rs ;
		rs = baseDao.queryForRowSet("select count(0) cn from MES_PACKAGE_VIEW where v_outboxcode='"+pa_outboxcode+"' and NVL(v_total,0)>"+Integer.valueOf(pr_fqty));
		if(rs.next()){
			if(rs.getInt("cn") >0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该包装箱内总数超过剩余数");	   
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该包装箱号不存在");	   
		}
		
	}
   
	@Override
	public List<Map<String,Object>> getWhcode (String inoutNo){
		SqlRowList rs0;
		int cn ;
		String no = inoutNo.toLowerCase();
		cn = baseDao.getCount("select count(1) cn from prodinout where lower(pi_inoutno)='"+no+"'");
		if(cn != 0){//判断单号是否存在
			cn = baseDao.getCount("select count(1) from prodinout where lower(pi_inoutno)='"+no+"' and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
			if(cn == 0){//判断单据的状态，必须是未过账，并且不是在录入状态才可以采集
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据:"+inoutNo+"必须在未过账并且不是在录入状态才允许采集");	   
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单号："+inoutNo+"不存在");	   
		}
		rs0 = baseDao.queryForRowSet("select distinct pd_whcode,pi_class,pi_inoutno,pi_id from prodinout left join prodiodetail on pi_id=pd_piid left join product on pr_code=pd_prodcode" 
                   +" where pr_tracekind>0 and lower(pi_inoutno)='"+no+"' and pd_status<>99 and pd_auditstatus <>'ENTERING'");
		if(rs0.next()){		
			return pdaCommonDao.changeKeyToLowerCase(rs0.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据："+inoutNo+",没有需要采集的明细");	   
		}
	}
	
	@Override
	public void clearGet(int id,String whcode) {
		Object ob = baseDao.getFieldDataByCondition("prodinout ","pi_statuscode", "pi_id="+id);
		if(ob != null){
			if(ob.toString().equals("POSTED")){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据已过帐，不允许清空!");
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在！");
		}
		SqlRowList rs1 = baseDao.queryForRowSet("select count(0) cn from barcodeIO where bi_piid="+id+"and bi_pdaget is null");
		if(rs1.next()){
			if(rs1.getInt("cn") > 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"在ERP中采集的数据无法在PDA端清除");
			}
		}
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from barcodeIO where bi_piid="+id+" and bi_whcode='"+whcode+"' and bi_pdaget=1");
		if(rs.next()){
			if(rs.getInt("cn") == 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有需要清空在PDA采集的数据");
			}
		}
		baseDao.execute("delete from barcodeIO where bi_piid=? and bi_whcode=? and bi_pdaget=1",id,whcode);		
		//更新出入库单中明细行中的条码数量
		baseDao.execute("update ProdIODetail set (pd_barcodeinqty,pd_barcodeoutqty)=(select NVL(sum(nvl(bi_inqty,0)),0),NVL(sum(nvl(bi_outqty,0)),0) from barcodeio where bi_pdid=pd_id )"+
		             " where  pd_piid="+id+"and pd_whcode='"+whcode+"'");
		rs = baseDao.queryForRowSet("select count(0)cn from barcodeIo where bi_piid=? and NVL(bi_pdaget,0)=0",id);
		if(rs.next()){
			if(rs.getInt("cn") > 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"已清空从PDA中采集的数据,存在条码从ERP中生成,请在ERP中操作!");
			}
		}
	}

	@Override
	public List<Map<String, Object>> getHaveSubmitList(int bi_piid,String whcode) {
		SqlRowList rs = null,rs0=null;
		rs0 = baseDao
				.queryForRowSet("select max(bi_barcode) bi_barcode, max(bi_outboxcode) bi_outboxcode , sum( bi_outqty) bi_outqty from barcodeio left join product "
						+ "on bi_prodcode=pr_code where bi_piid=?",bi_piid);
		if (rs0.next()) {
			if(rs0.getString("bi_outboxcode") != null && !("").equals(rs0.getString("bi_outboxcode"))){
				if(rs0.getString("bi_outqty")!=null && !("").equals(rs0.getString("bi_outqty")) && rs0.getDouble("bi_outqty")!=0  && rs0.getDouble("bi_outqty")!=-1){
			       /* for (int i = 0; i < rs0.getResultList().size() - 1; i++)
			        {
			            temp = rs0.getResultList().get(i);
			            outboxcode=temp.get("BI_OUTBOXCODE");
			            sum=Double.valueOf( rs0.getResultList().get(i).get("BI_OUTQTY").toString());
			            for (int j = i + 1; j < rs0.getResultList().size(); j++)
			            {
			                if (outboxcode.equals(rs0.getResultList().get(j).get("BI_OUTBOXCODE")))
			                {
			                	double bi_outqty=Double.valueOf( rs0.getResultList().get(j).get("BI_OUTQTY").toString());
			                	sum+=bi_outqty;
			                }
			            }
			        }*/
				}
				rs = baseDao.queryForRowSet("select distinct NVL(bi_barcode,'') bi_barcode,NVL(bi_outboxcode,'') bi_outboxcode,bi_prodcode ,bi_inoutno,bi_piid,bi_pdid,bi_whcode,NVL(bi_inqty,0) bi_inqty,NVL(bi_outqty,0) bi_outqty,pr_detail,bi_whcode,pr_id,pr_spec,bi_location,bi_batchcode from barcodeio "
										+"left join product on bi_prodcode=pr_code where bi_piid=?",bi_piid);
				return rs.getResultList();
				 
			}else{
				rs = baseDao
						.queryForRowSet("select NVL(bi_barcode,'') bi_barcode,bi_prodcode ,bi_inoutno,bi_piid,bi_pdid,bi_whcode,NVL(bi_inqty,0) bi_inqty,NVL(bi_outqty,0) bi_outqty,pr_detail,bi_whcode,pr_id,pr_spec,bi_location,bi_batchcode from barcodeio left join product on bi_prodcode=pr_code"
								  + " where bi_piid=?",bi_piid);
				return rs.getResultList();
			}
			}else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有已采集的数据!");
		}
	}
	
	@Override
	public Map<String, Object> getBarIoCheck(String json) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(json);
		SqlRowList rs = baseDao
				.queryForRowSet("select bi_pdno, bi_pdid,bi_id,bi_piid,bi_barcode,bi_batchcode,bi_inqty,bi_prodcode,pr_spec,pr_unit,pd_ordercode,bi_outboxcode from prodinout left join  prodiodetail on pi_id=pd_piid inner join barcodeio on pd_id=bi_pdid and bi_piid=pd_piid "
						+ "left join product on pd_prodcode=pr_code  where pi_id="
						+ map.get("pi_id")
						+ " and bi_barcode='"
						+ map.get("bi_barcode")
						+ "' and bi_whcode='"
						+ map.get("bi_whcode")
						+ "' and (pd_whcode='"
						+ map.get("bi_whcode")
						+ "' OR pi_whcode='" + map.get("bi_whcode") + "')");
		if (rs.next()) {
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码号不存在该入库单号中！");
		}
	}
	
	@Override
	@Transactional
	public void updateBarIoQty(int id,String json) {// 修改条码数量的时候，限制这一行的条码数量总和不能超过入库单明细
		Object status = baseDao.getFieldDataByCondition("Prodinout", "pi_statuscode", "pi_id=" + id);
		if (status.toString().equals("POSTED")) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"只能修改未过账单据!");
		}
		Map<Object, Object> map = FlexJsonUtil.fromJson(json);
		map.remove("pr_spec");
		map.remove("pr_unit");
		map.remove("pd_ordercode");
		Object bi_pdid = map.get("bi_pdid");
		baseDao.updateByCondition("ProdIODetail",
				"pd_barcodeinqty=NVL(pd_barcodeinqty,0)+" + Double.valueOf(map.get("bi_inqty").toString()), "pd_piid="
						+ map.get("bi_piid").toString() + " and pd_id='" + bi_pdid + "'");
		String sql = SqlUtil.getUpdateSqlByFormStore(map, "barcodeIo", "bi_id");
		baseDao.execute(sql);
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from ProdIODetail left join  ProdInOut on pi_id=pd_piid  where pi_id ="
				+ id + " and NVL(pd_inqty,0)< (select NVL(sum(NVL(bi_inqty,0)),0) bi_inqty from barcodeio where bi_piid=" + id
				+ "and bi_pdid=" + bi_pdid + ") ");
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"总数超过入库数");
			}
		}
	}

	@Override
	public List<Map<String, Object>> getBarIoBoxCheck(String json) {
		Map<Object,Object> map =  BaseUtil.parseFormStoreToMap(json);
		SqlRowList rs = baseDao.queryForRowSet("select bi_pdno, bi_pdid,bi_id,bi_piid,v_barcode as bi_barcode,bi_batchcode,'1' pd_innerqty,v_total,bi_prodcode,pr_spec,pr_unit,pd_ordercode,bi_outboxcode"+
				" from prodinout left join  prodiodetail on pi_id=pd_piid inner join barcodeio on pd_id=bi_pdid and bi_piid=pd_piid "+
		        " left join product on pd_prodcode=pr_code  inner join MES_PACKAGE_VIEW  on bi_outboxcode=v_outboxcode where pi_id="+map.get("pi_id")+" and bi_outboxcode='"+map.get("bi_outboxcode")+"' and bi_whcode='"+map.get("bi_whcode")+"' and (pd_whcode='"+map.get("bi_whcode")+"' OR pi_whcode='"+map.get("bi_whcode")+"')");
		if(rs.next()){
				return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号不存在该入库单号中！");	   
		}
	}

	/**
	 * 获取需要采集的明细
	 */
	@Override
	public List<Map<String,Object>> getNeedGetList(int id,String whcode,String type) {
		Object ob = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_id="+id+" and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
		if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在或状态不是已提交或已审核状态");	   
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+id+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或是在录入状态");	   
		}
		String sql = null;
		if(type.equals("IN")){
			sql = ("select pd_prodcode,pd_restqty,pr_detail,pr_spec,pr_zxbzs,pr_ifbarcodecheck,pd_whcode,pd_piid,pd_inoutno,pr_id from (select pd_prodcode,sum(pd_inqty)- nvL(sum(pd_barcodeinqty),0) pd_restqty,max(pd_whcode)pd_whcode,max(pd_piid)pd_piid ,max(pd_inoutno)pd_inoutno from prodiodetail  where pd_piid="+id+" and pd_whcode='"+whcode+"' group by pd_prodcode having sum(pd_inqty)- nvL(sum(pd_barcodeinqty),0)>0) T left join product on pr_code=pd_prodcode where pr_tracekind>0 ");
		}else if(type.equals("OUT")){
			sql = ("select pd_prodcode,pd_restqty,pr_detail,pr_spec,pd_whcode,pd_piid,pd_inoutno,pr_id from (select pd_prodcode,sum(pd_outqty)- nvL(sum(pd_barcodeoutqty),0) pd_restqty,max(pd_whcode)pd_whcode,max(pd_piid)pd_piid ,max(pd_inoutno)pd_inoutno from prodiodetail  where pd_piid="+id+" and pd_whcode='"+whcode+"' group by pd_prodcode having sum(pd_outqty)- nvL(sum(pd_barcodeoutqty),0)>0) T left join product on pr_code=pd_prodcode where pr_tracekind>0 ");
		}
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有需要采集的明细，或已经采集完成");	   
		}
	}

	@Override
	public String returnPdfUrl(HttpServletRequest request,String caller,String id,String reportName) {
	        SysPrintSet sysPrintSet = sysPrintSetDao.getSysPrintSet(caller,
					reportName);// 打印设置
			Enterprise enterprise = enterpriseDao.getEnterprise();// 企业信息
			String printurl = null;
			// 记录操作
			Map<String, String> params = new HashMap<String, String>();
			if(enterprise.getEn_Admin() == null ||!"jasper".equals(enterprise.getEn_Admin())){
				boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_intrajasperurl());
				if (accessible) {
					printurl = enterprise.getEn_intrajasperurl();//打印地址
				} else {
					printurl = enterprise.getEn_extrajasperurl();
				}
			}else{
				boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
				if (accessible) {
					printurl = enterprise.getEn_printurl();//打印地址
				} else {
					printurl = enterprise.getEn_Url();
				}
			}	
			params.put("userName", enterprise.getEn_whichsystem());// 当前账套的用户名
			params.put("reportName", sysPrintSet.getReportname());// 报表的名称
			if(("BarStock!BarcodePrint").equals(caller)){
				params.put("whereCondition", " where bdd_id in(" + id + ")");// 条件				
			} else if(("Barcode!Print").equals(caller)){
				params.put("whereCondition", " where bar_id in(" + id + ")");// 条件		
			}
			params.put("printtype", sysPrintSet.getPrinttype() == null ? "": sysPrintSet.getPrinttype());// 输出方式
			
			String url = printurl+"/pdfData?userName="+enterprise.getEn_whichsystem()+"&reportName="+sysPrintSet.getReportname()+"&whereCondition="+params.get("whereCondition");
			return url;
	}

	@Override
	public List<Map<String, Object>> getFieldsDatas(String field, String caller, String condition) {
		StringBuffer sb = new StringBuffer("SELECT ");
		sb.append(field);
		sb.append(" FROM ");
		sb.append(caller);
		sb.append(" WHERE ");
		sb.append(condition);
		SqlRowList list = baseDao.queryForRowSet(sb.toString());
		if(list.next()){
			return list.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"未找到相关数据");	
		}
	}				
}
