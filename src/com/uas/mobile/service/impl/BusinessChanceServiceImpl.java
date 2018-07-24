package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.BusinessChanceService;
import com.uas.mobile.service.CustomerService;

import freemarker.template.utility.Execute;

@Service("mobileBusinessChanceService")
public class BusinessChanceServiceImpl implements BusinessChanceService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private CustomerService customerService;
	@Override
	public List<Map<String, Object>> getBusinessChanceByMonthAndProcess(String emcode, String currentdate, String currentprocess,
			int start, int end) {

		Employee employee = employeeDao.getEmployeeByEmcode(emcode);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String condition = "";
		if (!"admin".equals(employee.getEm_type())) {
			Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + employee.getEm_code() + "'");
			if (bool) {
				List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(employee.getEm_code());
				condition += " and bc_domancode in (";
				for (Employee em : employeeList) {
					condition += "'" + em.getEm_code() + "',";
				}
				condition = condition.substring(0, condition.lastIndexOf(",")) + ")";

			} else
				condition = " and bc_domancode='" + employee.getEm_code() + "' ";
		}

		/*List<Object[]> objs = baseDao
				.getFieldsDatasByCondition(
						"(select a.*,rownum rn from (select * from   businesschance  where bc_currentprocess='"
								+ currentprocess
								+ "' "
								+ condition
								+ " and "
								+ currentdate
								+ " order by bc_recorddate desc) a where rownum<=" + end + ")", new String[] { "bc_tel", "bc_code",
								"bc_remark", "to_char(bc_recorddate,'yyyy-mm-ddhh24:mi:ss')", "bc_nichehouse", "bc_description", "bc_id",
								"bc_from" }, "rn>" + start);*/
		String datesql="SELECT bc_tel,bc_code,bc_remark,to_char(bc_recorddate,'yyyy-mm-dd hh24:mi:ss') bc_recorddate,bc_nichehouse,bc_description,bc_id,bc_address,bc_from FROM (select a.*,rownum rn from (select * from   businesschancestage left join businesschance on bs_name=bc_currentprocess  where bc_currentprocess='"+currentprocess+"' "+condition+" and "+currentdate+" order by bc_recorddate desc) a where rownum<="+end+") WHERE rn>"+start+"";
		SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(datesql);
		while(rs.next()){
			map = new HashMap<String, Object>();
			map.put("bc_tel", rs.getString("bc_tel"));
			map.put("bc_code", rs.getString("bc_code"));
			map.put("bc_remark", rs.getString("bc_remark"));
			map.put("bc_recorddate", rs.getString("bc_recorddate"));
			map.put("bc_nichehouse", rs.getString("bc_nichehouse"));
			map.put("bc_description", rs.getString("bc_description"));
			map.put("bc_id", rs.getString("bc_id"));
			map.put("bc_from", rs.getString("bc_from"));
			map.put("bc_address", rs.getString("bc_address"));
			lists.add(map);
		}
		/*for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("bc_tel", obj[0]);
			map.put("bc_code", obj[1]);
			map.put("bc_remark", obj[2]);
			map.put("bc_recorddate", obj[3]);
			map.put("bc_nichehouse", obj[4]);
			map.put("bc_description", obj[5]);
			map.put("bc_id", obj[6]);
			map.put("bc_from", obj[7]);
			lists.add(map);
		}*/
		return lists;
	}

	@Override
	public List<Map<String, Object>> getBusinessChancebyMonth(String currentdate, Employee employee) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String condition = "";
		if (!"admin".equals(employee.getEm_type())) {
			Boolean bool = baseDao.checkIf("hrorg", "or_headmancode='" + employee.getEm_code() + "'");
			if (bool) {
				List<Employee> employeeList = employeeDao.getHrorgEmployeesByEmcode(employee.getEm_code());
				condition += " and bc_domancode in (";
				for (Employee em : employeeList) {
					condition += "'" + em.getEm_code() + "',";
				}
				condition = condition.substring(0, condition.lastIndexOf(",")) + ")";

			} else
				condition = " and bc_domancode='" + employee.getEm_code() + "' ";
		}
		List<Object[]> objs = baseDao
				.getFieldsDatasByCondition(
						"( SELECT bs_name currentprocess,COUNT(bc_currentprocess) COUNT,to_char(case when COUNT(bc_currentprocess)=0 then 0 else round(100*COUNT(bc_currentprocess)/SUM(COUNT(bc_currentprocess)) "
								+ "OVER(),2) end ,'fm990.00')||'%' percent,bs_color color,bs_detno detno FROM BusinessChanceStage left join BusinessChance on  bs_name=bc_currentprocess and  "
								+ currentdate
								+ " and bc_currentprocess is not null "
								+ condition
								+ " left join businessdatabase on bc_nichehouse=bd_name and nvl(bd_prop,' ')<>'管理员分配' GROUP BY bs_name,bs_color,bs_detno)",
						new String[] { "currentprocess", "count", "percent", "color", "detno" }, "1=1  order by detno ");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("currentprocess", obj[0]);
			map.put("count", obj[1]);
			map.put("percent", obj[2]);
			map.put("color", obj[3]);
			map.put("detno", obj[4]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public List<Map<String, Object>> getBusinessChanceCombo(Employee employee, String caller, String field) {
		String sql = "select dlc_display,dlc_value from datalistcombo where dlc_caller='" + caller + "' and lower(dlc_fieldname)='" + field
				+ "' order by dlc_detno asc";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getBusinessChanceStage(String condition) {
		String sql = "select * from BusinessChanceStage where " + condition + " order by bs_detno";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getBusinessChanceRecorder(String condition) {
		String sql = "select distinct em_code,bc_doman from BusinessChance left join employee on em_name=bc_doman where " + condition;
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatebusinessChanceData(String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(gridStore);
		String type = (String) store.get("bcd_type");		
		if(type.equals("失效")){			
			boolean bool=baseDao.checkByCondition("customer", "cu_auditstatuscode='AUDITED' AND cu_nichecode='"+store.get("bcd_bccode")+"'");
			if(!bool)BaseUtil.showError("已转的正式客户已审核，不能失效");
			updatebusinessChangeDateimp(store,caller,type);
		}else {
			updatebusinessChangeDateimp(store,caller,type);	
		}
	}

	public void updatebusinessChangeDateimp(Map<Object, Object> store,String caller,String type){
		if(store.get("bcd_code")==null||"".equals("bcd_code")){
			String bcdCode = baseDao.sGetMaxNumber("BUSINESSCHANCEDATA", 2);
			store.put("bcd_code", bcdCode);
		}
		store.put("bcd_id", baseDao.getSeqId("BusinessChanceData_SEQ"));
		store.put("bcd_status", "已提交");
		store.put("bcd_statuscode", "COMMITED");
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BusinessChanceData", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update BusinessChanceData set bcd_bcid=(select bc_id from BusinessChance where bcd_bccode=bc_code) where bcd_id="
				+ store.get("bcd_id"));
		// 客户端没传商机阶段编号
		baseDao.execute("update BusinessChanceData set bcd_bscode=(select bs_code from BUSINESSCHANCESTAGE where bs_name=bcd_bsname) where bcd_id="
				+ store.get("bcd_id"));
		
		// 继续跟进如果该商机存在于日程当中，则要删除日程，商机返回到未排日程
		String sqlselect = "select  sourcecode from projecttask where taskorschedule='Schedule' and sourcecode='"
				+ store.get("bcd_bccode")
				+ "' and nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')";
		int count = baseDao.getCount("select count(1) from ("+sqlselect+")");
		if (count > 0) {
			String delsql = "delete from projecttask where taskorschedule='Schedule' and sourcecode='" + store.get("bcd_bccode") + "'";
			baseDao.execute(delsql);
		}
		if(!type.equals("失效")){
			//新华商智特有的逻辑，商机动态审核后直接更新商机主表的当前阶段和最后更新日期。
			if(baseDao.isDBSetting(caller, "UPDATACURRENTPROCESS")){ 
				baseDao.execute("update BusinessChance set bc_lastdate=sysdate,bc_currentprocess='" + store.get("bcd_bsname") + "' where bc_code='"
						+ store.get("bcd_bccode") + "'");	
			}else {
				Object bs_detno=baseDao.getFieldDataByCondition("businesschancestage", "bs_detno", "bs_name='"+store.get("bcd_bsname")+"'");	
				Object olddetno=baseDao.getFieldDataByCondition("businesschancestage", "bs_detno", "bs_name=(select bc_currentprocess from businesschance where bc_code='"+store.get("bcd_bccode")+"')");	
				if(bs_detno!=null&&!bs_detno.equals("")&&olddetno!=null&&!olddetno.equals("")){
					if(Integer.parseInt(bs_detno.toString())>Integer.parseInt(olddetno.toString())){
						baseDao.execute("update BusinessChance set bc_lastdate=sysdate,bc_currentprocess='" + store.get("bcd_bsname") + "' where bc_code='"
									+ store.get("bcd_bccode") + "'");
					}					
				}else {
					BaseUtil.showError("录入的商机或者商机阶段有错误");
					}
			}
		}
		
		handlerService.beforeSubmit(caller, store.get("bcd_id"));
		// 记录操作
		baseDao.logger.update(caller, "bcd_id", store.get("bcd_id"));
		baseDao.logger.submit(caller, "bcd_id", store.get("bcd_id"));
		//String type = (String) store.get("bcd_type");
		handlerService.afterSubmit(caller, store.get("bcd_id"));
	}
	
	@Override
	public void abateBusinessChance(int bcd_id, String caller) {

		baseDao.updateByCondition("BusinessChanceData",
				"bcd_statuscode='AUDITED',bcd_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "bcd_id=" + bcd_id);
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='UNVALID', bc_status='已失效'",
				"bc_id=(select bcd_bcid from BusinessChanceData where bcd_type='失效' and bcd_id=" + bcd_id + ")");
		baseDao.logger.audit(caller, "bcd_id", bcd_id);
		
	}

	@Override
	public void updateBusinessChanceHouse(String bc_code, String bc_nichehouse) {
		baseDao.updateByCondition("BusinessChance", "bc_nichehouse='" + bc_nichehouse + "'", "bc_code='" + bc_code + "'");
	}

	@Override
	public void updateBusinessChanceCust(String bc_code, String cu_code, String cu_name) {
		/**
		 * 单据编号：2017030537  客户资料保存时检查该商机已有关联客户，增加一个判空条件cu_nichecode<>'null'。
		 * @author wsy
		 */
		boolean bool = baseDao.checkIf("customer", "cu_nichecode='" + bc_code + "' and cu_code<>'"+cu_code+"' and cu_nichecode<>'null'");
		if (bool)
			BaseUtil.showError("该商机已有关联客户,请不要再转!");
		else {
			Object[] custMsg = baseDao.getFieldsDataByCondition("customer left join businesschancestage on cu_nichestep=bs_name",
					new String[] { "cu_add1", "cu_contact", "cu_degree", "cu_tel", "bs_detno" }, "cu_code='" + cu_code + "'");
			// 更新商机的客户资料
			if (custMsg != null) {
				baseDao.updateByCondition("BusinessChance", "bc_custcode='" + cu_code + "',bc_custname='" + cu_name + "',bc_address='"
						+ (custMsg[0] == null ? "" : custMsg[0]) + "',bc_contact='" + (custMsg[1] == null ? "" : custMsg[1])
						+ "',bc_position='" + (custMsg[2] == null ? "" : custMsg[2]) + "',bc_tel='"
						+ (custMsg[3] == null ? "" : custMsg[3]) + "'", "bc_code='" + bc_code + "'");
				baseDao.execute("update customer set cu_nichecode='"+bc_code+"' where cu_code='"+cu_code+"'");
				// 如果商机的当前阶段大于客户的商机阶段，则更新客户的商机阶段
				Object[] stage = baseDao.getFieldsDataByCondition(
						"businesschance lef join businesschancestage on bc_currentprocess=bs_name", new String[] { "bs_detno",
								"bc_currentprocess" }, "bc_code='" + bc_code + "'");
				if (custMsg[4] == null) {
					if (stage!=null&&stage[1] != null) {
						baseDao.updateByCondition("customer", "cu_nichestep='" + stage[1] + "'", "cu_code='" + cu_code + "'");
					}
				} else if (stage!=null&&stage[1] != null) {
					if (Integer.parseInt(custMsg[4].toString()) < Integer.parseInt(stage[0].toString())) {
						baseDao.updateByCondition("customer", "cu_nichestep='" + stage[1] + "'", "cu_code='" + cu_code + "'");
					}
				}
			}
		}
	}

	@Override
	public void updateBusinessChanceDoman(String bc_code, String bc_doman, String bc_domancode) {
		
		Object bb_dogap=baseDao.getFieldDataByCondition("businessbasis", "bb_dogap", "1=1");
		Object bd_orgrecovertime=baseDao.getFieldDataByCondition("businessbasis", "bd_orgrecovertime", "1=1");
		Object time=baseDao.getFieldDataByCondition("BusinessChanceData", "(sysdate-max(bcd_date))", "bcd_man='"+bc_doman+"' and bcd_count='-1' and bcd_bcid=(select bc_id from BusinessChance where bc_code='"+bc_code+"')");			
		List<Object[]> orgargument=baseDao.getFieldsDatasByCondition("BusinessChanceData", new String[]{"(sysdate-bcd_date)","bcd_man"}, " bcd_count='-1' and bcd_bcid=(select bc_id from BusinessChance where bc_code='"+bc_code+"') order  by bcd_date desc");
		if(bb_dogap!=null&&!"".equals(bb_dogap)&&time!=null&&!"".equals(time)&&time!=""){
				if(Integer.parseInt(String.valueOf(bb_dogap))>Integer.parseInt(String.valueOf(time).split("\\.")[0])){				
					BaseUtil.showError("商机在规定时间内不允许一个人重复跟进!");
				}
			}
		if(bd_orgrecovertime!=null&&!"".equals(bd_orgrecovertime)
				&&Integer.parseInt(bd_orgrecovertime.toString())!=0
					&&orgargument!=null&&!"".equals(orgargument)&&orgargument.size()>0){		
				Object manname=orgargument.get(0)[1];
				Object mandata=orgargument.get(0)[0];
				String sql="select count(1) from employee where em_code='"+bc_domancode+"' and em_defaultorid =(select em_defaultorid from employee where em_name='"+manname+"')";
				if(baseDao.getCount(sql)>0&&Integer.parseInt(bd_orgrecovertime.toString())>=Integer.parseInt(String.valueOf(mandata).split("\\.")[0])){
					BaseUtil.showError("同组织的人人员N天不允许重复跟进有同一商机！");
				};
		}
		int flag = baseDao.getCount("select * from businesschance where nvl(bc_domancode,' ')<>' '  and bc_code='" + bc_code + "'");
		if (flag != 0) {
			BaseUtil.showError("该商机已被抢");
		}
		baseDao.updateByCondition("BusinessChance", "bc_doman='" + bc_doman + "',bc_lastdate=sysdate,bc_domancode='" + bc_domancode + "'",
				"bc_code='" + bc_code + "' and bc_doman is null and bc_domancode is null");
	}

	@Override
	public void updateBusinessChanceType(String bc_code,String bc_nichehouse) {
		boolean bool = baseDao.checkIf("customer","cu_nichecode=(select bc_code  from businesschance where bc_code='"+bc_code+"') and cu_auditstatuscode='AUDITED'");		
		if(bool){
			BaseUtil.showError("商机已转正式客户，不允许释放!");
		}	
		
		String sqlselect = "select  sourcecode from projecttask where taskorschedule='Schedule' and sourcecode='"
				+ bc_code
				+ "' and nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')";
		int count = baseDao.getCount("select count(1) from ("+sqlselect+")");
		if (count > 0) {
			String delsql = "delete from projecttask where taskorschedule='Schedule' and sourcecode='" + bc_code + "'";
			baseDao.execute(delsql);
		}
		
		String bcd_code=baseDao.sGetMaxNumber("BUSINESSCHANCEDATA", 2);
		String sql = ("Insert into BUSINESSCHANCEDATA (BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE) select BUSINESSCHANCEDATA_seq.nextval,bc_id,bc_doman,sysdate,'释放商机','已审核','AUDITED',bs_code,bc_currentprocess,'"+bcd_code+"',-1,'释放商机' from BUSINESSCHANCE LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_code='"+bc_code+"'");		
		baseDao.execute(sql);
		if(bc_nichehouse!=null){
			baseDao.updateByCondition("BusinessChance", " bc_nichehouse='"+bc_nichehouse+"',bc_doman=null ,bc_domancode=null ,bc_lastdate=null,bc_currentprocess=(select bs_name from businesschancestage where bs_detno=1)", "bc_code='" + bc_code + "'");
		}else {
			baseDao.updateByCondition("BusinessChance", " bc_doman=null ,bc_domancode=null ,bc_lastdate=null,bc_currentprocess=(select bs_name from businesschancestage where bs_detno=1)", "bc_code='" + bc_code + "'");
		}
	}

	@Override
	public List<Map<String, Object>> getnichedata(String bc_domancode, int type, int pageIndex) {
		// type=0,代表未安排日程，type=1,已安排日程,type=2,代表即将超时,type=3,已转客户
		int pageSize = 10;
		int start = ((pageIndex - 1) * pageSize + 1);
		int end = pageIndex * pageSize;
		System.out.println("start=" + start);
		System.out.println("end" + end);
		String sql = "";
		if (type == 0) {
			// sql="select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance where bc_domancode='"+bc_domancode+"' and bc_code not in(select sourcecode from projecttask where nvl(sourcecode,' ')<>' ') order by bc_recorddate desc";
			sql = "select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description,bc_custname,bc_currentprocess,bc_address from BusinessChance where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "' and bc_code not in(select sourcecode from projecttask where nvl(sourcecode,' ')<>' ') order by bc_lastdate desc,bc_recorddate desc) a where rownum <="
					+ end + ") where rn>=" + start + "";
		}
		if (type == 1) {
			// sql="select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance where bc_domancode='"+bc_domancode+"' and bc_code  in(select sourcecode from (select sourcecode from projecttask where nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')  order by startdate asc) a)order by bc_recorddate desc";
			sql = "select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description,bc_custname,bc_currentprocess,bc_address from BusinessChance where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "' and bc_code  in(select sourcecode from (select sourcecode from projecttask where nvl(sourcecode,' ')<>' ' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')  order by startdate asc) a)order by bc_lastdate desc,bc_recorddate desc) a where rownum <="
					+ end + ") where rn>=" + start + "";
		}
		if (type == 2) {
			// sql="select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance, BusinessChanceStage where bs_name=bc_currentprocess and bc_domancode='"+bc_domancode+"'  and (to_date(to_char(BC_LASTDATE+bs_days,'YYYY-MM-DD'),'yyyy-MM-dd')-to_date(to_char(sysdate,'YYYY-MM-DD'),'yyyy-MM-dd'))<=(select case when nvl(bb_remindtime,0)=0 then 0 else bb_remindtime end remindtime from BUSINESSBASIS where rownum=1)order by bc_lastdate desc) a where rownum <="+end+") where rn>="+start+"";
			// 判断商机是否存在日程当中
			// 如果商机存在日程中，活跃天数为bb_recovertime
			Object recovertime = baseDao.getFieldDataByCondition("BUSINESSBASIS", "bb_recovertime", "1=1");
			/*sql = "select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance, 	 where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and bs_name=bc_currentprocess and bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "'  and (to_date(to_char(BC_LASTDATE+case when bc_code in(select sourcecode from projecttask where taskorschedule='Schedule') then "
					+ recovertime
					+ " else  bs_days end,'YYYY-MM-DD'),'yyyy-MM-dd')-to_date(to_char(sysdate,'YYYY-MM-DD'),'yyyy-MM-dd'))<=(select case when nvl(bb_remindtime,0)=0 then 0 else bb_remindtime end remindtime from BUSINESSBASIS where rownum=1)order by bc_lastdate desc) a where rownum <="
					+ end + ") where rn>=" + start + "";*/
			Object remindday=baseDao.getFieldDataByCondition("BUSINESSBASIS","bb_remindtime","1=1");
			sql = "select * from( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description,bc_custname,bc_currentprocess,bc_address from BusinessChance LEFT JOIN BusinessChanceStage ON bc_currentprocess=bs_name left join projecttask on sourcecode=bc_code where resourcecode=bc_domancode and  taskorschedule='Schedule' and statuscode<>'FINISHED' and nvl(bc_statuscode,' ')<>'UNVALID' and bc_domancode='"+bc_domancode+"' and bc_domancode is not null and nvl(bc_custname,' ') not in (select cu_name from customer where cu_auditstatuscode='AUDITED') and case when sourcecode is not null and to_char(nvl(enddate,sysdate)+"
					+ recovertime
					+ ",'yyyymmdd')>to_char(trunc(bc_lastdate)+bs_days,'yyyymmdd') then  nvl(enddate,sysdate)+"
					+ recovertime
					+ "-trunc(sysdate) else  trunc(bc_lastdate)+bs_days-trunc(sysdate)end <="
					+ remindday
					+ " and case when sourcecode is not null and to_char(nvl(enddate,sysdate)+"
					+ recovertime
					+ ",'yyyymmdd')>to_char(trunc(bc_lastdate)+bs_days,'yyyymmdd') then  nvl(enddate,sysdate)+"
					+ recovertime
					+ "-trunc(sysdate) else  trunc(bc_lastdate)+bs_days-trunc(sysdate)end>0) a where rownum <= "+end+") where rn>="+start+"";
		}
		if (type == 3) {
			// sql="select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance where bc_domancode='"+bc_domancode+"' and bc_custcode in (select cu_code from customer where nvl(cu_code,' ')<>' ')order by bc_recorddate desc";
			sql = "select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description,bc_custname,bc_currentprocess,bc_address from BusinessChance where nvl(bc_status,' ')<>'已失效' and bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "' and bc_code in (select cu_nichecode from customer where nvl(cu_code,' ')<>' ')order by bc_lastdate desc,bc_recorddate desc) a where rownum <="
					+ end + ") where rn>=" + start + "";
		}
		System.out.println(sql);
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public int nichlecount(String bc_domancode, int type) {
		// TODO Auto-generated method stub
		String sql = "";
		if (type == 0) {
			sql = "select count(1) from BusinessChance where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and   bc_doman is not null and bc_domancode='"
					+ bc_domancode + "' and bc_code not in(select sourcecode from projecttask where nvl(sourcecode,' ')<>' ')";
		}
		if (type == 1) {
			sql = "select count(1) from BusinessChance where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and  bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "' and bc_code  in(select  sourcecode from projecttask where nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD'))";
		}
		if (type == 2) {
			Object recovertime = baseDao.getFieldDataByCondition("BUSINESSBASIS", "bb_recovertime", "1=1");
			// sql="select * from ( select a.*,rownum rn from(select bc_id,bc_code,bc_doman,bc_from,bc_tel,bc_remark,TO_CHAR(bc_recorddate, 'YYYY-MM-DD') bc_recorddate,bc_nichehouse,bc_description from BusinessChance, BusinessChanceStage where nvl(bc_status,' ')<>'已失效' and bs_name=bc_currentprocess and bc_domancode='"+bc_domancode+"'  and (to_date(to_char(BC_LASTDATE+case when bc_code in(select sourcecode from projecttask where taskorschedule='Schedule') then "+recovertime+" else  bs_days end,'YYYY-MM-DD'),'yyyy-MM-dd')-to_date(to_char(sysdate,'YYYY-MM-DD'),'yyyy-MM-dd'))<=(select case when nvl(bb_remindtime,0)=0 then 0 else bb_remindtime end remindtime from BUSINESSBASIS where rownum=1)order by bc_lastdate desc) a where rownum <="+end+") where rn>="+start+"";
			Object remindday=baseDao.getFieldDataByCondition("BUSINESSBASIS","bb_remindtime","1=1");
			sql = "select count(1) from BusinessChance LEFT JOIN BusinessChanceStage ON bc_currentprocess=bs_name left join projecttask on sourcecode=bc_code where resourcecode=bc_domancode and  taskorschedule='Schedule' and statuscode<>'FINISHED' and nvl(bc_statuscode,' ')<>'UNVALID' and bc_domancode is not null and nvl(bc_custname,' ') not in (select cu_name from customer where cu_auditstatuscode='AUDITED') and case when sourcecode is not null and to_char(nvl(enddate,sysdate)+"
					+ recovertime
					+ ",'yyyymmdd')>to_char(trunc(bc_lastdate)+bs_days,'yyyymmdd') then  nvl(enddate,sysdate)+"
					+ recovertime
					+ "-trunc(sysdate) else  trunc(bc_lastdate)+bs_days-trunc(sysdate)end <="
					+ remindday
					+ " and case when sourcecode is not null and to_char(nvl(enddate,sysdate)+"
					+ recovertime
					+ ",'yyyymmdd')>to_char(trunc(bc_lastdate)+bs_days,'yyyymmdd') then  nvl(enddate,sysdate)+"
					+ recovertime
					+ "-trunc(sysdate) else  trunc(bc_lastdate)+bs_days-trunc(sysdate)end>0";
			/*sql = "select count(1) from BusinessChance, BusinessChanceStage where nvl(bc_custcode,' ')=' ' and nvl(bc_status,' ')<>'已失效' and bs_name=bc_currentprocess and bc_doman is not null and bc_domancode='"
					+ bc_domancode
					+ "'  and (to_date(to_char(BC_LASTDATE+case when bc_code in(select sourcecode from projecttask where taskorschedule='Schedule') then "
					+ recovertime
					+ " else  bs_days end,'YYYY-MM-DD'),'yyyy-MM-dd')-to_date(to_char(sysdate,'YYYY-MM-DD'),'yyyy-MM-dd'))<=(select case when nvl(bb_remindtime,0)=0 then 0 else bb_remindtime end remindtime from BUSINESSBASIS where rownum=1)";*/
		}
		if (type == 3) {
			sql = "select count(1) from BusinessChance where nvl(bc_status,' ')<>'已失效' and bc_doman is not null and bc_domancode='"
					+ bc_domancode + "' and bc_code in (select cu_nichecode from customer where nvl(cu_code,' ')<>' ')";
		}
		int count = baseDao.getCount(sql);
		return count;
	}

	@Override
	public String updateLastdate(String bc_code) {
		// TODO Auto-generated method stub
		String sql = "update BusinessChance set bc_lastdate=sysdate where bc_code='" + bc_code + "'";
		try {
			baseDao.execute(sql);
			return "success";
		} catch (Exception ex) {
			return "fail";
		}

	}

	@Override
	public List<Map<String, Object>> getNichehouse() {
		String sql = "select distinct bd_name,bd_prop from BusinessDataBase";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public String isadmin(String em_code) {
		// TODO Auto-generated method stub
		String value = baseDao.getFieldDataByCondition("employee", "em_type", "em_code='" + em_code + "'").toString();
		return value;
	}

	@Override
	public void updateBusinessChanceDataMsg(String bc_code, String bc_doman,
			String bc_domancode,int type) {	
		String bcd_Code = baseDao.sGetMaxNumber("BUSINESSCHANCEDATA", 2);
		if(type==0){
			String sql="Insert into BUSINESSCHANCEDATA (BCD_BCCODE,BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE)  select bc_code,BUSINESSCHANCEDATA_seq.nextval,bc_id,bc_doman,sysdate,'抢占商机','已审核','AUDITED',bs_code,bc_currentprocess,'"+bcd_Code+"',1,'抢占商机' from BUSINESSCHANCE LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_code='"+bc_code+"'";		
			baseDao.execute(sql);
		}else if(type==1){
			String sql="Insert into BUSINESSCHANCEDATA (BCD_BCCODE,BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE)  select bc_code,BUSINESSCHANCEDATA_seq.nextval,bc_id,bc_doman,sysdate,'商机分配','已审核','AUDITED',bs_code,bc_currentprocess,'"+bcd_Code+"',1,'商机分配' from BUSINESSCHANCE LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_code='"+bc_code+"'";
			baseDao.execute(sql);
		}else{
			BaseUtil.showError("type类型错误");
		}
		
		
		
	}
	
	public void isBusinesslimit(String bc_doman){
		Object BB_MAXRECVNUM = baseDao.getFieldDataByCondition("BUSINESSBASIS", "BB_MAXRECVNUM", "1=1");		
		int count=baseDao.getCountByCondition("businesschance","BC_DOMAN='"+bc_doman+"'and nvl(bc_status,' ')<>'已失效' and ((bc_custcode is not null and  bc_code not in(select cu_nichecode from customer where cu_auditstatuscode='AUDITED'))or bc_custcode is null)");				
		if(BB_MAXRECVNUM!=null){
			if (Integer.parseInt(BB_MAXRECVNUM.toString())<=count) {				
				BaseUtil.showError("已经超过最大跟进数,不能抢占或分配或者创建");
			}
		}		
		
	}
	
	@Override
	public Map<String, Object> getStagePoints(String bccode,String currentStep){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		List<Map<String,Object>> datalist = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		List<String> list = null;
		
		String columnStr = "";
		Object[] obj = baseDao.getFieldsDataByCondition("businesschancestage", new String[]{"bs_point","bs_pointdetno"},"bs_name='" + currentStep + "'");
		if(obj!=null){
			if(obj[0]!=null&&obj[1]!=null){
				String[] dataDetnos = obj[1].toString().split("#");
				
				for(int i=0;i<dataDetnos.length;i++){
					columnStr += "," + "bcd_column" + dataDetnos[i];
				}				
				columnStr = columnStr.substring(1);
				
				String bcdSql = "select " + columnStr + " from businesschancedata where bcd_bcid=(select bc_id from businesschance where bc_code='"+bccode+"') and bcd_bsname='"+currentStep+"'";
				List<Map<String,Object>> colData = baseDao.queryForList(bcdSql);				
				//取阶段描述
				if(colData.size()>0){
					String[] points = obj[0].toString().split("#");
					for(int i=0;i<points.length;i++){
						map = new HashMap<String,Object>();
						map.put("caption", points[i]);
						list = new ArrayList<String>();
						for(int j=0;j<colData.size();j++){
							Map<String,Object> column = colData.get(j);
							Object value = column.get("BCD_COLUMN" + dataDetnos[i]);
							if(value!=null){
								list.add(value.toString());
							}else{
								list.add("");
							}					
						}
						map.put("value",list);
						datalist.add(map);
					}					
				}
			}
		}
		modelMap.put("data", datalist);
		return modelMap;
	}
	//添加联系人
	@Override
	public Map<String,Object> addContactPerson(String caller,String formStore){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		List<Map<Object, Object>> form = BaseUtil
				.parseGridStoreToMaps(formStore);
		List<String> list=new ArrayList<String>();
		int ct_id=0;
		for(int i=0;i<form.size();i++){
			ct_id=baseDao.getSeqId("BUSINESSCHANCECONTACT_SEQ");
			form.get(i).put("ct_id", ct_id);
			Object ct_sourcecode=form.get(i).get("ct_sourcecode");
			String sqls="delete from BUSINESSCHANCECONTACT where ct_sourcecode='"+ct_sourcecode+"'";
			list.add(sqls);
			
		}
		baseDao.execute(list);;
		List<String> Sql = SqlUtil.getInsertSqlbyGridStore(form, "BUSINESSCHANCECONTACT");
		baseDao.execute(Sql);		
		return modelMap;
	}
	//商机搜索
	public List<Map<String,Object>> searchData(String stringSearch,int start,int end){
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
		Map<String,Object> modelMap=null;
		Map<String,Object> map=null;
		Map<String,Object> dmap=null;
		List<Object> datas=baseDao.getFieldDatasByCondition("ProductBrand", "pb_name", "pb_name like '%"+stringSearch+"%' and rownum<"+end+" and rownum>"+start+"");
		List<Object[]> mdatas=baseDao.getFieldsDatasByCondition("product", new String[] {"pr_detail","pr_spec"},"pr_detail like'%"+stringSearch+"%' and pr_spec like '%"+stringSearch+"%' and rownum<"+end+" and rownum>"+start+"");
		for(Object data:datas){
			modelMap=new HashMap<String,Object>();
			modelMap.put("result", data);
			list.add(modelMap);
		}
		
		for(Object[] mdata:mdatas){
			map=new HashMap<String,Object>();
			dmap=new HashMap<String,Object>();
			map.put("result", mdata[0]);
			dmap.put("result", mdata[1]);
			list.add(map);
			list.add(dmap);
		}
		
		return list;
	}
}
