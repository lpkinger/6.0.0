package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 *  投标单明细
 * Created by dongbw
 * 17/03/30 11:53.
 */
public class SaleTenderItemErp implements Serializable,Comparable<SaleTenderItemErp>{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private Long id;


    /**
     * 税率
     */
    private Long taxrate;

    /**
     * 单价
     */
    private Double price;

    /**
     * 采购周期
     */
    private Long cycle;
    
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
     * 说明
     */
    private String description;

    /**
     * 中标状态(为空表示未处理，0表示未中标，1表示已中标)
     */
    private Short applyStatus;

    /**
     * 招标产品明细
     */
    private SaleTenderProdErp tenderProd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(Long taxrate) {
        this.taxrate = taxrate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
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

	public String getProdSpec() {
		return prodSpec;
	}

	public void setProdSpec(String prodSpec) {
		this.prodSpec = prodSpec;
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

	public Short getApplyStatus() {
		Short status = tenderProd.getTenderErp().getAuditStatus();
		if (status!=null&&status==0) {
			return 0;
		}
        return applyStatus;
    }

    public void setApplyStatus(Short applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public SaleTenderProdErp getTenderProd() {
        return tenderProd;
    }

    public void setTenderProd(SaleTenderProdErp tenderProd) {
        this.tenderProd = tenderProd;
    }

	@Override
	public int compareTo(SaleTenderItemErp s) {
		return this.index.compareTo(s.getIndex());
	}

}
