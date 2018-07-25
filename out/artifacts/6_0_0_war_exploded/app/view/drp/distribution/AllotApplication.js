Ext.define('erp.view.drp.distribution.AllotApplication',{ 
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
					saveUrl: 'drp/distribution/saveAllotApplication.action',
					deleteUrl: 'drp/distribution/deleteAllotApplication.action',
					updateUrl: 'drp/distribution/updateAllotApplication.action',
					auditUrl: 'drp/distribution/auditAllotApplication.action',
					resAuditUrl: 'drp/distribution/resAuditAllotApplication.action',
					submitUrl: 'drp/distribution/submitAllotApplication.action',
					resSubmitUrl: 'drp/distribution/resSubmitAllotApplication.action',
					bannedUrl: 'drp/distribution/bannedAllotApplication.action',
					resBannedUrl: 'drp/distribution/resBannedAllotApplication.action',
					getIdUrl: 'common/getId.action?seq=ALLOTAPPLICATION_SEQ',
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