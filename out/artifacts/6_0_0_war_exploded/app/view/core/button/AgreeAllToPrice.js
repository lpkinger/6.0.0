/**
 * 批量处理按钮
 */	
Ext.define('erp.view.core.button.AgreeAllToPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAgreeAllToPriceButton',
		text: $I18N.common.button.erpAgreeAllToPriceButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpAgreeAllToPriceButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110,
		handler : function(btn) {
			var grid = btn.up('grid'), record = grid.store.first(), id = null;
			if(record && (id = record.get('id_inid')) > 0) {
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + btn.url,
			   		params: {
			   			caller: caller,
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   				return "";
			   			}
		    			if(localJson.success){
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					window.location.reload();
			   				});
			   			}
			   		}
				});
			}
		}
	});