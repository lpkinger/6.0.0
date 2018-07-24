package com.uas.b2b.service.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.b2b.model.Attach;
import com.uas.b2b.model.PurchaseQuaMRB;
import com.uas.b2b.model.PurchaseQuaMRBCheckItem;
import com.uas.b2b.model.PurchaseQuaMRBProjectItem;
import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;

/**
 * 作为卖家ERP，将采购MRB单传入平台
 * 
 * @author suntg
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class PurchaseMRBTask extends AbstractTask {
	@Autowired
	private EmployeeService employeeService;

	@Async
	public void execute() {
		super.execute();
	}

	@Override
	protected void onExecute(Master master) {
		logger.info(this.getClass() + " uploadMRB start");
		uploadMRB(master);// 上传采购验收
		logger.info(this.getClass() + " uploadMRB end");
	}

	/**
	 * 获取需要上传的MRB
	 * 
	 * @return
	 */
	public List<PurchaseQuaMRB> getMRBUpload() {
		try {
			List<PurchaseQuaMRB> purchaseMRBs = baseDao.getJdbcTemplate().query(
					"select qua_mrb.*, vendor.ve_uu mr_venduu, qua_mrb.mr_presult mr_result from qua_mrb "
							+ " left join vendor on qua_mrb.mr_vendcode = vendor.ve_code where mr_sendstatus = '待上传' "
							+ " and mr_status = '已审核' and mr_qctype = '采购检验单' and ve_uu is not null and nvl(ve_b2benable,0)=1 ",
					new BeanPropertyRowMapper<PurchaseQuaMRB>(PurchaseQuaMRB.class));
			for (PurchaseQuaMRB purchaseMRB : purchaseMRBs) {
				List<PurchaseQuaMRBCheckItem> checkItems = baseDao.getJdbcTemplate().query("select * from qua_mrbdet where md_mrid = ? ",
						new BeanPropertyRowMapper<PurchaseQuaMRBCheckItem>(PurchaseQuaMRBCheckItem.class), purchaseMRB.getMr_id());
				purchaseMRB.setCheckItems(checkItems);
				List<PurchaseQuaMRBProjectItem> projectItems = baseDao.getJdbcTemplate().query(
						"select * from qua_mrbdetail where mrd_mrid = ? ",
						new BeanPropertyRowMapper<PurchaseQuaMRBProjectItem>(PurchaseQuaMRBProjectItem.class), purchaseMRB.getMr_id());
				purchaseMRB.setProjectItems(projectItems);

				// 获取MRB单的附件信息
				if (StringUtil.hasText(purchaseMRB.getMr_attach())) {
					String[] fileIds = purchaseMRB.getMr_attach().split(";");
					String erpUrl = getEnterpriseErpUrl();
					List<Attach> attaches = baseDao.getJdbcTemplate()
							.query("select fp_id, fp_size, fp_name from filepath where fp_id in ("
									+ StringUtils.arrayToDelimitedString(fileIds, ",") + ")",
									new BeanPropertyRowMapper<Attach>(Attach.class));
					if (!CollectionUtil.isEmpty(attaches)) {
						for (Attach attach : attaches) {
							attach.setFp_url(erpUrl + Attach.DOWN_FILE_ACTION + attach.getFp_id());
						}
						purchaseMRB.setAttaches(attaches);
					}
				}
			}
			return purchaseMRBs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 获取企业UAS外网地址
	 * 
	 * @return
	 */
	private String getEnterpriseErpUrl() {
		String erpUrl = baseDao.getJdbcTemplate().queryForObject("select max(en_erpurl) from enterprise", String.class);
		if (!StringUtils.isEmpty(erpUrl) && erpUrl.endsWith("/")) {
			erpUrl = erpUrl.substring(0, erpUrl.length() - 1);
		}
		return erpUrl;
	}

	/**
	 * 上传成功后，修改采购验收单/采购验退单的状态
	 */
	public void updateUploadedMRB(List<PurchaseQuaMRB> purchaseMRBs) {
		for (PurchaseQuaMRB purchaseMRB : purchaseMRBs) {
			baseDao.execute("update qua_mrb set mr_sendstatus = '已上传' where mr_id = " + purchaseMRB.getMr_id());
		}
	}

	/**
	 * 上传MRB
	 * 
	 * @param en
	 * @return
	 */
	public boolean uploadMRB(Master master) {
		List<PurchaseQuaMRB> purchaseMRBs = getMRBUpload();
		if (!CollectionUtil.isEmpty(purchaseMRBs)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonArrayDeep(purchaseMRBs));
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite() + "/erp/purchase/MRB?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					updateUploadedMRB(purchaseMRBs);
				}
				baseDao.save(new TaskLog("(买家)MRB-上传MRB单", purchaseMRBs.size(), response));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

}