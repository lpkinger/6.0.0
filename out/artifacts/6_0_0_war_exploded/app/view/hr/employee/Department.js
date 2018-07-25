Ext.define('erp.view.hr.employee.Department',{ 
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
					saveUrl: 'hr/employee/saveDepartment.action',
					deleteUrl: 'hr/employee/deleteDepartment.action',
					updateUrl: 'hr/employee/updateDepartment.action',
					auditUrl: 'hr/employee/auditDepartment.action?caller=' +caller,
					resAuditUrl: 'hr/employee/resAuditDepartment.action',
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					bannedUrl: 'common/bannedCommon.action?caller='+caller,
					resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Department_SEQ',
					keyField: 'dp_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});