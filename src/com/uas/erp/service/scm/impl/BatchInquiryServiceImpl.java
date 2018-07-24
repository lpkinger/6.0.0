package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.BatchInquiryService;
import com.uas.erp.service.scm.InquiryService;

@Service
public class BatchInquiryServiceImpl implements BatchInquiryService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private InquiryService inquiryService;	

	@Override
	public void saveBatchInquiry(String formStore, String param1,String param2,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> vendgrid = BaseUtil.parseGridStoreToMaps(param2);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> prodmap : prodgrid) {
			int prodid = baseDao.getSeqId("BatchInProd_SEQ");
			prodmap.put("bip_id", prodid);
			// 保存更新时自动更新明细行采购员的信息,方便平台方发出讯息,由于企业信息中没有code,物料没有采购员uu号,需要取两次
			Object buyercode = baseDao.getFieldDataByCondition("Product", "pr_buyercode", "pr_code='"+prodmap.get("bip_prodcode")+"'");
			if(StringUtil.hasText(buyercode)){
				SqlRowList buyer = baseDao.queryForRowSet("select em_mobile,em_uu,em_name from employee where em_code='"+buyercode+"'");
				if(buyer.next()){
					prodmap.put("bip_buyercode", buyercode);
					prodmap.put("bip_buyername", buyer.getObject("em_name"));
					prodmap.put("bip_buyeruu", buyer.getObject("em_uu"));
					prodmap.put("bip_mobile", buyer.getObject("em_mobile"));
				}
			}else{
				Object[] buyer = baseDao.getFieldsDataByCondition("Enterprise left join Employee on en_adminuu=em_uu", new String[]{"em_mobile","em_uu","em_name","em_code"}, "1=1");
				prodmap.put("bip_buyercode", buyer[3]);
				prodmap.put("bip_buyername", buyer[2]);
				prodmap.put("bip_buyeruu", buyer[1]);
				prodmap.put("bip_mobile", buyer[0]);
			}
		}
		for (Map<Object, Object> vendmap : vendgrid) {
			int vendid = baseDao.getSeqId("BatchInVendor_SEQ");
			vendmap.put("biv_id", vendid);
		}
		sqls.add(SqlUtil.getInsertSqlByMap(store, "BatchInquiry"));
		// 保存InquiryDetail
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(prodgrid, "BatchInProd"));
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(vendgrid, "BatchInVendor"));
		baseDao.execute(sqls);	
		baseDao.logger.save(caller, "bi_id", store.get("bi_id"));
		// 判断配置的最大报价截止间隔,填写的日期有误自动更新成最大截止日期
		String log = "";
		String maxDateInquiry = baseDao.getDBSetting("BatchInquiry", "maxDateInquiry");
		SqlRowList rs = baseDao.queryForRowSet("select * from BatchInquiry where (bi_enddate<bi_date or bi_enddate>(bi_date+"+maxDateInquiry+")) and bi_id="+store.get("bi_id"));
		if (maxDateInquiry != null && !"0".equals(maxDateInquiry) && "公开询价".equals(store.get("bi_kind"))) {
			if(rs.next()){
				baseDao.execute("update BatchInquiry set bi_enddate=bi_date+"+maxDateInquiry+" where bi_id="+store.get("bi_id"));
				log = "您填写的报价截止日期有误,已自动更新为日期+设置的最大间隔天数";
			}
		}
		String code = baseDao.sGetMaxNumber("Inquiry", 2);
		baseDao.execute("update BatchInquiry a set bi_sendstatus='待上传',bi_recorduu=(select em_uu from employee where em_id=a.bi_recordid),bi_code='"+code+"' where bi_id="+store.get("bi_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
		BaseUtil.showErrorOnSuccess(log);
	}

	@Override
	public void updateBatchInquiryById(String formStore, String param1,String param2, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> vendgrid = BaseUtil.parseGridStoreToMaps(param2);
		List<String> sqls = new ArrayList<String>();
		handlerService.handler(caller, "save", "before", new Object[]{store});
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "BatchInquiry", "bi_id"));
		if (prodgrid.size() > 0) {
			for (Map<Object, Object> s : prodgrid) {
				if (s.get("bip_id") == null || s.get("bip_id").equals("") || s.get("bip_id").equals("0")
						|| Integer.parseInt(s.get("bip_id").toString()) <= 0) {
					int id = baseDao.getSeqId("BatchInProd_SEQ");
					s.put("bip_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "BatchInProd", new String[] { "bip_id" }, new Object[] { id });
					sqls.add(sql);
				}
				s.remove("bip_buyercode");
				s.remove("bip_buyername");
				s.remove("bip_buyeruu");
				s.remove("bip_mobile");
				// 保存更新时自动更新明细行采购员的信息,方便平台方发出讯息,由于企业信息中没有code,物料没有采购员uu号,需要取两次
				Object buyercode = baseDao.getFieldDataByCondition("Product", "pr_buyercode", "pr_code='"+s.get("bip_prodcode")+"'");
				if(StringUtil.hasText(buyercode)){
					SqlRowList buyer = baseDao.queryForRowSet("select em_mobile,em_uu,em_name from employee where em_code='"+buyercode+"'");
					if(buyer.next()){
						s.put("bip_buyercode", buyercode);
						s.put("bip_buyername", buyer.getObject("em_name"));
						s.put("bip_buyeruu", buyer.getObject("em_uu"));
						s.put("bip_mobile", buyer.getObject("em_mobile"));
					}
				}else{
					Object[] buyer = baseDao.getFieldsDataByCondition("Enterprise left join Employee on en_adminuu=em_uu", new String[]{"em_mobile","em_uu","em_name","em_code"}, "1=1");
					s.put("bip_buyercode", buyer[3]);
					s.put("bip_buyername", buyer[2]);
					s.put("bip_buyeruu", buyer[1]);
					s.put("bip_mobile", buyer[0]);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(prodgrid, "BatchInProd", "bip_id"));
		}
		if (vendgrid.size() > 0) {
			for (Map<Object, Object> s : vendgrid) {
				if (s.get("biv_id") == null || s.get("biv_id").equals("") || s.get("biv_id").equals("0")
						|| Integer.parseInt(s.get("biv_id").toString()) <= 0) {
					int id = baseDao.getSeqId("BatchInVendor_SEQ");
					s.put("biv_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "BatchInVendor", new String[] { "biv_id" }, new Object[] { id });
					sqls.add(sql);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(vendgrid, "BatchInVendor", "biv_id"));
		}
		baseDao.execute(sqls);
		// 判断配置的最大报价截止间隔,填写的日期有误自动更新成最大截止日期
		String maxDateInquiry = baseDao.getDBSetting("BatchInquiry", "maxDateInquiry");
		SqlRowList rs = baseDao.queryForRowSet("select * from BatchInquiry where (bi_enddate<bi_date or bi_enddate>(bi_date+"+maxDateInquiry+")) and bi_id="+store.get("bi_id"));
		if (maxDateInquiry != null && !"0".equals(maxDateInquiry) && "公开询价".equals(store.get("bi_kind"))) {
			if(rs.next()){
				baseDao.execute("update BatchInquiry set bi_enddate=bi_date+"+maxDateInquiry+" where bi_id="+store.get("bi_id"));
				BaseUtil.showErrorOnSuccess("您填写的报价截止日期有误,已自动更新为日期+设置的最大间隔天数");
			}
		}
		//记录操作
		baseDao.logger.update(caller, "bi_id", store.get("bi_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteBatchInquiry(int bi_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{bi_id});
		//删除主表内容
		baseDao.deleteById("BatchInquiry", "bi_id", bi_id);
		baseDao.logger.delete(caller, "bi_id", bi_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{bi_id});
	}

	@Override
	public String auditBatchInquiry(int bi_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("BatchInquiry", "bi_statuscode", "bi_id=" + bi_id);
		Object code = baseDao.getFieldDataByCondition("BatchInquiry", "bi_code", "bi_id="+bi_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit("BatchInquiry", bi_id);
		String log = "";
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{bi_id});
		Object bi_kind = baseDao.getFieldDataByCondition("batchinquiry","bi_kind", "bi_id="+bi_id);
		//插入采购询价单
		if("批量询价".equals(bi_kind)){
			SqlRowList proddetail = baseDao.queryForRowSet("select * from batchinprod where bip_biid="+bi_id);
			List<Map<Object, Object>> Detail = new ArrayList<Map<Object,Object>>();
			List<String> sqls = new ArrayList<String>();
			int in_id = baseDao.getSeqId("INQUIRY_SEQ");
			int a = 0;
			//供应商UU存在空的情况不允许提交
			SqlRowList rs = baseDao.queryForRowSet("select biv_detno from batchinvendor where biv_biid="+bi_id+" and biv_venduu is null");
			if(rs.next()){
				BaseUtil.showError("所选择供应商中存在UU号为空的供应商，不允许审核，行号:"+rs.getString("biv_detno")+"");
			}
			baseDao.execute("insert into inquiry(in_id,in_code,in_date,in_kind,in_status,in_statuscode,in_environment,in_purpose,in_recorder,in_recorddate,"
					+ "in_auditor,in_auditdate,in_enddate,in_checkstatus,in_checkstatuscode,in_cop,in_remark,in_attach,in_prodtype,in_pricetype)  select "+in_id+",'"+code+"',"
							+ "bi_date,bi_pricekind,bi_status,bi_statuscode,bi_environment,bi_purpose,bi_recorder,bi_recorddate,bi_auditor,bi_auditdate,"
							+ "bi_enddate,bi_checkstatus,bi_checkstatuscode,bi_cop,bi_remark,bi_attach,bi_kind,bi_pricetype from batchinquiry where bi_id="+bi_id+"");
			while(proddetail.next()){
				SqlRowList venddetail = baseDao.queryForRowSet("select * from batchinvendor where biv_biid="+bi_id);
				while(venddetail.next()){
					Map<Object, Object> detailmap = new HashMap<Object, Object>();
					int id_id = baseDao.getSeqId("InquiryDetail_SEQ");
					detailmap.put("id_id", id_id);
					detailmap.put("id_inid", in_id);
					detailmap.put("id_detno", a+venddetail.getInt("biv_detno"));
					detailmap.put("id_isagreed", 0);
					detailmap.put("id_prodcode", proddetail.getObject("bip_prodcode"));
					detailmap.put("id_vendcode", venddetail.getObject("biv_vendcode"));
					detailmap.put("id_vendname", venddetail.getObject("biv_vendname"));
					detailmap.put("id_venduu", venddetail.getObject("biv_venduu"));
					detailmap.put("id_currency", venddetail.getObject("biv_currency"));
					detailmap.put("id_remark", proddetail.getObject("bip_remark"));
					Detail.add(detailmap);
				}
				a = a+venddetail.size();
			}
			sqls.addAll(SqlUtil.getInsertSqlbyGridStore(Detail, "InquiryDetail"));
			baseDao.execute(sqls);
			inquiryService.auditInquiry(in_id, "Inquiry");
			log = "审核成功,已生成采购询价单,询价单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS" + in_id
					+ "&gridCondition=id_inidIS" + in_id + "')\">" + code + "</a>&nbsp;";
		}
		// 询价类型为公开询价，更新明细物料的最近询价日期   maz   17-09-25
		if("公开询价".equals(bi_kind)){
			baseDao.execute("update product set pr_lastinquirydate=sysdate where pr_code in (select bip_prodcode from batchinprod where bip_biid="+bi_id+")");
		}
		//执行审核操作
		baseDao.audit("BatchInquiry", "bi_id=" + bi_id, "bi_status", "bi_statuscode", "bi_auditdate", "bi_auditor");
		//记录操作
		baseDao.logger.audit(caller, "bi_id", bi_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{bi_id});
		return log;
	}
	@Override
	public void submitBatchInquiry(int bi_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BatchInquiry", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update BatchInquiry set bi_enddate=(TRUNC(bi_enddate)+1-1/86400) where bi_id="+bi_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { bi_id });
		// 执行提交操作
		baseDao.submit("BatchInquiry", "bi_id=" + bi_id, "bi_status", "bi_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "bi_id", bi_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { bi_id });
	}
	@Override
	public void resSubmitBatchInquiry(int bi_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BatchInquiry", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { bi_id });
		// 执行反提交操作
		baseDao.resOperate("BatchInquiry", "bi_id=" + bi_id, "bi_status", "bi_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "bi_id", bi_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { bi_id });
	}
	@Override
	public void resAuditBatchInquiry(int bi_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BatchInquiry", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("BatchInquiry", "bi_id=" + bi_id, "bi_status", "bi_statuscode", "bi_auditdate", "bi_auditor");
		baseDao.resOperate("BatchInquiry", "bi_id=" + bi_id, "bi_status", "bi_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "bi_id", bi_id);
	}
}
