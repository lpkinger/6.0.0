package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.VendProductLossSetService;

@Service
public class VendProductLossSetServiceImpl implements VendProductLossSetService {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveVendProductLossSet(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> prodmap : prodgrid) {
			int prodid = baseDao.getSeqId("VendProdLossSetDet_SEQ");
			prodmap.put("vpd_id", prodid);
		}
		sqls.add(SqlUtil.getInsertSqlByMap(store, "VendProdLossSet"));
		// 保存从表
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(prodgrid, "VendProdLossSetDet"));
		baseDao.execute(sqls);	
		baseDao.logger.save(caller, "vps_id", store.get("vps_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateVendProductLossSetById(String formStore, String param, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		handlerService.handler(caller, "save", "before", new Object[]{store});
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "VendProdLossSet", "vps_id"));
		if (prodgrid.size() > 0) {
			for (Map<Object, Object> s : prodgrid) {
				if (s.get("vpd_id") == null || s.get("vpd_id").equals("") || s.get("vpd_id").equals("0")
						|| Integer.parseInt(s.get("vpd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("VendProdLossSetDet_SEQ");
					s.put("vpd_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "VendProdLossSetDet", new String[] { "vpd_id" }, new Object[] { id });
					sqls.add(sql);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(prodgrid, "VendProdLossSetDet", "vpd_id"));
		}
		baseDao.execute(sqls);
		//记录操作
		baseDao.logger.update(caller, "vps_id", store.get("vps_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	

	}

	@Override
	public void deleteVendProductLossSet(int vps_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{vps_id});
		//删除主表内容
		baseDao.deleteById("VendProdLossSet", "vps_id", vps_id);
		baseDao.logger.delete(caller, "vps_id", vps_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{vps_id});
	

	}

	@Override
	public String auditVendProductLossSet(int vps_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("VendProdLossSet", "vps_statuscode", "vps_id=" + vps_id);
		StateAssert.auditOnlyCommited(status);
		Employee employee = SystemSession.getUser();
		handlerService.beforeAudit("VendProdLossSet", vps_id);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{vps_id});
		
		//执行审核操作
		baseDao.audit("VendProdLossSet", "vps_id=" + vps_id, "vps_status", "vps_statuscode", "vps_auditdate", "vps_auditer");
		//记录操作
		baseDao.logger.audit(caller, "vps_id", vps_id);
		//已经存在的委外损耗率做更新处理，不存在的则插入
		List<Object[]> os = baseDao.getFieldsDatasByCondition("VendProdLossSetDet", new String[]{"vpd_prodcode","vpd_osrate","vpd_id","VPD_Vendcode"}, "vpd_vpsid="+vps_id+" order by vpd_detno");
		for(Object[] o : os){
			SqlRowList rs = baseDao.queryForRowSet("select * from Product where pr_code='"+o[0]+"'");
			if(!rs.hasNext()){
				baseDao.execute("Insert into vendprodloss (vpl_id,vpl_code,vpl_oslossrate,vpl_vendcode,vPL_INDATE,vPL_RECORDMAN) select VendProductLoss_SEQ.nextval,vpd_prodcode,vpd_osrate,vpd_vendcode,sysdate,'"+employee.getEm_name()+"' from VendProdLossSetDet where vpd_id="+o[2]);
			}else if(rs.next()){
				baseDao.execute("update vendprodloss set vpl_oslossrate ='"+o[1]+"' where vpl_code='"+o[0]+"'");
			}
			//baseDao.execute("update product set pr_exportlossrate = '"+o[1]+"' where pr_code='"+o[0]+"'");
		}
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{vps_id});
		return "委外物料损耗设置成功!";
	
	}

	@Override
	public void resAuditVendProductLossSet(int vps_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendProdLossSet", "vps_statuscode", "vps_id=" + vps_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("VendProdLossSet", "vps_id=" + vps_id, "vps_status", "vps_statuscode", "vps_auditdate", "vps_auditor");
		baseDao.resOperate("VendProdLossSet", "vps_id=" + vps_id, "vps_status", "vps_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "vps_id", vps_id);
	}

	@Override
	public void submitVendProductLossSet(int vps_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendProdLossSet", "vps_statuscode", "vps_id=" + vps_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { vps_id });
		//List<Object[]> grid = baseDao.getFieldsDatasByCondition("VendProdLossSetDet", new String[]{"vpd_prodcode","vpd_detno"}, "vpd_vpsid="+vps_id);
		// 执行提交操作
		baseDao.submit("VendProdLossSet", "vps_id=" + vps_id, "vps_status", "vps_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "vps_id", vps_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { vps_id });
	}

	@Override
	public void resSubmitVendProductLossSet(int vps_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendProdLossSet", "vps_statuscode", "vps_id=" + vps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { vps_id });
		// 执行反提交操作
		baseDao.resOperate("VendProdLossSet", "vps_id=" + vps_id, "vps_status", "vps_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "vps_id", vps_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { vps_id });
	}

}
