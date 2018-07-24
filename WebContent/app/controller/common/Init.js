Ext.QuickTips.init();
Ext.define('erp.controller.common.Init', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.Init', 'common.init.Tree', 'core.button.UpExcel'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'#northbtn > button': {
    			click: function(btn){//切换步骤
    				me.toogle(btn, btn.step);
    			}
    		}
    	});
    },
    toogle: function(btn, step){
    	var c = Ext.ComponentQuery.query('#centerpanel > panel[hidden=false]')[0];
    	if(c){
    		var bt = parent.Ext.ComponentQuery.query('button[step=' + c.step + ']')[0];
    		bt.removeCls('stepon');
    		bt.addCls('stepoff');
    		c.hide();
    	}
    	var p = Ext.ComponentQuery.query('#centerpanel > panel[step=' + step + ']')[0];
    	if(p){
    		btn.removeCls('stepoff');
    		btn.addCls('stepon');
    		p.show();
    		if(!p.loaded){
    			p.getEl().down('iframe').dom.contentWindow.location.href = basePath + p.url;
    			p.loaded = true;
    		}
    	}
    }
});
