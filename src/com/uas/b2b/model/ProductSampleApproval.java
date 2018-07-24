package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;
import com.uas.erp.service.common.AttachUploadedAble;

/**
 * 买家ERP系统的供应商送样认定单
 * 
 * @author suntg
 * 
 */
public class ProductSampleApproval extends KeyEntity implements AttachUploadedAble {

	/**
	 * 附件上传选项,由于有多个附件属性,切换这个变量来切换需要上传的附件属性<br>
	 * "pa_attach" 上传pa_attach的附件<br>
	 * "pa_prdattach" 上传pa_prdattach的附件<br>
	 * "pa_padattach" 上传pa_padattach的附件<br>
	 * "pa_ppdattach" 上传pa_ppdattach的附件<br>
	 */
	public static String ATTACH_UPLOAD_ITEM = "";

	private Long pa_id;
	private String pa_code;
	private String pa_pscode;
	private String pa_sscode;
	private Long pa_venduu;
	private String pa_prodcode;
	private String pr_detail;
	private String pr_spec;
	private String pr_unit;
	private Double pa_sampleqty;
	private Double pa_height;
	private String pa_material;
	private String pa_materialquality;
	private String pa_address;
	private String pa_addressmark;
	private String pa_recordor;
	private Date pa_inDate;
	private String pa_remark;
	private String pa_attach;
	private List<Attach> attaches;
	private Date pa_prdtime;
	private Double pa_prdypsl;
	private String pa_prdresult;
	private String pa_prdadvice;
	private String pa_prdremark;
	private String pa_prdattach;
	private List<Attach> prdAttaches;
	private Date pa_padtime;
	private Double pa_padypsl;
	private String pa_padresult;
	private String pa_padadvice;
	private String pa_padremark;
	private String pa_padattach;
	private List<Attach> padAttaches;
	private Date pa_ppdtime;
	private Double pa_ppdypsl;
	private String pa_ppdresult;
	private String pa_ppdadvice;
	private String pa_ppdremark;
	private String pa_ppdattach;
	private List<Attach> ppdAttaches;
	private String pa_finalresult;
	private String pa_finalresultremark;
	private String pa_yxdj;// 优选等级

	public Long getPa_id() {
		return pa_id;
	}

	public void setPa_id(Long pa_id) {
		this.pa_id = pa_id;
	}

	public String getPa_code() {
		return pa_code;
	}

	public void setPa_code(String pa_code) {
		this.pa_code = pa_code;
	}

	public String getPa_pscode() {
		return pa_pscode;
	}

	public void setPa_pscode(String pa_pscode) {
		this.pa_pscode = pa_pscode;
	}

	public String getPa_sscode() {
		return pa_sscode;
	}

	public void setPa_sscode(String pa_sscode) {
		this.pa_sscode = pa_sscode;
	}

	public Long getPa_venduu() {
		return pa_venduu;
	}

	public void setPa_venduu(Long pa_venduu) {
		this.pa_venduu = pa_venduu;
	}

	public String getPa_prodcode() {
		return pa_prodcode;
	}

	public void setPa_prodcode(String pa_prodcode) {
		this.pa_prodcode = pa_prodcode;
	}

	public String getPr_detail() {
		return pr_detail;
	}

	public void setPr_detail(String pr_detail) {
		this.pr_detail = pr_detail;
	}

	public String getPr_spec() {
		return pr_spec;
	}

	public void setPr_spec(String pr_spec) {
		this.pr_spec = pr_spec;
	}

	public String getPr_unit() {
		return pr_unit;
	}

	public void setPr_unit(String pr_unit) {
		this.pr_unit = pr_unit;
	}

	public Double getPa_sampleqty() {
		return pa_sampleqty;
	}

	public void setPa_sampleqty(Double pa_sampleqty) {
		this.pa_sampleqty = pa_sampleqty;
	}

	public Double getPa_height() {
		return pa_height;
	}

	public void setPa_height(Double pa_height) {
		this.pa_height = pa_height;
	}

	public String getPa_material() {
		return pa_material;
	}

	public void setPa_material(String pa_material) {
		this.pa_material = pa_material;
	}

	public String getPa_materialquality() {
		return pa_materialquality;
	}

	public void setPa_materialquality(String pa_materialquality) {
		this.pa_materialquality = pa_materialquality;
	}

	public String getPa_address() {
		return pa_address;
	}

	public void setPa_address(String pa_address) {
		this.pa_address = pa_address;
	}

	public String getPa_addressmark() {
		return pa_addressmark;
	}

	public void setPa_addressmark(String pa_addressmark) {
		this.pa_addressmark = pa_addressmark;
	}

	public String getPa_recordor() {
		return pa_recordor;
	}

	public void setPa_recordor(String pa_recordor) {
		this.pa_recordor = pa_recordor;
	}

	public Date getPa_inDate() {
		return pa_inDate;
	}

	public void setPa_inDate(Date pa_inDate) {
		this.pa_inDate = pa_inDate;
	}

	public String getPa_remark() {
		return pa_remark;
	}

	public void setPa_remark(String pa_remark) {
		this.pa_remark = pa_remark;
	}

	public String getPa_attach() {
		return pa_attach;
	}

	public void setPa_attach(String pa_attach) {
		this.pa_attach = pa_attach;
	}

	public Date getPa_prdtime() {
		return pa_prdtime;
	}

