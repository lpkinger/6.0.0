Ext.QuickTips.init();
Ext.define('erp.controller.ma.SysUpdate', {
	extend : 'Ext.app.Controller',
	requires : [ 'erp.util.BaseUtil' ],
	views : [ 'ma.SysUpdate', 'core.form.FtDateField' ],
	refs : [ {
		ref : 'date',
		selector : '#date'
	}, {
		ref : 'title',
		selector : '#title'
	}, {
		ref : 'grid',
		selector : '#grid'
	}, {
		ref: 'paging',
		selector : '#grid > pagingtoolbar'
	}],
	init : function() {
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		var me = this;
		this.control({
			'#refresh' : {
				click : function(btn) {
					if (me.getPaging().fireEvent('beforechange', me.getPaging(), 1) !== false){
						me.search(0);
			        }
				}
			},
			'#close' : {
				click : function() {
					this.BaseUtil.getActiveTab().close();
				}
			},
			'#date' : {
				afterrender : function(field) {
					field.setValues(me.getDateArea('m', -1), me.getDateArea('d', 0));
				}
			},
			'#grid' : {
				afterrender : function(grid) {
					grid.store.on('load', function(store, datas){
						me.getUpdateLog(store);
					});
					me.search(0);
				}
			},
			'button' : {
				click : function(btn) {
					if (btn.param) {
						var a = btn.param[0], b = btn.param[2], m = btn.param[1], n = btn.param[3];
						me.getDate().setValues(me.getDateArea(a, m), me.getDateArea(b, n));
						if (me.getPaging().fireEvent('beforechange', me.getPaging(), 1) !== false){
							me.search(0);
				        }
					}
				}
			},
			'menuitem[name=item-find]': {
				click: function(m) {
					me.showPlan(m.ownerCt.record);
				}
			},
			'menuitem[name=item-cover]': {
				click: function(m) {
					me.upgrade(m.ownerCt.record, 'COVER');
				}
			},
			'menuitem[name=item-repair]': {
				click: function(m) {
					me.upgrade(m.ownerCt.record, 'REPAIR');
				}
			}
		});
	},
	getDateArea : function(type, val) {
		var today = new Date(), year = today.getFullYear(), month = today.getMonth(), day = today.getDate();
		if (type == 'd') {
			return new Date(year, month, day + val);
		} else if (type == 'm') {
			return new Date(year, month + val, val == 0 ? 1 : day);
		} else if (type == 'y') {
			return new Date(year + val, val == 0 ? 0 : month, val == 0 ? 1 : day);
		}
	},
	search : function(start) {
		var me = this, store = me.getGrid().store, 
			condition = me.toUnicode(me.getCondition());
		me.getGrid().condition  = condition;
		store.load({
			params : {
				start : start,
				limit : store.pageSize,
				condition : condition
			}
		});
	},
	getCondition : function() {
		var dateF = this.getDate(),  title = this.getTitle().value, filter = {};
		if (dateF.value != null && dateF.value.length > 0) {
			filter.createDate = {"gted": dateF.firstItem.value.getTime(), "lted": dateF.secondItem.value.getTime() + 86399999};
		}
		if (title != null && title.length > 0) {
			filter.title = title;
		}
		return Ext.encode(filter);
	},
	toUnicode : function(s) {
		return s.replace(/([\u4E00-\u9FA5]|[\uFE30-\uFFA0])/g, function() {
			return "\\u" + RegExp["$1"].charCodeAt(0).toString(16);
		});
	},
	getUpdateLog: function(store) {
		var plans = [];
		store.each(function(item){
			plans.push(item.get('id'));
		});
		Ext.Ajax.request({
			url: basePath + 'ma/upgrade/log.action',
			params: {
				planIds: plans.join(',')
			},
			callback: function(opts, success, response) {
				if(success) {
					var res = Ext.decode(response.responseText);
					Ext.each(res, function(d){
						var item = store.findRecord('id', d.plan_id);
						if(item) {
							item.set('install', true);
							item.set('install_version', d.version);
							item.set('install_date', d.install_date);
						}
					});
				}
			}
		});
	},
	upgrade: function(item, type) {
		var me = this;
		me.getGrid().setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'ma/upgrade.action',
			params: {
				planId: item.get('id'),
				type: type || item.get('installType'),
				version: item.get('version')
			},
			timeout: 300000,
			callback: function(opts, success, response) {
				me.getGrid().setLoading(false);
				if(success && response.responseText == 'true') {
					showMessage('恭喜', '升级成功！');
					me.search(0);
				} else {
					showError('升级失败！');
				}			
			}
		});
	},
	showPlan: function(item) {
		
	}
});