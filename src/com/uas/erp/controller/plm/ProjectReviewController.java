package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.ProjectReviewService;

@Controller
public class ProjectReviewController {
	@Autowired
	private ProjectReviewService projectReviewService;

	@RequestMapping("plm/review/loadKeyDevice.action")
	@ResponseBody
	public Map<String, Object> loadKeyDevice(HttpSession session, String producttype, int prid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.loadKeyDevice(producttype, prid);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/updateProjectReview.action")
	@ResponseBody
	public Map<String, Object> updateProjectReview(HttpSession session, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.updateProjectReview(formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/submitProjectReview.action")
	@ResponseBody
	public Map<String, Object> submitProjectReview(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.submitProjectReview(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/resSubmitProjectReview.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectReview(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.resSubmitProjectReview(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/auditProjectReview.action")
	@ResponseBody
	public Map<String, Object> auditProjectReview(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.auditProjectReview(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/resAuditProjectReview.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectReview(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.resAuditProjectReview(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/review/planMainTask.action")
	@ResponseBody
	public Map<String, Object> planMainTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.planMainTask(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/projectreview/reviewupdate.action")
	@ResponseBody
	public Map<String, Object> reviewupdate(HttpSession session, String reviewitem, String reviewresult, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.reviewupdate(reviewitem, reviewresult, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/plm/review/deleteProjectReview.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectReviewService.deleteProjectReview(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
