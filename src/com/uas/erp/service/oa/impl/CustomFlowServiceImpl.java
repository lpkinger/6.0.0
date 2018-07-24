package com.uas.erp.service.oa.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jbpm.api.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.common.impl.JbpmxmlService;
import com.uas.erp.service.oa.CustomFlowService;

@Service
public class CustomFlowServiceImpl implements CustomFlowService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProcessService processService;

	@Override
	public void saveCustomFlow(String formStore, String gridStore, String  caller) {

		String xml = null;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CustomFlow", "cf_caller='" + store.get("cf_caller") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		// handlerService.handler("CustomFlow", "save", "before", new
		// Object[]{formStore, language});
		// 保存FeeClaim
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomFlow", new String[] {}, new Object[] {});

		baseDao.execute(formSql);

		// //保存FeeClaimDetail
		Object[] cfd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据
			String[] datas = gridStore.split("},");
			cfd_id = new Object[datas.length];

			// 在此处 处理 gridStore;
			List<String> gStore = new LinkedList<String>();
			gStore.add(datas[0].substring(1, datas[0].length()) + "}");
			gStore.add(datas[datas.length - 1].substring(0, datas[datas.length - 1].length() - 1));
			for (int j = 1; j < datas.length - 1; j++) {
				gStore.add(datas[j] + "}");
			}
			List<Map<Object, Object>> gStoreMap = new LinkedList<Map<Object, Object>>();
			for (int i = 0; i < datas.length; i++) {
				cfd_id[i] = baseDao.getSeqId("CUSTOMFLOWDETAIL_SEQ");
				Map<Object, Object> gstore = BaseUtil.parseFormStoreToMap(gStore.get(i));

				gstore.put("cfd_code", "审批步骤" + gstore.get("cfd_detno").toString());
				gStoreMap.add(gstore);
			}
			xml = new JbpmxmlService().getXmlByCustomFlow(formStore, gStoreMap);
		} else {
			cfd_id[0] = baseDao.getSeqId("CUSTOMFLOWDETAIL_SEQ");
			Map<Object, Object> gstore = BaseUtil.parseFormStoreToMap(gridStore.substring(1, gridStore.length() - 1));
			gstore.put("cfd_code", "审批步骤" + gstore.get("cfd_detno").toString());
			List<Map<Object, Object>> gStoreMap = new LinkedList<Map<Object, Object>>();
			gStoreMap.add(gstore);
			xml = new JbpmxmlService().getXmlByCustomFlow(formStore, gStoreMap);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "CustomFlowDetail", "cfd_id", cfd_id);
		baseDao.execute(gridSql);

		try {
			processService.setUpProcess(xml, store.get("cf_caller").toString(), store.get("cf_name").toString(), store
					.get("cf_remark").toString(), "是","否", -1, "workflow");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("caller", store.get("cf_caller").toString());
			result.put("id", Integer.parseInt(store.get("cf_sourceId").toString()));
			result.put("jpName", store.get("cf_name").toString());
			result.put("code", SystemSession.getUser().getEm_code());
			result.put("name", SystemSession.getUser().getEm_name());
			processService.startProcess(result, SystemSession.getUser());

		} catch (Exception e) {

			e.printStackTrace();
			throw new JbpmException("部署  错误了哦 ！ ");
		}
		/*
		 * try{ //记录操作 baseDao.logMessage(new MessageLog(employee.getEm_name(),
		 * BaseUtil.getLocalMessage("msg.save", language),
		 * BaseUtil.getLocalMessage("msg.saveSuccess", language),
		 * "CustomFlow|cf_id=" + store.get("cf_id"))); } catch (Exception e) {
		 * e.printStackTrace(); } //执行保存后的其它逻辑
		 * handlerService.handler("CustomFlow", "save", "after", new
		 * Object[]{formStore, language});
		 */

	}

	@Override
	public void updateCustomFlowById(String formStore, String gridStore, String  caller) {

	}

	@Override
	public void deleteCustomFlow(int cf_id, String  caller) {

	}

}
