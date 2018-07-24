package com.uas.erp.service.fa.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.TransferAccountService;
import com.uas.erp.service.oa.FeePleaseService;
import com.uas.erp.service.plm.TaskService;

@Service("transferAccountService")
public class TransferAccountServiceImpl implements TransferAccountService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FeePleaseService feePleaseService;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private AccountRegisterBankService accuntRegisterBankService;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeService employeeService;
	
	private final static String LOG="insert into bank$log(bl_id, bl_date, bl_billcode, bl_billclass, bl_request, bl_response, bl_trnid) ";
	
	//主处理方法
	@Override
	public Map<String,Object>postRequests(String ip, String data,String psw, String trnCode,
			String banktype) {
		Employee employee = SystemSession.getUser();
		Map<String,Object> info=new HashMap<String,Object>();
		String xml = null;//发送报文
		String res=null;//返回报文
		int maxSize = 2000;
		int i=0;
		LinkedList<String> stack = new LinkedList<String>();
		List<String> logs = new ArrayList<String>();
		List<Map<String, Object>> bodyMaps = null;
		Map<String, Object> headMap = getBankInfoByEmp(employee,psw,banktype,trnCode);
		//产生主体map
		if ("Xfer".equals(trnCode) || "TransferXfer".equals(trnCode)
				|| "8801".equals(trnCode)) {
			bodyMaps = createBodyMaps(data, banktype);
		} else if ("qryXfer".equals(trnCode) || "8804".equals(trnCode) || "EY03".equals(trnCode)) {
			bodyMaps = createQryMap(data, banktype);
		}
		//生成xml
		if("spdb".equals(banktype))
			//浦发银行签名处理
			 bodyMaps=toSign(bodyMaps,banktype,ip);
		for (Map<String, Object> body : bodyMaps){
			if((Boolean) body.get("verified")){
				body.remove("verified");
				xml = createXml(headMap, body, trnCode,banktype);	
				if (stack.size() <= maxSize){
					stack.addLast(xml);
				}
			}	
		}	
		
		while (!stack.isEmpty()){	
			Map<String, Object> map =null;
			String post=null;
			String type=null;
			StringBuffer log = new StringBuffer(LOG);
			String trnId = "";//报文唯一标志
			int logId = baseDao.getSeqId("BANK$LOG_SEQ");
			String request = "";//发送报文
			String kind ="";//单据类型
			String code = "";//单据编号
			if ("cmbc".equals(banktype)){
				post=stack.getFirst();
				request = post;
				type="cmbc.request";
			} else if ("spdb".equals(banktype)){
				post=stack.getFirst().split("\\|")[0];
				request = post.substring(0, post.indexOf("<signature>")) + stack.getFirst().split("\\|")[1];
				trnId = parseTag(stack.getFirst(), "packetID");
				kind = billChange(trnId.split(",")[0]);
				code = trnId.split(",")[1];
				type="spdb.request";
			}
			
			res = purePost(ip,post, type,trnCode);	
			//发送前后报文记录日志字数不超过4000
			if (res.length() >4000) {
				res.substring(0, 4000);
			}
			log.append("select " + logId + ", sysdate, '" + kind + "', '" + code +  "', '" + request + "', '" +  res + "', '" + trnId + "' from dual");
			logs.add(log.toString());
			map = parseResult(res, trnCode,type, ip);			
			if ("Xfer".equals(trnCode)||"TransferXfer".equals(trnCode)||"8801".equals(trnCode)){								
				if ( map.get("result")!=null) {
					if ((Boolean) map.get("result")){
						Object id = record(stack.getFirst(),map.get("acceptNo"),map.get("msg"),true,banktype);
						//浦发银行转账状态为4或者3时，已经交易成功，直接转银行登记
						if(map.get("realRes")!=null&&(Boolean)map.get("realRes")){
							Map<String,Object> m=new HashMap<String,Object>();
							m.put("result", true);
							m.put("msg", "该笔支付处理成功，客户记账成功");
							updateAndTurnBankRegister(m, null,id.toString(),banktype);
						}
						i++;
					} else {
						//交易结果为8，查询交易失败原因
						record(stack.getFirst(),map.get("acceptNo"),null,false,banktype);
					}
				} else {
					String str=stack.getFirst();					
					String[] bill=null;
					if ("cmbc".equals(banktype)) {
						bill=parseTag(str,"trnId").split(",");
						if (bill != null) {
							kind = billChange(bill[1]);
							code = bill[0];		
						}
					}
					else if ("spdb".equals(banktype)){
						bill = parseTag(str,"packetID").split(",");
						if (bill != null) {
							kind = billChange(bill[0]);
							code = bill[1];
						}
					}
					updateRemark(code, kind, map.get("msg"));
				}							
		} else if ("qryXfer".equals(trnCode) || "8804".equals(trnCode) || "EY03".equals(trnCode)) {
				updateAndTurnBankRegister(map, stack.getFirst(),null,banktype);
		}
		stack.removeFirst();
	}
		baseDao.execute(logs);
		info.put("success", i);
		return info;
}
	
	private Map<String,Object> getBankInfoByEmp(Employee employee,String psw,String banktype,String transCode) {
		Map<String,Object> map=new HashMap<String,Object>();
		Date time=new Date();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sf.format(time);		
		boolean f = baseDao.checkIf("bank$man", "erpcode='"+employee.getEm_code()+"' and bankkind='"+banktype+"'");
		if(f){
			Object[] data=baseDao.getFieldsDataByCondition("bank$man left join bank$ on id=bankid", "bankid,erpcode,name,userid,userpwd,enterprisecode", "erpcode='"+employee.getEm_code()+"' and bankkind='"+banktype+"'");
			if("cmbc".equals(banktype)){
				map.put("clientId",data[5]);
				map.put("userId",data[3]);
				map.put("userPswd",psw);
				map.put("dtClient", date);
				map.put("language", "utf_8");
				map.put("appId","nsbdes");
				map.put("appVer", "201");
			}else if("spdb".equals(banktype)){
				map.put("transCode", transCode);
				map.put("signFlag","1");
				map.put("masterID", data[5]);//企业客户号
				map.put("packetID", null);
				map.put("timeStamp", date);
			}
		}else
			BaseUtil.showError("你没有权限进行相关操作!");
		return map;
	}
	
	private List<Map<String, Object>> createBodyMaps(String data,
			String banktype) {
		List<Map<Object, Object>> codeMaps = BaseUtil
				.parseGridStoreToMaps(data);
		String code = null;
		String class_ = null;
		String amount = null;
		String purpose = null;
		List<Map<String, Object>> maps = new LinkedList<Map<String, Object>>();
		for (Map<Object, Object> codeMap : codeMaps) {
			Set<Object> keySet = codeMap.keySet();
			for (Object key : keySet) {
				if ("code".equals(key)) {
					code = codeMap.get(key).toString();
				} else if ("class_".equals(key)) {
					class_ = codeMap.get(key).toString();
				} else if ("amount".equals(key)) {
					amount = codeMap.get(key).toString();
				} else {
					purpose = codeMap.get(key) != null ? codeMap.get(key)
							.toString() : "";
				}
			}
			maps.add(createBodyMap(code, class_, amount, purpose, banktype));
		}
		return maps;
	}
	
	//生成数据部分xml
	private Map<String, Object> createBodyMap(String code, String class_,
			String amount, String purpose, String banktype) {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Map<String, Object> need = new HashMap<String, Object>();
		Employee emp = SystemSession.getUser();
		bodyMap.put("verified", true);
		Map<String, Object> checkAmount = checkAmount(code, class_, amount);
		if (!(Boolean) checkAmount.get("flag")) {
			bodyMap.put("verified", false);
			// 更新错误信息
			updateRemark(code, class_, checkAmount.get("msg"));
		} else {
			String insId = getInsId().toString();
			Date date = new Date();
			int localFlag = 5;
			Object payAccount = null;// 付款账号
			Object payMan = null;// 付款账号名称
			Object receiveAccount = null;// 收款账号
			Object receiveMan = null;// 收款账号名称
			Object receiveBank = null;// 收款账号银行名称
			boolean sameBank = false;// 是否是同行
			Object bankcode = null;// 收款银行行号
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String d = sf.format(date);
			String sql = "select ca_bankaccount,enterprise from bank$ left join category on ca_code=category_code  where bank_kind='"
					+ banktype + "'";
			SqlRowList set = baseDao.queryForRowSet(sql);
			if (set.next()) {
				payAccount = set.getString("CA_BANKACCOUNT");
				payMan = set.getString("ENTERPRISE");
			}
			if ("借款申请单".equals(class_) || "费用报销单".equals(class_)) {
				Object[] data = baseDao.getFieldsDataByCondition("feeplease",
						"fp_v10,fp_v9,fp_v8,fp_n2,fp_v11,fp_v6", "fp_code='"
								+ code + "'");
				receiveBank = data[5].toString();
				receiveAccount = data[2];
				receiveMan = data[1];
			} else if ("付款申请单".equals(class_) || "预付款申请单".equals(class_)) {
				Object[] out = baseDao.getFieldsDataByCondition("PayPlease",
						"pp_id", "pp_code='" + code + "'");
				Object[] in = baseDao.getFieldsDataByCondition(
						"PayPleaseDetail",
						"ppd_bankname,ppd_bankaccount,ppd_vendname",
						"ppd_ppid=" + out[0].toString());
				receiveBank = in[0].toString();
				receiveAccount = in[1];
				receiveMan = in[2];
			}
			bankcode = baseDao.getFieldDataByCondition("bank$code", "bankcode",
					"bankname='" + receiveBank + "'");
			Object authMasterID = baseDao.getFieldDataByCondition("bank$man", "userid", "bankkind='" + banktype + "' and erpcode='"+ emp.getEm_code() + "'");

			// 单据里面各银行共同字段
			need.put("payAccount", payAccount);
			need.put("payMan", payMan);
			need.put("receiveAccount", receiveAccount);
			need.put("receiveMan", receiveMan);
			need.put("receiveBank", receiveBank);
			need.put("purpose", purpose);
			need.put("bankCode", bankcode);
			need.put("amount", amount);
			need.put("authMasterID", authMasterID);

			List<Object[]> configs = baseDao.getFieldsDatasByCondition(
					"bank$detail", new String[] { "bd_field", "bd_need","BD_RELATIVEFIELD" }, "bd_bankcode='" + banktype
							+ "' and BD_TYPE='body' order by bd_no");
			if ("cmbc".equals(banktype)) {
				// 银行信息填充以及非必填项赋空值
				for (Object[] con : configs) {
					fill(bodyMap, need, con[2], con[0].toString());
				}
				// 民生银行字段逻辑填写
				if (String.valueOf(receiveBank).contains("民生")) {
					bodyMap.put("externBank", "0");
					bodyMap.put("bankCode", "");
					localFlag = 2;
				} else {
					localFlag = Double.parseDouble(amount) <= 50000 ? 25 : 3;
					Map<String, Object> checkBank = checkBank(localFlag,
							receiveBank, code);
					if (checkBank.get("bankCode") != null) {
						bodyMap.put("bankCode", checkBank.get("bankCode").toString());
						if (checkBank.get("localFlag") != null) {
							localFlag = (Integer) checkBank.get("localFlag");
						}
					} else {
						checkBank.put("msg", "开户行名称不正确");
						bodyMap.put("verified", false);
						updateRemark(code, class_, checkBank.get("msg"));
					}
					bodyMap.put("externBank", "1");
				}
				bodyMap.put("localFlag", localFlag);
				bodyMap.put("actDate", d);
				bodyMap.put("trnId", code + "," + billChange(class_) + ","
						+ insId);
				bodyMap.put("insId", insId);
			} else if ("spdb".equals(banktype)) {
				// 浦发银行字段要求
				if (String.valueOf(receiveBank).contains("浦发")
						|| String.valueOf(receiveBank).contains("浦东发展")) {
					sameBank = true;
				} else
					sameBank = false;
				// 银行信息填充以及非必填项赋空值
				for (Object[] con : configs) {
					fill(bodyMap, need, con[2], con[0].toString());
				}
				// 特殊项赋值
				//bodyMap.put("remitLocation",bankcode == null ? 0 : 1);
				//bodyMap.put("payeeBankSelectFlag",bankcode == null ? "" : 1);				
				bodyMap.put("payeeType", (sameBank && receiveAccount.toString().length() == 16) ? 1 : "");
				bodyMap.put("remitLocation", 1);
				bodyMap.put("payeeBankSelectFlag", 1);
				bodyMap.put("payeeBankNo", bankcode);
				bodyMap.put("elecChequeNo", billChange(class_) + "," + code);
				bodyMap.put("sysFlag", sameBank ? 0 : 1);
				bodyMap.put("payeeAddress", receiveBank);
			}
		}
		return bodyMap;
	}
	
	private Map<String,Object> checkAmount(String code,String class_,String amount){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("flag", true);
		Object ori = baseDao.getFieldDataByCondition("TRANSFERACCOUNT_VIEW", "tr_amount", "tr_code='"+code+"' and tr_kind='"+class_+"'");
		Float f=Float.parseFloat(ori.toString());
		Float amo=Float.parseFloat(amount);
		if(f<amo||amo<0||amo==0){
			map.put("msg", "不允许超额付款且付款数不能为0或负数!");
			map.put("flag",false);
		}
		if("费用报销单".equals(class_)||"借款申请单".equals(class_)){
			if(f > amo){
				map.put("msg", "费用报销单/借款申请单不能部分付款!");
				map.put("flag",false);
			}
		}
		return map;			
	}
	
	private void updateRemark(String code, String kind, Object err){
		StringBuffer sql=new StringBuffer("update ");
		if("付款申请单".equals(kind)||"预付款申请单".equals(kind)){
			sql.append("payplease set pp_bankremark='"+err+"',pp_bankstatus='转账失败' where pp_code='"+code+"'");
		}else{
			sql.append("feeplease set fp_bankremark='"+err+"',fp_bankstatus='转账失败'  where fp_code='"+code+"'");
		}		
		baseDao.execute(sql.toString());
	}
	
	private Map<String,Object> checkBank(int localFlag, Object bankName, String code){
		Map<String,Object> map=new HashMap<String,Object>();
		if(localFlag==25){
		SqlRowList set2 = baseDao.queryForRowSet("select bankcode from bank$code where bankname='"+bankName+"' and bankway=5");
			if(set2.next()){
				String bankcode=set2.getString("bankcode"); 
				map.put("bankCode",bankcode.toString());
				map.put("localFlag", 5);
			}else{
				SqlRowList sl = baseDao.queryForRowSet("select bankcode from bank$code where bankname='"+bankName+"' and bankway=3");
				if(sl.next()){
					String bankcode=sl.getString("bankcode"); 
					map.put("bankCode",bankcode.toString());
					map.put("localFlag", 2);
				}else{
					map.put("wrongBankName", code);
				}
			}
		}else{
			SqlRowList sl = baseDao.queryForRowSet("select bankcode from bank$code where bankname='"+bankName+"' and bankway=3");
			if(sl.next()){
				String bankcode=sl.getString("bankcode"); 
				map.put("bankCode",bankcode.toString());
				map.put("localFlag", 3);
			}else{
				map.put("wrongBankName", code);
			}
		}	
		return map;
	}
	
	//生成xml
	private String createXml(Map<String,Object> head,Map<String,Object> body,String trnCode,String banktype) {
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			if("cmbc".equals(banktype)){
				sb.append("<CMBC header=\"100\" version=\"100\" security=\"none\" lang=\"chs\" trnCode=\""+trnCode+"\"><requestHeader>");
				sb.append(map2Xml(head));
				sb.append("</requestHeader><xDataBody>");
				 sb.append(map2Xml(body));
				 sb.append("</xDataBody></CMBC>");	 
			}else if("spdb".equals(banktype)){
				//先加上原先内容的XML 发送时再去除,
				Integer random=(int)(Math.random()*10);
				SimpleDateFormat sf=new SimpleDateFormat("SSS");
				Date d=new Date();
				Object packetID=body.get("id")+","+random+sf.format(d);//防止同一单据第二次付款时packetid相同
				head.put("packetID",packetID);
				body.remove("id");
				String ori=body.get("original").toString();
				body.remove("original");
				sb.append("<packet><head>");
				sb.append(map2Xml(head));
				sb.append("</head><body>");
				sb.append(map2Xml(body));
				sb.append("</body></packet>");
				sb.append("|"+ori);
			}
		return sb.toString();
	}
	
	private void fill(Map<String, Object> body, Map<String, Object> data,
			Object relavetive, String tag) {
		if (relavetive != null) {
			String[] strs = new String[] { "payAccount", "payMan",
					"receiveAccount", "receiveMan", "receiveBank", "sameBank",
					"purpose", "amount", "authMasterID" };
			for (String str : strs) {
				if (str.equals(relavetive)) {
					body.put(tag, data.get(str));
				}
			}
		} else
			body.put(tag, null);
	}
	
	private String map2Xml(Map<String,Object> map){//map解析到XML字符串
		Set<String> keys = map.keySet();
		StringBuffer sb=new StringBuffer();
		for(String obj:keys){
			sb.append("<"+obj+">");
			if(map.get(obj)==null){
				sb.append("");
			}else{
				sb.append(map.get(obj).toString());
			}
			sb.append("</"+obj.toString()+">");
				}
		return sb.toString();
	}

	public Object getInsId() {	
		String insId=baseDao.sGetMaxNumber("bank$task", 2);
		return insId;
	}
	
	private String purePost(String ip, String content, String type,
			String trnCode) {
		String result = null;
		String s = null;
		String urlStr = null;
		URL url;
		if (content != null) {
			if ("cmbc.request".equals(type)) {
				urlStr = "http://" + ip + ":8088/eweb/b2e/connect.do";
			} else if ("spdb.request".equals(type)) {
				urlStr = "http://" + ip + ":5777";
			} else if ("spdb.sign".equals(type) || "spdb.verify".equals(type)) {
				urlStr = "http://" + ip + ":4437";
			}
			Integer i = content.length();
			try {
				url = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestMethod("POST"); // 设置请求方式
				// 请求头设置
				if ("spdb.sign".equals(type)) {
					connection.setRequestProperty("Content-Type",
							"INFOSEC_SIGN/1.0");
					connection.setRequestProperty("Content-Length", i + "");// String.valueOf((i+str).length())+1);
				} else if ("spdb.verify".equals(type)) {
					connection.setRequestProperty("Content-Type",
							"INFOSEC_VERIFY_SIGN/1.0");
					connection.setRequestProperty("Content-Length", i + "");
				} else if ("spdb.request".equals(type)) {
					i = i + 6;
					s = String.valueOf(i);
					for (int j = i.toString().length(); j < 6; j++) {
						s = s + " ";
					}
					connection.setRequestProperty("Content-Length", i + "");
					content = s + content;
				} else if ("cmbc.request".equals(type)) {
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded"); // 设置接收数据的格式
					connection.setRequestProperty("User-Agent", "My session"); // 设置发送数据的格式
					connection.setRequestProperty("Cache-Control", "no-cache");
				}
				System.setProperty("sun.net.client.defaultConnectTimeout",
						"30000");
				connection.connect();
				OutputStreamWriter out = new OutputStreamWriter(
						connection.getOutputStream(), "GBK"); // utf-8编码
				out.append(content);
				out.flush();
				out.close();

				int code = connection.getResponseCode();
				InputStream is = null;
				if (code == 200) {
					is = connection.getInputStream();
				} else {
					is = connection.getErrorStream();
				}
				// 读取响应
				int length = (int) connection.getContentLength();// 获取长度
				if (length != -1) {
					byte[] data = new byte[length];
					byte[] temp = new byte[512];
					int readLen = 0;
					int destPos = 0;
					while ((readLen = is.read(temp)) > 0) {
						System.arraycopy(temp, 0, data, destPos, readLen);
						destPos += readLen;
					}
					String tep = new String(data);
					if (tep.contains("utf-8") || tep.contains("UTF-8")) {
						result = new String(data, "utf-8");
					} else if (tep.contains("GB2312") || tep.contains("gb2312")) {
						result = new String(data, "GB2312");
					} else {
						result = new String(data, "GB2312");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//解析返回的报文与各银行的逻辑
	private Map<String, Object> parseResult(String result, String trnCode,
			String type, String ip) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (result != null && !(result.toString().trim().isEmpty())) {
			if ("spdb.request".equals(type)) {
				String returnCode = parseTag(result, "returnCode");
				if ("AAAAAAA".equals(returnCode)) {
					// 获得正确回复进行验签
					String sign = parseTag(result, "signature");
					String res = purePost(ip, sign, "spdb.verify", trnCode);
					Object transStatus = parseTag(res, "transStatus");
					Object acceptNo = parseTag(res, "acceptNo");
					if ("8801".equals(trnCode)) {
						if ("9".equals(transStatus)) {
							map.put("msg", "该笔支付已被撤销");
						} else if ("8".equals(transStatus)) {
							map.put("result", false);
							map.put("msg", "该笔支付处理失败,被拒绝");
						} else {
							boolean flag = false;
							if ("4".equals(transStatus) || "3".equals(transStatus)) {
								flag =true;
							}
							map.put("realRes", flag);
							parseCode(map, transStatus, null, trnCode);
							map.put("result", true);
							map.put("acceptNo", acceptNo);
						}
					} else if ("8804".equals(trnCode)) {
						Object err = parseTag(res, "note");
						if (transStatus != null) {
							parseCode(map, transStatus, err, trnCode);
						} else {
							map.put("msg", "该笔支付还没有进展!");
						}
					} else if ("EY03".equals(trnCode)) {
						if (transStatus != null) {
							parseCode(map, transStatus, null, trnCode);
						} else {
							map.put("msg", "该笔支付还没有进展!");
						}
					}
				} else {
					map.put("msg", parseTag(result, "returnMsg"));
				}
			} else if ("cmbc.request".equals(type)) {
				String code = parseTag(result, "code");
				if ("0".equals(code) || "WYHL01".equals(code)) {
					if ("Xfer".equals(trnCode)
							|| "TransferXfer".equals(trnCode)) {
						map.put("result", true);
					} else if ("qryXfer".equals(trnCode)) {
						String sCode = parseTag(result, "statusCode");
						if ("0".equals(sCode)) {
							map.put("result", true);
							map.put("msg", "原交易成功");
						} else if ("2".equals(sCode)) {
							map.put("result", false);
							String msg = parseTag(result, "statusErrMsg");
							map.put("msg", msg);
						} else {
							String msg = parseTag(result, "statusErrMsg");
							msg = msg == null ? "" : msg;
							map.put("msg", msg);
						}
					}
				} else {
					String msg = parseTag(result, "message");
					msg = msg == null ? "" : msg;
					map.put("msg", msg);
				}
			}
		} else {
			map.put("msg", "服务器无数据返回!");
		}
		return map;
	}
	
	private void parseCode(Map<String,Object> map, Object transStatus, Object err, String trnCode) {
		if ("EY03".equals(trnCode)) {
			switch (transStatus.toString()) {
			case "1":
				map.put("msg", "通讯失败");
				break;
			case "2":
				map.put("msg", "主机拒绝");
				map.put("result", false);
				break;
			case "3":
				map.put("msg", "网银拒绝");
				map.put("result", false);
				break;
			case "4":
				map.put("msg", "授权拒绝");
				map.put("result", false);
				break;
			case "5":
				map.put("msg", "交易录入，待授权");
				break;
			case "9":
				map.put("msg", "待处理");
				break;
			case "Y":
				map.put("msg", "交易提交不成功");
				map.put("result", false);
				break;
			case "A":
				map.put("msg", "等待进一步授权");
				break;
			case "D":
				map.put("msg", "后台处理中");
				break;
			case "S":
				map.put("msg", "后台处理完成");
				map.put("result", true);
				break;
			case "F":
				map.put("msg", "后台处理拒绝");
				map.put("result", false);
				break;
			case "C":
				map.put("msg", "后台处理撤销");
				map.put("result", false);
				break;
			default:
				break;
			}
		} else {
			switch (transStatus.toString()) {
			case "0":
				map.put("msg", "待补录：该笔支付需要柜员手工补录必要支付信息");
				break;
			case "1":
				map.put("msg", "待记帐：该笔支付已经处在等待柜员处理记账");
				break;
			case "2":
				map.put("msg", "待复核：该笔支付处于等待柜员处理复核阶段");
				break;
			case "3":
				map.put("msg", "待授权：该笔支付处于等待客户进行网银授权阶段");
				break;
			case "4":
				map.put("result", true);
				map.put("msg", "该笔支付处理成功，客户记账成功");
				break;
			case "8":
				map.put("result", false);
				map.put("msg", "该笔支付处理失败,被拒绝"+(err==null?"":("  "+err)));
				break;
			case "9":
				map.put("result", false);
				map.put("msg", "该笔支付已被撤销");
				break;
			default:
				break;
			}
		}
	}
	
	private Object record(String str, Object acceptNo, Object msg, boolean  query, String banktype) {
		List<String> sqls=new LinkedList<String>();
		Employee emp = SystemSession.getUser();
		StringBuffer sql=new StringBuffer("insert into bank$task (main_id,main_code,main_class,date_,amount_,STATUS_,ID_,RECEIVEMAN_,receiveid_,"
				+ "BANKNAME_,handdate_,PLEASEMAN_,VENDNAME_,remark_ ,UPLOADERCODE,PAYACCOUNT,ACCEPTNO,banktype,acntName) values( bank$task_seq.nextval,");
		Map<Object, Object> requestHeader=null;
		Map<Object,Object> dataBody=null;
		Object amount=null;
		Object id=null;
		Object receiveman=null;
		Object receiveAccount=null;//收款账号
		Object receiveBank=null;//收款账号银行名称
		Object payAccount=null;//付款账号
		Object payAccountName = null;//付款账号名称
		String class_=null;
		String code=null;
		String pleaseman="";
		String vendname="";
		String update=null;
		String[] strs=null;
		Object time=null;
		if ("spdb".equals(banktype)) {
			dataBody = praseXmltoMap(str.split("\\|")[1], 2).get(0);
			requestHeader = praseXmltoMap(str.split("\\|")[0], 3).get(0);
			strs = requestHeader.get("packetID").toString().split(","); // FY,FY17120001,3320
			time = requestHeader.get("timeStamp");
			class_ = billChange(strs[0]);
			code = strs[1];
			receiveman = dataBody.get("payeeName") == null ? "" : dataBody.get(
					"payeeName").toString();
			amount = dataBody.get("amount");
			id = requestHeader.get("packetID");
			payAccount = dataBody.get("acctNo");
			payAccountName = dataBody.get("acctName");
			receiveAccount = dataBody.get("payeeAcctNo");
			receiveBank = dataBody.get("payeeBankName");
		} else if ("cmbc".equals(banktype)) {
			// 防止乱码出现，解析时将GB2312全部换成UTF-8
			str = str.replace("GB2312", "utf-8");
			List<Map<Object, Object>> maps = praseXmltoMap(str, 3);
			requestHeader = maps.get(0);
			dataBody = maps.get(1);
			strs = dataBody.get("trnId").toString().split(",");
			time = requestHeader.get("dtClient");
			class_ = billChange(strs[1]);
			code = strs[0];
			receiveman = dataBody.get("acntToName") == null ? "" : dataBody
					.get("acntToName").toString();
			amount = dataBody.get("amount");
			id = dataBody.get("insId");
			payAccount = dataBody.get("acntNo");
			payAccountName = dataBody.get("acntName");
			receiveAccount = dataBody.get("acntToNo");
			receiveBank = dataBody.get("bankName");
		}
	
		if ("借款申请单".equals(class_) || "费用报销单".equals(class_)) {
			update = "update feeplease set FP_BANKSTATUS="
					+ (query ? "'受理中'" : "'转账失败'")
					+ " ,fp_bankremark="
					+ (query ? (msg == null ? "''" : "'" + msg + "'")
							: "'交易被拒绝,具体原因请查询'") + " where fp_code='" + code
					+ "'";
			Object[] data = baseDao.getFieldsDataByCondition("feeplease",
					"fp_pleaseman,fp_vendname", "fp_kind='" + class_
							+ "' and fp_code='" + code + "'");
			if (data != null) {
				pleaseman = data[0] == null ? "" : data[0].toString();
				vendname = data[1] == null ? "" : data[0].toString();
			}
		} else {
			SqlRowList rowSet = baseDao
					.queryForRowSet("select ppd_vendname from PAYPLEASEDETAIL where ppd_id=(select pp_id from payplease where pp_code='"
							+ code + "')");
			update = "update payplease set pp_bankstatus="
					+ (query ? "'受理中'" : "'转账失败'")
					+ ",pp_bankremark="
					+ (query ? (msg == null ? "''" : "'" + msg + "'")
							: "'交易被拒绝,具体原因请查询'") + "  where pp_code='" + code
					+ "'";
			if (rowSet.next()) {
				pleaseman = rowSet.getObject(1).toString();
				vendname = pleaseman;
			}
		}
		sql.append("'" + code + "',");
		sql.append("'" + class_ + "',");
		sql.append("to_date('" + time + "' ,'yyyy-MM-dd HH24:mi:ss'),");
		sql.append(amount + ",");
		sql.append(query ? "'受理中'," : "'转账失败',");
		sql.append("'"  +id + "',");
		sql.append("'" + receiveman + "',");
		sql.append("'" + receiveAccount + "',");
		sql.append("'" + receiveBank+ "',");
		sql.append("'' ,");
		sql.append("'" + pleaseman + "',");
		sql.append("'" + vendname + "',");
		sql.append(msg == null ? "'未进行过查询'," : "'"+msg+"',");
		sql.append("'" + emp.getEm_code() + "',");
		sql.append("'" + payAccount + "',");
		sql.append("'" + (acceptNo == null ?"" : acceptNo) + "',");
		sql.append("'"+banktype+"',");
		sql.append("'" + (payAccountName == null ? "" : payAccountName) + "'");
		sql.append(")");
		sqls.add(update);
		sqls.add(sql.toString());
		baseDao.execute(sqls);
		return id;
	}

//xml解析成map
	@SuppressWarnings("unchecked")
	private static List<Map<Object,Object>> praseXmltoMap(String str, Integer level){
		SAXReader sax=new SAXReader();
		List<Map<Object,Object>> maps=new LinkedList<Map<Object,Object>> ();
		Map<Object,Object> map;
		Document doc=null;
		InputStream in=null;
			in = new ByteArrayInputStream(str.getBytes());
		try {
			doc=sax.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		if (level==3) {
			List<Element> childElements = root.elements();
			for(Element e:childElements){
				map=new HashMap<Object,Object>();
				List<Element> sonElements = e.elements();
				for(Element son:sonElements){
					map.put(son.getName(), son.getText());
				}
				maps.add(map);
			}
		} else if (level==2) {
			List<Element> childElements = root.elements();
			map=new HashMap<Object,Object>();
			for(Element e:childElements){
				map.put(e.getName(), e.getText());
			}
			maps.add(map);
		}
		return maps;
	}

public List<Map<String,Object>>  createQryMap(String ids,String banktype) {
	List<Map<String,Object>> maps=new LinkedList<Map<String,Object>>();
	Map<String,Object> map=null; 
	String[] strs;
	strs=ids.startsWith("[")?ids.substring(1, ids.length()-1).split(","):ids.split(",");
	if("cmbc".equals(banktype)){
		for(String idstr:strs){
			map=new HashMap<String,Object>();
			String id=idstr.substring(1,idstr.length()-1);
			map.put("verified", true);
			map.put("trnId", "t"+id);
			map.put("insId", id);
			map.put("cltcookie", "");
			map.put("svrId", "");
			maps.add(map);
		}	
	}else if("spdb".equals(banktype)){
		//"TA17110030","TA17110028","TA17110027" 去除引号
		List<Object[]> qrys = baseDao.getFieldsDatasByCondition("bank$task", new String[]{"ID_","to_char(DATE_,'yyyymmdd')","ACCEPTNO","PAYACCOUNT","MAIN_CLASS","main_code","to_char(DATE_+15,'yyyymmdd')"}, "id_ in("+ids.substring(1, ids.length()-1).replace("\"", "'")+") and banktype='"+banktype+"'");
		for(Object[] qry:qrys){
			map=new HashMap<String,Object>();
			map.put("verified", true);
			map.put("ori-packetID", qry[0]);//将转账时的ID进行记录，非银行字段
			/*	map.put("elecChequeNo", billChange(qry[4].toString())+","+qry[5]);
			map.put("acctNo", qry[3]);
			map.put("beginDate", qry[1]);
			map.put("endDate", endDate);
			map.put("acceptNo", qry[2]);
			map.put("serialNo", null);
			map.put("queryNumber", 1);
			map.put("beginNumber",1);
			map.put("singleOrBatchFlag", 0);*/
			map.put("elecChequeNo", billChange(qry[4].toString())+","+qry[5]);
			map.put("acctNo", qry[3]);
			map.put("beginDate", qry[1]);
			map.put("endDate", qry[6]);
			map.put("seqNo", qry[2]);
			map.put("queryNumber", 1);
			map.put("beginNumber",1);
			map.put("transType", 8801);
			map.put("authMasterID", "");
			maps.add(map);
		}
	}
	return maps;
}

private void updateAndTurnBankRegister(Map<String, Object> flag, String xml,String id_,String banktype) {
	List<Map<Object,Object>> maps=null;
	//String id_=null;
	if(id_==null){
		if ("cmbc".equals(banktype)) {
			//防止乱码出现，解析时将GB2312全部换成UTF-8
			xml=xml.replace("GB2312", "utf-8");
			maps = praseXmltoMap(xml,3);
			Map<Object,Object> body=maps.get(1);
			id_=body.get("insId").toString();
		} else if ("spdb".equals(banktype)) {
			Map<Object,Object> body=praseXmltoMap(xml.split("\\|")[1],2).get(0);
			id_=body.get("ori-packetID").toString();
		}	
	}
	Object[] obj = baseDao.getFieldsDataByCondition("bank$task", "amount_,main_class,main_code,status_,uploadercode", "id_='"+id_+"'");
		if (obj != null) {
			SqlRowList set = baseDao
					.queryForRowSet(" select category_code,category_name from bank$ where bank_kind='"
							+ banktype + "'");
			Employee emp = null;
			String result = null;
			if (obj[4] != null) {
				emp = employeeService.getEmployeeByEmcode(obj[4].toString());
			}
			String paymentcode = "";
			String payment = "";
			if (set.next()) {
				paymentcode = set.getString("category_code");
				payment = set.getString("category_name");
			}
			Object msg = flag.get("msg");
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String time = sf.format(date);
			int ar_id;
			String caller = null;
			String status = null;
			String code;
			String ar_code = null;
			String class_;
			double thispayamount;
			String type;
			int id = 0;
			String refno;
			msg = msg == null ? "" : msg;
			StringBuffer sb = new StringBuffer("update bank$task set ");
			thispayamount = Double.parseDouble(obj[0].toString());
			class_ = obj[1].toString();
			code = obj[2].toString();
			if ("转账成功".equals(obj[3].toString())) {
				return;
			}
			if (flag.get("result") == null) {
				sb.append("remark_='" + msg + "' where id_='" + id_ + "'");
			} else if ((Boolean) flag.get("result")) {
				Object[] fp;
				result = "成功";
				if ("费用报销单".equals(class_) || "借款申请单".equals(class_)) {
					caller = "费用报销单".equals(class_) ? "FeePlease!FYBX"
							: "FeePlease!JKSQ";
					fp = baseDao.getFieldsDataByCondition("feeplease", "fp_id",
							"fp_code='" + code + "'");
					id = Integer.parseInt(fp[0].toString());
					String res = feePleaseService.turnBankRegister(id,
							paymentcode, payment, thispayamount, caller);
					String idstr = res.substring(res.indexOf("aridIS")
							+ "aridIS".length(), res.indexOf("&whoami"));
					ar_id = Integer.parseInt(idstr);
					ar_code = res.substring(res.indexOf(">") + ">".length(),
							res.indexOf("</a>"));
					baseDao.execute("update feeplease set fp_bankstatus='转账成功',fp_bankremark='" + msg + "' where fp_code='"
							+ code + "'");
					baseDao.logger.others("转账操作",
							"成功转帐" + String.valueOf(thispayamount) + " 流水ID:"
									+ id_, caller, "fp_id", fp[0].toString());
				} else {
					caller = "付款申请单".equals(class_) ? "PayPlease" : "PayPlease!YF";
					Object[] data = baseDao.getFieldsDataByCondition("payplease", "pp_id,pp_type,pp_thispaydate,pp_refno", "pp_code='" + code + "'");
					Object o = baseDao.getFieldDataByCondition("paypleasedetail", "ppd_id", "ppd_ppid='" + data[0].toString() + "'");
					baseDao.execute("update payplease set pp_paymentcode='"
							+ paymentcode + "' , pp_payment='" + payment
							+ "' where pp_code='" + code + "'");
					id = Integer.parseInt(o.toString());
					refno = data[3] == null ? "" : data[3].toString();
					type = data[1].toString();
					JSONObject obj2 = payPleaseDao.turnBankRegister(id, code,
							type, thispayamount, time, refno);
					ar_id = obj2.getInt("ar_id");
					ar_code = obj2.getString("ar_code");
					baseDao.execute("update payplease set pp_bankstatus='转账成功',pp_bankremark='" + msg + "' where pp_code='"
							+ code + "'");
					baseDao.logger.others("转账操作",
							"成功转帐" + String.valueOf(thispayamount) + " 流水ID:"
									+ id_, caller, "pp_id", data[0].toString());
				}
			//	status = submitAndAccounted(ar_id, caller);
				sb.append("status_='转账成功', REGISTER_CODE='" + ar_code
						+ "', REGISTER_STATUS='在录入', remark_='"
						+ msg + "' where id_='" + id_ + "'");
				baseDao.execute("update ACCOUNTREGISTER set ar_bankid='" + id_
						+ "' where ar_code='" + ar_code + "'");
			} else {
				result = "失败";
				if ("费用报销单".equals(class_) || "借款申请单".equals(class_)) {
					baseDao.execute("update feeplease set fp_bankstatus='转账失败',fp_bankremark='" + msg + "' where fp_code='"
							+ code + "'");
				} else {
					baseDao.execute("update payplease set pp_bankstatus='转账失败',pp_bankremark='" + msg + "' where pp_code='"
							+ code + "'");
				}
				sb.append("status_='转账失败' , remark_='" + msg + "' where id_='"
						+ id_ + "'");
			}
			if (emp != null && result != null) {
				sendMsg(emp, class_ + " :" + code + " 转账" + result, id_);
			}
			baseDao.execute(sb.toString());
		}
}

private void sendMsg(Employee emp,String msg,String id){
	List<String> sqls=new LinkedList<String>();
	int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
	int ih_id = baseDao.getSeqId("ICQHISTORY_SEQ");
	
	sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,PR_FROM,pr_title,pr_caller,pr_keyvalue)"
			+ " select distinct '"+pr_id+"','系统管理员',sysdate,0,'"+msg+"','system',null,'BankTask',main_id  from bank$task where id_='"+id+"'");	
	sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient)"
			+ "select pagingreleasedetail_seq.nextval,"+pr_id+",'"+emp.getEm_id()+"','"+emp.getEm_name()+"'  from dual");	
	sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
			+ "select "+ih_id+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id "
			+ "from PAGINGRELEASE where pr_id="+pr_id);	
	sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
			+ "select ICQHISTORYdetail_seq.nextval,"+ih_id+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail "
			+ "where prd_prid="+pr_id+" and ("+ih_id+",prd_recipient,prd_recipientid) "
			+ "not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");		
	baseDao.execute(sqls);
}

