Ext.define('erp.view.hr.employee.EmployeeList',{ 
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
					updateUrl: 'hr/employee/updateEmployeeList.action',
					getIdUrl: 'common/getId.action?seq=EMPLOYEE_SEQ',
					keyField: 'em_id', 
					codeField: 'em_code',
					statusField: 'em_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});