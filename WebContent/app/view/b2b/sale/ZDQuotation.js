Ext.define('erp.view.b2b.sale.ZDQuotation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				saveUrl: 'scm/sale/saveZDquotation.action',
				updateUrl: 'scm/sale/updateZDquotation.action',
				deleteUrl: 'scm/sale/deleteZDquotation.action',
				auditUrl: 'scm/sale/auditZDquotation.action',
				resAuditUrl: 'scm/sale/resAuditZDquotation.action',
				submitUrl: 'scm/sale/submitZDquotation.action',
				resSubmitUrl: 'scm/sale/resSubmitZDquotation.action',
				getIdUrl: 'common/getId.action?seq=quotation_SEQ',
				keyField: 'qu_id',
				codeField: 'qu_code',
				statusField: 'qu_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 55%', 
				detno: 'qd_detno',				
				keyField: 'qd_id',
				mainField: 'qd_quid',
				allowExtraButtons: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')
			    , Ext.create('erp.view.b2b.sale.plugin.QuotationReply')],
			}]
		}); 
		me.callParent(arguments); 
	} 
});