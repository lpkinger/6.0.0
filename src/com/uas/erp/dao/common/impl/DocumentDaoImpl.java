package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentDao;
import com.uas.erp.model.Document;
@Repository("documentDao")
public class DocumentDaoImpl extends BaseDao implements DocumentDao {

	@Override
	public List<Document> getDocumentByCondition(String condition) {
		// TODO Auto-generated method stub
		try{
			List<Document> docs = getJdbcTemplate("Document").query("select * from document where "+condition,new BeanPropertyRowMapper<Document>(Document.class));
			return docs;			
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
