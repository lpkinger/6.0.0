package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SysnavigationDao;
import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.SysNavigation;
import com.uas.erp.service.common.SysnavigationService;

@Service("sysnavigationService")
public class SysnavigationServiceImpl implements SysnavigationService {
	@Autowired
	private SysnavigationDao sysnavigationDao;
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<JSONTree> getJSONTree() {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<SysNavigation> list = sysnavigationDao.getSysNavigations();
		for (SysNavigation s : list) {
			JSONTree ct = new JSONTree();
			if (s.getSn_ParentId() == 0) {
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}

	private JSONTree recursionFn(Collection<SysNavigation> list, SysNavigation s) {
		JSONTree jt = new JSONTree();
		jt.setId(String.valueOf(s.getSn_Id()));
		jt.setParentId(String.valueOf(s.getSn_ParentId()));
		jt.setText(s.getSn_DisplayName());
		jt.setQtip(s.getSn_DisplayName());
		jt.setExpanded(true);
		jt.setCaller(s.getSn_caller());
		if (s.getSn_addurl() != null)
			jt.setAddurl(s.getSn_addurl());
		if (hasChild(list, s)) {
			if (s.getSn_ParentId() == 0) {
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setQtitle("");
			jt.setLeaf(false);
			List<SysNavigation> childList = getChildList(list, s);
			Iterator<SysNavigation> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				SysNavigation n = (SysNavigation) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setQtitle("");
			jt.setUrl(s.getSn_Url());
			jt.setAllowDrag(true);
			jt.setLeaf(true);
			jt.setChildren(new ArrayList<JSONTree>());
			jt.setShowMode(s.getSn_showmode());
		}
		return jt;
	}

	// 判断是否有子节点
	private boolean hasChild(Collection<SysNavigation> list, SysNavigation s) {
		return getChildList(list, s).size() > 0 ? true : false;
	}

	// 获取子节点列表
	private List<SysNavigation> getChildList(Collection<SysNavigation> list, SysNavigation s) {
		List<SysNavigation> li = new ArrayList<SysNavigation>();
		Iterator<SysNavigation> it = list.iterator();
		while (it.hasNext()) {
			SysNavigation n = (SysNavigation) it.next();
			// 父id等于id时 有子节点 添加该条数据
			if ((n.getSn_ParentId()) == (s.getSn_Id())) {
				li.add(n);
			}
		}
		return li;
	}

	/**
	 * 改进的方法
	 * 
	 * @param parentId
	 *            父节点id 通过此参数，每次点击父节点时，若其子节点未加载，就将parentId传回来，返回其子节点
	 */
	@Override
	public List<JSONTree> getJSONTreeByParentId(int parentId, String condition, Integer _noc) {
		_noc = _noc == null ? 0 : _noc;
		return sysnavigationDao.getJSONTreeByParentId(parentId, condition, SystemSession.getUser(), _noc);
	}

	/**
	 * treepanel的searchfield从后台拿搜索结果
	 * 
	 * @param search
	 *            条件
	 */
	@Override
	public List<JSONTree> getJSONTreeBySearch(String search, Employee employee, Boolean isPower) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		Set<SysNavigation> list = sysnavigationDao.getSysNavigationsBySearch(search, isPower);
		for (SysNavigation s : list) {
			JSONTree ct = new JSONTree();
			if (s.getSn_ParentId() == 0 && s.getSn_using() == 1) {
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}

	@Override
	public List<JSONTree> getAllNavigation(int parentId, String condition) {
		return sysnavigationDao.getAllNavigation(parentId, condition);
	}

	@Override
	public Map<String, Object> getNavigationDetails(int id) {
		Employee employee = SystemSession.getUser();
		//String master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		Map<String, Object> map = new HashMap<String, Object>();
		Object[] ob = baseDao.getFieldsDataByCondition("uas_sysnavigation", new String[] { "sn_url", "sn_isleaf", "sn_caller", "sn_isleaf" },
				"sn_id=" + id);
		String caller = "", pagedesc = "", servicedesc = "";
		if (ob[2] == null || "".equals(ob[2])) {
			caller = id + "";
		} else {
			caller = ob[2].toString();
		}
		/*SpObserver.putSp(Constant.UAS_CLOUD);
		Object[] ob = baseDao.getFieldsDataByCondition("sysnavigation", new String[] { "sn_url", "sn_isleaf", "sn_caller", "sn_isleaf" },
				"sn_id=" + id);
		String caller = "", pagedesc = "", servicedesc = "";
		if (ob[2] == null || "".equals(ob[2])) {
			caller = id + "";
		} else {
			caller = ob[2].toString();
		}
		Object[] desc = baseDao.getFieldsDataByCondition("PAGEINSTRUCTION", new String[] { "pagedesc", "servicedesc" }, "caller='" + caller
				+ "'");
		if (desc != null) {
			pagedesc = desc[0] == null ? "" : desc[0].toString();
			servicedesc = desc[1] == null ? "" : desc[1].toString();
		}
		SpObserver.putSp(master);*/
		map.put("url", ob[0] == null ? "" : ob[0].toString());
		map.put("caller", caller);
		map.put("pagedesc", pagedesc);
		map.put("servicedesc", servicedesc);
		map.put("leaf", ob[3] == null ? "" : ob[3].toString());
		return map;
	}

	@Override
	public List<JSONTree> getJSONNavigationTreeBySearch(String search) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		Set<SysNavigation> list = sysnavigationDao.getNavigationTreeBySearch(search);
		for (SysNavigation s : list) {
			JSONTree ct = new JSONTree();
			if (s.getSn_ParentId() == 0 && s.getSn_show() == 1) {
				ct = recursionNavigation(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}

	private JSONTree recursionNavigation(Collection<SysNavigation> list, SysNavigation s) {
		JSONTree jt = new JSONTree();
		jt.setId(String.valueOf(s.getSn_Id()));
		jt.setParentId(String.valueOf(s.getSn_ParentId()));
		jt.setText(s.getSn_standardDesc());
		jt.setExpanded(true);
		jt.setCaller(s.getSn_caller());
		if (hasChild(list, s)) {
			if (s.getSn_ParentId() == 0) {
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setQtitle("");
			jt.setLeaf(false);
			List<SysNavigation> childList = getChildList(list, s);
			Iterator<SysNavigation> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				SysNavigation n = (SysNavigation) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setQtitle(s.getSn_TabTitle());
			jt.setUrl(s.getSn_Url());
			jt.setAllowDrag(true);
			jt.setLeaf(true);
			jt.setChildren(new ArrayList<JSONTree>());
		}
		return jt;
	}

	@Override
	public void savePageinstruction(String caller, String field, String path, int id) {
		if (baseDao.checkIf("PAGEINSTRUCTION", "caller='" + caller + "'")) {
			baseDao.updateByCondition("PAGEINSTRUCTION", field + "='" + path + "'", "caller='" + caller + "'");
		} else {
			baseDao.execute("insert into PAGEINSTRUCTION(caller," + field + ") values('" + caller + "','" + path + "')");
		}
		Object desc = baseDao.getFieldDataByCondition("sysnavigation", "sn_standarddesc", "sn_id=" + id);
		String content = "", result = "";
		if ("pagedesc".equals(field)) {
			content = "上传说明文档";
		} else {
			content = "上传业务说明文档";
		}
		result = desc == null ? "上传成功" : "上传成功:" + desc.toString();
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), content, result, "pageInstruction|sn_id=" + id));
	}

	@Override
	public void downloadPageinstruction(int id, String field) {
		Object desc = baseDao.getFieldDataByCondition("sysnavigation", "sn_standarddesc", "sn_id=" + id);
		String content = "", result = "";
		if ("pagedesc".equals(field)) {
			content = "下载说明文档";
		} else {
			content = "下载业务说明文档";
		}
		result = desc == null ? "下载成功" : "下载成功:" + desc.toString();
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), content, result, "pageInstruction|sn_id=" + id));
	}

	@Override
	public void initAllNavigation() {
		try {
			baseDao.callProcedure("UPGRADE_UAS.init_navigation");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public Map<String, Object> getUpdatePath(int id, String num) {
		Map<String, Object> map = new HashMap<String, Object>();
		String uaspath = "", desc_ = "", path = "/";
		Object parentid = null;
		Object[] ob = baseDao.getFieldsDataByCondition("UAS_sysnavigation", new String[] { "sn_parentid", "nvl(sn_svnversion,0)" },
				"sn_id=" + id);
		parentid =ob[0].toString();
		int count =0;
		if("0".equals(parentid)){
			map.put("path", path);
		}else{
			Object pnum = baseDao.getFieldDataByCondition("UAS_sysnavigation", "SN_num", "sn_id=" + parentid);
			count = baseDao.getCount("select count(1) from sysnavigation where SN_num='" + pnum + "'");
			if (count > 0) {
				path = getNavigationPath(pnum.toString());
				map.put("path", path);
			}
		}
		map.put("addTo","0".equals(parentid)|| count > 0);// 默认添加到的路径存在
		uaspath =getUASNavigationPath(id);
		map.put("uaspath", uaspath);
		map.put("desc", desc_);
		map.put("needdeploy", !baseDao.checkIf("sysinfo", "VERSION>=" + ob[1]));
		return map;
	}

	@Override
	public String getUASNavigationPath(int id) {// 获取UAS标准版路径
		String path = "";
		int currentId = id;
		Object[] ob = baseDao
				.getFieldsDataByCondition("UAS_sysnavigation", new String[] { "sn_parentid", "sn_displayname" }, "sn_id=" + id);
		currentId = Integer.parseInt(ob[0].toString());
		if (currentId == 0) {
			path = "/";
		}
		while (currentId > 0) {
			ob = baseDao.getFieldsDataByCondition("UAS_sysnavigation", new String[] { "sn_parentid", "sn_displayname" }, "sn_id="
					+ currentId);
			currentId = Integer.parseInt(ob[0].toString());
			if (currentId == 0) {
				path = "/" + ob[1].toString() + path;
			} else {
				path = "/" + ob[1].toString() + path;
			}
		}
		return path;
	}

	@Override
	public String getNavigationPath(String num) {
		String path = "";
		int currentId = 0;
		SqlRowList rs = baseDao.queryForRowSet("select sn_id from (select * from sysnavigation where sn_num=? order by sn_using desc "
				+ ",sn_id ) where rownum<2", num);
		try {
			if (rs.next()) {
				currentId = rs.getInt("sn_id");
				Object[] ob = baseDao.getFieldsDataByCondition("sysnavigation", new String[] { "sn_parentid", "sn_displayname" }, "sn_id="
						+ currentId);
				path = "/" + ob[1].toString() + path;
				currentId = Integer.parseInt(ob[0].toString());
				while (currentId > 0) {
					ob = baseDao.getFieldsDataByCondition("sysnavigation", new String[] { "sn_parentid", "sn_displayname" }, "sn_id="
							+ currentId);
					currentId = Integer.parseInt(ob[0].toString());
					if (currentId == 0) {
						path = "/" + ob[1].toString() + path;
					} else {
						path = "/" + ob[1].toString() + path;
					}
				}
			}
		}catch(Exception ex){
			return "";
		}
		return path;
	}

	@Override
	public void updateNavigation(int id, int addToId) {
		if (addToId==-1) {//添加到默认位置
			if(baseDao.checkIf("UAS_SYSNAVIGATION", "SN_PARENTID=0 AND SN_ID="+id)){//添加一级菜单
				Object snid=baseDao.getFieldDataByCondition("sysnavigation","min(sn_id)", "sn_num=(select sn_num from uas_sysnavigation where sn_id="+id+")");
				if(snid!=null){//一级菜单已存在
					addToId=Integer.parseInt(snid.toString());
				}else{//一级菜单不存在已存在
					addToId=baseDao.getSeqId("SYSNAVIGATION_SEQ");
					Object detno=baseDao.getFieldDataByCondition("SYSNAVIGATION", "NVL(MAX(SN_DETNO),0)+1", "SN_PARENTID=0");
					baseDao.execute("INSERT INTO SYSNAVIGATION(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
								+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
								+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) SELECT  "+addToId+",SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
								+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detno+",SN_ICON,SN_LOGIC,"
								+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION FROM UAS_SYSNAVIGATION WHERE SN_ID="+id);	
					baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
							+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级','升级成功','Sysnavigation|sn_id="+id+"','"+addToId+"')");
				}
			}else{
				SqlRowList rs =baseDao.queryForRowSet("select min(sn_id) sn_id from sysnavigation where sn_num =(select sn_num  from uas_sysnavigation where  sn_id= "+id+")");
				if(rs.next()&&rs.getInt("sn_id")!=-1){
					addToId=rs.getInt("sn_id");//获得要添加到的导航id
				}else{//升级菜单在客户中不存在，先把菜单添加到选中升级的菜单的父级菜单中
					SqlRowList rs1 =baseDao.queryForRowSet("select min(sn_id) sn_id from  sysnavigation Where sn_num=(Select b.sn_num from uas_sysnavigation a left"
							+ " Join uas_sysnavigation b on a.sn_parentid=b.sn_id where a.sn_id="+id+")");
					if(rs1.next()){
						addToId=baseDao.getSeqId("SYSNAVIGATION_SEQ");
						Object detno=baseDao.getFieldDataByCondition("sysnavigation", "nvl(max(sn_detno),0)+1", "sn_parentid="+rs1.getInt("sn_id"));
						baseDao.execute("insert into sysnavigation(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
								+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
								+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) select "+addToId+","
								+ "SN_DISPLAYNAME," + rs1.getInt("sn_id")
								+ ",SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,"
								+ "SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detno+",SN_ICON,SN_LOGIC,SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,"
								+ "SN_STANDARDDESC,SN_NUM,SN_SVNVERSION from uas_sysnavigation where sn_id=" + id);
						baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
								+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级','升级成功','Sysnavigation|sn_id="+id+"','"+addToId+"')");
						addDefaultPageNavigation(id,rs1.getInt("sn_id"),detno);
					}
				}
			}
			recursionNavigation(id,addToId);
		} else {//添加到用户选择的菜单下
			int addToIdN=baseDao.getSeqId("SYSNAVIGATION_SEQ");
			Object detno=baseDao.getFieldDataByCondition("SYSNAVIGATION", "NVL(MAX(SN_DETNO),0)+1", "SN_PARENTID="+addToId);
			baseDao.execute("INSERT INTO SYSNAVIGATION(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
						+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
						+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) SELECT  "+addToIdN+",SN_DISPLAYNAME,"+addToId+",SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
						+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detno+",SN_ICON,SN_LOGIC,"
						+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION FROM UAS_SYSNAVIGATION WHERE SN_ID="+id);	
			baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
					+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级','升级成功','Sysnavigation|sn_id="+id+"','"+addToIdN+"')");
			addDefaultPageNavigation(id,addToId,detno);
			recursionNavigation(id,addToIdN);
		}
		baseDao.callProcedure("UPGRADE_UAS.init_navigation");//刷新全功能导航更新状态		
	}
	/*
	 * 添加对应维护界面导航
	 * id:uas id  addToId 要添加到的父级菜单id  detno 当前序号
	 */
	private void addDefaultPageNavigation(int id,int addToId,Object detno){
		Object[] ob=baseDao.getFieldsDataByCondition("UAS_SYSNAVIGATION", new String[]{"SN_CALLER","SN_ADDURL","SN_PARENTID"}, "SN_ID="+id);
		if(ob!=null&&ob[0]!=null&&ob[1]!=null&&!"".equals(ob[0].toString())&&!"".equals(ob[1].toString())){
			int defaultPageCount=baseDao.getCount("select count(1) from sysnavigation where sn_caller='"+ob[0]+"' and SN_URL='"+ob[1]+"' and SN_PARENTID="+addToId);//对应维护界面导航是否已存在
			Object defaultPageId=baseDao.getFieldDataByCondition("UAS_SYSNAVIGATION", "sn_id", "SN_CALLER='"+ob[0]+"' and SN_URL='"+ob[1]+"' and SN_PARENTID="+ob[2]);//获得UAS导航中维护界面sn_id
			int detnoN=Integer.parseInt(detno.toString())+1;
			if(defaultPageCount==0&&defaultPageId!=null){
				int snid=baseDao.getSeqId("SYSNAVIGATION_SEQ");
				baseDao.execute("insert into sysnavigation(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
						+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
						+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) select "+snid+","
						+ "SN_DISPLAYNAME," +addToId
						+ ",SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,"
						+ "SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detnoN +",SN_ICON,SN_LOGIC,SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,"
						+ "SN_STANDARDDESC,SN_NUM,SN_SVNVERSION from uas_sysnavigation where sn_id=" + defaultPageId);
				baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
						+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级','升级成功','Sysnavigation|sn_id="+defaultPageId+"','"+snid+"')");
			}
		}
	}
	private void recursionNavigation(int id, int addToId) {
		int addToIdN=0;
		SqlRowList rs = baseDao.queryForRowSet("select sn_id from uas_sysnavigation where sn_updateflag=1 and sn_parentid=" + id+" order by sn_detno");
		while(rs.next()){
			SqlRowList rs1 = baseDao.queryForRowSet("Select min(sn_id) sn_id from sysnavigation where sn_num=(select sn_num from uas_sysnavigation where sn_id="+rs.getInt("sn_id")+") and sn_parentid="+addToId);
			if(rs1.next()&&rs1.getInt("sn_id")!=-1){
				addToIdN=rs1.getInt("sn_id");
			}else{
				addToIdN=baseDao.getSeqId("SYSNAVIGATION_SEQ");
				Object detno=baseDao.getFieldDataByCondition("sysnavigation", "nvl(max(sn_detno),0)+1", "sn_parentid="+addToId);
				baseDao.execute("insert into sysnavigation(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
						+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
						+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) select "+addToIdN+","
						+ "SN_DISPLAYNAME," + addToId
						+ ",SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,"
						+ "SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detno+",SN_ICON,SN_LOGIC,SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,"
						+ "SN_STANDARDDESC,SN_NUM,SN_SVNVERSION from uas_sysnavigation where sn_id=" + rs.getInt("sn_id"));
				baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
						+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级','升级成功','Sysnavigation|sn_id="+rs.getInt("sn_id")+"','"+addToIdN+"')");
				//添加对应维护界面导航
				addDefaultPageNavigation(rs.getInt("sn_id"),addToId,detno);
			}
			recursionNavigation(rs.getInt("sn_id"),addToIdN);
		}
	}

