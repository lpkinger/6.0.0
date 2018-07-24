Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.MakeComplete', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['fa.gla.MakeComplete',  'core.button.Close'],
    init: function(){
    	this.control({
    		'displayfield[name=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'button[id=deal]': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt;
    				this.deal(form);
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
    deal: function(form) {
    	var tab = this.FormUtil.getActiveTab(), 		
    		account = form.down('#account').value;
    	tab.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/makecomplete.action',
    		params: {
    			account: account
    		},
    		timeout: 4800000,
    		callback: function(opt, s, r) {
    			tab.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				if(rs.data) {
    					showMessage('提示', rs.data);
    				} else {
    					alert('结转成功!');
    				}
    			} else if(rs.exceptionInfo) {
    				showMessage('提示', rs.exceptionInfo);
    			}
    		}
    	});
    }
});