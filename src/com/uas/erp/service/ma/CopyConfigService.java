package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import com.uas.erp.model.Configs;

public interface CopyConfigService {
	void updateCopyConfigByCaller(String caller,String formCaller, String gridStore);
	void deleteCopyConfigByCondition(String condition);
}
