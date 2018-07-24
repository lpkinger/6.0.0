package com.uas.erp.service.oa.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.derby.tools.sysinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.BorrowListModule;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.BorrowAppService;

@Service("borrowAppService")
public class BorrowAppServiceImpl implements BorrowAppService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBorrowList(String formStore, String gridStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BorrowList", "bl_code='" + store.get("bl_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.borrowManage.BorrowApp.save_codeHasExist"));
		}
		checkGrid(grid);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		if(caller.equals("Renew")){
			StringBuffer sb=new StringBuffer();
			for (Map<Object, Object> s : grid) {
				String bookcode=s.get("bld_bookcode").toString();
				String borrowercode=store.get("bl_borrowercode").toString();
				Object[] ob=baseDao.getFieldsDataByCondition("borrowlist left join borrowlistdetail on bl_id=bld_blid",new String[]{"bld_id","bld_enddate+1"}, 
						"to_date(to_char(bld_enddate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') and bld_return=0 and bl_statuscode='AUDITED' and  bl_borrowercode='"+borrowercode+"' and bld_bookcode='"+bookcode+"'");   
				if(ob!=null){
					s.put("bld_startdate", ob[1]);
					s.put("bld_sourceid", ob[0]);
				}else{
					sb.append(s.get("bld_detno")+",");
				}
			}
			if(sb.length()>0){
				BaseUtil.showError("此图书不在您超时未还的图书中，不能续借.明细行编号："+sb.substring(0,sb.length()-1).toString());
			}
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BorrowList", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存BorrowListDetail
		for (Map<Object, Object> s : grid) {
			s.put("bld_id", baseDao.getSeqId("BorrowListDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BorrowListDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "bl_id", store.get("bl_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteBorrowList(int bl_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { bl_id });
		// 删除BorrowList
		baseDao.deleteById("BorrowList", "bl_id", bl_id);
		// 删除BorrowListDetail
		baseDao.deleteById("BorrowListDetail", "bld_bsid", bl_id);
		// 记录操作
		baseDao.logger.delete(caller, "bl_id", bl_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { bl_id });

	}

	@Override
	public void updateBorrowListById(String formStore, String param, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + store.get("bl_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 检查明细行更新人员是否与数据库以保存数据重复
		for (int i = 0; i < gstore.size(); i++) {
			SqlRowList rs = baseDao.queryForRowSet("select bld_detno from BorrowListDetail where bld_blid=" + store.get("bl_id")
					+ " and bld_detno<>'" + gstore.get(i).get("bld_detno") + "' and bld_bookcode='" + gstore.get(i).get("bld_bookcode")
					+ "'");
			if (rs.next()) {
				BaseUtil.showError("明细行图书编号重复，行号：" + rs.getString("bld_detno") + "&nbsp&nbsp" + gstore.get(i).get("bld_detno").toString());
			}
		}
		checkGrid(gstore);
		if(caller.equals("Renew")){
			StringBuffer sb=new StringBuffer();
			for (Map<Object, Object> s : gstore) {
				String bookcode=s.get("bld_bookcode").toString();
				String borrowercode=store.get("bl_borrowercode").toString();
				Object[] ob=baseDao.getFieldsDataByCondition("borrowlist left join borrowlistdetail on bl_id=bld_blid",new String[]{"bld_id","bld_enddate+1"}, 
						"to_date(to_char(bld_enddate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') and bld_return=0 and bl_statuscode='AUDITED' and  bl_borrowercode='"+borrowercode+"' and bld_bookcode='"+bookcode+"'");   
				if(ob!=null){
					s.put("bld_startdate", ob[1]);
					s.put("bld_sourceid", ob[0]);
				}else{
					sb.append(s.get("bld_detno")+",");
				}
			}
			if(sb.length()>0){
				BaseUtil.showError("此图书不在您超时未还的图书中，不能续借.明细行编号："+sb.substring(0,sb.length()-1).toString());
			}
		}
		// 修改BorrowList
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BorrowList", "bl_id");
		baseDao.execute(formSql);
		// 修改BorrowListDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "BorrowListDetail", "bld_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("bld_id") == null || s.get("bld_id").equals("") || s.get("bld_id").equals("0")
					|| Integer.parseInt(s.get("bld_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BorrowListDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BorrowListDetail", new String[] { "bld_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "bl_id", store.get("bl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitBorrowList(int bl_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.submitOnlyEntering(status);
		StringBuffer sb=new StringBuffer();
		SqlRowList rs=baseDao.queryForRowSet("select Wmsys.wm_concat(DISTINCT(a.bld_detno)) detnos from borrowlistdetail a inner join borrowlistdetail b on a.bld_bookcode=b.bld_bookcode "
											+ "left join borrowlist on b.bld_blid= bl_id where a.bld_blid="+bl_id+"  and b.bld_blid <>"+bl_id+" and (BL_STATUSCODE='COMMITED' or BL_STATUSCODE='AUDITED') and "
											+ "((to_date(to_char(a.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<=to_date(to_char(b.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') "
											+ "and to_date(to_char(b.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<=to_date(to_char(a.bld_enddate, 'yyyy-mm-dd'), 'yyyy-mm-dd')) "
											+ " or  (to_date(to_char(a.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')>=to_date(to_char(b.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') "
											+ "and to_date(to_char(a.bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<=to_date(to_char(b.bld_enddate, 'yyyy-mm-dd'), 'yyyy-mm-dd')))");
		if(rs.next()){
			if(rs.getString("detnos")!=null){
				sb.append("明细行号：["+rs.getString("detnos")+"]的图书与其他已提交或已审核的借书单或续借单时间冲突。").append("<hr>");
			}			
		}
		// 借书开始时间>预计还书时间
		String detno = baseDao.getFieldValue("BorrowListDetail", "Wmsys.wm_concat(BLD_DETNO)",
				"to_date(to_char(bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')>to_date(to_char(bld_enddate, 'yyyy-mm-dd'), 'yyyy-mm-dd') and bld_blid="
						+ bl_id, String.class);
		if (detno != null) {
			sb.append("明细行号：["+detno+"]的预计归还日期不能小于借书开始日期。").append("<hr>");
		}
		String detno1 = baseDao.getFieldValue("BorrowListDetail", "Wmsys.wm_concat(BLD_DETNO)",
				"to_date(to_char(bld_startdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')<to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') and bld_blid="
						+ bl_id, String.class);
		if (detno1 != null) {
			sb.append("明细行号：["+detno1+"]的借书开始日期不能小于当天。").append("<hr>");
		}
		if(sb.length()>0){
			BaseUtil.showError(sb.toString());
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { bl_id });
		// 执行提交操作
		baseDao.submit("BorrowList", "bl_id=" + bl_id, "bl_status", "bl_statuscode");
		;
		// 记录操作
		baseDao.logger.submit(caller, "bl_id", bl_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { bl_id });

	}

	@Override
	public void resSubmitBorrowList(int bl_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("BorrowList", "bl_id=" + bl_id, "bl_status", "bl_statuscode");
		;
		// 记录操作
		baseDao.logger.resSubmit(caller, "bl_id", bl_id);

	}

	@Override
	public void auditBorrowList(int bl_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { bl_id });
		Object[] ob=baseDao.getFieldsDataByCondition("BorrowList",new String[]{"bl_borrowercode","bl_borrower"}, "bl_id="+bl_id);   
		baseDao.execute("update book set bo_loanstatus='借出',bo_loanstatuscode='LOAN', bo_loane='"+ob[1]+"',bo_loanercode='"+ob[0]+"'"
				+ "where bo_code in ( select bld_bookcode from BorrowListDetail where bld_blid="
				+ bl_id + ")");
		if(caller.equals("Renew")){
			baseDao.execute("update borrowlistdetail set BLD_RETURN=-1 where bld_id in(select BLD_SOURCEID from borrowlistdetail where bld_blid="+bl_id+")");
		}
		// 执行审核操作
		baseDao.audit("BorrowList", "bl_id=" + bl_id, "bl_status", "bl_statuscode", "bl_auditdate", "bl_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "bl_id", bl_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { bl_id });

	}

	@Override
	public void resAuditBorrowList(int bl_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BorrowList", "bl_statuscode", "bl_id=" + bl_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("BorrowList", "bl_id=" + bl_id, "bl_status", "bl_statuscode");
		baseDao.execute("update book set BO_LOANSTATUS='未借出',BO_LOANSTATUSCODE='UNLOAN', bo_loane='',bo_loanercode='' where bo_code in (select bld_bookcode from BorrowListDetail where bld_blid="
				+ bl_id + ")");
		// 记录操作
		baseDao.logger.resAudit(caller, "bl_id", bl_id);
	}

	@Override
	public void vastRenew(String caller, int[] id) {
		String sql = "UPDATE borrowlist SET bl_status='续借待审核',bl_statuscode='RENEWAUDIT' ";
		for (int key : id) {
			baseDao.execute(sql + " WHERE bl_id=" + key);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<JSONTree> getJSONModule(String caller) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<BorrowListModule> modules = baseDao.getJdbcTemplate().query(
				"SELECT bl_borrower,bl_borrowercode FROM BorrowList group by bl_borrowercode, bl_borrower",
				new BeanPropertyRowMapper(BorrowListModule.class));
		if (modules.size() > 0) {
			for (BorrowListModule module : modules) {
				JSONTree jt = new JSONTree(module);
				tree.add(jt);
			}
		}
		return tree;
	}

	/**
	 * 检查明细行图书编号是否重复
	 * 
	 * @throws ParseException
	 */
	private void checkGrid(List<Map<Object, Object>> grid) throws ParseException {
		// 检查明细行人员编号是否存在重复
		for (int i = 0; i < grid.size(); i++) {
			for (int j = i + 1; j < grid.size(); j++) {
				if (grid.get(i).get("bld_bookcode").toString().equals(grid.get(j).get("bld_bookcode").toString())) {
					BaseUtil.showError("明细行图书编号重复，行号：" + grid.get(i).get("bld_detno").toString() + "&nbsp&nbsp"
							+ grid.get(j).get("bld_detno").toString());
				}
			}
		}
	}

	@Override
	public String vastReturn(String caller, String data) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "bld_id");
		baseDao.execute("update borrowlistdetail set bld_return=-1 where bld_id in("+ids+")");
		baseDao.execute("update book set BO_LOANSTATUS='未借出',BO_LOANSTATUSCODE='UNLOAN' where bo_code in (select bld_bookcode from borrowlistdetail where bld_id in ("+ids+"))");
		return "处理成功";		
	}

	@Override
	public String OverDue(String caller, String data) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "bld_id");
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context)values('" + pr_id + "','"
				+ SystemSession.getUser().getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'"
				+ SystemSession.getUser().getEm_id() + "','你有图书超时未还，请及时归还图书!')");
		baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) select pagingreleasedetail_seq.nextval,"
				+ pr_id + ",em_id,em_name from employee where em_code in (select DISTINCT(bl_borrowercode) from borrowlistdetail left join borrowlist on bld_blid=bl_id where bld_id in("+ids+"))");
		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id="+pr_id);
		baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		return "处理成功";		
	}
}
