Ext.define('erp.view.hr.attendance.holiday',{
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'hr/attendance/saveHoliday.action?caller=' +caller,
					deleteUrl: 'hr/attendance/deleteHoliday.action?caller=' +caller,
					updateUrl: 'hr/attendance/updateHoliday.action?caller=' +caller,
					auditUrl: 'hr/attendance/auditHoliday.action?caller=' +caller,
					resAuditUrl: 'hr/attendance/resAuditHoliday.action?caller=' +caller,
					submitUrl: 'hr/attendance/submitHoliday.action?caller=' +caller,
					printUrl: 'hr/attendance/printHoliday.action?caller=' +caller,
					resSubmitUrl: 'hr/attendance/resSubmitHoliday.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=HOLIDAY_SEQ',
					keyField: 'ho_id',
                    codeField: 'ho_code',
					statusField: 'ho_status',
					statuscodeField: 'ho_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});