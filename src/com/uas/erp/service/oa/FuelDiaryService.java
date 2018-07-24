package com.uas.erp.service.oa;


public interface FuelDiaryService {
	void saveFuelDiary(String formStore, String  caller);
	void updateFuelDiary(String formStore, String  caller);
	void deleteFuelDiary(int fd_id, String  caller);
}
