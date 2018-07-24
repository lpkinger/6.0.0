package com.uas.erp.service.ma.impl;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.corba.se.spi.orb.StringPair;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.CurrencyService;
@Service
public class CurrencyServiceImpl implements CurrencyService{
	@Autowired
	private BaseDao baseDao;

	@Override
	public Object getCurrencyDate() {
		Object data = baseDao.getFieldDataByCondition("PERIODS", "max(pe_firstday)","1=1");
		if("".equals(data)||data==null){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyyMM");
			String time=format.format(date);
			data = time;
		}
		return data;
	}
	@Override
	public Object getSysCurrency() {
		Object sysdata = baseDao.getFieldDataByCondition("CONFIGS", "DATA","code='defaultCurrency'");
		return sysdata;
	}
	@Override
	public Object getBsCurrency() {
		List<Map<String,Object>> list = baseDao.getJdbcTemplate().queryForList("select cr_name,cr_rate from currencys");
		return list;
	}
	@Override
	public void saveCurrency(String formstore, String gridstore) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formstore);
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(gridstore);
		if("".equals(map.get("firstdate-inputEl"))||map.get("firstdate-inputEl")==null){ //限制开帐必须填写
			BaseUtil.showError("请正确设置财务开账账期！");
		}
		if("".equals(map.get("rb"))||map.get("rb")==null){//限制财务记账必须填写
			BaseUtil.showError("请确认系统财务记账币别！");
		}
		int num = baseDao.getCountByCondition("configs", "code='defaultCurrency'");
		if(num>0){  //判断本位币有没有，有的话更新，没有就插入
			baseDao.updateByCondition("configs", "data='"+map.get("rb")+"'", "code='defaultCurrency'");
		}else{
			int conid = baseDao.getSeqId("CONFIGS_SEQ");
			String consql="Insert into configs (CODE,TITLE,DATA_TYPE,DATA,CLASS_,METHOD,CALLER,DBFIND,MULTI,ID,EDITABLE,HELP) values ('defaultCurrency','本位币','VARCHAR2','"+map.get("rb")+"',null,null,'sys','ab_currency',0,"+conid+",0,null)";
			baseDao.execute(consql);
		}
		baseDao.updateByCondition("Periodsdetail", "pd_status=99", "pd_detno<'"+map.get("firstdate-inputEl")+"'");
		baseDao.updateByCondition("Periodsdetail", "pd_status=0", "pd_detno>='"+map.get("firstdate-inputEl")+"'");
		baseDao.updateByCondition("Periods", "pe_firstday='"+map.get("firstdate-inputEl")+"'", "1=1");  //更新账期 当前日期和开帐账期
		String deletesql = "delete Currencys";  //删除币别表
		baseDao.execute(deletesql);
		String dlsql = "delete CURRENCYSMONTH where cm_yearmonth='"+map.get("firstdate-inputEl")+"'";//删除和账期相同表单
		baseDao.execute(dlsql);
		for(Map<Object, Object> m : list){//循环插入列表里的数据 到月度汇率表中
			int id = baseDao.getSeqId("CURRENCYSMONTH_SEQ");
			String code = baseDao.sGetMaxNumber("CURRENCYSMONTH", 2);
			String sql = "Insert into CURRENCYSMONTH (CM_CODE,CM_YEARMONTH,CM_CRNAME,CM_CRRATE,CM_ENDRATE,CM_ID) values ('"+code+"','"+map.get("firstdate-inputEl")+"','"+m.get("CR_NAME")+"','"+m.get("CR_RATE")+"','"+m.get("CR_RATE")+"',"+id+")";
			baseDao.execute(sql);
			int idc = baseDao.getSeqId("Currencys_SEQ");
			String codec = baseDao.sGetMaxNumber("Currencys", 2);
			String cursql = "Insert into Currencys (CR_ID,CR_CODE,CR_NAME,CR_RATE,CR_VORATE,CR_STATUS,CR_STATUSCODE,CR_TAXRATE) values ("+idc+",'"+codec+"','"+m.get("CR_NAME")+"','"+m.get("CR_RATE")+"','"+m.get("CR_RATE")+"','可使用','CANUSE',0)";
			baseDao.execute(cursql);
		}
		int sysnum = baseDao.getCountByCondition("Currencys", "CR_NAME='"+map.get("rb")+"'");
		if(sysnum>0){
			baseDao.updateByCondition("Currencys", "CR_RATE='1',CR_VORATE='1'", "CR_NAME='"+map.get("rb")+"'");
		}else{
			int idg = baseDao.getSeqId("Currencys_SEQ");
			String codeg = baseDao.sGetMaxNumber("Currencys", 2);
			String cgsql = "Insert into Currencys (CR_ID,CR_CODE,CR_NAME,CR_RATE,CR_VORATE,CR_STATUS,CR_STATUSCODE,CR_TAXRATE) values ("+idg+",'"+codeg+"','"+map.get("rb")+"','1','1','可使用','CANUSE',0)";
			baseDao.execute(cgsql);
		}
	}

}
