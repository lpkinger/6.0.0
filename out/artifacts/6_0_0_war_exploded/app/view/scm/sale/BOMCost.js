Ext.define('erp.view.scm.sale.BOMCost', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 44%',
				saveUrl : 'scm/sale/BOMCost/saveBOMCost.action',
				deleteUrl : 'scm/sale/BOMCost/deleteBOMCost.action',
				updateUrl : 'scm/sale/BOMCost/updateBOMCost.action',
				auditUrl : 'scm/sale/BOMCost/auditBOMCost.action',
				resAuditUrl : 'scm/sale/BOMCost/resAuditBOMCost.action',
				submitUrl : 'scm/sale/BOMCost/submitBOMCost.action',
				resSubmitUrl : 'scm/sale/BOMCost/resSubmitBOMCost.action',
				bannedUrl : 'scm/sale/BOMCost/bannedBOMCost.action',
				resBannedUrl : 'scm/sale/BOMCost/resBannedBOMCost.action',
				printUrl : 'scm/sale/BOMCost/printBOMCost.action',
				getIdUrl : 'common/getId.action?seq=BOMCOST_SEQ',
				keyField : 'bc_id',
				codeField : 'bc_code',
				statusField : 'bc_checkstatus',
				statusCodeField : 'bc_checkstatuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 56%',
				detno : 'bcd_detno',
				necessaryField : 'bcd_prodcode',
				keyField : 'bcd_id',
				mainField : 'bcd_evid',
				cls : 'custom-grid'
			} ]
		});
		me.callParent(arguments);
	}
});