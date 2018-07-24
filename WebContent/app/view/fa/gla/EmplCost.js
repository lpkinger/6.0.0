Ext.define('erp.view.fa.gla.EmplCost',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				tbar: {padding:'0 0 5 0',items:[{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpQueryButton,
					iconCls: 'x-button-icon-query',
			    	cls: 'x-btn-gray'
				},{
					margin:'0 0 0 5',
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray'
				},'->',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	margin: '0 10 0 0',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
		    	}]},
		    	xtype: 'emplcostgrid',
		    	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});