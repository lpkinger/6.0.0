Ext.QuickTips.init();
Ext.define('erp.controller.oa.daily.dailyplan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.daily.dailyplan','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.form.FileField',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Upload','core.button.DownLoad','core.button.Print',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger',
      		'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Delete','core.button.Update','core.button.ImportExcel',
			'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Abate','core.button.ResAbate',
		     'core.trigger.DbfindTrigger','core.form.FileField',
		    'core.button.CopyAll'
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
					//form.dp_recordid.setValue(em_code);
					//console.log('formdata',form.dp_recorderid)
					Ext.getCmp('dp_recordername').setValue(em_name);
					//form.dp_recordername=(em_name);
                    if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
                        me.BaseUtil.getRandomNumber(); //自动添加编号
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
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('Daily', '新增日报', 'jsps/oa/daily/daily.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				Ext.getCmp('dp_committime').setValue(new Date());
    				me.FormUtil.onSubmit(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('dp_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="ECR";
				var condition='{Ecr.dp_id}='+Ext.getCmp('dp_id').value+'';
				var id=Ext.getCmp('dp_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('dp_id').value);
    			}
    		},
    		'dbfindtrigger[name=ecr_prodcode]': {
    			aftertrigger:function(trigger){
	    			var name=Ext.getCmp('pr_detail').value, spec=Ext.getCmp('pr_spec').value;
	    			if (Ext.getCmp('ecr_newprodname')){
	    				Ext.getCmp('ecr_newprodname').setValue(name);
		    			Ext.getCmp('ecr_newspec').setValue(spec);
	    			} 
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getSetting : function(fn) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'ECNType\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			var t = false;
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success && r.data){
    				t = r.data == 'true';
	   			}
    			fn.call(me, t);
	   		}
		});
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});