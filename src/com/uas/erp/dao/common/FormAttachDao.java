package com.uas.erp.dao.common;

import java.util.List;

import net.sf.json.JSONObject;

import com.uas.erp.model.FormAttach;

public interface FormAttachDao {
	List<FormAttach> getFormAttachs(String caller, int keyvalue);
	void saveAttach(FormAttach attach);
	JSONObject getFilePath(int fp_id);
}
