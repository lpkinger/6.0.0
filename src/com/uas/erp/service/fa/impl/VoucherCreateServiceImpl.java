package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.VoucherStyle;
import com.uas.erp.model.VoucherStyle.VoucherStyleDetail;
import com.uas.erp.service.fa.VoucherCreateService;

@Service
public class VoucherCreateServiceImpl implements VoucherCreateService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;

	/**
	 * 制作凭证
	 * 
	 * @param vs_code
	 *            公式编号
	 * @param datas
	 *            待制作凭证的数据,用逗号分隔开{如果是合并制作，表示取数据的SQL条件}
	 * @param mode
	 *            single or merge
	 * @param kind
	 *            单据类型
	 * @param yearmonth
	 *            期间
	 * @param vomode
	 *            AR,AP...
	 * @param employee
	 * @param mergerway
	 *            ALL,CUST,VEND...
	 * @return 制作失败原因
	 */
	@Override
	public List<Map<String, Object>> create(String vs_code, String datas, String mode, String kind, int yearmonth, String vomode,
			String mergerway) {
		Employee employee = SystemSession.getUser();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if ("PRODINOUT".equals(vs_code)) {
			// 出入库单据加制作前逻辑 做一些制作前的数据检测
			handlerService.handler("VoucherCreate", "create", "before", new Object[] { datas, mode, kind });
		}
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT vs_explan,vs_classfield,vs_updatecondition,vs_pritable,vs_detailtable,vs_datacondition,vs_voucfield,vs_datefield,vs_prikey1 "
						+ "FROM VoucherStyle WHERE vs_code=?", vs_code);
		if (rs.next()) {
			String sql = "";
			SqlRowList rs1 = null;
			String codes = null;
			Map<String, Object> map = new HashMap<String, Object>();
			if ("merge".equals(mode) && mergerway != null && !"".equals(mergerway) && !"ALL".equals(mergerway)) {
				// 需要制作凭证的单号
				baseDao.execute("insert into AR_DIFF_TEMP (CODE) SELECT " + rs.getString("vs_prikey1") + " FROM "
						+ rs.getString("vs_detailtable") + " WHERE " + datas);
				codes = "SELECT CODE FROM AR_DIFF_TEMP";
				sql = "SELECT " + mergerway + " FROM " + rs.getString("vs_detailtable") + " WHERE " + datas + " GROUP BY " + mergerway;
				rs1 = baseDao.queryForRowSet(sql);
				StringBuffer sb = new StringBuffer();
				String con = datas;
				while (rs1.next()) {
					con = datas + " and (" + mergerway + "='" + rs1.getObject(1) + "')";
					synchronized (SpObserver.getSp()) {
						String res = baseDao.callProcedure("FA_VOUCHERCREATE", new Object[] { yearmonth, vs_code, con, mode, kind, vomode,
								employee.getEm_id(), employee.getEm_name() });
						if (res != null) {
							sb.append(res + "<hr/>");
						}
					}
				}
				if (sb.length() > 0) {
					map = new HashMap<String, Object>();
					map.put("errMsg", sb.toString());
					list.add(map);
				}
			} else {
				if ("merge".equals(mode)) {
					baseDao.execute("insert into AR_DIFF_TEMP (CODE) SELECT " + rs.getString("vs_prikey1") + " FROM "
							+ rs.getString("vs_detailtable") + " WHERE " + datas);
					codes = "SELECT CODE FROM AR_DIFF_TEMP";
				} else if ("single".equals(mode)) {
					codes = datas;
				}
				synchronized (SpObserver.getSp()) {
					String res = baseDao.callProcedure("FA_VOUCHERCREATE", new Object[] { yearmonth, vs_code, datas, mode, kind, vomode,
							employee.getEm_id(), employee.getEm_name() });
					if (res != null) {
						map = new HashMap<String, Object>();
						map.put("errMsg", res);
						list.add(map);
					}
				}
			}
			sql = "select vo_id, vo_code from voucher where vo_errstring is null and vo_code in (SELECT distinct "
					+ rs.getObject("vs_voucfield") + " FROM " + rs.getString("vs_detailtable") + " WHERE " + rs.getString("vs_prikey1")
					+ " in (" + codes + "))";
			rs1 = baseDao.queryForRowSet(sql);
			while (rs1.next()) {
				map = new HashMap<String, Object>();
				map.put("id", rs1.getInt("vo_id"));
				map.put("code", rs1.getString("vo_code"));
				list.add(map);
			}
			baseDao.execute("delete from AR_DIFF_TEMP where code is not null");
		} else {
			BaseUtil.showError("没有对应的凭证公式！");
		}
		return list;
	}

	/**
	 * if mode = 'merge' then datas = condition <br>
	 * 凭证取消
	 */
	@Override
	@Transactional
	public String unCreate(String vs_code, String mode, String kind, String datas, String vomode) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM VoucherStyle WHERE vs_code=?", vs_code);
		if (rs.next()) {
			String tab = rs.getString("vs_pritable");
			String prifield = rs.getString("vs_prikey1");
			String detailtab = rs.getString("vs_detailtable");
			String voucfield = rs.getString("vs_voucfield");
			if ("merge".equals(mode)) {
				datas = datas + " and nvl(" + rs.getString("vs_voucfield") + ",' ')<>' '";
				datas = "SELECT " + prifield + " FROM " + detailtab + " WHERE " + datas;
			}
			int nowYM = voucherDao.getNowPddetno("Month-A");// 当前总账期间
			// int nowBillYM = voucherDao.getNowPddetnoByType(vomode);// 单据当前期间
			int allno = 0; // 所有凭证张数
			int accountno = 0; // 已记账凭证张数
			int auditno = 0; // 已审核凭证张数
			int succno = 0; // 取消成功凭证张数
			String sql = "SELECT WMSYS.WM_CONCAT(vo_code),COUNT(1) FROM Voucher WHERE vo_code in (SELECT " + voucfield + " FROM " + tab
					+ " WHERE " + prifield + " IN (" + datas + ")";
			if ("PRODINOUT".equals(vs_code)) {
				sql += " AND pi_class='" + kind + "'";
			}
			// rs = baseDao.queryForRowSet(sql +
			// ") AND (rpad(vo_yearmonth,7,0) < rpad(" + nowYM +
			// ",7,0) or rpad(vo_yearmonth,7,0) < rpad("
			// + nowBillYM + ",7,0))");
			rs = baseDao.queryForRowSet(sql + ") AND (rpad(vo_yearmonth,7,0) < rpad(" + nowYM + ",7,0))");
			if (rs.next()) {
				if (rs.getString(1) != null)
					return "凭证:" + rs.getString(1) + " 所在总账期间已结账,不能取消制作!";
			}
			rs = baseDao.queryForRowSet(sql + ")");
			if (rs.next()) {
				if (rs.getString(1) != null)
					allno = rs.getGeneralInt(2);
			}
			rs = baseDao.queryForRowSet(sql + ") AND nvl(vo_statuscode,' ')='ACCOUNT'");
			if (rs.next()) {
				if (rs.getString(1) != null)
					accountno = rs.getGeneralInt(2);
			}
			rs = baseDao.queryForRowSet(sql + ") AND nvl(vo_statuscode,' ')='AUDITED'");
			if (rs.next()) {
				if (rs.getString(1) != null)
					auditno = rs.getGeneralInt(2);
			}
			sql = "SELECT WMSYS.WM_CONCAT(vo_id),COUNT(1) FROM Voucher WHERE vo_code in (SELECT " + voucfield + " FROM " + tab + " WHERE "
					+ prifield + " IN (" + datas + ")";
			if ("PRODINOUT".equals(vs_code)) {
				sql += " AND pi_class='" + kind + "'";
			}
			if (baseDao.isDBSetting("Voucher", "unCreateAudit")) {
				sql += ") AND nvl(vo_statuscode,' ')<>'ACCOUNT' and nvl(vo_statuscode,' ')<>'AUDITED'";
			} else {
				sql += ") AND nvl(vo_statuscode,' ')<>'ACCOUNT'";
			}
			rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				if (rs.getString(1) != null) {
					succno = rs.getGeneralInt(2);
					String[] sqls = new String[6];
					sqls[0] = "delete from VoucherDetailAss where vds_vdid in (" + "select vd_id from VoucherDetail where vd_void in("
							+ rs.getString(1) + "))";
					sqls[1] = "delete from VoucherDetail where vd_void in(" + rs.getString(1) + ")";
					sqls[2] = "delete from VoucherFlow where vf_voucherid in (" + rs.getString(1) + ")";
					String condition = voucfield + " in (SELECT vo_code FROM voucher where vo_id in (" + rs.getString(1) + "))";
					if ("PRODINOUT".equals(vs_code)) {
						condition += " AND pi_class='" + kind + "'";
					}
					sqls[3] = "delete from VoucherBill where vb_void in (" + rs.getString(1) + ")";
					sqls[4] = "update " + tab + " set " + voucfield + "=null where " + condition;
					sqls[5] = "delete from Voucher where vo_id in (" + rs.getString(1) + ")";
					baseDao.execute(sqls);
				}
			}
			if (allno == succno) {
				return "取消成功";
			} else {
				if (baseDao.isDBSetting("Voucher", "unCreateAudit")) {
					return succno + "张凭证取消成功<hr>" + (allno - succno) + "张凭证取消失败：已审核" + auditno + "张，已记账" + accountno + "张";
				} else {
					return succno + "张凭证取消成功<hr>" + (allno - succno) + "张凭证取消失败：已记账" + accountno + "张";
				}
			}
		} else {
			return "凭证公式不存在";
		}
	}

	/**
	 * 凭证制作公式
	 */
	@Override
	public void saveVs(String formStore, String gridStore, String assStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("VoucherStyle", "vs_code='" + store.get("vs_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "VoucherStyle"));
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "vsa_vdid");
		int id;
		for (Map<Object, Object> map : grid) {
			id = baseDao.getSeqId("VOUCHERSTYLEDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("vd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// VoucherDetailAss
					m.put("vsa_vdid", id);
					m.put("vsa_id", baseDao.getSeqId("VOUCHERSTYLEASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "VoucherStyleAss"));
			}
			map.put("vd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "VoucherStyleDetail"));
		baseDao.execute(
				"DELETE from voucherstyleass where exists (select 1 from voucherstyledetail where vd_vsid=? and vd_id=vsa_vdid) and (nvl(vsa_assname, ' ')=' ' or exists (select 1 from voucherstyledetail where vd_id=vsa_vdid and vd_vsid=? and vd_asstable is not null))",
				store.get("vs_id"), store.get("vs_id"));
		try {
			// 记录操作
			baseDao.logger.save("VoucherStyle", "vs_id", store.get("vs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 凭证制作公式
	 */
	@Override
	public void updateVs(String formStore, String gridStore, String assStore, String groupStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "VoucherStyle", "vs_id"));
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> group = BaseUtil.parseGridStoreToMaps(groupStore);
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "vsa_vdid");
		int id;
		List<String> gridSql = null;
		if (grid.size() > 0) {
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "VoucherStyleDetail", "vd_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("vd_id") == null || s.get("vd_id").equals("") || s.get("vd_id").equals("0")
						|| Integer.parseInt(s.get("vd_id").toString()) <= 0) {
					id = baseDao.getSeqId("VOUCHERSTYLEDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("vd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("vsa_vdid", id);
							m.put("vsa_id", baseDao.getSeqId("VOUCHERSTYLEASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "VoucherStyleAss"));
					}
					s.put("vd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "VoucherStyleDetail"));
				} else {
					id = Integer.parseInt(s.get("vd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "VoucherStyleAss", "vsa_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("vsa_id") == null || m.get("vsa_id").equals("") || m.get("vsa_id").equals("0")
									|| Integer.parseInt(m.get("vsa_id").toString()) <= 0) {
								m.put("vsa_id", baseDao.getSeqId("VOUCHERSTYLEASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "VoucherStyleAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "VoucherStyleAss", "vsa_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("vsa_id") == null || m.get("vsa_id").equals("") || m.get("vsa_id").equals("0")
								|| Integer.parseInt(m.get("vsa_id").toString()) <= 0) {
							m.put("vsa_id", baseDao.getSeqId("VOUCHERSTYLEASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "VoucherStyleAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		if (group.size() > 0) {
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(group, "VOUCHERSTYLEGROUP", "vsg_id"));
		}
		baseDao.execute(
				"DELETE from voucherstyleass where exists (select 1 from voucherstyledetail where vd_vsid=? and vd_id=vsa_vdid) and (nvl(vsa_assname, ' ')=' ' or exists (select 1 from voucherstyledetail where vd_id=vsa_vdid and vd_vsid=? and vd_asstable is not null))",
				store.get("vs_id"), store.get("vs_id"));
		try {
			// 记录操作
			baseDao.logger.update("VoucherStyle", "vs_id", store.get("vs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String unCreatePurcfee() {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");
		String yearmonth = String.valueOf(periods.get("PD_DETNO"));
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code,vo_statuscode", "vo_yearmonth=" + yearmonth
				+ " AND (vo_explanation like '%结转采购费用%' or vo_source like '%结转采购费用%')");
		if (vo == null) {
			return "本期" + yearmonth + "的采购费用未结转凭证!";
		}
		if ("ACCOUNT".equals(vo[2])) {// 已记账的凭证，不允许取消
			return "本期" + yearmonth + "采购费用结转的凭证" + vo[1] + "已记账，不允许取消!";
		}
		baseDao.deleteByCondition("VoucherDetailAss", "vds_vdid in (select vd_id from VoucherDetail where vd_void=" + vo[0] + ")");
		baseDao.deleteByCondition("VoucherDetail", "vd_id in (select vd_id from VoucherDetail where vd_void=" + vo[0] + ")");
		baseDao.deleteByCondition("VoucherFlow", "vf_voucherid =" + vo[0]);
		baseDao.updateByCondition("ProdInOut", "pi_vouchercode=null", "pi_vouchercode='" + vo[1] + "'");
		baseDao.deleteByCondition("Voucher", "vo_id =" + vo[0]);
		baseDao.deleteByCondition("VoucherBill", "vb_void =" + vo[0]);
		return null;
	}

	@Override
	public List<?> getDigestSource(String code, String type) {
		SqlRowList list = baseDao
				.queryForRowSet(
						"select DIS_DESCRIPTION,DIS_CODE,DIS_CLASS,DIS_ID,DIS_SQLPARAM from VS_DIGESTSOURCE where dis_code=? and dis_class=? order by dis_id",
						code, type);
		return list.getResultList();
	}

	@Override
	public void createSql(Integer id, String type) {
		StringBuffer sb = new StringBuffer();
		String columns = null;
		String table = null;
		StringBuffer sql = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet("select * from VoucherStyleDetail where vd_vsid=? and vd_class=? order by vd_detno", id,
				type);
		while (rs.next()) {
			if (rs.getGeneralInt("vd_casid") == 0) {
				sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的科目来源未填写，不能生成SQL！").append("<hr>");
			} else {
				SqlRowList cas = baseDao.queryForRowSet("select * from VS_CATESOURCE where cas_id=?", rs.getGeneralInt("vd_casid"));
				if (cas.next()) {
					columns = cas.getString("cas_sqlparam");
					table = cas.getString("cas_sqltitle");
				} else {
					sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的科目来源不存在，不能生成SQL！").append("<hr>");
				}
				if (rs.getObject("vd_catecode") != null && !"".equals(rs.getObject("vd_catecode"))) {
					columns = rs.getString("vd_catecode") + " as catecode";
				}
				if (rs.getGeneralInt("vd_amsid") == 0) {
					sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的金额来源未填写，不能生成SQL！").append("<hr>");
				} else {
					SqlRowList ams = baseDao.queryForRowSet("select * from VS_AMOUNTSOURCE where ams_id=?", rs.getGeneralInt("vd_amsid"));
					if (ams.next()) {
						columns = columns + "," + ams.getString("ams_sqlparam");
					} else {
						sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的金额来源不存在，不能生成SQL！").append("<hr>");
					}
				}
				if (rs.getGeneralInt("vd_cusid") == 0) {
					sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的币别来源未填写，不能生成SQL！").append("<hr>");
				} else {
					SqlRowList cus = baseDao.queryForRowSet("select * from VS_CURRENCYSOURCE where cus_id=?", rs.getGeneralInt("vd_cusid"));
					if (cus.next()) {
						columns = columns + "," + cus.getString("cus_sqlparam");
					} else {
						sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的币别来源不存在，不能生成SQL！").append("<hr>");
					}
				}
				if (rs.getGeneralInt("vd_rasid") == 0) {
					sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的汇率来源未填写，不能生成SQL！").append("<hr>");
				} else {
					SqlRowList ras = baseDao.queryForRowSet("select * from VS_RATESOURCE where ras_id=?", rs.getGeneralInt("vd_rasid"));
					if (ras.next()) {
						columns = columns + "," + ras.getString("ras_sqlparam");
					} else {
						sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的汇率来源不存在，不能生成SQL！").append("<hr>");
					}
				}
				if (rs.getString("vd_explanation") != null) {
					columns = columns + "," + rs.getString("vd_explanation") + " as explanation";
				}
				if (rs.getGeneralInt("vd_resid") == 0) {
					sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的参考号来源未填写，不能生成SQL！").append("<hr>");
				} else {
					SqlRowList res = baseDao.queryForRowSet("select * from VS_REFNOSOURCE where res_id=?", rs.getGeneralInt("vd_resid"));
					if (res.next()) {
						columns = columns + "," + res.getString("res_sqlparam");
					} else {
						sb.append("序号[" + rs.getGeneralInt("vd_detno") + "]的参考号来源不存在，不能生成SQL！").append("<hr>");
					}
				}
			}
			if (columns != null && table != null) {
				sql.append("SELECT ").append(columns).append(" FROM ").append(table);
			}
			if (sql.length() > 0) {
				baseDao.execute("update VoucherStyleDetail set vd_sqlstr='" + sql.toString().replace("'", "''") + "' where vd_id="
						+ rs.getObject("vd_id"));
			}
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	@Override
	public VoucherStyle getVoucherStyleByClass(int vs_id, String vd_class) {
		VoucherStyle voucherStyle = baseDao.queryBean("select * from VoucherStyle where vs_id=?", VoucherStyle.class, vs_id);
		if (null != voucherStyle) {
			voucherStyle.setDetails(baseDao.query("select * from VoucherStyleDetail where vd_vsid=? and vd_class=?",
					VoucherStyleDetail.class, vs_id, vd_class));
		}
		return voucherStyle;
	}

	@Override
	@Transactional
	public void saveVoucherStyle(VoucherStyle voucherStyle) {
		VoucherStyle oldOne = baseDao
				.queryBean("select * from VoucherStyle where vs_code=?", VoucherStyle.class, voucherStyle.getVs_code());
		Integer newId = null;
		if (null != oldOne) {
			newId = oldOne.getVs_id();
			// 覆盖原公式
			baseDao.deleteById("VoucherStyle", "vs_id", newId);
			if (!CollectionUtils.isEmpty(voucherStyle.getDetails())) {
				String vdClass = voucherStyle.getDetails().get(0).getVd_class();
				baseDao.deleteByCondition("VoucherStyleDetail", "vd_vsid=? and vd_class=?", newId, vdClass);
			}
		} else {
			newId = baseDao.getSeqId("VoucherStyle_SEQ");
		}
		voucherStyle.setVs_id(newId);
		baseDao.save(voucherStyle, "VoucherStyle");
		if (null != voucherStyle.getDetails()) {
			for (VoucherStyleDetail detail : voucherStyle.getDetails()) {
				detail.setVd_vsid(newId);
				detail.setVd_id(baseDao.getSeqId("VoucherStyleDetail_SEQ"));
			}
			baseDao.save(voucherStyle.getDetails(), "VoucherStyleDetail");
		}
	}
}
