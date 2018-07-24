Ext.define('erp.view.hr.attendance.SpeAttendance',{
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
					saveUrl: 'hr/attendance/saveSpeAttendance.action',
					deleteUrl: 'hr/attendance/deleteSpeAttendance.action',
					updateUrl: 'hr/attendance/updateSpeAttendance.action',
					auditUrl: 'hr/attendance/auditSpeAttendance.action',
					resAuditUrl: 'hr/attendance/resAuditSpeAttendance.action',
					submitUrl: 'hr/attendance/submitSpeAttendance.action',
					resSubmitUrl: 'hr/attendance/resSubmitSpeAttendance.action',
					confirmUrl:'hr/attendance/confirmSpeAttendance.action',
					endUrl: 'hr/attendance/endSpeAttendance.action',
					resEndUrl: 'hr/attendance/resEndSpeAttendance.action?caller=SpeAttendance',
					getIdUrl: 'common/getId.action?seq=SpeAttendance_SEQ',
					keyField: 'sa_id',
					codeField: 'sa_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});