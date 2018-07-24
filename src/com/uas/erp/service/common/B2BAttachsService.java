package com.uas.erp.service.common;

import java.util.Set;

import com.uas.b2b.model.TenderAttach;

public interface B2BAttachsService {

	/**
	 * 更新程序
	 * 
	 * @param tAttachs
	 *            平台附件集合
	 * @return id
	 * 			附件ID集
	 */
	String getAttaches(Set<TenderAttach> tAttachs);

	/**
	 * 程序更新日志
	 * 
	 * @param id
	 * 			附件ID集
	 * @return 
	 * 			平台附件集合
	 */
	Set<TenderAttach> parseAttachs(String id);

}
