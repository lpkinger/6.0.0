Ext.define('erp.view.core.button.GetForecastVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetForecastVendorButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'erpGetForecastVendorButton',
    	text: $I18N.common.button.erpGetVendorButton,
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
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
				if(records.length > 500) {
					showMessage('提示', '勾选行数必须小于500条!');
					return;
				}
				var id = new Array();
				Ext.each(records, function(record, index){
					id.push(record.data['pfd_id']);
				});
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'scm/purchaseforecast/getVendor.action',
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
		}
	});