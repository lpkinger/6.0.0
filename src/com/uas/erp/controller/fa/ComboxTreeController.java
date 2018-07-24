package com.uas.erp.controller.fa;


import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ComboxTreeController {
	@RequestMapping("/comboxTree.action")  
	@ResponseBody 
	public Map<String, Object>sendData() {
		System.out.println("请求数据");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String s ="[{text: \"detention\", leaf: true },{ text: \"homework\", expanded: true, children: [ { text: \"book report\", leaf: true }, { text: \"alegrbra\", leaf: true}] },{ text: \"buy lottery tickets\", leaf: true }]";
		modelMap.put("tree", s);
		return modelMap;
	}
	@RequestMapping("/testTimer.action")  
	@ResponseBody 
	public Map<String, Object>sendData2() {
		System.out.println("请求数据");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		throw new RuntimeException("渔歌");
	}
}
