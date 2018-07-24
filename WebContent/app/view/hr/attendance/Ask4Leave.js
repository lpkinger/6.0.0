Ext.define('erp.view.hr.attendance.Ask4Leave',{
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
					auditUrl: 'oa/check/auditAsk4Leave.action',
					resAuditUrl: 'oa/check/resAuditAsk4Leave.action',
					submitUrl: 'oa/check/submitVacation.action',
					resSubmitUrl: 'oa/check/resSubmitVacation.action',
					confirmUrl:'oa/check/confirmVacation.action',
					getIdUrl: 'common/getId.action?seq=Vacation_SEQ',
					endUrl:'oa/check/endVacation.action',
					resEndUrl:'oa/check/resEndVacation.action',
					//getEmdays:'oa/check/getEmdays.action',
					printUrl: 'common/printCommon.action',
					keyField: 'va_id',
					codeField: 'va_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});