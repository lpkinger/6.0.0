Ext.define('erp.view.hr.kpi.KpibillChange',{ 
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
					anchor: '100% 100%',
					saveUrl: 'hr/kpi/saveKpibillChange.action',
					deleteUrl: 'hr/kpi/deleteKpibillChange.action',
					updateUrl: 'hr/kpi/updateKpibillChange.action',
					getIdUrl: 'common/getId.action?seq=KpibillChange_SEQ',
					auditUrl: 'hr/kpi/auditKpibillChange.action',
					resAuditUrl: 'hr/kpi/resKpibillChange.action',
					submitUrl: 'hr/kpi/submitKpibillChange.action',
					resSubmitUrl: 'hr/kpi/resSubmitKpibillChange.action',
					keyField: 'kbc_id',
					codeField: 'kbc_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});