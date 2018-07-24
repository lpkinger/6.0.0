package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SysnavigationDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.SysNavigation;
import com.uas.erp.service.ma.MASysNavigationService;

@Service
public class MASysNavigationServiceImpl implements MASysNavigationService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SysnavigationDao sysnavigationDao;
	@Override
	@CacheEvict(value="tree",allEntries=true)
	public void save(String save) {
		//保存
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(save);
		int id = 0;
		for(Map<Object, Object> map:maps){
			id = baseDao.getSeqId("SYSNAVIGATION_SEQ");
			map.put("sn_id", baseDao.getSeqId("SYSNAVIGATION_SEQ"));
			baseDao.logger.save("SysNavigation", "sn_id", id);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(maps, "SysNavigation");
		baseDao.execute(gridSql);
		baseDao.execute("update SysNavigation set sn_standarddesc=sn_displayname where sn_show=1 and sn_standarddesc is null");
	}

	@Override
	@CacheEvict(value="tree",allEntries=true)
	public void update(String update) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(update);
		//修改
		for(Map<Object, Object> map:maps){
			baseDao.logger.update("SysNavigation", "sn_id", map.get("sn_id"));
			/**问题反馈单号:2017010288
			 * 处理: 导航栏保存时，更新的部分如果sn_url修改了，更新CommonUse中与原来导航链接相同的配置cu_url
			 * @author wusy
			 */
			Object sn_url = baseDao.getFieldDataByCondition("SysNavigation", "sn_url", "sn_id='"+map.get("sn_id")+"' and sn_url<>'"+parseQuotes(String.valueOf(map.get("sn_url")))+"' ");
			if(sn_url!=null && !"".equals(sn_url)){
				baseDao.updateByCondition("CommonUse", "cu_url='"+parseQuotes(String.valueOf(map.get("sn_url")))+"'", "cu_url='"+parseQuotes(String.valueOf(sn_url))+"'");
			}
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(maps, "SysNavigation", "sn_id");
		baseDao.execute(gridSql);
		baseDao.execute("update SysNavigation set sn_standarddesc=sn_displayname where sn_show=1 and sn_standarddesc is null");
	}

	@Override
	@CacheEvict(value="tree",allEntries=true)
	public void delete(int id) {
		//删除
		deleteChilds(id);
		//记录操作
		baseDao.logger.delete("SysNavigation", "sn_id", id);
	}
	
	public void deleteChilds(int id){
		baseDao.deleteByCondition("SysNavigation", "sn_deleteable='T' AND sn_id=" + id);
		//判断是否有子元素
		boolean bool = baseDao.checkByCondition("SysNavigation", "sn_parentid=" + id);
		if(!bool){
			List<Object> objs = baseDao.getFieldDatasByCondition("SysNavigation", "sn_id", "sn_parentid=" + id);
			for(Object obj:objs){
				deleteChilds(Integer.parseInt("" + obj));
			}
		}
	}

	@Override
	public List<JSONTree> getJSONTreeByParentId(int parentId,String condition) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<SysNavigation> list = sysnavigationDao.getSysNavigationsByParentId(parentId, condition, SystemSession.getUser());
		for(SysNavigation navigation:list){
			tree.add(new JSONTree(navigation, true));
		}
		return tree;
	}
	/**
	 * 将sn_url里面的单引号替换为两个单引号
	 */
	public String parseQuotes(String str){
		int length = str.length();
		boolean bool = false;
		StringBuffer sb = new StringBuffer(); 
		for(int i=0;i<length;i++){
			bool = false;
			char c = str.charAt(i);
			if(String.valueOf(c).equals("'")){
				if(i > 0){//上一个字符不能是'
					if(!String.valueOf(str.charAt(i-1)).equals("'")){
						bool = true;
					} else {
						bool = false;
					}
				}
				if(i < length - 1){//下一个字符不能是'
					if(!String.valueOf(str.charAt(i+1)).equals("'")){
						bool = true;
					} else {
						bool = false;
					}
				}
			}
			if(bool){
				sb.append("''");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	@Override
	@CacheEvict(value="tree",allEntries=true)
	public void addRoot(String save) {
		//添加根节点
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(save);
		int id = 0;
		for(Map<Object, Object> map:maps){
			id = baseDao.getSeqId("SYSNAVIGATION_SEQ");
			map.put("sn_id", baseDao.getSeqId("SYSNAVIGATION_SEQ"));
			baseDao.logger.save("SysNavigation", "sn_id", id);
			}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(maps, "SysNavigation");
			baseDao.execute(gridSql);
	}
}
