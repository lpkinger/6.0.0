Ext.define('erp.view.common.mailTemp.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'common/mailTemp/saveMailTemp.action',
				deleteUrl : 'common/mailTemp/deleteMailTemp.action',
				updateUrl : 'common/mailTemp/updateMailTemp.action',
				auditUrl : 'common/mailTemp/auditMailTemp.action',
				resAuditUrl : 'common/mailTemp/resAuditMailTemp.action',
				submitUrl : 'common/mailTemp/submitMailTemp.action',
				resSubmitUrl : 'common/mailTemp/resSubmitMailTemp.action',
				getIdUrl : 'common/getId.action?seq=mailTemp_SEQ',
				keyField : 'mt_id',
				codeField : 'mt_code',
				statusField : 'mt_status',
				statuscodeField : 'mt_statuscode'
			} ]
		});

		me.callParent(arguments);
	}
});