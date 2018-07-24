package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.VoucherTPService;

@Service
public class VoucherTPServiceImpl implements VoucherTPService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void saveVoucherTP(String formStore, String gridStore, String assStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Voucher", "vo_code='" + store.get("vo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		store.put("vo_recordman", SystemSession.getUser().getEm_name());
		List<String> sqls = new ArrayList<String>();
		// 保存
		sqls.add(SqlUtil.getInsertSqlByMap(store, "Voucher_TP"));
		// 保存VoucherDetail_TP
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(assStore);
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "vds_vdid");
		int id;
		for (Map<Object, Object> map : grid) {
			id = baseDao.getSeqId("VOUCHERDETAIL_TP_SEQ");
			assgrid = list.get(String.valueOf(map.get("vd_id")));
			if (assgrid != null) {
				for (Map<Object, Object> m : assgrid) {// VoucherDetailAss_TP
					m.put("vds_vdid", id);
					m.put("vds_type", "Voucher");
				}
				sqls.addAll(SqlUtil.getInsertSqlbyList(assgrid, "VoucherDetailAss_TP", "vds_id"));
			}
			map.put("vd_id", id);
		}
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "VoucherDetail_TP"));
		baseDao.execute(sqls);
		baseDao.execute(
				"update voucher_tp set vo_explanation=(select * from (select vd_explanation from voucherdetail_tp where vd_void=? and nvl(vd_explanation,' ')<>' ' order by vd_detno) where rownum=1) where vo_id=?",
				store.get("vo_id"), store.get("vo_id"));
		baseDao.execute(
				"update voucherdetail_tp set vd_debit=round(nvl(vd_debit,0),2),vd_doubledebit=round(nvl(vd_doubledebit,0),4),vd_credit=round(nvl(vd_credit,0),2),vd_doublecredit=round(nvl(vd_doublecredit,0),4) where vd_void=?",
				store.get("vo_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "vo_id", store.get("vo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateVoucherTP(String formStore, String gridStore, String assStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 修改
		List<String> sqls = new ArrayList<String>();
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "Voucher_TP", "vo_id"));
		// 保存VoucherDetail
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if (grid.size() > 0) {
			sqls.addAll(SqlUtil.getUpdateSqlbyGridStore(grid, "VoucherDetail_TP", "vd_id"));
			sqls.add(0, "update VoucherDetail_TP set vd_detno=-vd_detno where vd_void=" + store.get("vo_id"));
			List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(assStore);
			Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "vds_vdid");
			for (Map<Object, Object> s : grid) {
				if (s.get("vd_id") == null || s.get("vd_id").equals("") || s.get("vd_id").equals("0")
						|| Integer.parseInt(s.get("vd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("VOUCHERDETAIL_TP_SEQ");
					assgrid = list.get(String.valueOf(s.get("vd_id")));
					if (assgrid != null) {
						for (Map<Object, Object> m : assgrid) {// VoucherDetailAss
							m.put("vds_vdid", id);
							m.put("vds_type", "Voucher");
						}
						sqls.addAll(SqlUtil.getInsertSqlbyList(assgrid, "VoucherDetailAss_TP", "vds_id"));
					}
					s.put("vd_id", id);
					sqls.add(SqlUtil.getInsertSqlByMap(s, "VoucherDetail_TP"));
				} else {
					// 科目有修改的情况下，先删除之前科目的辅助核算
					sqls.add("delete from voucherdetailass_TP where vds_vdid="
							+ s.get("vd_id")
							+ " and instr(nvl((select ca_assname from category left join voucherdetail_TP on ca_code=vd_catecode where vd_id=vds_vdid and ca_assname is not null),' '), vds_asstype) = 0");
				}
			}
			for (Object key : list.keySet()) {
				Integer id = Integer.parseInt(String.valueOf(key));
				if (id > 0) {
					assgrid = list.get(key);
					if (assgrid != null) {
						sqls.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(assgrid, "VoucherDetailAss_TP", "vds_id"));
					}
				}
			}
			sqls.add("update VoucherDetail_TP set vd_detno=abs(vd_detno) where vd_void=" + store.get("vo_id"));
			baseDao.execute(sqls);
		} else {
			grid = BaseUtil.parseGridStoreToMaps(assStore);
			sqls.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "VoucherDetailAss_TP", "vds_id"));
			baseDao.execute(sqls);
		}
		baseDao.execute(
				"update voucher_tp set vo_explanation=(select * from (select vd_explanation from voucherdetail_tp where vd_void=? and nvl(vd_explanation,' ')<>' ' order by vd_detno) where rownum=1) where vo_id=?",
				store.get("vo_id"), store.get("vo_id"));
		baseDao.execute(
				"update voucherdetail_tp set vd_debit=round(nvl(vd_debit,0),2),vd_doubledebit=round(nvl(vd_doubledebit,0),4),vd_credit=round(nvl(vd_credit,0),2),vd_doublecredit=round(nvl(vd_doublecredit,0),4) where vd_void=?",
				store.get("vo_id"));
		// 记录操作
		baseDao.logger.update(caller, "vo_id", store.get("vo_id"));
	}

	@Override
	public void deleteVoucherTP(int vo_id, String caller) {
		// 删除
		baseDao.deleteById("Voucher_TP", "vo_id", vo_id);
		// 删除VoucherDetailAss
		baseDao.deleteByCondition("VoucherDetailAss_TP", "vds_vdid in(select vd_id from voucherdetail_TP where vd_void=" + vo_id + ")");
		// 删除VoucherDetail
		baseDao.deleteById("VoucherDetail_TP", "vd_void", vo_id);
		// 记录操作
		baseDao.logger.delete(caller, "vo_id", vo_id);
	}

	@Override
	public JSONObject createVoucher(int id) {
		// Copy 凭证
		SqlRowList vo = baseDao.queryForRowSet("select * from voucher_tp where vo_id=?", id);
		if (vo.hasNext()) {
			int nId = baseDao.getSeqId("VOUCHER_SEQ");
			String code = baseDao.sGetMaxNumber("Voucher", 2);
			Map<String, Object> period = voucherDao.getJustPeriods("Month-A");
			Object yearmonth = period.get("PD_DETNO");
			String lead = vo.getString("vo_lead");
			String num = voucherDao.getVoucherNumber(String.valueOf(yearmonth), lead, null);

			baseDao.execute("insert into voucher (vo_id,vo_date,vo_yearmonth,vo_number,vo_code,vo_emid,vo_recordman,vo_status,vo_statuscode,vo_recorddate,vo_printstatus,"
					+ "VO_REFNO,VO_TOTALUPPER,VO_TYPE,vo_explanation,VO_TOTAL,VO_CURRENCYTYPE,VO_CURRENCY,VO_REMARK,VO_COP,VO_LEAD) "
					+ "select "
					+ nId
					+ ", sysdate,"
					+ yearmonth
					+ ","
					+ num
					+ ",'"
					+ code
					+ "',"
					+ SystemSession.getUser().getEm_id()
					+ ",'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ Status.ENTERING.display()
					+ "','ENTERING',sysdate,'"
					+ Status.UNPRINT.display()
					+ "',VO_REFNO,VO_TOTALUPPER,VO_TYPE,vo_explanation,VO_TOTAL,VO_CURRENCYTYPE,VO_CURRENCY,VO_REMARK,VO_COP,VO_LEAD "
					+ "from voucher_tp where vo_id=" + id);
			baseDao.execute("update voucher set vo_date=(select pd_enddate from PeriodsDetail where pd_code='MONTH-A' and pd_detno="
					+ yearmonth + ") where vo_id=" + nId);
			// 生成凭证明细
			SqlRowList list = baseDao.queryForRowSet("SELECT vd_id FROM VoucherDetail_TP WHERE vd_void=?", id);
			List<String> sqls = new ArrayList<String>();
			List<String> asssqls = new ArrayList<String>();
			Integer dId = null;
			Object vdid = null;
			while (list.next()) {
				dId = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
				vdid = list.getObject("vd_id");
				sqls.add("insert into VOUCHERDETAIL(VD_ISONE,VD_ACID,VD_ASSMULTI,VD_ID,VD_VOID,VD_DETNO,VD_YEARMONTH,VD_EXPLANATION,VD_CATECODE,VD_GENCATECODE,VD_CURRENCY,VD_RATE,VD_DOUBLEDEBIT,VD_DOUBLECREDIT,VD_DEBIT,VD_CREDIT,VD_MARK,VD_DEBITCASHFLOW,VD_CREDITCASHFLOW,VD_SETFLOW,VD_ANTICATECODE,VD_CATENAME,VD_CODE,VD_FLOWCODE,VD_FLOWNAME) "
						+ "select VD_ISONE,VD_ACID,VD_ASSMULTI,"
						+ dId
						+ ","
						+ nId
						+ ",VD_DETNO,VD_YEARMONTH,VD_EXPLANATION,VD_CATECODE,VD_GENCATECODE,VD_CURRENCY,VD_RATE,VD_DOUBLEDEBIT,VD_DOUBLECREDIT,VD_DEBIT,VD_CREDIT,VD_MARK,VD_DEBITCASHFLOW,VD_CREDITCASHFLOW,VD_SETFLOW,VD_ANTICATECODE,VD_CATENAME,'"
						+ code + "',VD_FLOWCODE,VD_FLOWNAME from VoucherDetail_TP where vd_id=" + vdid);
				// Copy 辅助核算
				asssqls.add("insert into voucherdetailass(VDS_ID,VDS_VDID,VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE) "
						+ "select VOUCHERDETAILASS_SEQ.nextval,"
						+ dId
						+ ",VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE from VoucherDetailAss_TP where VDS_VDID="
						+ vdid);
			}
			baseDao.execute(sqls);
			baseDao.execute(asssqls);
			JSONObject obj = new JSONObject();
			obj.put("vo_id", nId);
			obj.put("vo_number", num);
			obj.put("vo_code", code);
			return obj;
		}
		return null;
	}

	@Override
	public Map<String, Object> getTp(int vo_id) {
		Map<String, Object> tp = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select vo_lead,vo_explanation,vo_refno,vo_currencytype from voucher_tp where vo_id=?",
				vo_id);
		Map<String, Object> vo = new HashMap<String, Object>();
		if (rs.next()) {
			vo.put("vo_id", 0);
			vo.put("vo_lead", rs.getObject("vo_lead"));
			vo.put("vo_explanation", rs.getObject("vo_explanation"));
			vo.put("vo_refno", rs.getObject("vo_refno"));
			vo.put("vo_currencytype", rs.getObject("vo_currencytype"));
			vo.put("vo_printstatus", Status.UNPRINT.display());
		}
		tp.put("voucher", vo);
		rs = baseDao
				.queryForRowSet(
						"select vd_id,vd_detno,vd_explanation,vd_catecode,vd_currency,vd_rate,vd_doubledebit,vd_doublecredit,vd_debit,vd_credit,ca_description,ca_asstype,ca_assname,vds_detno,vds_asstype,vds_asscode,vds_assname from voucherdetail_tp left join category on vd_catecode=ca_code left join voucherdetailass_tp on vds_vdid=vd_id where vd_void=? order by vd_detno,vds_detno",
						vo_id);
		List<Map<String, Object>> vd = new ArrayList<Map<String, Object>>();
		int tempId = -9999;
		while (rs.next()) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("vd_void", 0);
			d.put("vd_detno", rs.getObject("vd_detno"));
			d.put("vd_explanation", rs.getObject("vd_explanation"));
			d.put("vd_catecode", rs.getObject("vd_catecode"));
			d.put("vd_currency", rs.getObject("vd_currency"));
			d.put("vd_rate", rs.getObject("vd_rate"));
			d.put("vd_doubledebit", rs.getObject("vd_doubledebit"));
			d.put("vd_doublecredit", rs.getObject("vd_doublecredit"));
			d.put("vd_debit", rs.getObject("vd_debit"));
			d.put("vd_credit", rs.getObject("vd_credit"));
			d.put("ca_description", rs.getObject("ca_description"));
			d.put("ca_asstype", rs.getObject("ca_asstype"));
			d.put("ca_assname", rs.getObject("ca_assname"));
			d.put("vd_id", --tempId);
			List<Map<String, Object>> ass = new ArrayList<Map<String, Object>>();
			if (rs.getObject("ca_assname") != null) {
				int id = rs.getGeneralInt("vd_id");
				int index = rs.getCurrentIndex();
				int count = 0;
				while (count < 10) {
					Map<String, Object> as = rs.getAt(index++);
					if (as != null && Integer.parseInt(String.valueOf(as.get("vd_id"))) == id) {
						Map<String, Object> s = new HashMap<String, Object>();
						s.put("vds_detno", as.get("vds_detno"));
						s.put("vds_asstype", as.get("vds_asstype"));
						s.put("vds_asscode", as.get("vds_asscode"));
						s.put("vds_assname", as.get("vds_assname"));
						s.put("vds_vdid", tempId);
						ass.add(s);
						count++;
					} else
						break;
				}
				rs.next(count - 1);
			}
			d.put("ass", ass);
			vd.add(d);
		}
		tp.put("voucherdetail", vd);
		return tp;
	}

	@Override
	@Transactional
	public JSONObject createTpByVo(int id) {
		// Copy 模板
		SqlRowList vo = baseDao.queryForRowSet("select * from voucher where vo_id=?", id);
		if (vo.next()) {
			int nId = baseDao.getSeqId("VOUCHER_TP_SEQ");
			String code = baseDao.sGetMaxNumber("Voucher_TP", 2);
			baseDao.execute("insert into Voucher_TP(VO_REFNO,VO_TOTALUPPER,VO_ID,VO_CODE,VO_TYPE,VO_RECORDDATE,VO_EXPLANATION,VO_TOTAL,VO_CURRENCYTYPE,VO_CURRENCY,VO_REMARK,VO_RECORDMAN,VO_COP,VO_LEAD) "
					+ "select VO_REFNO,VO_TOTALUPPER,"
					+ nId
					+ ",'"
					+ code
					+ "',VO_TYPE,sysdate,VO_EXPLANATION,VO_TOTAL,VO_CURRENCYTYPE,VO_CURRENCY,VO_REMARK,'"
					+ SystemSession.getUser().getEm_name() + "',VO_COP,VO_LEAD from voucher where vo_id=" + id);
			// Copy 凭证明细
			SqlRowList list = baseDao.queryForRowSet("SELECT vd_id FROM VoucherDetail WHERE vd_void=?", id);
			Integer dId = null;
			List<String> sqls = new ArrayList<String>();
			List<String> asssqls = new ArrayList<String>();
			while (list.next()) {
				dId = baseDao.getSeqId("VOUCHERDETAIL_TP_SEQ");
				sqls.add("insert into voucherdetail_tp(VD_ISONE,VD_ACID,VD_ASSMULTI,VD_ID,VD_VOID,VD_DETNO,VD_YEARMONTH,VD_EXPLANATION,VD_CATECODE,VD_GENCATECODE,VD_CURRENCY,VD_RATE,VD_DOUBLEDEBIT,VD_DOUBLECREDIT,VD_DEBIT,VD_CREDIT,VD_MARK,VD_DEBITCASHFLOW,VD_CREDITCASHFLOW,VD_SETFLOW,VD_ANTICATECODE,VD_CATENAME,VD_CODE,VD_FLOWCODE,VD_FLOWNAME) "
						+ "select VD_ISONE,VD_ACID,VD_ASSMULTI,"
						+ dId
						+ ","
						+ nId
						+ ",VD_DETNO,VD_YEARMONTH,VD_EXPLANATION,VD_CATECODE,VD_GENCATECODE,VD_CURRENCY,VD_RATE,VD_DOUBLEDEBIT,VD_DOUBLECREDIT,VD_DEBIT,VD_CREDIT,VD_MARK,VD_DEBITCASHFLOW,VD_CREDITCASHFLOW,VD_SETFLOW,VD_ANTICATECODE,VD_CATENAME,'"
						+ code + "',VD_FLOWCODE,VD_FLOWNAME from voucherdetail where vd_id=" + list.getObject("vd_id"));
				// Copy 辅助核算
				asssqls.add("insert into voucherdetailass_tp(VDS_ID,VDS_VDID,VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE) "
						+ "select voucherdetailass_tp_seq.nextval,"
						+ dId
						+ ",VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE from voucherdetailass where VDS_VDID="
						+ list.getObject("vd_id"));
			}
			baseDao.execute(sqls);
			baseDao.execute(asssqls);
			JSONObject obj = new JSONObject();
			obj.put("vo_id", nId);
			obj.put("vo_code", code);
			return obj;
		}
		return null;
	}

}
