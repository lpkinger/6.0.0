package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mockrunner.util.common.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.BatchDealService;
import com.uas.pda.service.PdaQCToPurcCheckinService;

@Service("pdaQCToPurcCheckinImpl")
public class PdaQCToPurcCheckinServiceImpl implements PdaQCToPurcCheckinService{
	@Autowired 
	BaseDao baseDao;
	@Autowired
	BatchDealService batchDealService;

	@Override
	public Map<String, Object> getDataByBar(String bar_code) {
		Map<String, Object> map = new HashMap<>();
		SqlRowList rs = baseDao.queryForRowSet("select * from baracceptnotify where ban_barcode = ? or ban_outboxcode = ?",bar_code,bar_code);
		if(rs.next()){
			rs = baseDao.queryForRowSet("select and_anid,and_detno from acceptnotifydetail where and_id = ?",rs.getInt("ban_andid"));
			if(rs.next()){
				rs = baseDao.queryForRowSet("select qua_verifyapplydetail.ve_code ve_code,qua_verifyapplydetail.ve_statuscode ve_statuscode,qua_verifyapplydetail.ve_id ve_id,qua_verifyapplydetail.vad_code vad_code,QUA_VerifyApplyDetail.vad_prodcode vad_prodcode,pr_detail,pr_spec, "
						+" qua_verifyapplydetail.ve_status ve_status,qua_verifyapplydetail.ve_method ve_method,qua_verifyapplydetail.ve_result ve_result,qua_verifyapplydetail.ve_ordercode ve_ordercode, "
						+" qua_verifyapplydetail.ve_orderdetno ve_orderdetno,qua_verifyapplydetail.vad_vendname vad_vendname,verifyapplydetail.vad_qty vad_qty from acceptnotify inner join acceptnotifydetail on and_anid=an_id inner join verifyapplydetail "
						+" on verifyapplydetail.vad_andid=and_id inner join qua_verifyapplydetail on qua_verifyapplydetail.vad_code=verifyapplydetail.vad_code and qua_verifyapplydetail.vad_detno= "
						+" verifyapplydetail.vad_detno  left join product on QUA_VerifyApplyDetail.vad_prodcode = pr_code where an_id=? and and_detno=?",rs.getInt("and_anid"),rs.getString("and_detno"));
				if(rs.next()){
					Integer ve_id = rs.getInt("ve_id");
					if(!("AUDITED").equals(rs.getString("ve_statuscode"))){
						throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "检验单: "+rs.getString("ve_code")+"不是已审核状态");
					}
					map=rs.getCurrentMap();
					rs = baseDao.queryForRowSet(" select wm_concat(distinct ved_detno) ved_detno from QUA_VerifyApplyDetailDet left join QUA_VerifyApplyDetail on ved_veid = ve_id where ved_veid = ? "
							+" and nvl(ved_statuscode ,' ')='TURNIN' ",ve_id);
				   if(rs.next() && rs.getString("ved_detno")!= null){
					    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "存在明细已经验收,不允许整张单入库");
				   }
				   rs = baseDao.queryForRowSet("select sum(nvl(ved_okqty,0)) okqty ,sum(nvl(ved_ngqty,0)) ngqty ,sum(nvl(ved_checkqty,0)) checkqty "
						   +" from QUA_VerifyApplyDetailDet  where ved_veid=?",ve_id);
				   if(rs.next()){
					   map.put("OKQTY", rs.getDouble("okqty"));
					   map.put("NGQTY", rs.getDouble("ngqty"));
					   map.put("CHECKQTY", rs.getDouble("checkqty"));
				   }
				   rs = baseDao.queryForRowSet("select pr_whcode,wh_description from QUA_VerifyApplyDetail left join product on pr_code = vad_prodcode left join warehouse on wh_code = pr_whcode where ve_id=?",ve_id);
				   if(rs.next()){
					   map.put("DEFAULTWHCODE", rs.getString("pr_whcode"));
					   map.put("DEFAULTWHNAME", rs.getString("wh_description"));
				   }
				}else{
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码号: "+bar_code+"对应的收料通知单没有有对应的检验单");
				}
			}else{
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码号: "+bar_code+"没有对应的收料通知单");
			}
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码号: "+bar_code+"不存在");
		}
		map.put("barcode", bar_code);
		return map;
	}

	@Override
	@Transactional
	public Map<String, Object> turnPurcStorage(Integer ve_id,String okwh,String ngwh) {
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> dataTurn = new HashMap<>();
		SqlRowList rs = baseDao.queryForRowSet("select A.ve_checkdate ve_checkdate, A.vad_prodcode vad_prodcode, pr_detail, pr_spec, pr_unit, A.vad_qty vad_qty, pr_code, "
				+" A.ve_id ve_id, A.ve_code ve_code, A.ve_class ve_class, A.ve_method ve_method, A.vad_code vad_code, ved_ngqty, ved_status, "
				+" ved_okqty, pr_purcunit, ved_id, ved_veid, ved_checkqty, ved_detno, nvl(vad_whcode,pr_whcode) pr_whcode, A.vad_detno vad_detno, "
				+" A.ve_whman ve_whman, A.ve_sendcode ve_sendcode, A.ve_testman ve_testman, ved_isok, ved_isng, A.vad_vendcode vad_vendcode, "
				+" A.vad_vendname vad_vendname,'"+okwh+"' pr_whcode,'"+ngwh+"' wh_code from QUA_VerifyApplyDetail A left join Product on A.vad_prodcode=pr_code "
				+" left join QUA_VerifyApplyDetailDet on A.ve_id=ved_veid left join VerifyApplyDetail B on A.vad_vaid=B.vad_vaid "
				+" and A.vad_detno=B.vad_detno  where A.ve_id  = ?",ve_id);
		if(rs.next()){
			if(rs.getInt("ved_okqty") >0 && (StringUtils.isEmpty(okwh))){
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该检验单存在合格数,良品仓必填");
			}
			if(rs.getInt("ved_ngqty") >0 && (StringUtils.isEmpty(ngwh))){
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "检验单存在不合格数，不良品仓必填");
			}
			data =  rs.getCurrentMap();
			Set<String> keySet = data.keySet();
	        for (String key : keySet)
	        {
	            String newKey = key.toLowerCase();
	            dataTurn.put(newKey, data.get(key));
	        }
			String result = batchDealService.detailTurnIn("VerifyApplyDetail!Deal",JSON.toJSONString(dataTurn));
			if(!("<br>转入失败!").equals(result)){
				Map<String, Object> map = new HashMap<>();
				SqlRowList ys = baseDao.queryForRowSet("select distinct pi_class,pi_id,pi_inoutno,pi_title,pi_cardcode,pi_id,pi_statuscode, pd_whcode, pd_whname,'未入库',pd_pdno,pd_id,pd_batchcode,pd_whcode,pd_ordercode, "
							+" pi_pdastatus from ProdInOut left join ProdIODetail on pi_id=pd_piid left join QUA_VerifyApplyDetailDet on pd_qcid = ved_id "
							+" WHERE ved_veid = "+ve_id+" and exists(select 1 from "
							+" QUA_VerifyApplyDetailDet where ved_id =pd_qcid) and pd_piclass='采购验收单'");
				
				if(ys.next()){
					if(rs.getInt("ved_ngqty") == 0 && rs.getInt("ved_okqty") > 0){
						String pd_batchcode = baseDao.getBatchcode("ProdInOut!PurcCheckin");
						baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_piclass,bi_piid,bi_inoutno,bi_pdno,bi_pdid,bi_batchcode,bi_prodcode,bi_whcode,bi_inqty,bi_madedate,bi_prodid,bi_outboxcode,bi_vendbarcode,bi_indate,bi_ordercode) "
								+" select barcodeio_seq.nextval,ban_barcode,'采购验收单',"+ys.getInt("pi_id")+",'"+ys.getString("pi_inoutno")+"',"+ys.getInt("pd_pdno")+","+ys.getInt("pd_id")+",'"+pd_batchcode+"',ban_prodcode,'"+ys.getString("pd_whcode")+"',  "
								+"ban_qty,ban_madedate,ban_prodid,ban_outboxcode,lotcode,sysdate,'"+ys.getString("pd_ordercode")+"' from verifyapplydetail A left join qua_verifyapplydetail B on A.vad_vaid = B.vad_vaid left join baracceptnotify on A.vad_andid = ban_andid and B.vad_prodcode = ban_prodcode where B.ve_id =  "+ve_id +" and nvl(ban_barcode,' ')<>' '");
								//更新出入库单中明细行中的条码数量
						        baseDao.execute("update ProdIODetail set (pd_batchcode,pd_barcodeinqty)=(select '"+pd_batchcode+"',NVL(sum(nvl(bi_inqty,0)),0) from barcodeio where bi_pdid=pd_id )" +
						        				" where  pd_piid=" + ys.getInt("pi_id")+"and pd_id="+ys.getInt("pd_id") );
						        //如果主表pi_pdastatus为空时，就更新为未入库
						        baseDao.execute("update prodinout set pi_pdastatus = '未入库' where pi_id="+ys.getInt("pi_id")+" and nvl(pi_pdastatus,' ') <> '未入库'");
					}
					map.put("okNO", ys.getCurrentMap());
				}
				ys = baseDao.queryForRowSet("select distinct pi_class,pi_id,pi_inoutno,pi_title,pi_cardcode,pi_id,pi_statuscode, pd_whcode, pd_whname,'未入库',pd_pdno,pd_id,pd_batchcode,pd_whcode,pd_ordercode, "
							+" pi_pdastatus from ProdInOut left join ProdIODetail on pi_id=pd_piid left join QUA_VerifyApplyDetailDet "
							+" on pd_qcid = ved_id  WHERE ved_veid ="+ve_id+" and  exists "
							+"(select 1 from QUA_VerifyApplyDetailDet where ved_id =pd_qcid) and pd_piclass='不良品入库单'");
				if(ys.next()){
					if(rs.getInt("ved_okqty") == 0 && rs.getInt("ved_ngqty") > 0){	
						String pd_batchcode = baseDao.getBatchcode("ProdInOut!PurcCheckin");
						baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_piclass,bi_piid,bi_inoutno,bi_pdno,bi_pdid,bi_batchcode,bi_prodcode,bi_whcode,bi_inqty,bi_madedate,bi_prodid,bi_outboxcode,bi_vendbarcode,bi_indate,bi_ordercode )"
								+" select barcodeio_seq.nextval,ban_barcode,'不良品入库单',"+ys.getInt("pi_id")+",'"+ys.getString("pi_inoutno")+"',"+ys.getInt("pd_pdno")+","+ys.getInt("pd_id")+",'"+pd_batchcode+"',ban_prodcode,'"+ys.getString("pd_whcode")+"',  "
								+"ban_qty,ban_madedate,ban_prodid,ban_outboxcode,lotcode,sysdate,'"+ys.getString("pd_ordercode")+"' from verifyapplydetail A left join qua_verifyapplydetail B on A.vad_vaid = B.vad_vaid left join baracceptnotify on A.vad_andid = ban_andid and B.vad_prodcode = ban_prodcode where B.ve_id =  "+ve_id +" and nvl(ban_barcode,' ')<>' '");
								//更新出入库单中明细行中的条码数量
						        baseDao.execute("update ProdIODetail set (pd_batchcode,pd_barcodeinqty)=(select '"+pd_batchcode+"',NVL(sum(nvl(bi_inqty,0)),0) from barcodeio where bi_pdid=pd_id )" +
						        				" where  pd_piid=" + ys.getInt("pi_id")+"and pd_id="+ys.getInt("pd_id") );
						        //如果主表pi_pdastatus为空时，就更新为未入库
						        baseDao.execute("update prodinout set pi_pdastatus = '未入库' where pi_id="+ys.getInt("pi_id")+" and nvl(pi_pdastatus,' ') <> '未入库'");
					}
					map.put("ngNO", ys.getString("pi_inoutno"));
				}
				return map;
			}else{
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, result);
			}
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该检验单不存在");
		}
	}

	@Override
	public List<Map<String, Object>> getHaveList(String caller,String code,Integer page,Integer pageSize) {
		List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		SqlRowList rs;
		SqlRowList rsRe;
		String condition;
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		if(StringUtil.isEmptyOrNull(code)){
			condition =" and 1=1";
		}else{
			condition =" and (ve_code like '%"+code+"%' or vad_vendname  like '%"+code+"%'  or ve_recorder  like '%"+code+"%' or ve_status  like '%"+code+"%')";
		}
		if(("VerifyApplyDetail!Need").equals(caller) ){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select ve_id from  QUA_VerifyApplyDetail left join QUA_VerifyApplyDetailDet on ve_id=ved_veid "
						+" where vad_class='采购收料单' and nvl(ved_statuscode,' ') <> 'TURNIN' and (nvl(ved_isng,0)*nvl(ved_ngqty,0)+nvl(ved_isok,0)*nvl(ved_okqty,0)<ved_checkqty) "
						+" and (nvl(ved_okqty,0)+nvl(ved_ngqty,0) >0) "+condition+"group by ve_id order by ve_id desc) tt where rownum<="+end+" )where rn>="+start	);	
		}else if(("VerifyApplyDetail!Have").equals(caller)){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select ve_id from  QUA_VerifyApplyDetail left join QUA_VerifyApplyDetailDet on ve_id=ved_veid "
						+" where vad_class='采购收料单' and nvl(ved_statuscode,' ') = 'TURNIN' and (nvl(ved_isng,0)*nvl(ved_ngqty,0)+nvl(ved_isok,0)*nvl(ved_okqty,0)=ved_checkqty) and (nvl(ved_okqty,0)+nvl(ved_ngqty,0) >0) "+condition+" group by ve_id order by ve_id desc) tt where rownum<="+end+" )where rn>="+start);	
		}else{
			return null;
		}
		SqlRowList rsDetail;
		Map<String,Object> mapDetail = new HashMap<String,Object>();
		while(rs.next()){
			rsRe = baseDao.queryForRowSet("select product.pr_detail pr_detail,product.pr_spec pr_spec ,product.pr_unit pr_unit,product.pr_whcode defaultwhcode,wh_description defaultwhname,QUA_VerifyApplyDetail.* from QUA_VerifyApplyDetail left join Product on vad_prodcode=pr_code left join warehouse on wh_code = pr_whcode where ve_id = ?",rs.getInt("ve_id"));
			if(rsRe.next()){
				mapDetail = rsRe.getCurrentMap();
				rsDetail = baseDao.queryForRowSet("select sum(nvl(ved_okqty,0)) okqty ,sum(nvl(ved_ngqty,0)) ngqty ,sum(nvl(ved_checkqty,0)) checkqty from QUA_VerifyApplyDetailDet where ved_veid =? order by ved_detno asc",rs.getInt("ve_id"));
				if(rsDetail.next()){
					mapDetail.put("OKQTY", rsDetail.getDouble("okqty"));
					mapDetail.put("NGQTY", rsDetail.getDouble("ngqty"));
					mapDetail.put("CHECKQTY", rsDetail.getDouble("checkqty"));
				}
				map.put("main",mapDetail);		
			}else{
				map.put("main",null);	
			}
			if(("VerifyApplyDetail!Need").equals(caller) ){
				map.put("ifShowButton",("AUDITED").equals(rsRe.getString("ve_statuscode"))?true:false);
			}else{
				map.put("ifShowButton",false);
			}
			list.add(map);
			map = new HashMap<String,Object>();
			mapDetail = new HashMap<String,Object>();
		}
		return list;
	}
}
	
