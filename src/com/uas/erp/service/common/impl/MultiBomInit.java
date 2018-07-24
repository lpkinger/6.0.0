package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.service.common.AbstractInit;

public class MultiBomInit extends AbstractInit {

	private Employee employee;

	private List<String> sqls;

	public MultiBomInit(List<InitData> datas, Employee employee) {
		super(datas);
		this.employee = employee;
		sqls = new ArrayList<String>();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, timeout = 20)
	public synchronized void toFormal() {
		Map<String, List<JSONObject>> map = parseBom();
		setConfig(map.keySet().size(), "BOM", "bo_id");
		Set<String> mothers = map.keySet();
		List<JSONObject> list = null;
		int bo_id = 0;
		int detno = 1;
		int startId = 0;
		for (String m : mothers) {
			bo_id = getSeq();
			if (startId == 0)
				startId = bo_id;
			list = map.get(m);
			if (list.size() > 0) {
				detno = 1;
				JSONObject d = list.get(0);								
				sqls.add(getMotherSql(bo_id,d));
				Set<Object> sonCodes = new HashSet<Object>();
				for (JSONObject obj : list) {
					if (!sonCodes.contains(obj.get("bd_soncode"))) {
						sonCodes.add(obj.get("bd_soncode"));
					} else {
						BaseUtil.showError("子件重复，母件：" + m + "，子件：" + obj.get("bd_soncode"));
					}
					obj.put("bd_bomid", bo_id);
					sqls.add(getSonSql(obj, detno));
					// 位号
					Object location = obj.get("bd_location");
					if (location!=null){
						location=location.toString().replace("，", ",");//替换位号的全角逗号为半角
					}
					// 替代料
					Object replace = obj.get("bd_replace");
					if (replace != null && replace.toString().trim().length() > 0) {
						//去重 update 2016/12/6
						String[] repCodes = StringUtil.deleteRepeats(replace.toString(),",").split(",");
						int j = 1;
						for (String s : repCodes) {
							sqls.add(insertReplace(obj, s, j++));
						}
					}
					detno++;
				}
			}
		}
		getDB().execute(sqls);
		afterToFormal(startId);
	}
	
	public void afterToFormal(int startId){
		getDB().execute("DELETE from BOM WHERE (bo_mothercode) IN ( SELECT bo_mothercode FROM bom GROUP BY bo_mothercode HAVING COUNT(bo_mothercode) > 1) AND bo_id>=? AND bo_id NOT IN (SELECT MIN(bo_id) FROM bom GROUP BY bo_mothercode HAVING COUNT(*) > 1)",startId);
		getDB().execute("UPDATE bomdetail A set A.bd_detno=A.bd_detno+(select max(bd_detno) from bomdetail where bd_bomid=(select bo_id from bom where bo_mothercode=A.Bd_Mothercode)) ,A.Bd_Bomid=(select bo_id from bom where A.Bd_Mothercode=Bom.Bo_Mothercode ) where A.Bd_Bomid not in (select bo_id from bom) AND A.Bd_Bomid>=?",startId);
		getDB().execute("UPDATE Prodreplace SET PRE_BOMID=(SELECT  BD_BOMID FROM BOMDETAIL WHERE BD_ID=Prodreplace.Pre_Bdid) WHERE NOT Exists (SELECT 1 FROM BOM WHERE BO_ID=PRE_BOMID)");	
		getDB().execute(
				"Merge INTO BOMDetail using(select bo_mothercode,max(bo_id) bo_id from bom group by bo_mothercode)BOM on(bo_mothercode=bd_soncode) when matched then update set bd_sonbomid=bo_id where NVL(bd_sonbomid,0)=0 and exists (select 1 from product where pr_code=bd_soncode)");
		getDB().execute(
				"update bom set bo_wcname=(select wc_name from workcenter where wc_code=bo_wccode) where bo_id>=? and nvl(bo_wccode,' ')<>' '",
				startId);
		getDB().execute(
				"update bomdetail set bd_ifrep=-1,bd_repcode=(select wm_concat(pre_repcode) from prodreplace where pre_bdid=bd_id) where bd_bomid>=? and bd_id in (select pre_bdid from prodreplace where pre_bdid>0)",
				startId); 
		//2017020022 计算BOM的总贴片点数
		getDB().execute(
				"update bom set bo_smtpoints = (select sum(nvl(bd_baseqty,0)*nvl(pr_smtpoint,0)) from bomdetail left join bom on bo_id=bd_bomid left join product on bd_soncode=pr_code where bo_id >= ? and nvl(bd_usestatus,' ')<>'DISABLE') where bo_id >= ?",
				startId,startId);
		getDB().execute("update bom set bo_refbomid=(select max(A.bo_id) from bom A where A.bo_mothercode=bom.bo_refcode) where bo_id>=? and (bo_refbomid>0 OR nvl(bo_refcode,' ')<>' ')",startId);
		//20170831    反馈2017080676 将bd_esiddate更新为空
		getDB().execute("update bomdetail set bd_editdate = '' where bd_bomid >= ?",startId);
		//反馈2017090336 更新单位用量倒数字段bd_baseqtyback
		getDB().execute("update bomdetail set bd_baseqtyback=round((case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end),6) where bd_bomid>=? and bd_baseqtyback<>(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end)",startId);
		//更新单位
		
		//BOM导入后执行固定损耗数更新：
		getDB().callProcedure("SP_BOM_LOSSQTYUPDATE", new Object[] {startId,"BOM"});
		getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOM", "bo_id" });
		getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOMDetail", "bd_id" });
	}

