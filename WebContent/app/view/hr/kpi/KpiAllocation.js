Ext.define('erp.view.hr.kpi.KpiAllocation',{ 
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
					saveUrl: 'hr/kpi/saveKpiAllocation.action',
					deleteUrl: 'hr/kpi/deleteKpiAllocation.action',
					updateUrl: 'hr/kpi/updateKpiAllocation.action',
					getIdUrl: 'common/getId.action?seq=KpiAllocation_SEQ',
					auditUrl: 'hr/kpi/auditKpiAllocation.action',
					resAuditUrl: 'hr/kpi/resAuditKpiAllocation.action',
					submitUrl: 'hr/kpi/submitKpiAllocation.action',
					resSubmitUrl: 'hr/kpi/resSubmitKpiAllocation.action',
					keyField: 'ka_id',
					codeField: 'ka_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					keyField: 'kad_id',
					detno: 'kad_detno',
					mainField: 'kad_kaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});