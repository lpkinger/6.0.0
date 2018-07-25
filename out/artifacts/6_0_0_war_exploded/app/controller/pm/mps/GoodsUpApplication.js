Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.GoodsUpApplication', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.GoodsUpApplication','core.grid.Panel2','core.toolbar.Toolbar','core.button.Split',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.GoodsUpTurnOut',
    		'core.button.GetStandardUUId','core.button.GoodsUp'
    	],
    init:function(){
        var me=this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
	    				if(Ext.getCmp("gu_statuscode").value == 'AUDITED'){
							btn = Ext.getCmp('erpSplitButton');
							btn && btn.setDisabled(false);
						}
    					if (record.data.gd_id != 0 && record.data.gd_id != null && record.data.gd_id != '') {
				            btn = Ext.getCmp('erpSplitButton');
							btn && btn.setDisabled(false);
				        }
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},  		 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				var config=_config ? '?_config=CLOUD':'';
    				me.FormUtil.onAdd('addGoodsUpApplication', '新增上架申请单', 'jsps/pm/mps/goodsUpApplication.jsp'+config);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpGoodsUpTurnOutButton':{//转拨出单
    			click:function(btn){
    				me.GoodsUpTurnOut(Ext.getCmp("gu_id").value,btn.ownerCt.ownerCt);
    			},
    			afterrender:function(btn){//已审核转拨出单
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpSplitButton': {//拆分明细
    			beforerender: function(btn) { 
                   btn.text="拆分明细";
                   btn.width=100; 
                }, 
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.GoodsUpSplit(record);
    			}
    		},
    		'erpPrintButton': {//打印
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('gu_id').value);
    			}
    		},
    		'erpGetStandardUUIdButton':{//维护标准料号
    			afterrender:function(btn){//
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click:function(btn){//发送请求至后台，将明细行中没有平台料号的物料写入ProductBatch表中，写入成功之后跳转至批量维护页面
    				 me.FormUtil.setLoading(true);
    				 Ext.Ajax.request({
					   	  url : basePath +'pm/mps/getUUId.action',
					   	  params : {
					   	  	caller : caller,
					   	  	    id : Ext.getCmp("gu_id").value
					   	  },
					   	  method : 'post',
					   	  timeout:6000000,
					   	  callback : function(options,success,response){
					   	  	me.FormUtil.setLoading(false);
					   		var localJson = new Ext.decode(response.responseText);
					   		if(localJson.success){
					   			if(localJson.code)
					   			   me.FormUtil.onAdd('ProductBatchUUId','批量维护标准料号','jsps/scm/product/ProductBatchUUId.jsp?gridCondition=pub_code='+localJson.code+'&code='+localJson.code);
			    				/*saveSuccess(function(){
			    					Ext.getCmp('gdqty').setValue(remainqty);
			    					//add成功后刷新页面进入可编辑的页面 
			    					me.loadSplitData(originaldetno,guid,record);  
			    				});*/
				   			}else if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;				   				
				   				showError(str);
					   			return;				   								   			
					   	     }else{
					   	     	showError("请求超时");
					   			return;	
					   	     }
					   	  }
					  });   		
    			}
    		},
    		'erpGoodsUpButton':{//手动上架
    			afterrender: function(btn){
    				var status = Ext.getCmp('gu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				me.GoodsUp(Ext.getCmp("gu_id").value,btn.ownerCt.ownerCt);
    			}   			
    		},
    		'gridcolumn[dataIndex=gd_erpunit]':{
    			beforerender:function(column){
    				  console.log(column);
    				  //column.xtype='numbercolumn';
    				  column.readOnly = true;
    				  /*column.renderer = function(val, meta, record) {
						var standard = record.get('pvd_isstandard');
						if(!val && (standard == 0 || standard == 2)) {
							meta.tdCls = 'x-form-necessary';
						} else {
							meta.tdCls = null;
						}
						return val;
					}*/
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('gu_code').value == null || Ext.getCmp('gu_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	GoodsUpSplit:function(record){
		var me=this,originaldetno=Number(record.data.gd_detno);
		var guid=record.data.gd_guid;
		var gdid=record.data.gd_id;
		var bccode = record.data.gd_bccode;
		if(bccode){
			showError('该明细行已经生成拨出单，不允许拆分!');
			return;
		}
		Ext.create('Ext.window.Window',{
    		width:850,
    		height:'80%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>上架申请单拆分</h1>',
    		id:'win',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			region:'north',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
    			},
    			items:[{
    			 fieldLabel:'上架单号',
    			 value:Ext.getCmp("gu_code").value,
    			 id:'gucode'
    			},{
    			 fieldLabel:'物料编号',
    			 value:record.data.gd_prodcode
    			},{
    			 fieldLabel:'物料明细',
    			 value:record.data.pr_detail
    			},{
    			 fieldLabel:'原序号'	,
    			 value:record.data.gd_detno
    			},{
    		     fieldLabel:'原数量',
    		     value:record.data.gd_qty,
    		     id:'gdqty'
    			}],
    			buttonAlign:'center',
    			buttons:[{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var dd; 
    				   var remainqty;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(item.data.gd_qty>=0 && item.data.gd_madedate!=null && item.data.gd_price>0){
    						  if(item.dirty){
    							  dd=new Object();
    							  //说明是新增批次
    							  if(item.data.gd_madedate)
    							      dd['gd_madedate']=Ext.Date.format(item.data.gd_madedate, 'Y-m-d');
    							  dd['gd_qty']=item.data.gd_qty; 
    							  dd['gd_id']=item.data.gd_id;
    							  dd['gd_detno']=item.data.gd_detno;
    							  dd['gd_price']=item.data.gd_price;
    							  dd['gd_minbuyqty']=item.data.gd_minbuyqty;
    							  dd['gd_whcode']=item.data.gd_whcode;
    							  jsonData.push(Ext.JSON.encode(dd));
    							  if(item.data.gd_id!=0&&item.data.gd_id!=null&&item.data.gd_id>0){
    								  remainqty = item.data.gd_qty; 
  								  }
    						  }
    						  count+=Number(item.data.gd_qty);
    					  } 
    				   });	  
    				   var assqty = record.data.gd_qty;
    				   if(count != assqty){
	    					showError('分拆数量必须等于原数量!') ;  
	    					return;
    				   }else{
    					   var r=new Object();
        				   r['gd_id']=gdid;
        				   r['gd_guid']=guid;
        				   r['gd_detno']=record.data.gd_detno;      
        				   var params=new Object();
        				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        				   params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
    					   Ext.Ajax.request({
    					   	  url : basePath +'pm/mps/splitDetail.action',
    					   	  params : params,
    					   	  waitMsg:'拆分中...',
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
    			    				saveSuccess(function(){
    			    					Ext.getCmp('gdqty').setValue(remainqty);
    			    					//add成功后刷新页面进入可编辑的页面 
    			    					me.loadSplitData(originaldetno,guid,record); 
    			    					//刷新界面
    			    					me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
				                            caller: caller,
				                            condition: gridCondition
				                        });
    			    				});
    				   			} else if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					saveSuccess(function(){
    				    					//add成功后刷新页面进入可编辑的页面 
    				   					 me.loadSplitData(originaldetno,guid,record);  
    				    				});
    				   					showError(str);
    				   				} else {
    				   					showError(str);
    					   				return;
    				   				}    					   			
    					   	 } else{
    				   				saveFailure();
    				   			}
    					   	  }
    					   });   					   
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  region:'south',
    		  id:'smallgrid',
    		  layout:'fit',
    		  height:'80%',
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'gd_detno',type:'int'},{name:'gd_madedate',type:'date'},{name:'gd_qty',type:'int'},{name:'gd_price',type:'number'},{name:'gd_minbuyqty',type:'int'},{name:'gd_whcode',type:'string'},{name:'gd_id',type:'int'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        			Ext.Msg.alert('提示','不能修改已拆分明细!');
    		        		}
    		        	}
    		        }
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.gd_madedate=record.get('gd_madedate');
    	                r.gd_minbuyqty=record.get('gd_minbuyqty');
    	                r.gd_price=record.get('gd_price');
    	                r.gd_qty=0; 
    	                r.gd_id=0;
    	                r.gd_whcode='';
    	                r.gd_detno=store.getCount()+1;
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                var record=sm.getSelection();
    	                var gd_id=record[0].data.gd_id;
    	                if(gd_id && gd_id!= 0){
    	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
    	                	return;
    	                }
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(record);
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  }
    	      }, 
    		  columns:[{
    			 dataIndex:'gd_detno',
    			 header:'序号',
    			 format:'0',
    			 xtype:'numbercolumn'
    		   },{
    			  dataIndex:'gd_madedate',
    			  header:'生产日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d',
    				  allowBlank : false  
    			  }
    		  },{
    			  dataIndex:'gd_qty',
    			  header:'数量',
    			  width:120,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true,
    				  allowBlank : false  
    			  }
    		  },{
    			dataIndex:'gd_price',
    			header:'单价',
    			xtype:'numbercolumn',
    			width:100,
    			editable:true,
    			editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true,
    				  allowBlank : false  
    			  }
    		  },{
    			dataIndex:'gd_minbuyqty',
      			header:'最小起订量',
      			xtype:'numbercolumn',
      			width:100,
      			editable:true,
      			editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true,
    				  allowBlank : false  
    			  }  
    		  },{
    			dataIndex:'gd_whcode',
      			header:'仓库',
      			width:100,
      			editable:true,
      			dbfind:'WareHouse|wh_code',
    			editor:{
    				  allowBlank : false  ,
    				  xtype:'dbfindtrigger',
    				  hideTrigger: false,
    				  name:'gd_whcode',
    				  which:'grid',
    				  dbfind:'WareHouse|wh_code',
    				  listeners: {
                        aftertrigger: function(t, d) {
                        	var record = Ext.getCmp('smallgrid').selModel.lastSelected;  				
    				        record.set('gd_whcode', d.data.wh_code);
                        }
                   }
    			}
    		  },{
    			  dataIndex:'gd_id',
    			  header:'gdid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  }]
    		}]   		
    	}).show();
         this.loadSplitData(originaldetno,guid,record); 
	},
	loadSplitData:function(detno,guid,record){
		 var grid=Ext.getCmp('smallgrid');
         grid.setLoading(true);//loading...
 		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "common/loadNewGridStore.action",
         	params:{
         	  caller:'GoodsUpSplit',
         	  condition:"gd_detno="+detno+" AND gd_guid="+guid+" order by gd_id asc"
         	},
         	method : 'post',
         	callback : function(options,success,response){
         		grid.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}
         		var data = res.data;
         		if(!data || data.length == 0){
         			grid.store.removeAll();
         			var o=new Object();
         			o.gd_detno=detno;
         			o.gd_madedate=record.data.gd_madedate;
         			o.gd_qty=record.data.gd_qty;
         			o.gd_price=record.data.gd_price;
         			o.gd_minbuyqty=record.data.gd_minbuyqty;
         			o.gd_whcode=record.data.gd_whcode;
         			o.gd_id=record.data.gd_id;
         			data.push(o);
         		}
         		 grid.store.loadData(data);
         	}
         });
	},
	GoodsUpTurnOut:function(id,form){
		var me = this;
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "pm/mps/goodsUpTurnOut.action",
         	params:{
         	  caller:caller,
         	  id:id
         	},
         	method : 'post',
         	callback : function(options,success,response){
         		me.FormUtil.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}else{
         			if(res.log)
	    				showMessage('提示', res.log);
         		}
         	}
         });
	},
	GoodsUp:function(id,form){
		var me = this;
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "pm/mps/goodsUp.action",
         	params:{
         	  caller:caller,
         	  id:id
         	},
         	method : 'post',
         	timeout:6000000,
         	callback : function(options,success,response){
         		me.FormUtil.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}else{
         			if(res.log){
	    				showMessage('提示', res.log);
	    				var grid = Ext.getCmp("grid");
	    				grid.GridUtil.loadNewStore(grid, {
                            caller: caller,
                            condition: gridCondition
                       });  
         			}
         		}
         	}
         });
	}
	
});