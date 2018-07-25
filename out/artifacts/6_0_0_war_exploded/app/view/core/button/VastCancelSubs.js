/**
 * 我申请的订阅批量取消
 */	
Ext.define('erp.view.core.button.VastCancelSubs',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastCancelSubsApplyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpVastCancelSubsApplyButton',
    	text: $I18N.common.button.erpVastCancelSubsApplyButton,
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
	        	if(this.data['isapplied_']==-1){
	        		var bool = true;
	        		Ext.each(grid.multiselected, function(){
	        			if(this.data['num_id'] == item.data['num_id'] && this.data['emp_id'] == item.data['emp_id']){
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
				var data = new Array();
				Ext.each(records, function(record, index){
					data.push('('+record.data['emp_id']+','+record.data['num_id']+')');
				});
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + 'common/charts/vastCancelSubsApply.action',
			   		params: {
			   			datas: data.join(","),
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
				showError("请勾选已申请的订阅!");
			}
		}
	});