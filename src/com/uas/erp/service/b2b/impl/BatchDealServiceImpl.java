package com.uas.erp.service.b2b.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.model.Key;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2b.BatchDealService;

@Service("B2BBatchDealService")
public class BatchDealServiceImpl implements BatchDealService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private SaleDao saleDao;

	@Override
	public String onSaleNotifyDownSend(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			String idStr = CollectionUtil.pluckSqlString(maps, "sn_id");
			// 订单未审批
			checkOrder(idStr);
			// 存在变更单
			checkChange(idStr);
			SqlRowList rs = baseDao
					.queryForRowSet("select sn_id,sd_id,sd_said,sa_custcode,sa_shcustcode,sa_apcustcode,sa_currency,sa_paymentscode,sa_cop,sk_outtype from salenotifydown left join sale on sn_ordercode=sa_code left join saledetail on sd_said=sa_id and sd_detno=sn_orderdetno left join SaleKind on sk_name=sa_kind where sn_id in ("
							+ idStr + ") and nvl(sd_statuscode, ' ')='AUDITED'");
			List<Map<Object, Object>> acceptItems = new ArrayList<Map<Object, Object>>();
			List<Map<Object, Object>> prodItems = new ArrayList<Map<Object, Object>>();
			while (rs.next()) {
				Map<Object, Object> item = new HashMap<Object, Object>();
				item.put("sn_id", rs.getObject("sn_id"));
				item.put("sd_id", rs.getObject("sd_id"));
				item.put("sd_said", rs.getObject("sd_said"));
				item.put("sa_custcode", rs.getObject("sa_custcode"));
				item.put("sa_shcustcode", rs.getObject("sa_shcustcode"));
				item.put("sa_apcustcode", rs.getObject("sa_apcustcode"));
				item.put("sa_currency", rs.getObject("sa_currency"));
				item.put("sa_paymentscode", rs.getObject("sa_paymentscode"));
				item.put("sa_cop", rs.getObject("sa_cop"));
				for (Map<Object, Object> map : maps) {
					if (String.valueOf(map.get("sn_id")).equals(rs.getString("sn_id")))
						item.put("sd_tqty", map.get("sn_thisqty"));
				}
				if ("TURNOUT".equals(rs.getString("sk_outtype")))
					prodItems.add(item);
				else
					acceptItems.add(item);
			}
			StringBuffer result = new StringBuffer();
			if (acceptItems.size() > 0) {
				// 类型为转通知单
				Set<Key> keys = turnSendNotify(acceptItems);
				result.append("转入出货通知单：<br>");
				for (Key key : keys) {
					result.append("<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + key.getId()
							+ "&gridCondition=snd_snidIS" + key.getId() + "')\">" + key.getCode() + "</a><br>");
				}
			}
			if (prodItems.size() > 0) {
				// 类型为转出货
				Set<Key> keys = turnSaleOut(prodItems);
				result.append("转入出货单：<br>");
				for (Key key : keys) {
					result.append("<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + key.getId()
							+ "&gridCondition=pd_piidIS" + key.getId() + "&whoami=ProdInOut!Sale')\">" + key.getCode() + "</a><br>");
				}
			}
			return result.toString();
		}
		return null;
	}

	/**
	 * 是否有未审批的订单
	 */
	private void checkOrder(String notifyIds) {
		String codes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where exists (select 1 from SaleNotifyDown where sn_ordercode=sa_code and sn_orderdetno=sd_detno and sn_id in ("
								+ notifyIds + ")) and nvl(sa_statuscode, ' ')<>'AUDITED')", String.class);
		if (codes != null) {
			BaseUtil.showError("存在待审批的销售订单，不能进行转出操作!销售单号：" + codes);
		}
	}

	/**
	 * 是否有未审批的变更单
	 */
	private void checkChange(String notifyIds) {
		String codes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where exists (select 1 from SaleNotifyDown where sn_ordercode=sa_code and sn_orderdetno=sd_detno and sn_id in ("
								+ notifyIds
								+ ")) and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=sa_code and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' or sc_type<>'交期变更')))",
						String.class);
		if (codes != null) {
			BaseUtil.showError("存在待审批的销售变更单，不能进行转出操作!销售单号：" + codes);
		}
	}

	/**
	 * 批量转通知单
	 * 
	 * @return
	 */
	private Set<Key> turnSendNotify(List<Map<Object, Object>> maps) {
		// 判断本次数量
		saleDao.checkAdYqty(maps);
		String transferCaller = "Sale!ToAccept!Deal";
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode",
				"sa_currency", "sa_apcustcode", "sa_paymentscode", "sa_cop" });
		Set<Object> groupKeys = groups.keySet();
		List<Map<Object, Object>> items;
		Set<Key> keys = new HashSet<Key>();
		for (Object s : groupKeys) {
			items = groups.get(s);
			// 转入通知单主记录
			Key key = transferRepository.transfer(transferCaller, items.get(0).get("sd_said"));
			if (key != null) {
				int sn_id = key.getId();
				// 转入明细
				transferRepository.transfer(transferCaller, items, key);
				// 修改交期
				baseDao.updateByCondition(
						"SendNotify",
						"sn_deliverytime=(select max(sd_delivery) from saledetail left join SendNotifyDetail on sd_id=snd_sdid where snd_snid=sn_id)",
						"sn_id=" + sn_id);
				// 部门
				baseDao.execute(
						"update sendnotify set (sn_departmentcode,sn_departmentname)=(select dp_code,em_depart from employee left join department on em_depart=dp_name where em_code=sn_sellercode) where sn_id=?",
						sn_id);
				// 金额
				baseDao.execute(
						"update SendNotifyDetail set snd_total=round(snd_outqty*snd_sendprice,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6),snd_taxtotal=round(snd_outqty*snd_sendprice/(1+snd_taxrate/100),2) where snd_snid=?",
						sn_id);
				// 仓库
				baseDao.execute(
						"update SendNotifyDetail set snd_warehouse=(select wh_description from warehouse where wh_code=snd_warehousecode) where snd_snid=? and snd_warehousecode is not null",
						sn_id);
				keys.add(key);
			}
		}
		// 修改销售单状态
		for (Map<Object, Object> map : maps) {
			saleDao.updateturnstatus(Integer.parseInt(map.get("sd_id").toString()));
			baseDao.execute("update salenotifydown set sn_yqty=nvl(sn_yqty,0) + " + map.get("sd_tqty") + " where sn_id=" + map.get("sn_id"));
		}
		return keys;
	}

	/**
	 * 批量转出货
	 * 
	 * @param maps
	 * @return
	 */
	private Set<Key> turnSaleOut(List<Map<Object, Object>> maps) {
		String transferCaller = "Sale!ToProdIO!Deal";
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode",
				"sa_currency", "sa_apcustcode", "sa_paymentscode", "sa_cop" });
		Set<Object> groupKeys = groups.keySet();
		List<Map<Object, Object>> items;
		Set<Key> keys = new HashSet<Key>();
		for (Object s : groupKeys) {
			items = groups.get(s);
			// 转入出货单主记录
			Key key = transferRepository.transfer(transferCaller, items.get(0).get("sd_said"));
			if (key != null) {
				int pi_id = key.getId();
				// 转入明细
				transferRepository.transfer(transferCaller, items, key);
				baseDao.execute(
						"update ProdInOut set (pi_purposename, pi_expresscode)=(select CA_PERSON, CA_PHONE from CustomerAddress where pi_cardid=ca_cuid and ca_remark='是') where pi_id=?",
						pi_id);
				baseDao.execute(
						"update ProdIODetail set pd_netprice=ROUND(pd_netprice/(1 + pd_taxrate/ 100),6), pd_taxtotal=round(pd_sendprice*pd_outqty,2), pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?",
						pi_id);
				keys.add(key);
			}
		}
		// 修改销售单状态
		for (Map<Object, Object> map : maps) {
			saleDao.udpatestatus(Integer.parseInt(map.get("sd_id").toString()));
			baseDao.execute("update salenotifydown set sn_yqty=nvl(sn_yqty,0) + " + map.get("sd_tqty") + " where sn_id=" + map.get("sn_id"));
		}
		return keys;
	}

	@Override
	public String vastOpenVendorUU() {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Vendor", "ve_name",
					" nvl(ve_uu,' ')=' ' and ve_auditstatuscode='AUDITED' and nvl(ve_b2benable,0)=0");
			List<String> sqls = new ArrayList<String>();
			int nocountChecked = 0;
			int countChecked = 0;
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members",
									params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								if (backInfo.size() > 0) {
									for (String name : backInfo.keySet()) {
										int count = baseDao.getCount("select count(*) from vendor where nvl(ve_uu,' ')='"
												+ backInfo.get(name) + "'");
										if (count == 0) {
											sqls.add("update vendor set ve_emailkf='已获取', ve_uu=" + backInfo.get(name)
													+ ",ve_b2benable=1 where ve_name='" + name + "'");
											countChecked++;
										}
									}
								} else {
									nocountChecked++;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
			int lastcountChecked = baseDao.getCount("select count(*) from vendor where ve_emailkf='已获取' and nvl(ve_b2benable,0)=0 ");
			log.append("本次获取成功合计:" + countChecked + "条,失败合计:" + nocountChecked + "条,累计获取成功合计:" + lastcountChecked);
		} else {
			log.append("请先开通数据传输功能!");
		}
		return log.toString();
	}

	@Override
	public String vastOpenCustomerUU() {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Customer", "cu_name",
					" nvl(cu_uu,0)=0 and cu_auditstatuscode='AUDITED' and nvl(cu_b2benable,0)=0");
			List<String> sqls = new ArrayList<String>();
			int nocountChecked = 0;
			int countChecked = 0;
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members",
									params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								if (backInfo.size() > 0) {
									for (String name : backInfo.keySet()) {
										int count = baseDao.getCount("select count(*) from customer where nvl(cu_uu,0)="
												+ backInfo.get(name) + "");
										if (count == 0) {
											sqls.add("update customer set cu_checkuustatus='已获取', cu_uu=" + backInfo.get(name)
													+ ",cu_b2benable=1 where cu_name='" + name + "'");
											sqls.add("update CustSendSample set (ss_custcode,ss_custname)=(select cu_code,cu_name from customer where cu_uu=ss_custuu)"
													+ " where nvl(ss_custcode,' ')=' '");
											sqls.add("update SaleDown set (sa_custcode,sa_custname)=(select cu_code,cu_name from customer where cu_uu=sa_customeruu)"
													+ " where nvl(sa_custcode,' ')=' '");
											sqls.add("update QuotationDown set (qu_custcode,qu_custname)=(select cu_code,cu_name from customer where cu_uu=qu_custuu and nvl(cu_auditstatuscode,' ') <>'DISABLE')"
													+ " where nvl(qu_custcode,' ')=' '");
											countChecked++;
										}
									}
								} else {
									nocountChecked++;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
			int lastcountChecked = baseDao
					.getCount("select count(*) from customer where cu_checkuustatus='已获取' and nvl(cu_b2benable,0)=0 ");
			log.append("本次获取成功合计:" + countChecked + "条,失败合计:" + nocountChecked + "条,累计获取成功合计:" + lastcountChecked);
		} else {
			log.append("请先开通数据传输功能!");
		}
		return log.toString();
	}

	@Override
	public Map<String, Object> vastCheckUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("vendor", vastVendorUU());
		modelMap.put("customer", vastCustomerUU());
		return modelMap;
	}

	@Override
	public String vastCountVendorUU() {
		int countVendorY = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is not null");
		int countVendorN = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is null");
		return "已注册维护合计:" + countVendorY + "条,未维护合计:" + countVendorN + "条!";
	}

	@Override
	public String vastCountCustomerUU() {
		int countCustY = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)<>0 ");
		int countCustN = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)=0");
		return "已注册维护合计:" + countCustY + "条,未维护合计:" + countCustN + "条!";
	}

	@Override
	public Map<String, Object> vastCountUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int countVendorY = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is not null");
		int countVendorN = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is null");
		Map<String, Object> vendMap = new HashMap<String, Object>();
		vendMap.put("checkedcount", countVendorY);
		vendMap.put("nocheckedcount", countVendorN);
		int countCustY = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)<>0 ");
		int countCustN = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)=0");
		Map<String, Object> custmap = new HashMap<String, Object>();
		custmap.put("checkedcount", countCustY);
		custmap.put("nocheckedcount", countCustN);
		modelMap.put("vendor", vendMap);
		modelMap.put("customer", custmap);
		return modelMap;
	}

	public Map<String, Object> vastVendorUU() {
		Map<String, Object> vendMap = new HashMap<String, Object>();
		int countVendorY = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is not null");
		int countVendorN = baseDao.getCount("select count(*) from vendor where ve_auditstatuscode='AUDITED' and ve_uu is null");
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Vendor", "ve_name",
					" nvl(ve_uu,' ')=' ' and ve_auditstatuscode='AUDITED'");
			List<String> sqls = new ArrayList<String>();
			int nocountChecked = 0;
			int countChecked = 0;
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members",
									params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								if (backInfo.size() > 0) {
									for (String name : backInfo.keySet()) {
										int count = baseDao.getCount("select count(*) from vendor where nvl(ve_uu,' ')='"
												+ backInfo.get(name) + "'");
										if (count == 0) {
											sqls.add("update vendor set ve_emailkf='已获取', ve_uu=" + backInfo.get(name)
													+ ",ve_b2benable=1 where ve_name='" + name + "'");
											countChecked++;
										}
									}
								} else {
									nocountChecked++;
								}
							} else {
								nocountChecked++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
			vendMap.put("checkedcount", countVendorY);
			vendMap.put("nocheckedcount", countVendorN);
			vendMap.put("success", countChecked);
			vendMap.put("failure", nocountChecked);
		}
		return vendMap;
	}

	public Map<String, Object> vastCustomerUU() {
		Map<String, Object> custmap = new HashMap<String, Object>();
		int countCustY = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)<>0 ");
		int countCustN = baseDao.getCount("select count(*) from customer where cu_auditstatuscode='AUDITED' and nvl(cu_uu,0)=0");
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Customer", "cu_name", " nvl(cu_uu,0)=0 and cu_auditstatuscode='AUDITED'");
			List<String> sqls = new ArrayList<String>();
			int nocountChecked = 0;
			int countChecked = 0;
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members",
									params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								if (backInfo.size() > 0) {
									for (String name : backInfo.keySet()) {
										int count = baseDao.getCount("select count(*) from customer where nvl(cu_uu,0)="
												+ backInfo.get(name) + "");
										if (count == 0) {
											sqls.add("update customer set cu_checkuustatus='已获取', cu_uu=" + backInfo.get(name)
													+ ",cu_b2benable=1 where cu_name='" + name + "'");
											sqls.add("update CustSendSample set (ss_custcode,ss_custname)=(select cu_code,cu_name from customer where cu_uu=ss_custuu)"
													+ " where nvl(ss_custcode,' ')=' '");
											sqls.add("update SaleDown set (sa_custcode,sa_custname)=(select cu_code,cu_name from customer where cu_uu=sa_customeruu)"
													+ " where nvl(sa_custcode,' ')=' '");
											sqls.add("update QuotationDown set (qu_custcode,qu_custname)=(select cu_code,cu_name from customer where cu_uu=qu_custuu and nvl(cu_auditstatuscode,' ') <>'DISABLE')"
													+ " where nvl(qu_custcode,' ')=' '");											
											countChecked++;
										}
									}
								} else {
									nocountChecked++;
								}
							} else {
								nocountChecked++;
							}
						} catch (Exception e) {

							e.printStackTrace();
						}

					} else {

					}
				}
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
			custmap.put("checkedcount", countCustY);
			custmap.put("nocheckedcount", countCustN);
			custmap.put("success", countChecked);
			custmap.put("failure", nocountChecked);
		}
		return custmap;
	}
}
