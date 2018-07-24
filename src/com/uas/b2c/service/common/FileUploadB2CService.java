package com.uas.b2c.service.common;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.uas.api.b2c_erp.baisc.service.FileUploadUasService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Master;
import com.uas.remoting.hessian.MultiProxyFactoryBean;
/**
 *
 * 上传图片至商城
 * @author XiaoST  2016年8月23日 下午6:24:15
 */
@Service
public class FileUploadB2CService {
	@Resource(name = "api.FileUploadService")
	private FileUploadUasService UploadUasService;
	
	/**
	 * 上传图片 return图片路径
	 * 
	 * @param file
	 * @return
	 */
	public String saveImage(String fileName, byte[] fileBytes){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			return UploadUasService.imgUpload(fileName,fileBytes);
		}
		return null;
	}
	
	public String saveFile(String fileName, byte[] fileBytes){
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			return UploadUasService.fileUpload(fileName,fileBytes);
		}
		return null;
	}
}
