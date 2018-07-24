package com.uas.erp.service.fa;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Workbook;

import com.uas.erp.model.AccountRegisterDetailAss;
import com.uas.erp.model.JSONTree;

public interface AccountRegisterBankService {
	void saveAccountRegister(String formStore, String[] gridStore, String caller);

	void updateAccountRegisterById(String formStore, String[] gridStore, String caller);

	void deleteAccountRegister(int ar_id, String caller);

	void accountedAccountRegister(int ar_id, String caller);

	void resAccountedAccountRegister(int ar_id, String caller);

	void submitAccountRegister(int ar_id, String caller);

	void resSubmitAccountRegister(int ar_id, String caller);

	String validAccountRegister(int id);

	List<JSONTree> getJsonTrees(int parentid, String masterName);

	int turnPayBalance(int ar_id, String caller);

	int turnRecBalance(int ar_id, String caller);

	int turnRecBalanceIMRE(int ar_id, String custcode, String thisamount);

	void updateRemark(int id, String remark, String caller);

	JSONObject copyAccountRegister(int id, String caller);

	boolean ImportExcel(int id, Workbook wbs, String substring);

	void updateType(String custcode, String custname, String sellercode, String sellername, String arapcurrency, String araprate,
			String aramount, String vendcode, String vendname, String category, String description, String precurrency, String prerate,
			String preamount, String payment, String deposit, String id, String type, String caller);

	void refreshQuery(String condition);

	String[] printAccountRegister(int ar_id, String caller, String reportName, String condition);

	void endRecAmount(int ar_id, String caller);

	void updateErrorString(int ar_id);

	List<AccountRegisterDetailAss> findAss(int ar_id);
	
	boolean ImportExcel(int id, Workbook wbs, String substring, String caller);
}
