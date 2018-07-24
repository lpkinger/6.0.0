package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.KpiApplyDao;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Sign;
import com.uas.erp.service.hr.KpiApplyService;

@Service
public class KpiApplyServiceImpl implements KpiApplyService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private KpiApplyDao kpiApplyDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;

	@Override
	public void saveKpiApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KpiApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ks_id", store.get("ks_id"));
		handlerService.afterSave(caller,new Object[]{store});

	}

	@Override
	public void updateKpiApply(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KpiApply", "ka_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "KpiApplyDet", "kad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("kad_id") == null || s.get("kad_id").equals("") || s.get("kad_id").equals("0") ||
					Integer.parseInt(s.get("kad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("KPIAPPLYDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "KPIAPPLYDET",
						new String[] { "kad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ka_id", store.get("ka_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteKpiApply(int ka_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{ka_id});
		//删除
		baseDao.deleteById("KpiApply", "ka_id", ka_id);
		baseDao.deleteById("KpiApplyDet", "kad_kaid", ka_id);
		//记录操作
		baseDao.logger.delete(caller, "ka_id", ka_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{ka_id});
	}

	@Override
	public void auditKpiApply(int ka_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KpiApply", "ka_statuscode", "ka_id=" + ka_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ka_id);
		baseDao.callProcedure("KPI.turnKpidesign_kpi", new Object[] {ka_id});
		//执行审核操作
		baseDao.audit("KpiApply", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "ka_id", ka_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ka_id);
	}

	@Override
	public void resAuditKpiApply(int ka_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("KpiApply", "ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("Contract", ka_id);
		//执行反审核操作
		baseDao.resOperate("KpiApply", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ka_id", ka_id);
	}

	@Override
	public void submitKpiApply(int ka_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KpiApply", "ka_statuscode", "ka_id=" + ka_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ka_id);
		// TODO 在这个地方增加一个更新可以直接把所有ID全部更新上去的点。  校验+更新
		CheckName(ka_id);
		UpdateId(ka_id);
		//执行提交操作
		baseDao.submit("KpiApply", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ka_id", ka_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ka_id);
	}

	@Override
	public void resSubmitKpiApply(int ka_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KpiApply", "ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, ka_id);
		//执行反提交操作
		baseDao.resOperate("KpiApply", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ka_id", ka_id);
		handlerService.afterResSubmit(caller, ka_id);
	}

	@Override
	public GridPanel getGridPanel(String caller, String condition,
			Integer start, Integer end, Integer _m) {
		Employee employee = SystemSession.getUser();
		String _master=employee!=null?employee.getEm_master():SpObserver.getSp();
		GridPanel gridPanel = new GridPanel();
		String c = condition.replaceAll("kad_kaid","ka_id");
		Object ktcode = baseDao.getFieldDataByCondition("KpiApply", "ka_ktcode", c);
		int count=baseDao.getCount("select count(1) from Kpigradetype where kp_ktcode='"+ktcode+"'");
		List<DetailGrid> detailGrids=new ArrayList<DetailGrid>();
		if(count==0){
			BaseUtil.showError("未找到所属类型编号为"+ktcode+"的评分类型");
		}else{	
			detailGrids = kpiApplyDao.getGridsByCaller(caller, ktcode.toString(), count, _master);
			if (detailGrids != null && detailGrids.size() > 0) {
				List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, _master);
				List<GridFields> fields = new ArrayList<GridFields>();// grid
				List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
				List<LimitFields> limits = new ArrayList<LimitFields>();
				gridPanel.setLimits(limits);// 权限控制字段
				for (DetailGrid grid : detailGrids) {
					fields.add(new GridFields(grid));
					columns.add(new GridColumns(grid, combos));
				}
				gridPanel.setGridColumns(columns);
				gridPanel.setGridFields(fields);
				List<DBFindSetGrid> dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
				List<Dbfind> dbfinds = new ArrayList<Dbfind>();
				for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
					dbfinds.add(new Dbfind(dbFindSetGrid));
				}
				gridPanel.setDbfinds(dbfinds);
				if (!condition.equals("")) {
					gridPanel.setDataString(baseDao.getDataStringByDetailGrid(detailGrids, condition, start, end));
				}
			}
		}

		return gridPanel;
	}
	/**
	 * maz 考核申请校验+更新id
	 * @param ka_id
	 */
	public void CheckName(int id){
		String msg = "";
		SqlRowList rs = baseDao.queryForRowSet("select * from KpiApplyDet where kad_kaid="+id);
		while(rs.next()){
			String sb = "";
			if(StringUtil.hasText(rs.getString("kad_beman")) && !StringUtil.hasText(rs.getString("kad_bemanid"))){
				sb = sb + Check(rs.getString("kad_beman"));
			}
			baseDao.execute("update KpiApplyDet a set kad_kscode=(select ks_code from kpischeme where ks_name=a.kad_ksname) where kad_id="+rs.getInt("kad_id"));
			for(int i=1;i<=6;i++){
				if(StringUtil.hasText(rs.getString("kad_names"+i+"")) && rs.getString("kad_names"+i+"").contains("#") && !StringUtil.hasText(rs.getString("kad_ids"+i+""))){
					String[] names = rs.getString("kad_names"+i+"").split("#");
					for(int j=0;j<names.length;j++){
						sb = sb + Check(names[j]);
					}
				}else if(StringUtil.hasText(rs.getString("kad_names"+i+"")) && !rs.getString("kad_names"+i+"").contains("#") && !StringUtil.hasText(rs.getString("kad_ids"+i+""))){
					sb = sb + Check(rs.getString("kad_names"+i+""));
				}
			}
			if(!"".equals(sb) && sb != null){
				msg = msg + rs.getInt("kad_detno")+"行："+ sb +"<br>";
			}
		}
		if(!"".equals(msg)){
			BaseUtil.showError(msg);
		}
	}
	public String Check(String name){
		String log = "";
		int i = baseDao.getCount("select count(*) from employee where em_name='"+name+"'  and em_class<>'离职'");
		if(i>1){
			log = ""+name+"存在同名,请手动选择放大镜更新;";
		}else if(i==0){
			log = ""+name+"姓名有误;";
		}
		return log;
	}
	//更新ID
	public void UpdateId(int id){
		SqlRowList rs = baseDao.queryForRowSet("select * from KpiApplyDet where kad_kaid="+id);
		while(rs.next()){
			baseDao.execute("update KpiApplyDet a set kad_bemanid=(select em_id from employee where em_name=a.kad_beman and em_class<>'离职') where kad_id="+rs.getInt("kad_id")+" and nvl(kad_bemanid,0)=0");
			for(int i=1;i<=6;i++){
				if(StringUtil.hasText(rs.getString("kad_names"+i+"")) && rs.getString("kad_names"+i+"").contains("#") && !StringUtil.hasText(rs.getString("kad_ids"+i+""))){
					String[] names = rs.getString("kad_names"+i+"").split("#");
					String ids = "";
					for(int j=0;j<names.length;j++){
						Object em_id = baseDao.getFieldDataByCondition("Employee", "em_id", "em_name='"+names[j]+"'");
						ids = ids + em_id + "#";
					}
					ids = ids.substring(0,ids.length()-1);
					baseDao.execute("update KpiApplyDet set kad_ids"+i+"='"+ids+"' where kad_id="+rs.getInt("kad_id"));
				}else if(StringUtil.hasText(rs.getString("kad_names"+i+"")) && !rs.getString("kad_names"+i+"").contains("#") && !StringUtil.hasText(rs.getString("kad_ids"+i+""))){
					baseDao.execute("update KpiApplyDet a set kad_ids"+i+"=(select em_id from employee where em_name=a.kad_names"+i+") where kad_id="+rs.getInt("kad_id"));
				}
			}
		}
	}
}