/*private String submitAndAccounted(int ar_id,String caller){
	String status="在录入";
	//银行登记提交
	try{			
		accountRegisterBankService.submitAccountRegister(ar_id, "AccountRegister!Bank");
		status="已提交";
	}catch(Exception e){
		e.printStackTrace();
	}	
	return status;
}
*/
	//单据caller与名称转换
	private String billChange(String caller){
		String str=null;
		if(caller!=null){
			switch(caller){
			case"FY":
				str="费用报销单";
				break;
			case"JK":
				str="借款申请单";
				break;
			case"FK":
				str="付款申请单";
				break;
			case"YF":
				str="预付款申请单";
				break;
			case"费用报销单":
				str="FY";
				break;
			case"借款申请单":
				str="JK";
				break;
			case"付款申请单":
				str="FK";
				break;
			case"预付款申请单":
				str="YF";
				break;
			}
		}
		return str;
	}
	
	private String parseTag(String xml, String tagName) {
		if (xml != null && tagName != null && xml.contains(tagName)) {
			return xml.substring(xml.indexOf("<" + tagName + ">") + ("<" + tagName + ">").length(),
					xml.indexOf("</" + tagName + ">"));
		} else
			return null;
	}
	
	//进行签名的函数
	private List<Map<String, Object>> toSign(List<Map<String, Object>> bodyMaps, String banktype, String ip) {
		List<Map<String,Object>> signMaps=new LinkedList<Map<String,Object>>();
		Map<String,Object> map=null;
		String bodyXml=null;
		for (Map<String, Object> m : bodyMaps) {
			if ((boolean) m.get("verified")) {
				map = new HashMap<String, Object>();
				Object ori_packetID = m.get("ori-packetID");
				m.remove("verified");
				m.remove("ori-packetID");
				String body = map2Xml(m);
				String[] data = m.get("elecChequeNo").toString().split(",");
				StringBuffer sign = new StringBuffer("<body>");
				sign.append(body);
				sign.append("</body>");
				Object res = purePost(ip, sign.toString(), "spdb.sign", "8801");
				if (res != null && !(res.toString().trim().isEmpty())) {
					String tag = parseTag(res.toString(), "result");
					if ("0".equals(tag)) {
						String signature = parseTag(res.toString(), "sign");
						// 查询时将原先的packetid加入body中
						bodyXml = ori_packetID == null ? sign.toString()
								: (sign.substring(0, sign.length() - 7)
										+ "<ori-packetID>" + ori_packetID + "</ori-packetID></body>");
						map.put("signature", signature);
						map.put("id", m.get("elecChequeNo"));
						map.put("original", bodyXml);
						map.put("verified", true);
						signMaps.add(map);
					} else {
						map.put("verified", false);
						updateRemark(data[1], billChange(data[0]), "签名失败!");
					}
				} else {
					map.put("verified", false);
					updateRemark(data[1], billChange(data[0]), "签名服务器未返回数据!");
				}
			}
		}
		return signMaps;
	}
			
/*	@Override
	public Map<String, Object> singleTransferRequest(String ip,String class_,String code){
		Map<String, Object> map = postRequest(ip, "TransferXfer", class_, code);
		return map;
	}	

	@Override
	public Map<String, Object> searchRequest(String ip,String class_,String code){
		Map<String, Object> map = postRequest(ip, "qryXfer", class_, code);
		return map;
	}
	
	private Map<String, Object> postRequest(String ip, String trnCode,
			String class_, String code) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> headMap = getBankInfoByEmp(employee, "222", "", "");
		Map<String, Object> bodyMap = createBodyMap(code, class_, trnCode, "",
				"banktype");
		String xml = createXml(headMap, bodyMap, trnCode, "");
		Map<String, Object> map = null;// post("http://"+ip+":8088/eweb/b2e/connect.do",xml,
										// trnCode);

		if ("Xfer".equals(trnCode) || "TransferXfer".equals(trnCode)) {
			if ((Boolean) map.get("result")) {
				record(xml, "acceptNo", null, true, "spdb");
			}
		} else if ("qryXfer".equals(trnCode)) {
			updateAndTurnBankRegister(map, xml, null, "banktype");
		} else {
			map.put("msg", "没有对应的操作");
		}
		return map;
	}*/
}
