Ext.define('erp.view.drp.distribution.AllotApplicationCu',{ 
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
					anchor: '100% 50%',
					saveUrl: 'drp/distribution/saveAllotApplicationCu.action',
					deleteUrl: 'drp/distribution/deleteAllotApplicationCu.action',
					updateUrl: 'drp/distribution/updateAllotApplicationCu.action',
					auditUrl: 'drp/distribution/auditAllotApplicationCu.action',
					resAuditUrl: 'drp/distribution/resAuditAllotApplicationCu.action',
					submitUrl: 'drp/distribution/submitAllotApplicationCu.action',
					resSubmitUrl: 'drp/distribution/resSubmitAllotApplicationCu.action',
					bannedUrl: 'drp/distribution/bannedAllotApplicationCu.action',
					resBannedUrl: 'drp/distribution/resBannedAllotApplicationCu.action',
					getIdUrl: 'common/getId.action?seq=ALLOTAPPLICATIONCU_SEQ',
					keyField: 'aa_id',
					codeField: 'aa_code',
					statusField:'aa_status'
				},{
					xtype:'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_aaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});