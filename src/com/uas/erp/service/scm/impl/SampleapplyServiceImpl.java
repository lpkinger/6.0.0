package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.SampleapplyService;

@Service
public class SampleapplyServiceImpl implements SampleapplyService {

	static final String turnProdSample = "insert into productSample(ps_code,ps_prodcode," +
			",ps_status,ps_statuscode,ps_envrequire,ps_delivery,ps_scope,ps_oricode," +
			"ps_oridetno,ps_isfree,ps_id)values(?,?,?,?,?,?,?,?,?,?,?)";
	static final String selectSampleApp ="select * from Sampleapplydetail left join  product on sd_prodcode=pr_code left join "
			+ "Sampleapply on sd_said=sa_id where sd_id=?";
  
	static final String updateSample = "update Sampleapply set sa_isturn = '1' where sa_id=?";
	
	static final String updateSampleDetail="update sampleapplydetail set sd_turnprostatus=? where sd_id=?";

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSampleapply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if (store.get("sa_code").toString().trim().equals("")) {
			String code = baseDao.sGetMaxNumber("Sampleapply", 2);
			store.put("sa_code", code);
		}
		formStore = BaseUtil.parseMap2Str(store);
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Sampleapply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Sampleapplydetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "Sampleapplydetail", "sd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void updateSampleapplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Sampleapply", "sa_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "Sampleapplydetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sd_id") == null || s.get("sd_id").equals("") || s.get("sd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("Sampleapplydetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Sampleapplydetail", new String[] { "sd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteSampleapply(int sa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sa_id });
		// 删除purchase
		baseDao.deleteById("Sampleapply", "sa_id", sa_id);
		// 删除purchaseDetail
		baseDao.deleteById("Sampleapplydetail", "sd_said", sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sa_id });
	}

