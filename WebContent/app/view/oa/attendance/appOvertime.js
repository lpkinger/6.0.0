Ext.define('erp.view.oa.attendance.appOvertime',{ 
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
					anchor: '100% 50%',
					saveUrl: 'oa/attendance/saveAppOvertime.action',
					deleteUrl: 'oa/attendance/deleteAppOvertime.action',
					updateUrl: 'oa/attendance/updateAppOvertime.action',
					auditUrl: 'oa/attendance/auditAppOvertime.action',
					resAuditUrl: 'oa/attendance/resAuditAppOvertime.action',
					submitUrl: 'oa/attendance/submitAppOvertime.action',
					resSubmitUrl: 'oa/attendance/resSubmitAppOvertime.action',
					getIdUrl: 'common/getId.action?seq=APPOVERTIME_SEQ',
					keyField: 'ao_id',
					codeField: 'ao_code',
					statusField: 'ao_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'aod_detno',
					necessaryField: 'aod_code',
					keyField: 'aod_id',
					mainField: 'aod_aoid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});