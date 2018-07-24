Ext.define('erp.view.hr.kpi.KpiBill',{ 
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
					saveUrl: '',
					deleteUrl: '',
					updateUrl: 'hr/kpi/updateKpibill.action',
					auditUrl: '',
					resAuditUrl: '',
					submitUrl: 'hr/kpi/submitKpibill.action',
					resSubmitUrl: 'hr/kpi/resSubmitKpibill.action',
					getIdUrl: 'common/getId.action?seq=KPIBILL_SEQ',
					keyField: 'kb_id',
					codeField: '',
					statusField: 'kb_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'kbd_detno',
					keyField: 'kbd_id',
					mainField: 'kbd_kbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});