package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VendorChangeDao;

@Repository
public class VendorChangeDaoImpl extends BaseDao implements VendorChangeDao {

	static final String VendorChangeDETAIL = "SELECT VendorChangeDetail.*,pa_id,pa_name FROM VendorChangeDetail left join Payments on "
			+ " pa_code=vcd_newpaymentcode WHERE vcd_vcid=?";
	static final String UPDATEVENDOR = "update VENDOR set ve_degree=?,ve_tel=?,ve_fax=?,ve_email=?,ve_mobile=?,ve_bank=?,ve_bankaccount=?,"
			+ "ve_paymentcode=?,ve_payment=?,ve_taxrate=?,ve_contact=?,ve_paymentid=?,ve_apvendcode=?,ve_apvendname=?,ve_currency=? where ve_code=?";

	@Override
	@Transactional
	public List<String> turnVendor(int id) {
		SqlRowList rs = queryForRowSet(VendorChangeDETAIL, id);
		String vecode = null;
		List<String> codes = new ArrayList<String>();
		while (rs.next()) {
			vecode = rs.getString("vcd_vendcode");
			boolean bool = execute(
					UPDATEVENDOR,
					new Object[] { rs.getObject("vcd_newdegree"), rs.getObject("vcd_newtel"), rs.getObject("vcd_newfax"),
							rs.getObject("vcd_newemail"), rs.getObject("vcd_newmobile"), rs.getObject("vcd_newbank"),
							rs.getObject("vcd_newbankaccount"), rs.getObject("vcd_newpaymentcode"), rs.getObject("vcd_newpayment"),
							rs.getObject("vcd_newtaxrate"), rs.getObject("vcd_newcontact"), rs.getObject("pa_id"),
							rs.getObject("vcd_newapvendcode"), rs.getObject("vcd_newapvendname"), rs.getObject("vcd_newcurrency"), vecode });
			execute("update vendor set ve_system1=(select vcd_newsystem1 from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_newsystem1,' ' )<>' ' and vcd_newsystem1<>vcd_system1)");
			execute("update vendor set ve_system2=(select vcd_newsystem2 from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_newsystem2,' ' )<>' ' and vcd_newsystem2<>vcd_system2)");
			execute("update vendor set ve_system3=(select vcd_newsystem3 from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_newsystem3,' ' )<>' ' and vcd_newsystem3<>vcd_system3)");
			execute("update vendor set ve_bankman=(select vcd_newbankman from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_newbankman,' ' )<>' ' and vcd_newbankman<>ve_bankman)");
			execute("update vendor set (ve_buyercode,ve_buyername,ve_buyerid)=(select vcd_newbuyercode,vcd_newbuyername,em_id from VendorChangeDetail left join employee on em_code=vcd_newbuyercode where ve_code=vcd_vendcode and nvl(vcd_newbuyercode,' ')<>nvl(ve_buyercode,' ')) where ve_code='"+vecode+"' and  ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and vcd_newbuyercode<>' ' and vcd_newbuyercode<>ve_buyercode)");
			execute("update vendor set ve_add1=(select vcd_add1 from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_add1,' ' )<>' ' and vcd_add1<>ve_add1)");
			int argCount = getCountByCondition("user_tab_columns",
					"table_name='VENDORCHANGEDETAIL' and column_name in ('VE_BANKADDRESS','VCD_NEWBANKADDRESS')");
			if (argCount == 2) {
				execute("update vendor set ve_bankaddress=(select vcd_newbankaddress from VendorChangeDetail where vcd_id="+rs.getInt("vcd_id")+" ) where ve_code='"+vecode+"' and ve_code in (select vcd_vendcode from  VendorChangeDetail where vcd_id='" +rs.getInt("vcd_id")+ "' and nvl(vcd_newbankaddress,' ' )<>' ' and vcd_newbankaddress<>ve_bankaddress)");
			}
			argCount = getCountByCondition("user_tab_columns",
					"table_name='VENDORCHANGEDETAIL' and column_name in ('VCD_NEWVENDNAME','VCD_NEWSHORTNAME')");
			if (argCount == 2) {
				if (rs.getObject("vcd_newvendname")!=null && !rs.getGeneralString("vcd_newvendname").equals(rs.getGeneralString("ve_name"))) {
					execute("update vendor set ve_name='" + rs.getGeneralString("vcd_newvendname") + "' where ve_code='" + vecode + "'");
					execute("update vendor set ve_apvendname='" + rs.getGeneralString("vcd_newvendname") + "' where ve_apvendcode='" + vecode + "'");
				}
				if (rs.getObject("vcd_newshortname")!=null && !rs.getGeneralString("vcd_newshortname").equals(rs.getGeneralString("ve_shortname"))) {
					execute("update vendor set ve_shortname='" + rs.getGeneralString("vcd_newshortname") + "' where ve_code='" + vecode + "'");
				}
			}
			if (bool) {
				codes.add(vecode);
			}
		}
		return codes;
	}
}
