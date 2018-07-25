/**
 * 订阅批量申请
 */	
Ext.define('erp.view.core.button.VastAddSubs',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastAddSubsApplyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpVastAddSubsApplyButton',
    	text: $I18N.common.button.erpVastAddSubsApplyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(url){
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
				var id = new Array();
				Ext.each(records, function(record, index){
					id.push(record.data['id_']);
				});
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'common/charts/vastAddSubsApply.action',
			   		params: {
			   			ids: id.join(","),
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
			   					if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery(true);
			   				}
			   			}
			   		}
				});
			} else {
				showError("请勾选需要的明细!");
			}
		}
	});