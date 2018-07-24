package com.uas.erp.service.scm;

import java.util.List;

public interface MachineNoService {
	List<?> getProdioMachine(int piid, boolean iswcj);

	void insertProdioMac(int piid, String inoutno, String machineno, String prcode, int qty);

	void deleteProdioMac(int piid, String inoutno, String machineno, String prcode);

	void clearProdioMac(int piid, String prcode);
}
