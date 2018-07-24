Ext.define('erp.view.fa.fp.BadDebtsAudit',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					anchor: '100% 65%',
					xtype: 'erpFormPanel',
					saveUrl: 'fa/fp/saveBadDebtsAudit.action',
					deleteUrl: 'fa/fp/deleteBadDebtsAudit.action',
					updateUrl: 'fa/fp/updateBadDebtsAudit.action',
					auditUrl: 'fa/fp/auditBadDebtsAudit.action',
					resAuditUrl: 'fa/fp/resAuditBadDebtsAudit.action',
					submitUrl: 'fa/fp/submitBadDebtsAudit.action',
					resSubmitUrl: 'fa/fp/resSubmitBadDebtsAudit.action',
					printUrl: 'fa/fp/printBadDebtsAudit.action',
					getIdUrl: 'common/getId.action?seq=BADDEBTSAUDIT_SEQ',
					keyField: 'bda_id',	
					codeField: 'bda_code',
					statusField: 'bda_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%', 
					keyField: 'bdad_id',
					detno: 'bdad_detno',
					mainField: 'bdad_bdaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});