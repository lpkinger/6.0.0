package com.uas.erp.service.common.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.common.BarcodeService;

@Service("barcodeService")
public class BarcodeServiceImpl implements BarcodeService{
	@Autowired BaseDao baseDao;
	@Override
	public List<Map<String, Object>> barcodePrint(String caller,String lps_caller,String gridStore,String printForm) {
		// TODO Auto-generated method stub
		SqlRowList rs1,rs2;	
		List <Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(printForm);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object ob = baseDao.getFieldDataByCondition("label left join labelprintsetting on la_id=lps_laid", "lps_sql", "la_id="+Integer.valueOf(map.get("template").toString())+" and lps_caller='"+lps_caller+"'");
		for (Map<Object, Object> s : grid) {
		    if((!"".equals(ob) && ob != null)){	
		    	Map <String ,Object> mp1= new  HashMap<String, Object>();
				String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";
				String va = null;
				if(lps_caller.equals("ProdIO!PurcInBarPrint")){
					va = ob.toString().replaceAll(regex, s.get("bi_id").toString());	
				}else if(lps_caller.equals("BarSProfit!BarPrint")){
					va = ob.toString().replaceAll(regex, s.get("bdd_id").toString());
				}else if(lps_caller.equals("PdaBarcodePrint")){
					va = ob.toString().replaceAll(regex, s.get("bar_id").toString());	
				}else if(lps_caller.equals("VerifyAP!BarPrint")){
					va = ob.toString().replaceAll(regex, s.get("vadp_id").toString());	
				}else if(lps_caller.equals("MakeSerialCombinePrint")){
					va = ob.toString().replaceAll(regex, s.get("ms_id").toString());	
				}
				List <Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
				rs1 = baseDao.queryForRowSet(va);
				rs2 = baseDao.queryForRowSet("select la_pagesize,lp_id,lp_valuetype,lp_encode,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id="+Integer.valueOf(map.get("template").toString()));							
				if(rs1.next()){						
				    while(rs2.next()){				  
						mp1 = rs2.getCurrentMap();
                        mp1.put("value", rs1.getString(rs2.getString("lp_name")));						
						list1.add(mp1);					
					 }				  
				}
				Map<String, Object> mp = new  HashMap<String, Object>();
				mp.put("store", list1);
				if(lps_caller.equals("ProdIO!PurcInBarPrint")){
				   mp.put("bi_id", s.get("bi_id").toString());
				}else if(lps_caller.equals("VerifyAP!BarPrint")){
				   mp.put("vadp_id",s.get("vadp_id").toString());
				}
				list.add(mp);
			}	
		}		
		return list;
	}


