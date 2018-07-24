package com.uas.erp.core;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;
import com.uas.erp.model.Employee;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.FlexJsonUtil;
/**
 * 信鸽推送
 * @author suntg
 * @date 2015年3月7日10:01:04
 *
 */
public class XingePusher {
	
	/**
	 * 向单个账套的单个账号Android设备推送一条信息<p>在客户端使用用户手机号向信鸽注册</p>
	 * @param master 账套名
	 * @param account 账号
	 * @param enUU 所属企业UU号
	 * @param tittle 推送信息标题
	 * @param content 推送信息内容
	 * @param url 信息对应网页链接
	 * @param pageTitle 网页webView标题
	 * @return
	 */
	public static String tigase_url="http://113.105.74.140:8092/";//外网接口推送地址,已经上传过服务端地址，测试用本地的
	//public static String tigase_url="http://192.168.253.244:8092/";//本地接口推送地址
	public static JSONObject pushSingleAccountAndroid(String master, String account, String tittle, String content, String enUU, String masterId, String url, String pageTitle) {
		XingeApp xinge = new XingeApp(2100046094, "5805cd8bf93ea5405c98b3a6e81e29b3");
		Message message = new Message();
		message.setTitle(tittle);//推送信息标题
		message.setContent(content);//推送信息内容（第二行）
		message.setType(Message.TYPE_NOTIFICATION);
		//依次为 本地通知样式(就用0)[,是否响铃][,是否震动][,是否可清除][,覆盖模式][,是否呼吸灯][,图标类型][,$styleId]
		Style style = new Style(0, 1, 1, 1, 0, 1, 0, 0);
		message.setStyle(style);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uu", enUU);
		map.put("masterId", masterId);
		map.put("master", master);
		if(StringUtils.hasText(url)) {
			map.put("url", url);
			map.put("title", pageTitle);
			ClickAction clickAction = new ClickAction();
			clickAction.setActionType(ClickAction.TYPE_ACTIVITY);
			clickAction.setActivity("com.xzjmyk.pm.activity.WebViewLoadActivity");
			message.setAction(clickAction);
		}
		message.setCustom(map);
		JSONObject obj = xinge.pushSingleAccount(0, account, message);//发送给当前的账套
		return obj;
	}
	
	/**
	 * 向单个账套的单个账号IOS设备推送一条信息<p>在客户端使用用户手机号向信鸽注册</p>
	 * @param master 账套名
	 * @param account 用户名（手机号）
	 * @param title 消息标题
	 * @param content 消息内容（不显示）
	 * @param enUU 企业UU号
	 * @param url 消息对应链接
	 * @param pageTitle 网页webView
	 * @return
	 */
	public static JSONObject pushSingleAccountIOS(String master, String account, String title, String content, String enUU, String masterId, String url, String pageTitle) {
		//XingeApp xinge = new XingeApp(2200121309L, "fd52d406369688c2619b794862ea3f12");
		XingeApp xinge = new XingeApp(2200189555L, "34c14f881a4e72ded655c2f3870e4b31");
		//XingeApp xinge = new XingeApp(2200189555L, "30c672a3008e7177fa6c5ff7c4413ef0");
		MessageIOS message = new MessageIOS();
		message.setAlert(title);
//		System.out.println(title + " " + content + "${\"platform\":\"ERP\",\"url\":\"" + url + "\",\"enuu\":\"" + enUU + "\",\"master\":\"" + master + "\"}");
		message.setBadge(1);
		message.setSound("beep.wav");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enuu", enUU);
		map.put("masterId", masterId);
		map.put("master", master);
		map.put("platform", "ERP");
		if(StringUtils.hasText(url)) {
			map.put("url", url);
		}
		message.setCustom(map);
		TimeInterval acceptTime1 = new TimeInterval(0,0,23,59);
		message.addAcceptTime(acceptTime1);
		JSONObject obj = xinge.pushSingleAccount(0, account, message, XingeApp.IOSENV_PROD);
		return obj;
	}
	
