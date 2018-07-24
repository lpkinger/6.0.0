package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KBIAssessService;

@Service
public class KBIAssessServiceImpl implements KBIAssessService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveKBIAssess(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store});
		// 保存KBIAssess
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KBIAssess",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("kad_id", baseDao.getSeqId("KBIAssessdet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"KBIAssessdet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ka_id", store.get("ka_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}

	@Override
	public void deleteKBIAssess(int ka_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {ka_id});
		// 删除KBIAssess
		baseDao.deleteById("KBIAssess", "ka_id", ka_id);
		// 删除Contact
		baseDao.deleteById("KBIAssessdet", "kad_kaid", ka_id);
		// 记录操作
		baseDao.logger.delete(caller, "ka_id", ka_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {ka_id});
	}

	@Override
	public void updateKBIAssessById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + store.get("ka_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,gstore});
		// 修改KBIAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KBIAssess",
				"ka_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
			"KBIAssessdet", "kad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("kad_id") == null || s.get("kad_id").equals("") || s.get("kad_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("KBIAssessdet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "KBIAssessdet", new String[]{"kad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ka_id", store.get("ka_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,gstore});
	}

	@Override
	public void submitKBIAssess(int ka_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {ka_id});
		//人名去空格，判断员工是否存在
		List<Object[]> detailData=baseDao.getFieldsDatasByCondition("KBIAssessdet", new String[]{"kad_id","kad_detno",
				"kad_detpasses","kad_otherdetp","kad_detpboss","kad_boss","kad_hrs"}, "kad_kaid="+ka_id+" order by kad_detno");
		List<String> sqls=new ArrayList<String>();
		for(Object[] o:detailData){
			for(int i=2;i<o.length;i++){
				if(o[i]!=null){
					String[] names=o[i].toString().replaceAll("null", "").split("#");//有出现值为"null"的情况
					StringBuffer sb=new StringBuffer();
					Set<String> nameSet=new HashSet<String>();
					for(int j=0;j<names.length;j++){
						String name=names[j].trim();//去空格
						if(name!=null&&!"".equals(name)){
							if(baseDao.getCount("select count(1) from EMPLOYEE where em_name='"+name+"'")!=1){//人员不存在
								BaseUtil.showError("第"+o[1]+"行，"+name+"不存在，请核对后重试！");
							}
							sb.append(name+"#");
						}
					}
					if(sb.length()>0){
						o[i]=sb.toString().substring(0, sb.length()-1);
					}else{
						o[i]="";
					}
				}
			}
			sqls.add("update KBIAssessdet set kad_detpasses='"+o[2]+"',kad_otherdetp='"+o[3]+"',kad_detpboss='"+o[4]+"',kad_boss='"+o[5]+"',kad_hrs='"+o[6]+"' where kad_id="+o[0]);
		}
		baseDao.execute(sqls);
		// 执行提交操作
		baseDao.submit("KBIAssess", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		//修改明细中的状态
		baseDao.updateByCondition("KBIAssessdet", "kad_stutas='未转评估单'", "kad_kaid="+ka_id);
		// 记录操作
		baseDao.logger.submit(caller, "ka_id", ka_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {ka_id});
	}

	@Override
	public void resSubmitKBIAssess(int ka_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] {ka_id});
		// 执行反提交操作
		baseDao.resOperate("KBIAssess", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ka_id", ka_id);
		handlerService.afterResSubmit(caller, new Object[] {ka_id});
	}

	@Override
	public void auditKBIAssess(int ka_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {ka_id});
		// 执行审核操作
		baseDao.audit("KBIAssess", "ka_id=" + ka_id, "ka_status", "ka_statuscode", "ka_auditdate", "ka_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "ka_id", ka_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {ka_id});
	}

	@Override
	public void resAuditKBIAssess(int ka_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("KBIAssess",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("KBIAssess", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ka_id", ka_id);
	}

	@Override
	@Transactional
	public void turnKBIBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer codes=new StringBuffer();
		StringBuffer detnos=new StringBuffer();
		List<String>sqls=new ArrayList<String>();
		int i=0;
		for(Map<Object, Object> map:maps){
			if(i==0){
				codes.append(map.get("ka_code"));
				detnos.append(map.get("kad_detno"));
			}else{
				codes.append("#"+map.get("ka_code"));
				detnos.append("#"+map.get("kad_detno"));
			}
			i++;
			String sql="update KBIAssessdet set kad_stutas='已转评估单' where kad_id="+map.get("kad_id");
			sqls.add(sql);
		}
		String returnStr=baseDao.callProcedure("SP_TURNKBIBILL", new Object[]{codes.toString(),detnos.toString(),SystemSession.getUser().getEm_id()});
		if(!"good".equals(returnStr)){
			BaseUtil.showError(returnStr);
		}
		baseDao.execute(sqls);
	}

	@Override
	public int autoSave(String caller, String ka_detp) {
		// 要插入主记录的ID
		int ka_id = baseDao.getSeqId("KBIAssess_SEQ");
		int id = 0;// 符合条件的最新记录的ID
		try {
			id = Integer.parseInt(baseDao.getFieldDataByCondition("(select ka_id from KBIAssess where ka_detp='"
					+ ka_detp + "' order by ka_recorddate desc)", "ka_id",
					"rownum<=1")+"");
		} catch (Exception e) {
			BaseUtil.showError("没有此部门的考评人申请记录，请核对后重试！");
		}
		String detSql="insert into KBIAssessdet(kad_id,kad_detno,kad_kaid,kad_man,kad_position,kad_selfasses,kad_detpasses,kad_otherdetp,kad_detpboss,kad_boss,kad_remark,kad_stutas) " +
				"select KBIAssessdet_seq.nextval,kad_detno,"+ka_id+",kad_man,kad_position,kad_selfasses,kad_detpasses,kad_otherdetp,kad_detpboss,kad_boss,kad_remark,'未转评估单' from KBIAssessdet where kad_kaid="+id;
		// 执行主表的插入语句
		String code = baseDao.sGetMaxNumber("KBIAssess", 2);
		String sql = "insert into KBIAssess (ka_code,ka_status,ka_recorder,ka_recorddate,ka_season,ka_detp,ka_remark,ka_statuscode,ka_id"
				+ ") select '"+code+"','在录入','"+SystemSession.getUser().getEm_name()+"',sysdate,ka_season,ka_detp,ka_remark,'ENTERING',"+ka_id+" from KBIAssess where ka_id="+id;
		baseDao.execute(sql);
		baseDao.execute(detSql);	
		// 记录操作
		baseDao.logger.save(caller, "ka_id", ka_id);
		return ka_id;
	}

}
