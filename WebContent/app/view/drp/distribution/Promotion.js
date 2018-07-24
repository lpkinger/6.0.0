Ext.define('erp.view.drp.distribution.Promotion',{ 
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
					saveUrl: 'drp/distribution/savePromotion.action',
					deleteUrl: 'drp/distribution/deletePromotion.action',
					updateUrl: 'drp/distribution/updatePromotion.action',
					auditUrl: 'drp/distribution/auditPromotion.action',
					resAuditUrl: 'drp/distribution/resAuditPromotion.action',
					submitUrl: 'drp/distribution/submitPromotion.action',
					resSubmitUrl: 'drp/distribution/resSubmitPromotion.action',
					bannedUrl: 'drp/distribution/bannedPromotion.action',
					resBannedUrl: 'drp/distribution/resBannedPromotion.action',
					getIdUrl: 'common/getId.action?seq=PROMOTION_SEQ',
					keyField: 'pt_id',
					codeField: 'pt_code',
					statusField:'pt_status'
				},{
					xtype:'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ptd_detno',
					keyField: 'ptd_id',
					mainField: 'ptd_ptid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});