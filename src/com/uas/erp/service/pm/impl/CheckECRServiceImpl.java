package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.TransferDao;
import com.uas.erp.model.Transfer;
import com.uas.erp.service.pm.CheckECRService;

@Service("checkECRService")
public class CheckECRServiceImpl implements CheckECRService {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private TransferDao transferDao;
	@Override
	public void saveCheck(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ECR", "ecr_code='" + store.get("ecr_code") + "'");
		if (!bool) {	
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave("ECR!Check", new Object[] { store }); // 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECR", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存ECRDetail
		Object[] ecrd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ecrd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				ecrd_id[i] = baseDao.getSeqId("ECRDETAIL_SEQ");
			}
		} else {
			ecrd_id[0] = baseDao.getSeqId("ECRDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ECRDetail", "ecrd_id", ecrd_id);
		baseDao.execute(gridSql);
		Object ecr_id=store.get("ecr_id");
		baseDao.execute("update ecrdetail set ecrd_oldbaseqty=(select max(bd_baseqty) from bomdetail where bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno),ecrd_location=replace(replace(ecrd_location,'，',','),' ',''),ecrd_oldlocation=replace(replace(ecrd_oldlocation,'，',','),' ','') where ecrd_ecrid=?",ecr_id);
		// 评审表保存、更新，如果替换的行新用量0，默认等于原用量
		baseDao.execute("update ecrdetail set ecrd_newbaseqty=ecrd_oldbaseqty where ecrd_ecrid=? and ecrd_type in('替换','SWITCH') and nvl(ecrd_newbaseqty,0)=0 and ecrd_oldbaseqty is not null",ecr_id);
		//反馈编号:2017020577  增加更新 1.如果母件编号不为空，bomid 为0或者空，更新bomid2.如果变更类型为修改和禁用 新料或主料编号不为空 +序号为空，更新评审表bom序号3.如果操作类型是替换 ,旧料或替代料不为空 +序号为空，更新评审表bom序号
		//如果母件编号不为空，bomid 为0或者空，更新bomid
		baseDao.execute("update ecrdetail set ecrd_bomid=(select bo_id from bom where bo_mothercode=ecrd_mothercode)"
                       +"where ecrd_ecrid=? and nvl(ecrd_mothercode,' ')<>' ' and nvl(ecrd_bomid,0)=0 and exists(select 1 from bom where bo_mothercode=ecrd_mothercode)",ecr_id);
		//如果变更类型为修改和禁用 新料或主料编号不为空 +序号为空
		baseDao.execute("update ecrdetail set ecrd_bddetno=(select max(bd_detno) from BOMDETAIL where BD_BOMID=ecrd_bomid AND BD_SONCODE=ecrd_soncode)"
                       +"where ecrd_ecrid=? and ecrd_type in('UPDATE','DISABLE') and nvl(ecrd_soncode,' ')<>' ' and nvl(ecrd_bddetno,0)=0"
                       +" and exists(select 1 from bom LEFT JOIN BOMDETAIL on bo_id=bd_bomid where bo_ID=ecrd_BOMID AND BD_SONCODE=ecrd_soncode and nvl(bd_stepcode,' ')=nvl(ecrd_stepcode,' '))",ecr_id);
		//如果操作类型是替换 ,旧料或替代料不为空 +序号为空
		baseDao.execute("update ecrdetail set ecrd_bddetno=(select max(bd_detno) from BOMDETAIL where BD_BOMID=ecrd_bomid AND BD_SONCODE=ecrd_repcode)"
                       +"where ecrd_ecrid=? and ecrd_type in('SWITCH') and nvl(ecrd_repcode,' ')<>' ' and nvl(ecrd_bddetno,0)=0"
                       +" and exists(select 1 from bom LEFT JOIN BOMDETAIL on bo_id=bd_bomid where bo_ID=ecrd_BOMID AND BD_SONCODE=ecrd_repcode and nvl(bd_stepcode,' ')=nvl(ecrd_stepcode,' '))",ecr_id);
		//更新明细字段是否升级BOM，如果主表填写的是"是"，则明细表全部更新为是
		baseDao.execute("update ecrdetail set ecrd_upbomversion=-1 where ecrd_ecrid=? and exists(select 1 from ecr where ecr_id=? and nvl(ecr_upbomversion,0)<>0)",ecr_id,ecr_id);
		
		// 根据参数配置ifDCNChangePR， 是否启用BOM阶段等于DCN的，单据前缀设为DR
		bool = baseDao.isDBSetting(caller, "ifDCNChangePR");
		if (bool) {
			Object[] data = baseDao.getFieldsDataByCondition("ecr", "ecr_prodstage,ecr_code", "ecr_id=" + ecr_id);
			if (data[0] != null && "DCN".equals(data[0])) {
				Object ob = baseDao.getFieldDataByCondition("MAXNUMBERS", "MN_LEADCODE", "mn_tablename='" + caller + "'");
				if (ob != null) {
					baseDao.updateByCondition("ECR", "ecr_code='" + data[1].toString().replaceAll(ob.toString(), "DR") + "'", "ecr_code='" + data[1] + "'");
				} else {
					baseDao.updateByCondition("ecr", "ecr_code='DR'||'" + data[1].toString() + "'", "ecr_code='" + data[1] + "'");
				}
			}
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "ecr_id", store.get("ecr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//@编号 2018030078 
		baseDao.execute("update ecrdetail A set A.ecrd_oldbdremark2 = (select bd_remark2  from ecrdetail B"
                        +" left join bom on B.ecrd_bomid = bo_id left join bomdetail on bo_id = bd_bomid and B.ecrd_bddetno = bd_detno"
                        +" where A.ecrd_id = B.ecrd_id)"
                        +" where A.ecrd_ecrid =?",ecr_id);
		baseDao.execute("update ecrdetail A set A.ecrd_newbdremark2 = (select bd_remark2  from ecrdetail B"
                +" left join bom on B.ecrd_bomid = bo_id left join bomdetail on bo_id = bd_bomid and B.ecrd_bddetno = bd_detno"
                +" where A.ecrd_id = B.ecrd_id)"
                +" where A.ecrd_ecrid =? and A.ecrd_newbdremark2 is null",ecr_id);
		// 执行保存后的其它逻辑
		handlerService.afterSave("ECR!Check", new Object[] { store });
	}
		
	@Override
	public void deleteCheck(int ecr_id, String caller) {
		// 只能删除在录入的ECR
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("ECR!Check", new Object[] { ecr_id }); // 删除ECR
		// 判断ECR评审表是否来源于已审核的评审单，如果是的话则不允许删除
		Object ob = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		if (ob != null && !ob.equals("") && ob.toString().equals("AUDITED")) {
			BaseUtil.showError("不允许删除来源于设计变更申请单的评审单，请先反审核设计变更申请单");
		}
		baseDao.deleteById("ECR", "ecr_id", ecr_id);
		// 删除purchaseDetail
		baseDao.deleteById("ECRDetail", "ecrd_ecrid", ecr_id);
		// 记录操作
		baseDao.logger.delete(caller, "ecr_id", ecr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("ECR!Check", new Object[] { ecr_id });
	}
	
	
	
	@Override
	public void updateCheckById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的ECR!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + store.get("ecr_id"));
		StateAssert.updateOnlyEntering(status);
		// 更新ECR计划下达数\本次下达数\状态
		// purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECR", "ecr_id");
		baseDao.execute(formSql);
		// 修改ECRDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECRDetail", "ecrd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ecrd_id") == null || s.get("ecrd_id").equals("") || s.get("ecrd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ECRDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ECRDetail", new String[] { "ecrd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}

		baseDao.execute(gridSql);
		baseDao.execute("update ecrdetail set ecrd_oldbaseqty=(select max(bd_baseqty) from bomdetail where bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno) ,ecrd_location=replace(replace(ecrd_location,'，',','),' ',''),ecrd_oldlocation=replace(replace(ecrd_oldlocation,'，',','),' ','') where ecrd_ecrid='" + store.get("ecr_id") + "'");
		Object ecr_id = store.get("ecr_id");
		// 评审表保存、更新，如果替换的行新用量0，默认等于原用量
		baseDao.execute("update ecrdetail set ecrd_newbaseqty=ecrd_oldbaseqty where ecrd_ecrid=? and ecrd_type in('替换','SWITCH') and nvl(ecrd_newbaseqty,0)=0 and ecrd_oldbaseqty is not null",ecr_id);
		
		//反馈编号:2017020577,1.如果母件编号不为空，bomid 为0或者空，更新bomid2.如果变更类型为修改和禁用 新料或主料编号不为空 +序号为空，更新评审表bom序号3.如果操作类型是替换 ,旧料或替代料不为空 +序号为空，更新评审表bom序号
		baseDao.execute("update ecrdetail set ecrd_bomid=(select bo_id from bom where bo_mothercode=ecrd_mothercode)"
                       +"where ecrd_ecrid=? and nvl(ecrd_mothercode,' ')<>' ' and nvl(ecrd_bomid,0)=0 and exists(select 1 from bom where bo_mothercode=ecrd_mothercode)",ecr_id);
			//如果变更类型为修改和禁用 新料或主料编号不为空 +序号为空
		baseDao.execute("update ecrdetail set ecrd_bddetno=(select max(bd_detno) from BOMDETAIL where BD_BOMID=ecrd_bomid AND BD_SONCODE=ecrd_soncode)"
                       +"where ecrd_ecrid=? and ecrd_type in('UPDATE','DISABLE') and nvl(ecrd_soncode,' ')<>' ' and nvl(ecrd_bddetno,0)=0"
                       +" and exists(select 1 from bom LEFT JOIN BOMDETAIL on bo_id=bd_bomid where bo_ID=ecrd_BOMID AND BD_SONCODE=ecrd_soncode and nvl(bd_stepcode,' ')=nvl(ecrd_stepcode,' '))",ecr_id);
			//如果操作类型是替换 ,旧料或替代料不为空 +序号为空
		baseDao.execute("update ecrdetail set ecrd_bddetno=(select max(bd_detno) from BOMDETAIL where BD_BOMID=ecrd_bomid AND BD_SONCODE=ecrd_repcode)"
                       +"where ecrd_ecrid=? and ecrd_type in('SWITCH') and nvl(ecrd_repcode,' ')<>' ' and nvl(ecrd_bddetno,0)=0"
                       +" and exists(select 1 from bom LEFT JOIN BOMDETAIL on bo_id=bd_bomid where bo_ID=ecrd_BOMID AND BD_SONCODE=ecrd_repcode and nvl(bd_stepcode,' ')=nvl(ecrd_stepcode,' '))",ecr_id);
		//更新明细字段是否升级BOM，如果主表填写的是"是"，则明细表全部更新为是
		baseDao.execute("update ecrdetail set ecrd_upbomversion=-1 where ecrd_ecrid=? and exists(select 1 from ecr where ecr_id=? and nvl(ecr_upbomversion,0)<>0)",ecr_id,ecr_id);
		//@编号 2018030078 
		baseDao.execute("update ecrdetail A set A.ecrd_oldbdremark2 = (select bd_remark2  from ecrdetail B"
                        +" left join bom on B.ecrd_bomid = bo_id left join bomdetail on bo_id = bd_bomid and B.ecrd_bddetno = bd_detno"
                        +" where A.ecrd_id = B.ecrd_id)"
                        +" where A.ecrd_ecrid =?",ecr_id);
		baseDao.execute("update ecrdetail A set A.ecrd_newbdremark2 = (select bd_remark2  from ecrdetail B"
                +" left join bom on B.ecrd_bomid = bo_id left join bomdetail on bo_id = bd_bomid and B.ecrd_bddetno = bd_detno"
                +" where A.ecrd_id = B.ecrd_id)"
                +" where A.ecrd_ecrid =? and A.ecrd_newbdremark2 is null",ecr_id);
		// 记录操作
		baseDao.logger.update(caller, "ecr_id",ecr_id);
		// 更新上次采购价格、供应商
		// purchaseDao.updatePrePurchase((String)store.get("pu_code"),
		// (String)store.get("pu_date"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("ECR!Check", new Object[] { store, gstore });
	}

	@Override
	public void auditCheck(int ecr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.auditOnlyCommited(status);
		//根据物料资料中参数配置：允许物料名称+规格+规格参数重复   检查存在名称、规格一样的物料
		checkProdName(ecr_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("ECR!Check", new Object[] { ecr_id }); // 变更规格名称的处理
		baseDao.execute("update ecr set (ecr_prodname,ecr_prodspec,ecr_speccs)=(select pr_detail,pr_spec,pr_speccs from product where pr_code=ecr_prodcode) where ecr_id=" + ecr_id);
		SqlRowList rs = baseDao.queryForRowSet("select ecr_prodcode,ecr_prodname,ecr_prodspec,ecr_newprodname,ecr_newspec,ecr_newspeccs from ecr where ecr_id=" + ecr_id + "  and ecr_prodcode<>' ' and (NVL(ecr_newprodname,' ')<>' ' or nvl(ecr_newspec,' ')<>' ')");
		if (rs.next()) {
			if (StringUtil.hasText(rs.getString("ecr_newprodname"))) {
				baseDao.execute("update product set pr_detail=?,PR_SENDSTATUS='待上传'  where pr_code=?",rs.getString("ecr_newprodname"),rs.getString("ecr_prodcode"));
			}
			if (StringUtil.hasText(rs.getString("ecr_newspec"))) {
				baseDao.execute("update product set pr_spec=?,PR_SENDSTATUS='待上传'  where pr_code= ? ",rs.getString("ecr_newspec"),rs.getString("ecr_prodcode") );
			}
			if (StringUtil.hasText(rs.getString("ecr_newspeccs"))) {
				baseDao.execute("update product set pr_speccs=?  where pr_code= ?",rs.getString("ecr_newspeccs"),rs.getString("ecr_prodcode"));
			}
		}
		rs = baseDao.queryForRowSet("select ecrd_detno,ecrd_soncode,ecrd_sonname,ecrd_sonspec from ecrdetail where ecrd_ecrid=" + ecr_id + " and ecrd_type='CHANGENAME'");
		while (rs.next()) {
			if (StringUtil.hasText(rs.getString("ecrd_sonname"))) {
				baseDao.execute("update product set pr_detail= ? ,PR_SENDSTATUS='待上传'  where pr_code= ? ",rs.getString("ecrd_sonname"),rs.getString("ecrd_soncode"));
			}
			if (StringUtil.hasText(rs.getString("ecrd_sonspec"))) {
				baseDao.execute("update product set pr_spec= ? ,PR_SENDSTATUS='待上传'  where pr_code=?",rs.getString("ecrd_sonspec"),rs.getString("ecrd_soncode"));
			}
		}
		// 执行审核操作
		baseDao.audit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus2", "ecr_checkstatus2code", "ecr_auditdate2", "ecr_auditman2");
		// 记录操作
		baseDao.logger.audit(caller, "ecr_id", ecr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("ECR!Check", new Object[] { ecr_id });
		Map<String, Object> map = new HashMap<String, Object>();
		if (baseDao.isDBSetting("ECR!Check", "autoTurnECN")) {
			map = turnECN(ecr_id, caller);
			if (map.get("error") != null && !map.get("error").equals("")) {
				BaseUtil.showErrorOnSuccess(map.get("error").toString());
			}
		}
	}

	@Override
	public void resAuditCheck(int ecr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 判断是否转过ECN
		boolean bool = baseDao.checkIf("ECN left join ecr on ecn_ecrcode=ecr_code", "ecr_id=" + ecr_id);
		if (bool) {
			BaseUtil.showError("当前评审单已转ECN,不能反审核!");
		}
		// 执行反审核操作
		baseDao.resAudit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus2", "ecr_checkstatus2code", "ecr_auditman", "ecr_auditdate");
		// 规格名称还原处理
		SqlRowList rs = baseDao.queryForRowSet("select ecr_prodcode,ecr_prodname,ecr_prodspec,ecr_speccs,ecr_newprodname,ecr_newspec,ecr_newspeccs from ecr where ecr_id=" + ecr_id + " and ecr_prodcode<>' ' and (NVL(ecr_newprodname,' ')<>' ' or nvl(ecr_newspec,' ')<>' ')");
		if (rs.next()) {
			if (StringUtil.hasText(rs.getString("ecr_newprodname"))) {
				baseDao.execute("update product set pr_detail= ? ,PR_SENDSTATUS='待上传'  where pr_code= ? ",rs.getString("ecr_prodname"),rs.getString("ecr_prodcode"));
			}
			if (StringUtil.hasText(rs.getString("ecr_newspec"))) {
				baseDao.execute("update product set pr_spec= ? ,PR_SENDSTATUS='待上传'  where pr_code= ? ",rs.getString("ecr_prodspec") ,rs.getString("ecr_prodcode"));
			}
			if (StringUtil.hasText(rs.getString("ecr_newspeccs"))) {
				baseDao.execute("update product set pr_speccs= ?  where pr_code= ?",rs.getString("ecr_speccs"),rs.getString("ecr_prodcode"));
			}
		}
		rs = baseDao.queryForRowSet("select ecrd_detno,ecrd_soncode,ecrd_sonname,ecrd_sonspec,ecrd_repname,ecrd_repspec from ecrdetail where ecrd_ecrid=" + ecr_id + " and ecrd_type='CHANGENAME'");
		while (rs.next()) {
			if (StringUtil.hasText(rs.getString("ecrd_sonname"))) {
				baseDao.execute("update product set pr_detail= ? ,PR_SENDSTATUS='待上传'  where pr_code= ?",rs.getString("ecrd_repname"),rs.getString("ecrd_soncode"));
			}
			if (StringUtil.hasText(rs.getString("ecrd_sonspec"))) {
				baseDao.execute("update product set pr_spec= ? ,PR_SENDSTATUS='待上传'  where pr_code= ? ",rs.getString("ecrd_repspec"),rs.getString("ecrd_soncode") );
			}
		}
		//反审核时，对ecr_turnstatus设置为空值
		Object ecr_turnstatus = baseDao.getFieldDataByCondition("ECR", "ecr_turnstatus", "ecr_id=" + ecr_id);
		if(ecr_turnstatus!=null && !"".equals(ecr_turnstatus)){
			String sql = "update ecr set ecr_turnstatus='' where ecr_id=? and nvl(ecr_turnstatus,' ')<>' '";
			baseDao.execute(sql,ecr_id);
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "ecr_id", ecr_id);
	}

	@Override
	public void submitCheck(int ecr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.submitOnlyEntering(status);
		// 更新操作类型值
		UpdateECRTypeCode(ecr_id, caller);
		//更新ecrd_stepcode为bd_stepcode
		baseDao.execute("update ecrdetail set ecrd_stepcode=(select bd_stepcode from bomdetail where bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno) where ecrd_ecrid=? and exists (select 1 from bomdetail where bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno and nvl(bd_stepcode,' ')<> NVL(ecrd_stepcode,' '))",ecr_id);
		// 合法性检测
		CheckECR_ALL(ecr_id, caller);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("ECR!Check", new Object[] { ecr_id }); // 执行提交操作
		baseDao.submit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus2", "ecr_checkstatus2code");
		// 记录操作
		baseDao.logger.submit(caller, "ecr_id", ecr_id);
		//@zjh 2017090532 提交成功之后增加更新
		int n = baseDao.getCount("select * from ECRDetail where ecrd_ecrid=" + ecr_id + " and ecrd_type<>'CHANGENAME'");
		if(n==0){
			//无需转明细行
			baseDao.execute("update ecr set ecr_turnstatus='不执行' where ecr_id=?",ecr_id);
		}
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("ECR!Check", new Object[] { ecr_id });
	}

	@Override
	public void resSubmitCheck(int ecr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("ECR!Check", new Object[] { ecr_id });
		// 执行反提交操作
		baseDao.resOperate("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus2", "ecr_checkstatus2code");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ecr_id", ecr_id);
		//@zjh 2017090532 如果 ecr_turnstatus='不执行'，更新成空 update ecr set ecr_turnstatus='' where ecr_id=? 
		Object ecr_turnstatus = baseDao.getFieldDataByCondition("ECR", "ecr_turnstatus", "ecr_id=" + ecr_id);
		if("不执行".equals(ecr_turnstatus)){
			String sql = "update ecr set ecr_turnstatus='' where ecr_id=? and nvl(ecr_turnstatus,' ')<>' '";
			baseDao.execute(sql,ecr_id);
		}
		handlerService.afterResSubmit("ECR!Check", new Object[] { ecr_id });
	}

	@Override
	@Transactional
	public Map<String, Object> turnECN(int ecr_id, String caller) {
		int ecnid = 0;
		int edid = 0;
		int edlid = 0;
		int detno = 0;
		String ECNCode = "";
		String ECRCode = "";
		String sql = "";
		Map<String, Object> resM = new HashMap<String, Object>();
		resM.put("ecnid", ecnid);
		List<Map<Object, Object>> maps = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> lmaps = new ArrayList<Map<Object, Object>>();
		Object[] ve = baseDao.getFieldsDataByCondition("ECR", new String[] { "ecr_code", "ecr_checkstatus2code" }, "ecr_id=" + ecr_id);
		if (ve != null) {
			ECRCode = ve[0].toString();
			if (!ve[1].toString().equals("AUDITED")) {
				BaseUtil.showError("设计变更评审单未审核，不允许转ECN");
			}
		} else {
			BaseUtil.showError("记录不存在！");
		}
		Boolean autoTurnECN = baseDao.isDBSetting("ECR!Check", "autoTurnECN");
		Object FindCode = baseDao.getFieldDataByCondition("ecn", "ecn_code", "ecn_ecrcode='" + ECRCode + "'");
		if (FindCode != null && !FindCode.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.bom.ecr.haveturn") + "<a href=\"javascript:openUrl('jsps/pm/bom/ECN.jsp?formCondition=ecn_codeIS" + FindCode + "&gridCondition=ed_codeIS" + FindCode + "')\">" + FindCode + "</a>&nbsp;");
		} else {
			// 没有需要转ECN的明细，则退出
			int n = baseDao.getCount("select * from ECRDetail where ecrd_ecrid=" + ecr_id + " and ecrd_type<>'CHANGENAME' order by ecrd_detno ");
			if (n == 0) {
				if (autoTurnECN)
					return resM;
				else {
					BaseUtil.showError("没有需要转ECN的明细行");
					return resM;
				}
			}
			// 复制主表数据
			sql = "select * from ecr where ecr_id=" + ecr_id;
			String ECR_KIND = "";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				Map<String, Object> tMap = new HashMap<String, Object>();
				ecnid = baseDao.getSeqId("ECN_SEQ");
				ECNCode = baseDao.sGetMaxNumber("ECN", 2);
				ECR_KIND = rs.getString("ECR_KIND");
				tMap.put("ECN_ID", ecnid);
				tMap.put("ECN_CODE", ECNCode);
				tMap.put("ECN_TYPE", rs.getObject("ECR_KIND"));
				tMap.put("ECN_CHECKSTATUS", BaseUtil.getLocalMessage("ENTERING"));
				tMap.put("ECN_CHECKSTATUSCODE", "ENTERING");
				tMap.put("ECN_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
				tMap.put("ECN_DIDSTATUSCODE", "OPEN");
				tMap.put("ECN_UPMAN", rs.getObject("ECR_UPMAN"));
				tMap.put("ECN_UPDEPARTMENT", rs.getObject("ECR_UPDEPART"));
				tMap.put("ECN_CHANGEREASON", rs.getObject("ECR_REASON"));
				tMap.put("ECN_LEVEL", rs.getObject("ECR_LEVEL"));
				if (autoTurnECN) {
					tMap.put("ECN_RECORDMAN", rs.getObject("ECR_UPMAN"));
				} else {
					tMap.put("ECN_RECORDMAN", SystemSession.getUser().getEm_name());
				}
				tMap.put("ECN_INDATE", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
				tMap.put("ECN_REMARK", rs.getObject("ECR_REMARK"));
				tMap.put("ECN_PLANDATE", rs.getObject("ECR_AIMDATE"));
				tMap.put("ECN_ECRCODE", ECRCode);
				tMap.put("ECN_COP", rs.getObject("ECR_COP"));
				tMap.put("ECN_CUSTNAME", rs.getObject("ECR_CUNAME"));
				tMap.put("ECN_PRODSTAGE", rs.getObject("ECR_PRODSTAGE"));
				tMap.put("ECN_PRODKIND", rs.getObject("ECR_PRODKIND"));
				tMap.put("ECN_CUSTAUDIT", rs.getObject("ECR_CUSTAUDIT"));
				tMap.put("ECN_CHANGEKIND", rs.getObject("ECR_CHANGEKIND"));
				tMap.put("ECN_EFFECTRANGE", rs.getObject("ECR_EFFECTRANGE"));
				tMap.put("ECN_RECEIVEMAN", rs.getObject("ECR_RECEIVEMAN"));
				tMap.put("ECN_OLDPRODDEAL", rs.getObject("ECR_OLDPRODDEAL"));
				tMap.put("ECN_ATTACH", rs.getObject("ECR_ATTACH"));
				tMap.put("ECN_ISBATCH", rs.getObject("ECR_ISBATCH"));
				tMap.put("ECN_REQUEST", rs.getObject("ECR_REQUEST"));
				tMap.put("ECN_UPBOMVERSION", rs.getObject("ECR_UPBOMVERSION"));
				/**
				 * ECR配置转ECN，在有配置的时候通过配置转单，没有配置则使用写死的转单
				 * 							  2016年12月1日11:15:01
				 */
				Transfer transfer = transferDao.getTransfer(SpObserver.getSp(), "turnECN", "MAIN");
				if (transfer != null) {
					 Map<String, Object> tMaps = tMap;
					 tMaps.put("ecr_id", ecr_id);
				     transferRepository.transfer("turnECN", tMap);
				}else{
				    baseDao.execute(SqlUtil.getInsertSqlByMap(tMap, "ECN"));
				}
				resM.put("ecnid", ecnid);
				resM.put("ecncode", ECNCode);
			}
			/**
			 * 明细行操作类型属于批量变更的，执行ECN变更的范围包括已禁用的BOM
			 * 
			 * @author XiaoST 2016年8月15日 下午7:21:51
			 */
			Boolean TurnECNWithDisabledBom = baseDao.isDBSetting("ECR!Check", "TurnECNWithDisabledBom");
			String condition = "and bo_statuscode<>'DISABLE'";
			if (TurnECNWithDisabledBom) {
				condition = " ";
			}
			// 复制明细表数据
			sql = "select * from ECRDetail left join bom on bo_id=ecrd_bomid where ecrd_ecrid=" + ecr_id + " and ecrd_type<>'CHANGENAME' order by ecrd_detno ";
			rs = baseDao.queryForRowSet(sql);
			detno = 0;
			while (rs.next()) {
				if (rs.getObject("ECRD_TYPE").equals("BATCHDISABLE") || rs.getObject("ECRD_TYPE").equals("BATCHSWITCH") || rs.getObject("ECRD_TYPE").equals("BATCHREPDISABLE") || rs.getObject("ECRD_TYPE").equals("BATCHREPADD") || rs.getObject("ECRD_TYPE").equals("BATCHSETMAIN")) {// 批量变更的需要从BOM明细检索涉及变更的BOM
					if (rs.getObject("ECRD_TYPE").equals("BATCHSWITCH")) {
						if(baseDao.isDBSetting("ECR!Check","replaceRepprodcode")){
							String v_sql ="select * from prodreplace left join bom on bo_id = pre_bomid left join bomdetail on bo_id =bd_bomid and bd_detno = pre_bddetno where nvl(pre_status,' ')<>'已禁用' and nvl(bd_usestatus,' ')<>'DISABLE' and pre_repcode ='"+rs.getString("ecrd_repcode")+"'";
							
							SqlRowList rs1 = baseDao.queryForRowSet(v_sql);
							while(rs1.next()){
								v_sql ="select * from prodreplace left join bom on bo_id = pre_bomid left join "
										+ "bomdetail on bo_id =bd_bomid and bd_detno = pre_bddetno where nvl(pre_status,' ')<>'已禁用' "
										+ "and nvl(bd_usestatus,' ')<>'DISABLE' and pre_repcode ='"+rs.getString("ecrd_soncode")+"' and pre_soncode='"+rs1.getString("pre_soncode")+"'";
								
								SqlRowList rs2 = baseDao.queryForRowSet(v_sql);
								if(rs2.size()>0){
									while(rs2.next()){
										//禁用旧料   rs2 获取bom
										Map<Object, Object> new_map = new HashMap<Object, Object>();
										detno = detno + 1;
										edid = baseDao.getSeqId("ECNDETAIL_SEQ");
										new_map.put("ED_ID", edid);
										new_map.put("ED_ECNID", ecnid);
										new_map.put("ED_DETNO", detno);
										new_map.put("ED_BOID", rs2.getObject("bd_bomid"));
										new_map.put("ED_MOTHERCODE", rs2.getObject("bo_mothercode"));
										new_map.put("ED_MOTHERNAME", rs2.getObject("bo_mothername"));
										new_map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
										new_map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
										new_map.put("ED_DIDSTATUSCODE", "OPEN");
										new_map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
										new_map.put("ED_CODE", ECNCode);
										new_map.put("ED_SONCODE", rs2.getObject("bd_SONCODE"));;
										//禁用
										new_map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
										new_map.put("ED_TYPE", "REPDISABLE");
										new_map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
										new_map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
										new_map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
										new_map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
										new_map.put("ED_OLDBASEQTY", rs2.getObject("bd_baseqty"));
										new_map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
										new_map.put("ED_BDDETNO", rs2.getObject("bd_detno"));
										new_map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
										new_map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
										new_map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
										new_map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
										new_map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
										new_map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
										new_map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
										new_map.put("ED_OLDBOMVERSION", rs2.getObject("BO_VERSION"));
										new_map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
										new_map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
										maps.add(new_map);
										
									}
								}else{
									int count1 = baseDao.getCount("select count(*) from bomdetail where bd_usestatus<>'DISABLE' and bd_soncode='"+rs.getString("ecrd_soncode")+"' and bd_bomid="+rs1.getInt("bo_id"));
					                boolean flag = baseDao.isDBSetting("BOM", "checkRepProd");
									//没有符合条件的 A
									if(flag||(!flag&&count1==0)){
										//新增 A 从rs1获取bom
										Map<Object, Object> map = new HashMap<Object, Object>();
										detno = detno + 1;
										edid = baseDao.getSeqId("ECNDETAIL_SEQ");
										map.put("ED_ID", edid);
										map.put("ED_ECNID", ecnid);
										map.put("ED_DETNO", detno);
										map.put("ED_BOID", rs1.getObject("bd_bomid"));
										map.put("ED_MOTHERCODE", rs1.getObject("bo_mothercode"));
										map.put("ED_MOTHERNAME", rs1.getObject("bo_mothername"));
										map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
										map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
										map.put("ED_DIDSTATUSCODE", "OPEN");
										map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
										map.put("ED_CODE", ECNCode);
										map.put("ED_SONCODE", rs1.getObject("bd_SONCODE"));;
										//增加
										map.put("ED_REPCODE", rs.getObject("ECRD_SONCODE"));
										map.put("ED_TYPE", "REPADD");
										map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
										map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
										map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
										map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
										map.put("ED_OLDBASEQTY", rs1.getObject("bd_baseqty"));
										map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
										map.put("ED_BDDETNO", rs1.getObject("bd_detno"));
										map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
										map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
										map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
										map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
										map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
										map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
										map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
										map.put("ED_OLDBOMVERSION", rs1.getObject("BO_VERSION"));
										map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
										map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
										maps.add(map);
									}
									//禁用旧料   rs2 获取bom
									Map<Object, Object> new_map = new HashMap<Object, Object>();
									detno = detno + 1;
									edid = baseDao.getSeqId("ECNDETAIL_SEQ");
									new_map.put("ED_ID", edid);
									new_map.put("ED_ECNID", ecnid);
									new_map.put("ED_DETNO", detno);
									new_map.put("ED_BOID", rs1.getObject("bd_bomid"));
									new_map.put("ED_MOTHERCODE", rs1.getObject("bo_mothercode"));
									new_map.put("ED_MOTHERNAME", rs1.getObject("bo_mothername"));
									new_map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
									new_map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
									new_map.put("ED_DIDSTATUSCODE", "OPEN");
									new_map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
									new_map.put("ED_CODE", ECNCode);
									new_map.put("ED_SONCODE", rs1.getObject("bd_SONCODE"));;
									//禁用
									new_map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
									new_map.put("ED_TYPE", "REPDISABLE");
									new_map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
									new_map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
									new_map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
									new_map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
									new_map.put("ED_OLDBASEQTY", rs1.getObject("bd_baseqty"));
									new_map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
									new_map.put("ED_BDDETNO", rs1.getObject("bd_detno"));
									new_map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
									new_map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
									new_map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
									new_map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
									new_map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
									new_map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
									new_map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
									new_map.put("ED_OLDBOMVERSION", rs1.getObject("BO_VERSION"));
									new_map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
									new_map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
									maps.add(new_map);
									
								}
								
								
							}
						
						}
						
						if("AUTO".equals(ECR_KIND)){
							sql = "select * from BOMDETAIL,bom where bo_id=bd_bomid and bd_soncode='" + rs.getString("ecrd_repcode") + "' and nvl(bd_usestatus,' ')<>'DISABLE' " + condition + " and NVL(bd_prstate,' ')<>'是' and (bd_bomid,bd_detno) not in (select ed_boid,ed_bddetno from ECNDetail left join ECN on ed_ecnid = ecn_id where ecn_checkstatuscode='AUDITED' and ecn_type='AUTO' and ed_didstatuscode='OPEN') order by bd_bomid,bd_detno ";
						}else{
							sql = "select * from BOMDETAIL,bom where bo_id=bd_bomid and bd_soncode='" + rs.getString("ecrd_repcode") + "' and nvl(bd_usestatus,' ')<>'DISABLE' " + condition + " and NVL(bd_prstate,' ')<>'是' order by bd_bomid,bd_detno ";
						}
					} else if (rs.getObject("ECRD_TYPE").equals("BATCHREPDISABLE")) {
						if(rs.getObject("ecrd_soncode")!=null && !"".equals(rs.getObject("ecrd_soncode"))){//新料或主料不为空
							//判断当主料存在，判断替代料存不存在
							if(rs.getObject("ECRD_REPCODE")!=null && !"".equals(rs.getObject("ECRD_REPCODE"))){
								sql = "select * from bom,bomdetail,prodreplace where bd_soncode='" + rs.getString("ecrd_soncode") + "'and PRE_REPCODE ='"+rs.getString("ECRD_REPCODE")+"' and bo_id=bd_bomid and bd_id=pre_bdid and  nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and NVL(pre_statuscode,' ')<>'DISABLE' order by bd_bomid,bd_detno ";
							}else{
								sql =  "select * from bom,bomdetail,prodreplace where bd_soncode='" + rs.getString("ecrd_soncode") + "' and bo_id=bd_bomid and bd_id=pre_bdid and  nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and NVL(pre_statuscode,' ')<>'DISABLE' order by bd_bomid,bd_detno ";
							}
						}else{
							sql = "select * from bom,bomdetail,prodreplace where pre_repcode='" + rs.getString("ecrd_repcode") + "' and bo_id=bd_bomid and bd_id=pre_bdid and  nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and NVL(pre_statuscode,' ')<>'DISABLE' order by bd_bomid,bd_detno ";
						}
					} else if (rs.getObject("ECRD_TYPE").equals("BATCHSETMAIN")) {
						sql = "select * from bom,bomdetail,prodreplace where pre_repcode='" + rs.getString("ecrd_soncode") + "' and bo_id=bd_bomid and bd_id=pre_bdid and bd_soncode<>'" + rs.getString("ecrd_soncode") + "' and  nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and NVL(pre_statuscode,' ')<>'DISABLE' order by bd_bomid,bd_detno ";
					} else if (rs.getObject("ECRD_TYPE").equals("BATCHREPADD")) {
						//主料A已经是替代料，但A的替代料还有B
						if(baseDao.isDBSetting("ECR!Check", "RepReplace")){
							//需要判断BOM是否开启   主料和替代料可以相同
							boolean flag = baseDao.isDBSetting("BOM", "checkRepProd");
							String sql_2="";
							//查出替代料A的对应的BOM以及bomdetail
							SqlRowList rs1 = baseDao.queryForRowSet("select * from prodreplace left join bom on pre_bomid = bo_id left join bomdetail on bo_id = bd_bomid and pre_bdid = bd_id where nvl(bd_usestatus,' ')<>'DISABLE' and nvl(pre_status,' ')<>'已禁用' and pre_repcode='"+rs.getString("ecrd_soncode")+"'");
							while(rs1.next()){
								//过滤掉已有替代料B的数据
								int count = baseDao.getCount("select count(*) from prodreplace where nvl(pre_status,' ')<>'已禁用' and pre_bdid="+rs1.getString("pre_bdid")+" and pre_repcode='"+rs.getString("ecrd_repcode")+"'");
								if(count==0){
									sql_2 = "select * from prodreplace where nvl(pre_status,' ')<>'已禁用' and pre_bdid ="+rs1.getString("pre_bdid")+" and nvl(pre_status,' ')<>'已禁用' and not exists (select 1 from bomdetail where bd_id = pre_bdid and bd_soncode ='"+rs.getString("ecrd_repcode")+"')";
									if(flag){
										//可以直接插入替代料B
										detno = detno + 1;
										Map<Object, Object> map = new HashMap<Object, Object>();
										edid = baseDao.getSeqId("ECNDETAIL_SEQ");
										map.put("ED_ID", edid);
										map.put("ED_ECNID", ecnid);
										map.put("ED_DETNO", detno);
										map.put("ED_BOID", rs1.getObject("bd_bomid"));
										map.put("ED_MOTHERCODE", rs1.getObject("bo_mothercode"));
										map.put("ED_MOTHERNAME", rs1.getObject("bo_mothername"));
										map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
										map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
										map.put("ED_DIDSTATUSCODE", "OPEN");
										map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
										map.put("ED_CODE", ECNCode);
										map.put("ED_SONCODE", rs1.getObject("bd_SONCODE"));
										map.put("ED_SONNAME", rs1.getObject("bd_SONNAME"));
										map.put("ED_SONSPEC", rs1.getObject("bd_SONSPEC"));
										map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
										map.put("ED_REPNAME", rs.getObject("ECRD_REPNAME"));
										map.put("ED_REPSPEC", rs.getObject("ECRD_REPSPEC"));
										map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
										map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
										map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
										map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
										map.put("ED_OLDBASEQTY", rs.getObject("ECRD_OLDBASEQTY"));
										map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
										map.put("ED_BDDETNO", rs1.getObject("bd_detno"));
										map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
										map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
										map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
										map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
										map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
										map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
										map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
										map.put("ED_OLDBOMVERSION", rs1.getObject("BO_VERSION"));
										map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
										map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
										map.put("ED_TYPE", "REPADD");
										maps.add(map);
									}else{
										//不允许替代料和主料相同
										SqlRowList rs2 = baseDao.queryForRowSet(sql_2);
										while(rs2.next()){
											detno = detno + 1;
											Map<Object, Object> map = new HashMap<Object, Object>();
											edid = baseDao.getSeqId("ECNDETAIL_SEQ");
											map.put("ED_ID", edid);
											map.put("ED_ECNID", ecnid);
											map.put("ED_DETNO", detno);
											map.put("ED_BOID", rs1.getObject("bd_bomid"));
											map.put("ED_MOTHERCODE", rs1.getObject("bo_mothercode"));
											map.put("ED_MOTHERNAME", rs1.getObject("bo_mothername"));
											map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
											map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
											map.put("ED_DIDSTATUSCODE", "OPEN");
											map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
											map.put("ED_CODE", ECNCode);
											map.put("ED_SONCODE", rs1.getObject("bd_SONCODE"));
											map.put("ED_SONNAME", rs1.getObject("bd_SONNAME"));
											map.put("ED_SONSPEC", rs1.getObject("bd_SONSPEC"));
											map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
											map.put("ED_REPNAME", rs.getObject("ECRD_REPNAME"));
											map.put("ED_REPSPEC", rs.getObject("ECRD_REPSPEC"));
											map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
											map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
											map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
											map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
											map.put("ED_OLDBASEQTY", rs.getObject("ECRD_OLDBASEQTY"));
											map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
											map.put("ED_BDDETNO", rs1.getObject("bd_detno"));
											map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
											map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
											map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
											map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
											map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
											map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
											map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
											map.put("ED_OLDBOMVERSION", rs1.getObject("BO_VERSION"));
											map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
											map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
											map.put("ED_TYPE", "REPADD");
											maps.add(map);
										}
									}
								}
							}
						}
						//主料A是否存在B替代料，不存在就增加替代料
						sql = "select * from BOMDETAIL,bom where bo_id=bd_bomid and bd_soncode='" + rs.getString("ecrd_soncode") + "' and nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and not exists(select 1 from prodreplace where pre_bdid=bd_id and pre_repcode='" + rs.getString("ecrd_repcode") + "' and NVL(pre_statuscode,' ')<>'DISABLE') order by bd_bomid,bd_detno ";
					} else {
						if("AUTO".equals(ECR_KIND)){
							sql = "select * from BOMDETAIL,bom where bo_id=bd_bomid and bd_soncode='" + rs.getString("ecrd_soncode") + "' and nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " and (bd_bomid,bd_detno) not in (select ed_boid,ed_bddetno from ECNDetail left join ECN on ed_ecnid = ecn_id where ecn_checkstatuscode='AUDITED' and ecn_type='AUTO' and ed_didstatuscode='OPEN') order by bd_bomid,bd_detno ";
						}else{
							sql = "select * from BOMDETAIL,bom where bo_id=bd_bomid and bd_soncode='" + rs.getString("ecrd_soncode") + "' and nvl(bd_usestatus,' ')<>'DISABLE' and NVL(bd_prstate,' ')<>'是' " + condition + " order by bd_bomid,bd_detno ";
						}
					}
					SqlRowList rsbom = baseDao.queryForRowSet(sql);
					while (rsbom.next()) {
						detno = detno + 1;
						Map<Object, Object> map = new HashMap<Object, Object>();
						edid = baseDao.getSeqId("ECNDETAIL_SEQ");
						map.put("ED_ID", edid);
						map.put("ED_ECNID", ecnid);
						map.put("ED_DETNO", detno);
						map.put("ED_BOID", rsbom.getObject("bd_bomid"));
						map.put("ED_MOTHERCODE", rsbom.getObject("bo_mothercode"));
						map.put("ED_MOTHERNAME", rsbom.getObject("bo_mothername"));
						map.put("ED_MOTHERSPEC", rs.getObject("ecrd_motherspec"));
						map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
						map.put("ED_DIDSTATUSCODE", "OPEN");
						map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
						map.put("ED_CODE", ECNCode);
						map.put("ED_SONCODE", rs.getObject("ECRD_SONCODE"));
						map.put("ED_SONNAME", rs.getObject("ECRD_SONNAME"));
						map.put("ED_SONSPEC", rs.getObject("ECRD_SONSPEC"));
						map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
						map.put("ED_REPNAME", rs.getObject("ECRD_REPNAME"));
						map.put("ED_REPSPEC", rs.getObject("ECRD_REPSPEC"));
						map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
						map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
						map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
						map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
						map.put("ED_OLDBASEQTY", rs.getObject("ECRD_OLDBASEQTY"));
						map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
						map.put("ED_BDDETNO", rsbom.getObject("bd_detno"));
						map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
						map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
						map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
						map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
						map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
						map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
						map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
						map.put("ED_OLDBOMVERSION", rsbom.getObject("BO_VERSION"));
						map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
						map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
						Object thetype = rs.getObject("ECRD_TYPE");
						if (rs.getObject("ECRD_TYPE").equals("BATCHDISABLE")) {
							thetype = "DISABLE";
							map.put("ED_OLDBASEQTY", rsbom.getObject("bd_baseqty"));
						} else if (rs.getObject("ECRD_TYPE").equals("BATCHSWITCH")) {
							thetype = "SWITCH";
							map.put("ED_OLDBASEQTY", rsbom.getObject("bd_baseqty"));
							map.put("ED_NEWBASEQTY", rsbom.getObject("bd_baseqty"));
							map.put("ED_STEPCODE",  rsbom.getObject("bd_stepcode"));
						} else if (rs.getObject("ECRD_TYPE").equals("BATCHREPDISABLE")) {
							thetype = "REPDISABLE";
							map.remove("ED_SONCODE");
							map.put("ED_SONCODE", rsbom.getObject("bd_soncode"));
							if(rs.getObject("ecrd_soncode")!=null && !"".equals(rs.getObject("ecrd_soncode"))){
								map.put("ED_REPCODE", rsbom.getObject("PRE_REPCODE"));
							}
						} else if (rs.getObject("ECRD_TYPE").equals("BATCHSETMAIN")) {
							thetype = "SWITCH";
							map.put("ED_REPCODE", rsbom.getObject("bd_soncode"));
							map.put("ED_OLDFORREPLACE", -1);
							map.put("ED_OLDBASEQTY", rsbom.getObject("bd_baseqty"));
							map.put("ED_NEWBASEQTY", rsbom.getObject("bd_baseqty"));
							map.put("ED_STEPCODE",  rsbom.getObject("bd_stepcode"));
						} else if (rs.getObject("ECRD_TYPE").equals("BATCHREPADD")) {
							thetype = "REPADD";
						}
						map.put("ED_TYPE", thetype);
						maps.add(map);
						// baseDao.execute(SqlUtil.getInsertSqlByMap(map,
						// "ECNDETAIL"));
						// 复制明细表对应的位号数据
						if (rs.getObject("ECRD_TYPE").equals("ADD") || rs.getObject("ECRD_TYPE").equals("UPDATE")) {
							sql = "select * from ECRDetailLocation where edl_ecrid=" + ecr_id + " and edl_ecrdid=" + rs.getObject("ecrd_id") + " order by edl_id ";
							SqlRowList rslocation = baseDao.queryForRowSet(sql);
							detno = 0;
							while (rslocation.next()) {
								Map<Object, Object> map2 = new HashMap<Object, Object>();
								edlid = baseDao.getSeqId("ECNDETAILLOCATION_SEQ");
								map2.put("EDL_ID", edlid);
								map2.put("EDL_ECNID", ecnid);
								map2.put("EDL_EDID", edid);
								map2.put("EDL_EDDETNO", detno);
								map2.put("EDL_ECNCODE", ECNCode);
								map2.put("EDL_DETNO", rslocation.getObject("EDL_DETNO"));
								map2.put("EDL_TYPE", rslocation.getObject("EDL_TYPE"));
								map2.put("EDL_CODE", rslocation.getObject("EDL_CODE"));
								lmaps.add(map2);
							}
						}
					}
				} else {
					// 指定BOM变更的只需要复制变更内容
					detno = detno + 1;
					Map<Object, Object> map = new HashMap<Object, Object>();
					edid = baseDao.getSeqId("ECNDETAIL_SEQ");
					map.put("ED_ID", edid);
					map.put("ED_ECNID", ecnid);
					map.put("ED_DETNO", detno);
					map.put("ED_BOID", rs.getObject("ecrd_bomid"));
					map.put("ED_MOTHERCODE", rs.getObject("ECRD_MOTHERCODE"));
					map.put("ED_MOTHERNAME", rs.getObject("ECRD_MOTHERNAME"));
					map.put("ED_DIDSTATUS", BaseUtil.getLocalMessage("OPEN"));
					map.put("ED_DIDSTATUSCODE", "OPEN");
					map.put("ED_REMARK", rs.getObject("ECRD_REMARK"));
					map.put("ED_CODE", ECNCode);
					map.put("ED_TYPE", rs.getObject("ECRD_TYPE"));
					map.put("ED_SONCODE", rs.getObject("ECRD_SONCODE"));
					map.put("ED_SONNAME", rs.getObject("ECRD_SONNAME"));
					map.put("ED_SONSPEC", rs.getObject("ECRD_SONSPEC"));
					map.put("ED_REPCODE", rs.getObject("ECRD_REPCODE"));
					map.put("ED_REPNAME", rs.getObject("ECRD_REPNAME"));
					map.put("ED_REPSPEC", rs.getObject("ECRD_REPSPEC"));
					map.put("ED_MOTHERSPEC", rs.getObject("ECRD_MOTHERSPEC"));
					map.put("ED_REFNO", rs.getObject("ECRD_REFNO"));
					map.put("ED_OLDPRODDEAL", rs.getObject("ECRD_OLDPRODDEAL"));
					map.put("ED_OLDVENDDEAL", rs.getObject("ECRD_OLDVENDDEAL"));
					map.put("ED_OLDFORREPLACE", rs.getObject("ECRD_OLDFORREPLACE"));
					map.put("ED_OLDBASEQTY", rs.getObject("ECRD_OLDBASEQTY"));
					map.put("ED_NEWBASEQTY", rs.getObject("ECRD_NEWBASEQTY"));
					map.put("ED_BDDETNO", rs.getObject("ECRD_BDDETNO"));
					map.put("ED_LOCATION", rs.getObject("ECRD_LOCATION"));
					map.put("ED_OLDLOCATION", rs.getObject("ECRD_OLDLOCATION"));
					map.put("ED_EFFECTDATE", rs.getObject("ECRD_EFFECTDATE"));
					map.put("ED_UNEFFECTDATE", rs.getObject("ECRD_UNEFFECTDATE"));
					map.put("ED_BDREMARK", rs.getObject("ECRD_BDREMARK"));
					map.put("ED_ISBATCH", rs.getObject("ECRD_ISBATCH"));
					map.put("ED_UPBOMVERSION", rs.getObject("ECRD_UPBOMVERSION"));
					map.put("ED_STEPCODE", rs.getObject("ECRD_STEPCODE"));
					map.put("ED_OLDBOMVERSION", rs.getObject("BO_VERSION"));
					map.put("ed_oldbdremark2", rs.getObject("ecrd_oldbdremark2"));
					map.put("ed_newbdremark2", rs.getObject("ecrd_newbdremark2"));
					maps.add(map);
					// baseDao.execute(SqlUtil.getInsertSqlByMap(map,
					// "ECNDETAIL"));
					// 复制明细表对应的位号数据
					if (rs.getObject("ECRD_TYPE").equals("ADD") || rs.getObject("ECRD_TYPE").equals("UPDATE")) {
						sql = "select * from ECRDetailLocation where edl_ecrid=" + ecr_id + " and edl_ecrdid=" + rs.getObject("ecrd_id") + " order by edl_id ";
						SqlRowList rslocation = baseDao.queryForRowSet(sql);
						while (rslocation.next()) {
							Map<Object, Object> map2 = new HashMap<Object, Object>();
							edlid = baseDao.getSeqId("ECNDETAILLOCATION_SEQ");
							map2.put("EDL_ID", edlid);
							map2.put("EDL_ECNID", ecnid);
							map2.put("EDL_EDID", edid);
							map2.put("EDL_EDDETNO", detno);
							map2.put("EDL_ECNCODE", ECNCode);
							map2.put("EDL_DETNO", rslocation.getObject("EDL_DETNO"));
							map2.put("EDL_TYPE", rslocation.getObject("EDL_TYPE"));
							map2.put("EDL_CODE", rslocation.getObject("EDL_CODE"));
							lmaps.add(map2);
						}
					}
				}
			}
			
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(maps, "ECNDETAIL"));
			if(maps.size()==0&&Integer.parseInt(resM.get("ecnid").toString())!=0){
				//@zjh 如果没有生成明细 则将update ecr set ecr_turnstatus='无BOM' where ecr_id=? 
				baseDao.execute("update ecr set ecr_turnstatus='无BOM' where ecr_id=?",ecr_id);
				baseDao.execute("delete from ecn where ecn_id=?",resM.get("ecnid"));
				resM.put("error", "无关联BOM!");
				resM.put("ecnid", "");
				return resM;
			}else{
				baseDao.execute("update ecr set ecr_turnstatus='已转ECN' where ecr_id=?",ecr_id);
			}
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(lmaps, "ECNDetailLocation"));
			// 根据参数配置ifDCNChangePR， 是否启用BOM阶段等于DCN的，单据前缀设为DN
			boolean bool = baseDao.isDBSetting("ECN", "ifDCNChangePR");
			if (bool) {
				Object[] data = baseDao.getFieldsDataByCondition("ecn", "ecn_prodstage,ecn_code", "ecn_id=" + ecnid);
				if (data[0] != null && "DCN".equals(data[0])) {
					Object ob = baseDao.getFieldDataByCondition("MAXNUMBERS", "MN_LEADCODE", "mn_tablename='ECN'");
					if (ob != null) {
						String code = data[1].toString().replaceAll(ob.toString(), "DN");
						baseDao.updateByCondition("ECN", "ecn_code='" + code + "'", "ecn_id=" + ecnid);
						baseDao.updateByCondition("ecndetail", "ed_code='" + code + "'", "ed_ecnid=" + ecnid);
					} else {
						baseDao.updateByCondition("ecn", "ecn_code='DN'||'" + data[1].toString() + "'", "ecn_id=" + ecnid);
						baseDao.updateByCondition("ecndetail", "ed_code='DN'||'" + data[1].toString() + "'", "ed_ecnid=" + ecnid);
					}
				}
			}
			try {
				baseDao.execute("update ecndetail set (ed_mothername,ed_motherspec)=(select max(pr_detail),max(pr_spec) from product where pr_code=ed_mothercode) where ed_ecnid=" + ecnid + " and NVL(ed_mothername,' ')=' '");
			} catch (Exception e) {

			}
			// AutoAuditECN
			// ecr评审表加参数：转ECN后自动审核。默认不启用，启用则转ecn后更新状态为已提交并自动执行审核按钮业务
			boolean ifAuditECN = baseDao.isDBSetting("ECR!Check", "AutoAuditECN");
			if (ifAuditECN) {
				baseDao.execute("update ecn set ECN_CHECKSTATUS='" + BaseUtil.getLocalMessage("COMMITED") + "',ECN_CHECKSTATUSCODE='COMMITED' where ecn_id=" + ecnid);
				Object[] datas = baseDao.getFieldsDataByCondition("ECN", new String[] { "ecn_type", "ecn_code" }, "ecn_id=" + ecnid);
				Object type = datas[0];
				baseDao.audit("ECN", "ecn_id=" + ecnid, "ecn_checkstatus", "ecn_checkstatuscode", "ecn_auditdate", "ecn_auditman");
				// 执行立即变更存储过程
				if (type.equals("NOW")) {
					String str = baseDao.callProcedure("SP_DOECN_NOW", new String[] { datas[1].toString() });
					System.out.println("执行立即变更存储过程:"+str);
					if (str != null && !str.trim().equals("")) {
						baseDao.resAudit("ECN", "ecn_id=" + ecnid, "ecn_checkstatus", "ecn_checkstatuscode", "ecn_auditdate", "ecn_auditman");
						resM.put("error", "自动生成的ECN单[" + datas[1].toString() + "]审核失败");
					}
				} else {
					baseDao.execute("update bomdetail set bd_ecncode='待" + datas[1].toString() + "' where (bd_bomid,bd_detno) in (select ed_boid,ed_bddetno from ecndetail where  ed_ecnid=" + ecnid + ")");
				}
			}
			handlerService.handler("ECR!Check", "turn", "after", new Object[] { ecr_id });
		}
		return resM;
	}

	public void UpdateECRTypeCode(int ecr_id, String caller) {
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'增加','ADD') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='增加'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'修改','UPDATE') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='修改'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'禁用','DISABLE') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='禁用'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'替换','SWITCH') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='替换'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'增加替代料','REPADD') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='增加替代料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'禁用替代料','REPDISABLE') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='禁用替代料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'批量禁用','BATCHDISABLE') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='批量禁用'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'批量替换','BATCHSWITCH') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='批量替换'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'批量增加替代料','BATCHREPADD') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='批量增加替代料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'批量禁用替代料','BATCHREPDISABLE') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='批量禁用替代料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'批量设主料','BATCHSETMAIN') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='批量设主料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'增加通用替代料','ADDCOMMONREP') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='增加通用替代料'");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'禁用通用替代料','DISABLECOMMONREP') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='禁用通用替代料'");
		baseDao.execute("update ecrdetail set ecrd_isbatch=case when ecrd_type like 'BATCH%' then -1 else 0 end where ecrd_ecrid='" + ecr_id + "' ");
		baseDao.execute("update ecrdetail set ecrd_type=replace(ecrd_type,'变更描述','CHANGENAME') where ecrd_ecrid='" + ecr_id + "' and ecrd_type='变更描述'");
		try {
			baseDao.execute("update ecrdetail set ecrd_repcode=ecrd_soncode,(ecrd_repname,ecrd_repspec)=(select pr_detail,pr_spec from product where pr_code=ecrd_soncode) where ecrd_ecrid='" + ecr_id + "' and ecrd_type='CHANGENAME'");
		} catch (Exception e) {
			BaseUtil.showError("[变更描述]的操作，因旧料规格、旧料名称字段长度设置不够，旧料规格名称保存失败!'");
		}
		try {
			baseDao.execute("update ecrdetail set (ecrd_sonname,ecrd_sonspec)=(select pr_detail,pr_spec from product where pr_code=ecrd_soncode) where ecrd_ecrid='" + ecr_id + "' and ecrd_type<>'CHANGENAME' ");
		} catch (Exception e) {

		}
	}

	public void CheckECR_ALL(Integer ecr_id, String caller) {
		String SQLStr = "";
		SqlRowList rs;
		// 操作类型不能空
		SQLStr = "select  count(1) c,wm_concat(ecrd_detno)ecrd_detno  from  ecrdetail  where ecrd_ecrid='" + ecr_id + "' and NVL(ecrd_type,' ')=' '";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]必须填写操作类型!");
			}
		}
		// 自然变更类型的操作只能是替换和禁用
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail,ecr  where ecr_id=ecrd_ecrid and ecrd_ecrid='" + ecr_id + "' and (ecrd_type not in ('DISABLE','SWITCH','BATCHSWITCH','BATCHDISABLE') and ecr_kind='AUTO')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]操作类型不正确，自然变更类型的操作只能是替换和禁用!'");
			}
		}
		// 如果是自然切换类型，替换的变更必须新旧用量一致
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail,ecr  where ecr_id=ecrd_ecrid and ecrd_ecrid='" + ecr_id + "' and ecrd_newbaseqty<>ecrd_oldbaseqty and ecrd_type ='SWITCH' and ecr_kind='AUTO'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]替换变更必须新旧用量一致!'");
			}
		}
		// 指定的主料物料和序号是否一致
		SQLStr = "select  count(1)c,wm_concat(ecrd_detno)ecrd_detno from  ECRDETAIL left join bomdetail on bd_bomid=ecrd_bomid and bd_soncode=ecrd_soncode and ecrd_bddetno=bd_detno where ecrd_ecrid=? and (bd_soncode is null and ecrd_type in('DISABLE','UPDATE','REPDISABLE')) and nvl(ecrd_didstatuscode,' ')<>'CLOSE'";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定的主料和序号不对应!");
			}
		}
		// 自然变更判断子件编号ecrd_soncode如果存在BOM,且BOM等级（BOMlevel）是参与MRP运算
		SQLStr = "select count(1) c, wm_concat(ecrd_detno) ecrd_detno from ecrdetail left join ecr on ecrd_ecrid=ecr_id " + "left join bom on bo_mothercode=ecrd_soncode left join bomlevel on bo_level=bl_code left join product on pr_code=bo_mothercode " + "where ecrd_ecrid='" + ecr_id + "' and ecr_kind='AUTO' and nvl(ecrd_soncode,' ')<>' ' and BL_IFMRP <>0 and (bo_ispast=-1 OR pr_supplytype='VIRTUAL')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next() && rs.getInt("c") > 0) {
			BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]是虚拟子件BOM或跳层BOM且参与MRP运算，不允许执行自然变更!");
		}
		//根据物料资料中参数配置：允许物料名称+规格+规格参数重复   检查存在名称、规格一样的物料
		checkProdName(ecr_id);
		// 使用明细行变更描述，判断名称不能为空
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid='" + ecr_id + "' and ecrd_type='CHANGENAME' and NVL(ecrd_sonname,' ')=' '";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("变更描述必须填写子件名称、子件规格!序号[" + rs.getString("ecrd_detno") + "]'");
			}
		}
		// 禁用或修改的物料，必须与BOM序号对应的物料相一致
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail left join bomdetail on bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno where ecrd_ecrid='" + ecr_id + "' and (bd_soncode<>' ' and bd_soncode<>ecrd_soncode and ecrd_type in ('UPDATE','DISABLE'))";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定BOM的序号和对应的子件料号不对应!");
			}
		}
		// 增加操作不能录入BOM序号
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail where ecrd_ecrid='" + ecr_id + "' and ecrd_bddetno>0 and ecrd_type in('ADD','BATCHADD') ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("增加料号不能录入BOM序号!行号[" + rs.getString("ecrd_detno") + "]");
			}
		}
		// 判断变更的序号是否已经被禁用
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail left join bomdetail on bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno where ecrd_ecrid='" + ecr_id + "' and (bd_soncode<>' ' and bd_usestatus='DISABLE' and ecrd_type in ('UPDATE','DISABLE','REPADD','REPDISABLE'))";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定BOM的序号子件已经禁用，不能变更!");
			}
		}
		// 增加替代料判断
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join bomdetail on bd_bomid=ecrd_bomid and bd_soncode=ecrd_soncode and ecrd_bddetno=bd_detno where ecrd_ecrid='" + ecr_id + "' and (bd_soncode is null and ecrd_type='REPADD') and nvl(ecrd_bddetno,0)<>0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定的主料!");
			}
		}
		//BOM+主料料号是否在当前ECN中是增加的类型，并且增加类型的ECN明细行号ed_detno小于增加替代料的行号ed_detno 
		SQLStr="select count(1) c,wm_concat(B.ecrd_detno) ecrd_detno from ecrdetail A left join (select * from ecrdetail where ecrd_type='REPADD') B on A.ecrd_bomid = B.ecrd_bomid and A.ecrd_soncode = B.ecrd_soncode"
				+" and A.ecrd_ecrid = B.ecrd_ecrid where A.ecrd_type='ADD' and A.ecrd_ecrid='"+ecr_id+"' and A.ecrd_detno>B.ecrd_detno";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定的同BOM的主料的增加替代料类型在当前ECR单据里面序号大于新增类型的序号!");
			}
		}
		// 禁用替代料判断
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join bomdetail on bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno and bd_soncode=ecrd_soncode left join prodreplace on pre_bdid=bd_id and pre_repcode=ecrd_repcode  where ecrd_ecrid='" + ecr_id + "' and ((pre_repcode is null or pre_statuscode='DISABLE') and ecrd_type='REPDISABLE')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]指定的替代料不存在或该替代料已禁用!");
			}
		}
		// 新增替代料重复判断
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_repcode where ecrd_ecrid='" + ecr_id + "' and ecrd_type='REPADD' and ecrd_repcode<>' ' and ecrd_repcode in (select pre_repcode from prodreplace,bomdetail where bd_id=pre_bdid and bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno and NVL(pre_statuscode,' ')<>'DISABLE') ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新增的替代料已经在BOM替代料中存在!");
			}
		}
		// 新增物料不能是特征件
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid='" + ecr_id + "' and  (  ecrd_type='ADD' or ecrd_type='BATCHSWITCH') and  NVL(pr_specvalue,' ') in('SPECIFIC','UNSPECI')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新增物料不能是特征件!");
			}
		}
		// 操作类型与批量一致性检测
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail  where ecrd_ecrid='" + ecr_id + "' and ecrd_type like '%BATCH%' and NVL(ecrd_isbatch,0)=0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]操作类型与是否批量不一致!");
			}
		}
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail  where ecrd_ecrid='" + ecr_id + "' and ecrd_type not like '%BATCH%' and NVL(ecrd_isbatch,0)<>0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]操作类型与是否批量不一致!");
			}
		}
		// 变更替代料或者替换必须填写旧料或替代料编号
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail  where ecrd_ecrid=? and ecrd_type in ('SWITCH','BATCHSWITCH','BATCHREPADD','BATCHREPDISABLE','REPADD','REPDISABLE') and NVL(ecrd_repcode,' ')=' ' and NVL(ecrd_soncode,' ')=' ' ";
		rs = baseDao.queryForRowSet(SQLStr, ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]必须填写[旧料或替代料编号]或者[新料或主料编号]!");
			}
		}
		// 变更主料必须填写新料或主料编号
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail  where ecrd_ecrid=? and NVL(ecrd_isbatch,0)<>0 and NVL(ecrd_soncode,' ')=' ' and NVL(ecrd_type,' ') <>'BATCHREPDISABLE' ";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]必须填写[新料或主料编号]!");
			}
		}
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail  where ecrd_ecrid=? and NVL(ecrd_isbatch,0)=0 and ecrd_type<>'CHANGENAME' and (NVL(ecrd_soncode,' ')=' ' or (NVL(ecrd_bomid,0)=0 and ecrd_type not like '%COMMON%')) ";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]必须填写[新料或主料编号]和BOMID!");
			}
		}	
		// 新料或主料的料号必须存在且已审核
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid=? and  ecrd_type in ('ADD','SWITCH','BATCHSWITCH','CHANGENAME') and NVL(pr_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]物料不存在或已禁用!");
			}
		}
		// 新增物料必须在BOM子件中不存在
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail  where ecrd_ecrid=? and NVL(ecrd_isbatch,0)=0 and ecrd_type in ('ADD','SWITCH') and ecrd_bomid>0 and exists (select 1 from bomdetail where bd_soncode=ecrd_soncode and bd_bomid=ecrd_bomid and NVL(bd_stepcode,' ')=NVL(ecrd_stepcode,' ') and NVL(bd_usestatus,' ')<>'DISABLE')";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新增的料号+工序编号已经在BOM中存在!");
			}
		}
		
		// 正常物料新单位用量不能为0
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail left join product on pr_code=ecrd_soncode left join productkind on pr_kind=pk_name where ecrd_ecrid='" + ecr_id + "' and ecrd_type in ('ADD','UPDATE') and nvl(ecrd_newbaseqty,0)=0 "
				+"and ((nvl(pr_xikind,' ')<>' ' and exists (select 1 from productkind pk4 left join productkind pk_sub on pk4.pk_subof = pk_sub.pk_id where pk4.pk_name = pr_xikind and NVL(pk4.pk_ifzeroqty,0)=0 and pk4.pk_level= 4 and pk_sub.pk_name=pr_kind3 and pk_sub.pk_level=3 ) ) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')<>' ' and exists(select 1 from productkind pk3 left join productkind pk_sub on pk3.pk_subof = pk_sub.pk_id where pk3.pk_name =pr_kind3 and NVL(pk3.pk_ifzeroqty,0)=0 and pk3.pk_level= 3 and pk_sub.pk_name=pr_kind2 and pk_sub.pk_level=2)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')<>' ' and exists(select 1 from productkind pk2 left join productkind pk_sub on pk2.pk_subof = pk_sub.pk_id where pk2.pk_name =pr_kind2 and NVL(pk2.pk_ifzeroqty,0)=0 and pk2.pk_level= 2 and pk_sub.pk_name=pr_kind and pk_sub.pk_level=1))"
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')=' ' and nvl(pr_kind,' ')<> ' ' and exists (select 1 from productkind pk1 where pk1.pk_name=pr_kind and NVL(pk1.pk_ifzeroqty,0)=0 and pk1.pk_level= 1)))"
				+ " and ecrd_bomid not in (select bo_id from bom where bo_refbomid>0)";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新单位用量不能为0!");
			}
		}
		// 正常物料新单位用量不能为负数
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid='" + ecr_id + "' and ecrd_type in ('ADD','UPDATE','SWITCH') and nvl(ecrd_newbaseqty,0)<0 and ecrd_bomid not in (select bo_id from bom where bo_refbomid>0) and NVL(pr_putouttoint,0)=0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新单位用量不能为负数!");
			}
		}
		// 新替代料的料号必须存在且已审核
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_repcode where ecrd_ecrid='" + ecr_id + "' and ecrd_repcode<>' ' and ecrd_type in ('REPADD','BATCHREPADD') and NVL(pr_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]【要替代的物料】不存在或已禁用!");
			}
		}
		// 新增物料不能使虚拟特征件或特征件
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid='" + ecr_id + "' and  ecrd_type in ('ADD','SWITCH','BATCHSWITCH') and  NVL(pr_specvalue,' ') in('NOTSPECIFIC','SPECIFIC')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新增的物料不能是虚拟特征件或特征件!");
			}
		}
		// 新增半成品子件判断是否存在有效BOM
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_soncode left join bom on ecrd_soncode=bo_mothercode  where ecrd_ecrid='" + ecr_id + "' and ecrd_type in ('ADD','SWITCH','BATCHSWITCH') and pr_manutype in ('OSMAKE','MAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]新增的物料是制造件，但是未建立有效的BOM!");
			}
		}
		// 替换半成品判断是否存在有效BOM
		SQLStr = "select count(1) c,wm_concat(ecrd_detno)ecrd_detno from  ecrdetail left join product on pr_code=ecrd_repcode left join bom on ecrd_repcode=bo_mothercode  where ecrd_ecrid='" + ecr_id + "' and ecrd_type in ('REPADD','BATCHREPADD') and pr_manutype in ('OSMAKE','MAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]替换的物料是制造件，但是未建立有效的BOM!");
			}
		}
		// 位号个数判断
		SQLStr = "select ecrd_id,ecrd_ecrid,ecrd_detno,ecrd_newbaseqty,ecrd_newbaseqty-NVL(bd_baseqty,0) addqty,NVL(bd_location,' ')bd_location,NVL(ecrd_oldlocation,' ') ecrd_oldlocation,NVL(ecrd_location,' ')ecrd_location,ecrd_bomid from ecrdetail left join bomdetail on  ecrd_bomid=bd_bomid and ecrd_bddetno=bd_detno where ecrd_ecrid=" + ecr_id + " and ecrd_newbaseqty=round(ecrd_newbaseqty) and ((ecrd_type='UPDATE'  and bd_baseqty=round(bd_baseqty) and NVL(bd_location||ecrd_oldlocation||ecrd_location,' ')<>' ') OR (ecrd_type='ADD' and ecrd_location<>' ') OR (ecrd_type='SWITCH' and bd_baseqty=round(bd_baseqty) and NVL(bd_location||ecrd_oldlocation||ecrd_location,' ')<>' ')) ";
		rs = baseDao.queryForRowSet(SQLStr);
		// ECR错误拼接显示
		String NoShelvesNumber = "";
		String ErrorShelvesNumber = "";
		String ExistShelvesNumber = "";
		while (rs.next()) {
			int oldlocqty = rs.getString("bd_location").split(",").length;
			int addqty = rs.getString("ecrd_location").trim().split(",").length;
			int disableqty = rs.getString("ecrd_oldlocation").trim().split(",").length;
			int newqty = rs.getInt("ecrd_newbaseqty");
			if (rs.getString("bd_location") == null || rs.getString("bd_location").trim().equals("")) {
				oldlocqty = 0;
			}
			if (rs.getString("ecrd_location") == null || rs.getString("ecrd_location").trim().equals("")) {
				addqty = 0;
			}
			if (rs.getString("ecrd_oldlocation") == null || rs.getString("ecrd_oldlocation").trim().equals("")) {
				disableqty = 0;
			}
			String bdlocation = rs.getString("bd_location");
			if (rs.getString("ecrd_oldlocation") != null && !rs.getString("ecrd_oldlocation").equals(" ")) {
				String[] Arraydisable = rs.getString("ecrd_oldlocation").trim().split(",");
				bdlocation = "," + bdlocation + ",";
				for (String c : Arraydisable) {
					if (!bdlocation.contains("," + c + ",")) {
						NoShelvesNumber += "序号[" + rs.getString("ecrd_detno") + "]禁用的位号:" + c + "在BOM位号中不存在<br>";
					}
				}
			} else {
				disableqty = 0;
			}
			if (rs.getString("ecrd_location") != null && !rs.getString("ecrd_location").equals(" ")) {
				String[] Arrayadd = rs.getString("ecrd_location").trim().split(",");
				bdlocation = "," + bdlocation + ",";
				for (String c : Arrayadd) {
					if (bdlocation.contains("," + c + ",")) {
						ExistShelvesNumber += "序号[" + rs.getString("ecrd_detno") + "]新增的位号:" + c + " 在BOM位号已经存在<br>";
					}
					// 判断是否跟现有位号重复 先判断是否在本次ecr中禁用，如果不是要禁用的位号则判断是否在BOM中已经存在
					Object obj1 = baseDao.getFieldDataByCondition("ecrdetail left join bomdetail on ecrd_bomid=bd_bomid and ecrd_bddetno=bd_detno", "ecrd_detno", "ecrd_ecrid=" + rs.getString("ecrd_ecrid") + " and ecrd_bomid=" + rs.getString("ecrd_bomid") + " and ((ecrd_type='DISABLE' and NVL(bd_usestatus,' ')<>'DISABLE' and ','||bd_location||',' like '%," + c.trim() + ",%') OR(','||ecrd_oldlocation||',' like '%," + c.trim() + ",%' )) ");
					if (obj1 == null) {
						obj1 = baseDao.getFieldDataByCondition("BOMDetail", "bd_detno", "bd_bomid=" + rs.getString("ecrd_bomid") + " and NVL(bd_usestatus,' ')<>'DISABLE' and ','||bd_location||',' like '%," + c.trim() + ",%' ");
						if (obj1 != null) {
							ExistShelvesNumber += "序号[" + rs.getString("ecrd_detno") + "]新增的位号:" + c + " 在BOM中已经存在<br>";
						}
					}
				}
			} else {
				addqty = 0;
			}
			if (oldlocqty + addqty - disableqty != newqty) {
				ErrorShelvesNumber += rs.getString("ecrd_detno") + ",";
			}
		}
		if (ErrorShelvesNumber != "") {
			// 去掉多余的逗号
			ErrorShelvesNumber = ErrorShelvesNumber.substring(0, ErrorShelvesNumber.length() - 1);
			ErrorShelvesNumber = "序号[" + ErrorShelvesNumber + "]新单位用量跟变更后的位号数量不一致";
		}
		if (ErrorShelvesNumber != "" || NoShelvesNumber != "" || ExistShelvesNumber != "") {
			BaseUtil.showError(NoShelvesNumber + ExistShelvesNumber + ErrorShelvesNumber);
		}
		// 同一个ECR中，母件编号相同且序号相同的的限制；
		SQLStr = "select count(1) cn,wmsys.wm_concat(ecrd_detno) ecrd_detno from (select ecrd_bomid,ecrd_bddetno,count(0) c,wmsys.wm_concat(ecrd_detno)ecrd_detno from ecrdetail where ecrd_ecrid=" + ecr_id + " and ecrd_bomid>0 and ecrd_bddetno>0 and ecrd_type in('DISABLE','UPDATE','SWITCH') group by ecrd_bomid,ecrd_bddetno) where c>1";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]，操作为：修改、禁用、替换时，母件编号相同且序号相同，不允许提交!'");
			}
		}
		// 检查嵌套
		SQLStr = "select count(1) cn,wmsys.wm_concat(ecrd_detno) ecrd_detno from ecrdetail where ecrd_ecrid=" + ecr_id + " and ecrd_bomid>0 and ecrd_soncode=ecrd_mothercode and ecrd_type in('ADD','SWITCH') ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]，新增的子件料号不能与母件料号相同， 不允许提交!'");
			}
		}

		// 同一个ECR中都为增加操作时，同一母件中同新料+工序编号相同的也需要限制；
		SQLStr = "select count(1) cn,wmsys.wm_concat(ecrd_detno)ecrd_detno from (select ecrd_bomid,ecrd_soncode,count(0)c,wmsys.wm_concat(ecrd_detno)ecrd_detno from ecrdetail where ecrd_ecrid=? and ecrd_bomid>0  and ecrd_type in ('ADD','SWITCH') group by ecrd_bomid,ecrd_soncode,ecrd_stepcode) where c>1";
		rs = baseDao.queryForRowSet(SQLStr,ecr_id);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]同一母件中新料+工序编号相同，不允许提交'");
			}
		}
		
		// 判断子件BOM等级和物料等级是否符合母件的BOM等级要求
		ECR_CheckBomLevel(ecr_id);
		// 子件物料如果管控附件,则附件必须存在   反馈 2017100142
		String errProds;
		     errProds = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ecrd_detno) from ecrDetail left join product on ecrd_soncode=pr_code where ecrd_type in('ADD','SWITCH','BATCHSWITCH') and ecrd_ecrid=? and nvl(pr_needattach,0) = -1 and nvl(pr_attach,' ') = ' '",
							String.class, ecr_id);
			if (errProds != null) {
				BaseUtil.showError("明细行:" + errProds+"新增物料没有上传附件资料，不允许提交!");
			}
			errProds = baseDao   //ecrd_repcode
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ecrd_detno) from ecrDetail left join product on ecrd_repcode=pr_code where ecrd_type in( 'REPADD','BATCHREPADD') and ecrd_ecrid=? and nvl(pr_needattach,0) = -1 and nvl(pr_attach,' ') = ' '",
							String.class, ecr_id);
			if (errProds != null) {
				BaseUtil.showError("明细行:" + errProds+"新增物料没有上传附件资料，不允许提交!");
			}
	}

	/**
	 * 判断子件BOM等级和物料等级是否符合母件的BOM等级要求
	 * 
	 * @param ecr_id
	 */
	public void ECR_CheckBomLevel(Integer ecr_id) {
		String SQLStr = "";
		SqlRowList rs1, rs, rs0;
		String newcode = "";
		// 判断新料的BOM等级不能低于母件的BOM等级，新料的物料等级必须符合母件的BOM等级要求
		SQLStr = "select ecrd_detno,ecrd_bomid,ecrd_soncode,ecrd_repcode,ecrd_type,nvl(bo_level,'') bo_level from  ecrdetail left join bom on ecrd_bomid=bo_id left join bomdetail on bd_bomid=ecrd_bomid and bd_detno=ecrd_bddetno left join prodreplace on pre_bdid=bd_id and pre_repcode=ecrd_repcode  where ecrd_ecrid='" + ecr_id + "' and ecrd_type in ('SWITCH','ADD','REPADD') ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			Object bolevel;
			int bo_id = rs.getInt("ecrd_bomid");
			bolevel = rs.getGeneralString("bo_level");
			newcode = rs.getString("ecrd_soncode");
			if (rs.getString("ecrd_type").equals("REPADD")) {
				newcode = rs.getString("ecrd_repcode");
			}
			if (bolevel == null || bolevel.equals("")) {
				continue;
			}
			Object isExist = baseDao.getFieldDataByCondition("BOMlevel", "NVL(bl_grade,0)", "bl_code='" + bolevel.toString() + "'");
			if(isExist == null || "".equals(isExist.toString())){
				BaseUtil.showError("序号"+rs.getString("ecrd_detno")+":BOMID["+bo_id+"]对应的BOM等级["+bolevel+"]不存在");
			}
			// 新增子件BOM等级判断
			int bl_grade = Integer.parseInt(isExist.toString());
			rs0 = baseDao.queryForRowSet("select count(1) num from product left join bom on bo_mothercode=pr_code left join bomlevel on bl_code=bo_level where pr_code='" + newcode + "' and bl_grade<" + bl_grade);
			if (rs0.next()) {
				if (rs0.getInt("num") > 0) {
					BaseUtil.showError("新增的子件的BOM等级不能低于母件的等级，序号：" + rs.getString("ecrd_detno") + "");
				}
			}
			// 判断物料等级是否满足BOM等级要求
			SQLStr = "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid  where bl_code='" + bolevel.toString() + "'  ";
			rs1 = baseDao.queryForRowSet(SQLStr);
			if (rs1.next()) {
				if (rs1.getInt("disnum") > 0) {
					// 判断是否有禁用的物料等级
					rs0 = baseDao.queryForRowSet("select count(1) num from  product where pr_code='" + newcode + "' and pr_level<>' ' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='" + bolevel.toString() + "') ");
					if (rs0.next()) {
						if (rs0.getInt("num") > 0) {
							BaseUtil.showError("序号：" + rs.getString("ecrd_detno") + "新增的物料优选等级在BOM等级定义里面被禁用");
						}
					}
				}
				if (rs1.getInt("allnum") > 0 && rs1.getInt("disnum") == 0) {
					// 判断是否有物料等级达到要求等级
					rs0 = baseDao.queryForRowSet("select count(1) num from product  where pr_code='" + newcode + "' and pr_level<>' ' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='" + bolevel.toString() + "')  ");
					if (rs0.next()) {
						if (rs0.getInt("num") > 0) {
							BaseUtil.showError("序号：" + rs.getString("ecrd_detno") + "新增的物料优选等级还没有到达BOM等级要求");
						}
					}
				}
			}
		}
	}
	
	//检查物料名称+规格+规格参数是否重复
	private void checkProdName(Integer ecr_id) {
		//获取物料资料中的参数配置: 允许物料名称+规格+规格参数重复。     反馈编号：2017120082   @author:lidy
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		// 变更名称和规格判断不能跟现有物料的名称规格重复
		String SQLStr = "select ecr_prodcode,ecr_newprodname,ecr_newspec,ecr_newspeccs,pr_speccs,pr_id from  ecr left join product on pr_code=ecr_prodcode where ecr_id='" + ecr_id + "' and pr_id>0 and (nvl(ecr_newprodname,' ')<>' ' or nvl(ecr_newspec,' ')<>' ')  ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			// 判断名称规格是否存在重复
			SqlRowList rs0 =  baseDao.queryForRowSet("select pr_code from product where pr_id<>? and  nvl(pr_detail,' ')=? and nvl(pr_spec,' ')=? and nvl(pr_speccs,' ')=? and pr_statuscode<>'DISABLE'",rs.getString("pr_id"),StringUtil.nvl(rs.getString("ecr_newprodname"), " "),StringUtil.nvl(rs.getString("ecr_newspec"), " "),StringUtil.nvl(rs.getString("ecr_newspeccs"), " "));
		    if (rs0.next()) {
		    	if(checkProdName==null||"0".equals(checkProdName)){
					BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + rs0.getString("pr_code") + "<hr>");
				}else{
					BaseUtil.appendError("在物料资料中已经存在相同规格名称的物料，物料编号:" + rs0.getString("pr_code") + "<hr>");
				}
			}
			// 判断名称规格是否存在重复
		    rs0 = baseDao.queryForRowSet("select pre_thisid from PreProduct where NVL(pre_code,' ')<> ? AND nvl(pre_detail,' ')= ? and nvl(pre_spec,' ')= ? and nvl(pre_parameter,' ')= ? ",StringUtil.nvl(rs.getString("ecr_prodcode"), " "),StringUtil.nvl(rs.getString("ecr_newprodname"), " "),StringUtil.nvl(rs.getString("ecr_newspec"), " "),StringUtil.nvl(rs.getString("ecr_newspeccs"), " "));
			if (rs0.next()) {
				if(checkProdName==null||"0".equals(checkProdName)){
					BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + rs0.getString("pre_thisid") + "<hr>");
				}else{
					BaseUtil.appendError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + rs0.getString("pre_thisid") + "<hr>");
				}
			}
		}
		// 使用明细行变更描述，判断名称和规格判断不能跟现有物料的名称规格重复
		SQLStr = "select ecrd_detno,ecrd_soncode,ecrd_sonname,ecrd_sonspec,pr_id,ecrd_id from ecrdetail left join product on pr_code=ecrd_soncode where ecrd_ecrid='" + ecr_id + "' and ecrd_type='CHANGENAME'  ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			// 判断名称规格是否存在重复
			SqlRowList rsPro = baseDao.queryForRowSet("select pr_code from Product where pr_id <> ? AND nvl(pr_detail,' ')=? and nvl(pr_spec,' ')= ? and pr_statuscode<>'DISABLE'",rs.getString("pr_id"),StringUtil.nvl(rs.getString("ecrd_sonname")," "),StringUtil.nvl(rs.getString("ecrd_sonspec"), " "));
			if(rsPro.next()){		
				if(checkProdName==null||"0".equals(checkProdName)){
					BaseUtil.showError("序号:"+rs.getInt("ecrd_detno")+"在物料资料中已经存在相同规格名称的物料，物料编号:" + rsPro.getString("pr_code") + "<hr>");
				}else{
					BaseUtil.appendError("序号:"+rs.getInt("ecrd_detno")+"在物料资料中已经存在相同规格名称的物料，物料编号:" + rsPro.getString("pr_code") + "<hr>");
				}
			}
			// 判断名称规格是否存在重复
			rsPro = baseDao.queryForRowSet("select pre_thisid  from PreProduct where NVL(pre_code,' ') <> ? AND nvl(pre_detail,' ')= ? and nvl(pre_spec,' ')= ? ",StringUtil.nvl(rs.getString("ecrd_soncode"), " "),StringUtil.nvl(rs.getString("ecrd_sonname"), " "),StringUtil.nvl(rs.getString("ecrd_sonspec"), " ")   );
			if (rsPro.next()) {
				if(checkProdName==null||"0".equals(checkProdName)){
					BaseUtil.showError("序号:"+rs.getInt("ecrd_detno")+"在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + rsPro.getString("pre_thisid") + "<hr>");
				}else{
					BaseUtil.appendError("序号:"+rs.getInt("ecrd_detno")+"在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + rsPro.getString("pre_thisid") + "<hr>");
				}
			}
		}
	}

	@Override
	public void endCheck(int ecr_id, String caller) {
		// 只能对状态为[在录入]的表单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("ecr", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError("当前单据状态不是【在录入】，请确认");
		}
		baseDao.execute("update ecr set ecr_checkstatus2 ='已结案' , ecr_checkstatus2code='FINISH' where ecr_id=" + ecr_id);
		// 记录操作
		baseDao.logger.end(caller, "ecr_id", ecr_id);
	}

	@Override
	public void resEndCheck(int ecr_id, String caller) {
		// 只能对状态为[在录入]的表单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("ecr", "ecr_checkstatus2code", "ecr_id=" + ecr_id);
		if(!status.equals("FINISH")){
			BaseUtil.showError("当前单据状态不是【已结案】，请确认");
		}
		baseDao.execute("update ecr set ecr_checkstatus2 ='在录入' , ecr_checkstatus2code='ENTERING' where ecr_id=" + ecr_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "ecr_id", ecr_id);
		
	}

}
