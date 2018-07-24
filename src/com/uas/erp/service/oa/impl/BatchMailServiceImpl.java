package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.GroupTree;
import com.uas.erp.service.oa.BatchMailService;
import com.uas.erp.service.oa.SendMailService;

@Service
public class BatchMailServiceImpl implements BatchMailService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SendMailService sendMailService;
	
	/*public List<CheckTree> getHrOrgTree(String language) {

		List<CheckTree> tree = new ArrayList<CheckTree>();
		List<HROrg> hrOrgs = hrOrgStrDao.getAllHrOrgs(null);
		CheckTree checkTree = new CheckTree();
		for (HROrg hrOrg : hrOrgs) {
			if (hrOrg.getOr_subof() == 0) {
				checkTree = recursionCheckTree(hrOrgs, hrOrg);
				tree.add(checkTree);
			}
		}
		return tree;
	}*/

	/* (non-Javadoc)
	 * @see com.uas.erp.service.oa.impl.BatchMailService#createDir(java.lang.String, java.lang.String)
	 */
	@Override
	public String createDir(String ids, String folderName){
		int EM_ID = SystemSession.getUser().getEm_id();
		String countSql = "select count(1) from COMMUNICATIONGROUP where cg_group = '"+folderName+"' and cg_authorid = " + EM_ID; 
		int count = baseDao.getCount(countSql);
		if(count > 0){
			return "添加失败,输入的个人通讯组名已存在!";
		}else{
			String sql = "insert into COMMUNICATIONGROUP (select COMMUNICATIONGROUP_SEQ.nextval,em_name,'"+EM_ID+"',em_email,'"+folderName+"',em_id from employee"
					+ " where em_id in ("+ids+") )";
			baseDao.execute(sql);
			return "添加成功!";
		}
		
	}
	/**
	 * 获取个人通讯组组名
	 * @return
	 */
	public Map<String, Object> getGroups(){
		int EM_ID = SystemSession.getUser().getEm_id();
		String sql = "select wm_concat(cg_group) groups from (select cg_group from COMMUNICATIONGROUP where cg_authorid = "+EM_ID+" group by cg_group)";
		return baseDao.getJdbcTemplate().queryForMap(sql);
	}
	/**
	 * 添加节点至指定个人通讯组
	 * @param ids
	 * @param groupName
	 * @return
	 */
	public String addToGroup(String ids, String groupName){
		int EM_ID = SystemSession.getUser().getEm_id();
		try {
			//去掉表中已存在的id
			List<String> idsList  = new ArrayList<String>(Arrays.asList(ids.split(",")));
			String sql = "select cg_emid from communicationgroup where cg_group = '"+groupName+"' and cg_authorid = " + EM_ID + "and cg_emid in ("+ids+")";
			List<String> oldIdsList = new ArrayList<String>(baseDao.queryForList(sql, String.class));
			idsList.removeAll(oldIdsList);
			if(idsList != null && idsList.size() != 0){
				String id = idsList.toString().substring(1, idsList.toString().length()-1);
				//插入
				String insertSql = "insert into COMMUNICATIONGROUP (select COMMUNICATIONGROUP_SEQ.nextval,em_name,'"+EM_ID+"',em_email,'"+groupName+"',em_id from employee"
						+ " where em_id in ("+id+") )";
				baseDao.execute(insertSql);
			}
			return "添加成功!";
		} catch (Exception e) {
			return "添加失败!";
		}
	}
	
	/**
	 * 获取个人通讯组
	 */
	public List<GroupTree> getGroupsTree(){
		int EM_ID = SystemSession.getUser().getEm_id();
		String sql = "select cg_group as \"text\",cg_group from communicationgroup where cg_authorid = "+EM_ID+" group by cg_group";
		return baseDao.query(sql, GroupTree.class);
	}
	
	/**
	 * 获取通讯组下的人员信息
	 * @param groupName
	 * @return
	 */
	public List<GroupTree> getPersonByGroupName(String groupName){
		int EM_ID = SystemSession.getUser().getEm_id();
		String sql = "select cg_id,cg_name as \"text\",cg_email,cg_group,cg_emid from communicationgroup where cg_authorid = " + EM_ID + " and cg_group = '" + groupName + "'";
		return baseDao.query(sql, GroupTree.class);
	}
	
	/**
	 * 通讯组新增自定义人员信息
	 * @param name
	 * @param email
	 * @param group
	 * @return
	 */
	public List<GroupTree> addPersonToGroup(String name, String email, String group){
		int EM_ID = SystemSession.getUser().getEm_id();
		String countSql = "select count(1) from communicationgroup where cg_name = '"+name+"' and cg_group = '"+group+"' and cg_email = '"+email+"' and cg_authorid = "+ EM_ID;
		int count = baseDao.getCount(countSql);
		if(count == 0){
			String sql = "insert into communicationgroup values(COMMUNICATIONGROUP_SEQ.nextval,?,?,?,?,null)";
			baseDao.execute(sql, name, EM_ID, email, group);
			String querySql = "select cg_name as \"text\",cg_email,cg_group,cg_emid from communicationgroup where cg_name='"+name+"'"
					+ " and cg_email='"+email+"' and cg_group='"+group+"' and cg_authorid="+EM_ID;
			return baseDao.query(querySql, GroupTree.class);
		}
		return null;
	}
	
	/**
	 * 更新个人通讯组名
	 */
	public String updateGroupName(String name, String group){
		baseDao.updateByCondition("COMMUNICATIONGROUP", "cg_group='"+name+"'", "cg_group='"+group+"'");
		return "保存成功!";
	}
	
	/**
	 * 删除个人通讯组
	 */
	public void deleteGroup(String group){
		baseDao.deleteByCondition("COMMUNICATIONGROUP", "cg_group='"+group+"'");
	}
	
	/**
	 * 更新个人通讯组下的人员信息
	 */
	public String updatePersonInfo(String name, String email, String cg_id){
		baseDao.updateByCondition("COMMUNICATIONGROUP", "cg_name='"+name+"',cg_email='"+email+"'", "cg_id="+cg_id+" ");
		return "更新成功!";
	}
	
	/**
	 * 删除个人通讯组下的人员
	 */
	public void deletePerson(String cgId){
		baseDao.deleteByCondition("COMMUNICATIONGROUP", "cg_id="+cgId);
	}
	
	/**
	 * 实时搜索，用于显示邮件提示框
	 * @param value
	 * @return
	 */
	public List<Map<String, Object>> searchReciveman(String value, String type){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(!value.contains("@")){
			String sql = "select em_name as \"name\",em_email as \"email\" from employee where em_email is not null and (em_email like '"+value+"%@%.com' or em_name like '"+value+"%') "
					+ "union select cg_name,cg_email from communicationgroup where cg_email is not null and (cg_email like '"+value+"%@%.com' or cg_name like '"+value+"%')";
			list.addAll(baseDao.queryForList(sql));
		}
		if(type != null && !"".equals(type)){
			if(type.contains("1")){	//客户
				String customerSql = "SELECT CONCAT(CU_CODE,'('||CU_NAME||')') as \"name\",CU_EMAIL as \"email\" FROM CUSTOMER "
						+ " WHERE CU_EMAIL IS NOT NULL AND CU_CODE LIKE '"+value+"%' AND REGEXP_SUBSTR(CU_EMAIL,'^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$',1,1,'i') IS NOT NULL "
						+ " UNION ALL "
						+ " select CONCAT(CU_CODE,'('||CU_NAME||')') NAME,CT_PERSONEMAIL EMAIL  from contact LEFT JOIN CUSTOMER ON CT_CUID = CU_ID "
						+ " WHERE CT_PERSONEMAIL IS NOT NULL AND CU_CODE LIKE '"+value+"%' AND REGEXP_SUBSTR(CT_PERSONEMAIL,'^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$',1,1,'i') is not null";
				list.addAll(baseDao.queryForList(customerSql));
			}
			if(type.contains("2")){ // 供应商
				String vendorSql = "SELECT CONCAT(VE_CODE,'('||VE_NAME||')') as \"name\",VE_EMAIL as \"email\" FROM VENDOR "
						+ "WHERE VE_EMAIL IS NOT NULL AND VE_CODE LIKE '"+value+"%' AND REGEXP_SUBSTR(VE_EMAIL,'^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$',1,1,'i') IS NOT NULL";
				list.addAll(baseDao.queryForList(vendorSql));
			}
		}
		return list;
	}
	
	/**
	 * 发送邮件
	 */
	public Map<String, Object> send(final String recivemen,final String subject,final String content,final String files){
		Map<String, Object> map = new HashMap<String, Object>();
		final int id = baseDao.getSeqId("EMAILLOG_SEQ");
		String em_name = SystemSession.getUser().getEm_name();
		baseDao.execute("INSERT INTO EMAILLOG(EL_ID,EL_RECIVEMAN,EL_HANDLER,EL_HANDLERDATE,EL_RESULT,EL_MESSAGE,EL_EMAILSUBJECT) "
				+ "VALUES("+id+",'"+recivemen+"','"+em_name+"',sysdate,'发送中',null,'"+subject+"')");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sendMailService.sendSysMail(subject, content, recivemen, files);
					//发送成功
					baseDao.execute("update emaillog set el_result = '发送成功' where el_id = " + id);
				} catch (Exception e) {
					//发送失败
					if(e.getMessage().length() > 150){
						baseDao.execute("update emaillog set el_result = '发送失败', el_message='"+e.getMessage().substring(0,150)+"' where el_id="+id);
					}else{
						baseDao.execute("update emaillog set el_result = '发送失败', el_message='"+e.getMessage()+"' where el_id="+id);
					}
				}
			}
		}).start();
		map.put("success", true);
		return map;
	}
	
}
