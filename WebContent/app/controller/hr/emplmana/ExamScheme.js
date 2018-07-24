Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.ExamScheme', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.emplmana.ExamScheme','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('es_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addExamScheme', '新增试卷生成方案', 'jsps/hr/emplmana/question/examScheme.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('es_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('es_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('es_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('es_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('es_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('es_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('es_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('es_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'multidbfindtrigger[name=esd_eqcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['esd_eqtype'];
    				if(code == null || code == ''){
    					showError("请先选择题型！");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "eq_type='" + code + "'";
    				}
    			},
    			aftertrigger:function(t){
    				var grid = t.owner,
    				store = grid.store;
    				var length = store.data.items.length-1;
    				for(var i = 0;i<length;i++){
    					if(store.data.items[i].data['esd_eqcode']){
    						store.data.items[i].data['esd_number']=1;
    					}
    				}
    				grid.getView().refresh();
    			}
    		},
    		'dbfindtrigger[name=esd_eqcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['esd_eqtype'];
    				if(code == null || code == ''){
    					showError("请先选择题型！");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "eq_type='" + code + "'";
    				}
    			}
    		}
    		,'field[name=esd_number]':{
				change:function(field,newValue,oldValue,eOpts ){
    				if(newValue > 1){
    					var record = Ext.getCmp('grid').selModel.getLastSelected();
    					if(record.data['esd_eqcode']){
    						field.setValue(1);
    					}
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