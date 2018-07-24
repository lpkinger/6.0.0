Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.MeetingChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.meeting.MeetingChange','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger',
  			'core.form.ConDateHourMinuteField'
  	],
	init:function(){
		var me = this;
		Ext.override(erp.view.core.form.ConDateHourMinuteField, {
    		setValue: function(value){
    			this.value=value;
    		}
    	});
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'textfield[name=mc_macode]': {
    			aftertrigger: function(field){
    				if(field != null && field != ''){
        				Ext.getCmp('mc_newmrcode').setValue(Ext.getCmp('mc_oldmrcode').value);
        				Ext.getCmp('mc_newmrname').setValue(Ext.getCmp('mc_oldmrname').value);
        				Ext.getCmp('mc_newstarttime').setValue(Ext.getCmp('mc_oldstarttime').value);
        				Ext.getCmp('mc_newendtime').setValue(Ext.getCmp('mc_oldendtime').value);
        				var cdmf=Ext.getCmp('mc_newstarttime'),cdmf2=Ext.getCmp('mc_oldstarttime');
        				var endtime=Ext.getCmp('mc_oldendtime').value;
        				cdmf.items.items[0].setValue(cdmf.value.substring(0,10));
        				cdmf.items.items[1].setValue(cdmf.value.substring(11,16));
        				cdmf2.items.items[0].setValue(cdmf2.value.substring(0,10));
        				cdmf2.items.items[1].setValue(cdmf2.value.substring(11,16));
        				cdmf.items.items[3].setValue(endtime.substring(0,10));
        				cdmf.items.items[4].setValue(endtime.substring(11,16));
        				cdmf2.items.items[3].setValue(endtime.substring(0,10));
        				cdmf2.items.items[4].setValue(endtime.substring(11,16));
        				
    				}
    			}
    		},
    		/*'condatehourminutefield':{
    			setValue:function(value){
    				console.log(value);
    			}
    		},*/
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定				
					var start=new Date(Ext.getCmp('mc_newstarttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('mc_newendtime').value);
    				var myDate = new Date();//当前时间
    				if(start-end>0){
    					showError('新开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过新开会时间，请确认后重新输入');
    					}else{
    						this.FormUtil.beforeSave(this);
    					}
    				}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var start=new Date(Ext.getCmp('mc_newstarttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('mc_newendtime').value);
    				var myDate = new Date();//当前时间
    				if(start-end>0){
    					showError('新开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过新开会时间，请确认后重新输入');
    					}else{
    						this.FormUtil.onUpdate(this);
    					}
    				}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMeetingChange', '新增会议室申请变更', 'jsps/oa/meeting/meetingChange.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var start=new Date(Ext.getCmp('mc_newstarttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('mc_newendtime').value);
    				var myDate = new Date();//当前时间
    				if(start-end>0){
    					showError('新开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过新开会时间，请确认后重新输入');
    					}else{
    						me.FormUtil.onSubmit(Ext.getCmp('mc_id').value);
    					}
    				}
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var start=new Date(Ext.getCmp('mc_newstarttime').items.items[5].value);//新开会开始时间
    				var myDate = new Date();//当前时间
    				if(myDate-start>0){
    					showError('当前时间已超过新开会时间，无法审核');
    				}else{
					me.FormUtil.onAudit(Ext.getCmp('mc_id').value);
    				}
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mc_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
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