package com.uas.b2b.model;

import java.util.Date;

/**
 * 商品信息
 * 
 * @author yingp
 *
 */
public class Product {

	private Long id;

	/**
	 * 商品信息标题
	 */
	private String title;

	/**
	 * 产品编号
	 */
	private String code;

	/**
	 * 产品规格
	 */
	private String spec;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 所属企业UU
	 */
	private Long enUU;

	/**
	 * 个人UU
	 */
	private Long userUU;

	/**
	 * 最小包装量
	 */
	private Double minPack;

	/**
	 * 最小采购量
	 */
	private Double minOrder;

	/**
	 * 采购提前期
	 */
	private Double leadtime;

	/**
	 * 备货提前期（天数）
	 */
	private Double ltinstock;

	/**
	 * 价格
	 */
	private Double price;

	/**
	 * 品牌（ERP)
	 */
	private String brand;

	/**
	 * 买方ERP物料ID
	 */
	private Long sourceId;

	/**
	 * 保存erp传入数据的时间
	 * 
	 * @return
	 */
	private Date erpDate;

	/**
	 * 原厂型号(erp)
	 * 
	 * @return
	 */
	private String cmpCode;

	/**
	 * UUID 标准料号
	 * 
	 * @return
	 */
	private String cmpUuId;

	/**
	 * 应用来源<br>
	 * 平台上传的为可以销售的
	 * 
	 * @return
	 */
	private String sourceApp;

	/**
	 * 是否可卖<br>
	 * 1. 可以卖 <br>
	 * 0. 不可
	 */
	private Short isSale;

	/**
	 * 是否可买<br>
	 * 1. 可以买<br>
	 * 0. 不可
	 */
	private Short isPurchase;

	/**
	 * 公开展示<br>
	 * 
	 * 1. 是<br>
	 * 0. 否
	 */
	private Short isShow;

	/**
	 * 公开销售 <br>
	 * 1. 是<br>
	 * 0. 否
	 */
	private Short isPubsale;

	/**
	 * 附件
	 */
	private String attachment;

	/**
	 * 类目（平台）（中文）
	 */
	private String kind;

	/**
	 * 品牌（平台）（中文）
	 */
	private String pbrand;

	/**
	 * 型号（平台）
	 */
	private String pcmpcode;

	/**
	 * 是否是标准物料<br>
	 * 1.YES<br>
	 * 0.NO
	 */
	private Short standard;

	/**
	 * 类目（平台）（英文）
	 */
	private String kinden;

	/**
	 * 品牌（平台）（英文）
	 */
	private String pbranden;

	/**
	 * 匹配状态（记录匹配状态，方便下次查看）
	 */
	private Integer matchstatus;

	/**
	 * 匹配数量（用作排序）
	 */
	private Integer matchsize;

	/**
	 * 下载状态，平台更新ERP的数据后，将数据回传回ERP更新
	 */
	private Integer downloadstatus;

	/**
	 * 图片
	 */
	private String img;

	/**
	 * 封装
	 */
	private String encapsulation;

	/**
	 * 匹配成标准的日期
	 */
	private Date tostandard;

	/**
	 * 编码版本号
	 */
	private String goodsnover;

	/**
	 * 税收分类编码
	 */
	private String goodstaxno;

	/**
	 * 是否享受优惠政策
	 */
	private String taxpre;

	/**
	 * 享受优惠政策内容
	 */
	private String taxprecon;

	/**
	 * ERP库存
	 */
	private Double reserve;

	/**
	 * 标准类目id
	 */
	private Long kindId;

	/**
	 * 标准品牌id
	 */
	private Long pbrandId;

	/**
	 * 标准品牌uuid
	 */
	private String pbrandUuid;

	/**
	 * 包装方式
	 */
	private String packaging;

	/**
	 * 产品建立时间
	 */
	private Date createTime;

	/**
	 * 产品生产日期
	 */
	private String manufactureDate;

	/**
	 * 最大交期
	 */
	private Short maxDelivery;

	/**
	 * 最小交期
	 */
	private Short minDelivery;

	/**
	 * 是否可拆卖
	 */
	private Boolean isBreakUp;
	/**
	 * 是否匹配到（用于供应商推荐接口匹配状态） 1 为匹配  0为不匹配
	 */
	private String ifMatched;
	public Product() {
	}

