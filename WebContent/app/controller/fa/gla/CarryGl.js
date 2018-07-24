Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.CarryGl', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['fa.gla.CarryGl', 'core.trigger.CateTreeDbfindTrigger', 'core.button.Close'],
    init: function(){
    	this.control({
    		'displayfield[name=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'cateTreeDbfindTrigger[name=ca_code]': {
    			afterrender: function(f) {
    				this.getDefaultCatecode(f);
    			}
    		},
    		'button[id=deal]': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt,
    					ym = form.down('#yearmonth').value,
    					cacode = form.down('#ca_code').value,
    					account = form.down('#account').value;
    				this.deal(form, ym, cacode, account);
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
    getDefaultCatecode: function(f) {
    	this.BaseUtil.getSetting('carryGl', 'broughtCatecode', function(v){
    		if(v)
    			f.setValue(v);
    	});
    },
    deal: function(form, ym ,ca_code, account) {
    	var tab = this.FormUtil.getActiveTab();
    	tab.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/carrygl.action',
    		params: {
    			yearmonth: ym,
    			ca_code: ca_code,
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
    					alert('结转损益成功!');
    				}
    			}
    		}
    	});
    }
});