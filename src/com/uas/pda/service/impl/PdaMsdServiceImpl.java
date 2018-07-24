package com.uas.pda.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaMsdService;

@Service("pdaMsdService")
public class PdaMsdServiceImpl implements PdaMsdService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	
	@Override
	public Map<String, Object> getLog(String code) {
		SqlRowList rs;
		// 判断条码是否存在，状态,数量，是否为湿敏元件
		rs = baseDao
				.queryForRowSet("select nvl(bar_remain,0) bar_remain,NVL(bar_status,0) bar_status,pr_ismsd,nvl(pr_msdlevel,0)pr_msdlevel,pr_code,bar_batchcode,pr_detail from barcode left join product on pr_code=bar_prodcode where bar_code='"
						+ code + "'");
		if (rs.next()) {
			if (rs.getInt("bar_status") < 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码：" + code + "无效！");
			}
			if (rs.getDouble("bar_remain") == 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + ",剩余数为0");
			}
			if ("N".equals(rs.getString("pr_ismsd"))) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + code + ",对应物料不是湿敏元件!");
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码:" + code + ",不存在!");
		}
		return rs.getCurrentMap();
	}

	/**
	 * 确认入烘烤
	 */
	@Override
	public void confirmInOven(String data) {
		Map<Object, Object> map = FlexJsonUtil.fromJson(data);
		Map<String, Object> mapB = new HashMap<String, Object>();
		String code = (String) map.get("bar_code");
		// 判断条码是否存在，状态,数量，是否为湿敏元件,获取条码，物料相关信息
		mapB = getLog(code);
		//判断条码是否已经入烘烤
		SqlRowList rs = baseDao.queryForRowSet("select ms_action from (select ms_action,rownum from msdlog where ms_barcode='"+code+"' order by ms_date desc) where rownum=1");
		if(rs.next() && rs.getString("ms_action").equals("入烘烤")){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:"+code+"在烘烤状态中，请勿重复入烘烤！");
		}
		double restTime = 0;
		// 记录日志 msdLog
		int ms_id = baseDao.getSeqId("MSDLOG_SEQ");
		// 根据配置参数，以及湿敏等级获取剩余寿命
		restTime = pdaCommonDao.getMsdRestTime(code);
		baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action," +
				" ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty,ms_remark)" +
				" values ("+ms_id+",sysdate,'"+code+"','"+mapB.get("pr_msdlevel")+"','"+restTime+"','入烘烤','" +
				SystemSession.getUser().getEm_name()+"','"+map.get("location")+"','"+mapB.get("pr_code")+"','"+mapB.get("bar_batchcode")+"',"+mapB.get("bar_remain")+",'温度："+map.get("temp")+"')");
	}

	@Override
	public Map<String,Object> getOvenTime(String code) {
		Map<String, Object> mapB = new HashMap<String, Object>();
		// 判断条码是否存在，状态,数量，是否为湿敏元件,获取条码，物料相关信息
		mapB = getLog(code);
		// 判断条码是否入烘烤未出
		SqlRowList rs = baseDao.queryForRowSet("select ROUND(TO_NUMBER(sysdate-ms_date)*24,2) ovenTime,ms_action from (select ms_date,ms_action,rownum from msdlog where ms_barcode='"+code+"' order by ms_date desc ) where rownum=1");
		if(!rs.next()||!rs.getString("ms_action").equals("入烘烤")){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:"+code+"不是在烘烤中，无需出！");
		}
		mapB.put("OVENTIME", rs.getObject("ovenTime"));
		return mapB;
	}

	@Override
	public Map<String,Object> confirmOutOven(String code) {
		//获取烘烤时间
		Map<String,Object> mpR = getOvenTime(code);		
		//出烘烤记录日志 msdLog
		baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action," +
				" ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty,ms_remark)" +
				" select msdlog_seq.nextval,sysdate,ms_barcode,ms_level,999,'出烘烤','" +
				SystemSession.getUser().getEm_name()+"',ms_location,ms_prodcode,ms_batchcode,ms_qty,'烘烤时长:"+mpR.get("ovenTime")+"' from (select msdlog.*,rownum from msdlog where ms_barcode='"+code+"' order by ms_id desc) where rownum=1");
		return mpR;
	}
	
	@Override
	public Map<String,Object> loadMSDLog(String code){
		Map<String, Object> map = new HashMap<String, Object>();
		SqlRowList rs;
		Object obLevel = baseDao.getFieldDataByCondition("product left join barcode on bar_prodcode=pr_code", "pr_msdlevel", "bar_code='"
				+ code + "'");
		if (obLevel != null) {
			map.put("MS_LEVEL", obLevel.toString());
		}
		// 获取参数MsdReduceLTime:入柜参数配置，重新封装后尾料不扣减车间寿命，默认为否[就是扣减]
		boolean reduceLTime = baseDao.isDBSetting("MsdReduceLTime");
		rs = baseDao
				.queryForRowSet("select ms_action,ms_id,ms_date,ms_lifetime,ms_lifetime-ROUND(TO_NUMBER(sysdate-ms_date)*24,2) ms_restTime"
						+ " from (select ms_action,ms_id,ms_date,ms_lifetime from msdlog where ms_barcode='" + code
						+ "' order by ms_id desc) where rownum=1");
		map.put("MS_RESTTIME", -1);
		if (rs.next()) {// 有上一条记录
			if (rs.getString("ms_action").equals("拆封")) {// 上一条记录是拆封
				if (!reduceLTime) {// 扣减车间寿命
					map.put("MS_RESTTIME", rs.getDouble("ms_resttime"));
					map.put("STATUS", "已拆封");
				} else {// 不扣减
					map.put("MS_RESTTIME", rs.getDouble("ms_lifetime"));
					map.put("STATUS", "已拆封");
				}
			} else if (rs.getString("ms_action").equals("入烘烤")) {
				map.put("STATUS", "在烘烤");
			} else if (rs.getString("ms_action").equals("出烘烤")) {
				map.put("STATUS", "已烘烤");
			}
		} else {// 没有，则未拆封
			map.put("STATUS", "未拆封");
		}
		map.put("BAR_CODE", code);
		rs = baseDao.queryForRowSet("select ms_date,ms_action,ms_man,ms_lifetime,ms_prodcode from msdlog where ms_barcode='" + code
				+ "' order by ms_date desc");
		if (rs.next()) {
			map.put("log",rs.getResultList());
		}else{
			map.put("log",null);
		}
		return map;
	}
}
