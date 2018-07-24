package com.uas.pda.service;

import java.util.Map;

public interface PdaTailingBackService {

	Map<String,Object> getForcastRemain(String code);

	void tailingBack(String data);

}
