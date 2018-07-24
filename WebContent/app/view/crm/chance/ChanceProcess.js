Ext.define('erp.view.crm.chance.ChanceProcess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',  
				anchor: '100% 5.5%',
				layout: 'column',
				bodyStyle: 'background:#fff;',
				bbar:{
					/*style:{background:'#fff'},*/
					defaults:{cls:'x-btn-gray'},
					items: [{
						name: 'query',
						id: 'query',
						text: $I18N.common.button.erpQueryButton,
						iconCls: 'x-button-icon-query',
						margin: '0 10 4 0'
					},{
						name: 'export',
						text: $I18N.common.button.erpExportButton,
						iconCls: 'x-button-icon-excel',
						margin: '0 10 4 0'
					},{xtype: 'tbfill',cls:null},{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
						margin: '0 0 4 0',
						handler: function(){
							var main = parent.Ext.getCmp("content-panel"); 
							main.getActiveTab().close();
						}
					}]
				}
		    },{
		    	xtype: 'processgrid',  
		    	anchor: '100% 94.5%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});