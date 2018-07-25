Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.VendorKind','core.form.Panel','scm.purchase.VendorKindTree',
    		'core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.button.Scan','core.button.Add',
    		'core.button.Banned','core.button.ResBanned',
    		'core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		'vendkindtree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var id = Ext.getCmp('vk_id').value;
    			    this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
                    this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpBannedButton':{
    			click: function(btn){
    				var form = me.getForm(btn);
    			    this.FormUtil.onBanned(Ext.getCmp('vk_id').getValue());
    			}
    		},
    		'erpDeleteButton':{
    			click: function(btn){
    				var form = me.getForm(btn);
    			    this.FormUtil.onDelete(Ext.getCmp('vk_id').getValue());
    			}
    		},
	        'erpResBannedButton':{
	        	click: function(btn){
    				var form = me.getForm(btn);
    			    this.FormUtil.onResBanned(Ext.getCmp('vk_id').getValue());
    			}
    		},
    		'button[name=delete]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree-panel');
    				var items = treegrid.selModel.selected.items;
    				if(items[0].data['vk_id'] != null || items[0].data['vk_id'] != ''){
    					if(items[0].isLeaf() == true){
    						warnMsg('确定删除节点[' + items[0].data['text'] + ']？', function(btn){
    							if(btn == 'yes'){
    								me.deleteNode(items[0]);
    		    				} else if(btn == 'no'){
    		    					return;
    		    				} 
    						});
        				} else {
        					warnMsg('确定删除节点[' + items[0].data['text'] + ']及其子节点？', function(btn){
    							if(btn == 'yes'){
    								me.deleteNode(items[0]);
    		    				} else if(btn == 'no'){
    		    					return;
    		    				} 
    						});
        				}
					} else {
						items[0].remove(true);
					}
    			}
    		},
    		'button[name=add]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree-panel');
    				var items = treegrid.selModel.selected.items;
    				if(items.length > 0 && items[0].isLeaf() == true){
    					if(items[0].data['id'] == null || items[0].data['id'] == ''){
    						showError('请先描述该节点');
    					} else {
    						items[0].data['leaf'] = false;
    						items[0].data['cls'] = 'x-tree-cls-parent';
    						items[0].dirty = true;
    						var o = {
    								cls: "x-tree-cls-node",
    								parentId: items[0].data['id'],
    								leaf: true,
    								allowDrag: true
    						};
    						items[0].appendChild(o);
    						items[0].expand(true);
    					}
    				} else {
    					var record = treegrid.getExpandItem();
    					if(record){
    						var o = {
    								cls: "x-tree-cls-node",
    								parentId: record.data['id'],
    								leaf: true,
    								allowDrag: true
    						};
    						record.appendChild(o);
    					}
    					
    				}
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    loadTab: function(selModel, record){
    	var me = this;
    	var tree = Ext.getCmp('tree-panel');
    	if (!record.get('leaf')) {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'scm/purchase/getVendorKindTree.action',
			        	params: {
			        		parentid: record.data['id']
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
    	var choose = '';
    	Ext.each(tree.expandedNodes, function(){
    		me.lastCode += this.data['qtip'];
    		choose += '&raquo;' + this.data['text'];
    	});
    	Ext.getCmp('form').setTitle("<font color=blue>" + choose + "</font>");
    	this.getFormData(record);
    },
    getFormData: function(record){
    	var id = record.data['id'];
    	var me = this;
    	var form = Ext.getCmp('form');
    	var tree = Ext.getCmp('tree-panel');
    	if(id == null || id == ''){
    		Ext.each(form.items.items, function(field){
    			field.setValue(null);
    		});
    		Ext.getCmp('vk_subof').setValue(record.data['parentId']);
    		Ext.getCmp('save').setText($I18N.common.button.erpSaveButton);
    		tree.disable(true);
    	} else {
    		if(!me.datamanager[id]){
    			form.setLoading(true);
            	Ext.Ajax.request({//拿到tree数据
                	url : basePath + 'common/singleFormItems.action',
                	params: {
                		caller: caller,
                		condition: 'vk_id=' + id
                	},
                	callback : function(options,success,response){
                		form.setLoading(false);
                		var res = new Ext.decode(response.responseText);
                		if(res.data){
                			form.getForm().setValues(Ext.decode(res.data));
                			me.datamanager[id] = Ext.decode(res.data);//将取到的数据保存在本地，下次点击该节点，直接从本地获取
                			var field = Ext.getCmp('vk_id');
                			if(field.value == null || field.value == ''){
            					Ext.getCmp('save').setText($I18N.common.button.erpSaveButton);
            				} else {
            					Ext.getCmp('save').setText($I18N.common.button.erpUpdateButton);
            				}
                		} else if(res.exceptionInfo){
                			showError(res.exceptionInfo);
                		}
                	}
                });
    		} else {
    			form.getForm().setValues(me.datamanager[id]);
    		}
    	}
    },
    deleteNode: function(record){
		var me = this;
		if(record.data['id'] && record.data['id'] != ''){
			me.FormUtil.onDelete(record.data['id']);
		} else {
			record.remove(true);
		}
	}
});