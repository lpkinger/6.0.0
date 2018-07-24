package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.SysUpdates;
import com.uas.erp.model.upgrade.Config;
import com.uas.erp.model.upgrade.ConfigProp;
import com.uas.erp.model.upgrade.DataCascade;
import com.uas.erp.model.upgrade.DataLink;
import com.uas.erp.model.upgrade.DataRelation;
import com.uas.erp.model.upgrade.Datalist;
import com.uas.erp.model.upgrade.DatalistCombo;
import com.uas.erp.model.upgrade.DatalistDetail;
import com.uas.erp.model.upgrade.Dbfindset;
import com.uas.erp.model.upgrade.DbfindsetDetail;
import com.uas.erp.model.upgrade.DbfindsetGrid;
import com.uas.erp.model.upgrade.DbfindsetUI;
import com.uas.erp.model.upgrade.DetailGrid;
import com.uas.erp.model.upgrade.DocumentSetup;
import com.uas.erp.model.upgrade.Form;
import com.uas.erp.model.upgrade.FormDetail;
import com.uas.erp.model.upgrade.GridButton;
import com.uas.erp.model.upgrade.InitDetail;
import com.uas.erp.model.upgrade.Initialize;
import com.uas.erp.model.upgrade.Interceptor;
import com.uas.erp.model.upgrade.JprocessDeploy;
import com.uas.erp.model.upgrade.JprocessSet;
import com.uas.erp.model.upgrade.Navigation;
import com.uas.erp.model.upgrade.PostStyle;
import com.uas.erp.model.upgrade.PostStyleDetail;
import com.uas.erp.model.upgrade.PostStyleStep;
import com.uas.erp.model.upgrade.RelativeSearch;
import com.uas.erp.model.upgrade.RelativeSearchForm;
import com.uas.erp.model.upgrade.RelativeSearchGrid;
import com.uas.erp.model.upgrade.SearchTemplate;
import com.uas.erp.model.upgrade.SearchTemplateGrid;
import com.uas.erp.model.upgrade.SearchTemplateProp;
import com.uas.erp.model.upgrade.SysSpecialPower;
import com.uas.erp.model.upgrade.Transfer;
import com.uas.erp.model.upgrade.TransferDetail;
import com.uas.erp.model.upgrade.UpColumn;
import com.uas.erp.model.upgrade.UpIndex;
import com.uas.erp.model.upgrade.UpObject;
import com.uas.erp.model.upgrade.UpSequence;
import com.uas.erp.model.upgrade.UpSql;
import com.uas.erp.model.upgrade.UpTable;
import com.uas.erp.model.upgrade.UpTrigger;
import com.uas.erp.service.common.UpgradeService;

/**
 * @author yingp
 *
 */
