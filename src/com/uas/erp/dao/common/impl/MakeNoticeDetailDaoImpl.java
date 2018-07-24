package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeNoticeDetailDao;

@Repository
public class MakeNoticeDetailDaoImpl extends BaseDao implements MakeNoticeDetailDao{
	static final String TURNMAKE = "SELECT mnd_code,mnd_needdate,mnd_prid,mnd_prodcode,mnd_wccode,mnd_wcid,mnd_planbegindate,mnd_planenddate" + 
			",mnd_ordercode,mnd_orderdetno,mnd_mnid FROM makenoticedetail WHERE mnd_id=?";
	static final String INSERTMAKE = "INSERT INTO make(ma_id,ma_code,ma_status,ma_statuscode,ma_sourceid,ma_source,ma_requiredate,ma_prodid,ma_prodcode,ma_wccode,ma_workcenterid," +
			"ma_qty,ma_planbegindate,ma_planenddate,ma_salecode,ma_saledetno,ma_recorderid,ma_recorder,ma_recorddate,ma_tasktype) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	@Override
	@Transactional
	public Map<String, Object> turnMake(int id, double tqty) {
		try {
			SqlRowList rs = queryForRowSet(TURNMAKE, new Object[]{id});
			int maid = 0;
			if(rs.next()){
				maid = getSeqId("MAKE_SEQ");
				String code = sGetMaxNumber("Make!Base", 2);
				execute(INSERTMAKE, new Object[]{maid,code,BaseUtil.getLocalMessage("ENTERING"),"ENTERING",id,rs.getString(1),rs.getObject(2),
						rs.getObject(3),rs.getObject(4),rs.getObject(5),rs.getObject(6),tqty,rs.getObject(7),rs.getObject(8),rs.getObject(9),rs.getObject(10),
						SystemSession.getUser().getEm_id(),SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),"MAKE"});
				
				//修改makenotice和makenoticedetail状态
				updateByCondition("MakeNoticeDetail", "mnd_statuscode='PARTMA',mnd_status='" + 
						BaseUtil.getLocalMessage("PARTMA") + "',mnd_tomakeqty=mnd_tomakeqty+" + tqty, "mnd_id=" + id);
				updateByCondition("MakeNoticeDetail", "mnd_statuscode='TURNMA',mnd_status='" + 
						BaseUtil.getLocalMessage("TURNMA") + "'", "mnd_id=" + id + " AND mnd_tomakeqty=mnd_qty");
				int count1 = getCountByCondition("MakeNoticeDetail", "mnd_mnid=" + rs.getObject(11));
				int count2 = getCountByCondition("MakeNoticeDetail", "mnd_mnid=" + rs.getObject(11) + " AND mnd_statuscode='TURNMA'");
				if(count1 == count2){
					updateByCondition("MakeNotice", "mn_statuscode='TURNMA',mn_status='" + 
							BaseUtil.getLocalMessage("TURNMA") + "'", "mn_id=" + rs.getObject(11));
				} else {
					updateByCondition("MakeNotice", "mn_statuscode='PARTMA',mn_status='" + 
							BaseUtil.getLocalMessage("PARTMA") + "'", "mn_id=" + rs.getObject(11));
				}
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("code", code);
				m.put("id", maid);
				return m;
			} else {
				return null;
			}
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}
	public Map<String, Object> turnOutSource(int id, double tqty) {
		try {
			SqlRowList rs = queryForRowSet(TURNMAKE, new Object[]{id});
			int maid = 0;
			if(rs.next()){
				maid = getSeqId("MAKE_SEQ");
				String code = sGetMaxNumber("Make", 2);
				execute(INSERTMAKE, new Object[]{maid,code,BaseUtil.getLocalMessage("ENTERING"),"ENTERING",id,rs.getString(1),rs.getObject(2),
						rs.getObject(3),rs.getObject(4),rs.getObject(5),rs.getObject(6),tqty,rs.getObject(7),rs.getObject(8),rs.getObject(9),rs.getObject(10),
						SystemSession.getUser().getEm_id(),SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),"OS"});
				
				//修改makenotice和makenoticedetail状态
				updateByCondition("MakeNoticeDetail", "mnd_statuscode='PART2OSMA',mnd_status='" + 
						BaseUtil.getLocalMessage("PART2OSMA") + "',mnd_tomakeqty=mnd_tomakeqty+" + tqty, "mnd_id=" + id);
				updateByCondition("MakeNoticeDetail", "mnd_statuscode='TURNOSMA',mnd_status='" + 
						BaseUtil.getLocalMessage("TURNOSMA") + "'", "mnd_id=" + id + " AND mnd_tomakeqty=mnd_qty");
				int count1 = getCountByCondition("MakeNoticeDetail", "mnd_mnid=" + rs.getObject(11));
				int count2 = getCountByCondition("MakeNoticeDetail", "mnd_mnid=" + rs.getObject(11) + " AND mnd_statuscode='TURNOSMA'");
				if(count1 == count2){
					updateByCondition("MakeNotice", "mn_statuscode='TURNOSMA',mn_status='" + 
							BaseUtil.getLocalMessage("TURNOSMA") + "'", "mn_id=" + rs.getObject(11));
				} else {
					updateByCondition("MakeNotice", "mn_statuscode='PART2OSMA',mn_status='" + 
							BaseUtil.getLocalMessage("PART2OSMA") + "'", "mn_id=" + rs.getObject(11));
				}
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("code", code);
				m.put("id", maid);
				return m;
			} else {
				return null;
			}
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}
}
