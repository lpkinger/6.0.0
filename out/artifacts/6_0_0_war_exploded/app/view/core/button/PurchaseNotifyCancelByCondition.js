/**
 * 取消采购通知
 */	
Ext.define('erp.view.core.button.PurchaseNotifyCancelByCondition',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPurchaseNotifyCancelByConditionButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '取消当前结果通知',
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		},	
		handler: function(){
			var me=this;
			var params=new Object();
	        var form = Ext.getCmp('dealform');
	       	params.condition = form.getCondition(); 
	    	var main = parent.Ext.getCmp("content-panel");
	    	var grid = Ext.getCmp('batchDealGridPanel');
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
				url : basePath + 'scm/cancelCondPurcNotify.action',
		   		params: params,
		   		method : 'post',
		   		timeout: 6000000,
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
		   					str = str.replace('AFTERSUCCESS', '');
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				}
		   				showError(str);return;
		   			}
	    			if(localJson.success){
	    				if(localJson.log){
	    					showMessage("提示", localJson.log);
	    				}
		   				Ext.Msg.alert("提示", "处理成功!", function(){ 
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				});
		   			}
		   		}
			});
	    
		}
	});