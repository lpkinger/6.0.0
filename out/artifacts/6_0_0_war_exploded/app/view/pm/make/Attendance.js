Ext.define('erp.view.pm.make.Attendance',{
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
					anchor: '100% 35%',
					saveUrl: 'pm/make/saveAttendance.action',
					deleteUrl: 'pm/make/deleteAttendance.action',
					updateUrl: 'pm/make/updateAttendance.action',
					auditUrl: 'pm/make/auditAttendance.action',
					resAuditUrl: 'pm/make/resAuditAttendance.action',
					submitUrl: 'pm/make/submitAttendance.action',
					resSubmitUrl: 'pm/make/resSubmitAttendance.action',
					getIdUrl: 'common/getId.action?seq=ATTENDANCE_SEQ',
					keyField: 'at_id',
					codeField: 'at_code',
					statusField: 'at_status',
					statuscodeField: 'at_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					keyField: 'ad_id',
					detno: 'ad_detno',
					mainField: 'ad_atid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});