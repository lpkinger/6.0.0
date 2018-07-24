package com.uas.pda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.SMTFeedService;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaShopFloorManageService;

@Service("pdaShopFloorManageServiceImpl")
public class PdaShopFloorManageServiceImpl implements PdaShopFloorManageService{

	@Autowired 
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Autowired
	private SMTFeedService SMTFeedService;
	@Override
	public Map<String, Object> getMakeData(String code) {
		SqlRowList rs = baseDao.queryForRowSet("select *　from device where de_code='" + code + "'");
		if (rs.next()) {
			rs = baseDao
					.queryForRowSet("select msl_devcode mc_devcode, mc_id,mc_code,mc_makecode,mc_linecode,mc_craftcode,mc_craftname,mc_prodcode,mc_qty,mc_maid ,mc_pscode,nvl(mc_sourcecode,'"
							+ rs.getString("de_sourcecode")
							+ "') mc_sourcecode,"
							+ "ps_prodcode,ps_statuscode,ps_id from makesmtlocation left join makecraft on mc_code=msl_mccode left join productsmt "
							+ "on ps_code=mc_pscode where msl_devcode='"
							+ code
							+ "' and mc_statuscode='AUDITED' and ps_statuscode='AUDITED' " + "and  msl_status=0");
			if (rs.next()) {
				return rs.getCurrentMap();
			} else {
				return null;
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"设备编号不存在!");
		}
	}
	@Override
	public Map<String, Object> checkCode(String devCode, String code) {
		Map<String, Object> rmap = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select *　from device where de_code='" + devCode + "'");
		if (rs.next()) {	
			if(!rs.getString("de_statuscode").equals("AUDITED")){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"机台号：" + devCode +"未审核!");
			}
			if(code == null || "".equals(code) ){//作业单号为空
				rmap.put("craft",getMakeData(devCode));
			}else{
				//判断机台是否还有其他作业单料卷未下料
				Object ob = baseDao.getFieldDataByCondition("device left join makesmtlocation on de_code=msl_devcode","msl_mccode", "de_code='"+devCode+"' and msl_status=0 and msl_mccode<>'"+code+"'");
				if(ob != null){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"机台号："+devCode+"存在作业单号"+ob+"的料卷未下料，请先下料!");
				}
			    rs = baseDao.queryForRowSet("select mc_devcode, mc_id,mc_code,mc_makecode,mc_linecode,mc_craftcode,mc_craftname,mc_prodcode,mc_qty,mc_maid ,mc_pscode,nvl(mc_sourcecode,'"+rs.getString("de_sourcecode")+"') mc_sourcecode,ps_prodcode,ps_statuscode,ps_id from makecraft  left join productsmt on ps_code=mc_pscode where mc_code='"+code+"' and mc_statuscode='AUDITED' and ps_statuscode='AUDITED'");			
				if(rs.next()){
					if(rs.getObject("mc_pscode") == null ){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"单号未定义料站排程表或料站排程表未审核");
					}else if(!rs.getString("ps_prodcode").equals(rs.getString("mc_prodcode"))){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料站排程表中产品编号与单据中的产品编号不一致!");
					}else if(rs.getObject("mc_devcode") == null){
						rs.getAt(0).put("mc_devcode", devCode);
					}					
					//作业单号不能同时上两个机台号
					ob = baseDao.getFieldDataByCondition("makesmtlocation","msl_devcode", "msl_devcode<>'"+devCode+"' and nvl(msl_status,0)=0  and msl_mccode='"+code+"'");
					if(ob != null){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"作业单："+code+"已上机台号"+ob+",需要转线才能重新上其他机台!");
					}
					//判断是否在该机台上没有有效数据，未上过料，有备料单，提示导入
					int cn = baseDao.getCount("select count(1) cn from makesmtlocation where msl_mccode='"+code+"' and nvl(msl_status,0)=0 and msl_devcode='"+devCode+"'");
					if(cn == 0){//未上料
						ob = baseDao.getFieldDataByCondition("makePrepare", "mp_code", "mp_mccode='"+code+"' and mp_status='已上飞达' and mp_statuscode='AUDITED'");
						if(ob != null){
							rmap.put("prepare", ob);
						}
					}
					rmap.put("craft",rs.getCurrentMap());
				}else{
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单号不存在或者未审核!");
				}
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"设备编号不存在!");
		}
		return rmap;
	}
	@Override
	public List<Map<String, Object>> getCollectDetailData(String data) {
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		SqlRowList rs = baseDao.queryForRowSet("select psl_prodcode,psl_feeder, psl_location,psl_table,ps_prodcode,ps_id,psl_id,psl_repcode from productsmt left join productsmtlocation on ps_id=psl_psid  where ps_id="+map.get("ps_id")+" And ps_prodcode='"+map.get("prodcode")+"' and ps_statuscode='AUDITED'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}
		return null;
	}

	@Override
	public Map<String, Object> getBarRemain(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		SqlRowList rs = baseDao.queryForRowSet("select bar_remain ,bar_code,bar_prodcode,bar_place from barcode where bar_code='"
				+ map.get("bar_code") + "' and bar_status=0 and bar_remain>0");
		if (rs.next()) {
			// 等于物料号或者替代料编号
			if (!rs.getString("bar_prodcode").equals(map.get("prod_code").toString())
					&& !checkRep(rs.getString("bar_prodcode"), String.valueOf(map.get("rep_code")))) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料卷编号错误,所属物料不等于需要采集的物料或其替代料!");
			} else if (rs.getString("bar_place").equals("2")) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料卷编号错误,已经上线!");
			} else {
				return rs.getCurrentMap();
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"料卷编号错误");
		}
	}

	/**
	 * 检验采集的是否为替代料
	 * @param rep_code
	 * @param prod_code
	 * @return
	 */
	private boolean checkRep(String prod_code,String rep_code){
		String [] arr = rep_code.split(",");
		for(int i=0;i<arr.length;i++){
			if(arr[i].equals(prod_code)){
				return true;
			}
		}
		return false;		
	}
	/**
	 * 确认上料的判断
	 */
	@Override
	public Map<String ,Object> loading(String msl,String makeCraft) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(msl);
		Map<Object, Object> mapC = FlexJsonUtil.fromJson(makeCraft);
		SqlRowList rs1 = baseDao.queryForRowSet("Select fe_code,fe_usestatus,fe_spec,fe_statuscode from feeder  where fe_code=?",
				map.get("msl_fecode"));
		if (rs1.next()) {
			if (!rs1.getString("fe_spec").equals(map.get("msl_fespec"))) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"飞达：" + map.get("msl_fecode") + "错误，规格与站位表中需求的规格不符！");
			}
			SqlRowList rs2 = baseDao.queryForRowSet(
					"select count(0) cn from MakeSMTLocation where  msl_fecode=? and msl_status=0 and msl_mccode='" + mapC.get("mc_code")
							+ "'", map.get("msl_fecode"));
			if (rs2.next() && rs2.getInt("cn") > 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"飞达：" + map.get("msl_fecode") + "，不能重复上料！");
			}
			rs2 = baseDao.queryForRowSet("select count(0) cn from MakeSMTLocation where  msl_location=? and msl_status=0 and msl_mccode='"
					+ mapC.get("mc_code") + "'", map.get("msl_location"));
			if (rs2.next() && rs2.getInt("cn") > 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"站位：" + map.get("msl_location") + "，不能重复上料！");
			} else {
				Object obs[] = baseDao.getFieldsDataByCondition("productsmtlocation", new String[] { "psl_baseqty", "psl_repcode" },
						"psl_id=" + map.get("psl_id"));
				baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
						"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
						"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)"+
						" select MAKESMTLOCATION_SEQ.nextval,mc_maid,mc_makecode,mc_id,"+
                        "mc_code,1,'"+map.get("msl_location")+"','"+map.get("msl_prodcode")+"','"+obs[1]+"','"+rs1.getString("fe_spec")+"','"+obs[0]+"',"+
                        "mc_table,"+obs[0]+"*mc_qty,"+map.get("msl_remainqty")+","+map.get("msl_remainqty")+",'"+map.get("msl_fecode")+"','"+map.get("msl_barcode")+"',mc_linecode,'"+mapC.get("mc_devcode")+"',0 from makeCraft where mc_id="+mapC.get("mc_id")+" and mc_statuscode='AUDITED'" );
				baseDao.execute("Update FeederUse set fu_status='已上料',fu_devcode='"
						+ mapC.get("mc_devcode")
						+ "' where fu_makecode='"
						+ mapC.get("mc_makecode")
						+ "' And fu_fecode='"
						+ map.get("msl_fecode") + "'");		
				int id = baseDao
						.getSeqId("CRAFTMATERIAL_SEQ");
				//资源编号
				Object []obs2 = baseDao.getFieldsDataByCondition("source ", new String[]{"sc_stepcode","sc_stepname","sc_code"}, "sc_code='"+mapC.get("mc_sourcecode")+"'");
				baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode," +
						"cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
						+ " select "+ id+ ",'"+ mapC.get("mc_code")+ "','"+ mapC.get("mc_makecode")+ "','"+ mapC.get("mc_linecode")+ "',ma_id,ma_prodcode,"
						+ "'"+obs2[2]+"','"+obs2[0]+"','"+obs2[1]+"','"+ map.get("msl_barcode")+ "','"+ map.get("bar_prodcode")+ "',"+ map.get("bar_remain")+ ",0,"+ map.get("bar_remain")+ ",sysdate,'"+ SystemSession.getUser().getEm_name()
						+ "' from make left join makeCraft on ma_code=mc_makecode  where mc_id="+mapC.get("mc_id")+" and mc_statuscode='AUDITED'" );
				//更新bar_place 所在场所为已上线,线上【2】
				baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='"+map.get("msl_barcode")+"'");
				//位号msl_location，卷号msl_barcode,物料编号msl_prodcode,数量msl_remain,飞达编号msl_fecode.
				rs1 = baseDao.queryForRowSet("select msl_location,msl_barcode, msl_prodcode,msl_remainqty,msl_fecode from MakeSMTLocation where msl_location='"+map.get("msl_location")+"' and msl_status=0");
				if(rs1.next()){
				   return rs1.getCurrentMap();
				}
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"飞达：" + map.get("msl_fecode") + "错误，不存在!");
		}
		return null;
	}

	/**
	 * 确认下料
	 */
	@Override
	public Map<String,Object> cuttingStock(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		SqlRowList rs = null;
		if (map.get("type").toString().equals("location")) {
			rs = baseDao.queryForRowSet(
					"Select * from makesmtlocation where msl_mccode=?  And msl_devcode=?  And msl_location=? And msl_status=0",
					map.get("mc_code"), map.get("mc_devcode"), map.get("msl_location"));
		} else if (map.get("type").toString().equals("barcode")) {
			rs = baseDao.queryForRowSet(
					"Select * from makesmtlocation where msl_mccode=?  And msl_devcode=?  And msl_barcode=? And msl_status=0",
					map.get("mc_code"), map.get("mc_devcode"), map.get("msl_barcode"));
		} else if (map.get("type").toString().equals("fecode")) {
			rs = baseDao.queryForRowSet(
					"Select * from makesmtlocation where msl_mccode=?  And msl_devcode=?  And msl_fecode=? And msl_status=0",
					map.get("mc_code"), map.get("mc_devcode"), map.get("msl_fecode"));
		}
		if (rs != null && rs.next()) {
			baseDao.updateByCondition("makesmtlocation", "msl_status=-1", "msl_id=" + rs.getInt("msl_id"));
			baseDao.updateByCondition("feederUse", "fu_status='待上料'",
					"fu_devcode='" + map.get("mc_devcode") + "' And fu_fecode='" + rs.getString("msl_fecode") + "'");
			baseDao.execute("update CraftMaterial set cm_outqty=" + rs.getDouble("msl_remainqty") + ",cm_remain="
					+ rs.getDouble("msl_remainqty") + " where cm_mccode='" + map.get("mc_code") + "' and cm_sourcecode='"
					+ map.get("mc_sourcecode") + "' and cm_barcode='" + rs.getString("msl_barcode") + "'");
			// 更新bar_place 所在场所为线下，已下线【1】
			baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain=" + rs.getDouble("msl_remainqty"),
					"bar_code='" + rs.getString("msl_barcode") + "'");
			return rs.getCurrentMap();
		} else {
			if (map.get("type").toString().equals("location")) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"站位编号：" + map.get("msl_location") + "采集错误!");
			} else if (map.get("type").toString().equals("barcode")) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料号：" + map.get("msl_barcode") + "采集错误!");
			} else if (map.get("type").toString().equals("fecode")) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"飞达编号：" + map.get("msl_fecode") + "采集错误!");
			}
		}
		return null;
	}

	/**
	 * 确认全部下料
	 */
	@Override
	public void cuttingAllStock(int mc_id,String mc_sourcecode) {
		SqlRowList rs = baseDao.queryForRowSet(" select count(0) cn,msl_maid from makesmtlocation where msl_mcid=" + mc_id
				+ " and NVL(msl_status,0)=0 group by msl_maid");
		if (rs.next() && (rs.getInt("cn") > 0)) {
			// 修改飞达领用状态
			baseDao.execute(" update feederUse set fu_status='待上料' where fu_maid='" + rs.getString("msl_maid") + "'"
					+ "and fu_fecode in (select msl_fecode from makeSMTLocation where msl_mcid=" + mc_id + ")");
			// bar_place 线下
			baseDao.execute("update  barcode set bar_place='1',bar_forcastremain=(select msl_remainqty from makesmtlocation where msl_barcode=bar_code and msl_mcid="
					+ mc_id + " and nvl(msl_status,0)=0) where exists (select 1 from makesmtlocation where msl_barcode=bar_code and msl_mcid="
					+ mc_id + " and nvl(msl_status,0)=0)");
			baseDao.execute("merge into CraftMaterial  using makeSmtLocation "
					+ " on (msl_barcode=cm_barcode and msl_mccode=cm_mccode and cm_sourcecode='" + mc_sourcecode + "'  and msl_mcid="
					+ mc_id + " and nvl(msl_status,0)=0)"
					+ " when matched then update set cm_outqty=msl_remainqty,cm_remain=msl_remainqty ");
			// 修改状态
			baseDao.updateByCondition("makesmtlocation", "msl_status=-1", "nvl(msl_status,0)=0 and msl_mcid=" + mc_id);
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有需要下料的数据");
		}
	}

	/**
	 * 接料,修改料卷编号，将remainqty和getqty 数据累加
	 */
	@Override
	public Map<String, Object> joinMaterial(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		String newBar = map.get("msl_barcode").toString();
		// 先查找该作业单，站位飞达中是否有料在,没有提示该站位没有上料，不需要接料，再判断接料是否为现在所在料，或者为替代料
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from makeSMTLocation where msl_mcid=? and msl_devcode=? and msl_location=? and msl_status=0 and nvl(msl_remainqty,0)>0",
						map.get("mc_id"), map.get("mc_devcode"), map.get("msl_location"));
		if (rs.next()) {
			String oldBar = rs.getString("msl_barcode");
			// 判断所接料号是否等于原料号
			if (newBar.equals(oldBar)) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料卷号：" + oldBar + "已经在该站位上，不允许接相同的料号!");
			}
			// 生成新记录，remainqty+=原有
			int id = baseDao.getSeqId("MAKESMTLOCATION_SEQ");
			baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
					"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
					"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)" +
					" select "+id+", msl_maid,msl_makecode,msl_mcid," +
					" msl_mccode,msl_mmdetno,msl_location,'"+map.get("msl_prodcode")+"',msl_repcode,msl_fespec,msl_baseqty,"+
					" msl_table,msl_needqty,"+map.get("msl_remainqty")+",msl_remainqty+"+map.get("msl_remainqty")+",msl_fecode,'"+newBar+"',msl_linecode,msl_devcode,0 from makeSMTLocation where msl_id="+rs.getInt("msl_id"));
			// 将原有记录remainqty 变成0，
			baseDao.execute("Update MakeSMTLocation set msl_remainqty=0,msl_status=-1 where msl_id=" + rs.getInt("msl_id"));

			// 更新bar_place 所在场所为已上线【2】
			baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='" + newBar + "'");
			// 更新bar_place 所在场所为线下【1】
			baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain=0", "bar_code='" + oldBar + "'");

			// 接料更新备料单表，更新明细行,md_qty累加
			baseDao.updateByCondition(
					"makePrepareDetail",
					"md_barcode='" + newBar + "',md_prodcode='" + map.get("msl_prodcode") + "',md_qty =nvl(md_qty,0)-"
							+ rs.getDouble("msl_remainqty") + "+" + Double.valueOf(map.get("msl_remainqty").toString())
							+ ", md_record = md_record ||'," + newBar + "'", "md_barcode='" + oldBar
							+ "' and md_mpid=(select mp_id from makePrepare where mp_mccode='" + rs.getString("msl_mccode")
							+ "' and mp_statuscode='AUDITED' )");
			// 更新CraftMaterial,cm_outqty = msl_remainqty
			baseDao.execute("update CraftMaterial set cm_outqty=0,cm_remain=0 where cm_mccode='" + rs.getString("msl_mccode")
					+ "' and cm_sourcecode='" + map.get("mc_sourcecode") + "' and cm_barcode='" + oldBar + "'");
			// 新增一条CraftMaterial 记录
			baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
					+ "cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
					+ " select CraftMaterial_seq.nextval ,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
					+ "cm_sourcecode,cm_stepcode,cm_stepname,'" + newBar + "','" + map.get("msl_prodcode") + "',"
					+ map.get("msl_remainqty") + ",0," + map.get("msl_remainqty") + ",sysdate,'" + SystemSession.getUser().getEm_name()
					+ "' from CraftMaterial  where cm_mccode='" + rs.getString("msl_mccode") + "' and cm_sourcecode='"
					+ map.get("mc_sourcecode") + "' and cm_barcode='" + oldBar + "'");

			rs = baseDao.queryForRowSet("select * from makeSMTLocation where msl_id=" + id);
			if (rs.next()) {
				return rs.getCurrentMap();
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"站位:" + map.get("msl_location") + "没有上料，不需要接料");
		}
		return null;
	}

	/**
	 * 换料：相当于先下料，再上料
	 */
	@Override
	public Map<String, Object> changeMaterial(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		String newBar = map.get("msl_barcode").toString();
		// 先查找该作业单，站位飞达中是否有料在，没有提示该站位没有上料，不需要换料
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from makeSMTLocation where msl_mcid=? and msl_devcode=? and msl_location=? and msl_status=0", map.get("mc_id"),
				map.get("mc_devcode"), map.get("msl_location"));
		if (rs.next()) {
			String oldBar = rs.getString("msl_barcode");
			// 判断换料号是否等于原料号
			if (newBar.equals(oldBar)) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"料卷号：" + oldBar + "已经在该站位上，不允许换相同的料号!");
			}
			// 下料
			baseDao.updateByCondition("makeSMTLocation", "msl_status=-1", "msl_id=" + rs.getInt("msl_id"));
			// 更新bar_place 所在场所为线下【1】
			baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain=" + rs.getDouble("msl_remainqty"), "bar_code='" + oldBar
					+ "'");
			// 然后再上料，期间飞达状态一直保持
			Object obs[] = baseDao.getFieldsDataByCondition("productsmtlocation", new String[] { "psl_baseqty", "psl_repcode" }, "psl_id="
					+ map.get("psl_id"));
			int id = baseDao.getSeqId("MAKESMTLOCATION_SEQ");
			baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"
					+ "msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"
					+ "msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)"
					+ " select " + id + ", msl_maid,msl_makecode,msl_mcid," + " msl_mccode,msl_mmdetno,msl_location,'"
					+ map.get("msl_prodcode") + "','" + obs[1] + "',msl_fespec," + obs[0] + "," + "mc_table," + obs[0] + "*mc_qty,"
					+ map.get("msl_remainqty") + "," + map.get("msl_remainqty") + ",msl_fecode,'" + newBar
					+ "',msl_linecode,msl_devcode,0 from makeSMTLocation left join makeCraft on mc_id=msl_mcid where msl_mcid="
					+ map.get("mc_id") + " and msl_id=" + rs.getInt("msl_id"));
			// 更新bar_place 所在场所为已上线【2】
			baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='" + newBar + "'");
			// 换料同步更新备料单数据，replace
			baseDao.updateByCondition(
					"makePrepareDetail",
					"md_barcode='" + newBar + "',md_prodcode='" + map.get("msl_prodcode") + "',md_qty =nvl(md_qty,0)-"
							+ rs.getDouble("msl_remainqty") + "+" + Double.valueOf(map.get("msl_remainqty").toString())
							+ ", md_record = md_record ||'," + newBar + "'", "md_barcode='" + oldBar
							+ "' and md_mpid=(select mp_id from makePrepare where mp_mccode='" + rs.getString("msl_mccode")
							+ "' and mp_statuscode='AUDITED' )");
			// 更新CraftMaterial
			baseDao.execute("update CraftMaterial set cm_outqty=" + rs.getDouble("msl_remainqty") + " ,cm_remain="
					+ rs.getDouble("msl_remainqty") + " where cm_mccode='" + rs.getString("msl_mccode") + "' and cm_sourcecode='"
					+ map.get("mc_sourcecode") + "' and cm_barcode='" + oldBar + "'");
			// 新增一条CraftMaterial 记录
			baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
					+ "cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
					+ " select CraftMaterial_seq.nextval ,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
					+ "cm_sourcecode,cm_stepcode,cm_stepname,'" + newBar + "','" + map.get("msl_prodcode") + "',"
					+ map.get("msl_remainqty") + ",0," + map.get("msl_remainqty") + ",sysdate,'" + SystemSession.getUser().getEm_name()
					+ "' from CraftMaterial  where cm_mccode='" + rs.getString("msl_mccode") + "' and cm_sourcecode='"
					+ map.get("mc_sourcecode") + "' and cm_barcode='" + oldBar + "'");
			rs = baseDao.queryForRowSet("select * from makeSMTLocation where msl_id=" + id);
			if (rs.next()) {
				return rs.getCurrentMap();
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"站位:" + map.get("msl_location") + "没有上料，不需要换料");
		}
		return null;
	}

	/**
	 * 料卷查询
	 */
	@Override
	public List<Map<String, Object>> queryData(int id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from makeSMTLocation where msl_mcid="+id +"  and msl_status<>-1");
		if(rs.next()){
		  return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());	
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有上料的料卷");
		}
	}

	@Override
	public List<Map<String, Object>> checkMakeSMTLocation(int id) {
		String sql = "select msl_fecode,msl_barcode,msl_location,msl_id from makeSMTLocation where msl_mcid="+id + " and msl_status=0 order by msl_location";	
		SqlRowList rs ;
		rs = baseDao.queryForRowSet(sql);
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		}	
		return null;
	}

	@Override
	public void importMPData(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		String mc_devcode = map.get("devCode").toString(), mc_code = map.get("mc_code").toString(), mp_code = map.get("mp_code").toString();
		Object ob = baseDao.getFieldDataByCondition("makeCraft", "mc_makecode", "mc_code='" + mc_code + "'");
		Object[] obs = baseDao.getFieldsDataByCondition("Device", new String[] { "de_linecode", "de_sourcecode", }, "de_code='"
				+ mc_devcode + "'");
		if (ob != null && obs != null) {
			SMTFeedService.confirmImportMPData(mc_devcode, mc_code, ob.toString(), obs[0].toString(), mp_code, obs[1].toString());
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"机台号或作业单号错误!");
		}
	}
	
}
