Ext.QuickTips.init();
Ext.define('erp.controller.common.AfterInit', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.AfterInit'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[name=prev]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=4]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[name=confirm]': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt, fields = form.getForm().getFields();
    				var c = 0;
    				form.setLoading(true);
    				fields.each(function(f){
    					Ext.Ajax.request({
							url: basePath + f.action,
							callback: function(o, s, r) {
								if (s) {
									if(++c == fields.length) {
										form.setLoading(false);
										alert('完成操作!');
									}
								}
							}
						});
    				});
    			}
    		}
    	});
    },
    
});
