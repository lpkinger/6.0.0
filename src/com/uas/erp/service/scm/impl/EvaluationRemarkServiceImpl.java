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
import com.uas.erp.service.scm.EvaluationRemarkService;

@Service("evaluationRemarkService")
public class EvaluationRemarkServiceImpl implements EvaluationRemarkService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateEvaluationById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + store.get("ev_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("Evaluation!Remark", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Evaluation", "ev_id");
		baseDao.execute(formSql);
		//修改EvaluationRemark
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "EvaluationRemark", "er_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("er_id") == null || s.get("er_id").equals("") || s.get("er_id").equals("0") ||
					Integer.parseInt(s.get("er_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("EVALUATIONREMARK_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "EvaluationRemark", new String[]{"er_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("Evaluation!Remark", "ev_id", store.get("ev_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("Evaluation!Remark", new Object[]{store, gstore});
	}
}
