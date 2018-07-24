Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.LabelParameter', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.LabelParameter','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({	    		   
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    			      Ext.getCmp("lp_laid").setValue(lp_laid);
	    			      var error = me.beforeCheck();
	    			      if(error !=''){
	    			         showError(error);
	    			         return ;
	    	              }
	    			      me.FormUtil.beforeSave(me);
	    			   }
	    		   },	    		 
	    		   'combo[id=lp_encode]':{
	    		   	 afterrender:function(){
	    		   	 	me.hide();
	    		   	 	
	    		   	 }
	    		   },	    		   
                  'textfield[id=lp_notealignjustify]':{
		    		   	afterrender: function (){
		    		   		if(Ext.getCmp("lp_valuetype").value == 'barcode'){
		    		   			me.show();
		    		   		}
		    		   	}
	    		   	},

	    		   'combo[id=lp_valuetype]':{
	    		   	 select : function(combo,records, eOpts ){
	    		   	 	if(records[0].data.value == 'barcode'){	 
	    		   	 		me.show();
	    		   	 	}else{	    		   	 		
	    		   	 		me.hide();
	    		   	 	}
	    		   	 }
	    		   },
	    		   'erpDeleteButton' : {	    			
	    			   click: function(btn){	    			   	
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('lp_id').value)});
	    			   },
	    			   afterrender: function(btn){
	    			   	   var status = window.parent.Ext.getCmp('form').statuscodeField;			   	   
	    			   	   var statuscode = window.parent.Ext.getCmp(status);
	    			       if(statuscode && statuscode.value != 'ENTERING'){
	    					  btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    			   	 var status = window.parent.Ext.getCmp('form').statuscodeField;			   	   
	    			   	 var statuscode = window.parent.Ext.getCmp(status);
	    			   	if(statuscode && statuscode.value != 'ENTERING'){
	    					btn.hide();
	    				}
	    			   },
	    			   click: function(btn){
	    			   	  var error = me.beforeCheck();
	    			      if(error !=''){
	    			         showError(error);
	    			         return ;
	    	              }
	    			     this.FormUtil.onUpdate(this);
	    			   }
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
	       beforeCheck: function(){
	         var error='';
             //判断必填字段的内容是否合法
	    	 if(Ext.getCmp("lp_valuetype").value != 'barcode'){
	    		  Ext.getCmp("lp_encode").logic = 'ignore';
		    	  Ext.getCmp("lp_ifshownote").logic = 'ignore';		    	 
		    	  Ext.getCmp("lp_notealignjustify").logic = 'ignore';
	    	}else{	    		  
	    		  if(Ext.getCmp("lp_encode").value == ''){	    			      	
	    			   error += "编码方式不允许为空！" ;
	    		  }
	    	      if(Ext.getCmp("lp_ifshownote").value == ''){
	    			   error +="是否注释不允许为空！";
	    		 }
	    		 if(Ext.getCmp("lp_notealignjustify").value ==''){
	    		 	  error +="请选择对齐方式！";
	    		 }
	        }
	        return error;	  
	     }	,
	     hide :function(){
	     	Ext.getCmp("lp_encode").hide();
	    	Ext.getCmp("lp_ifshownote").hide();
	    	Ext.getCmp("lp_notealignjustify").hide();	    	
	     },
	     show : function(){
	     	Ext.getCmp("lp_encode").show();
	    	Ext.getCmp("lp_ifshownote").show();	    		   	 		
	    	Ext.getCmp("lp_notealignjustify").show();
	     }
});