package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.service.pm.MakeBadService;
import com.uas.pda.dao.PdaCommonDao;

@Service("makeBadService")
public class MakeBadServiceImpl implements MakeBadService{
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private PdaCommonDao pdaCommonDao;
    @Autowired
    private MakeCraftDao makeCraftDao;
    
	@Override
	public Map<String, Object> checkSNcode(String ms_sncode,
			String st_code) {
		// TODO Auto-generated method stub
		Object ob ;
		//判断工序是否为维修工序
		ob = baseDao.getFieldDataByCondition("step", "st_code", "st_code='"+st_code+"' and st_ifrepair=-1");
		if(ob == null){
			BaseUtil.showError("当前工序："+st_code+",不是维修工序!");
		}
		//判断序列号是否存在,状态是否为待维修
		Object []obs = baseDao.getFieldsDataByCondition("makeSerial", new String []{"NVL(ms_status,0)","ms_nextstepcode","ms_makecode","ms_mccode"}, "ms_sncode='"+ms_sncode+"'");
		if(obs == null){
			BaseUtil.showError("序列号:"+ms_sncode+"不存在!");
		}else if(!obs[0].toString().equals("3")){
			BaseUtil.showError("序列号:"+ms_sncode+"状态为"+obs[0]+",不允许操作!");
		}
		
		if(!makeCraftDao.ifNextStepcode(st_code, ms_sncode,"")){//判断序列号的ms_nextstepcode是否等于当前工序
			BaseUtil.showError("序列号:"+ms_sncode+"的当前工序不是"+st_code);
		}
		Map<String,Object> m = new HashMap<String, Object>();
		m.put("ms_makecode", obs[2]);
		m.put("ms_mccode", obs[3]);
		//判断通过，则加载已采集不良原因grid
        SqlRowList rs = baseDao.queryForRowSet("Select mb_id,mb_sncode,mb_badcode,bc_name,bc_groupcode,mb_badremark,bc_note,bc_dutyman,mb_status"+
			 " from Makebad left join badcode on mb_badcode=bc_code where mb_sncode=? ",ms_sncode);
		if(rs.next()){
			m.put("bddatas",pdaCommonDao.changeKeyToLowerCase(rs.getResultList()));			
		}		
		return m;
	}
	@Override
	public void deleteMakeBad(int mb_id) {
		// TODO Auto-generated method stub
		Object ob = baseDao.getFieldDataByCondition("makeBad", "mb_id", "mb_id="+mb_id);
		if (ob == null){
			BaseUtil.showError("当前不良记录不存在!");
		}
		ob = baseDao.getFieldDataByCondition("makeBad", "mb_id", "mb_id="+mb_id+" and mb_status=1");
		if(ob != null){//已经维修过的，不允许删除
			BaseUtil.showError("当前不良记录已经维修完成，不允许删除!");
		}
		baseDao.deleteByCondition("makeBad", "mb_id="+mb_id);
		
	}
	@Override
	public String addOrUpdateMakeBad(String data) {
		// TODO Auto-generated method stub
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		String mb_badcode = map.get("mb_badcode").toString();		
		Object ob = baseDao.getFieldDataByCondition("makeBad", "mb_id", "mb_id="+map.get("mb_id")+" and mb_status=1");
		if(ob != null){//已经维修过的，不允许删除
			BaseUtil.showError("当前不良记录已经维修完成，不允许修改!");
		}
		int count = baseDao.getCount("select count(1) from makebad where mb_badcode='"+mb_badcode+"' and mb_sncode='"+map.get("ms_sncode")+"'");
		if(count > 1){
			BaseUtil.showError("不良原因重复!");
		}
		if(map.get("mb_id") == null || "null".equals(map.get("mb_id")) || "".equals(map.get("mb_id")) ){//不存在新增
			int mb_id =  baseDao.getSeqId("MakeBad_seq");
			baseDao.execute("insert into makebad (mb_id,mb_makecode,mb_mscode,mb_sncode,mb_inman,mb_indate," +
					"mb_stepcode,mb_sourcecode,mb_badcode,mb_status,mb_badremark)" +
					" select "+mb_id+",ms_makecode,ms_code,ms_sncode,'"+SystemSession.getUser().getEm_name()+"',sysdate,'" +
					map.get("st_code")+"','"+map.get("sc_code")+"','"+map.get("mb_badcode")+"','"+map.get("mb_status")+"','"+map.get("mb_badremark")+"' from makeSerial  where ms_sncode='"+map.get("ms_sncode")+"'");
			return String.valueOf(mb_id);
		}else{
		    baseDao.updateByCondition("makebad", "mb_badcode='"+map.get("mb_badcode")+"',mb_status='"+map.get("mb_status")+"',mb_badremark='"+map.get("mb_badremark")+"'", "mb_id="+map.get("mb_id"));
		}
		return null;		
	}
	@Override
	public void finishFix(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		String ms_sncode = map.get("ms_sncode").toString();
		//判断是否所有记录mb_status 等于1或-1  （已维修或无不良）
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from makebad where mb_sncode='"+ms_sncode+"' and mb_status not in (1,-1)");
		if(rs.next() && rs.getInt("cn") > 0 ){
			BaseUtil.showError("明细维修结果必须为已维修或无不良!");
		}else{//更新makeserial的ms_nextstepcode=回流工序，ms_craftcode,=ms_ ms_stepcode=当前的返修工序,ms_status=1 生产中
			baseDao.updateByCondition("makeSerial", "ms_nextstepcode='"+map.get("cd_stepcode")+"',ms_stepcode='"+map.get("stCode")+"',ms_status=1,ms_craftcode='"+map.get("cr_code")+"'", "ms_sncode='"+ms_sncode+"'");
			//插入makeprocess 采集日志
			String mc_code = baseDao.getJdbcTemplate().queryForObject("select ms_mccode from makeSerial where ms_sncode='"+ms_sncode+"'", String.class);
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname," +
					" mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"+
				    " Select MakeProcess_seq.nextval,mc_makecode,mc_maid,'"+ms_sncode+"','"+ms_sncode+"','"+map.get("stCode")+"','"+map.get("stName")+"'," +
				    " '','','维修作业','完成维修',sysdate,'"+SystemSession.getUser().getEm_name()+"',sc_wccode,sc_linecode,sc_code from makeCraft" +
					" left join source on 1=1 where mc_makecode='"+mc_code+"' and sc_stepcode='"+map.get("stCode")+"'");
			boolean bo = makeCraftDao.checkHaveGetStep(ms_sncode, map.get("stCode").toString());
			if(!bo){
				//更新已经采集的工序
				baseDao.execute("Update makeserial 	set ms_paststep = ms_paststep ||',"+map.get("stCode")+"' where ms_sncode='"+ms_sncode+"'");
			}
		}		
	}
		
	@Override
	public void makeBadScrap(String data) {		
		// TODO Auto-generated method stub
		Map<Object,Object> map =  BaseUtil.parseFormStoreToMap(data);
		String ms_sncode = map.get("ms_sncode").toString();
		String  st_code = map.get("stCode").toString();
		//判断是否有不可维修的记录 mb_status=2
		int n = baseDao.getCount("select count(1) cn from makebad where mb_sncode='"+ms_sncode+"' and mb_status=2");
		if(n == 0){
			BaseUtil.showError("明细行维修结果不包含：不可维修，不允许报废!");
		}
		//更新makeserial的MS_STATUS = 4 已报废
		baseDao.updateByCondition("makeSerial", "ms_status=4,ms_nextstepcode='',ms_stepcode='"+st_code+"'", "ms_sncode='"+ms_sncode+"'");
		//插入makeprocess 采集日志
		String mc_code = baseDao.getJdbcTemplate().queryForObject("select ms_mccode from makeSerial where ms_sncode='"+ms_sncode+"'", String.class);
		baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname," +
				" mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"+
			    " Select MakeProcess_seq.nextval,mc_makecode,mc_maid,'"+ms_sncode+"','"+ms_sncode+"','"+st_code+"','"+map.get("stName")+"'," +
			    " '','','维修作业','报废',sysdate,'"+SystemSession.getUser().getEm_name()+"',sc_wccode,sc_linecode,sc_code from makeCraft " +
				" left join source on 1=1 where mc_makecode='"+mc_code+"' and sc_stepcode='"+st_code+"'");
		boolean bo = makeCraftDao.checkHaveGetStep(ms_sncode, st_code);
		if(!bo){
			//更新已经采集的工序
			baseDao.execute("Update makeserial 	set ms_paststep = ms_paststep ||',"+st_code+"' where ms_sncode='"+ms_sncode+"'");
		}
	}
				
}
