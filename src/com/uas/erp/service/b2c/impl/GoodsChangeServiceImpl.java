package com.uas.erp.service.b2c.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.api.b2c_erp.seller.model.GoodsFUas;
import com.uas.api.b2c_erp.seller.model.GoodsQtyPriceUas;
import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;
import com.uas.b2c.service.seller.B2CGoodsUpAndDownService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2c.GoodsChangeService;
import com.uas.erp.service.scm.ProductBatchUUIdService;

@Service("goodsChangeService")
public class GoodsChangeServiceImpl implements GoodsChangeService {

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
    private B2CGoodsUpAndDownService b2CGoodsUpAndDownService;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
	@Override
	@Transactional
	public void saveGoodsChange(String formStore, String caller, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("GoodsChange", "gc_code='" + store.get("gc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存GoodsChange
		String formSql = SqlUtil.getInsertSqlByMap(store, "GoodsChange");
		baseDao.execute(formSql);
		// 保存goodsChangeDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "goodsChangeDetail", "gcd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "gc_id", store.get("gc_id"));
		if ("GoodsOff".equals(caller)) {
			// 更新下架数量
			baseDao.execute("update goodschangedetail set gcd_offqty=(select gd_offqty from PM_GOODSSALE_VIEW where gd_barcode=gcd_barcode) where gcd_gcid="
					+ store.get("gc_id") + " and exists (select 1 from goodsdetail where gd_barcode=gcd_barcode)");
		}
		// 自动将拨入仓库更新为上架单的仓库
		baseDao.execute("update goodsChangeDetail set (gcd_whcode,gcd_whname)=(select gd_whcode,gd_whname from goodsdetail where gd_barcode=gcd_barcode) where gcd_gcid=? and nvl(gcd_whcode,' ')=' '",store.get("gc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteGoodsChange(int id, String caller) {
		// 只能删除在录入的单据
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id }); // 删除
		baseDao.deleteById("GoodsChange", "gc_id", id);
		// 删除Detail
		baseDao.deleteById("goodsChangeDetail", "gcd_gcid", id);
		// 记录操作
		baseDao.logger.delete(caller, "gc_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	public void updateGoodsChangeById(String formStore, String caller, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + store.get("gc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "GoodsChange", "gc_id");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "goodsChangeDetail", "gcd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "gc_id", store.get("gc_id"));
		if ("GoodsOff".equals(caller)) {
			// 更新下架数量
			baseDao.execute("update goodschangedetail set gcd_offqty=(select gcd_offqty from PM_GOODSSALE_VIEW where gd_barcode=gcd_barcode) where gcd_gcid="
					+ store.get("gc_id") + " and exists (select 1 from goodsdetail where gd_barcode=gcd_barcode)");
		}
		// 自动将拨入仓库更新为上架单的仓库
		baseDao.execute("update goodsChangeDetail set (gcd_whcode,gcd_whname)=(select gd_whcode,gd_whname from goodsdetail where gd_barcode=gcd_barcode) where gcd_gcid=? and nvl(gcd_whcode,' ')=' '",store.get("gc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitGoodsChange(int id, String caller) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 提交前如果下架数量为0限制提交
		SqlRowList rr = baseDao.queryForRowSet("select gcd_detno from GoodsChangeDetail where gcd_offqty=0 and gcd_gcid="+id);
		if(rr.next()){
			BaseUtil.showError("行号:"+rr.getString("gcd_detno")+"的下架数量为0,不允许提交");
		}
		// 根据gcd_barcode=gd_barcode更新原有信息
		baseDao.execute("update goodschangedetail set (gcd_oldprice,gcd_oldmadedate,gcd_oldminbuyqty,gcd_oldminpackqty,gcd_oldremark,gcd_b2bbatchcode,gcd_uuid,gcd_oldusdprice,gcd_olddeliverytime,gcd_oldhkdeliverytime)=(select gd_price,gd_madedate,gd_minbuyqty,gd_minpackqty,gd_remark,gd_b2bbatchcode,gd_uuid,gd_usdprice,gd_deliverytime,gd_hkdeliverytime from goodsdetail where gd_barcode=gcd_barcode) where gcd_gcid="
				+ id + " and exists(select 1 from goodsdetail where gd_barcode=gcd_barcode)");
		// 判断 数量 单价 等信息
		checkQty(id, caller);
		// 商品下架，需要限制不能同时存在两张未审核的同一上架批号的下架单
		Object ob = baseDao.getFieldDataByCondition("goodschange left join goodschangedetail on gcd_gcid=gc_id ", "wm_concat(gcd_barcode)",
				"gc_id<>" + id + "and (gc_statuscode<>'AUDITED' OR gc_status <>'已审核') "
						+ "and gcd_barcode in(select gcd_barcode from goodschangedetail where gcd_gcid=" + id + ")");
		if (ob != null) {
			BaseUtil.showError("上架批次号[" + ob.toString() + "]存在其他未审核的下架单或变更单");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("GoodsChange", "gc_id=" + id, "gc_status", "gc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "gc_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitGoodsChange(int id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { id }); // 执行反提交操作
		baseDao.updateByCondition("GoodsChange", "gc_statuscode='ENTERING',gc_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"gc_id=" + id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "gc_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id });
	}

	@Override
	public void auditGoodsChange(int id, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		// 判断数量 单价
		checkQty(id, caller);
		// 执行审核操作
		baseDao.audit("GoodsChange", "gc_id=" + id, "gc_status", "gc_statuscode", "gc_auditdate", "gc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "gc_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
		baseDao.execute("update goodsChangeDetail set gcd_sendstatus='待上传' where gcd_gcid=" + id);
		/*//调用更新商城批次库存数据
		Object code = baseDao.getFieldDataByCondition("goodschange", "gc_code", "gc_id="+id);
		baseDao.callProcedure("SP_GOODSPWONHAND_OFF", new Object[]{code});*/
		// 上传至平台
		sendToPlatForm(id, caller);
	}

	@Override
	public void resAuditGoodsChange(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("GoodsChange", "gc_statuscode", "gc_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 判断该单据上是否上传到B2B，已上传，则不允许反审核，需要变更的话，走变更单流程
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(gcd_detno) from goodschangedetail where gcd_gcid="
				  +id+" and gcd_sendstatus in('已上传','上传中')", String.class); 
		if (dets != null) {
			BaseUtil.showError("存在已上传或上传中的明细!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gcd_detno) from Prodinout left join prodiodetail on pi_id=pd_piid left join GoodsChange on pi_sourcecode=gc_code left join goodsChangeDetail on gcd_gcid=gc_id and pd_barcode=gcd_barcode where gc_id=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已转拨出单，不允许反审核!行号：" + dets);
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		// 执行反审核操作
		baseDao.resAudit("GoodsChange", "gc_id=" + id, "gc_status", "gc_statuscode", "gc_auditdate", "gc_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "gc_id", id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	private void checkQty(int id, String caller) {
		baseDao.execute("update goodsChangeDetail set gcd_b2bbatchcode=(select gd_b2bbatchcode from goodsdetail where gd_barcode=gcd_barcode) where gcd_gcid="
				+ id + " and nvl(gcd_b2bbatchcode,' ')<>' ' and exists (select 1 from goodsdetail where gd_barcode=gcd_barcode)");
		// 限制明细行不能重复上架批号
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gcd_detno) from goodschangedetail where (gcd_barcode,gcd_prodcode) in (select gcd_barcode,gcd_prodcode from goodschangedetail where gcd_gcid=? group by gcd_barcode,gcd_prodcode having count(1)>1) and gcd_gcid=?",
						String.class, new Object[]{id,id});
		if (dets != null) {
			BaseUtil.showError("明细行上架批号重复，行号：" + dets);
		}
		// 判断pr_uuid
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(gcd_detno) dt from goodsChangeDetail where nvl(gcd_uuid,' ')=' ' and gcd_gcid=" + id, String.class);
		if (dets != null) {
			BaseUtil.showError("序号[" + dets + "]物料没有对应的商城标准料号");
		}
		// 判断gcd_barcode是否存在于上架申请单的gd_barcode，
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct gcd_barcode) from goodschangedetail where gcd_gcid=" + id
						+ "and not exists(select * from goodsdetail)", String.class);
		if (dets != null) {
			BaseUtil.showError("上架批号[" + dets + "]没有对应的上架单");
		}
		// 变更必须满足上架申请单已经上传
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(gcd_detno) from goodschangedetail left join goodschange on gc_id=gcd_gcid where gcd_gcid=" + id
						+ " and nvl(gcd_b2bbatchcode,' ')=' ' ", String.class);
		if (dets != null) {
			BaseUtil.showError("明细中对应的上架单还未上传至平台，行号:" + dets);
		}
		// 更新信息，如果为空统一更新为原有数据
		baseDao.execute("update goodsChangeDetail set gcd_minbuyqty=(case when nvl(gcd_minbuyqty,0)<=0 then gcd_oldminbuyqty else gcd_minbuyqty end),"+
				" gcd_minpackqty=(case when nvl(gcd_minpackqty,0)<=0 then gcd_oldminpackqty else gcd_minpackqty  end),"+
				" gcd_price=(case when nvl(gcd_price,0)<=0 then gcd_oldprice else gcd_price end),"+
				" gcd_remark=(case when gcd_remark is null then gcd_oldremark else gcd_remark end) ,"+
				" gcd_madedate=(case when gcd_madedate is null then gcd_oldmadedate else gcd_madedate end),"+
				" gcd_returninweek=(case when gcd_returninweek is null then gcd_oldreturninweek else gcd_returninweek end),"+
				" gcd_usdprice=(case when nvl(gcd_usdprice,0)=0 then nvl(gcd_oldusdprice,0) else gcd_usdprice end ),"+
			    " gcd_deliverytime=(case when gcd_deliverytime is null then nvl(gcd_olddeliverytime,0) else gcd_deliverytime end),"+
			    " gcd_hkdeliverytime=(case when gcd_hkdeliverytime is null then nvl(gcd_oldhkdeliverytime,0) else gcd_hkdeliverytime end) where gcd_gcid="+id);
		if (caller.equals("GoodsChange")) {
			// 如果dc_type=信息变更,要求必须填写新最小包装、新最小起订量都必须大于0；
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(gcd_detno) from goodsChangeDetail left join goodschange on gc_id=gcd_gcid where gc_id=?"
									+ " and (nvl(gcd_price,0)<0 OR nvl(gcd_minpackqty,0)<0 OR nvl(gcd_minbuyqty,0)<0 or nvl(gcd_usdprice,0)<0 or nvl(gcd_minbuyqty,0)<nvl(gcd_minpackqty,0))", String.class,id);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]单价、最小包装数、最小订购量必须大于0,并且最小订购量不允许小于最小包装数");
			}
			/*// 如果填写了下架数量，要求下架之后剩余的数量必须大于等于最小订单量
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(gcd_detno) from goodschangedetail left join goodsdetail on gd_barcode=gcd_barcode"
									+ " where gcd_gcid=? and nvl(gcd_offqty,0)>0 and ((gd_qty-nvl(gcd_offqty,0))<gcd_minbuyqty or MOD((gd_qty-nvl(gcd_offqty,0)),gcd_minbuyqty)=0)",
							String.class, id);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]变更之后剩余的数量不允许小于最小起订量的整数倍！");
			}*/
			//不允许变更币别，如果原来只填写的人民币单价，只能修改人民币单价
			SqlRowList rs = baseDao.queryForRowSet("select gcd_detno,nvl(gcd_oldprice,0)gcd_oldprice,nvl(gcd_price,0)gcd_price,nvl(gcd_oldusdprice,0) gcd_oldusdprice,nvl(gcd_usdprice,0) gcd_usdprice,nvl(gcd_deliverytime,0) gcd_deliverytime,nvl(gcd_olddeliverytime,0)gcd_olddeliverytime,nvl(gcd_hkdeliverytime,0)gcd_hkdeliverytime,nvl(gcd_oldhkdeliverytime,0)gcd_oldhkdeliverytime "
					+ "from goodschangedetail where gcd_gcid=?",id);
			while(rs.next()){
				if(rs.getDouble("gcd_oldprice")==0 ){
					if(rs.getDouble("gcd_price") !=0 || rs.getInt("gcd_deliverytime") !=rs.getInt("gcd_olddeliverytime")){
					   BaseUtil.showError("序号："+rs.getString("gcd_detno")+",上架未维护人民币单价，不允许变更人民币单价和大陆交期");
					}
				}
				if(rs.getDouble("gcd_oldusdprice")==0 ){
					if(rs.getDouble("gcd_usdprice") !=0 || rs.getInt("gcd_hkdeliverytime") !=rs.getInt("gcd_oldhkdeliverytime")){
					   BaseUtil.showError("序号："+rs.getString("gcd_detno")+",上架未维护美元单价，不允许变更美元单价和香港交期");
					}
				}
			}
			// 所有明细的生产日期必须已经维护；
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(gcd_detno) from goodsChangeDetail where gcd_madedate is null and gcd_gcid=" + id, String.class);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]的生产日期没有维护！");
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(gcd_detno) dt from goodschangedetail where gcd_gcid=" + id
							+ " and gcd_madedate<>gcd_oldmadedate and to_char(gcd_madedate,'YYYY-mm-dd') > to_char(sysdate, 'YYYY-mm-dd')",
					String.class);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]生产日期必须小于等于当天");
			}
			// 如果填写了下架数量必须大于0
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(gcd_detno) from goodschangedetail left join goodschange on gc_id=gcd_gcid where nvl(gcd_offqty,0)<0 and gc_type='下架' and gc_id="
									+ id, String.class);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]下架数量不允许小于0！");
			}
			// 如果填写了下架数量，要求下架数量不能大于gd_qty 在售数量;
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(gcd_detno)cn from goodschangedetail left join goodsdetail on gd_barcode=gcd_barcode where gcd_gcid=?"
							+ " and nvl(gcd_offqty,0)>0 and gcd_offqty>gd_qty", String.class, id);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]维护的下架数量大于在售数量！");
			}
		} else if (caller.equals("GoodsOff")) {
			// 下架数量必须大于0
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(gcd_detno) from goodschangedetail left join goodschange on gc_id=gcd_gcid where gcd_gcid=" + id
							+ " and nvl(gcd_offqty,0)<=0", String.class);
			if (dets != null) {
				BaseUtil.showError("下架数量必须大于0，行号:" + dets);
			}
			// 如果填写了下架数量，要求下架数量不能大于gd_qty;
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(gcd_detno)cn from goodschangedetail left join goodsdetail on gd_barcode=gcd_barcode where gcd_gcid=?"
							+ " and gcd_offqty>gd_qty", String.class, id);
			if (dets != null) {
				BaseUtil.showError("序号[" + dets + "]下架数量大于在售数量！");
			}
		}
	}

	@Override
	public String turnAppropriationOut(int id, String caller) {
		String log;
		StringBuffer sb = new StringBuffer();
		Object whcode = null;
		int index = 1;
		Map<String, List<Map<String, Object>>> custs = new HashMap<String, List<Map<String, Object>>>();
		// 判断单据状态,必须已审核
		Object[] obs = baseDao.getFieldsDataByCondition("GoodsChange", new String[] { "gc_statuscode", "gc_code", "gc_whcode", "gc_type" },
				"gc_id=" + id);
		if (obs != null && !obs[0].toString().equals("AUDITED")) {
			BaseUtil.showError("商品变更单状态必须是已审核才能转拨出单！");
		} else if (obs == null) {
			BaseUtil.showError("该商品变更单不存在或已删除！");
		} else if(obs[2] ==null){
			BaseUtil.showError("未维护平台销售仓，不允许转拨出单！");
		}
		// 判断单据是否有明细,明细是否都已转拨出单
		SqlRowList rs = baseDao.queryForRowSet("select * from goodsChangeDetail where gcd_gcid=" + id);
		if (rs.next()) {
			rs = baseDao
					.queryForRowSet("select * from goodsChangeDetail where gcd_gcid="
							+ id
							+ " and nvl(gcd_offqty,0)>0 and gcd_barcode not in (select pd_barcode from  prodinout left join prodiodetail on pd_piid=pi_id where pi_sourcecode='"
							+ obs[1].toString() + "' and pi_class='拨出单')");
			if (rs.next()) {// 将未转拨出单的明细行按照gcd_whcode进行分组
				List<Map<String, Object>> gstore = rs.getResultList();
				for (Map<String, Object> map : gstore) {
					whcode = map.get("gcd_whcode");
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
				BaseUtil.showError("该商品变更单已转拨出单或者不存在需要转拨出单的明细");
			}
		} else {
			BaseUtil.showError("该商品变更单没有需要转拨出单的明细");
		}
		Set<String> mapSet = custs.keySet();
		for (String s : mapSet) {
			int pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
			String code = baseDao.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
			String sqlMain = "INSERT INTO ProdInOut(pi_id,pi_inoutno,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate"
					+ ",pi_purpose,pi_sourcecode,pi_date,pi_class,pi_status,pi_statuscode,pi_printstatus,pi_printstatuscode,pi_type,pi_whcode) VALUES (?,?,?,'ENTERING',?,sysdate,"
					+ "?,?,sysdate,'拨出单',?,'UNPOST',?,'UNPRINT','库存转移',?)";
			baseDao.execute(
					sqlMain,
					new Object[] { pi_id, code, BaseUtil.getLocalMessage("ENTERING"), SystemSession.getUser().getEm_name(), s,
							obs[1].toString(), BaseUtil.getLocalMessage("UNPOST"), BaseUtil.getLocalMessage("UNPRINT"), obs[2].toString() });
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
						+ map.get("gcd_prodcode")
						+ "','"
						+ obs[2].toString()
						+ "',"
						+ map.get("gcd_offqty")
						+ ","
						+ "'拨出单','"
						+ code
						+ "',pr_id,'"
						+ map.get("gcd_detno")
						+ "','"
						+ obs[1]
						+ "','"
						+ map.get("gcd_barcode")
						+ "','" + s + "' from product where pr_code='" + map.get("gcd_prodcode") + "'");
				detno++;
			}
			baseDao.execute(sqls);
			baseDao.execute("update ProdIODetail set pd_whname=(select wh_description from warehouse where wh_code=pd_whcode) where pd_piid="
					+ pi_id);
			baseDao.execute("update ProdIODetail set pd_inwhname=(select wh_description from warehouse where wh_code=pd_inwhcode) where pd_piid="
					+ pi_id);
			log = "转入成功,拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
					+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!AppropriationOut')\">" + code + "</a>&nbsp;";
			sb.append(index).append(": ").append(log).append("<hr>");
		}
		return sb.toString();
	}

	/**
	 * 上传数据至平台
	 * 
	 * @param id
	 * @param caller
	 */
	private void sendToPlatForm(int id, String caller) {
		if (caller.equals("GoodsChange")) {// 变更
			List<GoodsSimpleUas> simple = sendData(id);
			if(CollectionUtils.isEmpty(simple)){
				return;
			}
			try{
				final List<GoodsFUas> goodsesFUas = b2CGoodsUpAndDownService
						.updateGoodses(simple,SystemSession.getUser().getCurrentMaster());
				if (!CollectionUtils.isEmpty(goodsesFUas)) {// 上传成功
					// 更新上传状态为上传成功
					baseDao.execute("update goodschangedetail set gcd_sendstatus='已上传' where gcd_gcid=" + id);
					// 上传成功修改原来数据goodsdetail 表中数据
					baseDao.execute("update goodsdetail set gd_qty=gd_qty-(select nvl(gcd_offqty,0) from goodschangedetail where"
							+ " gcd_gcid=" + id + " and gcd_barcode=gd_barcode ),gd_sendstatus='已上传'"
							+ " where exists (select 1 from goodschangedetail where gcd_gcid=" + id + " and gcd_barcode=gd_barcode )");
	
					baseDao.execute("update goodsdetail set (gd_madedate,gd_minbuyqty,gd_minpackqty,gd_price,gd_remark,gd_usdprice,gd_deliverytime,gd_hkdeliverytime)=(select gcd_madedate,gcd_minbuyqty,gcd_minpackqty,gcd_price,gcd_remark,gcd_usdprice,gcd_deliverytime,gcd_hkdeliverytime from goodschangedetail where"
							+ " gcd_gcid="+id+" and gcd_barcode=gd_barcode "
							+ ")"
							+ " where exists (select 1 from goodschangedetail where gcd_gcid=" + id + " and gcd_barcode=gd_barcode)");
					//更新上架申请单中的未含税单价
					baseDao.execute("update Goodsdetail set gd_costprice=round(nvl(gd_price,0)/(1+nvl(gd_taxrate,0)/100),6) where gd_barcode in(select gcd_barcode from goodschangedetail where gcd_gcid="+id+")");
					//调用更新商城批次库存数据
					Object code = baseDao.getFieldDataByCondition("goodschange", "gc_code", "gc_id="+id);
					baseDao.callProcedure("SP_GOODSPWONHAND_OFF", new Object[]{code});
				} 
			}catch(Exception e){
				baseDao.execute("insert into b2c$task(ta_id,ta_docaller,ta_docode,ta_doid,ta_actiontime) select "
						+ "B2C$TASK_SEQ.nextval,'"+caller+"',gc_code,gc_id,sysdate from goodschange where gc_id=?",id);
				BaseUtil.showError("变更单上传至优软商城失败");
			}
		} else if (caller.equals("GoodsOff")) {// 下架
			SqlRowList rs = baseDao.queryForRowSet("select gcd_b2bbatchcode from goodschangedetail where gcd_gcid=" + id);
			StringBuffer strs = new StringBuffer();
			if (rs.next()) {
				for (Map<String, Object> map : rs.getResultList()) {
					if (map.get("gcd_b2bbatchcode") != null) {
						strs.append(map.get("gcd_b2bbatchcode") + ",");
					}
				}
				try{
					b2CGoodsUpAndDownService.pulloff(strs.substring(0, strs.length() - 1),SystemSession.getUser().getCurrentMaster());
					//调用更新商城批次库存数据
					Object code = baseDao.getFieldDataByCondition("goodschange", "gc_code", "gc_id="+id);
					baseDao.callProcedure("SP_GOODSPWONHAND_OFF", new Object[]{code});
				}catch(Exception e){
					baseDao.execute("insert into b2c$task(ta_id,ta_docaller,ta_docode,ta_doid,ta_actiontime) select "
							+ "B2C$TASK_SEQ.nextval,'"+caller+"',gc_code,gc_id,sysdate from goodschange where gc_id=?",id);
					BaseUtil.showError("下架单上传至商城失败");
				}
				
				// 更新上传状态为上传成功
				baseDao.execute("update goodschangedetail set gcd_sendstatus='已上传' where gcd_gcid=" + id);
				// 上传成功修改原来数据goodsdetail 表中数据
				baseDao.execute("update goodsdetail set gd_qty=0 ,gd_sendstatus='已上传'"
						+ " where exists (select 1 from goodschangedetail where gcd_barcode=gd_barcode and gcd_gcid=" + id + ")");
			}
		}
	}
	
	@Override
	public List<GoodsSimpleUas> sendData(int id){
		List<GoodsSimpleUas> simpleGoodses = new ArrayList<GoodsSimpleUas>();
		SqlRowList rs = baseDao.queryForRowSet("select gd_erpunit,go_unit,gd_taxrate,gd_original,gd_id,gcd_remark,gcd_uuid,gd_qty,gcd_minbuyqty,gd_qty-nvl(gcd_offqty,0) as reserve,gcd_minpackqty,"
				+ "nvl(gcd_price,0) gcd_price,nvl(gcd_usdprice,0) gcd_usdprice,gcd_b2bbatchcode,nvl(gcd_hkdeliverytime,0)gcd_hkdeliverytime,nvl(gcd_deliverytime,0) gcd_deliverytime,gcd_madedate gcd_madedate from goodschangedetail left join goodsdetail on gd_barcode=gcd_barcode left join B2C$GOODSONHAND on go_uuid=gcd_uuid and go_prodcode=gcd_prodcode where gcd_gcid=?",id);
		while (rs.next()){
			GoodsSimpleUas simpleGood = new GoodsSimpleUas();
			simpleGood.setSourceId(rs.getGeneralLong("gd_id"));
			simpleGood.setRemark(rs.getString("gcd_remark"));
			simpleGood.setProduceDate(rs.getGeneralTimestamp("gd_madedate",Constant.YMD));
			double rate = productBatchUUIdService.getUnitRate(rs.getString("gd_erpunit"),rs.getString("go_unit"));
			simpleGood.setOldReserve(rs.getGeneralDouble("gd_qty")*rate);
			simpleGood.setMinBuyQty(rs.getGeneralDouble("gcd_minbuyqty")*rate);
			simpleGood.setReserve(rs.getGeneralDouble("reserve")*rate);
			simpleGood.setMinPackQty(rs.getGeneralDouble("gcd_minpackqty")*rate);
			List<GoodsQtyPriceUas> li = new ArrayList<GoodsQtyPriceUas>();
			GoodsQtyPriceUas price = new GoodsQtyPriceUas();
			price.setStart(1.0);
			price.setEnd(rs.getGeneralDouble("reserve")*rate);
			if(rs.getGeneralDouble("gcd_price")!= 0 && rs.getGeneralDouble("gcd_usdprice")!=0){
				price.setRMBPrice(rs.getGeneralDouble("gcd_price")/rate);
				price.setUSDPrice(rs.getGeneralDouble("gcd_usdprice")/rate);
				simpleGood.setDeliveryDemTime(Short.valueOf(rs.getString("gcd_deliverytime")));
				simpleGood.setDeliveryHKTime(Short.valueOf(rs.getString("gcd_hkdeliverytime")));
			}else if(rs.getGeneralDouble("gcd_price") == 0 && rs.getGeneralDouble("gcd_usdprice")!=0){
				price.setUSDPrice(rs.getGeneralDouble("gcd_usdprice")/rate);
				simpleGood.setDeliveryHKTime(Short.valueOf(rs.getString("gcd_hkdeliverytime")));
			}else{
				price.setRMBPrice(rs.getGeneralDouble("gcd_price")/rate);
				simpleGood.setDeliveryDemTime(Short.valueOf(rs.getString("gcd_deliverytime")));
			}
			li.add(price);
			simpleGood.setPrices(li);
			simpleGood.setQtyPrice(FlexJsonUtil.toJsonArrayDeep(li));
			simpleGood.setOriginal(rs.getInt("gd_original"));
			simpleGood.setBatchCode(rs.getString("gcd_b2bbatchcode"));
			simpleGood.setUuid(rs.getString("gcd_uuid"));
			simpleGoodses.add(simpleGood);
		}
		return simpleGoodses;
	}
}
