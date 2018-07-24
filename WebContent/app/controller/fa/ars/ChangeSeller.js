Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ChangeSeller', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.ars.ChangeSellerForm', 'fa.ars.ChangeSeller',
			'core.button.Confirm', 'core.button.Close',
			'core.trigger.DbfindTrigger' ],
	init : function() {
		var me = this;
		this.control({
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpConfirmButton' : {
				click : function(btn) {
					var seller1 = Ext.getCmp('sa_sellercode').value;
					var seller2 = Ext.getCmp('sn_sellercode').value;
					if (seller1 == null || seller1 == '') {
						showError('业务员编号必须填写!');
						return;
					}
					if (seller2 == null || seller2 == '') {
						showError('业务员编号必须填写!');
						return;
					}
					var w = btn.ownerCt.ownerCt;
					me.confirm(me.getCondition(w));
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	getCondition : function(w) {
		var r = new Object(), v;
		Ext.each(w.items.items, function(item) {
			if (typeof item.getValue === 'function') {
				v = item.getValue();
				if (!Ext.isEmpty(v)) {
					r[item.id] = v;
				}
			}
		});
		return r;
	},
	confirm : function(cond) {
		Ext.Ajax.request({
			url : basePath
					+ 'fa/ars/ChangeSellerController/changeSeller.action',
			params : {
				condition : Ext.encode(cond)
			},
			method : 'post',
			callback : function(o, s, r) {
				var rs = new Ext.decode(r.responseText);
				if (rs.success) {
					// 执行成功
					Ext.Msg.alert('提示', '操作成功!');
				} else {
					// 执行失败
					Ext.Msg.alert('提示','操作失败!');
				}
			}
		});
	}
});