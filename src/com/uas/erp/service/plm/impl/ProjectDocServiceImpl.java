package com.uas.erp.service.plm.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.ProjectDocPowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.plm.ProjectDocService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service("projectDocService")
public class ProjectDocServiceImpl implements ProjectDocService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private FormAttachService  formAttachService;
	@Autowired
	private ProjectDocPowerDao projectDocPowerDao;
	@Autowired
	private FormDao formDao;
	
	@Override
	public List<Map<String, Object>>  getProjectFileTree(String condition,String checked) {
		if(condition==null||"".equals(condition)){
			condition = "1=1";
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("pd_id",0);
		List<Map<String, Object>>  rootchild = getChildrenNodes(root, condition,checked);
		return rootchild;		
	}
	
	List<Map<String, Object>> getChildrenNodes(Map<String, Object> parentNode,String condition,String checked){
		
		SqlRowList rs = baseDao.queryForRowSet("select * from ProjectDoc where "+condition +" and pd_parentid ="+parentNode.get("pd_id")+" order by pd_detno asc");
		List<Map<String, Object>>  nodes = new ArrayList<Map<String, Object>> ();
		while(rs.next()){
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("pd_id",rs.getInt("pd_id"));
			node.put("pd_name",rs.getString("pd_name"));
			node.put("pd_taskname",rs.getString("pd_taskname"));
			node.put("pd_checked",rs.getInt("pd_checked"));
			node.put("pd_filepath",rs.getString("pd_filepath"));
			node.put("pd_operator",rs.getString("pd_operator"));
			node.put("pd_operatime",rs.getDate("pd_operatime")==null?"":DateUtil.format(rs.getDate("pd_operatime"), Constant.YMD_HMS));
			node.put("id",rs.getInt("pd_id"));
			if (rs.getInt("pd_kind")==0) {
				node.put("leaf",true);
				if(checked!=null) node.put("checked",false);
			}else {
				node.put("leaf",false);
				node.put("expanded", true);
				node.put("children", getChildrenNodes(node, condition,checked));
			}
			nodes.add(node);	
		}	
				
		return nodes;
	}
	
	@Override
	public Map<String,Object> getFileList(String formCondition,Integer id,Integer kind,Integer page,Integer start,
			Integer limit,Integer _noc,String search, boolean canRead){
		Map<String,Object>	modelMap = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> rootList = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		List<Object[]> datas = new ArrayList<Object[]>();
		Object total = 0;
		Employee employee = SystemSession.getUser();
		boolean superPower = false;
		
		if(id==null||"".equals(id)){
			id = 0;
		}
		if(kind==null){
			kind = -1;
		}
		
		_noc = _noc ==null?0:_noc;
		String condition = formCondition.replace("pd_prjid", "prj_id");
		Object assigntocode = baseDao.getFieldDataByCondition("Project", "prj_assigntocode", condition);
		
		superPower = "admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()));
		if (!superPower) {
			String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
			
			if (ems!=null) {
				for (String em : ems) {
					if (em!=null&&em.equals(employee.getEm_code())) {
						superPower = true;
						break;
					}
				}
			}
		}
		
		if(kind==0){
			condition = "1=1";
			if(page!=null&&limit!=null){
				int endPage = page * limit;
				int startPage = endPage - limit + 1;
				condition += " and rn<=" + endPage + " and rn>=" + startPage;
			}
			if(id==0){
				datas = projectDocPowerDao.getFileList(superPower||canRead, formCondition, condition, search);
				total = projectDocPowerDao.getCountFile(superPower||canRead, formCondition, search);
			}else {
				String str = null;
				if (!canRead) {
					str = projectDocPowerDao.powerForScan(id, _noc);
				}
				
				if (str==null) {
					//分页
					datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from projectdoc left join documentlist on "
						+ "dl_prjdocid=pd_id where pd_kind=0 and pd_parentid="+id+" order by pd_detno) a)", new String[]{"pd_id","pd_kind","pd_prjid",
						"pd_parentid","pd_name","pd_remark","pd_virtualpath","pd_detno","pd_code","pd_filepath","dl_version","dl_createtime"}, condition);
					total = baseDao.getFieldDataByCondition("projectdoc", "count(1)", "pd_parentid=" + id + " and pd_kind=0");	
				}else if(str != null){
					modelMap.put("exceptionInfo",str);
					return modelMap;
				}
			}
		}else{
			datas = projectDocPowerDao.getFileList(superPower, formCondition, canRead);
		}
		
		if(kind==-1&&superPower){
			modelMap.put("addroot", true);
		}		
		for(Object[] obj:datas){
			map = new HashMap<String, Object>();
			map.put("id",obj[0]);
			map.put("pd_id",obj[0]);
			map.put("pd_kind",obj[1]);
			map.put("pd_prjid",obj[2]);
			map.put("pd_parentid", obj[3]);
			map.put("pd_name", obj[4]);
			map.put("pd_remark", obj[5]);
			map.put("pd_virtualpath", obj[6]);
			map.put("pd_detno", obj[7]);
			map.put("pd_code",obj[8]);
			
			if(kind==-1&&"0".equals(obj[3].toString())){ //取根目录，从根目录中取子目录
				
				if (superPower) {
					map.put("manage", 1);
				} else {
					map.put("manage", obj[9]);
				}
				List<Map<String,Object>> child = getChild(datas,obj,superPower);
				map.put("children",child);
				rootList.add(map);
			}else if(kind==0){
				map.put("pd_filepath", obj[9]);
				map.put("dl_version", obj[10]);
				map.put("dl_createtime", obj[11]);
				list.add(map);
			}
		}

		if(kind==-1){
			modelMap.put("datas", rootList);
		}else{
			modelMap.put("datas", list);
			modelMap.put("total",total);
		}
		
		return modelMap;
	}
	
	private List<Map<String,Object>> getChild(List<Object[]> datas,Object[] data,boolean superPower){
		List<Map<String,Object>> child = new ArrayList<Map<String,Object>>();
		Map<String,Object> childJson = null;
		for(Object[] tree:datas){
			if(data[0].equals(tree[3])){
				childJson = new HashMap<String,Object>();
				childJson.put("id",tree[0]);
				childJson.put("pd_id",tree[0]);
				childJson.put("pd_kind",tree[1]);
				childJson.put("pd_prjid",tree[2]);
				childJson.put("pd_parentid", tree[3]);
				childJson.put("pd_name", tree[4]);
				childJson.put("pd_remark", tree[5]);
				childJson.put("pd_virtualpath", tree[6]);
				childJson.put("pd_detno", tree[7]);
				childJson.put("pd_code",tree[8]);
				if (superPower) {
					childJson.put("manage", 1);
				}else {
					childJson.put("manage", tree[9]);
				}
				List<Map<String,Object>> childnext = getChild(datas,tree,superPower);
				childJson.put("children",childnext);
				child.add(childJson);
			}
		}
		return child;
	}
	
	
	@Override
	@Transactional
	public Map<String,Object> saveAndUpdateTree(String create,String update,Integer _noc) {
		
		List<Map<Object,Object>> maps = BaseUtil.parseGridStoreToMaps(create);
		List<Map<Object,Object>> updagteMaps = BaseUtil.parseGridStoreToMaps(update);
		
		List<String> ids = new ArrayList<String>();
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Object prjid = null; //项目id
		if(maps.size()>0){
			prjid = maps.get(0).get("pd_prjid");
			if ("0".equals(maps.get(0).get("pd_parentid").toString())) {
				projectDocPowerDao.powerForAddRoot(prjid,_noc);
			}else if ("0".equals(maps.get(0).get("pd_kind").toString())) {
				projectDocPowerDao.powerForManage(maps.get(0).get("pd_parentid"), _noc);
			}else {
				projectDocPowerDao.powerForManage(maps.get(0).get("pd_id"), _noc);
			}
			
		}else{
			prjid = updagteMaps.get(0).get("pd_prjid");
			projectDocPowerDao.powerForManage(updagteMaps.get(0).get("pd_id"), _noc);
			
		}
		
		for(Map<Object,Object> map:maps){
			
			boolean havedetno = baseDao.checkIf("projectdoc", "pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_detno=" + map.get("pd_detno"));
			//序号已存在就把后面的序号+1
			if(havedetno){
				baseDao.updateByCondition("projectdoc", "pd_detno=pd_detno+1","pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_detno>=" + map.get("pd_detno"));
			}
		}
		for(Map<Object,Object> map:updagteMaps){	
			
			Integer oldDetno = baseDao.getFieldValue("projectdoc", "pd_detno", "pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_id=" + map.get("pd_id"), Integer.class);
			if (oldDetno!=null&&oldDetno!=Integer.parseInt(map.get("pd_detno").toString())) {
				boolean havedetno = baseDao.checkIf("projectdoc", "pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_detno=" + map.get("pd_detno")+" AND pd_id<>" + map.get("pd_id"));
				if(havedetno){
					//序号增大就把之间的的序号-1，序号减小就把之间的的序号+1
					if (Integer.parseInt(map.get("pd_detno").toString())>oldDetno) {
						baseDao.updateByCondition("projectdoc", "pd_detno=pd_detno-1","pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_detno>" + oldDetno+" AND pd_detno<=" + map.get("pd_detno"));
					}else if(Integer.parseInt(map.get("pd_detno").toString())<oldDetno){
						baseDao.updateByCondition("projectdoc", "pd_detno=pd_detno+1","pd_prjid='"+map.get("pd_prjid")+"' AND pd_parentid=" + map.get("pd_parentid")+" AND pd_detno<" + oldDetno+" AND pd_detno>=" + map.get("pd_detno"));
					}				
				}
			}
			
		}
		
		Object prjcode = null;
		if(prjid!=null){
			prjcode = baseDao.getFieldDataByCondition("project", "prj_code", "prj_id=" + prjid);
		}
		
		for(Map<Object,Object> map:maps){
			if(map.get("pd_code")==null||"".equals(map.get("pd_code"))){
				String code = baseDao.sGetMaxNumber("PRJDOC_TEMP",2);
				map.remove("pd_code");
				map.put("pd_code", code);
			}		
			int id = baseDao.getSeqId("PROJECTDOC_SEQ");
			ids.add(String.valueOf(id));
			map.remove("pd_id");
			map.put("pd_id", id);
			String virtualpath = map.get("pd_virtualpath").toString() + "/" + map.get("pd_name");
			if("0".equals(map.get("pd_parentid"))){
				virtualpath = prjcode + "/" + virtualpath;
			}
			map.remove("pd_virtualpath");
			map.put("pd_virtualpath", virtualpath);
		}
		
		List<String> updatePath = new ArrayList<String>();
		for(Map<Object,Object> map:updagteMaps){
			String oriVirtualpath = map.get("pd_virtualpath").toString();
			String virtualpath = map.get("pd_virtualpath").toString().substring(0,oriVirtualpath.lastIndexOf("/")) +"/"+ map.get("pd_name");
			map.remove("pd_virtualpath");
			map.put("pd_virtualpath", virtualpath);
			//如果是目录，则更新下面所有子项的虚拟路径
			if("-1".equals(map.get("pd_kind").toString())){
				virtualpath += "/";
				String sql = "update projectdoc set pd_virtualpath='" + virtualpath + "'||pd_name where pd_id in (select pd_id from projectdoc start with pd_id=" + map.get("pd_id") + " connect by prior pd_id=pd_parentid) and pd_id<>" + map.get("pd_id");
				updatePath.add(sql);
			}
			
		}
		
		List<String> saveSql = SqlUtil.getInsertSqlbyGridStore(maps, "projectdoc");
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updagteMaps, "projectdoc", "pd_id");

		baseDao.execute(saveSql);
		baseDao.execute(updateSql);
		baseDao.execute(updatePath);

		//判断名称是否重复
		Object parentid = null;
		if(maps.size()>0){
			parentid = maps.get(0).get("pd_parentid");
		}else if(updagteMaps.size()>0){
			parentid = updagteMaps.get(0).get("pd_parentid");
		}
		if(prjid!=null&&parentid!=null){
			//判断名称是否重复
			String detno = baseDao.getJdbcTemplate().
					queryForObject("select wm_concat(pd_detno) from projectdoc where pd_name in (select pd_name from projectdoc where pd_prjid=? and pd_parentid=? group by pd_name having count(1)>1) and pd_prjid=? and pd_parentid=? order by pd_detno asc", 
							String.class, new Object[]{prjid,parentid,prjid,parentid});
			if(detno != null){
				BaseUtil.showError("文件名称重复，序号：" + detno);
			}
		}		
		
		modelMap.put("ids", ids);
		return modelMap;
		
	}
	
	@Override
	public void deleteNode(String id,String type,Integer _noc){
		String ID = id.split(",")[0];
		projectDocPowerDao.powerForManage(ID, _noc);
		if(id!=null&&"file".equals(type)){	
			boolean bool = baseDao.checkIf("projecttaskattach", "ptt_prjdocid in (" + id + ")");
			if(bool){
				BaseUtil.showError("该文件已关联任务，不允许删除");
			}
			
			baseDao.execute("delete from projectdoc where pd_id in (" + id + ")");
		}else if(id!=null&"index".equals(type)){
			boolean bool = baseDao.checkIf("projectdoc", "pd_id in(select pd_id from projectdoc start with pd_id="+id+" connect by prior pd_id=pd_parentid) and pd_id<>" + id);
			if(bool){
				BaseUtil.showError("该文件夹下面存在文件，不允许删除");
			}
			baseDao.execute("delete from projectdoc where pd_id in(select pd_id from projectdoc start with pd_id="+id+" connect by prior pd_id=pd_parentid)");
		}
	}
	
	@Override
	public Map<String,Object> getProjectMsg(String formCondition){
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> list = baseDao.queryForList("select * from project where " + formCondition);
		map.put("data", list);
		return map;
	}
	
	@Override
	public boolean ifMainTaskOpen(String condition){
		return baseDao.checkIf("projectmaintask", condition + " and pt_statuscode='DOING' or pt_statuscode='ENDED'");
	}

	@Override
	public List<Map<String, Object>> getFilePowers(Integer docid, Integer prjid,Integer _noc) {
		_noc =_noc==null?0:_noc;
		projectDocPowerDao.powerForManage(docid, _noc);
		return projectDocPowerDao.getFilePowers(docid, prjid);
	}

	@Override
	public void saveFilePowers(Boolean appyforChilds,String filePowers,Integer _noc) {
		_noc =_noc==null?0:_noc;
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(filePowers);
		projectDocPowerDao.powerForManage(store.get(0).get("pp_docid"), _noc);
		projectDocPowerDao.saveFilePowers(appyforChilds, store);
	}

	@Override
	public String upload(Employee employee, Integer fieldId, String condition, Integer _noc, FileUpload uploadItem) {
		try {
			if (fieldId<=0) {
				Object pd_id = baseDao.getFieldDataByCondition("ProjectDoc", "pd_id", condition);
				if (pd_id!=null) {
					fieldId = Integer.parseInt(pd_id.toString());
				}else {
					return "{error: '文件不存在，不能上传附件！'}";
				}
			}
			
			String str = projectDocPowerDao.powerForUpload(fieldId,_noc);
			if (str!=null) {
				return "{error: '"+str+"'}";
			}
			
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			
			String em_code = employee.getEm_code();
			String path = FileUtil.saveFile(uploadItem.getFile(), em_code);
			int id = filePathService.saveFilePath(path, (int) size, filename, employee);
			String newPath = filename+";"+id;
			updateVersion(employee,fieldId,newPath);
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}
	
	private void updateVersion(Employee employee, Integer pd_id,String newPath){
		List<String> updateDoc = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		//如果与原来的文档路径不相等，则说明有更新
		//更新文档检查表的checked状态
		if(newPath!=null&&!"".equals(newPath)){
			Object[] filePath = baseDao.getFieldsDataByCondition("projectdoc", "pd_filepath,pd_prjid", "pd_id=" + pd_id);
			if(!newPath.equals(filePath[0])){ //如果与原来的文档路径不相等，则说明有更新
				String checkUp = "update projectdoc set pd_checked=1,pd_operator='"+employee.getEm_name()+"',pd_operatime=sysdate,pd_filepath='"+newPath+"' where pd_id=" +pd_id;
				updateDoc.add(checkUp);
				//更新到任务日报的附件里
				String workUp = "update projecttaskattach set ptt_filepath='"+newPath+"' where ptt_prjdocid=" + pd_id;
				updateDoc.add(workUp);
				//更新到文档管理里
				Object pdid = pd_id;
				String fp = newPath.toString();
				String fpid = fp.substring(fp.indexOf(";")+1);
				String attach = "";
				String attachname="";
				int dlid = 0;
				Object path = null;
				Object size = null;
				Object style = null;
				JSONObject obj=null;
				JSONArray arr=formAttachService.getFiles(fpid);
				for (int i=0;i<arr.size();i++){	
					obj=arr.getJSONObject(i);
					path = obj.get("fp_path");
					size = obj.get("fp_size");
					attachname = String.valueOf(obj.get("fp_name"));
					style = attachname.substring(attachname.lastIndexOf(".")+1);		
					attach = "'" + attachname + "','" + path + "','" + size + "','" + String.valueOf(obj.get("fp_id"))+";','" + style + "'";
				}
				if(filePath!=null&&filePath[0]!=null&&!"".equals(filePath[0])){ //非第一次上传
					Object doclid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=" + pd_id);
					if(doclid!=null){
						dlid = Integer.parseInt(doclid.toString());
						String docUpdate = "update documentlist set dl_detno=dl_detno+1,dl_version=dl_version+1,dl_createtime=sysdate,dl_creator='" + employee.getEm_name() + "',dl_name='" + attachname + "',dl_filepath='" + path + "',dl_size=" + size + ",dl_fpid='" + fpid + "',dl_style='" + style + "' where dl_prjdocid=" + pdid;							
						updateDoc.add(docUpdate);								
					}						
				}else{    //第一次上传
					sb.append("," +pd_id);
					dlid = baseDao.getSeqId("DOCUMENTLIST_SEQ");
					Object parentid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=(select pd_parentid from projectdoc where pd_id="+pdid+")");
					String insertdoc = "insert into documentlist(dl_id,dl_virtualpath,dl_createtime,dl_creator,dl_parentid,dl_kind,dl_status,dl_statuscode,dl_name,dl_filepath,dl_size,dl_fpid,dl_style,dl_prjdocid,dl_prjid) select "+dlid+",'/项目文档'||pd_virtualpath,sysdate,'" 
							+ employee.getEm_name() + "'," + parentid + ",0,'已审核','AUDITED'," + attach + ","+pdid+","+filePath[1]+" from projectdoc where pd_id=" + pdid;
					updateDoc.add(insertdoc);												
				}
				//更新版本号
				Object detno=baseDao.getFieldDataByCondition("DOCUMENTVERSION", "max(dv_detno)+1", "dv_dlid="+dlid);
				detno=detno==null?1:detno;
				int dvid=baseDao.getSeqId("DOCUMENTVERSION_SEQ");
				String versionInsert = "INSERT INTO documentversion(dv_id,dv_dlid,dv_name,dv_filepath,dv_man,dv_explain,dv_detno,dv_size,dv_fpid) VALUES(" + dvid + "," + dlid + ",'"+attachname+"','"+path+"','" + employee.getEm_name() + "','" + SystemSession.getLang() + "'," + detno + "," + size + ",'"+fpid+"')";
				updateDoc.add(versionInsert);	
			}
			baseDao.execute(updateDoc);
		}
		//更新到projecttask附件上传状态
		if(sb.length()>0){
			List<Object[]> taskids = baseDao.getFieldsDatasByCondition("projectdoc left join projecttask on pd_taskid=id", new String[]{"pd_taskid","prjdocid"}, "pd_id in (" + sb.substring(1) + ") order by pd_taskid desc");
			String taid = "";
			List<String> statusUp = new ArrayList<String>();
			if(taskids.size()>0){
				for(Object[] taskid:taskids){
					if(taskid[1]!=null&&taskid[0]!=null){
						if(!taid.equals(taskid[0])){
							taid = taskid[0].toString();		//这一个不等于上一个,防止重复操作同一个taskid	
							String[] docidstr = taskid[1].toString().split(",");
							StringBuffer docSb = new StringBuffer();
							for(int i=0;i<docidstr.length;i++){
								docSb.append("," + docidstr[i] + "," + (i+1));
							}
							Object docstatus = baseDao.getFieldDataByCondition("projectdoc", "wm_concat(nvl(pd_checked,0))", "pd_taskid="+taskid[0]+" and pd_id in ("+taskid[1]+") order by decode(pd_id"+docSb.toString()+")");
							statusUp.add("update projecttask set prjdocstatus='"+docstatus+"' where id=" + taskid[0]);						
						}				
					}

				}
			}
			baseDao.execute(statusUp);			
		}
	}

	private String getTreeListPath(String prid) {
		String path = "";
		Object parentId = baseDao.getFieldDataByCondition("projectdoc", "pd_parentid", "pd_id = "+prid
				+" and rownum=1 order by pd_detno");
		Object parentName = baseDao.getFieldDataByCondition("projectdoc", "pd_name", "pd_id = "+parentId
		+" and rownum=1 order by pd_detno");
		if(!"0".equals(parentId.toString())) {
			path +=getTreeListPath(parentId.toString());
			return path+"/"+parentName.toString();
		}else {
			return path;
		}
	}
	@Override
	public HSSFWorkbook downloadAsExcel(String formCondition,String prids) {
		Object name = baseDao.getFieldDataByCondition("project", "prj_name", formCondition);
		String id = formCondition.split("=")[1];
		String title="项目名称:" + name.toString();
		List<Object[]> list = baseDao.getFieldsDatasByCondition(" projectdoc left join documentlist on dl_prjdocid=pd_id", 
			new String[]{"pd_virtualpath","pd_name","pd_filepath","dl_version","dl_createtime","pd_id"},
				"pd_prjid='"+id+"' order by pd_code,pd_parentid,pd_detno");
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.createFreezePane(0, 1);// 固定标题
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < 6; i++) {
			if(i==0 || i==1 || i==4) {
				sheet.setColumnWidth(i, 8000);
			}else {
				sheet.setColumnWidth(i, 3000);
			}
		}
		HSSFCellStyle titleStyle = getCellStyle(workbook, "title");
		row.setHeight((short) 400);
		// 指定合并区域
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (short) 5));
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(title));
		cell.setCellStyle(titleStyle);

		HSSFCellStyle labelStyle = getCellStyle(workbook, "label");
		HSSFRow row1 = sheet.createRow(1);
		row1.setHeight((short) 400);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("文件目录") );
		cell1.setCellStyle(labelStyle);
		HSSFCell cell2 = row1.createCell(1);
		cell2.setCellValue(new HSSFRichTextString("文件名称") );
		cell2.setCellStyle(labelStyle);
		HSSFCell cell3 = row1.createCell(2);
		cell3.setCellValue(new HSSFRichTextString("是否上传附件") );
		cell3.setCellStyle(labelStyle);
		HSSFCell cell4 = row1.createCell(3);
		cell4.setCellValue(new HSSFRichTextString("版本号") );
		cell4.setCellStyle(labelStyle);
		HSSFCell cell5 = row1.createCell(4);
		cell5.setCellValue(new HSSFRichTextString("最新上传时间") );
		cell5.setCellStyle(labelStyle);
		HSSFCell cell6 = row1.createCell(5);
		cell6.setCellValue(new HSSFRichTextString("负责人") );
		cell6.setCellStyle(labelStyle);
		
		HSSFCellStyle columnStyle = getCellStyle(workbook, "column");
		int m=2;
		for(int i=0;i< list.size();i++) {
			Object[] data = list.get(i);
			Object parentid = baseDao.getFieldDataByCondition("projectdoc", "pd_parentid", "pd_id = "+data[5].toString());
			Object kind = baseDao.getFieldDataByCondition("projectdoc", "pd_kind", "pd_id = "+data[5].toString());
			if(!"0".equals(parentid.toString()) && "0".equals(kind.toString())){ //判断是否为叶子节点
				HSSFRow rowm = sheet.createRow(m);
				String path = getTreeListPath(data[5].toString());
				for(int j=0;j<data.length-1;j++) {
					if(j==0) {
						data[j] = path;
					}
					if(j==2) {
						data[j] = data[j]==null?"否":"是";
					}
					HSSFCell cellk = rowm.createCell(j);
					cellk.setCellValue(new HSSFRichTextString(data[j]==null?"":data[j].toString()));
					cellk.setCellStyle(columnStyle);
				}
				List<Object[]> doc = baseDao.getFieldsDatasByCondition("projecttask left join projecttaskattach on ptt_taskid = id", new String[] {"resourcename","prjdocid"}, 
						"prjplanid="+id + " and ptt_prjdocid="+data[5]);
				String resourcename = "";
				for (Object[] objects : doc) {
					resourcename=objects[0]==null?"":objects[0].toString();
				}
				HSSFCell celldoc = rowm.createCell(5);
				celldoc.setCellValue(new HSSFRichTextString(resourcename));
				celldoc.setCellStyle(columnStyle);
				m++;
			}
		}
		
		return workbook;
	}
	private HSSFCellStyle getCellStyle(HSSFWorkbook workbook, String type) {
		HSSFFont font = workbook.createFont();
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (type.equals("title")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 300);
			cellStyle.setFont(font);
		} else if (type.equals("group")) {
			HSSFPalette customPalette = workbook.getCustomPalette();
			customPalette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte) 153, (byte) 153, (byte) 204);
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			font.setFontName("宋体");
			font.setFontHeight((short) 250);
			cellStyle.setFont(font);
		} else if (type.equals("column")) {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		} else {
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.WHITE.index);
			cellStyle.setBottomBorderColor(HSSFColor.WHITE.index);
			cellStyle.setLeftBorderColor(HSSFColor.WHITE.index);
			cellStyle.setRightBorderColor(HSSFColor.WHITE.index);
			font.setFontName("宋体");
			font.setFontHeight((short) 200);
			cellStyle.setFont(font);
		}
		return cellStyle;
	}
}
