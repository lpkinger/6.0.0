Ext.define('erp.view.core.button.EdiMarkAsDone',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEdiMarkAsDoneButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'sendEdi',
    	text: $I18N.common.button.erpEdiMarkAsDoneButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
        	click:function(self){
	 			var grid = Ext.getCmp('batchDealGridPanel');
	 			var keyField = grid.keyField;
		        var items = grid.selModel.getSelection();
		        var idArr = new Array();
		        var ids ;
		        
		        Ext.each(items, function(item, index){
		        	var keyVal = item.data[keyField];
		        	if(keyVal){
		        		var bool = true;
		        		Ext.each(idArr, function(id,index){
		        			if(id == keyVal){
		        				bool = false;
		        			}
		        		});
		        		if(bool){
		        			idArr.push(keyVal);
		        		}
		        	}
		        });
		        
				if(idArr.length<=0){
        			showError('请勾选需要的明细!');
        			return;
        		}
        		
        		Ext.Msg.confirm('确认','是否确定要标记为已处理?',function(btn){
        			if(btn=='yes'){
		        		ids = idArr.join(',');
		        		if(ids){
			        		var myMask = new Ext.LoadMask(Ext.getBody(), {
								msg    : "正在操作,请稍后...",
								msgCls : 'z-index:1000;'
							});
							myMask.show();
			        		Ext.Ajax.request({
			        			url:basePath + 'scm/reserve/markEdiAsDone.action',
			        			method:'post',
			        			params:{
			        				ids:ids,
			        				caller:caller
			        			},
			        			callback:function(options,success,response){
			        				myMask.hide();
			        				var res = Ext.decode(response.responseText);
			        				var html;
			        				if(res.success){
			        					Ext.Msg.alert('提示','处理成功!');
			        					Ext.getCmp('dealform').onQuery(true);
			        				}else if(res.exceptionInfo){
			        					showError(res.exceptionInfo);
			        				}
			        			}
			        		});		        			
		        		}
        			}
        		});
        	} 
		}
	});