package com.uas.erp.service.wisdomPark;


public interface NewsCenterService {
	
	void deleteNewsType(String caller, int id);
	
	void saveNews(String caller, String formStore);
	
	void updateNews(String caller, String formStore);
	
	void deleteNews(String caller, int id);
	
	void publishNews(String caller, int id);
	
	void cancelNews(String caller, int id);
	
	String getNewsHtml(int id);

}