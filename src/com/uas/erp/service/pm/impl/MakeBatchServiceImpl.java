package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hsqldb.DatabaseURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.impl.MakeDaoImpl;
import com.uas.erp.model.Employee;
import com.uas.erp.service.pm.MakeBaseService;
import com.uas.erp.service.pm.MakeBatchService;

@Service("MakeBatchService")
public class MakeBatchServiceImpl implements MakeBatchService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private MakeDao makeDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private MakeBaseService makeBaseService;
	
	@Override
	public void cleanMakeBatch(int mb_id,String caller) {
		baseDao.deleteByCondition("MakeBatch", "mb_id='"+mb_id+"'");		
		Employee employee = SystemSession.getUser();
		baseDao.execute("delete from MakeBatch where mb_emid="+employee.getEm_id());
	}
	
	@Override
	public void cleanFailed(int mb_id,String caller) { 
		Employee employee = SystemSession.getUser();
		SqlRowList rs =baseDao.queryForRowSet("select count(1) c,wm_concat(ma_code) macode from makebatch left join make on mb_makecode=ma_code where mb_emid="+mb_id+" and ma_statuscode<>'ENTERING' and mb_result in('算料失败','审核失败')");
		if (rs.next()){
			if(rs.getInt("c")>0){
				BaseUtil.showError("制造单号"+rs.getString("macode")+"状态不是在录入，不能删除！");
			}
		}
		rs =baseDao.queryForRowSet("select ma_code,ma_id,case when ma_tasktype='OS' then 'Make' else 'Make!Base' end makecaller from makebatch left join make on mb_makecode=ma_code where mb_emid="+mb_id+" and ma_statuscode='ENTERING' and mb_result in('算料失败','审核失败')");
		while (rs.next()){
			//删除导入失败的在录入制造单
			makeBaseService.deleteMakeBase(rs.getInt("ma_id"), rs.getString("makecaller"));
		}
		baseDao.execute("delete from MakeBatch where mb_emid="+employee.getEm_id()+" and mb_result in('算料失败','审核失败')");
	}
	
	@Override
	public void batchToMake(int mb_id){
		Employee employee = SystemSession.getUser();
		String caller ="Make!Base";
		SqlRowList rs =baseDao.queryForRowSet("select   count(1) c,wm_concat(ma_code) macode from makebatch left join make on mb_makecode=ma_code where mb_emid="+mb_id+" and ma_id>0");
		if (rs.next()){
			if(rs.getInt("c")>0){
				BaseUtil.showError("制造单号"+rs.getString("macode")+"已经存在，不能导入！");
			}
		}
		List<Object[]> objs=baseDao.getFieldsDatasByCondition("MAKEBATCH", new String []{"mb_id","mb_kind","mb_makecode","mb_tasktype","mb_prodcode","mb_qty","mb_planbegindate","mb_planenddate","mb_salecode","mb_saledetno","mb_wccode","mb_bonded","mb_teamcode","mb_cop","mb_remark"}, "mb_emid="+mb_id);
		if(objs!=null){
			for(Object os[]:objs){
				String oldCode=null;
				if(os[2]!=null){
					oldCode = os[2].toString();
				}else{					
					oldCode = baseDao.sGetMaxNumber("Make", 2);
					Object newLCode = baseDao.getFieldDataByCondition("MAKEKIND", "mk_excode", "mk_name='" + os[1] + "'");
					if (newLCode != null) {
						if (!newLCode.toString().equals("")) {
							// 修改前缀
							oldCode = newLCode.toString() + oldCode;
						}
					}					
				}
				if (os[3].equals("委外") || os[3].equals("OS")){
					caller="Make";
				}
				baseDao.execute("update makebatch set mb_makecode='"+oldCode+"' where mb_id="+os[0]);
				int countnum =baseDao.getCount("select count(*) from make where ma_code='"+oldCode+"'");
				if(countnum>0){					
					BaseUtil.showError("制造单号"+oldCode+"已经存在！");
				}else{
					int id = baseDao.getSeqId("MAKE_SEQ");

					String insertsql="insert into make(ma_id,ma_status,ma_statuscode,ma_checkstatus,ma_checkstatuscode,ma_finishstatus,ma_finishstatuscode,ma_turnstatus,ma_turnstatuscode,ma_kind,ma_code,ma_tasktype,ma_prodcode," +
							"ma_qty,ma_planbegindate,ma_planenddate)values("+id+",'"+BaseUtil.getLocalMessage("ENTERING")+"','"+"ENTERING"+"','"+BaseUtil.getLocalMessage("UNAPPROVED")+"','"+"UNAPPROVED"+"','"+BaseUtil.getLocalMessage("UNCOMPLET")+"','"+"UNCOMPLET"+"','"+BaseUtil.getLocalMessage("UNGET")+"','"+"UNGET"+"','"+os[1]+"','"+oldCode+"','"+os[3]+"','"+os[4]+"',"+Integer.parseInt(os[5].toString())+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, os[6].toString())+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, os[7].toString())+")";
					baseDao.execute(insertsql);
					baseDao.execute("UPDATE MAKE SET MA_TASKTYPE='MAKE' WHERE MA_CODE='"+oldCode +"' AND MA_TASKTYPE='制造'");
					baseDao.execute("UPDATE MAKE SET MA_TASKTYPE='OS' WHERE MA_CODE='"+oldCode +"' AND MA_TASKTYPE='委外'");

					Object masql=baseDao.getFieldDataByCondition("detailgrid", "wm_concat(dg_field)", "dg_caller='MakeBatch' and dg_field like 'ma_%'");
					if(masql != null && !masql.equals("")){
						String mbsql = "update make set ("+masql+") = (select "+masql+" from makebatch where mb_emid="+employee.getEm_id() +" and mb_makecode = ma_code) where ma_code in (select mb_makecode from makebatch where mb_emid="+mb_id+")";
						baseDao.execute(mbsql);
					} 
					baseDao.execute("update make set (ma_recorder,ma_salecode,ma_saledetno,ma_wccode,ma_bonded,ma_teamcode,ma_cop,ma_remark)=(select '"+employee.getEm_name()+"',mb_salecode,mb_saledetno,mb_wccode,mb_bonded,mb_teamcode,mb_cop,mb_remark from makebatch where mb_id="+os[0]+" ) where ma_id="+id);
					try{
						makeBaseService.setMakeMaterial(oldCode, caller); 
					}catch(Exception e){
						baseDao.execute("update makebatch set mb_result='算料失败' where mb_id="+os[0]);
						continue;
					} 
					if(baseDao.isDBSetting("MakeBatch", "autoaudit")){
						try{
							makeBaseService.auditMakeBase(id, caller); 
						}catch(Exception e){
							baseDao.execute("update makebatch set mb_result='审核失败' where mb_id="+os[0]);
							continue;
						}
					}
					baseDao.execute("update makebatch set mb_result='导入成功' where mb_id="+os[0]); 
				}
			}
		}
	}
	
	@Override
	public void updateMakeBatchById(String formStore, String param,String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		handlerService.beforeSave("MakeBatch", new Object[] {gstore});        
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MakeBatch", "mb_id");
		for (Map<Object, Object> s : gstore) {
			int kindnum = baseDao.getCount("select count(*) from makekind where mk_name='"+s.get("mb_kind")+"'");
		    if(kindnum > 0){
		     
		    }else{
		    	BaseUtil.showError("工单类型"+s.get("mb_kind")+"不存在");
		    }
			if (s.get("mb_id") == null || s.get("mb_id").equals("") || s.get("mb_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEBATCH_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MAKEBATCH", new String[] { "mb_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
			baseDao.execute("update makebatch set mb_makecode=ltrim(rtrim(mb_makecode)) where mb_id=?", s.get("mb_id"));
			baseDao.execute("update makebatch set mb_salecode=ltrim(rtrim(mb_salecode)) where mb_id=?", s.get("mb_id"));
		}
		baseDao.execute(gridSql); 
		// 执行修改后的其它逻辑
		handlerService.afterSave("MakeBatch", new Object[] {gstore});		
	}

	@Override
	public String getcode(String caller, String table, int type, String conKind) {
		// TODO Auto-generated method stub
		if(table == null || table.equals("")){
			table = (String)baseDao.getFieldDataByCondition("gird","dg_table", "dg_caller='" + caller + "'");
		}
		String oldcode = baseDao.sGetMaxNumber(table.split("")[0], type);
		Object newcode = baseDao.getFieldDataByCondition("MakeBatch", "mb_makecode","mb_kind='"+conKind+"'");
		if(newcode !=null){
			if(!newcode.toString().equals("")){
				oldcode = newcode.toString() + oldcode;
			}
		}
		return oldcode;
	}

	@Override
	public void makeupdateDatalist(Employee employee, String caller, String data) {
		//计划开工日不能比计划完工日晚，并且 单据状态必须是：已审核、不等于已结案、不等于已完工
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object ma_planbegindate = map.get("ma_planbegindate");
			Object ma_planenddate = map.get("ma_planenddate");
			int compare = DateUtil.compare(ma_planenddate.toString(), ma_planbegindate.toString());
			if(compare<0){
				BaseUtil.showError("计划开工日期不能大于计划完工日期！");
			}
			int count = baseDao.getCount("select count(1) from make where ma_id="+map.get("ma_id")+" and nvl(ma_status,' ')<>'已结案' and nvl(ma_status,' ')='已审核' and nvl(ma_finishstatus,' ')<>'已完工'");
			if(count==0){
				BaseUtil.showError("更改的工单必须是已审核、且不等于已结案、已完工！");
			}
			String sql ="update make set ma_wccode='"+map.get("ma_wccode")+"',ma_planbegindate=to_date('"+ma_planbegindate+"','yyyy-mm-dd'),ma_planenddate=to_date('"+ma_planenddate+"','yyyy-mm-dd'),ma_teamcode='"+map.get("ma_teamcode")+"' where ma_id='"+map.get("ma_id")+"'";
			baseDao.execute(sql);
			Object ma_tasktype = baseDao.getFieldDataByCondition("make", "ma_tasktype", "ma_id="+map.get("ma_id"));
			if("MAKE".equals(ma_tasktype)){
				ma_tasktype="Make!Base";
			}else{
				ma_tasktype="Make";
			}
			baseDao.logger.others("工单更新", "修改计划开工日、计划完工日、工作中心", ma_tasktype.toString(), "ma_id", map.get("ma_id"));
		}
	}
}
