Ext.define('erp.view.hr.attendance.EmpWorkDateModelSet',{
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor',
				items: [{
					xtype: 'EmpTree1'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});