Ext.QuickTips.init();
Ext.define('erp.controller.ma.SvnLog', {
	extend : 'Ext.app.Controller',
	requires : [ 'erp.util.BaseUtil' ],
	views : [ 'ma.SvnLog', 'core.form.FtDateField' ],
	refs : [ {
		ref : 'date',
		selector : '#date'
	}, {
		ref : 'remark',
		selector : '#remark'
	}, {
		ref : 'grid',
		selector : '#grid'
	}, {
		ref: 'paging',
		selector : '#grid > pagingtoolbar'
	}, {
		ref: 'switch',
		selector : '#switch'
	}, {
		ref: 'unAudit',
		selector : '#unaudit'
	}, {
		ref: 'unTest',
		selector : '#untest'
	}, {
		ref: 'svninfo',
		selector : '#svninfo'
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
				afterrender : function() {
					me.search(0);
				}
			},
			'#switch' : {
				change : function() {
					me.search(0);
				}
			},
			'#unaudit' : {
				change : function() {
					me.search(0);
				}
			},
			'#untest' : {
				change : function() {
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
			'form' : {
				afterrender: function(form) {
					// 系统程序版本信息
					Ext.Ajax.request({
						url: basePath + 'ma/program/version.action',
						callback: function(scope, success, response) {
							if(response.status == 200) {
								var rs = Ext.decode(response.responseText);
								form.down('#svninfo').setValue(rs.active);
								form.down('#svnlast').setValue(rs.newest);
							}
						}
					});
				}
			},
			'menuitem[name=item-changedetail]': {
				click: function(m) {
					me.showDetail(m.ownerCt.record);
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
		var dateF = this.getDate(), 
			remark = this.getRemark().value, 
			onlyNew = this.getSwitch().getValue(),
			onlyUnAudit = this.getUnAudit().getValue(),
			onlyUnTest = this.getUnTest().getValue(),
			version = this.getSvninfo().getValue(),
			filter = {};
		if (dateF.value != null && dateF.value.length > 0) {
			filter.date = {"gted": dateF.firstItem.value.getTime(), "lted": dateF.secondItem.value.getTime() + 86399999};
		}
		if (remark != null && remark.length > 0) {
			filter.remark = remark;
		}
		if (onlyNew && version != null) {
			filter.version = {"gt": version};
		}
		if (onlyUnAudit) {
			filter.auditor = {"eq": null};
		}
		if (onlyUnTest) {
			filter.tester = {"eq": null};
		}
		return Ext.encode(filter);
	},
	toUnicode : function(s) {
		return s.replace(/([\u4E00-\u9FA5]|[\uFE30-\uFFA0])/g, function() {
			return "\\u" + RegExp["$1"].charCodeAt(0).toString(16);
		});
	},
	showDetail : function(record) {
		var me = this, win = me.detailWin;
    	if (!win) {
    		win = this.detailWin = Ext.create('Ext.Window', {
    			title: '详细',
    			height: 300,
    			width: 500,
    			autoScroll: true,
    			closeAction: 'hide',
    			items: [{
    				xtype: 'displayfield',
    				width: '100%'
    			}]
    		});
    	}
    	var changed = record.get('changed').split('\n'), vals = [];
    	for(var i in changed) {
    		vals.push(me.parseSvnLog(changed[i]));
    	}
    	win.down('displayfield').setValue(vals.join('<br>'));
    	win.setTitle(record.get('remark'));
    	win.show();
	},
	parseSvnLog: function(val) {
		var ch = val.substr(0,1);
		switch(ch){
			case 'U':
				ch = '修改';break;
			case 'A':
				ch = '新增';break;
			case 'D':
				ch = '删除';break;
		}
		return ch + val.substr(1);
	}
});