package com.uas.erp.service.oa.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.BookStorageService;

@Service("bookStorageService")
public class BookStorageServiceImpl implements BookStorageService{
	@Autowired
	private BaseDao baseDao;
	//@Autowired
	//private BookStorageDao BookStorageDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBookStorage(String formStore, String gridStore, String  caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BookStorage", "bs_code='" + store.get("bs_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.basicData.BookStorage.save_codeHasExist"));
		}
		//检查明细行是否重复
		checkGrid(grid);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[]{store});
		//保存BookStorage
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BookStorage", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存BookStorageDetail
		for (Map<Object, Object> s : grid) {
			s.put("bsd_id", baseDao.getSeqId("BookStorageDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BookStorageDetail");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store});
		
	}

	@Override
	public void updateBookStorageById(String formStore, String param, String  caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("BookStorage", "bs_code='" + store.get("bs_code") + "' and bs_id <>"+store.get("bs_id"));
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.basicData.BookStorage.save_codeHasExist"));
		}
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + store.get("bs_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		for(int i=0;i<gstore.size();i++){
			SqlRowList rs=baseDao.queryForRowSet("select bsd_detno from BookStorageDetail where bsd_id="+store.get("bsd_id")+" and bsd_detno<>'"+gstore.get(i).get("bsd_detno")+"' and bsd_code='"+gstore.get(i).get("bsd_code")+"'");
			if(rs.next()){
				BaseUtil.showError("明细行图书类型编号重复，行号："+rs.getString("bsd_detno")+"&nbsp&nbsp"+gstore.get(i).get("bsd_detno").toString());
			}
		}
		checkGrid(gstore);
		//修改BookStorage
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BookStorage", "bs_id");
		baseDao.execute(formSql);
		//修改BookStorageDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "BookStorageDetail", "bsd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bsd_id") == null || s.get("bsd_id").equals("") || s.get("bsd_id").equals("0") ||
					Integer.parseInt(s.get("bsd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOOKSTORAGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BookStorageDetail", new String[]{"bsd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void deleteBookStorage(int bs_id, String  caller) {
		//只能删除在录入的采购单!
				Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + bs_id);
				StateAssert.delOnlyEntering(status);
				baseDao.delCheck("BookStorage",bs_id);
				//执行删除前的其它逻辑
				handlerService.beforeDel(caller,new Object[]{bs_id});
				//删除BookStorage
				baseDao.deleteById("BookStorage", "bs_id", bs_id);
				//删除BookStorageDetail
				baseDao.deleteById("BookStorageDetail", "bsd_bsid", bs_id);
				//记录操作
				baseDao.logger.delete(caller, "bs_id", bs_id);
				//执行删除后的其它逻辑
				handlerService.afterDel(caller,new Object[]{bs_id});
		
	}

	@Override
	public void submitBookStorage(int bs_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
				Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + bs_id);
				StateAssert.submitOnlyEntering(status);
				//执行提交前的其它逻辑
				handlerService.beforeSubmit(caller, new Object[]{bs_id});
				//执行提交操作
				baseDao.submit("BookStorage", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
				//记录操作
				baseDao.logger.submit(caller, "bs_id", bs_id);;
				//执行提交后的其它逻辑
				handlerService.afterSubmit(caller, new Object[]{bs_id});
		
	}

	@Override
	public void resSubmitBookStorage(int bs_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("BookStorage", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bs_id", bs_id);		
	}

	@Override
	public void auditBookStorage(int bs_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + bs_id);
				StateAssert.auditOnlyCommited(status);
				//执行审核前的其它逻辑
				handlerService.beforeAudit(caller,new Object[]{bs_id});
				//执行审核操作
				baseDao.audit("BookStorage", "bs_id=" + bs_id, "bs_status", "bs_statuscode", "bs_auditdate", "bs_auditman");
				//记录操作
				baseDao.logger.audit(caller, "bs_id", bs_id);
				//执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[]{bs_id});
		
	}

	@Override
	public void resAuditBookStorage(int bs_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BookStorage", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resAuditOnlyAudit(status);
			//执行反审核操作
			baseDao.resOperate("BookStorage", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
			//记录操作
			baseDao.logger.resAudit(caller, "bs_id", bs_id);
	}
	/**
	 * 检查明细行图书类型编号是否重复
	 * @throws ParseException 
	 */
	 private void checkGrid(List<Map<Object, Object>> grid) throws ParseException{
		//检查明细行人员编号是否存在重复
			for(int i=0;i<grid.size();i++){
				for(int j=i+1;j<grid.size();j++){
				if(grid.get(i).get("bsd_code").toString().equals(grid.get(j).get("bsd_code").toString())){
					BaseUtil.showError("明细行图书类型编号重复，行号："+grid.get(i).get("bsd_detno").toString()+"&nbsp&nbsp"+grid.get(j).get("bsd_detno").toString());
				}
				}
			}
	 }
}