	/**
	 * 推送单个账号（手机号）
	 * @param master
	 * @param account
	 * @param title
	 * @param content
	 * @param enUU
	 * @param url
	 * @param pageTitle
	 * @return
	 */
	public static JSONObject[] pushByAccount(String master, String account, String title, String content, String enUU, String masterId, String url, String pageTitle) {
		if(StringUtils.hasText(account)) {
			JSONObject[] json = new JSONObject[2];
			json[0] = pushSingleAccountAndroid(master, account, title, content, enUU, masterId, url, pageTitle);
			json[1] = pushSingleAccountIOS(master, account, title, content, enUU, masterId, url, pageTitle);
			return json;
		} else {
			return new JSONObject[2];
		}
	}
	
	/**
	 * 推送单个用户
	 * @param employee
	 * @param enUU
	 * @param title
	 * @param content
	 * @param url
	 * @param pageTitle
	 * @return
	 */
	public static JSONObject[] pushByEmployee(String master, Employee employee, String title, String content, String enUU, String masterId, String url, String pageTitle) {
		if(employee != null) {
			return pushByAccount(master, employee.getEm_tel(), title, content, enUU, masterId, url, pageTitle);
		} else {
			return new JSONObject[2];
		}
	}
	//================================百度推送
	/**
	 * 推送单个用户
	 * @param employee
	 * @param enUU
	 * @param title
	 * @param content
	 * @param url
	 * @param pageTitle
	 * @return
	 * @throws Exception 
	 */
	public static JSONObject[] pushByEmployeeBaidu(String master, Employee employee, String title, String content, String enUU, String masterId, String url, String pageTitle) throws Exception {
		if(employee != null) {
			String imid="0";
			if(employee.getEm_imid()==null){
				imid="0";
			}else{
				imid=employee.getEm_imid().toString();
			}
			return pushByAccountBaidu(master, imid, title, content, enUU, masterId, url, pageTitle);
		} else {
			return new JSONObject[2];
		}
	}
	public static JSONObject[] pushByAccountBaidu(String master, String account, String title, String content, String enUU, String masterId, String url, String pageTitle) throws Exception {
		if(StringUtils.hasText(account.toString())) {
			JSONObject[] json = new JSONObject[2];
			//=======百度推送，android，ios公用一个方法
			json[0] = pushSingleAccountAndroidBaidu(master, account, title, content, enUU, masterId, url, pageTitle);
			//json[1] = pushSingleAccountIOSBaidu(master, account, title, content, enUU, masterId, url, pageTitle);
			return json;
		} else {
			return new JSONObject[2];
		}
	}
	public static JSONObject pushSingleAccountAndroidBaidu(String master, String account, String title, String content, String enUU, String masterId, String url, String pageTitle) throws Exception {
		//调用tigase服务端的推送方法
		Map<String, String> params = new HashMap<String, String>();	
		params.put("master", master);//账套
		params.put("userid", String.valueOf(account));//推送目标用户
		params.put("title", title);//推送标题
		params.put("content", content);//正文
		params.put("enUU", enUU);//UU号
		params.put("masterId", masterId);//账套ID
		params.put("url", url);//跳转链接地址
		params.put("pageTitle", pageTitle);//页面标题
		params.put("platform", "ERP");//系统名称，ERP或者B2B
		
		Response response = HttpUtil.sendPostRequest(tigase_url
				+ "/tigase/baiduPush", params, false);
		JSONObject obj=new JSONObject();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			obj.put("result", "success");
			return obj;
		}else{
			obj.put("result", "fail");
			return obj;
		}
	}
	public static JSONObject pushSingleAccountIOSBaidu(String master, String account, String title, String content, String enUU, String masterId, String url, String pageTitle) throws Exception {
		//调用tigase服务端的推送方法
		Map<String, String> params = new HashMap<String, String>();	
		params.put("master", master);
		params.put("userid", String.valueOf(account));//推送目标用户
		params.put("title", title);
		params.put("content", content);
		params.put("enUU", enUU);
		params.put("masterId", masterId);
		params.put("url", url);
		params.put("pageTitle", pageTitle);
		
		Response response = HttpUtil.sendPostRequest(tigase_url
				+ "/tigase/baiduPush", params, false);
		JSONObject obj=new JSONObject();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			obj.put("result", "success");
			return obj;
		}else{
			obj.put("result", "fail");
			return obj;
		}
		
	}
}
