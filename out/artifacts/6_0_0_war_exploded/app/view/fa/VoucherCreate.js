Ext.define('erp.view.fa.VoucherCreate', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				anchor : '100% 23%',
				fixedlayout : true,
				xtype : 'erpFormPanel'
			}, {
				anchor : '100% 77%',
				xtype : 'panel',
				id : 'vc-panel',
				layout : 'anchor'
			} ]
		});
		me.callParent(arguments);
	}
});