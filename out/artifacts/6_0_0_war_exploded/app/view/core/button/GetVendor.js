/**
 * 请购单批量转采购单界面，获取缺省供应商按钮
 */	
Ext.define('erp.view.core.button.GetVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetVendorButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'erpGetVendorButton',
    	text: $I18N.common.button.erpGetVendorButton,
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        grid.multiselected = new Array();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		var bool = true;
	        		Ext.each(grid.multiselected, function(){
	        			if(this.data[grid.keyField] == item.data[grid.keyField]){
	        				bool = false;
	        			}
	        		});
	        		if(bool){
	        			grid.multiselected.push(item);
	        		}
	        	}
	        });
			var records = grid.multiselected;
			if(records.length > 0){
				if(records.length > 1000) {
					showMessage('提示', '勾选行数必须小于1000条!');
					return;
				}
				var id = new Array();
				Ext.each(records, function(record, index){
					id.push(record.data['ad_id']);
				});
				grid.setLoading(true);
				if(caller=='MCApplication!ToPurchase!Deal'){
					Ext.Ajax.request({
				   		url : basePath + 'scm/purchase/getMCVendor.action',
				   		params: {
				   			id: id
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			grid.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   			} else {
				   				if(localJson.success){
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery(true);
				   				}
				   			}
				   		}
					});
				}else if(caller=='Application!ToMake!Deal'){
					Ext.Ajax.request({
				   		url : basePath + 'scm/purchase/getVendorByCaller.action',
				   		params: {
				   			id: id,
				   			caller:caller
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			grid.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   			} else {
				   				if(localJson.success){
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery(true);
				   				}
				   			}
				   		}
					});
				} else {
					Ext.Ajax.request({
				   		url : basePath + 'scm/purchase/getVendor.action',
				   		params: {
				   			id: id
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			grid.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   			} else {
				   				if(localJson.success){
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery(true);
				   				}
				   			}
				   		}
					});
				}
			} else {
				showError("请勾选需要的明细!");
			}
		}
	});