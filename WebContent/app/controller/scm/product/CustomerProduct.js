Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.CustomerProduct', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : [ 'core.form.Panel', 'core.grid.Panel2', 'scm.product.CustomerProduct', 'core.trigger.DbfindTrigger',
			'core.button.Save', 'core.button.Update', 'core.button.Close', 'core.button.Sync', 'core.button.Add' ],
	refs : [ {
		ref : 'form',
		selector : '#form'
	},{
		ref : 'grid',
		selector : '#grid'
	} ],
	init : function() {
		var me = this;
		me.control({
			'erpGridPanel2': {
    			itemclick: function(sel, item) {
    				me.GridUtil.onGridItemClick(sel, item);
    			},
    			reconfigure: function() {
    				me.setSyncdatas();
    			},
    			storeloaded: function() {
    				me.setSyncdatas();
    			}
    		},
			'field[name=cp_custcode]': {
				afterrender: function(t) {
					var v = getUrlParam('cp_custcode');
					if(!Ext.isEmpty(v)) {
						t.setValue(v);
						(typeof t.autoDbfind === 'function') 
							&& t.autoDbfind('form', caller, 'cp_custcode', 'cu_code=\'' + v + '\'');
						me.loadData(v);
					}
				},
				aftertrigger: function(t) {
					if(t.isDirty())
						me.loadData(t.getValue());
				}
			},
			'erpAddButton': {
				click: function() {
					me.FormUtil.onAdd(null, '客户物料对照', 'jsps/scm/product/custprod.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSaveButton': {
				click: function(btn) {
					if(me.getForm().getForm().isValid()) {
						me.onSave();
					}
				}
			}
		});
	},
	loadData : function(cust) {
		if (!Ext.isEmpty(cust)) {
			var grid = this.getGrid();
			grid.GridUtil.loadNewStore(grid, {
				caller : caller,
				condition : 'cp_custcode=\'' + cust + '\''
			});
		}
	},
	onSave: function() {
		var me = this, arr = new Array(), cust = me.getForm().down('#cp_custcode').getValue();
		me.getGrid().store.each(function(i){
			if(i.dirty) {
				var d = i.data;
				if(!Ext.isEmpty(d.cp_prcode) && !Ext.isEmpty(d.cp_custprcode)) {
					delete d.pr_detail;
					delete d.pr_spec;
					d.cp_custcode = cust;
					arr.push(d);
				}
			}
		});
		if(arr.length > 0) {
			me.FormUtil.setLoading(true);
			Ext.Ajax.request({
				url: basePath + me.getForm().saveUrl,
				params: {
					param: Ext.encode(arr),
					caller: caller
				},
				callback: function(opt, s, res) {
					me.FormUtil.setLoading(false);
					var r = Ext.decode(res.responseText);
					if(r.success) {
						showMessage('提示', '保存成功', 1500);
						window.location.href = basePath + 'jsps/scm/product/custprod.jsp?cp_custcode=' + cust;
					} else if(r.exceptionInfo) {
						showError(r.exceptionInfo);
					}
				} 
			});
		}
	},
	setSyncdatas: function() {
		var btn = this.getForm().down('erpSyncButton');
		if(btn) {
			var arr = [], i;
			this.getGrid().store.each(function(){
				i = this.get('cp_id');
				if(!Ext.isEmpty(i) && Number(i) > 0) {
					arr.push(i);
				}
			});
			btn.syncdatas = arr.join(',');
		}
	}
});