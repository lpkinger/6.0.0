package com.uas.erp.service.scm;

public interface PlmpreproductService {
	void savePlmpreproduct(String formStore, String gridStore, String caller);

	void updatePlmpreproductById(String formStore, String gridStore, String caller);

	void deletePlmpreproduct(int pp_id, String caller);

	void auditPlmpreproduct(int pp_id, String caller);

	void resAuditPlmpreproduct(int pp_id, String caller);

	void submitPlmpreproduct(int pp_id, String caller);

	void resSubmitPlmpreproduct(int pp_id, String caller);

}
