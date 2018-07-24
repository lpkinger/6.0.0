Ext.define('erp.view.oa.daily.dailyplan',{ 
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
					saveUrl: 'oa/DailyPlan/saveDailyPlan.action',
					deleteUrl: 'oa/DailyPlan/deleteDailyPlan.action',
					updateUrl: 'oa/DailyPlan/updateDailyPlan.action',
					submitUrl: 'oa/DailyPlan/submitDailyPlan.action',
					auditUrl: 'oa/DailyPlan/auditDailyPlan.action',
					resAuditUrl: 'oa/DailyPlan/resAuditDailyPlan.action',					
					resSubmitUrl: 'oa/DailyPlan/resSubmitDailyPlan.action',
					getIdUrl: 'common/getId.action?seq=DailyPlan_SEQ',
					keyField: 'dp_id',
					codeField: 'dp_code',
					statusField: 'dp_status',
					statuscodeField: 'dp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'dpd_detno',
					keyField: 'dpd_id',
					mainField: 'dpd_dpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});