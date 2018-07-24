package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageAttendService;
@Service
public class WageAttendServiceImpl implements WageAttendService {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void save(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WageAttend", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "WageAttendDetail","wad_id");
		baseDao.execute(gridSql);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WageAttend", "wa_id");
		baseDao.execute(formSql);
		if (grid!=null) {
			List<String> gridsql = SqlUtil.getUpdateSqlbyGridStore(grid, "WageAttendDetail", "wad_id");
			for (Map<Object, Object> map : grid) {
				Object wad_id = map.get("wad_id");
				if (wad_id == null || wad_id.equals("") || wad_id.equals("0") || Integer.parseInt(wad_id.toString()) == 0) {
					baseDao.execute(SqlUtil.getInsertSql(map, "WageAttendDetail", "wad_id"));
				}
			}
			baseDao.execute(gridsql);
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, grid });
	}

	@Override
	public void delete(int wa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { wa_id });
		// 删除
		baseDao.deleteById("WageAttend", "wa_id", wa_id);
		//删除明细
		baseDao.deleteById("WageAttendDetail", "wad_waid", wa_id);
		// 记录操作
		baseDao.logger.delete(caller, "wa_id", wa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { wa_id });
	}

	@Override
	public void submit(int wa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("WageAttend", new String[] { "wa_statuscode"}, "wa_id=" + wa_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wa_id);
		
		// 当前年月的记录已经存在,不能新增!
		Object wa_date = baseDao.getFieldDataByCondition("WageAttend", "wa_date", "wa_id="+wa_id);
		boolean bool = baseDao.checkByCondition("WageAttend", "wa_date='" + wa_date + "' and  wa_statuscode='AUDITED' ");
		if (!bool) {
			BaseUtil.showError("已存在"+wa_date+"年月的已审核考勤数据");
		}		
		//判断明细行是否有重复的记录
		int count = baseDao.getCount("select count(*) from (select * from (select * from WageAttendDetail left join WageAttend on wad_waid= wa_id ) where wa_id="+wa_id+" ) a " + 
				"where a.wad_emcode in (select   b.wad_emcode from   (select * from (select * from WageAttendDetail left join WageAttend on wad_waid= wa_id ) where wa_id="+wa_id+" ) b group by b.wad_emcode having count (b.wad_emcode) > 1)");
		if (count>0) {
			BaseUtil.showError("明细行存在重复的员工编号!");
		}		
		
		
		// 执行提交操作
		baseDao.submit("WageAttend", "wa_id=" + wa_id, "wa_status", "wa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wa_id", wa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,wa_id);
	}

	@Override
	public void resSubmit(int wa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WageAttend", "wa_statuscode", "wa_id=" + wa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, wa_id);
		// 执行反提交操作
		baseDao.resOperate("WageAttend", "wa_id=" + wa_id, "wa_status", "wa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wa_id", wa_id);
		// 执行反提交后的其他逻辑
		handlerService.afterResSubmit(caller, wa_id);
	}	
	
	@Override
	public void audit(int wa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("WageAttend", new String[] { "wa_statuscode"}, "wa_id=" + wa_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { wa_id });

		// 执行审核操作
		baseDao.audit("WageAttend", "wa_id=" + wa_id, "wa_status", "wa_statuscode","wa_auditdate","wa_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "wa_id", wa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wa_id);
	}

	@Override
	public void resAudit(int wa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WageAttend", "wa_statuscode", "wa_id=" + wa_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { wa_id });
		//判断当前基础项数据是否已计算
		Object wa_date = baseDao.getFieldDataByCondition("WageAttend", "wa_date", "wa_id="+wa_id);
		boolean bool = baseDao.checkByCondition("WageReport", "wr_date='"+wa_date+"'");
		if (!bool) {
			BaseUtil.showError("当前考勤数据已计算，不可反审核");
		}
		
		// 执行反审核操作
		baseDao.resAudit("WageAttend", "wa_id=" + wa_id, "wa_status", "wa_statuscode","wa_auditdate","wa_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "wa_id", wa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wa_id);
	}


}
