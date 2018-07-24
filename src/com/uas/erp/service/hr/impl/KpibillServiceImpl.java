package com.uas.erp.service.hr.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.KpibillService;;
@Service
public class KpibillServiceImpl implements KpibillService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateKpibill(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//修改
		StringBuffer sb=new StringBuffer();
		for (Map<Object, Object> grid : gridstore) {
			int count=baseDao.getCount("select count(1) from Kpidesignitem where ki_id="+grid.get("kbd_kiid")+" and "+grid.get("kbd_score").toString()+">=Ki_Score_From and "+grid.get("kbd_score").toString()+"<=ki_score_to");		
			if(count==0){
				sb.append(grid.get("kbd_detno")).append(",");
			}
		}
		if(sb.length()>0){
			BaseUtil.showError("明细行得分不在评分范围内，行号："+sb.substring(0,sb.length()-1));
		}
		List<String> gridUpdateSql=SqlUtil.getUpdateSqlbyGridStore(gridstore, "Kpibilldetail", "kbd_id");
		baseDao.execute(gridUpdateSql);
		baseDao.execute("update kpibill set kb_score=(select sum(nvl(kbd_score,0)) from kpibilldetail where kbd_kbid="+store.get("kb_id")+") where kb_id="+store.get("kb_id"));
		//记录操作
		baseDao.logger.update(caller, "kb_id", store.get("kb_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteKpibill(int kb_id, String caller) {
			//执行删除前的其它逻辑
			handlerService.handler(caller, "delete", "before", new Object[]{kb_id});
			//删除主表
			baseDao.deleteById("Kpibill", "kb_id", kb_id);
			//删除明细
			baseDao.deleteById("Kpibilldetail", "kbd_kbid", kb_id);
			//记录操作
			baseDao.logger.delete(caller, "kb_id", kb_id);
			//执行删除后的其它逻辑
			handlerService.handler(caller, "delete", "after", new Object[]{kb_id});	
	}
	@Override
	public void submitKpibill(int kb_id, String caller) throws ParseException {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Kpibill","kb_statuscode", "kb_id=" + kb_id);
		StateAssert.submitOnlyEntering(status);
		Object date = baseDao.getFieldDataByCondition("Kpibill","kb_enddate", "kb_id=" + kb_id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String today=sdf.format(new Date());
		if(sdf.parse(date.toString()).before(sdf.parse(today))){
			BaseUtil.showError("时间已超过评分截止日期，无法提交！");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { kb_id});
		String MaxScore = baseDao.getDBSetting(caller,"MaxScore");
		if(MaxScore!=null&& Integer.parseInt(MaxScore)>0){
			Object score_sum=baseDao.getFieldDataByCondition("Kpibilldetail", "sum(nvl(kbd_score,0))", "kbd_kbid="+kb_id);
			if(Double.parseDouble(score_sum.toString())>=Double.parseDouble(MaxScore)){
				BaseUtil.showError("总分要小于"+MaxScore+"分");
			}
		}
		// 执行提交操作
		baseDao.submit("Kpibill", "kb_id=" + kb_id, "kb_status", "kb_statuscode");
		String sql="update Kpibill set kb_evaluatedate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+" where kb_id="+kb_id;
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.submit(caller, "kb_id", kb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { kb_id});
		
	}
	@Override
	public void resSubmitKpibill(int kb_id, String caller) throws ParseException {
		Object status = baseDao.getFieldDataByCondition("Kpibill","kb_statuscode", "kb_id=" + kb_id);
		StateAssert.resSubmitOnlyCommited(status);
		Object date = baseDao.getFieldDataByCondition("Kpibill","kb_enddate", "kb_id=" + kb_id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String today=sdf.format(new Date());
		if(sdf.parse(date.toString()).before(sdf.parse(today))){
			BaseUtil.showError("当前时间已超过有效期，不允许反提交！");
		}
		handlerService.beforeResSubmit(caller,new Object[] { kb_id});
		// 执行反提交操作
		baseDao.resOperate("Kpibill", "kb_id=" + kb_id, "kb_status", "kb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "kb_id", kb_id);
		handlerService.afterResSubmit(caller,new Object[] { kb_id});
		
	}
	@Override
	public String[] printKpibill(int kb_id, String caller, String reportName,
			String condition) {
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "kb_id", kb_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { kb_id });
		return keys;
	}
	@Override
	public Map<String, Object> getScorefrom(String kt_kdbid, String kt_bemanid,
			String ktd_kiid) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String sql="select kb_type as type,kb_percent as percent,COUNT(kb_type) as count,avg(kbd_score) as avg from kpibill left join kpibilldetail on kb_id=kbd_kbid where KB_KDBID="+kt_kdbid+" and KB_BEMANID="+kt_bemanid+" and kbd_kiid="+ktd_kiid+" and kb_statuscode='COMMITED' group by kb_type, kb_percent";
		SqlRowList rs=baseDao.queryForRowSet(sql);
		StringBuffer sb=new StringBuffer("(");
		StringBuffer sb1=new StringBuffer("(");
		double a=0,b=0;
		while(rs.next()) {
			Map<String, Object> data=new HashMap<String, Object>();
			data.put("type", rs.getString("type"));
			data.put("percent", rs.getDouble("percent"));
			data.put("count", rs.getInt("count"));
			data.put("avg", rs.getDouble("avg"));
			datas.add(data);	
			if(rs.hasNext()){
					sb.append(rs.getDouble("avg")+"*"+rs.getDouble("percent")+"%+");	
					sb1.append(rs.getDouble("percent")+"%+");	
					a+=rs.getDouble("avg")*rs.getDouble("percent");
					b+=rs.getDouble("percent");		
			}else{
					sb.append(rs.getDouble("avg")+"*"+rs.getDouble("percent")+"%)/");
					sb1.append(rs.getDouble("percent")+"%)");
					a+=rs.getDouble("avg")*rs.getDouble("percent");
					b+=rs.getDouble("percent");
			}
		}
		String dataString=BaseUtil.parseGridStore2Str(datas);
		map.put("data",dataString);
		map.put("cal",sb.toString()+sb1.toString()+"="+(a/b));
		return map;
	}
}
