Ext.define('erp.view.scm.qc.CheckItem', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'scm/qc/saveCheckItem.action',
				deleteUrl : 'scm/qc/deleteCheckItem.action',
				updateUrl : 'scm/qc/updateCheckItem.action',
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl : 'common/getId.action?seq=CheckItem_SEQ',
				codeField : 'ci_code',
				keyField : 'ci_id',
				statusField : 'ci_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});