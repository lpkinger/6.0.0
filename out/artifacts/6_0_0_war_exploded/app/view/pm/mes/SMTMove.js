Ext.define('erp.view.pm.mes.SMTMove',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'SMT机台转移',
				xtype: 'form',
				anchor: '100% 25%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				items:[{
					xtype: 'fieldcontainer',
					region: 'center',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 250
					},   					
					layout: {
						type: 'table',
						columns: 5
					},
					items: [{
							xtype: 'textfield',
							fieldLabel: '原机台编号',
							colspan: 1,
							id:'de_oldCode',
							name:'de_oldCode',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入机台编号',
				            allowBlank: true
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '作业单号',
							colspan: 1,
							id:'mc_code',
							name:'mc_code',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入作业单号',
				            allowBlank: true,
				            dbCaller:'SMTFeed'
						},{
							xtype: 'textfield',
							fieldLabel: '转至机台号',
							colspan: 1,
							id:'de_newCode',
							name:'de_newCode',
							emptyText:'请录入转至机台号',
							allowBlank: true
						},{
							xtype: 'erpQueryButton',
							colspan: 1,
							width:'80px',
							style: {
					    		marginLeft: '30px',
					    		marginRight:'20px'
					        }
						},{
							xtype: 'button',
							text: '确定转移 ',
							colspan: 1,
							id:'confirmBtn'	,
							iconCls: 'x-button-icon-save', 
							cls: 'x-btn-gray',
    	                    formBind: true,
    	                    width:'80px',
    	                    style: {
					    		marginLeft: '20px'
					        }
						}]					
					}]
			},{
				xtype: 'erpQueryGridPanel',
				anchor: '100% 75%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});