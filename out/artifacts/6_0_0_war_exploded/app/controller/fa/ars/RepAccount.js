Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.RepAccount', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.ars.RepAccount', 'core.button.Confirm', 'core.button.Close', 'core.form.MonthDateField' ],
	init : function() {
		var me = this;
		this.control({
			'#form' : {
				afterrender : function(form) {
					me.BaseUtil.getSetting('sys', 'auditDuring', function(bool) {
						if (bool) {
							form.down('#date').hide();
							form.down('#addate').show();
							me.getCurrentMonth(form.down('#addate'), "MONTH-A");
						} else {
							form.down('#addate').hide();
							form.down('#date').show();
							me.getCurrentMonth(form.down('#date'), "MONTH-A");
						}
					});
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.onClose();
				}
			},
			'erpConfirmButton' : {
				click : function(btn) {
					this.confirm(btn.ownerCt.ownerCt);
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	confirm : function(form) {
		var me = this, date;
		me.BaseUtil.getSetting('sys', 'auditDuring', function(bool) {
			if (bool) {
				date = form.down('#addate').value;
				me.repAccount(date);
			} else {
				date = form.down('#date').value;
				me.repAccount(date);
			}
		});
	},
	repAccount : function(date) {
		if (Ext.isEmpty(date)) {
			showError("请填写期间！");
			return;
		}
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "fa/ars/reportAccount.action",
			params : {
				date : date
			},
			method : 'post',
			timeout : 60000,
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					Ext.Msg.alert("提示", "操作成功！");
				} else {
					if (localJson.exceptionInfo) {
						var str = localJson.exceptionInfo;
						if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showError(str);
						} else {
							showError(str);
							return;
						}
					}
				}
			}
		});
	},
	getCurrentMonth : function(f, type) {
		Ext.Ajax.request({
			url : basePath + 'fa/getMonth.action',
			params : {
				type : type
			},
			callback : function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if (rs.data) {
					f.setValue(rs.data.PD_DETNO);
				}
			}
		});
	}
});