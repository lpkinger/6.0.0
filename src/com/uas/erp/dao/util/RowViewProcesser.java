package com.uas.erp.dao.util;

import java.util.Map;

import com.uas.erp.core.web.AbstractResultView;

/**
 * {@code Map<String, Object>}输出到view
 * 
 * @author yingp
 *
 */
public abstract class RowViewProcesser<T> extends MapResultProcesser {

	private final AbstractResultView<T> view;

	public RowViewProcesser(AbstractResultView<T> view) {
		this.view = view;
	}

	protected abstract void processResult(Map<String, Object> param, AbstractResultView<T> view, T row) throws Exception;

	@Override
	public void processResult(Map<String, Object> param) throws Exception {
		processResult(param, view, view.getCurrentRow());
	}
}
