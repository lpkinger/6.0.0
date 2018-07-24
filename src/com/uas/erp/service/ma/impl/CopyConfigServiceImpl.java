package com.uas.erp.service.ma.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.CopyConfigService;

@Service
public class CopyConfigServiceImpl implements CopyConfigService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCopyConfigByCaller(String caller,String formCaller,String gridStore) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		for (Map<Object, Object> map : store) {
			//修改
			if (StringUtil.hasText(map.get("cc_caller"))) {
				baseDao.updateByCondition("copyconfigs", "cc_copyvalue='"+map.get("cc_copyvalue")+"'", "cc_caller='"+map.get("cc_caller")+"'"
				+" AND cc_field='"+map.get("cc_field")+"'"+" AND cc_findkind='"+map.get("cc_findkind")+"'");
			}else {
			//插入
				map.put("cc_caller", formCaller);
				// 当前编号的记录已经存在,不能新增!
				boolean bool = baseDao.checkByCondition("copyconfigs", "cc_caller='" + map.get("cc_caller") + "'"
				+" AND cc_field='"+map.get("cc_field")+"'"+" AND cc_findkind='"+map.get("cc_findkind")+"'");
				if (!bool) {
					BaseUtil.showError("当前单据复制配置记录已存在");
				}
				String sql = SqlUtil.getInsertSqlByMap(map, "copyconfigs");
				baseDao.execute(sql);
			}
		}
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteCopyConfigByCondition(String condition) {
		baseDao.deleteByCondition("copyconfigs", condition);
	}
	
}
