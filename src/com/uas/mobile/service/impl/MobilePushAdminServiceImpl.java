package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.docProps.variantTypes.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.mobile.service.MobilePushAdminService;

@Service
public class MobilePushAdminServiceImpl implements MobilePushAdminService {

	@Autowired
	BaseDao baseDao;
	@Override
	public List<Map<String, Object>> getAdminUser() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Object[]> listEmployee = baseDao.getFieldsDatasByCondition("employee", new String[] {"EM_NAME","EM_POSITION","EM_MOBILE"}, "EM_TYPE='admin'");
		for (Object[] objects : listEmployee) {
			Map<String,Object> model = new HashMap<String, Object>();
			model.put("name", objects[0]);
			model.put("position", objects[1]);
			model.put("mobile", objects[2]);
			list.add(model);
		}
		return list;
	}

}
