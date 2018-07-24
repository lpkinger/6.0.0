Ext.define('erp.view.core.button.CancelEdi',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelEdiButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'cancelEdi',
    	text: $I18N.common.button.erpCancelEdiButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
        	afterrender:function(btn){
        		var sendEdiStatus = Ext.getCmp('syssendedi_');
        		if(!sendEdiStatus||sendEdiStatus.value==0){
        			btn.hide();
        		}
        	},
        	click:function(self){
        		var id;
        		if(caller){
        			var me = this;
        			if(me.win){
        				me.win.show();
        				return;
        			}
        			Ext.Msg.confirm('确认','确认退单?',function(btn){
        				if(btn=='yes'){
        					var win = Ext.create('Ext.window.Window', {
							    title: '确认',
							    height: 220,
							    width: 370,
							    layout: 'column',
							    closeAction:'hide',
							    items: [{
							    	xtype:'displayfield',
							    	fieldLabel:'请输入退单原因:',
							    	labelWidth:200,
							    	style:'color:red;font-size:14px',
							    	columnWidth:1,
							    },{
							        xtype : 'textareafield',
							        grow : true,
							        name : 'message',
							        fieldLabel : '',
							        columnWidth:1,
							        id:'ngremark',
							        maxLength:1500,
							    }],
							    buttons: [{
							    	text: '确定',
							    	handler:function(){
							   			var remark = Ext.getCmp('ngremark').value;
							   			if(!remark){
							   				Ext.Msg.alert('提示','请输入退单原因');
							   				return;
							   			}
							   			var form = self.ownerCt.ownerCt;
				        				var keyField = form.keyField;
				        				id = Ext.getCmp(keyField).value;
					        			if(id){
						    				Ext.Ajax.request({
												url:basePath + 'scm/reserve/cancelEdi.action',
												method:'post',
												params:{
													id:id,
													caller:caller,
													remark:remark
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
							    },{
							    	text:'取消',
							    	handler:function(btn){
							    		var win = btn.ownerCt.ownerCt;
							    		win.close();
							    	}
							    }]
							});
							win.show();
							me.win = win;
        				}
        			});
        		}
        	}          
		}
	});