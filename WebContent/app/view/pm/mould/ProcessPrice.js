Ext.define('erp.view.pm.mould.ProcessPrice',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProcessPriceViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mould/saveProcessPrice.action',
					deleteUrl: 'pm/mould/deleteProcessPrice.action',
					updateUrl: 'pm/mould/updateProcessPrice.action',
					auditUrl: 'pm/mould/auditProcessPrice.action',
					printUrl: 'pm/mould/printsingleProcessPrice.action',
					resAuditUrl: 'pm/mould/resAuditProcessPrice.action',
					submitUrl: 'pm/mould/submitProcessPrice.action',
					resSubmitUrl: 'pm/mould/resSubmitProcessPrice.action',
					getIdUrl: 'common/getId.action?seq=ProcessPrice_SEQ',
					keyField: 'pp_id',
					statusField: 'p_status',
					statuscodeField: 'pp_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});