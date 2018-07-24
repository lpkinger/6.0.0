Ext.define('erp.view.fa.ars.SaleKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'saleKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%',  
					/*detno: 'pd_detno',
					necessaryField: 'pd_prodcode',*/
					keyField: 'sk_id',
				/*	mainField: 'pd_puid'*/
					/*type:'onlySingleGrid'*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});