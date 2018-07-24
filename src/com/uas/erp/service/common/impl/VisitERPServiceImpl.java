package com.uas.erp.service.common.impl;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.CurNavigation;
import com.uas.erp.model.CurNavigationTree;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.common.VisitERPService;

@Service("visitERPService")
public class VisitERPServiceImpl implements VisitERPService {
	
	private final static String REG_D = "\\d{4}-\\d{2}-\\d{2}";
	private final static String REG_DT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	private final static String REG_TS = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{1}";
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private HandlerService handlerService;
	
	public List<CurNavigation> getActorNavigation(String type) {
		List<CurNavigation> navigations=baseDao.getJdbcTemplate().query(
				" select * from (select * from CURNAVIGATION  where cn_id in (select cn_subof from CURNAVIGATION left join rolepermission on cn_id = rp_cnid where rp_code = '"+type+"') " +
				" union " +
				" select * from CURNAVIGATION where cn_id in (select rp_cnid from rolepermission where rp_code = '"+type+"'))ORDER BY CN_SUBOF,CN_DETNO ", 
				new BeanPropertyRowMapper<CurNavigation>(CurNavigation.class));
		List<CurNavigation> alreadyNavs=new ArrayList<CurNavigation>();
		for(CurNavigation nav:navigations){
			if(nav.getCn_subof()==0){
				formatNavigations(navigations,nav);
				alreadyNavs.add(nav);
			}
			
		}
		return alreadyNavs;
	}
    private void formatNavigations(List<CurNavigation> navigations,CurNavigation nav){
    	/**
    	 * 客户服务模块只考虑两层结构*/
    	List<CurNavigation> children=new ArrayList<CurNavigation>();
    	for(CurNavigation n:navigations){
    		if(n.getCn_subof()==nav.getCn_id()){
    			children.add(n);
    	    }
        }
    	nav.setChildren(children);
    }
	@Override
	public void updateVAM(String formStore, String gridStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler("VendorAccountMaintain", "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
		baseDao.execute(formSql);
		int UU = Integer.valueOf(String.valueOf(store.get("cu_uu")));
		//修改VendorContrast
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "VendorContrast", "vc_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("vc_id") == null || s.get("vc_id").equals("") || s.get("vc_id").equals("0") ||
					Integer.parseInt(s.get("vc_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("VENDORCONTRAST_SEQ");
				s.put("vc_cuid",  store.get("cu_id"));
				s.put("vc_uu", UU);
				String sql = SqlUtil.getInsertSqlByMap(s, "VendorContrast", new String[]{"vc_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("VendorAccountMaintain", "cu_id", store.get("cu_id"));
		//执行修改后的其它逻辑
		handlerService.handler("VendorAccountMaintain", "save", "after", new Object[]{store, gstore});
	}

	/**
	 * 通过VendorId获取clientContrast表中配置的用户名密码
	 * @param vendorId
	 * @return
	 */
	public Map<String, Object> getNameAndPwd(String vendorId){
		Map<String, Object> map = new HashMap<String, Object>();
		String emCode = SystemSession.getUser().getEm_code();
		List<Map<String, Object>> resultList = baseDao.queryForList("select cc_master,cc_username,cc_password "
				+ "from clientContrast where cc_veid = " + vendorId + " and cc_clientCode = '" + emCode + "'");
		if(resultList.size() == 1){
			map.put("success", true);
			map.put("data", resultList.get(0));
		}else{
			map.put("success", false);
		}
		map.put("ve_uu", baseDao.getFieldDataByCondition("ENTERPRISE", "EN_UU", "1=1"));
		return map;
	}
	
	@Override
	public void updateCAM(String formStore, String gridStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler("CustomerAccountMaintain", "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vendor", "ve_id");
		baseDao.execute(formSql);
		//修改VendorContrast
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ClientContrast", "cc_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("cc_id") == null || s.get("cc_id").equals("") || s.get("cc_id").equals("0") ||
					Integer.parseInt(s.get("cc_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("CLIENTCONTRAST_SEQ");
				s.put("cc_veid",  store.get("ve_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ClientContrast", new String[]{"cc_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("CustomerAccountMaintain", "ve_id", store.get("ve_id"));
		//执行修改后的其它逻辑
		handlerService.handler("CustomerAccountMaintain", "save", "after", new Object[]{store, gstore});
	}
	
	@Override
	public void updateAM(String formStore, String gridStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler("ActorMaintain", "save", "before", new Object[]{store, gstore});
		//修改类型
		String type = String.valueOf(store.get("type"));
		//修改VendorContrast
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "RolePermission", "rp_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rp_id") == null || s.get("rp_id").equals("") || s.get("rp_id").equals("0") ||
					Integer.parseInt(s.get("rp_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ROLEPERMISSION_SEQ");
				s.put("rp_code",  type);
				String sql = SqlUtil.getInsertSqlByMap(s, "RolePermission", new String[]{"rp_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("ActorMaintain", "rp_id", store.get("rp_id"));
		//执行修改后的其它逻辑
		handlerService.handler("ActorMaintain", "save", "after", new Object[]{store, gstore});
	}
	
	/**
	 * 获取账套名和公司名称
	 * @param master
	 * @return
	 */
	public Map<String, Object> getMasterAndEntpName(String master){
		Map<String, Object> map = new HashMap<String, Object>();
		String masterName = String.valueOf(baseDao.getFieldDataByCondition("ENTERPRISE", "EN_SHORTNAME", "1=1"));
		String enterpriseName = String.valueOf(baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1"));
		map.put("masterName", masterName);
		map.put("enterpriseName", enterpriseName);
		return map;
	}
	
	/**
	 * 校验账号密码以及accesskey
	 * @param username		用户名
	 * @param password		密码
	 * @param cu_uu			企业UU
	 * @param accesskey		accesskey
	 * @return
	 */
	public Map<String, Object> validCustomer(String username, String password,String cu_uu, String accesskey){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("cu_uu", cu_uu);
		String cu_accesskey = String.valueOf(baseDao.getFieldDataByCondition("customer", "cu_accesskey", "cu_uu=" + cu_uu));
		if(accesskey.equals(cu_accesskey)){
			List<Map<String, Object>> accountList = baseDao.queryForList("select * from vendorcontrast where vc_uu=" + cu_uu);
			for(Map<String, Object> map : accountList){
				if(map.get("VC_USERNAME").equals(username) && map.get("VC_PASSWORD").equals(password)){
					resultMap.put("valid", "true");
					resultMap.put("role", map.get("VC_ROLECODE"));
					resultMap.put("vcUU", map.get("VC_UU"));
					return resultMap;
				}
			}
			resultMap.put("error", "账号密码不正确!");
			resultMap.put("valid", "false");
			resultMap.put("accesskey", accesskey);
			return resultMap;
		}else{
			resultMap.put("valid", "false");
		}
		return resultMap;
	}
	
	public Set<Map<String, Object>> getGridStore(String caller, String bomid, String cu_uu){
		//通过UU获取客户信息
		Object[] customer = baseDao.getFieldsDataByCondition("customer", "cu_code,cu_name,cu_id", "cu_uu="+cu_uu);
		String sql = "select A.pc_custprodcode \"pc_custprodcode\",C.pr_code \"pc_prodcode\",C.pr_detail \"pc_proddetail\",C.pr_spec \"pc_prodspec\",D.pr_custproddetail \"pc_custproddetail\",D.pr_custprodspec \"pc_custprodspec\", "
				+ "D.pr_custprodorispeccode \"pc_custprodorispeccode\",D.pr_custprodbrand \"pc_custprodbrand\",D.pr_custprodkind \"pc_custprodkind\",D.pr_custprodkind2 \"pc_custprodkind2\",D.pr_custprodkind3 \"pc_custprodkind3\",D.pr_custprodzxbzs \"pc_custprodzxbzs\", "
				+ "D.pr_manutype \"pr_manutype\",D.pr_supplytype \"pr_supplytype\",D.pr_dhzc \"pr_dhzc\",D.pr_jitype \"pr_jitype\",D.pr_unit \"pr_unit\" from "
				+ "(select bo_mothercode pc_custprodcode,bo_proddetail pc_custproddetail,bo_prodspec pc_custprodspec from bomtemplate where bo_id="+bomid+" "
				+ "union all "
				+ "select bd_soncode pc_custprodcode,bd_proddetail pc_custproddetail,bd_prodspec pc_custprodspec from bomtemplate left join bomdetailtemplate on bo_id=bd_bomid where bo_id="+bomid+" "
				+ "union all "
				+ "select pre_repcode pc_custprodcode, pre_prodname pc_custproddetail,pre_prodspec pc_custprodspec from prodreplacetemplate left join bomdetailtemplate on bd_id=pre_bdid left join bomtemplate on bd_bomid=bo_id where bo_id="+bomid+") A "
				+ "left join productcustomer B on A.pc_custprodcode = B.pc_custprodcode left join Product C on B.pc_prodcode=C.pr_code "
				+ "left join productTemplate D on D.pr_custprodCode = A.pc_custprodCode where D.pr_custcode='"+customer[0]+"' order by pc_prodcode desc";
		List<Map<String, Object>> list = baseDao.queryForList(sql);
		Set<Map<String, Object>> set = new HashSet<Map<String,Object>>(list);
		int i = 1;
		for(Map<String, Object> map : set){
			map.put("pc_detno", i);
			map.put("pc_custcode", customer[0]);
			map.put("pc_custname", customer[1]);
			map.put("pc_custid", customer[2]);
			i++;
		}
		return set;
	}
	
	/**
	 * 保存grid数据到客户物料资料对照表中
	 * @param data
	 */
	public void SaveGridStore(String data){
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		StringBuilder executeSql = new StringBuilder("begin ");
		for(Map<Object, Object> map : gridStore){
			String countSql = "select count(*) from productcustomer where pc_custcode='"+map.get("pc_custcode")+"' and pc_custprodcode='"+map.get("pc_custprodcode")+"' and pc_prodcode is not null";
			int count = baseDao.getCount(countSql);
			if(count > 0){
				StringBuilder sb = new StringBuilder("update productcustomer set ");
				Set<Object> keys = map.keySet();
				for(Object key : keys){
					if(map.get(key) != null){
						sb.append(key + "='" + map.get(key) + "',");
					}
				}
				sb.deleteCharAt(sb.length()-1).append(" where pc_custcode='"+map.get("pc_custcode")+"' and pc_custprodcode='"+map.get("pc_custprodcode")+"'");
				executeSql.append(sb + ";");
			}else{
				String insertSql = SqlUtil.getInsertSql(map, "productcustomer","pc_id");
				executeSql.append(insertSql + ";");
			}
		}
		executeSql.append(" end;");
		baseDao.execute(executeSql.toString());
	}
	
	/**
	 * 转正式BOM
	 * @param formStore
	 * @param gridStore
	 * @return
	 */
	@Transactional
	public Map<String, Object> TrunFormal(int bomId, String formStore, String gridStore){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String motherCode = String.valueOf(store.get("bo_mothercode"));
		Object customer = baseDao.getFieldDataByCondition("customer", "cu_code", "cu_uu="+store.get("cu_uu"));
		//motherCode
		Object prodCode = baseDao.getFieldDataByCondition("productcustomer", "pc_prodcode", "pc_custprodcode='"+motherCode+"' and pc_custcode='"+String.valueOf(customer)+"'");
		int count = baseDao.getCount("select count(*) from bom where bo_motherCode = '" + prodCode + "' and (bo_status = '已提交' or bo_status = '在录入')");
		if(count > 0){
			map.put("success", "false");
			map.put("message", "母件"+prodCode+"已存在且未审核!");
			return map;
		}
		count = baseDao.getCount("select count(*) from bom where bo_motherCode='"+prodCode+"' and nvl(bo_status,' ')<>'已提交' and nvl(bo_status,' ')<>'在录入'");
		if(count > 0){
			//执行ECN操作
			Object executeStatus = baseDao.getFieldDataByCondition("bomtemplate", "bo_executestatus", "bo_id="+bomId);
			if("待执行".equals(executeStatus)){
				map.put("message", "已存在待执行的ECN!");
				return map;
			}
			String message = baseDao.callProcedure("SP_ECN_SYNC", motherCode, SystemSession.getUser().getEm_code(),store.get("cu_uu"));
			String[] msgArray = message.split("#");
			if("true".equals(msgArray[0])){
				Object ecn_id = baseDao.getFieldDataByCondition("ECN", "ECN_ID", "ECN_CODE='"+msgArray[1]+"'"); 
				map.put("success", "true");
				map.put("message", "转ECN成功!,ECN单号：<a href=\"javascript:openUrl('jsps/pm/bom/ECN.jsp?formCondition=ecn_idIS"+ecn_id+"&gridCondition=ed_ecnidIS"+ecn_id+"')\">"+ msgArray[1]+"</a>&nbsp;");
				baseDao.execute("update bomtemplate set bo_executestatus='待执行' where bo_id="+bomId);
			}else{
				map.put("success", msgArray[0]);
				map.put("message", msgArray[1]);
			}
			return map;
		}
		//form
		int bomid = baseDao.getSeqId("BOM_SEQ");
		baseDao.execute(getInsertSqlByForm(bomid, store, String.valueOf(customer)));
		int oldBomid=Integer.parseInt(String.valueOf(store.get("bo_id")));
		String sql = "update bom set bo_recorder='"+SystemSession.getUser().getEm_name()+"',bo_mothercode= (select pc_prodcode from productcustomer left join customer on productcustomer.pc_custcode = customer.cu_code left join bomtemplate on customer.cu_uu=bomtemplate.cu_uu where bomtemplate.bo_id="+store.get("bo_id")+" and productcustomer.pc_custprodcode='"+store.get("bo_mothercode")+"') where bo_id="+bomid;
		baseDao.execute(sql);
		//grid
		if(grid.size() > 0){
			baseDao.execute(getInsertSqlByGridStore(bomid, grid, oldBomid, String.valueOf(customer)));
		}
		//prodreplace
		List<Map<String, Object>> prodReplaceList = baseDao.queryForList("select * from prodreplacetemplate where pre_bomid=" + bomId);
		if(prodReplaceList.size() != 0){
			StringBuilder sb = new StringBuilder("begin ");
			for(Map<String, Object> prodReplaceMap : prodReplaceList){
				prodReplaceMap.put("PRE_BOMID",bomid);
				prodReplaceMap.put("PRE_ID", baseDao.getSeqId("prodreplace_seq"));
				if(prodReplaceMap.get("PRE_REPCODE") != null){
					String prodCodeSql = "select pc_prodcode from productcustomer left join customer on productcustomer.pc_custcode = customer.cu_code left join bomtemplate on customer.cu_uu=bomtemplate.cu_uu where bomtemplate.bo_id="+oldBomid+" and productcustomer.pc_custprodcode='"+prodReplaceMap.get("PRE_REPCODE")+"'";
					Map<String, Object> prodCodeMap = baseDao.getJdbcTemplate().queryForMap(prodCodeSql);
					prodReplaceMap.put("PRE_REPCODE", prodCodeMap.get("PC_PRODCODE"));
				}
				prodReplaceMap.remove("CU_UU");
				sb.append(SqlUtil.getInsertSqlByMap(prodReplaceMap, "prodreplace")+ ";");
			}
			sb.append(" end;");
			baseDao.execute(sb.toString());
			//更新prodreplace 的 pre_bdid
			String updateSql = "update prodreplace set pre_bdid=(select bd_id from bomdetail where bd_tmpid=pre_bdid) where pre_bdid in (select bd_tmpid from prodreplace inner join bomdetail on pre_bdid=bd_tmpid where pre_bomid="+bomid+" group by bd_tmpid) and pre_bomid="+bomid;
			baseDao.execute(updateSql);
		}
		
		map.put("success", "true");
		map.put("message", "转正式成功!,母件编号：<a href=\"javascript:openUrl('jsps/pm/bom/BOM.jsp?formCondition=bo_idIS"+bomid+"&gridCondition=bd_bomidIS"+bomid+"')\">"+ prodCode+"</a>&nbsp;");
		//插入的母件编号
		map.put("mothercode", prodCode);
		return map;
	}
	
	/**
	 * 转临时BOM、BOMDETAIL、PRODREPLACE
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public boolean TurnTemplate(HttpServletRequest request, String key, String data, String cu_uu, String type){	//type=0表示第一次,type=1表示第二次
		//校验(key, cu_uu)
		Object accessKey = baseDao.getFieldDataByCondition("customer", "cu_accesskey", "cu_uu=" + cu_uu);
		if(key.equals(accessKey)){
			Object[] custInfo = baseDao.getFieldsDataByCondition("customer", "cu_code,cu_name", "cu_uu="+cu_uu);
			Object custCode = custInfo[0]; Object custName = custInfo[1];
			//遍历Data
			List<Map<String, Object>> dataList = JSONObject.parseObject(data, ArrayList.class);
			String batchCode = custCode + "-" + baseDao.getSeqId("BomTemplate_SEQ");	//批次号：客户编号+序列号
			for(Map<String, Object> map : dataList){
				List<Map<String,Object>> bomList = (List<Map<String, Object>>) map.get("BOM");
				List<Map<String,Object>> bomDetailList = (List<Map<String, Object>>) map.get("BOMDETAIL");
				List<Map<String,Object>> prodReplaceList = (List<Map<String, Object>>) map.get("PRODREPLACE");
				List<Map<String,Object>> productList = (List<Map<String, Object>>) map.get("PRODUCT"); 
				//插入BOM
				if("1".equals(type)){
					//禁用之前的bom、bomdetail、prodreplace   update
					//PRODREPLACETEMPLATE
					baseDao.execute("update prodreplacetemplate set pre_invalidstatus = '已作废' where cu_uu="+cu_uu+" and pre_bomid=(select bo_id from bomtemplate where cu_uu="+cu_uu+" and bo_mothercode='"+bomList.get(0).get("BO_MOTHERCODE")+"' and bo_invalidstatus='生效')");
					//BOMTEMPLATE
					baseDao.execute("update bomtemplate set bo_invalidstatus = '已作废' where cu_uu="+cu_uu+" and bo_mothercode='"+bomList.get(0).get("BO_MOTHERCODE")+"' and bo_invalidstatus='生效'");
					//BOMDETAILTEMPLATE
					baseDao.execute("update bomdetailtemplate set bd_invalidstatus = '已作废' where cu_uu="+cu_uu+" and bd_mothercode='"+bomList.get(0).get("BO_MOTHERCODE")+"' and bd_invalidstatus='生效'");
				}
				//bo_id取序列的值
				int bo_id = baseDao.getSeqId("BomTemplate_SEQ");
				bomList.get(0).put("BO_ID", bo_id);
				bomList.get(0).put("BO_BATCHCODE", batchCode);
				baseDao.execute(getTempSqlByList(bomList, "BomTemplate", "BO_ID"));
				//插入BOMDETAILTEMPLATE
				if(bomDetailList.size() != 0){
					//修改bd_bomid和bd_id
					for(Map<String, Object> bomdetailMap : bomDetailList){
						bomdetailMap.put("BD_BOMID", bo_id);
						int bd_id = baseDao.getSeqId("BomdetailTemplate_seq");
						//更新prodReplaceList中的pre_bomid,pre_bdid,pre_id
						for(Map<String, Object> prodreplaceMap : prodReplaceList){
							if(bomdetailMap.get("BD_ID").equals(prodreplaceMap.get("PRE_BDID"))){
								prodreplaceMap.put("PRE_BDID", bd_id);
								prodreplaceMap.put("PRE_BOMID", bo_id);
								prodreplaceMap.put("PRE_ID", baseDao.getSeqId("prodreplacetemplate_seq"));
							}
						}
						bomdetailMap.put("BD_ID", bd_id);
					}
					baseDao.execute(getTempSqlByList(bomDetailList, "BomdetailTemplate", "BD_ID"));
				}
				//插入prodreplacetemplate
				if(prodReplaceList.size() != 0){
					baseDao.execute(getTempSqlByList(prodReplaceList, "prodreplacetemplate", "PRE_ID"));
				}
				
				int count = 0;
				for(Map<String, Object> prodMap : productList){
					//插入临时物料表
					 count = baseDao.getCount("select count(*) from productTemplate where pr_custcode='"+custCode+"' and pr_custprodCode='"+prodMap.get("PR_CODE")+"'");
					 if(count == 0){
						 String insertSql = "insert into productTemplate(pr_id,pr_custcode,pr_custprodcode,pr_custproddetail,pr_custprodspec,pr_custprodorispeccode,pr_custprodbrand,"
						 		+ "pr_custprodkind,pr_custprodkind2,pr_custprodkind3,pr_custprodzxbzs,pr_manutype,pr_supplytype,pr_dhzc,pr_jitype,pr_unit) "
						 		+ "values(productTemplate_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						 baseDao.execute(insertSql,custCode,prodMap.get("PR_CODE"),prodMap.get("PR_DETAIL"),prodMap.get("PR_SPEC"),prodMap.get("PR_ORISPECCODE"),prodMap.get("PR_BRAND"),
								 prodMap.get("PR_KIND"),prodMap.get("PR_KIND2"),prodMap.get("PR_KIND3"),prodMap.get("PR_ZXBZS"),prodMap.get("PR_MANUTYPE"),prodMap.get("PR_SUPPLYTYPE"),prodMap.get("PR_DHZC"),prodMap.get("PR_JITYPE"),prodMap.get("PR_UNIT"));
					 }
					 
				}
			}
				
		}else{
			return false;
		}
		return true;
	}
	
	/**
	 * 解决当data为空时，由于开启了事物导致的无法切账套问题。
	 */
	@SuppressWarnings("deprecation")
	public boolean specileTruenTemplate(HttpServletRequest request, String key, String data, String cu_uu, String type, String master){
		boolean flag = false;
		if(data == null){
			Map<String, String> paramMap = analysisParam(request);
			data = paramMap.get("data");
			key = paramMap.get("key");
			cu_uu = paramMap.get("cu_uu");
			type = paramMap.get("type");
			master = paramMap.get("master");
			SpObserver.putSp(master);
			flag = TurnTemplate(request, key, data, cu_uu, type);
			SpObserver.back();
		}else{
			try {
				flag = TurnTemplate(request, key, URLDecoder.decode(data,"UTF-8"), cu_uu, type);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	/**
	 * 若请求发送过来的参数过长，则采用流的方式读取
	 * @param request
	 * @return
	 */
	private Map<String, String> analysisParam(HttpServletRequest request){
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String data = null;
		List<String> array = new ArrayList<String>();
		try {
			BufferedReader reader = request.getReader();
			char[] buff = new char[1024 * 1024];
			int len;
			while((len = reader.read(buff)) != -1){
				sb.append(buff, 0 , len);
			}
			data = URLDecoder.decode(sb.toString(), "UTF-8");
			data = URLDecoder.decode(data,"UTF-8");
			array = Arrays.asList(data.split("&"));
			for(String element : array){
				int index = element.indexOf("=");
				map.put(element.substring(0,index), element.substring(index+1));
			}
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 校验是否可转正式BOM
	 */
	public boolean validConvertTurn(int bomid, int cu_uu){
		String countSql = "select count(*) from "
				+ "(select bo_mothercode pc_custprodcode,bo_proddetail pc_custproddetail,bo_prodspec pc_custprodspec from bomtemplate where bo_id="+bomid+" "
				+ "union all "
				+ "select bd_soncode pc_custprodcode,bd_proddetail pc_custproddetail,bd_prodspec pc_custprodspec from bomtemplate left join bomdetailtemplate on bo_id=bd_bomid where bo_id="+bomid+" "
				+ "union all "
				+ "select pre_repcode pc_custprodcode, pre_prodname pc_custproddetail,pre_prodspec pc_custprodspec from prodreplacetemplate left join bomdetailtemplate on bd_id=pre_bdid left join bomtemplate on bd_bomid=bo_id where bo_id="+bomid+") A "
				+ "left join productcustomer B on A.pc_custprodcode = B.pc_custprodcode left join Product C on B.pc_prodcode=C.pr_code where pc_prodcode is null or pc_prodcode <> ''";
		int count = baseDao.getCount(countSql);
		if(count > 0){
			return false;
		}else{
			Object accessKey = baseDao.getFieldDataByCondition("customer", "cu_accesskey", "cu_uu="+cu_uu);
			if(accessKey == null)
				return false;
			else
				return true;
		}
		
	}
	
	private String getTempSqlByList(List<Map<String, Object>> list,String table, String mainKey){
		String countSql = "select count(*) from "+table+" where " + mainKey + " = ";
		StringBuilder sb = new StringBuilder("begin ");
		for(Map<String, Object> map : list){
			int count = baseDao.getCount(countSql + map.get(mainKey) + " and cu_uu=" + map.get("CU_UU"));
			if(count > 0){
				//update
				sb.append("update "+ table + " set ");
				Set<String> keys = map.keySet();
				for(String key : keys){
					Object value = map.get(key);
					if(value != null){
						sb.append(key + " = ");
						String val = value.toString();
						if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
							sb.append(DateUtil.parseDateToOracleString(Constant.YMD, val) + ",");
						} else if (val.matches(REG_DT)) {
							sb.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val) + ",");
						} else if (val.matches(REG_TS)) {
							sb.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))) + ",");
						} else if (value.toString().equals("null")) {
							sb.append("null,");
						} else if (val.contains("'")) {
							sb.append("'" + value.toString().replaceAll("'", "''") + "'" + ",");
						} else if (val.contains("%n")) {
							sb.append("'" + val.replaceAll("%n", "\n") + "'" + ",");
						} else {
							sb.append("'" + value + "'" + ",");
						}
					}
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(" where " + mainKey + " = " + map.get(mainKey) + " and cu_uu = "+map.get("CU_UU")+"; ");
			}else{
				//insert
				sb.append(SqlUtil.getInsertSqlByMap(map, table) + "; ");
			}
		}
		sb.append(" end;");
		return sb.toString();
	}
	
	private String getInsertSqlByForm(int bomid, Map<Object, Object> map, String customerCode){
		Set<Object> keys = map.keySet();
		StringBuilder sb = new StringBuilder("insert into bom(bo_istemplate,bo_custcode,");
		StringBuilder sb2 = new StringBuilder(" values(1,'"+customerCode+"',");
		for(Object key : keys){
			if(!"cu_uu".equals(key)){
				sb.append(key+",");
				Object value = map.get(key);
				if(value != null){
					String val = value.toString();
					if("bo_id".equals(key)){
						sb2.append(bomid);
					}else if("bo_status".equals(key)){
						sb2.append("'在录入'");
					}else if("bo_statuscode".equals(key)){
						sb2.append("'ENTERING'");
					}else if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (val.equals("null")) {
						sb2.append("null");
					} else {
						if (val.contains("'")) {
							val = val.replaceAll("'", "''");
						} else if (val.contains("%n")) {
							val = val.replaceAll("%n", "\n");
						}
						// 针对较长字段，比如clob类型，防止ORA-01704的简单处理：切割成多个字符串连接起来
						if (val.length() > 2000) {
							sb2.append("''");
						} else if (val.length() > 1000) {
							sb2.append(StringUtil.splitAndConcat(val, 1333, "'", "'", "||"));
						} else {
							sb2.append("'" + val + "'");
						}
					}
				}else{
					sb2.append("null");
				}
				sb2.append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		sb2.deleteCharAt(sb2.length()-1);
		sb2.append(")");
		String formSql = sb.toString() + sb2.toString();
		return formSql;
	}
	
	private String getInsertSqlByGridStore(int bomId, List<Map<Object, Object>> grid, int oldBomid, String custCode){
		StringBuilder sb = new StringBuilder("begin ");
		for(Map<Object, Object> map : grid){
			//prodreplace
			/*List<Map<String, Object>> prodReplaceList = baseDao.queryForList("select * from prodreplacetemplate where pre_bomid=" + bomId);
			StringBuilder sb = new StringBuilder("begin ");
			for(Map<String, Object> prodReplaceMap : prodReplaceList){
				prodReplaceMap.put("PRE_BOMID",bomId);
				prodReplaceMap.put("PRE_ID", baseDao.getSeqId("prodreplace_seq"));
				prodReplaceMap.remove("CU_UU");
				sb.append(SqlUtil.getInsertSqlByMap(prodReplaceMap, "prodreplace")+ ";");
			}
			sb.append(" end;");
			baseDao.execute(sb.toString());*/
			
			StringBuilder sb1 = new StringBuilder(" insert into Bomdetail(bd_tmpid,");
			StringBuilder sb2 = new StringBuilder(" values("+map.get("bd_id")+",");
			Set<Object> keys = map.keySet();
			int bd_id = baseDao.getSeqId("bomdetail_seq");
			for(Object key : keys){
				sb1.append(key + ",");
				Object value = map.get(key);
				if(value != null){
					String val = value.toString();
					if("bd_id".equals(key)){
						sb2.append(bd_id);
					}else if("bd_bomid".equals(key)){
						sb2.append(bomId);
					}else if("bd_repcode".equals(key)){
						val = "'"+ val.replaceAll(",", "','") + "'";
						Object repcodes = baseDao.getFieldDataByCondition("productcustomer", "wm_concat(pc_prodcode)", "pc_custprodcode in ("+val+") and pc_custcode='"+custCode+"'");
						sb2.append("'"+String.valueOf(repcodes)+"'");
					}else if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (val.equals("null")) {
						sb2.append("null");
					} else {
						if (val.contains("'")) {
							val = val.replaceAll("'", "''");
						} else if (val.contains("%n")) {
							val = val.replaceAll("%n", "\n");
						}
						// 针对较长字段，比如clob类型，防止ORA-01704的简单处理：切割成多个字符串连接起来
						if (val.length() > 2000) {
							sb2.append("''");
						} else if (val.length() > 1000) {
							sb2.append(StringUtil.splitAndConcat(val, 1333, "'", "'", "||"));
						} else {
							sb2.append("'" + val + "'");
						}
					}
				}else{
					sb2.append("null");
				}
				sb2.append(",");
			}
			sb1.deleteCharAt(sb1.length()-1);
			sb1.append(")");
			sb2.deleteCharAt(sb2.length()-1);
			sb2.append(");");
			sb.append(sb1.toString() + sb2.toString());
			//更新子件编号
			String updateSql = "update bomdetail set bd_soncode= (select pc_prodcode from productcustomer left join customer on productcustomer.pc_custcode = customer.cu_code left join bomtemplate on customer.cu_uu=bomtemplate.cu_uu where bomtemplate.bo_id="+oldBomid+" and productcustomer.pc_custprodcode='"+map.get("bd_soncode")+"') where bd_id="+bd_id;
			sb.append(updateSql+";");
		}
		sb.append(" end;");
		return sb.toString();
	}
	
	/**
	 * BOM数据对接
	 * @return 
	 */
	public List<Map<String, Object>> BomSync(String pr_code,String bomid,String data,String type) throws InterruptedException{
		List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();
		//获得客户方的UU
		Object cu_uu = baseDao.getFieldDataByCondition("enterprise", "en_uu", "1=1");
		//同步
		resultMaps = Sync(pr_code,bomid,data,String.valueOf(cu_uu));
		return resultMaps;
	}
	
	
	/**
	 * 循环访问接口
	 * c username		用户名
	 * @param password		密码
	 * @param cu_uu			企业UU
	 * @param accesskey		accesskey
	 * @return
	 */
	private List<Map<String, Object>> Sync(String pr_code,String bomid,String data,final String cu_uu) throws InterruptedException{
		List<Map<String, Object>> bomids = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet("select bo_id,pr_specdescription from product left join bom on (pr_code=bo_mothercode or pr_refno=bo_mothercode) where"
						+ " pr_code='" + pr_code + "' and bo_id=" + bomid);
		if (rs.next()) {
			String res = baseDao.callProcedure("MM_SetProdBomStruct",
					new Object[] { rs.getInt("bo_id"), rs.getString("pr_specdescription") });
			if (res != null && res.length() > 0) {
				BaseUtil.showError(res);
			}
		} else {
			BaseUtil.showError("找不到指定的父件编号或BOMID.");
		}
		
		//根据bomid读取所有表数据
		bomids = baseDao.queryForList("select BS_SONBOMID from BOMSTRUCT where bs_topbomid = '"+bomid+"' and nvl(bs_sonbomid,0)<>0");
		final List<Map<String, Object>> boms = new ArrayList<Map<String, Object>>();
		String bomIdStr = "";
		for (Map<String, Object> map : bomids) {
			Map<String, Object> bomData = new HashMap<String,Object>();
			StringBuilder prodCodeStr = new StringBuilder();
			String bom_id = String.valueOf(map.get("BS_SONBOMID"));
			bomIdStr += bom_id+",";
			//bom表
			List<Map<String, Object>> BOM = new ArrayList<Map<String, Object>>();
			BOM = baseDao.queryForList("select BO_ATTACH,BO_PRINCIPLE,BO_ORDERQTY_USER,BO_SOFTVERSION,BO_REFNAME,BO_REFSPEC,BO_VALIDSTATUS,BO_VALIDSTATUSCODE,BO_REFBOMID,"
					+ "BO_ISEXTEND,BO_SYNC,BO_VERSIONID,BO_SMTPOINTS,BO_STATUSCODE,BO_ID,BO_MOTHERID,BO_MOTHERCODE,BO_VERSION,BO_FINERATE,BO_MANHOUR,BO_COSTPRICE,BO_QUOTPRICE,"
					+ "BO_DATE,BO_STATUS,BO_REMARK,BO_RECORDERID,BO_GROUPCODE,BO_GROUPNAME,BO_UPDATEMAN,BO_UPDATEDATE,BO_AUDITMAN,BO_AUDITDATE,BO_CUSTCODE,BO_CUSTNAME,BO_MANWORKFEE,"
					+ "BO_MANUFEE,BO_EXPMATERIALFEE,BO_EXPMANUFEE,BO_PRODNAME,BO_SPEC,BO_UNIT,BO_ERRQT,BO_ERRFULL,BO_ERRCRAFT,BO_PPH,BO_CRAFTVERSION,BO_SPECCODE,BO_STRUCTTIME,BO_STYLE,"
					+ "BO_RELATIVECODE,BO_EDITION,BO_CRAFTCODE,BO_RECORDER,BO_WORKCENTER,BO_COMMITMAN,BO_LEVEL,BO_STAGE,BO_STAGEREMARK,BO_DEVCODE,BO_VALIDFROMDATE,BO_VALIDTODATE,BO_MOTHERNAME,"
					+ "BO_CODE,BO_WCCODE,BO_WCNAME,BO_ISPAST,BO_FLOWSTYLE,BO_COP,BO_INITFINERATE,BO_INITREPRATE,BO_REPRATE,BO_REFCODE,'"+cu_uu+"' as cu_uu from bom where bo_id = '"+bom_id+"'");
			for (Map<String, Object> map2 : BOM) {
				prodCodeStr.append(String.valueOf(map2.get("BO_MOTHERCODE"))+",");
			}
			//bomdetail表
			List<Map<String, Object>> BOMDETAIL = new ArrayList<Map<String, Object>>();
			BOMDETAIL = baseDao.queryForList("select BD_LOSSQTY,BD_ORDERQTY_USER,BD_REMARK2,BD_ID,BD_BOMID,BD_SONBOMID,BD_MOTHERID,BD_DETNO,BD_SONID,BD_SONCODE,BD_BASEQTY,BD_LOSSRATE,BD_OLDCODESTR,"
					+ "BD_ACTQTY,BD_PRICE,BD_PERCENT,BD_WORKCENTERID,BD_LOCATION,BD_EDITDATE,BD_SUPPLYTYPE,BD_BUILDDATE,BD_VALIDFROMDATE,BD_VALIDTODATE,BD_ECNID,BD_REMARK,BD_QUOTQTY,BD_QUOTPRICE,"
					+ "BD_USESTATUSCODE,BD_USESTATUS,BD_TOTAL,BD_OFFSETDAY,BD_FINISHQTY,BD_DESCRIPTION,BD_BASEQTYBACK,BD_EFFECTBEGINDATE,BD_EFFECTOVERDATE,BD_RADIXPOINT,BD_PRPLACE,BD_IFREP,BD_UNIT,"
					+ "BD_REPLACE,BD_RECIPROCAL,BD_WCCODE,BD_ECNCODE,BD_MOTHERCODE,BD_WHCODE,BD_BUILDFINISHQTY,BD_MAKELOSSRATE,BD_LEADTIME,BD_ECNNO,BD_REPCODE,BD_PRSTATE,BD_SONSPEC,BD_SONNAM,BD_SONNAME,"
					+ "BD_STEPCODE,'"+cu_uu+"' as cu_uu,BD_ID as BD_TMPID from bomDetail where bd_bomid = '"+bom_id+"' and nvl(BD_USESTATUS,' ')<>'DISABLE'");
			for (Map<String, Object> map2 : BOMDETAIL) {
				prodCodeStr.append(String.valueOf(map2.get("BD_SONCODE"))+",");
			}
			//prodreplace表
			List<Map<String, Object>> PRODREPLACE = new ArrayList<Map<String, Object>>();
			PRODREPLACE = baseDao.queryForList("select PRE_SOURCECODE,PRE_PRINCIPLE,PRE_STATUSCODE,PRE_ID,PRE_DETNO,PRE_BOMID,PRE_PRODCODE,PRE_RATE,PRE_REPRATE,PRE_LEVEL,PRE_STATUS,PRE_REMARK,PRE_STARTDATE,"
					+ "PRE_ENDDATE,PRE_RULE,PRE_VALIDDATE,PRE_BASEQTY,PRE_REPBASEQTY,PRE_BDDETNO,PRE_ECNCODE,PRE_SONBOMID,PRE_SONCODEID,PRE_REPCODEID,PRE_SONCODE,PRE_REPCODE,PRE_TOTAL,PRE_PRODUNIT,PRE_PRODSPEC,"
					+ "PRE_PRODNAME,PRE_SUBITEMID,PRE_BDID,PRE_PURCPRICE,PRE_CURRENCY,PRE_PURCPRICERMB,PRE_VENDNAME,PRE_SOURCEPRICE,PRE_ITEMID,PRE_NEWSONCODE,PRE_OLDSONCODE,PRE_SUBBOMID,'"+cu_uu+"' as cu_uu from PRODREPLACE where pre_bomid = '"+bom_id+"' and nvl(pre_status,' ')<>'已禁用'");
			for (Map<String, Object> map2 : PRODREPLACE) {
				prodCodeStr.append(String.valueOf(map2.get("PRE_REPCODE"))+",");
			}
			
			String prodCodes = prodCodeStr.substring(0, prodCodeStr.length()-1).replaceAll(",", "','");
			//product表
			List<Map<String, Object>> PRODUCT = new ArrayList<Map<String, Object>>();
			PRODUCT = baseDao.queryForList("select PR_ISSALE,PR_ISPURCHASE,PR_ISSHOW,PR_ISPUBSALE,PR_LASTINQUIRYDATE,PR_PICCODE,PR_NEEDATTACH,PR_B2CSTORESTATUS,PR_B2CINITSTATUS,PR_B2CINITPRODDTSTATUS,"
					+ "PR_SQECODE,PR_SQENAME,PR_COMBINEQTY,PR_KCZZL,PR_B2CSENDSTATUS,PR_WIPMINSTOCK,PR_BZKCZZL,PR_PKID,PR_JTCYCLE,PR_JTINQUIRYDATE,PR_AUTOINQUIRYDAYS,PR_JTNEXTDATE,PR_DEFAULTUSED,PR_TARGETQTY,"
					+ "PR_TARGETPRICE,PR_AUTOSTART,PR_USEPRIORITY,PR_B2CSALEPRICE,PR_B2CBIRTHDATE,PR_ROHS,PR_ENGREMARK,PR_INSPECTORCODE,PR_TYPE,PR_DETAIL,PR_DESCRIPTION,PR_SPEC,PR_ABC,PR_UNIT,PR_KIND,PR_KIND2,"
					+ "PR_WHCODE,PR_BARCODE,PR_OLDCODE,PR_GROUP,PR_PROJECTCODE,PR_EXPORTLOSSRATE,PR_VENDCODE,PR_JYFA,PR_INDATE,PR_JITYPE,PR_AVPRICE,PR_MAKETYPE,PR_PLANERCODE,PR_STOCKCATENAME,PR_WCCODE,PR_LEVEL,"
					+ "PR_ADMITSTATUSCODE,PR_CHECKSTATUSCODE,PR_MATERIAL,PR_TRYDAYS,PR_ARKQTY,PR_KIND3,PR_KINDCODE,PR_BONDED,PR_SPECDESCRIPTION,PR_WCNAME,PR_SENDSTATUS,PR_ENID,PR_SERVICEFEE,PR_RECONHAND,PR_RESOURCECODE,"
					+ "PR_OLDNAME,PR_OLDSPEC,PR_PRJCODE,PR_PRJNAME,PR_COMMITED,PR_ONORDER,PR_INCOMECATENAME,PR_CUSTOMPRICE,PR_IFMAIN,PR_ISKEYPR,PR_STANDARDIZED,PR_DHJGT,PR_SIZE,PR_CAPACITY,PR_ORINAME,PR_ORISPECCODE,"
					+ "PR_ORISPEC,PR_BGCODE,PR_CRRECORD,PR_XIKIND,PR_PURRECORDER,PR_SPECCS,PR_COP,PR_CGGDY,PR_FLOWTYPE,PR_BGNAME,PR_CRMAN,PR_SPECS,PR_ATTACH,PR_SPECRULE,PR_SPECEG,PR_PARAMETERRULE,PR_PARAMETEREG,"
					+ "PR_AAA,PR_COSTTEMP,PR_AUDITDATE,PR_AUDITMAN,PR_WORKCENTER,PR_ZXBZS2,PR_WHNAME,PR_NEEDSTATUS,PR_NAMERULE,PR_NAMEEG,PR_TRACEKIND,PR_FEEDERSPEC,PR_EXBARCODE,PR_SYNC,PR_IFBARCODECHECK,PR_CGGDYCODE,"
					+ "PR_SPECDESCRIPTION2,PR_WIPLOCATION,PR_COEFFICIENT,PR_USETYPE,PR_ISMSD,PR_MSDLEVEL,PR_REMARK,PR_UUID,PR_UPDATEDATE,PR_UPDATEMAN,PR_SMTPOINT,PR_CHECKSTATUS,PR_SQR,PR_SQRQ,PR_RDBG,PR_STANDTIME,"
					+ "PR_CUSTPRODCODE,PR_MRPONHAND,PR_MRPONORDER,PR_MRPCOMMITED,PR_VENDORSTATUS,PR_PRICESTATUS,PR_PURCLOSSRATE,PR_BPLOSSRATE,PR_PRECISION,PR_TESTLOSSRATE,PR_AQL,PR_VERSION,PR_SOURCECODE,PR_LOWLEVELCODE,"
					+ "PR_ISVALID,PR_ISBATCH,PR_BATHTYPE,PR_ISSERIAL,PR_SERIALTYPE,PR_RECENTPURCDATE,PR_RECENTINDATE,PR_RECENTOUTDATE,PR_RECENTCHANGEDATE,PR_ISBONDED,PR_COSTLEVEL,PR_MACHINETIME,PR_MATERIALCOST,PR_HUMANCOST,"
					+ "PR_MAKECOST,PR_SALECOST,PR_COST,PR_ISVENDORRATE,PR_LTMRP,PR_ISGROUPPURC,PR_PURCCENTERCODE,PR_RECENTPURCPRICE,PR_AVPURCPRICE,PR_ISQUAL,PR_LTINSTOCK,PR_MANUFACTOR,PR_MAKELIMITED,PR_MAKELOSSRATE,PR_LTQC,"
					+ "PR_STOCKOUTLIMITED,PR_ISSTOCKIO,PR_ISTRACBEFORE,PR_GROUPCODE,PR_ID,PR_REMARK_SALE,PR_REMARK_PURCHASE,PR_REMARK_FINANCE,PR_REMARK_FEATURE,PR_REMARK_PLAN,PR_CODE,PR_STATUSCODE,PR_REMARK_WAREHOUSE,PR_WIPONHAND,"
					+ "PR_INSPECTEDONHAND,PR_DEFECTONHAND,PR_MAKEID,PR_TOTESTED,PR_GDTQQ,PR_PLZL,PR_ZXDHL,PR_DHZC,PR_SERIAL,PR_LOSSRATE,PR_REFNO,PR_FACTORYCODE,PR_CHECKMETHOD,PR_CHECKLEVEL,PR_GRADE,PR_FIRSTTYPE,PR_SECONDTYPE,PR_SELF,"
					+ "PR_COSTCATENAME,PR_SALECATENAME,PR_SALECATECODE,PR_CATENAME,PR_CATECODE,PR_ST,PR_COLOR,PR_JHYNAME,PR_JHY,PR_CGYNAME,PR_CGY,PR_SAFETYSTOCK,PR_MAXSTOCK,PR_LEADTIME,PR_QTYPERPLACE,PR_PURCUNIT,PR_PURCRATE,PR_SALEUNIT,"
					+ "PR_STANDARDPRICE,PR_REMARK_BASE,PR_MAINVENDCODE,PR_RECORDMAN,PR_LOCATION,PR_DOCDATE,PR_PURCCURRENCY,PR_PURCPRICE,PR_SALECURRENCY,PR_SALEPRICE,PR_STATUS,PR_ADMITSTATUS,PR_LENGTH,PR_WIDTH,PR_HEIGHT,PR_OUTERBOXLENGTH,"
					+ "PR_OUTERBOXWIDTH,PR_OUTERBOXHEIGHT,PR_OUTERBOXGW,PR_OUTERBOXNW,PR_OUTERBOXCBM,PR_OUTERBOXBARCODE,PR_VENDPRODCODE,PR_WEIGHT,PR_BRAND,PR_VENDNAME,PR_MINSTOCK,PR_VALIDDAYS,PR_BZDAYS,PR_CHECKDATE,PR_PRECHECKDATE,"
					+ "PR_INEXCEEDRATE,PR_INOWERATE,PR_OUTEXCEEDRATE,PR_OUTOWERATE,PR_STOCKCATECODE,PR_INCOMECATECODE,PR_COSTCATECODE,PR_PURCHASEPOLICY,PR_LEADTIMECHANGED,PR_PURCHASEDAYS,PR_PURCHASELIMIT,PR_PURCHASEMAX,PR_LEADTIMEQTY,"
					+ "PR_ECONOMICQTY,PR_PLANNER,PR_PUTOUTTOINT,PR_MRPMERGE,PR_QUALPURC,PR_QUALMAKE,PR_QUALSEND,PR_QUALRETU,PR_QUALSTOCK,PR_QUALSTOCKDAYS,PR_LTWARNDAYS,PR_QUALMETHOD,PR_INSPECTOR,PR_PICTCODE,PR_ENGLISHSPEC,PR_HSCODE,PR_HSUNIT,"
					+ "PR_JKGSL,PR_HSDWHSL,PR_SPECVALUE,PR_BOMID,PR_ASSUNIT,PR_ZXBZS,PR_MADEIN,PR_BGMC,PR_BUYERCODE,PR_BUYERNAME,PR_JHZC,PR_CRAFTID,PR_MNCODE,PR_MANUTYPE,PR_WHMANCODE,PR_WHMANNAME,PR_SUPPLYTYPE,PR_PURCMERGEDAYS,PR_ACCEPTMETHOD from PRODUCT where pr_code in ('"+prodCodes+"') and pr_status='已审核'");
			bomData.put("BOM", BOM);
			bomData.put("BOMDETAIL", BOMDETAIL);
			bomData.put("PRODREPLACE", PRODREPLACE);
			bomData.put("PRODUCT", PRODUCT);
			boms.add(bomData);
		}
		bomIdStr = bomIdStr.substring(0, bomIdStr.length()-1);
		//收集返回数据
		final List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		final CountDownLatch latch = new CountDownLatch(datas.size());
		for(int i=0; i< datas.size(); i++){
		  final Map<String, String> reqMap = new HashMap<String,String>();
		  final String url = String.valueOf(datas.get(i).get("ve_erplink")) + "/ERP/common/VisitERP/TurnTemplate.action";
		  final String key = String.valueOf(datas.get(i).get("ve_accesskey"));
		  final String ve_id = String.valueOf(datas.get(i).get("ve_id"));
		  final String ve_code = String.valueOf(datas.get(i).get("ve_code"));
		  final String ve_name = String.valueOf(datas.get(i).get("ve_name"));
		  final String vbm_id = String.valueOf(datas.get(i).get("vbm_id"));
		  final String master = String.valueOf(datas.get(i).get("master"));
		  final String type = String.valueOf(datas.get(i).get("type"));
		  final int x = i;
          new Thread(new Runnable() {
        	  @Override
          	  public void run() {
                  //循环bom请求
        		  Map<String, Object> data = new HashMap<String, Object>();
        		  reqMap.put("data", JSONUtils.toJSONString(boms));
        		  reqMap.put("master", master);
        		  reqMap.put("key", key);
        		  reqMap.put("cu_uu", cu_uu);
        		  reqMap.put("type", type);
        		  data.put("ve_id", ve_id);
        		  data.put("ve_code", ve_code);
        		  data.put("ve_name", ve_name);
        		  data.put("vbm_id", vbm_id);
            	  try {
						Response res = HttpUtil.sendPostRequest(url,reqMap);
						if(res.getResponseText().equals("true")){
							data.put("success", true);
						}else{
							data.put("success", false);
						}
						resMap.add(x, data);
					} catch (Exception e) {
						data.put("success", false);
						resMap.add(x, data);
						e.printStackTrace();
					}
                  latch.countDown();
              }
          }).start();
       }
	   try {
          latch.await();
          System.out.println("执行完成！");
       } catch (InterruptedException e) {
          e.printStackTrace();
       }
	   //更新供应商物料表
	   updateVendorBom(bomIdStr,resMap);
	   return resMap;
	}
	
	public void updateVendorBom(String bomIdStr,List<Map<String, Object>> results){
		StringBuffer sql = new StringBuffer();
		sql.append("begin ");
		for (Map<String, Object> map : results) {
			String sync = String.valueOf(map.get("success")).equals("true")?"已同步":"未同步";
			String[] ids = bomIdStr.split(",");
			for (String str : ids) {
				Object vbm_id = baseDao.getFieldDataByCondition("VENDORBOMMAPPING","vbm_id" ,"vbm_bomid = '"+str+"' and vbm_veid ='"+map.get("ve_id")+"'");
				if(String.valueOf(vbm_id).equals("null")||String.valueOf(vbm_id).length()<1){//新增
					sql.append("INSERT INTO VENDORBOMMAPPING values("
							+ "VENDORBOMMAPPING_SEQ.nextval,"
							+ "'"+str+"',"
							+ "'"+map.get("ve_id")+ "',"
							+ "'"+map.get("ve_code")+ "',"
							+ "'"+map.get("ve_name")+ "',"
							+ "'" + sync + "',null);");
				}else{//更新
					sql.append("UPDATE  VENDORBOMMAPPING SET VBM_SYNC = '"+sync+"' where vbm_id = "+ String.valueOf(vbm_id) + ";");
				}
			}
		}
		sql.append(" end;");
		baseDao.execute(sql.toString());
	}
	
	@Override
	public List<JSONTree> getCNTree(int parentId,String condition) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<CurNavigationTree> list = getCNTreeById(parentId, condition);
		for(CurNavigationTree f:list){
			tree.add(new JSONTree(f));
		}
		return tree;
	}
	
	public List<CurNavigationTree> getCNTreeById(int parentId, String condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from curnavigation where cn_subof=?");
		if(condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by cn_detno");
		try {
			List<CurNavigationTree> list = baseDao.getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<CurNavigationTree>(CurNavigationTree.class),parentId);
			return list;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<CurNavigationTree>();
		}
	}
	
	public void saveCurnavigation(String cn_title, String cn_url,String type){
		if(type.equals("Root")){
			Object detno = baseDao.getFieldDataByCondition("curnavigation", "max(cn_detno)", "cn_isleaf = 0");
			int num = 1;
			if(!String.valueOf(detno).equals("null")){
				num = Integer.valueOf(String.valueOf(detno)) + 1;
			}
			String sql = "insert into curnavigation values(CURNAVIGATION_SEQ.nextval,'"+cn_title+"',"
						 + "null,null,0,0,'"+num+"',null,null)";
			baseDao.execute(sql);
		}
	}
	
	public void updateCurnavigation(String cn_id,String cn_title, String cn_url,String type){
		cn_url = cn_url.replaceAll("'", "''");
		if(type.equals("add")){
			Object detno = baseDao.getFieldDataByCondition("curnavigation", "max(cn_detno)", "cn_subof = "+cn_id);
			int num = 1;
			if(!String.valueOf(detno).equals("null")){
				num = Integer.valueOf(String.valueOf(detno)) + 1;
			}
			String sql = "insert into curnavigation values(CURNAVIGATION_SEQ.nextval,'"+cn_title+"',"
					 + "'"+cn_url+"',"
					 + "'30702_b.gif',"+Integer.valueOf(cn_id)+",1,'"+num+"',null,null)";
			baseDao.execute(sql);
		}else if(type.equals("update")){
			String sql = "update curnavigation set cn_title = '"+cn_title+"',cn_url = '"+cn_url+"' where cn_id = '"+cn_id+"'";
			baseDao.execute(sql);
		}else if(type.equals("Root")){
			String sql = "update curnavigation set cn_title = '"+cn_title+"' where cn_id = '"+cn_id+"'";
			baseDao.execute(sql);
		}
	}
	
	public void deleteCurnavigation(String cn_id,String type){
		if(type.equals("leaf")){
			String sql = "delete from curnavigation where cn_id = "+cn_id;
			baseDao.execute(sql);
		}else if(type.equals("model")){
			String sql = "delete from curnavigation where cn_id = "+cn_id+" or cn_subof =" + cn_id;
			baseDao.execute(sql);
		}
	}
	
	public boolean BomEnable(String bo_mothercode,String ve_uu,String key,String master){
		Object[] veStr = baseDao.getFieldsDataByCondition("VENDOR", "ve_id,ve_accesskey", "ve_uu = '"+ve_uu+"'");
		if(key.equals(veStr[1])){
			StringBuffer sql = new StringBuffer();
			//根据bomid读取所有表数据
			List<Map<String, Object>> bomids = new ArrayList<Map<String, Object>>();
			Object bomid = baseDao.getFieldDataByCondition("BOM", "bo_id", "bo_mothercode='"+bo_mothercode+"'");
			bomids = baseDao.queryForList("select BS_SONBOMID from BOMSTRUCT where bs_topbomid = '"+bomid+"' and nvl(bs_sonbomid,0)<>0");
			String bomIdStr = "";
			for (Map<String, Object> map : bomids) {
				bomIdStr += String.valueOf(map.get("BS_SONBOMID"))+",";
			}
			bomIdStr = bomIdStr.substring(0, bomIdStr.length()-1);
			sql.append("begin ");
			String[] ids = bomIdStr.split(",");
			for (String str : ids) {
				sql.append("UPDATE  VENDORBOMMAPPING SET VBM_ENABLE = '已生效' where vbm_bomid = '"+ str + "' and vbm_veid = '"+String.valueOf(veStr[0])+"';");
			}
			sql.append(" end;");
			try {
				baseDao.execute(sql.toString());
				return true;
			} catch (Exception e) {
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void bomCustomersync(){
		String defaultSob = BaseUtil.getXmlSetting("defaultSob");
		Object configMaster = baseDao.getFieldDataByCondition(defaultSob+".sys_scheduletask", "MASTER_", "ENABLE_=-1 and CODE_='2018050001'");
		SpObserver.putSp(String.valueOf(configMaster));	//切换至定时任务配置的运行账套
		String sql = "select bo_id from bom where bo_istemplate = '1' and bo_status = '已审核'";
		List<Map<String, Object>> BomList = baseDao.queryForList(sql);
		for (Map<String, Object> map : BomList) {
			boolean enable = customersync(Integer.parseInt(String.valueOf(map.get("BO_ID"))));
			if(enable){
				String updateSql = "update bom set bo_istemplate = 2 where bo_id="+map.get("BO_ID");
				baseDao.execute(updateSql);
			}
		}
		//定时执行抛转过去的ECN
		sql="select ecn_code,ed_mothercode,ecn_uu from ecn left join  ecndetail on ecn_id =ed_ecnid where ecn_istemplate=1 and ecn_checkstatus='已审核' and nvl(ecn_didstatus,' ')='已执行'";
		List<Map<String, Object>> ECNList = baseDao.queryForList(sql);
		for(Map<String, Object> map : ECNList){
			Object pc_custcode = baseDao.getFieldDataByCondition("customer", "cu_code", "cu_uu='"+map.get("ecn_uu")+"'");
			Object pc_custprodcode = baseDao.getFieldDataByCondition("productcustomer", "pc_custprodcode", "pc_prodcode='"+map.get("ed_mothercode")+"' and pc_custcode='"+pc_custcode+"'");
			baseDao.updateByCondition("bomtemplate", "BO_EXECUTESTATUS='已执行'", "bo_mothercode='"+pc_custprodcode+"' and BO_INVALIDSTATUS='生效' and cu_uu='"+map.get("ecn_uu")+"'");
			baseDao.updateByCondition("ECN", "ecn_istemplate=2", "ecn_code='"+map.get("ECN_CODE")+"'");
		}
		SpObserver.back();		//切回之前的账套
	}
	
	/**
	 * BOM审核后，同步到客户方，让供应商物料关系表生效状态改变
	 */
	public boolean customersync(Integer id) {
		//新BOM母件编号、客户编号
		Object[] bomStr = baseDao.getFieldsDataByCondition("BOM", "bo_mothercode,bo_custcode", "bo_id = "+id);
		if(StringUtil.hasText(bomStr[0])&&StringUtil.hasText(bomStr[1])){
			//旧母件编号
			Object bo_mothercode = baseDao.getFieldDataByCondition("PRODUCTCUSTOMER", "PC_CUSTPRODCODE", "PC_CUSTCODE = '"+bomStr[1]+"' and PC_PRODCODE = '"+bomStr[0]+"'");
			if(StringUtil.hasText(bo_mothercode)){
				Object[] obj = baseDao.getFieldsDataByCondition("customer", "cu_uu,cu_erplink,cu_accesskey,cu_targetmaster", "cu_code = '"+bomStr[1]+"'");
				//对应的临时BOM的BOMID
				Object bomTemplateId = baseDao.getFieldDataByCondition("BOMTEMPLATE", "bo_id", "bo_mothercode = '"+bo_mothercode+"' and cu_uu = '"+obj[0]+"'");
				if(StringUtil.hasText(obj[0])&&StringUtil.hasText(obj[1])&&StringUtil.hasText(obj[2])&&StringUtil.hasText(obj[3])&&StringUtil.hasText(bomTemplateId)){
					//访问接口
					Object ve_uu = baseDao.getFieldDataByCondition("enterprise", "en_uu", "1=1");
					String url = String.valueOf(obj[1]) + "/ERP/common/VisitERP/BomEnable.action";
				    String key = String.valueOf(obj[2]);
				    String master = String.valueOf(obj[3]);
				    Map<String,String> map = new HashMap<String,String>();
				    map.put("key", key);
				    map.put("ve_uu", String.valueOf(ve_uu));
				    map.put("master", String.valueOf(master));
				    map.put("bo_mothercode", String.valueOf(bo_mothercode));
				    try {
						Response res = HttpUtil.sendPostRequest(url,map);
						if(res.getResponseText().equals("true")){
							return true;
						}else{
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 订单进度查看
	 * @param code	PO号
	 * @return
	 */
	public List<Map<String, Object>> getOrderProcess(String code){
		List<Map<String, Object>> result = initOrderData();
		boolean hasWorkList = false, hasSend = false, hasComplate = false;
		int n = 0;
		//1.待确认
		Object[] status = baseDao.getFieldsDataByCondition("SALEDOWN", "SA_STATUS,SA_RECORDDATE,SA_DATE", "SA_POCODE='"+code+"'"); 
		if(status == null){
			return result;
		}
		result.get(0).put("start", String.valueOf(status[1]).substring(0,10));
		result.get(0).put("remark", "PO已下未转正式");result.get(0).put("status", "running");
		//2.已接收
		if("已转销售".equals(status[0])){
			result.get(0).put("remark", "PO已下已转正式");result.get(0).put("status", "finish");
			result.get(0).put("end", String.valueOf(status[2]).substring(0,10));
			result.get(1).put("start", String.valueOf(status[1]).substring(0,10));result.get(1).put("end", String.valueOf(status[2]).substring(0,10));
			result.get(1).put("remark", "已转正式订单");result.get(1).put("status", "finish");
			Object saleCode = baseDao.getFieldDataByCondition("SALEDOWN LEFT JOIN (SELECT SA_ID AS SAID,SA_POCODE AS SAPOCODE,SA_CODE AS SACODE FROM SALE) S ON S.SAPOCODE = SA_POCODE LEFT JOIN SALEDETAIL ON S.SAID = SD_SAID LEFT JOIN PRODUCT ON SD_PRODCODE=PR_CODE LEFT JOIN PRODUCTWH ON PW_PRODCODE=SD_PRODCODE AND PW_WHCODE=PR_WHCODE", "SACODE", "SA_POCODE = '"+ code +"'");
			String sql = "SELECT MA_CODE FROM MAKE LEFT JOIN MpsMain ON MM_CODE = MA_MPSCODE LEFT JOIN MpsDetail ON MM_ID = MD_MAINID WHERE MA_SOURCE = 'MRP' AND MD_SOURCECODE = '"+saleCode+"'";
			List<Map<String, Object>> maCodeList = baseDao.queryForList(sql);
			//3.需求投放		
			String xqSql = "SELECT COUNT(*) FROM Application LEFT JOIN MPSMAIN ON MPSMAIN.MM_CODE=APPLICATION.AP_REFCODE LEFT JOIN MpsDetail ON MM_ID=MD_MAINID WHERE MPSDETAIL.MD_ORDERCODE='"+saleCode+"'";
			int xqCount = baseDao.getCount(xqSql);
			Object[] minTime = null;
			minTime = baseDao.getFieldsDataByCondition("Application LEFT JOIN MPSMAIN ON MPSMAIN.MM_CODE=APPLICATION.AP_REFCODE LEFT JOIN MpsDetail ON MM_ID=MD_MAINID", "MIN(AP_DATE),MAX(AP_DATE)", "MPSDETAIL.MD_ORDERCODE='"+saleCode+"'");
			if(xqCount > 0){
				result.get(2).put("remark", "需求部分投放");result.get(2).put("status", "running");
				result.get(2).put("start", String.valueOf(minTime[0]).substring(0,10));
			}
			
			//	(工单下达)
			if(!hasWorkList){
				hasWorkList = workList(result, maCodeList, String.valueOf(saleCode));
			}
			if(hasWorkList){
				if(!hasSend){
					hasSend = sendMaterial(result, maCodeList, String.valueOf(saleCode));
				}
				if(hasSend){
					if(!hasComplate){
						hasComplate = complete(result, maCodeList, String.valueOf(saleCode));
					}
				}
			}
			
			/* 需求投放已结束 */
			/* ...... */
			xqSql = "SELECT COUNT(*) FROM SALE LEFT JOIN SALEDETAIL ON SA_ID = SD_SAID WHERE SA_CODE='"+saleCode+"' AND NVL(SD_PURNEED,' ') <> '全部投放'";
			xqCount = baseDao.getCount(xqSql);
			if(xqCount == 0){
				if(minTime[1]!=null && !"null".equals(minTime[1]) && !"".equals(minTime[1])){
					result.get(2).put("remark", "需求全部投放");result.get(2).put("status", "finish");
					result.get(2).put("end", String.valueOf(minTime[1]).substring(0,10));
				}
				
				//4.采购已下达		
				int count = baseDao.getCount("SELECT COUNT(*) FROM APPLICATION LEFT JOIN APPLICATIONDETAIL ON AP_ID=AD_APID WHERE AD_REMARK LIKE '%"+saleCode+"%' AND ad_status not in('已作废','已结案') AND nvl(AD_QTY,0) <> nvl(AD_YQTY,0)");
				Object[] recordDate = baseDao.getFieldsDataByCondition("APPLICATION LEFT JOIN APPLICATIONDETAIL ON AP_ID=AD_APID", "MIN(AP_DATE),MAX(AP_DATE)", "AD_REMARK LIKE '%"+saleCode+"%' AND ad_status not in('已作废','已结案')");
				if(count > 0){
					result.get(3).put("remark", "备注带有该销售订单单号序号的请购未全部转采购");result.get(3).put("status", "running");
					if(recordDate[0] != null && !"null".equals(recordDate[0])){
						result.get(3).put("start", String.valueOf(recordDate[0]).substring(0,10));
					}
				}else{
					result.get(3).put("remark", "备注带有该销售订单单号序号的请购已全部转采购");result.get(3).put("status", "finish");
					result.get(3).put("start", String.valueOf(recordDate[0]).substring(0,10));result.get(3).put("end", String.valueOf(recordDate[1]).substring(0,10));
					//5.到料			
					count = baseDao.getCount("SELECT COUNT(*) FROM PURCHASE LEFT JOIN PURCHASEDETAIL ON PU_ID=PD_PUID WHERE PD_REMARK LIKE '%"+saleCode+"%' AND nvl(PD_QTY,0) <> nvl(PD_YQTY,0)");
					Object[] sendMaterialDate = baseDao.getFieldsDataByCondition("PURCHASE LEFT JOIN PURCHASEDETAIL ON PU_ID=PD_PUID", "MIN(PU_DATE),MAX(PU_DATE)", "PD_REMARK LIKE '%"+saleCode+"%' AND nvl(PD_QTY,0) <> nvl(PD_YQTY,0)");
					if(count > 0){		//未全部到料
						result.get(4).put("remark", "未全部到料");result.get(4).put("status", "running");
						if(sendMaterialDate[0] != null && !"null".equals(sendMaterialDate[0])){
							result.get(4).put("start", String.valueOf(sendMaterialDate[0]).substring(0,10));
						}
					}else{
						sendMaterialDate = baseDao.getFieldsDataByCondition("PURCHASE LEFT JOIN PURCHASEDETAIL ON PU_ID=PD_PUID", "MIN(PU_DATE),MAX(PU_DATE)", "PD_REMARK LIKE '%"+saleCode+"%'");
						result.get(4).put("remark", "全部到料");result.get(4).put("status", "finish");
						result.get(4).put("start", String.valueOf(sendMaterialDate[0]).substring(0,10));result.get(4).put("end", String.valueOf(sendMaterialDate[1]).substring(0,10));
						
						//6.齐套			
						List<Object> list = baseDao.getFieldDatasByCondition("WCPLANDETAIL LEFT JOIN WCPLAN ON WC_ID=WD_WCID", "WD_TOPPRODCODE", "WD_SALECODE = '"+saleCode+"' AND WD_MAKECODE IS NULL ORDER BY WC_RUNDATE DESC");
						if(list != null && list.size() > 0){
							if(!"库存齐套".equals(list.get(0))){
								result.get(5).put("status", "running");
							}else{
								result.get(5).put("status", "finish");
							}
						}
					}
				}
			}
			
		}
		
		return result;
	}
	
	private List<Map<String, Object>> initOrderData(){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		//0.待确认
		map.put("text", "已接收"); map.put("status", "gray"); map.put("detno", 0);
		list.add(map);
		//1.已接收
		map = new HashMap<String, Object>();
		map.put("text", "待确认"); map.put("status", "gray"); map.put("detno", 1);
		list.add(map);
		//2.需求投放
		map = new HashMap<String, Object>();
		map.put("text", "需求投放"); map.put("status", "gray"); map.put("detno", 2);
		list.add(map);
		//3.采购已下达
		map = new HashMap<String, Object>();
		map.put("text", "采购已下达"); map.put("status", "gray"); map.put("detno", 3);
		list.add(map);
		//4.到料
		map = new HashMap<String, Object>();
		map.put("text", "到料"); map.put("status", "gray");  map.put("detno", 4);
		list.add(map);
		//5.齐套
		map = new HashMap<String, Object>();
		map.put("text", "齐套"); map.put("status", "gray"); map.put("detno", 5);
		list.add(map);
		//6.生产指令投放
		map = new HashMap<String, Object>();
		map.put("text", "生产指令投放"); map.put("status", "gray"); map.put("detno", 3);
		list.add(map);
		//7.发料
		map = new HashMap<String, Object>();
		map.put("text", "发料"); map.put("status", "gray"); map.put("detno", 4);
		list.add(map);
		//8.完工
		map = new HashMap<String, Object>();
		map.put("text", "完工"); map.put("status", "gray"); map.put("detno", 5);
		list.add(map);
		return list;
	}
	
	private boolean workList(List<Map<String, Object>> list, List<Map<String, Object>> maCodeList, String saleCode){
		boolean flag = false;
		Map<String, Object> map = new HashMap<String, Object>();
		if(maCodeList != null && maCodeList.size() > 0){
			flag = true;
			StringBuilder sb = new StringBuilder();
			for(Map<String, Object> maCodeMap : maCodeList){
				sb.append(maCodeMap.get("MA_CODE")+",");
			}
			sb.deleteCharAt(sb.length()-1);
			String maCodes = "'" + sb.toString().replaceAll(",", "','") + "'";
			Object[] minMaxDate = baseDao.getFieldsDataByCondition("MAKE", "MIN(MA_DATE),MAX(MA_DATE)", "MA_CODE IN ("+maCodes+")");
			if(!"null".equals(minMaxDate[0])&&minMaxDate[0]!=null){
				list.get(6).put("start", String.valueOf(minMaxDate[0]).substring(0,10));
			}
			//工单部分下达
			list.get(6).put("remark", "生产指令部分投放");list.get(6).put("status", "running");
			//工单全部下达
			int count = baseDao.getCount("select count(*) from sale left join saledetail on sa_id=sd_said where sa_code='"+saleCode+"' and nvl(SD_MAKENEED, ' ') <> '全部投放'");
			if(count == 0){
				list.get(6).put("remark", "生产指令全部投放");list.get(6).put("status", "finish");
				list.get(6).put("end", String.valueOf(minMaxDate[1]).substring(0,10));
			}
		}
		return flag;
	}
	
	/**
	 * 发料
	 * @param list
	 * @param maCodeList
	 * @param n
	 */
	private boolean sendMaterial(List<Map<String, Object>> list, List<Map<String, Object>> maCodeList, String saleCode){
		boolean flag = false;
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		for(Map<String, Object> maCodeMap : maCodeList){
			sb.append(maCodeMap.get("MA_CODE")+",");
		}
		sb.deleteCharAt(sb.length()-1);
		String maCodes = "'" + sb.toString().replaceAll(",", "','") + "'";
		String materialSql = "SELECT COUNT(*) FROM MAKE LEFT JOIN MakeMaterial ON MA_ID=MM_MAID WHERE mm_havegetqty > 0 AND MA_CODE in ("+maCodes+")";
		int count = baseDao.getCount(materialSql);
		if(count > 0){
			flag = true;
			Object[] minMaxDate = baseDao.getFieldsDataByCondition("MAKE LEFT JOIN MakeMaterial ON MA_ID=MM_MAID", "MIN(MA_DATE),MAX(MA_DATE)", "mm_havegetqty > 0 AND MA_CODE in ("+maCodes+")");
			if(minMaxDate[0] != null && minMaxDate.length > 0 && !"null".equals(minMaxDate[0])){
				list.get(7).put("start", String.valueOf(minMaxDate[0]).substring(0,10));
				list.get(7).put("remark", "部分发料");list.get(7).put("status", "running");	//部分领料
			}
			//全部领料(1.工单全部下达。2.工单下面的所有明细  制单需求数=已领料数    mm_qty=mm_havegetqty)
			//list.get(7).put("status", "finish");list.get(7).put("end", minMaxDate[1]);list.get(7).put("remark", "发料");
			int gdCount = baseDao.getCount("select count(*) from sale left join saledetail on sa_id=sd_said where sa_code='"+saleCode+"' and nvl(SD_MAKENEED,' ') <> '全部投放'");
			if(gdCount == 0){			//1.工单全部下达
				int flCount = baseDao.getCount("select count(*) from make left join makeMaterial on ma_id=mm_maid where MA_CODE in ("+maCodes+") and mm_qty<>mm_havegetqty");
				if(flCount == 0){		//2.工单下面的所有明细  制单需求数=已领料数
					list.get(7).put("remark", "发料");list.get(7).put("status", "finish");
					list.get(7).put("end", String.valueOf(minMaxDate[1]).substring(0,10));
				}
			}
		}
		return flag;
	}
	
	/**
	 * 完工
	 * @param list
	 * @param maCodeList
	 * @param n
	 */
	private boolean complete(List<Map<String, Object>> list, List<Map<String, Object>> maCodeList, String saleCode){
		boolean flag = false;
		StringBuilder sb = new StringBuilder();
		for(Map<String, Object> maCodeMap : maCodeList){
			sb.append(maCodeMap.get("MA_CODE")+",");
		}
		sb.deleteCharAt(sb.length()-1);
		String maCodes = "'" + sb.toString().replaceAll(",", "','") + "'";
		//完工
		int count = baseDao.getCount("SELECT count(*) FROM MAKE WHERE MA_FINISHSTATUS='已完工' AND MA_CODE IN ("+maCodes+")");
		if(count > 0){
			flag = true;
			Object[] minMaxDate = baseDao.getFieldsDataByCondition("MAKE", "MIN(MA_DATE),MAX(MA_DATE)", "MA_FINISHSTATUS='已完工' AND MA_CODE IN ("+maCodes+")");
			if(!"null".equals(minMaxDate[0]) && minMaxDate[0] != null){
				list.get(8).put("start", String.valueOf(minMaxDate[0]).substring(0,10));
			}
			list.get(8).put("remark", "部分完工");list.get(8).put("status", "running");	//部分完工
			//全部完工
			/* ...... */
			//list.get(8).put("end", minMaxDate[1]);list.get(8).put("status", "finish");list.get(8).put("remark", "完工");
			int gdCount = baseDao.getCount("select count(*) from sale left join saledetail on sa_id=sd_said where sa_code='"+saleCode+"' and nvl(SD_PURNEED,' ') <> '全部投放'");
			if(gdCount == 0){	//1.工单全部下达
				int wgCount = baseDao.getCount("SELECT count(*) FROM MAKE WHERE MA_FINISHSTATUS<>'已完工' AND MA_CODE IN ("+maCodes+")");
				if(wgCount == 0){	//2.不存在状态 不等于 已完工的  工单
					list.get(8).put("start", String.valueOf(minMaxDate[1]).substring(0,10));
					list.get(8).put("remark", "完工");list.get(8).put("status", "finish");	//部分完工
				}
			}
			
		}
		return flag;
	}
	
	/**
	 * 批量转新物料申请
	 * @param data
	 * @return
	 */
	public Map<String, Object> turnPreproduct(String data){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray array = JSONArray.parseArray(data);
		String emName = SystemSession.getUser().getEm_name();
		StringBuilder SqlBuilder = new StringBuilder(" begin ");
		for(int i = 0; i < array.size(); i++){
			String code = singleFormItemsService.getCodeString("PreProduct", "PreProduct", 2);
			Map<String, Object> tempMap = array.getJSONObject(i);
			baseDao.getCount("select count(*) from product ");
			Set<String> keys = tempMap.keySet();
			StringBuilder sb = new StringBuilder("insert into preproduct(pre_id,pre_recordman,pre_date,pre_status,pre_statuscode,pre_thisid,");
			StringBuilder sb2 = new StringBuilder(" values(preproduct_seq.nextval,'"+emName+"',sysdate,'在录入','ENTERING','"+code+"',");
			for(String key : keys){
				sb.append(key+",");
				Object value = tempMap.get(key);
				if(value != null){
					String val = value.toString();
					if (val.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD, val));
					} else if (val.matches(REG_DT)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val));
					} else if (val.matches(REG_TS)) {
						sb2.append(DateUtil.parseDateToOracleString(Constant.YMD_HMS, val.substring(0, val.lastIndexOf("."))));
					} else if (val.equals("null")) {
						sb2.append("null");
					}else{
						if (val.contains("'")) {
							val = val.replaceAll("'", "''");
						} else if (val.contains("%n")) {
							val = val.replaceAll("%n", "\n");
						}
						// 针对较长字段，比如clob类型，防止ORA-01704的简单处理：切割成多个字符串连接起来
						if (val.length() > 2000) {
							sb2.append("''");
						} else if (val.length() > 1000) {
							sb2.append(StringUtil.splitAndConcat(val, 1333, "'", "'", "||"));
						} else {
							sb2.append("'" + val + "'");
						}
					}
				}else{
					sb2.append("null");
				}
				sb2.append(",");
			}
			sb.deleteCharAt(sb.length()-1);sb.append(")");
			sb2.deleteCharAt(sb2.length()-1);sb2.append(");");
			SqlBuilder.append(sb.toString() + sb2.toString());
		}
		SqlBuilder.append(" end;");
		baseDao.execute(SqlBuilder.toString());
		map.put("message", "已转新物料申请!");
		return map;
	}
	
}
