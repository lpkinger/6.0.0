package com.uas.erp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.common.TransferDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.Transfer;

@Component
public class TransferRepository {
	private final static int maxSize = 100;
	@Autowired
	private TransferDao transferDao;

	@Autowired
	private BaseDao baseDao;

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param fromKeyValue
	 *            来源数据ID
	 * @return
	 */
	public Key transfer(String caller, Object fromKeyValue) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		if (transfer != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(transfer.getTr_fromrowkey(), fromKeyValue);
			int id = baseDao.getSeqId(transfer.getTr_totable() + "_SEQ");
			String code = null;
			if (StringUtil.hasText(transfer.getTr_tocodekey()) && StringUtil.hasText(transfer.getTr_tocodecaller()))
				code = baseDao.sGetMaxNumber(transfer.getTr_tocodecaller(), 2);
			Key key = new Key(id, code);
			baseDao.execute(getTransferSql(transfer, params, key));
			return key;
		}
		return null;
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param params
	 *            额外参数
	 * @param fromKeyValue
	 *            来源数据ID
	 * @return
	 */
	public Key transfer(String caller, Map<String, Object> params, Object fromKeyValue) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		if (transfer != null) {
			if (params == null)
				params = new HashMap<String, Object>();
			params.put(transfer.getTr_fromrowkey(), fromKeyValue);
			int id = baseDao.getSeqId(transfer.getTr_totable() + "_SEQ");
			String code = null;
			if (StringUtil.hasText(transfer.getTr_tocodekey()) && StringUtil.hasText(transfer.getTr_tocodecaller()))
				code = baseDao.sGetMaxNumber(transfer.getTr_tocodecaller(), 2);
			Key key = new Key(id, code);
			baseDao.execute(getTransferSql(transfer, params, key));
			return key;
		}
		return null;
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param fromKeyValue
	 *            来源数据ID
	 * @param key
	 *            id、code参数
	 * @return
	 */
	public void transfer(String caller, Object fromKeyValue, Key key) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		if (transfer != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(transfer.getTr_fromrowkey(), fromKeyValue);
			params.put(transfer.getTr_torowkey(), key.getId());
			if (StringUtil.hasText(transfer.getTr_tocodekey())) {
				params.put(transfer.getTr_tocodekey(), key.getCode());
			}
			baseDao.execute(getTransferSql(caller, params));
		}
	}

	/**
	 * 转单：单行数据、明细记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param fromKeyValue
	 *            来源数据ID
	 * @param key
	 *            id、code参数
	 * @return
	 */
	public void transferDetail(String caller, Object fromKeyValue, Key key) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "DETAIL");
		if (transfer != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(transfer.getTr_fromdockey(), fromKeyValue);
			params.put(transfer.getTr_torowforkey(), key.getId());
			if (StringUtil.hasText(transfer.getTr_tocodekey())) {
				params.put(transfer.getTr_tocodekey(), key.getCode());
			}
			baseDao.execute(getTransferSql(transfer, params));
			StringBuffer sql = new StringBuffer();
			String docField = transfer.getTr_fromdockey();
			String rowNum = transfer.getTr_fromrownum();
			String pageCaller = transfer.getTr_pagecaller();
			if (pageCaller == null)
				pageCaller = transfer.getTr_caller();
			if (docField == null)
				docField = transfer.getTr_fromrowkey();
			String rowYQ = transfer.getTr_fromrowyqty();
			// 改已转数
			if (StringUtil.hasText(rowYQ) && StringUtil.hasText(transfer.getTr_fromrowqty()))
				sql.append("UPDATE ").append(transfer.getTr_fromtabledet()).append(" SET ").append(rowYQ).append("=nvl(").append(rowYQ)
						.append(",0)+").append(transfer.getTr_fromrowqty()).append(" WHERE ").append(transfer.getTr_fromrowkey())
						.append(" in (select ").append(transfer.getTr_fromrowkey()).append(" from ").append(transfer.getTr_fromtablesql())
						.append(" where ").append(docField).append("=").append(fromKeyValue).append(")");
			// 日志
			if (StringUtil.hasText(rowNum)) {
				if (sql.length() > 0)
					sql.append(";");
				sql.append("INSERT INTO messagelog(ml_date,ml_man,ml_content,ml_result,ml_search) select sysdate,'")
						.append(SystemSession.getUser().getEm_name()).append("','").append(transfer.getTr_title()).append("','行'||")
						.append(transfer.getTr_fromrownum()).append(",'").append(pageCaller).append("|").append(docField).append("=")
						.append(fromKeyValue).append("' from ").append(transfer.getTr_fromtablesql()).append(" where ").append(docField)
						.append("=").append(fromKeyValue).append(";");
			}
			baseDao.execute("begin " + sql.toString() + " commit;end;");
		}
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param params
	 *            额外参数
	 * @return
	 */
	public void transfer(String caller, Map<String, Object> params) {
		baseDao.execute(getTransferSql(caller, params));
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param params
	 *            额外参数
	 * @return
	 */
	public String getTransferSql(String caller, Map<String, Object> params) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		return getTransferSql(transfer, params);
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param transfer
	 *            转单配置
	 * @param params
	 *            额外参数
	 * @return
	 */
	public String getTransferSql(Transfer transfer, Map<String, Object> params) {
		Employee employee = SystemSession.getUser();
		StringBuffer sqlInsert = new StringBuffer("INSERT INTO ");
		sqlInsert.append(transfer.getTr_totable()).append("(");
		StringBuffer sqlValues = new StringBuffer(" SELECT ");
		int i = 0;
		for (Transfer.Detail detail : transfer.getDetails()) {
			if (i > 0) {
				sqlInsert.append(",");
				sqlValues.append(",");
			} else
				i++;
			sqlInsert.append(detail.getTd_tofield());
			String fromTab = detail.getTd_fromtable();
			String fromField = detail.getTd_fromfield();
			if (StringUtil.hasText(fromTab)) {
				if ("@user".equals(fromTab)) {
					Object userInfo = "";
					if ("em_id".equals(fromField)) {
						userInfo = employee.getEm_id();
					} else if ("em_code".equals(fromField)) {
						userInfo = employee.getEm_code();
					} else if ("em_name".equals(fromField)) {
						userInfo = employee.getEm_name();
					}
					sqlValues.append("'").append(userInfo).append("'");
				} else if ("@param".equals(fromTab)) {
					if (params.containsKey(detail.getTd_fromfield()))
						sqlValues.append("'").append(params.get(detail.getTd_fromfield())).append("'");
					else
						sqlValues.append("null");
				} else
					sqlValues.append(fromTab).append(".").append(fromField);
			} else
				sqlValues.append(fromField);
		}
		// 新数据主键
		String toKey = transfer.getTr_torowkey();
		if (params.containsKey(toKey)) {
			sqlInsert.append(",").append(toKey);
			sqlValues.append(",").append(params.get(toKey));
		}
		if ("DETAIL".equals(transfer.getTr_mode())) {
			String toForKey = transfer.getTr_torowforkey();
			if (params.containsKey(toForKey)) {
				sqlInsert.append(",").append(toForKey);
				sqlValues.append(",").append(params.get(toForKey));
			}
			if (!params.containsKey(toKey)) {
				sqlInsert.append(",").append(toKey);
				sqlValues.append(",").append(transfer.getTr_totable()).append("_SEQ.NEXTVAL");
			}
			String toRowNum = transfer.getTr_torownum();
			if (toRowNum != null) {
				Object detno = "rownum";
				if (params.containsKey(toRowNum)) {
					detno = params.get(toRowNum);
				}
				sqlInsert.append(",").append(toRowNum);
				sqlValues.append(",").append(detno);
			}
		}
		// 新数据编号
		String toCode = transfer.getTr_tocodekey();
		if (StringUtil.hasText(toCode)) {
			sqlInsert.append(",").append(toCode);
			Object code = params.get(toCode);
			sqlValues.append(",'").append(code).append("'");
		}
		sqlInsert.append(")");
		String fromKey = transfer.getTr_fromdockey();
		if (!StringUtil.hasText(fromKey)) {
			fromKey = transfer.getTr_fromrowkey();
		}
		sqlValues.append(" FROM ").append(transfer.getTr_fromtablesql()).append(" WHERE ").append(fromKey).append("='")
				.append(params.get(fromKey)).append("'");
		return sqlInsert.append(sqlValues).toString();
	}

	/**
	 * 转单：单行数据、主记录
	 * 
	 * @param transfer
	 *            转单配置
	 * @param params
	 *            额外参数
	 * @param key
	 *            ID和CODE
	 * @return
	 */
	public String getTransferSql(Transfer transfer, Map<String, Object> params, Key key) {
		Employee employee = SystemSession.getUser();
		StringBuffer sqlInsert = new StringBuffer("INSERT INTO ");
		sqlInsert.append(transfer.getTr_totable()).append("(");
		StringBuffer sqlValues = new StringBuffer(" SELECT ");
		int i = 0;
		for (Transfer.Detail detail : transfer.getDetails()) {
			if (i > 0) {
				sqlInsert.append(",");
				sqlValues.append(",");
			} else
				i++;
			sqlInsert.append(detail.getTd_tofield());
			String fromTab = detail.getTd_fromtable();
			String fromField = detail.getTd_fromfield();
			if (StringUtil.hasText(fromTab)) {
				if ("@user".equals(fromTab)) {
					Object userInfo = "";
					if ("em_id".equals(fromField)) {
						userInfo = employee.getEm_id();
					} else if ("em_code".equals(fromField)) {
						userInfo = employee.getEm_code();
					} else if ("em_name".equals(fromField)) {
						userInfo = employee.getEm_name();
					} else if ("em_departmentcode".equals(fromField)) { // 为主动报价单转价格单添加部门名称
						userInfo = employee.getEm_departmentcode();
					} else if ("em_departmentname".equals(fromField)) {
						userInfo = employee.getEm_depart();
					}
					sqlValues.append("'").append(userInfo).append("'");
				} else if ("@param".equals(fromTab)) {
					sqlValues.append("'").append(params.get(detail.getTd_fromfield())).append("'");
				} else
					sqlValues.append(fromTab).append(".").append(fromField);
			} else
				sqlValues.append(fromField);
		}
		// 新数据主键
		String toKey = transfer.getTr_torowkey();
		if (toKey != null && (params.containsKey(toKey) || key != null)) {
			sqlInsert.append(",").append(toKey);
			Object toKeyValue = params.get(toKey);
			if (key != null)
				toKeyValue = key.getId();
			sqlValues.append(",").append(toKeyValue);
		}
		if ("DETAIL".equals(transfer.getTr_mode())) {
			String toForKey = transfer.getTr_torowforkey();
			if (params.containsKey(toForKey)) {
				sqlInsert.append(",").append(toForKey);
				sqlValues.append(",").append(params.get(toForKey));
			}
			if (!params.containsKey(toKey)) {
				sqlInsert.append(",").append(toKey);
				sqlValues.append(",").append(transfer.getTr_totable()).append("_SEQ.NEXTVAL");
			}
			String toRowNum = transfer.getTr_torownum();
			if (toRowNum != null) {
				Object detno = 1;
				if (params.containsKey(toRowNum)) {
					detno = params.get(toRowNum);
				}
				sqlInsert.append(",").append(toRowNum);
				sqlValues.append(",").append(detno);
			}
		}
		// 新数据编号
		String toCode = transfer.getTr_tocodekey();
		if (StringUtil.hasText(toCode)) {
			sqlInsert.append(",").append(toCode);
			Object code = params.get(toCode);
			if (key != null && key.getCode() != null)
				code = key.getCode();
			sqlValues.append(",'").append(code).append("'");
		}
		sqlInsert.append(")");
		String fromKey = transfer.getTr_fromrowkey();
		if (!StringUtil.hasText(fromKey)) {
			fromKey = transfer.getTr_fromdockey();
		}
		sqlValues.append(" FROM ").append(transfer.getTr_fromtablesql()).append(" WHERE ").append(fromKey).append("='")
				.append(params.get(fromKey)).append("'");
		return sqlInsert.append(sqlValues).toString();
	}

	/**
	 * 转单：更新单行数据、主记录
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param fromKeyValue
	 *            来源数据ID
	 * @param toKeyValue
	 *            新数据ID
	 * @return
	 */
	public void update(String caller, Object fromKeyValue, Object toKeyValue) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		if (transfer != null)
			baseDao.execute(getUpdateSql(transfer, fromKeyValue, toKeyValue));
	}

	public String getUpdateSql(Transfer transfer, Object fromKeyVal, Object toKeyVal) {
		StringBuffer sqlUpdate = new StringBuffer("UPDATE ");
		sqlUpdate.append(transfer.getTr_totable()).append(" SET (");
		StringBuffer sqlSelect = new StringBuffer(" SELECT ");
		int i = 0;
		for (Transfer.Detail detail : transfer.getDetails()) {
			String fromTab = detail.getTd_fromtable();
			String fromField = detail.getTd_fromfield();
			if (StringUtil.hasText(fromTab) && !"@user".equals(fromTab) && !"@param".equals(fromTab)) {
				if (i > 0) {
					sqlUpdate.append(",");
					sqlSelect.append(",");
				} else
					i++;
				sqlUpdate.append(detail.getTd_tofield());
				sqlSelect.append(fromTab).append(".").append(fromField);
			}
		}
		sqlUpdate.append(")");
		String fromKey = transfer.getTr_fromrowkey();
		if (!StringUtil.hasText(fromKey)) {
			fromKey = transfer.getTr_fromdockey();
		}
		sqlSelect.append(" FROM ").append(transfer.getTr_fromtablesql()).append(" WHERE ").append(fromKey).append("='").append(fromKeyVal)
				.append("'");
		sqlUpdate.append(" = (").append(sqlSelect).append(") WHERE ").append(transfer.getTr_torowkey()).append("='").append(toKeyVal)
				.append("'");
		return sqlUpdate.toString();
	}

	/**
	 * 转单：多行数据、明细<br>
	 * 自动记录日志<br>
	 * 自动修改已转数
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param dataList
	 *            待转明细数据
	 * @param params
	 *            额外参数
	 * @return
	 */
	public void transfer(String caller, List<Map<Object, Object>> dataList, Map<String, Object> params) {
		baseDao.execute(getTransferSql(caller, dataList, params));
	}

	/**
	 * 转单：多行数据、明细<br>
	 * 自动记录日志<br>
	 * 自动修改已转数
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param dataList
	 *            待转明细数据
	 * @param key
	 *            id、code参数
	 * @return
	 */
	public void transfer(String caller, List<Map<Object, Object>> dataList, Key key) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "DETAIL");
		if (transfer != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			if (StringUtil.hasText(transfer.getTr_torowforkey())) {
				params.put(transfer.getTr_torowforkey(), key.getId());
			}
			if (StringUtil.hasText(transfer.getTr_tocodekey())) {
				params.put(transfer.getTr_tocodekey(), key.getCode());
			}
			if (dataList.size() > maxSize) {
				int digit = dataList.size() % maxSize, page = 0;
				for (page = 0; page < dataList.size() / maxSize; page++) {
					long t1 = System.currentTimeMillis();
					String sql = getTransferSql(caller, dataList.subList(page * maxSize, (page + 1) * maxSize), params);
					long t2 = System.currentTimeMillis();
					System.out.println("generate sql " + (t2 - t1));
					baseDao.execute(sql);
					System.out.println("execute sql " + (System.currentTimeMillis() - t2));
				}
				if (digit > 0)
					baseDao.execute(getTransferSql(caller, dataList.subList(page * maxSize, dataList.size()), params));
			} else {
				long t1 = System.currentTimeMillis();
				String sql = getTransferSql(caller, dataList, params);
				long t2 = System.currentTimeMillis();
				System.out.println("generate sql " + (t2 - t1));
				System.out.println(sql);
				baseDao.execute(sql);
				System.out.println("execute sql " + (System.currentTimeMillis() - t2));
			}
		}
	}

	/**
	 * 转单：多行数据、明细<br>
	 * 自动记录日志<br>
	 * 自动修改已转数
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param dataList
	 *            待转明细数据
	 * @param params
	 *            额外参数
	 * @return
	 */
	public String getTransferSql(String caller, List<Map<Object, Object>> dataList, Map<String, Object> params) {
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "DETAIL");
		return getTransferSql(transfer, dataList, params);
	}

	/**
	 * 转单：多行数据、明细<br>
	 * 自动记录日志<br>
	 * 自动修改已转数
	 * 
	 * @param transfer
	 *            转单配置
	 * @param dataList
	 *            待转明细数据
	 * @param params
	 *            额外参数
	 * @return
	 */
	public String getTransferSql(Transfer transfer, List<Map<Object, Object>> dataList, Map<String, Object> params) {
		Employee employee = SystemSession.getUser();
		String pageCaller = transfer.getTr_pagecaller();
		if (pageCaller == null)
			pageCaller = transfer.getTr_caller();
		// 预定义源单据字段、转入单据
		StringBuffer sql = new StringBuffer("DECLARE ");
		sql.append("v_object ").append(transfer.getTr_totable()).append("%rowtype;");
		// 源主键，用于记录日志
		sql.append("COL_X varchar2(64);");
		StringBuffer sqlSelect = new StringBuffer();
		StringBuffer sqlValues = new StringBuffer();
		StringBuffer sqlObject = new StringBuffer();
		sqlSelect.append(transfer.getTr_fromtable()).append(".").append(transfer.getTr_fromdockey());
		sqlValues.append("COL_X");
		String rowField = transfer.getTr_fromrowkey();
		String rowNum = transfer.getTr_fromrownum();
		String rowYQ = transfer.getTr_fromrowyqty();
		// 源明细序号，用于记录日志
		if (StringUtil.hasText(rowNum)) {
			sqlSelect.append(",").append(transfer.getTr_fromtabledet()).append(".").append(rowNum);
			sqlValues.append(",COL_Y");
			sql.append("COL_Y number;");
		}
		int i = 0;
		String aliaCol = null;
		for (Transfer.Detail detail : transfer.getDetails()) {
			String fromTab = detail.getTd_fromtable();
			String fromField = detail.getTd_fromfield();
			if (StringUtil.hasText(fromTab)) {
				if (!"@param".equals(fromTab)) {
					if ("@user".equals(fromTab)) {
						Object userInfo = "";
						if ("em_id".equals(fromField)) {
							userInfo = employee.getEm_id();
						} else if ("em_code".equals(fromField)) {
							userInfo = employee.getEm_code();
						} else if ("em_name".equals(fromField)) {
							userInfo = employee.getEm_name();
						}
						sqlObject.append("v_object.").append(detail.getTd_tofield()).append(" := '").append(userInfo).append("';");
					} else {
						aliaCol = "COL_" + (i++);
						sql.append(aliaCol).append(" ").append(fromTab).append(".").append(fromField).append("%type;");
						sqlSelect.append(",").append(fromTab).append(".").append(fromField);
						sqlValues.append(",").append(aliaCol);
						sqlObject.append("v_object.").append(detail.getTd_tofield()).append(" := ").append(aliaCol).append(";");
					}
				}
			} else {
				sqlObject.append("v_object.").append(detail.getTd_tofield()).append(" := ").append(detail.getTd_fromfield()).append(";");
			}
		}
		sql.append("BEGIN ");
		// 初始序号
		Integer num = 0;
		String toNumField = transfer.getTr_torownum();
		String toRowKey = transfer.getTr_torowkey();
		String toRowForKey = transfer.getTr_torowforkey();
		String toCodeKey = transfer.getTr_tocodekey();
		if (StringUtil.hasText(toNumField)) {
			if (params.containsKey(toNumField))
				num = Integer.parseInt(params.get(toNumField).toString());
			else if (StringUtil.hasText(toRowForKey) && params.containsKey(toRowForKey))
				num = baseDao.getFieldValue(transfer.getTr_totable(), "nvl(max(" + toNumField + "),0)",
						toRowForKey + "=" + params.get(toRowForKey), Integer.class);
		}
		for (Map<Object, Object> map : dataList) {
			sql.append("SELECT ").append(sqlSelect).append(" INTO ").append(sqlValues).append(" FROM ")
					.append(transfer.getTr_fromtablesql()).append(" WHERE ").append(rowField).append("='").append(map.get(rowField))
					.append("';");
			sql.append(sqlObject);
			for (Transfer.Detail detail : transfer.getDetails()) {
				if ("@param".equals(detail.getTd_fromtable())) {
					Object val = "";
					if (map.containsKey(detail.getTd_fromfield()))
						val = map.get(detail.getTd_fromfield());
					else
						val = params.get(detail.getTd_fromfield());
					val = val == null ? "" : val;
					String format = null;
					if (val.toString().matches(SqlUtil.REG_D))
						format = "yyyy-mm-dd";
					else if (val.toString().matches(SqlUtil.REG_DT))
						format = "yyyy-mm-dd hh24:mi:ss";
					if (format != null)
						sql.append("v_object.").append(detail.getTd_tofield()).append(" := to_date('").append(val).append("','")
								.append(format).append("');");
					else
						sql.append("v_object.").append(detail.getTd_tofield()).append(" := '").append(val).append("';");
				}
			}
			// ID
			if (StringUtil.hasText(toRowKey))
				sql.append("v_object.").append(toRowKey).append(" := ").append(transfer.getTr_totable()).append("_SEQ.NEXTVAL;");
			// 序号
			if (StringUtil.hasText(toNumField))
				sql.append("v_object.").append(toNumField).append(" := ").append(++num).append(";");
			// 关联ID
			if (StringUtil.hasText(toRowForKey))
				sql.append("v_object.").append(toRowForKey).append(" := ").append(params.get(toRowForKey)).append(";");
			// 编号
			if (StringUtil.hasText(toCodeKey))
				sql.append("v_object.").append(toCodeKey).append(" := '").append(params.get(toCodeKey)).append("';");
			// 写入
			sql.append("INSERT INTO ").append(transfer.getTr_totable()).append(" VALUES v_object;");
			// 改已转数
			if (StringUtil.hasText(rowYQ) && StringUtil.hasText(transfer.getTr_fromrowqty()))
				sql.append("UPDATE ").append(transfer.getTr_fromtabledet()).append(" SET ").append(rowYQ).append("=nvl(").append(rowYQ)
						.append(",0)+").append(map.get(transfer.getTr_fromrowqty())).append(" WHERE ").append(rowField).append("=")
						.append(map.get(rowField)).append(";");
			// 日志
			sql.append("INSERT INTO messagelog(ml_date,ml_man,ml_content,ml_result,ml_search) values (sysdate,'")
					.append(employee.getEm_name()).append("','").append(transfer.getTr_title()).append("','");
			// 日志里面标注来源所在行、本次数量
			if (StringUtil.hasText(rowNum)) {
				sql.append("行'||COL_Y");
				if (StringUtil.hasText(transfer.getTr_fromrowqty())) {
					sql.append("||',数量'||").append(map.get(transfer.getTr_fromrowqty()));
				}
			} else
				sql.append("转入成功'");
			sql.append(",'").append(pageCaller).append("|").append(transfer.getTr_fromdockey()).append("='||COL_X);");
		}
		sql.append("END;");
		return sql.toString();
	}

}
