Ext.define('erp.view.fa.gs.ArDayQuery',{ 
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
			    	margin: '0'
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
				},{
					xtype: 'tbtext',
					id: 'gl_info_ym',
					updateInfo: function(args) {
						this.setText('日期: 从 ' + args.am_date.begin + ' 到 ' + 
								args.am_date.end + ' ');
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
		    	xtype: 'ardaydetail',  
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});