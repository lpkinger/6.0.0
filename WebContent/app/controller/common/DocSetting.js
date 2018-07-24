Ext.QuickTips.init();
Ext.define('erp.controller.common.DocSetting', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.DocSetting.Viewport','core.form.Panel','core.form.FtField','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
     		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
     		'core.form.FtNumberField','core.form.MonthDateField','core.button.AddDetail','core.button.Save','core.button.Add',
     		'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.Update','core.button.Delete',
     		'core.button.ResSubmit','core.button.Submit','core.trigger.AutoCodeTrigger','common.DocSetting.menuTree','core.trigger.DocMenuTrigger'
     	],
    init:function(){
    	var me = this;
    	var value = '';
        this.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({
    		'menuTree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'field[name=ds_label]': {
    			afterrender: function(t){
    				var value = t.up('form').down('field[name=ds_caller]').getValue();
    				if(!value){
    					t.autoDbfind = false;
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			},
    			beforetrigger: function(t){
    				var value = t.up('form').down('field[name=ds_caller]').getValue();
    				if(value){
    					t.dbBaseCondition = "fo_caller='" + value + "'";
    				}
    			}
    		},
    		'field[name=ds_caller]': {
    			aftertrigger: function(t){
    				var value = t.getValue();
    				if(value){
    					var label = t.up('form').down('field[name=ds_label]');
    					label.autoDbfind = false;
    					label.setHideTrigger(false);
    					label.setReadOnly(false);
    					label.setValue('');
    				}
    				
    			}
    		},
    		
    		'button[id=confirmMenu]': {
    			click: function(btn){
    				Ext.getCmp('form').items.get("ds_directory").setValue(Ext.getCmp('manuValue').value);
    				Ext.getCmp('form').items.get("ds_prefixcode").setValue(Ext.getCmp('prefixCode').value);
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
                    me.FormUtil.onDelete(Ext.getCmp('ds_id').value);
                }
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ds_statuscode');
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
                    me.FormUtil.onAdd('addDocSetting', '新增文档规则', 'jsps/common/DocSetting.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ds_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
	                fn:function(btn) {
	                	this.FormUtil.onSubmit(Ext.getCmp('ds_id').value);
	                }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ds_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: {
                    lock: 2000,
	                fn:function(btn) {
                        me.FormUtil.onResSubmit(Ext.getCmp('ds_id').value);
	                }
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ds_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                    	me.FormUtil.onAudit(Ext.getCmp('ds_id').value);
	                }
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ds_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                  	  	me.FormUtil.onResAudit(Ext.getCmp('ds_id').value);
	                }
                }
            }
            
    	});
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
});