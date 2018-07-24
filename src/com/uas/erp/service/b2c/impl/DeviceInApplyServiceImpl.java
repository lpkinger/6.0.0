package com.uas.erp.service.b2c.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mockrunner.util.common.StringUtil;
import com.uas.api.b2c_erp.baisc.model.BrandInfoUas;
import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.b2c_erp.baisc.model.ComponentSubmitUas;
import com.uas.api.b2c_erp.baisc.model.KindPropertyUas;
import com.uas.api.b2c_erp.baisc.model.PropertySubmitUas;
import com.uas.api.b2c_erp.baisc.model.PropertyUas;
import com.uas.api.b2c_erp.baisc.service.ComponentService;
import com.uas.api.b2c_erp.baisc.service.KindService;
import com.uas.b2c.service.common.B2CKindService;
import com.uas.b2c.service.common.FileUploadB2CService;
import com.uas.b2c.service.seller.B2CBrandService;
import com.uas.b2c.service.seller.B2CDeviceInApplyService;
import com.uas.b2c.service.seller.B2CBrandInApplyService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2c.DeviceInApplyService;

@Service
public class DeviceInApplyServiceImpl implements DeviceInApplyService {

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private HandlerService handlerService;

    @Autowired
    private B2CBrandService  B2CBrandService;
    
    @Autowired
    private B2CKindService  B2CKindService;
    
    @Autowired 
    private B2CDeviceInApplyService B2CDeviceInApplyService;
    
    @Autowired 
    private B2CBrandInApplyService B2CBrandInApplyService;
    
    @Autowired
    private KindService KindService;
    
    @Autowired
    private ComponentService ComponentService;
    
