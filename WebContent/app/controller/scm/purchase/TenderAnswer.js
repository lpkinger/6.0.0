Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TenderAnswer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.form.Panel', 'scm.purchase.TenderAnswer', 'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.form.MultiField', 
    		'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.Print', 'core.button.PrintHK', 'core.button.PrintEn',
    		'core.button.Upload', 'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update',
    		'core.button.DeleteDetail', 'core.button.ResSubmit','core.button.Export','core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 
    		'core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField', 'core.form.FileField', 'core.button.PrintByCondition'],
    init: function() {
        var me = this;
        this.control({
            'dbfindtrigger[name=tendercode]': {
            	beforetrigger: function(field){
            		var tendercode = Ext.getCmp('tendercode');
            		if(formCondition&&tendercode&&tendercode.value){
            			field.findConfig = "code='" + tendercode.value+"'";
            		}
            	},
  			   	aftertrigger: function(trigger, record, dbfinds){
  			   		Ext.Ajax.request({
			        	url : basePath + 'scm/purchase/getQuestionsByTender.action',
			        	params: {
			        		tenderCode:trigger.value
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			window.location.reload();
			        		}else{
								var form = trigger.ownerCt;
				        		Ext.Array.each(form.items.items,function(field){
				        			if(field.name!=form.statusField&&field.name!=form.statuscodeField&&field.name!=form.codeField){
				        				if(typeof(field.setValue)=='function'&&res.tenderAnswer[field.name]){
					        				if(field.xtype=='datefield'){
						        				field.setValue(Ext.Date.format(new Date(res.tenderAnswer[field.name]),'Y-m-d'));
						        			}else if(field.xtype=='datetimefield'){
						        				field.setValue(Ext.Date.format(new Date(res.tenderAnswer[field.name]),'Y-m-d H:i:s'));
						        			}else{
						        				field.setValue(res.tenderAnswer[field.name]);
						        			}
					        			}
				        				var grid = form.ownerCt.down('gridpanel');
						        		grid.store.loadData(res.tenderQuestions);
						        		grid.needUpdate = true;
					        		}
					        	});
			        		}
			        	}
    				});
  			   }
            },
            'erpSaveButton': {
            	afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var param = me.GridUtil.getAllGridStore();
                	if(param.length<1){
                		showError('没有提问明细！');
                		return;
                	}
                	me.FormUtil.onSave(param);
                }
            },
            'erpDeleteButton': {
                click: function(btn) {
                    me.FormUtil.onDelete(Ext.getCmp('id').value);
                }
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var needUpdate = btn.ownerCt.ownerCt.ownerCt.down('gridpanel').needUpdate;
                	if(needUpdate){
                		var param = me.GridUtil.getAllGridStore();
	                	if(param.length<1){
	                		showError('没有提问明细！');
	                		return;
	                	}
	                	me.FormUtil.onSave(param);
                	}else{
                		me.FormUtil.onUpdate(me);
                	}
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addTenderAnswer', '新增答疑汇总单', 'jsps/scm/purchase/tenderAnswer.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn){	
	              	me.FormUtil.onSubmit(Ext.getCmp('id').value);
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('id').value);
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click:function(btn) {
                   	me.FormUtil.onAudit(Ext.getCmp('id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('auditstatuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click:function(btn) {
                	me.FormUtil.onResAudit(Ext.getCmp('id').value);
                }
            }
        });
    }
});