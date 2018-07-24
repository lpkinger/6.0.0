package com.uas.erp.service.common;

/**
 * 实现附件上传的抽象接口
 * <br>一般是上传到<b>平台</b>
 * @author suntg
 *
 */
public interface AttachUploadedAble {

	/**
	 * 获取附件字段的值
	 * @return
	 */
	public String getAttachs();
	
	/**
	 * 获取附件对应的关联属性的值
	 * <br>关联属性通过上传的方法传递
	 * @return
	 */
	public Object getReffrencValue();
}
