/**
 * 工单中取消BOM跳层
 */	
Ext.define('erp.view.core.button.DisableBomPast',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDisableBomPastButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDisableBomPastButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
        requires: ['erp.view.core.grid.ButtonColumn'],
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var me = this;
			//根据ma_id 获取用量中的跳层物料，弹出window
			var ma_id = Ext.getCmp("ma_id").value;
			me.getPastBom(ma_id,function(data){
				if(data.data != null){
					var win = Ext.create("Ext.window.Window",{
					      title:'跳层BOM',
					      id:'pastBom',
					      width:750,
					      height:450,
					      closeAction: 'destroy',
					      maximizable : false,
    					  buttonAlign : 'center',
    					  autoScroll:true,
					      items: [{
					      	xtype:'grid',
					      	columnLines:true,
		                    plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					      	columns:[{
					      		text:'MM_ID',
					      		dataIndex:'MM_ID',
					      		hidden:true
					      	},{
					      	   text:'序号',
					      	   dataIndex:'MM_DETNO',
					      	   width:80
					      	},{
					      	   text:'物料编号',
					      	   dataIndex:'MM_PRODCODE',
					      	   flex:1.2
					      	},{
					      	   text:'物料名称',
					      	   dataIndex:'PR_DETAIL',
					      	   flex:1.3
					      	},{
					      	   text:'规格',
					      	   dataIndex:'PR_SPEC',
					      	   flex:1
					      	},{
					      	   text:'单位用量',
					      	   dataIndex:'MM_ONEUSEQTY',
					      	   width:80
					      	},{
					      	   text:'制单需求数',
					      	   dataIndex:'MM_QTY',
					      	   flex:1
					      	},{
					      		text: '操作',
								dataIndex: 'ACTION',
								cls: 'x-grid-header-1',
								xtype: 'buttoncolumn',
								width: 90,
								buttonText: '取消跳层',
								handler: function(view, cell, recordIndex, cellIndex, e) {
									var record = view.store.getAt(recordIndex), da = record.data;
									me.disablePastBom(da['MM_ID']);
								}
					      	}],
					      	store : new Ext.data.Store({
									fields:['MM_ID', 'MM_DETNO', 'MM_PRODCODE','PR_DETAIL','PR_SPEC','MM_ONEUSEQTY','MM_QTY','ACTION'],
									data : data.data
							})
					      }],
					      bbar: ['->',{
    						text:'关闭',
    						cls: 'x-btn-gray',
    						iconCls: 'x-button-icon-close',
    						listeners: {
    							click: function(btn){
    								btn.ownerCt.ownerCt.close();
    							}
    						}
    					 },'->'],
    					 listeners:{
                              'beforeclose':function(view ,opt){
                            	var grid = Ext.getCmp('grid');
     			        		var value = Ext.getCmp('ma_id').value;
     			        		var gridCondition = grid.mainField + '=' + value,
     			        		gridParam = {caller: caller, condition: gridCondition};
     			        		grid.GridUtil.loadNewStore(grid, gridParam);  
                           }	
    					 }
					});	
					win.show();
				}else if(data.exceptionInfo){
					showError(data.exceptionInfo);
				}
			});
		},
		
		getPastBom:function(ma_id,callback){//获取用料表中的跳层BOM
			Ext.Ajax.request({ 							             							    	      
					url: basePath + 'pm/make/getPastBom.action?_noc=1',
					params: {
						ma_id: ma_id,
						caller:caller
					},
					callback: function (opt, s, r) {	
						var res = new Ext.decode(r.responseText);
						if(res){
		 				    callback && callback.call(null, res);
			 			 } else{
			 			   	return;
			 			 }
					}
			});
		},
		disablePastBom:function(mm_id){
			var me = this;
			var ma_id = Ext.getCmp('ma_id').value;
			Ext.Ajax.request({ 							             							    	      
				url: basePath + 'pm/make/disableBomPast.action',
				params: {
					mm_id: mm_id,
					caller:caller
				},
				callback: function (opt, s, r) {	
					var res = new Ext.decode(r.responseText);
		        	if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			showMessage("提示", '取消跳层成功！');
	        			me.getPastBom(ma_id,function(data){
	        				if(data.data != null){
	        					Ext.getCmp("pastBom").down("grid").store.loadData(data.data);
	        				}else{
	        					Ext.getCmp("pastBom").close();
	        				}
	        			});
	        		}
				}
		    });
		}
	});