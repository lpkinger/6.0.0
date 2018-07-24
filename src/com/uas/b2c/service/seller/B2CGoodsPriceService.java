package com.uas.b2c.service.seller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.GoodsPriceUas;
import com.uas.api.b2c_erp.seller.service.GoodsPriceService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Service
public class B2CGoodsPriceService {

	@Resource(name = "api.GoodsPriceService")
	private GoodsPriceService goodsPriceService;
	

	/**
	 * 上架时添加或修改商品价格信息，如果存在，则修改价格信息
	 * 
	 * @param priceUas 包含商品币别，库存类型，税率 ，器件UUID， 未税单价信息
	 * @return
	 */
	public GoodsPriceUas saveOrUpdatePrice(GoodsPriceUas priceUas){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsPriceService.saveOrUpdatePrice(priceUas);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * 批量维护价格信息
	 * 
	 * @param priceUasList json，包含商品币别，库存类型，税率 ，器件UUID
	 * @param range 价格调整的幅度
	 * @param price 要修改的价格
	 * @return
	 */
	public List<GoodsPriceUas> maintain(List<GoodsPriceUas> priceUasList, Float range, Double price){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsPriceService.maintain(priceUasList,null,null);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
}
