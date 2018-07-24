package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.PreSaleForecastService;


@Service("preSaleForecastService")
public class PreSaleForecastServiceImpl implements PreSaleForecastService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public JSONObject getPreConfig(String condition) {
		JSONObject obj = new JSONObject();
		if (!condition.equals("")) {
			Object[] datas = baseDao.getFieldsDataByCondition("PreSaleForeCast", "sf_fromdate,sf_todate,sf_method,sf_monthfrom,sf_monthto,sf_dayfrom,sf_dayto",
					"sf_id=" + condition.split("=")[1]);
			if("周".equals(datas[2].toString())){
				obj.put("startdate", datas[0]);
				obj.put("enddate", datas[1]);
			}else if("月".equals(datas[2].toString())){
				obj.put("startdate", datas[3]);
				obj.put("enddate", datas[4]);	
			}else if("天".equals(datas[2].toString())){
				obj.put("startdate", datas[5]);
				obj.put("enddate", datas[6]);	
			}
			obj.put("method", datas[2]);
		}
		return obj;
	}

	@Override
	public void savePreSaleForecast(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PreSaleForecast", "sf_code='" + store.get("sf_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存SaleForecast
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreSaleForecast", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SaleForecastDetail
		Object[] sd_id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			sd_id[i] = baseDao.getSeqId("PRESALEFORECASTDETAIL_SEQ");
			map.put("sd_id", sd_id[i]);
			map.put("sd_statuscode", "ENTERING");
			map.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
			map.put("sd_code", store.get("sf_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PreSaleForecastDetail");
		baseDao.execute(gridSql);
		//保存后明细行客户编号、名称同时为空的  把主表的客户编号和名称更新进去  maz 2018010583
		if(StringUtil.hasText(store.get("sf_custcode")) && StringUtil.hasText(store.get("sf_custname"))){
			SqlRowList rs = baseDao.queryForRowSet("select sd_id from PreSaleForecastDetail where sd_sfid="+store.get("sf_id")+" and sd_custcode is null and sd_custname is null");
			while(rs.next()){
				baseDao.execute("update PreSaleForecastDetail set sd_custcode='"+store.get("sf_custcode")+"',sd_custname='"+store.get("sf_custname")+"' where sd_id="+rs.getInt("sd_id")+"");
			}
		}
		baseDao.logger.save(caller, "sf_id", store.get("sf_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updatePreForecast(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstores = BaseUtil.parseGridStoreToMaps(param);
		Object sf_id = store.get("sf_id");
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gridstores });
		List<String> sqls = new ArrayList<String>();
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreSaleForecast", "sf_id");
		String getSql = "";
		int detno = 1;
		Object prodid = null;
		String arr[] = null;
		String fromdate = null;
		String todate = null;
		Object custcode = null;
		sqls.add(formSql);
		SqlRowList sl = baseDao.queryForRowSet("select max(sd_detno) from PreSaleForecastDetail where sd_sfid=" + sf_id);
		if (sl.next()) {
			detno = sl.getInt(1);
		}
		for (Map<Object, Object> map : gridstores) {
			prodid = map.get("sd_prodid");
			custcode = "".equals(map.get("sd_custcode"))?" ":map.get("sd_custcode");
			Object sd_detno = baseDao.getFieldDataByCondition("PreSaleForecastDetail", "nvl(max(sd_detno),0)", "sd_sfid=" + sf_id
					+ " AND sd_prodid='" + prodid + "' AND nvl(sd_custcode,' ')='" + custcode + "'");
			if (sd_detno != null && Integer.parseInt(sd_detno.toString()) != 0) {
				detno = Integer.parseInt(sd_detno.toString());
			} else {
				detno++;
			}
			/* if(keyvalue!=null&&!keyvalue.equals("0")){ */
			// 更新 按日期拆开
			for (@SuppressWarnings("rawtypes")
			Iterator iter = map.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if (key.contains("#")) {
					arr = key.split("#");
					fromdate = DateUtil.parseDateToOracleString(Constant.YMD, arr[0]);
					todate = DateUtil.parseDateToOracleString(Constant.YMD, arr[1]);
					Object sd_id = baseDao.getFieldDataByCondition("PreSaleForecastDetail", "sd_id", "sd_sfid=" + sf_id
							+ " AND  sd_prodid='" + prodid + "' AND trunc(sd_startdate)=" + fromdate + " AND trunc(sd_enddate)=" + todate
							+ " AND nvl(sd_custcode,' ')='" + custcode + "'");
					if (sd_id != null) {
						getSql = "update PreSaleForecastDetail  set sd_qty='" + map.get(key) + "',sd_custcode='" + map.get("sd_custcode")
								+ "',sd_custname='" + map.get("sd_custname") + "',sd_person='" + map.get("sd_person") + "' where  sd_id="
								+ sd_id;
					} else {
						// detno++;
						// 数量为0 则不插入
						if (!"0".equals(map.get(key))) {
							getSql = "insert into PreSaleForecastDetail (sd_id,sd_sfid,sd_code,sd_detno,sd_custcode,sd_custname,sd_prodid,sd_prodcode,sd_qty,sd_startdate,sd_enddate,sd_person) values("
									+ baseDao.getSeqId("PreSaleForecastDetail_SEQ")
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
									+ map.get("sd_prodcode")
									+ "','"
									+ map.get(key)
									+ "',"
									+ fromdate
									+ ","
									+ todate
									+ ",'"
									+ map.get("sd_person") + "')";
						}
					}
					sqls.add(getSql);
				}
			}
		}
		baseDao.execute(sqls);
		//更新后明细行客户编号、名称同时为空的  把主表的客户编号和名称更新进去  maz 2018010583
		if(StringUtil.hasText(store.get("sf_custcode")) && StringUtil.hasText(store.get("sf_custname"))){
			SqlRowList rs = baseDao.queryForRowSet("select sd_id from PreSaleForecastDetail where sd_sfid="+store.get("sf_id")+" and sd_custcode is null and sd_custname is null");
			while(rs.next()){
				baseDao.execute("update PreSaleForecastDetail set sd_custcode='"+store.get("sf_custcode")+"',sd_custname='"+store.get("sf_custname")+"' where sd_id="+rs.getInt("sd_id")+"");
			}
		}
		// 记录操作
		baseDao.logger.update(caller, "sf_id", store.get("sf_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gridstores });
	}

	@Override
	public void auditPreSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sf_id);
		// 执行审核操作
		/**
		 * wusy
		 */
		baseDao.updateByCondition("PreSaleForecastDetail", "sd_sourceqty=sd_qty", "sd_sfid="+sf_id);
		baseDao.audit("PreSaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode", "sf_auditdate", "sf_auditman");
		baseDao.audit("PreSaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "sf_id", sf_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sf_id);
	}

	@Override
	public void resAuditPreSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, sf_id);
		String sfcodes = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(DISTINCT sf_code) from SaleForecastDetail left join SaleForecast on sd_sfid =sf_id "
				+ "where sd_id in(select SD_SOURCEID from  PreSaleForeCastdetail where sd_sfid=? and sd_statuscode='TURNSF')", String.class, sf_id);
		if (sfcodes != null) {
			BaseUtil.showError("已转销售预测不能反审核，销售预测单号：" + sfcodes);
		}
		// 执行反审核操作
		baseDao.resAudit("PreSaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode", "sf_auditdate", "sf_auditman");
		baseDao.resOperate("PreSaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sf_id", sf_id);
		// 执行反审核后取消产生的冲销单
		handlerService.afterResAudit(caller, sf_id);
	}

	@Override
	public void submitPreSaleForecast(int sf_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, sf_id);
		// 判断出货日期是的早于最短出货天数。
		SqlRowList rs = null;
		String minNeedDays = baseDao.getDBSetting(caller, "minNeedDays");
		minNeedDays = (minNeedDays==null || minNeedDays.equals("")) ? "-1" : minNeedDays;
		if (Integer.parseInt(minNeedDays) >= 0) {
			if (baseDao.isDBSetting(caller, "allowLeadTime")) {
				rs = baseDao
						.queryForRowSet("select sd_prodcode, nvl(pr_leadtime,0) pr_leadtime,trunc(sysdate+("
								+ minNeedDays
								+ " + nvl(pr_leadtime,0))) enddate from PreSaleforecastdetail left join Product on sd_prodcode=pr_code where sd_sfid="
								+ sf_id + " and nvl(sd_qty,0)>0 and trunc(SD_STARTDATE)<=trunc(sysdate+(" + minNeedDays
								+ " + nvl(pr_leadtime,0))) ");
				while (rs.next()) {
					BaseUtil.showError("物料[" + rs.getObject("sd_prodcode") + "]出货日期过早不能提交，预测出货日期不能早于今天+固定" + minNeedDays + "天+采购提前期"
							+ rs.getObject("pr_leadtime") + "天，即" + rs.getGeneralTimestamp("enddate", Constant.YMD) + "！");
				}
			} else {
				rs = baseDao.queryForRowSet("select sd_prodcode from PreSaleforecastdetail where sd_sfid=" + sf_id
						+ " and nvl(sd_qty,0)>0 and trunc(SD_STARTDATE)<=trunc(sysdate+" + minNeedDays + ") ");
				while (rs.next()) {
					BaseUtil.showError("物料[" + rs.getObject("sd_prodcode") + "]出货日期过早，不能提交，截止日期必须在" + minNeedDays + "天之后！");
				}
			}
		}
		// 执行提交操作
		baseDao.submit("PreSaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode");
		baseDao.submit("PreSaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sf_id", sf_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, sf_id);
	}

	@Override
	public void resSubmitPreSaleForecast(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sf_id);
		// 执行反审核操作
		baseDao.resOperate("PreSaleForecast", "sf_id=" + sf_id, "sf_status", "sf_statuscode");
		baseDao.resOperate("PreSaleForecastDetail", "sd_sfid=" + sf_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sf_id", sf_id);
		// 执行反审核后取消产生的冲销单
		handlerService.afterResSubmit(caller, sf_id);
	}

	@Override
	public void updatePreSaleForecastById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + store.get("sf_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改SaleForecast
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreSaleForecast", "sf_id");
		baseDao.execute(formSql);
		// 修改SaleForecastDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PreSaleForecastDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sd_id") == null || s.get("sd_id").equals("") || s.get("sd_id").equals("0")
					|| Integer.parseInt(s.get("sd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PRESALEFORECASTDETAIL_SEQ");
				s.put("sd_id", id);
				s.put("sd_statuscode", "ENTERING");
				s.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s, "PreSaleForecastDetail", new String[] { "sd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sf_id", store.get("sf_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deletePreSaleForecast(int sf_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode", "sf_id=" + sf_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sf_id);
		// 删除SaleForecast
		baseDao.deleteById("PreSaleForecast", "sf_id", sf_id);
		// 删除SaleForecastDetail
		baseDao.deleteById("PreSaleForecastdetail", "sd_sfid", sf_id);
		// 记录操作
		baseDao.logger.delete(caller, "sf_id", sf_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sf_id);
	}

	@Override
	public void deletePreSaleForecastDetail(String sd_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreSaleForecast", "sf_statuscode",
				"sf_id=(select sd_sfid from PreSaleForecastDetail where sd_id='" + sd_id + "')");
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("PreSaleForecastDetail", sd_id);
		Object[] obj = baseDao.getFieldsDataByCondition("PreSaleForecastdetail", new String[] { "sd_sfid", "sd_detno" }, "sd_id='" + sd_id
				+ "'");
		baseDao.deleteByCondition("PreSaleForecastdetail", "sd_sfid='" + obj[0] + "' and sd_detno='" + obj[1] + "'");
		// 记录操作
		baseDao.logger.delete("PreSaleForecastDetail", "sd_id", sd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("PreSaleForecastDetail", sd_id);
	}

	@Override
	public void savePreSaleForecastChangedate(String caller, String data) {
		// 修改SaleForecastDetail 出货日期和有效日期
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PreSaleForecastDetail", "sd_id");
		baseDao.execute(gridSql);
		// 记录操作
		if (gstore.size() > 0) {
			baseDao.logger.update("PreSaleForecast", "sf_id", gstore.get(0).get("sd_sfid"));
		}
	}
/**
 * 业务员预测复制方法
 */
	@Override
	public String copyPreSaleForecast(int sf_id, String caller,String forecast,String weeks,String weeke,String months,String monthe,String days,String daye) {
		int id = baseDao.getSeqId("PreSaleForecast_seq");		
		String code = baseDao.sGetMaxNumber("PreSaleForecast", 2);
		int time = 0;
		int time1 = 0;
			try{
				time = DateUtil.countDates(weeks, weeke);
			}catch (Exception e) {
				
			}
			try{
				time1 = DateUtil.countDates(days, daye);
			}catch (Exception e) {
				
			}
			if("周".equals(forecast)){  //选择周时插入的语句
			String sql = "insert into PreSaleForecast(sf_id,sf_code,sf_date,sf_username,sf_userid,sf_status,sf_statuscode,sf_tilldate,sf_monthselect,sf_fromdate,sf_todate,"
					+ "sf_department,sf_method,sf_remark,sf_monthfrom,sf_monthto) select "
					+ id
					+ ",'"
					+ code
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ SystemSession.getUser().getEm_id()
					+ "',"
					+ "'在录入','ENTERING',sf_tilldate,sf_monthselect,to_date('"+weeks+"','yyyy-mm-dd'),to_date('"+weeke+"','yyyy-mm-dd'),sf_department,'"+forecast+"',sf_remark,sf_monthfrom,sf_monthto from PreSaleForecast where sf_id="
					+ sf_id;
			double numb = time/7f;
			numb=Math.ceil(numb);
			int number = (int)numb;
			for(int i=0;i<number;i++){
				String detailSql = "insert into PreSaleForecastDetail(sd_id,sd_sfid,sd_statuscode,sd_status,sd_code,sd_detno,sd_prodcode,sd_custcode,sd_custname,sd_person,sd_enddate,"
						+ "sd_startdate,sd_prodid,sd_qty) select PreSaleForecastDetail_seq.nextval,"
						+ id
						+ ",'ENTERING','在录入','"
						+ code
						+ "',sd_detno,"
						+ "sd_prodcode,sd_custcode,sd_custname,sd_person,to_date('"+weeks+"','yyyy-mm-dd')+6+7*"+i+",to_date('"+weeks+"','yyyy-mm-dd')+7*"+i+",sd_prodid,0 from (select sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,"
						+ "sd_prodid,sd_detno from PreSaleForecastDetail where sd_sfid="
						+ sf_id+" group by sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,"
						+ "sd_prodid,sd_detno)";			
				baseDao.execute(new String[] {detailSql });	
			}	
			baseDao.execute(new String[] { sql});			
		}else if("月".equals(forecast)){   //选择月时插入的语句
			String mstarty = months.substring(0,4);
			String mendy = monthe.substring(0,4);
			String mstartm = months.substring(4,6);
			String mendm = monthe.substring(4,6);
			String datestart = mstarty+"-"+mstartm+"-"+"01";
			String dateend = DateUtil.getMaxMonthDate(datestart);
			int count = (Integer.parseInt(mendy)-Integer.parseInt(mstarty))*12+Integer.parseInt(mendm)-Integer.parseInt(mstartm);
			String sql = "insert into PreSaleForecast(sf_id,sf_code,sf_date,sf_username,sf_userid,sf_status,sf_statuscode,sf_tilldate,sf_monthselect,sf_fromdate,sf_todate,"
					+ "sf_department,sf_method,sf_remark,sf_monthfrom,sf_monthto) select "
					+ id
					+ ",'"
					+ code
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ SystemSession.getUser().getEm_id()
					+ "',"
					+ "'在录入','ENTERING',sf_tilldate,sf_monthselect,sf_fromdate,sf_todate,sf_department,'"+forecast+"',sf_remark,"+months+","+monthe+" from PreSaleForecast where sf_id="
					+ sf_id;
			for(int i=0;i<=count;i++){
				String detailSql = "insert into PreSaleForecastDetail(sd_id,sd_sfid,sd_statuscode,sd_status,sd_code,sd_detno,sd_prodcode,sd_custcode,sd_custname,sd_person,sd_enddate,"
						+ "sd_startdate,sd_prodid,sd_qty) select PreSaleForecastDetail_seq.nextval,"
						+ id
						+ ",'ENTERING','在录入','"
						+ code
						+ "',sd_detno,"
						+ "sd_prodcode,sd_custcode,sd_custname,sd_person,add_months(to_date('"+dateend+"','yyyy-mm-dd'),"+i+"),add_months(to_date('"+datestart+"','yyyy-mm-dd'),"+i+"),sd_prodid,0 from  (select sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,"
						+ "sd_prodid,sd_detno from PreSaleForecastDetail where sd_sfid="
						+ sf_id+" group by  sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,sd_prodid,sd_detno)";		
				baseDao.execute(new String[] { detailSql });
			}
			baseDao.execute(new String[] { sql});		
		}else if("天".equals(forecast)){
			String sql = "insert into PreSaleForecast(sf_id,sf_code,sf_date,sf_username,sf_userid,sf_status,sf_statuscode,sf_tilldate,sf_monthselect,sf_dayfrom,sf_dayto,"
					+ "sf_department,sf_method,sf_remark,sf_monthfrom,sf_monthto,sf_fromdate,sf_todate) select "
					+ id
					+ ",'"
					+ code
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ SystemSession.getUser().getEm_id()
					+ "',"
					+ "'在录入','ENTERING',sf_tilldate,sf_monthselect,to_date('"+days+"','yyyy-mm-dd'),to_date('"+daye+"','yyyy-mm-dd'),sf_department,'"+forecast+"',sf_remark,sf_monthfrom,sf_monthto,sf_fromdate,sf_todate from PreSaleForecast where sf_id="
					+ sf_id;
			int number = (int)time1;
			for(int i=0;i<number;i++){
				String detailSql = "insert into PreSaleForecastDetail(sd_id,sd_sfid,sd_statuscode,sd_status,sd_code,sd_detno,sd_prodcode,sd_custcode,sd_custname,sd_person,sd_enddate,"
						+ "sd_startdate,sd_prodid,sd_qty) select PreSaleForecastDetail_seq.nextval,"
						+ id
						+ ",'ENTERING','在录入','"
						+ code
						+ "',sd_detno,"
						+ "sd_prodcode,sd_custcode,sd_custname,sd_person,to_date('"+days+"','yyyy-mm-dd')+"+i+",to_date('"+days+"','yyyy-mm-dd')+"+i+",sd_prodid,0 from (select sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,"
						+ "sd_prodid,sd_detno from PreSaleForecastDetail where sd_sfid="
						+ sf_id+" group by sd_code,sd_prodcode,sd_custcode,sd_custname,sd_person,"
						+ "sd_prodid,sd_detno)";			
				baseDao.execute(new String[] {detailSql });	
			}	
			baseDao.execute(new String[] { sql});	
		}
		String log = "jsps/scm/sale/PreForecast.jsp?formCondition=sf_idIS" + id
				+ "&gridCondition=sd_sfidIS" + id + "";
		return log;
	}
}
