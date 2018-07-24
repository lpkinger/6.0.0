package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.BOMService;

import net.sf.json.JSONObject;

@Service("BOMService")
public class BOMServiceImpl implements BOMService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBOM(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOM", "bo_code='" + store.get("bo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}	
		// //保存Detail
		String mothercode = store.get("bo_mothercode").toString();
		String bo_id = store.get("bo_id").toString();
		//判断是否已经存在相同母件的BOM 
		SqlRowList rs = baseDao.queryForRowSet("SELECT  bo_id FROM bom where bo_mothercode='"+mothercode+"' and bo_id <>'"+bo_id+"' and bo_mothercode<>' '");
		if (rs.next()) {
			BaseUtil.showError("此母件料号已存在其他BOM，BOMID:"+"<a href=\"javascript:openUrl('jsps/pm/bom/BOM.jsp?formCondition=bo_idIS"
					+ rs.getInt("bo_id") + "&gridCondition=bd_bomidIS" + rs.getInt("bo_id") + "')\">"  + rs.getInt("bo_id") + "</a>&nbsp;<hr/>"+"不能重复录入");
		}
		List<String> repSql = new ArrayList<String>();
		for (Map<Object, Object> map : grid) {
			if (mothercode.equals(map.get("bd_soncode"))) {
				BaseUtil.showError("BOM清单里面不允许出现母件编号（" + mothercode + "）,明细行:" + map.get("bd_detno"));
			}
			map.put("bd_id", baseDao.getSeqId("BOMDETAIL_SEQ"));
			map.put("bd_bomid", store.get("bo_id"));
			map.put("bd_mothercode", store.get("bo_mothercode"));
			map.put("bd_motherid", store.get("bo_id"));
			//判断是否存在bd_repcode 替代料编号，如果有，判断是否替代料编号是否存在
		    Object rep_code = map.get("bd_repcode");
		    if(StringUtil.hasText(rep_code)){
		    	rep_code = rep_code.toString().replace("，", ",").replace(" ", "");
		    	checkRepCode(rep_code.toString().split(","),map,repSql);
		    	map.remove("bd_repcode");
				map.put("bd_repcode",rep_code);
		    }
			Object bd_location=map.get("bd_location");
			if (bd_location!=null){
				bd_location=bd_location.toString().replace("，", ",").replace(" ", "");//替换位号的全角逗号为半角,空格替换
			}
			map.remove("bd_location");
			map.put("bd_location",bd_location);
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave("BOM", new Object[] {store,grid});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BOMDetail");
		baseDao.execute(gridSql);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOM", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//插入替代料
		baseDao.execute(repSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "bo_id", store.get("bo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//设置从表倒数
		baseDao.execute("update bomdetail set bd_baseqtyback=(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) where bd_bomid=? and bd_baseqtyback<>(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) ", store.get("bo_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("BOM",new Object[] {store,grid});
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式 
			baseDao.execute("update Bom set bo_craftcode=(select max(cr_Code) from craft where cr_prodcode=bo_mothercode) where nvl(bo_craftcode,' ')=' ' and bo_id=" + store.get("bo_id"));
		}
		baseDao.execute("update Bom set bo_updatedate=sysdate, bo_updateman='" +SystemSession.getUser().getEm_name() +"' where bo_id=" + store.get("bo_id"));
		sortBomdetailDetno(store.get("bo_id"));
	}

	@Override
	public void deleteBOM(int bo_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		baseDao.delCheck("BOM", bo_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("BOM",new Object[] { bo_id});
		// 删除
		baseDao.deleteById("BOM", "bo_id", bo_id);
		// 删除Detail
		baseDao.deleteById("BOMdetail", "bd_bomid", bo_id);
		// 记录操作
		baseDao.logger.delete(caller, "bo_id", bo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("BOM",new Object[] { bo_id});
	}

	@Override
	public void deleteDetail(int bd_id, String caller) {
		baseDao.deleteById("BOMdetail", "bd_id", bd_id);
		baseDao.deleteById("prodreplace", "pre_bdid", bd_id);
	}

	@Override
	public void updateBOMById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		String mothercode = store.get("bo_mothercode").toString();
		//判断是否已经存在相同母件的BOM 
		SqlRowList rs = baseDao.queryForRowSet("SELECT  bo_id FROM bom where bo_mothercode='"+mothercode+"' and bo_mothercode<>' ' and bo_id<>"+store.get("bo_id"));
		if (rs.next()) {
			BaseUtil.showError("此母件料号已存在其他BOM，BOMID:"+"<a href=\"javascript:openUrl('jsps/pm/bom/BOM.jsp?formCondition=bo_idIS"
					+ rs.getInt("bo_id") + "&gridCondition=bd_bomidIS" + rs.getInt("bo_id") + "')\">"  + rs.getInt("bo_id") + "</a>&nbsp;<hr/>"+"不能重复录入");
		} 
		// 修改
		store.put("bo_updateman", SystemSession.getUser().getEm_name());
		store.put("bo_updatedate", DateUtil.currentDateString(null));
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BOMDetail", "bd_id");;
		List<String> repSql = new ArrayList<String>();
		for (Map<Object, Object> s : gstore) {
			Object bd_location=s.get("bd_location");
			if (bd_location!=null){
				bd_location=bd_location.toString().replace("，", ",").replace(" ", "");//替换位号的全角逗号为半角
			}
			s.remove("bd_location");
			s.put("bd_location",bd_location);
			if (mothercode.equals(s.get("bd_soncode"))) {
				BaseUtil.showError("BOM清单里面不允许出现母件编号（" + mothercode + "）,明细行:" + s.get("bd_detno"));
			}
			if (s.get("bd_id") == null || s.get("bd_id").equals("") || s.get("bd_id").equals("0")
					|| Integer.parseInt(s.get("bd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("bd_id", baseDao.getSeqId("BOMDETAIL_SEQ"));
				s.put("bd_bomid", store.get("bo_id"));
				s.put("bd_mothercode", store.get("bo_mothercode"));
				s.put("bd_motherid", store.get("bo_id"));
				//判断是否存在bd_repcode 替代料编号，如果有，判断是否替代料编号是否存在
			    Object rep_code = s.get("bd_repcode");
			    if(StringUtil.hasText(rep_code)){
			    	rep_code = rep_code.toString().replace("，", ",").replace(" ", "");	
			    	checkRepCode(rep_code.toString().split(","),s,repSql);
			    	s.remove("bd_repcode");
					s.put("bd_repcode",rep_code);
			    }
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMDetail", new String[] {}, new Object[] {});
				gridSql.add(sql);
			}else{
				 Object rep_code = s.get("bd_repcode");
				 if(StringUtil.hasText(rep_code)){
			    	 //判断替代料号是否改变
			    	 Object cd =  baseDao.getFieldDataByCondition("bomdetail", "bd_id", "bd_id="+s.get("bd_id")+" and bd_repcode='"+rep_code+"'");
			    	 if(cd == null){
				    	 rep_code = rep_code.toString().replace("，", ",").replace(" ", "");		
				    	 checkRepCode(rep_code.toString().split(","),s,repSql);
				    	 s.remove("bd_repcode");
						 s.put("bd_repcode",rep_code);
			    	 }
			     } 
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave("BOM", new Object[] {store,gstore});
		// 修改Detail 
		baseDao.execute(gridSql);
		//插入替代料
		baseDao.execute(repSql);
		// 保存BOM
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOM", "bo_id");
		baseDao.execute(formSql);
		baseDao.updateByCondition("BomDetail", "bd_mothercode='"+store.get("bo_mothercode")+"'", "bd_bomid="+store.get("bo_id"));
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式 
			baseDao.execute("update Bom set bo_craftcode=(select max(cr_Code) from craft where cr_prodcode=bo_mothercode) where nvl(bo_craftcode,' ')=' ' and bo_id=" + store.get("bo_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "bo_id", store.get("bo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("BOM", new Object[] { store, gstore});
		sortBomdetailDetno(store.get("bo_id"));
	}

	@Override
	public void auditBOM(int bo_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		checkProductAndRepKind(bo_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("BOM", new Object[] { bo_id});
		// 执行审核操作
		baseDao.audit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode","bo_auditdate","bo_auditman");
		//通过执行多级反查判断嵌套
		Object code = baseDao.getFieldDataByCondition("BOM", "bo_mothercode", "bo_id=" + bo_id);
		String errProds = baseDao.callProcedure("MM_BomMutiBack", new Object[] { code,SystemSession.getUser().getEm_id()});
		if (errProds != null && errProds.length() > 0) {
			BaseUtil.showError(errProds);
		}else{
			errProds = baseDao
				.getJdbcTemplate()
				.queryForObject("select wm_concat(distinct bm_mothercode) from bommutiback where bm_mothercode='"+code+"' and bm_emid="+SystemSession.getUser().getEm_id()+" and bm_prcode='"+code+"'",String.class);
			if(errProds != null){
				BaseUtil.showError("BOM中存在嵌套["+errProds+"]");
			}
		}
		//更新bomdetail 倒数bd_baseqtyback
		baseDao.execute("update bomdetail set bd_baseqtyback=(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) where bd_bomid=? and bd_baseqtyback<>(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) ", bo_id);
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式 刷新工艺路线展开 
			baseDao.execute("update craft set cr_boid=(select max(bo_id) from bom where bo_mothercode=cr_prodcode) where cr_prodcode='"+code+"'");
			baseDao.callProcedure("MM_SETPRODBOMSTRUCT",new Object[] { bo_id,null });
		}
		// 记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("BOM",new Object[] { bo_id});
	}

	@Override
	public void resAuditBOM(int bo_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		handlerService.beforeResAudit("BOM",new Object[] { bo_id});
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resAuditOnlyAudit(status);
		//反审核的关联业务数据判断
		baseDao.resAuditCheck("BOM", bo_id);
		/**
		 * @todo 新增如果BOM 存在 在录入，或者已提交，并且明细状态是打开的立即切换 或者 存在（在录入，已提交 ，已审核未执行）自然变更类型的，
		 * @author XiaoST  2016年12月13日 下午6:25:32
		 */
		SqlRowList rs = baseDao.queryForRowSet("select WM_CONCAT(ECN_CODE) code,count(1)cn from ecn left join ecndetail on ed_ecnid=ecn_id where ed_boid=? and ((ecn_checkstatuscode in('ENTERING','COMMITED') AND ECN_TYPE='NOW') OR ECN_TYPE='AUTO') AND NVL(ECN_DIDSTATUSCODE,'OPEN')='OPEN' and NVL(ed_didstatus,' ') in ('打开',' ') and rownum<10",bo_id);
		if(rs.next() && rs.getInt("cn")>0){
			BaseUtil.showError("BOM存在关联的打开ECN，不允许反审核，ECN单号:"+rs.getString("code"));
		}
		// 执行反审核操作
		baseDao.resAudit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditman", "bo_auditdate");
		baseDao.execute("delete from bomstruct where bs_topbomid in (select bs_topbomid from bomstruct A where A.bs_sonbomid=?)",bo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bo_id", bo_id);
		handlerService.afterResAudit("BOM",new Object[] { bo_id});
	}

	@Override
	public void submitBOM(int bo_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//更新bomdetail bd_location 去掉空格L1, L2 -->L1,L2
		baseDao.execute("update bomdetail set bd_location=replace(bd_location,' ','') where bd_bomid="+bo_id);
		JudgeExtendBom(bo_id);
		// 母件料号必须是已审核 
		Object code = baseDao.getFieldDataByCondition("BOM", "bo_mothercode", "bo_id=" + bo_id);
		status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + code + "'");
		if (status==null|| !"AUDITED".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + code
					+ "')\">" + code + "</a>&nbsp;");
		}
		//BOM中没有明细也可以提交
		String bo = baseDao.getDBSetting(caller,"BOMAllowNoSon");
		if(bo == null || bo.equals("0")){
			int cn = baseDao.getCount("select count(1) cn from bomdetail where bd_bomid="+bo_id);
			if(cn == 0){
				BaseUtil.showError("明细行没有数据，不允许提交!");
			}
		}
		//通过执行多级反查判断嵌套
		String errProds = baseDao.callProcedure("MM_BomMutiBack", new Object[] { code,SystemSession.getUser().getEm_id()});
		if (errProds != null && errProds.length() > 0) {
			BaseUtil.showError(errProds);
		}else{
			errProds = baseDao
				.getJdbcTemplate()
				.queryForObject("select wm_concat(distinct bm_mothercode) from bommutiback where bm_mothercode='"+code+"' and bm_emid="+SystemSession.getUser().getEm_id()+" and bm_prcode='"+code+"'",String.class);
			if(errProds != null){
				BaseUtil.showError("BOM中存在嵌套["+errProds+"]");
			}
		}
		// 子件料号必须是已审核 
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bd_detno) from bomdetail left join product on bd_soncode=pr_code where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' and nvl(pr_statuscode,' ')<>'AUDITED'",
						String.class, bo_id);
		if (errProds != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + ",行:" + errProds);
		}
		// 子件物料如果管控附件,则附件必须存在   反馈 2017090458
		
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bd_detno) from bomdetail left join product on bd_soncode=pr_code where bd_bomid=? and pr_needattach = -1 and nvl(pr_attach,' ') = ' '",
						String.class, bo_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"物料没有上传附件资料,不允许提交!");
		}
		// 替代料不能等于母件料号
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bd_detno) from bomdetail inner join prodreplace on bd_id=pre_bdid where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' and pre_repcode='"+code+"' ",
						String.class, bo_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的替代料等于母件料号");
		}
		// 替代料必须是已审核的物料
		errProds = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bd_detno) from bomdetail inner join prodreplace on bd_id=pre_bdid left join product on pre_repcode=pr_code where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE'  and nvl(pr_statuscode,' ')<>'AUDITED'",
						String.class, bo_id);
		if (errProds != null) {
			BaseUtil.showError("行:" + errProds+"的替代料不是已审核物料");
		}
		boolean isCheck = baseDao.isDBSetting(caller, "checkRepProd");
		if(!isCheck){
			// 替代料不能等于其它子件料号
			errProds = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(bd_detno) from bomdetail inner join prodreplace on bd_id=pre_bdid  where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE'  and pre_repcode in (select bd_soncode from bomdetail t where t.bd_bomid="+bo_id+" and NVL(t.bd_usestatus,' ')<>'DISABLE' and t.bd_detno<>bomdetail.bd_detno) ",
							String.class, bo_id);
			if (errProds != null) {
				BaseUtil.showError("行:" + errProds+"的替代料与BOM的子件料号相同");
			}
			// 替代料不能等于其它子件的替代料号
			errProds = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(bd_detno) from bomdetail inner join prodreplace on bd_id=pre_bdid where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE' and pre_repcode in (select pre_repcode from bomdetail t inner join prodreplace m on pre_bdid=t.bd_id where t.bd_bomid="+bo_id+" and NVL(m.pre_statuscode,' ')<>'DISABLE' and NVL(t.bd_usestatus,' ')<>'DISABLE' and t.bd_detno<>bomdetail.bd_detno) ",
							String.class, bo_id);
			if (errProds != null) {
				BaseUtil.showError("行:" + errProds+"的替代料相同");
			}
		}
		
		// 子件特征项必须都在母件特征项中存在 
		String SQLStr = "SELECT  * FROM BomDetail left join ProdFeature A on A.pf_prodcode=bd_soncode "
				+ "WHERE bd_bomid='"
				+ bo_id
				+ "'  and nvl(bd_usestatus,' ')<>'DISABLE' and pf_fecode<>' ' and pf_fecode not in (select pf_fecode from ProdFeature B where B.pf_prodcode='"
				+ code + "') ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			BaseUtil.showError("序号[" + rs.getString("bd_detno") + "],特征ID[" + rs.getString("pf_fecode")
					+ "]在母件特征中不存在,不能提交!'");
		} 
		// 判断子件属于虚拟特征件的是否都有特征项
		SQLStr = "SELECT  wmsys.wm_concat(bd_soncode) as soncode,count(1) n FROM BomDetail left join product on pr_code=bd_soncode where bd_bomid='" + bo_id + "' and pr_specvalue='NOTSPECIFIC' and pr_id not in (select pf_prid from prodfeature )  ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("子件编号："+ rs.getString("soncode")+"是虚拟特征件，但未定义特征项,不能提交!'");
			} 
		}
		// 判断是否已经存在相同母件的BOM
		SQLStr = "SELECT  bo_id FROM bom where bo_mothercode in (select bo_mothercode from bom S where S.bo_id="+bo_id+") and bo_id <> '"+bo_id+"'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			BaseUtil.showError("此母件料号已存在其他BOM，BOMID:"+"<a href=\"javascript:openUrl('jsps/pm/bom/BOM.jsp?formCondition=bo_idIS"
					+ rs.getInt("bo_id") + "&gridCondition=bd_bomidIS" + rs.getInt("bo_id") + "')\">"  + rs.getInt("bo_id") + "</a>&nbsp;<hr/>"+"不能重复录入");
		}
		SQLStr = "SELECT  count(1) num FROM bom where bo_id=" + bo_id + " and bo_wccode not in (select wc_code from workcenter where wc_code<>' ')";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num")>0){
				BaseUtil.showError("工作中心未填写或不正确");
			} 
		}
		
		// 判断子件编号是否重复
		boolean isAllowRepeat = baseDao.isDBSetting(caller, "allowRepeat");
		if(!isAllowRepeat){
			/*
			 * @todo 20171128 修改限制 子件编号+工序编号不允许重复,
			 * '子件编号:'||bd_soncode||'+工序编号:'||bd_stepcode||重复',
			 */
			rs = baseDao.queryForRowSet("select LOB_CONCAT(error)ers,count(1)cn from (select '行：'||wm_concat(bd_detno)||'<br>' error from bomdetail where bd_bomid=? and NVL(bd_usestatus,' ')<>'DISABLE' group by bd_soncode,bd_stepcode having count(*)>1)",bo_id);
			if(rs.next() && rs.getInt("cn")>0) {
				BaseUtil.showError("子件编号+工序编号重复,"+rs.getString("ers"));
			}
		}
		String errstr="";
		String bomError="";
		// 不是变形BOM,BOM明细的单位用量不能为负数
		SQLStr = "select wm_concat(bd_detno) bd_detno from bomdetail left join bom on bd_bomid=bo_id "
				+"left join product on bd_soncode = pr_code"
				+ " where bd_bomid='" + bo_id + "' and nvl(bd_baseqty,0)<0 and nvl(bo_isextend,0)=0 and NVL(pr_putouttoint,0)=0";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()&&rs.getString("bd_detno")!=null) {
			BaseUtil.showError("子件单位用量不能是负数,序号[" + rs.getString("bd_detno") + "],不能提交!");
		}
		// 判断位号是否跟用量一致
		SQLStr = "select bd_detno,bd_soncode,bd_location,bd_baseqty,abs(bd_baseqty) abs_bd_baseqty,bo_refbomid from bom left join bomdetail on bd_bomid=bo_id where bd_bomid=" + bo_id
				+ " and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(bd_location,' ')<>' ' order by bd_detno";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			if (rs.getFloat("abs_bd_baseqty") - Math.round(rs.getFloat("abs_bd_baseqty")) == 0) {
				int num=0;
				String[] Arraydisable = rs.getString("bd_location").replace(" ", "").split(",");
				int bd_detno = rs.getInt("bd_detno");
				num=Arraydisable.length ;
				if (num!= rs.getInt("abs_bd_baseqty")) {
					errstr+="<hr>序号:"+bd_detno+" 用量["+rs.getString("bd_baseqty")+"] 位号个数["+num+"]";
				}else if(rs.getGeneralDouble("bd_baseqty")<0){//判断变型BOM的位号是否在原型BOM中
				   SqlRowList rs1 = baseDao.queryForRowSet("select bd_location from bom left join bomdetail on bd_bomid=bo_id where bo_id=? and bd_soncode=?",rs.getObject("bo_refbomid"),rs.getObject("bd_soncode"));
				   if(rs1.next()){
					    String bdlocations = "," + rs1.getString("bd_location") + ",";
						for (String c : Arraydisable) {
							if (!bdlocations.contains("," + c + ",")) {
								bomError += "序号[" + bd_detno + "]的位号:" + c + "在原型BOM位号中不存在<hr>";
							}
						} 
				   }
				  				   	
				}	
			}								
		}
		if (!errstr.equals("")){
			BaseUtil.showError("单位用量和位号个数不匹配"+errstr+"<hr>"+bomError);
		}else if(!bomError.equals("")){
			BaseUtil.showError(bomError);
		}
		// 单位用量不能为0
		SQLStr = "select wmsys.wm_concat(bd_detno) as detno,count(*) n  from bomdetail left join product on pr_code=bd_soncode left join productkind on pr_kind=pk_name where bd_bomid=" + bo_id
				+ " and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(bd_baseqty,0)=0 "
				+"and ((nvl(pr_xikind,' ')<>' ' and exists (select 1 from productkind pk4 left join productkind pk_sub on pk4.pk_subof = pk_sub.pk_id where pk4.pk_name = pr_xikind and NVL(pk4.pk_ifzeroqty,0)=0 and pk4.pk_level= 4 and pk_sub.pk_name=pr_kind3 and pk_sub.pk_level=3 ) ) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')<>' ' and exists(select 1 from productkind pk3 left join productkind pk_sub on pk3.pk_subof = pk_sub.pk_id where pk3.pk_name =pr_kind3 and NVL(pk3.pk_ifzeroqty,0)=0 and pk3.pk_level= 3 and pk_sub.pk_name=pr_kind2 and pk_sub.pk_level=2)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')<>' ' and exists(select 1 from productkind pk2 left join productkind pk_sub on pk2.pk_subof = pk_sub.pk_id where pk2.pk_name =pr_kind2 and NVL(pk2.pk_ifzeroqty,0)=0 and pk2.pk_level= 2 and pk_sub.pk_name=pr_kind and pk_sub.pk_level=1)) "
				+"or (nvl(pr_xikind,' ')=' ' and nvl(pr_kind3,' ')=' ' and nvl(pr_kind2,' ')=' ' and nvl(pr_kind,' ')<> ' ' and exists (select 1 from productkind pk1 where pk1.pk_name=pr_kind and NVL(pk1.pk_ifzeroqty,0)=0 and pk1.pk_level= 1)))";
				rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("序号:" + rs.getString("detno") + " 单位用量0,不能提交,只有物料种类中设置了允许BOM零用量的物料才允许用量为0!");
			}
		}
		// 判断主料和替代料不能重复
		SQLStr = "select wmsys.wm_concat(bd_detno) as detno,count(bd_id) n  from bomdetail left join prodreplace on bd_id=pre_bdid where bd_bomid=" + bo_id
				+ " and bd_soncode=pre_repcode and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("序号:" + rs.getString("detno") + " 主料和替代料是相同料号!");
			}
		}
		// 判断替代料不能重复
		SQLStr = "select * from (select bd_detno,pre_repcode,count(1) num from prodreplace left join bomdetail on pre_bdid=bd_id where bd_bomid=" + bo_id
				+ " and NVL(pre_statuscode,' ')<>'DISABLE' and bd_id>0 group by bd_detno,pre_repcode)A where num>1";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("序号:" + rs.getString("bd_detno") + "替代料：" + rs.getString("pre_repcode") + "重复建立!");
			}
		}
		Map<Object, Object> loc=null;
		String reapstr="";
		// 判断位号是否重复
		SQLStr = "select bd_location from bomdetail where bd_bomid=" + bo_id
				+ " and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(bd_location,' ')<>' '  ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			String[] bdloc=rs.getString("bd_location").split(",");  
			for(String s:bdloc){ 
				if (loc==null){
					loc = new HashMap<Object, Object>();
					loc.put(s, 1);
				}else if(loc.containsKey(s)){
					reapstr+=","+s;
				}else{
					loc.put(s, 1);
				} 
			}  
		}
		if (!reapstr.equals("")) {
			BaseUtil.showError("位号:" + reapstr.substring(1) + " 重复出现,不能提交!'");
		}

		SQLStr = "select * from ProdFeature B where B.pf_prodcode='"
				+ code
				+ "' and pf_fecode not in (select pf_fecode FROM BomDetail left join ProdFeature A on A.pf_prodcode=bd_soncode "
				+ "WHERE bd_bomid='" + bo_id + "'  and nvl(bd_usestatus,' ')<>'DISABLE' and pf_fecode<>' ' ) ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			BaseUtil.showError("母件特征ID[" + rs.getString("pf_fecode") + "],在子件特征中不存在,不能提交!'");

		}
		//非制造件，非虚拟件，非虚拟特征件物料，且BOM等级不是外购件BOM则不允许提交
		SQLStr =  "select bo_id from bom left join product on bo_mothercode=pr_code where bo_id=? and NVL(pr_manutype,' ') not in ('MAKE','OSMAKE') and NVL(pr_supplytype,' ')<>'VIRTUAL' and bo_level not in (select bl_code from bomlevel where bl_code='外购件BOM' OR bl_ifpurchase<>0)";
		rs = baseDao.queryForRowSet(SQLStr,bo_id);
		if (rs.next()) { 
			BaseUtil.showError("母件不是制造/虚拟件物料，且BOM等级不是[外购件BOM]，不能提交"); 
		}  
		//@add 20161222 BOM 等级设置的是外购件，BOM 母件不允许是制造/虚拟件物料
		SQLStr = "select bo_id from bom left join product on bo_mothercode=pr_code inner join bomlevel on bl_code=bo_level where bo_id=? and NVL(pr_manutype,' ') in ('MAKE','OSMAKE') and (NVL(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )";
		rs = baseDao.queryForRowSet(SQLStr,bo_id);
		if (rs.next()) { 
			BaseUtil.showError("外购件BOM的母件生产类型不能为制造、委外"); 
		} 
		// BOM明细不能出现母件料号,否则导致嵌套
		SQLStr = "SELECT  bd_detno FROM BomDetail where bd_bomid='" + bo_id + "' and bd_soncode='" + code + "' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			BaseUtil.showError("子件编号不能是母件料号,序号[" + rs.getString("bd_detno") + "],不能提交!");
		}
		//检查主料和替代料的种类是否一致
		checkProductAndRepKind(bo_id);
		//子件BOM等级系数不能低于母件、物料等级须符合BOM等级要
		BOM_Check_Level(bo_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("BOM", new Object[] { bo_id});
		// 执行提交操作
		baseDao.execute("update bomdetail set bd_sonbomid=" + bo_id + " where bd_soncode='" + code + "' and bd_sonbomid<>" + bo_id);
		baseDao.execute("update bomdetail set bd_sonbomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=bd_soncode ) where bd_bomid='" + bo_id + "' ");
		//如果明细是通过excel 导入的检查bd_ifrep=-1有替代料的数据，是否确实存在替代料，如果无更新bd_ifrep 为0
		baseDao.execute("update bomdetail set bd_ifrep=0,bd_repcode='' where bd_bomid=? and (bd_ifrep=-1 or nvl(bd_repcode,' ')<>' ') "+
                        " and not exists(select 1 from prodreplace where NVL(pre_statuscode,' ')<>'DISABLE' and pre_bdid>0 and bd_id=pre_bdid)",bo_id);
		baseDao.execute("update bomdetail set bd_ifrep=-1,bd_repcode=(select wm_concat(pre_repcode) from prodreplace where bd_id=pre_bdid and NVL(pre_statuscode,' ')<>'DISABLE') where bd_bomid=" + bo_id +" and bd_id in (select pre_bdid from prodreplace where NVL(pre_statuscode,' ')<>'DISABLE' and pre_bdid>0)");	
		baseDao.submit("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//2017020022 计算BOM的总贴片点数
		baseDao.execute("update bom set bo_smtpoints = (select sum(nvl(bd_baseqty,0)*nvl(pr_smtpoint,0)) from bomdetail left join bom on bo_id=bd_bomid left join product on bd_soncode=pr_code where bo_id = ? and nvl(bd_usestatus,' ')<>'DISABLE') where bo_id = ?",bo_id,bo_id);
		//更新bomdetail 倒数bd_baseqtyback
		baseDao.execute("update bomdetail set bd_baseqtyback=(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) where bd_bomid=? and bd_baseqtyback<>(case when nvl(bd_baseqty,0)<>0 then 1/bd_baseqty else 0 end) ", bo_id);
		// 记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("BOM", new Object[] { bo_id});
		sortBomdetailDetno(bo_id);
	}

	@Override
	public void resSubmitBOM(int bo_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BOM", new Object[] { bo_id });
		// 执行反提交操作
		baseDao.resOperate("Bom", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		handlerService.afterResSubmit("BOM",new Object[] { bo_id});
	} 
	@Override
	public JSONObject calBOMCost(int bo_id, String pr_code,String caller) {
		Object prspecvalue = null;
		Object prrefno = null;
		if (!pr_code.equals("") && bo_id == 0) {
			prspecvalue = baseDao.getFieldDataByCondition("product", "pr_specvalue", "pr_code='" + pr_code + "'");
			try {
				if (!prspecvalue.equals("")) {
					if (prspecvalue.equals("SPECIFIC")) {
						prrefno = baseDao.getFieldDataByCondition("product", "pr_refno", "pr_code='" + pr_code + "'");
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id",
								"bo_mothercode='" + prrefno + "'").toString());
					} else {
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id",
								"bo_mothercode='" + pr_code + "'").toString());
					}
				}
			} catch (Exception ex) {

			}

		}
		if("BOMFiCost".equals(caller)){
			baseDao.procedure("SP_COSTCOUNTFI", new Object[] { bo_id, pr_code, "最新采购单价" });
		}else{
			baseDao.procedure("SP_COSTCOUNT", new Object[] { bo_id, pr_code, "最新采购单价" });
		}
		JSONObject js = new JSONObject();
		js.put("boid", bo_id);
		return js;
	}
 
	@Override
	public String[] printBomCost(int bo_id, String caller, String reportName, String condition,String prodcode) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("BomStruct", new Object[] { bo_id,});
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 取出当月汇率
		double ThisUSDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_rate),0)"," cr_name='USD' and nvl(cr_status,' ')<>'已禁用'").toString());
		double ThisHKDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_rate),0)"," cr_name='HKD' and nvl(cr_status,' ')<>'已禁用'").toString());
		if (ThisUSDRate == 0) {
			BaseUtil.showError("币别表未设置美金汇率!");
		}
		String SQLStr = "update BomStruct  set bs_osprice=0 where bs_topbomid=" + bo_id + " and bs_osprice is null ";
		baseDao.execute(SQLStr);
		String thisMonthercode = baseDao.getFieldDataByCondition("Bom", "bo_mothercode", " bo_id=" + bo_id + "")
				.toString();
		SQLStr = "merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_rate  END) where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_m=bs_l*bs_baseqty where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))";
		baseDao.execute(SQLStr);
		SQLStr = "merge into BomStruct using(select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on(bs_currency=cr_name)when matched then update set bs_j=(CASE WHEN bs_currency='RMB' then bs_purcprice ELSE bs_purcprice*cr_rate END) where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_k=bs_j*bs_baseqty where bs_topbomid=" + bo_id + " and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_n=CASE WHEN bs_currency='RMB' then bs_l/" + ThisUSDRate
				+ " ELSE bs_purcprice END where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_o=bs_n*bs_baseqty where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='' where  bs_topbomid=" + bo_id+" and bs_topmothercode='"+prodcode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_usdrate=" + ThisUSDRate + ",bs_hkdrate=" + ThisHKDRate
				+ " where bs_topbomid=" + bo_id+" and bs_topmothercode='"+prodcode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and nvl(bs_sonbomid,0)>0 ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + bo_id + " and bs_soncode='" + thisMonthercode + "' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)>0 or bs_soncode='" + thisMonthercode + "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ";
		baseDao.execute(SQLStr);
		SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid=" + bo_id
				+ " and bs_topmothercode='"+prodcode+"' and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {//bs_osprice 在存储过程中计算出来的值是含税的委外单价
			SQLStr = "SELECT sum(nvl(bs_m,0)),sum(nvl(bs_k,0)),sum(bs_o) from BomStruct WHERE bs_topbomid=" + bo_id
					+ " and bs_topmothercode='"+prodcode+"' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
			SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
			if (rsthis.next()) {
				SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1) + "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty,bs_k=round((" + rsthis.getString(2) + "+nvl(bs_osprice,0)),8)*bs_baseqty,bs_o="
						+ rsthis.getString(3) + " where bs_topbomid="+bo_id+" and bs_idcode=" + rs.getString("bs_idcode");
				baseDao.execute(SQLStr);
			}
		}
		// 当前计算的主件自身
		SQLStr = "SELECT bs_topmothercode from BomStruct WHERE bs_topbomid=" + bo_id
				+ " and bs_topmothercode='"+prodcode+"' and bs_mothercode='" + thisMonthercode + "' ";
		SqlRowList rss = baseDao.queryForRowSet(SQLStr);
		if (rss.next()) {//bs_m 不含税成本 ，bs_k 含税成本
				Object a = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_m,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object b = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_k,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object c = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_o,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				SQLStr = "update bomstruct set bs_m=round((?+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty,bs_k=round((?+nvl(bs_osprice,0)),8)*bs_baseqty,bs_o=? where bs_topbomid="+bo_id+" and bs_soncode='"
						+ thisMonthercode + "' ";
				baseDao.execute(SQLStr,new Object[]{a,b,c});
		}
		SQLStr = "update BomStruct set bs_m=0 where bs_topbomid=" + bo_id + " and bs_m is null ";
		baseDao.execute(SQLStr);
		return keys;
	}

	@Override
	public void bomcopy(int id, String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String copy_boid=(String) store.get("bo_id");
		boolean bool = baseDao.checkIf("BOM left join product on bo_mothercode=pr_code", "pr_id='" + id + "'");
		if (bool) {
			BaseUtil.showError("该母件编号已存在BOM!");
			return;
		}
		Object mothercode = baseDao.getFieldDataByCondition("Product", "pr_code", "pr_id=" + id);
		if (mothercode == null) {
			BaseUtil.showError("该母件编号不存在!");
			return;
		}
		// 修改
		int baseId = Integer.parseInt(store.get("bo_id").toString());
		store.remove("bo_id");
		store.remove("bo_motherid");
		store.remove("bo_mothercode");
		store.remove("bo_statuscode");
		store.remove("bo_status");
		store.remove("bo_commitman");
		store.remove("bo_auditman");
		store.remove("bo_updateman");
		store.remove("bo_updatedate");
		int bomid = baseDao.getSeqId("BOM_SEQ");
		store.put("bo_id", bomid);
		store.put("bo_motherid", id);
		store.put("bo_mothercode", mothercode);
		store.put("bo_status", BaseUtil.getLocalMessage("ENTERING"));
		store.put("bo_statuscode", "ENTERING");
		store.put("bo_updateman", SystemSession.getUser().getEm_name());
		store.put("bo_updatedate", DateUtil.currentDateString(null));
		store.put("bo_recorder", SystemSession.getUser().getEm_name());
		store.put("bo_date", DateUtil.currentDateString(null));
		store.put("bo_auditdate","");
		String formSql = SqlUtil.getInsertSqlByMap(store, "BOM");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = new ArrayList<String>();
		int seqId = 0;
		SqlRowList sl = baseDao.queryForRowSet("select *  from bomdetail where bd_bomid=" + baseId
				+ " AND NVL(BOMDetail.bd_usestatus,' ')<>'DISABLE'");
		Map<String, Object> modelMap = null;
		Map<String, Object> repMap = null;
		int bdid = 0;
		int repSeqId = 0;
		while (sl.next()) {
			seqId = baseDao.getSeqId("BOMDETAIL_SEQ");
			modelMap = sl.getCurrentMap();
			bdid = Integer.parseInt(modelMap.get("bd_id").toString());
			modelMap.remove("bd_id");
			modelMap.remove("bd_bomid");
			modelMap.remove("bd_mothercode");
			modelMap.remove("bd_motherid");
			modelMap.remove("bd_usestatus");
			modelMap.remove("bd_ecncode");
			modelMap.remove("bd_ecnid");
			modelMap.put("bd_id", seqId);
			modelMap.put("bd_bomid", bomid);
			modelMap.put("bd_mothercode", mothercode);
			modelMap.put("bd_motherid", id);
			modelMap.put("bd_editdate", "");
			String sql = SqlUtil.getInsertSqlByMap(modelMap, "BOMDetail", new String[] {}, new Object[] {});
			gridSql.add(sql);
			// 复制 替代料
			Object ifrep = modelMap.get("bd_ifrep");
			if (!ifrep.equals("0")) {
				SqlRowList sl2 = baseDao.queryForRowSet("select * from  prodreplace where pre_bdid=" + bdid
						+ " AND NVL(pre_statuscode,' ')<>'DISABLE'");
				while (sl2.next()) {
					repSeqId = baseDao.getSeqId("prodreplace_SEQ");
					repMap = sl2.getCurrentMap();
					repMap.remove("pre_id");
					repMap.remove("pre_bdid");
					repMap.remove("pre_bomid");
					repMap.put("pre_id", repSeqId);
					repMap.put("pre_bdid", seqId);
					repMap.put("pre_bomid", bomid);
					gridSql.add(SqlUtil.getInsertSqlByMap(repMap, "prodreplace"));
				}
			}
		}
		baseDao.execute(gridSql);
		baseDao.logger.copy("BOM",copy_boid,"bo_id",bomid);
		BaseUtil.showError(BaseUtil.getLocalMessage("赋值BOM成功!BOMID:")
				+ "<a href=\"javascript:openUrl('jsps/pm/bom/BOM.jsp?formCondition=bo_idIS" + bomid
				+ "&gridCondition=bd_bomidIS" + bomid + "')\">" + bomid + "</a>&nbsp;");
	}

	@Override
	public void turnBOM(String data,String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		// 更新bom
		String sqlLog="";
		Object isprocess = map.get("isprocess"); 
		Object bolevel=map.get("bolevel");
		Object boid= map.get("boid");
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + boid);
		Object bo_mothercode=baseDao.getFieldDataByCondition("BOM", "bo_mothercode", "bo_id=" + boid);
		Object old_bo_level=baseDao.getFieldDataByCondition("BOM", "bo_level", "bo_id=" + boid);
		if(status==null || !status.equals("AUDITED")){
			BaseUtil.showError("只能对已审核状态的BOM操作！");
		}
		if (!baseDao.checkIf("BOMlevel", "bl_code='"+bolevel+"' and bl_statuscode='AUDITED'")){
			BaseUtil.showError("BOM等级不存在或未审核！");
		}
		if (Integer.parseInt(isprocess.toString()) == 1) {
			// 重置流程
			baseDao.updateByCondition("BOM", "bo_level='" + bolevel
					+ "',bo_auditman=null,bo_commitman=null,bo_recorder='" +SystemSession.getUser().getEm_name() + "',bo_recorderid="
					+ SystemSession.getUser().getEm_id() + ",bo_status='在录入',bo_statuscode='ENTERING'", "bo_id=" + map.get("boid"));
			//日志操作
			sqlLog = "insert into MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES"
					+ "(sysdate,'"+SystemSession.getUser().getEm_name()+"','更新BOM等级','BOM等级由"+old_bo_level+"更新为"+bolevel+"，重置流程成功','"+"BOM|bo_id="+map.get("boid")+"')";
		} else {
			// 不重置流程
			//判断子件BOM等级是否到达母件的BOM等级
			String SQLStr = "";
			SqlRowList rs,rs0; 
			if (bolevel!=null){
				int bl_grade=Integer.parseInt(baseDao.getFieldDataByCondition("BOMlevel", "NVL(bl_grade,0)", "bl_code='"+bolevel.toString()+"'").toString());
				if (bl_grade>0 ){ 
					//升级BOM，限制子件的BOM等级不能低于本BOM等级
					rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join bom on bo_mothercode=bd_soncode left join bomlevel on bl_code=bo_level where bd_bomid='" + map.get("boid")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and bl_grade<" + bl_grade);
					if (rs0.next()) {
						if (rs0.getInt("num")>0){
							BaseUtil.showError("子件的BOM等级系数不能低于母件的BOM等级系数，BOM序号："+rs0.getString("detno")+"");
						} 
					} 
				}   
				//降BOM等级，限制上阶BOM等级不能高于本BOM的等级
				rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bo_id) bo_id from bomdetail left join bom on bo_mothercode=bd_soncode left join bomlevel on bl_code=bo_level where bd_soncode='" + bo_mothercode + "' and NVL(bd_usestatus,' ')<>'DISABLE' and bl_grade>" + bl_grade);
				if (rs0.next()) {
					if (rs0.getInt("num")>0){
						BaseUtil.showError("上级BOM等级系数不能高于本BOM的等级系数，BOM："+rs0.getString("bo_id")+"");
					} 
				}
				SQLStr = "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid  where bl_code='"+bolevel.toString()+"'  ";
				rs = baseDao.queryForRowSet(SQLStr);
				if (rs.next()) {
					if (rs.getInt("disnum") > 0) {
						//判断是否有禁用的物料等级
						rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + map.get("boid")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"') ");
						if (rs0.next()) {
							if (rs0.getInt("num")>0){
								BaseUtil.showError("BOM序号："+rs0.getString("detno")+"的物料优选等级在BOM等级定义里面被禁用");
							} 
						}
					} 
					if (rs.getInt("allnum") > 0 && rs.getInt("disnum")==0){
						//判断是否有物料等级达到要求等级
						rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + map.get("boid")+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"')  ");
						if (rs0.next()) {
							if (rs0.getInt("num")>0){
								BaseUtil.showError("BOM序号："+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求");
							} 
						}
					}
				}
			} 
			baseDao.updateByCondition("BOM", "bo_style='" + map.get("bostyle")+"',bo_level='" + map.get("bolevel")
					+ "',bo_recorder='" + SystemSession.getUser().getEm_name() + "',bo_recorderid=" + SystemSession.getUser().getEm_id(), "bo_id="
					+ map.get("boid"));
			//日志操作
			sqlLog = "insert into MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES"
					+ "(sysdate,'"+SystemSession.getUser().getEm_name()+"','更新BOM等级','BOM等级由"+old_bo_level+"更新为"+bolevel+"，未重置流程','"+"BOM|bo_id="+map.get("boid")+"')";
		}
		baseDao.execute(sqlLog);
		// 执行转BOM之后的其它逻辑
		handlerService.handler("BOM", "turnBOM", "after", new Object[] {data});
	} 
	//禁用BOM
	@Override
	public void bannedBOM(String data,String caller) { 
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);  
		Object remark = map.get("remark");
		Object boid = map.get("boid");
		Object mothercode=baseDao.getFieldDataByCondition("BOM", "bo_mothercode", "bo_id=" + boid); 
		SqlRowList rs =  baseDao.queryForRowSet("select 1 from product where pr_code='"+mothercode+"' and (pr_manutype in ('MAKE','OSMAKE') OR pr_supplytype='VIRTUAL') ");
		if(rs.next()){//母件是制造件或者虚拟件的，如果有父级BOM的情况不能反审核 
			Object errProds = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(bo_id) from bomdetail,bom where bo_id=bd_bomid and bd_soncode='"+mothercode+"' and bo_statuscode in ('COMMITED','AUDITED') and NVL(bd_usestatus,' ')<>'DISABLE'  ",
							String.class);
			if (errProds != null) {
				BaseUtil.showError("母件已经在BOM:" + errProds+"中使用，需先反审核父级BOM");
			}
		}
		baseDao.updateByCondition("BOM", "bo_status='已禁用',bo_statuscode='DISABLE',bo_remark=bo_remark||' " + remark
				+ "'", "bo_id=" + boid);
		baseDao.logger.banned(caller, "bo_id", map.get("boid")); 
	}
	//反禁用BOM
	@Override
	public void resBannedBOM(String data,String caller) { 
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);  
		Object remark = map.get("remark");
		Object boid = map.get("boid");
		String resBanned = baseDao.getDBSetting(caller, "BOMStatus");
		String statuscode = "ENTERING";
		if (resBanned != null) {
			// 反禁用状态是已审核
			if ("1".equals(resBanned)) {
				statuscode = "AUDITED";
			}
			// 反禁用状态是在录入
			if ("0".equals(resBanned)) {
				statuscode = "ENTERING";
			}
		}
		baseDao.updateByCondition("BOM", "bo_statuscode='" + statuscode + "',bo_status='" + BaseUtil.getLocalMessage(statuscode)
				+ "',bo_remark=bo_remark||'" + remark + "'", "bo_id=" + boid);
		baseDao.logger.resBanned(caller, "bo_id",map.get("boid"));

	}
	@Override
	public void updateBomPast(int bo_id,String value,String caller) {
		//更新跳层
		baseDao.execute("update bom set bo_ispast='"+value+"' where bo_id=" + bo_id );
		if(Integer.valueOf(value) != 0){
			value = "是";
		}else {
			value = "否";
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新跳层","更新跳层值为"+value, "BOM|bo_id=" + bo_id));		
				
	}

	//计算帐龄
	@Override
	public String zhangling(String todate,String caller) {
		try{
			baseDao.procedure("UPDATEBAREMAIN", new Object[]{todate});
			return "success";
		}catch(Exception ex){
			return "fail";
		}
		
	}

	@Override
	public String[] printsingleBom(int boId, String caller,
			String reportName, String condition) {
		// TODO Auto-generated method stub
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		return keys;
	}
	/**
	 * 制造ECN确认
	 * @author mad 2014-6-24 15:27:34
	 */
	@Override
	public String confirmECN(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			int maid = Integer.parseInt(map.get("ma_id").toString());
			int mcid = Integer.parseInt(map.get("mc_id").toString());
			baseDao.execute("update makematerialchangedet set md_didstatus='待执行' where md_mcid=? and md_makecode in (select ma_code from make where ma_id=?)", mcid, maid);
		}
		return "确认成功";
	}
	/**
	 * 制造ECN取消
	 * @author mad 2014-6-24 15:27:38
	 */
	@Override
	public String cancelECN(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			int maid = Integer.parseInt(map.get("ma_id").toString());
			int mcid = Integer.parseInt(map.get("mc_id").toString());
			baseDao.execute("update makematerialchangedet set md_didstatus='已取消' where md_mcid=? and md_makecode in (select ma_code from make where ma_id =?)", mcid, maid);
		}
		return "取消成功";
	}

	@Override
	public String loadRelation(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) { 
			int bomid = Integer.parseInt(map.get("bd_bomid").toString());
			int bddetno = Integer.parseInt(map.get("bd_detno").toString());
			String bdsoncode=map.get("bd_soncode").toString();
			String prrsoncode=map.get("prr_soncode").toString();
			String prrrepcode=map.get("prr_repcode").toString();
			String repcode="",SQLStr="";
			//获取单据的最新状态进行判断是否在录入状态
			SqlRowList rs = baseDao.queryForRowSet("select bo_status from bom where bo_id="+bomid+" and bo_statuscode<>'ENTERING'");
			if(rs.next()){
			    BaseUtil.showError("BOM单据的最新状态为"+rs.getString("bo_status")+"不允许操作"+",请刷新界面");
			 }
			//判断是否已经存在此替代
			SqlRowList sl = baseDao.queryForRowSet("select * from bomdetail left join prodreplace on bd_bomid=pre_bomid and bd_id=pre_bdid"+
					" where bd_bomid="+bomid+" and bd_detno="+bddetno+" and NVL(pre_repcode,' ') in ('"+prrsoncode+"','"+prrrepcode+"')");
			if (!sl.hasNext()) { 
				if (bdsoncode.equals(prrsoncode) && !bdsoncode.equals(prrrepcode)){
					repcode=prrrepcode;//建立了正向替代
				}else if (!bdsoncode.equals(prrsoncode) && bdsoncode.equals(prrrepcode)){
					repcode=prrsoncode;//建立了反向替代的双向替代
				}else{
					continue;//配置有错，不能替代
				}
				//获取最大序号 
				int maxdetno = 1 + Integer.parseInt(baseDao.getFieldDataByCondition("prodreplace", "NVL(max(pre_detno),0)", "pre_bomid="+ bomid +" and pre_bddetno=" + bddetno).toString());
				SQLStr="INSERT INTO prodreplace (pre_id,pre_bomid,pre_bdid,pre_bddetno,pre_detno,pre_soncode,pre_soncodeid,pre_repcode,pre_repcodeid,pre_prodcode,pre_baseqty ) "
				 + "select ProdReplace_Seq.nextval,bd_bomid,bd_id,bd_detno," + maxdetno + ",bd_soncode,0,'" + repcode + "',0,bd_mothercode,bd_baseqty from bomdetail where bd_bomid=" + bomid + " and bd_detno=" + bddetno;
				baseDao.execute(SQLStr);
				SQLStr="update bomdetail set bd_ifrep=-1,bd_repcode=(select wm_concat(pre_repcode) from prodreplace where bd_id=pre_bdid and NVL(pre_statuscode,' ')<>'DISABLE') where bd_bomid=" + bomid + " and bd_detno=" + bddetno;
		 		baseDao.execute(SQLStr);
			} 
		}
		return "确认成功";
	}

	@Override
	public List<Map<String, Object>> getProductCount(String codes,String caller) {
		SqlRowList rs = baseDao
				.queryForRowSet("select v_pr_code pr_code, v_po_mrponhand po_mrponhand ,v_po_defectonhand po_defectonhand ,v_reconhand reconhand, v_poqty poqty  ,v_arkqty arkqty,v_mmqty mmqty,v_maqty maqty from PM_PRODBALANCE_VIEW where v_pr_code ='"
						+ codes + "' ");
		if (rs.hasNext()) {
			return rs.getResultList();
		}
		return null;
	}
	
	/**
	 * 子件BOM等级系数不能低于母件、物料等级须符合BOM等级要求
	 * @author ZHONGYL
	 * */
	public void BOM_Check_Level(Integer bo_id){
		String SQLStr = "";
		SqlRowList rs,rs0;
		Object bolevel;
		bolevel=baseDao.getFieldDataByCondition("BOM", "bo_level", "bo_id="+bo_id);
		if (bolevel==null || bolevel.equals("")){
			return;
		}
		//判断物料等级是否满足BOM等级要求
		SQLStr = "select NVL(sum((case when NVL(pd_useable,0)=0 then 1 else 0 end)),0) as disnum,count(1) as allnum  from Productleveldetail left join bomlevel on bl_id=pd_blid  where bl_code='"+bolevel.toString()+"'  ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("disnum") > 0) {
				//判断是否有禁用的物料等级
				rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + bo_id+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"') ");
				if (rs0.next()) {
					if (rs0.getInt("num")>0){
						BaseUtil.showError("BOM序号："+rs0.getString("detno")+"的物料优选等级在BOM等级定义里面被禁用");
					} 
				}
				//判断替代料是否有禁用的物料等级
				rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id left join product on pr_code=pre_repcode where pre_bomid='" + bo_id+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE' and pr_level in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"') ");
				if (rs0.next()) {
					if (rs0.getInt("num")>0){
						BaseUtil.showError("BOM"+rs0.getString("detno")+"，物料优选等级在BOM等级定义里面被禁用");
					} 
				}
			} 
			if (rs.getInt("allnum") > 0 && rs.getInt("disnum")==0){
				//判断是否有物料等级达到要求等级
				rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode where bd_bomid='" + bo_id+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"')  ");
				if (rs0.next()) {
					if (rs0.getInt("num")>0){
						BaseUtil.showError("BOM序号："+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求");
					} 
				}
				//判断替代料是否有物料等级达到要求等级
				rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat('序号:'||bd_detno||'替代料:'||pre_repcode) detno from prodreplace left join bomdetail on pre_bdid=bd_id  left join product on pr_code=pre_repcode where bd_bomid='" + bo_id+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pre_statuscode,' ')<>'DISABLE' and pr_level not in (select pd_plcode from Productleveldetail left join bomlevel on bl_id=pd_blid where bl_code='"+bolevel.toString()+"')  ");
				if (rs0.next()) {
					if (rs0.getInt("num")>0){
						BaseUtil.showError("BOM"+rs0.getString("detno")+"的物料优选等级还没有到达BOM等级要求");
					} 
				}
			}
		} 
		//判断子件BOM等级是否到达母件的BOM等级
		Object ob = baseDao.getFieldDataByCondition("BOMlevel", "NVL(bl_grade,0)", "bl_code='"+bolevel.toString()+"'");
		if(ob == null){
			BaseUtil.showError("子件BOM["+bo_id+"],BOM等级["+bolevel+"]不存在BOM等级表中");
		}else{
			int bl_grade = Integer.parseInt(ob.toString());
			rs0 = baseDao.queryForRowSet("select count(1) num, wm_concat(bd_detno) detno from bomdetail left join product on pr_code=bd_soncode left join bom on bo_mothercode=bd_soncode left join bomlevel on bl_code=bo_level where bd_bomid='" + bo_id+ "' and NVL(bd_usestatus,' ')<>'DISABLE' and bl_grade<" + bl_grade);
			if (rs0.next()) {
				if (rs0.getInt("num")>0){
					BaseUtil.showError("子件的BOM等级不能低于母件的等级，BOM序号："+rs0.getString("detno")+"");
				} 
			}
		}		
    } 
	
	/*
	 * 变型BOM限制 
	 * 1、如果选择了是否变型BOM为“是”，必须选择原型BOMID，“否”则不能选择变型BOM。
	 * 2、对应的原型BOM必须是已审核且NVL(bo_isextend,0)=0的BOM。
	 * 3、变型BOM的负数用量的子件必须是在原型BOM的子件中出现，且 NVL(bd_usestatus,' ')<>'DISABLE'
	 * 4、变型BOM的负数用量的子件用量必须跟原型BOM中的子件用量汇总数为0，也就是说原型BOM用量n，则变型bom中必须是-n
	 * 5、变型BOM的子件中序号1必须是原型BOM母件，且用量为1
	 */
	public void JudgeExtendBom (int bo_id){
		SqlRowList rs,rs0;
		String sql;
		int count;
		baseDao.execute("update bom set bo_refcode=(select bo_mothercode from bom b where b.bo_id=bom.bo_refbomid) where bo_id="+bo_id);
		baseDao.execute("update bom set (bo_refname,bo_refspec)=(select pr_detail,pr_spec from product where pr_code=NVL(bo_refcode,' ')) where bo_id="+bo_id);
		rs = baseDao.queryForRowSet("select NVL(bo_isextend,0) bo_isextend,NVL(bo_refbomid,0) bo_refbomid,bo_refcode from Bom where bo_id="+bo_id+"");
		if(rs.next()){
			int ex = rs.getInt("bo_isextend");
			int ref = rs.getInt("bo_refbomid");
			String refcode = rs.getString("bo_refcode");
			if (ex != 0 && ref == 0){
				BaseUtil.showError("变型BOM必须选择原型BOMID");
			}else if(ex == 0 && ref > 0){
				BaseUtil.showError("填写了原型BOMID，则本BOM必须为变型BOM");
			}else if (ex != 0 && ref > 0){
				int is = baseDao.getCount("select count(1) from  Bom where bo_id="+ref+" AND bo_isextend <>0 ");
				if (is > 0 ){
					BaseUtil.showError("原型BOM所属的类型不能为变型BOM");					
				}	
				//判断序号1是否为原型母件料号
				sql="select count(1) from bomDetail left join bom on bd_bomid=bo_id where bd_bomid ="+bo_id+" AND bd_detno=1 AND  bd_soncode='"+ refcode + "'AND bd_baseqty=1 AND NVL(bd_usestatus,' ')<>'DISABLE' ";
				count = baseDao.getCount(sql);				
				if(count != 1){
					BaseUtil.showError("变型BOM的子件中序号1必须是原型BOM母件，且用量为1");
				}				
				//大于0用量的子件必须在原型BOM中不存在
				/*
				sql= "select count(1) c,wm_concat(bd_detno) detno  from BOM ,BOMDETAIL WHERE bo_id=bd_bomid "
					  +" AND bo_id = '"+bo_id+"' AND bd_baseqty>0 AND NVL(bd_usestatus,' ')<>'DISABLE' "
					  +" AND bd_soncode in (select bd_soncode from  bomDetail b where  b.bd_bomid='"+ref+"' AND NVL(b.bd_usestatus,' ')<>'DISABLE')";
				rs0 = baseDao.queryForRowSet(sql);
				if (rs0.next()){
					if(rs0.getInt("c")>0){
						BaseUtil.showError("序号："+rs0.getString("detno")+"子件在原型BOM中已经存在");
					}
				} 	
				*/
				sql="select  count(1) c,wm_concat(bd_detno) detno  from bomdetail left join bom on bo_id=bd_bomid where bd_bomid="+bo_id+" and bo_refbomid>0 and  bd_baseqty<0 and nvl(bd_usestatus,' ')<>'DISABLE'  " 
	                    +" AND bd_soncode not in (select bd_soncode from bomdetail b where b.bd_bomid="+ref+" and nvl(b.bd_usestatus,' ')<>'DISABLE' )";
				rs0 = baseDao.queryForRowSet(sql);
				if (rs0.next()){
					if(rs0.getInt("c")>0){
						BaseUtil.showError("序号："+rs0.getString("detno")+"子件用量为负数，但在原型BOM中不存在此料");
					}
				} 	
				sql="select  count(1) c,wm_concat(bd_detno) detno from bomdetail left join bom on bo_id=bd_bomid where bd_bomid="+bo_id+" and bo_refbomid>0 and  bd_baseqty<0 and nvl(bd_usestatus,' ')<>'DISABLE'  " 
	                    +" AND bd_soncode in (select bd_soncode from bomdetail b where b.bd_bomid="+ref+" and b.bd_soncode=bomdetail.bd_soncode and nvl(b.bd_usestatus,' ')<>'DISABLE' and b.bd_baseqty<-bomdetail.bd_baseqty)";
				rs0 = baseDao.queryForRowSet(sql);
				if (rs0.next()){
					if(rs0.getInt("c")>0){
						BaseUtil.showError("序号："+rs0.getString("detno")+"物料在原型和变型BOM中的用量汇总小于0");
					}
				} 	 			    
			}						
		}
	}
	
	/**
	 * 生成BOM替代料语句
	 * @param strs
	 * @param map
	 * @param repSql
	 */
	private void checkRepCode(String [] strs,Map<Object, Object> map,List<String> repSql){
		int detno = 1 ;
		for(String repcode : strs){
		    SqlRowList rs = baseDao.queryForRowSet("select pr_code from product where pr_code=? and pr_statuscode='AUDITED'", repcode);
		    if(!rs.next()){
		    	BaseUtil.showError("行："+map.get("bd_detno")+",替代料号："+repcode+",不存在或者未审核！");
		    }else{
		    	repSql.add("insert into prodreplace (pre_id,pre_detno,pre_prodcode,pre_bomid,pre_bddetno,pre_soncode,pre_repcode,pre_bdid,"+
                     " pre_rate ,pre_reprate ,pre_level ,pre_startdate,pre_enddate ,pre_validdate)values("+
                     " PRODREPLACE_SEQ.nextval,"+detno+",'"+map.get("bd_mothercode")+"','"+map.get("bd_bomid")+"','"+map.get("bd_detno")+"','"+map.get("bd_soncode")+"','"+repcode+"','"+map.get("bd_id")+"',"
                     + " 0, 0,0 ,sysdate,sysdate,sysdate)");
		    	detno ++;		    	
		    }		    
		}
		repSql.add(repSql.size()-detno+1, "delete from prodreplace where pre_bdid="+map.get("bd_id")+" and pre_bddetno='"+map.get("bd_detno")+"'");
	    repSql.add("update bomdetail set bd_ifrep=-1 where bd_id="+map.get("bd_id")+" and bd_detno="+map.get("bd_detno"));
	}
	
	
	private void coun(int bo_id,String prodcode){
		// 取出当月汇率
		double ThisUSDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_vorate),0)"," cr_name='USD'").toString());
		double ThisHKDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_vorate),0)"," cr_name='HKD'").toString());
		if (ThisUSDRate == 0) {
			BaseUtil.showError("币别表未设置美金汇率!");
		}
		//更新bs_osprice
		String SQLStr = "update BomStruct set bs_osprice=0 where bs_topbomid=" + bo_id + " and bs_osprice is null ";
		baseDao.execute(SQLStr);
		
		String thisMonthercode = baseDao.getFieldDataByCondition("Bom", "bo_mothercode", " bo_id=" + bo_id + "")
				.toString();
		SQLStr = "merge into BomStruct using (select cr_vorate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_vorate  END) where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_m=bs_l*bs_baseqty where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))";
		baseDao.execute(SQLStr);
		SQLStr = "merge into BomStruct using(select cr_vorate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on(bs_currency=cr_name)when matched then update set bs_j=(CASE WHEN bs_currency='RMB' then bs_purcprice ELSE bs_purcprice*cr_vorate END) where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_k=bs_j*bs_baseqty where bs_topbomid=" + bo_id + " and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_n=CASE WHEN bs_currency='RMB' then bs_l/" + ThisUSDRate
				+ " ELSE bs_purcprice END where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_o=bs_n*bs_baseqty where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='' where  bs_topbomid=" + bo_id+" and bs_topmothercode='"+prodcode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_usdrate=" + ThisUSDRate + ",bs_hkdrate=" + ThisHKDRate
				+ " where bs_topbomid=" + bo_id+" and bs_topmothercode='"+prodcode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + bo_id + " and bs_topmothercode='"+prodcode+"' and nvl(bs_sonbomid,0)>0 ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + bo_id + " and bs_soncode='" + thisMonthercode + "' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
				+ bo_id + " and bs_topmothercode='"+prodcode+"' and (nvl(bs_sonbomid,0)>0 or bs_soncode='" + thisMonthercode + "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ";
		baseDao.execute(SQLStr);
		SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid=" + bo_id
				+ " and bs_topmothercode='"+prodcode+"' and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {//bs_osprice 在存储过程中计算出来的值是含税的委外单价
			SQLStr = "SELECT sum(nvl(bs_m,0)),sum(nvl(bs_k,0)),sum(bs_o) from BomStruct WHERE bs_topbomid=" + bo_id
					+ " and bs_topmothercode='"+prodcode+"' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
			SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
			if (rsthis.next()) {
				SQLStr = "update bomstruct set bs_m=(" + rsthis.getString(1) + "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100))*bs_baseqty,bs_k=(" + rsthis.getString(2) + "+nvl(bs_osprice,0))*bs_baseqty,bs_o="
						+ rsthis.getString(3) + " where bs_topbomid="+bo_id+" and bs_idcode=" + rs.getString("bs_idcode");
				baseDao.execute(SQLStr);
			}
		}
		// 当前计算的主件自身
		SQLStr = "SELECT sum(bs_m+nvl(bs_osprice,0)),sum(bs_k+nvl(bs_osprice,0)),sum(bs_o) from BomStruct WHERE bs_topbomid=" + bo_id
				+ " and bs_topmothercode='"+prodcode+"' and bs_mothercode='" + thisMonthercode + "' ";
		SqlRowList rss = baseDao.queryForRowSet(SQLStr);
		if (rss.next()) {//bs_m 不含税成本 ，bs_k 含税成本
				Object a = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_m,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object b = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_k,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object c = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_o,0)),8)", " bs_topbomid=" + bo_id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				SQLStr = "update bomstruct set bs_m=round((?+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty,bs_k=round((?+nvl(bs_osprice,0)),8)*bs_baseqty,bs_o=? where bs_topbomid="+bo_id+" and bs_soncode='"
						+ thisMonthercode + "' ";
				baseDao.execute(SQLStr,new Object[]{a,b,c});
		}
		SQLStr = "update BomStruct set bs_m=0 where bs_topbomid=" + bo_id + " and bs_m is null ";
		baseDao.execute(SQLStr);
	}

	@Override
	public void BOMStructPrintAll() {
		// TODO Auto-generated method stub
		String str = baseDao.callProcedure("MM_BATCHBOMSTRUCT_ALL", new Object[] { SystemSession.getUser().getEm_id() });
		if (str != null && !str.trim().equals("")) {
			BaseUtil.showError(str);
		}
	}

	@Override
	public JSONObject calBOMPeriodCost(int bo_id, String bv_bomversionid, String fromdate, String todate) { 
		String res = null;
		res = baseDao.callProcedure("SP_COSTCOUNT_PERIOD", new Object[] { bo_id, bv_bomversionid,fromdate ,todate});
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		JSONObject js = new JSONObject();
		if("".equals(bv_bomversionid)){
			Object bomversionid=baseDao.getFieldDataByCondition("dual","to_char(sysdate,'YYMMDD.hhmmss')", "1=1");
			js.put("bv_bomversionid", bomversionid);
		}else{
			js.put("bv_bomversionid", bv_bomversionid);
		}
		return js; 
	}
	
	@Override
	public JSONObject bomCostCustom(int bo_id, String bv_bomversionid, String fromdate, String todate,String data) { 
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);  
		StringBuffer sb = new StringBuffer();
		sb.append(!map.get("bo_principle1_user").equals("")?map.get("bo_principle1_user").toString()+",":"");
		sb.append(!map.get("bo_principle2_user").equals("")?map.get("bo_principle2_user").toString()+",":"");
		sb.append(!map.get("bo_principle3_user").equals("")?map.get("bo_principle3_user").toString()+",":"");
		sb.append(!map.get("bo_principle4_user").equals("")?map.get("bo_principle4_user").toString()+",":"");
		sb.append(!map.get("bo_principle5_user").equals("")?map.get("bo_principle5_user").toString()+",":"");
		sb.append(!map.get("bo_principle6_user").equals("")?map.get("bo_principle6_user").toString()+",":"");
		String bo_principle = sb.substring(0,sb.length()-1).toString();
		Integer bo_ifusecurrency_user = Integer.parseInt(map.get("bo_ifusecurrency_user").toString());
		String res = null;
		res = baseDao.callProcedure("SP_COSTCOUNT_CUSTOM", new Object[] { bo_id, bv_bomversionid,fromdate ,todate
			 ,map.get("bo_currency1_user"),map.get("bo_currency2_user"),map.get("bo_currency3_user"),map.get("bo_currency4_user")
			 ,map.get("bo_rate1_user"),map.get("bo_rate2_user"),map.get("bo_rate3_user"),map.get("bo_rate4_user")
			 ,bo_principle,bo_ifusecurrency_user
		});
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		JSONObject js = new JSONObject();
		if("".equals(bv_bomversionid)){
			Object bomversionid=baseDao.getFieldDataByCondition("dual","to_char(sysdate,'YYMMDD.hhmmss')", "1=1");
			js.put("bv_bomversionid", bomversionid);
		}else{
			js.put("bv_bomversionid", bv_bomversionid);
		}
		return js; 
	}
	
	//检查主料和替代料的种类是否一致
	private void checkProductAndRepKind(Integer bomid){
		Boolean bo =baseDao.isDBSetting("BOM", "productAndRepKindSame");
		if(bo){			
			SqlRowList rs = baseDao.queryForRowSet("select LOB_CONCAT('序号:'||bd_detno||',替代料号:'||repcode||'<br>')ers,count(1)cn from "
					+ " (select bd_detno,wm_concat(pre_repcode)repcode from bomdetail left join product a on a.pr_code=bd_soncode "
					+ " inner join prodreplace on pre_bdid=bd_id left join product b on b.pr_code=pre_repcode "
					+ " where bd_bomid=?  and nvl(bd_usestatus,' ')<>'DISABLE' and nvl(pre_statuscode,' ')<>'DISABLE' "
					+ " and ((nvl(a.pr_kind,' ')<>nvl(b.pr_kind,' ') or nvl(a.pr_kind2,' ')<>nvl(b.pr_kind2,' ') or nvl(a.pr_kind3,' ')<>nvl(b.pr_kind3,' ') "
					+ " or nvl(a.pr_xikind,' ') <> nvl(b.pr_xikind,' '))) group by bd_detno )",bomid);
			if(rs.next() && rs.getInt("cn") >0){
				BaseUtil.showError("主料和替代料的种类不一致,"+rs.getString("ers"));
			}
		}
		
	}

	@Override
	public Object getProductMaster(String codes, String master) {
		SqlRowList rs = baseDao.queryForRowSet("select V_PW_WHCODE,V_WH_DESCRIPTION,V_PW_ONHAND,COMPANYNAME from GROUP_PM_PWONHAND_VIEW where nvl(V_PW_PRODCODE,' ')='"+codes+"' and nvl(WHICHSYSTEM,' ')='"+master+"'");
		if (rs.hasNext()) {
			return rs.getResultList();
		}
		return null;
	}
	
	private void sortBomdetailDetno(Object bo_id){
		int a = 0;
		SqlRowList rs1 = baseDao.queryForRowSet("select * from bomdetail where bd_bomid="+bo_id+" order by bd_detno");
		while(rs1.next()){
			a++;
			baseDao.execute("update bomdetail set bd_detno="+a+" where  bd_id="+rs1.getInt("bd_id"));
			baseDao.execute("update prodreplace set pre_bddetno="+a+" where pre_bdid="+rs1.getInt("bd_id"));
		}
	}
}
