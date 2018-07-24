package com.uas.b2b.model;

import java.io.Serializable;

/**
 * 招标单产品信息
 *
 * Created by dongbw
 * 17/03/30 10:00.
 */
public class SaleTenderProdErp implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 序号，用于按序显示
     */
    private Short index;

    /**
     * 物料名称
     */
    private String prodTitle;

    /**
     * 物料规格
     */
    private String prodSpec;

    /**
     * 物料型号
     */
    private String prodCode;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 单位
     */
    private String unit;

    /**
     * 采购数量
     */
    private Long qty;

    /**
     * 招标单
     */
    private PurchaseTenderErp tenderErp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getIndex() {
        return index;
    }

    public void setIndex(Short index) {
        this.index = index;
    }

    public String getProdTitle() {
        return prodTitle;
    }

    public void setProdTitle(String prodTitle) {
        this.prodTitle = prodTitle;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public String getProdSpec() {
        return prodSpec;
    }

    public void setProdSpec(String prodSpec) {
        this.prodSpec = prodSpec;
    }

	public PurchaseTenderErp getTenderErp() {
		return tenderErp;
	}

	public void setTenderErp(PurchaseTenderErp tenderErp) {
		this.tenderErp = tenderErp;
	}
}
