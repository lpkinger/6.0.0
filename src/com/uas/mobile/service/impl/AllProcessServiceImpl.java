package com.uas.mobile.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.support.SystemSession;
import com.uas.mobile.dao.AllProcessDao;
import com.uas.mobile.model.AllProcess;
import com.uas.mobile.service.AllProcessService;

/**
 * 待办事宜处理Service接口实现类
 * 
 * @author suntg
 * @date 2014年9月9日 15:03:45
 */
@Service("allProcessService")
public class AllProcessServiceImpl implements AllProcessService {
	@Autowired
	private AllProcessDao allProcessDao;

	@Override
	public List<AllProcess> getAllProcessByDealPersonCode(String employeeCode, String currentMaster) {
		List<AllProcess> allProcesses = allProcessDao.getAllProcessByDealPersonCode(employeeCode, currentMaster);
		setUrlWithList(allProcesses);
		return allProcesses;
	}

	@Override
	public List<AllProcess> getAllProcessSince(String employeeCode, long time, String currentMaster) {
		List<AllProcess> allProcesses = allProcessDao.getAllProcessSinceTime(employeeCode, time, currentMaster);
		setUrlWithList(allProcesses);
		return allProcesses;
	}

	@Override
	public long getLastTime(String employeeCode, String currentMaster) {
		return allProcessDao.getLastTime(employeeCode, currentMaster);
	}

	@Override
	public AllProcess getLastAllProcess(String employeeCode, String currentMaster) {
		AllProcess allProcess = allProcessDao.getLastAllProcess(employeeCode, employeeCode);
		setURL(allProcess);
		return allProcess;
	}

	@Override
	public void setURL(AllProcess allProcess) {
		String url = "";
		String typeCode = allProcess.getTypecode();
		if (typeCode.equals("worktask") || typeCode.equals("projecttask")) {
			url = "jsps/mobile/task.jsp?caller=WorkRecord&id=" + allProcess.getId();
		} else if (typeCode.equals("billtask") || typeCode.equals("mrptask") || typeCode.equals("kbitask")) {
			url = "jsps/mobile/task.jsp?caller=ResourceAssignment!Bill&id=" + allProcess.getId();
		} else if (typeCode.equals("communicatetask")) {
			url = "jsps/common/JprocessCommunicate.jsp?whoami=ResourceAssignment!Bill&formCondition=id=" +
					allProcess.getId() + "&gridCondition=null=" + allProcess.getId() + "&_noc=1";
		} else if (typeCode.equals("process") || typeCode.equals("unprocess") || typeCode.equals("transferprocess")) {			
			url = "jsps/mobile/process.jsp?nodeId=" + allProcess.getTaskid();		
		} else if (typeCode.equals("procand")){
			url = "jsps/mobile/jprocand.jsp?nodeId=" + allProcess.getTaskid();
		} else if ("pagingrelease".equals(typeCode)) {
			url = "jsps/mobile/commonForm/commonForm.jsp?caller=" + allProcess.getCaller() + "&" +
					allProcess.getLink() + "&_readOnly=true";
		} else {
			url = "jsps/common/jtaketask.jsp?whoami=JProCand&formCondition=jp_nodeId=" +
					allProcess.getTaskid() + "&gridCondition=1=" + allProcess.getTaskid() + "&_noc=1";
		}
		// 将url中所有的&替换为%26为防止url作为参数传递时&被识别
		url = url.replaceAll("&", "%26");
		allProcess.setLink(url);
	}

	public void setUrlWithList(List<AllProcess> allProcesses) {
		for (AllProcess allProcess : allProcesses) {
			setURL(allProcess);
		}
	}

	@Override
	public int getAllProcessCount(String employeeCode, String currentMaster) {
		return allProcessDao.getAllProcessCount(employeeCode, currentMaster);
	}

}
