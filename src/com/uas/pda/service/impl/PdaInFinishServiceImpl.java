package com.uas.pda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaInFinishService;

@Service("pdaInFinishServiceImpl")
public class PdaInFinishServiceImpl implements PdaInFinishService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	
	@Override
	public List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode) {
		SqlRowList rs;
		inoutNo = inoutNo.toLowerCase();
		if (StringUtil.hasText(whcode)) {
			rs = baseDao
					.queryForRowSet("select * from ( select pi_inoutno from prodinout left join prodiodetail on pi_id=pd_piid left join product on pr_code=pd_prodcode where pi_statuscode <>'POSTED' and pi_invostatuscode <>'ENTERING' and nvl(pr_tracekind,0)>0 and pd_whcode=?"
							+ " and "
							+ "lower(pi_inoutno) like '%"+inoutNo
							+ "%' and pd_inqty>0 group by pi_inoutno order by pi_inoutno desc) where rownum<10 ",whcode);
		} else {
			rs = baseDao
					.queryForRowSet("select * from ( select pi_inoutno from prodinout left join prodiodetail on pi_id=pd_piid left join product on pr_code=pd_prodcode where pi_statuscode <>'POSTED' and pi_invostatuscode <>'ENTERING' and nvl(pr_tracekind,0)>0 and "
							+ "lower(pi_inoutno) like '%"+inoutNo
							+ "%' and pd_inqty>0 group by pi_inoutno order by pi_inoutno desc) where rownum<10 ");
		}
		if (rs.next()) {
			return rs.getResultList();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getProdIn(String inoutNo, String whcode) {
		return pdaCommonDao.getProdInOut("pd_inqty", inoutNo, whcode);
	}

	@Override
	public Map<String, Object> getNeedGetList(Long pi_id, String whcode) {
		Map<String,Object> map = new HashMap<String,Object>();
		Object ob = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_id="+pi_id+" and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
		if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在或状态不是已提交或已审核状态");
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+pi_id+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或是在录入状态");
		}
		String sql = " select count(1)cn from prodiodetail left join product on pr_code=pd_prodcode where pd_piid=? and pd_whcode=? and pr_tracekind>0 and nvl(pd_inqty,0)>nvl(pd_barcodeinqty,0)";
		SqlRowList rs1 = baseDao.queryForRowSet(sql,pi_id,whcode);
		if(rs1.next() && rs1.getInt("cn")>0){
			map.put("message", "success");
		}else{
			map.put("message", "没有需要采集的明细，或已经采集完成");
		}
		SqlRowList rs = baseDao.queryForRowSet("select pd_ordercode,pd_prodcode,pd_restqty,pr_detail,pr_spec,"
								+" pd_whcode,pd_piid,pd_inoutno,pr_id from (select pd_ordercode,pd_prodcode,sum(pd_inqty)-nvL(sum(pd_barcodeinqty),0) pd_restqty,"
								+" max(pd_whcode)pd_whcode,max(pd_piid)pd_piid ,max(pd_inoutno)pd_inoutno from prodiodetail  "
								+" where pd_piid=? and pd_whcode=? group by pd_ordercode ,pd_prodcode "
								+" having sum(pd_inqty)- nvL(sum(pd_barcodeinqty),0)>0) T left join product "
								+" on pr_code=pd_prodcode and pr_tracekind>0",pi_id,whcode);
		if(rs.next()){
			map.put("data", rs.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
		}
		return map;
	}

	@Override
	public Map<String, Object> save(Long pd_piid, String whcode, String barcode, String kind) {
		SqlRowList rs, rs0;
		Map<String, Object> rMap = new HashMap<String, Object>();		
		String pr_code,ma_code = null;
		double remain = 0;
		Object[] obs = baseDao.getFieldsDataByCondition("prodinout", new String[]{"pi_id","pi_inoutno","pi_class"}, "pi_id="+pd_piid+" and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
		if(obs == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在或状态不是已提交或已审核状态");
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+pd_piid+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或是在录入状态");
		}
		if("sncode".equals(kind)){//序列号makeserial ,是否已经完工ms_status=2 
			//判断序列号是否存在，
			rs = baseDao.queryForRowSet("select ms_status,ms_makecode,ms_outboxcode,nvl(ms_iostatus,0) ms_iostatus,pr_code,pr_id,ms_enddate,pr_detail from makeserial left join product on pr_code=ms_prodcode where ms_sncode=?",barcode);
			if(rs.next()){
				//此处不能用object  会显示 System.out.println(!"2".equals(2)); 为true
				if(!"2".equals(rs.getString("ms_status"))){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]未完工或无效");
				}
				if(!"0".equals(rs.getString("ms_iostatus"))){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]已入库");
				}
				if(rs.getObject("ms_outboxcode") != null){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]已装包["+rs.getObject("ms_outboxcode")+"]，不允许单个采集入库");
				}
				ma_code = rs.getString("ms_makecode");
				pr_code = rs.getString("pr_code");
				rs0 = baseDao.queryForRowSet("select pd_ordercode,sum(pd_inqty)-sum(nvl(pd_barcodeinqty,0)) pd_restqty from prodiodetail where pd_piid=? and pd_whcode=? and pd_ordercode=? group by pd_ordercode",pd_piid,whcode,ma_code);
				if(rs0.next()){//判断是否已经采集完成
					if(rs0.getGeneralDouble("pd_restqty")<=0){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]所属工单["+ma_code+"]在该入库单中已经完成采集");
					}
				}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"序列号["+barcode+"]所属工单["+ma_code+"]不存在该入库单中");
				}
				//判断是否已采集
				rs0 = baseDao.queryForRowSet("select * from barcodeio where bi_barcode=? and bi_piid=?",barcode,pd_piid);
				if(rs0.next()){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]已采集，请勿重复采集");
				}
				//插入数据至barcodeio 表
				baseDao.execute("insert into barcodeio(bi_id,bi_barcode,bi_prodcode,bi_prodid,bi_inoutno"
						       + ",bi_piid,bi_piclass,bi_inqty,bi_pdaget,bi_whcode)"
						       + "values(BARCODEIO_SEQ.nextval,?,?,?,?,?,?,1,1,?)",barcode,pr_code,rs.getObject("pr_id"),obs[1],pd_piid,obs[2],whcode);
				
				rs0 = baseDao.queryForRowSet("select rownum, pd_id from prodiodetail where pd_piid=? and pd_ordercode=? and pd_whcode=?"+
									"and pd_inqty-nvl(pd_barcodeinqty,0)>0 and rownum=1 order by pd_pdno asc",pd_piid,ma_code,whcode);
				if(rs0.next()){
					baseDao.execute("update prodiodetail set pd_barcodeinqty=nvl(pd_barcodeinqty,0)+1 where pd_id=?",rs0.getInt("pd_id"));
				}		
				rMap.put("BAR_CODE", barcode);
				rMap.put("PR_CODE", pr_code);
				rMap.put("PR_DETAIL", rs.getString("pr_detail"));
				rMap.put("BAR_REMAIN",1);
				rMap.put("MA_CODE", ma_code);
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"序列号["+barcode+"]不存在");
			}			
		}else if("package".equals(kind)){//采集箱号
			rs = baseDao.queryForRowSet("select pa_makecode,pa_outboxcode,pa_totalqty,pa_whcode,pa_prodcode,pr_detail,pr_spec,pa_id,pa_printcount,pr_id from package left join product on pr_code=pa_prodcode where pa_outboxcode=? and pa_whcode=? and pa_type<>3",barcode,whcode);
			if(rs.next()){
				remain = rs.getGeneralDouble("pa_totalqty");
				pr_code = rs.getString("pa_prodcode");
				ma_code = rs.getString("pa_makecode");
				if(remain<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "箱内总数为0");
				}
				if(rs.getInt("pa_printcount")<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "未封装完成不允许采集");
				}
				SqlRowList rs2 = baseDao.queryForRowSet("select count(1) cn from barcodeio where bi_outboxcode=? and bi_prodcode=? and bi_piid=? and nvl(bi_status,0)<>99",barcode,pr_code,pd_piid);
				if (rs2.next() && rs2.getInt("cn")>0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "重复采集");
				}							
				rs2 = baseDao
						.queryForRowSet("select sum(pd_inqty)-nvl(sum(pd_barcodeinqty),0) restqty,count(1)cn from ProdIODetail where pd_piid=? and pd_ordercode=? and pd_whcode=?",pd_piid,ma_code,whcode);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					if (rs2.getDouble("restqty") <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号所属工单[" + ma_code + "]已经采集完成");
					else{
						if(remain>rs2.getDouble("restqty")){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱内总数["+remain+"]大于需要剩余需要采集数量[" + rs2.getDouble("restqty") + "],如需采集请进行拆箱操作!");
						}
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号所属工单[" + ma_code + "],不存在该入库单中");
				}
				//插入barcodeio 数据
				baseDao.execute("insert into barcodeio(bi_id,bi_outboxcode,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_inqty,bi_prodid,bi_pdaget,bi_outboxid,bi_piid,bi_inoutno,bi_piclass)"
						+ "values(BARCODEIO_SEQ.nextval,?,0,0,?,?,?,?,1,?,?,?,?)",barcode,pr_code,whcode,remain,rs.getInt("pr_id"),rs.getInt("pa_id"),pd_piid,obs[1],obs[2]);						
				
				rMap.put("PA_OUTBOXCODE", barcode);
				rMap.put("PR_CODE", pr_code);
				rMap.put("PR_DETAIL", rs.getString("pr_detail"));
				rMap.put("PA_TOTALQTY",remain);
				rMap.put("MA_CODE", ma_code);
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号:"+barcode+"，不存在!");
			}	
		}else if("pallet".equals(kind)){//栈板
			rs = baseDao.queryForRowSet("select pa_makecode,pa_outboxcode,pa_totalqty,pa_whcode,pa_prodcode,pr_detail,pr_spec,pa_id,pa_printcount,pr_id from package left join product on pr_code=pa_prodcode where pa_outboxcode=? and pa_whcode=? and pa_type=3",barcode,whcode);
			if(rs.next()){
				remain = rs.getGeneralDouble("pa_totalqty");
				pr_code = rs.getString("pa_prodcode");
				ma_code = rs.getString("pa_makecode");
				if(remain<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板号：" + barcode+ "内数量为0");
				}				
				SqlRowList rs2 = baseDao.queryForRowSet("select count(1) cn from barcodeio where bi_outboxcode=? and nvl(bi_inqty,0)>0 and bi_prodcode=? and bi_piid=? and nvl(bi_status,0)<>99",barcode,pr_code,pd_piid);
				if (rs2.next() && rs2.getInt("cn")>0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板号：" + barcode+ "重复采集");
				}	
				rs2 = baseDao
						.queryForRowSet("select sum(pd_inqty)-nvl(sum(pd_barcodeinqty),0) restqty,count(1)cn from ProdIODetail where pd_piid=? and pd_ordercode=? and pd_whcode=?",pd_piid,ma_code,whcode);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					if (rs2.getDouble("restqty") <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板所属工单[" + ma_code + "]已经采集完成");
					else{
						if(remain>rs2.getDouble("restqty")){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板总数["+remain+"]大于需要剩余需要采集数量[" + rs2.getDouble("restqty") + "]!");
						}
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"栈板所属工单[" + ma_code + "],不存在该入库单中");
				}
				//插入barcodeio 数据
				baseDao.execute("insert into barcodeio(bi_id,bi_outboxcode,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_inqty,bi_prodid,bi_pdaget,bi_outboxid,bi_piid,bi_inoutno,bi_piclass)"
						+ "values(BARCODEIO_SEQ.nextval,?,0,0,?,?,?,?,1,?,?,?,?)",barcode,pr_code,whcode,remain,rs.getInt("pr_id"),rs.getInt("pa_id"),pd_piid,obs[1],obs[2]);	
				rMap.put("PA_OUTBOXCODE", barcode);
				rMap.put("PR_CODE", pr_code);
				rMap.put("PR_DETAIL", rs.getString("pr_detail"));
				rMap.put("PA_TOTALQTY",remain);
				rMap.put("MA_CODE", ma_code);
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"栈板号:"+barcode+"，不存在!");
			}	
		}
		if(remain>0){
			// 更入库明细表中的pd_barcodeinqty
			rs0 = baseDao.queryForRowSet("select pd_id,pd_inqty-nvl(pd_barcodeinqty,0) pd_rest from prodiodetail where pd_piid=? and pd_ordercode=? and pd_whcode=? and pd_inqty-nvl(pd_barcodeinqty,0)>0",pd_piid,ma_code,whcode);
			if (rs0.next()) {
				for(Map<String, Object> map : rs0.getResultList()) {
					double rest = Double.valueOf(map.get("pd_rest").toString());
					if (remain > 0) {
						if (rest > 0 && rest < remain) {
							baseDao.execute("update prodiodetail set pd_barcodeinqty=pd_inqty where pd_id=" + map.get("pd_id"));
							remain = NumberUtil.sub(remain, rest);
						} else if (rest > 0 && rest >= remain) {
							baseDao.execute("update prodiodetail set pd_barcodeinqty=nvl(pd_barcodeinqty,0)+" + remain + " where pd_id="
									+ map.get("pd_id"));
							remain = 0;
						}
					}
				}
			}
		}
		return rMap;
	}

	@Override
	public void deleteDetail(Long bi_piid, String barcode, String outboxcode, String whcode) {
		SqlRowList rs0;
		String pr_code = null,ma_code=null;
		Object ob = baseDao.getFieldDataByCondition("prodinout ","pi_statuscode", "pi_id="+bi_piid);
		if(ob != null){
			if(ob.toString().equals("POSTED")){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该单据已过帐，不允许删除已采集数据!");
			}
			Object[] obs = null;
			if(!StringUtils.isEmpty(barcode)){//删除单个序列号
				rs0 = baseDao.queryForRowSet("select ms_prodcode,ms_makecode from makeserial where ms_sncode=?",barcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的序列号："+barcode+"不存在!");
				}
				pr_code = rs0.getString("ms_prodcode");
				ma_code = rs0.getString("ms_makecode");
				obs = baseDao.getFieldsDataByCondition("barcodeIo left join makeserial on ms_sncode=bi_barcode", new String[]{"bi_pdaget","bi_inqty"}, "bi_piid="+bi_piid+" and bi_barcode='"+barcode+"' and bi_whcode='"+whcode+"'");
				if(obs != null){
					if(obs[0]!=null){
					if (Integer.valueOf(obs[0].toString())!=1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的条码,请在ERP中操作！");
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid="+bi_piid+" and bi_barcode='"+barcode+"'");
					}
				}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的序列号未采集或者未采集至仓库["+whcode+"]中!");
				}
			}else if(!StringUtils.isEmpty(outboxcode)){//删除箱号
				rs0 = baseDao.queryForRowSet("select pa_prodcode,pa_makecode from package where pa_outboxcode=?",outboxcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的箱号或者栈板号："+outboxcode+"不存在!");
				}
				pr_code = rs0.getString("pa_prodcode");
				ma_code = rs0.getString("pa_makecode");
				obs = baseDao.getFieldsDataByCondition("barcodeIo",new String[]{"bi_pdaget","bi_inqty"}, "bi_piid="+bi_piid+" and bi_outboxcode='"+outboxcode+"' and bi_whcode='"+whcode+"'");
				if(obs != null){
					if(obs[0] != null){
					if (Integer.valueOf(obs[0].toString())!=1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的数据,请在ERP中操作！");
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid="+bi_piid+" and bi_outboxcode='"+outboxcode+"'");
					}}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的数据未采集或者未采集至仓库["+whcode+"]中!");
				}			
			}
			if(obs != null){
				double bi_inqty = Double.valueOf(obs[1].toString());
				SqlRowList rs = baseDao.queryForRowSet("select pd_id,pd_barcodeinqty from prodiodetail where pd_piid=? and pd_prodcode=? and pd_ordercode=? and pd_whcode=? and nvl(pd_barcodeinqty,0)>0 order by pd_pdno asc,pd_barcodeinqty desc",bi_piid,pr_code,ma_code,whcode);
				while(rs.next()){
					if(bi_inqty >0){
						if(bi_inqty > rs.getGeneralDouble("pd_barcodeinqty")){
							baseDao.execute("update prodiodetail set PD_BARCODEINQTY =0  where pd_id=?",rs.getInt("pd_id"));
							bi_inqty = NumberUtil.sub(bi_inqty, rs.getGeneralDouble("pd_barcodeinqty"));
						}else {
							baseDao.execute("update prodiodetail set PD_BARCODEINQTY = NVL(pd_barcodeinqty,0) - ?  where pd_id=?",bi_inqty,rs.getInt("pd_id"));
							bi_inqty = 0;
						}		
					}
				}
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该单据不存在或者已删除!");
		}					
	}

	@Override
	public  Map<Object, Object> saveAll(String data) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);	
		Map<Object,Object> returnMap = new HashMap<Object, Object>();
		if(CollectionUtil.isEmpty(gstore)){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要提交的数据");
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+gstore.get(0).get("bi_piid")+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或在录入状态");
		}	
		Object barcode="";
		
		for (Map<Object, Object> map : gstore) {
			try{
				Object bi_piid = map.get("bi_piid");
				Object bi_whcode = map.get("bi_whcode");
				Object bi_outboxcode = map.get("bi_outboxcode");
				Object bi_boxtype = map.get("bi_boxtype");
				if(StringUtil.hasText(bi_piid) && StringUtil.hasText(bi_whcode)){
					if(StringUtil.hasText(map.get("bi_barcode"))){
						barcode=map.get("bi_barcode");
						save(Long.valueOf(bi_piid.toString()), bi_whcode.toString(),map.get("bi_barcode").toString(), "sncode");
						returnMap.put("sncode:"+barcode, "success");
					}else if (StringUtil.hasText(bi_outboxcode) && StringUtil.hasText(bi_boxtype) ){
						barcode=bi_outboxcode;
						save(Long.valueOf(bi_piid.toString()), bi_whcode.toString(), bi_outboxcode.toString(),bi_boxtype.toString());
						returnMap.put(bi_boxtype+":"+barcode, "success");
					}
				}
			}
			catch(Exception e){
				Object bi_boxtype = map.get("bi_boxtype");
				if(map.get("bi_boxtype")==null || map.get("bi_boxtype")==""){
					returnMap.put("sncode"+barcode, e.getMessage());
				}else{
					returnMap.put(bi_boxtype+":"+barcode, e.getMessage());
				}
			}
		}		
		return returnMap;
	}
}
