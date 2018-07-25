Ext.define('erp.view.pm.mes.MakeBad',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'维修作业',
				xtype: 'form',
				anchor: '100% 25%',
				bodyStyle: 'background: #f1f1f1;',
				autoScroll: true,
				scrollable: true,
				items:[{
					xtype: 'fieldcontainer',					
					defaults: {
						width: 250
					},     
					layout: {
						type: 'table',
						columns: 4
					},
					items: [{
							xtype: 'dbfindtrigger',
							fieldLabel: '资源编号',
							colspan: 1,
							allowBlank:false,
							id:'scCode',
							name:'scCode',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;"
						},{
							xtype: 'textfield',
							fieldLabel: '资源名称',
							readOnly:true,						
							colspan: 1,
							id:'scName',
							name:'scName',
							fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
						},{
							xtype: 'textfield',
							fieldLabel: '工序编号',
							readOnly:true,
							colspan: 1,
							id:'stCode',
							name:'stCode',
							fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
						},{
							xtype: 'textfield',
							fieldLabel: '工序名称',
							readOnly:true,
							colspan: 1,
							id:'stName',
							name:'stName',
							fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
						},{			
						    xtype: 'textfield',
							fieldLabel: '序列号',
							colspan: 1,
							id:'ms_sncode',
							name:'ms_sncode',
							emptyText: '请录入序列号',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;"
						},{
							xtype: 'textfield',
							fieldLabel: '制造单号',
							colspan: 1,
							id:'mc_makecode' ,
							name:'mc_makecode',
							readOnly:true,
							fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '回流工艺',
							colspan: 1,
							id:'cr_code',
							name:'cr_code'
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '回流工序',
							colspan: 1,
							id:'cd_stepcode',
							name:'cd_stepcode'
						}]			 
			    }],
			    defaults: {
					margin: '5'
				},
			    buttonAlign: 'center',
				buttons: [{  
					xtype: 'button',
					id:'finishFix',
					text:'完成维修'	,
					cls: 'x-btn-gray'
				  },{ 
					xtype: 'button',
					id:'scrap',
					text:'报废',
					cls: 'x-btn-gray'
				  },{
					xtype: 'erpCloseButton'					
				}]
			},{				
				xtype: 'form',
				id:'fixForm',
				anchor: '100% 25%',
				title:'维修处理信息',
				bodyStyle: 'background: #f1f1f1;',
				items: [{
					xtype: 'fieldcontainer',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 250
					},
					layout: 'column',
					items: [{					
						xtype: 'textfield',
						fieldLabel: 'ID',
						id:'mb_id',
						hidden : true,
						hideLabel : true
					},{
						xtype: 'combo',
						fieldLabel: '不良组别',
						id:'bc_groupcode',
						name:'bc_groupcode',
						allowBlank: false,
						fieldStyle : "background:rgb(224, 224, 255);",    
				        labelStyle:"color:red;"	,
				        autoSelect:true,
						store: Ext.create('Ext.data.Store', {
							   fields: ['bg_code'],
							   proxy: {
						            type: 'ajax',
								    url : basePath + 'pm/mes/getBadGroup.action',				           
								    reader: {
								         type: 'json',
								         root: 'data'
								    },
								    headers: {
						                'Content-Type': 'application/json;charset=utf-8'
						            }		                   
						          }						      
						}),
						displayField: 'bg_code',
						valueField: 'bg_code'
					},{
						xtype: 'combo',
						fieldLabel: '不良原因',
						id:'mb_badcode',
						editable : false,
						queryMode: 'remote',
						autoSelect:true,
						defaultListConfig:{              //取消loading的Mask
			                  loadMask: false
			            },
						store: Ext.create('Ext.data.Store', {
						    fields: ['bc_code','bc_name'],
						    proxy: {
					             type: 'ajax',
							     url : basePath + 'pm/mes/getBadCode.action',				           
							     extraParams:{condition:''},
							     reader: {
							          type: 'json',
							          root: 'data'
							     },
							     headers: {
					                 'Content-Type': 'application/json;charset=utf-8'
					             }		                   
					           }				     
						}),
					   displayField: 'bc_name',
					   valueField: 'bc_code'					  
					},{
						xtype: 'textareatrigger',
						fieldLabel: '不良备注',
						id:'mb_badremark'
					},{
						xtype: 'combo',
						fieldLabel: '维修结果',
						id:'mb_status',
						store: Ext.create('Ext.data.Store', {
						   fields: ['display', 'value'],
						   data : [{"display": '待维修', "value": '0'},
						           {"display": '已维修', "value": '1'},
						           {"display": '不可维修', "value": '2'},
						           {"display": '无不良', "value": '-1'}]
					   }),
					   displayField: 'display',
					   valueField: 'value',
					   queryMode: 'local',
					   value:'0',
					   editable: false
					}]
				}],
				defaults: {
					margin: '5'
				},
			    buttonAlign: 'center',
				buttons: [{  
					xtype: 'erpSaveButton'
				  },{ 
					xtype:'erpAddButton'
				  },{
					xtype:'erpDeleteButton'				
				}]
			 },{			   
				xtype: 'grid',
				anchor: '100% 40%',
				id:'querygrid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns: [{
					text: 'ID',
					dataIndex: 'mb_id',
					flex: 1,
					hidden:true
				},{
					text: 'bc_groupcode',
					dataIndex: 'bc_groupcode',
					flex: 1,
					hidden:true
				},{
					text: '序列号',
					dataIndex: 'mb_sncode',
					flex: 1
				},{
					text: '不良原因码',
					dataIndex: 'mb_badcode',
					flex: 1					
				},{
					text: '不良原因',
					dataIndex: 'bc_name',
					flex: 1					
				},{
				    text: '不良备注',
					dataIndex: 'mb_badremark',
					flex: 1
				},{
				    text: '解决方案',
					dataIndex: 'bc_note',
					flex: 1
				},{
				    text: '责任方',
					dataIndex: 'bc_dutyman',
					flex: 1
				},{
				    text:'维修结果',
				    dataIndex:'mb_status',
				    flex:1,
				    xtype:"combocolumn",
				    editor: {
						xtype: 'combo',
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						editable: false,
						store: {
							fields: ['display', 'value'],
							data : [{"display": '待维修', "value": '0'},
						           {"display": '已维修', "value": '1'},
						           {"display": '不可维修', "value": '2'},
						           {"display": '无不良', "value": '-1'}]
						}
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: ['mb_id','bc_groupcode','mb_sncode','mb_badcode','bc_name','mb_badremark','bc_note','bc_dutyman','mb_status'],			  
			        data: [ {},{},{},{},{},{},{},{},{},{},{}],
                    autoLoad:true
			     })			
			}] 
		}); 
		me.callParent(arguments); 
	} 
});