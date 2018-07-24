Ext.define('erp.view.hr.wage.EmpWageStandard',{
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor',
				items: [{
					xtype: 'EmpTree'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});