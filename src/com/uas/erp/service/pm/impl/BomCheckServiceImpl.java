package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BomCheckService;
@Service
public class BomCheckServiceImpl implements BomCheckService {
	@Autowired
	private BaseDao baseDao;
	final static String BE="INSERT INTO BOMMessage(bm_id,bm_mothercode,bm_bomid,bm_item,bm_description,bm_date) VALUES(";

	/**
	 * 通过configs表中的caller找到BOM有效性检测中的检测明细项
	 * @param caller
	 */
	@Override
	public List<Map<String, Object>> getItems(String caller) {
		String sql="select code type ,title value from configs where caller='"+caller+"' and data<>0";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			return rs.getResultList();
		}else 
			return null;
	}

	/**
	 * 清除之前检验记录
	 * 
	 * @param bomId
	 */
	private void clearByBomId(String bomId) {
		baseDao.deleteByCondition("BomMessage", "bm_bomid="+bomId);
	}

	
	@Override
	public String bomCheck(String bomId,String bomMotherCode, String gridStore) {
		clearByBomId(bomId);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		StringBuffer ok=new StringBuffer();
		ok.append('[');
		String errMsg = baseDao.callProcedure("MM_SetProdBomStruct", new Object[] { Integer.valueOf(bomId),null});	
		for(Map<Object, Object> s:gstore){
			if(s.get("TYPE").toString().equals("nestCheck")){
				ok.append(nestCheck(errMsg,bomId,bomMotherCode)+",");						
			}else if(s.get("TYPE").toString().equals("prodExistCheck")){
				ok.append(prodExistCheck(bomId,bomMotherCode)+",");
			}else if(s.get("TYPE").toString().equals("bomStatusCheck")){
				ok.append(bomStatusCheck(bomId,bomMotherCode)+",");
			}else if(s.get("TYPE").toString().equals("baseQtyValidation")){
				ok.append(baseQtyValidation(bomId,bomMotherCode)+",");
			}else if (s.get("TYPE").toString().equals("locationQtyMatch")){
				ok.append(locationQtyMatch(bomId,bomMotherCode)+",");
			}else if (s.get("TYPE").toString().equals("semiFinishedProdCheck")){
				ok.append(semiFinishedProdCheck(bomId, bomMotherCode)+",");
			}else if (s.get("TYPE").toString().equals("bomLevelCheck")){
				ok.append(bomLevelCheck(bomId,bomMotherCode)+",");
			}else if (s.get("TYPE").toString().equals("prodLevelCheck")){
				ok.append(prodLevelCheck(bomId,bomMotherCode)+",");
			}else if (s.get("TYPE").toString().equals("FeatureCheck")){
				ok.append(FeatureCheck(bomId,bomMotherCode));				
			}
			
		}
		ok.append(']');
		return ok.toString();
	}
	
	/**
	 * 物料有效性检测
	 * @param bomId
	 */
	
	public String  prodExistCheck(String bomId,String bomMotherCode) {
		SqlRowList rs;
		String sql="select bs_topmothercode, bs_soncode,bs_bddetno from bomStruct,product where bs_soncode=pr_code AND  bs_topbomid="+bomId+"AND pr_statuscode<>'AUDITED'";
		rs=baseDao.queryForRowSet(sql);
		if(rs.next()){
			StringBuffer sb = new StringBuffer(BE);
			int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
			sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
					.append("物料有效性检测").append("','").append("存在行号："+rs.getInt("bs_bddetno")+" 物料："+rs.getString("bs_soncode")+" 的状态为未审核").append("',sysdate)");
			 baseDao.execute(sb.toString());
			 return  "{\"result\":\"false\",\"type\":\"prodExistCheck\"}";
		}		
		return "{\"result\":\"true\",\"type\":\"prodExistCheck\"}";		
	}

	/**
	 * 单位用量有效
	 * @param bomId
	 */
	
	public String baseQtyValidation(String bomId,String bomMotherCode) {
		boolean bo = true;
		SqlRowList rs, rs0, rs1;
		String sql;
		int count;
		rs1 = baseDao
				.queryForRowSet("select bs_sonbomid,bs_bddetno from bomStruct where bs_topbomid="
						+ bomId+"AND bs_sonbomid <>0");
		if (rs1.next()) {
			rs = baseDao
					.queryForRowSet("select NVL(bo_isextend,0) bo_isextend,NVL(bo_refbomid,0) bo_refbomid,bo_refcode from Bom where bo_id="
							+ rs1.getInt("bs_sonbomid"));
			if (rs.next()) {
				int ex = rs.getInt("bo_isextend");
				int ref = rs.getInt("bo_refbomid");
				String refcode = rs.getString("bo_refcode");
				if (ex != 0 && ref == 0) {
					bo=false;
					StringBuffer sb = new StringBuffer(BE);
					int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
					sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
							.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+"变型BOMID:"+rs1.getInt("bs_sonbomid") +"没有选择原型BOM").append("',sysdate)");
					 baseDao.execute(sb.toString());
				} else if (ex == 0 && ref > 0) {
					bo=false;
					StringBuffer sb = new StringBuffer(BE);
					int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
					sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
							.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+"BOMID:"+rs1.getInt("bs_sonbomid")+" 填写了原型BOMID，但不是变型BOM").append("',sysdate)");
					 baseDao.execute(sb.toString());
				} else if (ex != 0 && ref > 0) {
					int is = baseDao
							.getCount("select count(1) from  Bom where bo_id="
									+ ref + " AND bo_isextend <>0 ");
					if (is > 0) {
						bo=false;
						StringBuffer sb = new StringBuffer(BE);
						int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
						sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
								.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+"BOMID"+rs1.getInt("bs_sonbomid")+"所属的原型BOM的类型为变型BOM").append("',sysdate)");
						 baseDao.execute(sb.toString());

					}
					// 判断序号1是否为原型母件料号
					sql = "select count(1) from bomDetail left join bom on bd_bomid=bo_id where bd_bomid ="
							+ bomId
							+ " AND bd_detno=1 AND  bd_soncode='"
							+ refcode
							+ "'AND bd_baseqty=1 AND NVL(bd_usestatus,' ')<>'DISABLE' ";
					count = baseDao.getCount(sql);
					if (count != 1) {
						bo=false;
						StringBuffer sb = new StringBuffer(BE);
						int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
						sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
								.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+" 变型BOMID:"+rs1.getInt("bs_sonbomid")+"中序号为1的子件不是原型BOM或者单位用量不是1").append("',sysdate)");
	
					}
					// 大于0用量的子件必须在原型BOM中不存在
					sql = "select count(1) c,wm_concat(bd_detno) detno  from BOM ,BOMDETAIL WHERE bo_id=bd_bomid "
							+ " AND bo_id = '"
							+ bomId
							+ "' AND bd_baseqty>0 AND NVL(bd_usestatus,' ')<>'DISABLE' "
							+ " AND bd_soncode in (select bd_soncode from  bomDetail b where  b.bd_bomid='"
							+ ref + "' AND NVL(b.bd_usestatus,' ')<>'DISABLE')";
					rs0 = baseDao.queryForRowSet(sql);
					if (rs0.next()) {
						if (rs0.getInt("c") > 0) {
							bo=false;
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
									.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+" 变型BOMID:"+rs1.getInt("bs_sonbomid")+" 中序号为："+rs0.getString("detno")+"的子件在原型BOM中已经存在").append("',sysdate)");
							 baseDao.execute(sb.toString());
						}
					}
					sql = "select  count(1) c,wm_concat(bd_detno) detno  from bomdetail left join bom on bo_id=bd_bomid where bd_bomid="
							+ bomId
							+ " and bo_refbomid>0 and  bd_baseqty<0 and nvl(bd_usestatus,' ')<>'DISABLE'  "
							+ " AND bd_soncode not in (select bd_soncode from bomdetail b where b.bd_bomid="
							+ ref + " and nvl(b.bd_usestatus,' ')<>'DISABLE' )";
					rs0 = baseDao.queryForRowSet(sql);
					if (rs0.next()) {
						if (rs0.getInt("c") > 0) {
							bo=false;
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
									.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+" 变型BOMID:"+rs1.getInt("bs_sonbomid")+" 中序号为："+rs0.getString("detno")+"的子件用量为负数，但在原型BOM中不存在此料").append("',sysdate)");
							 baseDao.execute(sb.toString());
							
						}
					}
					sql = "select  count(1) c,wm_concat(bd_detno) detno  from bomdetail left join bom on bo_id=bd_bomid where bd_bomid="
							+ bomId
							+ " and bo_refbomid>0 and  bd_baseqty<0 and nvl(bd_usestatus,' ')<>'DISABLE'  "
							+ " AND bd_soncode in (select bd_soncode from bomdetail b where b.bd_bomid="
							+ ref
							+ " and nvl(b.bd_usestatus,' ')<>'DISABLE' and -b.bd_baseqty<>bomdetail.bd_baseqty)";
					rs0 = baseDao.queryForRowSet(sql);
					if (rs0.next()) {
						if (rs0.getInt("c") > 0) {
							bo=false;
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
									.append("单位用量有效").append("','").append("存在行号："+rs1.getInt("bs_bddetno")+" 变型BOMID:"+rs1.getInt("bs_sonbomid")+" 中序号为："+rs0.getString("detno")+"单位用量的绝对值跟原型BOM中用量不一致").append("',sysdate)");
							 baseDao.execute(sb.toString());
						}
					}
				}
			}
		}
		if(bo){
			return "{\"result\":\"true\",\"type\":\"baseQtyValidation\"}";	
		}else{
			return "{\"result\":\"false\",\"type\":\"baseQtyValidation\"}";	
		}
	}

	/**
	 * 位号用量匹配
	 * @param bomId
	 */

	public String locationQtyMatch(String bomId,String bomMotherCode) {
		boolean bo=true;
		SqlRowList rs;
		String SQLStr="";
		String errstr="";				
		// 判断位号是否跟用量一致
		SQLStr = "select bs_bomid,bs_bddetno,bs_soncode,bs_location,bs_baseqty from bomStruct  where bs_topbomid=" + bomId
				+ "  and NVL(bs_location,' ')<>' '  ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			if (rs.getFloat("bs_baseqty") - Math.round(rs.getFloat("bs_baseqty")) == 0) {
				int num=0;
				num=rs.getString("bs_location").split(",").length ;
				if (num!= rs.getInt("bs_baseqty")) {					
					errstr+="<hr>BOM:"+rs.getString("bs_bomid")+"序号:"+rs.getInt("bs_bddetno")+"物料编号为："+rs.getString("bs_soncode")+" ,用量["+rs.getString("bs_baseqty")+"] 位号个数["+num+"]";
				}
			}
		}
		if (!errstr.equals("")){
			StringBuffer sb = new StringBuffer(BE);
			int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
			sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
					.append("位号用量匹配").append("','").append("单位用量和位号个数不匹配"+errstr).append("',sysdate)");
			 baseDao.execute(sb.toString());				 
			 bo=false;
		}
		// 单位用量不能为0
		SQLStr = "select wmsys.wm_concat('序号'||bs_bddetno||'物料编号：'||bs_soncode) as detno,count(*) n from bomStruct left join product on pr_code=bs_soncode left join productkind on pr_kind=pk_name where bs_topbomid=" + bomId
				+ " and NVL(bs_usestatus,' ')<>'DISABLE' and NVL(bs_baseqty,0)=0 "
				+"and ((nvl(pr_xikind,' ')<>' ' and exists (select 1 from productkind pk4 left join productkind pk_sub on pk4.pk_subof = pk_sub.pk_id where pk4.pk_name = pr_xikind and NVL(pk4.pk_ifzeroqty,0)=0 and pk4.pk_level= 4 and pk_sub.pk_name=pr_kind3 and pk_sub.pk_level=3 ) ) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')<>' ' and exists(select 1 from productkind pk3 left join productkind pk_sub on pk3.pk_subof = pk_sub.pk_id where pk3.pk_name =pr_kind3 and NVL(pk3.pk_ifzeroqty,0)=0 and pk3.pk_level= 3 and pk_sub.pk_name=pr_kind2 and pk_sub.pk_level=2)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')<>' ' and exists(select 1 from productkind pk2 left join productkind pk_sub on pk2.pk_subof = pk_sub.pk_id where pk2.pk_name =pr_kind2 and NVL(pk2.pk_ifzeroqty,0)=0 and pk2.pk_level= 2 and pk_sub.pk_name=pr_kind and pk_sub.pk_level=1)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')=' ' and nvl(pr_kind,' ')<> ' ' and exists (select 1 from productkind pk1 where pk1.pk_name=pr_kind and NVL(pk1.pk_ifzeroqty,0)=0 and pk1.pk_level= 1)))";
				rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				errstr=rs.getString("detno") +" 单位用量0,不能提交,只有物料种类中设置了允许BOM零用量的物料才允许用量为0";
				StringBuffer sb = new StringBuffer(BE);
				int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
				sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
						.append("位号用量匹配").append("','").append("单位用量不能为0"+errstr).append("',sysdate)");
				 baseDao.execute( sb.toString());				 
				 bo=false;				 
			}
		}
		
		Map<Object, Object> loc=null;
		String reapstr="";
		// 判断位号是否重复
		SQLStr = "select bs_location,bs_bddetno,bs_soncode from bomStruct where bs_topbomid=" + bomId
				+ " and NVL(bs_usestatus,' ')<>'DISABLE' and NVL(bs_location,' ')<>' '  ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			String[] bdloc=rs.getString("bs_location").split(",");  
			for(String s:bdloc){ 
				if (loc==null){
					loc = new HashMap<Object, Object>();
					loc.put(s, 1);
				}else if(loc.containsKey(s)){
					reapstr+=","+s;
				}else{
					loc.put(s, 1);
				} 
			}  
		}
		if (!reapstr.equals("")) {
			errstr="<hr>序号："+rs.getString("bs_bddetno")+"物料编号："+rs.getString("bs_soncode")+"位号："+reapstr.substring(1)+"出现重复";
			StringBuffer sb = new StringBuffer(BE);
			int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
			sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
					.append("位号用量匹配").append("','").append("位号重复"+errstr).append("',sysdate)");
			 baseDao.execute( sb.toString());				 
			 bo=false;
		}	
		if(bo){
			return "{\"result\":\"true\",\"type\":\"prodExistCheck\"}";	
		}else{
			return "{\"result\":\"false\",\"type\":\"prodExistCheck\"}";	
		}
		
	}
	
	/**
	 * 嵌套检测
	 * @param bomId
	 */

	public String nestCheck(String errMsg, String bomId, String bomMotherCode) {
		if (errMsg != null && !errMsg.equals("")) {
			if (errMsg.indexOf("嵌套") != -1) {
				StringBuffer sb = new StringBuffer(BE);
				int bm_id = baseDao.getSeqId("BOMMESSAGE_SEQ");
				sb.append(bm_id).append(",'").append(bomMotherCode)
						.append("',").append(bomId).append(",'")
						.append("嵌套检测").append("','").append("BOM存在循环嵌套")
						.append("',sysdate)");
				baseDao.execute(sb.toString());
				return "{\"result\":\"false\",\"type\":\"nestCheck\"}";
			}
		}
		return "{\"result\":\"true\",\"type\":\"nestCheck\"}";
	}

	/**
	 * 半成品BOM建立检测
	 * @param bomId
	 */

	public String semiFinishedProdCheck(String bomId,String bomMotherCode) {
		SqlRowList rs0;
		Boolean Check=true;
		String sql="select count(1) num,wm_concat('半成品:'||bs_soncode) detno from bomStruct left join product on bs_soncode=pr_code where bs_topbomid="+bomId+" AND pr_manutype IN('MAKE','OSMAKE') AND bs_sonbomid=0";
		rs0=baseDao.queryForRowSet(sql);
		if(rs0.next()){
			if(rs0.getInt("num")>0){
				StringBuffer sb = new StringBuffer(BE);
				int bm_id = baseDao.getSeqId("BOMMESSAGE_SEQ");
				sb.append(bm_id)
						.append(",'")
						.append(bomMotherCode)
						.append("',")
						.append(bomId)
						.append(",'")
						.append("半成品BOM建立检测")
						.append("','")
						.append(rs0.getString("detno")+"未建立BOM")
						.append("',sysdate)");
				baseDao.execute(sb.toString());
				return "{\"result\":\"false\",\"type\":\"semiFinishedProdCheck\"}";
			}
		}
		return "{\"result\":\"true\",\"type\":\"semiFinishedProdCheck\"}";
	}
	
	/**
	 * BOM 等级检测
	 * @param bomId
	 */
	public String bomLevelCheck(String bo_id, String bomMotherCode) { 
		SqlRowList rs0, rs1;
		Object bolevel;
		int thisbomid=0; 
		Boolean Check=true;
		rs1 = baseDao
				.queryForRowSet("select NVL(bs_soncode,0),bs_sonbomid from bomStruct where bs_topbomid = "
						+ bo_id+" and bs_topmothercode='"+bomMotherCode+"' AND bs_sonbomid <>0");
		while (rs1.next()) {
			thisbomid= rs1.getInt("bs_sonbomid");
			bolevel = baseDao.getFieldDataByCondition("BOM", "bo_level",
					"bo_id=" + thisbomid);
			// 判断子件BOM等级是否到达母件的BOM等级
			int bl_grade=Integer.parseInt(baseDao.getFieldDataByCondition("BOMlevel", "NVL(bl_grade,0)", "bl_code='"+bolevel.toString()+"'").toString());
			rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode left join bom on bo_mothercode=bd_soncode left join bomlevel on bl_code=bo_level where bd_bomid='" + thisbomid+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and bl_grade<" + bl_grade);
			if (rs0.next()) {
				if (rs0.getInt("num") > 0) {
					StringBuffer sb = new StringBuffer(BE);
					int bm_id = baseDao.getSeqId("BOMMESSAGE_SEQ");
					sb.append(bm_id)
							.append(",'")
							.append(bomMotherCode)
							.append("',")
							.append(bo_id)
							.append(",'")
							.append("BOM等级检测")
							.append("','")
							.append("子件BOM等级未达到母件的BOM等级，BOM:"+ thisbomid+"序号："
									+ rs0.getString("detno"))
							.append("',sysdate)");
					baseDao.execute(sb.toString()); 
					Check=false;
				}
				
			}
		}
		if (Check){
			return "{\"result\":\"true\",\"type\":\"bomLevelCheck\"}";
		}else{
			return "{\"result\":\"false\",\"type\":\"bomLevelCheck\"}"; 
		} 
	}

	/**
	 * 物料等级检测
	 * @param bomId
	 */

	public String prodLevelCheck(String bo_id,String bomMotherCode) {
		//判断物料等级是否满足BOM等级要求
		boolean bo=true;
		String errMsg="",sonbomid;
		String SQLStr = "";
		SqlRowList rs,rs0,rs1;
		Object bolevel;
		rs1 = baseDao
				.queryForRowSet("select NVL(bs_soncode,0),bs_sonbomid from bomStruct where bs_topbomid = "
						+ bo_id+" and bs_topmothercode='"+bomMotherCode+"' AND bs_sonbomid <>0");
		while (rs1.next()) {
			sonbomid=rs1.getString("bs_sonbomid");
			bolevel=baseDao.getFieldDataByCondition("BOM", "bo_level", "bo_id="+sonbomid);
		    SQLStr = "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid  where bl_code='"+bolevel.toString()+"'  ";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				if (rs.getInt("disnum") > 0) {
					//判断是否有禁用的物料等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + sonbomid+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"') ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							errMsg="BOM："+sonbomid+"中的序号:"+rs0.getString("detno")+"的物料优选等级在BOM等级定义里面被禁用";
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bo_id).append(",'")
									.append("物料等级检测").append("','").append(errMsg).append("',sysdate)");
							 baseDao.execute( sb.toString());				 
							 bo=false;
						} 
					}
					//判断替代料是否有禁用的物料等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id left join product on pr_code=pre_repcode where pre_bomid='" + sonbomid+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"') ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							errMsg="BOM："+sonbomid+"中"+rs0.getString("detno")+"，物料优选等级在BOM等级定义里面被禁用";
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bo_id).append(",'")
									.append("物料等级检测").append("','").append(errMsg).append("',sysdate)");
							 baseDao.execute( sb.toString());				 
							 bo=false;
						} 
					}
				} 
				if (rs.getInt("allnum") > 0 && rs.getInt("disnum")==0){
					//判断是否有物料等级达到要求等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + sonbomid+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"')  ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							errMsg="BOM："+sonbomid+"中序号"+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求";
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bo_id).append(",'")
									.append("物料等级检测").append("','").append(errMsg).append("',sysdate)");
							 baseDao.execute( sb.toString());				 
							 bo=false;
						} 
					}
					//判断替代料是否有物料等级达到要求等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id  left join product on pr_code=pre_repcode where bd_bomid='" + sonbomid+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"')  ");
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							errMsg="BOM："+sonbomid+"中"+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求";
							StringBuffer sb = new StringBuffer(BE);
							int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
							sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bo_id).append(",'")
									.append("物料等级检测").append("','").append(errMsg).append("',sysdate)");
							 baseDao.execute( sb.toString());				 
							 bo=false;
						} 
					}
				}
			}  
		} 
		if(bo){
			return "{\"result\":\"true\",\"type\":\"prodLevelCheck\"}";	
		}else{
			return "{\"result\":\"false\",\"type\":\"prodLevelCheck\"}";	
		}
	}
	/**
	 * BOM状态检测
	 * @param bomId
	 */
	
	public String bomStatusCheck(String bomId,String bomMotherCode) {
		SqlRowList rs;
		String sql="select count(1) num, wm_concat('物料：'||bs_soncode||'的BOMID：'||bs_sonbomid) detno from bomStruct left join bom on bs_sonbomid=bo_id where bs_topbomid="+bomId+" and bs_topmothercode='"+bomMotherCode+"' and bs_sonbomid>0 AND bo_statuscode<>'AUDITED'";
		rs=baseDao.queryForRowSet(sql);
		if(rs.next()){
		 if (rs.getInt("num")>0){
			StringBuffer sb = new StringBuffer(BE);
			int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
			sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
					.append("BOM状态检测").append("','").append(rs.getString("detno")+"未审核").append("',sysdate)");
			 baseDao.execute( sb.toString());
			 return  "{\"result\":\"false\",\"type\":\"bomStatusCheck\"}";
			}
		}
	   return  "{\"result\":\"true\",\"type\":\"bomStatusCheck\"}";
	}


	//子件和母件特征项检测
	public String FeatureCheck(String bomId,String bomMotherCode) {
		boolean bo = true;
		String errMsg = "";
		String SQLStr = "";
		SqlRowList rs, rs0;
		rs0 = baseDao
				.queryForRowSet("select bs_sonbomid,bs_soncode from bomStruct left join product on bs_soncode=pr_code where bs_topbomid="
						+ bomId + " and bs_topmothercode='"+bomMotherCode+"' AND bs_sonbomid <>0 and pr_specvalue='NOTSPECIFIC'");
		while (rs0.next()) {
			SQLStr = "SELECT  count(1) num,wm_concat('物料：'||pf_prodcode||'特征ID：'||pf_fecode) detno FROM bomdetail left join ProdFeature A on bd_soncode=A.pf_prodcode "
					+ " WHERE bd_bomid='"
					+ rs0.getInt("bs_sonbomid")
					+ "' and pf_fecode<>' ' and pf_fecode not in (select pf_fecode from ProdFeature B where B.pf_prodcode='"
					+ rs0.getString("bs_soncode") + "') ";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				if(rs.getInt("num")>0){
				errMsg=rs.getString("detno")+"在母件"+rs0.getString("bs_soncode")+"特征中不存在";
				StringBuffer sb = new StringBuffer(BE);
				int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
				sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
						.append("子母件特征项检测").append("','").append(errMsg).append("',sysdate)");
				 baseDao.execute( sb.toString());				 
				 bo=false;
				}
			} 
		}
		// 判断子件属于虚拟特征件的是否都有特征项
		SQLStr = "SELECT  wmsys.wm_concat('子件编号:'||bs_soncode) as soncode,count(1) n FROM BomStruct left join product on pr_code=bs_soncode where bs_topbomid='"
				+ bomId + "' and bs_topmothercode='"+bomMotherCode+"' and pr_specvalue='NOTSPECIFIC' and pr_id not in (select pf_prid from prodfeature )  ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				errMsg=rs.getString("soncode")+"是虚拟特征件，但未定义特征项";
				StringBuffer sb = new StringBuffer(BE);
				int bm_id=baseDao.getSeqId("BOMMESSAGE_SEQ");
				sb.append(bm_id).append(",'").append(bomMotherCode).append("',").append(bomId).append(",'")
						.append("子母件特征项检测").append("','").append(errMsg).append("',sysdate)");
				 baseDao.execute( sb.toString());				 
				 bo=false;
			}
		}
		if (bo) {
			return "{\"result\":\"true\",\"type\":\"prodLevelCheck\"}";
		} else {
			return "{\"result\":\"false\",\"type\":\"prodLevelCheck\"}";
		}
	}
	
	@Override
	public List<Map<String, Object>> getBomMessage(String bomId,String value) {
		String sql="select bm_id, bm_bomid, bm_item, bm_description, bm_date from bomMessage where  bm_bomid="+bomId +" AND bm_item='"+value+"' ";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			return rs.getResultList();
		}else 
			return null;
	}
}
