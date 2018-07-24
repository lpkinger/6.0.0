package com.uas.erp.service.crm.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.ContactService;
@Service
public class ContactServiceImpl implements ContactService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveContact(String formStore, String gridStore,
			 String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存Contact
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CONTACT", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		Object enUU=baseDao.getFieldDataByCondition("customer","cu_uu","cu_code='"+store.get("ct_cucode")+"'");
		if(enUU!=null&&!"".equals(enUU)){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("enUU", enUU.toString());
			params.put("userTel", store.get("ct_mobile").toString());
			try {
				Response response = HttpUtil.sendGetRequest("http://www.ubtob.com/public/queriable/userUUByTelAndEnUU", params, false);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
					if (backInfo != null) {
						String emUU= backInfo.get("userUU")==null?"0":backInfo.get("userUU").toString();
						String userImid= backInfo.get("userImid")==null?"0":backInfo.get("userImid").toString();
						if(!"0".equals(emUU)){
							baseDao.execute("update contact set ct_uu="+emUU+",ct_imid="+userImid +" where ct_id="+store.get("ct_id"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		baseDao.logger.save(caller, "ct_id", store.get("ct_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteContact(int id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//删除AssistRequire
		baseDao.deleteById("CONTACT", "ct_id", id);
		//记录操作
		baseDao.logger.delete(caller, "ct_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public void updateContactById(String formStore, String gridStore,
			 String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{gstore});
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "Contact", "ct_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ct_id") == null || s.get("ct_id").equals("") || s.get("ct_id").equals("0") ||
					Integer.parseInt(s.get("ct_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("CONTACT_SEQ");
				s.put("ct_cuid",  store.get("cu_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "Contact", new String[]{"ct_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object[] data = baseDao.getFieldsDataByCondition("contact", new String[]{"ct_name","ct_officephone","ct_mobile","ct_position","ct_personemail"}, 
				"ct_cuid="+store.get("cu_id")+" and ct_remark='是'");
		if(data!=null && data.length>0) {
			baseDao.execute("update customer set cu_contact=?,cu_tel=?,cu_mobile=?,cu_degree=?,cu_email=? where cu_id="+store.get("cu_id"),data);
		}
		Object enUU=baseDao.getFieldDataByCondition("customer","cu_uu","cu_id='"+store.get("cu_id")+"'");
		for(Map<Object, Object> s:gstore){
			Object enMobile = s.get("ct_mobile");
			Object ctId = s.get("ct_id");
			if(enUU!=null&&!"".equals(enUU)){
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("enUU", enUU.toString());
				params.put("userTel", enMobile.toString());
				try {
					Response response = HttpUtil.sendGetRequest("http://uas.ubtob.com/public/queriable/userUUByTelAndEnUU", params, false);
					if (response.getStatusCode() == HttpStatus.OK.value()) {
						Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
						if ((Boolean) backInfo.get("ok")) {
							String emUU= backInfo.get("userUU")==null?"0":backInfo.get("userUU").toString();
							String userImid= backInfo.get("userImid")==null?"0":backInfo.get("userImid").toString();
							if(!"0".equals(emUU)){
								baseDao.execute("update contact set ct_uu="+emUU+",ct_imid="+userImid +" where ct_id="+ctId.toString());
							}
						}else {
							baseDao.execute("update contact set ct_uu=null,ct_imid=null where ct_id="+ctId.toString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Contact", "save", "after", new Object[]{store, gstore});
	}
}
