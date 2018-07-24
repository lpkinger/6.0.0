Ext.define('erp.view.fa.ars.BadDebitRate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'badDebitRateViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					/*detno: 'pd_detno',
					necessaryField: 'pd_prodcode',*/
					keyField: 'bdr_id'
				/*	mainField: 'pd_puid'*/
					/*type:'onlySingleGrid'*/
				}/*,{
					xtype: 'erpToolbar2',
				    anchor: '100%', 
				}*/]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});