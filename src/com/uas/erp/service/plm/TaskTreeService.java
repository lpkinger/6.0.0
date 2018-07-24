package com.uas.erp.service.plm;

import java.util.List;

import com.uas.erp.model.TaskJSONTree;

public interface TaskTreeService {
	List<TaskJSONTree> getJSONTreeByParentId(int parentId, String condition);
}
