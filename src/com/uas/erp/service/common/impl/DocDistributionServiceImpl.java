package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.common.DocDistributionService;
import com.uas.erp.service.oa.DocumentListService;
import com.uas.erp.service.oa.SendMailService;

@Service
public class DocDistributionServiceImpl implements DocDistributionService {

	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private DocumentListService documentListService;
	
	/**
	 * 获取文件信息树
	 */
	public List<Map<String, Object>>  getProjectFileTree(String condition,int id,String checked) {
		if(condition==null||"".equals(condition)){
			condition = "1=1";
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("dl_id",id);
		List<Map<String, Object>>  rootchild = getChildrenNodes(root, condition, checked);
		return rootchild;		
	}
	
	List<Map<String, Object>> getChildrenNodes(Map<String, Object> parentNode,String condition,String checked){
		
		SqlRowList rs = baseDao.queryForRowSet("select * from documentlist where "+condition +" and dl_parentid ="+parentNode.get("dl_id")+" and dl_statuscode = 'AUDITED' order by dl_detno asc");
		List<Map<String, Object>>  nodes = new ArrayList<Map<String, Object>> ();
		while(rs.next()){
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("dl_id",rs.getInt("dl_id"));
			node.put("dl_name",rs.getString("dl_name"));	
			node.put("dl_filepath",rs.getString("dl_filepath"));
			node.put("dl_creator",rs.getString("dl_creator"));
			node.put("dl_createtime",rs.getDate("dl_createtime")==null?"":DateUtil.format(rs.getDate("dl_createtime"), Constant.YMD_HMS));
			node.put("id",rs.getInt("dl_id"));
			if (rs.getInt("dl_kind")==0) {
				if(checked!=null) node.put("checked",false);
				node.put("leaf",true);
			}else {
				node.put("leaf",false);
				node.put("expanded", false);
				node.put("children", getChildrenNodes(node, condition,checked));
			}
			nodes.add(node);	
		}	
				
		return nodes;
	}

	/**
	 * 获取文件详细信息
	 */
	@Override
	public List<Map<String, Object>> getFileInfo(int[] ids) {
		if(ids != null && ids.length > 0 ){
			//去除数组等号两边的[]
			String id = Arrays.toString(ids);
			String codes = id.substring(1, id.length()-1);
			String sql = "select dl_code as \"sed_code\",substr(dl_name,0,(select instr(dl_name,'.',1,1) from dual)-1) as \"sed_name\",round(dl_size/1024,2) as \"sed_size\","
					+ "substr(dl_virtualpath,(select instr(dl_virtualpath,'/',1,2) from dual)) as \"sed_menu\","
					+ "dl_createtime as \"sed_uploaddate\",dl_creator as \"sed_uploadman\" from documentlist where dl_id in (" + codes + ")";
			return baseDao.queryForList(sql);
		}
		return null;
	}
	
	/**
	 * 保存
	 * @param formStore
	 * @param gridStore
	 * @param caller
	 */
	public void saveDocDistribution(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("sendemail", "se_code='" + store.get("se_code") + "'");
		if (!bool) {
			BaseUtil.showError("当前编号的记录已经存在,不能新增!");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存sendemail
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "sendemail", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存sendemaildetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "sendemaildetail", "sed_id");
		for (Map<Object, Object> map : grid) {
			Object uddId = map.get("sed_id");
			if (uddId == null || uddId.equals("") || uddId.equals("0") || Integer.parseInt(uddId.toString()) == 0) {// 新添加的数据，id不存在
				baseDao.execute(SqlUtil.getInsertSql(map, "sendemaildetail", "sed_id"));
			}
		}
		baseDao.execute(gridSql);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		
	}
	
	/**
	 * 删除
	 * @param seId
	 * @param caller
	 */
	public void deleteDocDistribution(int seId, String caller) {
		// 只能删除在录入!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + seId);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { seId });
		baseDao.delCheck("sendemail", seId);
		// 删除sendemaildetail
		String deleteSql = "delete from sendemaildetail where sed_seid = '" +seId + "'";
		baseDao.execute(deleteSql);
		// 删除sendemail
		baseDao.deleteById("sendemail", "se_id", seId);
		// 记录操作
		baseDao.logger.delete(caller, "se_id", seId);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { seId });
	}
	
