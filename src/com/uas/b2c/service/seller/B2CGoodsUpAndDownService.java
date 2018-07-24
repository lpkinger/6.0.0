package com.uas.b2c.service.seller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.GoodsFUas;
import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;
import com.uas.api.b2c_erp.seller.service.GoodsUpAndDownService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Service
public class B2CGoodsUpAndDownService {
	@Resource(name = "api.goodsUpAndDownService")
	private GoodsUpAndDownService goodsUpAndDownService;

	/**
	 * 上架申请
	 */
	public List<GoodsFUas> upToB2C(List<GoodsSimpleUas> simpleGoodses,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsUpAndDownService.upToB2C(simpleGoodses);
			} catch (Exception e) {
			    e.printStackTrace();
			    BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 参数 batchCodes 为用,间隔的字符串 下架处理
	 */
	public Map<String, List<String>> pulloff(String batchCodes,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsUpAndDownService.pulloff(batchCodes);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 商品上架数量修改、下架(传新库存 为0视为下架)
	 * 
	 * @param json
	 * @return 会生成新批次号的为新批次号，新库存为0的会返回原有批次号
	 */
	public List<GoodsFUas> updateGoodses(List<GoodsSimpleUas> simpleGoodses,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsUpAndDownService.updateGoodses(simpleGoodses);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 根据uuids查询上架信息,获取库存
	 */
	public List<GoodsSimpleUas> getListByUuids(String uuids,Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()),
					master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return goodsUpAndDownService.getListByUuids(uuids);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}
}
