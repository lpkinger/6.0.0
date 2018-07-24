package com.uas.erp.model;

import java.util.Comparator;
import java.util.Map;

public class CustomFlowDetailComparator implements Comparator<Map<Object,Object>>{

	@Override
	public int compare(Map<Object, Object> customFlowDetail1 , Map<Object, Object> customFlowDetail2) {
		if(Integer.parseInt(customFlowDetail1.get("cfd_detno").toString())>Integer.parseInt(customFlowDetail2.get("cfd_detno").toString())){
			return 1;
			}
		
		return -1;
		}
	
	

 
	
}
