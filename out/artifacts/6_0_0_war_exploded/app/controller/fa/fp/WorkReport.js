Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.WorkReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.WorkReport','core.form.Panel','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField','core.grid.Panel2',
		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
		'core.form.YnField','core.form.MultiField','core.button.DeleteDetail','core.trigger.DbfindTrigger',
		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
		'core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger', 'core.form.YnField','core.form.MonthDateField'
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
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWorkReport', '新工作日报', 'jsps/fa/fp/WorkReport.jsp');
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeSubmit(btn);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('wr_id').value);
    			}
    		}, 
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('wr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('wr_id').value);
    			}
    		},
    		'field[name=wr_emcode]':{
    			afterrender:function(f){
    				code=f.value;
    				if(Ext.getCmp('wr_id').value)
    				return;
    				var grid = Ext.getCmp('grid');
    				var a=grid.store.data.items;
    				var count=0;
    				Ext.Array.each(a, function(d){
    					if(d.data['wrd_worktype']!=''){
    					    count++;
    					}
    					console.log(d.data['wrd_worktype']);
    				});
    				
			    	Ext.Ajax.request({
		  					url : basePath + '/fa/fp/getJobWork.action',
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
			}
	
    	});
    },
    beforeSubmit:function(btn){
    	var me = this;
    	me.FormUtil.onSubmit(Ext.getCmp('wr_id').value);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
	}
});
