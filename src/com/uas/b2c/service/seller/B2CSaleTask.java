package com.uas.b2c.service.seller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.b2c.service.buyer.PurchaseOrderService;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Component("b2cSaleTask")
@EnableAsync
@EnableScheduling
public class B2CSaleTask {

	@Autowired
	private EnterpriseService enterpriseService;

	private static List<Master> masters = null;

	@Autowired
	private SaleOrderService saleOrderService;
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	@Autowired
	private B2CUtil b2cUtil;
	public void execute() {
		if (masters == null) {
			masters = Collections.synchronizedList(enterpriseService.getMasters());
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (b2cUtil.isB2CMAll(master)) {   
				String masterName = master.getMa_name();
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				SpObserver.putSp(master.getMa_name());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				saleOrderService.getAllUnReceived(masterName);
				
				purchaseOrderService.getAllUnReceived(masterName);
			}
		}
		SpObserver.putSp(sob);
	}
}
