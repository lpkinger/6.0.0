Ext.define('erp.view.common.VisitERP.BomVendorRead', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				keyField : 'bo_id',
				codeField : 'bo_mothercode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'vbm_id',
				mainField : 'vbm_bomid'
			} ]
		});
		me.callParent(arguments);
	}
});