	@Override
	public Boolean checkUpgrade() {
		try {
			int count=baseDao.getCount("select count(1) from UAS_sysnavigation");
			if(count==0){
				initAllNavigation();
			}
			return baseDao.checkIf("UAS_sysnavigation", "sn_updateflag=1");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	@Override
	@CacheEvict(value = "tree", allEntries = true)
	public String refreshSysnavigation(){
		return "";
	}

	@Override
	public Map<String, Object> getUpdateInfo(String num) {
		Map<String, Object> map = new HashMap<String, Object>();
		String  desc_ = "";
		Employee employee = SystemSession.getUser();
		String master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		SpObserver.putSp(Constant.UAS_CLOUD);
		// 根据标识号获得最新的升级说明
		SqlRowList rs = baseDao.queryForRowSet("select log_remark from (select * from SYSNAVIGATIONUPGRADE_LOG where log_numid=?"
				+ " order by LOG_VERSION desc) where ROWNUM<2", num);
		if (rs.next()) {
			desc_ = rs.getGeneralString("log_remark");
		}
		SpObserver.putSp(master);
		map.put("desc", desc_);
		return map;
	}

	@Override
	public List<CheckBoxTree> getAllCheckTree() {
		List<CheckBoxTree> trees = new ArrayList<CheckBoxTree>();
		trees=sysnavigationDao.getCheckTreeByParentId(0);
		for (CheckBoxTree tree : trees) {
			if(!tree.isLeaf()){
				List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
				children = recursionCheckTreeFn(tree);
				tree.setChildren(children);
			}
		}
		return trees;
	}
		
	private List<CheckBoxTree> recursionCheckTreeFn(CheckBoxTree tree) {
		List<CheckBoxTree> trees = new ArrayList<CheckBoxTree>();
		trees=sysnavigationDao.getCheckTreeByParentId(Integer.parseInt(tree.getId().toString()));
		for (CheckBoxTree ct : trees) {
			if(!ct.isLeaf()){
				List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
				children = recursionCheckTreeFn(ct);
				ct.setChildren(children);
			}
		}
		return trees;
	}

	@Override
	public Map<String, Object> updateAllNavigation() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("res","");
		modelMap.put("count",0);
		modelMap=recursionAllNavigation(0,0,modelMap);	
		baseDao.callProcedure("UPGRADE_UAS.init_navigation");//刷新全功能导航更新状态	
		return modelMap;
	}
	private Map<String, Object> recursionAllNavigation(int id, int addToId,Map<String, Object> map) {
		String res=map.get("res").toString();
		int count=Integer.parseInt(map.get("count").toString());
		int addToIdN=0;
		String resPath="";
		SqlRowList rs = baseDao.queryForRowSet("select sn_id,sn_displayname from uas_sysnavigation where sn_updateflag=1 and sn_parentid=" + id+" order by sn_detno");
		while(rs.next()){
			SqlRowList rs1 = baseDao.queryForRowSet("Select min(sn_id) sn_id from sysnavigation where sn_num=(select sn_num from uas_sysnavigation where sn_id="+rs.getInt("sn_id")+") and sn_parentid="+addToId);
			if(rs1.next()&&rs1.getInt("sn_id")!=-1){//根据标识好查找
				addToIdN=rs1.getInt("sn_id");
			}else{
				SqlRowList rs2 = baseDao.queryForRowSet("Select min(sn_id) sn_id from sysnavigation where sn_num is null and SN_DISPLAYNAME = '"+rs.getString("sn_displayname")+"' and sn_parentid="+addToId);
				if(rs2.next()&&rs2.getInt("sn_id")!=-1){//根据描述查找
					baseDao.execute("update sysnavigation set sn_num=(select sn_num from uas_sysnavigation where sn_id="+rs.getInt("sn_id")+")  where sn_id="+rs2.getInt("sn_id"));
					addToIdN=rs2.getInt("sn_id");
				}else{//添加导航
					count=count+1;//成功升级数量
					addToIdN=baseDao.getSeqId("SYSNAVIGATION_SEQ");
					Object detno=baseDao.getFieldDataByCondition("sysnavigation", "nvl(max(sn_detno),0)+1", "sn_parentid="+addToId);
					baseDao.execute("insert into sysnavigation(SN_ID,SN_DISPLAYNAME,SN_PARENTID,SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,"
							+ "SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,SN_DELETEABLE,SN_USING,SN_SHOWMODE,SN_DETNO,SN_ICON,SN_LOGIC,"
							+ "SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,SN_STANDARDDESC,SN_NUM,SN_SVNVERSION) select "+addToIdN+","
							+ "SN_DISPLAYNAME," + addToId
							+ ",SN_URL,SN_TABTITLE,SN_USEREMAIL,SN_CODE,SN_ISLEAF,SN_DISPLAYNAME_EN,SN_DISPLAYNAME_TW,"
							+ "SN_DELETEABLE,SN_USING,SN_SHOWMODE,"+detno+",SN_ICON,SN_LOGIC,SN_CALLER,SN_LIMIT,SN_ADDURL,SN_LEVEL,SN_SHOW,"
							+ "SN_STANDARDDESC,SN_NUM,SN_SVNVERSION from uas_sysnavigation where sn_id=" + rs.getInt("sn_id"));
					baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
							+ "values (messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','导航栏升级（一键升级）','升级成功','Sysnavigation|sn_id="+rs.getInt("sn_id")+"','"+addToIdN+"')");
					//添加对应维护界面导航
					addDefaultPageNavigation(rs.getInt("sn_id"),addToId,detno);
					resPath=getUpgradeResPath(addToIdN);
					if(!"".equals(resPath)){
						res=res+resPath+"<br>";
					}
					map.put("res", res);
					map.put("count", count);
				}
			}
			recursionAllNavigation(rs.getInt("sn_id"),addToIdN,map);
		}
		return  map;
	}
	
