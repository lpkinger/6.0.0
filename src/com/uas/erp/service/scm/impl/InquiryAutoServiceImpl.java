package com.uas.erp.service.scm.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InquiryAutoDao;
import com.uas.erp.service.scm.InquiryAutoService;

@Service("inquiryAutoService")
public class InquiryAutoServiceImpl implements InquiryAutoService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private InquiryAutoDao inquiryAutoDao;
	@Autowired
	private HandlerService handlerService;
	
	/**
	 * 转物料核价单
	 */
	@Override
	public int turnPurcPrice(int in_id, String caller) {
		int id = 0;
		// 判断该询价单是否已经转入过核价单
		Object code = baseDao.getFieldDataByCondition("InquiryAuto", "in_code", "in_id=" + in_id);
		Object intype = baseDao.getFieldDataByCondition("InquiryAuto", "in_kind", "in_id=" + in_id);
		Object[] pp = baseDao.getFieldsDataByCondition("InquiryAuto", "in_id,in_code", "in_source='" + code + "'");
		if (pp != null && pp[0] != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/Inquiry.jsp?formCondition=in_idIS" + pp[0]
					+ "&gridCondition=id_ppidIS" + pp[0] + "')\">" + pp[1] + "</a>&nbsp;");
		} else {
			int countnum = baseDao.getCount("select count(*) from inquiryAutodetail where nvl(id_isagreed,0)=-1 and id_inid=" + in_id);
			if (countnum > 0) {
				if (intype != null) {
					id = inquiryAutoDao.turnPurcPrice(in_id, intype.toString());
				} else {
					id = inquiryAutoDao.turnPurcPrice(in_id, "采购");
				}
				if (!baseDao.isDBSetting(caller, "noAutoPurcPrice")) {
					List<Object[]> list = baseDao.getFieldsDatasByCondition(
							"purchasePrice left join purchasePriceDetail on pp_id=ppd_ppid", new String[] { "ppd_vendcode", "ppd_prodcode",
									"ppd_currency", "pp_kind" }, "ppd_ppid=" + id + " and ppd_statuscode = 'VALID'");// 供应商、料号、币别、定价类型
					if (!list.isEmpty()) {
						for (Object[] objs : list) {
							List<Object[]> spds = baseDao.getFieldsDatasByCondition(
									"purchasePrice left join purchasePriceDetail on pp_id=ppd_ppid", new String[] { "ppd_id", "pp_code",
											"pp_id", "ppd_detno" }, "ppd_vendcode='" + objs[0] + "' AND ppd_statuscode='VALID'"
											+ " AND ppd_prodcode='" + objs[1] + "' AND ppd_currency='" + objs[2] + "'"
											+ " and ppd_ppid <> " + id + " AND pp_kind='" + objs[3] + "'");
							for (Object[] spd : spds) {
								String str = DateUtil.currentDateString(Constant.YMD_HMS) + "新价格转入失效";
								baseDao.updateByCondition("purchasePriceDetail",
										"ppd_unvaliddate=sysdate,ppd_statuscode='UNVALID',ppd_status='" + BaseUtil.getLocalMessage("UNVALID") + "',ppd_remark='"
												+ str + "'", "ppd_id=" + spd[0]);
							}
						}
					}
				}
				// 默认更新物料资料为0或者为空的最小包装数、最小订购量、采购周期
				if (baseDao.isDBSetting("PurchasePrice", "UpdateProduct")) {
					String sqlstr = "update product set (pr_leadtime,pr_zxdhl,pr_zxbzs)=(select  max(NVL(ppd_purctime,0)),MAX(NVL(ppd_minqty,0)),max(NVL(ppd_zxbzs,0)) from purchasepricedetail where ppd_prodcode=pr_code and ppd_ppid="
							+ id
							+ ") where pr_code in (select ppd_prodcode from purchasepricedetail where ppd_ppid="
							+ id
							+ ") and  nvl(pr_leadtime,0)=0 and nvl(pr_zxdhl,0)=0 and nvl(pr_zxbzs,0)=0";
					baseDao.execute(sqlstr);
				}
				//执行转审核单以后的逻辑
				handlerService.handler(caller, "turnPurcPrice", "after", new Object[] { id });
				// 记录操作
				baseDao.logger.turn("msg.turnPurcPrice", "InquiryAuto", "in_id", in_id);
			} else {
				baseDao.updateByCondition("InquiryAuto", "in_checkstatus='已批准',in_checkstatuscode='APPROVED'", "in_id=" + in_id);
				baseDao.updateByCondition("INQUIRYAUTODETAIL", "id_sendstatus='待上传'", "id_inid=" + in_id);
				BaseUtil.showErrorOnSuccess(BaseUtil.getLocalMessage("scm.purchase.inquiry.nohaveturn"));
			}
		}
		baseDao.updateByCondition("InquiryAuto", "in_checkstatus='已批准',in_checkstatuscode='APPROVED'", "in_id=" + in_id);
		baseDao.updateByCondition("InquiryAutoDetail ", "id_myfromdate=sysdate", "id_inid="+in_id+" and id_isagreed=-1");
		SqlRowList rsdate = baseDao.queryForRowSet("select * from purchasepricedetail where ppd_statuscode='VALID' and trunc(ppd_todate)<trunc(sysdate) and ppd_ppid="+id);
		if(rsdate.next()){
			String sql = "update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||' 过期自动失效' where ppd_statuscode='VALID' and trunc(ppd_todate)<trunc(sysdate) and ppd_ppid="+id;
			baseDao.execute(sql);
		}
		return id;
	}
	
	@Override
	public List<Map<String, Object>> getStepDet(int in_id) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from InquiryAutoDetailDet where idd_idid in (select id_id from InquiryAutoDetail where id_inid=?) order by idd_idid,idd_lapqty",
						in_id);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("idd_id", rs.getObject("idd_id"));
			map.put("idd_idid", rs.getObject("idd_idid"));
			map.put("idd_lapqty", rs.getObject("idd_lapqty"));
			data.add(map);
		}
		return data;
	}
	
	@Override
	public void agreeAutoPrice(int id, String param) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		int sign = -1;
		for(Map<Object, Object>map:grid){
			if(StringUtil.hasText(map.get("id_isagreed")) && "false".equals(map.get("id_isagreed").toString())){
				sign = 0;
			}
			baseDao.execute("update inquiryautodetail set id_isagreed="+sign+" where id_id="+map.get("id_id"));
		}
		baseDao.execute("update inquiryauto set in_pdstatus='已判定' where in_id="+id);
	}
	
	
	@Override
	public void submitInquiryAuto(int id,String caller) {
		Object checkstatus = baseDao.getFieldDataByCondition("InquiryAuto", "in_checkstatuscode", "in_id=" + id);
		if (checkstatus != null && !"ENTERING".equals(checkstatus)) {
			BaseUtil.showError("单据当前状态不允许提交!");
		}
		handlerService.beforeSubmit(caller,id);
		baseDao.execute("update inquiryauto set in_checkstatus='已提交',in_checkstatuscode='COMMITED' where in_id="+id);
		baseDao.logger.submit(caller, "in_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}
	
	@Override
	public void resSubmitInquiryAuto(int id,String caller) {
		Object checkstatus = baseDao.getFieldDataByCondition("InquiryAuto", "in_checkstatuscode", "in_id=" + id);
		if (checkstatus != null && !"COMMITED".equals(checkstatus)) {
			BaseUtil.showError("单据当前状态不允许反提交!");
		}
		handlerService.beforeResSubmit(caller, id);
		baseDao.execute("update inquiryauto set in_checkstatus='在录入',in_checkstatuscode='ENTERING' where in_id="+id);
		baseDao.execute("update inquiryautodetail set id_isagreed=-1 where id_inid="+id);
		baseDao.logger.resSubmit(caller, "in_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);
	}
	
	@Override
	public void deleteAutoDet(int id,String caller) {
		baseDao.execute("update inquiryautodetail set id_inid=0 where id_id="+id);
	}
	
	@Override
	public void deleteAuto(int id,String caller) {
		baseDao.execute("delete inquiryauto where in_id="+id);
		baseDao.execute("update inquiryautodetail set id_inid=0 where id_inid="+id);
	}
}
