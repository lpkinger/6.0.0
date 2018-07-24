package com.uas.erp.service.oa;

import java.util.List;
import com.uas.erp.model.JSONTree;

public interface AddressBookService {
	List<JSONTree> getJSONGroup(String caller,int emid);
	void saveAddressBookGroup(String formStore, String caller);
	void updateAddressBookGroup(String formStore, String caller);
	void deleteAddressBookGroup(int id, String caller);
	void removeToOtherGroup(int id, String data, String caller);
	void saveAddressPerson(String formStore, String caller);
	void updateAddressPerson(String formStore, String caller);
	void deleteAddressPerson(String data, String caller);
	void sharedToOther(String formStore, String type, String data,String caller);
	List<JSONTree> getEmployee(String caller);
}
