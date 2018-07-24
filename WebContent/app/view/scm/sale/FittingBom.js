Ext.define('erp.view.scm.sale.FittingBom', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'scm/sale/saveFittingBom.action',
				deleteUrl : 'scm/sale/deleteFittingBom.action',
				updateUrl : 'scm/sale/updateFittingBom.action',
				auditUrl : 'scm/sale/auditFittingBom.action',
				resAuditUrl : 'scm/sale/resAuditFittingBom.action',
				submitUrl : 'scm/sale/submitFittingBom.action',
				resSubmitUrl : 'scm/sale/resSubmitFittingBom.action',
				getIdUrl : 'common/getId.action?seq=FITTINGBOM_SEQ',
				keyField : 'fb_id',
				codeField : 'fb_code'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'fbd_detno',
				keyField : 'fbd_id',
				mainField : 'fbd_fbid'
			} ]
		});
		me.callParent(arguments);
	}
});