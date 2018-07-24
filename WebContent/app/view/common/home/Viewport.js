Ext.define('erp.view.common.home.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			id: 'home',
			items: [{ 
				region: 'center',
				width: '100%',
				id: 'bench',
				layout: 'column',
				autoScroll: true,
				bodyStyle: 'background: #f1f1f1;',
				defaults: {border:false},
				items: []
			}] 
		});
		me.callParent(arguments); 
	}
});