Ext.define('erp.view.fa.gla.DeptCost',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',  
				anchor: '100% 5%',
				layout: 'column',
				bodyStyle: 'background:#f1f1f1;',
				buttonAlign: 'center',
				bbar: ['->',{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpQueryButton,
					iconCls: 'x-button-icon-query',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0'
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0'
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	margin: '0 10 0 0',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]
		    },{
		    	xtype: 'deptcostgrid', 
		    	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		    	anchor: '100% 95%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});