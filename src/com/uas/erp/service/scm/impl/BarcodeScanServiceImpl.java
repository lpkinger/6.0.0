package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.service.common.ReportService;
import com.uas.erp.service.scm.BarcodeScanService;

@Service
public class BarcodeScanServiceImpl implements BarcodeScanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private ReportService reportService;

	@Override
	public List<?> getProdioBarcode(int piid, boolean iswcj) {
		String sql = "SELECT PIID, INOUTNO, PRCODE,QTY,nvl(YQTY,0) YQTY, PR_DETAIL PRNAME, PR_SPEC PRSPEC, QTY-nvl(YQTY,0) DCJQTY "
				+ "FROM ((Select pd_piid piid, pd_inoutno inoutno,pd_prodcode PRCODE, sum(nvl(pd_inqty,0)+nvl(pd_outqty,0)) QTY "
				+ "from prodiodetail where pd_piid=? group by pd_piid, pd_inoutno,pd_prodcode) left join product on pr_code=PRCODE "
				+ "left join (select pib_piid,pib_prodcode,count(1) YQTY from PRODIOBARCODE group by pib_piid,pib_prodcode) "
				+ "on pib_piid=piid and pib_prodcode=PRCODE)";
		if (iswcj) {
			sql = "SELECT PIID, INOUTNO, PRCODE,QTY,nvl(YQTY,0) YQTY, PR_DETAIL PRNAME, PR_SPEC PRSPEC, QTY-nvl(YQTY,0) DCJQTY "
					+ "FROM ((Select pd_piid piid, pd_inoutno inoutno,pd_prodcode PRCODE, sum(nvl(pd_inqty,0)+nvl(pd_outqty,0)) QTY "
					+ "from prodiodetail where pd_piid=? group by pd_piid, pd_inoutno,pd_prodcode) left join product on pr_code=PRCODE "
					+ "left join (select pib_piid,pib_prodcode,count(1) YQTY from PRODIOBARCODE group by pib_piid,pib_prodcode) "
					+ "on pib_piid=piid and pib_prodcode=PRCODE) where QTY>nvl(YQTY,0)";
		}
		SqlRowList list = baseDao.queryForRowSet(sql, piid);
		return list.getResultList();
	}

	@Override
	public void insertProdioBarcode(int piid, String inoutno, String lotNo,String DateCode,String remark, String prcode, int qty) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class,pi_status from ProdInOut where pi_id=" + piid);
		if (rs.next()) {
			if ("POSTED".equals(rs.getObject("pi_status"))) {
				BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已过账，不能采集！");
			} else {
				int count = baseDao.getCount("select count(*) from PRODIOBARCODE where PIB_PRODCODE= '" + prcode + "' and PIB_PIID=" + piid);
				if (count + 1 > qty) {
					BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已采集数不能大于总数量！");
				}
				count = baseDao.getCount("select count(*) from PRODIOBARCODE where PIB_LOTNO='" + lotNo + "' and PIB_PIID=" + piid);
				if (count > 0) {
					BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]，lotNo.[" + lotNo + "]已采集！");
				} else {
					baseDao.execute("Insert into PRODIOBARCODE (PIB_ID,PIB_PRODCODE,PIB_INDATE,PIB_INOUTNO,PIB_PIID,PIB_LOTNO,"
							+ "PIB_DATECODE,PIB_REMARK) values (?,?,sysdate,?,?,?,?,?)",
							new Object[] { baseDao.getSeqId("PRODIOMAC_SEQ"), prcode,inoutno,piid, lotNo,DateCode ,remark  });
				}
			}
		}
	}

	@Override
	public void deleteProdioBarcode(int piid, String inoutno, String lotNo, String prcode) {
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_class,pi_status from ProdInOut where pi_id=" + piid);
		if (rs.next()) {
			if ("POSTED".equals(rs.getObject("pi_status"))) {
				BaseUtil.showError(rs.getObject("pi_class") + "[" + rs.getObject("pi_inoutno") + "]已过账，不能取消机器号！");
			} else {
				int count = baseDao.getCount("select count(*) from PRODIOBARCODE where PIB_LOTNO='" + lotNo + "' and PIB_PIID=" + piid);
				if (count > 0) {
					baseDao.execute("delete from PRODIOBARCODE where PIB_PIID=" + piid + " and PIB_LOTNO='" + lotNo + "'");
				} else {
					BaseUtil.showError("要取消的lotNo[" + lotNo + "]不存在！");
				}
			}
		}
	}

	@Override
	public void clearProdioBarcode(int piid, String prcode) {
		baseDao.execute("delete from PRODIOBARCODE where PIB_PIID=" + piid + " and PIB_PRODCODE='" + prcode + "'");
	}

	private String getUrlQuietly(HttpServletRequest request, String innerUrl, String outerUrl) {
		if (UserAgentUtil.accessible(request, innerUrl)) {
			return innerUrl;
		}
		return outerUrl;
	}
	
	@Override
	public Map<String, Object> printBarcode(int id, String reportName,HttpServletRequest request) {
		Object tagformat =baseDao.getFieldDataByCondition("prodinout left join customer on pi_cardcode=cu_code", "cu_tagformat","pi_id="+id);
		if(tagformat==null||"".equals(tagformat)){
			BaseUtil.showError("客户资料中标签格式为空，请到客户资料中维护标签格式");
		}
		Enterprise enterprise = enterpriseDao.getEnterprise();
		String printUrl = getUrlQuietly(request, enterprise.getEn_printurl(), enterprise.getEn_Url());
		String rptName = reportService.getReportPath(tagformat.toString(), reportName);
		if(rptName == null){
			BaseUtil.showError("找不到报表文件，请通过Form维护中报表设置配置报表信息");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		Des des = new Des();
		try {
			rptName = des.toHexString(des.encrypt(rptName, "12345678")).toUpperCase();
		} catch (Exception e) {
		}
		params.put("reportname", rptName);
		params.put("whichsystem", enterprise.getEn_whichsystem());
		params.put("printUrl", printUrl);
		params.put("isbz", enterprise.getEn_Admin());
		return null;
	}
}
