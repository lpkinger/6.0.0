package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.Form;
import com.uas.erp.service.plm.ProjectTemplateService;

@Service
public class ProjectTemplateServiceImpl implements ProjectTemplateService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectTemplate(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectTemplate", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("ProjectTemplate", "pt_id", store.get("pt_id"));
	}

	@Override
	public void updateProjectTemplate(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectTemplate", "pt_id");
		baseDao.execute(formSql);
		baseDao.logger.update("ProjectTemplate", "pt_id", store.get("pt_id"));
	}

	@Override
	public void deleteProjectTemplate(int id) {
		handlerService.handler("ProjectTemplate", "delete", "before", new Object[] { id });
		// 删除CheckList
		baseDao.deleteById("ProjectTemplate", "pt_id", id);
		// 记录操作
		baseDao.logger.delete("ProjectTemplate", "pt_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("ProjectTemplate", "delete", "after", new Object[] { id });
	}

	@Override
	public String getProjectTemplateData(String caller, String condition) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		return baseDao.getDataStringByForm(form, condition);
	}

}
