package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.MapComparator;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.AppMouldDao;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.MakeNoticeDetailDao;
import com.uas.erp.dao.common.OtherExplistDao;
import com.uas.erp.dao.common.PurMouldDao;
import com.uas.erp.dao.common.QUAVerifyApplyDetailDao;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.BatchDealService;
import com.uas.erp.service.scm.ProdInOutService;

@Service("PmBatchDealService")
public class BatchDealServiceImpl implements BatchDealService {
	@Autowired
	private MakeNoticeDetailDao makeNoticeDetailDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private QUAVerifyApplyDetailDao QUAVerifyApplyDetailDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private AppMouldDao appMouldDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private PurMouldDao purMouldDao;
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	private OtherExplistDao otherExplistDao;
	@Autowired
	private MakeCraftDao makeCraftDao;

	/**
	 * 制造通知单批量转制造单
	 */
	@Override
	public String turnMake(String data, String caller) {
		try {
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			Object kind = null;
			StringBuffer sb = new StringBuffer();
			Map<String, Object> result = null;
			for (Map<Object, Object> s : store) {
				kind = s.get("ma_kind");
				if ("MAKE".equals(kind)) {
					result = makeNoticeDetailDao.turnMake(Integer.parseInt(s.get("mnd_id").toString()),
							Double.parseDouble(s.get("mnd_tqty").toString()));
					if (result != null) {
						sb.append("转入成功,制造单号:"
								+ "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?whoami=Make!Base&formCondition=ma_idIS"
								+ result.get("id") + "&gridCondition=mm_maidIS" + result.get("id") + "')\">" + result.get("code")
								+ "</a>&nbsp;");
					}
				} else if ("OS".equals(kind)) {
					result = makeNoticeDetailDao.turnOutSource(Integer.parseInt(s.get("mnd_id").toString()),
							Double.parseDouble(s.get("mnd_tqty").toString()));
					if (result != null) {
						sb.append("转入成功,委外单号:"
								+ "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?whoami=Make&formCondition=ma_idIS"
								+ result.get("id") + "&gridCondition=mm_maidIS" + result.get("id") + "')\">" + result.get("code")
								+ "</a>&nbsp;");
					}
				}

			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "转入失败";
		}
	}
	
	/**
	 * 工单/委外   领 退 补界面转单时  是否把当前用户部门信息带过去
	 * @param id 出入库单ID
	 * @param caller caller
	 */
	private void updateMakeProdinoutDepart(Object id,String caller){
		if("MakeMaterial!Return".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//生产退料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}else if("MakeMaterial!OS!Return".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//委外退料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}else if("MakeMaterial!Give".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//生产补料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}else if("MakeMaterial!OS!Give".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//委外补料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}else if("MakeMaterial!issue".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//生产领料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}else if("MakeMaterial!OS!issue".equals(caller) && baseDao.isDBSetting(caller, "isTurnDepartment")){//委外领料料单
			baseDao.execute(
					"update prodinout set pi_departmentcode='"+SystemSession.getUser().getEm_departmentcode()+"',pi_departmentname='"+SystemSession.getUser().getEm_depart()+"' where pi_id="+id);
		}
	}
	
	
	/**
	 * 
	 * 转生产退料单
	 */
	@Override
	public String turnProdIn(String data, boolean wh, String type, String caller, boolean outtoint) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		String whcode = CollectionUtil.pluckSqlString(store, "mm_whcode");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		String piclass = "", whoami = "", piintype = "";
		boolean isOS = false;
		String picodestr = "", whcodestr = "";
		if ("MAKE".equals(type)) {
			piclass = "生产退料单";
			whoami = "ProdInOut!Make!Return";
		} else if ("OS".equals(type)) {
			piclass = "委外退料单";
			whoami = "ProdInOut!OutsideReturn";
			isOS = true;
		}
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张退料单中!");
				}
			}
		}
		whcodestr = baseDao.queryForObject("select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in(" + whcode
				+ ") and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')", String.class);
		if (whcodestr != null) {
			BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
		}
		whcodestr = baseDao.queryForObject("select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in(" + whcode
				+ ") and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')", String.class);
		if (whcodestr != null) {
			BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
		}
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
			Object apvendcode = p.get("ma_apvendcode");// 应付供应商编号为空时应付供应商编号=供应商编号
			if (apvendcode == null || "".equals(apvendcode)) {
				p.put("ma_apvendcode", p.get("ma_vendcode"));
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		if (outtoint) {
			piintype = "水口料入库";
		}
		if (wh) {// 按仓库分组
			if (isOS) {
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "mm_whcode", "ma_vendcode",
						"ma_apvendcode" });
				List<Map<Object, Object>> s = null;
				String[] keys = null;
				int detno = 1;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						keys = m.toString().split("#");
						j = makeDao.newProdIOWithVendor(keys[0], keys[1], keys[2], piclass, whoami, piintype);
						if (j != null) {
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnInWh(code, detno++, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
										piclass);
							}
							sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
						}
					}
				}
			} else {
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "mm_whcode");
				List<Map<Object, Object>> s = null;
				int detno = 1;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						j = makeDao.newProdIO(m.toString(), piclass, whoami, piintype);
						if (j != null) {
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnInWh(code, detno++, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
										piclass);
							}
							sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
						}
					}
				}
			}
		} else {
			if (isOS) {
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "ma_vendcode", "ma_apvendcode" });
				List<Map<Object, Object>> s = null;
				int detno = 1;
				String[] keys = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						keys = m.toString().split("#");
						j = makeDao.newProdIOWithVendor(keys[0], keys[1], piclass, whoami, piintype);
						if (j != null) {
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnInWh(code, detno++, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
										piclass);
							}
							sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
						}
					}
				}
			} else {
				j = makeDao.newProdIO(null, piclass, whoami, piintype);
				if (j != null) {
					code = j.getString("pi_inoutno");
					picodestr += "," + code;
					int detno = 1;
					for (Map<Object, Object> p : store) {
						makeDao.turnIn(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
								Double.parseDouble(p.get("mm_thisqty").toString()), String.valueOf(p.get("mm_whcode")), piclass, whoami);
					}
					sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
							+ "</a>&nbsp;");
					//更新部门编号  
					updateMakeProdinoutDepart(j.get("pi_id"),caller);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 转领料单
	 */
	@Override
	public String turnProdOut(String data, boolean wh, String whman, String type, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		String piclass = "", whoami = "";
		boolean isOS = false;
		StringBuffer sb2 = new StringBuffer();
		boolean turn = true;
		if ("MAKE".equals(type)) {
			piclass = "生产领料单";
			whoami = "ProdInOut!Picking";
		} else if ("OS".equals(type)) {
			piclass = "委外领料单";
			whoami = "ProdInOut!OutsidePicking";
			isOS = true;
		}
		if (isOS) {
			Object v = null;
			List<Object> mmid = new ArrayList<Object>();
			for (Map<Object, Object> m : store) {
				v = m.get("ma_vendcode");
				if (v == null || "".equals(v)) {
					mmid.add(m.get("mm_id"));
				}
				Object apvendcode = m.get("ma_apvendcode");// 应付供应商编号为空时应付供应商编号=供应商编号
				if (apvendcode == null || "".equals(apvendcode)) {
					m.put("ma_apvendcode", v);
				}
			}
			if (mmid.size() > 0) {
				String codes = baseDao.getJdbcTemplate().queryForObject(
						"select WMSYS.WM_CONCAT(mm_code) from makematerial left join make on mm_maid=ma_id where mm_id in("
								+ BaseUtil.parseList2Str(mmid, ",", false) + ") and nvl(ma_vendcode,' ')=' '", String.class);
				if (codes != null) {
					return "委外加工单:" + codes + "的委外供应商未填写,不能领料!";
				}
			}
		}
		// 判断工单的状态
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的制造单不允许合并下达到一张领料单中!");
				}
			}
		}

		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		// 明细行增加可转
		for (Map<Object, Object> p : store) {
			p.put("UnDoECN", false);
		}
		// 根据配置 ifUnDoECN 存在未执行制造ECN的不允许领料，判断是否允许领料
		boolean ifUnDoEcn = baseDao.isDBSetting(caller, "ifUnDoECN");
		String picodestr = "";
		MapComparator comparator = new MapComparator("mm_detno");
		// 获取是否增加按物料仓管员，物料大类进行分组
		boolean groupByPrWhcode = baseDao.isDBSetting(caller, "groupByPrWhcode");
		boolean groupByPrKind = baseDao.isDBSetting(caller, "groupByPrKind");
		String groupfield = null;
		List<Map<String, Object>> listp = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapd = new HashMap<String, Object>();

		if (wh) {// 按仓库分组
			if (isOS) {
				groupfield = "mm_whcode,ma_vendcode,ma_apvendcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));// 添加按应付供应商分组
				List<Map<Object, Object>> s = null;
				String[] keys = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						Collections.sort(s, comparator);
						// 判断是否存在未执行的ECN
						turn = checkUnDoECN(sb2, s, ifUnDoEcn);
						if (turn) {
							keys = m.toString().split("#");
							j = makeDao.newProdIOWithVendor(keys[0], keys[1], keys[2], piclass, whoami, null);
							baseDao.execute("update prodinout set pi_cgycode='" + whman
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
									+ j.getInt("pi_id"));
							if (j != null) {
								mapd = new HashMap<String, Object>();
								code = j.getString("pi_inoutno");
								picodestr += "," + code;
								mapd.put("inoutno", code);
								mapd.put("id", j.get("pi_id"));
								int detno = 1;
								for (Map<Object, Object> p : s) {
									if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
										makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
												Integer.parseInt(p.get("mm_detno").toString()),
												Double.parseDouble(p.get("mm_thisqty").toString()));
									}
								}
								sb.append("转入成功,领料单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">"
										+ code + "</a>&nbsp;<br>");
								baseDao.execute(
										"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
										j.get("pi_id"));
								//更新部门编号  
								updateMakeProdinoutDepart(j.get("pi_id"),caller);
								// 20180126更新备注
								if (j != null) {
									baseDao.execute(
											"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
											j.get("pi_id"));
								}
								listp.add(mapd);
							}
						}
					}
				}
			} else {
				groupfield = "mm_whcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
				List<Map<Object, Object>> s = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						Collections.sort(s, comparator);
						// 判断是否存在未执行的ECN
						turn = checkUnDoECN(sb2, s, ifUnDoEcn);
						if (turn) {
							j = makeDao.newProdIO(m.toString().split("#")[0], piclass, whoami, null);
							baseDao.execute("update prodinout set pi_cgycode='" + whman
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
									+ j.getInt("pi_id"));
							if (j != null) {
								mapd = new HashMap<String, Object>();
								code = j.getString("pi_inoutno");
								picodestr += "," + code;
								mapd.put("inoutno", code);
								mapd.put("id", j.get("pi_id"));
								int detno = 1;
								for (Map<Object, Object> p : s) {
									if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
										makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
												Integer.parseInt(p.get("mm_detno").toString()),
												Double.parseDouble(p.get("mm_thisqty").toString()));
									}
								}
								sb.append("转入成功,领料单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">"
										+ code + "</a>&nbsp;<br>");
								baseDao.execute(
										"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
										j.get("pi_id"));
								//更新部门编号  
								updateMakeProdinoutDepart(j.get("pi_id"),caller);
								// 20180126更新备注
								if (j != null) {
									baseDao.execute(
											"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
											j.get("pi_id"));
								}
								listp.add(mapd);
							}
						}
					}
				}
			}
		} else {
			if (isOS) {
				groupfield = "ma_vendcode,ma_apvendcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
				List<Map<Object, Object>> s = null;
				int detno = 1;
				String[] keys = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						Collections.sort(s, comparator);
						// 判断是否存在未执行的ECN
						turn = checkUnDoECN(sb2, s, ifUnDoEcn);
						if (turn) {
							keys = m.toString().split("#");
							j = makeDao.newProdIOWithVendor(keys[0], keys[1], piclass, whoami, null);
							baseDao.execute("update prodinout set pi_cgycode='" + whman
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
									+ j.getInt("pi_id"));
							if (j != null) {
								mapd = new HashMap<String, Object>();
								code = j.getString("pi_inoutno");
								picodestr += "," + code;
								mapd.put("inoutno", code);
								mapd.put("id", j.get("pi_id"));
								detno = 1;
								for (Map<Object, Object> p : s) {
									if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
										makeDao.turnOut(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
												Integer.parseInt(p.get("mm_detno").toString()),
												Double.parseDouble(p.get("mm_thisqty").toString()), String.valueOf(p.get("mm_whcode")),
												piclass, whoami);
									}
								}
								sb.append("转入成功,领料单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">"
										+ code + "</a>&nbsp;<br>");
								baseDao.execute(
										"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
										j.get("pi_id"));
								//更新部门编号  
								updateMakeProdinoutDepart(j.get("pi_id"),caller);
								// 20180126更新备注
								if (j != null) {
									baseDao.execute(
											"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
											j.get("pi_id"));
								}
								listp.add(mapd);
							}
						}
					}
				}
			} else {
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = new HashMap<Object, List<Map<Object, Object>>>();
				if (groupfield != null && !"".equals(groupfield)) {
					map = BaseUtil.groupsMap(store, groupfield.split(","));// 添加按应付供应商分组
				} else {
					map.put("one1", store);
				}
				List<Map<Object, Object>> s = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						Collections.sort(s, comparator);
						// 判断是否存在未执行的ECN
						turn = checkUnDoECN(sb2, s, ifUnDoEcn);
						if (turn) {
							j = makeDao.newProdIO(s.get(0).get("mm_whcode").toString(), piclass, whoami, null);
							baseDao.execute("update prodinout set pi_cgycode='" + whman
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
									+ j.getInt("pi_id"));
							if (j != null) {
								mapd = new HashMap<String, Object>();
								code = j.getString("pi_inoutno");
								picodestr += "," + code;
								mapd.put("inoutno", code);
								mapd.put("id", j.get("pi_id"));
								int detno = 1;
								for (Map<Object, Object> p : s) {
									if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
										makeDao.turnOut(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
												Integer.parseInt(p.get("mm_detno").toString()),
												Double.parseDouble(p.get("mm_thisqty").toString()), String.valueOf(p.get("mm_whcode")),
												piclass, whoami);
									}
								}
								sb.append("转入成功,领料单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">"
										+ code + "</a>&nbsp;<br>");
								//更新部门编号  
								updateMakeProdinoutDepart(j.get("pi_id"),caller);
								baseDao.execute(
										"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
										j.get("pi_id"));
								
								// 20180126更新备注
								if (j != null) {
									baseDao.execute(
											"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
											j.get("pi_id"));
								}
								listp.add(mapd);
							}
						}
					}
				}
			}
		}
		if (!picodestr.equals("")) {
			//根据循环调用批次存储过程取获取批次号
			boolean ifGetBatchcode = baseDao.isDBSetting(caller, "getBatchcode");
			if(ifGetBatchcode){
				for (Map<String, Object> m : listp) {
					String inoutno = m.get("inoutno").toString();
					String res = baseDao.callProcedure("Sp_SplitProdOut",
							new Object[] {piclass,inoutno, String.valueOf(SystemSession.getUser().getEm_name()) });
					if (res != null && !res.trim().equals("")) {
						sb.append(piclass+":"+inoutno+",自动获取批号失败");
					}
				}
			}		
			picodestr = picodestr.substring(1);
			picodestr = picodestr.replace(",", "','");
			String codes = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in('" + picodestr
							+ "') and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (codes != null) {
				BaseUtil.showError("仓库:" + codes + "不存在，不能领料!");
			}
			codes = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in('" + picodestr
							+ "') and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (codes != null) {
				BaseUtil.showError("仓库:" + codes + "不存在，不能领料!");
			}
		}

		sb.append("<hr>").append(sb2);
		return sb.toString();
	}

	// 判断是否存在未执行的制造ECN
	private boolean checkUnDoECN(StringBuffer sb2, List<Map<Object, Object>> s, boolean ifUnDoEcn) {
		int i = 0;
		StringBuffer sb = new StringBuffer();
		if (ifUnDoEcn) {
			for (Map<Object, Object> p : s) {
				SqlRowList rs = baseDao
						.queryForRowSet("select count(1)cn ,WMSYS.WM_CONCAT(mc_code)mc_code from makematerialchange left join makematerialchangedet on mc_id=md_mcid where mc_statuscode in ('ENTERING','COMMITED') "
								+ "AND md_makecode='"
								+ p.get("mm_code").toString()
								+ "' and md_mmdetno="
								+ p.get("mm_detno").toString()
								+ " AND NVL(md_didstatus,' ')<>'已取消' and md_prodcode='" + p.get("mm_prodcode").toString() + "'");
				if (rs.next() && rs.getInt("cn") > 0) {
					i++;
					sb.append("制造单号："+p.get("mm_code").toString()+",制造单序号："+p.get("mm_detno").toString());
					p.put("UnDoECN", true);
				}
			}
		}

		if (i > 0) {
			sb2.append("存在" + i + "行数据有未执行的制造ECN,"+sb.toString());
		}
		if (i == s.size()) {
			return false;
		}
		return true;
	}

	/**
	 * 转完工入库单
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String turnMade(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "ma_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao.queryForRowSet("select  count(1) n from (select distinct ma_cop from make where ma_id in (" + ids
					+ ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的制造单不允许合并下达到一张完工入库单中!");
				}
			}
		}
		if (store.size() > 0) {
			Object _code = store.get(0).get("pi_inoutno");
			if (_code != null && _code.toString().trim().length() > 0) {
				code = String.valueOf(store.get(0).get("pi_inoutno"));
				boolean isExist = baseDao.checkIf("ProdInOut", "pi_inoutno='" + code
						+ "' AND pi_class='完工入库单' AND pi_invostatuscode='ENTERING' AND nvl(pi_statuscode,' ')<>'POSTED'");
				if (!isExist) {
					return "指定单据:" + code + "不存在，或非在录入、未过账状态!";
				} else {
					for (Map<Object, Object> p : store) {
						makeDao.turnMadeWh(code, Integer.parseInt(p.get("ma_id").toString()),
								Double.parseDouble(p.get("ma_thisqty").toString()));
					}
					Object pi_id = baseDao.getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + code + "' AND pi_class='完工入库单'");
					sb.append("转入成功,完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
							+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Make!In')\">" + code + "</a>&nbsp;");
				}
			} else {
				Map<Object, List<Map<Object, Object>>> map = null;
				if(baseDao.isDBSetting("ProdInOut!Make!In","makeKindType")){
					for(Map<Object, Object> p : store){
						//通过ma_id去查询mk_type
						Object ma_id = p.get("ma_id");
						SqlRowList rs = baseDao.queryForRowSet("select mk_type from makekind left join make on ma_kind = mk_name where ma_id=?",ma_id);
						if(rs.next()){
							p.put("mk_type", rs.getString("mk_type"));
						}
					}
					map = BaseUtil.groupsMap(store,new Object[] {"ma_whcode","mk_type"});
				}else{
					map = BaseUtil.groupMap(store, "ma_whcode");
				}
				//Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "ma_whcode");
				List<Map<Object, Object>> s = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						j = makeDao.newProdIO(m.toString().split("#")[0], "完工入库单", "ProdInOut!Make!In", null);
						if (j != null) {
							code = j.getString("pi_inoutno");
							for (Map<Object, Object> p : s) {
								// 重新更新已转完工入库数
								baseDao.execute("update make set ma_tomadeqty=(select sum(pd_inqty)-sum(case when pd_status=99 then pd_outqty else 0 end) from prodiodetail where pd_ordercode=ma_code  and pd_piclass in('完工入库单','委外验收单','委外验退单')) where ma_id="
										+ p.get("ma_id").toString());
								baseDao.execute("update make set ma_tomadeqty=ma_madeqty where ma_id=" + p.get("ma_id").toString()
										+ " and ma_madeqty>ma_tomadeqty");
								makeDao.setMaxCanMadeqty(p.get("ma_id").toString());// 更新最大套料数
								SqlRowList rs0 = baseDao
										.queryForRowSet("select ma_qty,nvl(ma_finishoverrate,0) ma_finishoverrate,NVL(ma_madeqty,0)ma_madeqty,NVL(ma_tomadeqty,0)ma_tomadeqty,NVL(ma_canmadeqty,0)ma_canmadeqty,NVL(wc_makegreater,0)wc_makegreater,ma_code,nvl(mk_finishunget,0)mk_finishunget from make left join workcenter on wc_code=ma_wccode left join makekind on ma_kind=mk_name where ma_id="
												+ p.get("ma_id").toString());
								if (rs0.next()) {
									//先判断工单比例超工单数，在判断工作中心是否超工单数
									if(rs0.getDouble("ma_finishoverrate") == 0){
										// 判断是否超工单数
										if (rs0.getDouble("wc_makegreater") == 0
												&& Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")
														- rs0.getDouble("ma_tomadeqty")) {
											BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
													+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
										}
										// 判断是否超工单数
										if (rs0.getDouble("wc_makegreater") == 0
												&& Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")
														- rs0.getDouble("ma_madeqty")) {
											BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
													+ "+已完工数" + rs0.getString("ma_madeqty") + "大于工单数：" + rs0.getString("ma_qty") + "!");
										}
									}else{
										// 判断是否工单超工比例的超工单数    已转完工
										if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
														- rs0.getDouble("ma_tomadeqty")) {
											BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
													+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
										}
										// 判断是否工单超工比例的超工单数   已完工
										if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
														- rs0.getDouble("ma_madeqty")) {
											BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
													+ "+已完工数" + rs0.getString("ma_madeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
										}
									}
									
									// 判断是否超已领料套数，@update 20171128
									// 针对工单类型为中允许未领料可完工，则不限制套料数
									if ((Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_canmadeqty")
											- rs0.getDouble("ma_tomadeqty"))
											&& rs0.getInt("mk_finishunget") == 0) {
										BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
												+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数领料套数：" + rs0.getString("ma_canmadeqty")
												+ "!");
									}
								}
								makeDao.turnMadeWh(code, Integer.parseInt(p.get("ma_id").toString()),
										Double.parseDouble(p.get("ma_thisqty").toString()));
								baseDao.execute("update ProdIODetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from ProdInOut where pi_id="
										+ j.getInt("pi_id") + ") where pd_piid=" + j.getInt("pi_id") + "");
							}
							sb.append("转入成功,完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=ProdInOut!Make!In')\">"
									+ code + "</a>&nbsp;");
						}
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 制造单转完工入库单——按流程单
	 */
	@Override
	public String turnMadebyflow(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		if (store.size() > 0) {
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "ma_whcode");
			List<Map<Object, Object>> s = null;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					j = makeDao.newProdIO(m.toString(), "完工入库单", "ProdInOut!Make!In", null);
					if (j != null) {
						code = j.getString("pi_inoutno");
						for (Map<Object, Object> p : s) {
							makeDao.turnMadeWhbyflow(code, Integer.parseInt(p.get("mf_id").toString()),
									Double.parseDouble(p.get("ma_thisqty").toString()));
						}
						sb.append("转入成功,完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=ProdInOut!Make!In')\">" + code
								+ "</a>&nbsp;");
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void vastSaveProductMrpSet(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("Product",
					"pr_leadtime='" + map.get("pr_leadtime") + "',pr_purchasedays='" + map.get("pr_purchasedays") + "',pr_workcenter='"
							+ map.get("pr_workcenter") + "'," + "pr_purcmergedays='" + map.get("pr_purcmergedays") + "',pr_supplytype='"
							+ map.get("pr_supplytype") + "',pr_isvalid='" + map.get("pr_isvalid") + "'", "pr_id=" + map.get("pr_id"));
		}
	}

	@Override
	public void EndSaleForeCast(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object statuscode = map.get("sf_statuscode");
			if (statuscode != null && statuscode.equals("AUDITED")) {
				baseDao.updateByCondition("SaleForecast", "sf_statuscode='FINISH',sf_status='" + BaseUtil.getLocalMessage("FINISH")
						+ "',sf_enddate=sysdate", "sf_id=" + map.get("sf_id"));
				// 记录操作
				baseDao.logger.resAudit(caller, "sf_id", map.get("sf_id"));
			}
		}
	}

	@Override
	public void ResEndSaleForeCast(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object statuscode = map.get("sf_statuscode");
			if (statuscode != null && statuscode.equals("AUDITED")) {
				baseDao.resOperate("SaleForecast", "sf_id=" + map.get("sf_id"), "sf_status", "sf_statuscode");
				// 记录操作
				baseDao.logger.resEnd(caller, "sf_id", map.get("sf_id"));
			}
		}
	}

	/**
	 * 转补料单
	 */
	@Override
	public String turnProdAdd(String data, boolean wh, String type, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		boolean isOS = false;
		String piclass = "", whoami = "";
		String picodestr = "", whcodestr = "";
		if ("MAKE".equals(type)) {
			piclass = "生产补料单";
			whoami = "ProdInOut!Make!Give";
		} else if ("OS".equals(type)) {
			piclass = "委外补料单";
			whoami = "ProdInOut!OSMake!Give";
			isOS = true;
		}
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张补料单中!");
				}
			}
		}
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
			Object apvendcode = p.get("ma_apvendcode");// 应付供应商编号为空时应付供应商编号=供应商编号
			if (apvendcode == null || "".equals(apvendcode)) {
				p.put("ma_apvendcode", p.get("ma_vendcode"));
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		// 获取是否增加按物料仓管员，物料大类进行分组
		boolean groupByPrWhcode = baseDao.isDBSetting(caller, "groupByPrWhcode");
		boolean groupByPrKind = baseDao.isDBSetting(caller, "groupByPrKind");
		String groupfield = null;
		List<Map<String, Object>> listp = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapd = null;

		if (wh ) {// 按仓库分组
			if (isOS) {
				groupfield = "mm_whcode,ma_vendcode,ma_apvendcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
				List<Map<Object, Object>> s = null;
				String[] keys = null;
				int detno = 1;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						keys = m.toString().split("#");
						j = makeDao.newProdIOWithVendor(keys[0], keys[1], keys[2], piclass, whoami, null);
						if (j != null) {
							mapd = new HashMap<String, Object>();
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							mapd.put("inoutno", code);
							mapd.put("id", j.get("pi_id"));
							detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()));
							}
							sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
							listp.add(mapd);
						}
					}
				}
			} else {
				groupfield = "mm_whcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
				List<Map<Object, Object>> s = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						j = makeDao.newProdIO(m.toString().split("#")[0], piclass, whoami, null);
						if (j != null) {
							mapd = new HashMap<String, Object>();
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							mapd.put("inoutno", code);
							mapd.put("id", j.get("pi_id"));
							int detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()));
							}
							sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
							listp.add(mapd);
						}
					}
				}
			}
		} else {
			if (isOS) {
				groupfield = "ma_vendcode,ma_apvendcode";
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
				List<Map<Object, Object>> s = null;
				int detno = 1;
				String[] keys = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						keys = m.toString().split("#");
						j = makeDao.newProdIOWithVendor(keys[0], keys[1], piclass, whoami, null);
						if (j != null) {
							mapd = new HashMap<String, Object>();
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							detno = 1;
							mapd.put("inoutno", code);
							mapd.put("id", j.get("pi_id"));
							for (Map<Object, Object> p : s) {
								makeDao.turnAdd(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
										String.valueOf(p.get("mm_whcode")), piclass, whoami);
							}
							sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
							listp.add(mapd);
						}
					}
				}
			} else {
				if (groupByPrWhcode) {
					groupfield += ",pr_whmancode";
				}
				if (groupByPrKind) {
					groupfield += ",pr_kind";
				}
				Map<Object, List<Map<Object, Object>>> map = new HashMap<Object, List<Map<Object, Object>>>();
				if (groupfield != null && !"".equals(groupfield)) {
					map = BaseUtil.groupsMap(store, groupfield.split(","));// 添加按应付供应商分组
				} else {
					map.put("one1", store);
				}
				List<Map<Object, Object>> s = null;
				for (Object m : map.keySet()) {
					if (m != null) {
						s = map.get(m);
						j = makeDao.newProdIO(null, piclass, whoami, null);
						if (j != null) {
							mapd = new HashMap<String, Object>();
							code = j.getString("pi_inoutno");
							picodestr += "," + code;
							mapd.put("inoutno", code);
							mapd.put("id", j.get("pi_id"));
							int detno = 1;
							for (Map<Object, Object> p : s) {
								makeDao.turnAdd(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
										Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
										String.valueOf(p.get("mm_whcode")), piclass, whoami);
							}
							sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							//更新部门编号  
							updateMakeProdinoutDepart(j.get("pi_id"),caller);
							listp.add(mapd);
						}
					}
				}
			}
		}
		if (!picodestr.equals("")) {
			picodestr = picodestr.substring(1);
			picodestr = picodestr.replace(",", "','");
			whcodestr = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in('" + picodestr
							+ "') and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (whcodestr != null) {
				BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
			}
			whcodestr = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in('" + picodestr
							+ "') and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (whcodestr != null) {
				BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
			}
		}
		return sb.toString();
	}

	/**
	 * 委外单批量转制造单
	 */
	@Override
	public String OSturnMake(String data, String caller) {
		try {
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			JSONObject j = null;
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : store) {
				j = makeDao.turnMake(Integer.parseInt(s.get("ma_id").toString()), Double.parseDouble(s.get("mm_thisqty").toString()));
				if (j != null) {
					sb.append("转入成功,制造单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?formCondition=ma_idIS"
							+ j.get("ma_id") + "&gridCondition=mm_maidIS" + j.get("ma_id") + "')\">" + j.get("ma_code") + "</a>&nbsp;");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "转入失败";
		}
	}

	/**
	 * 制造单批量转检验单
	 */
	@Override
	public String turnQuaCheck(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		handlerService.handler("Make!Base", "turnFQC", "before", new Object[] { store });
		JSONObject j = null;
		Object line = null;// 线别
		Object whcode = "";// 仓库编号
		int maid = 0;// 制造单ID
		Object prcode = null;// 物料编号
		Object type = null;// 物料检验类型
		StringBuffer sb = new StringBuffer();
		// 天派科技先入检
		boolean fqcSeq = baseDao.isDBSetting("Make!ToQuaCheck!Deal", "checkNestQty");
		for (Map<Object, Object> s : store) {
			line = s.get("ma_remark");
			maid = Integer.parseInt(s.get("ma_id").toString());
			whcode = s.get("ve_whcode");
			if ("".equals(whcode) || whcode == null) {
				whcode = baseDao.getFieldDataByCondition("Make left join product on ma_prodcode=pr_code", "nvl(ma_whcode,pr_whcode) a",
						"ma_id=" + maid);
			}
			Object zldcode = s.get("ma_zldcode");
			Object batchcode = s.get("ma_contractcode");// 批号
			prcode = baseDao.getFieldDataByCondition("Make", "ma_prodcode", "ma_id=" + maid);
			type = baseDao.getFieldDataByCondition("Product", "pr_acceptmethod", "pr_code='" + prcode + "'");
			if ((zldcode != null && !"".equals(zldcode.toString())) || (batchcode != null && !"".equals(batchcode.toString()))) {
				if ("检验".equals(type.toString()) || "0".equals(type.toString())) {
					j = makeDao.turnQuaCheck(maid, Double.parseDouble(s.get("ma_thisqty").toString()), zldcode, batchcode, "UNAUDIT",
							fqcSeq);
				} else {
					j = makeDao.turnQuaCheck2(maid, Double.parseDouble(s.get("ma_thisqty").toString()), zldcode, batchcode, "AUDITED",
							fqcSeq);
				}
			} else {
				if (type == null) {
					j = makeDao.turnQuaCheck(maid, Double.parseDouble(s.get("ma_thisqty").toString()), null, null, "UNAUDIT", fqcSeq);
				} else {
					if ("检验".equals(type.toString()) || "0".equals(type.toString())) {
						j = makeDao.turnQuaCheck(maid, Double.parseDouble(s.get("ma_thisqty").toString()), null, null, "UNAUDIT", fqcSeq);
					} else {
						j = makeDao.turnQuaCheck2(maid, Double.parseDouble(s.get("ma_thisqty").toString()), null, null, "AUDITED", fqcSeq);
					}
				}
			}
			if (j != null) {
				Object[] ve = baseDao.getFieldsDataByCondition("Qua_VerifyApplyDetail left join Product on vad_prodcode=pr_code",
						new String[] { "vad_qty", "pr_aql","vad_prodcode","ve_ordercode" }, "ve_id=" + j.get("ve_id"));
				if (ve != null) {
					baseDao.execute(
							"update Qua_VerifyApplyDetail set ve_samplingqty=(select ad_qty from QUA_Aql,QUA_AqlDetail where al_id=ad_alid and al_statuscode='AUDITED' and ad_aql=? and ad_minqty<=? and ad_maxqty>=?) where ve_id=?",
							ve[1], ve[0], ve[0], j.get("ve_id"));
					baseDao.updateByCondition(
							"QUA_VerifyApplyDetail",
							"ve_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
									+ j.get("ve_id") + ") order by ve_date desc) where rownum<2)", "ve_id=" + j.get("ve_id"));
					baseDao.updateByCondition("QUA_VerifyApplyDetailDet",
							"ved_testman=(select ve_testman from qua_verifyapplydetail where ved_veid=ve_id)", "ved_veid=" + j.get("ve_id"));
				}
				// 2018040263 FQC【是否强制检验方式】 maz  18-07-02  根据物料+制造单号 取最近检验批次的检验方式
				if ("Make!ToQuaCheck!Deal".equals(caller)) {
					SqlRowList method = baseDao.queryForRowSet("select nvl(ve_method,'正常抽检')method from (select * from Qua_VerifyApplyDetail where vad_prodcode='"+ve[2]+"' and ve_ordercode='"+ve[3]+"' and ve_id<>"+j.get("ve_id")+" order by ve_id desc) where rownum<2");
					if(method.next()){
						baseDao.execute("update Qua_VerifyApplyDetail set ve_method='"+method.getString("method")+"' where ve_id="+j.get("ve_id"));
					}else{
						baseDao.execute("update Qua_VerifyApplyDetail set ve_method='正常抽检' where ve_id="+j.get("ve_id"));
					}
				}
				sb.append("转入成功,检验单号:"
						+ "<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetail.jsp?whoami=VerifyApplyDetail!FQC&formCondition=ve_idIS"
						+ j.get("ve_id") + "&gridCondition=ved_veidIS" + j.get("ve_id") + "')\">" + j.get("ve_code") + "</a>&nbsp;");
				if (line != null) {
					baseDao.execute("update QUA_VerifyApplyDetail set ve_linename=? where ve_id =?", line, j.get("ve_id"));
				}
				if (whcode != null) {
					baseDao.execute("update QUA_VerifyApplyDetail set ve_whcode=? where ve_id =? and nvl(ve_whcode,' ')=' '", whcode,
							j.get("ve_id"));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 生产检验单转完工入库单
	 */
	@Override
	public String turnFinishIn(String data, String caller) {
		StringBuffer sb = new StringBuffer();
		String call = null;
		List<JSONObject> okR = null;
		List<JSONObject> ngR = null;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<Map<Object, Object>> ok = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> ng = new ArrayList<Map<Object, Object>>();
		// 判断明细状态是否是已审核
		QUAVerifyApplyDetailDao.checkstatus(maps);
		if (baseDao.isDBSetting("CopCheck")) {
			String ids = CollectionUtil.pluckSqlString(maps, "ved_veid");
			SqlRowList rs = baseDao
					.queryForRowSet(" select count(1)n from (select distinct ma_cop from QUA_VerifyApplyDetail left join make on ve_ordercode=ma_code where ve_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的检验单不允许合并下达到一张完工入库单中!");
				}
			}
		}
		for(Map<Object, Object> p : maps){
			//通过ma_id去查询mk_type
			SqlRowList rs = baseDao.queryForRowSet("SELECT vad_code,mk_type FROM QUA_VerifyApplyDetail LEFT JOIN QUA_VerifyApplyDetailDet "
					+"ON ve_id=ved_veid LEFT JOIN Make ON ma_code  =vad_code left join makekind on ma_kind = mk_name WHERE ved_id=?",p.get("ved_id"));
			if(rs.next()){
				p.put("mk_type", rs.getString("mk_type"));
			}
		}
		Object ngallow = null;
		double okqty = 0;
		double ngqty = 0;
		double _okqty = 0;
		double _ngqty = 0;
		double turnqty = 0;
		int veid = 0;
		int isng = 0;
		Map<Object, Object> n = null;
		Object ma_tasktype = baseDao.getFieldDataByCondition("Make", "ma_tasktype",
				"ma_code=(select vad_code from QUA_VerifyApplyDetail where ve_id=" + maps.get(0).get("ved_veid") + ")");
		for (Map<Object, Object> m : maps) {
			_okqty = Double.parseDouble(m.get("ved_okqty").toString());
			if (m.get("ved_turnqty") != null) {
				turnqty = Double.parseDouble(m.get("ved_turnqty").toString());
			}
			okqty += _okqty;
			_ngqty = Double.parseDouble(m.get("ved_ngqty").toString());
			isng = Integer.parseInt(m.get("ved_isng").toString());
			ngqty += _ngqty;
			if (veid == 0) {
				veid = Integer.parseInt(String.valueOf(m.get("ved_veid")));
			}
			ngallow = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "nvl(ve_criqty,0)", "ve_id=" + veid);
			if (_okqty > 0) {
				m.put("qty", _okqty - turnqty);
				m.put("wh", m.get("pr_whcode"));
				m.put("_okqty", _okqty);
				if (m.get("pr_whcode") == null || "".equals(m.get("pr_whcode"))) {
					BaseUtil.showError("请指定良品仓!");
				}
				ok.add(m);
			}
			if (_ngqty > 0 && isng == 0) {
				n = new HashMap<Object, Object>();
				n.put("qty", _ngqty);
				if (m.get("wh_code") == null || "".equals(m.get("wh_code"))) {
					BaseUtil.showError("请指定不良品仓!");
				}
				n.put("wh", m.get("wh_code"));
				n.put("ve_id", m.get("ve_id"));
				n.put("ved_id", m.get("ved_id"));
				ng.add(n);
			}
		}
		if (ok.size() > 0) {
			if ("OS".equals(ma_tasktype)) {
				call = "ProdInOut!OutsideCheckIn";
				okR = QUAVerifyApplyDetailDao.detailTurnStorageOs(call, "委外验收单", ok, true);
				for (JSONObject j : okR) {
					if ("委外验收".equals(j.get("intype"))) {
						baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
								+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
						baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
								+ j.get("pi_id"));
					} else if ("工序验收".equals(j.get("intype"))) {
						baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select mc_currency,mc_rate from makecraft,prodiodetail where pd_piid=pi_id and pd_ordercode=mc_code and nvl(pd_intype,' ')='工序验收' and pd_pdno=1) where pi_id="
								+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
					}
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
							+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
					// 委外验收成功之后增加业务
					handlerService.handler(caller, "turn", "after", new Object[] { j.get("pi_id") });
					sb.append("转入成功,入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
							+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=nvl(ve_ingoodqty,0)+" + okqty, "ve_id=" + veid);
				}
			} else {
				call = "ProdInOut!Make!In";
				if(baseDao.isDBSetting("ProdInOut!Make!In", "makeKindType")){
					Map<Object, List<Map<Object, Object>>> groupMap = BaseUtil.groupMap(ok, "mk_type");
					for(Object obj :groupMap.keySet()){
						if(obj!=null){
							ok=groupMap.get(obj);
							JSONObject j = new JSONObject();
							j = QUAVerifyApplyDetailDao.newProdIO2(call, "完工入库单");
							okR = QUAVerifyApplyDetailDao.turnFinish(call, "完工入库单", ok, true, j);
							if (okR != null && okR.size() > 0) {
								sb.append("转入成功,完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
										+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
								baseDao.updateByCondition("ProdInOut", "pi_whcode='" + ok.get(0).get("wh") + "'", "pi_id=" + j.get("pi_id"));
								baseDao.updateByCondition("ProdInOut", "pi_whname=(select wh_description from warehouse where pi_whcode=wh_code)",
										"pi_id=" + j.get("pi_id"));
								baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=nvl(ve_ingoodqty,0)+" + okqty, "ve_id=" + veid);
								// 生产检验单转完工入库单：把工单公司机型和客户型号带到完工入库单(万利达)
								if (baseDao.isDBSetting("VerifyApplyDetail!FQC", "othersTurn")) {
									baseDao.execute("update ProdIODetail set (pd_custprodcode,pd_bgxh)=(select ma_companytype,ma_custprodcode from make where ma_code=pd_ordercode)"
											+ " where pd_piid=" + j.get("pi_id"));
								}
							}
						}
					}
				}else{
					JSONObject j = new JSONObject();
					j = QUAVerifyApplyDetailDao.newProdIO2(call, "完工入库单");
					okR = QUAVerifyApplyDetailDao.turnFinish(call, "完工入库单", ok, true, j);
					if (okR != null && okR.size() > 0) {
						sb.append("转入成功,完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
								+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
						baseDao.updateByCondition("ProdInOut", "pi_whcode='" + ok.get(0).get("wh") + "'", "pi_id=" + j.get("pi_id"));
						baseDao.updateByCondition("ProdInOut", "pi_whname=(select wh_description from warehouse where pi_whcode=wh_code)",
								"pi_id=" + j.get("pi_id"));
						baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=nvl(ve_ingoodqty,0)+" + okqty, "ve_id=" + veid);
						// 生产检验单转完工入库单：把工单公司机型和客户型号带到完工入库单(万利达)
						if (baseDao.isDBSetting("VerifyApplyDetail!FQC", "othersTurn")) {
							baseDao.execute("update ProdIODetail set (pd_custprodcode,pd_bgxh)=(select ma_companytype,ma_custprodcode from make where ma_code=pd_ordercode)"
									+ " where pd_piid=" + j.get("pi_id"));
						}
					}
				}
			}
		}
		if (Integer.parseInt(ngallow.toString()) != 0) {
			if (ng.size() > 0 && isng == 0) {
				if ("OS".equals(ma_tasktype)) {
					call = "ProdInOut!DefectIn";
					ngR = QUAVerifyApplyDetailDao.detailTurnStorageOs(call, "不良品入库单", ng, false);
					for (JSONObject j : ngR) {
						if ("委外验收".equals(j.get("intype"))) {
							baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
									+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
							baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
									+ j.get("pi_id"));
						} else if ("工序验收".equals(j.get("intype"))) {
							baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select mc_currency,mc_rate from makecraft,prodiodetail where pd_piid=pi_id and pd_ordercode=mc_code and nvl(pd_intype,' ')='工序验收' and pd_pdno=1) where pi_id="
									+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
						}
						baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
								+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
						sb.append("转入成功,不良品入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
								+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
					}
					baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=nvl(ve_inbadqty,0)+" + ngqty, "ve_id=" + veid);
				} else {
					call = "ProdInOut!Make!In";
					JSONObject j = new JSONObject();
					j = QUAVerifyApplyDetailDao.newProdIO2(call, "完工入库单");
					ngR = QUAVerifyApplyDetailDao.turnFinish(call, "完工入库单", ng, false, j);
					ngR = QUAVerifyApplyDetailDao.detailTurnStorageOs(call, "不良品入库单", ng, false);
					if (ngR != null && ngR.size() > 0) {
						sb.append("转入成功,不良完工入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
								+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
						baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=nvl(ve_inbadqty,0)+" + ngqty, "ve_id=" + veid);
						baseDao.updateByCondition("ProdInOut", "pi_whcode='" + ng.get(0).get("wh") + "'", "pi_id=" + j.get("pi_id"));
						baseDao.updateByCondition("ProdInOut", "pi_whname=(select wh_description from warehouse where pi_whcode=wh_code)",
								"pi_id=" + j.get("pi_id"));
						baseDao.updateByCondition("ProdInOut", "pi_remark='不合格入库'", "pi_id=" + j.get("pi_id"));
						// 生产检验单转完工入库单：把工单公司机型和客户型号带到完工入库单(万利达)
						if (baseDao.isDBSetting("VerifyApplyDetail!FQC", "othersTurn")) {
							baseDao.execute("update ProdIODetail set (pd_custprodcode,pd_bgxh)=(select ma_companytype,ma_custprodcode from make where ma_code=pd_ordercode)"
									+ " where pd_piid=" + j.get("pi_id"));
						}
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * maz 锤子科技 FQC转委外验收单
	 */
	@Override
	public String OSturnFinishIn(String data, String caller) {
		StringBuffer sb = new StringBuffer();
		String call = null;
		List<JSONObject> okR = null;
		List<JSONObject> ngR = null;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<Map<Object, Object>> ok = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> ng = new ArrayList<Map<Object, Object>>();
		// 判断明细状态是否是已审核
		QUAVerifyApplyDetailDao.checkstatus(maps);
		if (baseDao.isDBSetting("CopCheck")) {
			String ids = CollectionUtil.pluckSqlString(maps, "ved_veid");
			SqlRowList rs = baseDao
					.queryForRowSet(" select count(1)n from (select distinct ma_cop from QUA_VerifyApplyDetail left join make on ve_ordercode=ma_code where ve_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的检验单不允许合并下达到一张委外验收单中!");
				}
			}
		}
		Object ngallow = null;
		double okqty = 0;
		double ngqty = 0;
		double _okqty = 0;
		double _ngqty = 0;
		double turnqty = 0;
		int veid = 0;
		int isng = 0;
		Map<Object, Object> n = null;
		for (Map<Object, Object> m : maps) {
			_okqty = Double.parseDouble(m.get("ved_okqty").toString());
			if (m.get("ved_turnqty") != null) {
				turnqty = Double.parseDouble(m.get("ved_turnqty").toString());
			}
			okqty += _okqty;
			_ngqty = Double.parseDouble(m.get("ved_ngqty").toString());
			isng = Integer.parseInt(m.get("ved_isng").toString());
			ngqty += _ngqty;
			if (veid == 0) {
				veid = Integer.parseInt(String.valueOf(m.get("ved_veid")));
			}
			ngallow = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "nvl(ve_criqty,0)", "ve_id=" + veid);
			if (_okqty > 0) {
				m.put("qty", _okqty - turnqty);
				m.put("wh", m.get("pr_whcode"));
				m.put("_okqty", _okqty);
				if (m.get("pr_whcode") == null || "".equals(m.get("pr_whcode"))) {
					BaseUtil.showError("请指定良品仓!");
				}
				ok.add(m);
			}
			if (_ngqty > 0 && isng == 0) {
				n = new HashMap<Object, Object>();
				n.put("qty", _ngqty);
				if (m.get("wh_code") == null || "".equals(m.get("wh_code"))) {
					BaseUtil.showError("请指定不良品仓!");
				}
				n.put("wh", m.get("wh_code"));
				n.put("ve_id", m.get("ve_id"));
				n.put("ved_id", m.get("ved_id"));
				ng.add(n);
			}
		}
		if (ok.size() > 0) {
			call = "ProdInOut!OutsideCheckIn";
			okR = QUAVerifyApplyDetailDao.OSdetailTurnStorageOs(call, "委外验收单", ok, true);
			for (JSONObject j : okR) {
				if ("委外验收".equals(j.get("intype"))) {
					baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
							+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
							+ j.get("pi_id"));
				}
				baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
						+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
				// 委外验收成功之后增加业务
				handlerService.handler(caller, "turn", "after", new Object[] { j.get("pi_id") });
				sb.append("转入成功,入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
				baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=nvl(ve_ingoodqty,0)+" + okqty, "ve_id=" + veid);
			}
		}
		if (Integer.parseInt(ngallow.toString()) != 0) {
			if (ng.size() > 0 && isng == 0) {
				call = "ProdInOut!DefectIn";
				ngR = QUAVerifyApplyDetailDao.OSdetailTurnStorageOs(call, "不良品入库单", ng, false);
				for (JSONObject j : ngR) {
					if ("委外验收".equals(j.get("intype"))) {
						baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
								+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
						baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
								+ j.get("pi_id"));
					}
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
							+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
					sb.append("转入成功,不良品入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">"
							+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
				}
				baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=nvl(ve_inbadqty,0)+" + ngqty, "ve_id=" + veid);
			}
		}
		return sb.toString();
	}

	/**
	 * 委外加工单批量转委外收料单
	 */
	@Override
	public String vastTurnAccept(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		//
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "ma_id"), ",");
		String codes = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(ma_code) from make where ma_id in (" + ids
						+ ") and (nvl(ma_vendcode,' ')=' ' or nvl(ma_currency,' ')=' ')", String.class);
		if (codes != null) {
			return "委外单:" + codes + "的供应商或币别未填写!";
		}
		// 按供应商号分组
		Map<Object, List<Map<Object, Object>>> set = BaseUtil.groupMap(maps, "ma_vendcode");
		List<Map<Object, Object>> s = null;
		StringBuffer sb = new StringBuffer();
		String log = null;
		int index = 1;
		for (Object obj : set.keySet()) {
			if (obj != null) {
				s = set.get(obj);
				for (Map<Object, Object> p : s) {
					// 重新更新已转完工入库数
					makeDao.setMaxCanMadeqty(p.get("ma_id").toString());// 更新最大套料数
					SqlRowList rs0 = baseDao
							.queryForRowSet("select ma_qty,nvl(ma_finishoverrate,0) ma_finishoverrate,NVL(ma_madeqty,0)ma_madeqty,NVL(ma_haveqty,0)ma_haveqty,NVL(ma_canmadeqty,0)ma_canmadeqty,NVL(wc_makegreater,0)wc_makegreater,ma_code,nvl(mk_finishunget,0) mk_finishunget from make left join workcenter on wc_code=ma_wccode left join makekind on ma_kind=mk_name  where ma_id="
									+ p.get("ma_id").toString());
					if (rs0.next()) {
						if(rs0.getDouble("ma_finishoverrate") == 0){
							// 判断是否超工单数
							if (rs0.getDouble("wc_makegreater") == 0
									&& Double.parseDouble(p.get("ma_thisqty").toString()) + rs0.getDouble("ma_haveqty") > rs0
											.getDouble("ma_qty")) {
								BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 收料数" + p.get("ma_thisqty").toString() + "+已转收料数"
										+ rs0.getString("ma_haveqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
							}
							// 判断是否超工单数
							if (rs0.getDouble("wc_makegreater") == 0
									&& Double.parseDouble(p.get("ma_thisqty").toString()) + rs0.getDouble("ma_madeqty") > rs0
											.getDouble("ma_qty")) {
								BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 收料数" + p.get("ma_thisqty").toString() + "+已完数"
										+ rs0.getString("ma_madeqty") + "大于工单数：" + rs0.getString("ma_qty") + "!");
							}
						}else{
							// 判断是否工单超工比例的超工单数
							if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
											- rs0.getDouble("ma_tomadeqty")) {
								BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
										+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
							}
							// 判断是否工单超工比例的超工单数   已完工
							if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
											- rs0.getDouble("ma_madeqty")) {
								BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
										+ "+已完工数" + rs0.getString("ma_madeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
							}
						}
						// 判断是否超已领料套数,工单类型为未领料可完工不限制
						if ((Double.parseDouble(p.get("ma_thisqty").toString()) + rs0.getDouble("ma_haveqty") > rs0
								.getDouble("ma_canmadeqty")) && rs0.getInt("mk_finishunget") == 0) {
							List<Object> mm = baseDao
									.getFieldDatasByCondition(
											"MakeMaterial left join make on mm_maid=ma_id left join makekind on mk_name=ma_kind",
											"mm_detno",
											" mm_maid = "
													+ p.get("ma_id")
													+ " and ceil((NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0))/mm_oneuseqty)<case when NVL(ma_madeqty,0)>NVL(ma_haveqty,0) then NVL(ma_madeqty,0) else NVL(ma_haveqty,0) end+"
													+ p.get("ma_thisqty")
													+ "  AND nvl(mm_materialstatus, ' ')=' ' AND NVL(mk_finishunget,0)=0 AND nvl(mm_oneuseqty,0)>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1 and NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)<mm_qty and rownum<=20");
							if (mm.size() > 0) {
								BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 收料数" + p.get("ma_thisqty").toString() + "+已转收料数"
										+ rs0.getString("ma_haveqty") + "大于工单数领料套数：" + rs0.getString("ma_canmadeqty") + "!,领料数不足序号:"
										+ BaseUtil.parseList2Str(mm, ",", true) + "!");
							}
						}
					}
				}
			}
		}
		for (Object obj : set.keySet()) {
			if (obj != null) {
				// 转收料
				log = makeDao.turnAccept(caller, set.get(obj));
				sb.append(index++ + ":" + log + "<hr/><br/>");
			}
		}
		return sb.toString();
	}

	/**
	 * 拉式发料生成调拨单和领料单
	 */
	@Override
	public String turnlssend(String data, boolean bywhcode, String wipwhcode, String maid, String departmentcode, String emcode,
			String cgycode, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String bccode = null;
		Object bcpiid = 0;
		JSONObject j = null;
		// 判断是否手工填写本次调拨数
		Boolean makeSendLS_NoCount = baseDao.isDBSetting(caller, "makeSendLS_NoCount");
		// 是否生成调拨单，勾选不生成，不勾选默认生成
		Boolean notCreateAppropriation = baseDao.isDBSetting(caller, "notCreateAppropriation");
		String llpiclass = "生产领料单", llcaller = "ProdInOut!Picking", bcpiclass = "拨出单", bccaller = "ProdInOut!AppropriationOut";
		Object ma_tasktype = baseDao.getFieldDataByCondition("Make", "ma_tasktype", "ma_id in (" + maid + ")");
		boolean bool = baseDao.checkIf("make", "(nvl(ma_checkstatuscode,' ')<>'APPROVE' or ma_statuscode<>'AUDITED') and ma_id in (" + maid
				+ ")");
		if (bool) {
			BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
		}
		if ("OS".equals(ma_tasktype)) {
			llpiclass = "委外领料单";
			llcaller = "ProdInOut!OutsidePicking";
		}
		j = makeDao.newProdIO(wipwhcode, llpiclass, llcaller, null);
		// 更新部门和人员
		baseDao.execute("update prodinout set pi_emcode='" + emcode + "',pi_emname=(select max(em_name) from employee where em_code='"
				+ emcode + "'),pi_departmentcode='" + departmentcode
				+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode + "' ) where pi_id="
				+ j.getInt("pi_id"));
		if (cgycode != null && !cgycode.equals("")) {
			baseDao.execute("update prodinout set pi_cgycode='" + cgycode + "',pi_cgy=(select max(em_name) from employee where em_code='"
					+ cgycode + "')  where pi_id=" + j.getInt("pi_id"));
		}
		String llcode = j.getString("pi_inoutno");
		Object llpiid = j.get("pi_id");
		int lldetno = 1;
		String wipwhname = "";
		wipwhname = baseDao.getFieldDataByCondition("warehouse", "wh_description", "wh_code='" + wipwhcode + "'").toString();
		if (bywhcode) {// 按仓库分组
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "mm_whcode");
			List<Map<Object, Object>> s = null;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					int bcdetno = 1;
					int mmid = 0, mmdetno = 0;
					Object prid = null;
					Float thisplanqty = null;
					Float wipuseqty = null;
					Float thisqty = null;
					List<String> sqls = new ArrayList<String>();
					for (Map<Object, Object> p : s) {
						mmid = Integer.parseInt(p.get("mm_id").toString());
						mmdetno = Integer.parseInt(p.get("mm_detno").toString());
						thisplanqty = Float.parseFloat(p.get("mm_thisplanqty").toString());
						wipuseqty = Float.parseFloat(p.get("mm_wipuseqty").toString());
						thisqty = Float.parseFloat(p.get("mm_thisqty").toString());
						if (!makeSendLS_NoCount && thisplanqty + wipuseqty < thisqty) {
							thisqty = thisplanqty + wipuseqty;
						}
						// 产生拨出单明细
						if (thisplanqty > 0 && !notCreateAppropriation) {
							if (bcdetno == 1) {
								j = makeDao.newProdIO(m.toString(), bcpiclass, bccaller, null);
								// 更新部门和人员
								baseDao.execute("update prodinout set pi_type='库存转移',pi_purpose='" + wipwhcode + "',pi_purposename='"
										+ wipwhname + "',pi_emcode='" + emcode
										+ "',pi_emname=(select max(em_name) from employee where em_code='" + emcode
										+ "'),pi_departmentcode='" + departmentcode
										+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode
										+ "' ),pi_sourcecode='"+p.get("mm_code")+"',pi_remark='拉式发料' where pi_id=" + j.getInt("pi_id"));
								if (cgycode != null && !cgycode.equals("")) {
									baseDao.execute("update prodinout set pi_cgycode='" + cgycode
											+ "',pi_cgy=(select max(em_name) from employee where em_code='" + cgycode + "')  where pi_id="
											+ j.getInt("pi_id"));
								}
								bccode = j.getString("pi_inoutno");
								bcpiid = j.get("pi_id");
								sb.append("成功产生，拨出单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + bcpiid
										+ "&gridCondition=pd_piidIS" + bcpiid + "&whoami=" + bccaller + "')\">" + bccode + "</a>&nbsp;<br>");
								// 标示领料单的拨出单号
								baseDao.execute("update ProdInOut set pi_fromcode='" + bccode + "' where pi_id=" + llpiid);
							}
							if (bccode != null && llcode != null) {
								prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + p.get("mm_prodcode").toString()
										+ "'");
								String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
										+ "pd_ordercode, pd_orderdetno, pd_orderid, pd_prodid,pd_whcode,pd_inwhcode,pd_inwhname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								if (mmid < 0) {
									mmdetno = Integer.parseInt(baseDao.getFieldDataByCondition("MakeMaterialReplace", "mp_mmdetno",
											"mp_mmid=" + Math.abs(mmid) + " AND mp_detno=" + mmdetno).toString());
								}
								baseDao.execute(newdetailstr, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), bcpiid, bccode,
										bcpiclass, bcdetno++, 0, "ENTERING", p.get("mm_prodcode").toString(), thisplanqty,
										p.get("mm_code").toString(), mmdetno, maid, prid, m.toString(), wipwhcode, wipwhname });
								// 20180126更新备注
								baseDao.execute(
										"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
										bcpiid);
							}

						}
						// 产生领料单明细
						if (thisqty > 0) {
							makeDao.turnOutWh(llcode, lldetno++, llpiclass, mmid, Integer.parseInt(p.get("mm_detno").toString()),
									Double.parseDouble(thisqty.toString()));
							sqls.add("update ProdIODetail set pd_outqty1=" + thisplanqty + " where pd_piid=" + llpiid + " and pd_pdno="
									+ (lldetno - 1));
						}
					}
					baseDao.execute(sqls);
				}
			}
		} else {
			int bcdetno = 1;
			List<String> sqls2 = new ArrayList<String>();
			int mmdetno = 0;
			for (Map<Object, Object> p : store) {
				int mmid = 0;
				Object prid = null;
				Float thisplanqty = null;
				Float thisqty = null;
				Float wipuseqty = null;
				mmdetno = Integer.parseInt(p.get("mm_detno").toString());
				mmid = Integer.parseInt(p.get("mm_id").toString());
				thisplanqty = Float.parseFloat(p.get("mm_thisplanqty").toString());
				wipuseqty = Float.parseFloat(p.get("mm_wipuseqty").toString());
				thisqty = Float.parseFloat(p.get("mm_thisqty").toString());
				if (!makeSendLS_NoCount && thisplanqty + wipuseqty < thisqty) {
					thisqty = thisplanqty + wipuseqty;
				}
				// 产生拨出单明细
				if (thisplanqty > 0 && !notCreateAppropriation) {
					if (bcdetno == 1) {
						j = makeDao.newProdIO(p.get("mm_whcode").toString(), bcpiclass, bccaller, null);
						// 更新部门和人员
						baseDao.execute("update prodinout set pi_type='库存转移',pi_purpose='" + wipwhcode + "',pi_purposename='" + wipwhname
								+ "',pi_emcode='" + emcode + "',pi_emname=(select max(em_name) from employee where em_code='" + emcode
								+ "'),pi_departmentcode='" + departmentcode
								+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode
								+ "' ),pi_sourcecode='"+p.get("mm_code")+"',pi_remark='拉式发料' where pi_id=" + j.getInt("pi_id"));
						if (cgycode != null && !cgycode.equals("")) {
							baseDao.execute("update prodinout set pi_cgycode='" + cgycode
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + cgycode + "')  where pi_id="
									+ j.getInt("pi_id"));
						}
						bccode = j.getString("pi_inoutno");
						bcpiid = j.get("pi_id");
						sb.append("成功产生，拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ bcpiid + "&gridCondition=pd_piidIS" + bcpiid + "&whoami=" + bccaller + "')\">" + bccode
								+ "</a>&nbsp;<br>");
						// 标示领料单的拨出单号
						baseDao.execute("update ProdInOut set pi_fromcode='" + bccode + "' where pi_id=" + llpiid);
					}
					if (bccode != null && llcode != null) {
						prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + p.get("mm_prodcode").toString() + "'");
						String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
								+ "pd_ordercode, pd_orderdetno, pd_orderid, pd_prodid,pd_whcode,pd_inwhcode,pd_inwhname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						if (mmid < 0) {
							mmdetno = Integer.parseInt(baseDao.getFieldDataByCondition("MakeMaterialReplace", "mp_mmdetno",
									"mp_mmid=" + Math.abs(mmid) + " AND mp_detno=" + mmdetno).toString());
						}
						baseDao.execute(newdetailstr, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), bcpiid, bccode, bcpiclass,
								bcdetno++, 0, "ENTERING", p.get("mm_prodcode").toString(), thisplanqty, p.get("mm_code").toString(),
								mmdetno, maid, prid, p.get("mm_whcode").toString(), wipwhcode, wipwhname });
						// 20180126更新备注
						baseDao.execute(
								"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
								bcpiid);

					}
				}
				// 产生领料单明细
				if (thisqty > 0) {
					makeDao.turnOutWh(llcode, lldetno++, llpiclass, mmid, Integer.parseInt(p.get("mm_detno").toString()),
							Double.parseDouble(thisqty.toString()));
					sqls2.add("update ProdIODetail set pd_outqty1=" + thisplanqty + " where pd_piid=" + llpiid + " and pd_pdno="
							+ (lldetno - 1));
				}
			}
			baseDao.execute(sqls2);
		}
		// 20170321新增更新拨出单拨出仓库名称
		baseDao.execute("update Prodiodetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid=?",
				bcpiid);
		if ("OS".equals(ma_tasktype)) {
			String updatesql = "update prodinout set(pi_cardcode,pi_title,pi_receivecode,pi_receivename)=(select ma_vendcode,ma_vendname,nvl(ma_apvendcode,ma_vendcode),nvl(ma_apvendname,ma_vendname) from make where ma_id= "
					+ maid + ") where pi_id=" + llpiid;
			baseDao.execute(updatesql);
		}
		if (lldetno == 1) {// 条件不符，未生成明细数据
			baseDao.deleteByCondition("ProdInOut", "pi_id=" + llpiid);
			BaseUtil.showError("数量有误.本次领料数不能超过 线边仓可用 +调拨数");
		} else {
			String ifaudit = "";
			if (bccode == null && !llcode.equals("")) {
				try {
					// 不需要调拨有库存的直接过帐
					handlerService.handler(caller, "save", "after", new Object[] { llpiid });
					// 拉式发料，在不需要调拨的情况下自动过账领料单
					if (baseDao.isDBSetting(caller, "MakeSendLSAutoInOut")) {
						if (llpiclass.equals("生产领料单")) {
							prodInOutService.postProdInOut(j.getInt("pi_id"), "ProdInOut!Picking");
						} else if (llpiclass.equals("委外领料单")) {
							prodInOutService.postProdInOut(j.getInt("pi_id"), "ProdInOut!OutsidePicking");
						}
					}
					ifaudit = "自动过帐成功,";
				} catch (Exception e) {
					// 屏蔽错误
					ifaudit = "未自动过帐成功,";
				}
			}
			sb.append("成功产生," + ifaudit + llpiclass + "号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + llpiid
					+ "&gridCondition=pd_piidIS" + llpiid + "&whoami=" + llcaller + "')\">" + llcode + "</a>&nbsp;<br>");
			// 20180126更新备注
			baseDao.execute(
					"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
					 llpiid);
		}

		return sb.toString();
	}

	@Override
	public String turnProdScrap(String data, String type, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String piclass = "", whoami = "";
		if ("MAKE".equals(type)) {
			piclass = "生产报废单";
			whoami = "MakeScrap";
		} else if ("OS".equals(type)) {
			piclass = "委外报废单";
			whoami = "MakeScrap!Make";
		}
		// 判断工单的状态
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张报废单中!");
				}
			}
		}
		JSONObject js = makeDao.turnScrap(whoami, piclass, store);
		if (js != null) {
			if ("MAKE".equals(type)) {
				sb.append("成功生成，报废单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeScrap.jsp?formCondition=ms_idIS" + js.get("ms_id")
						+ "&gridCondition=md_msidIS" + js.get("ms_id") + "')\">" + js.get("ms_code") + "</a>&nbsp;<br>");
			} else if ("OS".equals(type)) {
				sb.append("成功生成，报废单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeScrapmake.jsp?formCondition=ms_idIS"
						+ js.get("ms_id") + "&gridCondition=md_msidIS" + js.get("ms_id") + "')\">" + js.get("ms_code") + "</a>&nbsp;<br>");
			}
		}
		return sb.toString();
	}

	/**
	 * 生成拨出单 调拨到配套中心
	 */
	@Override
	public String turnProdIOBC(String data, String inwhcode, String whmancode, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String code = null;
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		JSONObject j = null;
		String piclass = "", whoami = "", inwhname = "";
		Object[] objs = null;
		Object prid = null;
		int mmid = 0, mmdetno = 0;
		piclass = "拨出单";
		whoami = "ProdInOut!AppropriationOut";
		// 判断工单的状态
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张拨出单中!");
				}
			}
		}
		inwhname = baseDao.getFieldDataByCondition("warehouse", "wh_description", "wh_code='" + inwhcode + "'").toString();
		MapComparator comparator = new MapComparator("mm_detno");
		Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "mm_whcode");
		List<Map<Object, Object>> s = null;
		for (Object m : map.keySet()) {
			if (m != null) {
				s = map.get(m);
				Collections.sort(s, comparator);
				j = makeDao.newProdIO(m.toString(), piclass, whoami, null);
				baseDao.execute("update prodinout set pi_purpose='" + inwhcode + "',pi_purposename='" + inwhname + "',pi_cgycode='"
						+ whmancode + "',pi_cgy=(select max(em_name) from employee where em_code='" + whmancode + "')  where pi_id="
						+ j.getInt("pi_id"));
				if (j != null) {
					code = j.getString("pi_inoutno");
					int detno = 1;
					String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
							+ "pd_ordercode, pd_orderdetno, pd_orderid, pd_prodid,pd_whcode,pd_inwhcode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					for (Map<Object, Object> p : s) {
						mmid = Integer.parseInt(p.get("mm_id").toString());
						mmdetno = Integer.parseInt(p.get("mm_detno").toString());
						if (mmid < 0) {// 替代料mp_mmid
							mmid = Math.abs(mmid);
							objs = baseDao.getFieldsDataByCondition("MakeMaterialReplace", new String[] { "mp_mmcode", "mp_mmdetno",
									"mp_prodcode" }, "mp_mmid=" + mmid + " AND mp_detno=" + p.get("mm_detno").toString());
							mmdetno = Integer.parseInt(objs[1].toString());
						}
						prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + p.get("mm_prodcode").toString() + "'");
						baseDao.execute(newdetailstr,
								new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), j.getInt("pi_id"), code, piclass, detno++, 0,
										"ENTERING", p.get("mm_prodcode").toString(), Double.parseDouble(p.get("mm_thisqty").toString()),
										p.get("mm_code").toString(), mmdetno, mmid, prid, m.toString(), inwhcode });
					}
					sb.append("成功产生拨出单,单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
							+ "</a>&nbsp;");
					
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String turnPurMould(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		JSONObject j = null;
		String newcode = null;
		int newid = 0;
		int detno = 1;
		Object[] objs = null;
		String log = null;
		Object vendcode = maps.get(0).get("pm_vendcode");
		Object pmcode = maps.get(0).get("pm_code");
		Object iscust = maps.get(0).get("app_iscust");
		j = appMouldDao.newPurMould(vendcode, iscust, pmcode, caller);
		if (j != null) {
			newcode = j.getString("pm_code");
			newid = j.getInt("pm_id");
		}
		for (Map<Object, Object> map : maps) {
			Object adid = map.get("ad_id"); // 开模申请单明细ID
			appMouldDao.toAppointedPurMould(newid, newcode, adid, detno++, "开模申请单");
			// 修改SendNotifyDetail
			baseDao.updateByCondition("AppMouldDetail", "ad_statuscode='TURNPURC', ad_status='已转采购单'", "ad_id=" + adid);
			// 记录日志
			objs = baseDao.getFieldsDataByCondition("AppMouldDetail", "ad_appid,ad_detno", "ad_id=" + adid);
			baseDao.logger.turnDetail("msg.turnPurchase", caller, "ap_id", objs[0], objs[1]);
		}
		log = "转入成功,模具采购单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/purcMould.jsp?formCondition=pm_idIS" + newid
				+ "&gridCondition=pmd_pmidIS" + newid + "&whoami=Purc!Mould')\">" + newcode + "</a>&nbsp;";
		return log;
	}

	// 模具采购单转模具付款申请
	@Override
	public String vastToMouleFee(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if (maps.size() > 0) {
			boolean mouldFeePurcDet = baseDao.isDBSetting(caller, "mouldFeePurcDet");
			if (!mouldFeePurcDet) {
				// 判断采购单状态、本次数量限制
				purMouldDao.checkPdYamount(maps);
			}
			String log = null;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pm_vendcode", "pu_receivecode",
					"pm_currency", "pm_prjcode" });
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				// 转入主记录
				Integer pm_id = baseDao.getFieldValue("PurMouldDet", "pd_pmid", "pd_id=" + items.get(0).get("pd_id"), Integer.class);
				Key key = transferRepository.transfer(caller, pm_id);
				if (key != null) {
					int mp_id = key.getId();
					String mp_code = key.getCode();
					index++;
					if (!mouldFeePurcDet) {
						// 转入明细
						transferRepository.transfer(caller, items, key);
					} else {
						for (Map<Object, Object> map : items) {
							Object pdid = map.get("pd_id");
							baseDao.execute("insert into MOULDFEEPLEASEDETAIL(mfd_id, mfd_mpid, mfd_code, mfd_purccode, mfd_pddetno, mfd_purcamount, "
									+ "mfd_amount, mfd_paymentscode, mfd_payments, mfd_pdid, mfd_purcdetno, mfd_pscode, mfd_custcode, mfd_custname, "
									+ "mfd_sellercode, mfd_seller, mfd_turndate) select MOULDFEEPLEASEDETAIL_seq.nextval, "
									+ mp_id
									+ ",'"
									+ mp_code
									+ "', pm_code, pd_detno, pmd_total, round(pmd_total*in_purcrate/100,2), in_code, pd_paydesc, pd_id, pmd_detno, "
									+ "pmd_pscode, pm_custcode, pm_custname, cu_sellercode, cu_sellername, sysdate from PURMOULD left join PurMouldDetail on pm_id=pmd_pmid "
									+ "left join PURMOULDDET on pd_pmid=pm_id left join customer on pm_custcode=cu_code "
									+ "left join Installment on pd_paydesc=in_name where pd_id=" + pdid);
							baseDao.updateByCondition(
									"PURMOULDDET",
									"pd_yamount=nvl((SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_pdid=pd_id),0)",
									"pd_id=" + pdid);
						}
					}
					if (mouldFeePurcDet) {
						baseDao.updateByCondition("MOULDFEEPLEASEDETAIL", "mfd_detno=ROWNUM", "mfd_mpid=" + mp_id);
					}
					baseDao.updateByCondition(
							"PURMOULDDETAIL",
							"pmd_yamount=nvl((SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_purccode=pmd_code and mfd_purcdetno=pmd_detno),0)",
							"exists (select 1 from MOULDFEEPLEASEDETAIL where mfd_purccode=pmd_code and mfd_purcdetno=pmd_detno and mfd_mpid="
									+ mp_id + ") ");
					baseDao.updateByCondition("MOULDFEEPLEASE",
							"mp_total=(SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_mpid=mp_id)",
							"mp_id=" + mp_id);
					log = "转入成功,申请单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/mouldFeePlease.jsp?formCondition=mp_idIS" + mp_id
							+ "&gridCondition=mfd_mpidIS" + mp_id + "')\">" + mp_code + "</a>";
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			// 修改采购单状态
			for (Map<Object, Object> map : maps) {
				int pdid = Integer.parseInt(map.get("pd_id").toString());
				purMouldDao.udpatestatus(pdid);
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String confirmThrowQty(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, List<Map<Object, Object>>> maps = BaseUtil.groupMap(store, "mr_mdid");
		List<Map<Object, Object>> s = null;
		int ddqty = 0;
		String mr_mdid = "";
		String sql = "";
		String log = "替代料的数量超过可投放的数量";
		List<String> listsql = new ArrayList<String>();
		for (Object m : maps.keySet()) {
			if (m != null) {
				s = maps.get(m);
				int a = 0;
				for (Map<Object, Object> map : s) {
					mr_mdid = map.get("mr_mdid").toString();
					String mr_id = map.get("mr_id").toString();
					Object qty = map.get("mr_changeqty");
					a += Integer.parseInt(qty.toString());
					String sql1 = "update mrpreplace set mr_changeqty='" + Integer.parseInt(qty.toString()) + "' where mr_id='" + mr_id
							+ "' and mr_mdid='" + mr_mdid + "'";
					listsql.add(sql1);
				}
				sql = "select sum(mr_needqty) from mrpreplace where mr_mdid=' " + mr_mdid + "'group by mr_mdid";
				ddqty = baseDao.getCount(sql);
				// 判断替代料的数量是否超过可投放的数量
				if (a > ddqty) {
					return log = "替代料的数量超过可投放的数量";
				}
			}
		}
		baseDao.execute(listsql);
		log = "确认成功";
		return log;
	}

	@Override
	public void updatecust(String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object statuscode = map.get("ps_custreturnstatus");
			if (statuscode == null || "".equals(statuscode)) {
				baseDao.updateByCondition("ProductSet", "ps_custreturnstatus='已返还'", "ps_id=" + map.get("ps_id"));
				// 记录操作
				baseDao.logger.others("更新客户返还状态", "msg.updateSuccess", "ProductSet", "ps_id", map.get("ps_id"));
			}
		}
	}

	@Override
	public void updatevend(String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object statuscode = map.get("ps_vendreturnstatus");
			if (statuscode == null || "".equals(statuscode)) {
				baseDao.updateByCondition("ProductSet", "ps_vendreturnstatus='已返还'", "ps_id=" + map.get("ps_id"));
				// 记录操作
				baseDao.logger.others("更新供应商返还状态", "msg.updateSuccess", "ProductSet", "ps_id", map.get("ps_id"));
			}
		}
	}

	private void judgeMakeStatus(List<Map<Object, Object>> store) {
		Map<Object, List<Map<Object, Object>>> mamap = BaseUtil.groupsMap(store, new Object[] { "mm_code" });
		String macode = "";
		for (Object m : mamap.keySet()) {
			if (m != null) {
				macode += "," + m.toString();
			}
		}
		String codes = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(ma_code) from make where ma_code in('" + macode.replace(",", "','")
						+ "') and nvl(ma_statuscode,' ')<>'AUDITED'", String.class);
		if (codes != null) {
			BaseUtil.showError("工单:" + codes + "未审核,不能生成单据!");
		}
		codes = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(ma_code) from make where ma_code in('" + macode.replace(",", "','")
						+ "') and nvl(ma_checkstatuscode,' ')<>'APPROVE'", String.class);
		if (codes != null) {
			BaseUtil.showError("工单:" + codes + "未批准,不能生成单据!");
		}
	}

	@Override
	public String vastTurnMakeCraft(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		String log = null;
		int maid = 0;
		String str = CollectionUtil.pluckSqlString(store, "ma_id");
		if (str != null) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(0) cn ,wm_concat(ma_code) error  from make left join craft on ma_craftcode=cr_code where nvL(cr_statuscode,' ')<>'AUDITED' and ma_id in("
							+ str + ") and rownum<11");
			if (rs.next() && rs.getInt("cn") > 0) {
				BaseUtil.showError("制造单必须维护有效的工艺路线，制造单号[" + rs.getString("error") + "]");
			}
		}
		for (Map<Object, Object> m : store) {
			maid = Integer.parseInt(m.get("ma_id").toString());
			Key key = transferRepository.transfer(caller, maid);
			if (key != null) {
				int mcid = key.getId();
				// 更新 mc_qty
				baseDao.updateByCondition("makeCraft", "mc_qty=" + m.get("ma_thisqty") + ",mc_linecode='" + m.get("ma_teamcode") + "'",
						"mc_id=" + mcid);
				boolean isE = baseDao.isDBSetting("Make!ToMakeCraft!Deal", "ifMakeEqualsMakeCraft");
				if (isE) {// 更新作业单号和数量等于制造单号和数量
					Object[] obs = baseDao.getFieldsDataByCondition("make", new String[] { "ma_code", "ma_qty" }, "ma_id=" + maid);
					if (obs != null) {
						baseDao.updateByCondition("makeCraft", "mc_code='" + obs[0] + "',mc_qty=" + obs[1], "mc_id=" + mcid);
						key.setCode(obs[0].toString());
					}
				}
				index++;
				// 转入明细
				transferRepository.transferDetail(caller, maid, key);
				baseDao.execute("update makecraftdetail set (mcd_nextstepcode,mcd_nextstepname)=(select max(mcd_stepcode),max(mcd_stepname) from makeCraftdetail a where a.mcd_mcid="
						+ mcid + " and a.mcd_detno=makeCraftdetail.mcd_detno+1) where mcd_mcid=" + mcid);
				log = "转入成功,生产车间作业单:" + "<a href=\"javascript:openUrl('jsps/pm/mes/makeCraft.jsp?formCondition=mc_idIS" + mcid
						+ "&gridCondition=mcd_mcidIS" + mcid + "')\">" + key.getCode() + "</a>&nbsp;";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		return sb.toString();
	}

	/**
	 * 委外单转委外验收单
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String turnProdIOMakeOS(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "ma_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		Object piid = 0;
		String log = null;
		int index = 0;
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao.queryForRowSet("select  count(1) n from (select distinct ma_cop from make where ma_id in (" + ids
					+ ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的委外单不允许合并下达到一张委外验收单中!");
				}
			}
		}
		if (store.size() > 0) {
			Object _code = store.get(0).get("pi_inoutno");
			Object whcode = store.get(0).get("wh_code");
			if (_code != null && _code.toString().trim().length() > 0) {
				code = String.valueOf(store.get(0).get("pi_inoutno"));
				Object[] pi = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_invostatuscode", "pi_statuscode",
						"pi_cardcode", "pi_receivecode" }, "pi_inoutno='" + code + "' and pi_class='委外验收单'");
				if (pi == null) {
					BaseUtil.showError("指定的委外验收单不存在或已删除！");
				} else if (!"ENTERING".equals(pi[1])) {
					BaseUtil.showError("指定的委外验收单状态不等于[在录入]！");
				} else if (!"UNPOST".equals(pi[2])) {
					BaseUtil.showError("指定的委外验收单状态不等于[未过账]！");
				} else {
					SqlRowList r = baseDao
							.queryForRowSet("select count(1) cn from (select nvl(nvl(ma_apvendcode,ve_apvendcode),ve_code) apvendcode "
									+ "from make left join vendor on ve_code=ma_vendcode " + "where ma_id in (" + ids
									+ "))t where t.apvendcode <> '" + pi[4] + "'");
					if (r.next()) {
						if (r.getInt("cn") > 0) {
							BaseUtil.showError("指定入库单" + _code + "应付供应商与委外单应付供应商不一致!");
						}
					}
					transferRepository.transfer(caller, store, new Key(Integer.parseInt(pi[0].toString()), code.toString()));
					if (whcode != null && !"".equals(whcode)) {
						baseDao.execute("update ProdinOut set pi_whcode='" + whcode + "' where pi_id=" + piid
								+ " and nvl(pi_whcode,' ')=' '");
						baseDao.execute("update ProdinOut set pi_whname=(select wh_description from warehouse where pi_whcode=wh_code) where pi_id="
								+ piid);
						baseDao.execute("update Prodiodetail set pd_whcode='" + whcode + "' where pd_piid=" + piid
								+ " and nvl(pd_whcode,' ')=' '");
					}
					piid = pi[0];
					baseDao.execute("update Prodiodetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid="
							+ piid);
					baseDao.execute("update Prodiodetail set pd_total=round(pd_inqty*pd_orderprice,2) where pd_piid=" + piid);
					log = "转入成功,委外验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
							+ "&gridCondition=pd_piidIS" + piid + "&whoami=ProdInOut!OutsideCheckIn')\">" + code + "</a>&nbsp;";
					sb.append(log).append("<hr>");
				}
			} else {
				for (Map<Object, Object> map : store) {
					SqlRowList rs = baseDao
							.queryForRowSet(
									"select ma_vendcode,nvl(nvl(ma_apvendcode,ve_apvendcode),ma_vendcode) apvendcode from make left join vendor on ve_code=ma_vendcode where ma_id=?",
									map.get("ma_id"));
					if (rs.next()) {
						map.put("ma_apvendcode", rs.getObject("apvendcode"));
					}
				}
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "ma_vendcode", "ma_currency",
						"ma_apvendcode" });
				// 按供应商+币别+应付供应商分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入委外验收单主记录
					Integer maid = Integer.parseInt(items.get(0).get("ma_id").toString());
					Key key = transferRepository.transfer(caller, maid);
					if (key != null) {
						piid = key.getId();
						code = key.getCode();
						for (Map<Object, Object> p : items) {
							// 重新更新已转委外验收单数
							baseDao.execute("update make set ma_tomadeqty=(select sum(pd_inqty)-sum(case when pd_status=99 then pd_outqty else 0 end) from prodiodetail where pd_ordercode=ma_code  and pd_piclass in('完工入库单','委外验收单','委外验退单')) where ma_id="
									+ p.get("ma_id").toString());
							baseDao.execute("update make set ma_tomadeqty=ma_madeqty where ma_id=" + p.get("ma_id").toString()
									+ " and ma_madeqty>ma_tomadeqty");
							makeDao.setMaxCanMadeqty(p.get("ma_id").toString());// 更新最大套料数
							SqlRowList rs0 = baseDao
									.queryForRowSet("select ma_qty,nvl(ma_finishoverrate,0) ma_finishoverrate,NVL(ma_madeqty,0)ma_madeqty,NVL(ma_tomadeqty,0)ma_tomadeqty,NVL(ma_canmadeqty,0)ma_canmadeqty,NVL(wc_makegreater,0)wc_makegreater,ma_code,nvl(mk_finishunget,0)mk_finishunget from make left join workcenter on wc_code=ma_wccode left join makekind on ma_kind=mk_name  where ma_id="
											+ p.get("ma_id").toString());
							if (rs0.next()) {
								if(rs0.getDouble("ma_finishoverrate") == 0){
									// 判断是否超工单数
									if (rs0.getDouble("wc_makegreater") == 0
											&& Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")
											- rs0.getDouble("ma_tomadeqty")) {
										BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
												+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
									}
									// 判断是否超工单数
									if (rs0.getDouble("wc_makegreater") == 0
											&& Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")
											- rs0.getDouble("ma_madeqty")) {
										BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
												+ "+已完工数" + rs0.getString("ma_madeqty") + "大于工单数：" + rs0.getString("ma_qty") + "!");
									}
								}else{
									// 判断是否工单超工比例的超工单数
									if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
													- rs0.getDouble("ma_tomadeqty")) {
										BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
												+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
									}
									// 判断是否工单超工比例的超工单数   已完工
									if (Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_qty")*rs0.getDouble("ma_finishoverrate")
													- rs0.getDouble("ma_madeqty")) {
										BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
												+ "+已完工数" + rs0.getString("ma_madeqty") + "大于工单数" + rs0.getString("ma_qty") + "!");
									}
								}
								// 判断是否超已领料套数,如果工单类型是未完工可领料，则不限制套料数
								if ((Double.parseDouble(p.get("ma_thisqty").toString()) > rs0.getDouble("ma_canmadeqty")
										- rs0.getDouble("ma_tomadeqty"))
										&& rs0.getInt("mk_finishunget") == 0) {
									BaseUtil.showError("工单:" + rs0.getString("ma_code") + "本次 入库数" + p.get("ma_thisqty").toString()
											+ "+已转完工数" + rs0.getString("ma_tomadeqty") + "大于工单数领料套数：" + rs0.getString("ma_canmadeqty")
											+ "!");
								}
							}
						}
						index++;
						// 转入明细
						transferRepository.transfer(caller, items, key);
						if (whcode != null && !"".equals(whcode)) {
							baseDao.execute("update ProdinOut set pi_whcode='" + whcode + "' where pi_id=" + piid
									+ " and nvl(pi_whcode,' ')=' '");
							baseDao.execute("update Prodiodetail set pd_whcode='" + whcode + "' where pd_piid=" + piid
									+ " and nvl(pd_whcode,' ')=' '");
						}
						int argCount = baseDao.getCountByCondition("user_tab_columns",
								"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
						if (argCount == 2) {
							baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
									+ piid);
							baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
									+ piid + " AND NVL(pi_receivecode,' ')=' '");
						}
						baseDao.execute("update ProdinOut set pi_whname=(select wh_description from warehouse where pi_whcode=wh_code) where pi_id="
								+ piid);
						baseDao.execute("update ProdinOut set pi_sellername=pi_belongs where pi_id=" + piid);
						baseDao.execute("update ProdinOut set pi_rate=(select CR_RATE from Currencys where pi_currency=cr_name) where pi_id="
								+ piid + " and nvl(pi_rate,0)=0");
						baseDao.execute("update Prodiodetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid="
								+ piid);
						baseDao.execute("update Prodiodetail set pd_total=round(pd_inqty*pd_orderprice,2) where pd_piid=" + piid);
						log = "转入成功,委外验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
								+ "&gridCondition=pd_piidIS" + piid + "&whoami=ProdInOut!OutsideCheckIn')\">" + code + "</a>&nbsp;";
						sb.append(index).append(": ").append(log).append("<hr>");
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void refreshFeatureView(String ftcode, String caller) {
		String str = baseDao.callProcedure("MM_FEATUREVIEW", new Object[] { Integer.valueOf(SystemSession.getUser().getEm_id()), ftcode });
		if (str != null && !str.equals("")) {
			BaseUtil.showError(str);
		}
	}

	@Override
	public void refreshFeatureViewProd(String ftcode, String caller) {
		String str = baseDao.callProcedure("MM_FEATUREVIEW_PR",
				new Object[] { Integer.valueOf(SystemSession.getUser().getEm_id()), ftcode });
		if (str != null && !str.equals("")) {
			BaseUtil.showError(str);
		}
	}

	@Override
	public String batchGoodsOff(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		// 按仓库分组。拨入仓库一致的分为一组,拨入仓库有可能为空
		Map<Object, List<Map<Object, Object>>> map = new HashMap<Object, List<Map<Object, Object>>>();
		List<Map<Object, Object>> list = null;
		for (Map<Object, Object> map1 : store) {
			Object key = map1.get("gu_whcode");
			if (map.containsKey(key)) {
				list = map.get(key);
			} else {
				list = new ArrayList<Map<Object, Object>>();
			}
			list.add(map1);
			map.put(key, list);
		}
		String ids = CollectionUtil.pluckSqlString(store, "gd_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		int id;
		// 判断上架数量-已销售数量是否大于0
		String error = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(gd_barcode) from PM_GOODSSALE_VIEW where gd_id in(" + ids + ") and (gd_qty-nvl(gd_saleqty,0))<=0",
				String.class);
		if (error != null) {
			BaseUtil.showError("上架批号[" + error + "]上架数量-已销售数量必须大于0");
		}

		for (Object m : map.keySet()) {
			List<Map<Object, Object>> s = null;
			int detno = 1;
			List<String> sqls = new ArrayList<String>();
			if (m != null) {
				s = map.get(m);
				code = baseDao.sGetMaxNumber("GoodsOff", 2);
				id = baseDao.getSeqId("GOODSCHANGE_SEQ");
				// 转入主表
				baseDao.execute("insert into GoodsChange(gc_id,gc_code,gc_indate,gc_inman,gc_status," + "gc_statuscode,gc_whcode,gc_type)"
						+ "values(" + id + ",'" + code + "',sysdate,'" + SystemSession.getUser().getEm_name() + "','"
						+ BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','" + m + "','下架')");
				baseDao.execute("update goodschange set gc_whname=(select wh_description from warehouse where wh_code=gc_whcode) where gc_id="
						+ id);
				// 转入明细表
				for (Map<Object, Object> mn : s) {
					sqls.add("insert into goodschangedetail(gcd_id,gcd_gcid,gcd_detno,gcd_prodcode,gcd_barcode,gcd_oldprice,"
							+ "gcd_oldmadedate,gcd_oldminpackqty,gcd_oldminbuyqty,gcd_oldremark,gcd_b2bbatchcode,gcd_uuid,gcd_offqty,"
							+ "gcd_whcode,gcd_whname) " + "select GOODSCHANGEDETAIL_SEQ.nextval," + id + "," + detno
							+ ",gd_prodcode,gd_barcode,gd_price,gd_madedate,gd_minpackqty,gd_minbuyqty,gd_remark,gd_b2bbatchcode,gd_uuid,"
							+ mn.get("gd_offqty")
							+ ",gd_whcode,gd_whname from goodsup left join goodsdetail on gd_guid=gu_id where gd_barcode='"
							+ mn.get("gd_barcode") + "'");
					detno++;
				}
				baseDao.execute(sqls);
				sb.append("转入成功,下架单号:" + "<a href=\"javascript:openUrl('jsps/b2c/sale/goodsChange.jsp?formCondition=gc_idIS" + id
						+ "&gridCondition=gcd_gcidIS" + id + "&whoami=GoodsOff')\">" + code + "</a>&nbsp;");
			}
		}
		return sb.toString();
	}

	@Override
	public void vastMakeClose(String data, String caller) {
		// 判断制造单状态
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "ma_id");
		String error = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ma_code) from make where ma_id in(" + ids + ") and (ma_status <>'已审核' OR ma_statuscode <>'AUDITED')",
				String.class);
		if (error != null) {
			BaseUtil.showError("只能关闭已审核的制造单[" + error + "]");
		}
		Object ma_bzremark = null;
		List<String> sqls = new ArrayList<String>();
		List<String> sqlsRemark = new ArrayList<String>();
		for (Map<Object, Object> map : store) {
			sqls.add("update make set ma_status='已关闭',ma_statuscode='CLOSED' where ma_id=" + map.get("ma_id"));
			sqls.add("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
					+ SystemSession.getUser().getEm_name() + "','批量关闭','关闭成功','Make|ma_id=" + map.get("ma_id") + "')");
			sqls.add("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
					+ SystemSession.getUser().getEm_name() + "','批量关闭','关闭成功','Make!Base|ma_id=" + map.get("ma_id") + "')");

			if (map.containsKey("ma_bzremark") && !StringUtil.hasText(map.get("ma_bzremark"))) {
				BaseUtil.showError("关闭备注没有填写,关闭失败!");
			}
			ma_bzremark = map.get("ma_bzremark");
			// ma_bzremark 关闭备注
			if (StringUtil.hasText(ma_bzremark)) {
				sqlsRemark.add("update make set ma_bzremark='" + ma_bzremark + "' where ma_id=" + map.get("ma_id"));
			}
		}
		baseDao.execute(sqls);
		baseDao.execute(sqlsRemark);
	}

	@Override
	public void vastMakeOpen(String data, String caller) {
		// 判断制造单状态
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "ma_id");
		String error = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ma_code) from make where ma_id in(" + ids + ") and (ma_status <>'已关闭' OR ma_statuscode <>'CLOSED')",
				String.class);
		if (error != null) {
			BaseUtil.showError("只能打开已关闭的制造单[" + error + "]");
		}
		List<String> sqls = new ArrayList<String>();
		List<String> sqlsRemark = new ArrayList<String>();
		for (Map<Object, Object> map : store) {
			sqls.add("update make set ma_status='已审核',ma_statuscode='AUDITED' where ma_id=" + map.get("ma_id"));
			sqls.add("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
					+ SystemSession.getUser().getEm_name() + "','批量打开','打开成功','Make|ma_id=" + map.get("ma_id") + "')");
			sqls.add("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
					+ SystemSession.getUser().getEm_name() + "','批量打开','打开成功','Make!Base|ma_id=" + map.get("ma_id") + "')");
			sqlsRemark.add("update make set ma_bzremark='' where ma_id=" + map.get("ma_id"));
		}
		baseDao.execute(sqls);
		baseDao.execute(sqlsRemark);
	}

	@Override
	@Transactional
	public void updateMakeSubMaterial(String data, String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		if (map.get("mm_id") != null) {
			int mm_id = Integer.valueOf(map.get("mm_id").toString());
			makeDao.setThisQty(mm_id, 0, "");
			int repdetno = Integer.parseInt(map.get("mp_detno").toString());
			int detno = Integer.parseInt(map.get("mm_detno").toString());
			Object repqty = baseDao.getFieldDataByCondition("makematerialreplace", "nvl(mp_repqty,0)+nvl(mp_haverepqty,0) ", "mp_mmid="
					+ mm_id + " and mp_detno=" + repdetno);
			if (repqty != null) {
				if (Float.parseFloat(map.get("mp_canuseqty").toString()) < Float.parseFloat(repqty.toString())) {
					BaseUtil.showError("可替代数量不能小于此替代料已转领料数!制造单:" + map.get("ma_code") + ",工单序号:" + detno + "替代料序号：" + repdetno);
				}
			}
			// 更新替代料数量
			baseDao.updateByCondition("MakeMaterialReplace", "mp_canuseqty='" + map.get("mp_canuseqty") + "'", "mp_mmid=" + mm_id
					+ " AND mp_detno=" + repdetno);
			//更新工单明细的替代料维护数
			baseDao.updateByCondition("MakeMaterial", "mm_canuserepqty='" + map.get("mp_canuseqty") + "'", "mm_id=" + mm_id
					+ " AND mm_detno=" + detno);
			SqlRowList sl0 = baseDao
					.queryForRowSet("select max(mm_qty-(NVL(mm_havegetqty,0)-NVL(mm_addqty,0)-NVL(mm_haverepqty,0))-NVL(mm_totaluseqty,0)+NVL(mm_repqty,0)) as canrepqty,sum(nvl(mp_canuseqty,0)) as allqty,"
							+ "max(mm_maid) mm_maid  from makematerial left join makematerialreplace on mp_mmid=mm_id where mm_id='"
							+ mm_id + "' ");
			if (sl0.next()) {
				if (sl0.getFloat("allqty") > sl0.getFloat("canrepqty")) {
					BaseUtil.showError("可替代数量不能大于工单需求数-主料已领数-主料已转数!制造单:" + map.get("ma_code") + ",工单序号:" + detno);
					return;
				}
			}
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
					"第" + detno + "行,修改替代料" + map.get("mp_prodcode") + "数量", "成功", "Make|ma_id=" + sl0.getString("mm_maid")));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
					"第" + detno + "行,修改替代料" + map.get("mp_prodcode") + "数量", "成功", "Make!Base|ma_id=" + sl0.getString("mm_maid")));
		}
	}

	@Override
	public void vastSetMain(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, List<Map<Object, Object>>> group = BaseUtil.groupMap(store, "mm_id");
		if (group.size() < store.size()) {
			BaseUtil.showError("针对同一工单同一用料明细不能同时勾选两行或多行然后点击设为主料");
		}
		for (Map<Object, Object> map : store) {
			int detno = Integer.valueOf(map.get("mp_detno").toString());
			int mm_id = Integer.valueOf(map.get("mm_id").toString());
			SqlRowList sl = baseDao
					.queryForRowSet("select mm_maid,mm_detno,mm_prodcode,mm_totaluseqty,mm_haverepqty,mm_repqty,mp_prodcode,mm_havegetqty,mm_whcode,nvl(mp_whcode,mm_whcode) mp_whcode,mm_code from MakeMaterial left join makematerialreplace on mp_mmid=mm_id where mm_id="
							+ mm_id + " and mp_detno=" + detno);
			if (sl.next()) {
				int maid = sl.getInt("mm_maid");
				if (sl.getDouble("mm_havegetqty") > 0 || sl.getDouble("mm_haverepqty") > 0) {
					BaseUtil.showError("已发生领料的明细行不能变更主替关系,制造单:" + sl.getString("mm_code") + ",工单序号:" + sl.getString("mm_detno"));
				}
				if (sl.getDouble("mm_repqty") > 0 || sl.getDouble("mm_totaluseqty") > 0) {
					BaseUtil.showError("已转领料数大于0的明细行不能变更主替关系,制造单:" + sl.getString("mm_code") + ",工单序号:" + sl.getString("mm_detno"));
				}
				baseDao.updateByCondition("makematerial", "mm_prodcode='" + sl.getString("mp_prodcode") + "'", "mm_id=" + mm_id);
				baseDao.updateByCondition("makematerialreplace",
						"mp_prodcode='" + sl.getString("mm_prodcode") + "',mp_whcode='" + sl.getString("mm_whcode") + "'", "mp_mmid="
								+ mm_id + " and mp_detno=" + detno);
				baseDao.updateByCondition("makematerial",
						"mm_repprodcode=(select wm_concat(mp_prodcode) from makematerialreplace where mp_mmid=mm_id)", "mm_id=" + mm_id);
				// 记录操作
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + sl.getString("mm_detno") + "替代料设为主料,"
						+ "原主料:" + sl.getString("mm_prodcode"), "成功", "Make|ma_id=" + maid));
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + sl.getString("mm_detno") + "替代料设为主料,"
						+ "原主料:" + sl.getString("mm_prodcode"), "成功", "Make!Base|ma_id=" + maid));
			}
		}
	}

	/**
	 * 多工单拉式发料生成调拨单和领料单
	 */
	@Override
	public String multiturnlssend(String data, boolean bywhcode, String wipwhcode, String maid, String departmentcode, String emcode,
			String cgycode, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String bccode = null;
		Object bcpiid = 0;
		JSONObject j = null;
		// 判断是否手工填写本次调拨数
		Boolean makeSendLS_NoCount = baseDao.isDBSetting(caller, "makeSendLS_NoCount");
		// 是否生成调拨单，勾选不生成，不勾选默认生成
		Boolean notCreateAppropriation = baseDao.isDBSetting(caller, "notCreateAppropriation");
		String llpiclass = "生产领料单", llcaller = "ProdInOut!Picking", bcpiclass = "拨出单", bccaller = "ProdInOut!AppropriationOut";
		Object ma_tasktype = baseDao.getFieldDataByCondition("Make", "ma_tasktype", "ma_id in(" + maid + ")");
		boolean bool = baseDao.checkIf("make", "(nvl(ma_checkstatuscode,' ')<>'APPROVE' or ma_statuscode<>'AUDITED') and ma_id in (" + maid
				+ ")");
		if (bool) {
			BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
		}
		if ("OS".equals(ma_tasktype)) {
			llpiclass = "委外领料单";
			llcaller = "ProdInOut!OutsidePicking";
			SqlRowList rs = baseDao.queryForRowSet("select count (distinct nvl(ma_apvendcode,ma_vendcode)) cn from make where ma_id in ("
					+ maid + ")");
			if (rs.next() && rs.getInt("cn") > 1) {
				BaseUtil.showError("多个委外单的应付供应商必须一致");
			}
		}
		j = makeDao.newProdIO(wipwhcode, llpiclass, llcaller, null);
		// 更新部门和人员
		baseDao.execute("update prodinout set pi_emcode='" + emcode + "',pi_emname=(select max(em_name) from employee where em_code='"
				+ emcode + "'),pi_departmentcode='" + departmentcode
				+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode + "' ) where pi_id="
				+ j.getInt("pi_id"));
		if (cgycode != null && !cgycode.equals("")) {
			baseDao.execute("update prodinout set pi_cgycode='" + cgycode + "',pi_cgy=(select max(em_name) from employee where em_code='"
					+ cgycode + "')  where pi_id=" + j.getInt("pi_id"));
		}
		String llcode = j.getString("pi_inoutno");
		Object llpiid = j.get("pi_id");
		int lldetno = 1;
		String wipwhname = "";
		wipwhname = baseDao.getFieldDataByCondition("warehouse", "wh_description", "wh_code='" + wipwhcode + "'").toString();
		if (bywhcode) {// 按仓库分组
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "mm_whcode");
			List<Map<Object, Object>> s = null;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					int bcdetno = 1;
					int mmid = 0, mmdetno = 0, singlemaid = 0;
					Object prid = null;
					Float thisplanqty = null;
					Float wipuseqty = null;
					Float thisqty = null;
					Object mm_code = null;
					List<String> sqls = new ArrayList<String>();
					for (Map<Object, Object> p : s) {
						mmid = Integer.parseInt(p.get("mm_id").toString());
						singlemaid = Integer.parseInt(p.get("mm_maid").toString());
						mmdetno = Integer.parseInt(p.get("mm_detno").toString());
						thisplanqty = Float.parseFloat(p.get("mm_thisplanqty").toString());
						wipuseqty = Float.parseFloat(p.get("mm_wipuseqty").toString());
						thisqty = Float.parseFloat(p.get("mm_thisqty").toString());
						mm_code = baseDao.getFieldDataByCondition("MakeMaterial", "mm_code", "mm_maid=" + singlemaid);
						if (!makeSendLS_NoCount && thisplanqty + wipuseqty < thisqty) {
							thisqty = thisplanqty + wipuseqty;
						}
						// 产生拨出单明细
						if (thisplanqty > 0 && !notCreateAppropriation) {
							if (bcdetno == 1) {
								j = makeDao.newProdIO(m.toString(), bcpiclass, bccaller, null);
								// 更新部门和人员
								baseDao.execute("update prodinout set pi_type='库存转移',pi_purpose='" + wipwhcode + "',pi_purposename='"
										+ wipwhname + "',pi_emcode='" + emcode
										+ "',pi_emname=(select max(em_name) from employee where em_code='" + emcode
										+ "'),pi_departmentcode='" + departmentcode
										+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode
										+ "' ) where pi_id=" + j.getInt("pi_id"));
								if (cgycode != null && !cgycode.equals("")) {
									baseDao.execute("update prodinout set pi_cgycode='" + cgycode
											+ "',pi_cgy=(select max(em_name) from employee where em_code='" + cgycode + "')  where pi_id="
											+ j.getInt("pi_id"));
								}
								bccode = j.getString("pi_inoutno");
								bcpiid = j.get("pi_id");
								sb.append("成功产生，拨出单号:"
										+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + bcpiid
										+ "&gridCondition=pd_piidIS" + bcpiid + "&whoami=" + bccaller + "')\">" + bccode + "</a>&nbsp;<br>");
								// 标示领料单的拨出单号
								baseDao.execute("update ProdInOut set pi_fromcode='" + bccode + "' where pi_id=" + llpiid);
							}
							if (bccode != null && llcode != null) {
								prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + p.get("mm_prodcode").toString()
										+ "'");
								String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
										+ "pd_ordercode, pd_orderdetno, pd_orderid, pd_prodid,pd_whcode,pd_inwhcode,pd_inwhname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								if (mmid < 0) {
									mmdetno = Integer.parseInt(baseDao.getFieldDataByCondition("MakeMaterialReplace", "mp_mmdetno",
											"mp_mmid=" + Math.abs(mmid) + " AND mp_detno=" + mmdetno).toString());
								}
								baseDao.execute(newdetailstr,
										new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), bcpiid, bccode, bcpiclass, bcdetno++, 0,
												"ENTERING", p.get("mm_prodcode").toString(), thisplanqty, mm_code.toString(), mmdetno,
												singlemaid, prid, m.toString(), wipwhcode, wipwhname });
								// 20180126更新备注
								baseDao.execute("update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid ="+bcpiid);
							}

						}
						// 产生领料单明细
						if (thisqty > 0) {
							makeDao.turnOutWh(llcode, lldetno++, llpiclass, mmid, Integer.parseInt(p.get("mm_detno").toString()),
									Double.parseDouble(thisqty.toString()));
							sqls.add("update ProdIODetail set pd_outqty1=" + thisplanqty + " where pd_piid=" + llpiid + " and pd_pdno="
									+ (lldetno - 1));
						}
					}
					baseDao.execute(sqls);
				}
			}
		} else {
			int bcdetno = 1;
			List<String> sqls2 = new ArrayList<String>();
			int mmdetno = 0;
			for (Map<Object, Object> p : store) {
				int mmid = 0, singlemaid = 0;
				Object prid = null;
				Object mm_code = null;
				Float thisplanqty = null;
				Float thisqty = null;
				Float wipuseqty = null;
				mmdetno = Integer.parseInt(p.get("mm_detno").toString());
				mmid = Integer.parseInt(p.get("mm_id").toString());
				singlemaid = Integer.parseInt(p.get("mm_maid").toString());
				thisplanqty = Float.parseFloat(p.get("mm_thisplanqty").toString());
				wipuseqty = Float.parseFloat(p.get("mm_wipuseqty").toString());
				thisqty = Float.parseFloat(p.get("mm_thisqty").toString());
				mm_code = baseDao.getFieldDataByCondition("MakeMaterial", "mm_code", "mm_maid=" + singlemaid);
				if (!makeSendLS_NoCount && thisplanqty + wipuseqty < thisqty) {
					thisqty = thisplanqty + wipuseqty;
				}
				// 产生拨出单明细
				if (thisplanqty > 0 && !notCreateAppropriation) {
					if (bcdetno == 1) {
						j = makeDao.newProdIO(p.get("mm_whcode").toString(), bcpiclass, bccaller, null);
						// 更新部门和人员
						baseDao.execute("update prodinout set pi_type='库存转移',pi_purpose='" + wipwhcode + "',pi_purposename='" + wipwhname
								+ "',pi_emcode='" + emcode + "',pi_emname=(select max(em_name) from employee where em_code='" + emcode
								+ "'),pi_departmentcode='" + departmentcode
								+ "',pi_departmentname=(select max(dp_name) from department where dp_code='" + departmentcode
								+ "' ) where pi_id=" + j.getInt("pi_id"));
						if (cgycode != null && !cgycode.equals("")) {
							baseDao.execute("update prodinout set pi_cgycode='" + cgycode
									+ "',pi_cgy=(select max(em_name) from employee where em_code='" + cgycode + "')  where pi_id="
									+ j.getInt("pi_id"));
						}
						bccode = j.getString("pi_inoutno");
						bcpiid = j.get("pi_id");
						sb.append("成功产生，拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ bcpiid + "&gridCondition=pd_piidIS" + bcpiid + "&whoami=" + bccaller + "')\">" + bccode
								+ "</a>&nbsp;<br>");
						// 标示领料单的拨出单号
						baseDao.execute("update ProdInOut set pi_fromcode='" + bccode + "' where pi_id=" + llpiid);
					}
					if (bccode != null && llcode != null) {
						prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + p.get("mm_prodcode").toString() + "'");
						String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
								+ "pd_ordercode, pd_orderdetno, pd_orderid, pd_prodid,pd_whcode,pd_inwhcode,pd_inwhname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						if (mmid < 0) {
							mmdetno = Integer.parseInt(baseDao.getFieldDataByCondition("MakeMaterialReplace", "mp_mmdetno",
									"mp_mmid=" + Math.abs(mmid) + " AND mp_detno=" + mmdetno).toString());
						}
						baseDao.execute(newdetailstr, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), bcpiid, bccode, bcpiclass,
								bcdetno++, 0, "ENTERING", p.get("mm_prodcode").toString(), thisplanqty, mm_code.toString(), mmdetno,
								singlemaid, prid, p.get("mm_whcode").toString(), wipwhcode, wipwhname });
						// 20180126更新备注
						baseDao.execute("update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid ="+bcpiid);

					}
				}
				// 产生领料单明细
				if (thisqty > 0) {
					makeDao.turnOutWh(llcode, lldetno++, llpiclass, mmid, Integer.parseInt(p.get("mm_detno").toString()),
							Double.parseDouble(thisqty.toString()));
					sqls2.add("update ProdIODetail set pd_outqty1=" + thisplanqty + " where pd_piid=" + llpiid + " and pd_pdno="
							+ (lldetno - 1));
				}
			}
			baseDao.execute(sqls2);
		}
		// 20170321新增更新拨出单拨出仓库名称
		baseDao.execute("update Prodiodetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid=?",
				bcpiid);
		if ("OS".equals(ma_tasktype)) {
			String updatesql = "update prodinout set(pi_cardcode,pi_title,pi_receivecode,pi_receivename) = (select max(ma_vendcode),max(ma_vendname),max(nvl(ma_apvendcode,ma_vendcode)),max(nvl(ma_apvendname,ma_vendname)) from make where ma_id in ("
					+ maid + ")) where pi_id=" + llpiid;
			baseDao.execute(updatesql);
		}
		if (lldetno == 1) {// 条件不符，未生成明细数据
			baseDao.deleteByCondition("ProdInOut", "pi_id=" + llpiid);
			BaseUtil.showError("数量有误.本次领料数不能超过 线边仓可用 + 调拨数");
		} else {
			String ifaudit = "";
			if (bccode == null && !llcode.equals("")) {
				try {
					// 不需要调拨有库存的直接过帐
					handlerService.handler(caller, "save", "after", new Object[] { llpiid });
					// 拉式发料，在不需要调拨的情况下自动过账领料单
					if (baseDao.isDBSetting(caller, "MakeSendLSAutoInOut")) {
						if (llpiclass.equals("生产领料单")) {
							prodInOutService.postProdInOut(j.getInt("pi_id"), "ProdInOut!Picking");
						} else if (llpiclass.equals("委外领料单")) {
							prodInOutService.postProdInOut(j.getInt("pi_id"), "ProdInOut!OutsidePicking");
						}
					}
					ifaudit = "自动过帐成功,";
				} catch (Exception e) {
					// 屏蔽错误
					ifaudit = "未自动过帐成功,";
				}
			}
			sb.append("成功产生," + ifaudit + llpiclass + "号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + llpiid
					+ "&gridCondition=pd_piidIS" + llpiid + "&whoami=" + llcaller + "')\">" + llcode + "</a>&nbsp;<br>");
			// 20180126更新备注
			baseDao.execute(
					"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid =?",
					 llpiid);
		}

		return sb.toString();
	}

	/**
	 * 加工委外单转加工验收单（批量界面）
	 */
	@Override
	@Transactional
	public String vastTurnProcessIn(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			StringBuffer sb = new StringBuffer();
			int index = 0;
			// 判断本次数量
			otherExplistDao.checkYqty(maps);
			String ids = CollectionUtil.pluckSqlString(maps, "md_id");
			handlerService.handler(caller, "turnProdIO", "before", new Object[] { maps });
			if (baseDao.isDBSetting("CopCheck")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ma_cop from OtherExplist,OtherExplistDetail where ma_id=md_maid and md_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的加工委外不允许合并下达到一张加工验收单!");
					}
				}
			}
			Object code = maps.get(0).get("pi_inoutno");
			String log = "";
			// 指定了加工验收单
			if (StringUtil.hasText(code)) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pi_id,pi_invostatuscode,pi_statuscode,pi_cop,pi_cardcode,pi_receivecode,pi_arcode,pi_currency,pi_paymentcode from ProdInOut where pi_inoutno=? and pi_class='加工验收单'",
								code);
				if (rs.next()) {
					// 判断指定的加工验收单状态是否[在录入]
					if (!"ENTERING".equals(rs.getString(2))) {
						BaseUtil.showError("只能指定[在录入]的加工验收单!");
					}
					if (!"UNPOST".equals(rs.getString(3))) {
						BaseUtil.showError("只能指定[未过账]的加工验收单!");
					}
					// 供应商、币别、付款方式不同，限制转单
					StringBuffer errBuffer = new StringBuffer();
					String errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(ma_code) from OtherExplist where ma_id in (select md_maid from OtherExplistdetail where md_id in ("
									+ ids + ")) and ma_vendcode<>?", String.class, rs.getString("pi_cardcode"));
					if (errSn != null)
						errBuffer.append("您选择的加工委外单的委外商，与指定的加工验收单的供应商不一致！加工委外单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(ma_code) from OtherExplist where ma_id in (select md_maid from OtherExplistdetail where md_id in ("
									+ ids + ")) and ma_currency<>?", String.class, rs.getString("pi_currency"));
					if (errSn != null)
						errBuffer.append("您选择的加工委外单的币别，与指定的加工验收单的币别不一致！加工委外单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(ma_code) from OtherExplist where ma_id in (select md_maid from OtherExplistdetail where md_id in ("
									+ ids + ")) and ma_paymentscode<>?", String.class, rs.getString("pi_paymentcode"));
					if (errSn != null)
						errBuffer.append("您选择的加工委外单的付款方式，与指定的加工验收单的付款方式不一致！加工委外单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					int pi_id = rs.getInt(1);
					if (baseDao.isDBSetting("CopCheck")) {
						errSn = baseDao.getJdbcTemplate().queryForObject(
								"select wm_concat(ma_code) from OtherExplist where ma_id in (select md_maid from OtherExplistdetail where md_id in ("
										+ ids + ")) and ma_cop<>?", String.class, rs.getString("pi_cop"));
						if (errSn != null)
							errBuffer.append("您选择的加工委外单的所属公司，与指定的加工验收单的所属公司不一致！加工委外单：<br>" + errSn.replace(",", "<hr>"));
					}
					if (errBuffer.length() > 0)
						BaseUtil.showError(errBuffer.toString());
					// 转入明细
					transferRepository.transfer("ProdInOut!ProcessIn", maps, new Key(pi_id, code.toString()));
					// 金额
					baseDao.execute(
							"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
							pi_id);
					baseDao.execute(
							"update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where nvl(pi_whcode,' ')=' ' and pi_id=?",
							pi_id);
					baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_inqty*pd_orderprice,2) where pd_piid=?", pi_id);
					baseDao.updateByCondition(
							"ProdIODetail",
							"pd_netprice=round(pd_orderprice/(1+pd_taxrate/100),8),pd_nettotal=round(round(pd_orderprice/(1+nvl(pd_taxrate,0)/100),8)*pd_inqty,2)",
							"pd_piid=" + pi_id);
					baseDao.execute(
							"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
							pi_id);
					baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
					log = "加工验收单:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
							+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!ProcessIn')\">" + code + "</a>&nbsp;";
					sb.append(log).append("<hr>");
				} else {
					BaseUtil.showError("指定加工验收单不存在!");
				}
			} else {// 未指定出货通知单
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "ma_vendcode", "ma_currency",
						"ma_paymentscode" });
				// 按委外商+币别+付款方式分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入加工验收单主记录
					Integer ma_id = baseDao.getFieldValue("OtherExplistDetail", "md_maid", "md_id=" + items.get(0).get("md_id"),
							Integer.class);
					Key key = transferRepository.transfer("ProdInOut!ProcessIn", ma_id);
					if (key != null) {
						int pi_id = key.getId();
						index++;
						// 转入明细
						transferRepository.transfer("ProdInOut!ProcessIn", items, key);
						baseDao.execute(
								"update ProdIODetail set pd_taxtotal=round(pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
								pi_id);
						baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_inqty*pd_orderprice,2) where pd_piid=?", pi_id);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_netprice=round(pd_orderprice/(1+pd_taxrate/100),8),pd_nettotal=round(round(pd_orderprice/(1+nvl(pd_taxrate,0)/100),8)*pd_inqty,2)",
								"pd_piid=" + pi_id);
						baseDao.execute(
								"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
								pi_id);
						baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
						log = "加工验收单:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
								+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!ProcessIn')\">" + key.getCode() + "</a>&nbsp;";
						sb.append(index).append("、 ").append(log).append("<hr>");
					}
				}
			}
			// 修改加工委外状态
			for (Map<Object, Object> map : maps) {
				int mdid = Integer.parseInt(map.get("md_id").toString());
				otherExplistDao.updateStatus(mdid);
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 工序委外单批量转委外收料单
	 */
	@Override
	@Transactional
	public String vastMakeCraftTurnAccept(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		//
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "mc_id"), ",");
		String codes = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(mc_code) from MAKECRAFT where mc_id in (" + ids
						+ ") and (nvl(mc_vendcode,' ')=' ' or nvl(mc_currency,' ')=' ')", String.class);
		if (codes != null) {
			return "委外单:" + codes + "的供应商或币别未填写!";
		}
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao.queryForRowSet("select  count(1) n from (select distinct mc_cop from MAKECRAFT where mc_id in (" + ids
					+ ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的工序委外单不允许合并下达到一张委外收料单!");
				}
			}
		}
		Object code = maps.get(0).get("va_code");
		String log = "";
		StringBuffer sb = new StringBuffer();
		int index = 0;
		// 指定了委外收料单
		if (StringUtil.hasText(code)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select va_id,va_statuscode,va_cop,va_vendcode,va_currency from verifyapply where va_code=? and va_class='委外收料单'",
							code);
			if (rs.next()) {
				// 判断指定的委外收料单状态是否[在录入]
				if (!"ENTERING".equals(rs.getString("va_statuscode"))) {
					BaseUtil.showError("只能指定[在录入]的委外收料单!");
				}
				// 供应商、币别不同，限制转单
				StringBuffer errBuffer = new StringBuffer();
				String errSn = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(mc_code) from MakeCraft where mc_id in (" + ids + ") and mc_vendcode<>?", String.class,
						rs.getString("va_vendcode"));
				if (errSn != null)
					errBuffer.append("您选择的工序委外单的委外商，与指定的委外收料单的供应商不一致！工序委外单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
				errSn = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(mc_code) from MakeCraft where mc_id in (" + ids + ") and mc_currency<>?", String.class,
						rs.getString("va_currency"));
				if (errSn != null)
					errBuffer.append("您选择的工序委外单的币别，与指定的委外收料单的币别不一致！工序委外单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
				int va_id = rs.getInt("va_id");
				if (baseDao.isDBSetting("CopCheck")) {
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(mc_code) from MakeCraft where mc_id in (" + ids + ") and mc_cop<>?", String.class,
							rs.getString("va_cop"));
					if (errSn != null)
						errBuffer.append("您选择的工序委外单的所属公司，与指定的委外收料单的所属公司不一致！工序委外单：<br>" + errSn.replace(",", "<hr>"));
				}
				if (errBuffer.length() > 0) {
					BaseUtil.showError(errBuffer.toString());
				}
				for (Map<Object, Object> p : maps) {
					SqlRowList rs0 = baseDao.queryForRowSet("select mc_whinqty,mc_code,mc_yqty from MakeCraft where mc_id="
							+ p.get("mc_id"));
					if (rs0.next()) {
						if (Double.parseDouble(p.get("mc_tqty").toString()) + rs0.getGeneralDouble("mc_yqty") > rs0
								.getGeneralDouble("mc_whinqty")) {
							BaseUtil.showError("工序委外单:" + rs0.getString("mc_code") + "本次 收料数" + p.get("mc_tqty").toString() + "+已转收料数"
									+ rs0.getGeneralDouble("mc_yqty") + "大于领料套数" + rs0.getGeneralDouble("mc_whinqty") + "!");
						}
					}
				}
				// 转入明细
				transferRepository.transfer("MakeCraftStep!Verify!Deal", maps, new Key(va_id, code.toString()));
				baseDao.execute("update VerifyApplydetail set vad_ordertotal=round(vad_orderprice*vad_qty,2) where vad_vaid=?", va_id);
				baseDao.execute(
						"update VerifyApply set va_total=round((select sum(vad_ordertotal) from VERIFYAPPLYdetail where va_id=vad_vaid),2) where va_id=?",
						va_id);
				log = "委外收料单:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?formCondition=va_idIS" + va_id
						+ "&gridCondition=vad_vaidIS" + va_id + "&whoami=VerifyApply!OS')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
			} else {
				BaseUtil.showError("指定委外收料单不存在!");
			}
		} else {// 未指定委外收料单
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "mc_vendcode", "mc_currency" });
			List<Map<Object, Object>> s = null;
			for (Object obj : groups.keySet()) {
				if (obj != null) {
					s = groups.get(obj);
					for (Map<Object, Object> p : s) {
						SqlRowList rs0 = baseDao.queryForRowSet("select mc_whinqty,mc_code,mc_yqty from MakeCraft where mc_id="
								+ p.get("mc_id"));
						if (rs0.next()) {
							if (Double.parseDouble(p.get("mc_tqty").toString()) + rs0.getGeneralDouble("mc_yqty") > rs0
									.getGeneralDouble("mc_whinqty")) {
								BaseUtil.showError("工序委外单:" + rs0.getString("mc_code") + "本次 收料数" + p.get("mc_tqty").toString() + "+已转收料数"
										+ rs0.getGeneralDouble("mc_yqty") + "大于领料套数" + rs0.getGeneralDouble("mc_whinqty") + "!");
							}
						}
					}
				}
			}
			// 按委外商+币别分组的转入操作
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object ms : mapSet) {
				items = groups.get(ms);
				// 转入委外收料单主记录
				Integer mc_id = Integer.parseInt(items.get(0).get("mc_id").toString());
				Key key = transferRepository.transfer("MakeCraftStep!Verify!Deal", mc_id);
				if (key != null) {
					int va_id = key.getId();
					index++;
					// 转入明细
					transferRepository.transfer("MakeCraftStep!Verify!Deal", items, key);
					baseDao.execute("update VerifyApplydetail set vad_ordertotal=round(vad_orderprice*vad_qty,2) where vad_vaid=?", va_id);
					baseDao.execute(
							"update VerifyApply set va_total=round((select sum(vad_ordertotal) from VERIFYAPPLYdetail where va_id=vad_vaid),2) where va_id=?",
							va_id);
					log = "委外收料单:<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?formCondition=va_idIS" + va_id
							+ "&gridCondition=vad_vaidIS" + va_id + "&whoami=VerifyApply!OS')\">" + key.getCode() + "</a>&nbsp;";
					sb.append(index).append("、 ").append(log).append("<hr>");
				}
			}
		}
		// 修改工序委外单状态
		for (Map<Object, Object> map : maps) {
			makeCraftDao.updateStatus(map.get("mc_id"));
		}
		return sb.toString();
	}

	private List<Map<String, Object>> splitProdIo(String caller, String pi_class, String pi_inoutno) {
		// 按仓库拆分，并且有多仓库
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select max(pi_id)pi_id,pd_whcode,MAX(pd_whname)pd_whname from prodinout left join prodiodetail on pd_piid=pi_id where pd_inoutno=? and pd_piclass=? group by pd_whcode",
						pi_inoutno, pi_class);
		int pi_id = 0;
		int npiid = 0;
		String ninoutno = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		if (rs.next()) {
			if (rs.getResultList().size() > 1) {
				List<String> sqls = new ArrayList<String>();
				while (rs.next()) {
					map = new HashMap<String, Object>();
					pi_id = rs.getInt("pi_id");
					npiid = baseDao.getSeqId("PRODINOUT_SEQ");
					ninoutno = baseDao.sGetMaxNumber(caller, 2);
					sqls.add("INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
							+ " pi_recordman, pi_recorddate, pi_whcode, pi_whname, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_cardcode,pi_title,pi_printstatuscode,pi_printstatus,pi_receivecode,pi_receivename,pi_intype)"
							+ " select "
							+ npiid
							+ ",'"
							+ ninoutno
							+ "', pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
							+ " pi_recordman, pi_recorddate,'"
							+ rs.getString("pd_whcode")
							+ "','"
							+ rs.getString("pd_whname")
							+ "', pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_cardcode,pi_title,pi_printstatuscode,pi_printstatus,pi_receivecode,pi_receivename,pi_intype from prodinout where pi_id="
							+ pi_id);
					sqls.add("insert into prodiodetail(pd_id,Pd_Piid,pd_piclass,pd_inoutno,pd_pdno,pd_batchcode,pd_batchid,pd_prodid,"
							+ " pd_ordercode,pd_orderdetno,pd_sdid,pd_prodcode,pd_beipinoutqty,"
							+ " pd_sendprice,pd_orderprice,pd_plancode,pd_location,pd_description,"
							+ " pd_remark,pd_pocode,pd_taxrate,pd_discount,pd_custprodcode,pd_custprodspec,pd_outqty,pd_sellercode,"
							+ " pd_seller,pd_barcode,pd_department,pd_price,pd_status,pd_inqty "
							+ " ,PD_TOTALOUTQTY,PD_WHCODE,PD_WHNAME,PD_INWHCODE,PD_INWHNAME,PD_ORDERID,PD_SNID,PD_BONDED,PD_CUSTOMPRICE,PD_TAXAMOUNT,PD_SKSTATUS,"
							+ " Pd_Forecastdetno,pd_ioid,pd_noticeid,pd_notinqty1,pd_vendorreplydate,pd_salecode,pd_custcode,pd_custname,pd_remark2,pd_remark3,pd_outqty1,pd_custproddetail,pd_topmothercode)"
							+ " select prodiodetail_seq.nextval,"
							+ npiid
							+ ",pd_piclass,'"
							+ ninoutno
							+ "',rownum,pd_batchcode,pd_batchid,pd_prodid,"
							+ " pd_ordercode,pd_orderdetno,pd_sdid,pd_prodcode,pd_beipinoutqty,"
							+ " pd_sendprice,pd_orderprice,pd_plancode,pd_location,pd_description,"
							+ " pd_remark,pd_pocode,pd_taxrate,pd_discount,pd_custprodcode,pd_custprodspec,pd_outqty,pd_sellercode,"
							+ " pd_seller,pd_barcode,pd_department,pd_price,pd_status,pd_inqty "
							+ " ,PD_TOTALOUTQTY,'"
							+ rs.getString("pd_whcode")
							+ "','"
							+ rs.getString("pd_whname")
							+ "',PD_INWHCODE,PD_INWHNAME,PD_ORDERID,PD_SNID,PD_BONDED,PD_CUSTOMPRICE,PD_TAXAMOUNT,PD_SKSTATUS,"
							+ " Pd_Forecastdetno,pd_ioid,pd_noticeid,pd_notinqty1,pd_vendorreplydate,pd_salecode,pd_custcode,pd_custname,pd_remark2,pd_remark3,pd_outqty1,pd_custproddetail,pd_topmothercode"
							+ " from prodiodetail where pd_piid=" + pi_id + " and pd_whcode='" + rs.getString("pd_whcode") + "'");
					map.put("inoutno", ninoutno);
					map.put("id", npiid);
					list.add(map);
				}
				sqls.add("delete from prodinout where pi_id=" + pi_id);
				sqls.add("delete from prodiodetail where pd_piid=" + pi_id);
				baseDao.execute(sqls);
				return list;
			} else {
				map = new HashMap<String, Object>();
				map.put("inoutno", pi_inoutno);
				map.put("id", rs.getInt("pi_id"));
				list.add(map);
				return list;
			}
		}
		return null;
	}

	/**
	 * 转补料单
	 */
	@Override
	public String turnProdIOAdd(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		Object wh = (boolean) store.get(0).get("wh")? 1 : 0;
		String piclass = "", whoami = "";
		String picodestr = "", whcodestr = "";
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张补料单中!");
				}
			}
		}
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		// 获取是否增加按物料仓管员，物料大类进行分组
		boolean groupByPrWhcode = baseDao.isDBSetting(caller, "groupByPrWhcode");
		boolean groupByPrKind = baseDao.isDBSetting(caller, "groupByPrKind");
		String groupfield = null;
		if ("1".equals(wh.toString())) {// 按仓库分组
			groupfield = "mm_whcode,mc_tasktype,mc_vendcode,mc_apvendcode";
			if (groupByPrWhcode) {
				groupfield += ",pr_whmancode";
			}
			if (groupByPrKind) {
				groupfield += ",pr_kind";
			}
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					keys = m.toString().split("#");
					Object type = keys[1];
					if ("车间作业单".equals(type)) {
						piclass = "生产补料单";
						whoami = "ProdInOut!Make!Give";
					} else if ("工序委外单".equals(type)) {
						piclass = "委外补料单";
						whoami = "ProdInOut!OSMake!Give";
					}
					j = makeDao.newProdIOWithVendor(keys[0], keys[2], keys[3], piclass, whoami, null);
					if (j != null) {
						code = j.getString("pi_inoutno");
						picodestr += "," + code;
						detno = 1;
						for (Map<Object, Object> p : s) {
							makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
									Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()));
						}
						if ("工序委外单".equals(type)) {
							baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
						}
						sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
								+ "</a>&nbsp;<br>");
					}
				}
			}
		} else {
			groupfield = "mc_tasktype,mc_vendcode,mc_apvendcode";
			if (groupByPrWhcode) {
				groupfield += ",pr_whmancode";
			}
			if (groupByPrKind) {
				groupfield += ",pr_kind";
			}
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					keys = m.toString().split("#");
					Object type = keys[0];
					if ("车间作业单".equals(type)) {
						piclass = "生产补料单";
						whoami = "ProdInOut!Make!Give";
					} else if ("工序委外单".equals(type)) {
						piclass = "委外补料单";
						whoami = "ProdInOut!OSMake!Give";
					}
					j = makeDao.newProdIOWithVendor(null, keys[1], keys[2], piclass, whoami, null);
					if (j != null) {
						code = j.getString("pi_inoutno");
						picodestr += "," + code;
						detno = 1;
						for (Map<Object, Object> p : s) {
							makeDao.turnAdd(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
									Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
									String.valueOf(p.get("mm_whcode")), piclass, whoami);
						}
						if ("工序委外单".equals(type)) {
							baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
						}
						sb.append("转入成功,补料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
								+ "</a>&nbsp;<br>");
					}
				}
			}
		}
		if (!picodestr.equals("")) {
			picodestr = picodestr.substring(1);
			picodestr = picodestr.replace(",", "','");
			whcodestr = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in('" + picodestr
							+ "') and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (whcodestr != null) {
				BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
			}
			whcodestr = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in('" + picodestr
							+ "') and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (whcodestr != null) {
				BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * 转退料单
	 */
	@Override
	public String turnProdIOReturn(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		String whcode = CollectionUtil.pluckSqlString(store, "mm_whcode");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		String piclass = "", whoami = "", piintype = "";
		Object wh = (boolean) store.get(0).get("wh")? 1 : 0;
		Object outtoint = (boolean) store.get(0).get("outtoint")? 0 : 1;
		if ("1".equals(outtoint.toString())) {
			piintype = "水口料入库";
		}
		String whcodestr = "";
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张退料单中!");
				}
			}
		}
		whcodestr = baseDao.queryForObject("select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in(" + whcode
				+ ") and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')", String.class);
		if (whcodestr != null) {
			BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
		}
		whcodestr = baseDao.queryForObject("select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in(" + whcode
				+ ") and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')", String.class);
		if (whcodestr != null) {
			BaseUtil.showError("仓库:" + whcodestr + "不存在或不是已审核!");
		}
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		String groupfield = null;
		if ("1".equals(wh.toString())) {// 按仓库分组
			groupfield = "mm_whcode,mc_tasktype,mc_vendcode,mc_apvendcode";
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					keys = m.toString().split("#");
					Object type = keys[1];
					if ("车间作业单".equals(type)) {
						piclass = "生产退料单";
						whoami = "ProdInOut!Make!Return";
					} else if ("工序委外单".equals(type)) {
						piclass = "委外退料单";
						whoami = "ProdInOut!OutsideReturn";
					}
					j = makeDao.newProdIOWithVendor(keys[0], keys[2], keys[3], piclass, whoami, piintype);
					if (j != null) {
						code = j.getString("pi_inoutno");
						detno = 1;
						for (Map<Object, Object> p : s) {
							makeDao.turnInWh(code, detno++, Integer.parseInt(p.get("mm_id").toString()),
									Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("mm_thisqty").toString()),
									piclass);
						}
						if ("工序委外单".equals(type)) {
							baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
						}
						sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
								+ "</a>&nbsp;");
					}
				}
			}
		} else {
			groupfield = "mc_tasktype,mc_vendcode,mc_apvendcode";
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					keys = m.toString().split("#");
					Object type = keys[0];
					if ("车间作业单".equals(type)) {
						piclass = "生产退料单";
						whoami = "ProdInOut!Make!Return";
					} else if ("工序委外单".equals(type)) {
						piclass = "委外退料单";
						whoami = "ProdInOut!OutsideReturn";
					}
					j = makeDao.newProdIOWithVendor(null, keys[1], keys[2], piclass, whoami, piintype);
					if (j != null) {
						code = j.getString("pi_inoutno");
						detno = 1;
						for (Map<Object, Object> p : s) {
							makeDao.turnIn(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
									Double.parseDouble(p.get("mm_thisqty").toString()), String.valueOf(p.get("mm_whcode")), piclass, whoami);
						}
						if ("工序委外单".equals(type)) {
							baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
						}
						sb.append("转入成功,退料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
								+ "</a>&nbsp;");
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 转领料单
	 */
	@Override
	public String turnProdIOGet(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		String piclass = "", whoami = "";
		StringBuffer sb2 = new StringBuffer();
		Object wh = (boolean) store.get(0).get("wh")? 1 : 0;
		Object whman = store.get(0).get("whman");
		boolean turn = true;
		// 判断工单的状态
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的制造单不允许合并下达到一张领料单中!");
				}
			}
		}
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		// 明细行增加可转
		for (Map<Object, Object> p : store) {
			p.put("UnDoECN", false);
		}
		// 根据配置 ifUnDoECN 存在未执行制造ECN的不允许领料，判断是否允许领料
		boolean ifUnDoEcn = baseDao.isDBSetting(caller, "ifUnDoECN");
		String picodestr = "";
		MapComparator comparator = new MapComparator("mm_detno");
		// 获取是否增加按物料仓管员，物料大类进行分组
		boolean groupByPrWhcode = baseDao.isDBSetting(caller, "groupByPrWhcode");
		boolean groupByPrKind = baseDao.isDBSetting(caller, "groupByPrKind");
		String groupfield = null;
		if ("1".equals(wh.toString())) {// 按仓库分组
			groupfield = "mm_whcode,mc_tasktype,mc_vendcode,mc_apvendcode";
			if (groupByPrWhcode) {
				groupfield += ",pr_whmancode";
			}
			if (groupByPrKind) {
				groupfield += ",pr_kind";
			}
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					Collections.sort(s, comparator);
					// 判断是否存在未执行的ECN
					turn = checkUnDoECN(sb2, s, ifUnDoEcn);
					if (turn) {
						keys = m.toString().split("#");
						Object type = keys[1];
						if ("车间作业单".equals(type)) {
							piclass = "生产领料单";
							whoami = "ProdInOut!Picking";
						} else if ("工序委外单".equals(type)) {
							piclass = "委外领料单";
							whoami = "ProdInOut!OutsidePicking";
						}
						j = makeDao.newProdIOWithVendor(keys[0], keys[2], keys[3], piclass, whoami, null);
						if (j != null) {
							code = j.getString("pi_inoutno");
							if (whman != null) {
								baseDao.execute("update prodinout set pi_cgycode='" + whman
										+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
										+ j.getInt("pi_id"));
							}
							picodestr += "," + code;
							detno = 1;
							for (Map<Object, Object> p : s) {
								if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
									makeDao.turnOutWh(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
											Integer.parseInt(p.get("mm_detno").toString()),
											Double.parseDouble(p.get("mm_thisqty").toString()));
								}
							}
							if ("工序委外单".equals(type)) {
								baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
							}
							sb.append("转入成功,领料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							baseDao.execute(
									"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
									j.get("pi_id"));
						}
					}
				}
			}
		} else {
			groupfield = "mc_tasktype,mc_vendcode,mc_apvendcode";
			if (groupByPrWhcode) {
				groupfield += ",pr_whmancode";
			}
			if (groupByPrKind) {
				groupfield += ",pr_kind";
			}
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
			List<Map<Object, Object>> s = null;
			String[] keys = null;
			int detno = 1;
			for (Object m : map.keySet()) {
				if (m != null) {
					s = map.get(m);
					Collections.sort(s, comparator);
					// 判断是否存在未执行的ECN
					turn = checkUnDoECN(sb2, s, ifUnDoEcn);
					if (turn) {
						keys = m.toString().split("#");
						Object type = keys[0];
						if ("车间作业单".equals(type)) {
							piclass = "生产领料单";
							whoami = "ProdInOut!Picking";
						} else if ("工序委外单".equals(type)) {
							piclass = "委外领料单";
							whoami = "ProdInOut!OutsidePicking";
						}
						j = makeDao.newProdIOWithVendor(null, keys[1], keys[2], piclass, whoami, null);
						if (j != null) {
							code = j.getString("pi_inoutno");
							if (whman != null) {
								baseDao.execute("update prodinout set pi_cgycode='" + whman
										+ "',pi_cgy=(select max(em_name) from employee where em_code='" + whman + "')  where pi_id="
										+ j.getInt("pi_id"));
							}
							picodestr += "," + code;
							detno = 1;
							for (Map<Object, Object> p : s) {
								if (!Boolean.valueOf(p.get("UnDoECN").toString())) {
									makeDao.turnOut(code, j.getInt("pi_id"), detno++, Integer.parseInt(p.get("mm_id").toString()),
											Integer.parseInt(p.get("mm_detno").toString()),
											Double.parseDouble(p.get("mm_thisqty").toString()), String.valueOf(p.get("mm_whcode")),
											piclass, whoami);
								}
							}
							if ("工序委外单".equals(type)) {
								baseDao.execute("update prodinout set pi_intype='工序委外' where pi_id=" + j.get("pi_id"));
							}
							sb.append("转入成功,领料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + whoami + "')\">" + code
									+ "</a>&nbsp;<br>");
							baseDao.execute(
									"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
									j.get("pi_id"));
							baseDao.execute(
									"update prodiodetail set pd_remark=(select mm_remark from make left join makematerial on mm_maid=ma_id where ma_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid in(?)",
									j.get("pi_id"));
						}
					}
				}
			}
		}
		if (!picodestr.equals("")) {
			picodestr = picodestr.substring(1);
			picodestr = picodestr.replace(",", "','");
			String codes = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pi_whcode) from ProdInout  where  pi_inoutno in('" + picodestr
							+ "') and pi_whcode<>' ' and pi_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (codes != null) {
				BaseUtil.showError("仓库:" + codes + "不存在，不能领料!");
			}
			codes = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pd_whcode) from prodiodetail where pd_inoutno in('" + picodestr
							+ "') and pd_whcode<>' ' and pd_whcode not in (select wh_code from warehouse where wh_statuscode='AUDITED')",
					String.class);
			if (codes != null) {
				BaseUtil.showError("仓库:" + codes + "不存在，不能领料!");
			}
		}

		sb.append("<hr>").append(sb2);
		return sb.toString();
	}

	/**
	 * 转报废单
	 */
	@Override
	public String turnStockScrap(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String piclass = "", whoami = "";
		// 判断工单的状态
		judgeMakeStatus(store);
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的单据不允许合并下达到一张报废单中!");
				}
			}
		}
		String groupfield = "mm_code,mc_tasktype";
		Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, groupfield.split(","));
		List<Map<Object, Object>> s = null;
		String[] keys = null;
		for (Object m : map.keySet()) {
			if (m != null) {
				s = map.get(m);
				keys = m.toString().split("#");
				Object type = keys[1];
				if ("车间作业单".equals(type)) {
					piclass = "生产报废单";
					whoami = "MakeScrap";
				} else if ("工序委外单".equals(type)) {
					piclass = "委外报废单";
					whoami = "MakeScrap!Make";
				}
				JSONObject js = makeDao.turnScrap(whoami, piclass, s);
				if (js != null) {
					if ("车间作业单".equals(type)) {
						sb.append("成功生成，报废单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeScrap.jsp?formCondition=ms_idIS"
								+ js.get("ms_id") + "&gridCondition=md_msidIS" + js.get("ms_id") + "')\">" + js.get("ms_code")
								+ "</a>&nbsp;<br>");
					} else if ("工序委外单".equals(type)) {
						sb.append("成功生成，报废单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeScrapmake.jsp?formCondition=ms_idIS"
								+ js.get("ms_id") + "&gridCondition=md_msidIS" + js.get("ms_id") + "')\">" + js.get("ms_code")
								+ "</a>&nbsp;<br>");
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public void batchEndMakeCraft(String caller ,String data) { 
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object, Object>map:store){
			baseDao.updateByCondition("MakeCraft", "mc_status='" + BaseUtil.getLocalMessage("FINISH") + "',mc_statuscode='FINISH'", "mc_id="
					+ map.get("mc_id"));
			baseDao.logger.others("结案操作", "批量结案", caller,
					"mc_id", map.get("mc_id"));
			if (baseDao.isDBSetting("usingMakeCraft")) {
				Object sign = baseDao.getFieldDataByCondition("MakeCraft", "count(*)", "mc_makecode='"+map.get("mc_makecode")+"' and mc_status<>'已结案'");
				if(StringUtil.hasText(sign) && "0".equals(sign.toString())){
					baseDao.updateByCondition("Make", "ma_status='" + BaseUtil.getLocalMessage("FINISH") + "',ma_statuscode='FINISH'", "ma_code='"
							+ map.get("mc_makecode") + "'");
					Object ma_id = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='"+map.get("mc_makecode")+"'");
					baseDao.logger.others("结案操作", "明细工厂单均结案自动结案", "Make!Base",
							"ma_id", ma_id);
				}
			}
		}
	}

	@Override
	public void changeWhcode(String isrep, String whcode, String mmid,String mpdetno) {
		Object wh_id = baseDao.getFieldDataByCondition("warehouse", "wh_id", "wh_code='"+whcode+"'");
		
		if(!"".equals(isrep)){
			//是替代料
			if(!"".equals(wh_id)){
				baseDao.execute("update makematerialreplace set mp_whcode='"+whcode+"',mp_warehouseid='"+wh_id+"' where mp_mmid ="+mmid+" and mp_detno="+mpdetno);
			}
		}else{
			//不是替代料
			baseDao.execute("update makematerial set mm_whcode='"+whcode+"',mm_whid='"+wh_id+"' where mm_id="+mmid);
		}
	}
	
	/**
	 * 转取消执行
	 */
	@Override
	public void batchMakeECNCancelPerform(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int flag = 0;
		StringBuffer sb=new StringBuffer();
		for(Map<Object, Object> map:maps){
			sb.append(map.get("md_id"));
			sb.append(",");
		}
		String md_ids = sb.substring(0, sb.toString().length()-1);
		for(Map<Object, Object> map:maps){
			flag++;
			Object md_id = map.get("md_id");
			int count = baseDao.getCount("select count(1) from makematerialchangedet where md_id="+md_id+" and nvl(MD_DIDSTATUS,' ') in (' ','待执行')");
			if(count==0){
				BaseUtil.showError("第"+flag+"行，制造ECN明细执行状态不是空或者待执行！");
			}
			count = baseDao.getCount("select count(1) from makematerialchangedet where md_edid in (select nvl(md_edid,0) from makematerialchangedet where md_id="+md_id+" )"
					+" and md_id<>"+md_id+" and md_id not in ("+md_ids+") and nvl(MD_DIDSTATUS,' ')<>'已取消'");
			if(count>0){
				BaseUtil.showError("所勾选的第"+flag+"行，制造ECN明细执存在相同ECN明细来源，且不存在勾选中或者未勾选！");
			}
			baseDao.execute("update makematerialchangedet set MD_DIDSTATUS='已取消' where md_id="+md_id);
			
			baseDao.logger.others("序号："+map.get("md_detno")+"批量转取消执行", "成功", "MakeMaterialChange", "mc_id", map.get("md_mcid"));
		}
	}
	
	/**
	 * 转执行
	 */
	@Override
	@Transactional
	public void batchMakeECNTurnPerform(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int flag = 0;
		StringBuffer sb=new StringBuffer();
		for(Map<Object, Object> map:maps){
			sb.append(map.get("md_id"));
			sb.append(",");
		}
		String md_ids = sb.substring(0, sb.toString().length()-1);
		for(Map<Object, Object> map:maps){
			flag++;
			Object md_id = map.get("md_id");
			//判断明细状态是否真正为，已取消或者无
			int count = baseDao.getCount("select count(1) from makematerialchangedet where md_id="+md_id+" and nvl(MD_DIDSTATUS,' ') in (' ','已取消')");
			if(count==0){
				BaseUtil.showError("第"+flag+"行，制造ECN明细执行状态不是空或者已取消！");
			}
			//判断是否存在同md_edid明细的不同状态的制造ECN明细
			count = baseDao.getCount("select count(1) from makematerialchangedet where md_edid in (select nvl(md_edid,0) from makematerialchangedet where md_id="+md_id+" )"
					+" and md_id<>"+md_id+" and md_id not in ("+md_ids+") and nvl(MD_DIDSTATUS,' ')<>'待执行'");
			if(count>0){
				BaseUtil.showError("所勾选的第"+flag+"行，该制造ECN明细行存在相同ECN明细来源，且不存在勾选中或者未勾选！");
			}
			baseDao.execute("update makematerialchangedet set MD_DIDSTATUS='待执行' where md_id="+md_id);
			
			baseDao.logger.others("序号："+map.get("md_detno")+"批量转执行", "成功", "MakeMaterialChange", "mc_id", map.get("md_mcid"));
		}
	}
}
