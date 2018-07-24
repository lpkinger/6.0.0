package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

public interface DocDistributionService {

	public abstract List<Map<String, Object>> getProjectFileTree(
			String condition, int id, String checked);

	public abstract List<Map<String, Object>> getFileInfo(int[] ids);
	
	public void saveDocDistribution(String formStore, String gridStore, String caller);
	
	public void deleteDocDistribution(int seId, String caller);
	
	public void updateDocDistribution(String formStore, String gridStore, String caller);
	
	public void submitDocDistribution(int seId, String caller);
	
	public void resSubmitDocDistribution(int seId, String caller);
	
	public void auditDocDistribution(int seId, String caller);
	
	public void resAuditDocDistribution(int seId, String caller);

}