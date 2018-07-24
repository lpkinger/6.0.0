package com.uas.erp.service.oa.impl;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DocumentListDao;
import com.uas.erp.model.DocumentList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.oa.DocumentListService;
import com.uas.erp.service.oa.DocumentPowerService;
@Service
public class DocumentListServiceImpl implements DocumentListService {
	@Autowired
	private  DocumentListDao documentListDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private FormAttachService  formAttachService;
	@Autowired
	private DocumentPowerService documentPowerService;
	private static String CODEKIND="CODE";
	private static String LOCKKIND="LOCK";
	static final String INSERTVERSION = "INSERT INTO documentversion(dv_id,dv_dlid,dv_name,dv_filepath,dv_man,dv_explain,dv_detno,dv_size) VALUES (?,?,?,?,?,?,?,?)";
	@Override
	public List<JSONTree> getDirectoryByCondition(int parentId,String condition, String  caller) {
		return get(parentId,condition);
	}
	public List<JSONTree> get(int parentId,String condition){
		//所有文件夹
		/*List<DocumentList> directorys =documentListDao.getDocumentListByCondition(condition);
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		for (DocumentList dir : directorys) {
			JSONTree ct = new JSONTree(dir, language);
			List<JSONTree> children = new ArrayList<JSONTree>();
		    ct.setCls("org");
			treeList.add(ct);
			ct.setChildren(children);
		}
		List<JSONTree> root = new ArrayList<JSONTree>();
		for (JSONTree ct : treeList) {
			if (Integer.parseInt(ct.getParentId().toString())==0) {
				root.add(ct);
			}
		}
		getTree(treeList, root);
		return root;*/
		return new ArrayList<JSONTree>();
	}

