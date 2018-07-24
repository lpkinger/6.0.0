Ext.define('erp.view.fa.fp.DebitInformation',{ 
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
					saveUrl: 'fa/fp/saveDebitInformation.action',
					deleteUrl: 'fa/fp/deleteDebitInformation.action',
					updateUrl: 'fa/fp/updateDebitInformation.action',
					auditUrl: 'fa/fp/auditDebitInformation.action',
					resAuditUrl: 'fa/fp/resAuditDebitInformation.action',
					submitUrl: 'fa/fp/submitDebitInformation.action',
					resSubmitUrl: 'fa/fp/resSubmitDebitInformation.action',
					getIdUrl: 'common/getId.action?seq=DebitInformation_SEQ',
					keyField: 'di_id',	
					codeField: 'di_no',
					statusField: 'di_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
