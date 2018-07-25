Ext.define('erp.view.oa.custom.MainDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 55%',
					saveUrl: 'custom/savePage.action?caller=' +caller,
					deleteUrl: 'custom/deletePage.action?caller=' +caller,
					updateUrl: 'custom/updatePage.action?caller=' +caller,
					auditUrl: 'custom/auditPage.action?caller=' +caller,
					resAuditUrl: 'custom/resAuditPage.action?caller=' +caller,
					submitUrl: 'custom/submitPage.action?caller=' +caller,
					bannedUrl: 'custom/bannedPage.action?caller='+caller,
					printUrl: 'custom/printPage.action?caller=' + caller,
					resBannedUrl: 'custom/resBannedPage.action?caller='+caller,
					resSubmitUrl: 'custom/resSubmitPage.action?caller=' +caller,
					onConfirmUrl : 'oa/custom/confirm.action?caller=' + caller,
					resConfirmUrl:'oa/custom/resConfirm.action?caller=' + caller,
					endUrl: 'custom/endPage.action?caller='+caller,
					resEndUrl: 'custom/resEndPage.action?caller='+caller,					
					getIdUrl: 'common/getId.action?seq=CUSTOMTABLE_SEQ',
					keyField: 'CT_ID',
					codeField: 'CT_CODE',
					statusField: 'CT_STATUS'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%', 
					allowExtraButtons: true,
					detno: 'CD_DETNO',
					keyField: 'CD_ID',
					mainField: 'CD_CTID'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});