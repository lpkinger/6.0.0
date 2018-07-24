Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.EmplCost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.EmplCost', 'fa.gla.EmplCostGrid', 'core.form.MonthDateField', 'core.trigger.CateTreeDbfindTrigger',
            'core.form.ConMonthDateField', 'core.form.ConDateField', 'core.form.YearDateField', 'core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					var y = getUrlParam('y'),
    					c = getUrlParam('c'),
    					cr = getUrlParam('cr'),
    					at = getUrlParam('at'),
    					ac = getUrlParam('ac'),
    					an = getUrlParam('an'),
    					un = getUrlParam('un');
    					me.showFilterPanel(btn, y, c, cr, at, ac, an, un);
    					if(y && y != 'undefined') {
    						var fl = Ext.getCmp('query-filter'),
	        					con = me.getCondition(fl);
	        				con.querytype = 'current';
	        				me.query(con);
    					}
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},

    		'EmplCostgrid': {
    			itemclick: function(selModel, record) {
    				me.loadVoucher(record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('emplcost');
    				me.BaseUtil.exportGrid(grid, '人员费用');
    			}
    		}
    	});
    },
    showFilterPanel: function(btn, ym, caCode, curr, at, ac, an, un) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(!filter) {
    		filter = this.createFilterPanel(btn);
    	}
    	filter.show();
    	if(ym && ym != 'undefined') {
    		filter.down('#sl_yearmonth').setValue(ym);
    		filter.hide();
    	}
    	if(caCode && caCode != 'undefined') {
    		filter.down('#ca_code').setValue(caCode);
    		this.catecode = caCode;//当前科目
    	}
    	if(at && at != 'undefined') {
    		filter.down('#ak_name').setValue(at);
    		filter.down('#ak_name').asskind = {
    			ak_name: at
    		};
    		filter.down('#ak_name').autoDbfind('form', caller, 'ak_name', 'ak_name like \'%' + at + '%\'');
    	}
    	if(ac && ac != 'undefined') {
    		filter.down('#ak_asscode').setValue(ac);
    		filter.down('#chkall').setValue(true);
    	}
    	if(an && an != 'undefined') {
    		filter.down('#ak_assname').setValue(an);
    	}
    	if(un && un != 'undefined') {
    		filter.down('#chkhaveun').setValue(un);
    	}
    },
    hideFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(filter) {
    		filter.hide();
    	}
    },
    query: function(cond) {
    	var me = this,
		grid = Ext.getCmp('emplcost');
		grid.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'fa/ars/getEmplCost.action',
			params: {
				condition: Ext.encode(cond)
			},
			callback: function(opt, s, r) {
				var res = Ext.decode(r.responseText);
				var clmlist = [];
				Ext.each(grid.columns,function(column,index){
					if(column.type == 'add'){
						clmlist.push(column);
					}
				});
				Ext.each(clmlist,function(column,index){
					grid.headerCt.remove(column);
				});
				if(grid && res.column) {
					Ext.each(res.column,function(colu, index){
						grid.headerCt.insert(grid.columns.length, new Ext.grid.column.Column({
							type:'add',
							dataIndex: 'cacode_'+colu.ca_code,
							cls: 'x-grid-header-1',
							sortable: false,
							text: colu.ca_name+'('+colu.ca_code+')',
							width: 150,
							xtype: 'numbercolumn',
							format: '0,000.000',
							align: 'right'
						}));
					});
					grid.getView().refresh();
					if(res.data != null){
						console.log(res.data);
						grid.store.loadData(res.data);
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
    	if(this.catecode) {
    		r.catecode = this.catecode;
    	}
    	return r;
    },
    loadVoucher: function(record) {
    	var me = this, vc = record.get('sl_vocode');
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'Voucher',
	   			field: 'vo_id',
	   			condition: 'vo_code=\'' + vc + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return null;
	   			}
    			if(rs.success){
    				if(rs.data != null){
    					me.BaseUtil.onAdd('Voucher_' + vc, '凭证', 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + rs.data +
    			    			'&gridCondition=vd_voidIS' + rs.data);
    				}
	   			}
	   		}
		});
    },
    createYearmonthField : function() {
    	return Ext.create('Ext.form.FieldContainer', {
	    	margin: '10 2 2 10',
	    	columnWidth: 1,
	    	layout: 'column',
	    	id:'yearmonth',
	    	name:'yearmonth',
	    	items: [{
	    		fieldLabel:'期间',
	    		id: 'am_yearmonth',
	    		labelWidth: 80,
				name: 'am_yearmonth',
				xtype: 'conmonthdatefield',
				columnWidth: 1,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				}
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('am_yearmonth');
	    		return a.getValue();
	    	}
    	});
    },
    createCateField : function() {
    	var me = this, t, t1;
    	if (me.CateTreeSearch) {
    		t = Ext.create('erp.view.core.trigger.CateTreeDbfindTrigger', {
				name: 'vd_catecode',
				id: 'vd_catecode',
				autoDbfind: false,
				columnWidth: 0.4,
				listeners: {
					change: function() {
						me.catecode = null;
					},
					aftertrigger: function(t, d) {
						t.ownerCt.down('#vd_catename').setValue(d[0].raw.data.ca_name);
					}
				}
    		});
    		t1 = Ext.create('erp.view.core.trigger.CateTreeDbfindTrigger', {
				name: 'vd_catecode1',
				id: 'vd_catecode1',
				autoDbfind: false,
				columnWidth: 0.4,
				listeners: {
					change: function() {
						me.catecode = null;
					},
					aftertrigger: function(t, d) {
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
						if (d) {
							t.setValue(d.data.ca_code);
							t.ownerCt.down('#vd_catename').setValue(d.data.ca_name);
						}
					}
				}
			});
			t1 = Ext.create('erp.view.core.trigger.DbfindTrigger', {
				id: 'vd_catecode1',
				name: 'vd_catecode1',
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						if (d) {
							t.setValue(d.data.ca_code);
							t.ownerCt.down('#vd_catename1').setValue(d.data.ca_name);
						}
					}
				}
			});
    	}
    	return Ext.create('Ext.form.FieldContainer', {
			id: 'ca_code',
			margin: '2 2 2 10',
	    	columnWidth: 1,
	    	height: 100,
	    	layout: 'column',
	    	items: [{
	    		fieldLabel:'科目',
				labelWidth: 80,
				layout: 'column',
				columnWidth: 1,
				height: 56,
				xtype: 'fieldcontainer',
				id: 'con_vd_catecode',
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
				},t1 ,{
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
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('con_vd_catecode');
	    		var val = a.getValue();
	    		if(val) {
    				if(me.catecode == null) {
    					me.catecode = val.begin;
    				}
	    			return  {continuous: true,value: val};
	    		}
	    		return null;
	    	},
	    	setValue: function(v) {
	    		Ext.getCmp('vd_catecode').setValue(v);
	    		Ext.getCmp('vd_catecode1').setValue(v);
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
			id: 'ca_level',
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
			},
			setValue: function() {
				
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
				value:'员工',
				readOnly:true,
				asskind:{
					ak_asscode:'em_code',
					ak_assname:'em_name',
					ak_code:'Empl',
					ak_dbfind:'Employee',
					ak_table:'Employee',
					ak_name:'员工'
				},
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
	    			change: function() {
	    				var a = Ext.getCmp('ak_asscode'),
	    					b = Ext.getCmp('ak_assname');
	    				a.setValue(null);
	    				b.setValue(null);
	    			}
	    		}
			},{
				xtype: 'dbfindtrigger',
				id: 'ak_asscode',
				name:'em_code',
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
				name:'em_name',
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
    		height: 350,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCateField(), me.createCateLevelField(), me.createAssField(),
    	            {
    					xtype: 'checkbox',
    					id: 'chkhaveun',
    					name: 'chkhaveun',
    					columnWidth: .5,
    					boxLabel: '包括未过账凭证'
    				}],
    	    buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
	    			var fl = Ext.getCmp('query-filter'),
						con = me.getCondition(fl);
	    			con.querytype = 'current';
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
    	this.getCurrentMonth(filter.down('#am_yearmonth'));
		return filter;
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
    }
});