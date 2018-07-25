Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.RemainQuery', {
	extend : 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	views : [ 'fa.gla.RemainQuery', 'common.batchDeal.GridPanel', 'core.trigger.DbfindTrigger',
	          'core.button.Query', 'core.button.Export', 'core.button.Print', 'core.button.Close',
	          'core.form.MonthDateField'],
	init : function() {
		this.control({
			'button[id=query]': {
    			click: function(btn) {
    				this.query();
    			}
    		},
			'button[name=export]': {
				click: function(btn) {
					var grid = btn.ownerCt.ownerCt.ownerCt.down('gridpanel');
					grid.BaseUtil.exportGrid(grid);
				}
			},
			'monthdatefield': {
				afterrender: function(f) {
					this.getCurrentMonth(f);
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
	    			f.setValue(rs.data.PD_DETNO);
	    		}
	    	}
	    });
	},
	query: function() {
    	var grid = Ext.getCmp('batchDealGridPanel'),
    		form = grid.ownerCt.down('form');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gls/getRemainQuery.action',
    		params: {
    			condition: Ext.encode(form.getForm().getValues())
    		},
    		callback: function(opt, s, r) {
    			grid.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				grid.store.loadData(res.data);
    			}
    		}
    	});
    }
});