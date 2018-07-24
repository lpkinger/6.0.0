package com.uas.erp.service.pm;

	public interface MakePlanService {
		
		int save(String caller, String formStore, String gridStore);

		void update(String caller, String formStore, String gridStore);

		void delete(String caller, int id);

		void audit(int id,String caller);

		void resAudit(String caller, int id);

		void submit(String caller, int id);

		void resSubmit(String caller, int id);

	}

