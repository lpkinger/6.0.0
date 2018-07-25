Ext.define('erp.view.fa.gla.RemainQuery',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',  
		    	anchor: '100% 14%',
		    	layout: 'hbox',
		    	bodyStyle: 'background: #f1f1f1;',
		    	fieldDefaults: {
		    		margin: '3 2 3 8',
		    		cls: 'form-field-allowBlank'
		    	},
		    	items: [{
		    		 xtype: 'monthdatefield',
		    		 fieldLabel: '期间',
		    		 margin: '3 2 3 0',
		    		 id: 'cmc_yearmonth',
		    		 name: 'cmc_yearmonth'
		    	 },{
		    		 xtype: 'dbfindtrigger',
		    		 fieldLabel: '科目编号',
		    		 id: 'cmc_catecode',
		    		 margin: '3 2 3 0',
		    		 name: 'cmc_catecode'
		    	 },{
		    		 xtype: 'checkbox',
		    		 id: 'chkhaveun',
		    		 name: 'chkhaveun',
		    		 margin: '3 2 3 0',
		    		 boxLabel: '包括未过账凭证',
		    		 cls: ''
		    	  }],
		    	  tbar: [{
		    		  name: 'query',
		    		  id: 'query',
		    		  text: $I18N.common.button.erpQueryButton,
		    		  iconCls: 'x-button-icon-query',
		    		  cls: 'x-btn-gray',
		    		  margin: '0 4 0 0',
		    		  handler: function(){
							
		    		  }
		    	  }, '->', {
		    		  name: 'print',
		    		  text: $I18N.common.button.erpPrintButton,
		    		  iconCls: 'x-button-icon-print',
		    		  margin: '0 4 0 0',
		    		  cls: 'x-btn-gray'
		    	  }, {
		    		  name: 'export',
		    		  text: $I18N.common.button.erpExportButton,
		    		  iconCls: 'x-button-icon-excel',
		    		  cls: 'x-btn-gray',
		    		  margin: '0 4 0 0'
		    	  },{
		    		  xtype: 'erpCloseButton',
		    		  margin: '0 4 0 0'
		    	  }]
		    },{
		    	xtype: 'erpBatchDealGridPanel',  
		    	anchor: '100% 86%',
		    	selModel: Ext.create('Ext.selection.RowModel',{
		    		
		    	})
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});