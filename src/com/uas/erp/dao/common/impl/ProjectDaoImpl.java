package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProjectPlan;

@Repository
public class ProjectDaoImpl extends BaseDao implements ProjectDao {
	static final String TURNPROJECTREVIEW = "SELECT prj_code,prj_name,prj_cost,prj_producttype,prj_text1,prj_text2,prj_text3,prj_text4,prj_text5,prj_text6,prj_text7,prj_text8,prj_text9,prj_text10,prj_text11"
			+ ",prj_text12,prj_text13,prj_text14,prj_text15,prj_date1,prj_date2,prj_date3,prj_date4,prj_date5,prj_date6,prj_date7,prj_date8,prj_date9,prj_date10,prj_number1,prj_number2,prj_number3,prj_number4,prj_number5,prj_assignto,prj_dept FROM project WHERE prj_id=?";
	static final String INSERPPROJECTREVIEW = "INSERT INTO ProjectReview(pr_id,pr_code,pr_producttype,pr_recorder,pr_reviewitem,pr_reviewtitle,pr_reviewresult,pr_prjid,pr_prjcode,pr_cost,pr_prjname,pr_prjtext1,pr_prjtext2,pr_prjtext3,pr_prjtext4,pr_prjtext5,pr_prjtext6,pr_prjtext7,pr_prjtext8,"
			+ "pr_prjtext9,pr_prjtext10,pr_prjtext11,pr_prjtext12,pr_prjtext13,pr_prjtext14,pr_prjtext15,pr_prjdate1,pr_prjdate2,pr_prjdate3,pr_prjdate4,pr_prjdate5,pr_prjdate6,pr_prjdate7,pr_prjdate8,pr_prjdate9,pr_prjdate10,pr_prjnumber1,pr_prjnumber2,pr_prjnumber3,pr_prjnumber4,pr_prjnumber5,pr_chargeperson,pr_chargedepart) VALUES ("
			+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public JSONTree getJSONResource(String condition) {
		String str = "SELECT * FROM ProjectPlan";
		String Sql = condition.equals("") ? str : str + " where " + condition;
		List<ProjectPlan> plans = getJdbcTemplate().query(Sql, new BeanPropertyRowMapper<ProjectPlan>(ProjectPlan.class));
		JSONTree tree = new JSONTree();
		tree.setCls("x-tree-cls-parent");
		tree.setText("项目计划");
		tree.setQtip("项目计划");
		tree.setExpanded(true);
		tree.setLeaf(false);
		List<JSONTree> treearray = new ArrayList<JSONTree>();
		if (plans.size() > 0) {
			for (ProjectPlan plan : plans) {
				JSONTree jt = new JSONTree(plan);
				treearray.add(jt);
			}
		}
		tree.setChildren(treearray);

		return tree;
	}

	/**
	 * 根据code查询projectplan-------查找code=workplan是否存在
	 * 
	 * @param code
	 * @return
	 */
	@Override
	public ProjectPlan getProjectPlanByCode(String code) {
		try {
			return getJdbcTemplate().queryForObject("select * from PROJECTPLAN where PRJPLAN_CODE=?",
					new BeanPropertyRowMapper<ProjectPlan>(ProjectPlan.class), code);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public String TurnProjectReview(int id) {
		SqlRowList sl = queryForRowSet(TURNPROJECTREVIEW, new Object[] { id });
		String reviewcode = "PR_" + sGetMaxNumber("ProjectReview", 2);
		StringBuffer log = new StringBuffer();
		
		if (sl.next()) {
			Object pr_id = getSeqId("ProjectReview_SEQ");
			String[] review = getReviewItem(sl.getString("prj_producttype"));
			execute(INSERPPROJECTREVIEW,
					new Object[] { pr_id, reviewcode, sl.getString("prj_producttype"),
							SystemSession.getUser().getEm_name(), review[0], review[1], review[2], id, sl.getObject("prj_code"),
							sl.getObject("prj_cost"), sl.getString("prj_name"), sl.getObject("prj_text1"), sl.getObject("prj_text2"),
							sl.getObject("prj_text3"), sl.getObject("prj_text4"), sl.getObject("prj_text5"), sl.getObject("prj_text6"),
							sl.getObject("prj_text7"), sl.getObject("prj_text8"), sl.getObject("prj_text9"), sl.getObject("prj_text10"),
							sl.getObject("prj_text11"), sl.getObject("prj_text12"), sl.getObject("prj_text13"), sl.getObject("prj_text14"),
							sl.getObject("prj_text15"), sl.getDate("prj_date1"), sl.getDate("prj_date2"), sl.getDate("prj_date3"),
							sl.getDate("prj_date4"), sl.getDate("prj_date5"), sl.getDate("prj_date6"), sl.getDate("prj_date7"),
							sl.getDate("prj_date8"), sl.getDate("prj_date9"), sl.getDate("prj_date10"), sl.getGeneralDouble("prj_number1"),
							sl.getGeneralDouble("prj_number2"), sl.getGeneralDouble("prj_number3"), sl.getGeneralDouble("prj_number4"),
							sl.getGeneralDouble("prj_number5"), sl.getObject("prj_assignto"), sl.getObject("prj_dept") });
			log.append("<a href=\"javascript:openUrl('jsps/plm/project/projectReview.jsp?formCondition=pr_idIS"
					+ pr_id + "&gridCondition=pp_pridIS"	+ pr_id + "')\">" + reviewcode + "</a>&nbsp;");
			log.append("<hr>");
		}
		return log.toString();
	}

	public String[] getReviewItem(String producttype) {
		SqlRowList sl = queryForRowSet("select ri_name,ri_type from  ReviewItem  where ri_productkind='" + producttype
				+ "' order by ri_detno");
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
		while (sl.next()) {
			sb1.append(sl.getString("ri_name"));
			sb2.append(sl.getString("ri_type"));
			sb3.append("0");
			if (sl.hasNext()) {
				sb1.append("#");
				sb2.append("#");
				sb3.append("#");
			}
		}
		return new String[] { sb1.toString(), sb2.toString(), sb3.toString() };
	}
}
