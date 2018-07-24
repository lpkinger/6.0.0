package com.uas.erp.service.b2c;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface DeviceInApplyService {
	void saveDeviceInApply(String formStore, String param, String caller);

	void updateDeviceInApplyById(String formStore, String param, String caller);

	void deleteDeviceInApply(int de_id, String caller);

	void submitDeviceInApply(int de_id, String caller);

	void resSubmitDeviceInApply(int de_id, String caller);

	void auditDeviceInApply(int de_id, String caller);

	void resAuditDeviceInApply(int de_id, String caller);
	
	String saveFile(CommonsMultipartFile file);

	Map<String,Object> getDeviceData(int id, String caller);

	Map<String,Object> getAllKind(String caller);
	
	Map<String,Object> getAllBrand(String caller);

	Map<String,Object> getSearchData(String searchWord,String caller);
	
	Map<String,Object> getOldSpecData(String searchWord,String caller);
	
	Map<String,Object> getKindData(String searchWord,String caller);
	
	Map<String,Object> getPropertiesById(Long id);
	
	Map<String,Object> findDeviceByUUID(String UUID, String caller);
	
	Map<String,Object> getATypeAssociation(Long kindid,Long id,String searchword,Long shownum ,String caller);
	
	Map<String,Object> checkBrandAndCode(String nameCn,String code);
	
	Map<String,Object> getPackaging(Long kindid);
}
