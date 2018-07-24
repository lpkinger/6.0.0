package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.RationService;

@Service
public class RationServiceImpl implements RationService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateRation(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		Object topbomid = store.get("ra_topbomid");
		int count = baseDao.getCount("select count(1) from ration where ra_topbomid=" + store.get("ra_topbomid"));
		if (count == 0) {
			// 插入Ration
			baseDao.execute("insert into ration (ra_id,ra_detno,RA_MOTHERCODE,RA_TOPBOMID,RA_TOPMOTHERCODE,RA_STEPCODE,RA_STEPNAME,"
					+ "RA_WCCODE,RA_WCNAME,RA_STATUS,RA_RECORDER,RA_INDATE,RA_STATUSCODE) select ration_seq.nextval,rownum,t.* from (select bs_soncode,"
					+ topbomid
					+ ",bo_mothercode,cd_stepcode,cd_stepname,bo_wccode,wc_name,'"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "','"
					+ SystemSession.getUser().getEm_name()
					+ "',sysdate,'ENTERING' "
					+ "FROM BOM left join BOMSTRUCT on BO_MOTHERCODE=BS_SONCODE left join CRAFT on BO_CRAFTCODE=CR_CODE left join CRAFTDETAIL on CR_ID=CD_CRID left join WORKCENTER on BO_WCCODE=wc_code "
					+ "WHERE BS_TOPBOMID=" + topbomid
					+ " and (bs_sonbomid>0 or bs_idcode=0) and nvl(BS_SUPPLYTYPE,' ')<>'VIRTUAL' order by cd_detno asc,BS_LEVEL desc) t");
		} else {
			// 修改Ration
			baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(gridStore, "Ration", "ra_id"));
		}
		baseDao.execute("update ration set ra_bomid=ra_topbomid where ra_topbomid=" + topbomid);
		// 记录操作
		baseDao.logger.update(caller, "ra_topbomid", store.get("ra_topbomid"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitRation(int id, String caller) {
		baseDao.submit(" ration", "ra_topbomid = " + id, "ra_status", "ra_statuscode");
	}

	@Override
	public void resSubmitRation(int id, String caller) {
		baseDao.resOperate("ration", "ra_topbomid = " + id, "ra_status", "ra_statuscode");
	}

	@Override
	public void auditRation(int id, String caller) {
		baseDao.audit(" ration", "ra_topbomid = " + id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
	}

	@Override
	public void resAuditRation(int id, String caller) {
		baseDao.resAudit("ration", "ra_topbomid = " + id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
	}

	@Override
	public void deleteRation(int id, String caller) {
		baseDao.execute("delete from ration where ra_topbomid = " + id + " and ra_statuscode='ENTERING'");
	}
}