	@Override
	public void auditSampleapply(int sa_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Sampleapply", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sa_id });
		// 执行审核操作
		baseDao.audit("Sampleapply", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", sa_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sa_id });
	}

	@Override
	public void resAuditSampleapply(int sa_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Sampleapply", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resAuditOnlyAudit(status);
		int count = baseDao.getCountByCondition("Sampleapplydetail", "sd_said="+ sa_id + " and nvl(sd_turnprostatus, ' ')<>' '");
		if(count>0){
			BaseUtil.showError("已转打样申请单或者已转认定单不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resOperate("Sampleapply", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", sa_id);
	}

	@Override
	public void submitSampleapply(int sa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Sampleapply", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sd_detno) from Sampleapplydetail where trunc(sd_remanddate)<trunc(sysdate) and sd_said=?", String.class,
				sa_id);
		if (dets != null) {
			BaseUtil.showError("单据需求日期小于当前日期，不允许提交!行号：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sa_id });
		// 执行提交操作
		baseDao.submit("Sampleapply", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sa_id", sa_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sa_id });
	}

	@Override
	public void resSubmitSampleapply(int sa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Sampleapply", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sa_id });
		// 执行反提交操作
		baseDao.resOperate("Sampleapply", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sa_id", sa_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sa_id });
	}

	@Override
	@Transactional
	public String turnProductSample(String data, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(data);
		if (grid.size() == 0) {
			return "";
		}
		int id = Integer
				.parseInt(baseDao.getFieldDataByCondition("sampleapplydetail", "sd_said", "sd_id=" + grid.get(0).get("sd_id")) + "");
		String code = baseDao.getFieldDataByCondition("Sampleapply", "sa_code", "sa_id=" + id) + "";
		String prodSamCode;
		String sql;
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		StringBuffer sb = new StringBuffer();
		try {
			for (Map<Object, Object> g : grid) {
				SqlRowList rs = baseDao.queryForRowSet(selectSampleApp, g.get("sd_id"));
				while (rs.next()) {
					prodSamCode = baseDao.sGetMaxNumber("productSample", 2);
					String date = "null";
					if (rs.getString("sd_remanddate") != null) {
						date = "to_date('" + rs.getString("sd_remanddate").substring(0, 10) + "','yyyy-MM-dd')";
					}
					int newid = baseDao.getSeqId("productSample_SEQ");
					sql = "insert into productSample(ps_code,ps_prodcode,"
							+ "ps_statuscode,ps_status,ps_envrequire,ps_delivery,ps_scope,ps_oricode,"
							+ "ps_oridetno,ps_isfree,ps_id,ps_indate,ps_recordor,ps_prodname,ps_prodspec,ps_unit,ps_sampnum,ps_appman,ps_appmanid)values('"
							+ prodSamCode
							+ "','"
							+ rs.getString("sd_prodcode")
							+ "','"
							+ "ENTERING','"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "','"
							+ rs.getString("sd_envrequire")
							+ "',"
							+ date
							+ ",'"
							+ rs.getString("sd_scope")
							+ "','"
							+ code
							+ "','"
							+ rs.getInt("sd_detno")
							+ "','"
							+ rs.getString("sd_freeable")
							+ "','"
							+ newid
							+ "',to_date('"
							+ DateUtil.currentDateString("yyyy-MM-dd")
							+ "','yyyy-MM-dd'),'"
							+ SystemSession.getUser().getEm_name()
							+ "','"
							+ rs.getString("pr_detail")
							+ "','"
							+ rs.getString("pr_spec")
							+ "','"
							+ rs.getString("pr_unit") + "'," + rs.getFloat("sd_remandnum") + ",'"+rs.getString("sa_appman")+"',"+rs.getInt("sa_appmanid")+")";
					sqls.add(sql);
					sb.append(detno++ + ":<br/>"
							+ "转入成功,打样申请单号:<a href=\"javascript:openUrl('jsps/scm/product/ProductSample.jsp?formCondition=ps_idIS" + newid
							+ "&gridCondition=pd_psidIS" + newid + "')\">" + prodSamCode + "</a>&nbsp;" + "<hr/>");
					baseDao.execute(updateSampleDetail, new Object[] { "已转打样申请", g.get("sd_id") });
				}
			}
			baseDao.execute(sqls);
			baseDao.execute(updateSample, new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
		}
		baseDao.logger.turn("scm.product.turnProductSample", caller, "sa_id", id);
		return sb.toString();
	}

	static final String turnProductApproval = "insert into ProductApproval(pa_id,pa_code,pa_sacode,pa_statuscode,pa_status,"
			+ "pa_prodcode,pa_prodname,pa_spec,pa_unit,pa_recordorid,pa_recordor,pa_indate,pa_isturn,pa_sampleqty,PA_APPROTYPE,pa_sdid) "
			+ "values(?,?,?,'ENTERING',?,?,?,?,?,?,?,sysdate,0,?,?,?)";
	
	@Override
	public String turnProductApproval(String data) {
		List<Map<Object, Object>> grid=BaseUtil.parseGridStoreToMaps(data);
		if(grid.size()==0){
			return "";
		}
		int id=Integer.parseInt(baseDao.getFieldDataByCondition("sampleapplydetail", "sd_said", "sd_id="+grid.get(0).get("sd_id"))+"");
		String code=baseDao.getFieldDataByCondition("Sampleapply", "sa_code", "sa_id="+id)+"";
		String pacode;
		int detno=1;
		StringBuffer sb=new StringBuffer();
		for(Map<Object, Object> g:grid){
			SqlRowList rs = baseDao.queryForRowSet(selectSampleApp, g.get("sd_id"));
			while(rs.next()){
				int paid = baseDao.getSeqId("ProductApproval_SEQ");
				pacode = baseDao.sGetMaxNumber("ProductApproval", 2);
				baseDao.execute(turnProductApproval, new Object[]{paid, pacode, code, 
					BaseUtil.getLocalMessage("ENTERING"), rs.getString("sd_prodcode"),
					rs.getString("pr_detail"), rs.getString("pr_spec"), rs.getString("pr_unit"),
					SystemSession.getUser().getEm_uu(), SystemSession.getUser().getEm_name(), rs.getObject("sd_remandnum"),
					rs.getObject("sa_remark"), g.get("sd_id")});
				sb.append(detno++ + ":<br/>"+"转入成功,认定单号:<a href=\"javascript:openUrl('jsps/scm/product/ProductApproval.jsp?formCondition=pa_idIS" + paid
					+ "&gridCondition=null')\">" + pacode + "</a>&nbsp;"+"<hr/>");
				baseDao.execute(updateSampleDetail,new Object[]{"已转认定单", g.get("sd_id")});
			}
			baseDao.logger.others("scm.prodcut.turnProductApproval", "scm.prodcut.turnProductApprovalsuccess", "Sampleapply", "sa_id", id);
		}
		baseDao.execute(updateSample,new Object[]{id});
		return sb.toString();
	}
}
