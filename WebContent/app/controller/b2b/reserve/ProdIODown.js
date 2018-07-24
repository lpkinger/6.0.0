Ext.QuickTips.init();
Ext.define('erp.controller.b2b.reserve.ProdIODown', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','b2b.reserve.ProdIODown','core.grid.Panel2','core.form.FileField','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.TurnExOut', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.button.Close'
	       
	       ],
	       init:function(){
	    	   var me = this;
	    	   var grid = Ext.getCmp('grid');
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({	    		  
	    		   'erpGridPanel2': { 
	    			   itemclick: this.onGridItemClick
	    		   },	    		  
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       onGridItemClick: function(selModel, record){//grid行选择	      	
	      	 this.GridUtil.onGridItemClick(selModel, record);
	      }
});