Ext.define('erp.view.fs.cust.FinancApplyProgress',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	bodyStyle : 'background:#ffffff',
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{ 
				xtype:'financapplynavition'
			}] 
		}); 
		this.callParent(arguments); 
	} 
});