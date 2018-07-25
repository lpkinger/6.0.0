Ext.define('erp.view.core.button.SendEdi',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSendEdiButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'sendEdi',
    	text: $I18N.common.button.erpSendEdiButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
        	afterrender:function(btn){
        		var sendEdiStatus = Ext.getCmp('syssendedi_');
        		if(!sendEdiStatus){
        			btn.hide();
        		}
        	},
        	click:function(self){
        		var id;
        		if(caller){
        			Ext.Msg.confirm('确认','确认发送EDI?',function(btn){
        				if(btn=='yes'){
		        			if(caller=='ProdInOut!PurcCheckin'||caller=='ProdInOut!Sale'){
		        				id = Ext.getCmp('pi_id').value;      				
		        			}else if(caller=='Purchase'){
		        				id = Ext.getCmp('pu_id').value;
		        			}else{
		        				var form = self.ownerCt.ownerCt;
		        				var keyField = form.keyField;
		        				id = Ext.getCmp(keyField).value;
		        			}
		        			if(id){
			    				Ext.Ajax.request({
									url:basePath + 'scm/reserve/sendEdi.action',
									method:'post',
									params:{
										id:id,
										caller:caller
									},
									callback:function(options,success,response){
										var res = Ext.decode(response.responseText);
										if(res.success){
											showError('发送成功!');
											window.location.href = window.location.href;
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
		}
	});