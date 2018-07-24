Ext.QuickTips.init();
Ext.define('erp.controller.ma.SysCheckSet', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','ma.SysCheckSet','core.grid.Panel4','core.toolbar.Toolbar3','core.form.MultiField','core.form.YnField','core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.PrintA4','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn'
  			],
    init:function(){
    	var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'textfield[name=sf_setemcode]':{
    			afterrender:function(field){
    				if(!field.value){
    					field.setValue(em_code);
    				}
    			}
    		},
    		'erpSaveButton':{
    			click:function(btn){
    				me.save(btn);
    			}
    		},
    		'erpUpdateButton':{
    			click:function(btn){
    				me.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton':{
    			click:function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sf_id').getValue());
    			}
    		},
    		'erpCloseButton':{
    			click:function(btn){
    				me.FormUtil.beforeClose(this);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
	    var data=me.GridUtil.getGridStore();
	   if(data.length>0){
		   Ext.Ajax.request({
			  method:'post',
			  url:basePath+'ma/vastUpdateSysCheckFormula.action',
			  params:{
				  data: unescape(data.toString().replace(/\\/g,"%"))
			  },
		      callback:function(options,success,response){
		    	  var localJson = new Ext.decode(response.responseText);
		    	  if(localJson.success){
		    		  saveSuccess(function(){
		    			  var grid =Ext.getCmp('grid');
		    			  me.GridUtil.loadNewStore(grid,{caller:caller,condition:'1=1'});
		    		  });
		    	  }
		      }
		   });
	   }
	 
	}
});