package com.uas.erp.core.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Object转List
 * 
 * @author yingp
 * @example <pre>
 * private List&lt;Object[]&gt; getGoodsArgs(List&lt;Goods&gt; goodses) {
 * 	return new ObjectTransform&lt;Goods&gt;(goodses) {
 * 		&#064;Override
 * 		public Object[] apply(Goods obj) {
 * 			return new Object[] {};
 * 		}
 * 	}.toList();
 * }
 * </pre>
 * @param <T>
 */
public abstract class ObjectTransform<T> {

	private List<T> objs;

	public abstract Object[] apply(T obj);

	public ObjectTransform(List<T> objs) {
		this.objs = objs;
	}

	public List<Object[]> toList() {
		List<Object[]> list = new ArrayList<Object[]>();
		for (T obj : objs) {
			list.add(apply(obj));
		}
		return list;
	}

}
