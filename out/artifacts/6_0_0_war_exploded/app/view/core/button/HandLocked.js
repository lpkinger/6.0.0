Ext.define('erp.view.core.button.HandLocked',{
	extend : 'Ext.Button',
	alias : 'widget.erpHandLocked',
	iconCls : 'x-button-icon-submit',
	text : $I18N.common.button.erpHandLockedButton,
	requires: ['erp.util.GridUtil'],
	GridUtil: Ext.create('erp.util.GridUtil'),
	cls: 'x-btn-gray',
	width: 110,
	id: 'erpHandLockedButton',
	initComponent : function(){
		this.callParent(arguments); 
	},
	handler : function(btn){
		var me = this;
		var formStore = new Object();
		var type = getUrlParam("type");
		var code = getUrlParam("sacode");
		var detno = getUrlParam("detno");
		var whichsystem = getUrlParam("whichsystem");
		var id = getUrlParam("id");
		var pr_ispubsale = Ext.getCmp("pr_ispubsale").value;
		formStore.type = type;
		formStore.code = code;
		formStore.detno = detno;
		formStore.en_whichsystem = whichsystem;
		formStore.id = id;
		formStore.pr_ispubsale = pr_ispubsale;
		var qty = Ext.getCmp("qty").value;
		var grid = Ext.getCmp('batchDealGridPanel');
		var items = grid.getMultiSelected();
		if(items.length<=0){
			showError("请选择明细行！");
			return;
		}
		var count=0;
		var arr = [];
		Ext.Array.each(items,function(item){
			count = count + item.data.tqty;
			arr.push(item.data);
		});
		if(count>qty){
			showError("锁定数量不能大于非锁定总数！");
			return;
		}
		grid.setLoading(true);
		Ext.Ajax.request({
			url:basePath+'scm/handLocked.action',
			params :{
				caller : caller,
				formStore : Ext.JSON.encode(formStore),
				data:Ext.JSON.encode(arr)
			} ,
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.success){
					showError(res.log);
					var cont = parent.Ext.getCmp('content-panel');
					cont.getActiveTab().close();
				}
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
	}
});