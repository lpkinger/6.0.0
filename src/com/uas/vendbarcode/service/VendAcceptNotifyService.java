package com.uas.vendbarcode.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface VendAcceptNotifyService {
	
	public Map<String, Object> getAcceptNotifyList(Integer page,String condition,Integer start,Integer pageSize,String vendcode);
	
	public List<Map<String, Object>> getPurchaseData(String caller,String condition,String vendcode);
	
    public Map<String, Object> getAcceptNotifyForm(String caller,Integer id);
	
	public List<Map<String, Object>> getAcceptNotifyGrid(String caller,Integer id);
	
	void update(String caller, String formStore, String gridStore);
	
	void submit(String caller, int id);

	void resSubmit(String caller, int id);
	
	void delete(String caller, int id);
	
	void confirmDelivery(String caller, int id);
	
	void cancelDelivery(String caller, int id);
	
    public void batchGenBarcode(String caller,int id,String data,HttpSession session);
	
	public void deleteAllBarDetails(String caller, Integer pi_id,String biids);
	
	String vastTurnAccptNotify(String caller, String data,HttpSession session);
	
	public Map<String, Object> getAcceptNotifyListDetail(Integer page,String condition,Integer start,Integer pageSize,String vendcode);
}
