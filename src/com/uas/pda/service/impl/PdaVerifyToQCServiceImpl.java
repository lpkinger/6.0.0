package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mockrunner.util.common.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.service.PdaVerifyToQCService;

@Service("pdaVerifyToQCServiceImpl")
public class PdaVerifyToQCServiceImpl implements PdaVerifyToQCService{
	@Autowired 
	BaseDao baseDao;

	@Override
	public Map<String, Object> getDataByBar(String bar_code) {
		Map<String, Object> map = new HashMap<>();
		SqlRowList rsBar;
		SqlRowList rs = baseDao.queryForRowSet("select * from baracceptnotify where ban_barcode = ? or ban_outboxcode = ?",bar_code,bar_code);
		if(rs.next()){
			rsBar = baseDao.queryForRowSet("select va_id,va_code,va_vendname,va_statuscode,va_status from verifyapply where va_anid=?",rs.getInt("ban_anid"));
			if(rsBar.next()){
				if(!("AUDITED").equals(rsBar.getString("va_statuscode"))){
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码对应的收料单: "+rsBar.getString("va_code")+"不是已审核状态");
				}
				map.put("main",rsBar.getCurrentMap());
				SqlRowList rsDetail = baseDao.queryForRowSet("select vad_pucode,vad_pudetno,vad_prodcode, vad_qty,pr_detail,pr_spec from verifyapplydetail  left join product on pr_code=vad_prodcode where vad_vaid=?",rsBar.getInt("va_id"));
				if(rsDetail.next()){
					map.put("detail",rsDetail.getResultList());
				}else{
					map.put("detail",null);
				}
			}else{
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码号: "+bar_code+"不存在对应的收料单");
			}
		}else{
			map.put("main",null);
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码号: "+bar_code+"不存在");
		}
		map.put("barcode", bar_code);
		return map;
	}

	@Override
	public List<Map<String, Object>> getHaveList(String caller,String code,Integer page,Integer pageSize) {
		SqlRowList rs;
		SqlRowList rsRe;
		String condition;
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtil.isEmptyOrNull(code)){
			condition =" and 1=1";
		}else{
			condition =" and ( va_code like '%"+code+"%' or va_vendname  like '%"+code+"%'  or va_recorder  like '%"+code+"%' or va_status  like '%"+code+"%')";
		}
		if(("VerifyApply!Have").equals(caller)){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select va_id from VerifyApply left join VerifyApplyDetail on va_id=vad_vaid  where nvl(va_class,' ') ='采购收料单' and nvl(ve_code,' ')<>' '"+condition+" group by va_id order by va_id desc) tt where rownum<="+end+" )where rn>="+start);
		}else if(("VerifyApply!Need").equals(caller)){
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select va_id from VerifyApply left join VerifyApplyDetail on va_id=vad_vaid  where nvl(va_class,' ') ='采购收料单' and nvl(ve_code,' ')=' ' "+condition+" group by va_id order by va_id desc) tt where rownum<="+end+" )where rn>="+start);
		}else{
			return null;
		}
		while(rs.next()){
			rsRe = baseDao.queryForRowSet("select * from VerifyApply where va_id = ?",rs.getInt("va_id"));
			if(rsRe.next()){
				map.put("main",rsRe.getCurrentMap());	
				if(("VerifyApply!Need").equals(caller)){					
					map.put("ifShowButton", ("AUDITED").equals(rsRe.getString("va_statuscode"))?true:false);
				}else{
					map.put("ifShowButton",false);
				}
			}else{
				map.put("main",null);	
				map.put("ifShowButton",false);
			}
			
			rsRe = baseDao.queryForRowSet("select product.pr_detail pr_detail,product.pr_spec pr_spec ,product.pr_unit pr_unit,verifyapplydetail.* from verifyapplydetail left join product on vad_prodcode = pr_code where vad_vaid =? order by vad_detno asc",rs.getInt("va_id"));
			if(rsRe.next()){
				map.put("detail",rsRe.getResultList());	
			}else{
				map.put("detail",null);	
			}
			list.add(map);
			map = new HashMap<String,Object>();
		}
		return list;
	}
}
	
