package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.JSONTree;

public interface KnowledgeService {
	void saveKnowledge(String formStore,String   caller);
	void updateKnowledge(String formStore,String  caller);
	void deleteKnowledge(int id,String  caller);
	void submitKnowledge(int id, String  caller);
	void resSubmitKnowledge(int id,String  caller);
	void auditKnowledge(int id,String  caller);
	void resAuditKnowledge(int id,String  caller);
	void recommendKnowledge(String data, String  caller);
	void VastDeleteKnowledgeModule(String data, String  caller);
	List<JSONTree> getJSONModule(String caller);
	void saveKnowledgeComment(String formStore, String caller);
	void saveKnowledgeRecommend(String formStore, String caller);
	
}
