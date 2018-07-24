Ext.QuickTips.init();

Ext.define('erp.view.common.messageCenter.InformationForm', {
	extend:'Ext.form.Panel',
	alias:'widget.erpInformationForm',
	bodyCls : 'x-panel-body-gray',
	activeCls : 'x-btn-switch-active',
	layout:'column',
	margin:0,
	id:'informationform',
	cls:'form',
	//title:'消息中心',
	closable:false,
	draggable:false,
	likestr:'',
	defaultCondition:'',
	defaultfields:'',
	defaulttype:'',
	items:[{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>消息分类:</b>',
		},{
			xtype:'erpSwitchButton',
			id:'type',
			defaults:{
				scale : 'medium',
				width : 80,
				margin:'0 14px 0 0',
				groupName:'infoType',
			},
			items:[{
				xtype : 'erpStatButton',
				text : '全部',
				type:'all',
				id:'allbutton',
				condition:"1=1",
				active : true,			
			},{
				xtype : 'erpStatButton',
				id:'processbutton',
				type:'process',
				text : '审批',		
				condition:"IH_FROM ='process'",
				
			},{
				xtype : 'erpStatButton',
				id:'taskbutton',
				type:'task',
				text : '任务',	
				condition:"IH_FROM ='task'",
			},{
				xtype : 'erpStatButton',
				id:'notebutton',
				text : '通知公告',	
				type:'note',
				condition:"IH_FROM ='note'",
			},{
				xtype : 'erpStatButton',
				id:'systembutton',
				text : '知会消息',	
				type:'system',
				condition:"nvl(IH_FROM,' ') not in ('process','task','note','b2b')"
			},{
				xtype : 'erpStatButton',
				id:'b2bbutton',
				text : 'B2B商务',	
				type:'b2b',
				condition:"IH_FROM ='b2b'"
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
				text : '我接收的',
				id:'recived',
				active : true,	
				condition:"IHD_RECEIVEID =" + emuu,
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				text : '我发出的',	
				id:'send',
				condition:"IH_CALLID ='"+emuu+"' and IHD_RECEIVE is not null",
				cls:'btnhide'
			}]
		}]
	},{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>状态:</b>',
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
				text : '全部',
				id:'allstatus',
				condition:"1=1",
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				id:'unread',
				text : '未读',	
				active : true,	
				condition:"IHD_READSTATUS =0",
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				text : '已读',
				id:'alreadyread',
				condition:"IHD_READSTATUS =-1",
				cls:'btnhide'
			}]
		}]
	}]
});


