Ext.define('erp.view.common.JProcess.PersonalProcessSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			layout: {
				type: 'border',
				padding: '0 5 5 5' // pad the layout from the window edges
			},
			items: [{
				xtype: 'box',
				region: 'north',
				height: 40,
				style:'color: #596F8F;font-size: 22px;font-weight: 200;padding: 8px 15px;text-shadow: 0 1px 0 #fff;',
				html: '设置个人导航流程'
			},{
				xtype:'form',
				region:'center',
				frame:true,		
				items:[{	           
					xtype: 'itemselector',
					name: '选择流程',
					anchor: '100%',
					fieldLabel: '选择流程',		
					id: 'itemselector-field',
					displayField: 'text',
					valueField: 'value',
					allowBlank: false,
					msgTarget: 'side',
				}],
				dockedItems:[/*{
		            xtype: 'toolbar',
		            dock: 'top',
		            items: {
		                text: '设置',
		                menu: [{
		                    text: 'Set value (2,3)',
		                    handler: function(){
		                        Ext.getCmp('itemselector-field').setValue(['2', '3']);
		                    }
		                },{
		                    text: 'Toggle enabled',
		                    checked: true,
		                    checkHandler: function(item, checked){
		                        Ext.getCmp('itemselector-field').setDisabled(!checked);
		                    }
		                },{
		                    text: 'Toggle delimiter',
		                    checked: true,
		                    checkHandler: function(item, checked) {
		                        var field = Ext.getCmp('itemselector-field');
		                        if (checked) {
		                            field.delimiter = ',';
		                            Ext.Msg.alert('Delimiter Changed', 'The delimiter is now set to <b>","</b>. Click Save to ' +
		                                          'see that values are now submitted as a single parameter separated by the delimiter.');
		                        } else {
		                            field.delimiter = null;
		                            Ext.Msg.alert('Delimiter Changed', 'The delimiter is now set to <b>null</b>. Click Save to ' +
		                                          'see that values are now submitted as separate parameters.');
		                        }
		                    }
		                }]
		            }
		        }, */{
		            xtype: 'toolbar',
		            dock: 'bottom',
		            ui: 'footer',
		            defaults: {
		                minWidth: 75
		            },
		            items: ['->', {
		                text: '清空',
		                handler: function(){
		                    var field = Ext.getCmp('itemselector-field');
		                    if (!field.readOnly && !field.disabled) {
		                        field.clearValue();
		                    }
		                }
		            }, {
		                text: '重置',
		                handler: function() {
		                    Ext.getCmp('itemselector-field').up('form').getForm().reset();
		                }
		            }, {
		                text: '保存',
		                handler: function(){
		                    var form = Ext.getCmp('itemselector-field').up('form').getForm();
		                    if (form.isValid()){
		                    	Ext.Ajax.request({
		                			url : basePath + 'common/savePersonalProcess.action',
		                			method : 'post',
		                			params:{
		                				data:form.getValues()
		                			},
		                			callback : function(options,success,response){	   		
		                				var localJson = new Ext.decode(response.responseText);
		                				if(localJson.success){
		                				Ext.Msg.alert('提示','保存成功');
		                				}
		                			}
		                		});                
		                    }
		                }
		            },'->']
		        }]
			}]
		});
		me.callParent(arguments); 
	}
});