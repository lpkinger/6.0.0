Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.GeneralLedgerSingle', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.GeneralLedgerSingle', 'fa.gla.LedgerSingle', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField', 'core.trigger.CateTreeDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					me.showFilterPanel(btn);
    					me.getCateSetting();
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'ledgerSingle': {
    			itemclick: function(selModel, record) {
    				me.loadDetail(selModel.ownerCt.store, record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('ledgerSingle');
    				me.BaseUtil.exportGrid(grid, '总账');
    			}
    		},
    		'#ak_asscode': {
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					var ch = f.up('window').down('#chkall');
    					if(!ch.value) {
    						ch.setValue(true);
    					}
    				}
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
    query: function(cond) {
    	var grid = Ext.getCmp('ledgerSingle');
    	if(cond.cm_yearmonth.end - cond.cm_yearmonth.begin > 1) {// 期间跨度大于2个月
    		if(!cond.ca_code) {
    			showError('当前期间跨度较大，请选择科目号.');
    			return;
    		}
    	}
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getGeneralLedgerSingle.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				var cmc_currency = grid.down('gridcolumn[dataIndex=cmc_currency]');
    				grid.store.loadData(res.data);
    				if(cond.cmc_currency != '0') {//显示原币
    					if(!cmc_currency) {
    						grid.reconfigure(grid.store, grid.doubleColumns);
    					}
    				} else {//只显示本币
    					if(cmc_currency) {
    						grid.reconfigure(grid.store, grid.defaultColumns);
    					}
    				}
    				var am_asscode = grid.down('gridcolumn[dataIndex=am_asscode]'),
						am_assname = grid.down('gridcolumn[dataIndex=am_assname]');
    				if(cond.chkall) {
    					if(am_assname.hidden)
    						am_assname.show();
    					if(am_asscode.hidden)
    						am_asscode.show();
    				} else {
    					if(!am_assname.hidden)
    						am_assname.hide();
    					if(!am_asscode.hidden)
    						am_asscode.hide();
    				}
    			}
    			grid.setLoading(false);
    		}
    	});
    },
    getCondition: function(pl) {
    	var r = new Object(),v;
    	Ext.each(pl.items.items, function(item){
    		if(item.getValue !== undefined) {
    			v = item.getValue();
        		if(!Ext.isEmpty(v)) {
        			r[item.id] = v;
        		}
    		}
    	});
    	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	return r;
    },
    loadDetail: function(store, record) {
    	var ym = record.get('cm_yearmonth'),
    		caCode = this._getCaCode(store, record),
    		cr = record.get('cmc_currency'),
    		at = record.get('am_asstype'),
    		ac = record.get('am_asscode'),
    		an = record.get('am_assname'),
    		un = Ext.getCmp('chkhaveun').value;
    	this.BaseUtil.onAdd('GLDetail_' + ym + '_' + caCode, '明细账', 
    			'jsps/fa/gla/glDetail.jsp?y=' + ym + '&c=' + caCode + '&cr=' + cr + '&at=' + at + '&ac=' + ac + '&an=' + an + '&un=' + un);
    },
    _getCaCode: function(store, record) {
    	var c = record.get('ca_code');
    	if(Ext.isEmpty(c)) {
    		return this._getCaCode(store, store.getAt(store.indexOf(record) - 1));
    	}
    	return c;
    },
	getCateSetting : function() {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'CateTreeSearch\''
	   		},
	   		method : 'post',
	   		async : false,
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			} else if(r.success && r.data == 'true' ){
    				me.CateTreeSearch = true;
	   			}
	   		}
		});
	},
	createYearmonthField : function() {
		return Ext.create('erp.view.core.form.ConMonthDateField', {
			id: 'cm_yearmonth',
			name: 'cm_yearmonth',
			fieldLabel: '期间',
			labelWidth: 80,
			margin: '10 2 2 10',
			columnWidth: .51,
			getValue: function() {
				if(!Ext.isEmpty(this.value)) {
					return {begin: this.firstVal, end: this.secondVal};
				}
				return null;
			}
		});
	},
	createCateLevelField : function() {
		return Ext.create('Ext.form.FieldContainer', {
			fieldLabel: '科目级别',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .51,
			id: 'cm_catelevel',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				xtype: 'combo',
				flex: 1,
				editable: false,
				name: 'ca_level1',
				id: 'ca_level1',
				displayField: 'display',
				valueField: 'value',
				queryMode: 'local',
				value: 1,
				store: Ext.create('Ext.data.Store', {
		            fields: ['display', 'value'],
		            data : [
		                {"display": 1, "value": 1},
		                {"display": 2, "value": 2},
		                {"display": 3, "value": 3},
		                {"display": 4, "value": 4},
		                {"display": 5, "value": 5}
		            ]
		        })
			},{
				xtype: 'combo',
				flex: 1,
				name: 'ca_level2',
				id: 'ca_level2',
				editable: false,
				displayField: 'display',
				valueField: 'value',
				queryMode: 'local',
				value: 5,
				store: Ext.create('Ext.data.Store', {
		            fields: ['display', 'value'],
		            data : [
		                {"display": 1, "value": 1},
		                {"display": 2, "value": 2},
		                {"display": 3, "value": 3},
		                {"display": 4, "value": 4},
		                {"display": 5, "value": 5}
		            ]
		        })
			}],
			getValue: function() {
				var a = this.down('#ca_level1').value,b = this.down('#ca_level2').value;
				return {begin: Math.min(a, b), end: Math.max(a, b)};
			}
		});
	},
	createCateField : function() {
		var me = this ,t, t1;
		if (me.CateTreeSearch) {
			t = Ext.create('Ext.form.field.Trigger', {
				triggerCls: 'x-form-search-trigger',
				id: 'vd_catecode',
				name: 'vd_catecode',
				columnWidth: 0.4,
				onTriggerClick: function() {
					me.showCateTree(this);
				},
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d[0].raw.data.ca_code);
						t.ownerCt.down('#vd_catename').setValue(d[0].raw.data.ca_name);
					}
				}
			});
			t1 = Ext.create('Ext.form.field.Trigger', {
				triggerCls: 'x-form-search-trigger',
				id: 'vd_catecode1',
				name: 'vd_catecode1',
				columnWidth: 0.4,
				onTriggerClick: function() {
					me.showCateTree(this);
				},
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d[0].raw.data.ca_code);
						t.ownerCt.down('#vd_catename1').setValue(d[0].raw.data.ca_name);
					}
				}
			});
		} else {
			t = Ext.create('erp.view.core.trigger.DbfindTrigger', {
				id: 'vd_catecode',
				name: 'vd_catecode',
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d.data.ca_code);
						t.ownerCt.down('#vd_catename').setValue(d.data.ca_name);
					}
				}
			});
			t1 = Ext.create('erp.view.core.trigger.DbfindTrigger', {
				id: 'vd_catecode1',
				name: 'vd_catecode1',
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d.data.ca_code);
						t.ownerCt.down('#vd_catename1').setValue(d.data.ca_name);
					}
				}
			});
		}
		return Ext.create('Ext.form.FieldContainer', {
			fieldLabel: '科目编号',
			labelWidth: 80,
			layout: 'column',
			columnWidth: 1,
			height: 56,
			id: 'ca_code',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [t, {
				xtype: 'textfield',
				name: 'vd_catename',
				id: 'vd_catename',
				columnWidth: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},t1,{
				xtype: 'textfield',
				name: 'vd_catename1',
				id: 'vd_catename1',
				columnWidth: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = this.down('#vd_catecode').value,b = this.down('#vd_catecode1').value,
					x = Ext.isEmpty(a), y = Ext.isEmpty(b);
				if(x && y) {
					return null;
				} else if(x && !y) {
					a = b;
				} else if(!x && y){
					b = a;
				}
				return {begin: a, end: b};
			}
		});
	},
	createCurrencyField : function() {
		var me = this;
		return Ext.create('Ext.form.field.ComboBox', {
			fieldLabel: '币别',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .51,
			id: 'cmc_currency',
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			editable: false,
			store: Ext.create('Ext.data.Store', {
	            fields: ['display', 'value'],
	            data : [
	                {"display": '本位币', "value": '0'},
	                {"display": '所有币别', "value": '99'}
	            ]
	        }),
	        value: '0',
			listeners: {
				afterrender: function(f) {
					me.getCurrency(f);
				}
			}
		});
	},
	createAssField : function() {
		return Ext.create('Ext.form.FieldContainer', {
			fieldLabel: '辅助核算',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: 1,
			id: 'vds_asscode',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				labelWidth: 35,
				xtype: 'dbfindtrigger',
				flex: 0.2,
				id: 'ak_name',
				name: 'ak_name',
	    		listeners: {
	    			aftertrigger: function(f, r) {
	    				if(!Ext.isEmpty(f.value)) {
	    					f.asskind = r.data;
	    					var c = Ext.getCmp('ak_asscode'),
	    						n = Ext.getCmp('ak_assname');
	    					c.name = r.data.ak_asscode;
	    					n.name = r.data.ak_assname;
	    				} else {
	    					f.asskind = null;
	    				}
	    			},
	    			change: function(f) {
	    				var a = Ext.getCmp('ak_asscode'),
	    					b = Ext.getCmp('ak_assname');
	    				a.setValue(null);
	    				b.setValue(null);
	    				if(Ext.isEmpty(f.value)) {
	    					f.asskind = null;
	    				}
	    			}
	    		}
			},{
				xtype: 'dbfindtrigger',
				id: 'ak_asscode',
				flex: 0.2,
				listeners: {
					beforetrigger: function(f) {
						var a = Ext.getCmp('ak_name');
						f.dbBaseCondition = null;
						if(!a.asskind) {
							a.focus(false, 200);
							showError('请先选择核算项!');
							return false;
						} else if(f.name == 'akd_asscode') {
							f.dbBaseCondition = 'ak_name=\'' + a.value + '\'';
						}
					},
					aftertrigger: function(f, r) {
						var a = Ext.getCmp('ak_name'),
							n = Ext.getCmp('ak_assname');
						if(a.asskind) {
							f.setValue(r.data[a.asskind.ak_asscode]);
							n.setValue(r.data[a.asskind.ak_assname]);
						}
	    			}
				}
			},{
				xtype: 'textfield',
				id: 'ak_assname',
				flex: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = Ext.getCmp('ak_name'),
					c = Ext.getCmp('ak_asscode');
				if(a.asskind) {
					return {am_asstype: a.asskind.ak_name, am_asscode: c.value};
				}
				return null;
			}
		});
	},
    createFilterPanel: function(btn) {
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 500,
    		height: 415,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCateLevelField(), me.createCateField(),
    	            me.createCurrencyField(), me.createAssField(),{
				xtype: 'checkbox',
				id: 'chkall',
				name: 'chkall',
				columnWidth: .5,
				boxLabel: '显示辅助核算'
			},{
				xtype: 'checkbox',
				id: 'chkno',
				name: 'chkno',
				columnWidth: .5,
				boxLabel: '无发生额不显示'
			},{
				xtype: 'checkbox',
				id: 'chkzeroandno',
				name: 'chkzeroandno',
				columnWidth: .5,
				boxLabel: '余额为零且无发生额不显示'
			},{
				xtype: 'checkbox',
				id: 'chkhaveun',
				name: 'chkhaveun',
				columnWidth: .5,
				boxLabel: '包含未记账凭证'
			},{
				xtype: 'checkbox',
				id: 'chkDispLeaf',
				name: 'chkDispLeaf',
				columnWidth: .5,
				boxLabel: '只显示末级科目'
			},{
				xtype: 'checkbox',
				id: 'chkdis',
				name: 'chkdis',
				columnWidth: 1,
				boxLabel: '显示禁用科目'
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt,
						con = me.getCondition(fl);
					me.query(con);
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
    	this.getCurrentMonth(filter.down('#cm_yearmonth'));
		return filter;
    },
    getCurrency: function(f) {
    	Ext.Ajax.request({
    		url : basePath + 'common/getFieldDatas.action',
       		async: false,
       		params: {
       			caller: 'Currencys',
       			field: 'cr_name',
       			condition: 'cr_statuscode=\'CANUSE\''
       		},
       		method : 'post',
       		callback : function(options,success,response){
       			var rs = new Ext.decode(response.responseText);
       			if(rs.exceptionInfo){
       				showError(rs.exceptionInfo);return null;
       			}
    			if(rs.success && rs.data){
    				var cr = rs.data.split('#'),c = new Array();
    				Ext.each(cr, function(r){
    					c.push({display: r, value: r});
    				});
    				f.store.add(c);
    			}
       		}
    	});
    },
    getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				var month=String(rs.data.PD_DETNO).substring(0,6);
    				f.setValue(month);
    			}
    		}
    	});
    },
    showCateTree: function(f) {
		var cawin = f.win;
		if(!cawin) {
			f.win = cawin = new Ext.window.Window({
			    title: '科目查找',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				modal:true,
			    items: [{
			    	tag : 'iframe',
			    	frame : true,
			    	anchor : '100% 100%',
			    	layout : 'fit',
			    	html : '<iframe src="'+basePath+'jsps/common/catetreepaneldbfind.jsp?key='+f.name+"&dbfind=&caller1="+caller+"&keyValue="+f.value+"&trigger="+f.id+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	text : '确  认',
			    	iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		var contentwindow = btn.ownerCt.ownerCt.body.dom.getElementsByTagName('iframe')[0].contentWindow;
			    		var tree = contentwindow.Ext.getCmp('tree-panel');
			    		var data = tree.getChecked();
			    		f.fireEvent('aftertrigger', f, data);
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    },{
			    	text : '关  闭',
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    }]
			});
		}
		cawin.show();
	}
});