Ext.define('erp.view.core.button.ResSubmitNoStandard',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitNoStandardButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '反提交(转非标准)',
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		hidden:true,
		listeners:{
			'afterrender':function(btn){
				var standard=Ext.getCmp('pr_standardized');
				var statuscode =Ext.getCmp('pr_standardstatus');
				if(statuscode && statuscode.value=='COMMITED'){
					btn.show();
					if(standard && standard.value!=-1){
						btn.setText('反提交(入标准库)');
					}else btn.setText('反提交(出标准库)');
				}
			}
		},
		handler:function(){
				var form = Ext.getCmp('form');
				var id=Ext.getCmp(form.keyField).value;
				form.FormUtil.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath +'scm/product/resSubmitNoStandard.action',
			   		params : {
			   			id:id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			form.FormUtil.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
		    				showMessage('提示', '反提交成功!', 1000);
		    					window.location.reload();
			   			} else if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
			   					str = str.replace('AFTERSUCCESS', '');
			   					showMessage('提示', '反提交成功!', 1000);
			   					showError(str);
			   				} else {
			   					showError(str);
				   				return;
			   				}
			   			} else{
			   				showMessage('提示', '反提交失败', 1000);
			   			}
			   		}
			   		
				});
		  }
	});