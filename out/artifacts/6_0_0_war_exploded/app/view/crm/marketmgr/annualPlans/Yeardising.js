Ext.define('erp.view.crm.marketmgr.annualPlans.Yeardising',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'Merchandising', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'crm/plan/savePlanDising.action?caller=' +caller,
					deleteUrl: 'crm/plan/deletePlanDising.action?caller=' +caller,
					updateUrl: 'crm/plan/updatePlanDising.action?caller=' +caller,
					submitUrl: 'crm/plan/submitPlanDising.action?caller='+caller,
					resSubmitUrl:'crm/plan/resSubmitPlanDising.action?caller='+caller,
					auditUrl:'crm/plan/auditPlanDising.action',
					resAuditUrl:'crm/plan/resAuditPlanDising.action',
					getIdUrl: 'common/getId.action?seq=MERCHANDISING_SEQ',
					keyField: 'mh_id',
					codeField: 'mh_code',
					statusField: 'mh_statuscode'
				},
				{
					xtype: 'erpYearDesingGrid',
					anchor: '100% 70%', 
					detno: 'mhd_detno',
					necessaryField: 'mhd_prodcode',
					keyField: 'mhd_id',
					mainField: 'mhd_mhid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});