	@Override
	public List<Map<String, Object>> barcodePrintAll(String caller, String lps_caller,String printStore,
			String printForm) {
		// TODO Auto-generated method stub
		SqlRowList rs = null,rs1,rs2;
		List <Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(printForm);	
		Map<Object,Object> print = BaseUtil.parseFormStoreToMap(printStore);
		Object ob = baseDao.getFieldDataByCondition("label left join labelprintsetting on la_id=lps_laid", "lps_sql", "la_id="+Integer.valueOf(map.get("template").toString())+" and lps_caller='"+lps_caller+"'");
		if(lps_caller.equals("ProdIO!PurcInObxPrint")){
			rs = baseDao.queryForRowSet("select distinct pa_id from barcodeio left join package on pa_outboxcode=bi_outboxcode where bi_piid='"+print.get("bi_piid").toString()+"' and bi_pdno='"+print.get("bi_pdno").toString()+"' and pa_id is not null order by pa_id desc");
		}else if(lps_caller.equals("ProdIO!PurcInBarPrint")){
			rs = baseDao.queryForRowSet("select bi_id from barcodeIO where bi_piid='"+print.get("bi_piid").toString()+"' and bi_pdno='"+print.get("bi_pdno").toString()+"' and bi_id is not null order by bi_id desc");
		}else if(lps_caller.equals("BarSProfit!BarPrint")){
			rs = baseDao.queryForRowSet("select bdd_id from BarStocktakingDetailDet where bdd_bsdid='"+print.get("bdd_bsdid").toString()+"' order by bdd_id desc");
		}else if(lps_caller.equals("BarSProfit!ObxPrint")){
			rs = baseDao.queryForRowSet("select distinct pa_id from BarStocktakingDetailDet left join package on pa_outboxcode=bdd_outboxcode where bdd_bsdid='"+print.get("bdd_bsdid").toString()+"' order by pa_id desc");
		}else if(lps_caller.equals("VerifyAP!BarPrint")){
			rs = baseDao.queryForRowSet("select vadp_id from VerifyApplyDetailP where vadp_vadid='"+print.get("vadp_vadid").toString()+"' and vadp_vacode='"+print.get("vadp_vacode").toString()+"' order by vadp_id desc");
		}else if(lps_caller.equals("VerifyAP!ObxPrint")){
			rs = baseDao.queryForRowSet("select distinct pa_id from VerifyApplyDetailP left join package on pa_outboxcode=vadp_outboxcode where vadp_vadid='"+print.get("vadp_vadid").toString()+"' and vadp_vacode='"+print.get("vadp_vacode").toString()+"' and pa_id is not null order by pa_id desc");
		}else if(lps_caller.equals("BarStockPrint")){
			rs = baseDao.queryForRowSet("select bdd_id from BarStocktakingDetailDet left join BarStocktakingDetail on bsd_id=bdd_bsdid left join BarStocktaking on bs_id=bsd_bsid where bs_id="+print.get("bs_id")+" order by bdd_id DESC");    		
		}else if(lps_caller.equals("ProdIO!BarPrintAll")){
			rs = baseDao.queryForRowSet("select bi_id from barcodeIO where bi_piid='"+print.get("pi_id").toString()+"' and bi_id is not null order by bi_id desc");
		}else if(lps_caller.equals("MakeSerialCodePrintAll")){
			rs = baseDao.queryForRowSet("select ms_id from makeSerial where ms_mcid="+print.get("mc_id")+ "order by ms_id desc");
		}else if(lps_caller.equals("MakeSerialCombinePrintAll")){
			rs = baseDao.queryForRowSet("select distinct ms_id from makeserial where ms_mcid="+print.get("mc_id")+ "And ms_sncode= ms_combinecode and ms_combinecode is not null order by ms_id desc ");
		}
		while (rs != null && rs.next()) {
		    if((!"".equals(ob) && ob != null)){			
		    	Map <String ,Object> mp1= new  HashMap<String, Object>();
				String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";	
				String va = null;
				if(lps_caller.equals("ProdIO!PurcInObxPrint") ||lps_caller.equals("BarSProfit!ObxPrint") ){
					va = ob.toString().replaceAll(regex, rs.getString("pa_id"));
				}else if(lps_caller.equals("ProdIO!PurcInBarPrint") || lps_caller.equals("ProdIO!BarPrintAll")){
					va = ob.toString().replaceAll(regex, rs.getString("bi_id"));	
				}else if(lps_caller.equals("BarSProfit!BarPrint")|| lps_caller.equals("BarStockPrint")){
					va = ob.toString().replaceAll(regex, rs.getString("bdd_id"));
				}else if(lps_caller.equals("VerifyAP!BarPrint")){
					va = ob.toString().replaceAll(regex, rs.getString("vadp_id"));
				}if(lps_caller.equals("MakeSerialCodePrintAll") || lps_caller.equals("MakeSerialCombinePrintAll")){
					va = ob.toString().replaceAll(regex, rs.getString("ms_id"));
				}
				List <Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
				rs1 = baseDao.queryForRowSet(va);
				rs2 = baseDao.queryForRowSet("select la_pagesize,lp_id,lp_encode,lp_valuetype,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id="+Integer.valueOf(map.get("template").toString()));
				if(rs1.next()){
				    while(rs2.next()){
				    	mp1 = rs2.getCurrentMap();
                        mp1.put("value", rs1.getString(rs2.getString("lp_name")));						
					    list1.add(mp1);
					}
				}
				Map<String, Object> mp = new  HashMap<String, Object>();
				mp.put("store", list1);	
				if(lps_caller.equals("ProdIO!PurcInBarPrint")){
					mp.put("bi_id", rs.getString("bi_id"));
				}else if(lps_caller.equals("VerifyAP!BarPrint")){
					mp.put("vadp_id",rs.getString("vadp_id"));
				}
				list.add(mp);
			}	
		}
		return list;
	}


