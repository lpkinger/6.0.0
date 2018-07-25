Ext.define('erp.view.core.button.ClearWareMan',{
	extend : 'Ext.Button',
	alias : 'widget.erpClearWareManButton',
	requires: ['erp.util.FormUtil'],
	iconCls : 'x-button-icon-check',
	text : $I18N.common.button.erpClearWareManButton,
	cls: 'x-btn-gray',
	width: 110,
	id:'erpClearWareManButton',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	handler : function(btn){
		var me = this;
		var form = btn.grid ||btn.ownerCt.ownerCt;
		if(form.fo_detailMainKeyField){
			warnMsg('确定清除明细吗?', function(btn){
				if(btn == 'yes'){
					var url = "scm/reserve/clearWareMan.action";
					form.setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + url,
				   		params: {
				   			caller:caller,
				   			condition: form.fo_detailMainKeyField + "=" + Ext.getCmp(''+form.keyField+'').value,
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			form.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
			        			showError(localJson.exceptionInfo);return;
			        		}
			    			if(localJson.success){
			    				alert('清除明细成功!');
								window.location.reload();
				   			} else {
				   				delFailure();
				   			}
				   		}
					});
				}
			});
		}else{
			showError("该张单据没有配置关联主表字段，无法清除明细数据!");
		}
	},
});