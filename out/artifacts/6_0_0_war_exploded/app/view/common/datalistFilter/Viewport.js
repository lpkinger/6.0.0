Ext.define('erp.view.common.datalistFilter.Viewport',{ 
	extend: 'Ext.Viewport',
	layout:'fit',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 			    	        
			items :[{
				layout: 'border', 
				height:'90%',
				items:[{	    	  
					xtype:'panel',
					region: 'west',
					layout:'fit',
					width:250,
					items:[{
						xtype: 'FilterTreePanel',
						width:'100%',
						height:'100%'
					}]
				},
				{
					xtype: 'FilterTabPanel',	
					region: 'center'	    	 	
				}],
				dockedItems:[{
					xtype:'toolbar',
					dock : 'bottom',
					defaults:{
						cls:'x-btn-gray'
					},
					items:[{
						text:"保存",
						id:'saveButton',
					    margin:'0 0 0 5',
						iconCls:'saveButtonico'
					},{
						text:"另存为",
						id:'saveAsButton',
						margin:'0 0 0 3',
						iconCls:'saveAsButtonico'
					},{
						text:"删除",
						id:'deleteButton',
						margin:'0 0 0 3',
						iconCls:'deleteButtonico'
					},{
						xtype:'checkbox',
						boxLabel:'默认方案',
						id:'setDefaultButton',
						cls:'setDefaultButton'
					},{xtype:'tbfill',cls:null},{
						xtype:"button",
						text:"确定",
						margin:'0 3 0 0',
						cls:'btn-footer',
						iconCls:'sureIcon',
						id:'sureButton'
					},{
						xtype:"button",
						text:"取消",
						iconCls:'cancelIcon',	
						margin:'0 3 0 0',
						cls:'btn-footer',
						handler:function(){
							parent.Ext.getCmp('searchwin').close();
						}
					}] 
				}]
			}]
		});
		me.callParent(arguments); 
	}
});