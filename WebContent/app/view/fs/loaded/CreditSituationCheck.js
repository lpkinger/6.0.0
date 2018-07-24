Ext.define('erp.view.fs.loaded.CreditSituationCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				updateUrl: 'fs/loaded/updateInvestReport.action?caller='+caller+'&_noc=1'
			}]
		}); 
		this.callParent(arguments); 
	}
});