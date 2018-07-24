package com.uas.erp.service.b2c.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.api.b2c_erp.seller.model.GoodsFUas;
import com.uas.api.b2c_erp.seller.model.GoodsQtyPriceUas;
import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;
import com.uas.b2c.service.seller.B2CGoodsUpAndDownService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.b2c.GoodsUpApplicationService;
import com.uas.erp.service.scm.ProductBatchUUIdService;

@Service("goodsUpApplicationService")
public class GoodsUpApplicationServiceImpl implements GoodsUpApplicationService {

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
	@Autowired
	private B2CGoodsUpAndDownService b2CGoodsUpAndDownService;

	@Override
	@Transactional
	public void saveGoodsUpApplication(String formStore, String caller,
			String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("GoodsUp",
				"gu_code='" + store.get("gu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存GoodsUp
		String formSql = SqlUtil.getInsertSqlByMap(store, "GoodsUp");
		baseDao.execute(formSql);
		// 保存GoodsDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore,
				"Goodsdetail", "gd_id");
		baseDao.execute(gridSql);
		// 税率等于币别中默认的税率
		baseDao.execute("update Goodsdetail set gd_taxrate=(select nvl(cr_taxrate,0) from currencys where cr_statuscode='CANUSE' and cr_name='RMB')"
				+ " where gd_guid=" + store.get("gu_id"));
		baseDao.execute("update Goodsdetail set gd_costprice=round(nvl(gd_price,0)/(1+nvl(gd_taxrate,0)/100),6) where gd_guid="
				+ store.get("gu_id"));
		baseDao.logger.save(caller, "gu_id", store.get("gu_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteGoodsUpApplication(int id, String caller) {
		// 只能删除在录入的单据
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id }); // 删除
		baseDao.deleteById("GoodsUp", "gu_id", id);
		// 删除Detail
		baseDao.deleteById("Goodsdetail", "gd_guid", id);
		// 记录操作
		baseDao.logger.delete(caller, "gu_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	@Transactional
	public void updateGoodsUpApplicationById(String formStore, String caller,
			String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + store.get("gu_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "GoodsUp",
				"gu_id");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore,
				"GoodsDetail", "gd_id");
		baseDao.execute(gridSql);
		// 税率等于币别中默认的税率
		baseDao.execute("update Goodsdetail set gd_taxrate=(select nvl(cr_taxrate,0) from currencys where cr_statuscode='CANUSE' and cr_name='RMB')"
				+ " where gd_guid=" + store.get("gu_id"));
		// 计算不含税单价
		baseDao.execute("update Goodsdetail set gd_costprice=round(nvl(gd_price,0)/(1+nvl(gd_taxrate,0)/100),6) where gd_guid="
				+ store.get("gu_id"));
		// 记录操作
		baseDao.logger.update(caller, "gu_id", store.get("gu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitGoodsUpApplication(int id, String caller) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//判断明细没有数据
		int cn = baseDao.getCount("select count(gd_id) from goodsdetail where gd_guid="+id);
		if(cn == 0){
			BaseUtil.showError("未维护明细，不允许提交！");
		}
		// 标准物料必须匹配，即pr_uuid值大于0；
		// 更新标准物料的平台uuid
		baseDao.execute("update goodsdetail set gd_uuid=(select pr_uuid from product where pr_code=gd_prodcode) where exists (select 1 from product where pr_code=gd_prodcode and gd_guid="
				+ id + ")");
		//判断明细uuid 数量 单价,等信息
		checkPrice(id);
		// 判断gu_whcode是否属于wh_ifb2c<>0的仓库
		/*
		 * Object ob = baseDao.getFieldDataByCondition("goodsUp", "gu_whcode",
		 * "gu_id=" + id); if (ob == null) { BaseUtil.showError("请维护平台销售仓"); }
		 * else { rs = baseDao .queryForRowSet(
		 * "select wh_code from warehouse  where nvl(wh_ifb2c,0)<>0 and wh_code='"
		 * + ob + "'"); if (!rs.next()) { BaseUtil.showError("仓库[" + ob +
		 * "]不属于平台销售仓"); } }
		 */
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("GoodsUp", "gu_id=" + id, "gu_status", "gu_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "gu_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitGoodsUpApplication(int id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { id }); // 执行反提交操作
		baseDao.resOperate("GoodsUp",  "gu_id="+ id, "gu_status", "gu_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "gu_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id });
	}

	@Override
	public void auditGoodsUpApplication(int id, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 判断明细uuid 数量 单价
		checkPrice(id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		// 执行审核操作
		baseDao.audit("GoodsUp", "gu_id=" + id, "gu_status", "gu_statuscode",
				"gu_auditdate", "gu_auditman");
		baseDao.execute("update GoodsDetail set gd_sendstatus='待上传' where gd_guid="
				+ id);
		// 审核后更新gd_barcode=gu_code||gd_detno,gd_originalqty=gd_qty
		baseDao.updateByCondition("GoodsDetail",
				"gd_barcode=(select gu_code from goodsUp where gu_id=" + id
						+ ")||gd_detno,gd_originalqty=gd_qty", "gd_guid=" + id);
		// 审核后更新物料价格，物料单位 到b2c$goodsonhand
		baseDao.execute("update b2c$goodsonhand set (go_saleprice,go_erpunit,go_usdsaleprice)=(select max(gd_price),max(gd_erpunit),max(gd_usdprice) from goodsdetail where gd_guid=?"
						+" and gd_prodcode=go_prodcode) where exists (select 1 from goodsdetail where gd_guid=? and gd_prodcode=go_prodcode)",id,id);
		// 记录操作
		baseDao.logger.audit(caller, "gu_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
		// 审核后自动上架
		upToB2C(id);
	}

	@Override
	public void resAuditGoodsUpApplication(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 判断该单据上是否上传到B2C，已上传，则不允许反审核，需要变更的话，走变更单流程
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(gd_detno) from goodsdetail where gd_guid="
						+ id + " and gd_sendstatus in('已上传','上传中')", String.class);
		if (dets != null) {
			BaseUtil.showError("存在已上传或上传中明细!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(gd_detno) from Prodinout left join prodiodetail on pi_id=pd_piid left join goodsUp on pi_sourcecode=gu_code left join goodsDetail on gd_guid=gu_id and pd_barcode=gd_barcode where gu_id=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已转拨出单，不允许反审核!行号：" + dets);
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		// 执行反审核操作
		baseDao.resAudit("GoodsUp", "gu_id=" + id, "gu_status",
				"gu_statuscode", "gu_auditdate", "gu_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "gu_id", id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	private void checkPrice(int id) {
		// 判断pr_uuid
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt  from goodsDetail where nvl(gd_uuid,' ')=' ' and gd_guid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]物料没有对应的商城标准料号");
		}
		// 2、数量大于0；
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt from goodsDetail where nvl(gd_qty,0)<=0 and gd_guid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]数量必须大于0");
		}
		//美元单价和人民币单价至少有一个填写，并且大于O
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt from goodsDetail where nvl(gd_price,0)<=0 and nvl(gd_usdprice,0)<=0 and gd_guid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]美元单价和人民币单价至少有一个大于0");
		}
		//根据单价的填写限制交期的填写
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt from goodsDetail where gd_guid=? and ((nvl(gd_price,0)>0 and nvl(gd_deliverytime,0)<=0) OR (nvl(gd_usdprice,0)>0 and nvl(gd_hkdeliverytime,0)<=0)) ",String.class,id);
		if (dets != null) {
			BaseUtil.showError("维护了对应币别的单价，必须维护相应币别的交期，序号[" + dets + "]");
		}
		//限制最小包装，最小订购量必须大于0
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(gd_detno) dt from goodsDetail where gd_guid=" + id
						+ " and (nvl(gd_minPackqty,0)<=0 or nvl(gd_minbuyqty,0)<=0 or gd_minbuyqty<gd_minPackqty)",
				String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]最小包装数、最小订购量必须大于0,并且最小订购量不允许小于最小包装数");
		}
		// 上架数量限制大于等于最小订购数
		dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(gd_detno) dt from goodsDetail where gd_guid=?"
									+ " and nvl(gd_minPackqty,0)<>0 and gd_qty<gd_minbuyqty",
							String.class,id);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]上架数量不允许小于最小订购量");
		}
		// 所有明细的生产日期必须已经维护；
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt  from goodsDetail where gd_madedate is null and gd_guid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]的生产日期没有维护！");
		}
		// 限制明细行生产日期必须小于或者等于录入日期
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt from goodsDetail left join goodsup on gd_guid=gu_id where gu_id=?"
								+ " and to_char(gd_madedate,'YYYY-mm-dd')>to_char(gu_indate,'YYYY-mm-dd')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]生产日期必须小于等于录入日期");
		}
		// 出库仓库gd_whcode必须已经维护
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) dt  from goodsDetail where nvl(gd_whcode,' ')=' ' and gd_guid="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]的出库仓库没有维护！");
		}
		// 上架商品必须有足够库存（与productwh表的 pw_prodcode和pw_whcode匹配）
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) from goodsdetail where gd_detno not in(select gd_detno from  productwh left join goodsdetail on pw_prodcode=gd_prodcode"
								+ " and pw_whcode=gd_whcode where gd_guid=?"
								+ " and pw_onhand>=gd_qty)and gd_guid=?",
						String.class, id, id);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]物料在对应的出库仓库中没有足够的库存！");
		}
		// 库存类型必须填写，并且必须是现货，呆滞库存，废料三种之一
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) from goodsdetail where gd_guid=? and (nvl(gd_original,0)=0 OR gd_original not in(1311,1312,1313)) and rownum<20",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets
					+ "]库存类型不允许为空，并且必须是[现货，呆滞库存，废料]三种之一！");
		}
		//单位必填
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) from goodsdetail where gd_guid=? and nvl(gd_erpunit,' ')=' ' and rownum<20",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets+ "]单位必填！");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void splitDetail(String formdata, String data) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int gd_id = Integer.parseInt(formmap.get("gd_id").toString());
		int guid = Integer.parseInt(formmap.get("gd_guid").toString());
		int basedetno = Integer.parseInt(formmap.get("gd_detno").toString());

		// 判断上架申请单状态,必须是在录入，已提交状态
		Object status = baseDao.getFieldDataByCondition("GoodsUp",
				"gu_statuscode", "gu_id=" + guid);
		if (status != null && status.equals("AUDITED")) {
			BaseUtil.showError("上架申请单已经审核不允许拆分！");
		}
		double baseqty = 0, splitqty = 0;
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> currentMap = new HashMap<String, Object>();
		SqlRowList cur = baseDao
				.queryForRowSet("select * from GoodsDetail where gd_id="
						+ gd_id);
		if (cur.next()) {
			// 判断拆分明细是否已转拨出单
			Object ob = baseDao
					.getFieldDataByCondition(
							"Prodinout left join prodiodetail on pi_id=pd_piid left join goodsUp on pi_sourcecode=gu_code left join goodsDetail on gd_guid=gu_id and pd_barcode=gd_barcode",
							"pi_inoutno", "gu_id=" + guid);
			if (ob != null) {
				BaseUtil.showError("该明细行已经转拨出单[" + ob.toString() + "]不允许拆分！");
			}
			currentMap = cur.getCurrentMap();
			baseqty = cur.getDouble("gd_qty");
		} else {
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		}
		SqlRowList sl = baseDao
				.queryForRowSet("select max(gd_detno) from goodsDetail where gd_guid="
						+ guid);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		List<Map<Object, Object>> gridmaps = BaseUtil
				.parseGridStoreToMaps(data);
		Object gdid = null, gdwhcode = null;
		int gdqty = 0, gddetno = 0;
		double gdprice = 0, minbuyqty = 0;
		SqlRowList sl2 = null;
		// 判断总数量是否与拆分前一致
		for (Map<Object, Object> map : gridmaps) {
			splitqty = NumberUtil.add(splitqty,
					Double.parseDouble(map.get("gd_qty").toString()));
		}
		if (splitqty != baseqty) {
			BaseUtil.showError("拆分后的总数跟当前序号总数不一致!");
		}
		// 判断原始的序号 值不能
		for (Map<Object, Object> map : gridmaps) {
			gdid = map.get("gd_id");
			gddetno = Integer.parseInt(map.get("gd_detno").toString());
			gdqty = Integer.parseInt(map.get("gd_qty").toString());
			gdprice = Double.valueOf(map.get("gd_price").toString());
			gdwhcode = map.get("gd_whcode").toString();
			minbuyqty = Double.valueOf(map.get("gd_minbuyqty").toString());
			if (gdid != null && Integer.parseInt(gdid.toString()) != 0) {
				sl2 = baseDao
						.queryForRowSet("select gd_qty from goodsDetail where gd_id="
								+ gdid);
				if (sl2.next()) {
					sqls.add("update goodsDetail set gd_qty=" + gdqty
							+ ",gd_madedate=to_date('" + map.get("gd_madedate")
							+ "','yyyy-MM-dd'),gd_price=" + gdprice
							+ ",gd_whcode='" + gdwhcode + "',gd_minbuyqty="
							+ minbuyqty + " where gd_id=" + gdid);
				} else {
					BaseUtil.showError("序号 :[" + gddetno + "],明细数据已经不存在，不能拆分!");
				}
			} else {
				boolean bool = true;
				while (bool) {
					newdetno++;
					bool = baseDao.checkIf("GoodsDetail", "gd_guid=" + guid
							+ " AND gd_detno=" + newdetno);
					if (!bool)
						break;
				}
				currentMap.remove("gd_madedate");
				currentMap
						.put("gd_madedate", map.get("gd_madedate").toString());
				currentMap.remove("gd_detno");
				currentMap.put("gd_detno", newdetno);
				currentMap.remove("gd_id");
				currentMap.put("gd_id", baseDao.getSeqId("GOODSDETAIL_SEQ"));
				currentMap.remove("gd_qty");
				currentMap.put("gd_qty", gdqty);
				currentMap.remove("gd_minbuyqty");
				currentMap.put("gd_minbuyqty", minbuyqty);
				currentMap.remove("gd_whcode");
				currentMap.put("gd_whcode", gdwhcode);
				currentMap.remove("gd_price");
				currentMap.put("gd_price", gdprice);
				sqls.add(SqlUtil.getInsertSqlByMap(currentMap, "GoodsDetail"));
			}
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				"上架拆分", "明细行:" + basedetno + "=>被拆分",
				"GoodsUpApplication|gu_id=" + guid));
	}

	@Override
	public String turnAppropriationOut(int id, String caller) {
		String log;
		StringBuffer sb = new StringBuffer();
		Object whcode = null;
		int index = 1;
		Map<String, List<Map<String, Object>>> custs = new HashMap<String, List<Map<String, Object>>>();
		// 判断单据状态,必须已审核
		Object[] obs = baseDao.getFieldsDataByCondition("GoodsUp",
				new String[] { "gu_statuscode", "gu_code", "gu_whcode" },
				"gu_id=" + id);
		
		if (obs != null && !obs[0].toString().equals("AUDITED")) {
			BaseUtil.showError("上架单状态必须是已审核才能转拨出单！");
		} else if (obs == null) {
			BaseUtil.showError("该上架单不存在或已删除！");
		}else if(obs[2] == null || obs[2].equals("")){
			BaseUtil.showError("必须选择平台销售仓才可以转拨出单！");
		}
		// 判断单据是否有明细,明细是否都已转拨出单
		SqlRowList rs = baseDao
				.queryForRowSet("select * from goodsdetail where gd_guid=" + id);
		if (rs.next()) {
			rs = baseDao
					.queryForRowSet(
							"select * from goodsDetail where gd_barcode not in (select pd_barcode from  prodinout left join prodiodetail on pd_piid=pi_id where pi_sourcecode=? and pi_class='拨出单') and gd_guid=?",
							obs[1].toString(), id);
			if (rs.next()) {// 将未转拨出单的明细行按照gd_whcode进行分组
				List<Map<String, Object>> gstore = rs.getResultList();
				for (Map<String, Object> map : gstore) {
					whcode = map.get("gd_whcode");
					List<Map<String, Object>> list = null;
					if (!custs.containsKey(whcode)) {
						list = new ArrayList<Map<String, Object>>();
					} else {
						list = custs.get(whcode.toString());
					}
					list.add(map);
					custs.put(whcode.toString(), list);
				}
			} else {
				BaseUtil.showError("该上架单已转拨出单");
			}
		} else {
			BaseUtil.showError("该上架单没有需要转拨出单的明细");
		}
		Set<String> mapSet = custs.keySet();
		for (String s : mapSet) {
			int pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
			String code = baseDao
					.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
			String sqlMain = "INSERT INTO ProdInOut(pi_id,pi_inoutno,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate"
					+ ",pi_purpose,pi_sourcecode,pi_date,pi_class,pi_status,pi_statuscode,pi_printstatus,pi_printstatuscode,pi_type,pi_whcode) VALUES (?,?,?,'ENTERING',?,sysdate,"
					+ "?,?,sysdate,'拨出单',?,'UNPOST',?,'UNPRINT','库存转移',?)";
			baseDao.execute(
					sqlMain,
					new Object[] { pi_id, code,
							BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(),
							obs[2].toString(), obs[1].toString(),
							BaseUtil.getLocalMessage("UNPOST"),
							BaseUtil.getLocalMessage("UNPRINT"), s });
			baseDao.execute("update ProdInOut set pi_whname=(select wh_description from warehouse where wh_code=pi_whcode) where pi_id="
					+ pi_id);
			baseDao.execute("update ProdInOut set pi_purposename=(select wh_description from warehouse where wh_code=pi_purpose) where pi_id="
					+ pi_id);
			List<Map<String, Object>> gridData = custs.get(s);
			List<String> sqls = new ArrayList<String>();
			int detno = 1;
			for (Map<String, Object> map : gridData) {// 转明细表
				sqls.add("INSERT INTO ProdIODetail(pd_id,pd_piid,pd_pdno,pd_prodcode,pd_whcode,pd_outqty,"
						+ "pd_piclass,pd_inoutno,pd_prodid,pd_orderdetno,pd_ordercode,pd_barcode,pd_inwhcode) "
						+ " select PRODIODETAIL_SEQ.nextval,"
						+ pi_id
						+ ","
						+ detno
						+ ",'"
						+ map.get("gd_prodcode")
						+ "','"
						+ map.get("gd_whcode")
						+ "',"
						+ map.get("gd_qty")
						+ ","
						+ "'拨出单','"
						+ code
						+ "',pr_id,'"
						+ map.get("gd_detno")
						+ "','"
						+ obs[1]
						+ "','"
						+ map.get("gd_barcode")
						+ "','"
						+ obs[2].toString()
						+ "' from product where pr_code='"
						+ map.get("gd_prodcode") + "'");
				detno++;
			}
			baseDao.execute(sqls);
			baseDao.execute("update ProdIODetail set pd_whname=(select wh_description from warehouse where wh_code=pd_whcode) where pd_piid="
					+ pi_id);
			baseDao.execute("update ProdIODetail set pd_inwhname=(select wh_description from warehouse where wh_code=pd_inwhcode) where pd_piid="
					+ pi_id);
			log = "转入成功,拨出单号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
					+ pi_id + "&gridCondition=pd_piidIS" + pi_id
					+ "&whoami=ProdInOut!AppropriationOut')\">" + code
					+ "</a>&nbsp;";
			sb.append(index).append(": ").append(log).append("<hr>");
			// 更新明细拨出单号
			baseDao.updateByCondition("goodsDetail",
					"gd_bccode='" + code + "'", "gd_guid=" + id
							+ " and gd_whcode='" + s + "'");
		}
		return sb.toString();
	}

	/**
	 * 匹配标准uuid 根据原厂型号
	 */
	@Override
	public String getUUId(int id, String caller) {
		// 判断上架申请单状态，必须是在录入
		Object ob = baseDao.getFieldDataByCondition("GoodsUp", "gu_statuscode",
				"gu_id=" + id);
		if (ob != null && !ob.toString().equals("ENTERING")) {
			BaseUtil.showError("上架单必须是在录入状态！");
		} else if (ob == null) {
			BaseUtil.showError("该上架单不存在或已删除！");
		}
		SqlRowList rs;
		List<String> sqls = new ArrayList<String>();
		rs = baseDao
				.queryForRowSet("select distinct pr_code,pr_uuid,pr_brand,pr_orispeccode from goodsdetail left join product on pr_code=gd_prodcode where gd_guid="
						+ id + " and nvl(pr_uuid,' ')=' '");
		if (rs.next()) {
			// 将数据写入临时表中
			List<Map<String, Object>> list = rs.getResultList();
			int detno = 1;
			String code = baseDao.sGetMaxNumber("ProductUUIdBatch", 2);
			List<String> oriCodes = new ArrayList<String>();
			for (Map<String, Object> map : list) {
				sqls.add("insert into ProductUUIdBatch (pub_id,pub_emcode,pub_emid,pub_prodcode,pub_detno,pub_orispeccode,pub_code) values"
						+ "(PRODUCTUUIDBATCH_SEQ.nextval,'"
						+ SystemSession.getUser().getEm_code()
						+ "',"
						+ SystemSession.getUser().getEm_id()
						+ ",'"
						+ map.get("pr_code")
						+ "',"
						+ detno
						+ ",'"
						+ map.get("pr_orispeccode") + "','" + code + "')");
				detno++;
				if (map.get("pr_orispeccode") != null) {
					oriCodes.add(map.get("pr_orispeccode").toString());
				}
			}
			baseDao.execute(sqls);
			if (!oriCodes.isEmpty()) {
				productBatchUUIdService.batchGetByOriCode(oriCodes,code);
			}
			return code;
		} else {
			BaseUtil.showError("所有的物料都已经维护了标准料号！");
		}
		return null;
	}

	@Override
	public String goodsUp(int id, String caller) {
		// 必须是已审核，状态不等于已上架，拨出单已过帐
		Object[] obs = baseDao.getFieldsDataByCondition("GoodsUp",
				new String[] { "gu_statuscode", "gu_code", "gu_whcode" },
				"gu_id=" + id);
		if (obs != null && !obs[0].toString().equals("AUDITED")) {
			BaseUtil.showError("上架单状态必须是已审核才能上架！");
		} else if (obs == null) {
			BaseUtil.showError("该上架单不存在或已删除！");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gd_detno) from goodsdetail where gd_guid=? and gd_sendstatus in('已上传','上传中') and rownum<10",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细已上传或正在上传中，不允许重复上架!");
		}
		/*
		 * dets = baseDao .getJdbcTemplate() .queryForObject(
		 * "select wm_concat(''''|| pi_inoutno||'''') from (select distinct pi_inoutno from Prodinout left join prodiodetail on pi_id=pd_piid left join goodsUp on pi_sourcecode=gu_code left join goodsDetail on gd_guid=gu_id and pd_barcode=gd_barcode where gu_id=? and pi_class='拨出单')"
		 * , String.class, id); if (dets == null) {
		 * BaseUtil.showError("请确保上架单已转拨出单");
		 */
		/*
		 * } else {// 判断所有拨出单都已上架 String det =
		 * baseDao.getJdbcTemplate().queryForObject(
		 * "select wm_concat(pi_inoutno) from Prodinout where pi_inoutno in(" +
		 * dets + ") and pi_statuscode<>'POSTED'", String.class); if (det !=
		 * null) { BaseUtil.showError("请确保上架单转出拨出单都已过账，拨出单号：[" + det + "]"); } }
		 */
		// 上架
		upToB2C(id);
		return "上架成功";
	}

	/**
	 * 上传至B2C,拨出单过账也可以调用
	 * 
	 * @param id
	 */
	@Override
	public void upToB2C(int id) {
		//单位换算，销售数量，最小包装数，价格，样品数量，最小订购量
		/**
		 * PCS,KPCS  1000 pcs= 1 kpcs
		 * G,KG      1000 g =  1 kg
		 * 其他不进行换算
		 * 获取商城标准单位，
		 * 对比UAS中的单位进行转换
		 */
		List<GoodsSimpleUas> simpleGoodses = sendData(id);
		if(CollectionUtils.isEmpty(simpleGoodses)){
			return;
		}
		try{
			final List<GoodsFUas> goodsesFUas = b2CGoodsUpAndDownService
					.upToB2C(simpleGoodses,SystemSession.getUser().getCurrentMaster());
			if (!CollectionUtils.isEmpty(goodsesFUas)) {// 上传成功
				// 更新上传状态为上传成功
				baseDao.getJdbcTemplate()
						.batchUpdate(
								"update goodsdetail set gd_sendstatus='已上传',gd_b2bbatchcode=? where gd_id=?",
								new BatchPreparedStatementSetter() {
	
									@Override
									public void setValues(PreparedStatement ps,
											int index) throws SQLException {
										GoodsFUas goods = goodsesFUas.get(index);
										ps.setObject(1, goods.getBatchCode());
										ps.setObject(2, goods.getSourceId());
									}
	
									@Override
									public int getBatchSize() {
										return goodsesFUas.size();
									}
								});
				/**
				 * @Tip 将上架成功的数据写入到商城批次数据表goodspwonhand中,并记录日志
				 */
				Object ob = baseDao.getFieldDataByCondition("goodsup", "gu_code",
						"gu_id=" + id);
				baseDao.callProcedure("SP_GOODSPWONHAND_UP",
						new Object[] { ob });
			} 
		}catch(Exception e){
			//如果已经产生过任务，将任务先作废
			baseDao.execute("update b2c$task set ta_finishstatus='-1' where ta_docaller='GoodsUp' and ta_doid=?",id);
			//上传失败添加任务至B2CTASK
			baseDao.execute("insert into b2c$task(ta_id,ta_docaller,ta_docode,ta_doid,ta_actiontime) select "
					+ "B2C$TASK_SEQ.nextval,'GoodsUp',gu_code,gu_id,sysdate from goodsup where gu_id=?",id);
			BaseUtil.showError("上架单上传至优软商城失败");
		}
	}
	
	@Override
	public List<GoodsSimpleUas> sendData(int id){
		List<GoodsSimpleUas> simpleGoodses = new ArrayList<GoodsSimpleUas>();
		SqlRowList rs = baseDao.queryForRowSet("select gd_prodcode,gd_erpunit,go_unit,gd_madedate,gd_id,gd_remark,nvl(gd_minbuyqty,1) gd_minbuyqty,gd_qty,nvl(gd_minpackqty,1)gd_minpackqty,gd_uuid,"
				+ "NVL(gd_price,0) gd_price,nvl(gd_usdprice,0)gd_usdprice,gd_original,gd_simpleqty,case when nvl(gd_returninweek,0)<>0 then 1 else 0 end returnInWeek,gd_taxrate,lower(gd_erpunit) gd_erpunit,lower(go_unit) go_unit,nvl(gd_deliverytime,1) gd_deliverytime,nvl(gd_hkdeliverytime,1) gd_hkdeliverytime,nvl(gd_ismallsale,0) gd_ismallsale from goodsup left join goodsdetail on gd_guid=gu_id left join b2c$goodsonhand on gd_uuid=go_uuid and gd_prodcode=go_prodcode where gd_guid=? ",id);
		//查找业务员，第一原则：customer 表中的cu_sellercode, 第二原则：product 表中的计划员 pr_plancode
		SqlRowList rs2 = baseDao.queryForRowSet("select em_name,em_mobile from customer left join configs on data=cu_code left join employee on em_code=cu_sellercode where caller=? and code=?","B2CSetting","B2CCusomter");		
		while (rs.next()){
			GoodsSimpleUas simpleGood = new GoodsSimpleUas();
			simpleGood.setProduceDate(rs.getGeneralTimestamp("gd_madedate",Constant.YMD));
			simpleGood.setSourceId(rs.getGeneralLong("gd_id"));
			simpleGood.setRemark(rs.getString("gd_remark"));
			simpleGood.setUuid(rs.getString("gd_uuid"));
			simpleGood.setOldReserve(0.0);
			simpleGood.setDeliveryDemTime(Short.valueOf(rs.getString("gd_deliverytime")));
			simpleGood.setReturnInWeek(Short.valueOf(rs.getString("returnInWeek")));
			double rate = productBatchUUIdService.getUnitRate(rs.getString("gd_erpunit"),rs.getString("go_unit"));
			simpleGood.setMinBuyQty(rs.getGeneralDouble("gd_minbuyqty")*rate);
			simpleGood.setReserve(rs.getGeneralDouble("gd_qty")*rate);
			simpleGood.setMinPackQty(rs.getGeneralDouble("gd_minpackqty")*rate);
			simpleGood.setIsMallSale(Short.valueOf(rs.getString("gd_ismallsale")));
			List<GoodsQtyPriceUas> li = new ArrayList<GoodsQtyPriceUas>();
			GoodsQtyPriceUas price = new GoodsQtyPriceUas();
			price.setStart(1.0);
			price.setEnd(rs.getGeneralDouble("gd_qty")*rate);
			String currency = "RMB"; //默认为人民币
			if(rs.getGeneralDouble("gd_price")!= 0 && rs.getGeneralDouble("gd_usdprice")!=0){
				currency = "RMB-USD";
				price.setRMBPrice(rs.getGeneralDouble("gd_price")/rate);
				price.setUSDPrice(rs.getGeneralDouble("gd_usdprice")/rate);
				simpleGood.setDeliveryDemTime(Short.valueOf(rs.getString("gd_deliverytime")));
				simpleGood.setDeliveryHKTime(Short.valueOf(rs.getString("gd_hkdeliverytime")));
			}else if(rs.getGeneralDouble("gd_price") == 0 && rs.getGeneralDouble("gd_usdprice")!=0){
				currency = "USD";
				price.setUSDPrice(rs.getGeneralDouble("gd_usdprice")/rate);
				simpleGood.setDeliveryHKTime(Short.valueOf(rs.getString("gd_hkdeliverytime")));
			}else{
				price.setRMBPrice(rs.getGeneralDouble("gd_price")/rate);
				simpleGood.setDeliveryDemTime(Short.valueOf(rs.getString("gd_deliverytime")));
			}
			li.add(price);
			simpleGood.setQtyPrice(FlexJsonUtil.toJsonArrayDeep(li));
			simpleGood.setCurrencyName(currency);
			simpleGood.setSampleQty(rs.getGeneralDouble("gd_simpleqty")*rate);
			simpleGood.setOriginal(rs.getInt("gd_original"));
			if(rs2.next()){
				simpleGood.setPublishName(rs2.getString("em_name"));
				simpleGood.setPublishPhone(rs2.getString("em_mobile"));
			}else {
				rs2 = baseDao.queryForRowSet("select em_name,em_mobile from product left join employee on em_code=pr_planercode where pr_code=?",rs.getString("gd_prodcode"));
				if(rs2.next()){
					simpleGood.setPublishName(rs2.getString("em_name"));
					simpleGood.setPublishPhone(rs2.getString("em_mobile"));
				}
			}
			simpleGoodses.add(simpleGood);
		}
		return simpleGoodses;
	}

}
