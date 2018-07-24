Ext.define('erp.view.hr.employee.updateHrOrg',{ 
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
					updateUrl: 'hr/HrOrgStrTree/updateEmployee.action',		
					keyField: 'em_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});