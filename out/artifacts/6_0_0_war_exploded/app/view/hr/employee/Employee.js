Ext.define('erp.view.hr.employee.Employee',{ 
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
					anchor: '100% 70%',
					saveUrl: 'hr/employee/saveEmployee.action',
					deleteUrl: 'hr/employee/deleteEmployee.action',
					updateUrl: 'hr/employee/updateEmployee.action',
					auditUrl: 'hr/employee/auditEmployee.action',
					resAuditUrl: 'hr/employee/resAuditEmployee.action',
					submitUrl: 'hr/employee/submitEmployee.action',
					resSubmitUrl: 'hr/employee/resSubmitEmployee.action',
					getIdUrl: 'common/getId.action?seq=EMPLOYEE_SEQ',
					keyField: 'em_id',
					codeField: 'em_code',
					statusField: 'em_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					necessaryField: 'jo_name',
					keyField: 'jo_id',
					mainField: 'jo_emid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});