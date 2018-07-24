Ext.define('erp.view.ma.DataDictionary',{ 
	extend: 'Ext.Viewport', 
	id:'datadictionary',
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 				
			items: [{
				xtype:'form',
				id:'form',
				frame:true,
				region:'north',
				layout:'column',
				defaults:{
					cls: "form-field-allowBlank",
					xtype:'textfield',
					focusCls: 'x-form-field-cir-focus',
					fieldStyle: "background:#FFFAFA;color:#515151;",
					columnWidth:0.5
				},
				items:[{
					fieldLabel:'表名',
					allowBlank:false,
					name:'object_name'				
				},{				
					fieldLabel:'注释',			
					allowBlank:false,
					name:'comments',
					xtype:'textareatrigger'
				},{
					fieldLabel:'ID',
					name:'object_id',
					hidden:true
				}],
				bbar:['->',{ 
					xtype: 'button', 
					cls: 'x-btn-blue',
					text: '新增',
					itemId:'add',
					width: 80,
					margin: '0 0 0 5'
				},{ 
					xtype: 'button', 
					cls: 'x-btn-blue',
					text: '保存',
					itemId:'save',
					formBind: true,
					width: 80,
					margin: '0 0 0 5'
				},{ 
					xtype: 'button', 
					cls: 'x-btn-blue',
					text: '刷新',
					itemId:'refresh',
					tooltip:'刷新FORMDETAIL,DETAILGRID,INITDETAIL对应字段长度',
					width: 80,
					margin: '0 0 0 5'
				},{
					xtype: 'button', 
					cls: 'x-btn-blue',
					text: '关闭',
					width: 80,
					itemId:'close',
					margin: '0 0 0 5'  
				},'->']		
			},{
				xtype:'tabpanel',
				region:'center',
				id:'dictab',
				animScroll:true,	//使用动画滚动效果
				layoutOnTabChange : true,	//随着布局变化
				resizeTabs:true, // turn on tab resizing
			    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
				items:[{
					title:'列信息',					
					xtype: 'dictionarygrid'
				},{
					title:'索引',
					layout:'border',
					items:[{
						region:'west',
						width:300,
						xtype:'panel',
						layout:'fit',
						frame:true,
						dockedItems:{
							dock : 'top',
							/*ui: 'footer',*/
							xtype:'toolbar',
							items:[{
								xtype:'label',
								html:'<h1>索引名称</h1>'
							},{
								xtype:'button',
								iconCls:'x-button-icon-add',
								text:'添加',
								itemId:'add_index'
							},{
								xtype:'button',
								iconCls:'x-button-icon-delete2',
								text:'删除',
								id:'disabled',
								itemId:'delete_index'
								/*listeners:{
									afterrender:function(){
									
									if(isbasic==1){								//添加判断是否调用删除组件功能
										
										Ext.getCmp('disabled').disabled=false;
										
									}else if(isbasic==null||isbasic==''){
	
										Ext.getCmp('disabled').disabled=true;
									}
								}
								}*/
							}]
						},
						items:[{
							xtype:'boundlist',
							deferInitialRefresh: false,
							multiSelect: true,
							store: me.store,
							name:'tab_indexs',
							itemSelector:'li',
							displayField:'index_name',
							border: false,
							disabled: false,
							store:Ext.create('Ext.data.Store', {
								fields: [ {name: 'index_name'},
								          {name:'uniqueness'},
								          {name:'ind_columns'}],
								          proxy: {
								        	  type: 'ajax',
								        	  url: basePath+'/common/getColumnIndex.action',
								        	  extraParams :{
								        		  tablename:tablename
								        	  },
								        	  reader: {
								        		  idProperty:'index_name',
								        		  type: 'json',
								        		  root: 'list'
								        	  }
								          },
								          autoLoad:true   
							})
						}]
					},{
						region:'center',
						layout: 'anchor',
						style:'margin-top:10px',
						xtype:'container',
						items:[{
							xtype:'textfield',
							fieldLabel:'索引名称',
							name:'index_name',
							id:'index_name',
							anchor:'100%',
							maxWidth:400,
							frame:true
						},{
							xtype: 'radiogroup',
							fieldLabel: '唯一性',
							layout: 'column',
							name:'uniqueness',
							id:'uniqueness',
							defaults: {
								columnWidth:1,
								labelStyle: 'padding:4px;'
							},
							items: [{boxLabel: '唯一', name: 'uniqueness', inputValue: 'UNIQUE'},
							        {boxLabel: '不唯一', name: 'uniqueness', inputValue: 'NONUNIQUE', checked: true}]
						},{
							xtype:'gridpanel',
							id:'index_column_grid',
							columnLines:true,
							plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
								clicksToEdit: 1
							})],
							tbar:[{
								xtype:'label',
								html:'<h1>表达式</h1>'
							},{
								xtype:'button',
								iconCls:'x-button-icon-add',
								text:'添加',
								itemId:'add_ind_column'
							},{
								xtype:'button',
								iconCls:'x-button-icon-delete2',
								text:'删除',
								itemId:'delete_ind_column'		
							}],
							columns:[{
								text:'索引字段',
								dataIndex:'COLUMN_NAME',
								cls: "x-grid-header-1",
								width:200,
								editor:{
									xtype:'combo',
									queryMode: 'local',
									displayField: 'column_name',
									valueField: 'column_name',
									store:Ext.create('Ext.data.Store',{
										fields:['column_name'],
										data:[]
									}),
									onTriggerClick:function(trigger){
										var me=this,store=this.getStore();
										if(store.totalCount<1){		
											store.loadRecords(Ext.getCmp('grid').getStore().data.items);
										}
										if (!me.readOnly && !me.disabled) {
											if (me.isExpanded) {
												me.collapse();
											} else {
												me.expand();
											}
											me.inputEl.focus();
										}    
									}
								}
							},{
								text:'排序',
								dataIndex:'DESCEND',
								cls: "x-grid-header-1",
								width:100,
								editor:{
									xtype:'combo',
									queryMode: 'local',
									displayField: 'display',
									valueField: 'value',
									store:Ext.create('Ext.data.Store',{
										fields:['display','value'],
										data:[{
											display:'ASC',
											value:'ASC'
										},{
											display:'DESC',
											value:'DESC'
										}]
									})
								}
							}],
							store:Ext.create('Ext.data.Store',{
								fields:['COLUMN_NAME','DESCEND'],
								data:[]
							})
						}]

					}]
				},{
					title:'修改日志',
					listeners:{
						activate:function(tab){
							if(!tab.loaded && tablename){
								Ext.Ajax.request({
									method: 'post',
									url : basePath + '/common/getFieldsDatas.action',
									params :{
										caller:'DB$LOG',
										fields:'alter_type,alter_remark,alter_date,alter_man',
										condition:"table_name='"+tablename+"'"
									},
									callback : function(options, success, response){					  				
										var res = new Ext.decode(response.responseText);
										tab.loaded=true;
										tab.getStore().loadData(new Ext.decode(res.data));
									}
								});	
							}
						}
					},
					xtype:'gridpanel',
					id:'log-grid',
					loaded:false,
					columnLines:true,
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					columns:[{
						text:'操作描述',
						dataIndex:'ALTER_REMARK',
						flex:1,
						filter: {xtype:"textfield", filterName:"ALTER_REMARK"}
					},{
						text:'操作类型',
						dataIndex:'ALTER_TYPE',
						width:80,
						filter: {xtype:"textfield", filterName:"ALTER_TYPE"}
					},{
						text:'操作人',
						width:150,
						filter: {xtype:"textfield", filterName:"ALTER_MAN"},
						dataIndex:'ALTER_MAN'
					},{
						text:'操作时间',
						xtype:'datecolumn',
						format:'Y-m-d H:i:s',
						width:150,
						filter: {xtype:"textfield", filterName:"ALTER_DATE"},
						dataIndex:'ALTER_DATE'
					}],
					store:Ext.create('Ext.data.Store', {
						fields: [ {name: 'alter_remark'},
						          {name:'alter_type'},
						          {name:'alter_man'},{
						        	  name:'alter_date'	  
						          }],
						          sorters: [{
						        	  property : 'alter_date',
						        	  direction: 'ASC'
						          }],
						          data:[]				  
					})
				},{
					title:'其他属性',					
					xtype: 'dictpropertygrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});