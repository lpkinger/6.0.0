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
import com.uas.erp.dao.common.SampleDao;
import com.uas.erp.service.scm.SampleService;

@Service
public class SampleServiceImpl implements SampleService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private SampleDao sampleDao;

	@Override
	public void saveSample(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_Sample", "sa_code='" + store.get("sa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存Sample
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_Sample", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存SampleMaterial
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "QUA_SampleDetail", "sd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void updateSampleById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("Sample", "sa_checkstatuscode", "sa_id=" + store.get("sa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改Sample
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Sample", "sa_id");
		baseDao.execute(formSql);
		// 修改SampleMaterial
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SampleDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sd_id") == null || s.get("sd_id").equals("") || s.get("sd_id").equals("0")
					|| Integer.parseInt(s.get("sd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SAMPLEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_SampleDetail", new String[] { "sd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printSample(int sa_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sa_id });
		// 执行打印操作
		// TODO
		// 记录操作
		baseDao.logger.print(caller, "sa_id", sa_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sa_id });
	}

	@Override
	public void deleteSample(int sa_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Sample", "sa_checkstatuscode", "sa_id=" + sa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sa_id });
		// 删除Sample
		baseDao.deleteById("QUA_Sample", "sa_id", sa_id);
		// 删除SampleMaterial
		baseDao.deleteById("QUA_SampleDetail", "sd_said", sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sa_id });
	}

	@Override
	public int turnSendSample(String caller, int id) {
		int ssid = 0;
		// 判断该问题反馈是否已转过BUG
		Object code = baseDao.getFieldDataByCondition("SendSample", "ss_id", "ss_sourceid=" + id);
		if (code != null && !code.equals("")) {
			Object clid = baseDao.getFieldDataByCondition("checklistdetail", "cld_clid", "cld_sourceid=" + id);
			BaseUtil.showError(BaseUtil.getLocalMessage("sys.feedback.haveturnBug")
					+ "<a href=\"javascript:openUrl('jsps/plm/test/newchecklist.jsp?formCondition=cl_idIS" + clid
					+ "&gridCondition=cld_clidIS" + clid + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转BUG
			ssid = sampleDao.turnSendSample(id);
			// 判断已转数量是否大于订单数量，大于则提示不让转
			// 修改问题反馈状态
			baseDao.updateByCondition("CuProductSample", "cps_status='已转送样单',cps_sendstatus='待上传'", "cps_id=" + id);
			// 记录操作
			baseDao.logger.turn("msg.turnBuglist", "Feedback", "fb_id", id);
		}
		return ssid;
	}
}
