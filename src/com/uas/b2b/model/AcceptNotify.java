package com.uas.b2b.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Status;

/**
 * 买家ERP的收料通知单
 */
public class AcceptNotify {

	/**
	 * 平台上的发货单ID
	 */
	private Long b2b_ss_id;
	private Integer an_id;
	private long an_venduu;
	private String an_currency;
	private Float an_rate;
	private String an_payment;
	private Date an_date;
	private Long an_buyeruu;
	private String an_sendcode;
	private String an_recorder;
	private List<AcceptNotifyDetail> details;

	public Long getB2b_ss_id() {
		return b2b_ss_id;
	}

	public void setB2b_ss_id(Long b2b_ss_id) {
		this.b2b_ss_id = b2b_ss_id;
	}

	@JsonIgnore
	public Integer getAn_id() {
		return an_id;
	}

	public void setAn_id(Integer an_id) {
		this.an_id = an_id;
	}

	public long getAn_venduu() {
		return an_venduu;
	}

	public void setAn_venduu(long an_venduu) {
		this.an_venduu = an_venduu;
	}

	public String getAn_currency() {
		return an_currency;
	}

	public void setAn_currency(String an_currency) {
		this.an_currency = an_currency;
	}

	public Float getAn_rate() {
		return an_rate;
	}

	public void setAn_rate(Float an_rate) {
		this.an_rate = an_rate;
	}

	public String getAn_payment() {
		return an_payment;
	}

	public void setAn_payment(String an_payment) {
		this.an_payment = an_payment;
	}
	
	

	public Date getAn_date() {
		return an_date;
	}

	public void setAn_date(Date an_date) {
		this.an_date = an_date;
	}

	public Long getAn_buyeruu() {
		return an_buyeruu;
	}

	public void setAn_buyeruu(Long an_buyeruu) {
		this.an_buyeruu = an_buyeruu;
	}

	public String getAn_sendcode() {
		return an_sendcode;
	}

	public void setAn_sendcode(String an_sendcode) {
		this.an_sendcode = an_sendcode;
	}

	public String getAn_recorder() {
		return an_recorder;
	}

	public void setAn_recorder(String an_recorder) {
		this.an_recorder = an_recorder;
	}

	public List<AcceptNotifyDetail> getDetails() {
		return details;
	}

	public void setDetails(List<AcceptNotifyDetail> details) {
		this.details = details;
	}

	/**
	 * @param primaryKey
	 *            主键
	 * @param code
	 *            流水号
	 * @return
	 */
	public List<String> toCascadedSqlString(int primaryKey, String code) {
		List<String> sqls = new ArrayList<String>();
		sqls.add(toSqlString(primaryKey, code));
		if (!CollectionUtils.isEmpty(details)) {
			for (AcceptNotifyDetail detail : details)
				sqls.add(detail.toSqlString(primaryKey));
		}
		return sqls;
	}

	private String toSqlString(int primaryKey, String code) {
		return "insert into AcceptNotify(b2b_ss_id,an_id,an_code,an_venduu,an_buyeruu,an_sendcode,an_status,an_statuscode,an_date,an_indate,an_sendstatus,an_recorder) values ("
				+ b2b_ss_id
				+ ","
				+ primaryKey
				+ ",'"
				+ code
				+ "',"
				+ an_venduu
				+ ","
				+ an_buyeruu
				+ ",'"
				+ an_sendcode
				+ "','"
				+ Status.AUDITED.display()
				+ "','"
				+ Status.AUDITED.code()
				+ "',"
				+ DateUtil.parseDateToOracleString(null, an_date)
				+ ",sysdate,'已下载','"
				+ StringUtil.nvl(an_recorder, "")
				+ "')";
	}

}
