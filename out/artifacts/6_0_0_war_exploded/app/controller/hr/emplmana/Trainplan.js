Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Trainplan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
		'hr.emplmana.Trainplan','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.YnField','core.form.MultiField',
		'core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.DeleteDetail',   		
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'dbfindtrigger[name=tp_ttname]':{
    			aftertrigger:function(trigger){
    				var code=Ext.getCmp('tp_ttcode').value;
    				var grid = Ext.getCmp('grid');
    				var a=grid.store.data.items;
    				var count=0;
    				Ext.Array.each(a, function(d){
    					if(d.data['ti_tcname']!=''){
    					    count++;
    					}
    				});
			    	Ext.Ajax.request({//查询数据
		  					url : basePath + '/hr/emplmana/getTrainingCourse.action',
							params:{
						 		 code:code
							},
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);													
						 		if(res.data){
									for(var i=0;i<res.data.length;i++){
										var record = grid.view.store.data.items[count];
										var r=res.data[i];
										Ext.Array.each(Ext.Object.getKeys(r), function(k){
											record.set(k, r[k]);
										});
										count++;
									}
								 } else if(res.exceptionInfo){
							    	 showError(res.exceptionInfo);
								 }
						 }
					 });
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
    				me.FormUtil.onDelete(Ext.getCmp('tp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTrainingPlan', '新增个人培训计划', 'jsps/hr/emplmana/train/trainplan.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('tp_id').value);
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