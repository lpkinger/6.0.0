Ext.define('erp.view.common.bench.BatchDeal',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				region: 'north',         
				xtype: "erpBatchDealFormPanel",
				_noc:1
		    },{
				region: 'center',         
				xtype: "erpBatchDealGridPanel"
		    }]
		});
		this.callParent(arguments); 
	}
});