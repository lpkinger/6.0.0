package com.uas.erp.service.scm.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.MachineNoService;

@Service("machineNoService")
public class MachineNoServiceImpl implements MachineNoService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<?> getProdioMachine(int piid, boolean iswcj) {
		String sql = "SELECT PIID, INOUTNO, PRCODE,QTY,nvl(YQTY,0) YQTY, PR_DETAIL PRNAME, PR_SPEC PRSPEC, QTY-nvl(YQTY,0) DCJQTY "
				+ "FROM ((Select pd_piid piid, pd_inoutno inoutno,pd_prodcode PRCODE, sum(nvl(pd_inqty,0)+nvl(pd_outqty,0)) QTY "
				+ "from prodiodetail where pd_piid=? group by pd_piid, pd_inoutno,pd_prodcode) left join product on pr_code=PRCODE "
				+ "left join (select pim_piid,pim_prodcode,count(1) YQTY from prodiomac group by pim_piid,pim_prodcode) "
				+ "on pim_piid=piid and pim_prodcode=PRCODE)";
		if (iswcj) {
			sql = "SELECT PIID, INOUTNO, PRCODE,QTY,nvl(YQTY,0) YQTY, PR_DETAIL PRNAME, PR_SPEC PRSPEC, QTY-nvl(YQTY,0) DCJQTY "
					+ "FROM ((Select pd_piid piid, pd_inoutno inoutno,pd_prodcode PRCODE, sum(nvl(pd_inqty,0)+nvl(pd_outqty,0)) QTY "
					+ "from prodiodetail where pd_piid=? group by pd_piid, pd_inoutno,pd_prodcode) left join product on pr_code=PRCODE "
					+ "left join (select pim_piid,pim_prodcode,count(1) YQTY from prodiomac group by pim_piid,pim_prodcode) "
					+ "on pim_piid=piid and pim_prodcode=PRCODE) where QTY>nvl(YQTY,0)";
		}
		SqlRowList list = baseDao.queryForRowSet(sql, piid);
		return list.getResultList();
	}

	@Override
	public void insertProdioMac(int piid, String inoutno, String machineno, String prcode, int qty) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class,pi_status from ProdInOut where pi_id=" + piid);
		if (rs.next()) {
			if ("POSTED".equals(rs.getObject("pi_status"))) {
				BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已过账，不能采集！");
			} else {
				int count = baseDao.getCount("select count(*) from PRODIOMAC where PIM_PRODCODE= '" + prcode + "' and PIM_PIID=" + piid);
				if (count + 1 > qty) {
					BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已采集数不能大于总数量！");
				}
				count = baseDao.getCount("select count(*) from PRODIOMAC where PIM_MAC='" + machineno + "' and PIM_PIID=" + piid);
				if (count > 0) {
					BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]，机器号[" + machineno + "]已采集！");
				} else {
					baseDao.execute(
							"INSERT INTO PRODIOMAC(PIM_ID, PIM_PRODCODE, PIM_INDATE, PIM_MAC, PIM_INOUTNO, PIM_PIID) values (?,?,sysdate,?,?,?)",
							new Object[] { baseDao.getSeqId("PRODIOMAC_SEQ"), prcode, machineno, inoutno, piid });
				}
			}
		}
	}

	@Override
	public void deleteProdioMac(int piid, String inoutno, String machineno, String prcode) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class,pi_status from ProdInOut where pi_id=" + piid);
		if (rs.next()) {
			if ("POSTED".equals(rs.getObject("pi_status"))) {
				BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已过账，不能取消机器号！");
			} else {
				int count = baseDao.getCount("select count(*) from PRODIOMAC where PIM_MAC='" + machineno + "' and PIM_PIID=" + piid);
				if (count > 0) {
					baseDao.execute("delete from PRODIOMAC where PIM_PIID=" + piid + " and PIM_MAC='" + machineno + "'");
				} else {
					BaseUtil.showError("要取消的机器号[" + machineno + "]不存在！");
				}
			}
		}
	}

	@Override
	public void clearProdioMac(int piid, String prcode) {
		baseDao.execute("delete from PRODIOMAC where PIM_PIID=" + piid + " and PIM_PRODCODE='" + prcode + "'");
	}
}
