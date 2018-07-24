Ext.define('erp.view.fa.ars.ShowCmDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',  
				anchor: '100% 13%',
				layout: 'column',
				bodyStyle: 'background:#f1f1f1;',
				buttonAlign: 'center',
				tbar: [{
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
					id:'close',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				},'->'],
				items:[{
					xtype: 'displayfield',
			        fieldLabel: "<font size='4'>客户名称</font>",
			        value:"<font size='4'>"+custname+"</font>",
//			        labelWidth:'50',
			        columnWidth:.38,
			        readOnly:true
				},{
					xtype: 'displayfield',
			        name: 'cm_currency',
			        id: 'cm_currency',
			        fieldLabel: "<font size='4'>币别</font>",
			        value:"<font size='4'>"+currency+"</font>",
			        labelWidth:'50',
			        columnWidth:.15,
			        readOnly:true
				},{
					xtype: 'displayfield',
			        name: 'cm_yearmonth',
			        id: 'cm_yearmonth',
			        labelAlign:'left',
			        labelWidth:'50',
			        fieldLabel: "<font size='4'>期间</font>",
			        value:"<font size='4'>"+yearmonth+"</font>",
			        columnWidth:.15,
			        readOnly:true
				}
				       
				       
		       ]
		    },{
		    	xtype: 'cmdetailgrid',  
		    	anchor: '100% 87%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});