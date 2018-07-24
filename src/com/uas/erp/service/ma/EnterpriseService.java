package com.uas.erp.service.ma;

import java.sql.Blob;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.uas.b2b.model.Enterprise;

public interface EnterpriseService {
	void updateEnterpriseById(String formStore, String caller);

	void regB2BEnterprise(int enid);

	void saveLogo(MultipartFile file);

	Blob getLogo();

	public boolean hasLogo();

	void setMasterInfo(String param, String caller);

	Map<String, Object> regEnterprise(Enterprise enterprise);

	void saasupdateEnterpriseById(String formStore, String caller);
}
