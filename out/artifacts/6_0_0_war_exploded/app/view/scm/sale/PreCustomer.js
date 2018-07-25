Ext.define('erp.view.scm.sale.PreCustomer', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/sale/savePreCustomer.action',
				deleteUrl : 'scm/sale/deletePreCustomer.action',
				updateUrl : 'scm/sale/updatePreCustomer.action',
				auditUrl : 'scm/sale/auditPreCustomer.action',
				resAuditUrl : 'scm/sale/resAuditPreCustomer.action',
				submitUrl : 'scm/sale/submitPreCustomer.action',
				resSubmitUrl : 'scm/sale/resSubmitPreCustomer.action',
				bannedUrl : 'scm/sale/bannedPreCustomer.action',
				resBannedUrl : 'scm/sale/resBannedPreCustomer.action',
				getIdUrl : 'common/getId.action?seq=PRECUSTOMER_SEQ',
				keyField : 'cu_id',
				codeField : 'cu_code',
				statusField : 'cu_auditstatuscode'
			} ]
		});
		me.callParent(arguments);
	}
});