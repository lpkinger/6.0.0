package com.uas.pda.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Page;
import com.uas.pda.dao.PdaCommonDao;

@Repository("pdaCommonDaoImpl")
public class PdaCommonDaoImpl implements PdaCommonDao {
	@Autowired
	private BaseDao baseDao;
	byte[] dotfont;
	String s_prt = "^XA", s_prt_buffer = "";
	int DPI;

	@Override
	public Page<Map<String, Object>> getInOutData(String condition, String inoutNo, String whcode, int pi_id) {
		String sql = null;
		Object[] obs = baseDao.getFieldsDataByCondition("WAREHOUSE", new String[] { "wh_code", "wh_description" },
				"wh_statuscode='AUDITED' AND wh_code='" + whcode + "'");
		sql = "select distinct '102' enAuditStatus,pi_class, pi_Inoutno,pi_title,pi_cardcode,pi_id,'" + obs[1] + "' pi_whname,'" + obs[0]
				+ "' pi_whcode,pi_statuscode from ProdInOut left join ProdIODetail on pi_id=pd_piid where  pi_id=" + pi_id;
		final SqlRowList rs = baseDao.queryForRowSet(sql);
		if (!rs.next()) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单号"+inoutNo+"不存在");
		} else if (rs.getString("pi_statuscode").equals("POSTED")) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单号已经过账");
		} else {
			Map<String, Object> mapT = rs.getResultList().get(0);
			int cn1=baseDao.getCount("select count(1)cn  from (select sum(pd_inqty) qty,sum(nvl(pd_barcodeinqty,0))"
					+ " barcodeqty from prodiodetail left join product on pr_code=pd_prodcode "
                    +"where pd_piid="+pi_id+" and  pd_whcode='"+whcode+"' and pr_tracekind>0) t where t.qty>t.barcodeqty "
                    		+ "and t.barcodeqty>0");
			if(cn1>0){
				mapT.put("ENAUDITSTATUS", "103");// 修改采集状态，为采集中
			}
			int a = baseDao.getCount("select count(0) cn from prodiodetail where pd_piid=" + pi_id
					+ " and (NVL(pd_barcodeinqty,0)<NVL(pd_inqty,0) OR NVL(pd_barcodeoutqty,0)<NVL(pd_outqty,0))");
			if (a == 0) {
				mapT.put("ENAUDITSTATUS", "101");// 修改采集状态，为已采集
			}
			final Map<String, Object> map = changeKeyToLowerCase(mapT);
			if (condition.equals("pd_inqty")) {
				sql = "select pi_Inoutno ,pd_prodcode,pr_detail,pr_spec,("
						+ condition
						+ "- NVL(pd_barcodeinqty,0))"
						+ condition
						+ ",NVL(pr_zxbzs,0) pr_zxbzs,pr_id,pd_ordercode,pd_pdno,pr_location,pd_id,pr_ifbarcodecheck from ProdInOut left join ProdIODetail on pd_piid=pi_id  left join Product on pd_prodcode=pr_code  where  pi_statuscode<>'POSTED' and NVL("
						+ condition + ",0)>0  and pd_whcode='" + whcode + "' and pi_id='" + pi_id + "' and pr_tracekind>0 and NVL("
						+ condition + ",0)>NVL(pd_barcodeinqty,0) order by pd_pdno";
			} else {
				sql = "select pi_Inoutno ,pd_prodcode,pr_detail,pr_spec,("
						+ condition
						+ "- NVL(pd_barcodeoutqty,0))"
						+ condition
						+ ",NVL(pr_zxbzs,0) pr_zxbzs,pr_id,pd_ordercode,pd_pdno,pr_location,pd_id,pr_ifbarcodecheck from ProdInOut left join ProdIODetail on pd_piid=pi_id  left join Product on pd_prodcode=pr_code  where  pi_statuscode<>'POSTED' and NVL("
						+ condition + ",0)>0  and pd_whcode='" + whcode + "' and pi_id='" + pi_id + "' and pr_tracekind>0 and NVL("
						+ condition + ",0)>NVL(pd_barcodeoutqty,0) order by pd_pdno";
			}
			final List<Map<String, Object>> list1 = baseDao.getJdbcTemplate().queryForList(sql);
			// final Map<String,Object> mp =
			// baseDao.getJdbcTemplate().queryForMap("select * from barcodeset where bs_type='BATCH'");
			if (!list1.isEmpty()) {
				return new Page<Map<String, Object>>() {
					@Override
					public int getTotalCount() {
						return 1;
					}

					@Override
					public List<Map<String, Object>> getTarget() {
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						map.put("product", changeKeyToLowerCase(list1));
						list.add(map);
						return list;
					}
				};
			} else {
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单据没有需要采集的物料");
			}
		}
	}

	@Override
	public List<Map<String, Object>> changeKeyToLowerCase(List<Map<String, Object>> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Iterator<Map<String, Object>> iter = list.iterator();
		map = null;
		while (iter.hasNext()) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map = iter.next();
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				map1.put(key.toLowerCase(), map.get(key));
			}
			datas.add(map1);
		}
		return datas;
	}

	@Override
	public Map<String, Object> changeKeyToLowerCase(Map<String, Object> map) {
		Map<String, Object> map1 = new HashMap<String, Object>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			map1.put(key.toLowerCase(), map.get(key));
		}
		return map1;
	}

	@Override
	public double getMsdTime(String level) {
		double restTime = 0;
		if (level.equals("2")) {
			restTime = 360 * 24;
		} else if (level.equals("2a")) {
			restTime = 4 * 7 * 24;
		} else if (level.equals("3")) {
			restTime = 7 * 24;
		} else if (level.equals("4")) {
			restTime = 3 * 24;
		} else if (level.equals("5")) {
			restTime = 2 * 24;
		} else if (level.equals("5a")) {
			restTime = 24;
		} else if (level.equals("6")) {
			restTime = 0;
		}
		return restTime;
	}

	@Override
	public double getMsdRestTime(String code) {
		double restTime;
		// 获取参数MsdReduceLTime:入柜参数配置，重新封装后尾料不扣减车间寿命，默认为否[就是扣减]
		boolean reduceLTime = baseDao.isDBSetting("MsdReduceLTime");
		SqlRowList rs = baseDao
				.queryForRowSet("select ms_action,ms_id,ms_date,ms_lifetime,ms_lifetime-ROUND(TO_NUMBER(sysdate-ms_date)*24,2) ms_restTime"
						+ " from (select ms_action,ms_id,ms_date,ms_lifetime from msdlog where ms_barcode='" + code
						+ "' order by ms_id desc) where rownum=1");
		if (reduceLTime) {// 不扣减,直接将上一条记录的lifetime 记录到
			if (rs.next()) {// 有上一条记录
				restTime = rs.getDouble("ms_lifetime");
			} else {// 没有上一条记录
				restTime = 999;
			}
		} else {// 扣减
			restTime = 999;
			if (rs.next() && rs.getString("ms_action").equals("拆封")) {// 有上一条记录,并且上一条是拆封记录
				restTime = rs.getDouble("ms_resttime");
			} else {// 没有上一条记录,根据生命等级获取
				Object ob = baseDao.getFieldDataByCondition("product left join msdlog on pr_code=ms_prodcode", "pr_msdlevel",
						"ms_barcode='" + code + "'");
				if (ob != null) {
					restTime = getMsdTime(ob.toString());
				} else {
					restTime = 999;
				}
			}
		}
		return restTime;
	}

	@Override
	public List<Map<String, Object>> getProdInOut(String condition, String inoutNo, String whcode) {
		StringBuffer sql = new StringBuffer();
		String field;
		String no = inoutNo.toUpperCase();
		SqlRowList rs = baseDao.queryForRowSet("select pi_statuscode from prodinout where pi_inoutno=?",no);
		if(rs.next()){
			if("pd_inqty".equals(condition)){
				field = "nvl(pi_pdastatus,'未入库')";
			}else{
				field = "nvl(pi_pdastatus,'未备料')";
			}
			sql.append("select distinct pi_class,pi_Inoutno,pi_title,pi_cardcode,pi_id,pi_statuscode,'' pd_whcode,'' pd_whname,")
					.append(field).append(" pi_pdastatus from ProdInOut left join ProdIODetail on pi_id=pd_piid where pi_inoutno='").append(no)
					.append("'");
			if(("pd_inqty").equals(condition)){
				sql.append(" and pd_inqty>0");
			}else{
				sql.append(" and pd_outqty>0");
			}
			// 获取单据
			rs = baseDao.queryForRowSet(sql.toString());
			if (rs.next()) {
				return rs.getResultList();
	        } else {
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"没有需要操作的数据或者单据与仓库不一致");
			}
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单号：" + inoutNo + "不存在");
		}
	}

	@Override
	public List<Map<String, Object>> getCheckProdInOut(String condition, String inoutNo, String whcode) {
		String sql = null;
		int cn;
		String no = inoutNo.toLowerCase();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		cn = baseDao.getCount("select count(1) cn from prodinout where lower(pi_inoutno)='" + no + "'");
		if (cn != 0) {// 判断单号是否存在
			cn = baseDao.getCount("select count(1) from prodinout where lower(pi_inoutno)='" + no
					+ "' and pi_invostatuscode<>'ENTERING'");
			if (cn == 0) {// 判断单据的状态，必须是未过账，并且不是在录入状态才可以采集
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据:" + inoutNo + "必须在非录入状态才允许抽检校验");
			}
		} else {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单号：" + inoutNo + "不存在");
		}
		if (StringUtil.hasText(whcode)) {
			Object ob = baseDao.getFieldsDataByCondition("WAREHOUSE", "wh_description",
					"wh_statuscode='AUDITED' AND wh_code='" + whcode + "'");
			if (ob == null) {
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"仓库不存在或者未审核");
			}
			sql = "select distinct pd_whcode,pd_whname,pi_class,pi_Inoutno,pi_title,pi_cardcode,pi_id,pi_statuscode from ProdInOut left join ProdIODetail on pi_id=pd_piid where lower(pi_inoutno)='"
					+ no + "' and pd_whcode='" + whcode + "' and pd_auditstatus <>'ENTERING'";
		} else {
			sql = "select distinct pd_whcode,pd_whname,pi_class,pi_Inoutno,pi_title,pi_cardcode,pi_id,pi_statuscode from ProdInOut left join ProdIODetail on pi_id=pd_piid where lower(pi_inoutno)='"
					+ no + "' and pd_auditstatus <>'ENTERING'";
		}
		// 获取单据
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			for (Map<String, Object> map : rs.getResultList()) {
					sql = "select pd_inoutno pi_inoutno,pd_prodcode from ProdIODetail left join Product on pd_prodcode=pr_code"
							+ " inner join barcodeio on bi_piid=pd_piid and bi_prodcode=pd_prodcode where pd_piid="
							+ map.get("pi_id")
							+ "  and pd_auditstatus <>'ENTERING' and NVL("
							+ condition + ",0)>0  and pd_whcode='" + map.get("pd_whcode") + "' and pr_tracekind>0";
				List<Map<String, Object>> list1 = baseDao.getJdbcTemplate().queryForList(sql);
				map.put("ENAUDITSTATUS", "101");// 修改采集状态，为已采集
				if (!list1.isEmpty()) {// 没有需要抽检的明细移除
					list.add(map);
				}
				int cn1=baseDao.getCount("select count(1)cn  from (select sum(pd_inqty) qty,sum(nvl(pd_barcodeinqty,0))"
						+ " barcodeqty from prodiodetail left join product on pr_code=pd_prodcode "
                        +"where pd_piid="+map.get("pi_id")+" and  pd_whcode='"+map.get("pd_whcode")+"' and pr_tracekind>0) t where t.qty>t.barcodeqty "
                        		+ "and t.barcodeqty>0");
				if(cn1>0){
					map.put("ENAUDITSTATUS", "103");// 修改采集状态，为采集中
				}
			}
			if (list.size() > 0) {
				return list;
			} else {
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据不存在需要抽检的明细");
			}
		} else {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单据与仓库不一致");
		}
	}
}
