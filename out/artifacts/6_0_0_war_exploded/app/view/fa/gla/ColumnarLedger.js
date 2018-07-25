Ext.define('erp.view.fa.gla.ColumnarLedger',{ 
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
					name: 'first',
					id: 'first',
					text: $I18N.common.tip.first,
					iconCls: 'first',
			    	cls: 'x-btn-gray',
			    	margin: '0 0 0 5',
			    	hidden: true
				},{
					name: 'prev',
					id: 'prev',
					text: $I18N.common.tip.prev,
					iconCls: 'prev',
			    	cls: 'x-btn-gray',
			    	margin: '0 0 0 5',
			    	hidden: true
				},{
					name: 'next',
					id: 'next',
					text: $I18N.common.tip.next,
					iconCls: 'next',
			    	cls: 'x-btn-gray',
			    	margin: '0 0 0 5',
			    	hidden: true
				},{
					name: 'end',
					id: 'end',
					text: $I18N.common.tip.end,
					iconCls: 'end',
			    	cls: 'x-btn-gray',
			    	margin: '0 0 0 5',
			    	hidden: true
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 5 0 5'
				},'期间:', {
					xtype: 'tbtext',
					id: 'gl_info_ym',
					updateInfo: function(args) {
						this.setText(args.sl_yearmonth.begin.toString().substr(0,4)+ '年第' + 
								args.sl_yearmonth.begin.toString().substr(4,5) + '期 到 ' +
								args.sl_yearmonth.end.toString().substr(0,4)+ '年第' + 
								args.sl_yearmonth.end.toString().substr(4,5) + '期');
					}
				},'-', {
					xtype: 'tbtext',
					id: 'gl_info_ass'
				} ,'->',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]},
		    	xtype: 'columnarledgerdetail',  
		    	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});