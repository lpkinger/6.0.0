package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.service.pm.MakePrepareService;
import com.uas.pda.dao.PdaCommonDao;

@Service("makePrepareService")
public class MakePrepareServiceImpl implements MakePrepareService{
	@Autowired
	private BaseDao baseDao;		
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private MakeCraftDao makeCraftDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Override
	public void saveMakePrepare(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("MakePrepare", "mp_code", store.get("mp_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store);
		//判断作业单号是否已经存在工单备料单
		int cn = baseDao.getCount("select count(1) from makePrepare where mp_mccode='"+store.get("mp_mccode")+"'") ;
		if(cn > 0){
			BaseUtil.showError("作业单号："+store.get("mp_mccode")+",已经存在备料单，不允许重复备料!");
		}
		String precent = null;
		Object ob ;
		//保存之前判断作业单号状态是否为已审核，作业单号中的产线代码是否填写,
		checkMccode(store.get("mp_mccode"),store.get("mp_linecode"),store.get("mp_makecode"));
		//判断是否输入了上一备料单号，输入了则判断输入的上一备料单号的线别是否等于现在的线别
		Object lastCode = store.get("mp_lastcode");
		if(lastCode != null && !"".equals(lastCode)){			
			ob = baseDao.getFieldDataByCondition("makePrepare", "mp_code", "mp_code='"+lastCode+"' and mp_linecode='"+store.get("mp_linecode")+"'");
			if(ob == null){
				BaseUtil.showError("上一备料单号："+lastCode+"错误，线别不是："+store.get("mp_linecode"));
			}
			precent = baseDao.getDBSetting(caller,"MantissaChangePrecent");
			if(precent == null || precent.equals("0")){
				BaseUtil.showError("请设置尾数可转移百分比参数！");
			}			
		}		
		// 保存MakePrepare
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "MakePrepare"));		
		//根据SMT上料排位表,productsmtlocation 生成makePreparedetail 明细数据
		baseDao.execute("insert into makePreparedetail (md_id,md_detno,md_mpid,md_prodcode,md_location,md_fespec,md_needqty,md_baseqty,md_repcode)"+
		           " select MAKEPREPAREDETAIL_SEQ.nextval,psl_detno,"+store.get("mp_id")+",psl_prodcode,psl_location,psl_feeder,psl_baseqty*mc_qty,psl_baseqty,psl_repcode from"+ 
		           " makeCraft left join productsmt on mc_pscode=ps_code left join productsmtlocation on psl_psid=ps_id where mc_code='"+store.get("mp_mccode")+"'");		
		//更新md_mmdetno制造单明细序号
		baseDao.execute("update makepreparedetail set md_mmdetno=(select min(mm_detno) from makematerial left join makematerialreplace on mm_id=mp_mmid where mm_maid="+store.get("mp_maid")
				 +" and (mm_prodcode=md_prodcode or mp_prodcode=md_prodcode)) where md_mpid="+store.get("mp_id"));
		if(lastCode != null && !"".equals(lastCode)){//更新上一站位
			//,((md_qty-md_needqty)/pr_zxbzs)*100 md_precent 
			SqlRowList rs0 = baseDao.queryForRowSet("select md_prodcode,md_location,nvl(md_qty,0)-nvl(md_needqty,0) md_rest,md_barcode from makePrepare left join makePrepareDetail on md_mpid=mp_id left join product on pr_code=md_prodcode where mp_code='"+lastCode+"' and md_qty-md_needqty>pr_zxbzs*"+Double.valueOf(precent)+"*0.01");
			SqlRowList rs1 = baseDao.queryForRowSet("select md_id,md_location,md_prodcode,md_repcode from  makePrepareDetail  where md_mpid="+store.get("mp_id"));			
			if(rs0.next() && rs1.next()){
				for(Map<String,Object> map1:rs1.getResultList()){
					for(Map<String,Object> map0:rs0.getResultList()){
						if(map1.get("md_prodcode").equals(map0.get("md_prodcode")) || (map1.get("md_repcode") != null && "".equals(map1.get("md_repcode")) && makeCraftDao.checkRep(map0.get("md_prodcode").toString(),map1.get("md_repcode").toString()))){
							//判断上一站位有没有被占用
							ob = baseDao.getFieldDataByCondition("makePrepareDetail", "md_lastlocation", "md_mpid="+store.get("mp_id")+" and md_lastlocation ='"+map0.get("md_lastlocation")+"'");
							if(ob == null){//md_rest
								//获取配置是否将上一备料单剩余料卷飞达，直接转移到现备料单
							  if(baseDao.isDBSetting("transferRest")){
								   baseDao.execute("update makePrepareDetail set md_lastlocation='"+map0.get("md_location")+"',md_lastbarcode='"+map0.get("md_barcode")+"',md_lastqty="+map0.get("md_rest")+" where md_id="+map1.get("md_id"));		
							  }else{
							     baseDao.execute("update makePrepareDetail set md_lastlocation='"+map0.get("md_location")+"',md_lastbarcode='"+map0.get("md_barcode")+"',md_lastqty="+map0.get("md_rest")+",md_location='"+map0.get("md_location")+"',md_barcode='"+map0.get("md_barcode")+"',md_qty="+map0.get("md_rest")+" where md_id="+map1.get("md_id"));		
							  }
							}
						}
					}					
				}
			}
		}
		// 记录操作
		baseDao.logger.save(caller, "mp_code", store.get("mp_code"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	@Override
	public void updateMakePrepareById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakePrepare", "mp_statuscode", "mp_id=" + store.get("mp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store);
		// 修改MakePrepare
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "MakePrepare", "mp_id"));
		// 记录操作
		baseDao.logger.update(caller, "mp_id", store.get("mp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store);
	}
	
	@Override
	public void auditMakePrepare(int mp_id,String caller) {
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{mp_id});
		//执行审核操作
		baseDao.audit("MakePrepare", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditdate", "mp_auditman");
		//记录操作
		baseDao.logger.audit(caller,  "mp_id", mp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{mp_id});
	}
	
	@Override
	public void resAuditMakePrepare(int mp_id,String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakePrepare", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("MakePrepare", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditdate", "mp_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "mp_id" ,mp_id);
	}

	@Override
	public Map<String,Object> getBar(String barcode,String whcode,int maid,int mpid){
		String location = null;
		Double getQty = 0.0;
		Map<String,Object> rmap = new HashMap<String, Object>();
		//判断该备料单状态
		checkMPstatuscode(mpid);
		//检查料卷是否已经备料，或者在其他状态
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(md_barcode) from MakePrepareDetail where md_barcode=? and md_mpid=?",
						String.class, barcode, mpid);
		if (dets != null) {
			BaseUtil.showError("料卷号：" + barcode + "，已备料！");
		}
		//料卷是否存在,数量以及状态
		Object ob = baseDao.getFieldDataByCondition("barcode", "nvl(bar_place,'1')", "bar_code='"+barcode+"'and nvl(bar_status,0)=0");		
		if (ob == null) {
			BaseUtil.showError("料卷号：" + barcode + "，无效或不存在");
		}else if(ob.equals("2")){//已上线
			BaseUtil.showError("料卷号：" + barcode + "错误，已上线");
		}else if(!ob.equals("1")){//已备料
			BaseUtil.showError("料卷号：" + barcode + "错误，已在["+ob+"]中备料!");
		}
		Object [] obs = baseDao.getFieldsDataByCondition("barcode left join product on pr_code=bar_prodcode", new String[]{"bar_remain","bar_prodcode","bar_whcode","pr_supplytype","pr_zxbzs"}, "bar_code='"+barcode+"'");
		//判断料卷号是否属于该备料单
		SqlRowList rs = baseDao.queryForRowSet("select md_location,md_barcode,md_lastlocation,md_lastbarcode,md_lastqty,mp_whcode from makePrepareDetail left join makePrepare on mp_id=md_mpid where md_mpid="+mpid+" and md_prodcode='"+obs[1]+"'");
		if(rs.next()){//属于备料单主料
			if(rs.size() > 1){//一个物料有多个站位可选
				List<Map<String,Object>> list = rs.getResultList();
				Map<String,Object> map = checkLocations(list,obs,barcode);
				getQty = Double.valueOf(map.get("getQty").toString());
				location = map.get("location").toString();				
			}else if(rs.getString("md_barcode") == null || "".equals(rs.getString("md_barcode"))){//站位唯一,并且没有备料
				//判断是否存在上一料卷
				if(rs.getString("md_lastbarcode") != null && !"".equals(rs.getString("md_lastbarcode"))){//存在，判断是否等于上一尾料
					if(!rs.getString("md_lastbarcode").equals(barcode)){//不等于上一料卷						
						BaseUtil.showError("料卷对应的站位:"+rs.getString("md_location")+",必须使用上一备料单结余的料卷编号："+rs.getString("md_barcode"));
					}else{//等于上一料卷
						 getQty = rs.getDouble("md_lastqty");
					}
				}else {//不存在上一料卷(尾料)
					getQty = getBarQty(obs,barcode,rs.getString("mp_whcode"));
				}
				location = rs.getString("md_location");
			}else{//站位已备料
				BaseUtil.showError("料卷对应的站位:"+rs.getString("md_location")+",已备料!");
			}
		}else{//不属于主料，判断是否属于替代料
			rs = baseDao.queryForRowSet("select md_repcode,md_location,md_barcode,md_lastlocation,md_lastbarcode,md_lastqty,mp_whcode from makePrepareDetail left join makePrepare on mp_id=md_mpid where md_mpid="+mpid+" and nvl(md_repcode,' ')<>' '");
			if(rs.next()){//存在替代料
				List<Map<String,Object>> list = rs.getResultList();
				List<Map<String,Object>> delList = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> map:list){
					if(!makeCraftDao.checkRep(obs[1].toString(),map.get("md_repcode").toString())){
						delList.add(map);						
					}
				}
				list.removeAll(delList);
				if(list.size() == 0){//不属于任何一个替代料
					BaseUtil.showError("料卷:"+barcode+"不是站位表中物料或替代料!");
				}else{//属于某个替代料
					Map<String,Object> map = checkLocations(list,obs,barcode);
					getQty = Double.valueOf(map.get("getQty").toString());
					location = map.get("location").toString();
				}				
			}else{//不存在替代料
				BaseUtil.showError("料卷:"+barcode+"所属物料,不在站位表中");
			}
		}	
		//更新备料单明细备料料卷数量等
		baseDao.updateByCondition("makePrepareDetail", "md_barcode='"+barcode+"', md_qty="+getQty+",md_record='"+barcode+"'", "md_location='"+location+"' and md_mpid="+mpid);		
		//更新料卷所在场所bar_place
		baseDao.updateByCondition("barcode", "bar_place=(select mp_code from makePrepare where mp_id="+mpid+")", "bar_code='"+barcode+"'");
	    rs = baseDao.queryForRowSet("select md_location,md_barcode,md_qty,md_prodcode,pr_spec,pr_detail from makePrepareDetail left join product on pr_code=md_prodcode where md_mpid="+mpid+" and md_location='"+location+"'");
		if(rs.next()){
			rmap = pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}
	    //判断是否所有明细都已备料，如果是更新为已备料
		int cn = baseDao.getCount("select count(1) from makePrePareDetail where md_barcode is null and md_mpid="+mpid);
		if( cn == 0){
			baseDao.updateByCondition("makePrepare", "mp_status='已备料'", "mp_id="+mpid);
			rmap.put("finish", true);
		}
		return rmap;
	}
	
	public Map<String,Object> checkLocations (List<Map<String,Object>> list,Object[]obs,String barcode){
		String los = null;
		Map<String,Object> rmap = new HashMap<String, Object>();
		//逐一判断站位是否已经备料
		List<Map<String,Object>> delList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map:list){
			if(map.get("md_barcode") != null && !"".equals(map.get("md_barcode"))){//已备料
				los += map.get("md_location")+",";
				delList.add(map);			
			}
		}
		list.removeAll(delList);
		delList.clear();
		if(list.size() == 0){//都已经备料
			BaseUtil.showError("料卷对应的站位["+los+"]都已备料!");
		}else{//剩余未备料的
			los = "";
			for(Map<String,Object> map:list){//判断上一站位
				if(map.get("md_lastbarcode") != null && !"".equals(map.get("md_lastbarcode"))){//存在上一站位
					//判断是否等于上一站位
					if(map.get("md_lastbarcode").toString().equals(barcode)){//等于上一站位
						rmap.put("getQty",Double.valueOf(map.get("md_lastqty").toString()));
						rmap.put("location",map.get("md_location").toString());
						return rmap;
					}else{
						los += map.get("md_lastbarcode")+",";
						delList.add(map);
					}
				}
			}
			list.removeAll(delList);
			if(list.size() == 0){//剩余的都存在上一料卷，不等于上一料卷
				BaseUtil.showError("料卷对应的站位存在上一料卷["+los+"]!");
			}else{//剩下的list 没有上一料卷，判断，选取其中的一个进行判断即可，判断成功则分配站位给此料卷号
				rmap.put("getQty",getBarQty(obs,barcode,list.get(0).get("mp_whcode").toString()));
				rmap.put("location",list.get(0).get("md_location").toString());
			}
		}	
		return rmap;
	}
	/**
	 * 在站位没有上一料卷时，判断料卷获取上料数量
	 * @return
	 */
	public double getBarQty(Object[] obs,String barcode,String whcode){
		double getQty ;
		if(obs[3].toString().equals("PULL")){//拉式，并且不是上一尾料
			if(!obs[2].toString().equals(whcode)){//判断仓库是否正确
				BaseUtil.showError("料卷："+barcode+"不在仓库："+whcode+"中");
			}
			double bar_remain = Double.valueOf(obs[0].toString());
			if(bar_remain == 0){//判断bar_remain等于0
				BaseUtil.showError("料卷数量为0");
			}
			double pr_zxbzs = Double.valueOf(obs[4].toString());
			if(pr_zxbzs == bar_remain || pr_zxbzs < bar_remain){//料卷remain 等于或大于最小包装数
				//获取线边仓存在尾料的结余超过最小包装数的百分比			
				String precent = baseDao.getDBSetting("MakePrepare", "MustUsePrecent");
				if(precent != null){
					String dets = baseDao.getJdbcTemplate()
							   .queryForObject("select wm_concat(bar_code) from barcode where  bar_code<>'"+barcode+"' and nvl(bar_status,0)=0  and nvl(bar_remain,0)>"+pr_zxbzs+"*"+precent+"*0.01 and nvl(bar_remain,0)<"+pr_zxbzs+" and bar_whcode='"+whcode+"' and nvl(bar_place,'1')='1' and bar_prodcode='"+obs[1]+"'",String.class);					
					if(dets != null)
						  BaseUtil.showError("存在尾料结余大于最小包装数的百分之"+precent+"的料卷编号："+dets);
				}
			}
	        getQty = bar_remain;			          
		}else {//推式
			Object ob = baseDao.getFieldDataByCondition("barcodeIO", "bi_outqty", "bi_barcode='"+barcode+"'");
			if(ob == null || Double.valueOf(ob.toString())==0){
				BaseUtil.showError("料卷号："+barcode+"错误，推式物料未生成领料单！");
			}
			getQty = Double.valueOf(ob.toString());				
		}	
	   return getQty;
	}

	@Override
	public Map<String,Object> returnBar(String barcode, int mpid) {
		//判断该备料单状态
		checkMPstatuscode(mpid);
		SqlRowList rs = baseDao.queryForRowSet("select NVL(bar_status,0)bar_status from barcode where bar_code='"+barcode+"'");
		if(rs.next()){
			if(rs.getInt("bar_status") == -1){
				BaseUtil.showError("料卷号:"+barcode+"无效！");
			}
		}else{
			BaseUtil.showError("料卷号:"+barcode+"不存在！");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(md_barcode) from MakePrepareDetail where md_barcode=? and md_mpid=?",
						String.class, barcode, mpid);
		if (dets == null) {
			BaseUtil.showError("料卷号：" + barcode + "，未备料！");
		}
		rs = baseDao.queryForRowSet("select md_location,md_barcode from MakePrepareDetail where md_barcode='"+barcode+"'");		
		//更新备料明细中料卷已备数量为空
		baseDao.execute("update  MakePrepareDetail set md_barcode='',md_qty=0,md_record='' where md_mpid=" + mpid + " and md_barcode='"+barcode+"'");
	    //更新条码所在场所bar_place
		baseDao.updateByCondition("barcode", "bar_place=''", "bar_code='"+barcode+"'");
		//如果备料单状态为已备料更新为已审核
		baseDao.updateByCondition("makePrePare", "mp_status='已审核'", "mp_id="+mpid+" and mp_status='已备料'");
		if(rs.next()){
			return pdaCommonDao.changeKeyToLowerCase(rs.getCurrentMap());
		}
		return null;
	}

	@Override
	public void deleteMakePrepare(int id, String caller) {
		// TODO Auto-generated method stub
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakePrepare", "mp_statuscode", "mp_id=" + id);
		StateAssert.delOnlyEntering(status);		
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//判断已经备料的物料中是否已经上料
		SqlRowList rs = baseDao.queryForRowSet("select count(1) cn,wm_concat(md_detno) no from makesmtlocation left join makepreparedetail on md_prodcode=msl_prodcode and md_barcode=msl_barcode where  md_mpid="+id+" and NVL(msl_status,0)=0");
		if(rs.next() && rs.getInt("cn")> 0){
			BaseUtil.showError("不允许删除,序号："+rs.getString("no")+",已经上料!");
		}
		// 删除MakePrepare
		baseDao.deleteById("makePrepare", "mp_id", id);	
		// 删除MakePreparedetail
		baseDao.deleteById("MakePreparedetail", "md_mpid", id);
		// 记录操作
	    baseDao.logger.delete(caller, "mp_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public void submitMakePrepare(int id, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[在录入]的备料单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("makeprepare", "mp_statuscode", "mp_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("makePrepare", "mp_id=" + id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mp_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });		
	}

	@Override
	public void resSubmitMakePrepare(int id, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[已提交]的备料单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakePrepare", "mp_statuscode", "mp_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, id); // 执行反提交操作
        baseDao.resOperate("makePrepare", "mp_id="+id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", id);
		handlerService.afterResSubmit(caller, id);	
	}
	/**
	 * 判断备料单状态是否能够备料
	 * @param mp_id
	 */
	private void checkMPstatuscode (int mp_id){
		//判断该备料单状态
		Object []obs = baseDao.getFieldsDataByCondition("makePrepare", new String []{"mp_status","mp_statuscode"}, "mp_id="+mp_id);
		if(obs != null){
			if(!obs[1].toString().equals("AUDITED")){
				BaseUtil.showError("备料单未审核，请先审核再进行备料相关操作!");
			}
			if(obs[0].toString().equals("已上飞达")){
				BaseUtil.showError("备料单已上飞达!");
			}
			if(obs[0].toString().equals("已上线")){
				BaseUtil.showError("备料单已上线，不允许修改!");
			}
		}
	}
	//判断作业单逻辑
	private void checkMccode(Object mccode, Object linecode, Object makecode) {
		// TODO Auto-generated method stub
		if(mccode != null && !"".equals(mccode)){
			//判断作业单号是否存在
			Object [] obs = baseDao.getFieldsDataByCondition("MakeCraft", new String[]{"mc_statuscode","mc_linecode","mc_makecode"}, "mc_code='"+mccode+"'");
			if(obs != null){
				if(obs[0] != null && !obs[0].toString().equals("AUDITED")){
					BaseUtil.showError("作业单:"+mccode+"，未审核!");
				}
				if(!obs[2].toString().equals(makecode)){
					BaseUtil.showError("制造单号:"+makecode+"与作业单号中的制造单不一致!");
				}
				if(obs[1] != null && !"".equals(obs[1])){
					if(linecode != null && !"".equals(linecode)){
						if(!obs[1].toString().equals(linecode)){
							BaseUtil.showError("产线代码:"+linecode+"，与作业单中所属线别不一致!");
						}
					}else{
						BaseUtil.showError("产线代码不允许为空!");
					}
				}else{
					BaseUtil.showError("作业单:"+mccode+",所属线别未维护!");
				}
			}else{
				BaseUtil.showError("作业单:"+mccode+"，不存在!");
			}
		}else{
			BaseUtil.showError("作业单号不允许为空!");
		}		
	}

	@Override
	public void toProdIOGet(int id,String caller) {
		// TODO Auto-generated method stub
		//判断makesmtlocation 是否存在，并且msl_status=-1都一下料，
		//不能重复转pi_sourcecode
		String mc_code = baseDao.getJdbcTemplate().queryForObject("select mp_mccode from makePrepare where mp_id="+id, String.class);
		if(mc_code != null){
			int cn = baseDao.getCount("select count(1) from makesmtlocation where msl_mccode='"+mc_code+"'");
			if(cn == 0){
				BaseUtil.showError("不允许转领料单，作业单未进行过上料!");
			}
			cn = baseDao.getCount("select count(1) from makesmtlocation where msl_mccode='"+mc_code+"' and nvl(msl_status,0)=0");
			if(cn > 0){
				BaseUtil.showError("不允许转领料单，作业单还有未下料的料卷!");
			}
			Object ob = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_sourcecode='"+mc_code+"'");
			if(ob != null){
				BaseUtil.showError("已转领料单："+ob+",不允许重复转!");
			}else{
				String ma_id = baseDao.getJdbcTemplate().queryForObject("select mp_maid from makePrepare where mp_id="+id, String.class);
				//更新md_mmdetno制造单明细序号
				baseDao.execute("update makepreparedetail set md_mmdetno=(select min(mm_detno) from makematerial left join makematerialreplace on mm_id=mp_mmid where mm_maid="+ma_id
						 +" and (mm_prodcode=md_prodcode or mp_prodcode=md_prodcode)) where md_mpid="+id);
				makeCraftDao.turnProdOut(mc_code);
			}
		}else{
			BaseUtil.showError("备料单不存在，或者备料单中未维护作业单号!");
		}
		// 记录操作
		baseDao.logger.getMessageLog("转领料单", "成功", caller, "mp_id", id);
	}

}

