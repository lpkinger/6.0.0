package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProductKindDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.ProductKind;
import com.uas.erp.service.scm.ProductKindService;

import bsh.StringUtil;

@Service
public class ProductKindServiceImpl implements ProductKindService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProductKindDao productKindDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public void saveProductKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object subof = store.get("pk_subof");
		subof = subof == null ? 0 : subof;
		// 同一父级下的种类，编号不允许重复
		boolean bool = baseDao.checkByCondition("ProductKind", "pk_code='" + store.get("pk_code") + "' and pk_subof=" + subof);
		if (!bool)
			BaseUtil.showError("同一父级下的种类，编号不允许重复");
		// 名称不允许重复
		bool = baseDao.checkByCondition("ProductKind", "pk_name='" + store.get("pk_name") + "' and pk_subof=" + subof);
		if (!bool)
			BaseUtil.showError("同一父级下的种类，种类名称不允许重复");
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store);
		// 保存
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProductKind"));
		if (subof != null && !subof.equals(0)) {
			baseDao.updateByCondition("ProductKind", "pk_leaf='F'", "pk_id=" + subof);
		}
		baseDao.logger.save(caller, "pk_id", store.get("pk_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store);
	}

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public void deleteProductKind(int pk_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pk_id);
		// 删除
		deleteByPid(pk_id);
		// 记录操作
		baseDao.logger.delete(caller, "pk_id", pk_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pk_id);
	}

	private void deleteByPid(int pid) {
		// 是否已产生业务数据
		baseDao.delCheck("productkind", pid);
		boolean isleaf = baseDao.checkIf("ProductKind", "pk_id=" + pid + " AND pk_leaf='T'");
		baseDao.deleteByCondition("ProductKind", "pk_id=" + pid);
		if (!isleaf) {
			SqlRowList rs = baseDao.queryForRowSet("SELECT pk_id FROM ProductKind WHERE pk_subof=?", pid);
			while (rs.next()) {
				deleteByPid(rs.getInt(1));
			}
		}
	}

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public void updateProductKindById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object id = store.get("pk_id");
		Object subof = store.get("pk_subof");
		subof = subof == null ? 0 : subof;
		// 同一父级下的种类，编号不允许重复
		boolean bool = baseDao.checkByCondition("ProductKind", "pk_code='" + store.get("pk_code") + "' and pk_subof=" + subof
				+ " and pk_id<>" + id);
		if (!bool)
			BaseUtil.showError("同一父级下的种类，编号不允许重复");
		// 名称不允许重复
		bool = baseDao.checkByCondition("ProductKind", "pk_name='" + store.get("pk_name") + "' and pk_subof=" + subof + " and pk_id<>" + id);
		if (!bool)
			BaseUtil.showError("同一父级下的种类，种类名称不允许重复");
		// 修改前的编号和名称已被使用到
		SqlRowList rs = baseDao.queryForRowSet("select pk_code,pk_name from productkind where pk_id=?", id);
		if (rs.next()) {
			if (!rs.getGeneralString("pk_name").equals(store.get("pk_name")) || !rs.getGeneralString("pk_code").equals(store.get("pk_code"))) {
				String rel = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pre_code) from (select pre_code from productkind left join preproduct on case when pk_level=1 then pre_kind when pk_level=2 then pre_kind2 when pk_level=3 then pre_kind3 when pk_level=4 then pre_xikind else pre_kind end = pk_name where pk_id=? and pre_code is not null) where rownum <= 5",
								String.class, id);
				if (rel != null)
					BaseUtil.showError("种类已经被使用到，不允许修改编号和名称，相关物料申请单: <br>" + rel.replace(",", "<br>"));
				rel = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pr_code) from (select pr_code from productkind left join product on case when pk_level=1 then pr_kind when pk_level=2 then pr_kind2 when pk_level=3 then pr_kind3 when pk_level=4 then pr_xikind else pr_kind end = pk_name where pk_id=? and pr_code is not null) where rownum <= 5",
								String.class, id);
				if (rel != null)
					BaseUtil.showError("种类已经被使用到，不允许修改编号和名称，相关物料资料: <br>" + rel.replace(",", "<br>"));
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave("ProductKind", store);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProductKind", "pk_id"));
		// 记录操作
		baseDao.logger.update(caller, "pk_id", id);
		// 执行修改后的其它逻辑
		handlerService.afterSave("ProductKind", store);
	}

	@Override
	@Cacheable(value = "productkind", key = "#employee.em_master + '@'+#parentid  +#allKind+ 'getProductKind'")
	public List<JSONTree> getJsonTrees(Employee employee, int parentid, String allKind, String caller) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<ProductKind> list = productKindDao.getProductKindByParentId(parentid, allKind);
		for (ProductKind navigation : list) {
			tree.add(new JSONTree(navigation));
		}
		return tree;
	}

	@Override
	public synchronized String getProductKindNum(int id,String postfix) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProductKind", "PK_MAXNUM,pk_length", "pk_id=" + id);
		if(objs!=null && objs[0]!=null && objs[1]!=null ){
			int ret = Integer.parseInt(objs[0].toString());
			int length = Integer.parseInt(objs[1].toString());
			ret++;
			baseDao.updateByCondition("ProductKind", "PK_MAXNUM=" + ret, "pk_id=" + id);
			if(length!=-1){
				String number = "";
				length -= String.valueOf(ret).length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						number += "0";
					}
				}
				number += String.valueOf(ret);
				if(postfix != null && !postfix.equals("")){
					number += postfix.toString();
				}
				return number;
			}
		}
		return null;
	}

	@Override
	public String getProductKindNumByKind(String k1, String k2, String k3, String k4,String postfix) {
		if(k1 != null && !"".equals(k1)&& k2 != null && !"".equals(k2)&& k3 != null && !"".equals(k3)){
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select k3.pk_id pk_id,k3.pk_leaf pk_leaf,k1.pk_code||k2.pk_code||k3.pk_code pk_code from productkind k1,productkind k2,productkind k3 where k1.pk_name=? and k1.pk_level=1 and k2.pk_name=? and k2.pk_level=2 and k3.pk_name=? and k3.pk_level=3 and k1.pk_id=k2.pk_subof and k2.pk_id=k3.pk_subof"
						+ " and k1.pk_effective='有效' and k2.pk_effective='有效' and k3.pk_effective='有效'",
						k1, k2, k3);
		if (rs.next()) {
			String num = rs.getGeneralString("pk_code");
			String streamNumber = "";
			if ("F".equals(rs.getString("pk_leaf"))) {
				SqlRowList rd = baseDao.queryForRowSet("select pk_id,pk_code from ProductKind where pk_subof=" + rs.getInt("pk_id") + " and pk_effective='有效'"
						+ " and pk_name='" + ((k4 == null || "".equals(k4)) ? '无' : k4) + "' and nvl(pk_leaf,'T')='T'");
				if (rd.next()) {
					streamNumber = getProductKindNum(rd.getInt("pk_id"),postfix);
					num += rd.getGeneralString("pk_code") + (streamNumber==null?"":streamNumber);
				}
			} else {
				streamNumber = getProductKindNum(rs.getInt("pk_id"),postfix);
				num += (streamNumber==null?"":streamNumber);
			}
			return num;
		}
		return null;
		}else{Object[] objs=null;
			if((k2 == null || "".equals(k2))&&(k1 != null && !"".equals(k1)))
			objs =baseDao.getFieldsDataByCondition("ProductKind", "pk_id,pk_code", "pk_level=1 and pk_effective='有效' and nvl(pk_leaf,'T')='T' and pk_name='"+k1+"'");
			if((k3 == null || "".equals(k3))&&(k2 != null && !"".equals(k2))&&(k1 != null && !"".equals(k1)))
			objs=baseDao.getFieldsDataByCondition("(select k2.pk_id pk_id,k1.pk_code||k2.pk_code pk_code from productkind k1,productkind k2 where k2.pk_level=2 and k2.pk_effective='有效' and nvl(k2.pk_leaf,'T')='T' and k2.pk_name='"+k2+"' and k1.pk_id=k2.pk_subof and k1.pk_name='"+k1+"' and k1.pk_level=1 and k1.pk_effective='有效')", "pk_id,pk_code", "1=1");
			if (objs!=null && objs[0]!=null && objs[1]!=null)
			return objs[1]+getProductKindNum(Integer.parseInt(objs[0].toString()),postfix);
		    return null;}
	}

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public void setEffective(int id, Boolean bool) {
		if (!bool) {
			boolean allInvalid = baseDao.checkByCondition("ProductKind", "pk_subof=" + id + " and pk_effective='有效'");
			if (allInvalid)
				baseDao.execute("update ProductKind set pk_effective='无效' where pk_id=?", id);
			else
				BaseUtil.showError("存在有效的子类型，不能失效当前种类.");
		} else {
			boolean isParentInvalid = baseDao.checkIf("ProductKind", "pk_id=(select nvl(p.pk_subof,0) from productkind p where p.pk_id="
					+ id + ") and pk_effective='无效'");
			if (isParentInvalid)
				BaseUtil.showError("父级种类无效，请先将父级种类转有效");
			baseDao.execute("update ProductKind set pk_effective='有效' where pk_id=?", id);
		}
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转" + (bool ? "有效" : "无效"), "成功", "ProductKind|pi_id=" + id));
	}

	@Override
	public void updateProdLoss(int pk_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from ProductKind where pk_id=" + pk_id);
		if(rs.next()){
			int level = rs.getInt("pk_level");
			int pkId = pk_id;
			String[] str = new String[10];
			int i = 0;
			if (level > 1) {
				do {
					String sql = "select  pk1.pk_name  pk_name ,pk1.pk_id pk_id,pk1.pk_level pk_level  from  productkind pk1,productkind pk2 where pk1.pk_id=pk2.pk_subof and pk2.pk_id="
							+ pkId;
					SqlRowList sqr = baseDao.queryForRowSet(sql);
							if (sqr.next()) {
								str[i] = sqr.getString("pk_name");
								i++;
								pkId = sqr.getInt("pk_id");
								level = sqr.getInt("pk_level");
							}
				} while (level != 1 && i<5);
			}
			if (i > 3) {
				BaseUtil.showErrorOnSuccess("物料种类的级层超过4层，只按照前面4个节点对物料表中物料损耗进行批量更新");
			} else {
				Double lossrate = rs.getGeneralDouble("pk_lossrate");
				Double testlossrate = rs.getGeneralDouble("pk_testlossrate");
				Double exportlossrate = rs.getGeneralDouble("pk_exportlossrate");
				Double purclossrate = rs.getGeneralDouble("pk_purclossrate");
				String productSql = "";
				if (i == 0) {
					productSql = "update product set pr_lossrate=" + lossrate + ",pr_testlossrate=" + testlossrate + ",pr_exportlossrate="
							+ exportlossrate + ",pr_purclossrate=" + purclossrate + " where pr_kind='" + rs.getString("pk_name") + "'";
				} else if (i == 1) {
					productSql = "update product set pr_lossrate=" + lossrate + ",pr_testlossrate=" + testlossrate + ",pr_exportlossrate="
							+ exportlossrate + ",pr_purclossrate=" + purclossrate + " where pr_kind='" + str[0] + "' and pr_kind2='" + rs.getString("pk_name") + "'";
				} else if (i == 2) {
					productSql = "update product set pr_lossrate=" + lossrate + ",pr_testlossrate=" + testlossrate + ",pr_exportlossrate="
							+ exportlossrate + ",pr_purclossrate=" + purclossrate + " where pr_kind='" + str[1] + "' and pr_kind2='" + str[0] + "' and pr_kind3='"
							+ rs.getString("pk_name") + "'";
				} else if (i == 3) {
					productSql = "update product set pr_lossrate=" + lossrate + ",pr_testlossrate=" + testlossrate + ",pr_exportlossrate="
							+ exportlossrate + ",pr_purclossrate=" + purclossrate + " where pr_kind='" + str[2] + "'and pr_kind2='" + str[1] + "' and pr_kind3='" + str[0]
							+ "'and pr_xikind='" + rs.getString("pk_name") + "'";
				}
				baseDao.execute(productSql);
			}
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新物料损耗率", "更新成功", "ProductKind|pi_id=" + pk_id));
		}
	}

	@Override
	@CacheEvict(value = "productkind", allEntries = true)
	public ProductKind addProductKindByParent(int parentId) {
		// TODO Auto-generated method stub
		int pkId=baseDao.getSeqId("ProductKind_SEQ");
		ProductKind pkind=new ProductKind();
        pkind.setPk_id(pkId);
        pkind.setPk_subof(parentId);
        pkind.setPk_code("P"+pkId);
        pkind.setPk_name("新种类"+pkId);
        pkind.setPk_effective("有效");
        pkind.setPk_leaf("F");
		baseDao.save(pkind, "ProductKind");
		return pkind;
	}

	@Override
	public List<JSONTree> getJSONTreeBySearch(String search, Employee employee) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		Set<ProductKind> list = productKindDao.getProductKindBySearch(search);
		for (ProductKind s : list) {
			JSONTree ct = new JSONTree();
			if (s.getPk_subof() == 0) {
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}
	
	private JSONTree recursionFn(Collection<ProductKind> list, ProductKind s) {
		JSONTree jt = new JSONTree();
		jt.setId(String.valueOf(s.getPk_id()));
		jt.setParentId(String.valueOf(s.getPk_subof()));
		jt.setText(s.getPk_name());
		jt.setQtip(s.getPk_code());
		
		if (s.getPk_leaf().equals("F")) {
			if (s.getPk_subof() == 0) {
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setAllowDrag(false);
			jt.setLeaf(false);
			List<ProductKind> childList = getChildList(list, s);
			Iterator<ProductKind> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				ProductKind n = (ProductKind) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setAllowDrag(true);
			jt.setLeaf(true);
			jt.setChildren(new ArrayList<JSONTree>());
		}
		return jt;
	}



	// 获取子节点列表
	private List<ProductKind> getChildList(Collection<ProductKind> list, ProductKind s) {
		List<ProductKind> li = new ArrayList<ProductKind>();
		Iterator<ProductKind> it = list.iterator();
		while (it.hasNext()) {
			ProductKind n = (ProductKind) it.next();
			// 父id等于id时 有子节点 添加该条数据
			if ((n.getPk_subof()) == (s.getPk_id())) {
				li.add(n);
			}
		}
		return li;
	}
	
	@Override
	public List<Map<String, Object>> getPrKind(String tablename, String fields, String condition) {
		return baseDao.queryForList("select "+fields+ " from "+tablename +" where "+condition);
	}
	
	@Override
	public void auditProductKind(int pk_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductKind",
				"pk_statuscode", "pk_id=" + pk_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pk_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ProductKind",
				"pk_statuscode='AUDITED',pk_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pk_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',pk_AUDITDATE=sysdate,pk_effective='有效'", "pk_id=" + pk_id);
		// 记录操作
		baseDao.logger.audit(caller, "pk_id", pk_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pk_id);

	}

	@Override
	public void resAuditProductKind(int pk_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProductKind",
				"pk_statuscode", "pk_id=" + pk_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pk_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ProductKind",
				"pk_statuscode='ENTERING',pk_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pk_AUDITMAN='',pk_AUDITDATE=null,pk_effective='无效'", "pk_id="
						+ pk_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pk_id", pk_id);
		handlerService.afterResAudit(caller, pk_id);
	}

	@Override
	public void submitProductKind(int pk_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductKind",
				"pk_statuscode", "pk_id=" + pk_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pk_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ProductKind",
				"pk_statuscode='COMMITED',pk_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "',pk_effective='无效'", "pk_id="
						+ pk_id);
		// 记录操作
		baseDao.logger.submit(caller, "pk_id", pk_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pk_id);
	}

	@Override
	public void resSubmitProductKind(int pk_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductKind",
				"pk_statuscode", "pk_id=" + pk_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pk_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ProductKind",
				"pk_statuscode='ENTERING',pk_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pk_id="
						+ pk_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pk_id", pk_id);
		handlerService.afterResSubmit(caller, pk_id);
	}
}
