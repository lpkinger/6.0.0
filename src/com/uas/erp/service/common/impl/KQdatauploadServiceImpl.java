package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.service.common.KQdatauploadService;

@Service
public class KQdatauploadServiceImpl implements KQdatauploadService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public String upload(String data, String master) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		List<Map<Object, Object>> list = JSONUtil.toMapList(data);
		System.out.println(list);
		synchronized(this.getClass()){
			if (master != null && !"".equals(master)) {
				SpObserver.putSp(master);
				for (int i = 0; i < list.size(); i++) {
					sqls.add("insert into cardlog (CL_CARDCODE,CL_EMCODE,cl_time,cl_emname) values('"
							+ list.get(i).get("cl_cardcode") + "','" + list.get(i).get("cl_emcode") + "',to_date('"
							+ list.get(i).get("cl_time")+ "','yyyy-mm-ddhh24:mi:ss '),'" + list.get(i).get("cl_emname") + "')");
				}
			} else {
				SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
				for (int i = 0; i < list.size(); i++) {
					sqls.add("insert into cardlog (CL_CARDCODE,CL_EMCODE,cl_time,cl_emname) values('"
							+ list.get(i).get("cl_cardcode") + "','" + list.get(i).get("cl_emcode") + "',to_date('"+list.get(i).get("cl_time")+"','yyyy-mm-ddhh24:mi:ss'),'" + list.get(i).get("cl_emname") + "')");
				}
			}
			baseDao.execute(sqls);
			return "true";
		} 
	}

}
