Ext.QuickTips.init();
Ext.define('erp.controller.common.uploadDocument', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
    		'core.form.Panel','core.form.FtField','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
    		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
    		'core.form.FtNumberField','core.form.MonthDateField','core.button.AddDetail','core.button.Save','core.button.Add',
    		'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.Update','core.button.Delete',
    		'core.button.ResSubmit','core.button.Submit','core.trigger.AutoCodeTrigger','core.button.Upload','common.DocSetting.menuTree','core.trigger.DocMenuTrigger'
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
	    		'toolbar': {
	    			afterrender: function(){
	    				var form = Ext.getCmp('form');
	    				var status = Ext.getCmp('ud_statuscode');
	                    if (status && status.value == 'ENTERING') {
	                    	var toolbar = form.dockedItems.items[0];
	                    	if(toolbar){
		    					if(!Ext.getCmp('fileForm')){
		    						toolbar.insert(toolbar.items.length-2,{
			    						xtype: 'form',
			    						id: 'fileForm',
			    						bodyStyle: 'background:#fff;margin-top:-2px;',
//			    						border: '0',
			    						frame: false,
			    						border: false,
			    						items:[{
			    							xtype: 'filefield',
			    							id: 'fileBrower',
				    						name: 'file',
				    						buttonText: '浏览<font color=blue size=1>(≤100M)</font>...',
				    						buttonOnly: true,
				    						hideLabel: true,
				    						setValue: function(value){
				    							this.value = value;
				    						},
				    						listeners: {
				    							change: function(field){
				    								var value = Ext.getCmp('form').down('field[name=ud_directory]').getValue();
				    								if(!value){
				    									showError("请先选择目录!");
				    									field.setValue('');
//				    									return;
				    								}else{
				    									if(field.value != null){
					    									me.upload(field.ownerCt, field,i);
					    									i++;
					    								}
				    								}
				    								
				    							}
				    						}
			    						}]
			    						
			    					});
		    					}
		    				}
	                    }
	    			}
	    		},
	    		
	    		'menuTree': {
	    			itemmousedown: function(selModel, record){
	    				me.loadTab(selModel, record);
	    				me.lastSelected = record;
	    			}
	    		},
	    		'button[id=confirmMenu]': {
	    			click: function(btn){
	    				Ext.getCmp('form').items.get("ud_directory").setValue(Ext.getCmp('manuValue').value);
	    				Ext.getCmp('form').items.get("ud_prefixcode").setValue(Ext.getCmp('prefixCode').value);
	    				btn.ownerCt.ownerCt.close();
	    			}
	    		},
	    		'button[id=closeMenu]': {
	    			click: function(btn){
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
	                    me.FormUtil.onDelete(Ext.getCmp('ud_id').value);
	                }
	            },
	            'erpUpdateButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('ud_statuscode');
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
	                    me.FormUtil.onAdd('adduploadDocument', '文档上传', 'jsps/common/uploadDocument.jsp');
	                }
	            },
	            'erpCloseButton': {
	                click: function(btn) {
	                    me.FormUtil.beforeClose(me);
	                }
	            },
	            'erpSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('ud_statuscode');
	                    if (status && status.value != 'ENTERING') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                	lock: 2000,
		                fn:function(btn) {
		                	this.FormUtil.onSubmit(Ext.getCmp('ud_id').value);
		                }
	                }
	            },
	            'erpResSubmitButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('ud_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click: {
	                    lock: 2000,
		                fn:function(btn) {
	                        me.FormUtil.onResSubmit(Ext.getCmp('ud_id').value);
		                }
	                }
	            },
	            'erpAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('ud_statuscode');
	                    if (status && status.value != 'COMMITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                    	me.FormUtil.onAudit(Ext.getCmp('ud_id').value);
		                }
	                }
	            },
	            'erpResAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('ud_statuscode');
	                    if (status && status.value != 'AUDITED') {
	                        btn.hide();
	                    }
	                },
	                click:{ 
	    				lock: 2000,
		                fn: function(btn) {
	                  	  	me.FormUtil.onResAudit(Ext.getCmp('ud_id').value);
		                }
	                }
	            }
	    		
	    	})
    	 },
	 loadTab: function(selModel, record){
	    	var me = this;
	    	var tree = Ext.getCmp('tree-docMenu');
	    	var parentId='';
	    	if (record.get('leaf')) {
	    		parentId=record.data['parentId'];
	    	} else {
	    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
					record.collapse(true,true);//收拢
				} else {//未展开
					//看是否加载了其children
					if(record.childNodes.length == 0){
						//从后台加载
			            tree.setLoading(true, tree.body);
						Ext.Ajax.request({//拿到tree数据
				        	url : basePath + 'common/DocSetting/getDocTree.action',
				        	params: {
				        		parentid: record.data['id'],
				        	},
				        	async: false,
				        	callback : function(options,success,response){
				        		tree.setLoading(false);
				        		var res = new Ext.decode(response.responseText);
				        		if(res.tree){
				        			record.appendChild(res.tree);
				        			record.expand(false,true);//展开
				        		} else if(res.exceptionInfo){
				        			showError(res.exceptionInfo);
				        		}
				        	}
				        });
					} else {
						record.expand(false,true);//展开
					}
				}
	    	}
	    	tree.getExpandedItems(record);
	    	Ext.each(tree.expandedNodes, function(){
	    		if(!this.data['leaf'] && this.data['parentId']==parentId )
	    		this.collapse(true,true);
	    	});
	    	var manuName = '';
	    	var prefixCode = '';
	    	if(record.raw){
	    		value = record.raw.prefixcode;
	    	}
	    	tree.getExpandedItems(record);
	    	Ext.each(tree.expandedNodes, function(){
	    		manuName += '/' + this.data['text'];
	    		prefixCode += '-' + this.data['prefixcode'];
	    	}); 
	    	Ext.getCmp('manuValue').setValue(manuName.substring(1,manuName.length))
	    	var str = prefixCode.substring(1,prefixCode.length).replace("undefined",value);
	    	Ext.getCmp('prefixCode').setValue(str);
	    },
	getForm: function(btn) {
	    return btn.ownerCt.ownerCt;
	},
	upload: function(form, field,i){
		var me = this;
		var filename = '';
		var fileData = {};
		if(contains(field.value, "\\", true)){
			filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
		} else {
			filename = field.value.substring(field.value.lastIndexOf('/') + 1);
		}
		if(me.checkFile(filename)){
			showError('当前类型文件不允许上传!');
			return false;
		}
		var grid = Ext.getCmp('grid');
//		{udd_code:"",udd_detno:"1",udd_name:name,udd_size:"4",udd_type:"txt"}
		form.getForm().submit({
			url: basePath + 'common/upload.action?em_code=' + em_code+'&caller='+caller,
			waitMsg: "正在上传:" + filename,
			success: function(fp, o){
				Ext.Msg.alert("恭喜", filename + " 上传成功!");
				if(o.result.error){
					showError(o.result.error);
				}else {
					var size = o.result.size;
					var udd_code = Ext.getCmp('form').down('field[name=ud_prefixcode]').getValue() +'-' + o.result.filepath;	//上传文件的fpid
					var name = filename.substring(0,filename.lastIndexOf('.'));
					var type = filename.substring(filename.lastIndexOf('.') + 1);
					//构造data
					fileData.udd_code = udd_code;
					fileData.udd_remarks = '';
					fileData.udd_name = name;
					fileData.udd_type = type;
					fileData.udd_size = Ext.util.Format.round(size / 1024 ,2) + 'K';
					for(var j = 0; j < grid.store.data.items.length; j++){
						if(grid.store.data.items[j].data.udd_name == null || grid.store.data.items[j].data.udd_name == '')
							break;
					}
					i = j;
					fileData.udd_detno = i+1;
					grid.store.data.items[i].data = fileData;
					var obj = {};
					grid.store.data.items[i].modified = {udd_name: ''};
					grid.store.data.items[i].dirty = true;
					grid.getView().refresh();
				}
			}
		});
	},
	checkFile:function(fileName){
		var arr=['php','php2','php3', 'php5', 'phtml', 'asp', 'aspx', 'ascx', 'jsp', 'cfm', 'cfc', 'pl','pl','bat',  'dll', 'reg', 'cgi','war'];
	    var suffix=fileName.substring(fileName.lastIndexOf(".")+1);
	    return Ext.Array.contains(arr,suffix);
	},
	onGridItemClick: function(selModel, record) { //grid行选择	
        this.GridUtil.onGridItemClick(selModel, record);
    }
	
	
});
