package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomzlService;

@Service
public class CustomzlServiceImpl implements CustomzlService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveCustomzl(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "customzlb",new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "cz_id", store.get("cz_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	
	@Override
	public void updateCustomzlById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "customzlb", "cz_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "cz_id", store.get("cz_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteCustomzl(int cz_id, String caller) {
		handlerService.handler(caller, "delete", "before", new Object[] { cz_id });
		baseDao.deleteById("customzlb", "cz_id", cz_id);
		// 记录操作
		baseDao.logger.delete(caller, "cz_id", cz_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cz_id });
	}

	@Override
	public void calculateDate(int cz_id, String caller) {
		//获得区间设置数据
		Object[] ob=baseDao.getFieldsDataByCondition("customzlb", new String[]{"nvl(cz_setfrom1,0)||','||nvl(cz_setfrom2,0)||','||nvl(cz_setfrom3,0)||','||nvl(cz_setfrom4,0)||','||nvl(cz_setfrom5,0)"
				+ "||','||nvl(cz_setfrom6,0)||','||nvl(cz_setfrom7,0)||','||nvl(cz_setfrom8,0)||','||nvl(cz_setfrom9,0)||','||nvl(cz_setfrom10,0)"
				+ "||','||nvl(cz_setfrom11,0)||','||nvl(cz_setfrom12,0)||','||nvl(cz_setfrom13,0)||','||nvl(cz_setfrom14,0)||','||nvl(cz_setfrom15,0) AS SETFROMS",
				"nvl(cz_setto1,0)||','||nvl(cz_setto2,0)||','||nvl(cz_setto3,0)||','||nvl(cz_setto4,0)||','||nvl(cz_setto5,0)||','"
				+ "||nvl(cz_setto6,0)||','||nvl(cz_setto7,0)||','||nvl(cz_setto8,0)||','||nvl(cz_setto9,0)||','||nvl(cz_setto10,0)"
				+ "||','||nvl(cz_setto11,0)||','||nvl(cz_setto12,0)||','||nvl(cz_setto13,0)||','||nvl(cz_setto14,0)||','||nvl(cz_setto15,0) AS SETTOS",
				"nvl(cz_settype1,0)||','||nvl(cz_settype2,0)||','||nvl(cz_settype3,0)||','||nvl(cz_settype4,0)||','||nvl(cz_settype5,0)||','||"
				+ "nvl(cz_settype6,0)||','||nvl(cz_settype7,0)||','||nvl(cz_settype8,0)||','||nvl(cz_settype9,0)||','||"
				+ "nvl(cz_settype10,0)||','||nvl(cz_settype11,0)||','||nvl(cz_settype12,0)||','||nvl(cz_settype13,0)||','||nvl(cz_settype14,0)||','"
				+ "||nvl(cz_settype15,0) AS SETTYPES"}, "cz_id="+cz_id);
		String[] froms=ob[0].toString().split(",");
		String[] tos=ob[1].toString().split(",");
		String[] types=ob[2].toString().split(",");
		List<String> sqls = new ArrayList<String>();
		for(int i=0;i<15;i++){
			int nf=1-Integer.parseInt(froms[i]);
			int nt=0-Integer.parseInt(tos[i]);
			if(!"0".equals(froms[i])&&!"0".equals(tos[i])&&"MONTH".equals(types[i])){
				sqls.add("update customzlb set cz_todate"+(i+1)+"=add_months(cz_todate,"+nf+"),cz_fromdate"+(i+1)+"=add_months(cz_todate,"+nt+")+1 where cz_id="+cz_id);
			}else if(!"0".equals(froms[i])&&!"0".equals(tos[i])&&"DAY".equals(types[i])){
				sqls.add("update customzlb set cz_todate"+(i+1)+"=cz_todate+"+nf+",cz_fromdate"+(i+1)+"=cz_todate+"+(nt+1)+" where cz_id="+cz_id);
			}
		}
		if(sqls.size()>0){
			baseDao.execute(sqls);
		}else{//为进行区间设置
			BaseUtil.showError("请先设置区间");
		}
	}
}
