Ext.define('erp.view.hr.attendance.AttendConfirm',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	//hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: '',
					deleteUrl: '',
					updateUrl: '',
					getIdUrl: '',
					auditUrl: '',
					resAuditUrl: '',
					submitUrl: '',
					resSubmitUrl: '',
					confirmUrl: 'hr/attendance/AttendConfirm.action',
					resconfirmUrl: 'hr/attendance/AttendResConfirm.action?caller=AttendConfirm',
					keyField: 'ac_id',
					codeField: 'ac_code',
					statusField: '',
					statusCodeField: '',
					confirmStatusField:'ac_confirmstatus',
					confirmStatusCodeField:'ac_confirmstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%',
					detno: 'acd_detno',
					/*necessaryField: 'pd_prodcode',*/
					keyField: 'acd_id',
					mainField: 'acd_acid'
				}
			] 
		}); 
		me.callParent(arguments); 
	} 
});