package com.uas.erp.service.ma.impl;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.ma.LogoService;
@Service
public class LogoServiceImpl implements LogoService {
	@Autowired
	private BaseDao baseDao;
	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();    
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder(); 
	@Override
	public void saveLogo(MultipartFile file) {
		// TODO Auto-generated method stub   
		try {
			byte[] bytes=file.getBytes(); 
			SqlMap sqlMap=new SqlMap("SYS_LOGO");
			sqlMap.set("TEXT_", encoder.encodeBuffer(bytes).trim());
			baseDao.execute(sqlMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public byte[] getLogo() {
		// TODO Auto-generated method stub
		SqlRowList sl=baseDao.queryForRowSet("SELECT TEXT_ FROM SYS_LOGO");
		if(sl.next()){
			try {
				return  decoder.decodeBuffer(sl.getGeneralString(1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	@Override
	public void del() {
		// TODO Auto-generated method stub
		baseDao.execute("DELETE SYS_LOGO ");
	}
	@Override
	public boolean hasLogo() {
		// TODO Auto-generated method stub
		try {
			return baseDao.checkIf("SYS_LOGO", "1=1");
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		} 
		
	}
}