	private String getUpgradeResPath(int id) {
		String path = "";
		int currentId = 0;
		Object[] ob = baseDao.getFieldsDataByCondition("sysnavigation", new String[] { "sn_parentid", "sn_displayname" },
						"sn_id="+ id);
		try {
			if(ob!=null){
				currentId = Integer.parseInt(ob[0].toString());
				path = "/" + ob[1].toString() + path;
				while (currentId > 0) {
					ob = baseDao.getFieldsDataByCondition("sysnavigation", new String[] { "sn_parentid", "sn_displayname" }, "sn_id="
							+ currentId);
					currentId = Integer.parseInt(ob[0].toString());
					if (currentId == 0) {
						path = "/" + ob[1].toString() + path;
					} else {
						path = "/" + ob[1].toString() + path;
					}
				}
			}
		}catch(Exception ex){
			return "";
		}
		return path;
	}

	@Override
	public List<Map<String, Object>> getAddBtn() {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Employee employee = SystemSession.getUser();
		if(employee.getEm_type().toLowerCase().equals("admin")){
			SqlRowList rs = baseDao.queryForRowSet(" select distinct fo_title as title,sn_addurl as url,MAX(sn_icon) as iconcls,MAX(sn_id) as id, " +
				" Max(CASE WHEN a.cu_count IS NULL THEN 0 ELSE a.cu_count END) as count from sysnavigation sn " +
			    " left join FORM on sn_caller = fo_caller " +
			    " left join (select cu_addurl,cu_count from CommonUse where cu_emid='"+employee.getEm_id()+"' and cu_addurl is not null order by cu_count desc) a " +
			    " on a.cu_addurl = sn.sn_addurl " +
			    " where sn_isleaf='T' and sn_addurl <> ' ' and fo_title <> ' ' " +
			    " group by fo_title,sn_addurl order by count desc ");
			if (rs.next()) {
				datas = rs.getResultList();
			}else{
				BaseUtil.showError("当前账套有误，无法读取制单按钮");
			}
			return datas;
		}
		SqlRowList rs = baseDao.queryForRowSet("select fo_title as title,sn_addurl as url,max(sn_icon) as iconcls,max(sn_id) as id, " +
				" Max(CASE WHEN a.cu_count IS NULL THEN 0 ELSE a.cu_count END) as count from sysnavigation sn " + 
				" left join FORM on sn_caller = fo_caller " +
				" left join (select cu_addurl,cu_count from CommonUse where cu_emid='"+employee.getEm_id()+"' and cu_addurl is not null order by cu_count desc) a " +
			    " on a.cu_addurl = sn.sn_addurl " +
				" where sn_isleaf='T' and sn_addurl <> ' ' and fo_title <> ' ' and "+ 
				" ((sn_caller in (select distinct pp_caller from positionpower where pp_see=1 and "+ 
				" pp_joid in (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or "+ 
				" sn_caller in (select distinct pp_caller from personalpower where pp_see=1 and pp_emid=' "+employee.getEm_id()+"'))"+
				" group by fo_title,sn_addurl order by count desc ");
		if (rs.next()) {
			datas = rs.getResultList();
		}else{
			BaseUtil.showError("当前账户有误，无法读取制单按钮");
		}
		return datas;
	}
	
