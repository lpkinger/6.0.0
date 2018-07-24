package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.News;

public interface NewsService {

	void saveNews(News news, String caller,String attachs);

	void updateNews(String formStore, String caller);

	void deleteNews(int ne_id, String caller);

	News getNews(int ne_id);

	void sendComment(int id, String comment);

	List<News> getNewsSnapshot(Integer page, Integer pageSize);

	int getNewsCount();
}
