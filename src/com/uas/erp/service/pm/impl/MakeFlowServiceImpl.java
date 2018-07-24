package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.MakeFlowService;

@Service
public class MakeFlowServiceImpl implements MakeFlowService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeFlow(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("mf_code")==null || store.get("mf_code").equals("")){
			store.put("mf_code", baseDao.sGetMaxNumber("MakeFlow", 2));
		}
		String formSql = null ;
		if(store.get("mf_id").equals("0")||store.get("mf_id")==null||store.get("mf_id").equals("")){
			formSql = "insert into makeflow(mf_id,mf_code,mf_date,mf_maid,mf_qty,mf_madeqty)values('"+
					baseDao.getSeqId("makeflow_SEQ")+"','"+store.get("mf_code")+"'," +
							"to_date('"+store.get("mf_date")+"','yyyy-MM-dd'),'"+store.get("mf_maid")+"','"+
					store.get("mf_qty")+"','"+store.get("mf_madeqty")+"')";
		} else {
			formSql = "update makeflow set mf_code='"+store.get("mf_code")+"',mf_date=to_date('"
					+store.get("mf_date")+"','yyyy-MM-dd'),mf_qty='"+store.get("mf_qty")+"' where mf_id="+store.get("mf_id");
		}
		baseDao.execute(formSql);
	}

	@Override
	public void deleteMakeFlow(int ma_id, String caller) {
		handlerService.beforeDel(caller, new Object[]{ma_id});		//删除
		baseDao.deleteById("MakeFlow", "mf_id", ma_id);
		handlerService.afterDel(caller, new Object[]{ma_id});
	}

	@Override
	public String CheckdeleteMakeFlow(String mf_code, String caller) {
		Object[] objects = null;
		objects = baseDao.getFieldsDataByCondition("Stepio", new String[]{"si_code","si_id"}, "si_flowcode='"+mf_code+"'");
		String log = null;
		if(objects !=null){
			log ="该流程单已走工序转移流程,不能删除！工序转移单号:"+ "<a href=\"javascript:openUrl('jsps/pm/make/Stepio.jsp?whoami=Stepio&formCondition=si_idIS"
					+ objects[1]+"')\">"+objects[0] + "</a>&nbsp;";
		}
		return log;
	}

	@Override
	public String[] printMakeFlow(int sa_id, String caller, String reportName, String condition
			) {
		handlerService.beforePrint(caller, new Object[]{sa_id});		//执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.logger.print(caller, "mf_id", sa_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[]{sa_id});
		return keys;
	}

	@Override
	public void makeMakeFlows(int id, int number, int mfqty, String date,
			String caller) {
		int num = number/mfqty;
		int mod = number%mfqty;
		String formSql = null;
		if(num==0){
			 formSql = "insert into makeflow(mf_id,mf_code,mf_date,mf_maid,mf_qty,mf_madeqty)values('"+
					baseDao.getSeqId("makeflow_SEQ")+"','"+baseDao.sGetMaxNumber("MakeFlow", 2)+"'," +
							"to_date('"+date+"','yyyy-MM-dd'),'"+id+"','"+mfqty+"','0')";
			baseDao.execute(formSql);
		}else{
			/*Date newdate = BaseUtil.parseStringToDate(date, null);
			Calendar calendar = new GregorianCalendar();*/
			List<String> sqls = new ArrayList<String>();
			for(int i=0;i<num;i++){
			    formSql = "insert into makeflow(mf_id,mf_code,mf_date,mf_maid,mf_qty,mf_madeqty)values('"+
							baseDao.getSeqId("makeflow_SEQ")+"','"+baseDao.sGetMaxNumber("MakeFlow", 2)+"'," +
									"to_date('"+date+"','yyyy-MM-dd'),'"+id+"','"+mfqty+"','0')";
			     sqls.add(formSql);
			     /* calendar.setTime(newdate);
				 calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
				 newdate = calendar.getTime();
				 date = BaseUtil.parseDateToString(newdate, null);*/
			}
			if(mod!=0){
				/*calendar.setTime(newdate); 
			    calendar.add(calendar.DATE,num+1);//把日期往后增加一天.整数往后推,负数往前移动 
			    newdate = calendar.getTime();
			    date = BaseUtil.parseDateToString(newdate, null);*/
			    formSql = "insert into makeflow(mf_id,mf_code,mf_date,mf_maid,mf_qty,mf_madeqty)values('"+
						baseDao.getSeqId("makeflow_SEQ")+"','"+baseDao.sGetMaxNumber("MakeFlow", 2)+"'," +
								"to_date('"+date+"','yyyy-MM-dd'),'"+id+"','"+mod+"','0')";
			    sqls.add(formSql);
			}
			baseDao.execute(sqls);
		}
	}
}
