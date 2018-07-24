Ext.define('erp.view.scm.product.IntegratedQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpIntegratedQueryFormPanel",  
	    	anchor: '100% 25%'
	    },{
			region: 'south',         
			xtype: "erpIntegratedQueryGridPanel",  
	    	anchor: '100% 25%'
	    },{
			 anchor: '100% 50%', 
				xtype: "tabpanel",
				id:'tabpanel',
				minTabWidth:80,
				layout:'border',
				items:[{
				 title:'库存明细',
				 id:'ProductWh',
				 iconCls:'workrecord-log' ,
				/** items:[{
				 xtype:'DeskProductGridPanel1',
	               height:height,
				 }]**/
	             listeners:{
	               activate:function(tab){
	            caller='Desk!ProductWh';
	             var item={
	               itemId:'ProductWhgrid',
	               xtype:'DeskProductGridPanel1',
	               height:height,
	               };
	             var tabgrid=tab.getComponent('ProductWhgrid');
	               if(!tab.getComponent('ProductWhgrid')){
	                 tab.add(item);
	               }else{           	 
	              	 if(tabgrid.LastCondition!=BaseQueryCondition){
	              		 tabgrid.getCount("Desk!ProductWh",BaseQueryCondition);
	              		 tabgrid.LastCondition=BaseQueryCondition;
	              	 }
	                }
	               }            
	             }
				},
				{
				 title:'在  途',
				 iconCls:'workrecord-log' ,
				 id:'MPSPRonorder',
				 listeners:{
		               activate:function(tab){
		             caller='Desk!MPSPRonorder';
		             var item={
		               itemId:'MPSPRonordergrid',
		               xtype:'DeskProductGridPanel2',
		               height:height,
		               };
		             var tabgrid=tab.getComponent('MPSPRonordergrid');	        
		               if(!tabgrid){
		               item.LastCondition=BaseQueryCondition;	   
		               tab.add(item);
		               }else{
		            	 if(tabgrid.LastCondition!=BaseQueryCondition){
		            		 //说明条件更新了
		            		 tabgrid.getCount('MPSPRonordergrid',BaseQueryCondition);
		            		 tabgrid.LastCondition=BaseQueryCondition;
		            	 }
		                }
		               }            
		             }
				},
				{
				 title:'预  约',
				 id:'MakeCommit',
				 iconCls:'workrecord-log' ,
				listeners:{
	               activate:function(tab){
	             caller='Desk!MakeCommit';
	             var item={
	               itemId:'MakeCommitgrid',
	               xtype:'DeskProductGridPanel3',
	               height:height,
	               };
	             var tabgrid=tab.getComponent('MakeCommitgrid');
	               if(!tabgrid){
	               item.LastCondition=BaseQueryCondition;	   
	               tab.add(item);
	               }else{
	            	 if(tabgrid.LastCondition!=BaseQueryCondition){
	            		 tabgrid.getCount('Desk!MakeCommit',BaseQueryCondition);
	            		 tabgrid.LastCondition=BaseQueryCondition;
	            	 }
	                }
	               }            
	             }
				}]
		    	
		    }]
		});
		me.callParent(arguments); 
	}
});