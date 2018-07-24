package com.uas.erp.service.cost.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.CostAccountService;
import com.uas.erp.service.fa.CheckAccountService;

@Service
public class CostAccountServiceImpl implements CostAccountService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private CheckAccountService checkAccountService;

	@Autowired
	private VoucherDao voucherDao;

	final static String BE = "INSERT INTO BillError(be_code,be_class,be_type,be_date,be_checker,be_remark) VALUES(";
	static final String SQL_BF_B01 = "select pd_inoutno || '(明细:' || pd_pdno || ')',pd_piclass,'工单:'||pd_ordercode||'序号'||pd_orderdetno from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单') and (pd_ordercode,pd_orderdetno) not in (select mm_code,mm_detno from makematerial)";
	static final String SQL_BF_B02 = "select pd_inoutno || '(明细:' || pd_pdno || ')',pd_piclass,'工单:'||pd_ordercode from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and pi_class in ('完工入库单','拆件入库单','委外验收单','委外验退单') and pd_ordercode not in (select ma_code from make) and nvl(pd_ordercode,' ')<>'000000' ";
	static final String SQL_BF_C01 = "select c1.cd_makecode,'工单','当月期初:'||c1.cd_begincostamount||',上月末'||c2.cd_endcostamount from costdetail c1,costdetail c2 where c1.cd_yearmonth=? and c2.cd_yearmonth=? and c1.cd_makecode=c2.cd_makecode and c1.cd_begincostamount<>c2.cd_endcostamount order by c1.cd_makecode";
	static final String SQL_BF_D01 = "select c1.cd_makecode,'工单','当月期初:'||c1.cd_beginscrapamount||',上月末'||c2.cd_endscrapamount from costdetail c1,costdetail c2 where c1.cd_yearmonth=? and c2.cd_yearmonth=? and c1.cd_makecode=c2.cd_makecode and c1.cd_beginscrapamount<>c2.cd_endscrapamount order by c1.cd_makecode";
	static final String SQL_BF_E01 = "select c1.cd_makecode,'工单','当月期初:'||c1.cd_beginendqty||',上月末'||c2.cd_endendqty from costdetail c1,costdetail c2 where c1.cd_yearmonth=? and c2.cd_yearmonth=? and c1.cd_makecode=c2.cd_makecode and c1.cd_beginendqty<>c2.cd_endendqty order by c1.cd_makecode";
	static final String SQL_BF_F01 = "select c1.cdm_mmcode,'工单','序号:'||c1.cdm_mmdetno||',当月期初:'||round(nvl(c1.cdm_beginqty,0),2)||',上月末'||round(nvl(c2.cdm_endqty,0),2) from costdetailmaterial c1,costdetailmaterial c2,make where c1.cdm_yearmonth=? and c2.cdm_yearmonth=? and c1.cdm_mmcode=c2.cdm_mmcode and c1.cdm_mmdetno=c2.cdm_mmdetno and c1.cdm_mmcode=ma_code and round(nvl(c1.cdm_beginqty,0),2)<>round(nvl(c2.cdm_endqty,0),2) and nvl(ma_kind,' ')<>'拆件工单' order by c1.cdm_mmcode,c1.cdm_mmdetno";
	static final String SQL_BF_G01 = "select c1.cdm_mmcode,'工单','序号:'||c1.cdm_mmdetno||',当月期初:'||round(nvl(c1.cdm_beginamount,0),2)||',上月末'||round(nvl(c2.cdm_endamount,0),2) from costdetailmaterial c1,costdetailmaterial c2,make where c1.cdm_yearmonth=? and c2.cdm_yearmonth=? and c1.cdm_mmcode=c2.cdm_mmcode and c1.cdm_mmdetno=c2.cdm_mmdetno and c1.cdm_mmcode=ma_code and round(nvl(c1.cdm_beginamount,0),2)<>round(nvl(c2.cdm_endamount,0),2) and nvl(ma_kind,' ')<>'拆件工单' order by c1.cdm_mmcode,c1.cdm_mmdetno";
	static final String SQL_BF_H01 = "select c1.cdm_mmcode,'工单','序号:'||c1.cdm_mmdetno||',当月期初:'||round(nvl(c1.cdm_beginscrapamount,0),2)||',上月末'||round(nvl(c2.cdm_endscrapamount,0),2) from costdetailmaterial c1,costdetailmaterial c2 where c1.cdm_yearmonth=? and c2.cdm_yearmonth=? and c1.cdm_mmcode=c2.cdm_mmcode and c1.cdm_mmdetno=c2.cdm_mmdetno and round(nvl(c1.cdm_beginscrapamount,0),2)<>round(nvl(c2.cdm_endscrapamount,0),2) order by c1.cdm_mmcode,c1.cdm_mmdetno";
	static final String SQL_BF_I01 = "select ma_prodcode,ma_kind,'工单：'||ma_code||'，中心：'||ma_wccode from make left join product on ma_prodcode=pr_code where nvl(ma_tasktype,' ')<>'OS' and nvl(pr_standtime,0)=0 and nvl(ma_madeqty,0)>0 and exists (select 1 from prodinout,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pi_class='完工入库单' and pi_statuscode='POSTED' and to_char(pi_date,'yyyymm')=?)";
	static final String SQL_BF_J01 = "select pi_inoutno,pi_class from prodinout where to_char(pi_date,'yyyymm')=? and nvl(pi_statuscode,' ')<>'POSTED'";
	static final String SQL_BF_K01 = "select pi_inoutno,pi_class,'行号:'||pd_pdno||',批次:'||pd_batchcode from prodinout,prodiodetail,warehouse,product where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pd_status=99 and nvl(wh_nocost,0)=0 and pd_prodcode=pr_code and nvl(pr_manutype,' ')='PURCHASE' and nvl(pd_price,0)=0";
	static final String SQL_BF_L01 = "select count(1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨出单'";
	static final String SQL_BF_L02 = "select count(1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨入单'";
	static final String SQL_BF_L03 = "select nvl(sum(pd_outqty),0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨出单'";
	static final String SQL_BF_L04 = "select nvl(sum(pd_inqty),0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨入单'";
	static final String SQL_BF_L05 = "select round(nvl(sum(pd_outqty*pd_price),0),1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨出单'";
	static final String SQL_BF_L06 = "select round(nvl(sum(pd_inqty*pd_price),0),1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='拨入单'";
	static final String SQL_BF_M01 = "select count(1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨出单'";
	static final String SQL_BF_M02 = "select count(1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨入单'";
	static final String SQL_BF_M03 = "select nvl(sum(pd_outqty),0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨出单'";
	static final String SQL_BF_M04 = "select nvl(sum(pd_inqty),0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨入单'";
	static final String SQL_BF_M05 = "select round(nvl(sum(pd_outqty*pd_price),0),1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨出单'";
	static final String SQL_BF_M06 = "select round(nvl(sum(pd_inqty*pd_price),0),1) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pd_status=99 and pi_class='销售拨入单'";
	static final String SQL_BF_N01 = "select pi_inoutno,pi_class,'凭证:'||pi_vouchercode from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(pi_vouchercode,' ')<>' '";
	static final String SQL_BF_O01 = "select pi_inoutno,pi_class,'凭证:'||pi_vouchercode from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(pi_vouchercode,' ')<>' ' and nvl(pi_vouchercode,' ') not in (select vo_code from voucher where vo_yearmonth=?)";
	static final String SQL_BF_P01 = "select pi_inoutno,pi_class,'状态:'||pi_status from prodinout where pi_status<>'未过账' and pi_status<>'已过账' and to_char(pi_date,'yyyymm')=?";
	static final String SQL_BF_Q01 = "select ma_code,ma_kind,ma_prodcode from make where ma_prodcode not in (select nvl(pr_code,' ') from product) and ma_code in(select cd_makecode from costdetail where cd_yearmonth=?) order by ma_code";
	static final String SQL_BF_R01 = "select mm_code,'工单','行'||mm_detno||'物料:'||mm_prodcode from makematerial where mm_prodcode not in (select nvl(pr_code,' ') from product) and mm_code in(select cd_makecode from costdetail where cd_yearmonth=?) order by mm_code,mm_detno";
	static final String SQL_BF_S01 = "select ma_code,ma_kind from make where ma_tasktype='OS' and to_char(ma_date,'yyyymm')=? and nvl(ma_currency,' ')=' ' and nvl(ma_price,0)>0";
	static final String SQL_BF_S101 = "select pd_inoutno,pd_piclass,'行号:'||pd_pdno||',加工价:'||pd_orderprice||';委外单号:'||ma_code||',加工价:'||ma_price from make,prodiodetail,prodinout where pd_ordercode=ma_code and pd_piid=pi_id and pd_piclass in ('委外验收单','委外验退单') and ma_tasktype='OS' and to_char(pi_date,'yyyymm')=? and nvl(ma_price,0)<>nvl(pd_orderprice,0)";
	static final String SQL_BF_S201 = "select pd_inoutno,pd_piclass,'行号:'||pd_pdno||',税率:'||pd_taxrate||';委外单号:'||ma_code||',税率:'||ma_taxrate from make,prodiodetail,prodinout where pd_ordercode=ma_code and pd_piid=pi_id and pd_piclass in ('委外验收单','委外验退单') and ma_tasktype='OS' and to_char(pi_date,'yyyymm')=? and nvl(ma_taxrate,0)<>nvl(pd_taxrate,0)";
	static final String SQL_BF_T01 = "select cd_makecode,'工单','成本表:'||cd_beginendqty||',出入库:'||qty from (select cd_makecode,cd_beginendqty,(select sum(nvl(pd_inqty,0)-nvl(pd_outqty,0)) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cd_makecode and pi_statuscode='POSTED' and pd_piclass in ('完工入库单','委外验收单','委外验退单') and to_char(pi_date,'yyyymmdd')<?) qty from costdetail where cd_yearmonth=?) where nvl(cd_beginendqty,0)<>nvl(qty,0) order by cd_makecode";
	static final String SQL_BF_U01 = "select pi_inoutno,pi_class,'行:'||pd_pdno||',物料:'||pd_prodcode from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_class in ('生产领料单','委外领料单','生产补料单','委外补料单','生产退料单','委外退料单','完工入库单','委外验收单','委外验退单') and pd_prodcode not in (select nvl(pr_code,' ') from product)";
	static final String SQL_BF_V01 = "select ms_code,ms_class from MakeScrap where ms_class='生产报废单' and to_char(ms_date,'yyyymm')=? and ms_statuscode<>'AUDITED'";
	static final String SQL_BF_W01 = "select pi_inoutno,pi_class,'发票:'||ab_code||',凭证:'||ab_vouchercode from apbill left join apbilldetail on ab_id=abd_abid left join prodiodetail on abd_pdid=pd_id left join prodinout on pd_piid=pi_id where abd_sourcekind='PRODIODETAIL' and to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and ab_vouchercode is not null";
	static final String SQL_BF_W02 = "select pi_inoutno,pi_class,'暂估单号:'||es_code||',凭证:'||es_vouchercode from estimatedetail left join estimate on esd_esid=es_id left join prodiodetail on esd_pdid=pd_id left join prodinout on pd_piid=pi_id where to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and es_vouchercode is not null";
	static final String SQL_BF_X01 = "select pi_inoutno,pi_class,'发票:'||ab_code||',凭证:'||ab_costvouchercode from arbill left join arbilldetail on ab_id=abd_abid left join prodiodetail on abd_pdid=pd_id left join prodinout on pd_piid=pi_id where abd_sourcekind='PRODIODETAIL' and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and ab_costvouchercode is not null";
	static final String SQL_BF_Y01 = "select gs_code,gs_class,'凭证:'||gs_vouchercode from goodssend where to_char(gs_date,'yyyymm')=? and gs_vouchercode is not null";
	// TODO
	static final String SQL_AF_A01 = "select cd_makecode,'期间:'||cd_yearmonth, '成本表：'||cd_wccode||'，工单：'||ma_wccode from costdetail,make where ma_code=cd_makecode and cd_yearmonth=? and nvl(cd_wccode,' ')<>nvl(ma_wccode,' ') and ma_tasktype='MAKE'";
	static final String SQL_AF_B01 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and nvl(cd_maketype,' ')<>(select nvl(ma_tasktype,' ') from make where ma_code=cd_makecode)";
	static final String SQL_AF_C01 = "select cd_makecode || ' 产品编号:' ||cd_prodcode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and nvl(cd_prodcode,' ') not in (select pr_code from product)";
	static final String SQL_AF_D01 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and nvl(cd_makeqty,0)<>(select nvl(ma_qty,0) from make where ma_code=cd_makecode)";
	static final String SQL_AF_E01 = "select cd_makecode,'工单','成本表:'||cd_nowendqty||',出入库:'||qty from (select cd_makecode,cd_nowendqty,(select sum(nvl(pd_inqty,0)-nvl(pd_outqty,0)) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cd_makecode and pi_statuscode='POSTED' and pd_piclass in ('完工入库单','委外验收单','委外验退单') and to_char(pi_date,'yyyymm')=?) qty from costdetail where cd_yearmonth=?) where nvl(cd_nowendqty,0)<>nvl(qty,0) order by cd_makecode";
	static final String SQL_AF_F01 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and round(nvl(cd_nowscrapqty,0),0)<>nvl((select round(sum(cdm_nowscrapqty),0) from costdetailmaterial where cd_id=cdm_cdid),0)";
	static final String SQL_AF_F02 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and round(nvl(cd_nowscrapamount,0),2)<>nvl((select round(sum(cdm_nowscrapamount),2) from costdetailmaterial where cd_id=cdm_cdid),0)";
	static final String SQL_AF_G01 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and round(nvl(cd_nowgetamount,0),0)<>NVL(( SELECT ROUND(SUM(NVL(cdm_nowgetamount, 0) + NVL(cdm_nowaddamount, 0) - NVL(cdm_nowbackamount, 0)), 0) FROM costdetailmaterial WHERE cdm_cdid = cd_id), 0)";
	static final String SQL_AF_G02 = "select cd_makecode || ' 工作中心:' ||cd_wccode,'期间:'||cd_yearmonth from costdetail where cd_yearmonth=? and round(nvl(cd_nowgetamount,0),0)<>nvl((SELECT ROUND(SUM((NVL(pd_outqty, 0) - NVL(pd_inqty, 0)) * pd_price), 0) FROM prodinout,prodiodetail WHERE  pi_id = pd_piid AND cd_makecode = pd_ordercode AND pi_statuscode = 'POSTED' AND to_char(pi_date,'yyyymmdd') >= ? AND to_char(pi_date,'yyyymmdd') <= ? AND pi_class IN ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单')),0)";
	static final String SQL_AF_H01 = "select mf_wccode, sum(round(nvl(mf_amount,0),2)) mf_amount, sum(round(nvl(mf_laboramount,0),2)) mf_laboramount, sum(round(nvl(mf_weamount,0),2)) mf_weamount, sum(round(nvl(mf_deamount,0),2)) mf_deamount, sum(round(nvl(mf_otheramount,0),2)) mf_otheramount, sum(round(nvl(mf_dlaboramount,0),2)) mf_dlaboramount from manufactfee where mf_yearmonth=? and mf_wccode=? group by mf_wccode";
	static final String SQL_AF_H02 = "select cd_wccode,round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee2),2),round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee4),2),round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee7),2),round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee8),2),round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee9),2),round(sum((nvl(cd_nowendqty,0)-nvl(cd_nowendqtynocost,0))*cd_fee10),2) from costdetail where cd_yearmonth=? group by cd_wccode";
	static final String SQL_AF_I01 = "select cd_id,cd_yearmonth,cd_makecode,cd_wccode from costdetail where cd_yearmonth=? and round(cd_costprice,8)<>ROUND(NVL(cd_nowturnunitprice, 0) + NVL(cd_fee1, 0) + NVL(cd_fee2, 0) + NVL(cd_fee3, 0) + NVL(cd_fee4, 0) + NVL(cd_fee5, 0) + NVL(cd_fee6, 0) + NVL(cd_fee7, 0) + NVL(cd_fee8, 0) + NVL(cd_fee9, 0) + NVL(cd_fee10, 0), 8) and NVL(CD_TURNOUTAMOUNTMINUS,0)=0";
	static final String SQL_AF_J01 = "select cd_id,cd_yearmonth,cd_makecode,cd_makestatus from costdetail where cd_yearmonth=? and cd_makestatus = '已完工' and NVL(cd_beginendqty, 0) + NVL(cd_nowendqty, 0) < NVL(cd_makeqty, 0)";
	static final String SQL_AF_J02 = "select cd_id,cd_yearmonth,cd_makecode,cd_makestatus from costdetail,make where cd_makecode=ma_code and cd_yearmonth=? and cd_makestatus = '已结案' and to_char(ma_actenddate,'yyyymmdd') >?";
	static final String SQL_AF_J03 = "select cd_id,cd_yearmonth,cd_makecode,cd_makestatus from costdetail where cd_yearmonth=? and cd_makestatus = '已审核' and NVL(cd_beginendqty, 0) + NVL(cd_nowendqty, 0) >= NVL(cd_makeqty, 0)";
	static final String SQL_AF_K01 = "select distinct '工单' ||pd_ordercode,pd_piclass from prodinout,prodiodetail where pi_id=pd_piid and pi_statuscode='POSTED' and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_class in ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单','完工入库单','拆件入库单','委外验收单','委外验退单') and nvl(pd_ordercode,' ')<>'000000' and pd_ordercode not in (select cd_makecode from costdetail where cd_yearmonth=?)";
	static final String SQL_AF_L01 = "select cd_makecode,cd_costprice,pd_inoutno,pd_piclass,pd_price,pd_avprice from CostDetail,prodinout,ProdIODetail,warehouse where pi_id=pd_piid and cd_makecode=pd_ordercode and pd_whcode=wh_code and pd_piclass in ('完工入库单','委外验收单') and pd_status=99 and round(pd_price,8)<>round(cd_costprice,8) and to_char(pi_date,'yyyymm')=? and nvl(wh_nocost,0)=0 and cd_yearmonth=?";
	static final String SQL_AF_M01 = "select cd_makecode, '工单' from costdetail where cd_yearmonth=? and round(nvl(cd_fee1,0),4)<>NVL(( SELECT ROUND(ma_price * NVL(CurrencysMonth.cm_crrate, 1)/ (1 + NVL(ma_taxrate, 0)/100), 4) FROM make,CurrencysMonth WHERE cd_makecode = ma_code AND cm_yearmonth = ? AND cm_crname = ma_currency ), 0) and cd_maketype='OS'";
	static final String SQL_AF_N01 = "select cdm_mmcode,cdm_mmdetno,count(1) from costdetailmaterial left join make on ma_code=cdm_mmcode where cdm_yearmonth=? and nvl(ma_kind,' ')<>'拆件工单' group by cdm_mmcode,cdm_mmdetno having count(1)>1";
	static final String SQL_AF_O01 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_oneuseqty,4)||',工单:'||round(mm_oneuseqty,4) from costdetailmaterial,makematerial where cdm_yearmonth=? and mm_code=cdm_mmcode and mm_detno=cdm_mmdetno and round(mm_oneuseqty,4)<>round(cdm_oneuseqty,4)";
	static final String SQL_AF_P01 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_qty,4)||',工单:'||round(mm_qty,4) from costdetailmaterial,makematerial where cdm_yearmonth=? and mm_code=cdm_mmcode and mm_detno=cdm_mmdetno and round(cdm_qty,4)<>round(mm_qty,4)";
	static final String SQL_AF_Q01 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowgetqty,4)||'<br>领料单:'||round(outqty,4) from (select cdm_mmcode,cdm_mmdetno,cdm_nowgetqty,(select round(sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)),4) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产领料单','委外领料单','拆件入库单')) outqty from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowgetqty,4)<>round(outqty,4)";
	static final String SQL_AF_Q02 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowgetamount,2)||'<br>领料单:'||amount from (select cdm_mmcode,cdm_mmdetno,cdm_nowgetamount,(select round(sum(round((nvl(pd_outqty,0)-nvl(pd_inqty,0))*pd_price,2)),2) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产领料单','委外领料单','拆件入库单')) amount from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowgetamount,0)<>round(amount,0)";
	static final String SQL_AF_R01 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowaddqty,4)||'<br>补料单:'||round(outqty,4) from (select cdm_mmcode,cdm_mmdetno,cdm_nowaddqty,(select round(sum(nvl(pd_outqty,0)),4) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产补料单','委外补料单')) outqty from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowaddqty,4)<>round(outqty,4)";
	static final String SQL_AF_R02 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowaddamount,2)||'<br>补料单:'||amount from (select cdm_mmcode,cdm_mmdetno,cdm_nowaddamount,(select round(sum(round(nvl(pd_outqty,0)*pd_price,2)),2) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产补料单','委外补料单')) amount from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowaddamount,0)<>round(amount,0)";
	static final String SQL_AF_S01 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowbackqty,4)||'<br>退料单:'||round(inqty,4) from (select cdm_mmcode,cdm_mmdetno,cdm_nowbackqty,(select round(sum(pd_inqty),4) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产退料单','委外退料单')) inqty from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowbackqty,4)<>round(inqty,4)";
	static final String SQL_AF_S02 = "select cdm_mmcode||',行'||cdm_mmdetno,'工单','成本表:'||round(cdm_nowbackamount,2)||'<br>退料单:'||amount from (select cdm_mmcode,cdm_mmdetno,cdm_nowbackamount,(select round(sum(pd_inqty*pd_price),2) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cdm_mmcode and pd_orderdetno=cdm_mmdetno and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and pi_class in ('生产退料单','委外退料单')) amount from costdetailmaterial where cdm_yearmonth=?) where round(cdm_nowbackamount,0)<>round(amount,0)";
	static final String SQL_AF_T01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and round(cdm_price,8)<>round(NVL((NVL(cdm_beginamount, 0) + NVL(cdm_nowgetamount, 0) + NVL(cdm_nowaddamount, 0)) / (NVL(cdm_beginqty, 0) + NVL(cdm_nowgetqty, 0) + NVL(cdm_nowaddqty, 0)), 0),8) and (NVL(cdm_beginqty, 0) + NVL(cdm_nowgetqty, 0) + NVL(cdm_nowaddqty, 0)) <> 0";
	static final String SQL_AF_U01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and round(cdm_nowscrapqty,2)<>nvl((select round(sum(md_qty),2) from MakeScrap,MakeScrapDetail where ms_id=md_msid and ms_statuscode='AUDITED' and to_char(ms_indate,'yyyymmdd')>=? and to_char(ms_indate,'yyyymmdd')<=? and md_mmcode=cdm_mmcode and md_mmdetno=cdm_mmdetno),0)";
	static final String SQL_AF_V01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and round(cdm_totalscrapqty,2)<>nvl((select round(sum(md_qty),2) from MakeScrap,MakeScrapDetail where ms_id=md_msid and ms_statuscode='AUDITED' and to_char(ms_indate,'yyyymmdd')<=? and md_mmcode=cdm_mmcode and md_mmdetno=cdm_mmdetno),0)";
	static final String SQL_AF_W01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and cdm_nowmadeqty<>nvl((select cd_nowendqty from costdetail where cd_id=cdm_cdid),0)";
	static final String SQL_AF_X01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial left join costdetail on cdm_cdid=cd_id where cdm_yearmonth=? and exists (select 1 from makematerial where cdm_mmcode = mm_code and cdm_mmdetno = mm_detno and (nvl(cd_makeqty, 0) - nvl(cd_beginendqty, 0)) > 0 and cdm_unitqty<>nvl((nvl(mm_qty, 0) - nvl(cdm_beginturnoutqty, 0)) / (nvl(cd_makeqty, 0) - nvl(cd_beginendqty, 0)), 0))";
	static final String SQL_AF_Y01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and round(cdm_endqty,2)<>round(NVL(cdm_beginqty, 0) + NVL(cdm_nowgetqty, 0) - NVL(cdm_nowbackqty, 0) + NVL(cdm_nowaddqty, 0) - NVL(cdm_nowturnoutqty, 0) - NVL(cdm_nowscrapqty, 0),2)";
	static final String SQL_AF_Z01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and ABS(ROUND(CDM_ENDAMOUNT,1)-(round(NVL(cdm_beginamount, 0) + NVL(cdm_nowgetamount, 0) - NVL(cdm_nowbackamount, 0) + NVL(cdm_nowaddamount, 0) - NVL(cdm_nowturnoutamount, 0) - NVL(cdm_nowscrapamount, 0),1)))>0.1";
	static final String SQL_AF_AA01 = "select cdm_mmcode||',行'||cdm_mmdetno from costdetailmaterial where cdm_yearmonth=? and abs(round(cdm_endscrapamount,1)-round(NVL(cdm_beginscrapamount, 0) + NVL(cdm_nowscrapamount, 0) - NVL(cdm_nowscrapturnoutamount, 0),1))>0.1";
	static final String SQL_AF_AB01 = "select cdm_mmcode,'工单','序号:'||cdm_mmdetno from costdetailmaterial left join make on ma_code=cdm_mmcode where cdm_yearmonth=? and nvl(ma_kind,' ')<>'拆件工单' group by cdm_mmcode,cdm_mmdetno having count(1)>1 order by cdm_mmcode,cdm_mmdetno";
	static final String SQL_AF_AC01 = "select cdm_mmcode,'工单','序号:'||cdm_mmdetno||'料号'||cdm_prodcode from costdetailmaterial where cdm_yearmonth=? and nvl(cdm_prodcode,' ') not in (select nvl(pr_code,' ') from product)";
	static final String SQL_AF_AC02 = "select cdm_mmcode,'工单','序号:'||cdm_mmdetno||'料号'||cdm_prodcode from costdetailmaterial where cdm_yearmonth=? and nvl(cdm_prodid,0) not in (select nvl(pr_id,0) from product)";
	static final String SQL_AF_AD01 = "select cd_makecode,'工单','成本表:'||round(nvl(cd_nowgetamount,0),2)||',出入库:'||round(nvl(amount,0),2) from (select cd_makecode,cd_nowgetamount,(select sum(round((nvl(pd_outqty,0)-nvl(pd_inqty,0))*pd_price,2)) from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode=cd_makecode and pi_statuscode='POSTED' and pd_piclass in ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单','拆件入库单') and to_char(pi_date,'yyyymm')=?) amount from costdetail where cd_yearmonth=?) where round(nvl(cd_nowgetamount,0),0)<>round(nvl(amount,0),0) order by cd_makecode";
	static final String SQL_AF_AE02 = "select cd_makecode,'工单','成本表:'||cd_nowgetamount||'<br>出入库:'||cd_nowgetamountio||'<br>差异:'||(cd_nowgetamountio-cd_nowgetamount) from costdetail where cd_yearmonth=? and round(cd_nowgetamount,2)<>round(cd_nowgetamountio,2) order by abs(cd_nowgetamountio-cd_nowgetamount) desc";
	static final String SQL_AF_AF01 = "select cd_makecode,'工单','期末完工:'||nvl(cd_endendqty,0)||'<br>期初完工数:'||nvl(cd_beginendqty,0)||'<br>本期完工数:'||nvl(cd_nowendqty,0) from costdetail where cd_yearmonth=? and nvl(cd_endendqty,0)<>nvl(cd_beginendqty,0)+nvl(cd_nowendqty,0)";
	static final String SQL_AF_AG01 = "select cd_makecode,cd_maketype,'工单:'||cd_makeqty||',期末完工:'||cd_endendqty from costdetail where cd_yearmonth=? and nvl(cd_endendqty,0)>nvl(cd_makeqty,0)";
	static final String SQL_AF_AH01 = "select '工单：'||ma_code,pd_piclass||':'||pd_inoutno||'，序号：'||pd_pdno, pd_piclass||':'||pd_orderprice||',工单:'||ma_price from (select pd_piclass, pd_inoutno,pd_pdno,pd_orderprice,pd_ordercode,ma_price,ma_code from ((select pd_piclass, pd_inoutno,pd_pdno, round(nvl(pd_orderprice,0),6) pd_orderprice,pd_ordercode from ProdIODetail,ProdInOut where pd_piid=pi_id and nvl(pd_ordercode,' ')<>'000000' and pd_piclass in ('委外验收单','委外验退单') and to_char(pi_date,'yyyymm')=?) left join ( select ma_code, round(nvl(ma_price,0),6) ma_price from make) on pd_ordercode=ma_code) where ma_price<>pd_orderprice)";
	static final String SQL_AF_AI01 = "select cd_makecode,'工单','数量:'||cd_nowendqty||',成本价:'||cd_costprice from costdetail where cd_yearmonth=? and nvl(cd_costprice,0)<0 order by cd_costprice";
	static final String SQL_AF_AJ01 = "select cd_makecode,'工单','数量:'||cd_nowendqty||',材料成本:'||nvl(cd_material,0) from costdetail where cd_yearmonth=? and nvl(cd_material,0)<0 and nvl(cd_nowendqty,0)<>0 order by cd_material";
	static final String SQL_AF_AK01 = "select cd_makecode,'工单','成本表:'||cd_standardtime||',物料:'||to_char(pr_standtime) from costdetail,product where cd_yearmonth=? and cd_prodcode=pr_code and cd_standardtime<>pr_standtime";
	static final String SQL_AF_AL01 = "select cd_makecode,'工单','总工时:'||round(cd_totaltime,2)||','||round(cd_standardtime * cd_nowendqty,2) from costdetail,product where cd_yearmonth=? and cd_prodcode=pr_code and round(cd_totaltime,2) <> round(cd_standardtime * cd_nowendqty,2)";
	static final String SQL_AF_AM01 = "select '物料：'||pwm_prodcode, '期间：'||pwm_yearmonth from productwhmonth where nvl(pwm_endqty,0)=0 and nvl(pwm_endamount,0)<>0 and pwm_yearmonth=?";
	static final String SQL_AF_AN01 = "select cdm_mmcode,'工单','序号:'||cdm_mmdetno||'料号'||cdm_prodcode from costdetail,costdetailmaterial where cd_id=cdm_cdid and cdm_yearmonth=? and round(cdm_nowturnoutqty,2)<>round((case when cd_makestatus = '已完工' or cd_makestatus = '已结案' or nvl(cd_nowendqty,0) + nvl(cd_beginendqty,0) >= nvl(cd_makeqty,0) then NVL(cdm_beginqty, 0) + NVL(cdm_nowgetqty, 0) - NVL(cdm_nowbackqty, 0) + NVL(cdm_nowaddqty, 0) - NVL(cdm_nowscrapqty, 0) when cd_nowendqty=0 then 0 else NVL(cd_nowendqty, 0) *cdm_unitqty end),2)";
	static final String SQL_AF_AO01 = "select cd_makecode,'工单','成本表:'||cd_nowscrapturnoutamount||',月结表:'||amount from ((select round(cd_nowscrapturnoutamount,2) cd_nowscrapturnoutamount,cd_makecode,cd_id from costdetail where cd_yearmonth=?) full join (select ROUND(SUM(NVL(cdm_nowscrapturnoutamount, 0)), 2) amount, cdm_mmcode, cdm_cdid FROM CostDetailMaterial where cdm_yearmonth=? group by cdm_mmcode, cdm_cdid) on cdm_cdid = cd_id and cd_makecode=cdm_mmcode) where cd_nowscrapturnoutamount<>amount";

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
	private int isError(String sql, Object[] args, String type, Employee employee, String remark, Boolean all) {
		SqlRowList rs = baseDao.queryForRowSet(sql, args);
		int maxSize = all == null || all == false ? 100 : 50000;// 默认只记录前100条
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			while (rs.next()) {
				String rem = rs.getString(3);
				rem = rem == null ? remark : rem;
				sqls.add(getErrorSql(rs.getGeneralString(1), rs.getGeneralString(2), type, employee.getEm_name(), rem));
				if (sqls.size() == maxSize)
					break;
			}
			baseDao.execute(sqls);
			return sqls.size();
		}
		return 0;
	}

	private String getErrorSql(String be_code, String be_class, String be_type, String be_checker, String be_remark) {
		StringBuffer sb = new StringBuffer(BE);
		sb.append("'").append(be_code).append("','").append(be_class).append("','").append(be_type).append("',sysdate,'")
				.append(be_checker).append("','").append(be_remark).append("')");
		return sb.toString();
	}

	/**
	 * 分币别比较数量,金额
	 * 
	 * @param sql1
	 * @param objs1
	 * @param desc1
	 * @param sql2
	 * @param objs2
	 * @param desc2
	 * @return 如果两者有差异，返回错误信息，否则为空
	 */
	private String compare(String sql1, Object[] objs1, String desc1, String sql2, Object[] objs2, String desc2) {
		Double count1 = baseDao.getJdbcTemplate().queryForObject(sql1, Double.class, objs1);
		Double count2 = baseDao.getJdbcTemplate().queryForObject(sql2, Double.class, objs2);
		if (Math.abs(NumberUtil.formatDouble(count1, 2) - NumberUtil.formatDouble(count2, 2)) >= 0.1) {
			return desc1 + ":" + NumberUtil.parseBigDecimal(count1) + "<br>" + desc2 + ":" + NumberUtil.parseBigDecimal(count2);
		}
		return null;
	}

	/**
	 * 清除之前检验记录
	 * 
	 * @param caller
	 */
	private void clearByCaller(String caller) {
		baseDao.deleteByCondition("BillError", "be_type='" + caller + "'");
	}

	@Override
	public boolean co_chk_before_a(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		String freezemonth = baseDao.getDBSetting("freezeMonth");
		boolean bool = true;
		if (freezemonth != null) {
			if (!freezemonth.equals(month.toString())) {
				bool = false;
				baseDao.execute(getErrorSql("当前冻结期间：" + freezemonth, "", type, employee.getEm_name(), "期间未冻结"));
			}
		} else {
			bool = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "无冻结期间"));
		}
		return bool;
	}

	@Override
	public boolean co_chk_before_b(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "工单号+序号不存在";
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_BF_B01, new Object[] { month }, type, employee, remark, all);
		count += isError(SQL_BF_B02, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_c(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "前期成本结余金额不等于上个月的期末成本结余金额";
		clearByCaller(type);
		int count = isError(SQL_BF_C01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_d(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "报废期初结余金额不等于上个月的期末报废结余金额";
		clearByCaller(type);
		int count = isError(SQL_BF_D01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_e(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "期初完工数不等于上个月的期末完工数";
		clearByCaller(type);
		int count = isError(SQL_BF_E01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_f(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "期初数量与上个月期末结余数量不一致";
		clearByCaller(type);
		int count = isError(SQL_BF_F01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_g(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "期初金额与上个月期末结余金额不一致";
		clearByCaller(type);
		int count = isError(SQL_BF_G01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_h(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "报废期初余额与上个月期末报废结余金额不一致";
		clearByCaller(type);
		int count = isError(SQL_BF_H01, new Object[] { month, pmonth }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_i(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "当月有完工数的制造单没工时的";
		clearByCaller(type);
		int count = isError(SQL_BF_I01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_j(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "未过账的出入库单";
		clearByCaller(type);
		int count = isError(SQL_BF_J01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_k(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "有非无值仓原材料单价为0";
		clearByCaller(type);
		int count = isError(SQL_BF_K01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_l(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		boolean checked = true;
		clearByCaller(type);
		String err = compare(SQL_BF_L01, new Object[] { month }, "拨出单条数", SQL_BF_L02, new Object[] { month }, "拨入单条数");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		err = compare(SQL_BF_L03, new Object[] { month }, "拨出总数", SQL_BF_L04, new Object[] { month }, "拨入总数");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		err = compare(SQL_BF_L05, new Object[] { month }, "拨出总额", SQL_BF_L06, new Object[] { month }, "拨入总额");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		return checked;
	}

	@Override
	public boolean co_chk_before_m(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		boolean checked = true;
		clearByCaller(type);
		String err = compare(SQL_BF_M01, new Object[] { month }, "销售拨出单条数", SQL_BF_M02, new Object[] { month }, "销售拨入单条数");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		err = compare(SQL_BF_M03, new Object[] { month }, "销售拨出总数", SQL_BF_M04, new Object[] { month }, "销售拨入总数");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		err = compare(SQL_BF_M05, new Object[] { month }, "销售拨出总额", SQL_BF_M06, new Object[] { month }, "销售拨入总额");
		if (err != null) {
			checked = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), err));
		}
		return checked;
	}

	@Override
	public boolean co_chk_before_n(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "出入库单已做凭证";
		clearByCaller(type);
		int count = isError(SQL_BF_N01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_o(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "出入库单有凭证编号,但凭证在当月不存在";
		clearByCaller(type);
		int count = isError(SQL_BF_O01, new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_p(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "出入库单中文状态不正确";
		clearByCaller(type);
		int count = isError(SQL_BF_P01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_q(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "工单的成品物料编号不存在";
		clearByCaller(type);
		int count = isError(SQL_BF_Q01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_r(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "工单用料表的物料不存在";
		clearByCaller(type);
		int count = isError(SQL_BF_R01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_s(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "委外工单有加工单价但是没有维护币别";
		clearByCaller(type);
		int count = isError(SQL_BF_S01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_s1(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "当月委外验收、验退加工价跟委外单不一致";
		clearByCaller(type);
		int count = isError(SQL_BF_S101, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_s2(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "当月委外验收、验退单税率跟委外单不一致";
		clearByCaller(type);
		int count = isError(SQL_BF_S201, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_t(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "成本表期初完工数不正确";
		clearByCaller(type);
		int count = isError(SQL_BF_T01, new Object[] { start, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_u(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "出入库单料号不存在";
		clearByCaller(type);
		int count = isError(SQL_BF_U01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_v(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "未审核";
		clearByCaller(type);
		int count = isError(SQL_BF_V01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_w(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "生成应付发票并制作了凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_BF_W01, new Object[] { month }, type, employee, remark, all);
		count += isError(SQL_BF_W02, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_x(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "生成应收发票并制作了结转主营业务成本凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_BF_X01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_y(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "已制作凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(SQL_BF_Y01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_a(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "与工单的工作中心不一致";
		clearByCaller(type);
		int count = isError(SQL_AF_A01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_b(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "与工单的类型不一致";
		clearByCaller(type);
		int count = isError(SQL_AF_B01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_c(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "产品编号不存在";
		clearByCaller(type);
		int count = isError(SQL_AF_C01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_d(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "与工单的数量不一致";
		clearByCaller(type);
		int count = isError(SQL_AF_D01, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_e(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		String remark = "与实际本期完工不一致";
		clearByCaller(type);
		int count = isError(SQL_AF_E01, new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_f(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_F01, new Object[] { month }, type, employee, "报废数量与用料月结表里不一致", all);
		count += isError(SQL_AF_F02, new Object[] { month }, type, employee, "报废金额与用料月结表里不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_g(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_G01, new Object[] { month }, type, employee, "本期领料金额与用料月结表中(本期领料金额+本期补料金额-本期退料金额)不一致", all);
		// count += isError(SQL_AF_G02, new Object[] { month, start, end },
		// type, employee, "与工单关联的领退补单据不一致");
		return count == 0;
	}

	@Override
	public boolean co_chk_after_h(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = 0;
		SqlRowList rs1 = baseDao.queryForRowSet(SQL_AF_H02, month);
		while (rs1.next()) {
			SqlRowList rs2 = baseDao.queryForRowSet(SQL_AF_H01, month, rs1.getString(1));
			if (rs2.next()) {
				String wc = "中心：" + rs1.getGeneralString(1);
				double a = rs1.getGeneralDouble(2);
				double b = rs2.getGeneralDouble(2);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表制造费用:" + b));
				}
				a = rs1.getGeneralDouble(3);
				b = rs2.getGeneralDouble(3);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表人工费用:" + b));
				}
				a = rs1.getGeneralDouble(4);
				b = rs2.getGeneralDouble(4);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表mf_weamount:" + b));
				}
				a = rs1.getGeneralDouble(5);
				b = rs2.getGeneralDouble(5);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表mf_deamount:" + b));
				}
				a = rs1.getGeneralDouble(6);
				b = rs2.getGeneralDouble(6);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表mf_otheramount:" + b));
				}
				a = rs1.getGeneralDouble(7);
				b = rs2.getGeneralDouble(7);
				if (a != b) {
					count++;
					baseDao.execute(getErrorSql(wc, "", type, employee.getEm_name(), "中心总和:" + a + "<br>费用表mf_dlaboramount:" + b));
				}
			}
		}
		return count == 0;
	}

	@Override
	public boolean co_chk_after_i(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_I01, new Object[] { month }, type, employee, "成本与(总转出成本/本期完工数+加工价+分摊的单个费用)不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_j(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_J01, new Object[] { month }, type, employee, "工单状态=已完工,但期初完工数+本期完工数<工单数量,应该为已审核", all);
		count += isError(SQL_AF_J02, new Object[] { month, end }, type, employee, "工单状态=已结案,但工单结案日期>todate，应该为已审核", all);
		count += isError(SQL_AF_J03, new Object[] { month }, type, employee, "工单状态=已审核,期初完工数+本期完工数>=工单数量,应该为已完工", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_k(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_K01, new Object[] { start, end, month }, type, employee, "有发生领退补完工验收验退报废的工单,但没有体现在成本表里", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_l(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = 0;
		SqlRowList rs = baseDao.queryForRowSet(SQL_AF_L01, month, month);
		while (rs.next()) {
			count++;
			baseDao.execute(getErrorSql(
					rs.getGeneralString("pd_inoutno") + ",工单:" + rs.getString("cd_makecode"),
					rs.getString("pd_piclass"),
					type,
					employee.getEm_name(),
					"成本表成本价:" + rs.getDouble("cd_costprice") + "<br>出入库单成本价:" + rs.getDouble("pd_price") + ",核算单价:"
							+ rs.getDouble("pd_avprice")));
		}
		return count == 0;
	}

	@Override
	public boolean co_chk_after_m(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_M01, new Object[] { month, month }, type, employee, "成本表委外加工单价跟委外单不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_n(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_N01, new Object[] { month }, type, employee, "有重复", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_o(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_O01, new Object[] { month }, type, employee, "不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_p(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_P01, new Object[] { month }, type, employee, "不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_q(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_Q01, new Object[] { start, end, month }, type, employee, "领料数量、金额异常", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_r(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_R01, new Object[] { start, end, month }, type, employee, "补料数量、金额异常", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_s(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_S01, new Object[] { start, end, month }, type, employee, "退料数量、金额异常", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_t(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_T01, new Object[] { month }, type, employee, "单价cdm_price的逻辑", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_u(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_U01, new Object[] { month, start, end }, type, employee, "本期报废数量异常", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_v(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_V01, new Object[] { month, end }, type, employee, "累计报废数量异常", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_w(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_W01, new Object[] { month }, type, employee, "本期成品入库数不等于成本表本期完工数", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_x(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_X01, new Object[] { month }, type, employee, "实际单位用量与（总用量-前期转出数量）/期初未完工数 不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_y(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_Y01, new Object[] { month }, type, employee, "期末数量<>期初数量+本期领料数量-本期退料数量+本期补料数量-本期转出数量-本期报废数量", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_z(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_Z01, new Object[] { month }, type, employee, "期末金额<>期初金额+本期领料金额-本期退料金额+本期补料金额-本期转出金额-本期报废金额", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_aa(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AA01, new Object[] { month }, type, employee, "期末报废结余金额<>本期报废金额+报废期初余额-本期报废转出金额", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ab(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AB01, new Object[] { month }, type, employee, "月结表用料重复", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ac(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AC01, new Object[] { month }, type, employee, "月结表料号不存在(按编号)", all);
		// count += isError(SQL_AF_AC02, new Object[] { month }, type, employee,
		// "月结表料号不存在(按ID)", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ad(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AD01, new Object[] { month, month }, type, employee, "成本表领退补跟出入库差异", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ae(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AE02, new Object[] { month }, type, employee, "工单差异金额", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_af(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AF01, new Object[] { month }, type, employee, "期末完工数不等于期初完工数+本期完工数", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ag(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AG01, new Object[] { month }, type, employee, "期末完工数大于工单数", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ah(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AH01, new Object[] { month }, type, employee, "加工价", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ai(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AI01, new Object[] { month }, type, employee, "最终成本为负数", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_aj(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AJ01, new Object[] { month }, type, employee, "材料成本为负数", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_ak(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AK01, new Object[] { month }, type, employee, "标准工时与物料里不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_al(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AL01, new Object[] { month }, type, employee, "总工时", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_after_am(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AM01, new Object[] { month }, type, employee, "存月结表有期末金额没有期末数量", all);
		return count == 0;
	}

	// 月结表：转出数量是否正确
	@Override
	public boolean co_chk_after_an(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AN01, new Object[] { month }, type, employee, "转出数量不正确", all);
		return count == 0;
	}

	// 成本表：本期报废转出成本与月结表本期报废转出金额是否一致
	@Override
	public boolean co_chk_after_ao(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(SQL_AF_AO01, new Object[] { month, month }, type, employee, "本期报废转出金额不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String freezemonth = baseDao.getDBSetting("freezeMonth");
		boolean bool = true;
		if (freezemonth != null) {
			if (!freezemonth.equals(month.toString())) {
				bool = false;
				baseDao.execute(getErrorSql("当前冻结期间：" + freezemonth, "", type, employee.getEm_name(), "期间未冻结"));
			}
		} else {
			bool = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "无冻结期间"));
		}
		return bool;
	}

	@Override
	public boolean co_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(
				"select pi_inoutno,pi_class,'日期：'||pi_date||',录入人：'||pi_recordman||',录入日期：'||pi_recorddate from Prodinout where pi_statuscode<>'POSTED' and to_char(pi_date,'yyyymm')=? order by pi_class,pi_inoutno",
				new Object[] { month }, type, employee, "未过账", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(
				"select pd_inoutno,pd_piclass,'行:'||pd_pdno||'<br>物料编号:'||pd_prodcode from ProdIODetail left join ProdInOut on pd_piid=pi_id"
						+ " where nvl(pd_status,0)<>0 and to_char(pi_date,'yyyymm')=? and not exists (select 1 from product where pr_code=pd_prodcode) order by pi_class,pi_inoutno,pd_pdno",
				new Object[] { month }, type, employee, "物料编号不存在", all);
		return count == 0;
	}

	static final String CO_CHK_D = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('生产领料单','生产补料单','生产退料单','完工入库单','拆件入库单') order by typename, catecode";
	static final String CO_CHK_D1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('生产领料单','生产补料单','生产退料单','完工入库单','拆件入库单') order by typename, catecode";

	@Override
	public boolean co_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_D1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_D.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_E = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('委外领料单','委外补料单','委外退料单')  order by typename, catecode ";
	static final String CO_CHK_E1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('委外领料单','委外补料单','委外退料单')  order by typename, catecode ";

	@Override
	public boolean co_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_E1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_E.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_F = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('其它出库单','其它入库单')  order by typename, catecode ";
	static final String CO_CHK_F1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('其它出库单','其它入库单')  order by typename, catecode ";

	@Override
	public boolean co_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_F1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_F.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_G = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('盘盈调整单','盘亏调整单','报废单')  order by typename, catecode ";
	static final String CO_CHK_G1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('盘盈调整单','盘亏调整单','报废单')  order by typename, catecode ";

	@Override
	public boolean co_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_G1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_G.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_H = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('拨入单','拨出单','销售拨入单','销售拨出单')  order by typename, catecode ";
	static final String CO_CHK_H1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('拨入单','拨出单','销售拨入单','销售拨出单')  order by typename, catecode ";

	@Override
	public boolean co_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_H1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_H.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_I = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,pr_stockcatecode) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('采购验收单','采购验退单')  order by typename, catecode ";
	static final String CO_CHK_I1 = "select typename,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select (CASE WHEN NVL(type1,' ')=' ' THEN type2 ELSE type1 END) typename,round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select pi_class type1,sum(round((NVL(pd_outqty,0)+NVL(pd_inqty,0))*NVL(pd_price,0),2)) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' group by pi_class,WH_CATECODE) full join (select vo_source type2,abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in (@CODES) group by vo_source,vd_catecode) on type1=type2 and catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) where typename in ('采购验收单','采购验退单')  order by typename, catecode ";

	@Override
	public boolean co_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_I1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_I.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
					employee, "存货模块金额与总账模块金额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_J_PR2 = "select ca_description,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select abs(sum(round((NVL(pd_outqty,0)-NVL(pd_inqty,0))*NVL(pd_price,0),2))) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and pi_statuscode='POSTED' group by pr_stockcatecode) full join (select abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source in ('发出商品','主营业务成本','应收发票') and vd_catecode in (@CODES) group by vd_catecode) on catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) left join category on catecode=ca_code order by catecode";
	static final String CO_CHK_J_WH2 = "select ca_description,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select abs(sum(round((NVL(pd_outqty,0)-NVL(pd_inqty,0))*NVL(pd_price,0),2))) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and pi_statuscode='POSTED' group by WH_CATECODE) full join (select abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source in ('发出商品','主营业务成本','应收发票') and vd_catecode in (@CODES) group by vd_catecode) on catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) left join category on catecode=ca_code order by catecode";
	static final String CO_CHK_J_PR1 = "select ca_description,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(case when nvl(catecode1,' ')=' ' then catecode2 else catecode1 end) catecode from (select abs(sum(round((nvl(pd_outqty,0)-nvl(pd_inqty,0))*nvl(pd_price,0),2))) sum1, pr_stockcatecode catecode1 from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and pi_statuscode='POSTED' group by pr_stockcatecode) full join (select abs(sum(nvl(vd_debit,0)-nvl(vd_credit,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source in ('应收发票') and vd_catecode in (@CODES) group by vd_catecode) on catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) left join category on catecode=ca_code order by catecode";
	static final String CO_CHK_J_WH1 = "select ca_description,'科目编号:'||catecode,'存货:'||s1||'<br>总账:'||s2 from (select round(nvl(sum1,0),2) s1,round(nvl(sum2,0),2) s2,(CASE WHEN NVL(catecode1,' ')=' ' THEN catecode2 ELSE catecode1 END) catecode from (select abs(sum(round((NVL(pd_outqty,0)-NVL(pd_inqty,0))*NVL(pd_price,0),2))) sum1, WH_CATECODE catecode1 from prodinout,prodiodetail,warehouse where pi_id=pd_piid and pd_whcode=wh_code and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and pi_statuscode='POSTED' group by WH_CATECODE) full join (select abs(sum(NVL(vd_debit,0)-nvl(VD_CREDIT,0))) sum2, vd_catecode catecode2 from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vo_source in ('应收发票') and vd_catecode in (@CODES) group by vd_catecode) on catecode1=catecode2 where round(nvl(sum1,0),0)<>round(nvl(sum2,0),0)) left join category on catecode=ca_code order by catecode";

	@Override
	public boolean co_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cate = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (!StringUtil.hasText(cate)) {
			BaseUtil.showError("存货科目未设置.");
		}
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			if (baseDao.isDBSetting("useBillOutAR")) {
				int count = isError(CO_CHK_J_WH1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
						employee, "存货模块金额与总账模块金额不一致", all);
				return count == 0;
			} else {
				int count = isError(CO_CHK_J_WH2.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
						employee, "存货模块金额与总账模块金额不一致", all);
				return count == 0;
			}
		} else {
			// 按物料分存货科目
			if (baseDao.isDBSetting("useBillOutAR")) {
				int count = isError(CO_CHK_J_PR1.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
						employee, "存货模块金额与总账模块金额不一致", all);
				return count == 0;
			} else {
				int count = isError(CO_CHK_J_PR2.replace("@CODES", CollectionUtil.toSqlString(cate)), new Object[] { month, month }, type,
						employee, "存货模块金额与总账模块金额不一致", all);
				return count == 0;
			}

		}
	}

	static final String CO_CHK_K1 = "select (CASE WHEN NVL(cate1,' ')=' ' THEN cate2 ELSE cate1 END) catecode,'','存货:'||ROUND(NVL(sum1,0),2)||'<br>总账:'||ROUND(NVL(sum2,0),2) from (select wh_catecode cate1,sum(nvl(pwm_endamount,0)) sum1 from productwhmonth,warehouse where pwm_whcode=wh_code and pwm_yearmonth=? and wh_catecode in (@CATE) group by wh_catecode) full join (select cm_catecode cate2,sum(nvl(cm_enddebit,0)+nvl(cm_endcredit,0)) sum2 from catemonth where cm_yearmonth=? and cm_catecode in (@CATE) group by cm_catecode) on cate1=cate2 where ROUND(NVL(sum1,0),0)<>ROUND(NVL(sum2,0),0) ORDER BY catecode";

	static final String CO_CHK_K2 = "select (CASE WHEN NVL(cate1,' ')=' ' THEN cate2 ELSE cate1 END) catecode,'','存货:'||ROUND(NVL(sum1,0),2)||'<br>总账:'||ROUND(NVL(sum2,0),2) from (select pr_stockcatecode cate1, sum(NVL(pwm_endamount,0)) sum1 from productwhmonth,product where pwm_prodcode=pr_code and pwm_yearmonth=? and pr_stockcatecode in (@CATE) group by pr_stockcatecode) full join (select cm_catecode cate2,sum(NVL(cm_enddebit,0)+NVL(cm_endcredit,0)) sum2 from catemonth where cm_yearmonth=? and cm_catecode in (@CATE) group by cm_catecode) on cate1=cate2 where ROUND(NVL(sum1,0),0)<>ROUND(NVL(sum2,0),0) ORDER BY catecode";

	@Override
	public boolean co_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cates = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (cates == null || cates.length == 0)
			BaseUtil.showError("请先设置存货科目");
		String cateStr = CollectionUtil.toSqlString(cates);
		// 按仓库分存货科目
		if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
			int count = isError(CO_CHK_K1.replace("@CATE", cateStr), new Object[] { month, month }, type, employee,
					"存货期末结存金额与总账对应存货科目余额不一致", all);
			return count == 0;
		} else {
			// 按物料分存货科目
			int count = isError(CO_CHK_K2.replace("@CATE", cateStr), new Object[] { month, month }, type, employee,
					"存货期末结存金额与总账对应存货科目余额不一致", all);
			return count == 0;
		}
	}

	static final String CO_CHK_L = "select a.pwm_prodcode,'仓库:'||a.pwm_whcode,'上月末:'||b.pwm_endqty||'<br>本月初:'||a.pwm_beginqty from productwhmonth a,productwhmonth b where a.pwm_prodcode=b.pwm_prodcode and a.pwm_whcode=b.pwm_whcode and a.pwm_yearmonth=? and b.pwm_yearmonth=? and a.pwm_beginqty<>b.pwm_endqty";

	@Override
	public boolean co_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int preMonth = DateUtil.addMonth(month, -1);
		boolean hasPre = baseDao.checkIf("productwhmonth", "pwm_yearmonth=" + preMonth);
		if (hasPre) {
			int count = isError(CO_CHK_L, new Object[] { month, preMonth }, type, employee, "期初数量与上月期末数量不一致", all);
			return count == 0;
		}
		return true;
	}

	static final String CO_CHK_M = "select a.pwm_prodcode,'仓库:'||a.pwm_whcode,'上月末:'||b.pwm_endamount||'<br>本月初:'||a.pwm_beginamount from productwhmonth a,productwhmonth b where a.pwm_prodcode=b.pwm_prodcode and a.pwm_whcode=b.pwm_whcode and a.pwm_yearmonth=? and b.pwm_yearmonth=? and a.pwm_beginamount<>b.pwm_endamount";

	@Override
	public boolean co_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int preMonth = DateUtil.addMonth(month, -1);
		boolean hasPre = baseDao.checkIf("productwhmonth", "pwm_yearmonth=" + preMonth);
		if (hasPre) {
			int count = isError(CO_CHK_M, new Object[] { month, preMonth }, type, employee, "期初金额与上月期末金额不一致", all);
			return count == 0;
		}
		return true;
	}

	static final String CO_CHK_N = "select pwm_prodcode,'仓库:'||pwm_whcode,'期末数量:'||pwm_endqty||'<br>期末金额:'||pwm_endamount from productwhmonth,warehouse where pwm_whcode=wh_code and pwm_yearmonth=? and pwm_endqty<>0 and pwm_endamount=0 and nvl(wh_nocost,0)=0";

	@Override
	public boolean co_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_N, new Object[] { month }, type, employee, "有数量无金额", all);
		return count == 0;
	}

	static final String CO_CHK_O = "select pwm_prodcode,'仓库:'||pwm_whcode,'期末数量:'||pwm_endqty||'<br>期末金额:'||pwm_endamount from productwhmonth where pwm_yearmonth=? and pwm_endqty=0 and pwm_endamount<>0";

	@Override
	public boolean co_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_O, new Object[] { month }, type, employee, "有金额无数量", all);
		return count == 0;
	}

	static final String CO_CHK_P = "select pwm_prodcode,'仓库:'||pwm_whcode,'期末数量:'||pwm_endqty||'<br>期末金额:'||pwm_endamount from productwhmonth where pwm_yearmonth=? and (pwm_endqty<0 or pwm_endamount<0)";

	@Override
	public boolean co_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_P, new Object[] { month }, type, employee, "负数金额、负数数量", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		// TODO
		return true;
	}

	static final String CO_CHK_R = "select distinct pi_inoutno,pi_class from prodinout,warehouse,prodiodetail where pd_piid=pi_id and to_char(pi_date,'yyyymm')=? and nvl(pi_statuscode,' ')='POSTED' and nvl(pi_vouchercode,' ')=' ' "
			+ "and pi_class not in ('出货单','销售退货单','采购验收单','采购验退单','委外验收单','委外验退单','库存初始化','用品验收单','用品验退单') and pd_whcode=wh_code and nvl(wh_nocost,0)=0 ";

	@Override
	public boolean co_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_R, new Object[] { month }, type, employee, "单据未制作凭证", all);
		return count == 0;
	}

	static final String CO_CHK_S = "select '行:'||vd_detno, vo_code from voucher left join voucherDetail on vd_void=vo_id where vo_yearmonth=? and vd_catecode in (@CATE) and nvl(vo_source,' ')=' '";
	static final String CO_CHK_X = "select '行:'||vd_detno, vo_code, '来源单据非指定单据，来源：'||vo_source from voucher left join voucherDetail on vd_void=vo_id where vo_yearmonth=? and vd_catecode in (@CATE) and (vo_source not in ('应付发票','应收发票','应付暂估','发出商品','期初调整单','主营业务成本','应付开票记录') and vo_source not in (select distinct pi_class from prodinout))";

	@Override
	public boolean co_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cates = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (cates == null || cates.length == 0)
			BaseUtil.showError("请先设置存货科目");
		String cateStr = CollectionUtil.toSqlString(cates);
		int count = 0;
		count += isError(CO_CHK_S.replace("@CATE", cateStr), new Object[] { month }, type, employee, "来源单据为空", all);
		count += isError(CO_CHK_X.replace("@CATE", cateStr), new Object[] { month }, type, employee, "来源单据非指定单据", all);
		return count == 0;
	}

	static final String CO_CHK_T = "select pi_inoutno,pi_class,'行:'||pd_pdno||',单价:'||pd_price from prodinout left join prodiodetail on pi_id=pd_piid left join warehouse on pd_whcode=wh_code where to_char(pi_date,'yyyymm')=? and nvl(pd_inqty,0)>0 and abs(wh_nocost)=0 and nvl(pd_price,0)<0 and pi_class not in ('拨入单') order by pi_class,pi_inoutno,pd_pdno";

	@Override
	public boolean co_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_T, new Object[] { month }, type, employee, "非无值仓单价为负数", all);
		return count == 0;
	}

	static final String CO_CHK_T1 = "select pi_inoutno,pi_class,'行:'||pd_pdno||',单价:'||pd_price from prodinout left join prodiodetail on pi_id=pd_piid left join warehouse on pd_whcode=wh_code where to_char(pi_date,'yyyymm')=? and nvl(pd_inqty,0)>0 and abs(wh_nocost)=0 and nvl(pd_price,0)=0 and pi_class not in ('拨入单') order by pi_class,pi_inoutno,pd_pdno";

	@Override
	public boolean co_chk_t1(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_T1, new Object[] { month }, type, employee, "非无值仓单价为0", all);
		return count == 0;
	}

	static final String CO_CHK_U = "select pi_inoutno,pi_class,'行:'||pd_pdno from prodinout left join prodiodetail on pi_id=pd_piid left join warehouse on pd_whcode=wh_code where to_char(pi_date,'yyyymm')=? and abs(wh_nocost)=1 and nvl(pd_price,0)<>0 order by pi_class,pi_inoutno,pd_pdno";

	@Override
	public boolean co_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_U, new Object[] { month }, type, employee, "无值仓存在单价", all);
		return count == 0;
	}

	static final String CO_CHK_V = "select pi_inoutno,pi_class,'行:'||pd_pdno from prodinout p1 left join prodiodetail d1 on pi_id=pd_piid where nvl(pd_inqty,0)<>0 and to_char(pi_date,'yyyymm')=? and exists (select 1 from prodinout p2 left join prodiodetail d2 on p2.pi_id=d2.pd_piid where nvl(d2.pd_outqty,0)<>0 and to_char(p2.pi_date,'yyyymm')<? and d2.pd_batchcode=d1.pd_batchcode and d2.pd_prodcode=d1.pd_prodcode and d2.pd_whcode=d1.pd_whcode) order by pi_class,pi_inoutno,pd_pdno";

	@Override
	public boolean co_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_V, new Object[] { month, month }, type, employee, "上月出库当月入库", all);
		return count == 0;
	}

	static final String CO_CHK_W = "select pi_inoutno,pi_class,'出入库:'||to_char(pi_date,'yyyymm')||'<br>凭证:'||vo_yearmonth from prodinout left join voucher on pi_vouchercode=vo_code where pi_vouchercode is not null and to_char(pi_date,'yyyymm')=? and to_char(pi_date,'yyyymm')<>vo_yearmonth order by pi_class,pi_inoutno";

	@Override
	public boolean co_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_W, new Object[] { month }, type, employee, "出入库单据会计期间与凭证不一致", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_x(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Map<String, Object> map = checkAccountService.getYearMonth("MONTH-P", null);
		boolean bool = String.valueOf(month).equals(map.get("PD_DETNO").toString());
		if (!bool) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "成本期间:" + month + "<br>库存期间:" + map.get("PD_DETNO")));
		}
		return bool;
	}

	@Override
	public List<Map<String, Object>> getCostAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-T");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String[] macateCode = baseDao.getDBSettingArray("CheckAccount!COST", "DirectMaterialsCatecode");
		String[] oscateCode = baseDao.getDBSettingArray("CheckAccount!COST", "ProcessingCatecode");
		String macateStr = CollectionUtil.toSqlString(macateCode);
		String oscateStr = CollectionUtil.toSqlString(oscateCode);
		String sql = "select CM_YEARMONTH,'MAKE' cm_code,'生产成本-直接材料' cm_name,NVL(CM_BEGINAMOUT,0) cm_beginamount,NVL(CM_UMNOWDEBIT,0) CM_UMNOWDEBIT,NVL(CM_UMNOWCREDIT,0) CM_UMNOWCREDIT,"
				+ "NVL(CM_NOWDEBIT,0) CM_NOWDEBIT,NVL(CM_NOWCREDIT,0) CM_NOWCREDIT,NVL(CM_UMENDAMOUNT,0) CM_UMENDAMOUNT,NVL(CM_ENDAMOUNT,0) CM_ENDAMOUNT,"
				+ "nvl(CD_BEGINAMOUNT,0) CD_BEGINAMOUNT,nvl(CD_DEBIT,0) CD_DEBIT,nvl(CD_CREDIT,0) CD_CREDIT,nvl(CD_ENDAMOUNT,0) CD_ENDAMOUNT from "
				+ "(select CM_YEARMONTH,sum(CM_BEGINDEBIT-CM_BEGINCREDIT) CM_BEGINAMOUT,sum(CM_UMNOWDEBIT) CM_UMNOWDEBIT,sum(CM_UMNOWCREDIT) CM_UMNOWCREDIT,sum(CM_UMENDDEBIT-CM_UMENDCREDIT) CM_UMENDAMOUNT,"
				+ "sum(CM_NOWDEBIT) CM_NOWDEBIT,sum(CM_NOWCREDIT) CM_NOWCREDIT,sum(CM_ENDDEBIT-CM_ENDCREDIT) CM_ENDAMOUNT from CATEMONTH where CM_CATECODE in ("
				+ macateStr
				+ ") group by CM_YEARMONTH) full join "
				+ "(select cd_yearmonth,SUM(ROUND(CD_BEGINCOSTAMOUNT,2)+ROUND(CD_BEGINSCRAPAMOUNT,2)) CD_BEGINAMOUNT,SUM(ROUND(CD_NOWGETAMOUNT,0)) CD_DEBIT,"
				+ "SUM(ROUND(nvl(cd_nowturnunitprice,0)*CD_NOWENDQTY,2)) CD_CREDIT,SUM(ROUND(CD_ENDCOSTAMOUNT,2)+ROUND(CD_ENDSCRAPAMOUNT,2)) CD_ENDAMOUNT from COSTDETAIL "
				+ "where CD_MAKETYPE='MAKE' group by CD_YEARMONTH) on CM_YEARMONTH=CD_YEARMONTH where CM_YEARMONTH="
				+ ym
				+ " union select CM_YEARMONTH,'OS' cm_code,'委托加工物资' cm_name,NVL(CM_BEGINAMOUT,0) CM_BEGINAMOUT,NVL(CM_UMNOWDEBIT,0) CM_UMNOWDEBIT,NVL(CM_UMNOWCREDIT,0) CM_UMNOWCREDIT,"
				+ "NVL(CM_NOWDEBIT,0) CM_NOWDEBIT,NVL(CM_NOWCREDIT,0) CM_NOWCREDIT,NVL(CM_UMENDAMOUNT,0) CM_UMENDAMOUNT,NVL(CM_ENDAMOUNT,0) CM_ENDAMOUNT,"
				+ "nvl(CD_BEGINAMOUNT,0) CD_BEGINAMOUNT,nvl(CD_DEBIT,0) CD_DEBIT,nvl(CD_CREDIT,0) CD_CREDIT,nvl(CD_ENDAMOUNT,0) CD_ENDAMOUNT from "
				+ "(select CM_YEARMONTH,sum(CM_BEGINDEBIT-CM_BEGINCREDIT) CM_BEGINAMOUT,sum(CM_UMNOWDEBIT) CM_UMNOWDEBIT,sum(CM_UMNOWCREDIT) CM_UMNOWCREDIT,sum(CM_UMENDDEBIT-CM_UMENDCREDIT) CM_UMENDAMOUNT,"
				+ "sum(CM_NOWDEBIT) CM_NOWDEBIT,sum(CM_NOWCREDIT) CM_NOWCREDIT,sum(CM_ENDDEBIT-CM_ENDCREDIT) CM_ENDAMOUNT from CATEMONTH where CM_CATECODE in ("
				+ oscateStr
				+ ") group by CM_YEARMONTH) full join (select cd_yearmonth,"
				+ "SUM(ROUND(CD_BEGINCOSTAMOUNT,2)+ROUND(CD_BEGINSCRAPAMOUNT,2)) CD_BEGINAMOUNT,SUM(ROUND(CD_NOWGETAMOUNT,0)) CD_DEBIT,"
				+ "SUM(ROUND(CD_COSTPRICE*CD_NOWENDQTY,2)) CD_CREDIT,SUM(ROUND(CD_ENDCOSTAMOUNT,2)+ROUND(CD_ENDSCRAPAMOUNT,2)) CD_ENDAMOUNT from COSTDETAIL "
				+ "where CD_MAKETYPE='OS' group by CD_YEARMONTH) on CM_YEARMONTH=CD_YEARMONTH where CM_YEARMONTH=" + ym;

		Map<String, Object> item = null;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("cm_yearmonth", rs.getGeneralString("cm_yearmonth"));
			item.put("cm_code", rs.getGeneralString("cm_code"));
			item.put("cm_name", rs.getGeneralString("cm_name"));
			item.put("cd_beginamount", rs.getGeneralDouble("cd_beginamount"));
			item.put("cd_nowdebit", rs.getGeneralDouble("cd_debit"));
			item.put("cd_nowcredit", rs.getGeneralDouble("cd_credit"));
			item.put("cd_endamount", rs.getGeneralDouble("cd_endamount"));
			item.put("cm_beginbalance", rs.getGeneralDouble("cm_beginamount"));
			if (chkun) {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_umendamount"));
			} else {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_endamount"));
			}
			store.add(item);
		}
		return store;
	}
}
