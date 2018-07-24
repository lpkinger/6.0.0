package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormPanel;
import com.uas.erp.service.common.RecyclesService;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.common.SingleGridPanelService;

@Service
public class RecyclesServiceImpl implements RecyclesService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	
	@Override
	public Map<String, Object> getRecycles(int id, String language, Employee employee) {
		SqlRowList list = baseDao.queryForRowSet("SELECT * FROM Recycles WHERE re_id=" + id);
		if(list.next()) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			String caller = list.getString("re_caller");
			Object re_keyvalue = list.getObject("re_keyvalue");
			List<String> gridstore = new ArrayList<String>();
			if(re_keyvalue != null) {
				Object re_detailvalue;
				list = baseDao.queryForRowSet("SELECT * FROM Recycles WHERE re_caller='" + caller + 
						"' AND re_keyvalue=" + re_keyvalue);
				boolean bool = false;
				FormPanel panel = singleFormItemsService.getFormItemsByCaller(caller, "", language, employee,false);
				modelMap.put("formset", panel);
				while(list.next()) {
					re_detailvalue = list.getObject("re_detailvalue");
					if(re_detailvalue == null) {
						//form
						bool = true;
						modelMap.put("formdata", list.getObject("re_data"));
					} else {
						//grid
						gridstore.add(list.getString("re_data"));
					}
				}
				if(!bool) {//这种是只删除了明细某几行，主表未删除
					modelMap.put("formdata", singleFormItemsService.getFormDataByCaller(caller, panel.getFo_keyField() + "=" + re_keyvalue));
				}
			} else {
				gridstore.add(list.getString("re_data"));
			}
			if(gridstore.size() > 0) {
				modelMap.put("gridset", singleGridPanelService.getGridPanelByCaller(caller, "", null, null, 1,false,""));
				modelMap.put("griddata", gridstore);
			}
			return modelMap;
		}
		return null;
	}

}
