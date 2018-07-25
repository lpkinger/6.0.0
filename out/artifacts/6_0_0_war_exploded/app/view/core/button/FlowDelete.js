Ext.define('erp.view.core.button.FlowDelete',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpFlowDeleteButton',
	text: $I18N.common.button.erpFlowDeleteButton,
	iconCls: 'x-button-icon-close',
	cls: 'x-btn-gray',
	margin:'0 5 0 0',
	id: 'erpFlowDeleteButton',
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	handler:function(){
		var form = Ext.getCmp('dealform');
		var grid = Ext.getCmp('batchDealGridPanel');
    	var count=0;
    	if(grid.multiselected.length==0){
    		var items = grid.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
            		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
            		grid.multiselected.push(item);
            	}
            });
    	}
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length>0){
			var map = {},params = [];
			Ext.each(records, function(record, index){
				    if(!map[record.data.fi_caller]){
				        params.push({
				            caller: record.data.fi_caller,
				            data: [record.data.fi_keyvalue]
				        });
				        map[record.data.fi_caller] = record;
				    }else{
				        for(var j = 0; j < params.length; j++){
				            var dj = params[j];
				            if(dj.caller == record.data.fi_caller){
				                dj.data.push(record.data.fi_keyvalue);
				                break;
				            }
				        }
				    }
				
			 });
			 var datas = unescape(escape(Ext.JSON.encode(params)));
			 warnMsg('确定要删除所选的流程吗？', function(btn){
			 	if(btn == 'yes'){
					form.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'oa/flow/delete.action',
						params: {
							datas:datas
						},
						method : 'post',
						callback : function(options,success,response){
							form.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
							if(localJson.success){
								showInformation('删除成功！', function(btn){
									Ext.getCmp('dealform').onQuery();
								});
							} else {
								delFailure();
							}
						}
					});
				}
			 });
		}else{
			showError("请勾选数据!");
		}
	}
});