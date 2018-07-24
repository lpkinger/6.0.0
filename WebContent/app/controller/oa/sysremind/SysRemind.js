Ext.QuickTips.init();
Ext.define('erp.controller.oa.sysremind.SysRemind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','common.CommonPage','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber'
    ],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				/*
    				 * 获取到开始时间，截止时间，录入时间，然后对时间进行判定
    				 * */
    				var form = me.getForm(btn);
    				var beginTime=Ext.getCmp('no_begintime').value;
    				var endTime=Ext.getCmp('no_endtime').value;
    				var today=Ext.getCmp('no_apptime').value;
    				if(endTime<beginTime){ 
    					showError('开始时间不能晚于结束时间');
    				}else if(beginTime<today){
    					Ext.Msg.alert('操作错误','起始时间不能早于今天');
    				}else{
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpUpdateButton':{
    			click: function(btn){   				
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton':{
    			click: function(btn){   				
    				me.FormUtil.onAdd('addSysRemind', '新增登录提醒', 'jsps/oa/sysremind/sysremind.jsp?whoami=SysRemind');
    			}
    		},
    		'erpDeleteButton':{   			   			
    				click: function(btn){   				
        				me.FormUtil.onDelete(Ext.getCmp('no_id').value);
        			}
    			}   		
    		});
    	},
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
});