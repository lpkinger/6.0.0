Ext.define('erp.view.common.VisitERP.VendorBomRead', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				keyField : 've_id',
				codeField : 've_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'vbm_id',
				mainField : 'vbm_veid'
			} ]
		});
		me.callParent(arguments);
	}
});