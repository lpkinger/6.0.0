package com.uas.b2c.service.common;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.uas.api.b2c_erp.seller.model.ProductDetailERP;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * 将UAS空闲库存信息同步到商城
 * 
 * @author hy
 *
 */

@Component("b2cStoreTask")
@EnableAsync
@EnableScheduling
public class B2CStoreTask {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Autowired
    private B2CStoreService b2cStoreService;
	
	private static List<Master> masters = null;
	
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
				uploadStore();//上传任务
			}
		}
		SpObserver.putSp(sob);
	}

	/**
	 * 初始化信息传到商城
	 */
	public void uploadStore() {/*
				List<ProductDetailERP> prods = getProductDetail();
				if(!CollectionUtil.isEmpty(prods)){			
					try {
						b2cStoreService.uploadStoreB2C(prods);												
						uploadStore();							
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	*/}
	
	/**
	 *	获取初始化物料信息
	 */
	private List<ProductDetailERP> getProductDetail() {
		try {
			List<ProductDetailERP> prods = baseDao.getJdbcTemplate().query(
					"select * from (select PO_PRODCODE,reserve,pr_zxbzs,pr_zxdhl,pr_ltinstock,ppd.PPD_PRICE from (select PO_PRODCODE,PO_ONHAND,PO_ONHAND-nvl(lockqty,0) as reserve"
					+"  from Productonhand left join ( select pd_prodcode,sum(pd_outqty) lockqty from prodiodetail where pd_piclass='出货单' and pd_status<99"
					+"group by pd_prodcode) a on po_prodcode=a.pd_prodcode)b left join product on b.PO_PRODCODE = product.pr_code left join PurchasePriceDetail"
					+" ppd on b.PO_PRODCODE = ppd.PPD_PRODCODE where ppd.ppd_status='有效') where rownum <= 500",
					new BeanPropertyRowMapper<ProductDetailERP>(ProductDetailERP.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
