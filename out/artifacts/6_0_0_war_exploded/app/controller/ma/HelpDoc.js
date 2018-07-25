Ext.QuickTips.init();
Ext.define('erp.controller.ma.HelpDoc', {
	extend: 'Ext.app.Controller',
	stores: ['TreeStore'],
	views: ['common.main.TreePanel','ma.logic.Config', 'core.form.ColorField','core.form.FileField',
	        'common.main.Toolbar','core.trigger.SearchField', 'core.trigger.DbfindTrigger'],
	        FormUtil:Ext.create('erp.util.FormUtil'),        
	        refs: [{
	        	ref: 'tree',
	        	selector: '#tree-panel'
	        },{
	        	ref: 'tabPanel',
	        	selector: '#tabpanel'
	        }],
	        init: function(){ 
	        	var me = this;
	        	me.FormUtil = Ext.create('erp.util.FormUtil');
	        	me.Toast = Ext.create('erp.view.core.window.Toast');
	        	this.control({ 
	        		'erpTreePanel': { 
	        			itemmousedown: function(selModel, record){
	        				Ext.defer(function(){
	        					me.onNodeClick(selModel, record);
	        				}, 20);
	        			},
	        			beforerender: function(tree) {
	        				if(window.whoami)
	        					tree.hide();
	        			}
	        		},
	        		'button[itemId=btn-save]':{
	        			click:function(btn){
	        				me.setHelpDoc(btn.ownerCt.ownerCt);
	        			}
	        		},
	        		'button[id=btn-scan]':{
	        			click:function(btn){
	        			   me.scan();
	        			}
	        		},
	        		'textfield[name=CALLER_]':{
	        			change:function(field){
	        				Ext.defer(function(){
	        				 var field=Ext.ComponentQuery.query('textfield[name=CALLER_]')[0];
	        				 var mfield=field.ownerCt.down('mfilefield[name=PATH_]');
		        			 if(mfield.items.items[0].value){
		        				  mfield.download(mfield.items.items[0].value);
		        				}
	        				},100);
	        				
	        			}
	        		}
	        	});
	        },
	        onNodeClick: function(selModel, record){
	        	var me = this;
	        	if (record.get('leaf')) {
	        		caller=record.raw.caller;
	        		me.getHelpRecord(record,function(r,caller){
	        			me.loadRecord(r,caller);
	        		});
	        	} else {
	        		if(record.isExpanded() && record.childNodes.length > 0){
	        			record.collapse(true, true);// 已展开则收拢
	        		} else {
	        			//未展开看是否加载了children
	        			if(record.childNodes.length == 0){
	        				me.getChildren(record);
	        			} else {
	        				record.expand(false, true);//展开
	        			}
	        		}
	        	}
	        },
	        setHelpDoc:function(form){
	        	var r=form.getValues(),me=this;
	        	Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
	        		if(contains(k, 'trashfield-', true)){
	        			delete r[k];
	        		}
	        	});
	        	me.FormUtil.setLoading(true);
	        	Ext.Ajax.request({
	        		url : basePath + 'ma/help/saveHelpDoc.action',
	        		params : {
	        			data:Ext.JSON.encode(r)
	        		},
	        		method : 'post',
	        		callback : function(options,success,response){
	        			me.FormUtil.setLoading(false);
	        		}
	        	});	
	        },
	        getHelpRecord:function(record,callback){
	        	var caller=record.raw.caller;
	        	if(caller){
	        		Ext.Ajax.request({
	        			url: basePath + 'ma/help/getHelpInfo.action?caller=' + caller,
	        			method: 'GET',
	        			callback: function(opt, s, r) {
	        				if(r && r.status == 200) {
	        					var res = Ext.JSON.decode(r.responseText);
	        					callback.call(null, res.data,caller);
	        				}
	        			}
	        		});
	        	}
	        },
	        loadRecord:function(record,caller){
	        	var _scan=false;
	        	if(!record){
	        		record={
	        				CALLER_:caller,
	        				PATH_:null,
	        				VERSION_:null,
	        				KEYWORDS_:null,
	        				DESC_:null
	        		}
	        	}else {
	        		Ext.getCmp('log-grid').getStore().load({
	        			params:{caller:caller}
	        		});
	        		_scan=true;
	        	}
	        	Ext.getCmp('docform').getForm().setValues(record);
                this.refreshScan(_scan);
	        },
	        scan:function(){
	        	Ext.Ajax.request({
        			url: basePath + 'ma/help/scan.action?caller=' + caller,
        			method: 'GET',
        			callback: function(opt, s, r) {
        				if(r && r.status == 200) {
        					var res = Ext.JSON.decode(r.responseText);
        					var win = Ext.create('Ext.Window', {
        							id: 'ext-help',
        							width: '90%',
        							height: '100%',
        							closeAction: 'destroy',
        							title: '帮助文档',
        							layout: 'border',
        							items: [{
        								region:'center',
        								tag : 'iframe',
        								layout : 'fit',
        								html : '<iframe src="' + basePath + res.path + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
        							}/*,{
        								region: 'south',
        								height: 100,
        								split: true,
        								collapsible: true,
        								title: '相关信息',
        								minHeight:60,
        								collapsed: true,
        								html: '相关信息'	
        							}*/]
        						});
        					win.show();
        				}
        			}
        		});
	        },
	        refreshScan:function(bool){
	        	var btn=Ext.getCmp('btn-scan');
	        	if(btn) btn.setDisabled(!bool);
	        },
	        /**
	         * 从后台加载树节点
	         */
	        getChildren: function(record) {
	        	var tree = this.getTree();
	        	tree.setLoading(true, tree.body);
	        	Ext.Ajax.request({//拿到tree数据
	        		url : basePath + 'common/lazyTree.action',
	        		params: {
	        			parentId: record.get('id')
	        		},
	        		callback : function(opt, s, r){
	        			tree.setLoading(false);
	        			var res = new Ext.decode(r.responseText);
	        			if(res.tree && record.childNodes.length == 0){
	        				record.appendChild(res.tree);
	        				record.expand(false, true);//展开
	        			} else if(res.exceptionInfo){
	        				showError(res.exceptionInfo);
	        			}
	        		}
	        	});
	        },
	        getSetting: function(caller, title){
	        	var me = this;
	        	if(caller) {
	        		if(caller != me.currCaller) {
	        			me.loadConfigs(caller, function(configs){
	        				me.currCaller = caller;
	        				me.setConfigs(configs);
	        				title && me.getConfigPanel().setTitle(title);
	        			});
	        			me.loadInterceptors(caller, function(interceptors){
	        				me.setInterceptors(interceptors);
	        				var tab = me.getTabPanel();
	        				if(interceptors.length == 0 && !tab.collapsed)
	        					tab.collapse();
	        				else if(interceptors.length > 0 && tab.collapsed)
	        					tab.expand();
	        			});
	        		}
	        	} else {
	        		me.currCaller = null;
	        	}
	        },
	        /**
	         * 配置参数
	         */
	        loadConfigs: function(caller, callback) {
	        	Ext.Ajax.request({
	        		url: basePath + 'ma/setting/configs.action?caller=' + caller,
	        		method: 'GET',
	        		callback: function(opt, s, r) {
	        			if(r && r.status == 200) {
	        				var res = Ext.JSON.decode(r.responseText);
	        				callback.call(null, res);
	        			}
	        		}
	        	});
	        },
	        /**
	         * 配置逻辑
	         */
	        loadInterceptors: function(caller, callback) {
	        	Ext.Ajax.request({
	        		url: basePath + 'ma/setting/interceptors.action?caller=' + caller,
	        		method: 'GET',
	        		callback: function(opt, s, r) {
	        			if(r && r.status == 200) {
	        				var res = Ext.JSON.decode(r.responseText);
	        				callback.call(null, res);
	        			}
	        		}
	        	});
	        },
	        setConfigs: function(configs) {
	        	var me = this, pane = me.getConfigPanel(), items = [];
	        	Ext.Array.each(configs, function(c, i){
	        		switch(c.data_type) {
	        		case 'YN':
	        			items.push({
	        				xtype: 'checkbox',
	        				boxLabel: c.title,
	        				name: c.code,
	        				id: c.id,
	        				checked: c.data == 1,
	        				columnWidth: 1,
	        				margin: c.help ? '4 8 0 8' : '4 8 4 8'
	        			});
	        			break;
	        		case 'RADIO':
	        			var s = [];
	        			Ext.Array.each(c.properties, function(p){
	        				s.push({
	        					name: c.code,
	        					boxLabel: p.display,
	        					inputValue: p.value,
	        					checked: p.value == c.data
	        				});
	        			});
	        			items.push({
	        				xtype: 'radiogroup',
	        				id: c.id,
	        				fieldLabel: c.title,
	        				columnWidth: 1,
	        				columns: 2,
	        				vertical: true,
	        				items: s
	        			});
	        			break;
	        		case 'COLOR':
	        			items.push({
	        				xtype: 'colorfield',
	        				fieldLabel: c.title,
	        				id: c.id,
	        				name: c.code,
	        				value: c.data,
	        				readOnly: c.editable == 0,
	        				editable: c.editable == 1,
	        				labelWidth: 150
	        			});
	        			break;
	        		case 'NUMBER':
	        			items.push({
	        				xtype: 'numberfield',
	        				fieldLabel: c.title,
	        				id: c.id,
	        				name: c.code,
	        				value: c.data,
	        				readOnly: c.editable == 0,
	        				labelWidth: 150
	        			});
	        			break;
	        		default :
	        			if(c.multi == 1) {
	        				var data = c.data ? c.data.split('\n') : [null], s = [];
	        				Ext.Array.each(data, function(d){
	        					s.push({
	        						xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
	        						name: c.dbfind || c.code,
	        						value: d,
	        						readOnly: !c.dbfind && c.editable == 0,
	        						editable: c.editable == 1,
	        						clearable: true
	        					});
	        				});
	        				s.push({
	        					xtype: 'button',
	        					text: '添加',
	        					width: 22,
	        					cls: 'x-dd-drop-ok-add',
	        					iconCls: 'x-dd-drop-icon',
	        					iconAlign: 'right',
	        					config: c
	        				});
	        				items.push({
	        					xtype: 'fieldset',
	        					title: c.title,
	        					id: c.id,
	        					name: c.code,
	        					columnWidth: 1,
	        					layout: 'column',
	        					defaults: {
	        						columnWidth: .25,
	        						margin: '4 8 4 8'
	        					},
	        					items: s
	        				});
	        			} else {
	        				items.push({
	        					xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
	        					fieldLabel: c.title,
	        					id: c.id,
	        					name: c.dbfind || c.code,
	        					value: c.data,
	        					readOnly: !c.dbfind && c.editable == 0,
	        					editable: c.editable == 1,
	        					clearable: true,
	        					columnWidth: .5,
	        					labelWidth: 150
	        				});
	        			}
	        		break;
	        		}
	        		if(c.help) {
	        			items.push({
	        				xtype: 'displayfield',
	        				value: c.help,
	        				columnWidth: ['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1 ? .5 : 1,
	        						cls: 'help-block',
	        						margin: '4 8 8 8'
	        			});
	        		} else {
	        			if(['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1) {
	        				items.push({
	        					xtype: 'displayfield'
	        				});
	        			}
	        		}
	        	});
	        	pane.removeAll();
	        	if(items.length == 0)
	        		items.push({
	        			html: '没有参数配置',
	        			cls: 'x-form-empty'
	        		});
	        	pane.add(items);
	        },
	        /**
	         * 字符长度
	         */
	        getCharLength: function(str) {
	        	if(str) {
	        		for (var len = str.length, c = 0, i = 0; i < len; i++) 
	        			str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126 ? c += 2 : c++;
	        			return c;
	        	}
	        	return 0;
	        }
});