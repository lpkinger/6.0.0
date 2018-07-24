package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.AutoInquiryService;
import com.uas.erp.service.scm.InquiryService;

@Service
public class AutoInquiryServiceImpl implements AutoInquiryService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private InquiryService inquiryService;
	
	
	final static String INSERT_INQUIRYAUTO= "insert into inquiryauto(in_id,in_code,in_date,in_recorder,in_recorddate,in_enddate,"
			+ "in_kind,in_pricetype,in_source,in_class,in_status,in_statuscode,in_sendstatus) values(?,?,sysdate,?,sysdate,sysdate+7,'采购','标准','公共询价单','采购询价','在录入','ENTERING','待上传')";
			
	@Override
	public void saveAutoInquiry(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		//当前编号的记录已经存在,不能新增!
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AutoInquiry", new String[]{}, new Object[]{});
		baseDao.execute(formSql);		
		baseDao.logger.save(caller, "ai_id", store.get("ai_id"));	
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteAutoInquiry(int ai_id, String caller) {
		handlerService.handler(caller, "delete", "before", new Object[]{ai_id});
		//删除
		baseDao.deleteById("AutoInquiry", "ai_id", ai_id);
		//记录操作
		baseDao.logger.delete(caller, "ai_id", ai_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ai_id});
	}

	@Override
	public void updateAutoInquiry(String formStore,String param, String caller,String sign) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		Object[] lastChoose = baseDao.getFieldsDataByCondition("AutoInquiry", "ai_defaultdays,ai_defaultused,ai_jtcycle,ai_jtnextdate,ai_type", "ai_id=3000");
		Employee employee = SystemSession.getUser();
		List<String> sqls = new ArrayList<String>();
		handlerService.handler(caller, "save", "before", new Object[]{store});
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "AutoInquiry", "ai_id"));
		if (prodgrid.size() > 0) {
			for (Map<Object, Object> s : prodgrid) {
				s.remove("qty");
				s.remove("price");
				if("无".equals(s.get("pk_code"))){
					sqls.add("update autoInquiry set ai_use='"+s.get("pk_autoinquiry")+"',ai_spacedate='"+s.get("pk_autoinquirydays")+"' where 1=1");
				}else{
					s.remove("pk_detno");
					sqls.add(SqlUtil.getUpdateSqlByFormStore(s, "productkind", "pk_id"));
				}
			}
		}
		baseDao.execute(sqls);
		//这个地方需要根据 这个参数配置看是静态还是动态,由于也要支持主从表都能同时修改，那么就需要这样分开写才能达到效果。TODO
		String ai_defaultdays = store.get("ai_defaultdays") == null ? "" : store.get("ai_defaultdays").toString();
		String ai_defaultdaysChoose = lastChoose[0] == null ? "" : lastChoose[0].toString();
		String ai_defaultused = store.get("ai_defaultused") == null ? "" : store.get("ai_defaultused").toString();
		String ai_defaultusedChoose = lastChoose[1] == null ? "" : lastChoose[1].toString();
		String ai_jtcycle = store.get("ai_jtcycle") == null ? "" : store.get("ai_jtcycle").toString();
		String ai_jtcycleChoose = lastChoose[2] == null ? "" : lastChoose[2].toString();
		String ai_jtnextdate = store.get("ai_jtnextdate") == null ? "" : store.get("ai_jtnextdate").toString();
		String ai_jtnextdateChoose = lastChoose[3] == null ? "" : lastChoose[3].toString().substring(0, 10);
		String ai_type = store.get("ai_type") == null ? "" : store.get("ai_type").toString();
		String ai_typeChoose = lastChoose[4] == null ? "" : lastChoose[4].toString();
		if(!ai_defaultdaysChoose.equals(ai_defaultdays) && !baseDao.isDBSetting("AutoInquiry","JTAutoInquiry")){
			baseDao.execute("update productkind set pk_autoinquirydays="+store.get("ai_defaultdays")+",pk_jtcycle=null,pk_jtinquirydate=null");
			baseDao.execute("update autoinquiry set ai_spacedate="+store.get("ai_defaultdays")+" where ai_id=3000");
		}
		if(!ai_defaultusedChoose.equals(ai_defaultused) && !baseDao.isDBSetting("AutoInquiry","JTAutoInquiry")){
			baseDao.execute("update productkind set pk_autoinquiry="+store.get("ai_defaultused")+",pk_jtcycle=null,pk_jtinquirydate=null");
			baseDao.execute("update autoinquiry set ai_use="+store.get("ai_defaultused")+" where ai_id=3000");
		}
		if(!ai_jtnextdateChoose.equals(ai_jtnextdate) && baseDao.isDBSetting("AutoInquiry","JTAutoInquiry")){
			baseDao.execute("update productkind set pk_jtnextdate=to_date('"+ai_jtnextdate+"','yyyy-mm-dd'),pk_autoinquirydays=null");
			baseDao.execute("update autoinquiry set ai_jtnextdate=to_date('"+ai_jtnextdate+"','yyyy-mm-dd') where ai_id=3000");
		}
		if(!ai_jtcycleChoose.equals(ai_jtcycle) && baseDao.isDBSetting("AutoInquiry","JTAutoInquiry")){
			baseDao.execute("update productkind set pk_jtcycle="+store.get("ai_jtcycle")+",pk_autoinquirydays=null");
			baseDao.execute("update autoinquiry set ai_jtcycle="+store.get("ai_jtcycle")+" where ai_id=3000");
		}
		if(!ai_typeChoose.equals(ai_type)){
			baseDao.execute("update productkind set pk_type="+store.get("ai_type")+" where pk_type is null");
			baseDao.execute("update autoinquiry set ai_type="+store.get("ai_type")+" where ai_id=3000");
		}
		if(StringUtil.hasText(sign) && "1".equals(sign)){
			baseDao.execute("update product a set (pr_autoinquirydays,pr_defaultused,pr_jtcycle,pr_jtinquirydate,pr_jtnextdate,pr_type)=(select pk_autoinquirydays,pk_autoinquiry,pk_jtcycle,pk_jtinquirydate,pk_jtnextdate,pk_type from productkind where pk_id=a.pr_pkid)");
		}
		baseDao.execute("update AutoInquiry set ai_updateman='"+employee.getEm_name()+"',ai_updatedate=sysdate");
		//记录操作
		baseDao.logger.others("更新操作", "更新成功", "AutoInquiry", "ai_id", store.get("ai_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	
	@Override
	public List<Map<String, Object>> getGridStore(){
		List<Map<String, Object>> list = baseDao
				.queryForRowSet(
						"select pk_id,pk_detno,pk_code,pk_name,kindname,pk_dhzc,pk_autoinquiry,pk_autoinquirydays,qty,price,pk_targetprice,pk_targetqty from Auto_Inquiry").getResultList();
		return list;
	}
	
	@Override
	public String updateInquiryProd(String data,String caller){
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object, Object>map:maps){
			if(baseDao.isDBSetting("AutoInquiry","JTAutoInquiry")){
				baseDao.execute("update product set pr_jtcycle='"+map.get("ai_jtcycle")+"',pr_jtnextdate=to_date('"+map.get("ai_jtnextdate")+"','yyyy-mm-dd'),pr_jtinquirydate='"+map.get("ai_jtinquirydate")+"',pr_defaultused='"+map.get("ai_defaultused")+"',pr_autoinquirydays=null,pr_targetqty='"+map.get("pr_targetqty")+"',pr_targetprice='"+map.get("pr_targetprice")+"',pr_type='"+map.get("ai_type")+"' where pr_code='"+map.get("pr_code")+"'");
			}else{
				baseDao.execute("update product set pr_jtcycle=null,pr_jtnextdate=null,pr_defaultused='"+map.get("ai_defaultused")+"',pr_targetqty='"+map.get("pr_targetqty")+"',pr_targetprice='"+map.get("pr_targetprice")+"',pr_type='"+map.get("ai_type")+"' where pr_code='"+map.get("pr_code")+"'");
				if(StringUtil.hasText(map.get("ai_defaultdays"))){
					baseDao.execute("update product set pr_autoinquirydays='"+map.get("ai_defaultdays")+"' where pr_code='"+map.get("pr_code")+"'");
				}
			}
		}
		baseDao.execute("update AutoInquiry set ai_type='"+maps.get(0).get("ai_type")+"',ai_enddate='"+maps.get(0).get("ai_enddate")+"',ai_prodrange='"+maps.get(0).get("ai_prodrange")+"',ai_newprodrange='"+maps.get(0).get("ai_newprodrange")+"',ai_defaultused='"+maps.get(0).get("ai_defaultused")+"' where ai_id=2000");
		return "批量更新成功";
	}
	
	@Override
	public String inquiryTurnPrice(String data,String caller){
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		StringBuffer sb = new StringBuffer();
		Map<Object, List<Map<Object, Object>>> kind = BaseUtil.groupsMap(maps, new Object[] { "kind" });
		int detno = 1;
		int in_id = 0;
		String in_code = "";
		if(kind.keySet().toString().contains("公共询价单")){
			for(Map<Object, Object>map:kind.get("公共询价单")){
				if(detno==1){
					in_id = baseDao.getSeqId("INQUIRYAUTO_SEQ");
					in_code = baseDao.sGetMaxNumber("InquiryAuto", 2);
					baseDao.execute(INSERT_INQUIRYAUTO,new Object[] {in_id,in_code,employee.getEm_name()});
				}
				baseDao.execute("update inquiryautoDetail a set id_inid="+in_id+",id_detno="+detno+",id_vendcode=(select ve_code from vendor where ve_uu=a.id_venduu) where id_id="+map.get("id_id"));
				detno ++;
				baseDao.execute("update BATCHINQUIRY set bi_checksendstatus='待上传' where bi_code='"+map.get("id_incode")+"'");
				baseDao.execute("update InquiryAutoDetail a set id_vendcode=(select ve_code from vendor where ve_uu=a.id_venduu) where id_inid="+in_id);
			}
			// 清除所有无效分段明细
			baseDao.execute("delete from InquiryAutoDetailDet where not exists (select 1 from InquiryAutoDetail where id_id=idd_idid)");
			baseDao.execute("update InquiryAutoDetail set id_code=(select in_code from InquiryAuto where id_inid=in_id) where id_inid=" + in_id
					+ " and not exists (select 1 from InquiryAuto where id_code=in_code)");
			baseDao.execute("update InquiryAutoDetail a set id_exchangerate=(select cm_crrate from currencysmonth where cm_crname=a.id_currency and cm_yearmonth=to_char(sysdate,'yyyymm')),id_isagreed=-1,id_status='已转报价',id_sendstatus='待上传' where id_inid="+in_id); //2018.3.7  转报价时id_status,id_sendstatus协助平台那边赋值，方便他们取价
			baseDao.execute("update INQUIRYAutoDETAILDET a set idd_preprice=(idd_price*nvl((select id_exchangerate from inquiryAutodetail where a.idd_idid=id_id),1)/(1+nvl((select id_rate from inquiryAutodetail where a.idd_idid=id_id),0)/100)) where idd_idid in (select id_id from inquiryAutodetail where id_inid="+in_id+")");
			// 执行提交操作
			baseDao.submit("InquiryAuto", "in_id=" + in_id, "in_checkstatus", "in_checkstatuscode");
			baseDao.execute("update InquiryAuto set in_checksendstatus='待上传' where in_id=? and in_sendstatus='已上传'", in_id);
			baseDao.audit("InquiryAuto", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditor");
			// 记录操作
			baseDao.logger.submit("InquiryAuto", "in_id", in_id);
			getLastPrice(in_id);
			// 执行提交后的其它逻辑
			handlerService.handler("InquiryAuto", "commit", "after", new Object[] { in_id });
			sb.append( "转报价单成功,已报价询价单号:"+"<a href=\"javascript:openUrl('jsps/scm/purchase/inquiryAuto.jsp?formCondition=in_idIS" + in_id
					+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;<br>");
		}
		Map<Object, List<Map<Object, Object>>> kind2 = BaseUtil.groupsMap(maps, new Object[] { "kind","id_inid" });
		for (Object key2 : kind2.keySet()) {
			if(key2.toString().contains("采购询价单")){
				for(Map<Object, Object>map3:kind2.get(key2)){
					inquiryService.submitInquiry(Integer.parseInt(map3.get("id_inid").toString()), "Inquiry");
					sb.append("采购询价单:"+"<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + map3.get("id_inid")
							+ "&gridCondition=id_inidIS" + map3.get("id_inid") + "')\">" + map3.get("id_incode") + "</a>&nbsp;"+"提交成功<br>");
				}
			}
		}
		return sb.toString();
	}
	// 计算上次价格浮动率等
	private void getLastPrice(Object id) {
		baseDao.updateByCondition("inquiryautodetail", "id_freerate=0", "id_inid="+id);
		String datas = baseDao.getDBSetting("freeRateGetPrice");
		Object kind = baseDao.getFieldDataByCondition("inquiryauto", "in_kind", "in_id="+id);
		if(datas==null || datas.equals("N,N,N,N,N")){//默认为物料+供应商+币别+类型--最新有效价格
			datas = "A";
		}
	    for(int i=0;i<datas.replace(",", "").length();i++){
	    	SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM inquiryautodetail LEFT JOIN inquiryauto on in_id=id_inid WHERE id_freerate=0 and in_id=?", id);
	    	Object[] price = null;
			while (rs.next()) {
				String[] data = datas.split(",");
				if(data[i].equals("A")){
					price = baseDao.getFieldsDataByCondition("(select * from (select ppd_price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
									+ "where ppd_vendcode='" + rs.getString("id_vendcode") + "' and ppd_prodcode='" + rs.getString("id_prodcode")+"' "
									+ "and ppd_currency='"+rs.getString("id_currency")+"' and pp_kind='"+kind+"' and ppd_statuscode='VALID' "
									+ "order by pp_indate desc) order by ppd_id desc) ", new String[]{"ppd_price","ppd_id"},
									"rownum<2");//物料+供应商+币别+类型--最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) {
						baseDao.updateByCondition("inquiryautodetail", "id_preprice=" + price[0] + ",id_freerate=(id_price-" + price[0] + ")/" + price[0]+"",
								"id_freerate=0 and id_id=" + rs.getInt("id_id"));
					}
					//防止没有该参数的客户正常客户因为缺少id_ppdid字段报错
					if(datas.replace(",", "").length()>1 && price!=null){
						baseDao.updateByCondition("inquiryautodetail", "id_ppdid="+price[1]+"",
								"id_id=" + rs.getInt("id_id"));
					}
				}else if(data[i].equals("B")){
					price = baseDao.getFieldsDataByCondition("(select * from (select ppd_price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
									+ "  where ppd_vendcode='" + rs.getString("id_vendcode") + "' and ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and ppd_currency='" + rs.getString("id_currency")+ "' and pp_kind='"+kind+"' "
									+ " and ppd_statuscode='VALID' and nvl(ppd_lapqty,0)="+rs.getDouble("id_lapqty")+" order by pp_indate desc) order by ppd_id desc) ", new String[]{"ppd_price","ppd_id"},
									" rownum<2 "); //物料+供应商+币别+类型+分段数---最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						baseDao.updateByCondition("inquiryautodetail", "id_preprice=" + price[0] + ",id_freerate=(id_price-" + price[0] + ")/" + price[0]+",id_ppdid="+price[1]+"",
								"id_freerate=0 and id_id=" + rs.getInt("id_id"));
					}
				}else if(data[i].equals("C")){
					price = baseDao.getFieldsDataByCondition("(select * from (select nvl(ppd_price,0)*nvl(cm_crrate,1) price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') where ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and pp_kind='"+kind+"' and ppd_statuscode='VALID'  order by pp_indate desc) order by ppd_id desc) ", new String[]{"price","ppd_id"},
									" rownum<2"); //物料+类型----最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquiryautodetail", "id_preprice=" + preprice + ",id_freerate=(round(id_price,8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("D")){
					price = baseDao.getFieldsDataByCondition("(SELECT nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1) price,ppd_id "
							+ "FROM PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
							+ "left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') "
							+ "WHERE ppd_prodcode='"+rs.getString("id_prodcode")+"' and pp_kind='"+kind+"' and ppd_statuscode='VALID' and ppd_appstatus='合格'"
							+ "order by nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1))", new String[]{"price","ppd_id"},
									"rownum=1"); //物料+类型---有效且合格最低不含税价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquiryautodetail", "id_preprice=" + preprice + ",id_freerate=(round((id_price/(1+nvl(id_rate,0)/100)),8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("E")){
					price = baseDao.getFieldsDataByCondition("(select nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1) price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') where ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and pp_kind='"+kind+"' and ppd_statuscode='VALID' order by nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1))", new String[]{"price","ppd_id"},
									"rownum=1"); //物料+类型--有效最低不含税价格。
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquiryautodetail", "id_preprice=" + preprice + ",id_freerate=(round((id_price/(1+nvl(id_rate,0)/100)),8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}
			}
	    }
	    baseDao.updateByCondition("inquiryautodetail", "id_freerate=id_freerate*100", "id_inid=" + id);
	}
}
