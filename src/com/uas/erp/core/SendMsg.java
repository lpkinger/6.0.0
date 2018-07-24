package com.uas.erp.core;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Vector;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.uas.erp.dao.BaseDao;

import sun.misc.BASE64Encoder;

public class SendMsg {
	public SendMsg() {
	}

	private String registerUrl = "http://202.105.212.146:8080/jboss-net/services/Register";
	private String sendUrl = "http://202.105.212.146:8080/jboss-net/services/SendSMS";
	// private static String callBack = "http://127.0.0.1:8080/Callback";
	private static String callBack = "http://102.71.103.47:8080/jboss-net/services/CallBack";
	private String soapurl = "http://xml.apache.org/xml-soap";
	private String isreturn = "0";
	private int msgid = 1006;
	// final static String PWD="Aa123456";
	// final static String UC="18098932060";
	final static String PWD = "sD456789()";
	final static String UC = "18022285520";
	final static Vector<Vector> rowdata = null;

	// 18022285520 密码：SD123456 qiaojing
	/**
	 * 
	 * @return String
	 */
	public String getRandom() {
		String rand = "";
		try {
			Service srv = new Service();
			Call call = (Call) srv.createCall();
			call.setTargetEndpointAddress(new URL(this.registerUrl));
			call.setOperationName("getRandom");
			call.setReturnType(XMLType.XSD_STRING);
			rand = (String) call.invoke(new Object[] {});
		} catch (ServiceException ex) {
			System.out.println("createCall:" + ex.getMessage());
		} catch (MalformedURLException ex) {
			System.out.println("setTargetEndpointAddress:" + ex.getMessage());
		} catch (RemoteException ex) {
			System.out.println("invoke:" + ex.getMessage());
		}
		return rand;
	}

	public String setCallBackAddress(String uc, String pwd, String callbackurl) {
		String connid = "";
		String rand = this.getRandom();
		String md5pwd = this.MD5Encode(rand + pwd + pwd);
		Service srv = new Service();
		try {
			Call call = (Call) srv.createCall();
			call.setTargetEndpointAddress(new URL(this.registerUrl));
			call.setOperation("setCallBackAddr");
			call.addParameter("uc", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("pw", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("rand", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("url", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			connid = (String) call.invoke(new Object[] { uc, md5pwd, rand, callbackurl });
		} catch (ServiceException ex) {
			System.out.println("call.createCall" + ex.getMessage());
		} catch (MalformedURLException ex) {
			/** @todo Handle this exception */
			System.out.println("URL" + ex.getMessage());
		} catch (RemoteException ex) {
			/** @todo Handle this exception */
			System.out.println("call.invoke" + ex.getMessage());
		}
		return connid;
	}

	public String sendMsg(String uc, String pwd, String callees, String isReturn, String cont, int msgid, String connid) {
		String re = "";
		String rand = this.getRandom();
		String callee[] = callees.split(",");
		String md5pwd = this.MD5Encode(rand + pwd + pwd);
		Service srv = new Service();
		try {
			Call call = (Call) srv.createCall();
			call.setTargetEndpointAddress(new URL(this.sendUrl));
			call.setOperationName("sendSMS");

			call.addParameter("uc", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("pw", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("rand", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("callee", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("isreturn", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("cont", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("msgid", XMLType.XSD_INT, ParameterMode.IN);
			call.addParameter("connID", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);// 杩斿洖鍊肩被鍨�
			re = (String) call.invoke(new Object[] { uc, md5pwd, rand, callee, isReturn, Bey64Ecnode(cont), msgid, connid });// 璋冪敤鏂规硶
		} catch (ServiceException ex) {
			System.out.println("createCall" + ex.getMessage());
		} catch (MalformedURLException ex) {
			/** @todo Handle this exception */
			System.out.println("setTargetEndpointAddress" + ex.getMessage());
		} catch (RemoteException ex) {
			/** @todo Handle this exception */
			System.out.println("invoke" + ex.getMessage());
		}
		return re;
	}

	public String sendMsg(String callees, String isReturn, String cont, int msgid, String connid, String msguc, String msgpwd) {
		String re = "";
		String rand = this.getRandom();
		String callee[] = callees.split(",");
		String md5pwd = this.MD5Encode(rand + msgpwd + msgpwd);
		Service srv = new Service();
		try {
			Call call = (Call) srv.createCall();
			call.setTargetEndpointAddress(new URL(this.sendUrl));
			call.setOperationName("sendSMS");
			call.addParameter("uc", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("pw", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("rand", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("callee", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("isreturn", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("cont", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("msgid", XMLType.XSD_INT, ParameterMode.IN);
			call.addParameter("connID", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			re = (String) call.invoke(new Object[] { msguc, md5pwd, rand, callee, isReturn, Bey64Ecnode(cont), msgid, connid });
		} catch (ServiceException ex) {
			System.out.println("createCall" + ex.getMessage());
		} catch (MalformedURLException ex) {
			/** @todo Handle this exception */
			System.out.println("setTargetEndpointAddress" + ex.getMessage());
		} catch (RemoteException ex) {
			/** @todo Handle this exception */
			System.out.println("invoke" + ex.getMessage());
		}
		return re;
	}

	private static String Bey64Ecnode(String s) {
		if (s == null) {
			return null;
		}
		BASE64Encoder en = new BASE64Encoder();
		try {
			return en.encode(s.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String MD5Encode(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	private String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public void sendMsg(final String tels, final String emcode, final String nodeId, final String msg, final String uc, final String pwd)
			throws Exception {
		final SendMsg call = new SendMsg();
		final String randomstr = StringUtil.getRandomString(10);
		try {
			final DesUtil des = new DesUtil(randomstr);
			des.setKey(randomstr);
			new Thread() {
				public void run() {
					String callbackurl = callBack.trim();
					String jtamsg = "";
					BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
					if (msg == null) {
						try {

							jtamsg = baseDao.getDBSetting("ERPServer") + "/process/LoadProcess.action?em_code=" + des.encrypt(emcode)
									+ "&nodeId=" + des.encrypt(nodeId) + "&root=" + randomstr;
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else
						try {
							jtamsg = msg + " " + baseDao.getDBSetting("ERPServer") + "/process/LoadProcess.action?em_code="
									+ des.encrypt(emcode) + "&nodeId=" + des.encrypt(nodeId) + "&root=" + randomstr;
							// jtamsg=msg+"http:192.168.1.106:8080/ERP/process/LoadProcess.action?em_code="+ des.encrypt(emcode)+"&nodeId="+des.encrypt(nodeId)+"&root="+randomstr;
						} catch (Exception e) {
							e.printStackTrace();
						}
					String cont = jtamsg.trim();
					String connid = call.setCallBackAddress(uc, pwd, callbackurl);
					call.sendMsg(tels, "0", cont, 1008, connid, uc, pwd);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendSimpleMsg(final String tels, final String msg, final String uc, final String pwd) throws Exception {
		final SendMsg call = new SendMsg();
		final String randomstr = StringUtil.getRandomString(10);
		try {
			final DesUtil des = new DesUtil(randomstr);
			des.setKey(randomstr);
			new Thread() {
				public void run() {
					String callbackurl = callBack.trim();
					String cont = msg.trim();
					String connid = call.setCallBackAddress(uc, pwd, callbackurl);
					call.sendMsg(tels, "0", cont, 1008, connid, uc, pwd);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
