package com.uas.pda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.service.PdaOutFinishService;


@Service("pdaOutFinishServiceImpl")
public class PdaOutFinishServiceImpl implements PdaOutFinishService{
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> fuzzySearch(String inoutNo) {
		if(StringUtil.hasText(inoutNo)){
			inoutNo = inoutNo.toLowerCase();
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请输入出库单号");
		}
		SqlRowList rs;
		rs = baseDao.queryForRowSet("select * from ( select distinct pi_inoutno from prodinout left join prodiodetail on pi_id=pd_piid left join product on pr_code=pd_prodcode where pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING' and nvl(pr_tracekind,0)>0 and "+
                   "lower(pi_inoutno) like '%"+inoutNo+"%' and pd_outqty>0 order by pi_inoutno desc) where  rownum<10 ");
	    if(rs.next()){
			return rs.getResultList();
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> getProdOut(String inoutNo,String pi_class) {
		int cn;
		String no = inoutNo.toLowerCase();
		cn = baseDao.getCount("select count(1) cn from prodinout where lower(pi_inoutno)='" + no + "'");
		if (cn != 0) {// 判断单号是否存在
			cn = baseDao.getCount("select count(1) from prodinout where lower(pi_inoutno)='" + no
					+ "' and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
			if (cn == 0) {// 判断单据的状态，必须是未过账，并且不是在录入状态才可以采集
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单据:" + inoutNo + "必须在未过账并且不是在录入状态才允许采集");
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单号：" + inoutNo + "不存在");
		}
		// 获取单据
		SqlRowList rs;
		if (pi_class != null && !"".equals(pi_class) && !"null".equals(pi_class)) {
			rs = baseDao.queryForRowSet("select distinct '102' enAuditStatus,pi_class,pi_Inoutno,pi_title,pi_id,"
					+ "pi_statuscode,pi_cardcode,pd_whcode from ProdInOut left join ProdIODetail on pi_id=pd_piid left join product on pr_code=pd_prodcode where lower(pi_inoutno)=?"+
				      " and pd_status<>99 and nvl(pd_auditstatus,' ') <>'ENTERING' and pr_tracekind>0 and pi_class=?",no,pi_class);
		} else {
			 rs = baseDao.queryForRowSet("select distinct '102' enAuditStatus,pi_class,pi_Inoutno,pi_title,pi_id,"
					+ "pi_statuscode,pi_cardcode,pd_whcode from ProdInOut left join ProdIODetail on pi_id=pd_piid left join product on pr_code=pd_prodcode where lower(pi_inoutno)=?"+
					      " and pd_status<>99 and nvl(pd_auditstatus,' ') <>'ENTERING' and pr_tracekind>0",no);
		}
		if (rs.next()) {
			for (Map<String, Object> map : rs.getResultList()) {
				int cn1=baseDao.getCount("select count(1)cn  from (select sum(pd_outqty) qty,sum(nvl(pd_barcodeoutqty,0))"
						+ " barcodeqty from prodiodetail left join product on pr_code=pd_prodcode "
                        +"where pd_piid="+map.get("pi_id")+" and  pd_whcode='"+map.get("pd_whcode")+"' and pr_tracekind>0) t where t.qty>t.barcodeqty "
                        		+ "and t.barcodeqty>0");
				if(cn1>0){
					map.put("ENAUDITSTATUS", "103");// 修改采集状态，为采集中
				}
				int a = baseDao.getCount("select count(0) cn from prodiodetail left join product on pr_code=pd_prodcode where pd_piid=" + map.get("pi_id")
						+ " and NVL(pd_barcodeoutqty,0)<NVL(pd_outqty,0) and pr_tracekind>0");
				if (a == 0) {
					map.put("ENAUDITSTATUS", "101");// 修改采集状态，为已采集
				}							
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该单据不存在需要采集的物料");
		}
		return rs.getResultList();
	}
	
	@Override
	public Map<String, Object> getNeedGetList(Long id) {
		 Map<String,Object> map = new HashMap<String,Object>();
		Object ob = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_id="+id+" and pi_statuscode<>'POSTED' and pi_invostatuscode<>'ENTERING'");
		if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在或状态不是已提交或已审核状态");
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+id+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或是在录入状态");
		}
		String sql = " select count(1)cn from prodiodetail left join product on pr_code=pd_prodcode where pd_piid=? and pr_tracekind>0 and nvl(pd_outqty,0)>nvl(pd_barcodeinqty,0)";
		SqlRowList rs1 = baseDao.queryForRowSet(sql,id);
		if(rs1.next() && rs1.getInt("cn")>0){
			map.put("message", "success");
		}else{
			map.put("message", "没有需要采集的明细，或已经采集完成");
		}
		SqlRowList rs = baseDao.queryForRowSet("select pd_prodcode,pd_restqty,pr_detail,pr_spec,"
								+" pd_piid,pd_inoutno,pr_id from (select pd_prodcode,sum(pd_outqty)-nvL(sum(pd_barcodeoutqty),0) pd_restqty,"
								+" max(pd_piid)pd_piid ,max(pd_inoutno)pd_inoutno from prodiodetail "
								+" where pd_piid=? group by pd_prodcode "
								+" having sum(pd_outqty)- nvL(sum(pd_barcodeoutqty),0)>0) T left join product "
								+" on pr_code=T.pd_prodcode and pr_tracekind>0",id);
		if(rs.next()){
			map.put("data", rs.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
		}
		return map;
	}
	@Override
	public void clearGet(Long id) {
		Object ob = baseDao.getFieldDataByCondition("prodinout ","pi_statuscode", "pi_id="+id);
		if(ob!=null){
		if(ob.toString().equals("POSTED")){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单据已过帐，不允许清空!");
		}
		SqlRowList rs1 = baseDao.queryForRowSet("select count(0) cn from barcodeIO where bi_piid="+id+"and bi_pdaget is null");
		if(rs1.next()){
			if(rs1.getInt("cn") > 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"在ERP中采集的数据无法在PDA端清除");
			}
		}
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from barcodeIO where bi_piid="+id+" and bi_pdaget=1");
		if(rs.next()){
			if(rs.getInt("cn") == 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有需要清空在PDA采集的数据");
			}
		}
		baseDao.execute("delete from barcodeIO where bi_piid="+id+" and bi_pdaget=1");		
		//更新出入库单中明细行中的条码数量
		baseDao.execute("update ProdIODetail set pd_barcodeoutqty=(select NVL(sum(nvl(bi_outqty,0)),0) from barcodeio where bi_pdid=pd_id )"+
		             " where  pd_piid="+id);
		rs = baseDao.queryForRowSet("select count(0)cn from barcodeIo where bi_piid=? and NVL(bi_pdaget,0)=0",id);
		if(rs.next()){
			if(rs.getInt("cn") > 0){
				BaseUtil.showErrorOnSuccess("已清空从PDA中采集的数据,存在条码从ERP中生成,请在ERP中操作!");
			}
		}	
		}
	}
	@Override
	public Map<String, Object> save(String barcode, int id, String kind) {
		SqlRowList rs, rs0;
		String pr_code = null,cu_code = null,wh_code=null;
		Map<String,Object> returnMap = new HashMap<String, Object>();
		double remain = 0;
		// 判断单据状态
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id=" + id
				+ " and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if (cn > 0) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或在录入");
		}
		//存在客户编号就自动绑定客户编号
		Object objs[] = baseDao.getFieldsDataByCondition("prodinout", new String[]{"pi_inoutno","pi_class","pi_cardcode"},"pi_id="+id);
		if(objs == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"出库单不存在或已删除！");
		}else{
			if(objs[2] == null){
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"出库单客户编号不存在！");
			}else{
				cu_code = objs[2].toString();
			}
		}
		if("sncode".equals(kind)){
			// 判断序列号是否存在，序列状态
			rs = baseDao
					.queryForRowSet("select ms_id,ms_sncode,ms_prodcode,ms_iostatus,ms_makecode,ms_id,ms_boxtype,ms_whcode,ms_custcode,pr_detail,ms_outboxcode,ms_enddate"
							+ " from makeserial left join product on pr_code=ms_prodcode where ms_sncode=? and ms_custcode=?",barcode,cu_code);
			if (rs.next()) {
				pr_code = rs.getString("ms_prodcode");
				cu_code = rs.getString("ms_custcode");
				wh_code = rs.getString("ms_whcode");
				remain = 1;
				if (rs.getInt("ms_iostatus") != 1) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列[" + barcode + "]未入库无法出库");
				}
				if(rs.getString("ms_outboxcode") != null){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列[" + barcode + "]有归属外箱号["+rs.getString("ms_outboxcode")+"]，不允许单独出货");
				}
				rs0 = baseDao.queryForRowSet("select pd_prodcode,pd_outqty-nvl(pd_barcodeoutqty,0) pd_restqty from prodiodetail  where pd_piid=? and pd_prodcode=? and nvl(pd_outqty,0)-nvl(pd_barcodeoutqty,0)>0 ",id,pr_code);
				if(rs0.next()){//判断是否已经采集完成
					//判断是否已经采集完成
					if(rs0.getGeneralDouble("pd_restqty")<=0){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]所属物料["+pr_code+"]在该出库单中已经完成采集");
					}
				
				}else{
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号["+barcode+"]所属物料["+pr_code+"]不存在该出库单中");
				}
				cn = baseDao.getCount("select count(1) cn from barcodeio where bi_piid="+id+" and bi_whcode='" + wh_code +"' and bi_barcode='" + barcode + "'");
				if (cn > 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"序列号：[" + barcode + "]已采集");
				}
				// 材料出库采集
				baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_piid,bi_inoutno,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_prodid,bi_pdaget,bi_barid,bi_piclass) " 
						+" select BARCODEIO_SEQ.nextval,'"
						+ barcode + "'," + id + ",pi_inoutno,0,0,'" + pr_code + "','" + wh_code + "'," + remain + ","
						+ DateUtil.parseDateToOracleString(null, rs.getDate("ms_enddate")) + ","
						+ rs.getInt("pr_id") + ",1,"+rs.getInt("ms_id")+",pd_piclass from prodiodetail left join prodinout on pi_id = pd_piid where pd_piid=" + id);
				//出库单数量增加
				rs0 = baseDao.queryForRowSet("select rownum, pd_id from prodiodetail where pd_piid=? and pd_prodcode=?"+
						"and pd_outqty-nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",id,pr_code);
				if(rs0.next()){
					baseDao.execute("update prodiodetail set pd_barcodeoutqty=nvl(pd_barcodeoutqty,0)+1 where pd_id=?",rs0.getInt("pd_id"));
				}
				returnMap.put("MS_SNCODE", barcode);
				returnMap.put("MS_PRODCODE", pr_code);
				returnMap.put("REMAIN", remain);			
				returnMap.put("PR_DETAIL", rs.getString("pr_detail"));
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"序列号[" + barcode + "]不存在或不属于客户["+cu_code+"]");
			}
		}else if("package".equals(kind)){//采集箱号,外箱，大箱
			rs = baseDao.queryForRowSet("select pa_custcode,pa_salecode,pa_outboxcode,pa_totalqty,pa_whcode,pa_prodcode,pr_detail,pr_spec,pa_id,pa_status from package left join product on pr_code=pa_prodcode where pa_outboxcode=? and pa_custcode=? and pa_type<>3",barcode,cu_code);
			if(rs.next()){
				remain = rs.getGeneralDouble("pa_totalqty");
				pr_code = rs.getString("pa_prodcode");
				if(remain<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "箱内总数为0");
				}
				if(rs.getInt("pa_status")!=1){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号[" + barcode + "]未入库无法出库");
				}
				SqlRowList rs2 = baseDao.queryForRowSet("select count(1) cn from barcodeio where bi_outboxcode=? and nvl(bi_outqty,0)>0 and bi_status<>99 and bi_prodcode=?",barcode,pr_code);
				if (rs2.next() && rs2.getInt("cn")>0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "重复采集");
				}							
				//包装箱号所属批次已经采集
				rs2 = baseDao
						.queryForRowSet("select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn from ProdIODetail left join ProdInOut on pi_id=pd_piid where pi_id=? and pd_prodcode=?",id,pr_code);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					if (rs2.getDouble("restqty") <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号所属物料[" + pr_code + "]已经采集完成");
					else{
						if(remain>rs2.getDouble("restqty")){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱内总数["+remain+"]大于需要剩余需要采集数量[" + rs2.getDouble("restqty") + "],如需采集请进行拆箱操作!");
						}
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号所属物料[" + pr_code + "],不存在该出库单中");
				}
				//插入barcodeio 数据
				baseDao.execute("insert into barcodeio(bi_id,bi_outboxcode,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_prodid,bi_pdaget,bi_outboxid,bi_piid,bi_piclass)"
						+ " select barcodeio_seq.nextval,pa_outboxcode,0,0,pa_prodcode,pa_whcode,pa_totalqty,pa_packdate,pr_id,1,pa_id,"+id+",'"+objs[1]+"'"
						+ " from package left join product on pr_code=pa_prodcode where pa_outboxcode=?",barcode);						
				
				returnMap.put("PA_OUTBOXCODE", barcode);
				returnMap.put("PA_PRODCODE", pr_code);
				returnMap.put("PA_TOTALQTY", remain);			
				returnMap.put("PR_DETAIL", rs.getString("pr_detail"));
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号:"+barcode+"，不存在!");
			}	
		}else if("pallet".equals(kind)){//栈板
			rs = baseDao.queryForRowSet("select pa_custcode,pa_salecode,pa_outboxcode,pa_totalqty,pa_whcode,pa_prodcode,pr_detail,pr_spec,pa_id,pa_status,pr_id from package left join product on pr_code=pa_prodcode where pa_outboxcode=? and pa_type=3 and pa_custcode=?",barcode,cu_code);
			if(rs.next()){
				remain = rs.getGeneralDouble("pa_totalqty");
				pr_code = rs.getString("pa_prodcode");
				wh_code = rs.getString("pa_whcode");
				if(remain<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板：" + barcode+ "内数量为0");
				}	
				if(rs.getInt("pa_status")!=1){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板[" + barcode + "]未入库无法出库");
				}
				SqlRowList rs2 = baseDao.queryForRowSet("select count(1) cn from barcodeio where bi_outboxcode=? and nvl(bi_outqty,0)>0 and bi_prodcode=? and nvl(bi_status,0)<>99",barcode,pr_code);
				if (rs2.next() && rs2.getInt("cn")>0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板：" + barcode+ "重复采集");
				}							
				rs2 = baseDao
						.queryForRowSet("select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn from ProdIODetail where pd_piid=? and pd_prodcode=?",id,pr_code);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					if (rs2.getDouble("restqty") <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板所属物料[" + pr_code + "]已经采集完成");
					else{
						if(remain>rs2.getDouble("restqty")){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"栈板总数["+remain+"]大于需要剩余需要采集数量[" + rs2.getDouble("restqty") + "]!");
						}
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"栈板所属物料[" + pr_code + "],不存在该出库单中");
				}
				//插入barcodeio 数据
				baseDao.execute("insert into barcodeio(bi_id,bi_outboxcode,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_outqty,bi_prodid,bi_pdaget,bi_outboxid,bi_piid,bi_inoutno,bi_piclass)"
						+ "values(BARCODEIO_SEQ.nextval,?,0,0,?,?,?,?,1,?,?,?,?)",barcode,pr_code,wh_code,remain,rs.getInt("pr_id"),rs.getInt("pa_id"),id,objs[0],objs[1]);	
				
				returnMap.put("PA_OUTBOXCODE", barcode);
				returnMap.put("PA_PRODCODE", pr_code);
				returnMap.put("PA_TOTALQTY", remain);			
				returnMap.put("PR_DETAIL", rs.getString("pr_detail"));
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"栈板号:"+barcode+"，不存在!");
			}				
		}
		if(remain>0){
			// 更新出库明细表中的pd_barcodeouqty
			rs0 = baseDao.queryForRowSet("select pd_id,pd_outqty-nvl(pd_barcodeoutqty,0) pd_rest from prodiodetail where pd_piid=" + id
					+ " and pd_prodcode=? and nvl(pd_outqty,0)-nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",pr_code);
			if (rs0.next()) {
				for (Map<String, Object> map : rs0.getResultList()) {
					double rest = Double.valueOf(map.get("pd_rest").toString());
					if (remain > 0) {
						if (rest > 0 && rest < remain) {
							baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_id=" + map.get("pd_id"));
							remain = NumberUtil.sub(remain, rest);
						} else if (rest > 0 && rest >= remain) {
							baseDao.execute("update prodiodetail set pd_barcodeoutqty=nvl(pd_barcodeoutqty,0)+" + remain + " where pd_id="
									+ map.get("pd_id"));
							remain = 0;
						}
					}
				}
			}
		}
		return returnMap;
	}
	
	
	@Override
	public List<Map<String, Object>> getHaveSubmitList(Long id) {
		SqlRowList rs = null;	
		rs = baseDao.queryForRowSet("select NVL(bi_barcode,'') bi_barcode,NVL(bi_outboxcode,'') bi_outboxcode,bi_outboxid,bi_barid,bi_prodcode ,bi_inoutno,bi_piid,NVL(bi_outqty,0) bi_outqty,pr_detail from barcodeio left join product on bi_prodcode=pr_code where bi_piid=?",id);
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有已采集的数据!");
		}
	}
	
	@Override
	public void deleteDetail(Long bi_piid, String barcode, String outboxcode) {
		SqlRowList rs0;
		String pr_code = null;
		Object ob = baseDao.getFieldDataByCondition("prodinout ","pi_statuscode", "pi_id="+bi_piid);
		if(ob != null){
			if(ob.toString().equals("POSTED")){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单据已过帐，不允许删除已采集数据!");
			}
			Object[] obs = null;
			if(!StringUtils.isEmpty(barcode)){//删除单个序列号
				rs0 = baseDao.queryForRowSet("select ms_prodcode,ms_makecode from makeserial where ms_sncode=?",barcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的序列号："+barcode+"不存在!");
				}
				pr_code = rs0.getString("ms_prodcode");
				obs = baseDao.getFieldsDataByCondition("barcodeIo left join makeserial on ms_sncode=bi_barcode", new String[]{"bi_pdaget","bi_outqty"}, "bi_piid="+bi_piid+" and bi_barcode='"+barcode+"'");
				if(obs != null){
					if(obs[0]!=null){
					if (Integer.valueOf(obs[0].toString())!=1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的条码,请在ERP中操作！");
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid="+bi_piid+" and bi_barcode='"+barcode+"'");
				}}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的序列号未采集!");
				}
			}else if(!StringUtils.isEmpty(outboxcode)){//删除箱号
				rs0 = baseDao.queryForRowSet("select pa_prodcode,pa_makecode from package where pa_outboxcode=?",outboxcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的箱号或者栈板号："+outboxcode+"不存在!");
				}
				pr_code = rs0.getString("pa_prodcode");
				obs = baseDao.getFieldsDataByCondition("barcodeIo",new String[]{"bi_pdaget","bi_outqty"}, "bi_piid="+bi_piid+" and bi_outboxcode='"+outboxcode+"'");
				if(obs != null){
					if(obs[0] == null){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的数据,请在ERP中操作！");
					}
					else if (Integer.valueOf(obs[0].toString())!=1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的数据,请在ERP中操作！");
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid="+bi_piid+" and bi_outboxcode='"+outboxcode+"'");
				}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的数据未采集!");
				}			
			}
			if(obs != null){
				double bi_outqty = Double.valueOf(obs[1].toString());
				SqlRowList rs = baseDao.queryForRowSet("select pd_id,pd_barcodeoutqty from prodiodetail where pd_piid=? and pd_prodcode=?  and nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc,pd_barcodeinqty desc",bi_piid,pr_code);
				while(rs.next()){
					if(bi_outqty >0){
						if(bi_outqty > rs.getGeneralDouble("pd_barcodeoutqty")){
							baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY =0  where pd_id=?",rs.getInt("pd_id"));
							bi_outqty = NumberUtil.sub(bi_outqty, rs.getGeneralDouble("pd_barcodeoutqty"));
						}else {
							baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY = NVL(pd_barcodeoutqty,0) - ?  where pd_id=?",bi_outqty,rs.getInt("pd_id"));
							bi_outqty = 0;
						}								
					}
				}
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该单据不存在或者已删除!");
		}						
	}
	
	@Override
	public Map<Object, Object> saveAll(String data) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);	
		Map<Object,Object> returnMap = new HashMap<Object, Object>();
		if(CollectionUtil.isEmpty(gstore)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"没有需要提交的数据");
		}
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="+gstore.get(0).get("bi_piid")+" and (pd_status= 99 OR pd_auditstatus='ENTERING')");
		if(cn > 0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据已过账或在录入状态");
		}	
		String code = null;
		for (Map<Object, Object> map : gstore) {
			try {
				if(StringUtil.hasText(map.get("bi_piid"))){
					if(StringUtil.hasText(map.get("bi_barcode"))){
						code = map.get("bi_barcode").toString();
						save(code,Integer.valueOf(map.get("bi_piid").toString()), "sncode");
						returnMap.put("sncode:"+code, "success");
					}else if (StringUtil.hasText(map.get("bi_outboxcode")) && StringUtil.hasText(map.get("bi_boxtype")) ){
						code = map.get("bi_outboxcode").toString();
						save(code,Integer.valueOf(map.get("bi_piid").toString()),map.get("bi_boxtype").toString());
						returnMap.put(map.get("bi_boxtype").toString()+":"+code, "success");
					}
				}
			} catch (Exception e) {
				if(map.get("bi_boxtype")==null || map.get("bi_boxtype")==""){
					returnMap.put("sncode:"+code, e.getMessage());
				}else{
					returnMap.put(map.get("bi_boxtype")+":"+code, e.getMessage());
				}
			}
		}	
		return returnMap;
	}
}
