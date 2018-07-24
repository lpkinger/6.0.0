Ext.define('erp.view.hr.kpi.KpiApply',{ 
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
					anchor: '100% 50%',
					saveUrl: 'hr/kpi/saveKpiApply.action',
					deleteUrl: 'hr/kpi/deleteKpiApply.action',
					updateUrl: 'hr/kpi/updateKpiApply.action',
					getIdUrl: 'common/getId.action?seq=KPIAPPLY_SEQ',
					auditUrl: 'hr/kpi/auditKpiApply.action',
					//resAuditUrl: '',
					submitUrl: 'hr/kpi/submitKpiApply.action',
					resSubmitUrl: 'hr/kpi/resSubmitKpiApply.action',
					keyField: 'ka_id',
					codeField: 'ka_code'
				},{
					xtype: 'erpApplyGrid',
					anchor: '100% 50%', 
					keyField: 'kad_id',
					detno: 'kad_detno',
					mainField: 'kad_kaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});