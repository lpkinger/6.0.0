Ext.define('erp.view.hr.attendance.AttendDataCom',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
	    		xtype: 'AttendDataCom'
			}]
		}); 
		me.callParent(arguments); 
	} 
});