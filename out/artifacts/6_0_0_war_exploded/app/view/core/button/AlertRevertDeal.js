Ext.define('erp.view.core.button.AlertRevertDeal',{ 
		extend: 'Ext.Button', 
		FormUtil: Ext.create('erp.util.FormUtil'),
		alias: 'widget.erpAlertRevertDealButton',
		param: [],
		id: 'erpAlertRevertDealButton',
		text: $I18N.common.button.erpAlertRevertDealButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	/*width: 120,*/
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
			click:function(btn){
			   var grid = Ext.getCmp('batchDealGridPanel');
			   var record = grid.getSelectionModel().selected;
			   var params = [];
			   var b = new Object();
			   Ext.Array.forEach(record.items,function(i){
				   b.ad_id=i.data["ad_id"];
				   b.ad_cause=i.data["ad_cause"];
				   b.ad_solution=i.data["ad_solution"];
				   params.push(JSON.stringify(b));
			   })
			   if(record.items.length>0){
				   Ext.Ajax.request({
					   url: basePath+"sys/alert/revertDealAlertData.action" ,
					   params:{caller:caller,data:params},
					   method : 'post',
				   	   timeout: 6000000,
					   callback:function(options,success,response){
						   var res = new Ext.decode(response.responseText);
						   if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}
						   if(res.success){
							   	Ext.Msg.alert("提示", "批量回复成功！");
							   	Ext.getCmp('dealform').onQuery();
							}
					   }
				   });
			   }else{
				   Ext.Msg.alert('提示', '请选择要回复的预警信息');
			   }
			}
		}
});