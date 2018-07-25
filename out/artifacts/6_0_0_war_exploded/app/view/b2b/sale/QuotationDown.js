Ext.define('erp.view.b2b.sale.QuotationDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',					
				updateUrl: 'b2b/sale/updateQuotationDown.action',
				auditUrl: 'b2b/sale/auditQuotationDown.action',
				resAuditUrl: 'b2b/sale/resAuditQuotationDown.action',
				printUrl: 'b2b/sale/printQuotationDown.action',
				submitUrl: 'b2b/sale/submitQuotationDown.action',
				resSubmitUrl: 'b2b/sale/resSubmitQuotationDown.action',
				keyField: 'qu_id',
				codeField: 'qu_code',
				statusField: 'qu_status',
				statuscodeField: 'qu_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 				
				keyField: 'qd_id',
				mainField: 'qd_quid',
				allowExtraButtons: true,
				sequenceFn: function(count) {
					var grid = this;
					if(!grid.custLap) {
						var size = grid.store.data.items.length;
						if(size < 10) {
							for(var i = size;i < 10;i++ ) {
								grid.store.add({});
							}
						}
					}
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});