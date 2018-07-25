Ext.QuickTips.init();
Ext.define('erp.controller.common.EditorColumn', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.RenderUtil', 'erp.util.GridUtil'],
	views:[
	       'common.editorColumn.GridPanel',
	       'common.editorColumn.Viewport',
	       'core.trigger.DbfindTrigger',
	       'core.grid.YnColumn'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({ 
	    		   'erpEditorColumnGridPanel': { 
	    			   storeloaded: function(grid){
	    				   me.getProductWh(grid);
	    			   }
	    		   },
	    		   'dbfindtrigger[name=pw_whcode]':{
	    			   focus: function(t){
	    				   t.setHideTrigger(false);
	    				   t.setReadOnly(false);
	    				   var record = t.record;
	    				   var code = record.data['sd_prodcode'];
	    				   if(caller =='Sale!ToAccept!Deal'){
	    					   t.dbBaseCondition ="pw_prodcode='" + code + "'";
	    				   }
	    			   }
	    		   },
	    		   'dbfindtrigger[name=sd_whcode]':{
	    			   focus: function(t){
	    				   t.setHideTrigger(false);
	    				   t.setReadOnly(false);
	    				   var record = t.record;
	    				   var code = record.data['sd_prodcode'];
	    				   if(caller =='Sale!ToAccept!Deal'){
	    					   t.dbBaseCondition ="pwd_prodcode='" + code + "'";
	    				   }
	    			   }
	    		   }
	    	   });
	       },
	       getProductWh: function(grid) {
	    	   var prodfield = grid.getProdField();
	    	   if(prodfield) {
	    		   var codes = [];
	    		   grid.store.each(function(d){
	    			   codes.push("'" + d.get(prodfield) + "'");
	    		   });
	    		   Ext.Ajax.request({
	    			   url: basePath + 'scm/product/getProductwh.action',
	    			   params: {
	    				   codes: codes.join(',')
	    			   },
	    			   callback: function (opt, s, r) {
	    				   if(s) {
	    					   var rs = Ext.decode(r.responseText);
	    					   if(rs.data) {
	    						   grid.productwh = rs.data;
	    					   }
	    				   }
	    			   }
	    		   });
	    	   }
	       }
});