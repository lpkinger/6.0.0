Ext.define('erp.view.oa.info.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items:[{
				xtype:'toolbar',
				region:'north',
				//ui: 'footer',
				defaults:{
					cls: 'x-btn-gray'
				},
				items: [{
					margin:'0 0 0 1',
					text: '发送',
					itemId:'send',
					iconCls:'x-button-icon-confirm'
				},{
					text:'刷新',
					itemId:'refresh',
					style:'margin-right:20px;margin-left:5px',
					iconCls:'tree-refresh'
				}]
			},{
				xtype:'tabpanel',
				region:'center',
				items:[{
					title:'在线人员',
					xtype:'grid',
					id:'on-line',
					dockedItems: [{				
						xtype: 'toolbar',
						dock: 'top',
						displayInfo: true,
						height:30,
						items:[{
							xtype:'checkbox',
							name:'only_online',
							boxLabel:'仅在线',
							labelAlign:'left',
							inputValue:1,
							padding:'0 0 0 30'
						}]
					}],
					columns:[{
						dataIndex:'EM_NAME',
						text:'姓名',
						cls:'x-grid-header-1',
						filter: {xtype:"textfield", filterName:"EM_NAME"},
						renderer:function(val,meta,record){
							var _online=record.get('ISONLINE'),_sex=record.get('EM_SEX'),iconName;
							iconName=_sex=='男'?'qman':'qwoman';
							iconName+=_online==0?'1':'';
							return '<img src="'+basePath+'resource/images/icon/'+iconName+'.gif" border="0" align="center">'+'<span style="padding-left:2px">' + val + '</span>';
						}
					},{
						dataIndex:'EM_POSITION',
						text:'岗位',
						cls:'x-grid-header-1',
						filter: {xtype:"textfield", filterName:"EM_POSITION"},
						flex:1
					},{
						dataIndex:'EM_DEFAULTORNAME',
						cls:'x-grid-header-1',
						text:'所属组织',
						filter: {xtype:"textfield", filterName:"EM_DEFAULTORNAME"},
						flex:1
					},{
						dataIndex:'ISONLINE',
						width:0,
						filter: {xtype:"textfield", filterName:"ISONLINE"}
					},{
						text: '发送消息',
						dataIndex: 'send',
						xtype:'actioncolumn',
						cls: 'x-grid-header-1',		
						sortable:false,			
						flex: 0.5,
						align:'left',
						icon:basePath+'resource/images/icon/reply.gif',
						tooltip: '发送消息',
						handler: function(grid, rowIndex, colIndex) {
							var record = grid.getStore().getAt(rowIndex);
							grid.fireEvent('itemclick',grid,record);
						}
					}],
					columnLines:true,
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						checkOnly : true,
						ignoreRightMouseSelection : false
					}),
					necessaryFields:['EM_ID','EM_NAME'],
					keyField:'EM_ID',
					manidField:'EM_ID',
					manField:'EM_NAME',
					store:Ext.create('Ext.data.Store',{
						fields:['EM_ID','EM_NAME',{name:'ISONLINE'},'EM_SEX','EM_DEFAULTORNAME','EM_DEPART','EM_POSITION'],
						proxy: {
							type: 'ajax',
							url : basePath + 'oa/info/getUsersIsOnline.action',
							reader: {
								type: 'json',
								root: 'emps'
							}
						},
						sorters:[{
							property : 'ISONLINE',
							direction: 'DESC'
						}]				
					})
				},{
					title:'发件箱',
					id:'send-grid',
					xtype: 'erpDatalistGridPanel',
					_noc:1,
					anchor:'100% 100%',
					caller:'PagingRelease!Send',
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					showRowNum:false,
					defaultCondition:'Pr_Releaserid='+em_uu,
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						checkOnly : true,
						ignoreRightMouseSelection : false
					}),
					dockedItems: [{
						id : 'pagingtoolbar1',
						xtype: 'erpDatalistToolbar',
						dock: 'bottom',
						displayInfo: true,
						items:[]
					}]
				},{
					title:'收件箱',
					xtype: 'erpDatalistGridPanel',
					_noc:1,
					id:'receive-grid',
					caller:'PagingRelease',
					anchor:'100% 100%',
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					showRowNum:false,
					defaultCondition:'prd_recipientid='+em_uu,
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						checkOnly : true,
						ignoreRightMouseSelection : false
					}),
					dockedItems: [{
						id : 'pagingtoolbar12',
						xtype: 'erpDatalistToolbar',
						dock: 'bottom',
						displayInfo: true,
						items:[]
					}]
				}]
			}]	
		});
		me.callParent(arguments); 
	}
});