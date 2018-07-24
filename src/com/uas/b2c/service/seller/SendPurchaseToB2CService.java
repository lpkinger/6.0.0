package com.uas.b2c.service.seller;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.buyer.model.B2cOrder;
import com.uas.api.b2c_erp.buyer.model.Purchase;
import com.uas.api.b2c_erp.buyer.service.PurchaseService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Service
public class SendPurchaseToB2CService {

	@Resource(name = "api.purchaseService")
	private PurchaseService purchaseService;

	/**
	 * 上传采购单至商城
	 * 
	 * @param purchase
	 */
	public B2cOrder save(Purchase purchase,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			return purchaseService.save(purchase);
		}
		return null;
	}

}
