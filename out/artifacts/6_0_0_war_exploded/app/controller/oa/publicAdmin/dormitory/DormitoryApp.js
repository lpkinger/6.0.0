Ext.QuickTips.init();
Ext.define('erp.controller.oa.publicAdmin.dormitory.DormitoryApp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.publicAdmin.dormitory.DormitoryApp','core.form.Panel','core.grid.Panel2','core.button.Scan',
		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.DeleteDetail',
		'core.button.ResAudit','core.button.Flow','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
		'core.form.YnField','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar','core.form.FileField'
	],
    init:function(){
    	var me = this;
        	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(grid){
    				
    				var str = me.GridUtil.getGridStore();
    				if(str != null || str != ''){//说明grid加载时带数据
    					me.alloweditor = false;
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('da_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addDormitoryApp', '新增宿舍申请', 'jsps/oa/publicAdmin/dormitory/dormitoryApp.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);;
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('da_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('da_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('da_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('da_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('da_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('da_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('da_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('da_id').value);
    			}
    		},
    		'dbfindtrigger[name=da_bedno]': {
    			afterrender:function(trigger){
    			trigger.dbKey='da_doid';//dbfind表 关联表 的ID
    			trigger.mappingKey='dd_doid';//匹配  主从表关联ID
    			trigger.dbMessage='请先选择房间号！';
    			}
    		},
    		'htmleditor': {
    			afterrender: function(f){
    				f.setHeight(500);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	if(this.alloweditor){
    		this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});