Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.ArDayQuery', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gs.ArDayQuery', 'fa.gs.ArDayDetail', 'core.form.MonthDateField', 'core.trigger.CateTreeDbfindTrigger',
            'core.form.ConMonthDateField', 'core.form.ConDateField', 'core.form.YearDateField', 'core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn){
    				me.showFilterPanel(btn);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'ardaydetail': {
    			itemclick: function(selModel, record) {
    				me.loadAccountRegister(record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('ardaydetail');
    				me.BaseUtil.exportGrid(grid, '银行现金日记账');
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
    	var me = this,
    		grid = Ext.getCmp('ardaydetail');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/ArQueryController/getArDayDetail.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				grid.store.loadData(res.data);
    				if(res.data.length > 0) {
    					for(var i in res.data) {
							if (res.data[i].ca_code) {
								cond.catecode = me.catecode = res.data[i].ca_code;
	        					cond.catename = res.data[i].ca_name;
	        					break;
							}
						}
    				} else {
    					cond.catecode = me.catecode;
    					cond.catename = '<font color=red>(无数据)</font>';
    				}
					me._updateInfo(cond);
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
    _updateInfo: function(r) {
       	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	tb = Ext.getCmp('gl_info_c');
    	if(tb)
    		tb.updateInfo(r);
    },
    loadAccountRegister: function(record) {
    	var me = this, vc = record.get('ar_code');
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'AccountRegister',
	   			field: 'ar_id',
	   			condition: 'ar_code=\'' + vc + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return null;
	   			}
    			if(rs.success){
    				if(rs.data != null){
    					me.BaseUtil.onAdd('AccountRegister_' + vc, '银行登记', 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank&formCondition=ar_idIS' + rs.data +
    			    			'&gridCondition=ard_aridIS' + rs.data);
    				}
	   			}
	   		}
		});
    },
    createFilterPanel: function(btn) {
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 530,
    		height: 250,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '10 2 2 10'
    	    },
    	    items: [{
    	    	xtype: 'monthdatefield',
				fieldLabel: '期间',
				height: 23,
				labelWidth: 80,
				id: 'am_yearmonth',
				name:'am_yearmonth',
				flex: 0.2,
				columnWidth: .51,
				hidden: true
    	    },{
    	    	xtype: 'condatefield',
				fieldLabel: '日期',
				height: 23,
				labelWidth: 80,
				id: 'am_date',
				name:'am_date',
				flex: 0.2,
				columnWidth: 1,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: Ext.Date.toString(this.firstVal), end: Ext.Date.toString(this.secondVal)};
					}
					return null;
				}
    	    },{
				fieldLabel: '账户编号',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'am_accountcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.35,
					id: 'ca_code',
					name: 'ca_code'
				},{
					xtype: 'textfield',
					id: 'ca_description',
					name: 'ca_description',
					flex:0.65,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('ca_code'), b = Ext.getCmp('ca_description');
					if(!Ext.isEmpty(a.value)) {
						return {ca_code: a.value, ca_name: b.value};
					}
					return null;
				}
			},{
				xtype: 'dbfindtrigger',
				fieldLabel: '币别',
				height: 23,
				labelWidth: 80,
				id: 'ca_currency',
				name:'ca_currency',
				flex: 0.2,
				columnWidth: .51
			}],
    	    buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
	    			var fl = Ext.getCmp('query-filter'),
						con = me.getCondition(fl);
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
    	this.getCurrentMonth(filter.down('#am_yearmonth'));
		return filter;
    },
    getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-B'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				var month=String(rs.data.PD_DETNO).substring(0,6);
    				f.setValue(month);
    				Ext.getCmp('am_date').setMonthValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    }
});