package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.derby.tools.sysinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.MultiFormService;

@Service
public class MultiFormServiceImpl implements MultiFormService {
	@Autowired
	private BaseDao baseDao;

	@Override
	@CacheEvict(value = "formpanel", allEntries = true)
	public Map<String, Object> add(String form, String formDetail,
			String gridStore, String language, Employee employee, String type) {
		if ("crm".equals(type)) {// 市场调研
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
			boolean bool = baseDao.checkByCondition("Reporttemplate",
					"rt_code='" + store.get("rt_code") + "'");
			if (!bool) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("common.save_codeHasExist"));
			}
			String caller = null;// 用code做caller
			if (store.get("rt_code") == null
					|| "".equals("" + store.get("rt_code"))
					|| "null".equals("" + store.get("rt_code"))) {
				caller = baseDao.sGetMaxNumber("Reporttemplate", 2);
				store.put("rt_code", caller);
			} else {
				caller = store.get("rt_code") + "";
			}
			int rt_id = baseDao.getSeqId("Reporttemplate_SEQ");
			int fo_id = baseDao.getSeqId("form_SEQ");
			store.put("rt_id", rt_id);
			store.put("rt_foid", fo_id);
			// 添加到Reporttemplate
			String inSql = SqlUtil.getInsertSqlByFormStore(store,
					"Reporttemplate", new String[] {}, new Object[] {});
			List<String> sqls = new ArrayList<String>();
			sqls.add(inSql);
			// 插入一条记录到form表
			baseDao.execute("insert into form(fo_id,fo_table,fo_codefield,fo_title,"
					+ "fo_caller,fo_seq,fo_keyfield,fo_detailtable,fo_detailkeyfield,fo_detailmainkeyfield,fo_detailseq,"
					+ "fo_detailgridfixedcol,fo_button4add,fo_button4rw,fo_isautoflow,fo_detailgridorderby,fo_detaildetnofield) select "
					+ fo_id
					+ ",fo_table,fo_codefield,fo_title,'"
					+ caller
					+ "',fo_seq,fo_keyfield,fo_detailtable,fo_detailkeyfield,"
					+ "fo_detailmainkeyfield,fo_detailseq,fo_detailgridfixedcol,fo_button4add,fo_button4rw,fo_isautoflow,fo_detailgridorderby,fo_detaildetnofield from form where fo_caller='MarketTaskReport'");
			List<Map<Object, Object>> fDetail = BaseUtil
					.parseGridStoreToMaps(formDetail);
			// 修改formdetail必要信息，如fd_foid
			for (Map<Object, Object> m : fDetail) {
				m.put("fd_id", baseDao.getSeqId("formdetail_SEQ"));
				m.put("fd_foid", fo_id);
				sqls.add(SqlUtil.getInsertSqlByMap(m, "formdetail"));
			}
			// 修改detailGrid必要信息，如caller
			List<Map<Object, Object>> gStore = BaseUtil
					.parseGridStoreToMaps(gridStore);
			for (Map<Object, Object> m : gStore) {
				if ("mrd_costname".equals(m.get("dg_field") + "")) {
					m.put("dg_findfunctionname", "FeeCategorySet|fcs_itemname");// detailGrid从前台返回的数据没有这个
				}
				m.put("dg_id", baseDao.getSeqId("detailgrid_SEQ"));
				m.put("dg_caller", caller);
				sqls.add(SqlUtil.getInsertSqlByMap(m, "detailgrid"));
			}
			// 设置detailGrid的dbfind
			sqls.add("insert into dbfindsetgrid (ds_id,ds_detno,ds_caller,ds_gridfield,ds_dbfindfield) select dbfindsetgrid_seq.nextval,rownum,'"
					+ caller
					+ "',ds_gridfield,ds_dbfindfield from dbfindsetgrid where ds_caller='MarketTaskReport'");
			// 设置datalistcombo
			sqls.add("insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_caller,dlc_fieldname,dlc_display) "
					+ "select datalistcombo_seq.nextval,dlc_value,dlc_value_en,dlc_value_tw,'"
					+ caller
					+ "',dlc_fieldname,dlc_display from datalistcombo where dlc_caller='MarketTaskReport'");
			baseDao.execute(sqls);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("caller", caller);
			data.put("fo_id", fo_id);
			data.put("cond", "rt_idIS" + rt_id);
			data.put("type", type);
			return data;
		}
		if ("ProductTrain".equals(type)) {// 产品培训
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
			String caller = null;// 用code做caller
			if (store.get("px_code") == null
					|| "".equals("" + store.get("px_code"))
					|| "null".equals("" + store.get("px_code"))) {
				caller = baseDao.sGetMaxNumber("PXReporttemplate", 2);
				store.put("px_code", caller);
			} else {
				caller = store.get("px_code") + "";
			}
			int rt_id = baseDao.getSeqId("PXReporttemplate_SEQ");
			int fo_id = baseDao.getSeqId("form_SEQ");
			store.put("px_id", rt_id);
			store.put("px_foid", fo_id);
			// 添加到Reporttemplate
			String inSql = SqlUtil.getInsertSqlByFormStore(store,
					"PXReporttemplate", new String[] {}, new Object[] {});
			List<String> sqls = new ArrayList<String>();
			sqls.add(inSql);
			// 插入一条记录到form表
			baseDao.execute("insert into form(fo_id,fo_table,fo_codefield,fo_title,"
					+ "fo_caller,fo_seq,fo_keyfield,fo_detailtable,fo_detailkeyfield,fo_detailmainkeyfield,fo_detailseq,"
					+ "fo_detailgridfixedcol,fo_button4add,fo_button4rw,fo_isautoflow,fo_detailgridorderby,fo_detaildetnofield) select "
					+ fo_id
					+ ",fo_table,fo_codefield,fo_title,'"
					+ caller
					+ "',fo_seq,fo_keyfield,fo_detailtable,fo_detailkeyfield,"
					+ "fo_detailmainkeyfield,fo_detailseq,fo_detailgridfixedcol,fo_button4add,fo_button4rw,fo_isautoflow,fo_detailgridorderby,fo_detaildetnofield from form where fo_caller='TrainReport'");
			List<Map<Object, Object>> fDetail = BaseUtil
					.parseGridStoreToMaps(formDetail);
			// 修改formdetail必要信息，如fd_foid
			for (Map<Object, Object> m : fDetail) {
				m.put("fd_id", baseDao.getSeqId("formdetail_SEQ"));
				m.put("fd_foid", fo_id);
				sqls.add(SqlUtil.getInsertSqlByMap(m, "formdetail"));
			}
			// 设置datalistcombo
			sqls.add("insert into datalistcombo(dlc_id,dlc_value,dlc_value_en,dlc_value_tw,dlc_caller,dlc_fieldname,dlc_display) "
					+ "select datalistcombo_seq.nextval,dlc_value,dlc_value_en,dlc_value_tw,'"
					+ caller
					+ "',dlc_fieldname,dlc_display from datalistcombo where dlc_caller='TrainReport'");
			baseDao.execute(sqls);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("caller", caller);
			data.put("fo_id", fo_id);
			data.put("cond", "px_idIS" + rt_id);
			data.put("type", type);
			return data;
		}
		return null;
	}

	@Override
	public void mdelete(int id, String language, Employee employee, String type) {
		if ("crm".equals(type)) {
			Object obj = baseDao.getFieldDataByCondition("form", "fo_caller",
					"fo_id=" + id);
			Object rt_code = baseDao.getFieldDataByCondition("Reporttemplate",
					"rt_code", "rt_foid=" + id);
			int count = baseDao
					.getCount("select count(*) from Reporttemplate left join project on prj_customercode=rt_code left join ExpandPlan on ep_tpcode=rt_code where "
							+ "(prj_customercode='"
							+ rt_code
							+ "' or ep_tpcode='"
							+ rt_code
							+ "') and rt_foid="
							+ id);
			if (count > 0) {
				BaseUtil.showError("模板编号：" + rt_code + " 已被使用，不能删除");
			}
			// 删除Reporttemplate
			baseDao.deleteByCondition("Reporttemplate", "rt_foid=" + id);
			// 删除formDetail
			baseDao.deleteByCondition("FormDetail", "fd_foid=" + id);
			// 删除form
			baseDao.deleteById("Form", "fo_id", id);
			
			// detailgrid和dbfind
			if (obj != null) {
				baseDao.deleteByCondition("DetailGrid", "dg_caller='" + obj
						+ "'");
				baseDao.deleteByCondition("dbfindsetgrid", "ds_caller='" + obj
						+ "'");
			}
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.delete", language), BaseUtil
					.getLocalMessage("msg.deleteSuccess", language),
					"Form|fo_id=" + id));
		}
		if ("ProductTrain".equals(type)) {
			Object obj = baseDao.getFieldDataByCondition("form", "fo_caller",
					"fo_id=" + id);
			// 删除Reporttemplate
			baseDao.deleteByCondition("PXReporttemplate", "px_foid=" + id);
			// 删除form
			baseDao.deleteById("Form", "fo_id", id);
			// 删除formDetail
			baseDao.deleteByCondition("FormDetail", "fd_foid=" + id);
			// detailgrid和dbfind
			if (obj != null) {
				baseDao.deleteByCondition("DetailGrid", "dg_caller='" + obj
						+ "'");
				baseDao.deleteByCondition("dbfindsetgrid", "ds_caller='" + obj
						+ "'");
			}
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.delete", language), BaseUtil
					.getLocalMessage("msg.deleteSuccess", language),
					"Form|fo_id=" + id));
		}
	}

	@Override
	@CacheEvict(value = "formpanel", allEntries = true)
	public void update(String form, String add, String update, String del,
			String language, Employee employee, String type) {
		if ("crm".equals(type)) {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
			List<String> sqls = new ArrayList<String>();
			// 修改form
			String sql = SqlUtil.getUpdateSqlByFormStore(store,
					"Reporttemplate", "rt_id");
			sqls.add(sql);
			// 修改formDetail
			// added
			List<Map<Object, Object>> gstore = BaseUtil
					.parseGridStoreToMaps(add);
			for (Map<Object, Object> m : gstore) {
				m.put("fd_foid", store.get("rt_foid"));
				// FormDetail 触发器
				sqls.add(SqlUtil.getInsertSqlByMap(m, "FormDetail"));
			}
			// updated
			gstore = BaseUtil.parseGridStoreToMaps(update);
			for (Map<Object, Object> m : gstore) {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "FormDetail",
						"fd_id"));
			}
			// deleted
			gstore = BaseUtil.parseGridStoreToMaps(del);
			for (Map<Object, Object> m : gstore) {
				sqls.add(SqlUtil.getDeleteSql("FormDetail",
						"fd_id=" + m.get("fd_id")));
			}
			baseDao.execute(sqls);
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.update", language), BaseUtil
					.getLocalMessage("msg.updateSuccess", language),
					"Form|fo_id=" + store.get("rt_foid")));
		}
		if ("ProductTrain".equals(type)) {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(form);
			List<String> sqls = new ArrayList<String>();
			// 修改form
			String sql = SqlUtil.getUpdateSqlByFormStore(store,
					"PXReporttemplate", "px_id");
			sqls.add(sql);
			// 修改formDetail
			// added
			List<Map<Object, Object>> gstore = BaseUtil
					.parseGridStoreToMaps(add);
			for (Map<Object, Object> m : gstore) {
				m.put("fd_foid", store.get("px_foid"));
				// FormDetail 触发器
				sqls.add(SqlUtil.getInsertSqlByMap(m, "FormDetail"));
			}
			// updated
			gstore = BaseUtil.parseGridStoreToMaps(update);
			for (Map<Object, Object> m : gstore) {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "FormDetail",
						"fd_id"));
			}
			// deleted
			gstore = BaseUtil.parseGridStoreToMaps(del);
			for (Map<Object, Object> m : gstore) {
				sqls.add(SqlUtil.getDeleteSql("FormDetail",
						"fd_id=" + m.get("fd_id")));
			}
			baseDao.execute(sqls);
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.update", language), BaseUtil
					.getLocalMessage("msg.updateSuccess", language),
					"Form|fo_id=" + store.get("px_foid")));
		}
	}

	@Override
	@CacheEvict(value = "gridpanel", allEntries = true)
	public void updateDetailGrid(String add, String update, String del,
			String language, Employee employee) {
		List<String> sqls = new ArrayList<String>();
		// added
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(add);
		for (Map<Object, Object> m : gstore) {
			// DetailGrid 触发器
			sqls.add(SqlUtil.getInsertSqlByMap(m, "DetailGrid"));
		}
		// updated
		gstore = BaseUtil.parseGridStoreToMaps(update);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(m, "DetailGrid", "dg_id"));
		}
		// deleted
		gstore = BaseUtil.parseGridStoreToMaps(del);
		for (Map<Object, Object> m : gstore) {
			sqls.add(SqlUtil.getDeleteSql("DetailGrid",
					"dg_id=" + m.get("dg_id")));
		}
		baseDao.execute(sqls);
	}
	
	public void deleteButtonGroup(String caller){
		//删除历史分组
		String del = "delete buttongroupset where bgs_caller = '"+caller+"'";
		baseDao.execute(del);
	}
	
	@SuppressWarnings("unchecked")
	public void saveButtonGroup(String jsonstr,String caller){
		//解析jsonstr
		List<Map<Object, Object>> dataMaps = BaseUtil.parseGridStoreToMaps(jsonstr);
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<Object, Object> group : dataMaps) {
			String bgs_group = String.valueOf(group.get("text"));
			List<Map<Object, Object>> buttons = (List<Map<Object, Object>>) group.get("items");
			for (Map<Object, Object> button : buttons) {
				sql.append("INSERT INTO BUTTONGROUPSET(BGS_ID,BGS_DETNO,BGS_XTYPE,BGS_NAME,BGS_GROUP,BGS_CALLER,BGS_GROUPID) "
						+ "values(BUTTONGROUPSET_SEQ.nextval,"
						+ "'"+button.get("index")+"',"
						+ "'"+button.get("xtype")+"',"
						+ "'"+button.get("text")+"',"
						+ "'"+bgs_group+"',"
						+ "'"+caller+"',"
						+ "'"+button.get("groupid")+"');");
			}
		}
		sql.append("end;");
		//删除历史分组
		String del = "delete buttongroupset where bgs_caller = '"+caller+"'";
		baseDao.execute(del);
		//执行插入
		baseDao.execute(sql.toString());
	}
	
	public void updateButton(String caller,String groupid,String oldText,String newText){
		//判断该分组是否存在该名称
		boolean Exist = baseDao.checkIf("buttongroupset", "bgs_caller='"+caller+"' and bgs_groupid='"+groupid+"' and bgs_name='"+oldText+"'");
		if(Exist){
			boolean haveName = baseDao.checkIf("buttongroupset", "bgs_caller='"+caller+"' and bgs_name='"+newText+"'");
			if(!haveName){
				String sql = "update buttongroupset set bgs_name ='"+newText+"' where bgs_caller='"+caller+"' and bgs_groupid='"+groupid+"' and bgs_name='"+oldText+"'";
				baseDao.execute(sql);
			}else{
				BaseUtil.showError("该名称已被使用，请修改后再保存");
			}
		}else{
			BaseUtil.showError("数据有误，请保存按钮分组后再修改按钮名称！");
		}
	}
}
