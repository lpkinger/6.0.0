Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectTG', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.ProjectTG','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.grid.YnColumn','core.button.Scan','core.trigger.TextAreaTrigger',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    			'core.trigger.MultiDbfindTrigger','core.form.MultiField','core.button.TurnReturn'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpTurnReturnButton':{
    			beforerender:function(btn){
    				btn.setText('复制表单');
    			},
    			click:function(){
    				Ext.Ajax.request({
 			        	url : basePath + 'plm/project/copyProjectTG.action',
 			        	params: {
 			        		id: Ext.getCmp('prj_id').value
 			        	},
 			        	method : 'post',
 			        	callback : function(options,success,response){
     			        	var res = new Ext.decode(response.responseText);
     			        	if(res.exceptionInfo){
     			        		showError(res.exceptionInfo);return;
     			        	} 
     			        	if(res.log)
    							showMessage('提示', res.log);
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
    				if(!me.validate()){
						showError('结束时间不能小于开始时间!');
						return;
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				if(caller=='Project!TG'){
    					me.FormUtil.onAdd('addProjectTG', '新增市场推广立项申请', 'jsps/plm/project/projectTG.jsp');
    				}
    				if(caller=='Project!DY'){
    					me.FormUtil.onAdd('addProjectDY', '新增市场调研立项申请', 'jsps/plm/project/projectTG.jsp?whoami=Project!DY');
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				if(!me.validate()){
						showError('结束时间不能小于开始时间!');
						return;
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){    			
    				me.FormUtil.onDelete((Ext.getCmp('prj_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('prj_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('prj_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('prj_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('prj_id').value);
    			}
    		},
    		'field[name=prj_start]': {
	 			   blur:function(f){
	 				   var endtime=Ext.getCmp('prj_end').value;
	 				   if(endtime&&endtime!=''&&f.value&&f.value!=''){
	 					   Ext.getCmp('prj_number1').setValue((endtime-f.value)/(24*3600*1000)+1);
	 				   }
	 			   }
	   		},
	   		'field[name=prj_end]': {
	   			blur:function(f){
	 				   var startime=Ext.getCmp('prj_start').value;
	 				   if(startime&&startime!=''&&f.value&&f.value!=''){
	 					   Ext.getCmp('prj_number1').setValue((f.value-startime)/(24*3600*1000)+1);
	 				   }
	 			   }
	   		},
    		'field[name=prj_customername]': {
	 			   afterrender:function(f){
	 				  if(caller=='Project!DY'){
	 					 f.setFieldStyle({
		   					 'color': 'blue'
		   				  });
		   				  f.focusCls = 'mail-attach';
		   				   var c = Ext.Function.bind(me.openRelative, me);
		   				   Ext.EventManager.on(f.inputEl, {
		   					   mousedown : c,
		   					   scope: f,
		   					   buffer : 100
		   				   });
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
	},
	openRelative:function(e, el, obj){
		if(Ext.getCmp('prj_customername').value=='')return;
		var tpcode=Ext.getCmp('prj_customercode').value;
		var url='jsps/crm/marketmgr/marketresearch/preView.jsp?_noc=1&formCondition=rt_codeIS'+tpcode;
		this.FormUtil.onAdd('ReportTemplates!PreView', '模板预览', 
				url);
		
	},
	validate:function(){
		if(caller=='Project!DY'&&(Ext.getCmp('prj_start').value>Ext.getCmp('prj_end').value)){
			return false;
		}
		return true;
	}
});