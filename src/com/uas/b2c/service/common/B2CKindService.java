package com.uas.b2c.service.common;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.baisc.model.KindUas;
import com.uas.api.b2c_erp.baisc.service.KindService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;
/**
 * 获取b2c平台商品类目信息
 * @author XiaoST  2016-7-8 下午4:17:18
 */
@Service
public class B2CKindService {
	@Resource(name = "api.kindService")
	private KindService kindService;
	
	/**
	 * 商品类目的子类目
	 * @Tip 对应原接口 sendGetRequest("/api/product/kind/" + parentid + "/children")
	 * @param parentId
	 * @return
	 */
	public List<KindUas> getProductKinds(Long parentId){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return kindService.getProductKinds(parentId);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * 根据型号搜索对应所有的当前类目和父级类目（兄弟类目不用）
	 * @Tip 新增接口
	 * @param code
	 * @return
	 */
	public Map<String,List<KindUas>> getParentsByCode(String code){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return kindService.getParentsByCode(code);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * 根据类目搜索对应所有的当前类目和父级类目
	 * @Tip 新增接口
	 * @param code
	 * @return
	 */
	public Map<String,List<KindUas>> getParentsByKindCode(String code){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return kindService.getParentsByKindCode(code);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
	
}
