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
import com.uas.erp.service.hr.WageBaseService;
@Service
public class WageBaseServiceImpl implements WageBaseService {
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
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WageBase", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "WageBaseDetail","wbd_id");
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
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WageBase", "wb_id");
		baseDao.execute(formSql);
		if (grid!=null) {
			List<String> gridsql = SqlUtil.getUpdateSqlbyGridStore(grid, "WageBaseDetail", "wbd_id");
			for (Map<Object, Object> map : grid) {
				Object wbd_id = map.get("wbd_id");
				if (wbd_id == null || wbd_id.equals("") || wbd_id.equals("0") || Integer.parseInt(wbd_id.toString()) == 0) {
					baseDao.execute(SqlUtil.getInsertSql(map, "WageBaseDetail", "wbd_id"));
				}
			}
			baseDao.execute(gridsql);
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, grid });
	}

	@Override
	public void delete(int wb_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { wb_id });
		// 删除
		baseDao.deleteById("WageBase", "wb_id", wb_id);
		//删除明细
		baseDao.deleteById("WageBaseDetail", "wbd_wbid", wb_id);
		// 记录操作
		baseDao.logger.delete(caller, "wb_id", wb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { wb_id });
	}

	@Override
	public void submit(int wb_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("WageBase", new String[] { "wb_statuscode"}, "wb_id=" + wb_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wb_id);
		// 当前年月的记录已经存在,不能新增!
		Object wb_date = baseDao.getFieldDataByCondition("WageBase", "wb_date", "wb_id="+wb_id);
		boolean bool = baseDao.checkByCondition("WageBase", "wb_date='" + wb_date + "' and  wb_statuscode='AUDITED' ");
		if (!bool) {
			BaseUtil.showError("已存在"+wb_date+"年月的已审核基础项数据");
		}		
		//判断明细行是否有重复的记录
		int count = baseDao.getCount("select count(*) from (select * from (select * from WAGEBASEDETAIL left join wageBase on wbd_wbid= wb_id ) where wb_id="+wb_id+" ) a " + 
				"where a.wbd_emcode in (select   b.wbd_emcode from   (select * from (select * from WAGEBASEDETAIL left join wageBase on wbd_wbid= wb_id) where wb_id="+wb_id+" ) b group by b.wbd_emcode having count (b.wbd_emcode) > 1)");
		if (count>0) {
			BaseUtil.showError("明细行存在重复的员工编号!");
		}
		
		// 执行提交操作
		baseDao.submit("WageBase", "wb_id=" + wb_id, "wb_status", "wb_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wb_id", wb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,wb_id);
	}

	@Override
	public void resSubmit(int wb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WageBase", "wb_statuscode", "wb_id=" + wb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, wb_id);
		// 执行反提交操作
		baseDao.resOperate("WageBase", "wb_id=" + wb_id, "wb_status", "wb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wb_id", wb_id);
		// 执行反提交后的其他逻辑
		handlerService.afterResSubmit(caller, wb_id);
	}	
	
	@Override
	public void audit(int wb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("WageBase", new String[] { "wb_statuscode"}, "wb_id=" + wb_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { wb_id });
		// 执行审核操作
		baseDao.audit("WageBase", "wb_id=" + wb_id, "wb_status", "wb_statuscode","wb_auditdate","wb_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "wb_id", wb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wb_id);
	}

	@Override
	public void resAudit(int wb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WageBase", "wb_statuscode", "wb_id=" + wb_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { wb_id });
		//判断当前基础项数据是否已计算
		Object wb_date = baseDao.getFieldDataByCondition("WageBase", "wb_date", "wb_id="+wb_id);
		boolean bool = baseDao.checkByCondition("WageReport", "wr_date='"+wb_date+"'");
		if (!bool) {
			BaseUtil.showError("当前基础项数据已计算，不可反审核");
		}
		// 执行反审核操作
		baseDao.resAudit("WageBase", "wb_id=" + wb_id, "wb_status", "wb_statuscode","wb_auditdate","wb_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "wb_id", wb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wb_id);
	}


}
