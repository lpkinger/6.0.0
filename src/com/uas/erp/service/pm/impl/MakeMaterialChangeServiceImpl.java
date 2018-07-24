package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeMaterialChangeService;

import net.sf.json.JSONObject;

@Service("makeMaterialChangeService")
public class MakeMaterialChangeServiceImpl implements MakeMaterialChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao; 
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeMaterialChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Integer mc_id = Integer.valueOf(store.get("mc_id").toString());
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeMaterialChange", "mc_code='" + store.get("mc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		//保存Dispatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeMaterialChange", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存DispatchDetail
		Object[] md_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			md_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				md_id[i] = baseDao.getSeqId("MAKEMATERIALDETCHANGEDET_SEQ");
			}
		} else {
			md_id[0] = baseDao.getSeqId("MAKEMATERIALDETCHANGEDET_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "MakeMaterialChangeDet", "md_id", md_id);
		baseDao.execute(gridSql);
		baseDao.updateByCondition("MakeMaterialChangeDet", "md_didstatus='待执行'", "md_mcid='"+mc_id+"'");
		try{
			//记录操作
			baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sqlString = "SELECT * FROM MakematerialchangeDet,make WHERE ma_code=md_makecode and " +
				"md_type<>'禁用' and md_oneuseqty*ma_qty>md_qty and md_mcid="+mc_id;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sqlString);
		StringBuffer errMessage = new StringBuffer();
		while(sqlRowList.next()){
			errMessage.append("行号为："+sqlRowList.getInt("md_detno")+"的 新需求数目"+sqlRowList.getFloat("md_qty")+
					"小于无损耗用量"+sqlRowList.getFloat("ma_qty")*sqlRowList.getFloat("md_oneuseqty")+"!\n");
		}
		//禁用的新需求数和新用量更新成0
		baseDao.execute("update MakematerialchangeDet set md_qty=0,md_oneuseqty=0 where md_mcid='"+mc_id+"' and md_type in ('DISABLE','禁用')and (md_qty<>0 or md_oneuseqty<>0)");
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}
	@Override
	public void deleteMakeMaterialChange(int mc_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{mc_id});		//删除Dispatch
		baseDao.deleteById("MakeMaterialChange", "mc_id", mc_id);
		//删除DispatchDetail
		baseDao.deleteById("MakeMaterialChangeDet", "md_mcid", mc_id);
		//记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{mc_id});
	}
	
	@Override
	public void updateMakeMaterialChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + store.get("mc_id"));
		StateAssert.updateOnlyEntering(status);		
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeMaterialChange", "mc_id");
		baseDao.execute(formSql);
		//修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeMaterialChangeDet", "md_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEMATERIALDETCHANGEDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeMaterialChangeDet", new String[]{"md_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//禁用的新需求数和新用量更新成0
		baseDao.execute("update MakematerialchangeDet set md_qty=0,md_oneuseqty=0 where md_mcid='"+store.get("mc_id")+"' and md_type in ('DISABLE','禁用')and (md_qty<>0 or md_oneuseqty<>0)");
				
		//记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}
	@Override
	public void auditMakeMaterialChange(int mc_id, String caller) {
		SqlRowList rs0;
		int gcount ;
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.auditOnlyCommited(status);
		rs0 = baseDao.queryForRowSet("SELECT md_makecode,ma_id,ma_status from MakematerialChangeDet left join make on md_makecode=ma_code where md_mcid='" + mc_id + "' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and (ma_id is null or ma_statuscode='FINISH') ");
		while(rs0.next()){
			if (rs0.getInt("ma_id")<=0){
				BaseUtil.showError("制造单:" + rs0.getString("md_makecode") + "不存在");
			}else{
				BaseUtil.showError("制造单:" + rs0.getString("md_makecode") + "状态:" + rs0.getString("ma_status") + ",不能变更");
			} 
			if (rs0.getInt("ma_id")>0){ 
				makeDao.refreshTurnQty(rs0.getInt("ma_id"),0); 
			} 
		}
		//禁用，修改时，必须工单序号不允许为空
		rs0 = baseDao.queryForRowSet("SELECT min(md_detno) detno,wm_concat(md_detno) as md_detno from MakematerialChangeDet WHERE md_mcid=" + mc_id+" and md_type in('UPDATE','修改','DISABLE','禁用') and nvl(md_mmdetno,0)=0 and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
		if(rs0.next()){
			if (rs0.getInt("detno")>0){
				BaseUtil.showError("序号:" + rs0.getString("detno") + "禁用或者修改变更类型的工单序号不允许为空");
			} 
		}
		rs0 = baseDao.queryForRowSet("SELECT count(1) num,wm_concat(md_detno) detno from MakematerialChangeDet left join makematerial on md_makecode=mm_code and md_mmdetno=mm_detno where md_mcid='" + mc_id + "' and md_type in ('UPDATE','DISABLE') and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and nvl(mm_havegetqty,0)+NVL(mm_totaluseqty,0)>NVL(md_qty,0)");
		if(rs0.next()){
			if (rs0.getInt("num")>0){
				BaseUtil.showError("序号:" + rs0.getString("detno") + "已领料数+已转领料数大于变更后的需求数量");
			} 
		}				
		
		Check_Commit_before_ALLcheck(mc_id,caller);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{mc_id});
		String sql = "SELECT  * from MakematerialChangeDet left join make on md_makecode=ma_code " +
				"left join product on pr_code=md_prodcode left join makematerial on md_makecode=mm_code" +
				" and mm_detno=md_mmdetno WHERE md_mcid="+mc_id+" and NVL(md_didstatus,' ')<>'执行成功' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭'  order by md_detno";
		Object mcCode = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_code", "mc_id="+mc_id); 
		String[] detno ;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		String mmcode =null;
		Float oneuseqty,maqty,mmqty,balance;
		while(sqlRowList.next()){
			mmcode = sqlRowList.getString("md_makecode");
			oneuseqty = sqlRowList.getFloat("md_oneuseqty");
			maqty = sqlRowList.getFloat("ma_qty");
			mmqty = sqlRowList.getFloat("md_qty");
			balance = mmqty - oneuseqty * maqty;
			balance = balance<0?0:balance;
			Object ma_id = baseDao.getFieldDataByCondition("make", "ma_id", "ma_code='"+mmcode+"'");
			//在制造ECN审核的工单用料业务处理中，对这种有BOM，并且等级不是外购件bom的,跳层BOM,虚拟件
			boolean ifvirtualorpast = false;
		    int bo_id = 0;
		    int mp_detno =0;
		    //判断是否虚拟件
			String v_pr_code = baseDao.queryForObject("select pr_code from product where pr_code=? and pr_supplytype='VIRTUAL'",String.class,sqlRowList.getString("md_prodcode"));
			//如果是虚拟件判断是否启用，虚拟件必须建立BOM
			ifvirtualorpast = StringUtil.hasText(v_pr_code)?true:false;
			//判断有BOM
			SqlRowList rsbomdata = baseDao.queryForRowSet("select bo_id,nvl(bo_ispast,0) bo_ispast from bom where bo_mothercode=? and bo_statuscode='AUDITED' AND bo_level not in (select bl_code from bomlevel where bl_code='外购件BOM' OR bl_ifpurchase<>0)",sqlRowList.getString("md_prodcode"));
			if(rsbomdata.next()){
				bo_id = rsbomdata.getInt("bo_id");
				ifvirtualorpast = (rsbomdata.getInt("bo_ispast")!=0 || ifvirtualorpast)?true:false;
			}
			
			if(("ADD").equals(sqlRowList.getString("md_type"))){
				int mm_topbomid = sqlRowList.getInt("md_bomid")==-1 ? 0:sqlRowList.getInt("md_bomid");
				gcount = baseDao.getCount("select mm_detno from makematerial where mm_code='"+mmcode+"' and nvl(mm_topbomid,0)="+ mm_topbomid +" and mm_qty>0 and mm_oneuseqty>0 and mm_prodcode='"+sqlRowList.getString("md_prodcode")+"' and mm_stepcode='"+sqlRowList.getString("md_newstepcode")+"'");
				if (gcount==0){			
					detno = baseDao.getStringFieldsDataByCondition("makematerial", new String[]{"NVL(max(mm_detno),0)","max(mm_wccode)"}, "mm_code='"+mmcode+"'");
					String mm_materialstatus = "",stepcode ="";
					int dn = Integer.valueOf(Integer.valueOf(detno[0].toString())+1);
					if(ifvirtualorpast){//跳层BOM
						mm_materialstatus = "JUMP";
					}
					stepcode = sqlRowList.getGeneralString("md_newstepcode");
					if(stepcode==null || "".equals(stepcode)){
						//获取bo_stepcode
						Object step = baseDao.getFieldDataByCondition("bomdetail", "bd_stepcode", "bd_bomid="+mm_topbomid+" and bd_soncode='"+sqlRowList.getString("md_prodcode")+"'");
						if(step != null && !"".equals(stepcode)){
							stepcode = step.toString();
						}
					}
					int mm_id=baseDao.getSeqId("makematerial_SEQ");
					sql = "insert into makematerial(mm_code,mm_detno,mm_prodcode,mm_oneuseqty,mm_qty,mm_havegetqty," +
							"mm_topbomid,mm_supplytype,mm_wccode,mm_whcode,mm_remark,mm_balance,mm_id,mm_maid,mm_materialstatus,mm_bomid,mm_stepcode)values('"
							+mmcode+"','"+dn+"','"+sqlRowList.getString("md_prodcode")+"','"+oneuseqty+"','"+mmqty+"','0','"+mm_topbomid+
							"','"+sqlRowList.getString("pr_supplytype")+"','"+detno[1]+"','"+sqlRowList.getString("pr_whcode")+"','"+"增加,ECN:"+mcCode+"','"+balance+"','"+
							mm_id+"','"+sqlRowList.getInt("ma_id")+"','"+mm_materialstatus+"','"+(ifvirtualorpast?bo_id:0)+"','"+stepcode+"')";				
					baseDao.execute(sql);
					
					if(ifvirtualorpast && bo_id>0){//跳层BOM的子件
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("ma_id", sqlRowList.getInt("ma_id"));
						map.put("topbomid",bo_id);
						map.put("md_qty", mmqty);
						map.put("maqty", maqty);
						map.put("wccode", detno[1]);
						map.put("mmcode", mmcode);
						map.put("mccode", mcCode);
						addMakeMaterialByBom(map);
					}
					sql = "update MakematerialchangeDet set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
					if(sqlRowList.getString("md_repcode")!=null && !"".equals(sqlRowList.getString("md_repcode"))){
						mp_detno++;
						String sql0 = "insert into makematerialreplace(mp_mmid,mp_maid,mp_mmcode,mp_mmdetno,mp_prodcode,mp_canuseqty,mp_whcode,mp_detno)"+
								" values ('"+mm_id+"','"+sqlRowList.getInt("ma_id")+"','"+mmcode+"','"+dn+"','"+sqlRowList.getString("MD_REPCODE")+"','"+0+"','"+sqlRowList.getString("pr_whcode")+"','"+mp_detno+"')";
						baseDao.execute(sql0);
						sql0 = "update makematerial set mm_ifrep=-1,mm_repprodcode='"+sqlRowList.getString("md_repcode")
								+"' where mm_id='"+mm_id+"' and mm_detno='"+dn+"'";
						baseDao.execute(sql0);
					}
					//使用md_edid去查询对应ECN单据的增加替代料数据  插入到MakeMaterialreplace
					Object ed_ecnid = baseDao.getFieldDataByCondition("ecndetail", "ed_ecnid", "ed_id="+sqlRowList.getInt("md_edid"));
					if (!"".equals(ed_ecnid)&& ed_ecnid!=null){
						SqlRowList rs = baseDao.queryForRowSet("select * from ecndetail where ed_ecnid='"+ed_ecnid+"' and ed_soncode='"+sqlRowList.getString("md_prodcode")+"' and nvl(ed_type,' ')='REPADD' and nvl(ed_didstatus,' ')='已执行'");
						while(rs.next()){
							mp_detno++;
							String sql0 = "insert into makematerialreplace(mp_mmid,mp_maid,mp_mmcode,mp_mmdetno,mp_prodcode,mp_canuseqty,mp_whcode,mp_detno)"+
									" values ('"+mm_id+"','"+sqlRowList.getInt("ma_id")+"','"+mmcode+"','"+dn+"','"+rs.getString("ed_repcode")+"','"+0+"','"+sqlRowList.getString("pr_whcode")+"','"+mp_detno+"')";
							baseDao.execute(sql0);
							baseDao.execute("update makematerialreplace set (mp_warehouseid,mp_whcode)=(select wh_id,pr_whcode from product left join Warehouse on pr_whcode=wh_code where pr_code=mp_prodcode) where mp_mmid="+sqlRowList.getInt("mm_id")+" and mp_detno="+mp_detno);
							sql0 = "update makematerial set mm_ifrep=-1,mm_repprodcode=(select wm_concat(mp_prodcode) from makematerialreplace where mp_mmid=mm_id) where mm_id='"+mm_id+"' and mm_detno='"+dn+"'";
							baseDao.execute(sql0);
						}
					}
				}else{
					sql = "update MakematerialchangeDet set md_didstatus='执行失败' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
				}
			} else if(("DISABLE").equals(sqlRowList.getString("md_type"))){ 				
				if(sqlRowList.getInt("mm_detno") > 0 && sqlRowList.getString("mm_prodcode").equals(sqlRowList.getString("md_prodcode"))){
					detno = baseDao.getStringFieldsDataByCondition("makematerial",new String[]{"mm_oneuseqty","mm_qty"},"mm_code='"+mmcode
							+"' and mm_detno='"+sqlRowList.getString("md_mmdetno")+"' ");
					sql = "update MakematerialchangeDet set md_oldoneuseqty=" + detno[0]+ " ,md_oldqty=" +detno[1] + 
							" where md_mcid='"+mc_id+"' and md_detno='"+sqlRowList.getString("md_detno")+"'";
					//ADD 20161228禁用增加更新mm_updatetype='D',
					String SQLStr = "update makematerial set mm_oneuseqty=0 ,mm_qty=0,mm_remark=mm_remark||'禁用,ECN:"+mcCode+"',mm_updatetype='D',mm_updatedate=sysdate where mm_maid='"
							+ma_id+"' and mm_prodcode='" + sqlRowList.getString("md_prodcode") + "' and mm_detno=" + sqlRowList.getInt("md_mmdetno");
					baseDao.execute(sql);
					baseDao.execute(SQLStr);
					if(ifvirtualorpast && bo_id>0){//禁用虚拟跳层BOM展开子件
						disableBom(Integer.valueOf(ma_id.toString()),Integer.valueOf(bo_id),mcCode.toString(),0);
					}
					sql = "update MakematerialchangeDet set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
				}else{
					sql = "update MakematerialchangeDet set md_didstatus='执行失败' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
				}				
			} else if(("UPDATE").equals(sqlRowList.getString("md_type"))){
				gcount = baseDao.getCount("select mm_detno from makematerial where mm_code='" + mmcode + "' and mm_detno=" + sqlRowList.getString("md_mmdetno") + " and mm_prodcode='"+sqlRowList.getString("md_prodcode")+"'");
				if (gcount>0){
					detno = baseDao.getStringFieldsDataByCondition("makematerial",new String[]{"mm_oneuseqty","mm_qty"},"mm_code='"+mmcode
							+"' and mm_detno='"+sqlRowList.getString("md_mmdetno")+"' ");
					sql = "update MakematerialchangeDet set md_oldoneuseqty=" + detno[0]+ " ,md_oldqty=" +detno[1] + 
							" where md_mcid='"+mc_id+"' and md_detno='"+sqlRowList.getString("md_detno")+"'";
					baseDao.execute(sql);
					sql = "update makematerial set mm_balance="+balance+", mm_oneuseqty="+oneuseqty+ " ,mm_qty="+mmqty+ ",mm_remark=mm_remark||'修改,ECN:" +mcCode
							+"' where mm_maid='"+ma_id+"' and mm_prodcode='"+sqlRowList.getString("md_prodcode")+"' and mm_detno="+sqlRowList.getInt("md_mmdetno");
					baseDao.execute(sql); 
					sql = "update MakematerialchangeDet set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
				}else{
					sql = "update MakematerialchangeDet set md_didstatus='执行失败' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
					baseDao.execute(sql);
				}
			} else if(("REPADD").equals(sqlRowList.getString("md_type"))){
				int count = baseDao.getCount("select nvl(max(mp_detno),0) from makematerialreplace where mp_mmid="+sqlRowList.getInt("mm_id"));
				count++;
				baseDao.execute("insert into makematerialreplace(mp_mmid,mp_maid,mp_mmcode,mp_mmdetno,mp_detno,mp_prodcode,mp_canuseqty,mp_remark)"
						+" values ("+sqlRowList.getInt("mm_id")+","+sqlRowList.getInt("mm_maid")+",'"+sqlRowList.getString("mm_code")+"','"+sqlRowList.getInt("mm_detno")+"',"+count+",'"+sqlRowList.getString("md_repcode")+"',0,'"+mcCode.toString()+"')") ;
				baseDao.execute("update makematerialreplace set (mp_warehouseid,mp_whcode)=(select wh_id,pr_whcode from product left join Warehouse on pr_whcode=wh_code where pr_code=mp_prodcode) where mp_mmid="+sqlRowList.getInt("mm_id")+" and mp_detno="+count);
				baseDao.execute("update makematerial set mm_ifrep=-1,mm_repprodcode=(select wm_concat(mp_prodcode) from makematerialreplace where mp_mmid=mm_id) where mm_id="+sqlRowList.getInt("mm_id"));
				sql = "update MakematerialchangeDet set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
				baseDao.execute(sql);
			} else if(("REPDISABLE").equals(sqlRowList.getString("md_type"))){
				baseDao.execute("delete from makematerialreplace where mp_mmid ="+sqlRowList.getString("mm_id")+" and mp_prodcode='"+sqlRowList.getString("md_repcode")+"'");
				SqlRowList rowList = baseDao.queryForRowSet("select count(*) cn ,wm_concat(mp_prodcode) mp_prodcode from makematerialreplace where mp_mmid="+sqlRowList.getInt("mm_id"));
				if(rowList.next()&&rowList.getInt("cn")==0){
					baseDao.execute("update makematerial set mm_ifrep=0,mm_repprodcode='' where mm_id="+sqlRowList.getInt("mm_id"));
				}else if(rowList.next()&&rowList.getInt("cn")>0){
					baseDao.execute("update makematerial set mm_ifrep=-1,mm_repprodcode='"+rowList.getString("mp_prodcode")+"' where mm_id="+sqlRowList.getInt("mm_id"));
				}
				baseDao.logger.others("删除禁用替代料,料号："+sqlRowList.getString("md_repcode"), "删除成功", "Make!Base", "ma_id", sqlRowList.getInt("mm_maid"));
				baseDao.logger.others("删除禁用替代料,料号："+sqlRowList.getString("md_repcode"), "删除成功", "Make", "ma_id", sqlRowList.getInt("mm_maid"));
				sql = "update MakematerialchangeDet set md_didstatus='执行成功' where md_mcid='" + mc_id + "' and md_detno=" + sqlRowList.getInt("md_detno");
				baseDao.execute(sql);
			}
		}
		String ids = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ma_id) from (select distinct ma_id from MakeMaterialChangeDet left join make on ma_code=md_makecode where md_mcid=?)", String.class, mc_id);
		if(ids != null){
			makeDao.updateMakeGetStatus(ids);
		}
		//执行审核操作
		baseDao.audit("MakeMaterialChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode", "mc_auditdate", "mc_auditman");		
		//记录操作
		baseDao.logger.audit(caller, "mc_id", mc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{mc_id});
	}
	@Override
	public void resAuditMakeMaterialChange(int mc_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("MakeMaterialChange", "mc_statuscode='ENTERING',mc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mc_id=" + mc_id);
		//记录操作
		baseDao.logger.resAudit(caller, "mc_id", mc_id);
	}
	@Override
	public void submitMakeMaterialChange(int mc_id, String caller) {
		SqlRowList rs0;
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update MakeMaterialChangeDet set md_type='ADD' where md_mcid=" + mc_id + " and md_type='增加'");
		baseDao.execute("update MakeMaterialChangeDet set md_type='UPDATE' where md_mcid=" + mc_id + " and md_type='修改'");
		baseDao.execute("update MakeMaterialChangeDet set md_type='DISABLE' where md_mcid=" + mc_id + " and md_type='禁用'");
		//@add 2017-11-25 问题反馈2017110400 ,更新工单已完工数至明细表字段md_oldmadeqty
		baseDao.execute("update MakeMaterialChangeDet set md_oldmadeqty=(select ma_madeqty from make where ma_code=md_makecode and nvl(ma_madeqty,0)>0) where exists (select 1 from make where ma_code=md_makecode and nvl(ma_madeqty,0)>0)");	
		rs0 = baseDao.queryForRowSet("select count(1) cn ,wm_concat(md_detno) as md_detno from MakeMaterialChangeDet left join product on pr_code=md_prodcode left join productkind on pr_kind=pk_name where md_mcid="+mc_id+
                    " and md_type in('ADD','UPDATE') and NVL(md_didstatus,' ')<>'已取消' and (nvl(md_oneuseqty,0)=0 OR nvl(md_qty,0)=0)and nvl(pk_ifzeroqty,0)=0  and "+
                    " NVL(pr_kind3,' ') not in (select pk_name from productkind where nvl(pk_ifzeroqty,0)<>0 )");
		if(rs0.next() && rs0.getInt("cn") > 0){
			BaseUtil.showError("序号:"+rs0.getString("md_detno")+"新单位用量和新制单需求量不允许为空或0");
		}
		//禁用的新需求数和新用量更新成0
		baseDao.execute("update MakematerialchangeDet set md_qty=0,md_oneuseqty=0 where md_mcid='"+mc_id+"' and md_type in ('DISABLE','禁用') and (md_qty<>0 or md_oneuseqty<>0)");		
		rs0 = baseDao.queryForRowSet("SELECT md_makecode,ma_id,ma_status from MakematerialChangeDet left join make on md_makecode=ma_code where md_mcid='" + mc_id + "' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and (ma_id is null or ma_statuscode='FINISH') ");
		while(rs0.next()){
			if (rs0.getInt("ma_id")<=0){
				BaseUtil.showError("制造单:" + rs0.getString("md_makecode") + "不存在");
			}else{
				BaseUtil.showError("制造单:" + rs0.getString("md_makecode") + "状态:" + rs0.getString("ma_status") + ",不能变更");
			} 
			if (rs0.getInt("ma_id")>0){ 
				makeDao.refreshTurnQty(rs0.getInt("ma_id"),0); 
			} 
		}
		Check_Commit_before_ALLcheck(mc_id,caller);		
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{mc_id});
		//执行提交操作
		baseDao.submit("MakeMaterialChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "mc_id", mc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{mc_id});
	}
	@Override
	public void resSubmitMakeMaterialChange(int mc_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterialChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resSubmitOnlyCommited(status);
		//增加限制2018060 如果存在执行成功的明细行，单据不允许反审核
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(md_detno) from MakeMaterialChangedet where md_mcid=? and md_didstatus='执行成功'", String.class,mc_id);
		if (dets != null) {
			BaseUtil.showError("存在执行成功的明细行，不允许反提交单据！行号"+dets);
		}
		handlerService.beforeResSubmit(caller, new Object[]{mc_id});		//执行反提交操作
		baseDao.updateByCondition("MakeMaterialChange", "mc_statuscode='ENTERING',mc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mc_id=" + mc_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.afterResSubmit(caller, new Object[]{mc_id});
	}
	@Override
	public void updateMakeMaterialChangeInProcss(String formStore,String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});	
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeMaterialChangeDet", "md_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEMATERIALDETCHANGEDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeMaterialChangeDet", new String[]{"md_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}
	
	@Override
	public void MakeMaterialChangeCloseDet(int id, String caller) { 
		String SQLStr = "";
		SqlRowList rs;
		int mc_id=0;
		SQLStr = "SELECT mc_code,md_id,md_detno,md_mcid,mc_statuscode,md_didstatus from makematerialchange,makematerialchangedet where mc_id=md_mcid and md_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			mc_id=rs.getInt("md_mcid");
			if(rs.getString("mc_statuscode").equals("AUDITED") ){
				BaseUtil.showError("不能操作已审核的单据");
				return;
			}
			if (rs.getString("md_didstatus")!=null && rs.getString("md_didstatus").equals("执行成功")){
				BaseUtil.showError("不能操作已执行成功的明细行");
				return;
			}
			if (rs.getObject("md_didstatus")!=null && rs.getObject("md_didstatus").equals("已取消")) {
				BaseUtil.showError("当前行已处于[已取消]状态");
			}  
			baseDao.updateByCondition("makematerialchangedet", "md_didstatus='已取消'", "md_id=" + id); 
			// 记录操作
			baseDao.logger.getMessageLog("转取消执行,行号:"+rs.getInt("md_detno"), "明细行取消执行成功", caller, "mc_id", mc_id);
		}  
	}
	@Override
	public void MakeMaterialChangeOpenDet(int id, String caller) { 
		String SQLStr = "";
		SqlRowList rs;
		int mc_id=0;
		SQLStr = "SELECT mc_code,md_id,md_detno,md_mcid,mc_statuscode,md_didstatus from makematerialchange,makematerialchangedet where mc_id=md_mcid and md_id="+id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			mc_id=rs.getInt("md_mcid");
			if(rs.getString("mc_statuscode").equals("AUDITED") ){
				BaseUtil.showError("不能操作已审核的单据");
				return;
			}
			if (rs.getString("md_didstatus")!=null && rs.getString("md_didstatus").equals("执行成功")){
				BaseUtil.showError("不能操作已执行成功的明细行");
				return;
			}
			if (rs.getObject("md_didstatus")!=null && !rs.getObject("md_didstatus").equals("已取消")) {
				BaseUtil.showError("只能操作[已取消]的明细行");
			}  			
			baseDao.updateByCondition("makematerialchangedet", "md_didstatus='待执行'", "md_id=" + id); 
			// 记录操作
			baseDao.logger.getMessageLog("转待执行,行号:"+rs.getInt("md_detno"), "明细行待执行成功", caller, "mc_id", mc_id); 
		}  
			 
	}
	
	private void Check_Commit_before_ALLcheck( int id,String  caller){
		SqlRowList rs0;
		//@add 2017-11-25 问题反馈2017110400 ,限制已完工的工单不允许提交审核，如果是完工后允许继续发料及更改用料启用该参数配置，则不限制是否完工
		if(!baseDao.isDBSetting("Make!Base", "allowChangeAfterCom")){
			rs0 = baseDao.queryForRowSet("select count(1) cn,wm_concat(md_detno) detno  from MakeMaterialChangeDet where md_mcid=? and exists (select 1 from make where ma_code=md_makecode and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and nvl(ma_madeqty,0)>=ma_qty) and rownum<20",id);		
			if(rs0.next()){
				if(rs0.getInt("cn")>0)
				   BaseUtil.showError("序号["+rs0.getString("detno")+"],工单已经完工");
			}		
		}
		//变更类型为“新增”时，需要限制新增的物料不在制造单用料表中存在，如果分别属于不同BOM的子件是可以新增的,已经手工新增过，再通过ECN转制造ECN新增不允许
		 rs0 = baseDao.queryForRowSet("select count(1) num,wm_concat(md_detno) detno  from MakeMaterialChangeDet  where md_mcid=?  and exists"+
				 " (select 1 from makematerial where mm_code = md_makecode and"+
				 " mm_prodcode=md_prodcode and nvl(md_newstepcode,' ')=nvl(mm_stepcode,' ') and (mm_topbomid=md_bomid or  NVL(mm_topbomid,0)=0)) "+
				 " and md_type in('ADD','增加') and NVL(md_didstatus,' ')<>'已取消' "+
				 " and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'",id);		
		if(rs0.next()){
			if(rs0.getInt("num")>0)
			   BaseUtil.showError("序号["+rs0.getString("detno")+"],新增的物料+工序编号在制造单用料表中已经存在");
		}
		//变更类型为“新增”时，替代料不允许与新增的主料重复 @zjh
		rs0 = baseDao.queryForRowSet("select wm_concat(md_detno) detno,count(1) cn from makematerialchangedet where md_mcid=? and md_type in('ADD','增加') and nvl(md_prodcode,' ')<>' ' and  md_prodcode=md_repcode and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and rownum<20",id);
		if(rs0.next()){
			if(rs0.getInt("cn")>0){
			   BaseUtil.showError("序号["+rs0.getString("md_detno")+"],替代料不允许与新增的主料重复");
			}
		}
		//禁用，修改时，必须工单序号不允许为空
		rs0 = baseDao.queryForRowSet("SELECT min(md_detno) detno,wm_concat(md_detno) as md_detno from MakematerialChangeDet WHERE md_mcid=" + id+" and md_type in('UPDATE','修改','DISABLE','禁用') and nvl(md_mmdetno,0)=0 and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
		if(rs0.next()){
			if (rs0.getInt("detno")>0){
				BaseUtil.showError("序号:" + rs0.getString("detno") + "禁用或者修改变更类型的工单序号不允许为空");
			} 
		}
		//@zjh start 2017080537
		//【变更类型】为“禁用替代料”的，如果替代料已经领料或者部分领料，则限制提交并提示
		rs0 = baseDao.queryForRowSet("select count (1) cn, wm_concat(md_detno) as md_detno from makematerialchangedet left join makematerial on mm_code = md_makecode and md_mmdetno= mm_detno"
				+ " left join makematerialreplace on mp_mmid = mm_id where md_mcid="+id+" and md_type='REPDISABLE' and (nvl(mp_repqty,0)>0 or nvl(mp_haverepqty,0)>0) and nvl(md_didstatus,'待执行')='待执行'");
		if(rs0.next() && rs0.getInt("cn")>0){
			BaseUtil.showError("替代料已经领料或者已转领料,限制提交!序号:" + rs0.getString("md_detno"));
		}
		
		//增加替代料”的，如果替代料已经在替代料表中，限制提交并提示
		if(!baseDao.isDBSetting("BOM", "checkRepProd")){
			//替代料和主料相同
			rs0 = baseDao.queryForRowSet("select count (1) cn, wm_concat(md_detno) as md_detno from makematerialchangedet left join makematerial on mm_code = md_makecode and md_mmdetno= mm_detno"
					+ " where md_mcid="+id+" and md_type='REPADD' and nvl(md_didstatus,'待执行')='待执行'"
					+ " and exists (select * from makematerial A where A.mm_code=md_makecode and A.mm_prodcode = md_repcode)");
			if(rs0.next() && rs0.getInt("cn")>0){
				BaseUtil.showError("替代料和主料相同,限制提交!序号:" + rs0.getString("md_detno"));
			}
		}
		
		rs0 = baseDao.queryForRowSet("select count (1) cn, wm_concat(md_detno) as md_detno from makematerialchangedet"
				+ " left join makematerial on mm_code = md_makecode and md_mmdetno= mm_detno"
				+ " where md_mcid="+id+" and md_type='REPADD' and nvl(md_didstatus,'待执行')='待执行' and Exists (select * from makematerialreplace where mp_mmid = mm_id and md_repcode=mp_prodcode)");
		if(rs0.next() && rs0.getInt("cn")>0){
			BaseUtil.showError("替代料已经存在工单,限制提交!序号:" + rs0.getString("md_detno"));
		}
		
		//@zjh end  
		//判断明细变更行是否跳层物料，跳层的不允许修改
		rs0 = baseDao.queryForRowSet("SELECT min(md_detno) detno,wm_concat(md_detno) as md_detno from MakematerialChangeDet left join make on ma_code=md_makecode "
				+ "left join makematerial on mm_code=md_makecode"
				+ " and mm_detno=md_mmdetno WHERE md_mcid=" + id+" and md_type in('UPDATE','修改') and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and nvl(mm_materialstatus,' ')<>' ' ");
		if(rs0.next()){ 
			if (rs0.getInt("detno")>0){
				BaseUtil.showError("序号:" + rs0.getString("detno") + "是跳层物料,不能修改");
			} 
		}				
		//变更类型为“禁用”时，提交时有已转数限制提交
		rs0 = baseDao.queryForRowSet(" SELECT count (1) cn ,wm_concat(md_detno) as md_detno from MakematerialChangeDet left join  makematerial on md_makecode=mm_code and md_mmdetno=mm_detno where md_mcid='" + id + "'"
                                     +"  and  md_type in('DISABLE','禁用')  and (NVL(mm_totaluseqty,0)>0 OR nvl(mm_havegetqty,0)>0) and  NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
		if(rs0.next()){
			if (rs0.getInt("cn")>0){
				BaseUtil.showError("类型为禁用的制造单物料存在已转数或已领数,限制提交!序号:" + rs0.getString("md_detno"));
			} 
		}
		//判断makematerial中mm_topbomid等于该BOMID的物料是否有已转数，有则不能提交
		rs0 = baseDao.queryForRowSet("select count (1) cn ,wm_concat(distinct md_detno) as md_detno from MakeMaterialChangeDet left join make on ma_code=md_makecode "+
						"left join bom on bo_mothercode=md_prodcode left join makematerial on mm_maid=ma_id and mm_topbomid=bo_id "+
						"where md_mcid="+id+" and md_type in('DISABLE','禁用') and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and NVL(mm_havegetqty,0)+NVL(mm_totaluseqty,0)>0 ");
		if(rs0.next() && rs0.getInt("cn")>0){
			BaseUtil.showError("变更物料等于制造单用料表中顶层BOM的物料存在已转数,限制提交!序号:" + rs0.getString("md_detno"));
		}	
	    rs0 = baseDao.queryForRowSet("select mm_bomid,mm_id,md_detno,mm_maid from MakeMaterialChangeDet left join bom on bo_mothercode=md_prodcode left join makematerial on md_makecode=mm_code and mm_topbomid=bo_id "+
				"where md_mcid="+id+" and md_type in('DISABLE','禁用') and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭'  and NVL(md_didstatus,' ')<>'执行成功' and nvl(mm_bomid,0)>0 ");
		while(rs0.next()){
			   checkDisableQty(rs0.getInt("mm_bomid"),rs0.getInt("md_detno"),rs0.getInt("mm_maid"),0);		
		}		
		//变更类型为“修改”时，提交时限制新制单需求量不能小于 已领数量+已转数量-报废数
		rs0 = baseDao.queryForRowSet(" SELECT count (1) cn ,wm_concat(md_detno) as md_detno from MakematerialChangeDet left join  makematerial on md_makecode=mm_code  and md_mmdetno=mm_detno where md_mcid='" + id + "'"
                +"  and  md_type in('UPDATE','修改') and NVL(md_qty,0)<(NVL(mm_havegetqty,0)+NVL(mm_totaluseqty,0)-NVL(mm_scrapqty,0)) and  NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
         if(rs0.next()){
              if (rs0.getInt("cn")>0){
                 BaseUtil.showError("新制单需求量不能小于 （已领数量+已转数量-报废数）!序号:" + rs0.getString("md_detno"));
                } 
           }	
       //为“新增”、“修改”时，提交时提示新制单需求量是否小于制造单数量*新单位用量
 		rs0 = baseDao.queryForRowSet("select count(1) cn, wm_concat(md_detno) as md_detno from  MakematerialChangeDet  left join make  on md_makecode =ma_code where  md_mcid="+id+" and md_qty<((NVL(ma_qty,0)-NVL(ma_madeqty,0))*NVL(md_oneuseqty,0)) and md_type in('ADD','增加','UPDATE','修改')  and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
 		if(rs0.next()){
 			if(rs0.getInt("cn")>0)
 				BaseUtil.showError("序号:" + rs0.getString("md_detno") + "新制单需求量小于(制造单数量-已完工数)*新单位用量");
 		} 
 		String SQLStr = "select md_detno,md_makecode,md_mmdetno from MakeMaterialChangeDet  left join MakeMaterialChange on mc_id =md_mcid where md_mcid="+id+ " and NVL(md_didstatus,' ') in ('待执行',' ')";		
 		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
 		while (rs.next()) { 
 			// 判断是否已经存在本次ECN明细变更的其它未执行ECN
 			SQLStr = "select md_detno, mc_code from MakeMaterialChangeDet  left join MakeMaterialChange on mc_id =md_mcid where mc_id<>"+id+" and  md_makecode='"+rs.getString("md_makecode")+"' and ((md_mmdetno="+rs.getInt("md_mmdetno") 
                          +" and md_mmdetno>0) or md_prodcode='"+ rs.getString("md_prodcode")+"') and  mc_statuscode in ('COMMITED') and  NVL(md_didstatus,' ') in ('待执行',' ') ";
 			rs0 = baseDao.queryForRowSet(SQLStr);
 			if (rs0.next()) {
 				BaseUtil.showError("行号[" + rs.getString("md_detno") + "]已经存在未执行的ECN["+rs0.getString("mc_code")+"]!");
 			} 
 		}   
 		rs0 = baseDao.queryForRowSet("SELECT min(md_detno) detno,wm_concat(md_detno) as md_detno from MakematerialChangeDet left join product on pr_code=md_prodcode where md_mcid='" + id + "' and NVL(pr_code,' ')=' ' and NVL(md_didstatus,' ') in ('待执行',' ')");
 		if(rs0.next()){
 			if (rs0.getInt("detno")>0){
 				BaseUtil.showError("序号:" + rs0.getString("md_detno") + "物料号不正确");
 			} 
 		}
 		rs0 = baseDao.queryForRowSet("SELECT min(md_detno) detno,wm_concat(md_detno) as md_detno from MakematerialChangeDet left join product on pr_code=md_prodcode where md_mcid='" + id + "' and md_type in('ADD','增加') and NVL(pr_specvalue,' ')='NOTSPECIFIC' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功'");
 		if(rs0.next()){
 			if (rs0.getInt("detno")>0){
 				BaseUtil.showError("序号:" + rs0.getString("md_detno") + "是虚拟特征件,不能添加至用料表");
 			} 
 		}
 		rs0 = baseDao.queryForRowSet("select * from (SELECT md_makecode,md_type,md_prodcode,count(1) num, min(md_detno) mindetno,max(md_detno) maxdetno from MakematerialChangeDet  where md_mcid='" + id + "' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' group by md_makecode,md_type,md_prodcode) where num>=2");
 		if(rs0.next()){
 			if (rs0.getInt("detno")>0){
 				BaseUtil.showError("序号:" + rs0.getString("mindetno") + "和"+  rs0.getString("maxdetno") +"变更的工单号和料号相同");
 			} 
 		}
 		rs0 = baseDao.queryForRowSet("select * from (SELECT md_makecode,md_mmdetno,count(1) num, min(md_detno) mindetno,max(md_detno) maxdetno from MakematerialChangeDet  where md_mcid='" + id + "' and NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and md_mmdetno>0 and NVL(md_didstatus,' ')<>'执行成功' group by md_makecode,md_mmdetno) where num>=2");
 		if(rs0.next()){
 			if (rs0.getInt("md_mmdetno")>0){
 				BaseUtil.showError("序号:" + rs0.getString("mindetno") + "和"+  rs0.getString("maxdetno") +"变更的工单号和序号相同");
 			} 
 		}
 		//反馈编号2018030052
 		rs0 = baseDao.queryForRowSet("select wm_concat(md_detno) as md_detno  from MakeMaterialChangeDet left join MakeMaterialChange on mc_id =md_mcid where NVL(md_didstatus,' ')<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' and md_type in ('DISABLE','UPDATE') "
 				+ "and not Exists (select * from makematerial where mm_code = md_makecode and mm_detno = md_mmdetno and md_prodcode = mm_prodcode) and md_mcid=?",id);
		
 		if(rs0.next()){
 			if (rs0.getString("md_detno")!=null){
 				BaseUtil.showError("序号:" + rs0.getString("md_detno") + ",同制造单号+工单序号的物料编号与制造单用料表中的不一致！");
 			} 
 		}
 		
 		//审核前判断工单当前需求数与制造ECN的旧需求数是否一致，不一致不能审核，避除已取消的明细行 
		rs0 = baseDao.queryForRowSet("select count(1) num,wm_concat(md_detno) detno from MakeMaterialChangeDet  left join "+
                                " makematerial on md_makecode=mm_code and mm_detno=md_mmdetno and nvl(md_didstatus,0)<>'已取消' and NVL(md_didstatus,' ')<>'关闭' and NVL(md_didstatus,' ')<>'执行成功' "+
                                 "where nvl(md_oldqty,0)<nvl(mm_qty,0) and md_type='UPDATE' and md_mcid="+id);
		if(rs0.next()){
			if (rs0.getInt("num")>0){
				BaseUtil.showError("序号:" + rs0.getString("detno") + "工单当前需求数与制造ECN的旧需求数不一致");
			} 
		}
	}
		
	
	@Override
	public void MakeMaterialChangeCloseAll(int id, String caller) {		 
		String SQLStr = "";
		SqlRowList rs;
		Object ob = baseDao.getFieldDataByCondition("makematerialchange", "mc_statuscode", "mc_id="+id);
		if(ob != null){
			if(ob.toString().equals("AUDITED")){
				BaseUtil.showError("不能操作已审核的单据");
			}
		}else{
			BaseUtil.showError("单据已删除或不存在!");
		}
		//判断是否存在已取消，执行成功的明细
		int cn = baseDao.getCount("select count(1) from makematerialchangedet where md_mcid="+id +" and nvl(md_didstatus,0) in ('已取消','执行成功')");
		
		SQLStr = "SELECT count (1) cn, wm_concat(md_detno) as detno from makematerialchange,makematerialchangedet where mc_id=md_mcid and mc_id="+id+" and nvl(md_didstatus,0) not in ('已取消','执行成功') and rownum<30" ;
		rs = baseDao.queryForRowSet(SQLStr);				
		if (rs.next() && rs.getInt("cn") > 0 ) {						
			baseDao.updateByCondition("makematerialchangedet", "md_didstatus='已取消'", "md_mcid=" + id+" and nvl(md_didstatus,0) not in ('已取消','执行成功')"); 			
			if(cn >0){
				// 记录操作
				baseDao.logger.getMessageLog("全部取消执行行号["+rs.getString("detno")+"]","执行成功",caller, "mc_id", id);
				BaseUtil.showErrorOnSuccess("行号["+rs.getString("detno")+"],取消执行成功");
			}
		}else{
			BaseUtil.showError("单据不存在需要取消执行的明细行！");
		}		
	}	
	
	private void addMakeMaterialByBom(Map<String,Object> map){
		double baseqty = 0,qty = 0, bal = 0,lossrate;
		int dn = 0;
		double maqty = Double.valueOf(map.get("maqty").toString());
		double md_qty = Double.valueOf(map.get("md_qty").toString());
		int ma_id = Integer.valueOf(map.get("ma_id").toString());
		String mm_code = map.get("mmcode").toString();
		String pr_supplytype;
		SqlRowList rs,rs1;		
		//获取是否启用BOM损耗
		boolean ifusebomlossrate = baseDao.isDBSetting("BOM", "useBOMLossRate");
		rs = baseDao.queryForRowSet("select bd_soncode,bd_id,bd_baseqty,pr_supplytype,pr_whcode,bd_stepcode,nvl(bd_lossrate,0)bd_lossrate,nvl(pr_lossrate,0)pr_lossrate,nvl(pr_precision,0)pr_precision from bomdetail left join Product on bd_soncode=pr_code where bd_bomid="+map.get("topbomid")+" and NVL(bd_usestatus,' ')<>'DISABLE'");
		while(rs.next()){
			Object ob = baseDao.getFieldDataByCondition("makematerial", "NVL(max(mm_detno),0)", "mm_maid="+ma_id);
			if(ob != null){
				dn = Integer.valueOf(ob.toString());
			}
			pr_supplytype = rs.getString("pr_supplytype");
			if(ifusebomlossrate){
				lossrate = rs.getDouble("bd_lossrate");
			}else{
				lossrate = rs.getDouble("pr_lossrate");
			}
			dn++;			
			baseqty = rs.getGeneralDouble("bd_baseqty");
			qty = Math.round((md_qty*baseqty* (1 + lossrate * 0.01) + 0.4999*Math.pow(0.1,rs.getInt("pr_precision")))*Math.pow(10,rs.getInt("pr_precision")))/Math.pow(10,rs.getInt("pr_precision"));
			bal = NumberUtil.sub(qty,baseqty * maqty);
			bal = bal<0?0:bal;
			int mm_id = baseDao.getSeqId("makematerial_SEQ");
			
			//在制造ECN审核的工单用料业务处理中，对这种有BOM，并且等级不是外购件bom的,跳层BOM,虚拟件
			boolean ifvirtualorpast = false;
		    int bo_id = 0;
		    //判断是否虚拟件,
			ifvirtualorpast = "VIRTUAL".equals("pr_supplytype")?true:false;
			//判断有BOM
			SqlRowList rsbomdata = baseDao.queryForRowSet("select bo_id,nvl(bo_ispast,0) bo_ispast from bom where bo_mothercode=? and bo_statuscode='AUDITED' AND bo_level not in (select bl_code from bomlevel where bl_code='外购件BOM' OR bl_ifpurchase<>0)",rs.getString("bd_soncode"));
			if(rsbomdata.next()){
				bo_id = rsbomdata.getInt("bo_id");
				ifvirtualorpast = (rsbomdata.getInt("bo_ispast")!=0 || ifvirtualorpast)?true:false;
			}
			baseDao.execute("insert into makematerial(mm_code,mm_detno,mm_prodcode,mm_oneuseqty,mm_qty,mm_havegetqty,mm_topbomid," +
					"mm_supplytype,mm_wccode,mm_whcode,mm_remark,mm_balance,mm_id,mm_maid,mm_stepcode,mm_materialstatus,mm_bomid)values('"
					+mm_code+"','"+dn+"','"+rs.getString("bd_soncode")+"','"+baseqty+"','"+qty+"','0','"+map.get("topbomid")+"'," +
					"'"+pr_supplytype+"','"+map.get("wccode")+"','"+rs.getString("pr_whcode")+"','增加,ECN:"+map.get("mccode")+"','"+bal+"'," +
					mm_id+",'"+ma_id+"','"+rs.getGeneralString("bd_stepcode")+"','"+(ifvirtualorpast?"JUMP":"")+"',"+(ifvirtualorpast?bo_id:0)+")");			
			//取替代料
			rs1 = baseDao.queryForRowSet("SELECT distinct pre_repcode FROM ProdReplace left join product on pr_code=pre_repcode WHERE pre_bdid="+rs.getInt("bd_id")+" AND NVL(pre_repcode,' ')<>' ' AND NVL(pre_statuscode, ' ') <> 'DISABLE' AND pr_statuscode='AUDITED' AND pre_repcode<>'"+rs.getString("bd_soncode")+"'");			
			int dt = 1;
			while(rs1.next()){
				baseDao.execute("INSERT INTO MakeMaterialReplace(mp_mmid,mp_detno,mp_prodcode,mp_rate,mp_mmdetno,mp_mmcode,mp_maid) " +
						"VALUES("+mm_id+","+dt+",'"+rs1.getString("pre_repcode")+"',1,"+dn+",'"+mm_code+"',"+ma_id+")");
				if(dt == 1){
					baseDao.execute("update makematerial set mm_repprodcode='"+rs1.getString("pre_repcode")+"' where mm_id ="+mm_id);
				}else{
					baseDao.execute("update makematerial set mm_repprodcode=nvl(mm_repprodcode,' ')||';'||"+rs1.getString("pre_repcode")+" where mm_id ="+mm_id);
				}
				dt++;
			}
			//如果子件包含BOM并且是虚拟件或者跳层物料
			if(ifvirtualorpast && bo_id>0){//展开子件
				map.put("topbomid", bo_id);
				map.put("md_qty", qty);
				map.put("maqty", qty);
				addMakeMaterialByBom(map);
			}
		}
	}
	
	public void checkDisableQty (int bo_id,int detno,int mm_maid,int num){
		if(num>15){ //add 20170310 BOM层级最多15层，超过就是默认数据有问题出现循环嵌套
			BaseUtil.showError("用料表半成品出现BOM循环嵌套，限制提交！序号["+detno+"]");
		}
		//调整物料禁用，加循环判断已领数+已转数大于0		
		SqlRowList rs0 = baseDao.queryForRowSet("select count(*)cn from makematerial where mm_maid="+mm_maid+" and mm_topbomid="+bo_id+" and nvl(mm_havegetqty,0)+NVL(mm_totaluseqty,0)>0");
		if(rs0.next() && rs0.getInt("cn") > 0){
			BaseUtil.showError("禁用物料展开子件的已领数+已转数大于0，限制提交!序号["+detno+"]");
		}
		rs0 = baseDao.queryForRowSet("select bo_id from makematerial,bom where bo_mothercode=mm_prodcode and mm_maid="+mm_maid+" and mm_topbomid="+bo_id+" and nvl(mm_bomid,0)>0");
		while(rs0.next()){
			checkDisableQty(rs0.getInt("bo_id"),detno,mm_maid,num++);
		}
	}
	
	public void disableBom(int ma_id,int bo_id,String mcCode,int num){
		if(num>15){
			BaseUtil.showError("出现BOM循环嵌套，限制审核!");
		}
		baseDao.execute("update makematerial set mm_oneuseqty=0 ,mm_qty=0,mm_remark=mm_remark||'禁用,ECN:"+mcCode+"'"+
				" where mm_maid='"+ma_id+"' and mm_topbomid='" + bo_id+ "'");
		 SqlRowList rs0 = baseDao.queryForRowSet("select bo_id from makematerial left join bom on bo_mothercode=mm_prodcode where mm_maid="+ma_id+" and mm_topbomid="+bo_id+" and nvl(bo_id,0)>0");
		 while(rs0.next()){ 
			 disableBom(ma_id,rs0.getInt("bo_id"),mcCode,num++);		
		 }	
	}
	
	@Override
	@Transactional
	public String makeMaterialChangeTurnProdIOReturn(int id, String caller) {
		String mc_code = null;
		//只能对状态为未审核的单据进行转
		Object[] mddatas = baseDao.getFieldsDataByCondition("MakeMaterialChange", "mc_statuscode,mc_code", "mc_id=" + id);
		if(mddatas!=null){
			if("AUDITED".equals(mddatas[0])){
				BaseUtil.showError("只能对未审核的单据进行转退料单!");
			}
			mc_code = mddatas[1].toString();
		}
		//如果有已转的退料单则不允许重复转
		SqlRowList rs0 = baseDao.queryForRowSet("select pi_inoutno,pi_class from prodinout where pi_sourcecode=? and pi_class in('生产退料单','委外退料单')",mc_code);
		if(rs0.next()){
			BaseUtil.showError("不允许重复转，已转"+rs0.getString("pi_class")+":"+rs0.getString("pi_inoutno")+"!");
		}
		String piclass,whoami,code = null,ma_code,pr_code,picodestr = "";
		int  pi_id = 0,prid,maid,mmid,mmdetno;
		double thisqty;
		boolean ifrep;
		StringBuffer sb = new StringBuffer();	
		JSONObject j = null;
		
		String insert_detail_r = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_inqty,"
				+ "pd_ordercode, pd_orderdetno, pd_plancode, pd_wccode, pd_orderid, pd_prodid,pd_whcode,pd_whname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// 点击针对明细行状态是待执行或空， 且退仓数>0的生成生产退料单或委外退料单， 如果出现在制数<需退仓数，则不允许转单。		
		// 区分产生生产退料单或委外领料单，产生的生产退料单按（工作中心+仓库），委外退料单按照（委外商+工作中心+仓库）
		piclass = "生产退料单";
		whoami = "ProdInOut!Make!Return";
		String ma_wccode = "",mm_whcode = "";
		int detno = 1;
		SqlRowList rs = baseDao.queryForRowSet("select ma_id,mm_id,mm_havegetqty-NVL(mm_haverepqty,0) maingetqty,ma_wccode,mm_havegetqty-nvl(md_qty,0) canreturnqty,mm_prodcode,mm_detno,ma_code,mm_whcode,pr_id,wh_description from"
				+ " makematerialchangedet left join make on ma_code=md_makecode left join makematerial on mm_maid=ma_id and mm_detno=md_mmdetno left join product on pr_code=md_prodcode left join warehouse on wh_code=mm_whcode"
				+ " where md_mcid=? and nvl(md_didstatus,'待执行')='待执行' and md_oneuseqty<>mm_oneuseqty and nvl(mm_havegetqty,0)>0 and mm_havegetqty-nvl(md_qty,0)>0  and mm_havegetqty-nvl(md_qty,0)>nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-nvl(mm_oneuseqty,0)*ma_qty and ma_tasktype='MAKE' order by ma_wccode,mm_whcode",id);
		while(rs.next()){
			if(rs.getCurrentIndex()==0 || !ma_wccode.equals(rs.getGeneralString("ma_wccode"))|| !mm_whcode.equals(rs.getGeneralString("mm_whcode"))){
				j = makeDao.newProdIO(rs.getString("mm_whcode"),piclass, whoami,"");
				if(j!=null){
					pi_id = j.getInt("pi_id");
					code = j.getString("pi_inoutno");
					detno = 1;
					picodestr += "," + code;
					//--将ECN 单号记录在字段PI_SOURCECODE   备注来源ECN退料
					baseDao.execute("update prodinout set PI_SOURCECODE=? ,pi_remark=? where pi_id=?",mc_code,"来源制造ECN转退料,单号："+mc_code,pi_id);
					if(rs.getCurrentIndex()==0){
						sb.append("转入成功,生产退料单号:");
					}
					sb.append("<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
							+ "</a>&nbsp;<br/>");
				}
			}else{
				detno++;
			}
			if(j!=null){
				ifrep = false;
				mmid = rs.getInt("mm_id");
				mmdetno = rs.getInt("mm_detno");
				maid = rs.getInt("ma_id");
				ma_code = rs.getString("ma_code");
				pr_code = rs.getString("mm_prodcode");
				prid = rs.getInt("pr_id");
				thisqty = rs.getDouble("canreturnqty");
				if(rs.getDouble("maingetqty")==0 || rs.getDouble("maingetqty")<0){
					//取替代料
					SqlRowList rep = baseDao.queryForRowSet("select mp_prodcode,pr_id from MakeMaterialReplace left join product on pr_code=mp_prodcode where mp_mmid=? and mp_haverepqty>0 order by mp_haverepqty desc",mmid);
					if(rep.next()){
						pr_code = rep.getString("mp_prodcode");
						prid = rep.getInt("pr_id");
					}
					ifrep = true;
				}
				baseDao.execute(insert_detail_r, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"),pi_id, code, piclass, detno, 0, "ENTERING",pr_code, thisqty,
						ma_code, mmdetno, null, null, mmid, prid,rs.getString("mm_whcode"),rs.getString("wh_description")});
				if(ifrep){
					//替代料
					baseDao.updateByCondition("MakeMaterialReplace", "mp_backqty=nvl(mp_backqty,0)+" + thisqty, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
				}else{//主料
					baseDao.updateByCondition("MakeMaterial", "mm_backqty=nvl(mm_backqty,0)+" + thisqty, "mm_id=" + mmid);
					makeDao.setThisQty(mmid, null, null);
					//baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED',mm_backqty=nvl(mm_backqty,0) + " + thisqty, "mm_id=" + mmid);
					baseDao.updateByCondition("MakeMaterial", "mm_status='PARTGET'", "mm_id=" + mmid + " AND mm_thisqty > 0");
					makeDao.updateMakeGetStatus(String.valueOf(maid));
				}
			}
			ma_wccode = rs.getGeneralString("ma_wccode");
			mm_whcode = rs.getGeneralString("mm_whcode");			
		}
		
		//转委外退料单
		piclass = "委外退料单";
		whoami = "ProdInOut!OutsideReturn";
		ma_wccode = "";mm_whcode = "";
		String ma_vendcode = "",ma_apvendcode = "";
		rs = baseDao.queryForRowSet("select ma_vendcode,ma_apvendcode,mm_id,mm_havegetqty-NVL(mm_haverepqty,0) maingetqty,ma_wccode,mm_havegetqty-nvl(md_qty,0) canreturnqty,mm_prodcode,mm_detno,ma_code,mm_whcode,wh_description from"
				+ " makematerialchangedet left join make on ma_code=md_makecode left join makematerial on mm_maid=ma_id and mm_detno=md_mmdetno left join warehouse on wh_code=mm_whcode"
				+ " where md_mcid=? and nvl(md_didstatus,'待执行')='待执行' and md_oneuseqty<>mm_oneuseqty and nvl(mm_havegetqty,0)>0 and mm_havegetqty-nvl(md_qty,0)>0 and mm_havegetqty-nvl(md_qty,0)>nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-nvl(mm_oneuseqty,0)*ma_qty and ma_tasktype='OS' order by ma_wccode,mm_whcode,ma_vendcode,ma_apvendcode",id);
		while(rs.next()){
			if(rs.getCurrentIndex()==0 || !ma_wccode.equals(rs.getGeneralString("ma_wccode"))|| !mm_whcode.equals(rs.getGeneralString("mm_whcode"))||!ma_apvendcode.equals(rs.getGeneralString("ma_apvendcode"))||!ma_vendcode.equals(rs.getGeneralString("ma_vendcode"))){
				j = makeDao.newProdIOWithVendor(rs.getString("mm_whcode"),rs.getString("ma_vendcode"),rs.getString("ma_apvendcode"), piclass, whoami,"");
				if(j!=null){
					pi_id = j.getInt("pi_id");
					code = j.getString("pi_inoutno");
					detno = 1;
					picodestr += "," + code;
					//--将ECN 单号记录在字段PI_SOURCECODE   备注来源ECN退料
					baseDao.execute("update prodinout set PI_SOURCECODE=? ,pi_remark=? where pi_id=?",mc_code,"来源制造ECN转退料,单号："+mc_code,pi_id);
					if(rs.getCurrentIndex()==0){
						sb.append("转入成功,委外退料单号:");
					}
					sb.append("<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
							+ "</a>&nbsp;<br/>");
				}
			}else{
				detno++;
			}
			if(j!=null){
				ifrep = false;
				mmid = rs.getInt("mm_id");
				mmdetno = rs.getInt("mm_detno");
				maid = rs.getInt("ma_id");
				ma_code = rs.getString("ma_code");
				pr_code = rs.getString("mm_prodcode");
				prid = rs.getInt("pr_id");
				thisqty = rs.getDouble("canreturnqty");
				if(rs.getDouble("maingetqty")==0 || rs.getDouble("maingetqty")<0){
					//取替代料
					SqlRowList rep = baseDao.queryForRowSet("select mp_prodcode,pr_id from MakeMaterialReplace left join product on pr_code=mp_prodcode where mp_mmid=? and mp_haverepqty>0 order by mp_haverepqty desc",mmid);
					if(rep.next()){
						pr_code = rep.getString("mp_prodcode");
						prid = rep.getInt("pr_id");
					}
					ifrep = true;
				}
				baseDao.execute(insert_detail_r, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"),pi_id, code, piclass, detno, 0, "ENTERING",pr_code, thisqty,
						ma_code, mmdetno, null, null, mmid, prid,rs.getString("mm_whcode"),rs.getString("wh_description")});
				if(ifrep){
					//替代料
					baseDao.updateByCondition("MakeMaterialReplace", "mp_backqty=nvl(mp_backqty,0)+" + thisqty, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
				}else{//主料
					baseDao.updateByCondition("MakeMaterial", "mm_backqty=nvl(mm_backqty,0)+" + thisqty, "mm_id=" + mmid);
					makeDao.setThisQty(mmid, null, null);
					baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED',mm_backqty=nvl(mm_backqty,0) + " + thisqty, "mm_id=" + mmid);
					baseDao.updateByCondition("MakeMaterial", "mm_status='PARTGET'", "mm_id=" + mmid + " AND mm_thisqty > 0");
					makeDao.updateMakeGetStatus(String.valueOf(maid));
				}
			}
			ma_wccode = rs.getGeneralString("ma_wccode");
			mm_whcode = rs.getGeneralString("mm_whcode");	
			ma_vendcode= rs.getGeneralString("ma_vendcode");
			ma_apvendcode = rs.getGeneralString("ma_apvendcode");
		}
		 if(sb.length()>0){
			 //记录日志转退料单
			 baseDao.logMessage(baseDao.logger.getMessageLog("转退料单["+picodestr+"]","转退料单成功",caller, "mc_id", id));
			 return sb.toString();
		 }else{
			 BaseUtil.showError("无需要转退料的数据!");
		 }
		return null;
	}
	  
}
