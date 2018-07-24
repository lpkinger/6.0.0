Ext.define('erp.view.core.button.ConfirmVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmVendorButton',
		param: [],
		id:'confirmvendorbutton',
		text: $I18N.common.button.erpConfirmVendorButton,
		iconCls: 'x-button-icon-save', 
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
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
			   		url : basePath + 'scm/purchaseforecast/confirmVendor.action',
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
		},
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});