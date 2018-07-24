package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.InstructionDao;
import com.uas.erp.model.Instruction;
import com.uas.erp.service.oa.InstructionService;

@Service("InstructionService")
public class InstructionServiceImpl implements InstructionService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private InstructionDao instructionDao;
	@Override
	public void saveInstruction(String formStore, String  caller) {
//		formStore = formStore.substring(0, formStore.lastIndexOf("}"));
//		formStore = formStore + ",\"rod_registrant_id\":\"" + employee.getEm_id() +"\"}";
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Instruction", "in_id='" + store.get("in_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		store.put("in_number", store.get("size1") + " (" + store.get("size2") + ") " + store.get("size3") + "号");
		store.remove("size1");
		store.remove("size2");
		store.remove("size3");
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Instruction", new String[]{}, new Object[]{});
//		System.out.println("sql: "+formSql);
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "in_id", store.get("in_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}
	@Override
	public void deleteInstruction(int in_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{in_id});
		//删除
		baseDao.deleteById("Instruction", "in_id", in_id);
		//记录操作
		baseDao.logger.delete(caller, "in_id", in_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{in_id});
	}
	@Override
	public void updateInstructionById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		store.put("in_number", store.get("size1") + " (" + store.get("size2") + ") " + store.get("size3") + "号");
		store.remove("size1");
		store.remove("size2");
		store.remove("size3");
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Instruction", "in_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "in_id", store.get("in_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
	@Override
	public Instruction getInstructionById(int id, String  caller) {
		return instructionDao.getInstructionById(id);
	}
	@Override
	public void submitInstruction(int in_id, String  caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{in_id});
		//执行提交操作
		baseDao.submit("Instruction", "in_id=" + in_id, "in_status", "in_statuscode");
		//记录操作
		baseDao.logger.submit(caller,"in_id",in_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{in_id});
	}
	
	@Override
	public void deleteById(int in_id) {
		instructionDao.delete(in_id);
		
	}
	
	@Override
	public List<Instruction> getList(int page, int pageSize) {
		return instructionDao.getList(page, pageSize);
	}
	@Override
	public int getListCount() {
		return instructionDao.getListCount();
	}
	
	@Override
	public List<Instruction> getByCondition(String condition, int page, int pageSize) {
		return instructionDao.getByCondition(condition, page, pageSize);
	}
	@Override
	public int getSearchCount(String condition) {
		return instructionDao.getSearchCount(condition);
	}

}
