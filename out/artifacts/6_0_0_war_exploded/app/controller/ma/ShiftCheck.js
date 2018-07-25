Ext.QuickTips.init();
Ext.define('erp.controller.ma.ShiftCheck', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.ShiftCheck'],
    init:function(){
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=check]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				this.check(grid, 0, btn);
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		}
    	});
    },
    check: function(grid, idx, btn) {
    	var me =this,f = grid.store.getAt(idx);
    	if(!f) {
    		btn.setDisabled(false);
    		return;
    	}
    	f.set('check', 'loading');
    	var win = Ext.getCmp('win-' + f.get('type'));
    	if(win) {
    		win.destroy();
		}
    	Ext.Ajax.request({
    		url: basePath + f.get('action'),
    		params: {
    			type: f.get('type')
    		},
    		callback: function(opt, s, r) {
    			me.check(grid, ++idx, btn);
    			var rs = Ext.decode(r.responseText);
    			if(rs.ok) {
    				f.set('check', 'checked');
    			} else {
    				f.set('check', 'error');
    			}
    		}
    	});
    }
});