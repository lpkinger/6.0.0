package com.uas.erp.service.as.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.SqlServerDao;
import com.uas.erp.model.Page;
import com.uas.erp.service.as.NewBackService;

@Service("newBackService")
public class NewBackServiceImpl implements NewBackService {

	private SqlServerDao dao = SqlServerDao.getSqlServerDao();
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<?> getNewBack(Map<String, Object> filters) {
		String condition = parseFilter(filters);
		if (StringUtil.hasText(condition))
			condition = " and " + condition;
		SqlRowList list = dao
				.query("SELECT newfittingback_no,store_name,CONVERT(varchar(100),back_day,23) as back_day,shenqing_op,objective_store,bill_detail,beizhu from newfitting_back left join service_store on newfitting_back.store_id= service_store.store_id where isnull(status,'')<>'已处理' and isnull(bill_runflags,'')='OK'"
						+ condition + " order by newfittingback_no");
		return list.getResultList();
	}

	@Override
	public List<?> getNewBackDetail(String code) {
		SqlRowList list = dao
				.query("SELECT ID,newfittingback_detail.fitting_code FITTING_CODE, FITTING_NAME,queren_quantity quantity,UNIT,KEHEXIAO,goodorbad,BEIZHU from newfittingback_detail left join fitting_table on newfittingback_detail.fitting_code= fitting_table.fitting_code where newfittingback_no='"
						+ code + "' order by id");
		return list.getResultList();
	}

	@Override
	public String newBackToProdIO(String data) {
		StringBuffer sb = new StringBuffer();
		int okNum = 0;
		int piid = 0;
		String inoutno = null;
		String piclass = null;
		String pitype = null;
		if (StringUtil.hasText(data)) {
			String log = null;
			String codes = CollectionUtil.toSqlString(data.split(","));
			SqlRowList rs = dao
					.query("SELECT * from newfitting_back where newfittingback_no in("
							+ codes + ") order by newfittingback_no");
			while (rs.next()) {
				String code = rs.getString("newfittingback_no");
				piid = baseDao.getSeqId("PRODINOUT_SEQ");
				inoutno = baseDao
						.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
				piclass = "拨出单";
				pitype = "网点调拨";
				baseDao.execute("insert into prodinout(pi_id, pi_inoutno, pi_class, pi_date, pi_recorddate, pi_currency, pi_rate, pi_status, pi_statuscode,pi_invostatus,pi_invostatuscode, pi_recordman, pi_type,pi_fromcode,pi_remark,PI_REFNO,pi_printstatus,pi_printstatuscode) values('"
						+ piid
						+ "','"
						+ inoutno
						+ "','"
						+ piclass
						+ "',sysdate,sysdate,'RMB',1,'未过帐','UNPOST','在录入','ENTERING','"
						+ SystemSession.getUser().getEm_name()
						+ "','"
						+ pitype
						+ "','"
						+ rs.getGeneralString("newfittingback_no").trim()
						+ "','"
						+ rs.getGeneralString("newfittingback_no").trim()
						+ "','"
						+ rs.getGeneralString("store_id").trim() + "', '未打印','UNPRINT')");
				baseDao.execute("update prodinout set (pi_cardcode,pi_whcode)=(select cu_erpcustcode,CU_LOCATION from customerfs where PI_REFNO=cu_code) where pi_id=" + piid);
				baseDao.execute("update prodinout set pi_whname=(select wh_description from warehouse where wh_code=pi_purpose) where pi_id="
						+ piid);
				baseDao.execute("update prodinout set (pi_cardid,pi_title)=(select cu_id,cu_name from customer where cu_code=pi_cardcode) where pi_id="
						+ piid);
				int count = baseDao.getCountByCondition("prodinout", "pi_id="
						+ piid);
				if (count > 0) {
					dao.execute("update newfitting_back set status='已处理',inoutno='"
							+ inoutno
							+ "',updateman='"
							+ SystemSession.getUser().getEm_name()
							+ "',updatedate=getdate(),class='"
							+ piclass
							+ "' where newfittingback_no='" + code + "' ");
					SqlRowList rs2 = dao
							.query("Select * from newfittingback_detail where newfittingback_no='"
									+ code + "' order by fitting_code");
					Double ftprice = 0.0;
					int index = 1;
					while (rs2.next()) {
						SqlRowList rs3 = dao
								.query("Select onelevel_price from fitting_table where fitting_code='"
										+ rs2.getObject("fitting_code") + "' ");
						if (rs3.next()) {
							ftprice = rs3.getDouble("onelevel_price");
						}
						baseDao.execute("insert into prodiodetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_prodcode, pd_outqty, pd_status, pd_price,pd_sendprice) values( PRODIODETAIL_SEQ.NEXTVAL,"
								+ piid
								+ ",'"
								+ inoutno
								+ "','"
								+ piclass
								+ "',"
								+ index
								+ ",'"
								+ rs2.getGeneralString("fitting_code").trim()
								+ "',"
								+ rs2.getGeneralDouble("queren_quantity")
								+ ",0,0,"
								+ ftprice + ")");
						index++;
					}
					baseDao.execute("update prodiodetail set (pd_whcode,pd_whname,pd_inwhcode,pd_inwhname)=(select pi_whcode,pi_whname,pi_purpose,pi_purposename from prodinout where pd_piid=pi_id) where pd_piid="
							+ piid);
					log = "转入成功,拨出单:"
							+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ piid + "&gridCondition=pd_piidIS" + piid
							+ "&whoami=ProdInOut!AppropriationOut')\">"
							+ inoutno + "</a>";
					okNum = okNum + 1;
					sb.append(okNum).append(": ").append(log).append("<hr>");
				}
			}
		}
		if (okNum > 0) {
			return sb.toString();
		}
		return null;
	}

