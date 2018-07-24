package com.uas.b2c.service.seller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.uas.api.b2c_erp.baisc.model.BrandInfoUas;
import com.uas.api.b2c_erp.baisc.model.BrandSubmitUas;
import com.uas.api.b2c_erp.baisc.model.BrandSubmitUasInfo;
import com.uas.api.b2c_erp.baisc.service.BrandService;
import com.uas.api.b2c_erp.baisc.service.BrandSubmitUasService;
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
public class B2CBrandInApplyService {
	@Resource(name = "api.brandSubmitService")
	private BrandSubmitUasService brandSubmitUasService;

	@Resource(name = "api.BrandService")
	private BrandService brandService;

	@Resource(name = "api.OperationService")
	private OperationInfoUasService operationInfoUasService;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 上传品牌入库申请至商城
	 * 
	 * @param brandInApply
	 */
	public List<BrandSubmitUas> save(List<BrandSubmitUas> brandInApply) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.save(brandInApply);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * UAS审核后提交品牌申请
	 * 
	 * @param brandSubmitUass
	 * @return
	 */
	public BrandSubmitUasInfo submit(Long id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.submit(id);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 获取所有申请
	 * 
	 * @return
	 */
	public List<BrandSubmitUasInfo> findAllSubmit() {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.findAllSubmit();
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 获取单个品牌申请
	 * 
	 * @param id
	 * @return
	 */
	public BrandSubmitUas findOne(Long id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.findOne(id);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 通过UUID获取品牌信息
	 * */
	public BrandSubmitUas findOneByUUID(String UUID) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.findByUuid(UUID);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 通过名字获取最新版本品牌信息
	 * 
	 * @return
	 */
	public BrandSubmitUas findByName(String nameCn, String nameEn) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandSubmitUasService.findByName(nameCn, nameEn);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	public List<BrandInfoUas> findByNameCn(String nameCn, String nameEn) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				return brandService.findByName(nameCn, nameEn);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return null;
	}

	public void getB2CBrandAuditStatus() {
		try {
			final List<OperationInfoUas> bs = operationInfoUasService.getSubmit(104);
			if (!CollectionUtils.isEmpty(bs)) {
				final List<String> callbackIds = new ArrayList<String>();
				baseDao.getJdbcTemplate().batchUpdate("update BrandInApply set br_b2cstatus=?,br_b2cauditopinion=? where br_b2cid=?",
						new BatchPreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps, int row) throws SQLException {
								OperationInfoUas info = bs.get(row);
								if (null != info.getB2cId()) {
									String status = null;
									if (null != info.getOperationResult()) {
										switch (info.getOperationResult()) {
										case 103:
											status = "审批未通过";
											break;
										case 104:
											status = "审批通过";
											break;
										}
									}
									ps.setString(1, status);
									ps.setString(2, info.getOperationRemark());
									ps.setObject(3, info.getB2cId());
									callbackIds.add(String.valueOf(info.getB2cId()));
								}
							}

							@Override
							public int getBatchSize() {
								return bs.size();
							}
						});
				if (!callbackIds.isEmpty())
					operationInfoUasService.updateSendStatus(CollectionUtil.toString(callbackIds));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
