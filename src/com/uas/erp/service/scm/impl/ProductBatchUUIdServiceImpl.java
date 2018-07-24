package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.b2c_erp.baisc.model.KindUas;
import com.uas.api.domain.IPage;
import com.uas.b2c.service.common.B2CComponentService;
import com.uas.b2c.service.common.B2CKindService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.ProductBatchUUIdService;

@Service("productBatchUUIdServiceImpl")
public class ProductBatchUUIdServiceImpl implements ProductBatchUUIdService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private B2CComponentService b2cComponentService;
	@Autowired
	private B2CKindService b2cKindService;

	@Override
	public List<Map<Object, Object>> getProductKindTree(String type, Long parentid) {
		try {
			List<KindUas> kindUas = b2cKindService.getProductKinds(parentid);
			if (!CollectionUtil.isEmpty(kindUas)) {
				// 接收返回值
				List<Map<Object, Object>> li = new ArrayList<Map<Object, Object>>();
				for (KindUas kind : kindUas) {
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("text", kind.getNameCn());
					map.put("id", kind.getId());
					map.put("count", kind.getCount());
					map.put("level", kind.getLevel());
					map.put("parentid", kind.getParentid());
					map.put("nameEn", kind.getNameEn());
					map.put("detno", kind.getDetno());
					map.put("isLeaf", kind.getIsLeaf());
					if (kind.getIsLeaf() == 1) {
						map.put("leaf", true);
						map.put("expandable", false);
						map.put("cls", "x-tree-cls-node");
					} else {
						map.put("leaf", false);
						map.put("expandable", true);
						if (kind.getParentid() == 0) {
							map.put("cls", "x-tree-cls-root");
						} else {
							map.put("cls", "x-tree-cls-parent");
						}
					}
					li.add(map);
				}
				return li;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IPage<ComponentInfoUas> getProductComponent(Long kindId, int page, int pageSize, String orispeccode) {
		if (orispeccode != null && !"".equals(orispeccode)) {// 根据kindid 和原厂型号
			List<ComponentInfoUas> compInfo = b2cComponentService.findComponentInfoUasByCodeAndKindId(kindId, orispeccode);
			if (!CollectionUtil.isEmpty(compInfo)) {
				IPage<ComponentInfoUas> info = new IPage<ComponentInfoUas>();
				info.setContent(compInfo);
				info.setSize(compInfo.size());
				info.setTotalElements(compInfo.size());
				return info;
			}
		} else {
			IPage<ComponentInfoUas> componentInfoUas = b2cComponentService.findAllComponentActiveSimpleInfo(page, pageSize, kindId, false);
			if (componentInfoUas != null) {
				return componentInfoUas;
			}
		}
		return null;
	}

	@Override
	public List<ComponentInfoUas> getUUIdByCode(String code) {
		List<ComponentInfoUas> componentInfoUas = b2cComponentService.findByCode(Arrays.asList(code.split(",")),SystemSession.getUser().getCurrentMaster());
		if (!CollectionUtil.isEmpty(componentInfoUas)) {
			return componentInfoUas;
		}
		return null;

	}

	@Override
	public List<ComponentInfoUas> getByUUIds(String ids) {
		List<ComponentInfoUas> componentInfoUas = b2cComponentService.getSimpleInfoByUuids(ids, SystemSession.getUser().getCurrentMaster());
		if (!CollectionUtil.isEmpty(componentInfoUas)) {
			return componentInfoUas;
		}
		return null;
	}

	@Override
	public Map<String, Object> getPageAccess() {
		Master master = SystemSession.getUser().getCurrentMaster();
		String url = Constant.b2cTestHost();
		if (master.getEnv().equals("prod")) {
			url = Constant.b2cHost();
		}
		String requestURL = url + "/api/webpage/token";
		Response response = new Response();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			response = HttpUtil.sendGetRequest(requestURL + "?id=" + master.getMa_uu() + "&secret=" + master.getMa_accesssecret(), null,
					true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (response.getStatusCode() == HttpStatus.OK.value()) {// 获取成功
			map = FlexJsonUtil.fromJson(response.getResponseText());
		} else {
			BaseUtil.showError(response.getResponseText());
		}
		map.put("path", url);
		return map;
	}

	// 通过选择UAS中物料范围全部导入至匹配表中
	@Override
	public void loadAllProd(String caller, String condition, String code) {
		Object[] obj1 = baseDao.getFieldsDataByCondition("form", "fo_detailtable,fo_detailcondition", " fo_caller='BatchUUIdSource'");
		if (obj1 == null) {
			return;
		}
		String BaseCondition = obj1[1].toString();
		BaseCondition = BaseCondition + " AND " + condition;
		SqlRowList sl = baseDao.queryForRowSet("select pr_code,pr_uuid,pr_orispeccode from " + obj1[0].toString() + " where "
				+ BaseCondition);
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		Object maxdetno = baseDao.getFieldDataByCondition("ProductUUIDBatch ", "nvl(max(pub_detno),0)", "pub_code='" + code + "'");
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		while (sl.next()) {
			modelmap = new HashMap<Object, Object>();
			modelmap.put("pub_id", baseDao.getSeqId("PRODUCTBATCH_SEQ"));
			modelmap.put("pub_emid", SystemSession.getUser().getEm_id());
			modelmap.put("pub_emcode", SystemSession.getUser().getEm_code());
			modelmap.put("pub_prodcode", sl.getObject("pr_code"));
			modelmap.put("pub_detno", detno);
			modelmap.put("pub_uuid", sl.getObject("pr_uuid"));
			modelmap.put("pub_orispeccode", sl.getObject("pr_orispeccode"));
			modelmap.put("pub_code", code);
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "ProductUUIDBatch"));
			detno++;
		}
		baseDao.execute(sqls);

	}

	@Override
	public void loadProd(String caller, String data, String code) {
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		Object maxdetno = baseDao.getFieldDataByCondition("ProductUUIDBatch ", "nvl(max(pub_detno),0)", "pub_code='" + code + "'");
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		for (int i = 0; i < lists.size(); i++) {
			map = lists.get(i);
			SqlRowList rs = baseDao.queryForRowSet("select pr_code,pr_uuid,pr_orispeccode from product where pr_id=?", map.get("pr_id"));
			if (rs.next()) {
				modelmap = new HashMap<Object, Object>();
				modelmap.put("pub_id", baseDao.getSeqId("PRODUCTBATCH_SEQ"));
				modelmap.put("pub_detno", detno);
				modelmap.put("pub_emid", SystemSession.getUser().getEm_id());
				modelmap.put("pub_emcode", SystemSession.getUser().getEm_code());
				modelmap.put("pub_uuid", rs.getString("pr_uuid"));
				modelmap.put("pub_prodcode", rs.getString("pr_code"));
				modelmap.put("pub_orispeccode", rs.getString("pr_orispeccode"));
				modelmap.put("pub_code", code);
				modelmap.put("pub_date",DateUtil.currentDateString(null));
				sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "ProductUUIDBatch"));
				detno++;
			}
		}
		baseDao.execute(sqls);
		getUUId(caller, code);
	}

	// 根据原厂型号获取uuid
	public void getUUId(String caller, String code) {
		try {
			List<String> oriCodes = baseDao
					.getJdbcTemplate()
					.queryForList(
							"select distinct pub_orispeccode from ProductUUIDBatch where pub_code=? and nvl(pub_uuid,' ')=' ' and nvl(pub_orispeccode,' ')<>' '",
							String.class, code);
			batchGetByOriCode(oriCodes,code);
		} catch (EmptyResultDataAccessException e) {

		}
	}

	// 解除匹配关系
	@Override
	public void removeUUId(String caller, String code, String data) {
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(gridStore, "pub_id");
		// 判断是否有上架，有采购单，
		String str = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pub_prodcode) code from ProductUUIDBatch left join PM_GOODSSALE_VIEW on pub_uuid=gd_uuid and pub_prodcode=gd_prodcode where gd_offqty>0 and  pub_id in("
								+ ids + ")", String.class);
		if (str != null) {
			BaseUtil.showError("存在有效的上架申请单，不允许解除对照关系，料号[" + str + "]");
		}
		str = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pub_prodcode) code from ProductUUIDBatch left join B2C$PURCHASEBATCH on pb_uuid=pub_uuid and pb_prodcode=pub_prodcode left join purchasedetail on pd_puid=pb_puid and pub_prodcode=pd_prodcode left join purchase on pu_id=pb_puid where pu_statuscode='AUDITED' and pub_id in("
								+ ids + ")", String.class);
		if (str != null) {
			BaseUtil.showError("存在已审核的采购单，不允许解除对照关系，料号[" + str + "]");
		}
		// 解除对照关系
		List<String> sqls = new ArrayList<String>();
		sqls.add("update product set pr_uuid='' where exists (select 1 from ProductUUIDBatch where pub_prodcode=pr_code and pub_code='"
				+ code + "' and pub_id in(" + ids + "))");
		sqls.add("delete from B2C$GOODSBATCH WHERE exists(select 1 from ProductUUIDBatch where pub_uuid=gb_uuid  and pub_code='" + code
				+ "' and pub_id in(" + ids + "))");
		sqls.add("delete from B2C$GOODSONHAND WHERE exists(select 1 from ProductUUIDBatch where pub_uuid=go_uuid and pub_code='" + code
				+ "' and pub_id in(" + ids + "))");
		sqls.add("update ProductUUIDBatch set pub_uuid='' where pub_code='" + code + "' and pub_id in(" + ids + ")");
		sqls.add("INSERT INTO messagelog(ml_date,ml_man,ml_content,ml_result,ml_search) select sysdate,'"
				+ SystemSession.getUser().getEm_name() + "','解除标准料号对照关系','解除成功','ProductBatchUUId|pub_id=0' from dual");
		baseDao.execute(sqls);
	}

	// 根据原厂型号搜索
	@Override
	public List<Map<Object, Object>> searchByOrispecode(String caller, String code) {
		Map<String, List<KindUas>> ku = b2cKindService.getParentsByCode(code);
		if (ku != null) {
			List<Map<Object, Object>> tree = new ArrayList<Map<Object, Object>>();
			for (Map.Entry<String, List<KindUas>> entry : ku.entrySet()) {
				for (KindUas s : entry.getValue()) {
					Map<Object, Object> ct = new HashMap<Object, Object>();
					if (s.getParentid() == 0) {
						ct = recursionFn(entry.getValue(), s, code);
						tree.add(ct);
					}
				}
			}
			return tree;
		}
		return null;
	}
	
	// 根据类目搜索
	@Override
	public List<Map<Object, Object>> searchByKindcode(String caller, String code) {
		Map<String, List<KindUas>> ku = b2cKindService.getParentsByKindCode(code);
		if (ku != null) {
			List<Map<Object, Object>> tree = new ArrayList<Map<Object, Object>>();
			for (Map.Entry<String, List<KindUas>> entry : ku.entrySet()) {
				for (KindUas s : entry.getValue()) {
					Map<Object, Object> ct = new HashMap<Object, Object>();
					if (s.getParentid() == 0) {
						ct = recursionFn(entry.getValue(), s, code);
						tree.add(ct);
					}
				}
			}
			return tree;
		}
		return null;
	}

	// 确认选择
	@Override
	public void confirmUUId(String param, String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(param);
		String prodcode = "", str = null,uuid = "",orispeccode = "";
		if ("MRPOnhandThrow".equals(caller) && map.get("mdd_prodcode") != null) {
			prodcode = map.get("mdd_prodcode").toString();
			uuid = map.get("uuid").toString();
			orispeccode = map.get("orispeccode").toString();
		} else if ("ProductBatchUUId".equals(caller) && map.get("pub_prodcode") != null) {
			prodcode = map.get("pub_prodcode").toString();
			uuid = map.get("pub_uuid").toString();
			orispeccode = map.get("pub_orispeccode").toString();
		}
		
		if (!prodcode.equals("")) {
			// 判断物料是否已经维护了原厂型号
			Object[]  obs = baseDao.getFieldsDataByCondition("product", new String[] {"pr_id","pr_unit","pr_uuid"}, "pr_code='" + prodcode + "'");
			if (obs == null) {
				BaseUtil.showError("物料编号[" +prodcode+ "]不存在");
				if(obs[2] != null && obs[2] != ""){
					BaseUtil.showError("物料编号[" +prodcode+ "]已经维护了标准料号:"+obs[2]);
				}
			}
			List<String> sqls = new ArrayList<String>();
			if ("MRPOnhandThrow".equals(caller)) {
				sqls.add("update mrpdataonhanddeal set mdd_uuid='" +uuid + "' where mdd_id='" + map.get("mdd_id") + "'");					
			} else {
				// 判断是否有上架，有采购单，
				SqlRowList rs = baseDao.queryForRowSet("select count(1) cn from ProductUUIDBatch left join goodsdetail on pub_uuid=gd_uuid and pub_prodcode=gd_prodcode where pub_id=? and gd_qty>0 and gd_sendstatus='已上传'",
								 map.get("pub_id"));
				if (rs.next() && rs.getInt("cn")> 0) {
					BaseUtil.showError("存在有效的上架单，不允许重新选择标准料号，料号[" + prodcode + "]");
				}
				rs = baseDao.queryForRowSet
								("select count(1) cn from ProductUUIDBatch left join B2C$PURCHASEBATCH on pb_uuid=pub_uuid and pb_prodcode=pub_prodcode left join purchasedetail on pd_puid=pb_puid and pub_prodcode=pd_prodcode left join purchase on pu_id=pb_puid where pub_id=? and pu_statuscode='AUDITED' ",
								   map.get("pub_id"));
				if (rs.next() && rs.getInt("cn")> 0) {
					BaseUtil.showError("存在已审核的采购单，不允许重新选择标准料号，料号[" + str + "]");
				}
				
				sqls.add("delete from B2C$GOODSBATCH WHERE exists(select 1 from ProductUUIDBatch where pub_uuid=gb_uuid and pub_id='"
						+ map.get("pub_id") + "')");
				sqls.add("delete from B2C$GOODSONHAND WHERE go_prodcode='"+prodcode+"'");
				
				sqls.add("update ProductUUIDBatch set pub_uuid='" + uuid+ "' ,pub_orispeccode='"
						+ map.get("pub_orispeccode") + "' where pub_id='" + map.get("pub_id") + "'");
			}
			sqls.add("update Product set pr_uuid='"+uuid+"',pr_orispeccode='" +orispeccode+ "' where pr_code='" + prodcode + "'");
			String erpunit = getUASUnit(String.valueOf(map.get("unit")), obs[1].toString());
			//自动产生平台物料信息表B2C$GOODSONHAND
			sqls.add("insert into b2c$goodsonhand(go_uuid,go_id,go_unit,go_erpunit,go_prodcode,go_code) select '"+uuid+"',b2c$goodsonhand_seq.nextval,'"+map.get("unit")+"','"+erpunit+"','"+prodcode+"','"+orispeccode+"' from dual where not exists (select 1 from b2c$goodsonhand where go_prodcode='"+prodcode+"' or go_uuid='"+uuid+"')");
			baseDao.execute(sqls);
		}
	}

	//匹配获取UAS 中的go_erpunit
	@Override
	public String getUASUnit(String unit,String erpunit){
		/**
		 * 1、标准单位是pcs的，lower(pr_unit) like '%k%' or pr_unit like '%千%' ，
		 * 则erpunit赋值为kpcs，否则赋值go_unit；
		 *2、标准单位是g的，lower(pr_unit) in('千克'、'公斤'，'kg')，
		 * 则erpunit赋值为kg，否则为go_unit；
		 */
		if(unit == null){
			unit = "PCS";
		}
		if(erpunit == null){
			erpunit = unit;
		}
		String unitn = unit.toLowerCase();
		String erpunitn = erpunit.toLowerCase();
		if(unitn.contains("pcs")){
			if(erpunitn.contains("k") || erpunitn.contains("千")){
				return "KPCS";
			}
		}else if(unitn.equals("g")){
			if(erpunitn.equals("千克") || erpunitn.equals("公斤")|| erpunitn.equals("kg")){
				return "KG";
			}
		}
		return unit;
	}

	//上架单位和标准单位比率
	public double getUnitRate(String erpunit,String unit){
		if(unit != null && erpunit != null){
			unit = unit.toLowerCase();
			erpunit = erpunit.toLowerCase();
			if((erpunit.equals("kpcs") && unit.equals("pcs"))
					|| (erpunit.equals("kg") && unit.equals("g"))){//*1000
				return 1000;
			}else if((erpunit.equals("pcs") && unit.equals("kpcs")) // 除1000
				 || (erpunit.equals("g") && unit.equals("kg"))){
				return 0.001;
			}
		}
		return 1;
	}
	
	/**
	 * 根据原厂型号批量匹配 标准料号
	 * @param str  原厂型号，根据逗号分隔
	 * @param code pub_code
	 */
	public void batchGetByOriCode(List<String> oriCodes,String code) {
		List<ComponentInfoUas> componentInfoUas = b2cComponentService.findByCode(oriCodes,SystemSession.getUser().getCurrentMaster());
		if (!CollectionUtil.isEmpty(componentInfoUas)) {
			// 按照分组code 进行分组
			Map<Object, List<ComponentInfoUas>> set = new HashMap<Object, List<ComponentInfoUas>>();
			List<ComponentInfoUas> list = null;
			for (ComponentInfoUas componentInfo : componentInfoUas) {
				String key = componentInfo.getCode();
				if (StringUtil.hasText(key) && set.containsKey(key)) {
					list = set.get(key);
				} else {
					list = new ArrayList<ComponentInfoUas>();
				}
				list.add(componentInfo);
				set.put(key, list);
			}
			String uuid = "";
			for (Map.Entry<Object, List<ComponentInfoUas>> entry : set.entrySet()) {
				if (entry.getValue().size() > 1) {// 一个原厂型号多个uuid
					String uuidStr = CollectionUtil.getParamString(entry.getValue(), "uuid", ",");
					baseDao.execute("update productuuidBatch set pub_uuidstr='" + uuidStr + "' where pub_code='"+code+"' and pub_orispeccode='" + entry.getKey() + "'");
				} else {
					List<String> sqls = new ArrayList<String>();
					SqlRowList rs = baseDao.queryForRowSet("select pr_id,pr_orispeccode,pr_unit,pr_code from productuuidBatch left join product on pr_code=pub_prodcode where pub_code=? and pr_orispeccode=? ",code,entry.getKey());
					while (rs.next()){
						uuid = entry.getValue().get(0).getUuid();
						sqls.add("update Product set pr_uuid='"+uuid+"',pr_orispeccode='" +entry.getKey()+ "' where pr_code='" +rs.getString("pr_code")+ "'");
						sqls.add("update productuuidBatch set pub_uuid='" +uuid+ "' where pub_code='"+code+"'"
								+ "and pub_orispeccode='" + entry.getKey()+ "'");
						String unit = entry.getValue().get(0).getUnit();
						String erpunit = getUASUnit(unit,rs.getString("pr_unit"));
						//自动产生平台物料信息表B2C$GOODSONHAND
						sqls.add("insert into b2c$goodsonhand(go_uuid,go_id,go_unit,go_erpunit,go_prodcode,go_code) select '"+uuid+"',b2c$goodsonhand_seq.nextval,'"+unit+"','"+erpunit+"','"+rs.getString("pr_code")+"','"+entry.getValue().get(0).getCode()+"' from dual where not exists (select 1 from b2c$goodsonhand where go_prodcode='"+rs.getString("pr_code")+"')");
					}					
					baseDao.execute(sqls);
				}
			}
		}
	}

	private Map<Object, Object> recursionFn(List<KindUas> kindUas, KindUas kind, String code) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("text", kind.getNameCn());
		map.put("id", kind.getId());
		map.put("count", kind.getCount());
		map.put("level", kind.getLevel());
		map.put("parentid", kind.getParentid());
		map.put("nameEn", kind.getNameEn());
		map.put("detno", kind.getDetno());
		map.put("isLeaf", kind.getIsLeaf());
		if (kind.getIsLeaf() == 1) {
			map.put("leaf", true);
			map.put("expandable", false);
			map.put("cls", "x-tree-cls-node");
			map.put("children", "");
			map.put("orispecode", code);
		} else {
			map.put("leaf", false);
			map.put("expandable", true);
			if (kind.getParentid() == 0) {
				map.put("cls", "x-tree-cls-root");
			} else {
				map.put("cls", "x-tree-cls-parent");
			}
			List<KindUas> childList = getChildList(kindUas, kind);
			Iterator<KindUas> it = childList.iterator();
			List<Map<Object, Object>> children = new ArrayList<Map<Object, Object>>();
			Map<Object, Object> ct = new HashMap<Object, Object>();
			while (it.hasNext()) {
				KindUas n = (KindUas) it.next();
				ct = recursionFn(kindUas, n, code);
				children.add(ct);
			}
			map.put("children", children);
		}
		return map;
	}

	// 获取子节点列表
	private List<KindUas> getChildList(List<KindUas> kindUas, KindUas kind) {
		List<KindUas> li = new ArrayList<KindUas>();
		Iterator<KindUas> it = kindUas.iterator();
		while (it.hasNext()) {
			KindUas n = (KindUas) it.next();
			// 父id等于id时 有子节点 添加该条数据
			if (n.getParentid().longValue() == kind.getId().longValue()) {
				li.add(n);
			}
		}
		return li;
	}

}
