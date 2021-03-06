package com.uas.b2c.service.seller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.service.buyer.PurchaseOrderService;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Component("b2cTask")
@EnableAsync
@EnableScheduling
public class B2CTask {

	@Autowired
	private EnterpriseService enterpriseService;

	private static List<Master> masters = null;

	@Autowired
	private SaleOrderService saleOrderService;
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;

	@Autowired
	private B2CPurchaseAcceptNotifyService b2CPurchaseAcceptNotifyService;
	
	@Autowired
	private B2CDeviceInApplyService b2cDeviceInApplyService;

	@Autowired
	private B2CBrandInApplyService b2cBrandInApplyService;
	
	public void execute() {
		if (masters == null) {
			masters = Collections.synchronizedList(enterpriseService.getMasters());
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {          
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				SpObserver.putSp(master.getMa_name());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				
				//saleOrderService.getAllUnReceived();
				//purchaseOrderService.getAllUnReceived();
				/*b2CPurchaseAcceptNotifyService.getAllInvoiceNoitify();
				b2cDeviceInApplyService.getB2CDeviceAuditStatus();
				b2cBrandInApplyService.getB2CBrandAuditStatus();*/
			}
		}
		SpObserver.putSp(sob);
	}
}
