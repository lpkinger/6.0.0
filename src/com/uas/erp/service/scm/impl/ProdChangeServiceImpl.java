package com.uas.erp.service.scm.impl;

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
import com.uas.erp.service.scm.ProdChangeService;


@Service("ProdChangeService")
public class ProdChangeServiceImpl implements ProdChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	static final String UPDATEPROD = "update product set pr_defaultused=?,pr_autoinquirydays=?,pr_jtcycle=?,pr_jtinquirydate=?,pr_jtnextdate=?,pr_targetprice=?,pr_targetqty=? where pr_code=?";
	
	@Override
	public void saveProdChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProdChange", "pc_code='" + store.get("pc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存ProdChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存ProdChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "ProdChangeDetail", "pcd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteProdChange(int pc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pc_id });
		// 删除ProdChange
		baseDao.deleteById("ProdChange", "pc_id", pc_id);
		// 删除ProdChangeDetail
		baseDao.deleteById("ProdChangedetail", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pc_id });
	}

	@Override
	public void updateProdChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + store.get("pc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改ProdChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdChange", "pc_id");
		baseDao.execute(formSql);
		// 修改ProdChangeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdChangeDetail", "pcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pcd_id") == null || s.get("pcd_id").equals("") || s.get("pcd_id").equals("0")
					|| Integer.parseInt(s.get("pcd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ProdChangeDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdChangeDetail", new String[] { "pcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printProdChange(int pc_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pc_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "pc_id", pc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pc_id });
	}

	@Override
	public void auditProdChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pc_id });
		SqlRowList rs = baseDao.queryForRowSet("select * from ProdChangeDetail where pcd_pcid="+pc_id);
		while(rs.next()){
				baseDao.execute(UPDATEPROD,new Object[]{rs.getObject("pcd_use"),rs.getObject("pcd_newiqdays"),rs.getObject("pcd_newjtcycle"),rs.getObject("pcd_newjtinquirydate"),rs.getObject("pcd_newjtnextdate"),
						rs.getObject("pcd_newtargetprice"),rs.getObject("pcd_newtargetqty"),rs.getObject("pcd_prcode")});
		}
		// 执行审核操作
		baseDao.audit("ProdChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pc_id });
	}

	@Override
	public void resAuditProdChange(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProdChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
	}

	@Override
	public void submitProdChange(int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		String remark = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('行'||pcd_detno||':'||err_info||' ') from (select pcd_detno,"
						+ " case when pcd_newiqdays<>pcd_iqdays then '动态询价天数.' else '' end ||"
						+ " case when pcd_newjtcycle<>pcd_jtcycle then '静态询价周期.' else '' end ||"
						+ " case when pcd_newtargetqty<>pcd_targetqty then '目标数量.' else '' end ||"
						+ " case when pcd_newtargetprice<>pcd_targetprice then '目标金额.' else '' end err_info"
						+ " from ProdChangeDetail where pcd_pcid=?  and rownum<=30) where err_info is not null", String.class, pc_id);
		if (remark != null) {
			baseDao.execute("update ProdChange set pc_info=? where pc_id=?", remark, pc_id);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("ProdChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}

	@Override
	public void resSubmitProdChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pc_id });
		baseDao.execute("update ProdChange set pc_info=null where pc_id=?",  pc_id);
		// 执行反提交操作
		baseDao.resOperate("ProdChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
	}
}
