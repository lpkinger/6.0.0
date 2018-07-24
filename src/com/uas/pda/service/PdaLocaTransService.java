package com.uas.pda.service;

import java.util.Map;

public interface PdaLocaTransService {

	Map<String ,Object> getCodeData(String whcode, String code, String type);

	void locaTransfer(String data, String location);

	Map<String, Object> getCodeWhcode(String code, String type);

	void whcodeTransfer(String data, String whcode);
}
