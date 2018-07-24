Ext.QuickTips.init();
Ext.define('erp.controller.ma.Conf', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.Conf'],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=condition]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					me.showFilterPanel(btn);
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'triggerfield[name=sn_displayname]': {
    			afterrender: function(f){
    				f.onTriggerClick = function(){
    					me.getLogicTree();
    				};
    			}
    		},
    		'treepanel': {
    			itemmousedown: function(selModel, record){
    				var tree = selModel.ownerCt;
    				me.loadTree(tree, record);
    			}
    		},
    		'#analysis': {
    			click: function(btn) {
    				btn.ownerCt.ownerCt.hide();
    				var cal = Ext.getCmp('sn_caller').value;
    				if(!Ext.isEmpty(cal)) {
    					var record = me.lastSelected;
    					this.errors = 0;
    					Ext.getCmp('logger').setValue('');
    			    	Ext.getCmp('sync').hide();
    					me.BaseUtil.getActiveTab().setLoading(true);
    					Ext.Ajax.request({
    						url: basePath + 'ma/sync.action',
    						params: {
    							caller: cal,
    							path: record.getPath('id', '/').replace('/root/', ''),
    							spath: record.getPath('text', '/').replace('/root/', '')
    						},
    						callback: function(opt, s, r) {
    							me.BaseUtil.getActiveTab().setLoading(false);
    							if(s) {
    								var rs = Ext.decode(r.responseText);
    								if(rs.navigation) {
    									me.compareNavigation(rs.navigation);
    								}
    								if(rs.form) {
    									me.compareForm(rs.form);
    								}
    								if(rs.detailgrid) {
    									me.compareGrid(rs.detailgrid);
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'#others': {
    			click: function(btn) {
    				me.showOtherConf();
    			}
    		}
    	});
    },
    count: function(n) {
    	if(!this.errors) {
    		this.errors = 0;
    	}
    	n = n == null ? 1 : n;
    	this.errors += n;
    	Ext.getCmp('logger').setValue('共发现 ' + this.errors + '个差异项');
    	Ext.getCmp('sync').show();
    },
    compareNavigation: function(navigation) {
    	var usoft = navigation[0], current = navigation[1], dd = [], isExist = usoft.sn_Id != null;
		dd[0] = {conf: '描述', usoft: isExist ? usoft.sn_DisplayName : usoft, current: current.sn_DisplayName};
		dd[1] = {conf: '链接', usoft: isExist ? usoft.sn_Url : '--', current: current.sn_Url};
		dd[2] = {conf: '序号', usoft: isExist ? usoft.sn_detno : '--', current: current.sn_detno};
		dd[3] = {conf: '是否叶节点', usoft: isExist ? usoft.sn_isleaf : '--', current: current.sn_isleaf};
		dd[4] = {conf: '显示模式', usoft: isExist ? usoft.sn_showmode : '--', current: current.sn_showmode};
		dd[5] = {conf: '允许删除', usoft: isExist ? usoft.sn_deleteable : '--', current: current.sn_deleteable};
		dd[6] = {conf: '是否启用', usoft: isExist ? usoft.sn_using : '--', current: current.sn_using};
		dd[7] = {conf: '允许扩展逻辑', usoft: isExist ? usoft.sn_logic : '--', current: current.sn_logic};
		dd[8] = {conf: 'Caller', usoft: isExist ? usoft.sn_caller : '--', current: current.sn_caller};
		Ext.getCmp('navigation').store.loadData(dd);
		if(!this.equals(usoft, current, ['sn_Id', 'sn_ParentId'])) {
			this.count();
		}
    },
    compareForm: function(forms) {
    	var me = this, usoft = forms[0], current = forms[1], dd = [], 
    		isExist = usoft != null && usoft.fo_id != null, 
    		_isExist = current != null && current.fo_id != null;
    	dd[0] = {conf: '详细', usoft: isExist ? ('共' + usoft.formDetails.length + '条<button>展开</button>') : usoft, current:
    		_isExist ? '共' + current.formDetails.length + '条<button>展开</button>' : ''};
    	if(isExist) {//比较FormDetail
    		var u = usoft.formDetails, c = current.formDetails, n = 0;
    		for(var i in u) {
    			var x = u[i], y = null;
    			for(var j in c) {
    				if(x.fd_field == c[j].fd_field) {
    					y = c[j];
    				}
    			}
    			if(!y || !me.equals(x, y, ['fd_id', 'fd_foid'])) {
    				n++;
    			}
    		}
    		if(n > 0) {
    			dd[0].result = n;
    			this.count(n);
    		}
    	}
    	dd[1] = {conf: 'Caller', usoft: isExist ? usoft.fo_caller : '--', current: _isExist ? current.fo_caller : '--'};
    	dd[2] = {conf: '表名', usoft: isExist ? usoft.fo_table : '--', current: _isExist ? current.fo_table : '--'};
    	dd[3] = {conf: '界面名称', usoft: isExist ? usoft.fo_title : '--', current: _isExist ? current.fo_title : '--'};
    	dd[4] = {conf: '主表关键字', usoft: isExist ? usoft.fo_keyfield : '--', current: _isExist ? current.fo_keyfield : '--'};
    	dd[5] = {conf: 'Code字段', usoft: isExist ? usoft.fo_codefield : '--', current: _isExist ? current.fo_codefield : '--'};
    	dd[6] = {conf: '明细表名', usoft: isExist ? usoft.fo_detailtable : '--', current: _isExist ? current.fo_detailtable : '--'};
    	dd[7] = {conf: '明细关联字段', usoft: isExist ? usoft.fo_detailmainkeyfield : '--', current: _isExist ? current.fo_detailmainkeyfield : '--'};
    	dd[8] = {conf: '明细orderby', usoft: isExist ? usoft.fo_detailgridorderby : '--', current: _isExist ? current.fo_detailgridorderby : '--'};
    	dd[9] = {conf: '明细序号字段', usoft: isExist ? usoft.fo_detaildetnofield : '--', current: _isExist ? current.fo_detaildetnofield : '--'};
    	dd[10] = {conf: 'Buttons(新增)', usoft: isExist ? usoft.fo_button4add : '--', current: _isExist ? current.fo_button4add : '--'};
    	dd[11] = {conf: 'Buttons(读写)', usoft: isExist ? usoft.fo_button4rw : '--', current: _isExist ? current.fo_button4rw : '--'};
    	dd[12] = {conf: '审批流Caller', usoft: isExist ? usoft.fo_flowcaller : '--', current: _isExist ? current.fo_flowcaller : '--'};
    	dd[13] = {conf: '状态字段', usoft: isExist ? usoft.fo_statusfield : '--', current: _isExist ? current.fo_statusfield : '--'};
    	dd[14] = {conf: '状态码字段', usoft: isExist ? usoft.fo_statuscodefield : '--', current: _isExist ? current.fo_statuscodefield : '--'};
    	dd[15] = {conf: '明细状态', usoft: isExist ? usoft.fo_detailstatus : '--', current: _isExist ? current.fo_detailstatus : '--'};
    	dd[16] = {conf: '明细状态码', usoft: isExist ? usoft.fo_detailstatuscode : '--', current: _isExist ? current.fo_detailstatuscode : '--'};
    	dd[17] = {conf: '录入人字段', usoft: isExist ? usoft.fo_recorderfield : '--', current: _isExist ? current.fo_recorderfield : '--'};
    	dd[18] = {conf: '提交触发流程', usoft: isExist ? usoft.fo_isautoflow : '--', current: _isExist ? current.fo_isautoflow : '--'};
    	Ext.getCmp('form').store.loadData(dd);
		if(!this.equals(usoft, current, ['fo_id', 'formDetails'])) {
			this.count();
		}
    },
    compareGrid: function(grids) {
    	var me = this, usoft = grids[0], current = grids[1], dd = [], 
    		isExist = usoft instanceof Array,
    		_isExist = current instanceof Array;
    	dd[0] = {conf: '详细', usoft: isExist ? ('共' + usoft.length + '条<button>展开</button>') : usoft, current:
    		_isExist ? '共' + current.length + '条<button>展开</button>' : ''};
    	if(isExist) {
    		var n = 0;
    		for(var i in usoft) {
    			var x = usoft[i], y = null;
    			for(var j in current) {
    				if(x.dg_field == current[j].dg_field) {
    					y = current[j];
    				}
    			}
    			if(!y || !me.equals(x, y, ['dg_id'])) {
    				n++;
    			}
    		}
    		if(n > 0) {
    			dd[0].result = n;
    			this.count(n);
    		}
    	}
    	Ext.getCmp('detailgrid').store.loadData(dd);
    },
    compareDbfindSetui: function(sets) {
    	var me = this, usoft = sets[0], current = sets[1], dd = [], 
			isExist = usoft instanceof Array,
			_isExist = current instanceof Array;
		dd[0] = {conf: '详细', usoft: isExist ? ('共' + usoft.length + '条<button>展开</button>') : usoft, current:
			_isExist ? '共' + current.length + '条<button>展开</button>' : ''};
		if(isExist) {
			var n = 0;
			for(var i in usoft) {
				var x = usoft[i], y = null;
				for(var j in current) {
					if(x.ds_whichui == current[j].ds_whichui) {
						y = current[j];
					}
				}
				if(!y || !me.equals(x, y, ['ds_id'])) {
					n++;
				}
			}
			if(n > 0) {
				dd[0].result = n;
				this.count(n);
			}
		}
		Ext.getCmp('dbfindsetui').store.loadData(dd);
    },
    compareDbfindSet: function(sets) {
    	var me = this, usoft = sets[0], current = sets[1], dd = [], 
			isExist = usoft instanceof Array,
			_isExist = current instanceof Array;
		if(isExist) {
			var n = 0;
			for(var i in usoft) {
				var x = usoft[i], y = null;
				dd[0 + 7*i] = {conf: 'Caller', usoft: x.ds_caller, current: ''};
				dd[1 + 7*i] = {conf: '表名', usoft: x.ds_tablename, current: ''};
				dd[2 + 7*i] = {conf: '描述', usoft: x.ds_caption, current: ''};
				dd[3 + 7*i] = {conf: '分组SQL', usoft: x.ds_groupby, current: ''};
				dd[4 + 7*i] = {conf: '排序SQL', usoft: x.ds_orderby, current: ''};
				dd[5 + 7*i] = {conf: '筛选条件', usoft: x.ds_fixedcondition, current: ''};
				dd[6 + 7*i] = {conf: '明细', usoft: '共' + x.dbFindSetDetails.length + '条<button>展开</button>', 
						current: ''};
				for(var j in current) {
					if(x.ds_caller == current[j].ds_caller) {
						y = current[j];
						dd[0 + 7*i].current = y.ds_caller;
						dd[1 + 7*i].current;
						dd[2 + 7*i].current;
						dd[3 + 7*i].current;
						dd[4 + 7*i].current;
						dd[5 + 7*i].current;
						dd[6 + 7*i].current;
					}
				}
				if(y) {
					
				}
				if(!y || !me.equals(x, y, ['ds_id'])) {
					n++;
				}
			}
			if(n > 0) {
				dd[0].result = n;
				this.count(n);
			}
		}
		Ext.getCmp('dbfindset').store.loadData(dd);
    },
    getLogicTree: function(){
    	var me = this;
		var w = Ext.create('Ext.Window',{
		    title: '查找',
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
		    		var t = btn.ownerCt.ownerCt.down('treepanel'),
		    			record = t.selModel.lastSelected;
		    		if(record && record.data.leaf && record.data.caller) {
		    			Ext.getCmp('sn_displayname').setValue(record.data.text);
		    			Ext.getCmp('sn_caller').setValue(record.data.caller);
		    			Ext.getCmp('sn_caller').show();
		    			me.lastSelected = record;
		    			btn.ownerCt.ownerCt.close();
		    		}
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
        	url : basePath + 'ma/lazyTree.action',
        	params: {
        		parentId: pid,
        		condition: 'sn_limit=1'
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
	},
	showFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(!filter) {
    		filter = this.createFilterPanel(btn);
    	}
    	filter.show();
    },
    hideFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(filter) {
    		filter.hide();
    	}
    },
    createFilterPanel: function(btn) {
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '条件',
    		width: 500,
    		height: 300,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
    	    	xtype: 'fieldset',
    	    	title: '按导航栏查询',
    	    	checkboxToggle: true,
    	    	items: [{
    	    		xtype: 'displayfield',
    	    		value: '* 根据导航栏节点配置的Caller,分析关联的Form、DetailGrid、DataList、Dbfind、Button、Table、DataDictionary、Index等的配置差异'
    	    	},{
					xtype: 'triggerfield',
					triggerCls: 'x-form-search-trigger',
					fieldLabel: '选择页面',
					name: 'sn_displayname',
					id: 'sn_displayname',
					margin: '3 10 3 3',
					width: 400
				}, {
					xtype: 'displayfield',
					hidden: true,
					name: 'sn_caller',
					id: 'sn_caller'
				}]
    	    }, {
    	    	xtype: 'fieldset',
    	    	title: '按Caller查询',
    	    	checkboxToggle: true,
    	    	collapsed: true,
    	    }, {
    	    	xtype: 'fieldset',
    	    	title: '按Table查询',
    	    	checkboxToggle: true,
    	    	collapsed: true,
    	    }],
    	    buttonAlign: 'center',
    	    buttons: [{
    	    	text: '确定',
    	    	cls: 'x-btn-blue',
    	    	id: 'analysis'
    	    }, {
    	    	text: '关闭',
    	    	cls: 'x-btn-blue',
    	    	handler: function(btn) {
    	    		btn.ownerCt.ownerCt.hide();
    	    	}
    	    }]
    	});
    	return filter;
    },
    showOtherConf: function() {
    	var win = Ext.getCmp('win-others');
    	if(!win) {
    		win = Ext.create('Ext.Window', {
        		id: 'win-others',
        		style: 'background:#f1f1f1',
        		title: '其它配置项',
        		width: 500,
        		height: 300,
        	    layout: 'column',
        	    defaults: {
        	    	margin: '2 2 2 10',
        	    	xtype: 'checkbox',
        	    	columnWidth: .33,
        	    	checked: true
        	    },
        	    items: [{
        	    	boxLabel: '全选/反选',
        	    	columnWidth: 1,
        	    	listeners: {
        	    		change: function(f) {
            	    		var g = f.ownerCt.down('checkboxgroup');
            	    		if(f.checked) {
            	    			Ext.each(g.items.items, function(c){
            	    				c.setValue(true);
            	    			});
            	    		} else {
            	    			Ext.each(g.getChecked(), function(c){
            	    				c.setValue(false);
            	    			});
            	    		}
            	    	}
        	    	}
        	    }, {
        	    	xtype: 'checkboxgroup',
        	        fieldLabel: '',
        	        columnWidth: 1,
        	        columns: 2,
        	        vertical: true,
        	        items: [
        	            { boxLabel: '特殊权限库', name: 'rb', inputValue: '1' },
        	            { boxLabel: '业务逻辑设计', name: 'rb', inputValue: '2', checked: true },
        	            { boxLabel: '出入库参数', name: 'rb', inputValue: '4' },
        	            { boxLabel: '初始化系统配置', name: 'rb', inputValue: '5' },
        	            { boxLabel: '数据库视图', name: 'rb', inputValue: '6' },
        	            { boxLabel: '数据库函数', name: 'rb', inputValue: '7' },
        	            { boxLabel: '数据库类型', name: 'rb', inputValue: '8' },
        	            { boxLabel: '数据库过程', name: 'rb', inputValue: '9' }
        	        ]
        	    }]
    		});
    	}
    	win.show();
    },
	equals: function( x, y, ignores ) {
		var me = this;
        if ( x === y ) {
            return true;
        }
        if ( ! ( x instanceof Object ) || ! ( y instanceof Object ) ) {
            return false;
        }
        if ( x.constructor !== y.constructor ) {
            return false;
        }
        for ( var p in x ) {
        	var b = true;
        	for( var i in ignores ) {
        		if(ignores[i] == p) {
        			b = false;
        			break;
        		}
        	}
        	if( !b ) {
        		continue;
        	}
            if ( x.hasOwnProperty( p ) ) {
                if ( ! y.hasOwnProperty( p ) ) {
                    return false;
                }
                if ( x[ p ] === y[ p ] ) {
                    continue;
                }
                if ( typeof( x[ p ] ) !== "object" ) {
                    return false;
                }
                if ( ! me.equals( x[ p ],  y[ p ] ) ) {
                    return false;
                }
            }
        }
        for ( p in y ) {
        	var b = true;
        	for( var i in ignores ) {
        		if(ignores[i] == p) {
        			b = false;
        			break;
        		}
        	}
        	if( !b ) {
        		continue;
        	}
            if ( y.hasOwnProperty( p ) && ! x.hasOwnProperty( p ) ) {
                return false;
            }
        }
        return true;
    }
});