package com.uas.b2c.service.seller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.baisc.model.BrandInfoUas;
import com.uas.api.b2c_erp.baisc.model.BrandSubmitUasInfo;
import com.uas.api.b2c_erp.baisc.model.PropertyUas;
import com.uas.api.b2c_erp.baisc.service.BrandService;
import com.uas.api.b2c_erp.baisc.service.BrandSubmitUasService;
import com.uas.api.b2c_erp.baisc.service.KindService;
import com.uas.api.b2c_erp.search.service.SearchService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.api.domain.IPage;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;


@Service
public class B2CBrandService {
	@Resource(name = "api.BrandService")
	private BrandService brandService;
	
	@Resource(name = "api.SearchService")
	private SearchService  searchService;
	
	@Resource(name = "api.kindService")
	private KindService  kindService;
	
	@Resource(name = "api.brandSubmitService")
	private BrandSubmitUasService brandSubmitUasService;
	@Autowired
	private BaseDao baseDao;
	/**
	 * 获取所有的品牌
	 * 
	 * @return
	 */
	public List<BrandInfoUas> getAll(){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			return brandService.getAll();
		}
		return null;
	}

	/**
	 * 分页获取品牌信息
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public IPage<BrandInfoUas> getPage(int page, int pageSize){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			return brandService.getPage(page,pageSize);
		}
		return null;
	}
	
	/***
	 * 	获取模糊搜索的数据
	 * @throws UnsupportedEncodingException 
	 * */
	public List<Map<String ,Object>> getSearchData(String data){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try{
				return searchService.getAssociateBrands(data);
			}catch (Exception e){
				e.printStackTrace();
     		    BaseUtil.showError(e.getMessage());
			}	
		}
		return null;
	}
	
	/***
	 * 	获取原厂型号模糊搜索
	 * @throws UnsupportedEncodingException 
	 * */
	public List<Map<String ,Object>> getOldSpecData(String data){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try{
				return searchService.getAssoicateComponents(data);
			}catch (Exception e){
				e.printStackTrace();
     		    BaseUtil.showError(e.getMessage());
			}	
		}
		return null;
	}
	/***
	 * 	获取类目
	 * @throws UnsupportedEncodingException 
	 * */
	public List<Map<String ,Object>> getKindData(String data){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try{
				return searchService.getAssoicateLeafKinds(data);
			}catch (Exception e){
				e.printStackTrace();
     		    BaseUtil.showError(e.getMessage());
			}	
		}
		return null;
	}
	/***
	 * 	获取器件属性
	 * */
	public List<PropertyUas> getPropertiesById(Long id){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try{
				return kindService.getPropertiesById(id);
			}catch (Exception e){
				e.printStackTrace();
     		    BaseUtil.showError(e.getMessage());
			}	
		}
		return null;
	}
	/***
	 * 	AType联想词
	 * */
	public List<Map<String, String>> getATypeAssociation(Long kindid,Long propertyid,String searchword,Long shownum){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try{
				return searchService.getAssoicatePropertyValues(kindid,propertyid, searchword, shownum);
			}catch (Exception e){
				e.printStackTrace();
     		    BaseUtil.showError(e.getMessage());
			}	
		}
		return null;
	}
	
	public void getB2CBrandAuditStatus() {
		try{
			List<BrandSubmitUasInfo> bs =brandSubmitUasService.findAllSubmit();
			for(int i=0;i<bs.size();i++){
			  String id=bs.get(i).getId().toString();
			  int b2cstatuscode=bs.get(i).getStatus();
			  String b2cauditopinion=bs.get(i).getAuditOpinion();
			  String status =null;
			  if(b2cstatuscode==311){
				  status="平台审核中";
			  }else if(b2cstatuscode==103){
				  status="平台审核未通过";
			  }else if(b2cstatuscode==104){
				  status="平台审核通过";
			  }
			  String sql="update BrandInApply set br_b2cstatus='"+status+"',br_b2cauditopinion='"+b2cauditopinion+"' where br_b2cid="+id;
			  baseDao.execute(sql);
			}
		}catch (Exception e){
			e.printStackTrace();
		}	
	}
}
