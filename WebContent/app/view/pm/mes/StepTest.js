Ext.define('erp.view.pm.mes.StepTest',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'测试采集',
				xtype: 'form',
				anchor: '100% 30%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				items:[{
					xtype: 'fieldcontainer',
					region: 'center',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 250,
						fieldStyle : 'background:#f0f0f0;border: 0px solid #8B8970;font-color:blue'
					},   					
					layout: {
						type: 'table',
						columns: 4
					},
					items: [{
							xtype: 'dbfindtrigger',
							fieldLabel: '资源编号',
							colspan: 1,
							id:'sc_code',
							name:'sc_code',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入资源编号'
						},{
							xtype: 'textfield',
							fieldLabel: '资源名称',
							readOnly:true,
							colspan: 1,
							id:'sc_name',
							name:'sc_name'
						},{
							xtype: 'textfield',
							fieldLabel: '工序编号',
							readOnly:true,
							colspan: 1,
							id:'st_code',
							name:'st_code'
						},{
							xtype: 'textfield',
							fieldLabel: '工序名称',
							readOnly:true,
							colspan: 1,
							id:'st_name',
							name:'st_name'
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '作业单号 ',
							colspan: 1,
							id:'mc_code',
							name:'mc_code',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入作业单号'
						},{
							xtype: 'textfield',
							fieldLabel: '制造单号',
							readOnly:true,
							colspan: 1,
							id:'mc_makecode' ,
							name:'mc_makecode'
						},{			
						    xtype: 'textfield',
							fieldLabel: '产品编号',
							readOnly:true,
							colspan: 1,
							id:'mc_prodcode',
							name:'mc_prodcode'
						},{
							xtype: 'textfield',
							fieldLabel: '产品名称',
							readOnly:true,
							colspan: 1,
							id:'pr_detail',
							name:'pr_detail'
						},{
							xtype: 'textfield',
							fieldLabel: '数量',
							readOnly:true,
							colspan: 1,
							id:'mc_qty'	,
							name:'mc_qty'
						},{
						    xtype: 'textfield',
							fieldLabel: '已采集',
							readOnly:true,
							colspan: 1,
							id:'mcd_inqty',
							name:'mcd_inqty'
						},{			
						    xtype: 'textfield',
							fieldLabel: '待采集',
							readOnly:true,
							colspan: 1,
							id:'mc_restqty',
							name:'mc_restqty'
						}]					
					}]
			},{				
				xtype: 'form',
				id:'testform',
				anchor: '100% 20%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:'8,5,5,5',
				autoScroll: true,
				scrollable: true,
				items: [{
					xtype: 'fieldcontainer',
					items:[{
					   layout:'column',
					   bodyStyle: 'background: #f1f1f1;',
					   border:false,
					   items:[{					   	
							xtype: 'textfield',
							fieldLabel: '序列号',
							readOnly: false,
							id:'ms_code',
							columnWidth : 0.55,
							allowBlank: false,
							emptyText: '请录入序列号',
							listener:{
								beforerender:function(){
									
								}
							}
						 },{
							xtype: 'button',
							text:'合格通过',
							id:'confirmBtn',
							width:'90px;',				
							cls: 'x-btn-gray',
							style: {
					    		    marginLeft: '60px',
                                    marginRight: '70px'
					        }
						 },{						
							xtype: 'dbfindtrigger',
							fieldLabel: '返修工序',
							id:'st_rcode',
							name:'st_rcode'
						 },{
							xtype: 'button',
							text: '转返修',
							id:'confirmRepairStep',
							width:'90px;',
							cls: 'x-btn-gray',
							style: {
					    		marginLeft: '10px'
					      }										
					    }]
					},{
						bodyStyle: 'background: #f1f1f1;',
						border:false,
						defaults: {
						  width: 250
						},
						layout: {
							type: 'table',
							columns: 4
						},
						items:[{
							xtype: 'combo',
							fieldLabel: '不良组别',
							id:'bc_groupcode',
							name:'bc_groupcode',
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
							id:'bc_reason',
							editable : false,
							autoSelect:true,
							queryMode: 'remote',
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
						           },
						          listeners:{
						          	load : function (store){
						          		Ext.getCmp('bc_reason').select(store.getAt(0));								
						          	}								
						           }
							}),
						   displayField: 'bc_name',
						   valueField: 'bc_code'
						},{
							xtype: 'textareatrigger',
							fieldLabel: '不良备注',
							id:'bc_remark',
							allowBlank: true
						},{
							xtype: 'button',
							text: '保存不良原因',
							id:'saveBad',
							width:'90px;',
							cls: 'x-btn-gray',
							style: {
					    		marginLeft: '10px'
					        }						
						}]
				  }]
			   }]
			 },{			   
				xtype: 'grid',
				anchor: '100% 50%',
				id:'querygrid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				tbar : [		   
				   '->',{ xtype: 'button', text: '删除',cls: 'x-btn-gray', width: 60,iconCls: 'x-button-icon-delete',id:'deletebutton' }
				 ],
				selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	checkOnly : true,
					ignoreRightMouseSelection : false,
					listeners:{
				        selectionchange:function(selModel, selected, options){
				        	selModel.view.ownerCt.selectall = false;
				        }
				    },
				    getEditor: function(){
				    	return null;
				    },
				    onHeaderClick: function(headerCt, header, e) {
				        if (header.isCheckerHd) {
				            e.stopEvent();
				            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
				            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
				                this.deselectAll(true);
				            } else {
				                this.selectAll(true);
				                this.view.ownerCt.selectall = true;
				            }
				        }
				    }
				}),
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