	public Map<String, List<JSONObject>> parseBom() {
		Map<String, List<JSONObject>> map = new ConcurrentHashMap<String, List<JSONObject>>();
		JSONObject data = null;
		Object code = null;
		List<JSONObject> list = null;
		setConfig(datas.size(), "BOMDetail", "bd_id");
		for (InitData d : datas) {
			data = JSONObject.fromObject(d.getId_data());
			code = data.get("bo_mothercode");
			if (code != null && !"".equals(code.toString().trim())) {
				data.put("bd_id", getSeq());
				if (map.containsKey(code.toString())) {
					list = map.get(code);
				} else {
					list = new ArrayList<JSONObject>();
				}
				list.add(data);
				map.put(code.toString(), list);
			}
		}
		return map;
	}

	private static final String SQL_BOM_F = "INSERT INTO BOM (bo_id,bo_mothercode,bo_statuscode,bo_status,bo_ispast,bo_validstatuscode,bo_validstatus,bo_updateman,bo_recorder";

	private static final String SQL_DETAIL = "INSERT INTO BOMDetail (bd_id,bd_bomid,bd_soncode,bd_detno,bd_baseqty,bd_mothercode,bd_remark,bd_remark2,bd_location,bd_ifrep,bd_repcode,bd_stepcode,bd_lossqty,bd_lossrate,bd_buildfinishqty,bd_unit";