@Service
public class UpgradeServiceImpl implements UpgradeService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getUpgradePlans(Integer page, Integer limit, String filter) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("count", String.valueOf(limit));
		params.put("page", String.valueOf(page));
		params.put("filter", filter);
		params.put("sorting", "{\"modifyDate\":\"DESC\"}");
		params.put("type", "snapshot");
		try {
			Response response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/upgrade/plan", params, true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				return FlexJsonUtil.fromJson(response.getResponseText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<SysUpdates> getUpgradeLog(String[] planIds) {
		try {
			return baseDao.query("select * from sysupdates where plan_id in (" + CollectionUtil.toSqlString(planIds)
					+ ") order by plan_id,version desc,install_date desc", SysUpdates.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public boolean upgrade(String planId, String type, int version) {
		try {
			deleteUpgradePlan(planId);
			saveUpgradeConfigs(planId);
			saveUpgradeCascades(planId);
			saveUpgradeLinks(planId);
			saveUpgradeLists(planId);
			saveUpgradeCombos(planId);
			saveUpgradeDbfinds(planId);
			saveUpgradeDbfindGrids(planId);
			saveUpgradeDbfindUIs(planId);
			saveUpgradeGrids(planId);
			saveUpgradeSetup(planId);
			saveUpgradeForms(planId);
			saveUpgradeButtons(planId);
			saveUpgradeInits(planId);
			saveUpgradeInitItems(planId);
			saveUpgradeInterceptors(planId);
			saveUpgradeProcessDeploy(planId);
			saveUpgradeProcessSet(planId);
			saveUpgradeNavigation(planId);
			saveUpgradePosts(planId);
			saveUpgradePostDetails(planId);
			saveUpgradeRelations(planId);
			saveUpgradeRelatives(planId);
			saveUpgradeSearchs(planId);
			saveUpgradeSpecialPowers(planId);
			saveUpgradeTransfers(planId);
			saveUpgradeTables(planId);
			saveUpgradeIndexes(planId);
			saveUpgradeSequences(planId);
			saveUpgradeObjects(planId);
			saveUpgradeTriggers(planId);
			saveUpgradeSqls(planId);
			doUpdate(planId, type);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			deleteUpgradePlan(planId);
		}
		log(planId, version, type);
		return true;
	}

	/**
	 * 删除方案
	 * 
	 * @param planId
	 */
	private void deleteUpgradePlan(String planId) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("delete from upgrade$configprops where exists (select 1 from upgrade$configs where config_id=id and plan_id='" + planId
				+ "')");
		sqls.add("delete from upgrade$configs where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$datacascade where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$datalink where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$datalistdetail where exists (select 1 from upgrade$datalist where dld_dlid=dl_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$datalist where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$datalistcombo where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$datarelation where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$dbfindsetdetail where exists (select 1 from upgrade$dbfindset where dd_dsid=ds_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$dbfindset where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$dbfindsetgrid where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$dbfindsetui where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$detailgrid where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$documentsetup where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$formdetail where exists (select 1 from upgrade$form where fd_foid=fo_id and plan_id='" + planId
				+ "')");
		sqls.add("delete from upgrade$form where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$gridbutton where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$indexes where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$initdetail where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$initialize where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$interceptors where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$jprocessdeploy where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$jprocessset where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$navigation where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$objects where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$poststylestep where exists (select 1 from upgrade$poststyle where pss_psid=ps_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$poststyle where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$poststyledetail where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$relativesearchform where exists (select 1 from upgrade$relativesearch where rsf_rsid=rs_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$relativesearchgrid where exists (select 1 from upgrade$relativesearch where rsg_rsid=rs_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$relativesearch where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$searchtemplategrid where exists (select 1 from upgrade$searchtemplate where stg_stid=st_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$searchtemplateprop a where exists (select 1 from upgrade$searchtemplate b where a.st_id=b.st_id and b.plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$searchtemplate where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$sequences where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$sysspecialpower where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$columns where exists (select 1 from upgrade$tables where table_id=id and plan_id='" + planId + "')");
		sqls.add("delete from upgrade$tables where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$transferdetail where exists (select 1 from upgrade$transfers where td_trid=tr_id and plan_id='"
				+ planId + "')");
		sqls.add("delete from upgrade$transfers where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$triggers where plan_id='" + planId + "'");
		sqls.add("delete from upgrade$sqls where plan_id='" + planId + "'");
		baseDao.execute(sqls);
	}

	/**
	 * 记录本次更新操作
	 */
	private void log(String planId, int version, String type) {
		Long manage = SystemSession.getUser().getCurrentMaster().getMa_manageid();
		if (manage != null) {
			try {
				// 回执给manage
				Map<String, String> params = new HashMap<String, String>();
				params.put("planId", planId);
				params.put("masterId", String.valueOf(manage));
				params.put("version", String.valueOf(version));
				params.put("installType", type);
				HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/upgrade/plan/" + planId + "/log", params, true);
			} catch (Exception e) {
			}
		}
		baseDao.execute("delete from sysupdates where plan_id=?", planId);
		baseDao.execute("insert into sysupdates(plan_id,version,install_date,install_type,install_man) values (?,?,sysdate,?,?)", planId,
				version, type, SystemSession.getUser().getEm_name());
	}

	/**
	 * 参数配置
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeConfigs(String planId) throws Exception {
		List<Config> configs = getRemotePlanData(planId, "configs", Config.class);
		if (!CollectionUtil.isEmpty(configs)) {
			baseDao.save(configs);
			List<ConfigProp> props = new ArrayList<ConfigProp>();
			for (Config config : configs) {
				props.addAll(config.getProperties());
			}
			if (!CollectionUtil.isEmpty(props))
				baseDao.save(props);
		}
	}

	/**
	 * 级联关系
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeCascades(String planId) throws Exception {
		List<DataCascade> configs = getRemotePlanData(planId, "cascades", DataCascade.class);
		if (!CollectionUtil.isEmpty(configs)) {
			baseDao.save(configs);
		}
	}

	/**
	 * 字段链接库
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeLinks(String planId) throws Exception {
		List<DataLink> links = getRemotePlanData(planId, "links", DataLink.class);
		if (!CollectionUtil.isEmpty(links)) {
			baseDao.save(links);
		}
	}

	/**
	 * 列表
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeLists(String planId) throws Exception {
		List<Datalist> lists = getRemotePlanData(planId, "lists", Datalist.class);
		if (!CollectionUtil.isEmpty(lists)) {
			baseDao.save(lists);
			List<DatalistDetail> details = new ArrayList<DatalistDetail>();
			for (Datalist list : lists) {
				details.addAll(list.getDetails());
			}
			baseDao.save(details);
		}
	}

	/**
	 * 下拉框
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeCombos(String planId) throws Exception {
		List<DatalistCombo> combos = getRemotePlanData(planId, "combos", DatalistCombo.class);
		if (!CollectionUtil.isEmpty(combos)) {
			baseDao.save(combos);
		}
	}

	/**
	 * 关联关系
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeRelations(String planId) throws Exception {
		List<DataRelation> relations = getRemotePlanData(planId, "relations", DataRelation.class);
		if (!CollectionUtil.isEmpty(relations)) {
			baseDao.save(relations);
		}
	}

	/**
	 * 放大镜
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeDbfinds(String planId) throws Exception {
		List<Dbfindset> dbfinds = getRemotePlanData(planId, "dbfinds", Dbfindset.class);
		if (!CollectionUtil.isEmpty(dbfinds)) {
			baseDao.save(dbfinds);
			List<DbfindsetDetail> details = new ArrayList<DbfindsetDetail>();
			for (Dbfindset dbfind : dbfinds) {
				details.addAll(dbfind.getDbFindSetDetails());
			}
			baseDao.save(details);
		}
	}

	/**
	 * 放大镜对照关系
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeDbfindGrids(String planId) throws Exception {
		List<DbfindsetGrid> dbfindGrids = getRemotePlanData(planId, "dbfindGrids", DbfindsetGrid.class);
		if (!CollectionUtil.isEmpty(dbfindGrids)) {
			baseDao.save(dbfindGrids);
		}
	}

	/**
	 * 单表放大镜
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeDbfindUIs(String planId) throws Exception {
		List<DbfindsetUI> dbfindUIs = getRemotePlanData(planId, "dbfindUIs", DbfindsetUI.class);
		if (!CollectionUtil.isEmpty(dbfindUIs)) {
			baseDao.save(dbfindUIs);
		}
	}

	/**
	 * Grid
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeGrids(String planId) throws Exception {
		List<DetailGrid> grids = getRemotePlanData(planId, "grids", DetailGrid.class);
		if (!CollectionUtil.isEmpty(grids)) {
			baseDao.save(grids);
		}
	}

	/**
	 * 出入库单设置
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeSetup(String planId) throws Exception {
		List<DocumentSetup> setups = getRemotePlanData(planId, "setup", DocumentSetup.class);
		if (!CollectionUtil.isEmpty(setups)) {
			baseDao.save(setups);
		}
	}

	/**
	 * Form
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeForms(String planId) throws Exception {
		List<Form> forms = getRemotePlanData(planId, "forms", Form.class);
		if (!CollectionUtil.isEmpty(forms)) {
			baseDao.save(forms);
			List<FormDetail> details = new ArrayList<FormDetail>();
			for (Form form : forms) {
				details.addAll(form.getFormDetails());
			}
			baseDao.save(details);
		}
	}

	/**
	 * Grid配置按钮
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeButtons(String planId) throws Exception {
		List<GridButton> buttons = getRemotePlanData(planId, "buttons", GridButton.class);
		if (!CollectionUtil.isEmpty(buttons)) {
			baseDao.save(buttons);
		}
	}

	/**
	 * 初始化导航
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeInits(String planId) throws Exception {
		List<Initialize> inits = getRemotePlanData(planId, "inits", Initialize.class);
		if (!CollectionUtil.isEmpty(inits)) {
			baseDao.save(inits);
		}
	}

	/**
	 * 初始化配置
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeInitItems(String planId) throws Exception {
		List<InitDetail> initItems = getRemotePlanData(planId, "initItems", InitDetail.class);
		if (!CollectionUtil.isEmpty(initItems)) {
			baseDao.save(initItems);
		}
	}

	/**
	 * 逻辑配置
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeInterceptors(String planId) throws Exception {
		List<Interceptor> interceptors = getRemotePlanData(planId, "interceptors", Interceptor.class);
		if (!CollectionUtil.isEmpty(interceptors)) {
			baseDao.save(interceptors);
		}
	}

	/**
	 * 流程图
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeProcessDeploy(String planId) throws Exception {
		List<JprocessDeploy> processDeploies = getRemotePlanData(planId, "processDeploy", JprocessDeploy.class);
		if (!CollectionUtil.isEmpty(processDeploies)) {
			baseDao.save(processDeploies);
		}
	}

	/**
	 * 单据流程设置
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeProcessSet(String planId) throws Exception {
		List<JprocessSet> processSets = getRemotePlanData(planId, "processSet", JprocessSet.class);
		if (!CollectionUtil.isEmpty(processSets)) {
			baseDao.save(processSets);
		}
	}

	/**
	 * 系统导航
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeNavigation(String planId) throws Exception {
		List<Navigation> navigations = getRemotePlanData(planId, "navigations", Navigation.class);
		if (!CollectionUtil.isEmpty(navigations)) {
			baseDao.save(navigations);
		}
	}

	/**
	 * 同步公式
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradePosts(String planId) throws Exception {
		List<PostStyle> posts = getRemotePlanData(planId, "posts", PostStyle.class);
		if (!CollectionUtil.isEmpty(posts)) {
			baseDao.save(posts);
			List<PostStyleStep> steps = new ArrayList<PostStyleStep>();
			for (PostStyle post : posts) {
				steps.addAll(post.getSteps());
			}
			baseDao.save(steps);
		}
	}

	/**
	 * 同步公式字段对照表
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradePostDetails(String planId) throws Exception {
		List<PostStyleDetail> postDetails = getRemotePlanData(planId, "postDetails", PostStyleDetail.class);
		if (!CollectionUtil.isEmpty(postDetails)) {
			baseDao.save(postDetails);
		}
	}

	/**
	 * 关联查询
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeRelatives(String planId) throws Exception {
		List<RelativeSearch> relatives = getRemotePlanData(planId, "relatives", RelativeSearch.class);
		if (!CollectionUtil.isEmpty(relatives)) {
			baseDao.save(relatives);
			List<RelativeSearchForm> forms = new ArrayList<RelativeSearchForm>();
			List<RelativeSearchGrid> grids = new ArrayList<RelativeSearchGrid>();
			for (RelativeSearch relative : relatives) {
				forms.addAll(relative.getForms());
				grids.addAll(relative.getGrids());
			}
			baseDao.save(forms);
			baseDao.save(grids);
		}
	}

	/**
	 * 查询方案
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeSearchs(String planId) throws Exception {
		List<SearchTemplate> searchs = getRemotePlanData(planId, "searchs", SearchTemplate.class);
		if (!CollectionUtil.isEmpty(searchs)) {
			baseDao.save(searchs);
			List<SearchTemplateGrid> grids = new ArrayList<SearchTemplateGrid>();
			List<SearchTemplateProp> props = new ArrayList<SearchTemplateProp>();
			for (SearchTemplate search : searchs) {
				grids.addAll(search.getItems());
				props.addAll(search.getProperties());
			}
			baseDao.save(grids);
			if (!CollectionUtil.isEmpty(props))
				baseDao.save(props);
		}
	}

	/**
	 * 特殊权限库
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeSpecialPowers(String planId) throws Exception {
		List<SysSpecialPower> specialPowers = getRemotePlanData(planId, "specialPowers", SysSpecialPower.class);
		if (!CollectionUtil.isEmpty(specialPowers)) {
			baseDao.save(specialPowers);
		}
	}

	/**
	 * 转单公式
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeTransfers(String planId) throws Exception {
		List<Transfer> transfers = getRemotePlanData(planId, "transfers", Transfer.class);
		if (!CollectionUtil.isEmpty(transfers)) {
			baseDao.save(transfers);
			List<TransferDetail> details = new ArrayList<TransferDetail>();
			for (Transfer transfer : transfers) {
				details.addAll(transfer.getDetails());
			}
			baseDao.save(details);
		}
	}

	/**
	 * 表
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeTables(String planId) throws Exception {
		List<UpTable> tables = getRemotePlanData(planId, "tables", UpTable.class);
		if (!CollectionUtil.isEmpty(tables)) {
			baseDao.save(tables);
			List<UpColumn> columns = new ArrayList<UpColumn>();
			for (UpTable table : tables) {
				columns.addAll(table.getColumns());
			}
			baseDao.save(columns);
		}
	}

	/**
	 * 索引
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeIndexes(String planId) throws Exception {
		List<UpIndex> indexes = getRemotePlanData(planId, "indexes", UpIndex.class);
		if (!CollectionUtil.isEmpty(indexes)) {
			baseDao.save(indexes);
		}
	}

	/**
	 * 序列
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeSequences(String planId) throws Exception {
		List<UpSequence> sequences = getRemotePlanData(planId, "sequences", UpSequence.class);
		if (!CollectionUtil.isEmpty(sequences)) {
			baseDao.save(sequences);
		}
	}

	/**
	 * 触发器
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeTriggers(String planId) throws Exception {
		List<UpTrigger> triggers = getRemotePlanData(planId, "triggers", UpTrigger.class);
		if (!CollectionUtil.isEmpty(triggers)) {
			baseDao.save(triggers);
		}
	}

	/**
	 * 触发器
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeSqls(String planId) throws Exception {
		List<UpSql> sqls = getRemotePlanData(planId, "sqls", UpSql.class);
		if (!CollectionUtil.isEmpty(sqls)) {
			baseDao.save(sqls);
		}
	}

	/**
	 * 函数、过程等
	 * 
	 * @param planId
	 * @throws Exception
	 */
	private void saveUpgradeObjects(String planId) throws Exception {
		List<UpObject> objects = getRemotePlanData(planId, "objects", UpObject.class);
		if (!CollectionUtil.isEmpty(objects)) {
			baseDao.save(objects);
		}
	}

	private static <T> List<T> getRemotePlanData(String planId, String type, Class<T> cls) throws Exception {
		Response response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/develop/upgrade/plan/" + planId + "/" + type, null,
				true);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return FlexJsonUtil.fromJsonArray(response.getResponseText(), cls);
		}
		throw new Exception(response.getResponseText());
	}

	/**
	 * 执行最后更新操作
	 * 
	 * @param planId
	 * @param type
	 */
	private void doUpdate(String planId, String type) {
		baseDao.procedure("utl_upgrade.upgrade", new Object[] { planId, type });
	}

}
