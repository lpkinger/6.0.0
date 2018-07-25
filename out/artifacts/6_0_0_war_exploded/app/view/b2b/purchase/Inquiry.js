Ext.define('erp.view.b2b.purchase.Inquiry',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'InquirySaleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					keyField: 'in_id',
					codeField: 'in_code',
					statusField: 'in_status',
					statuscodeField: 'in_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'id_detno',
					necessaryField: 'id_prodcode',
					keyField: 'id_id',
					mainField: 'id_inid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});