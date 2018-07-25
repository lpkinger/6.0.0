Ext.define('erp.view.scm.sale.TenderInfo',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'gridpanel',
				id:'tenderInfo',
				title : '公共招标信息',
				bodyStyle : 'background-color:#f1f1f1;',
				columnLines : true,
				emptyText: '未查询到结果！', 
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns : {
					defaults:{
						cls : 'x-grid-header-1'
					},
					items:[{
						xtype:'rownumberer',
						align : 'center',
						width : 35
					},{
						header : 'ID',
						dataIndex : 'id',
						hidden : true
					},{
						header: "招标编号", 
						width : 150,
						dataIndex:'code'
					},{
						header: "招标标题", 
						width : 200,
						dataIndex:'title'
					},{
						header: "招标类型", 
						align : 'center',
						width : 80,
						dataIndex:'ifAll',
						renderer:function(val){
							return val==1?'全包':'甲供料';
						}
					},{
						header: "招标方式", 
						align : 'center',
						width : 80,
						dataIndex:'ifOpen',
						renderer:function(val){
							return val==1?'公开招标':'指定招标';
						}
					},{
						header: "招标企业", 
						width : 200,
						dataIndex:'enName'
					},{
						header: "联系人",
						width : 100,
						dataIndex:'user'
					},{
						header: "联系电话", 
						width : 120,
						dataIndex:'userTel'
					},{
						header: "收货地址", 
						width : 300,
						dataIndex:'shipAddress'
					},{
						xtype:'datecolumn',
						header: "投标截止时间", 
						align : 'center',
						width : 100,
						dataIndex:'endDate',
						format:'Y-m-d',
						renderer:function(val){
							if(val){
								return Ext.Date.format(new Date(val),'Y-m-d');
							}
						}
					},{
						xtype:'datecolumn',
						header: "公布结果时间", 
						align : 'center',
						width : 100,
						dataIndex:'publishDate',
						format:'Y-m-d',
						renderer:function(val){
							if(val){
								return Ext.Date.format(new Date(val),'Y-m-d');
							}
						}
					},{
						header: "交易币别",
						align : 'center',
						width : 70,
						dataIndex:'currency',
						id:'currency',
						name:'currency'
					}]
				},
				store : Ext.create('Ext.data.Store', {
					storeId : 'myStore',
					pageSize : pageSize,
					fields : ['id', 'code', 'title', 'ifAll', 'ifOpen','enName', 'user', 'userTel',
							'shipAddress', 'endDate','publishDate','currency','status'],
					autoLoad : true,
					proxy : {
						type : 'ajax',
						url : basePath + 'scm/sale/getTenderList.action',
						reader : {
							type : 'json',
							root : 'content',
							totalProperty : 'total'
						},
						actionMethods: {
				            read   : 'POST'
				        }
					},
					listeners : {
						beforeload : function(store) {
							var grid = Ext.getCmp('tenderInfo');
							var search = Ext.getCmp('search').getValue();
							var date = Ext.getCmp('date').getValue();
							Ext.apply(grid.getStore().proxy.extraParams, {
								search: search,
								date: date
							});
						}
					}
				}),
				dockedItems : [{
					xtype : 'toolbar',
					dock : 'top',
					aligh:'right',
					pack:'right',
					border : false,
					defaults:{
						style:'margin-left:5px;margin-top:5px'
					},
					items : [{
						xtype: 'triggerfield',
						id: 'search',
						fieldLabel:"查询",
						labelWidth:50,
						triggerCls: 'x-form-search-trigger',
						width:200,
						onTriggerClick: function() {
							var trigger = this;
							var grid = trigger.ownerCt.ownerCt;
							grid.store.load();
						}
					},{
						fieldLabel:"日期",
						labelWidth:50,
						fieldStyle:"background:#FFFAFA;color:#515151;",
						width: 350,
						id:'date',
						name:'date',
						xtype:'recondatefield'
					}]
				}, {
					xtype : 'pagingtoolbar',
					dock : 'bottom',
					displayInfo : true,
					store : Ext.data.StoreManager.lookup('myStore'),
					displayMsg:"显示{0}-{1}条数据，共{2}条数据",
					beforePageText: '第',
					afterPageText: '页,共{0}页'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});