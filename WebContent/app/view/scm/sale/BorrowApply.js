Ext.define('erp.view.scm.sale.BorrowApply', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'scm/sale/saveBorrowApply.action',
				deleteUrl : 'scm/sale/deleteBorrowApply.action',
				updateUrl : 'scm/sale/updateBorrowApply.action',
				auditUrl : 'scm/sale/auditBorrowApply.action',
				resAuditUrl : 'scm/sale/resAuditBorrowApply.action',
				submitUrl : 'scm/sale/submitBorrowApply.action',
				resSubmitUrl : 'scm/sale/resSubmitBorrowApply.action',
				getIdUrl : 'common/getId.action?seq=BORROWAPPLY_SEQ',
				printUrl:'scm/sale/printBorrowApply.action',
				keyField : 'ba_id',
				codeField : 'ba_code',
				statusField : 'ba_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'bad_detno',
				necessaryField : 'bad_prodcode',
				keyField : 'bad_id',
				mainField : 'bad_baid'
			} ]
		});
		me.callParent(arguments);
	}
});