	@Override
	public List<Map<String, Object>> printPurBarcode(String caller,
			String gridStore, String printForm) {
		// TODO Auto-generated method stub
		SqlRowList rs1,rs2;	
		List <Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(printForm);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object ob = baseDao.getFieldDataByCondition("label left join labelprintsetting on la_id=lps_laid", "lps_sql", "la_id="+Integer.valueOf(map.get("template").toString())+" and lps_caller='"+caller+"'");
		if((!"".equals(ob) && ob != null)){	
		   for (Map<Object, Object> s : grid) {		    
		    	Map <String ,Object> mp1= new  HashMap<String, Object>();
				String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";
				String va = ob.toString().replaceAll(regex, s.get("vadp_id").toString());				
				List <Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
				rs1 = baseDao.queryForRowSet(va);
				rs2 = baseDao.queryForRowSet("select la_pagesize,lp_id,lp_valuetype,lp_encode,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id="+Integer.valueOf(map.get("template").toString()));							
				if(rs1.next()){						
				    while(rs2.next()){				  
						mp1 = rs2.getCurrentMap();
                        mp1.put("value", rs1.getString(rs2.getString("lp_name")));						
						list1.add(mp1);							
					 }				  
				}
				Map<String, Object> mp = new  HashMap<String, Object>();
				mp.put("store", list1);
				mp.put("vadp_id",s.get("vadp_id").toString());
				list.add(mp);
			}	
		}		
		return list;
	}


	@Override
	public List<Map<String, Object>> printAllPurBarcode(String caller,
			String printStore, String printForm) {
		// TODO Auto-generated method stub
		SqlRowList rs,rs1,rs2;
		List <Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(printForm);	
		Map<Object,Object> print = BaseUtil.parseFormStoreToMap(printStore);
		Object ob = baseDao.getFieldDataByCondition("label left join labelprintsetting on la_id=lps_laid", "lps_sql", "la_id="+Integer.valueOf(map.get("template").toString())+" and lps_caller='"+caller+"'");
		rs = baseDao.queryForRowSet("select vadp_id from VerifyApplyDetailP where vadp_vadid='"+print.get("vadp_vadid").toString()+"' and vadp_vacode='"+print.get("vadp_vacode").toString()+"'");
		if((!"".equals(ob) && ob != null)){
		   while (rs.next()) {		    			
		    	Map <String ,Object> mp1= new  HashMap<String, Object>();
				String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";			 
				String va = ob.toString().replaceAll(regex, rs.getString("vadp_id"));	
				List <Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
				rs1 = baseDao.queryForRowSet(va);
				rs2 = baseDao.queryForRowSet("select la_pagesize,lp_id,lp_encode,lp_valuetype,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id="+Integer.valueOf(map.get("template").toString()));
				if(rs1.next()){
				    while(rs2.next()){
				    	mp1 = rs2.getCurrentMap();
                        mp1.put("value", rs1.getString(rs2.getString("lp_name")));						
					    list1.add(mp1);
					}
				}
				Map<String, Object> mp = new  HashMap<String, Object>();
				mp.put("store", list1);	
				mp.put("vadp_id", rs.getString("vadp_id"));
				list.add(mp);
			}	
		}
		return list;
	}


	@Override
	public void updatePrintStatus(String caller, String ids) {//更新打印状态为已打印
		// TODO Auto-generated method stub	
		if(caller.equals("ProdIO!BarPrintAll")){
			baseDao.updateByCondition("BarcodeIO", "bi_printstatus='1'", "bi_piid='"+ids+"'");
		}else{
			ids  =  CollectionUtil.pluckSqlString(BaseUtil.parseGridStoreToMaps(ids), "bi_id");
			baseDao.updateByCondition("BarcodeIO ", "bi_printstatus='1'", "bi_id in ("+ids+")");
		}
	}


	@Override
	public void updatePurPrintStatus(String caller, String ids) {
		// TODO Auto-generated method stub
		ids  =  CollectionUtil.pluckSqlString(BaseUtil.parseGridStoreToMaps(ids), "vadp_id");
		baseDao.updateByCondition("VerifyApplyDetailP ", "vadp_printstatus='1'", "vadp_id in ("+ids+")");
	}

}