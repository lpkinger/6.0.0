package com.uas.erp.service.pm;

public interface MakeCraftPieceWorkService {
	
	public void updateMakeCraftPieceWorkChange(String caller,String formStore,String gridStore);
	public void deleteDetail(String caller,int id);
	public void loadPeople(String makecode,String prodcode);
	
}
