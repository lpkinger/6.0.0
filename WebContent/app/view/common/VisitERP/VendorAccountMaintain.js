Ext.define('erp.view.common.VisitERP.VendorAccountMaintain', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				updateUrl : 'common/VisitERP/updateVAM.action',
				getIdUrl : 'common/getId.action?seq=VENDORCONTRAST_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'vc_id',
				mainField : 'vc_cuid'
			} ]
		});
		me.callParent(arguments);
	}
});