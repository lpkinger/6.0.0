Ext.define('erp.view.oa.attention.AttentionManageDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	AttentionFunction: Ext.create('erp.view.oa.attention.AttentionFunction'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	initComponent : function(){
    var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'home', 
				layout: 'border', 
				items: [{
					region: 'center',
					width: '100%',
					id: 'bench',
					layout: 'column',
					autoScroll: true,
					bodyStyle: 'background: #CDC8B1;',
					defaults: {frame:true},
					items: []
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});