	@Override
	public List<JSONTree> getCommonUseTree(Employee employee) {
		List<JSONTree> trees = new ArrayList<JSONTree>();
		
		SqlRowList rs = baseDao.queryForRowSet("select * from commonuse2 where cu_emid=? and cu_group=-1 order by cu_index asc",employee.getEm_id());
		if(!rs.hasNext()) {
			SqlRowList data = baseDao.queryForRowSet("select * from (select * from COMMONUSE where cu_emid=? and cu_snid<>0 order by cu_lock desc,cu_count desc) where rownum<=20",employee.getEm_id());
			while(data.next()){
				JSONTree tree = new JSONTree();
				
				tree.setId(data.getString("cu_snid"));
				tree.setText(data.getString("cu_description"));
				tree.setQtip(data.getString("cu_description"));
				tree.setCls("x-tree-cls-node");
				tree.setUrl(data.getString("cu_url"));
				tree.setAddurl(data.getString("cu_addurl"));
				tree.setDeleteable(false);
				tree.setAllowDrag(false);
				tree.setLeaf(true);
				tree.setExpanded(false);
				
				trees.add(tree);
			}
		}else {
			List<JSONTree> ungroup = new ArrayList<JSONTree>();
			
			while(rs.next()){
				SqlRowList data = baseDao.queryForRowSet("select s.*,c.* from COMMONUSE2 c left join SysNavigation s on s.sn_id = c.cu_itemid where c.cu_emid=? and c.cu_group=0 and cu_groupid=? order by c.cu_index asc",employee.getEm_id(),rs.getInt("cu_itemid"));
				JSONTree tree = new JSONTree();
				List<JSONTree> children = new ArrayList<JSONTree>();
				
				while(data.next()) {
					JSONTree chil = new JSONTree();
					
					chil.setId(data.getString("cu_itemid"));
					chil.setText(data.getString("cu_text")!=null?data.getString("cu_text"):data.getString("sn_displayname"));
					chil.setQtip(data.getString("sn_displayname")!=null?data.getString("sn_displayname"):data.getString("cu_text"));
					chil.setCls("x-tree-cls-node");
					chil.setUrl(data.getString("sn_url")!=null?data.getString("sn_url"):data.getString("cu_url"));
					chil.setAddurl(data.getString("sn_addurl")!=null?data.getString("sn_addurl"):data.getString("cu_addurl"));
					chil.setDeleteable(false);
					chil.setAllowDrag(false);
					chil.setLeaf(true);
					// 如果是[未分组]组别直接加入第二级
					if(rs.getInt("cu_itemid") == -1) {
						ungroup.add(chil);
					}else {
						children.add(chil);
					}
				}
				
				// 如果是[未分组]组别不添加
				if(rs.getInt("cu_itemid") == -1) {
					// do nothing...
				}else {
					tree.setText(rs.getString("cu_text"));
					tree.setQtip(rs.getString("cu_text"));
					tree.setCls("x-tree-cls-parent");
					tree.setDeleteable(false);
					tree.setAllowDrag(false);
					tree.setLeaf(false);
					tree.setExpanded(rs.getInt("cu_expanded")!=0);
					tree.setChildren(children );
					
					trees.add(tree);
				}
			}
			trees.addAll(ungroup);
		}
		
		return trees;
	}
	