	public Product(PurchaseTenderProd tenderProd, Long currentEnUU) {
    	this.code = tenderProd.getProdCode();
    	this.title = tenderProd.getProdTitle();
    	this.unit = tenderProd.getUnit();
    	this.brand = tenderProd.getBrand();
    	this.enUU = currentEnUU;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Long getEnUU() {
		return enUU;
	}

	public void setEnUU(Long enUU) {
		this.enUU = enUU;
	}

	public Long getUserUU() {
		return userUU;
	}

	public void setUserUU(Long userUU) {
		this.userUU = userUU;
	}

	public Double getMinPack() {
		return minPack;
	}

	public void setMinPack(Double minPack) {
		this.minPack = minPack;
	}

	public Double getMinOrder() {
		return minOrder;
	}

	public void setMinOrder(Double minOrder) {
		this.minOrder = minOrder;
	}

	public Double getLeadtime() {
		return leadtime;
	}

	public void setLeadtime(Double leadtime) {
		this.leadtime = leadtime;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Double getLtinstock() {
		return ltinstock;
	}

	public void setLtinstock(Double ltinstock) {
		this.ltinstock = ltinstock;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Date getErpDate() {
		return erpDate;
	}

	public void setErpDate(Date erpDate) {
		this.erpDate = erpDate;
	}

	public String getCmpCode() {
		return cmpCode;
	}

	public void setCmpCode(String cmpCode) {
		this.cmpCode = cmpCode;
	}

	public String getCmpUuId() {
		return cmpUuId;
	}

	public void setCmpUuId(String cmpUuId) {
		this.cmpUuId = cmpUuId;
	}

	public Boolean getIsBreakUp() {
		return isBreakUp;
	}

	public void setIsBreakUp(Boolean isBreakUp) {
		this.isBreakUp = isBreakUp;
	}

	public String getIfMatched() {
		return ifMatched;
	}

	public void setIfMatched(String ifMatched) {
		this.ifMatched = ifMatched;
	}

	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public Short getIsSale() {
		return isSale;
	}

	public void setIsSale(Short isSale) {
		this.isSale = isSale;
	}

	public Short getIsPurchase() {
		return isPurchase;
	}

	public void setIsPurchase(Short isPurchase) {
		this.isPurchase = isPurchase;
	}

	public Short getIsShow() {
		return isShow;
	}

	public void setIsShow(Short isShow) {
		this.isShow = isShow;
	}

	public Short getIsPubsale() {
		return isPubsale;
	}

	public void setIsPubsale(Short isPubsale) {
		this.isPubsale = isPubsale;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getPbrand() {
		return pbrand;
	}

	public void setPbrand(String pbrand) {
		this.pbrand = pbrand;
	}

	public String getPcmpcode() {
		return pcmpcode;
	}

	public void setPcmpcode(String pcmpcode) {
		this.pcmpcode = pcmpcode;
	}

	public Short getStandard() {
		return standard;
	}

	public void setStandard(Short standard) {
		this.standard = standard;
	}

	public String getKinden() {
		return kinden;
	}

	public void setKinden(String kinden) {
		this.kinden = kinden;
	}

	public String getPbranden() {
		return pbranden;
	}

	public void setPbranden(String pbranden) {
		this.pbranden = pbranden;
	}

	public Integer getMatchstatus() {
		return matchstatus;
	}

	public void setMatchstatus(Integer matchstatus) {
		this.matchstatus = matchstatus;
	}

	public Integer getMatchsize() {
		return matchsize;
	}

	public void setMatchsize(Integer matchsize) {
		this.matchsize = matchsize;
	}


	public Integer getDownloadstatus() {
		return downloadstatus;
	}

	public void setDownloadstatus(Integer downloadstatus) {
		this.downloadstatus = downloadstatus;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}

	public Date getTostandard() {
		return tostandard;
	}

	public void setTostandard(Date tostandard) {
		this.tostandard = tostandard;
	}

	public String getGoodsnover() {
		return goodsnover;
	}

	public void setGoodsnover(String goodsnover) {
		this.goodsnover = goodsnover;
	}

	public String getGoodstaxno() {
		return goodstaxno;
	}

	public void setGoodstaxno(String goodstaxno) {
		this.goodstaxno = goodstaxno;
	}

	public String getTaxpre() {
		return taxpre;
	}

	public void setTaxpre(String taxpre) {
		this.taxpre = taxpre;
	}

	public String getTaxprecon() {
		return taxprecon;
	}

	public void setTaxprecon(String taxprecon) {
		this.taxprecon = taxprecon;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public Double getReserve() {
		return reserve;
	}

	public void setReserve(Double reserve) {
		this.reserve = reserve;
	}

	public Long getKindId() {
		return kindId;
	}

	public void setKindId(Long kindId) {
		this.kindId = kindId;
	}

	public Long getPbrandId() {
		return pbrandId;
	}

	public void setPbrandId(Long pbrandId) {
		this.pbrandId = pbrandId;
	}

	public String getPbrandUuid() {
		return pbrandUuid;
	}

	public void setPbrandUuid(String pbrandUuid) {
		this.pbrandUuid = pbrandUuid;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Short getMaxDelivery() {
		return maxDelivery;
	}

	public void setMaxDelivery(Short maxDelivery) {
		this.maxDelivery = maxDelivery;
	}

	public Short getMinDelivery() {
		return minDelivery;
	}

	public void setMinDelivery(Short minDelivery) {
		this.minDelivery = minDelivery;
	}

	public Boolean getBreakUp() {
		return isBreakUp;
	}

	public void setBreakUp(Boolean breakUp) {
		isBreakUp = breakUp;
	}
	@Override
	public String toString() {
		return "编号：" + getCode() + "，标题：" + getTitle() + "，规格型号：" + getSpec();
	}
}
