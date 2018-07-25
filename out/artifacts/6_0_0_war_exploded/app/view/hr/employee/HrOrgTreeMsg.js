Ext.define('erp.view.hr.employee.HrOrgTreeMsg',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					region: 'center',
					width: '100%',
					xtype: 'hrOrgStrTree'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});