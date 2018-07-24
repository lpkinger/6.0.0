package com.uas.b2b.model;

import java.io.Serializable;
import java.util.Date;

/**
 *  用于招标中的企业基本信息
 * Created by dongbw on 17/03/07 11:50.
 */

public class EnterpriseBaseInfo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 企业uu
     */
    private Long uu;

    /**
     * 公司名称
     */
    private String enName;

    /**
     * 注册地址
     */
    private String enAddress;

    /**
     * 企业执照号
     */
    private String enBusinessCode;

    /**
     * 成立时间
     */
    private Date enEstablishDate;

    /**
     * 员工人数
     */
    private Long emNum;

    /**
     * 公司电话
     */
    private String enTel;
    /**
     * 传真
     */
    private String enFax;

    /**
     * 联系人（暂时设置为企业系统管理员）
     */
    private String enUser;

    /**
     * 联系人电话
     */
    private String userTel;

    /**
     * 开户行
     */
    private String bank;

    /**
     * 经营范围
     */
    private String scope;

    /**
     * 备注
     */
    private String remark;

    /**
     * 管理员
     * */
    private String adminName;
    /**
     * 法人
     * */
    private String enCorporation;
    
    /**
     * 邀请企业名
     * */
    private String inviteEnName;
    
    /**
     * 邀请用户名
     * */
    private String inviteUserName;
    
    /**
     * 注册时间
     * */
    private Date date;
    
    public EnterpriseBaseInfo() {
    }
    public EnterpriseBaseInfo(Long uu, String enBusinessCode, String enName, String enAddress) {
        this.uu = uu;
        this.enBusinessCode = enBusinessCode;
        this.enName = enName;
        this.enAddress = enAddress;
    }
    public EnterpriseBaseInfo(Long uu, String enName, String enAddress, String enBusinessCode, Date enEstablishDate,
			Long emNum, String enTel, String enFax, String enUser, String userTel, String bank, String scope,
			String remark, String adminName, String enCorporation, String inviteEnName, String inviteUserName,
			Date date) {
		super();
		this.uu = uu;
		this.enName = enName;
		this.enAddress = enAddress;
		this.enBusinessCode = enBusinessCode;
		this.enEstablishDate = enEstablishDate;
		this.emNum = emNum;
		this.enTel = enTel;
		this.enFax = enFax;
		this.enUser = enUser;
		this.userTel = userTel;
		this.bank = bank;
		this.scope = scope;
		this.remark = remark;
		this.adminName = adminName;
		this.enCorporation = enCorporation;
		this.inviteEnName = inviteEnName;
		this.inviteUserName = inviteUserName;
		this.date = date;
	}

	

    public Long getUu() {
        return uu;
    }

    public void setUu(Long uu) {
        this.uu = uu;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnAddress() {
        return enAddress;
    }

    public void setEnAddress(String enAddress) {
        this.enAddress = enAddress;
    }

    public String getEnBusinessCode() {
        return enBusinessCode;
    }

    public void setEnBusinessCode(String enBusinessCode) {
        this.enBusinessCode = enBusinessCode;
    }

    public Date getEnEstablishDate() {
        return enEstablishDate;
    }

    public void setEnEstablishDate(Date enEstablishDate) {
        this.enEstablishDate = enEstablishDate;
    }

    public Long getEmNum() {
        return emNum;
    }

    public void setEmNum(Long emNum) {
        this.emNum = emNum;
    }

    public String getEnTel() {
        return enTel;
    }

    public void setEnTel(String enTel) {
        this.enTel = enTel;
    }

    public String getEnFax() {
        return enFax;
    }

    public void setEnFax(String enFax) {
        this.enFax = enFax;
    }

    public String getEnUser() {
        return enUser;
    }

    public void setEnUser(String enUser) {
        this.enUser = enUser;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getEnCorporation() {
		return enCorporation;
	}

	public void setEnCorporation(String enCorporation) {
		this.enCorporation = enCorporation;
	}

	public String getInviteEnName() {
		return inviteEnName;
	}

	public void setInviteEnName(String inviteEnName) {
		this.inviteEnName = inviteEnName;
	}

	public String getInviteUserName() {
		return inviteUserName;
	}

	public void setInviteUserName(String inviteUserName) {
		this.inviteUserName = inviteUserName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "EnterpriseBaseInfo [uu=" + uu + ", enName=" + enName + ", enAddress=" + enAddress + ", enBusinessCode="
				+ enBusinessCode + ", enEstablishDate=" + enEstablishDate + ", emNum=" + emNum + ", enTel=" + enTel
				+ ", enFax=" + enFax + ", enUser=" + enUser + ", userTel=" + userTel + ", bank=" + bank + ", scope="
				+ scope + ", remark=" + remark + ", adminName=" + adminName + ", enCorporation=" + enCorporation
				+ ", inviteEnName=" + inviteEnName + ", inviteUserName=" + inviteUserName + ", date=" + date + "]";
	}
    
}
