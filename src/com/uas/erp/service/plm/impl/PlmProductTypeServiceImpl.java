package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProductType;
import com.uas.erp.service.plm.PlmProductTypeService;

@Service
public class PlmProductTypeServiceImpl implements PlmProductTypeService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getFileList(String productTypeCode, Integer id, Integer kind, Integer page, Integer start, Integer limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rootList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> datas = null;
		Object total = null;
		if (id == null || "".equals(id)) {
			id = 0;
		}
		if (kind == null) {
			kind = -1;
		}

		if (kind == 0) {
			String condition = "1=1";
			if (page != null && limit != null) {
				int endPage = page * limit;
				int startPage = endPage - limit + 1;
				condition += " and rn<=" + endPage + " and rn>=" + startPage;
			}
			// 分页
			datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from prjdoc_temp where kind_=0 and parentid_="
					+ id + " order by detno_) a)", new String[] { "id_", "kind_", "prjtypecode_", "parentid_", "name_", "remark_",
					"virtualpath_", "detno_", "code_", "attach_" }, condition + " and prjtypecode_='" + productTypeCode + "'");
			total = baseDao.getFieldDataByCondition("prjdoc_temp", "count(1)", "parentid_=" + id + " and kind_=0");
		} else {
			datas = baseDao.getFieldsDatasByCondition("prjdoc_temp", new String[] { "id_", "kind_", "prjtypecode_", "parentid_", "name_",
					"remark_", "virtualpath_", "detno_", "code_" }, "kind_=-1 and prjtypecode_='" + productTypeCode + "' order by detno_");
		}

		for (Object[] obj : datas) {
			map = new HashMap<String, Object>();
			map.put("id_", obj[0]);
			map.put("kind_", obj[1]);
			map.put("prjtypecode_", obj[2]);
			map.put("parentid_", obj[3]);
			map.put("name_", obj[4]);
			map.put("remark_", obj[5]);
			map.put("qtip", obj[5]);
			map.put("virtualpath_", obj[6]);
			map.put("detno_", obj[7]);
			map.put("code_", obj[8]);
			map.put("id", obj[0]);

			if (kind == -1 && "0".equals(obj[3].toString())) { // 取根目录，从根目录中取子目录
				List<Map<String, Object>> child = getChild(datas, obj);
				map.put("children", child);
				rootList.add(map);
			} else if (kind == 0) {
				map.put("attach_", obj[9]);
				list.add(map);
			}

		}

		if (kind == -1) {
			modelMap.put("datas", rootList);
		} else {
			modelMap.put("datas", list);
		}
		modelMap.put("total", total);
		return modelMap;
	}

	private List<Map<String, Object>> getChild(List<Object[]> datas, Object[] data) {
		List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
		Map<String, Object> childJson = null;
		for (Object[] tree : datas) {
			if (data[0].equals(tree[3])) {
				childJson = new HashMap<String, Object>();
				childJson.put("id_", tree[0]);
				childJson.put("id", tree[0]);
				childJson.put("kind_", tree[1]);
				childJson.put("prjtypecode_", tree[2]);
				childJson.put("parentid_", tree[3]);
				childJson.put("name_", tree[4]);
				childJson.put("remark_", tree[5]);
				childJson.put("qtip", tree[5]);
				childJson.put("virtualpath_", tree[6]);
				childJson.put("detno_", tree[7]);
				childJson.put("code_", tree[8]);
				List<Map<String, Object>> childnext = getChild(datas, tree);
				childJson.put("children", childnext);
				child.add(childJson);
			}
		}
		return child;
	}

	@Override
	@Transactional
	public Map<String, Object> saveAndUpdateTree(String create, String update) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(create);
		List<Map<Object, Object>> updagteMaps = BaseUtil.parseGridStoreToMaps(update);

		List<String> ids = new ArrayList<String>();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		for (Map<Object, Object> map : maps) {
			boolean havedetno = baseDao.checkIf(
					"prjdoc_temp",
					"PRJTYPECODE_='" + map.get("prjtypecode_") + "' AND parentid_=" + map.get("parentid_") + " AND detno_="
							+ map.get("detno_"));
			// 序号已存在就把后面的序号+1
			if (havedetno) {
				baseDao.updateByCondition("prjdoc_temp", "detno_=detno_+1", "PRJTYPECODE_='" + map.get("prjtypecode_") + "' AND parentid_="
						+ map.get("parentid_") + " AND detno_>=" + map.get("detno_"));
			}
		}
		for (Map<Object, Object> map : updagteMaps) {
			Integer oldDetno = baseDao.getFieldValue("prjdoc_temp", "detno_", "PRJTYPECODE_='" + map.get("prjtypecode_")
					+ "' AND parentid_=" + map.get("parentid_") + " AND id_=" + map.get("id_"), Integer.class);
			if (oldDetno != null && oldDetno != Integer.parseInt(map.get("detno_").toString())) {
				boolean havedetno = baseDao.checkIf(
						"prjdoc_temp",
						"PRJTYPECODE_='" + map.get("prjtypecode_") + "' AND parentid_=" + map.get("parentid_") + " AND detno_="
								+ map.get("detno_") + " AND id_<>" + map.get("id_"));
				if (havedetno) {
					// 序号增大就把之间的的序号-1，序号减小就把之间的的序号+1
					if (Integer.parseInt(map.get("detno_").toString()) > oldDetno) {
						baseDao.updateByCondition("prjdoc_temp", "detno_=detno_-1",
								"PRJTYPECODE_='" + map.get("prjtypecode_") + "' AND parentid_=" + map.get("parentid_") + " AND detno_>"
										+ oldDetno + " AND detno_<=" + map.get("detno_"));
					} else if (Integer.parseInt(map.get("detno_").toString()) < oldDetno) {
						baseDao.updateByCondition("prjdoc_temp", "detno_=detno_+1",
								"PRJTYPECODE_='" + map.get("prjtypecode_") + "' AND parentid_=" + map.get("parentid_") + " AND detno_<"
										+ oldDetno + " AND detno_>=" + map.get("detno_"));
					}
				}
			}

		}
		for (Map<Object, Object> map : maps) {
			if (map.get("code_") == null || "".equals(map.get("code_"))) {
				String code = baseDao.sGetMaxNumber("PRJDOC_TEMP", 2);
				map.remove("code_");
				map.put("code_", code);
			}
			int id = baseDao.getSeqId("PRJDOC_TEMP_SEQ");
			ids.add(String.valueOf(id));
			map.remove("id_");
			map.put("id_", id);
			String virtualpath = map.get("virtualpath_").toString() + "/" + map.get("name_");
			map.remove("virtualpath_");
			map.put("virtualpath_", virtualpath);
		}

		List<String> updatePath = new ArrayList<String>();
		for (Map<Object, Object> map : updagteMaps) {
			if (map.get("code_") == null || "".equals(map.get("code_"))) {
				String code = baseDao.sGetMaxNumber("PRJDOC_TEMP", 2);
				map.remove("code_");
				map.put("code_", code);
			}
			String oriVirtualpath = map.get("virtualpath_").toString();
			String virtualpath = map.get("virtualpath_").toString().substring(0, oriVirtualpath.lastIndexOf("/")) + "/" + map.get("name_");
			map.remove("virtualpath_");
			map.put("virtualpath_", virtualpath);
			// 如果是目录，则更新下面所有子项的虚拟路径
			if ("-1".equals(map.get("kind_").toString())) {
				virtualpath += "/";
				String sql = "update prjdoc_temp set virtualpath_='" + virtualpath
						+ "'||name_ where id_ in (select id_ from prjdoc_temp start with id_=" + map.get("id_")
						+ " connect by prior id_=parentid_) and id_<>" + map.get("id_");
				updatePath.add(sql);
			}

		}
		SqlUtil.getInsertSqlbyGridStore(maps, "prjdoc_temp");

		List<String> saveSql = SqlUtil.getInsertSqlbyGridStore(maps, "prjdoc_temp");
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updagteMaps, "prjdoc_temp", "id_");

		baseDao.execute(saveSql);
		baseDao.execute(updateSql);
		baseDao.execute(updatePath);

		Object prjtypecode_ = null;
		Object parentid_ = null;
		if (maps.size() > 0) {
			prjtypecode_ = maps.get(0).get("prjtypecode_");
			parentid_ = maps.get(0).get("parentid_");
		} else if (updagteMaps.size() > 0) {
			prjtypecode_ = updagteMaps.get(0).get("prjtypecode_");
			parentid_ = updagteMaps.get(0).get("parentid_");
		}
		if (prjtypecode_ != null) {
			// 判断编号是否重复
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(detno_) from prjdoc_temp where (code_) in (select code_ from prjdoc_temp where prjtypecode_=? and parentid_=? group by code_ having count(1)>1) and prjtypecode_=? and parentid_=?",
							String.class, new Object[] { prjtypecode_, parentid_, prjtypecode_, parentid_ });
			if (dets != null) {
				BaseUtil.showError("文件编号重复，序号：" + dets);
			}
		}
		if (prjtypecode_ != null && parentid_ != null) {
			// 判断名称是否重复
			String detno = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(detno_) from prjdoc_temp where name_ in (select name_ from prjdoc_temp where prjtypecode_=? and parentid_=? group by name_ having count(1)>1) and prjtypecode_=? and parentid_=? order by detno_ asc",
							String.class, new Object[] { prjtypecode_, parentid_, prjtypecode_, parentid_ });
			if (detno != null) {
				BaseUtil.showError("文件名称重复，序号：" + detno);
			}
		}

		modelMap.put("ids", ids);
		return modelMap;

	}

	@Override
	@Transactional
	public void deleteNode(String id, String type, String productTypeCode) {
		if (id != null && "file".equals(type)) {
			String[] Ids = id.split(",");
			String ids = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(docid_temp) from ProjectTask_Temp where prjtypecode_ = ?", String.class, productTypeCode);
			if (ids != null) {
				if (ids != null) {
					String[] IDs = ids.split(",");
					for (String ID : IDs) {
						for (String iD : Ids) {
							if (iD.equals(ID)) {
								BaseUtil.showError("当前文件关联有产品任务，不允许删除！");
							}
						}
					}
				}
			}
			baseDao.execute("delete from prjdoc_temp where prjtypecode_ = '" + productTypeCode + "' and id_ in (" + id + ")");
		} else if (id != null & "index".equals(type)) {
			baseDao.execute("delete from prjdoc_temp where prjtypecode_ = '" + productTypeCode
					+ "' and  id_ in(select id_ from prjdoc_temp start with id_=" + id + " connect by prior id_=parentid_)");
		} else if (id != null & "allfile".equals(type)) { // 删除目录下的所有文件
			String ids = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(docid_temp) from ProjectTask_Temp where prjtypecode_ = ?", String.class, productTypeCode);
			List<Object> docids = baseDao.getFieldDatasByCondition("prjdoc_temp", "id_", "parentid_=" + id + " and kind_=0");
			if (docids.size() > 0) {
				for (Object docid : docids) {
					if (ids != null) {
						String[] IDs = ids.split(",");
						if (IDs != null && IDs.length > 0) {
							for (String ID : IDs) {
								if (ID.equals(docid.toString())) {
									BaseUtil.showError("当前文件关联有产品任务，不允许删除！");
								}
							}
						}
					}
				}
			}
			baseDao.execute("delete from prjdoc_temp where prjtypecode_ = '" + productTypeCode + "' and  parentid_=" + id + " and kind_=0");
		}
	}

	/**
	 * wusy
	 */
	@Override
	public List<JSONTree> getRootProductType(int parentid) {
		//检查是否存在FormFiles
		boolean bool = baseDao.checkByCondition("PlmProductType", "pt_code = 'FormFiles' and nvl(pt_subof,0)=0");
		if (bool) {
			int id = baseDao.getSeqId("PLMPRODUCTTYPE_SEQ");
			Employee employee = SystemSession.getUser();
			String sql = "insert into PlmProductType(pt_id,pt_name,pt_description,pt_recorder,pt_recorddate,pt_code,pt_subof)"
					+ " values ("+id+",'表单附件模板','表单的文件管理模板','"+employee.getEm_name()+"',sysdate,'FormFiles',0)";
			baseDao.execute(sql);
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ProductType> types = baseDao.getJdbcTemplate().query("select * from plmproducttype order by pt_id desc",
				new BeanPropertyRowMapper(ProductType.class));
		List<JSONTree> tree = new ArrayList<JSONTree>();
		ProductType type = null;
		JSONTree jsontree = null;
		for (int i = 0; i < types.size(); i++) {
			type = types.get(i);
			jsontree = new JSONTree(type);
			jsontree.setData(type.getPt_code());
			jsontree.setParentId(type.getPt_subof());
			if (type.getPt_subof() == 0) {
				List<JSONTree> children = getChild(type, types);
				jsontree.setChildren(children);
				if (children.size() == 0) {
					jsontree.setLeaf(true);
				} else {
					jsontree.setLeaf(false);
				}
				tree.add(jsontree);
			}
		}
		return tree;
	}

	private List<JSONTree> getChild(ProductType type, List<ProductType> types) {
		List<JSONTree> child = new ArrayList<JSONTree>();
		ProductType data = null;
		JSONTree json = null;
		for (int i = 0; i < types.size(); i++) {
			data = types.get(i);
			if (data.getPt_subof() == type.getPt_id()) {
				json = new JSONTree(data);
				json.setData(data.getPt_code());
				json.setParentId(data.getPt_subof());
				json.setParentId(data.getPt_subof());
				List<JSONTree> children = getChild(data, types);
				json.setChildren(children);
				if (children.size() == 0) {
					json.setLeaf(true);
				} else {
					json.setLeaf(false);
				}
				child.add(json);
			}
		}
		return child;
	}

	@Override
	public ProductType saveProductKind(String formStore, int parentId) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		ProductType productType = null;
		// 产品类型存在就不能保存
		boolean bool = baseDao.checkByCondition("plmproducttype", "pt_name='" + store.get("pt_name") + "' and pt_subof =" + parentId);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("同父级同层级的产品类型名称不允许重复!"));
		}
		int id = baseDao.getSeqId("PLMPRODUCTTYPE_SEQ");
		String pt_code = baseDao.sGetMaxNumber("plmproducttype", 2);
		store.put("pt_id", id);
		store.put("pt_subof", parentId);
		store.put("pt_recorder", SystemSession.getUser().getEm_name());
		store.put("pt_recorddate", DateUtil.parseDateToString(null, Constant.YMD));
		store.put("pt_code", pt_code);
		String formSql = SqlUtil.getInsertSqlByMap(store, "plmproducttype");
		baseDao.execute(formSql);
		productType = baseDao.queryBean("select * from plmproducttype where pt_id=" + id, ProductType.class);
		return productType;
	}

	@Override
	public void deleteProductKind(int id, String caller) {
		if (id == 0) {
			BaseUtil.showError("请选择要删除的数据");
		} else {
			Object code = baseDao.getFieldDataByCondition("PlmProductType", "pt_code", "pt_id=" + id);
			boolean bool = baseDao.checkByCondition("PROJECTPHASE_TEMP", "PRJTYPECODE_='" + code + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil.getLocalMessage("该产品类型已经被产品阶段模板使用，不允许删除!"));
			}
			bool = baseDao.checkByCondition("PRJDOC_TEMP", "PRJTYPECODE_='" + code + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil.getLocalMessage("该产品类型已经被产品目录模板使用，不允许删除!"));
			}
			bool = baseDao.checkByCondition("PROJECTTASK_TEMP", "PRJTYPECODE_='" + code + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil.getLocalMessage("该产品类型已经被产品任务书模板使用，不允许删除!"));
			}
			bool = baseDao.checkByCondition("PROJECT", "PRJ_PRODUCTTYPECODE='" + code + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil.getLocalMessage("该产品类型已经被项目申请单使用，不允许删除!"));
			}
			baseDao.execute("delete from PlmProductType where pt_id in(select pt_id from PlmProductType start with pt_id=" + id
					+ " connect by prior pt_id=pt_subof)");
		}
	}

	@Override
	public void updateProductKind(String formStore, int parentId) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 产品类型存在就不能保存
		boolean bool = baseDao.checkByCondition("plmproducttype", "pt_name='" + store.get("pt_name") + "' and pt_subof =" + parentId
				+ " and pt_id<>" + store.get("pt_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("同父级同层级的产品类型名称不允许重复!"));
		}
		store.put("pt_recorder", SystemSession.getUser().getEm_name());
		store.put("pt_recorddate", DateUtil.parseDateToString(null, Constant.YMD));
		String updateSql = SqlUtil.getUpdateSqlByFormStore(store, "plmproducttype", "pt_id");
		baseDao.execute(updateSql);
	}

	/**
	 * maz
	 */
	@Override
	public Map<String, Object> getProjectPhaseData(String condition, Integer start, Integer page, Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("".equals(condition) || condition == null) {
			condition = "1=1";
		}
		if (page != null && start != null && limit != null) {
			int end = page * limit;
			String sql = "select * from (select a.*,rownum rn from (select * from (select * from PROJECTPHASE_TEMP  where " + condition
					+ " order by ph_detno_temp asc) where rownum<=" + end + ") a) where rn >" + start;
			map.put("count", baseDao.getCount("select count(1) from PROJECTPHASE_TEMP" + " where " + condition));
			map.put("data", baseDao.getJdbcTemplate().queryForList(sql));
		} else {
			String sql = "select * from PROJECTPHASE_TEMP  where " + condition + " order by ph_detno_temp asc";
			map.put("count", baseDao.getCount("select count(1) from PROJECTPHASE_TEMP" + " where " + condition));
			map.put("data", baseDao.getJdbcTemplate().queryForList(sql));
		}
		return map;
	}

	// @Override
	// public void saveProjectPhase(String productTypeCode,String formStore) {
	// Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
	// if(store.get("PH_NAME_TEMP")==null||store.get("PH_NAME_TEMP").equals("")){
	// BaseUtil.showError("请填写阶段描述");
	// }
	// int id = baseDao.getSeqId("PROJECTPHASE_TEMP_SEQ");
	// store.put("PH_ID_TEMP", id);
	// String formSql = SqlUtil.getInsertSqlByFormStore(store, "PROJECTPHASE_TEMP", new String[] {}, new Object[] {});
	// baseDao.execute(formSql);
	// }

	@Override
	public void updateProjectPhase(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (store.get("PH_NAME_TEMP") == null || store.get("PH_NAME_TEMP").equals("")) {
			BaseUtil.showError("请填写阶段描述");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PROJECTPHASE_TEMP", "PH_ID_TEMP");
		baseDao.execute(formSql);
	}

	@Override
	public void deleteProjectPhase(String id) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(id);
		String idcondition = " PH_ID_TEMP in (";
		for (int i = 0; i < maps.size(); i++) {
			idcondition += maps.get(i).get("PH_ID_TEMP") + ",";
		}
		idcondition = idcondition.substring(0, idcondition.length() - 1) + ")";
		baseDao.deleteByCondition("PROJECTPHASE_TEMP", idcondition);
	}

	@Override
	@Transactional
	public void saveProjectPhase(String productTypeCode, String gridStore) { // maz 更新保存产品阶段计划的信息，保存和更新按钮功能二合一
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : store) {
			if (map.get("PH_NAME_TEMP") == null || "".equals(map.get("PH_NAME_TEMP"))) {
				BaseUtil.showError("请填写产品阶段描述");
			}
			if ("".equals(map.get("PH_ID_TEMP")) || map.get("PH_ID_TEMP") == null || "0".equals("PH_ID_TEMP")) {
				int id = baseDao.getSeqId("PROJECTPHASE_TEMP_SEQ");
				boolean havedetno = baseDao.checkIf("PROJECTPHASE_TEMP", "PRJTYPECODE_='" + map.get("PRJTYPECODE_")
						+ "' AND PH_DETNO_TEMP=" + map.get("PH_DETNO_TEMP"));
				// 序号已存在就把后面的序号+1
				if (havedetno) {
					baseDao.updateByCondition("PROJECTPHASE_TEMP", "PH_DETNO_TEMP=PH_DETNO_TEMP+1",
							"PRJTYPECODE_='" + map.get("PRJTYPECODE_") + "' AND PH_DETNO_TEMP>=" + map.get("PH_DETNO_TEMP"));
				}
				baseDao.execute("insert into PROJECTPHASE_TEMP(PH_ID_TEMP,PRJTYPECODE_,PH_NAME_TEMP,PH_REMARK_TEMP,PH_DETNO_TEMP) values('"
						+ id + "','" + (map.get("PRJTYPECODE_").equals("") ? productTypeCode : map.get("PRJTYPECODE_")) + "','"
						+ map.get("PH_NAME_TEMP") + "','" + map.get("PH_REMARK_TEMP") + "'," + map.get("PH_DETNO_TEMP") + ")");
			} else {
				Integer oldDetno = baseDao.getFieldValue("PROJECTPHASE_TEMP", "PH_DETNO_TEMP", "PRJTYPECODE_='" + map.get("PRJTYPECODE_")
						+ "' AND PH_ID_TEMP=" + map.get("PH_ID_TEMP"), Integer.class);
				if (oldDetno != null && oldDetno != Integer.parseInt(map.get("PH_DETNO_TEMP").toString())) {
					boolean havedetno = baseDao.checkIf("PROJECTPHASE_TEMP", "PRJTYPECODE_='" + map.get("PRJTYPECODE_")
							+ "' AND PH_DETNO_TEMP=" + map.get("PH_DETNO_TEMP") + " AND PH_ID_TEMP<>" + map.get("PH_ID_TEMP"));
					if (havedetno) {
						// 序号增大就把之间的的序号-1，序号减小就把之间的的序号+1
						if (Integer.parseInt(map.get("PH_DETNO_TEMP").toString()) > oldDetno) {
							baseDao.updateByCondition("PROJECTPHASE_TEMP", "PH_DETNO_TEMP=PH_DETNO_TEMP-1",
									"PRJTYPECODE_='" + map.get("PRJTYPECODE_") + "' AND PH_DETNO_TEMP>" + oldDetno + " AND PH_DETNO_TEMP<="
											+ map.get("PH_DETNO_TEMP"));
						} else if (Integer.parseInt(map.get("PH_DETNO_TEMP").toString()) < oldDetno) {
							baseDao.updateByCondition("PROJECTPHASE_TEMP", "PH_DETNO_TEMP=PH_DETNO_TEMP+1",
									"PRJTYPECODE_='" + map.get("PRJTYPECODE_") + "' AND PH_DETNO_TEMP<" + oldDetno + " AND PH_DETNO_TEMP>="
											+ map.get("PH_DETNO_TEMP"));
						}
					}
				}
				baseDao.execute("update PROJECTPHASE_TEMP set PH_REMARK_TEMP='"
						+ (map.get("PH_REMARK_TEMP") == null ? "" : map.get("PH_REMARK_TEMP")) + "',PH_NAME_TEMP='"
						+ map.get("PH_NAME_TEMP") + "',PH_DETNO_TEMP=" + map.get("PH_DETNO_TEMP") + " where PH_ID_TEMP='"
						+ map.get("PH_ID_TEMP") + "'");
			}
		}

		Object prjtypecode_ = null;
		if (store.size() > 0) {
			prjtypecode_ = store.get(0).get("PRJTYPECODE_");
		}
		if (prjtypecode_ != null) {
			// 判断产品阶段描述是否重复
			String detno = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(PH_DETNO_TEMP) from PROJECTPHASE_TEMP where PH_NAME_TEMP in (select PH_NAME_TEMP from PROJECTPHASE_TEMP where PRJTYPECODE_=? group by PH_NAME_TEMP having count(1)>1) and PRJTYPECODE_=? order by PH_DETNO_TEMP asc",
							String.class, new Object[] { prjtypecode_, prjtypecode_ });
			if (detno != null) {
				BaseUtil.showError("产品阶段描述重复，序号：" + detno);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getProjectFileTree(String condition) {
		if (condition == null || "".equals(condition)) {
			condition = "1=2";
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("id_", 0);
		List<Map<String, Object>> rootchild = getChildrenNodes(root, condition);
		return rootchild;
	}

	List<Map<String, Object>> getChildrenNodes(Map<String, Object> parentNode, String condition) {

		SqlRowList rs = baseDao.queryForRowSet("select * from prjdoc_temp where " + condition + " and parentid_ =" + parentNode.get("id_")
				+ " order by detno_ asc");
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", rs.getInt("id_"));
			node.put("id_", rs.getInt("id_"));
			node.put("name_", rs.getString("name_"));
			node.put("text", rs.getString("name_"));
			node.put("expanded", true);
			node.put("qtip", rs.getString("name_"));

			if (rs.getInt("kind_") == 0) {
				node.put("leaf", true);
				node.put("checked", false);
			} else {
				node.put("leaf", false);
				node.put("children", getChildrenNodes(node, condition));
			}
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	public List<Map<String, Object>> getTaskBookTree(String condition) {
		if (condition == null || "".equals(condition)) {
			condition = "1=2";
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Id_temp", 0);
		List<Map<String, Object>> rootchild = getChildrenNode(root, condition);
		return rootchild;
	}

	List<Map<String, Object>> getChildrenNode(Map<String, Object> parentNode, String condition) {

		SqlRowList rs = baseDao.queryForRowSet("select * from ProjectTask_Temp where " + condition + " and parentId ="
				+ parentNode.get("Id_temp") + " order by detno_temp asc");
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", rs.getInt("Id_temp"));
			node.put("Id_temp", rs.getInt("Id_temp"));
			node.put("parentId", rs.getInt("parentId"));
			node.put("detno_temp", rs.getInt("detno_temp"));
			node.put("name_temp", rs.getString("name_temp"));
			node.put("description_temp", rs.getString("description_temp"));
			node.put("pretaskdetno_temp", rs.getString("pretaskdetno_temp"));
			node.put("duration_temp", rs.getObject("duration_temp"));
			node.put("resourceId_temp", rs.getString("resourceId_temp"));
			node.put("resourcecode_temp", rs.getString("resourcecode_temp"));
			node.put("resourcename_temp", rs.getString("resourcename_temp"));
			node.put("resourceunits_temp", rs.getString("resourceunits_temp"));
			node.put("docId_temp", rs.getString("docId_temp"));
			node.put("docname_temp", rs.getString("docname_temp"));
			node.put("tasktype_temp", rs.getString("tasktype_temp"));
			node.put("text", rs.getString("name_temp"));
			node.put("qtip", rs.getString("name_temp"));
			node.put("taskclass_temp", rs.getString("taskclass_temp"));
			node.put("condition_temp", rs.getString("condition_temp"));
			node.put("conditioncode_temp", rs.getString("conditioncode_temp"));
			node.put("taskclass_temp", rs.getString("taskclass_temp"));

			List<Map<String, Object>> childnodes = getChildrenNode(node, condition);
			if (childnodes.size() == 0) {
				node.put("leaf", true);
			} else {
				node.put("expanded", true);
				node.put("leaf", false);
				node.put("children", childnodes);
			}
			nodes.add(node);
		}

		return nodes;
	}

	public boolean ImportMpp(String productTypeCode, ProjectFile pf) {

		Map<Integer, Map<String, Object>> resourceMap = new HashMap<Integer, Map<String, Object>>();
		// SQL语句
		List<String> sqls = new ArrayList<String>();
		// 所有任务信息
		List<Task> tasks = pf.getAllTasks();
		// 任务信息
		Map<Integer, Integer> subofMap = new HashMap<Integer, Integer>();
		// 分析资源
		getResources(pf, resourceMap);

		try {
			for (int i = 0; i < tasks.size(); i++) {
				if (tasks.get(i).getID() != 0) {
					List<Relation> relations = tasks.get(i).getPredecessors();
					Map<String, Object> node = new HashMap<String, Object>();
					int id = baseDao.getSeqId("PROJECTTASK_TEMP_SEQ");
					node.put("Id_temp", id);
					node.put("name_temp", tasks.get(i).getName());
					node.put("duration_temp", tasks.get(i).getDuration().getDuration());
					setResouceAssignments(tasks.get(i), node, resourceMap);
					// 有子任务就获取子节点
					if (tasks.get(i).getChildTasks().size() > 0) {
						for (Task t : tasks.get(i).getChildTasks()) {
							subofMap.put(t.getID(), id);
						}
					}
					for (int j = i + 1; j < tasks.size(); j++) {
						if (tasks.get(i).getID() == tasks.get(j).getID()) {
							BaseUtil.showError("任务" + tasks.get(i).getName() + "和任务" + tasks.get(j).getName() + "序号重复，不允许导入！");
						}
					}
					node.put("detno_temp", tasks.get(i).getID());
					String pretask = "";
					if (relations != null && !relations.isEmpty()) {
						int count = 0;
						for (Relation relation : relations) {
							for (Task task : tasks) {
								if (relation.getTargetTask().getID() == task.getID()) {
									count++;
								}
							}
							if (count < 1) {
								BaseUtil.showError("任务" + tasks.get(i).getName() + "的前置任务不存在当前模板，不允许导入！");
							}
							if (relation.getType().toString().equals("FS")) {
								pretask += relation.getTargetTask().getID() + ",";
							}
						}
					}

					if (pretask.length() > 1) {
						pretask = pretask.substring(0, pretask.lastIndexOf(","));
						node.put("pretaskdetno_temp", pretask);
					}
					if (tasks.get(i).getParentTask().getID() == 0) {
						node.put("parentId", 0);
					} else {
						node.put("parentId", subofMap.get(tasks.get(i).getID()));
					}
					node.put("PRJTYPECODE_", productTypeCode);
					node.put("tasktype_temp", "normal");
					sqls.add(SqlUtil.getInsertSqlByMap(node, "PROJECTTASK_TEMP"));
				}
			}
			baseDao.deleteByCondition("PROJECTTASK_TEMP", "PRJTYPECODE_='" + productTypeCode + "'");
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("导入失败！" + e.getMessage());
		}

		return true;
	}

	// 处理资源分配
	private void setResouceAssignments(Task task, Map<String, Object> taskInfo, Map<Integer, Map<String, Object>> resourceMap) {
		List<ResourceAssignment> resourceAssignments = task.getResourceAssignments();
		if (resourceAssignments.size() > 0) {
			int units = 0;
			String unit = "";
			String codes = "";
			String names = "";
			String Ids = "";
			int count = 0;
			for (int i = 1; i < resourceAssignments.size() + 1; i++) {
				ResourceAssignment assign = resourceAssignments.get(i - 1);
				units += assign.getUnits().intValue();
				unit += assign.getUnits().intValue() + ",";
				Map<String, Object> rs = resourceMap.get(assign.getResourceUniqueID());
				Object resourceid = rs.get("resourceid");
				if (resourceid != null && !"".equals(resourceid)) {
					count = baseDao.getCount("select count(1) from Employee WHERE EM_ID=" + resourceid);
					if (count < 1) {
						BaseUtil.showError("任务" + task.getName() + "的资源在人员资料中不存在，导入失败！");
					}
				}

				Ids += rs.get("resourceid") + ",";
				codes += rs.get("resourcecode") + ",";
				names += rs.get("resourcename") + ",";
			}

			if (units != 100)
				BaseUtil.showError("任务【" + task.getName() + "】资源分配之和不等于100%!");

			if (unit.length() > 1) {
				unit = unit.substring(0, unit.lastIndexOf(","));
				taskInfo.put("resourceunits_temp", unit);
			}
			if (Ids.length() > 1) {
				Ids = Ids.substring(0, Ids.lastIndexOf(","));
				taskInfo.put("resourceId_temp", Ids);
			}
			if (codes.length() > 1) {
				codes = codes.substring(0, codes.lastIndexOf(","));
				taskInfo.put("resourcecode_temp", codes);
			}
			if (names.length() > 1) {
				names = names.substring(0, names.lastIndexOf(","));
				taskInfo.put("resourcename_temp", names);
			}

			count = baseDao.getCount("select count(1) from Employee WHERE EM_CLASS ='离职' AND EM_ID IN (" + Ids + ")");
			if (count > 0) {
				BaseUtil.showError("任务" + task.getName() + "的资源在人员资料中不存在，导入失败！");
			}
		}
	}

	// 分析项目资源信息
	private void getResources(ProjectFile pf, Map<Integer, Map<String, Object>> resourceMap) {
		List<net.sf.mpxj.Resource> resources = pf.getAllResources();

		for (net.sf.mpxj.Resource resource : resources) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (resource.getName() != null) {
				if (resource.getCode() == null) {
					BaseUtil.showError("资源【" + resource.getName() + "】，未设置代码！");
				}
				map.put("resourcecode", resource.getCode());
				map.put("resourcename", resource.getName());
				try {
					int id = baseDao.getFieldValue("Employee", "em_id", "em_code ='" + resource.getCode() + "'", Integer.class);
					map.put("resourceid", id);
				} catch (Exception e) {
					BaseUtil.showError("资源" + resource.getName() + "在人员资料中不存在，导入失败！");
				}
				resourceMap.put(resource.getUniqueID(), map);
			}

		}
	}

	@Override
	public void updateTaskBookTeamplates(String productTypeCode, String gridStore) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : store) {
			int count = 0;
			String files = (String) map.get("docId_temp");
			if (files != null && !"".equals(files)) {
				String[] docs = files.split(",");
				for (int i = 0; i < docs.length; i++) {
					count = baseDao.getCount("select count(1) from PrjDoc_Temp WHERE PRJTYPECODE_='" + productTypeCode + "' AND ID_="
							+ docs[i]);
					if (count < 1) {
						BaseUtil.showError("任务" + map.get("name_temp") + "的文件在当前产品类型的文件清单中不存在，保存失败！");
					}
				}
			}
			Object[] parentData = new Object[5];
			parentData[0] = map.get("Id_temp");
			parentData[1] = map.get("taskclass_temp");
			parentData[2] = map.get("parentId");
			parentData[3] = map.get("name_temp");
			parentData[4] = map.get("detno_temp");
			for(Map<Object, Object> son : store) {
				int parentId = (int) son.get("parentId");
				Object taskclass =  son.get("taskclass_temp");
				if(parentData[0].equals(parentId)) {
					if(!parentData[1].equals(taskclass)) {
						BaseUtil.showError("请检查:<h2 style='display:inline;'>"+parentData[4]+"-"+parentData[3]+"</h2>的目录下任务分类是否统一！");
					}
				}
			}
		}
		try {
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(store, "PROJECTTASK_TEMP", "Id_temp");
			baseDao.execute(gridSql);
		} catch (Exception e) {
			BaseUtil.showError("保存失败" + e.getMessage());
		}
	}
	
	@Override
	public void deleteTaskBookTeamplate(String productTypeCode){
		baseDao.deleteByCondition("PROJECTTASK_TEMP", "PRJTYPECODE_='"+productTypeCode+"'");
	}
}
