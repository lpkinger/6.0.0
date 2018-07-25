Ext.define('erp.view.fs.credit.CustCreditQuery',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
			var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpCustCreditFormPanel",  
	    	anchor: '100% 15%'
	    },{
			region: 'south',         
			xtype: "erpCustCreditGridPanel",  
	    	anchor: '100% 85%'
	    }]
		});
		this.callParent(arguments); 
	}
});