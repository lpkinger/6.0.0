package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.SaleDetailDetService;

@Service
public class SaleDetailDetSeviceImpl implements SaleDetailDetService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public void updateSaleDetailSet(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SaleDetaildet", "sdd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sdd_id") == null || s.get("sdd_id").equals("") || s.get("sdd_id").equals("0")
					|| Integer.parseInt(s.get("sdd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SaleDetaildet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleDetaildet", new String[] { "sdd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		int sd_id = Integer.valueOf(store.get("sd_id").toString());
		baseDao.execute("update SaleDetail set sd_ifplandelivery=-1 ,sd_plandate="
				+ DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + " where sd_id=" + sd_id);
		baseDao.execute("update SaleDetaildet set sdd_updatedate=" + DateUtil.parseDateToOracleString(Constant.YMD, new Date())
				+ " where sdd_sdid=" + sd_id);
	}

	/**
	 * @param whereString
	 *            只和 sale,saledetail 有关
	 */
	@Override
	public void SetSaleDelivery(String where) {

		String sql = " select sd_id,sd_qty,sd_sendqty,nvl(sd_delivery,sysdate) sd_delivery from saledetail left join sale on sd_said=sa_id where "
				+ where;
		SqlRowList srs = baseDao.queryForRowSet(sql);
		double sddqty = 0, allqty = 0, needqty = 0, margin = 0, quality = 0, yqty = 0, id = 0;
		String date = null;
		while (srs.next()) {
			quality = srs.getGeneralDouble("sd_qty", 6);
			yqty = srs.getGeneralDouble("sd_sendqty", 6);
			id = srs.getInt("sd_id");
			date = srs.getString("sd_delivery").substring(0, 10);
			sddqty = baseDao.getSummaryByField("SaleDetaildet", "sdd_qty", "sdd_sdid=" + id);
			needqty = quality - yqty;
			needqty = NumberUtil.formatDouble(needqty, 6);
			try {
				if (sddqty == 0 && needqty > 0) {
					// 没有排程，默认为订单交期
					sql = "insert into SaleDetaildet(sdd_sdid,sdd_detno,sdd_qty,sdd_delivery,sdd_id)" + "values('" + id + "','1','"
							+ needqty + "',to_date('" + date + "','yyyy-MM-dd'),SaleDetaildet_SEQ.nextval)";
					baseDao.execute(sql);
				} else {
					if (needqty > sddqty) {
						// 未发货数大于已排数量，插入差异数
						margin = needqty - sddqty;
						margin = NumberUtil.formatDouble(margin, 6);
						sql = "select max(sdd_detno)sdd_detno from SaleDetaildet where sdd_sdid=" + id + " order by sdd_detno desc";
						int maxdetno = baseDao.getCount(sql);
						sql = "select sdd_detno from SaleDetaildet where sdd_sdid=" + id + " and sdd_delivery=to_date('" + date
								+ "','yyyy-MM-dd') ";
						int lastdetno = baseDao.getCount(sql);
						if (lastdetno == 0) {// 默认为订单交期
							maxdetno = maxdetno + 1;
							sql = "insert into SaleDetaildet(sdd_sdid,sdd_detno,sdd_qty,sdd_delivery,sdd_id)" + "values('" + id + "','"
									+ maxdetno + "','" + margin + "',to_date('" + date + "','yyyy-MM-dd'),SaleDetaildet_SEQ.nextval)";
						} else {// 已有此交期，累加数量
							sql = "update SaleDetaildet set sdd_qty=sdd_qty+" + margin + " where sdd_sdid=" + id + " and sdd_detno="
									+ lastdetno;
						}
						baseDao.execute(sql);
					}
					if (needqty < sddqty) {
						// 未发货数小于排程数，按照最早交期开始，冲减多余排程数
						List<String> sqls = new ArrayList<String>();
						margin = sddqty - needqty;
						margin = NumberUtil.formatDouble(margin, 6);
						List<Object[]> objects = baseDao.getFieldsDatasByCondition("SaleDetaildet", new String[] { "sdd_qty",
								"sdd_delivery", "sdd_id" }, "sdd_sdid=" + id + " order by sdd_delivery ");
						for (Object[] object : objects) {
							allqty = Double.valueOf(object[0].toString());
							if (allqty <= margin) {
								margin = margin - allqty;
								sql = "delete from SaleDetaildet where sdd_id=" + object[2];
								sqls.add(sql);
							} else {
								sql = "update SaleDetaildet set sdd_qty=sdd_qty-" + margin + " where sdd_id=" + object[2];
								sqls.add(sql);
								break;
							}
						}
						baseDao.execute(sqls);
					}
				}
			} catch (Exception ex) {
				BaseUtil.showError(ex.toString());
				return;
			}
		}
	}
}
