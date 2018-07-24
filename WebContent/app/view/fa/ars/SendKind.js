Ext.define('erp.view.fa.ars.SendKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'sendKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%',  
					updateUrl: '/fa/ars/updateSendKind.action',
					getIdUrl: 'common/getId.action?seq=SENDKIND_SEQ',
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