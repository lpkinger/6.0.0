package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.model.FileUpload;

public interface AddrBookService {

	public List<Map<String, Object>> getRootHrorg();
	public List<Map<String, Object>> getLeafHrorg(int or_id);
	public List<Map<String, Object>> getEmployeesByOrId(int or_id, int start, int end);
	public String getSobName(String master);
	public List<Object> getJobs(int em_id);
	public List<Map<String, Object>> queryEmployeeByName(String em_name);
	void updateEmployeePic(int em_id, int fp_id, String fp_path);
	public Object[] getEmployeePic(String em_code);
	public List<Map<String, Object>> getAllEmps(String lastdate);
	public List<Map<String, Object>> getOuterEmps(String lastdate,String departcode);
	public List<Map<String, Object>> getAllHrorg(String lastdate);
	public Map<String, Object> getAllNoteCount(String master,String emid);
	public String update_hrorgmobile(String orid,int kind);
	List<Map<String,Object>> addWorkReport(String  caller,String formStore);
	public List<Map<String, Object>> formConfig(String caller);
	public List<Map<String, Object>> gridConfig(String caller);
	void addMobileMac(String emcode,String macAddress);
	public List<Map<String, Object>> queryMobileMac(String emcode,String macaddress);
	public List<Map<String, Object>> getWorkDaily(String emcode,int pageIndex);
	public List<Map<String, Object>> getWorkReports(String emcode,int pageIndex,String caller);
	void addSignCard(String  caller,String formStore);
	void configUpdate(String  caller,String formStore,String gridStore);
	void commonUpdate(String  caller,String formStore,String gridStore,int id);
	void commondelete(String  caller,int id);
	List<Map<String, Object>> mobileoutplan(String emcode);
	void addAutoSign(String  caller,String formStore,int mpd_id);
	public String yesornoplan(String emcode);
	 public Map<String,Object> getFormAndGridDetail(String caller,String condition,int id);
	 void addOutSet(String  caller,String formStore);
	 List<Map<String, Object>> getOutSet();
	 void commonres(String  caller,int id);
	 List<Map<String, Object>> getsingledata(int id);
	 List<Map<String, Object>> getsingleWorkReports(String caller ,int id);
	 void mobileplanUpdate(int id);
	Map<String, Object> Commentsback_mobile(String formStore, String caller,
			MultipartFile img1, MultipartFile img2, MultipartFile img3);
	public List<Map<String, Object>> getOuterHrorg(String lastdate,
			Integer orid);
	Map<String, Object> getWorkReportInit(String emcode, String caller);
	List<Map<String, Object>> getTodayData(String emcode, String caller);
	List<Map<String, Object>> getYesterdayData(String emcode, String caller);

}
