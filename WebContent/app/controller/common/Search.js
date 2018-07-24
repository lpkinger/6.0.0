Ext.QuickTips.init();
Ext.define('erp.controller.common.Search', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.GridUtil', 'erp.util.BaseUtil'],
	views : [ 'common.search.Viewport', 'core.trigger.DbfindTrigger', 'core.form.FtField',
			'core.form.ConDateField', 'core.form.YnField','core.button.Sync',
			'core.form.FtDateField', 'core.form.YearDateField', 'core.form.OperatorField', 'core.form.OperatorDateField',
			'core.form.MonthDateField', 'core.form.FtFindField', 'core.form.FtNumberField',
			'core.grid.YnColumn', 'core.grid.TfColumn',
			'core.form.ConMonthDateField', 'core.trigger.TextAreaTrigger','core.trigger.AddDbfindTrigger','core.trigger.MultiDbfindTrigger' ],
	models : ['SearchTemplate'],
	refs : [ {
		ref : 'grid',
		selector : '#querygrid'
	}, {
		ref: 'dataCount',
		selector: '#dataCount'
	} ],
	init : function() {
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'button[name=find]' : {
				click : function(btn) {
					this.onQueryClick();
				},
				afterrender : function() {
					Ext.defer(function(){
						me.onQueryClick();
					}, 500);
				}
			},
			'button[name=group]' : {
				click : function(btn) {
					this.onGroupClick();
				}
			},
			'button[name=close]' : {
				click : function() {
					if (parent.Ext && parent.Ext.getCmp('content-panel')) {
						parent.Ext.getCmp('content-panel').getActiveTab().close();
					} else {
						window.close();
					}
				}
			},
			'button[name=clearcondition]' : {
				click : function() {
					if (this.querywin) {
						var g = this.querywin.down('grid');
						g.store.removeAll();
						g.loadEmptyData();
					}
				}
			},
			'button[name=filter]' : {
				click : function() {
					
				}
			},
			'button[name=sort]' : {
				click : function() {
					var items = this.searchTemplate ? this.searchTemplate.items : [],
						sorts = this.searchTemplate ? this.searchTemplate.st_sorts : null;
					this.onSortClick(items, sorts, function(sortSql, newSorts){
						me.getGrid().store.sort(newSorts);
						me.searchTemplate.st_sorts = sortSql;
					});
				}
			},
			'button[name=temp]' : {
				click : function() {
					warnMsg('保存当前设置到方案?', function(b){
						if(b == 'ok' || b == 'yes') {
							me.updateTemp();
						}
					});
				}
			},
			'menuitem[name=exportexcel]' : {
				click : function() {
					var temp = Ext.getCmp('temp');
					me.exportExcel(me.getGrid(), temp.selModel.getSelection(), me.getGrid().queryFilter);
				}
			},
			'menuitem[name=exportpdf]' : {
				click : function() {
					this.BaseUtil.exportPdf(this.getGrid().normalGrid);
				}
			},
			'button[name=lock]' : {
				click : function() {
					this.onLockClick();
				}
			},
			'menuitem[name=sum]' : {
				click : function() {
					
				}
			},
			'menuitem[name=average]' : {
				click : function() {
					
				}
			},
			'menuitem[name=max]' : {
				click : function() {
					window.open(window.location.href);
				}
			},
			'menuitem[name=webexcel]' : {
				click : function() {
					window.open(basePath + 'jsps/Excel/sheet/index.jsp');
				}
			},
			'button[name=removeformat]' : {
				click : function() {
					this.onClear();
				}
			},
			'button[name=clearall]' : {
				click : function() {
					this.getGrid().normalGrid.store.loadData([{},{},{},{},{},{},{},{},{},{}]);
				}
			},
			'menuitem[name=template-copy]': {
				click: function(item) {
					var record = item.ownerCt.record;
					if (record) {
						var t = record.get('st_title');
						me.addTemp(t + '(新)', function(title){
							me.copyTemp(title, record.get('st_id'));
						});
					}
				}
			},
			'menuitem[name=template-title]': {
				click: function(item) {
					var record = item.ownerCt.record;
					if (record) {
						me.addTemp(record.get('st_title'), function(title){
							me.onTempTitleChange(title, record.get('st_id'), function(){
								record.set('st_title', title);
							});
						});
					}
				}
			},
			'menuitem[name=template-set]': {
				click: function(item) {
					var record = item.ownerCt.record;
					if (record) {
						me.onTempSet(record, record.get('items'), record.get('st_id'));
					}
				}
			},
			'menuitem[name=template-del]': {
				click: function(item) {
					var grid = item.ownerCt.grid, record = item.ownerCt.record;
					if (record) {
						warnMsg('确定删除 ' + record.get('st_title') + ' ?', function(k){
                    		if(k == 'ok' || k == 'yes') {
                    			me.onTempDel(record.get('st_id'), function(){
                    				Ext.example.msg('info', '提示', record.get('st_title') + ' 删除成功', 2000);
                					grid.store.remove(record);
                    				record = grid.store.first();
                    				if(record) {
                    					grid.selModel.select(record);
                    				}
                    			});
                    		}
                    	});
					}
				}
			}
		});
		if(!window.onLinkClick)
			window.onLinkClick = function(l, t, f, arg1, arg2){
				me.onLinkClick(l, t, f, arg1, arg2);
			};
	},
	onQueryClick : function() {
		var me = this, win = me.querywin;
		if (!win) {
			var form  = me.createQueryForm(), temp = me.createTempGrid();
			win = me.querywin = Ext.create('Ext.window.Window', {
				closeAction : 'hide',
				title : '筛选',
				height: 500,
        		width: 550,
        		layout: 'anchor',
				items : [form, temp],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpQueryButton,
					height : 26,
					handler : function(b) {
						var items = temp.selModel.getSelection();
						if(items.length > 0) {
							me.onQuery(items[0].data);
						}
					}
				},{
					text : '导出数据',
					height : 26,
					handler : function(b) {
						if(!form.isValid()) {
							Ext.example.msg('warning', '警告', '请先填写 ' + Ext.Array.unique(form.getInvalidFields()).join(','), 5000);
							return;
						}
						me.exportExcel(me.getGrid().normalGrid, temp.selModel.getSelection(), Ext.encode(form.getFilter()));
						b.ownerCt.ownerCt.hide();
					}
				},{
					text : '复制方案到...',
					iconCls: '',
					height : 26,
					handler : function(b) {
						var items = temp.selModel.getSelection();
						if(items.length > 0) {
							me.duplicateTemp(items[0].data);
						}
					}				
				},{
					text : '导出方案',
					height : 26,
					handler : function(b) {
						window.open(basePath + 'common/dump/exp.action?type=SearchTemplate&identity=' + caller);
					}
				},{
					height : 26,
					iconCls: '',
					xtype: 'erpSyncButton',
					itemId : 'sync',
					caller:'Search!Post',
					style : {
						marginLeft : '0px'
					},
					autoClearCache: true					
				},{
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
			me.onTempLoad(function(data, lastId){
				temp.store.loadData(data);
				if(data.length > 0) {
					var r = (lastId && temp.store.findRecord('st_id', lastId)) || temp.store.first();
					temp.selModel.select(r);
					form.setTitle(r.get('st_title'));
				}
			});
		}
		win.show();
	},
    createTempGrid : function() {
    	var me = this;
    	return Ext.create('Ext.grid.Panel', {
    		id: 'temp',
    		cls: 'custom-grid',
			title: '自定义方案',
			region: 'south',
			anchor: '100% 40%',
			autoScroll: true,
			tools: [{
				type: 'plus',
				tooltip: '添加方案',
				handler: function(e, el, tb, tool) {
					me.addTemp();
				}
			}],
			columns: [{
				text: '日期',
				dataIndex: 'st_date',
				renderer: function(v) {
					return Ext.Date.format(new Date(v), 'Y-m-d');
				},
				flex: 1
			},{
				text: '创建人',
				dataIndex: 'st_man',
				flex: 0.8
			},{
				text: '描述',
				dataIndex: 'st_title',
				flex: 3
			},{
				text: 'APP端运用',
				dataIndex: 'st_appuse',
				xtype:"checkcolumn",
				flex: 1,
				listeners:{
					checkchange: function(cm, y, check){
						var grid=cm.ownerCt.ownerCt;
						var store=grid.store;
						var st_id=store.data.items[y].data.st_id;
						if(!st_id)return;
						Ext.Ajax.request({
							url:basePath+"ma/search/saveAppuse.action",
							method:'post',
							params:{
								st_id:st_id,
								check:check?1:0
							},
							callback:function(opts,suc,res){
								var res=Ext.decode(res.responseText);
								if(res.exception){
									showError(res.exception);
									return;
									}
								if(res.success){
								grid.getStore().commitChanges();
								}
							}
						});												
					}
				}				
			},{
				xtype: 'actioncolumn',
				flex: 1,
				align: 'center',
                items: [{
                	iconCls: 'x-button-icon-install',
                    tooltip: '设置',
                    handler: function(grid, rIdx, cIdx) {
                    	var record = grid.store.getAt(rIdx);
                        me.onTempSet(record, record.get('items'), record.get('st_id'));
                    }
                },{
                	iconCls: 'x-button-icon-delete',
                    tooltip: '删除',
                    handler: function(grid, rIdx, cIdx) {
                    	var record = grid.store.getAt(rIdx);
                    	warnMsg('确定删除 ' + record.get('st_title') + ' ?', function(k){
                    		if(k == 'ok' || k == 'yes') {
                    			me.onTempDel(record.get('st_id'), function(){
                    				Ext.example.msg('info', '提示', record.get('st_title') + ' 删除成功', 2000);
                					grid.store.remove(record);
                    				record = grid.store.first();
                    				if(record) {
                    					grid.selModel.select(record);
                    				}
                    			});
                    		}
                    	});
                    }
                }]
			}],
			selModel: Ext.create('Ext.selection.CheckboxModel',{
				mode: 'SINGLE',
				listeners: {
					select : function(sm, record, idx) {
						var items = record.get('items');
						var title = record.get('st_title');
						if(items && title) {
							var f = me.querywin.down('form');
							f.setTitle(title);
							me.formatTempSet(items, record.get('properties'), record.get('pre_hook'), true);
						}
						me.querywin.down('erpSyncButton[itemId=sync]').syncdatas = record.get('st_id');
						me.searchTemplate = record.data;
					}
				}
    		}),
    		columnLines: true,
    		store: new Ext.data.Store({
    			fields: ['st_date', 'st_man', 'st_title', 'items', 'properties', 'st_id','st_condition','st_usedtable','st_tablesql','st_sorts', 'st_limits', 'pre_hook', 'pre_hook_type','st_appuse']
    		}),
    		viewConfig: {
    			listeners: {
    				itemcontextmenu: function(view, record, item, index, e) {
    					me.onTempContextmenu(view, record, e);
    				}
    			}
    		}
    	});
    },
    /**
     * 方案grid，右键菜单
     */
    onTempContextmenu: function(view, record, e) {
    	e.preventDefault();
		var menu = view.contextMenu;
		if (!menu) {
			menu = view.contextMenu = new Ext.menu.Menu({
				items: [{
					text : '复制方案',
					name: 'template-copy'
				},{
					text : '修改描述',
					name: 'template-title'
				},{
					text : '编辑方案',
					name: 'template-set'
				},{
					iconCls: 'icon-remove',
					text : '删除',
					name: 'template-del'
				}]
			});
			menu.grid = view.ownerCt;
		}
		menu.showAt(e.getXY());
		menu.record = record;
    },
    formatTempSet : function(items, properties, hook, load) {
    	var me = this, grid = this.getGrid(),isMultiItem=false;
    	var datas = new Array(), columns = new Array(), alias = new Array(),
    		group = null, locks = new Array(), temp = new Array(), v, w = 0.5, 
    		hookParams = Ext.Object.getKeys(me.parseHook(hook).params), allowBlank;
    	Ext.Array.each(items, function(i){
    		var modeItems = [];
    		if(1 === i.stg_query) {
    			// 执行pre-hook所需字段必须有值
    			allowBlank = hookParams.indexOf(':' + i.stg_table + '.' + i.stg_field) == -1;
    			if(i.stg_operator) {
    				var dataType = me.getTypeByStg(i.stg_type),isFormula=!Ext.isEmpty(i.stg_formula);
    				datas.push({
        				xtype: ('datecolumn' == dataType ? 'operatordatefield' : 'operatorfield'),
        				name: (isFormula?i.stg_formula:i.stg_field),
        				isFormula:isFormula,
        				fieldLabel: i.stg_text,
        				relativeTable: i.stg_table,
        				labelWidth: 100,
        				columnWidth: .5,
        				operator: i.stg_operator,
        				dataType: dataType,
        			    value: i.stg_value,
        			    allowBlank: allowBlank
        			});
    			} else if (i.stg_mode && i.stg_mode != '' && properties) {// multi properties field
    				Ext.Array.each(properties, function(p){
						if(p.stg_field == i.stg_field) {
							modeItems.push({
								display: p.display,
								value: p.value
							});
						}
					});
    				if(i.stg_mode == 'checkboxgroup' || i.stg_mode == 'radiogroup') {
    					var items = [];
    					Ext.Array.each(properties, function(p){
    						if(p.stg_field == i.stg_field) {
    							items.push({
    								boxLabel: p.display,
    								inputValue: p.value,
    								name: i.stg_field,
    								checked: (i.stg_value && (i.stg_value == p.value || i.stg_value == '$ALL'))
    							});
    						}
    					});
    					datas.push({
        					xtype: i.stg_mode,
        					fieldLabel: i.stg_text,
        					relativeTable: i.stg_table,
        					columnWidth: 1,
        					columns: 3,
        			        vertical: true,
        			        items: items,
        			    	allowBlank: allowBlank
        				});
    				} else if (i.stg_mode == 'combobox') {
    					var store = new Ext.data.Store({
    						fields: ['display', 'value'],
    						data: Ext.Array.merge([{
    							display: '全部',
    							value: '$ALL'
    						},{
    							display: '无',
    							value: '$NULL'
    						}], modeItems)
    					});
    					datas.push({
        					xtype: i.stg_mode,
        					name: i.stg_field,
        					fieldLabel: i.stg_text,
        					relativeTable: i.stg_table,
        					columnWidth: .5,
        			        store: store,
        			        queryMode: 'local',
        			        displayField: 'display',
        			        valueField: 'value',
        			        value: i.stg_value,
        			    	allowBlank: allowBlank
        				});
    				}    					
    			} else {
    				w = .5;
        			v = i.stg_value;
    				var t = 'textfield', type = i.stg_type.toUpperCase();
    				if(/(DATE|TIMESTAMP)(\(\d+\)){0,1}/.test(type)) {
    					t = 'datefield';
    					if(!Ext.isEmpty(v)) {
    						w = 1;
    						isMultiItem = true;
    						switch(v) {
    						case '今天':
    							t = 'condatefield';v = 1;break;
    						case '昨天':
    							t = 'condatefield';v = 2;break;
    						case '本月':
    							t = 'condatefield';v = 3;break;
    						case '上个月':
    							t = 'condatefield';v = 4;break;
    						case '本年度':
    							t = 'condatefield';v = 5;break;
    						case '上年度':
    							t = 'condatefield';v = 6;break;
    						case '自定义':
    							t = 'condatefield';v = 7;break;
    						default:
    							if(!Ext.isDate(v) && /\d{4}-\d{2}-\d{2}/.test(v)) {
    								v = Ext.Date.parse(v, 'Y-m-d');
    							}
    							break;
    						}
    					}
    				} else if(/(NUMBER|FLOAT|INT)(\(\d+\)){0,1}/.test(type)) {
    					t = 'numberfield';
    				}
    				if(1 == i.stg_dbfind) {
    					t = 'dbfindtrigger';
    				} else if(2 == i.stg_dbfind) {
    					t = 'adddbfindtrigger';
    				} else if(3 == i.stg_dbfind){
    					t = 'multidbfindtrigger';
    				}
    				if(1 == i.stg_double) {
    					w = 1;
    					if(t == 'numberfield') {
    						t = 'erpFtNumberField';
    					} else if (t == 'dbfindtrigger') {
    						t = 'ftfindfield';
    					} else if (t == 'datefield') {
    						t = 'ftdatefield';
    					} else if (t == 'textfield'){
    						t = 'erpFtField';
    					}
    					if(i.stg_format == 'Ym') {
    						t = 'conmonthdatefield';
    						if(!Ext.isEmpty(v)) {
        						switch(v) {
        						case '本月':
        							v = Ext.Date.format(new Date(),'Ym');break;
        						default:
        							break;
        						}
        					}
    					}
    				}else if(0 == i.stg_double){
    					if(!isMultiItem){
    						w = .5;
    					}
    					if(i.stg_format == 'Ym') {
    						t = 'monthdatefield';
    					}    				
    				};
    				datas.push({
        				fieldLabel: i.stg_text,
        				relativeTable: i.stg_table,
        				labelWidth: 100,
        				xtype: t,
        				name: i.stg_field,
        				id: i.stg_field,
        				value: v,
        				columnWidth: w,
        			    allowBlank: allowBlank
        			});
    			}
    		}
    		if(1 === i.stg_group) {
    			group = i.stg_alias;
    		}
    		if(1 === i.stg_lock) {
    			locks.push(i.stg_alias);
    		}
    		var xtype = i.stg_mode ? null : me.getTypeByStg(i.stg_type);// 设置多选/单选/下拉框了的，grid列统一按字符串格式
    		if(1 === i.stg_use) {
    			var col = {
        				text: i.stg_text,
        				xtype: xtype,
        				dataIndex: i.stg_alias,
        				dataField: i.stg_field,
        				dataTable: i.stg_table,
        				format: (i.stg_format == 'Ym') ? null : i.stg_format,
        				width: i.stg_width,
        				summaryType: i.stg_sum == 1 ? 'sum' : null,
        				align: xtype == 'numbercolumn' ? 'right' : 'left',
        				locked: i.stg_lock==1
        			};
    			if(i.stg_link) {
    				col.renderer = function(value, p, record) {
    					if(value) {
	    			        return Ext.String.format(
	    			        		'<a href="javascript:void(0)" onclick="onLinkClick(\'' + i.stg_link + '\',\'{2}\',\'{3}\',\'{0}\',\'{1}\')">{2}</a>',
	    			                record.get(i.stg_tokencol1),
	    			                record.get(i.stg_tokencol2),
	    			                value,
	    			                i.stg_field
	    			            );
	    			        return null;
	    			    };
	    			};
    			}
    			columns.push(col);
    		}
    		temp.push({
    			stg_text: i.stg_text,
    			stg_use: i.stg_use,
    			stg_field: i.stg_field,
    			stg_value: i.stg_value,
    			stg_lock: i.stg_lock == 1,
    			stg_group: i.stg_group == 1,
    			stg_sum: i.stg_sum == 1,
    			stg_dbfind: i.stg_dbfind == 1,
    			stg_double: i.stg_double == 1,
    			stg_query: i.stg_query == 1,
    			stg_width: i.stg_width,
    			stg_type: i.stg_type,
    			stg_table: i.stg_table,
    			stg_format: i.stg_format,
    			stg_formula: i.stg_formula,
    			stg_mode: i.stg_mode,
    			stg_operator: i.stg_operator,
    			modeItems: i.stg_mode ? (modeItems || [{dispay:null,value:null}]) : modeItems,
    			type: xtype,
    			links: i.links,
    			stg_link: i.stg_link,
    			stg_tokentab1: i.stg_tokentab1,
    			stg_tokencol1: i.stg_tokencol1,
    			stg_tokentab2: i.stg_tokentab2,
    			stg_tokencol2: i.stg_tokencol2,
    			stg_appuse:i.stg_appuse,
    			stg_appcondition:i.stg_appcondition
    		});
    	});
    	// 加载筛选条件
    	if(load) {
    		var f = this.querywin.down('form');
    		f.removeAll();
	        f.add(datas);
	        var alias = Ext.Array.pluck(columns, 'dataIndex');
	        Ext.Array.each(temp, function(t){
	        	if(t.stg_tokencol1 && !Ext.Array.contains(alias, t.stg_tokencol1)) {
	        		alias.push(t.stg_tokencol1);
	        	}
	        	if(t.stg_tokencol2 && !Ext.Array.contains(alias, t.stg_tokencol2)) {
	        		alias.push(t.stg_tokencol2);
	        	}
	        });
	        Ext.suspendLayouts();
	        grid.reconfigure(new Ext.data.Store({
	        	fields: alias
	        }), columns);
	        Ext.resumeLayouts(true);
	    	// 加载分组条件
	        this.groupfield = group;
	        this.toogleGroup(group, true);
	        // 固定列
	        this.lockfields = locks;
	    	// 加载合计
    	}
    	if(this.lockwin){
        	this.lockwin.destroy();
        	this.lockwin = null;
        }
        if( this.groupwin){
        	this.groupwin.destroy();
        	this.groupwin = null;
        }
        
		me.getDataCount().hide();
    	return temp;
    },
    onTempSet : function(record, items, sId) {
    	var me = this, win = me.tempwin, temp = me.formatTempSet(items, record.get('properties'), record.get('pre_hook'), false);
		if (!win) {
			var sGrid  = me.getSettingGrid();
			Ext.define("Post", {
		        extend: 'Ext.data.Model',
		        proxy: {
		            type: 'ajax',
		            url : basePath + 'ma/dataDictionary/search.action',
		            reader: {
		                type: 'json',
		                root: 'datas',
		                totalProperty: 'totalCount'
		            },
		            headers: {
                        'Content-Type': 'application/json;charset=utf-8'
                    }
		        },
		        fields: [
		            {name: 'desc', mapping: 'comments'},
		            {name: 'table', mapping: 'table_name'}
		        ]
		    });
		    ds = Ext.create('Ext.data.Store', {
		        pageSize: 10,
		        model: 'Post'
		    });
			win = me.tempwin = Ext.create('Ext.window.Window', {
				header: {
					items: [{
						xtype: 'tbtext',
						id: 'set-title',
						text: '方案设置',
						style: 'font-weight: 700;'
					}, {
						xtype: 'tbtext',
						id: 'set-info',
				    	tpl: Ext.create('Ext.XTemplate',
				    			'共 <strong>{tableCount}</strong> 个表关联，' + 
				    			'可选 <strong>{columnCount}</strong> 列 ' +
				    			'已选 <strong>{usedCount}</strong> 列')
					}]
				},
				closeAction : 'hide',
				width : '100%',
				height : '80%',
				layout: 'border',
				items : [{
					region: 'west',
					width: 400,
					layout: 'accordion',
					id: 'dictionary',
					tbar: [{
						xtype: 'combo',
			            store: ds,
			            width: 360,
			            displayField: 'desc',
			            emptyText: '查找数据字典',
			            typeAhead: false,
			            hideLabel: true,
			            hideTrigger:true,
			            minChars: 3,
			            listConfig: {
			            	minHeight: 360,
			            	maxHeight: 360,
			                loadingText: '查找中...',
			                emptyText: '<h3>没有找到您需要的数据字典.</h3>',
			                getInnerTpl: function() {
			                    return '<div style="padding: 5px 10px;">' + 
			                    			'<span style="font-weight:bold;font-size:120%;">' +
			                    				'<tpl if="desc">{desc}<tpl else>{table}</tpl>' +
			                    			'</span>' + 
			                    			'<span style="float:right;">{table}</span>' + 
			                    		'</div>';
			                }
			            },
			            pageSize: 10,
			            listeners: {
			            	select: function(combo, records, opts) {
			            		me.addDictionary(win, records[0].get('table'));
			            		combo.reset();
			            	}
			            },
			            getParams: function(queryString) {
			                var params = {},
			                    param = this.queryParam;
			                if (param) {
			                    params[param] = escape(queryString);
			                }
			                return params;
			            }
					},'->', {
						xtype: 'tool',
						type: 'left',
						style: {
							marginRight: '5px'
						},
						tooltip: '收拢',
						handler: function(e, el, tb, tool) {
							tb.ownerCt.collapse(Ext.Component.DIRECTION_LEFT, true);
						}
					}]
				},{
					region: 'center',
					layout: 'anchor',
					items: [sGrid]
				}],
				buttonAlign : 'center',
				buttons : [{
					text : '保存到方案',
					height : 26,
					width:100,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						me.updateTemp(function(){
							w.hide();
						});
					}
				},{
					text : '作为新方案保存',
					id: 'new_temp_btn',
					height : 26,
					width:100,
					hidden : (sId <= 0),
					handler : function(b) {
						var t = win.relativeRecord.get('st_title');
						me.addTemp(t + '(新)', function(title){
							var w = b.ownerCt.ownerCt;
							w.hide();
							me.copyTemp(title);
						});
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					width:100,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
		if(sId <= 0) {
			win.down('#new_temp_btn').hide();
		} else {
			win.down('#new_temp_btn').show();
		}
		var g = win.down('#grid-setting');
		g.store.loadData(temp);
		g.sorts = record.get('st_sorts');
		g.limits = record.get('st_limits');
		g.defaultCondition = record.get('st_condition');
		g.preHook = record.get('pre_hook');
		// 加载数据字典
		var dic = win.down('#dictionary'), 
			usedTabs = record.get('st_usedtable'), tabSql = record.get('st_tablesql');
		if(dic.tables != usedTabs || win.relativeId != sId) {
			g.down('#set-tab-info').setTooltip(me.getCodeTip({
				usedTabs: usedTabs == null ? '' : usedTabs.split(','), 
				tabSql: tabSql,
				condSql: record.get('st_condition'),
				sortSql: g.sorts,
				data: temp
			}));
			me.getDictionary(usedTabs, function(datas){
				dic.tables = usedTabs;
				dic.removeAll();
				var c = 0;
				for(var i in datas) {
					dic.add(me.createDictionaryGrid(datas[i]));
					c += datas[i].dataDictionaryDetails.length;
				}
				win.down('#set-info').update({
					tableCount: datas.length,
					columnCount: c,
					usedCount: temp.length
				});
			});
		}
		if(win.relativeId != sId) {
			win.down('#set-title').setText(record.get('st_title'));
			win.relativeRecord = record;
			win.relativeId = sId;
		}
    },
    /**
     * 显示方案sql的tip
     */
    getCodeTip: function(config) {
    	var me = this, oldConf = me.templateConfig;
    	if(!oldConf)
    		oldConf = {};
    	me.templateConfig = config = Ext.Object.merge(oldConf, config);
    	return new Ext.tip.ToolTip({
			target: 'set-tab-info',
			cls: 'tip-custom',
			autoHide: false,
			hideDelay: 0,
			html: me.getCodeHtml(config)
		});
    },
    /**
     * 代码html
     */
    getCodeHtml: function(datas) {
    	return new Ext.XTemplate(
    		'<strong class="code-title">数据字典：</strong>' + 
    		'<tpl for="usedTabs">' + 
    			'<label class="label label-info">{.}</label>' +
    		'</tpl>' +
    		'<strong class="code-title">关联SQL：</strong>' +
    		'<div class="code-body">{tabSql:this.format}</div>' +
    		'<strong class="code-title">排序SQL：</strong>' +
    		'<div class="code-body">{sortSql:this.orderBy}</div>' +
    		'<strong class="code-title">条件SQL：</strong>' +
    		'<div class="code-body">{condSql:this.format}</div>' +
    		'<strong class="code-title">查询SQL：</strong>' +
    		'<div class="code-body">{[this.getSearchSql(values)]}</div>', {
    		keywords : ['SELECT ', ' FROM ', ' WHERE ', ' LEFT JOIN ', ' ON ', 'ORDER BY ', ' AND ', ' DESC', ' ASC', 'TO_CHAR'],
    		format: function(sql) {
    			if(!sql) return null;
    			var kw = this.keywords;
    			for(var i in kw) {
    				var reg = new RegExp(kw[i], 'g');
        			sql = sql.replace(reg, '<span class="code-key">' + kw[i] + '</span>');
    			}
    			sql = this.replaceQuot(sql);
    			return sql;
    		},
    		replaceQuot: function(sql) {
    			var index = 0, length = sql.length, s, e = -1, c = 0, htm = '';
    			while(index < length) {
    				if((s = sql.indexOf('\'', index)) != -1 && (e = sql.indexOf('\'', s + 1)) != -1) {
    					c = 1;
    					while(sql.substring(e + c, e + c + 1) == '\'' || c%2 == 0){
    						e++;
    						c++;
    					}
    					htm += sql.substring(index, s) + '<span class="code-quot">' + sql.substring(s, e+1) +'</span>';
    					index = e + 1;
    				} else {
    					htm += sql.substring(e + 1);
    					break;
    				}
    			}
    			return htm.length > 0 ? htm : sql;
    		},
    		orderBy: function(sql) {
    			if(!sql) return null;
    			return this.format('ORDER BY ' + sql);
    		},
    		getSearchSql: function(values) {
    			var fs = [];
    			Ext.Array.each(values.data, function(d){
    				if(d.stg_type.toLowerCase() == 'date')
    					fs.push('TO_CHAR(' + d.stg_table + '.' + d.stg_field + 
    							', \'yyyy-mm-dd hh24:mi:ss\') ' + d.stg_field);
    				else
    					fs.push(d.stg_table + '.' + d.stg_field + ' ' + d.stg_field);
    				
    			});
    			return this.format('SELECT ' + fs.join(',') + ' FROM ' + values.tabSql +
    				(values.condSql ? (' WHERE (' + values.condSql + ')') : '') +
    				(values.sortSql ? (' ORDER BY ' + values.sortSql) : ''));
    		}
    	}).apply(datas);
    },
    /**
     * 添加数据字典
     */
    addDictionary: function(win, table) {
    	var me = this, dic = win.down('#dictionary');
    	if(dic.tables && Ext.Array.indexOf(dic.tables.split(','), table) > -1) {
    		Ext.example.msg('warning', '警告',  '不能添加已经存在的表', 4000);
    		return;
    	}
    	var newTabs = dic.tables ? dic.tables + ',' + table : table;
    	me.getTabSql(newTabs, function(sql){
    		me.getDictionary(table, function(datas){
    			dic.tables = newTabs;
    			var c = 0;
    			for(var i in datas) {
    				dic.add(me.createDictionaryGrid(datas[i]));
    				c += datas[i].dataDictionaryDetails.length;
    			}
    			Ext.each(dic.items.items, function(g){
    				c += g.store.getCount();
    			});
    			win.down('#set-info').update({
    				tableCount: dic.items.items.length,
    				columnCount: c
    			});
    			win.down('#set-tab-info').setTooltip(me.getCodeTip({
    				usedTabs: dic.tables.split(','), 
    				tabSql: sql
    			}));
    		});
    	});
    },
    getTypeByStg: function(t) {
    	t = t.toUpperCase();
		if(/(NUMBER|FLOAT|INT)(\(\d+\)){0,1}/.test(t))
			return 'numbercolumn';
		if(/(DATE|TIMESTAMP)(\(\d+\)){0,1}/.test(t))
			return 'datecolumn';
		return null;
    },
    /**
      * 显示数据字典的grid 
      */
    createDictionaryGrid: function(dic) {
    	var id = 'grid-' + dic.table_name, datas = [], me = this;
    	Ext.Array.each(dic.dataDictionaryDetails, function(d){
    		var nl = {
        			text: d.comments,
        			type: me.getTypeByStg(d.data_type),
        			stg_width: 100,
        			stg_text: d.comments,
        			stg_use: 1,
        			stg_field: d.column_name.toUpperCase(),
        			stg_table: d.table_name.toUpperCase(),
        			stg_type: d.data_type.toUpperCase(),
        			stg_appuse:1,
        			modeItems: [],
        			links: d.links
        		};
    		if(d.links && d.links.length > 0) {
    			nl.stg_link = d.links[0].dl_link;
    			nl.stg_tokentab1 = d.links[0].dl_tokentab1;
    			nl.stg_tokencol1 = d.links[0].dl_tokencol1;
    			nl.stg_tokentab2 = d.links[0].dl_tokentab2;
    			nl.stg_tokencol2 = d.links[0].dl_tokencol2;
    		}
    		datas.push(nl);
    	});
    	return  new Ext.grid.Panel({
    		title: dic.comments,
    		cls: 'custom-grid',
    		id: id,
    		columns: [{
    			text: '代码',
    			dataIndex: 'stg_field',
    			flex: 1,
    			filter: {
    				xtype : 'textfield'
    			}
    		},{
    			text: '描述',
    			dataIndex: 'stg_text',
    			flex: 1,
    			filter: {
    				xtype : 'textfield'
    			}
    		}],
    		plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
    		viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'grid-setting',
                    dropGroup: 'grid-setting'
                },
                listeners: {
                	drop: function(node, data) {
                		// 从配置里面拖过来的，表示删除
                		// 重新加载数据，防止出现checkcolumn的勾选错位情况
                		var newData = [];
                		data.view.store.each(function(record){
                			newData.push(record.data);
                		});
                		data.view.store.loadData(newData);
                	}
                }
            },
            selModel: new Ext.selection.RowModel({
            	mode: 'MULTI'
            }),
    		store: new Ext.data.Store({
    			model: erp.model.SearchTemplate,
    			data: datas,
    			sorters: [{
    				property: 'stg_field',
			        direction: 'ASC'
			    }]
    		})
    	});
    },
    createQueryForm : function() {
    	var me = this;
    	var form = Ext.create('Ext.form.Panel', {
    		region: 'center',
    		anchor: '100% 60%',
    		title: '(未选择方案)',
    		layout: 'column',
    		autoScroll: true,
    		defaults: {
    			columnWidth: 1,
    			margin: '4 8 4 8',
    			labelAlign:'right'
    		},
    		bodyStyle: 'background:#f1f2f5;',
    		/** 
    		 * 返回过滤条件，例如{"pu_kind":"批量采购", "pu_date":{"gte":"2017-05-01","lte":"2017-05-31"}}
    		 * eq:	=
    		 * ne:  ≠
    		 * gt: >
    		 * lt: <
    		 * gte: >=
    		 * lte: <=
    		 * like: like
    		 **/
    		getFilter: function() {
    			var filter = {};
    			Ext.each(this.items.items, function(){
    				var v = (this.getFilter ? this.getFilter() : this.getValue()), 
    					b = this.relativeTable, n = this.name, _n = b + '.' + n, t;
    				if(!Ext.isEmpty(v) && v != '$ALL') {
    					switch(this.xtype) {
    					case 'datefield':
    						filter[_n] = Ext.Date.format(v,'Y-m-d');break;
    					case 'condatefield':
    						filter[_n] = v;break;
    					case 'operatorfield':
    						filter[this.isFormula ? n : _n] = v;break;
    					case 'operatordatefield':
    						filter[this.isFormula ? n : _n] = v;break;
    					case 'ftdatefield':
    						filter[_n] = v;break;
    					case 'ftfindfield':
    						filter[_n] = v;break;
    					case 'erpFtNumberField':
    						filter[_n] = v;break;
    					case 'checkboxgroup':
    						if(!Ext.Object.isEmpty(v)) {
    							Ext.apply(filter, v);
    						}
    						break;
    					case 'radiogroup':
    						if(!Ext.Object.isEmpty(v)) {
    							Ext.apply(filter, v);
    						}
    						break;
    					case 'conmonthdatefield':
    						filter[_n] = v;break;
    					default:
    						v = String(v);
    						if(v.charAt(0) == '%' || v.charAt(v.length - 1) == '%') {
    							filter[_n] = {"like": v};
    						} else if (v == '$NULL') {
    							filter[_n] = null;
    						} else {
    							filter[_n] = v;
    						}
    						break;
    					}
    				}
    			});
    			return filter;
    		}
    	});
    	form.getInvalidFields = function() {
    		var invalid = [];
			this.getForm().getFields().each(function(field){
				if(!field.validate()) {
					if(!field.fieldLabel) {
						if(field.ownerCt.fieldLabel) {
							invalid.push(field.ownerCt.fieldLabel);
						}
					} else {
						invalid.push(field.fieldLabel);
					}
				}
			});
			return invalid;
    	};
    	return form;
    },
	onQuery : function(data) {
		var g = this.getGrid(), q = this.querywin.down('form'), end = g.maxDataSize || 10000;
		if(!q.isValid()) {
			Ext.example.msg('warning', '警告', '请先填写 ' + Ext.Array.unique(q.getInvalidFields()).join(','), 5000);
			return;
		}
		this.loadNewStore(g, {
			sId: data ? data.st_id : this.$sid,
			filter: Ext.encode(q.getFilter()), 
			sorts: data ? data.st_sorts : this.templateConfig.sorts,
			start: 1, 
			end: end
		});
		this.querywin.hide();
		this.log();//记录本次选择的方案
	},
	onGroupClick : function() {
		var me = this, win = me.groupwin;
		if (!win) {
			var form  = me.getAliaForm();
			win = me.groupwin = Ext.create('Ext.window.Window', {
				title : '分组设置',
				closeAction : 'hide',
				width : 500,
				maxHeight : 400,
				items : [form],
				buttonAlign : 'center',
				buttons : [{
					text : '取消分组',
					flag : 'cancel',
					height : 26,
					disabled : true,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						me.toogleGroup(w.down('form'), false);
						w.hide();
					}
				},{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						me.toogleGroup(w.down('form'), true);
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
			if(this.groupfield) {
				var f = win.down('radio[inputValue=' + this.groupfield + ']');
				if(f) {
					f.setValue(true);
				}
			}
		}
		win.show();
		var f = win.down('radio[value=true]'), b = win.down('button[flag=cancel]');
		b.setDisabled(!f);
	},
	onLockClick : function() {
		var me = this, win = me.lockwin;
		if (!win) {
			var form  = me.getAliaForm('checkbox');
			win = me.lockwin = Ext.create('Ext.window.Window', {
				title : '固定列设置',
				closeAction : 'hide',
				width : 500,
				maxHeight : 400,
				items : [form],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						me.onLock(w.down('form'));
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
			if(this.lockfields) {
				Ext.Array.each(this.lockfields, function(l){
					var f = win.down('checkbox[inputValue=' + l + ']');
					if(f) {
						f.setValue(true);
					}
				});
			}
		}
		win.show();
	},
	onSortClick : function(source, oldSorts, callback) {
		var me = this, win = me.sortwin;
		var oldData = [], oldProp = [];
		if(oldSorts) {
			Ext.Array.each(oldSorts.split(','), function(s, i){
				var p = s.split(' '), t = null, f = p[0], tx = '';
				if(f.indexOf('.') > 0) {// table.field
					t = f.substr(0, f.indexOf('.'));
					f = f.substr(f.indexOf('.') + 1);
				}
				if(Ext.isArray(source)) {
					var obj = Ext.Array.findBy(source, function(i){
						return i.stg_field == f;
					});
					if (obj){
						tx = obj.stg_text;
						as = obj.stg_alias;
					}
				} else {
					var field = source.findRecord('stg_field', f);
					if (field) {
						tx = field.get('stg_text');
						as = field.get('stg_alias');
					}
				}
				if (tx) {
					oldData.push({alias:as,property: f, direction: p[1], number: i+1, description: tx, table: t});
					oldProp.push(f);
				}
			});
		}
		if (!win) {
			var form  = me.getGridForm('checkbox', null, source, oldProp), 
				view = me.getSortingView(oldData);
			win = me.sortwin = Ext.create('Ext.window.Window', {
				title : '排序设置',
				closeAction : 'hide',
				width : 800,
				layout: 'hbox',
				defaults: {flex: 1},
				items : [form, view],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt, sorts = [], sortSql = [], desc = '';
						view.getStore().each(function(i){
							desc = i.get('property') + ' ' + i.get('direction');
							if(i.get('table'))
								desc = i.get('table') + '.' + desc;
							sortSql.push(desc);
							sorts.push({
								property: i.get('alias'),
								direction: i.get('direction')
							});
						});
						callback.call(me, sortSql.join(','), sorts);
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		} else {
			var form = win.down('form'), view = win.down('dataview');
			form.removeAll();
			form.add(me.createFormItems(source, oldProp));
			view.store.loadData(oldData);
		}
		win.show();
		me.onSortChange(win.down('form'), win.down('dataview'));
	},
	/**
	 * 排序设置，监听字段变化
	 */
	onSortChange: function(form, view) {
		var me = this, onDirectionChange = function() {
			var d = Ext.select("td.sort-value input"), e = d.elements;
            Ext.each(e, function(m) {
				Ext.EventManager.on(m, {
					change : function(e, el){
						var record = view.getStore().findRecord('property', el.name);
						record.set('direction', el.value);
					},
					buffer : 100
				});
            });
		};
		form.getForm().getFields().each(function(field){
			field.on('change', function(scope){
				view.getStore().loadData(me.getSortProperties(scope.ownerCt, view.getStore()));
				onDirectionChange();
			});
		});
		Ext.defer(onDirectionChange, 100);
	},
	/**
	 * 选中的排序字段view
	 */
	getSortingView: function(oldData) {
		var sortStore = new Ext.data.Store({
			fields: ['property', 'description', 'direction', 'number', 'table', 'alias'],
			sorters: [{
				property: 'number',
				direction: 'ASC'
		    }],
		    data: oldData
		});
		return Ext.create('Ext.view.View', {
	        cls: 'sort-view',
	        tpl: '<tpl for=".">' +
	                '<div class="sort"><table><tbody>' +
	                    '<tr><td class="sort-name">{description}</td></tr>' +
	                    '<tr><td class="sort-value">' +
	                    	'<em>升序 </em><input type="radio" name="{property}" value="ASC" ' +
	                    	'<tpl if="direction == &quot;ASC&quot;"> checked="checked"</tpl>' +
	                    	'/>' +
	                    '</td></tr>' +
	                    '<tr><td class="sort-value">' +
                    		'<em>降序 </em><input type="radio" name="{property}" value="DESC" ' +
                    		'<tpl if="direction == &quot;DESC&quot;"> checked="checked"</tpl>' +
                    		'/>' +
                    	'</td></tr>' +
	                '</tbody></table></div>' +
	             '</tpl>' +
	             '<ol class="sort-note">' +
	             	'<li>如果直接点击列抬头来排序，将不会使用到本次排序设置</li>' +
	             	'<li>可以拖放选中的字段来改变排序的优先顺序</li>' +
	             '</ol>',
	        itemSelector: 'div.sort',
	        overItemCls: 'sort-over',
	        selectedItemClass: 'sort-selected',
	        singleSelect: true,
	        ddGroup: 'sort',
	        store: sortStore,
	        listeners: {
	        	render: function(v) {
	        		v.dragZone = new Ext.dd.DragZone(v.ownerCt.el, {
	        			ddGroup: 'sort',
	        			getDragData: function(e) {
	        	            var sourceEl = e.getTarget(v.itemSelector, 10), d;
	        	            if (sourceEl) {
	        	                d = sourceEl.cloneNode(true);
	        	                d.id = Ext.id();
	        	                return (v.dragData = {
	        	                    sourceEl: sourceEl,
	        	                    repairXY: Ext.fly(sourceEl).getXY(),
	        	                    ddel: d,
	        	                    record: v.getRecord(sourceEl)
	        	                });
	        	            }
	        	        },
	        	        getRepairXY: function() {
	        	            return this.dragData.repairXY;
	        	        }
	        	    });
	        		v.dropZone = new Ext.dd.DropZone(v.ownerCt.el, {
	        			ddGroup: 'sort',
	        			getTargetFromEvent: function(e) {
	        				return e.getTarget('.sort .sort-over');
	        			},
	        			onNodeEnter: function(target, dd, e, data){
	        				var fly = Ext.fly(target);
	        				if(fly && typeof fly.addClass === 'function')
	        					fly.addClass('sort-target-hover');
	        			},
	        			onNodeOut: function(target, dd, e, data){ 
	        				var fly = Ext.fly(target);
	        				if(fly && typeof fly.removeClass === 'function')
	        					fly.removeClass('sort-target-hover');
	        			},
	        			onNodeOver: function(target, dd, e, data){
	        				return Ext.dd.DropZone.prototype.dropAllowed;
	        			},
	        			onNodeDrop : function(target, dd, e, data){
	        				var dragRec = dd.dragData.record,
	        					dropRec = v.getRecord(target);
	        				var i = dragRec.get('number'), j = dropRec.get('number');
	        				dragRec.set('number', j);
	        				dropRec.set('number', i);
	        				v.getStore().sort('number', 'ASC');
	        				return true;
	        			}
	        		});
	        	}
	        }
	    });
	},
	newComboConfig: function(data) {
		return {
			store: Ext.create('Ext.data.Store', {
                fields: ['display', 'value'],
                data : data
            }),
            displayField: 'display',
            valueField: 'value',
    		queryMode: 'local'
		};
	},
	getSettingGrid : function() {
		var me = this, config = {
				store: Ext.create('Ext.data.Store', {
                    fields: ['display', 'value'],
                    data : [
                        {"display": '今天', "value": '今天'},
                        {"display": '昨天', "value": '昨天'},
                        {"display": '本月', "value": '本月'},
                        {"display": '上个月', "value": '上个月'},
                        {"display": '本年度', "value": '本年度'},
                        {"display": '上年度', "value": '上年度'},
                        {"display": '自定义', "value": '自定义'}
                    ]
                }),
                displayField: 'display',
                valueField: 'value',
        		queryMode: 'local'
		};
		var formatStore = new Ext.data.Store({
            fields: ['display', 'value', 'type'],
            data: [{
            	display: '0,000.00',
            	value: '0,000.00',
            	type: 'numbercolumn'
            },{
            	display: '0,000.0000',
            	value: '0,000.0000',
            	type: 'numbercolumn'
            },{
            	display: '0,000.000000',
            	value: '0,000.000000',
            	type: 'numbercolumn'
            },{
            	display: '整数',
            	value: '0,000',
            	type: 'numbercolumn'
            },{
            	display: '年-月-日',
            	value: 'Y-m-d',
            	type: 'datecolumn'
            },{
            	display: '年-月-日 时:分:秒',
            	value: 'Y-m-d H:i:s',
            	type: 'datecolumn'
            },{
            	display: '年-月',
            	value: 'Y-m',
            	type: 'datecolumn'
            },{
            	display: '月-日 时:分',
            	value: 'm-d H:i',
            	type: 'datecolumn'
            }]
        });
		return Ext.create('Ext.grid.Panel', {
			id: 'grid-setting',
			anchor: '100% 100%',
			autoScroll : true,
			cls: 'custom-grid',
			border: false,
			columnLines: true,
			tbar: [{
				text: '自定义公式',
				iconCls: 'icon-fx',
		    	handler: function(t) {
		    		var g = t.ownerCt.ownerCt;
		    		me.onFormulaClick(g.getStore(), null, function(formula, type){
		    			g.store.add({
		    				stg_text: '输入公式名称',
		    				stg_use: true,
		    				stg_field: 'COL_' + new Date().getTime(),
		    				stg_formula: formula,
		    				stg_width: 100,
		    				stg_type: type,
		    				stg_format: ('NUMBER' == type ? '0,000.00' : null),
		    				modeItems: []
		    			});
		    			g.selModel.select(g.store.last());
		    		});
		    	}
			},{
		    	text: '权限约束',
		    	iconCls: 'icon-limit',
		    	handler: function(t) {
		    		var g = t.ownerCt.ownerCt;
		    		me.onLimitClick(g.getStore(), g.limits, function(val){
		    			g.limits = val;
		    		});
		    	}
		    },{
		    	text: '排序设置',
		    	iconCls: 'icon-sort',
		    	handler: function(t) {
		    		var g = t.ownerCt.ownerCt;
		    		me.onSortClick(g.getStore(), g.sorts, function(sortSql){
		    			g.sorts = sortSql;
		    			g.down('#set-tab-info').setTooltip(me.getCodeTip({
	    					sortSql: g.sorts
	    				}));
		    		});
		    	}
		    },{
		    	text: '筛选前执行',
		    	iconCls: 'icon-fixed',
		    	handler: function(t) {
		    		var g = t.ownerCt.ownerCt;
		    		me.onPreHookClick(g.getStore(), g.preHook, function(preHook){
		    			g.preHook = preHook;
		    		});
		    	}
		    },{
		    	text: '默认筛选条件',
		    	iconCls: 'icon-find',
		    	handler: function(t) {
		    		var g = t.ownerCt.ownerCt;
		    		me.onDefaultConditionClick(g.defaultCondition, function(val){
		    			g.defaultCondition = val;
		    			g.down('#set-tab-info').setTooltip(me.getCodeTip({
	    					condSql: g.defaultCondition
	    				}));
		    		});
		    	}
		    }],
		    bbar: ['提示：1. 可以拖动数据字典的字段到右边；2. 字段可以拖放来调整顺序；3. 行展开设置特殊选项', '->', {
				icon: basePath + 'resource/images/16/question.png',
		        cls: 'x-btn-icon',
		        text: '查看方案代码',
		        tooltip: '',
		        id: 'set-tab-info'
			}],
		    viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'grid-setting',
                    dropGroup: 'grid-setting'
                },
                listeners: {
                	drop: function(node, data, over) {
                		// 重新加载数据，防止出现checkcolumn的勾选错位情况
                		var newData = [];
                		if(over) {
                			over.store.each(function(record){
                    			newData.push(record.data);
                    		});
                    		over.store.loadData(newData);
                		}
                	}
                }
            },
			plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		        clicksToEdit: 1,
		        listeners: {
		        	beforeedit: function(editor, e, opts) {
			        	if(e.column.dataIndex == 'stg_value') {
			        		var isDate = ['datecolumn','datetimecolumn'].indexOf(e.record.get('type')) > -1,
			        			isRadio = e.record.get('stg_mode') == 'radiogroup',
			        			isMulti = ['checkboxgroup','combobox'].indexOf(e.record.get('stg_mode')) > -1,
			        			items = e.record.get('modeItems');
			        		if(isDate) {
			        			e.column.setEditor(new Ext.form.field.ComboBox(Ext.Object.merge(config, {
			        				value: e.value
			        			})));
			        		} else if(isRadio) {
			        			e.column.setEditor(new Ext.form.field.ComboBox(Ext.Object.merge(me.newComboConfig(items), {
			        				value: e.value
			        			})));
			        		} else if(isMulti) {
			        			var _items = Ext.Array.merge([{display: '全选', value: '$ALL'}], items);
			        			e.column.setEditor(new Ext.form.field.ComboBox(Ext.Object.merge(me.newComboConfig(_items), {
			        				value: e.value
			        			})));
			        		} else {
			        			e.column.setEditor(new Ext.form.field.Text({
									value: e.value
								}));
			        		}
			        	} else if(e.column.dataIndex == 'stg_format') {
			        		formatStore.clearFilter(true);
			        		formatStore.filter('type', e.record.get('type'));
			        	}
		        	}
		        }
		    }), {
				ptype: 'rowexpander',
				pluginId: 'rowexpander',
				expandOnDblClick: false,
				rowBodyTpl : [
				            '<tpl if="stg_formula">' +
					            '<div class="row">' +
									'<label class="radio-inline text-info col-xs-2">' +
										'表达式:' +
									'</label>' +
									'<div class="col-xs-8">{stg_formula:this.formatFormula}</div>' +
									'<div class="col-xs-1">' +
										'<button id="{[this.linkEvent(\'onFormulaEdit\')]}" class="x-btn" data-bind="{stg_field}">编辑</button>' +
									'</div>' +
								'</div><br>' +
								   '<div class="mode-type row" data-bind="{stg_field}">' +
						              '<label class="radio-inline text-info col-xs-2">' +
						              	'运算符:' +
						              '</label>' +
						              '<div class="col-xs-10">' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="" ' +
							              		'<tpl if="!stg_operator || stg_operator == &quot;&quot;"> checked="checked"</tpl>' +
							              	' /> 无' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="&gt;" ' +
							              		'<tpl if="stg_operator == &quot;&gt;&quot;"> checked="checked"</tpl>' +
							              	' /> 大于' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="≥" ' +
							              		'<tpl if="stg_operator == &quot;≥&quot;"> checked="checked"</tpl>' +
							              	' /> 大于等于' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="&lt;" ' +
							              		'<tpl if="stg_operator == &quot;&lt;&quot;"> checked="checked"</tpl>' +
							              	' /> 小于' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="≤" ' +
							              		'<tpl if="stg_operator == &quot;≤&quot;"> checked="checked"</tpl>' +
							              	' /> 小于等于' +
							              '</label>' +
						              '</div>' +
					              '</div>' +							
					              '<div class="mode-type row" data-bind="{stg_field}">' +
						              '<label class="radio-inline text-info col-xs-2">' +
						              	'查询格式:' +
						              '</label>' +
						              '<div class="col-xs-10">' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="" ' +
							              		'<tpl if="!stg_mode || stg_mode == &quot;&quot;"> checked="checked"</tpl>' +
							              	' /> 无' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="checkboxgroup" ' +
							              		'<tpl if="stg_mode == &quot;checkboxgroup&quot;"> checked="checked"</tpl>' +
							              	' /> 勾选框' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="radiogroup" ' +
							              		'<tpl if="stg_mode == &quot;radiogroup&quot;"> checked="checked"</tpl>' +
							              	' /> 单选框' +
							              '</label>' +
							              '<label class="radio-inline">' +
							              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="combobox" ' +
							              		'<tpl if="stg_mode == &quot;combobox&quot;"> checked="checked"</tpl>' +
							              	' /> 下拉框' +
							              '</label>' +
						              '</div>' +
					              '</div>' +
				            '</tpl>'+
							'<tpl if="stg_link">' +
								'<div class="links row">' +
									'<label class="radio-inline text-info col-xs-2">' +
										'链接:' +
									'</label>' +
									'<div class="col-xs-10">' +
								    	'<tpl for="links">' +
								    		'<label class="radio-inline">' +
								    			'<input type="radio" id="{[this.linkEvent(\'onLinkChange\')]}" name="{dl_fieldname}-link" data-bind="{dl_fieldname}" value="{[xindex]}" ' +
								    				'<tpl if="dl_link == parent.stg_link"> checked="checked"</tpl>' +
								    			' /> {dl_title}' +
								    		'</label>' +
								    	'</tpl>' +
									'</div>' +
								'</div>' +
							'</tpl>' +
							'<tpl if="stg_table">' +
				              '<div class="mode-type row" data-bind="{stg_field}">' +
					              '<label class="radio-inline text-info col-xs-2">' +
					              	'运算符:' +
					              '</label>' +
					              '<div class="col-xs-10">' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="" ' +
						              		'<tpl if="!stg_operator || stg_operator == &quot;&quot;"> checked="checked"</tpl>' +
						              	' /> 无' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="&gt;" ' +
						              		'<tpl if="stg_operator == &quot;&gt;&quot;"> checked="checked"</tpl>' +
						              	' /> 大于' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="≥" ' +
						              		'<tpl if="stg_operator == &quot;≥&quot;"> checked="checked"</tpl>' +
						              	' /> 大于等于' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="&lt;" ' +
						              		'<tpl if="stg_operator == &quot;&lt;&quot;"> checked="checked"</tpl>' +
						              	' /> 小于' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onOperatorChange\')]}" data-bind="{stg_field}" name="{stg_field}-operator" value="≤" ' +
						              		'<tpl if="stg_operator == &quot;≤&quot;"> checked="checked"</tpl>' +
						              	' /> 小于等于' +
						              '</label>' +
					              '</div>' +
				              '</div>' +							
				              '<div class="mode-type row" data-bind="{stg_field}">' +
					              '<label class="radio-inline text-info col-xs-2">' +
					              	'查询格式:' +
					              '</label>' +
					              '<div class="col-xs-10">' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="" ' +
						              		'<tpl if="!stg_mode || stg_mode == &quot;&quot;"> checked="checked"</tpl>' +
						              	' /> 无' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="checkboxgroup" ' +
						              		'<tpl if="stg_mode == &quot;checkboxgroup&quot;"> checked="checked"</tpl>' +
						              	' /> 勾选框' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="radiogroup" ' +
						              		'<tpl if="stg_mode == &quot;radiogroup&quot;"> checked="checked"</tpl>' +
						              	' /> 单选框' +
						              '</label>' +
						              '<label class="radio-inline">' +
						              	'<input type="radio" id="{[this.linkEvent(\'onModeChange\')]}" name="{stg_field}" value="combobox" ' +
						              		'<tpl if="stg_mode == &quot;combobox&quot;"> checked="checked"</tpl>' +
						              	' /> 下拉框' +
						              '</label>' +
					              '</div>' +
				              '</div>' +
				              '</tpl>' +
				              '<tpl if="stg_mode">' +
					              '<div class="mode-items row" data-bind="{stg_field}">' +
					              	'<div class="col-xs-12">' + 
					              		'<a class="x-btn btn-default" id="{[this.linkEvent(\'onModeItemAdd\')]}"><i class="x-btn-icon-el icon-add"></i><span class="x-btn-text">添加属性</span></a>' +
					              		'<tpl for="modeItems">' +
					              			'<div class="mode-item">' +
							    				'<input type="text" id="{[this.linkEvent(\'onModeItemFocus\')]}" name="display" value="{display}" placeholder="显示属性" />' +
							    				'<input type="text" id="{[this.linkEvent(\'onModeItemFocus\')]}" name="value" value="{value}" placeholder="实际属性" />' +
							    				'<input type="button" id="{[this.linkEvent(\'onModeItemDel\')]}" data-index="{[xindex]}" value="&times;" title="删除"/>' +
						    				'</div>' +
						    			'</tpl>' +
						    		'</div>'+
					              '</div>' +
				              '</tpl>',
				              {
				            	formatFormula: function(formula) {
				            		// 数据库格式的表达式转化为界面显示的表达式
				            		var units = formula.replace(/>=/g,'≥').replace(/<=/g,'≤').replace(/<>/g,'≠').replace(/(\|\|)/g,'‖')._split(/[\+\-\*=><\/%,\(\)\s]/), text = '', scope = this;
				            		Ext.Array.each(units, function(unit){
				            			if(!isNumber(unit) && (unit.indexOf('.') > 0 || unit.indexOf('COL_') == 0))
				            				text += scope.getDesc(unit);
				            			else
				            				text += unit;
				            		});
				            		return text;
				            	},
				            	onFormulaEdit: function(elm) {
				            		var store = this.owner.view.store, grid = this.owner.grid;
				            		  Ext.EventManager.on(elm, {
				            			  click: function(event, el) {
				            				  var record = grid.store.findRecord('stg_field', el.getAttribute('data-bind'));
				            				  me.onFormulaClick(store, record.get('stg_formula').replace(/>=/g,'≥').replace(/<=/g,'≤').replace(/<>/g,'≠').replace(/(\|\|)/g,'‖'), function(formula, type){
				            					  record.set('stg_formula', formula);
				            					  record.set('stg_type', type);
				            				  });
				 			                  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 50
				            		  });
				            	},
				            	getDesc: function(unit) {
				            		var table = null, field = unit;
				            		if(unit.indexOf('.') > 0) {
				            			table = unit.substring(0, unit.indexOf('.'));
				            			field = unit.substr(unit.indexOf('.')+1);
				            		}
				            		var res = this.owner.view.store.queryBy(function(record){
				            			return record.get('stg_table') == table && record.get('stg_field') == field;
				            		}), item = res.first();
				            		if(item)
				            			return item.get('stg_text');
				            		return '';
				            	},
				            	  linkEvent: function(eventName) {
				            		  var result = Ext.id();
				            		  Ext.defer(this.addListener, 1, this, [result, eventName]);
				            		  return result;
				            	  },
				            	  addListener: function(id, eventName) {
				            		  var elm = Ext.get(id);
				            		  elm && this[eventName].call(this, elm);
				            	  },
				            	  onModeChange: function(elm) {
				            		  var grid = this.owner.grid;
				            		  Ext.EventManager.on(elm, {
				            			  change: function(event, el) {
				            				  var record = grid.store.findRecord('stg_field', el.name);
				            				  record.set('stg_mode', el.value);
				            				  if(el.value && el.value != '') {
				            					  var items = record.get('modeItems');
				            					  if(!items || items.length == 0) {
				            						  record.set('modeItems', [{display: null,value:null}]);
				            					  }
				            				  }
				            				  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 100
				            		  });
				            	  },
				            	  onOperatorChange: function(elm) {
				            		  var grid = this.owner.grid;
				            		  Ext.EventManager.on(elm, {
				            			  change: function(event, el) {
				            				  var record = grid.store.findRecord('stg_field', el.getAttribute('data-bind'));
				            				  record.set('stg_operator', el.value);
				            				  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 100
				            		  });
				            	  },				            	  
				            	  getViewItems: function(el) {
				            		  var p = el.parentNode.parentNode.parentNode;
		            				  if(p) {
			            				  var gps = Ext.query('.mode-item', p), items = [];
			            				  Ext.Array.each(gps, function(els){
			            					  var e = els.childNodes, d = e[0].value, v = e[1].value;
			            					  v = (!v || v == '') ? d : v;
			            					  v = (!d || d == '') ? null : v;
			            					  items.push({display: d, value: v});
			            				  });
			            				  return items;
		            				  }
		            				  return null;
				            	  },
				            	  onModeItemAdd: function(elm) {
				            		  var grid = this.owner.grid, me = this;
				            		  Ext.EventManager.on(elm, {
				            			  click: function(event, el) {
				            				  var p = el.parentNode.parentNode.parentNode;
				            				  if(p) {
				            					  var field = p.getAttribute('data-bind'),
					            				  	  record = grid.store.findRecord('stg_field', field),
					            				  	  items = me.getViewItems(el) || [];
					 			                  items.push({
					 			                	  display: null,
					 			                      value: null
					 			                  });
					 			                  record.set('modeItems', items);
				            				  }
				 			                  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 50
				            		  });
				            	  },
				            	  onModeItemDel: function(elm) {
				            		  var grid = this.owner.grid;
				            		  Ext.EventManager.on(elm, {
				            			  click: function(event, el) {
				            				  var p = el.parentNode.parentNode.parentNode;
				            				  if(p) {
				            					  var field = p.getAttribute('data-bind'),
					            				  	  record = grid.store.findRecord('stg_field', field),
					            				  	  items = record.get('modeItems'), idx = Number(el.getAttribute('data-index')) - 1;
				            					  items.splice(idx, 1);
				            					  Ext.get(el.parentNode).remove();
					 			                  record.set('modeItems', items);
				            				  }
				 			                  Ext.EventManager.stopEvent(event);
				            			  }
				            		  });
				            	  },
				            	  onModeItemFocus: function(elm) {
				            		  var me = this;
				            		  Ext.EventManager.on(elm, {
				            			  mousedown: function(event, el) {
				            				  el.focus();
				            				  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 100
				            		  });
				            		  Ext.EventManager.on(elm, {
				            			  blur: function(event, el) {
				            				  if(el.name == 'display') {
				            					  var nextEl = el.nextSibling;
				            					  if(Ext.isEmpty(el.value) || el.value == '') {
				            						  nextEl.value = null;
				            					  } else {
				            						  if(Ext.isEmpty(nextEl.value) || nextEl.value == '') {
				            							  nextEl.value = el.value;
				            						  }
				            					  }
				            				  }
				            			  },
				            			  change: function(event, el) {
				            				  var items = me.getViewItems(el);
				            				  if (items) {
					 			                  var field = el.parentNode.parentNode.parentNode.getAttribute('data-bind'),
				            				  	  		grid = me.owner.grid,
				            				  	  		record = grid.store.findRecord('stg_field', field);
				            					  record.set('modeItems', items);
				            				  }
				            			  },
				            			  buffer: 10
				            		  });
				            	  },
				            	  onLinkChange: function(elm) {
				            		  var grid = this.owner.grid;
				            		  Ext.EventManager.on(elm, {
				            			  change: function(event, el) {
				            				  var record = grid.store.findRecord('stg_field', el.getAttribute('data-bind')),
				            				  	  links = record.get('links'), dl = links[el.value - 1];
				            				  record.set('stg_link', dl.dl_link);
				            				  record.set('stg_tokentab1', dl.dl_tokentab1);
				            				  record.set('stg_tokencol1', dl.dl_tokencol1);
				            				  record.set('stg_tokentab2', dl.dl_tokentab1);
				            				  record.set('stg_tokencol2', dl.dl_tokencol2);
				            				  Ext.EventManager.stopEvent(event);
				            			  },
				            			  buffer: 100
				            		  });
				            	  }
				              }]
 		    }],
			store: new Ext.data.Store({
				model: erp.model.SearchTemplate
			}),
			columns: [{
				text: '列',
				flex: 1,
				columns: [{
					text: '名称',
					dataIndex: 'stg_text',
					width: 180,
					editor: {
						xtype: 'textfield'
					},
					sortable: false,
					renderer: function(val, meta, record){
						var a = record.get('stg_table'), b = record.get('stg_field'),
							c = record.get('stg_formula'), text = '';
						a && (text += '表：' + a);
						b && (text += ' 字段：' + b);
						c && (text += ' 公式：' + c);
						return '<span title="' + text + '">' + val + '</span>';
					}
				},{
					text: '是否<br>显示',
					xtype: 'checkcolumn',
					dataIndex: 'stg_use',
					align: 'center',
					headerCheckable: false,
					width: 45,
					sortable: false
				},{
					text: 'APP<br>显示',
					xtype: 'checkcolumn',
					dataIndex: 'stg_appuse',
					align: 'center',
					headerCheckable: false,
					width: 45,
					sortable: false
				},{
					text: '宽度',
					xtype: 'numbercolumn',
					dataIndex: 'stg_width',
					align: 'center',
					width: 50,
					format: '0,00',
					editor: {
						xtype: 'numberfield',
						hideTrigger: true
					},
					sortable: false
				},{
					text: '格式转换',
					dataIndex: 'stg_format',
					align: 'center',
					width: 90,
					editor: {
						xtype: 'combo',
						store: formatStore,
		                displayField: 'display',
		                valueField: 'value',
		        		queryMode: 'local'
					},
					sortable: false
				}],
				sortable: false
			},{
				text: '查询',
				columns: [{
					text: '用于<br>查询',
					xtype: 'checkcolumn',
					dataIndex: 'stg_query',
					align: 'center',
					width: 45,
					sortable: false
				},{
					text: 'APP<br>查询',
					xtype: 'checkcolumn',
					dataIndex: 'stg_appcondition',
					align: 'center',
					width: 60,
					sortable: false
				},{
					text: '多输<br>入框',
					dataIndex: 'stg_double',
					xtype: 'checkcolumn',
					align: 'center',
					width: 45,
					sortable: false
				},{
					text: '带放<br>大镜',
					dataIndex: 'stg_dbfind',
					xtype: 'checkcolumn',
					align: 'center',
					width: 45,
					sortable: false
				},{
					text: '默认值',
					dataIndex: 'stg_value',
					align: 'center',
					flex: 1,
					sortable: false,
					editor: {
						xtype: 'textfield'
					}
				}],
				sortable: false
			},{
				text: '锁<br>列',
				dataIndex: 'stg_lock',
				xtype: 'checkcolumn',
				width: 40,
				sortable: false
			},{
				text: '分<br>组',
				dataIndex: 'stg_group',
				xtype: 'checkcolumn',
				singleChecked: true,
				width: 40,
				sortable: false
			},{
				text: '合<br>计',
				dataIndex: 'stg_sum',
				xtype: 'checkcolumn',
				width: 40,
				sortable: false
			}]
		});
	},
	getLimitForm: function(combo) {
		return Ext.create('Ext.form.Panel', {
			title: '选择字段，建立约束关系',
			bodyStyle : 'background:#f1f2f5;padding:5px 5px 0',
		    items: [{
		        xtype:'fieldset',
		        title: '客户分配',
		        padding: '0 5 10 10',
		        name: '_L',
		        checkboxToggle: true,
		        collapsible: true,
		        collapsed: true,
		        defaultType: 'combobox',
		        defaults: {columnWidth: .5, margin: '3 10 0 0', labelWidth: 80, editable: false},
		        layout: 'column',
		        items :[Ext.Object.merge({
		            fieldLabel: '客户编号',
		            name: '_L_CU_1'
		        }, combo), {
		        	xtype: 'displayfield',
		        	value: '调取分配给你的客户的所有数据'
		        }, Ext.Object.merge({
		            fieldLabel: '业务员编号',
		            name: '_L_CU_2'
		        }, combo), {
		        	xtype: 'displayfield',
		        	value: '调取业务员是你的所有数据'
		        }],
		        getValue: function() {
		        	var a = this.down('combo[name=_L_CU_1]').getValue(),
		        		b = this.down('combo[name=_L_CU_2]').getValue();
		        	return a ? 'CU(' + a + (b ? (',' + b) : '') + ')' : null;
		        }
		    }, {
		        xtype:'fieldset',
		        title: '供应商分配',
		        padding: '0 5 10 10',
		        name: '_L',
		        checkboxToggle: true,
		        collapsible: true,
		        collapsed: true,
		        defaultType: 'combobox',
		        defaults: {columnWidth: .5, margin: '3 10 0 0', labelWidth: 80, editable: false},
		        layout: 'column',
		        items :[Ext.Object.merge({
		        	fieldLabel: '供应商编号',
		            name: '_L_VE_1'
		        }, combo), {
		        	xtype: 'displayfield',
		        	value: '调取分配给你的供应商的所有数据'
		        }, Ext.Object.merge({
		        	fieldLabel: '采购员编号',
		            name: '_L_VE_2'
		        }, combo), {
		        	xtype: 'displayfield',
		        	value: '调取采购员是你的所有数据'
		        }],
		        getValue: function() {
		        	var a = this.down('combo[name=_L_VE_1]').getValue(),
		        		b = this.down('combo[name=_L_VE_2]').getValue();
		        	return a && b ? 'VE(' + a + (b ? (',' + b) : '') + ')' : null;
		        }
		    }, {
		        xtype:'fieldset',
		        title: '部门',
		        padding: '0 5 10 10',
		        name: '_L',
		        checkboxToggle: true,
		        collapsible: true,
		        collapsed: true,
		        defaultType: 'combobox',
		        defaults: {columnWidth: .5, margin: '3 10 0 0', labelWidth: 80, editable: false},
		        layout: 'column',
		        items :[Ext.Object.merge({
		        	fieldLabel: '部门编号',
		            name: '_L_DP_1'
		        }, combo)],
		        getValue: function() {
		        	var a = this.down('combo[name=_L_DP_1]').getValue();
		        	return a ? 'DP(' + a + ')' : null;
		        }
		    }, {
		        xtype:'fieldset',
		        title: '个人',
		        padding: '0 5 10 10',
		        name: '_L',
		        checkboxToggle: true,
		        collapsible: true,
		        collapsed: true,
		        defaultType: 'combobox',
		        defaults: {columnWidth: .5, margin: '3 10 0 0', labelWidth: 80, editable: false},
		        layout: 'column',
		        items :[Ext.Object.merge({
		        	fieldLabel: '个人编号',
		            name: '_L_EM_1'
		        }, combo)],
		        getValue: function() {
		        	var a = this.down('combo[name=_L_EM_1]').getValue();
		        	return a ? 'EM(' + a + ')' : null;
		        }
		    }]
		});
	},
	/**
	 * 权限约束设置
	 */
	onLimitClick: function(source, oldLimits, callback) {
		var me = this, win = me.limitwin, datas = [{display: '(无)', value: null}];
		source.each(function(item){
			if(item.get('stg_type').indexOf('VARCHAR2') == 0) {
				datas.push({
					display: item.get('stg_text'),
					value: item.get('stg_table') + '.' + item.get('stg_field')
				});
			}
		});
		var combo = me.newComboConfig(datas);
		if (!win) {
			win = me.limitwin = Ext.create('Ext.window.Window', {
				title : '权限约束设置',
				closeAction : 'hide',
				width : 500,
				items : [me.getLimitForm(combo)],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt, 
							fs = w.down('form').down('fieldset[collapsed=false]'),
							value = fs ? fs.getValue() : null;
						callback && callback.call(me, value);
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		} else {
			var coms = win.down('form').query('combobox');
			Ext.Array.each(coms, function(com){
				com.getStore().loadData(datas);
			});
		}
		win.show();
		var form = win.down('form');
		if(oldLimits) {
			if(oldLimits.indexOf('CU') == 0) {// 客户分配
				var col1 = null, col2 = null;
				if(oldLimits.indexOf(",") > -1) {
					col1 = oldLimits.substring(3, oldLimits.indexOf(','));
					col2 = oldLimits.substring(oldLimits.indexOf(',') + 1, oldLimits.lastIndexOf(')'));
				} else {
					col1 = oldLimits.substring(3, oldLimits.indexOf(')'));
				}
				form.down('combobox[name=_L_CU_1]').setValue(col1);
				form.down('combobox[name=_L_CU_2]').setValue(col2);
				form.down('combobox[name=_L_CU_1]').ownerCt.setExpanded(true);
			} else if(oldLimits.indexOf('VE') == 0) {// 供应商分配
				var col1 = null, col2 = null;
				if(oldLimits.indexOf(",") > -1) {
					col1 = oldLimits.substring(3, oldLimits.indexOf(','));
					col2 = oldLimits.substring(oldLimits.indexOf(',') + 1, oldLimits.lastIndexOf(')'));
				} else {
					col1 = oldLimits.substring(3, oldLimits.indexOf(')'));
				}
				form.down('combobox[name=_L_VE_1]').setValue(col1);
				form.down('combobox[name=_L_VE_2]').setValue(col2);
				form.down('combobox[name=_L_VE_1]').ownerCt.setExpanded(true);
			} else if(oldLimits.indexOf('DP') == 0) {// 部门
				var col1 = oldLimits.substring(3, oldLimits.lastIndexOf(')'));
				form.down('combobox[name=_L_DP_1]').setValue(col1);
				form.down('combobox[name=_L_DP_1]').ownerCt.setExpanded(true);
			} else if(oldLimits.indexOf('EM') == 0) {// 个人
				var col1 = oldLimits.substring(3, oldLimits.lastIndexOf(')'));
				form.down('combobox[name=_L_EM_1]').setValue(col1);
				form.down('combobox[name=_L_EM_1]').ownerCt.setExpanded(true);
			}
		} else {
			var fs = form.down('fieldset[collapsed=false]');
			fs && fs.setExpanded(false);
		}
	},
	onDefaultConditionClick: function(oldCondition, callback) {
		var me = this, win = me.condwin;
		if (!win) {
			win = me.condwin = Ext.create('Ext.window.Window', {
				title : '默认筛选条件设置',
				closeAction : 'hide',
				width : 500,
				items : [{
					xtype: 'textarea',
					width: 488
				}],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt;
						callback && callback.call(me, w.down('textarea').getValue());
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.down('textarea').setValue(oldCondition);
		win.show();
	},
	getPreHookForm: function(combo){
		var me = this, form = Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;padding:5px',
			defaults: {
				margin: '5 0'
			},
			combo: combo,
			items: [{
				xtype: 'radiogroup',
        		fieldLabel: '类型',
        		columns: 2,
        		items: [{ 
					boxLabel: 'Java程序', 
        			name: 'pre_hook_type', 
        			inputValue: 'java' 
        		}, { 
					boxLabel: '存储过程', 
        			name: 'pre_hook_type', 
        			inputValue: 'procedure',
        			checked: true 
        		}],
        		listeners: {
        			change: function(r, v, o) {
        				var type = v['pre_hook_type'], oldType = o['pre_hook_type'],
        					oldCt = r.ownerCt.down('container[pre_hook_type=' + oldType + ']'),
        					ct = r.ownerCt.down('container[pre_hook_type=' + type + ']');
        				oldCt.hide();
        				oldCt.items.each(function(item){
        					item.setFieldDefaults({allowBlank: true});
        					item.allowBlank = true;
        				});
        				ct.show();
        				ct.items.each(function(item){
        					item.setFieldDefaults({allowBlank: false});
        					item.allowBlank = false;
        				});
        			}
        		}
			}, {
				xtype: 'container',
				pre_hook_type: 'java',
				hidden: true,
				defaults: {
					margin: '5 0'
				},
				items: [{
					xtype: 'textfield',
					fieldLabel: 'Bean名',
					emptyText: '例如：exampleService',
					name: 'bean_name' 
				},{
					xtype: 'textfield',
					fieldLabel: '方法名',
					emptyText: '例如：exampleMethod',
					name: 'method_name'
				}]
			}, {
				xtype: 'container',
				pre_hook_type: 'procedure',
				items: [{
					xtype: 'textfield',
					fieldLabel: '过程名',
					emptyText: '例如：exampleProcedure',
					name: 'procedure_name',
					allowBlank: false
				}]
			}, {
				xtype: 'fieldcontainer',
				defaultType: 'fieldcontainer',
				fieldLabel: '参数',
				name: 'paramsCt',
				items: [{
					xtype: 'button',
					text: '添加参数',
					margin: '0 5 0 0',
					handler: function() {
						this.ownerCt.addParam(true);
					}
				},{
					xtype: 'button',
					text: '添加常量',
					handler: function() {
						this.ownerCt.addParam();
					}
				}]
			}]
		});
		var paramsCt = form.down('fieldcontainer[name=paramsCt]');
		paramsCt.addParam = function(isParam, value) {
			var paramField;
			if(isParam) {
				paramField = Ext.apply({
				    xtype: 'combobox',
				    name: 'params',
				    editable: false,
				    column: .75,
				    allowBlank: false,
				    value: value
				}, form.combo);
			} else {
				paramField = {
			        xtype: 'textfield',
			        name: 'params',
			        emptyText: '不能带有, : ( ) .',
			        column: .75,
			        allowBlank: false,
			        value: value
			    };
			}
			this.add({
			    width: 300,
			    layout: 'column',
			    defaults: {
			        margin: '5 5 0 0'
			    },
			    items: [ paramField, {
			        xtype: 'button',
			        text: '删除',
			        column: .25,
			        handler: function() {
			            var ct = this.ownerCt;
			            ct.ownerCt.remove(ct);
			        }
			    }]
			});
		};
		paramsCt.removeAllParams = function() {
			this.query('fieldcontainer').forEach(function(item){
				paramsCt.remove(item);
			});
		};
		form.getData = function() {
			var vals = form.getValues(), data = vals.pre_hook_type;
			if('java' == data) {
				data += ':' + vals.bean_name + '.' + vals.method_name;
			} else {
				data += ':' + vals.procedure_name;
			}
			return data + '(' + (Ext.isArray(vals.params) ? vals.params.join() : (vals.params || '')) + ')';
		};
		form.setData = function(data) {
			paramsCt.removeAllParams();
			form.getForm().reset();
			var cfg = me.parseHook(data);
			form.getForm().setValues(cfg);
			Ext.Object.getKeys(cfg.params).forEach(function(param){
				paramsCt.addParam(cfg.params[param], param);
			});
		};
		return form;
	},
	parseHook: function(hook) {
		var cfg = {params: {}};
		if (hook) {
			var matcher = hook.match(/(.+):(.+)\((.*)\)/);
			if (matcher) {
				var type = matcher[1], execName = matcher[2], args = matcher[3], names = execName.split('.'),
					params = (args ? args.split(',') : []);
				cfg.pre_hook_type = type;
				if ('java' == type) {
					cfg.bean_name = names[0];
					cfg.method_name = names[1];				
				} else {
					cfg.procedure_name = execName;			
				}
				params.forEach(function(param){
					cfg.params[param] = (':' == param.charAt(0));
				});
			}
		}
		return cfg;
	},
	onPreHookClick: function(source, oldHook, callback) {
		var me = this, win = me.hookwin, datas = [];
		source.each(function(item){
			if (item.get('stg_query')) {
				datas.push({
					display: item.get('stg_text'),
					value: ':' + item.get('stg_table') + '.' + item.get('stg_field')
				});
			}
		});
		var combo = me.newComboConfig(datas);
		if (!win) {
			win = me.hookwin = Ext.create('Ext.window.Window', {
				title : '筛选前执行动作设置',
				closeAction : 'hide',
				width : 500,
				items : [me.getPreHookForm(combo)],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt, f = w.down('form');
						if (f.isValid()) {
							callback && callback.call(me, f.getData());
							w.hide();							
						}
					}
				}, {
					text : $I18N.common.button.erpOffButton,
					height : 26,
					handler : function(b) {
						var w = b.ownerCt.ownerCt, f = w.down('form');
						callback && callback.call(me, null);
						w.hide();
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		var form = win.down('form');
		form.combo = combo;
		form.setData(oldHook);
		win.show();
	},
	/**
	 * 公式设置用到的函数
	 */
	formula: function() {
		var me = this;
		me.formula_operator = [];
		return {
			log: function(oper, text, data, isfn) {
				me.formula_operator.push({oper: oper, text: text, data: data, isfn: isfn});
			},
			getContainer: function(scope) {
				return scope.up('form').down('fieldcontainer[cls~=x-screen]');
			},
			add: function(scope, parentScope) {
				var f = this.getContainer(parentScope || scope);
				f.add({text: scope.text, data: scope.data, isfn: scope.isfn});
				if(scope.isfn) {
					f.add({text: '('});
					this.log(1, scope.text, null, true);
					this.log(2, '(');
				} else {
					this.log(1, scope.text, scope.data);
				}
			},
			del: function(scope) {
				var f = this.getContainer(scope), l = f.down('button:last');
				if (l) {
					f.remove(l);
					this.log(0, l.text, l.data, l.isfn);
				}
			},
			back: function(scope) {
				var f = this.getContainer(scope), len = me.formula_operator.length;
				if(len > 0) {
					var i = len - 1, o = me.formula_operator[i], oper = o.oper;
					switch(oper) {
					case 0:
						f.add({text: o.text, data: o.data, isfn: o.isfn});
						break;
					case 1:
						var b = f.down('button:last');
						if(b && b.text == o.text)
							f.remove(b);
						break;
					case 2:
						var b = f.down('button:last');
						if(b && b.text == o.text) {
							f.remove(b);
							f.remove(f.down('button:last'));
						}
						break;
					case 3:
						var j = 0;
						for(;i > 0;i-- ) {
							if(me.formula_operator[i].oper == 3) {
								j = i;
							} else {
								break;
							}
						}
						for(;j < len;j++ ) {
							o = me.formula_operator[j];
							f.add({text: o.text, data: o.data, isfn: o.isfn});
						}
						i++;
						break;
					}
					me.formula_operator.splice(i);
				}
			},
			clear: function(scope) {
				var m = this, f = m.getContainer(scope), btns = f.query('button');
				f.removeAll();
				Ext.Array.each(btns, function(b){
					m.log(3, b.text, b.data, b.isfn);
				});
			},
			reset: function(scope, source, oldData) {
				var f = this.getContainer(scope);
				if(me.formula_operator.length > 0) {
					f.removeAll();
					f.add(f.initItems);
				}
				me.formula_operator = [];
			}
		};
	},
	getFormulaForm: function(source, oldData) {
		var defItems = [], defBtns = "789/%456*(123-)0.+=><≥≤≠".split(""),
			colItems = [], moreItems = [], me = this, formula = me.formula();
		defItems.push({
			text: '←', 
			tooltip: '删除', 
			handler: function(btn) {
				formula.del(btn);
			}
		});
		defItems.push({
			text: '→', 
			tooltip: '回退', 
			handler: function(btn) {
				formula.back(btn);
			}
		});
		defItems.push({text: ','});
		defItems.push({
			text: 'RE', 
			tooltip: '重置', 
			handler: function(btn) {
				formula.reset(btn);
			}
		});
		defItems.push({
			text: 'CE', 
			tooltip: '清除', 
			handler: function(btn) {
				formula.clear(btn);
			}
		});
		Ext.Array.each(defBtns, function(b){
			var o = {text: b};
			if(b == '0')
				o.width = 108;
			defItems.push(o);
		});
		defItems.push({text: 'nvl', isfn: true, tooltip: 'nvl(x,y)，如果x不为空，返回x，否则返回y'});
		defItems.push({text: 'round', isfn: true, tooltip: 'round(x,y)，返回四舍五入到小数点右边y位的x值'});
		defItems.push({text: 'floor', isfn: true, tooltip: 'floor(x)，返回小于或等于x的最大整数'});
		defItems.push({text: 'ceil', isfn: true, tooltip: 'ceil(x)，返回大于或等于x的最小整数'});
		defItems.push({text: 'abs', isfn: true, tooltip: 'abs(x)，返回x的绝对值'});
		defItems.push({text: 'nvl2', isfn: true, tooltip: 'nvl2(x,y,z)，如果x不为空，返回y，否则返回z'});
		defItems.push({text: 'trim', isfn: true, tooltip: 'trim(x)，去除x前后空格'});
		defItems.push({text: 'lpad', isfn: true, tooltip: 'lpad(x,y,z)，如果x的长度小于y，左边填充z'});
		defItems.push({text: 'rpad', isfn: true, tooltip: 'rpad(x,y,z)，如果x的长度小于y，右边填充z'});
		defItems.push({text: '‖', tooltip: '字符串连接符'});
		defItems.push({text: 'sysdate', tooltip: 'sysdate，当前时间'});
		defItems.push({text: 'to_char', isfn: true, tooltip: 'to_char(x,y)，日期x按格式y转化成字符串'});
		defItems.push({text: 'add_months', isfn: true, tooltip: 'add_months(x,y)，日期x加减y个月份', width: 108});
		defItems.push({text: 'trunc', isfn: true, tooltip: 'trunc(x,y)，日期截断。清除时分秒：trunc(sysdate)；年初：trunc(sysdate,\'y\')；月初：trunc(sysdate,\'mm\')'});
		// case when
		Ext.Array.each('case,when,then,else,end'.split(','), function(b){
			var o = {text: b, tooltip: '判断语句case when..then..when..then..else..end'};
			defItems.push(o);
		});
		defItems.push({
			width: 279,
			text: '添加自定义内容', 
			handler: function(btn) {
				me.onUserDefinedClick(function(text){
					formula.add({text: "'" + text + "'"}, btn);// 当字符串处理
				});
			}
		});
		source.each(function(item){
			if(/(NUMBER|FLOAT|INT|DATE|TIMESTAMP)(\(\d+\)){0,1}/.test(item.get('stg_type')) && item.get('stg_table')) {
				colItems.push({
					text: item.get('stg_text'),
					data: item.data
				});
			}else moreItems.push({
				   text: item.get('stg_text'),
				   data: item.data,
				   handler:function(btn){
					   formula.add(btn);
				   }
			});
		});
		colItems.push({
			text:'获取更多字段',
			columnWidth:1,
			handler:function(btn){
				var f=btn.up('form');
				btn.hide();
				f.items.items[1].items.items[1].add(f.moreItems);	
				formula.add(f.moreItems);
			}
		});
		var form = Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;padding:5px',
			layout: 'vbox',
			minHeight : 510,
			moreItems:moreItems,
			items: [{
				xtype: 'fieldcontainer',
				margin: '0 3 8 3',
				width: '100%',
				minHeight: 50,
				cls: 'x-form-text x-screen',
				defaultType: 'button',
				defaults: {
					margin: '0 0 3 0',
					cls: 'x-btn-clear'
				}
			},{
				xtype: 'container',
				layout: 'column',
				width: '100%',
				defaultType: 'fieldcontainer',
				defaults: {flex: 1},
				autoScroll:true,
				maxHeight : 500,
				items: [{
					defaultType: 'button',
					defaults: {
						width: 51,
						height: 30,
						margin: '3 3 3 3'
					},
					items: defItems,
					columnWidth:0.5
				},{
					layout: 'column',
					defaultType: 'button',
					columnWidth:0.5,
					defaults: {
						columnWidth: .5,
						height: 30,
						margin: '3 3 3 3'
					},
					items: colItems
				}]
			}]
		});
		var btns = form.query('button');
		Ext.Array.each(btns, function(btn){
			if(!btn.handler) {
				btn.handler = function() {
					formula.add(btn);
				};
			}
		});
		if(oldData) {
			var container = form.down('fieldcontainer[cls~=x-screen]'), 
				items = me.getItemsFromFormula(source, oldData);
			container.initItems = items;
			container.add(items);
		}
		return form;
	},
	/**
	 * 解析表达式
	 */
	getItemsFromFormula: function(source, oldData) {
		var sign = /[\+\-\*=\/%,\(\)\s]/, units = oldData._split(sign), items = [],
			fns = ['abs', 'ceil', 'floor', 'round', 'nvl', 'nvl2', 'lpad', 'rpad', 'trim', 'trunc', 'to_char', 'add_months'], 
			cw = ['case', 'when', 'then', 'else', 'end', 'sysdate', '||'];
		Ext.Array.each(units, function(unit){
			if(isNumber(unit)) {
				Ext.Array.each(unit.split(""), function(u){
					items.push({text: u});
				});
			} else if(fns.indexOf(unit) > -1){
				items.push({
					text: unit,
					isfn: true
				});
			} else if(sign.test(unit) || cw.indexOf(unit) > -1) {
				if(unit != ' ')
					items.push({text: unit});
			} else {
				var table = null, field = unit, type = null;
				if(unit.indexOf('.') > 0) {
	    			table = unit.substring(0, unit.indexOf('.'));
	    			field = unit.substr(unit.indexOf('.')+1);
	    		}
				var res = source.queryBy(function(record){
	    			return record.get('stg_table') == table && record.get('stg_field') == field;
	    		}), item = res.first();
	    		if (item) {
	    			unit = item.get('stg_text');
	    			type = item.get('stg_type');
	    		}
				items.push({
					text: unit,
					data: {stg_table: table, stg_field: field, stg_type: type}
				});
			}
		});
		return items;
	},
	onUserDefinedClick: function(callback) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '自定义内容',
			closeAction: 'destroy',
			width : 300,
			items : [{
				xtype : 'textfield',
				emptyText : '输入除单引号外任意字符',
				width: '100%'
			}],
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt, f = w.down('textfield'), v = f.getValue();
					v && (callback.call(me, v));
					w.close();
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
		win.show();
	},
	/**
	 * 自定义公式
	 */
	onFormulaClick: function(source, oldData, callback) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '自定义公式',
			closeAction: 'destroy',
			width : 640,
			items : [me.getFormulaForm(source, oldData)],
			buttonAlign : 'center',
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt,
						items = w.query('fieldcontainer[cls~=x-screen] > button'),
						text = '', test = '', c, h;
					Ext.Array.each(items, function(item){
						if(item.data) {
							text += item.data.stg_table + '.' + item.data.stg_field;
							var type = me.getTypeByStg(item.data.stg_type);
							// 生成随机测试数据
							if("datecolumn" == type) {
								test += 'randomDate()';
							} else if("numbercolumn" == type) {
								test += 'randomNum()';
							} else {
								test += 'randomStr()';
							}
						} else {
							if(item.text == 'sysdate'){
								text += 'sysdate';
								test += 'new Date()';
							} else if(item.text == 'case'){
								c = true;
								text += item.text + ' ';
							} else if(item.text == 'when') {
								h = true;
								if(c) {
									c = false;
									test += '(function(){if(';
								} else
									test += '}else if(';
								text += ' ' + item.text + ' ';
							} else if(item.text == 'then') {
								h = false;
								test += '){return ';
								text += ' ' + item.text + ' ';
							} else if(item.text == 'else') {
								test += '}else{return ';
								text += ' ' + item.text + ' ';
							} else if(item.text == '>') {
								test += '>';
								text += ' > ';
							} else if(item.text == '<') {
								test += '<';
								text += ' < ';
							} else if(item.text == '≥') {
								test += '>=';
								text += ' >= ';
							} else if(item.text == '≤') {
								test += '<=';
								text += ' <= ';
							} else if(item.text == '≠') {
								test += '!=';
								text += ' <> ';
							} else if(item.text == '‖') {
								test += '+';
								text += ' || ';
							} else if(item.text == 'end') {
								test += '}})()';
								text += ' ' + item.text + ' ';
							} else {
								if(h && item.text == '=') {
									test += '==';
								} else
									test += item.text;
								text += item.text;
							}
						}
					});
					try {
						// 公式结果类型判断
						var res = Ext.eval(test), t = Ext.isDate(res) ? 'DATE' : (Ext.isNumber(res) ? 'NUMBER' : 'VARCHAR2(100)');
						callback && callback.call(me, text, t);
						w.close();
					} catch(e) {
						Ext.example.msg('error', '错误', '您的公式有误，请检查并修改正确' +
								'<p><strong>' +e + '</strong></p>', 5000);
					}
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
		win.show();
	},
	/**
	 * 生成form的字段<br>
	 * 勾选、单选框
	 */
	createFormItems: function(source, oldData) {
		var	fields = [];
		if(source) {
			if(Ext.isArray(source)) {
				Ext.Array.each(source, function(i){
					if(i.stg_use == 1) {
						var obj = {boxLabel : i.stg_text, inputValue : i.stg_field, table: i.stg_table, alias: i.stg_alias};
						if(oldData && Ext.Array.indexOf(oldData, i.stg_field) > -1)
							obj.checked = true;
						fields.push(obj);
					}
				});
			} else {
				source.each(function(i){
					if(i.get('stg_use') == 1) {
						var obj = {boxLabel : i.get('stg_text'), inputValue : i.get('stg_field'), table: i.get('stg_table'), alias: i.get('stg_alias')};
						if(oldData && Ext.Array.indexOf(oldData, i.get('stg_field')) > -1)
							obj.checked = true;
						fields.push(obj);
					}
				});
			}
		} else {
			var grid = this.getGrid().getView().normalGrid, columns = grid.headerCt.getGridColumns();
			Ext.each(columns, function(){
				if(!this.hidden && this.getWidth() > 0 && this.dataIndex) {
					var obj = {boxLabel : this.text, inputValue : this.dataField, table: this.dataTable, alias: this.dataIndex};
					if(oldData && Ext.Array.indexOf(oldData, this.dataIndex) > -1)
						obj.checked = true;
					fields.push(obj);
				}
			});
		}
		return fields;
	},
	/**
	 * 生成form的字段<br>
	 * 勾选、单选框<br>使用别名
	 */
	createAliaItems: function(oldData) {
		var	fields = [];
		var grid = this.getGrid(), columns = grid.headerCt.getGridColumns();
		Ext.each(columns, function(){
			if(!this.hidden && this.getWidth() > 0 && this.dataIndex) {
				var obj = {boxLabel : this.text, inputValue : this.dataIndex};
				if(oldData && Ext.Array.indexOf(oldData, this.dataIndex) > -1)
					obj.checked = true;
				fields.push(obj);
			}
		});
		return fields;
	},
	/**
	 * 
	 */
	getAliaForm : function(type, itemId, oldData) {
		var me = this;
		return Ext.create('Ext.form.Panel', {
			itemId : itemId,
			autoScroll : true,
			layout : 'column',
			bodyStyle : 'background:#f1f2f5;',
			defaults : {
				xtype : type || 'radio',
				name : 'gridfield',
				columnWidth : .33,
				margin : '3 3 3 10'
			},
			items : me.createAliaItems(oldData)
		});
	},
	getGridForm : function(type, itemId, source, oldData) {
		var me = this;
		return Ext.create('Ext.form.Panel', {
			itemId : itemId,
			autoScroll : true,
			layout : 'column',
			maxHeight: 500,
			bodyStyle : 'background:#f1f2f5;',
			defaults : {
				xtype : type || 'radio',
				name : 'gridfield',
				columnWidth : .33,
				margin : '3 3 3 10'
			},
			items : me.createFormItems(source, oldData)
		});
	},
	toogleGroup : function(f, b) {
		if (!f) return;
		var dx = null;
		if (typeof f === 'string') {
			dx = f;
		} else {
			var r = f.down('radio[value=true]');
			if (r) {
				dx = r.inputValue;
			}
		}
		if (dx) {
			var grid = this.getGrid(), c = grid.down('gridcolumn[dataIndex=' + dx + ']');
			if(b) {
				if(grid.store.groupField) {
	            	var m = grid.down('gridcolumn[dataIndex=' + grid.store.groupField + ']');
	            	if(m) {
	            		m.summaryType = m.lastSummaryType;
	    				m.summaryRenderer = m.lastSummaryRenderer;
	            	}
	            }
				if(typeof c.lastSummaryType === 'undefined') {
					c.lastSummaryType = c.summaryType;
				}
				if(typeof c.lastSummaryRenderer === 'undefined') {
					c.lastSummaryRenderer = c.summaryRenderer;
				}
				c.summaryType = 'count';
				c.summaryRenderer = function(v) {
	                return '共（' + v + '）条';
	            };
	            // 先筛选数据，再选择分组字段时，不出现分组合计行的问题
	            if (grid.store.getTotalCount() > 0) {
	            	var grouper = grid.view.normalView.getFeature('group');
	            	grouper.enable();
	            	grouper = grid.view.lockedView.getFeature('group');
	            	grouper.enable();
	            }
	            grid.store.groupField = dx;
	            grid.store.group(dx, 'ASC');
			} else {
				c.summaryType = c.lastSummaryType;
				c.summaryRenderer = c.lastSummaryRenderer;
				var view = grid.lockedGrid.getView(), fe = view.getFeature('group');
				if(fe) {
					fe.disable();
	                view.refresh();
				}
                view = grid.normalGrid.getView(), fe = view.getFeature('group');
                if(fe) {
					fe.disable();
	                view.refresh();
				}
                var r = f.down('radio[value=true]');
                if (r) {
                	r.setValue(false);
                }
                grid.store.groupField = null;
			}
		}
	},
	/**
	 * 锁定列
	 */
	onLock : function(form) {
		var r = form.query('checkbox[value=true]'), 
			checked = Ext.Array.pluck(r, 'inputValue');
		var grid = this.getGrid(), locked = grid.getView().lockedGrid,
			normal = grid.getView().normalGrid;
		var lockedCols = locked.headerCt.getGridColumns();
		Ext.Array.each(lockedCols, function(column){
			if(!Ext.Array.contains(checked, column.dataIndex)) {
				grid.unlock(column);
			}
		});
		Ext.Array.each(r, function(){
			var column = normal.down('gridcolumn[dataIndex=' + this.inputValue + ']');
			if(column && !column.locked) {
				grid.lock(column);
	        }
		});
	},
	/**
	 * 取出排序设置
	 */
	getSortProperties : function(form, store) {
		var r = form.query('checkbox[value=true]'), prop = [];
		Ext.Array.each(r, function(c, i){
			var obj = {
				property: c.inputValue,
				direction: 'ASC',
				description: c.boxLabel,
				alias: c.alias,
				number: i + 1,
				table: c.table
			};
			if (store) {
				var item = store.findRecord('property', c.inputValue);
				if (item) {
					obj.direction = item.get('direction');
				}
			}
			prop.push(obj);
		});
		return prop;
	},
	/**
	 * 后台排序
	 */
	onSort : function(sorts) {
//		var grid = this.getGrid().getView().normalGrid;
//		grid.store.sort(this.getSortProperties(form));
	},
	/**
	 * 清除排序、分组等
	 */
	onClear : function() {
		var grid = this.getGrid().getView().normalGrid;
		grid.store.clearGrouping();
		grid.store.clearFilter();
	},
	onTempLoad : function(fn) {
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/g.action',
			params: {
				caller: caller
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success && r.data) {
					fn.call(null, r.data, r.lastId);
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '错误', r.exceptionInfo, 5000);
				}
			}
		});
	},
	duplicateTemp:function(data){
		var me = this;
		var w=Ext.getCmp('duplicateTemp');
		if(w)w.data=data;
		else{
			w = Ext.create('Ext.window.Window', {
			width: 300,
			height: 97,
			data:data,
			title: '请输入导航栏CALLER',
			id:'duplicateTemp',
			layout: 'anchor',
			items: [{
				xtype: 'textfield',
				allowBlank: false,
				anchor: '100% 100%',
				value:'New'+caller
			}],
			buttonAlign: 'center',
			closeAction:'hide',
			buttons: [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt, f = w.down('textfield');
					if(!Ext.isEmpty(f.getValue())) {
						me.checkCaller(f.getValue(),w.data);
					}
					w.close();
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					b.ownerCt.ownerCt.hide();
				}
			}]
		});}
		w.show();
	},
	checkCaller:function(newCaller,data){
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/checkCaller.action',
			params: {
				caller: newCaller,
				title: newCaller+data.st_title
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.result) {
    				Ext.MessageBox.confirm('提示', r.result, function(but) {  
                    if(but=='yes')me.onDuplicateTemp(newCaller,data);                       
                 });  
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '提示', r.exceptionInfo, 5000);
				}else me.onDuplicateTemp(newCaller,data);
			}
		});
	},
	onDuplicateTemp:function(newCaller,data){
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/duplTemp.action',
			params: {
				caller: newCaller,
				sId: data.st_id,
				title:newCaller+data.st_title
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					Ext.example.msg('info', '提示', '复制方案到新导航栏成功', 2000);
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '提示', r.exceptionInfo, 5000);
				}
			}
		});
	},
	addTemp : function(title, fn) {
		var me = this;
		var t = title || (me.BaseUtil.getActiveTab().title + '(' + em_name + ')' + Ext.Date.format(new Date(),'Y-m-d'));
		var w = Ext.create('Ext.window.Window', {
			width: 300,
			height: 97,
			title: '为方案命名',
			layout: 'anchor',
			items: [{
				xtype: 'textfield',
				allowBlank: false,
				anchor: '100% 100%',
				value: t
			}],
			buttonAlign: 'center',
			buttons: [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt, f = w.down('textfield');
					if(!Ext.isEmpty(f.getValue())) {
						if(fn) {
							fn.call(me, f.getValue());
						} else {
							var g = me.querywin.down('grid');
							var r = g.store.add({st_id: 0, st_date: new Date().getTime(), st_man: em_name, st_title: f.getValue(), st_caller: caller});
							if(r.length > 0) {
								g.selModel.select(r[0]);
								me.onTempSet(r[0], [], 0);
							}
						}
					}
					w.close();
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					b.ownerCt.ownerCt.hide();
				}
			}]
		});
		w.show();
	},
	updateTemp : function(callback) {
		var me = this, win = this.tempwin, g = win.down('#grid-setting'), 
			datas = new Array(), d, s = win.relativeId, i = 1,
			c = g.defaultCondition, sorts = g.sorts, limits = g.limits, preHook = g.preHook;
		g.store.each(function(r){
			d = r.data;
			if(d.stg_use || d.stg_query || d.stg_group || d.stg_lock) {
				d.stg_use = d.stg_use ? 1 : 0;
				d.stg_query = d.stg_query ? 1 : 0;
				d.stg_group = d.stg_group ? 1 : 0;
				d.stg_lock = d.stg_lock ? 1 : 0;
				d.stg_sum = d.stg_sum ? 1 : 0;
				d.stg_double = d.stg_double ? 1 : 0;
				d.stg_dbfind = d.stg_dbfind ? 1 : 0;
				d.stg_appuse=d.stg_appuse?1:0;
				d.stg_appcondition=d.stg_appcondition?1:0;
				d.stg_stid = s;
				d.stg_detno = i++;
				delete d.text;
				delete d.type;
				delete d.links;
				delete d.stg_alias;
				if (d.modeItems) {
					var validItems = [];
					Ext.Array.each(d.modeItems, function(item, i){
						if(item.display && item.display != '') {
							item.num = validItems.length + 1;
							item.st_id = s;
							item.stg_field = d.stg_field;
							item.value = (item.value == null || item.value == '') ?
									item.display : item.value;
							validItems.push(item);
						}
					});
					d.modeItems = validItems;
				}
				datas.push(d);
			}
		});
		var e = me.getRepeats(datas);
		if(e) {
			Ext.example.msg('error', '有重复选择的字段', e, 5000);
			return;
		}
		if(s > 0) {
			me.onTempUpdate(datas, c, sorts, limits, preHook, s, callback);
		} else {
			me.onTempSave(win.relativeRecord.get('st_title'), datas, c, sorts, limits, preHook, callback);
		}
	},
	/**
	  * 判断是否有重复拖放过来的字段或重复的公式 
	  */
	getRepeats: function(datas) {
		var p = {}, k = null, e = '';
		Ext.Array.each(datas, function(d, i){
			k = d.stg_table + '.' + d.stg_field;
			if(d.stg_formula)
				k = d.stg_formula;
			if(p[k])
				e += '行' + (i + 1) + '的' + d.stg_text;
			else
				p[k] = true;
		});
		if(e.length > 0)
			return e;
		return null;
	},
	copyTemp: function(title, sourceId) {
		if(sourceId) {
			this.onTempCopy(title, sourceId);
		} else {
			var me = this, win = this.tempwin, g = win.down('#grid-setting'), 
				datas = new Array(), d, i = 1,
				c = g.defaultCondition, sorts = g.sorts, limits = g.limits, preHook = g.preHook;
			g.store.each(function(r){
				d = r.data;
				if(d.stg_use || d.stg_query || d.stg_group || d.stg_lock) {
					d.stg_use = d.stg_use ? 1 : 0;
					d.stg_query = d.stg_query ? 1 : 0;
					d.stg_group = d.stg_group ? 1 : 0;
					d.stg_lock = d.stg_lock ? 1 : 0;
					d.stg_sum = d.stg_sum ? 1 : 0;
					d.stg_double = d.stg_double ? 1 : 0;
					d.stg_dbfind = d.stg_dbfind ? 1 : 0;
					d.stg_stid = 0;
					d.stg_detno = i++;
					delete d.text;
					delete d.type;
					delete d.links;
					delete d.stg_alias;
					if (d.modeItems) {
						var validItems = [];
						Ext.Array.each(d.modeItems, function(item, i){
							if(item.display && item.display != '') {
								item.num = validItems.length + 1;
								item.stg_field = d.stg_field;
								item.value = (item.value == null || item.value == '') ?
										item.display : item.value;
								validItems.push(item);
							}
						});
						d.modeItems = validItems;
					}
					datas.push(d);
				}
			});
			me.onTempSave(title, datas, c, sorts, limits, preHook);
		}
	},
	onTempUpdate : function(datas, condition, sorts, limits, preHook, id, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/u.action',
			params: {
				sId: id,
				caller: caller,
				datas: Ext.encode(datas),
				condition: condition,
				sorts: sorts,
				limits: limits,
				preHook: preHook
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					callback && callback.call(me);
					Ext.example.msg('info', '提示', '方案修改成功', 2000);
					me.onTempLoad(function(data){
						var temp = me.querywin.down('gridpanel[id=temp]'),
							grid = me.querywin.down('grid');
						temp.store.loadData(data);
						if(data.length > 0) {
							var r = temp.store.findRecord('st_id', id) || temp.store.last();
							temp.selModel.select(r);
							grid.setTitle(r.get('st_title'));
						}
					});
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '失败', r.exceptionInfo, 5000);
				}
			}
		});
	},
	onTempSave : function(title, datas, condition, sorts, limits, preHook, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/s.action',
			params: {
				caller: caller,
				title: title,
				datas: Ext.encode(datas),
				condition: condition,
				sorts: sorts,
				limits: limits,
				preHook: preHook
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					callback && callback.call(me);
					Ext.example.msg('info', '提示', '方案保存成功', 2000);
					me.onTempLoad(function(data){
						var temp = me.querywin.down('gridpanel[id=temp]'),
							grid = me.querywin.down('grid');
						temp.store.loadData(data);
						if(data.length > 0) {
							var r = temp.store.last();
							temp.selModel.select(r);
							grid.setTitle(r.get('st_title'));
						}
					});
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '失败', r.exceptionInfo, 5000);
				}
			}
		});
	},
	onTempCopy : function(title, id) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/c.action',
			params: {
				title: title,
				sId: id
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					Ext.example.msg('info', '提示', '方案复制成功', 2000);
					me.onTempLoad(function(data){
						var temp = me.querywin.down('gridpanel[id=temp]'),
							grid = me.querywin.down('grid');
						temp.store.loadData(data);
						if(data.length > 0) {
							var r = temp.store.last();
							temp.selModel.select(r);
							grid.setTitle(r.get('st_title'));
						}
					});
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '失败', r.exceptionInfo, 5000);
				}
			}
		});
	},
	onTempTitleChange: function(title, id, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/temp/t.action',
			params: {
				title: title,
				sId: id
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					Ext.example.msg('info', '提示', '修改成功', 2000);
					callback && callback.call(me);
				} else if(r.exceptionInfo) {
					Ext.example.msg('error', '失败', r.exceptionInfo, 5000);
				}
			}
		});
	},
	onTempDel : function(id, fn) {
		if(id > 0) {
			Ext.Ajax.request({
				url: basePath + 'ma/search/temp/d.action',
				params: {
					caller: caller,
					sId: id
				},
				callback: function(opt, s, res) {
					var r = Ext.decode(res.responseText);
					if(r.success) {
						fn.call();
					} else if(r.exceptionInfo) {
						Ext.example.msg('error', '失败', r.exceptionInfo, 5000);
					}
				}
			});
		} else {
			fn.call();
		}
	},
	/**
	 * 记录选择方案
	 */
	log : function() {
		var grid = this.querywin.down('grid'), record = grid.selModel.lastSelected;
		if (record) {
			var id = record.get('st_id');
			if(id && id > 0 ) {
				Ext.Ajax.request({
					url: basePath + 'ma/search/log.action',
					params: {
						caller: caller,
						sId: id
					},
					callback: function(opt, s, res) {
						var r = Ext.decode(res.responseText);
						if(r.exceptionInfo) {
							Ext.example.msg('error', '错误', r.exceptionInfo, 5000);
						}
					}
				});
			}
		}
	},
	/**
	  * 数据字典 
	  */
	getDictionary: function(tableNames, callback) {
		if(tableNames && tableNames != '') {
			var me = this;
			Ext.Ajax.request({
				url: basePath + 'ma/getDataDictionaries.action',
				params: {
					tables: tableNames
				},
				callback: function(opt, s, res) {
					var r = Ext.decode(res.responseText);
					if(r.exceptionInfo) {
						Ext.example.msg('error', '错误', r.exceptionInfo, 5000);
					} else if(r.datas) {
						callback.call(me, r.datas);
					}
				}
			});
		} else {
			callback.call(me, []);
		}
	},
	/**
	 * 重新加载数据
	 */
	loadNewStore : function(grid, params) {
		var me = this;
		grid.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + "common/search.action",
        	params: params,
        	timeout: 6000000,
        	method : 'post',
        	callback : function(opt, s, res){
        		grid.setLoading(false);
        		grid.queryFilter = params.filter;
        		var r = Ext.decode(res.responseText);
        		if(r.datas) {
        			grid.store.totalCount = r.datas.length;
        			grid.store.loadData(r.datas);
        			me.getDataCount().update({count: r.datas.length});
					me.getDataCount().show();
        		}
        	}
		});
	},
	/**
	 * 导出excel
	 */
	exportExcel : function(grid, records, filter) {
		var columns = grid.headerCt.getGridColumns(),
			cm = new Array(), gf = grid.store.groupField;
		Ext.Array.each(columns, function(c){
			if(!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd) {
				cm.push({
					text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')), 
					dataIndex: c.dataIndex, 
					width: c.width, 
					xtype: c.xtype,
					format: c.format, 
					locked: c.locked, 
					summary: c.summaryType == 'sum', 
					group: c.dataIndex == gf
				});
			}
		});
		if (!Ext.fly('ext-grid-excel')) {
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';
			frm.name = frm.id;
			frm.className = 'x-hidden';
			document.body.appendChild(frm);
		}
		var record = records[0];
		Ext.Ajax.request({
			url: basePath + 'common/search/excel.xls',
			method: 'post',
			form: Ext.fly('ext-grid-excel'),
			isUpload: true,
			params: {
				sId: record.get('st_id'),
				filter: filter,
				columns: unescape(Ext.encode(cm).replace(/\\/g,"%")),
				sorts: record.get('st_sorts'),
				title: record.get('st_title') + '----' + Ext.Date.format(new Date(), 'Y-m-d H:i:s')
			}
		});
	},
	/**
	 * 传入表名，获得表的关联sql
	 */
	getTabSql: function(tables, callback) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'ma/search/relation.action',
			method : 'post',
			params: {
				tables: tables
			},
        	callback : function(opt, s, res){
        		var r = Ext.decode(res.responseText);
        		if(r.data) {
        			callback && callback.call(me, r.data);
        		} else {
        			Ext.example.msg('error', '错误', '您选择的数据字典 ' + tables + 
        					' 暂时还没有建立关联关系', 5000);
        		}
        	}
		});
    },
    /**
     * 行点击链接，打开单据界面
     */
    onLinkClick: function(link, title, field) {
    	var args = encodeURI(Ext.Array.toArray(arguments, 3).join('-')).replace(/%/g,'-');
    	var tabPanel = this.getTabPanel(), 
    		panel = tabPanel.down('#' + args);
    	link += '&_noc=1';// no control
    	// 出入库单据、工单、等按类型来获取caller，统一类型标志class
    	// 用于jsp页面一致，caller不同的
    	if(link.indexOf('class=') > -1) {
    		var cls = this.getUrlParam(link, 'class');
    		link += '&whoami=' + this.getCaller(cls, field);
    		// 无法直接配置，需要按类型来匹配具体链接的
	    	if(link.indexOf('{url}') > -1) {
	    		link = link.replace('{url}', this.getUrl(cls, field));
	    	}
    	}
    	if (!panel) {
    		panel = tabPanel.add({    
				id : args,
    			title : title,
    			tag : 'iframe',
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab1',
    			html : '<iframe src="' + link + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    			closable : true
    		});
    	}
    	tabPanel.setActiveTab(panel);
    },
    /**
     * 主界面的tabPanel
     */
    getTabPanel: function() {
    	var main = parent.Ext.getCmp("content-panel");
    	if (!main) {
			main = parent.parent.Ext.getCmp("content-panel");
		}
    	return main;
    },
    /**
     * URL里面解析出参数值
     */
    getUrlParam: function(url, param) {
    	var reg = new RegExp("(^|&)" + param + "=([^&]*)(&|$)"),
    		matchs = url.substr(url.indexOf('?') + 1).match(reg);
    	if (matchs)
    		return decodeURI(matchs[2]);
    	return null;
    },
    /**
     * 按类型获取caller。用于出入库单等
     */
    getCaller: function(cls, field) {
    	var call = null;
    	switch (cls) {
    	case '采购验收单':
    		call = 'ProdInOut!PurcCheckin';
    		break;
    	case '采购验退单':
    		call = 'ProdInOut!PurcCheckout';
    		break;
    	case '出货单':
    		call = 'ProdInOut!Sale';
    		break;
    	case '拨入单':
    		call = 'ProdInOut!AppropriationIn';
    		break;
    	case '销售拨出单':
    		call = 'ProdInOut!SaleAppropriationOut';
    		break;
    	case '销售退货单':
    		call = 'ProdInOut!SaleReturn';
    		break;
    	case '拨出单':
    		call = 'ProdInOut!AppropriationOut';
    		break;
    	case '不良品入库单':
    		call = 'ProdInOut!DefectIn';
    		break;
    	case '不良品出库单':
    		call = 'ProdInOut!DefectOut';
    		break;
    	case '委外领料单':
    		call = 'ProdInOut!OutsidePicking';
    		break;
    	case '委外退料单':
    		call = 'ProdInOut!OutsideReturn';
    		break;
    	case '委外验收单':
    		call = 'ProdInOut!OutsideCheckIn';
    		break;
    	case '委外验退单':
    		call = 'ProdInOut!OutesideCheckReturn';
    		break;
    	case '借货归还单':
    		call = 'ProdInOut!OutReturn';
    		break;
    	case '研发采购验收单':
    		call = 'ProdInOut!PurcCheckin!PLM';
    		break;
    	case '研发采购验退单':
    		call = 'ProdInOut!PurcCheckout!PLM';
    		break;
    	case '换货入库单':
    		call = 'ProdInOut!ExchangeIn';
    		break;
    	case '换货出库单':
    		call = 'ProdInOut!ExchangeOut';
    		break;
    	case '生产补料单':
    		call = 'ProdInOut!Make!Give';
    		break;
    	case '完工入库单':
    		call = 'ProdInOut!Make!In';
    		break;
    	case '生产退料单':
    		call = 'ProdInOut!Make!Return';
    		break;
    	case '生产报废单':
    		call = 'ProdInOut!Make!Useless';
    		break;
    	case '无订单出货单':
    		call = 'ProdInOut!NoSale';
    		break;
    	case '委外补料单':
    		call = 'ProdInOut!OSMake!Give';
    		break;
    	case '其它入库单':
    		call = 'ProdInOut!OtherIn';
    		break;
    	case '其它出库单':
    		call = 'ProdInOut!OtherOut';
    		break;
    	case '其它采购入库单':
    		call = 'ProdInOut!OtherPurcIn';
    		break;
    	case '其它采购出库单':
    		call = 'ProdInOut!OtherPurcOut';
    		break;
    	case '拆件入库单':
    		call = 'ProdInOut!PartitionStockIn';
    		break;
    	case '生产领料单':
    		call = 'ProdInOut!Picking';
    		break;
    	case '库存初始化':
    		call = 'ProdInOut!ReserveInitialize';
    		break;
    	case '借货出货单':
    		call = 'ProdInOut!SaleBorrow';
    		break;
    	case '销售拨入单':
    		call = 'ProdInOut!SalePutIn';
    		break;
    	case '盘亏调整单':
    		call = 'ProdInOut!StockLoss';
    		break;
    	case '盘盈调整单':
    		call = 'ProdInOut!StockProfit';
    		break;
    	case '报废单':
    		call = 'ProdInOut!StockScrap';
    		break;
    	case '研发退料单':
    		call = 'ProdInOut!YFIN';
    		break;
    	case '研发领料单':
    		call = 'ProdInOut!YFOUT';
    		break;
    	case 'MAKE':
    		call = 'Make!Base';
    		break;
    	case 'OS':
    		call = 'Make';
    		break;
    	case '采购收料单':
    		call = 'VerifyApply';
    		break;
    	case '委外收料单':
    		call = 'VerifyApply!OS';
    		break;
    	case '采购入库申请单':
    		call = 'VerifyApply';
    		break;
    	case '委外入库申请单':
    		call = 'VerifyApply!OS';
    		break;
    	case '应收发票':
    		call = 'ARBill!IRMA';
    		break;
    	case '应收款转销':
    		call = field == 'AB_CODE' ? 'ARBill!IRMA' : 'RecBalance!ARRM';
    		break;
    	case '其它应收单':
    		call = 'ARBill!OTRS';
    		break;
    	case '收款单':
    		call = 'RecBalance!PBIL';
    		break;
    	case '冲应收款':
    		call = 'RecBalance!IMRE';
    		break;
    	case '应收冲应付':
    		call = field == 'RB_CODE' ? 'RecBalance!RRCW' : 'PayBalance';
    		break;
    	case '预收冲应收':
    		call = 'RecBalance!PTAR';
    		break;
    	case '应收退款单':
    		call = 'RecBalance!TK';
    		break;
    	case '预收款':
    		call = 'PreRec!Ars!DERE';
    		break;
    	case '预收退款':
    		call = 'PreRec!Ars!DEPR';
    		break;
    	case '应付发票':
    		call = 'APBill!CWIM';
    		break;
    	case '其它应付单':
    		call = 'APBill!OTDW';
    		break;
    	case '付款单':
    		call = 'PayBalance';
    		break;
    	case '应付款转销':
    		call = 'PayBalance!APRM';
    		break;
    	case '冲应付款':
    		call = 'PayBalance!CAID';
    		break;
    	case '预付冲应付':
    		call = 'PayBalance!Arp!PADW';
    		break;
    	case '应付退款单':
    		call = 'PayBalance!TK';
    		break;
    	case '预付款':
    		call = 'PrePay!Arp!PAMT';
    		break;
    	case '预付退款':
    		call = 'PrePay!Arp!PAPR';
    		break;
    	}
    	return call;
    },
    /**
     * 存在同一table，同一链接不同caller情况；同一table，不同界面，不同caller的情况
     * 直接按类型取链接
     */
    getUrl: function(cls, field) {
    	var url = null;
    	switch (cls) {
    	case '应收发票':
    		url = 'jsps/fa/ars/arbill.jsp';
    		break;
    	case '应收款转销':
    		url = field == 'AB_CODE' ? 'jsps/fa/ars/arbill.jsp' : 'jsps/fa/ars/recBalance.jsp';
    		break;
    	case '其它应收单':
    		url = 'jsps/fa/ars/arbill.jsp';
    		break;
    	case '收款单':
    		url = 'jsps/fa/ars/recBalance.jsp';
    		break;
    	case '冲应收款':
    		url = 'jsps/fa/ars/recBalance.jsp';
    		break;
    	case '应收冲应付':
    		url = field == 'RB_CODE' ? 'jsps/fa/ars/recBalanceAP.jsp' : 'jsps/fa/arp/paybalance.jsp';
    		break;
    	case '预收冲应收':
    		url = 'jsps/fa/ars/recBalancePRDetail.jsp';
    		break;
    	case '应收退款单':
    		url = 'jsps/fa/ars/recBalanceTK.jsp';
    		break;
    	case '预收款':
    		url = 'jsps/fa/ars/preRec.jsp';
    		break;
    	case '预收退款':
    		url = 'jsps/fa/ars/preRec.jsp';
    		break;
    	case '应付发票':
    		url = 'jsps/fa/ars/apbill.jsp';
    		break;
    	case '其它应付单':
    		url = 'jsps/fa/ars/apbill.jsp';
    		break;
    	case '付款单':
    		url = 'jsps/fa/arp/paybalance.jsp';
    		break;
    	case '应付款转销':
    		url = 'jsps/fa/arp/paybalance.jsp';
    		break;
    	case '冲应付款':
    		url = 'jsps/fa/arp/paybalance.jsp';
    		break;
    	case '预付冲应付':
    		url = 'jsps/fa/arp/payBalancePRDetail.jsp';
    		break;
    	case '应付退款单':
    		url = 'jsps/fa/arp/paybalanceTK.jsp';
    		break;
    	case '预付款':
    		url = 'jsps/fa/arp/prepay.jsp';
    		break;
    	case '预付退款':
    		url = 'jsps/fa/arp/prepay.jsp';
    		break;
    	}
    	return url;
    }
});