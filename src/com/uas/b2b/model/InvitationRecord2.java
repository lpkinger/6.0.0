package com.uas.b2b.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 邀请合作伙伴注册
 * 
 * @author hejq
 * @time 创建时间：2017年4月19日
 */
public class InvitationRecord2 {

	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 邀请企业uu
	 */
	private Long enuu;
	
	/**
	 * 用户uu
	 */
	private Long useruu;
	
	/**
	 * 最后一次邀请时间
	 */
	private Date date;

	/**
	 * 邀请的客户名称
	 */
	private String vendname;

	/**
	 * 邀请的用户名称
	 */
	private String vendusername;

	/**
	 * 邀请的用户的电话
	 */
	private String vendusertel;

	/**
	 * 邀请的用户的邮箱
	 */
	private String venduseremail;

	/**
	 * 可能出现重复邀请的情况，记录邀请次数
	 */
	private Integer count;
	/**
	 * 邀请状态
	 * @return
	 */
	private Integer active;
	/**
	 * 如果已经注册，填入注册生成的UU号
	 * */
	private Long venduu;
	/**
	 * 如果已注册而且记录的注册时间，填入注册时间
	 * */
	private Date registerDate;
	/**
	 * 匹配出相同名字的已注册的企业数量
	 * */
	private Integer samecount;
	
	/**
	 * 邀请人信息（企业状态为已注册）
	 * */
	private String user;
	/**
	 * 企业注册邀请人
	 * */
	private String inviteUserName;
	/**
	 * 企业注册邀请人公司
	 * */
	private String inviteEnName;
	/**
	 * 来源
	 * */
	private String source;
	/**上传物料数
	 * */
	private int productNum;
	
	public int getProductNum() {
		return productNum;
	}

	public void setProductNum(int productNum) {
		this.productNum = productNum;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public Long getUseruu() {
		return useruu;
	}

	public void setUseruu(Long useruu) {
		this.useruu = useruu;
	}

	public String getDate() {
		if(this.date!=null){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.date);
		}else return "";
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getVendname() {
		return vendname;
	}

	public void setVendname(String vendname) {
		this.vendname = vendname;
	}

	public String getVendusertel() {
		return vendusertel;
	}

	public void setVendusertel(String vendusertel) {
		this.vendusertel = vendusertel;
	}

	public String getVenduseremail() {
		return venduseremail;
	}

	public void setVenduseremail(String venduseremail) {
		this.venduseremail = venduseremail;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getVendusername() {
		return vendusername;
	}

	public void setVendusername(String vendusername) {
		this.vendusername = vendusername;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Long getVenduu() {
		return venduu;
	}

	public void setVenduu(Long venduu) {
		this.venduu = venduu;
	}

	public String getRegisterDate() {
		if(this.registerDate!=null){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.registerDate); 
		}else return "";
	}
	
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Integer getSamecount() {
		return samecount;
	}

	public void setSamecount(Integer samecount) {
		this.samecount = samecount;
	}


	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getInviteUserName() {
		return inviteUserName;
	}

	public void setInviteUserName(String inviteUserName) {
		this.inviteUserName = inviteUserName;
	}

	public String getInviteEnName() {
		return inviteEnName;
	}

	public void setInviteEnName(String inviteEnName) {
		this.inviteEnName = inviteEnName;
	}

}
