Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.TrainResult', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
		'hr.emplmana.TrainResult','core.form.Panel','core.form.YnField','core.form.MultiField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
		,'core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Submit',
		'core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Close'		
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=tr_exam]':{
    			change:function(f){
    				if(f.value){
    					Ext.getCmp('tr_score').show();
    				}else{
    					Ext.getCmp('tr_score').hide();
    				};
    			}
    		},
    		'field[name=tr_tagrade]':{
    			afterrender:function(f){
    				f.setMaxValue(100);
    			}
    		},
    		'field[name=tr_tascore]':{
    			afterrender:function(f){
    				f.setMaxValue(100);
    			}
    		},
    		'field[name=tr_score]':{
    			afterrender:function(f){
    				f.setMaxValue(100);
    			}
    		},
    		'dbfindtrigger[name=ti_tcname]':{
				beforetrigger:function(t){
					t.autoDbfind = false;
    				var em_code =Ext.getCmp('tr_emcode').value; console.log(em_code);
    				t.dbBaseCondition = "tp_emcode='" + em_code + "'";
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
    		'erpCloseButton': {
    			afterrender:function(){
	    			var exam=Ext.getCmp('tr_exam').value;
	    			if(exam){
	    				Ext.getCmp('tr_score').show();
	    			}else{
	    				Ext.getCmp('tr_score').hide();
	    			}
    			},
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('tr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTrainingResult', '新增培训反馈', 'jsps/hr/emplmana/train/trainResult.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('tr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('tr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('tr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('tr_id').value);
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