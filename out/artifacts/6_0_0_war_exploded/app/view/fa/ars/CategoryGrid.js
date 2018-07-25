Ext.define('erp.view.fa.ars.CategoryGrid',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'categoryGridViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel2',
					anchor: '100% 95%', 
					/*detno: 'pd_detno',
					necessaryField: 'pd_prodcode',*/
					keyField: 'bdr_id',
				/*	mainField: 'pd_puid'*/
					/*type:'onlySingleGrid'*/
				},{
					xtype: 'erpToolbar2',
				    anchor: '100%', 
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});