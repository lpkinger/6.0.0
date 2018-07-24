package com.uas.b2c.service.seller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.b2c_erp.baisc.model.ComponentSubmitUas;
import com.uas.api.b2c_erp.baisc.service.BrandSubmitUasService;
import com.uas.api.b2c_erp.baisc.service.ComponentService;
import com.uas.api.b2c_erp.baisc.service.ComponentSubmitUasService;
import com.uas.api.b2c_erp.operation.model.OperationInfoUas;
import com.uas.api.b2c_erp.operation.service.OperationInfoUasService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Service
public class B2CDeviceInApplyService {
	@Resource(name = "api.componentSubmitService")
	private ComponentSubmitUasService componentSubmitUasService;

	@Resource(name = "api.componentService")
	private ComponentService componentService;

	@Resource(name = "api.brandSubmitService")
	private BrandSubmitUasService brandSubmitUasService;

	@Resource(name = "api.OperationService")
	private OperationInfoUasService operationInfoUasService;

	@Autowired
	private BaseDao baseDao;

	/**
	 * 上传器件入库申请至商城
	 * 
	 * @param deviceInApply
	 */
	public List<ComponentSubmitUas> save(List<ComponentSubmitUas> deviceInApply) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				// System.out.println(FlexJsonUtil.toJson(deviceInApply));
				return componentSubmitUasService.save(deviceInApply);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 查询单个器件
	 * */
	public ComponentSubmitUas findOne(long id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentSubmitUasService.findOne(id);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 器件审核入库申请至商城
	 * 
	 * @param deviceInApply
	 */
	public Integer submit(Long id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentSubmitUasService.submit(id);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 通过UUID获取器件
	 * 
	 * @param deviceInApply
	 */
	public ComponentSubmitUas findDeviceByUUID(String UUID) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentSubmitUasService.findByUuid(UUID);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	public List<ComponentInfoUas> checkBrandAndCode(String code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentService.findByCode(code);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	public List<Map<String, String>> findPackaging(Long kindid) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return componentService.findPackaging(kindid);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 获取商城审批状态
	 **/
	public void getB2CDeviceAuditStatus() {
		try {
			final List<OperationInfoUas> bs = operationInfoUasService.getSubmit(103);
			if (!CollectionUtil.isEmpty(bs)) {
				final List<String> callbackids = new ArrayList<String>();
				baseDao.getJdbcTemplate().batchUpdate("update BrandInApply set br_b2cstatus=?,br_b2cauditopinion=? where br_b2cid=?", new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement arg0, int arg1) throws SQLException {
						OperationInfoUas info = bs.get(arg1);
						if (info.getB2cId() != null) {
							String status = null;
							if (info.getOperationResult() != null) {
								switch (info.getOperationResult()) {
								case 103:
									status = "审批未通过";
									break;
								case 104:
									status = "审批通过";
									break;
								}
							}
							arg0.setString(1, status);
							arg0.setString(2, info.getOperationRemark());
							arg0.setString(3, info.getB2cId().toString());
							callbackids.add(info.getB2cId().toString());
						}
					}

					@Override
					public int getBatchSize() {
						return bs.size();
					}
				});
				if (!callbackids.isEmpty()) {
					operationInfoUasService.updateSendStatus(CollectionUtil.toString(callbackids));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
