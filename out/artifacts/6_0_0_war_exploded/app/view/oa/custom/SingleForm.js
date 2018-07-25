Ext.define('erp.view.oa.custom.SingleForm', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'custom/savePage.action?caller=' + caller,
				deleteUrl : 'custom/deletePage.action?caller=' + caller,
				updateUrl : 'custom/updatePage.action?caller=' + caller,
				auditUrl : 'custom/auditPage.action?caller=' + caller,
				resAuditUrl : 'custom/resAuditPage.action?caller=' + caller,
				submitUrl : 'custom/submitPage.action?caller=' + caller,
				bannedUrl : 'custom/bannedPage.action?caller=' + caller,
				printUrl: 'custom/printPage.action?caller=' + caller,
				resBannedUrl : 'custom/resBannedPage.action?caller=' + caller,
				resSubmitUrl : 'custom/resSubmitPage.action?caller=' + caller,
				onConfirmUrl : 'oa/custom/confirm.action?caller=' + caller,
				resConfirmUrl:'oa/custom/resConfirm.action?caller=' + caller,
				getIdUrl : 'common/getId.action?seq=CUSTOMTABLE_SEQ',
				
				submitApprovesUrl:'custom/submitapproves.action?caller=' + caller+'!Confirm',
				resSubmitApprovesUrl:'custom/ressubmitapproves.action?caller=' + caller+'!Confirm',				
				checkUrl : 'custom/aprovePage.action?caller=' + caller+'!Confirm',
				codeField : 'CT_CODE',
				keyField : 'CT_ID'
			} ]
		});
		me.callParent(arguments);
	}
});