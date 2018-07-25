/**
 * 批量获取客户UU号，一键获取
 */	
Ext.define('erp.view.core.button.CheckCustomerUU',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckCustomerUUButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'erpCheckCustomerUUButton',
    	text: $I18N.common.button.erpCheckVendorUUButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'b2b/vastOpenCustomerUU.action',
			   		method : 'post',
			   		params: null,
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   			} else {
			   				if(localJson.success){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery(true);
			   					showError(localJson.log);
			   				}
			   			}
			   		}
				});			
		}
	});