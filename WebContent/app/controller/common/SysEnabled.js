Ext.QuickTips.init();
Ext.define('erp.controller.common.SysEnabled', {
	extend: 'Ext.app.Controller',
	views: ['common.sysinit.SysEnabled','core.trigger.SearchField', 'core.trigger.DbfindTrigger','common.sysinit.SysDataGrid','common.sysinit.SysrefreshGrid'],
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
			'#infos-panel':{
				afterrender:function(panel){
					if (!detailEl) {
						var bd = Ext.getCmp('infos-panel').body;
						bd.update('').setStyle('background','#fff');
						detailEl = bd.createChild(); 
					}
			    	var syspanel =parent.Ext.getCmp('syspanel');		
					detailEl.hide().update(syspanel.currentRecord.raw.description).slideIn('l', {stopAnimation:true,duration: 200});
				}
			},
			'#configPanel': {
				boxready: function() {
					me.getSetting(caller);
				}
			},
			'button[id=btn-close]': {
				click: function(){
					var p = parent.Ext.getCmp('content-panel');
					if(p){
						p.getActiveTab().close();
					} else {
						window.close();
					}
				}
			},
			'button[id=btn-save]': {
				click: function(btn){
					me.onSaveClick();
				}
			},
			'dbfindtrigger': {
				aftertrigger: function(field, record, dbfinds) {
					Ext.Array.each(dbfinds, function(d){
						if(d.field == field.name) {
							field.setValue(record.get(d.dbGridField));
						}
					});
				}
			},
			'button[cls=x-dd-drop-ok-add]': {
				click: function(btn) {
					var f = btn.ownerCt, c = btn.config;
					f.insert(f.items.length - 1, {
						xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
						name: c.dbfind || c.code,
						readOnly: !c.dbfind && c.editable == 0,
						editable: c.editable == 1,
						clearable: true
					});
				}
			}
		});
	},
	getSetting: function(caller, title){
		var me = this;caller=caller||'sys';
		if(caller) {
			if(caller != me.currCaller) {
				me.loadConfigs(caller, function(configs){
					me.currCaller = caller;
					me.setConfigs(configs);
					title && me.getConfigPanel().setTitle(title);
				});
				/*	me.loadInterceptors(caller, function(interceptors){
            		me.setInterceptors(interceptors);
            		var tab = me.getTabPanel();
                	if(interceptors.length == 0 && !tab.collapsed)
                		tab.collapse();
                	else if(interceptors.length > 0 && tab.collapsed)
                		tab.expand();
            	});*/
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
		var me = this, pane = Ext.getCmp('configPanel'), items = [];
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
							margin: '0 8 4 8'
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
				xtype: 'displayfield',
				text:'无参数配置'
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
	},
	setInterceptors: function(interceptors) {
		var me = this, pane = me.getTabPanel(), panels = [];
		var types = Ext.Array.unique(Ext.Array.pluck(interceptors, 'type'));
		types = Ext.Array.sort(types, function(a, b){
			return me.getMethodTypes(a).weight > me.getMethodTypes(b).weight;
		});
		Ext.Array.each(types, function(type){
			var data = Ext.Array.filter(interceptors, function(i){
				return i.type == type;
			});
			Ext.Array.each(data, function(d){
				d.enable = d.enable == 1;
			});
			panels.push({
				title: me.getMethodTypes(type).text,
				xtype: 'grid',
				columns: [{
					text: '顺序',
					dataIndex: 'detno',
					xtype: 'numbercolumn',
					align: 'center',
					format: '0',
					width: 40
				},{
					text: '描述',
					dataIndex: 'title',
					flex: 10
				},{
					text: '启用',
					xtype: 'checkcolumn',
					dataIndex: 'enable',
					width: 60
				}],
				columnLines: true,
				store: new Ext.data.Store({
					fields: ['id', 'title', 'type', 'turn', 'detno', 'enable', 'class_', 'method'],
					data: data,
					groupField: 'turn',
					sorters: [{
						property: 'detno'
					}]
				}),
				features : [{
					ftype: 'grouping',
					groupHeaderTpl: '<tpl if="name == 0">前<tpl else>后</tpl> (共 {rows.length} 项)',
					startCollapsed: false
				}],
				viewConfig: {
					listeners: {
						render: function(view) {
							if (!view.tip) {
								view.tip = Ext.create('Ext.tip.ToolTip', {
									target : view.el,
									delegate : view.itemSelector,
									trackMouse : true,
									renderTo : Ext.getBody(),
									tpl: new Ext.XTemplate('<dl class="dl-horizontal">' +
											'<dt>类：</dt><dd>{class_}</dd>' +
											'<dt>方法：</dt><dd>{method}</dd>' +
									'</dl>'),
									listeners: {
										beforeshow: function (tip) {
											var record = view.getRecord(tip.triggerElement);
											if(record){
												tip.update(record.data);
											} else {
												tip.on('show', function(){
													Ext.defer(tip.hide, 10, tip);
												}, tip, {single: true});
											}
										}
									}
								});
							}
						}
					}
				}
			});
		});
		pane.removeAll();
		pane.add(panels);
	},
	getMethodTypes: function(type) {
		var types = {
				'save': {
					text: '保存',
					weight: 1
				},
				'update': {
					text: '更新',
					weight: 2
				},
				'commit': {
					text: '提交',
					weight: 3
				},
				'resCommit': {
					text: '反提交',
					weight: 4
				},
				'audit': {
					text: '审核',
					weight: 5
				},
				'resAudit': {
					text: '反审核',
					weight: 6
				},
				'post': {
					text: '过账',
					weight: 7
				},
				'resPost': {
					text: '反过账',
					weight: 8
				},
				'print': {
					text: '打印',
					weight: 9
				},
				'turnout': {
					text: '转出货',
					weight: 10
				},
				'turn': {
					text: '转单',
					weight: 11
				},
				'delete': {
					text: '删除',
					weight: 12
				},
				'deletedetail': {
					text: '删除明细',
					weight: 13
				},
				'finish': {
					text: '结案',
					weight: 14
				}
		};
		return types[type] || {text: type, weight: 99};
	},
	onSaveClick: function() {
		var me = this, pane = me.getConfigPanel(), tab = me.getTabPanel(), 
		updatedConfigs = [], updatedInters = [];
		Ext.Array.each(pane.items.items, function(field){
			if(field.xtype == 'fieldset') {
				var vals = [];
				Ext.Array.each(field.items.items, function(i){
					if(i.name && typeof i.getValue === 'function' && !Ext.isEmpty(i.getValue())) {
						vals.push(i.getValue());
					}
				});
				updatedConfigs.push({
					id: field.id,
					data: vals.length > 0 ? vals.join('\n') : null
				});
			} else if(typeof field.isDirty === 'function' && field.isDirty()) {
				var value = field.getValue();
				updatedConfigs.push({
					id: field.id,
					data: typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ?
							Ext.Object.getValues(value)[0] : value)
				});
			}
		});
		Ext.Array.each(tab.items.items, function(grid){
			var modified = grid.store.getModifiedRecords();
			Ext.Array.each(modified, function(m){
				updatedInters.push({
					id: m.get('id'),
					enable: m.get('enable') ? 1 : 0,
							detno: m.get('detno'),
							turn: m.get('turn')
				});
			});
		});
		if(updatedConfigs.length > 0) {
			me.saveConfigs(updatedConfigs, function(){
				me.loadConfigs(me.currCaller, function(configs){
					me.setConfigs(configs);
				});
			});
		}
		if(updatedInters.length > 0) {
			me.saveInterceptors(updatedInters, function(){
				me.loadInterceptors(me.currCaller, function(interceptors){
					me.setInterceptors(interceptors);
				});
			});
		}
	},
	/**
	 * 修改参数配置
	 */
	saveConfigs: function(updated, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/setting/configs.action',
			params: {
				updated: Ext.JSON.encode(updated)
			},
			method: 'POST',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					me.Toast.info('提示', '参数修改成功');
					callback.call();
				}
			}
		});
	},
	/**
	 * 修改逻辑配置
	 */
	saveInterceptors: function(updated, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/setting/interceptors.action',
			params: {
				updated: Ext.JSON.encode(updated)
			},
			method: 'POST',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					me.Toast.info('提示', '逻辑修改成功');
					callback.call();
				}
			}
		});
	}
});