Ext.define('erp.view.fa.gla.GeneralLedgerSingle',{ 
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
			    	cls: 'x-btn-gray',
			    	handler: function(){
						
			    	}
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 0 0 5'
				},{
					name: 'print',
					text: $I18N.common.button.erpPrintButton,
			    	iconCls: 'x-button-icon-print',
			    	margin: '0 5 0 5',
			    	cls: 'x-btn-gray'
				},'期间:', {
					xtype: 'tbtext',
					id: 'gl_info_ym',
					updateInfo: function(args) {
						this.setText(args.cm_yearmonth.begin.toString().substr(0,4)+ '年第' + 
								args.cm_yearmonth.begin.toString().substr(4,5) + '期 到 ' +
								args.cm_yearmonth.end.toString().substr(0,4)+ '年第' + 
								args.cm_yearmonth.end.toString().substr(4,5) + '期');
					}
				},'->',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]},
		    	xtype: 'ledgerSingle',  
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});