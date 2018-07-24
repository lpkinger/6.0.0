package com.uas.erp.service.oa;


public interface EmFoodCardService {

	void saveEmFoodCard(String formStore, String param, String caller);

	void deleteEmFoodCard(int id, String  caller);

	void updateEmFoodCard(String formStore, String param, String caller);

}
