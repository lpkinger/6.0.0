Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.ExchangeGl', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['fa.gla.ExchangeGl', 'core.trigger.CateTreeDbfindTrigger', 'core.button.Close'],
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
    					account = form.down('#account').value,
    					grid = form.ownerCt.down('gridpanel');
    				var dd = new Array(), err = new Array();
    				grid.store.each(function(){
    					dd.push(this.data);
    					if(this.get('cm_endrate') == 0) {
    						err.push(this.get('cm_crname'));
    					}
    				});
    				if(err.length > 0) {
    					showError('汇率不能为空,币别:' + err.join(','));
    					return;
    				}
    				this.deal(form, grid, ym, cacode, account, Ext.encode(dd));
    			}
    		}
    	});
    },
    getCurrentMonth: function(f) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    				me.getCurrencyMonth(f.ownerCt.ownerCt.down('gridpanel'), f.value);
    			}
    		}
    	});
    },
    getDefaultCatecode: function(f) {
    	this.BaseUtil.getSetting('exchangeGl', 'exchangeCatecode', function(code){
    		f.setValue(code);
    	});
    },
    getCurrencyMonth: function(grid, ym) {
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: 'CurrencysMonth',
	   			fields: 'cm_code,cm_crname,cm_endrate',
	   			condition: 'cm_yearmonth=' + ym
	   		},
	   		method : 'post',
	   		callback : function(opt, s, r){
	   			var rs = new Ext.decode(r.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return;
	   			}
    			if(rs.success){
    				var data = Ext.decode(rs.data), _data = new Array();
    				Ext.each(data, function(d) {
    					_data.push({
    						cm_code: d.CM_CODE,
    						cm_crname: d.CM_CRNAME,
    						cm_endrate: d.CM_ENDRATE
    					});
    				});
    				grid.store.loadData(_data);
	   			}
	   		}
		});
    },
    deal: function(form, grid, ym ,ca_code, account, data) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/exchangegl.action',
    		params: {
    			yearmonth: ym,
    			ca_code: ca_code,
    			account: account,
    			data: data
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				if(rs.data) {
    					showMessage('提示', rs.data);
    				}
    				me.getCurrencyMonth(grid, ym);
    			} else if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			}
    		}
    	});
    }
});