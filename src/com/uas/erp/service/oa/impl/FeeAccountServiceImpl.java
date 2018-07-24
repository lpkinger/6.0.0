package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.oa.FeeAccountService;
@Service
public class FeeAccountServiceImpl implements FeeAccountService {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public Object getDate() {		
		return baseDao.getFieldDataByCondition("feelimit", " max(FL_YEARMONTH)", "1=1");
	}

	@Override
	@Transactional
	public void account(int yearmonth) {
		Object nextyearmonth=baseDao.getFieldDataByCondition("(select pd_detno from periodsdetail where pd_code='MONTH-C' and pd_detno>"+yearmonth+" order by pd_detno)", "* ", "rownum<2");
		if(baseDao.getCount("select count(1) from feelimit where fl_yearmonth="+nextyearmonth)>0){
			BaseUtil.showError("已存在下个月额度表，不能结转!");
		}
		int newid=0;
		String code=null;
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean bool = baseDao.checkIf(""+defaultSource+".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
		if(bool){
			SqlRowList rs = baseDao.queryForRowSet("select * from "+defaultSource+".master" );
			while (rs.next()) {
				baseDao.execute("update "+rs.getObject("ma_user")+".feeplease set fp_billdate=trunc(add_months(fp_billdate,1),'mm') where fp_kind in ('差旅费报销单','费用报销单') and "
						+ "fp_statuscode in ('ENTERING','COMMITED')  and to_char(fp_billdate,'yyyymm')="+yearmonth);
			}
		}else{
			String updateSql3="update feeplease set fp_billdate=trunc(add_months(fp_billdate,1),'mm') where fp_kind in ('差旅费报销单','费用报销单') and fp_statuscode in ('ENTERING','COMMITED') "
					+ "and to_char(fp_billdate,'yyyymm')="+yearmonth;
			baseDao.execute(updateSql3);
		}
		String sql = null;
		List<String> detailSqls = new ArrayList<String>();
		if(bool){
			newid=baseDao.getSeqId(defaultSource+".feelimit_seq");
			code=baseDao.sGetMaxNumber(defaultSource+".FeeLimit", 2);
			sql="insert into "+defaultSource+".feelimit(fl_id,fl_code,fl_yearmonth,fl_recorder,fl_indate,fl_statuscode,fl_status) values (?,?,?,?,sysdate,'AUDITED' ,'已审核')";
			detailSqls.add("insert into "+defaultSource+".FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,fld_amount,fld_beginamount,fld_departmentcode,fld_departmentname)"+ 
					" select "+defaultSource+".FeeLimitdetail_seq.nextval,"+newid+",fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,nvl(fld_amount,0)-nvl(fld_actamount,0),nvl(fld_amount,0)-nvl(fld_actamount,0),fld_departmentcode,fld_departmentname "+ 
					" from feelimit,FeeLimitdetail,feekind where fl_id=fld_flid and fld_class=fk_name and fk_iscarryover='是' and fl_yearmonth="+yearmonth);
			//额度类型有 【月末结转余额】 为否的，之前是不会结转到下个月既feelimitdetail 不会生成下个月的记录，现调整为生成记录单期初为空；这个方便到时反提交单据能够还回额度数据
			detailSqls.add("insert into "+defaultSource+".FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,fld_amount,fld_beginamount,fld_departmentcode,fld_departmentname)"+ 
					" select "+defaultSource+".FeeLimitdetail_seq.nextval,"+newid+",fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,0,0,fld_departmentcode,fld_departmentname "+ 
					" from feelimit,FeeLimitdetail,feekind where fl_id=fld_flid and fld_class=fk_name and fk_iscarryover='否' and fl_yearmonth="+yearmonth);
		}else{
			newid=baseDao.getSeqId("feelimit_seq");
			code=baseDao.sGetMaxNumber("FeeLimit", 2);
			sql="insert into feelimit(fl_id,fl_code,fl_yearmonth,fl_recorder,fl_indate,fl_statuscode,fl_status) values (?,?,?,?,sysdate,'AUDITED' ,'已审核')";
			detailSqls.add("insert into FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,fld_amount,fld_beginamount,fld_departmentcode,fld_departmentname)"+ 
					" select FeeLimitdetail_seq.nextval,"+newid+",fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,nvl(fld_amount,0)-nvl(fld_actamount,0),nvl(fld_amount,0)-nvl(fld_actamount,0),fld_departmentcode,fld_departmentname "+ 
					" from feelimit,FeeLimitdetail,feekind where fl_id=fld_flid and fld_class=fk_name and fk_iscarryover='是' and fl_yearmonth="+yearmonth);
			detailSqls.add("insert into "+defaultSource+".FeeLimitdetail(fld_id,fld_flid,fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,fld_amount,fld_beginamount,fld_departmentcode,fld_departmentname)"+ 
					" select "+defaultSource+".FeeLimitdetail_seq.nextval,"+newid+",fld_detno,fld_emcode,fld_emname,fld_emid,fld_class,0,0,fld_departmentcode,fld_departmentname "+ 
					" from feelimit,FeeLimitdetail,feekind where fl_id=fld_flid and fld_class=fk_name and fk_iscarryover='否' and fl_yearmonth="+yearmonth);
		}
		baseDao.execute(sql, new Object[]{newid,code,nextyearmonth,SystemSession.getUser().getEm_name()});
		baseDao.execute(detailSqls);
		//反馈编号：2017110124  @author：lidy   费用额度数据结转增加操作日志
		Employee employee = SystemSession.getUser();
		baseDao.execute("insert into MessageLog (ml_id,ml_date,ml_man,ml_content,ml_result,ml_search,code) "
				+ "values (MessageLog_seq.nextval,sysdate,'"+employee.getEm_name()+"','额度数据结转，期间："+yearmonth+"','额度数据结转成功',null,'"+yearmonth+"')");
	}

	@Override
	@Transactional
	public void beforeaccount(int yearmonth) {
		//启用集团管控方式，查询所有子账套
		Object defaultSource = BaseUtil.getXmlSetting("defaultSob");
		boolean UnionChargeAmount = false;
		if(defaultSource!=null && !defaultSource.equals("")){
			UnionChargeAmount = baseDao.checkIf( defaultSource+".configs", "caller='FeeLimit' and code='UnionChargeAmount' and data=1");
			if(UnionChargeAmount){
				List<Master> masters = enterpriseService.getMasters();
				for (Master m : masters) {
					boolean bool = baseDao.checkIf( m.getMa_user()+".feeplease", "to_char(fp_billdate,'yyyymm')=" + yearmonth + " and fp_kind in ('差旅费报销单','费用报销单') and fp_statuscode in ('ENTERING','COMMITED') ");
					if (bool) {
						BaseUtil.showError("当前期间还存在已提交、在录入的单据，会将这部分单据的日期变更为下个月1号");
					}
				}
			}else{
				boolean bool = baseDao.checkIf("feeplease", "to_char(fp_billdate,'yyyymm')=" + yearmonth + " and fp_kind in ('差旅费报销单','费用报销单') and fp_statuscode in ('ENTERING','COMMITED') ");
				if (bool) {
					BaseUtil.showError("当前期间还存在已提交、在录入的单据，会将这部分单据的日期变更为下个月1号");
				}
			}
		}		
	}
}
