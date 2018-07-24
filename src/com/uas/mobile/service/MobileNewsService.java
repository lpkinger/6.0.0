package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.News;
import com.uas.erp.model.Note;

public interface MobileNewsService {
 List<News>getNewsByPage(int page, int pageSize);
 News getNewsById(Integer ne_id);
 List<Note> getNotesByPage(Integer page, Integer pageSize);
 Note getNoteById(Integer id);
 Map<String , Object> getMessageDetailById(Integer id);
}
