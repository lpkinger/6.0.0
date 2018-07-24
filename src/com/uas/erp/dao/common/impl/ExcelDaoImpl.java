package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ExcelDao;
import com.uas.erp.model.DataStoreDetail;
import com.uas.erp.model.ExcelFx;
import com.uas.erp.model.ExcelTemplate;
import com.uas.erp.model.ExcelTemplateDetail;

@Repository("excelDao")
public class ExcelDaoImpl extends BaseDao implements ExcelDao {
	@Override
	public ExcelTemplate getExcelTemplateById(int id) {
		try {
			ExcelTemplate template = getJdbcTemplate().queryForObject("select *  from ExcelTemplate where et_id=?", new BeanPropertyRowMapper<ExcelTemplate>(ExcelTemplate.class), id);
			return template;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template) {
		try {
			List<ExcelTemplateDetail> excelTemplateDetails = getJdbcTemplate(template.getEt_tablename()).query("select * from ExcelTemplateDetail where etd_mainid=?",
					new BeanPropertyRowMapper<ExcelTemplateDetail>(ExcelTemplateDetail.class), template.getEt_id());
			return excelTemplateDetails;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template, String sheetname) {
		try {
			List<ExcelTemplateDetail> excelTemplateDetails = getJdbcTemplate(template.getEt_tablename()).query(
					"select * from ExcelTemplateDetail where etd_mainid=? And etd_sheetname=?  order by etd_rowindex ", new BeanPropertyRowMapper<ExcelTemplateDetail>(ExcelTemplateDetail.class),
					template.getEt_id(), sheetname);
			return excelTemplateDetails;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template, String sheetname, int i) {
		try {
			List<ExcelTemplateDetail> excelTemplateDetails = getJdbcTemplate(template.getEt_tablename()).query(
					"select * from ExcelTemplateDetail where etd_mainid=? And etd_sheetname=? AND etd_celltype=? order by etd_rowindex",
					new BeanPropertyRowMapper<ExcelTemplateDetail>(ExcelTemplateDetail.class), template.getEt_id(), sheetname, i);
			return excelTemplateDetails;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SqlRowList getSqlRowListByDetails(String tablename, String condition, List<ExcelTemplateDetail> Details) {
		// 处理condition
		String BaseCondition = "";
		StringBuffer sb1 = new StringBuffer();
		if (condition != null && !condition.equals("null") && !condition.equals("")) {
			String[] arr = condition.split("#%");
			for (int i = 0; i < arr.length; i++) {
				String[] arr2 = arr[i].split(";");
				if (arr2[1].equals("like")) {
					sb1.append(arr2[0] + " like '%" + arr2[2] + "'% ");
				} else {
					sb1.append(arr2[0] + " " + arr2[1] + "'" + arr2[2] + "'");
				}
				if (i < arr.length - 1) {
					sb1.append(" AND ");
				}
			}
		}
		BaseCondition = sb1.toString();
		String FindSql = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select  ");
		for (ExcelTemplateDetail detail : Details) {
			sb.append(detail.getField() + ",");
		}

		FindSql = (BaseCondition == null || BaseCondition.equals("")) ? sb.substring(0, sb.lastIndexOf(",")) + " From " + tablename : sb.substring(0, sb.lastIndexOf(",")) + " From " + tablename
				+ " where " + BaseCondition;
		return queryForRowSet(FindSql);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getSheets(int id) {
		SqlRowList rl = queryForRowSet("Select etd_sheetname from ExcelTemplateDetail where etd_mainid=" + id + " group by etd_sheetname");
		List<String> sheets = new ArrayList<String>();
		while (rl.next()) {
			sheets.add(rl.getString(1));
		}
		return sheets;
	}

	@Override
	public List<DataStoreDetail> getDataStoreDetails(String condition) {
		try {
			List<DataStoreDetail> Details = getJdbcTemplate().query("select * from DataStoreDetail where " + condition, new BeanPropertyRowMapper<DataStoreDetail>(DataStoreDetail.class));
			return Details;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExcelTemplateDetail> getExcelTemplteDetails(String condition) {
		try {
			List<ExcelTemplateDetail> excelTemplateDetails = getJdbcTemplate().query("select * from ExcelTemplateDetail where " + condition,
					new BeanPropertyRowMapper<ExcelTemplateDetail>(ExcelTemplateDetail.class));
			return excelTemplateDetails;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExcelFx> getExcelFxs(String condition) {
		try {
			List<ExcelFx> excelfxs = getJdbcTemplate().query("select * from ExcelFx where " + condition, new BeanPropertyRowMapper<ExcelFx>(ExcelFx.class));
			return excelfxs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getExcelFxData(ExcelTemplateDetail detail, String cellCondition, String BaseMonth) {
		String beginmonthy = null;
		if (BaseMonth.length() > 5) {
			beginmonthy = BaseMonth.substring(0, 4) + "01";
		}
		String thisMonth = BaseMonth;
		String[] condition = cellCondition.split("#&");
		String Fxcondition = "";
		String FindSql = "";
		ExcelFx excelfx = null;
		Object value = null;
		// 手动传条件
		if (condition[0] != null && !condition[0].equals("")) {
			for (int i = 0; i < condition.length; i++) {
				String label = condition[i].split(":")[0];
				String args = condition[i].split(":")[1];
				int row = Integer.parseInt(label.substring(label.indexOf("(") + 1, label.indexOf(",")));
				int col = Integer.parseInt(label.substring(label.lastIndexOf(",") + 1, label.lastIndexOf(")")));
				if (detail.checkCondition(row, col)) {
					// 说明函数匹配 找到函数
					Fxcondition = "ef_datastoreid=" + getExcelTemplateById(detail.getEtd_mainid()).getEt_dsid() + " AND ef_name='" + detail.getFxname() + "'";
					excelfx = getExcelFx(Fxcondition);
					FindSql = excelfx.getEf_sql();
					String[] argarr = args.split(",");
					if (argarr.length > 0) {
						for (int j = 0; j < argarr.length; j++) {
							FindSql = FindSql.replaceAll(argarr[j].split(";")[0], "'" + argarr[j].split(";")[1] + "'");
						}
					}
				}
			}
		} else {
			Fxcondition = "ef_datastoreid=" + getExcelTemplateById(detail.getEtd_mainid()).getEt_dsid() + " AND ef_name='" + detail.getFxname() + "'";
			excelfx = getExcelFx(Fxcondition);
			FindSql = excelfx.getEf_sql();
		}
		// 模板函数可能自带了参数 不需要条件输入
		// 模板函数中的参数
		String templateargs = detail.getFxArgs();
		// 定义函数中的参数
		String fxargs = excelfx.getEf_args();
		// 如果参数名相同的话 说明定义模板的时候没有修改参数就不更改 否则 不同直接替换
		if (excelfx.getEf_argnum() > 0) {
			String[] fxarg = fxargs.split(",");
			String[] templatearg = templateargs.split(",");
			for (int i = 0; i < fxarg.length; i++) {
				String argname = fxarg[i].split(";")[0];
				if (!argname.equals(templatearg[i])) {
					FindSql = FindSql.replaceAll(argname, " '" + templatearg[i] + "' ");
				}
			}
		}
		FindSql = FindSql.replaceAll("beginmonthy", "'" + beginmonthy + "'").replaceAll("thismonthy", "'" + thisMonth + "'").replaceAll("@@", "'");
		if (!FindSql.equals("")) {
			SqlRowList sl = queryForRowSet(FindSql);
			while (sl.next()) {
				value = sl.getObject(1);
				if (value == null) {
					value = "";
				}
			}
		}

		return value;
	}

	@Override
	public ExcelFx getExcelFx(String condition) {
		try {
			ExcelFx excelfx = getJdbcTemplate().queryForObject("select * from ExcelFx where " + condition, new BeanPropertyRowMapper<ExcelFx>(ExcelFx.class));
			return excelfx;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
