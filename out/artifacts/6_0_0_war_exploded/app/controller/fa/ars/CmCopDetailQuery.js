Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.CmCopDetailQuery', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.ars.CmCopDetailQuery', 'fa.ars.QueryGrid', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField'],
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
    				var grid = Ext.getCmp('copquerygrid');
    				me.BaseUtil.exportGrid(grid, '应收明细账');
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
    	var grid = Ext.getCmp('copquerygrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/CmQueryController/getCmCopDetailQuery.action',
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
    		cr = record.get('cmc_currency');
    	this.BaseUtil.onAdd('GLDetail_' + ym + '_' + caCode, '明细账', 
    			'jsps/fa/gla/glDetail.jsp?y=' + ym + '&c=' + caCode + '&cr=' + cr);
    },
    _getCaCode: function(store, record) {
    	var c = record.get('ca_code');
    	if(Ext.isEmpty(c)) {
    		return this._getCaCode(store, store.getAt(store.indexOf(record) - 1));
    	}
    	return c;
    },
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'fa/ars/getCurrentYearmonth.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
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
    		width: 500,
    		height: 385,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    //期间 从…到…/币别/客户/单据类型(全部/发票/其它应收单/出货单/收款单/退款单/冲账单)/包含已出货未开票/包含未记账发票/余额为零不显示/无发生额不显示
    	    items: [{
				id: 'cm_yearmonth',
				name: 'cm_yearmonth',
				xtype: 'conmonthdatefield',
				fieldLabel: '期间',
				labelWidth: 80,
				margin: '10 2 2 10',
				columnWidth: .51,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				},
				listeners:{
					afterrender:function(cmd){
						me.getCurrentYearmonth(cmd);
					}
				}
			},{

				xtype: 'dbfindtrigger',
				fieldLabel: '币别',
				height: 23,
				labelWidth: 80,
				id: 'cm_currency',
				name:'cm_currency',
//				margin: '10 2 2 10',
				flex: 0.2,
				columnWidth: .51
				
			},{

				fieldLabel: '客户编码',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'cmq_custcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.32,
					id: 'cm_custcode',
					name: 'cm_custcode'
				},{
					xtype: 'textfield',
					id: 'cm_custname',
					name: 'cm_custname',
					flex:0.32,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('cm_custcode');
					if(!Ext.isEmpty(a.value)) {
						return {cm_custcode: a.value};
					}
					return null;
				}
			
			},{
				xtype: 'combo',
//				flex: 1,
				editable: false,
				labelWidth: 80,
				name: 'tb_source',
				id: 'tb_source',
				fieldLabel: '单据类型',
				displayField: 'display',
				valueField: 'value',
				queryMode: 'local',
				value: "all",
				columnWidth: .51,
				store: Ext.create('Ext.data.Store', {
		            fields: ['display', 'value'],
		            data : [
		                {"display": "全部", "value": "all"},
		                {"display": "发票", "value": "arbill"},
		                {"display": "其它应收单", "value": "other"},
		                {"display": "出货单", "value": "inout"},
		                {"display": "收款单", "value": "recb"},
		                {"display": "退款单", "value": "recbr"},
		                {"display": "冲账单", "value": "cmb"},
		                {"display": "预收款", "value": "prer"}
		            ]
		        })
			},{
				xtype: 'checkbox',
				id: 'chknoturn',
				name: 'chknoturn',
				columnWidth: .51,
				boxLabel: '包括已出货未开票信息'
			},{
				xtype: 'checkbox',
				id: 'chknopost',
				name: 'chknopost',
				columnWidth: .51,
				boxLabel: '包含未记账发票'
			},{
				xtype: 'checkbox',
				id: 'chkzerobalance',
				name: 'chkzerobalance',
				columnWidth: .51,
				boxLabel: '余额为零的不显示'
			},{
				xtype: 'checkbox',
				id: 'chknoamount',
				name: 'chknoamount',
				columnWidth: .51,
				boxLabel: '无发生额的不显示'
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
		return filter;
    }
});