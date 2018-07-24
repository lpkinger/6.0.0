package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KBIChangeManService;

@Service
public class KBIChangeManServiceImpl implements KBIChangeManService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveKBIChangeMan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store});
		// 保存KBIChangeMan
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KBIChangeMan",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kc_id", store.get("kc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}

	@Override
	public void deleteKBIChangeMan(int kc_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("KBIChangeMan",
				"kc_statuscode", "kc_id=" + kc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {kc_id});
		// 删除KBIChangeMan
		baseDao.deleteById("KBIChangeMan", "kc_id", kc_id);
		// 记录操作
		baseDao.logger.delete(caller, "kc_id", kc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {kc_id});
	}

	@Override
	public void updateKBIChangeManById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("KBIChangeMan",
				"kc_statuscode", "kc_id=" + store.get("kc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] {store});
		// 修改KBIChangeMan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KBIChangeMan",
				"kc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "kc_id", store.get("kc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] {store});
	}

	@Override
	public void submitKBIChangeMan(int kc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIChangeMan",
				"kc_statuscode", "kc_id=" + kc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {kc_id});
		// 执行提交操作
		baseDao.submit("KBIChangeMan", "kc_id=" + kc_id, "kc_status", "kc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "kc_id", kc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {kc_id});
	}

	@Override
	public void resSubmitKBIChangeMan(int kc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KBIChangeMan",
				"kc_statuscode", "kc_id=" + kc_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("KBIChangeMan", "kc_id=" + kc_id, "kc_status", "kc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "kc_id", kc_id);
	}

	@Override
	public void auditKBIChangeMan(int kc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KBIChangeMan",
				"kc_statuscode", "kc_id=" + kc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {kc_id});
		// 执行审核操作
		baseDao.audit("KBIChangeMan", "kc_id=" + kc_id, "kc_status", "kc_statuscode", "kc_auditdate", "kc_auditer");
		//原评估单作废
		Object[] data=baseDao.getFieldsDataByCondition("KBIChangeMan",
				new String[]{"kc_kbcode","kc_newkbrecorder"}, "kc_id=" + kc_id);
		baseDao.updateByCondition("KBIbill","kb_statuscode='NULLIFIED',kb_status='已作废'", "kb_code='"+data[0]+"'");
		//创建新的评估单
		String code=baseDao.sGetMaxNumber("KBIbill", 2);
		int id=baseDao.getSeqId("KBIbill_seq");
		String sql="insert into KBIbill(kb_id,kb_code,kb_recorder,kb_season,kb_kacode,kb_assessman,kb_position,kb_status,kb_statuscode,kb_attribution,kb_scheme) select "+id+",'"+code+"','" +
				data[1]+"',kb_season,kb_kacode,kb_assessman,kb_position,'在录入','ENTERING',kb_attribution,kb_scheme from KBIbill where kb_code='"+data[0]+"'";
		baseDao.execute(sql);
		List<Object[]> datas=baseDao.getFieldsDatasByCondition("KBIStand", new String[]{"ks_id","ks_key","ks_stand","ks_source"}, "ks_statuscode='AUDITED' order by ks_id");
		String detSql="insert into KBIbilldet(kbd_id,kbd_kbid,kbd_detno,kbd_element,kbd_target,kbd_ksid,kbd_gist)values(KBIbilldet_seq.nextval,?,?,?,?,?,?)";
		int detno=1;
		for(Object[] d:datas){
			baseDao.execute(detSql, new Object[]{id,detno++,d[1],d[2],d[0],d[3]});
		}
		// 记录操作
		baseDao.logger.audit(caller, "kc_id", kc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {kc_id});
	}

}
