/**
 * @author lpkinger
 */
Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.ContractType', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.fee.ContractType','core.form.Panel','oa.fee.ContractTypeTree',
    		'core.button.Save','core.button.Close','core.trigger.SearchField',
    		'core.trigger.DbfindTrigger','core.form.YnField','core.button.Sync',
    		'core.button.Add','core.button.Submit','core.button.ResAudit',
      		'core.button.Audit','core.button.Delete','core.button.Update','core.button.ResSubmit',
      		'core.trigger.TextAreaTrigger', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.CheckBoxGroup','core.trigger.ContractTypeSearchField'    	
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		'contracttypetree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'#ct_id': {
    			change: function(f) {
    				Ext.defer(function(){
	    				var a = f.up('form').down('erpSyncButton'), 
	    					b = f.up('form').down('erpSubmitButton'),
	    					d =f.up('form').down('erpResSubmitButton'),
	    					e =f.up('form').down('erpAuditButton'),
	    					g =f.up('form').down('erpResAuditButton'),
	    					h = f.up('form').down('erpUpdateButton'),
	    					k = f.up('form').down('erpAddButton'),
	    					j = f.up('form').down('erpDeleteButton'),
	    					c = f.up('form').down('#CT_STATUSCODE'),
	    					i =	f.up('form').down('#ct_id');
	    				if(c.getValue() == 'ENTERING'&&(i.getValue()!=null||i.getVaule()!='')) {
	    					b && b.show();
	    					h && h.show();
	    					j && j.show();
	    					k && k.show();
	    					a && a.hide();
	    					g && g.hide();
	    					d && d.hide();
	    					e && e.hide();
	    				} else if(c.getValue() == 'COMMITED'){
	    					d && d.show();
	    					k && k.show();
	    					j && j.hide();
	    					a && a.hide();
	    					b && b.hide();
	    					g && g.hide();
	    					h && h.hide();
	    					e && e.show();
	    				}else if(c.getValue() == 'AUDITED'){
	    					a && a.show();
	    					g && g.show();
	    					k && k.show();
	    					d && d.hide();
	    					h && h.hide();
	    					b && b.hide();
	    					e && e.hide();
	    					j && j.hide();
	    				}else if(i.getValue()!=null||i.getVaule()!=''){
	    					d && d.hide();
	    					h && h.hide();
	    					b && b.hide();
	    					e && e.hide();
	    					a && a.hide();
	    					g && g.hide();
	    					j && j.hide();
	    					k && k.hide();
	    				}
    				}, 100);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var id = Ext.getCmp('ct_id').value;
    				if(id == null || id == ''){
    					this.FormUtil.beforeSave(this);
    				} else {
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			},
    			beforerender:function(btn){
    				var id = Ext.getCmp('ct_id').value;
    				if(id==null||id==''){
    					btn.hide();
    				}
    			}
    		},
    		'erpSyncButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				if(status!='AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpUpdateButton': {
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				var id = Ext.getCmp('ct_id').value;
    				if(status!='ENTERING'||(id==null||id=='')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    					this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			},
    			afterrender:function(btn){
    				var id = Ext.getCmp('ct_id').value;
    				if(id==null||id==''){
    					btn.hide();
    				}
    			}
    		},
    		'erpSubmitButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				var id = Ext.getCmp('ct_id').value;
    				if(status!='ENTERING'||(id==null||id=='')){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
        		}
    		},
    		'erpResSubmitButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				if(status!='COMMITED'){
    					btn.hide();
    				}  				
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				if(status!='COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResAuditButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp('CT_STATUSCODE').value;
    				if(status!='AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'button[name=delete]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree-panel');
    				var items = treegrid.selModel.selected.items;
    				if(items[0].data['ct_id'] != null || items[0].data['ct_id'] != ''){
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
    								level: (items[0].data['depth'] + 1),
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
    								level: (record.data['depth'] + 1),
    								allowDrag: true
    						};
    						record.appendChild(o);
    					}    					
    				}
    			}
    		},
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
			        	url : basePath + 'oa/fee/getContractTypeTree.action',
			        	params: {
			        		parentid: record.data['id'],
			        		allKind:tree.allKind
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
    		var  param = {caller: caller,condition:''};
    		//from移除旧组件
    		form.removeAll();
    		//移除docked
    		Ext.each(form.dockedItems.items, function(item){
    			if(item.dock=='bottom')
    			form.removeDocked(item,false);
    		});
    		form.setLoading(true);
    		//拿到form的items
			Ext.Ajax.request({
			url : basePath + 'common/singleFormItems.action',
			params: param,
			method : 'post',
			callback : function(options, success, response){
				form.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var items = me.FormUtil.setItems(form, res.items, res.data, res.limits, {
					labelColor: res.necessaryFieldColor
				});
				form.add(items);
				me.FormUtil.setButtons(form, res.buttons);
				
    			//根据父节点生成当前节点信息
	    		Ext.getCmp('ct_subof').setValue(record.data['parentId']);
	    		Ext.getCmp('ct_level').setValue(record.data['level'] || 1);
	    		Ext.getCmp('save').setText($I18N.common.button.erpSaveButton);
	    		tree.disable(true);
				//form第一个可编辑框自动focus
				me.FormUtil.focusFirst(form);
				form.fireEvent('afterload', form);
			}
		});
    	} else {
    		if(!me.datamanager[id]){
    			form.setLoading(true);
            	Ext.Ajax.request({//拿到tree数据
                	url : basePath + 'common/singleFormItems.action',
                	params: {
                		caller: caller,
                		condition: 'ct_id=' + id
                	},
                	callback : function(options,success,response){
                		form.setLoading(false);
                		var res = new Ext.decode(response.responseText);
                		if(res.data){
                			form.getForm().setValues(Ext.decode(res.data));
                			me.datamanager[id] = Ext.decode(res.data);//将取到的数据保存在本地，下次点击该节点，直接从本地获取
                			var field = Ext.getCmp('ct_id');
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
			var form = Ext.getCmp('form');
			if(form.deleteUrl.indexOf('caller=') == -1){
				form.deleteUrl = form.deleteUrl + "?caller=" + caller;
			}
			me.FormUtil.setLoading(true);
			Ext.Ajax.request({
				url : basePath + form.deleteUrl,
				params: {
					id: record.data['id']
				},
				method : 'post',
				callback : function(options,success,response){
					me.FormUtil.setLoading(true);
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						showError(localJson.exceptionInfo);return;
					}
					if(localJson.success){
						delSuccess(function(){	
							window.location.href = window.location.href;
						});//@i18n/i18n.js
					}else {
						delFailure();
					}
				}
			});
		} else {
			record.remove(true);
		}
	}
});