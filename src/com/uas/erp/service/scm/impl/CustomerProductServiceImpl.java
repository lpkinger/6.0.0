package com.uas.erp.service.scm.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerProductService;

@Service
public class CustomerProductServiceImpl implements CustomerProductService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateCustomerProductById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String custCode = (String) store.get("cu_code");
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);

		// 判断物料对照关系中，是否存在相同的物料编号或相同的客户物料号（不存在一对多，多对一）
		// 如果存在，不做任何处理，直接返回错误。
		Object[] array = gstore.toArray();
		if(!baseDao.isDBSetting("Sale","allowCustprodcode")){

			for (int i = 0; i < array.length; i++) {
				HashMap<Object, Object> map = (HashMap<Object, Object>) array[i];
				String pr_CodeId = (String) map.get("pc_prodcode");
				String pr_custCode = (String) map.get("pc_custprodcode");
				String prCustCode = null;
				try {
					prCustCode = URLDecoder.decode(pr_custCode.replaceAll("%", "%25"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				for (int j = i + 1; j < array.length; j++) {
					HashMap<Object, Object> custProductrelation = (HashMap<Object, Object>) array[j];
					String product_CodeId = (String) custProductrelation.get("pc_prodcode");
					String cust_product_code = (String) custProductrelation.get("pc_custprodcode");
					try {
						String prodCustCode = URLDecoder.decode(cust_product_code.replaceAll("%", "%25"), "UTF-8");
						if (pr_CodeId.equals(product_CodeId)) {
							BaseUtil.showError("明细栏存在相同的物料号");
							return;
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 从数据库中匹配，这个数据是否存在
				Long sameProCode = sameProCode("PRODUCTCUSTOMER", "pc_custcode='" + custCode + "'and pc_prodcode='" + pr_CodeId + "'",
						gstore.get(i), gstore, pr_CodeId, false);
				if (sameProCode != null) {
					BaseUtil.showError("明细栏存在相同的物料号");
					return;
				}

			}
		}

		// 执行修改前的其它逻辑
		handlerService.beforeSave("Customer!ProductCustomer", new Object[] { store, gstore });

		// 修改ProductCustomer
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductCustomer", "pc_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pc_id") == null || s.get("pc_id").equals("") || s.get("pc_id").equals("0")
					|| Integer.parseInt(s.get("pc_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTCUSTOMER_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductCustomer", new String[] { "pc_id" }, new Object[] { id });
				gridSql.add(sql);
			}
			if(baseDao.checkIf("SaleDown left join SaledownDetail  on sd_said=sa_id", "sd_custprodcode='"+s.get("pc_custprodcode")+"' and sd_prodcode is null "
					+ "and sa_custcode='"+custCode+"'")) {
				baseDao.execute("update SaledownDetail set sd_prodcode='"+s.get("pc_prodcode")+"' where sd_custprodcode='"+s.get("pc_custprodcode")+
						"' and sd_said in (select sa_id from SaleDown where sa_custcode='"+custCode+"')");
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update productcustomer set pc_prodid=(select pr_id from product where pc_prodcode=pr_code) where pc_custid="
				+ store.get("cu_id"));
		baseDao.execute("update productcustomer set (pc_custcode,pc_custname)=(select cu_code,cu_name from customer where cu_id=pc_custid) where pc_custid="
				+ store.get("cu_id"));
		// 记录操作
		baseDao.logger.update("Customer!ProductCustomer", "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("Customer!ProductCustomer", new Object[] { store, gstore });
	}

	/**
	 * 2015年12月15日 查看数据库是否存在相同的记录
	 * 
	 * @param table
	 *            要搜索的表
	 * @param condition
	 *            条件
	 * @param s
	 *            当前正在匹配的记录
	 * @param gstore
	 *            需要保存的所有数据
	 * @param code
	 *            需要匹配的物料号 或客户物料号
	 * @param isCust
	 *            确定 code参数是物料号，还是客户物料号
	 * @author yujia
	 */
	private Long sameProCode(String table, String condition, Map<Object, Object> s, List<Map<Object, Object>> gstore, String code,
			boolean isCust) {
		String proCode = null;
		if (isCust) {
			proCode = "pc_custprodcode";
		} else {
			proCode = "pc_prodcode";
		}
		BigDecimal bigId = ((BigDecimal) baseDao.getFieldDataByCondition(table, "pc_id", condition));
		Long id = null;
		if (bigId != null) {
			id = bigId.longValue();
		}
		if (id != null) { // 能找到相同的记录
			if (s.get("pc_id") == null || s.get("pc_id").equals("") || s.get("pc_id").equals("0")
					|| Integer.parseInt(s.get("pc_id").toString()) == 0) {// 新添加的数据，id不存在
				// 新添加的数据与数据库已保存的数据相同,如果保存的这条数据没有被修改，则验证通过
				return getSameId(gstore, id, code, proCode);
			} else {
				if (id != new Long((String) s.get("pc_id")).longValue()) {
					// 存在相同的物料号，id不相同，代表该数据数据与之前保存的数据相同,如果这条保存的数据没有修改，返回这个id
					return getSameId(gstore, id, code, proCode);
				}
			}
		}
		return null;
	}

	/**
	 * @author yujia
	 * @param gstore
	 *            需要保存的数据
	 * @param id
	 *            要匹配的id
	 * @param code
	 *            物料号
	 * @param proCode
	 *            需要get的物料，是客户物料号，还是物料号
	 * @return 如果没有被修改，返回id;如果已经修改，返回null
	 */
	private Long getSameId(List<Map<Object, Object>> gstore, Long id, String code, String proCode) {
		for (Map<Object, Object> map : gstore) {
			if (!(map.get("pc_id") == null || map.get("pc_id").equals("") || map.get("pc_id").equals("0") || Integer.parseInt(map.get(
					"pc_id").toString()) == 0)) { // id 不存在的不需要比较
				Long pcId = new Long((String) map.get("pc_id"));
				if (pcId.longValue() == id.longValue()) {
					String product_code = (String) map.get(proCode);
					try {
						String productcode = URLDecoder.decode(product_code.replaceAll("%", "%25"), "UTF-8");
						if (!productcode.equals(code)) {
							return null;
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return id;
	}
}
