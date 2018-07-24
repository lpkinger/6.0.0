Ext.QuickTips.init();

Ext.define('erp.view.oa.flow.flowCenter.flowCenterFormPanel', {
	extend:'Ext.form.Panel',
	alias:'widget.erpFlowCenterFormPanel',
	bodyCls : 'x-panel-body-gray',
	activeCls : 'x-btn-switch-active',
	layout:'column',
	margin:0,
	/*padding:'17px 17px 0 17px',*/
	id:'flowCenterForm',
	cls:'form',
	title:'流程中心',
	processType:'',
	likestr:'',
	tools: [{
		height:28,
		xtype : 'button',
		text:'新建流程',
		id:'addFlow',
		cls: 'x-button-addflow',
		iconCls: 'x-button-icon-add',
		width:90
	}],
	items:[{
		xtype:'container',
		columnWidth:1,
		layout:'column',
		items:[{
			xtype:'displayfield',
			value:'<b>流程类型:</b>'
		},{
			xtype:'erpSwitchButton',
			defaults:{
				scale : 'medium',
				width : 110,
				margin:'0 14px 0 0',
				groupName:'taskType'
			},
			items:[{
				xtype : 'erpStatButton',
				text : '我待处理的流程',
				type:'pending',
				id:'pending',
				active : true
			},{
				xtype : 'erpStatButton',
				text : '我已处理的流程',	
				type:'processed',
				id:'processed'
			},{
				xtype : 'erpStatButton',
				text : '我发起的流程',	
				type:'created',
				id:'created'
			}]
		}]
	}]
});
