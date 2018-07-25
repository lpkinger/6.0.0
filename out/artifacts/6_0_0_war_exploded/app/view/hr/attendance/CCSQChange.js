Ext.define('erp.view.hr.attendance.CCSQChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'hr/attendance/saveCCSQChange.action',
					deleteUrl: 'hr/attendance/deleteCCSQChange.action',
					updateUrl: 'hr/attendance/updateCCSQChange.action',
					getIdUrl: 'common/getId.action?seq=CCSQChange_SEQ',
					auditUrl: 'hr/attendance/auditCCSQChange.action',
					resAuditUrl: 'hr/attendance/resAuditCCSQChange.action',
					submitUrl: 'hr/attendance/submitCCSQChange.action',
					resSubmitUrl: 'hr/attendance/resSubmitCCSQChange.action',
					keyField: 'cc_id',
					codeField: 'cc_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					keyField: 'cd_id',
					detno: 'cd_detno',
					mainField: 'cd_ccid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});