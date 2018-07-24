Ext.QuickTips.init();
Ext.define('erp.controller.fa.VoucherCreate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views: ['fa.VoucherCreate', 'core.form.Panel', 'core.form.MonthDateField', 'core.form.ConDateField', 
            'core.form.MultiField', 'core.trigger.DbfindTrigger', 'core.button.Export', 
            'core.button.Close', 'core.button.Query', 'core.button.VoCreate','core.trigger.AddDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({
    		'erpFormPanel': {
    			titlechange:function(panel, newtitle, oldtitle){
    				var cls = getUrlParam('cls');
    				if(cls && newtitle.indexOf(cls) == -1){
    					newtitle = newtitle+'_'+cls;
	    				panel.setTitle(newtitle);
	    				var main = parent.Ext.getCmp("content-panel");
						if(main){
							var p = main.getActiveTab(); 
							if(p){
								p.on('titlechange', function(panel, newTitle, oldTitle){
									if(newTitle != newtitle){
										p.setTitle(newtitle);
									}
								});
							}
						}
	    			}
    			},
    			afterload: function(form) {
    				var bar = form.ownerCt.down('toolbar');
    				if(isCreate == 1){
    					Ext.defer(function(){
        					me.loadMergerWay(bar);
        				}, 200);
    				}
    			}
    		},
    		'field[name=yearmonth]': {
    			afterrender: function(f) {
    				me.getMonth(f);
    			},
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					var d = Ext.ComponentQuery.query('condatefield');
    					if(d && d.length > 0)
    						d[0].setMonthValue(f.value);
    				}
    			}
    		},
    		'condatefield[name=rb_date]': {
    			afterrender: function(f) {
    				f.hideScope(true);
    			}
    		},
    		'panel[id=vc-panel]': {
    			afterrender: function(p) {
					me.getVsSet(p);
				}
    		},
    		'erpQueryButton': {
    			click: function(btn) {
    				var condition = this.getCondition(btn.ownerCt.ownerCt);
    				if(condition != null) {
    					var grid = Ext.getCmp('grid');
    					grid.selModel.deselectAll(true);
    					grid.busy = true;
    					this.GridUtil.loadNewStore(grid, {caller: caller, condition: condition, start: 1, end: c_type == 'merge' ? 1000 : 200});
    				}
    			}
    		},
    		'gridpanel': {
    			storeloaded: function(grid) {
    				this.generateSummaryData(grid);
    			}
    		},
    		'erpVoCreateButton': {
    			afterrender: function(btn) {
    				if(!isCreate) {
    					btn.setText($I18N.common.button.erpVoUnCreateButton);
    					btn.setIconCls('x-button-icon-recall');
    				} else if(c_type == 'single'){
    					btn.ownerCt.insert(3, {
    						xtype: 'button',
    						iconCls: 'x-button-icon-check',
    						cls: 'x-btn-gray-right',
    						text: '合并生成',
    						id: 'mergecreate'
    					});
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.voucherCreate(btn);
        			},
        			lock:2000
    			}
    		},
    		'#mergecreate': {
    			click: {
    				fn:function(btn){
        				me.mergecreate(btn);
        			},
        			lock:2000
    			}
    		}
    	});
    },
    getCondition: function(form, _get) {
    	var items = form.items.items,
			val = null,value = new Array();
		Ext.each(items, function(item){
			if(item.logic != null) {
				val = item.value;
				if(!Ext.isEmpty(val)) {
					if(contains(val.toString(), 'BETWEEN', true) 
							&& contains(val.toString(), 'AND', true)){
						value.push('(' + item.logic + ' ' + val + ')');
					} else if(item.xtype == 'combo' && val == '$ALL'){
						var condition = '';
						if(item.store.data.length > 1) {
							item.store.each(function(d, idx){
								if(d.data.value != '$ALL') {
									if(condition == ''){
										condition += item.logic + "='" + d.data.value + "'";
									} else {
										condition += ' OR ' + item.logic + "='" + d.data.value + "'";
									}
								}
							});
						}
						if(condition != ''){
							value.push('(' + condition + ')');
						}
					} else if(item.xtype=='adddbfindtrigger' && item.value != null && item.value != ''){
						var condition = '';
						if(condition == ''){
							condition += item.logic + ' in (' ;		
						}
						var str=item.value,constr="";
						for(var i=0;i<str.split("#").length;i++){
							if(i<str.split("#").length-1){
								constr+="'"+str.split("#")[i]+"',";
							}else constr+="'"+str.split("#")[i]+"'";
						}
						condition += constr;
						condition += ') ';
						if(condition != ''){
							value.push('(' + condition + ')');
						}
					} else {
						if(String(val).indexOf('%') > 0) {
							value.push('(' + item.logic + ' like \'' + val + '\')');
						} else {
							value.push('(' + item.logic + '=\'' + val + '\')');
						}
					}
				}
			}
		});
		if(value.length > 0) {
			var grid = Ext.getCmp('grid');
			var condition = value.join(' AND ');
			condition += ' AND (' + (('nvl(' + grid.voucfield + ',\' \')') + (isCreate && !_get ? '=' : '<>') + '\' \')');
			condition += Ext.isEmpty(grid.defaultCondition) ? '' : (' AND ' + grid.defaultCondition);
			if(!isCreate) {
				condition += ' AND (' + grid.voucfield + ' IN (SELECT vo_code FROM Voucher WHERE ' + 
					'vo_createkind=\'' + (c_type == 'merge' ? '合并制作' : '单张制作') + '\'))';
			}
			if (form.classfield && cls) {
				condition += ' AND (' + form.classfield + '=\'' + cls + '\')';
			}
			return condition;
		}
		return null;
    },
    /**
     * 根据VoucherStyle--vs_datalist取Grid配置
     */
    getVsSet: function(p) {
    	var me = this;
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		params: {
	   			caller: 'VoucherStyle',
	   			fields: 'vs_datalist,vs_classfield,vs_prikey1,vs_voucfield,vs_datacondition,vs_detailtable,vs_datefield',
	   			condition: 'vs_code=\'' + vs_code + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);
	   			} else if(rs.success){
    				if(rs.data != null){
    					var form = Ext.getCmp('form');
    					form.classfield = rs.data.vs_classfield;
    					me.voucherSet = rs.data;
    					me.getGridSet(p, (window.caller || rs.data.vs_datalist), rs.data.vs_prikey1, 
    							rs.data.vs_voucfield, rs.data.vs_datacondition);
    				}
	   			}
	   		}
		});
    },
    getGridSet: function(p, cal, prikey, voucfield, condition) {
    	var me = this, grid = Ext.create('Ext.grid.Panel', {
    		anchor: '100% 100%',
    		id: 'grid',
    		columnLines: true,
    		columns: [],
    		store: [],
    		prikey: prikey,
    		voucfield: voucfield,
    		defaultCondition: condition,
    		caller: cal,
    		listeners: {
    			scrollershow: function(scroller) {
    				if (scroller && scroller.scrollEl) {
    					scroller.clearManagedListeners();  
    					scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
    				}
    			}
    		},
    		selModel: c_type == 'single' ? new Ext.selection.CheckboxModel({
    			checkOnly : true,
    			ignoreRightMouseSelection : false,
    			listeners: {
    				selectionchange: function(selModel, selected, options){
    		        	me.generateSummaryData(selModel.view.ownerCt, true);
    		        }
    			}
    		}) : new Ext.selection.Model()//单张制作时，有checkbox
    	});
    	p.add(grid);
    	this.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', {caller: caller, condition: null});
    },
    /**
     * 制作凭证
     */
    create: function(ym, kind, data, mode) {
    	var me = this, grid = Ext.getCmp('grid'), form = Ext.getCmp('form'), 
    		mergerway = form.ownerCt.down('toolbar').down('radio[checked=true]');
    	if(mergerway){
    		if('merge' == (mode || c_type)){
    			mergerway = mergerway.inputValue;
    		} else {
    			mergerway = '';
    		}
    	} else {
    		mergerway = '';
    	}
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/vc/createVoucher.action',
    		params: {
    			vs_code: vs_code,
    			yearmonth: ym,
    			datas: data,
    			mode: (mode || c_type),
    			kind: kind,
    			vomode: vo_type,
    			mergerway: mergerway
    		},
    		timeout: 60000,
    		callback: function(opt, s, r) {
    			grid.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else {
    				if(rs.success && rs.content){
		   				var msg = "";
		   				Ext.Array.each(rs.content, function(item){
		   					if(item.errMsg) {
		   						msg += item.errMsg + '<hr>';
		   					} else if(item.id) {
		   						msg += '凭证号:<a href="javascript:openUrl2(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' 
	    							+ item.id + '&gridCondition=vd_voidIS' + item.id + '\',\'凭证\',\'vo_id\','+item.id+');">' + item.code + '</a><hr>';	
		   					}
		   				});
    					showMessage('提示', msg);
    					var btn = Ext.getCmp('querybtn');
    					btn.fireEvent('click', btn);
		   			}
    			}
    		}
    	});
    },
    /**
     * 制作凭证
     */
    syncCreate: function(ym, kind, data) {
    	var res, form = Ext.getCmp('form'), 
			mergerway = form.ownerCt.down('toolbar').down('radio[checked=true]');
    	if(mergerway){
    		if('merge' == c_type){
    			mergerway = mergerway.inputValue;
    		} else {
    			mergerway = '';
    		}
    	} else {
    		mergerway = '';
    	}
    	Ext.Ajax.request({
    		url: basePath + 'fa/vc/createVoucher.action',
    		params: {
    			vs_code: vs_code,
    			yearmonth: ym,
    			datas: data,
    			mode: c_type,
    			kind: kind,
    			vomode: vo_type,
    			mergerway: mergerway
    		},
    		async: false,
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    				res = "";
    			} else if(rs.success && rs.content) {
    				var msg = "";
	   				Ext.Array.each(rs.content, function(item){
	   					if(item.errMsg) {
	   						msg += item.errMsg + '<hr>';
	   					} else if(item.id) {
	   						msg += '凭证号:<a href="javascript:openUrl2(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' 
    							+ item.id + '&gridCondition=vd_voidIS' + item.id + '\',\'凭证\',\'vo_id\','+item.id+');">' + item.code + '</a><hr>';	
	   					}
	   				});
    				res = msg;
    			}
    		}
    	});
    	return res;
    },
    /**
     * 超过40条的制作
     */
    largeCreate: function(ym, kind, datas) {
    	var size = 20, len = datas.length, t = Math.ceil(len / size), a = 0,
    		grid = Ext.getCmp('grid'), res= "", r;
    	grid.setLoading(true);
    	for (var i = 0;i < t;i++ ) {
    		var s = [], b = Math.min(a + size, len);
    		for(;a < b;a++ ) {
    			s.push(datas[a]);
    		}
    		r = this.syncCreate(ym, kind, s.join(','));
    		if(r){
    			res += r + '<hr>';
    		}
    	}
    	grid.setLoading(false);
    	var btn = Ext.getCmp('querybtn');
		btn.fireEvent('click', btn);
    	if(res) {
    		showMessage('提示', res);
    	}
    },
    /**
     * 取消凭证
     */
    unCreate: function(data) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/vc/unCreateVoucher.action',
    		params: {
    			vs_code: vs_code,
    			mode: c_type,
    			kind: cls,
    			datas: data,
    			vomode: vo_type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else if(rs.error) {
    				showMessage('提示', rs.error);
    				var btn = Ext.getCmp('querybtn');
    				btn.fireEvent('click', btn);
    			}
    		}
    	});
    },
    getMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			votype: 'GL'//vo_type//直接取总账期间
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    getVoucher: function(vs, kind, data, singlemerge, codeStr) {
    	var me = this, s = me.voucherSet, cond = '';
    	if('merge' == c_type) {
    		cond = this.getCondition(Ext.getCmp('form'), true);
    		codeStr && (cond += ' and (' + codeStr + ')');
    	} else if(singlemerge) {
    		cond = data;
    		if(s.vs_classfield)
    			cond += ' and ' + s.vs_classfield + '=\'' + kind + '\'';
    	} else {
    		cond = s.vs_prikey1 + ' in (' + data + ')';
    		if (s.vs_classfield)
    			cond += ' and ' + s.vs_classfield + '=\'' + kind + '\'';
    	}
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: s.vs_detailtable + ' left join voucher vo on vo.vo_code=' + s.vs_voucfield,
	   			fields: 'distinct vo.vo_id,' + s.vs_voucfield,
	   			condition: cond
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);
	   			} else if(rs.data){
    				var msg = '', json = Ext.decode(rs.data);
    				var f = s.vs_voucfield.toUpperCase(), id = 0;
    				for(var i in json) {
    					id = json[i]['VO_ID'];
    					if (id)
    						msg += '<hr><a href="javascript:openUrl(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + 
    							id + '&gridCondition=vd_voidIS' + id + '\')">' + json[i][f] + '</a>';
    				}
    				showMessage('提示', msg ? ('凭证:<br>' + msg) : '无需制作凭证');
	   			}
	   		}
		});
    },
	generateSummaryData : function(grid, onlySelected) {
		var me = this, store = grid.store,
        	columns = grid.columns, s = grid.features[grid.features.length - 1],
            i = 0, length = columns.length, comp, to = (onlySelected ? 'selected' : ''), 
            selected = grid.selModel.getSelection(),
            bar = (onlySelected ? grid.down('toolbar[to=' + to + ']') : grid.dockedItems.items[1]);
		if (!onlySelected && !bar) return;
        for (; i < length; i++ ) {
        	comp = columns[i];
        	if(comp.summaryType) {
        		if(!bar)
        			bar = grid.addDocked({
    			    	xtype: 'toolbar',
    			    	dock: 'bottom',
    			    	to: to,
    			    	items: [{
    			    		xtype: 'tbtext',
    			    		text: '已勾选',
    			    		style: {
    			    			marginLeft: '6px'
    			    		}
    			    	}]
    			    })[0];
                var tb_id = comp.dataIndex + '_' + comp.summaryType + to,
                	tb = bar.down('tbtext[id=' + tb_id + ']');
                if(!tb){
                	bar.add('-');
                	tb = bar.add({
                		id: tb_id,
    					itemId: comp.dataIndex,
    					xtype: 'tbtext'
                	});
                }
                var val = (onlySelected ? me.getSummaryBySelected(store, selected, comp.summaryType, comp.dataIndex)
                		: me.getSummary(store, comp.summaryType, comp.dataIndex, false));
                if(comp.xtype == 'numbercolumn') {
            		val = Ext.util.Format.number(val, (comp.format || '0,000.000'));
    			}
            	tb.setText(comp.text + ':' + val);
        	}
        }
        if (bar) {
			var count = bar.down('tbtext[itemId=count]');
			if(!count) {
				bar.add('->');
				count = bar.add({
					xtype: 'tbtext',
					itemId: 'count'
				});
			}
			count.setText(onlySelected ? ('已选: ' + selected.length + ' 条' ) : ('共: ' + (store.data.items[0].get(grid.keyField) ==0 ? 0 : store.count()) + ' 条'));
		}
    },
    getSummary: function(store, type, field, group){
        if (type) {
            if (Ext.isFunction(type)) {
                return store.aggregate(type, null, group);
            }
            switch (type) {
                case 'count':
                    return store.count(group);
                case 'min':
                    return store.min(field, group);
                case 'max':
                    return store.max(field, group);
                case 'sum':
                    return store.sum(field, group);
                case 'average':
                    return store.average(field, group);
                default:
                    return group ? {} : '';
                    
            }
        }
    },
    getSummaryBySelected: function(store, selected, type, field){
        if (type) {
        	if (Ext.isFunction(type)) {
                return;
            }
            switch (type) {
                case 'count':
                    return selected.length;
                case 'min':
                    return store.getMin(selected, field);
                case 'max':
                    return store.getMax(selected, field);
                case 'sum':
                    return store.getSum(selected, field);
                case 'average':
                    return store.getAverage(selected, field);
                default:
                    return '';
                    
            }
        }
    },
    voucherCreate:function(btn){
    	var me = this;
    	var form = btn.ownerCt.ownerCt, ym = form.down('#yearmonth').value;
		var grid = Ext.getCmp('grid'),s = new Array(), c = null;
		var dateField = me.voucherSet.vs_datefield, 
			first = grid.store.first();
		if(dateField && first.get(dateField)) {
			var val = first.get(dateField), dateYm = 0;
			if(!Ext.isDate(val)) {
				if(/\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/.test(val))
					dateYm = Ext.Date.format(Ext.Date.parse(val, 'Y-m-d H:i:s'), 'Ym');
				else if(/\d{4}-\d{2}-\d{2}/.test(val))
					dateYm = Ext.Date.format(Ext.Date.parse(val, 'Y-m-d'), 'Ym');
			} else
				dateYm  = Ext.Date.format(val, 'Ym');
			if(Number(dateYm) != Number(ym)) {
				showError('单据日期与表头选择的期次是不一致，请重新筛选后再继续操作');
				return;
			}
		}
		if(c_type == 'single') {
			grid.store.each(function(item){
				c = item.get(grid.prikey);
				if(!Ext.isEmpty(c)) {
					if(grid.selModel.isSelected(item)) {
						s.push("'" + c + "'");
					}
				}
			});
			if(s.length > 0) {
				if(isCreate) {
					if(s.length > 40) {// 超过40条的，换成分批制作
						me.largeCreate(ym, cls, s);
					} else {
						me.create(ym, cls, s.join(','));
					}
				} else {
					me.unCreate(s.join(','));
				}
			} else
				showError('没有数据！');
		} else {// 合并制作的改为直接传递条件
			if(grid.store.data.length > 0 && !Ext.isEmpty(first.get(grid.prikey))) {
				var condition = this.getCondition(form);
				if(condition != null) {
					if(isCreate) {
						me.create(ym, cls, condition);
					} else {
						me.unCreate(condition);
					}
				}
			} else
				showError('没有数据！');
		}
    },
    mergecreate: function(btn){
    	var me = this;
    	var form = btn.ownerCt.ownerCt, ym = form.down('#yearmonth').value;
		var grid = Ext.getCmp('grid'),s = new Array(), c = null;
		var dateField = me.voucherSet.vs_datefield, 
			first = grid.store.first();
		if(dateField && first.get(dateField)) {
			var val = first.get(dateField), dateYm = 0;
			if(!Ext.isDate(val)) {
				if(/\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/.test(val))
					dateYm = Ext.Date.format(Ext.Date.parse(val, 'Y-m-d H:i:s'), 'Ym');
				else if(/\d{4}-\d{2}-\d{2}/.test(val))
					dateYm = Ext.Date.format(Ext.Date.parse(val, 'Y-m-d'), 'Ym');
			} else
				dateYm  = Ext.Date.format(val, 'Ym');
			if(Number(dateYm) != Number(ym)) {
				showError('单据日期与表头选择的期次是不一致，请重新筛选后再继续操作');
				return;
			}
		}
		grid.store.each(function(item){
			c = item.get(grid.prikey);
			if(!Ext.isEmpty(c)) {
				if(grid.selModel.isSelected(item)) {
					s.push("'" + c + "'");
				}
			}
		});
		if(s.length > 0) {
			var cond = grid.prikey + ' in (' + s.join(',') + ')';
			if(isCreate) {
				me.create(ym, cls, cond, 'merge');
			} else {
				me.unCreate(cond);
			}
		} else
			showError('没有数据！');
    },
    loadMergerWay: function(bar) {
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/getFieldsDatas.action",
        	params: {
        		caller: "VOUCHERSTYLE left join VOUCHERSTYLEGROUP on vsg_vsid=vs_id",
        		fields: 'vsg_groupname,vsg_groupfield',
        		condition: 'vs_code=\'' + vs_code + '\'' 
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(data != null && res.data != '[{}]'){
        			data = new Ext.decode(data);
        			if(data != null && data.length > 0){
            			bar.add({
        					xtype: 'fieldcontainer',
        					name: 'all',
        					margin: '0 10 0 0',
        					columnWidth: 1,
        					items: [{
        						xtype: 'radio',
        						columnWidth: 1,
        						boxLabel: '全部合并',
        						name: 'mergerway',
        						inputValue: 'ALL',
        						checked: true
        					}],
        					getValue: function(){
        						return 'all';
        					},
        					setValue: function(){
        						this.down('radio').setValue(true);
        					}
            			});
            			Ext.Array.each(data, function(d){
            				bar.add({
            					xtype: 'fieldcontainer',
            					name: d.VSG_GROUPFIELD,
            					margin: '0 10 0 0',
            					columnWidth: 1,
            					items: [{
            						xtype: 'radio',
            						columnWidth: 1,
            						boxLabel: d.VSG_GROUPNAME,
            						inputValue: d.VSG_GROUPFIELD,
            						name: 'mergerway'
            					}],
            					getValue: function(){
            						return d.VSG_GROUPFIELD;
            					},
            					setValue: function(){
            						this.down('radio').setValue(true);
            					}
                			});
    					});
            		}
        		}
        	}
		});
	}
});