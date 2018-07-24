package com.uas.mobile.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.uas.erp.core.XingePusher;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mobile.dao.AllProcessDao;
import com.uas.mobile.model.AllProcess;

@Component
@EnableAsync
@EnableScheduling
public class PusherTask {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private AllProcessDao allProcessDao;

	private static final Log logger = LogFactory.getLog("PushTask");

	static List<Master> masters = null;

	public void execute() {
		if (masters == null) {// 第一次循环操作
			masters = enterpriseService.getMasters();// 获取所有账套信息
		}
		if (masters != null) {
			for (Master master : masters) {
				String masterName = master.getMa_name();
				List<AllProcess> allProcesses = new ArrayList<AllProcess>();
				long lastTime = allProcessDao.getLastTimeByMaster(masterName);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 完整的时间

				long inttime = System.currentTimeMillis();
				Long timeStamp = date2TimeStamp(sdf.format(new Date()) + " 07:59:00", "yyyy-MM-dd HH:mm:ss");
				Long fromtime = date2TimeStamp(sdf.format(new Date()) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");

				if (inttime >= timeStamp) {
					allProcesses = allProcessDao.getAllProcessSinceTimeByMaster2(fromtime, masterName);

					if (!CollectionUtils.isEmpty(allProcesses)) {
						for (AllProcess allprocess : allProcesses) {
							setURL(allprocess);// 设置连接地址
							if (allprocess.getTypecode().equals("dingyue")) {
								pushSingleAccount(allprocess, master);// 把订阅消息推送给的接收者
							}
						}
					}
				}

				if (master.getMa_selectTime() != 0 && master.getMa_selectTime() < lastTime) {// 不是第一次执行循环操作
					// 根据上一次的最大时间获取新的待办事宜（新的需要推送）
					allProcesses = allProcessDao.getAllProcessSinceTimeByMaster(master.getMa_selectTime(), masterName);
					if (!CollectionUtils.isEmpty(allProcesses)) {
						for (AllProcess allprocess : allProcesses) {
							setURL(allprocess);// 设置连接地址
							if (!allprocess.getTypecode().equals("dingyue")) {
								pushSingleAccount(allprocess, master);// 把每个待办事宜推送给对应的接收者
							}
						}
					}
				}
				// 获取本次的最大时间，用于下次获取新的待办事宜
				master.setMa_selectTime(lastTime);

			}
		}
	}

	/**
	 * 推送一条待办事宜给单个账号
	 * 
	 * @return
	 */
	protected void pushSingleAccount(AllProcess allProcess, Master master) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
		String masterName = allProcess.getMaster();
		String recorderId = allProcess.getRecorderid();
		String dealPersonCode = allProcess.getDealpersoncode();		
		String content = "发起人:" + allProcess.getRecorder() + "，发起时间:" + timeFormat.format(allProcess.getDatetime());// 推送信息第二行
		String tittle = "";
		if (allProcess.getTypecode().equals("dingyue")) {
			tittle = "您有新的订阅消息" + " : " + allProcess.getMainname();// 推送信息标题);
		}else if(allProcess.getTypecode().equals("pagingrelease")){
			tittle = "您有新的推送" + " : " + allProcess.getMainname()+"("+allProcess.getTaskname()+")";// 推送信息标题
			content=content+"...";
		}else if(allProcess.getTypecode().equals("billtask")||allProcess.getTypecode().equals("communicatetask")||allProcess.getTypecode().equals("projecttask")||allProcess.getTypecode().equals("worktask")){
			tittle = "您有一个新的待办任务，请及时处理!";// 推送信息标题
		}else {
			tittle = allProcess.getStatus() + " : " + allProcess.getMainname();// 推送信息标题);
		}
		String account = null;
		String imid = null;
		if (allProcess.getStatus().equals("未通过")) {// 未通过的待办事宜，推送给发起者
			account = getEmMobileByMasterAndEmcode(masterName, recorderId);
		} else {// 其他的推送给处理者
			account = getEmMobileByMasterAndEmcode(masterName, dealPersonCode);
		}
		// =====================================百度推送
		if (allProcess.getStatus().equals("未通过")) {// 未通过的待办事宜，推送给发起者
			imid = getEmMobileByMasterAndImid(masterName, recorderId);
		} else {// 其他的推送给处理者
			imid = getEmMobileByMasterAndImid(masterName, dealPersonCode);
		}
		// ======================================
		Long enUU = master.getMa_uu();
		Long masterId = master.getMa_manageid();
		if (StringUtils.hasText(account)) {
			JSONObject[] result = XingePusher.pushByAccount(masterName, account, tittle, content, String.valueOf(enUU),
					String.valueOf(masterId), allProcess.getLink(), "");
			/*logger.info(masterName + "#" + account + "#" + tittle + "#" + content + "#" + enUU + "#" + masterId + "#"
					+ allProcess.getLink() + "#" + "A:" + result[0].toString() + "#I:" + result[1].toString());*/
		}
		// ================================百度推送
		try {
			if (null != imid) {
				JSONObject[] resultBaidu = XingePusher.pushByAccountBaidu(masterName, imid, tittle, content, String.valueOf(enUU),
						String.valueOf(masterId), allProcess.getLink(), allProcess.getType());
				try {
					if (allProcess.getTypecode().equals("dingyue")) {
						// 更新状态
						String updateSQL = "update " + masterName + ".SUBS_MAN_INSTANCE set ispush_=1 where id_="
								+ allProcess.getCodevalue() + "";
						baseDao.execute(updateSQL);
					}
					if (allProcess.getTypecode().equals("pagingrelease")) {
						// 更新状态
						baseDao.execute("update ICQHISTORYdetail set ihd_status=-1  where IHD_RECEIVEID=(select em_id from employee where em_code='"+allProcess.getDealpersoncode()+"') and "
								+ "ihd_ihid in (select ih_id from ICQHISTORY where ih_prid="+allProcess.getId()+")");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setURL(AllProcess allProcess) {
		String url = "";
		String typeCode = allProcess.getTypecode();
		if (typeCode.equals("worktask") || typeCode.equals("projecttask")) {
			url = "jsps/mobile/task.jsp?caller=WorkRecord&id=" + allProcess.getId();
		} else if (typeCode.equals("billtask") || typeCode.equals("mrptask") || typeCode.equals("kbitask")) {
			url = "jsps/mobile/task.jsp?caller=ResourceAssignment!Bill&id=" + allProcess.getId();
		} else if (typeCode.equals("communicatetask")) {
			url = "jsps/common/JprocessCommunicate.jsp?whoami=ResourceAssignment!Bill&formCondition=id=" + allProcess.getId()
					+ "&gridCondition=null=" + allProcess.getId() + "&_noc=1";
		} else if (typeCode.equals("process") || typeCode.equals("unprocess") || typeCode.equals("transferprocess")) {
			url = "jsps/mobile/process.jsp?nodeId=" + allProcess.getTaskid();
		} else if (typeCode.equals("procand")) {
			url = "jsps/mobile/jprocand.jsp?nodeId=" + allProcess.getTaskid();
		} else if ("pagingrelease".equals(typeCode)) {
			url="";
			//url = "jsps/mobile/commonForm/commonForm.jsp?caller=" + allProcess.getCaller() + "&" + allProcess.getLink() + "&_readOnly=true";
		} else if (typeCode.equals("dingyue")) {
			url = "common/charts/mobileCharts.action?numId=" + allProcess.getTaskid() + "&mainId=" + allProcess.getId() + "&insId="
					+ allProcess.getCodevalue() + "&title=" + allProcess.getTaskname() + "";
		} else {
			url = "jsps/common/jtaketask.jsp?whoami=JProCand&formCondition=jp_nodeId=" + allProcess.getTaskid() + "&gridCondition=1="
					+ allProcess.getTaskid() + "&_noc=1";
		}
		// 将url中所有的&替换为%26为防止url作为参数传递时&被识别
		url = url.replaceAll("&", "%26");
		allProcess.setLink(url);
	}

	/**
	 * 根据账套获取用户手机号
	 * 
	 * @param master
	 * @param em_code
	 * @return
	 */
	private String getEmMobileByMasterAndEmcode(String master, String em_code) {
		String sql = "select max(em_mobile) from " + master + ".employee where em_code='" + em_code + "'";
		String mobile = baseDao.getJdbcTemplate().queryForObject(sql, String.class);
		return mobile;
	}

	/**
	 * 根据账套获取用户imid
	 * 
	 * @param master
	 * @param em_code
	 * @return
	 */
	private String getEmMobileByMasterAndImid(String master, String em_code) {
		String sql = "select max(nvl(em_imid,0)) from " + master + ".employee where em_code=?";
		String imid = baseDao.getJdbcTemplate().queryForObject(sql, String.class, em_code);
		if (StringUtils.isEmpty(imid) || "0".equals(imid)) {
			return null;
		}
		return imid;
	}

	public static Long date2TimeStamp(String date_str, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(date_str).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
