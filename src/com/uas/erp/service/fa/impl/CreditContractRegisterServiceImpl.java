package com.uas.erp.service.fa.impl;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.CreditContractRegisterService;

@Service("CreditContractRegisterService")
public class CreditContractRegisterServiceImpl implements CreditContractRegisterService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCreditContractRegister(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 合同编号相同的限制保存
		boolean bool = baseDao.checkByCondition("CreditContractRegister", "ccr_contractno='" + store.get("ccr_contractno") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CreditContractRegister", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		Double ccr_loanamount = Double.parseDouble(store.get("ccr_loanamount").toString());
		Double ccr_loanrate = Double.parseDouble(store.get("ccr_loanrate").toString());
		String ccr_startdate = store.get("ccr_startdate").toString();
		int ccr_months = Integer.parseInt(store.get("ccr_months").toString());
		String ccr_payinterestway = store.get("ccr_payinterestway").toString();
		Object detno = baseDao.getFieldDataByCondition("CreditContractRegisterDet", "max(ccrd_detno)", "ccrd_ccrid="+store.get("ccr_id"));
		detno = detno == null ? 0 : detno;
		//四种算法
		if (ccr_payinterestway.equals("等额本金")) {
			List<Object[]>  objs=principal(ccr_startdate,ccr_loanamount, ccr_loanrate, ccr_months);
			int i = 0;
			for (Object[] os : objs){	
				i++;
				int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+os[0]+"','yyyy-mm-dd'),"+os[2]+","+os[3]+")";
				baseDao.execute(sql);
			}
		} else if (ccr_payinterestway.equals("等额本息")) {
			List<Object[]>  objs=interest(ccr_startdate,ccr_loanamount, ccr_loanrate, ccr_months);
			int i =0;
			for (Object[] os : objs){
				i++;
				int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+os[0]+"','yyyy-mm-dd'),"+os[2]+","+os[3]+")";
				baseDao.execute(sql);
			}
		} else if (ccr_payinterestway.equals("利随本清")) {
			int i = 0;
			i++;
			int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+store.get("ccr_deadline")+"','yyyy-mm-dd'),"+store.get("ccr_loanamount")+","+store.get("ccr_interest")+")";
			baseDao.execute(sql);
		} else if (ccr_payinterestway.equals("不付息")) {
			int i = 0;
			i++;
			int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+store.get("ccr_deadline")+"','yyyy-mm-dd'),"+store.get("ccr_loanamount")+","+0+")";
			baseDao.execute(sql);
		}	
		// 记录操作
		baseDao.logger.save(caller, "ccr_id", store.get("ccr_id")); 
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteCreditContractRegister(int ccr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ccr_id });
		// 删除CreditContractRegister
		baseDao.deleteById("CreditContractRegister", "ccr_id", ccr_id);
		// 记录操作
		baseDao.logger.delete(caller, "ccr_id", ccr_id); 
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ccr_id);;
	}

	@Override
	public void updateCreditContractRegisterById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + store.get("ccr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 更新操作
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CreditContractRegister", "ccr_id");
		baseDao.execute(formSql);	
		//四种算法
		Double ccr_loanamount = Double.parseDouble(store.get("ccr_loanamount").toString());
		Double ccr_loanrate = Double.parseDouble(store.get("ccr_loanrate").toString());
		String ccr_startdate = store.get("ccr_startdate").toString();
		int ccr_months = Integer.parseInt(store.get("ccr_months").toString());
		String ccr_payinterestway = store.get("ccr_payinterestway").toString();
		String sqls = "delete from CreditContractRegisterDet where ccrd_ccrid="+store.get("ccr_id");
		baseDao.execute(sqls);
		Object detno = baseDao.getFieldDataByCondition("CreditContractRegisterDet", "max(ccrd_detno)", "ccrd_ccrid="+store.get("ccr_id"));
		detno = detno == null ? 0 : detno;		
		if (ccr_payinterestway.equals("等额本金")) {
			List<Object[]>  objs=principal(ccr_startdate,ccr_loanamount, ccr_loanrate, ccr_months);
			int i = 0;
			for (Object[] os : objs){	
				i++;
				int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
				System.out.println("1:"+os[0]+";2:"+os[1]+"");
				String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+os[0]+"','yyyy-mm-dd'),"+os[2]+","+os[3]+")";
				baseDao.execute(sql);
			}
		} else if (ccr_payinterestway.equals("等额本息")) {
			List<Object[]>  objs=interest(ccr_startdate,ccr_loanamount, ccr_loanrate, ccr_months);
			int i =0;
			for (Object[] os : objs){
				i++;
				int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+os[0]+"','yyyy-mm-dd'),"+os[2]+","+os[3]+")";
				baseDao.execute(sql);
			}
		} else if (ccr_payinterestway.equals("利随本清")) {
			int i = 0;
			i++;
			int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+store.get("ccr_deadline")+"','yyyy-mm-dd'),"+store.get("ccr_loanamount")+","+store.get("ccr_interest")+")";
			baseDao.execute(sql);
		} else if (ccr_payinterestway.equals("不付息")) {
			int i = 0;
			i++;
			int id1= baseDao.getSeqId("CREDITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into CreditContractRegisterDet (ccrd_id,ccrd_ccrid,ccrd_detno,ccrd_plandate,ccrd_planprincipal,ccrd_planinterest) Values('"+id1+"','"+store.get("ccr_id")+"','"+(Integer.parseInt(detno.toString())+i)+"',to_date('"+store.get("ccr_deadline")+"','yyyy-mm-dd'),"+store.get("ccr_loanamount")+","+0+")";
			baseDao.execute(sql);
		}		
		// 记录操作
		baseDao.logger.update(caller, "ccr_id", store.get("ccr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submitCreditContractRegister(int ccr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ccr_id);
		// 执行提交操作
		baseDao.updateByCondition(caller, "ccr_statuscode='COMMITED',ccr_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.submit(caller, "ccr_id", ccr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ccr_id);
	}

	@Override
	public void resSubmitCreditContractRegister(int ccr_id,String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[] { ccr_id });
		// 执行反提交操作
		baseDao.updateByCondition("CreditContractRegister",
				"ccr_statuscode='ENTERING',ccr_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ccr_id", ccr_id);
		handlerService.afterResSubmit(caller, ccr_id);
	}

	@Override
	public void auditCreditContractRegister(int ccr_id,String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ccr_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "ccr_statuscode='AUDITED',ccr_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.audit(caller, "ccr_id", ccr_id); 
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ccr_id);
	}

	@Override
	public void resAuditCreditContractRegister(int ccr_id,String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ccr_id);
		// 执行反审核操作
		baseDao.updateByCondition(caller, "ccr_statuscode='ENTERING',ccr_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ccr_id", ccr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ccr_id);
	}

	/**
	 * 等额本金还款法【利息少，但前期还的多】
	 * @param totalMoeny 贷款总额
	 * @param rate 贷款商业利率           
	 * @param year 贷款年限            
	 */
	public List<Object[]> principal(String begindate,double totalMoney,double rate,int year){  		           
    	List<Object[]> list = new ArrayList<Object[]>();
    	int totalMonth=year;  
    	//每月本金
        double monthPri=totalMoney/totalMonth;  
        //获取月利率
        double monRate=resMonthRate(rate);            
        BigDecimal   b   =   new   BigDecimal(monRate);    
        monRate   =   b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
        Object [] os= null;
        for(int i=1;i<=totalMonth;i++){  
        	os= new Object[4];
        	begindate=GetSysDate("yyyy-MM-dd", begindate, 0, 1, 0);
        	double monthinterest = (totalMoney-monthPri*(i-1))*monRate;
            double monthRes=monthPri+(totalMoney-monthPri*(i-1))*monRate;  
            BigDecimal   b1   =   new   BigDecimal(monthRes);    
            monthRes   =   b1.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
            //开始还款日期
            os[0]=begindate;
            //每月还款
            os[1]=String.valueOf(monthRes);
            //每月还本金
            os[2]=String.valueOf(monthPri);
            //每月利息
            os[3]=String.valueOf(monthinterest);
            list.add(os); 
           
        } 
        return list;
    }
	/**
	 * 等额本息还款【利息多】
	 * @param totalMoeny 贷款总额          
	 * @param rate 贷款商业利率            
	 * @param year 贷款年限          
	 */
	public List<Object[]> interest(String begindate,Double ccr_loanamount, double rate, int year) {
		List<Object[]> list = new ArrayList<Object[]>();
    	int totalMonth=year;  
    	//每月本金
        double monthPri=ccr_loanamount/totalMonth;  
		//获取月利率
		double monRate = resMonthRate(rate);         
        BigDecimal   b   =   new   BigDecimal(monRate);    
        monRate   =   b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
        Object [] os= null;
        for(int i=1;i<=totalMonth;i++){  
        	os= new Object[4];
        	begindate=GetSysDate("yyyy-MM-dd", begindate, 0, 1, 0);
    		double monInterest = ccr_loanamount * monRate * Math.pow((1 + monRate), year ) / (Math.pow((1 + monRate), year) - 1) - monthPri;
            double monthRes=monthPri+monInterest;  
            BigDecimal   b1   =   new   BigDecimal(monthRes);    
            monthRes   =   b1.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
    		BigDecimal b2 = new BigDecimal(monInterest);
    		monInterest = b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //开始还款日期
            os[0]=begindate;
            //每月还款
            os[1]=String.valueOf(monthRes);
            //每月还本金
            os[2]=String.valueOf(monthPri);
            //每月利息
            os[3]=String.valueOf(monInterest);
            list.add(os);
        }
        return list;
	}

	/**
	 * 转换为月利率 
	 * @param rate
	 * @return
	 */
	public static double resMonthRate(double rate) {
		return rate / 12 * 0.01;
	}
	
	@SuppressWarnings("static-access")
	static String GetSysDate(String format, String StrDate, int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sFmt = new SimpleDateFormat(format);
		cal.setTime(sFmt.parse((StrDate), new ParsePosition(0)));

		if (day != 0) {
			cal.add(cal.DATE, day);
		}
		if (month != 0) {
			cal.add(cal.MONTH, month);
		}
		if (year != 0) {
			cal.add(cal.YEAR, year);

		}
		return sFmt.format(cal.getTime());
	}
}
