Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ECR', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.ECR','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','pm.bom.ECRChangeGrid',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.form.FileField',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Upload','core.button.DownLoad','core.button.Print',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger', 'core.button.Sync'
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
					var form = me.getForm(btn),s = Ext.getCmp('ecr_prodstage');					
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();
					}
				/*	//万利达grid保存
					var ecr_tempb=Ext.getCmp('ecr_tempb'),ecr_tempc=Ext.getCmp('ecr_tempc');
					if(ecr_tempb && ecr_tempc){
						var grid=Ext.getCmp('ecrchangegrid');
						var data=grid.getReturnData(grid);
						if(data.tempb!='' && data.tempc!=''){
							ecr_tempb.setValue(data.tempb);
							ecr_tempc.setValue(data.tempc);
    	                 }						
					}*/
				   this.FormUtil.beforeSave(this);
					
				}
			},
			'textareafield[name=ecr_tempb]':{
				beforerender:function(field){
					field.labelAlign='top';
					field.height=250;
					field.fieldStyle=field.fieldStyle+';font-weight:700;font-color:#0A0A0A;';
				}
			},
			'textareafield[name=ecr_tempc]':{
				beforerender:function(field){
					field.labelAlign='top';
					field.height=250;
					field.fieldStyle=field.fieldStyle+';font-weight:700;font-color:#0A0A0A;';
				}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
					var form = me.getForm(btn),s = Ext.getCmp('ecr_prodstage');
					if(s) {
						var source = s.value;
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.getSetting(function(s){
								if(s) {
									if(source == 'ECN'){
										me.BaseUtil.getRandomNumber('ECR!ECN');//自动添加编号
									} else if(source == 'DCN'){
										me.BaseUtil.getRandomNumber('ECR!DCN');//自动添加编号
									}
								} else {
									me.BaseUtil.getRandomNumber();
								}
							});
	    				}
					} else {
						if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.BaseUtil.getRandomNumber();
						}
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addECR', '新增设计变更申请单', 'jsps/pm/bom/ECR.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ecr_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ecr_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="ECR";
				var condition='{Ecr.ecr_id}='+Ext.getCmp('ecr_id').value+'';
				var id=Ext.getCmp('ecr_id').value;
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
    				me.FormUtil.onResAudit(Ext.getCmp('ecr_id').value);
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#ecr_checkstatuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
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