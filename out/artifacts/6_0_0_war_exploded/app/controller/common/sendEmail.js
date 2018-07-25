Ext.QuickTips.init();
Ext.define('erp.controller.common.sendEmail', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
    		'core.form.Panel','core.form.FtField','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
    		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
    		'core.form.FtNumberField','core.form.MonthDateField','core.button.AddDetail','core.button.Save','core.button.Add',
    		'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.Update','core.button.Delete',
    		'core.button.ResSubmit','core.button.Submit'
    	],
    	 init:function(){
    		var me = this;
    		var i = 0;
	        this.BaseUtil = Ext.create('erp.util.BaseUtil');
	        this.FormUtil = Ext.create('erp.util.FormUtil');
	        this.GridUtil = Ext.create('erp.util.GridUtil');
	    	this.control({
	    		'erpGridPanel2': {
	                 itemclick: me.onGridItemClick
	             },
	             'multidbfindtrigger':{
	            	 aftertrigger: function(){
	            		var newValue = Ext.getCmp('se_address').value;
//	            		newValue = newValue.replace(/#/g,";");
	            		var reciveName = Ext.getCmp('se_reciveman').value;
	            		var arr1 = newValue.split("#"), arr2 = reciveName.split("#");
	            		if(arr1.length != arr2.length){
	            			Ext.Msg.alert("提示", "选择的记录中有重复邮箱");
	            			return;
	            		}
	            		var array = [];
	            		for(var i = 0; i < arr1.length; i++){
	            			array.push(arr2[i] + '<' + arr1[i] + '>');
	            		}
	            		var value = array.join(';');
	            		Ext.getCmp('se_address').setValue(value);
	            	 } 
	             },
	    		'toolbar': {
	    			afterrender: function(){
	    				var form = Ext.getCmp('form');
	    				var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value == 'ENTERING') {
	                    	var toolbar = form.dockedItems.items[0];
	                    	if(toolbar){
		    					if(!Ext.getCmp('fileButton')){
	    							toolbar.insert(toolbar.items.length-2,{
		    							xtype: 'button',
		    							id: 'fileButton',
		    							cls:'x-btn-gray',
		    							text: '浏览',
		    							handler: function(){
		    								me.getData();
		    							}
		    						});
		    					}
	                    	}
	                    }
	    			}
	    		},
	    		/*'erpGridPanel2':{
	    			beforeedit: function(e){
	    				me.getData();
	    			}
	    		},*/
	    		'button[id=confirm]':{
	    			click: function(btn){
	    				//获取选中的值
	    				var tree = btn.ownerCt.ownerCt.child('#fileTree');
	    				var records = tree.getView().getChecked(), ids = [];
	    				Ext.Array.each(records, function(rec){
	                        ids.push(rec.get('id'));
	                    });
	    				me.getFileInfo(ids);
	    				btn.ownerCt.ownerCt.close();
	    			}
	    		},
	    		'erpSaveButton': {
	                click: function(btn) {
	                	var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
						if(codeField.value == null || codeField.value == ''){
							me.BaseUtil.getRandomNumber(caller);//自动添加编号
						}
	                    //保存
						this.FormUtil.beforeSave(this);
	                }
	            },
	            'erpDeleteButton': {
	                click: function(btn) {
	                    me.FormUtil.onDelete(Ext.getCmp('se_id').value);
	                }
	            },
	            'erpUpdateButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value != 'ENTERING') {
	                        btn.hide();
	                    }
	                },
	                click: function(btn) {
	                	this.FormUtil.onUpdate(this);
	                }
	            },
	            'erpAddButton': {
	                click: function() {
	                    me.FormUtil.onAdd('addsendEmail', '文档上传', 'jsps/common/sendEmail.jsp');
	                }
	            },
	            'erpCloseButton': {
	                click: function(btn) {
	                    me.FormUtil.beforeClose(me);
	                }
	            },
	            'erpSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value != 'ENTERING') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                	lock: 2000,
		                fn:function(btn) {
		                	this.FormUtil.onSubmit(Ext.getCmp('se_id').value);
		                }
	                }
	            },
	            'erpResSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                    lock: 2000,
		                fn:function(btn) {
	                        me.FormUtil.onResSubmit(Ext.getCmp('se_id').value);
		                }
	                }
	            },
	            'erpAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                    	me.FormUtil.onAudit(Ext.getCmp('se_id').value);
		                }
	                }
	            },
	            'erpResAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('se_statuscode');
	                    if (status && status.value != 'AUDITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                  	  	me.FormUtil.onResAudit(Ext.getCmp('se_id').value);
		                }
	                }
	            }
	    		
	    	})
    	 },
	getForm: function(btn) {
	    return btn.ownerCt.ownerCt;
	},
	getData: function(){
		var width = Ext.isIE ? screen.width * 0.7 * 0.4 : '50%',
				height = Ext.isIE ? screen.height * 0.9 : '90%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		Ext.Ajax.request({
			url:basePath + 'common/sendEmail/getMenuTree.action',
			params:{
				condition: "DL_STATUSCODE = 'AUDITED'" ,
				id: 0,
				checked: 'true'
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.tree){
						var store =Ext.create('Ext.data.TreeStore', {
							fields:['dl_name'],
							root : {
								text : 'Root',
								id : 0,
								expanded : true
							}
						});
						store.setRootNode({
							text: 'root',
							id: 'root',
							expanded: true,
							children: res.tree
						});	
						var tree = Ext.create('Ext.tree.Panel', {	
							id: 'fileTree',
							rootVisible : false,
							autoScroll:true,
							hideHeaders : true,
							border:false,
							store:store,
							columns : [{
								xtype : 'treecolumn',
								dataIndex:'dl_name',
								renderer:function(val,meta,record){
									return val;
								},
								sortable : true,
								flex : 1
							}]
						});
						var win = new Ext.window.Window({
							title:'文件信息 ',
							height: height,
							width: width,
							buttonAlign: 'center',
							layout: 'fit',
							modal:true,
							items: [tree],
							buttons: [{
								text: '确认',
								id: 'confirm',
								/*handler: function(btn) {									
									btn.ownerCt.ownerCt.close();
									//获取
								}*/
							},{
								text: '关  闭',
								handler: function(btn) {
									btn.ownerCt.ownerCt.close();
								}
							}]
						});
						win.show();
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
				
			}
		});
		
	},
	
	getFileInfo: function(ids){
		Ext.Ajax.request({
			url: basePath + 'common/sendEmail/getFileInfo.action',
			params:{
				ids: ids
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				var grid = Ext.getCmp('grid');
				if(res.success){
					if(res.fileList != null){
						//设置数据
						for(var j = 0; j < grid.store.data.items.length; j++){
							if(grid.store.data.items[j].data.sed_name == null || grid.store.data.items[j].data.sed_name == '')
								break;
						}
						for(var i = 0; i < res.fileList.length; i++){
							res.fileList[i].sed_detno = i + j + 1;
							res.fileList[i].sed_size = res.fileList[i].sed_size + 'K';
							var date = new Date(res.fileList[i].sed_uploaddate);
							res.fileList[i].sed_uploaddate = date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate();
							var data = JSON.stringify(res.fileList[i]);
							grid.store.data.items[i+j].data = JSON.parse(data);
							grid.store.data.items[i+j].modified = {sed_name: ''};
							grid.store.data.items[i+j].dirty = true;
						}
						grid.getView().refresh();
					}
				}
			}
		});
	},
	onGridItemClick: function(selModel, record) { //grid行选择	
        this.GridUtil.onGridItemClick(selModel, record);
    }
	
});
