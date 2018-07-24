package com.uas.erp.core.web;

import java.util.List;
import java.util.Map;

public abstract class AbstractDataView<T> extends AbstractResultView<T> {

	private final List<Map<String, Object>> datas;

	public AbstractDataView(DocumentConfig config, List<Map<String, Object>> datas) {
		super(config);
		this.datas = datas;
	}

	@Override
	protected void onRender() {
		for (Map<String, Object> data : datas) {
			try {
				getViewProcesser().processResult(data);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
