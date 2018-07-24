package com.uas.erp.service.b2c.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mockrunner.util.common.StringUtil;
import com.uas.api.b2c_erp.baisc.model.BrandInfoUas;
import com.uas.api.b2c_erp.baisc.model.BrandSubmitUas;
import com.uas.api.b2c_erp.baisc.model.BrandSubmitUasInfo;
import com.uas.api.domain.IPage;
import com.uas.b2c.service.common.FileUploadB2CService;
import com.uas.b2c.service.seller.B2CBrandInApplyService;
import com.uas.b2c.service.seller.B2CBrandService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2c.BrandInApplyService;

@Service
public class BrandInApplyServiceImpl implements BrandInApplyService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private B2CBrandInApplyService B2CBrandInApplyService;
	@Autowired
	private FileUploadB2CService fileUploadB2CService;
	@Autowired
	private B2CBrandService b2CBrandService;
	
	@Override
	public void saveBrandInApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BrandInApply", "br_code='" + store.get("br_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		List<BrandSubmitUas> brandInApply = new ArrayList<BrandSubmitUas>();
		BrandSubmitUas brandSub = new BrandSubmitUas();
		Short version;
		if(StringUtil.isEmptyOrNull(store.get("br_version").toString())){
			 version=Short.parseShort("1");
		}else{
			 version=Short.parseShort(store.get("br_version").toString());
		}
		brandSub.setApplication(store.get("br_application").toString());
		brandSub.setVersion(version);
		brandSub.setStatus(0);
		brandSub.setUuid(store.get("br_uuid").toString());
		brandSub.setAchievement(store.get("br_achievement").toString());
		brandSub.setBrief(store.get("br_brief").toString());
		brandSub.setArea(store.get("br_area").toString());
		brandSub.setNameCn(store.get("br_name").toString());
		brandSub.setNameEn(store.get("br_engname").toString());
		brandSub.setVendor(store.get("br_vendor").toString());
		brandSub.setSeries(store.get("br_series").toString());
		brandSub.setUrl(store.get("br_url").toString());
		//获取IMID
		SqlRowList rs = baseDao.queryForRowSet("select em_imid,em_uu,em_enid,em_name from employee where em_code='"+SystemSession.getUser().getEm_code()+"'");
		if(rs.next()){
			 if( StringUtil.isEmptyOrNull(rs.getString("em_uu"))){
        		 BaseUtil.showError("该账号未开通B2B平台，不允许录入");
        	 }else{
        		brandSub.setImid(rs.getLong("em_imid"));
     			brandSub.setModifyuu(rs.getLong("em_uu"));
     			brandSub.setModifyenuu(rs.getLong("em_enid"));
        	 }
		}
		brandSub.setLogoUrl(store.get("br_logourl").toString());
		brandInApply.add(brandSub);
		List<BrandSubmitUas> returnBrand = B2CBrandInApplyService.save(brandInApply);
		if(!CollectionUtil.isEmpty(returnBrand))
		{
			store.put("br_b2cid", returnBrand.get(0).getId());
			store.put("br_indate",DateUtil.parseDateToString(new Date(), null));
			store.put("br_recorder",SystemSession.getUser().getEm_name());
			store.put("br_date", DateUtil.parseDateToString(new Date(), null));
			store.put("br_statuscode", "ENTERING");
			store.put("br_version", version);
			store.put("br_uuid", store.get("br_uuid"));
			//保存
			
			String formSql = SqlUtil.getInsertSqlByFormStore(store, "BrandInApply", new String[] {}, new Object[] {});
			baseDao.execute(formSql);
			// 记录操作
			baseDao.logger.save(caller, "br_id", store.get("br_id"));
			// 执行保存后的其它逻辑
			handlerService.afterSave(caller, new Object[] { store });
		}
	}

	@Override
	public void updateBrandInApplyById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		BrandSubmitUas brandSub = new BrandSubmitUas();
		Object[] status = baseDao.getFieldsDataByCondition("BrandInApply", new String []{"br_statuscode","br_b2cid"}, "br_id=" + store.get("br_id"));
		StateAssert.updateOnlyEntering(status[0]);
		
		SqlRowList sql=baseDao.queryForRowSet("select br_uuid,br_version from brandinapply where br_id ="+store.get("br_id"));
		
		if(sql.next()){
			brandSub.setUuid(sql.getString("br_uuid"));
			Short version=Short.parseShort(sql.getString("br_version"));
			brandSub.setVersion(version);
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		List<BrandSubmitUas> brandInApply = new ArrayList<BrandSubmitUas>();
	
		brandSub.setApplication(store.get("br_application").toString());
		brandSub.setAchievement(store.get("br_achievement").toString());
		brandSub.setBrief(store.get("br_brief").toString());
		brandSub.setArea(store.get("br_area").toString());
		brandSub.setNameCn(store.get("br_name").toString());
		brandSub.setNameEn(store.get("br_engname").toString());
		brandSub.setVendor(store.get("br_vendor").toString());
		brandSub.setSeries(store.get("br_series").toString());
		brandSub.setUrl(store.get("br_url").toString());
		if( status[1] != null){
		   brandSub.setId(Long.valueOf(status[1].toString()));
		}
		brandSub.setLogoUrl(store.get("br_logourl").toString());
		//获取IMID
		SqlRowList rs = baseDao.queryForRowSet("select em_imid,em_uu,em_enid from employee where em_code='"+SystemSession.getUser().getEm_code()+"'");
		if(rs.next()){
			if( StringUtil.isEmptyOrNull(rs.getString("em_uu"))){
       		 	BaseUtil.showError("该账号未开通B2B平台，不允许录入");
			}else{
				brandSub.setImid(rs.getLong("em_imid"));
				brandSub.setModifyuu(rs.getLong("em_uu"));
				brandSub.setModifyenuu(rs.getLong("em_enid"));
			}
		}
		brandInApply.add(brandSub);
		List<BrandSubmitUas> returnBrand = B2CBrandInApplyService.save(brandInApply);
		
		if(!CollectionUtil.isEmpty(returnBrand)){
			store.put("br_b2cid", returnBrand.get(0).getId());
			store.remove("br_status");
			store.remove("br_statuscode");
			store.remove("br_recorder");
			store.remove("br_indate");
			store.remove("br_date");
			// 修改
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BrandInApply", "br_id");
			baseDao.execute(formSql);
			// 记录操作
			baseDao.logger.update(caller, "br_id", store.get("br_id"));
			handlerService.afterSave(caller, new Object[] { store });
		}
	}

	@Override
	public void deleteBrandInApply(int br_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("BrandInApply", "br_statuscode", "br_id=" + br_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { br_id });
		// 删除
		baseDao.deleteById("BrandInApply", "br_id", br_id);
		// 记录操作
		baseDao.logger.delete(caller, "br_id", br_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { br_id });
	}

	@Override
	public void submitBrandInApply(int br_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BrandInApply", "br_statuscode", "br_id=" + br_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { br_id });
		// 执行提交操作
		baseDao.submit("BrandInApply", "br_id=" + br_id, "br_status", "br_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "br_id", br_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { br_id });
	}

	@Override
	public void resSubmitBrandInApply(int br_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BrandInApply", "br_statuscode", "br_id=" + br_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { br_id });
		// 执行反提交操作
		baseDao.resOperate("BrandInApply", "br_id=" + br_id, "br_status", "br_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "br_id", br_id);
		handlerService.afterResSubmit(caller, new Object[] { br_id });
	}

	@Override
	public void auditBrandInApply(int br_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BrandInApply", "br_statuscode", "br_id=" + br_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		baseDao.updateByCondition("BrandInApply", "br_b2cstatus='商城审核中'", "br_id="+br_id);
		handlerService.beforeAudit(caller, new Object[] { br_id });
		baseDao.audit("BrandInApply", "br_id=" + br_id, "br_status", "br_statuscode", "br_auditdate", "br_auditman");
		baseDao.logger.audit(caller, "br_id", "br_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { br_id });
		sendToB2CPlatform(br_id);
	}
	
	@Override
	public void resAuditBrandInApply(int br_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("BrandInApply", new String[] { "br_statuscode", "br_sendstatus" }, "br_id="
				+ br_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		if ("已上传".equals(status[1])) {
			BaseUtil.showError("不能反审核已上传的单据！");
		}
		// 执行反审核操作
		baseDao.resAudit("BrandInApply", "br_id=" + br_id, "br_status", "br_statuscode", "br_auditdate", "br_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "br_id", br_id);
	}

	// 审核之后将传回商城
	private void sendToB2CPlatform(int br_id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from BrandInApply where br_id=? and nvl(br_sendstatus,' ')<>'已上传'", br_id);
		if(rs.next()){
		   BrandSubmitUasInfo info = B2CBrandInApplyService.submit(rs.getLong("br_b2cid"));
		   if(info.getStatus() == 311){
			   baseDao.execute("update BrandInApply set br_sendstatus='已上传' where br_id=" + br_id);
		   }
		}else{
			baseDao.execute("update BrandInApply set br_sendstatus='已上传' where br_id=" + br_id);
		}
	}

	@Override
	public String saveBrandLogo(CommonsMultipartFile file) {
		 return fileUploadB2CService.saveImage(file.getOriginalFilename(),file.getBytes());
	}

	@Override
	public Map<String, Object> getBrandData(int id, String caller) {
		Map<String,Object> map = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select br_b2cstatus,br_b2cauditopinion,br_recorder,br_b2cid,br_version,br_uuid,br_id,br_statuscode,br_code,br_status,br_auditman,br_auditdate,br_indate,br_url from BrandInApply where br_id=?",id);
		if(rs.next()){
			BrandSubmitUas  brandSubmitUas = B2CBrandInApplyService.findOne(rs.getLong("br_b2cid"));
			if(brandSubmitUas != null){
				map.put("br_version", brandSubmitUas.getVersion());
				map.put("br_uuid",brandSubmitUas.getUuid());
				map.put("br_application", brandSubmitUas.getApplication());
				map.put("br_brief", brandSubmitUas.getBrief());
				map.put("br_area", brandSubmitUas.getArea());
				map.put("br_name", brandSubmitUas.getNameCn());
				map.put("br_engname", brandSubmitUas.getNameEn());
				map.put("br_series", brandSubmitUas.getSeries());
				map.put("br_vendor", brandSubmitUas.getVendor());
				map.put("br_logourl", brandSubmitUas.getLogoUrl());
				map.put("br_achievement", brandSubmitUas.getAchievement());
				map.put("br_url", brandSubmitUas.getUrl());
			}
			map.put("br_b2cstatus",rs.getString("br_b2cstatus"));
			map.put("br_b2cauditopinion", rs.getString("br_b2cauditopinion"));
			map.put("br_recorder",rs.getString("br_recorder"));
			map.put("br_uuid", rs.getString("br_uuid"));
			map.put("br_version", rs.getString("br_version"));
			map.put("br_code", rs.getString("br_code"));
			map.put("br_b2cid", rs.getString("br_b2cid"));
			map.put("br_id", rs.getString("br_id"));
			map.put("br_statuscode", rs.getString("br_statuscode"));
			map.put("br_status", rs.getString("br_status"));
			map.put("br_auditman", rs.getString("br_auditman"));
			map.put("br_auditdate", rs.getString("br_auditdate"));
			map.put("br_indate", rs.getString("br_indate"));
		}
		return map;
	}

	@Override
	public IPage<BrandInfoUas> findBrandByPage(String caller, int page, int pageSize) {
		IPage<BrandInfoUas> brandInfoUas = b2CBrandService.getPage(page, pageSize);
		return brandInfoUas;
	}

	@Override
	public List<BrandInfoUas> findBrandAll(String caller) {
		return b2CBrandService.getAll();
	}

	@Override
	public Map<String, Object> getBrandDataByUUID(String UUID, String caller) {
		Map<String,Object> map = new HashMap<String, Object>();
		BrandSubmitUas  brandSubmitUas = B2CBrandInApplyService.findOneByUUID(UUID);
		Long br_id=brandSubmitUas.getId();
		SqlRowList rs = baseDao.queryForRowSet("select br_b2cid,br_id,br_statuscode,br_code,br_status,br_auditman,br_auditdate,br_indate from BrandInApply where br_id=?",br_id);
		if(brandSubmitUas != null){
			map.put("br_uuid", brandSubmitUas.getUuid());
			map.put("br_version",brandSubmitUas.getVersion());
			map.put("br_application", brandSubmitUas.getApplication());
			map.put("br_brief", brandSubmitUas.getBrief());
			map.put("br_area", brandSubmitUas.getArea());
			map.put("br_name", brandSubmitUas.getNameCn());
			map.put("br_engname", brandSubmitUas.getNameEn());
			map.put("br_series", brandSubmitUas.getSeries());
			map.put("br_vendor", brandSubmitUas.getVendor());
			map.put("br_logourl", brandSubmitUas.getLogoUrl());
			map.put("br_achievement", brandSubmitUas.getAchievement());
			map.put("br_url", brandSubmitUas.getUrl());
		}
		if(rs.next()){
			map.put("br_code", rs.getString("br_code"));
			map.put("br_b2cid", rs.getString("br_b2cid"));
			map.put("br_id", rs.getString("br_id"));
			map.put("br_statuscode", rs.getString("br_statuscode"));
			map.put("br_status", rs.getString("br_status"));
			map.put("br_auditman", rs.getString("br_auditman"));
			map.put("br_auditdate", rs.getString("br_auditdate"));
			map.put("br_indate", rs.getString("br_indate"));
		}
		return map;	
	}

	@Override
	public Map<String, Object> getUpdateBrand(String nameCn,String nameEn,String caller) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		BrandSubmitUas  brandSubmitUas = B2CBrandInApplyService.findByName(nameCn, nameEn);
		map.put("br_uuid", brandSubmitUas.getUuid());
		map.put("br_version",brandSubmitUas.getVersion());
		map.put("br_application", brandSubmitUas.getApplication());
		map.put("br_brief", brandSubmitUas.getBrief());
		map.put("br_area", brandSubmitUas.getArea());
		map.put("br_name", brandSubmitUas.getNameCn());
		map.put("br_engname", brandSubmitUas.getNameEn());
		map.put("br_series", brandSubmitUas.getSeries());
		map.put("br_vendor", brandSubmitUas.getVendor());
		map.put("br_logourl", brandSubmitUas.getLogoUrl());
		map.put("br_achievement", brandSubmitUas.getAchievement());
		map.put("br_url", brandSubmitUas.getUrl());
		return map;
	}
	
	@Override
	public Map<String, Object> CheckBrandName(String nameCn, String nameEn,String caller) {
		Map<String,Object> map = new HashMap<String, Object>();
		List<BrandInfoUas>  brandInfoUas = B2CBrandInApplyService.findByNameCn(nameCn, nameEn);
		if(brandInfoUas.size()>0){
			map.put("nameCn", brandInfoUas.get(0).getNameCn());
			map.put("nameEn", brandInfoUas.get(0).getNameEn());
		}
		return map;
	}
}
