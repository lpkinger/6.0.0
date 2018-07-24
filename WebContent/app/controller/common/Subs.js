Ext.QuickTips.init();
Ext.define('erp.controller.common.Subs', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil','erp.view.core.grid.HeaderFilter','erp.view.core.plugin.CopyPasteMenu'],
    views:[
	'core.form.Panel','common.subs.SubsFormula','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
	'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
	'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	'core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
	'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.TextAreaSelectField',
	'core.form.MonthDateField','core.form.SpecialContainField','core.button.Test','common.subsFormula.GridPanel','common.subsFormula.Form','common.subsFormula.Viewport'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil=Ext.create('erp.util.FormUtil');
    	me.GridUtil=Ext.create('erp.util.GridUtil');
    	me.BaseUtil=Ext.create('erp.util.BaseUtil');
    	var flag = true;
    	this.control({
    		'#grid': { 
    			itemclick: this.onGridItemClick   	,
    			containerclick:function(){
    				if(flag)
    				me.containerClick();
    				flag = false;
    			},
    			afterrender:function(grid){
    				 var me=this;
					 var gridStore=grid.getStore();
					 if(gridCondition){
						   gridStore.load({
						   params :{
				                caller: caller, 
					        	formulaId:gridCondition.split("=")[1],
					        	_noc: (getUrlParam('_noc') || me._noc)
				             }
					   });
		  			 }
		  			var form = Ext.getCmp('form');
    				form.on('afterload', function(){
        					grid.readOnly = !!form.readOnly;
        			});
    			}
    		},
    		'subsForm':{
    		},
    		'#statuscode_':{
    			afterrender:function(f){f.value},
    		    change:function(field,newvalue){
    		      var form=field.ownerCt,toolbar=form.down('toolbar');
    		      if(newvalue && toolbar){
    		        switch(newvalue){
    		          case 'COMMITED': 
    		             toolbar.down('erpResSubmitButton').show();
    		             toolbar.down('erpAuditButton').show();
    		             toolbar.down('erpAddButton').show();
    		             toolbar.down('erpSaveButton').hide();
    		             toolbar.down('erpUpdateButton').hide();
    		             toolbar.down('erpSubmitButton').hide();
    		             toolbar.down('erpDeleteButton').hide();
    		             toolbar.down('erpTestButton').hide();
    		             toolbar.down('#preview').hide();
    		            break;
    		           case 'AUDITED':
    		            toolbar.down('erpAddButton').show();
    		            toolbar.down('erpSaveButton').hide();
    		            toolbar.down('erpUpdateButton').hide();
    		        	toolbar.down('erpSubmitButton').hide();
    		            toolbar.down('erpResSubmitButton').hide();
    		            toolbar.down('erpDeleteButton').hide();
    		            toolbar.down('erpAuditButton').hide();
    		            toolbar.down('#preview').show();
    		            toolbar.down('erpResAuditButton').show();
    		            toolbar.down('erpTestButton').show();
    		            break;
    		           default:
    		            toolbar.down('erpAddButton').show();
    		            toolbar.down('erpUpdateButton').show();
    		            toolbar.down('erpDeleteButton').show();
    		            toolbar.down('erpSubmitButton').show();
    		            toolbar.down('erpSaveButton').hide();
    		            toolbar.down('erpResSubmitButton').hide();
    		            toolbar.down('erpAuditButton').hide();
    		            toolbar.down('erpResAuditButton').hide();
    		            toolbar.down('erpTestButton').hide();
    		            toolbar.down('#preview').hide();
    		        }
    		      }
    		    }
    		},
    		'erpSaveButton' : {
				click : function(btn) {
					if (Ext.getCmp('form').getValues().code_ == null
							|| Ext.getCmp('form').getValues().code_ == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					} 
						if(form.keyField){
						if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
							me.FormUtil.getSeqId(form);
						}
					}
					Ext.getCmp('statuscode_').setValue('ENTERING');
    				this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('id_').value);
				}
			},
    		'button[id="preview"]' : {
				click : function(btn) {
					var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				this.showwindow(form,grid);
				}
			},'erpCloseButton' : {
				click : function(btn) {
				   btn.ownerCt.ownerCt.ownerCt.hide();
				}
			},'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},'erpAddButton' : {
				click : function(btn) {
				 	me.FormUtil.onAdd('addSubsFormula', '新增订阅项', 'jsps/common/subs.jsp');
				}
			},
			'erpAuditButton' : {
				click : function(btn) {
					this.FormUtil.onAudit(Ext.getCmp('id_').value);
				}
			},'erpResAuditButton' : {			 
				click : function(btn) {
					this.FormUtil.onResAudit(Ext.getCmp('id_').value);
				}
			},'erpSubmitButton' : {
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('id_').value);
				}
			},'erpResSubmitButton' : {
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('id_').value);
				}
			},'erpTestButton': {  
    			click: function(btn){
    				Ext.Ajax.request({
    					url: basePath + 'common/charts/testSubsFormula.action',
    					params: {
    						id: Ext.getCmp('id_').value,
    						caller:caller
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						var res = new Ext.decode(response.responseText);
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    						} else {
    		                    Ext.MessageBox.confirm('提示', '测试通过,是否需要预览数据?', del);
    		                    function del(btn) {
    		                        if(btn == 'yes'){
    		                        	// 弹出win预览数据
    		                        	var win = new Ext.window.Window({
    		                    			title: '预 览',
    		                    			height: "100%",
    		                    			width: "80%",  
    		                    			maximizable : true,
    		                    			resizable:true,
    		                    			buttonAlign : 'center',
    		                    			layout : 'anchor',
    		                        		bodyStyle: 'background:#f1f1f1;',
    		                        		items:[{
	    				   xtype: 'form',
	    				   id:'paramsForm',
	    				   anchor: '100% 20%',
	    				   autoScroll:true,
	    				   bodyStyle: 'background:#f1f1f1;',
	    				   layout: 'column',
	    				   items:[ { 
                			   margin: '5 0 0 5',
                               xtype: 'textfield',
                               fieldLabel: '员工ID',
                               name: 'EMID',
                               value:  EMID,
                               id: 'EMID'
                            },{ 
                               margin: '5 0 0 5',
                               xtype: 'textfield',
                               fieldLabel: '编号',
                               name: 'EMCODE',
                               value:  EMCODE,
                               id: 'EMCODE'
                            },{ 
                               margin: '5 0 0 5',
                               xtype: 'textfield',
                               fieldLabel: '姓名',
                               name: 'EMNAME',
                               value:  EMNAME,
                               id: 'EMNAME'
                            },{ 
                               margin: '5 0 0 5',
                               xtype: 'textfield',
                               fieldLabel: '组织',
                               name: 'EMDEFAULTORNAME',
                               value:  EMDEFAULTORNAME,
                               id: 'EMDEFAULTORNAME'
                            },{ 
                               margin: '5 0 0 5',
                               xtype: 'textfield',
                               fieldLabel: '部门',
                               name: 'EMDEPART',
                               value:  EMDEPART,
                               id: 'EMDEPART'
                            },{ 
                               margin: '5 0 0 5',
                               xtype: 'numberfield',
                               fieldLabel: '预览行数',
                               minValue: 0,
                               allowDecimals:false,
                               name: 'RN',
                               value:  10,
                               id: 'RN'
                            }]},{
                            	xtype: 'grid',
                				anchor: '100% 80%',
                		        id : 'configGrid',    
                	            name : 'configGrid',    
                	            columns : [],       
                	            emptyText : "暂无数据", 
                	            store:[],
                	            items : []                  	            
                            }],
	                		buttons:[{
	            				text : '预览',
	            				flag : 'confirm',
	            				height : 26,
	            				handler : function(b) { 
	            					var params=Ext.getCmp('paramsForm').getValues();
	            					var keys = Ext.Object.getKeys(params);
	            					var reg = /[!@#$%^&*()'":,\/?]/;
	            					Ext.each(keys, function(k){
	            						params[k] = params[k].trim().toUpperCase().replace(reg, '');
	            					});    		                    					
	            					Ext.Ajax.request({
	                					url: basePath + 'common/charts/getPreviewDatas.action',
	                					params: { 
	                						id: Ext.getCmp('id_').value,
	                						caller:caller,
	                						params:Ext.JSON.encode(params)
	                					},
	                					method: 'post',
	                					callback: function(options, success, response){
	                						var res = new Ext.decode(response.responseText);
	                						if(res.exceptionInfo) {
	                							showError(res.exceptionInfo);
	                						} else {
                							 var datas = res.datas; //获得后台传递json     		                       							 
                								 var fieldsNames=[];   
                    							 var columModle=[];
                    							 if (datas.length>0){
                    								 for(var item in datas[0]){  
                    						    	   fieldsNames.push({"name":item});
                    						    	   columModle.push({"header":item,"dataIndex":item,"flex":1,"draggable":false});
                    						       }  
                    							 }        		               						      
                    					           var store = Ext.create('Ext.data.Store', {    
                    					           fields :fieldsNames,  
                    					           data : datas         
                    					           });     
                    					           Ext.getCmp("configGrid").reconfigure(store, columModle);  //定义grid的store和column    
                    					           Ext.getCmp("configGrid").render();   
                							 }    		                        							
	                						
	                					}});
	            					}
	            			},{
	            				text : '关  闭',
	            				flag : 'cancel',
	            				height : 26,
	            				handler : function(b) {
	            					b.ownerCt.ownerCt.close();
	            				}
	            			}]
	            		});
	            					win.show();
    		                        }else window.location.reload();
    		                    }    		                    							
    						}
    					}
    				});
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    containerClick:function(container){
    	var data=[];
    	var grid = Ext.getCmp('grid');
    	var itemslength = grid.store.data.items.length;
   	   if(itemslength==0){
    	this.add10EmptyItems(grid, 10,false);
    }
    },
	getForm: function(btn){
		return btn;
	},
add10EmptyItems: function(grid, count, append){
		var store = grid.store, 
			items = store.data.items, arr = new Array();
		var detno = grid.detno;
		count = count || 10;
		append = append === undefined ? true : false;
		if(typeof grid.sequenceFn === 'function')
			grid.sequenceFn.call(grid, count);
		else {
			if(detno){
				var index = items.length == 0 ? 0 : Number(store.last().get(detno));
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o[detno] = index + i + 1;
					arr.push(o);
				}
			} else {
				for(var i=0;i < count;i++ ){
					var o = new Object();
					arr.push(o);
				}
			}
			store.loadData(arr, append);
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
	},
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<20;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<20;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
	showwindow: function(form,grid){
		var store = grid.getStore();
		var id = Ext.getCmp('id_').value;
		var title = form.getValues().title_;
		var url = 'common/charts/mobilePreviews.action?id='+id;
		if (Ext.getCmp('DYpreviews')) {
			Ext.getCmp('DYpreviews').setTitle(title);
			}
		else {
		var DYpreviews = new Ext.window.Window({
		   id : 'DYpreviews',
		   title: '订阅项详情',
		   height: "100%",
		   width: "60%",
		   resizable:false,
		   modal:true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   items: [{
			   tag : 'iframe',
			   frame : true,
			   anchor : '100% 100%',
			   layout : 'fit',
			   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
		   }],
		   buttons : [{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(){
				   Ext.getCmp('DYpreviews').close();
			   }
		   }]
	   });
		DYpreviews.show();}
	}
});