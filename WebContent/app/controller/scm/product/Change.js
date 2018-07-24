Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.Change', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
	'scm.product.Change','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
	'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
	'core.button.Update','core.button.Delete','core.form.YnField','core.form.MultiField',
	'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
	'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Productleveldetail',
	'core.trigger.MultiDbfindTrigger'
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
    				me.FormUtil.onDelete(Ext.getCmp('cs_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('add'+caller, '新增', 'jsps/scm/product/change.jsp?whoami='+caller);
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cs_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cs_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cs_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cs_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cs_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cs_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cs_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cs_id').value);
				}
			},
			'dbfindtrigger[name=csd_prodcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				if(caller=='Change!WLJY'){
    					t.dbBaseCondition = "pr_status<>'已禁用'";
    				} else if(caller=='Change!WLFJY'){
    					t.dbBaseCondition = "pr_status='已禁用'";
    				}
    			}
    		},
    		'dbfindtrigger[name=csd_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				if(caller=='Change!PLXG'){
    					var cs_class = Ext.getCmp('cs_class').value;
    					if(cs_class=='转无效'){
    						t.dbBaseCondition = "ppd_status='有效' and pp_status='已审核'";
    					} else if(cs_class=='转有效'){
    						t.dbBaseCondition = "ppd_status='无效' and pp_status='已审核'";
    					} else if(cs_class=='转未认定'){
    						t.dbBaseCondition = "ppd_appstatus='合格' and pp_status='已审核'";
    					} else if(cs_class=='转合格'){
    						t.dbBaseCondition = "(ppd_appstatus='未认定' or ppd_appstatus is null) and pp_status='已审核'";
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=csd_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				if(caller=='Change!PLXG'){
    					var cs_class = Ext.getCmp('cs_class').value;
    					if(cs_class=='转无效'){
    						t.dbBaseCondition = "ppd_status='有效' and pp_status='已审核'";
    					} else if(cs_class=='转有效'){
    						t.dbBaseCondition = "ppd_status='无效' and pp_status='已审核'";
    					} else if(cs_class=='转未认定'){
    						t.dbBaseCondition = "ppd_appstatus='合格' and pp_status='已审核'";
    					} else if(cs_class=='转合格'){
    						t.dbBaseCondition = "(ppd_appstatus='未认定' or ppd_appstatus is null) and pp_status='已审核'";
    					}
    				}
    			}
    		},
    		'combo[name=cs_class]':{
    			change:function(f){
    				var grid = Ext.getCmp('grid');
    				grid.store.loadData([{csd_detno:1}]);
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