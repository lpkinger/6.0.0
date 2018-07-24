package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaOutMaterialService {

	List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode);

	List<Map<String, Object>> getProdOut(String inoutNo, String whcode);

	List<Map<String, Object>> getNeedGetList(Integer id, String whcode, String type);

	Map<String, Object> outByProdcode(String barcode, int id, String whcode, String type,boolean msdcheck);

	Map<String, Object> outByBatch(String barcode, int id, String whcode, String type,boolean msdcheck,String old_barcode);

	Object freeOut(String barcode, String type);
	
	Map<String,Object> getNextByProdcode(int pi_id ,String pd_whcode);
	
	Map<String,Object> getNextByBatch(int pi_id ,String pd_whcode);
	
	Map<String,Object> deleteDetail(Integer bi_piid, String barcode, String outboxcode, String whcode);
	
	Map<String, Object> getBarcodeData(String barcode);
	
	Map<String,Object> updateBarCodeQty( String barcode,double nowqty);
	
	Map<String,Object> specialOut(String barcode,String reason ,Integer  id ,String whcode);

	List<Map<String, Object>> getNeedGetListDeal(String ids, String type, Integer page, Integer pagesize);

	Map<String, Object> outByBatchDeal(String barcode, String ids, String type, boolean msdcheck);

	Map<String,Object> getNextByBatchDeal(String ids);

	List<Map<String, Object>> deleteDetailDeal(String ids, String barcode, String outboxcode);

	Map<String,Object> specialOutDeal(String barcode, String reason, String ids);

    List<Map<String, Object>> getHaveSubmitListDeal(String ids,Integer page, Integer pagesize);

	Map<String, Object> outByBatchBreaking(int id, String whcode, String barcode, Double or_remain, Double bar_remain);
	
	Map<String, Object> outByBatchBreakingDeal(String ids,String barcode, Double or_remain, Double bar_remain,String type);

	Map<String, Object> specialOutBreaking(String ids,String barcode,Double or_remain,Double bar_remain,String reason);
	
	List<Map<String, Object>> getProdOutStatus(String ids);
}
