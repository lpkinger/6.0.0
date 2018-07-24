package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SearchDao;
import com.uas.erp.model.DataDictionaryDetail;
import com.uas.erp.model.SearchTemplate;

@Repository
public class SearchDaoImpl extends BaseDao implements SearchDao {

	@Override
	@Cacheable(value = "searchtemplate", key = "#sob + '@' + #caller + 'getSearchTemplates'")
	public List<SearchTemplate> getSearchTemplates(String caller, String sob) {
		try {
			List<SearchTemplate> searchs = getJdbcTemplate().query(
					"select *  from SearchTemplate where st_caller=? order by st_detno",
					new BeanPropertyRowMapper<SearchTemplate>(SearchTemplate.class), caller);
			for (SearchTemplate s : searchs) {
				try {
					List<SearchTemplate.Grid> items = getJdbcTemplate().query(
							"select *  from SearchTemplateGrid where stg_stid=? order by stg_detno",
							new BeanPropertyRowMapper<SearchTemplate.Grid>(SearchTemplate.Grid.class), s.getSt_id());
					try {
						List<DataDictionaryDetail.Link> links = getJdbcTemplate()
								.query(
										"select *  from DataLink where exists (select 1 from SearchTemplateGrid left join SearchTemplate on st_id=stg_stid where st_id=? and stg_table=dl_tablename and stg_field=dl_fieldname)",
										new BeanPropertyRowMapper<DataDictionaryDetail.Link>(
												DataDictionaryDetail.Link.class), s.getSt_id());
						for (SearchTemplate.Grid grid : items) {
							List<DataDictionaryDetail.Link> myLink = new ArrayList<DataDictionaryDetail.Link>();
							for (DataDictionaryDetail.Link link : links) {
								if (grid.getStg_table().equals(link.getDl_tablename())
										&& grid.getStg_field().equals(link.getDl_fieldname()))
									myLink.add(link);
							}
							grid.setLinks(myLink);
						}
					} catch (EmptyResultDataAccessException e) {

					} catch (Exception e) {

					}
					s.setItems(items);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
				try {
					List<SearchTemplate.Property> properties = getJdbcTemplate().query(
							"select *  from SearchTemplateProp where st_id=? order by stg_field,num",
							new BeanPropertyRowMapper<SearchTemplate.Property>(SearchTemplate.Property.class),
							s.getSt_id());
					s.setProperties(properties);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
			}
			return searchs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "searchtemplate", key = "#sob + '@' + #sId + 'getSearchTemplate'")
	public SearchTemplate getSearchTemplate(Integer sId, String sob) {
		try {
			SearchTemplate search = getJdbcTemplate().queryForObject(
					"select *  from SearchTemplate where st_id=? order by st_detno",
					new BeanPropertyRowMapper<SearchTemplate>(SearchTemplate.class), sId);
			try {
				List<SearchTemplate.Grid> items = getJdbcTemplate().query(
						"select *  from SearchTemplateGrid where stg_stid=? order by stg_detno",
						new BeanPropertyRowMapper<SearchTemplate.Grid>(SearchTemplate.Grid.class), sId);
				search.setItems(items);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			try {
				List<SearchTemplate.Property> properties = getJdbcTemplate().query(
						"select *  from SearchTemplateProp where st_id=? order by stg_field,num",
						new BeanPropertyRowMapper<SearchTemplate.Property>(SearchTemplate.Property.class),
						search.getSt_id());
				search.setProperties(properties);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return search;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
