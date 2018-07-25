Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.MakeFeeClose', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'co.cost.MakeFeeClose', 'core.trigger.DbfindTrigger', 
	          'core.button.MakeFeeClose', 'core.button.Close' ],
	init : function() {
		var me = this;
		this.control({
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.onClose();
				}
			},
			'erpMakeFeeCloseButton' : {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt, 
						makeCatecode = form.down('#makeCatecode').value, 
						makeToCatecode = form.down('#makeToCatecode').value,
						materialsCatecode = form.down('#materialsCatecode').value,
						manMakeCatecode = form.down('#manMakeCatecode').value,
						account = form.down('#account').value,
						account2 = form.down('#account2').value,
						account3 = form.down('#account3').value;
					if (!Ext.isEmpty(makeCatecode) && !Ext.isEmpty(makeToCatecode)) {
						this.deal(makeCatecode, makeToCatecode, materialsCatecode, manMakeCatecode, account, account2, account3);
					} else {
						alert('请先选择制造成本科目.');
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
			'#makeCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('MakeFeeClose', 'makeCatecode', function(d){
						f.setValue(d);
						d && (typeof f.autoDbfind === 'function') && f.autoDbfind('form', null, 'ca_code', 'ca_code=\'' + d + '\'');
					});
				}
			},
			'#makeToCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('MakeFeeClose', 'makeToCatecode', function(d){
						f.setValue(d);
						d && (typeof f.autoDbfind === 'function') && f.autoDbfind('form', null, 'ca_code', 'ca_code=\'' + d + '\'');
					});
				}
			},
			'#materialsCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('MakeFeeClose', 'materialsCatecode', function(d){
						f.setValue(d);
						d && (typeof f.autoDbfind === 'function') && f.autoDbfind('form', null, 'ca_code', 'ca_code=\'' + d + '\'');
					});
				}
			},
			'#manMakeCatecode' : {
				afterrender: function(f) {
					this.BaseUtil.getSetting('MakeComplete', 'manMakeCatecode', function(d){
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
	deal : function(makeCatecode, makeToCatecode, materialsCatecode, manMakeCatecode, account, account2, account3) {
		if(account2 && Ext.isEmpty(materialsCatecode)){
			showError("请先选择材料成本差异科目.");
			return;
		}
		if(account3 && Ext.isEmpty(manMakeCatecode)){
			showError("请先选择直接人工科目.");
			return;
		}
		var tab = this.BaseUtil.getActiveTab();
		tab.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'co/cost/makeCreate.action',
			params : {
				makeCatecode : makeCatecode,
				makeToCatecode : makeToCatecode,
				materialsCatecode : materialsCatecode,
				manMakeCatecode : manMakeCatecode,
				account : account,
				account2 : account2,
				account3 : account3
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