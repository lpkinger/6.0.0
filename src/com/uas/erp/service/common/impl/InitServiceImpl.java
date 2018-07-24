package com.uas.erp.service.common.impl;

import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InitDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitLog;
import com.uas.erp.model.InitNodes;
import com.uas.erp.model.Initialize;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AbstractInit;
import com.uas.erp.service.common.InitService;
import com.uas.erp.service.crm.impl.CrmHandlerService;

@Service("initService")
public class InitServiceImpl implements InitService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private InitDao initDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private CrmHandlerService CrmHandler;

	/**
	 * @param pid
	 *            父节点ID
	 * @return 初始化项目
	 */
	@Override
	public List<Initialize> getInitTree(int pid) {
		return initDao.getInitializes(pid);
	}

	/**
	 * @return caller的初始化数据字段
	 */
	@Override
	public List<InitDetail> getInitDetails(String caller) {
		return initDao.getInitDetails(caller);
	}

	/**
	 * @return caller的初始化数据字段
	 */
	public List<InitDetail> getInitDetails(String caller, String condition) {
		return initDao.getInitDetailsByCondition(caller, condition);
	}

	/**
	 * 保存从excel解析的数据到临时表initdata
	 * 
	 * @param caller
	 * @param data
	 *            从excel解析出来的数据
	 * @param ilid
	 *            InitLog_ID
	 * @return 该caller已导入数据次数sequence
	 */
	@Override
	public int saveInitData(String caller, List<String> data, Integer ilid) {
		Employee employee = SystemSession.getUser();
		int detno = 1;
		if (ilid == null) {
			int sequence = 0;
			Object obj = baseDao.getFieldDataByCondition("initlog", "max(il_sequence)", "il_caller='" + caller + "'");
			sequence = obj == null ? 1 : (Integer.parseInt(obj.toString()) + 1);
			ilid = baseDao.getSeqId("INITLOG_SEQ");
			StringBuffer sb = new StringBuffer("INSERT INTO initlog(il_id,il_caller,il_sequence,il_count,il_man) VALUES(");
			sb.append(ilid);
			sb.append(",'");
			sb.append(caller);
			sb.append("',");
			sb.append(sequence);
			sb.append(",");
			sb.append(data.size());
			sb.append(",'");
			sb.append(employee.getEm_name());
			sb.append("')");
			baseDao.execute(sb.toString());
		} else {
			Object obj = baseDao.getFieldDataByCondition("initdata", "max(id_detno)", "id_ilid=" + ilid);
			detno = obj == null ? 1 : (Integer.parseInt(obj.toString()) + 1);
			baseDao.updateByCondition("InitLog", "il_count=il_count+" + data.size() + ",il_man='"+employee.getEm_name()+"'", "il_id=" + ilid);
		}
		List<InitData> datas = new ArrayList<InitData>();
		for (String d : data) {
			datas.add(new InitData(d, ilid, detno++));
		}
		if (datas.size() > 0) {
			initDao.save(datas);
		}
		return ilid;
	}

	@Override
	public List<InitData> getInitDatas(String condition) {
		return initDao.getInitDatas(condition);
	}

	@Override
	public List<InitLog> getInitHistory(String caller) {
		return initDao.getInitHistory(caller);
	}

	@Override
	public void updateInitData(String data) {
		Employee employee = SystemSession.getUser();
		JSONArray items = JSONArray.fromObject(data);
		List<String> sqls = new ArrayList<String>();
		Object idid = null;
		String json = null;
		Iterator<?> iter = items.iterator();
		JSONObject obj = null;
		while (iter.hasNext()) {
			obj = (JSONObject) iter.next();
			idid = obj.get("id_id");
			obj.remove("id_id");//why
			json = obj.toString();
			sqls.add("UPDATE InitData set id_data='" + json + "' where id_id=" + idid);
		}
		//保存修改时，更新initLog中的操作人
		sqls.add("UPDATE InitLog set il_man='"+employee.getEm_name()+"' "
				+ "where il_id=(select id_ilid from InitData where id_id="+ idid +")");
		baseDao.execute(sqls);
	}

	@Override
	public void deleteInitData(int id) {
		int count = baseDao.getCountByCondition("InitLog", "il_id=" + id + " AND il_toformal=0");// 非已转正式
		if (count == 1) {
			baseDao.deleteByCondition("InitData", "id_ilid=" + id);
			baseDao.deleteByCondition("InitLog", "il_id=" + id);
			// 删除InitNodes的数据
			baseDao.deleteByCondition("InitNodes", "in_ilid=" + id);
		} else {
			BaseUtil.showError("该数据已被删除或已转正式数据，无法删除.");
		}
	}

	@Override
	public void check(int il_id) {
		String caller = baseDao.getFieldDataByCondition("InitLog", "il_caller", "il_id=" + il_id).toString();
		// 待校验字段
		List<InitDetail> details = getInitDetails(caller, "id_type is not null or id_logic is not null");
		// 待校验数据
		List<InitData> datas = getInitDatas("id_ilid=" + il_id);
		CheckUtil util = new CheckUtil(il_id, details, datas);
		// 将一部分错误信息先记录在InitLog
		String err = util.getErrorNodes().toString();
		if (err.trim().length() > 0) {
			initDao.saveErrorMsg(il_id, err);
		}
		// 临时存放unique和accord节点
		List<InitNodes> nodes = util.getNodes();
		if ("AssetsCard".equals(caller)) {// 卡片入账期ac_date必须小于等于当前固定资产账期
			String PD_ENDDATE = baseDao.getFieldDataByCondition("PeriodsDETAIL", "min(to_char(PD_ENDDATE,'yyyymm'))", "PD_CODE='MONTH-F' AND PD_STATUS='0'")
					.toString();
			String pe_firstday=baseDao.getFieldDataByCondition("Periods", "to_char(to_date(pe_firstday,'yyyymm'),'yyyymm')", "pe_code='MONTH-F'").toString();
			// 科目
			Map<String, List<String>> codeMap = new HashMap<String, List<String>>();
			//固定资产科目
			Map<String, List<String>> accateCodeMap = new HashMap<String, List<String>>();
			//累计折旧科目
			Map<String, List<String>> ascateCodeMap = new HashMap<String, List<String>>();
			String[] codeFields = new String[] { "ac_accatecode", "ac_ascatecode", "ac_totalcatecode" };
			StringBuffer errBuffer = new StringBuffer();
			for (InitData d : datas) {
				JSONObject j = JSONObject.fromObject(d.getId_data());
				for (String field : codeFields) {
					String code = j.getString(field);
					if (!StringUtils.isEmpty(code)) {
						if (!codeMap.containsKey(code)) {
							codeMap.put(code, new ArrayList<String>());
						}
						codeMap.get(code).add(d.getId_id() + ":" + field);
						
						if("ac_accatecode".equals(field)){
							if (!accateCodeMap.containsKey(code)) {
								accateCodeMap.put(code, new ArrayList<String>());
							}
							accateCodeMap.get(code).add(d.getId_id() + ":" + field);
						}
						if("ac_ascatecode".equals(field)){
							if (!ascateCodeMap.containsKey(code)) {
								ascateCodeMap.put(code, new ArrayList<String>());
							}
							ascateCodeMap.get(code).add(d.getId_id() + ":" + field);
						}
					}
					
				}
				try {
					if(!(PD_ENDDATE.equals(pe_firstday))){
						BaseUtil.showError("当前账期与初始化期间不一致！");
					}
				} catch (Exception e) {
					e.printStackTrace();
					BaseUtil.showError("当前账期与初始化期间不一致！");
				}
				try {
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
					SimpleDateFormat sdfc=new SimpleDateFormat(Constant.YMD);
					Date dt = sdfc.parse(j.getString("ac_date"));
					String sDate=sdf.format(dt);
					if(Integer.parseInt(sDate)>Integer.parseInt(PD_ENDDATE)){
						errBuffer.append("," + d.getId_id() + ":ac_date");
						initDao.saveErrorMsg(il_id, errBuffer.toString());
					}
				} catch (Exception e) {
					BaseUtil.showError("卡片入账日期不能大于当前账期！");
				}
			}
			// 科目号每行基本相同，放一起统一校验
			for (String code : codeMap.keySet()) {
				int count = baseDao.getCount("select count(1) from category where ca_code='" + code + "' and ca_isleaf<>0");
				if (count == 0)
					errBuffer.append("," + CollectionUtil.toString(codeMap.get(code)));
			}
			
			//检查固定资产科目编号和累计折旧科目编号是不是参数设置中的科目
			checkCate(accateCodeMap,ascateCodeMap,errBuffer);			
			
			if (errBuffer.length() > 0)
				initDao.saveErrorMsg(il_id, errBuffer.toString());
		}
		// 只检查科目
		// 数据校验（验证同一父级科目下，科目名称不能重复，其中包括父级科目为空）
		if ("Category!Base".equals(caller)) {
			Boolean bool = false;
			for (int i = 0; i < datas.size(); i++) {
				JSONObject m = JSONObject.fromObject(datas.get(i).getId_data());
				if (m.get("ca_pcode").equals("")) {
					bool = baseDao.checkIf("category",
							"nvl(ca_pcode,' ')=' ' and ca_statuscode<>'DISABLE' and ca_name='" + m.get("ca_name") + "'");
				} else {
					bool = baseDao.checkIf("category",
							"nvl(ca_pcode,' ')<>' ' and ca_statuscode<>'DISABLE' and ca_pcode='" + m.get("ca_pcode") + "' and ca_name='"
									+ m.get("ca_name") + "'");
				}
				if (bool) {
					initDao.saveErrorMsg(il_id, "," + datas.get(i).getId_id() + ":ca_name");
					continue;
				}
				for (int j = i + 1; j < datas.size(); j++) {
					JSONObject p = JSONObject.fromObject(datas.get(j).getId_data());
					String mdata = m.get("ca_pcode") + "#" + m.get("ca_name");
					String pdata = p.get("ca_pcode") + "#" + p.get("ca_name");
					if (mdata.equals(pdata)) {
						initDao.saveErrorMsg(il_id, "," + datas.get(j).getId_id() + ":ca_name");
						initDao.saveErrorMsg(il_id, "," + datas.get(i).getId_id() + ":ca_name");
					}
				}
			}
		}
		//科目年初数开账
		if("CateMonth!Begin".equals(caller)){
			for (int i = 0; i < datas.size(); i++){
				JSONObject m = JSONObject.fromObject(datas.get(i).getId_data());
				if(m.get("cm_yearmonth").toString().substring(4).indexOf("01")==-1){
				initDao.saveErrorMsg(il_id, "," + datas.get(i).getId_id() + ":cm_yearmonth");
				BaseUtil.showError("科目年初数期间必须为一月！");
				}
			}
		}
		// 物料种类校验
		if ("ProductKind".equals(caller)) {
			String err1 = "";
			String err2 = "";
			for (int i = 0; i < datas.size(); i++) {
				JSONObject m = JSONObject.fromObject(datas.get(i).getId_data());
				Boolean b = false;
				Boolean c = false;
				for (int j = i + 1; j < datas.size(); j++) {
					JSONObject p = JSONObject.fromObject(datas.get(j).getId_data());
					String mdata = m.get("pk_code1") + "#" + m.get("pk_code2") + "#" + m.get("pk_code3") + "#" + m.get("pk_code4");
					String pdata = p.get("pk_code1") + "#" + p.get("pk_code2") + "#" + p.get("pk_code3") + "#" + p.get("pk_code4");
					String ndata = m.get("pk_name1") + "#" + m.get("pk_name2") + "#" + m.get("pk_name3") + "#" + m.get("pk_name4");
					String cdata = p.get("pk_name1") + "#" + p.get("pk_name2") + "#" + p.get("pk_name3") + "#" + p.get("pk_name4");
					if (mdata.equals(pdata)) {
						b = true;
						err1 += "," + datas.get(j).getId_id() + ":pk_code1" + "," + datas.get(j).getId_id() + ":pk_code2" + ","
								+ datas.get(j).getId_id() + ":pk_code3" + "," + datas.get(j).getId_id() + ":pk_code4";
					}
					if (ndata.equals(cdata)) {
						c = true;
						err2 += "," + datas.get(j).getId_id() + ":pk_name1" + "," + datas.get(j).getId_id() + ":pk_name2" + ","
								+ datas.get(j).getId_id() + ":pk_name3" + "," + datas.get(j).getId_id() + ":pk_name4";
					}
				}
				if (b)
					err1 += "," + datas.get(i).getId_id() + ":pk_code1" + "," + datas.get(i).getId_id() + ":pk_code2" + ","
							+ datas.get(i).getId_id() + ":pk_code3" + "," + datas.get(i).getId_id() + ":pk_code4";
				if (c)
					err2 += "," + datas.get(i).getId_id() + ":pk_name1" + "," + datas.get(i).getId_id() + ":pk_name2" + ","
							+ datas.get(i).getId_id() + ":pk_name3" + "," + datas.get(i).getId_id() + ":pk_name4";
			}
			if (err1 != null && err1 != "")
				initDao.saveErrorMsg(il_id, err1);
			if (err2 != null && err2 != "")
				initDao.saveErrorMsg(il_id, err2);
		}
		if ("Payments!Sale".equals(caller)) {
			for (int i = 0; i < datas.size(); i++) {
				JSONObject m = JSONObject.fromObject(datas.get(i).getId_data());
				for (int j = i + 1; j < datas.size(); j++) {
					JSONObject p = JSONObject.fromObject(datas.get(j).getId_data());
					String mdata = m.get("pa_code") + "#" + m.get("pa_name") + "#" + m.get("pa_class");
					String pdata = p.get("pa_code") + "#" + p.get("pa_name") + "#" + m.get("pa_class");
					if (mdata.equals(pdata)) {
						initDao.saveErrorMsg(il_id, "," + datas.get(i).getId_id() + ":pa_code" + "," + datas.get(i).getId_id() + ":pa_name"
								+ "," + datas.get(i).getId_id() + ":pa_class");
						initDao.saveErrorMsg(il_id, "," + datas.get(j).getId_id() + ":pa_code" + "," + datas.get(j).getId_id() + ":pa_name"
								+ "," + datas.get(j).getId_id() + ":pa_class");
					}
				}
			}

		}
		if ("BOM".equals(caller)) {
			// BOM基本校验
			validBOMDetail(datas);
			nodes.addAll(validBOM(il_id, datas));
		}
		if (nodes.size() > 0) {
			initDao.logNodes(nodes);
		}
		// if ("BOM".equals(caller)) {
		// baseDao.execute(
		// "delete from initnodes A where in_ilid=? and in_table='BOM' and in_logic='unique(BOM|bo_mothercode)' and (in_value ||'#'|| in_idid) not in(select min(in_value ||'#'|| in_idid) from initnodes where in_ilid=? and in_table=A.in_table and in_logic=A.in_logic group by in_table,in_logic,in_value)",
		// il_id, il_id);
		// }
		
		//物料资料导入限制
		if("Product".equals(caller)||"WareHouse".equals(caller)){
			//存货科目
			Map<String, List<String>> stockcateCodeMap = new HashMap<String, List<String>>();
			//收入科目
			Map<String, List<String>> incomecateCodeMap = new HashMap<String, List<String>>();
			//成本科目
			Map<String, List<String>> costcateCodeMap = new HashMap<String, List<String>>();
			String[] codeFields = null;
			if("Product".equals(caller)){
				codeFields = new String[] { "pr_stockcatecode", "pr_incomecatecode","pr_costcatecode"};
			}else{
				codeFields = new String[] { "wh_catecode", "wh_salecatecode","wh_costcatecode"};
			}
			
			StringBuffer errBuffer = new StringBuffer();
			for (InitData d : datas) {
				JSONObject j = JSONObject.fromObject(d.getId_data());
				for (String field : codeFields) {
					if(j.containsKey(field)){
						String code = j.getString(field);
						if (!StringUtils.isEmpty(code)) {
							if(codeFields[0].equals(field)){
								if (!stockcateCodeMap.containsKey(code)) {
									stockcateCodeMap.put(code, new ArrayList<String>());
								}
								stockcateCodeMap.get(code).add(d.getId_id() + ":" + field);
							}
							if(codeFields[1].equals(field)){
								if (!incomecateCodeMap.containsKey(code)) {
									incomecateCodeMap.put(code, new ArrayList<String>());
								}
								incomecateCodeMap.get(code).add(d.getId_id() + ":" + field);
							}
							if(codeFields[2].equals(field)){
								if (!costcateCodeMap.containsKey(code)) {
									costcateCodeMap.put(code, new ArrayList<String>());
								}
								costcateCodeMap.get(code).add(d.getId_id() + ":" + field);
							}
						}						
					}
				}
			}
			
			//检查存货科目编号、收入科目编号和成本科目编号是不是参数设置中的科目
			checkProductCate(stockcateCodeMap,incomecateCodeMap,costcateCodeMap,errBuffer);			
			
			if (errBuffer.length() > 0)
				initDao.saveErrorMsg(il_id, errBuffer.toString());			
		}
		
	}

	public void checkProductCate(Map<String, List<String>> stockcateCodeMap,Map<String, List<String>> incomecateCodeMap,Map<String, List<String>> costcateCodeMap,StringBuffer errBuffer){
		//检查存货科目编号是不是参数设置中的科目
		for(String code:stockcateCodeMap.keySet()){
			String error = "";
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!scm','stockCatecode'), chr(10))))",
							String.class, code);
			if (error != null) {
				errBuffer.append("," + CollectionUtil.toString(stockcateCodeMap.get(code)));
			}
			
		}
		//检查收入科目编号是不是参数设置中的科目
		for(String code:incomecateCodeMap.keySet()){
			String error = "";
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','incomeCatecode'), chr(10))))",
							String.class, code);
			if (error != null) {
				errBuffer.append("," + CollectionUtil.toString(incomecateCodeMap.get(code)));
			}			
		}
		//检查成本科目编号是不是参数设置中的科目
		for(String code:costcateCodeMap.keySet()){
			String error = "";
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','costCatecode'), chr(10))))",
							String.class, code);
			if (error != null) {
				errBuffer.append("," + CollectionUtil.toString(costcateCodeMap.get(code)));
			}			
		}
	}
	
	public void checkCate(Map<String, List<String>> accateCodeMap,Map<String, List<String>> ascateCodeMap,StringBuffer errBuffer){
		//检查固定资产科目编号是不是参数设置中的科目
		for(String code:accateCodeMap.keySet()){
			String error = "";
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','fixCatecode'), chr(10))))",
							String.class, code);
			if (error != null) {
				errBuffer.append("," + CollectionUtil.toString(accateCodeMap.get(code)));
			}
			
		}
		//检查累计折旧科目编号是不是参数设置中的科目
		for(String code:ascateCodeMap.keySet()){
			String error = "";
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','deCatecode'), chr(10))))",
							String.class, code);
			if (error != null) {
				errBuffer.append("," + CollectionUtil.toString(ascateCodeMap.get(code)));
			}			
		}
		
	}
	
	private List<InitNodes> validBOM(int ilid, List<InitData> datas) {
		Map<String, Integer> codes = new HashMap<String, Integer>();
		for (InitData d : datas) {
			JSONObject j = JSONObject.fromObject(d.getId_data());
			codes.put(j.getString("bo_mothercode"), d.getId_id());
		}
		List<InitNodes> nodes = new ArrayList<InitNodes>();
		Set<String> cs = codes.keySet();
		for (String c : cs) {
			nodes.add(new InitNodes("BOM", ilid, "unique(BOM|bo_mothercode)", "bo_mothercode", c, codes.get(c), 0));
		}
		return nodes;
	}

	private void validBOMDetail(List<InitData> datas) {
		Set<String> keys = new HashSet<String>();
		StringBuffer sb = new StringBuffer();
		for (InitData d : datas) {
		    JSONObject j = JSONObject.fromObject(d.getId_data());
			//Map<Object, Object> j = JSONUtil.toMap(d.getId_data());
			Object motherCode = j.get("bo_mothercode");
			Object sonCode = j.get("bd_soncode");
			Object bd_detno = j.get("bd_detno");
			if (motherCode == null || sonCode == null)
				continue;
			// 子件重复
			String key = motherCode.toString() + sonCode;
			if (keys.contains(key)) {
				sb.append("子件重复(母件:").append(motherCode).append("子件:").append(sonCode).append(")<br>");
			} else {
				keys.add(key);
			}
			if (motherCode.equals(sonCode))
				sb.append("子件编号不能等于母件编号(母件:").append(motherCode).append("序号:").append(bd_detno).append(")<br>");
			// 单位用量与位号匹配
			int count = 0;
			Object location = j.get("bo_location");
			if (location != null && location.toString().trim().length() != 0) {
				count = StringUtils.countOccurrencesOf(location.toString(), ",") + 1;
			}
			if (count > 0) {
				Double baseQty = Double.parseDouble(String.valueOf((j.get("bd_baseqty") == null || j.get("bd_baseqty").toString().trim()
						.length() == 0) ? 0 : j.get("bd_baseqty")));
				if (baseQty >= 1) {
					if (baseQty != count) {
						sb.append("位号与单位用量不匹配(母件:").append(motherCode).append("子件:").append(sonCode).append(")<br>");
					}
				}
			}
			
			//@add 20180109 反馈编号2017120499，替代料校验是否在物料资料表中
			Object replace = j.get("bd_replace");
			if (replace != null && replace.toString().trim().length() > 0) {
				String[] repCodes = StringUtil.deleteRepeats(replace.toString(),",").split(",");
				String errRep="";
				for (String s : repCodes) {
					SqlRowList rs =baseDao.queryForRowSet("select pr_id from product where pr_code=?",s);
					if(!rs.next()){
						errRep +=s;
					}
				}
				if(errRep.length()>0){
					sb.append("替代料号错误，不在物料资料中(母件：").append(motherCode).append(",子件:").append(sonCode).append(",替代料号:").append(errRep).append(")<br>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
	}

	@Override
	public InputStream getResult(int id) {
		baseDao.updateByCondition("InitLog", "il_checked=1", "il_id=" + id);
		baseDao.updateByCondition("InitLog", "il_success=0", "il_id=" + id + " and dbms_lob.instr(il_result, ':') > 0");
		baseDao.updateByCondition("InitLog", "il_success=1,il_result=' '", "il_id=" + id
				+ " and dbms_lob.instr(nvl(il_result,' '), ':') = 0");
		return initDao.getResult(id);
	}

	@Override
	public void beforeCheckLog(int id) {
		// 防死进程
		/*
		 * List<String> locks = baseDao .query(
		 * "SELECT 'alter system kill session '||''''||trim(a.sid)||','||trim(a.serial#)||'''' from v$session a, v$sqlarea b,v$locked_object c where a.sql_address = b.address and c.session_id=a.sid and lower(sql_text)='delete from initnodes where in_ilid="
		 * + id + "'", String.class); baseDao.execute(locks);
		 */
		// 删除InitNodes的数据
		baseDao.deleteByCondition("InitNodes", "in_ilid=" + id);
		// 删除上次校验结果
		baseDao.updateByCondition("InitLog", "il_result=null", "il_id=" + id);
		// 删除InitNodes的索引
		try {
			int count = baseDao.getCountByCondition("InitData", "id_ilid=" + id);
			if (count >= 1000) {// 1000条以上的数据，校验前，删除索引，校验时，加上索引
				baseDao.execute("alter index INITNODES_INDEX1 UNUSABLE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterCheckLog(int id) {
		// 创建InitNodes的索引
		try {
			int count = baseDao.getCountByCondition("InitData", "id_ilid=" + id);
			if (count >= 1000) {
				baseDao.execute("alter index INITNODES_INDEX1 rebuild online");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 存储过程验证
		baseDao.procedure("INIT_CHECK", new Object[] { id });
	}

	/**
	 * 
	 * 转入正式表单
	 */
	@Override
	public void toFormalData(Employee employee, int id, int start, int end) {
		Object obj = baseDao.getFieldDataByCondition("initlog", "il_caller", "il_id=" + id + " AND il_toformal = 0");
		if (obj != null) {
			Object tableName = baseDao.getFieldDataByCondition("InitDetail", "id_table", "id_caller='" + obj + "'");
			if (tableName == null) {
				BaseUtil.showError("未找到配置!");
			} else {
				String error = null;
				if ("Purchase".equals(obj) || "Sale".equals(obj) || "PurchasePrice".equals(obj) || "SalePrice".equals(obj)
						|| "ProdInOut".equals(obj) || "Estimate".equals(obj) || "Make".equals(obj) || "SaleForecast".equals(obj)
						|| "GoodsSend".equals(obj) || "ARBill".equals(obj) || "APBill".equals(obj) || "ProdInOut!ExchangeIn".equals(obj)
						|| "ProdInOut!SaleBorrow".equals(obj) || "ProdInOut!OtherPurcIn".equals(obj) || "ProductSet".equals(obj)
						|| "Purc!Mould".equals(obj) || "MJProject!Mould".equals(obj) || "QUAProject".equals(obj)
						|| "CustomerDistr".equals(obj) || "CustomerAddress".equals(obj) || "Customer!ProductCustomer".equals(obj)
						|| "Craft".equals(obj) || "PackageCollection".equals(obj) || "Inquiry!Input".equals(obj)||"WarehouseMan".equals(obj)
						|| "ProductBrand!Input".equals(obj) || "StandbyOut!Input".equals(obj)|| "Purchasereply!Input".equals(obj) 
						|| "Contact".equals(obj) || "BOM!New".equals(obj)||"ProdRelation".equals(obj)||"ProdInOut!PurcCheckin".equals(obj)
						|| "ProdInOut!Sale".equals(obj)|| "ProdInOut!PurcCheckout".equals(obj)|| "ProdInOut!SaleReturn".equals(obj)
						|| "pledge".equals(obj)||"PurchaseChange".equals(obj)||"ProductLoss".equals(obj)){
					error = baseDao.callProcedure("INIT_PARSETOFORMAL", new Object[] { id, tableName });
				} else { 
					// 总账导入较特殊，LedgerInit、LedgerInitDetail均是非事务型临时用表，需要主动清除
					if ("LedgerInit".equals(obj)) {
						baseDao.getJdbcTemplate().update("delete from LedgerInit");
					} else if ("LedgerInitDetail".equals(obj)) {
						baseDao.getJdbcTemplate().update("delete from LedgerInitDetail");
					}else if("CateMonth!Begin".equals(obj)){
						baseDao.getJdbcTemplate().update("delete from CateMonthInit");
					}
					try {
						List<InitData> datas = getInitDatas("id_ilid=" + id + " AND id_detno between " + start + " AND " + end);
						List<InitDetail> details = initDao.getInitDetails(obj.toString());
						if ("ProductKind".equals(obj)) {
							AbstractInit init = new ProductKindInit(datas);
							init.toFormal();
						} else if ("BOM".equals(obj)) {
							AbstractInit init = new MultiBomInit(datas, employee);
							init.toFormal();
						}else if("BOM!Additional".equals(obj)) {
							AbstractInit init = new BomAdditionalInit(datas, employee);
							init.toFormal();
						}else {						
							// 限于所有单表转正式
							error = initDao.toFormalData(null, tableName.toString(),
									new ToFormalUtil(details, datas, employee).getFormals());
							// 导入辅助核算余额后直接调用开账程序（如果没有，只有科目余额的话，需要去对应web页面完成刷新操作）
							if (error == null && "LedgerInitDetail".equals(obj)) {
								error = baseDao.callProcedure("INIT_LEDGER", new Object[] {});
							}
							//科目年初数开账
							if (error == null && "CateMonth!Begin".equals(obj)) {
								error = baseDao.callProcedure("INIT_CATEMONTH", new Object[] {});
							}
							//商机导入后自动注册优软云
							if (error == null && "BusinessChance".equals(obj)) {
								boolean flag = baseDao.isDBSetting("BusinessChance","BC_ToUbtob");
								if (flag) {
									//接口调用
									for (InitData data : datas) {
										String d = data.getId_data();
										Map<Object, Object> map = BaseUtil.parseFormStoreToMap(d);
										if (map.get("bc_tel") != null) {
											Object tel = map.get("bc_tel");
											CrmHandler.https_post("http://login.uuzcc.com/index/ubtob/login_reg/mobile/"+tel);
										}
									}
								}		
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new SystemException(e.getMessage());
					}
				}
				if (error != null)
					throw new SystemException(error);
			}
		} else {
			throw new SystemException("数据已删除或已转正式,无法执行转正式数据操作!");
		}
	}
	
	@Override
	public void beforeToFormal(int id) {
		Object obj = baseDao.getFieldDataByCondition("InitLog", "il_caller", "il_id=" + id + " AND il_toformal = 0");
		if (obj != null) {
			// Object[] objs = baseDao.getFieldsDataByCondition("InitDetail",
			// new String[]{"id_table", "id_field"}, "id_caller='" + obj +
			// "' AND id_default='keyField'");
			// if(objs != null && objs[1] != null) {
			// baseDao.createTrigger(objs[0].toString(), objs[1].toString());
			// }
		} else {
			BaseUtil.showError("数据已删除或已转正式,无法执行转正式数据操作!");
		}
	}

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public void afterToFormal(int id) {
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition("InitLog", "il_toformal=1,il_man='"+employee.getEm_name()+"'", "il_id=" + id);
		baseDao.updateByCondition("InitData", "id_toformal=1", "id_ilid=" + id);
		baseDao.deleteByCondition("InitNodes", "in_ilid=" + id);
		Object obj = baseDao.getFieldDataByCondition("InitLog", "il_caller", "il_id=" + id);
		// 执行导入成功之后的逻辑
		handlerService.handler(obj.toString(), "import", "after", new Object[] {});
	}

	@Override
	public void saveInitDetail(Employee employee, String store) {
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(store);
		Object id, table = null;
		if (datas.size() > 0)
			table = String.valueOf(datas.get(0).get("id_table")).toUpperCase();
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> d : datas) {
			id = d.get("id_id");
			if (id == null || "".equals(id) || Integer.parseInt(id.toString()) == 0) {
				d.put("id_id", baseDao.getSeqId("INITDETAIL_SEQ"));
				sqls.add(SqlUtil.getInsertSqlByMap(d, "InitDetail"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(d, "InitDetail", "id_id"));
			}
		}
		baseDao.execute(sqls);
		if (table != null && !table.equals("NULL") && !table.equals("") && String.valueOf(table).substring(0, 3).equals("TT_")) {
			Master master = employee.getCurrentMaster();
			// 修改字段前先判断临时表是否被占用
			if (master != null) {
				Object objId = baseDao.getFieldDataByCondition("dba_objects", "object_id", "object_name='" + table + "' and owner='"
						+ master.getMa_user() + "'");
				if (objId != null) {
					List<String> locks = baseDao
							.queryForList(
									"SELECT 'alter system kill session '||''''||trim(sid)||','||trim(serial#)||'''' from v$session where SID in ( select sid from v$enqueue_lock t where t.type='TO' and  id1='"
											+ objId + "' )", String.class);
					baseDao.execute(locks);
				}
			}
			Object field, type, caption, fieldtype;
			String sql;
			for (Map<Object, Object> d : datas) {
				field = d.get("id_field");
				type = d.get("id_type");
				caption = d.get("id_caption");
				fieldtype = d.get("id_fieldtype");
				if (field != null && type != null && fieldtype != null) {
					field = String.valueOf(field).toUpperCase();
					type = String.valueOf(type).startsWith("number") ? "varchar2(50)" : type;
					type = String.valueOf(type).startsWith("date") ? "date" : type;
					boolean bl1 = baseDao.checkIf("user_tab_columns", "table_name='" + table + "'");
					if (bl1) {
						bl1 = baseDao.checkIf("user_tab_columns", "table_name='" + table + "' and column_name ='" + field + "'");
						if (bl1) {
							sql = "alter table " + table + " modify " + field + " " + type;
							baseDao.execute(sql);
						} else {
							sql = "alter table " + table + " add " + field + " " + type;
							baseDao.execute(sql);
						}
						sql = "COMMENT ON COLUMN " + table + "." + field + " IS '" + caption + "'";
						baseDao.execute(sql);
					}
				}
			}
		}
	}

	@Override
	public void deleteErrInitData(int id) {
		int count = baseDao.getCountByCondition("InitLog", "il_id=" + id + " AND il_toformal=0");// 非已转正式
		if (count == 1) {
			InputStream in = getResult(id);
			if (in != null) {
				String result = StringUtil.parserInputStream(in);
				if (result.length() > 0) {
					String[] nodes = result.split(",");
					StringBuffer sb = new StringBuffer();
					for (String n : nodes) {
						if (n != null && n.indexOf(":") > 0) {
							if (sb.length() > 0)
								sb.append(",");
							sb.append(n.split(":")[0]);
						}
					}
					baseDao.deleteByCondition("InitData", "id_id in(" + sb.toString() + ")");
					count = baseDao.getCountByCondition("InitData", "id_ilid=" + id);
					if (count == 0) {
						baseDao.deleteByCondition("InitLog", "il_id=" + id);
						baseDao.deleteByCondition("InitNodes", "in_ilid=" + id);
					} else {
						baseDao.updateByCondition("InitLog", "il_count=" + count, "il_id=" + id);
					}
				}
			}
		} else {
			BaseUtil.showError("该数据已被删除或已转正式数据，无法删除.");
		}
	}

	/**
	 * 导出所有数据
	 */
	@Override
	public List<Map<String, Object>> getInitData(String condition) {
		SqlRowList rs = baseDao.queryForRowSet("select id_data from InitData where " + condition + " order by id_detno");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> data = FlexJsonUtil.fromJson(rs.getString(1));
			list.add(data);
		}
		return list;
	}

	/**
	 * 导出错误数据
	 */
	@Override
	public List<Map<String, Object>> getErrInitData(int id) {
		int count = baseDao.getCountByCondition("InitLog", "il_id=" + id + " AND il_toformal=0");// 非已转正式
		if (count == 1) {
			InputStream in = getResult(id);
			if (in != null) {
				String result = StringUtil.parserInputStream(in);
				String sql = "";
				if (result.length() > 0) {
					String[] nodes = result.split(",");
					int splitNum = (int) Math.ceil(nodes.length * 0.001);
					for (int i = 0; i < splitNum; i++) {
						String[] tmpNodes = null;
						if (i == splitNum - 1) {
							tmpNodes = new String[nodes.length - i * 1000];
							System.arraycopy(nodes, i * 1000, tmpNodes, 0, nodes.length - i * 1000);
						} else {
							tmpNodes = new String[1000];
							System.arraycopy(nodes, i * 1000, tmpNodes, 0, 1000);
						}
						StringBuffer sb = new StringBuffer();
						for (String n : tmpNodes) {
							if (n != null && n.indexOf(":") > 0) {
								if (sb.length() > 0)
									sb.append(",");
								sb.append(n.split(":")[0]);
							}
						}
						if (sb.length() == 0)
							sb.append("0");
						if (i == 0)
							sql += " and( id_id in (" + sb.toString() + ")";
						else
							sql += " or id_id in (" + sb.toString() + ")";
						if (i == splitNum - 1)
							sql += ")";
					}
					SqlRowList rs = baseDao.queryForRowSet("select id_data from InitData where id_ilid=" + id + sql);
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					while (rs.next()) {
						Map<String, Object> data = FlexJsonUtil.fromJson(rs.getString(1));
						list.add(data);
					}
					return list;
				}
			}
		}
		return null;
	}

	@Override
	public void clearInitData() {
		baseDao.execute("truncate table initdata");
		baseDao.execute("truncate table initlog");
		baseDao.execute("truncate table initnodes");
	}

	@Override
	public void clearBefore(String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select il_id from InitLog where il_caller=? AND il_toformal=0 and il_result is not null",
				caller);
		while (rs.next()) {
			deleteInitData(rs.getInt(1));
		}
	}

	/**
	 * 只显示错误行
	 */
	@Override
	public List<InitData> getErrDatas(int id, String condition) {
		int count = baseDao.getCountByCondition("InitLog", "il_id=" + id + " AND il_toformal=0");// 非已转正式
		if (count == 1) {
			InputStream in = getResult(id);
			if (in != null) {
				String result = StringUtil.parserInputStream(in);
				String sql = "";
				if (result.length() > 0) {
					String[] nodes = result.split(",");
					int splitNum = (int) Math.ceil(nodes.length * 0.001);
					for (int i = 0; i < splitNum; i++) {
						String[] tmpNodes = null;
						if (i == splitNum - 1) {
							tmpNodes = new String[nodes.length - i * 1000];
							System.arraycopy(nodes, i * 1000, tmpNodes, 0, nodes.length - i * 1000);
						} else {
							tmpNodes = new String[1000];
							System.arraycopy(nodes, i * 1000, tmpNodes, 0, 1000);
						}
						StringBuffer sb = new StringBuffer();
						for (String n : tmpNodes) {
							if (n != null && n.indexOf(":") > 0) {
								if (sb.length() > 0)
									sb.append(",");
								sb.append(n.split(":")[0]);
							}
						}
						if (sb.length() == 0)
							sb.append("0");
						if (i == 0)
							sql += " and( id_id in (" + sb.toString() + ")";
						else
							sql += " or id_id in (" + sb.toString() + ")";
						if (i == splitNum - 1)
							sql += ")";
					}
					try {
						List<InitData> sns = baseDao.getJdbcTemplate().query(
								"select B.* from (select A.*,ROWNUM RN from (select * from initdata  where id_ilid=" + id + sql
										+ " order by id_detno)A)B where " + condition, new BeanPropertyRowMapper<InitData>(InitData.class));
						return sns;
					} catch (EmptyResultDataAccessException exception) {
						return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> sysInitNavigation() {
		List<Map<String, Object>> list = baseDao.queryForList("select * from sysinitnavigation");
		return list;
	}

	@Override
	public List<String> getAdminInfo() {
		List<String> adminlist = new ArrayList<String>();
		String admin = (String) baseDao.getFieldDataByCondition("ENTERPRISE", "en_adminname", "1=1");
		String orgadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='行政人事部'");
		String saleadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='销售部'");
		String purchaseadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='采购部'");
		String reseachadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='研发部'");
		String financeadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='财务部'");
		String warehouseadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='仓库'");
		String productadmin = (String) baseDao.getFieldDataByCondition("HrOrg", "or_headmanname", "or_name='生产部'");
		adminlist.add(admin == null ? "" : admin);
		if (orgadmin == null || orgadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(orgadmin == null ? "" : orgadmin);
		}
		if (saleadmin == null || saleadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(saleadmin == null ? "" : saleadmin);
		}
		if (purchaseadmin == null || purchaseadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(purchaseadmin == null ? "" : purchaseadmin);
		}
		if (reseachadmin == null || reseachadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(reseachadmin == null ? "" : reseachadmin);
		}
		if (financeadmin == null || financeadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(financeadmin == null ? "" : financeadmin);
		}
		if (warehouseadmin == null || warehouseadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(warehouseadmin == null ? "" : warehouseadmin);
		}
		if (productadmin == null || productadmin == "") {
			adminlist.add(admin == null ? "" : admin);
		} else {
			adminlist.add(productadmin == null ? "" : productadmin);
		}
		return adminlist;
	}

	@Override
	public boolean checkData(String table, String value) {
		String sql = "select count(*) from " + table;
		String update = "update sysinitnavigation set initabled=1 where value='" + value + "'";
		SqlRowList srs = baseDao.queryForRowSet(sql.toString());
		if (srs.next()) {
			if (srs.getInt(1) == 0) {
				return false;
			} else {
				baseDao.execute(update);
				return true;
			}
		} else {
			try {
				throw new Exception("数据更新失败!");
			} catch (Exception e) {
				return false;
			}
		}
	}

	@Override
	@Transactional
	public void importInitDetail(List<InitDetail> details) {
		String caller = details.get(0).getId_caller();
		// 覆盖
		baseDao.deleteByCondition("InitDetail", "id_caller=?", caller);
		for (InitDetail detail : details) {
			detail.setId_id(baseDao.getSeqId("InitDetail_SEQ"));
		}
		baseDao.save(details);
	}

	@Override
	public void toDemo(int id, String caller) {
		try {
			baseDao.execute("update INITIALIZE set in_demo=(select LOB_CONCAT(id_data) from initdata where id_ilid=" + id
					+ ") where in_caller='" + caller + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> getDemoData(String caller) {
		Object demo = baseDao.getFieldDataByCondition("INITIALIZE", "in_demo", "in_caller='" + caller + "'");
		if (demo != null && demo != "" && demo != "null") {
			List<Map<String, Object>> list = FlexJsonUtil.fromJsonArray("[" + demo.toString() + "]", HashMap.class);
			return list;
		}
		return null;
	}

	@Override
	public void matchingCode(int id, String caller) {
		if(!("BOM".equals(caller)||"BOM!Additional".equals(caller)||"BOM!New".equals(caller))){
			return;
		}
		List<InitData> datas = getInitDatas("id_ilid=" + id);
		List<Integer> failureDetno = new ArrayList<Integer>();
		List<Integer> failureDetailDetno = new ArrayList<Integer>();
		for (InitData d : datas) {
			Map<Object,Object> data = BaseUtil.parseFormStoreToMap(d.getId_data());
			boolean matchingSuccess = false;
			//匹配母件料号
			if(data.get("bo_mothercode")==null||"".equals(data.get("bo_mothercode").toString().trim())){
				if(data.get("bo_mothername")==null||"".equals(data.get("bo_mothername").toString().trim())){
					failureDetno.add(d.getId_detno());
				}else if(data.get("bo_spec")==null||"".equals(data.get("bo_spec").toString().trim())){
					Object pr_code = baseDao.getFieldDataByCondition("PRODUCT", "PR_CODE", "PR_DETAIL='"+data.get("bo_mothername")+"' AND PR_SPEC IS NULL");
					if(pr_code==null){
						failureDetno.add(d.getId_detno());
					}
					data.put("bo_mothercode", pr_code);
					matchingSuccess = true;
				}else{
					Object pr_code = baseDao.getFieldDataByCondition("PRODUCT", "PR_CODE", "PR_DETAIL='"+data.get("bo_mothername")+"' AND PR_SPEC='"+data.get("bo_spec")+"'");
					if(pr_code==null){
						failureDetno.add(d.getId_detno());
					}
					data.put("bo_mothercode", pr_code);
					matchingSuccess = true;
				}
			}
			//匹配子件料号
			if(data.get("bd_soncode")==null||"".equals(data.get("bd_soncode").toString().trim())){
				if(data.get("bd_flowstyle_user")!=null){		
					baseDao.callProcedure("SP_BOM_MATCHPRODUCT", new Object[] {data.get("bd_soncode"),data.get("bd_sonname"),data.get("bd_sonspec"),data.get("bd_flowstyle_user"),d.getId_data()});
				}
				
				if(data.get("bd_sonname")==null||"".equals(data.get("bd_sonname").toString().trim())){
					failureDetailDetno.add(d.getId_detno());
				}else if(data.get("bd_sonspec")==null||"".equals(data.get("bd_sonspec").toString().trim())){
					Object pr_code = baseDao.getFieldDataByCondition("PRODUCT", "PR_CODE", "PR_DETAIL='"+data.get("bd_sonname")+"' AND PR_SPEC IS NULL");
					if(pr_code==null){
						failureDetailDetno.add(d.getId_detno());
					}
					data.put("bd_soncode", pr_code);
					matchingSuccess = true;
				}else{					
					Object pr_code = baseDao.getFieldDataByCondition("PRODUCT", "PR_CODE", "PR_DETAIL='"+data.get("bd_sonname")+"' AND PR_SPEC='"+data.get("bd_sonspec")+"'");
					if(pr_code==null){
						failureDetailDetno.add(d.getId_detno());
					}
					data.put("bd_soncode", pr_code);
					matchingSuccess = true;
				}
			}
			//母件或者子件有匹配成功的才更新
			if(matchingSuccess){			
				String json = BaseUtil.parseMap2Str(data);
				baseDao.updateByCondition("initdata", "id_data='"+json+"'", "id_id='"+d.getId_id()+"'");
			}
		}
		String errorStr = "";
		String errorDetailStr = "";
		if(failureDetno.size()>0){
			errorStr = "行号为："+failureDetno.toString()+"的母件物料号没有找到或名称、规格为空<br>";		
		}
		if(failureDetailDetno.size()>0){
			errorDetailStr = "行号为："+failureDetailDetno.toString()+"的子件物料号没有找到或名称、规格为空";	
		}
		if(!"".equals(errorStr)||!"".equals(errorDetailStr)){
			BaseUtil.showErrorOnSuccess(errorStr+errorDetailStr);
		}
	}
}
