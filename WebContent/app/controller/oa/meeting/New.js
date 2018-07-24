Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.New', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.meeting.New','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': { 
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);
        			
        			}
        		},
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('addApplication', '新增会议室', 'jsps/oa/meeting/new.jsp');
        			}
        		},
        		/*'erpDatalistGridPanel': {
        			afterrender: function(grid){
        				grid.onGridItemClick = function(){//改为点击button进入详细界面
        					me.onGridItemClick(grid.selModel.lastSelected);
        				};
        			}
        		},*/
        		'erpSaveButton': {
        			click: function(btn){
        				var form = me.getForm(btn);
        				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
        					me.BaseUtil.getRandomNumber();//自动添加编号
        				}
        				var bool=me.checkTime();
        				if(bool){
        					this.FormUtil.beforeSave(this);
        				}else{
        					showError('开放开始时间不能晚于开放结束时间');
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
        				var bool=me.checkTime();
        				if(bool){
        					this.FormUtil.onUpdate(this);
        				}else{
        					showError('开放开始时间不能晚于开放结束时间');
        				}
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('mr_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mr_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				var bool=me.checkTime();
        				if(bool){
        					me.FormUtil.onSubmit(Ext.getCmp('mr_id').value);
        				}else{
        					showError('开放开始时间不能晚于开放结束时间');
        				}	
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mr_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('mr_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mr_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('mr_id').value);
        			}
        		},
        		'erpResAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('mr_statuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResAudit(Ext.getCmp('mr_id').value);
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
        checkTime: function(){
        	var start=(Ext.getCmp('mr_openstarttime').value).split(":");
			var end=(Ext.getCmp('mr_openendtime').value).split(":");
			var start1=start[0]*60+(start[1]-0);
			var end1=end[0]*60+(end[1]-0);
			if(start1>=end1){
				return false;
			}else{
				return true;
			}
        }
});