Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.Query', {
	extend : 'Ext.app.Controller',
	views : [ 'common.query.Viewport', 'fa.ars.repQuery.GridPanel', 'common.query.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 
			'core.form.ConMonthDateField' ],
	init : function() {
		this.control({
			'erpQueryFormPanel' : {
				alladded : function(form) {
					var items = form.items.items;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
						}
					});
				}
			},
			'erpQueryGridPanel' : {
				itemclick : this.onGridItemClick
			},
			'monthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'frd_yearmonth') {
						type = 'MONTH-A';
					}
					if(type != '') {
						this.getCurrentMonth(f, type);
					}
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {
		if (caller == 'CustMonth!ARLI!Query') {
			var cmid = record.data['cm_id'];
			if (cmid > 0) {
				var panel = Ext.getCmp(caller + "cm_id" + "=" + cmid);
				var main = parent.Ext.getCmp("content-panel");
				if (!main) {
					main = parent.parent.Ext.getCmp("content-panel");
				}
				if (!panel) {
					var title = "";
					if (value.toString().length > 4) {
						title = value.toString().substring(value.toString().length - 4);
					} else {
						title = value;
					}
					var myurl = '';
					if (me.BaseUtil.contains(url, '?', true)) {
						myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
					} else {
						myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
					}
					myurl += "&datalistId=" + main.getActiveTab().id;
					main.getActiveTab().currentStore = me.getCurrentStore(value);// 用于单据翻页
					panel = {
						title : me.BaseUtil.getActiveTab().title + '(' + title + ')',
						tag : 'iframe',
						tabConfig : {
							tooltip : me.BaseUtil.getActiveTab().tabConfig.tooltip + '(' + keyField + "=" + value + ')'
						},
						frame : true,
						border : false,
						layout : 'fit',
						iconCls : 'x-tree-icon-tab-tab1',
						html : '<iframe id="iframe_maindetail_' + caller + "_" + value + '" src="' + myurl
								+ '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
						closable : true,
						listeners : {
							close : function() {
								if (!main) {
									main = parent.parent.Ext.getCmp("content-panel");
								}
								main.setActiveTab(main.getActiveTab().id);
							}
						}
					};
					this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
				} else {
					main.setActiveTab(panel);
				}

			}
		}
	},
	getCurrentMonth: function(f, type) {
	    Ext.Ajax.request({
	    	url: basePath + 'fa/getMonth.action',
	    	params: {
	    		type: type
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