Ext.define('erp.view.plm.budget.FundSubject',{ 
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
					saveUrl: 'plm/budget/saveFundSubject.action',
					deleteUrl:'plm/budget/deleteFundSubject.action',
					updateUrl:'plm/budget/updateFundSubject.action',
					submitUrl:'plm/budget/submitFundSubject.action',
					resSubmitUrl:'plm/budget/resSubmitFundSubject.action',
					auditUrl:'plm/budget/auditFundSubject.action',
					resAuditUrl:'plm/budget/resAuditFundSubject.action',
					getIdUrl:'common/getId.action?seq=FUNDSUBJECT_SEQ',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});