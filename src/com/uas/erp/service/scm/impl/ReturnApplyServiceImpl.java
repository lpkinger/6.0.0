package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.ReturnApplyDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.ReturnApplyService;


@Service("returnApplyService")
public class ReturnApplyServiceImpl implements ReturnApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ReturnApplyDao returnApplyDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Override
	public void saveReturnApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存ReturnApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ReturnApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存ReturnApplyDetail
		for(Map<Object, Object> s:grid){
			s.put("rad_id", baseDao.getSeqId("RETURNAPPLYDETAIL_SEQ"));
			s.put("rad_statuscode", "ENTERING");
			s.put("rad_status", Status.ENTERING.display());
			Object qty = s.get("rad_qty");
			Object price = s.get("rad_orderprice");
			if(qty != null && price != null){
				s.put("rad_amount", Float.parseFloat(qty.toString()) * Float.parseFloat(price.toString()));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ReturnApplyDetail");
		baseDao.execute(gridSql);
		/**
		 * maz 保存后自动抓去客户物料编号、客户物料名称、客户物料规格  2017080360
		 */
		baseDao.execute("update ReturnApplyDetail a set (rad_custprodcode,rad_custproddetail,rad_custprodspec)=(select pc_custprodcode,pc_custproddetail,pc_custprodspec from productcustomer where a.rad_prodcode=pc_prodcode and pc_custcode='"+store.get("ra_custcode")+"') where rad_raid="+store.get("ra_id")+"");
		baseDao.logger.save(caller, "ra_id", store.get("ra_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	
	@Override
	public void deleteReturnApply(int ra_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.delOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and nvl(rad_yqty,0)>0", String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转销售退货单，不允许删除!行号：" + dets);
		}
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ra_id});
		//删除ReturnApply
		baseDao.deleteById("ReturnApply", "ra_id", ra_id);
		//删除ReturnApplyDetail
		baseDao.deleteById("returnapplydetail", "rad_raid", ra_id);
		//记录操作
		baseDao.logger.delete(caller, "ra_id", ra_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ra_id});
	}
	
	@Override
	public void updateReturnApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + store.get("ra_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改ReturnApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ReturnApply", "ra_id");
		baseDao.execute(formSql);
		//修改ReturnApplyDetail
		for(Map<Object, Object> s:gstore){
			Object qty = s.get("rad_qty");
			Object price = s.get("rad_orderprice");
			if(qty != null && price != null){
				s.put("rad_amount", Float.parseFloat(qty.toString()) * Float.parseFloat(price.toString()));
			}
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ReturnApplyDetail", "rad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rad_id") == null || s.get("rad_id").equals("") || s.get("rad_id").equals("0") ||
					Integer.parseInt(s.get("rad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("RETURNAPPLYDETAIL_SEQ");
				s.put("rad_statuscode", "ENTERING");
				s.put("rad_status", Status.ENTERING.display());
				String sql = SqlUtil.getInsertSqlByMap(s, "ReturnApplyDetail", new String[]{"rad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		/**
		 * maz 保存后自动抓去客户物料编号、客户物料名称、客户物料规格  2017080360
		 */
		baseDao.execute("update ReturnApplyDetail a set (rad_custprodcode,rad_custproddetail,rad_custprodspec)=(select pc_custprodcode,pc_custproddetail,pc_custprodspec from productcustomer where a.rad_prodcode=pc_prodcode and pc_custcode='"+store.get("ra_custcode")+"') where rad_raid="+store.get("ra_id")+"");
		//记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	
	@Override
	public String[] printReturnApply(int ra_id, String reportName, String condition, String caller)  {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { ra_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		//执行打印操作
		//记录操作
		baseDao.logger.print(caller, "ra_id", ra_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ra_id});
		return keys;
	}
	
	@Override
	public void auditReturnApply(int ra_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and not exists (select sd_code,sd_detno from saledetail where sd_code=rad_ordercode and sd_detno=rad_orderdetno and sd_statuscode='AUDITED') and nvl(rad_ordercode,' ')<>' '",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("销售单号+销售序号不存在或者状态不等于已审核，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and not exists(select sd_code,sd_detno,sd_prodcode from saledetail where sd_code=rad_ordercode and sd_detno=rad_orderdetno and sd_prodcode=rad_prodcode) and nvl(rad_ordercode,' ')<>' '",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("销售单号+销售序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_custcode,' ')<> nvl(sa_custcode,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据客户同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_paymentscode,' ')<> nvl(sa_paymentscode,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据收款方式同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_currency,' ')<> nvl(sa_currency,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据币别同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ra_id});
		//执行审核操作
		baseDao.audit("ReturnApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
		baseDao.audit("ReturnApplyDetail", "rad_raid=" + ra_id, "RAD_STATUS", "rad_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ra_id});
	}
	
	@Override
	public void resAuditReturnApply(int ra_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and nvl(rad_yqty,0)>0", String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转销售退货单，不允许反审核!行号：" + dets);
		}
		//执行反审核操作
		baseDao.resAudit("ReturnApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
		baseDao.resOperate("ReturnApplyDetail", "rad_raid=" + ra_id, "RAD_STATUS", "rad_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ra_id", ra_id);
	}
	
	@Override
	public void submitReturnApply(int ra_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and not exists(select sd_code,sd_detno from saledetail where sd_code=rad_ordercode and sd_detno=rad_orderdetno and sd_statuscode='AUDITED') and nvl(rad_ordercode,' ')<>' '",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("销售单号+销售序号不存在或者状态不等于已审核，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail where rad_raid=? and not exists (select sd_code,sd_detno,sd_prodcode from saledetail where sd_code=rad_ordercode and sd_detno=rad_orderdetno and sd_prodcode=rad_prodcode) and nvl(rad_ordercode,' ')<>' '",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("销售单号+销售序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_custcode,' ')<> nvl(sa_custcode,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据客户同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_paymentscode,' ')<> nvl(sa_paymentscode,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据收款方式同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rad_detno) from ReturnApplyDetail left join ReturnApply on rad_raid=ra_id left join Sale on rad_ordercode=sa_code where rad_raid=? and nvl(rad_ordercode,' ')<>' ' and nvl(ra_currency,' ')<> nvl(sa_currency,' ')",
						String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("单据币别同明细行订单客户不一致，不允许进行当前操作!行号：" + dets);
		}
		//退货申请单提交添加限制:可退货数量=已出货数量-未过账销售退货单数量-状态不为在录入的退货申请单的未转数量   2017110225  maz
		StringBuffer sb = new StringBuffer();
		List<Object[]> sale = baseDao.getFieldsDatasByCondition("ReturnApplyDetail", new String[]{"rad_ordercode","rad_orderdetno","rad_qty","rad_detno"}, "rad_raid="+ra_id);
		for(Object[] a:sale){
			if (StringUtil.hasText(a[0])&&StringUtil.hasText(a[1])) {
				int dCount = 0;//可退货数
				//已出货数
				Integer aC = baseDao.getFieldValue("Sale left join Saledetail on sa_id=sd_said", "nvl(sd_sendqty,0)sd_sendqty", "sa_code='"+a[0]+"' and sd_detno="+a[1]+"",Integer.class);
				//未过账销售退货数量
				Integer bC = baseDao.getFieldValue("ProdInOut left join ProdIODetail on pi_id=pd_piid", "nvl(sum(pd_inqty),0)pd_inqty", "pd_ordercode='"+a[0]+"' and pd_orderdetno="+a[1]+" and pi_status<>'已过账'",Integer.class);
				//状态不为在录入的退货申请
				Integer cC = baseDao.getFieldValue("ReturnApply left join ReturnApplyDetail on ra_id=rad_raid", "nvl(sum(rad_qty)-sum(rad_yqty),0)rad_qty", "rad_ordercode='"+a[0]+"' and rad_orderdetno="+a[1]+" and ra_status<>'在录入' and ra_id<>"+ra_id+"",Integer.class);
				aC = aC == null ? 0 : aC;
				bC = bC == null ? 0 : bC;
				cC = cC == null ? 0 : cC;
				dCount = aC-bC-cC;
				if(Integer.parseInt(a[2].toString())>dCount){
					sb.append("行"+a[3]+"本次退货数量不能大于可退货数量，可退货数量"+dCount+"!<hr>");
				}
			}
		}
		if(sb.length()>0){
			BaseUtil.showError(sb.toString());
		}
		//修改金额
		baseDao.updateByCondition("ReturnApplyDetail", "rad_amount=rad_qty*rad_orderprice", "rad_raid=" + ra_id);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ra_id});
		//执行提交操作
		baseDao.submit("ReturnApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		baseDao.submit("ReturnApplyDetail", "rad_raid=" + ra_id, "RAD_STATUS", "rad_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ra_id", ra_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ra_id});
	}
	
	@Override
	public void resSubmitReturnApply(int ra_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ReturnApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ra_id});
		//执行反提交操作
		baseDao.resOperate("ReturnApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		baseDao.resOperate("ReturnApplyDetail", "rad_raid=" + ra_id, "RAD_STATUS", "rad_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ra_id", ra_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ra_id});
	}
	
	static final String CHECK_YQTY = "SELECT ra_code,rad_detno,rad_qty,rad_yqty FROM ReturnApplyDetail left join ReturnApply on ra_id=rad_raid WHERE rad_id=? and rad_qty-nvl(rad_yqty,0)<?";
	/**
	 * 通知单转退货
	 */
	@Override
	public String turnReturn(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = null;
		for (Map<Object, Object> map : maps) {
			int radid = Integer.parseInt(map.get("rad_id").toString());
			double tqty = Double.parseDouble(map.get("rad_thisqty").toString());
			rs = baseDao.queryForRowSet(CHECK_YQTY, radid, tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],退货申请单号:").append(rs.getString("ra_code")).append(",行号:")
						.append(rs.getInt("rad_detno")).append(",数量:").append(rs.getDouble("rad_qty"))
						.append(",已转数量:").append(rs.getDouble("rad_yqty")).append(",本次数:").append(tqty);
				BaseUtil.showError(sb.toString());
			}
		}
		if (maps.size() > 0) {
			String ids = CollectionUtil.pluckSqlString(maps, "rad_id");
			// 存在未审批变更单
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(rad_detno) from (select distinct rad_detno from ReturnApplyDetail where nvl(rad_ordercode,' ')<>' ' and rad_id in("
									+ ids
									+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=rad_ordercode and rad_orderdetno=scd_sddetno and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' or sc_type<>'交期变更')))",
							String.class);
			if (dets != null) {
				BaseUtil.showError("明细行订单存在待审批的销售变更单，不能进行转出操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat('订单号：'||rad_ordercode||'订单行号：'||rad_orderdetno) from ReturnApplyDetail where rad_id in ("
							+ ids + ") and not exists (select sd_code, sd_detno from saledetail where sd_code=rad_ordercode and sd_detno=rad_orderdetno and sd_statuscode='AUDITED') and nvl(rad_ordercode,' ')<>' '", String.class);
			if (dets != null) {
				BaseUtil.showError("明细行订单状态不等于已审核，不能进行转出操作!" + dets);
			}
			// 转入通知单主记录
			Integer ra_id = Integer.parseInt(maps.get(0).get("rad_raid").toString());
			Key key = transferRepository.transfer(caller, ra_id);
			int pi_id = key.getId();
			// 转入明细
			transferRepository.transfer(caller, maps, key);
			baseDao.execute("update PRODIODETAIL set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_piid=?", pi_id);
			baseDao.execute("update PRODIODETAIL set pd_pocode=(SELECT sa_pocode FROM sale WHERE sa_code=pd_ordercode) where pd_piid=?", pi_id);
			baseDao.execute("update PRODIODETAIL set (pd_whcode, pd_whname)=(SELECT pi_whcode, pi_whname FROM ProdInOut WHERE pd_piid=pi_id) where pd_piid=?", pi_id);
			baseDao.execute("update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?", pi_id);
			// 修改销售退货申请单状态
			for (Map<Object, Object> map : maps) {
				int radid = Integer.parseInt(map.get("rad_id").toString());
				returnApplyDao.checkRADQty(radid);
			}
			handlerService.handler(caller, "turn", "after", new Object[]{pi_id});
			return "转入成功,销售退货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
					+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!SaleReturn')\">" + key.getCode() + "</a>&nbsp;<hr>";
		}
		return null;
	}
}
