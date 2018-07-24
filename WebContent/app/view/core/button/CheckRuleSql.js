Ext.define('erp.view.core.button.CheckRuleSql',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckRuleSqlButton',
		iconCls: 'x-button-icon-help',
    	cls: 'x-btn-gray',
    	id: 'checkRuleSql',
    	text: $I18N.common.button.erpCheckRuleSqlButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
	    	click:function(self){
	    		var sqlField;
	    		var form = self.ownerCt.ownerCt;
	    		Ext.Array.each(form.items.items,function(item,index){
	    			if(item.logic=='SQL'){
	    				sqlField = item;
	    			}
	    		});
	    		if(sqlField&&sqlField.value){
					Ext.Ajax.request({
						url:basePath + 'common/checkRuleSql.action',
						method:'post',
						params:{
							sql:sqlField.value
						},
						callback:function(options,success,response){
							var res = Ext.decode(response.responseText);
							if(res.success){
								console.log(res.result);
								if(res.result){
									Ext.Msg.alert('提示','检测通过!');
								}else{
									Ext.Msg.alert('提示','检测失败，原因:' + res.errorInfo);
								}
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});	    			
	    		}
	        }          
		}
	});