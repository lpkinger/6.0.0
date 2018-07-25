Ext.define('erp.view.hr.employee.Role',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'hr/employee/saveRole.action',
				deleteUrl: 'hr/employee/deleteRole.action',
				updateUrl: 'hr/employee/updateRole.action',
				auditUrl: 'hr/employee/auditRole.action',
				resAuditUrl: 'hr/employee/resAuditRole.action',
				submitUrl: 'hr/employee/submitRole.action',
				resSubmitUrl: 'hr/employee/resSubmitRole.action',
				getIdUrl: 'common/getId.action?seq=ROLE_SEQ',
				keyField: 'ro_id',
				statusField: 'ro_status'
			}]
		}); 
		me.callParent(arguments); 
	} 
});