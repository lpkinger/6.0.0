Ext.define('erp.view.common.JProcess.JProcessAutoAudit.Viewport', {
	extend : 'Ext.Viewport',
	id : 'viewport',
	border : false,
	bodyBorder : false,
	autoScroll:true,
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		var flowbody=parent.Ext.getCmp("flowbody");
		Ext.apply(me, {
			items : [{
				xtype:'tbtext',
				height:40,
				text:'流程:<font color="red"><span id="protitle">' + flowbody.processtitle +'<span></font>'+'&nbsp;&nbsp;&nbsp;节点:<font color="red"><span id="curnode">' + flowbody.currentnode + '<span></font>',
				style:'margin:0 auto;text-align:center;line-height:40px;font-size:14px;',
				id:'toolbar',
				anchor:'100% 0%'
			}, {
				xtype : 'fieldset',
				title : '<span style="font-weight:bold;font-size:13px;">新需求申请</span>',
				id : 'newapplyfieldset',
				collapsible : true,
				collapsed : false,
				border : 0,
				items : [{
					xtype : 'NewrRequireApply'
				}]
			}, {
				xtype : 'fieldset',
				title : '<span style="font-weight:bold;font-size:13px;">规则选择</span>',
				collapsible : true,
				collapsed : false,
				id : 'chooserulesfieldset',
				border : 0,
				items : [{
					xtype : 'ChangeRules',
					height : 210
				}]
			}, {
				xtype : 'fieldset',
				title : '<span style="font-weight:bold;font-size:13px;">明细</span>',
				collapsible : true,
				collapsed : false,
				border : 0,
				items : [{
					xtype : 'OtherRules'
				}]
			}]
		});
		me.callParent(arguments);
	}
});