	@Override
	public String newBackDelete(String data) {
		if (StringUtil.hasText(data)) {
			String codes = CollectionUtil.toSqlString(data.split(","));
			dao.execute("update newfitting_back set status='已处理', inoutno='不处理', updateman='"
					+ SystemSession.getUser().getEm_name()
					+ "', updatedate=getdate(), class='' where newfittingback_no in("
					+ codes + ")");
		}
		return "删除成功!";
	}

	final static String REG_D = "\\d{4}-\\d{2}-\\d{2}";

	public static String parseFilter(Map<String, Object> filters) {
		List<String> conditions = new ArrayList<String>();
		if (filters != null) {
			for (String key : filters.keySet()) {
				Object val = filters.get(key);
				if (val instanceof Number) {
					conditions.add(key + "=" + val);
				} else if (val instanceof String) {
					if (val.toString().matches(REG_D)) {
						conditions.add("CONVERT(varchar(100)," + key + ",23)='" + val + "'");
					} else {
						conditions.add("upper(" + key + ") like '%" + val.toString().toUpperCase() + "%'");
					}
				}
			}
		}
		return CollectionUtil.toString(conditions, " and ");
	}

	@Override
	public Page<Map<String, Object>> getNewBackList(int page, int start,
			int limit, Map<String, Object> filters) {
		String condition = parseFilter(filters);
		if (StringUtil.hasText(condition))
			condition = " and " + condition;
		final Integer total = dao
				.query("select count(*) from newfitting_back left join service_store on newfitting_back.store_id= service_store.store_id where back_day>'2011-7-11' and isnull(status,'')='已处理' and updatedate>='2011-8-1'"
						+ condition, Integer.class);
		final SqlRowList rs = dao
				.query("SELECT newfitting_back.newfittingback_no, CONVERT(varchar(100),newfitting_back.BACK_DAY,23) as BACK_DAY, newfitting_back.STORE_ID, service_store.STORE_NAME, newfitting_back.OBJECTIVE_STORE, CONVERT(varchar(100),newfitting_back.APPLY_DATETIME,23) as APPLY_DATETIME, newfitting_back.APPLY_RESULT, newfitting_back.APPLY_OP, newfitting_back.INOUTNO, newfitting_back.CLASS, newfitting_back.UPDATEMAN, CONVERT(varchar(100),newfitting_back.UPDATEDATE,23) as UPDATEDATE FROM newfitting_back left join service_store on newfitting_back.store_id= service_store.store_id,(select top "
						+ page
						* limit
						+ " row_number() OVER (order by updatedate desc,back_day desc,NEWFITTINGBACK_NO desc) n, newfittingback_no from newfitting_back left join service_store on newfitting_back.store_id= service_store.store_id where back_day>'2011-7-11' and isnull(status,'')='已处理' and updatedate>='2011-8-1'"
						+ condition
						+ ") w2 WHERE newfitting_back.newfittingback_no = w2.newfittingback_no AND w2.n > "
						+ start + " ORDER BY w2.n ASC");
		Page<Map<String, Object>> pageData = new Page<Map<String, Object>>() {

			@Override
			public int getTotalCount() {
				return total;
			}

			@Override
			public List<Map<String, Object>> getTarget() {
				return rs.getResultList();
			}
		};
		return pageData;
	}
}
