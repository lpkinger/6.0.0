Ext.QuickTips.init();
Ext.define('erp.controller.common.Master', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.Master'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=prev]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=1]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[id=next]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=3]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[id=confirm]': {
    			click: function(btn){
    				var form = btn.ownerCt.ownerCt;
    				me.login(form);
    			}
    		},
    		'combo[name=ma_name]': {
    			afterrender: function(f){
    				me.getMasterNames(f);
    			}
    		}
    	});
    },
    getMasterNames: function(f){
    	Ext.Ajax.request({
    		url: basePath + 'system/getMasters.action',
    		method: 'get',
    		callback: function(options, success, response){
    			var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		} else if(res.success) {
        			var datas = new Array();
        			Ext.each(res.masters, function(m){
        				datas.push({
        					display: m,
        					value: m
        				});
        			});
        			f.store.loadData(datas);
        			if(datas.length > 0){
        				f.select(f.store.first().get('display'));
        			}
        		}
    		}
    	});
    },
    login: function(form){
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'common/login.action',
    		method: 'post',
    		params: {
    			sob: form.down('#ma_name').value,
    			username: form.down('#em_code').value,
    			password: form.down('#em_password').value
    		},
    		callback: function(o, s, r){
    			form.setLoading(false);
    			var res = new Ext.decode(r.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);
        		} else if(res.success) {
        			var b = form.down('button[id=next]');
        			b.setDisabled(false);
    				b.fireEvent('click', b);
        		} else if(res.reason) {
    				showError(res.reason);
    			} else{
        			alert('系统错误!');
        		}
    		}
    	});
    }
});
