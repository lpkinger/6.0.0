Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MpsOption', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.MpsOption','core.toolbar.Toolbar','core.form.FtField','core.button.ResSubmit','core.button.ResAudit',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(this);
    			}
    		},
    		'textfield[name=mo_kind]':{
    		 afterrender:function(field){
    		 if(field.value==''){
    		  field.setValue(me.BaseUtil.getUrlParam('kind'));
    		  }
    		 }
    		},
    		'checkbox': {
    		   beforerender:function(checkbox){
    		   if(checkbox.initialConfig.value=="1"){
    		      checkbox.setValue("true");
    		      checkbox.originalValue="true";
    		   }
    		   }	  
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);    				
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("mo_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp("mo_id").value);
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMRPOption', '创建MPS运算方案', 'jsps/pm/mps/MpsOption.jsp?kind=MPS');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("mo_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp("mo_id").value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("mo_statuscode");
    				console.log(status);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp("mo_id").value);
    			}
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("mo_statuscode");
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp("mo_id").value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("mo_statuscode");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp("mo_id").value);
    			}
    		},
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form=Ext.getCmp('form');
		Ext.each(form.items.items, function(item){
				if(item.xtype == 'checkbox'){
				   item.dirty=true;
					if(item.checked){
					item.inputValue='1';					
					}else item.inputValue='0';
				}
			});
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	changeGrid: function(trigger){
		var grid = Ext.getCmp('grid');
		Ext.Array.each(grid.store.data.items, function(item){
			item.set('tm_prjid',trigger.value);
		});
	}
	
});