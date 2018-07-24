Ext.define('erp.view.scm.purchase.VendorBank', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 20%',
				updateUrl : 'scm/purchase/updateVendorBank.action',
				getIdUrl : 'common/getId.action?seq=VENDOR_SEQ',
				keyField : 've_id',
				codeField : 've_code',
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 80%',
				keyField : 'vpd_id',
				mainField : 'vpd_vpid',
				detno : 'vpd_detno',
				autoSetSequence : true,
				necessaryField : 'vpd_bank'
			} ]
		});
		me.callParent(arguments);
	}
});