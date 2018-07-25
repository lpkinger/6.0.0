Ext.define('erp.view.fa.fp.DelegationLetter',{ 
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
					anchor: '100% 100%',
					saveUrl: 'fa/fp/saveDelegationLetter.action',
					deleteUrl: 'fa/fp/deleteDelegationLetter.action',
					updateUrl: 'fa/fp/updateDelegationLetter.action',
					submitUrl: 'fa/fp/submitDelegationLetter.action',
					resSubmitUrl: 'fa/fp/resSubmitDelegationLetter.action',
					auditUrl: 'fa/fp/auditDelegationLetter.action',
					resAuditUrl: 'fa/fp/resAuditDelegationLetter.action',
					endUrl: 'fa/fp/endDelegationLetter.action',
					resEndUrl: 'fa/fp/resEndDelegationLetter.action',
					printUrl: 'fa/fp/printReceiptDelegationLetter.action',
					getIdUrl: 'common/getId.action?seq=DelegationLetter_SEQ',
					keyField: 'dgl_id',
					codeField: 'dgl_code',
					statusField: 'dgl_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});