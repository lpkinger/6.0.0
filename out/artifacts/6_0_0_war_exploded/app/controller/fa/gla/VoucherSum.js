Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.VoucherSum', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.VoucherSum', 'fa.gla.VoucherSumDetail', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.ConDateField','core.form.YearDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					me.showFilterPanel(btn);
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('vouchersumdetail');
    				me.BaseUtil.exportGrid(grid, '凭证汇总表');
    			}
    		},
    		'#vo_status': {
    			change: function(f) {
    				var enter = f.up('window').down('#enter');
    				if(f.value == 1){
    					enter.setValue(false);
    					enter.setReadOnly(true);
    				} else {
    					enter.setValue(false);
    					enter.setReadOnly(false);
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
    	var grid = Ext.getCmp('vouchersumdetail');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getVoucherSum.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			grid.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				var sl_currency = grid.down('gridcolumn[dataIndex=sl_currency]');
    				var count = Ext.getCmp('count');
    				if(cond.sl_currency != '0') {//显示原币
    					grid.reconfigure(grid.store, grid.doubleColumns);
    				} else {//只显示本币
    					grid.reconfigure(grid.store, grid.defaultColumns);
    				}
    				grid.store.loadData(res.data);
    			}
    		}
    	});
    },
    getCount: function(cond) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getVoucherSumCount.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				Ext.getCmp('count').setText(String(rs.data));
    			} else {
    				Ext.getCmp('count').setText('0');
    			}
    		}
    	});
    },
    getCondition: function(pl) {
    	var me = this, r = new Object(),v;
    	Ext.each(pl.items.items, function(item){
    		if(item.getValue !== undefined) {
    			v = item.getValue();
        		if(!Ext.isEmpty(v)) {
        			r[item.id] = v;
        		}
    		}
    	});
    	me._updateInfo(r);
    	return r;
    },
    _updateInfo: function(r) {
       	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	tb = Ext.getCmp('gl_info_curr');
    	if(tb)
    		tb.updateInfo(r);
    },
	createYearmonthField : function() {
    	return Ext.create('Ext.form.FieldContainer', {
	    	margin: '10 2 2 10',
	    	columnWidth: 1,
	    	height: 70,
	    	layout: 'column',
	    	items: [{
	    		xtype: 'radio',
	    		boxLabel: '按期间查询',
	    		name: 'dateorym',
	    		columnWidth: 0.3,
	    		checked: true,
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('sl_yearmonth');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'sl_yearmonth',
				name: 'sl_yearmonth',
				xtype: 'conmonthdatefield',
				columnWidth: .7,
				height: 30,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				}
	    	},{
	    		xtype: 'radio',
	    		boxLabel: '按日期查询',
	    		name: 'dateorym',
	    		columnWidth: 0.3,
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('sl_date');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'sl_date',
				name: 'sl_date',
				xtype: 'condatefield',
				disabled: true,
				columnWidth: .7,
				height: 30,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: Ext.Date.toString(this.firstVal), end: Ext.Date.toString(this.secondVal)};
					}
					return null;
				}
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('sl_yearmonth'),b = Ext.getCmp('sl_date');
	    		this.id = b.disabled ? 'sl_yearmonth' : 'sl_date';
	    		return b.disabled ? a.getValue() : b.getValue();
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
	createVoucherField : function() {
		return Ext.create('Ext.form.FieldContainer', {
			fieldLabel: '凭证号',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .51,
			id: 'vo_number',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				xtype: 'numberfield',
				flex: 1,
				minValue: 0,
				name: 'vo_num',
				id: 'vo_num'
			},{
				xtype: 'numberfield',
				flex: 1,
				minValue: 0,
				name: 'vo_num1',
				id: 'vo_num1'
			}],
			getValue: function() {
				var a = this.down('#vo_num').value,b = this.down('#vo_num1').value;
				if(a && b) {
					return null;
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
			id: 'sl_currency',
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			editable: false,
			store: Ext.create('Ext.data.Store', {
	            fields: ['display', 'value'],
	            data : [
	                {"display": '本位币', "value": '0'}
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
    createVoStatusField : function() {
    	var me = this;
    	return Ext.create('Ext.form.field.ComboBox', {
    		fieldLabel: '凭证状态',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .5,
			id: 'vo_status',
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			editable: false,
			store: Ext.create('Ext.data.Store', {
	            fields: ['display', 'value'],
	            data : [
	                {"display": '全部', "value": '0'},
	                {"display": '已记账', "value": '1'},
	                {"display": '未记账', "value": '2'}
	            ]
	        }),
	        value: '0'
    	});
    },
    createFilterPanel: function(btn) {
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 500,
    		height: 300,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCateLevelField(),me.createCurrencyField(), 
    	            me.createVoStatusField(),{
    					xtype: 'checkbox',
    					id: 'enter',
    					name: 'enter',
    					columnWidth: .5,
    					boxLabel: '显示在录入凭证'
    				},me.createVoucherField()],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt,
						con = me.getCondition(fl);
    				me.getCount(con);
					me.query(con);
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
    	this.getCurrentMonth(filter.down('#sl_yearmonth'), filter.down('#sl_date'));
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
    getCurrentMonth: function(f, con) {
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
    				if(con != null) {
    					con.setMonthValue(rs.data.PD_DETNO);
    				}
    			}
    		}
    	});
    }
});