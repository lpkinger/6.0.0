package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.dao.PdaPrintDao;
import com.uas.pda.service.PdaPrintService;

@Service("pdaPrintService")
public class PdaPrintServiceImpl implements PdaPrintService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaPrintDao pdaPrintDao;
	
	//private static String PDAPRINT_HOST = "http://192.168.253.212:8090/PdaPrint";//打印服务器地址
	@Override
	public String labelPrint(String data,int em_id) {
		List<Map<String, Object>> printData = new ArrayList<Map<String, Object>>();
		List<Map<Object, Object>> grid = FlexJsonUtil.fromJsonArray(data, HashMap.class);
		if (grid.get(0).get("bar_id") != null) {
			printData = getPrintData("PdaBarcodePrint", data);
		} else if (grid.get(0).get("pa_id") != null) {
			printData = getPrintData("PdaOutboxPrint", data);
		}
		SqlRowList rs = baseDao.queryForRowSet("select em_pdaprintip ,em_pdaprintport ,em_pdaprintdpi from employee where em_id=" + em_id);
		if (rs.next()) {
			if (rs.getString("em_pdaprintip") == null) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请设置打印IP！");
			} else if (rs.getString("em_pdaprintport") == null) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请设置打印端口！");
			} else if (rs.getString("em_pdaprintdpi") == null) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请设置打印机分辨率！");
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请完善相关的打印设置！");
		}
		return pdaPrintDao.pdaPrint(printData, rs.getString("em_pdaprintip"), rs.getString("em_pdaprintport"),
				rs.getString("em_pdaprintdpi"));
	}
    
	private List<Map<String, Object>> getPrintData(String caller, String gridStore) {
		SqlRowList rs1, rs2;
		List<Map<Object, Object>> grid = FlexJsonUtil.fromJsonArray(gridStore, HashMap.class);
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		SqlRowList rs0 = baseDao
				.queryForRowSet("select  lps_sql ,la_id  from label left join labelprintsetting on la_id=lps_laid where lps_caller='"
						+ caller + "'");
		if (rs0.next()) {
			for (Map<Object, Object> s : grid) {
				Map<String, Object> mp1 = new HashMap<String, Object>();
				String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";
				String va;
				if (caller == "PdaBarcodePrint") {
					va = rs0.getString("lps_sql").replaceAll(regex, s.get("bar_id").toString());
				} else {
					va = rs0.getString("lps_sql").replaceAll(regex, s.get("pa_id").toString());
				}
				rs1 = baseDao.queryForRowSet(va);
				rs2 = baseDao
						.queryForRowSet("select la_pagesize,lp_id,lp_valuetype,lp_encode,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id="
								+ rs0.getInt("la_id"));
				if (rs1.next()) {
					while (rs2.next()) {
						mp1 = rs2.getCurrentMap();
						mp1.put("value", "a" + rs1.getString(rs2.getString("lp_name")));
						list1.add(mp1);
					}
				}
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("new", "newpage");
				list1.add(mp);
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"未配置相应的打印模板");
		}
		return list1;
	}

	@Override
	public void setDefaultPrint(String data, int em_id) {
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		baseDao.updateByCondition("employee", "em_pdaprintip='"+map.get("defaultPrintIp")+"', em_pdaprintport='"+map.get("defaultPrintPort")+"',em_pdaprintdpi='"+map.get("defaultPrintDPI")+"'", "em_id="+em_id);
	}

	@Override
	public Map<String, Object> getDefaultPrint(int em_id) {
		SqlRowList rs = baseDao.queryForRowSet("select EM_PDAPRINTIP as \"DEFAULTPRINTIP\"  ,EM_PDAPRINTPORT as \"DEFAULTPRINTPORT\" ,EM_PDAPRINTDPI as \"DEFAULTPRINTDPI\" from employee where em_id="+em_id);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
		  return null;
		}
	}

	@Override
	public synchronized String zplPrint(String caller,String dpi, String data) {
		SqlRowList rs1, rs2;
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		Object condition = map.get("condition");
		int page = 0;
		int pageSize = 0;
		String logCaller = caller;
		if(!StringUtil.hasText(map.get("condition"))){
			BaseUtil.showError("请传递打印条件!");  
		}
		if(map.get("page") != null && !("").equals(map.get("page"))){			
			 page  = Integer.valueOf(map.get("page").toString());
		}
		if(map.get("pageSize") != null && !("").equals(map.get("pageSize"))){
			 pageSize =  Integer.valueOf(map.get("pageSize").toString());
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		if("NewBar!BaPrint".equals(caller)){
			caller = "Barcode!Print";
			SqlRowList rsPrint = baseDao.queryForRowSet("select wm_concat(bar_code) bar_code from barcode where "+condition+" and nvl(bar_printcount,0)>0 and rownum <= 20 ");
			if(rsPrint.next()&& rsPrint.getString("bar_code") != null){
				BaseUtil.showError("条码:<br>"+rsPrint.getString("bar_code")+"已经被打印,不允许重复打印");
			}
		}
		SqlRowList rs0 = baseDao.queryForRowSet("select la_id,la_sql,la_width,la_height from label where la_formcaller=?",caller);
		if (rs0.next()) {
			String va ="";
			String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";
			va = rs0.getString("la_sql").replaceAll(regex,"where "+condition);
			if(page >0 && pageSize >0){		
				  va = "select * from (select tt.*,rownum rn from ( "+va+" )tt  where rownum<="+end+" )where rn>="+start;
			}
			if ("BarcodeInPrint".equals(caller) ) {
			    baseDao.execute("update barcodeio set bi_printstatus = 1,bi_lastprintdate=sysdate,bi_lastprintman='"+SystemSession.getUser().getEm_name()+"',bi_printcount =nvl(bi_printcount,0)+1  where "+ condition);
			    baseDao.execute("insert into barcodeprintlog(bl_id,bl_date,bl_man,bl_content,bl_caller,bl_barcode) select "
						+ " barcodeprintlog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','入库单打印成功','"+logCaller+"', bi_barcode from "
						+ " barcodeio where "+condition);
			}else if ("Barcode!Print".equals(caller)){
				baseDao.execute("update barcode set bar_lastprintdate=sysdate,bar_printcount=nvl(bar_printcount,0)+1 ,bar_lastprintman = '"+SystemSession.getUser().getEm_name()+"' where "+condition);
				baseDao.execute("insert into barcodeprintlog(bl_id,bl_date,bl_man,bl_content,bl_caller,bl_barcode) select "
						+ " barcodeprintlog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','"+("NewBar!BaPrint".equals(logCaller)?"新拆分条码":"库存条码")+"打印成功','"+logCaller+"', bar_code from "
						+ " barcode where "+condition);
			}else if(("VendBarcodeInPrint").equals(caller)){
				baseDao.execute("update baracceptnotify set ban_printstatus=-1 where "+condition);
			}
			rs1 = baseDao.queryForRowSet(va);
			rs2 = baseDao.queryForRowSet("select la_width,la_height,lp_id,lp_valuetype,lp_encode,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id=?", rs0.getInt("la_id"));
			List<Map<String,Object>> list = rs2.getResultList();
			while(rs1.next()) {
				for(Map<String,Object> mapl :list){
					Map<String,Object> mp1 = new HashMap<String, Object>();
					mp1.putAll(mapl);
					mp1.put("value",rs1.getString(mapl.get("LP_NAME").toString()));
					list1.add(mp1);
				}
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("new", "newpage");
				list1.add(mp);
			}
			return pdaPrintDao.printZpl(list1, dpi, rs0.getInt("la_width"), rs0.getInt("la_height"));
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"未配置相应的打印模板");
		}
	}

	@Override
	public Map<String, Object> vendorZplPrint(String caller, String data) {
		SqlRowList rs1, rs2;
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		Object condition = map.get("condition");
		if(!StringUtil.hasText(map.get("condition"))){
			BaseUtil.showError("请传递打印条件!");  
		}
		SqlRowList rs0 = baseDao.queryForRowSet("select la_id,la_sql,la_width,la_height from label where la_formcaller=?",caller);
		if (rs0.next()) {
			String va ="";
			String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";
			va = rs0.getString("la_sql").replaceAll(regex,"where "+condition);
			if(("VendBarcodeInPrint").equals(caller)){
				baseDao.execute("update baracceptnotify set ban_printstatus=-1 where "+condition);
			}
			rs1 = baseDao.queryForRowSet(va);
			rs2 = baseDao.queryForRowSet("select la_width,la_height,lp_id,lp_valuetype,lp_encode,lp_name,lp_leftrate,lp_toprate,lp_width,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_height from labelParameter left join label on la_id=lp_laid where la_id=?", rs0.getInt("la_id"));
			List<Map<String,Object>> parameter = new ArrayList<Map<String, Object>>();
			List<Map<String,Object>> barcode = new ArrayList<Map<String, Object>>();
			Map<String,Object> re = new HashMap<>();
			if(rs1.next()){
				barcode = rs1.getResultList();
			}
			if(rs2.next()){
				parameter = rs2.getResultList();
			}
			re.put("parameter",parameter);
			re.put("barcode",barcode);
			return re;
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"未配置相应的打印模板");
		}
	}	
}
