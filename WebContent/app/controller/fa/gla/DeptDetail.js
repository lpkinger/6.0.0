Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.DeptDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.DeptDetail', 'fa.gla.LedgerDeptDetail', 'core.form.MonthDateField', 'core.trigger.CateTreeDbfindTrigger',
            'core.form.ConMonthDateField', 'core.form.ConDateField', 'core.form.YearDateField', 'core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){

    					me.showFilterPanel(btn);
    					me.getCateSetting();
    						var fl = Ext.getCmp('query-filter'),
	        					con = me.getCondition(fl);
	        				con.querytype = 'current';
	        				me.query(con);
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'ledgerdeptdetail': {
    			itemclick: function(selModel, record) {
    				me.loadVoucher(record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('ledgerdept');
    				me.BaseUtil.exportGrid(grid, '部门明细账');
    			}
    		}
    	});
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
    showFilterPanel: function(btn, ym, caCode, dpCode ,  un) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(!filter) {
    		filter = this.createFilterPanel(btn);
    	}
    	filter.show();
    	if(ym && ym != 'undefined') {
    		filter.down('#asl_yearmonth').setValue(ym);
    		filter.hide();
    	}
    	if(caCode && caCode != 'undefined') {
    		filter.down('#ca_code').setValue(caCode);
    		this.catecode = caCode;//当前科目
    	}
    	if(dpCode && dpCode != 'undefined') {
    		filter.down('#dp_code').setValue(dpCode);
    		this.deptcode = dpCode;//当前部门
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
    	console.log(cond);
    	var me = this,
    		grid = Ext.getCmp('ledgerdept');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getDeptDetail.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {

    				grid.store.loadData(res.data);

    			}
    			grid.setLoading(false);
    		}
    	});
    },
    getCondition: function(pl) {
    	console.log(pl);
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
    _updateInfo: function(r) {
       	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	tb = Ext.getCmp('gl_info_c');
    	if(tb)
    		tb.updateInfo(r);
    	tb = Ext.getCmp('gl_info_ass');
    	if(tb)
    		tb.updateInfo(r);
    },
    loadVoucher: function(record) {
    	var me = this, vc = record.get('dp_vocode');
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
	    				var s = Ext.getCmp('asl_yearmonth');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'asl_yearmonth',
				name: 'asl_yearmonth',
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
	    				var s = Ext.getCmp('asl_date');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'asl_date',
				name: 'asl_date',
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
	    		var a = Ext.getCmp('asl_yearmonth'),b = Ext.getCmp('asl_date');
	    		this.id = b.disabled ? 'asl_yearmonth' : 'asl_date';
	    		return b.disabled ? a.getValue() : b.getValue();
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
			id: 'ca_code',
			margin: '2 2 2 10',
	    	columnWidth: 1,
	    	height: 100,
	    	layout: 'column',
	    	items: [{
	    		xtype: 'radio',
	    		boxLabel: '连续科目范围',
	    		columnWidth: 0.3,
	    		checked: true,
	    		name: 'continueornot',
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('con_vd_catecode');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
				labelWidth: 80,
				layout: 'column',
				columnWidth: 0.7,
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
	    	},{
	    		xtype: 'radio',
	    		boxLabel: '非连续科目范围',
	    		columnWidth: 0.3,
	    		name: 'continueornot',
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('un_vd_catecode');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		xtype: 'cateTreeDbfindTrigger',
	    		mode: 'MULTI',
	    		id: 'un_vd_catecode',
				name: 'un_vd_catecode',
				disabled: true,
				columnWidth: 0.7
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('con_vd_catecode'),b = Ext.getCmp('un_vd_catecode');
	    		var val = b.disabled ? a.getValue() : b.value;
	    		if(val) {
	    			if(!b.disabled) {
	    				var arr = val.split('#');
	    				if(me.catecode != null && !Ext.Array.contains(arr, me.catecode)) {
	    					me.catecode = null;
	    				}
	    			} else {
	    				if(me.catecode == null) {
	    					me.catecode = val.begin;
	    				}
	    			}
	    			return b.disabled ? {continuous: true,value: val} : {continuous: false, value: val};
	    		}
	    		return null;
	    	},
	    	setValue: function(v) {
	    		Ext.getCmp('vd_catecode').setValue(v);
	    		Ext.getCmp('vd_catecode1').setValue(v);
	    	}
    	});
    },
    createDeptField : function() {
    	var me = this, t, t1;
		t = Ext.create('erp.view.core.trigger.DbfindTrigger', {
			id: 'vd_deptcode',
			name: 'vd_deptcode',
			columnWidth: 0.4,
			listeners: {
				aftertrigger: function(t, d) {
					
					t.setValue(d.data.dp_code);
					t.ownerCt.down('#vd_deptname').setValue(d.data.dp_name);
				}
			}
		});
		t1 = Ext.create('erp.view.core.trigger.DbfindTrigger', {
			id: 'vd_deptcode1',
			name: 'vd_deptcode1',
			columnWidth: 0.4,
			listeners: {
				aftertrigger: function(t, d) {
					t.setValue(d.data.dp_code);
					t.ownerCt.down('#vd_deptname1').setValue(d.data.dp_name);
				}
			}
		});
    	return Ext.create('Ext.form.FieldContainer', {
			id: 'dp_code',
			margin: '2 2 2 10',
	    	columnWidth: 1,
	    	height: 100,
	    	layout: 'column',
	    	items: [{
	    		xtype: 'label',
	    		text: '部门范围',
	    		columnWidth: 0.3
	    	},{
				labelWidth: 80,
				layout: 'column',
				columnWidth: 0.7,
				height: 56,
				xtype: 'fieldcontainer',
				id: 'con_vd_deptcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [t, {
					xtype: 'textfield',
					name: 'vd_deptname',
					id: 'vd_deptname',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				},t1 ,{
					xtype: 'textfield',
					name: 'vd_deptname1',
					id: 'vd_deptname1',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = this.down('#vd_deptcode').value,b = this.down('#vd_deptcode1').value,
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
	    		var a = Ext.getCmp('con_vd_deptcode');
	    		var val = a.getValue();
	    		if(val) {
    				if(me.deptcode == null) {
    					me.deptcode = val.begin;
    				}
	    			return {continuous: true,value: val};
	    		}
	    		return null;
	    	},
	    	setValue: function(v) {
	    		Ext.getCmp('vd_deptcode').setValue(v);
	    		Ext.getCmp('vd_deptcode1').setValue(v);
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
    		height: 500,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCateField(),
    	            me.createDeptField(),
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
    	this.getCurrentMonth(filter.down('#asl_yearmonth'));
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
    }
});