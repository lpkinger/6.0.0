package com.uas.erp.service.pm.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.b2c.service.common.B2CComponentService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.ScheduleTask;
import com.uas.erp.service.b2c.GoodsChangeService;
import com.uas.erp.service.b2c.GoodsUpApplicationService;
import com.uas.erp.service.pm.MPSMainService;
import com.uas.erp.service.pm.MakeBaseService;
import com.uas.erp.service.scm.ApplicationService;
import com.uas.erp.service.scm.ProductBatchUUIdService;

@Service(value = "mPSMainService")
public class MPSMainServiceImpl implements MPSMainService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private MakeBaseService makeBaseService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private B2CComponentService b2cComponentService;
	@Autowired
	private GoodsChangeService goodsChangeService;
	@Autowired
	private GoodsUpApplicationService goodsUpApplicationService;
	@Autowired
    private ProductBatchUUIdService productBatchUUIdService;
	private int throwCount;
	@Override
	public void saveMPS(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MPSMain",
				"mm_code='" + store.get("mm_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MPSMain",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "mm_id", store.get("mm_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//是否自动装载需求
		if(store.containsKey("mm_ifautoload") && !"0".equals(store.get("mm_ifautoload"))){
			Object mm_coplist = store.get("mm_coplist");
			Object mm_id = store.get("mm_id");
			Object mm_code = store.get("mm_code");
			String user =  baseDao.getJdbcTemplate().queryForObject("select user from dual",String.class);
			int v_m_count = 0;
			if(StringUtil.hasText(mm_coplist)){
				  //解析账套循环载入各账套销售订单，预测单据
				   String[] masterArray = mm_coplist.toString().split("#");
				   for(String master:masterArray){
					v_m_count = baseDao.getCount("select nvl(max(md_detno),0) from MpsDetail where md_mainid="+mm_id);
				    baseDao.execute(" Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty, "
						   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded,md_master) "
						   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_pmcdate,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0), "
						   +" sa_code,sd_detno,'SALE',sa_cop,'"+mm_code+"',sd_id,NVL(sd_bonded,0),'"+master+"' from "
						   + master+".Saledetail left join "+master+".sale on sa_id=sd_said left join "+master+".product on pr_id=sd_prodid left join "+master+".salekind on sa_kind=sk_name "
						   +" where SK_mrp<>0 and  sa_statuscode= 'AUDITED' and sd_qty>0 and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and nvl(sd_mrpclosed,0)=0 and NVL(sa_type,' ')=' '");
				    v_m_count = baseDao.getCount("select max(md_detno) from MpsDetail where md_mainid="+mm_id);
				    baseDao.execute("Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty,"
							   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded,md_master)"
							   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_needdate,sd_qty,"
							   +" sf_code,sd_detno,'FORECAST',sf_cop, '"+mm_code+"',sd_id,NVL(sd_bonded,0),'"+master+"' from "
							   + master+".SaleForecast left join "+master+".SaleForecastdetail on sd_sfid=sf_id left join "+master+".product on pr_code=sd_prodcode"
							   +" where  sf_statuscode='AUDITED'  and sd_qty>0 and nvl(sd_mrpclosed,0)=0 and NVL(sd_statuscode,' ')<>'FINISH' and trunc(sd_enddate)>trunc(sysdate)");			  
					}
			}else{
				     baseDao.execute(" Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty, "
						   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded) "
						   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_pmcdate,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0), "
						   +" sa_code,sd_detno,'SALE',sa_cop,'"+mm_code+"',sd_id,NVL(sd_bonded,0) from "
						   +" Saledetail left join sale on sa_id=sd_said left join product on pr_id=sd_prodid left join salekind on sa_kind=sk_name "
						   +" where SK_mrp<>0 and  sa_statuscode= 'AUDITED' and sd_qty>0 and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and nvl(sd_mrpclosed,0)=0 and NVL(sa_type,' ')=' '");
				    v_m_count = baseDao.getCount("select max(md_detno) from MpsDetail where md_mainid="+mm_id);
				    baseDao.execute("Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty,"
							   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded)"
							   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_needdate,sd_qty,"
							   +" sf_code,sd_detno,'FORECAST',sf_cop, '"+mm_code+"',sd_id,NVL(sd_bonded,0) from "
							   +" SaleForecast left join SaleForecastdetail on sd_sfid=sf_id left join product on pr_code=sd_prodcode"
							   +" where  sf_statuscode='AUDITED'  and sd_qty>0 and nvl(sd_mrpclosed,0)=0 and NVL(sd_statuscode,' ')<>'FINISH' and trunc(sd_enddate)>trunc(sysdate)");			  
			}
		}
		// baseDao.execute("update mpsdetail set md_sdid=(select NVL(max(sd_id),0) from saledetail where sd_code=md_ordercode and sd_detno=md_orderdetno) where md_mainid="+store.get("mm_id")+" and NVL(md_sdid,0)=0");
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMPS(int mm_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MPSMain",
				"mm_statuscode", "mm_id=" + mm_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mm_id });
		// 删除MPS
		baseDao.deleteById("MPSMain", "mm_id", mm_id);
		baseDao.deleteByCondition("MpsDetail", "md_mainid=" + mm_id);
		baseDao.deleteByCondition("mrpdata", "md_mpsid=" + mm_id);
		baseDao.deleteByCondition("mrpresultdetail", "md_mpsid=" + mm_id);
		// 记录操作
		baseDao.logger.delete(caller, "mm_id", mm_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mm_id });
	}

	@Override
	public void updateMPSById(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil
				.parseGridStoreToMaps(param);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MPSMain",
				"mm_statuscode", "mm_id=" + store.get("mm_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gridstore });
		//获取原明细是否有数据
		int count = baseDao.getCount("select count(1) from  MPSDetail where md_mainid="+ store.get("mm_id"));
		List<String> sqls = new ArrayList<String>();
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MPSMain",
				"mm_id");
		sqls.add(formSql);
		for (Map<Object, Object> map : gridstore) {
			if (map.get("md_id") != null && !map.get("md_id").equals("0")) {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "MPSDetail",
						"md_id"));
			} else
				sqls.add(SqlUtil.getInsertSqlByMap(map, "MPSDetail",
						new String[] { "md_id" },
						new Object[] { baseDao.getSeqId("MPSDETAIL_SEQ") }));
		}
		/*baseDao.execute("update mpsdetail set md_sdid=(select NVL(max(md_id),0) from saledetail where sd_code=md_ordercode and sd_detno=md_orderdetno) where md_mainid="
				+ store.get("mm_id") + " and NVL(md_sdid,0)=0");*/
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update(caller, "mm_id", store.get("mm_id"));
		//是否自动装载需求
		if(count==0 && gridstore.size()==0 && store.containsKey("mm_ifautoload") && !"0".equals(store.get("mm_ifautoload"))){
			Object mm_coplist = store.get("mm_coplist");
			Object mm_id = store.get("mm_id");
			Object mm_code = store.get("mm_code");
			String user =  baseDao.getJdbcTemplate().queryForObject("select user from dual",String.class);
			int v_m_count = 0;
			if(StringUtil.hasText(mm_coplist)){
				  //解析账套循环载入各账套销售订单，预测单据
				  String[] masterArray = mm_coplist.toString().split("#");
				  for(String master:masterArray){
					v_m_count = baseDao.getCount("select nvl(max(md_detno),0) from MpsDetail where md_mainid="+mm_id);	
				    baseDao.execute(" Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty, "
						   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded,md_master) "
						   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_pmcdate,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0), "
						   +" sa_code,sd_detno,'SALE',sa_cop,'"+mm_code+"',sd_id,NVL(sd_bonded,0),'"+master+"' from "
						   + master+".Saledetail left join "+master+".sale on sa_id=sd_said left join "+master+".product on pr_id=sd_prodid left join "+master+".salekind on sa_kind=sk_name "
						   +" where SK_mrp<>0 and  sa_statuscode= 'AUDITED' and sd_qty>0 and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and nvl(sd_mrpclosed,0)=0 and NVL(sa_type,' ')=' '");
				    v_m_count = baseDao.getCount("select max(md_detno) from MpsDetail where md_mainid="+mm_id);
				    baseDao.execute("Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty,"
							   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded,md_master)"
							   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_needdate,sd_qty,"
							   +" sf_code,sd_detno,'FORECAST',sf_cop, '"+mm_code+"',sd_id,NVL(sd_bonded,0),'"+master+"' from "
							   + master+".SaleForecast left join "+master+".SaleForecastdetail on sd_sfid=sf_id left join "+master+".product on pr_code=sd_prodcode"
							   +" where  sf_statuscode='AUDITED'  and sd_qty>0 and nvl(sd_mrpclosed,0)=0 and NVL(sd_statuscode,' ')<>'FINISH' and trunc(sd_enddate)>trunc(sysdate)");			  
					}
			}else{
				 baseDao.execute(" Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty, "
						   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded) "
						   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_pmcdate,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0), "
						   +" sa_code,sd_detno,'SALE',sa_cop,'"+mm_code+"',sd_id,NVL(sd_bonded,0) from "
						   +" Saledetail left join sale on sa_id=sd_said left join product on pr_id=sd_prodid left join salekind on sa_kind=sk_name "
						   +" where SK_mrp<>0 and  sa_statuscode= 'AUDITED' and sd_qty>0 and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and nvl(sd_mrpclosed,0)=0 and NVL(sa_type,' ')=' '");
				    v_m_count = baseDao.getCount("select max(md_detno) from MpsDetail where md_mainid="+mm_id);
				    baseDao.execute("Insert into MpsDetail(md_id,md_mainid,md_detno,md_prodcode,md_date,md_qty,"
							   +" md_ordercode,md_orderdetno,md_orderkind,md_cop,md_maincode,md_sdid,Md_Bonded)"
							   +" select "+user+".MpsDetail_seq.nextval,"+mm_id+","+v_m_count+"+rownum,sd_prodcode,sd_needdate,sd_qty,"
							   +" sf_code,sd_detno,'FORECAST',sf_cop, '"+mm_code+"',sd_id,NVL(sd_bonded,0) from "
							   +" SaleForecast left join SaleForecastdetail on sd_sfid=sf_id left join product on pr_code=sd_prodcode"
							   +" where  sf_statuscode='AUDITED'  and sd_qty>0 and nvl(sd_mrpclosed,0)=0 and NVL(sd_statuscode,' ')<>'FINISH' and trunc(sd_enddate)>trunc(sysdate)");			  
			}
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gridstore });
	}

	@Override
	public void auditMPS(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MPSMain",
				"mm_statuscode", "mm_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 审核之前判断订单编号+序号+物料编号是否一致
		check(id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		baseDao.audit("MPSMain", "mm_id=" + id, "mm_status", "mm_statuscode",
				"mm_auditdate", "mm_auditman");
		// 调用存储
		// 记录操作
		baseDao.logger.audit(caller, "mm_id", id);
		// 执行审核后的其它逻辑
		// 冲减已发货或多于的需求
		MRPNeedClash("", id);
		handlerService.afterAudit(caller, new Object[] { id });
	}

	@Override
	public void resAuditMPS(int mm_id, String caller) {
		// 执行反审核操作
		baseDao.resOperate("MPSMain", "mm_id=" + mm_id, "mm_status",
				"mm_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "mm_id", mm_id);
	}

	@Override
	public void submitMPS(int mm_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MPSMain",
				"mm_statuscode", "mm_id=" + mm_id);
		StateAssert.submitOnlyEntering(status);		
		SqlRowList rs1 = baseDao.queryForRowSet(" select column_value from table(PARSESTRING((select mm_coplist from mpsmain where mm_id=?), '#')) where column_value is not null",mm_id);
		if(rs1.hasNext()){
			String master;
			//提交之前判断是否有禁用物料
			boolean isAllowDisabled = baseDao.isDBSetting(caller, "allowDisabled");
			while(rs1.next()){
				master = rs1.getString("column_value");
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(md_detno) no from MpsDetail left join MpsMain on md_mainid=mm_id left join  "+master+".product on md_prodcode=pr_code where mm_id=? and md_master=? and nvl(pr_statuscode,' ')<>'AUDITED' and nvl(pr_statuscode,' ')<>'DISABLE' and rownum<25",
								String.class, mm_id,master);
				if (dets != null) {
					BaseUtil.showError("物料不存在或者物料状态为在录入或已提交,行号:" + dets);
				}
				if(!isAllowDisabled){
					SqlRowList rs2 = baseDao.queryForRowSet("select wm_concat(md_detno) no from MpsDetail left join MpsMain on md_mainid=mm_id left join "+master+".product on md_prodcode=pr_code where mm_id=? and md_master=? and pr_statuscode='DISABLE' and rownum<25",mm_id,master);
					if(rs2.next()&&rs2.getObject("no")!=null){
						BaseUtil.showError("物料已禁用,行号:"+rs2.getObject("no"));
					}
				}
			}
		}else{
			//提交之前判断物料是否已审核
			SqlRowList rs = baseDao.queryForRowSet("select wm_concat(md_detno) no from MpsDetail left join MpsMain on md_mainid=mm_id left join  product on md_prodcode=pr_code where mm_id=? and nvl(pr_statuscode,' ')<>'AUDITED' and nvl(pr_statuscode,' ')<>'DISABLE' and rownum<25",mm_id);
			if(rs.next()&&rs.getObject("no")!=null){
				BaseUtil.showError("物料不存在或者物料状态为在录入或已提交,行号:"+rs.getObject("no"));
			}
			//提交之前判断是否有禁用物料
			boolean isAllowDisabled = baseDao.isDBSetting(caller, "allowDisabled");
			if(!isAllowDisabled){
				SqlRowList rs2 = baseDao.queryForRowSet("select wm_concat(md_detno) no from MpsDetail left join MpsMain on md_mainid=mm_id left join  product on md_prodcode=pr_code where mm_id=? and pr_statuscode='DISABLE' and rownum<25",mm_id);
				if(rs2.next()&&rs2.getObject("no")!=null){
					BaseUtil.showError("物料已禁用,行号:"+rs2.getObject("no"));
				}
			}
		}
		
		// 审核之前判断订单编号+序号+物料编号是否一致
		check(mm_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { mm_id });
		// 执行提交操作
		baseDao.submit("MPSMain", "mm_id=" + mm_id, "mm_status",
				"mm_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mm_id", mm_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { mm_id });
	}

	@Override
	public void resSubmitMPS(int mm_id, String caller) {
		handlerService.beforeResSubmit(caller, new Object[] { mm_id });
		baseDao.resOperate("MPSMain", "mm_id=" + mm_id, "mm_status",
				"mm_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mm_id", mm_id);
		handlerService.afterResSubmit(caller, new Object[] { mm_id });
	}

	@Override
	public void LoadData(int keyValue, String mainCode, String caller,
			String detailcaller, String Store, String gridStore, String kind) {
		DataList datalist = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String keyField = datalist.getDl_keyfield();
		List<Map<Object, Object>> sourceStore = BaseUtil
				.parseGridStoreToMaps(Store);
		String condition = " ";
		String tablename = datalist.getDl_tablename();
		String DLCondition = datalist.getDl_condition();
		String OrderKind = "";
		String COP = "";
		if (DLCondition != null && !DLCondition.trim().equals("")) {
			condition = " and sd_qty>0 ";
		} else {
			condition = " sd_qty>0 ";
		}
		if (kind.equals("MPS")) {
			Object ob = baseDao.getFieldDataByCondition("MPSMain",
					"NVL(mm_sourcecode,'ALL')", "mm_id=" + keyValue);
			if (ob != null) {
				COP = ob.toString();
			}
			if (ob == null || COP.equals("全部") || COP.equals(" ")) {
				COP = "ALL";
			}
			if (!COP.equals("ALL")) {
				if (caller.toUpperCase().indexOf("FORECAST") > 0) {
					DLCondition = DLCondition + " and sf_cop='" + COP + "' ";
				} else {
					DLCondition = DLCondition + " and sa_cop='" + COP + "' ";
				}
			}
		}
		String sql = "";
		if (caller.equals("MRPSSaleM")) {
			OrderKind = "SALE";
			sql = "Select sd_id,sd_detno,sa_code,sd_code,sd_pmcdate as sd_delivery,sd_prodcode,sd_bomid,sd_bomid,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0) as sd_qty,sa_cop as cop,sd_factory from "
					+ tablename + "  where " + DLCondition;
			condition = condition
					+ " AND sd_qty>NVL(sd_sendqty,0) AND nvl(sd_statuscode,' ')<>'FINISH' ";
		} else if (caller.equals("MRPSSaleD")) {
			OrderKind = "SALE";
			sql = "Select sd_id,sd_detno,sa_code,sd_code,sd_pmcdate as sd_delivery,sd_prodcode,sd_bomid,sd_bomid,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0) as sd_qty,sa_cop as cop,sd_factory from "
					+ tablename + "  where " + DLCondition;
			condition = condition
					+ " AND sd_qty>NVL(sd_sendqty,0) AND nvl(sd_statuscode,' ')<>'FINISH' ";
		} else if (caller.equals("MRPSForeCastM")) {
			OrderKind = "FORECAST";
			sql = "Select sd_id,sd_detno,sf_code,sd_code,sd_needdate as sd_delivery,sd_prodcode,sd_bomid,sd_qty,sf_cop as cop,sd_factory from "
					+ tablename + "  where " + DLCondition;
			condition = condition + " AND nvl(sd_mrpclosed,0)=0";
		} else if (caller.equals("MRPSForeCastD")) {
			OrderKind = "FORECAST";
			sql = "Select sd_id,sd_detno,sf_code,sd_code,sd_needdate as sd_delivery,sd_prodcode,sd_bomid,sd_qty,sf_cop as cop,sd_factory from "
					+ tablename + "  where " + DLCondition;
			condition = condition + " AND nvl(sd_mrpclosed,0)=0";
		}
		if (sql.equals("")) {
			BaseUtil.showError("装载出错！");
			return;
		}
		if (kind.equals("MDS")) {
			condition = condition
					+ " and sd_id not in (select mdd_sdid from mdsdetail where mdd_mainid="
					+ keyValue + " and mdd_orderkind='" + OrderKind + "' ) ";
		} else {
			condition = condition
					+ " and sd_id not in (select md_sdid from mpsdetail where md_mainid="
					+ keyValue + " and md_orderkind='" + OrderKind + "' ) ";
		}
		sql = sql + condition;
		List<Map<Object, Object>> maps = new ArrayList<Map<Object, Object>>();
		String table = "";
		int count = 1;
		String findMAXDetnoSQl = "";
		if (kind.equals("MDS")) {
			findMAXDetnoSQl = "select nvl(max(mdd_detno),0) from mdsdetail where mdd_mainid="
					+ keyValue;
		} else
			findMAXDetnoSQl = "select nvl(max(md_detno),0) from mpsdetail where md_mainid="
					+ keyValue;
		SqlRowList rs1 = baseDao.queryForRowSet(findMAXDetnoSQl);
		if (rs1.next()) {
			count += rs1.getInt(1);
		}

		StringBuffer sb = new StringBuffer();
		int index = 0;
		String querycondition = "";
		int size = sourceStore.size();
		for (int i = 0; i < sourceStore.size(); i++) {
			sb.append(sourceStore.get(i).get(keyField) + ",");
			index++;
			if (index > 800 || i == size - 1) {
				String range = "("
						+ sb.toString()
								.substring(0, sb.toString().length() - 1) + ")";
				sb.setLength(0);
				index = 0;
				querycondition = keyField + " in " + range;
				SqlRowList rs = baseDao.queryForRowSet(sql + " AND "
						+ querycondition);
				while (rs.next()) {
					Map<Object, Object> map = new HashMap<Object, Object>();
					if (kind.equals("MDS")) {
						map.put("mdd_id", baseDao.getSeqId("MDSDETAIL_SEQ"));
						map.put("mdd_mainid", keyValue);
						map.put("mdd_detno", count);
						map.put("mdd_maincode", mainCode);
						map.put("mdd_qty", rs.getObject("sd_qty"));
						map.put("mdd_date", rs.getObject("sd_delivery"));
						map.put("mdd_prodcode", rs.getObject("sd_prodcode"));
						map.put("mdd_source", OrderKind);
						map.put("mdd_sourcedate", rs.getObject("sd_delivery"));
						map.put("mdd_sourceqty", rs.getObject("sd_qty"));
						map.put("mdd_sourcecode", rs.getObject("sd_code"));
						map.put("mdd_sourcedetno", rs.getObject("sd_detno"));
						map.put("mdd_orderkind", OrderKind);
						map.put("mdd_ordercode", rs.getObject(3));
						map.put("mdd_orderdetno", rs.getObject("sd_detno"));
						map.put("mdd_cop", rs.getObject("cop"));
						map.put("mdd_bomid", rs.getObject("sd_bomid"));
						map.put("mdd_sdid", rs.getObject("sd_id"));
						count++;
						table = "MDSDetail";
					} else {
						map.put("md_id", baseDao.getSeqId("MPSDETAIL_SEQ"));
						map.put("md_mainid", keyValue);
						map.put("md_detno", count);
						map.put("md_maincode", mainCode);
						map.put("md_qty", rs.getObject("sd_qty"));
						map.put("md_date", rs.getObject("sd_delivery"));
						map.put("md_prodcode", rs.getObject("sd_prodcode"));
						map.put("md_source", OrderKind);
						map.put("md_sourcedate", rs.getObject("sd_delivery"));
						map.put("md_sourceqty", rs.getObject("sd_qty"));
						map.put("md_sourcecode", rs.getObject("sd_code"));
						map.put("md_sourcedetno", rs.getObject("sd_detno"));
						map.put("md_orderkind", OrderKind);
						map.put("md_ordercode", rs.getObject(3));
						map.put("md_orderdetno", rs.getObject("sd_detno"));
						map.put("md_cop", rs.getObject("cop"));
						map.put("md_bomid", rs.getObject("sd_bomid"));
						map.put("md_sdid", rs.getObject("sd_id"));
						map.put("md_factory", rs.getObject("sd_factory"));
						count++;
						table = "MPSDetail";
					}
					maps.add(map);
				}
			}

		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(maps, table));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void autoLoadData(int id, String caller) {
		// 装载订单和预测
		String SQL = "";
		String InSQL = "";
		String COP = "";
		String OrderKind = "";
		String maincode = "";
		int nowdetno = 0;
		int mainid = 0;
		nowdetno = Integer.parseInt(baseDao.getFieldDataByCondition(
				"mpsdetail", "NVL(max(md_detno),0)", "md_mainid=" + id)
				.toString());
		if (nowdetno > 0) {
			BaseUtil.showError("请先清除明细!");
			return;
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select mm_id,mm_code,mm_kind,NVL(mm_sourcecode,'ALL')mm_sourcecode,mm_statuscode from mpsmain where  mm_id = "
						+ id + "");
		if (rs.next()) {
			mainid = rs.getInt("mm_id");
			maincode = rs.getString("mm_code");
			if (!rs.getString("mm_statuscode").equals("ENTERING")) {
				BaseUtil.showError("只能装载在录入的单据!");
				return;
			}
			COP = rs.getString("mm_sourcecode");
			if (COP == null || COP.equals("全部") || COP.equals(" ")) {
				COP = "ALL";
			}
		} else {
			BaseUtil.showError("此MRP计划不存在!");
			return;
		}
		List<DataList> datalists = baseDao
				.getJdbcTemplate()
				.query("select * from datalist where dl_caller in ('MRPSSaleM','MRPSForeCastM') order by dl_caller asc",
						new BeanPropertyRowMapper<DataList>(DataList.class));
		for (DataList datalist : datalists) {
			SQL = "";
			nowdetno = Integer.parseInt(baseDao.getFieldDataByCondition(
					"mpsdetail", "NVL(max(md_detno),0)", "md_mainid=" + mainid)
					.toString());
			caller = datalist.getDl_caller();
			if (caller.equals("MRPSSaleM")) {
				SQL = "Select sd_id,sd_detno,sa_code,sd_code,sd_pmcdate as sd_delivery,sd_prodcode,sd_bomid,sd_qty+nvl(sd_beipin,0)-nvl(sd_sendqty,0)-nvl(sd_beipinsendqty,0) as sd_qty,sa_cop as cop,sd_factory from "
						+ datalist.getDl_tablename()
						+ "  where "
						+ datalist.getDl_condition();
			} else {
				SQL = "Select sd_id,sd_detno,sf_code,sf_code sd_code,sd_needdate as sd_delivery,sd_prodcode,sd_bomid,sd_qty,sf_cop as cop,sd_factory from "
						+ datalist.getDl_tablename()
						+ "  where "
						+ datalist.getDl_condition();
			}
			if (!COP.equals("ALL") && caller.equals("MRPSSaleM")) {
				SQL += " and sa_cop='" + COP + "' ";
			} else if (!COP.equals("ALL") && caller.equals("MRPSForeCastM")) {
				SQL += " and sf_cop='" + COP + "' ";
			}
			if (caller.equals("MRPSSaleM")) {
				OrderKind = "SALE";
				SQL = SQL
						+ " AND sd_qty>NVL(sd_sendqty,0) AND nvl(sd_statuscode,' ')<>'FINISH' ";
			} else if (caller.equals("MRPSForeCastM")) {
				OrderKind = "FORECAST";
				SQL = SQL + " AND nvl(sd_mrpclosed,0)=0";
			}
			if (SQL.equals("")) {
				BaseUtil.showError("装载出错！");
				return;
			}
			InSQL = "insert into mpsdetail(md_id,md_mainid,md_detno,md_maincode,md_qty,md_date,md_prodcode,md_source,md_sourcedate,md_sourceqty,md_sourcecode,md_sourcedetno,md_orderkind,md_ordercode,md_orderdetno,md_cop,md_bomid,md_sdid,md_factory)"
					+ "select MPSDETAIL_SEQ.nextval,"
					+ mainid
					+ ","
					+ nowdetno
					+ "+rownum,'"
					+ maincode
					+ "',sd_qty,sd_delivery,sd_prodcode,'"
					+ OrderKind
					+ "',sd_delivery as md_sourcedate,sd_qty,sd_code,sd_detno as md_sourcedetno,'"
					+ OrderKind
					+ "',sd_code,sd_detno as md_orderdetno,cop,sd_bomid,sd_id,sd_factory from ("
					+ SQL + ")A";
			baseDao.execute(InSQL);
		}
	}

	/*
	 * 冲减MRP需求里面超订单数量的需求，参数需其中一个
	 */
	public void MRPNeedClash(String mm_code, int mm_id) {
		float clashqty = 0;
		int mainid = 0;
		String SQLStr = "";
		mainid = mm_id;
		if (mainid == 0) {
			mainid = Integer
					.parseInt(baseDao.getFieldDataByCondition("MpsMain",
							"mm_id", "mm_code='" + mm_code + "'").toString());
		}
		if (mainid == 0) {
			return;
		}
		SQLStr = "UPDATE mpsdetail set md_orderkind='FORECAST' where  md_mainid='"
				+ mainid
				+ "' and md_ordercode in (select sf_code from saleforecast) and md_ordercode<>' ' and NVL(md_orderkind,' ')=' '  ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_orderkind='SALE' where   md_mainid='"
				+ mainid
				+ "' and md_ordercode in (select sa_code from sale) and md_ordercode<>' ' and NVL(md_orderkind,' ')=' '  ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_orderdetno=nvl((select max(sd_detno) from saledetail where sd_code=md_ordercode and md_prodcode=sd_prodcode),0) where md_mainid='"
				+ mainid
				+ "' and md_orderkind='SALE' and md_ordercode<>' ' and NVL(md_orderdetno,0)=0 ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_orderdetno=nvl((select max(sd_detno) from saleforecastdetail where sd_code=md_ordercode and md_prodcode=sd_prodcode),0) where md_mainid='"
				+ mainid
				+ "' and md_orderkind='FORECAST' and md_ordercode<>' ' and NVL(md_orderdetno,0)=0 ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_bonded=nvl(( select max(sd_bonded) from saledetail where sd_code=md_ordercode and sd_detno=md_orderdetno and md_prodcode=sd_prodcode ),0) where md_mainid='"
				+ mainid + "' and md_orderkind='SALE'  and md_ordercode<>' ' ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_bonded=nvl(( select max(sd_bonded) from saleforecastdetail where sd_code=md_ordercode and sd_detno=md_orderdetno and md_prodcode=sd_prodcode),0) where md_mainid='"
				+ mainid
				+ "' and md_orderkind='FORECAST'  and md_ordercode<>' ' ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_factory=(select max(sd_factory)  from saledetail where sd_code=md_ordercode and sd_detno=md_orderdetno and md_prodcode=sd_prodcode )  where md_mainid='"
				+ mainid + "' and md_orderkind='SALE'  and md_ordercode<>' '  ";
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE mpsdetail set md_factory=(select max(sd_factory)  from saleforecastdetail where sd_code=md_ordercode and sd_detno=md_orderdetno and md_prodcode=sd_prodcode) where md_mainid='"
				+ mainid
				+ "' and md_orderkind='FORECAST'  and md_ordercode<>' ' ";
		baseDao.execute(SQLStr);

		// 第一次装载的记录标示来源数量
		SQLStr = "select * from (select  md_orderkind,md_ordercode,md_orderdetno,sum(md_qty) as mrpqty,NVL(max(qty),0) as remain from mpsdetail left join "
				+ "(select sa_code as code ,sd_detno as detno,sd_qty-NVL(sd_sendqty,0) as qty,'SALE' as kind"
				+ " from sale left join saledetail on sa_id=sd_said left join salekind on sa_kind=sk_name where  sa_statuscode in('AUDITED','FREEZE') and sd_qty-NVL(sd_sendqty,0)>0 and sk_mrp<>0 "
				+ " union select sf_code as code,sd_detno as detno ,sd_qty as qty ,'FORECAST' as kind from "
				+ "saleforecast,saleforecastdetail where sd_sfid=sf_id and  sf_statuscode='AUDITED' and sd_qty>0 )A "
				+ " on A.code=md_ordercode and A.detno=md_orderdetno and A.kind=md_orderkind where md_mainid='"
				+ mainid
				+ "' and md_ordercode<>' ' group by md_orderkind,md_ordercode,md_orderdetno) where NVL(mrpqty,0)-NVL(remain,0)>0 ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			clashqty = rs.getFloat("mrpqty") - rs.getFloat("remain");
			if (clashqty > 0) {
				SQLStr = "select md_id,md_qty from mpsdetail where md_mainid='"
						+ mainid + "' and md_ordercode='"
						+ rs.getString("md_ordercode")
						+ "' and NVL(md_orderdetno,0)='"
						+ rs.getInt("md_orderdetno") + "' "
						+ "and md_orderkind='" + rs.getString("md_orderkind")
						+ "' and md_qty>0 order by md_date asc";
				SqlRowList rs2 = baseDao.queryForRowSet(SQLStr);
				while (rs2.next() && clashqty > 0) {
					if (rs2.getFloat("md_qty") > clashqty) {
						SQLStr = "UPDATE mpsdetail set md_qty=md_qty-"
								+ clashqty + ",md_clashqty=NVL(md_clashqty,0)+"
								+ clashqty + " where md_id="
								+ rs2.getInt("md_id");
						clashqty = 0;
					} else {
						SQLStr = "UPDATE mpsdetail set md_qty=0,md_clashqty=NVL(md_clashqty,0)+"
								+ clashqty
								+ " where md_id="
								+ rs2.getInt("md_id");
						clashqty = clashqty - rs2.getFloat("md_qty");
					}
					baseDao.execute(SQLStr);
				}
			}

		}

	}

	@Override
	public String RunMrp(String code, String caller) {
		// 执行mrp运算前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { code });
		Object status = baseDao.getFieldDataByCondition("MPSMain",
				"mm_statuscode", "mm_code='" + code + "' ");
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("只能运算状态[已审核]的计划");
		}
		// 获取参数"AllowRunMrpAgain" 是否"允许对已投放过的MRP计划再次运算",默认不允许
		boolean ifAgain = baseDao.isDBSetting(caller, "AllowRunMrpAgain");
		if (!ifAgain) {// 不允许
			// 判断此mrpdata是否存在已投放的记录，如果有则限制进行mrp运算，并提示需求已投放，不允许重新计算。
			int cn = baseDao
					.getCount("select count(1) cn from mrpdata where md_mpscode='"
							+ code
							+ "'and NVL(md_toufang,' ')='Y' and NVL(md_statuscode,' ')='THROWED'");
			if (cn > 0) {// 已有投放记录
				BaseUtil.showError("存在已投放的需求，不允许重新计算");
			}
		}
		// 冲减已发货或多于的需求
		MRPNeedClash(code, 0);
		// 执行运算存储过程
		String str = baseDao.callProcedure("MM_RUNMRP", new Object[] { code,
				SystemSession.getUser().getEm_name() });
		return str;
	}

	 //maKind 投放工单类型
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> NeedThrow(String caller, String mainCode,
			String gridStore, String toWhere, String toCode, String condition,String maKind,String purcaseCop,String apKind) {
		int count = 0;
		int Makecount = 0;
		int OSMakecount = 0;
		int Purcount = 0;
		int Purdetno = 1;
		int Purdetno2 = 1;
		int PFcount = 0;
		int PFdetno = 1;
		int allcount =0;
		int mainid = 0;
		int apid = 0, apid2 = 0;
		int pfid = 0;
		String NewCode = "", apCode = "", apCode2 = "";
		String statuscondition = "";
		String sql = "";
		String PFCode = "";
		String IfThrowCustOffer = ""; // 是否投放允许投放客供的物料到请购单
		String IfmrpSeparateFactory = ""; // 是否启用多工厂模式MRP
		String IfmrpSeparateSeller = "";//是否启用按业务员分配模式MRP
		boolean IfMrpSeparateSaleNO ;//是启用按订单锁库模式MRP
		boolean IfPRBonedFromProduct;//是否启用投放的请购单明细行保税属性取物料资料的保税属性
		String addapkind = "";
		IfThrowCustOffer = baseDao.isDBSetting("MpsDesk", "allowCustomThrow") ? "Y"
				: "N";
		IfmrpSeparateFactory = baseDao.isDBSetting("MpsDesk",
				"mrpSeparateFactory") ? "Y" : "N";
		IfmrpSeparateSeller = baseDao.isDBSetting("MpsDesk",
				"mrpSeparateSeller") ? "Y" : "N";
		IfMrpSeparateSaleNO = baseDao.isDBSetting("MpsDesk","mrpSeparateSaleNO") ;
		IfPRBonedFromProduct = baseDao.isDBSetting("MpsDesk","PRBonedFromProduct") ;
		
		if (toCode != null && !toCode.equals("")) {
			SqlRowList cl = baseDao
					.queryForRowSet("select * from application where ap_code='"
							+ toCode + "'");
			if (!cl.hasNext()) {
				BaseUtil.showError("指定的请购单号不存在");
			}
			while (cl.next()) {
				// 在录入状态才允许追加
				if (!"ENTERING".equals(cl.getString("ap_statuscode"))) {
					BaseUtil.showError("只能追加到在录入的请购单");
				}
				addapkind = cl.getString("ap_kind");
			}

		}
		Object Runstatus = baseDao.getFieldDataByCondition("mpsmain",
				"mm_runstatus", "mm_code='" + mainCode + "'");
		if (Runstatus != null && Runstatus.equals("运算中")) {
			BaseUtil.showError("运算中的MRP计划，不允许投放！");
		}
		
		Map<String, Object> ReturnMap = new HashMap<String, Object>();
		Map<String, Object> errMap = new HashMap<String, Object>();
		List<Map<Object, Object>> NeedStore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		int needStoreSize = NeedStore.size();
		statuscondition = " and pr_statuscode='AUDITED' and md_changeqty>0 and NVL(md_toufang,' ')<>'N' and (md_statuscode='UNTHROW' or NVL(md_statuscode,' ')=' ') ";
		if (IfmrpSeparateFactory.equals("Y")) {// 如果启用多工厂模式MRP，需求未指定工厂的不能投放
			statuscondition += " and (md_factory<>'UNSET' OR (md_worktype='MRP' and pr_manutype like '%MAKE%') )";
		} 
		if (condition.length() < 5) { 
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			for (int i = 0; i < needStoreSize; i++) {
				sb.append(NeedStore.get(i).get("md_id") + ",");
				if(i==999){
					break;
				}
			}
			String range = sb.toString().substring(0,
					sb.toString().length() - 1)
					+ ")";
			String idcondition = " and md_id in " + range;
			sql = "Select MRPData.*,pr_code,pr_id,pr_detail,pr_spec,PR_DHZC,pr_unit,pr_whcode,pr_manutype,pr_specvalue,pr_refno,nvl(pr_leadtime,0)pr_leadtime,nvl(pr_zxbzs,0)pr_zxbzs,nvl(pr_zxdhl,0)pr_zxdhl,pr_plzl,md_needdate+NVl(pr_gdtqq,0) requiredate,md_needdate-NVL(pr_leadtime,0)-(case when pr_plzl>1 then ceil(md_changeqty/pr_plzl) else 0 end) begindate,pr_bonded  from  MRPData left join product on pr_id=md_prodid  where 1=1 "
					+ idcondition + statuscondition;
			//打钩投放的总数
			allcount = baseDao.getCount("select count(1) from MRPData left join product on pr_id=md_prodid  where 1=1 "+ idcondition );
		} else {
			//筛选投放的总数
			allcount = baseDao.getCount("select count(1) from MRPData left join product on pr_id=md_prodid  where 1=1 and "+ condition  );
			sql = "Select MRPData.*,pr_code,pr_id,pr_detail,pr_spec,PR_DHZC,pr_unit,pr_manutype,pr_specvalue,pr_refno,nvl(pr_leadtime,0)pr_leadtime,nvl(pr_zxbzs,0)pr_zxbzs,nvl(pr_zxdhl,0)pr_zxdhl,pr_plzl,md_needdate+NVl(pr_gdtqq,0) requiredate,md_needdate-NVL(pr_leadtime,0)-(case when pr_plzl>1 then ceil(md_changeqty/pr_plzl) else 0 end) begindate,pr_bonded  from  MRPData left join product on pr_id=md_prodid  where 1=1 "
					+ " and (pr_manutype='PURCHASE' or ('"
					+ IfThrowCustOffer
					+ "'='Y' and pr_manutype='CUSTOFFER')) and "
					+ condition
					+ statuscondition;
		}
		SqlRowList rs0;
		Object Postatus = baseDao.getFieldDataByCondition("mpsmain",
				"mm_postatus", "mm_code='" + mainCode + "'");
		if (Postatus.equals("MAKE")) {
			rs0 = baseDao
					.queryForRowSet(sql
							+ " and pr_manutype not in ('MAKE','OSMAKE') and NVL(md_action,' ')<>'PFNEED' ");
			if (rs0.next()) {

				BaseUtil.showError("当前状态只允许投放制造件");
			}

		}

		rs0 = baseDao.queryForRowSet(sql + " and NVL(md_action,' ')='PFNEED' ");
		if (rs0.next()) {
			if (!Postatus.equals("PURCHASEFORECAST")) {
				BaseUtil.showError("先作废当前采购预测，才允许投放新采购预测");
			}
		}
		// 内部交易投放
		if (caller != null && caller.equalsIgnoreCase("MPSSupply")) {
			ThrowSupply(mainCode, sql, caller);
			return ReturnMap;
		}
		
		Object makekind=null;
		if(StringUtil.hasText(maKind)){//判断选择的制造单投放类型是否有效
			int cn = baseDao.getCount("select count(1)cn FROM makekind where mk_name='"+maKind+"' and mk_makind='MAKE' and nvl(mk_isuse,0)=-1");
		    if(cn==0){
		    	BaseUtil.showError("指定的制造单投放类型["+maKind+"]不存在或者无效,无法投放");
		    }else{
		    	makekind=maKind;
		    }
		}else{
			makekind = baseDao.getFieldDataByCondition("makekind",
					"mk_name", "mk_ifmrpkind<>0 and mk_makind='MAKE' ");
		}
	
		Object osmakekind = baseDao.getFieldDataByCondition("makekind",
				"mk_name", "mk_ifmrpkind<>0 and mk_makind='OSMAKE' ");
		Object apkind = baseDao.getFieldDataByCondition("purchasekind",
				"pk_name", "pk_ifmrpkind<>0");
		if (apkind == null || apkind.equals("")) {
			BaseUtil.showError("未定义默认的MRP投放请购单类型，不能投放");
		}
		Object apcustoffer = baseDao.getFieldDataByCondition("purchasekind",
				"pk_name", "PK_IFCUSTOFFER<>0");
		Object makelCode = baseDao.getFieldDataByCondition("MAKEKIND",
				"mk_excode", "mk_name='" + makekind + "'");
		Object osmakelCode = baseDao.getFieldDataByCondition("MAKEKIND",
				"mk_excode", "mk_name='" + osmakekind + "'");
		Object purclCode = baseDao.getFieldDataByCondition("purchasekind",
				"pk_excode", "pk_name='" + apkind + "'");
		// 以下开始需求投放,按照物料升序，需求时间
		SqlRowList rs = baseDao.queryForRowSet(sql+" order by md_prodcode,md_needdate");
		while (rs.next()) {
			if ("MAKE".equals(rs.getString("pr_manutype"))
					|| "OSMAKE".equals(rs.getString("pr_manutype"))) {
				Object bomid = baseDao
						.getFieldDataByCondition(
								"bom left join product on (pr_code=bo_mothercode or pr_refno=bo_mothercode)",
								"nvl(max(bo_id),0)",
								" pr_code='" + rs.getString("md_prodcode")
										+ "' and bo_statuscode='AUDITED'");
				if (Integer.parseInt(bomid.toString()) == 0) {
					baseDao.updateByCondition("mrpdata",
							" md_throwremark='BOM不存在或状态无效'",
							"md_id=" + rs.getString("md_id"));
					continue;
				}
			}
			Map<Object, Object> map = new HashMap<Object, Object>();
			if ("MAKE".equals(rs.getString("pr_manutype"))) {
				if (makekind == null || makekind.equals("")) {
					BaseUtil.showError("未定义默认的MRP投放制造单类型，不能投放");
				}
				mainid = baseDao.getSeqId("MAKE_SEQ");
				NewCode = baseDao.sGetMaxNumber("Make!Base", 2);
				if (makelCode != null) {
					if (!makelCode.toString().equals("")) {
						// 修改前缀
						NewCode = makelCode.toString() + NewCode;
					}
				}
				map.put("ma_id", mainid);
				map.put("ma_code", NewCode);
				map.put("ma_source", "MRP");
				map.put("ma_tasktype", "MAKE");
				map.put("ma_prodid", rs.getInt("pr_id"));
				map.put("ma_prodcode", rs.getString("md_prodcode"));
				map.put("ma_prodname", rs.getString("pr_detail"));
				map.put("ma_prodspec", rs.getString("pr_spec"));
				map.put("ma_produnit", rs.getString("pr_unit"));
				map.put("ma_whcode", rs.getString("pr_whcode"));
				map.put("ma_qty", rs.getDouble("md_changeqty"));
				map.put("ma_madeqty", 0);
				map.put("ma_status", BaseUtil.getLocalMessage("AUDITED"));
				map.put("ma_statuscode", "AUDITED");
				map.put("ma_date", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				map.put("ma_recorddate", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				map.put("ma_recorder", SystemSession.getUser().getEm_name());
				map.put("ma_requiredate", rs.getDate("requiredate"));
				Date enddate = rs.getDate("md_needdate");
				if (enddate.compareTo(new Date()) < 0) {
					enddate = new Date();
				}
				Date begindate = rs.getDate("begindate");
				if (begindate.compareTo(new Date()) < 0) {
					begindate = new Date();
				}
				map.put("ma_planbegindate",
						DateUtil.parseDateToString(begindate, Constant.YMD_HMS));
				map.put("ma_planenddate",
						DateUtil.parseDateToString(enddate, Constant.YMD_HMS));
				map.put("ma_mpscode", mainCode);
				map.put("ma_sourceid", rs.getInt("md_id"));
				map.put("ma_bomid", rs.getInt("md_bomid"));
				map.put("ma_salecode", rs.getString("md_sacode"));
				map.put("ma_saledetno", rs.getString("md_sadetno"));
				map.put("ma_cop", rs.getString("md_cop"));
				map.put("ma_kind", makekind);
				map.put("ma_checkstatus",
						BaseUtil.getLocalMessage("UNAPPROVED"));
				map.put("ma_checkstatuscode", "UNAPPROVED");
				map.put("ma_finishstatus",
						BaseUtil.getLocalMessage("UNCOMPLET"));
				map.put("ma_finishstatuscode", "UNCOMPLET");
				map.put("ma_turnstatus", BaseUtil.getLocalMessage("UNGET"));
				map.put("ma_turnstatuscode", "UNGET");
				map.put("ma_bonded", rs.getObject("md_bonded"));
				map.put("ma_factory", rs.getObject("md_factory"));
				// Makemaps.add(map);
				baseDao.execute(SqlUtil.getInsertSqlByMap(map, "Make"));
				makeBaseService.setMakeMaterial(NewCode, caller);
				baseDao.updateByCondition("mrpdata",
						" md_statuscode='THROWED',md_status='已投放',md_tomacode='"
								+ NewCode + "'",
						"md_id=" + rs.getString("md_id"));
				if(rs.getString("md_sacode")!=null&&!"".equals(rs.getString("md_sacode"))){
					int count1 = baseDao.getCount("select count(1) from mrpdata left join product on pr_id=md_prodid where nvl(md_status,' ')='未投放' and md_sacode='"+rs.getString("md_sacode")+"' and md_sadetno='"+rs.getString("md_sadetno")+"' and nvl(pr_dhzc,' ')='"+rs.getString("pr_dhzc")+"'");
					int sale_count = baseDao.getCount("select count(1) from sale left join saledetail on sa_id =sd_said where sa_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					int SaleForecast_count = baseDao.getCount("select count(1) from SaleForecast left join SaleForecastDetail on sf_id =sd_sfid where sf_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					//判断是否还存在改订单的需求投放界面
					if(count1>0){
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_makeneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_makeneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}else{
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_makeneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_makeneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}
				}
				count++;
				Makecount++;

			} else if ("OSMAKE".equals(rs.getObject("pr_manutype"))) {
				if (osmakekind == null || osmakekind.equals("")) {
					BaseUtil.showError("未定义默认的MRP投放委外单类型，不能投放");
				}
				mainid = baseDao.getSeqId("MAKE_SEQ");
				NewCode = baseDao.sGetMaxNumber("Make", 2);
				if (osmakelCode != null) {
					if (!osmakelCode.toString().equals("")) {
						// 修改前缀
						NewCode = osmakelCode.toString() + NewCode;
					}
				}
				map.put("ma_id", mainid);
				map.put("ma_code", NewCode);
				map.put("ma_source", "MRP");
				map.put("ma_tasktype", "OS");
				map.put("ma_prodid", rs.getInt("pr_id"));
				map.put("ma_prodcode", rs.getString("md_prodcode"));
				map.put("ma_prodname", rs.getString("pr_detail"));
				map.put("ma_prodspec", rs.getString("pr_spec"));
				map.put("ma_produnit", rs.getString("pr_unit"));
				map.put("ma_whcode", rs.getString("pr_whcode"));
				map.put("ma_qty", rs.getDouble("md_changeqty"));
				map.put("ma_madeqty", 0);
				map.put("ma_status", BaseUtil.getLocalMessage("AUDITED"));
				map.put("ma_statuscode", "AUDITED");
				map.put("ma_date", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				map.put("ma_recorddate", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				map.put("ma_recorder", SystemSession.getUser().getEm_name());
				map.put("ma_requiredate", rs.getDate("requiredate"));
				Date enddate = rs.getDate("md_needdate");
				if (enddate.compareTo(new Date()) < 0) {
					enddate = new Date();
				}
				Date begindate = rs.getDate("begindate");
				if (begindate.compareTo(new Date()) < 0) {
					begindate = new Date();
				}
				map.put("ma_planbegindate",
						DateUtil.parseDateToString(begindate, Constant.YMD_HMS));
				map.put("ma_planenddate",
						DateUtil.parseDateToString(enddate, Constant.YMD_HMS));
				map.put("ma_mpscode", mainCode);
				map.put("ma_sourceid", rs.getInt("md_id"));
				map.put("ma_bomid", rs.getInt("md_bomid"));
				map.put("ma_salecode", rs.getString("md_sacode"));
				map.put("ma_saledetno", rs.getString("md_sadetno"));
				map.put("ma_cop", rs.getString("md_cop"));
				map.put("ma_kind", osmakekind);
				map.put("ma_checkstatus",
						BaseUtil.getLocalMessage("UNAPPROVED"));
				map.put("ma_checkstatuscode", "UNAPPROVED");
				map.put("ma_finishstatus",
						BaseUtil.getLocalMessage("UNCOMPLET"));
				map.put("ma_finishstatuscode", "UNCOMPLET");
				map.put("ma_turnstatus", BaseUtil.getLocalMessage("UNGET"));
				map.put("ma_turnstatuscode", "UNGET");
				map.put("ma_bonded", rs.getObject("md_bonded"));
				map.put("ma_factory", rs.getObject("md_factory"));
				// Expmaps.add(map);
				baseDao.execute(SqlUtil.getInsertSqlByMap(map, "Make"));
				makeBaseService.setMakeMaterial(NewCode, caller);
				baseDao.updateByCondition("mrpdata",
						" md_statuscode='THROWED',md_status='已投放',md_tomacode='"
								+ NewCode + "'",
						"md_id=" + rs.getString("md_id"));
				if(rs.getString("md_sacode")!=null&&!"".equals(rs.getString("md_sacode"))){
					int count1 = baseDao.getCount("select count(1) from mrpdata left join product on pr_id=md_prodid where nvl(md_status,' ')='未投放' and md_sacode='"+rs.getString("md_sacode")+"' and md_sadetno='"+rs.getString("md_sadetno")+"' and nvl(pr_dhzc,' ')='"+rs.getString("pr_dhzc")+"'");
					int sale_count = baseDao.getCount("select count(1) from sale left join saledetail on sa_id =sd_said where sa_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					int SaleForecast_count = baseDao.getCount("select count(1) from SaleForecast left join SaleForecastDetail on sf_id =sd_sfid where sf_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					//判断是否还存在改订单的需求投放界面
					if(count1>0){
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_makeneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_makeneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}else{
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_makeneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_makeneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}
				}
				count++;
				OSMakecount++;
			} else if (rs.getString("md_action").equals("NEED")
					&& ("PURCHASE".equals(rs.getObject("pr_manutype")) || ("CUSTOFFER"
							.equals(rs.getObject("pr_manutype")) && IfThrowCustOffer
							.equals("Y")))) {
				if (Purcount == 0
						|| ("PURCHASE".equals(rs.getObject("pr_manutype")) && Purdetno == 1)
						|| ("CUSTOFFER".equals(rs.getObject("pr_manutype")) && Purdetno2 == 1)) {
					if (toCode == null || toCode.equals("")) {
						// 新增请购单,客供料不允许指定单号
						Map<Object, Object> tMap = new HashMap<Object, Object>();
						mainid = baseDao.getSeqId("APPLICATION_SEQ");
						NewCode = baseDao.sGetMaxNumber("Application", 2);
						if (purclCode != null) {
							if (!purclCode.toString().equals("")) {
								// 修改前缀
								NewCode = purclCode.toString() + NewCode;
							}
						}
						if ("PURCHASE".equals(rs.getObject("pr_manutype"))) {
							apid = mainid;
							apCode = NewCode;
							tMap.put("ap_kind", apkind);
						} else {
							if (apcustoffer == null || apcustoffer.equals("")) {
								BaseUtil.showError("未定义默认客供的请购单类型，不能投放");
							}
							apCode2 = NewCode;
							apid2 = mainid;
							tMap.put("ap_kind", apcustoffer);
						}
						if(apKind!=null&&!"".equals(apKind)){
							tMap.put("ap_kind", apKind);
						}
						tMap.put("ap_id", mainid);
						tMap.put("ap_code", NewCode);
						tMap.put("ap_date", DateUtil.parseDateToString(
								new Date(), Constant.YMD_HMS));
						tMap.put("ap_status",
								BaseUtil.getLocalMessage("ENTERING"));
						tMap.put("ap_statuscode", "ENTERING");
						tMap.put("ap_recorder", SystemSession.getUser()
								.getEm_name());
						tMap.put("ap_pleamanname", SystemSession.getUser()
								.getEm_name());
						tMap.put("ap_pleamanid", SystemSession.getUser()
								.getEm_id());
						tMap.put("ap_departname", SystemSession.getUser()
								.getEm_depart());
						tMap.put("ap_departcode", baseDao
								.getFieldDataByCondition("department",
										"dp_code", "dp_name='"
												+ SystemSession.getUser()
														.getEm_depart() + "'"));
						tMap.put("ap_recorderid", SystemSession.getUser()
								.getEm_id());
						tMap.put("ap_recorddate", DateUtil.parseDateToString(
								new Date(), Constant.YMD_HMS));
						tMap.put("ap_source", "MRP");
						tMap.put("ap_refcode", mainCode);
						tMap.put("ap_cop", rs.getString("md_cop"));
						tMap.put("AP_PURCHASECOP", purcaseCop!=null?purcaseCop:"");
						baseDao.getJdbcTemplate().update(
								SqlUtil.getInsertSqlByMap(tMap, "Application"));
					} else {// 检测请购单号是否正确
						if ("CUSTOFFER".equals(rs.getObject("pr_manutype"))) {
							if (!apcustoffer.equals(addapkind)) {
								BaseUtil.showError("客供料只能追加到客供类型的请购单");
							}
						} else {
							if (!apkind.equals(addapkind)) {
								BaseUtil.showError("只能追加到MRP默认投放的请购单");
							}
						}
						if (apid == 0) {
							String findsql = "select * from application where ap_code='"
									+ toCode + "'";
							SqlRowList sl = baseDao.queryForRowSet(findsql);
							if (!sl.hasNext()) {
								BaseUtil.showError("指定的请购单号不存在");
							}
							while (sl.next()) {// 在录入状态才允许追加
								if (!"ENTERING".equals(sl
										.getString("ap_statuscode"))) {
									BaseUtil.showError("只能追加到在录入的请购单");
								}
								mainid = sl.getInt("ap_id");
								apid = mainid;
								apid2 = mainid;
								NewCode = toCode;
								apCode = NewCode;
								apCode2 = NewCode;
								findsql = "select nvl(max(ad_detno),0) from applicationdetail where ad_apid="
										+ Integer.toString(mainid);
								SqlRowList rs1 = baseDao
										.queryForRowSet(findsql);
								if (rs1.next()) {
									Purdetno += rs1.getInt(1);
								}
							}
						}

					}

				}
				map.put("ad_id", baseDao.getSeqId("APPLICATIONDETAIL_SEQ"));
				if ("PURCHASE".equals(rs.getObject("pr_manutype"))) {
					map.put("ad_detno", Purdetno);
					map.put("ad_apid", apid);
					map.put("ad_code", apCode);
					Purdetno++;
				} else {// 客供请购
					map.put("ad_detno", Purdetno2);
					map.put("ad_apid", apid2);
					map.put("ad_code", apCode2);
					Purdetno2++;
				}
				map.put("ad_prodid", rs.getInt("pr_id"));
				map.put("ad_prodcode", rs.getString("md_prodcode"));
				map.put("ad_qty", rs.getDouble("md_changeqty"));
				map.put("ad_mrpqty", rs.getDouble("md_needqty"));
				map.put("ad_leadtime", rs.getGeneralInt("pr_leadtime"));
				map.put("ad_minpack", rs.getGeneralDouble("pr_zxbzs"));
				map.put("ad_minorder", rs.getGeneralDouble("pr_zxdhl"));
				Date needdate = rs.getDate("md_needdate");
				if (needdate.compareTo(new Date()) < 0) {
					needdate = new Date();
				}
				map.put("ad_delivery",
						DateUtil.parseDateToString(needdate, Constant.YMD_HMS));
				map.put("ad_jypudate",
						DateUtil.parseDateToString(needdate, Constant.YMD_HMS));
				map.put("ad_mrpcode", mainCode);
				map.put("ad_mdid", rs.getInt("md_id"));
				map.put("ad_remark", rs.getString("md_topprodcode"));
				if(IfPRBonedFromProduct){
				   map.put("ad_bonded", rs.getGeneralInt("pr_bonded"));
				}else{
				   map.put("ad_bonded", rs.getInt("md_bonded"));
				}
				if (IfmrpSeparateSeller.equals("Y")){
					map.put("ad_sellercode", rs.getObject("md_partno"));
					map.put("ad_seller", rs.getObject("md_partname"));
				}
				map.put("ad_factory", rs.getObject("md_factory"));
				map.put("ad_ifrep", rs.getInt("md_ifrep"));
				if(IfMrpSeparateSaleNO){//@add20171103如果启用订单锁库模式参数，则md_sacode,md_sadetno转到请购单明细行的字段订单号和订单序号
					map.put("ad_sacode", rs.getObject("md_sacode"));
					map.put("ad_sadetno", rs.getInt("md_sadetno"));
				}
				baseDao.execute(SqlUtil.getInsertSqlByMap(map,
						"APPLICATIONDETAIL"));
				baseDao.updateByCondition("mrpdata",
						" md_statuscode='THROWED',md_status='已投放'", "md_id="
								+ rs.getString("md_id"));
				if(rs.getString("md_sacode")!=null&&!"".equals(rs.getString("md_sacode"))){
					int count1 = baseDao.getCount("select count(1) from mrpdata left join product on pr_id=md_prodid where nvl(md_status,' ')='未投放' and md_sacode='"+rs.getString("md_sacode")+"' and md_sadetno='"+rs.getString("md_sadetno")+"' and nvl(pr_dhzc,' ')='"+rs.getString("PR_DHZC")+"'");
					int sale_count = baseDao.getCount("select count(1) from sale left join saledetail on sa_id =sd_said where sa_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					int SaleForecast_count = baseDao.getCount("select count(1) from SaleForecast left join SaleForecastDetail on sf_id =sd_sfid where sf_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					//判断是否还存在改订单的需求投放界面
					if(count1>0){
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_purneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastdetail set sd_purneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}else{
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_purneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastdetail set sd_purneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}
				}
				// Expmaps.add(map);
				count++;
				Purcount++;
			} else if ("PURCHASE".equals(rs.getObject("pr_manutype"))
					&& rs.getString("md_action").equals("PFNEED")) {
				if (PFcount == 0) {
					Map<Object, Object> tMap = new HashMap<Object, Object>();
					mainid = baseDao.getSeqId("PURCHASEFORECAST_SEQ");
					NewCode = baseDao.sGetMaxNumber("PurchaseForecast", 2);
					tMap.put("pf_id", mainid);
					tMap.put("pf_code", NewCode);
					tMap.put("pf_date", DateUtil.parseDateToString(new Date(),
							Constant.YMD_HMS));
					tMap.put("pf_status", BaseUtil.getLocalMessage("ENTERING"));
					tMap.put("pf_statuscode", "ENTERING");
					tMap.put("pf_recorder", SystemSession.getUser()
							.getEm_name());
					tMap.put("pf_recorderid", SystemSession.getUser()
							.getEm_id());
					tMap.put("pf_indate", DateUtil.parseDateToString(
							new Date(), Constant.YMD_HMS));
					tMap.put("pf_source", "MRP");
					tMap.put("pf_sourcecode", mainCode);
					baseDao.getJdbcTemplate()
							.update(SqlUtil.getInsertSqlByMap(tMap,
									"PurchaseForecast"));
					pfid = mainid;
					PFCode = NewCode;
				}

				map.put("pfd_id",
						baseDao.getSeqId("PURCHASEFORECASTDETAIL_SEQ"));
				map.put("pfd_pfid", pfid);
				map.put("pfd_code", PFCode);
				map.put("pfd_detno", PFdetno);
				map.put("pfd_prodid", rs.getInt("pr_id"));
				map.put("pfd_prodcode", rs.getString("md_prodcode"));
				map.put("pfd_qty", rs.getDouble("md_changeqty"));
				map.put("pfd_delivery", rs.getDate("md_needdate"));
				map.put("pfd_sourcecode", mainCode);
				map.put("pfd_mdid", rs.getInt("md_id"));
				map.put("pfd_bonded", rs.getInt("md_bonded"));
				map.put("pfd_ifrep", rs.getInt("md_ifrep"));
				baseDao.execute(SqlUtil.getInsertSqlByMap(map,
						"PURCHASEFORECASTDETAIL"));
				baseDao.updateByCondition("mrpdata",
						" md_statuscode='THROWED',md_status='已投放'", "md_id="
								+ rs.getString("md_id"));
				// Expmaps.add(map);
				
				if(rs.getString("md_sacode")!=null&&!"".equals(rs.getString("md_sacode"))){
					int count1 = baseDao.getCount("select count(1) from mrpdata left join product on pr_id=md_prodid where nvl(md_status,' ')='未投放' and md_sacode='"+rs.getString("md_sacode")+"' and md_sadetno='"+rs.getString("md_sadetno")+"' and nvl(pr_dhzc,' ')='"+rs.getString("pr_dhzc")+"'");
					int sale_count = baseDao.getCount("select count(1) from sale left join saledetail on sa_id =sd_said where sa_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					int SaleForecast_count = baseDao.getCount("select count(1) from SaleForecast left join SaleForecastDetail on sf_id =sd_sfid where sf_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
					//判断是否还存在改订单的需求投放界面
					if(count1>0){
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_purneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_purneed='部分投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}else{
						//判断是销售还是销售预测
						if(sale_count>0){
							baseDao.execute("update saledetail set sd_purneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}else if(SaleForecast_count>0){
							baseDao.execute("update SaleForecastDetail set sd_purneed='全部投放' where sd_code='"+rs.getString("md_sacode")+"' and sd_detno='"+rs.getString("md_sadetno")+"'");
						}
					}
				}
				
				count++;
				PFcount++;
				PFdetno++;
			}
			errMap.put("ID", rs.getInt("md_id"));
			errMap.put("REASON", "UNDEFINED MANUTYPE");
		}
		// 将mrpreplace的数据根据md_id写入到applicationreplace[去掉 已经生成过replace的明细]中
		if (apid > 0) {
			baseDao.execute("insert into ApplicationReplace (ar_id,ar_mdid,ar_adid,ar_apid,ar_prodcode,ar_repcode,ar_needqty,"
					+ "ar_purcqty,ar_mpsid,ar_realqty,ar_vendor,ar_vendname,ar_changeqty,ar_veid,ar_price,ar_currency,ar_ifvendrate) "
					+ "select APPLICATIONREPLACE_SEQ.nextval,mr_mdid,ad_id,ad_apid,mr_prodcode,mr_repcode,mr_needqty,"
					+ "mr_purcqty,mr_mpsid,mr_realqty,mr_vendor,mr_vendname,mr_changeqty,mr_veid,mr_price,mr_currency,mr_ifvendrate "
					+ "from applicationdetail , mrpreplace where ad_mdid=mr_mdid and ad_apid="
					+ apid
					+ " and not exists(select 1 from ApplicationReplace where ar_apid="
					+ apid + " and ar_adid=ad_id)");
		} else if (apid2 > 0) {
			baseDao.execute("insert into ApplicationReplace (ar_id,ar_mdid,ar_adid,ar_apid,ar_prodcode,ar_repcode,ar_needqty,"
					+ "ar_purcqty,ar_mpsid,ar_realqty,ar_vendor,ar_vendname,ar_changeqty,ar_veid,ar_price,ar_currency,ar_ifvendrate) "
					+ "select APPLICATIONREPLACE_SEQ.nextval,mr_mdid,ad_id,ad_apid,mr_prodcode,mr_repcode,mr_needqty,"
					+ "mr_purcqty,mr_mpsid,mr_realqty,mr_vendor,mr_vendname,mr_changeqty,mr_veid,mr_price,mr_currency,mr_ifvendrate "
					+ "from applicationdetail , mrpreplace where ad_mdid=mr_mdid and ad_apid="
					+ apid2
					+ " and not exists(select 1 from ApplicationReplace where ar_apid="
					+ apid2 + " and ar_adid=ad_id)");
		}
		StringBuffer log = new StringBuffer();
		if(needStoreSize>999){
			log.append("勾选的数据超过1000条，已为您投放前1000条，剩余的部分请再次勾选投放!<br>");
		}
		if (baseDao.isDBSetting("MpsDesk", "autoAuditAfterThrow")) {// 请购单投放后自动审核
			try {
				if (apid > 0) {// 审核MRP请购单
					baseDao.updateByCondition(
							"Application",
							"ap_statuscode='COMMITED',ap_status='"
									+ BaseUtil.getLocalMessage("COMMITED")
									+ "'", "ap_id=" + apid);
					baseDao.updateByCondition(
							"ApplicationDetail",
							"ad_statuscode='COMMITED',ad_status='"
									+ BaseUtil.getLocalMessage("COMMITED")
									+ "'", "ad_apid=" + apid);
					applicationService.auditApplication(apid, "Application");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.append("请购单：").append("<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?whoami=Application&formCondition=ap_idIS").append(apid).append("&gridCondition=ad_apidIS").append(apid).
				append("')\">").append(apCode).append("</a>&nbsp;").append("自动审核失败,").append(e.getMessage()).append("<br>");
			}
			try {
				if (apid2 > 0) {// 审核客供请购单
					baseDao.updateByCondition(
							"Application",
							"ap_statuscode='COMMITED',ap_status='"
									+ BaseUtil.getLocalMessage("COMMITED")
									+ "'", "ap_id=" + apid2);
					baseDao.updateByCondition(
							"ApplicationDetail",
							"ad_statuscode='COMMITED',ad_status='"
									+ BaseUtil.getLocalMessage("COMMITED")
									+ "'", "ad_apid=" + apid2);
					applicationService.auditApplication(apid2, "Application");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.append("请购单：").append("<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?whoami=Application&formCondition=ap_idIS").append(apid2).append("&gridCondition=ad_apidIS").append(apid2).
				append("')\">").append(apCode2).append("</a>&nbsp;").append("自动审核失败,").append(e.getMessage());
			}
		}
		if (baseDao.isDBSetting("MpsDesk", "autoCommitAfterThrow")&&!baseDao.isDBSetting("MpsDesk", "autoAuditAfterThrow")) {// 请购单投放后自动审核
			try {
				if (apid > 0) {
					// 提交MRP请购单
					applicationService.submitApplication(apid, "Application");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.append("请购单：").append("<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?whoami=Application&formCondition=ap_idIS").append(apid).append("&gridCondition=ad_apidIS").append(apid).
				append("')\">").append(apCode).append("</a>&nbsp;").append("自动提交失败,").append(e.getMessage()).append("<br>");
			}
			
			try {
				if (apid2 > 0) {// 提交客供请购单
					applicationService.submitApplication(apid2, "Application");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.append("请购单：").append("<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?whoami=Application&formCondition=ap_idIS").append(apid2).append("&gridCondition=ad_apidIS").append(apid2).
				append("')\">").append(apCode2).append("</a>&nbsp;").append("自动提交失败,").append(e.getMessage());
			}
		}
		ReturnMap.put("falserow", errMap);
		ReturnMap.put("Makecount", Makecount);
		ReturnMap.put("OSMakecount", OSMakecount);
		ReturnMap.put("Purcount", Purcount);
		ReturnMap.put("PFcount", PFcount);
		ReturnMap.put("count", count);
		ReturnMap.put("errcount", allcount-count);
		ReturnMap.put("log", log.toString());
		return ReturnMap;
	}

	@Override
	public void deleteAllDetails(int id, String caller) {
		baseDao.deleteByCondition("MpsDetail", "md_mainid=" + id);
		baseDao.logger.delete(caller, "mm_id", id);
	}

	@Override
	public int getCountByCaller(String caller, String condition) {
		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String distinct = dataList.getDl_distinct();
		String str = distinct != null ? "Select count(distinct " + distinct
				+ ") FROM " + dataList.getDl_tablename()
				: "SELECT count(*) FROM " + dataList.getDl_tablename();
		String con = dataList.getDl_condition();
		condition = (con == null || "".equals(con)) ? condition : con
				+ ((condition == null || "".equals(condition)) ? "" : " AND "
						+ condition);
		condition = condition.equals("") ? "" : " WHERE " + condition;

		String sql = condition.equals("") ? str : str + " " + condition;
		return baseDao.getCount(sql);
	}

	@Override
	public GridPanel getDataListGridByCaller(String caller, String condition,
			Boolean _self) {
		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		List<DataListDetail> details = dataList.getDataListDetails();
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(
				caller, SystemSession.getUser().getEm_master());
		GridPanel gridPanel = new GridPanel();
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		// store的字段fields
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		for (DataListDetail detail : details) {
			// 从数据库表datalistdetail的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
			fields.add(new GridFields(detail));
			columns.add(new GridColumns(detail, combos, SystemSession.getLang()));
		}
		if (_self != null && _self) {// 只查看自己录入的数据
			String f = dataList.getDl_entryfield();
			if (f != null && f.trim().length() > 0) {
				if (condition != null && condition.trim().length() > 0) {
					condition += " AND ";
				}
				condition += f + "='" + SystemSession.getUser().getEm_id()
						+ "'";
			} else {
				BaseUtil.showErrorOnSuccess("无法限制列表权限!原因: 未配置录入人字段.");
			}
		}
		gridPanel.setDataString(BaseUtil.parseGridStore2Str(getDataListData(
				dataList, condition)));
		gridPanel.setGridColumns(columns);
		gridPanel.setGridFields(fields);
		gridPanel.setKeyField(dataList.getDl_keyfield());
		gridPanel.setUrl(dataList.getDl_lockpage());
		gridPanel.setRelative(dataList.getDl_relative());
		gridPanel.setPfField(dataList.getDl_pffield());
		gridPanel.setVastbutton(dataList.getDl_fixedcondition());
		return gridPanel;
	}

	public List<Map<String, Object>> getDataListData(DataList dataList,
			String condition) {

		String con = dataList.getDl_condition();
		condition = (con == null || "".equals(con)) ? condition : con
				+ ((condition == null || "".equals(condition)) ? "" : " AND "
						+ condition);
		StringBuffer sb = new StringBuffer("SELECT ");
		String groupby = dataList.getDl_orderby();
		if (dataList.getDl_distinct() != null) {
			sb.append("distinct " + dataList.getDl_distinct() + ",");
		}
		for (DataListDetail detail : dataList.getDataListDetails()) {
			if (!detail.getDld_field().equals(dataList.getDl_distinct())) {
				sb.append(detail.getDld_field());
				sb.append(",");
			}

		}
		String str = sb.substring(0, sb.length() - 1);
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		if (groupby == null || groupby.equals("")) {
			groupby = "order by " + dataList.getDl_keyfield() + " desc";
		}
		sb.setLength(0);
		sb.append(str);
		sb.append(" from " + dataList.getDl_tablename() + " ");
		sb.append(condition);
		sb.append(" " + groupby);
		List<Map<String, Object>> list = baseDao.getJdbcTemplate(
				dataList.getDl_tablename()).queryForList(sb.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DataListDetail detail : dataList.getDataListDetails()) {
				String field = detail.getDld_field();
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(
									new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}

	@Override
	public int getMPSPRonorder(String caller, String condition) {
		String sql = "select count(*) from ((SELECT pd_code AS code, pd_detno AS detno,(pd_qty - nvl(pd_acceptqty,0)) AS qty, pu_vendname AS cust, pd_delivery AS deliverydate, '采购单' AS kind, pd_prodcode AS pcode FROM PurchaseDetail LEFT JOIN Purchase ON pu_id = pd_puid WHERE pd_qty>nvl(pd_acceptqty,0) and pu_statuscode in ('ENTERING','COMMITED','AUDITED')and NVL(pd_mrpstatuscode, ' ') not in ('FREEZE','已冻结','FINISH','已结案','已作废','NULLIFIED')  )UNION (SELECT ma_code AS code,  0 AS detno, (ma_qty - nvl(ma_madeqty,0)) AS qty, ma_custname AS cust, ma_planenddate AS deliverydate, '制造单' AS kind, ma_prodcode AS pcode FROM  make WHERE  ma_statuscode ='AUDITED' and nvl(ma_madeqty,0)<ma_qty )UNION (SELECT ap_code AS code, ad_detno AS detno, ad_qty - (CASE WHEN nvl(ad_yqty, 0) > ad_qty THEN ad_qty ELSE nvl(ad_yqty, 0) END) AS qty,'' AS cust, ad_delivery AS deliverydate, '请购单' AS kind, ad_prodcode AS pcode FROM application,applicationdetail WHERE ad_apid=ap_id and ap_statuscode='AUDITED' and ad_qty>nvl(ad_yqty,0) AND NVL(ad_statuscode, ' ') not in ('FINISH','已结案','已作废','NULLIFIED'))) A";
		return baseDao.getCount(sql);
	}

	@Override
	public GridPanel getMPSPRonorder(String caller, String condition,
			Boolean _self, int page, int pageSize) {
		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		List<DataListDetail> details = dataList.getDataListDetails();
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(
				caller, SystemSession.getUser().getEm_master());
		GridPanel gridPanel = new GridPanel();
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		// store的字段fields
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		for (DataListDetail detail : details) {
			// 从数据库表datalistdetail的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
			fields.add(new GridFields(detail));
			columns.add(new GridColumns(detail, combos, SystemSession.getLang()));
		}
		if (_self != null && _self) {// 只查看自己录入的数据
			String f = dataList.getDl_entryfield();
			if (f != null && f.trim().length() > 0) {
				if (condition != null && condition.trim().length() > 0) {
					condition += " AND ";
				}
				condition += f + "='" + SystemSession.getUser().getEm_id()
						+ "'";
			} else {
				BaseUtil.showErrorOnSuccess("无法限制列表权限!原因: 未配置录入人字段.");
			}
		}
		gridPanel.setDataString(BaseUtil.parseGridStore2Str(getMakeCommits(
				caller, dataList, condition, page, pageSize)));
		gridPanel.setGridColumns(columns);
		gridPanel.setGridFields(fields);
		gridPanel.setKeyField(dataList.getDl_keyfield());
		gridPanel.setUrl(dataList.getDl_lockpage());
		gridPanel.setRelative(dataList.getDl_relative());
		gridPanel.setPfField(dataList.getDl_pffield());
		gridPanel.setVastbutton(dataList.getDl_fixedcondition());
		return gridPanel;
	}

	public List<Map<String, Object>> getMakeCommits(String caller,
			DataList dataList, String condition, int page, int pageSize) {
		String con = dataList.getDl_condition();
		String table = "((SELECT pd_code AS code, pd_detno AS detno,(pd_qty - nvl(pd_acceptqty,0)) AS qty, pu_vendname AS cust, pd_delivery AS deliverydate, '采购单' AS kind, pd_prodcode AS pcode,pd_factory factory,pr_detail pdetail,pr_spec pspec FROM PurchaseDetail LEFT JOIN Purchase ON pu_id = pd_puid left join product on pr_code=pd_prodcode WHERE pd_qty>nvl(pd_acceptqty,0) and pu_statuscode in ('ENTERING','COMMITED','AUDITED')and NVL(pd_mrpstatuscode, ' ') not in ('FREEZE','已冻结','FINISH','已结案','已作废','NULLIFIED')  ) "
				+ " UNION (SELECT ma_code AS code,  0 AS detno, (ma_qty - nvl(ma_madeqty,0)) AS qty, ma_custname AS cust, ma_plandelivery AS deliverydate, case when ma_tasktype='OS' then '委外单' else '制造单' end kind, ma_prodcode AS pcode,ma_factory factory,pr_detail pdetail,pr_spec pspec FROM  make left join product on pr_code=ma_prodcode WHERE  ma_statuscode ='AUDITED' and nvl(ma_madeqty,0)<ma_qty ) "
				+ " UNION (SELECT ap_code AS code, ad_detno AS detno, ad_qty - (CASE WHEN nvl(ad_yqty, 0) > ad_qty THEN ad_qty ELSE nvl(ad_yqty, 0) END) AS qty,'' AS cust, ad_delivery AS deliverydate, '请购单' AS kind, ad_prodcode AS pcode,ad_factory factory,pr_detail pdetail,pr_spec pspec FROM applicationdetail left join application on ad_apid=ap_id left join product on pr_code=ad_prodcode where ap_statuscode='AUDITED' and ad_qty>nvl(ad_yqty,0) AND NVL(ad_statuscode, ' ') not in ('FINISH','已结案','已作废','NULLIFIED'))) A";
		condition = (con == null || "".equals(con)) ? condition : con
				+ ((condition == null || "".equals(condition)) ? "" : " AND "
						+ condition);
		StringBuffer sb = new StringBuffer("SELECT ");
		StringBuffer sub = new StringBuffer("SELECT ");
		String orderby = null;
		String[] ff = null;
		for (DataListDetail detail : dataList.getDataListDetails()) {
			ff = detail.getDld_field().split(" ");
			sub.append(ff[ff.length - 1]);// 别名
			sub.append(",");
		}
		for (DataListDetail detail : dataList.getDataListDetails()) {
			sb.append(detail.getDld_field());
			sb.append(",");
		}
		String str = sb.substring(0, sb.length() - 1);
		condition = "".equals(condition) ? "" : " WHERE " + condition;

		if (orderby == null || orderby.equals("")) {
			orderby = "order by " + dataList.getDl_keyfield() + " desc";
		}
		sb = new StringBuffer(sub.substring(0, sub.length() - 1));
		sb.append(" FROM (");
		sb.append(str);
		sb.append(",row_number()over(");
		sb.append(orderby);
		sb.append(") rn FROM ");
		sb.append(table);
		sb.append(" ");
		sb.append(condition);
		sb.append(")where rn between ");
		sb.append(((page - 1) * pageSize + 1));
		sb.append(" and ");
		sb.append(page * pageSize);
		List<Map<String, Object>> list = baseDao.getJdbcTemplate()
				.queryForList(sb.toString());

		Iterator<Map<String, Object>> iter = list.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		while (iter.hasNext()) {
			map = iter.next();
			for (DataListDetail detail : dataList.getDataListDetails()) {
				String field = detail.getDld_field();
				Object value = map.get(field.toUpperCase());
				value = value == null || value.equals("null") ? "" : value;
				if (value != null) {
					String classname = value.getClass().getSimpleName();
					if (classname.toUpperCase().equals("TIMESTAMP")) {
						Timestamp time = (Timestamp) value;
						try {
							value = DateUtil.parseDateToString(
									new Date(time.getTime()), Constant.YMD_HMS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				map.remove(field.toUpperCase());
				map.put(field, value);
			}
			datas.add(map);
		}
		return datas;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String turnReplaceProd(String data, String apdata,
			String purchasecode, String caller) {
		JSONObject j = new JSONObject();
		Map<Object, Object> apmap = BaseUtil.parseFormStoreToMap(apdata);
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		String vendcode = null;
		String pucode = null;
		String log = null;
		int puid = 0;
		double endqty = 0;
		if (purchasecode != null && !purchasecode.equals("")) {
			pucode = purchasecode;
		}
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < lists.size(); i++) {
			//判断替代料供应商是否为空，为空就不允许下达操作
			if("".equals(lists.get(i).get("ar_vendor")) || lists.get(i).get("ar_vendor")==null){
				BaseUtil.showError("该替代料没有供应商，不允许下达操作");
			}
			// 判断采购单的状态
			if ("".equals(purchasecode) || purchasecode == null) {
				if (vendcode == null
						|| !vendcode.equals(lists.get(i).get("ar_vendor")
								.toString())) {
					vendcode = lists.get(i).get("ar_vendor").toString();
				}
				String type = apmap.get("ap_type") == null ? null : apmap.get(
						"ap_type").toString();
				if (vendcode != null && !"".equals(vendcode)) {
					Object[] objs = baseDao.getFieldsDataByCondition("Vendor",
							new String[] { "ve_id", "ve_name" }, "ve_code='"
									+ vendcode + "'");
					Object currency=apmap.get("ap_currency");
					if (currency==null||"".equals(currency)){
						currency=baseDao.getFieldDataByCondition("applicationreplace","ar_currency", "ar_id="+lists.get(i).get("ar_id"));
						if(currency==null||"".equals(currency)){
							currency=baseDao.getFieldDataByCondition("vendor","ve_currency", "ve_code='"+vendcode+"'");
						}
					}
					j = applicationDao.newPurchaseWithVendor(type,
							Integer.parseInt(objs[0].toString()), vendcode,
							objs[1].toString(),
							String.valueOf(apmap.get("ap_kind")),
							String.valueOf(currency));
					pucode = j.get("pu_code").toString();
				} else {
					pucode = applicationDao.newPurchase(type);
					j.put("pu_code", pucode);
					j.put("pu_id", baseDao.getFieldDataByCondition("purchase",
							"pu_id", "pu_code='" + j.get("pu_code") + "'"));
				}
				// 更新交货地址
				baseDao.execute("update purchase set pu_shipaddresscode=(select en_deliveraddr from enterprise where nvl(en_deliveraddr,' ')<>' ') where pu_code='"
						+ pucode + "' and nvl(pu_shipaddresscode,' ')=' '");
				baseDao.execute("update purchase set pu_rate=(select cm_crrate from currencysmonth where cm_crname=pu_currency and cm_yearmonth=to_char(pu_date,'yyyymm')) where pu_code='"
						+ pucode + "'");
			}
			Object[] fielddatas = baseDao.getFieldsDataByCondition("Purchase",
					"pu_statuscode,pu_id", "pu_code='" + pucode + "'");
			if (fielddatas != null && fielddatas[0] != null
					&& !fielddatas[0].equals("ENTERING")) {
				BaseUtil.showError("只能下达到单据状态为在录入的采购单!");
			} else {
				puid = Integer.parseInt(fielddatas[1].toString());
				// 取到最大的 序号
				int maxdetno = 0;
				Object detno = baseDao.getFieldDataByCondition(
						"PurchaseDetail left join Purchase on pd_puid=pu_id",
						"max(pd_detno)", "pu_code='" + pucode + "'");
				maxdetno = detno != null ? Integer.parseInt(detno.toString()) + 1
						: 1;
				Map<Object, Object> map = lists.get(i);
				//下达数量不能为0
				if(Integer.parseInt(map.get("ar_realqty").toString())<=0){
					BaseUtil.showError("下达数量小于等于0不允许下达操作");
				}
				if (Double.parseDouble(map.get("ar_realqty").toString()) > 0) {
					endqty += Double.parseDouble(map.get("ar_realqty")
							.toString());
					int ar_id = 0;
					if (map.get("ar_id") != null
							&& !"0".equals(map.get("ar_id"))) {
						ar_id = Integer.valueOf(map.get("ar_id").toString());
					}
					sqls.add("insert into purchaseDetail(pd_id,pd_code,pd_puid,pd_prodcode,pd_detno,pd_qty,pd_source,pd_sourcecode,pd_sourcedetail,pd_mmid,pd_factory,pd_mtid) values("
							+ baseDao.getSeqId("PURCHASEDETAIL_SEQ")
							+ ",'"
							+ pucode
							+ "',"
							+ fielddatas[1]
							+ ",'"
							+ map.get("ar_repcode")
							+ "','"
							+ maxdetno
							+ "',"
							+ map.get("ar_realqty")
							+ ","
							+ apmap.get("ap_id")
							+ ",'"
							+ apmap.get("ap_code")
							+ "',"
							+ apmap.get("ad_id")
							+ ","
							+ apmap.get("mr_id")
							+ ",(select max(ad_factory) from applicationDetail where ad_id="
							+ apmap.get("ad_id") + " )," + ar_id + ")");
					// 更新MRPReplace里面的mr_purcqty
					sqls.add("update ApplicationReplace set ar_purcqty=NVL(ar_purcqty,0)+"
							+ Double.parseDouble(map.get("ar_realqty")
									.toString())
							+ " where ar_id="
							+ map.get("ar_id"));
					if (map.get("mr_id") != null
							&& !"0".equals(map.get("mr_id"))
							&& map.get("mr_realqty") != null
							&& !"0".equals(map.get("mr_realqty"))) {
						// applicationreplace
						sqls.add("update MrpReplace set mr_purcqty=NVL(mr_purcqty,0)+"
								+ Double.parseDouble(map.get("mr_realqty")
										.toString())
								+ " where mr_id="
								+ map.get("mr_id"));
					}
				}
			}
		}
		int c = baseDao.getCountByCondition("applicationDetail", "ad_id='"
				+ apmap.get("ad_id") + "' and NVL(ad_yqty,0)+" + endqty
				+ ">ad_qty");
		if (c > 0) {
			BaseUtil.showError("不能超请购数下达!");
		}
		// 再更新 ApplicationDetail 里面的 最终采购计划的已下达数
		sqls.add("update applicationDetail set ad_yqty=NVL(ad_yqty,0)+"
				+ endqty + " where ad_id=" + apmap.get("ad_id"));
		baseDao.execute(sqls);
		// 取价原则：抓取最近一次采购单单价  maz  2018060670  改成radio形式 增加取采购验收单单价
		String PriceByPurc = baseDao.getDBSetting("Purchase", "getPriceByPurc");
		if ("1".equals(PriceByPurc)) {
			SqlRowList rs = baseDao
					.queryForRowSet("select pu_id from purchase where pu_code='" + pucode + "' and nvl(pu_getprice,0)=0");
			while (rs.next()) {
				purchaseDao.getLastPrice(rs.getGeneralInt(1));
			}
		} else if ("2".equals(PriceByPurc)){
			SqlRowList rs = baseDao
					.queryForRowSet("select pu_id from purchase where pu_code='" + pucode + "' and nvl(pu_getprice,0)=0");
			while (rs.next()){
				SqlRowList rs1 = baseDao.queryForRowSet(
						"SELECT * FROM PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid WHERE pu_id=? and nvl(pd_price,0)=0 and nvl(pu_getprice,0)=0",
						rs.getInt("pu_id"));
				SqlRowList pd = baseDao.queryForRowSet(
						"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
								+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
						rs1.getString("pd_prodcode"), rs1.getString("pu_currency"), rs1.getString("pu_vendcode"));
				if (pd.next()) {
					baseDao.updateByCondition("PurchaseDetail",
							"pd_price=" + pd.getGeneralDouble("pd_price") + ",pd_bgprice=" + pd.getGeneralDouble("pd_customprice") + ", pd_rate="
									+ pd.getGeneralDouble("pd_taxrate") + ", pd_netprice=" + pd.getGeneralDouble("pd_netprice"),
							"pd_id=" + rs1.getGeneralInt("pd_id"));
				}
			}
		} else {
			// 修改新增的采购单的单价
			SqlRowList rs = baseDao.queryForRowSet("select pu_code from purchase where pu_code='" + pucode
					+ "' and abs(nvl(pu_getprice,0))=1");
			while (rs.next()) {
				purchaseDao.getPrice(rs.getString(1));
			}
		}
		baseDao.execute("update PurchaseDetail set pd_total=round(pd_price*pd_qty,2) where pd_code='" + pucode + "'");
		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_code='"
				+ pucode + "'");
		baseDao.execute("update purchasedetail set pd_netprice=round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8) where pd_code='"
				+ pucode + "'");
		baseDao.execute("update purchasedetail set pd_taxtotal=round(nvl(pd_netprice,0)*nvl(pd_qty,0),2) where pd_code='" + pucode
				+ "'");
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_code='"
				+ pucode + "'");
		baseDao.execute("update Purchase set pu_totalupper=L2U(nvl(pu_total,0)) WHERE pu_code='" + pucode + "'");
		log = "转入成功,采购单号:"
				+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS"
				+ puid + "&gridCondition=pd_puidIS" + puid + "')\">" + pucode
				+ "</a>&nbsp;";
		return log;
	}

	@Override
	public String mpsdesk_turnmake(String code, String caller) {
		baseDao.execute("update mpsmain set mm_postatus='MAKE' where mm_code='"
				+ code + "'");
		return "目前可以投放制造件了";
	}

	@Override
	public String mpsdesk_turnpurchase(String code, String caller) {
		baseDao.execute("update mpsmain set mm_postatus='PURCHASE' where mm_code='"
				+ code + "'");
		Object mm_id="";
		SqlRowList rs ;
		if(!"".equals(code) && code!=null){
			mm_id = baseDao.getFieldDataByCondition("MpsMain", "mm_id", "mm_code='"+code+"'");
		}
		boolean is = baseDao.isDBSetting(caller, "unAuditApplicationforMRP");
		if(is){
			rs = baseDao.queryForRowSet("select ap_id from application where nvl(ap_statuscode,' ') ='ENTERING' and nvl(ap_source,' ')='MRP'");
			while(rs.next()){
				baseDao.logger.others("MRP清除无效PR", "操作结果，清除成功，MRP编号："+code, "Application", "ap_id", rs.getInt("ap_id"));
			}
			baseDao.execute("update applicationDetail set ad_status='"
					+ BaseUtil.getLocalMessage("NULLIFIED")
					+ "',ad_statuscode='NULLIFIED' where ad_apid in (select ap_id from application where ap_statuscode='ENTERING' and ap_source='MRP')");
			baseDao.execute("update application set ap_status='"
					+ BaseUtil.getLocalMessage("NULLIFIED")
					+ "',ap_statuscode='NULLIFIED' where ap_statuscode ='ENTERING' and ap_source='MRP'");
		}else{
			rs = baseDao.queryForRowSet("select ap_id from application where ap_statuscode in ('ENTERING','COMMITED') and ap_source='MRP'");
			while(rs.next()){
				baseDao.logger.others("MRP清除无效PR", "操作结果，清除成功，MRP编号："+code, "Application", "ap_id", rs.getInt("ap_id"));
			}
			baseDao.execute("update applicationDetail set ad_status='"
					+ BaseUtil.getLocalMessage("NULLIFIED")
					+ "',ad_statuscode='NULLIFIED' where ad_apid in (select ap_id from application where ap_statuscode in ('ENTERING','COMMITED') and ap_source='MRP')");
			baseDao.execute("update application set ap_status='"
					+ BaseUtil.getLocalMessage("NULLIFIED")
					+ "',ap_statuscode='NULLIFIED' where ap_statuscode in ('ENTERING','COMMITED') and ap_source='MRP'");
		}
		baseDao.execute("update applicationDetail set ad_status='"
				+ BaseUtil.getLocalMessage("NULLIFIED")
				+ "',ad_statuscode='NULLIFIED' where ad_apid in (select ap_id from application where ap_statuscode='NULLIFIED') and ad_statuscode<>'NULLIFIED'");
		baseDao.logger.getMessageLog("清理无效PR", "清理成功", caller, "mm_code", code);
		baseDao.logger.others("清理无效PR", "清理成功", "MpsMain", "mm_id", mm_id);
		// to do 清除审批流
		baseDao.execute("update jprocess set jp_flag=0,jp_status='已结束',jp_type='清理无效PR',jp_updatetime=sysdate  where  jp_caller='Application'  and (jp_status='待审批' or jp_status='未通过') and jp_keyvalue in (select ap_id from application where ap_statuscode='NULLIFIED' and ap_source='MRP')");
		baseDao.execute("update jprocand set jp_flag=0,jp_status='已结束',jp_type='清理无效PR',jp_updatetime=sysdate  where  jp_caller='Application'  and (jp_status='待审批' or jp_status='未通过') and jp_keyvalue in (select ap_id from application where ap_statuscode='NULLIFIED' and ap_source='MRP')");
		return "当前未审批的请购单已经作废，目前可以投放采购件了";
	}

	@Override
	public String mpsdesk_turnpurchaseforecast(String code, String caller) {
		baseDao.execute("update mpsmain set mm_postatus='PURCHASEFORECAST' where mm_code='"
				+ code + "'");
		baseDao.execute("update purchaseforecast set pf_status='"
				+ BaseUtil.getLocalMessage("NULLIFIED")
				+ "',pf_statuscode='NULLIFIED',pf_sendstatus='待上传' where pf_statuscode <>'NULLIFIED' and pf_source='MRP' ");
		baseDao.execute("update purchaseforecastdetail set PFD_THROWSTATUS='已取消',PFD_THROWSTATUScode='CANCELED',pfd_sendstatus='待上传' where PFD_THROWSTATUS='已确认' ");
		return "当前MRP投放的采购预测已经作废，可以重新投放采购预测单了";
	}

	// 装载销售排程
	@Override
	public void loadSaleDetailDet(int keyValue, String type, String caller,
			String data, String condition) {
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> insertSqls = new ArrayList<String>();
		if ("MPS".equals(type)) {
			Object value = baseDao.getFieldDataByCondition("MpsDetail",
					"max(md_detno)", "md_mainid=" + keyValue);
			Object mainCode = baseDao.getFieldDataByCondition("MpsMain",
					"mm_code", "mm_id=" + keyValue);
			int count = value == null ? 1
					: Integer.parseInt(value.toString()) + 1;
			if (condition != null && !condition.equals("")
					&& condition.length() > 5) {

				baseDao.execute("insert into mpsdetail(md_id,md_mainid,md_detno,md_maincode,md_qty,md_date,md_prodcode,md_source,md_sourcedate,md_sourceqty,md_sourcecode,md_sourcedetno,md_orderkind,md_ordercode,md_orderdetno,md_cop,md_bomid,md_sdid,md_bonded)"
						+ "select MPSDETAIL_SEQ.nextval,"
						+ keyValue
						+ ","
						+ count
						+ "-1+rownum,'"
						+ mainCode
						+ "',sdd_qty,sdd_delivery,sd_prodcode,'SALEDETAILDET',sdd_delivery,sdd_qty,sa_code,sd_detno,'SALE',sa_code,sd_detno,sa_cop,sd_bomid,sdd_sdid,sd_bonded from sale,salekind,saledetail,saledetaildet,product where sa_kind=sk_name and sk_mrp<>0 and sa_id=sd_said and sd_id=sdd_sdid and pr_code=sd_prodcode and sa_statuscode in('AUDITED','FREEZE') and sd_qty>NVL(sd_sendqty,0) and NVL(sd_statuscode,' ')<>'FINISH' and "
						+ condition);
			} else {
				for (Map<Object, Object> m : datas) {
					map.put("md_id", baseDao.getSeqId("MPSDETAIL_SEQ"));
					map.put("md_mainid", keyValue);
					map.put("md_detno", count);
					map.put("md_maincode", mainCode);
					map.put("md_qty", m.get("sdd_qty"));
					map.put("md_date", m.get("sdd_delivery"));
					map.put("md_prodcode", m.get("sd_prodcode"));
					map.put("md_source", "SALEDETAILDET");
					map.put("md_sourcedate", m.get("sdd_delivery"));
					map.put("md_sourceqty", m.get("sdd_qty"));
					map.put("md_sourcecode", m.get("sa_code"));
					map.put("md_sourcedetno", m.get("sd_detno"));
					map.put("md_orderkind", "SALE");
					map.put("md_ordercode", m.get("sa_code"));
					map.put("md_orderdetno", m.get("sd_detno"));
					map.put("md_cop", m.get("sa_cop"));
					map.put("md_bomid", m.get("sd_bomid"));
					map.put("md_sdid", m.get("sdd_sdid"));
					insertSqls.add(SqlUtil.getInsertSqlByMap(map, "MpsDetail"));
					count++;
				}
			}
		} else if ("APS".equals(type)) {
			Object value = baseDao.getFieldDataByCondition("ApsDetail",
					"max(ad_detno)", "ad_amid=" + keyValue);
			Object mainCode = baseDao.getFieldDataByCondition("ApsMain",
					"am_code", "am_id=" + keyValue);
			int count = value == null ? 1
					: Integer.parseInt(value.toString()) + 1;
			for (Map<Object, Object> m : datas) {
				map.put("ad_id", baseDao.getSeqId("APSDETAIL_SEQ"));
				map.put("ad_amid", keyValue);
				map.put("ad_detno", count);
				map.put("ad_code", mainCode);
				map.put("ad_qty", m.get("sdd_qty"));
				map.put("ad_date", m.get("sdd_delivery"));
				map.put("ad_prodcode", m.get("sd_prodcode"));
				map.put("ad_source", "SALEDETAILDET");
				map.put("ad_sourceqty", m.get("sd_qty"));
				map.put("ad_orderkind", "SALEDETAILDET");
				map.put("ad_sacode", m.get("sa_code"));
				map.put("ad_sadetno", m.get("sdd_detno"));
				map.put("ad_cop", m.get("sa_cop"));
				map.put("ad_bomid", m.get("sd_bomid"));
				map.put("ad_sddid", m.get("sdd_id"));
				insertSqls.add(SqlUtil.getInsertSqlByMap(map, "ApsDetail"));
				count++;
			}

		}
		baseDao.execute(insertSqls);

	}

	@Override
	public String getSum(String fields, String caller, String condition) {
		String querySql = " ";
		Map<String, Object> map = new HashMap<String, Object>();
		String str[] = {};
		if (fields != null) {
			str = fields.split("#");
		}
		if (caller.equals("Desk!MPSPRonorder")) {
			querySql = " select round(sum(qty)) from ((SELECT pd_code AS code, pd_detno AS detno,(pd_qty - nvl(pd_acceptqty,0)) AS qty, pu_vendname AS cust, pd_delivery AS deliverydate, '采购单' AS kind, pd_prodcode AS pcode,pd_factory factory FROM PurchaseDetail LEFT JOIN Purchase ON pu_id = pd_puid WHERE pd_qty>nvl(pd_acceptqty,0) and pu_statuscode in ('ENTERING','COMMITED','AUDITED')and NVL(pd_mrpstatuscode, ' ') not in ('FREEZE','已冻结','FINISH','已结案','已作废','NULLIFIED')  )UNION (SELECT ma_code AS code,  0 AS detno, (ma_qty - nvl(ma_madeqty,0)) AS qty, ma_custname AS cust, ma_plandelivery AS deliverydate, '制造单' AS kind, ma_prodcode AS pcode,ma_factory factory FROM  make WHERE  ma_statuscode ='AUDITED' and nvl(ma_madeqty,0)<ma_qty )UNION (SELECT ap_code AS code, ad_detno AS detno, ad_qty - (CASE WHEN nvl(ad_yqty, 0) > ad_qty THEN ad_qty ELSE nvl(ad_yqty, 0) END) AS qty,'' AS cust, ad_delivery AS deliverydate, '请购单' AS kind, ad_prodcode AS pcode,ad_factory factory FROM application,applicationdetail WHERE ad_apid=ap_id and ap_statuscode='AUDITED' and ad_qty>nvl(ad_yqty,0) AND NVL(ad_statuscode, ' ') not in ('FINISH','已结案','已作废','NULLIFIED')))";
			if (!"".equals(condition) && condition != null) {
				querySql += " WHERE " + condition;
			}
		} else {
			if (fields != null) {
				DataList datalsit = dataListDao.getDataList(caller,
						SpObserver.getSp());
				String table = datalsit.getDl_tablename();
				String basecondition = datalsit.getDl_condition();
				querySql = " SELECT ";
				for (int i = 0; i < str.length; i++) {
					querySql += " nvl(sum(" + str[i] + "),0)";
					if (i < str.length - 1)
						querySql += ",";
				}
				querySql += " from " + table;
				if (condition != null && !condition.equals(""))
					querySql += "  where " + condition;
				if (basecondition != null && querySql.contains("where")
						&& !basecondition.equals(""))
					querySql += " AND " + basecondition;
				else if (basecondition != null && !querySql.contains("where")
						&& !basecondition.equals(""))
					querySql += " where " + basecondition;
			}
		}
		SqlRowList sl = baseDao.queryForRowSet(querySql);
		if (sl.next()) {

			for (int i = 0; i < str.length; i++) {
				map.put(str[i], (int) sl.getFloat(i + 1));
			}
		}
		return BaseUtil.parseMap2Str(map);
	}

	@SuppressWarnings("unused")
	private String getFullTableName(String masterCode, DataList datalist) {
		String[] strs = datalist.getDl_tablename().split("left join ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = strs.length; i < len; i++) {
			sb.append(masterCode).append(".").append(strs[i]);
			if (i != len - 1)
				sb.append("left join ");
		}
		return sb.toString();
	}

	private String ThrowSupply(String MainCode, String sql, String caller) {
		int Purcount = 0;
		int idx = -1;
		int puid = 0;
		String pucode = "";
		String vendcop = "", needcop = "";
		SqlMap map = null;
		idx = sql.indexOf("order by");
		if (idx > 0) {
			sql = sql.substring(0, idx);
		}
		sql = sql
				+ " and md_worksource='内部交易' order by md_needcop,md_cop,md_prodcode,md_id";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			if (null == rs.getString("md_cop")
					|| rs.getString("md_cop").equals("")) {
				continue;
			}
			if (rs.getString("md_needcop") == null
					|| rs.getString("md_needcop").equals("")) {
				continue;
			}
			vendcop = rs.getString("md_cop");
			if (!needcop.equals(rs.getString("md_needcop"))
					|| !vendcop.equals(rs.getString("md_cop"))) {
				needcop = rs.getString("md_needcop");
				vendcop = rs.getString("md_cop");
				Purcount = 1;
				Map<Object, Object> tMap = new HashMap<Object, Object>();
				puid = baseDao.getSeqId("PURCHASE_SEQ");
				pucode = baseDao.sGetMaxNumber("Purchase", 2);
				tMap.put("pu_id", puid);
				tMap.put("pu_code", pucode);
				tMap.put("pu_indate", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				tMap.put("pu_status", BaseUtil.getLocalMessage("ENTERING"));
				tMap.put("pu_statuscode", "ENTERING");
				tMap.put("pu_recordman", SystemSession.getUser().getEm_name());
				tMap.put("pu_recordid", SystemSession.getUser().getEm_id());
				tMap.put("pu_delivery", DateUtil.parseDateToString(new Date(),
						Constant.YMD_HMS));
				tMap.put("pu_kind", "MRP");
				tMap.put("pu_source", "MRP");
				tMap.put("pu_vendid", null);
				tMap.put("pu_vendname", vendcop);
				tMap.put("pu_vendcode", vendcop);
				baseDao.execute(SqlUtil.getInsertSqlByMap(tMap, "purchase"));
			}
			map = new SqlMap("purchasedetail");
			map.set("pd_id", baseDao.getSeqId("PURCHASEDETAIL_SEQ"));
			map.set("pd_puid", puid);
			map.set("pd_code", pucode);
			map.set("pd_detno", Purcount++);
			map.set("pd_prodcode", rs.getObject("md_prodcode"));
			map.set("pd_vendid", null);
			map.set("pd_vendcode", null);
			map.set("pd_vendname", null);
			map.set("pd_qty", rs.getObject("md_changeqty"));
			map.set("pd_price", 0);
			map.set("pd_delivery", rs.getObject("md_needdate"));
			map.set("pd_source", "MRP");
			map.set("pd_sourcecode", MainCode);
			map.set("pd_sourcedetail", 0);
			map.set("pd_remark", rs.getString("md_topprodcode"));
			map.set("pd_apremark", rs.getString("md_id"));
			map.execute();
			baseDao.updateByCondition("mrpdata",
					" md_statuscode='THROWED',md_status='已投放'",
					"md_id=" + rs.getString("md_id"));
		}
		return "";
	}

	@Override
	public String turnSupplyToNeed(String caller, String gridstore,
			String maincode) {
		String idstr = "";
		List<Map<Object, Object>> NeedStore = BaseUtil
				.parseGridStoreToMaps(gridstore);
		for (int i = 0; i < NeedStore.size(); i++) {
			idstr += "," + NeedStore.get(i).get("md_id");
		}
		if (idstr == null || idstr.equals("")) {
			BaseUtil.showError("请打勾选择明细行!");
			return "请打勾选择明细行";
		} else {
			idstr = idstr.substring(1);
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select md_id,md_prodcode from mrpdata where  md_id in ("
						+ idstr + ") and md_statuscode='THROWED' ");
		if (rs.next()) {
			BaseUtil.showError("已经投放的供应明细不能转需求!");
			return "已经投放的供应明细不能转需求";
		}
		rs = baseDao
				.queryForRowSet("select md_id,md_prodcode from mrpdata where  md_id in ("
						+ idstr + ") and NVL(md_worksource,' ')<>'内部交易' ");
		if (rs.next()) {
			BaseUtil.showError("不属于内部交易的供应不能转需求!");
			return "不属于内部交易的供应不能转需求";
		}
		String sql = "update mrpdata set md_kind='NEED' where md_id in ("
				+ idstr + ") ";
		baseDao.execute(sql);
		return "";
	}

	@Override
	public String getMaxMcode(String caller, String code) {
		String mcode = "";
		String sql = "select max(mm_id) mm_id from mpsmain where mm_id < '"
				+ code + "'";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			mcode = rs.getString("mm_id");
		}
		return mcode;
	}

	@Override
	public List<Map<String, Object>> getGridData(String caller, String condition) {
		String[] arr = condition.split(",");
		String sql = "select md_prodcode,pr_detail,pr_spec,md_ordercode,md_orderdetno,md_orderkind,diffqty,changekind,qty1,qty2 from "
				+ " (select NVL(md_ordercode,code)md_ordercode,NVL(md_orderdetno,detno)md_orderdetno,NVL(md_prodcode,prcode)md_prodcode,NVL(md_orderkind,kind)md_orderkind,NVL(qty1,0)-NVL(qty2,0)diffqty,case when qty1>NVL(qty2,0) then '增加' else '减少' end changekind,qty1,qty2"
				+ " from  (select md_ordercode,md_orderdetno,md_prodcode,md_orderkind,sum(md_qty)qty1 from mpsdetail"
				+ " where md_mainid='"
				+ arr[0]
				+ "' group by  md_ordercode,md_orderdetno,md_prodcode,md_orderkind) T1"
				+ " full join  (select  md_ordercode code,md_orderdetno detno,md_prodcode prcode,md_orderkind kind,sum(md_qty)qty2 from  MPSDetail"
				+ " where md_mainid='"
				+ arr[1]
				+ "' group by  md_ordercode,md_orderdetno,md_prodcode,md_orderkind) T2 "
				+ " on T1.md_ordercode=T2.code and T1.Md_Orderdetno=T2.detno and T1.Md_Prodcode=T2.prcode and T1.md_orderkind=T2.kind"
				+ " where NVL(qty1,0)<>NVL(qty2,0)) TT"
				+ " left join Product ON TT.md_prodcode=Product.pr_code order by diffqty";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			return rs.getResultList();
		}
		return null;
	}

	/*
	 * 提交，审核之前的限制
	 */
	private void check(Object mm_id) {
		// 判断订单编号+订单序号与物料编号是否对应，不对应则不能提交
		//判断是否为集团MRP
		SqlRowList rs = baseDao.queryForRowSet(" select column_value from table(PARSESTRING((select mm_coplist from mpsmain where mm_id=?), '#')) where column_value is not null",mm_id);
		if(rs.hasNext()){
			String master;
			while(rs.next()){
				master = rs.getString("column_value");
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(md_detno) from MpsDetail where md_mainid=? and md_master=? and nvl(md_ordercode,' ')<>' ' and md_orderdetno>0  "
										+ " AND not exists(select 1 from "+master+".sale,"+master+".saledetail where sa_code=md_ordercode and sa_id=sd_said and sd_detno=md_orderdetno and sd_prodcode=md_prodcode)"
										+ " AND not exists(select 1 from "+master+".saleforecast,"+master+".SaleForecastDetail where sf_code=md_ordercode and sf_id=sd_sfid and sd_detno=md_orderdetno and sd_prodcode=md_prodcode)",
								String.class, mm_id,master);
				if (dets != null) {
					BaseUtil.showError("订单编号+订单序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
				}
			}
		}else{
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(md_detno) from MpsDetail where md_mainid=? and nvl(md_ordercode,' ')<>' ' and md_orderdetno>0  "
									+ " AND not exists(select 1 from sale,saledetail where sa_code=md_ordercode and sa_id=sd_said and sd_detno=md_orderdetno and sd_prodcode=md_prodcode)"
									+ " AND not exists(select 1 from saleforecast,SaleForecastDetail where sf_code=md_ordercode and sf_id=sd_sfid and sd_detno=md_orderdetno and sd_prodcode=md_prodcode)",
							String.class, mm_id);
			if (dets != null) {
				BaseUtil.showError("订单编号+订单序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
			}
		}
		
	}

	// 库存运算及上架
	@Override
	public String RunMrpAndGoods(String code, String caller) {
		List<String> result = baseDao.callProcedureWithOut(
				"MM_RUNMRP_DEADSTOCK", new Object[] { "",
						SystemSession.getUser().getEm_name() }, new Integer[] {
						1, 2 }, new Integer[] { 3, 4 });
		if (result.get(1) != null) {
			// 运算失败
			BaseUtil.showError(result.get(1));
		} else {
			// 自动匹配
			try {
				getUUId(result.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 判断是否启用了自动下架，是则执行以下方法
			if (baseDao.isDBSetting("B2CSetting", "autoOfftoB2C")){
				autoOffgoods(result.get(0));
			}
			if (baseDao.isDBSetting("B2CSetting", "autoUptoB2C")) {
				autoUpgoods(result.get(0));
			}
			return result.get(0);
		}
		return null;
	}

	@Override
	@Transactional
	public String TurnGoodsUp(String caller, String mainCode, String gridStore,
			String toCode) {
		int mainid = 0;
		String NewCode = null;
		String sql = "";
		List<Map<Object, Object>> NeedStore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		if(!CollectionUtil.isEmpty(NeedStore)){
			if(NeedStore.get(0).get("gu_currency") == null || "".equals(NeedStore.get(0).get("gu_currency"))){
				BaseUtil.showError("请选择上架币别");
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < NeedStore.size(); i++) {
			sb.append(NeedStore.get(i).get("mdd_id") + ",");
		}
		String range = sb.toString().substring(0, sb.toString().length() - 1)
				+ ")";
		String idcondition = " and mdd_id in " + range;
		sql = "Select pr_code,pr_id,pr_detail,pr_spec,pr_unit,pr_manutype,pr_specvalue,pr_refno,pr_leadtime,pr_zxbzs,pr_zxdhl,pr_plzl from mrpdataonhanddeal left join product on pr_code=mdd_prodcode  where 1=1 "
				+ idcondition
				+ " and pr_statuscode='AUDITED' and mdd_qty>0 and mdd_action='UP' ";
		// 以下开始转上架单
		SqlRowList rs = baseDao.queryForRowSet(sql);
		int detno = 1;
		if (!rs.hasNext()) {
			BaseUtil.showError("选择的物料未审核！");
		}
		if (NewCode == null) {
			// 区分新生成上架申请单，还是追加到上架申请单中
			if (toCode != null && !toCode.equals("")) {
				SqlRowList cl = baseDao
						.queryForRowSet("select * from GoodsUp where gu_code='"
								+ toCode + "'");
				if (!cl.hasNext()) {
					BaseUtil.showError("指定的上架申请单号不存在");
				}
				while (cl.next()) {
					// 在录入状态才允许追加
					if (!"ENTERING".equals(cl.getString("gu_statuscode"))) {
						BaseUtil.showError("只能追加到在录入的申请单");
					}
				}
				NewCode = toCode;
				mainid = cl.getInt("gu_id");
				SqlRowList rs1 = baseDao
						.queryForRowSet("select nvl(max(gd_detno),0) from Goodsdetail where gd_guid="
								+ mainid);
				if (rs1.next()) {
					detno += rs1.getInt(1);
				}
			} else {
				NewCode = baseDao.sGetMaxNumber("GoodsUpApplication", 2);
				mainid = baseDao.getSeqId("GoodsUp_SEQ");
				baseDao.execute("insert into GoodsUp (gu_id,gu_code,gu_indate,gu_inman,gu_status,gu_statuscode,gu_mrpcode,gu_currency)"
						+ "values("
						+ mainid
						+ ",'"
						+ NewCode
						+ "',sysdate,'"
						+ SystemSession.getUser().getEm_name()
						+ "','"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "','"
						+ "ENTERING','"
						+ mainCode
						+ "','"
						+ NeedStore.get(0).get("gu_currency") + "')");
			}
		}
		// 判断上架数量是否填写，并且大于0，小于建议上架数量
		for (Map<Object, Object> need : NeedStore) {
			rs = baseDao
					.queryForRowSet("select mdd_id,mdd_uuid,mdd_whcode,mdd_whname,pr_zxbzs,pr_code,mdd_purcdate,go_erpunit from mrpdataonhanddeal left join product on pr_code=mdd_prodcode left join B2C$GOODSONHAND on go_uuid=mdd_uuid and go_prodcode=mdd_prodcode where mdd_id="
							+ need.get("mdd_id"));
			if (rs.next()) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("gd_id", baseDao.getSeqId("GOODSDETAIL_SEQ"));
				map.put("gd_guid", mainid);
				map.put("gd_detno", detno);
				map.put("gd_prodcode", rs.getString("pr_code"));
				map.put("gd_qty", need.get("mdd_upqty"));
				map.put("gd_mdid", rs.getString("mdd_id"));
				map.put("gd_minpackqty", rs.getDouble("pr_zxbzs"));
				map.put("gd_uuid", rs.getString("pr_uuid"));
				map.put("gd_ismallsale",need.get("mdd_ismallsale"));
				if (need.get("mdd_throwkind").equals("现货")) {
					map.put("gd_original", 1311);
					map.put("gd_salekind", '1');
				} else {
					map.put("gd_original", 1312);
					map.put("gd_salekind", '2');
				}
				if(NeedStore.get(0).get("gu_currency").equals("RMB")){//RMB默认税率17%
					map.put("gd_price", need.get("mdd_price"));
					map.put("gd_taxrate", 17);
				}else{//美金无税率
					map.put("gd_usdprice", need.get("mdd_price"));
					map.put("gd_taxrate", "");
				}
				map.put("gd_whcode", rs.getObject("mdd_whcode"));
				map.put("gd_whname", rs.getObject("mdd_whname"));
				
				map.put("gd_madedate", rs.getObject("mdd_purcdate"));
				map.put("gd_erpunit", rs.getObject("go_erpunit"));
				map.put("gd_remain", need.get("mdd_upqty"));
				baseDao.execute(SqlUtil.getInsertSqlByMap(map, "goodsdetail"));
				baseDao.updateByCondition("mrpdataonhanddeal",
						"mdd_status='已投放',mdd_togucode='" + NewCode
								+ "',mdd_lockqty=" + need.get("po_lockqty"),
						"mdd_id=" + rs.getString("mdd_id"));
				// 更新锁库数量
				detno++;
			}
		}
		String log = "转入成功,上架单号:"
				+ "<a href=\"javascript:openUrl('jsps/b2c/sale/goodsUpApplication.jsp?formCondition=gu_idIS"
				+ mainid + "&gridCondition=gd_guidIS" + mainid + "')\">"
				+ NewCode + "</a>&nbsp;";
		return log;
	}

	private void getUUId(String code) {
		try {
			List<String> oriCodes = baseDao
					.getJdbcTemplate()
					.queryForList(
							"select distinct pr_orispeccode from mrpdataonhanddeal left join product on pr_code=mdd_prodcode where mdd_mpscode=? and nvl(mdd_uuid,'0')='0' and nvl(pr_orispeccode,' ')<>' '",
							String.class, code);
			List<ComponentInfoUas> componentInfoUas = b2cComponentService
					.findByCode(oriCodes,SystemSession.getUser().getCurrentMaster());
			if (!CollectionUtil.isEmpty(componentInfoUas)) {
				List<String> sqls = new ArrayList<String>();
				// 按照分组code 进行分组
				Map<Object, List<ComponentInfoUas>> set = new HashMap<Object, List<ComponentInfoUas>>();
				List<ComponentInfoUas> list = null;
				for (ComponentInfoUas componentInfo : componentInfoUas) {
					String key = componentInfo.getCode();
					if (StringUtil.hasText(key) && set.containsKey(key)) {
						list = set.get(key);
					} else {
						list = new ArrayList<ComponentInfoUas>();
					}
					list.add(componentInfo);
					set.put(key, list);
				}
				String unit ="", erpunit="";
				for (Map.Entry<Object, List<ComponentInfoUas>> entry : set
						.entrySet()) {
					if (entry.getValue().size() > 1) {// 一个原厂型号多个uuid
					} else {
						SqlRowList rs = baseDao.queryForRowSet("select distinct pr_id,pr_unit,pr_code from mrpdataonhanddeal left join product on pr_code=mdd_prodcode where mdd_mpscode=?  and pr_orispeccode=? and nvl(mdd_uuid,'0')='0' ",code,entry.getKey());
						while (rs.next()){
							sqls.add("update product set pr_uuid='"+entry.getValue().get(0).getUuid()+"' where pr_id="+rs.getLong("pr_id"));
							unit = entry.getValue().get(0).getUnit();
							erpunit = productBatchUUIdService.getUASUnit(unit, rs.getString("pr_unit"));
							//自动产生平台物料信息表B2C$GOODSONHAND,写入商城标准单位，和ERP 单位
							sqls.add("insert into b2c$goodsonhand(go_uuid,go_id,go_unit,go_erpunit,go_prodcode,go_code) select '"+entry.getValue().get(0).getUuid()+"',b2c$goodsonhand_seq.nextval,'"+unit+"','"+erpunit+"','"+rs.getString("pr_code")+"','"+entry.getValue().get(0).getCode()+"' from dual where not exists(select 1 from b2c$goodsonhand where go_prodcode='"+rs.getString("pr_code")+"') ");
						}
					}
				}
				baseDao.execute(sqls);
				baseDao.execute("update mrpdataonhanddeal set mdd_uuid=(select pr_uuid from product where pr_code=mdd_prodcode) where mdd_mpscode='"
						+ code
						+ "' and exists (select 1 from product where pr_code=mdd_prodcode)");
			}
		} catch (EmptyResultDataAccessException e) {

		}
	}

	@Override
	public void updatePoLockqty(String caller, String data) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		if (map.get("po_lockqty") != null
				&& Double.valueOf(map.get("po_lockqty").toString())
						.doubleValue() > 0) {
			baseDao.updateByCondition("productonhand",
					"po_lockqty='" + map.get("po_lockqty") + "'",
					"po_prodcode='" + map.get("mdd_prodcode") + "'");
			baseDao.logger.others("上架修改锁库数量", "物料：" + map.get("mdd_prodcode")
					+ "修改锁库数量[" + map.get("po_lockqty") + "]成功，", caller,
					"mdd_id", map.get("mdd_id"));
			;
		} else {
			BaseUtil.showError("锁库数量必须大于0");
		}
	}

	@Override
	public String TurnDeviceInApply(String caller, String gridStore) {
		List<Map<Object, Object>> store = BaseUtil
				.parseGridStoreToMaps(gridStore);
		StringBuffer sb = new StringBuffer();
		String ids = CollectionUtil.pluckSqlString(store, "mdd_id");
		SqlRowList rs = baseDao
				.queryForRowSet("select mdd_prodcode,pr_orispeccode,pr_uuid from MRPDataOnHandDeal left join product on pr_code=mdd_prodcode where mdd_id in ("
						+ ids + ") ");
		while (rs.next()) {
			if (!StringUtil.hasText(rs.getObject("pr_orispeccode"))) {
				sb.append("物料编号[" + rs.getObject("mdd_prodcode")
						+ "]的原厂型号为空<br>");
			} else {
				if (StringUtil.hasText(rs.getObject("pr_uuid"))) {
					sb.append("物料编号[" + rs.getObject("mdd_prodcode")
							+ "]已有标准料号[" + rs.getObject("pr_uuid") + "]<br>");
				}
			}
			SqlRowList rs1 = baseDao.queryForRowSet(
					"select de_code from DEVICEINAPPLY where de_prodcode=?",
					rs.getObject("mdd_prodcode"));
			if (rs1.next()) {
				sb.append("物料编号[" + rs.getObject("mdd_prodcode")
						+ "]的已存在ERP器件入库申请单[" + rs1.getObject("de_code")
						+ "]，不允许重复转！<br>");
			}
		}
		rs = baseDao
				.queryForRowSet("select mdd_id from MRPDataOnHandDeal left join product on pr_code=mdd_prodcode where mdd_id in ("
						+ ids
						+ ") and nvl(pr_orispeccode,' ')<>' ' and nvl(pr_uuid,' ')=' ' and not exists (select 1 from DEVICEINAPPLY where mdd_prodcode=de_prodcode)");
		while (rs.next()) {
			baseDao.execute("insert into DEVICEINAPPLY(de_id,de_code,de_date,de_indate,de_statuscode,de_status,de_recorder,DE_PRODCODE,DE_OLDSPEC,DE_BRAND)"
					+ " select DEVICEINAPPLY_SEQ.NEXTVAL,'"
					+ baseDao.sGetMaxNumber("DeviceInApply", 2)
					+ "',sysdate,sysdate,'ENTERING','"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "','"
					+ SystemSession.getUser().getEm_name()
					+ "',mdd_prodcode,pr_spec,pr_brand from MRPDataOnHandDeal left join product on pr_code=mdd_prodcode where mdd_id="
					+ rs.getGeneralInt("mdd_id"));
		}
		if (sb.length() > 0) {
			return sb.toString();
		} else {
			return "处理成功";
		}
	}

	private void autoOffgoods(String mpscode) { 
			String caller = "GoodsOff";
			// 自动生成下架单并审核
			String code = null;
			int id;
			int count;
			count = baseDao.getCount("select count(1) from mrpdataonhanddeal where mdd_mpscode='"
					+ mpscode + "' and mdd_action='OFF' and mdd_qty>0");
			if (count==0){
				return;//没有需要下架的
			}
			code = baseDao.sGetMaxNumber("GoodsOff", 2);
			id = baseDao.getSeqId("GOODSCHANGE_SEQ"); 
			// 转入主表
			baseDao.execute("insert into GoodsChange(gc_id,gc_code,gc_indate,gc_inman,gc_status,"
					+ "gc_statuscode,gc_whcode,gc_type,gc_mrpcode)"
					+ "values("
					+ id
					+ ",'"
					+ code
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ BaseUtil.getLocalMessage("COMMITED")
					+ "','COMMITED','"
					+ "" + "','下架','"+mpscode+"')");
			baseDao.execute("update goodschange set gc_whname=(select wh_description from warehouse where wh_code=gc_whcode) where gc_id="
					+ id);
			//明细表添加数据
			baseDao.execute("insert into goodschangedetail(gcd_id,gcd_gcid,gcd_detno,gcd_prodcode,gcd_mdid,gcd_barcode,gcd_offqty,gcd_whcode,gcd_whname,gcd_uuid,gcd_b2bbatchcode) select goodschangedetail_seq.nextval,"
					+ id
					+ ",rownum,mdd_prodcode,mdd_id,mdd_barcode,mdd_qty,mdd_whcode,mdd_whname,mdd_uuid,(select max(gd_b2bbatchcode) from goodsdetail where gd_barcode=mdd_barcode) from mrpdataonhanddeal where mdd_mpscode='"
					+ mpscode + "' and mdd_action='OFF' and mdd_qty>0");
			//更新已投放
			baseDao.execute("update mrpdataonhanddeal set mdd_status='已投放',mdd_togucode='"+code+"' where mdd_mpscode=? and exists(select 1 from goodschangedetail where gcd_gcid=? and gcd_mdid=mdd_id)",mpscode,id);
			String str = "自动下架，单号:"
					+ "<a href=\"javascript:openUrl('jsps/pm/mps/goodsChange.jsp?formCondition=gc_idIS"
					+ id + "&gridCondition=gcd_gcidIS" + id
					+ "&whoami=GoodsOff')\">" + code + "</a>&nbsp;";
			try{
				goodsChangeService.auditGoodsChange(id, caller);
				BaseUtil.appendError(str+",审核通过");
			}catch(Exception e){
				BaseUtil.appendError(str+",审核未通过");
			}
	}

	private void autoUpgoods(String mpscode) {
		String caller = "GoodsUpApplication";
		// 自动生成上架单并审核
		String code = null;
		int id;

		// 如果明细行没数据则不生成上架单
		int count = baseDao
				.getCount("select count(1) from MRPDataOnHandDeal left join product on pr_code=mdd_prodcode where mdd_mpscode='"
						+ mpscode
						+ "' and mdd_action='UP' and pr_statuscode='AUDITED' and mdd_qty>0 and mdd_uuid is not null and  mdd_price>0");
		if (count == 0) {
			return;
		}

		code = baseDao.sGetMaxNumber("GoodsUpApplication", 2);
		id = baseDao.getSeqId("GoodsUp_SEQ");
		String currency = baseDao.getDBSetting("defaultCurrency");
		Object taxrate = baseDao.getFieldDataByCondition("currencys",
				"cr_taxrate", "cr_name='" + currency + "'");
		// 转入主表
		baseDao.execute("insert into GoodsUp (gu_id,gu_code,gu_indate,gu_inman,gu_status,gu_statuscode,gu_mrpcode,gu_currency)"
				+ "values("
				+ id
				+ ",'"
				+ code
				+ "',sysdate,'"
				+ SystemSession.getUser().getEm_name()
				+ "','"
				+ BaseUtil.getLocalMessage("COMMITED")
				+ "','"
				+ "COMMITED','"
				+ mpscode + "','" + currency + "')");
		// 明细表添加数据
		baseDao.execute("insert into goodsdetail(gd_id,gd_guid,gd_detno,gd_prodcode,gd_qty,gd_mdid,gd_minpackqty,gd_uuid,gd_original,gd_salekind,gd_price,gd_whcode,gd_whname,gd_madedate,gd_remain,gd_minbuyqty,gd_erpunit,gd_taxrate) select goodsdetail_seq.nextval,"
				+ id
				+ ",rownum,mdd_prodcode,mdd_qty,mdd_id,pr_zxbzs,mdd_uuid,1311,1,mdd_price,mdd_whcode,mdd_whname,mdd_purcdate,mdd_qty,pr_zxdhl,go_erpunit,"
				+ taxrate.toString()
				+ " from MRPDataOnHandDeal left join product on pr_code=mdd_prodcode left join B2C$GOODSONHAND on go_uuid=mdd_uuid and go_prodcode=mdd_prodcode where mdd_mpscode='"
				+ mpscode
				+ "' and mdd_action='UP' and pr_statuscode='AUDITED' and mdd_qty>0 and mdd_uuid is not null and  mdd_price>0");
		//更新已投放
		baseDao.execute("update mrpdataonhanddeal set mdd_status='已投放',mdd_togucode='"+code+"' where mdd_mpscode=? and exists(select 1 from goodsdetail where gd_guid=? and gd_mdid=mdd_id)",mpscode,id);
		String str = "转入成功,上架单号:"
				+ "<a href=\"javascript:openUrl('jsps/pm/mps/goodsUpApplication.jsp?formCondition=gu_idIS"
				+ id + "&gridCondition=gd_guidIS" + id + "')\">" + code
				+ "</a>&nbsp;";
		try {
			goodsUpApplicationService.auditGoodsUpApplication(id, caller);
			BaseUtil.appendError(str + ",审核通过");
		} catch (Exception e) {
			BaseUtil.appendError(str + ",审核未通过");
		}
	}

	@Override
	public List<Map<String, Object>> getSeriousWarn(String caller, String code) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count1 = baseDao.getCount("select count(1) from mrpmessage where mm_mpscode='"+code+"' and mm_kind = '有需求的物料有拨入单未过账'");
		if(count1 > 0){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", "发现物料库存调拨过账状态不一致");
			map.put("count", count1);
			map.put("id", "error1");
			list.add(map);
		}
		int count2 = baseDao.getCount("select count(1) from mrpmessage where mm_mpscode='"+code+"' and mm_kind in ('委外单未审核' ,'制造单未审核')");
		if(count2 > 0){
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("name", "发现有需求的MPS件工单未审核");
			map2.put("count", count2);
			map2.put("id", "error2");
			list.add(map2);
		}
		return list;
	}

	@Override
	public String throwCancle(String gridStore) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(gridStore);
		String ids = "";
		for(Map<Object,Object> map : store){
			ids = ids + "," + map.get("md_id").toString();
		}
		String log = null;
		StringBuffer sb = new StringBuffer();
		if (!ids.equals("")) {
			ids = ids.substring(1);
			List<Map<String, Object>> list = baseDao.queryForList("select * from mrpdata where md_id in ("+ids+") and nvl(md_status,' ')='未投放' and nvl(md_model,' ')='取消' ");
			//先根据md_worktype分组
			Map<Object, List<Map<String, Object>>> groupByType = groupMap(list, "md_worktype");
			Set<Object> mapSetType = groupByType.keySet();
			List<String> sqls = new ArrayList<String>();
			List<Map<String, Object>> itemsByType;
			for(Object s : mapSetType){
				//如果md_worktype='采购单'，则根据单号分组，生成变更单
				if("采购单".equals(s)){
					List<Map<String, Object>> itemsByCode;
					itemsByType = groupByType.get(s);
					Map<Object, List<Map<String, Object>>> groupByCode  = groupMap(itemsByType, "md_ordercode");
					Set<Object> mapSetCode = groupByCode.keySet();
					for(Object pu_code : mapSetCode){
						int pcd_detno = 1;
						itemsByCode = groupByCode.get(pu_code);
						int pc_id = baseDao.getSeqId("PURCHASECHANGE_SEQ");
						String pc_code = baseDao.sGetMaxNumber("PURCHASECHANGE", 2);
						String sql = "insert into purchasechange(pc_id,pc_code,pc_purccode,pc_status,pc_recorder,pc_indate,pc_purcid,pc_statuscode,pc_apvendcode,pc_apvendname,pc_newapvendcode,pc_newapvendname,pc_paymentscode,pc_payments,pc_newpaymentscode,pc_newpayments,pc_currency,pc_rate,pc_newcurrency,pc_newrate) select "+pc_id+","
								+ " '"+pc_code+"', '"+pu_code+"','在录入','"+SystemSession.getUser().getEm_name()+"',sysdate,pu_id,'ENTERING',pu_receivecode,pu_receivename,pu_receivecode,pu_receivename,pu_paymentscode,pu_payments,pu_paymentscode,pu_payments,pu_currency,pu_rate,pu_currency,pu_rate from purchase where pu_code='"+pu_code+"'";
						sqls.add(sql);
						for(Map<String,Object> map : itemsByCode){
							int pcd_id = baseDao.getSeqId("PURCHASECHANGEDETAIL_SEQ");
							Object md_changeqty = map.get("md_changeqty");
							String sqlDetail = "insert into purchasechangedetail(pcd_id,pcd_pcid,pcd_detno,pcd_pddetno,pcd_prodcode,pcd_newprodcode,pcd_oldqty,pcd_newqty,pcd_olddelivery,pcd_newdelivery,pcd_oldprice,pcd_newprice,pcd_taxrate,pcd_newtaxrate,pcd_factory,pcd_newfactory,pcd_oldsellercode,pcd_oldseller,pcd_newsellercode,pcd_newseller,pcd_bgprice,pcd_newbgprice,pcd_oldpurcqty,pcd_newpurcqty,pcd_oldnetprice,pcd_newnetprice) select "+pcd_id+","
									+ " "+pc_id+","+pcd_detno+",pd_detno,pd_prodcode,pd_prodcode,pd_qty,pd_qty+("+md_changeqty+"),pd_delivery,pd_delivery,pd_price,pd_price,pd_rate,pd_rate,pd_factory,pd_factory,pd_sellercode,pd_seller,pd_sellercode,pd_seller,pd_bgprice,pd_bgprice,pd_purcqty,pd_purcqty,pd_netprice,pd_netprice from purchasedetail where pd_code='"+pu_code+"' and pd_detno="+map.get("md_orderdetno")+" ";
							sqls.add(sqlDetail);
							pcd_detno++;
						}
						log = "转入变更单成功,变更单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/purchaseChange.jsp?formCondition=pc_idIS" + pc_id
								+ "&gridCondition=pcd_pcidIS" + pc_id + "')\">" + pc_code + "</a>&nbsp;";
						sb.append(log).append("<br>");
					}
					
				}else if("请购单".equals(s)){
					//如果md_worktype='请购单',则将明细结案
					itemsByType = groupByType.get(s);
					for(Map<String,Object> map : itemsByType){
						Object ap_code = map.get("md_ordercode");
						String remark = SystemSession.getUser().getEm_name()+"结案"+map.get("md_mpscode");
						baseDao.execute("update ApplicationDetail set ad_status='已结案',ad_statuscode='FINISH',ad_remark=ad_remark||' "+remark+"' where ad_code='"+ap_code+"' and ad_detno="+map.get("md_orderdetno")+" and nvl(ad_status,' ')<>'已结案' ");
						log = "请购单明细更新为结案，单号："+ap_code+",行号："+map.get("md_orderdetno")+"";
						sb.append(log).append("<br>");
					}
				}
			}
			baseDao.execute(sqls);
		}
		if(sb!=null && sb.length()>0){
			baseDao.execute("update MRPData set MD_STATUS='已投放',MD_STATUSCODE='THROWED' where md_id in ("+ids+")");
			return sb.toString();
		}
		return null;
	}
	public Map<Object, List<Map<String, Object>>> groupMap(List<Map<String, Object>> maps, String groupField) {
		Map<Object, List<Map<String, Object>>> set = new HashMap<Object, List<Map<String, Object>>>();
		List<Map<String, Object>> list = null;
		for (Map<String, Object> map : maps) {
			Object key = map.get(groupField);
			if (set.containsKey(key)) {
				list = set.get(key);
			} else {
				list = new ArrayList<Map<String, Object>>();
			}
			list.add(map);
			set.put(key, list);
		}
		return set;
	}
	@Override
	public void autoThrow() throws Exception{
		try {
			String sob = SpObserver.getSp();//获取当前账套
			String defaultSob = BaseUtil.getXmlSetting("defaultSob");//默认账套
			String sql = "select * from "+defaultSob+".SYS_SCHEDULETASK where CODE_ = 'MRP-autoThrowTask' and enable_ = -1";
			ScheduleTask scheduleTask = null;
			try{
				scheduleTask = baseDao.getJdbcTemplate().queryForObject(sql, ParameterizedBeanPropertyRowMapper.newInstance(ScheduleTask.class));
			} catch (EmptyResultDataAccessException e) {
				
			}
			if(scheduleTask!=null){
				String masterStr = scheduleTask.getMaster_();
				String condition = scheduleTask.getCondition_();
				if(StringUtils.isEmpty(condition))
					condition = " 1=1 ";
				//清理无效PR 记录操作日志 当前用户默认为ADMIN
				Employee employee = new Employee();
				employee.setEm_name("ADMIN");
				SystemSession.setUser(employee);
				if (!StringUtils.isEmpty(masterStr)) {
					String[] masterArray = masterStr.toUpperCase().replace(" ", "").split(",");
					for (String master : masterArray) {
						SpObserver.putSp(master);
						baseDao.execute("insert into "+defaultSob+".SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
								+ "(sysdate,'账套（开始）："+master+"',"+scheduleTask.getId_()+")");
						throwCount = 0;
						Object mainCode = baseDao.getFieldDataByCondition("( select * from MPSMAIN where MM_RUNSTATUS ='已运算' and nvl(mm_ifautothrow,0)<>0 order by mm_id desc)", "MM_CODE", "rownum = 1");
						if(!StringUtils.isEmpty(mainCode)){
							//清理无效PR 转投放状态为 PURCHASE 投放采购件
							mpsdesk_turnpurchase(mainCode.toString(),"MpsDesk");
							//获取所有可投放mm_id
							sql ="select MD_ID from ( select MD_ID from MRPData left join product on pr_id=md_prodid left join employee on em_code = md_partno "
									+ "where NVL(pr_supplytype,' ')<>'VIRTUAL' and NVL(pr_specvalue,' ')<>'NOTSPECIFIC' "
									+ "and md_kind='NEED' and md_statuscode='UNTHROW' and nvl(MD_THROWREMARK,' ') = ' ' "
									+ "and md_mpscode ='"+mainCode+"' and ( "+condition+" ) order by MD_ID ) where rownum <=1000";
							//递归 每次1000投放数据
							throwMrp(sql,mainCode.toString());
							//循环 自动提交请购单 
							//默认条件 ap_source = MRP and ap_refcode = mainCode and ap_statuscode = ENTERING
							List<Object> apIdList = baseDao.getFieldDatasByCondition("Application", "ap_id", "ap_statuscode ='ENTERING' and ap_source ='MRP' and ap_refcode ='"+mainCode+"' order by ap_id");
							if(!CollectionUtil.isEmpty(apIdList)){
								for (int i = 0; i < apIdList.size(); i++) {
									int apId = Integer.valueOf(apIdList.get(i).toString()) ;
									try{
										applicationService.submitApplication(apId,"Application");
									}catch(Exception e){
										baseDao.execute(baseDao.logger.getMessageLog("自动提交MRP来源请购单", "失败："+e.getMessage(),
												"Application","ap_id",(Object)apId).getSql());
									}
								}
							}
						}
						baseDao.execute("insert into "+defaultSob+".SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
								+ "(sysdate,'账套（结束）："+master+" 投放数量（"+mainCode+"）："+throwCount+"',"+scheduleTask.getId_()+")");
					}
				}
			}
			SystemSession.clear();
			SpObserver.putSp(sob);//切回原账套
			}catch(Exception e){
				throw new Exception(e);
			}
	}
	private void throwMrp(String sql,String mainCode){
		List<Map<String, Object>> mdIdList = baseDao.queryForList(sql);
		if(!CollectionUtil.isEmpty(mdIdList)){
			throwCount += mdIdList.size();
			String gridStore = BaseUtil.parseGridStore2Str(mdIdList).toLowerCase();
			NeedThrow("MPSNeed", mainCode, gridStore, "AUTO", null,"", "","","");
			if(mdIdList.size()==1000){
				throwMrp(sql,mainCode);
			}
		}
	}
}
