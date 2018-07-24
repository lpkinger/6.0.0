Ext.define('erp.view.b2c.common.B2CSetting',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				xtype: 'form',
				anchor: '100% 100%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				autoScroll: true,
				scrollable: true,
				buttonAlign:'center',
				/*dockedItems: [{
				    xtype: 'toolbar',
				    dock: 'top',
				    padding:'8 5 8 20',	
				    items: [
				       '->', { 
				          xtype: 'button', 
				          text: '启 用',
				          id :'startB2C',
				          name:'startB2C',
				          iconCls: 'x-button-icon-delete',
    					  cls: 'x-btn-gray',
    					  width: 90,
    					  scale  : 'medium'
				       },'->' 
				    ]
				}],*/
				items:[{
					xtype: 'checkbox',
    				boxLabel: '启用优软商城',
    				name: 'startCheck',
    				id: 'startCheck',
    				checked: 0,
    				margin: '4 8 4 8'
				},{
					 html: '<span>--勾选启用优软商城，系统会进行商城启用必填项数据设置和检测</span>'  ,
					 bodyStyle: 'background: #f1f1f1;border:none',
					 margin:'0 5 20 40'
				},{
			    	xtype: "form",
					anchor: '100% 10%',
					hidden: true,
					bodyStyle: 'background: #f1f1f1;border:none',	
					items:[{
						id:'tips',
						html:'<span style="color: gray;">提示信息</span>',
						bodyStyle: 'background: #f1f1f1;border:none',
						margin:'0 3 3 30'
					}]
			    },{
			    	xtype: 'fieldset',
			    	title: '优软商城客户设置',
					defaults: {
						labelWidth:150,
						margin:'5 5 2 5'
					}, 
					layout: 'anchor',
					items:[{
						layout: 'hbox',
						bodyStyle: 'background: #f1f1f1;border:none',
						defaults: {
							labelWidth:150,
							labelAlign:'right',
							margin:'5 5 2 5'
						}, 
						items: [{
								xtype: 'dbfindtrigger',
								fieldLabel: '客户编号',
								allowBlank:false,
								id:'b2ccusomter',
								name:'b2ccusomter',
								width:350,
								dbCaller:'Sale',
								triggerName:'sa_custcode',
								listeners:{
									aftertrigger:function(t, d){
										t.ownerCt.down('textfield[name=b2ccusomter]').setValue(d.get('cu_code')); 
										t.ownerCt.down('textfield[name=b2ccusomtername]').setValue(d.get('cu_name')); 
									}
								}
							},{
								xtype: 'textfield',
								allowBlank:false,
								id:'b2ccusomtername',
								name:'b2ccusomtername',
								width:200,
								readOnly:true
							},{
								xtype: 'button',
								text: '新增',
								id:'newCustomer',
								iconCls: 'x-button-icon-add',
	    					    cls: 'x-btn-gray'
							}]
						},{					
						  html: '<span>【优软商城客户】指平台生成的销售订单 用户确认接收后 在UAS系统产生的销售订单所取的客户信息</span>'  ,
						  bodyStyle: 'background: #f1f1f1;border:none',
						  margin:'0 5 20 50'
					    }]
					},{					
						xtype: 'fieldset',
				    	title: '优软商城供应商设置',
						defaults: {
							labelWidth:150,
							margin:'5 5 2 5'
						}, 
						layout: 'anchor',
						items:[{
							xtype: 'fieldcontainer',					
							defaults: {
								labelWidth:150,
								labelAlign:'right',
								margin:'5 5 2 5'
							}, 
							layout: 'hbox',
							items: [{
								xtype: 'dbfindtrigger',
								fieldLabel: '供应商编号',
								allowBlank:false,
								id:'b2cvendor',
								name:'b2cvendor',
								width:350,
								dbCaller:'Purchase',
								triggerName:'pu_vendcode',
								listeners:{
									aftertrigger:function(t, d){
										t.ownerCt.down('textfield[name=b2cvendor]').setValue(d.get('ve_code')); 
										t.ownerCt.down('textfield[name=b2cvendorname]').setValue(d.get('ve_name'));
									}
								}
							},{
								xtype: 'textfield',
								allowBlank:false,
								id:'b2cvendorname',
								name:'b2cvendorname',
								width:200,
								readOnly:true
							},{
								xtype: 'button',
								text: '新增',
								id:'newVendor',
								iconCls: 'x-button-icon-add',
	    					    cls: 'x-btn-gray'
							}]
						   },{
								 html: '<span>【优软商城供应商】指平台购买生成的采购订单在UAS系统产生的采购订单所取的供应商信息</span>'  ,
								 bodyStyle: 'background: #f1f1f1;border:none;margin:3 3 3 3;',
								 margin:'0 5 20 40'
							}]
					},{
						xtype: 'fieldset',
				    	title: '优软商城销售类型设置',
						defaults: {
							labelWidth:150,
							margin:'5 5 2 5'
						}, 
						layout: 'anchor',
							items:[{
						     xtype: 'fieldcontainer',					
							 defaults: {
								 labelWidth:150,
								 labelAlign:'right',
								 margin:'5 5 5 5'
							  }, 
							 layout: 'hbox',
							 items: [{
								    xtype: 'dbfindtrigger',
									fieldLabel: '销售类型编号',
									id:'b2csalekind',
									name:'b2csalekind',
									width:350,
									dbCaller:'Sale',
									triggerName:'sa_kind',
									listeners:{
										aftertrigger:function(t, d){
											t.ownerCt.down('textfield[name=b2csalekind]').setValue(d.get('sk_code')); 
											t.ownerCt.down('textfield[name=b2csalekindname]').setValue(d.get('sk_name'));
										}
									}
								},{
									xtype: 'textfield',
									allowBlank:false,
									id:'b2csalekindname',
									name:'b2csalekindname',
									width:200,
									readOnly:true
								}]	
							},{
							 html: '<span>【优软商城销售类型】指平台生成的销售订单 用户确认接收后 在UAS系统产生的销售订单所取的销售订单类型</span>'  ,
						  		bodyStyle: 'background: #f1f1f1;border:none;margin:3 3 3 3;',
						 	 margin:'0 5 20 40'
					 }]					
				 }],
				 buttons: [{
			        text: '保存',
			        id:'save',
			        name:'save',
			       /* formBind: true, //only enabled once the form is valid
			        disabled: true,*/
			        height:30,
			        handler: function() {
			            var form = this.up('form').getForm();
			           /* if (form.isValid()) {
			                form.submit({
			                    success: function(form, action) {
			                       Ext.Msg.alert('保存成功', action.result.msg);
			                    },
			                    failure: function(form, action) {
			                        Ext.Msg.alert('操作失败', action.result.msg);
			                    }
			                });
			            }*/
			        }
			    }]
			  }]		 
		}); 
		me.callParent(arguments); 
	} 
});