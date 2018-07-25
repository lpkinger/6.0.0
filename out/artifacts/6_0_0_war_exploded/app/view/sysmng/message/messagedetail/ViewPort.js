Ext.define('erp.view.sysmng.message.messagedetail.ViewPort',{ 
	extend: 'Ext.Viewport', 
	id:'viewport',
	layout: 'anchor', 
	border: false,
	bodyBorder:false,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [{
				xtype:'MessageForm',
				anchor: '100% 40%',

			},
			{
				xtype:'MessageGrid',
				anchor: '100% 60%'
			}
			]
		});
		me.callParent(arguments); 
	}
});