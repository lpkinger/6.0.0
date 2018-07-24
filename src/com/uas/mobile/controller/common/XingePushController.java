package com.uas.mobile.controller.common;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.XingePusher;

/**
 * 推送请求
 * @author suntg
 * @date 2015年11月2日下午3:59:46
 */
@Controller
public class XingePushController {
	
	/**
	 * 指定接收的手机号和推送标题、推送内容，发送消息
	 * @param tel 接收者的手机号
	 * @param title 消息标题
	 * @param content 消息内容
	 * @param enUU 指定的enUU
	 * @param url 消息对应的链接
	 * @return
	 */
	@RequestMapping("/mobile/push.action")
	@ResponseBody
	public ModelMap push(String master,String tel, String title, String content, String enUU, String masterId, String url, String page) {
		ModelMap map = new ModelMap();
		JSONObject[] result = XingePusher.pushByAccount(master, tel, title, content, enUU, masterId, url, page);
		if(result[0] != null) map.put("android", result[0].toString());
		if(result[1] != null) map.put("ios", result[1].toString());
		return map;
	}
}
