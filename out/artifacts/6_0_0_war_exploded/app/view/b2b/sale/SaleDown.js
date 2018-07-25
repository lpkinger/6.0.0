Ext.define('erp.view.b2b.sale.SaleDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',	
				updateUrl: 'b2b/sale/updateSaleDown.action',
				replyUrl: 'b2b/sale/replySaleDown.action',
				keyField: 'sa_id',
				codeField: 'sa_code',
				statusField: 'sa_status',
				statuscodeField: 'sa_statuscode',
				focusFirst:false
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'sd_detno',
				keyField: 'sd_id',
				mainField: 'sd_said',
				allowExtraButtons: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')
			    , Ext.create('erp.view.b2b.sale.plugin.Reply')],
			    NoAdd:true
			}]
		}); 
		me.callParent(arguments); 
	} 
});