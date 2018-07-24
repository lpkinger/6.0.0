Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.InternalSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.InternalSet','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.Close','core.button.Delete',
      		'core.button.Update',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField','core.trigger.CateTreeDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				//保存之前的一些前台的逻辑判定
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('is_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addInternalSet', '新增内部交易设置', 'jsps/fa/gla/internalSet.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'dbfindtrigger[name=is_asstype]': {
    			aftertrigger: function(t,record){
    				if(!t.dbfind||t.dbfind.ak_name!=record.data['ak_name']){
    					t.dbfind = record.data;
    					var asscode = Ext.getCmp('is_asscode');
    					var assname = Ext.getCmp('is_assname');
    					asscode && asscode.setValue(null);
    					assname && assname.setValue(null);
    				}
    			}
    		},
    		'dbfindtrigger[name=is_asscode]': {
    			beforetrigger: function(t){
    				var asstype = Ext.getCmp('is_asstype');
    				if(!asstype||Ext.isEmpty(asstype.value)){
    					showError('请先选择核算类型!');
						return false;
    				}else{
    					if(asstype.dbfind.ak_dbfind=='AssKindDetail'){
    						t.dbBaseCondition = 'ak_name=\'' + asstype.dbfind.ak_name + '\'';
    					}else{
    						t.dbBaseCondition = '';
    					}
    					t.column = true;
    					t.hidden = true;
    					t.dbfind = asstype.dbfind.ak_dbfind+'|' + asstype.dbfind.ak_asscode
    				}
    			},
    			aftertrigger: function(t,record){
    				var asstype = Ext.getCmp('is_asstype');
    				if(asstype){
    					t.setValue(record.data[asstype.dbfind.ak_asscode]);
    					var assname = Ext.getCmp('is_assname');
    					assname && assname.setValue(record.data[asstype.dbfind.ak_assname]);
    				}
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});