Ext.define('erp.view.ma.ObjectExplain',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					xtype: 'erpObjectExplainFormPanel',
					anchor:'100% 100%',
					saveUrl:'ma/objectexplain/save.action'
				}]
		}); 
		me.callParent(arguments); 
	} 
});