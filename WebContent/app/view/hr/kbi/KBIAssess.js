Ext.define('erp.view.hr.kbi.KBIAssess',{ 
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
					saveUrl: 'hr/kbi/saveKBIAssess.action',
					deleteUrl: 'hr/kbi/deleteKBIAssess.action',
					updateUrl: 'hr/kbi/updateKBIAssess.action',
					getIdUrl: 'common/getId.action?seq=KBIAssess_SEQ',
					auditUrl: 'hr/kbi/auditKBIAssess.action',
					resAuditUrl: 'hr/kbi/resAuditKBIAssess.action',
					submitUrl: 'hr/kbi/submitKBIAssess.action',
					resSubmitUrl: 'hr/kbi/resSubmitKBIAssess.action',
					keyField: 'ka_id',
					codeField: 'ka_code'
				},{
					xtype: 'erpGridPanel2',
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