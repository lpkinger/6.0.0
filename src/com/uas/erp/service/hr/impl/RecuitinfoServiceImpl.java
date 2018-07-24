package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormAttach;
import com.uas.erp.model.Master;
import com.uas.erp.service.hr.RecuitinfoService;

@Service
public class RecuitinfoServiceImpl implements RecuitinfoService {

	static final String turnSql = "select re_name,re_age,re_sex,re_recuipos from Recuitinfo where re_id=?";
	static final String turnmainSql = "insert into Careerapply(ca_code,ca_recordorid,ca_recordor,ca_date,"
			+ "ca_status,ca_statuscode,ca_isturn,ca_id)values(?,?,?,?,?,?,?,?)";
	static final String turndetailSql = "insert into Careerapplydetail(cd_detno,cd_name,cd_age,cd_sex,"
			+ "cd_position,cd_caid,cd_id,cd_sourceid,cd_departcode,cd_depart,cd_defaultorid,cd_defaultorcode,cd_hrorg,cd_defaulthsid,cd_defaulthscode)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String updateSql = "update recuitinfo set re_isincaree='1',re_enroll='是' where re_id=?";
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private FormAttachDao formAttachDao;

	@Autowired
	private DataListDao dataListDao;
	
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveRecuitinfo(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String filesId = null;
		if (store.containsKey("files")) {
			filesId = (String) store.get("files");
			store.remove("files");
		}

		handlerService.beforeSave(caller, new Object[] { store });
		// 名字设为唯一
		if (baseDao.getCount("select count(*) from Recuitinfo where re_name='"
				+ store.get("re_name") + "'") > 0) {
			BaseUtil.showError("该名字已存在,请核对后重试!");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Recuitinfo",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存附件
		if (filesId != null) {
			String[] files = filesId.split(",");
			int id = Integer.parseInt(store.get("re_id").toString());
			String path = null;
			FormAttach attach = null;
			for (String file : files) {
				if (file != null && !file.equals("")) {
					Object res = baseDao.getFieldDataByCondition("filepath",
							"fp_path", "fp_id=" + Integer.parseInt(file));
					if (res != null) {
						path = (String) res;
						attach = new FormAttach();
						attach.setFa_caller("Recuitinfo");
						attach.setFa_keyvalue(id);
						attach.setFa_path(path);
						formAttachDao.saveAttach(attach);
					}
				}
			}
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "re_id", store.get("re_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateRecuitinfoById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 名字设为唯一
		if (baseDao.getCount("select count(*) from Recuitinfo where re_name='"
				+ store.get("re_name") + "' and re_id<>"+store.get("re_id")) > 0) {
			BaseUtil.showError("该名字已存在,请核对后重试!");
		}
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Recuitinfo",
				"re_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteRecuitinfo(int re_id, String caller) {
		handlerService.beforeDel(caller, new Object[] { re_id });
		// 删除
		baseDao.deleteById("Recuitinfo", "re_id", re_id);
		// 记录操作
		baseDao.logger.delete(caller, "re_id", re_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { re_id });
	}

	@Override
	public void vastWriteexam(String caller, int[] id) {

		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String tablename = dataList.getDl_tablename();

		for (int key : id) {
			baseDao.updateByCondition(tablename, " re_wriexam ='是'", " re_id="
					+ key);
		}

	}

	@Override
	public void vastinterview(String caller, int[] id) {

		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String tablename = dataList.getDl_tablename();

		for (int key : id) {
			baseDao.updateByCondition(tablename, " re_interview ='是'",
					" re_id=" + key);
		}

	}

	@Override
	public void vastJointalcpool(String caller, int[] id) {
		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String tablename = dataList.getDl_tablename();

		for (int key : id) {
			baseDao.updateByCondition(tablename, " re_jointalcpool ='是'",
					" re_id=" + key);
		}

	}

	@Override
	public void vastWritemark(String caller, int[] id, int[] mark) {

		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String tablename = dataList.getDl_tablename();

		int i, idValue, writeMark;
		for (i = 0; i < id.length; i++) {
			idValue = id[i];
			writeMark = mark[i];
			baseDao.updateByCondition(tablename, " re_mark =" + writeMark,
					" re_id=" + idValue);
		}

	}

	@Override
	public void vastInterviewmark(String caller, int[] id, int[] mark) {

		DataList dataList = dataListDao.getDataList(caller, SystemSession
				.getUser().getEm_master());
		String tablename = dataList.getDl_tablename();

		int i, idValue, writeMark;
		for (i = 0; i < id.length; i++) {
			idValue = id[i];
			writeMark = mark[i];
			baseDao.updateByCondition(tablename, " re_intermark =" + writeMark,
					" re_id=" + idValue);
		}

	}

	@Override
	public void vastTurnrecruitplan(String caller, int[] id) {

		int i = 0, idvalue;
		int careeId = baseDao.getSeqId("Careerapply_SEQ");
		String code = baseDao.sGetMaxNumber("Careerapply", 2);
		int careDetailId;
		try {
			boolean bool = baseDao.execute(
					turnmainSql,
					new Object[] {
							code,
							SystemSession.getUser().getEm_id(),
							SystemSession.getUser().getEm_name(),
							Timestamp.valueOf(DateUtil
									.currentDateString(Constant.YMD_HMS)),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
							"0", careeId });
			if (bool) {
				for (i = 0; i < id.length; i++) {
					idvalue = id[i];
					SqlRowList rs = baseDao.queryForRowSet(turnSql,
							new Object[] { idvalue });
					careDetailId = baseDao.getSeqId("Careerapplydetail_SEQ");
					if (rs.next()) {
						baseDao.execute(
								turndetailSql,
								new Object[] { i + 1, rs.getObject(1),
										rs.getObject(2), rs.getObject(3),
										rs.getObject(4), careeId, careDetailId,
										idvalue});
						baseDao.execute(updateSql, new Object[] { idvalue });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			BaseUtil.showError("数据异常,转入失败");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnJointalcpool(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("Recuitinfo", " re_jointalcpool ='是'",
					" re_id=" + map.get("re_id"));
		}
		return "转入成功！";
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnrecruitplan(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String log = null;
		int idvalue = 0;
		int careDetailId;
		int index = 0;
		if (maps.size() > 0) {
			int ca_id = baseDao.getSeqId("Careerapply_SEQ");
			String code = baseDao.sGetMaxNumber("Careerapply", 2);
			boolean bool = baseDao.execute(
					turnmainSql,
					new Object[] {
							code,
							SystemSession.getUser().getEm_id(),
							SystemSession.getUser().getEm_name(),
							Timestamp.valueOf(DateUtil
									.currentDateString(Constant.YMD_HMS)),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
							"0", ca_id });
			if (bool) {
				for (Map<Object, Object> map : maps) {
					idvalue = Integer.parseInt(map.get("re_id").toString());
					SqlRowList rs = baseDao.queryForRowSet(turnSql,
							new Object[] { idvalue });
					careDetailId = baseDao.getSeqId("Careerapplydetail_SEQ");
					if (rs.next()) {
						Object[] ob=baseDao.getFieldsDataByCondition("job left join hrorg on JO_ORGID=or_id",new String[]{"nvl(jo_id,0)","nvl(jo_code,'')",
								"nvl(or_id,0)","nvl(or_code,'')","nvl(or_name,'')","nvl(or_departmentcode,'')","nvl(or_department,'')"}, "jo_name='"+rs.getObject(4)+"'");
						if(ob==null){
							BaseUtil.showError("应聘岗位:"+rs.getObject(4)+"不存在,请确认");
						}
						baseDao.execute(
								turndetailSql,
								new Object[] { index + 1, rs.getObject(1),
										rs.getObject(2), rs.getObject(3),
										rs.getObject(4), ca_id, careDetailId,
										idvalue,ob[5],ob[6],ob[2],ob[3],ob[4],ob[0],ob[1]});
						baseDao.execute(updateSql, new Object[] { idvalue });
					}
				}
				log = "转入成功,录用申请单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/recruitment/careerapply.jsp?formCondition=ca_idIS" + ca_id
						+ "&gridCondition=cd_caidIS" + ca_id + "')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
			}
		}
		return sb.toString();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void submitRecuitinfo(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Recuitinfo", new String[] { "re_statuscode"}, "re_id=" + id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit("Recuitinfo", "re_id=" + id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "re_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void resSubmitRecuitinfo(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Recuitinfo", "re_statuscode", "re_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("Recuitinfo", "re_id=" + id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "re_id", id);
		handlerService.afterResSubmit(caller, id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void auditRecuitinfo(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Recuitinfo", new String[] { "re_statuscode"}, "re_id=" + id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("Recuitinfo", "re_id=" + id, "re_status", "re_statuscode","RE_AUDITDATE","RE_AUDITMAN");
		// 记录操作
		baseDao.logger.audit(caller, "re_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void resAuditRecuitinfo(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Recuitinfo", "re_statuscode", "re_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.resAudit("Recuitinfo", "re_id=" + id, "re_status", "re_statuscode","RE_AUDITDATE","RE_AUDITMAN");
		// 记录操作
		baseDao.logger.resAudit(caller, "re_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}
}
