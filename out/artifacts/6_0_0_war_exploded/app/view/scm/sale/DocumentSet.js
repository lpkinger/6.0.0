Ext.define('erp.view.scm.sale.DocumentSet', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				keyField : 'ds_id',
				codeField : 'ds_code'
			} ]
		});
		me.callParent(arguments);
	}
});