    @Autowired
	private FileUploadB2CService fileUploadB2CService;
    @Override
    public void saveDeviceInApply(String formStore, String param, String caller) {
        
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
        
        //验证品牌和原厂型号
        Long de_brandid=null;
        Long de_kindid=null;
        if(!store.get("de_brandid").equals(null)&&!store.get("de_brandid").equals("")){
        	  de_brandid=Long.parseLong(store.get("de_brandid").toString());
        }
        if(!store.get("de_kindid").equals(null)&&!store.get("de_kindid").equals("")){        	
        	  de_kindid=Long.parseLong(store.get("de_kindid").toString());
        }
        float weight=Float.parseFloat(store.get("de_weight").toString());
        int pnum=Integer.parseInt(store.get("PropertiesNum").toString());
        List<ComponentSubmitUas> componentInApply = new ArrayList<ComponentSubmitUas>();
        
        ComponentSubmitUas ComponentSubmitUas=new ComponentSubmitUas();
        Set<PropertySubmitUas> properties=new HashSet<PropertySubmitUas>();
        Short de_version=null;
        //将参数插入Map
        for(int i=0;i<pnum;i++){
            Long id=Long.parseLong(store.get("Properties"+i).toString());
            String stringValue=null;
            Short detno=null;
            String unit=null;
            Double numberic=null;
            Double min=null;
            Double max=null;
            if(!store.get("num"+i).equals("")&&!store.get("num"+i).equals(null)){
                numberic=Double.parseDouble(store.get("num"+i).toString());
            }
            if(!store.get("min"+i).equals("")&&!store.get("min"+i).equals(null)){
               min=Double.parseDouble(store.get("min"+i).toString());
            }
            if(!store.get("max"+i).equals("")&&!store.get("max"+i).equals(null)){
               max=Double.parseDouble(store.get("max"+i).toString());
            }
            if(store.get("unit"+i).equals("")&&store.get("unit"+i).equals(null)){
               unit=store.get("unit"+i).toString();
            }
            if(!store.get("detno"+i).equals("")&&!store.get("detno"+i).equals(null)){
               detno=Short.parseShort(store.get("detno"+i).toString());
            }
            if(!store.get(id.toString()).equals("")){
               stringValue=store.get(id.toString()).toString();
            }
            PropertySubmitUas  propertySubmitUas=new PropertySubmitUas();
            propertySubmitUas.setPropertyId(id);
            propertySubmitUas.setStringValue(stringValue);
            propertySubmitUas.setDetno(detno);
            propertySubmitUas.setNumberic(numberic);
            propertySubmitUas.setUnit(unit);
            propertySubmitUas.setMax(min);
            propertySubmitUas.setMin(max);
            properties.add(propertySubmitUas);
        }
        if(!store.get("de_attach").equals("")&&!store.get("de_attach").equals(null)){
        	 ComponentSubmitUas.setAttach(store.get("de_attach").toString());
        }else{
        	 ComponentSubmitUas.setAttach(null);
        }
        if(!store.get("de_version").equals("")&&!store.get("de_version").equals(null)){
        	de_version=Short.parseShort(store.get("de_version").toString());
        }
        ComponentSubmitUas.setBrandName(store.get("de_brand").toString());
        ComponentSubmitUas.setKindNameCn(store.get("de_kind").toString());
        ComponentSubmitUas.setUnit(store.get("de_unit").toString());
        ComponentSubmitUas.setCode(store.get("de_oldspec").toString());
        ComponentSubmitUas.setPackaging(store.get("de_packingspec").toString());
        ComponentSubmitUas.setKindid(de_kindid);
        ComponentSubmitUas.setWeight(weight);
        ComponentSubmitUas.setBrandid(de_brandid);
        ComponentSubmitUas.setUuid(store.get("de_uuid").toString());
        ComponentSubmitUas.setVersion(de_version);
        ComponentSubmitUas.setImg(store.get("de_image").toString());
        ComponentSubmitUas.setDescription(store.get("de_description").toString());
        ComponentSubmitUas.setAuditOpinion("a");
        ComponentSubmitUas.setProperties(properties);
       
        SqlRowList rs = baseDao.queryForRowSet("select em_imid,em_uu,em_enid from employee where em_code='"+SystemSession.getUser().getEm_code()+"'");
        if(rs.next()){
             if( StringUtil.isEmptyOrNull(rs.getString("em_uu"))){
                 BaseUtil.showError("该账号未开通B2B平台，不允许录入");
             }else{
            	 ComponentSubmitUas.setImid(rs.getLong("em_imid"));
            	 ComponentSubmitUas.setModifyuu(rs.getLong("em_uu"));
            	 if(!SystemSession.getUser().getCurrentMaster().getMa_uu().equals(null)){
            		 ComponentSubmitUas.setModifyenuu(SystemSession.getUser().getCurrentMaster().getMa_uu());
            	 }else{
            		 ComponentSubmitUas.setModifyenuu(null);
            	 }
             }
        }
        componentInApply.add(ComponentSubmitUas);
        List<ComponentSubmitUas> returnDevice = B2CDeviceInApplyService.save(componentInApply);
        if(!CollectionUtil.isEmpty(returnDevice)){
        	 store.put("de_version",(short)0);
             store.put("de_b2cid", returnDevice.get(0).getId());
             store.put("de_indate",DateUtil.parseDateToString(new Date(), null));
             store.put("de_recorder",SystemSession.getUser().getEm_name());
             store.put("de_date", DateUtil.parseDateToString(new Date(), null));
             store.put("de_status","在录入");
             store.put("de_statuscode", "ENTERING");
             for(int i=0;i<pnum;i++){
             	store.remove(store.get("Properties"+i).toString());
             	store.remove("Properties"+i);
             	store.remove("detno"+i);
             	store.remove("unit"+i);
             	store.remove("num"+i);
             	store.remove("min"+i);
             	store.remove("max"+i);
             }
             store.remove("PropertiesNum");
             //保存
             String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceInApply", new String[] {}, new Object[] {});
             baseDao.execute(formSql);
            
             //前天获取序列不行，暂时这样
             baseDao.logger.save(caller, "de_id", store.get("de_id"));
             // 执行保存后的其它逻辑
        }
    }

