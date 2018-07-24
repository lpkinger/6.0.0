package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.QUABatchService;

@Service("QUABatchService")
public class QUABatchServiceImpl implements QUABatchService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveQUABatch(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUABatch", "qba_code='" + store.get("qba_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("QUABatch", "save", "before", new Object[] { store, grid });
		// 保存QUABatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUABatch", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存QUABatchDetail
		for (Map<Object, Object> m : grid) {
			m.put("qbd_id", baseDao.getSeqId("QUABatchDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "QUABatchDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "qba_id", store.get("qba_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("QUABatch", "save", "after", new Object[] { store, grid });
	}
	@Override
	public void updateQUABatchById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + store.get("qba_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler("QUABatch", "save", "before", new Object[] { store, gstore });
		// 修改QUABatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUABatch", "qba_id");
		baseDao.execute(formSql);
		// 修改QUABatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "QUABatchDetail", "qbd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("qbd_id") == null || s.get("qbd_id").equals("") || s.get("qbd_id").equals("0")
					|| Integer.parseInt(s.get("qbd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUABATCHDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "QUABatchDetail", new String[] { "qbd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "qba_id", store.get("qba_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("QUABatch", "save", "after", new Object[] { store, gstore });
	}
	@Override
	public void deleteQUABatch(int qba_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + qba_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("QUABatch", "delete", "before", new Object[] { qba_id });
		// 删除QUABatch
		baseDao.deleteById("QUABatch", "qba_id", qba_id);
		// 删除QUABatchDetail
		baseDao.deleteById("QUABatchdetail", "qbd_qbaid", qba_id);
		// 记录操作
		baseDao.logger.delete(caller, "qba_id", qba_id);
		// 执行删除后的其它逻辑
		handlerService.handler("QUABatch", "delete", "after", new Object[] { qba_id });
	}
	@Override
	public void auditQUABatch(int qba_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + qba_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler("QUABatch", "audit", "before", new Object[]{qba_id});
		baseDao.execute("merge into  batch using (select qbd_baid,qbd_newvalidtime from quabatchdetail where  qbd_qbaid=?)S on (S.qbd_baid=ba_id) when matched then update set ba_validtime=qbd_newvalidtime where ba_id in (select qbd_baid from quabatchdetail where  qbd_qbaid=?)", qba_id, qba_id );
		//执行审核操作
		baseDao.audit("QUABatch", "qba_id=" + qba_id, "qba_status", "qba_statuscode", "qba_auditdate", "qba_auditman");
		baseDao.execute("update batch set ba_validtime = (select qba_date from QUABatch where qba_id = ?) where (ba_code,ba_prodcode,ba_whcode)"
				+" in (select qbd_batchcode,qbd_prodcode,qbd_whcode from QUABatch left join QUABATCHDETAIL on qba_id = qbd_qbaid where qba_id = ?)",qba_id,qba_id);
		//

		/**
		 * 库存检验单审核后：如果批次复检结果全部合格 且当前批号处于待检冻结状态审核后自动解锁 更新批号状态为合格、非冻结
		 */
		baseDao.execute("update batch set BA_QUALITYSTATUS='合格',ba_kind=0 where (ba_code,ba_prodcode,ba_whcode) in ( select qbd_batchcode,qbd_prodcode,qbd_whcode from QUABATCHDETAIL where qbd_qbaid=? and qbd_qty=qbd_okqty and BA_QUALITYSTATUS='待检' and ba_kind=-1 )", qba_id);
		//记录操作
		baseDao.logger.audit(caller, "qba_id", qba_id);
		//执行审核后的其它逻辑
		handlerService.handler("QUABatch", "audit", "after", new Object[]{qba_id});
	}
	@Override
	public void resAuditQUABatch(int qba_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + qba_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("QUABatch", "qba_id=" + qba_id, "qba_status", "qba_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "qba_id", qba_id);
	}
	@Override
	public void submitQUABatch(int qba_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + qba_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler("QUABatch", "commit", "before", new Object[]{qba_id});
		Object detno = baseDao.getFieldDataByCondition("QUABATCHDETAIL left join batch on qbd_batchcode=ba_code", "WMSYS.WM_CONCAT(qbd_detno) ", 
				"qbd_qbaid="+qba_id+" and nvl(qbd_okqty,0)+nvl(qbd_ngqty,0)<>nvl(qbd_qty,0) ");
		if (detno != null) {
			BaseUtil.showError("明细行合格数量与不合格数量之和与数量不相等，行号：" + detno);
		}
		//执行提交操作
		baseDao.submit("QUABatch", "qba_id=" + qba_id, "qba_status", "qba_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "qba_id", qba_id);
		//执行提交后的其它逻辑
		handlerService.handler("QUABatch", "commit", "after", new Object[]{qba_id});
	}
	@Override
	public void resSubmitQUABatch(int qba_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("QUABatch", "qba_statuscode", "qba_id=" + qba_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("QUABatch", "resCommit", "before", new Object[]{qba_id});
		//执行反提交操作
		baseDao.resOperate("QUABatch", "qba_id=" + qba_id, "qba_status", "qba_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "qba_id", qba_id);
		handlerService.handler("QUABatch", "resCommit", "after", new Object[]{qba_id});
	}
}
