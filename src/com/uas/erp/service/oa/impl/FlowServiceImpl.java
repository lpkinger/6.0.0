package com.uas.erp.service.oa.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.FlowDefine;
import com.uas.erp.model.FlowInstance;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.oa.FlowService;

@Service
public class FlowServiceImpl  implements FlowService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	
	private static String pattern = "yyyy-MM-dd";
	static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	static final String YMD_HMS_SQL = "yyyy-MM-dd HH24:MI:ss";

	/**
	 * 获取页面需要显示的标签页
	 * 
	 * @param nodeId
	 *            节点Id
	 * @param caller
	 *            Caller
	 */
	public String getActivatePanel(String nodeId, String caller) {
		// 获取流程版本
		String shortName = getShortName(caller);

		if (shortName!=null && !"".equals(shortName)) {
			String groups = String.valueOf(baseDao.getFieldDataByCondition("FLOW_NODE", "FN_GROUPS",
					"FN_FDSHORTNAME='" + shortName + "' AND FN_ID=" + nodeId));
			return groups;
		} else {
			BaseUtil.showError("当前单据没有设置对应的流程");
			return null;
		}
	}
	
	
	/**
	 * 获取页面需要显示的标签页
	 * 
	 * @param nodeName
	 *            节点名称
	 * @param shortName
	 *            版本名称
	 */
	public Map<String, Object> getUsingGroups(String nodeName, String shortName) {
			Map<String, Object> map = new HashMap<String, Object>();
			Object[] obj = baseDao.getFieldsDataByCondition("FLOW_NODE", "FN_GROUPS,FN_REMARK",
					"FN_FDSHORTNAME='" + shortName + "' AND FN_NODENAME='" + nodeName + "'");
			if(obj!=null) {
				map.put("groups", String.valueOf(obj[0]));
				map.put("remark", String.valueOf(obj[1]));
			}else {
				map.put("groups", null);
				map.put("remark", null);
			}
			return map;
	}
	
	/**
	 * 保存groups
	 * 
	 * @param nodeName
	 *            节点名称
	 * @param shortName
	 *            版本名称
	 * @param groups
	 *            json数据
	 */
	@Transactional
	public void saveUsingGroups(String remark,String groups,String nodeName, String shortName){
			boolean isExits = baseDao.checkIf("flow_node", "fn_nodename ='"+nodeName+"' and fn_fdshortname ='"+shortName+"'");
			if(isExits){
				String sql = "UPDATE FLOW_NODE SET FN_REMARK = '"+remark+"',FN_GROUPS = '"+groups+"' WHERE fn_nodename ='"+nodeName+"' and fn_fdshortname ='"+shortName+"'";
				baseDao.execute(sql);
			}else{
				String sql = "INSERT INTO FLOW_NODE(FN_ID,FN_NODENAME,FN_FDSHORTNAME,FN_GROUPS,FN_TYPE,FN_REMARK) VALUES (FLOW_NODE_SEQ.nextval,"
						   + "'"+nodeName+"',"
						   + "'"+shortName+"',"
						   + "'"+groups+"',"
						   + "'node',"
						   + "'"+remark+"')";
				baseDao.execute(sql);
			}
	}
	
	/**
	 * 保存派生意见操作
	 * 
	 * @param shortName
	 *            版本名称
	 * @param groups
	 *            json数据
	 * @param remark
	 *            备注
	 */
	@Transactional
	public void saveUpdateOperation(String isDuty,String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId){
		//保存提交操作信息
		boolean isOpExits = baseDao.checkIf("flow_operation", "fo_name ='"+name+"' and fo_fdshortname ='"+shortName+"'");
		String duty = isDuty.equals("责任人")?"true":"false";
		if(!isOpExits){
			String sql = "INSERT INTO FLOW_OPERATION VALUES (FLOW_OPERATION_SEQ.nextval,"
					   + "'"+name+"',"
					   + "'Update',null,"
					   + "'"+remark+"',"
					   + "'"+fromNodeName+"',"
					   + "'"+groupName+"',null,null,"
					   + "'"+shortName+"',"
					   + "null,null,null,'"+duty+"',null,"
					   + "'"+fromId+"',"
					   + "null)";
			baseDao.execute(sql);
		}else{
			String sql = "UPDATE FLOW_OPERATION SET FO_ISDUTY = '"+duty+"',FO_REMARK = '"+remark+"',FO_GROUPNAME = '"+groupName+"',fo_nodename='"+fromNodeName+"',fo_nextnodename='"+
					nextNodeName+"',fo_nodeid="+fromId+",fo_nextnodeid="+toId+"  WHERE fo_name ='"+name+"' and fo_fdshortname ='"+shortName+"'";
			
			baseDao.execute(sql);
		}
		//保存提交操作字段信息
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(nowItems);
		List<Map<Object, Object>> deleteMaps = BaseUtil.parseGridStoreToMaps(deleteItems);
		//分辨新增的字段 和 旧字段
		List<Map<Object, Object>> newFields = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> oldFields = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> map : maps) {
			if(String.valueOf(map.get("fgc_id"))!=""&&String.valueOf(map.get("fgc_id")).length()>0){
				oldFields.add(map);
			}else{
				newFields.add(map);
			}
		}
		//更新or插入数据
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<Object, Object> map : oldFields) {//更新
			sql.append("UPDATE FLOW_GROUPCONFIG SET FGC_GROUPNAME = '"+groupName+"',FGC_READ = '"+map.get("read")+"',fgc_detno = '"+map.get("detno")+"',fgc_requiredfield = '"+map.get("main")+"',"
					 + " fgc_new = '"+map.get("isNew")+"',fgc_width = '"+map.get("columnsWidth")+"',fgc_role = '"+map.get("fgc_role")+"',fgc_rolecode = '"+map.get("fgc_rolecode")+"'"
					 + " where fgc_id = "+map.get("fgc_id")+";");
		}
		for (Map<Object, Object> map : newFields) {//插入
			sql.append("INSERT INTO FLOW_GROUPCONFIG values("
					+ "FLOW_GROUPCONFIG_SEQ.nextval,"
					+ "'"+groupName+"',"
					+ "'"+map.get("field")+ "',"
					+ "'"+map.get("isNew")+ "',"
					+ "'"+map.get("main")+ "',"
					+ "'"+map.get("read")+ "',null,"
					+ "'"+shortName+ "',"
					+ "'"+map.get("detno")+ "',"
					+ "'"+map.get("columnsWidth")+ "',"
					+ "'"+map.get("fgc_role")+ "',"
					+ "'"+map.get("fgc_rolecode")+ "');");
		}
		for (Map<Object, Object> map : deleteMaps) {//删除
			sql.append("DELETE FROM FLOW_GROUPCONFIG where fgc_id = "+map.get("fgc_id")+";");
		}
		sql.append(" end;");
		baseDao.execute(sql.toString());
	}
	
	/**
	 * 保存跳转操作
	 * 
	 * @param shortName
	 *            版本名称
	 * @param groups
	 *            json数据
	 * @param remark
	 *            备注
	 */
	@Transactional
	public void saveTurnOperation(String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId){
		//保存提交操作信息
		boolean isOpExits = baseDao.checkIf("flow_operation", "fo_name ='"+name+"' and fo_fdshortname ='"+shortName+"'");
		if(!isOpExits){
			String sql = "INSERT INTO FLOW_OPERATION VALUES (FLOW_OPERATION_SEQ.nextval,"
					   + "'"+name+"',"
					   + "'Turn',"
					   + "'"+nextNodeName+"',"
					   + "'"+remark+"',"
					   + "'"+fromNodeName+"',"
					   + "'"+groupName+"',null,null,"
					   + "'"+shortName+"',"
					   + "null,null,null,'true',"
					   + "'"+toId+"',"
					   + "'"+fromId+"',"
					   + "null)";
			baseDao.execute(sql);
		}else{
			String sql = "UPDATE FLOW_OPERATION SET FO_REMARK = '"+remark+"',FO_GROUPNAME = '"+groupName+"',fo_nodename='"+fromNodeName+"',fo_nextnodename='"+
					nextNodeName+"',fo_nodeid="+fromId+",fo_nextnodeid="+toId+"  WHERE fo_name ='"+name+"' and fo_fdshortname ='"+shortName+"'";
			baseDao.execute(sql);
		}
		//保存提交操作字段信息
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(nowItems);
		List<Map<Object, Object>> deleteMaps = BaseUtil.parseGridStoreToMaps(deleteItems);
		//分辨新增的字段 和 旧字段
		List<Map<Object, Object>> newFields = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> oldFields = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> map : maps) {
			if(String.valueOf(map.get("fgc_id"))!=""&&String.valueOf(map.get("fgc_id")).length()>0){
				oldFields.add(map);
			}else{
				newFields.add(map);
			}
		}
		//更新or插入数据
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<Object, Object> map : oldFields) {//更新
			sql.append("UPDATE FLOW_GROUPCONFIG SET FGC_GROUPNAME = '"+groupName+"',FGC_READ = '"+map.get("read")+"',fgc_detno = '"+map.get("detno")+"',fgc_requiredfield = '"+map.get("main")+"',"
					 + " fgc_new = '"+map.get("isNew")+"',fgc_width = '"+map.get("columnsWidth")+"',fgc_role = '"+map.get("fgc_role")+"',fgc_rolecode = '"+map.get("fgc_rolecode")+"'"
					 + " where fgc_id = "+map.get("fgc_id")+";");
		}
		for (Map<Object, Object> map : newFields) {//插入
			sql.append("INSERT INTO FLOW_GROUPCONFIG values("
					+ "FLOW_GROUPCONFIG_SEQ.nextval,"
					+ "'"+groupName+"',"
					+ "'"+map.get("field")+ "',"
					+ "'"+map.get("isNew")+ "',"
					+ "'"+map.get("main")+ "',"
					+ "'"+map.get("read")+ "',null,"
					+ "'"+shortName+ "',"
					+ "'"+map.get("detno")+ "',"
					+ "'"+map.get("columnsWidth")+ "',"
					+ "'"+map.get("fgc_role")+ "',"
					+ "'"+map.get("fgc_rolecode")+ "');");
		}
		for (Map<Object, Object> map : deleteMaps) {//删除
			sql.append("DELETE FROM FLOW_GROUPCONFIG where fgc_id = "+map.get("fgc_id")+";");
		}
		sql.append(" end;");
		baseDao.execute(sql.toString());
	}
	
	/**
	 * 保存提交操作
	 * 
	 * @param shortName
	 *            版本名称
	 * @param groups
	 *            json数据
	 * @param remark
	 *            备注
	 */
	@Transactional
	public void saveCommitOperation(String remark,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId){
		//保存START节点信息
		JSONObject  o  =  new JSONObject();
		o.put("name", "基本信息");
		o.put("title", "基本信息");
		JSONArray a = new JSONArray();
		a.add(o);
		String startGroups = a.toJSONString();
		int newNodeId = baseDao.getSeqId("FLOW_NODE_SEQ");
		boolean isNodeExits = baseDao.checkIf("flow_node", "fn_nodename ='START' and fn_fdshortname ='"+shortName+"'");
		if(!isNodeExits){
			String sql = "INSERT INTO FLOW_NODE(FN_ID,FN_NODENAME,FN_FDSHORTNAME,FN_GROUPS,FN_TYPE,FN_REMARK) VALUES ("
					   + "'"+newNodeId+"',"
					   + "'START',"
					   + "'"+shortName+"',"
					   + "'"+startGroups+"',"
					   + "'node',"
					   + "null)";
			baseDao.execute(sql);
		}
		//保存提交操作信息
		boolean isOpExits = baseDao.checkIf("flow_operation", "fo_name ='commit' and fo_groupname = '基本信息' and fo_fdshortname ='"+shortName+"'");
		if(!isOpExits){
			String sql = "INSERT INTO FLOW_OPERATION (FO_ID,FO_NAME,FO_TYPE,FO_NEXTNODENAME,FO_REMARK,FO_NODENAME,FO_GROUPNAME,FO_FLOWNAME,FO_FLOWNODENAME,FO_FDSHORTNAME,FO_FLOWCALLER,FO_FLOWNODEID,FO_URL,FO_ISDUTY,FO_NEXTNODEID,FO_NODEID,FO_CONDITION) VALUES (FLOW_OPERATION_SEQ.nextval,"
					   + "'commit','Turn',"
					   + "'"+nextNodeName+"',"
					   + "'"+remark+"','START','基本信息',null,null,"
					   + "'"+shortName+"',"
					   + "null,null,null,'true',"
					   + "'"+toId+"',"
					   + "'"+newNodeId+"',"
					   + "null)";
			baseDao.execute(sql);
		}else{
			String sql = "UPDATE FLOW_OPERATION SET FO_REMARK = '"+remark+"',fo_nextnodename='"+nextNodeName+"',fo_nextnodeid="+toId+"  WHERE fo_name ='commit' and fo_groupname = '基本信息' and fo_fdshortname ='"+shortName+"'";
			baseDao.execute(sql);
		}
		//保存提交操作字段信息
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(nowItems);
		List<Map<Object, Object>> deleteMaps = BaseUtil.parseGridStoreToMaps(deleteItems);
		//分辨新增的字段 和 旧字段
		List<Map<Object, Object>> newFields = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> oldFields = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> map : maps) {
			if(String.valueOf(map.get("fgc_id"))!=""&&String.valueOf(map.get("fgc_id")).length()>0){
				oldFields.add(map);
			}else{
				newFields.add(map);
			}
		}
		//更新or插入数据
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<Object, Object> map : oldFields) {//更新
			sql.append("UPDATE FLOW_GROUPCONFIG SET FGC_READ = '"+map.get("read")+"',fgc_detno = '"+map.get("detno")+"',fgc_requiredfield = '"+map.get("main")+"',"
					 + " fgc_new = '"+map.get("isNew")+"',fgc_width = '"+map.get("columnsWidth")+"',fgc_role = '"+map.get("fgc_role")+"',fgc_rolecode = '"+map.get("fgc_rolecode")+"'"
					 + " where fgc_id = "+map.get("fgc_id")+";");
		}
		for (Map<Object, Object> map : newFields) {//插入
			sql.append("INSERT INTO FLOW_GROUPCONFIG (FGC_ID,FGC_GROUPNAME,FGC_FIELD,FGC_NEW,FGC_REQUIREDFIELD,FGC_READ,FGC_REMARK,FGC_FDSHORTNAME,FGC_DETNO,FGC_WIDTH,FGC_ROLE,FGC_ROLECODE) values("
					+ "FLOW_GROUPCONFIG_SEQ.nextval,"
					+ "'基本信息',"
					+ "'"+map.get("field")+ "',"
					+ "'"+map.get("isNew")+ "',"
					+ "'"+map.get("main")+ "',"
					+ "'"+map.get("read")+ "',null,"
					+ "'"+shortName+ "',"
					+ "'"+map.get("detno")+ "',"
					+ "'"+map.get("columnsWidth")+ "',"
					+ "'"+map.get("fgc_role")+ "',"
					+ "'"+map.get("fgc_rolecode")+ "');");
		}
		for (Map<Object, Object> map : deleteMaps) {//删除
			sql.append("DELETE FROM FLOW_GROUPCONFIG where fgc_id = "+map.get("fgc_id")+";");
		}
		sql.append(" end;");
		baseDao.execute(sql.toString());
	}
	
	/**
	 * 保存新tab
	 * 
	 * @param nodeName
	 *            节点名称
	 * @param shortName
	 *            版本名称
	 * @param tabs
	 *            json数据
	 */
	public void saveNewTab(String tabs,String tabName, String shortName){
		boolean isExist = baseDao.checkIf("FLOW_GROUPCONFIG", "FGC_GROUPNAME = '"+tabName+"' and fgc_fdshortname='"+shortName+"'");
		if(isExist){
			BaseUtil.showError("新增的分组名称已存在，请修改后保存");
		}
		List<Map<Object, Object>>  maps = BaseUtil.parseGridStoreToMaps(tabs);
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<Object, Object> map : maps) {
			sql.append("INSERT INTO FLOW_GROUPCONFIG values("
					+ "FLOW_GROUPCONFIG_SEQ.nextval,"
					+ "'"+tabName+"',"
					+ "'"+map.get("field")+ "',"
					+ "'"+map.get("isNew")+ "',"
					+ "null,null,null,"
					+ "'"+shortName + "',"
					+ "'" +map.get("detno") + "',"
					+ "'" +map.get("width") + "',"
					+ "null,null);");
		}
		sql.append(" end;");
		baseDao.execute(sql.toString());
	}
	
	/**
	 * 获取所有的可用分组
	 * 
	 * @param shortName
	 *            版本名称
	 */
	
	public List<Map<String, Object>> getAllGroups(String shortName){
			String sql = "select fgc_groupname as fgc_groupname,min(fgc_id) as detno from flow_groupconfig where fgc_fdshortname = ?  group by fgc_groupname  order by detno ";
			return baseDao.queryForList(sql, shortName);
	}
	
	
	/**
	 * 获取参照页信息
	 * 
	 * @param shortName
	 *            版本名称
	 */
	
	public List<Map<String, Object>> getSelectTab(String groupName,String shortName,String caller){
			String sql = " select * from FLOW_GROUPCONFIG left join formdetail "+
					" on formdetail.fd_field = flow_groupconfig.fgc_field "+
					" where fgc_fdshortname = ?  and fgc_groupname = ?  "+
					" and fd_foid = (select fo_id from form where fo_caller = ?) order by fgc_detno";
			return baseDao.queryForList(sql, shortName,groupName,caller);
	}

	/**
	 * 获取标签页配置
	 * 
	 * @param groupName
	 *            分组名称
	 * @param caller
	 *            caller
	 */
	public Map<String, Object> getGroupConfig(String groupName, String caller) {
		// 获取流程版本
		String shortName = getShortName(caller);

		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "SELECT * FROM FLOW_GROUPCONFIG WHERE FGC_GROUPNAME = ? AND FGC_FDSHORTNAME = ?";
		map.put("data", baseDao.queryForList(sql, groupName, shortName));
		map.put("count", baseDao.getCountByCondition("FLOW_GROUPCONFIG",
				"FGC_GROUPNAME = '" + groupName + "' AND FGC_FDSHORTNAME = '" + shortName + "'"));
		return map;
	}

	/**
	 * 获取节点有哪些操作（即页面要出现的按钮）
	 * 
	 * @param nodeId
	 *            节点Id
	 * @param id
	 *            单据Id
	 * @param caller
	 *            Caller
	 */
	public List<Map<String, Object>> getOperation(int nodeId, String caller) {
		// 获取流程版本
		String shortName = getShortName(caller);
		
		String sql = "SELECT * FROM FLOW_OPERATION WHERE FO_NODEID = ? AND FO_FDSHORTNAME = ?";
		return baseDao.queryForList(sql, nodeId, shortName);
	}
	
	/**
	 * 获取流程
	 * @param caller  Caller
	 */
	public List<Map<String, Object>> getDefine(String caller) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM FLOW_DEFINE WHERE FD_PARENTID = 0 ");
		if(caller!=null&&!"".equals(caller)) {
			sql.append("AND FD_CALLER = ?");
			return baseDao.queryForList(sql.toString(), caller);
		}else
			return baseDao.queryForList(sql.toString());
	}
	
	/**
	 * 获取流程实例
	 * @param fd_id  flow_define
	 */
	public List<Map<String, Object>> getDefineInstance(String fd_id) {
		String sql = "SELECT * FROM FLOW_DEFINE WHERE FD_ID = ?";
		return baseDao.queryForList(sql, fd_id);
	}

	/**
	 * 更新流程
	 * @param caller  Caller
	 */
	public void updateFlowDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode) {
		Object duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code = '"+defaultCode+"'");
		String sql = "UPDATE FLOW_DEFINE SET FD_DEFAULTDUTY = '"+duty+"',FD_DEFAULTDUTYCODE = '"+defaultCode+"',FD_NAME = '"+name+"',FD_SHORTNAME = '"+shortname+"',FD_REMARK = '"+remark+"' WHERE FD_PARENTID = 0 AND FD_CALLER = '"+caller+"'";
		baseDao.execute(sql);
		//生成单据前缀码
		if(baseDao.checkIf("MaxNumbers", "MN_TABLENAME='"+caller+"'")) {
			if(PrefixCode!=null && !PrefixCode.equals("")) {
				String createPrefixCode = "update MaxNumbers set MN_LEADCODE='"+PrefixCode+"' where MN_TABLENAME='"+caller+"'";
				baseDao.execute(createPrefixCode);
			}
		}else {
			if(PrefixCode!=null && !PrefixCode.equals("")) {
				String YearMonth = new SimpleDateFormat("YYYYMM").format(new Date());
				String createPrefixCode = "insert into MaxNumbers(MN_TABLENAME,MN_TYPE,"
						+ "MN_LEADCODE,MN_NUMBER,MN_DESCRIPTION,MN_MAXRETURN,MN_ID) "
						+ "values('"+caller+"',"
								+ "2,'"+PrefixCode+"','"+YearMonth+"0001','"+shortname+"',20,MaxNumbers_seq.nextval)";
				baseDao.execute(createPrefixCode);
			}
		}
	}
	
	/**
	 * 保存流程
	 * @param caller  Caller
	 */
	public void saveDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode){
		boolean isCaller = baseDao.checkIf("FLOW_DEFINE", "FD_CALLER = '"+caller+"' and FD_PARENTID = 0");
		if(!isCaller){
			Employee employee = SystemSession.getUser();
			Object detno = baseDao.getFieldDataByCondition("FLOW_DEFINE", "MAX(FD_DETNO)", "FD_PARENTID = 0");
			int newDetno = 1;
			if(detno!=null){
				newDetno = Integer.parseInt(String.valueOf(detno)) + 1;
			}
			Object duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code = '"+defaultCode+"'");
			String sql = "INSERT INTO FLOW_DEFINE(FD_ID,FD_NAME,FD_VERSIONNUM," + 
					"FD_STATUS,FD_SHORTNAME,FD_DATE,FD_REMARK,FD_MAN," + 
					"FD_CALLER,FD_DETNO,FD_PARENTID,FD_ISLEAF,FD_FCID," + 
					"FD_DEFAULTDUTY,FD_DEFAULTDUTYCODE) VALUES (FLOW_DEFINE_SEQ.nextval,"
					   + "'"+name+"',"
					   + "null,"
					   + "'using',"
					   + "'"+shortname+"',"
					   + getSqlDate()+","
					   + "'"+remark+"',"
					   + "'"+employee.getEm_name()+"',"
					   + "'"+caller+"',"
					   + newDetno+","
					   + "0,"
					   + "'F',"
					   + "null,'"+duty+"','"+defaultCode+"')";
			baseDao.execute(sql);
			//生成单据前缀码
			if(PrefixCode!=null && !PrefixCode.equals("")) {
				String YearMonth = new SimpleDateFormat("YYYYMM").format(new Date());
				String createPrefixCode = "insert into MaxNumbers(MN_TABLENAME,MN_TYPE,"
						+ "MN_LEADCODE,MN_NUMBER,MN_DESCRIPTION,MN_MAXRETURN,MN_ID) "
						+ "values('"+caller+"',"
								+ "2,'"+PrefixCode+"','"+YearMonth+"0001','"+shortname+"',20,MaxNumbers_seq.nextval)";
				baseDao.execute(createPrefixCode);
			}
		}else{
			BaseUtil.showError("新增流程的caller已存在");
		}
	}
	
	
	/**
	 * 修改流程实例
	 * @param caller  Caller
	 */
	@Transactional
	public void updateDefineInstance(String id,String remark,String caller){
		String sql = "UPDATE FLOW_DEFINE SET FD_REMARK = '"+remark+"' WHERE FD_ID = "+ id;
		baseDao.execute(sql);
		//修改流程图备注
		Object[] str = baseDao.getFieldsDataByCondition("FLOW_DEFINE", "FD_NAME,FD_SHORTNAME", "FD_ID = "+ id);
		String sql2 = "UPDATE FLOW_CHART SET FC_REMARK = '"+remark+"' WHERE FC_NAME = '"+ str[0] + "' AND FC_SHORTNAME ='" + str[1] +"'";
		baseDao.execute(sql2);
	}
	
	/**
	 * 修改流程实例
	 * @param caller  Caller
	 */
	@Transactional
	public void updateInstanceStatus(String id,String status,String caller){
		if(status.equals("close")){//启动流程实例
			String sql = "UPDATE FLOW_DEFINE SET FD_STATUS = 'close' WHERE FD_CALLER = '"+caller+"' AND FD_ISLEAF = 'T'";
			baseDao.execute(sql);
			String sql2 = "UPDATE FLOW_DEFINE SET FD_STATUS = 'enable' WHERE FD_ID = "+ id;
			baseDao.execute(sql2);
		} else if(status.equals("enable")){//关闭流程实例
			String sql = "UPDATE FLOW_DEFINE SET FD_STATUS = 'close' WHERE FD_ID = "+ id;
			baseDao.execute(sql);
		}
	}
	
	/**
	 * 修改流程实例chartId
	 * @param caller  Caller
	 */
	public void updateInstanceChartId(String id,String fcid){
		if(!fcid.equals("0")) {
			String sql = "UPDATE FLOW_DEFINE SET FD_FCID = "+fcid+" WHERE FD_ID = "+ id;
			baseDao.execute(sql);
		}
	}
	
	public Map<String, String> getFlowChart(String fcid){
		String sql = "select *  from flow_chart where fc_id = ?";
		Map<String, Object> map = baseDao.getJdbcTemplate().queryForMap(sql, new Object[] { fcid });
		Map<String, String> mapInfo = new HashMap<String, String>();
		mapInfo.put("xmlInfo", String.valueOf(map.get("FC_XMLSTRING")));
		mapInfo.put("caller", String.valueOf(map.get("FC_CALLER")));
		mapInfo.put("shortName", String.valueOf(map.get("FC_SHORTNAME")));
		mapInfo.put("remark", String.valueOf( map.get("FC_REMARK")));
		return mapInfo;
	}
	
	
	public Map<String, String> getFlowChartByCaller(String caller){
		Map<String, String> mapInfo = new HashMap<String, String>();
		String shortName = getShortName(caller);
		String sql = "select *  from flow_chart where fc_caller = '"+caller+"' and fc_shortname = '"+shortName+"'";
		Map<String, Object> map = baseDao.getJdbcTemplate().queryForMap(sql);
		mapInfo.put("fcid", String.valueOf(map.get("FC_ID")));
		return mapInfo;
	}
	
	
	/**
	 * 保存流程实例
	 * @param caller  Caller
	 */
	@Transactional
	public void saveDefineInstance(String remark,String caller){
		List<Map<String, Object>> modelMap = new ArrayList<Map<String, Object>>();
		Employee employee = SystemSession.getUser();
		Object[] mainDefine = baseDao.getFieldsDataByCondition("FLOW_DEFINE", "FD_ID,FD_SHORTNAME,FD_NAME", "FD_CALLER = '"+caller+"' AND FD_PARENTID = 0 and FD_ISLEAF = 'F'");
		//最新版本号
		modelMap = baseDao.queryForList("SELECT * FROM FLOW_DEFINE WHERE FD_CALLER = '"+caller+"' and FD_ISLEAF = 'T' order by fd_id desc ");
		if(modelMap.size()>0){
			Object detno = baseDao.getFieldDataByCondition("FLOW_DEFINE", "MAX(FD_DETNO)", "FD_PARENTID = 0");
			Map<String,Object> map = new HashMap<String,Object>();
			map = modelMap.get(0);
			//生成新版本号
			String Version = String.valueOf(map.get("FD_VERSIONNUM"));
			if(Version.length()<1){
				BaseUtil.showError("查询流程版本错误");
			}
			Version = Version.substring(1, Version.length());
			double num = Double.parseDouble(Version);
			num = num + 0.1;
			DecimalFormat df = new DecimalFormat("######0.0"); 
			Version = "V" + df.format(num);
			String sql = "INSERT INTO FLOW_DEFINE(FD_ID,FD_NAME,FD_VERSIONNUM," + 
					"FD_STATUS,FD_SHORTNAME,FD_DATE,FD_REMARK,FD_MAN," +
					"FD_CALLER,FD_DETNO,FD_PARENTID,FD_ISLEAF,FD_FCID," + 
					"FD_DEFAULTDUTY,FD_DEFAULTDUTYCODE) VALUES (FLOW_DEFINE_SEQ.nextval,"
					   + "'"+mainDefine[2]+"',"
					   + "'"+Version+"',"
					   + "'close',"
					   + "'"+mainDefine[1]+"-"+Version+"',"
					   + getSqlDate()+","
					   + "'"+remark+"',"
					   + "'"+employee.getEm_name()+"',"
					   + "'"+caller+"',"
					   + String.valueOf(detno)+","
					   + mainDefine[0]+","
					   + "'T',"
					   + "null,null,null)";
			baseDao.execute(sql);
		}else{//第一个流程实例
			//生成新版本号
			String Version = "V1.0";
			String sql = "INSERT INTO FLOW_DEFINE(FD_ID,FD_NAME,FD_VERSIONNUM," + 
					"FD_STATUS,FD_SHORTNAME,FD_DATE,FD_REMARK,FD_MAN," + 
					"FD_CALLER,FD_DETNO,FD_PARENTID,FD_ISLEAF,FD_FCID," + 
					"FD_DEFAULTDUTY,FD_DEFAULTDUTYCODE) VALUES (FLOW_DEFINE_SEQ.nextval,"
					   + "'"+mainDefine[2]+"',"
					   + "'"+Version+"',"
					   + "'close',"
					   + "'"+mainDefine[1]+"-"+Version+"',"
					   + getSqlDate()+","
					   + "'"+remark+"',"
					   + "'"+employee.getEm_name()+"',"
					   + "'"+caller+"',"
					   + "1,"
					   + mainDefine[0]+","
					   + "'T',"
					   + "null,null,null)";
			baseDao.execute(sql);
		}
	}
	
	/**
	 * 获取操作日志
	 * 
	 * @param id
	 *            单据id
	 */
	public List<Map<String, Object>> getLog(String id) {
		String sql = "SELECT * FROM FLOW_LOG LEFT JOIN FLOW_OPERATION ON FL_FOID = FO_ID WHERE Fl_keyvalue = ? order by FL_dealtime";
		
		return baseDao.queryForList(sql, id);
	}

	/**
	 * 	获取附件
	 * 	@param id	单据id
	 */
	public List<Map<String, Object>> getFile(String id){ 
		String sql = "SELECT * FROM FLOW_FILE WHERE FF_KEYVALUE = ?  AND FF_STATUS = 'using' order by FF_UPTIME DESC";
		 
		return baseDao.queryForList(sql, id);
	}
	
	/**
	 * 	保存附件
	 *  @param file	文件对象
	 *  @param id	单据id
	 * 	@param caller	单据caller
	 */
	public void saveFile(String file,String id,String caller,String name){
		//获取流程版本
		String shortName = getShortName(caller);
		//将文件对象转成map
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(file);
		//获取当前登录人
		Employee employee = SystemSession.getUser();
		
		String sql = "INSERT INTO FLOW_FILE (FF_ID,FF_FILEID,FF_UPMAN,FF_UPMANCODE,FF_UPTIME,FF_FDSHORTNAME,FF_KEYVALUE,FF_CALLER,FF_NAME,FF_SIZE) values("
				+ "FLOW_FILE_SEQ.nextval,"
				+ store.get("filepath")+ ","
				+ "'"+employee.getEm_name()+ "',"
				+ "'"+employee.getEm_code()+ "',"
				+ getSqlDate() + ","
				+ "'"+shortName + "',"
				+ id + ","
				+ "'"+caller+"',"
				+ "'"+name+"',"
				+ "'"+store.get("size")+"')";
		baseDao.execute(sql);
	}
	
	/**
	 * 	保存系统的附件到流程的附件列表
	 *  @param store  数据对象
	 *  @param caller	单据caller
	 * 	@param foid	      form表id
	 *  @param id	      单据id
	 */
	@Transactional
	public void saveFileIntoGrid(Map<Object, Object> store,String caller,int foid,int id){
		//获取流程版本
		String shortName = String.valueOf(baseDao.getFieldDataByCondition("FLOW_DEFINE", "FD_SHORTNAME", "FD_CALLER='"+caller+"' AND FD_STATUS='enable'"));
		//查询附件字段
		Object fd_field = baseDao.getFieldDataByCondition("formdetail", "fd_field", "fd_foid = "+foid+" and fd_logictype = 'file'");
		String file = ObjectToString(store.get(ObjectToString(fd_field)));
		if(file!=null&&!file.equals("")&&file.length()>0){
			String[] arr = file.split(";");
			//插入附件表
			Employee employee = SystemSession.getUser();
			//当前时间
			String nowTime = getSqlDate();
			List<String> list = new ArrayList<String>();
			for (String str : arr) {
				Object[] obj = baseDao.getFieldsDataByCondition("filepath", "fp_name,fp_size", "fp_id = "+str);
				if(obj!=null) {
					list.add("INSERT INTO FLOW_FILE (FF_ID,FF_FILEID,FF_UPMAN,FF_UPMANCODE,FF_UPTIME,FF_FDSHORTNAME,FF_KEYVALUE,FF_CALLER,FF_NAME,FF_SIZE) values("
							+ "FLOW_FILE_SEQ.nextval,"
							+ str+ ","
							+ "'"+employee.getEm_name()+ "',"
							+ "'"+employee.getEm_code()+ "',"
							+ nowTime + ","
							+ "'"+shortName + "',"
							+ id + ","
							+ "'"+caller+"',"
							+ "'"+ObjectToString(obj[0])+"',"
							+ "'"+ObjectToString(obj[1])+"')");
				}
			}
			baseDao.execute(list);
		}
	}
	
	/**
	 * 	更新附件
	 *  @param fileid	文件id
	 *  @param id	单据id
	 *  @param filename	文件名称
	 */
	public void updateFile(String fileid,String id, String filename){
		//当前时间
		String nowTime = getSqlDate();
		
		Employee employee = SystemSession.getUser();
		//更新附件状态
		baseDao.updateByCondition("FLOW_FILE", "FF_STATUS = 'end'", "FF_FILEID = '"+fileid+"' and FF_KEYVALUE = '"+id+"'");
		//记录日志
		String remark = "删除附件:"+filename;
		String sql = "INSERT INTO FLOW_LOG values("
				+ "FLOW_LOG_SEQ.nextval,null,"
				+ "'"+employee.getEm_code()+ "',"
				+ "'"+employee.getEm_name()+ "',"
				+ nowTime + ","
				+ "null,null,"
				+ id + ","
				+ "null,null,null,"
				+ "'deletefile',"
				+ fileid+","
				+ "'"+remark+"')";
		baseDao.execute(sql);
	}
	
	/**
	 * 	还原附件
	 *  @param fileid	文件id
	 *  @param id	单据id
	 *  @param logid	日志id
	 */
	public void backFile(String logid,String fileid,String id){
		//更新附件状态
		baseDao.updateByCondition("FLOW_FILE", "FF_STATUS = 'using'", "FF_FILEID = '"+fileid+"' and FF_KEYVALUE = '"+id+"'");
		//删除日志
		baseDao.deleteByCondition("FLOW_LOG", "FL_ID = ?",new Object[] { logid });
	}
	
	/**
	 * 	检验上下节点保存信息
	 *  @param fileid	文件id
	 *  @param id	单据id
	 *  @param logid	日志id
	 */
	public Map<String, Object> checkNodeSaved(String shortName,String fromNodeName,String toNodeName,String operationType){
		Map<String, Object> map = new HashMap<String, Object>();
		Object fromId = baseDao.getFieldDataByCondition("flow_node", "fn_id", "fn_nodename ='"+fromNodeName+"' and fn_fdshortname = '"+shortName+"'");
		Object toId = baseDao.getFieldDataByCondition("flow_node", "fn_id", "fn_nodename ='"+toNodeName+"' and fn_fdshortname = '"+shortName+"'");
		if(operationType.equals("Turn")||operationType.equals("Judge")){//校验前后节点
			if(String.valueOf(fromId).equals("null")){
				BaseUtil.showError("来源节点未保存，请保存后再编辑操作信息");
			}
			if(String.valueOf(toId).equals("null")){
				BaseUtil.showError("目标节点未保存，请保存后再编辑操作信息");
			}
			if(String.valueOf(fromId).equals(String.valueOf(toId))){
				BaseUtil.showError("不能编辑同一节点操作到同一节点");
			}
		}else if(operationType.equals("Commit")){//提交只校验目标节点
			if(String.valueOf(toId).equals("null")){
				BaseUtil.showError("目标节点未保存，请保存后再编辑操作信息");
			}
		}else if(operationType.equals("Flow")||operationType.equals("Update")||operationType.equals("Task")){//派生只校验来源节点
			if(String.valueOf(fromId).equals("null")){
				BaseUtil.showError("来源节点未保存，请保存后再编辑操作信息");
			}
		}
		map.put("fromId", fromId);
		map.put("toId", toId);
		map.put("fromNodeName", fromNodeName);
		map.put("toNodeName", toNodeName);
		return map;
	}
	
	/**
	 * 创建新的实例
	 * 
	 */
	@Transactional
	public void createInstance(int nodeId, int id, String caller,int btnid,Map<String,Object> map,List<Map<String,Object>> personCodes,String title){
		//获取流程版本
		String shortName = getShortName(caller);
		
		//获取当前节点名称
		String nodeName= ObjectToString(baseDao.getFieldDataByCondition("Flow_Node", "FN_NODENAME", "FN_FDSHORTNAME='"+shortName+"' and FN_ID="+nodeId));
		//当前时间
		String nowTime = getSqlDate();
		//获取当前登录人
		Employee employee = SystemSession.getUser();
		Form form = getFormWithCloud(caller, false);
		
		String codeField = form.getFo_codefield();
		String keyField = form.getFo_keyfield();
		String tableName = form.getFo_table();
		Object codeValue = baseDao.getFieldDataByCondition(tableName, codeField, keyField+"="+id);
		Object[] nextNode = baseDao.getFieldsDataByCondition("FLOW_OPERATION LEFT JOIN FLOW_NODE ON FO_NEXTNODEID=FN_ID", new String[] {"FN_ID","FN_NODENAME"}, 
				"FN_FDSHORTNAME='"+shortName+"' AND FO_NODEID="+nodeId+" AND FO_ID = " + btnid );
		//判断是否决策节点
		boolean checkJudge = baseDao.checkIf("FLOW_OPERATION LEFT JOIN FLOW_NODE ON FO_NEXTNODEID=FN_ID", "FO_ID="+btnid+" AND FN_TYPE='judge'");
		if(checkJudge) {
			List<String> sqlList = new ArrayList<String>();
			//判断是否第一节点
			boolean isStart = false;
			String commit = ObjectToString(baseDao.getFieldDataByCondition("FLOW_OPERATION", "FO_NAME", "FO_FDSHORTNAME='"+shortName+"' and FO_NODEID="+nodeId));
			if(nodeName.equals("START")&&commit.equals("commit")) {
				isStart = true;
			}
			if(isStart) {
				//新建Start流程实例
				FlowInstance startInstance = new FlowInstance();
				int startInstanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
				int startInstanceLogId = baseDao.getSeqId("FLOW_LOG_SEQ");
				startInstance.setFi_id(startInstanceId);
				startInstance.setFi_fdshortname(shortName);
				startInstance.setFi_nodeid(nodeId);
				startInstance.setFi_codevalue(ObjectToString(codeValue));
				startInstance.setFi_keyvalue(id);
				startInstance.setFi_handler(ObjectToString(map.get("duty")));
				startInstance.setFi_handlercode(ObjectToString(map.get("dutycode")));
				startInstance.setFi_nodename(nodeName);
				startInstance.setFi_startman(ObjectToString(map.get("creator")));
				startInstance.setFi_startmancode(ObjectToString(map.get("creatorcode")));
				startInstance.setFi_status("end");;
				startInstance.setFi_caller(caller);
				startInstance.setFi_keyfield(keyField);
				startInstance.setFi_time(DateUtil.format(new Date(), YMD_HMS));
				startInstance.setFi_starttime(DateUtil.format(new Date(), YMD_HMS));
				startInstance.setFi_title(title);
				startInstance.setFi_flid(startInstanceLogId);
				sqlList.add(FlowInstanceInsertSql(startInstance));
				
				//新建提交日志
				sqlList.add("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
						"FL_NODENAME,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,FL_NODEID) select "+startInstanceLogId+",FO_ID,'"+employee.getEm_code()+"',"
						+ "'"+employee.getEm_name()+"',"+nowTime+",'"+shortName+"','"+nodeName+"',0,'"+codeValue+"',"+id+ ","+nodeId
						+ " FROM FLOW_OPERATION WHERE FO_NODENAME='"+nodeName+"' AND FO_FDSHORTNAME='"+shortName+"' AND FO_ID="+btnid);
				sqlList.add("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
						+ "FLOW_INSTANCEROLE_SEQ.nextval,"
						+ "'"+employee.getEm_code()+"',"
						+ "'duty',"
						+ "'"+startInstanceLogId+"')");
			}else {
				//新建到决策流程实例
				FlowInstance toJudgeInstance = new FlowInstance();
				int toJudgeInstanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
				int toJudgeInstanceLogId = baseDao.getSeqId("FLOW_LOG_SEQ");
				String startTime = ObjectToString(baseDao.getFieldDataByCondition("flow_instance", "to_char(fi_starttime,'yyyy-MM-dd HH24:MI:ss')", 
						"fi_nodename='START' and FI_FDSHORTNAME='"+shortName+"' and FI_KEYVALUE="+id));
				
				toJudgeInstance.setFi_id(toJudgeInstanceId);
				toJudgeInstance.setFi_fdshortname(shortName);
				toJudgeInstance.setFi_nodeid(nodeId);
				toJudgeInstance.setFi_codevalue(ObjectToString(codeValue));
				toJudgeInstance.setFi_keyvalue(id);
				toJudgeInstance.setFi_handler(ObjectToString(map.get("duty")));
				toJudgeInstance.setFi_handlercode(ObjectToString(map.get("dutycode")));
				toJudgeInstance.setFi_nodename(nodeName);
				toJudgeInstance.setFi_startman(ObjectToString(map.get("creator")));
				toJudgeInstance.setFi_startmancode(ObjectToString(map.get("creatorcode")));
				toJudgeInstance.setFi_status("end");;
				toJudgeInstance.setFi_caller(caller);
				toJudgeInstance.setFi_keyfield(keyField);
				toJudgeInstance.setFi_time(DateUtil.format(new Date(), YMD_HMS));
				toJudgeInstance.setFi_starttime(startTime);
				toJudgeInstance.setFi_title(title);
				toJudgeInstance.setFi_flid(toJudgeInstanceLogId);
				sqlList.add(FlowInstanceInsertSql(toJudgeInstance));
				
				
				//新建提交日志
				//当前开启的流程与最近的流程时间差
				Object resultTime = baseDao.getFieldDataByCondition("(SELECT FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+codeValue+"' AND FI_STATUS='using') a, "
						+ "(select * from (SELECT ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN, FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+codeValue+"' AND FI_STATUS='end' ORDER BY FI_TIME DESC)where RN<=1 ) b",
						"(a.fi_time-b.fi_time)*24*60", "1=1");
				String timeStr = ObjectToString(resultTime);
				float stayTime=NumberUtil.subFloat(Float.valueOf(timeStr), 0);
				sqlList.add("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
						"FL_NODENAME,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,FL_NODEID) select "+toJudgeInstanceLogId+",FO_ID,'"+employee.getEm_code()+"',"
						+ "'"+employee.getEm_name()+"',"+nowTime+",'"+shortName+"','"+nodeName+"',"+stayTime+",'"+codeValue+"',"+id+ ","+nodeId
						+ " FROM FLOW_OPERATION WHERE FO_NODENAME='"+nodeName+"' AND FO_FDSHORTNAME='"+shortName+"' AND FO_ID="+btnid);
				sqlList.add("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
						+ "FLOW_INSTANCEROLE_SEQ.nextval,"
						+ "'"+employee.getEm_code()+"',"
						+ "'duty',"
						+ "'"+toJudgeInstanceLogId+"')");
			}
			baseDao.execute(sqlList);
			Object[] nextJudgeNode = baseDao.getFieldsDataByCondition("FLOW_OPERATION", "FO_NEXTNODEID", 
					"FO_FDSHORTNAME='"+shortName+"' AND FO_NODEID="+nodeId+" AND FO_ID =" + btnid );
			checkCondition(ObjectToInterger(nextJudgeNode[0]),id,String.valueOf(codeValue),caller,shortName,btnid,map,personCodes,keyField,title);
		}else {
			//判断是否第一节点
			boolean isStart = false;
			String commit = ObjectToString(baseDao.getFieldDataByCondition("FLOW_OPERATION", "FO_NAME", "FO_FDSHORTNAME='"+shortName+"' and FO_NODEID="+nodeId));
			if(nodeName.equals("START")&&commit.equals("commit")) {
				isStart = true;
			}
			int logId = baseDao.getSeqId("FLOW_LOG_SEQ");
			if(isStart) {
				StringBuffer start = new StringBuffer();
				start.append("INSERT INTO FLOW_INSTANCE(FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_CODEVALUE,FI_KEYVALUE,FI_HANDLER,FI_HANDLERCODE,FI_NODENAME,FI_STARTMAN,FI_STARTMANCODE,FI_STATUS,"
						+ "FI_CALLER,FI_KEYFIELD,FI_TIME,FI_STARTTIME,FI_TITLE,FI_FLID) values("
						+ "FLOW_INSTANCE_SEQ.nextval,"
						+ "'"+shortName+"',"
						+ nodeId+","
						+ "'"+codeValue+"',"
						+ id+","
						+ "'"+map.get("duty")+"',"
						+ "'"+map.get("dutycode")+"',"
						+ "'"+nodeName+"',"
						+ "'"+map.get("creator")+"',"
						+ "'"+map.get("creatorcode")+"',"
						+ "'end',"
						+ "'"+caller+"','"+keyField+"',"
				        + nowTime+","
				        + nowTime+","
				        + "'"+title + "',"
				        + logId + ")");
				baseDao.execute(start.toString());
			}
			String startTime = ObjectToString(baseDao.getFieldDataByCondition("flow_instance", "to_char(fi_starttime,'yyyy-MM-dd HH24:MI:ss')", "fi_nodename='START' and FI_FDSHORTNAME='"+shortName+"' and FI_KEYVALUE="+id));
			StringBuffer sql = new StringBuffer();
			int instanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
			sql.append("INSERT INTO FLOW_INSTANCE(FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_CODEVALUE,FI_KEYVALUE,FI_HANDLER,FI_HANDLERCODE,FI_NODENAME,FI_STARTTIME,FI_TIME,FI_STARTMAN,FI_STARTMANCODE,FI_STATUS,"
					+ "FI_CALLER,FI_KEYFIELD,FI_TITLE) values("
					+ instanceId+","
					+ "'"+shortName+"',"
					+ nextNode[0]+","
					+ "'"+codeValue+"',"
					+ id+","
					+ "'"+map.get("duty")+"',"
					+ "'"+map.get("dutycode")+"',"
					+ "'"+nextNode[1]+"',"
					+ (isStart?nowTime+",":(!startTime.equals("null")?"to_date('"+startTime+"','yyyy-MM-dd HH24:MI:ss'),":nowTime+","))
					+ nowTime+","
					+ "'"+map.get("creator")+"',"
					+ "'"+map.get("creatorcode")+"',"
					+ "'using',"
					+"'"+caller+"','"+keyField+"','"+title+"')");
			baseDao.execute(sql.toString());
			//插入角色权限
			StringBuffer sql2 = new StringBuffer();
			//遍历数组
			sql2.append("begin ");
			for (Map<String,Object> person : personCodes) {
				sql2.append("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
						+ "FLOW_INSTANCEROLE_SEQ.nextval,"
						+ "'"+person.get("code")+"',"
						+ "'"+person.get("type")+"',"
						+ "'"+instanceId+"');");
			}
			sql2.append(" end;");
			baseDao.execute(sql2.toString());
			//当前实例状态关闭 新增除外 同时绑定跳转流程的操作记录ID
			if(!nodeName.equals("START")) {
				baseDao.updateByCondition("FLOW_INSTANCE", "FI_STATUS='end',FI_FLID = "+logId, "FI_KEYVALUE="+id+" AND FI_NODEID="+nodeId);
			}
			float stayTime;
			if(isStart) {
				stayTime = 0;
			}else {
				//当前开启的流程与最近的流程时间差
				Object resultTime = baseDao.getFieldDataByCondition("(SELECT FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+codeValue+"' AND FI_STATUS='using') a, "
						+ "(select * from (SELECT ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN, FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+codeValue+"' AND FI_STATUS='end' ORDER BY FI_TIME DESC)where RN<=1 ) b",
						"(a.fi_time-b.fi_time)*24*60", "1=1");
				String timeStr = ObjectToString(resultTime);
				stayTime=NumberUtil.subFloat(Float.valueOf(timeStr), 0);
			}
			StringBuffer sqlLog = new StringBuffer();
			sqlLog.append("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
					"FL_NODENAME,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,FL_NODEID) select "+logId+",FO_ID,'"+employee.getEm_code()+"',"
					+ "'"+employee.getEm_name()+"',"+nowTime+",'"+shortName+"','"+nodeName+"',"+stayTime+",'"+codeValue+"',"+id+","+nodeId+" FROM FLOW_OPERATION WHERE FO_NODENAME='"+nodeName+
					"' AND FO_FDSHORTNAME='"+shortName+"' AND FO_ID="+btnid);
			baseDao.execute(sqlLog.toString());
			//关闭流程时时 关闭所有实例
			if(nextNode[1].equals("END")) {
				baseDao.updateByCondition("FLOW_INSTANCE", "FI_STATUS='end'", "FI_KEYVALUE="+id);
			}
		}
	}

	/**
	 * 决策处理
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @return
	 */
	@Transactional
	private boolean checkCondition(int nodeId, int id,String codeValue, String caller,String shortName,int btnid,Map<String,Object> handlerPerson,List<Map<String,Object>> personCodes,String keyField,String title) {
		//当前时间
		String nowTime = getSqlDate();
		//获取当前登录人
		Employee employee = SystemSession.getUser();
		//只允许一个条件通过
		int count = 0;
		List<Map<String,Object>> list = baseDao.queryForList("SELECT * FROM FLOW_OPERATION WHERE FO_TYPE='Judge' AND FO_NODEID="+nodeId);
		for (Map<String, Object> operation : list) {
			String condition = ObjectToString(operation.get("FO_CONDITION"));
			if(condition!=null && !"null".equals(condition)) {
				//判断当前条件会不会符合
				if(conditionJudge(caller,nodeId,id,condition)) {
					count++;
					if(count>1) {
						BaseUtil.showError("决策有多种执行操作，请检查决策条件是否冲突");
					}
					List<String> judgeSql = new ArrayList<String>();
					int judgeOperationId = ObjectToInterger(operation.get("FO_ID"));
					Object[] nextNode = baseDao.getFieldsDataByCondition("FLOW_OPERATION", "FO_NEXTNODEID,FO_NEXTNODENAME", 
							"FO_FDSHORTNAME='"+shortName+"' AND FO_NODEID="+nodeId+" AND FO_ID =" + judgeOperationId );
					
					int judgeLogId = baseDao.getSeqId("FLOW_LOG_SEQ");
					judgeSql.add("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
							"FL_NODENAME,FL_STAYTIME"+ ",FL_CODEVALUE,FL_KEYVALUE,FL_NODEID,FL_TYPE,FL_URL,FL_REMARK)  values("+judgeLogId+","+btnid+",'"+employee.getEm_code()+"',"
							+ "'"+employee.getEm_name()+"',"+nowTime+",'"+shortName+"','"+String.valueOf(operation.get("FO_NODENAME"))+"',0,'"+codeValue+"',"+id
							+ ","+judgeOperationId+",'judge',null,null"+ ")");
					
					boolean checkNextNextNode = baseDao.checkIf("flow_operation LEFT JOIN FLOW_NODE ON FO_NEXTNODEID=FN_ID", 
							"FO_NODEID="+ObjectToInterger(operation.get("FO_NODEID"))+" AND FN_TYPE='node'");
					if(checkNextNextNode) {
						String startTime = String.valueOf(baseDao.getFieldDataByCondition("flow_instance", "to_char(fi_starttime,'yyyy-MM-dd HH24:MI:ss')", "fi_nodename='START' and FI_FDSHORTNAME='"+shortName+"' and FI_KEYVALUE="+id));
						
						FlowInstance toNextInstance = new FlowInstance();
						int toNextInstanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
						
						toNextInstance.setFi_id(toNextInstanceId);
						toNextInstance.setFi_fdshortname(shortName);
						toNextInstance.setFi_nodeid(ObjectToInterger(nextNode[0]));
						toNextInstance.setFi_codevalue(ObjectToString(codeValue));
						toNextInstance.setFi_keyvalue(id);
						toNextInstance.setFi_handler(ObjectToString(handlerPerson.get("duty")));
						toNextInstance.setFi_handlercode(ObjectToString(handlerPerson.get("dutycode")));
						toNextInstance.setFi_nodename(ObjectToString(nextNode[1]));
						toNextInstance.setFi_startman(ObjectToString(handlerPerson.get("creator")));
						toNextInstance.setFi_startmancode(ObjectToString(handlerPerson.get("creatorcode")));
						toNextInstance.setFi_status("using");;
						toNextInstance.setFi_caller(caller);
						toNextInstance.setFi_keyfield(keyField);
						toNextInstance.setFi_time(DateUtil.format(new Date(), YMD_HMS));
						toNextInstance.setFi_starttime(startTime);
						toNextInstance.setFi_title(title);
						toNextInstance.setFi_flid(judgeLogId);
						judgeSql.add(FlowInstanceInsertSql(toNextInstance));
						
						
						for (Map<String,Object> person : personCodes) {
							judgeSql.add("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
									+ "FLOW_INSTANCEROLE_SEQ.nextval,"
									+ "'"+person.get("code")+"',"
									+ "'"+person.get("type")+"',"
									+ "'"+toNextInstanceId+"')");
						}
						baseDao.execute(judgeSql);
					}else{
						checkCondition(ObjectToInterger(nextNode[0]),id,codeValue,caller,shortName,judgeOperationId,handlerPerson,personCodes,keyField,title);
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 判断条件是否正确
	 * @return
	 */
	private boolean conditionJudge(String caller,int nodeId, int id,String condition) {

		Form form = getFormWithCloud(caller, false);
		
		String field=condition.substring(condition.indexOf("check(")+6, condition.lastIndexOf(")"));
		String result = getExpression(field,form,id);
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		
		try {
			return Boolean.valueOf(engine.eval(condition+result).toString());
		} catch (ScriptException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String getExpression(String condition,Form form,int id){
		condition=condition.replace(" ","");
		String[] conditionField = condition.split(",");
		StringBuffer sb=new StringBuffer();		 
		if(conditionField.length > 0)  
		{  
			for (String string : conditionField) {
				Object fieldData = baseDao.getFieldDataByCondition(form.getFo_table(),string , form.getFo_keyfield()+"="+id);
				sb.append("var ");
				sb.append(string);
				sb.append("='"+fieldData+"';");
			}
			sb.append("check("+condition+");");
		}  
		return sb.toString();
	}

	@Override
	public List<Map<String, Object>> getHistoryIntance(String id,String caller,String condition) {
		//获取流程版本
		String shortName = getShortName(caller);
		
		if(id.indexOf("IS")>0) {
			id = id.split("IS")[1];
		}else if(id.indexOf("=")>0){
			id = id.split("=")[1];
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		//判断是否派生流程
		boolean isDerive = baseDao.checkIf("Flow_instance", "fi_keyvalue= "+id+" and fi_type='Derive'");
		if(isDerive){
			//是否是第一条
			Object fi_status = baseDao.getFieldDataByCondition("Flow_instance", "fi_status","fi_keyvalue= "+id+" and fi_type='Derive'");
			if(String.valueOf(fi_status).equals("using")){
				list = baseDao.queryForList("select * from flow_instance LEFT JOIN FLOW_LOG ON FI_NODENAME=FL_NODENAME AND FL_KEYVALUE=FI_KEYVALUE "+
						" LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID "+
						" where FI_FDSHORTNAME='"+shortName+"' "+
						" and FI_KEYVALUE = '"+id+"'");
				return list;
			}else{//合并查询 将第一条 和后续正常流程拼接
				list = baseDao.queryForList("select * from (select * from flow_instance LEFT JOIN FLOW_LOG ON FI_NODENAME=FL_NODENAME AND FL_KEYVALUE=FI_KEYVALUE "+
						" LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID "+
						" where FI_KEYVALUE = '"+id+"' and fi_type='Derive' and fo_type = 'Flow' "+
						" union "+
						" select * from flow_instance LEFT JOIN FLOW_LOG ON fi_flid = fl_id "+
						" LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID  "+
						" where fi_id in (select fi_id from  "+
						" (select min(fi_id) fi_id from flow_instance where FI_KEYVALUE = '"+id+"' and  fi_id<  "+
						" (select min(fi_id) from flow_instance where FI_KEYVALUE = '"+id+"' and fi_nodename =(select fi_nodename from flow_instance where FI_KEYVALUE = '"+id+"' and fi_status = 'using'))  "+ 
						" group by fi_nodename order by fi_id) a) )");
				return list;
			}
		}
		//判断是否是结束流程
		Object fi_id = baseDao.getFieldDataByCondition("flow_instance","fi_id","FI_KEYVALUE = '"+id+"' and fi_status = 'using'");
		if(String.valueOf(fi_id).length()<1||String.valueOf(fi_id).equals("null")){
			list = baseDao.queryForList("select * from flow_instance LEFT JOIN FLOW_LOG ON fi_flid = fl_id "+
										" LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID "+
										" where FI_FDSHORTNAME='"+shortName+"' "+
										" and fi_id in (select * from (select min(fi_id) fi_id from flow_instance where FI_KEYVALUE = '"+id+"' "+
						  				" group by fi_nodename) a) order by fi_id ");
			return list;
		}
		if(condition==null){
			list = baseDao.queryForList("select * from flow_instance LEFT JOIN FLOW_LOG ON fi_flid = fl_id "+
					" LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID "+
					" where FI_FDSHORTNAME='"+shortName+"' AND fi_id in (select fi_id from "+
					" (select min(fi_id) fi_id from flow_instance where FI_KEYVALUE = '"+id+"' and  fi_id< "+
					" (select min(fi_id) from flow_instance where FI_KEYVALUE = '"+id+"' and fi_nodename =(select fi_nodename from flow_instance where FI_KEYVALUE = '"+id+"' and fi_status = 'using')) "+ 
					" group by fi_nodename order by fi_id) a) order by fi_id ");
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getInstance(String id,String caller,String condition) {
		//获取流程版本
		String shortName = getShortName(caller);
		if(id.indexOf("IS")>0) {
			id = id.split("IS")[1];
		}else if(id.indexOf("=")>0){
			id = id.split("=")[1];
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(condition==null){
			list = baseDao.queryForList("Select * from (SELECT DISTINCT FL_ID,FI_NODENAME,FO_NAME,FL_NAME,FL_CODE,FL_DEALTIME,FL_STAYTIME,FO_TYPE FROM FLOW_INSTANCE "
									  + "LEFT JOIN FLOW_LOG ON FI_NODENAME=FL_NODENAME AND FL_KEYVALUE=FI_KEYVALUE "
									  + "LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID  WHERE FI_FDSHORTNAME='"+shortName+"' AND FI_KEYVALUE='"+id+"' and (FO_TYPE = 'Turn' or FO_TYPE = 'Flow')) A ORDER BY A.FL_ID");
		}else if(condition.equals("getUsingInstance")){
			list = baseDao.queryForList("SELECT * FROM FLOW_INSTANCE LEFT JOIN FLOW_DEFINE ON FD_SHORTNAME = FI_FDSHORTNAME WHERE FI_FDSHORTNAME='"+shortName+"' AND FI_KEYVALUE='"+id+"' and FI_STATUS='using' and FD_STATUS= 'enable'");
			//无在使用的流程 为结束流程
			if(list.size()<1){
				list = baseDao.queryForList("select * from (SELECT ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN,fi.*,fd.* from flow_instance fi LEFT JOIN FLOW_DEFINE fd ON fd.FD_SHORTNAME = fi.FI_FDSHORTNAME where fi.FI_FDSHORTNAME='"+shortName+"' AND fi.FI_KEYVALUE='"+id+"' and fd.FD_STATUS= 'enable' order by fi_id desc) where rn=1");
			}
		}
		return list;
	}
	
	@Override
	public Map<String, Object> getRelation(int nodeId, String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//获取流程版本
		String shortName = getShortName(caller);
		
		Form form = getFormWithCloud(caller, false);

		List<Map<String,Object>> task = new ArrayList<Map<String,Object>>();
		String codeField = form.getFo_codefield();
		String table = form.getFo_table();
		String keyField = form.getFo_keyfield();
		Object codevalue = baseDao.getFieldDataByCondition(table, codeField, keyField+"="+id);
		task = baseDao.queryForList("select id, name, description, startdate, enddate," + 
				"resourcename, resourcecode, resourceemid, type, confirmor, confirmorid, sourcelink, sourcecode," + 
				"sourcecaller, handstatus, handstatuscode, statuscode, recorder, recorderid, sourceid from projecttask where sourcecaller='"
				+ caller + "' and sourcecode='" + codevalue + "'");
		modelMap.put("task", task);
		
		List<Map<String,Object>> flow = new ArrayList<Map<String,Object>>();
		flow = baseDao.queryForList("SELECT FI_KEYFIELD,FR_NAME,FR_FDSHORTNAME,FR_KEYVALUE,FR_RELATIONCODE,FR_RELATIONID,FR_CALLER,FR_CALLER,FR_NODENAME,FO_TYPE,FO_URL,FI_TITLE FROM FLOW_RELATION "
				+ "LEFT JOIN FLOW_OPERATION ON FR_FOID=FO_ID LEFT JOIN FLOW_INSTANCE ON FR_RELATIONID=FI_KEYVALUE  WHERE FR_FDSHORTNAME='"+shortName+"' AND FO_TYPE='Flow' AND FR_KEYVALUE="+id+" and fi_type = 'Derive'");
		for (Map<String, Object> fr : flow) {
			Object foid = fr.get("FR_FOID");
			Object url = baseDao.getFieldDataByCondition("FLOW_OPERATION", "FO_URL", "fo_id="+foid);
			fr.put("url", url);
		}
		modelMap.put("flow", flow);
		
		List<Map<String,Object>> update = new ArrayList<Map<String,Object>>();
		update = baseDao.queryForList("SELECT FR_NAME,FR_FDSHORTNAME,FR_KEYVALUE,FR_RELATIONCODE,FR_RELATIONID,FR_CALLER,FR_CALLER,FR_NODENAME,FO_TYPE,FO_URL FROM FLOW_RELATION "
				+ "LEFT JOIN FLOW_OPERATION ON FR_FOID=FO_ID WHERE FR_NODEID="+nodeId+" AND FR_FDSHORTNAME='"+shortName+"' AND FO_TYPE='Update' AND FR_KEYVALUE="+id);
		modelMap.put("update", update);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public void commit(int nodeId, int id, String caller,String formStore,String Status) {
		//获取流程版本
		String shortName = getShortName(caller);
		
		//获取form数据
		Form form = getFormWithCloud(caller, false);
		String tableName = form.getFo_table();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//将附件信息转到附件表中显示
		saveFileIntoGrid(store,caller,form.getFo_id(),id);
		
		//保存操作
		if(Status.equals("text")){
			Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statuscodefield" },
					"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
			if (objs != null) {
				String tab = (String) objs[0];
				String keyF = (String) objs[1];
				if (tab != null && keyF != null) {
					if (tab.contains(" ")) {
						tab = tab.substring(0, tab.indexOf(" "));
					}
					String formSql = SqlUtil.getUpdateSqlByFormStore(store, tab, keyF);
					baseDao.execute(formSql);
				}
			}
		}else{
			String formSql = SqlUtil.getInsertSqlByFormStore(store, tableName, new String[] {}, new Object[] {});
			baseDao.execute(formSql);
		}
		
		//所有的人员编码以及角色类型
		List<Map<String,Object>> personCodes = new ArrayList<Map<String,Object>>();
		Map<String,Object> personMap = new HashMap<String,Object>();
		// 获取四种角色的人员编号：责任人、参与者、读者、创建者
		Map<String,Object> map = new HashMap<String,Object>();
		
		//责任人
		Object[] op = baseDao.getFieldsDataByCondition("flow_operation", "fo_id,fo_groupname", "fo_fdshortname='"+shortName+"' and fo_type='Turn' and fo_name='commit'");
		List<Object> dealMan = baseDao.getFieldDatasByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op[1]+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='duty'"); 
		Object duty = new Object();
		String dutyCode = "";
		if(dealMan.size()==1){//单责任人
			dutyCode = ObjectToString(store.get(dealMan.get(0)));
			if(dutyCode.equals("")){
				Object[] defaultDuty = baseDao.getFieldsDataByCondition("flow_define", "fd_defaultduty,fd_defaultdutycode", "fd_shortname = '"+shortName.split("-")[0]+"'");
				dutyCode = String.valueOf(defaultDuty[1]);
				duty = String.valueOf(defaultDuty[0]);
			}else{
				duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+dutyCode+"'");
			}
			//收集责任人
			personMap = new HashMap<String,Object>();
			personMap.put("code", dutyCode);
			personMap.put("type", "duty");
			personCodes.add(personMap);
		}else{//多责任人
			int num = 0;
			for (Object object : dealMan) {
				dutyCode = String.valueOf(store.get(object));
				if("".equals(dutyCode)||dutyCode==null||dutyCode.length()==0){
					num++;//记录空责任人
				}else{
					duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+dutyCode+"'");
					//收集责任人
					personMap = new HashMap<String,Object>();
					personMap.put("code", dutyCode);
					personMap.put("type", "duty");
					personCodes.add(personMap);
				}
			}
			if(num==dealMan.size()){
				BaseUtil.showError("当前有多个责任人，至少填写一个责任人字段");
			}
		}
		//记录流程责任人  （多责任人随意）
		map.put("duty", duty);
		map.put("dutycode", dutyCode);
		
		//参与者（多）
		Object actorMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op[1]+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='actor'"); 
		String actorCode = String.valueOf(store.get(actorMan));
		String[] actorArr = actorCode.split("#");
		//收集参与者
		if(!actorCode.equals("null")&&actorArr.length>0){
			for (String str : actorArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "actor");
				personCodes.add(personMap);
			}
		}
		
		//读者（多）
		Object readerMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op[1]+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='reader'"); 
		String readerCode = String.valueOf(store.get(readerMan));
		String[] readerArr = readerCode.split("#");
		//收集读者
		if(!readerCode.equals("null")&&readerArr.length>0){
			for (String str : readerArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "reader");
				personCodes.add(personMap);
			}
		}
		
		//创建者(单)
		//获取当前登录人
		Employee employee = SystemSession.getUser();
		map.put("creator", employee.getEm_name());
		map.put("creatorcode", employee.getEm_code());
		//收集创建人
		personMap = new HashMap<String,Object>();
		personMap.put("code", employee.getEm_code());
		personMap.put("type", "creator");
		personCodes.add(personMap);
		
		personCodes = removeDuplicate(personCodes);
		
		//标题
		Object titlefield = baseDao.getFieldDataByCondition("form left join formdetail on fo_id = fd_foid", "FD_FIELD", "fo_caller='"+caller+"' and FD_LOGICTYPE='title'"); 
		Object title = baseDao.getFieldDataByCondition(tableName, String.valueOf(titlefield), form.getFo_keyfield()+"='"+id+"'"); 
		
		createInstance(nodeId,id, caller,Integer.valueOf(String.valueOf(op[0])),map,personCodes,String.valueOf(title));  
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void save(int nodeId, int id, String caller,String formStore,int btnid) {
		//获取流程版本
		String shortName = getShortName(caller);
		//获取form配置
		Form form = getFormWithCloud(caller, false);
		String tableName = form.getFo_table();
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql =  SqlUtil.getUpdateSqlByFormStore(store, tableName, form.getFo_keyfield());
		Object codeValue = baseDao.getFieldDataByCondition(tableName, form.getFo_codefield(), form.getFo_keyfield()+"="+id);
		baseDao.execute(formSql);
		
		//所有的人员编码以及角色类型
		List<Map<String,Object>> personCodes = new ArrayList<Map<String,Object>>();
		Map<String,Object> personMap = new HashMap<String,Object>();
		// 获取四种角色的人员编号：责任人、参与者、读者、创建者
		Map<String,Object> map = new HashMap<String,Object>();
		
		//责任人
		Object op = baseDao.getFieldDataByCondition("flow_operation", "fo_groupname", "fo_id="+btnid);
		List<Object> dealMan = baseDao.getFieldDatasByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='duty'"); 
		Object duty = new Object();
		String dutyCode = "";
		if(dealMan.size()==1){//单责任人
			dutyCode = ObjectToString(store.get(dealMan.get(0)));
			if(dutyCode.equals("")){
				Object[] defaultDuty = baseDao.getFieldsDataByCondition("flow_define", "fd_defaultduty,fd_defaultdutycode", "fd_shortname = '"+shortName.split("-")[0]+"'");
				dutyCode = String.valueOf(defaultDuty[1]);
				duty = String.valueOf(defaultDuty[0]);
			}else{
				duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+dutyCode+"'");
			}
			//收集责任人
			personMap = new HashMap<String,Object>();
			personMap.put("code", dutyCode);
			personMap.put("type", "duty");
			personCodes.add(personMap);
		}else{//多责任人
			int num = 0;
			for (Object object : dealMan) {
				dutyCode = String.valueOf(store.get(object));
				if("".equals(dutyCode)||dutyCode==null||dutyCode.length()==0){
					num++;//记录空责任人
				}else{
					duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+dutyCode+"'");
					//收集责任人
					personMap = new HashMap<String,Object>();
					personMap.put("code", dutyCode);
					personMap.put("type", "duty");
					personCodes.add(personMap);
				}
			}
			if(dealMan.size()>0&&num==dealMan.size()){
				BaseUtil.showError("当前有多个责任人，至少填写一个责任人字段");
			}
		}
		//记录流程责任人  （多责任人随意）
		map.put("duty", duty);
		map.put("dutycode", dutyCode);
		
		//收集责任人
		personMap = new HashMap<String,Object>();
		personMap.put("code", dutyCode);
		personMap.put("type", "duty");
		personCodes.add(personMap);
		
		//参与者（多）
		Object actorMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='actor'"); 
		String actorCode = ObjectToString(store.get(actorMan));
		String[] actorArr = actorCode.split("#");
		//收集参与者
		if(!actorCode.equals("null")&&actorArr.length>0){
			for (String str : actorArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "actor");
				personCodes.add(personMap);
			}
		}
		
		//读者（多）
		Object readerMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='reader'"); 
		String readerCode = ObjectToString(store.get(readerMan));
		String[] readerArr = readerCode.split("#");
		//收集读者
		if(!readerCode.equals("null")&&readerArr.length>0){
			for (String str : readerArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "reader");
				personCodes.add(personMap);
			}
		}
		
		//创建者
		Object[] creator = baseDao.getFieldsDataByCondition("flow_instance", "FI_startman,FI_startmancode", "FI_FDSHORTNAME='"+shortName+"' and FI_NODENAME='START' and FI_status='end' and fi_keyvalue="+id);
		if(creator == null){
			creator = baseDao.getFieldsDataByCondition("flow_instance", "FI_startman,FI_startmancode", "FI_FDSHORTNAME='"+shortName+"' and fi_keyvalue="+id);
		}
		map.put("creator", ObjectToString(creator[0]));
		map.put("creatorcode", ObjectToString(creator[1]));
		//收集创建者
		personMap = new HashMap<String,Object>();
		personMap.put("code", ObjectToString(creator[1]));
		personMap.put("type", "creator");
		personCodes.add(personMap);
		
		//标题
		Object titlefield = baseDao.getFieldDataByCondition("form left join formdetail on fo_id = fd_foid", "FD_FIELD", "fo_caller='"+caller+"' and FD_LOGICTYPE='title'"); 
		Object title = baseDao.getFieldDataByCondition(tableName, String.valueOf(titlefield), form.getFo_keyfield()+"='"+id+"'"); 
		
		personCodes = removeDuplicate(personCodes);
		
		//多个责任人保存时只在最后一个责任人保存时创实例
		Employee employee=SystemSession.getUser();
		boolean allCheck = baseDao.checkIf("flow_instance left join flow_instancerole on fi_id = fir_fiid", "fi_keyvalue = '"+id+"' and fi_status = 'using' and fir_type = 'duty' and FIR_CONFIRM = '0'");
		if(allCheck){
			List<Object[]> dutys = baseDao.getFieldsDatasByCondition("flow_instance left join flow_instancerole on fi_id = fir_fiid", new String[]{"FIR_ID","FIR_MANCODE"}, "fi_keyvalue = '"+id+"' and fi_status = 'using' and fir_type = 'duty' and FIR_CONFIRM = '0'");
			if(dutys.size()>1){//多个责任人
				for (Object[] obj : dutys) {
					if(obj[1].equals(employee.getEm_code())){
						baseDao.updateByCondition("flow_instancerole", "FIR_CONFIRM = '1'", "FIR_ID = "+obj[0]);
						StringBuffer sqlLog = new StringBuffer();
						String nowTime = getSqlDate();
						//获取当前节点名称
						String nodeName= ObjectToString(baseDao.getFieldDataByCondition("Flow_Node", "FN_NODENAME", "FN_FDSHORTNAME='"+shortName+"' and FN_ID="+nodeId));
						sqlLog.append("INSERT INTO FLOW_LOG(FL_ID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME,"
								+ "FL_NODENAME,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,FL_NODEID,FL_TYPE,FL_REMARK) values "
								+ "(FLOW_LOG_SEQ.NEXTVAL,'"+employee.getEm_code()+"','"+employee.getEm_name()+"',"
								+ nowTime+",'"+shortName+"','"+nodeName+"','0','"+codeValue+"','"+id+"','"+nodeId+"','moreDuty','并发审核')");
						baseDao.execute(sqlLog.toString());
					}
				}
			}else{//单责任人
				baseDao.updateByCondition("flow_instancerole", "FIR_CONFIRM = '1'", "FIR_ID = "+dutys.get(0)[0]);
				createInstance(nodeId,id, caller,btnid,map,personCodes, String.valueOf(title));
			}
		}else{
			createInstance(nodeId,id, caller,btnid,map,personCodes, String.valueOf(title));
		}
	}
	

	@Override
	public Map<String,Object> getNodeId(String id, String caller) {
		if(id.indexOf("IS")>0) {
			id = id.split("IS")[1];
		}else if(id.indexOf("=")>0){
			id = id.split("=")[1];
		}
		//获取流程版本
		String shortName = getShortName(caller);
		
		boolean existIns = baseDao.checkIf("FLOW_INSTANCE",  "FI_STATUS='using' AND FI_KEYVALUE='"+id+"' AND FI_FDSHORTNAME='"+shortName+"'");
		Map<String,Object> modelMap = new HashMap<String,Object>();

		if(existIns) {
			Object[] node = baseDao.getFieldsDataByCondition("FLOW_INSTANCE", "FI_NODEID,FI_CODEVALUE", "FI_STATUS='using' AND FI_KEYVALUE='"+id+"' and FI_FDSHORTNAME='"+shortName+"'");
			modelMap.put("nodeId",node[0]);
			modelMap.put("codeValue",node[1]);
			return modelMap;
		}else {
			String status = "END";
			//区分新增和结束流程
			if(id.equals("")&&id.length()<1){
				status = "START";
			}
			Object[] node = baseDao.getFieldsDataByCondition("FLOW_INSTANCE", "FI_NODEID,FI_CODEVALUE", "FI_NODENAME='"+status+"' AND FI_KEYVALUE='"+id+"' and FI_FDSHORTNAME='"+shortName+"'");
			Object nodeId = baseDao.getFieldDataByCondition("FLOW_NODE LEFT JOIN FLOW_DEFINE ON FLOW_DEFINE.FD_SHORTNAME=FLOW_NODE.FN_FDSHORTNAME", "FN_ID", "FD_CALLER='"+caller+"' AND FN_NODENAME='"+status+"' AND FD_STATUS='enable'");
			
			if(nodeId==null){//草稿界面读取start
				status = "START";
				node = baseDao.getFieldsDataByCondition("FLOW_INSTANCE", "FI_NODEID,FI_CODEVALUE", "FI_NODENAME='"+status+"' AND FI_KEYVALUE='"+id+"' and FI_FDSHORTNAME='"+shortName+"'");
				nodeId = baseDao.getFieldDataByCondition("FLOW_NODE LEFT JOIN FLOW_DEFINE ON FLOW_DEFINE.FD_SHORTNAME=FLOW_NODE.FN_FDSHORTNAME", "FN_ID", "FD_CALLER='"+caller+"' AND FN_NODENAME='"+status+"' AND FD_STATUS='enable'");
				modelMap.put("status","text");
			}
			
			modelMap.put("nodeId",nodeId);
			if(node!=null&&node.length>0) {
				modelMap.put("codeValue",node[1]);
			}else {
				modelMap.put("codeValue",null);
			}
			return modelMap;
		}
	}

	/**
	 * 删除单据数据、流程实例、操作日志、关联
	 * 
	 * @param id 单据ID
	 * @param caller 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void delete(String list) {
		List<Map<Object,Object>> ls = BaseUtil.parseGridStoreToMaps(list);
		//根据caller分组
		List<Map<String,Object>> DelObject = new ArrayList<Map<String,Object>>();
		for (Map<Object, Object> map : ls) {
			Map<String,Object> modelMap = new HashMap<String,Object>();
			String ids = "";
			for (Object a : (List) map.get("data")) {
				ids = ids + ObjectToString(a) + ",";
			}
			ids = ids.substring(0, ids.length()-1);
			modelMap.put("caller", map.get("caller"));
			modelMap.put("ids", ids);
			DelObject.add(modelMap);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("BEGIN ");
		//根据caller删除
		for (Map<String, Object> map : DelObject) {
			String ids = ObjectToString(map.get("ids"));
			String caller = ObjectToString(map.get("caller"));
			Form form = getFormWithCloud(caller, false);
			String tableName = form.getFo_table();
			String keyField = form.getFo_keyfield();
			//查询角色表ids
			String instanceids = "";
			List<Object> instanceid = baseDao.getFieldDatasByCondition("FLOW_INSTANCE", "FI_ID", "FI_KEYVALUE in ("+ids+")");
			for (Object obj : instanceid) {
				instanceids = instanceids + ObjectToString(obj) + ",";
			}
			instanceids = instanceids.substring(0, instanceids.length()-1);
			sql.append("DELETE FROM "+tableName+" WHERE "+keyField+" in ("+ids+");");
			sql.append("DELETE FROM FLOW_INSTANCEROLE WHERE FIR_FIID in ("+instanceids+");");
			sql.append("DELETE FROM FLOW_INSTANCE WHERE FI_KEYVALUE in ("+ids+");");
			sql.append("DELETE FROM FLOW_RELATION WHERE FR_KEYVALUE in ("+ids+");");
			sql.append("DELETE FROM FLOW_RELATION WHERE FR_RELATIONID in ("+ids+");");
			sql.append("DELETE FROM FLOW_LOG WHERE FL_KEYVALUE in ("+ids+");");
			sql.append("DELETE FROM FLOW_FILE WHERE FF_KEYVALUE in ("+ids+");");
		}
		sql.append("COMMIT; EXCEPTION WHEN OTHERS THEN ROLLBACK; end;");
		baseDao.execute(sql.toString());
	}

	@Override
	public Map<String, Object> getDerive(String caller,int foid,int id) {
		Form form = getFormWithCloud(caller, false);

		Object[] operation = baseDao.getFieldsDataByCondition("FLOW_OPERATION", "FO_TYPE,FO_FLOWNAME,FO_NAME,FO_URL,FO_FLOWCALLER,FO_FLOWNODEID,FO_GROUPNAME", "FO_ID="+foid);
		
		Map<String, Object> modelMap = new HashMap<String,Object>();
		String type = ObjectToString(operation[0]);
		if(type.equals("Task")) {
			List<Map<String, Object>> tranferResult = new ArrayList<Map<String,Object>>();
			
			String table = form.getFo_table();
			String keyField = form.getFo_keyfield();
			String taskCaller = String.valueOf(baseDao.getFieldDataByCondition("FLOW_TRANSFER", "FT_CALLER", "ROWNUM=1 AND FT_FOID="+foid));
			if(form!=null) {

				List<Object[]> taskTranfer = baseDao.getFieldsDatasByCondition("FLOW_TRANSFER", new String[] {"FT_FROM","FT_TO"}, "FT_FOID="+foid);
				for (Object[] objects : taskTranfer) {
					Object result = baseDao.getFieldDataByCondition(table, String.valueOf(objects[0]), keyField+"="+id);
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("name", String.valueOf(objects[1]));
					map.put("value", result);
					tranferResult.add(map);
				}
			}
			modelMap.put("transfer", tranferResult);
			modelMap.put("taskCaller", taskCaller);
			String codeField = form.getFo_codefield();
			Object codevalue = baseDao.getFieldDataByCondition(table, codeField, keyField+"="+id);
			modelMap.put("codevalue", codevalue);
			modelMap.put("success", true);
		}
		if(type.equals("Flow")) {
			Map<String, Object> flowResult = new HashMap<String,Object>();

			String groups = getActivatePanel(ObjectToString(operation[5]), ObjectToString(operation[4]));
			
			if(groups.equals("")) {
				BaseUtil.showError("派生流程节点未配置界面");
			}else {
				flowResult.put("FlowCaller", ObjectToString(operation[4]));
				flowResult.put("nodeId", ObjectToInterger(operation[5]));
				flowResult.put("groupname", ObjectToString(operation[6]));
				flowResult.put("btnid", foid);
			}
			
			modelMap.put("transfer", flowResult);
			modelMap.put("success", true);
		}
		return modelMap;
	}
	
	@Override
	public Map<String, Object> getRole(String caller,String nodeId,String id) {
		//获取当前登录人
		Employee employee = SystemSession.getUser();
		String code = employee.getEm_code();
		//获取流程版本
		String shortName = getShortName(caller);
		Map<String, Object> modelMap = new HashMap<String,Object>();
		List<Object[]> data = baseDao.getFieldsDatasByCondition("FLOW_INSTANCE LEFT JOIN FLOW_INSTANCEROLE R ON FI_ID = R.FIR_FIID", new String[]{"FIR_TYPE","FIR_CONFIRM"}, " FI_STATUS='using' AND FI_FDSHORTNAME = '"+shortName+"' and FI_KEYVALUE = "+id+" and R.FIR_MANCODE = '"+code+"'");
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Object[] obj : data) {
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("type",obj[0]);
			map.put("confirm",obj[1]);
			datas.add(map);
		}
		modelMap.put("data", datas);
		return modelMap;
	}
	
	public boolean isPower(String codes,String code){
		if(codes.length()>0){
			String[] str = codes.split("#");
			for (String string : str) {
				if(string.equals(code)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public List<Map<String, Object>> getAddFlow() {
		List<Map<String, Object>> modelMap = new ArrayList<Map<String, Object>>();
		SqlRowList rs = new SqlRowList();
		rs = baseDao.queryForRowSet(
			"SELECT FD_NAME AS NAME,FD_CALLER AS CALLER FROM FLOW_DEFINE WHERE FD_STATUS = 'enable' ORDER BY FD_ID DESC");
		if (rs.next()) {
			modelMap = rs.getResultList();
		}
		return modelMap;
	}

	public HSSFWorkbook downLoadAsExcel(String caller, int id, int nodeId, Employee employee, String language){

		boolean checked = baseDao.checkIf("flow_node", "fn_id="+nodeId);
		if(checked){
			//获取流程版本
			String shortName = getShortName(caller);

			Object groupConfig = baseDao.getFieldDataByCondition("flow_node", "fn_groups", "fn_id="+nodeId);
			JSONArray jsonArray = JSON.parseArray(String.valueOf(groupConfig));
			int length = jsonArray.size();
			Map<String, Object> map = null;
			
			Form form = getFormWithCloud(caller, false);
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, employee.getEm_master());
			List<FormDetail> formDetails = form.getFormDetails();
			
			Map<String, Object> formData =baseDao.getFormData(form, form.getFo_keyfield() + "=" + id);
			List<Map<String, Object>> groupList = baseDao.queryForList("select * from flow_groupconfig where fgc_fdshortname='"+shortName+"' order by fgc_groupname,fgc_detno");
			//遍历groupconfig，重置formDetails的序号
			/*for (FormDetail detail : formDetails) {
				for(Map<String, Object> groupMap : groupList){
					if(groupMap.get("FGC_DETNO")!=null){
						if(detail.getFd_field().equals(groupMap.get("FGC_FIELD"))){
							detail.setFd_detno((int)groupMap.get("FGC_DETNO"));
						}
					}
				}
			}*/
			
			//声明一个工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();
			for(int i = 0; i < length; i++){
				int rowIndex = 0;
				map = JSON.parseObject(ObjectToString(jsonArray.get(i)));
				String groupName = ObjectToString(map.get("name"));
				// 生成一个表格
				HSSFSheet sheet = workbook.createSheet(groupName);
				sheet.createFreezePane(0, 1);// 固定标题
				HSSFCellStyle labelStyle = getCellStyle(workbook, "label");
				HSSFCellStyle fieldStyle = getCellStyle(workbook, "field");
				fieldStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);
				for (int j = 0; j < 4; j++) {
					if (j % 2 == 1) {
						sheet.setColumnWidth(j, 7700);
					} else
						sheet.setColumnWidth(j, 3850);
				}
				HSSFCellStyle titleStyle = getCellStyle(workbook, "title");
				
				// 指定合并区域
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (short) 3));
				sheet = createHeaderCell(sheet, labelStyle, fieldStyle);		//
				HSSFRow row = sheet.createRow(3);
				row.setHeight((short) 400);
				HSSFCell cell = row.createCell(0);
				cell.setCellType(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(new HSSFRichTextString(groupName));
				cell.setCellStyle(titleStyle);
				
				// form分组
				int count = 1;
				if (count == 1) {// 无分组写入
					for(Map<String, Object> groupMap : groupList){
						for (FormDetail detail : formDetails) {
							if(groupName.equals(ObjectToString(groupMap.get("FGC_GROUPNAME")))){
								if(ObjectToString(groupMap.get("FGC_FIELD")).equals(detail.getFd_field())){
									if (detail.getFd_type() != null && !detail.getFd_type().equals("H") && detail.getFd_columnwidth() != 0) {
										row = getRow(sheet, rowIndex / 2 + 4);
										if("DF".equals(detail.getFd_type())){
											detail.setFd_columnwidth((float)4.0);
										}
										boolean autoCreate = false;		//用作出现columnwidth=4时，换行处理
										if(detail.getFd_columnwidth() == 4.0){
											if(rowIndex % 2 != 0){
												rowIndex++;
												row = getRow(sheet, rowIndex / 2 + 4);
											}
											if("TA".equals(detail.getFd_type())){
												row.setHeight((short) 1200);
											}
											//合并单元格  
											CellRangeAddress cra =new CellRangeAddress(rowIndex / 2 + 4, rowIndex / 2 + 4, 1, (short) 3); // 起始行, 终止行, 起始列, 终止列  
											sheet.addMergedRegion(cra);
											// 使用RegionUtil类为合并后的单元格添加边框  
									        RegionUtil.setBorderBottom(1, cra, sheet, workbook); // 下边框  
									        RegionUtil.setBorderLeft(1, cra, sheet, workbook); // 左边框  
									        RegionUtil.setBorderRight(1, cra, sheet, workbook); // 有边框  
									        RegionUtil.setBorderTop(1, cra, sheet, workbook); // 上边框  
											autoCreate = true;
										}
										createCellByFormDetail(row, labelStyle, fieldStyle, detail, combos, formData.get(detail.getFd_field()), rowIndex, language);
										rowIndex++;
										if(autoCreate)
											rowIndex++;
									}
								}
							}
						}
					}
				}
				
			}
			/*增加固定的两栏：操作记录和关联*/
			workbook = createSpecialSheet(workbook, shortName, id, caller, nodeId);
			
			return workbook;
		}else{
			BaseUtil.showError("excel下载错误");
		}
		return null;
		
		
	}
	
	/**
	 * 创建导出头，输出人、输出日期
	 * @param sheet				HSSFSheet页
	 * @param labelStyle		标签样式
	 * @param fieldStyle		值样式
	 * @return
	 */
	private HSSFSheet createHeaderCell(HSSFSheet sheet, HSSFCellStyle labelStyle, HSSFCellStyle fieldStyle){
		//第0行  输出人
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellStyle(labelStyle);
		cell.setCellValue("输出人");
		cell = row.createCell(1);
		cell.setCellStyle(fieldStyle);
		cell.setCellValue(SystemSession.getUser().getEm_name());
		//第1行  输出日期
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellStyle(labelStyle);
		cell.setCellValue("输出日期");
		cell = row.createCell(1);
		cell.setCellStyle(fieldStyle);
		cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
		return sheet;
	}
	
	/**
	 * 创建特殊的两个界面导出,流程操作和关联界面
	 * @param workbook		HSSFWorkbook
	 * @param shortName		版本简称
	 * @param id			单据id
	 * @param caller		caller
	 * @param nodeId		节点ID
	 * @return
	 */
	private HSSFWorkbook createSpecialSheet(HSSFWorkbook workbook,String shortName, int id, String caller, int nodeId){
		HSSFCellStyle labelStyle = getCellStyle(workbook, "label");
		HSSFCellStyle fieldStyle = getCellStyle(workbook, "field");
		fieldStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);
		//操作记录sheet
		HSSFSheet sheet = workbook.createSheet("流程操作");
		sheet.createFreezePane(0, 1);// 固定标题
		sheet = createHeaderCell(sheet, labelStyle, fieldStyle);
		HSSFRow row = sheet.createRow(3);
		for(int k = 0; k < 7; k++){
			if(k == 0){
				sheet.setColumnWidth(k, 3000);
			}else{
				sheet.setColumnWidth(k, 6000);
			}
		}
		HSSFCellStyle titleStyle = getCellStyle(workbook, "title");
		row.setHeight((short) 400);
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (short) 6));
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString("流程操作"));
		cell.setCellStyle(titleStyle);
		StringBuffer flowLog = new StringBuffer();
		flowLog.append("select FI_NODENAME,name,FL_NAME,FL_CODE,FL_DEALTIME,nvl(FL_STAYTIME,0) from (select * from (");
		flowLog.append("SELECT FL_ID,FI_NODENAME,FO_NAME as name,FL_NAME,FL_CODE,TO_CHAR(FL_DEALTIME,'yyyy-MM-dd HH24:mm:ss') FL_DEALTIME,FL_STAYTIME "
				+ "FROM FLOW_INSTANCE LEFT JOIN FLOW_LOG ON FI_NODENAME=FL_NODENAME AND FL_KEYVALUE=FI_KEYVALUE LEFT JOIN FLOW_OPERATION "
				+ "ON FL_FOID=FO_ID  WHERE FI_FDSHORTNAME='"+shortName+"' AND FI_KEYVALUE='"+id+"' and FO_TYPE='Turn'");
		flowLog.append(" union ");
		flowLog.append("SELECT FL_ID,FI_NODENAME,Fl_remark as name,FL_NAME,FL_CODE,TO_CHAR(FL_DEALTIME,'yyyy-MM-dd HH24:mm:ss') " + 
				"FL_DEALTIME,FL_STAYTIME FROM FLOW_INSTANCE LEFT JOIN FLOW_LOG  " + 
				"ON FI_NODENAME=FL_NODENAME AND FL_KEYVALUE=FI_KEYVALUE LEFT JOIN FLOW_OPERATION ON FL_FOID=FO_ID " + 
				"WHERE FL_TYPE='change' AND  FI_FDSHORTNAME='"+shortName+"' AND FI_KEYVALUE='"+id+"'");
		flowLog.append(") ORDER BY FL_ID)");
		List<Map<String, Object>> list = baseDao.queryForList(flowLog.toString());
		row = getRow(sheet, 4);
		String[] titleStr = new String[]{"序号","节点名称","操作","处理人","处理人编号","处理时间","停留时间(分钟)"};
		for(int m = 0; m < titleStr.length; m++){
			HSSFCell specilCell = row.createCell(m);
			specilCell.setCellValue(titleStr[m]);
			specilCell.setCellStyle(labelStyle);
		}
		int dataRow = 5, rowIndex = 1;
		for(Map<String, Object> logMap : list){
			row = getRow(sheet, dataRow);
			row.createCell(0).setCellValue(dataRow-4);
			dataRow++;
			for(Object value: logMap.values()){
				row.createCell(rowIndex).setCellValue(String.valueOf(value));
				rowIndex++;
			}
			rowIndex = 1;
		}
		//关联sheet
		sheet = workbook.createSheet("关联");
		sheet.createFreezePane(0, 1);// 固定标题
		sheet = createHeaderCell(sheet, labelStyle, fieldStyle);
		row = sheet.createRow(3);
		
		titleStyle = getCellStyle(workbook, "title");
		row.setHeight((short) 400);
		
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (short) 5));
		cell = row.createCell(0);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString("关联"));
		cell.setCellStyle(titleStyle);
		HSSFCellStyle groupStyle = getCellStyle(workbook, "group");
		int rowNum = 4; dataRow = 6; rowIndex = 0;
		row = getRow(sheet, rowNum++);
		row.setHeight((short) 350);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, (short) 2));
		HSSFCell groupCell = row.createCell(0);
		groupCell.setCellValue("派生任务");
		groupCell.setCellStyle(groupStyle);
		//设置表单标题
		row = getRow(sheet, rowNum++);
		for(int k = 0; k < 4; k++){
			if(k == 0){
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, (short) 2));
			}else{
				sheet.setColumnWidth(k, 6000);
			}
		}
		titleStr = new String[]{"任务描述","截止时间","执行人","当前状态"};
		for(int n = 0; n < titleStr.length; n++){
			HSSFCell specilCell = null;
			if(n == 0){
				specilCell = row.createCell(n);
				row.createCell(n+1).setCellStyle(labelStyle);
				row.createCell(n+2).setCellStyle(labelStyle);
			}
			else{
				specilCell = row.createCell(n+2);
			}
			specilCell.setCellValue(titleStr[n]);
			specilCell.setCellStyle(labelStyle);
		}
		Object codevalue = baseDao.getFieldDataByCondition("CUSTOMTABLE", "CT_CODE", "ct_id="+id);
		list = baseDao.queryForList("select description,resourcename,enddate,handstatus from projecttask where sourcecaller='"+caller+"' and sourcecode='"+codevalue+"'");
		for(Map<String, Object> dataMap : list){
			row = getRow(sheet, rowNum++);
			for(Object value: dataMap.values()){
				if(rowIndex == 0){
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, (short) 2));
					row.createCell(rowIndex).setCellValue(String.valueOf(value));
				}else{
					row.createCell(rowIndex+2).setCellValue(String.valueOf(value));
				}
				
				rowIndex++;
			}
			rowIndex = 0;
		}
		row = getRow(sheet, rowNum++);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, (short) 2));
		groupCell = row.createCell(0);
		groupCell.setCellValue("派生流程");
		groupCell.setCellStyle(groupStyle);
		for(int k = 0; k < 6; k++){
			if(k == 0){
				sheet.setColumnWidth(k, 3000);
			}else{
				sheet.setColumnWidth(k, 6000);
			}
		}
		//设置表单标题
		row = getRow(sheet, rowNum++);
		titleStr = new String[]{"序号","派生流程名称","派生单据编号","派生单据标题","派生节点名称"};
		for(int n = 0; n < titleStr.length; n++){
			HSSFCell specilCell = row.createCell(n);
			specilCell.setCellValue(titleStr[n]);
			specilCell.setCellStyle(labelStyle);
		}
		list = baseDao.queryForList("SELECT FR_NAME,FR_RELATIONCODE,FI_TITLE,FR_NODENAME "
				+ "FROM FLOW_RELATION LEFT JOIN FLOW_OPERATION ON FR_FOID=FO_ID LEFT JOIN FLOW_INSTANCE ON FR_RELATIONID=FI_KEYVALUE "
				+ "WHERE fi_type = 'Derive' AND FR_FDSHORTNAME='"+shortName+"' AND FO_TYPE='Flow' AND FR_KEYVALUE="+id);
		rowIndex = 1;
		int count = 1;
		for(Map<String, Object> dataMap : list){
			row = getRow(sheet, rowNum++);
			row.createCell(0).setCellValue(count++);
			for(Object value: dataMap.values()){
				if(!"null".equals(ObjectToString(value)) && !"".equals(ObjectToString(value))){
					row.createCell(rowIndex).setCellValue(ObjectToString(value));
				}else{
					row.createCell(rowIndex).setCellValue("");
				}
				rowIndex++;
			}
			rowIndex = 1;
		}
		return workbook;
	}
	
	private HSSFCellStyle getCellStyle(HSSFWorkbook workbook, String type) {
		HSSFFont font = workbook.createFont();
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (type.equals("title")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 300);
			cellStyle.setFont(font);
		} else if (type.equals("group")) {
			HSSFPalette customPalette = workbook.getCustomPalette();
			customPalette.setColorAtIndex(HSSFColor.ORANGE.index, (byte) 153, (byte) 153, (byte) 204);
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 250);
			cellStyle.setFont(font);
		} else if (type.equals("column")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		} else {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		}
		return cellStyle;
	}
	
	private HSSFRow getRow(HSSFSheet sheet, int rowIndex) {
		HSSFRow row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);
		return row;
	}
	
	private void createCellByFormDetail(HSSFRow row, HSSFCellStyle labelStyle, HSSFCellStyle fieldStyle, FormDetail detail, List<DataListCombo> combos, Object value,
			int rowIndex, String language) {
		if(!"EM_PASSWORD".equals(detail.getFd_field().toUpperCase())){
			HSSFCell labelCell = row.createCell(2 * (rowIndex % 2));
			if (language.equals("en_US")) {
				labelCell.setCellValue(detail.getFd_captionen());
			} else if (language.equals("zh_TW")) {
				labelCell.setCellValue(detail.getFd_captionfan());
			} else {
				labelCell.setCellValue(detail.getFd_caption());
			}
			labelCell.setCellStyle(labelStyle);
			HSSFCell valueCell = row.createCell(2 * (rowIndex % 2) + 1);
			fieldStyle.setWrapText(true);
			fieldStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);
			valueCell.setCellStyle(fieldStyle);
			setCellValue(valueCell, value, detail.getFd_type(), combos);	
		}
	}
	
	/**
	 * 将数值按配置进行转化后再写入cell
	 * 
	 * @param cell
	 * @param value
	 * @param detail
	 * @param combos
	 */
	private void setCellValue(Cell cell, Object value, String type, List<DataListCombo> combos) {
		if (value != null) {
			if ("C".equals(type) || "EC".equals(type) || "combo".equals(type) || "combocolumn".equals(type)) {
				for (DataListCombo combo : combos) {
					if (value.equals(combo.getDlc_display())) {
						value = combo.getDlc_value();
						break;
					}
				}
			} else if ("B".equals(type) || "checkcolumn".equals(type)) {
				if ("1".equals(value) || "-1".equals(value))
					value = "√";
				else
					value = "×";
			} else if ("YN".equals(type) || "yncolumn".equals(type)) {
				if ("1".equals(value.toString()) || "-1".equals(value.toString()))
					value = "是";
				else
					value = "否";
			}
		}
		setCellValue(cell, value, type);
	}
	
	
	private void setCellValue(Cell cell, Object value, String type) {
		String textValue = "";
		if (value != null) {
			if (value instanceof Boolean) {
			} else if (value instanceof Date) {
				Date date = (Date) value;
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				textValue = sdf.format(date);
			} else if (value instanceof byte[]) {
				// 有图片时，设置行高为60px;
			} else if (value instanceof Integer) {
				textValue = value.toString();
			} else {
				// 其它数据类型都当作字符串简单处理
				textValue = value.toString();
			}
			if (textValue != null) {
				Pattern p = Pattern.compile("([1-9][0-9]*(\\.\\d+)?)$");
				Matcher matcher = p.matcher(textValue);
				if (matcher.matches()
						&& !("S".equals(type) || "text".equals(type) || "AC".equals(type) || "C".equals(type) || "T".equals(type))) {
					cell.setCellValue(Double.parseDouble(textValue));
				} else {
					HSSFRichTextString richString = new HSSFRichTextString(textValue);
					cell.setCellValue(richString);
				}
			}

		}
	}
	
	
	public String getFormTitle(String caller, int id) {
		String title = "";
		if (caller != null) {
			Form form = getFormWithCloud(caller, false);
			if (form != null) {
				title += form.getFo_title();
				Object codefield = form.getFo_codefield();
				if (codefield != null) {
					String table = form.getFo_table();
					if (caller.endsWith("$Change"))
						table = "COMMONCHANGELOG";
					Object codevalue = baseDao.getFieldDataByCondition(table, codefield.toString(), form.getFo_keyfield() + "=" + id);
					if (codevalue != null) {
						title += "-" + codevalue.toString();
					}
				}
			}
		}
		if (title.equals("")) {
			title = "导出数据";
		}
		return title;
	}

	@Override
	public void update(int id, String caller, String formStore) {	
		//获取form配置
		Form form = getFormWithCloud(caller, false);

		String tableName = form.getFo_table();
		String keyField = form.getFo_keyfield();
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.put(keyField, id);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, tableName, keyField);
		baseDao.execute(formSql);
	}

	/**
	 * 
	 * 
	 * @param caller 派生流程caller
	 * @param nodeId 派生流程nodeId
	 * @param id 	  派生流程单据id
	 * @param formStore 派生流程单据填写的数据
	 * @param btnid  派生流程操作ID
	 * @param preCaller 当前流程caller
	 * @param preNodeId 当前流程节点id
	 * @param preKeyValue 当前单据id
	 */
	@Transactional
	public void saveFlow(int preNodeId,int nodeId, int id, String preCaller, String caller, String formStore, int btnid, int preKeyValue,String url) {
		//获取派生流程版本
		String shortName = getShortName(caller);
		//当前时间
		String nowTime = getSqlDate();
		//获取之前的流程版本
		String preShortName = getShortName(preCaller);

		Form form = getFormWithCloud(caller, false);

		String tableName = form.getFo_table();
		String codeField = form.getFo_codefield();
		String keyField = form.getFo_keyfield();
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = "";
		formSql = SqlUtil.getInsertSqlByFormStore(store, tableName, new String[] {}, new Object[] {});

		baseDao.execute(formSql);
		
		//将附件信息转到附件表中显示
		saveFileIntoGrid(store,caller,form.getFo_id(),id);
		
		//所有的人员编码以及角色类型
		List<Map<String,Object>> personCodes = new ArrayList<Map<String,Object>>();
		Map<String,Object> personMap = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		
		//收集责任人（单）
		Object op = baseDao.getFieldDataByCondition("flow_operation", "fo_groupname", "fo_id="+btnid);
		Object dealMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='duty'"); 
		String dutyCode = String.valueOf(store.get(dealMan));
		Object duty = new Object();
		if(dutyCode.equals("")){
			Object[] defaultDuty = baseDao.getFieldsDataByCondition("flow_define", "fd_defaultduty,fd_defaultdutycode", "fd_shortname = '"+shortName.split("-")[0]+"'");
			dutyCode = String.valueOf(defaultDuty[1]);
			duty = String.valueOf(defaultDuty[0]);
		}else{
			duty = baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+dutyCode+"'");
		}
		map.put("duty", duty);
		map.put("dutycode", dutyCode);
		personMap = new HashMap<String,Object>();
		personMap.put("code", dutyCode);
		personMap.put("type", "duty");
		personCodes.add(personMap);
		
		//参与者（多）
		Object actorMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='actor'"); 
		String actorCode = String.valueOf(store.get(actorMan));
		String[] actorArr = actorCode.split("#");
		//收集参与者
		if(!actorCode.equals("null")&&actorArr.length>0){
			for (String str : actorArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "actor");
				personCodes.add(personMap);
			}
		}
		
		//读者（多）
		Object readerMan = baseDao.getFieldDataByCondition("flow_groupconfig", "FGC_FIELD", "fgc_groupname='"+op+"' and fgc_fdshortname='"+shortName+"' and FGC_ROLECODE='reader'"); 
		String readerCode = String.valueOf(store.get(readerMan));
		String[] readerArr = readerCode.split("#");
		//收集读者
		if(!readerCode.equals("null")&&readerArr.length>0){
			for (String str : readerArr) {
				personMap = new HashMap<String,Object>();
				personMap.put("code", str);
				personMap.put("type", "reader");
				personCodes.add(personMap);
			}
		}
		
		//创建者
		Employee employee = SystemSession.getUser();
		map.put("creator", employee.getEm_name());
		map.put("creatorcode", employee.getEm_code());
		personMap = new HashMap<String,Object>();
		personMap.put("code", employee.getEm_code());
		personMap.put("type", "creator");
		personCodes.add(personMap);
	
		
		//标题
		Object titlefield = baseDao.getFieldDataByCondition("form left join formdetail on fo_id = fd_foid", "FD_FIELD", "fo_caller='"+caller+"' and FD_LOGICTYPE='title'"); 
		Object title = baseDao.getFieldDataByCondition(tableName, String.valueOf(titlefield), form.getFo_keyfield()+"='"+id+"'"); 
		
		
		//获取当前节点名称
		String nodeName = String.valueOf(baseDao.getFieldDataByCondition("Flow_Node", "FN_NODENAME", "FN_FDSHORTNAME='"+shortName+"' and FN_ID="+nodeId));
	
		
		Object codeValue = baseDao.getFieldDataByCondition(tableName, codeField, keyField+"="+id);
		StringBuffer start = new StringBuffer();
		int newInstanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
		start.append("INSERT INTO FLOW_INSTANCE(FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_CODEVALUE,FI_KEYVALUE,FI_HANDLER,FI_HANDLERCODE,FI_NODENAME,FI_STARTMAN,FI_STARTMANCODE,FI_STATUS,"
				+ "FI_CALLER,FI_KEYFIELD,FI_TITLE,FI_STARTTIME,FI_TIME,FI_TYPE) values("
				+ newInstanceId+","
				+ "'"+shortName+"',"
				+ nodeId+","
				+ "'"+codeValue+"',"
				+ id+","
				+ "'"+map.get("duty")+"',"
				+ "'"+map.get("dutycode")+"',"
				+ "'"+nodeName+"',"
				+ "'"+map.get("creator")+"',"
				+ "'"+map.get("creatorcode")+"',"
				+ "'using',"
				+ "'"+caller+"','"+keyField+"','"+title+"',"
				+ nowTime +","
				+ nowTime +","
				+ "'Derive')");
		baseDao.execute(start.toString());
		
		//创建角色表
		StringBuffer sql2 = new StringBuffer();
		//遍历数组
		sql2.append("begin ");
		for (Map<String,Object> person : personCodes) {
			sql2.append("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
					+ "FLOW_INSTANCEROLE_SEQ.nextval,"
					+ "'"+person.get("code")+"',"
					+ "'"+person.get("type")+"',"
					+ "'"+newInstanceId+"');");
		}
		sql2.append(" end;");
		baseDao.execute(sql2.toString());
		
		StringBuffer sqlRelation = new StringBuffer();
		String preNodeName = String.valueOf(baseDao.getFieldDataByCondition("FLOW_NODE", "FN_NODENAME", "FN_ID="+preNodeId));
		sqlRelation.append("INSERT INTO flow_relation(fr_id,fr_name,fr_fdshortname,fr_keyvalue,fr_relationcode,fr_relationid,fr_caller,fr_nodeid,fr_nodename,fr_foid) "
				+ "select flow_relation_seq.nextval,FI_NODENAME,'"+preShortName+"',"+preKeyValue+",FI_CODEVALUE,FI_KEYVALUE,'"+caller +"',"+preNodeId+",'"+preNodeName+"',"+btnid+" from flow_instance where fi_id="+newInstanceId);
		baseDao.execute(sqlRelation.toString());
			
		StringBuffer sqlLog = new StringBuffer();
		sqlLog.append("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
				"FL_NODENAME,FL_NODEID,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,fl_url) select FLOW_LOG_SEQ.NEXTVAL,FO_ID,'"+employee.getEm_code()+"',"
				+ "'"+employee.getEm_name()+"',"+nowTime+",'"+shortName+"','"+nodeName+"',"+nodeId+",0,'"+codeValue+"',"+id+",'"+url+"' FROM FLOW_OPERATION WHERE "+
				" FO_FDSHORTNAME='"+shortName+"' AND FO_ID="+btnid);
		baseDao.execute(sqlLog.toString());
	}
	
	@Override
	public List<JSONTree> getAllFlowTree(int parentId,String condition) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<FlowDefine> list = getFlowTreeById(parentId, condition);
		for(FlowDefine f:list){
			tree.add(new JSONTree(f));
		}
		return tree;
	}
	
	public List<FlowDefine> getFlowTreeById(int parentId, String condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from flow_define where fd_parentid=?");
		if(condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by fd_id asc");
		
		try {
			List<FlowDefine> list = baseDao.getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<FlowDefine>(FlowDefine.class),parentId);
			return list;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<FlowDefine>();
		}
	}
	
	@Override
	public void updateHandler(String ids, String code){
		Object em_name = baseDao.getFieldDataByCondition("employee", "em_name", "em_code = '"+code+"'");
		if(String.valueOf(em_name).equals("")&&String.valueOf(em_name).length()<1){
			BaseUtil.showError("未查询到输入的变更人，请重新选择");
		}
		//单据ID串
		String idStr = "";
		List<Map<Object,Object>> ls = BaseUtil.parseGridStoreToMaps(ids);
		for (Map<Object, Object> map : ls) {
			idStr = idStr + String.valueOf(map.get("id")) + ",";
		}
		idStr = idStr.substring(0, idStr.length()-1);
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		//更新实例表和角色权限表
		sql.append("update flow_instance set fi_handlercode = '"+code+"',fi_handler = '"+String.valueOf(em_name)+"' where fi_keyvalue in ("+idStr+");");
		sql.append("update flow_instancerole set fir_mancode = '"+code+"' where fir_type = 'duty' and "+
				   "fir_fiid in (select fi_id from flow_instance where fi_keyvalue in ("+idStr+"));");
		sql.append(" end;");
		baseDao.execute(sql.toString());
		
		//记录操作日志
		Employee employee = SystemSession.getUser();
		//当前时间
		String nowTime = getSqlDate();
		
		String remark = "变更责任人为:" + em_name;
		StringBuffer sqllog = new StringBuffer();
		sqllog.append("begin ");
		for (Map<Object, Object> map : ls) {
			int id = Integer.valueOf(String.valueOf(map.get("id")));
			sqllog.append("INSERT INTO FLOW_LOG(FL_ID,FL_NODENAME,FL_CODEVALUE,FL_CODE,FL_NAME,FL_DEALTIME,FL_KEYVALUE,FL_TYPE,FL_REMARK)"
			+ " select FLOW_LOG_SEQ.NEXTVAL,FI_NODENAME,FI_CODEVALUE,"
			+ "'"+employee.getEm_code()+"',"
			+ "'"+employee.getEm_name()+"',"+nowTime+","+id+",'change','"+remark+"'"
			+ " FROM FLOW_INSTANCE WHERE FI_KEYVALUE="+id+" AND FI_STATUS = 'using';");
		}
		sqllog.append(" end;");
		baseDao.execute(sqllog.toString());
	}
	
	/**
	 * 获取可回退的节点
	 * @param caller
	 * @param id
	 * @param nodeId
	 * @return
	 */
	public List<Map<String, Object>> getRollbackNodename(String caller, String id, String nodeId){
		//判断是否派生流程
		boolean isEnd = baseDao.checkIf("Flow_instance", "fi_keyvalue= "+id+" and fi_status='using'");
		if(!isEnd){
			//结束流程不能回退
			BaseUtil.showError("结束流程不能回退！");
		}
		//流程版本
		String shortName = String.valueOf(baseDao.getFieldDataByCondition("FLOW_DEFINE", "FD_SHORTNAME", "FD_CALLER='"+caller+"' and FD_STATUS='enable'"));
		//获取当前节点的流程实例id(最小)
		String minIdSql = "select * from (select fi_id from flow_instance where fi_fdshortname='"+shortName+"' and fi_keyvalue='"+id+"' and fi_nodeId="+nodeId+" order by fi_id) where rownum=1";
		int fi_id = baseDao.queryForObject(minIdSql, Integer.class);
		//取可回退的节点
		
		String sql = "select fi_nodename,fi_nodeId from flow_instance where fi_fdshortname='"+shortName+"' and fi_keyvalue='"+id+"' and FI_NODENAME <> 'START' and fi_id < "+fi_id+" group by fi_nodename,fi_nodeid";
		List<Map<String, Object>> nodeList = baseDao.queryForList(sql);
		return nodeList; 
	}
	
	/**
	 * 回退
	 * @param caller
	 * @param id
	 * @param oldNodeName
	 * @param newNodeName
	 */
	@Transactional
	public void versionRollback(String caller, int id, int newNodeId){
		//流程版本
		String version = String.valueOf(baseDao.getFieldDataByCondition("FLOW_DEFINE", "FD_SHORTNAME", "FD_CALLER='"+caller+"' and FD_STATUS='enable'"));
		//nodeId
		String newNodeName = String.valueOf(baseDao.getFieldDataByCondition("flow_node", "fn_nodename", "fn_id ='"+newNodeId+"' and fn_fdshortname='"+version+"'"));
		//获取在该节点上已经保存的实例信息
		Map<String, Object> instanceMap = baseDao.getJdbcTemplate().queryForMap("select * from flow_instance where fi_id = (select  min(fi_id) from flow_instance where fi_keyvalue="+id+" and fi_fdshortname='"+version+"' and fi_nodeId="+newNodeId+")");
		//获取当前流程实例节点名
		Object oldNodeName = baseDao.getFieldDataByCondition("flow_instance", "FI_nodename", "fi_keyvalue="+id+" and fi_fdshortname = '"+version+"' and FI_STATUS= 'using'");
		//关闭当前流程实例
		baseDao.updateByCondition("flow_instance", "FI_STATUS='end'", "fi_keyvalue="+id+" and fi_fdshortname = '"+version+"' and FI_STATUS= 'using'");
		
		//历史流程id
		int fi_id = Integer.parseInt(String.valueOf(baseDao.getFieldDataByCondition("flow_instance", "fi_id", "fi_keyvalue="+id+" and fi_fdshortname = '"+version+"' and fi_nodeid= '"+newNodeId+"'")));
		//拿到要回退节点的历史角色映射
		String roleSql = "select * from flow_instancerole where fir_fiid="+fi_id;
		List<Map<String, Object>> roleList = baseDao.queryForList(roleSql);
		
		Employee employee = SystemSession.getUser();
		int newInstanceId = baseDao.getSeqId("FLOW_INSTANCE_SEQ");
		/*创建流程实例*/
		String sql = "insert into flow_instance(FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_CODEVALUE,FI_KEYVALUE,FI_HANDLER,FI_HANDLERCODE,FI_NODENAME,FI_STARTTIME,FI_TIME,FI_STARTMAN,FI_STARTMANCODE,FI_STATUS,"
				+ "FI_CALLER,FI_KEYFIELD,FI_TITLE) values("
				+ newInstanceId + ","
				+ "'" + version + "',"
				+ newNodeId +","
				+ "'" + String.valueOf(instanceMap.get("FI_CODEVALUE")) +"',"
				+ id + ","
				+ "'" + String.valueOf(instanceMap.get("FI_HANDLER")) + "',"
				+ "'" + String.valueOf(instanceMap.get("FI_HANDLERCODE")) + "',"
				+ "'" + newNodeName + "',"
				+ "sysdate,"
				+ "sysdate,"
				+ "'" + employee.getEm_name() + "',"
				+ "'" + employee.getEm_code() + "',"
				+ "'using',"
				+ "'" + caller +"',"
				+ "'" + String.valueOf(instanceMap.get("FI_KEYFIELD")) + "',"
				+ "'" + String.valueOf(instanceMap.get("FI_TITLE")) + "')";
		baseDao.execute(sql);
		
		/*增加角色映射*/
		StringBuffer sql2 = new StringBuffer();
		//遍历数组
		sql2.append("begin ");
		for (Map<String,Object> map : roleList) {
			if("creator".equals(map.get("FIR_TYPE"))){
				sql2.append("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
						+ "FLOW_INSTANCEROLE_SEQ.nextval,"
						+ "'"+employee.getEm_code()+"',"
						+ "'"+map.get("FIR_TYPE")+"',"
						+ "'"+newInstanceId+"');");
			}else{
				sql2.append("INSERT INTO FLOW_INSTANCEROLE(FIR_ID,FIR_MANCODE,FIR_TYPE,FIR_FIID) values("
						+ "FLOW_INSTANCEROLE_SEQ.nextval,"
						+ "'"+map.get("FIR_MANCODE")+"',"
						+ "'"+map.get("FIR_TYPE")+"',"
						+ "'"+newInstanceId+"');");
			}
		}
		sql2.append(" end;");
		baseDao.execute(sql2.toString());
		
		float stayTime = 0;
		
		Object resultTime = baseDao.getFieldDataByCondition("(SELECT FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+String.valueOf(instanceMap.get("FI_CODEVALUE"))+"' AND FI_STATUS='using') a, "
				+ "(SELECT FI_TIME FROM FLOW_INSTANCE WHERE FI_CODEVALUE = '"+String.valueOf(instanceMap.get("FI_CODEVALUE"))+"' AND FI_STATUS='end' AND ROWNUM=1 ORDER BY FI_TIME DESC) b",
				"(a.fi_time-b.fi_time)*24*60", "1=1");
		String timeStr = String.valueOf(resultTime);
		stayTime=NumberUtil.subFloat(Float.valueOf(timeStr), 0);
		
		//记录操作日志
		StringBuffer sqlLog = new StringBuffer();
		sqlLog.append("INSERT INTO FLOW_LOG(FL_ID,FL_FOID,FL_CODE,FL_NAME,FL_DEALTIME,FL_FDSHORTNAME," + 
				"FL_NODENAME,FL_STAYTIME,FL_CODEVALUE,FL_KEYVALUE,FL_TYPE,FL_REMARK,FL_NODEID) values (FLOW_LOG_SEQ.NEXTVAL,null,'"+employee.getEm_code()+"',"
				+ "'"+employee.getEm_name()+"',"+getSqlDate()+",'"+version+"','"+String.valueOf(oldNodeName)+"',"+stayTime+",'"+String.valueOf(instanceMap.get("FI_CODEVALUE"))+"',"+id+",'ROLLBACK',"
				+ "'回退至"+newNodeName+"',"+newNodeId+")");
		baseDao.execute(sqlLog.toString());
	}
	
	/**
	 * 根据caller获取流程版本
	 * 
	 * @param caller form--Caller
	 */
	private String getShortName(String caller) {
		//获取流程版本
		String shortName = String.valueOf(baseDao.getFieldDataByCondition("FLOW_DEFINE", "FD_SHORTNAME",
				"FD_CALLER='"+caller+"' AND FD_STATUS='enable'"));	
		
		if(shortName==null || "".equals(shortName)) {
			BaseUtil.showError("请检查此单据的流程是否存在！");
			return null;
		}else{
			return shortName;
		}
	}
	
	/**
	 * 根据id获取流程版本
	 * 
	 * @param caller form--Caller
	 */
	@SuppressWarnings("unused")
	private String getShortName(int fdid) {
		//获取流程版本
		String shortName = String.valueOf(baseDao.getFieldDataByCondition("FLOW_DEFINE", "FD_SHORTNAME",
				"fd_id="+fdid));	
		
		if(shortName==null || "".equals(shortName)) {
			BaseUtil.showError("请检查此单据的流程是否存在！");
			return null;
		}else{
			return shortName;
		}
	}
	
	/**
	 * 根据caller获取form配置
	 * @param caller
	 * @param isCloud   优软云
	 * @return
	 */
	private Form getFormWithCloud(String caller,boolean isCloud) {
		isCloud=false;
		Form form = null;
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			form = formDao.getForm(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			form = formDao.getForm(caller, master);
		
		return form;
	}
	
	/**
	 * 获取当前时间
	 * 		格式：to_date('2018-3-28 11:26:00','yyyy-MM-dd HH24:MI:ss')
	 */
	private String getSqlDate() {
		String nowTime = "to_date('"+DateUtil.format(new Date(), YMD_HMS)+"','"+YMD_HMS_SQL+"')";
		return nowTime;
	}
	
	/**
	 * 对象转成Interger
	 * @param o
	 */
	private int ObjectToInterger(Object o) {
			return Integer.valueOf(String.valueOf(o));
	}
	
	/**
	 * 对象转成String
	 * @param o
	 */
	private String ObjectToString(Object o) {
		return String.valueOf(o);
	}

	@Override
	public void logger(int nodeId, int id, String caller) {
		
	}


	@Override
	public List<Map<String, Object>> getTransferField(String fdid, String fromId,String shortName) {
		if(fdid!=null && !"".equals(fdid)) {
			String caller = String.valueOf(baseDao.getFieldDataByCondition("flow_define", "fd_caller", 
					"fd_id="+fdid));
			String groups = String.valueOf(baseDao.getFieldDataByCondition("flow_node", "fn_groups","FN_ID="+fromId));
			JSONArray array = JSONArray.parseArray(groups);
			StringBuffer groupArray = new StringBuffer();
			if(array!=null && array.size()>0) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject job = array.getJSONObject(i); 
					groupArray.append("'"+(String) job.get("name")+"',");
				}
				String groupsName = groupArray.toString();
				groupsName = groupsName.substring(0, groupsName.length()-1);
				Form form = getFormWithCloud(caller, false);
				String sql = " select fd_caption,fd_field from (select  distinct fgc_field  from FLOW_GROUPCONFIG where fgc_groupname in ("+groupsName+") and fgc_fdshortname='"+shortName+"') " + 
						"left join formdetail on fgc_field = fd_field  where fd_foid="+form.getFo_id()+" order by fd_detno";
				List<Map<String, Object>> field = baseDao.queryForList(sql);
				return field;
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public void deleteDefineByCondition(String shortName,String caller,String condition){
		if(condition==null||condition.equals("")||condition.length()==0){//删除子实例
			List<String> list = new ArrayList<String>();
			//根据版本号删除
			list.add("DELETE FROM FLOW_DEFINE WHERE FD_SHORTNAME = '"+ shortName +"'");
			list.add("DELETE FROM FLOW_CHART WHERE FC_SHORTNAME = '"+ shortName +"'");
			list.add("DELETE FROM FLOW_CHARTLOG WHERE FCL_SHORTNAME = '"+ shortName +"'");
			list.add("DELETE FROM FLOW_GROUPCONFIG WHERE FGC_FDSHORTNAME = '"+ shortName +"'");
			list.add("DELETE FROM FLOW_NODE WHERE FN_FDSHORTNAME = '"+ shortName +"'");
			//查询操作ID
			String idSql = "select fo_id from FLOW_OPERATION where fo_type in ('Task,Flow') and fo_fdshortname = '"+shortName+"'";
			List<Map<String, Object>> ids = baseDao.queryForList(idSql);
			String idStr = "";
			for (Map<String, Object> map : ids) {
				idStr = idStr + map.get("FO_ID") + ",";
			}
			if(idStr.length()>0){
				idStr = idStr.substring(0, idStr.length()-1);
				//删除所有映射信息
				list.add("DELETE FROM FLOW_TRANSFER WHERE FT_FOID IN ("+ idStr +")");
			}
			//删除操作
			list.add("DELETE FROM FLOW_OPERATION WHERE FO_FDSHORTNAME = '"+ shortName +"'");
			baseDao.execute(list);
		}else{//删除父实例和子实例
			List<String> list = new ArrayList<String>();
			//根据caller 删除form 和  formDetail
			Object fo_id = baseDao.getFieldDataByCondition("FORM", "FO_ID", "FO_CALLER = '"+caller+"'");
			list.add("DELETE FROM FORMDETAIL WHERE FD_FOID = '"+ String.valueOf(fo_id) +"'");
			list.add("DELETE FROM FORM WHERE FO_CALLER = '"+ caller +"'");
			list.add("DELETE FROM FLOW_DEFINE WHERE FD_SHORTNAME = '"+ shortName +"'");
			baseDao.execute(list);
		}
	}
	
	@Override
	public void checkNodeAndOpt(String shortName,String allNode,String allConnection){
		//校验节点
		List<Map<Object, Object>> nodeMap = BaseUtil.parseGridStoreToMaps(allNode);
		List<Object> nodeStr = baseDao.getFieldDatasByCondition("flow_node", "fn_nodename", "fn_fdshortname = '"+shortName+"'");
		String saveNodeStr = "";
		if(nodeStr.size()>0){
			for (Map<Object, Object> map : nodeMap) {
				saveNodeStr = saveNodeStr + "'" + String.valueOf(map.get("name")) + "',";
				String badnode = String.valueOf(map.get("name"));
				for (int i = 0; i < nodeStr.size(); i++) {
					if(badnode.equals(String.valueOf(nodeStr.get(i)))){
						badnode = "";
						break; 
					}
				}
				if(badnode!=""&&badnode.length()>1){
					BaseUtil.showError("有未保存的节点，请保存节点后再保存流程图："+badnode);
				}
			}
		}
		//删除多余的节点
		if(saveNodeStr.length()>0){
			saveNodeStr = saveNodeStr.substring(0, saveNodeStr.length()-1);
			String sql = "delete from flow_node where fn_fdshortname = '"+shortName+"' and fn_nodename not in ("+saveNodeStr+")";
			baseDao.execute(sql);
		}
		//校验线段
		List<Map<Object, Object>> optMap = BaseUtil.parseGridStoreToMaps(allConnection);
		List<Object> optStr = baseDao.getFieldDatasByCondition("flow_operation", "fo_name", "fo_fdshortname = '"+shortName+"'");
		String saveOptStr = "";
		if(optStr.size()>0){
			for (Map<Object, Object> map : optMap) {
				String badopt = String.valueOf(map.get("name"));
				if(badopt.equals("提交")){
					badopt = "commit";
				}
				saveOptStr = saveOptStr + "'" + badopt + "',";
				for (int i = 0; i < optStr.size(); i++) {
					if(badopt.equals(String.valueOf(optStr.get(i)))){
						badopt = "";
						break; 
					}
				}
				if(badopt!=""&&badopt.length()>1){
					BaseUtil.showError("有未保存的操作，请保存操作后再保存流程图："+badopt);
				}
				//检查操作NodeName和操作对应，NodeName和NodeId对应，如果不对应就更新
				String operationName = String.valueOf(map.get("name"));
				Object[] node = baseDao.getFieldsDataByCondition("FLOW_OPERATION", "FO_NEXTNODENAME,FO_NEXTNODEID,FO_NODENAME,FO_NODEID", 
						"FO_NAME='"+operationName+"' AND FO_FDSHORTNAME='"+shortName+"'");
				if(node!=null) {
					String nextNodeName = ObjectToString(node[0]);
					String nextNodeId = ObjectToString(node[1]);
					String nodeName = ObjectToString(node[2]);
					int nodeId = ObjectToInterger(node[3]);
					String tagNodeName = ObjectToString(map.get("tag"));
					String rourceNodeName = ObjectToString(map.get("source"));
					//判断下一节点
					if(!nextNodeName.equals(tagNodeName)) {
						//数据库保存目标节点与流程图目标节点不同
						baseDao.execute("UPDATE FLOW_OPERATION SET FO_NEXTNODENAME='"+tagNodeName+"',FO_NEXTNODEID="
								+ "(SELECT FN_ID FROM FLOW_NODE WHERE FN_FDSHORTNAME='"+shortName+"' AND FN_NODENAME='"+tagNodeName+"')"
								+ " WHERE FO_NAME='"+operationName+"' AND FO_FDSHORTNAME='"+shortName+"'");
					}else {
						//节点名称相同，判断节点id是否相同
						boolean checkNodeId = baseDao.checkIf("FLOW_NODE", "FN_FDSHORTNAME='"+shortName+"' AND FN_NODENAME='"+tagNodeName+"' AND FN_ID="+
								nextNodeId);
						if (!checkNodeId) {
							baseDao.execute("UPDATE FLOW_OPERATION SET FO_NEXTNODEID=(SELECT FN_ID FROM FLOW_NODE WHERE FN_FDSHORTNAME='"
									+shortName+"' AND FN_NODENAME='"+tagNodeName+"') WHERE FO_NAME='"+operationName+"' AND FO_FDSHORTNAME='"+shortName+"'");
						}
					}
					//判断来源节点
					if(!nodeName.equals(rourceNodeName)) {
						//数据库保存目标节点与流程图目标节点不同
						baseDao.execute("UPDATE FLOW_OPERATION SET FO_NODENAME='"+rourceNodeName+"',FO_NODEID="
								+ "(SELECT FN_ID FROM FLOW_NODE WHERE FN_FDSHORTNAME='"+shortName+"' AND FN_NODENAME='"+rourceNodeName+"')"
								+ " WHERE FO_NAME='"+operationName+"' AND FO_FDSHORTNAME='"+shortName+"'");
					}else {
						//节点名称相同，判断节点id是否相同
						boolean checkNodeId = baseDao.checkIf("FLOW_NODE", "FN_FDSHORTNAME='"+shortName+"' AND FN_NODENAME='"+rourceNodeName+"' AND FN_ID="+
								nodeId);
						if (!checkNodeId) {
							baseDao.execute("UPDATE FLOW_OPERATION SET FO_NODEID=(SELECT FN_ID FROM FLOW_NODE WHERE FN_FDSHORTNAME='"
									+shortName+"' AND FN_NODENAME='"+rourceNodeName+"') WHERE FO_NAME='"+operationName+"' AND FO_FDSHORTNAME='"+shortName+"'");
						}
					}
				}
			}
		}
		
		
		//删除多余的线
		if(saveOptStr.length()>0){
			saveOptStr = saveOptStr.substring(0, saveOptStr.length()-1);
			String sql = "delete from flow_operation where fo_fdshortname = '"+shortName+"' and fo_name not in ("+saveOptStr+")";
			baseDao.execute(sql);
		}
	}


	@Override
	@Transactional
	public void saveDerive(String deriveData,String baseMessage,String groupData,String caller,String shortName) {
		Map<Object, Object> base = BaseUtil.parseFormStoreToMap(baseMessage);
		String operationType = ObjectToString(base.get("operationType"));
		if(operationType.equals("Task")){
			String nodeId = ObjectToString(base.get("nodeId"));
			String nodeName = ObjectToString(base.get("nodeName"));
			String operationName = ObjectToString(base.get("operationName"));
	    	String remark = ObjectToString(base.get("remark"));
	    	int flowOperationId = 0;
			if(nodeId!=null && !"".equals(nodeId) && operationName!=null && !"".equals(operationName)) {
				boolean isExist = baseDao.checkIf("flow_operation", "fo_nodeId = "+ nodeId +" and fo_name = '" + operationName +"' and fo_fdshortname='"+shortName+"'");
			    if(!isExist) {
			    		flowOperationId = baseDao.getSeqId("flow_operation_seq");
			    		String baseSql = "insert into flow_operation(fo_id,fo_name,fo_type,fo_remark,fo_nodeName,fo_fdshortname,fo_isduty,fo_flowcaller,fo_nodeid) "
			    				+ "values(?,?,?,?,?,?,'true','DriverTask',?)";
			    		baseDao.execute(baseSql, flowOperationId,operationName,operationType,remark,nodeName,shortName,nodeId);		    		

			    }else {
			    	flowOperationId = ObjectToInterger(baseDao.getFieldDataByCondition("flow_operation", "fo_id", 
			    			"fo_nodeId = "+ nodeId +" and fo_name = '" + operationName +"' and fo_fdshortname='"+shortName+"'"));
			    	String updateSql = "update flow_operation set fo_name='"+operationName+"',fo_remark='"+remark+"' where fo_id="+flowOperationId;
			    	baseDao.execute(updateSql);
			    }
			  
				
				List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(deriveData);
				String transferId = "";
				 for (Map<Object,Object> map : store) {
				     map.put("ft_caller", "DriverTask");
				     map.put("ft_foid", flowOperationId);
				     if(map.get("ft_id")!=null && !"".equals(map.get("ft_id"))) {
				    	 transferId +="'"+map.get("ft_id")+"',";
				     }
				 }
				 //比对前一次映射，删除映射
				 if(!"".equals(transferId)) {
					 transferId = transferId.substring(0,transferId.length()-1);
					 baseDao.deleteByCondition("flow_transfer", "ft_id not in ("+transferId+") and ft_foid="+flowOperationId);
				 }
				 
				 List<String> list = SqlUtil.getInsertOrUpdateSql(store, "flow_transfer", "ft_id");
				 baseDao.execute(list);
			}else {
				BaseUtil.showError("请检查上一节点是否存在！");
			}
		}
		if(operationType.equals("Flow")){
			String operationName = ObjectToString(base.get("operationName"));
	    	String remark = ObjectToString(base.get("remark"));
	    	String flowName = ObjectToString(base.get("flowName"));
	    	String flowNodeName = ObjectToString(base.get("flowNodeName"));
	    	String flowNodeId = ObjectToString(base.get("flowNodeId"));
	    	String nodeId = ObjectToString(base.get("nodeId"));
			String nodeName = ObjectToString(base.get("nodeName"));
			String groupName = ObjectToString(base.get("groupName"));
	    	String flowCaller = ObjectToString(baseDao.getFieldDataByCondition("flow_define", "fd_caller", "fd_shortname='"+flowName+"'"));
	    	int  flowOperationId = 0;
	    	boolean checkOperation = baseDao.checkIf("flow_operation", "fo_nodeid='"+nodeId+"' and fo_fdshortname='"+shortName+"' and fo_name='"+operationName+"'");
	    	if(checkOperation) {
	    		flowOperationId = ObjectToInterger(baseDao.getFieldDataByCondition("flow_operation", "fo_id", 
		    			"fo_nodeId = "+ nodeId +" and fo_name = '" + operationName +"' and fo_fdshortname='"+shortName+"'"));
	    		String updateSql = "update flow_operation set fo_name=?,fo_flowname=?,fo_flownodename=?,"
	    				+ "fo_flowcaller=?,fo_flownodeid=?,fo_groupname=? where fo_nodeid='"+nodeId+"' and fo_fdshortname='"+shortName+"' and fo_name = '" + operationName +"'";
	    	    baseDao.execute(updateSql,operationName,flowName,flowNodeName,flowCaller,flowNodeId,groupName);
	    	}else {
	    		flowOperationId = baseDao.getSeqId("flow_operation_seq");
	    		String baseSql = "insert into flow_operation(fo_id,fo_name,fo_type,fo_nodeName,fo_flowname,"
	    				+ "fo_flownodename,fo_fdshortname,fo_flowcaller,fo_flownodeid,fo_groupname,fo_isduty,fo_nodeid) "
	    				+ "values(?,?,?,?,?,?,?,?,?,?,'true',?)";
	    		baseDao.execute(baseSql, flowOperationId,operationName,operationType,nodeName,flowName,
	    				flowNodeName,shortName,flowCaller,flowNodeId,groupName,nodeId);
	    	}
	    	List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(deriveData);
			String transferId = "";
			 for (Map<Object,Object> map : store) {
			     map.put("ft_caller", flowCaller);
			     map.put("ft_foid", flowOperationId);
			     if(map.get("ft_id")!=null && !"".equals(map.get("ft_id"))) {
			    	 transferId +="'"+map.get("ft_id")+"',";
			     }
			 }
			 //比对前一次映射，删除映射
			 if(!"".equals(transferId)) {
				 transferId = transferId.substring(0,transferId.length()-1);
				 baseDao.deleteByCondition("flow_transfer", "ft_id not in ("+transferId+") and ft_foid="+flowOperationId);
			 }
			 
			 List<String> list = SqlUtil.getInsertOrUpdateSql(store, "flow_transfer", "ft_id");
			 baseDao.execute(list);
			 Map<Object, Object> groupMap = BaseUtil.parseFormStoreToMap(groupData);
	    	 String nowItems=  JSONArray.toJSONString(groupMap.get("nowItems"));
	    	 String deleteItems=  JSONArray.toJSONString(groupMap.get("deleteItems"));
	    	 String groupNameUpdate = "UPDATE FLOW_OPERATION SET FO_REMARK = '"+remark+"',FO_GROUPNAME = '"+groupName+"'  WHERE fo_id="+flowOperationId;
			 baseDao.execute(groupNameUpdate);
			//保存提交操作字段信息
			List<Map<Object, Object>> nowItemsMaps = BaseUtil.parseGridStoreToMaps(nowItems);
			List<Map<Object, Object>> deleteMaps = BaseUtil.parseGridStoreToMaps(deleteItems);
			List<Map<Object, Object>> nowItemsMapsTemp = new ArrayList<Map<Object, Object>>();
			for (Map<Object, Object> map : nowItemsMaps) {//插入
				Map<Object, Object> temp = new HashMap<Object,Object>();
				temp.put("fgc_id", map.get("fgc_id"));
				temp.put("fgc_groupname", groupName);
				temp.put("fgc_field", map.get("field"));
				temp.put("fgc_new", map.get("isNew"));
				temp.put("fgc_requiredfield", map.get("main"));
				temp.put("fgc_read", map.get("read"));
				temp.put("fgc_fdshortname", flowName);
				temp.put("fgc_detno", map.get("detno"));
				temp.put("fgc_width", map.get("columnsWidth"));
				temp.put("fgc_role", map.get("fgc_role"));
				temp.put("fgc_rolecode", map.get("fgc_rolecode"));
				nowItemsMapsTemp.add(temp);
			}
			List<String> groupConfigDelete = new ArrayList<String>();
			for (Map<Object, Object> map : deleteMaps) {//插入
				groupConfigDelete.add("DELETE FROM FLOW_GROUPCONFIG where fgc_id = "+map.get("fgc_id"));
			}
			List<String> groupConfigUpdateOrInsert = SqlUtil.getInsertOrUpdateSql(nowItemsMapsTemp, "flow_groupconfig", "fgc_id");
			baseDao.execute(groupConfigUpdateOrInsert);
			baseDao.execute(groupConfigDelete);
		}
	}


	@Override
	public void saveJudgeOperation(String caller,String operation, String nextNodeName, String nodeName, String shortName,
			String nextNodeId, String nodeId, String condition,String remark) {
		condition = condition.replace("'", "''");
		int flowOperationId = 0;
		boolean checkOperation = baseDao.checkIf("flow_operation", "fo_nodeid='"+nodeId+"' and fo_fdshortname='"+shortName+"' and fo_name='"+operation+"'");
    	if(checkOperation) {
    		flowOperationId = ObjectToInterger(baseDao.getFieldDataByCondition("flow_operation", "fo_id", 
	    			"fo_nodeId = "+ nodeId +" and fo_name = '" + operation +"' and fo_fdshortname='"+shortName+"'"));
    		String updateSql = "update flow_operation set fo_name='"+operation+"',fo_remark='"+remark+"',fo_condition='"+condition+"' where fo_nodeid='"+nodeId+"' and fo_fdshortname='"+shortName+"' and fo_name = '" + operation +"'";
    	    baseDao.execute(updateSql);
    	}else {
    		flowOperationId = baseDao.getSeqId("flow_operation_seq");
    		String baseSql = "insert into flow_operation(FO_CONDITION,FO_ISDUTY,FO_NEXTNODEID,FO_NODEID,FO_ID,FO_NAME,FO_TYPE,FO_NEXTNODENAME"
    				+ ",FO_NODENAME,FO_REMARK,FO_FDSHORTNAME)"
    				+ " values("
    				+ "'"+condition+"',"
    				+ "'true',"
    				+ "'"+nextNodeId+"',"
    				+ "'"+nodeId+"',"
    				+ "'"+flowOperationId+"',"
    				+ "'"+operation+"',"
    				+ "'Judge',"
    				+ "'"+nextNodeName+"',"
    				+ "'"+nodeName+"',"
    				+ "'"+remark+"',"
    				+ "'"+shortName+"'"
    				+ ")";
    		baseDao.execute(baseSql);
    	}
		
	}
	/**
	 * 导入模板下载
	 * @param shortName
	 * @param caller
	 * @return
	 */
	public HSSFWorkbook getExcelTemplate(String caller){
		String shortName = getShortName(caller);
		//声明一个工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		//设置样式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFCellStyle redCellStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		HSSFFont redFont = workbook.createFont();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		redCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		redCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		redCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		redCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		redFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		redFont.setFontName("宋体");
		redFont.setFontHeight((short) 200);
		redFont.setColor(Font.COLOR_RED);
		redCellStyle.setFont(redFont);
		cellStyle.setFont(font);
		
		HSSFCell cell = null;
		int n = 0;
		//设置前两个必填列
		sheet.setColumnWidth(n, 8*512);
		cell = row.createCell(n);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("责任人");
		cell.setCellStyle(redCellStyle);
		n++;
		sheet.setColumnWidth(n, 8*512);
		cell = row.createCell(n);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("流程状态");
		cell.setCellStyle(redCellStyle);
		n++;
		sheet.setColumnWidth(n, 8*512);
		cell = row.createCell(n);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("标题");
		cell.setCellStyle(redCellStyle);
		n++;
		
		//设置流程状态下拉项
		//获取下拉候选项
		List<Object> dataList = baseDao.getFieldDatasByCondition("flow_node", "fn_nodename", "fn_fdshortname='"+shortName+"' and fn_nodename not in ('END','START')");
		CellRangeAddressList regions = new CellRangeAddressList(1, 500, 1, 1);		//有效单元格
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(dataList.toArray(new String[dataList.size()]));		//下拉内容  
		HSSFDataValidation data_validation = new HSSFDataValidation(regions,constraint);  	//绑定下拉框和区域
		sheet.addValidationData(data_validation);  	//对sheet页生效
		
		String captionSql = "select distinct fd_caption,fd_detno from form"
				+ " left join formdetail on fo_id=fd_foid"
				+ " where fo_caller='"+caller+"'"
				+ " and fd_type<>'H' and nvl(fd_logictype,1) <> 'title' order by fd_detno";
		List<Map<String, Object>> captionList = baseDao.queryForList(captionSql);
		for(Map<String, Object> map : captionList){
			sheet.setColumnWidth(n, 8*512);
			cell = row.createCell(n);
			cell.setCellType(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(String.valueOf(map.get("FD_CAPTION")));
			cell.setCellStyle(cellStyle);
			n++;
		}
		return workbook;
	}
	
	/**
	 * 保存导入数据
	 * @return 
	 */
	public Map<String, Object> saveByExcel(String caller, FileUpload fileUpload){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		InputStream is = null;
		CommonsMultipartFile file = fileUpload.getFile();
		String shortName = getShortName(caller);
		try {
			//获取excel列号与字段的对应关系
			String fieldSql = "select distinct fd_field FD_FIELD,fd_caption,fd_detno from form left join formdetail on fo_id=fd_foid"
					+ " where fo_caller='"+caller+"'"
					+ " and fd_type<>'H' and nvl(fd_logictype,1) <> 'title' order by fd_detno";
			List<Map<String,Object>> fieldList = baseDao.queryForList(fieldSql);
			
			Map<String,Integer> map = new HashMap<String, Integer>();
			for(int m = 0; m < fieldList.size(); m++){
				map.put(String.valueOf(fieldList.get(m).get("FD_CAPTION")), m+3);
			}
			int codeNumber = map.get("单据编号");
			int manNumber = 0;
			if(map.get("创建人") == null){
				if(map.get("录入人") != null){
					manNumber = map.get("录入人");
				}
			}else{
				manNumber = map.get("创建人");
			}
			is = file.getInputStream();
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			//校验excel是否符合要求
			boolean isValid = validExcel(caller, sheet);
			if(isValid){
				//解析Excel
				int rowSum = sheet.getLastRowNum();
				int cellSum = sheet.getRow(0).getLastCellNum();
				//检查单据编号是否已存在
				StringBuilder codes = new StringBuilder();
				for(int x = 1; x < rowSum; x++){
					Cell codeCell = sheet.getRow(x).getCell(codeNumber);
					if(codeCell != null){
						codes.append("'" + sheet.getRow(x).getCell(codeNumber).getStringCellValue() + "',");
					}
				}
				if(codes.length() > 0){
					codes.deleteCharAt(codes.length()-1);
					List<Map<String, Object>> codesList = baseDao.queryForList("select ct_code from customtable where ct_caller='"+caller+"' and ct_code in (" + codes + ")");
					if(codesList != null){
						StringBuilder codesStr = new StringBuilder();
						for(Map<String, Object> codesMap : codesList){
							codesStr.append(codesMap.get("CT_CODE")+",");
						}
						returnMap.put("data", "已存在编号为:" + codesStr.deleteCharAt(codesStr.length()-1).toString() + "的记录");
						returnMap.put("success", false);
					}
				}
				
				StringBuilder sb = new StringBuilder("begin ");
				for(int i = 1; i <= rowSum; i++){
					int customtableId = baseDao.getSeqId("customtable_seq");
					int flowInstanceId = baseDao.getSeqId("flow_instance_seq");
					StringBuilder valueString = new StringBuilder();
					StringBuilder insertSql = new StringBuilder();
					String code = null;
					insertSql.append("insert into customtable(");
					for(Map<String,Object> mappingMap : fieldList){
						insertSql.append(mappingMap.get("FD_FIELD")+",");
					}
					//获取类型为title的字段名
					String title = String.valueOf(baseDao.getFieldDataByCondition("form left join formdetail on fo_id=fd_foid", "fd_field", "fo_caller='"+caller+"' and fd_logictype='title'"));
					insertSql.append("ct_caller,ct_id,"+title+") values(");
					Row row = sheet.getRow(i);
					for(int j = 0; j < cellSum-3; j++){
						Cell cell = row.getCell(j+3);
						if(cell != null){
							if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								if(HSSFDateUtil.isCellDateFormatted(cell)){
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									String cellValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
									valueString.append("to_date('"+cellValue+"','yyyy-MM-dd'),");
								}else{
									valueString.append("'"+cell.getStringCellValue()+"',");
								}
							}else{
								valueString.append("'"+cell.getStringCellValue()+"',");
							}
						}else{
							if(j+3 == codeNumber){
								//自动生成单号
								code = singleFormItemsService.getCodeString(caller, "flow_instance", 2);
								valueString.append("'"+code+"',");
							}else{
								valueString.append("null,");
							}
						}
					}
					valueString.deleteCharAt(valueString.length()-1);
					String flowTitle = row.getCell(2).getStringCellValue();
					insertSql.append(valueString.toString() + ",'" + caller + "',"+customtableId+",'"+flowTitle+"');");
					sb.append(insertSql);
					//插入实例
					String flowStatus = row.getCell(1).getStringCellValue();
					StringBuilder createInstanceSql = new StringBuilder("insert into flow_instance(fi_id,fi_startman,fi_startmancode,fi_fdshortname,fi_nodeid,fi_nodename,"
							+ "fi_codevalue,fi_keyvalue,fi_handler,fi_handlercode,fi_status,fi_title,fi_starttime,fi_time,fi_caller"
							+ ") values("+flowInstanceId+",");
					String createMan = null;
					Object createCode = null;
					if(row.getCell(manNumber) != null){
						createMan = row.getCell(manNumber).getStringCellValue();
						createInstanceSql.append("'"+createMan+"',");
						List<Map<String, Object>> empList = baseDao.queryForList("select em_code from employee where em_name='"+createMan+"'");
						if(empList != null && empList.size()>0){
							createCode = empList.get(0).get("EM_CODE");
							createInstanceSql.append("'"+createCode+"',");
						}else{
							createInstanceSql.append("null,");
						}
					}else{
						createInstanceSql.append("null,null,");
					}
					createInstanceSql.append("'"+shortName+"'");
					Object fnId = String.valueOf(baseDao.getFieldDataByCondition("flow_node", "fn_id", "fn_fdshortname='"+shortName+"' and fn_nodename='"+flowStatus+"'"));
					createInstanceSql.append("," + fnId + ",'" + flowStatus + "','");
					
					if(row.getCell(codeNumber) != null){			//append 单据编号
						createInstanceSql.append(row.getCell(codeNumber).getStringCellValue()+"',"+customtableId+",'");
					}else{
						createInstanceSql.append(code + "',"+customtableId+",'");
					}
					String emName = row.getCell(0).getStringCellValue();
					List<Map<String, Object>> emNameList = baseDao.queryForList("select em_code from employee where em_name='" + emName + "' or em_code='"+emName+"'");
					if(emNameList != null){
						createInstanceSql.append(emName + "','" + emNameList.get(0).get("EM_CODE") + "','using','");
					}else{
						createInstanceSql.append(emName + "',null,'using','");
					}
					createInstanceSql.append(row.getCell(2).getStringCellValue()+"',sysdate,sysdate,'"+caller+"');");
					sb.append(createInstanceSql);
					sb.append("insert into flow_instanceRole(fir_id,fir_mancode,fir_type,fir_fiid) values(flow_instanceRole_seq.nextval,'"+emNameList.get(0).get("EM_CODE")+"','duty',"+flowInstanceId+");");
					if(createCode != null)
						sb.append("insert into flow_instanceRole(fir_id,fir_mancode,fir_type,fir_fiid) values(flow_instanceRole_seq.nextval,'"+createCode+"','creator',"+flowInstanceId+");");
				}
				sb.append(" end;");
				//将数据保存到数据库中
				baseDao.execute(sb.toString().replaceAll("'null'", "null"));
				returnMap.put("data", "恭喜,导入成功!");
				returnMap.put("success", true);
			}else{
				returnMap.put("data", "excel校验不通过");
				returnMap.put("success", false);
			}
			return returnMap;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnMap;
	}
	
	/**
	 * excel检验
	 * @param caller
	 * @param sheet
	 * @return
	 */
	private boolean validExcel(String caller, HSSFSheet sheet){
		HSSFRow row = sheet.getRow(0);
		int rowNum = sheet.getLastRowNum();
		int cellNum = row.getLastCellNum();
		List<String> cellValueList = new ArrayList<String>();
		for(Cell cell : row){
			cellValueList.add(cell.getStringCellValue());
		}
		String shortName = getShortName(caller);
		List<Map<String, Object>> fieldList = baseDao.queryForList("select distinct fd_caption,fd_detno from form "
				+ "left join formdetail on fo_id=fd_foid "
				+ "where fd_type<>'H' "
				+ "and fo_caller='"+caller+"' and nvl(fd_logictype,1) <> 'title' order by fd_detno");
		if(cellValueList.size() != fieldList.size()+3){
			return false;
		}else{
			for(int k = 0; k < fieldList.size(); k++){
				if(!cellValueList.get(k+3).equals(fieldList.get(k).get("FD_CAPTION"))){
					return false;
				}
			}
			if("责任人".equals(cellValueList.get(0)) && "流程状态".equals(cellValueList.get(1)) && "标题".equals(cellValueList.get(2))){
				for(int j = 1; j < rowNum; j++){
					if(StringUtils.isEmpty(sheet.getRow(j).getCell(0).getStringCellValue()) ||
						StringUtils.isEmpty(sheet.getRow(j).getCell(1).getStringCellValue()) ||
						StringUtils.isEmpty(sheet.getRow(j).getCell(2).getStringCellValue())){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 保存决策节点
	 */
	@Override
	public void saveJudgeNode(String shortName,String operationName,String remark) {
		
		boolean checkJudgeNode = baseDao.checkIf("flow_node",
				"fn_nodename='" + operationName + "' and fn_fdshortname='" + shortName + "'");
		if (checkJudgeNode) {
			baseDao.execute("update flow_node set fn_nodename='"+operationName+"',fn_remark='"+remark+"' where fn_nodename='"+operationName+"' and fn_fdshortname='"+shortName+"'");
		}else {
			String sql = "insert into flow_node(fn_id,fn_nodename,fn_fdshortname,fn_type,fn_remark) values(flow_node_seq.nextval,'"+operationName
					+"','"+shortName+"','judge','"+remark+"')";
			baseDao.execute(sql);
		}
		
	}
	
	private String FlowInstanceInsertSql(FlowInstance flowInstance) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(flowInstance.TABLE);
		sql.append("(");
		sql.append(flowInstance.FIELD);
		sql.append(") values(");
		sql.append(flowInstance.getValues(flowInstance));
		sql.append(")");
		return sql.toString();
	}


	@Override
	public void deleteTab(String shortName, String groupName) {
		//获取当前版本所有在使用的groupName
		Set<Object> set = new HashSet<Object>();
		
		List<Object> listNode = baseDao.getFieldDatasByCondition("flow_node", "fn_groups", "fn_fdshortname='"+shortName+"'");
		for (Object object : listNode) {
			if(object!=null){
				JSONArray json = JSONArray.parseArray(String.valueOf(object));
				for (Object object2 : json) {
					JSONObject group = JSONObject.parseObject(String.valueOf(object2));
					set.add(group.get("name"));
				}
			}
		}
		
		List<Object> listOperation = baseDao.getFieldDatasByCondition("flow_operation", "fo_groupname", "fo_fdshortname='"+shortName+"'");
		for (Object object : listOperation) {
			set.add(object);
		}
		
		//判断groupName是否在使用中
		 Iterator<Object> i = set.iterator();//先迭代出来  
         
        while(i.hasNext()){//遍历  
           if(groupName.equals(i.next())) {
        	   BaseUtil.showError("分组："+groupName+"已经被其它节点或者操作使用过，不允许删除！");
           }
        }
        
        baseDao.deleteByCondition("flow_groupconfig", "fgc_fdshortname='"+shortName+"' and fgc_groupName='"+groupName+"'", new Object[] {});
	}


	@Override
	public void updateTab(String shortName, String tabName,String tabs) {
		boolean checkIsOperationTab = baseDao.checkIf("flow_operation", "fo_groupname='"+tabName+"'");
		if(checkIsOperationTab) {
			BaseUtil.showError("不允许更新操作的页面配置，请重新选择！");
		}
		baseDao.deleteByCondition("FLOW_GROUPCONFIG", "FGC_GROUPNAME='"+tabName+"' and FGC_FDSHORTNAME='"+shortName+"'", new Object[] {});
		List<Map<Object, Object>>  maps = BaseUtil.parseGridStoreToMaps(tabs);
		List<String> list = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			list.add("INSERT INTO FLOW_GROUPCONFIG(FGC_ID,FGC_GROUPNAME,FGC_FIELD,FGC_NEW,FGC_REQUIREDFIELD,FGC_READ,FGC_REMARK,FGC_FDSHORTNAME,FGC_DETNO,FGC_WIDTH,FGC_ROLE,FGC_ROLECODE) values("
					+ "FLOW_GROUPCONFIG_SEQ.nextval,"
					+ "'"+tabName+"',"
					+ "'"+map.get("field")+ "',"
					+ "'"+map.get("isNew")+ "',"
					+ "null,null,null,"
					+ "'"+shortName + "',"
					+ "'" +map.get("detno") + "',"
					+ "'" +map.get("width") + "',"
					+ "null,null)");
		}
		baseDao.execute(list);
	}
	
	public static List removeDuplicate(List list) {   
	    HashSet h = new HashSet(list);   
	    list.clear();   
	    list.addAll(h);   
	    return list;   
	} 
}



