Ext.QuickTips.init();
Ext.define('erp.controller.common.CheckBase', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.init.CheckBase'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[name=prev]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=2]')[0];
    				bt.fireEvent('click', bt);
    			}
    		},
    		'button[name=check]' : {
    			click: function(b) {
        			var c = 0, form = b.up('form'), bar = form.down('progressbar');
        			var ds = form.query('displayfield');
        			b.setDisabled(true);
        			form.setLoading(true);
        			bar.updateProgress(0);
        			me.errtables = new Array();
        			Ext.each(ds, function(d) {
        				if(d.table) {
        					d.setFieldStyle('');
        					bar.updateText(d.value);
            				Ext.Ajax.request({
            					url: basePath + 'ma/guide/checktab.action',
            					params: {
            						t: d.table
            					},
            					async: false,
            					callback: function (o, s, r) {
            						bar.updateProgress((bar.value*ds.length + 1)/ds.length);
            						if('success' == r.responseText) {
            							d.setFieldStyle('color: blue');
            							c += 1;
            						} else {
            							d.setFieldStyle('color: red');
            							me.errtables.push(d.table);
            						}
            					}
            				});
        				}
        			});
        			form.setLoading(false);
        			b.setDisabled(false);
        			bar.updateText('检测完成 检测项:' + ds.length + ' 通过:' + c + ' 失败:' + (ds.length - c));
        			if(c == ds.length) {
        				form.down('button[name=next]').setDisabled(false);
        				form.down('button[name=repair]').setDisabled(true);
        			} else {
        				form.down('button[name=repair]').setDisabled(false);
        			}
        		}
    		},
    		'button[name=repair]' : {
    			click: function(b) {
    				var form = b.up('form'), tbs = me.errtables.join(',');
    				form.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'ma/guide/repair.action',
    					params: {
    						tbs: tbs
    					},
    					callback: function(o, s, r) {
    						form.setLoading(false);
    						if ('success' == r.responseText) {
    							alert('修复成功');
    							var b = form.down('button[name=check]');
    		    				b.fireEvent('click', b);
    						} else {
    							alert(r.responseText);
    						}
    					}
    				});
    			}
    		},
    		'button[name=next]': {
    			click: function(btn){
    				var bt = parent.Ext.ComponentQuery.query('button[step=4]')[0];
    				bt.fireEvent('click', bt);
    			}
    		}
    	});
    }
});
