Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.EngineeringFeeClose', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'co.cost.EngineeringFeeClose', 'core.trigger.DbfindTrigger', 
	          'core.button.Confirm', 'core.button.Close' ],
	init : function() {
		var me = this;
		this.control({
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.onClose();
				}
			},
			'erpConfirmButton' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt, 
						enCatecode = form.down('#enCatecode').value, 
						gsCatecode = form.down('#gsCatecode').value;
						account = form.down('#account').value;
					if (!Ext.isEmpty(enCatecode) && !Ext.isEmpty(gsCatecode)) {
						this.deal(enCatecode, gsCatecode, account);
					} else {
						alert('请先选择科目.');
					}
				}
			},
			'displayfield[name=yearmonth]' : {
				afterrender: function(f) {
					if(Ext.isEmpty(f.value)){
						this.getMonth(f);
					}
				}
			},
			'#enCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('EngineeringFeeClose', 'enCatecode', function(d){
						f.setValue(d);
						d && (typeof f.autoDbfind === 'function') && f.autoDbfind('form', null, 'ca_code', 'ca_code=\'' + d + '\'');
					});
				}
			},
			'#gsCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('EngineeringFeeClose', 'gsCatecode', function(d){
						f.setValue(d);
						d && (typeof f.autoDbfind === 'function') && f.autoDbfind('form', null, 'ca_code', 'ca_code=\'' + d + '\'');
					});
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	deal : function(enCatecode, gsCatecode, account) {
		var tab = this.BaseUtil.getActiveTab();
		tab.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'co/cost/engineeringCreate.action',
			params : {
				enCatecode : enCatecode,
				gsCatecode : gsCatecode,
				account : account
			},
			timeout: 120000,
			callback : function(opt, s, r) {
				tab.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.data) {
					showMessage('提示', rs.data);
				} else if(rs.exceptionInfo) {
					showMessage('错误', rs.exceptionInfo);
				}
			}
		});
	},
	getMonth: function(f) {
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
    }
});