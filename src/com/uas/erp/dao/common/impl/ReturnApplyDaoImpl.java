package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ReturnApplyDao;

@Repository
public class ReturnApplyDaoImpl extends BaseDao implements ReturnApplyDao {

	@Override
	public void checkRADQty(int radid) {
		String status = null;
		Object ra_id = getFieldDataByCondition("ReturnApplyDetail", "rad_raid", "rad_id = "+ radid);
		int count = getCountByCondition("ReturnApplyDetail", "rad_raid=" + ra_id);
		int yCount = getCountByCondition("ReturnApplyDetail", "rad_raid=" + ra_id
				+ " and rad_yqty=rad_qty and nvl(rad_yqty,0)>0");
		int xCount = getCountByCondition("ReturnApplyDetail", "rad_raid=" + ra_id + " and nvl(rad_yqty,0)=0");
		status = "PARTSR";
		if (yCount == count) {
			status = "TURNSR";
		}
		if (xCount == count) {
			status = "";
		}
		updateByCondition("ReturnApply",
				"ra_turnstatuscode='" + status + "',ra_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"ra_id=" + ra_id);
	}
}
