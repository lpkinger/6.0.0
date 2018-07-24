package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.SaleForecastService;

@Service("saleForecastService")
public class SaleForecastServiceImpl implements SaleForecastService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSaleForecast(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("SaleForecast", "sf_code", store.get("sf_code"));
		checkCode(store.get("sf_id"), store.get("sf_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, grid);
		/**
		 * @author wsy
		 * 反馈编号：2017050375
		 * 销售预测：主表币别为空，则取对应客户资料默认币别。
		 */
		Object sf_currency = store.get("sf_currency");
		if(sf_currency==null || "".equals(sf_currency)){
			Object cu_currency = baseDao.getFieldDataByCondition("Customer", "cu_currency", "cu_code='"+(store.get("sf_custcode")==null?"":store.get("sf_custcode"))+"'");
			store.put("sf_currency", (cu_currency==null?"":cu_currency));
		}
		// 保存SaleForecast
		String formSql = SqlUtil.getInsertSqlByMap(store, "SaleForecast");
		baseDao.execute(formSql);
		// 保存SaleForecastDetail
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			map.put("sd_statuscode", "ENTERING");
			map.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
			map.put("sd_code", store.get("sf_code"));
			/**
			 * @author wsy
			 * 销售预测：取销售价格表中的价格。
			 */
			SqlRowList res = baseDao.queryForRowSet("select spd_price from (select spd_price from SalePriceDetail left join SalePrice on spd_spid=sp_id where spd_arcustcode='"+store.get("sf_custcode")+"' and spd_prodcode='"+map.get("sd_prodcode")+"' and spd_currency='"+store.get("sf_currency")+"' and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' AND sp_status='已审核' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2");
			if(res.next()){
				map.put("sd_cost", res.getString("spd_price"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SaleForecastDetail", "sd_id");
		baseDao.execute(gridSql);
		/*
		 * maz   销售预测保存时，更新时，如明主表存在业务员，明细表业务员为空时强制赋值主表上的业务员  2017080319
		 */
		SqlRowList seller = baseDao.queryForRowSet("select em_code,em_name from saleforecast left join saleforecastdetail on sf_id=sd_sfid"
				+ " left join employee on sf_name=em_name where sd_sfid="+store.get("sf_id")+" and sd_sellercode is null and sd_seller is null and sf_name is not null");
		if(seller.next()){
			baseDao.execute("update saleforecastdetail set sd_sellercode='"+seller.getString("em_code")+"',sd_seller='"+seller.getString("em_name")+"' where sd_sfid="+store.get("sf_id")+" and sd_sellercode is null and sd_seller is null");
		}
		StringBuffer sb = new StringBuffer();
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from (select sd_prodcode,sd_detno,(sd_qty) qty from saleforecastdetail where sd_sfid=? )A left join product on A.sd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)",
						String.class, store.get("sf_id"));
		if (dets != null) {
			sb.append("行号:"+dets+" 下单数量小于最小订购量！ <br>");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from (select sd_prodcode,sd_detno,(sd_qty) qty from saleforecastdetail where sd_sfid=? )A left join product on A.sd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))",
						String.class, store.get("sf_id"));
		if (dets != null) {
			sb.append("行号:"+dets+" 下单数量不是最小包装量整数倍！ <br>");
		}
		if(sb.length()!=0){
			BaseUtil.appendError(sb.toString());
		}
		// 记录操作
		baseDao.logger.save(caller, "sf_id", store.get("sf_id"));
		// 设置失效日期
		setForeCastEndDate(caller, Integer.parseInt(store.get("sf_id").toString()));
		baseDao.execute("update SaleForecastDetail set sd_code=(select sf_code from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ store.get("sf_id") + " and not exists (select 1 from SaleForecast where sd_code=sf_code)");
		baseDao.execute("update SaleForecastDetail set sd_custcode=(select sf_custcode from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ store.get("sf_id") + " and nvl(sd_custcode,' ')=' '");
		baseDao.execute("update SaleForecastDetail set sd_custname=(select cu_name from Customer where sd_custcode=cu_code) where sd_sfid="
				+ store.get("sf_id"));
		baseDao.execute("update saleforecastdetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_sfid="
				+ store.get("sf_id"));
		/**
		 * 更新金额
		 */
		baseDao.updateByCondition("SaleForecastDetail", "sd_total=round(nvl(sd_cost*sd_qty,0),2)", "sd_sfid="+store.get("sf_id"));
		baseDao.updateByCondition("SaleForecast", "sf_total=(select sum(nvl(sd_total,0)) from SaleForecastDetail where sd_sfid='"+store.get("sf_id")+"')", "sf_id="+store.get("sf_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store, grid);
	}

	@Override
	public void deleteSaleForecast(int sf_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sf_id);
		// 删除SaleForecast
		baseDao.deleteById("SaleForecast", "sf_id", sf_id);
		// 删除SaleForecastDetail
		baseDao.deleteById("SaleForecastdetail", "sd_sfid", sf_id);
		// 记录操作
		baseDao.logger.delete(caller, "sf_id", sf_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sf_id);
	}

	private void checkCode(Object sf_id, Object sfcode) {
		// 判断预测单号在预测单中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(sf_code) from SaleForecast where sf_code=? and sf_id<>?",
				String.class, sfcode, sf_id);
		if (dets != null) {
			BaseUtil.showError("预测单号在销售预测单中已存在!预测单号：" + dets);
		}
		// 判断订单编号在销售预测单中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(sa_code) from Sale where sa_code=?", String.class, sfcode);
		if (dets != null) {
			BaseUtil.showError("预测单号在销售订单中已存在!订单号：" + dets);
		}
	}

	@Override
	public void updateSaleForecastById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		checkCode(store.get("sf_id"), store.get("sf_code"));
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + store.get("sf_id"));
		StateAssert.updateOnlyEntering(status);
		Object sf_currency = store.get("sf_currency");
		if(sf_currency==null || "".equals(sf_currency)){
			Object cu_currency = baseDao.getFieldDataByCondition("Customer", "cu_currency", "cu_code='"+(store.get("sf_custcode")==null?"":store.get("sf_custcode"))+"'");
			store.put("sf_currency", (cu_currency==null?"":cu_currency));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, store, gstore);
		//给更新人和更新时间赋值
		store.put("sf_updateman", SystemSession.getUser().getEm_name());
		store.put("sf_updatedate", DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));	
		// 修改SaleForecast
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleForecast", "sf_id");
		baseDao.execute(formSql);
		// 修改SaleForecastDetail 
		/*
		 *  maz 更新时需要先做更新在做插入 2017100112   17-10-16
		 */
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SaleForecastDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			s.put("sd_statuscode", "ENTERING");
			s.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
			s.put("sd_code", store.get("sf_code"));
			Object sdid = s.get("sd_id");
			SqlRowList res = baseDao.queryForRowSet("select spd_price from (select spd_price from SalePriceDetail left join SalePrice on spd_spid=sp_id where spd_arcustcode='"+store.get("sf_custcode")+"' and spd_prodcode='"+s.get("sd_prodcode")+"' and spd_currency='"+store.get("sf_currency")+"' and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' AND sp_status='已审核' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2");
			if(res.next()){
				s.put("sd_cost", res.getString("spd_price"));
			}
			if(sdid==null || "".equals(sdid) || "0".equals(sdid) || Integer.parseInt(sdid.toString())==0){
				String sql = SqlUtil.getInsertSql(s, "SaleForecastDetail", "sd_id");
				gridSql.add(sql);
			}
		}
		if(gstore.size()==0 || gstore==null){
			SqlRowList rs = baseDao.queryForRowSet("select * from SaleForecastDetail where sd_sfid="+store.get("sf_id"));
			while(rs.next()){
				SqlRowList res = baseDao.queryForRowSet("select spd_price from (select spd_price from SalePriceDetail left join SalePrice on spd_spid=sp_id where spd_arcustcode='"+store.get("sf_custcode")+"' and spd_prodcode='"+rs.getString("sd_prodcode")+"' and spd_currency='"+store.get("sf_currency")+"' and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' AND sp_status='已审核' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2");
				if(res.next()){
					baseDao.updateByCondition("SaleForecastDetail", "sd_cost='"+res.getString("spd_price")+"'", "sd_id="+rs.getInt("sd_id"));
				}
			}
		}
		baseDao.execute(gridSql);
		/*
		 * maz   销售预测保存时，更新时，如明主表存在业务员，明细表业务员为空时强制赋值主表上的业务员  2017080319
		 */
		SqlRowList seller = baseDao.queryForRowSet("select em_code,em_name from saleforecast left join saleforecastdetail on sf_id=sd_sfid left join employee on sf_name=em_name where sd_sfid="+store.get("sf_id")+" and sd_sellercode is null and sd_seller is null and sf_name is not null");
		if(seller.next()){
			baseDao.execute("update saleforecastdetail set sd_sellercode='"+seller.getString("em_code")+"',sd_seller='"+seller.getString("em_name")+"' where sd_sfid="+store.get("sf_id")+" and sd_sellercode is null and sd_seller is null");
		}
		StringBuffer sb = new StringBuffer();
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from (select sd_prodcode,sd_detno,(sd_qty) qty from saleforecastdetail where sd_sfid=? )A left join product on A.sd_prodcode=pr_code where nvl(pr_zxdhl,0)>0 and nvl(qty,0)<nvl(pr_zxdhl,0)",
						String.class, store.get("sf_id"));
		if (dets != null) {
			sb.append("行号:"+dets+" 下单数量小于最小订购量！ <br>");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from (select sd_prodcode,sd_detno,(sd_qty) qty from saleforecastdetail where sd_sfid=? )A left join product on A.sd_prodcode=pr_code where nvl(pr_zxbzs,0)>0 and nvl(qty,0)<>nvl(pr_zxbzs,0)*CEIL(nvl(qty,0)/nvl(pr_zxbzs,0))",
						String.class, store.get("sf_id"));
		if (dets != null) {
			sb.append("行号:"+dets+" 下单数量不是最小包装量整数倍！ <br>");
		}
		if(sb.length()!=0){
			BaseUtil.appendError(sb.toString());
		}
		/**
		 *  maz 2017090279  销售预测出货日期为空，则赋值主表日期
		 */
		SqlRowList rs = baseDao.queryForRowSet("select * from saleforecastdetail where sd_needdate is null and sd_sfid="+store.get("sf_id"));
		if(rs.next()){
			baseDao.execute("update saleforecastdetail a set sd_needdate=(select sf_date from saleforecast where sf_id=a.sd_sfid) where sd_needdate is null and sd_sfid="+store.get("sf_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "sf_id", store.get("sf_id"));
		// 设置失效日期
		setForeCastEndDate(caller, Integer.parseInt(store.get("sf_id").toString()));
		baseDao.execute("update SaleForecastDetail set sd_code=(select sf_code from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ store.get("sf_id") + " and not exists (select 1 from SaleForecast where sd_code=sf_code)");
		baseDao.execute("update SaleForecastDetail set sd_custcode=(select sf_custcode from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ store.get("sf_id") + " and nvl(sd_custcode,' ')=' '");
		baseDao.execute("update SaleForecastDetail set sd_custname=(select cu_name from Customer where sd_custcode=cu_code) where sd_sfid="
				+ store.get("sf_id"));
		baseDao.execute("update saleforecastdetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_sfid="
				+ store.get("sf_id"));
		
		baseDao.updateByCondition("SaleForecastDetail", "sd_total=round(nvl(sd_cost*sd_qty,0),2)", "sd_sfid="+store.get("sf_id"));
		baseDao.updateByCondition("SaleForecast", "sf_total=(select sum(nvl(sd_total,0)) from SaleForecastDetail where sd_sfid='"+store.get("sf_id")+"')", "sf_id="+store.get("sf_id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, store, gstore);
	}

	@Override
	public String[] printSaleForecast(int sf_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sf_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "sf_id", sf_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sf_id });
		return keys;
	}

	@Override
	public void auditSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail where sd_sfid = ? and sd_forecastcode<>' ' and  not exists (select sd_code,sd_detno from saleForecastdetail A where A.sd_code=saleForecastdetail.sd_forecastcode and A.sd_detno=saleForecastdetail.sd_forecastdetno)  ",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("冲减预测单号+冲减预测序号不存在，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail where sd_sfid = ? and sd_forecastcode<>' ' and  not exists  (select sd_code,sd_detno from saleForecastdetail A where A.sd_code=saleForecastdetail.sd_forecastcode and A.sd_detno=saleForecastdetail.sd_forecastdetno and sd_statuscode='AUDITED') ",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("冲减预测单号+序号状态不等于已审核，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail left join Product on sd_prodcode=pr_code left join Productlevel on pr_level=pl_levcode where sd_sfid=? and nvl(pl_nosale,0)<>0",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料的物料等级属性为不可销售，不允许进行当前操作！行号：" + dets);
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select A.*,B2.sd_qty rqty from SaleForecastDetail A left join SaleForecast B1 on A.sd_forecastcode=B1.sf_code left join SaleForecastDetail B2 on B1.sf_id=B2.sd_sfid and A.sd_forecastdetno=B2.sd_detno  where A.sd_sfid="
						+ sf_id + "  and A.sd_forecastcode<>' ' ");
		if (rs.next()) {
			Object yqty = baseDao.getFieldDataByCondition(
					"SaleForecastDetail,SaleForecast",
					"NVL(sum(sd_qty),0)",
					"sf_id=sd_sfid and sf_statuscode in ('ENTERING','COMMITED') and sd_id<>" + rs.getInt("sd_id") + "  and sf_code='"
							+ rs.getString("sd_forecastcode") + "' and sd_detno=" + rs.getInt("sd_forecastdetno"));
			if (rs.getDouble("sd_qty") > rs.getDouble("rqty") - Double.parseDouble(yqty.toString())) {
				BaseUtil.showError("序号:" + rs.getString("sd_detno") + "预测数大于被冲销的预测单的预测数:" + rs.getString("rqty") + "-已转未审核数:"
						+ yqty.toString());
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sf_id);
		// 执行审核操作
		baseDao.audit("SaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode", "sf_auditdate", "sf_auditman");
		baseDao.audit("SaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		baseDao.updateByCondition("SaleForecastDetail", "sd_sourceqty=sd_qty", "sd_sfid=" + sf_id);
		// 记录操作
		baseDao.logger.audit(caller, "sf_id", sf_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sf_id);
	}

	@Override
	public void resAuditSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resAuditOnlyAudit(status);
		SqlRowList rs = baseDao.queryForRowSet("select sd_detno from SaleForecastDetail where sd_sfid=" + sf_id + " and sd_clashsaleqty>0");
		if (rs.next()) {
			BaseUtil.showError("序号[" + rs.getString("sd_detno") + "]已经被冲销，不能反审核");
		}
		rs = baseDao.queryForRowSet("select sd_detno from SaleForecastDetail where sd_sfid=" + sf_id + " and sd_yqty>0");
		if (rs.next()) {
			BaseUtil.showError("序号[" + rs.getString("sd_detno") + "]已经被转销售单，不能反审核");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail where sd_sfid=? and (sd_code,sd_detno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id)",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("当前单存在销售预测变更单，不允许反审核!行号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, sf_id);
		// 执行反审核操作
		baseDao.resAudit("SaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode", "sf_auditdate", "sf_auditman");
		baseDao.resOperate("SaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sf_id", sf_id);
		// 执行反审核后取消产生的冲销单
		handlerService.afterResAudit(caller, sf_id);
	}

	@Override
	public void submitSaleForecast(int sf_id, String caller) {
		baseDao.execute("update SaleForecastDetail set sd_code=(select sf_code from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ sf_id + " and not exists (select 1 from SaleForecast where sd_code=sf_code)");
		baseDao.execute("update SaleForecastDetail set sd_custcode=(select sf_custcode from SaleForecast where sd_sfid=sf_id) where sd_sfid="
				+ sf_id + " and nvl(sd_custcode,' ')=' '");
		baseDao.execute("update SaleForecastDetail set sd_custname=(select cu_name from Customer where sd_custcode=cu_code) where sd_sfid="
				+ sf_id);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail where sd_sfid = ? and sd_forecastcode<>' ' and  not exists (select sd_code,sd_detno from saleForecastdetail A where A.sd_code=saleForecastdetail.sd_forecastcode and A.sd_detno=saleForecastdetail.sd_forecastdetno)  ",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("冲减预测单号+冲减预测序号不存在，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail where sd_sfid = ? and sd_forecastcode<>' ' and  not exists  (select sd_code,sd_detno from saleForecastdetail A where A.sd_code=saleForecastdetail.sd_forecastcode and A.sd_detno=saleForecastdetail.sd_forecastdetno and sd_statuscode='AUDITED') ",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("冲减预测单号+序号状态不等于已审核，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sd_detno) from SaleForecastDetail left join Product on sd_prodcode=pr_code left join Productlevel on pr_level=pl_levcode where sd_sfid=? and nvl(pl_nosale,0)<>0",
						String.class, sf_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料的物料等级属性为不可销售，不允许进行当前操作！行号：" + dets);
		}
		// 只能选择已审核的客户!
		Object code = baseDao.getFieldDataByCondition("SaleForecast", "sf_custcode", "sf_id=" + sf_id);
		status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + code + "'");
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customerBase.jsp?formCondition=cu_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		List<Object> codes2 = baseDao.getFieldDatasByCondition("SaleForecastDetail", "sd_custcode", "sd_sfid=" + sf_id);
		for (Object c : codes2) {
			status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + c + "'");
			if (status != null && !status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("SaleForecastDetail", "sd_prodcode", "sd_sfid=" + sf_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if(status==null){
				BaseUtil.showError("明细行物料编号："+c+"在物料资料中不存在");
			}
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select A.*,B2.sd_qty rqty from SaleForecastDetail A left join SaleForecast B1 on A.sd_forecastcode=B1.sf_code left join SaleForecastDetail B2 on B1.sf_id=B2.sd_sfid and A.sd_forecastdetno=B2.sd_detno  where A.sd_sfid="
						+ sf_id + "  and A.sd_forecastcode<>' ' ");
		if (rs.next()) {
			Object yqty = baseDao.getFieldDataByCondition(
					"SaleForecastDetail,SaleForecast",
					"NVL(sum(sd_qty),0)",
					"sf_id=sd_sfid and sf_statuscode in ('ENTERING','COMMITED') and sd_id<>" + rs.getInt("sd_id") + "  and sf_code='"
							+ rs.getString("sd_forecastcode") + "' and sd_detno=" + rs.getInt("sd_forecastdetno"));
			if (rs.getDouble("sd_qty") > rs.getDouble("rqty") - Double.parseDouble(yqty.toString())) {
				BaseUtil.showError("序号:" + rs.getString("sd_detno") + "预测数大于被冲销的预测单的预测数:" + rs.getString("rqty") + "-已转未审核数:"
						+ yqty.toString());
			}
		}
		// 判断出货日期是的早于最短出货天数。
		String minNeedDays = baseDao.getDBSetting(caller, "minNeedDays");
		minNeedDays = (minNeedDays == null || "null".equals(minNeedDays) || minNeedDays.equals("")) ? "-1" : minNeedDays;
		if (Integer.parseInt(minNeedDays) >= 0) {
			if (baseDao.isDBSetting(caller, "allowLeadTime")) {
				rs = baseDao
						.queryForRowSet("select count(1) n ,wm_concat(sd_detno) detno from Saleforecastdetail left join Product on sd_prodcode=pr_code where sd_sfid="
								+ sf_id + " and trunc(sd_needdate)<=trunc(sysdate+(" + minNeedDays + " + nvl(pr_leadtime,0))) ");
				if (rs.next()) {
					if (rs.getInt("n") > 0) {
						BaseUtil.showError("序号:" + rs.getString("detno") + "出货日期早于系统设置的最短出货天数：" + minNeedDays + "加上采购提前期");
					}
				}
			} else {
				rs = baseDao.queryForRowSet("select count(1) n ,wm_concat(sd_detno) detno from Saleforecastdetail where sd_sfid=" + sf_id
						+ " and trunc(sd_needdate)<=trunc(sysdate+" + minNeedDays + ") ");
			}
			if (rs.next()) {
				if (rs.getInt("n") > 0) {
					BaseUtil.showError("序号:" + rs.getString("detno") + "出货日期早于系统设置的最短出货天数：" + minNeedDays);
				}
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, sf_id);
		// 执行提交操作
		baseDao.submit("SaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode");
		baseDao.submit("SaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sf_id", sf_id);
		// 设置失效日期
		setForeCastEndDate(caller, sf_id);
		baseDao.execute("update saleforecastdetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_sfid="
				+ sf_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, sf_id);
	}

	@Override
	public void resSubmitSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sf_id);
		// 执行反提交操作
		baseDao.resOperate("SaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode");
		baseDao.resOperate("SaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sf_id", sf_id);
		handlerService.afterResSubmit(caller, sf_id);
	}

	@Override
	public void saveSaleForecastChangedate(String caller, String data) {
		// 修改SaleForecastDetail 出货日期和有效日期
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SaleForecastDetail", "sd_id");
		baseDao.execute(gridSql);
		// 记录操作
		if (gstore.size() > 0) {
			baseDao.logger.update(caller, "sf_id", gstore.get(0).get("sd_sfid"));
		}
	}

	@Override
	public void printSaleForeCast(int sf_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, sf_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "sf_id", sf_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, sf_id);
	}

	@Override
	public void openMrp(int id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select sd_sfid ,sd_detno from SaleForecastDetail where sd_id=" + id
				+ " and sd_mrpclosed<>0");
		if (rs.next()) {
			String sql = "update SaleForecastDetail set sd_mrpclosed='0' where sd_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.openMrp"), BaseUtil
					.getLocalMessage("msg.openMrpSuccess") + "序号" + rs.getString("sd_detno"), "SaleForecast|sf_id="
					+ rs.getString("sd_sfid")));
		}
	}

	@Override
	public void closeMrp(int id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select sd_sfid ,sd_detno from SaleForecastDetail where sd_id=" + id
				+ " and NVL(sd_mrpclosed,0)=0");
		if (rs.next()) {
			String sql = "update SaleForecastDetail set sd_mrpclosed='-1' where sd_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.closeMrp"), BaseUtil
					.getLocalMessage("msg.closeMrpSuccess") + "序号" + rs.getString("sd_detno"), "SaleForecast|sf_id="
					+ rs.getString("sd_sfid")));
		}
	}

	@Override
	public JSONObject getShortConfig(String condition) {
		JSONObject obj = new JSONObject();
		if (!condition.equals("")) {
			Object[] datas = baseDao.getFieldsDataByCondition("SaleForeCast", "sf_fromdate,sf_todate,sf_method",
					"sf_id=" + condition.split("=")[1]);
			obj.put("startdate", datas[0]);
			obj.put("enddate", datas[1]);
			obj.put("method", datas[2]);
		}
		return obj;
	}

	@Override
	public void updateShortForecast(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(param);
		Object sf_id = store.get("sf_id");
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, store, gridStore);
		List<String> sqls = new ArrayList<String>();
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleForecast", "sf_id");
		String getSql = "";
		int detno = 0;
		Object prodid = null;
		String arr[] = null;
		String fromdate = null;
		String todate = null;
		sqls.add(formSql);
		SqlRowList sl = baseDao.queryForRowSet("select max(sd_detno) from SaleForecastDetail where sd_sfid=" + sf_id);
		if (sl.next()) {
			detno = sl.getInt(1);
		}
		detno = detno < 0 ? 0 : detno;
		for (Map<Object, Object> map : gridStore) {
			prodid = map.get("sd_prodid");
			// 更新 按日期拆开
			for (@SuppressWarnings("rawtypes")
			Iterator iter = map.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if (key.contains("#")) {
					arr = key.split("#");
					fromdate = DateUtil.parseDateToOracleString(Constant.YMD, arr[0]);
					todate = DateUtil.parseDateToOracleString(Constant.YMD, arr[1]);
					Object sd_id = baseDao.getFieldDataByCondition("SaleForecastDetail", "sd_id", "sd_sfid=" + sf_id + " AND  sd_prodid='"
							+ prodid + "' AND sd_startdate=" + fromdate + " AND sd_enddate=" + todate);
					if (sd_id != null) {
						getSql = "update SaleForecastDetail  set sd_qty='" + map.get(key) + "' where  sd_id=" + sd_id;
					} else {
						detno++;
						// 数量为0 则不插入
						if (!"0".equals(map.get(key))) {
							getSql = "insert into SaleForecastDetail (sd_id,sd_sfid,sd_code,sd_detno,sd_custcode,sd_custname,sd_prodid,sd_prodcode,sd_qty,sd_startdate,sd_enddate) values("
									+ baseDao.getSeqId("SaleForecastDetail_SEQ")
									+ ","
									+ sf_id
									+ ",'"
									+ store.get("sf_code")
									+ "',"
									+ detno
									+ ",'"
									+ map.get("sd_custcode")
									+ "','"
									+ map.get("sd_custname")
									+ "','"
									+ map.get("sd_prodid")
									+ "','"
									+ map.get("sd_prodcode") + "','" + map.get(key) + "'," + fromdate + "," + todate + ")";
						}
					}
					sqls.add(getSql);
				}
			}

			/*
			 * }else {
			 * 
			 * }
			 */
		}
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update("WCPlan", "wc_id", store.get("wc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, store, gridStore);
	}

	@Override
	public void UpdateForecastQty(String data, String caller) {
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : lists) {
			// 更新预测数量
			sqls.add("update Saleforecastdetail set sd_qty=" + map.get("sd_needqty") + ",sd_needqty=" + map.get("sd_qty")
					+ "  where sd_id=" + map.get("sd_id"));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更改预测数量", "预测数量："+map.get("sd_qty")+",变更数量："+map.get("sd_needqty"),
					"SaleForecast!UpdateQty|sd_id=" + map.get("sd_id")));
		}
		baseDao.execute(sqls);
	}

	public void setForeCastEndDate(String caller, int id) {
		int leadday = 0;
		String Aheadday = "";
		// 获取配置表的有效期延迟天数
		Aheadday = baseDao.getDBSetting(caller, "lateDays");// -1~+N
		boolean haveenddate = baseDao.isDBSetting(caller, "haveEndDate");// Y/N
		if (!haveenddate) {
			leadday = 365;// 不考虑失效日期，则有效期为365天。
		} else {
			if (Aheadday == null || Aheadday.equals("")) {
				Aheadday = "0";
			}
			leadday = Integer.parseInt(Aheadday);
		}
		if (leadday == -1) {// -1，按照录入的失效日期为准
			return;
		}
		baseDao.execute("update Saleforecastdetail set sd_enddate=sd_needdate+" + leadday + " where sd_sfid=" + id);
	}

	@Override
	public void saleforecastdataupdate(int id, String caller) {
		Object kind = baseDao.getFieldDataByCondition("SaleForecast", "sf_needkind", "sf_id=" + id);
		String sqlstrb = "update saleforecastdetail SFD set SFD.sd_b=nvl((select sum(SFS.sd_qty) from SaleForecast,SaleForecastDetail SFS where sf_id=SFS.sd_sfid and SFS.sd_prodcode=SFD.sd_prodcode and nvl(sf_statuscode,' ')<>'FINISH' and sf_needkind='"
				+ kind + "' and nvl(sf_statuscode,' ')<>'ENTERING' and nvl(SFS.sd_statuscode,' ')<>'FINISH'),0) where sd_sfid=" + id;
		String sqlstrc = "update saleforecastdetail set sd_c=NVL((select sum(ma_qty-nvl(ma_madeqty,0)) from make where sd_prodcode=ma_prodcode and nvl(ma_statuscode,' ')<>'FINISH'),0) where sd_sfid="
				+ id;
		String sqlstrd = "update saleforecastdetail set sd_d=NVL((select sum(pw_onhand) from productwh,warehouse where pw_whcode=wh_code and pw_prodcode=sd_prodcode and nvl(wh_type,' ')='良品仓'),0) where sd_sfid="
				+ id;
		String sqlstre = "update saleforecastdetail set sd_e=NVL((select sum(pw_onhand) from productwh,warehouse where pw_whcode=wh_code and pw_prodcode=sd_prodcode and nvl(wh_type,' ')='不良品仓'),0) where sd_sfid="
				+ id;
		String sqlstrf = "update saleforecastdetail set sd_f=NVL((select sum(pd_outqty) from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode=sd_prodcode and pi_class='出货单' and pi_statuscode='POSTED' and dateadd('M',1,pi_date)>=sysdate ),0) where sd_sfid="
				+ id;
		String sqlstrg = "update saleforecastdetail set sd_g=NVL((select sum(sd_qty-nvl(sd_sendqty,0)) from sale,saledetail where sale.sa_id=saledetail.sd_said and saledetail.sd_prodcode=saleforecastdetail.sd_prodcode and nvl(sa_statuscode,' ')<>'FINISH' and nvl(sa_statuscode,' ')<>'ENTERING' and nvl(saledetail.sd_statuscode,' ')<>'FINISH'),0) where sd_sfid="
				+ id;
		List<String> sqls = new ArrayList<String>();
		sqls.add(sqlstrb);
		sqls.add(sqlstrc);
		sqls.add(sqlstrd);
		sqls.add(sqlstre);
		sqls.add(sqlstrf);
		sqls.add(sqlstrg);
		baseDao.execute(sqls);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void splitSaleForecast(String formdata, String data) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int sd_id = Integer.parseInt(formmap.get("sd_id").toString());
		int sfid = Integer.parseInt(formmap.get("sd_sfid").toString());
		Object enddate = baseDao.getFieldDataByCondition("saleForecastdetail", "sd_enddate", "sd_id=" + sd_id);
		int basedetno = Integer.parseInt(formmap.get("sd_detno").toString());
		double baseqty = 0, splitqty = 0;
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> currentMap = new HashMap<String, Object>();
		baseDao.execute("update saleForecastdetail set sd_originalqty=sd_qty,sd_originaldetno=sd_detno where sd_id=" + sd_id
				+ " and sd_originaldetno is null");
		SqlRowList cur = baseDao.queryForRowSet("select * from saleForecastdetail where sd_id=" + sd_id);
		if (cur.next()) {
			currentMap = cur.getCurrentMap();
			baseqty = cur.getDouble("sd_qty");
		} else
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		SqlRowList sl = baseDao.queryForRowSet("select max(sd_detno) from saleForecastdetail where sd_sfid=" + sfid);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object sdid = null;
		int sdqty = 0;
		int sddetno = 0;
		SqlRowList sl2 = null;
		// 判断总数量是否与拆分前一致
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			splitqty = NumberUtil.add(splitqty, Double.parseDouble(map.get("sd_qty").toString()));
		}
		if (splitqty != baseqty) {
			BaseUtil.showError("拆分后的总数跟当前序号总数不一致!");
		}
		Object newsd_enddate = null;
		Object newsd_qty = null;
		// 判断原始的序号 值不能
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			sdid = map.get("sd_id");
			sddetno = Integer.parseInt(map.get("sd_detno").toString());
			sdqty = Integer.parseInt(map.get("sd_qty").toString());
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				newsd_qty = sdqty;
				newsd_enddate = map.get("sd_enddate");
				// 说明是原来已经拆分的订单 更新数量和交货日期 前台判定会有问题
				sl2 = baseDao.queryForRowSet("select sd_qty,sd_clashsaleqty,sd_yqty from saleForecastdetail where sd_id=" + sdid);
				boolean b = baseDao.checkIf("saleForecastdetail", "sd_id=" + sd_id + " AND sd_yqty>" + sdqty);
				if (b) {
					// 原始拆分后数量 不能小于
					BaseUtil.showError("原始拆分后的数量不能小于转订单的数量!");
				}
				if (sl2.next()) {
					if (sdqty < sl2.getInt("sd_yqty")) {
						BaseUtil.showError("序号 :[" + sddetno + "] ,拆分后的数量小于已经转订单的数量，不能拆分!");
					} else {
						sqls.add("update saleForecastdetail set sd_qty=" + sdqty + ",sd_needdate=to_date('"
								+ map.get("sd_needdate").toString() + "','yyyy-MM-dd') where sd_id=" + sdid);
						if (map.get("sd_enddate") != null) {
							sqls.add("update saleForecastdetail set sd_enddate=to_date('" + map.get("sd_enddate").toString()
									+ "','yyyy-MM-dd') where sd_id=" + sdid);
						}
					}
				} else
					BaseUtil.showError("序号 :[" + sddetno + "] ，明细数据已经不存在，不能拆分!");
			} else {

				boolean bool = true;
				while (bool) {
					newdetno++;
					bool = baseDao.checkIf("saleForecastdetail", "sd_sfid=" + sfid + " AND sd_detno=" + newdetno);
					if (!bool)
						break;
				}
				currentMap.remove("sd_enddate");
				currentMap.put("sd_enddate", enddate);
				currentMap.remove("sd_needdate");
				currentMap.put("sd_needdate", map.get("sd_needdate").toString());
				currentMap.remove("sd_detno");
				currentMap.put("sd_detno", newdetno);
				currentMap.remove("sd_id");
				currentMap.put("sd_id", baseDao.getSeqId("SALEFORECASTDETAIL_SEQ"));
				currentMap.remove("sd_qty");
				currentMap.put("sd_qty", sdqty);
				currentMap.remove("sd_clashsaleqty");
				currentMap.put("sd_clashsaleqty", 0);
				currentMap.remove("sd_yqty");
				currentMap.put("sd_yqty", 0);
				currentMap.remove("sd_originaldetno");
				currentMap.put("sd_originaldetno", basedetno);
				currentMap.remove("sd_originalqty");
				currentMap.put("sd_originalqty", baseqty);
				sqls.add(SqlUtil.getInsertSqlByMap(currentMap, "saleforecastdetail"));
			}
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "销售预测单拆分", "明细行:" + basedetno + "=>被拆分,原数量："+baseqty+"、原截止日期："+enddate.toString()
		+",新数量："+newsd_qty+"、新截止日期："+newsd_enddate.toString(),
				"SaleForecast|sf_id=" + sfid));
	}
}
