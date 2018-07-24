package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.ma.ShiftCheckService;

@Service
public class ShiftCheckServiceImpl implements ShiftCheckService{
	
	@Autowired
	private BaseDao baseDao;
	/**
	 * 清除之前检验记录
	 * 
	 * @param caller
	 */
	private void clearByCaller(String caller) {
		baseDao.deleteByCondition("BillError", "be_type='" + caller + "'");
	}
	final static String BE = "INSERT INTO BillError(be_code,be_class,be_type,be_date,be_checker,be_remark) VALUES(";
	/**
	 * 检测错误<br>
	 * 如果有错误数据，写入到BillError
	 * 
	 * @param sql
	 * @param args
	 * @param caller
	 * @param employee
	 * @param remark
	 * @param url
	 * @return
	 */
	private int isError(String sql, String type) {
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			Employee employee = SystemSession.getUser();
			List<String> sqls = new ArrayList<String>();
			while (rs.next()) {
				sqls.add(getErrorSql(rs.getString(1), rs.getString(2), type, employee.getEm_name(), rs.getString(3)));
			}
			baseDao.execute(sqls);
			return sqls.size();
		}
		return 0;
	}
	private String getErrorSql(String be_code, String be_class, String be_type, String be_checker, String be_remark) {
		StringBuffer sb = new StringBuffer(BE);
		sb.append("'").append(be_code).append("','").append(be_class).append("','").append(be_type)
				.append("',sysdate,'").append(be_checker).append("','").append(be_remark).append("')");
		return sb.toString();
	}
	private final static String CHK_A = "select ad_code || '明细行:' ||ad_detno, '请购单', '请购单已转数:'||ad_yqty||'<br>采购单数量:'||pd_qty from (select ad_code,ad_detno,ad_yqty,nvl((select sum(pd_qty) from purchasedetail where pd_source=ad_apid and pd_sourcedetail=ad_id),0) pd_qty from applicationdetail) where ad_yqty <> pd_qty";
	@Override
	public boolean ma_chk_a(String type) {
		clearByCaller(type);
		return isError(CHK_A, type) == 0;
	}
	private final static String CHK_B = "select pd_code || '明细行:' ||pd_detno, '采购单', '采购单已转数:'||pd_yqty||'<br>收料单数量:'||vad_qty from (select pd_code,pd_detno,nvl(pd_yqty,0) pd_yqty,nvl((select sum(vad_qty) from verifyapplydetail where vad_pucode=pd_code and vad_pudetno = pd_detno),0) vad_qty from purchasedetail) where pd_yqty <> vad_qty";
	@Override
	public boolean ma_chk_b(String type) {
		clearByCaller(type);
		return isError(CHK_B, type) == 0;
	}
	
	@Override
	public boolean ma_chk_c(String type) {
		clearByCaller(type);
		return false;
	}

	@Override
	public boolean ma_chk_d(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_e(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_f(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_g(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_h(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_i(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_j(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_k(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_l(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_m(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ma_chk_n(String type) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
