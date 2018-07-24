package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.MulticolacSchemeService;

@Service
public class MulticolacSchemeServiceImpl implements MulticolacSchemeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveMulticolacScheme(String formStore, String param, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		check(store, grid,true);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		Object direction = baseDao.getFieldDataByCondition("CateGory", "ca_balancetype", "ca_code = '" + store.get("mas_cacode") + "'");
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MULTICOLACSCHEME", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		for (Map<Object, Object> map : grid) {
			map.put("masd_id", baseDao.getSeqId("MULTICOLACSCHEMEDET_SEQ"));
			map.put("masd_masid", store.get("mas_id"));
			if (!StringUtil.hasText(map.get("masd_direction"))) {
				map.put("masd_direction", direction);
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MULTICOLACSCHEMEDET");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "mas_id", store.get("mas_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@Transactional
	public void updateMulticolacScheme(String formStore, String param, String param2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = new ArrayList<String>();
		check(store, gstore,"true".equals(param2));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MULTICOLACSCHEME", "mas_id");
		baseDao.execute(formSql);
		Object direction = baseDao.getFieldDataByCondition("CateGory", "ca_balancetype", "ca_code = '" + store.get("mas_cacode") + "'");
		
		if ("true".equals(param2)) {
			for (Map<Object, Object> map : gstore) {
				map.put("masd_id", baseDao.getSeqId("MULTICOLACSCHEMEDET_SEQ"));
				if (!StringUtil.hasText(map.get("masd_direction"))) {
					map.put("masd_direction", direction);
				}
			}
			gridSql.add("DELETE FROM MULTICOLACSCHEMEDET WHERE MASD_MASID = " + store.get("mas_id"));
			gridSql.addAll(SqlUtil.getInsertSqlbyGridStore(gstore, "MULTICOLACSCHEMEDET"));
		} else {
			for (Map<Object, Object> map : gstore) {
				if (!StringUtil.hasText(map.get("masd_direction"))) {
					map.put("masd_direction", direction);
				}
			}
			gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "MULTICOLACSCHEMEDET", "masd_id");
		}

		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "mas_id", store.get("mas_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteMulticolacScheme(int mas_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mas_id });

		// 删除MULTICOLACSCHEME
		baseDao.deleteById("MULTICOLACSCHEME", "mas_id", mas_id);
		// 删除MULTICOLACSCHEMEDET
		baseDao.deleteById("MULTICOLACSCHEMEDET", "masd_masid", mas_id);
		// 记录操作
		baseDao.logger.delete(caller, "mas_id", mas_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mas_id });
	}

	@Override
	public void submitMulticolacScheme(int mas_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MULTICOLACSCHEME", "mas_statuscode", "mas_id=" + mas_id);
		StateAssert.submitOnlyEntering(status);
		String err = "";
		List<Object[]> masd = baseDao.getFieldsDatasByCondition("MULTICOLACSCHEMEDET", new String[]{"masd_direction","masd_detno"}, "masd_masid="+mas_id+" order by masd_detno");
		for (Object[] obj : masd) {
			if (!StringUtil.hasText(obj[0])) {
				err+="、"+obj[1];
			}
		}
		if (!"".equals(err)) {
			BaseUtil.showError("明细行序号:"+err.substring(1)+"的方向为空，不予许提交");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { mas_id });
		// 执行提交操作
		baseDao.submit("MULTICOLACSCHEME", "mas_id=" + mas_id, "mas_status", "mas_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mas_id", mas_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { mas_id });
	}

	@Override
	public void resSubmitMulticolacScheme(int mas_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MULTICOLACSCHEME", "mas_statuscode", "mas_id=" + mas_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, mas_id);
		// 执行反提交操作
		baseDao.resOperate("MULTICOLACSCHEME", "mas_id=" + mas_id, "mas_status", "mas_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mas_id", mas_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, mas_id);
	}

	@Override
	@Transactional
	public void auditMulticolacScheme(int mas_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { mas_id });
		SqlRowList rs = baseDao.queryForRowSet("select * from MulticolacScheme where mas_id=? ", mas_id);
		if (rs.next()) {
			StringBuffer sql = new StringBuffer(" ");
			if (rs.getGeneralInt("mas_assistant") != 0) {
				sql.append(" and ");
				sql.append(" asl_asstype=''" + rs.getObject("mas_assistanttype")
						+ "'' and als_asscode in (select distinct masd_assistant from MulticolacSchemeDet where masd_masid=" + mas_id + ")");
			}
			baseDao.execute("update MulticolacScheme set MAS_CONDITIONSQL='" + sql + "' where mas_id=" + mas_id);
		}
		// 执行审核操作
		baseDao.audit("MULTICOLACSCHEME", "mas_id=" + mas_id, "mas_status", "mas_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "mas_id", mas_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { mas_id });
	}

	@Override
	public void resAuditMulticolacScheme(int mas_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MULTICOLACSCHEME", "mas_statuscode", "mas_id=" + mas_id);
		StateAssert.resAuditOnlyAudit(status);

		baseDao.resAuditCheck("MULTICOLACSCHEME", mas_id);
		handlerService.beforeResAudit(caller, new Object[] { mas_id });

		// 执行反审核操作
		baseDao.resOperate("MULTICOLACSCHEME", "mas_id=" + mas_id, "mas_status", "mas_statuscode");

		// 记录操作
		baseDao.logger.resAudit(caller, "mas_id", mas_id);
		handlerService.afterResAudit(caller, new Object[] { mas_id });
	}

	// 自动编排逻辑
	@Override
	public Map<Object, Object> autoArrange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> result = new HashMap<Object, Object>();
		SqlRowList rs = null;
		boolean over = false;
		boolean bool = baseDao.checkIf("Category", "abs(ca_isleaf)=1 and ca_code='" + store.get("mas_cacode") + "'");
		if (bool && "1".equals(store.get("mas_assistant"))) {
			if (store.get("mas_assistanttype") != null && !"".equals(store.get("mas_assistanttype"))) {
				Object[] caasstype = baseDao.getFieldsDataByCondition("Category", "ca_asstype,ca_balancetype",
						"ca_code='" + store.get("mas_cacode") + "'");
				if (caasstype[1]==null||(!"0".equals(caasstype[1].toString())&&!"1".equals(caasstype[1].toString()))) {
					BaseUtil.showError("余额方向非借非贷，不能成为方案！");
				}
				if (caasstype[0] != null && !"".equals(caasstype[0])) {
					String[] asstypes = caasstype[0].toString().split("#");
					boolean exits = false;
					for (String asstype : asstypes) {
						if (asstype.equals(store.get("mas_assistanttype"))) {
							exits = true;
							break;
						}
					}
					if (!exits) {
						BaseUtil.showError("辅助核算类型在当前科目辅助核算中不存在！");
					}
					Object ak[] = baseDao.getFieldsDataByCondition("AssKind", "ak_dbfind,ak_asscode,ak_assname",
							"ak_code='" + store.get("mas_assistanttype") + "'");

					if (ak != null) {
						Object[] table = baseDao.getFieldsDataByCondition("dbfindset", "ds_tablename,ds_fixedcondition,ds_orderby",
								"ds_caller='" + ak[0] + "'");
						if (table != null) {

							String condition = "";
							String sql = "select " + ak[1] + "," + ak[2] + " from " + table[0];
							if (table[1] != null) {
								sql += " where " + table[1];
								condition += table[1];
							}else {
								condition = "1=1";
							}
							if (table[2] != null) {
								condition += table[2];
								sql+=" "+table[2];
							}
							int count = baseDao.getCountByCondition(table[0].toString(), condition);
							if (count > 100) {
								sql = "select * from (" + sql + ") where rownum<=100";
								over = true;
							}
							rs = baseDao.queryForRowSet(sql);
						} else {
							BaseUtil.showError("辅助核算" + store.get("mas_assistanttype") + "未配置dbfind！");
						}
						while (rs.next()) {
							Map<Object, Object> map = new HashMap<Object, Object>();
							if (store.get("mas_id") != null && !"".equals(store.get("mas_id"))) {
								map.put("masd_masid", store.get("mas_id"));
							}
							map.put("masd_detno", rs.getCurrentIndex() + 1);
							map.put("masd_direction", caasstype[1]);
							map.put("masd_assistant", rs.getString(ak[1].toString()));
							map.put("masd_colname", rs.getString(ak[2].toString()));
							list.add(map);
						}
					}
				}
			}
		} else if (!bool) {
			if ("1".equals(store.get("mas_setbylevel"))) {
				Object calevel = baseDao.getFieldDataByCondition("Category", "ca_level", "ca_code='" + store.get("mas_cacode") + "'");
				if (Integer.parseInt(store.get("mas_level").toString()) <= Integer.parseInt(calevel.toString())) {
					BaseUtil.showError("级别必须大于主表科目级别！");
				} else {
					findCategoryByLevel(store.get("mas_cacode").toString(), Integer.parseInt(store.get("mas_level").toString()), list,
							over, store.get("mas_id"));
				}
			} else {
				findCategoryIsLeaf(store.get("mas_cacode").toString(), list, over, store.get("mas_id"));
			}
		}

		// 自动编排时如果单据属于新增的，自动保存单据
		if (store.get("mas_id") == null || "".equals(store.get("mas_id"))) {
			Map<String, Object> res= saveMulticolacScheme(store, list, caller);
			result.putAll(res);
		} else {
			result.put("over", over);
			result.put("data", list);
		}
		return result;
	}

	// 按级别查找明细科目
	private void findCategoryByLevel(String cacode, int level, List<Map<Object, Object>> list, boolean over, Object id) {
		if (!over) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select ca_code,ca_name,ca_level,ca_balancetype from Category where nvl(ca_statuscode,' ')='AUDITED' AND ca_pcode = ? order by ca_code",
					cacode);
			while (rs.next()) {
				if (rs.getInt("ca_level") == level) {
					if (list.size() >= 100) {
						over = true;
						break;
					}
					Map<Object, Object> map = new HashMap<Object, Object>();
					if (id != null && !"".equals(id)) {
						map.put("masd_masid", id);
					}
					map.put("masd_detno", list.size() + 1);
					if (rs.getInt("ca_balancetype")!=0&&rs.getInt("ca_balancetype")!=1) {
						map.put("masd_direction", null);
					}else {
						map.put("masd_direction", rs.getInt("ca_balancetype"));
					}
					
					map.put("masd_cacode", rs.getString("ca_code"));
					map.put("masd_colname", rs.getString("ca_name"));
					list.add(map);
				} else if (rs.getInt("ca_level") < level) {
					findCategoryByLevel(rs.getString("ca_code"), level, list, over, id);
				}
			}
		}
	}

	// 查找末级科目
	private void findCategoryIsLeaf(String cacode, List<Map<Object, Object>> list, boolean over, Object id) {
		if (!over) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select ca_code,ca_name,abs(ca_isleaf) isleaf,ca_balancetype from Category where nvl(ca_statuscode,' ')='AUDITED' AND ca_pcode = ? order by ca_code",
							cacode);
			while (rs.next()) {
				if (rs.getInt("isleaf") == 1) {
					if (list.size() >= 100) {
						over = true;
						break;
					}
					Map<Object, Object> map = new HashMap<Object, Object>();
					if (id != null && !"".equals(id)) {
						map.put("masd_masid", id);
					}
					map.put("masd_detno", list.size() + 1);
					if (rs.getInt("ca_balancetype")!=0&&rs.getInt("ca_balancetype")!=1) {
						map.put("masd_direction", null);
					}else{
						map.put("masd_direction", rs.getInt("ca_balancetype"));
					}
					map.put("masd_cacode", rs.getString("ca_code"));
					map.put("masd_colname", rs.getString("ca_name"));
					list.add(map);
				} else {
					findCategoryIsLeaf(rs.getString("ca_code"), list, over, id);
				}
			}
		}
	}

	private Map<String, Object> saveMulticolacScheme(Map<Object, Object> store, List<Map<Object, Object>> grid, String caller) {

		Map<String, Object> result = new HashMap<String, Object>();
		Integer id = baseDao.getSeqId("MULTICOLACSCHEME_SEQ");
		store.put("mas_id", id);
		boolean bool = baseDao.checkByCondition("MulticolacScheme", "mas_name='"+store.get("mas_name")+"'");
		if (!bool) {
			BaseUtil.showError("多栏账方案名称重复！");
		}
		String str ="";
		for (int i=0;i<grid.size()-1;i++) {
			for (int j=i+1;j<grid.size();j++) {
				if(grid.get(i).get("masd_colname").toString().equals(grid.get(j).get("masd_colname").toString())){
					str += "、序号"+grid.get(i).get("masd_detno")+"和序号"+grid.get(j).get("masd_detno");
				}
			}
		}
		if (!"".equals(str)) {
			result.put("tip", "明细行"+str.substring(1)+"的栏目名称重复！");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MULTICOLACSCHEME", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		for (Map<Object, Object> map : grid) {
			map.put("masd_id", baseDao.getSeqId("MULTICOLACSCHEMEDET_SEQ"));
			map.put("masd_masid", id);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MULTICOLACSCHEMEDET");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.others("自动编排", "编排成功", caller, "mas_id", store.get("mas_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		result.put("id", id);
		return result;
	}

	private void check(Map<Object, Object> store, List<Map<Object, Object>> grid,boolean autoArrange) {
		
		List<Object> mas_names = baseDao.getFieldDatasByCondition("MulticolacScheme", "mas_name", "mas_id<>"+store.get("mas_id"));
		for (Object mas_name : mas_names) {
			if (store.get("mas_name").toString().equals(mas_name.toString())) {
				BaseUtil.showError("多栏账方案名称重复！");
			}
		}
		
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
		boolean isEqual = !baseDao.checkByCondition("MultiColAcScheme", "mas_assistant="+store.get("mas_assistant")+" and mas_id="+store.get("mas_id"));
		if (isEqual&&!autoArrange) {
			list1 = baseDao.queryForList("select * from MultiColAcSchemeDet where masd_masid = "+store.get("mas_id"));
		}
		boolean bool = baseDao.checkIf("Category", "abs(ca_isleaf)=1 and ca_code='" + store.get("mas_cacode") + "'");
		
		if (bool && "1".equals(store.get("mas_assistant"))) {
			Object direction = baseDao.getFieldDataByCondition("CateGory", "ca_balancetype", "ca_code = '" + store.get("mas_cacode")+ "'");
			if (direction==null||(!"0".equals(direction.toString())&&!"1".equals(direction.toString()))) {
				BaseUtil.showError("余额方向非借非贷，不能成为方案！");
			}
			if (store.get("mas_assistanttype") != null && !"".equals(store.get("mas_assistanttype"))) {
				Object ak[] = baseDao.getFieldsDataByCondition("AssKind", "ak_dbfind,ak_asscode,ak_assname",
						"ak_code='" + store.get("mas_assistanttype") + "'");

				if (ak != null) {
					Object[] table = baseDao.getFieldsDataByCondition("dbfindset", "ds_tablename,ds_fixedcondition,ds_orderby","ds_caller='" + ak[0] + "'");
					if (table != null) {
						String sql = "select " + ak[1] + "," + ak[2] + " from " + table[0];
						if (table[1] != null) {
							sql += " where " +  table[1];
						}
						if (table[2] != null) {
							sql += " " + table[2];
						}
						list = baseDao.queryForList(sql);
					} else {
						BaseUtil.showError("辅助核算" + store.get("mas_assistanttype") + "未配置dbfind！");
					}
				}
				
				for (Map<Object, Object> map : grid) {
					Object assistant = map.get("masd_assistant");
					Object detno = map.get("masd_detno");
					for (int i=0;i<list1.size();i++) {
						Object detno1 = list1.get(i).get("masd_detno");
						if (Integer.parseInt(detno.toString())!=Integer.parseInt(detno1.toString())&&list1.get(i).get("masd_assistant").equals(assistant.toString())) {
							BaseUtil.showError("序号"+detno1+"和序号"+detno+"的核算编号重复！");
						}
					}
					
					boolean exits = false;
					for (int i=0;i<list.size();i++) {
						if (list.get(i).get(ak[1].toString()).toString().equals(assistant.toString())) {
							exits = true;
							break;
						}
					}
					if (!exits) {
						BaseUtil.showError("辅助核算编号不存在，行" + map.get("masd_detno") + "！");
					}
				}
			}
		} else if (!bool) {
			for (Map<Object, Object> map : grid) {
				boolean check = baseDao.checkIf("Category",
						"ca_code ='" + map.get("masd_cacode") + "'");
				if (!check) {
					BaseUtil.showError("科目编号不存在，行" + map.get("masd_detno") + "!");
				}
				Object direction = baseDao.getFieldDataByCondition("CateGory", "ca_balancetype", "ca_code = '" + map.get("masd_cacode")+ "'");
				if (direction==null||(!"0".equals(direction.toString())&&!"1".equals(direction.toString()))) {
					BaseUtil.showError("行"+map.get("masd_detno")+",余额方向非借非贷，不能成为方案！");
				}
				Object detno = map.get("masd_detno");
				for (int i=0;i<list1.size();i++) {
					Object detno1 = list1.get(i).get("masd_detno");
					
					if (Integer.parseInt(detno.toString())!=Integer.parseInt(detno1.toString())&&list1.get(i).get("masd_cacode").equals(map.get("masd_cacode").toString())) {
						BaseUtil.showError("序号"+detno1+"和序号"+detno+"的科目编号重复！");
					}
				}
			}
		}
		
		for (int i=0;i<grid.size()-1;i++) {
			for (int j=i+1;j<grid.size();j++) {
				if (bool && "1".equals(store.get("mas_assistant"))) {
					if (grid.get(i).get("masd_assistant").equals(grid.get(j).get("masd_assistant"))) {
						BaseUtil.showError("序号"+grid.get(i).get("masd_detno")+"和序号"+grid.get(j).get("masd_detno")+"的核算编号重复！");
					}
				}else {
					if (grid.get(i).get("masd_cacode").equals(grid.get(j).get("masd_cacode"))) {
						BaseUtil.showError("序号"+grid.get(i).get("masd_detno")+"和序号"+grid.get(j).get("masd_detno")+"的科目编号重复！");
					}
				}
			}
		}		
		
		if (grid.size() > 100) {
			BaseUtil.showError("科目和辅助核算最多100条！");
		}
	}
}
