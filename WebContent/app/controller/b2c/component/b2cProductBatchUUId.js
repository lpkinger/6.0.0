Ext.QuickTips.init();
Ext.define('erp.controller.b2c.component.b2cProductBatchUUId', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'b2c.component.b2cPanel','b2c.common.Viewport','b2c.common.b2cForm','b2c.common.b2cGrid','core.toolbar.Toolbar','core.button.Scan','core.button.Export',
    		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.CleanDetail','core.button.Confirm','core.button.GetB2CProductKind',
  			'scm.product.GetUUid.ComponentGrid','scm.product.GetUUid.Toolbar','core.button.LoadProd','core.button.RemoveUUId','core.button.GetUUId'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender:function(grid){
    				if(code == ''){
	    				Ext.Ajax.request({
					   		url : basePath + 'common/getCodeString.action',
					   		async: false,//同步ajax请求
					   		params: {
					   			caller: caller,//如果table==null，则根据caller去form表取对应table
					   			type: 2
					   		},
					   		method : 'post',
					   		callback : function(options,success,response){
					   			var localJson = new Ext.decode(response.responseText);
					   			if(localJson.exceptionInfo){
					   				showError(localJson.exceptionInfo);
					   			}
				    			if(localJson.success){
				    				code = localJson.code;
					   			}
					   		}
						});
    				}
    				grid.multiselected=[];
    			},
    			storeloaded:function(grid){
    				grid.multiselected=[];
    			}
    			/*cellclick:function(view,td,cellIndex,record,tr,rowIndex,e,eOpts){
    				var field = view.ownerCt.columns[cellIndex].dataIndex;
					if (field == 'pub_uuidstr') {
						this.onCellItemClick(record);
					};
    			}*/
    		},
    		'erpConfirmButton':{
    			beforerender:function(btn){
    				btn.text="保存导入";
    				btn.width=100;
    			},
    			click:function(btn){
    				var stores = me.GridUtil.getGridStore(Ext.getCmp('grid'));
    				if(stores.length != 0){//
    					var errInfo = me.GridUtil.getUnFinish(Ext.getCmp('grid'));		    
						if(errInfo.length > 0){
							showError('明细表有必填字段未完成填写<hr>' + errInfo);						
						}else{
							me.batchUpdateUUId(stores);
						}
    				}else{
    					showError("还未添加或修改数据.");
    				}
    			}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'gridcolumn[dataIndex=pub_uuidstr]': {
				afterrender: function(column) {
					column.renderer = function(val, meta, record) {
						if(val != null && val != "") {
							val = '有';
						} else {
							val = '无';
						}
						return val;
					}
				}
			},
			'erpGetB2CProductKindButton':{
				click:function(btn){					
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				if(record && record.get("pub_prodcode")){
    					me.onCellItemClick(record);
    				} 			
				},
				afterrender:function(btn){
					btn.setDisabled(true); 
				}
			},
			'erpLoadProdButton':{
				click:function(btn){	
					var urlcondition =" pr_code not in (select pub_prodcode from ProductUUIDBatch where pub_code='"+code+"')";					
		    		var me = this; 
		    		var url = basePath + "jsps/b2c/component/b2cProdResource.jsp";  
		    		Ext.create('Ext.window.Window',{
   			    		id : 'win',
   			    		title:'选择匹配物料范围',
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					layout : 'anchor',
	   				    items: [{
	   				    	  tag : 'iframe',
	   				    	  frame : true,
	   				    	  anchor : '100% 100%',
	   				    	  layout : 'fit',
	   				    	  html : '<iframe id="iframe_dl_'+caller+'" src="'+url+'?urlcondition='+urlcondition+'&whoami=BatchUUIdSource&code='+code+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	   				    }],
	   				    listeners:{
						    'beforeclose':function(view ,opt){
						    	 window.location.href = basePath + "jsps/b2c/component/b2cProductBatchUUId.jsp?gridCondition=pub_code="+code+"&code="+code+'&_noc=1&_config=CLOUD'; 
						    } 
						  }
	   				}).show();  		    	
				}
			},
			'erpRemoveUUIdButton':{
				click:function(btn){
    				me.removeUUId();  		
				}
			},
			'erpGetUUIdButton':{//自动匹配
				click:function(btn){
					var stores = me.GridUtil.getAllGridStore(Ext.getCmp('grid'));
    				if(stores.length != 0){
    					me.getUUid();
    				}
				}
			},
			'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype: 'erpGetB2CProductKindButton'
    				});
    			}       		
    		}
		});
	},
	onCellItemClick:function(record){
		var me = this;
		// grid行选择
		var str = record.data['pub_uuidstr'];	
		var uuid = record.data['pub_uuid'];
		if(!record.dirty && !Ext.isEmpty(uuid)){
			//根据uuid获取相关信息
			me.getByUUid(uuid,function(data){ 	
    		    if(data != null){
    				me.createWin(data);
    		    }					    		  
	  		});			
		}else if(!Ext.isEmpty(str)){
			me.getByUUid(str,function(data){ 	  		    	
    		    if( data != null){
    				me.createWin(data);
    		    }					    		  
	  		});		
		}else{
			var linkCaller = 'Product';
			var status= '';
			var win = new Ext.window.Window({
				id : 'uuWin',
				height : "100%",
				width : "80%",
				maximizable : true,
				closeAction : 'destroy',
				buttonAlign : 'center',
				layout : 'anchor',
				title : '获取编号',
				items : [{
					tag : 'iframe',
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_'+linkCaller+'" src="'
							+ basePath
							+ 'jsps/scm/product/getUUid.jsp?type='
							+ linkCaller+'&status='+status
							+ '" height="100%" width="100%" frameborder="0"></iframe>'
				}]
			});
		  win.show();	
		}
	},
	createWin:function(data){
		var me = this, win = me.orWin;
		me.data=data;
		if (!win) {
		     me.orWin=win = Ext.create('Ext.Window',{  
				id : 'wind',
				title:'标准料号',
				height : '65%',
				width : '65%',
				maximizable : true,
				buttonAlign : 'center',
				closeAction:'hide',
				layout : 'anchor',
				items : [{
					xtype:'erpComponentGrid',
					anchor: '100% 100%'					
				}],
				bbar: ['->',{
						text:'关闭',
						cls: 'x-btn-gray',
						iconCls: 'x-button-icon-close',
						listeners: {
							click: function(btn){
								 btn.up('window').close();
							}
						}
					},'->'],
				listeners:{
					beforeshow:function(win){
						var g = win.down('erpComponentGrid');					
						g.store.loadData(me.data);	
						dataCount = data.length;
						Ext.getCmp('pagingtoolbar').afterOnLoad();
					}
				}
		  });		 
		}	
		 win.show(); 
	},
	getByUUid:function(str,callback){
		Ext.Ajax.request({//获取匹配结果
			 url : basePath + "scm/product/getByUUIds.action",
			 params :{
			     ids:str
			 },
			 method : 'post',
			 callback : function(opt, s, res){
			 	var r = new Ext.decode(res.responseText);
			 	if(r && r.exceptionInfo){
			 		showError(r.exceptionInfo);
				 }else{
	    			callback && callback.call(null, r.gridStore); 					    					
				 }
			   }
		});		
	},
	cleanDetail: function(){
		var grid=Ext.getCmp('grid'); 
		grid.setLoading(true);
		 Ext.Ajax.request({
	   		url :basePath+ 'scm/product/clearUUDetail.action',
	   		params: {
	   			id:Ext.getCmp('em_id').value,
	   			caller  : caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){ 
	   			grid.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '操作成功!', 1000);
    				//update成功后刷新页面进入可编辑的页面
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){ 
        			showError(localJson.exceptionInfo);return;
        		}
	   		}
		});		
	},
	onGridItemClick: function(selModel, record){//grid行选择
		var btn= Ext.getCmp('getb2cproductkind');
		if(btn && btn.disabled){
		   btn.setDisabled(false);
		}
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    batchUpdateUUId:function(data){
    	var me = this;
    	//有修改值  	
    	me.FormUtil.setLoading(true); 
		 Ext.Ajax.request({
	   		url :basePath+ 'scm/product/batchUpdateUUId.action',
	   		params: {
	   			param:unescape(data)
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){ 
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '操作成功!', 1000);
    				//update成功后刷新页面进入可编辑的页面
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){ 
        			showError(localJson.exceptionInfo);return;
        		}
	   		}
		});		
    },
    getUUid:function(){
    	var me = this;
    	//有修改值  	
    	me.FormUtil.setLoading(true); 
		 Ext.Ajax.request({
	   		url :basePath+ 'scm/product/getUUId.action',
	   		params: {
	   			caller  : caller
	   		},
	   		method : 'post',
	   		timeout:300000,
	   		callback : function(options,success,response){ 
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '操作成功!', 1000);
    				//update成功后刷新页面进入可编辑的页面
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){ 
        			showError(localJson.exceptionInfo);return;
        		}
	   		}
		});		
    },
    removeUUId:function(){
    	//请勾选需要解除关系的物料
    	var grid = Ext.getCmp("grid");
    	grid.multiselected=[];
        var items = grid.selModel.getSelection();
        if(grid.selectall) {
        	items = grid.store.prefetchData.items;
        }
    	if(items.length>0){
        	Ext.each(items, function(item, index){
	        	if(this.data['pub_uuid'] != null && this.data['pub_uuid'] != ''
	        		&& this.data['pub_uuid'] != '0' && this.data['pub_uuid'] != 0){
	        		item.index = this.data['pub_uuid'];
	        		grid.multiselected.push(item);        		
	        	}
	        });
    	 }else{
    	 	showError("请选择需要解除匹配关系的明细");return;
    	 }
    	 var records = Ext.Array.unique(grid.multiselected);
    	 if(records.length > 0){
			var params = new Object();
			params.caller = caller;
			var data = new Array();
			var bool = false;
			Ext.each(records, function(record, index){
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} 
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								o[f] = v;
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool){
		    	var me = this;
		    	//有修改值  	
		    	me.FormUtil.setLoading(true); 
				 Ext.Ajax.request({
			   		url :basePath+ 'scm/product/removeUUId.action',
			   		params: {
			   			caller  : caller,
		   				data: unescape(Ext.JSON.encode(data).replace(/\\/g,"%")),
		   				code :code,
		   				_noc:1
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){ 
			   			me.FormUtil.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
		    				showMessage('提示', '操作成功!', 1000);
		    				//update成功后刷新页面进入可编辑的页面
		    				window.location.reload();
			   			} else if(localJson.exceptionInfo){ 
		        			showError(localJson.exceptionInfo);return;
		        		}
			   		}
				});	
			}
    	 }
    }    
});