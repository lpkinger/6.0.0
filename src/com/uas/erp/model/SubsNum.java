package com.uas.erp.model;

import java.util.Date;
import java.util.List;

public class SubsNum {

	private String title_;
	private Date date_;
	private String kind_;
	private String freq_;
	private Integer sharecounts_;
	private Integer enable_;
	private Integer subscounts_;
	private String remark_;
	private String statuscode_;
	private String status_;
	private String type_;
	private Integer id_;
	private Integer isapplied_;
	private String img_;
	private List<SubsNumDet> dets;

	public String getTitle_() {
		return title_;
	}

	public void setTitle_(String title_) {
		this.title_ = title_;
	}

	public Date getDate_() {
		return date_;
	}

	public void setDate_(Date date_) {
		this.date_ = date_;
	}

	public String getKind_() {
		return kind_;
	}

	public void setKind_(String kind_) {
		this.kind_ = kind_;
	}

	public String getFreq_() {
		return freq_;
	}

	public void setFreq_(String freq_) {
		this.freq_ = freq_;
	}

	public Integer getSharecounts_() {
		return sharecounts_;
	}

	public void setSharecounts_(Integer sharecounts_) {
		this.sharecounts_ = sharecounts_;
	}

	public Integer getEnable_() {
		return enable_;
	}

	public void setEnable_(Integer enable_) {
		this.enable_ = enable_;
	}

	public Integer getSubscounts_() {
		return subscounts_;
	}

	public void setSubscounts_(Integer subscounts_) {
		this.subscounts_ = subscounts_;
	}

	public String getRemark_() {
		return remark_;
	}

	public void setRemark_(String remark_) {
		this.remark_ = remark_;
	}

	public String getStatuscode_() {
		return statuscode_;
	}

	public void setStatuscode_(String statuscode_) {
		this.statuscode_ = statuscode_;
	}

	public String getStatus_() {
		return status_;
	}

	public void setStatus_(String status_) {
		this.status_ = status_;
	}

	public String getType_() {
		return type_;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}

	public Integer getId_() {
		return id_;
	}

	public void setId_(Integer id_) {
		this.id_ = id_;
	}

	public Integer getIsapplied_() {
		return isapplied_;
	}

	public void setIsapplied_(Integer isapplied_) {
		this.isapplied_ = isapplied_;
	}

	public String getImg_() {
		return img_;
	}

	public void setImg_(String img_) {
		this.img_ = img_;
	}

	public List<SubsNumDet> getDets() {
		return dets;
	}

	public void setDets(List<SubsNumDet> dets) {
		this.dets = dets;
	}

	public static class SubsNumDet {
		private Integer num_id;
		private int detno_;
		private String formula_code_;
		private String formula_args_;
		private Integer det_id;
		private SubsFormula formula;

		public Integer getNum_id() {
			return num_id;
		}

		public void setNum_id(Integer num_id) {
			this.num_id = num_id;
		}

		public int getDetno_() {
			return detno_;
		}

		public void setDetno_(int detno_) {
			this.detno_ = detno_;
		}

		public String getFormula_code_() {
			return formula_code_;
		}

		public void setFormula_code_(String formula_code_) {
			this.formula_code_ = formula_code_;
		}

		public String getFormula_args_() {
			return formula_args_;
		}

		public void setFormula_args_(String formula_args_) {
			this.formula_args_ = formula_args_;
		}

		public Integer getDet_id() {
			return det_id;
		}

		public void setDet_id(Integer det_id) {
			this.det_id = det_id;
		}

		public SubsFormula getFormula() {
			return formula;
		}

		public void setFormula(SubsFormula formula) {
			this.formula = formula;
		}
	}

}
