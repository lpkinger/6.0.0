package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.DataLimit;
import com.uas.erp.model.DataLimitDetail;
import com.uas.erp.model.DataLimitInstace;
import com.uas.erp.service.ma.DataLimitService;

@Service
public class DataLimitServiceImpl implements DataLimitService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<DataLimit> getDataLimits(Integer all_) {
		// TODO Auto-generated method stub
		String querySql = "select * from datalimit";
		querySql += all_ != null ? " order by id_ asc" : " where uasable_=1 order by id_ asc";
		return baseDao.getJdbcTemplate().query(querySql, new BeanPropertyRowMapper<DataLimit>(DataLimit.class));
	}

	@Override
	public DataLimitInstace getDataLimitInstace(Integer empid_,Integer jobid_, Integer limitid_) {
		// TODO Auto-generated method stub
		if (empid_ != null && limitid_ != null && jobid_==null) {
			try {
				return baseDao.getJdbcTemplate().queryForObject(
						"select * from datalimit left join Datalimit_Instance on Datalimit.Id_=Datalimit_Instance.Limit_Id_ where limit_id_=?  and empid_=?",
						new BeanPropertyRowMapper<DataLimitInstace>(DataLimitInstace.class), limitid_, empid_);
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
		if (jobid_ != null && limitid_ != null && empid_==null) {
			try {
				return baseDao.getJdbcTemplate().queryForObject(
						"select * from datalimit left join Datalimit_Instance on Datalimit.Id_=Datalimit_Instance.Limit_Id_ where limit_id_=?  and jobid_=?",
						new BeanPropertyRowMapper<DataLimitInstace>(DataLimitInstace.class), limitid_, jobid_);
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getSourceData(Integer limitId_, String condition) {
		// TODO Auto-generated method stub
		try {
			DataLimit limit = baseDao.getJdbcTemplate().queryForObject("select * from datalimit  where id_=?", new BeanPropertyRowMapper<DataLimit>(DataLimit.class), limitId_);
			if (limit != null) {
				String querySql = "select " + limit.getCodefield_() + " CODE_," + limit.getNamefield_() + " DESC_ FROM " + limit.getTable_();
				if (limit.getCondition_() != null) {
					querySql += " where " + limit.getCondition_();
					querySql += condition != null ? " and " + condition : "";
				} else {
					querySql += condition != null ? " where " + condition : "";
				}
				List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
				SqlRowList sl = baseDao.queryForRowSet(querySql);
				while (sl.next()) {
					lists.add(sl.getCurrentMap());
				}
				return lists;
			}
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object InstanceDataLimit(String formData, String updates, String inserts) {
		// TODO Auto-generated method stub
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formData);
		List<String> sqls = new ArrayList<String>();
		Object id_ = map.get("instanceid_");
		if (id_ != null && !"0".equals(id_) && !"".equals(id_)) {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "datalimit_instance", "instanceid_"));
		} else {			
			id_ = baseDao.getSeqId("datalimit_instance_SEQ");
			map.put("instanceid_", id_);
			sqls.add(SqlUtil.getInsertSqlByMap(map, "datalimit_instance"));
		}
		if (inserts != null) {
			List<Map<Object, Object>> inlists = BaseUtil.parseGridStoreToMaps(inserts);
			for (Map<Object, Object> in : inlists) {
				in.put("id_", baseDao.getSeqId("DATALIMIT_DETAIL_SEQ"));
				in.put("instanceid_", id_);
				sqls.add(SqlUtil.getInsertSqlByMap(in, "DATALIMIT_DETAIL"));
			}
		}
		if (updates != null) {
			List<Map<Object, Object>> uplists = BaseUtil.parseGridStoreToMaps(updates);
			sqls.addAll(SqlUtil.getUpdateSqlbyGridStore(uplists, "DATALIMIT_DETAIL", "id_"));
		}
		baseDao.execute(sqls);
		return id_;
	}

	@Override
	public List<DataLimitDetail> getLimitDetails(Integer instanceId_) {
		// TODO Auto-generated method stub
		if (instanceId_ != null) {
			try {
				return baseDao.getJdbcTemplate().query("select * from datalimit_detail where instanceid_=?", new BeanPropertyRowMapper<DataLimitDetail>(DataLimitDetail.class),
						instanceId_);
			} catch (EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void CopyLimitPower(String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		if (lists.size() > 0) {
			Object fromId = lists.get(0).get("InstanceId_");	
			int LimitId_ = baseDao.getFieldValue("Datalimit_Instance", "limit_id_", "instanceid_=" + fromId, Integer.class);
			StringBuffer emids = new StringBuffer();
			for (Map<Object, Object> m : lists) {
				emids.append(m.get("em_id") + ",");
			}
			String idSql = emids.toString();
			idSql = idSql.substring(0, idSql.lastIndexOf(","));
			baseDao.deleteByCondition("datalimit_detail", " datalimit_detail.instanceid_  in (select instanceid_ from datalimit_instance where  empid_ in (" + idSql
					+ ") and limit_id_=" + LimitId_ + ")");
			baseDao.deleteByCondition("datalimit_instance", "empid_ in (" + idSql + ") and limit_id_=" + LimitId_);
			baseDao.execute("INSERT INTO DATALIMIT_INSTANCE(INSTANCEID_,LIMIT_ID_,EMPID_,NOLIMIT_,NOADDLIMIT_,USEREPORT_,PARENTID_,TYPE_ ,DATE_,CONDITION_,LIMITTYPE_) SELECT "
					+ "DATALIMIT_INSTANCE_SEQ.NEXTVAL," + LimitId_
					+ ",EM_ID,NOLIMIT_,NOADDLIMIT_,USEREPORT_,PARENTID_,TYPE_,SYSDATE,CONDITION_,LIMITTYPE_ FROM DATALIMIT_INSTANCE,EMPLOYEE WHERE LIMIT_ID_=" + LimitId_ + " AND EM_ID IN (" + idSql
					+ ")");
			baseDao.execute("INSERT INTO DATALIMIT_DETAIL(ID_,CODE_,DESC_,INSTANCEID_,SEE_,DELETE_,UPDATE_) SELECT DATALIMIT_DETAIL_SEQ.NEXTVAL,CODE_,DESC_,Datalimit_Instance.INSTANCEID_,SEE_,DELETE_,UPDATE_ FROM DATALIMIT_DETAIL,Datalimit_Instance  "
					+ "WHERE DATALIMIT_DETAIL.INSTANCEID_="+fromId+"  AND DATALIMIT_INSTANCE.INSTANCEID_ IN (SELECT  INSTANCEID_ FROM Datalimit_Instance WHERE  LIMIT_ID_ ="+LimitId_+" AND Datalimit_Instance.EMPID_ IN ("+idSql+") )");
		}
	}

	@Override
	public void deleteLimitPower(String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls=new ArrayList<String>();
		List<String> ids=new ArrayList<String>();
		for(Map<Object,Object> map:lists){
			sqls.add(SqlUtil.getDeleteSql("DATALIMIT_DETAIL","ID_="+map.get("id_")));
			Object a=baseDao.getFieldDataByCondition("DATALIMIT_DETAIL", "instanceid_", " id_ in("+map.get("id_")+")");
			ids.add(a.toString());
		}
		baseDao.execute(sqls);
		sqls.clear();
		for(String id:ids){
			SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet("select * from DATALIMIT_DETAIL where instanceid_="+id+"");
			if(!(rs.next())){
				sqls.add(SqlUtil.getDeleteSql("DATALIMIT_INSTANCE", "instanceid_ in("+id+")"));
			}
		}
		sqls.add("delete from datalimit_instance where INSTANCEID_ not in(select INSTANCEID_ from datalimit_detail)");
		sqls.add("delete from datalimit_detail where  INSTANCEID_ not in(select INSTANCEID_ from datalimit_instance)");
		baseDao.execute(sqls);
	}
}
