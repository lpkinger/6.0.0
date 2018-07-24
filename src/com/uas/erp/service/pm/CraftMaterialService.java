package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface CraftMaterialService {

	Map<String, Object>  checkCraftMaterialQuery(String mccode,String sccode,
			String stepcode, String mcprodcode);

	String backCraftMaterial(String mscode,String mccode,String sccode, String barcode);

	String getCraftMaterial(String mscode,String mccode, String licode, String sccode,
			String stepcode, String barcode, int sp_id);

	List<Map<String,Object>> checkCraftMaterialGet(String mscode, String mccode, String licode,
			String sccode, String stepcode, boolean ifGet);

	Map<String ,Object> getBarDescription(String condition);

}
