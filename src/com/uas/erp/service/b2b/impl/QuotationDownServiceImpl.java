package com.uas.erp.service.b2b.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.uas.b2b.model.PublicRelay;
import com.uas.b2b.model.QuotationDetail;
import com.uas.b2b.model.QuotationDownReply;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.service.b2b.QuotationDownService;

@Service("quotationDownService")
public class QuotationDownServiceImpl implements QuotationDownService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void updateQuotationDown(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (store.get("qu_todate") == null || store.get("qu_todate").toString().trim().equals("")) {
			store.put("qu_todate", store.get("qu_custtodate"));
		}
		if (store.get("qu_fromdate") == null || store.get("qu_fromdate").toString().trim().equals("")) {
			store.put("qu_fromdate", store.get("qu_custfromdate"));
		}
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		Object quid = store.get("qu_id");
		baseDao.execute("update QuotationDown set (qu_custcode,qu_custname)=(select cu_code,cu_name from customer where cu_uu=qu_custuu and nvl(cu_auditstatuscode,' ')<>'DISABLE')"
				+ " where nvl(qu_custcode,' ')=' ' and qu_id="+ quid);
		Object qu_source = store.get("qu_source");
		if(StringUtil.hasText(qu_source) && !qu_source.equals("平台商机")){
			Object uu = baseDao.getFieldDataByCondition("QuotationDown", "qu_custuu", "qu_id=" + quid);
			if (uu != null) {
				if (!baseDao.checkIf("customer", "cu_uu='" + uu + "'")) {
					BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
				}
			}
			Object cucode = store.get("qu_custcode");
			if (!StringUtil.hasText(cucode)) {
				BaseUtil.showError("请维护客户UU号为[" + uu + "]的客户资料!");
			}
			Object custprodcode = store.get("qu_custprodcode");
			Object prodcode = store.get("qu_prodcode");
			String sql = null;
			if (StringUtil.hasText(custprodcode)) {
				if (StringUtil.hasText(prodcode)) {
					/*if (baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_prodcode='" + prodcode + "'")) {
						BaseUtil.showError("物料[" + prodcode + "]已经有对应的客户产品料号，请更改或删除原来数据！");
					}*/
					if (!baseDao.checkIf("productcustomer", "pc_custcode='" + cucode + "' and pc_custprodcode='" + custprodcode
							+ "' and pc_prodcode='" + prodcode + "'")) {
						Object i = baseDao.getFieldDataByCondition("productcustomer", "max(nvl(pc_detno,0))", "PC_CUSTCODE='" + cucode + "'");
						i = i == null ? 0 : i;
						sql = "Insert into productcustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
								+ "PC_CUSTPRODDETAIL,PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) "
								+ " select ProductCustomer_seq.nextval, cu_id," + (Integer.parseInt(i.toString()) + 1)
								+ ",pr_id,qu_custprodcode,qu_custproddetail,qu_custprodspec,pr_unit,qu_custcode,qu_custname,"
								+ "qu_prodcode from QuotationDown,customer,product where qu_custcode=cu_code and qu_prodcode=pr_code"
								+ " AND qu_id=" + quid;
					}
				} else {
					Object prcode = baseDao.getFieldDataByCondition("productcustomer", "pc_prodcode", "pc_custcode='" + cucode
							+ "' and pc_custprodcode='" + custprodcode + "'");
					if (StringUtil.hasText(prcode)) {
						store.put("qu_prodcode", prcode);
					} else {
						BaseUtil.showError("没有客户物料对照资料，请手工填写物料编号！");
					}
				}
			}
			// 修改AskLeave
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QuotationDown", "qu_id");
			baseDao.execute(formSql);
			if (sql != null) {
				baseDao.execute(sql);
			}
			baseDao.execute("update QuotationDown set qu_prodcode=(select max(pc_prodcode) from productcustomer where pc_custcode='" + cucode
					+ "' and pc_custprodcode=qu_custprodcode) where nvl(qu_prodcode,' ')=' ' and nvl(qu_custprodcode,' ')<>' ' "
					+ "and qu_custcode='" + cucode + "'");
		}else{
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QuotationDown", "qu_id");
			baseDao.execute(formSql);
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "QuotationDownDetail", "qd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("qd_id") == null || s.get("qd_id").equals("") || s.get("qd_id").equals("0")
					|| Integer.parseInt(s.get("qd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QuotationDownDetail_SEQ");
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "QuotationDownDetail", new String[] { "qd_id" }, new Object[] { id }));
			}
		}
		baseDao.execute(gridSql);
		// checkProduct(Integer.parseInt(store.get("qu_id").toString()));
		// 记录操作
		baseDao.logger.update(caller, "qu_id", store.get("qu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submitQuotationDowny(int qu_id, String caller) {
		handlerService.beforeSubmit(caller, new Object[] { qu_id });
		int count = baseDao.getCount("select count(1) from QuotationDownDetail where qd_quid=" + qu_id + " and nvl(qd_price,0)=0");
		if (count > 0)
			BaseUtil.showError("价格未全部填写。请先填写价格并更新后，再执行提交操作！");
		Object qu_source = baseDao.getFieldDataByCondition("QuotationDown", "qu_source", "qu_id="+qu_id);
		if(StringUtil.hasText(qu_source) && !qu_source.equals("平台商机")){
			checkCustUU(qu_id);
			checkProduct(qu_id);
			checkOfferprice(qu_id);
		}	
		// 执行提交操作
		baseDao.submit("QuotationDown", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "qu_id", qu_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { qu_id });
	}

	@Override
	public void resSubmitQuotationDown(int qu_id, String caller) {
		//执行反提交前的逻辑
		handlerService.beforeResSubmit(caller, qu_id);
		baseDao.resOperate("QuotationDown", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "qu_id", qu_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, qu_id);
	}

	@Transactional
	public void auditQuotationDown(int qu_id, String caller) {
		Object source = baseDao.getFieldDataByCondition("QuotationDown", "qu_source", "qu_id = "+qu_id);
		handlerService.beforeAudit(caller, new Object[] { qu_id });
		// 执行审核操作
		baseDao.audit("QuotationDown", "qu_id=" + qu_id, "qu_status", "qu_statuscode", "qu_auditdate", "qu_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "qu_id", qu_id);
		
		//来源为平台商机 的报价 审核后回传平台
		if("平台商机".equals(source)){
			String message = uploadQuotation(qu_id);
			if(message.length()>0){BaseUtil.showError(message);}
		}else{
			baseDao.execute("update QuotationDown set qu_sendstatus='待上传' where nvl(qu_sendstatus,' ')<>'已下载' and qu_id=" + qu_id);
			toSalePrice(qu_id);
		}
		
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { qu_id });
	}

	@Override
	public void resAuditQuotationDown(int qu_id, String caller) {
		Object qu_sendstatus = baseDao.getFieldDataByCondition("QuotationDown", "qu_sendstatus", "qu_id = "+qu_id);
		if("已报价".equals(qu_sendstatus)){
			BaseUtil.showError("该单据已报价到平台，无法反审核");
		}
		baseDao.resOperate("QuotationDown", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "qu_id", qu_id);
	}

	private void checkProduct(int qu_id) {
		baseDao.execute("update QuotationDown  set qu_prodcode=(select max(pc_prodcode) from productcustomer  where  pc_custcode=qu_custcode and pc_custprodcode=qu_custprodcode) where  qu_id = "
				+ qu_id);
		int countnum = baseDao.getCount("select count(*) from QuotationDown where qu_id=" + qu_id + " and nvl(qu_prodcode,' ')=' '");
		if (countnum != 0)
			BaseUtil.showError("该物料还未建立【客户物料对照关系】");
	}

	private void checkOfferprice(int qu_id) {
		int countnum = baseDao
				.getCount("select count(*) from QuotationDownDetail where nvl(qd_lapqty,0)=0 and nvl(qd_price,0)>0 and qd_quid=" + qu_id);
		if (countnum != 1) {
			BaseUtil.showError("分段报价的必须有一个0数量的报价,没有分段数量的不用填写分段数量,请直接报价!");
		}
		int checkdate = baseDao.getCount("select count(*) from QuotationDown where qu_fromdate>qu_todate and qu_id=" + qu_id);
		if (checkdate > 0) {
			BaseUtil.showError("报价日期有效开始日期必须小于等于截止日期!");
		}
		int checkenddate = baseDao.getCount("select count(*) from QuotationDown where qu_enddate + 1>sysdate and qu_id=" + qu_id);
		if (checkenddate == 0) {
			BaseUtil.showError("报价已经过了有效期,不能进行报价了!");
		}
	}

	private void checkCustUU(int qu_id) {
		baseDao.execute("update quotationdown a SET(qu_custcode,qu_custname)=(select cu_code,CU_NAME from Customer where cu_uu=a.qu_custuu and nvl(cu_auditstatuscode,' ') <>'DISABLE' ) where a.qu_id='"
				+ qu_id + "'");
		int countNum = baseDao.getCount("select count(*) from quotationdown where QU_ID='" + qu_id
				+ "'  and (nvl(QU_CUSTCODE,' ')=' ' OR nvl(qu_custname,' ')=' ')");
		if (countNum != 0) {
			BaseUtil.showError("客户编号或客户名称为空，不能提交");
		}
	}

	public void toSalePrice(int qu_id) {
		Key key = transferRepository.transfer("QuotationDown!ToSalePrice", qu_id);
		/*
		 * int spid = key.getId(); System.out.println(spid);
		 */
		// 转入明细
		transferRepository.transferDetail("QuotationDown!ToSalePrice", qu_id, key);
	}
	
	/**
	 * 审核时  上传平台商机来源类型的报价单
	 * 
	 * @return
	 */
	@Transactional
	private String uploadQuotation(int qu_id) {
		String url = "https://api-inquiry.usoftmall.com/inquiry/sale/item/saveQuote";
		Employee employee = SystemSession.getUser();
		if(employee.getEm_uu()==null){
			BaseUtil.showError("当前用户没有UU号，不能审核");
		}
		Object en_uu = baseDao.getFieldDataByCondition("enterprise", "en_uu", "1=1");
		Object[] data = baseDao.getFieldsDataByCondition("QuotationDown", "b2b_id_id,qu_custuu,qu_leadtime,qu_minqty,qu_minbuyqty,qu_currency,qu_taxrate,qu_isreplace", "qu_id = "+qu_id);
		List<Map<String,Object>> detail = baseDao.queryForList("select qd_lapqty as lapQty,qd_price as price from QuotationDownDetail where qd_quid = "+qu_id);
		if(detail.size()==0){
			BaseUtil.showError("审核失败，报价明细为空");
		}
		if(String.valueOf(data[2])==null||("null").equals(String.valueOf(data[2]))){
			BaseUtil.showError("审核失败，报价提前期为空");
		}
		List<Map<String,Object>> detailDate = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : detail) {
			Map<String,Object> hm = new HashMap<String,Object>();
			hm.put("lapQty", map.get("LAPQTY"));
			hm.put("qd_price", map.get("PRICE"));
			detailDate.add(hm);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("sourceId", String.valueOf(data[0]));
		params.put("vendUU", String.valueOf(en_uu));
		params.put("vendUserUU", String.valueOf(employee.getEm_uu()));
		params.put("leadtime", String.valueOf((Integer.valueOf((String.valueOf(data[2])))*24*60*60)));
		params.put("minPackQty", String.valueOf(data[3]));
		params.put("minOrderQty", String.valueOf(data[4]));
		params.put("currency", String.valueOf(data[5]));
		params.put("taxrate", String.valueOf(data[6]));
		if(String.valueOf(data[7]).equals("-1")){//替代料报价
			Object[] replaceData = baseDao.getFieldsDataByCondition("QuotationDown left join Product on qu_prodcode=pr_code", "pr_spec,pr_orispeccode,qu_brand", "qu_id = "+qu_id);
			if("null".equals(String.valueOf(replaceData[1]))||"null".equals(String.valueOf(replaceData[2]))){
				return "审核失败：替代料报价需要有品牌和型号";
			}
			params.put("replaceSpec", String.valueOf(replaceData[0]));
			params.put("replaceCmpCode", String.valueOf(replaceData[1]));
			params.put("replaceBrand", String.valueOf(replaceData[2]));
			params.put("isReplace", "1");
		}
		List<PublicRelay> details = baseDao.getJdbcTemplate().query(
				"select qd_lapqty as lapQty,qd_price as price from QuotationDownDetail where qd_quid=?",
				new BeanPropertyRowMapper<PublicRelay>(PublicRelay.class), qu_id);
		params.put("replies", details);
		try {
			if("10041559".equals(String.valueOf(en_uu))){//胜芳作为测试账套
				url = "http://218.17.158.219:24000/inquiry/sale/item/saveQuote";
			}
			com.uas.erp.core.HttpUtil.Response response = HttpUtil.doPost(url, JSON.toJSONString(params),false,null);
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
				String res = response.getResponseText();
				if(res!=null){
					Map<Object, Object> result = BaseUtil.parseFormStoreToMap(res);
					if(!result.get("success").equals(true)){
						return String.valueOf(result.get("message"));
					}
					baseDao.updateByCondition("QuotationDown", "qu_sendstatus='已报价'", "qu_id ="+qu_id);
				}else{
					return "报价到平台失败，审核失败";
				}
			} else {
				return "报价到平台失败，接口信息不是200";
			}
		} catch (Exception e) {
			return "报价到平台失败，审核失败";
		}
		return "";
	}
	
	@Transactional
	public void deleteQuotationDownDetail(int id){
		baseDao.deleteByCondition("QuotationDownDetail","qd_quid="+id);
	}
}
