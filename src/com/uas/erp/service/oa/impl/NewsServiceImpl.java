package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
//import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.dao.common.NewsDao;
import com.uas.erp.model.News;
import com.uas.erp.service.oa.NewsService;

@Service
public class NewsServiceImpl implements NewsService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private NewsDao newsDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveNews(News news, String caller,String attachs) {
		news.setNe_browsenumber(0);
		newsDao.saveNews(news, SystemSession.getUser(),attachs);
		// 记录操作
		baseDao.logger.save(caller, "ne_id", news.getNe_id());
	}

	@Override
	public void updateNews(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改News
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "News", "ne_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ne_id", store.get("ne_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void deleteNews(int ne_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.afterDel(caller, new Object[] { ne_id });
		// 删除purchase
		baseDao.deleteById("News", "ne_id", ne_id);
		baseDao.deleteById("NewsComment", "nc_neid", ne_id);
		// 记录操作
		baseDao.logger.delete(caller, "ne_id", ne_id);
		// 执行删除后的其它逻辑
		handlerService.handler("News", "delete", "after", new Object[] { ne_id });

	}

	@Override
	public News getNews(int ne_id) {
		boolean bool=baseDao.checkIf("readstatus","sourcekind='new' and mainid="+ne_id+" and man="+SystemSession.getUser().getEm_id());
		if(!bool)baseDao.logger.read("new",ne_id);
		return newsDao.getNews(ne_id);
	}

	@Override
	public void sendComment(int nc_id, String comment) {
		int id = baseDao.getSeqId("NEWSCOMMENT_SEQ");
		baseDao.execute("insert into newscomment (nc_id,nc_neid,nc_caster,nc_comment)values('" + id + "','" + nc_id + "','"
				+ SystemSession.getUser().getEm_name() + "','" + comment + "')");
	}

	@Override
	public List<News> getNewsSnapshot(Integer page, Integer pageSize) {
		return newsDao.getNews(page, pageSize);
	}

	@Override
	public int getNewsCount() {
		return baseDao.getCount("select count(1) from news");
	}
}
