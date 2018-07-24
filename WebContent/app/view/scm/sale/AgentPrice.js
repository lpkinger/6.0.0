Ext.define('erp.view.scm.sale.AgentPrice', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 35%',
				saveUrl : 'scm/sale/saveAgentPrice.action',
				deleteUrl : 'scm/sale/deleteAgentPrice.action',
				updateUrl : 'scm/sale/updateAgentPrice.action',
				auditUrl : 'scm/sale/auditAgentPrice.action',
				resAuditUrl : 'scm/sale/resAuditAgentPrice.action',
				submitUrl : 'scm/sale/submitAgentPrice.action',
				resSubmitUrl : 'scm/sale/resSubmitAgentPrice.action',
				getIdUrl : 'common/getId.action?seq=AGENTPRICE_SEQ',
				keyField : 'ap_id',
				codeField : 'ap_code',
				statusField : 'ap_statuscode'
			}, {
				xtype : 'erpGridPanel2',
				anchor : '100% 65%',
				detno : 'apd_detno',
				necessaryField : 'apd_prodcode',
				keyField : 'apd_id',
				mainField : 'apd_apid'
			} ]
		});
		me.callParent(arguments);
	}
});