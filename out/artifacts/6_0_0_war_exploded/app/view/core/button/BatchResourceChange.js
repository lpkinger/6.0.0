Ext.define("erp.view.core.button.BatchResourceChange",{
	extend: 'Ext.Button', 
	alias: 'widget.erpBatchResourceChangeButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	id: 'erpBatchResourceChangeButton',
	FormUtil:Ext.create('erp.util.FormUtil'),
	text: '责任人批量变更',
	style: {
		marginLeft: '10px'
    },
    width: 130,
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	listeners:{
	click:function(btn){
		var grid=Ext.getCmp('batchDealGridPanel');
		var models=grid.getSelectionModel().getSelection();
		var form = btn.ownerCt.ownerCt;
		var remark = form.down('#remark');
		if (!remark || !remark.value){
			showError("请填写变更备注!");
			return;
		}
		var newResource = form.down('#newresourcecode'),newResourcecode;
		if (newResource && newResource.value) {
			newResourcecode = newResource.value;
		}
		if(models.length > 0) {
			var arr = new Array();	
			Ext.each(models,function(record){
				var data = record.data,obj = new Object() ;
				newResourcecode = newResourcecode ? newResourcecode : data.ra_newresourcecode;
				if(newResourcecode && data.ra_id && newResourcecode != data.ra_resourcecode) {
						obj.id = data.ra_id;
						obj.resourcecode = newResourcecode;
						obj.remark = remark.value;
						arr.push(obj);
				}
			});
			if (arr.length > 0) {
				var jsonArr=unescape(escape(JSON.stringify(arr)));
				Ext.Ajax.request({
					url:basePath + 'plm/change/batchResourceChange.action',
					method:'POST',
					params:{
						data:jsonArr
					},
					callback:function(opts, success, response) {
						var res=Ext.decode(response.responseText);
						if(res.exceptionInfo != null){
							showError(res.exceptionInfo);
							return;
						}
						if(res.success){
							showMessage("提示",'资源已成功转移!');			
							grid.tempStore={};
							grid.multiselected = new Array();
							Ext.getCmp('dealform').onQuery();
						}	
					}
					
				});
			}
		} else {
			showError('请选择明细行!');
			return;
		}
	}
	}
});