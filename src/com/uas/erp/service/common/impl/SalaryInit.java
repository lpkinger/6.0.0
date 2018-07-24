package com.uas.erp.service.common.impl;


import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;




import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.service.common.AbstractInit;

public class SalaryInit extends AbstractInit {
	
	private Employee emp;
	private Integer il_id;
	private String date;
	private String type;
	private static final String SQL_SALARY="INSERT INTO SALARY(SL_ID,SL_TYPE,SL_DETNO,SL_NAME,SL_EMCODE,SL_PHONE,SL_UU,SL_DEPT,SL_DEPTCODE,SL_RECORDER,SL_RECORDERCODE,SL_RECORDDATE,SL_DATE,SL_BASICSAL,SL_SKILLSAL,SL_PERFORMANCESAL,SL_PERCENTAGESAL,"
			+ "SL_BONUS,SL_TRAFFICSUB,SL_PHONESUB,SL_OVERTIMESUB,SL_FOODSUB,SL_HOUSESUB,SL_HOTSUB,SL_OTHERSUB,SL_SHOULDPAY,SL_UNJOBINSURANCE,SL_OLDINSURANCE,SL_MEDICALINSURANCE,SL_HURTINSURANCE,SL_BIRTHINSURANCE,SL_HOUSINGFUND,SL_TAX,SL_WATERFEE,SL_ELECTRICFEE,SL_BOARDFEE,SL_REALPAY,SL_REMARK"
			+ ",SL_NUMBER1,SL_NUMBER2,SL_NUMBER3,SL_NUMBER4,SL_NUMBER5,SL_TEXT1,SL_TEXT2,SL_ILID) VALUES(";
	
	public SalaryInit(List<InitData> datas,Employee emp,int id,String date,String type) {
		super(datas);
		this.emp=emp;
		this.il_id=id;
		this.date=date;
		this.type=type;
	}
	
	@Override
	public void toFormal() {
		// TODO Auto-generated method stub
		JSONObject data = null;
		List<String> sqls=new LinkedList<String>();
		setConfig(datas.size(), "Salary", "sl_id");
		for (InitData d : datas) {
			data = JSONObject.fromObject(d.getId_data());
			data.put("sl_id", getSeq());
			sqls.add(getSql(data));
		} 
		getDB().execute(sqls);		
	}
	
	private String getSql(JSONObject d){
		StringBuffer sql=new StringBuffer();
		String recorder=null;
		String emcode=null;
		if(emp!=null){
			recorder=emp.getEm_name();
			emcode=emp.getEm_code();
		}	
		if(d!=null){
			sql.append(SQL_SALARY);
			sql.append(d.get("sl_id"));
			//处理类型
			sql.append(this.type==null?",null":",'"+this.type+"'");
			Object obj=d.get("sl_detno");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'"+obj+"'");
			}		
			obj=d.get("sl_name");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_emcode");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_phone");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_uu");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_dept");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_deptcode");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			sql.append(",'"+recorder+"'");
			sql.append(",'"+emcode+"'");
			obj=d.get("sl_recorderdate");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",sysdate");
			}else{
				sql.append(",to_date('"+obj+"','yyyy-mm-dd')");
			}
			//工资日期处理
			if(this.date==null||this.date.toString().trim().length() == 0){
				sql.append(",sysdate");
			}else{
				sql.append(",to_date('"+this.date+"','yyyymm')");
			}
			obj=d.get("sl_basicsal");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",'0'");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_skillsal");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",'0'");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_performancesal");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_percentagesal");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_bonus");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_trafficsub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_phonesub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_overtimesub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_foodsub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_housesub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_hotsub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_othersub");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_shouldpay");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_unjobinsurance");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_oldinsurance");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_medicalinsurance");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_hurtinsurance");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_birthinsurance");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_housingfund");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_tax");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_waterfee");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_electricfee");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_boardfee");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_realpay");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_remark");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_number1");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_number2");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_number3");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_number4");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_number5");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",0");
			}else{
				sql.append(",'" + enCode(obj.toString(), this.il_id.toString()) + "'");
			}
			obj=d.get("sl_text1");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
			obj=d.get("sl_text2");
			if(obj==null||obj.toString().trim().length() == 0){
				sql.append(",null");
			}else{
				sql.append(",'"+obj+"'");
			}
		sql.append(",'"+this.il_id+"')");
		}
		return sql.toString();
	}
	
	private static Double  enCode(String ori,String key){
		Double rev=0.0;
		if(ori != null){
			BigDecimal a1 = new BigDecimal(ori);
			BigDecimal a2 = new BigDecimal(key);
			BigDecimal a3 = new BigDecimal(key.length());
			a1 = a1.add(a2);
			a1 = a1.multiply(a3);
			a1 = a1.divide(new BigDecimal("100"));
			rev=a1.doubleValue();
		}
		return rev;
	}
}