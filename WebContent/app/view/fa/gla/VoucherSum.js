Ext.define('erp.view.fa.gla.VoucherSum',{ 
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
			    	margin: '0 5 0 0',
			    	handler: function(){
						
			    	}
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 5 0 0'
				},{
					xtype: 'tbtext',
					id: 'gl_info_ym',
					updateInfo: function(args) {
						if(args.sl_yearmonth) {
							this.setText('期间: 从 ' + args.sl_yearmonth.begin + ' 到 ' + 
									args.sl_yearmonth.end + ' ');
						} else if(args.sl_date){
							this.setText('日期: 从 ' + args.sl_date.begin + ' 到 ' + 
									args.sl_date.end + ' ');
						}
					}
				},'-',{
					xtype: 'tbtext',
					id: 'gl_info_curr',
					updateInfo: function(args) {
						this.setText('币别: ' + (args.sl_currency == '0' ? '本位币' : args.sl_currency));
					}
				},'-','总凭张数:',{
					xtype: 'tbtext',
					id: 'count'
				},'->',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]},
		    	xtype: 'vouchersumdetail',  
		    	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});