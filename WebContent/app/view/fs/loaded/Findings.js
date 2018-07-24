Ext.define('erp.view.fs.loaded.Findings',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				fixedlayout: true,
				updateUrl: 'fs/loaded/updateInvestReport.action?caller='+caller+'&_noc=1'
			}]
		}); 
		this.callParent(arguments); 
	}
});