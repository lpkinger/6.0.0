package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Instruction;

public interface InstructionDao {
	
	Instruction getInstructionById(int id);
	void delete(int in_id);
	List<Instruction> getByCondition(String condition, int page, int pageSize);
	int getSearchCount(String condition);
	List<Instruction> getList(int page, int pageSize);
	int getListCount();

}
