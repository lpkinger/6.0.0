Ext.QuickTips.init();
Ext.define('erp.controller.ma.update.UpdateScheme', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'core.form.Panel','core.grid.Panel2','ma.update.UpdateScheme','core.trigger.AddDbfindTrigger','ma.update.UpdateSchemeTree','core.trigger.MultiDbfindTrigger',
   		'core.button.Save','core.button.Add','core.button.Delete','core.button.Update','core.trigger.SearchField','core.button.HistoryUpdate','core.button.Sync', 'core.grid.TfColumn',
		'core.grid.YnColumn'
   	],
    init:function(){
    	var me = this;
    	this.flag = true; 
    	this.control({
    		'field[name=empnames_]':{
				afterrender:function(f){
					Ext.apply(f, {
						 extend: 'Ext.form.field.Trigger',
    				     triggerCls: 'x-form-search-trigger',
    				     selecteddata:new Array(),
    				     initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true,
    							beforetrigger: true
    					   });
    					   this.callParent(arguments);  
    				   },
    				    onTriggerClick: function() {
    				    	var dbwin=Ext.getCmp('empwin');
    				    	if(dbwin){
    				    		return;
    				    	}else{
    				    		dbwin=me.createWin();
								dbwin.show(); 
    				   		 }
    				    }
					});
				}
    		},
    		'erpGridPanel2':{
    			reconfigure:function(){
    				var id=Ext.getCmp('id_').value;
    				if(id){
	    				var grid=Ext.getCmp('grid');
						grid.getSelectionModel().selectAll();
						this.getOtherData(id);
    				}
    			
    			},
	    		storeloaded:function(){
	    			var grid=Ext.getCmp('grid');
					grid.getSelectionModel().selectAll();
				}
    		},
    		'upstreepanel':{
    			itemmousedown:function(selModel, record){
	    			 if (!this.flag) {
	                        return;
	                    }
	                 this.flag = false;
	                 setTimeout(function() {
	                        me.flag = true;
	                        me.loadData(selModel, record);
	                 }, 20);
    			}
    		},
    		'erpHistoryUpdateButton':{
	    		afterrender:function(b){
	    			if(!Ext.getCmp('id_').value){
	    				b.hide();
	    			}
	    		},
	    		click: function(btn){
	    			var id=Ext.getCmp('id_').value;
			    	var w = Ext.create('Ext.Window', {
			    		width: '60%',
			    		height: '80%',
			    		id: 'history-win',
			    		title: '更新数据历史记录',
			    		autoShow: true,
			    		layout: 'anchor',
			    		items: [{
			    			xtype: 'gridpanel',
			    			anchor: '100% 100%',
			    			columnLines: true,
			    			columns: [{dataIndex: 'ul_id', hidden: true},{dataIndex: 'ul_usid', hidden: true},
			    					  {dataIndex: 'ul_man', text: '更新人编号', flex: 1},
			    					  {dataIndex: 'ul_date', text: '日期', flex: 2},
			    			          {dataIndex: 'ul_count', text: '数据量(条)', flex: 1},
			    			          {dataIndex: 'ul_checked', text: '是否校验通过', flex: 1, renderer: function(val, m){
			    			        	  if(val == '否') {
			    			        		  m.style = 'float:right';
			    			        	  }
			    			        	  return val;
			    			          }}, {dataIndex: 'ul_success', text: '是否更新成功', flex: 1, renderer: function(val, m){
			    			        	  if(val == '否') {
			    			        		  m.style = 'float:right';
			    			        	  }
			    			        	  return val;
			    			          }}],
			    			store: Ext.create('Ext.data.Store', {
			    				fields: ['ul_id', 'ul_usid', 'ul_man', 'ul_date', 'ul_count', 'ul_checked', 'ul_success'],
			    				data: [{},{},{},{},{},{},{},{}]
			    			})
			    		}]
			    	});
			    	me.getUpdateLog(w.down('gridpanel'),id);
	    		}
    		},
    		'erpUpdateButton': {
    			afterrender:function(b){
    				if(!Ext.getCmp('id_').value){
    					b.hide();
    				}
    			},
    			click: function(btn){
    				this.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender:function(b){
    				if(!Ext.getCmp('id_').value){
    					b.hide();
    				}
    			},
    			click: function(btn){
    				me.onDelete(Ext.getCmp('id_').value);
    			}
    		},
            'erpSyncButton': {
    			afterrender:function(b){
    				if(!Ext.getCmp('id_').value){
    					b.hide();
    				}
    			}
            },
    		'erpAddButton': {
    			afterrender:function(b){
    				if(!Ext.getCmp('id_').value){
    					b.hide();
    				}
    			},
				click: function(){
					var form=Ext.getCmp('form');
					Ext.each(form.items.items,function(item){
						item.setValue("");
					});
					var grid=Ext.getCmp('grid');
					grid.store.removeAll();
        			me.GridUtil.add10EmptyItems(grid,40);
        			Ext.getCmp('addbtn').hide();
					Ext.getCmp('deletebutton').hide();
					Ext.getCmp('save').show();					
					Ext.getCmp('syncbtn').hide();
					Ext.getCmp('updatebutton').hide();
					Ext.getCmp('historyupdate').hide();
				}
			},
    		'erpSaveButton': {
    			afterrender:function(b){
    				if(Ext.getCmp('id_').value){
    					b.hide();
    				}
    			},
				click: function(btn){
					//保存之前的一些前台的逻辑判定
					this.beforeSave(this);
				}
			},
    		'field[name=indexfields_]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='table_';
	    			trigger.mappingKey='table_name';
	    			trigger.dbMessage='请先选择更新表';
    			}
    		},
    		'dbfindtrigger[name=table_]':{
    			aftertrigger:function(trigger){
    				var grid = Ext.getCmp('grid');
    				var table=trigger.value;
			    	Ext.Ajax.request({//查询数据
		  					url : basePath + '/ma/getColumns.action',
							params:{
						 		 tablename:table
							},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);	
								var count=0;
						 		if(res.data){
						 			var store = grid.store;
									var arr=res.data;
									store.loadData(arr, false);
									var i = 0;
									store.each(function(item, x){
										if(item.index) {
											i = item.index;
										} else {
											if (i) {
												item.index = i++;
											} else {
												item.index = x;
											}
										}
									});
								 } else if(res.exceptionInfo){
							    	 showError(res.exceptionInfo);
								 }
						 }
					 });
    			}
    		}
    	});
    },
    beforeSave:function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.FormUtil.checkForm()){
			return;
		}
		
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var grid = Ext.getCmp('grid');
		var jsonGridData = new Array();
		if(grid) {
			
        var items=grid.store.data.items;
        var flag=0;
        Ext.Array.each(items,function(item,index){//数据顺序是否变化
        	if(item.data.checked_!=0||item.data.checked_==true){
        		if(item.data.detno_!=(index+1)){
        			flag=1;
     		}            		
        	}            	
        });            
        var i=1,j=0,jsonGridData=new Array(),s=new Array();
        Ext.Array.each(items,function(item,index){
    		if(item.dirty){
				j++;
				}
        	if(item.data.checked_!=0||item.data.checked_==true){
                item.data.detno_=i;
                item.data.checked_=1;
                s.push(item.data);
                i++;
            }
        });
        
        	if(i==1){
			showError('请勾选明细行');return;
		}else{
		
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i];
				dd = new Object();
				Ext.each(grid.columns, function(c){
					if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
						if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
								dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							} else {
								dd[c.dataIndex] = "" + s[i][c.dataIndex];
							}
						} else {
							dd[c.dataIndex] = s[i][c.dataIndex];
						}
						if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
							dd[c.dataIndex] = c.defaultValue;
						}
					}
				});
				if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
					dd[grid.mainField] = Ext.getCmp(form.keyField).value;
				}
				
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		
		
		}
        }
		param1 = jsonGridData == null ? [] : "[" + jsonGridData.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.save(r, param1);
			}else{
				me.FormUtil.checkForm();
			}		
    },
    save:function(){	
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form'),url = form.saveUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		};
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入可编辑的页面 
						var value =r[form.keyField];
						window.location.href =basePath+'jsps/ma/update/updateScheme.jsp?formCondition=id_IS' + value+ '&gridCondition=Scheme_Id_IS'+value;
					});
				} else if(localJson.exceptionInfo){
	   					var str = localJson.exceptionInfo;
	   					showError(str);
		   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	},
	loadData: function(selModel, record) {
		me=this;
		var form=Ext.getCmp('form');
		var grid = Ext.getCmp('grid');
		var id=record.data['id'];
		Ext.Ajax.request({//查询数据
			url : basePath + '/ma/getUpdateScheme.action',
			params:{
		 		 id:id
			},
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);	
				if(res.success){
			 		if(res.data){
			 			var formdata=res.data.formdata;
			 			var griddata=Ext.decode(res.data.griddata);
			 			var d = Ext.decode(formdata);
						form.getForm().setValues(d);
						grid.store.loadData(griddata);
						grid.fireEvent('storeloaded', grid, griddata);
						if(res.data.otherdatas){
							var store = grid.store;
							var arr=res.data.otherdatas;
							store.loadData(arr, true);
							var i = 0;
							store.each(function(item, x){
								if(item.index) {
									i = item.index;
								} else {
									if (i) {
										item.index = i++;
									} else {
										item.index = x;
									}
								}
							});
						}
						Ext.getCmp('helpFunction').hide();
						Ext.getCmp('addbtn').show();
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('save').hide();
						Ext.getCmp('updatebutton').show();
						Ext.getCmp('syncbtn').show();
						Ext.getCmp('historyupdate').show();
						var w=Ext.getCmp('history-win');
						if(w){
							var id=Ext.getCmp('id_').value;
							me.getUpdateLog(w.down('gridpanel'),id);
						}
			 		}
			 } else if(res.exceptionInfo){
		    	 showError(res.exceptionInfo);
			 }
		 }
	 });
	},
	onUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var s1=me.FormUtil.checkFormDirty(form);
		if(!me.FormUtil.checkForm){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var grid = Ext.getCmp('grid');
		var jsonGridData = new Array();
		if(grid) {
            var items=grid.store.data.items;
            var flag=0;
            Ext.Array.each(items,function(item,index){//数据是否变化位置
            	if(item.data.checked_!=0||item.data.checked_==true){
            		if(item.data.detno_!=(index+1)){
            			flag=1;
         		}            		
            	}            	
            });            
            var i=1,j=0,jsonGridData=new Array(),s=new Array();
            Ext.Array.each(items,function(item,index){
        		if(item.dirty){
    				j++;
    				}
            	if(item.data.checked_!=0||item.data.checked_==true){
                    item.data.detno_=i;
                    item.data.checked_=1;
                    s.push(item.data);
                    i++;
                }
            });
			if(s1==''&&j==0&&flag==0){
			showError('未更改数据');return;
			}else if(i==1){
				showError('请勾选明细行');return;
			}else{
			
				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
					var data = s[i];
					dd = new Object();
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i][c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i][c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			
			
			}
            }
			param = jsonGridData == null ? [] : "[" + jsonGridData.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.update(r, param);
			}else{
			me.FormUtil.checkForm();
		}	
	},
	update:function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form') ,url = form.updateUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		};
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '更新成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					var u = String(window.location.href);
					var value = r[form.keyField];
					window.location.href =basePath+'jsps/ma/update/updateScheme.jsp?formCondition=id_IS' + value+ '&gridCondition=Scheme_Id_IS'+value;
				} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				showError(str);
		   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		} 		
		});
	},
	getOtherData: function(id){
		var me = this;
		var grid=Ext.getCmp('grid');
  		Ext.Ajax.request({
        	url : basePath + 'ma/getOtherData.action',
        	async: false,
        	params: {
        		id:id
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		} else if(res.success) {
        			if(res.data){
						var store = grid.store;
						var arr=res.data;
						store.loadData(arr, true);
						var i = 0;
						store.each(function(item, x){
							if(item.index) {
								i = item.index;
							} else {
								if (i) {
									item.index = i++;
								} else {
									item.index = x;
								}
							}
						});
					}
        		}
        	}
		});
	},
    getUpdateLog: function(g,id){  	
    	Ext.Ajax.request({
    		url: basePath + 'ma/update/updateHistory.action',
    		params: {
    			id: id
    		},
    		method: 'post',
    		callback: function(opt, s, r){
    			var res = new Ext.decode(r.responseText);
    			var dd = res.data;
    			Ext.each(dd, function(d){
    				d.ul_date = Ext.Date.format(new Date(d.ul_date), 'Y-m-d H:i:s');
    				d.ul_checked = d.ul_checked == 1 ? '是' : '否';
    				d.ul_success = d.ul_success == 1 ? '是' : '否';
    			});
    			g.store.loadData(dd);
    		}
    	});
    },
	onDelete: function(id){
		var me = this;
		warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form'),url = form.deleteUrl;
				if(url.indexOf('caller=') == -1){
					url = url + "?caller=" + caller;
				};
				form.setLoading(true);
				Ext.Ajax.request({
					url : basePath + url,
					params: {
						id: id
					},
					method : 'post',
					callback : function(options,success,response){
						form.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							window.location.href =basePath+'jsps/ma/update/updateScheme.jsp';
						} else {
							delFailure();
						}
					}
				});
			}
		});
	},
	confirm:function(){
		var win=Ext.getCmp('empwin');
		var select=Ext.getCmp('empnames_').selecteddata;
		if(select.length==0){
			showError('未选择数据');
		}else{
			var codes='',names='';
			Ext.each(select,function(s){
				codes+=s.em_code+"#";
				names+=s.em_name+"#";
			});
			Ext.getCmp('emps_').setValue(codes.substring(0,codes.length-1));
			Ext.getCmp('empnames_').setValue(names.substring(0,names.length-1));
			win.close();
		}
	},
	createWin: function() {
		var me=this;
     	var dbwin = new Ext.window.Window({
			title: '查找',
			height: "100%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			id:'empwin',
			//resizable:false,
			items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_empdbfind" src="'+basePath+'jsps/ma/update/empdbfind.jsp" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
			}],
			buttons : [{
			   		text : '确认',
			   		iconCls: 'x-button-icon-save',
			   		cls: 'x-btn-gray',
			   		handler : function(b){
			   			me.confirm();
			     	}
			     },{
			   	    text : '关  闭',
			   	   	iconCls: 'x-button-icon-close',
			   	   	cls: 'x-btn-gray',
			   	   	handler : function(b){
			   	   		b.ownerCt.ownerCt.close();
			   	}
		  }]
      	});
      	return dbwin;
	}
});