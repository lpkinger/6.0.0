package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.StartExamService;

@Service
public class StartExamServiceImpl implements StartExamService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> start(String password, String name,HttpSession session,String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		int count = 0;
		Object[] data = new Object[4] ;
		if(!StringUtil.hasText(code)){
			count=baseDao.getCount("select count(1) from Recuitinfo where re_name='"+name+"' and substr(re_phone,-6,6)='"+password+"'");
			if(count != 1){
				map.put("exmsg", "姓名或密码不正确!");
				return map;
			}
			data = baseDao.getFieldsDataByCondition("exam", 
					new String[]{"ex_id","ex_starttime","ex_latetime","ex_issubmit"},
					"ex_recorder='"+name+"' and ex_exiid is null and nvl(EX_ISSUBMIT,' ') <> '是' order by ex_id desc");
			if(data==null){
				map.put("exmsg", "您没有相应的试卷，请联系工作人员！");
				return map;
			}
		}else{
			count=baseDao.getCount("select count(1) from employee where em_name='"+name+"' and substr(em_mobile,-6,6)='"+password+"' and nvl(em_class,' ') <> '离职' and em_code = '"+code+"'");
			if(count != 1){
				map.put("exmsg", "人员填写有误或在职状态异常!");
				return map;
			}
			data = baseDao.getFieldsDataByCondition("exam", 
					new String[]{"ex_id","ex_starttime","ex_latetime","ex_issubmit"},
					"ex_recorder='"+name+"' and ex_recordercode ='"+code+"' and ex_exiid is not null and nvl(EX_ISSUBMIT,' ') <> '是' order by ex_id desc");
			if(data==null){
				map.put("exmsg", "您没有相应的试卷，请联系工作人员！");
				return map;
			}
		}
	
		long starttime=DateUtil.parseStringToDate(data[1].toString(), "yyyy-MM-dd HH:mm:ss").getTime();
		long latetime=DateUtil.parseStringToDate(data[2].toString(), "yyyy-MM-dd HH:mm:ss").getTime();
		long now=System.currentTimeMillis();
		if(now<starttime){
			map.put("exmsg", "考试尚未开始,请稍后重试！");
			return map;
		}
		if(now>latetime){
			map.put("exmsg", "迟到太久,不予考试！");
			return map;
		}
		map.put("success", true);
		session.setAttribute("examId", Integer.parseInt(data[0].toString()));
		//map.put("examId", Integer.parseInt(data[0].toString()));
		return map;
	}

	@Override
	public Map<String, Object> getExam(int ex_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", baseDao
				.getFieldsDatasByCondition("examdetail ", new String[] {
						"exd_id", "exd_detno", "exd_eqcontent", "exd_eqtype",
						"exd_seleanswer" }, "exd_exid=" + ex_id
						+ " order by exd_detno"));
		Object[] data=baseDao.getFieldsDataByCondition("exam", new String[]{"ex_starttime","ex_endtime","ex_recorder"}, "ex_id="+ex_id);
		modelMap.put("starttime", data[0]);
		modelMap.put("endtime", data[1]);
		modelMap.put("name", data[2]);
		return modelMap;
	}

	@Override
	public void submitExam(List<Map<Object, Object>> data) {
		if(data==null||data.size()==0){
			return;
		}
		Object ex_id = baseDao.getFieldDataByCondition("examdetail",
				"exd_exid", "exd_id=" + data.get(0).get("exd_id"));
		if(baseDao.getCount("select count(1) from exam where ex_issubmit='是' and ex_id="+ex_id)==1){
			BaseUtil.showError("该试卷已经提交，请不要重复提交!");
		}
		List<Object[]> exam = baseDao.getFieldsDatasByCondition("examdetail ",
		new String[] { "exd_id", "exd_detno", "exd_eqcontent","exd_eqtype", "exd_stanscore", "exd_stanswer" }
		,"exd_exid=" + ex_id + " order by exd_detno");
		int index = 0;
		List<String> sqls = new ArrayList<String>(50);
		for (Object[] e : exam) {
			if ("多选题".equals(e[3]) || "单选题".equals(e[3])|| "判断题".equals(e[3])) {
				
				if (e[0].toString().equals(data.get(index).get("exd_id").toString())) 
				{
					Object score = judgeScore(e[5],data.get(index).get("answer"),e) ;
					
					sqls.add("update examdetail set "
							+ "exd_answer='"+data.get(index).get("answer") + 
							"',exd_score="+ score 
							+ " where exd_id="+ data.get(index).get("exd_id"));
					
				} else {// ID对不上
					for (Map<Object, Object> d : data) {
						if (e[0].equals(d.get("exd_id"))) {
							Object score = judgeScore(e[5],data.get(index).get("answer"),e);
									
							sqls.add("update examdetail set "
									+ "exd_answer='"+ d.get("answer") 
									+ "',exd_score=" + score
									+ " where exd_id=" + d.get("exd_id"));
							break;
						}
					}
				}
			} else {// 简答题，填空题
				if (e[0].toString().equals(data.get(index).get("exd_id").toString())) {
					sqls.add("update examdetail set exd_answer='"
							+ data.get(index).get("answer") + "' where exd_id="
							+ data.get(index).get("exd_id"));
				} else {// ID对不上
					for (Map<Object, Object> d : data) {
						if (e[0].equals(d.get("exd_id"))) {
							sqls.add("update examdetail set exd_answer='"
									+ data.get(index).get("answer")
									+ "' where exd_id="
									+ data.get(index).get("exd_id"));
							break;
						}
					}
				}

			}
			index++;
		}
		sqls.add("update exam set ex_total=(select sum(exd_stanscore) from examdetail where exd_exid="
				+ ex_id + ")  where ex_id=" + ex_id);
		sqls.add("update exam set ex_choicescore=(select sum(exd_score) from examdetail where exd_exid="
				+ ex_id + ") where ex_id=" + ex_id);
		sqls.add("update exam set ex_issubmit='是' where ex_id="+ex_id);
		baseDao.execute(sqls);
	}

	/**
	 * @param answer
	 * @param stdanswer
	 *            标准答案
	 * @param actanswer
	 *            实际答案
	 * @return
	 */
	private boolean isRight(Map<String, Integer> answer, Object stdanswer,Object actanswer) {
		if (stdanswer.equals(actanswer)) {
			return true;
		}
		char[] stdchars = stdanswer.toString().trim().toUpperCase()
				.toCharArray();
		char[] actchars = actanswer.toString().trim().toUpperCase()
				.toCharArray();
		if (stdchars.length != actchars.length) {
			return false;
		}
		int stdint = 0;
		int actint = 0;
		for (int i = 0; i < stdchars.length; i++) {
			stdint += answer.get(stdchars[i] + "");
			actint += answer.get(actchars[i] + "");
		}
		if (stdint == actint) {
			return true;
		}
		return false;
	}
	/**
	 * @param answer
	 * @param stdanswer
	 *            标准答案
	 * @param actanswer
	 *            实际答案
	 * @return
	 */
	private Object judgeScore( Object stdanswer,Object actanswer,Object[] e) {
		//String type = e[4];
		/*new String[] { "exd_id", "exd_detno", "exd_eqcontent","exd_eqtype", "exd_stanscore", "exd_stanswer" }*/
		Map<String, Integer> answer = new HashMap<String, Integer>();
		answer.put("A", 1);
		answer.put("B", 10);
		answer.put("C", 100);
		answer.put("D", 1000);
		answer.put("E", 10000);
		answer.put("F", 100000);
		answer.put("G", 1000000);
		if (stdanswer.equals(actanswer)) {
			return e[4];
		}
		//多选题 有错误选项则不给分，漏选按比例给分
		if(e[3].equals("多选题")){
			//stdanswer 标准答案
			char[] stdchars = stdanswer.toString().trim().toUpperCase()
					.toCharArray();
			//actanswer 实际答案
			char[] actchars = actanswer.toString().trim().toUpperCase()
					.toCharArray();
			int right = 0;
			int error = 0;
			int stdint = 0;
			int actint = 0;
			//核对答案
			for (int i = 0; i < actchars.length; i++) {
				if(stdanswer.toString().indexOf(actchars[i])>=0){
					right ++;
				}else{
					error ++;
				}
				actint += answer.get(actchars[i] + "");
			}
			for (int i = 0; i < stdchars.length; i++) {
				stdint += answer.get(stdchars[i] + "");
			}
			if(error>0){
				return 0;
			}else if (right == stdchars.length){
				//已选对数量等于
				if(stdint == actint){
					return e[4];
				}else{
					return Double.valueOf(e[4].toString())*0.5;
				}
			}else{
				return Double.valueOf(e[4].toString())*0.5;
			}
//			if (stdchars.length == actchars.length) {//长度相同
//				int stdint = 0;
//				int actint = 0;
//				for (int i = 0; i < stdchars.length; i++) {
//					stdint += answer.get(stdchars[i] + "");
//					actint += answer.get(actchars[i] + "");
//				}
//				/*if (stdint == actint) {
//					return e[4];
//				}*/
//				
//			}
//		}else{//长度不同
		}
		return 0;
	}

	@Override
	public void selScheme(String caller,String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer errBuffer = new StringBuffer();
		if (maps.size() > 0) {
			StringBuffer names = new StringBuffer();
			StringBuffer schemes = new StringBuffer();
			StringBuffer starttimes = new StringBuffer();
			StringBuffer latetimes = new StringBuffer();
			StringBuffer endtimes = new StringBuffer();
			Object exi_escode = "";
			Object exi_exstarttime = "";
			Object exi_exlatetime = "";
			Object exi_exendtime = "";
			boolean bool = true;
			String errStr ="";
			if(caller.equals("Recuitinfo!SelScheme")){
				for (Map<Object, Object> map : maps) {
					errStr = "";
					bool = true;
					exi_escode = "";
					exi_exstarttime = "";
					exi_exlatetime = "";
					exi_exendtime = "";
					
					if(map.get("re_escode") != null && !"".equals(map.get("re_escode"))){
						exi_escode = map.get("re_escode");
					} else {
						errStr += "考生<"+map.get("re_name")+">没有指定方案！<hr>";
					}
					
					if(map.get("re_exstarttime") != null && !"".equals(map.get("re_exstarttime"))){
						exi_exstarttime = map.get("re_exstarttime");
					} else {
						errStr += "考生<"+map.get("re_name")+">没有指定开始时间！<hr>";
					}
					
					if(map.get("re_exlatetime") != null && !"".equals(map.get("re_exlatetime"))){
						exi_exlatetime = map.get("re_exlatetime");
					} else {
						errStr += "考生<"+map.get("re_name")+">没有指定最迟取卷时间！<hr>";
					}
					
					if(map.get("re_exendtime") != null && !"".equals(map.get("re_exendtime"))){
						exi_exendtime = map.get("re_exendtime");
					} else {
						errStr += "考生<"+map.get("re_name")+">没有指定结束时间！<hr>";
					}
					
					//exi_exstarttime > exi_exlatetime > exi_exendtime
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exlatetime)){
						//exi_exlatetime > exi_exstarttime
						if (!judgeTime(exi_exstarttime,exi_exlatetime)){
							errStr += "考生<"+map.get("em_name")+">最迟取卷时间需大于开始时间！<hr>";
						}
					}
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exendtime)){
						//exi_exendtime > exi_exstarttime
						if(!judgeTime(exi_exstarttime,exi_exendtime)){
							errStr += "考生<"+map.get("em_name")+">结束时间需大于开始时间！<hr>";
						}
					}
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exendtime)){
						//exi_exendtime > exi_exlatetime
						if(!judgeTime(exi_exstarttime,exi_exendtime)){
							errStr += "考生<"+map.get("em_name")+">考试结束时间需大于最迟取卷时间！<hr>";
						}
						if(!exi_exstarttime.toString().substring(0, 10).equals(exi_exendtime.toString().substring(0, 10))){
							//exi_exstarttime == exi_exendtime Y:M:S
							errStr += "考生<"+map.get("em_name")+">开始时间和结束时间必须在同一天！<hr>";
						}
					}
					if(errStr.length()>0){
						bool = false;
						errBuffer.append(errStr);
					}
					if(bool){
						names.append(map.get("re_name")+"#");
						schemes.append(exi_escode+"#");
						starttimes.append(exi_exstarttime+"#");
						latetimes.append(exi_exlatetime+"#");
						endtimes.append(exi_exendtime+"#");
					}
				}
			}else{
				Object exi_escode1 = maps.get(0).get("exi_escode1");
				Object exi_exstarttime1 = maps.get(0).get("exi_exstarttime1");
				Object exi_exlatetime1 = maps.get(0).get("exi_exlatetime1");
				Object exi_exendtime1 = maps.get(0).get("exi_exendtime1");
				for (Map<Object, Object> map : maps) {
					errStr = "";
					bool = true;
					exi_escode = "";
					exi_exstarttime = "";
					exi_exlatetime = "";
					exi_exendtime = "";
					//考试方案编号
					if(map.get("exi_escode") != null && !"".equals(map.get("exi_escode"))){
						exi_escode = map.get("exi_escode");
					} else if(StringUtil.hasText(exi_escode1)){
						exi_escode = exi_escode1;
					}else{
						errStr += "考生<"+map.get("em_name")+">没有指定方案！<hr>";
					}
					
					//考试开始时间
					if(map.get("exi_exstarttime") != null && !"".equals(map.get("exi_exstarttime"))){
						exi_exstarttime = map.get("exi_exstarttime");
					} else if(StringUtil.hasText(exi_exstarttime1)){
						exi_exstarttime = exi_exstarttime1;
					}else{
						errStr+="考生<"+map.get("em_name")+">没有指定开始时间！<hr>";
					}
					
					//最迟取卷时间
					if(map.get("exi_exlatetime") != null && !"".equals(map.get("exi_exlatetime"))){
						exi_exlatetime = map.get("exi_exlatetime");
					} else if(StringUtil.hasText(exi_exlatetime1)){
						exi_exlatetime = exi_exlatetime1;
					}else{
						errStr+="考生<"+map.get("em_name")+">没有指定最迟取卷时间！<hr>";
					}
					
					//考试结束时间
					if(map.get("exi_exendtime") != null && !"".equals(map.get("exi_exendtime"))){
						exi_exendtime = map.get("exi_exendtime");
					} else if(StringUtil.hasText(exi_exendtime1)){
						exi_exendtime = exi_exendtime1;
					}else{
						errStr+="考生<"+map.get("em_name")+">没有指定结束时间！<hr>";
					}
					
					//exi_exstarttime > exi_exlatetime > exi_exendtime
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exlatetime)){
						//exi_exlatetime > exi_exstarttime
						if (!judgeTime(exi_exstarttime,exi_exlatetime)){
							errStr += "考生<"+map.get("em_name")+">最迟取卷时间需大于开始时间！<hr>";
						}
					}
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exendtime)){
						//exi_exendtime > exi_exstarttime
						if(!judgeTime(exi_exstarttime,exi_exendtime)){
							errStr += "考生<"+map.get("em_name")+">结束时间需大于开始时间！<hr>";
						}
					}
					if(StringUtil.hasText(exi_exstarttime)&&StringUtil.hasText(exi_exendtime)){
						//exi_exendtime > exi_exlatetime
						if(!judgeTime(exi_exstarttime,exi_exendtime)){
							errStr += "考生<"+map.get("em_name")+">考试结束时间需大于最迟取卷时间！<hr>";
						}
						if(!exi_exstarttime.toString().substring(0, 10).equals(exi_exendtime.toString().substring(0, 10))){
							//exi_exstarttime == exi_exendtime Y:M:S
							errStr += "考生<"+map.get("em_name")+">开始时间和结束时间必须在同一天！<hr>";
						}
					}
					if(errStr.length()>0){
						bool = false;
						errBuffer.append(errStr);
					}
					if(bool){
						names.append(map.get("em_id")+"#");
						schemes.append(exi_escode+"#");
						starttimes.append(exi_exstarttime+"#");
						latetimes.append(exi_exlatetime+"#");
						endtimes.append(exi_exendtime+"#");
					}
				}
			}
			if (errBuffer.length() > 0){
				BaseUtil.showError(errBuffer.toString());
			}
			String name = names.toString().substring(0, names.length()-1);
			String scheme = schemes.toString().substring(0, schemes.length()-1);
			String starttime = starttimes.toString().substring(0, starttimes.length()-1);
			String latetime = latetimes.toString().substring(0, latetimes.length()-1);
			String endtime = endtimes.toString().substring(0, endtimes.length()-1);
			String returnStr = baseDao.callProcedure("SP_CREATEEXAM", new Object[]{scheme,name,starttime,latetime,endtime,caller});
			if(!"good".equals(returnStr)){
				BaseUtil.showError(returnStr);
			}
		}
	}

	@Override
	public Map<String, Object> checkExam(int ex_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", baseDao
				.getFieldsDatasByCondition("examdetail ", new String[] {
						"exd_id", "exd_detno", "exd_eqcontent", "exd_eqtype",
						"exd_seleanswer","exd_stanswer","exd_stanscore" ,"exd_score","exd_answer"}, "exd_exid=" + ex_id
						+ " order by exd_detno"));
		Object[] data=baseDao.getFieldsDataByCondition("exam", 
				new String[]{"ex_starttime","ex_endtime","ex_recorder","nvl(ex_actscore,0)","nvl(ex_total,0)","nvl(ex_choicescore,0)","ex_judger","ex_judgetime","ex_hasjudged"}, "ex_id="+ex_id);
		modelMap.put("starttime", data[0]);
		modelMap.put("endtime", data[1]);
		modelMap.put("name", data[2]);
		modelMap.put("score", data[3]);
		modelMap.put("total", data[4]);
		modelMap.put("choicescore", data[5]);
		modelMap.put("judger", data[6]);
		modelMap.put("judgetime", data[7]);
		modelMap.put("hasjudged", data[8]);
		return modelMap;
	}
	@Override
	public void judgeExam(List<Map<Object, Object>> data) {
		if(data==null||data.size()==0){
			return;
		}
		Object ex_id = baseDao.getFieldDataByCondition("examdetail",
				"exd_exid", "exd_id=" + data.get(0).get("exd_id"));
		List<String>sqls=new ArrayList<String>();
		for(Map<Object,Object> map:data){
			String sql="update examdetail set exd_score="+map.get("score")+" where exd_id="+ map.get("exd_id");
			sqls.add(sql);
		}
		sqls.add("update exam set ex_actscore=(select sum(exd_score) from examdetail where exd_exid="+ex_id
				+"),ex_judger='"+SystemSession.getUser().getEm_name()+"',ex_judgetime=sysdate,ex_hasjudged='是' where ex_id="+ex_id);
		//是否及格
		sqls.add("update exam set ex_result=(case when (ex_actscore/EX_TOTAL)>=0.6 then '是' else '否' end) where ex_id="+ex_id);
		//反应到应聘人员信息表
		Object ex_exiid = baseDao.getFieldDataByCondition("exam",
				"ex_exiid", "ex_id=" + ex_id);
		
		if(!StringUtil.hasText(ex_exiid)){
			sqls.add("update recuitinfo set (re_examed,re_mark)=(select '是',ex_actscore from exam where ex_id="+ex_id
					+" and ex_recorder=re_name) where exists(select 1 from exam where ex_id="+ex_id+" and ex_recorder=re_name)");
		}
		
		baseDao.execute(sqls);
	}
	private boolean judgeTime(Object startTime ,Object endTime){
		Object bool = baseDao.getFieldDataByCondition("dual", "case when to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss') - to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss') > 0 then 'false' else 'true' end ", "1=1");
		return Boolean.valueOf(bool.toString());
	}
	
}
