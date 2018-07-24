package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.hr.StartExamService;

@Controller
public class StartExamController {
	@Autowired
	private StartExamService startExamService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/hr/emplmana/startExam.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String password, String name,String code) {
		return startExamService.start(password, name, session,code);
	}

	/**
	 * 返回试卷
	 */
	@RequestMapping("/hr/emplmana/getExam.action")
	@ResponseBody
	public Map<String, Object> getExam(HttpSession session, int id) {
		return startExamService.getExam(id);
	}

	/**
	 * 阅卷
	 */
	@RequestMapping("/hr/emplmana/checkExam.action")
	@ResponseBody
	public Map<String, Object> checkExam(HttpSession session, int id) {
		return startExamService.checkExam(id);
	}
	/**
	 * 退出
	 */
	@RequestMapping("/hr/emplmana/logoutExam.action")
	@ResponseBody
	public void logoutExam(HttpSession session) {
		session.setAttribute("examId", null);
	}

	/**
	 * 提交试卷
	 */
	@RequestMapping("/hr/emplmana/submitExam.action")
	@ResponseBody
	public Map<String, Object> submitExam(HttpSession session, String values) {
		session.setAttribute("examId", null);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<Object, Object>> data = BaseUtil.parseGridStoreToMaps(values);
		startExamService.submitExam(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量选择试卷生成方案
	 */
	@RequestMapping("/hr/selScheme.action")
	@ResponseBody
	public Map<String, Object> selScheme(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		System.out.println("data: "+data);
		startExamService.selScheme(caller,data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 进入考试系统
	 */
	@RequestMapping("/exam/exam.action")
	public ModelAndView exam(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		Object examId = session.getAttribute("examId");
		if (examId == null) {
			return new ModelAndView("hr/emplmana/question/startExam");
		} else {
			ModelMap modelMap = new ModelMap();
			modelMap.put("id", examId);
			return new ModelAndView("hr/emplmana/question/exam", modelMap);
		}
	}

	/**
	 * 阅卷
	 */
	@RequestMapping("/hr/emplmana/judgeExam.action")
	@ResponseBody
	public Map<String, Object> judgeExam(HttpSession session, String values) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<Object, Object>> data = BaseUtil.parseGridStoreToMaps(values);
		startExamService.judgeExam(data);
		modelMap.put("success", true);
		return modelMap;
	}

}
