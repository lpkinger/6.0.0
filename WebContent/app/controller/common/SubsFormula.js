Ext.QuickTips.init();
Ext.define('erp.controller.common.SubsFormula', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
	'core.form.Panel','common.subs.SubsFormula','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
	'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
	'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	'core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
	'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd',
	'core.form.MonthDateField','core.form.SpecialContainField','core.button.Test','erp.view.core.button.DYpreview'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);   
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					} 
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('id_').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSubsFormula', '新增订阅项', 'jsps/common/subsformula.jsp');
    			}
    		},
    		'erpDYpreviewButton':{
    			click: function(btn){
    				var form = Ext.getCmp('form');
    				this.showwindow(form);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onAudit(Ext.getCmp('id_').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResAudit(Ext.getCmp('id_').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onSubmit(Ext.getCmp('id_').value);

				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResSubmit(Ext.getCmp('id_').value);
				}
			},
			'erpTestButton': {  
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
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
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	showwindow: function(form,grid){
		var id = form.getValues().id_
		var url = 'common/charts/mobilePreviews.action?id='+id;
		if (Ext.getCmp('DYpreviews')) {
			Ext.getCmp('DYpreviews').setTitle(title);
			}
		else {
		var DYpreviews = new Ext.window.Window({
		   id : 'DYpreviews',
		   title: '预览',
		   height: "100%",
		   width: "80%",
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