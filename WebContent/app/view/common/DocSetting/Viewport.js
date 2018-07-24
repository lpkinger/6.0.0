Ext.define('erp.view.common.DocSetting.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'common/DocSetting/saveDocSetting.action',
				deleteUrl : 'common/DocSetting/deleteDocSetting.action',
				updateUrl : 'common/DocSetting/updateDocSetting.action',
				auditUrl : 'common/DocSetting/auditDocSetting.action',
				resAuditUrl : 'common/DocSetting/resAuditDocSetting.action',
				submitUrl : 'common/DocSetting/submitDocSetting.action',
				resSubmitUrl : 'common/DocSetting/resSubmitDocSetting.action',
				getIdUrl : 'common/getId.action?seq=docSetting_SEQ',
				keyField : 'ds_id',
				codeField : 'ds_code',
				statusField : 'ds_status',
				statuscodeField : 'ds_statuscode'
			} ]
		});

		me.callParent(arguments);
	}
});