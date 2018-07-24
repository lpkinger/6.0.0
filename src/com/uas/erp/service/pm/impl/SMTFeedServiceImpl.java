package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.SMTFeedService;

@Service("SMTFeedService")
public class SMTFeedServiceImpl implements SMTFeedService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public double getSMTFeed(String mpcode, String fecode, String mlscode,
			String macode, String table, String barcode, String mccode,
			String licode, String devcode, String sccode) {
		Double qty = 0.0;
		Object ob;
		if (mpcode != null && !"".equals(mpcode)) {
			int count = baseDao.getCountByCondition("Makeprepare", "mp_code='"
					+ mpcode + "'");
			if (count == 0) {
				BaseUtil.showError("备料单：" + mpcode + "，不存在！");
			}
			if (barcode != null) {
				count = baseDao
						.getCountByCondition(
								"Makeprepare left join MakeprepareDetail on mp_id=mpd_mpid",
								"mp_code='" + mpcode + "' and md_barcode='"
										+ barcode + "'");
				if (count == 0) {
					BaseUtil.showError("料卷号：" + barcode + "，在备料单[" + mpcode
							+ "]明细中不存在！");
				}
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(
				"select bar_prodcode,NVL(bar_remain,0) bar_remain,bar_place from barcode where bar_code=?",
				barcode);
		if (rs.next()) {
			if(rs.getDouble("bar_remain") == 0){
				BaseUtil.showError("料卷号:"+barcode+",错误，库存为0!");
			}	
			if(rs.getString("bar_place").equals("2")){//料卷已经上线
				BaseUtil.showError("料卷号:"+barcode+",已经上线!");
			}
			Object ids = baseDao.getFieldDataByCondition("productsmt left join productsmtlocation on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code","psl_id", "mc_code='" + mccode + "' and psl_prodcode='"+ rs.getObject("bar_prodcode") + "'");
			if (ids == null) {
				ob =  baseDao.getFieldDataByCondition("productsmt left join productsmtlocation on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code","psl_repcode", "mc_code='" + mccode + "' and psl_location='"+mlscode+"'");
				if(ob != null){
					if(!checkRep(rs.getObject("bar_prodcode").toString(),ob.toString())){
						BaseUtil.showError("料卷"+barcode+"不是站位表中物料或替代料!");
					}
				}else{
				   BaseUtil.showError("料卷"+barcode+"不是站位表中物料");
				}
			}else{
				ob =  baseDao.getFieldDataByCondition("productsmt left join productsmtlocation on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code","psl_table", "mc_code='" + mccode + "' and psl_location='"+mlscode+"'");
				if(ob == null){
					BaseUtil.showError("物料"+rs.getObject("bar_prodcode")+"是站位表中物料，但不是站位"+mlscode+"的料号!");
				}else if(table != null && !table .equals("") && !table.equals(ob.toString())){//板面不为空，判断输入的版面是否正确
					BaseUtil.showError("板面："+table+",错误，与SMT上料排位表中物料对应的板面不一致!");					
				}else{
					qty = rs.getDouble("bar_remain") ;
				}
			 }
			if (fecode != null) {
				SqlRowList rs1 = baseDao
						.queryForRowSet(
								"Select fe_code, fe_makecode,fe_usestatus,fe_spec,fe_statuscode from feeder  where fe_code=?",
								fecode);
				if (rs1.next()) {
					if(!rs1.getString("fe_statuscode").equals("AUDITED")){
						BaseUtil.showError("飞达：" + fecode
								+ "，未审核！");
					}
					ob =  baseDao.getFieldDataByCondition("productsmt left join productsmtlocation on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code","psl_feeder", "mc_code='" + mccode + "' and psl_location='"+mlscode+"'");
					if(!rs1.getString("fe_spec").equals(ob.toString())){
						BaseUtil.showError("飞达：" + fecode
								+ "错误，规格与站位表中需要的规格不符！");
					}	
					SqlRowList rs2 = baseDao
							.queryForRowSet(
									"select msl_id,msl_barcode from MakeSMTLocation where msl_mccode=? And msl_fecode=? and msl_status=0",
									mccode, fecode);
					if (rs2.next()) {							
							BaseUtil.showError("飞达：" + fecode
									+ "，不能重复上料！");
					}
					if (mlscode != null) {
						 rs2 = baseDao
								.queryForRowSet(
										"select msl_id,msl_barcode from MakeSMTLocation where MSL_MAKECODE=? And msl_location=? And msl_prodcode=? And msl_table=? and msl_status=0",
										macode, mlscode,
										rs.getObject("bar_prodcode"),
										table);
						if (rs2.next()) {							
								BaseUtil.showError("站位：" + mlscode
										+ "，不能重复上料！");
						}else{
							Object obs[] = baseDao.getFieldsDataByCondition("productsmtlocation", new String[]{"psl_baseqty","psl_repcode"}, "psl_id="+ids);
							baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
									"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
									"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)"+
									" select MAKESMTLOCATION_SEQ.nextval,mc_maid,mc_makecode,mc_id,"+
		                            " mc_code,1,'"+mlscode+"','"+rs.getObject("bar_prodcode")+"','"+obs[1]+"','"+rs1.getString("fe_spec")+"','"+obs[0]+"','"+
		                             table+"',"+obs[0]+"*mc_qty,"+qty+","+qty+",'"+fecode+"','"+barcode+"',mc_linecode,'"+devcode+"',0 from makeCraft where mc_code='"+mccode+"' and mc_statuscode='AUDITED'" );
							//更新bar_place 所在场所为已上线,线上【2】
							baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='"+barcode+"'");
							baseDao.execute("Update FeederUse set fu_status='已上料',fu_devcode='"
									+ devcode
									+ "' where fu_makecode='"
									+ macode
									+ "' And fu_fecode='"
									+ fecode + "'");
							int id = baseDao
									.getSeqId("CRAFTMATERIAL_SEQ");
							baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
									+ " select "
									+ id
									+ ",'"
									+ mccode
									+ "','"
									+ macode
									+ "','"
									+ licode
									+ "',ma_id,ma_prodcode,'"
									+ sccode
									+ "',sc_stepcode, sc_stepname,'"
									+ barcode
									+ "','"
									+ rs.getObject("bar_prodcode")
									+ "',"
									+ qty
									+ ",0,"
									+ qty
									+ ",sysdate,'"
									+ SystemSession.getUser()
											.getEm_name()
									+ "' from make,source where ma_code='"
									+ macode
									+ "' and sc_code='"
									+ sccode + "'");
									
						}
					}else{
						BaseUtil.showError("站位不能为空");
					}
				} else {
					BaseUtil.showError("飞达：" + fecode + "，不存在！");
				}
			}
		} else {
			BaseUtil.showError("料卷号：" + barcode + "，不存在！");
		}
		return qty;
	}

	@Override
	public void backSMTFeed( String mlscode,String macode, String mccode,String licode, String devcode, String sccode) {
		SqlRowList rs =baseDao.queryForRowSet("select * from MakeSMTLocation left join feeder on msl_fecode=fe_code where msl_mccode='"+mccode+"' and msl_linecode='"+licode+"'  and msl_location='"+mlscode+"'");
		if (rs.next()){
			baseDao.execute("Update MakeSMTLocation set msl_status=-1 where msl_makecode='" + macode +"' and msl_linecode='"+licode+"' and msl_location='"+mlscode+"'");
			baseDao.execute("update FeederUse set fu_status='待上料',fu_statuscode='UNFEEDING' where fu_makecode='"+macode+"' and fu_fecode='"
					+ rs.getString("msl_fecode") + "'");  
			baseDao.execute("update CraftMaterial set cm_outqty="+rs.getDouble("msl_remainqty")+",cm_remain="+rs.getDouble("msl_remainqty")+" where cm_mccode='"
					+mccode + "' and cm_sourcecode='"+sccode+"' and cm_barcode='"+rs.getString("msl_barcode")+"'");
			//更新bar_place 所在场所为线下，已下线【1】
			baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain="+rs.getDouble("msl_remainqty"), "bar_code='"+rs.getString("msl_barcode")+"'");
		}else{
			BaseUtil.showError("站位错误，该站位不存在作业单上料表中！");
		}  
	}

	@Override
	public void changeSMTFeed(String mlscode,String macode, String table, String new_barcode, String mccode,String licode, String devcode, String sccode) {
		String old_prcode, old_barcode,new_prcode;
		Double new_remain,old_remain;
		SqlRowList rs0 =baseDao.queryForRowSet("select * from MakeSMTLocation left join feeder on msl_fecode=fe_code where msl_makecode='"+macode+"' and msl_devcode='"+devcode+"'  and msl_location='"+mlscode+"'and msl_status=0");
		if (rs0.next()){
			old_prcode=rs0.getString("msl_prodcode"); old_barcode = rs0.getString("msl_barcode");
			old_remain = rs0.getDouble("msl_remainqty");
			SqlRowList rs =baseDao.queryForRowSet("select bar_code,bar_prodcode,bar_remain from barcode where bar_code='"+new_barcode+"' and bar_remain>0 and bar_status=0");
			if (rs.next()){	
				 new_prcode = rs.getString("bar_prodcode");	//换料料卷物料	
				 new_remain = rs.getDouble("bar_remain");
				if(new_barcode.equals(old_barcode)){
					BaseUtil.showError("料卷号："+old_barcode+"已经在该站位上，不允许换相同的料号!");
				}
				if(new_prcode.equals(old_prcode) || checkRep(new_prcode,old_prcode)){
					//先下料
					baseDao.updateByCondition("makeSMTLocation", "msl_status=-1", "msl_id="+rs0.getInt("msl_id"));
					//更新bar_place 所在场所为线下，已下线【1】
					baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain="+old_remain, "bar_code='"+old_barcode+"'");
					//然后再上料，期间飞达状态一直保持
					Object obs[] = baseDao.getFieldsDataByCondition("productsmtlocation left join productsmt on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code", new String[]{"psl_baseqty","psl_repcode"}, "psl_prodcode='"+new_prcode+"' and mc_code='"+mccode+"' and psl_location='"+mlscode+"'");
					int id = baseDao.getSeqId("MAKESMTLOCATION_SEQ");
					baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
									"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
									"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)" +
									" select "+id+", msl_maid,msl_makecode,msl_mcid," +
									" msl_mccode,msl_mmdetno,msl_location,'"+new_prcode+"','"+obs[1]+"',msl_fespec,"+obs[0]+",'"+
									table+"',"+obs[0]+"*mc_qty,"+new_remain+","+new_remain+",msl_fecode,'"+new_barcode+"',msl_linecode,msl_devcode,0 from makeSMTLocation left join makeCraft on mc_id=msl_mcid where mc_code='"+mccode+"' and msl_id="+rs0.getInt("msl_id"));
					//更新bar_place 所在场所已上线，线上【2】
					baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='"+new_barcode+"'");
					//换料同步更新备料单数据，replace替换原有记录
					baseDao.updateByCondition("makePrepareDetail", "md_barcode='"+new_barcode+"',md_prodcode='"+new_prcode+"',md_qty =nvl(md_qty,0)-"+old_remain+"+"+new_remain+", md_record = md_record ||',"+new_barcode+"'",
							"md_barcode='"+old_barcode+"' and md_mpid=(select mp_id from makePrepare where mp_mccode='"+mccode+"' and mp_statuscode='AUDITED' )");
					//更新CraftMaterial
					baseDao.execute("update CraftMaterial set cm_outqty="+old_remain+",cm_remain="+old_remain+" where cm_mccode='"
							+ mccode + "' and cm_sourcecode='"+sccode+"' and cm_barcode='"+old_barcode+"'");
					//新增一条CraftMaterial 记录
					baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode," +
							"cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
							+ " select CraftMaterial_seq.nextval ,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
							+ "cm_sourcecode,cm_stepcode,cm_stepname,'"+new_barcode+"','"+new_prcode+ "',"+new_remain+ ",0,"+new_remain+ ",sysdate,'"+ SystemSession.getUser().getEm_name()
							+ "' from CraftMaterial  where cm_mccode='"
							+ mccode + "' and cm_sourcecode='"+sccode+"' and cm_barcode='"+old_barcode+"'");
				}else{
					BaseUtil.showError("料卷号:"+new_barcode+"错误，不允许接与现有料或者其替代料不同的物料");
				}
			}else{
				BaseUtil.showError("无效料卷号！");
			}
		}else{
			BaseUtil.showError("无此机台工单站位！");
		}
	}

	@Override
	public void blankAll(String macode, String devcode,String mccode,String sccode) {
		//更新bar_place 所在场所已下线，线下【1】
		baseDao.execute("update barcode set bar_place='1',bar_forcastremain=(select msl_remainqty from makesmtlocation where msl_barcode=bar_code and msl_mccode='"+mccode+"' and nvl(msl_status,0)=0) where exists (select 1 from makesmtlocation where msl_barcode=bar_code and msl_mccode='"+mccode+"' and msl_status=0)");
		 baseDao.execute("merge into CraftMaterial  using makeSmtLocation "+
                 " on (msl_barcode=cm_barcode and msl_mccode=cm_mccode and cm_sourcecode='"+sccode+"'  and msl_mccode='"+mccode+"' and nvl(msl_status,0)=0)"+
                 " when matched then update set cm_outqty=msl_remainqty,cm_remain=msl_remainqty ");
		baseDao.execute("Update MakeSMTLocation set msl_status=-1 where msl_makecode='"
				+ macode + "' And msl_devcode='" + devcode + "' and nvl(msl_status,0)=0");
		baseDao.execute("Update FeederUse set fu_status='待上料',fu_devcode=null where fu_makecode='"
				+ macode + "' and fu_devcode='"+devcode+"'");		
	}
	
	@Override
	public void enableDevice(String decode) {
		baseDao.execute("update device set de_runstatus='运行中' where  de_code='" + decode + "' and de_runstatus='停止'");
	}
	
	@Override
	public void stopDevice(String decode) {
		baseDao.execute("update device set de_runstatus='停止' where  de_code='" + decode + "' and de_runstatus='运行中'");
	}

	@Override
	public void addSMTFeed(String mlscode, String macode,
			String table, String new_barcode, String mccode, String licode,
			String devcode, String sccode) {
		// TODO Auto-generated method stub
		String old_prcode,new_prcode,old_barcode;
		Double new_remain,old_remain;
		SqlRowList rs0 =baseDao.queryForRowSet("select * from MakeSMTLocation where msl_mccode='"+mccode+"' and msl_devcode='"+devcode+"'  and msl_location='"+mlscode+"'and msl_status=0 and nvl(msl_remainqty,0)>0");
		if (rs0.next()){
			old_prcode = rs0.getString("msl_prodcode");
			old_barcode = rs0.getString("msl_barcode");
			old_remain = rs0.getDouble("msl_remainqty");
			SqlRowList rs =baseDao.queryForRowSet("select bar_code,bar_prodcode,bar_remain from barcode where bar_code='"+new_barcode+"' and bar_remain>0 and bar_status=0");
			if (rs.next()){
				new_prcode = rs.getString("bar_prodcode");
				new_remain = rs.getDouble("bar_remain");
				if(new_barcode.equals(old_barcode)){
					BaseUtil.showError("料卷号："+old_barcode+"已经在该站位上，不允许接相同的料号!");
				}
				if(new_prcode.equals(old_prcode) || checkRep(new_prcode, old_prcode)){
					//生成新记录，remainqty+=原有
					baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
							"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
							"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)" +
							" select makeSMtlocation_seq.nextval, msl_maid,msl_makecode,msl_mcid," +
							" msl_mccode,msl_mmdetno,msl_location,'"+new_prcode+"',msl_repcode,msl_fespec,msl_baseqty,"+
							"msl_table,msl_needqty,"+new_remain+",msl_remainqty+"+new_remain+",msl_fecode,'"+new_barcode+"',msl_linecode,msl_devcode,0 from makeSMTLocation where msl_id="+rs0.getInt("msl_id"));
					//更新bar_place 所在场所为已上线【2】
					baseDao.updateByCondition("barcode", "bar_place='2'", "bar_code='"+new_barcode+"'");
					//更新bar_place 所在场所为线下【1】
					baseDao.updateByCondition("barcode", "bar_place='1',bar_forcastremain=0", "bar_code='"+old_barcode+"'");
					//将原有记录remainqty 变成0，
					baseDao.execute("Update MakeSMTLocation set msl_remainqty=0 ,msl_status=-1 where msl_id="+rs0.getInt("msl_id"));
					//接料更新备料单表，更新明细行,md_qty累加
					baseDao.updateByCondition("makePrepareDetail", "md_barcode='"+new_barcode+"',md_prodcode='"+new_prcode+"',md_qty =nvl(md_qty,0)-"+old_remain+"+"+new_remain+", md_record = md_record ||',"+new_barcode+"'", "md_barcode='"+old_barcode+"' and md_mpid=(select mp_id from makePrepare where mp_mccode='"+mccode+"' and mp_statuscode='AUDITED' )");
					//更新原CraftMaterial
					baseDao.execute("update CraftMaterial set cm_outqty=0,cm_remain=0 where cm_mccode='"
							+ mccode + "' and cm_sourcecode='"+sccode+"' and cm_barcode='"+old_barcode+"'");
					//新增一条CraftMaterial 记录
					baseDao.execute("INSERT INTO CraftMaterial(cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode," +
							"cm_sourcecode,cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"
							+ " select CraftMaterial_seq.nextval ,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,"
							+ "cm_sourcecode,cm_stepcode,cm_stepname,'"+new_barcode+"','"+new_prcode+ "',"+new_remain+ ",0,"+new_remain+ ",sysdate,'"+ SystemSession.getUser().getEm_name()
							+ "' from CraftMaterial  where cm_mccode='"
							+ mccode + "' and cm_sourcecode='"+sccode+"' and cm_barcode='"+old_barcode+"'");
				}else{
					BaseUtil.showError("料卷号:"+new_prcode+"错误，不允许接与现有料或者其替代料不同的物料");
				}
			}else{
				BaseUtil.showError("无效料卷号！");
			}
		}else{
			BaseUtil.showError("无此机台工单站位！");
		}
	}

	@Override
	public String beforeSMTFeedQuery(String caller, String condition) {
		// TODO Auto-generated method stub		
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(condition);
		String mc_code = String.valueOf(map.get("mc_code")), msl_devcode = String.valueOf(map.get("msl_devcode"));
		//判断作业单号是否存在，是否审核
		Object[] obs = baseDao.getFieldsDataByCondition("makeCraft", new String []{"mc_statuscode","mc_pscode"}, "mc_code='"+mc_code+"'");
		if( obs != null){
			if(obs[0] != null && !(obs[0].toString().equals("AUDITED"))){
				BaseUtil.showError("作业单:"+mc_code+"未审核!");
			}
		}else{
			BaseUtil.showError("作业单:"+mc_code+"不存在!");
		}	
		//判断机台号
		Object ob = baseDao.getFieldDataByCondition("device", "de_statuscode", "de_code='"+msl_devcode+"'");
		if(ob != null){
			if(!ob.toString().equals("AUDITED")){
				BaseUtil.showError("机台号："+msl_devcode+"，未审核！");
			}
		}else{
			BaseUtil.showError("机台号："+msl_devcode+"，不存在！");
		}	
		//判断机台是否还有其他作业单料卷未下料
		ob = baseDao.getFieldDataByCondition("device left join makesmtlocation on de_code=msl_devcode","msl_mccode", "de_code='"+msl_devcode+"' and msl_status=0 and msl_mccode<>'"+mc_code+"'");
		if(ob != null){
			BaseUtil.showError("机台号："+msl_devcode+"存在作业单号"+ob+"的料卷未下料，请先下料!");
		}
		//作业单号不能同时上两个机台号
		ob = baseDao.getFieldDataByCondition("makesmtlocation","msl_devcode", "msl_devcode<>'"+msl_devcode+"' and msl_status=0 and msl_mccode='"+mc_code+"'");
		if(ob != null){
			BaseUtil.showError("作业单："+mc_code+"已上机台号"+ob+",需要转线才能重新上其他机台!");
		}
		if(obs[1] != null){
			return obs[1].toString();
		}
		return null;
	}

	@Override
	@Transactional
	public void confirmChangeMake(String mc_devcode, String mc_code,
			String mc_makecode, String mc_linecode, String mcCode,String makeCode) {
		// TODO Auto-generated method stub
		//判断机台是否有料未下线
		SqlRowList rs ;
		int cn = baseDao.getCount("select count(1) cn  from makesmtlocation where msl_devcode='"+mc_devcode+"' and NVL(msl_status,0)=0 and msl_mccode='"+mc_code+"'");
		if(cn == 0){
			BaseUtil.showError("机台号:"+mc_devcode+",没有在线的料卷,无法进行工单切换!");			
		}else{
			rs = baseDao.queryForRowSet("select mc_statuscode,mc_prodcode,mc_pscode,mc_linecode,NVL(mc_craftcode,'0') mc_craftcode ,mc_maid,mc_qty,mc_id from makeCraft where mc_code='"+mcCode+"'");
			if(rs.next()){
				if(!rs.getString("mc_statuscode").equals("AUDITED")){//判断切换的作业单是否审核
					BaseUtil.showError("切换作业单号:"+mcCode+"未审核!");
				}
				Object [] obs = baseDao.getFieldsDataByCondition("makeCraft", new String []{"mc_prodcode","mc_pscode"}, "mc_code='"+mc_code+"'");
				if(obs != null){
					if(obs[0] != null && !obs[0].toString().equals(rs.getString("mc_prodcode"))){//产品编号是否一样
						BaseUtil.showError("切换工单："+makeCode+"产品编号与原工单不一致!");
					}					
					if(obs[1] != null && !obs[1].toString().equals(rs.getString("mc_pscode"))){//站位表编号是否一样
						BaseUtil.showError("切换工单："+makeCode+"排位编号与原工单不一致!");
					}
				}				
				if(rs.getString("mc_craftcode").equals("0")){//是否维护工艺路线
					BaseUtil.showError("切换作业单号:"+mcCode+"未维护工艺路线!");
				}	
				Object ob = baseDao.getFieldDataByCondition("device", "de_code", "de_code='"+mc_devcode+"' and de_linecode='"+rs.getString("mc_linecode")+"'");
				if(ob == null){//产线编号是否与机台编号中的产线编号一样 mc_linecode
					BaseUtil.showError("切换作业单号:"+mcCode+"的产线编号与机台所属的产线编号不一致!");
				}	
				cn = baseDao.getCount("select count(1) cn from makesmtlocation where msl_mccode='"+mcCode+"' and NVL(msl_status,0)=0");
				if(cn > 0){	//作业单没有在线的料卷
					BaseUtil.showError("切换作业单号:"+mcCode+"存在在线物料,不允许切换!");
				}
			}else{
				BaseUtil.showError("切换作业单号:"+mcCode+"不存在!");
			}			
			//执行切换操作
			baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
							"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
							"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)" +
							" select MAKESMTLOCATION_SEQ.nextval,"+rs.getString("mc_maid")+",'"+makeCode+"',"+rs.getInt("mc_id")+",'" +
							 mcCode +"',1,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
							" msl_table,msl_baseqty*"+rs.getString("mc_qty")+",msl_remainqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,0 from makeSMTLocation  where" +
							" msl_mccode='"+mc_code+"' and msl_status=0 and msl_remainqty>0");		   
		   //更新飞达领用工单号	
		   baseDao.execute("insert into FeederUse (fu_id,fu_makecode,fu_linecode,fu_wccode,fu_fecode,fu_fespec,fu_status,"+
                    " fu_usedate,fu_useman,fu_sourcecode,fu_devcode,fu_maid)"+
                    " select FEEDERUSE_SEQ.nextval,'"+makeCode+"',fu_linecode,fu_wccode,fu_fecode,fu_fespec,'已上料',"+
                    " sysdate,'"+SystemSession.getUser().getEm_name()+"',fu_sourcecode,fu_devcode,"+rs.getString("mc_maid")+" from feederUse  where fu_fecode in "+
                    " (select msl_fecode from makesmtlocation where msl_mccode='"+mcCode+"') and fu_makecode='"+mc_makecode+"' and fu_devcode='"+mc_devcode+"'");

			baseDao.execute("INSERT INTO CraftMaterial" +
					" (cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,cm_sourcecode," +
					" cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"+
					" select CRAFTMATERIAL_SEQ.nextval,'"+mcCode+"','"+makeCode+"',cm_linecode,"+rs.getString("mc_maid")+",cm_maprodcode,cm_sourcecode," +
					"  cm_stepcode,cm_stepname,cm_barcode,cm_soncode,msl_remainqty,0,msl_remainqty,sysdate,'"+SystemSession.getUser().getEm_name()+
                    "' from craftmaterial  left join makesmtlocation on cm_barcode=msl_barcode where cm_mccode='"+mc_code+"' and msl_status=0");
			//线上
			baseDao.execute("update  barcode set bar_place='2' where exists (select 1 from makesmtlocation where msl_barcode=bar_code and msl_mccode='"+mcCode+"' and msl_status=0)");
			//原有msl_remainqty = 0 线下
			baseDao.execute("update  barcode set bar_place='1',bar_forcastremain=0 where exists (select 1 from makesmtlocation where msl_barcode=bar_code and msl_mccode='"+mc_code+"' and msl_status=0 and nvl(msl_remainqty,0)=0)");
			//原作业单料卷下料
			baseDao.updateByCondition("makeSMTLocation", "msl_status=-1", "msl_mccode='"+mc_code+"'");		   
			//更新飞达状态
			baseDao.execute("Update FeederUse set fu_status='待上料',fu_devcode=null where fu_makecode='"
				   + mc_makecode + "' and fu_devcode='"+mc_devcode+"' and fu_fecode in (select msl_fecode from makesmtlocation where msl_mccode='"+mc_code+"')");
		}		
	}

	@Override
	public void confirmImportMPData(String mc_devcode, String mc_code,
			String mc_makecode, String mc_linecode, String mp_code,String sccode) {
		// TODO Auto-generated method stub
		//判断本机台当前是否没有物料在站位上
		int cn = baseDao.getCount("select count(1) cn  from makesmtlocation where msl_devcode='"+mc_devcode+"' and NVL(msl_status,0)=0 and msl_mccode='"+mc_code+"'");
		if(cn > 0){
			BaseUtil.showError("机台号:"+mc_devcode+",存在物料在线,不允许导入备料单数据!");			
		}else{
			//判断备料单是否属于作业单
			Object code = baseDao.getFieldDataByCondition("makePrepare", "mp_code", "mp_code='"+mp_code+"' and mp_mccode='"+mc_code+"'");
			if(code == null){
				BaseUtil.showError("备料单："+mp_code+"，不存在或者不属于该作业单!");
			}
			//判断备料单是否审核
			code = baseDao.getFieldDataByCondition("makePrepare", "mp_code", "mp_code='"+mp_code+"' and mp_status in ('已上飞达','已上线') and mp_statuscode='AUDITED'");
			if(code != null){
				//把备料单的站位物料数据导入到makesmtlocation表， 相当于批量上了所有料站的数据。	
				baseDao.execute("insert into MakeSMTLocation(msl_id,msl_maid,msl_makecode,msl_mcid,"+
						"msl_mccode,msl_mmdetno,msl_location,msl_prodcode,msl_repcode,msl_fespec,msl_baseqty,"+
						"msl_table,msl_needqty,msl_getqty,msl_remainqty,msl_fecode,msl_barcode,msl_linecode,msl_devcode,msl_status)" +
						" select MAKESMTLOCATION_SEQ.nextval,mp_maid,mp_makecode,mc_id,"+
						" mp_mccode,md_mmdetno,md_location,md_prodcode,md_repcode,md_fespec,md_baseqty,"+
						"'',md_needqty,md_qty,md_qty,md_fecode,md_barcode,'"+mc_linecode+"','"+mc_devcode+"',0 from makeprepare left join makeprepareDetail " +
						" on md_mpid=mp_id left join makeCraft on mc_code=mp_mccode  where" +
						" mp_mccode='"+mc_code+"' and mp_code='"+mp_code+"'");	
				Object []obs = baseDao.getFieldsDataByCondition("source", new String []{"sc_stepcode", "sc_stepname"}, "sc_code='"+sccode+"'");
				baseDao.execute("INSERT INTO CraftMaterial" +
						" (cm_id,cm_mccode,cm_makecode,cm_linecode,cm_maid,cm_maprodcode,cm_sourcecode," +
						" cm_stepcode,cm_stepname,cm_barcode,cm_soncode,cm_inqty,cm_outqty,cm_remain,cm_indate,cm_inman)"+
						" select CRAFTMATERIAL_SEQ.nextval,'"+mc_code+"','"+mc_makecode+"','"+mc_linecode+"',mp_maid,mc_prodcode,'"+sccode+"'," +
						"'"+obs[0]+"','"+obs[1]+"',md_barcode,md_prodcode,md_qty,0,md_qty,sysdate,'"+SystemSession.getUser().getEm_name()+
	                    "' from makePrepare  left join makePrepareDetail on md_mpid=mp_id left join makeCraft  on mc_code=mp_mccode where mp_mccode='"+mc_code+"' and mp_code='"+mp_code+"'");
				//更新备料单状态为已上线
				baseDao.updateByCondition("makePrepare", "mp_status='已上线'", "mp_code='"+mp_code+"'");
				//更新bar_place 所在场所为已上线【2】
				baseDao.execute("update  barcode set bar_place='2' where exists (select 1 from makesmtlocation where msl_barcode=bar_code and msl_mccode='"+mc_code+"' and msl_status=0)");
			}else {
				BaseUtil.showError("备料单:"+mp_code+",未上飞达或者不存在!");
			}
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
}
