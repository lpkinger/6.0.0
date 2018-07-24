package com.uas.erp.service.hr;


/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-6-17
 * Time: 上午9:25
 * To change this template use File | Settings | File Templates.
 */
public interface HolidayService {

    public void saveHoliday(String formStore, String gridStore, String  caller);

    public void updateHolidayById(String formStore, String gridStore, String  caller);

    public void deleteHoliday(int ho_id, String  caller);

}
