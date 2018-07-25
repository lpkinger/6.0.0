Ext.define('erp.view.common.AllNavigation',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	cls:'AllNavigation-Viewport',
	bodyStyle:'background:#FFFFFF !important;',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
			    region: 'center', 
			    width : '80%',
				xtype: 'panel',
				id:'descPanel',
				layout : 'fit',
				border:false,
				items:[{xtype:'panel',bodyStyle:'background:#E5E5E5;',
						html:'<div align="center" class="default-panel"><img src="'+basePath+'resource/images/upgrade_default.png"></div>' }]
			},{
			   region: 'west', 
			   width : '20%',
			   xtype:'erpNavigationTreePanel'
			   
			}]
		});
		me.callParent(arguments); 
	}
});