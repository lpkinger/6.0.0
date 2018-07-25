Ext.define('erp.view.hr.kpi.KpiPeriods',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'hr/kpi/saveKpiPeriods.action',
					deleteUrl: 'hr/kpi/deleteKpiPeriods.action',
					updateUrl: 'hr/kpi/updateKpiPeriods.action',
					getIdUrl: 'common/getId.action?seq=KpiPeriods_SEQ',
					auditUrl: 'hr/kpi/auditKpiPeriods.action',
					resAuditUrl: 'hr/kpi/resAuditKpiPeriods.action',
					submitUrl: 'hr/kpi/submitKpiPeriods.action',
					resSubmitUrl: 'hr/kpi/resSubmitKpiPeriods.action',
					keyField: 'pe_id',
					codeField: 'pe_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					keyField: 'pd_id',
					detno: 'pd_pdno',
					mainField: 'pd_peid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});