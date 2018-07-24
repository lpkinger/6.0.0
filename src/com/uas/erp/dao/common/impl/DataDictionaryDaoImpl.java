package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DataDictionaryDao;
import com.uas.erp.model.DataDictionary;
import com.uas.erp.model.DataDictionaryDetail;

@Repository
public class DataDictionaryDaoImpl extends BaseDao implements DataDictionaryDao {
	@Override
	public DataDictionary getDataDictionary(String table) {
		try {
			DataDictionary dictionary = getJdbcTemplate().queryForObject(
					"select *  from User_Tab_Comments where table_name=?",
					new BeanPropertyRowMapper<DataDictionary>(DataDictionary.class), table);
			List<DataDictionaryDetail> details = getJdbcTemplate().query(
					"select a.table_name,a.column_name,a.data_type,a.data_length,comments from  User_Tab_Columns a left join User_Col_Comments  b on  a.table_name=b.table_name and a.column_name=b.column_name  where a.table_name=? order by column_id",
					new BeanPropertyRowMapper<DataDictionaryDetail>(DataDictionaryDetail.class), dictionary.getTable_name());
			try {
				List<DataDictionaryDetail.Link> links = getJdbcTemplate().query(
						"select * from DataLink where dl_tablename=? order by dl_fieldname",
						new BeanPropertyRowMapper<DataDictionaryDetail.Link>(DataDictionaryDetail.Link.class),
						dictionary.getTable_name());
				for (DataDictionaryDetail detail : details) {
					List<DataDictionaryDetail.Link> myLink = new ArrayList<DataDictionaryDetail.Link>();
					for (DataDictionaryDetail.Link link : links) {
						if (detail.getColumn_name().equalsIgnoreCase(link.getDl_fieldname()))
							myLink.add(link);
					}
					detail.setLinks(myLink);
				}
			} catch (EmptyResultDataAccessException e) {

			}
			dictionary.setDataDictionaryDetails(details);
			return dictionary;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
