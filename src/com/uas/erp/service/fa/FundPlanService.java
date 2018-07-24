package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface FundPlanService {
	
	void save(String formStore, String gridStore, String caller);

	void update(String formStore, String gridStore, String caller);

	void delete(int fp_id, String caller);

}
