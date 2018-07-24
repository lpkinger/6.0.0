package com.uas.erp.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;

@Repository
public class CancelProdInOutDao extends BaseDao {
	
	public Map<String, Object> getFormData(Form form, String condition) {
		String sql = form.getSql(condition);
		sql = sql.replaceAll("(?i)\\s+PRODINOUT(\\s)+", " PRODINOUT_DEL ").replaceAll("(?i)\\s+PRODINOUT\\.", " PRODINOUT_DEL.")
				.replaceAll("(?i),+PRODINOUT(\\s)+", ",PRODINOUT_DEL ").replaceAll("(?i),+PRODINOUT\\.", ",PRODINOUT_DEL.")
				.replaceAll("(?i)\\s+PRODIODETAIL(\\s)+", " PRODIODETAIL_DEL ").replaceAll("(?i)\\s+PRODIODETAIL\\.", " PRODIODETAIL_DEL.")
				.replaceAll("(?i),PRODIODETAIL(\\s)+", ",PRODIODETAIL_DEL ").replaceAll("(?i),PRODIODETAIL\\.", ",PRODIODETAIL_DEL.");
		
		List<Map<String, Object>> list = getJdbcTemplate(form.getFo_table()).queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		Map<String, Object> map = null;
		if (iter.hasNext()) {
			map = iter.next();
			for (FormDetail detail : form.getFormDetails()) {
				String field = detail.getFd_field();
				if (field.contains(" ")) {// field有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				String key = field.replaceAll("(?i)^PRODINOUT$", "PRODINOUT_DEL").replaceAll("(?i)^PRODINOUT\\.", " PRODINOUT_DEL.")
						.replaceAll("(?i)^PRODIODETAIL$", " PRODIODETAIL_DEL ").replaceAll("(?i)^PRODIODETAIL\\.", " PRODIODETAIL_DEL.");
				Object value = map.get(key);
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(key.toUpperCase());
				map.put(field, value);
			}
		}
		return map;
	}
	
	public List<Map<String, Object>> getDetailGridData(List<DetailGrid> detailGrids, String condition, Employee employee, Integer start,
			Integer end) {
		String caller = detailGrids.get(0).getDg_caller();
		Object[] objs = getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition,fo_detailgridorderby", "fo_caller='" + caller
				+ "'");
		Object table = detailGrids.get(0).getDg_table();
		if (objs != null) {// 优先用Form的配置
			if (objs[0] != null)
				table = objs[0];
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
			if (objs[2] != null && objs[2].toString().toLowerCase().indexOf("order by") > -1) {
				int index = condition.toLowerCase().indexOf("order by");
				if (index > -1) {
					condition = condition.substring(0, index);
				}
				condition += " " + objs[2];
			}
		}
		String sql = SqlUtil.getQuerySqlByDetailGrid(detailGrids, String.valueOf(table), condition, employee, start, end);
		sql = sql.replaceAll("(?i)\\s+PRODINOUT(\\s)+", " PRODINOUT_DEL ").replaceAll("(?i)\\s+PRODINOUT\\.", " PRODINOUT_DEL.")
				.replaceAll("(?i),+PRODINOUT(\\s)+", ",PRODINOUT_DEL ").replaceAll("(?i),+PRODINOUT\\.", ",PRODINOUT_DEL.")
				.replaceAll("(?i)\\s+PRODIODETAIL(\\s)+", " PRODIODETAIL_DEL ").replaceAll("(?i)\\s+PRODIODETAIL\\.", " PRODIODETAIL_DEL.")
				.replaceAll("(?i),PRODIODETAIL(\\s)+", ",PRODIODETAIL_DEL ").replaceAll("(?i),PRODIODETAIL\\.", ",PRODIODETAIL_DEL.");
		
		List<Map<String, Object>> list = getJdbcTemplate(detailGrids.get(0).getDg_table().split(" ")[0]).queryForList(sql);
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DetailGrid detail : detailGrids) {
				String field = detail.getDg_field();
				if (field.contains(" ")) {// column有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				String key = field.replaceAll("(?i)^PRODINOUT$", "PRODINOUT_DEL").replaceAll("(?i)^PRODINOUT\\.", " PRODINOUT_DEL.")
						.replaceAll("(?i)^PRODIODETAIL$", " PRODIODETAIL_DEL ").replaceAll("(?i)^PRODIODETAIL\\.", " PRODIODETAIL_DEL.");
				Object value = map.get(key);
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String type = detail.getDg_type();
					if (type != null && type.matches("^checkcolumn-?\\d{1}$")) {
						// 数据库里是number类型，在Grid里面作为checkcolumn时，需将number数据按配置转化成true和false
						if (value == null || "".equals(value))
							value = false;
						else
							value = Integer.parseInt(value.toString()) == Integer.parseInt(type.substring(11).toString());
					}
				}
				map.remove(key.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}
}
