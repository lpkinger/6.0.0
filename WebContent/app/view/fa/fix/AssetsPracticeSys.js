Ext.define('erp.view.fa.fix.AssetsPracticeSys',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'assetsPracticeSysViewport', 
				layout: 'anchor', 
				items: [/*{
					xtype: 'erpToolbar3',
				    anchor: '100% 7%', 
				},*/{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					/*detno: 'pd_detno',
					necessaryField: 'pd_prodcode',*/
					keyField: 'ps_id',
				/*	mainField: 'pd_puid'*/
					/*type:'onlySingleGrid'*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});