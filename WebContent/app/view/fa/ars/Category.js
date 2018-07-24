Ext.define('erp.view.fa.ars.Category',{ 
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
					saveUrl: 'fa/ars/saveCategoryBase.action',
					deleteUrl: 'fa/ars/deleteCategoryBase.action',
					updateUrl: 'fa/ars/updateCategoryBase.action',
					auditUrl: 'fa/ars/auditCategoryBase.action',
					resAuditUrl: 'fa/ars/resAuditCategoryBase.action',
					submitUrl: 'fa/ars/submitCategoryBase.action',
					resSubmitUrl: 'fa/ars/resSubmitCategoryBase.action',
					bannedUrl: 'fa/ars/bannedCategoryBase.action',
					resBannedUrl: 'fa/ars/resBannedCategoryBase.action',
					getIdUrl: 'common/getId.action?seq=CATEGORY_SEQ',
					keyField: 'ca_id',	
					codeField:'ca_code',
					statuscodeField: 'ca_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});