package com.uas.erp.service.scm.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.ProdAccountService;

@Service
public class ProdAccountServiceImpl implements ProdAccountService {

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

	/**
	 * 检测错误<br>
	 * 如果有错误数据,写入到BillError
	 * 
	 * @param sql
	 * @param args
	 * @param caller
	 * @param employee
	 * @param remark
	 * @param url
	 * @return
	 */
	private int isError(String sql, Object[] args, String type, String remark) {
		Employee employee = SystemSession.getUser();
		SqlRowList rs = baseDao.queryForRowSet(sql, args);
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			while (rs.next()) {
				String rem = rs.getString(3);
				rem = rem == null ? remark : rem;
				sqls.add(getErrorSql(rs.getGeneralString(1), rs.getGeneralString(2), type, employee.getEm_name(), rem));
				if (sqls.size() == 100)// 只记录前100条
					break;
			}
			baseDao.execute(sqls);
			return sqls.size();
		}
		return 0;
	}

	private final static String BE = "INSERT INTO BillError(be_code,be_class,be_type,be_date,be_checker,be_remark) VALUES(";

	private String getErrorSql(String be_code, String be_class, String be_type, String be_checker, String be_remark) {
		StringBuffer sb = new StringBuffer(BE);
		sb.append("'").append(be_code).append("','").append(be_class).append("','").append(be_type).append("',sysdate,'")
				.append(be_checker).append("','").append(be_remark).append("')");
		return sb.toString();
	}

	static final String SQL_A01 = "select distinct pi_inoutno,pi_class from prodinout,warehouse,prodiodetail where pd_piid=pi_id and to_char(pi_date,'yyyymm')=? and nvl(pi_statuscode,' ')='POSTED' and nvl(pi_vouchercode,' ')=' ' "
			+ "and pi_class not in ('出货单','销售退货单','采购验收单','采购验退单','委外验收单','委外验退单','用品验收单','用品验退单') and pd_whcode=wh_code and nvl(wh_nocost,0)=0 ";

	@Override
	public boolean scm_chk_a(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_A01, new Object[] { month }, type, "单据未制作凭证");
		return count == 0;
	}

	static final String SQL_B01 = "select distinct pi_inoutno,pi_class from prodinout where to_char(pi_date,'yyyymm')=? and nvl(pi_statuscode,' ')='POSTED' and nvl(pi_vouchercode,' ')<>' ' and nvl(pi_vouchercode,' ') not in (select vo_code from voucher where vo_yearmonth=?)";

	@Override
	public boolean scm_chk_b(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_B01, new Object[] { month, month }, type, "单据凭证号异常");
		return count == 0;
	}

	static final String SQL_C01 = "select '总金额不相等',' ','拨入:'||A1||'<br>拨出:'||A2 from (SELECT A1,A2 FROM (select round(sum(pd_inqty*pd_price),2) A1, round(sum(pd_outqty*pd_price),2) A2 from prodinout left join prodiodetail on pi_id=pd_piid where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('拨出单','拨入单')) WHERE nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_C02 = "select '总数量不相等',' ','拨入:'||A1||'<br>拨出:'||A2 from (SELECT A1,A2 FROM (select sum(pd_inqty) A1, sum(pd_outqty) A2 from prodinout left join prodiodetail on pi_id=pd_piid where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('拨出单','拨入单')) WHERE nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_C03 = "select '总主记录条数不相等',' ','拨入:'||A2||'<br>拨出:'||A1 from (SELECT A1,A2 FROM (select sum( case when pi_class='拨出单' then 1 else 0 end ) A1, sum(case when pi_class='拨入单' then 1 else 0 end) A2 from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('拨出单','拨入单')) where nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_C04 = "select '总明细记录条数不相等',' ','拨入:'||A2||'<br>拨出:'||A1 from (SELECT A1,A2 FROM (select sum( case when pd_piclass='拨出单' then 1 else 0 end ) A1, sum(case when pd_piclass='拨入单' then 1 else 0 end) A2 from prodinout,prodiodetail where pd_piid=pi_id and to_char(pi_date,'yyyymm')=? and pd_status>0 and pd_piclass in ('拨出单','拨入单')) where nvl(A1,0)<>NVL(A2,0))";

	@Override
	public boolean scm_chk_c(String type, Integer month) {
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_C01, new Object[] { month }, type, "拨入拨出总金额不相等");
		count += isError(SQL_C02, new Object[] { month }, type, "拨入拨出总数量不相等");
		count += isError(SQL_C03, new Object[] { month }, type, "拨入拨出总主记录条数不相等");
		count += isError(SQL_C04, new Object[] { month }, type, "拨入拨出总明细记录条数不相等");
		return count == 0;
	}

	static final String SQL_D01 = "select '总金额不相等',' ','拨入:'||A1||'<br>拨出:'||A2 from (SELECT A1,A2 FROM (select round(sum(pd_inqty*pd_price),2) A1, round(sum(pd_outqty*pd_price),2) A2 from prodinout left join prodiodetail on pi_id=pd_piid where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('销售拨出单','销售拨入单')) WHERE nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_D02 = "select '总数量不相等',' ','拨入:'||A1||'<br>拨出:'||A2 from (SELECT A1,A2 FROM (select sum(pd_inqty) A1, sum(pd_outqty) A2 from prodinout left join prodiodetail on pi_id=pd_piid where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('销售拨出单','销售拨入单')) WHERE nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_D03 = "select '总主记录条数不相等',' ','拨入:'||A2||'<br>拨出:'||A1 from (SELECT A1,A2 FROM (select sum( case when pi_class='销售拨出单' then 1 else 0 end ) A1, sum(case when pi_class='销售拨入单' then 1 else 0 end) A2 from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('销售拨出单','销售拨入单')) where nvl(A1,0)<>NVL(A2,0))";
	static final String SQL_D04 = "select '总明细记录条数不相等',' ','拨入:'||A2||'<br>拨出:'||A1 from (SELECT A1,A2 FROM (select sum( case when pd_piclass='销售拨出单' then 1 else 0 end ) A1, sum(case when pd_piclass='销售拨入单' then 1 else 0 end) A2 from prodinout,prodiodetail where pd_piid=pi_id and to_char(pi_date,'yyyymm')=? and pd_status>0 and pd_piclass in ('销售拨出单','销售拨入单')) where nvl(A1,0)<>NVL(A2,0))";

	@Override
	public boolean scm_chk_d(String type, Integer month) {
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_D01, new Object[] { month }, type, "销售拨入拨出总金额不相等");
		count += isError(SQL_D02, new Object[] { month }, type, "销售拨入拨出总数量不相等");
		count += isError(SQL_D03, new Object[] { month }, type, "销售拨入拨出总主记录条数不相等");
		count += isError(SQL_D04, new Object[] { month }, type, "销售拨入拨出总明细记录条数不相等");
		return count == 0;
	}

	static final String SQL_E01 = "select ab_code,pd_inoutno||'行'||pd_pdno,'发票上:'||round(nvl(abd_costprice,0),6)||'<br>验收单:'||round(nvl(pd_price,0),6) from apbill left join apbilldetail on abd_abid=ab_id left join prodiodetail on abd_sourcedetailid=pd_id where Pd_piClass In ('采购验收单','采购验退单','委外验收单','委外验退单') and pd_status=99 and abd_sourcekind='PRODIODETIL' and round(nvl(abd_costprice,0),6)<>round(nvl(pd_price,0),6) and to_char(ab_date,'yyyymm')=?";

	@Override
	public boolean scm_chk_e(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_E01, new Object[] { month }, type, "应付发票中成本单价跟出入库成本单价不一致");
		return count == 0;
	}

	static final String SQL_F01 = "select es_code,pd_inoutno||'行'||pd_pdno,'暂估上:'||round(nvl(esd_costprice,0),6)||'<br>验收单:'||round(nvl(pd_price,0),6) from estimate left join estimatedetail on esd_esid=es_id left join prodiodetail on esd_pdid=pd_id where Pd_piClass In ('采购验收单','采购验退单','委外验收单','委外验退单') and pd_status=99 and round(nvl(esd_costprice,0),6)<>round(nvl(pd_price,0),6) and to_char(es_date,'yyyymm')=?";

	@Override
	public boolean scm_chk_f(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_F01, new Object[] { month }, type, "暂估单中成本单价跟出入库成本单价不一致");
		return count == 0;
	}

	static final String SQL_G01 = "select pd_inoutno||'行'||pd_pdno,pd_piclass,'验收单:'||pd_qty||'<br>暂估:'||esd_qty||'<br>发票:'||abd_qty from (Select pd_inoutno,pd_pdno,pd_piclass,(Pd_Inqty-Pd_Outqty) Pd_Qty,(Select Nvl(Sum(Esd_Qty),0) From Estimatedetail Left Join Estimate On Es_Id=Esd_Esid Where Esd_Pdid=Pd_Id And Es_Statuscode='POSTED') Esd_Qty,(Select nvl(Sum(Abd_Qty),0) From Apbilldetail Left Join Apbill On Ab_Id=Abd_Abid Where Abd_Pdid=Pd_Id And Ab_Statuscode='POSTED' And abd_sourcekind='PRODIODETAIL') Abd_Qty From Prodiodetail Left Join Prodinout On Pi_Id=Pd_Piid Where To_Char(Pi_Date,'yyyymm')=? And Pi_Class In ('采购验收单','采购验退单','委外验收单','委外验退单') and pi_statuscode='POSTED') where pd_qty<>esd_qty+abd_qty order by pd_inoutno";

	@Override
	public boolean scm_chk_g(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_G01, new Object[] { month }, type, "验收单据数量与当月开票+暂估不一致");
		return count == 0;
	}

	static final String SQL_H01 = "select ab_code,pd_inoutno||'行'||pd_pdno,'发票上:'||round(nvl(abd_costprice,0),6)||'<br>出货单:'||round(nvl(pd_price,0),6) from arbill left join arbilldetail on abd_abid=ab_id left join prodiodetail on abd_sourcedetailid=pd_id where Pd_piClass in ('出货单','销售退货单') and pd_status=99 and abd_sourcekind='PRODIODETIL' and round(nvl(abd_costprice,0),6)<>round(nvl(pd_price,0),6) and to_char(ab_date,'yyyymm')=?";

	@Override
	public boolean scm_chk_h(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_H01, new Object[] { month }, type, "应收发票中成本单价跟出入库成本单价不一致");
		return count == 0;
	}

	static final String SQL_I01 = "select gs_code,pd_inoutno||'行'||pd_pdno,'发出商品上:'||round(nvl(gsd_costprice,0),6)||'<br>出货单:'||round(nvl(pd_price,0),6) from goodssend left join goodssenddetail on gsd_gsid=gs_id left join prodiodetail on gsd_pdid=pd_id where Pd_piClass In ('出货单','销售退货单') and pd_status=99 and round(nvl(gsd_costprice,0),6)<>round(nvl(pd_price,0),6) and to_char(gs_date,'yyyymm')=?";

	@Override
	public boolean scm_chk_i(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_I01, new Object[] { month }, type, "发出商品单中成本单价跟出入库成本单价不一致");
		return count == 0;
	}

	static final String SQL_J01 = "select pd_inoutno||'行'||pd_pdno,pd_piclass,'出货单:'||pd_qty||'<br>发出商品:'||gsd_qty||'<br>发票:'||abd_qty from (Select pd_inoutno,pd_pdno,pd_piclass,(Pd_outqty-Pd_inqty) Pd_Qty,(Select Nvl(Sum(gsd_Qty),0) From Goodssenddetail Left Join Goodssend On Gs_Id=Gsd_Gsid Where Gsd_Pdid=Pd_Id And Gs_Statuscode='POSTED') Gsd_Qty,(Select nvl(Sum(Abd_Qty),0) From Arbilldetail Left Join Arbill On Ab_Id=Abd_Abid Where Abd_Pdid=Pd_Id And Ab_Statuscode='POSTED' And abd_sourcekind='PRODIODETAIL') Abd_Qty From Prodiodetail Left Join Prodinout On Pi_Id=Pd_Piid Where To_Char(Pi_Date,'yyyymm')=? And Pi_Class In ('出货单','销售退货单') and pi_statuscode='POSTED') where pd_qty<>gsd_qty+abd_qty order by pd_inoutno";

	@Override
	public boolean scm_chk_j(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_J01, new Object[] { month }, type, "出货单据数量与当月开票+发出商品不一致");
		return count == 0;
	}

	static final String SQL_K01 = "select distinct pi_inoutno,pi_class from prodinout where pi_class in ('其它出库单','其它入库单') and to_char(pi_date,'yyyymm')>=? and pi_statuscode='POSTED' and "
			+ "(pi_class,pi_type,pi_departmentcode) not in (select pc_class,pc_type,pc_departmentcode from prodiocateset) ";

	@Override
	public boolean scm_chk_k(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_K01, new Object[] { month }, type, "没有设置了对方科目");
		return count == 0;
	}

	static final String SQL_L01 = "select pc_departmentcode,pc_class,pc_type,count(*) from prodiocateset group by pc_class,pc_type,pc_departmentcode having count(*)>1 order by 4 desc";

	@Override
	public boolean scm_chk_l(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_L01, new Object[] {}, type, "科目设置有重复");
		return count == 0;
	}

	// 检查是否有出入库单据出、入数量都不为0
	static final String SQL_M01 = "select  pi_inoutno||',第'||pd_pdno||'行',pi_class  from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_inqty>0 and pd_outqty>0 and pd_status=99";

	@Override
	public boolean scm_chk_m(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_M01, new Object[] { month }, type, "出、入库数量都不为0");
		return count == 0;
	}

	static final String SQL_N01 = "select pi_inoutno||',第'||pd_pdno||'行',pi_class from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(pd_prodcode,' ') not in (select nvl(pr_code,' ') from product)";

	@Override
	public boolean scm_chk_n(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_N01, new Object[] { month }, type, "料号不存在");
		return count == 0;
	}

	static final String SQL_O01 = "select distinct pr_code, pr_detail from prodinout,prodiodetail,product where pd_piid=pi_id and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and nvl(pr_stockcatecode,' ')=' '";
	static final String SQL_O02 = "select distinct wh_code, wh_description from prodinout,prodiodetail,warehouse where pd_piid=pi_id and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and nvl(wh_catecode,' ')=' '";

	@Override
	public boolean scm_chk_o(String type, Integer month) {
		clearByCaller(type);
		int count = 0;
		if (!baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			count = isError(SQL_O01, new Object[] { month }, type, "物料的存货科目没有设置");
		} else {
			count = isError(SQL_O02, new Object[] { month }, type, "仓库的存货科目没有设置");
		}
		return count == 0;
	}

	static final String SQL_P01 = "select pi_inoutno,pi_class,'日期:'||to_char(pi_date,'yyyy-mm-dd')||'未过账，录入人:'||pi_recordman from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode<>'POSTED'";

	@Override
	public boolean scm_chk_p(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_P01, new Object[] { month }, type, "");
		return count == 0;
	}

	static final String SQL_Q01 = "select pi_inoutno||',第'||pd_pdno||'行',pi_class,'日期:'||to_char(pi_date,'yyyy-mm-dd') from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(wh_nocost,0)<>0 and nvl(pd_price,0)<>0";

	@Override
	public boolean scm_chk_q(String type, Integer month) {
		clearByCaller(type);
		int count = isError(SQL_Q01, new Object[] { month }, type, "");
		return count == 0;
	}

	// --检查库存月结表金额与存货科目金额是否一致(存货科目按物料)
	static final String SQL_R01 = "select (CASE WHEN NVL(cate1,' ')=' ' THEN cate2 ELSE cate1 END) catecode,'','存货:'||ROUND(NVL(sum1,0),2)||'<br>总账:'||ROUND(NVL(sum2,0),2) from (select pr_stockcatecode cate1, sum(NVL(pwm_endamount,0)) sum1 from productwhmonth,product where pwm_prodcode=pr_code and pwm_yearmonth=? and pr_stockcatecode in (@CATE)  group by pr_stockcatecode) full join (select cm_catecode cate2,sum(NVL(cm_enddebit,0)+NVL(cm_endcredit,0)) sum2 from catemonth where cm_yearmonth=? and cm_catecode in (@CATE) group by cm_catecode) on cate1=cate2 where ROUND(NVL(sum1,0),2)<>ROUND(NVL(sum2,0),2) ORDER BY catecode";

	// --检查库存月结表金额与存货科目金额是否一致(存货科目按仓库)
	static final String SQL_S01 = "select (CASE WHEN NVL(cate1,' ')=' ' THEN cate2 ELSE cate1 END) catecode,'','存货:'||ROUND(NVL(sum1,0),2)||'<br>总账:'||ROUND(NVL(sum2,0),2) from (select wh_catecode cate1,sum(nvl(pwm_endamount,0)) sum1 from productwhmonth,warehouse where pwm_whcode=wh_code and pwm_yearmonth=? and wh_catecode in (@CATE)  group by wh_catecode) full join (select cm_catecode cate2,sum(nvl(cm_enddebit,0)+nvl(cm_endcredit,0)) sum2 from catemonth where cm_yearmonth=? and cm_catecode in (@CATE) group by cm_catecode) on cate1=cate2 where ROUND(NVL(sum1,0),2)<>ROUND(NVL(sum2,0),2) ORDER BY catecode";

	@Override
	public boolean scm_chk_r(String type, Integer month) {
		clearByCaller(type);
		String[] cates = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (cates == null || cates.length == 0)
			BaseUtil.showError("请先设置存货科目");
		String cateStr = CollectionUtil.toSqlString(cates);
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(SQL_S01.replace("@CATE", cateStr), new Object[] { month, month }, type, "存货期末结存金额与总账对应存货科目余额不一致");
			return count == 0;
		} else {
			int count = isError(SQL_R01.replace("@CATE", cateStr), new Object[] { month, month }, type, "存货期末结存金额与总账对应存货科目余额不一致");
			return count == 0;
		}
	}

	static final String CO_CHK_D = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_inqty,0)-NVL(pd_outqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0)) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),2)<>round(nvl(sum2,0),2)) where typename in ('生产领料单','生产补料单','生产退料单','完工入库单','拆件入库单','完工不良入库单','委外领料单','委外退料单','委外补料单','其它出库单','其它入库单','盘盈调整单','盘亏调整单','其它采购入库单','其它采购出库单','拨出单','拨入单','销售拨出单','销售拨入单','报废单','借货出货单','借货归还单') order by typename, catecode";
	static final String CO_CHK_D1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_inqty,0)-NVL(pd_outqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0)) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),2)<>round(nvl(sum2,0),2)) where typename in ('生产领料单','生产补料单','生产退料单','完工入库单','拆件入库单','完工不良入库单','委外领料单','委外退料单','委外补料单','其它出库单','其它入库单','盘盈调整单','盘亏调整单','其它采购入库单','其它采购出库单','拨出单','拨入单','销售拨出单','销售拨入单','报废单','借货出货单','借货归还单') order by typename, catecode";
	static final String CO_CHK_DC = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round(NVL(pd_orderqty,0)*(NVL(pd_price,0)-nvl(pd_orderprice,0)),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0)) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),2)<>round(nvl(sum2,0),2)) where typename ='成本调整单' order by typename, catecode";
	static final String CO_CHK_DC_1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round(NVL(pd_orderqty,0)*(NVL(pd_price,0)-nvl(pd_orderprice,0)),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0)) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),2)<>round(nvl(sum2,0),2)) where typename ='成本调整单' order by typename, catecode";

	@Override
	public boolean scm_chk_t(String type, Integer month) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = 0;
			count += isError(CO_CHK_D1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					"存货模块金额与总账模块金额不一致");
			count += isError(CO_CHK_DC_1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					"存货模块金额与总账模块金额不一致");
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = 0;
			count += isError(CO_CHK_D.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					"存货模块金额与总账模块金额不一致");
			count += isError(CO_CHK_DC.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					"存货模块金额与总账模块金额不一致");
			return count == 0;
		}
	}

	// --检查应付暂估与存货科目金额是否一致
	static final String SQL_U01 = "select ROUND(SUM(ROUND(esd_qty*esd_costprice+0.000000000000001,2)),2) from estimate,estimatedetail WHERE es_id=esd_esid AND to_char(es_date,'yyyymm')=? and es_statuscode='POSTED' ";
	static final String SQL_U02_WH = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应付暂估' and vd_catecode in (select wh_catecode from warehouse)";
	static final String SQL_U02_PR = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应付暂估' and vd_catecode in (select pr_stockcatecode from Product)";

	@Override
	public boolean scm_chk_u(String type, Integer month) {
		Employee employee = SystemSession.getUser();
		clearByCaller(type);
		double sum1 = 0;
		double sum2 = 0;
		SqlRowList rs1 = baseDao.queryForRowSet(SQL_U01, new Object[] { month });
		SqlRowList rs2 = null;
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			rs2 = baseDao.queryForRowSet(SQL_U02_WH, new Object[] { month });
		} else {
			rs2 = baseDao.queryForRowSet(SQL_U02_PR, new Object[] { month });
		}
		if (rs1.next()) {
			sum1 = rs1.getGeneralDouble(1);
		}
		if (rs2.next()) {
			sum2 = rs2.getGeneralDouble(1);
		}
		if (Math.abs(sum1 - sum2) > 1) {
			baseDao.execute(getErrorSql("", "期间:" + month, type, employee.getEm_name(), "暂估金额:" + sum1 + "<br>存货金额:" + sum2));
			return false;
		}
		return true;
	}

	// --检查应付发票（当月验收验退当月开票）与存货科目金额是否一致
	static final String SQL_V01 = "select ROUND(SUM(ROUND(abd_qty*abd_costprice+0.000000000000001,2)),2) from Apbill,ApbillDetail WHERE ab_id=abd_abid and to_char(ab_date,'yyyymm')=? and abd_sourcekind='PRODIODETAIL' and ab_statuscode='POSTED' and ab_class='应付发票'";
	static final String SQL_V02_WH = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应付发票' and vd_catecode in (select wh_catecode from warehouse) and vd_catecode in (@CATE)";
	static final String SQL_V02_PR = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应付发票' and vd_catecode in (select pr_stockcatecode from Product) and vd_catecode in (@CATE)";

	@Override
	public boolean scm_chk_v(String type, Integer month) {
		Employee employee = SystemSession.getUser();
		clearByCaller(type);
		double sum1 = 0;
		double sum2 = 0;
		String[] cates = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (cates == null || cates.length == 0)
			BaseUtil.showError("请先设置存货科目");
		String cateStr = CollectionUtil.toSqlString(cates);
		SqlRowList rs1 = baseDao.queryForRowSet(SQL_V01, new Object[] { month });
		SqlRowList rs2 = null;
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			rs2 = baseDao.queryForRowSet(SQL_V02_WH.replace("@CATE", cateStr), new Object[] { month });
		} else {
			rs2 = baseDao.queryForRowSet(SQL_V02_PR.replace("@CATE", cateStr), new Object[] { month });
		}
		if (rs1.next()) {
			sum1 = rs1.getGeneralDouble(1);
		}
		if (rs2.next()) {
			sum2 = rs2.getGeneralDouble(1);
		}
		if (Math.abs(sum1 - sum2) > 1) {
			baseDao.execute(getErrorSql("", "期间:" + month, type, employee.getEm_name(), "应付发票金额:" + sum1 + "<br>存货科目金额:" + sum2));
			return false;
		}
		return true;
	}

	// --检查应收发出商品与存货科目金额是否一致
	static final String SQL_W01 = "select ROUND(SUM(ROUND(gsd_costprice*gsd_qty+0.000000000000001,2)),2) from GoodsSend,GoodsSendDetail WHERE gs_id=gsd_gsid AND to_char(gs_date,'yyyymm')=? and gs_statuscode='POSTED'";
	static final String SQL_W02_WH = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='发出商品' and vd_catecode in (select wh_catecode from warehouse)";
	static final String SQL_W02_PR = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='发出商品' and vd_catecode in (select pr_stockcatecode from Product)";

	@Override
	public boolean scm_chk_w(String type, Integer month) {
		Employee employee = SystemSession.getUser();
		clearByCaller(type);
		BigDecimal sum1 = new BigDecimal(0);
		BigDecimal sum2 = new BigDecimal(0);
		SqlRowList rs1 = baseDao.queryForRowSet(SQL_W01, new Object[] { month });
		SqlRowList rs2 = null;
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			rs2 = baseDao.queryForRowSet(SQL_W02_WH, new Object[] { month });
		} else {
			rs2 = baseDao.queryForRowSet(SQL_W02_PR, new Object[] { month });
		}
		if (rs1.next()) {
			sum1 = rs1.getGeneralBigDecimal(1);
		}
		if (rs2.next()) {
			sum2 = rs2.getGeneralBigDecimal(1);
		}
		if (sum1.compareTo(sum2) != 0) {
			baseDao.execute(getErrorSql("", "期间:" + month, type, employee.getEm_name(),
					"发出商品金额:" + sum1.toString() + "<br>存货科目金额:" + sum2.toString()));
			return false;
		}
		return true;
	}

	// --检查应收发票（当月出货退货当月开票）与存货科目金额是否一致
	static final String SQL_X01 = "select ROUND(SUM(round(abd_costprice*abd_qty,2)),2) from Arbill,ArbillDetail WHERE ab_id=abd_abid and to_char(ab_date,'yyyymm')=? and nvl(abd_sourcekind,' ')<>'GOODSSEND' and ab_statuscode='POSTED' and ab_class='应收发票'";
	static final String SQL_X02_WH = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='主营业务成本' and vd_catecode in (select wh_catecode from warehouse)";
	static final String SQL_X02_PR = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='主营业务成本' and vd_catecode in (select pr_stockcatecode from Product)";
	static final String SQL_X03_WH = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应收发票' and vd_catecode in (select wh_catecode from warehouse)";
	static final String SQL_X03_PR = "select sum(vd_debit+vd_credit) from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source='应收发票' and vd_catecode in (select pr_stockcatecode from Product)";

	@Override
	public boolean scm_chk_x(String type, Integer month) {
		Employee employee = SystemSession.getUser();
		clearByCaller(type);
		BigDecimal sum1 = new BigDecimal(0);
		BigDecimal sum2 = new BigDecimal(0);
		SqlRowList rs1 = baseDao.queryForRowSet(SQL_X01, new Object[] { month });
		SqlRowList rs2 = null;
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
			if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
				rs2 = baseDao.queryForRowSet(SQL_X03_WH, new Object[] { month });
			} else {
				rs2 = baseDao.queryForRowSet(SQL_X03_PR, new Object[] { month });
			}
		} else {
			if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
				rs2 = baseDao.queryForRowSet(SQL_X02_WH, new Object[] { month });
			} else {
				rs2 = baseDao.queryForRowSet(SQL_X02_PR, new Object[] { month });
			}
		}
		if (rs1.next()) {
			sum1 = rs1.getGeneralBigDecimal(1);
		}
		if (rs2.next()) {
			sum2 = rs2.getGeneralBigDecimal(1);
		}
		if (sum1.compareTo(sum2) != 0) {
			baseDao.execute(getErrorSql("", "期间:" + month, type, employee.getEm_name(),
					"应收发票金额:" + sum1.toString() + "<br>存货科目金额:" + sum2.toString()));
			return false;
		}
		return true;
	}

	@Override
	public boolean scm_chk_y(String type, Integer month) {
		String remark = "未审核";
		clearByCaller(type);
		int count = isError(
				"select ms_code, ms_class, ms_date from MakeScrap where to_char(ms_date,'yyyymm')=? and nvl(ms_statuscode,' ')<>'AUDITED'",
				new Object[] { month }, type, remark);
		return count == 0;
	}

	@Override
	public boolean scm_chk_z(String type, Integer month) {
		String remark = "未结案";
		clearByCaller(type);
		int count = isError(
				"select ma_code,MA_STATUS,MA_FINISHSTATUS||'，制单人:'||ma_recorder from MAKE where to_char(ma_date,'yyyymm')=? and nvl(ma_statuscode,' ')='AUDITED' and nvl(ma_madeqty,0)>=nvl(ma_qty,0)  and to_char(ma_actenddate,'yyyymm')=?",
				new Object[] { month, month }, type, remark);
		return count == 0;
	}
}
