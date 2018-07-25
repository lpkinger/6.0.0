Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.LogicDesc', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.logic.LogicDesc','core.form.Panel','core.grid.Panel2','core.form.YnField',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Test','core.button.Update',
   		'core.button.Publish','core.form.FileField','core.button.Sync',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.toolbar.Toolbar'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=ld_pwd]': {
    			afterrender: function(f){
    				f.el.dom.getElementsByTagName('input')[0].type = "password";
    			}
    		},
    		'textarea[name=ld_source]': {
    			afterrender: function(f){
    				f.setHeight(300);
    			}
    		},
    		'textarea[name=ld_desc]': {
    			afterrender: function(f){
    				f.setHeight(40);
    			}
    		},
    		'combobox[name=ld_mtype]': {
    			afterrender: function(f){
    				//me.showDefaultParams(f);
    			},
    			change: function(f){
    				me.showDefaultParams(f);
    			}
    		},
    		'combobox[name=ld_type]': {
    			afterrender: function(t) {
    				me.checkType(t.value);
    			},
    			change: function(t){
    				me.checkType(t.value);
    			}
    		},
    		'field[name=ld_classname]': {
    			afterrender: function(f){
    				if(Ext.isEmpty(f.value)){
    					f.hide();
    				} else {
    					f.show();
    				}
    			}
    		},
    		'field[name=ld_methodname]': {
    			afterrender: function(f){
    				if(Ext.isEmpty(f.value)){
    					f.hide();
    				} else {
    					f.show();
    				}
    			}
    		},
    		'field[name=ld_lncode]': {
    			afterrender: function(f){
    				if (!Ext.isEmpty(ld_lncode) && Ext.isEmpty(f.value) ) {
    					f.setValue(ld_lncode);
    				}
    			}
    		},
    		'multidbfindtrigger[name=ldf_field]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var tab = record.data['ldf_table'];
    				if(tab != null && tab != ''){
    					t.dbBaseCondition = "upper(ddd_tablename)='" + tab.toUpperCase() + "'";
    				}
    			}
    		},
    		'field[name=ldf_table]': {
    			change: function(f){
    				if (!Ext.isEmpty(f.value)) {
    					f.setValue(f.value.toUpperCase());
    				}
    			}
    		},
    		'dbfindtrigger[name=ld_module]': {
    			afterrender: function(f){
    				f.onTriggerClick = function(){
    					me.getLogicTree();
    				};
    				f.autoDbfind = false;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var s = Ext.getCmp('ld_status').value;
    				if (s == '无效') {//无效状态下，直接更新
    					this.FormUtil.onUpdate(this);
    				} else {//有效状态下，升级版本
    					var f = Ext.getCmp('ld_type');
    					if(f && f.value == 0) {
    						warnMsg('当前更改需要升级算法的版本，确定升级版本?', function(btn){
        						if(btn == 'yes'){
        							var c = Ext.getCmp('ld_code'),
        								v = Ext.getCmp('ld_version');
        							me.FormUtil.onAdd('addLogicChange', '算法升级', 'jsps/ma/logic/logicChange.jsp?lc_ldcode=' + c + 
        									"&lc_oldversion=" + v);
        						}
        					});
    					} else {
    						showError('当前类型的算法(系统算法和主算法)不支持修改和升级!');
    					}
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addLogicDesc', '添加新算法', 'jsps/ma/logic/logicDesc.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		},
    		'erpGridPanel2': {
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpTestButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp('ld_test');
    				if (f.value == '测试通过') {
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				Ext.Ajax.request({
    					url: basePath + 'ma/logic/testLogicDesc.action',
    					params: {
    						id: Ext.getCmp('ld_id').value
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						var res = new Ext.decode(response.responseText);
    						if(res.log) {
    							showError(res.log);
    						} else {
    							alert("测试通过");
    							window.location.reload();
    						}
    					}
    				});
    			}
    		},
    		'erpPublishButton': {
    			afterrender: function(btn){
    				var f = Ext.getCmp('ld_status'),
    					t = Ext.getCmp('ld_test');
    				if (t.value != '测试通过' || f.value != '无效') {
    					btn.hide();
    				}
    				if (f.value == '有效') {
    					var items = f.ownerCt.items.items;
    					Ext.each(items, function(item){
    						item.setReadOnly(true);
    						item.setFieldStyle('background:#f1f1f1;');
    					});
    					Ext.getCmp('grid').readOnly = true;
    				}
    			},
    			click: function(btn){
    				var tab = me.BaseUtil.getActiveTab();
    				tab.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'ma/logic/publishLogicDesc.action',
    					params: {
    						id: Ext.getCmp('ld_id').value
    					},
    					method: 'post',
    					callback: function(options, success, response){
    						tab.setLoading(false);
    						var res = new Ext.decode(response.responseText);
    						if(res.exceptionInfo) {
    							showError(res.exceptionInfo);
    							return;
    						}
    						if(res.log) {
    							showError(res.log);
    						} else {
    							alert("发布成功，您现在可以到【算法配置】里面将该算法配置到相应的单据逻辑中");
    							window.location.reload();
    						}
    					}
    				});
    			}
    		},
    		'treepanel': {
    			itemmousedown: function(selModel, record){
    				var tree = selModel.ownerCt;
    				me.loadTree(tree, record);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	showDefaultParams: function(f){
		var form = f.ownerCt,
			idx = 0;
		if(!Ext.getCmp('defaultParams')){
			Ext.each(form.items.items, function(item, i){
				if(item.id == f.id){
					idx = i;
				}
			});
			form.insert(idx + 3 , {
				xtype: 'displayfield',
				columnWidth: 1 - f.columnWidth,
				id: 'defaultParams'
			});
		}
		var d = Ext.getCmp('defaultParams'),
			v = d.value;
		switch(f.value){
			case 'save':
				v = '参数:主表数据(HashMap&lt;Object, Object&gt; store), 明细表数据(ArrayList&lt;Map&lt;Object, Object&gt;&gt; gstore), 语言(String language)';
				break;
			case 'commit':
				v = '参数:单据ID(Integer id), 语言(String language), 操作人员信息(Employee employee)';
				break;
			case 'resCommit':
				v = '参数:单据ID(Integer id), 语言(String language), 操作人员信息(Employee employee)';
				break;
			case 'delete':
				v = '参数:单据ID(Integer id), 语言(String language)';
				break;
			case 'deletedetail':
				v = '参数:明细行ID(Integer id), 语言(String language)';
				break;
			case 'audit':
				v = '参数:单据ID(Integer id), 语言(String language), 操作人员信息(Employee employee)';
				break;
			case 'resAudit':
				v = '参数:单据ID(Integer id), 语言(String language), 操作人员信息(Employee employee)';
				break;
			case 'post':
				v = '参数:单据ID(Integer id), 语言(String language)';
				break;
			case 'resPost':
				v = '参数:单据ID(Integer id), 语言(String language)';
				break;
			case 'turn':
				v = '参数:单据ID(Integer id), 语言(String language)';
				break;
			case 'resTurn':
				v = '参数:单据ID(Integer id), 语言(String language)';
				break;
		}
		d.setValue(v);
	},
	checkType: function(val){
		if(val == -1) {
			Ext.getCmp('ld_source').allowBlank = true;
			Ext.getCmp('ld_source').hide();
			Ext.getCmp('ld_classname').show();
			Ext.getCmp('ld_methodname').show();
			Ext.getCmp('ld_classname').allowBlank = false;
			Ext.getCmp('ld_classname').setFieldStyle('background:#fffac0;');
			Ext.getCmp('ld_methodname').allowBlank = false;
			Ext.getCmp('ld_methodname').setFieldStyle('background:#fffac0;');
		} else {
			Ext.getCmp('ld_source').allowBlank = false;
			Ext.getCmp('ld_source').show();
			Ext.getCmp('ld_classname').hide();
			Ext.getCmp('ld_methodname').hide();
			Ext.getCmp('ld_classname').allowBlank = true;
			Ext.getCmp('ld_classname').setFieldStyle('background:#f1f1f1;');
			Ext.getCmp('ld_methodname').allowBlank = true;
			Ext.getCmp('ld_methodname').setFieldStyle('background:#f1f1f1;');
		}
	},
	getLogicTree: function(){
		var w = Ext.create('Ext.Window',{
		    title: '查找模板',
		    height: "100%",
		    width: "80%",
		    maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'treepanel',
				rootVisible: false,
				useArrows: true,
				store: Ext.create('Ext.data.TreeStore', {
					root : {
						text: 'root',
						id: 'root',
						expanded: true
					}
				})
			}],
		    buttons : [{
		    	text : '关  闭',
		    	iconCls: 'x-button-icon-close',
		    	cls: 'x-btn-gray',
		    	handler : function(btn){
		    		btn.ownerCt.ownerCt.close();
		    	}
		    },{
		    	text: '确定',
		    	iconCls: 'x-button-icon-confirm',
		    	cls: 'x-btn-gray',
		    	handler: function(btn){
		    		var t = btn.ownerCt.ownerCt.down('treepanel');
		    		if(!Ext.isEmpty(t.title)) {
		    			Ext.getCmp('ld_module').setValue(t.title);
		    		}
		    		btn.ownerCt.ownerCt.close();
		    	}
		    }]
		});
		w.show();
		this.loadTree(w.down('treepanel'), null);
	},
	loadTree: function(tree, record){
		var pid = 0;
		if(record) {
			if (record.get('leaf')) {
				return;
			} else {
				if(record.isExpanded() && record.childNodes.length > 0){
					record.collapse(true, true);//收拢
					return;
				} else {
					if(record.childNodes.length != 0){
						record.expand(false, true);//展开
						return;
					}
				}
			}
			pid = record.get('id');
		}
		tree.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'common/lazyTree.action',
        	params: {
        		parentId: pid,
        		condition: 'sn_logic=1'
        	},
        	callback : function(options,success,response){
        		tree.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			if(record) {
        				record.appendChild(res.tree);
            			record.expand(false,true);//展开
            			tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
        			} else {
        				tree.store.setRootNode({
                    		text: 'root',
                    	    id: 'root',
                    		expanded: true,
                    		children: res.tree
                    	});
        			}
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	}
});