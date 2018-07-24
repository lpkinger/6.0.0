package com.uas.erp.service.scm.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.BorrowApplyDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.BorrowApplyService;

@Service("borrowApplyService")
public class BorrowApplyServiceImpl implements BorrowApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BorrowApplyDao borrowApplyDao;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveBorrowApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BorrowApply", "ba_code='" + store.get("ba_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存BorrowApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BorrowApply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存BorrowApplyDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "BorrowApplyDetail", "bad_id");
		baseDao.execute(gridSql);
		String sql="update BorrowApplyDetail set bad_amount=round(bad_price*bad_qty,2) where bad_baid="+store.get("ba_id");
		baseDao.execute(sql);
		baseDao.logger.save(caller, "ba_id", store.get("ba_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deleteBorrowApply(int ba_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + ba_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(bad_detno) from BorrowApplyDetail where bad_baid=? and nvl(bad_yqty,0)>0", String.class, ba_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转借货出货单，不允许删除!行号：" + dets);
		}
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ba_id});
		//删除BorrowApply
		baseDao.deleteById("BorrowApply", "ba_id", ba_id);
		//删除BorrowApplyDetail
		baseDao.deleteById("borrowapplydetail", "bad_baid", ba_id);
		//记录操作
		baseDao.logger.delete(caller, "ba_id", ba_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ba_id});
	}
	
	@Override
	public void updateBorrowApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + store.get("ba_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改BorrowApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BorrowApply", "ba_id");
		baseDao.execute(formSql);
		//修改BorrowApplyDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BorrowApplyDetail", "bad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bad_id") == null || s.get("bad_id").equals("") || s.get("bad_id").equals("0") ||
					Integer.parseInt(s.get("bad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BORROWAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BorrowApplyDetail", new String[]{"bad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String sql="update BorrowApplyDetail set bad_amount=round(bad_price*bad_qty,2) where bad_baid="+store.get("ba_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logger.update(caller, "ba_id", store.get("ba_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void printBorrowApply(int ba_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{ba_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "ba_id", ba_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ba_id});
	}
	@Override
	public void auditBorrowApply(int ba_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + ba_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bad_detno) from BorrowApplyDetail left join BorrowApply on bad_baid=ba_id left join BorrowCargoType on bt_name=ba_outtype "
								+ "where bad_baid=? and nvl(bad_whcode,' ')<>' ' and nvl(bt_whcodes,' ')<>' ' and bad_whcode not in (select column_value from table(parsestring(bt_whcodes,'#')))", String.class, ba_id);
		if (dets != null) {
			BaseUtil.showError("明细仓库与借货类型允许入仓库不一致，不允许进行当前操作!行号：" + dets);
		}
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ba_id);
		//执行审核操作
		baseDao.audit("BorrowApply", "ba_id=" + ba_id, "ba_status", "ba_statuscode", "ba_auditdate", "ba_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ba_id", ba_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ba_id);
	}
	@Override
	public void resAuditBorrowApply(int ba_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + ba_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(bad_detno) from BorrowApplyDetail where bad_baid=? and nvl(bad_yqty,0)>0", String.class, ba_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转借货出货单，不允许反审核!行号：" + dets);
		}
		//执行反审核操作
		baseDao.resAudit("BorrowApply", "ba_id=" + ba_id, "ba_status", "ba_statuscode", "ba_auditdate", "ba_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "ba_id", ba_id);
	}
	
	final static String SALE_PRICE_PC = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC) where rownum<2";
	@Override
	public void submitBorrowApply(int ba_id, String caller) {
		StringBuffer error = new StringBuffer();
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + ba_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bad_detno) from BorrowApplyDetail left join BorrowApply on bad_baid=ba_id left join BorrowCargoType on bt_name=ba_outtype "
								+ "where bad_baid=? and nvl(bad_whcode,' ')<>' ' and nvl(bt_whcodes,' ')<>' ' and bad_whcode not in (select column_value from table(parsestring(bt_whcodes,'#')))", String.class, ba_id);
		if (dets != null) {
			BaseUtil.showError("明细仓库与借货类型允许入仓库不一致，不允许进行当前操作!行号：" + dets);
		}
		List<Object[]> objects = baseDao.getFieldsDatasByCondition(
				"BorrowApplyDetail left join BorrowApply on ba_id=bad_baid", new String[] { "bad_prodcode",
						"ba_currency", "bad_qty", "bad_id" }, " bad_baid=" + ba_id);
		for (Object[] obj : objects) {
			Object sumqty = baseDao.getFieldDataByCondition("BorrowApplyDetail", "sum(bad_qty)",
					" bad_baid=" + ba_id + " and bad_prodcode='" + String.valueOf(obj[0]) + "'");
			SqlRowList rs = baseDao.queryForRowSet(SALE_PRICE_PC, String.valueOf(obj[0]), String.valueOf(obj[1]), Double.parseDouble(sumqty.toString()));
			double price = 0;
			double tax = 0;
			double qty = Double.parseDouble(obj[2].toString());
			double p = 0;
			double total = 0;
			if (rs.next()) {
				price = rs.getDouble("spd_price");
				tax = rs.getDouble("spd_taxrate");
			}
			if (price != 0) {
				p = NumberUtil.formatDouble(price, 6);
				total = NumberUtil.formatDouble(qty * p, 2);
				baseDao.updateByCondition("BorrowApplyDetail", "bad_price=" + p + ",bad_taxrate=" + tax + ",bad_amount=" + total, "bad_id=" + obj[3] + " and nvl(bad_price,0)=0");
			} else {
				error.append("根据 物料编号:[" + obj[0] + "],币别:[" + obj[2] + "],分段数量：["+sumqty+"] 在销售单价表未找到对应单价，或单价为空值、0等!<BR/>");
			}
		}
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ba_id});
		//执行提交操作
		baseDao.updateByCondition("BorrowApply", "ba_statuscode='COMMITED',ba_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "ba_id=" + ba_id);
		//记录操作
		baseDao.logger.submit(caller, "ba_id", ba_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ba_id});
		if (error.length() > 0) {
			BaseUtil.appendError(error.toString());
		}
	}
	@Override
	public void resSubmitBorrowApply(int ba_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BorrowApply", "ba_statuscode", "ba_id=" + ba_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler("BorrowApply", "resCommit", "before", new Object[]{ba_id});
		//执行反提交操作
		baseDao.updateByCondition("BorrowApply", "ba_statuscode='ENTERING',ba_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ba_id=" + ba_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ba_id", ba_id);
		handlerService.handler("BorrowApply", "resCommit", "after", new Object[]{ba_id});
	}
	@Override
	public int turnBorrow(int id, String caller) {
		int piid = 0;
		Object code = baseDao.getFieldDataByCondition("BorrowApply", "ba_code", "ba_id=" + id);
		final String cod=code+"";
		code = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_sourcecode='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.BorrowApply.haveturn"));
		} else {
			final Object[] formdata=baseDao.getFieldsDataByCondition("BorrowApply", new String[]{"ba_code","ba_custid","ba_custcode","ba_custname",
					"ba_departcode","ba_department","ba_description","ba_currency","ba_rate"}, "ba_id=" + id);
			String formSql="INSERT INTO prodinout (pi_id,pi_cardid,pi_cardcode,pi_title,pi_departmentcode,pi_departmentname,pi_recorddate," +
					"pi_recordman,pi_updateman,pi_updatedate,pi_invostatuscode,pi_invostatus,pi_class,pi_sourcecode,pi_inoutno,pi_statuscode," +
					"pi_status,pi_printstatuscode,pi_printstatus,pi_remark,pi_currency,pi_rate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			piid=baseDao.getSeqId("PRODINOUT_SEQ");
			final int pi_id=piid;
			baseDao.getJdbcTemplate().update(formSql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, pi_id);
					ps.setInt(2, Integer.parseInt(formdata[1]+""));
					ps.setString(3, formdata[2]+"");
					ps.setString(4, formdata[3]+"");
					ps.setString(5, formdata[4]+"");
					ps.setString(6, formdata[5]+"");//pi_departmentname
					ps.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
					ps.setString(8, SystemSession.getUser().getEm_name());
					ps.setString(9, SystemSession.getUser().getEm_name());
					ps.setDate(10, new java.sql.Date(new java.util.Date().getTime()));
					ps.setString(11, "ENTERING");
					ps.setString(12, "在录入");
					ps.setString(13, "借货出货单");
					ps.setString(14, cod);
					ps.setString(15, baseDao.sGetMaxNumber("ProdInOut!SaleBorrow", 2));
					ps.setString(16, "UNPOST");
					ps.setString(17, BaseUtil.getLocalMessage("UNPOST"));
					ps.setString(18, "UNPRINT");
					ps.setString(19, BaseUtil.getLocalMessage("UNPRINT"));
					ps.setString(20, formdata[6]+"");
					ps.setString(21, formdata[7]+"");
					ps.setDouble(22, Double.parseDouble(formdata[8].toString()));
				}
			});
			List<Object[]> gridData=baseDao.getFieldsDatasByCondition("BorrowApplyDetail", new String[]{"bad_detno","bad_prodcode","bad_qty","bad_price","bad_amount","bad_remark","bad_id"}, "bad_baid=" + id + " and nvl(vad_yqty,0) < bad_qty");
			String gridSql="INSERT INTO ProdIODetail (pd_pdno,pd_prodcode,pd_outqty,pd_sendprice,pd_ordertotal,pd_remark,pd_piid,pd_id,pd_piclass," +
					"pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_status,pd_reply,pd_orderid,pd_ordercode,pd_orderdetno) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			for(final Object[] o:gridData){
				baseDao.getJdbcTemplate().update(gridSql, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, Integer.parseInt(o[0]+""));
						ps.setString(2, o[1]+"");
						ps.setDouble(3, Double.parseDouble(o[2]+""));
						ps.setDouble(4, Double.parseDouble(o[3]+""));
						ps.setDouble(5, Double.parseDouble(o[4]+""));
						ps.setString(6, o[5]+"");
						ps.setInt(7, pi_id);
						ps.setInt(8, baseDao.getSeqId("ProdIODetail_Seq"));
						ps.setString(9, "借货出货单");
						ps.setString(10, "ENTERING");
						ps.setString(11,"UNACCOUNT");
						ps.setString(12, BaseUtil.getLocalMessage("UNACCOUNT"));
						ps.setInt(13, 0);
						ps.setString(14, "未归还");
						ps.setInt(15, Integer.parseInt(o[6]+""));
						ps.setString(16, cod);
						ps.setInt(17, Integer.parseInt(o[0]+""));
					}
				});
				baseDao.getJdbcTemplate().update("update BorrowApplyDetail set bad_yqty=?,bad_statuscode='TURNPRODIO',bad_status='已转出货单' where bad_id=?", Double.parseDouble(o[2]+""), Integer.parseInt(o[6]+""));
			}
			//修改申请单状态
			baseDao.updateByCondition("BorrowApply", "ba_statuscode='TURNPRODIO',ba_status='已转出货单'", "ba_id=" + id);
			//记录操作
			baseDao.logger.turn("转借货出货单", "BorrowApply", "ba_id", id);
		}
		return piid;
	}
	@Override
	public String turnProdBorrow(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量、状态
		borrowApplyDao.checkAdYqty(maps);
		if (maps.size() > 0) {
			// 转入通知单主记录
			Integer ba_id = baseDao.getFieldValue("BorrowApplyDetail", "bad_baid", "bad_id=" + maps.get(0).get("bad_id"), Integer.class);
			Key key = transferRepository.transfer(caller, ba_id);
			int pi_id = key.getId();
			// 转入明细
			transferRepository.transfer(caller, maps, key);
			baseDao.execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '", pi_id);
			baseDao.execute("update PRODIODETAIL set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_piid=?", pi_id);
			baseDao.execute("update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?", pi_id);
			// 修改借货申请单状态
			for (Map<Object, Object> map : maps) {
				int badid = Integer.parseInt(map.get("bad_id").toString());
				borrowApplyDao.checkBADQty(badid, null);
			}
			return "转入成功,借货出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
					+ pi_id + "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!SaleBorrow')\">"
					+ key.getCode() + "</a>&nbsp;";
		}
		return null;
	}
}