	public void getTree(List<JSONTree> list, List<JSONTree> root){
		for (JSONTree ct : root) {
			List<JSONTree> cts = findById(list, ct.getId());
			if (cts.size() != 0) {
				ct.getChildren().addAll(findById(list, ct.getId()));				
				getTree(list, ct.getChildren());
			}
		}
	}
	public List<JSONTree> findById(List<JSONTree> list ,Object id){
		List<JSONTree> cts = new ArrayList<JSONTree>();
		for (JSONTree ct : list) {
			if (ct.getParentId().toString().equals(id.toString())) {
				cts.add(ct);
			}
		}
		return cts;
	}
	@Override
	public boolean save(String caller,String formStore) {
		Map<Object,Object> data=BaseUtil.parseFormStoreToMap(formStore);
		String prefixcode = String.valueOf(data.get("dl_prefixcode"));
		String folderName = String.valueOf(data.get("dl_name"));
		String needflow="";
		int  parentId=Integer.parseInt(data.get("dl_parentid").toString());
		List<Object[]> prefixCodeList = baseDao.getFieldsDatasByCondition("DocumentList", new String[]{"dl_prefixcode","dl_name"}, "dl_statuscode='AUDITED' and dl_kind = -1 and dl_parentid = " + parentId);
		for(Object[] obj: prefixCodeList){
			if(obj[0] != null){
				if(obj[0].toString().equals(prefixcode))
					BaseUtil.showError("该前缀码已存在,请重新输入!");
			}
			if(obj[1] != null){
				if(obj[1].toString().equals(folderName)){
					BaseUtil.showError("该文件夹已存在,请重新输入");
				}
			}
		}
		needflow=String.valueOf(baseDao.getFieldDataByCondition("DocumentList", "dl_needflowchildren", "dl_id="+parentId));
		List<String> sqls=new ArrayList<String>();
		if("-1".equals(needflow)){
			data.put("dl_status", "在录入");
			data.put("dl_statuscode", "ENTERING");
		}else{
			data.put("dl_status", "已审核");
			data.put("dl_statuscode", "AUDITED");
		}
		//类型区分
		Object kind=String.valueOf(data.get("dl_kind"));
		boolean hasPower = documentPowerService.CheckPowerByFolderId(Integer.parseInt(String.valueOf(data.get("dl_parentid"))), "dp_control");
		if(!hasPower){
			documentPowerService.checkSavePower(Integer.parseInt(String.valueOf(data.get("dl_parentid"))));
		}
		String Virtualpath="";
		Virtualpath=getVirtualpath(data.get("dl_parentid"),data.get("dl_name"));
		data.put("dl_virtualpath", Virtualpath);
		if("-1".equals(kind)){	    	
			sqls.add(SqlUtil.getInsertSqlByMap(data, "DOCUMENTLIST"));
			/**默认继承父级目录的权限*/
			extendParentPower(data.get("dl_parentid"),data.get("dl_id"));
		}else {
			if("-1".equals(needflow)){
				String docname= getDocName(String.valueOf(data.get("dl_fpid")));
				data.put("dl_name", getDocName(String.valueOf(data.get("dl_fpid"))));
				data.put("dl_style",docname.substring(docname.lastIndexOf(".")+1));
				sqls.add(SqlUtil.getInsertSqlByMap(data, "DOCUMENTLIST"));
			}else{
				//无需审核
				SplitAttachsByIds(data,String.valueOf(data.get("dl_fpid")));
			} 

		}
		baseDao.execute(sqls);
		if(!"-1".equals(kind)){
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "上传文档", 
					"上传成功", "dl_id="+data.get("dl_id")));
		}else{
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "创建目录", 
					"创建成功", "dl_id="+data.get("dl_id")));
		}
		return "-1".equals(needflow);

	}
	@Override
	public void update(String formStore, String  caller) {
		Map<Object,Object> data=BaseUtil.parseFormStoreToMap(formStore);
		if(data.get("dl_kind")!=null && Integer.parseInt(String.valueOf(data.get("dl_kind")))==-1){
			documentPowerService.checkControlPower(Integer.parseInt(String.valueOf(data.get("dl_id"))));
		}
		boolean hasPower = documentPowerService.CheckPowerByFolderId(Integer.parseInt(String.valueOf(data.get("dl_parentid"))), "dp_control");
		if(!hasPower){
			documentPowerService.checkSavePower(Integer.parseInt(String.valueOf(data.get("dl_parentid"))));
		}
		data.remove("dl_parentid");	//移除父级ID，导致更新时发生父级ID变化
		String updateSql=SqlUtil.getUpdateSqlByFormStore(data, "DocumentList","dl_id");
		baseDao.execute(updateSql);
		if(data.get("dl_kind") != null && !"-1".equals(data.get("dl_kind"))){
			/*baseDao.execute("insert into documentlog(dl_id,dl_handler,dl_date,dl_content,dl_result,dl_docid) values(documentlog_seq.nextval,?,sysdate,?,?,?)",
					SystemSession.getUser().getEm_name(),"更新文档信息","更新成功",data.get("dl_id"));*/
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新文档信息", 
					"更新成功", "dl_id="+data.get("dl_id")));
		}
	}
	@Override
	public List<JSONTree> loadDir(int parentId,String condition, String  caller) {
		return documentListDao.getDocumentListByCondition(parentId,condition,SystemSession.getUser(), SystemSession.getLang());
	}
	@Override
	public List<DocumentList> getDocumentsByParentId(int parentId,String condition, String  caller) {
		return  documentListDao.getDocumentsByCondition(parentId, condition, SystemSession.getUser(), SystemSession.getLang());

	}
	private String getVirtualpath(Object parentid,Object name){
		Object parentvitualpath="";
		int id=Integer.parseInt(parentid.toString());
		switch (id) {
		case 0:
			parentvitualpath="/公共文档";
			break;
		case 1:
			parentvitualpath="/我的文档";
			break;	
		default:
			parentvitualpath=baseDao.getFieldDataByCondition("documentlist", "dl_virtualpath", "dl_id="+parentid);
			break;
		}	
		if(parentvitualpath==null) BaseUtil.showError("父目录已被删除不能在该路径下创建!");
		else parentvitualpath=name==null?parentvitualpath:parentvitualpath+"/"+name;
		return String.valueOf(parentvitualpath);
	}
	private String getDocName(String FPid){
		String Docname="";
		if(FPid!=null){
			FPid=FPid.replaceAll(";", ",").substring(0,FPid.length()-1);
		}
		SqlRowList sl=baseDao.queryForRowSet("select wmsys.wm_concat(fp_name) from filepath  where fp_id in ("+FPid+")");
		if(sl.next()) Docname=sl.getString(1);
		return Docname;
	}
	private void SplitAttachsByIds(Map<Object,Object> data,String FPid){
		JSONObject obj=null;
		String attachname="";
		int dlid=0;
		if(FPid!=null){
			JSONArray arr=formAttachService.getFiles(FPid);
			for (int i=0;i<arr.size();i++){		
				dlid=baseDao.getSeqId("DOCUMENTLIST_SEQ");
				obj=arr.getJSONObject(i);
				attachname=String.valueOf(obj.get("fp_name"));
				data.put("dl_name", attachname);
				String prefixCode = (String) data.get("dl_code");
				if(prefixCode != null && !"".equals(prefixCode) && !"null".equals(prefixCode)){
					data.put("dl_code", prefixCode+"-"+String.valueOf(obj.get("fp_id")));
				}else{
					int parentId = Integer.parseInt(String.valueOf(data.get("dl_parentid")));
					List<String> list = new ArrayList<String>();
					while(!(parentId == 0 || parentId == 1)){
						Object[] fields = baseDao.getFieldsDataByCondition("documentlist", "dl_parentid,dl_prefixcode", "dl_id = " + parentId);
						parentId = Integer.parseInt(String.valueOf(fields[0]));
						String pre = String.valueOf(fields[1]);
						if(!"".equals(pre) && !"null".equals(pre)){
							list.add(pre);
						}
					}
					StringBuilder sb = new StringBuilder();
					for(int j = list.size()-1; j >= 0; j--){
						sb.append(list.get(j) + "-");
					}
					if(sb != null && sb.toString().length() != 0){
						String prefix = sb.toString().substring(0,sb.toString().length()-1);
						data.put("dl_code",prefix+"-"+String.valueOf(obj.get("fp_id")));
					}else{
						data.put("dl_code",String.valueOf(obj.get("fp_id")));
					}
				}
				data.put("dl_filepath",obj.get("fp_path"));
				data.put("dl_size",obj.get("fp_size"));
				data.put("dl_fpid",String.valueOf(obj.get("fp_id"))+";");
				data.put("dl_id",dlid);
				data.put("dl_style",attachname.substring(attachname.lastIndexOf(".")+1));
				baseDao.execute(SqlUtil.getInsertSqlByMap(data, "DOCUMENTLIST"));
				Version(dlid,String.valueOf(obj.get("fp_path")),attachname,obj.get("fp_size"));
			}
		}
	}
	@Override
	public void DocUpdateByType(String caller,String formData, String type) {
		String logMessage="";
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formData);
		int keyValue=Integer.parseInt(String.valueOf(map.get("dl_id")));
		int parentId=Integer.parseInt(String.valueOf(map.get("dl_parentid")));
		documentPowerService.checkControlPower(parentId);
		if(CODEKIND.equals(type)){
			logMessage="修改文件编号为:"+map.get("dl_code");
		}else if(LOCKKIND.equals(type)){
			String status="-1".equals(String.valueOf(map.get("dl_locked")))?"已锁定":"未锁定";
			logMessage="修改文件锁定状态为:"+status;
		}else if("DocRename".equals(caller)){
			logMessage = "重命名文件为:" + map.get("dl_name");
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "DOCUMENTLIST", "dl_id"));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "修改操作", 
				logMessage, "dl_id="+keyValue));
	}
	@Override
	public void saveChange(String formStore, String caller) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		//文档信息变更
		int  parentId=Integer.parseInt(map.get("dlc_parentid").toString());
		String needflow=String.valueOf(baseDao.getFieldDataByCondition("DocumentList", "dl_needflowchildren", "dl_id="+parentId));
		if("-1".equals(needflow)){
			map.put("dlc_status", "在录入");
			map.put("dlc_statuscode", "ENTERING");
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "DOCUMENTLISTCHANGE"));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"变更操作","变更文档保存","dl_id="+map.get("dlc_id")));
			//baseDao.logger.getMessageLog("变更操作", "变更文档保存", caller, "dlc_id", map.get("dlc_id"))	;
		}else{
			map.put("dlc_status", "已审核");
			map.put("dlc_statuscode", "AUDITED");
			/**
			 * 执行变更
			 * */
			ChangeDoc(map.get("dlc_olddlid"),String.valueOf(map.get("dlc_newattach")),"更新文档","CHANGE",String.valueOf(map.get("dlc_newversion")));
		}
	}
	private void ChangeDoc(Object olddlid,String newattach,String message,String type,String version){
		Map<Object,Object> map=new HashMap<Object, Object>();
		if(!"BACK".equals(type)){
			JSONArray arr=formAttachService.getFiles(newattach);
			JSONObject obj=arr.getJSONObject(0);
			String attachname=String.valueOf(obj.get("fp_name"));
			map.put("dl_id", olddlid);
			map.put("dl_name", obj.get("fp_name"));
			map.put("dl_filepath",obj.get("fp_path"));
			map.put("dl_size",obj.get("fp_size"));
			map.put("dl_fpid",String.valueOf(obj.get("fp_id"))+";");
			map.put("dl_style",attachname.substring(attachname.lastIndexOf(".")+1));
			map.put("dl_createtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			if(version != null && !"".equals(version.trim())){
				map.put("dl_version", version);
			}
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "DOCUMENTLIST", "dl_id"));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"变更操作","变更文档保存","dl_id="+olddlid));
			Version(Integer.parseInt(olddlid.toString()),String.valueOf(obj.get("fp_path")),String.valueOf(obj.get("fp_name")),obj.get("fp_size"));
		}else {
			
		}

	}
	private void Version(int dlid,String path,String name,Object size){
		Object detno=baseDao.getFieldDataByCondition("DOCUMENTVERSION", "max(dv_detno)+1", "dv_dlid="+dlid);
		detno=detno==null?1:detno;
		int dvid=baseDao.getSeqId("DOCUMENTVERSION_SEQ");
		baseDao.execute(INSERTVERSION, new Object[]{
				dvid,dlid,name,path,SystemSession.getUser().getEm_name(),SystemSession.getLang(),detno,size
		});
	}
	@Override
	public void delete(int id, String caller) {
		boolean hasPower = documentPowerService.CheckPowerByFolderId(id, "dp_control");
		if(!hasPower){
			documentPowerService.checkDeletePower(id);
		}
		baseDao.updateByCondition("DOCUMENTLIST", "dl_statuscode='DELETED',dl_status='已删除'", "dl_id='"+id+"'");
//		String message="DocDeleteDir".equals(caller)?"删除目录":"删除文件";
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"删除操作","删除成功","dl_id="+id));
		//baseDao.logger.delete(caller, "dl_id", id);
	}
	@Override
	public void review(String formStore,String caller) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		map.put("dr_id", baseDao.getSeqId("DOCUMENTREVIEW_SEQ"));
		map.put("dr_man",SystemSession.getUser().getEm_name()+"("+SystemSession.getUser().getEm_code()+")");
		baseDao.execute(SqlUtil.getInsertSqlByMap(map, "DOCUMENTREVIEW"));
	}
	@Override
	public void deleteDoc(String data, String  caller) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		int folderId=Integer.parseInt(String.valueOf(maps.get(0).get("dl_parentid")));
		boolean hasPower = documentPowerService.CheckPowerByFolderId(folderId, "dp_control");
		if(!hasPower){
			documentPowerService.checkDeletePower(folderId);
		}
     	String ids="";
		for(Map<Object,Object> map:maps){
			ids+=map.get("dl_id")+",";
		}
		ids="("+ids.substring(0,ids.lastIndexOf(","))+")";
		List<Object> list = baseDao.getFieldDatasByCondition("documentlist", "dl_statuscode", "dl_id in" + ids);
		if("DELETED".equalsIgnoreCase(list.get(0).toString())){
			baseDao.execute("delete from documentlist where dl_id in " + ids);
		}
		baseDao.execute("update  documentlist  set dl_statuscode='DELETED',dl_status='已删除'  where dl_locked=0  and dl_id in "+ids);
		
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void moveDoc(String data, int folderId) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		int formfolderId=Integer.parseInt(String.valueOf(maps.get(0).get("dl_parentid")));
	    documentPowerService.checkControlPower(formfolderId);
	    documentPowerService.checkControlPower(folderId);
	    String ids=CollectionUtil.pluckSqlString(maps, "dl_id");
	    baseDao.updateByCondition("DOCUMENTLIST", "dl_parentid="+folderId+",DL_STATUSCODE='AUDITED',DL_STATUS='已审核'", "dl_id  in ("+ids+")"); 
	    Object newFolderName = baseDao.getFieldDataByCondition("documentlist", "dl_name", "dl_parentid="+folderId);
	    //记录文档移动日志
	    String[] idArray = ids.split(",");
	    for(String id : idArray){
	    	Object oldFolderName = baseDao.getFieldDataByCondition("documentlist", "dl_name", "dl_id="+id);
	    	baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"移动操作","移动文档,从"+oldFolderName+"移动至:"+newFolderName,"dl_id="+id.replace("'", "")));
	    }
	}
	@Override
	public void relateDoc(String data,String relateCode) {
		Integer relateId=baseDao.getFieldValue("DocumentList","dl_id", "dl_code='"+relateCode+"'",Integer.class);
		if(relateId==null) BaseUtil.showError("选择的关联文档不存在!");
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		documentPowerService.checkControlPower(Integer.parseInt(maps.get(0).get("dl_parentid").toString()));
		String insertSql="insert into documentlistrelate(dlr_id,dlr_dlid,dlr_relateid,dlr_man) values(?,?,?)";
        for(Map<Object,Object> map:maps){
        	baseDao.execute(insertSql, new Object[]{baseDao.getSeqId("DOCUMENTLISTRELATE_SEQ"),map.get("dl_id"),relateId,SystemSession.getUser().getEm_name()});
        }
	}
	
	@Override
    public  void extendParentPower(Object parentId,Object dirId){
    	baseDao.execute("begin  for rs in (select * from Documentorgpower where Dp_Dclid="+parentId+" ) "
        +"loop rs.Dp_Dclid:="+dirId+"; rs.dp_id :=Documentorgpower_SEQ.nextval; insert into Documentorgpower values rs; end loop;"
        +"for rs in (select * from Documentjobpower where Dp_Dclid="+parentId+") "
        +"loop rs.Dp_Dclid:="+dirId+"; rs.dp_id :=Documentjobpower_SEQ.nextval; insert into Documentjobpower values rs; end loop;"
        +"for rs in (select * from Documentpersonpower where Dp_Dclid="+parentId+" ) "
        +"loop rs.Dp_Dclid:="+dirId+"; rs.dp_id :=Documentpersonpower_SEQ.nextval;insert into Documentpersonpower values rs; end loop;"
        +" end;");
      }
	@Override
	public void downloadbyIds(HttpServletResponse response,String ids,String zipFileName) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String[] idArr = ids.split(";");
		//把重复的id去掉
		Set<String> idSet = new HashSet<String>(Arrays.asList(idArr));
		JSONObject obj;
		String path;
		long size = 0;
		List<File> listFiles = new ArrayList<File>();
		String zipName = "";
		String fileName = "";
		Map<String,Integer> fileCount = new HashMap<String,Integer>();
		for(String id:idSet){
			JSONArray json = formAttachService.getFiles(id);
			if(json.size()>0){
				obj = formAttachService.getFiles(id).getJSONObject(0);
			}else{
				continue;
			}
			path = obj.getString("fp_path");
			InputStream in = null;
			fileName = obj.getString("fp_name");
			//判断是否存在重复的文件名
			if(fileCount.containsKey(fileName)){
				int count = fileCount.get(fileName);
				fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "(" + count + ")" + fileName.substring(fileName.lastIndexOf("."));
				fileCount.put(fileName, count+1);
			}else { 
				fileCount.put(fileName, 1);
			}
			if("".equals(zipName)){
				zipName = fileName;
			}
			try{
				if (path.startsWith("B2B://")) {// 文件在云平台
					path = SystemSession.getUser().getCurrentMaster().getMa_b2bwebsite() + "/" + path.substring(6);
					size += obj.getInt("fp_size");
					in = HttpUtil.download(path);
					File file = stream2file(in,fileName);
					listFiles.add(file);
				} else if (path.startsWith("http:")) {// 文件存放在文件系统，这里存放的是http接口地址
					in = HttpUtil.download(path);
					size += in.available();
					File file = stream2file(in,fileName);
					listFiles.add(file);
				} else {
					File file = new File(path);					
					in = new FileInputStream(file);
					File oriFile = stream2file(in,fileName);
					listFiles.add(oriFile);
					size += file.length();						
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			//记录下载日志
			List<Object> idList = baseDao.getFieldDatasByCondition("documentlist", "dl_id", "dl_filepath='"+path+"'");
			for(Object docid : idList){
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"下载文档","下载成功","dl_id="+docid));
			}
		}
		
		if(listFiles.size()<=0){
			BaseUtil.showError("找不到任何文件!");
		}
		if(zipFileName!=null){
			zipName = zipFileName;
		}
		response.setHeader("Content-Disposition",
			"attachment; filename="+new String((zipName + ".zip").getBytes("utf-8"), "iso-8859-1"));
		response.addHeader("Content-Length", String.valueOf(size));
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octec-stream");
		
		ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));
		
		for (File file : listFiles) {
			zos.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException fnfe) {
				zos.write(("Can not find file " + file.getName())
						.getBytes());
				zos.closeEntry();
				continue;
			}
			
			BufferedInputStream fif = new BufferedInputStream(fis);
			int data = 0;
			while ((data = fif.read()) != -1) {
				zos.write(data);
			}
			fif.close();
			zos.closeEntry();
		}
		zos.close();
	} 
	private File stream2file (InputStream in,String fileName) throws IOException {
        String tDir = System.getProperty("java.io.tmpdir");
        File file = new File(tDir,fileName);
        file.deleteOnExit();
        FileOutputStream out = new FileOutputStream(file);
        try{
            IOUtils.copy(in, out);    	
        }catch(IOException e){
        	e.printStackTrace();
        }
        in.close();
        out.close();
        return file;
    }
	@Override
	public String download(HttpServletResponse response, String path,String escape, String fileName) throws IOException {
		if (!"1".equals(escape)) {
			path = new String(path.getBytes("iso-8859-1"), "utf-8");
		}
		try {
			fileName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
			File file = new File(path);
			InputStream in = new FileInputStream(file);
			OutputStream os = response.getOutputStream();
			fileName = fileName.replace(",", " ");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Content-Length", file.length() + "");
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/octec-stream");
			int data = 0;
			while ((data = in.read()) != -1) {
				os.write(data);
			}
			in.close();
			os.close();
			//记录下载日志
			List<Object> idList = baseDao.getFieldDatasByCondition("documentlist", "dl_id", "dl_filepath='"+path+"'");
			for(Object id : idList){
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"下载文档","下载成功","dl_id="+id));	
			}
		} catch (IOException e) {
			return new String("{error:'文件路径不对，无法下载!'}".getBytes("utf-8"), "iso8859-1");
		}
		return new String("{success: true}".getBytes("utf-8"), "iso8859-1");
	}
    /**
     * 搜索
     * @param condition
     * @return
     */
    public Set<DocumentList> getFilesBySearch(String condition){
    	List<DocumentList> list = new ArrayList<DocumentList>();
    	StringBuffer sb = new StringBuffer();
    	//获取顶层目录下的文件
    	String sql = "select * from documentlist where dl_statuscode = 'AUDITED' and dl_kind = 0 and dl_parentid in (0,1) "
    			+ "and dl_name like '%" + condition + "%' or dl_labelinfo like '%" + condition + "%'" ;
    	sb.append(sql);
    	Employee employee = SystemSession.getUser();
    	if(employee != null){
    		if (!"admin".equals(employee.getEm_type())) {
    			sb.append(" and (dl_id in (select dp_dclid from documentjobpower where dp_role in(");
    			StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
    			if(employee.getEmpsJobs() != null) {
    				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
    					jobIds.append(",").append(empsJob.getJob_id());
    				}
    			}
    			sb.append(jobIds).append("))");
    			sb.append(" or dl_id in (select dp_dclid from documentpersonpower where dp_role=");
    			sb.append(employee.getEm_id()).append(") ");
    			sb.append(" or dl_id in (select dp_dclid from documentorgpower  left join HRORGEMPLOYEES on dp_role=OM_ORID where OM_EMID=");
    			sb.append(employee.getEm_id()).append(")) ");
    		}
    	}
    	
    	List<DocumentList> fileList = baseDao.getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<DocumentList>(DocumentList.class));
    	if(fileList != null){
    		list.addAll(fileList);
    	}
    	String dl_parentid = "0,1";
    	List<DocumentList> resultList = getFiles(list,dl_parentid,condition);
    	Set<DocumentList> set = new HashSet<DocumentList>(resultList);
		return set;
    }
    
    private List<DocumentList> getFiles(List<DocumentList> list, String parentId, String condition){
    	String getDirSql = "select dl_id from documentlist where dl_statuscode = 'AUDITED' and dl_kind = -1 and dl_parentid in (" + parentId + ")";
    	List<Map<String,Object>> dirList = baseDao.queryForList(getDirSql);
    	if(dirList != null){
    		for(Map<String,Object> map : dirList){
    			String dlId =  String.valueOf(map.get("dl_id".toUpperCase()));
    			String getFilesSql = "select * from documentlist where dl_statuscode = 'AUDITED' and dl_kind = 0 and dl_parentid = " + dlId + " and "
    					+ "dl_name like '%" + condition + "%' or dl_labelinfo like '%" + condition + "%'" ;
    			List<DocumentList> fileList = baseDao.getJdbcTemplate().query(getFilesSql, new BeanPropertyRowMapper<DocumentList>(DocumentList.class));
    			if(fileList != null){
    				list.addAll(fileList);
    			}
    			getFiles(list,dlId,condition);
    		}
    	}
    	return list;
    }
    
    /**
     * 查找文档操作日志
     * @param docId	文档ID
     * @return
     */
    public List<Map<String, Object>> getDocLog(int docId){
    	return baseDao.queryForList("select * from messagelog where ml_search='dl_id="+docId+"' order by ml_date desc");
    }
    
}
