Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AttentionGrade', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.attention.AttentionGrade','oa.attention.AttentionGrid','oa.attention.Form','core.form.ColorField','core.button.Save','core.button.Close',
    		'core.form.ScopeField','core.trigger.HrOrgTreeDbfindTrigger',
    	],
    init:function(){
        var me=this;
    	this.control({ 
          'button[id=add]':{
            click:function(){
              me.addGrade(me);
            }          
          },
          'button[id=delete]':{
            click:function(){
              me.deleteGrade(me);  
             }          
           }
    	});
    },
    addGrade:function(me){
          var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '55%',
	    		   width: '45%',
	    		   title:'添加等级',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 100%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:'AttentionGrade', 
	    			   saveUrl:'oa/attention/saveAttentionGrade.action', 				       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',
	    				   handler:function(){
	    				       var form=Ext.getCmp('form');	    				     
	    				       form.save();
	    				       me.reLoadGrid();	    				       
	    					   Ext.getCmp('win').close();
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }],

	    	   });
	    	   win.show();	     
    },
    deleteGrade:function(me){
       var grid=Ext.getCmp('AttentionGridPanel')
	       var params=grid.getMultiSelected();
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/attention/deleteAttentionGrade.action',
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   				return "";
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "删除成功!", function(){
			   				grid.multiselected = new Array();
			   				   me.reLoadGrid();
			   				});
			   			}
			   		}
		   });
    },
    reLoadGrid:function(){	  
      var grid=Ext.getCmp('AttentionGridPanel');
	   var gridParam = {caller:'AttentionGrade', condition:'1=1'};
	   grid.loadNewStore(grid,gridParam);
    
    }

});