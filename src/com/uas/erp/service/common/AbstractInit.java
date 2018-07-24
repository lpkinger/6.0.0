package com.uas.erp.service.common;

import java.util.List;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.InitData;

/**
 * @author yingp
 * 
 */
public abstract class AbstractInit {

	private Integer id;
	private int count;
	private String leadcode;
	private Integer number;
	public String tabName;
	public String keyField;
	public List<InitData> datas;

	public void setCount(int c) {
		this.count = c;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName.toUpperCase();
	}

	public AbstractInit(List<InitData> datas) {
		this.datas = datas;
	}
	
	/**
	 * @param count 有效数据count
	 * @param tabName 表名
	 */
	public void setConfig(int count, String tabName, String keyField) {
		this.id = null;
		this.count = count;
		this.tabName = tabName.toUpperCase();
		this.leadcode = null;
		this.number = null;
		this.keyField = keyField;
	}

	/**
	 * 一次取count个序列号
	 */
	public synchronized int getSeq() {
		if (this.id != null) {
			id += 1;
		} else {
			BaseDao baseDao = getDB();
			String seq = tabName + "_SEQ";
			id = baseDao.getSeqId(seq);
			Object maxId = baseDao.getFieldDataByCondition(tabName, "max(" + keyField + ")", "1=1");
			if(maxId != null) {
				if(Integer.parseInt(maxId.toString()) > id) {
					baseDao.execute("alter sequence " + seq + " increment by " + 
							(Integer.parseInt(maxId.toString()) - id + 1) + " nocache");
					baseDao.getSeqId(seq);
					baseDao.execute("alter sequence " + seq + " increment by 1 Cache 20");
					id = baseDao.getSeqId(seq);
				}
			}
			if (count > 1) {
				baseDao.execute("alter sequence " + seq + " increment by " + (count - 1));
				baseDao.getSeqId(seq);
				baseDao.execute("alter sequence " + seq + " increment by 1");
			}
		}
		return id;
	}

	/**
	 * 一次取多个流水号
	 */
	public synchronized String getCode() {
		String code = null;
		if (number != null) {
			number += 1;
			if (leadcode == null) {
				code = String.valueOf(number);
			} else {
				code = leadcode + String.valueOf(number);
			}
		} else {
			BaseDao baseDao = getDB();
			code = getDB().sGetMaxNumber(tabName, 2);
			if (count > 1) {
				baseDao.execute("UPDATE maxnumbers set mn_number=mn_number+? where upper(mn_tablename)=?", count,
						tabName);
			}
			Object[] objs = baseDao.getFieldsDataByCondition("maxnumbers", "mn_leadcode,mn_number",
					"upper(mn_tablename)='" + tabName + "'");
			if (objs[0] == null)
				leadcode = null;
			else
				leadcode = String.valueOf(objs[0]);
			number = Integer.parseInt(objs[1].toString());
		}
		return code;
	}

	public BaseDao getDB() {
		return (BaseDao) ContextUtil.getBean("baseDao");
	}

	/**
	 * 转正式
	 * 
	 * @param datas
	 */
	public abstract void toFormal();
}
