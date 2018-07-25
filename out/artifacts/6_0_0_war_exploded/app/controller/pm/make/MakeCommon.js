Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeCommon', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','pm.make.MakeCommon','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.button.SetMain' 		
  			],
    init:function(){
    	var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){    				
    				grid.setReadOnly(true);
    			},
    			itemclick:me.ItemClick
    		},
    		'erpSaveButton' :{
    			click:function(btn){
    				this.FormUtil.beforeSave(this);	
    			}    			
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				if(caller=='MakeBase!Sub'){
    					//需要传多个参数所以单独删除方法  	
    					me.deleteSub();
    				}
    			},
    			afterrender:function(btn){
    			  btn.setDisabled(true);
    			}
    		},
    	     'erpCloseButton':{
    	    	click:function(btn){
    	    		window.parent.location.reload();
    	    		parent.Ext.getCmp('win').close();
    	    	} 
    	     },
    		'erpAddButton': {
    			click: function(){
    				if(caller=='MakeBase!Sub'){
    				 Ext.getCmp('deletebutton').setDisabled(true);
    				 Ext.getCmp('mp_detno').setValue("");
    				 Ext.getCmp('mp_prodcode').setValue("");
    				 Ext.getCmp('pr_detail').setValue("");
    				 Ext.getCmp('pr_spec').setValue("");
    				 Ext.getCmp('mp_canuseqty').setValue("");
    				 Ext.getCmp('mp_warehouseid').setValue("");
    				 Ext.getCmp('mp_remark').setValue("");
    				}
    			},
    			afterrender:function(){
    				if(caller=='MakeBase!Sub'){
    				 Ext.getCmp('mp_detno').setValue("");
    				 Ext.getCmp('mp_prodcode').setValue("");
    				 Ext.getCmp('pr_detail').setValue("");
    				 Ext.getCmp('pr_spec').setValue("");
    				 Ext.getCmp('mp_canuseqty').setValue("");
    				 Ext.getCmp('mp_warehouseid').setValue("");
    				 Ext.getCmp('mp_remark').setValue("");
    				}    				
    			}
    		},
    	     'erpSetMainButton':{
    	     	afterrender:function(btn){
    			  btn.setDisabled(true);
    			},
     	    	click:function(btn){
     	    		if(caller=='MakeBase!Sub'){
     	    			Ext.Ajax.request({
        			   		url : basePath + 'pm/make/setMain.action',
        			   		params: {
        			   			mmid:Ext.getCmp('mm_id').value,
        			   			detno:Ext.getCmp('mp_detno').value
        			   		},
        			   		method : 'post',
        			   		callback : function(options,success,response){ 
        			   			var localJson = new Ext.decode(response.responseText);
        			   			if(localJson.exceptionInfo){
        			   				showError(localJson.exceptionInfo);
        			   			} else {
        			   				if(localJson.success){ 
        			   					parent.window.location.reload(); 
        			   					parent.Ext.getCmp('win').close(); 
        			   				}
        			   			}
        			   		}
        				});
     	    		} 
     	    	} 
     	    	 
     	    }
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	ItemClick:function(view ,record){
	 if(record.data["mp_prodcode"]!='' &&  record.data["mp_prodcode"]!=null){
		 if(caller=='MakeBase!Sub'){
			 Ext.getCmp('deletebutton').setDisabled(false);
			 Ext.getCmp('SetMain').setDisabled(false);
			var form=view.ownerCt.ownerCt.items.items[0];
			form.getForm().setValues(record.data);
		 }
		 } 
	},
	deleteSub:function(){
		var me = this;
		if(Ext.getCmp('grid').getStore().data.items.length == 0){
			showError("不存在需要删除的替代关系！");
			return ;
		}
		var form = Ext.getCmp("form");
		//获取主表数据mm_id，mp_detno
		var params = new Object();
		var r = new Object();
		r.mp_detno = Ext.getCmp("mp_detno").value;
		r.mm_id = Ext.getCmp("mm_id").value;
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		parent.Ext.getCmp('win').setLoading(true);
		Ext.Ajax.request({
			url : basePath+form.deleteUrl,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				parent.Ext.getCmp('win').setLoading(false);;
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示','删除成功!',1000);
	    			window.location.reload();
				}else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
			}
		});
	}
});