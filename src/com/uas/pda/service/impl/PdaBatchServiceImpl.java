package com.uas.pda.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaBatchService;
import com.uas.pda.service.PdaMsdService;

@Service("pdaBatchServiceImpl")
public class PdaBatchServiceImpl implements PdaBatchService{
	@Autowired BaseDao baseDao;
    @Autowired 
	private  VerifyApplyDao verifyApplyDao;
    @Autowired 
   	private PdaCommonDao pdaCommonDao;
	@Autowired
	private PdaMsdService pdaMsdService;
	
	@Override
	public Map<String, Object> getBarcodeData(String code,boolean pr_ismsd) {
		Map<String, Object> map = new HashMap<String, Object>();
		SqlRowList rs = baseDao
				.queryForRowSet("select nvl(bar_status,0) bar_status,bar_code ,bar_remain,bar_prodcode ,bar_id,pr_detail,bar_whcode,bar_location,bar_batchcode,pr_ismsd,nvl(pr_msdlevel,0) ps_msdlevel from barcode left join product on bar_prodcode=pr_code where bar_code='"
						+ code + "' and bar_status = 1");
		if (rs.next()) {
			if (rs.getDouble("bar_remain") == 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + ",剩余数为0");
			}else if (pr_ismsd && "N".equals(rs.getString("pr_ismsd"))) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + ",不是湿敏元件!");
			}
			map.put("data", rs.getCurrentMap());
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码号:" + code + "不存在或者不是在库状态");
		}
		if (pr_ismsd) {
			// 获取湿敏元件的MSDLog操作
			map.put("msdLog", pdaMsdService.loadMSDLog(code));
		}
		return map;
	}

	/**
	 * 分拆将原条码一分为二，将原条码存入到barcode中的前条码字段中，将bar_status状态置为无效（-2）
	 */
	@Transactional
	@Override
	public List<Map<String,Object>> breakingBatch(String or_barcode, Double or_remain, Double bar_remain,String reason) {
		String level = null;
		boolean ismsd = false;
		double restTime = 0;
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		SqlRowList rs0;
		if(bar_remain<=0){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数必须大于0!");
		}
		// 判断是否是在烘烤状态
		rs0 = baseDao.queryForRowSet("select ms_action from (select ms_action from msdlog where ms_barcode=?" + 
				 " order by ms_id desc) where rownum=1",or_barcode);
		if (rs0.next() && "入烘烤".equals(rs0.getString("ms_action"))) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码：" + or_barcode + "，状态为在烘烤，请先出烘烤再拆分!");
		}
		
		rs0 = baseDao.queryForRowSet("select * from barcode where bar_code=? and bar_status=1",or_barcode);
		if(rs0.next()) {
			double total_remain = rs0.getDouble("bar_remain");// 原条码的总数
			if(total_remain<=0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码库存不足!");
			}
			int or_barid = rs0.getInt("bar_id");
			Map<String, Object> mp1 = rs0.getCurrentMap();
			// 更新原barcode表，锁定该条码正在拆分，不允许同时操作
			baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
			SqlRowList rsP = baseDao.queryForRowSet("select pr_ismsd,pr_msdlevel from product where pr_id=" + mp1.get("bar_prodid"));
			if (rsP.next()) {
				if ("Y".equals(rsP.getString("pr_ismsd")) && !"1".equals(rsP.getString("pr_msdlevel"))) {// 是湿敏元件,等级不为1
					ismsd = true;
					level = rsP.getString("pr_msdlevel");
					restTime = pdaCommonDao.getMsdRestTime(or_barcode);
				}
			}
			
			// 判断当前条码数量是否等于前台传送参数的条码数，不等于则返回提示提示条码已经拆分
			if (NumberUtil.compare(total_remain, or_remain) != 0) {
				baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码已经被拆分!");
			}
			
			if (bar_remain >= total_remain) {
				baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数量必须小于原条码数量!");
			}

			// 新增的barcode 中的前条形码
			Object[] objs = baseDao.getFieldsDataByCondition("barcode left join Vendor  on bar_vendcode=ve_code", new String[] { "ve_id"}, "bar_id=" + or_barid);// 供应商ID
			// 新增Barcode1分拆
			int bar_id = baseDao.getSeqId("BARCODE_SEQ");
			String bar_code;
			if (objs[0] != null) {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
			} else {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 无供应商信息
			}
			mp1.remove("BAR_LASTPRINTDATE");
			mp1.remove("BAR_LASTPRINTMAN");
			mp1.remove("BAR_PRINTCOUNT");
			mp1.put("BAR_LASTCODE", or_barcode);
			mp1.put("BAR_LASTID", or_barid);
			mp1.put("BAR_ID", bar_id);
			mp1.put("BAR_KIND", "1");// 类型为分拆 ：1,合并：2，原始：0
			mp1.put("BAR_CODE", bar_code);
			mp1.put("BAR_REMAIN", bar_remain);
			mp1.put("BAR_STATUS", "1");
			mp1.put("BAR_RECORDDATE", DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
			baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
			// 产生barcodechange记录
			baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
					+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),reason,total_remain,or_barcode,or_barid,bar_code,bar_id,bar_remain);
			
			// 湿敏元件产生MSDLOG
			if (ismsd && !"1".equals(level)) {
				baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
						+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)"
						,bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
			}
			
			Map<String,Object> mapr = new HashMap<String, Object>();
			mapr.put("BAR_ID", bar_id);
			mapr.put("BAR_CODE", bar_code);
			mapr.put("BAR_REMAIN", bar_remain);
			rlist.add(mapr);
			
			boolean bo = baseDao.isDBSetting("BarCodeSetting", "BarInvalidAfBatch");// 条码拆分后原条码作废,默认原条码不作废*/	
			double rest = (new BigDecimal(Double.toString(total_remain))).subtract(new BigDecimal(Double.toString(bar_remain))).doubleValue();
			
			if(bo){// 如果确定原条码作废，则会生成两个新的条码
				bar_id = baseDao.getSeqId("BARCODE_SEQ");
				if (objs[0] != null) {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
				} else {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 生成条码
				}
				mp1.put("BAR_ID", bar_id);
				mp1.put("BAR_CODE", bar_code);
				mp1.put("BAR_REMAIN", rest);
				mp1.put("BAR_RECORDDATE",DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
				baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
				baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
				// 产生barcodechange记录
				baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
						+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),reason,total_remain,or_barcode,or_barid,bar_code,bar_id,rest);			
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),rest);
				
					Map<String,Object> mapl = new HashMap<String, Object>();
					mapl.put("BAR_ID", bar_id);
					mapl.put("BAR_CODE", bar_code);
					mapl.put("BAR_REMAIN", rest);
					rlist.add(mapl);
				}
			} else {// 如果原条码不作废，则只会生成一个新的条码为你需要拆分的数量，原条码数量减少// 更新原barcode表，修改锁定，更新原条码数量
				baseDao.updateByCondition("Barcode", "bar_status=1,bar_remain=" + rest, "bar_id=" + or_barid);
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							or_barcode,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
				}
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该条码不是在库状态");
		}
		return rlist;
	}

	@Override
	public Map<String, Object> combineBatch(String data,double total_remain) {
		List<Map<Object, Object>> gridStore = FlexJsonUtil.fromJsonArray(data, HashMap.class);
		Map<Object, List<Map<Object, Object>>> groupM = BaseUtil.groupsMap(gridStore, new String[] { "BAR_BATCHCODE", "BAR_WHCODE",
				"BAR_PRODCODE", "BAR_LOCATION" });
		// 物料号一致、仓库一致、储位一致,批号一致
		if (groupM.size() > 1) {//
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"需要合并的物料批次，料号，仓库，储位不一致不允许合并!");
		} else {
			SqlRowList rs = baseDao.queryForRowSet("select * from barcode where bar_code='" + gridStore.get(0).get("BAR_CODE")
					+ "' and bar_status=1");
			if (rs.next()) {
				if(rs.getDouble("bar_remain")<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码库存不足!");
				}
			String bar_code;
			Map<String, Object> mp = baseDao.getJdbcTemplate().queryForMap(
					"select * from barcode where bar_code='" + gridStore.get(0).get("BAR_CODE") + "'");
			Object ob = baseDao.getFieldDataByCondition("vendor", "ve_id", "ve_code='" + mp.get("BAR_VENDCODE") + "'");
			if (ob != null) {
				bar_code = verifyApplyDao.barcodeMethod(mp.get("BAR_PRODCODE").toString(), ob.toString(),0);
			} else {
				bar_code = verifyApplyDao.barcodeMethod(mp.get("BAR_PRODCODE").toString(), "",0);
			}
			int bar_id = baseDao.getSeqId("BARCODE_SEQ");
			mp.put("BAR_ID", bar_id);
			mp.put("BAR_CODE", bar_code);
			mp.put("BAR_KIND", "2");// 类型为合并
			mp.put("BAR_REMAIN", total_remain);
			mp.put("BAR_BATCHQTY", total_remain);
			mp.put("BAR_STATUS", "1");
			// 合并成一条barcode
			baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp, "barcode", new String[] {}, new String[] {}));
			int bc_id;
			for (Map<Object, Object> map : gridStore) {
				// 产生barcodechange记录
				bc_id = baseDao.getSeqId("BARCODECHANGE_SEQ");
				baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) values("
						+ bc_id
						+ ",'"
						+ map.get("BAR_PRODCODE")
						+ "',2,sysdate,'"
						+ SystemSession.getUser().getEm_name()
						+ "','',"
						+ map.get("BAR_REMAIN")
						+ ",'"
						+ map.get("BAR_CODE")
						+ "',"
						+ map.get("BAR_ID")
						+ ",'"
						+ bar_code
						+ "',"
						+ bar_id
						+ "," + total_remain + ")");
				// 更新原barcode表
				if(map.get("BAR_ID")!=null){
				baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + map.get("BAR_ID").toString() + "'");
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BAR_CODE", bar_code);
			map.put("BAR_REMAIN", total_remain);
			map.put("BAR_ID", bar_id);
			return map;
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码"+gridStore.get(0).get("BAR_CODE")+"不是在库状态");
			}
		}
	}

	@Override
	public Map<String, Object> searchPackageData(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		SqlRowList rs;
		// 判断序列号,内箱号是否属于该箱内产品
		rs = baseDao
				.queryForRowSet("select pa_prodcode,pr_detail,pr_spec,pa_outboxcode,pa_totalqty,pd_innerboxcode,pd_barcode,pd_innerqty,pd_id,pa_packageqty from packagedetail left join package on pa_id=pd_paid left join product on pr_code=pa_prodcode where pa_outboxcode='"
						+ map.get("or_outbox")
						+ "' and (pd_innerboxcode='"
						+ map.get("innerCode")
						+ "' OR pd_barcode='"
						+ map.get("innerCode") + "')");
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"拆分条码[" + map.get("innerBox") + "],不属于该外箱号");
		}
		return rs.getCurrentMap();
	}

	@Override
	@Transactional
	public List<Map<String,Object>> breakingPackage(String data,String param) {
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> store = FlexJsonUtil.fromJson(param);
		int total=0;
		for(int i=0;i<gridStore.size();i++){			
			SqlRowList rs11=baseDao.queryForRowSet("select pd_innerqty from packagedetail where pd_id="+gridStore.get(i).get("PD_ID"));
			if(rs11.next()){
				for (Map<String, Object> map : rs11.getResultList()) {
					total+= Integer.parseInt(map.get("pd_innerqty").toString());
				}
			}
		}
		String outBox;
		int pa_id;
		List<String> sqls = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		int old_id = Integer.valueOf(store.get("pa_id").toString());
		// 判断箱号是否有效
		SqlRowList rs = baseDao
				.queryForRowSet("select pa_id,pa_outboxcode,pa_totalqty,pa_packageqty,pr_id,pa_level,pa_makecode,pa_indate,pa_prodcode from package left join product on pr_code=pa_prodcode where pa_id='"
						+ old_id + "' and nvl(pa_status,0)=0");
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该箱号已被拆分");
		}
		// 锁定原包装箱不允许再拆分、状态为-1 已拆分
		baseDao.updateByCondition("package ", "pa_status='-1'", "pa_id='" + old_id + "'");

		// 生成新包装箱号
		outBox = verifyApplyDao.outboxMethod(rs.getString("pr_id"), "2");
		pa_id = baseDao.getSeqId("PACKAGE_SEQ");
		baseDao.execute("insert into package (pa_id,pa_prodcode,pa_outboxcode,pa_packdate,pa_level,pa_packageqty,pa_totalqty,pa_makecode,pa_status,pa_indate)values ("
				+ pa_id
				+ ",'"
				+ rs.getString("pa_prodcode")
				+ "','"
				+ outBox
				+ "',sysdate,'"
				+ rs.getString("pa_level")
				+ "',"
				+ gridStore.size()
				+ ",'"
				+ store.get("totalqty")
				+ "','"
				+ rs.getString("pa_makecode")
				+ "','0',"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, rs.getDate("pa_indate")) + ")");
		for (Map<Object, Object> mp : gridStore) {
			sqls.add("insert into packagedetail(pd_id,pd_paid,pd_outboxcode,pd_innerboxcode,pd_barcode,pd_innerqty) values("
					+ baseDao.getSeqId("PACKAGEDETAIL_SEQ") + "," + pa_id + "," + outBox + "," + mp.get("pd_innerboxcode") + ","
					+ mp.get("pd_barcode") + "," + mp.get("pd_innerqty") + ")");
		}
		baseDao.execute(sqls);
		baseDao.execute("update barcode set bar_outboxcode1='" + outBox + "',bar_outboxid1=" + pa_id
				+ " where exists (select 1 from packagedetail where pd_barcode=bar_code and pd_paid=" + pa_id + ")");
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("PA_ID", pa_id);
		map1.put("PA_OUTBOXCODE", outBox);
		map1.put("PA_TOTALQTY",rs.getInt("pa_totalqty")-total);
		list.add(map1);
		sqls.clear();

		// 保存原包装箱剩余的，生成新包装箱
		outBox = verifyApplyDao.outboxMethod(rs.getString("pr_id"), "2");
		pa_id = baseDao.getSeqId("PACKAGE_SEQ");
		baseDao.execute("insert into package(pa_id,pa_prodcode,pa_outboxcode,pa_packdate,pa_level,pa_packageqty,pa_totalqty,pa_makecode,pa_status,pa_indate) select "
				+ pa_id
				+ ",pa_prodcode,'"
				+ outBox
				+ "',sysdate,pa_level,pa_packageqty-"
				+ gridStore.size()
				+ ",pa_totalqty-"
				+ Double.valueOf(store.get("totalqty").toString()) + ",pa_makecode,'0',pa_indate from package where pa_id=" + old_id);

		String ids = CollectionUtil.pluckSqlString(gridStore, "pd_id");
		// 将原箱内剩余数据装另一箱
		if(ids!=null && !"".equals(ids)){
		SqlRowList rs1 = baseDao.queryForRowSet("select packagedetail.* from packagedetail left join package on pa_id=pd_paid where pa_id="
				+ old_id + " and pd_id not in(" + ids + ")");
		if (rs1.next()) {
			for (Map<String, Object> mp : rs1.getResultList()) {
				sqls.add("insert into packagedetail(pd_id,pd_paid,pd_outboxcode,pd_innerboxcode,pd_barcode,pd_innerqty) values("
						+ baseDao.getSeqId("PACKAGEDETAIL_SEQ") + "," + pa_id + ",'" + outBox + "','" + mp.get("pd_innerboxcode") + "','"
						+ mp.get("pd_barcode") + "','" + mp.get("pd_innerqty") + "')");
			}
			baseDao.execute(sqls);
		}
		}
		baseDao.execute("update barcode set bar_outboxcode1='" + outBox + "',bar_outboxid1=" + pa_id
				+ " where exists (select 1 from packagedetail where pd_barcode=bar_code and pd_paid=" + pa_id + ")");
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("PA_ID", pa_id);
		map2.put("PA_OUTBOXCODE", outBox);
		map2.put("PA_TOTALQTY", total);
		list.add(map2);
		return list;
	}	
	@Override
	public String outboxCodeMethod(String pr_code){
		String code = null;
		SqlRowList rs;
		rs = baseDao.queryForRowSet("select pr_id  from product where pr_code='" + pr_code + "'");
		if (rs.next()) {
			verifyApplyDao.outboxMethod(rs.getString("pr_id"), "2");
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该物料不存在!");
		}
		return code;
	}

	/**
	 * 获取外箱数据
	 */
	@Override
	public Map<String, Object> getOutBoxData(String outBox) {
		Map<String, Object> map = new HashMap<String, Object>();
		SqlRowList rs = baseDao
				.queryForRowSet("select pa_id,pa_prodcode,pa_outboxcode,pa_totalqty,pa_packageqty,pa_status,pa_level,pa_whcode from package where pa_outboxcode='"
						+ outBox + "' and nvl(pa_status,0)=0");
		if (rs.next()) {
			map.put("main",rs.getCurrentMap());
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"外箱号：" + outBox + "不存在或无效!");
		}
		// 获取明细表数据
		rs = baseDao.queryForRowSet("select packagedetail.* from package left join packagedetail on pd_paid=pa_id where pa_outboxcode='"
				+ outBox + "'");
		if (rs.next()) {
			map.put("detail",rs.getResultList());
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"外箱：" + outBox + "内不存在明细数据!");
		}
		return map;
	}

	
	
	@Override
	public Map<String, Object> backBreaking(int sourceid, String bar_ids) {
		 Map<String,Object> rmap = new HashMap<String, Object>();
		//原条码必须是在库状态
		SqlRowList rs = baseDao.queryForRowSet("select bar_id,bar_code,bar_remain,bar_batchqty,bar_whcode,bar_batchid,bar_batchcode,bar_prodcode,pr_detail,pr_spec from barcode left join product on pr_code=bar_prodcode where bar_id=? and bar_status=1",sourceid);
		if(rs.next()){//
			//判断子条码号不存在
				SqlRowList rs0 = baseDao.queryForRowSet("select wm_concat(bar_code)code,count(1)cn from barcode where bar_id in("+bar_ids+") and bar_status<>1 and rownum<20");
				if(rs0.next() && rs0.getInt("cn")>0){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"子条码不是在库状态，请选择未出库的条码进行撤销，子条码："+rs0.getString("code")+"!");
				}
				rs0 = baseDao.queryForRowSet("select wm_concat(bar_code)code,count(1)cn from barcode where bar_id in("+bar_ids+") and bar_status=1 and bar_sourcecode<>"+rs.getString("bar_code"));
				if(rs0.next() && rs0.getInt("cn")>0){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"子条码的来源条码不正确，子条码："+rs0.getString("code")+"!");
				}
				//同一个仓库，或者同一批次
				rs0 = baseDao.queryForRowSet("select wm_concat(bar_code)code,count(1)cn from barcode where bar_id in("+bar_ids+") and bar_status=1 and (bar_whcode<>'"+rs.getString("bar_whcode")+"' or bar_batchid<>"+rs.getInt("bar_batchid")+" or bar_prodcode<>'"+rs.getString("bar_prodcode")+"')");
				if(rs0.next() && rs0.getInt("cn")>0){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"子条码与来源条码的仓库、批次号、物料号必须一致，子条码："+rs0.getString("code")+"!");
				}
				rs0 = baseDao.queryForRowSet("select sum(nvl(bar_remain,0)) bar_remain from barcode where bar_id in("+bar_ids+")");
				if(rs0.next() && rs0.getDouble("bar_remain")>rs.getDouble("bar_batchqty")){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"子条码数量之和大于来源条码的入库批数量("+rs.getDouble("bar_remain")+")");
				}
				
				//进行合并
			    List<String> sqls = new ArrayList<String>();
			    double remain = NumberUtil.add(rs.getGeneralDouble("bar_remain"), rs0.getGeneralDouble("bar_remain"));
			    sqls.add("update barcode set bar_status=-2 where bar_id in("+bar_ids+")");
			    sqls.add("update barcode set bar_remain="+remain+" where bar_id ="+sourceid);
			    //记录日志barcodechange 表
			    sqls.add("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,"
			    		+ "bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty)"
						+ "select barcodechange_seq.nextval,bar_prodcode,2,sysdate,'"+SystemSession.getUser().getEm_name()+"','撤销拆分"+bar_ids.split(",").length+"条',bar_remain,bar_code,bar_id,'"+rs.getString("bar_code")+"',"+sourceid+","+remain+" from barcode where bar_id in("+bar_ids+")");
			    baseDao.execute(sqls);
			    rmap = rs.getCurrentMap();
			    rmap.put("BAR_REMAIN", remain);
			    return rmap;
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"来源条码号不存在或者不是在库状态！");
		}
	}

	@Override
	public Map<String, Object> getSonBarcode(String barcode) {
		Map<String,Object> rmap = new HashMap<String, Object>(); 
		SqlRowList rs = baseDao.queryForRowSet("select bar_id,bar_code,bar_remain,bar_batchqty,bar_whcode,bar_batchcode,bar_prodcode,pr_detail,pr_spec from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
		if(rs.next()){
			int totalcount = 0,count=0;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			//查询子条码
			SqlRowList rs0 = baseDao.queryForRowSet("select bar_id,bar_code,bar_status,bar_remain from barcode where bar_sourcecode=? and bar_status=1 and bar_code<>?",barcode,barcode);
			if(rs0.next()){
				list = rs0.getResultList();
				count = list.size();
				totalcount+=count;
				rmap.put("onwhcode",list);
				rmap.put("onwhcodecount", count);
			}
			rs0 = baseDao.queryForRowSet("select bar_id,bar_code,bar_status,bar_remain from barcode where bar_sourcecode=? and bar_status=2 and bar_code<>?",barcode,barcode);
			if(rs0.next()){
				list = rs0.getResultList();
				count = list.size();
				totalcount+=count;
				rmap.put("outwhcode",list);
				rmap.put("outwhcodecount",count);
			}
			if(totalcount == 0){
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码号不存在拆分的条码！");
			}else{
				rmap.put("totalcount", totalcount);
			}
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码号["+barcode+"]不存在或者不是在库状态！");
		}
		rmap.put("sourcecode", rs.getCurrentMap());
		return rmap;
	}
}
