package com.uas.erp.model;

import java.io.Serializable;

import com.lowagie.text.pdf.PdfPublicKeyRecipient;

public class ProductKind implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pk_id;
	private String pk_code;
	private String pk_name;
	private int pk_subof;
	private String pk_leaf;
	private String pk_engname;
	private String pk_namerule;
	private String pk_nameeg;
	private String pk_specrule;
	private String pk_speceg;
	private String pk_parameterrule;
	private String pk_parametereg;
	private Integer pk_ifzeroqty;
	private String pk_dhzc;
	private String pk_cop;
	private String pk_acceptmethod;
	private String pk_wccode;
	private String pk_wcname;
	private String pk_whname;
	private String pk_stockcatecode;
	private String pk_stockcate;
	private String pk_incomecatecode;
	private String pk_incomecate;
	private String pk_costcatecode;
	private String pk_costcate;
	private String pk_manutype;
	private String pk_supplytype;
	private String pk_whcode;
	private String pk_material;
	private String pk_priority;
	private Float pk_lossrate;// 量产
	private Float pk_testlossrate;// 试产
	private Float pk_exportlossrate;// 委外
	private Double pk_purclossrate;
	private Integer pk_ltwarndays;
	private Integer pk_purchasedays;
	private Integer pk_leadtime;
	private Integer pk_ltinstock;
	private Integer pk_purcmergedays;
	private Integer pk_validdays;
	private String pk_prname;
	private String pk_qualmethod;
	private String pk_serial;
	private String pk_location;
	private String pk_effective;
	private Integer pk_isstandardpr;// 是否标准化
	private Integer pk_isgrouppurc;// 是否集团采购
	private Integer pk_period;// 生产周期
	private Integer pr_plzl;// 提前期批量
	private String pk_aql;
	private String pk_whmancode;
	private String pk_whmanname;
	private String pk_buyercode;//采购员编号
	private String pk_buyername;//采购员姓名
	private String pk_abc;//ABC分类
	private Integer pk_ltqc;//复检周期
	private Integer pk_precision; //计算精度
	private String pk_cggdycode;//采购跟单员编号
	private String pk_cggdy;//采购跟单员名称
	private String pk_sqecode;//SQE工程师编号
	private String pk_sqename;//SQE工程师名称
	private String pk_msdlevel; //湿敏等级
	private String pk_self;//是否标准件
	private String pk_engremark;//
	private String pk_inspector;
	private String pk_inspectorcode;
	private String pk_jhy;//检验员
	private String pk_jhyname;
	private String pk_tracekind;//管控类型
	private String pk_firsttype;//量产
	private String pk_secondtype;//试产
	public String getPk_cggdycode() {
		return pk_cggdycode;
	}

	public void setPk_cggdycode(String pk_cggdycode) {
		this.pk_cggdycode = pk_cggdycode;
	}

	public String getPk_cggdy() {
		return pk_cggdy;
	}

	public void setPk_cggdy(String pk_cggdy) {
		this.pk_cggdy = pk_cggdy;
	}

	public String getPk_sqecode() {
		return pk_sqecode;
	}

	public void setPk_sqecode(String pk_sqecode) {
		this.pk_sqecode = pk_sqecode;
	}

	public String getPk_sqename() {
		return pk_sqename;
	}

	public void setPk_sqename(String pk_sqename) {
		this.pk_sqename = pk_sqename;
	}

	public String getPk_abc() {
		return pk_abc;
	}

	public void setPk_abc(String pk_abc) {
		this.pk_abc = pk_abc;
	}

	public int getPk_id() {
		return pk_id;
	}

	public void setPk_id(int pk_id) {
		this.pk_id = pk_id;
	}

	public String getPk_code() {
		return pk_code;
	}

	public void setPk_code(String pk_code) {
		this.pk_code = pk_code;
	}

	public String getPk_name() {
		return pk_name;
	}

	public void setPk_name(String pk_name) {
		this.pk_name = pk_name;
	}

	public int getPk_subof() {
		return pk_subof;
	}

	public String getPk_namerule() {
		return pk_namerule;
	}

	public void setPk_namerule(String pk_namerule) {
		this.pk_namerule = pk_namerule;
	}

	public String getPk_nameeg() {
		return pk_nameeg;
	}

	public void setPk_nameeg(String pk_nameeg) {
		this.pk_nameeg = pk_nameeg;
	}

	public String getPk_specrule() {
		return pk_specrule;
	}

	public void setPk_specrule(String pk_specrule) {
		this.pk_specrule = pk_specrule;
	}

	public String getPk_speceg() {
		return pk_speceg;
	}

	public void setPk_speceg(String pk_speceg) {
		this.pk_speceg = pk_speceg;
	}

	public String getPk_parameterrule() {
		return pk_parameterrule;
	}

	public void setPk_parameterrule(String pk_parameterrule) {
		this.pk_parameterrule = pk_parameterrule;
	}

	public String getPk_parametereg() {
		return pk_parametereg;
	}

	public void setPk_parametereg(String pk_parametereg) {
		this.pk_parametereg = pk_parametereg;
	}

	public void setPk_subof(int pk_subof) {
		this.pk_subof = pk_subof;
	}

	public String getPk_leaf() {
		return pk_leaf;
	}

	public void setPk_leaf(String pk_leaf) {
		this.pk_leaf = pk_leaf;
	}

	public String getPk_engname() {
		return pk_engname;
	}

	public void setPk_engname(String pk_engname) {
		this.pk_engname = pk_engname;
	}

	public Integer getPk_ifzeroqty() {
		return pk_ifzeroqty;
	}

	public void setPk_ifzeroqty(Integer pk_ifzeroqty) {
		this.pk_ifzeroqty = pk_ifzeroqty;
	}

	public String getPk_dhzc() {
		return pk_dhzc;
	}

	public void setPk_dhzc(String pk_dhzc) {
		this.pk_dhzc = pk_dhzc;
	}

	public String getPk_cop() {
		return pk_cop;
	}

	public void setPk_cop(String pk_cop) {
		this.pk_cop = pk_cop;
	}

	public String getPk_acceptmethod() {
		return pk_acceptmethod;
	}

	public void setPk_acceptmethod(String pk_acceptmethod) {
		this.pk_acceptmethod = pk_acceptmethod;
	}

	public String getPk_wccode() {
		return pk_wccode;
	}

	public void setPk_wccode(String pk_wccode) {
		this.pk_wccode = pk_wccode;
	}

	public String getPk_wcname() {
		return pk_wcname;
	}

	public void setPk_wcname(String pk_wcname) {
		this.pk_wcname = pk_wcname;
	}

	public String getPk_whname() {
		return pk_whname;
	}

	public void setPk_whname(String pk_whname) {
		this.pk_whname = pk_whname;
	}

	public String getPk_stockcatecode() {
		return pk_stockcatecode;
	}

	public void setPk_stockcatecode(String pk_stockcatecode) {
		this.pk_stockcatecode = pk_stockcatecode;
	}

	public String getPk_stockcate() {
		return pk_stockcate;
	}

	public void setPk_stockcate(String pk_stockcate) {
		this.pk_stockcate = pk_stockcate;
	}

	public String getPk_incomecatecode() {
		return pk_incomecatecode;
	}

	public void setPk_incomecatecode(String pk_incomecatecode) {
		this.pk_incomecatecode = pk_incomecatecode;
	}

	public String getPk_incomecate() {
		return pk_incomecate;
	}

	public void setPk_incomecate(String pk_incomecate) {
		this.pk_incomecate = pk_incomecate;
	}

	public String getPk_costcatecode() {
		return pk_costcatecode;
	}

	public void setPk_costcatecode(String pk_costcatecode) {
		this.pk_costcatecode = pk_costcatecode;
	}

	public String getPk_costcate() {
		return pk_costcate;
	}

	public void setPk_costcate(String pk_costcate) {
		this.pk_costcate = pk_costcate;
	}

	public String getPk_manutype() {
		return pk_manutype;
	}

	public void setPk_manutype(String pk_manutype) {
		this.pk_manutype = pk_manutype;
	}

	public String getPk_supplytype() {
		return pk_supplytype;
	}

	public void setPk_supplytype(String pk_supplytype) {
		this.pk_supplytype = pk_supplytype;
	}

	public String getPk_whcode() {
		return pk_whcode;
	}

	public void setPk_whcode(String pk_whcode) {
		this.pk_whcode = pk_whcode;
	}

	public String getPk_material() {
		return pk_material;
	}

	public void setPk_material(String pk_material) {
		this.pk_material = pk_material;
	}

	public String getPk_priority() {
		return pk_priority;
	}

	public void setPk_priority(String pk_priority) {
		this.pk_priority = pk_priority;
	}

	public Float getPk_lossrate() {
		return pk_lossrate;
	}

	public void setPk_lossrate(Float pk_lossrate) {
		this.pk_lossrate = pk_lossrate;
	}

	public Float getPk_testlossrate() {
		return pk_testlossrate;
	}

	public void setPk_testlossrate(Float pk_testlossrate) {
		this.pk_testlossrate = pk_testlossrate;
	}

	public Float getPk_exportlossrate() {
		return pk_exportlossrate;
	}

	public void setPk_exportlossrate(Float pk_exportlossrate) {
		this.pk_exportlossrate = pk_exportlossrate;
	}

	public Integer getPk_leadtime() {
		return pk_leadtime;
	}

	public void setPk_leadtime(Integer pk_leadtime) {
		this.pk_leadtime = pk_leadtime;
	}

	public Double getPk_purclossrate() {
		return pk_purclossrate;
	}

	public void setPk_purclossrate(Double pk_purclossrate) {
		this.pk_purclossrate = pk_purclossrate;
	}

	public Integer getPk_ltwarndays() {
		return pk_ltwarndays;
	}

	public void setPk_ltwarndays(Integer pk_ltwarndays) {
		this.pk_ltwarndays = pk_ltwarndays;
	}

	public Integer getPk_purchasedays() {
		return pk_purchasedays;
	}

	public void setPk_purchasedays(Integer pk_purchasedays) {
		this.pk_purchasedays = pk_purchasedays;
	}

	public Integer getPk_ltinstock() {
		return pk_ltinstock;
	}

	public void setPk_ltinstock(Integer pk_ltinstock) {
		this.pk_ltinstock = pk_ltinstock;
	}

	public String getPk_prname() {
		return pk_prname;
	}

	public void setPk_prname(String pk_prname) {
		this.pk_prname = pk_prname;
	}

	public String getPk_qualmethod() {
		return pk_qualmethod;
	}

	public void setPk_qualmethod(String pk_qualmethod) {
		this.pk_qualmethod = pk_qualmethod;
	}

	public Integer getPk_purcmergedays() {
		return pk_purcmergedays;
	}

	public void setPk_purcmergedays(Integer pk_purcmergedays) {
		this.pk_purcmergedays = pk_purcmergedays;
	}

	public String getPk_serial() {
		return pk_serial;
	}

	public void setPk_serial(String pk_serial) {
		this.pk_serial = pk_serial;
	}

	public String getPk_location() {
		return pk_location;
	}

	public void setPk_location(String pk_location) {
		this.pk_location = pk_location;
	}

	public Integer getPk_validdays() {
		return pk_validdays;
	}

	public void setPk_validdays(Integer pk_validdays) {
		this.pk_validdays = pk_validdays;
	}

	public String getPk_effective() {
		return pk_effective;
	}

	public void setPk_effective(String pk_effective) {
		this.pk_effective = pk_effective;
	}

	public Integer getPk_isstandardpr() {
		return pk_isstandardpr;
	}

	public void setPk_isstandardpr(Integer pk_isstandardpr) {
		this.pk_isstandardpr = pk_isstandardpr;
	}

	public Integer getPk_period() {
		return pk_period;
	}

	public void setPk_period(Integer pk_period) {
		this.pk_period = pk_period;
	}

	public Integer getPr_plzl() {
		return pr_plzl;
	}

	public void setPr_plzl(Integer pr_plzl) {
		this.pr_plzl = pr_plzl;
	}

	public String getPk_aql() {
		return pk_aql;
	}

	public void setPk_aql(String pk_aql) {
		this.pk_aql = pk_aql;
	}

	public Integer getPk_isgrouppurc() {
		return pk_isgrouppurc;
	}

	public void setPk_isgrouppurc(Integer pk_isgrouppurc) {
		this.pk_isgrouppurc = pk_isgrouppurc;
	}

	@Override
	public int hashCode() {
		return this.pk_id;
	}

	@Override
	public boolean equals(Object paramObject) {
		if (paramObject == null)
			return false;
		if (this == paramObject)
			return true;
		if (paramObject instanceof ProductKind) {
			ProductKind pk = (ProductKind) paramObject;
			if (pk.getPk_id() == this.pk_id)
				return true;
		}
		return false;
	}

	public String getPk_whmancode() {
		return pk_whmancode;
	}

	public void setPk_whmancode(String pk_whmancode) {
		this.pk_whmancode = pk_whmancode;
	}

	public String getPk_whmanname() {
		return pk_whmanname;
	}

	public void setPk_whmanname(String pk_whmanname) {
		this.pk_whmanname = pk_whmanname;
	}

	public String getPk_buyercode() {
		return pk_buyercode;
	}

	public void setPk_buyercode(String pk_buyercode) {
		this.pk_buyercode = pk_buyercode;
	}

	public String getPk_buyername() {
		return pk_buyername;
	}

	public void setPk_buyername(String pk_buyername) {
		this.pk_buyername = pk_buyername;
	}

	public Integer getPk_ltqc() {
		return pk_ltqc;
	}

	public void setPk_ltqc(Integer pk_ltqc) {
		this.pk_ltqc = pk_ltqc;
	}

	public Integer getPk_precision(){
		return pk_precision;
	}
	
	public void setPk_precision(Integer pk_precision){
		this.pk_precision = pk_precision;
	}
	
	public String getPk_msdlevel() {
		return pk_msdlevel;
	}

	public void setPk_msdlevel(String pk_msdlevel) {
		this.pk_msdlevel = pk_msdlevel;
	}
	
	public String getPk_self() {
		return pk_self;
	}

	public void setPk_self(String pk_self) {
		this.pk_self = pk_self;
	}
	
	public String getPk_engremark() {
		return pk_engremark;
	}

	public void setPk_engremark(String pk_engremark) {
		this.pk_engremark = pk_engremark;
	}
	
	public String getPk_inspector() {
		return pk_inspector;
	}

	public void setPk_inspector(String pk_inspector) {
		this.pk_inspector = pk_inspector;
	}
	
	public String getPk_inspectorcode() {
		return pk_inspectorcode;
	}

	public void setPk_inspectorcode(String pk_inspectorcode) {
		this.pk_inspectorcode = pk_inspectorcode;
	}
	public String getPk_jhy() {
		return pk_jhy;
	}

	public void setPk_jhy(String pk_jhy) {
		this.pk_jhy = pk_jhy;
	}
	public String getPk_jhyname() {
		return pk_jhyname;
	}

	public void setPk_jhyname(String pk_jhyname) {
		this.pk_jhyname = pk_jhyname;
	}
	public String getPk_tracekind() {
		return pk_tracekind;
	}

	public void setPk_firsttype(String pk_firsttype) {
		this.pk_firsttype = pk_firsttype;
	}
	public String getPk_firsttype() {
		return pk_firsttype;
	}

	public void setPk_secondtype(String pk_secondtype) {
		this.pk_secondtype = pk_secondtype;
	}
	public String getPk_secondtype() {
		return pk_secondtype;
	}

	public void setPk_tracekind(String pk_tracekind) {
		this.pk_tracekind = pk_tracekind;
	}
}
