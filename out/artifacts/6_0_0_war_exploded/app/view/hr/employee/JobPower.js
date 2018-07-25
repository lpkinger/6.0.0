Ext.define('erp.view.hr.employee.JobPower',{ 
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
					anchor: '100% 24%',
					keyField: 'jo_id'
				},{
					xtype: 'erpJobPowerTreeGrid',
					anchor: '100% 76%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});