Ext.define('erp.view.scm.sale.RenewApply', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'scm/sale/saveRenewApply.action',
				deleteUrl : 'scm/sale/deleteRenewApply.action',
				updateUrl : 'scm/sale/updateRenewApply.action',
				auditUrl : 'scm/sale/auditRenewApply.action',
				resAuditUrl : 'scm/sale/resAuditRenewApply.action',
				submitUrl : 'scm/sale/submitRenewApply.action',
				resSubmitUrl : 'scm/sale/resSubmitRenewApply.action',
				getIdUrl : 'common/getId.action?seq=RENEWAPPLY_SEQ',
				keyField : 'ra_id',
				codeField : 'ra_code',
				statusField : 'ra_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'rad_detno',
				necessaryField : 'rad_prodcode',
				keyField : 'rad_id',
				mainField : 'rad_raid'
			} ]
		});
		me.callParent(arguments);
	}
});