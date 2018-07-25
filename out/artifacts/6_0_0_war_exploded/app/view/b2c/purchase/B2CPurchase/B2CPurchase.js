Ext.define('erp.view.b2c.purchase.B2CPurchase.B2CPurchase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 			 
	          items: [{
	            	id:'buyContainer',
	            	xtype : 'panel',
	            	anchor: '100% 100%',
	            	autoScroll :true,
	            	bodyStyle: 'background: #f1f1f1;border:none',
	            	items:[{
	            		 xtype:'form',
	            		 bodyStyle: 'background: #f1f1f1;border:none',
	            		 items:[{
	            		 	xtype:'combo',
	            		 	fieldLabel:'交易币别',
	            		 	id:'currency',
	            		 	name:'currency',
	            		 	store:Ext.create('Ext.data.Store', {
	            				 fields: ['value', 'display'],
	            				 data : [{value:"RMB", display:"RMB"},
	            				         {value:"USD", display:"USD"}]
	            			 }),
						    queryMode: 'local',
						    displayField: 'display',
						    valueField: 'value',
						    value:'RMB'
	            		 }]
	            	}],
	            	dockedItems: [{
					    xtype: 'toolbar',
					    dock: 'bottom',
					    items: [{
								xtype : 'tbtext',
								text : '汇总金额：0',
								name:'totalprice',
								id:'totalprice'
							}, '->',{
								xtype : 'button',
								text : '确认',
								name : 'confirmBuyBtn',
								id : 'confirmBuyBtn',
								iconCls : 'x-button-icon-confirm',
								cls : 'x-btn-gray'
							},{
					            xtype:'button',
					            text :'取消',
					            iconCls: 'x-button-icon-close',
							    cls: 'x-btn-gray',
							    handler : function(){
							       parent.Ext.getCmp('dlwin').close();					 
							    }
					      },'->']
					}]
	          }]
		}); 
		me.callParent(arguments); 
	}
});