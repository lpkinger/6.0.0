Ext.define('erp.view.opensys.commonpage.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		 items:[{
			 xtype:'erpFormPanel2'
		 }]
		});
		me.callParent(arguments); 
	}
});