package com.uas.erp.service.sys;

public interface AlertItemService {

	public void save(String caller, String formStore, String params1, String params2);

	public void update(String caller,String formStore,String params1,String params2);

	public void submit(int id, String caller);
	
	public void resSubmit(int id, String caller);
	
	public void audit(int id, String caller);
	
	public void resAudit(int id, String caller);
	
	public void delete(int id, String caller);
	
	public void banned(int id, String caller);
	
	public void resBanned(int id, String caller);
}
