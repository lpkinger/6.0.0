package com.uas.pda.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.FeederUseService;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaFeederUseService;

@Service("pdaFeederUseService")
public class PdaFeederUseServiceImpl implements PdaFeederUseService{
	@Autowired 
	private BaseDao baseDao;
    @Autowired
    private PdaCommonDao pdaCommonDao;
    @Autowired
    private FeederUseService feederUseService;
	public Map<String,Object> searchMa(String fu_makecode) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select ma_statuscode,ma_prodcode,pr_detail, pr_spec,ma_code fu_makecode from Make left join product on ma_prodcode=pr_code where ma_code='"+fu_makecode+"'");
		if(rs.next()){
			if(!rs.getString("ma_statuscode").equals("AUDITED")){
				BaseUtil.showError("制造单:"+fu_makecode+"未审核!");
			}
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}else{
			BaseUtil.showError("制造单:"+fu_makecode+"不存在!");
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> feederUsedlist(String fu_makecode,String fu_linecode) {
		// TODO Auto-generated method stub
		String sql = "select fu_fecode fe_code, fu_fespec, fu_status,fu_id from FeederUse where fu_status='待上料'";
		if(fu_makecode != null){
			sql += " and fu_linecode='"+fu_linecode+"' and fu_makecode='"+fu_makecode+"'";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}else{
			BaseUtil.showError("没有已领用的飞达!");
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> feederMakeQuery(String fu_makecode,
			String fu_linecode) {
		// TODO Auto-generated method stub
		SqlRowList rs = baseDao.queryForRowSet("select psl_feeder, needqty, getqty, ungetqty from PM_FeederUse left join make on ma_prodcode=ps_prodcode where ma_code='"+fu_makecode+"' and ps_linecode='"+fu_linecode+"'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}else{
			//BaseUtil.showError("不存在需要领取的飞达!");
		}
		return null;
	}
	@Override
	public List<Map<String,Object>> feederGet(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		feederUseService.getFeeder(map.get("fe_code").toString(), map.get("fu_makecode").toString(), map.get("fu_linecode").toString());
		return feederMakeQuery(map.get("fu_makecode").toString(), map.get("fu_linecode").toString());
	}
	@Override
	public void feederBack(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		feederUseService.returnFeeder(map.get("fe_code").toString(), map.get("reason").toString(), Integer.valueOf(map.get("isuse").toString()));
	}
	@Override
	public void feederBackAll(String fu_makecode) {
		// TODO Auto-generated method stub
		feederUseService.returnAllFeeder(fu_makecode);
	}
	
	

}