    @Override
    public void updateDeviceInApplyById(String formStore, String param, String caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
        //器件的List
        List<ComponentSubmitUas> componentInApply = new ArrayList<ComponentSubmitUas>();
        //新建一个器件实体
        ComponentSubmitUas componentSubmitUas=new ComponentSubmitUas();
        //器件参数的HashMap
        Set<PropertySubmitUas> properties=new HashSet<PropertySubmitUas>();
        //器件参数的个数
        int pnum=Integer.parseInt(store.get("PropertiesNum").toString());
        Long de_brandid=null;
        if(!store.get("de_brandid").equals(null)&&!store.get("de_brandid").equals("")){
        	  de_brandid=Long.parseLong(store.get("de_brandid").toString());
        }
        Long de_kindid=Long.parseLong(store.get("de_kindid").toString());
        Short de_version=null;
        float weight=Float.parseFloat(store.get("de_weight").toString());
        for(int i=0;i<pnum;i++){
            Long id=Long.parseLong(store.get("Properties"+i).toString());
            String stringValue=null;
            Short detno=null;
            String unit=null;
            Double numberic=null;
            Double min=null;
            Double max=null;
            if(!store.get("num"+i).equals("")&&!store.get("num"+i).equals(null)){
                 numberic=Double.parseDouble(store.get("num"+i).toString());
            }
            if(!store.get("min"+i).equals("")&&!store.get("min"+i).equals(null)){
                min=Double.parseDouble(store.get("min"+i).toString());
            }
            if(!store.get("max"+i).equals("")&&!store.get("max"+i).equals(null)){
                max=Double.parseDouble(store.get("max"+i).toString());
            }
            if(store.get("unit"+i).equals("")&&store.get("unit"+i).equals(null)){
                unit=store.get("unit"+i).toString();
            }
            if(!store.get("detno"+i).equals("")&&!store.get("detno"+i).equals(null)){
                detno=Short.parseShort(store.get("detno"+i).toString());
            }
            if(!store.get(id.toString()).equals("")){
            	stringValue=store.get(id.toString()).toString();
            }
            PropertySubmitUas  propertySubmitUas=new PropertySubmitUas();
            propertySubmitUas.setPropertyId(id);
            propertySubmitUas.setStringValue(stringValue);
            propertySubmitUas.setDetno(detno);
            propertySubmitUas.setNumberic(numberic);
            propertySubmitUas.setUnit(unit);
            propertySubmitUas.setMax(min);
            propertySubmitUas.setMin(max);
            properties.add(propertySubmitUas);
        }  
        SqlRowList rs = baseDao.queryForRowSet("select em_imid,em_uu,em_enid from employee where em_code='"+SystemSession.getUser().getEm_code()+"'");
        if(rs.next()){
             if( StringUtil.isEmptyOrNull(rs.getString("em_uu"))){
                 BaseUtil.showError("该账号未开通B2B平台，不允许录入");
             }else{
            	 componentSubmitUas.setImid(rs.getLong("em_imid"));
            	 componentSubmitUas.setModifyuu(rs.getLong("em_uu"));
            	 if(!SystemSession.getUser().getCurrentMaster().getMa_uu().equals(null)){
            	 	componentSubmitUas.setModifyenuu(SystemSession.getUser().getCurrentMaster().getMa_uu());
            	 }else{
            	 	componentSubmitUas.setModifyenuu(null);
            	 }
             }
        }
        SqlRowList rs1 = baseDao.queryForRowSet("select de_b2cid from deviceinapply where de_id="+store.get("de_id"));
        if(rs1.next()){
            componentSubmitUas.setId(rs1.getLong("de_b2cid"));
        }
        if(!store.get("de_attach").equals("")&&!store.get("de_attach").equals(null)){
        	  componentSubmitUas.setAttach(store.get("de_attach").toString());
        }else{
    	   componentSubmitUas.setAttach(null);
        }
        if(!store.get("de_version").equals("")&&!store.get("de_version").equals(null)){
    	   de_version=Short.parseShort(store.get("de_version").toString());
        }
       	componentSubmitUas.setBrandName(store.get("de_brand").toString());
       	componentSubmitUas.setKindNameCn(store.get("de_kind").toString());
        componentSubmitUas.setVersion(de_version);
        componentSubmitUas.setUnit(store.get("de_unit").toString());
        componentSubmitUas.setUuid(store.get("de_branduuid").toString());
        componentSubmitUas.setCode(store.get("de_oldspec").toString());
        componentSubmitUas.setPackaging(store.get("de_packingspec").toString());
        componentSubmitUas.setKindid(de_kindid);
        componentSubmitUas.setWeight(weight);
        componentSubmitUas.setBrandid(de_brandid);
        componentSubmitUas.setUuid(store.get("de_uuid").toString());
        componentSubmitUas.setVersion(de_version);
        componentSubmitUas.setImg(store.get("de_image").toString());
        componentSubmitUas.setDescription(store.get("de_description").toString());
        componentSubmitUas.setAuditOpinion("a");
        componentSubmitUas.setProperties(properties);
        componentInApply.add(componentSubmitUas);
        for(int i=0;i<pnum;i++){
        	store.remove(store.get("Properties"+i).toString());
        	store.remove("Properties"+i);
        	store.remove("detno"+i);
        	store.remove("unit"+i);
        	store.remove("num"+i);
        	store.remove("min"+i);
        	store.remove("max"+i);
        }
        
        store.remove("PropertiesNum");
        List<ComponentSubmitUas> returnDevice = B2CDeviceInApplyService.save(componentInApply);
        if(!CollectionUtil.isEmpty(returnDevice)){
        	   // 只能修改[在录入]的采购单资料!
            Object status = baseDao.getFieldDataByCondition("DeviceInApply", "de_statuscode", "de_id=" + store.get("de_id"));
            StateAssert.updateOnlyEntering(status);
            // 修改
            baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "DeviceInApply", "de_id"));

