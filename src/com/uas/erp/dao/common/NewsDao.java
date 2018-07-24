package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Employee;
import com.uas.erp.model.News;

public interface NewsDao {
	void saveNews(News news, Employee em,String attachs);

	News getNews(int ne_id);

	List<News> getNews(int page, int pageSize);
}
