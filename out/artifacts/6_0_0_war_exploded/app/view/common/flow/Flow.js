Ext.define('erp.view.common.flow.Flow', {
	extend: 'Ext.Viewport',
	layout: 'auto',
	hideBorders: true,
	autoScroll:true ,
	bodyStyle: {
		background: '#E3E3E3'
	},
	initComponent: function() {
		var me = this; 
		Ext.apply(this,{
		    items:[{
				xtype:'flowheader'
			},{
				xtype:'flowbody'
			},{ 
				xtype:'flowbottom',
				deferLoadData:true
			}]
		});
		me.callParent(arguments); 
	}
});