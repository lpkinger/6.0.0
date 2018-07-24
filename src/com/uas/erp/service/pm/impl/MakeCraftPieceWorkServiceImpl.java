package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeCraftPieceWorkService;

@Service("MakeCraftPieceWorkService")
public class MakeCraftPieceWorkServiceImpl implements MakeCraftPieceWorkService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateMakeCraftPieceWorkChange(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object ma_id = store.get("ma_id");
		SqlRowList rs = baseDao.queryForRowSet("select ma_statuscode,ma_checkstatuscode,ma_code,ma_craftcode,ma_wccode,ma_wcname from Make where ma_id=?", ma_id);
		if (rs.next()) {
			// 如果制造单checkstatuscode不等于approve或者statuscode不等于audited则不允许更新
			if (!"APPROVE".equals(rs.getObject("ma_checkstatuscode")) || !"AUDITED".equals(rs.getObject("ma_statuscode"))) {
				BaseUtil.showError("制造单必须是已审核或者已批准状态");
			}
			String ErrorStr = "";
			for (Map<Object, Object> s : gstore) {
				s.put("mcp_macode", rs.getString("ma_code"));
				s.put("mcp_craftcode", rs.getString("ma_craftcode"));
				s.put("mcp_wccode", rs.getString("ma_wccode"));
				s.put("mcp_wcname", rs.getString("ma_wcname"));
				String mcp_id = s.get("mcp_id").toString();
				String mcp_stepcode = s.get("mcp_stepcode").toString();
				String mcp_macode = s.get("mcp_macode").toString();
				if (!mcp_id.equals("") && !mcp_id.equals("0")) {
					mcp_stepcode = baseDao.getFieldDataByCondition("makecraftpiecework", "mcp_stepcode", "mcp_id=" + mcp_id).toString();
				}
				int haveDispatch = baseDao.getCount("select count(did_makecode) as haveDispatch from dispatchdetail where did_makecode='" + mcp_macode + "' and did_stepcode='" + mcp_stepcode + "'");
				if (haveDispatch > 0) {
					ErrorStr += mcp_stepcode + ",";
				}
			}
			if (ErrorStr != "") {
				ErrorStr = ErrorStr.substring(0, ErrorStr.length() - 1);
				BaseUtil.showError("明细[" + ErrorStr + "]已产生生产日报，不允许更新   ");
			}
			// 执行审核前的其它逻辑
			handlerService.beforeUpdate(caller, new Object[] { ma_id });
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "MakeCraftPieceWork", "mcp_id"));
			// 检查执行顺序，出现错误回滚
			SqlRowList rs1 = baseDao.queryForRowSet("select mcp_stepno from makecraftpiecework where mcp_maid=" + ma_id);
			String[] stepno = new String[rs1.size()];
			while (rs1.next()) {
				stepno[rs1.getCurrentIndex()] = rs1.getString("mcp_stepno");
			}
			if (BaseUtil.checkDuplicateArray(stepno)) {
				BaseUtil.showError("请检出执行顺序，不允许出现重复");
			}
			// 记录日志
			baseDao.logger.update(caller, "ma_id", ma_id);
			handlerService.afterUpdate(caller, new Object[] { ma_id });
		} else {
			BaseUtil.showError("制造单不存在或者已删除");
		}
	}

	@Override
	public void deleteDetail(String caller, int id) {
		// ID是ma_id
		Object ma_code = baseDao.getFieldDataByCondition("MAKE", "ma_code", "ma_id=" + id);
		int bool = baseDao.getCount("select count(1) from Dispatchdetail where DID_MAKECODE='" + ma_code + "'");
		if (bool > 0) {
			BaseUtil.showError("该制造单已产生生产日报，不允许删除");
		}
		// 执行审核前的其它逻辑
		baseDao.execute(SqlUtil.getDeleteSql("MakeCraftPieceWork", "mcp_maid='" + id + "'"));
		// 记录日志
		baseDao.logger.others("删除明细", "成功", caller, "ma_id", id);
	}

	@Override
	public void loadPeople(String makecode, String prodcode) {
		SqlRowList rsQ = baseDao.queryForRowSet("select ma_statuscode,ma_checkstatuscode,ma_craftcode,ma_wccode,ma_wcname from Make where ma_code=?", makecode);
		if (rsQ.next()) {
			// 如果制造单checkstatuscode不等于approve或者statuscode不等于audited则不允许载入
			if (!"APPROVE".equals(rsQ.getObject("ma_checkstatuscode")) || !"AUDITED".equals(rsQ.getObject("ma_statuscode"))) {
				BaseUtil.showError("制造单必须是已审核或者已批准状态");
			}
			int cn = baseDao.getCount("select count(1) from Dispatchdetail where did_makecode='" + makecode + "'");
			// 如果工作日报中的did_makecode存在则不允许载入
			if (cn > 0) {
				BaseUtil.showError("该制造单已产生生产日报，不允许载入加工人！");
			}
			SqlRowList rs = baseDao.queryForRowSet("select ma_code from (select rownum rn,ma_code from make left join MakeCraftPieceWork on mcp_macode=ma_code where ma_code<>?" + " and ma_prodcode=? and nvl(mcp_processmancode,' ')<>' ' order by ma_indate desc )T where rn=1", makecode, prodcode);
			if(rs.next()){
				rs = baseDao.queryForRowSet("select mcp_processmancode,mcp_processman,mcp_stepcode,mcp_stepno from MakeCraftPieceWork where mcp_macode=?",rs.getString("ma_code"));
				while (rs.next()) {
					baseDao.execute("update MakeCraftPieceWork set mcp_processmancode=?,mcp_processman=? where mcp_macode=? and mcp_stepcode=? and mcp_stepno=?",rs.getString("mcp_processmancode"),rs.getString("mcp_processman"),makecode,rs.getString("mcp_stepcode"),rs.getGeneralString("mcp_stepno"));
				} 
			}else {
				BaseUtil.showError("该制造单找不到上一个加工人，请手动添加加工人！");
			}
		} else {
			BaseUtil.showError("制造单不存在或者已删除");
		}
	}

}
