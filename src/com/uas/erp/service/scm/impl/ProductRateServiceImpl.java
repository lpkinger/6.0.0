package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ProductRateService;

@Service("productRateService")
public class ProductRateServiceImpl implements ProductRateService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductRate(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductRate", "pdr_code='" + store.get("pdr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存SalePrice
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductRate", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SalePriceDetail
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			map.put("pdrd_id", baseDao.getSeqId("PRODUCTRATEDETAIL_SEQ"));
			map.put("pdrd_status", BaseUtil.getLocalMessage("UNVALID"));
			map.put("pdrd_statuscode", "UNVALID");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProductRateDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pdr_id", store.get("pdr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteProductRate(int pdr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + pdr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pdr_id });
		// 删除SalePrice
		baseDao.deleteById("ProductRate", "pdr_id", pdr_id);
		// 删除SalePriceDetail
		baseDao.deleteById("ProductRatedetail", "pdrd_pdrid", pdr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pdr_id", pdr_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pdr_id });
	}

	@Override
	public void updateProductRateById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + store.get("pdr_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改SalePrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductRate", "pdr_id");
		baseDao.execute(formSql);
		// 修改SalePriceDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductRateDetail", "pdrd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pdrd_id") == null || s.get("pdrd_id").equals("") || s.get("pdrd_id").equals("0")
					|| Integer.parseInt(s.get("pdrd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTRATEDETAIL_SEQ");
				s.put("pdrd_status", BaseUtil.getLocalMessage("UNVALID"));
				s.put("pdrd_statuscode", "UNVALID");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductRateDetail", new String[] { "pdrd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pdr_id", store.get("pdr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printProductRate(int pdr_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pdr_id });
		// 执行打印操作
		baseDao.logger.post(caller, "pdr_id", pdr_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pdr_id });
	}

	@Override
	public void auditProductRate(int pdr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + pdr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pdr_id });
		baseDao.updateByCondition("ProductRateDetail", "pdrd_statuscode='VALID',pdrd_status='" + BaseUtil.getLocalMessage("VALID") + "'",
				"pdrd_pdrid=" + pdr_id);
		// 执行审核操作
		baseDao.audit("ProductRate", "pdr_id=" + pdr_id, "pdr_status", "pdr_statuscode", "pdr_auditdate", "pdr_auditman");
		// 自动失效同物料同单价的费用比例
		if (baseDao.isDBSetting(caller, "autoProductRate")) {
			StringBuffer sb = new StringBuffer();
			SqlRowList rs2 = baseDao
					.queryForRowSet(
							"select pdrd_prodcode, pdrd_saleprice,pdr_custid from ProductRateDetail,ProductRate where pdrd_pdrid=pdr_id and pdrd_pdrid=? and pdrd_statuscode='VALID' and nvl(pdrd_rate,0)=0",
							pdr_id);
			while (rs2.next()) {
				SqlRowList rs1 = baseDao
						.queryForRowSet(
								"select pdrd_id, pdr_code, pdr_id, pdrd_detno from ProductRate left join ProductRateDetail on pdrd_pdrid=pdr_id where pdrd_statuscode='VALID' and pdrd_prodcode=? and pdrd_saleprice=? and pdr_custid=? and pdr_id<>?",
								rs2.getObject("pdrd_prodcode"), rs2.getGeneralDouble("pdrd_saleprice"), rs2.getGeneralInt("pdr_custid"),
								pdr_id);
				while (rs1.next()) {
					baseDao.updateByCondition("ProductRateDetail",
							"pdrd_statuscode='UNVALID',pdrd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'",
							"pdrd_id=" + rs1.getGeneralInt("pdrd_id"));
					sb.append("费用比例原编号为<a href=\"javascript:openUrl('jsps/scm/sale/productRate.jsp?formCondition=pdr_idIS"
							+ rs1.getGeneralInt("pdr_id") + "&gridCondition=pdrd_pdridIS" + rs1.getGeneralInt("pdr_id")
							+ "&whoami=ProductRate')\">" + rs1.getObject("pdr_code") + "</a>&nbsp;第" + rs1.getGeneralInt("pdrd_detno")
							+ "行数据已自动失效!<hr>");
				}
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "pdr_id", pdr_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pdr_id });
	}

	@Override
	public void resAuditProductRate(int pdr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + pdr_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.updateByCondition("ProductRateDetail", "pdrd_statuscode='UNVALID',pdrd_status='" + BaseUtil.getLocalMessage("UNVALID")
				+ "'", "pdrd_pdrid=" + pdr_id);
		// 执行反审核操作
		baseDao.resOperate("ProductRate", "pdr_id=" + pdr_id, "pdr_status", "pdr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pdr_id", pdr_id);
	}

	@Override
	public void submitProductRate(int pdr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + pdr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pdr_id });
		// 执行提交操作
		baseDao.submit("ProductRate", "pdr_id=" + pdr_id, "pdr_status", "pdr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pdr_id", pdr_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pdr_id });
	}

	@Override
	public void resSubmitProductRate(int pdr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductRate", "pdr_statuscode", "pdr_id=" + pdr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pdr_id });
		// 执行反提交操作
		baseDao.resOperate("ProductRate", "pdr_id=" + pdr_id, "pdr_status", "pdr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pdr_id", pdr_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pdr_id });
	}
}
