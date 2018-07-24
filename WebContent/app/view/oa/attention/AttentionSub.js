Ext.define('erp.view.oa.attention.AttentionSub',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 	
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				 items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 30%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:caller, 	    			 				       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',	    				 
	    			   },{
	    				   xtype:'erpCloseButton',	    				   
	    			   },'->']         
	    		   },{
	    		     anchor:'100% 70%',
	    		     layout:'fit',
	    		     id: 'AttentionSubGrid',
	    		     xtype: 'AttentionSubGridPanel',
	    		     
	    		   }],
			}] 
		}); 
		me.callParent(arguments); 
	} 
});