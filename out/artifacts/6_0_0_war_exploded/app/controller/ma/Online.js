Ext.QuickTips.init();
Ext.define('erp.controller.ma.Online', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.Online', 'core.grid.HeaderFilter'],
    init:function(){
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	var me = this;
    	this.control({ 
    		'#refresh': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
		    		grid.store.load();
    			}
    		},
    		'#close': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		},
    		'#online-grid': {
    			lockitem: function(x) {
    				var grid = Ext.getCmp('online-grid'), store = grid.store, record = store.getAt(x);
    				if(record) {
    					warnMsg('确定要锁定在IP' + record.get('ip') + '登录的用户:' + record.get('em_code') + ' 吗?', function(btn){
    						if(btn == 'ok' || btn == 'yes') {
    							me.lock(record.get('sid'));
    						}
    					});
    				}
    			}
    		}
    	});
    },
    lock: function(sid) {
    	Ext.Ajax.request({
    		url: basePath + 'ma/user/lock.action',
    		params: {
    			sid: sid
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				alert('锁定成功!');
    			}
    		}
    	});
    }
});