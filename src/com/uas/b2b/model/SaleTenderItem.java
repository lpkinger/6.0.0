package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 *  投标单明细
 * Created by dongbw
 * 17/03/30 11:53.
 */
public class SaleTenderItem implements Serializable{

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
     * 说明
     */
    private String description;

    /**
     * 中标状态(为空表示未处理，0表示未中标，1表示已中标)
     */
    private Short applyStatus;
    
    /**
     * 投标企业主表id
     */
    private Long saleId;
    /**
   	 * 投标企业UU
   	 */
   	private Long vendUU;
    
    /**
   	 * 投标企业名称
   	 */
   	private String enName;


    /**
     * 招标产品明细
     */
    private PurchaseTenderProd tenderProd;

    /**
     * 投标主表
     */
    private SaleTender saleTender;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Long getTaxrate() {
    	Short status = saleTender.getAuditStatus();
    	if (status!=null&&status==0) {
			return (long) 0;
		}else if(saleTender.getIfAll()!=null&&saleTender.getIfAll()==1){
			return saleTender.getTaxrate();
		}
        return taxrate;
    }
	
    public void setTaxrate(Long taxrate) {
        this.taxrate = taxrate;
    }

    public Double getPrice() {
    	Short status = saleTender.getAuditStatus();
    	if (status!=null&&status==0) {
			return (double) 0;
		}
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCycle() {
    	Short status = saleTender.getAuditStatus();
    	if (status!=null&&status==0) {
			return (long) 0;
		}else if(saleTender.getIfAll()!=null&&saleTender.getIfAll()==1){
			return saleTender.getCycle();
		}
        return cycle;
    }
    
    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Double getTotalMoney() {
    	Short status = saleTender.getAuditStatus();
    	if (status!=null&&status==0) {
			return (double) 0;
		}else{
			return saleTender.getTotalMoney();
		}
	}

	public Short getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Short applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getDescription() {
    	Short status = saleTender.getAuditStatus();
    	if (status!=null&&status==0) {
			return null;
		}
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Long getSaleId() {
		saleId = saleTender.getId();
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public Long getVendUU() {
		vendUU = saleTender.getVendUU();
		return vendUU;
	}

	public void setVendUU(Long vendUU) {
		this.vendUU = vendUU;
	}

	public String getEnName() {
		enName = saleTender.getEnterpriseBaseInfo().getEnName();
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}


	@JsonIgnore
    @JSONField(serialize = false)
    public PurchaseTenderProd getTenderProd() {
        return tenderProd;
    }

    public void setTenderProd(PurchaseTenderProd tenderProd) {
        this.tenderProd = tenderProd;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public SaleTender getSaleTender() {
        return saleTender;
    }

    public void setSaleTender(SaleTender saleTender) {
        this.saleTender = saleTender;
    }
}
