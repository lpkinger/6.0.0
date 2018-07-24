Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImport',{
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function() {		
		var me = this;
		Ext.apply(me, {
			items : [{
					region: 'north',         
					xtype: "erpVendorImportFormPanel",  
			    	anchor: '100% 18%'
	    		},{
					region: 'center',         
					xtype: "erpVendorImportGridPanel",  
			    	anchor: '100% 82%'
	   			}]	
			});	
			me.callParent(arguments); 
	}
	
});
