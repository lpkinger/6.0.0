package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.TransferDao;
import com.uas.erp.model.Key;
import com.uas.erp.model.Transfer;
import com.uas.erp.service.plm.SampleMakeApplyService;
import com.uas.erp.service.pm.BOMBatchExpandService;
@Service
public class SampleMakeApplyServiceImpl implements SampleMakeApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private BOMBatchExpandService bomBatchExpandService; 
	
	@Autowired
	private TransferDao transferDao;
	
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveSampleMakeApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		/*int i = baseDao.getCountByCondition("PRJREQUEST", "pr_name='"+store.get("pr_name")+"'");
		if(i>0){//#maz 需求单名称不可重复
			BaseUtil.showError("该需求单名称已存在:"+ 
					"<a href=\"javascript:openUrl('jsps/plm/request/SampleMakeApply.jsp?formCondition=pr_nameIS" + store.get("pr_name") +  "')\">" + store.get("pr_name") + "</a>&nbsp;");
		}*/
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SAMPLEMAKEAPPLY", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "sm_id", store.get("sm_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});

	}

	@Override
	public void updateSampleMakeApplyById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		/*int i = baseDao.getCountByCondition("samplemakeapply", "pr_name='"+store.get("pr_name")+"' and pr_id<>"+store.get("pr_id")+"");
		if(i>0){//#maz 需求单名称不可重复，排除本身的需求名称判断是否有重复
			BaseUtil.showError("该需求单名称已存在:"+ 
					"<a href=\"javascript:openUrl('jsps/plm/request/require.jsp?formCondition=pr_nameIS" + store.get("pr_name") +  "')\">" + store.get("pr_name") + "</a>&nbsp;");
		}*/
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "samplemakeapply", "sm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sm_id", store.get("sm_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});

	}

	@Override
	public void deleteSampleMakeApply(int sm_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{sm_id});
		//删除主表内容
		baseDao.deleteById("samplemakeapply", "sm_id", sm_id);
		baseDao.logger.delete(caller, "sm_id", sm_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{sm_id});

	}

	@Override
	public void auditSampleMakeApply(int id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("samplemakeapply", "sm_statuscode", "sm_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{id});
		baseDao.audit("samplemakeapply", "sm_id=" + id, "sm_status", "sm_statuscode", "sm_auditdate", "sm_auditname");
		//记录操作
		baseDao.logger.audit(caller, "sm_id", id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{id});

	}

	@Override
	public void submitSampleMakeApply(int id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("samplemakeapply", "sm_statuscode", "sm_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("samplemakeapply", "sm_id=" + id, "sm_status", "sm_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sm_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });

	}

	@Override
	public void resSubmitSampleMakeApply(int id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("samplemakeapply", "sm_statuscode", "sm_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("samplemakeapply", "sm_id=" + id, "sm_status", "sm_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sm_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });

	}

	@Override
	public void resAuditSampleMakeApply(int sm_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("samplemakeapply", "sm_statuscode", "sm_id=" + sm_id);
		StateAssert.resAuditOnlyAudit(status);
		/**
		 * 对已转单的单据不能进行反审核操作 ，该限制暂未添加
		 */
		// 执行反审核操作
		baseDao.resAudit("samplemakeapply", "sm_id=" + sm_id, "sm_status", "sm_statuscode", "sm_auditdate", "sm_auditname");
		baseDao.resOperate("samplemakeapply", "sm_id=" + sm_id, "sm_status", "sm_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sm_id", sm_id);

	}

	@Override
	public String turnApplication(int id, String caller) {
		//转单配置  BOM多级展开查询所有子件物料
		
		//请购单                请购时间和录入时间都是当前时间
		//请购主表              
		Map<String, Object> tMap = new HashMap<String, Object>();
		List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();
		int detno = 0;
		int sm_reqqty = 0;
		String sm_prodcode=null;
		SqlRowList rs = baseDao.queryForRowSet("select sm_code,sm_prodcode,sm_reqqty from samplemakeapply "
				+ "where exists (select 1 from bomstruct where bs_mothercode=sm_prodcode) and sm_id=?",id);
		if(rs.next()){
			sm_reqqty = rs.getInt("sm_reqqty");
			sm_prodcode=rs.getString("sm_prodcode");
		}
		
		int ap_id = baseDao.getSeqId("APPLICATION_SEQ");
		String ap_code = baseDao.sGetMaxNumber("APPLICATION", 2);
		tMap.put("ap_id", ap_id);
		tMap.put("ap_code", ap_code);
		tMap.put("ap_remark", "样机制作申请");
		tMap.put("ap_source", rs.getString("sm_code"));
		tMap.put("ap_kind", "研发请购");
		tMap.put("ap_status",  BaseUtil.getLocalMessage("ENTERING"));
		tMap.put("ap_statuscode", "ENTERING");
		tMap.put("ap_printstatus",  BaseUtil.getLocalMessage("UNPRINT"));
		tMap.put("ap_printstatuscode", "UNPRINT");
		tMap.put("ap_pleamanname", SystemSession.getUser().getEm_name());
		tMap.put("ap_recorder", SystemSession.getUser().getEm_name());
		tMap.put("ap_recorddate",  DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
		
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), caller, "MAIN");
		if (transfer != null) {
			 Map<String, Object> tMaps = tMap;
//			 tMaps.put("ecr_id", ecr_id);
		     transferRepository.transfer("turnApplication", tMap);
		}else{
		    baseDao.execute(SqlUtil.getInsertSqlByMap(tMap, "Application"));
		}
		
		//BOM多级展开的BOM明细
		SqlRowList rs1 = baseDao.queryForRowSet("select distinct bs_soncode,pr_detail,pr_spec,pr_unit,bs_baseqty from bomstruct left join product on pr_code=bs_soncode where bs_topmothercode=? and bs_sonbomid = 0 and pr_manutype='PURCHASE'",sm_prodcode);
		while(rs1.next()){
			Map<Object, Object> tdMap = new HashMap<Object, Object>();
			int ad_id = baseDao.getSeqId("APPLICATIONDETAIL_SEQ");
			detno = detno + 1;
			tdMap.put("ad_apid", ap_id);
			tdMap.put("ad_id", ad_id);
			tdMap.put("ad_detno", detno);
			tdMap.put("ad_prodcode", rs1.getString("bs_soncode"));
			tdMap.put("ad_qty", rs1.getInt("bs_baseqty")*sm_reqqty);
			tdMap.put("ad_delivery", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			list.add(tdMap);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(list, "APPLICATIONDETAIL"));
		return "转入成功，请购单号:<a href=\"javascript:openUrl('jsps/scm/purchase/application.jsp?formCondition=ap_idIS" + ap_id
		+ "&gridCondition=ad_apidIS" + ap_id + "&whoami=Application')\">" + ap_code + "</a>&nbsp";
	}

	@Override
	public String turnMake(int sm_id, String caller) {
		int ma_id=0;
		String ma_code ="";
		Map<String, Object> resM = new HashMap<String, Object>();
		ma_id = baseDao.getSeqId("MAKE_SEQ");
		ma_code = baseDao.sGetMaxNumber("Make", 2);
		Map<String, Object> tMap = new HashMap<String, Object>();
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), "turnMake", "MAIN");
		if (transfer != null) {
			 Map<String, Object> tMaps = tMap;
			 tMaps.put("sm_id", sm_id);
			 tMaps.put("ma_id", ma_id);
			 tMaps.put("ma_code", ma_code);
		     transferRepository.transfer("turnMake", tMap);
		}else{
		    baseDao.execute(SqlUtil.getInsertSqlByMap(tMap, "make"));
		}
		return "转入成功，制造单号:<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?formCondition=ma_idIS" + ma_id
				+ "&gridCondition=mm_maidIS" + ma_id + "&whoami=Make!Base')\">" + ma_code + "</a>&nbsp";
	}

	@Override
	public String turnOtherOut(int sm_id, String caller) {
		int pi_id=0;
		int pd_id =0;
		String pi_inoutno ="";
		Map<String, Object> resM = new HashMap<String, Object>();
		List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();
		pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
		pi_inoutno = baseDao.sGetMaxNumber("Prodinout", 2);
		Map<String, Object> tMap = new HashMap<String, Object>();
		Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), "turnOtherOut", "MAIN");
		if (transfer != null) {
			 Map<String, Object> tMaps = tMap;
			 tMaps.put("sm_id", sm_id);
			 tMaps.put("pi_id", pi_id);
			 tMaps.put("pi_inoutno", pi_inoutno);
		     transferRepository.transfer("turnOtherOut", tMap);
		}else{
		    baseDao.execute(SqlUtil.getInsertSqlByMap(tMap, "prodinout"));
		}
		//产品编号转入其它出库单明细
		pd_id = baseDao.getSeqId("PRODINOUTDETAIL_SEQ");
		Map<Object,Object> map = new HashMap<Object,Object>();
		map.put("pd_id", pd_id);
		map.put("pd_piid", pi_id);
		map.put("pd_pdno", 1);
		int pd_outqty =0;
		SqlRowList rs = baseDao.queryForRowSet("select sm_reqqty,sm_prodcode from samplemakeapply where sm_id=?",sm_id);
		if(rs.next()){
			pd_outqty = rs.getInt("sm_reqqty");
			map.put("pd_outqty",pd_outqty);
			map.put("pd_prodcode", rs.getString("sm_prodcode"));
		}
		list.add(map);
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(list, "prodiodetail"));
		//反写回样板制造申请单
		String sm_takename = SystemSession.getUser().getEm_name();//取板人
		Date date = new Date();
		baseDao.execute("update samplemakeapply set sm_takename=?,sm_prodoutcode=?,sm_takedate=?,sm_takeqty=? where sm_id=?",sm_takename,pi_inoutno,date,pd_outqty,sm_id);
		return "转入成功，其它出库单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
				+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!OtherOut')\">" + pi_inoutno + "</a>&nbsp";
	}

}
