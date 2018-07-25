Ext.define('erp.view.common.VisitERP.CustomerAccountMaintain', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				updateUrl : 'common/VisitERP/updateCAM.action',
				getIdUrl : 'common/getId.action?seq=CLIENTCONTRAST_SEQ',
				keyField : 've_id',
				codeField : 've_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				keyField : 'cc_id',
				mainField : 'cc_veid'
			} ]
		});
		me.callParent(arguments);
	}
});