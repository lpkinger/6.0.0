Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CustCreditRatingApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.credit.CustCreditRatingApply', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.form.YearDateField',
	         	'core.form.FileField','core.form.YnField','core.form.MultiField',
				'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit',
				'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
				'core.button.TurnProject','core.button.CopyAll','core.button.ChangeResponsible',
				'core.button.ManualEvaluation','core.button.ModelScore','core.button.CustTargets',
				'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger', 'core.grid.YnColumn', 'core.form.StatusField'],
	init : function() {
		var me = this;
		this.control({
			'textfield[id=cra_recorder]': {
    			afterrender: function(field){
    				var status = Ext.getCmp('cra_statuscode');
					if (status && status.value == 'ENTERING'&&field.value=='') {
						field.setValue(emname);
					}
    			}
    		},
			'erpAddButton': {
    			click: function(){
    				if(caller=='CustCreditRatingApply'){
    					me.FormUtil.onAdd('CustCreditRatingApply', '企业经营风险', 'jsps/fs/credit/custCreditRatingApply.jsp?whoami=CustCreditRatingApply');
    				}else{
    					me.FormUtil.onAdd('CustCreditRatingApply!Moral', '企业信用风险', 'jsps/fs/credit/custCreditRatingApply.jsp?whoami=CustCreditRatingApply!Moral');
    				}
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){ 
    				var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号						
					}
					this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cra_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					if(caller=='CustCreditRatingApply'&&Ext.isEmpty(Ext.getCmp('cra_yearmonth').value)){
						showError('财务报表年份不能为空！');
						return;
					}
					me.FormUtil.onSubmit(Ext.getCmp('cra_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cra_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cra_id').value);
				}
			},
			'erpManEvalButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value != 'COMMITED'&&caller=='CustCreditRatingApply') {
						btn.hide();
					}
				},
				click : function(btn) {
					var craid = Ext.getCmp('cra_id').value;
					me.createWindow('人工评定','CustCreditTargets',craid,'NOFINANCE');
				}
			},
			'erpModelScoreButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value == 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					var craid = Ext.getCmp('cra_id').value;
					me.createWindow('模型得分','CustCreditTargets',craid,'FINANCE');			
				}
			},
			'erpCustTargetsButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					
					if (status && status.value != 'AUDITED'&&status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					var craid = Ext.getCmp('cra_id').value;
					me.createWindow('信用指标得分','CustCreditTargets',craid);			
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cra_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cra_id').value);
				}
			},
			'field[name=cra_cuvename]':{
				afterrender: function(f){
					var issyscust = Ext.getCmp('cra_issyscust');
					if(issyscust&&issyscust.value == 0){
						f.setReadOnly(false);
					} else {
						f.setReadOnly(true);
					}
				}
			},
			'field[name=cra_issyscust]':{
				change: function(f){
					if(f.value == 0){
						Ext.getCmp('cra_cuvename').setReadOnly(false);
					} else {
						Ext.getCmp('cra_cuvename').setReadOnly(true);
					}
				}
			}
		})
	},
	getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
	createWindow:function(title,ccaller,id,type){
		var url = 'jsps/fs/credit/custCreditTargets.jsp?whoami='+ccaller+'&gridCondition=cct_craidIS'+id;
		if(type){
			url=url+'&type='+type;
		}
		
		var status = Ext.getCmp('cra_statuscode');
		if (caller=='CustCreditRatingApply!Moral'&&status && status.value != 'ENTERING') {
			url +='&readOnly=true';
		}else if(status && status.value == 'AUDITED'){
			url +='&readOnly=true';
		}
		
		var win = new Ext.window.Window({
			id: 'win',
			title:title,
			width : '70%',
			height : '85%',
			draggable : true,
			closable : true,
			modal : true,
			layout : 'fit',
			items: [{
		    	  tag : 'iframe',
		    	  frame : false,
		    	  layout : 'fit',
		    	  html : '<iframe src="'+ basePath + url+'" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>'
		    }]
		});
		win.show();
	}
});