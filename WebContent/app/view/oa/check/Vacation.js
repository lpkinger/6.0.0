Ext.define('erp.view.oa.check.Vacation',{ 
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
					saveUrl: 'oa/check/saveVacation.action',
					deleteUrl: 'oa/check/deleteVacation.action',
					updateUrl: 'oa/check/updateVacation.action',
					auditUrl: 'oa/check/auditVacation.action',
					resAuditUrl: 'oa/check/resAuditVacation.action',
					submitUrl: 'oa/check/submitVacation.action',
					resSubmitUrl: 'oa/check/resSubmitVacation.action',
					getIdUrl: 'common/getId.action?seq=Vacation_SEQ',
					keyField: 'va_id',
					codeField: 'va_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});