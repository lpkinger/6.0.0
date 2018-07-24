package com.uas.pda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Page;
import com.uas.pda.service.PdaCountingService;

@Service("pdaCountingServiceImpl")
public class PdaCountingServiceImpl implements PdaCountingService{
     @Autowired
    private BaseDao baseDao;
	@Override
	public Page<Map<String, Object>> getCountingData(String st_code) {
		final SqlRowList rs = baseDao.queryForRowSet("select st_id ,st_code ,st_whcode,st_statuscode from stockTaking where st_code='" + st_code + "'");
		if (rs.next()) {
			if (rs.getString("st_statuscode").equals("ENTERING")) {
				return new Page<Map<String, Object>>() {
					@Override
					public int getTotalCount() {
						return rs.size();
					}

					@Override
					public List<Map<String, Object>> getTarget() {
						return rs.getResultList();
					}
				};
			} else {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该盘点底稿的状态为：" + rs.getString("st_statuscode"));
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"盘点底稿编号不存在");
		}
	}
	@Override
	public Map<String, Object> getBarData(String bar_code, String bar_whcode,String st_code) {
		SqlRowList rs = baseDao.queryForRowSet("select bar_code,bar_whcode,bar_remain,bar_prodcode,bar_id,bar_vendcode,pr_detail from barcode left join product on pr_code=bar_prodcode left join StockTakingDetail on std_prodcode=bar_prodcode  where bar_code='" + bar_code + "' and bar_whcode='" + bar_whcode + "' and std_code='" + st_code + "'");
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该条码号不存在！");
		}
		return rs.getCurrentMap();
	}
	@Override
	public void saveBarcode(String data) {
		// 提交校验：判断待提交的数据barcode不跟stocktakingbarcode里的重复
		SqlRowList rs;
		List<Map<Object, Object>> gstore = FlexJsonUtil.fromJsonArray(data, HashMap.class);
		for (Map<Object, Object> map : gstore) {
			Object stb_barcode = map.get("stb_barcode");
			Object stb_outboxcode = map.get("stb_outboxcode");
			Object stb_prodcode = map.get("stb_prodcode");
			Object stb_stcode = map.get("stb_stcode");
			if (!"".equals(stb_barcode) && null == stb_barcode) {
				rs = baseDao.queryForRowSet("select  stb_barcode ,count(0) cn from stocktakingbarcode where stb_prodcode='" + stb_prodcode + "'and stb_barcode ='" + stb_barcode + "'and stb_stcode='" + stb_stcode + "' group by stb_barcode");
				if (rs.next()) {
					if (rs.getInt(0) > 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码编号：[" + rs.getString("stb_barcode") + "]重复");
				}
			}
			if (stb_outboxcode != null) {
				rs = baseDao.queryForRowSet("select  stb_outboxcode ,count(0) cn from stocktakingbarcode where stb_prodcode='" + stb_prodcode + "'and stb_outboxcode ='" + stb_outboxcode + "'and stb_stcode='" + stb_stcode + "' group by stb_barcode");
				if (rs.next()) {
					if (rs.getInt(0) > 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号：[" + rs.getString("stb_outboxcode") + "]重复");
				}
			}
			map.put("stb_id", baseDao.getSeqId("STOCKTAKINGBARCODE_SEQ"));
			map.remove("pr_detail");
			map.remove("PR_DETAIL");
			map.remove("PR_ID");
			map.remove("MS_MAKECODE");
		}

		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "STOCKTAKINGBARCODE");
		baseDao.execute(gridSql);
	}
	@Override
	public Map<String, Object> serialSearch(String code, String whcode,String st_code) {
		SqlRowList rs = baseDao.queryForRowSet("select bar_id as \"stb_barid\",bar_code as \"stb_barcode\",bar_remain as \"stb_qty\", pr_code as \"stb_prodcode\",pr_detail,ms_makecode ,bar_vendcode as \"stb_vendcode\" from barcode left join product on pr_code=bar_prodcode left join MakeSerial on ms_sncode=bar_code left join StockTakingDetail on std_prodcode=bar_prodcode where bar_code='" + code + "' and bar_whcode='" + whcode + "' and std_code='" + st_code + "'");
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该序列号不存在！");
		}
		return rs.getCurrentMap();
	}
	@Override
	public Map<String, Object> outboxSearch(String code, String whcode,String st_code) {
		SqlRowList rs = baseDao.queryForRowSet("select pa_id as \"stb_outboxid\",pa_prodcode as \"stb_prodcode\",pa_outboxcode as \"stb_outboxcode\", pa_totalqty as \"stb_qty\",pr_id,pr_detail from PACKAGE left join product on pr_code=pa_prodcode left join StockTakingDetail on std_prodcode=pa_prodcode where pa_outboxcode='" + code + "' and std_code='" + st_code + "'");
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该包装箱号不存在！");
		}
		Map<String, Object> map = rs.getCurrentMap();
		return map;
	}
}
