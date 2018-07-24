package com.uas.erp.dao.common.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.PaymentsDao;
import com.uas.erp.model.PaymentsForDate;
@Repository
public class PaymentsDaoImpl extends BaseDao implements PaymentsDao {

	@SuppressWarnings("null")
	@Override
	public PaymentsForDate findPaymentsById(String pa_id) {
		PaymentsForDate paymentsForDate = null;
		Connection conn=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn=getConnection();
			ps = conn.prepareStatement("SELECT * FROM payments WHERE pa_id='"+ pa_id+"'");
			rs = ps.executeQuery();
			if(rs.next()){
				paymentsForDate = new PaymentsForDate();
				paymentsForDate.setPa_id(Integer.parseInt(pa_id));
				paymentsForDate.setPa_monthadd(rs.getInt("pa_monthadd"));
				paymentsForDate.setPa_dayadd(rs.getInt("pa_dayadd"));
				paymentsForDate.setPa_code(rs.getString("pa_code"));
				
				
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return paymentsForDate;
	}

}