	@Override
	public List<JSONTree> searchCommonUseTree(Employee employee, String value) {
		List<JSONTree> trees = new ArrayList<JSONTree>();
		
		SqlRowList rs = baseDao.queryForRowSet("select * from commonuse2 where cu_emid=? and cu_group=-1 order by cu_index asc",employee.getEm_id());
		if(!rs.hasNext()) {
			SqlRowList data = baseDao.queryForRowSet("select * from (select * from (select * from COMMONUSE where cu_emid=? and cu_snid<>0 order by cu_lock desc,cu_count desc) where rownum<=20) where instr(cu_description, ?) > 0  ", employee.getEm_id(), value);
			while(data.next()){
				JSONTree tree = new JSONTree();
				
				tree.setId(data.getString("cu_snid"));
				tree.setText(data.getString("cu_description"));
				tree.setQtip(data.getString("cu_description"));
				tree.setCls("x-tree-cls-node");
				tree.setUrl(data.getString("cu_url"));
				tree.setAddurl(data.getString("cu_addurl"));
				tree.setDeleteable(false);
				tree.setAllowDrag(false);
				tree.setLeaf(true);
				tree.setExpanded(false);
				
				trees.add(tree);
			}
		}else {
			List<JSONTree> ungroup = new ArrayList<JSONTree>();
			
			while(rs.next()){
				SqlRowList data = baseDao.queryForRowSet("select s.*,c.* from COMMONUSE2 c left join SysNavigation s on s.sn_id = c.cu_itemid where c.cu_emid=? and c.cu_group=0 and cu_groupid=? and instr(cu_text, ?) > 0 order by c.cu_index asc", employee.getEm_id(), rs.getInt("cu_itemid"), value);
				JSONTree tree = new JSONTree();
				List<JSONTree> children = new ArrayList<JSONTree>();
				
				while(data.next()) {
					JSONTree chil = new JSONTree();
					
					chil.setId(data.getString("cu_itemid"));
					chil.setText(data.getString("cu_text")!=null?data.getString("cu_text"):data.getString("sn_displayname"));
					chil.setQtip(data.getString("sn_displayname")!=null?data.getString("sn_displayname"):data.getString("cu_text"));
					chil.setCls("x-tree-cls-node");
					chil.setUrl(data.getString("sn_url")!=null?data.getString("sn_url"):data.getString("cu_url"));
					chil.setAddurl(data.getString("sn_addurl")!=null?data.getString("sn_addurl"):data.getString("cu_addurl"));
					chil.setDeleteable(false);
					chil.setAllowDrag(false);
					chil.setLeaf(true);
					// 如果是[未分组]组别直接加入第二级
					if(rs.getInt("cu_itemid") == -1) {
						ungroup.add(chil);
					}else {
						children.add(chil);
					}
				}
				
				// 如果是[未分组]组别不添加
				if(rs.getInt("cu_itemid") == -1) {
					// do nothing...
				}else {
					if(children.size() != 0) {
						tree.setText(rs.getString("cu_text"));
						tree.setQtip(rs.getString("cu_text"));
						tree.setCls("x-tree-cls-parent");
						tree.setDeleteable(false);
						tree.setAllowDrag(false);
						tree.setLeaf(false);
						tree.setExpanded(rs.getInt("cu_expanded")!=0);
						tree.setChildren(children );
						tree.setExpanded(true);
						
						trees.add(tree);
					}
				}
			}
			trees.addAll(ungroup);
		}
		
		return trees;
	}
}
