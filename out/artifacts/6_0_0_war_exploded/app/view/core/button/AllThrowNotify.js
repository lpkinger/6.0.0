Ext.define('erp.view.core.button.AllThrowNotify',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAllThrowNotifyButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	text: $I18N.common.button.erpAllThrowButton,
    	style: {
    		marginLeft: '10px'
        },
        width:90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
          var form =Ext.getCmp('dealform');
          var grid = Ext.getCmp('batchDealGridPanel');
          var condition=form.getCondition(grid);
          condition=condition!=""?condition + " AND "+getUrlParam("urlcondition"):getUrlParam("urlcondition");
      	 var main = parent.Ext.getCmp("content-panel");
		 main.getActiveTab().setLoading(true);
           Ext.Ajax.request({
		   		url : basePath + 'pm/wcplan/throwpurchasenotify.action',
		   		params: {
		   			condition:condition		   			
		   		},
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