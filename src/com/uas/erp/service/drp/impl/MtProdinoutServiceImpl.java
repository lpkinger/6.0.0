package com.uas.erp.service.drp.impl;

import com.sun.xml.internal.ws.api.model.MEP;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.drp.MtProdinoutService;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MtProdinoutServiceImpl implements MtProdinoutService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMtProdinout(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"mtprodinout", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("mtprodiodetail_SEQ");
			map.put("mtd_id", id[i]);
			map.put("mtd_mtcode", store.get("mt_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"mtprodiodetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "mt_id", store.get("mt_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updateMtProdinoutById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"mtprodinout", "mt_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"mtprodiodetail", "mtd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("mtd_id") == null || s.get("mtd_id").equals("")
					|| s.get("mtd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("mtprodiodetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "mtprodiodetail",
						new String[] { "mtd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "mt_id", store.get("mt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteMtProdinout(int mt_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mt_id);
		if("MainTain!Out".equals(caller)){
			List<Object[]> mtd = baseDao.getFieldsDatasByCondition("MTPRODIODETAIL", new String[]{"mtd_incode","mtd_mtno","mtd_qty"}, "mtd_mtid="+mt_id);
			for(Object[] det:mtd){
				baseDao.updateByCondition("MTPRODIODETAIL", "mtd_yqty=mtd_yqty-"+det[2], "mtd_mtcode='"+det[0]+"' and mtd_mtno='"+det[1]+"'");
			}
		}
		// 删除purchase
		baseDao.deleteById("mtprodinout", "mt_id", mt_id);
		// 删除purchaseDetail
		baseDao.deleteById("mtprodiodetail", "mtd_mtid", mt_id);
		// 记录操作
		baseDao.logger.delete(caller, "mt_id", mt_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, mt_id);
	}

	@Override
	public void auditMtProdinout(int mt_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("mtprodinout",
				"mt_statuscode", "mt_id=" + mt_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, mt_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"mtprodinout",
				"mt_statuscode='AUDITED',mt_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',mt_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',mt_AUDITDATE=sysdate", "mt_id=" + mt_id);
		// 记录操作
		baseDao.logger.audit(caller, "mt_id", mt_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, mt_id);

	}

	@Override
	public void resAuditMtProdinout(int mt_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("mtprodinout",
				"mt_statuscode", "mt_id=" + mt_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, mt_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"mtprodinout",
				"mt_statuscode='ENTERING',mt_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',mt_AUDITMAN='',mt_AUDITDATE=null", "mt_id="
						+ mt_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "mt_id", mt_id);
		handlerService.afterResAudit(caller, mt_id);
	}

	@Override
	public void submitMtProdinout(int mt_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("mtprodinout",
				"mt_statuscode", "mt_id=" + mt_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mt_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"mtprodinout",
				"mt_statuscode='COMMITED',mt_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "mt_id="
						+ mt_id);
		// 记录操作
		baseDao.logger.submit(caller, "mt_id", mt_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, mt_id);
	}

	@Override
	public void resSubmitMtProdinout(int mt_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("mtprodinout",
				"mt_statuscode", "mt_id=" + mt_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, mt_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"mtprodinout",
				"mt_statuscode='ENTERING',mt_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "mt_id="
						+ mt_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mt_id", mt_id);
		handlerService.afterResSubmit(caller, mt_id);
	}
	
	public String maintainInToOut(String caller,String data){
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		SqlRowList rs = null;
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_mtqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_inqty FROM ProdIODetail WHERE pd_id=? and pd_inqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],出库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_inqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}	
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			int detno = 1;
			for (Map<Object, Object> map : maps) {
				Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + map.get("pd_id"));
				Map<String, Object> diffence = new HashMap<String, Object>();
				int outid = baseDao.getSeqId("PRODINOUT_SEQ");
				String outcode = baseDao.sGetMaxNumber("PRODINOUT", 2);
				diffence.put("pi_id", outid);
				diffence.put("pi_inoutno", "'" + outcode + "'");
				diffence.put("pi_class", "'维修出库单'");
				diffence.put("pi_recorddate", "sysdate");
				diffence.put("pi_date", "sysdate");
				diffence.put("pi_recordman", "'" + employee.getEm_name() + "'");
				diffence.put("pi_invostatus", "'在录入'");
				diffence.put("pi_invostatuscode", "'ENTERING'");
				diffence.put("pi_updateman", "null");
				diffence.put("pi_updatedate", "null");
				diffence.put("pi_auditman", "null");
				diffence.put("pi_auditdate", "null");
				diffence.put("pi_inoutman", "null");
				diffence.put("pi_date1", "null");
				diffence.put("pi_status", "'未过账'");
				diffence.put("pi_statuscode", "'UNPOST'");
				diffence.put("pi_printstatus", "null");
				diffence.put("pi_printstatuscode", "null");
				// 转入主表
				baseDao.copyRecord("Prodinout", "Prodinout", "pi_id=" + pi_id, diffence);
				Object[] pi = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + outid);
				Object newid = baseDao.getSeqId("PRODIODETAIL_SEQ");
				Map<String, Object> diffenceDet = new HashMap<String, Object>();
				diffenceDet.put("pd_piid", outid);
				diffenceDet.put("pd_inoutno", "'" + pi[2] + "'");
				diffenceDet.put("pd_piclass", "'维修出库单'");
				diffenceDet.put("pd_inqty", 0);
				diffenceDet.put("pd_yqty", 0);
				diffenceDet.put("pd_status", 0);
				diffenceDet.put("pd_ioid", ""+map.get("pd_id")+"");
				diffenceDet.put("pd_pdno", detno++);
				diffenceDet.put("pd_id", newid);
				diffenceDet.put("pd_ordercode", "'"+map.get("pd_inoutno")+"'");
				diffenceDet.put("pd_orderdetno", ""+map.get("pd_pdno")+"");
				baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" +map.get("pd_id"), diffenceDet);
				baseDao.updateByCondition("prodiodetail", "pd_yqty=pd_yqty+"+map.get("pd_mtqty"), "pd_id="+map.get("pd_id"));
				baseDao.updateByCondition("prodiodetail", "pd_outqty=pd_outqty+"+map.get("pd_mtqty"), "pd_id="+newid);
				baseDao.logger.turnDetail("转维修出库单", caller, "pi_id",map.get("pd_piid"),  map.get("pd_pdno") + ",数量：" + map.get("pd_mtqty"));
				sb.append("<br>转入成功,维修出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=Maintain!Out&formCondition=pi_idIS" + outid
				+ "&gridCondition=pd_piidIS" + outid + "')\">" + outcode + "</a>&nbsp;");
			}
		}
		return sb.toString();
	}
}