            // 记录操作
            baseDao.logger.update(caller, "de_id", store.get("de_id"));
            // 更新上次采购价格、供应商
        }
    }
    
    @Override
    public void deleteDeviceInApply(int de_id, String caller) {
        Object status = baseDao.getFieldDataByCondition("DeviceInApply", "de_statuscode", "de_id=" + de_id);
        StateAssert.delOnlyEntering(status);
        // 执行删除前的其它逻辑
        handlerService.beforeDel(caller, new Object[] { de_id });
        // 删除
        baseDao.deleteById("DeviceInApply", "de_id", de_id);
        // 删除明细
        //baseDao.deleteById("DEVICEKINDPROPERTY", "dep_deid", de_id);
        // 记录操作
        baseDao.logger.delete(caller, "de_id", de_id);
        // 执行删除后的其它逻辑
        handlerService.afterDel(caller, new Object[] { de_id });
    }

    @Override
    public void submitDeviceInApply(int de_id, String caller) {
        // 只能对状态为[在录入]的订单进行提交操作!
        Object status = baseDao.getFieldDataByCondition("DeviceInApply", "de_statuscode", "de_id=" + de_id);
        StateAssert.submitOnlyEntering(status);
        // 执行提交前的其它逻辑
      
        handlerService.beforeSubmit(caller, new Object[] { de_id });
        // 执行提交操作
        baseDao.submit("DeviceInApply", "de_id=" + de_id, "de_status", "de_statuscode");
        // 记录操作
        baseDao.logger.submit(caller, "de_id", de_id);
        handlerService.afterSubmit(caller, new Object[] { de_id });
    }
    
    @Override
    public void resSubmitDeviceInApply(int de_id, String caller) {
        // 只能对状态为[已提交]的订单进行反提交操作!
        Object status = baseDao.getFieldDataByCondition("DeviceInApply", "de_statuscode", "de_id=" + de_id);
        StateAssert.resSubmitOnlyCommited(status);
        handlerService.beforeResSubmit(caller, new Object[] { de_id });
        // 执行反提交操作
        baseDao.resOperate("DeviceInApply", "de_id=" + de_id, "de_status", "de_statuscode");
        // 记录操作
        baseDao.logger.resSubmit(caller, "de_id", de_id);
        handlerService.afterResSubmit(caller, new Object[] { de_id });
    }

    @Override
    public void auditDeviceInApply(int de_id, String caller) {
        // 只能对状态为[已提交]的订单进行审核操作!
        Object status = baseDao.getFieldDataByCondition("DeviceInApply", "de_statuscode", "de_id=" + de_id);
        StateAssert.auditOnlyCommited(status);
        // 执行审核前的其它逻辑
        baseDao.updateByCondition("DeviceInApply", "de_b2cstatus='商城审核中'", " de_id="+de_id);
        handlerService.beforeAudit(caller, new Object[] { de_id });
        baseDao.audit("DeviceInApply", "de_id=" + de_id, "de_status", "de_statuscode", "de_auditdate", "de_auditman");
        baseDao.logger.audit(caller, "de_id", "de_id");
       
        handlerService.afterAudit(caller, new Object[] { de_id });
        sendToB2CPlatform(de_id);
    }

    @Override
    public void resAuditDeviceInApply(int de_id, String caller) {
        Object[] status = baseDao.getFieldsDataByCondition("DeviceInApply", new String[] { "de_statuscode", "de_sendstatus" }, "de_id="
                + de_id);
        StateAssert.resAuditOnlyAudit(status[0]);
        if ("已上传".equals(status[1])) {
            BaseUtil.showError("不能反审核已上传的单据！");
        }
        // 执行反审核操作
        baseDao.resAudit("DeviceInApply", "de_id=" + de_id, "de_status", "de_statuscode", "de_auditdate", "de_auditman");
        // 记录操作
        baseDao.logger.resAudit(caller, "de_id", de_id);
    }

    // 审核之后将传回商城
    private void sendToB2CPlatform(int de_id) {
        SqlRowList rs = baseDao.queryForRowSet("select * from DeviceInApply where de_id=? and nvl(de_sendstatus,' ')='待上传'", de_id);
        if (rs.next()) {
            B2CDeviceInApplyService.submit(rs.getLong("de_b2cid"));
            baseDao.execute("update DeviceInApply set de_sendstatus='已上传' where de_id=" + de_id);
        }
    }
    
    @Override
    public Map<String, Object> getDeviceData(int id, String caller) {
        Map<String,Object> map = new HashMap<String, Object>();
        SqlRowList rs = baseDao.queryForRowSet("select de_prodcode,de_id,de_b2cstatus,de_b2cauditopinion,de_weight,de_brand,de_kind,de_b2cid,de_statuscode,de_code,de_status,de_auditman,de_recorder,de_auditdate,de_indate from DeviceInApply where de_id=?",id);
        if(rs.next()){
            ComponentSubmitUas componetSubmitUas = B2CDeviceInApplyService.findOne(rs.getLong("de_b2cid")) ;
            if(componetSubmitUas != null){
            	map.put("de_attach", componetSubmitUas.getAttach());
            	map.put("de_unit",componetSubmitUas.getUnit());
                map.put("de_oldspec", componetSubmitUas.getCode());
                map.put("de_version", componetSubmitUas.getVersion());
                map.put("de_packingspec", componetSubmitUas.getPackaging());
                map.put("de_uuid", componetSubmitUas.getUuid());
                map.put("de_weight",componetSubmitUas.getWeight());
                map.put("de_brandid",componetSubmitUas.getBrandid());
                map.put("de_kindid",componetSubmitUas.getKindid());
                map.put("de_description",componetSubmitUas.getDescription());
                map.put("de_image", componetSubmitUas.getImg());
                map.put("properties",componetSubmitUas.getProperties());
            }
            map.put("de_prodcode",rs.getString("de_prodcode"));
            map.put("de_recorder",rs.getString("de_recorder"));
            map.put("de_b2cstatus",rs.getString("de_b2cstatus"));
            map.put("de_b2cauditopinion", rs.getString("de_b2cauditopinion"));
            map.put("de_weight",rs.getString("de_weight"));
            map.put("de_brand", rs.getString("de_brand"));
            map.put("de_kind", rs.getString("de_kind"));
            map.put("de_code", rs.getString("de_code"));
            map.put("de_id", rs.getString("de_id"));
            map.put("de_statuscode", rs.getString("de_statuscode"));
            map.put("de_status", rs.getString("de_status"));
            map.put("de_auditman", rs.getString("de_auditman"));
            map.put("de_auditdate", rs.getString("de_auditdate"));
            map.put("de_indate", rs.getString("de_indate"));
        }
        return map;
    }
    
    @Override
    public Map<String, Object> getAllKind(String caller) {
        Map<String,Object> map = new HashMap<String, Object>();
        return map;
    }
    
    @Override
    public Map<String, Object> getAllBrand(String caller) {
        Map<String,Object> map = new HashMap<String, Object>();
        List<BrandInfoUas> Brand = new ArrayList<BrandInfoUas>();
        Brand=B2CBrandService.getAll();
        map.put("data", Brand);
        return map;
    }
    
    @Override
    public Map<String, Object> getSearchData(String searchWord, String caller) {
        Map<String,Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> searchData=new ArrayList<Map<String, Object>>();
        searchData=B2CBrandService.getSearchData(searchWord);
        map.put("data",searchData);
        return map;
    }

    public Map<String, Object> getPropertiesById(Long id) {
         Map<String,Object> map = new HashMap<String, Object>();
         List<KindPropertyUas> Properties=new ArrayList<KindPropertyUas>();
         Properties=KindService.getKindPropertiesById(id);
         map.put("data", Properties);
         return map;
    }
    
	@Override
	public String saveFile(CommonsMultipartFile file) {
		 return fileUploadB2CService.saveFile(file.getOriginalFilename(),file.getBytes());
	}

	@Override
	public Map<String, Object> getOldSpecData(String searchWord, String caller) {
		  Map<String,Object> map = new HashMap<String, Object>();
	        List<Map<String, Object>> searchData=new ArrayList<Map<String, Object>>();
	        searchData=B2CBrandService.getOldSpecData(searchWord);
	        map.put("data",searchData);
	        return map;
	}

	@Override
    public Map<String, Object> findDeviceByUUID(String UUID, String caller) {
          Map<String,Object> map = new HashMap<String, Object>();
            ComponentSubmitUas searchData=B2CDeviceInApplyService.findDeviceByUUID(UUID);
            if(searchData != null){
                map.put("de_brandid", searchData.getBrandid());
                map.put("de_kindid", searchData.getKindid());
                map.put("de_oldspec", searchData.getCode());
                map.put("de_packingspec", searchData.getPackaging());
                map.put("de_unit", searchData.getUnit());
                map.put("de_uuid", searchData.getUuid());
                map.put("de_weight", searchData.getWeight());
                map.put("de_image", searchData.getImg());
                map.put("de_attach", searchData.getAttach());
                map.put("de_description", searchData.getDescription());
                map.put("de_kind", searchData.getKindNameCn());
                map.put("de_brand", searchData.getBrandName());
                map.put("de_version",searchData.getVersion());
                map.put("properties", searchData.getProperties());
            }
            return map;
    }

	@Override
    public Map<String, Object> getKindData(String searchWord, String caller) {
         Map<String,Object> map = new HashMap<String, Object>();
         List<Map<String, Object>> searchData=new ArrayList<Map<String, Object>>();
         searchData=B2CBrandService.getKindData(searchWord);
         map.put("data",searchData);
         return map;
    }

	@Override
	public Map<String, Object> getATypeAssociation(Long kindid,Long id,String searchword,Long shownum, String caller) {
		Map<String,Object> map = new HashMap<String, Object>();
        List<Map<String, String>> searchData=new ArrayList<Map<String, String>>();
        searchData=B2CBrandService.getATypeAssociation(kindid,id,searchword,shownum);
        map.put("data",searchData);
        return map;
	}

	@Override
	public Map<String, Object> checkBrandAndCode(String nameCn, String code) {
		  Map<String,Object> map = new HashMap<String, Object>();
		  List<BrandInfoUas> brandInfoUas = B2CBrandInApplyService.findByNameCn(nameCn,null);
	      if(brandInfoUas.size()>0){
	    	 map.put("brandid",brandInfoUas.get(0).getId());
	         map.put("nameCn",brandInfoUas.get(0).getNameCn())	;
	         map.put("nameCn",brandInfoUas.get(0).getNameCn())	;
	      }
	      List<ComponentInfoUas> componentInfoUas=B2CDeviceInApplyService.checkBrandAndCode(code);
	      if(componentInfoUas.size()>0){
	    	 map.put("code",componentInfoUas.get(0).getCode());
	      }
	      return map;
	}
 
	@Override
	public Map<String, Object> getPackaging(Long kindid) {
		Map<String,Object> map = new HashMap<String, Object>();
	    List<Map<String, String>> packaging=B2CDeviceInApplyService.findPackaging(kindid);
        map.put("packaging",packaging);
		return map;
	}

}
