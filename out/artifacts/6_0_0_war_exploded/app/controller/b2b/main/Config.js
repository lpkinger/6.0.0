Ext.define('erp.controller.b2b.main.Config', {
    extend: 'Ext.app.Controller',
    stores: ['TreeStore'],
    views: ['common.main.TreePanel','b2b.main.Config', 'core.form.ColorField',
            'common.main.Toolbar','core.trigger.SearchField', 'core.trigger.DbfindTrigger'],
    refs: [{
    	ref: 'tree',
    	selector: '#tree-panel'
    },{
    	ref: 'configPanel',
    	selector: '#configPanel'
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
    		'#configPanel': {
    			boxready: function() {
    				var caller = window.whoami || 'sys';
    				me.loadConfigs(caller, function(configs){
                		me.setConfigs(configs,'configPanel');
                	});
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
    		'button[id=btn-logs]': {
    			click: function(btn){
    				var me=this;
					var store=Ext.create('Ext.data.Store',{
						fields: ['ml_id', 'ml_man', 'ml_date','ml_content', 'ml_result'],
						pageSize:15,
						proxy:{
							type:'ajax',
							method:'post',
							url:basePath+'common/getLogicMessageLogs.action',
							reader:{
								type:"json",		
								root:'logs',
								totalProperty:'num',
							},
								actionMethods: {
						            read   : 'POST'
						        }
						},
					listeners:{
						beforeload : function(store) {
							var context=Ext.getCmp('context').value;
								Ext.apply(store.proxy.extraParams, {
									caller:whoami||me.currCaller,
									context:context
								});
						},
					},
					autoLoad:true
					});						
					Ext.create('Ext.window.Window', {
						id : 'win' + whoami,
						title: '<span style="color:#CD6839;">操作日志</span>',
						iconCls: 'x-nbutton-icon-log',
						closeAction: 'destroy',
						height: "90%",
						width: "90%",
						modal:true,
						maximizable : true,
						buttonAlign : 'center',
						layout : 'border',
						//	iframe:true,
						items: [{
						xtype:'panel',
						region:'north',
						layout:"column",
						bodyStyle: 'background:#f1f1f1;',
						defaults: {
						xtype : 'textfield',
						anchor: '100%'
							},
						items:[{
							fieldLabel:'<span style="color:blue;font-weight:bold;margin:0px 0px 0px 15px">操作内容</span>',
							id:'context',
							allowblank:true,
							labelWidth:80,
							labelSeparator:':',
							},{
							xtype:'button',
							id:"search",
							iconCls:'x-button-search',
							handler:function(btn){
								var grid=Ext.getCmp('logPanel');
								grid.store.removeAll();
								grid.store.reload();								
								},
							}],
						},{
							xtype: 'gridpanel',
							id:"logPanel",
							bodyStyle: 'background:#f1f1f1;',
							autoScroll: true,
							autoheight:true,
							region:'center',
							store:store,
							columnLines: true,
							columns: [{
								header:'<span style="text-align:center;color:red">ID</span>',
								dataIndex:'ml_id',
								hidden:true,
							},{
								header: '时间', 
								dataIndex: 'ml_date',
								height:30,
								flex: 0.45, 
								renderer: function(val){
									if(val != ''){
										return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
									 }
								}
							},{ 
								header: '操作人员', 
								dataIndex: 'ml_man',
								flex: 0.3,
								renderer:function(val){
									if(val==(em_name+'('+em_code+')'))
										return '<font color=red>'+val+'</font>';
									else 
										return val;
								}
							},{ 
								header: '操作类型',
								align:'center',
								dataIndex: 'ml_content', 
								flex: 0.5,
								renderer:function(val){
									if(val=='参数配置')
										return val;
									else{
										return '逻辑配置: '+me.getMethodTypes(val).text;
									}									
								}
							},{ 
								header: '操作内容',
								dataIndex: 'ml_result',
								flex: 1.8
								}],							     
							dockedItems:[{
								xtype:'pagingtoolbar',//分页组件
								pageSize:15,
								store:store,
								dock:'bottom',
								displayInfo:true,
								displayMsg:'当前显示第{0}到{1}条数据,一共有{2}条',
								emptyMsg: "没有数据",
								beforePageText: "当前第",
								afterPageText: "页,共{0}页"
								}],
							}],
						buttons : [{
							text : '关  闭',
							iconCls: 'x-button-icon-close',
							cls: 'x-btn-gray',
							handler : function(btn){
							btn.ownerCt.ownerCt.close();
							}
						}],
						listeners:{
            		 		render:function(){
            		 			var me = this;
            		 			this.el.on('keyup',function(e,input){
            		 				if(e.button==12){
            		 					var btn=Ext.getCmp('search');
            		 					btn.handler();
            		 				}
            		 			});
            		 		}
            		 	},
					}).show();
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
    		},
    		/**其他特殊需求*/
    		'field[name=SetEmailPassword]':{
    			beforerender:function(field){
    				field.inputType='password';
    			}
    		}
    	});
    },
    onNodeClick: function(selModel, record){
    	var me = this;
    	if (record.get('leaf')) {
    		me.getSetting(record.raw.caller, record.get('text'));
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
            		me.setConfigs(configs,'');
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
    	if(caller!='B2CSetting'){
	    	if(em_type!='admin'){
	    		showError('ERR_POWER_025:您没有修改业务配置的权限!');return;
	    	}
    	}
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
    setConfigs: function(configs,name) {
    	var me = this, pane = Ext.getCmp(name), items = [];
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
    			if(c.code=='freeRateGetPrice'){//浮动率计算取价原则
    				var data = c.data ? c.data.split(',') : [null], s = [];
    				for(var i=0;i<5;i++){
    					if(i==0){
    						s.push({
	    						xtype:'combo',
	    						fieldLabel: '第'+(i+1)+'原则',
								id:'freeRateGetPrice'+i,
								name:'freeRateGetPrice'+i,
								store: Ext.create('Ext.data.Store', {
								   fields: ['display', 'value'],
								   data : [{"display": '物料+供应商+币别+类型--最新有效价格', "value": 'A'},
								           {"display": '物料+供应商+币别+类型+分段数---最新有效价格', "value": 'B'},
								           {"display": '物料+类型--最新有效价格', "value": 'C'},
								           {"display": '物料+类型---有效且合格最低不含税价格', "value": 'D'},
								           {"display": '物料+类型--有效最低不含税价格', "value": 'E'}]
							   }),
							   displayField: 'display',
							   valueField: 'value',
							   queryMode: 'local',
							   value:data[i],
							   editable: false	
			    			});
    					}else{
	    					s.push({
	    						xtype:'combo',
	    						fieldLabel: '第'+(i+1)+'原则',
								id:'freeRateGetPrice'+i,
								name:'freeRateGetPrice'+i,
								store: Ext.create('Ext.data.Store', {
								   fields: ['display', 'value'],
								   data : [{"display": '请选择原则...', "value": 'N'},
								           {"display": '物料+供应商+币别+类型--最新有效价格', "value": 'A'},
								           {"display": '物料+供应商+币别+类型+分段数---最新有效价格', "value": 'B'},
								           {"display": '物料+类型--最新有效价格', "value": 'C'},
								           {"display": '物料+类型---有效且合格最低不含税价格', "value": 'D'},
								           {"display": '物料+类型--有效最低不含税价格', "value": 'E'}]
							   }),
							   displayField: 'display',
							   valueField: 'value',
							   queryMode: 'local',
							   value:data[i]?data[i]:'N',
							   editable: false	
			    			});
    					}
    				}
    				items.push({
    					xtype: 'fieldset',
    					title: '浮动率运算取价原则设置',
    					id: c.id,
    					name: c.code,
    					columnWidth: 1,
    					layout: 'column',
    					defaults: {
    						columnWidth: .5,
    						margin: '4 8 4 8'
    					},
    					items: s
    				});
    				break;
    			}
	    		if(c.code=='bomCostPrinciple'){//BOM成本计算取价原则
					var data = c.data ? c.data.split(',') : [null], s = [];
					for(var i=0;i<6;i++){
						if(i==0){
							s.push({
	    						xtype:'combo',
	    						fieldLabel: '第'+(i+1)+'原则',
								id:'bomCostPrinciple'+i,
								name:'bomCostPrinciple'+i,
								store: Ext.create('Ext.data.Store', {
								   fields: ['display', 'value'],
								   data : [{"display": '最新下单采购单价', "value": 'B'},
								           {"display": '最新有效采购价格', "value": 'A'},
								           {"display": '最新采购验收单价', "value": 'C'},
								           {"display": '最新入库单成本价', "value": 'D'},
								           {"display": '物料标准单价', "value": 'E'},
								           {"display": '库存平均单价', "value": 'F'},
								           {"display": '最新有效采购价格(含税)', "value": 'G'},
								           {"display": '最新下单采购价格(含税)', "value": 'H'},
								           {"display": '最大有效采购价格(含税)', "value": 'J'},
								           {"display": '最小有效采购价格(含税)', "value": 'K'},
								           {"display": '上月库存平均单价', "value": 'I'}]
							   }),
							   displayField: 'display',
							   valueField: 'value',
							   queryMode: 'local',
							   value:data[i],
							   editable: false	
			    			});
						}else{
	    					s.push({
	    						xtype:'combo',
	    						fieldLabel: '第'+(i+1)+'原则',
								id:'bomCostPrinciple'+i,
								name:'bomCostPrinciple'+i,
								store: Ext.create('Ext.data.Store', {
								   fields: ['display', 'value'],
								   data : [{"display": '请选择原则...', "value": 'N'},
								   		   {"display": '最新下单采购单价', "value": 'B'},
								           {"display": '最新有效采购价格', "value": 'A'},
								           {"display": '最新采购验收单价', "value": 'C'},
								           {"display": '最新入库单成本价', "value": 'D'},
								           {"display": '物料标准单价', "value": 'E'},
								           {"display": '库存平均单价', "value": 'F'},
								           {"display": '最新有效采购价格(含税)', "value": 'G'},
								           {"display": '最新下单采购价格(含税)', "value": 'H'},
								           {"display": '上月库存平均单价', "value": 'I'},
								           {"display": '最大有效采购价格（含税）', "value": 'J'},
								           {"display": '最小有效采购价格（含税）', "value": 'K'}]
							   }),
							   displayField: 'display',
							   valueField: 'value',
							   queryMode: 'local',
							   value:data[i]?data[i]:'N',
							   editable: false	
			    			});
						}
					}
					items.push({
						xtype: 'fieldset',
						title: 'BOM成本运算取价原则设置',
						id: c.id,
						name: c.code,
						columnWidth: 1,
						layout: 'column',
						defaults: {
							columnWidth: .5,
							margin: '4 8 4 8'
						},
						items: s
					});
					break;
				}
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
			    					mouseOffset:[-315,18],
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
				},
				'TurnProdIN2':{
					text:'转出货单',
					weight: 13
				},
				'turnSendNotify':{
					text:'转通知单',
					weight:13	
				},
				'turnProdIO':{
					text:'转出货单',
					weight:13
				},
				'CreateOtherBill':{
					text:'生成其它应收单',
					weight:15
				},
				'query':{
					text:'筛选',
					weight:15
				},
				'processupdate':{
					text:'审批流中更新',
					weight:15
				},
				'turnBOM':{
					text:'转BOM',
					weight:15
				},
				'banned':{
					text:'禁用',
					weight:15
				},
				'import':{
					text:'导入',
					weight:15
				},
				'approve':{
					text:'批准',
					weight:15
				},
				'turnFQC':{
					text:'转FQC检验',
					weight:15
				},
				'enddetail': {
					text: '结案明细',
					weight: 13
				}
		};
		return types[type] || {text: type, weight: 99};
	},
	onSaveClick: function() {
		var me = this, pane = me.getConfigPanel(), tab = me.getTabPanel(), 
			updatedConfigs = [], updatedInters = [];
		//更新仓库：是否商城仓库来源字段
		var checkbg = Ext.getCmp('cbg');
		var params = new Object();		
		var jsonData = new Array();
		var checkUnCheck = true;
		for(var i = 1; i < checkbg.items.length ; i++){
			var dd = new Object();
			dd['wh_id']=checkbg.items.items[i].whid;
			dd['wh_ismallstore']=checkbg.items.items[i].checked;			
			jsonData.push(Ext.JSON.encode(dd));
			if(checkbg.items.items[i].checked){
				checkUnCheck=false;
			}
		}
		if(checkUnCheck){
			showError("请最少勾选一个!");
			return;
		}
		var param = jsonData == null ? [] : "[" + jsonData.toString().replace(/\\/g,"%") + "]";
		params.param=unescape(param.toString().replace(/\\/g,"%"));
		me.isLoading = false;
		me.FormUtil.setLoading(true);
		Ext.defer(function(){//延时一段时间避免loading不被渲染加载
			Ext.Ajax.request({
	        	url : basePath + 'scm/updateIsMallStore.action',
	        	async: false,
	        	params:params,
	        	method : 'post',
	        	callback : function(options,success,response){
	        		me.FormUtil.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo){
	        			showError(res.exceptionInfo);return;
	        		}
					if(res.success){
	        			if(res.log){
		        			if(res.log.success){
			        			if(res.log.acount){	
			        				var acount = parseInt(res.log.acount);//勾选物料总数
			        				var scount = 0;
			        				if(res.log.scount){
			        					parseInt(res.log.scount);//成功条数
			        				}
			        				var ecount = parseInt(res.log.ecount);//被筛选掉的条数
			        				var ocount = acount-scount-ecount;//数据有误条数
			        				if(acount==0){//没有初始化物料
			        					me.showRes('提示','<p style=" margin: 0px; ">上架仓库配置修改成功！</p>');
			        				}else if(ocount>0){ //成功上传未知 失败>0 
			        					me.showRes('提示','<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;上架产品数据来源设置完成！</p>' +
			        							'<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;成功'+scount+'条，失败'+(ecount+ocount)+'条（失败原因：品牌或型号为空，或最小订购量大于当前库存量。您可完善产品信息后再次设置）</p>');
			        				}else if(ecount>0){//失败0 筛选>0
		        						me.showRes('提示', '<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;上架产品数据来源设置完成！</p>' +
		        							'<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;成功'+scount+'条，失败'+ecount+'条（失败原因：品牌或型号为空，或最小订购量大于当前库存量。您可完善产品信息后再次设置）</p>');
		        					}else{//筛选==0
		        						me.showRes('提示', '<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;上架产品数据来源设置完成！</p>' +
		        							'<p style=" margin: 0px; ">&nbsp;&nbsp;&nbsp;&nbsp;成功'+scount+'条，失败'+ecount+'条</p>');
		        					}
			        			}
		        			}else{
		        				me.showRes('提示','<p style=" margin: 0px; ">上架仓库配置修改成功！</p>');
		        			}	
	        			}
	        		}       		
	        	}
	        });
    	},400);
		var bool = false;
		if(pane){Ext.Array.each(pane.items.items, function(field){
			if(field.xtype == 'fieldset') {
				var vals = [];
				var check = [];
				if(field.name=='bomCostPrinciple'||field.name=='freeRateGetPrice'){
					Ext.Array.each(field.items.items, function(i){
						if(i.name && typeof i.getValue === 'function' && !Ext.isEmpty(i.getValue())) {
							for(var ch = 0 ; ch < check.length ; ch++){
								if(check[ch]==i.getValue()&&i.getValue()!='N'){
									bool = true;
								}
							}
							vals.push(i.getValue());
							check.push(i.getValue());
						}
					});
					updatedConfigs.push({
						id: field.id,
						data: vals.length > 0 ? vals.join(',') : null
					});
				}else{
					Ext.Array.each(field.items.items, function(i){
						if(i.name && typeof i.getValue === 'function' && !Ext.isEmpty(i.getValue())) {
							vals.push(i.getValue());
						}
					});
					updatedConfigs.push({
						id: field.id,
						data: vals.length > 0 ? vals.join('\n') : null
					});
				}
			} else if(typeof field.isDirty === 'function' && field.isDirty()) {
				var value = field.getValue();
				updatedConfigs.push({
					id: field.id,
					data: typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ?
							Ext.Object.getValues(value)[0] : value)
				});
			}
		});
		}
		if(bool){
			showError("原则不能重复!");
			return;
		}
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
	},showRes : function (title,val){
		Ext.create('Ext.window.Window', {
			header:{
				title:'<h2 style=" margin: 6px; font-size: 16px; ">'+title+'</h2>',
				width: 400,
				height:40
			},
			width: 400,
		    height: 180,
		    layout: 'fit',
		    html:'<div style=" font-size:14px; width: 97%; line-height: 25px; margin: 20px auto; ">'+val+'</div>' 
		}).show();
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
				}else{
					var  localJson= new Ext.decode(r.responseText);
					if(localJson.exceptionInfo){
						me.Toast.info('提示', localJson.exceptionInfo);
						return;
					}
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