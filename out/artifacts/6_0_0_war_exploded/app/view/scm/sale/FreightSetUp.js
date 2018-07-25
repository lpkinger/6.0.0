Ext.define('erp.view.scm.sale.FreightSetUp', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/saveFreightSetUp.action',
				deleteUrl : 'scm/sale/deleteFreightSetUp.action',
				updateUrl : 'scm/sale/updateFreightSetUp.action',
				getIdUrl : 'common/getId.action?seq=FREIGHTSETUP_SEQ',
				keyField : 'fs_id',
				codeField : 'fs_code',
			} ]
		});
		me.callParent(arguments);
	}
});