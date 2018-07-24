package com.uas.erp.service.sys;

public interface ScheduleConfigService {

	public abstract void save(String caller, String formStore);

	public abstract void update(String caller, String formStore);

	public abstract void deleteDocSetting(int id, String caller);

}