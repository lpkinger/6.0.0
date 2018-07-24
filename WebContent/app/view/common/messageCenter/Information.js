Ext.define('erp.view.common.messageCenter.Information',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', //fit
	hideBorders: true, 
	id:'information',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region:'north',
				xtype:'erpInformationForm',
				width:'100%',
				height: 120
			},{
				region:'center',
				xtype:'InformationGrid',
				width:'100%',
				style:'border:none!important'
			}]
		}); 
		me.callParent(arguments); 
	} 
});