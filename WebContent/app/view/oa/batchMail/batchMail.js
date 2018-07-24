Ext.define('erp.view.oa.batchMail.batchMail',{
	extend: 'Ext.Viewport',
	layout: 'fit',
	initComponent: function(){
		var me = this;
		Ext.apply(me, {
			items: [{
				id: 'view_batchMail',
				layout: 'border',
				items: [{
					xtype: 'erpMailSelectPanel',
					region: 'west',
					width: '30%'
				},{
					xtype: 'erpMailContentPanel',
					region: 'center'
				}]
			}]
		});
		me.callParent(arguments); 
	}
});