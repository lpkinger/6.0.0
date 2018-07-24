package com.uas.b2c.service.seller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.Invoice;
import com.uas.api.b2c_erp.seller.service.InvoiceService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * ERP中销售出货单过账之后通知B2C平台发货
 * @author XiaoST
 *
 */
@Service
public class SaleInvoiceService {
	@Resource(name = "api.invoiceService")
	private InvoiceService invoiceService;

	/**
	 * 发货
	 * 
	 */ 
	public void send(Invoice invoice,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			invoiceService.send(invoice);
		}
	}

	/**
	 * 批量发货
	 * 
	 */
	public void sendAll(List<Invoice> invoices) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			invoiceService.sendAll(invoices);
		}
	}

}
