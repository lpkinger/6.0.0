package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeSerialService;

@Service("makeSerialService")
public class MakeSerialServiceImpl implements MakeSerialService{
	@Autowired
	private BaseDao baseDao;	
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteMakeSerial(int mc_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mc_id });		
		//判断序列号是否已经上料
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn,max(ms_sncode)ms_sncode from makeserial where ms_mcid="+mc_id+" and nvl(ms_status,0)>0");
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("序列号["+rs.getString("ms_sncode")+"]已经上线，不允许清空所有!");
		}
		// 删除MakeSerial
		baseDao.deleteById("MakeSerial", "ms_mcid", mc_id);
		// 记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mc_id });
	}

	@Override
	@Transactional
	public void updateMakeSerialById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String craftcode=null,craftname=null,wccode=null,nextstepcode=null,macode = null,mccode = null,prodcode =null; 
		SqlRowList rs0=baseDao.queryForRowSet("select mc_code,mc_prodcode,ma_craftcode,ma_wccode,ma_id,ma_code,ma_craftname from MakeCraft left join make on mc_makecode=ma_code where mc_id="+store.get("mc_id"));
		if(rs0.next()){
			craftcode=rs0.getString("ma_craftcode");
			craftname=rs0.getString("ma_craftname");
			wccode=rs0.getString("ma_wccode");
			macode = rs0.getString("ma_code");
		    mccode = rs0.getString("mc_code");
		    prodcode = rs0.getString("mc_prodcode");
		}
		//判断制造单是否完工,完工则不让生成
		Object status = baseDao.getFieldDataByCondition("make", "ma_finishstatuscode", "ma_id="+rs0.getInt("ma_id"));
		if(status != null){
			if(status.toString().equals("COMPLETED")){
				BaseUtil.showError("制造单："+macode+",已完工!");
			}
		}
		rs0=baseDao.queryForRowSet("select cd_stepcode,cd_stepname from craftdetail inner join craft on cr_id=cd_crid where cr_code='"+craftcode+"' order by cd_detno");
		if(rs0.next()){
			nextstepcode=rs0.getString("cd_stepcode");
		}else{
			BaseUtil.showError( "工艺路线未维护，不能生成序列号!");
		}			
		String re = CollectionUtil.getRepeats(gstore, "ms_code");
		if(re != null && !re.equals("")){
			BaseUtil.showError("流程码:"+re+"重复!");
		}
		re = CollectionUtil.getRepeats(gstore, "ms_sncode");
		if(re != null && !re.equals("")){
			BaseUtil.showError("序列号:"+re+"重复!");
		}		
		for(Map<Object,Object> map:gstore){		
			SqlRowList rs;
			//判断序列号是否已经有工序
			if(map.get("ms_id") != null && !map.get("ms_id").equals("0")){
				rs =  baseDao.queryForRowSet("select cm_sncode from craftmaterial left join makeserial on ms_sncode=cm_sncode where ms_sncode='"+store.get("ms_sncode")+"'");
				if(rs.next()){
					BaseUtil.showError("序列号:"+rs.getString("cm_sncode")+"在生产中不允许修改!");
				}
			}
			//判断流程码，序列号是否重复
			Object ob = null;
			if(map.get("ms_code") != null){
				ob = baseDao.getFieldDataByCondition("makeSerial", "ms_code", "ms_code='"+map.get("ms_code")+"' and ms_id<>"+map.get("ms_id"));
				if(ob != null){
					BaseUtil.showError("流程码:"+map.get("ms_code")+"重复!");
				}
			}
			ob = baseDao.getFieldDataByCondition("makeSerial", "ms_sncode", "ms_sncode='"+map.get("ms_sncode")+"' and ms_id<>"+map.get("ms_id"));
			if(ob != null){
				BaseUtil.showError("序列号:"+map.get("ms_sncode")+"重复!");
			}	
			map.put("ms_wccode", wccode);
			map.put("ms_craftcode", craftcode);
			map.put("ms_craftname", craftname);
			map.put("ms_nextstepcode", nextstepcode);
			map.put("ms_status", 0);
			map.put("ms_mccode", mccode);
			map.put("ms_prodcode", prodcode);
			map.put("ms_makecode", macode);
			if(map.get("ms_indate") == null)
			    map.put("ms_indate", DateUtil.parseDateToString(new Date(),Constant.YMD_HMS));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改MakeSerial
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "MakeSerial", "ms_id"));
		//限制有效的序列条数不能大于工单数量
		int cn = baseDao.getCount("select count(1) cn from makeCraft left join makeSerial on mc_id=ms_mcid where NVL(ms_status,0) <>4 and mc_id="+store.get("mc_id")+" group by mc_id,mc_qty having count(ms_id)>mc_qty");
		if(cn > 0 ){
			BaseUtil.showError("作业单有效序列号数量超过了作业单数量!");
		}				
		baseDao.updateByCondition("makeSerial", "ms_combinecode=ms_sncode", "ms_combinecode is not null and ms_mcid="+store.get("mc_id"));
		baseDao.updateByCondition("makeSerial", "ms_code=ms_sncode", "ms_code is null and ms_mcid="+store.get("mc_id"));
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}
	
	final static String INSERTMAKESERIAL = "Insert into MakeSerial (ms_id, ms_code, ms_sncode, ms_mcid, ms_mccode,ms_prodcode, ms_indate"
			+ ",ms_wccode,ms_craftcode,ms_craftname,ms_nextstepcode,ms_status,ms_makecode) values (MAKESERIAL_SEQ.NEXTVAL,?,?,?,?,?,sysdate,?,?,?,?,0,?)";
	@Override
	@Transactional
	public void occurCode(int id, String prefixcode, String suffixcode, String startno, int number,int combineqty) {		
		String code=null,mccode = null,mcprodcode=null,craftcode=null,craftname=null,wccode=null,nextstepcode=null,macode = null; 
		SqlRowList rs0=baseDao.queryForRowSet("select mc_code,mc_prodcode,ma_craftcode,ma_wccode,ma_id,ma_code,ma_craftname from MakeCraft left join make on mc_makecode=ma_code where mc_id="+id);
		if(rs0.next()){
			mccode=rs0.getString("mc_code");
			mcprodcode=rs0.getString("mc_prodcode");
			craftcode=rs0.getString("ma_craftcode");
			craftname=rs0.getString("ma_craftname");
			wccode=rs0.getString("ma_wccode");
			macode = rs0.getString("ma_code");
		}
		//判断制造单是否完工,完工则不让生成
		Object status = baseDao.getFieldDataByCondition("make", "ma_finishstatuscode", "ma_id="+rs0.getInt("ma_id"));
		if(status != null){
			if(status.toString().equals("COMPLETED")){
				BaseUtil.showError("制造单："+macode+",已完工!");
			}
		}
		rs0=baseDao.queryForRowSet("select cd_stepcode,cd_stepname from craftdetail inner join craft on cr_id=cd_crid where cr_code='"+craftcode+"' order by cd_detno");
		if(rs0.next()){
			nextstepcode=rs0.getString("cd_stepcode");
		}else{
			BaseUtil.showError( "工艺路线未维护，不能生成序列号!");
		}
		//限制有效的工艺条数不能大于工单数量
		int cn = baseDao.getCount("select count(1) cn  from makeCraft left join makeSerial on mc_id=ms_mcid where NVL(ms_status,0) <>4 and mc_id="+id+" group by mc_id,mc_qty having count(ms_id)>mc_qty-"+number);
		if(cn > 0 ){
			BaseUtil.showError("作业单有效序列号数量超过了作业单数量!");
		}
		String ms_combinecode = null ;
		for (int i = 0; i < number; i++) {				
			code = suffixcode == null ? prefixcode + startno : prefixcode + startno + suffixcode;
			//startno ++;
			String num1=Long.parseLong(startno)+1+"";
			if(num1.length() > startno.length()){
				startno = num1;
			}else{				
				startno = startno.substring(0,startno.length()-num1.length())+num1;
			}
			//判断是否存在重复的序列号和流程码
			cn = baseDao.getCount("select count(1) from makeSerial where ms_code='"+code+"'");
			if(cn > 0){
				BaseUtil.showError("流程码:"+code+"重复!");
			}
			cn = baseDao.getCount("select count(1)  from makeSerial where ms_sncode='"+code+"'");
			if(cn > 0){
				BaseUtil.showError("序列号:"+code+"重复!");
			}
			baseDao.execute(INSERTMAKESERIAL, new Object[]{code, code, id,mccode, mcprodcode, wccode,craftcode,craftname,nextstepcode,macode});			
            if(combineqty > 1 ) {//拼板数大于0，每产生n个序列号就以第一个序列号作为拼板号保存到ms_combinecode字段
            	if(i%combineqty == 0){
                	ms_combinecode = code;		  
    			}
    			baseDao.updateByCondition("makeSerial", "ms_combinecode='"+ms_combinecode+"'", "ms_code='"+code+"'");   			
            }
		}
	}

	@Override
	public String checkOrNewBarcode(boolean newSerial, String serialCode,
			int mc_id) {
		if(newSerial){//新生成，将输入的条码插入表
			String mccode = null,mcprodcode=null,craftcode=null,craftname=null,wccode=null,nextstepcode=null,macode = null; 
			SqlRowList rs0=baseDao.queryForRowSet("select mc_code,mc_prodcode,ma_craftcode,ma_wccode,ma_id,ma_code,ma_finishstatuscode from MakeCraft left join make on mc_makecode=ma_code where mc_id="+mc_id);
			if(rs0.next()){
				mccode = rs0.getString("mc_code");
				mcprodcode = rs0.getString("mc_prodcode");
				craftcode = rs0.getString("ma_craftcode");
				craftname = rs0.getString("ma_craftname");
				wccode = rs0.getString("ma_wccode");
				macode = rs0.getString("ma_code");
				//判断制造单是否完工,完工则不让生成			
				if(rs0.getString("ma_finishstatuscode").toString().equals("COMPLETED")){
					BaseUtil.showError("制造单："+macode+",已完工!");
				}				
				rs0=baseDao.queryForRowSet("select cd_stepcode,cd_stepname from craftdetail inner join craft on cr_id=cd_crid where cr_code='"+craftcode+"' order by cd_detno");
				if(rs0.next()){
					nextstepcode=rs0.getString("cd_stepcode");
				}else{
					BaseUtil.showError( "工艺路线未维护，不能生成序列号!");
				}
				//判断是否存在重复的序列号和流程码
				int cn = baseDao.getCount("select count(1) from makeSerial where ms_code='"+serialCode+"'");
				if(cn > 0){
					BaseUtil.showError("流程码:"+serialCode+"重复!");
				}
				cn = baseDao.getCount("select count(1) from makeSerial where ms_sncode='"+serialCode+"'");
				if(cn > 0){
					BaseUtil.showError("序列号:"+serialCode+"重复!");
				}
				baseDao.execute(INSERTMAKESERIAL, new Object[]{serialCode, serialCode, mc_id,mccode, mcprodcode, wccode,craftcode,craftname,nextstepcode,macode});	
				Object ob = baseDao.getFieldDataByCondition("makeSerial", "ms_id", "ms_code='"+serialCode+"'");
				return ob.toString();
			}
		}else{//原有序列号
			//判断序列号是否存在
			Object ob = baseDao.getFieldDataByCondition("makeSerial", "ms_id", "ms_code='"+serialCode+"'");
			if(ob == null){
				BaseUtil.showError("序列号:"+serialCode+"不存在!");
			}else{
				return ob.toString();
			}
		}
		return null;
	}
}

