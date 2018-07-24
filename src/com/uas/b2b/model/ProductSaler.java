package com.uas.b2b.model;

/**
 * ERP个人物料
 *
 * Created by hejq on 2018-01-12.
 */
public class ProductSaler {

    /**
     * 主键id
     */
    private Long ps_id;

    /**
     * 物料code
     */
    private String ps_code;

    /**
     * 个人uu号
     */
    private Long em_uu;

    /**
     * 企业uu号
     */
    private Long en_uu;

    public Long getPs_id() {
        return ps_id;
    }

    public void setPs_id(Long ps_id) {
        this.ps_id = ps_id;
    }

    public String getPs_code() {
        return ps_code;
    }

    public void setPs_code(String ps_code) {
        this.ps_code = ps_code;
    }

    public Long getEm_uu() {
        return em_uu;
    }

    public void setEm_uu(Long em_uu) {
        this.em_uu = em_uu;
    }

    public Long getEn_uu() {
        return en_uu;
    }

    public void setEn_uu(Long en_uu) {
        this.en_uu = en_uu;
    }
}

