package com.uas.pda.service.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.service.PdaTailingBackService;

@Service("pdaTailingBackService")
public class PdaTailingBackServiceImpl implements PdaTailingBackService{
    @Autowired
    private BaseDao baseDao;

	@Override
	public Map<String,Object> getForcastRemain(String code) {
		SqlRowList rs = baseDao
				.queryForRowSet("select nvl(bar_place,'0') bar_place,nvl(bar_remain,0)bar_remain,bar_forcastremain,bar_location,bar_prodcode from barcode where bar_code='"
						+ code + "' and nvl(bar_status,0)=0");
		if (rs.next()) {
			if (!rs.getString("bar_place").equals("1")) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + "不是下线状态，不需要还仓!");
			}
			if (rs.getDouble("bar_remain") == 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + "剩余数为0，不需要还仓!");
			}
			// 获取预计剩余数
			return rs.getCurrentMap();
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + "不存在或者无效!");
		}
	}


	@Override
	public void tailingBack(String data) {
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(data);
		String code = map.get("bar_code").toString();
		getForcastRemain(code);
		baseDao.updateByCondition("barcode", "bar_forcastremain="+map.get("bar_fremain")+",bar_location='"+map.get("bar_location")+"',bar_remain="+map.get("bar_fremain")+",bar_place=''", "bar_code='"+code+"'");
		baseDao.logger.getMessageLog("尾料还仓", "成功", "TailingBack", "bar_code", code);
	}

}
