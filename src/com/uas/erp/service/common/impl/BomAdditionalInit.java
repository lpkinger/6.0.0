package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;

/**
 * @since 2017年11月8日17:40:07
 * @author lidy
 * 反馈编号：2017110060    BOM批量追加
 */
public class BomAdditionalInit extends MultiBomInit{

	private List<String> sqls;
	
	public BomAdditionalInit(List<InitData> datas, Employee employee) {
		super(datas,employee);
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
		for (String m : mothers) {
			list = map.get(m);
			if (list.size() > 0) {
				JSONObject d = list.get(0);
				Object bom_id =  getDB().getFieldDataByCondition("BOM", "bo_id", "bo_mothercode='"+d.getString("bo_mothercode")+"'");
				if(bom_id!=null){
					bo_id=Integer.parseInt(bom_id.toString());
					Object bd_detno = getDB().getFieldDataByCondition("BOM left join BOMDetail on bo_id=bd_bomid", "max(bd_detno)", "bo_id='"+bo_id+"'");
					detno = Integer.parseInt(bd_detno.toString()) + 1 ;
				}else{
					BaseUtil.showError("该母件编号的BOM已经不存在");
				}
				Set<Object> sonCodes = new HashSet<Object>();
				for (JSONObject obj : list) {
					if (!sonCodes.contains(obj.get("bd_soncode"))) {
						if(!getDB().checkByCondition("BOM left join BOMDetail on bo_id=bd_bomid", "bo_mothercode='"+d.getString("bo_mothercode")+"' and bd_soncode='"+obj.get("bd_soncode")+"'")){
							BaseUtil.showError("子件重复，母件：" + m + "，子件：" + obj.get("bd_soncode"));
						}
						sonCodes.add(obj.get("bd_soncode"));
					}else {
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
			getDB().execute(sqls);
			sqls = new ArrayList<String>();
			afterToFormal(bo_id);
		}
	}
	
	public void afterToFormal(int startId){
		getDB().execute("UPDATE bomdetail A set A.bd_detno=A.bd_detno+(select max(bd_detno) from bomdetail where bd_bomid=(select bo_id from bom where bo_mothercode=A.Bd_Mothercode)) ,A.Bd_Bomid=(select bo_id from bom where A.Bd_Mothercode=Bom.Bo_Mothercode ) where A.Bd_Bomid not in (select bo_id from bom) AND A.Bd_Bomid=?",startId);
		getDB().execute("UPDATE Prodreplace SET PRE_BOMID=(SELECT  BD_BOMID FROM BOMDETAIL WHERE BD_ID=Prodreplace.Pre_Bdid) WHERE NOT Exists (SELECT 1 FROM BOM WHERE BO_ID=PRE_BOMID)");	
		getDB().execute(
				"Merge INTO BOMDetail using(select bo_mothercode,max(bo_id) bo_id from bom group by bo_mothercode)BOM on(bo_mothercode=bd_soncode) when matched then update set bd_sonbomid=bo_id where NVL(bd_sonbomid,0)=0 and exists (select 1 from product where pr_code=bd_soncode)");
		getDB().execute(
				"update bom set bo_wcname=(select wc_name from workcenter where wc_code=bo_wccode) where bo_id=? and nvl(bo_wccode,' ')<>' '",
				startId);
		getDB().execute(
				"update bomdetail set bd_ifrep=-1,bd_repcode=(select wm_concat(pre_repcode) from prodreplace where pre_bdid=bd_id) where bd_bomid=? and bd_id in (select pre_bdid from prodreplace where pre_bdid>0)",
				startId); 
		//2017020022 计算BOM的总贴片点数
		getDB().execute(
				"update bom set bo_smtpoints = (select sum(nvl(bd_baseqty,0)*nvl(pr_smtpoint,0)) from bomdetail left join bom on bo_id=bd_bomid left join product on bd_soncode=pr_code where bo_id = ? and nvl(bd_usestatus,' ')<>'DISABLE') where bo_id = ?",
				startId,startId);
		getDB().execute("update bom set bo_refbomid=(select max(A.bo_id) from bom A where A.bo_mothercode=bom.bo_refcode) where bo_id=? and (bo_refbomid>0 OR nvl(bo_refcode,' ')<>' ')",startId);
		//20170831    反馈2017080676 将bd_esiddate更新为空
		getDB().execute("update bomdetail set bd_editdate = '' where bd_bomid = ?",startId);
		//反馈2017090336 更新单位用量倒数字段bd_baseqtyback
		getDB().execute("update bomdetail set bd_baseqtyback=round((case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end),6) where bd_bomid=? and bd_baseqtyback<>(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end)",startId);
		//BOM导入后执行固定损耗数更新：
		getDB().callProcedure("SP_BOM_LOSSQTYUPDATE", new Object[] {startId,"BOM!Additional"});
		getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOM", "bo_id" });
		getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOMDetail", "bd_id" });
	}
}
