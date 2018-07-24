Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImportProd',{
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function() {		
		var me = this;
		Ext.apply(me, {
			items : [{
					region: 'north',         
					xtype: "erpVendorImportProdFormPanel",  
			    	anchor: '100% 20%'
	    		},{
					region: 'center',         
					xtype: "erpVendorImportProdGridPanel",  
			    	anchor: '100% 80%'
	   			}]	
			});	
			me.callParent(arguments); 
	}
	
});
