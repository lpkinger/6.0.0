package com.uas.b2b.model;
import java.util.List;


/**
 * 供应商推荐实体
 * Created by dongbw
 * 18/01/18 15:40.
 */
public class VendorRecommend {

    /**
     * 推荐的供应商UU
     */
    private Long en_uu;

    /**
     * 推荐的供应商名称
     */
    private String en_name;

    /**
     * 推荐的供应商简称
     */
    private String en_shortname;

    /**
     * 推荐的供应商地址
     */
    private String en_address;

    /**
     * 推荐的供应商电话
     */
    private String en_tel;

    /**
     * 推荐的供应商邮箱
     */
    private String en_email;

    /**
     * 公司法人
     */
    private String en_corporation;

    /**
     * 商业登记证号
     */
    private String en_businesscode;

    /**
     * 行业
     */
    private String en_profession;

    /**
     * 主营业务
     */
    private String en_tags;

    /**
     * 联系人
     */
    private String en_contactman;

    /**
     * 联系人电话
     */
    private String en_contacttel;

    /**
     * 匹配物料信息 名称（品牌）
     */
    private String productInfo;

    /**
     * 匹配物料个数
     * @return
     */
    private Integer hitNums;
    /**
     * 币别
     * @return
     * */
    private String en_currency;

    /**
     * 是否为供应商 1为是， 0 为否
     */
    private Short isVendor;

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public Integer getHitNums() {
        return hitNums;
    }

    public void setHitNums(Integer hitNums) {
        this.hitNums = hitNums;
    }

    public Short getIsVendor() {
        return isVendor;
    }

    public void setIsVendor(Short isVendor) {
        this.isVendor = isVendor;
    }

    public Long getEn_uu() {
        return en_uu;
    }

    public void setEn_uu(Long en_uu) {
        this.en_uu = en_uu;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getEn_shortname() {
        return en_shortname;
    }

    public void setEn_shortname(String en_shortname) {
        this.en_shortname = en_shortname;
    }

    public String getEn_address() {
        return en_address;
    }

    public void setEn_address(String en_address) {
        this.en_address = en_address;
    }

    public String getEn_tel() {
        return en_tel;
    }

    public void setEn_tel(String en_tel) {
        this.en_tel = en_tel;
    }

    public String getEn_email() {
        return en_email;
    }

    public void setEn_email(String en_email) {
        this.en_email = en_email;
    }

    public String getEn_corporation() {
        return en_corporation;
    }

    public void setEn_corporation(String en_corporation) {
        this.en_corporation = en_corporation;
    }

    public String getEn_businesscode() {
        return en_businesscode;
    }

    public void setEn_businesscode(String en_businesscode) {
        this.en_businesscode = en_businesscode;
    }

    public String getEn_profession() {
        return en_profession;
    }

    public void setEn_profession(String en_profession) {
        this.en_profession = en_profession;
    }

    public String getEn_tags() {
        return en_tags;
    }

    public void setEn_tags(String en_tags) {
        this.en_tags = en_tags;
    }

    public String getEn_contactman() {
        return en_contactman;
    }

    public void setEn_contactman(String en_contactman) {
        this.en_contactman = en_contactman;
    }

    public String getEn_contacttel() {
        return en_contacttel;
    }

    public void setEn_contacttel(String en_contacttel) {
        this.en_contacttel = en_contacttel;
    }

	public String getEn_currency() {
		return en_currency;
	}

	public void setEn_currency(String en_currency) {
		this.en_currency = en_currency;
	}

	@Override
	public String toString() {
		return "VendorRecommend [en_uu=" + en_uu + ", en_name=" + en_name + ", en_shortname=" + en_shortname
				+ ", en_address=" + en_address + ", en_tel=" + en_tel + ", en_email=" + en_email + ", en_corporation="
				+ en_corporation + ", en_businesscode=" + en_businesscode + ", en_profession=" + en_profession
				+ ", en_tags=" + en_tags + ", en_contactman=" + en_contactman + ", en_contacttel=" + en_contacttel
				+ ", productInfo=" + productInfo + ", hitNums=" + hitNums + ", en_currency=" + en_currency
				+ ", isVendor=" + isVendor + "]";
	}
    
}
