package com.uas.erp.service.common.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.CustomFlowDetailComparator;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JTask;
import com.uas.erp.model.JnodeRelation;

@Service
public class JbpmxmlService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;

	/**
	 * 获得 xml 中所有的 candidate-groups
	 */
	@SuppressWarnings("unchecked")
	// 该方法没用到……
	public List<String> getCandidateGroupsOfXml(String str) {
		if (!str.contains("candidate-groups")) {
			return null;
		} else {
			List<String> groups = null;
			SAXReader saxReader = new SAXReader();
			try {
				groups = new LinkedList<String>();
				Document doc = saxReader.read(new ByteArrayInputStream(str.getBytes()));
				Element root = doc.getRootElement();
				/* root.elements("task"); */
				List<Element> list = root.elements("task");
				for (Element element : list) {
					Attribute att = element.attribute("candidate-groups");
					if (!groups.contains(att.getStringValue())) {
						groups.add(att.getStringValue());
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
				throw new RuntimeException("xml文件格式不正确!");

			}
			return groups;
		}
	}

	/**
	 * 获取导航图节点信息
	 */
	public List<Map<String, String>> getCustomListOfXml(String str) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Document doc;
		try {
			SAXReader saxReader = new SAXReader();
			doc = saxReader.read(new ByteArrayInputStream(str.getBytes("UTF-8")));
			Element root = doc.getRootElement();
			/* root.elements("task"); */
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements("custom");
			for (Element element : list) {
				Map<String, String> map = new HashMap<String, String>();
				Attribute name = element.attribute("name");
				@SuppressWarnings("unchecked")
				List<Element> participants = element.elements("participant");
				for (Element el : participants) {
					Attribute partname = el.attribute("name");
					Attribute partID = el.attribute("id");
					Attribute type = el.attribute("type");
					if (name != null) {
						map.put("sysname", partname.getStringValue());
						map.put("name", name.getStringValue());
						map.put("id", partID.getStringValue());
						map.put("url", type.getStringValue());
						maps.add(map);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("xml文件格式不正确!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return maps;
	}

	/** 获得任务信息列表 **/
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getTaskListOfXml(String str) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<JTask> tasks = null;
		List<JnodeRelation> relations = null;
		JTask task = null;
		JnodeRelation relation = null;
		Document doc;
		try {
			tasks = new LinkedList<JTask>();
			relations = new LinkedList<JnodeRelation>();
			SAXReader saxReader = new SAXReader();
			doc = saxReader.read(new ByteArrayInputStream(str.getBytes("UTF-8")));
			Element root = doc.getRootElement();
			List<Element> list = root.elements("task");
			List<Element> forklist = root.elements("fork");
			List<Element> joinlist = root.elements("join");
			List<Element> decisionlist = root.elements("decision");
			List<Element> sqlList = root.elements("sql");
			Element startelement = root.element("start");
			relation = new JnodeRelation();
			relation.setJr_name("start");
			relation.setJr_to(startelement.element("transition").attribute("to").getStringValue());
			relation.setJr_type("start");
			relations.add(relation);
			for (Element element : list) {
				task = new JTask();
				relation = new JnodeRelation();
				Attribute name = element.attribute("name");
				task.setJt_name(name.getStringValue());
				Attribute assignee = element.attribute("assignee");
				if (assignee != null) {
					task.setJt_assignee(assignee.getStringValue());
				}
				Attribute sqlAssignee = element.attribute("sqlAssignee");
				if (sqlAssignee != null) {
					task.setJt_assignee(sqlAssignee.getStringValue());
				}
				Attribute jobs = element.attribute("candidate-groups");
				if (jobs != null) {
					task.setJt_jobs(jobs.getStringValue());
				}
				Attribute departjobs = element.attribute("departjob-groups");
				if (departjobs != null) {
					task.setJt_jobs(departjobs.getStringValue());
					task.setJt_isDepartjob(1);
				}
				Attribute roles = element.attribute("rolAssignee");
				if (roles != null) {
					task.setJt_roles(roles.getStringValue());
				}
				Attribute customerSetup = element.attribute("customSetup");
				if (customerSetup != null) {
					task.setJt_customSetup(customerSetup.getStringValue());
				}
				Attribute notifygroup = element.attribute("notifyGroups");
				if (notifygroup != null) {
					task.setJt_notifygroup(notifygroup.getStringValue());
				}
				Attribute notifypeople = element.attribute("notifyPeople");
				if (notifypeople != null) {
					task.setJt_notifypeople(notifypeople.getStringValue());
				}
				Attribute notifySql = element.attribute("notifySql");
				if (notifySql != null) {
					task.setJt_notifysql(notifySql.getStringValue());
				}
				// 以上都是属性，下面是子节点;
				Attribute button = element.attribute("specialbutton");
				if (button != null) {
					task.setJt_button(button.getStringValue());
				}
				Attribute neccessaryField = element.attribute("neccessaryField");
				if (neccessaryField != null) {
					task.setJt_neccessaryfield(neccessaryField.getStringValue());
				}
				Attribute approve = element.attribute("approve");
				if (approve != null) {
					task.setJt_isApprove(Integer.parseInt(approve.getStringValue()));
				}				
				Attribute smsalert = element.attribute("smsalert");
				if (smsalert != null) {
					task.setJt_smsalert(Integer.parseInt(smsalert.getStringValue()));
				}
				Attribute extra = element.attribute("extra");
				if (extra != null) {
					relation.setJr_canextra(extra.getStringValue());
				}
				Attribute sendmsg = element.attribute("sendmsg");
				if (sendmsg != null) {
					task.setJt_sendMsg(Integer.parseInt(sendmsg.getStringValue()));
				}
				Attribute before = element.attribute("exebefore");
				if (before != null) {
					task.setJt_before(before.getStringValue());
				}
				Attribute after = element.attribute("exeafter");
				if (after != null) {
					task.setJt_after(after.getStringValue());
				}
				Attribute ruleid = element.attribute("jprocessRuleId");
				if (ruleid != null) {
					task.setJt_ruleid(ruleid.getStringValue());
				}
				/*List<Element> reminders = element.elements("reminder");
				if (reminders.size() > 0) {
					Element reminder = reminders.get(0);
					int duedate = Integer.parseInt(reminder.attribute("duedate").getStringValue()); // 假定前端都填了;
					int repeat = Integer.parseInt(reminder.attribute("repeat").getStringValue());
					task.setJt_duedate(duedate);
					task.setJt_repeat(repeat);
				} else {
					task.setJt_duedate(0);
					task.setJt_repeat(0);
				}*/
				Attribute duration = element.attribute("duration");
				if (duration != null) {
					task.setJt_duedate(Integer.parseInt(duration.getStringValue()));
				}else task.setJt_duedate(8);
				// 节点层级关系
				relation.setJr_name(name.getStringValue());
				String codestr = "";
				String namestr = "";
				if (jobs != null) {
					List<Employee> employees = employeeDao.getEmployeesByJobs(jobs.getStringValue().split(","));
					for (Employee em : employees) {
						codestr += em.getEm_code() + ",";
						namestr += em.getEm_name() + ",";
					}
				} else if (assignee != null) {
					String[] codearr = assignee.getStringValue().split(",");
					for (String a : codearr) {
						Employee em = employeeDao.getEmployeeByEmCode(a);
						if (em != null) {
							codestr += em.getEm_code() + ",";
							namestr += em.getEm_name() + ",";
						} else {
							// BaseUtil.showErrorOnSuccess("节点:"+name.getStringValue()+"处理人"+a+" 可能不存在请重新设置!");
						}
					}
				}
				if (!codestr.equals("")) {
					relation.setJr_nodedealman(codestr.substring(0, codestr.length() - 1));
					relation.setJr_nodedealmanname(namestr.substring(0, namestr.length() - 1));
				}
				List<Element> transitions = element.elements("transition");
				for (Element el : transitions) {
					Attribute transtionname = el.attribute("name");
					if (transtionname.getStringValue().equals("同意")) {
						relation.setJr_to(el.attribute("to").getStringValue());
					}
				}
				relation.setJr_type("task");
				relations.add(relation);
				tasks.add(task);
			}
			for (Element element : forklist) {
				List<Element> transitions = element.elements("transition");
				String tostr = "";
				relation = new JnodeRelation();
				for (Element el : transitions) {
					tostr += el.attribute("to").getStringValue() + ",";
				}
				relation.setJr_name(element.attribute("name").getStringValue());
				relation.setJr_to(tostr.substring(0, tostr.length() - 1));
				relation.setJr_type("fork");
				relations.add(relation);
			}
			for (Element element : joinlist) {
				relation = new JnodeRelation();
				List<Element> transitions = element.elements("transition");
				relation.setJr_name(element.attribute("name").getStringValue());
				relation.setJr_to(transitions.get(0).attribute("to").getStringValue());
				relation.setJr_type("join");
				relations.add(relation);
			}
			for (Element element : decisionlist) {
				// 分析条件分支
				relation = new JnodeRelation();
				relation.setJr_name(element.attribute("name").getStringValue());
				List<Element> transitions = element.elements("transition");
				String tostr = "";
				String conditionstr = "";
				for (Element transition : transitions) {
					tostr += transition.attribute("to").getStringValue() + ",";
					Element conel = transition.element("condition");
					conditionstr += conel.attribute("expr").getStringValue() + ",";
				}
				relation.setJr_to(tostr.substring(0, tostr.length() - 1));
				relation.setJr_condition(conditionstr.substring(0, conditionstr.length() - 1));
				relation.setJr_type("decision");
				relations.add(relation);
			}
			for (Element element : sqlList) {
				relation = new JnodeRelation();
				relation.setJr_name(element.attribute("name").getStringValue());
				Element sql = element.element("query");
				Element parameters = element.element("parameters");
				Element object =parameters!=null? parameters.element("object"):null;
				String name = object!=null?object.attributeValue("name"):"";
				String expr = object!=null?object.attributeValue("expr"):"";
				relation.setJr_to(element.element("transition").attributeValue("to"));
				relation.setJr_type("sql");
				relation.setJr_condition(object!=null?name + "|" + expr:null);
				relation.setJr_nodedealman(sql.getStringValue());
				relations.add(relation);

			}

		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("xml文件格式不正确!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		map.put("tasks", tasks);
		map.put("relation", relations);
		return map;
	}

	@SuppressWarnings("unchecked")
	public String setReminderOfXml(String str) throws Exception {
		if (!str.contains("reminder")) {
			return str;
		} else {
			Document doc = null;
			SAXReader saxReader = new SAXReader();
			try {
				doc = saxReader.read(new ByteArrayInputStream(str.getBytes("UTF-8")));
				Element root = doc.getRootElement();
				List<Element> list = root.elements("task");
				for (Element el : list) {
					Element element = el.element("reminder");
					Attribute att = element.attribute("duedate");
					if (att != null) {
						String dudate = att.getValue();
						if (Integer.parseInt(dudate) > 1) {
							att.setValue(dudate + " business hours");
						} else {
							att.setValue(dudate + " business hours");
						}
					}
					Attribute attr = element.attribute("repeat");
					if (attr != null) {
						String repeat = attr.getValue();
						if (Integer.parseInt(repeat) > 1) {
							attr.setValue(repeat + " business hours");
						} else {
							attr.setValue(repeat + " business hours");
						}

					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
				throw new RuntimeException("xml文件格式不正确!");

			}
			return doc.asXML();
		}
	}

	@SuppressWarnings("unchecked")
	public String clearCustomerSetupOfTasks(String xml) {
		if (!xml.contains("customSetup")) {
			return xml;
		} else {
			Document doc = null;
			SAXReader saxReader = new SAXReader();

			try {
				doc = saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
				Element root = doc.getRootElement();

				List<Element> tasks = root.elements("task");
				for (Element task : tasks) {
					Attribute a = task.attribute("customSetup");
					if (a != null) {
						task.remove(a);
					}
				}

			} catch (DocumentException e) {
				e.printStackTrace();
				throw new RuntimeException("xml文件格式不正确!");

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return doc.asXML();
		}

	}

	@SuppressWarnings("unchecked")
	public String analyzeAssigneeOfTasks(String xml) {

		Document doc = null;
		SAXReader saxReader = new SAXReader();

		try {
			doc = saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			Element root = doc.getRootElement();
			Attribute name = root.attribute("name");
			if (name != null) {
				name.setValue(name.getStringValue() + "_" + SystemSession.getUser().getEm_master());
			}
			List<Element> tasks = root.elements("task");
			for (Element task : tasks) {
				Attribute a = task.attribute("assignee");
				if (a != null) {
					String aValue = a.getStringValue();
					if (aValue.contains(",")) {
						task.addAttribute("candidate-users", aValue);
						task.remove(a);

					}

				}
				Attribute att = task.attribute("sqlAssignee");
				if (att != null) {
					String aValue = att.getStringValue();
					task.addAttribute("assignee", aValue);
					task.remove(att);
				}
				Attribute att1 = task.attribute("rolAssignee");
				if (att1 != null) {
					String aValue = att1.getStringValue();
					task.addAttribute("assignee", aValue);
					task.remove(att1);
				}
				Attribute att2 = task.attribute("departjob-groups");//新增属性部门->岗位
				if (att2 != null) {
					String aValue = att2.getStringValue();
					task.addAttribute("candidate-groups", aValue);
					task.remove(att2);
				}
			}

		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("xml文件格式不正确!");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return doc.asXML();

	}

	public String getXmlByCustomFlow(String formStore, List<Map<Object, Object>> gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("process");
		root.addAttribute("name", store.get("cf_name").toString());
		doc.setRootElement(root);
		Element start = DocumentHelper.createElement("start");
		start.addAttribute("name", "start 1");
		Element transition = DocumentHelper.createElement("transition");
		transition.addAttribute("to", "审批步骤1");
		start.add(transition);
		root.add(start);
		if (!store.get("cf_remark").toString().equals("null") && store.get("cf_remark").toString().equals(" ")) {
			Element des = DocumentHelper.createElement("description");
			des.setText(store.get("cf_remark").toString());
			root.add(des);
		}
		Collections.sort(gridStore, new CustomFlowDetailComparator());
		for (int i = 0; i < gridStore.size() - 1; i++) {
			Element task = DocumentHelper.createElement("task");
			task.addAttribute("name", gridStore.get(i).get("cfd_code").toString());
			if (gridStore.get(i).get("cfd_code").toString().contains(",")) { // 分配了多个人;
				task.addAttribute("candidate-users", gridStore.get(i).get("cfd_actorUsers").toString());
			} else {
				task.addAttribute("assignee", gridStore.get(i).get("cfd_actorUsers").toString());
			}
			Element transition1 = DocumentHelper.createElement("transition");
			transition1.addAttribute("name", "同意");
			transition1.addAttribute("to", gridStore.get(i + 1).get("cfd_code").toString());
			task.add(transition1);
			Element transition2 = DocumentHelper.createElement("transition");
			transition2.addAttribute("name", "不同意");
			transition2.addAttribute("to", "end-cancel");
			task.add(transition2);
			root.add(task);
		}
		Element task = DocumentHelper.createElement("task");
		task.addAttribute("name", gridStore.get(gridStore.size() - 1).get("cfd_code").toString());
		Element transition1 = DocumentHelper.createElement("transition");
		transition1.addAttribute("name", "同意");
		transition1.addAttribute("to", "end");
		task.add(transition1);
		if (gridStore.get(gridStore.size() - 1).get("cfd_code").toString().contains(",")) { // 分配了多个人;
			task.addAttribute("candidate-users", gridStore.get(gridStore.size() - 1).get("cfd_actorUsers").toString());
		} else {
			task.addAttribute("assignee", gridStore.get(gridStore.size() - 1).get("cfd_actorUsers").toString());
		}
		Element transition2 = DocumentHelper.createElement("transition");
		transition2.addAttribute("name", "不同意");
		transition2.addAttribute("to", "end-cancel");
		task.add(transition2);
		root.add(task);
		Element cancel = DocumentHelper.createElement("end-cancel");
		cancel.addAttribute("name", "end-cancel");
		root.add(cancel);
		Element end = DocumentHelper.createElement("end");
		end.addAttribute("name", "end");
		root.add(end);
		return doc.asXML();
	}
}
