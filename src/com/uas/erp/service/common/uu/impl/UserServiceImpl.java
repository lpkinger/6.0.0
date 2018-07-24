package com.uas.erp.service.common.uu.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MobileInfo;
import com.uas.erp.service.common.uu.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private DataListDao dataListDao;

	private static final String CREATE_USER = "INSERT INTO ofUser (username,plainPassword,encryptedPassword,name,email,creationDate,modificationDate) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String REMOVE_USER = "DELETE FROM OFUSER WHERE USERNAME = ?";

	private static final String MODIFY_PASSWORD = "UPDATE OFUSER SET ENCRYPTEDPASSWORD = ?, MODIFICATIONDATE = ?" + "WHERE USERNAME = ?";

	private static final String LOAD_PASSWORDKEY = "select propvalue from ofproperty where name = 'passwordKey'";

	private static final char[] zeroArray = "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();

	private String passwordKey;

	public String getEncryptedPassword(String plainPassword) {
		passwordKey = this.getPasswordKey();
		Blowfish blowfish = new Blowfish(passwordKey);
		String encryptedPassword = blowfish.encryptString(plainPassword);
		return encryptedPassword;
	}

	public String getPlainPassword(String encryptedPassword) {
		passwordKey = this.getPasswordKey();
		Blowfish blowfish = new Blowfish(passwordKey);
		String plainPassword = blowfish.decryptString(encryptedPassword);
		return plainPassword;
	}

	public String getPassword(String encryptedPassword) {
		passwordKey = this.getPasswordKey();
		Blowfish blowfish = new Blowfish(passwordKey);
		String plainPassword = blowfish.decryptString(encryptedPassword);
		return plainPassword;
	}

	public String decryptPassword(String token, String date) {
		if (date == null) {
			return token;
		}
		try {
			int length = 15;
			StringBuilder buf = new StringBuilder(length);
			buf.append(zeroArray, 0, length - date.length()).append(date);
			date = buf.toString();
			Blowfish blowfish = new Blowfish(date);
			return blowfish.decryptString(token);
		} catch (Exception e) {
			return null;
		}
	}

	public String getPlainPassword(String token, String date) {
		String plainPassword = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		long time = Long.parseLong(date);
		Date d = new Date(time);
		String key = sdf.format(d);
		String encryptedPassword = null;
		try {
			encryptedPassword = decrypt(token, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		plainPassword = getPlainPassword(encryptedPassword);
		return plainPassword;
	}

	private void connect() {
		SpObserver.putSp("uu");
	}

	public void createUser(String username, String password) {
		connect();
		String encryptedPassword = getEncryptedPassword(password);
		String creationDate = dateToMillis();
		String modificationDate = creationDate;
		baseDao.execute(CREATE_USER, username, null, encryptedPassword, null, null, creationDate, modificationDate);
	}

	public void createUser(String username, String password, String name, String email) {
		connect();
		String plainPassword = null;
		String encryptedPassword = getEncryptedPassword(password);
		String creationDate = null;
		String modificationDate = null;
		creationDate = dateToMillis();
		modificationDate = creationDate;
		if (name == null || name.matches("\\s*")) {
			name = null;
		}
		if (email == null || email.matches("\\s*")) {
			email = null;
		}
		baseDao.execute(CREATE_USER, username, plainPassword, encryptedPassword, name, email, creationDate, modificationDate);
		Friends friends = new Friends();
		friends.beFriends(username, username, "我的好友");
	}

	public void removeUser(String username) {
		connect();
		baseDao.execute(REMOVE_USER, username);
	}

	public boolean modifyPassword(String username, String newPlainPassword) {
		boolean status = false;
		try {
			connect();
			String modificationDate = dateToMillis();
			String newEncryptedPassword = getEncryptedPassword(newPlainPassword);
			status = baseDao.execute(MODIFY_PASSWORD, newEncryptedPassword, modificationDate, username);
		} catch (Exception e) {

		}
		return status;
	}

	private String getPasswordKey() {
		connect();
		SqlRowList rs = baseDao.queryForRowSet(LOAD_PASSWORDKEY);
		if (rs.next()) {
			passwordKey = rs.getString("propvalue");
			return passwordKey;
		}
		return null;
	}

	private String dateToMillis() {
		// return zeroPadString(Long.toString(date.getTime()), 15);
		Date date = new Date();
		String string = Long.toString(date.getTime());
		int length = 15;
		if (string == null || string.length() > length) {
			return string;
		}
		StringBuilder buf = new StringBuilder(length);
		buf.append(zeroArray, 0, length - string.length()).append(string);
		return buf.toString();
	}

	public String encrypt(String message, String key) throws Exception {
		if (message == null) {
			return null;
		}
		String result = null;
		message = URLEncoder.encode(message, "utf-8");

		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		byte[] temp = cipher.doFinal(message.getBytes("UTF-8"));
		result = byteArrToHexStr(temp);
		return result;
	}

	public String decrypt(String message, String key) throws Exception {
		if (message == null) {
			return null;
		}
		String result = null;
		byte[] bytesrc = hexStrToByteArr(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		result = new String(retByte);
		result = URLDecoder.decode(result, "utf-8");
		return result;
	}

	public String byteArrToHexStr(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString();
	}

	public byte[] hexStrToByteArr(String s) {
		byte digest[] = new byte[s.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = s.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}
		return digest;
	}

	@Override
	public String getDBSetting(String code) {
		return baseDao.getDBSetting(code);
	}

	@Override
	public MobileInfo getMobileInfo(Employee employee) {
		SpObserver.putSp(employee.getEm_master());
		DataList dataList = dataListDao.getDataList("JProcess!Me", employee.getEm_master());
		MobileInfo info = new MobileInfo();
		List<Map<String, Object>> flows = dataListDao.getDataListData(dataList, "(jp_nodedealman='" + employee.getEm_code()
				+ "' AND jp_status='待审批') or (jp_launcherid='" + employee.getEm_code() + "'  AND jp_status='未通过')", employee, 1, 50, 1,
				false, null,false);
		info.setFlows(flows);
		info.setFlowCount(flows.size());
		dataList = dataListDao.getDataList("JProCand", employee.getEm_master());
		List<Map<String, Object>> procands = dataListDao.getDataListData(dataList, "jp_candidate='" + employee.getEm_code()
				+ "' AND jp_status='待审批' AND jp_flag=1", employee, 1, 50, 1, false, null,false);
		info.setProcands(procands);
		info.setProcandCount(procands.size());
		dataList = dataListDao.getDataList("ResourceAssignment!Bill", employee.getEm_master());
		List<Map<String, Object>> tasks = dataListDao.getDataListData(dataList, "ra_emid=" + employee.getEm_id()
				+ " AND ra_taskpercentdone<100", employee, 1, 50, 1, false, null,false);
		info.setTasks(tasks);
		info.setTaskCount(tasks.size());
		info.setEmployee(employee);
		return info;
	}
}
