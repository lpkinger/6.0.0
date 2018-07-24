Ext.define('erp.view.hr.employee.JobEmployee',{ 
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
					saveUrl: 'hr/employee/saveJobEmployee.action',
					deleteUrl: 'hr/employee/deleteJobEmployee.action',
					updateUrl: 'hr/employee/updateJobEmployee.action',		
					getIdUrl: 'common/getId.action?seq=JobEmployee_SEQ',
					keyField: 'je_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});