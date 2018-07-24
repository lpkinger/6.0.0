package com.uas.pda.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.service.PdaCheckService;

@Service("pdaCheckServiceImpl")
public class PdaCheckServiceImpl implements PdaCheckService{

	@Autowired 
	private BaseDao baseDao;

	/**
	 * 物料库存核查
	 * @param pr_code
	 * @param wh_code
	 * @return
	 */
	@Override
	public List<Map<String, Object>> makeMaterialCheck(String pr_code,String wh_code) {
		SqlRowList rs;
		if (wh_code == null || wh_code.equals("")) {
			rs = baseDao
					.queryForRowSet("select NVL(sum(bar_remain),0) bar_remain ,bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode from barcode left join product on pr_id=bar_prodid left join vendor on ve_code=bar_vendcode where lower(pr_code)='"
							+ pr_code.toLowerCase() + "' group by bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode ");
		} else {
			rs = baseDao
					.queryForRowSet("select  NVL(sum(bar_remain),0) bar_remain ,bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode from barcode left join product on pr_id=bar_prodid left join vendor on ve_code=bar_vendcode where lower(pr_code)='"
							+ pr_code.toLowerCase()
							+ "'and bar_whcode='"
							+ wh_code
							+ "' group by bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode ");
		}
		if (rs.next()) {
			return rs.getResultList();
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该物料不存在条码数据");
		}
	}

	/**
	 * 条码信息核查
	 * @param barcode
	 * @param whcode
	 * @return
	 */
	@Override
	public Map<String ,Object > barcodeCheck(String barcode, String whcode) {
		SqlRowList rs;
		if (whcode == null || whcode.equals("")) {
			rs = baseDao
					.queryForRowSet("select bar_code,bar_remain ,bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode,ve_shortname,bar_prodid from barcode left join product on pr_id=bar_prodid left join vendor on ve_code=bar_vendcode where lower(bar_code)='"
							+ barcode.toLowerCase() + "'");
		} else {
			rs = baseDao
					.queryForRowSet("select bar_code, bar_remain ,bar_whcode,bar_location,pr_spec,pr_detail,bar_prodcode,ve_shortname,bar_prodid from barcode left join product on pr_id=bar_prodid left join vendor on ve_code=bar_vendcode where lower(pr_code)='"
							+ barcode.toLowerCase() + "'and bar_whcode='" + whcode + "'");
		}
		if (rs.next()) {
			return rs.getCurrentMap();
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该条码号不存在");
		}
	}

	/**
	 * 包装信息核查
	 * @param outboxCode
	 * @return
	 */	
	@Override
	public Map<String, Object> packageCheck(String outboxCode) {
		SqlRowList rs;
		rs = baseDao
				.queryForRowSet("select v_outboxcode as pa_outboxcode, v_prodcode bar_prodcode ,pr_detail,pr_spec,v_total as pa_totalqty,v_makecode as pa_makecode, ma_vendcode, ma_salecode from MES_PACKAGE_VIEW left join barcode on v_barcode=bar_code and bar_ordercode = v_makecode left join product on pr_code=v_prodcode left join make on v_makecode=ma_code  where v_outboxcode ='"
						+ outboxCode + "'");
		if (rs.next()) {
			return rs.getCurrentMap();
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该包装箱号不存在");
		}
	}

	@Override
	public List<Map<String, Object>> makeFinishCheck(String makeCode) {
		SqlRowList rs;
		rs = baseDao
				.queryForRowSet("select ms_makecode ma_code, bar_remain ,bar_whcode,bar_location ,bar_prodcode from barcode left join makeSerial  on ms_sncode=bar_code and ms_makecode=bar_ordercode where  ms_makecode='"
						+ makeCode + "'");
		if (rs.next()) {
			return rs.getResultList();
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该生产单号不存在或者不存在完工品信息");
		}
	}

	@Override
	public List<Map<String, Object>> orderFinishCheck(String saleCode) {
		SqlRowList rs;
		rs = baseDao
				.queryForRowSet("select ma_salecode,bar_remain ,bar_whcode,bar_location ,bar_prodcode　from barcode left join makeserial on bar_code=ms_sncode  and ms_makecode=bar_ordercode left join make on ma_code=ms_makecode  where ma_salecode='"
						+ saleCode + "'");
		if (rs.next()) {
			return rs.getResultList();
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该订单号不存在或者不存在完工品信息");
		}
	}

	@Override
	public List<Map<String, Object>> checkPO(String or_code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> makeMaterialDetail(String pr_code, String bar_prodcode, String wh_code, String bar_location) {
		SqlRowList rs = baseDao
				.queryForRowSet("select bar_code,bar_remain,ve_shortname,bar_indate from barcode left join vendor on bar_vendcode=ve_code where bar_prodcode='"
						+ pr_code
						+ "' and bar_whcode='"
						+ wh_code
						+ "' and nvl(bar_location,' ')='"
						+ bar_location + "'");
		if (rs.next()) {
			return rs.getResultList();
		}
		return null;
	}

}
