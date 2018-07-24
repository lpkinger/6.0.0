Ext.define('erp.view.hr.attendance.WDSetAndChange',{
	extend: 'Ext.Viewport', 
	layout: 'border',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				height: '35%'
			},{
				region: 'west',
				width: '35%',
				xtype: 'EmpTree2'
			},{
				region: 'center',
//				id: 'centerpanel',
				xtype:'erpGridPanel2'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});