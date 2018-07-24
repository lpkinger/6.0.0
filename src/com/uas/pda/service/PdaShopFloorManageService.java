package com.uas.pda.service;

import java.util.List;
import java.util.Map;


public interface PdaShopFloorManageService {

	Map<String ,Object> getMakeData(String code);

	Map<String ,Object> checkCode(String devCode,String code);

	List<Map<String ,Object>> getCollectDetailData(String data);

	Map<String ,Object> getBarRemain(String data);

	Map<String ,Object> loading(String msl, String makeCraft);

	Map<String ,Object> cuttingStock(String data);

	void cuttingAllStock(int id, String mc_sourcecode);

	Map<String ,Object> joinMaterial(String data);

	Map<String ,Object> changeMaterial(String data);

	List<Map<String ,Object>>  queryData(int id);

	List<Map<String ,Object>> checkMakeSMTLocation( int id);

	void importMPData(String data);
 
}
