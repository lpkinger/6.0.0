Ext.define('erp.view.scm.reserve.GetInCode',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
				bodyStyle: 'background: #f1f1f1;',
				xtype: 'form',
				title: '入仓单号提取',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '20 0 0 0',
			    	xtype: 'textfield',
			    	fieldLabel: '入仓单号',
			    	readOnly: true,
			    	labelWidth: 80,
			    	id: 'code',
			    	name: 'code',
				},{
					margin:'5 0 0 0',
					xtype:'combo',
					fieldLabel:'前缀码',
					labelWidth:80,
					id:'whi_prefix',
					name:'whi_prefix',
					queryMode:'local',
					displayField:'display',
					valueField:'value',
					store:Ext.create('Ext.data.Store',{
						fields : ['display','value'],
						data : [{display:'本港(JR)',value:'JR'},
						        {display:'报关(JRG)',value:'JRG'},
						        {display:'仓储(JRW)',value:'JRW'}]
					})
				},{		
					margin : '5 0 0 0',
			    	xtype: 'dbfindtrigger',
			    	fieldLabel: '委托方号',
			    	readOnly: false,
			    	labelStyle:"color:red;",
			    	labelWidth: 80,
			    	id: 'whi_clientcode',
			    	name: 'whi_clientcode',
			    	allowBlank:false,
			    	listeners:{
						aftertrigger:function(f, d){
							/*console.log(f);
							console.log(d);*/
						}
					}
				},{		
					margin : '5 0 0 0',
			    	xtype: 'textfield',
			    	fieldLabel: '委托方名',
			    	labelStyle:"color:red;",
			    	fieldStyle:"background:rgb(224, 224, 224);",
			    	readOnly: true,
			    	labelWidth: 80,
			    	id: 'whi_clientname',
			    	name: 'whi_clientname'
				},{
			        margin:'5 0 0 0',
					xtype: 'numberfield',
			        name: 'whi_amount',
			        id:'whi_amount',
			        labelWidth: 80,
			        fieldLabel: '数量',
			        value: 1,
			        minValue: 1,
			        allowBlank:false
			    }],
				buttonAlign: 'center',
	    		buttons: [{
	    			xtype: 'erpConfirmButton',
	    		},{
	    			xtype:'erpCloseButton',
	    		}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});