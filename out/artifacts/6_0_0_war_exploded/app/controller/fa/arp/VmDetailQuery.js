Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.VmDetailQuery', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.arp.VmDetailQuery', 'fa.arp.QueryGrid', 'core.form.MonthDateField', 
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
    				var grid = Ext.getCmp('querygrid');
    				me.BaseUtil.exportGrid(grid, '应付明细账');
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
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'fa/arp/getCurrentYearmonth.action',
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
    hideFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(filter) {
    		filter.hide();
    	}
    },
    query: function(cond) {
    	var grid = Ext.getCmp('querygrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/arp/VmQueryController/getVmDetailQuery.action',
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
    	
    },
    _getCaCode: function(store, record) {
    	var c = record.get('ca_code');
    	if(Ext.isEmpty(c)) {
    		return this._getCaCode(store, store.getAt(store.indexOf(record) - 1));
    	}
    	return c;
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
    	    items: [{
				id: 'vm_yearmonth',
				name: 'vm_yearmonth',
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
				id: 'vm_currency',
				name:'vm_currency',
				flex: 0.2,
				columnWidth: .51
			},{
				fieldLabel: '供应商号',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'vm_vendcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.32,
					id: 'pu_vendcode',
					name: 'pu_vendcode'
				},{
					xtype: 'textfield',
					id: 'pu_vendname',
					name: 'pu_vendname',
					flex:0.32,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('pu_vendcode');
					if(!Ext.isEmpty(a.value)) {
						return a.value;
					}
					return null;
				}
			
			},{
				xtype: 'combo',
				editable: false,
				labelWidth: 80,
				name: 'asl_source',
				id: 'asl_source',
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
		                {"display": "其它应付单", "value": "other"},
		                {"display": "验收单", "value": "inout"},
		                {"display": "付款单", "value": "recb"},
		                {"display": "退款单", "value": "recbr"},
		                {"display": "冲账单", "value": "cmb"}
		            ]
		        })
			},{
				xtype: 'checkbox',
				id: 'chknoturn',
				name: 'chknoturn',
				columnWidth: .51,
				boxLabel: '包括已收货未开票信息'
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