	public void setPa_prdtime(Date pa_prdtime) {
		this.pa_prdtime = pa_prdtime;
	}

	public Double getPa_prdypsl() {
		return pa_prdypsl;
	}

	public void setPa_prdypsl(Double pa_prdypsl) {
		this.pa_prdypsl = pa_prdypsl;
	}

	public String getPa_prdresult() {
		return pa_prdresult;
	}

	public void setPa_prdresult(String pa_prdresult) {
		this.pa_prdresult = pa_prdresult;
	}

	public String getPa_prdadvice() {
		return pa_prdadvice;
	}

	public void setPa_prdadvice(String pa_prdadvice) {
		this.pa_prdadvice = pa_prdadvice;
	}

	public String getPa_prdremark() {
		return pa_prdremark;
	}

	public void setPa_prdremark(String pa_prdremark) {
		this.pa_prdremark = pa_prdremark;
	}

	public String getPa_prdattach() {
		return pa_prdattach;
	}

	public void setPa_prdattach(String pa_prdattach) {
		this.pa_prdattach = pa_prdattach;
	}

	public Date getPa_padtime() {
		return pa_padtime;
	}

	public void setPa_padtime(Date pa_padtime) {
		this.pa_padtime = pa_padtime;
	}

	public Double getPa_padypsl() {
		return pa_padypsl;
	}

	public void setPa_padypsl(Double pa_padypsl) {
		this.pa_padypsl = pa_padypsl;
	}

	public String getPa_padresult() {
		return pa_padresult;
	}

	public void setPa_padresult(String pa_padresult) {
		this.pa_padresult = pa_padresult;
	}

	public String getPa_padadvice() {
		return pa_padadvice;
	}

	public void setPa_padadvice(String pa_padadvice) {
		this.pa_padadvice = pa_padadvice;
	}

	public String getPa_padremark() {
		return pa_padremark;
	}

	public void setPa_padremark(String pa_padremark) {
		this.pa_padremark = pa_padremark;
	}

	public String getPa_padattach() {
		return pa_padattach;
	}

	public void setPa_padattach(String pa_padattach) {
		this.pa_padattach = pa_padattach;
	}

	public Date getPa_ppdtime() {
		return pa_ppdtime;
	}

	public void setPa_ppdtime(Date pa_ppdtime) {
		this.pa_ppdtime = pa_ppdtime;
	}

	public Double getPa_ppdypsl() {
		return pa_ppdypsl;
	}

	public void setPa_ppdypsl(Double pa_ppdypsl) {
		this.pa_ppdypsl = pa_ppdypsl;
	}

	public String getPa_ppdresult() {
		return pa_ppdresult;
	}

	public void setPa_ppdresult(String pa_ppdresult) {
		this.pa_ppdresult = pa_ppdresult;
	}

	public String getPa_ppdadvice() {
		return pa_ppdadvice;
	}

	public void setPa_ppdadvice(String pa_ppdadvice) {
		this.pa_ppdadvice = pa_ppdadvice;
	}

	public String getPa_ppdremark() {
		return pa_ppdremark;
	}

	public void setPa_ppdremark(String pa_ppdremark) {
		this.pa_ppdremark = pa_ppdremark;
	}

	public String getPa_ppdattach() {
		return pa_ppdattach;
	}

	public void setPa_ppdattach(String pa_ppdattach) {
		this.pa_ppdattach = pa_ppdattach;
	}

	public String getPa_finalresult() {
		return pa_finalresult;
	}

	public void setPa_finalresult(String pa_finalresult) {
		this.pa_finalresult = pa_finalresult;
	}

	public String getPa_finalresultremark() {
		return pa_finalresultremark;
	}

	public void setPa_finalresultremark(String pa_finalresultremark) {
		this.pa_finalresultremark = pa_finalresultremark;
	}

	public String getPa_yxdj() {
		return pa_yxdj;
	}

	public void setPa_yxdj(String pa_yxdj) {
		this.pa_yxdj = pa_yxdj;
	}

	@Override
	public String getAttachs() {
		if (ProductSampleApproval.ATTACH_UPLOAD_ITEM.equals("pa_attach"))
			return this.pa_attach;
		else if (ProductSampleApproval.ATTACH_UPLOAD_ITEM.equals("pa_padattach"))
			return this.pa_padattach;
		else if (ProductSampleApproval.ATTACH_UPLOAD_ITEM.equals("pa_prdattach"))
			return this.pa_prdattach;
		else if (ProductSampleApproval.ATTACH_UPLOAD_ITEM.equals("pa_ppdattach"))
			return this.pa_ppdattach;
		return null;
	}

	@Override
	public Object getReffrencValue() {
		return this.pa_code;
	}

	@Override
	public Object getKey() {
		return this.pa_id;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

	public List<Attach> getPrdAttaches() {
		return prdAttaches;
	}

	public void setPrdAttaches(List<Attach> prdAttaches) {
		this.prdAttaches = prdAttaches;
	}

	public List<Attach> getPadAttaches() {
		return padAttaches;
	}

	public void setPadAttaches(List<Attach> padAttaches) {
		this.padAttaches = padAttaches;
	}

	public List<Attach> getPpdAttaches() {
		return ppdAttaches;
	}

	public void setPpdAttaches(List<Attach> ppdAttaches) {
		this.ppdAttaches = ppdAttaches;
	}

}
