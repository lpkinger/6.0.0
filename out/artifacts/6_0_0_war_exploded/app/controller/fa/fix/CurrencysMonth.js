Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.CurrencysMonth', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	selectid : '',
	selectmf : Number(Ext.Date.format(new Date(), 'Ym')),
	views : [ 'fa.fix.CurrencysMonthGrid', 'core.button.Save', 'core.form.MonthDateField',
			'core.trigger.DbfindTrigger', 'core.button.Delete', 'core.button.Close', 'core.button.Sync' ],
	init : function() {
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'button[name=getcrrate]' : {
				click: function (btn){
					
					var grid = Ext.getCmp('grid');
					console.log(grid);
					var items = grid.store.data.items;
					Ext.each(items,function(item,index){
						item.set("cm_endrate",item.data.cm_crrate);
						
						
					});
					
				}
			},
			'currencysMonthGrid' : {
				itemclick : this.GridUtil.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					this.beforeUpdate();
				}
			},
			'button[id=searchCurrency]' : {
				click : function(btn) {
					this.searchCurrency(btn);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					btn.setDisabled(true);
				},
				click : function(btn) {
					this.deleteRe();
				}
			},
			'monthdatefield' : {
				afterrender: function(f) {
					me.getCurrentMonth(function(data){
						var ym = data ? data.PD_DETNO : Ext.Date.format(new Date(), 'Ym');
						f.setValue(ym);
					});
					Ext.defer(function(){
						var btn = f.ownerCt.down('erpSyncButton');
						btn && (btn.syncdatas = f.getValue());
					}, 100);
				},
				change : function(f) {
					me.selectmf = f.getValue();
					var btn = f.ownerCt.down('erpSyncButton');
					btn && (btn.syncdatas = f.getValue());
					me.searchCurrency();
				}
			},
			'button[name=getlastend]' : {
				click : function(b) {
					var v = b.ownerCt.down('#monthfield').getValue(), d = Ext.Date.parse(v + '01', 'Ymd'), y = d
							.getFullYear(), m = d.getMonth(), e = Ext.Date.format(new Date(y, m - 1, 1), 'Ym');
					warnMsg('确定将上个月(' + e + ')末的汇率设置成本月(' + v + ')的月初汇率?', function(k) {
						if (k == 'ok' || k == 'yes') {
							me.getLastEnd(v, e, b.ownerCt.ownerCt);
						}
					});
				}
			}
		});
	},
	getCurrentMonth: function(callback) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				callback.call(null, rs.data);
    			}
    		}
    	});
    },
	deleteRe : function(btn) {
		var me = this;
		var grid = Ext.getCmp('grid');
		Ext.Ajax.request({
			url : basePath + 'fa/fix/CurrencysController/deleteCurrencysMonth.action',
			params : {
				id : me.selectid
			},
			method : 'post',
			callback : function(options, success, response) {
				me.getActiveTab().setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					delSuccess(function() {
						// add成功后刷新页面进入可编辑的页面
						var gridCondition = 'cm_yearmonth=' + me.selectmf;
						var gridParam = {
							caller : caller,
							condition : gridCondition
						};
						console.log(caller+":"+gridCondition);
						me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
					});
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						delSuccess(function() {
							// add成功后刷新页面进入可编辑的页面
							var gridCondition = ' 1=1 and cm_yearmonth=' + me.selectmf;
							var gridParam = {
								caller : caller,
								condition : gridCondition
							};							
							me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
						});
						showError(str);
					} else {
						showError(str);
						return;
					}
				} else {
					saveFailure();// @i18n/i18n.js
				}
			}
		});
	},
	searchCurrency : function(btn) {
		var me = this;
		Ext.getCmp('deletebutton').setDisabled(true);
		var gridParam = {
			caller : caller,
			condition : 'cm_yearmonth=' + me.selectmf
		};
		var grid = Ext.getCmp('grid');
		me.GridUtil.loadNewStore(grid, gridParam);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	beforeSave : function() {
		this.FormUtil.beforeSave(this);
	},
	beforeUpdate : function() {
		var grid = Ext.getCmp('grid');
		var me = this;
		var isnumber = true;
		Ext.each(grid.store.data.items, function(item, index) {
			if (!Ext.isEmpty(item.get('cm_crname'))) {
				if (Ext.Number.from(item.data['cm_crrate'], -1) < 0)
					isnumber = false;
				return;
				if (Ext.Number.from(item.data['cm_endrate'], -1) < 0)
					isnumber = false;
				return;
			}
		});
		if (!isnumber) {
			showError($I18N.common.msg.failure_isnotnumber);
			return;
		}

		var params = me.GridUtil.getAllGridStore(grid);
         console.log(params);
		params = params == null ? [] : "[" + params.toString().replace(/\\/g, "%") + "]";

		me.update(params);

	},
	getActiveTab : function() {
		var tab = null;
		if (Ext.getCmp("content-panel")) {
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if (!tab && parent.Ext.getCmp("content-panel"))
			tab = parent.Ext.getCmp("content-panel").getActiveTab();
		if (!tab && parent.parent.Ext.getCmp("content-panel"))
			tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
		if (!tab) {
			var win = parent.Ext.ComponentQuery.query('window');
			if (win.length > 0) {
				tab = win[win.length - 1];
			}
		}
		return tab;
	},
	update : function(param) {
		var me = this;
		var mf = Ext.getCmp('monthfield') ? Ext.getCmp('monthfield').getValue() : Number(Ext.Date.format(new Date(),
				'Ym'));
		me.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
			url : basePath + 'fa/fix/CurrencysController/updateCurrencysMonth.action',
			params : {
				param : param,
				mf : mf
			},
			method : 'post',
			callback : function(options, success, response) {
				me.getActiveTab().setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					saveSuccess(function() {
						// add成功后刷新页面进入可编辑的页面
						var grid = Ext.getCmp('grid');
						var gridCondition = ' 1=1 and cm_yearmonth=' + me.selectmf;
						var gridParam = {
							caller : caller,
							condition : gridCondition
						};
						me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
					});
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						saveSuccess(function() {
							var gridCondition = ' 1=1 and cm_yearmonth=' + me.selectmf;
							var gridParam = {
								caller : caller,
								condition : gridCondition
							};
							var grid = Ext.getCmp('grid');
							me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
						});
						showError(str);
					} else {
						showError(str);
						return;
					}
				} else {
					saveFailure();// @i18n/i18n.js
				}
			}

		});
	},
	getLastEnd : function(y, e, grid) {
		Ext.Ajax.request({
			url : basePath + 'fa/fix/lastrate.action',
			params : {
				yearmonth : y,
				last : e
			},
			callback : function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if (r.success && r.data) {
					grid.store.each(function(record){
						if(Ext.isEmpty(record.get('cm_crname')))
							grid.store.remove(record);
					});
					var first = grid.store.first(), isEmpty = first && Ext.isEmpty(first.get('cm_crname'));
					if (isEmpty) {
						Ext.Array.each(r.data, function(d) {
							d.cm_crrate = d.cm_endrate;
							d.cm_endrate = 0;
						});
						grid.store.loadData(r.data);
					} else {
						Ext.Array.each(r.data, function(d) {
							var record = grid.store.findRecord('cm_crname', d.cm_crname);
							if (record) {
								record.set('cm_crrate', d.cm_endrate);
							} else {
								d.cm_crrate = d.cm_endrate;
								d.cm_endrate = 0;
								grid.store.add(d);
							}
						});
					}
				}
			}
		});
	}
});