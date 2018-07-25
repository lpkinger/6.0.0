Ext.QuickTips.init();
Ext.define('erp.controller.ma.jprocess.RefreshSet', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.jprocess.RefreshSet'],
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
	setLoading : function(b) {
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "系统正在为您保存,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
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
    	if (r.index==3)me.setLoading(true);
    	r.set('check', 'loading');
    	var actions=r.get('action').split('#');
    	Ext.Array.each(actions,function(action){
    		Ext.Ajax.request({
    			url: basePath + action,
    			method: 'GET',
    			timeout: 600000,
    			callback: function(opt, s, re) {  
    				r.set('check', 'checked');
    				r.set('detail', null);
    				if (r.index==3) me.setLoading(false);
    				grid.toggleRow(r);
    				var rs = Ext.decode(re.responseText); 
    				if(rs.error) {
    					r.set('check', 'error');
    				}   				
    				if(rs.result) {
    					r.set('detail', rs.result);
    				}
    				if(rs.exceptionInfo) {//批量保存流程，异常信息处理
    					r.set('check', 'error');
    					r.set('detail', rs.exceptionInfo);
    				}
    				if(Ext.isNumber(idx) && idx!=2 && idx!=3 && idx!=4) {
    					me.check(grid, ++idx, btn);
    				} else {
    					btn.setDisabled(false);
    				}
    			}
        	});
    	});
    	
    }
});