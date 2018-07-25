Ext.QuickTips.init();
Ext.define('erp.controller.plm.change.ProjectTaskChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.change.ProjectTaskChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
      var me = this;
    	me.attachcount = 0;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'dbfindtrigger[name=ptc_oldtaskid]':{
    		  afterrender:function(trigger){
    		   trigger.dbKey='ptc_prjid';
    		   trigger.mappingKey='prjplanid';
    		   trigger.dbMessage='请先选择所属项目!';
    		  }    		
    		},
    		'dbfindtrigger[name=ptc_oldtaskname]':{
      		  afterrender:function(trigger){
      		   trigger.dbKey='ptc_prjid';
      		   trigger.mappingKey='prjplanid';
      		   trigger.dbMessage='请先选择所属项目!';
      		   
      		  }    		
      		},
    		'dbfindtrigger[name=ptc_proposer]':{
    		   afterrender:function(trigger){
    		   trigger.dbKey='ptc_prjid';
    		   trigger.mappingKey='tm_prjid';
    		   trigger.dbMessage='请选择该变更任务的项目计划ID';
    		  }    
    		},
    		 'dbfindtrigger[name=rc_oldresourcecode]': {
    			afterrender:function(trigger){
    			trigger.gridKey='ptc_oldtaskid';
    			trigger.mappinggirdKey='ra_taskid';
    			trigger.gridErrorMessage='请选择需变更的任务ID';
    			}
    		},
    		 'dbfindtrigger[name=rc_resourcecode]': {
    			afterrender:function(trigger){
    			trigger.gridKey='ptc_prjid';
    			trigger.mappinggirdKey='tm_prjid';
    			trigger.gridErrorMessage='请选择该变更任务的项目计划ID';
    			}
    		},
    		'datefield[name=ptc_startdate]':{
    			select:function(field){
    				var startdate=field.value;
    				var enddate=Ext.getCmp('ptc_enddate').value;
    				if(enddate){
    					var last=me.countDay(startdate,enddate);
    					Ext.getCmp('ptc_newduration').setValue(last);
    				}
    			}
    		},
    		'datefield[name=ptc_enddate]':{
    			select:function(field){
    				var enddate=field.value;
    				var startdate=Ext.getCmp('ptc_startdate').value;
    				if(startdate){
    					var last=me.countDay(startdate,enddate);
    					Ext.getCmp('ptc_newduration').setValue(last);
    				}  				
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('ptc_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectTaskChange', '新增任务变更', 'jsps/plm/change/ProjectTaskChange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var startdate=Ext.getCmp('ptc_startdate').value;
    				var enddate=Ext.getCmp('ptc_enddate').value;
    				var last=Ext.getCmp('ptc_newduration').value;
    				if(startdate&&enddate){
    					if(last<=0){
							showError('新的任务结束时间必须大于开始时间!');
    						return;
						}
    				}
    				if(startdate||enddate){		
    					if(!last){
    						showError('改变任务时间时,新开始时间与新结束时间都必须填写!');
    						return;
    					}
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('ptc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    		    afterrender:function(btn){
    		    	var status=Ext.getCmp('ptc_statuscode');
    		    	if(status && status.value!='COMMITED'){
    		    		btn.hide();
    		    	}    		    	
    		    },
    		    click:function (btn){
    		    	me.FormUtil.onResSubmit(Ext.getCmp('ptc_id').value);
    		    }
    		    
    		},
    	    'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ptc_id').value);
    			}
    		},
    		'erpResAuditButton':{
    			afterrender:function (btn){
    				var status=Ext.getCmp('ptc_statuscode');
    				if(status && status.value!='AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ptc_id').value);	
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
		me.FormUtil.beforeSave(me);
	},
	countDay:function(startdate,enddate){
		var day=(enddate-startdate)/(1000 * 60 * 60 * 24)+1;
		var weekend=0;
		for(i=0;i<day;i++){
			 if(startdate.getDay() == 0 || startdate.getDay() == 6) weekend++; 
			 startdate = startdate.valueOf(); 
			 startdate += 1000 * 60 * 60 * 24; 
			 startdate= new Date(startdate); 
			}
		return(day-weekend);
	}
});