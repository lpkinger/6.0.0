package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.Instruction;

public interface InstructionService {
	void saveInstruction(String formStore, String  caller);

	void deleteInstruction(int id, String  caller);

	void updateInstructionById(String formStore, String  caller);

	Instruction getInstructionById(int id, String  caller);

	void submitInstruction(int in_id, String  caller);
	
	void deleteById(int in_id);
	
	List<Instruction> getList(int page, int pageSize);
	
	int getListCount();
	
	List<Instruction> getByCondition(String condition, int page, int pageSize);
	
	int getSearchCount(String condition);

}
