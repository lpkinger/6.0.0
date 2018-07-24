Ext.QuickTips.init();

Ext.define('erp.view.common.messageCenter.TaskCenterFormPanel', {
	extend:'Ext.form.Panel',
	alias:'widget.erpTaskCenterFormPanel',
	bodyCls : 'x-panel-body-gray',
	activeCls : 'x-btn-switch-active',
	layout:'column',
	margin:'0',
	id:'centerForm',
	cls:'form',
	//title:'任务中心',
	defaultCondition:'',
	likestr:'',
	tbar: [{xtype : 'button',	
			text: '发起任务',
			cls: 'x-btn-top',
			iconCls: 'x-button-icon-benchadd',
			id:'addTaskBtn'
		}],
	items:[{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>任务类型:</b>',
		},{ 
			xtype:'erpSwitchButton',
			id:'type',
			defaults:{
				scale : 'medium',
				width : 80,
				margin:'0 14px 0 0',
				groupName:'taskType',
			},
			items:[{
				xtype : 'erpStatButton',
				text : '日常任务',
				id:'normalTask',
				active : true,			
				condition:"ra_type in ('billtask','communicatetask')"
			},{
				xtype : 'erpStatButton',
				text : '项目任务',	
				group:2,
				id:'projectTask',
				condition:"ra_type in ('projecttask','worktask')"
			}]
		}]
	},{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>场景:</b>',
		},{
			xtype:'erpSwitchButton',
			id:'scene',
			defaults:{
				scale : 'medium',
				width : 80,
				margin:'0 14px 0 0',
			},
			items:[{
				xtype : 'erpStatButton',
				id:'myTask',
				text : '我接收的',
				active : true,
				condition:"ra_resourcecode='"+emcode+"'"
			},{
				xtype : 'erpStatButton',
				text : '我发出的',
				id:'fromMe',
				condition:"(recorderid is null and recorder='" + emname + "' or recorderid='"+emuu+"')"
			},{
				xtype : 'erpStatButton',
				text : '我下属的',	
				id:'staff',
				condition:'staff=1'
			}]
		}]
	},{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>状态:</b>',
			cls:'task'
		},{
			xtype:'erpSwitchButton',
			id:'status',
			defaults:{
				scale : 'medium',
				width : 80,
				margin:'0 14px 0 0',
			},
			items:[{
				xtype : 'erpStatButton',
				text : '待处理',
				id:'doing',
				active : true,
				//condition:"ra_taskpercentdone<100 AND nvl(ra_statuscode,' ')<>'ENDED' and nvl(ra_statuscode,' ')<>'UNACTIVE' or (recorderid="+emuu+" and ra_statuscode='UNCONFIRMED') "
			},{
				xtype : 'erpStatButton',
				text : '已处理',	
				id:'finished',
				condition:"ra_taskpercentdone=100 AND nvl(ra_statuscode,' ')='FINISHED'"
			}]
		}]
	}]
});