	/**
	 * 更新
	 * @param formStore
	 * @param gridStore
	 * @param caller
	 */
	public void updateDocDistribution(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + store.get("se_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改sendemail
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "sendemail", "se_id");
		baseDao.execute(formSql);
		
		// 修改sendemaildetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "sendemaildetail", "sed_id");
		for (Map<Object, Object> map : gstore) {
			Object uddId = map.get("udd_id");
			if (uddId == null || uddId.equals("") || uddId.equals("0") || Integer.parseInt(uddId.toString()) == 0) {// 新添加的数据，id不存在
				baseDao.execute(SqlUtil.getInsertSql(map, "sendemaildetail", "sed_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "se_id", store.get("se_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		
	}
	
	/**
	 * 提交
	 * @param seId
	 * @param caller
	 */
	public void submitDocDistribution(int seId, String caller) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + seId);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { seId });
		// 执行提交操作
		baseDao.submit("sendemail", "se_id=" + seId, "se_status", "se_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "se_id", seId);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { seId });
	}
	
	/**
	 * 反提交
	 * @param seId
	 * @param caller
	 */
	public void resSubmitDocDistribution(int seId, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + seId);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { seId });
		// 执行反提交操作
		baseDao.resOperate("sendemail", "se_id=" + seId, "se_status", "se_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "se_id", seId);
		handlerService.handler(caller, "resCommit", "after", new Object[] { seId });
	}
	
	/**
	 * 审核
	 * @param seId
	 * @param caller
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditDocDistribution(int seId, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + seId);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { seId });
		// 执行审核操作
		baseDao.audit("sendemail", "se_id=" + seId, "se_status", "se_statuscode", "se_auditdate", "se_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "se_id", seId);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { seId });
		//发邮件
		List<Object> sedCodeList = baseDao.getFieldDatasByCondition("sendemaildetail", "sed_code", "sed_seid = " + seId);
		List<String> filepathList = new ArrayList<String>();
		List<Map<String,String>> fpidList = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>(); 
		if(sedCodeList != null && !sedCodeList.isEmpty()){
			for(Object sedCode : sedCodeList){
				int start = sedCode.toString().lastIndexOf("-") + 1;
				String fpId = sedCode.toString().substring(start);
				String prefixCode = sedCode.toString().substring(0,start-1);
				String filepath = String.valueOf(baseDao.getFieldDataByCondition("documentlist", "dl_filepath", "dl_fpid in ('" + fpId +"','" + fpId + ";') and Rownum = 1"));
				if(fpId != null && !"".equals(fpId)){
					map.put("prefixCode", prefixCode);
					map.put("fpId", fpId);
					fpidList.add(map);
					filepathList.add(filepath);
				}
			}
		}
		String sql = "select SE_THEME,SE_CONTENT,SE_ADDRESS from sendemail where se_id = " + seId;
		List<Map<String,Object>> list = baseDao.queryForList(sql);
		String title = String.valueOf(list.get(0).get("SE_THEME"));
		String context = String.valueOf(list.get(0).get("SE_CONTENT"));
		String tomail = String.valueOf(list.get(0).get("SE_ADDRESS"));
		String regex = "<(.*)>";
		Pattern pattern = Pattern.compile(regex);
		StringBuilder sb = new StringBuilder();
		if(tomail != null && !"".equals(tomail) && !"null".equals(tomail)){
			String[] array = tomail.split(";");
			for(String email : array){
				Matcher matcher = pattern.matcher(email);
				if(matcher.find()){
					sb.append(matcher.group(1) + ";");
				}else{
					sb.append(tomail);
				}
			}
		}
		sendMailService.sendSysMail(title, context, sb.toString(), filepathList);
		//将文件放到文档管理
		for(Map<String,String> map1: fpidList){
			saveFileToDocManage(caller, map1.get("fpId"), map1.get("prefixCode"));
			String fpid = map1.get("fpId");
			Object dl_id = baseDao.getFieldDataByCondition("DocumentList", "dl_id", "dl_fpid in ('"+fpid+"','"+fpid+";')");
			//设置改文档的权限
			documentListService.extendParentPower(1,dl_id);
		}
	}
	
	/**
	 * 反审核
	 */
	public void resAuditDocDistribution(int seId, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("sendemail", "se_statuscode", "se_id=" + seId);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { seId });
		baseDao.resAuditCheck("sendemail", seId);
		// 执行反审核操作
		baseDao.resAudit("sendemail", "se_id=" + seId, "se_status", "se_statuscode", "se_auditdate", "se_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "se_id", seId);
		handlerService.afterResAudit(caller, new Object[] { seId });
	}
	
	private void saveFileToDocManage(String caller, String fpid, String prefixCode){
		int parentId = 1;
		String dl_style = "";
		int dl_kind = 0;
		String dl_status = "在录入";
		String dl_statuscode = "ENTERING";
		//构造formStore
		JSONObject formStore = new JSONObject();
		formStore.put("dl_virtualpath", "");
		formStore.put("dl_fpid", fpid + ";");
		formStore.put("dl_code", prefixCode);
		formStore.put("dl_remark", "");
		formStore.put("dl_createtime", "");
		formStore.put("dl_creator", "");
		formStore.put("dl_parentid", parentId);
		formStore.put("dl_style", dl_style);
		formStore.put("dl_kind", dl_kind);
		formStore.put("dl_status", dl_status);
		formStore.put("dl_statuscode", dl_statuscode);
		formStore.put("dl_creator",SystemSession.getUser().getEm_name());
		formStore.put("dl_createtime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		String dl_source = String.valueOf(baseDao.getFieldDataByCondition("documentlist", "DL_VIRTUALPATH", "dl_fpid in ('" + fpid + ";','" + fpid + "')"));
		formStore.put("dl_source", dl_source);
		//保存
		documentListService.save(caller, formStore.toJSONString());
	}
	
	

}
