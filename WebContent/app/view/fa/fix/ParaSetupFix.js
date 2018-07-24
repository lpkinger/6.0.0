Ext.define('erp.view.fa.fix.ParaSetupFix',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'arbillViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpToolbar2',
				    anchor: '100%', 
				},{
					xtype: 'erpGridPanel',
					anchor: '100% 95%', 
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