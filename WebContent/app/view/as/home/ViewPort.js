Ext.define('erp.view.opensys.home.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
    border:false,
	bodyBorder:false,
	padding:'10 10 10 10 ',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(this, { 
			items: [{
				xtype: 'infopanel'
			},{
				xtype: 'problempanel'
			}]
		});
		me.callParent(arguments); 
	}
});