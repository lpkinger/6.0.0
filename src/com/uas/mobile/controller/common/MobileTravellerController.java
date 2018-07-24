package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.MobileTravellerService;

@Controller
public class MobileTravellerController {
	@Autowired
	private MobileTravellerService mobileTravellerService;
	@Autowired
	private BaseDao baseDao;
    
	@RequestMapping("/mobile/getBussinessTrip.action")
	@ResponseBody
	public Map<String, Object> BussinesStrip(HttpServletRequest request,HttpSession session,String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("sessionId", session.getId());
		map.put("success", true);
		//取身份证号和人员类型（领导型和非领导型）
		Object[] iccode = baseDao.getFieldsDataByCondition("EMPLOYEE", "EM_ICCODE,EM_CTTPTYPE,EM_COP", "em_code='"+emcode+"' and em_class<>'离职'");
		Object travelCard = baseDao.getFieldDataByCondition("EMPLOYEE LEFT JOIN COP_TRAVELCARD ON EM_COP = SHORTNAME", "TRAVELCARD", "em_code='"+emcode+"' and em_class<>'离职'");
		map.put("em_iccode", iccode[0]);
		map.put("listdata",mobileTravellerService.getBussinessTrip(emcode));
		map.put("isLead", iccode[1]);
		map.put("travelCard", travelCard);
		Object[] appkey = baseDao.getFieldsDataByCondition("tp_url left join COP_TRAVELCARD ON TU_NAME=TRAVELCARD", "TU_PARAMS1,TU_PARAMS2", "shortname='"+iccode[2]+"'");
		if(appkey!=null) {
			map.put("appKey", appkey[0]);
			map.put("appSceret", appkey[1]);
		}else {
			map.put("appKey", null);
			map.put("appSceret", null);
		}
		return map;
	}
}
