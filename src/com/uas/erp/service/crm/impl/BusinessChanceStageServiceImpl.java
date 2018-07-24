package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.BusinessChanceStageService;

@Service
public class BusinessChanceStageServiceImpl implements
		BusinessChanceStageService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBusinessChanceStage(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BusinessChanceStage",
				"bs_code='" + store.get("bs_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		boolean bool1 = baseDao.checkByCondition("BusinessChanceStage",
				"bs_name='" + store.get("bs_name") + "'");
		if (!bool1) {
			BaseUtil.showError("该商机阶段名称已存在");
		}
		boolean bool2 = baseDao.checkByCondition("BusinessChanceStage",
				"bs_detno='" + store.get("bs_detno") + "'");
		if (!bool2) {
			BaseUtil.showError("该商机阶段顺序已存在");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
	
		String detStr = "";
		List<Object[]> detnos = baseDao.getFieldsDatasByCondition("businesschancestage", new String[]{"bs_point","bs_pointdetno"}, "1=1");
		for(Object[] detno:detnos){
			if(detno[1]!=null){
				detStr += "#" + detno[1];
			}
		}
		
		Map<Object,Object> formMap = null;
		
		if(!"".equals(detStr)){
			detStr = detStr.substring(1);
		}
		
		List<Integer> columnEmptyIndex = new ArrayList<Integer>(); //需要清空商机动态的字段
		
		//自动补段号，先把所有的序号取出来，进行排序后补差，要注意两种情况：刚开始没有序号和只有一个序号的情况
		String[] detStrArr = detStr.split("#");
		int[] detnoIntArr = new int[detStrArr.length];
		List<Integer> intLog = new ArrayList<Integer>();
		if(!"".equals(detStr)){
			for(int i=0;i<detStrArr.length;i++){
				detnoIntArr[i] = Integer.parseInt(detStrArr[i]);
			}
		}
		Arrays.sort(detnoIntArr);
		for(int i=0;i<detnoIntArr.length-1;i++){
			if((detnoIntArr[i+1]-detnoIntArr[i])>1){
				intLog.add(detnoIntArr[i]+1);
			}
		}
		
		formMap = BaseUtil.parseFormStoreToMap(formStore);
		Object pointDetno = formMap.get("bs_pointdetno");
		String[] detnoArr = pointDetno.toString().split("#");
		
		int temp = 0;
		int max = detnoIntArr[detnoIntArr.length-1]+1; 
		if("".equals(detStr)||detnoIntArr.length==1){ //只有一个序号或没有的情况，如只有一个7
			max = 1;
		}
		for(int i=0;i<detnoArr.length;i++){
			if("-1".equals(detnoArr[i])){
				if(temp<intLog.size()){
					columnEmptyIndex.add(temp);
					detnoArr[i] = intLog.get(temp).toString();
					temp++;	
				}else if("".equals(detStr)||detnoIntArr.length==1){
					if(detnoIntArr.length==1){  //跳过只有一个序号的情况
						if(detnoIntArr[0]==max){
							max++;
						}
					}
					columnEmptyIndex.add(max);
					detnoArr[i] = ""+max;
					max ++;
				}else{
					columnEmptyIndex.add(max);
					detnoArr[i] = ""+max;
					max ++;
				}
			}
		}
		
		String detnoJoin = "";
		for(int i=0;i<detnoArr.length;i++){
			detnoJoin += "#" + detnoArr[i];
		}
		detnoJoin = detnoJoin.substring(1);
		
		formMap.remove("bs_pointdetno");
		formMap.put("bs_pointdetno", detnoJoin);
		//BaseUtil.parseMap2Str(map)

		
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(formMap,
				"BusinessChanceStage", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		

		//List<String> setEmptySql = new ArrayList<String>();
		if(columnEmptyIndex.size()!=0){
			//清空补差序号对应的商机动态备用字段
			String dataBackUp = "";
			for(Integer ig:columnEmptyIndex){
				dataBackUp += ",bcd_column" + ig + "=null"; 
			}
			dataBackUp = dataBackUp.substring(1);
			baseDao.execute("update businesschancedata set " + dataBackUp);
		}
	}

	@Override
	public void deleteBusinessChanceStage(int bs_id, String caller) {
		Object bs_name = baseDao.getFieldDataByCondition("BusinessChanceStage",
				"bs_name", "bs_id=" + bs_id);
		int count = baseDao
				.getCount("select count(*) from BusinessChanceStage left join BusinessChanceData on bs_name=bcd_bsname where bcd_bsname='"
						+ bs_name + "' and bs_id=" + bs_id);
		if (count > 0) {
			BaseUtil.showError("商机阶段名称：" + bs_name + " 已被使用，不能删除");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { bs_id });
		// 删除
		baseDao.deleteById("BusinessChanceStage", "bs_id", bs_id);
		// 记录操作
		baseDao.logger.delete(caller, "bs_id", bs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bs_id);
	}

	@Override
	public void updateBusinessChanceStage(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		String detStr = "";
		List<Object[]> detnos = baseDao.getFieldsDatasByCondition("businesschancestage", new String[]{"bs_point","bs_pointdetno"}, "1=1");
		for(Object[] detno:detnos){
			if(detno[1]!=null){
				detStr += "#" + detno[1];
			}
		}
		
		Map<Object,Object> formMap = null;
		
		if(!"".equals(detStr)){
			detStr = detStr.substring(1);
		}
		
		List<Integer> columnEmptyIndex = new ArrayList<Integer>(); //需要清空商机动态的字段
		
		//自动补段号，先把所有的序号取出来，进行排序后补差，要注意两种情况：刚开始没有序号和只有一个序号的情况
		String[] detStrArr = detStr.split("#");
		int[] detnoIntArr = new int[detStrArr.length];
		List<Integer> intLog = new ArrayList<Integer>();
		if(!"".equals(detStr)){
			for(int i=0;i<detStrArr.length;i++){
				detnoIntArr[i] = Integer.parseInt(detStrArr[i]);
			}
		}
		Arrays.sort(detnoIntArr);
		for(int i=0;i<detnoIntArr.length-1;i++){
			if((detnoIntArr[i+1]-detnoIntArr[i])>1){
				intLog.add(detnoIntArr[i]+1);
			}
		}
		
		formMap = BaseUtil.parseFormStoreToMap(formStore);
		Object pointDetno = formMap.get("bs_pointdetno");
		String[] detnoArr = pointDetno.toString().split("#");
		
		int temp = 0;
		int max = detnoIntArr[detnoIntArr.length-1]+1; 
		if("".equals(detStr)||detnoIntArr.length==1){ //只有一个序号或没有的情况，如只有一个7
			max = 1;
		}
		for(int i=0;i<detnoArr.length;i++){
			if("-1".equals(detnoArr[i])){
				if(temp<intLog.size()){
					columnEmptyIndex.add(temp);
					detnoArr[i] = intLog.get(temp).toString();
					temp++;	
				}else if("".equals(detStr)||detnoIntArr.length==1){
					if(detnoIntArr.length==1){  //跳过只有一个序号的情况
						if(detnoIntArr[0]==max){
							max++;
						}
					}
					columnEmptyIndex.add(max);
					detnoArr[i] = ""+max;
					max ++;
				}else{
					columnEmptyIndex.add(max);
					detnoArr[i] = ""+max;
					max ++;
				}
			}
		}
		
		String detnoJoin = "";
		for(int i=0;i<detnoArr.length;i++){
			detnoJoin += "#" + detnoArr[i];
		}
		detnoJoin = detnoJoin.substring(1);
		
		formMap.remove("bs_pointdetno");
		formMap.put("bs_pointdetno", detnoJoin);
		//BaseUtil.parseMap2Str(map)

		
		// 修改BusinessChanceStage
		String formSql = SqlUtil.getUpdateSqlByFormStore(formMap,
				"BusinessChanceStage", "bs_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
		
		//List<String> setEmptySql = new ArrayList<String>();
		if(columnEmptyIndex.size()!=0){
			//清空补差序号对应的商机动态备用字段
			String dataBackUp = "";
			for(Integer ig:columnEmptyIndex){
				dataBackUp += ",bcd_column" + ig + "=null"; 
			}
			dataBackUp = dataBackUp.substring(1);
			baseDao.execute("update businesschancedata set " + dataBackUp);
		}
	}

	@Override
	public void auditBusinessChanceStage(int bs_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceStage",
				"bs_statuscode", "bs_id=" + bs_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bs_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"BusinessChanceStage",
				"bs_statuscode='AUDITED',bs_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "bs_id="
						+ bs_id);
		// 记录操作
		baseDao.logger.audit(caller, "bs_id", bs_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bs_id);
	}

	@Override
	public void resAuditBusinessChanceStage(int bs_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceStage",
				"bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, bs_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"BusinessChanceStage",
				"bs_statuscode='ENTERING',bs_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "bs_id="
						+ bs_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bs_id", bs_id);
		handlerService.afterResAudit(caller, bs_id);
	}

	@Override
	public void submitBusinessChanceStage(int bs_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceStage",
				"bs_statuscode", "bs_id=" + bs_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bs_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"BusinessChanceStage",
				"bs_statuscode='COMMITED',bs_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "bs_id="
						+ bs_id);
		// 记录操作
		baseDao.logger.submit(caller, "bs_id", bs_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bs_id);
	}

	@Override
	public void resSubmitBusinessChanceStage(int bs_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceStage",
				"bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bs_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"BusinessChanceStage",
				"bs_statuscode='ENTERING',bs_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "bs_id="
						+ bs_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bs_id", bs_id);
		handlerService.afterResSubmit(caller, bs_id);
	}
}
