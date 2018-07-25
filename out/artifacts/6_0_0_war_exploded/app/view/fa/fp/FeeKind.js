Ext.define('erp.view.fa.fp.FeeKind',{ 
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
					saveUrl: 'fa/fp/saveFeeKind.action',
					deleteUrl: 'fa/fp/deleteFeeKind.action',
					updateUrl: 'fa/fp/updateFeeKind.action',
					auditUrl: 'fa/fp/auditFeeKind.action',
					resAuditUrl: 'fa/fp/resAuditFeeKind.action',
					submitUrl: 'fa/fp/submitFeeKind.action',
					resSubmitUrl: 'fa/fp/resSubmitFeeKind.action',
					printUrl: 'fa/fp/printReceiptFeeKind.action',
					getIdUrl: 'common/getId.action?seq=FeeKind_SEQ',
					keyField: 'fk_id',
					codeField: 'fk_code',
					statusField: 'fk_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});