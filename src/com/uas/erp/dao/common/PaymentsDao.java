package com.uas.erp.dao.common;

import com.uas.erp.model.PaymentsForDate;

public interface PaymentsDao {

	PaymentsForDate findPaymentsById(String pa_id);
}
