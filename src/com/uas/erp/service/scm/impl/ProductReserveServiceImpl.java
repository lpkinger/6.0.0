package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ProductReserveService;

@Service
public class ProductReserveServiceImpl implements ProductReserveService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductReserve(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateProductReserveById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void RefreshProdMonthNew(String currentMonth, String caller) {
		String str = baseDao.callProcedure("Sp_gRefreshProdMonthNew", new Object[] { currentMonth });
		if (str != null && !str.equals("")) {
			BaseUtil.showError(str);
		}
	}

	static final String PRODUCTWHMONTHADJUST = "INSERT INTO PRODUCTWHMONTHADJUST(pwa_id, pwa_code, pwa_yearmonth, pwa_status, pwa_recorder,"
			+ "pwa_indate,pwa_date,pwa_statuscode)"
			+ " values (?,?,?,?,?,sysdate,sysdate,'ENTERING')";
	
	static final String PRODUCTWHMONTHADJUSTDETAIL = "INSERT INTO ProductWHMonthAdjustDetail (pwd_id, pwd_pwaid, pwd_detno, pwd_prid, pwd_prodcode,"
			+ "pwd_whcode,pwd_whname,pwd_oldamount,pwd_oldqty,pwd_amount)"
			+ " values (PRODUCTWHMONTHADJUSTDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?)";
	
	@Override
	public String turnProductWHMonthAdjust(String currentMonth, String caller) {
		int pwaid = baseDao.getSeqId("PRODUCTWHMONTHADJUST_SEQ");
		String code = baseDao.sGetMaxNumber("ProductWHMonthAdjust", 2);
		String log = null;
		SqlRowList rs = baseDao.queryForRowSet("select * from productwhmonth left join warehouse on pwm_whcode=wh_code left join product on pwm_prodcode=product.pr_code left join CATEGORY on wh_catecode=ca_code where nvl(pwm_endqty,0)=0 and nvl(pwm_endamount,0)<>0 and pwm_yearmonth=?", currentMonth);
		if(rs.hasNext()){
			boolean bool = baseDao.execute(
					PRODUCTWHMONTHADJUST,
					new Object[] { pwaid, code, currentMonth, BaseUtil.getLocalMessage("ENTERING"), SystemSession.getUser().getEm_name()});
			if(bool){
				int count = 1;
				while (rs.next()) {
					baseDao.execute(PRODUCTWHMONTHADJUSTDETAIL, new Object[]{pwaid, count++, rs.getInt("pr_id"), rs.getObject("pwm_prodcode"),
							rs.getObject("pwm_whcode"), rs.getObject("wh_description"), rs.getObject("pwm_endamount"), rs.getObject("pwm_endqty"),
							rs.getGeneralDouble("pwm_endamount")*(-1)});	
				}
				log = "转入成功,期末调整单号:" + "<a href=\"javascript:openUrl('jsps/co/inventory/productWHMonthAdjust.jsp?formCondition=pwa_idIS" + pwaid
						+ "&gridCondition=pwd_pwaidIS" + pwaid + "')\">" + code + "</a>&nbsp;";
			}
		} else {
			log = "没有需要处理的数据";
		}
		return log;
	}
}
