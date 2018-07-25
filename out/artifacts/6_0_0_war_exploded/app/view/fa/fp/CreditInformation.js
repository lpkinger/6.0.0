Ext.define('erp.view.fa.fp.CreditInformation',{ 
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
					saveUrl: 'fa/fp/saveCreditInformation.action',
					deleteUrl: 'fa/fp/deleteCreditInformation.action',
					updateUrl: 'fa/fp/updateCreditInformation.action',
					auditUrl: 'fa/fp/auditCreditInformation.action',
					resAuditUrl: 'fa/fp/resAuditCreditInformation.action',
					submitUrl: 'fa/fp/submitCreditInformation.action',
					resSubmitUrl: 'fa/fp/resSubmitCreditInformation.action',
					getIdUrl: 'common/getId.action?seq=CreditInformation_SEQ',
					keyField: 'ci_id',	
					codeField: 'ci_no',
					statusField: 'ci_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
