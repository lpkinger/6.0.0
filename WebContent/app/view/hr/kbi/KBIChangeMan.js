Ext.define('erp.view.hr.kbi.KBIChangeMan',{ 
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
					saveUrl: 'hr/kbi/saveKBIChangeMan.action',
					deleteUrl: 'hr/kbi/deleteKBIChangeMan.action',
					updateUrl: 'hr/kbi/updateKBIChangeMan.action',
					getIdUrl: 'common/getId.action?seq=KBIChangeMan_SEQ',
					auditUrl: 'hr/kbi/auditKBIChangeMan.action',
					resAuditUrl: 'hr/kbi/resAuditKBIChangeMan.action',
					submitUrl: 'hr/kbi/submitKBIChangeMan.action',
					resSubmitUrl: 'hr/kbi/resSubmitKBIChangeMan.action',
					keyField: 'ka_id',
					codeField: 'ka_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});