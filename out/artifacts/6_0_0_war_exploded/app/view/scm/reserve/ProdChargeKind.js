Ext.define('erp.view.scm.reserve.ProdChargeKind', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/reserve/saveProdChargeKind.action',
				deleteUrl: 'scm/reserve/deleteProdChargeKind.action',
				updateUrl: 'scm/reserve/updateProdChargeKind.action',
				auditUrl: 'scm/reserve/auditProdChargeKind.action',
				resAuditUrl: 'scm/reserve/resAuditProdChargeKind.action',
				submitUrl: 'scm/reserve/submitProdChargeKind.action',
				resSubmitUrl: 'scm/reserve/resSubmitProdChargeKind.action',
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=PRODCHARGEKIND_SEQ',
				keyField: 'pck_id',
				statusField: 'pck_status',
				statuscodeField: 'pck_statuscode',
				codeField: 'pck_code'
			}]
		});
		me.callParent(arguments);
	}
});