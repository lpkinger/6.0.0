package com.uas.erp.service.common.impl;

public class XmlStringFilter {
	int start = 0;
	int end = 0;
	int counter = 0;
	/** 该方法是把 所有的 <sql> 子节点 中的 <parameters> 
	 * 中的 <object 中的  "&gt;","&apos;" 
	 * 替换成 "<" ,">"
	 */
	/**现在前台休整后，此方法用处不大 **/
	String handler(String xmlString){
		if(xmlString.contains("<parameters>")&&start!=-1&&end!=-1){
			start = xmlString.indexOf("<parameters>",start+counter);
			if(start!=-1){
				end = xmlString.indexOf("</parameters>",end+counter);
				String substr = xmlString.substring(start, end).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'");
				String str = xmlString.replace(xmlString.substring(start, end),substr); // 第一次替换 ;
				counter = counter+13;
				return handler(str);
			}
		}
		return xmlString;
	}
	
	String replaceQuotation(String str){
		return str.replaceAll("'", "\"");
	}
	
	String replaceSingleQuotation(String str){
		return str.replaceAll("\"","'" );
	}
	
	 String replaceSomeSign(String str){
		if(str.contains("<cancel")||str.contains("<error")){
			return str.replaceAll("<cancel","<end-cancel").replaceAll("<error","<end-error");
		}
		return str;
	}
	
	public static void main(String[] args) {
		//String s = "<process xmlns='http://jbpm.org/4.4/jpdl'><sql var='s' unique='s' g='213,116,90,50' name='sql 2'><query>s</query><description>sssss</description><parameters>&lt;object name = &quot;dp_name&quot; expr=&quot;#{department}&quot; &gt;&lt;/object&gt;</parameters><transition to='sql 3'/></sql><sql var='d' unique='d' g='216,223,90,50' name='sql 3'><query>d</query><parameters>&lt;string name = &quot;dp_name&quot; expr=&quot;#{department}&quot; &gt;&lt;/object&gt;</parameters><description>ddddddd</description></sql></process>";
		/*System.out.println(handler(s));*/
		String str = "<process xmlns='http://jbpm.org/4.4/jpdl'>"+
       "<start g='253,67,48,48' name='start 1'>"+
       "<transition to='end 1'/>"+
       "</start>"+
       "<end g='247,239,48,48' name='end 1'/>"+
       "<task candidate-groups='C005,C006,C007,C004,C008' g='333,189,90,50' name='task 1'/>"+
       "<task candidate-groups='C005,C006,C007,C004,C008' g='349,298,90,50' name='task 2'/>"+
	"</process>";
		/*System.out.println(getCandidateGroupsOfXml(str));*/
	}

}
