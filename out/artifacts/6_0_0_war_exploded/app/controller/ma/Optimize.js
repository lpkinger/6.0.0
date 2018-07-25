Ext.QuickTips.init();
Ext.define('erp.controller.ma.Optimize', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.Optimize'],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=check]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'gridpanel[id=check-grid]': {
    			afterrender: function(grid) {
    				grid.check = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.check(grid, record, btn);
    				};
    			}
    		}
    	});
    },
    check: function(grid, idx, btn) {
    	var me = this, r;
    	if(Ext.isNumber(idx)) {
    		r = grid.store.getAt(idx);
    	} else {
    		r = idx;
    	}
    	if(!r) {
    		btn.setDisabled(false);
    		return;
    	}
    	r.set('check', 'loading');
    	Ext.Ajax.request({
			url: basePath + r.get('action'),
			method: 'GET',
			callback: function(opt, s, re) {
				r.set('check', 'checked');
				grid.toggleRow(r);
				var rs = Ext.decode(re.responseText);
				if(rs.error) {
					r.set('check', 'error');
				}
				if(rs.result) {
					r.set('detail', rs.result);
				}
				if(Ext.isNumber(idx)) {
					me.check(grid, ++idx, btn);
				} else {
					btn.setDisabled(false);
				}
			}
    	});
    }
});