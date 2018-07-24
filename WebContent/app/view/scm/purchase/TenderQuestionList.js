Ext.define('erp.view.scm.purchase.TenderQuestionList',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'gridpanel',
				id:'tenderquestionlist',
				title : '提问答疑列表',
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
						header: "提问编号", 
						width : 120,
						dataIndex:'code'
					},{
						header: "提问单位", 
						width : 200,
						dataIndex:'vendName'
					},{
						header: "提问主题", 
						width : 250,
						dataIndex:'topic'
					},{
						xtype:'datecolumn',
						header: "提问截止时间",
						align : 'center',
						width : 150,
						dataIndex:'questionEndDate',
						format:'Y-m-d H:i:s',
						renderer:function(val){
							if(val){
								return Ext.Date.format(new Date(val),'Y-m-d H:i:s');
							}
						}
					},{
						xtype:'datecolumn',
						header: "提问时间",
						align : 'center',
						width : 100,
						dataIndex:'inDate',
						format:'Y-m-d',
						renderer:function(val){
							if(val){
								return Ext.Date.format(new Date(val),'Y-m-d');
							}
						}
					},{
						header: "招标ID", 
						hidden: true,
						width : 200,
						dataIndex:'tenderId'
					},{
						header: "招标编号",
						width : 130,
						dataIndex:'tenderCode',
						renderer:function(val, meta, record, x, y, store, view){
						 	meta.style="padding-right:0px!important";
						 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
						 	if(val){
						 		return  '<a href="javascript:openUrl2(\'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS' + record.data['tenderId'] + '\',\'评标单('+Ext.String.htmlEncode(val)+'\')">' + Ext.String.htmlEncode(val) + '</a>';
						 	}
						 	return '';
					 	}
					},{
						header: "招标标题", 
						width : 250,
						dataIndex:'tenderTitle'
					},{
						xtype:'datecolumn',
						header: "回复时间",
						align : 'center',
						width : 150,
						dataIndex:'replyDate',
						format:'Y-m-d H:i:s',
						renderer:function(val){
							if(val){
								return Ext.Date.format(new Date(val),'Y-m-d H:i:s');
							}
						}
					},{
				        header: '回复状态',
				        align : 'center',
				        width : 80,
				        dataIndex:'status',
				        renderer:function(val){
							return val==201?'已回复':'未回复';
						}
				    }]
				},
				store : Ext.create('Ext.data.Store', {
					storeId : 'myStore',
					pageSize : pageSize,
					fields : ['id', 'code', 'vendName', 'topic', 'questionEndDate', 'inDate', 'tenderId','tenderCode',
							'tenderTitle', 'replyDate','status'],
					autoLoad : true,
					proxy : {
						type : 'ajax',
						url : basePath + 'scm/purchase/getTenderQuestionList.action',
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
							var grid = Ext.getCmp('tenderquestionlist');
							var search = Ext.getCmp('search').getValue();
							var date = Ext.getCmp('date').getValue();
							var status = Ext.getCmp('_status').value;
							Ext.apply(grid.getStore().proxy.extraParams, {
								search: search,
								date: date,
								status: status
							});
							
						},
						datachanged:function(store){
							var grid = Ext.getCmp('tenderquestionlist');
							grid.scrollByDeltaX(1);  //为了解决grid列错位的bug
							grid.scrollByDeltaX(-1);
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
					},{
						fieldLabel:"招标状态",
						labelWidth:80,
						fieldStyle:"background:#FFFAFA;color:#515151;",
						width: 160,
						id:'_status',
						name:'_status',
						xtype:'combo',
						store:Ext.create('Ext.data.Store', {
					    fields: ['display', 'value'],
					    data : [
					        {display:'全部', value:'all'},
					        {display:'已回复', value:'done'},
					        {display:'未回复', value:'todo'}
					    ]
						}),
						queryMode: 'local',
	    				displayField: 'display',
	    				valueField: 'value',
						value:'all'
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