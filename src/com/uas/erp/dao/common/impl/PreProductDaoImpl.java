package com.uas.erp.dao.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PreProductDao;
import com.uas.erp.model.Key;

@Repository
public class PreProductDaoImpl extends BaseDao implements PreProductDao {

	@Autowired
	private TransferRepository transferRepository;

	@Override
	public int TurnProd(int id) {
		// 初始化对应的物料资料的主键Id
		Integer pr_id = getFieldValue("preproduct left join product on pr_code=trim(pre_code)", "pr_id", "pre_id=" + id
				+ " and nvl(pr_code,' ')<>' ' and nvl(pr_sourcecode,' ')<>pre_thisid", Integer.class);
		if (pr_id != null) {
			BaseUtil.showError("申请的物料已经存在，但是不是从本申请单产生");
		}
		pr_id = getFieldValue("product", "pr_id", "(pr_code,pr_sourcecode) in (select pre_code,pre_thisid from preproduct where pre_id=" + id + ")",
				Integer.class);
		if (pr_id != null) {
			// 料号已产生，只需要更新属性
			transferRepository.update("PreProduct", id, pr_id);
		} else {// 料号未产生
			// 新物料申请 转 物料资料
			Key key = transferRepository.transfer("PreProduct", id);
			pr_id = key.getId();
			execute("insert into productonhand(po_id,po_prodcode) select pr_id,pr_code from product where pr_id=?", pr_id);
			execute("update product set pr_auditdate=sysdate where pr_id=?",pr_id);
		}
		Object pre_period = getFieldDataByCondition("preproduct", "pre_period", "pre_thisid=(select pr_sourcecode from product where pr_id=" + pr_id +") and nvl(pre_manutype,' ') in ('MAKE','OSMAKE')");
		if(pre_period != null && Integer.parseInt(pre_period.toString()) > 0){
			execute("update product set pr_leadtime="+pre_period+" where pr_id=?", pr_id);
		}
		return pr_id;
	}
}
