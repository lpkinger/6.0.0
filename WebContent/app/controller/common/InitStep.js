Ext.QuickTips.init();
Ext.define('erp.controller.common.InitStep', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.InitStep'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[name=prev]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=3]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[name=next]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=5]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[name=refresh]': {
    			click: function(btn){
    				var grids = btn.ownerCt.ownerCt.query('grid');
    				Ext.each(grids, function(){
    					me.getDataCount(this.store);
    				});
    			}
    		},
    		'grid': {
    			afterrender: function(g) {
    				setTimeout(function(){
    					me.getDataCount(g.store);
    				}, 1000);
    			}
    		}
    	});
    },
    getDataCount: function(store, idx) {
    	idx = idx || 0;
    	var me = this, record = store.getAt(idx);
    	if( !record ) return;
    	if(!Ext.isEmpty(record.get('table'))) {
    		record.set('count', 'loading');
    		Ext.Ajax.request({
        		url: basePath + 'ma/guide/count.action',
        		params: {
        			t : record.get('table'),
        			c : record.get('cond')
        		},
        		callback: function(o, s, r) {
        			me.getDataCount(store, ++idx);
        			if( s ) {
        				var e = r.responseText;
        				if ( /^-?[0-9]+(.[0-9]+)?/.test(e) ) {
        					record.set('count', e);
        				}
        			}
        		}
        	});
    	} else {
    		me.getDataCount(store, ++idx);
    	}
    }
});
