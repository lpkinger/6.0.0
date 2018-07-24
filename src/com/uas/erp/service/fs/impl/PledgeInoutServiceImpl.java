package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.PledgeInoutService;

@Service
public class PledgeInoutServiceImpl implements PledgeInoutService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePledgeInout(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FSPledgeInout", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "pio_id", store.get("pio_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updatePledgeInout(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FSPledgeInout", "pio_id");
		baseDao.execute(formSql);
		//更新最近更新日期
		baseDao.updateByCondition("FSPledgeInout", "pio_lastupdate=sysdate", "pio_id="+store.get("pio_id"));
		//记录操作
		baseDao.logger.update(caller, "pio_id", store.get("pio_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deletePledgeInout(int pio_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pio_id});
		//删除
		baseDao.deleteById("FSPledgeInout", "pio_id", pio_id);
		//记录操作
		baseDao.logger.delete(caller, "pio_id", pio_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pio_id});
	}

	@Override
	public void submitPledgeInout(int pio_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FSPledgeInout",
				"pio_statuscode", "pio_id=" + pio_id);
		if(status==null){
			BaseUtil.showError("该单已不存在");
		}
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		//检查入库、出库操作是否能够对应上抵质押物的状态
		checkClass(pio_id);
		//检查同一抵质押物是否存在其他已提交、未审核的单据
		checkPledge(pio_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pio_id);
		// 执行提交操作
		baseDao.submit("FSPledgeInout", "pio_id="+pio_id, "pio_status", "pio_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pio_id", pio_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pio_id);
	}

	@Override
	public void resSubmitPledgeInout(int pio_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FSPledgeInout",
				"pio_statuscode", "pio_id=" + pio_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, pio_id);
		// 执行反提交操作
		baseDao.resOperate("FSPledgeInout", "pio_id="+pio_id, "pio_status", "pio_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pio_id", pio_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, pio_id);
	}

	@Override
	public void auditPledgeInout(int pio_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FSPledgeInout",
				"pio_statuscode", "pio_id=" + pio_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		//检查入库、出库操作是否能够对应上抵质押物的状态
		checkClass(pio_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pio_id);
		// 执行审核操作
		baseDao.audit("FSPledgeInout", "pio_id="+pio_id, "pio_status", "pio_statuscode", "pio_auditdate", "pio_auditman");
		//审核后，修改抵押物信息中的状态
		Object[] pio_data = baseDao.getFieldsDataByCondition("FSPledgeInout", "pio_class,pio_plcode,pio_plstatus", "pio_id="+pio_id);
		if(pio_data!=null&&pio_data[0]!=null&&pio_data[1]!=null){
			if(pio_data[0].equals("入库")){
				baseDao.updateByCondition("FSPLEDGE", "pl_fspledgestatus='已入库'", "pl_code='"+pio_data[1]+"'");
				//单据类型是入库，抵质押物类型是临时出库的
				if(pio_data[2]!=null&&pio_data[2].equals("临时出库")){
					//找到最近的一个状态为已审核，同抵押物单号（pio_plcode），单据类型是临时出库，且实际回库日期是空的 单据
					Object outid = baseDao.getFieldDataByCondition("(select * from FSPLEDGEINOUT where pio_plcode='"+pio_data[1]+"' and pio_class='临时出库' and pio_realdate is null and pio_statuscode='AUDITED' order by pio_date desc)",
							"pio_id", "rownum=1");
					if(outid!=null){   //修改实际回库日期为审核单据的入库日期
						baseDao.execute("update FSPLEDGEINOUT set pio_realdate=(select pio_indate from FSPLEDGEINOUT where pio_id="+pio_id+")"
								+ " where pio_id="+outid+" ");
					}
				}
			}else if(pio_data[0].equals("出库")){
				baseDao.updateByCondition("FSPLEDGE", "pl_fspledgestatus='已出库'", "pl_code='"+pio_data[1]+"'");
			}else{
				baseDao.updateByCondition("FSPLEDGE", "pl_fspledgestatus='"+pio_data[0]+"'", "pl_code='"+pio_data[1]+"'");
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "pio_id", pio_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pio_id);
	}

	@Override
	public void resAuditPledgeInout(int pio_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FSPledgeInout",
				"pio_statuscode", "pio_id=" + pio_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, pio_id);
		// 执行反审核操作
		baseDao.resAudit("FSPledgeInout", "pio_id="+pio_id, "pio_status", "pio_statuscode", "pio_auditman", "pio_auditdate");
		//反审核后，修改抵押物信息中的状态为原来的状态
		Object[] pio_data = baseDao.getFieldsDataByCondition("FSPledgeInout", "pio_plstatus,pio_plcode,pio_plstatus", "pio_id="+pio_id);
		if(pio_data!=null&&pio_data[0]!=null&&pio_data[1]!=null){
			baseDao.updateByCondition("FSPLEDGE", "pl_fspledgestatus='"+pio_data[0]+"'", "pl_code='"+pio_data[1]+"'");
			//单据类型是入库，抵质押物类型是临时出库的
			if(pio_data[2]!=null&&pio_data[2].equals("临时出库")){
				//找到最近的一个状态为已审核，同抵押物单号（pio_plcode），单据类型是临时出库，且实际回库日期是空的 单据
				Object outid = baseDao.getFieldDataByCondition("(select * from FSPLEDGEINOUT where pio_plcode='"+pio_data[1]+"' and pio_class='临时出库' and pio_statuscode='AUDITED' order by pio_date desc)",
						"pio_id", "rownum=1");
				if(outid!=null){   //修改实际回库日期为空
					baseDao.execute("update FSPLEDGEINOUT set pio_realdate=null"
							+ " where pio_id="+outid+" ");
				}
			}
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "pio_id", pio_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, pio_id);
	}

	/**
	 * 检查入库、出库操作是否能够对应上抵质押物的状态
	 */
	private void checkClass(int pio_id){
		Object[] pio_data = baseDao.getFieldsDataByCondition("FSPledgeInout", "pio_class,pio_plcode", "pio_id="+pio_id);
		if(pio_data!=null&&pio_data[0]!=null&&pio_data[1]!=null){
			if(pio_data[0].equals("入库")){
				int count = baseDao.getCount("select pl_id from FSPledge where pl_code='"+pio_data[1]+"' and pl_fspledgestatus in ('未入库','已出库','临时出库')");
				if(count==0){
					BaseUtil.showError("只能对未入库、已出库、临时出库的抵质押物进行入库操作");
				}
			}else if(pio_data[0].equals("出库")||pio_data[0].equals("临时出库")){
				int count = baseDao.getCount("select pl_id from FSPledge where pl_code='"+pio_data[1]+"' and pl_fspledgestatus in ('已入库')");
				if(count==0){
					BaseUtil.showError("只能对已入库的抵质押物进行出库操作");
				}
			}
		}
		//提交前更新出入库表中的抵质押物状态
		baseDao.updateByCondition("FSPledgeInout", "pio_plstatus=(select pl_fspledgestatus from FSPledge where pl_code='"+pio_data[1]+"')", "pio_id="+pio_id);
	}
	/**
	 * 检查同一抵质押物是否存在其他已提交、未审核的单据
	 */
	private void checkPledge(int pio_id){
		Object pio_plcode = baseDao.getFieldDataByCondition("FSPledgeInout", "pio_plcode", "pio_id="+pio_id);
		if(pio_plcode!=null){
			Object codes = baseDao.getFieldDataByCondition("FSPLEDGEinout", "wm_concat(pio_code)", "pio_plcode='"+pio_plcode+"' and pio_statuscode='COMMITED'");
			if(codes!=null&&!codes.equals("")&&!codes.equals("null")){
				BaseUtil.showError("当前质押物存在其他未审核的出入库单！单号："+codes);
			}
		}
	}
}
