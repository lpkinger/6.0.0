package com.uas.erp.service.scm.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import oracle.net.aso.s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.Session;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.tools.javac.code.Attribute.Array;
import com.uas.b2b.model.InquiryDetailDet;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.InquiryDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.InquiryService;
import com.uas.erp.model.DetailGrid;

@Service("inquiryService")
public class InquiryServiceImpl implements InquiryService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private InquiryDao inquiryDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private DetailGridDao detailGridDao;

	final static String startAutoInquiry="Insert into batchinquiry(BI_ID,BI_CODE,BI_DATE,BI_KIND,BI_STATUS,BI_STATUSCODE,BI_PRICEKIND,BI_RECORDER,BI_RECORDDATE,BI_ENDDATE,BI_REMARK,BI_PRICETYPE,BI_SENDSTATUS) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String startAutoInquiryDetail="Insert into BATCHINPROD (BIP_ID,BIP_BIID,BIP_DETNO,BIP_PRODCODE,bip_buyercode,bip_buyername,bip_buyeruu,bip_mobile,bip_prec,bip_prdetail,bip_orispeccode,bip_brand,bip_unit) values (BATCHINPROD_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String startAutoInquiryVend="Insert into batchinvendor (BIV_ID,BIV_BIID,BIV_DETNO,BIV_VENDCODE,BIV_VENDNAME,BIV_VENDUU,BIV_CURRENCY) values (batchinvendor_seq.nextval,?,?,?,?,?,?)";
	final static String startInquiryDetail ="Insert into inquirydetail (ID_ID,ID_INID,ID_DETNO,ID_PRODID,ID_PRODCODE,ID_DELIVERY,ID_REMARK,ID_CURRENCY,ID_RATE,ID_ISAGREED,ID_VENDCODE,ID_VENDNAME,ID_VENDUU,ID_PPDATE,ID_CODE) values  (inquirydetail_seq.nextval,?,?,?,?,sysdate,'询价单入口转入',?,?,1,?,?,?,sysdate,?)";
	@Override
	@Transactional
	public void saveInquiry(String formStore, String gridStore, String dets, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> detGrid = BaseUtil.parseGridStoreToMaps(dets);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Inquiry", "in_code='" + store.get("in_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.save_codeHasExist"));
		}
		// maz 判断是否是批量询价类型，如果是，grid中添加所有供应商
		if("批量询价".equals(store.get("in_prodtype"))){
			String[] batchvendor = store.get("in_batchvendor").toString().split("#");
			int size = grid.size();
			for(int i=1;i<batchvendor.length;i++){
				for(int j=0;j<size;j++){
					Map<Object, Object> addmap = new HashMap<Object, Object>();
					addmap.putAll(grid.get(j));
					addmap.put("id_vendcode", batchvendor[i]);
					addmap.put("id_detno", j+1+size*i);
					grid.add(addmap);
				}
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		List<String> sqls = new ArrayList<String>();
		// 保存Inquiry
		sqls.add(SqlUtil.getInsertSqlByMap(store, "Inquiry"));
		// 保存InquiryDetailDet
		Map<Object, List<Map<Object, Object>>> detList = BaseUtil.groupMap(detGrid, "idd_idid");
		for (Map<Object, Object> map : grid) {
			int id = baseDao.getSeqId("InquiryDetail_SEQ");
			detGrid = detList.get(map.get("id_id"));
			if (detGrid != null) {
				for (Map<Object, Object> m : detGrid)
					m.put("idd_idid", id);
				sqls.addAll(SqlUtil.getInsertSqlbyList(detGrid, "InquiryDetailDet", "idd_id"));
			} else {
				sqls.add("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty) values (InquiryDetailDet_seq.nextval," + id + ",0)");
			}
			map.put("id_id", id);
		}
		// 保存InquiryDetail
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "InquiryDetail"));
		baseDao.execute(sqls);
		Object in_id = store.get("in_id");
		baseDao.updateByCondition("InquiryDetail", "id_isagreed=1", "id_inid=" + in_id);
		baseDao.execute("update InquiryDetail set id_code=(select in_code from Inquiry where id_inid=in_id) where id_inid="
				+ store.get("in_id") + " and not exists (select 1 from Inquiry where id_code=in_code)");
		baseDao.execute("update InquiryDetail set (id_currency,id_vendname,id_venduu)=(select ve_currency,ve_name,ve_uu from vendor where ve_code=id_vendcode) where id_inid= "
				+ in_id + " and nvl(id_vendname,' ')=' '");
		useVendDefaultCurrency(caller, in_id);
		defaultTax(caller, in_id);
		defaultEndDate(caller, in_id);
		checkDetail(in_id);
		baseDao.logger.save(caller, "in_id", in_id);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	/**
	 * 物料编号+供应商编号+币别有重复
	 */
	private void checkDetail(Object in_id) {
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet("select * from Inquiry left join InquiryDetail on in_id=id_inid where id_inid=?", in_id);
		while (rs.next()) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '询价单号：'||in_code||'，序号：'||id_detno) from Inquiry left join InquiryDetail on in_id=id_inid where id_id<>? and id_prodcode=? and id_vendcode=? and id_currency=?",
							String.class, rs.getObject("id_id"), rs.getObject("id_prodcode"), rs.getObject("id_vendcode"),
							rs.getObject("id_currency"));
			if (dets != null) {
				sb1.append(dets).append("<hr>");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '询价单号：'||in_code||'，序号：'||id_detno) from Inquiry left join InquiryDetail on in_id=id_inid where id_id<>? and id_prodcode=? and id_vendcode=? and id_currency=? and id_myfromdate=? and id_mytodate=? and id_inid=?",
							String.class, rs.getObject("id_id"), rs.getObject("id_prodcode"), rs.getObject("id_vendcode"),
							rs.getObject("id_currency"), rs.getObject("id_myfromdate"), rs.getObject("id_mytodate"), in_id);
			if (dets != null) {
				sb2.append(dets).append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.appendError("物料+供应商+币别在其它询价单明细行中已存在！<hr>" + sb1.toString());
		}
		if (sb2.length() > 0) {
			BaseUtil.showError("物料+供应商+币别+价格生效日期+价格截止日期在其它询价单明细行中已存在！<hr>" + sb2.toString());
		}
		if ("是".equals(rs.getGeneralString("id_ismaxlimit")) && rs.getGeneralDouble("id_maxlimit") <= 0) {
			BaseUtil.appendError("明细行最大限购量不能为0！行号：" + rs.getGeneralInt("id_detno"));
		}
	}

	@Override
	public void deleteInquiry(int in_id, String caller) {
		// 只能删除在录入的单!
		Object status = baseDao.getFieldDataByCondition("Inquiry", "in_statuscode", "in_id=" + in_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, in_id);
		// 删除Inquiry
		baseDao.deleteById("Inquiry", "in_id", in_id);
		// 删除InquiryDetail
		baseDao.deleteById("inquirydetail", "id_inid", in_id);
		baseDao.deleteByCondition("inquirydetaildet", "idd_idid=" + in_id);
		// 记录操作
		baseDao.logger.delete(caller, "in_id", in_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, in_id);
	}

	@Override
	@Transactional
	public void updateInquiryById(String formStore, String gridStore, String dets, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> detGrid = BaseUtil.parseGridStoreToMaps(dets);
		// 只能修改未批准的询价单资料!
		Object status = baseDao.getFieldDataByCondition("Inquiry", "in_checkstatuscode", "in_id=" + store.get("in_id"));
		if (status != null && status.equals("已批准")) {
			BaseUtil.showError("单据已批准，不允许修改!");
		}
		// 当前编号的记录已经存在,不能更新!
		boolean bool = baseDao.checkByCondition("Inquiry", "in_code='" + store.get("in_code") + "' and in_id<>" + store.get("in_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		List<String> sqls = new ArrayList<String>();
		// 修改Inquiry
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "Inquiry", "in_id"));
		if (gstore.size() > 0) {
			Map<Object, List<Map<Object, Object>>> detList = BaseUtil.groupMap(detGrid, "idd_idid");
			for (Map<Object, Object> s : gstore) {
				if (s.get("id_id") == null || s.get("id_id").equals("") || s.get("id_id").equals("0")
						|| Integer.parseInt(s.get("id_id").toString()) <= 0) {
					int id = baseDao.getSeqId("InquiryDetail_SEQ");
					detGrid = detList.get(s.get("id_id"));
					if (detGrid != null) {
						for (Map<Object, Object> m : detGrid)
							m.put("idd_idid", id);
						sqls.addAll(SqlUtil.getInsertSqlbyList(detGrid, "InquiryDetailDet", "idd_id"));
					} else {
						sqls.add("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty) values (InquiryDetailDet_seq.nextval," + id
								+ ",0)");
					}
					s.put("id_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "InquiryDetail", new String[] { "id_id" }, new Object[] { id });
					sqls.add(sql);
				} else {
					detGrid = detList.get(s.get("id_id"));
					if (detGrid != null) {
						// 先删除无效行
						sqls.add("delete from InquiryDetailDet where idd_idid=" + s.get("id_id") + " and idd_id not in ("
								+ CollectionUtil.pluckSqlString(detGrid, "idd_id") + ")");
						sqls.addAll(SqlUtil.getInsertOrUpdateSql(detGrid, "InquiryDetailDet", "idd_id"));
					} else {
						sqls.add("delete from InquiryDetailDet where idd_idid=" + s.get("id_id"));
						sqls.add("insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty) values (InquiryDetailDet_seq.nextval,"
								+ s.get("id_id") + ",0)");
					}
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(gstore, "InquiryDetail", "id_id"));
		}
		baseDao.execute(sqls);
		Object in_id = store.get("in_id");
		baseDao.updateByCondition("InquiryDetail", "id_isagreed=1", "id_inid=" + in_id);
		baseDao.execute("update InquiryDetail set id_code=(select in_code from Inquiry where id_inid=in_id) where id_inid="
				+ store.get("in_id") + " and not exists (select 1 from Inquiry where id_code=in_code)");
		baseDao.execute("update InquiryDetail set (id_currency,id_vendname,id_venduu)=(select ve_currency,ve_name,ve_uu from vendor where ve_code=id_vendcode) where id_inid= "
				+ in_id + " and nvl(id_vendname,' ')=' '");
		useVendDefaultCurrency(caller, in_id);
		defaultTax(caller, in_id);
		String effectiveDays = baseDao.getDBSetting("PurchasePrice", "effectiveDays");
		if (effectiveDays != null && !"0".equals(effectiveDays)) {
			baseDao.execute("update InquiryDetail set id_mytodate=id_myfromdate+" + effectiveDays + " where id_mytodate is null and id_inid=" + in_id);
		}
		checkDetail(in_id);
		// 记录操作
		baseDao.logger.update(caller, "in_id", store.get("in_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printInquiry(int in_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, in_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.post(caller, "in_id", in_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, in_id);
	}

	@Override
	public void auditInquiry(int in_id, String caller) {
		baseDao.execute("update InquiryDetail set id_code=(select in_code from Inquiry where id_inid=in_id) where id_inid=" + in_id
				+ " and not exists (select 1 from Inquiry where id_code=in_code)");
		baseDao.execute("update inquirydetail set id_venduu=(select max(ve_uu) from vendor where ve_code=id_vendcode) where id_inid="+in_id+" and "
				+ "nvl(id_vendcode,' ') <>' ' and not EXISTS (select 1 from vendor where nvl(ve_code,' ')=nvl(id_vendcode,' ') and nvl(id_venduu,0)=nvl(ve_uu,0))");//更新供应商UU，解决导入数据导致供应商编号与UU号不匹配
		baseDao.execute("update inquiry set in_enddate=(TRUNC(in_enddate)+1-1/86400) where in_id="+in_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, in_id);
		int countnum = baseDao.getCount("select count(*) from inquirydetail where id_inid=" + in_id);
		if (countnum == 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.nodetailAudited"));
		} else {
			countnum = baseDao.getCount("select count(*) from inquirydetail where nvl(id_venduu,' ')=' ' and id_inid=" + in_id);
			if (countnum > 0) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.novendorAudited"));
			}
			countnum = baseDao.getCount("select count(*) from inquirydetail where nvl(id_price,0)>0 and nvl(id_isagreed,0)=-1 and id_inid="
					+ in_id);
			if (countnum > 0) {
				BaseUtil.showError("明细行价格不为0，若不需要供应商询价的请直接走核价单！");
			}
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(id_detno) from InquiryDetail left join Product on id_prodcode=pr_code where nvl(pr_statuscode,' ')<>'AUDITED' and id_inid="
								+ in_id, String.class);
		if (dets != null) {
			BaseUtil.showError("物料状态不等于已审核或者不存在，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(id_detno) from InquiryDetail left join Vendor on id_vendcode=ve_code where (nvl(ve_auditstatuscode,' ')<>'AUDITED' or nvl(ve_b2benable,0)=0) and id_inid="
								+ in_id, String.class);
		if (dets != null) {
			BaseUtil.showError("供应商状态不等于已审核或者不存在，或者UU号未检测通过，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(id_detno) from InquiryDetail left join Currencys on id_currency=cr_name where nvl(cr_statuscode,' ')<>'CANUSE' and id_inid="
								+ in_id, String.class);
		if (dets != null) {
			BaseUtil.showError("币别状态不等于可使用或者不存在，不允许审核!行号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(id_detno) from InquiryDetail where id_inid=" + in_id
						+ " and nvl(id_ismaxlimit,' ')='是' and nvl(id_maxlimit,0)<=0", String.class);
		if (dets != null) {
			BaseUtil.showError("明细行最大限购量不能为0！行号：" + dets);
		}
		useVendDefaultCurrency(caller, in_id);
		defaultTax(caller, in_id);
		allowZeroTax(caller, in_id);
		maxDateInterval(caller, in_id);
		// maz  采购询价单审核时更新该物料在物料资料中的最近询价日期  17-09-25
		baseDao.execute("update product set pr_lastinquirydate=(select in_date from inquiry where in_id="+in_id+") where pr_code in (select id_prodcode from inquirydetail where id_inid="+in_id+")");
		// 执行审核操作
		baseDao.audit("Inquiry", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditor");
		baseDao.updateByCondition("Inquiry", "in_sendstatus='待上传'", "in_id=" + in_id
				+ " and nvl(in_class,' ')<>'主动报价' and nvl(IN_SENDSTATUS,' ')<>'已上传'");
		// 记录操作
		baseDao.logger.audit(caller, "in_id", in_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, in_id);
	}

	@Override
	public void resAuditInquiry(int in_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Inquiry", "in_statuscode,in_checkstatuscode", "in_id=" + in_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.resAudit_onlyAudit"));
		}
		if (status[1] != null && !"ENTERING".equals(status[1])) {
			BaseUtil.showError("当前状态不允许反审核!");
		}
		// 询价单上传状态
		String sendStatus = baseDao.getFieldValue("Inquiry", "in_sendstatus", "in_id=" + in_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		StateAssert.onSendedLimit(sendStatus);
		// 判断该询价单是否已经转入过核价单
		Object code = baseDao.getFieldDataByCondition("Inquiry", "in_code", "in_id=" + in_id);
		code = baseDao.getFieldDataByCondition("Inquiry", "in_code", "in_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.resAudit_haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/Inquiry.jsp?formCondition=in_codeIS" + code
					+ "&gridCondition=id_ppcodeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 执行反审核操作
			baseDao.resAudit("Inquiry", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditor");
			baseDao.updateByCondition("Inquiry", "in_sendstatus=null", "in_id=" + in_id);
			// 记录操作
			baseDao.logger.resAudit(caller, "in_id", in_id);
		}
	}

	@Override
	public void submitInquiry(int in_id, String caller) {
		// 清除所有无效分段明细
		baseDao.execute("delete from InquiryDetailDet where not exists (select 1 from InquiryDetail where id_id=idd_idid)");
		baseDao.execute("update InquiryDetail set id_code=(select in_code from Inquiry where id_inid=in_id) where id_inid=" + in_id
				+ " and not exists (select 1 from Inquiry where id_code=in_code)");
		Object[] source = baseDao.getFieldsDataByCondition("Inquiry", new String[]{"in_code","in_prodtype","in_class","in_enddate"}, "in_id="+in_id);
		if (StringUtil.hasText(source[2]) && "主动报价".equals(source[2])) {
			baseDao.execute("update Inquiry set in_kind='采购',in_enddate=nvl(in_enddate,sysdate),in_cop=in_enname,in_recorder='"
					+ SystemSession.getUser().getEm_name() + "' where in_id=" + in_id);
			baseDao.execute("update inquirydetail set id_vendcode=(select ve_code from vendor where ve_uu=id_venduu) where id_inid="
					+ in_id);
		}
		Object checkstatus = baseDao.getFieldDataByCondition("Inquiry", "in_checkstatuscode", "in_id=" + in_id);
		if (checkstatus != null && !"ENTERING".equals(checkstatus)) {
			BaseUtil.showError("单据当前状态不允许提交!");
		}
		int count = 0;
		count = baseDao.getCount("select count(*) from inquirydetail left join inquirydetaildet on idd_idid=id_id where (nvl(id_price,0)>0 or nvl(idd_price,0)>0) and id_inid=" + in_id);
		if (count == 0) {
			BaseUtil.showError("没有供应商报价或者供应商报价全部为0，不能提交!");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(id_detno) from InquiryDetail left join Product on id_prodcode=pr_code where nvl(pr_statuscode,' ')<>'AUDITED' and id_inid="
								+ in_id, String.class);
		if (dets != null) {
			BaseUtil.showError("物料状态不等于已审核或者不存在，不允许审核!行号：" + dets);
		}
		if(!"公开询价".equals(source[1]) && DateUtil.compare(source[3].toString(), DateUtil.getCurrentDate())==1){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(id_detno) from InquiryDetail left join Vendor on id_vendcode=ve_code where (nvl(ve_auditstatuscode,' ')<>'AUDITED' or nvl(ve_b2benable,0)=0) and id_inid="
									+ in_id, String.class);
			if (dets != null) {
				BaseUtil.showError("供应商状态不等于已审核或者不存在，或者UU号未检测通过，不允许审核!行号：" + dets);
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(id_detno) from InquiryDetail left join Currencys on id_currency=cr_name where nvl(cr_statuscode,' ')<>'CANUSE' and id_inid="
								+ in_id, String.class);
		if (dets != null) {
			BaseUtil.showError("币别状态不等于可使用或者不存在，不允许审核!行号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(id_detno) from InquiryDetail where id_inid=" + in_id
						+ " and nvl(id_ismaxlimit,' ')='是' and nvl(id_maxlimit,0)<=0", String.class);
		if (dets != null) {
			BaseUtil.showError("明细行最大限购量不能为0！行号：" + dets);
		}
		Object status = null;
		if(!"公开询价".equals(source[1])){
			List<Object> codes2 = baseDao.getFieldDatasByCondition("InquiryDetail", "id_vendcode", "id_inid=" + in_id);
			for (Object c : codes2) {
				status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + c + "'");
				if (!status.equals("AUDITED")) {
					BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited")
							+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + c + "')\">" + c
							+ "</a>&nbsp;");
				}
			}
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("InquiryDetail", "id_prodcode", "id_inid=" + in_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		/**
		 *  maz 询价单明细行物料+供应商+币别+分段数量重复限制提交 2017080406
		 */
		SqlRowList rs = baseDao.queryForRowSet("select * from InquiryDetail left join InquiryDetailDet on id_id=idd_idid  where id_inid=?", in_id);
		StringBuffer sb = new StringBuffer();
		int i = -1;
		String prod = "";
		while(rs.next()){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(id_lapqty) from Inquiry left join InquiryDetail on in_id=id_inid left join InquiryDetailDet on id_id=idd_idid where in_id=? and idd_id<>? and id_prodcode=? and id_vendcode=? and id_currency=? and idd_lapqty=? order by idd_lapqty",
							String.class,rs.getObject("id_inid"), rs.getObject("idd_id"), rs.getObject("id_prodcode"), rs.getObject("id_vendcode"),
							rs.getObject("id_currency"),rs.getObject("idd_lapqty"));
			if (dets != null && i != rs.getInt("idd_lapqty") && !rs.getString("id_prodcode").equals(prod)) {
				sb.append(rs.getObject("id_prodcode")).append("&nbsp&nbsp").append("分段数量"+rs.getObject("idd_lapqty")+"<hr>");
			}
			i = rs.getInt("idd_lapqty");
			prod = rs.getString("id_prodcode");
		}
		if(sb.length()>0){
			BaseUtil.showError("同供应商同物料同币别同分段数量不允许重复报价!明细行:<hr>" + sb.toString() +"出现重复报价");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, in_id);
		// maz  审核后计算汇率、本币不含税单价   2017-08-28
		baseDao.execute("update InquiryDetail a set id_exchangerate=(select cm_crrate from currencysmonth where cm_crname=a.id_currency and cm_yearmonth=to_char(sysdate,'yyyymm')) where id_inid="+in_id);
		baseDao.execute("update INQUIRYDETAILDET a set idd_preprice=(idd_price*nvl((select id_exchangerate from inquirydetail where a.idd_idid=id_id),1)/(1+nvl((select id_rate from inquirydetail where a.idd_idid=id_id),0)/100)) where idd_idid in (select id_id from inquirydetail where id_inid="+in_id+")");
		// 执行提交操作
		baseDao.submit("Inquiry", "in_id=" + in_id, "in_checkstatus", "in_checkstatuscode");
		baseDao.execute("update Inquiry set in_checksendstatus='待上传' where in_id=? and in_sendstatus='已上传'", in_id);
		if("公开询价".equals(source[1])){
			baseDao.execute("update BATCHINQUIRY set bi_checksendstatus='待上传' where bi_code='"+source[0]+"'");
			baseDao.execute("update InquiryDetail a set id_vendcode=(select ve_code from vendor where ve_uu=a.id_venduu) where id_inid="+in_id);
		}
		// 记录操作
		baseDao.logger.submit(caller, "in_id", in_id);
		
		getLastPrice(in_id);
		
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, in_id);
	}

	@Override
	public void resSubmitInquiry(int in_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Inquiry", "in_checkstatuscode", "in_id=" + in_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, in_id);
		// 执行反提交操作
		baseDao.resOperate("Inquiry", "in_id=" + in_id, "in_checkstatus", "in_checkstatuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "in_id", in_id);
		handlerService.afterResSubmit(caller, in_id);
	}

	/**
	 * 转物料核价单
	 */
	@Override
	public int turnPurcPrice(int in_id, String caller) {
		int id = 0;
		// 判断该询价单是否已经转入过核价单
		Object code = baseDao.getFieldDataByCondition("Inquiry", "in_code", "in_id=" + in_id);
		Object intype = baseDao.getFieldDataByCondition("Inquiry", "in_kind", "in_id=" + in_id);
		Object[] pp = baseDao.getFieldsDataByCondition("Inquiry", "in_id,in_code", "in_source='" + code + "'");
		if (pp != null && pp[0] != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.inquiry.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/Inquiry.jsp?formCondition=in_idIS" + pp[0]
					+ "&gridCondition=id_ppidIS" + pp[0] + "')\">" + pp[1] + "</a>&nbsp;");
		} else {
			int countnum = baseDao.getCount("select count(*) from inquirydetail where nvl(id_isagreed,0)=-1 and id_inid=" + in_id);
			if (countnum > 0) {
				/*
				 * if (intype.toString().contains("样品")) { id =
				 * inquiryDao.turnPurcPrice(in_id, "样品"); } else if
				 * (intype.toString().contains("委外")) { id =
				 * inquiryDao.turnPurcPrice(in_id, "委外"); } else { id =
				 * inquiryDao.turnPurcPrice(in_id, "采购"); }
				 */
				if (intype != null) {
					id = inquiryDao.turnPurcPrice(in_id, intype.toString());
				} else {
					id = inquiryDao.turnPurcPrice(in_id, "采购");
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
				baseDao.logger.turn("msg.turnPurcPrice", "Inquiry", "in_id", in_id);
			} else {
				baseDao.updateByCondition("Inquiry", "in_checkstatus='已批准',in_checkstatuscode='APPROVED'", "in_id=" + in_id);
				baseDao.updateByCondition("INQUIRYDETAIL", "id_sendstatus='待上传'", "id_inid=" + in_id);
				BaseUtil.showErrorOnSuccess(BaseUtil.getLocalMessage("scm.purchase.inquiry.nohaveturn"));
			}
		}
		baseDao.updateByCondition("Inquiry", "in_checkstatus='已批准',in_checkstatuscode='APPROVED'", "in_id=" + in_id);
		/**
		 * @author wsy
		 * 索菱询价单批准，更新已经采纳的明细数据的“价格生效日期”为“批准日期”
		 */
		baseDao.updateByCondition("InquiryDetail ", "id_myfromdate=sysdate", "id_inid="+in_id+" and id_isagreed=-1");
		SqlRowList rsdate = baseDao.queryForRowSet("select * from purchasepricedetail where ppd_statuscode='VALID' and trunc(ppd_todate)<trunc(sysdate) and ppd_ppid="+id);
		if(rsdate.next()){
			String sql = "update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||' 过期自动失效' where ppd_statuscode='VALID' and trunc(ppd_todate)<trunc(sysdate) and ppd_ppid="+id;
			baseDao.execute(sql);
		}
		return id;
	}

	// 有效截止日期默认等于有效开始日期+设置有效天数
	private void defaultEndDate(String caller, Object in_id) {
		String effectiveDays = baseDao.getDBSetting("PurchasePrice", "effectiveDays");
		if (effectiveDays != null && !"0".equals(effectiveDays)) {
			baseDao.execute("update InquiryDetail set id_mytodate=id_myfromdate+" + effectiveDays + " where id_inid=" + in_id);
		}
	}

	// 税率默认
	private void defaultTax(String caller, Object in_id) {
		String defaultTax = baseDao.getDBSetting("PurchasePrice", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update InquiryDetail set id_rate=(select nvl(cr_taxrate,0) from currencys where id_currency=cr_name and cr_statuscode='CANUSE')"
						+ " where id_inid=" + in_id);
			}
			// 税率强制等于供应商资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute("update InquiryDetail set id_rate=(select nvl(ve_taxrate,0) from vendor where id_vendcode=ve_code and ve_auditstatuscode='AUDITED')"
						+ " where id_inid=" + in_id);
			}
		}
	}

	// 币别强制等于供应商资料的默认币别
	private void useVendDefaultCurrency(String caller, Object in_id) {
		if (baseDao.isDBSetting("PurchasePrice", "useVendDefaultCurrency")) {
			baseDao.execute("update InquiryDetail set id_currency=(select ve_currency from vendor where id_vendcode=ve_code and ve_auditstatuscode='AUDITED')"
					+ " where id_inid=" + in_id);
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object in_id) {
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(id_detno) from InquiryDetail where nvl(id_rate,0)=0 and id_currency=? and id_inid=?", String.class,
					currency, in_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	// 间隔天数大于所设置天数，不允许提交
	private void maxDateInterval(String caller, Object in_id) {
		String maxDateInterval = baseDao.getDBSetting("PurchasePrice", "maxDateInterval");
		if (maxDateInterval != null) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(id_detno) from InquiryDetail where id_todate-id_fromdate>? and id_inid=?", String.class,
					maxDateInterval, in_id);
			if (dets != null) {
				BaseUtil.showError("有效起止日期间隔大于" + maxDateInterval + "天，不允许提交!行号：" + dets);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getStepDet(int in_id) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from InquiryDetailDet where idd_idid in (select id_id from InquiryDetail where id_inid=?) order by idd_idid,idd_lapqty",
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
	public List<InquiryDetailDet> findReplyByInid(int id) {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select InquiryDetailDet.* from InquiryDetailDet left join inquirydetail on id_id=idd_idid  where id_inid=? order by idd_idid,idd_lapqty",
							new BeanPropertyRowMapper<InquiryDetailDet>(InquiryDetailDet.class), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 计算上次价格，当前物料价格浮动率
	 */
	private void getLastPrice(Object id) {
		baseDao.updateByCondition("inquirydetail", "id_freerate=0", "id_inid="+id);
		String datas = baseDao.getDBSetting("freeRateGetPrice");
		Object kind = baseDao.getFieldDataByCondition("inquiry", "in_kind", "in_id="+id);
		if(datas==null || datas.equals("N,N,N,N,N,N")){//默认为物料+供应商+币别+类型--最新有效价格
			datas = "A";
		}
	    for(int i=0;i<datas.replace(",", "").length();i++){
	    	SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM inquirydetail LEFT JOIN inquiry on in_id=id_inid WHERE id_freerate=0 and in_id=?", id);
	    	Object[] price = null;
			while (rs.next()) {
				String[] data = datas.split(",");
				if(data[i].equals("A")){
					price = baseDao.getFieldsDataByCondition("(select * from (select ppd_price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
									+ "where ppd_vendcode='" + rs.getString("id_vendcode") + "' and ppd_prodcode='" + rs.getString("id_prodcode")+"' "
									+ "and ppd_currency='"+rs.getString("id_currency")+"' and pp_kind='"+kind+"' and ppd_statuscode='VALID' "
									+ "order by pp_indate desc) order by ppd_id desc) ", new String[]{"ppd_price","ppd_id"},
									"rownum<2");//物料+供应商+币别+类型--最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) {
						baseDao.updateByCondition("inquirydetail", "id_preprice=" + price[0] + ",id_freerate=(id_price-" + price[0] + ")/" + price[0]+"",
								"id_freerate=0 and id_id=" + rs.getInt("id_id"));
					}
					//防止没有该参数的客户正常客户因为缺少id_ppdid字段报错
					if(datas.replace(",", "").length()>1 && price!=null){
						baseDao.updateByCondition("inquirydetail", "id_ppdid="+price[1]+"",
								"id_id=" + rs.getInt("id_id"));
					}
				}else if(data[i].equals("B")){
					price = baseDao.getFieldsDataByCondition("(select * from (select ppd_price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
									+ "  where ppd_vendcode='" + rs.getString("id_vendcode") + "' and ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and ppd_currency='" + rs.getString("id_currency")+ "' and pp_kind='"+kind+"' "
									+ " and ppd_statuscode='VALID' and nvl(ppd_lapqty,0)="+rs.getDouble("id_lapqty")+" order by pp_indate desc) order by ppd_id desc) ", new String[]{"ppd_price","ppd_id"},
									" rownum<2 "); //物料+供应商+币别+类型+分段数---最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						baseDao.updateByCondition("inquirydetail", "id_preprice=" + price[0] + ",id_freerate=(id_price-" + price[0] + ")/" + price[0]+",id_ppdid="+price[1]+"",
								"id_freerate=0 and id_id=" + rs.getInt("id_id"));
					}
				}else if(data[i].equals("C")){
					price = baseDao.getFieldsDataByCondition("(select * from (select nvl(ppd_price,0)*nvl(cm_crrate,1) price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') where ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and pp_kind='"+kind+"' and ppd_statuscode='VALID'  order by pp_indate desc) order by ppd_id desc) ", new String[]{"price","ppd_id"},
									" rownum<2"); //物料+类型----最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquirydetail", "id_preprice=" + preprice + ",id_freerate=(round(id_price,8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("D")){
					price = baseDao.getFieldsDataByCondition("(SELECT nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1) price,ppd_id "
							+ "FROM PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
							+ "left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') "
							+ "WHERE ppd_prodcode='"+rs.getString("id_prodcode")+"' and pp_kind='"+kind+"' and ppd_statuscode='VALID' and ppd_appstatus='合格'"
							+ "order by nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1))", new String[]{"price","ppd_id"},
									"rownum=1"); //物料+类型---有效且合格最低不含税价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquirydetail", "id_preprice=" + preprice + ",id_freerate=(round((id_price/(1+nvl(id_rate,0)/100)),8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("E")){
					price = baseDao.getFieldsDataByCondition("(select nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1) price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') where ppd_prodcode='" + rs.getString("id_prodcode")
									+ "' and pp_kind='"+kind+"' and ppd_statuscode='VALID' order by nvl(ppd_price,0)/(1+nvl(ppd_rate,0)/100)*nvl(cm_crrate,1))", new String[]{"price","ppd_id"},
									"rownum=1"); //物料+类型--有效最低不含税价格。
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("inquirydetail", "id_preprice=" + preprice + ",id_freerate=(round((id_price/(1+nvl(id_rate,0)/100)),8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("F")){
					price = baseDao.getFieldsDataByCondition("(select * from (select ppd_price,ppd_id,ppd_currency from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid "
							+ "where ppd_vendcode='" + rs.getString("id_vendcode") + "' and ppd_prodcode='" + rs.getString("id_prodcode")+"' "
							+ "and pp_kind='"+kind+"' and ppd_statuscode='VALID' "
							+ "order by pp_indate desc) order by ppd_id desc) ", new String[]{"ppd_price","ppd_id","ppd_currency"},
							"rownum<2");//物料+供应商+类型--最新有效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double Currencyrate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + price[2] + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						Double Rmbprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) * Currencyrate, 8);
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("id_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Rmbprice / fprate, 8);
							baseDao.updateByCondition("inquirydetail", "id_preprice=" + preprice + ",id_freerate=(round((id_price/(1+nvl(id_rate,0)/100)),8)-" + preprice + ")/" + preprice+",id_ppdid="+price[1]+"",
									"id_freerate=0 and id_id=" + rs.getInt("id_id"));
						}
					}
				}else if(data[i].equals("G")){
					price = baseDao.getFieldsDataByCondition("(select * from (select nvl(ppd_price,0)*nvl(cm_crrate,1) price,ppd_id from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid left join CurrencysMonth on cm_crname=ppd_currency and cm_yearmonth=to_char(pp_indate,'yyyymm') where ppd_prodcode='" + rs.getString("ppd_prodcode")
					+ "' and pp_kind='"+kind+"' and ppd_statuscode='UNVALID' and ppd_ppid<>"+rs.getInt("ppd_ppid")+"  order by ppd_unvaliddate desc) order by ppd_id desc) ", new String[]{"price","ppd_id"},
					" rownum<2"); //物料+类型----最新无效价格
					if (price!=null && price[0] != null && Double.parseDouble(price[0].toString()) != 0) { 
						Double fprate = baseDao.getFieldValue("(select nvl(cm_crrate,0)cm_crrate from Currencysmonth where cm_crname='" + rs.getString("ppd_currency") + "' order by cm_yearmonth desc)", "cm_crrate",
								"rownum=1", Double.class);
						if (fprate!=null && fprate != 0) {
							double preprice = NumberUtil.formatDouble(Double.parseDouble(price[0].toString()) / fprate, 8);
							baseDao.updateByCondition("PurchasePricedetail", "ppd_preprice=" + preprice + ",ppd_freerate=(round((ppd_price/(1+nvl(ppd_rate,0)/100)),8)-" + preprice + ")/" + preprice+"",
									"ppd_freerate=0 and ppd_id=" + rs.getInt("ppd_id"));
						}
					}
				}
			}
	    }
	    baseDao.updateByCondition("inquirydetail", "id_freerate=id_freerate*100", "id_inid=" + id);
	}

	@Override
	public void updateInfo(int id, String purpose, String remark, String caller) {
		baseDao.updateByCondition("Inquiry", "in_purpose='" + purpose + "', in_remark='" + remark + "'", "in_id=" + id);
		// 记录操作
		baseDao.logger.others("更新信息", "更新成功", caller, "in_id", id);
	}

	@Override
	public void nullifyInquiry(int in_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Inquiry", "in_checkstatuscode", "in_id=" + in_id);
		if (status != null && "COMMITED".equals(status)) {
			resSubmitInquiry(in_id, caller);
		}
		// 作废
		baseDao.updateByCondition("Inquiry", "in_status='" + BaseUtil.getLocalMessage("NULLIFIED") + "', in_statuscode='NULLIFIED'",
				"in_id=" + in_id);
		// 记录操作
		baseDao.logger.others(BaseUtil.getLocalMessage("msg.nullify"), BaseUtil.getLocalMessage("msg.nullifySuccess"), caller, "in_id",
				in_id);
	}

	@Override
	public JSONObject copyInquiry(int id, String caller) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> dif = new HashMap<String, Object>();
		int nId = baseDao.getSeqId("INQUIRY_SEQ");
		dif.put("in_id", nId);
		dif.put("in_date", "sysdate");
		dif.put("in_recorddate", "sysdate");
		String code = baseDao.sGetMaxNumber("Inquiry", 2);
		dif.put("in_code", "'" + code + "'");
		dif.put("in_recorderid", employee.getEm_id());
		dif.put("in_recorder", "'" + employee.getEm_name() + "'");
		dif.put("in_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("in_statuscode", "'ENTERING'");
		dif.put("in_auditor", "null");
		dif.put("in_auditdate", "null");
		dif.put("in_sendstatus", "null");
		dif.put("in_uploadstatus", "null");
		dif.put("in_confirmstatus", "null");
		dif.put("in_checksendstatus", "null");
		dif.put("in_turnstatus", "null");
		dif.put("in_enddate", "null");
		dif.put("in_sourceid", "0");
		dif.put("b2b_qu_id", "0");
		dif.put("in_taskid", "0");
		dif.put("in_checkstatus", "null");
		dif.put("in_checkstatuscode", "null");
		dif.put("IN_SOURCE", "null");
		baseDao.copyRecord("Inquiry", "Inquiry", "in_id=" + id, dif);
		// Copy InquiryDetail
		SqlRowList list = baseDao.queryForRowSet("SELECT id_id FROM InquiryDetail WHERE id_inid=?", id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("INQUIRYDETAIL_SEQ");
			dif.put("id_id", dId);
			dif.put("id_inid", nId);
			dif.put("id_isagreed", 0);
			dif.put("id_fromdate", "null");
			dif.put("id_todate", "null");
			dif.put("id_myfromdate", "sysdate");
			dif.put("id_qucode", "null");
			dif.put("id_qudetno", 0);
			dif.put("id_price", 0);
			dif.put("id_qty", 0);
			dif.put("id_minqty", 0);
			dif.put("id_minbuyqty", 0);
			dif.put("id_spare", 0);
			dif.put("id_leadtime", 0);
			dif.put("id_brand", "null");
			dif.put("id_vendorprodcode", "null");
			dif.put("id_status", "null");
			dif.put("id_code", "'" + code + "'");
			dif.put("id_freerate", 0);
			dif.put("id_brand", "null");
			dif.put("id_minqty", 0);
			dif.put("id_minbuyqty", 0);
			dif.put("id_leadtime", 0);
			baseDao.copyRecord("InquiryDetail", "InquiryDetail", "id_id=" + list.getInt("id_id"), dif);
			// Copy InquiryDetailDet
			ass = baseDao.queryForRowSet("SELECT idd_id FROM InquiryDetailDet WHERE idd_idid=?", list.getInt("id_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("idd_id", baseDao.getSeqId("INQUIRYDETAILDET_SEQ"));
				dif.put("idd_idid", dId);
				dif.put("idd_price", 0);
				baseDao.copyRecord("InquiryDetailDet", "InquiryDetailDet", "idd_id=" + ass.getInt("idd_id"), dif);
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("id", nId);
		obj.put("code", code);
		return obj;
	}
	/*
	 * 询价单新的判定方法
	 */
	@Override
	public void agreeInquiryPrice(int id, String param) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		int sign = -1;
		for(Map<Object, Object>map:grid){
			if(StringUtil.hasText(map.get("id_isagreed")) && "false".equals(map.get("id_isagreed").toString())){
				sign = 0;
			}
			baseDao.execute("update inquirydetail set id_isagreed="+sign+" where id_id="+map.get("id_id"));
		}
		baseDao.execute("update inquirydetail set id_isagreed=0 where id_inid="+id+" and id_isagreed=1");
		baseDao.execute("update inquiry set in_pdstatus='已判定' where in_id="+id);
	}
	
	//询价入口按全部物料
	@Override
	public List<Map<String, Object>> getAllPurc(String caller,String condition) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Employee employee = SystemSession.getUser();
		if(!StringUtil.hasText(condition)){
			condition = "pr_buyercode='"+employee.getEm_code()+"'";
		}
		SqlRowList rs = baseDao.queryForRowSet("select pr_code,pr_detail,pr_spec,pr_orispeccode,pr_brand,pr_unit from product where "+condition+" and rownum<=200");
		int i = 1;
		while(rs.next()){
			Map<String, Object>map = new HashMap<String,Object>();
			map.put("ip_detno", i++);
			map.put("ip_prodcode", rs.getObject("pr_code"));
			map.put("ip_prodname", rs.getObject("pr_detail"));
			map.put("ip_spec", rs.getObject("pr_spec"));
			map.put("ip_orispeccode", rs.getObject("pr_orispeccode"));
			map.put("ip_brand", rs.getObject("pr_brand"));
			map.put("ip_unit", rs.getObject("pr_unit"));
			datas.add(map);
		}
		return datas;
	}
	
	//询价入口按BOM
	@Override
	public List<Map<String, Object>> getBom(String caller,String param) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		int i = 1;
		for(Map<Object, Object>maps : grid){
			if("0".equals(maps.get("sign"))){
				SqlRowList rs = baseDao.queryForRowSet("select pr_code,pr_detail,pr_spec,pr_orispeccode,pr_brand,pr_unit from product left join BOMDetail on bd_soncode=pr_code where pr_supplytype<>'VIRTUAL' and bd_bomid="+maps.get("bo_id")+"");
				while(rs.next()){
					Map<String, Object>map = new HashMap<String,Object>();
					map.put("ip_detno", i++);
					map.put("ip_prodcode", rs.getObject("pr_code"));
					map.put("ip_prodname", rs.getObject("pr_detail"));
					map.put("ip_spec", rs.getObject("pr_spec"));
					map.put("ip_orispeccode", rs.getObject("pr_orispeccode"));
					map.put("ip_brand", rs.getObject("pr_brand"));
					map.put("ip_unit", rs.getObject("pr_unit"));
					datas.add(map);
				}
			}else{
				SqlRowList rs = baseDao.queryForRowSet("select code,detail,spec,orispeccode,brand,unit from (select pr_code code,pr_detail detail,pr_spec spec,pr_orispeccode orispeccode,pr_brand brand,pr_unit unit from product)  left join MA_BOMSTRUCT_VIEW on bs_soncode=code where bs_topbomid="+maps.get("bo_id")+"");
				while(rs.next()){
					Map<String, Object>map = new HashMap<String,Object>();
					map.put("ip_detno", i++);
					map.put("ip_prodcode", rs.getObject("code"));
					map.put("ip_prodname", rs.getObject("detail"));
					map.put("ip_spec", rs.getObject("spec"));
					map.put("ip_orispeccode", rs.getObject("orispeccode"));
					map.put("ip_brand", rs.getObject("brand"));
					map.put("ip_unit", rs.getObject("unit"));
					datas.add(map);
				}
			}
		}
		return datas;
	}
	
	/**
	 * 询价单入口：发起询价
	 */
	@Override
	public String startIQ(String caller, String formStore, String param1 , String param2, String param3){
		String log = "";
		int detno = 1;
		Employee employee = SystemSession.getUser();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		int bi_id = 0;
		String bi_code = "";
		String cycle = "";
		int in_id = 0;
		String in_code = "";
		int month = Integer.parseInt(store.get("dt").toString());
		int times = 12%month==0 ? 12/month : 12/month+1;
		if("月".equals(store.get("ms"))){
			cycle = getAutoCycle(store.get("jt").toString(),times,month);
		}
		if(StringUtil.hasText(store.get("fw")) && "外部询价".equals(store.get("fw"))){  //外部询价  生成公共询价单
			if("所有物料".equals(store.get("dx"))){
				SqlRowList rs = baseDao.queryForRowSet("select pr_code,pr_detail,pr_spec,pr_orispeccode,pr_brand,pr_unit from product where pr_buyercode='"+employee.getEm_code()+"'");
				while(rs.next()){
					Object[] buyercode = baseDao.getFieldsDataByCondition("Product", new String[]{"pr_buyercode","pr_detail","pr_spec","pr_unit","pr_orispeccode","pr_brand"}, "pr_code='"+rs.getObject("pr_code")+"'");
					if(detno==1 || detno>200){
						bi_id = baseDao.getSeqId("BATCHINQUIRY_SEQ");
						bi_code = baseDao.sGetMaxNumber("Batchinquiry", 2);
						baseDao.execute("Insert into batchinquiry (BI_ID,BI_CODE,BI_DATE,BI_KIND,BI_STATUS,BI_STATUSCODE,BI_PRICEKIND,BI_RECORDER,BI_RECORDDATE,BI_ENDDATE,BI_REMARK,BI_PRICETYPE,BI_SENDSTATUS,bi_attach) values "
								+ "("+bi_id+",'"+bi_code+"',sysdate,'公开询价','已审核','AUDITED','"+store.get("lx")+"','"+employee.getEm_name()+"',sysdate,(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("bz")+"','"+store.get("zl")+"','待上传','"+store.get("fj")+"')");
						detno = 1;
					}
					Object[] buyer = baseDao.getFieldsDataByCondition("Employee", new String[]{"em_mobile","em_uu","em_name","em_code"}, "em_code='"+buyercode[0]+"'");
					if(StringUtil.hasText(buyer)){
						baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,rs.getObject("pr_code"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
					}else{
						buyer = baseDao.getFieldsDataByCondition("Enterprise left join Employee on en_adminuu=em_uu", new String[]{"em_mobile","em_uu","em_name","em_code"}, "1=1");
						baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,rs.getObject("pr_code"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
					}
					if("天".equals(store.get("ms"))){
						baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
					}else if("月".equals(store.get("ms"))){
						baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "BatchInquiry", "bi_id", bi_id);
				log = "转入成功,公共询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/batchInquiry.jsp?formCondition=bi_idIS" + bi_id
						+ "&gridCondition=bip_biidIS" + bi_id + "')\">" + bi_code + "</a>&nbsp;";
			}else if("按物料种类".equals(store.get("dx"))){
				for(Map<Object, Object>kind : grid3){
					SqlRowList rs = baseDao.queryForRowSet("select pr_code from product where pr_pkid="+kind.get("ik_pkid"));
					while(rs.next()){
						if(detno==1 || detno>200){
							bi_id = baseDao.getSeqId("BATCHINQUIRY_SEQ");
							bi_code = baseDao.sGetMaxNumber("Batchinquiry", 2);
							baseDao.execute("Insert into batchinquiry (BI_ID,BI_CODE,BI_DATE,BI_KIND,BI_STATUS,BI_STATUSCODE,BI_PRICEKIND,BI_RECORDER,BI_RECORDDATE,BI_ENDDATE,BI_REMARK,BI_PRICETYPE,BI_SENDSTATUS,bi_attach) values "
									+ "("+bi_id+",'"+bi_code+"',sysdate,'公开询价','已审核','AUDITED','"+store.get("lx")+"','"+employee.getEm_name()+"',sysdate,(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("bz")+"','"+store.get("zl")+"','待上传','"+store.get("fj")+"')");
							detno = 1;
						}
						Object[] buyercode = baseDao.getFieldsDataByCondition("Product", new String[]{"pr_buyercode","pr_detail","pr_spec","pr_unit","pr_orispeccode","pr_brand"}, "pr_code='"+rs.getObject("pr_code")+"'");
						Object[] buyer = baseDao.getFieldsDataByCondition("Employee", new String[]{"em_mobile","em_uu","em_name","em_code"}, "em_code='"+buyercode[0]+"'");
						if(StringUtil.hasText(buyer)){
							baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,rs.getObject("pr_code"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
						}else{
							buyer = baseDao.getFieldsDataByCondition("Enterprise left join Employee on en_adminuu=em_uu", new String[]{"em_mobile","em_uu","em_name","em_code"}, "1=1");
							baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,rs.getObject("pr_code"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "BatchInquiry", "bi_id", bi_id);
				log = "转入成功,公共询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/batchInquiry.jsp?formCondition=bi_idIS" + bi_id
						+ "&gridCondition=bip_biidIS" + bi_id + "')\">" + bi_code + "</a>&nbsp;";
			}else{
				for(Map<Object, Object>prod : grid1){
					if(StringUtil.hasText(prod.get("ip_prodcode"))){
						Object[] buyercode = baseDao.getFieldsDataByCondition("Product", new String[]{"pr_buyercode","pr_detail","pr_spec","pr_unit","pr_orispeccode","pr_brand"}, "pr_code='"+prod.get("ip_prodcode")+"'");
						if(detno == 1 || detno>200){
							bi_id = baseDao.getSeqId("BATCHINQUIRY_SEQ");
							bi_code = baseDao.sGetMaxNumber("Batchinquiry", 2);
							baseDao.execute("Insert into batchinquiry (BI_ID,BI_CODE,BI_DATE,BI_KIND,BI_STATUS,BI_STATUSCODE,BI_PRICEKIND,BI_RECORDER,BI_RECORDDATE,BI_ENDDATE,BI_REMARK,BI_PRICETYPE,BI_SENDSTATUS,bi_attach) values "
									+ "("+bi_id+",'"+bi_code+"',sysdate,'公开询价','已审核','AUDITED','"+store.get("lx")+"','"+employee.getEm_name()+"',sysdate,(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("bz")+"','"+store.get("zl")+"','待上传','"+store.get("fj")+"')");
							detno = 1;
						}
						Object[] buyer = baseDao.getFieldsDataByCondition("Employee", new String[]{"em_mobile","em_uu","em_name","em_code"}, "em_code='"+buyercode[0]+"'");
						if(StringUtil.hasText(buyer)){
							baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,prod.get("ip_prodcode"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
						}else{
							buyer = baseDao.getFieldsDataByCondition("Enterprise left join Employee on en_adminuu=em_uu", new String[]{"em_mobile","em_uu","em_name","em_code"}, "1=1");
							baseDao.execute(startAutoInquiryDetail,new Object[]{bi_id,detno++,prod.get("ip_prodcode"),buyer[3],buyer[2],buyer[1],buyer[0],buyercode[2],buyercode[1],buyercode[4],buyercode[5],buyercode[3]});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "BatchInquiry", "bi_id", bi_id);
				log = "转入成功,公共询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/batchInquiry.jsp?formCondition=bi_idIS" + bi_id
						+ "&gridCondition=bip_biidIS" + bi_id + "')\">" + bi_code + "</a>&nbsp;";
			}
		}else if(StringUtil.hasText(store.get("fw")) && "内部询价".equals(store.get("fw"))){ //内部询价生成采购询价单
			if("所有物料".equals(store.get("dx"))){
				SqlRowList rs = baseDao.queryForRowSet("select pr_code,pr_detail,pr_spec,pr_orispeccode,pr_brand,pr_unit from product where pr_buyercode='"+employee.getEm_code()+"'");
				while(rs.next()){
					SqlRowList vend = baseDao.queryForRowSet("select * from (select distinct prodcode,ve_code,ve_name,ve_uu,ve_currency,ve_taxrate from (select pd_vendcode vendcode,pd_prodcode prodcode from PURCHASE LEFT JOIN PURCHASEDETAIL ON PD_PUID=PU_ID WHERE PU_STATUSCODE='AUDITED' UNION all SELECT PPD_VENDCODE vendcode ,ppd_prodcode prodcode FROM PURCHASEPRICEdetail WHERE ppd_statuscode='VALID' and PPD_VENDCODE is not null) left join vendor on ve_code=vendcode where ve_code is not null and ve_uu is not null) where prodcode='"+rs.getObject("pr_code")+"'");
					while(vend.next()){
						if(detno ==1 || detno>200){
							in_id = baseDao.getSeqId("INQUIRY_SEQ");
							in_code = baseDao.sGetMaxNumber("Inquiry", 2);
							baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
							detno = 1;
						}
						Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.getObject("ve_currency")+"'");
						baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,rs.getObject("pr_id"),rs.getObject("pr_code"),vend.getObject("ve_currency"),rate,vend.getObject("ve_code"),vend.getObject("ve_code"),vend.getObject("ve_uu"),in_code});
					}
					if("天".equals(store.get("ms"))){
						baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
					}else if("月".equals(store.get("ms"))){
						baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}else if("按物料种类".equals(store.get("dx"))){
				for(Map<Object, Object>kind : grid3){
					SqlRowList rs = baseDao.queryForRowSet("select pr_code from product where pr_pkid="+kind.get("ik_pkid"));
					while(rs.next()){
						SqlRowList vend = baseDao.queryForRowSet("select * from (select distinct prodcode,ve_code,ve_name,ve_uu,ve_currency,ve_taxrate from (select pd_vendcode vendcode,pd_prodcode prodcode from PURCHASE LEFT JOIN PURCHASEDETAIL ON PD_PUID=PU_ID WHERE PU_STATUSCODE='AUDITED' UNION all SELECT PPD_VENDCODE vendcode ,ppd_prodcode prodcode FROM PURCHASEPRICEdetail WHERE ppd_statuscode='VALID' and PPD_VENDCODE is not null) left join vendor on ve_code=vendcode where ve_code is not null and ve_uu is not null) where prodcode='"+rs.getObject("pr_code")+"'");
						while(vend.next()){
							if(detno==1 || detno>200){
								in_id = baseDao.getSeqId("INQUIRY_SEQ");
								in_code = baseDao.sGetMaxNumber("Inquiry", 2);
								baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
								detno = 1;
							}
							Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.getObject("ve_currency")+"'");
							baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,rs.getObject("pr_id"),rs.getObject("pr_code"),vend.getObject("ve_currency"),rate,vend.getObject("ve_code"),vend.getObject("ve_code"),vend.getObject("ve_uu"),in_code});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}else{
				for(Map<Object, Object>prod : grid1){
					if(StringUtil.hasText(prod.get("ip_prodcode"))){
						SqlRowList vend = baseDao.queryForRowSet("select * from (select distinct prodcode,ve_code,ve_name,ve_uu,ve_currency,ve_taxrate from (select pd_vendcode vendcode,pd_prodcode prodcode from PURCHASE LEFT JOIN PURCHASEDETAIL ON PD_PUID=PU_ID WHERE PU_STATUSCODE='AUDITED' UNION all SELECT PPD_VENDCODE vendcode ,ppd_prodcode prodcode FROM PURCHASEPRICEdetail WHERE ppd_statuscode='VALID' and PPD_VENDCODE is not null) left join vendor on ve_code=vendcode where ve_code is not null and ve_uu is not null) where prodcode='"+prod.get("ip_prodcode")+"'");
						while(vend.next()){
							if(detno==1 || detno>200){
								in_id = baseDao.getSeqId("INQUIRY_SEQ");
								in_code = baseDao.sGetMaxNumber("Inquiry", 2);
								baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
								detno = 1;
							}
							Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.getObject("ve_currency")+"'");
							baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,prod.get("ip_prid"),prod.get("ip_prodcode"),vend.getObject("ve_currency"),rate,vend.getObject("ve_code"),vend.getObject("ve_code"),vend.getObject("ve_uu"),in_code});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}
		}else if(StringUtil.hasText(store.get("fw")) && "指定供应商".equals(store.get("fw"))){ //指定供应商，生成采购询价单
			if("所有物料".equals(store.get("dx"))){
				SqlRowList rs = baseDao.queryForRowSet("select pr_id,pr_code,pr_detail,pr_spec,pr_orispeccode,pr_brand,pr_unit from product where pr_buyercode='"+employee.getEm_code()+"'");
				while(rs.next()){
					for(Map<Object, Object>vend:grid2){
						if(detno ==1 || detno>200){
							in_id = baseDao.getSeqId("INQUIRY_SEQ");
							in_code = baseDao.sGetMaxNumber("Inquiry", 2);
							baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
							detno = 1;
						}
						Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.get("iv_currency")+"'");
						baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,rs.getObject("pr_id"),rs.getObject("pr_code"),vend.get("iv_currency"),rate,vend.get("iv_vendcode"),vend.get("iv_vendname"),vend.get("iv_venduu"),in_code});
					}
					if("天".equals(store.get("ms"))){
						baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
					}else if("月".equals(store.get("ms"))){
						baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}else if("按物料种类".equals(store.get("dx"))){
				for(Map<Object, Object>kind : grid3){
					SqlRowList rs = baseDao.queryForRowSet("select pr_code from product where pr_pkid="+kind.get("ik_pkid"));
					while(rs.next()){
						for(Map<Object, Object>vend:grid2){
							if(detno==1 || detno>200){
								in_id = baseDao.getSeqId("INQUIRY_SEQ");
								in_code = baseDao.sGetMaxNumber("Inquiry", 2);
								baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
								detno = 1;
							}
							Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.get("iv_currency")+"'");
							baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,rs.getObject("pr_id"),rs.getObject("pr_code"),vend.get("iv_currency"),rate,vend.get("iv_vendcode"),vend.get("iv_vendname"),vend.get("iv_venduu"),in_code});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+rs.getObject("pr_code")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+rs.getObject("pr_code")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}else{
				for(Map<Object, Object>prod:grid1){
					if(StringUtil.hasText(prod.get("ip_prodcode"))){
						for(Map<Object, Object>vend:grid2){
							if(detno==1 || detno>200){
								in_id = baseDao.getSeqId("INQUIRY_SEQ");
								in_code = baseDao.sGetMaxNumber("Inquiry", 2);
								baseDao.execute("Insert into inquiry (IN_ID,IN_CODE,IN_DATE,IN_DELIVERY,IN_STATUS,IN_RECORDERID,IN_RECORDDATE,IN_STATUSCODE,IN_APPLDATE,IN_RECORDER,IN_ENDDATE,IN_KIND,IN_PRICETYPE,in_remark,in_attach,in_sendstatus) values ("+in_id+",'"+in_code+"',sysdate,sysdate,'已审核',"+employee.getEm_id()+",sysdate,'AUDITED',sysdate,'"+employee.getEm_name()+"',(TRUNC(sysdate+"+store.get("rq")+")+1-1/86400),'"+store.get("lx")+"','"+store.get("zl")+"','"+store.get("bz")+"','"+store.get("fj")+"','待上传')");
								detno = 1;
							}
							Object rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+vend.get("iv_currency")+"'");
							baseDao.execute(startInquiryDetail,new Object[]{in_id,detno++,prod.get("ip_prid"),prod.get("ip_prodcode"),vend.get("iv_currency"),rate,vend.get("iv_vendcode"),vend.get("iv_vendname"),vend.get("iv_venduu"),in_code});
						}
						if("天".equals(store.get("ms"))){
							baseDao.execute("update product set pr_defaultused=-1,pr_lastinquirydate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd')-"+store.get("dt")+",pr_jtcycle=null,pr_jtinquirydate=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}else if("月".equals(store.get("ms"))){
							baseDao.execute("update product set pr_jtnextdate=to_date('"+store.get("jt").toString()+"','yyyy-mm-dd'),pr_defaultused=-1,pr_jtcycle="+times+",pr_jtinquirydate='"+cycle+"',pr_autoinquirydays=null where pr_code='"+prod.get("ip_prodcode")+"'");
						}
					}
				}
				baseDao.logger.others("询价入口转入", "转入成功", "Inquiry", "in_id", in_id);
				log = "转入成功,采购询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
						+ "&gridCondition=id_inidIS" + in_id + "')\">" + in_code + "</a>&nbsp;";
			}
		}
		return log;
	}
	
	//设置为月时,自动计算静态询价周期和日期。
	public String getAutoCycle(String date,int times,int add){
		String day = date.substring(7,10);
		String month = date.substring(5,7);
		String[] arr = new String[times];
		arr[0] = date.substring(5,10);
		int m = Integer.parseInt(month);
		for(int i=1 ; i<times ; i++){
			m = m+add>12? m+add-12 : m+add;
			String mon = m >= 10 ? m+"" : "0"+m+"";
			String cycle = mon + day;
			arr[i]=cycle;
		}
		return StringUtils.join(arr,";");	
	}
}

