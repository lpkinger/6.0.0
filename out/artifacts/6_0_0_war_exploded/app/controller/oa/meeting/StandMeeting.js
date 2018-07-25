Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.StandMeeting', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.meeting.StandMeeting','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.button.ResBanned',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.trigger.MultiDbfindTrigger',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan','oa.meeting.StandMeetingMan','core.grid.YnColumn',
    			'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','oa.meeting.StandMeetingManbar','core.button.ConfirmMan',
    			'core.button.TurnDoc','core.form.HrOrgSelectField','oa.doc.OrgTreePanel','core.form.TimeMinuteField','core.button.Banned'
    	],
    init:function(){
    	var me = this;
    	this.control({    		
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				var bool=me.checkTime();
    				if(bool){
    					me.FormUtil.beforeSave(this);
    				}else{
    					showError('开始时间不能晚于结束时间');
    				}
    			}
    		},
    		'erpBannedButton':{
    			afterrender: function(btn){
    				var status=Ext.getCmp('sm_statuscode');
    				if(status&&status.value!='AUDITED'){
    					btn.hide();
    				}
				},
				click:function(btn){
					me.FormUtil.onBanned(Ext.getCmp('sm_id').value);
				}
    		},
    		'erpResBannedButton':{
    			afterrender: function(btn){
    				var status=Ext.getCmp('sm_statuscode');
    				if(status&&status.value!='BANNED'){
    					btn.hide();
    				}
				},
				click:function(btn){
					me.FormUtil.onResBanned(Ext.getCmp('sm_id').value);
				}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
    				var status=Ext.getCmp('sm_statuscode');
    				if(status&&status.value!='ENTERING'){
    					btn.hide();
    				}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sm_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status=Ext.getCmp('sm_statuscode');
    				if(status&&status.value!='ENTERING'){
    					btn.hide();
    				}
				},
				click: function(btn){
					var bool=me.checkTime();
    				if(bool){
    					me.FormUtil.onUpdate(this);
    				}else{
    					showError('开始时间不能晚于结束时间');
    				}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addStandMeeting', '新增例会', 'jsps/oa/meeting/standmeeting.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('sm_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sm_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('sm_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sm_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('sm_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sm_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('sm_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    checkTime: function(){
    	var start=(Ext.getCmp('sm_starttime').value).split(":");
		var end=(Ext.getCmp('sm_endtime').value).split(":");
		var start1=start[0]*60+(start[1]-0);
		var end1=end[0]*60+(end[1]-0);
		if(start1>=end1){
			return false;
		}else{
			return true;
		}
    }
});