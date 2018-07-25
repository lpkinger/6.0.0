Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.SellerChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.SellerChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.trigger.MultiDbfindTrigger',
      		'core.button.Audit','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.SpecialContainField','core.button.Close'
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
    			click: function(btn){
    				var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber(caller);
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){ 
    				me.FormUtil.onAdd('addSellerChange', '业务员变更', 'jsps/scm/purchase/sellerChange.jsp?whoami='+caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'combo[name=sc_type]':{
    			afterrender:function(field){
    				var sc_id=Ext.getCmp('sc_id');
    				if(sc_id && sc_id.value != 0 ){//选择类型保存后不能修改
    					field.hideTrigger=true; 
    					field.editable=false;
    				}
    			},
    			beforequery:function(field){//选择类型保存后不能修改
    				var sc_id=Ext.getCmp('sc_id');
    				if(sc_id && sc_id.value != 0 ){
    					field.hideTrigger=true;
    					return false;
    				}
    			},
    			change:function (field){
    				var href=window.location.href;
    				var arrstr=href.split("?");
    				if(formCondition!=""){
    					window.location.href =arrstr[0]+'?whoami=SellerChange!'+field.value+ '&formCondition=' + 
       					formCondition + '&gridCondition=' + gridCondition+'&source='+field.value;
    				}else window.location.href =arrstr[0]+'?whoami=SellerChange!'+field.value+ '&source='+field.value;
    				
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