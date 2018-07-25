Ext.define('erp.view.scm.sale.Sale', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'scm/sale/saveSale.action',
				deleteUrl : 'scm/sale/deleteSale.action',
				updateUrl : 'scm/sale/updateSale.action',
				auditUrl : 'scm/sale/auditSale.action',
				resAuditUrl : 'scm/sale/resAuditSale.action',
				submitUrl : 'scm/sale/submitSale.action',
				resSubmitUrl : 'scm/sale/resSubmitSale.action',
				getIdUrl : 'common/getId.action?seq=SALE_SEQ',
				printUrl : 'scm/sale/printSale.action',
				keyField : 'sa_id',
				codeField : 'sa_code',
				statuscodeField : 'sa_statuscode',
				voucherConfig:true
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 50%',
				detno : 'sd_detno',
				necessaryField : 'sd_prodcode',
				keyField : 'sd_id',
				mainField : 'sd_said',
				allowExtraButtons : true
			} ]
		});
		me.callParent(arguments);
	}
});