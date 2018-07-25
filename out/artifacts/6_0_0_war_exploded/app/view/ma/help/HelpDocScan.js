Ext.define('erp.view.ma.help.HelpDocScan',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items:[{
				xtype : 'erpTreePanel',
				dockedItems : null,
				region : 'west',
				width : '28%',
				height : '100%',
				maxWidth:300,
				useArrows: false
			},{
				xtype : 'panel',
				title:'帮助文档',
				id:'doc-panel',
				layout:'fit',
				region : 'center'				
			} ]
		}); 
		me.callParent(arguments); 
	} 
});