	private String getMotherSql(int bo_id,JSONObject d) {
		String em_name = "";
		if (employee != null)
			em_name = employee.getEm_name();
		StringBuffer values = new StringBuffer();
		StringBuffer fields = new StringBuffer(SQL_BOM_F);
		values.append(" values (").append(bo_id).append(",'").append(String.valueOf(d.get("bo_mothercode"))).append("','ENTERING','在录入',")
				.append("是".equals(String.valueOf(d.get("bo_ispast"))) ? -1 : 0).append(",'UNVALID','无效','").append(em_name)
				.append("','").append(em_name).append("'");
		if (String.valueOf(d.get("bo_wccode")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_wccode")))) {
			fields.append(",bo_wccode");
			values.append(",'").append(String.valueOf(d.get("bo_wccode"))).append("'");
		}
		if (String.valueOf(d.get("bo_cop")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_cop")))) {
			fields.append(",bo_cop");
			values.append(",'").append(String.valueOf(d.get("bo_cop"))).append("'");
		}
		if (String.valueOf(d.get("bo_groupcode")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_groupcode")))) {
			fields.append(",bo_groupcode");
			values.append(",'").append("是".equals(String.valueOf(d.get("bo_groupcode"))) ? -1 : 0).append("'");
		}
		if (String.valueOf(d.get("bo_flowstyle")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_flowstyle")))) {
			fields.append(",bo_flowstyle");
			values.append(",'").append(String.valueOf(d.get("bo_flowstyle"))).append("'");
		}
		if (String.valueOf(d.get("bo_style")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_style")))) {
			fields.append(",bo_style");
			values.append(",'").append(String.valueOf(d.get("bo_style"))).append("'");
		}
		if (String.valueOf(d.get("bo_level")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_level")))) {
			fields.append(",bo_level");
			values.append(",'").append(String.valueOf(d.get("bo_level"))).append("'");
		}
		if (String.valueOf(d.get("bo_version")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_version")))) {//版本
			fields.append(",bo_version");
			values.append(",'").append(String.valueOf(d.get("bo_version"))).append("'");
		}
		if (String.valueOf(d.get("bo_craftversion")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_craftversion")))) {//工艺路线
			fields.append(",bo_craftversion");
			values.append(",'").append(String.valueOf(d.get("bo_craftversion"))).append("'");
		}
		if (String.valueOf(d.get("bo_finerate")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_finerate")))) {//优选率
			fields.append(",bo_finerate");
			values.append(",'").append(String.valueOf(d.get("bo_finerate"))).append("'");
		}
		if (String.valueOf(d.get("bo_devcode")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_devcode")))) {//部门编号
			fields.append(",bo_devcode");
			values.append(",'").append(String.valueOf(d.get("bo_devcode"))).append("'");
		}
		if (String.valueOf(d.get("bo_isextend")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_isextend")))) {//是否变型BOM
			fields.append(",bo_isextend");
			values.append(",'").append("是".equals(String.valueOf(d.get("bo_isextend"))) ? -1 : 0).append("'");
		}
		if (String.valueOf(d.get("bo_refbomid")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_refbomid")))) {//原型BOM ID
			fields.append(",bo_refbomid");
			values.append(",'").append(String.valueOf(d.get("bo_refbomid"))).append("'");
		}	
		if (String.valueOf(d.get("bo_refcode")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_refcode")))) {//原型母件编号
			fields.append(",bo_refcode");
			values.append(",'").append(String.valueOf(d.get("bo_refcode"))).append("'");
		}
		if (String.valueOf(d.get("bo_refname")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_refname")))) {//原型母件名称
			fields.append(",bo_refname");
			values.append(",'").append(String.valueOf(d.get("bo_refname"))).append("'");
		}
		if (String.valueOf(d.get("bo_refspec")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_refspec")))) {//原型母件规格
			fields.append(",bo_refspec");
			values.append(",'").append(String.valueOf(d.get("bo_refspec"))).append("'");
		}
		if (String.valueOf(d.get("bo_remark")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_remark")))) {//备注
			fields.append(",bo_remark");
			values.append(",'").append(String.valueOf(d.get("bo_remark"))).append("'");
		}
		//欧盛字段  @author：lidy   反馈编号：2017110060
		if (String.valueOf(d.get("bo_relativecode")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_relativecode")))) {//项目编号
			fields.append(",bo_relativecode");
			values.append(",'").append(String.valueOf(d.get("bo_relativecode"))).append("'");
		}
		//鼎智增加软件版本字段 反馈编号：2017120371
		if(String.valueOf(d.get("bo_softversion")).length() > 0 && !"null".equals(String.valueOf(d.get("bo_softversion")))){//软件版本
			fields.append(",bo_softversion");
			values.append(",'").append(String.valueOf(d.get("bo_softversion"))).append("'");
		}
		//获取BOM表的自定义字段    
		Iterator<String> dIterator = d.keys();  
		while(dIterator.hasNext()){  
		    String key = dIterator.next();  
		    if(key.endsWith("_user")&&key.startsWith("bo_")){
		    	System.out.println("key: "+key);  
		    	if (d.get(key) != null && d.get(key).toString().trim().length() > 0) {
					fields.append(","+key);
					values.append(",'").append(d.get(key)).append("'");
				}
		    }
		} 
		fields.append(")");
		values.append(")");
		return fields.toString() + values.toString();
	}

	public String getSonSql(JSONObject d, Integer detno) {
		StringBuffer values = new StringBuffer();
		StringBuffer fields = new StringBuffer(SQL_DETAIL);
		int ifrep = 0;
		String rep_code="";
		if(d.get("bd_replace") == null || d.get("bd_replace").toString().trim().length() == 0){
			ifrep = 0;
		}else{//替代料去除重复
			ifrep = -1;
			rep_code = StringUtil.deleteRepeats(d.get("bd_replace").toString(),",");
		}
		Object bd_location=d.get("bd_location");
		if (bd_location!=null){
			bd_location=bd_location.toString().replace("，", ",").replaceAll("(,){2,}", ",");//替换位号的全角逗号为半角
		}
		Object qty = (d.get("bd_baseqty") == null || d.get("bd_baseqty").toString().trim().length() == 0) ? 0 : d
				.get("bd_baseqty");
		values.append(" values (").append(d.get("bd_id")).append(",").append(d.get("bd_bomid"));
		if (d.get("bd_soncode") != null && d.get("bd_soncode").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_soncode")).append("'").append(",").append(detno).append(",").append(qty);
		} else {
			values.append(",null").append(",").append(detno).append(",").append(qty);
		}
		if (d.get("bo_mothercode") != null && d.get("bo_mothercode").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bo_mothercode")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_remark") != null && d.get("bd_remark").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_remark")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_remark2") != null && d.get("bd_remark2").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_remark2")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_location") != null && d.get("bd_location").toString().trim().length() > 0) {
			values.append(",'").append(bd_location).append("'");
		} else {
			values.append(",null");
		}
		values.append(",").append(ifrep);
		if (d.get("bd_repcode") != null && d.get("bd_repcode").toString().trim().length() > 0) {
			values.append(",'").append(rep_code).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_stepcode") != null && d.get("bd_stepcode").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_stepcode")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_lossqty") != null && d.get("bd_lossqty").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_lossqty")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_lossrate") != null && d.get("bd_lossrate").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_lossrate")).append("'");
		} else {
			values.append(",null");
		}
		if (d.get("bd_buildfinishqty") != null && d.get("bd_buildfinishqty").toString().trim().length() > 0) {
			values.append(",'").append(d.get("bd_buildfinishqty")).append("'");
		} else {
			values.append(",0");
		}
		if(d.get("bd_unit")!=null && d.get("bd_unit").toString().trim().length() > 0){
			values.append(",'").append(d.get("bd_unit")).append("'");
		}else{
			values.append(",null");
		}
		//欧盛自定义字段   @author：lidy   反馈编号：2017110060
		//获取BOMDetail表的自定义字段
		Iterator<String> dIterator = d.keys();  
		while(dIterator.hasNext()){  
		    String key = dIterator.next();  
		    if(key.endsWith("_user")&&key.startsWith("bd_")){
		    	System.out.println("key: "+key);  
		    	if (d.get(key) != null && d.get(key).toString().trim().length() > 0) {
					fields.append(","+key);
					values.append(",'").append(d.get(key)).append("'");
				}
		    }
		}  
		fields.append(")");
		values.append(")");
		return fields.toString() + values.toString();
	}

	private static final String SQL_REPLACE = "INSERT INTO ProdReplace(pre_id, pre_detno, pre_soncode, pre_repcode,pre_bdid, pre_bomid) VALUES(";

	public String insertReplace(JSONObject d, String replaceCode, int detno) {
		StringBuffer sb = new StringBuffer();
		sb.append(SQL_REPLACE).append("PRODREPLACE_SEQ.nextval,").append(detno).append(",'")
				.append(d.get("bd_soncode"))
				.append("','").append(replaceCode).append("',").append(d.get("bd_id")).append(",")
				.append(d.get("bd_bomid"))
				.append(")");
		return sb.toString();
	} 
}
