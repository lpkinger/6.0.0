package com.uas.erp.service.oa.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.service.oa.DocumentPowerService;
@Service
public class DocumentPowerServiceImpl implements DocumentPowerService {
	@Autowired
	private BaseDao baseDao;
	private static String SEE="dp_see";
	private static String DELETE="dp_delete";
	private static String SAVE="dp_save";
	private static String CONTROL="dp_control";
	private static String READ="dp_read";
	private static String DOWNLOAD="dp_download";
	private static String PRINT="dp_print";
	private static String SHARE="dp_share";
	private static String JOURANAL="dp_jouranal";
	@Override
	public void setDocPower(String folderId, String powers, String objects, int sub) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(powers);
		List<Map<Object,Object>> lists=BaseUtil.parseGridStoreToMaps(objects);
		List<String> sqls=new ArrayList<String>();
		Object []values=null;String table="",emids="",joids="",orids="";
		List<String> idsList = new ArrayList<String>();
		if(sub == 1){		//查找该目录下的所有子目录
			String idsSql = "select wm_concat(dl_id) from documentlist where dl_kind=-1 and dl_statuscode='AUDITED' start with dl_id="+folderId+" connect by dl_parentid=prior dl_id  order by dl_id";
			String ids = baseDao.queryForObject(idsSql, String.class);
			idsList.addAll(Arrays.asList(ids.split(",")));
		}else{
			idsList.add(folderId);
		}
		for(String id: idsList){
			map.put("DP_DCLID",id);
			for(Map<Object,Object> obj:lists){
				values=String.valueOf(obj.get("value")).split("#");
				if("employee".equals(values[0])){
					table="DOCUMENTPERSONPOWER";
					emids+=values[1]+",";
				}else  if("job".equals(values[0])){
					table="DOCUMENTJOBPOWER";
					joids+=values[1]+",";
				}else if("org".equals(values[0])){
					table="DOCUMENTORGPOWER";
					orids+=values[1]+",";
				}
				sqls.add(SqlUtil.getInsertSqlByFormStore(map, table, new String[]{"DP_ROLE","DP_NAME","DP_ID"},new Object[]{values[1],obj.get("text"),
						baseDao.getSeqId(table+"_SEQ")}));
				baseDao.deleteByCondition(table,"dp_dclid="+id+"and dp_role="+values[1]);
			}
			baseDao.execute(sqls); 
			sqls.clear();
			if(emids.length()>0){
				baseDao.procedure("sp_checkdocpersonpower", new Object[] { emids.substring(0, emids.lastIndexOf(",")-1), id });
			}
			if(joids.length()>0){
				baseDao.procedure("sp_checkdocjobpower", new Object[] { joids.substring(0, joids.lastIndexOf(",")-1), id });
			}
			if(orids.length()>0){
				baseDao.procedure("sp_checkdocorgpower", new Object[] { orids.substring(0, orids.lastIndexOf(",")-1), id });
			}
			baseDao.logger.others("权限设置", "设置成功", "Document", "dlc_id", id);
		}
		
	}
	@Override
	public void updatePowerSet(String param) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(param);
		map.remove("dp_type");
		String table=String.valueOf(map.get("dp_table"));
		map.remove("dp_table");
		String sql=SqlUtil.getUpdateSqlByFormStore(map, table, "dp_id");
		baseDao.execute(sql);
		baseDao.logger.others("权限更新", "更新成功", "Document", "dl_id", map.get("dp_dclid"));
	}
	@Override
	public void deletePowerSet(String param) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(param);
		baseDao.deleteById(String.valueOf(map.get("dp_table")), "dp_id",(Integer)map.get("dp_id"));
		baseDao.logger.others("权限删除", "删除成功", "Document", "dl_id",map.get("dp_dclid"));
	}
	@Override
	public void checkSeePower(int folderId) {
		 if(!CheckPower(SEE,folderId)) BaseUtil.showError("您没有<浏览>该文件的权限!");
	}
	@Override
	public void checkDeletePower(int folderId) {
		if(!CheckPower(DELETE,folderId)) BaseUtil.showError("您没有<删除>该文件的权限!");
	}
	@Override
	public void checkSavePower(int folderId) {
		if(!CheckPower(SAVE,folderId)) BaseUtil.showError("您没有<修改>该文件的权限!");
	}
	@Override
	public void checkControlPower(int folderId) {
		 if(!CheckPower(CONTROL,folderId)) BaseUtil.showError("您没有<管理>该文件或文件夹的权限!");
	}
	@Override
	public void checkReadPower(int folderId) {
		 if(!CheckPower(READ,folderId))  BaseUtil.showError("您没有<阅读>该文件的权限!");
	}
	@Override
	public void checkDownloadPower(int folderId) {
		 if(!CheckPower(DOWNLOAD,folderId)) BaseUtil.showError("您没有<下载>该文件的权限!");
	}
	@Override
	public void checkPrintPower(int folderId) {
		if(!CheckPower(PRINT,folderId)) BaseUtil.showError("您没有<打印>该文件的权限!");
	}
	@Override
	public void checkSharePower(int folderId) {
		if(!CheckPower(SHARE,folderId)) BaseUtil.showError("您没有<共享>该文件的权限!");
	}
	@Override
	public void checkJouranalPower(int folderId) {
		 if(!CheckPower(JOURANAL,folderId)) BaseUtil.showError("您没有<查看日志>该文件的权限!");
	}
	private boolean CheckPower(String type,int forlderId){
		Employee employee=SystemSession.getUser();
		boolean  bool=false;
		if(employee!=null){
			if("admin".equals(employee.getEm_type())){
				bool=true;
			}else {
				bool=baseDao.checkIf("DOCUMENTORGPOWER LEFT JOIN HRORGEMPLOYEES ON DP_ROLE=OM_ORID", "DP_DCLID="+forlderId+" AND "+type+"=1 AND OM_EMID="+employee.getEm_id());
				if(!bool){
					String jobIds=String.valueOf(employee.getEm_defaulthsid());
					if(employee.getEmpsJobs() != null) {
						for (EmpsJobs empsJob : employee.getEmpsJobs()) {
							jobIds+=","+empsJob.getJob_id();
						}
					}
					bool=baseDao.checkIf("DOCUMENTJOBPOWER", "DP_DCLID="+forlderId+" AND "+type+"=1  AND DP_ROLE in ("+jobIds+")");
					if(!bool){
						bool=baseDao.checkIf("DOCUMENTPERSONPOWER", "DP_DCLID="+forlderId+" AND "+type+"=1  AND DP_ROLE="+employee.getEm_id());
					}
				}

			}
		}
		return bool;
	}
	@Override
	public boolean CheckPowerByFolderId(int folderId, String type) {
		return CheckPower(type,folderId);
	}
}
