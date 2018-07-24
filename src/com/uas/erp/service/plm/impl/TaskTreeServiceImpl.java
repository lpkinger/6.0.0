package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.common.TaskDao;
import com.uas.erp.model.TaskJSONTree;
import com.uas.erp.model.TaskTemplate;
import com.uas.erp.service.plm.TaskTreeService;

@Service
public class TaskTreeServiceImpl implements TaskTreeService {
	@Autowired
	private TaskDao taskDao;

	@Override
	public List<TaskJSONTree> getJSONTreeByParentId(int parentId, String condition) {
		List<TaskJSONTree> tree = new ArrayList<TaskJSONTree>();
		List<TaskTemplate> list = taskDao.getTaskTemplateByParentId(parentId, condition);
		if (list.size() > 0) {
			for (TaskTemplate tasktemplate : list) {
				tree.add(new TaskJSONTree(tasktemplate));
			}
		}
		return tree;
	}
}
