package com.uas.erp.service.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.uas.erp.model.InitData;
import com.uas.erp.service.common.AbstractInit;

public class CustomerInit extends  AbstractInit{
	private Map<String, JSONObject> stackDatas;
	private List<String> sqls;
	public CustomerInit(List<InitData> datas) {
		super(datas);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public synchronized void toFormal() {
		// TODO Auto-generated method stub
		
	}

}
