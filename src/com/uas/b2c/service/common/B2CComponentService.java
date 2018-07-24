package com.uas.b2c.service.common;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.b2c_erp.baisc.service.ComponentService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.api.domain.IPage;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.support.ICallable;
import com.uas.erp.core.support.MergeTask;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * 获取b2c平台器件相关信息
 * 
 * @author XiaoST 2016-7-8 下午4:17:18
 */

@Service
public class B2CComponentService {
	@Resource(name = "api.componentService")
	private ComponentService componentService;

	/**
	 * 分页查找类目下器件信息
	 * 
	 * @Tip 对应原接口 sendGetRequest("/api/product/component/list?kindId=?&page=?&count=?))
	 * @return
	 */
	public IPage<ComponentInfoUas> findAllComponentActiveSimpleInfo(int page, int pageSize, Long kindId, boolean reserve) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentService.findAllComponentActiveSimpleInfo(page, pageSize, kindId, reserve);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 根据UUIds获取器件摘要
	 * 
	 * @Tip 对应原接口 sendGetRequest("/api/product/component/Info/byUuids=?")
	 * @param uuids
	 *            (这里可以用","拼接多个uuid)
	 * @return
	 */
	public List<ComponentInfoUas> getSimpleInfoByUuids(String uuids, Master master) {
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentService.getSimpleInfoByUuids(uuids);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 按分割字符拆分原厂型号查找
	 * 
	 * @Tip 对应原接口 sendGetRequest("/api/product/component?code=?")
	 * @param code
	 * @return
	 */
	public List<ComponentInfoUas> findByCode(List<String> oriCodes,final Master master) {
		if (master.b2bEnable()) {
			// 每40一组拆分
			List<List<String>> codesGroup = CollectionUtil.split(oriCodes, 40);
			MergeTask<List<ComponentInfoUas>, String> task = new MergeTask<List<ComponentInfoUas>, String>(
					new ICallable<List<ComponentInfoUas>, String>() {
						@Override
						public List<ComponentInfoUas> call(String codesStr) throws Exception {
							SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
							MultiProxyFactoryBean.setProxy(master.getEnv());
							return componentService.findByCode(codesStr);
						}

					});
			// 按分拆的codes，添加为支线任务
			for (List<String> group : codesGroup) {
				task.join(CollectionUtil.toString(group));
			}
			try {
				return task.execute(ComponentInfoUas.class);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 查找指定类目下的指定原厂型号器件
	 * 
	 * @param kindId
	 * @param code
	 * @return
	 */
	public List<ComponentInfoUas> findComponentInfoUasByCodeAndKindId(Long kindId, String code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentService.findComponentInfoUasByCodeAndKindId(kindId, code);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

}
