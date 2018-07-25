Ext.QuickTips.init();
Ext.define('erp.controller.ma.DBfindSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.DBfindSet','core.form.Panel','core.grid.Panel2',
   		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.form.YnField',
   			'core.button.Upload','core.button.Update','core.button.Delete','core.button.DeleteDetail','core.button.ResAudit','core.grid.YnColumn',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.DlcCaller',
   		'core.button.CopyByConfigs'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addDBFindSet', '新增DBFindSet设置', 'jsps/ma/dbFindSet.jsp');
				}
			},
    		'erpUpdateButton':{
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton':{
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('ds_id').getValue());
    			}
    		},
    		'combo': { 
    			change: this.onChange 
    		},
    		'erpGridPanel2': {
    			itemclick: this.GridUtil.onGridItemClick
    		},
    		'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'fo_table'){
    					this.changeGrid(trigger);
    				}
    			}
    		},
    		'erpDlcCallerButton':{
    			click:function(btn){
    				var tablename=Ext.getCmp('ds_tablename');
    				var form=me.getForm(btn);
    				var items=Ext.getCmp('grid').store.data.items;
    				var fields='';
    				Ext.each(items,function(i){
    					if(i.data['dd_fieldtype']=='C'){
    						fields+="'"+i.data['dd_fieldname']+"',";
    					}
    				});
    				if(fields.length>0&&tablename&&tablename.value!=''){
    					fields=fields.substring(0,fields.length-1);
    					var con="'"+tablename.value.toUpperCase().replace(/\s+(LEFT|RIGHT)\s+JOIN\s+/g,"','")
			    					.replace(/(\s+ON\s+)([A-Z0-9$_.]+)(\s*=\s*)([A-Z0-9$_.]+)/g,"")
			    					.replace(/((\s+AND\s+)([A-Z0-9$_.]+)(\s*=\s*)([A-Z0-9$_.]+))/g,"")+"'";
    					form.setLoading(true);
    					Ext.Ajax.request({
								url : basePath +'common/getDlccallerByTables.action',
								params: {
									table:con,
									fields:fields
								},
								async: false,
								method : 'post',
								callback : function(options,success,response){
									form.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										showError(res.exceptionInfo);return;
									}
									if(res.success){
										var arr=arr1=arr2=new Array();
										var s1=Ext.getCmp('ds_dlccaller').value;
										arr1=s1.split(',');
										var s2=res.callers;
										var arr2=s2.split(',');
										var arr=Ext.Array.union(arr1,arr2);
										arr=Ext.Array.remove(arr,'');
										Ext.getCmp('ds_dlccaller').setValue(arr.join(','));				                 
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
	onChange: function(field, value){
		field.setValue(value);
		if(value == 'C'){
			
		}
	},
	changeGrid: function(trigger){
		var grid = Ext.getCmp('grid');
		Ext.Array.each(grid.store.data.items, function(item){
			item.set('fd_table',trigger.value);
		});
	}
});