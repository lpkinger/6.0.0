package com.uas.erp.service.common.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.BatchPrintService;
import com.uas.erp.service.pm.impl.PmHandler;

@Service
public class BatchPrintServiceImpl implements BatchPrintService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PmHandler pmHandler;

	@Override
	public String[] printBatch(String idS, String language, Employee employee, String reportName, String condition, String title,
			String todate, String dateRange, String fromdate, String enddate) {
		// 执行打印操作
		String SQLStr = "";
		String key = "12345678";
		String[] keys = new String[4];
		Des de = new Des();
		try {
			keys[0] = de.toHexString(de.encrypt(reportName, key)).toUpperCase();

			String skey = URLEncoder.encode(key, "utf-8").toLowerCase();
			keys[1] = de.toHexString(de.encrypt(skey, key)).toUpperCase();

			String cond = java.net.URLEncoder.encode(condition, "utf-8").toLowerCase();
			keys[2] = de.toHexString(de.encrypt(cond, key)).toUpperCase();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String lyTime = sdf.format(new java.util.Date());
			String time = java.net.URLEncoder.encode(lyTime, "utf-8").toLowerCase();
			keys[3] = de.toHexString(de.encrypt(time, key)).toUpperCase();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String now = DateUtil.getCurrentDate();
		if (title.equals("在制仓库库存表(制单)")) {
			SQLStr = "UPDATE MakeMaterial SET mm_wipqty=0,mm_wipamount=0 where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
					+ todate + "','yyyy-MM-dd'))) and (NVL(mm_wipqty,1)<>0 or NVL(mm_wipamount,1)<>0)";
			baseDao.execute(SQLStr);
			SQLStr = "UPDATE Make SET ma_wipqty=0 where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('" + todate
					+ "','yyyy-MM-dd')) and nvl(ma_wipqty,1)<>0 ";
			baseDao.execute(SQLStr);
			if (DateUtil.compare(todate, now) == -1) {
				SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id  AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
						+ todate
						+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
						+ todate
						+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
						+ todate + "','yyyy-MM-dd')))";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND  pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
						+ todate
						+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
						+ todate + "','yyyy-MM-dd')) ";
				baseDao.execute(SQLStr);
			} else {
				SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND  pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>trunc(sysdate) AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>trunc(sysdate)) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>trunc(sysdate)))";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>trunc(sysdate) AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>trunc(sysdate)) ";
				baseDao.execute(SQLStr);
			}
			// SQLStr =
			// "update makematerial set mm_havegetamountcost2=(select round(sum((nvl(pd_outqty,0)-nvl(pd_inqty,0))*pd_price),8) from prodinout,prodiodetail where pi_inoutno=pd_inoutno and pi_class=pd_piclass and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pi_status='已过账' and trunc(pi_date)<=to_date('"
			// + todate +
			// "','yyyy-MM-dd') and pd_piclass in ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单')) where mm_code  in (select ma_code from make where ma_status<>'已结案' or (ma_status='已结案' and ma_actenddate>to_date('"
			// + todate + "','yyyy-MM-dd')))";
			// baseDao.execute(SQLStr);
			// SQLStr =
			// "update makematerial set mm_havegetqtycost2=(select sum((nvl(pd_outqty,0)-nvl(pd_inqty,0))) from prodinout,prodiodetail where pi_inoutno=pd_inoutno and pi_class=pd_piclass and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_status>10 and trunc(pi_date)<=to_date('"
			// +todate +
			// "','yyyy-MM-dd') and pd_piclass in ('生产领料单','生产退料单','生产补料单','委外领料单','委外退料单','委外补料单')) where mm_code  in (select ma_code from make where ma_status<>'已结案' or (ma_status='已结案' and ma_actenddate>to_date('"
			// + todate + "','yyyy-MM-dd')))";
			// baseDao.execute(SQLStr);
			// SQLStr =
			// "update makematerial set mm_price=nvl((select top 1 cdm_price from costdetailmaterial where cdm_mmcode=mm_code and cdm_mmdetno=mm_detno order by cdm_yearmonth desc),0)  where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and ma_actenddate>to_date('"
			// + todate + "','yyyy-MM-dd')))";
			// baseDao.execute(SQLStr);
		}
		/*
		 * if (title.equals("物料综合查询")) { if (condition != "") { try { pmHandler.product_query_before(condition); } catch (Exception e) { System.out.println(e.toString()); } }
		 * 
		 * }
		 */
		if (title.equals("派车费用统计")) {
			try {
				baseDao.procedure("CARCOSTTOTAL", new Object[] { todate, fromdate, enddate });
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (title.equals("在制仓库库存表(汇总)")) {
			SQLStr = "UPDATE MakeMaterial SET mm_wipqty=0,mm_wipamount=0 where mm_code  in (select ma_code from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
					+ todate + "','yyyy-MM-dd'))) and (NVL(mm_wipqty,1)<>0 or NVL(mm_wipamount,1)<>0)";
			baseDao.execute(SQLStr);
			SQLStr = "UPDATE Make SET ma_wipqty=0 where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('" + todate
					+ "','yyyy-MM-dd')) and nvl(ma_wipqty,1)<>0 ";
			baseDao.execute(SQLStr);
			try {
				if (DateUtil.compare(todate, now) == -1) {
					SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
							+ todate + "','yyyy-MM-dd')))";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
							+ todate + "','yyyy-MM-dd')) ";
					baseDao.execute(SQLStr);
				} else {
					SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>trunc(sysdate) AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>trunc(sysdate)) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>trunc(sysdate)))";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>trunc(sysdate) AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>trunc(sysdate)) ";
					baseDao.execute(SQLStr);
				}
			} catch (Exception ex) {
				BaseUtil.showError(BaseUtil.getLocalMessage(ex.toString(), language));
			}

		}
		if (title.equals("工单调拨查询")) {
			if (idS != null && !idS.equals("")) {
				try {
					SQLStr = "UPDATE MakeMaterial SET mm_onhand=NVL((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='2110' AND pw_prodcode=mm_prodcode),0) WHERE NVL(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE MakeMaterial SET mm_onhandjg=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='1104' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE MakeMaterial SET mm_halfonhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='2108' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
				} catch (Exception ex) {
					// BaseUtil.showError(BaseUtil.getLocalMessage(ex.toString(),language));
					ex.printStackTrace();
				}

			}

		}
		if (title.equals("工单素材查询")) {
			if (idS != null && !idS.equals("")) {
				try {
					SQLStr = "UPDATE MakeMaterial SET mm_onhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='2110' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE MakeMaterial SET mm_onhandjg=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='1104' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE MakeMaterial SET mm_halfonhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
							+ "pw_whcode='2108' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
							+ idS + ")";
					baseDao.execute(SQLStr);
				} catch (Exception ex) {
					// BaseUtil.showError(BaseUtil.getLocalMessage(ex.toString(),language));
					ex.printStackTrace();
				}

			}
		}
		if (title.equals("客户订单明细表")) {

			try {
				SQLStr = "update saledetail set sd_finishqty=nvl((select sum(NVL(ma_madeqty,0)) from make where ma_prodcode=sd_prodcode and ma_salecode=sd_code and ma_saledetno=sd_detno),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_bzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='包装'),0) where  sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_zzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname in ('制造一组装','制造二组装')),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_hhfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='后焊'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_smtfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='SMT'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=(select max(ba_date) from batch where ba_prodcode=sd_prodcode and ba_salecode=sd_code) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=' ' where sd_lastfinishdate<to_date('2000-1-1','yyyy-MM-dd')";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (title.equals("未发货客户订单明细表")) {

			try {
				SQLStr = "update saledetail set sd_finishqty=nvl((select sum(NVL(ma_madeqty,0)) from make where ma_prodcode=sd_prodcode and ma_salecode=sd_code and ma_saledetno=sd_detno),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_bzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='包装'),0) where  sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_zzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname in ('制造一组装','制造二组装')),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_hhfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='后焊'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				// System.out.println(SQLStr);
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_smtfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='SMT'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=(select max(ba_date) from batch where ba_prodcode=sd_prodcode and ba_salecode=sd_code) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				// System.out.println(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=' ' where sd_lastfinishdate<to_date('2000-1-1','yyyy-MM-dd')";
				baseDao.execute(SQLStr);
				System.out.println(SQLStr);
				// baseDao.callProcedure("UpdateInfo",new Object[]{SQLStr});

			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}
		}
		/*
		 * if(title.equals("分仓库库存报表")){ try{//SQLStr= "update ProductWH set pw_amount=nvl((select round(SUM(ba_total),2) from batch where  ba_whcode=pw_whcode and ba_prodcode=pw_prodcode),0) where round(nvl(pw_amount,0),2)<>nvl((select round(SUM(ba_total),2) from Batch  where ba_whcode= pw_whcode and ba_prodcode=pw_prodcode),0)" ; //baseDao.execute(SQLStr);//SQLStr=
		 * "update ProductWH set pw_avprice=ROUND(pw_amount/pw_onhand,8) where round(pw_amount,2)<>round(pw_onhand*pw_avprice,2) and nvl(pw_onhand,0)<>0" ; //baseDao.execute(SQLStr);//SQLStr= "update productwh set pw_lastdate=(select min(ba_date) from batch where ba_whcode=pw_whcode and ba_prodcode=pw_prodcode and ba_remain>0) where nvl(pw_onhand,0)>0" ; //baseDao.execute(SQLStr);//SQLStr= "update productwh set pw_lastdate='' where pw_lastdate is null and nvl(pw_onhand,0)>0" ;
		 * //baseDao.execute(SQLStr); }catch(Exception ex){ System.out.println(ex.toString()); }
		 * 
		 * }
		 */
		if (title.equals("FQC检验报告打印")) {
			// 更新打印状态
			try {
				baseDao.updateByCondition("qua_verifyapplydetail", "ve_printstatus='已打印'", "ve_id in(" + idS + ")");
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("制造单备料单(A4)")) {
			// 更新打印状态
			try {
				baseDao.updateByCondition("make", "ma_printstatus='已打印'", "ma_code in('" + idS + "')");
			} catch (Exception ex) {
			}

		}
		if (title.equals("凭证批量打印")) {

			try {
				if (!idS.equals("")) {
					baseDao.updateByCondition("voucher", "vo_printstatus='已打印'", "vo_id in(" + idS + ")");
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("费用批量打印")) {
			try {
				if (!idS.equals("")) {
					baseDao.updateByCondition("FeePlease", "fp_printstatus='已打印'", "fp_id in(" + idS + ")");
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("采购验收单批量打印")) {
			try {
				if (!idS.equals("")) {
					baseDao.updateByCondition("Prodinout", "pi_printstatus='已打印'", "pi_id in(" + idS + ")");
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("出货单批量打印")) {
			try {
				if (!idS.equals("")) {
					baseDao.updateByCondition("ProdInOut", "pi_printstatus='已打印',pi_printstatuscode='PRINTED',pi_count=nvl(pi_count,0)+1",
							"pi_id in(" + idS + ")");
					baseDao.execute("update ProdIODetail set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),"
							+ "pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_netprice=round(pd_sendprice/(1+nvl(pd_taxrate,0)/100),6) "
							+ "where pd_piid in(" + idS + ")");
					baseDao.execute("update  ProdIODetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),"
							+ "pd_nettotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))/(1+nvl(pd_taxrate,0)/100),2) "
							+ "where pd_piid in(" + idS + ")");
					baseDao.execute("update ProdInOut set pi_total=(SELECT sum(round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)) FROM ProdIODetail "
							+ "WHERE pd_piid=pi_id) where pi_id in(" + idS + ")");
					baseDao.execute("update prodiodetail set pd_customprice=pd_sendprice, pd_taxamount=pd_ordertotal where pd_piclass in ('出货单','销售退货单') "
							+ "and pd_piid in(" + idS + ") and nvl(pd_customprice,0)=0");
					baseDao.execute("update ProdInOut set pi_totalupper=L2U(nvl(pi_total,0)) where pi_id in(" + idS + ")");
					baseDao.execute("insert into messagelog(ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
							+ "select messageLog_SEQ.nextval,sysdate,'" + SystemSession.getUser().getEm_name()
							+ "','批量打印','打印成功','ProdInOut!Sale|pi_id='||pi_id,PI_INOUTNO " + "from prodinout where pi_id in(" + idS + ")");// 插入日志
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("出货单批量打印(无价)")) {
			try {
				if (!idS.equals("")) {
					baseDao.execute("update ProdIODetail set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),"
							+ "pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_netprice=round(pd_sendprice/(1+nvl(pd_taxrate,0)/100),6) "
							+ "where pd_piid in(" + idS + ")");
					baseDao.execute("update  ProdIODetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),"
							+ "pd_nettotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))/(1+nvl(pd_taxrate,0)/100),2) "
							+ "where pd_piid in(" + idS + ")");
					baseDao.execute("update ProdInOut set pi_total=(SELECT sum(round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)) FROM ProdIODetail "
							+ "WHERE pd_piid=pi_id) where pi_id in(" + idS + ")");
					baseDao.execute("update prodiodetail set pd_customprice=pd_sendprice, pd_taxamount=pd_ordertotal where pd_piclass in ('出货单','销售退货单') "
							+ "and pd_piid in(" + idS + ") and nvl(pd_customprice,0)=0");
					baseDao.execute("update ProdInOut set pi_totalupper=L2U(nvl(pi_total,0)) where pi_id in(" + idS + ")");
					baseDao.execute("insert into messagelog(ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
							+ "select messageLog_SEQ.nextval,sysdate,'" + SystemSession.getUser().getEm_name()
							+ "','批量打印','打印成功','ProdInOut!Sale|pi_id='||pi_id,PI_INOUTNO " + "from prodinout where pi_id in(" + idS + ")");// 插入日志
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("应收账龄分析表") || title.equals("应收发票汇总表") || title.equals("应收帐龄表")|| title.equals("到期应收帐龄表")) {
			try {
				System.out.println("应收todate=" + todate);
				baseDao.procedure("UPDATEYSZL", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		if (title.equals("应付账龄分析表") || title.equals("应付发票汇总表") || title.equals("应付帐龄表")|| title.equals("到期应付帐龄表")) {
			try {
				System.out.println("应付todate=" + todate);
				baseDao.procedure("UPDATEYFZL", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		if (title.equals("库存帐龄表")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEBAREMAIN", new Object[] { todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}

		}
		if (title.equals("库存金额统计表-品牌")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("updateKCMoney", new Object[] { todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("应收未开票明细表")) {
			try {
				String upstr = "update goodssenddetail set gsd_tempqty=gsd_qty-NVL(gsd_showinvoqty,0)-nvl((select sum(abd_qty) from arbill,arbilldetail where ab_id=abd_abid and ab_date>to_date('"
						+ todate + "','yyyy-MM-dd') and gsd_id=abd_sourcedetailid and abd_sourcekind='GOODSSEND'),0)";
				baseDao.execute(upstr);
			} catch (Exception ex) {

			}
		}
		/*
		 * if(title.equals("制造单领料情况(按结案日期)")){ try{SQLStr= "update makematerial set mm_price=(select pr_avprice from product where pr_code=mm_prodcode and nvl(mm_price,0)=0 and pr_avprice>0 ) where mm_maid in (select ma_id from make where ma_date " +dateRange+")"; baseDao.execute(SQLStr); System.out.println(SQLStr); }catch(Exception ex){
		 * 
		 * } }
		 */

		if (title.equals("毛利润分析(按订单)") || title.equals("毛利润分析明细表")) {
			try {
				// SQLStr = "update product set pr_costtemp=(select cd_bomcost from (select * from costdetail where nvl(cd_bomcost,0)>0 order by cd_yearmonth desc) where cd_prodcode=pr_code and rownum<=1  ) where pr_code in (select  cd_prodcode from costdetail where nvl(cd_bomcost,0)>0)";
				SQLStr = "merge into product using (select cd_prodcode,max(cd_bomcost) cd_bomcost from (select distinct cd_prodcode,cd_bomcost from (select cd_prodcode,cd_bomcost,cd_yearmonth,rank() over(partition by cd_prodcode order by cd_yearmonth desc) row_id from costdetail where nvl(cd_bomcost,0) > 0) where row_id=1) group by cd_prodcode) costdetail on (pr_code=cd_prodcode) when matched then update set pr_costtemp=cd_bomcost";
				baseDao.execute(SQLStr);
			} catch (Exception ex) {

			}
		}
		if (title.equals("毛利润分析明细(平+负)")) {
			try {
				SQLStr = "update batch set ba_currency=(select max(pi_currency) from prodinout,prodiodetail where pi_id=pd_piid and pd_batchcode=ba_code and pd_whcode=ba_whcode and pd_prodcode=ba_prodcode and nvl(pd_inqty,0)>0) where nvl(ba_currency,' ')=' ' ;";
				baseDao.execute(SQLStr);
			} catch (Exception ex) {

			}
		}
		if (title.equals("未开票明细表") || title.equals("应收发票明细表")) {
			try {
				baseDao.procedure("UPDATEYSTEMQTY", new Object[] { todate });
			} catch (Exception ex) {

			}

		}
		if (title.equals("应付未开票明细表") || title.equals("应付发票明细表")) {
			try {
				baseDao.procedure("UPDATEYFTEMQTY", new Object[] { todate });
			} catch (Exception ex) {

			}

		}
		if (title.equals("销售订单达成率表")) {

			try {
				System.out.println(todate);
				baseDao.procedure("UPDATESALEDETAILDCDATE", new Object[] { todate });
			} catch (Exception ex) {

			}

		}
		if (title.equals("期间库存表")) {
			if(fromdate.isEmpty()){
				BaseUtil.showError("起始止日期不能为空");
			}
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("updateProductWH", new Object[] { fromdate, todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("销售预测达成率")) {
			try {
				baseDao.procedure("UPDATE_SCM_PRODQTY_C3", new Object[] { fromdate, todate });
			} catch (Exception ex) {

			}
		}
		
		if (title.equals("出入库明细表(过账日期)")) {
			try {
				baseDao.procedure("UPDATEJIECUN", new Object[] { fromdate, todate });
			} catch (Exception ex) {

			}
		}
		if (title.equals("库存周转率分析表")) {
			if(fromdate.isEmpty()){
				BaseUtil.showError("起始止日期不能为空");
			}
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEKCZZL", new Object[] { fromdate, todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("库存帐龄表*")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEADDTEMP", new Object[] { todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("自定义库存帐龄表")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEADDTEMP", new Object[] { todate});
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("产品分析表")) {
			try {
				baseDao.procedure("UPDATEPRODIOTEMP", new Object[] { fromdate, todate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("集团库存账龄表(按品牌)") || title.equals("集团库存账龄表(按物料)")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEADDTEMP", new Object[] { todate });

			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("生产报废明细表")) {
			try {
				System.out.println(todate);
				baseDao.procedure("UPDATEMAKESCRAPE", new Object[] {});
			} catch (Exception ex) {
			}
		}
		if (title.equals("采购成本降价表(按最新入库)")) {
			try {
				baseDao.procedure("UPDATENEWPRICE", new Object[] { fromdate, todate, enddate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("月COSTDOWN统计表")) {
			try {
				baseDao.procedure("sp_updatepreprice", new Object[] { fromdate, todate, enddate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("月COSTDOWN统计表(优软云)")) {
			try {
				baseDao.procedure("sp_updatepreprice", new Object[] { fromdate, todate, enddate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("集团应收周报表")) {
			try {
				baseDao.procedure("CURRENCYSMONTH_NEW", new Object[] { todate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("超期欠款表")) {
			try {
				String sql = "UPDATE ARBill SET ab_thispayamount=nvl(ab_payamount,0)-nvl((select sum(rbd_nowbalance) from recbalance,recbalancedetail where to_char(rb_date,'yyyymm')>'"
						+ todate + "' and rb_id=rbd_rbid and rb_status='已过账' and rbd_ordercode=ab_code),0)";
				baseDao.execute(sql);
			} catch (Exception ex) {
			}
		}
		if (title.equals("客户信用执行报表")) {
			try {
				baseDao.procedure("sp_CustCreditReport", new Object[] { todate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("批号期间库存")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATEBAREMAIN", new Object[] { todate });
			} catch (Exception ex) {
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("借货未归还明细表")) {
			try {
				baseDao.procedure("SP_UPDATENOWSUMQTY", new Object[] { todate });
			} catch (Exception ex) {
			}
		}
		if (title.equals("出货单标签批量打印")) {
			try {
				if (!idS.equals("")) {
					baseDao.updateByCondition("Prodinout", "pi_printlabel='已打印'", "pi_id in(" + idS + ")");
				}
			} catch (Exception ex) {
				// System.out.println(ex.toString());
			}

		}
		if (title.equals("供应商预付账龄分析表") || title.equals("预付帐龄表")) {
			try {
				baseDao.procedure("UPDATEPREPAYZL", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		if (title.equals("预收账龄分析表") || title.equals("预收帐龄表")) {
			try {
				baseDao.procedure("UPDATEPRERECZL", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		if (title.equals("应付暂估账龄")) {
			try {
				if (baseDao.isDBSetting("sys", "autoCreateApBill") && baseDao.isDBSetting("sys", "useBillOutAP")) {
					baseDao.procedure("UPDATEYFTEMQTY", new Object[] { todate });
				} else {
					baseDao.procedure("UPDATEESINVOQTY", new Object[] { todate });
				}
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}

		if (title.equals("应收票据明细表")) {
			try {
				System.out.println("应收票据明细表todate=" + todate);
				baseDao.procedure("UPDATEBILLAR", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}

		}
		
		if (title.equals("应付票据明细表")) {
			try {
				System.out.println("应付票据明细表todate=" + todate);
				baseDao.procedure("UPDATEBILLAP", new Object[] { todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}

		}
		
		if (title.equals("发出商品账龄")) {
			try {
				if (baseDao.isDBSetting("sys", "autoCreateArBill") && baseDao.isDBSetting("sys", "useBillOutAR")) {
					baseDao.procedure("UPDATEYSTEMQTY", new Object[] { todate });
				} else {
					baseDao.procedure("UPDATEGSINVOQTY", new Object[] { todate });
				}
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		if (title.equals("其他应收账龄表")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.callProcedure("SP_REFRESHAGING.refresh_ar", new Object[] {todate.substring(0,4)+todate.substring(5,7)});
			} catch (Exception ex) {
				System.out.println(ex.toString());
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("其他应付账龄表")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.callProcedure("SP_REFRESHAGING.refresh_ap", new Object[] {todate.substring(0,4)+todate.substring(5,7)});
			} catch (Exception ex) {
				System.out.println(ex.toString());
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("应收明细账(全部)")||title.equals("应收明细账(未全部收款)")||title.equals("应收明细账(已收款)")) {
			if(todate.isEmpty()){
				BaseUtil.showError("截止日期不能为空");
			}
			try {
				baseDao.procedure("UPDATERECAMOUNT", new Object[] {null,fromdate, todate });
			} catch (Exception ex) {
				System.out.println(ex.toString());
				BaseUtil.showError("计算失败");
			}
		}
		if (title.equals("物料收发明细表")) {
			if(fromdate.isEmpty()||todate.isEmpty()){
				BaseUtil.showError("期间不能为空");
			}
			try {
				String str =baseDao.callProcedure("SP_PRINT_PRODINOUTDETAIL", new Object[] { fromdate,todate });
				if (str != null && !str.equals("")) {
					BaseUtil.showError(str);
				}
			} catch (Exception ex) {
				System.out.println(ex.toString());
				BaseUtil.showError("计算失败");
			}
		}
		
		return keys;
	}

}
