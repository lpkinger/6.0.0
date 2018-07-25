Ext.define('erp.view.fa.arp.VmDetailQuery',{ 
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
				bbar: ['期间:', {
					xtype: 'tbtext',
					id: 'gl_info_ym',
					updateInfo: function(args) {
						this.setText(args.vm_yearmonth.begin.toString().substr(0,4)+ '年第' + 
								args.vm_yearmonth.begin.toString().substr(4,5) + '期 到 ' +
								args.vm_yearmonth.end.toString().substr(0,4)+ '年第' + 
								args.vm_yearmonth.end.toString().substr(4,5) + '期');
					}
				},'-' ,'->',{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpQueryButton,
					iconCls: 'x-button-icon-query',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0',
			    	handler: function(){
						
			    	}
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0'
				},{
					name: 'print',
					text: $I18N.common.button.erpPrintButton,
			    	iconCls: 'x-button-icon-print',
			    	margin: '0 4 0 0',
			    	cls: 'x-btn-gray'
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				},'->']
		    },{
		    	xtype: 'querygrid',  
		    	anchor: '100% 95%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});