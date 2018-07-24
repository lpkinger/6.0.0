Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.ProductKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.ProductKind','core.form.Panel','scm.product.ProductKindTree','core.form.MultiField',
    		'core.button.Save','core.button.Close', 'core.button.Abate', 'core.button.ResAbate','core.button.ResAudit',
    		'core.button.LossUpdate','core.trigger.SearchField','core.button.Submit','core.button.ResSubmit','core.button.Audit',
    		'core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		'prodkindtree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'#pk_level':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_leadtime':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_purchasedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_ltinstock':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_ltwarndays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_ltinstock':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		/*'#pk_length':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},*/
    		'#pk_purcmergedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_validdays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pk_period':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				/**
    				 * @autor wusy
    				 */
    				var pk_codelength = Ext.getCmp('pk_codelength'),
    					pk_subof = Ext.getCmp('pk_subof').value;
    				if(pk_codelength && pk_codelength.value!=0 && pk_subof!=0){
    					showError("只有顶级种类才能限制编码长度");
    					return;
    				}
    				var id = Ext.getCmp('pk_id').value;
    				if(id == null || id == ''){
    					this.FormUtil.beforeSave(this);
    				} else {
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pk_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pk_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pk_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pk_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pk_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pk_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pk_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pk_id').value);
				}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'button[name=delete]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree-panel');
    				var items = treegrid.selModel.selected.items;
    				if(items[0].data['pk_id'] != null || items[0].data['pk_id'] != ''){
    					if(items[0].isLeaf() == true){
    						warnMsg('确定删除节点[' + items[0].data['text'] + ']？', function(btn){
    							if(btn == 'yes'){
    								treegrid.deleteNode(items[0]);
    		    				} else if(btn == 'no'){
    		    					return;
    		    				} 
    						});
        				} else {
        					warnMsg('确定删除节点[' + items[0].data['text'] + ']及其子节点？', function(btn){
    							if(btn == 'yes'){
    								treegrid.deleteNode(items[0]);
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
    		'#pk_id': {
    			change: function(f) {
    				Ext.defer(function(){
	    				var a = f.up('form').down('erpAbateButton'), 
	    					b = f.up('form').down('erpResAbateButton'),
	    					c = f.up('form').down('#pk_effective');
	    				if(c.getValue() == '有效') {
	    					a && a.show();
	    					b && b.hide();
	    				} else {
	    					a && a.hide();
	    					b && b.show();
	    				}
    				}, 100);
    			}
    		},
    		'erpAbateButton': {
    			afterrender: function(btn) {
    				var f = btn.up('form').down('#pk_effective'), 
    					id = btn.up('form').down('#pk_id');
    				if((f && f.getValue() == '无效') || (id && !id.getValue())) {
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				me.setEffective(false, function(){
    					btn.hide();
    					btn.up('form').down('erpResAbateButton').enable();
    				});
    			}
    		},
    		'erpResAbateButton': {
    			afterrender: function(btn) {
    				var f = btn.up('form').down('#pk_effective'), 
						id = btn.up('form').down('#pk_id');
    				if((f && f.getValue() == '有效') || (id && !id.getValue())) {
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				me.setEffective(true, function(){
    					btn.hide();
    					btn.up('form').down('erpAbateButton').enable();
    				});
    			}
    		},
    		'erpLossUpdateButton':{
    			afterrender: function(btn) {
					if (Ext.getCmp('pk_id').value==="") {
						btn.hide();
					}
    			},
				click: function(btn){
					warnMsg("确定要更新物料损耗率吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/updateProdLoss.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pk_id').value,
    	    			   			caller:caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert('更新成功！');
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    				
    				
    				
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
			        	url : basePath + 'scm/product/getProductKindTree.action',
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
    			if(item.id=='form_toolbar')
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
				me.FormUtil.addItemsForUI(form,items,true);
				me.FormUtil.setButtons(form, res.buttons);
				Ext.each(form.items.items, function(field){
	    			if(field.name == 'pk_effective')
	    				field.setValue('有效');
    			});
    			//根据父节点生成当前节点信息
	    		Ext.getCmp('pk_subof').setValue(record.data['parentId']);
	    		Ext.getCmp('pk_level').setValue(record.data['level'] || 1);
	    		Ext.getCmp('save').setText($I18N.common.button.erpSaveButton);
	    		tree.disable(true);
				//form第一个可编辑框自动focus
				me.FormUtil.focusFirst(form);
				form.fireEvent('afterload', form);
				var btn =  Ext.getCmp('lossupdatebutton');
		    	if (Ext.getCmp('pk_id').value!==""){
		    		btn.show();
		    	}else {
		    		btn.hide();
		    	}
			}
		});
    	} else {
    		if(!me.datamanager[id]){
    			form.setLoading(true);
            	Ext.Ajax.request({//拿到tree数据
                	url : basePath + 'common/singleFormItems.action',
                	params: {
                		caller: caller,
                		condition: 'pk_id=' + id
                	},
                	callback : function(options,success,response){
                		form.setLoading(false);
                		var res = new Ext.decode(response.responseText);
                		if(res.data){
                			form.getForm().setValues(Ext.decode(res.data));
                			me.datamanager[id] = Ext.decode(res.data);//将取到的数据保存在本地，下次点击该节点，直接从本地获取
                			var field = Ext.getCmp('pk_id');
                			if(field.value == null || field.value == ''){
            					Ext.getCmp('save').setText($I18N.common.button.erpSaveButton);
            				} else {
            					Ext.getCmp('save').setText($I18N.common.button.erpUpdateButton);
            				}
                		} else if(res.exceptionInfo){
                			showError(res.exceptionInfo);
                		}
                		//按钮错乱BUG
                		Ext.getCmp('work_group').hide();
                		Ext.getCmp('logic_group').show();
                		var btn =  Ext.getCmp('lossupdatebutton');
				    	if (Ext.getCmp('pk_id').value!==""){
				    		btn.show();
				    	}else {
				    		btn.hide();
				    	}
				    	if(form){
				    		form.autoSetBtnStyle(form);
				    	}
                	}
                });
    		} else {
    			form.getForm().setValues(me.datamanager[id]);
    		}
    	}
    },
	setEffective: function(bool, fn) {
		var form = Ext.getCmp('form'), id = form.down('#pk_id').value;
		Ext.Ajax.request({
			url: basePath + 'scm/product/effective.action',
			params: {
				id: id,
				bool: bool
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					alert('执行成功.');
					form.FormUtil.loadNewStore(form, {
						caller: caller, 
						condition: "pk_id=" + id
					});
					fn.call();
				} else if(r.exceptionInfo) {
					showError(r.exceptionInfo);
				}
			}
		});
	}
});