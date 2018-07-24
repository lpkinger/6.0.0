package com.uas.erp.service.common.impl;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.interceptor.InterceptorUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BenchDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormAttach;
import com.uas.erp.service.bench.BenchService;
import com.uas.erp.service.common.AttachUploadedAble;
import com.uas.erp.service.common.DataListService;
import com.uas.erp.service.common.FormAttachService;

@Service
public class FormAttachServiceImpl implements FormAttachService {

	@Autowired
	private FormAttachDao formAttachDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private BenchDao benchDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DetailGridDao detailGridDao;

	@Override
	public List<FormAttach> getFormAttachs(String caller, int keyValue) {
		return formAttachDao.getFormAttachs(caller, keyValue);
	}

	@Override
	public JSONArray getFiles(String id) {
		JSONArray arr = new JSONArray();
		JSONObject obj = null;
		for (String i : id.split(";")) {
			if (i != null && !i.trim().equals("")) {
				obj = formAttachDao.getFilePath(Integer.parseInt(i));
				if (obj != null) {
					arr.add(obj);
				}
			}
		}
		return arr;
	}
	
	@Override
	public int beforeExport(String caller, String type, String condition, Employee employee,Boolean self,boolean jobemployee,HttpServletRequest req) {
		handlerService.handler(caller, "export", "before", new Object[] { condition });
		if ("datalist".equals(type)) {
			DataList dataList = dataListDao.getDataList(caller, employee.getEm_master());
			DataListService dataListService = (DataListService) ContextUtil.getBean("dataListService");
			/** 添加其他约束条件 */
			condition = dataListService.appendCondition(dataList, condition, employee);
			/**添加权限条件，看自己/看所有 */
			condition = dataListService.appendPowerCondition(dataList, condition, employee, self,jobemployee);
			
			String sql = dataList.getSearchSql(condition);
			if(jobemployee){
				sql = dataListDao.getSqlWithJobEmployee(null) + sql;
			}
			return baseDao.getCount(sql);
		}else if("scenegrid".equals(type)){
			
			BenchScene benchScene = benchDao.getBenchScene(caller,employee.getEm_master());
			if (benchScene==null) {
				BaseUtil.showError("此工作台场景未配置！");
			}
			if(benchScene.getBenchSceneGrids()==null||benchScene.getBenchSceneGrids().size()==0){
				BaseUtil.showError("此场景的列表未配置！");
			}
			BenchService benchService = (BenchService) ContextUtil.getBean("benchService");
			/** 添加其他约束条件 */
			condition = benchService.appendCondition(benchScene, condition, employee);
			boolean noControl = InterceptorUtil.noControl(req, employee);
			if (!noControl) {
				/**添加权限条件，看自己/看所有 */
				jobemployee = jobemployee || benchService.isJobEmployee(benchScene.getBs_caller(), employee);
				condition = benchService.appendPowerCondition(benchScene, employee, condition, self, jobemployee, true);
			}
			condition = benchService.getCondition(benchScene, employee, condition);
			String sql = benchScene.getSql(condition);
			if(jobemployee){
				sql = benchDao.getSqlWithJobEmployee(null) + sql;
			}
			return baseDao.getCount(sql);
		} else if ("detailgrid".equals(type)) {  //2018040452,type为detailgrid的也获取数量
			List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
			String table = detailGrids.get(0).getDg_table();
			Object[] objs = baseDao.getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition", "fo_caller='"
					+ caller + "'");
			if (objs != null) {// 优先用Form的配置
				if (objs[0] != null)
					table = objs[0].toString();
				if (objs[1] != null) {
					if ("".equals(condition)) {
						condition = objs[1].toString();
					} else {
						int index = condition.toLowerCase().indexOf("order by");
						if (index > -1) {
							condition = condition.substring(0, index) + " AND " + objs[1] + " " + condition.substring(index);
						} else {
							condition += " AND " + objs[1];
						}
					}
				}
			}
			return baseDao.getCountByCondition(table, condition);
		} else {
			return 1;
		}
	}

	@Override
	public void uploadAttachs(List<? extends AttachUploadedAble> attachAbles, String uploadPath, String refrenceKey, boolean sign,
			String signKey) {
		Map<String, Map<String, Object>> files = new HashMap<String, Map<String, Object>>();
		String[] srcFiles = new String[] {};
		for (AttachUploadedAble attachAble : attachAbles) {
			if (StringUtil.hasText(attachAble.getAttachs())) {
				String[] fileIds = attachAble.getAttachs().split(";");
				for (String fileId : fileIds) {
					if (StringUtil.hasText(fileId)) {
						SqlRowList rs = baseDao.queryForRowSet("select fp_name,fp_path from filepath where fp_id=?", fileId);
						if (rs.next()) {
							Map<String, Object> obj = new HashMap<String, Object>();
							obj.put(refrenceKey, attachAble.getReffrencValue());
							obj.put("name", rs.getString("fp_name"));
							files.put(FileUtil.getFileName(rs.getString("fp_path")), obj);
							srcFiles = Arrays.copyOf(srcFiles, srcFiles.length + 1);
							srcFiles[srcFiles.length - 1] = rs.getString("fp_path");
						}
					}
				}
			}
		}
		if (files.size() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonDeep(files));
			String zipFile = PathUtil.getTempPath() + File.separator + UUID.randomUUID().toString().replaceAll("\\-", "") + ".zip";
			boolean success = FileUtil.zip(srcFiles, zipFile);
			if (success) {
				try {
					HttpUtil.upload(uploadPath, zipFile, params, sign, signKey);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					File file = new File(zipFile);
					if (file.exists())
						file.delete();
				}
			}
		}
	}
}
