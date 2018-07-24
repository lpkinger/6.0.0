Ext.QuickTips.init();

Ext.define('erp.view.common.messageCenter.JProcessCenterFormPanel', {
	extend:'Ext.form.Panel',
	alias:'widget.erpJProcessCenterFormPanel',
	bodyCls : 'x-panel-body-gray',
	activeCls : 'x-btn-switch-active',
	layout:'column',
	margin:0,
	id:'centerForm',
	cls:'form',
	//title:'流程中心',
	processType:'',
	likestr:'',
	items:[{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>流程类型:</b>',
		},{
			xtype:'erpSwitchButton',
			defaults:{
				scale : 'medium',
				width : 110,
				margin:'0 14px 0 0',
				groupName:'taskType',
			},
			items:[{
				xtype : 'erpStatButton',
				text : '我接收的待审批',
				type:'toDo',
				id:'toDo',
				active : true
			    
			},{
				xtype : 'erpStatButton',
				text : '我接收的已审批',	
				type:'alreadyDo',
				id:'alreadyDo',
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				text : '我发起的未审批',	
				type:'alreadyLaunchUndo',
				id:'alreadyLaunchUndo',
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				text : '我发起的已审批',
				type:'alreadyLaunchDone',
				id:'alreadyLaunchDone',
				cls:'btnhide'
			},{
				xtype : 'erpStatButton',
				text : '我未发起的流程',	
				type:'toLaunch',
				id:'toLaunch',
			},

			]
		}		
		]
	}]
});
