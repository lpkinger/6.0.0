package com.uas.erp.service.b2c;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.uas.api.b2c_erp.baisc.model.BrandInfoUas;
import com.uas.api.domain.IPage;

public interface BrandInApplyService {
	void saveBrandInApply(String formStore, String caller);

	void updateBrandInApplyById(String formStore, String caller);

	void deleteBrandInApply(int br_id, String caller);

	void submitBrandInApply(int br_id, String caller);

	void resSubmitBrandInApply(int br_id, String caller);

	void auditBrandInApply(int br_id, String caller);

	void resAuditBrandInApply(int br_id, String caller);

	String saveBrandLogo(CommonsMultipartFile file);

	Map<String,Object> getBrandData(int id, String caller);
	
	Map<String,Object> getBrandDataByUUID(String  UUID, String caller);

	IPage<BrandInfoUas> findBrandByPage(String caller, int page, int pageSize);

	List<BrandInfoUas> findBrandAll(String caller);
	
	Map<String,Object> getUpdateBrand(String nameCn,String nameEn, String caller);
	
	Map<String,Object> CheckBrandName(String nameCn,String nameEn, String caller);
}
