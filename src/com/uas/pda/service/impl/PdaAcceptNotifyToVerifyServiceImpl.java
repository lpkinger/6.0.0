package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mockrunner.util.common.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.AcceptNotifyService;
import com.uas.erp.service.scm.VerifyApplyService;
import com.uas.pda.service.PdaAcceptNotifyToVerifyService;

@Service("pdaAcceptNotifyToVerifyServiceImpl")
public class PdaAcceptNotifyToVerifyServiceImpl implements PdaAcceptNotifyToVerifyService{
	@Autowired 
	BaseDao baseDao;
	@Autowired
	AcceptNotifyService acceptNotifyService;
	@Autowired
	VerifyApplyService verifyApplyService;

	@Override
	public Map<String, Object> getDataByBar(String bar_code) {
		Map<String, Object> map = new HashMap<>();
		SqlRowList rs = baseDao.queryForRowSet("select * from baracceptnotify where ban_barcode = ? or ban_outboxcode = ?",bar_code,bar_code);
		if(rs.next()){
			rs = baseDao.queryForRowSet("select an_statuscode,and_yqty,an_code,and_detno,an_id,and_id from acceptnotify inner join acceptnotifydetail on an_id = and_anid where "
					+ " an_id = ?",rs.getInt("ban_anid"));
			if(rs.next()){
				if(!("AUDITED").equals(rs.getString("an_statuscode"))){
					if(("TURNVA").equals(rs.getString("an_statuscode"))){
						throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料通知单 "+rs.getString("an_code")+"已转收料");
					}else{					
						throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料通知单 "+rs.getString("an_code")+"未审核");
					}
				}
				if(rs.getInt("andyqty")>0){
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "收料通知单: "+rs.getString("an_code")+",序号"+rs.getInt("and_detno")+"存在已转收料数据");
				}
				SqlRowList rsMain = baseDao.queryForRowSet("select an_code,an_id ,an_vendname,an_status from acceptnotify where an_id = ?",rs.getInt("an_id"));
				if(rsMain.next()){
					map.put("main",rsMain.getCurrentMap());
				}else{
					map.put("main",null);
				}
				SqlRowList rsDetail = baseDao.queryForRowSet("select and_ordercode,and_orderdetno,and_prodcode, and_inqty,pr_detail,pr_spec from acceptnotifydetail left join product on pr_code=and_prodcode where and_anid=?",rs.getInt("an_id"));
				if(rsDetail.next()){
					map.put("detail",rsDetail.getResultList());
				}else{
					map.put("detail",null);
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
	public Map<String, Object> turnVerify(Integer an_id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from acceptnotify where an_id = ?",an_id);
		if(rs.next()){
			if(!("AUDITED").equals(rs.getString("an_statuscode"))){
				if(("TURNVA").equals(rs.getString("an_statuscode"))){
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料通知单 "+rs.getString("an_code")+"已转收料");
				}else{					
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料通知单 "+rs.getString("an_code")+"未审核");
				}
			}
			Integer va_id = acceptNotifyService.turnVerifyApply("AcceptNotify",an_id);
			try{		
				verifyApplyService.submitVerifyApply(va_id,"VerifyApply");
				verifyApplyService.auditVerifyApply(va_id,"VerifyApply");
			}catch(Exception e){
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, e.getMessage());
			}
			rs = baseDao.queryForRowSet("select va_id,va_code,va_status from verifyapply where va_id=?",va_id);
			if(rs.next()){
				return rs.getCurrentMap();
			}
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料通知单不存在");
		}
		return null;
	}

	@Override
	@Transactional
	public List<Map<String, Object>> turnQC(Integer va_id) {
		SqlRowList rs = baseDao.queryForRowSet("select va_statuscode,va_code from VerifyApply  where va_id = ?",va_id);
		String va_code ;
		if(rs.next()){
			va_code = rs.getString("va_code");
			if(!("AUDITED").equals(rs.getString("va_statuscode"))){
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "收料单: "+rs.getString("va_code")+"不是已审核状态");
			}
			rs = baseDao.queryForRowSet(" select wm_concat(distinct vad_detno) vad_detno from VerifyApplyDetail A left join VerifyApply on va_id = vad_vaid where vad_vaid = ? "
						+" and nvl(vad_yqty,0)>0 and exists(select 1 from QUA_VerifyApplyDetail B where vad_code= va_code and A.vad_detno = B.vad_detno)",va_id);
			if(rs.next() && rs.getString("vad_detno")!= null){
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "序号: "+rs.getString("vad_detno")+"已转检验单,不允许整张单转单");
			}
			rs = baseDao.queryForRowSet("select  vad_id from VerifyApplyDetail where vad_vaid=?",va_id);
			if(rs.hasNext()){				
				while(rs.next()){	
					try{					
						verifyApplyService.detailTurnIQC("{vad_id:"+rs.getString("vad_id")+"}");
					}catch(Exception e){
						throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, e.getMessage());
					}				  
				}
			}
			rs = baseDao.queryForRowSet("select ve_id,ve_code, ve_status from QUA_VerifyApplyDetail where vad_code=?",va_code);
			if(rs.next()){
				return rs.getResultList();
			}
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "该收料单不存在");
		}
		return null;
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
			condition =" and (an_code like '%"+code+"%' or an_vendname  like '%"+code+"%' or an_recorder  like '%"+code+"%' or an_status  like '%"+code+"%')";
		}
		if(("AcceptNotify!Have").equals(caller) ){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select * from AcceptNotify where nvl(an_status,' ') = '已转收料' and (select sum(nvl(and_inqty,0)) from acceptnotifydetail where and_anid = an_id)>0  "+condition+" order by an_id desc) tt where rownum<="+end+" )where rn>="+start);	
		}else if(("AcceptNotify!Need").equals(caller)){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select * from AcceptNotify where nvl(an_status,' ') <> '已转收料' and (select sum(nvl(and_inqty,0)) from acceptnotifydetail where and_anid = an_id)>0  "+condition+" order by an_id desc) tt where rownum<="+end+" )where rn>="+start);	
		}else{
			return null;
		}
		while(rs.next()){
			map.put("main",rs.getCurrentMap());					
			rsRe = baseDao.queryForRowSet("select  product.pr_detail pr_detail,product.pr_spec pr_spec ,product.pr_unit pr_unit,AcceptnotifyDetail.* from AcceptnotifyDetail left join product on and_prodcode = pr_code where and_anid =? order by and_detno asc",rs.getInt("an_id"));
			if(rsRe.next()){
				map.put("detail",rsRe.getResultList());	
			}else{
				map.put("detail",null);	
			}
			if(("AcceptNotify!Need").equals(caller)){				
				map.put("ifShowButton", ("AUDITED").equals(rs.getString("an_statuscode"))?true:false);
			}else{
				map.put("ifShowButton",false);
			}
			list.add(map);
			map = new HashMap<String,Object>();
		}
		return list;
	}
}
	
