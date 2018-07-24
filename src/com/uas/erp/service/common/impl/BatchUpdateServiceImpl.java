package com.uas.erp.service.common.impl;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.service.common.BatchUpdateService;
@Service
public class BatchUpdateServiceImpl implements BatchUpdateService {
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private BaseDao baseDao;
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public String importExcel(String caller, HSSFSheet sheet) {
		// TODO Auto-generated method stub
		List<DetailGrid> details=detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		int size=details.size();
		StringBuffer sb=new StringBuffer();
		String fields="(";
		DetailGrid detail=null;
		String tablename=details.get(0).getDg_table();
		String updateSql="";
		//
		String updatestr=" ";
		String selectstr=" select " ;
		String conditionstr="";
		String groupby=" group by ";
		String existscondition=" where exists(select 1 from tt_tempupdate where ";
		for(int k=0;k<size;k++){
			fields+="data"+k+",";
			detail=details.get(k);
			if(detail.getDg_logictype().equals("update")){
				updatestr+=detail.getDg_field()+"=src.data"+k+",";
				if(detail.getDg_type().equals("datecolumn")){
					selectstr+="max(to_date(data"+k+",'yyyy-MM-dd'))" + "as data"+k +",";
				}else selectstr+="max(data"+k+") as data"+k+",";
			}else if(detail.getDg_logictype().equals("condition")){
				groupby+="data"+k+",";
				if(conditionstr.equals("")){
					conditionstr+=detail.getDg_field()+"="+"data"+k;
				}else conditionstr+=" AND "+detail.getDg_field()+"="+"data"+k;
				selectstr+="data"+k+",";
			}
		}
		// updateSql=updatestr.substring(0, updatestr.length()-1)+") ="+selectstr.substring(0, selectstr.length()-1)+" from tt_tempupdate where "+conditionstr+" and rownum=1)";
		updateSql="Merge into "+tablename+ " using ("+selectstr.substring(0, selectstr.length()-1)+" from tt_tempupdate "+groupby.substring(0,groupby.length()-1)+") src on("+conditionstr+") when matched then update set "+updatestr.substring(0, updatestr.length()-1)+"  "+existscondition+conditionstr+")";
		fields=fields.substring(0,fields.length()-1)+")";
		List<String>sqls=new ArrayList<String>();
		StringBuffer err=new StringBuffer();
		boolean bool=true;
		int errcount=0;
		int successcount=0;
		
		//限制 5000行;
		int limit=sheet.getLastRowNum()>5000?5000:sheet.getLastRowNum();
		for (int i = 1; i <=limit; i++) {
			HSSFRow row = sheet.getRow(i);	
			if(row != null){
				if (i > 0) {
					sb.append("{");
				}
				sb.setLength(0);
				bool=true;
				sb.append("insert into tt_tempupdate "+fields+" Values ( ");
				for (int j = 0; j < size; j++) {
					HSSFCell cell = row.getCell(j);
					if(cell != null){
						Object value = cell.toString();
						switch (cell.getCellType()) {  
						case HSSFCell.CELL_TYPE_NUMERIC: // 数字  
							if (HSSFDateUtil.isCellDateFormatted(cell)) {        
								value = DateUtil.parseDateToString(cell.getDateCellValue(), null);
							} else {  															
								value = cell.getNumericCellValue(); 								
								if(String.valueOf(value).toUpperCase().indexOf('E') > -1 ){	 
								value = String.valueOf(new DecimalFormat("#.#########").format(value));																					
								}
							}
							break;  
						case HSSFCell.CELL_TYPE_STRING: // 字符串  
							value = cell.getStringCellValue();
							value = value.toString().replace("\"", "\\\"");
							break;  
						case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean  
							value = cell.getBooleanCellValue(); 
							break;  
						case HSSFCell.CELL_TYPE_FORMULA: // 公式  
							value = cell.getCellFormula() + "";  
							break;  
						case HSSFCell.CELL_TYPE_BLANK: // 空值  
							value = "";  
							break;  
						case HSSFCell.CELL_TYPE_ERROR: // 故障  
							value = "";  
							break;  
						default:  
							value = "";  
							break;  
						}
						sb.append("'"+value.toString().trim()+"',");
					} else {
						//存在空值 不算入插于语句
						bool=true;
						/*errcount++;
						err.append("<div style='color:red;'>Excel 第 "+(j+1)+" 行存在空值  无法更新对应数据!</></br>");
						break;*/
						//取消限制 null值允许更新成null 
						sb.append("null,");
					}

				}
				if(bool){
					successcount++;
					sqls.add(sb.toString().substring(0,sb.toString().length()-1)+")");
				}
			}
		}
		baseDao.execute(sqls);
		baseDao.execute(updateSql);
		baseDao.logger.others("数据批量更新,caller:"+caller, "更新成功，有效"+successcount+"条,无效 "+errcount+"条", caller,"","");
		if("ExportPriceUpdate".equals(caller)){
			String update="Merge into "+tablename+ " using ("+selectstr.substring(0, selectstr.length()-1)+" from tt_tempupdate "+groupby.substring(0,groupby.length()-1)+") src on("+conditionstr+") when matched then update set pd_ordertotal=round(pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),pd_taxtotal=round((pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*pd_taxrate/100)/(100+nvl(pd_taxrate,0)),2)*100 "+existscondition+conditionstr+")";
			baseDao.execute(update);
		}    
		String MSG="<h1>有效"+successcount+"条,&nbsp&nbsp 无效 "+errcount+" 条</h1>";
		return MSG+err.toString();
	}

}
