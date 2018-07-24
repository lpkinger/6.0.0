package com.uas.erp.dao.common.impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MrpDao;

@Transactional
@Repository
public class MrpDaoImpl extends BaseDao implements MrpDao {
	@Override
	public String ThrowApplication(List<Map<Object, Object>> list) {
		// 主从表
		String insertSQl = "INSERT INTO Application(ap_id,ap_code,ap_status,ap_statuscode"
				+ ",ap_recordid,ap_recorddate,ap_sourcecode,ap_delivery,ap_kind) VALUES (?,?,?,?,?,?,?,?,?)";
		String insertDetailSql = "Insert into ApplicationDetail(ad_id,ad_detno,ad_apid,ad_prodcode,ad_prodname,ad_delivery,ad_qty,ad_sourcecode)values(?,?,?,?,?,?,?,?)";
		try {
			int apid = 0;
			int index = 1;
			String code = null;
			boolean bool = false;
			for (int i = 0; i < list.size(); i++) {
				Map<Object, Object> map = list.get(i);
				apid = getSeqId("APPLICATION_SEQ");
				if (code == null) {
					code = sGetMaxNumber("Application", 2);
					execute(insertSQl, new Object[] { apid, code, BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", SystemSession.getUser().getEm_id(), Date.valueOf(DateUtil.currentDateString("yyyy-MM-dd")),
							map.get("md_sourcecode"), map.get("md_deliverydate"), map.get("md_pokind") });
					bool = true;
				}
				if (bool) {
					// 插入明细
					int adid = getSeqId("APPLICATIONDETAIL_SEQ");
					execute(insertDetailSql,
							new Object[] { adid, index, apid, map.get("md_prodcode"), map.get("md_prodname"),
									"to_date('" + map.get("md_needdate") + "',yyyy-MM-dd')", map.get("md_changeqty"),
									map.get("md_sourcecode") });
					index++;
				}
			}
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}

	@Override
	public JSONObject ThrowMakeTask(int mdid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject ThrowToMakeTask(int mdid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ThrowPurchaseChange(List<Map<Object, Object>> list) {
		String insertSQl = "INSERT INTO Application(ap_id,ap_code,ap_status,ap_statuscode"
				+ ",ap_recordid,ap_recorddate,ap_sourcecode,ap_delivery,ap_kind) VALUES (?,?,?,?,?,?,?,?,?)";
		String insertDetailSql = "Insert into PurchaseChangeDetail(pcd_id,pcd_detno,pcd_pcid,pcd_oldprodcode,pcd_oldprodname,pcd_olddelivery,pcd_oldqty)values(?,?,?,?,?,?,?)";
		try {
			int pcid = 0;
			int index = 1;
			String code = null;
			boolean bool = false;
			for (int i = 0; i < list.size(); i++) {
				Map<Object, Object> map = list.get(i);
				pcid = getSeqId("PURCHASECHANGE_SEQ");
				if (code == null) {
					code = sGetMaxNumber("PurchaseChange", 2);
					execute(insertSQl, new Object[] { pcid, code, BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", SystemSession.getUser().getEm_id(), Date.valueOf(DateUtil.currentDateString(Constant.YMD)),
							map.get("md_sourcecode"), map.get("md_deliverydate"), map.get("md_pokind") });
					bool = true;
				}
				if (bool) {
					// 插入明细
					int pcdid = getSeqId("PURCHASECHANGEDETAIL_SEQ");
					execute(insertDetailSql,
							new Object[] { pcdid, index, pcid, map.get("md_prodcode"), map.get("md_prodname"),
									"to_date('" + map.get("md_needdate") + "',yyyy-MM-dd')" });
					index++;
				}
			}
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}

	@Override
	public JSONObject ThrowMakeChange(int mdid) {
		String FindSQL = "Select md_prodcode,md_prodname,md_prodspec form MRPData where md_id=" + mdid;
		String insertSQL = "insert into makechangedetail(md_id,md_mcid,md_prodcode,md_prodname,md_prodspec) values(?,?,?)";
		SqlRowList rs = queryForRowSet(FindSQL);
		while (rs.next()) {
			int md_id = getSeqId("MAKECHANGEDETAIL_SEQ");
			execute(insertSQL, new Object[] { md_id, mdid, rs.getString(3), rs.getString(4), rs.getString(5) });
		}
		return null;
	}

}
