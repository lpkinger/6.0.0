Ext.define('erp.view.hr.kbi.KBIbill',{ 
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
					anchor: '100% 35%',
					saveUrl: 'hr/kbi/saveKBIbill.action',
					deleteUrl: 'hr/kbi/deleteKBIbill.action',
					updateUrl: 'hr/kbi/updateKBIbill.action',
					getIdUrl: 'common/getId.action?seq=KBIbill_SEQ',
					auditUrl: 'hr/kbi/auditKBIbill.action',
					resAuditUrl: 'hr/kbi/resAuditKBIbill.action',
					submitUrl: 'hr/kbi/submitKBIbill.action',
					resSubmitUrl: 'hr/kbi/resSubmitKBIbill.action',
					keyField: 'kb_id',
					codeField: 'kb_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					keyField: 'kbd_id',
					detno: 'kbd_detno',
					mainField: 'kbd_kbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});