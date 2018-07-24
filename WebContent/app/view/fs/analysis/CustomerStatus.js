Ext.define('erp.view.fs.analysis.CustomerStatus',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				anchor: '100% 10%',
				xtype: "erpFormPanel", 
				enableTools: false,
				getItemsAndButtons:function(){
					Ext.apply(this,{
						items : [{
							xtype: 'textfield',
							fieldLabel: '客户编号',		
							columnWidth: 0.25,
							id: 'custcode',
							name: 'custcode',
							hidden: true
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '客户名称',		
							columnWidth: 0.25,
							dbCaller: 'CustomerInfor',
							id: 'custname',
							name: 'custname'
						},{
							xtype: 'button',
							text: '搜索',
							style:'left:20px;',
							width:80,
							id:'search'
						}]
					});
				}
		    },{
		    	xtype : 'tabpanel',
		    	anchor: '100% 90%',
		    	items : [{
				title:'贷前业务',
				id: 'beforeload',
				xtype : 'erpGridPanel2',
				condition:"status='贷前'",
				autoScroll:false,
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'业务详情汇总-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')]
			},{
				title:'贷中业务',
				id: 'loading',
				xtype : 'erpGridPanel2',
				condition:"status='贷中'",
				autoScroll:false,
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'业务详情汇总-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				bbar: {xtype: 'erpToolbar',id:'toolbar1'}
			},{
				title:'贷后业务',
				id: 'loaded',
				xtype : 'erpGridPanel2',
				condition:"status='贷后'",
				autoScroll:false,
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'业务详情汇总-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				bbar: {xtype: 'erpToolbar',id:'toolbar2'}
			},{
				title:'已完结业务',
				id: 'loadend',
				xtype : 'erpGridPanel2',
				condition:"status='已完结'",
				autoScroll:false,
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'业务详情汇总-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				bbar: {xtype: 'erpToolbar',id:'toolbar3'}
			}]
		    }]
		});
		this.callParent(arguments); 
	}
});