Ext.define('erp.view.scm.reserve.SelPrintTemplate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 	
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 							    					
				  anchor:'100% 100%',
				  xtype:'form',
				  id :'printLabelForm',
				  buttonAlign : 'center',
				  items:[{
				        xtype: 'combo',
						id: 'template',
						fieldLabel: '打印模板',
						store: Ext.create('Ext.data.Store', {
							autoLoad: true,
						    fields: ['la_code','la_id'],
						    proxy: {
					             type: 'ajax',
							     url : basePath + 'scm/reserve/getFields.action',				           
							     extraParams:{condition:caller},
							     reader: {
							          type: 'json',
							          root: 'datas'
							     },
							     headers: {
					                 'Content-Type': 'application/json;charset=utf-8'
					             }		                   
					           },
					          listeners:{
					          	load : function (store){
					          		Ext.getCmp('template').select(store.getAt(0));								
					          	}								
					           }
						}),
					    displayField: 'la_code',
					    valueField: 'la_id',
						width:200,
					    allowBlank:false,
						style:'margin-left:15px;margin-top:15px;',										
					}], 
					dockedItems:[{  	
				        xtype: 'toolbar',
						dock: 'bottom',
						layout: {  
		                 pack: 'center'   //放置位置  
		                },
						defaults: {
							style: {
								marginLeft: '10px',
								marginBottom:'15px'
							}
						},	
						items:[{
						    xtype: 'button',
							name: 'barPrint',
							id: 'barPrint',
							text: '打印',						
							cls: 'x-btn-gray',																				
							iconCls: 'x-button-icon-print',
							width:60,
							formBind: true//form.isValid() == false时,按钮disabled,
						},{
						    xtype: 'button',
							name: 'barPrintPreview',
							id: 'barPrintPreview',
							text: '打印预览',						
							cls: 'x-btn-gray',																				
							iconCls: 'x-button-icon-preview',
							width:100,
							formBind: true//form.isValid() == false时,按钮disabled,
						},{
							xtype: 'erpCloseButton'							
					   }]										
				 }]				    							    	     				    							           	
			}] 
		}); 
		me.callParent(arguments); 
	} 
});