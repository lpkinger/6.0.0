package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.common.FormsDocService;

@Service("formsDocService")
public class FormsDocServiceImpl implements FormsDocService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public Map<String, Object> getFileList(String caller, Integer formsid, Integer id, Integer kind, Integer page, Integer start, Integer limit,
			String search) {
		
		Master master = SystemSession.getUser().getCurrentMaster();
		if (baseDao.isDBSetting(caller,"fileInParent")) {
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rootList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String condition = "1=1";
		List<Object[]> datas = new ArrayList<Object[]>();
		Object total = 0;

		if (id == null || "".equals(id)) {
			id = 0;
		}
		if (kind == null) {
			kind = -1;
		}
		String formCondition = "fd_caller='"+caller+"' and fd_formsid="+formsid;
		int count = baseDao.getCount("select count(1) from formsdoc where fd_kind=-1 and " + formCondition);
		if (count == 0) {
			String code = baseDao.sGetMaxNumber("FORMSDOC",2);
			String filestemp = baseDao.getFieldValue((master!=null?master.getMa_name()+".":"")+"form", "fo_filestemp", "fo_caller='" + caller + "'",String.class);
			if (StringUtil.hasText(filestemp)) {
				baseDao.execute("insert into formsdoc (fd_formsid,fd_id,fd_kind,fd_parentid,fd_name,fd_remark,fd_virtualpath,fd_detno,fd_doccode,fd_caller,fd_tempid) select "
						+ formsid
						+ ",formsdoc_seq.nextval,kind_,parentid_,name_,remark_,virtualpath_,detno_,"+code
						+",'"
						+ caller
						+ "',id_ from PRJDOC_TEMP where PRJTYPECODE_='"
						+ filestemp.toString()
						+ "'");
				
				baseDao.execute("update formsdoc a set a.fd_parentid=(select b.fd_id from formsdoc b,PRJDOC_TEMP where b.fd_tempid=id_ and id_=a.fd_parentid and a.fd_formsid=b.fd_formsid and a.fd_caller=b.fd_caller) where a.fd_parentid<>0 and a.fd_formsid="
						+ formsid + " and fd_caller='" + caller + "'");
			}
		}
		if (kind == 0) {
			if (page != null && limit != null) {
				int endPage = page * limit;
				int startPage = endPage - limit + 1;
				condition += " and rn<=" + endPage + " and rn>=" + startPage;
			}
			if (id == 0) {
				datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from formsdoc "
						+ "where fd_kind=0 and fd_name like '%" + search + "%' and " + formCondition + " order by fd_detno) a)",
						new String[] { "fd_id", "fd_kind", "fd_formsid", "fd_parentid", "fd_name", "fd_remark", "fd_virtualpath",
								"fd_detno", "fd_doccode", "fd_caller", "fd_filepath", "fd_operatime" }, condition);
				total = baseDao.getFieldDataByCondition("formsdoc", "count(1)", "fd_kind=0 and " + formCondition + " and fd_name like '%"
						+ search + "%'");
			} else {
				// 分页
				datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from formsdoc "
						+ "where fd_kind=0 and fd_parentid=" + id + " and " + formCondition + " order by fd_detno) a)", new String[] {
						"fd_id", "fd_kind", "fd_formsid", "fd_parentid", "fd_name", "fd_remark", "fd_virtualpath", "fd_detno",
						"fd_doccode", "fd_caller", "fd_filepath", "fd_operatime" }, condition);
				total = baseDao.getFieldDataByCondition("formsdoc", "count(1)", "fd_parentid=" + id + " and fd_kind=0");
			}
		} else {
			datas = baseDao.getFieldsDatasByCondition("formsdoc", new String[] { "fd_id", "fd_kind", "fd_formsid", "fd_parentid",
					"fd_name", "fd_remark", "fd_virtualpath", "fd_detno", "fd_doccode", "fd_caller" }, "fd_kind=-1 and " + formCondition
					+ " and " + condition + " order by fd_detno");
		}

		if (kind == -1) {
			modelMap.put("addroot", true);
		}
		for (Object[] obj : datas) {
			map = new HashMap<String, Object>();
			map.put("id", obj[0]);
			map.put("fd_id", obj[0]);
			map.put("fd_kind", obj[1]);
			map.put("fd_formsid", obj[2]);
			map.put("fd_parentid", obj[3]);
			map.put("fd_name", obj[4]);
			map.put("fd_remark", obj[5]);
			map.put("fd_virtualpath", obj[6]);
			map.put("fd_detno", obj[7]);
			map.put("fd_doccode", obj[8]);
			map.put("fd_caller", obj[9]);

			if (kind == -1 && "0".equals(obj[3].toString())) { // 取根目录，从根目录中取子目录

				List<Map<String, Object>> child = getChild(datas, obj);
				map.put("children", child);
				rootList.add(map);
			} else if (kind == 0) {
				map.put("fd_filepath", obj[10]);
				map.put("fd_operatime", obj[11]);
				list.add(map);
			}
		}

		if (kind == -1) {
			modelMap.put("datas", rootList);
		} else {
			modelMap.put("datas", list);
			modelMap.put("total", total);
		}

		return modelMap;
	}

	private List<Map<String, Object>> getChild(List<Object[]> datas, Object[] data) {
		List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
		Map<String, Object> childJson = null;
		for (Object[] tree : datas) {
			if (data[0].equals(tree[3])) {
				childJson = new HashMap<String, Object>();
				childJson.put("id", tree[0]);
				childJson.put("fd_id", tree[0]);
				childJson.put("fd_kind", tree[1]);
				childJson.put("fd_formsid", tree[2]);
				childJson.put("fd_parentid", tree[3]);
				childJson.put("fd_name", tree[4]);
				childJson.put("fd_remark", tree[5]);
				childJson.put("fd_virtualpath", tree[6]);
				childJson.put("fd_detno", tree[7]);
				childJson.put("fd_doccode", tree[8]);
				childJson.put("fd_caller", tree[8]);

				List<Map<String, Object>> childnext = getChild(datas, tree);
				childJson.put("children", childnext);
				child.add(childJson);
			}
		}
		return child;
	}

	@Override
	public String upload(Employee employee, String caller, Integer fieldId, String condition, FileUpload uploadItem) {
		
		if (baseDao.isDBSetting(caller, "fileInParent")) {
			Master master = SystemSession.getUser().getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		try {
			if (fieldId <= 0) {
				Object fd_id = baseDao.getFieldDataByCondition("formsdoc", "fd_id", condition);
				if (fd_id != null) {
					fieldId = Integer.parseInt(fd_id.toString());
				} else {
					return "{error: '文件不存在，不能上传附件！'}";
				}
			}

			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}

			String em_code = employee.getEm_code();
			String path = FileUtil.saveFile(uploadItem.getFile(), em_code);
			int id = filePathService.saveFilePath(path, (int) size, filename, employee);
			String newPath = filename + ";" + id;
			if (newPath != null && !"".equals(newPath)) {
				Object[] filePath = baseDao.getFieldsDataByCondition("formsdoc", "fd_filepath,fd_formsid", "fd_id=" + fieldId);
				if (!newPath.equals(filePath[0])) { // 如果与原来的文档路径不相等，则说明有更新
					List<String> sqls = new ArrayList<String>();
					if (StringUtil.hasText(filePath[0])) {
						String fp_id = String.valueOf(filePath[0]).split(";")[1];
						JSONObject obj = formAttachService.getFiles(fp_id).getJSONObject(0);
						String realPath = obj.getString("fp_path");
						if (FileUtil.deleteFile(realPath)) {
							sqls.add("delete from FilePath where fp_id = " + fp_id);
						}
					}
					String checkUp = "update formsdoc set fd_operator='" + employee.getEm_name() + "',fd_operatime=sysdate,fd_filepath='"
							+ newPath + "' where fd_id=" + fieldId;
					sqls.add(checkUp);
					baseDao.execute(sqls);
				}
			}
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}

	@Override
	public Map<String, Object> saveAndUpdateTree(String caller, String create, String update) {

		if (baseDao.isDBSetting(caller, "fileInParent")) {
			Master master = SystemSession.getUser().getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(create);
		List<Map<Object, Object>> updagteMaps = BaseUtil.parseGridStoreToMaps(update);
		List<String> ids = new ArrayList<String>();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 判断名称是否重复
		Object parentid = null;
		if (maps.size() > 0) {
			parentid = maps.get(0).get("fd_parentid");
		} else if (updagteMaps.size() > 0) {
			parentid = updagteMaps.get(0).get("fd_parentid");
		}
		if (parentid != null) {
			for (Map<Object, Object> map : maps) {
				// 判断名称是否重复
				String name=map.get("fd_name").toString();
				String detno = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(fd_detno) from formsdoc where fd_name =? and fd_parentid=?",
								String.class, new Object[] { name ,parentid});
				if (detno != null) {
					BaseUtil.showError("文件名称重复，序号：" + detno);
				}
				boolean havedetno = baseDao.checkIf(
						"formsdoc",
						"fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid=" + map.get("fd_parentid") + " AND fd_detno="
								+ map.get("fd_detno"));
				// 序号已存在就把后面的序号+1
				if (havedetno) {
					baseDao.updateByCondition("formsdoc", "fd_detno=fd_detno+1", "fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid="
							+ map.get("fd_parentid") + " AND fd_detno>=" + map.get("fd_detno"));
				}
			}
			for (Map<Object, Object> map : updagteMaps) {
				// 判断名称是否重复
				String name=map.get("fd_name").toString();
				String detno = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(fd_detno) from formsdoc where fd_name =? and fd_parentid=?",
								String.class, new Object[] { name ,parentid});
				if (detno != null) {
					BaseUtil.showError("文件名称重复，序号：" + detno);
				}

				Integer oldDetno = baseDao.getFieldValue("formsdoc", "fd_detno", "fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid="
						+ map.get("fd_parentid") + " AND fd_id=" + map.get("fd_id"), Integer.class);
				if (oldDetno != null && oldDetno != Integer.parseInt(map.get("fd_detno").toString())) {
					boolean havedetno = baseDao.checkIf(
							"formsdoc",
							"fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid=" + map.get("fd_parentid") + " AND fd_detno="
									+ map.get("fd_detno") + " AND fd_id<>" + map.get("fd_id"));
					if (havedetno) {
						// 序号增大就把之间的的序号-1，序号减小就把之间的的序号+1
						if (Integer.parseInt(map.get("fd_detno").toString()) > oldDetno) {
							baseDao.updateByCondition("formsdoc", "fd_detno=fd_detno-1",
									"fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid=" + map.get("fd_parentid") + " AND fd_detno>"
											+ oldDetno + " AND fd_detno<=" + map.get("fd_detno"));
						} else if (Integer.parseInt(map.get("fd_detno").toString()) < oldDetno) {
							baseDao.updateByCondition("formsdoc", "fd_detno=fd_detno+1",
									"fd_formsid='" + map.get("fd_formsid") + "' AND fd_parentid=" + map.get("fd_parentid") + " AND fd_detno<"
											+ oldDetno + " AND fd_detno>=" + map.get("fd_detno"));
						}
					}
				}

			}
		}else{
			BaseUtil.showError("请先选择目录！");
		}

		for (Map<Object, Object> map : maps) {
			int id = baseDao.getSeqId("FORMSDOC_SEQ");
			ids.add(String.valueOf(id));
			map.put("fd_id", id);
			String virtualpath = map.get("fd_virtualpath").toString() + "/" + map.get("fd_name");
			map.put("fd_virtualpath", virtualpath);
			String code = baseDao.sGetMaxNumber("FORMSDOC",2);
			map.put("fd_doccode", code);
		}
		for (Map<Object, Object> map : updagteMaps) {
			String oriVirtualpath = map.get("fd_virtualpath").toString();
			String virtualpath = map.get("fd_virtualpath").toString().substring(0, oriVirtualpath.lastIndexOf("/")) + "/"
					+ map.get("fd_name");
			map.put("fd_virtualpath", virtualpath);
		}
		List<String> saveSql = SqlUtil.getInsertSqlbyGridStore(maps, "formsdoc");
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updagteMaps, "formsdoc", "fd_id");
		
		baseDao.execute(saveSql);
		baseDao.execute(updateSql);

		modelMap.put("ids", ids);
		return modelMap;

	}

	@Override
	public void deleteNode(String caller, String id) {
		
		Master master = SystemSession.getUser().getCurrentMaster();
		if (baseDao.isDBSetting(caller,"fileInParent")) {
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		String[] ids = id.split(",");
		String ID = ids[0];

		Object status[] = baseDao.getFieldsDataByCondition((master!=null?master.getMa_name()+".":"")+"form left join formsdoc on fo_caller=fd_caller", new String[] { "fo_table",
				"fo_statuscodefield", "fo_keyfield", "fd_formsid" }, "fd_id=" + ID);
		boolean bool = baseDao.checkIf(status[0].toString(), status[1].toString() + "='AUDITED' and " + status[2].toString() + "="
				+ status[3].toString());
		if (bool) {
			BaseUtil.showError("该资料已审核，不允许删除");
		}
		List<String> sqls = new ArrayList<String>();
		for (String Id : ids) {
			String filePath = baseDao.getFieldValue("FormsDoc", "fd_filepath", "fd_id = " + Id, String.class);
			if (StringUtil.hasText(filePath)) {
				String fp_id = filePath.split(";")[1];
				JSONObject obj = formAttachService.getFiles(fp_id).getJSONObject(0);
				String realPath = obj.getString("fp_path");
				if (FileUtil.deleteFile(realPath)) {
					sqls.add("delete from FilePath where fp_id = " + fp_id);
				}
			}
		}
		sqls.add("delete from formsdoc where fd_id in (" + id + ")");
		baseDao.execute(sqls);
	}

}
