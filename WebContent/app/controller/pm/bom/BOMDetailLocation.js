Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMDetailLocation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.BOMDetailLocation','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpSaveButton': {
    			afterrender:function(btn){
	   				 var statuscode=Ext.getCmp('bo_statuscode');
	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
	   		   			btn.hide(); 
	   				 }
  			 	},
				click: function(btn){
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				afterrender:function(btn){
	   				 var statuscode=Ext.getCmp('bo_statuscode');
	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
	   		   			btn.hide(); 
	   				 }
  			 	},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bd_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender:function(btn){
	   				 var statuscode=Ext.getCmp('bo_statuscode');
	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
	   		   			btn.hide(); 
	   				 }
  			 	},
				click: function(btn){
					me.GridUtil.onUpdate(Ext.getCmp('grid'));
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBOMDetailLocation', '新增BOM物料位号', 'jsps/pm/bom/BOMDetailLocation.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=bd_soncode]': {
    			afterrender: function(t){
    				t.dbKey = "bd_bomid";
    				t.mappingKey = "bd_bomid";
    				t.dbMessage = "请先选择BOMID!";
    			}
    		},
    		'dbfindtrigger[name=bd_bomid]': {
    			aftertrigger:function(field){  
  	   				 var statuscode=Ext.getCmp('bo_statuscode');
  	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){   
  	   					 Ext.getCmp('updatebutton').hide(); 
  	   					 Ext.getCmp('deletebutton').hide();
  	   				 } else{  
  	   					 Ext.getCmp('updatebutton').show();
  	   					 Ext.getCmp('deletebutton').show(); 
  	   				 }
    			}
    		},
			'field[name=bd_id]': {
				change: function(f){
					if(f.value != null && f.value != ''){
						Ext.getCmp('deletebutton').show();
						Ext.getCmp('updatebutton').show();
						//Ext.getCmp('savebutton').hide();
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
							caller: caller,
							condition: 'bdl_bdid=' + f.value
						});
						
					} else {
						//Ext.getCmp('deletebutton').hide();
						//Ext.getCmp('updatebutton').hide();
						//Ext.getCmp('save').show();
					}
				}
			}
		});
	}, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});