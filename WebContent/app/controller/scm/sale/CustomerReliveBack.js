Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerReliveBack', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.sale.CustomerReliveBack', 'core.button.Confirm','core.button.Close'],
	init : function() {
		var me = this;
		this.control({
			'erpConfirmButton': {
    			click: function(btn){
    				this.confirm();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.onClose();
    			}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	confirm: function(){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "scm/sale/countCustReturn.action",
			timeout: 120000,
			method:'post',
			callback:function(options,success,r){
				me.FormUtil.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					Ext.Msg.alert("提示","计算成功！");
					window.location.reload();
				}
			}
		});
	}
});