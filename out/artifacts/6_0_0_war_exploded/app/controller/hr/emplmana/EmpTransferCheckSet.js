Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.EmpTransferCheckSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'hr.emplmana.EmpTransferCheckSet','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
   	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'dbfindtrigger[name=field]':{
	    		beforetrigger: function(t){
	 			  var table = Ext.getCmp('tablename').value;
				   if(!table) {
				    	showError("请先选择单据caller!");
				    	return false;
				    }else{
				    	t.dbBaseCondition = " table_name = '" + table.split(' ')[0].toUpperCase()+"'";
				    }
	    		}
    		},
    		'dbfindtrigger[name=field_rel]':{
	    		beforetrigger: function(t){
	 			 	var record = Ext.getCmp('grid').selModel.selected.first();
   					if(!record || !record.data['table_rel']) {
				    		showError("请先选择关联表!");
				    		return false;
				    }else {
				    		t.dbBaseCondition = " table_name = '" +record.data['table_rel'].split(' ')[0].toUpperCase() + "'";
				   }
	    		}
    		},
    		'field[name=table_rel]':{
	    		/*change: function(t){
	 			  t.setValue(t.value.split(' ')[0].toUpperCase());
	    		}*/
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid');
					var record = grid.getStore();
					var items = record.data.items;
					for(var i=0;i<items.length;i++){
						var allowupdate = items[i].data['allowupdate'];
						var field_rel = items[i].data['field_rel'];
						var link = items[i].data['link'];
						if(allowupdate==true){
							var updatesql = items[i].data['updatesql'];
							if(!updatesql){
								showError("关联字段为："+field_rel+"的对应字段更新语句不允许为空");
								return;
							}
						}
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var caller=Ext.getCmp('caller').value;
    				var tablename=Ext.getCmp('tablename').value;
    				var field=Ext.getCmp('field').value;
    				var type=Ext.getCmp('type').value;
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
						item.set('caller',caller);
						item.set('tablename',tablename);
						item.set('field',field);
						item.set('type',type);
					});
					var record = grid.getStore();
					var items = record.data.items;
					for(var i=0;i<items.length;i++){
						var allowupdate = items[i].data['allowupdate'];
						var field_rel = items[i].data['field_rel'];
						if(allowupdate==true){
							var updatesql = items[i].data['updatesql'];
							if(!updatesql){
								showError("关联字段为："+field_rel+"的对应字段更新语句不允许为空");
								return;
							}
						}
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		/*'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('to_id').value);
    			}
    		},*/
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('EmpTransferCheckSet', '新增人员交接检测设置', 'jsps/hr/emplmana/employee/empTransferCheckSet.jsp');
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