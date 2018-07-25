Ext.define('erp.view.scm.purchase.PurchaseAcceptNotify',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				codeField: 'sa_code',
				keyField: 'pan_id',
				statusField: 'pan_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'pnd_detno',
				necessaryField: 'pnd_prodcode',
				keyField: 'pnd_id',
				mainField: 'pnd_panid',
				allowExtraButtons:true
			}]
		}); 
		me.callParent(arguments); 
	} 
});