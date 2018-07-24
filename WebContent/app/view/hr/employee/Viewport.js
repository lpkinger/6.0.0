Ext.define('erp.view.hr.employee.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'employeeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',anchor: '100% 100%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});