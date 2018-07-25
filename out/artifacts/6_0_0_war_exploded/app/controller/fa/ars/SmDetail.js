Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.SmQuery', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    GridUtil: Ext.create('erp.util.GridUtil'),
    condition:'',
    views:[
     		'fa.ars.SmQuery', 'core.grid.Panel2', 'core.trigger.DbfindTrigger','core.form.FtField','core.form.ConDateField','core.form.YnField',
     		'core.form.FtDateField','core.form.FtFindField','core.grid.YnColumn','core.grid.TfColumn','core.form.ConMonthDateField'
    ],
	refs : [ {
		selector : 'tbtext[name=info]',
		ref : 'info'
	} , {
		selector : '#grid',
		ref : 'grid'
	}],
    init:function(){
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'#grid': {
    			itemclick: function() {
    				
    			}
    		},
    		'button[name=query]' : {
    			delay: 200,
    			afterrender : function(btn) {
    				me.createFilterPanel(btn).show();
    			},
    			click: function() {
    				var f = me.filter;
    				if(f) {
    					f[f.hidden ? 'show' : 'hide'].call(f);
    				}
    			}
    		},
    		'button[name=export]' : {
    			click : function() {
    				this.BaseUtil.exportGrid(this.getGrid());
    			}
    		},
    		'button[name=print]' : {
    			click : function() {
    				
    			}
    		},
    		'button[name=refresh]' : {
    			click: function(btn){
    				me.getGrid().setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'fa/ars/CmQueryController/refreshCmQuery.action',
    					method: 'GET',
    					callback: function(opt, s, r) {
    						me.getGrid().setLoading(false);
    						me.query(me.getCondition());
    					}
    				});
    			}
    		}
    	});
    },
    createYearmonthField: function() {
    	var me = this;
    	return Ext.create('erp.view.core.form.ConMonthDateField', {
    		id: 'cm_yearmonth',
			name: 'cm_yearmonth',
			fieldLabel: '期间',
			labelWidth: 80,
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
    	});
    },
    createCurrencyField: function() {
    	return Ext.create('erp.view.core.trigger.DbfindTrigger', {
			fieldLabel: '币别',
			labelWidth: 80,
			id: 'cm_currency',
			name:'cm_currency'
    	});
    },
    createCustomerField: function() {
    	return Ext.create('Ext.form.FieldContainer', {
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
    	});
    },
    createDeptField: function() {
    	return Ext.create('Ext.form.FieldContainer', {
    		fieldLabel: '部门',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: 1,
			xtype: 'fieldcontainer',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			id: 'em_depart',
			items: [{
				labelWidth: 35,
				xtype: 'dbfindtrigger',
				flex: 0.32,
				id: 'dp_code',
				name: 'dp_code'
			},{
				xtype: 'textfield',
				id: 'dp_name',
				name: 'dp_name',
				flex:0.32,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = Ext.getCmp('dp_code');
				if(!Ext.isEmpty(a.value)) {
					return a.value;
				}
				return null;
			}
    	});
    },
    createSellerField: function() {
    	return Ext.create('Ext.form.FieldContainer', {
    		fieldLabel: '业务员编码',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: 1,
			xtype: 'fieldcontainer',
			id: 'cmq_sellercode',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				labelWidth: 35,
				xtype: 'dbfindtrigger',
				flex: 0.32,
				id: 'sa_sellercode',
				name: 'sa_sellercode'
			},{
				xtype: 'textfield',
				id: 'sa_seller',
				name: 'sa_seller',
				flex:0.32,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = Ext.getCmp('sa_sellercode');
				if(!Ext.isEmpty(a.value)) {
					return {sa_sellercode: a.value};
				}
				return null;
			}
    	});
    },
    createFilterPanel:function(btn){
    	var me = this;
    	me.filter = Ext.create('Ext.window.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		closeAction: 'hide',
    		width: 500,
    		height: 385,
    	    layout: 'column',
    	    defaults: {
    	    	columnWidth: .51,
    			margin: '10 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCurrencyField(), me.createCustomerField(),
    	            me.createDeptField(), me.createSellerField(), {
				xtype: 'checkbox',
				id: 'chkumio',
				boxLabel: '包含未开票未转发出商品出货'
			},{
				xtype: 'checkbox',
				id: 'chkzerobalance',
				boxLabel: '余额为零的不显示'
			},{
				xtype: 'checkbox',
				id: 'chknoamount',
				boxLabel: '无发生额的不显示'
			},{
				xtype: 'checkbox',
				id: 'chkstatis',
				checked: true,
				boxLabel: '是否显示汇总数'
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var w = btn.ownerCt.ownerCt;
					me.query(me.getCondition(w));
					w.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(b) {
	    			b.ownerCt.ownerCt.hide();
	    		}
	    	}]
    	});
		return me.filter;
    
    },
    getCondition: function(w) {
    	w = w || this.filter;
    	var r = new Object(),v;
    	Ext.each(w.items.items, function(item){
    		if(typeof item.getValue === 'function') {
    			v = item.getValue();
        		if(!Ext.isEmpty(v)) {
        			r[item.id] = v;
        		}
    		}
    	});
    	this.getInfo().update(r);
    	return r;
    },
    query: function(cond) {
    	var grid = this.getGrid();
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/CmQueryController/getSmQuery.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
        	callback : function(o, s, r){
        		grid.setLoading(false);
        		var rs = new Ext.decode(r.responseText);
        		if(rs.exceptionInfo){
        			showError(rs.exceptionInfo);return;
        		}
        		if (rs.data) {
        			grid.store.loadData(rs.data);
        		}
        	}
    	});
    },
	getCurrentYearmonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-C'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
	}
});