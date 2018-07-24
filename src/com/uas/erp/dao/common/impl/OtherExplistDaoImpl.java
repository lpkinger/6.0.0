package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.OtherExplistDao;

@Repository
public class OtherExplistDaoImpl extends BaseDao implements OtherExplistDao {

	static final String CHECK_YQTY = "SELECT ma_code,md_detno,md_qty FROM OtherExplist,OtherExplistdetail WHERE ma_id=md_maid and md_id=? and md_qty<?";

	/**
	 * 1.判断状态 2.判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkYqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		SqlRowList rs = null;
		boolean bool = false;
		Object[] qus = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("md_id").toString());
			qus = getFieldsDataByCondition("OtherExplistdetail left join OtherExplist on md_maid=ma_id", "ma_code,md_detno", "md_id=" + id);
			if (qus != null) {
				bool = checkIf("OtherExplist", "ma_code='" + qus[0] + "' and ma_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("加工委外单:" + qus[0] + " 未审核通过,无法转加工验收单!");
				}
				y = getFieldDataByCondition("ProdioDetail", "sum(nvl(pd_inqty,0))", "pd_piclass='加工验收单' and pd_orderid=" + id);
				y = y == null ? 0 : y;
				rs = queryForRowSet(CHECK_YQTY, id, Double.parseDouble(y.toString()) + Double.parseDouble(d.get("md_tqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],加工委外单号:").append(rs.getString("ma_code")).append(",行号:")
							.append(rs.getInt("md_detno")).append(",委外数量:").append(rs.getDouble("md_qty")).append(",已转数:").append(y)
							.append(",本次数:").append(d.get("md_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	@Override
	public void updateStatus(int mdid) {
		int ma_id = getFieldValue("OtherExplistdetail", "md_maid", "md_id=" + mdid, Integer.class);
		execute("update OtherExplist set ma_turnstatuscode=null,ma_turnstatus=null where ma_id=" + ma_id
				+ " and not exists(select 1 from OtherExplistdetail where md_maid=ma_id and abs(NVL(md_yqty,0))>0)");
		execute("update OtherExplist set ma_turnstatuscode='PART2IN',ma_turnstatus='"
				+ BaseUtil.getLocalMessage("PART2IN")
				+ "' where ma_id="
				+ ma_id
				+ " and nvl(ma_turnstatuscode,' ')<>'PART2IN' and exists (select 1 from OtherExplistdetail where md_maid=ma_id and abs(NVL(md_yqty,0))>0)");
		execute("update OtherExplist set ma_turnstatuscode='TURNIN',ma_turnstatus='"
				+ BaseUtil.getLocalMessage("TURNIN")
				+ "' where ma_id="
				+ ma_id
				+ " and nvl(ma_turnstatuscode,' ')<>'TURNIN' and not exists (select 1 from OtherExplistdetail where md_maid=ma_id and abs(nvl(md_qty,0))-abs(NVL(md_yqty,0))>0)");
	}

	@Override
	public void restoreSourceYqty(SqlRowList oldpd, SqlRowList newpd) {
		Object y = null;
		Object macode = null;
		Object mddetno = 0;
		Object pdid = 0;
		Double thisqty = 0.0;
		if (oldpd != null && oldpd.hasNext()) {
			while (oldpd.next()) {
				macode = oldpd.getObject("pd_ordercode");
				mddetno = oldpd.getObject("pd_orderdetno");
				pdid = oldpd.getObject("pd_id");
				SqlRowList md = queryForRowSet("select md_id,md_qty from OtherExplistdetail where md_code=? and md_detno=?", macode,
						mddetno);
				if (md.next()) {
					execute("update OtherExplistdetail set md_yqty=nvl((select sum(nvl(pd_inqty,0)-nvl(pd_outqty,0)) from prodiodetail where pd_piclass in ('加工验收单','加工验退单') and pd_ordercode=md_code and pd_orderdetno=md_detno),0) "
							+ "where md_id=" + md.getGeneralInt("md_id"));
					updateStatus(md.getGeneralInt("md_id"));
				}
			}
		}
		if (newpd != null && newpd.hasNext()) {
			while (newpd.next()) {
				macode = newpd.getObject("pd_ordercode");
				mddetno = newpd.getObject("pd_orderdetno");
				pdid = newpd.getObject("pd_id");
				thisqty = newpd.getGeneralDouble("pd_qty");
				SqlRowList md = queryForRowSet("select md_id,md_qty from OtherExplistdetail where md_code=? and md_detno=?", macode,
						mddetno);
				if (md.next()) {
					y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0)-nvl(pd_outqty,0))",
							"pd_piclass in ('加工验收单','加工验退单') and pd_ordercode='" + macode + "' and pd_orderdetno=" + mddetno
									+ " and pd_id<>" + pdid);
					y = y == null ? 0 : y;
					if (thisqty + Double.parseDouble(y.toString()) > md.getGeneralDouble("md_qty")) {
						BaseUtil.showError("加工单号[" + macode + "]行号[" + mddetno + "]本次修改数量[" + thisqty + "]大于剩余数量["
								+ (md.getGeneralDouble("md_qty") - Double.parseDouble(y.toString())) + "]！");
					}
					execute("update OtherExplistdetail set md_yqty=nvl((select sum(nvl(pd_inqty,0)-nvl(pd_outqty,0)) from prodiodetail where pd_piclass in ('加工验收单','加工验退单') and pd_ordercode=md_code and pd_orderdetno=md_detno),0) "
							+ "where md_id=" + md.getGeneralInt("md_id"));
					updateStatus(md.getGeneralInt("md_id"));
				}
			}
		}
	}
}
