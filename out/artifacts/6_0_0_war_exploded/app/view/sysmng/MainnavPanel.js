Ext.define('erp.view.sysmng.MainnavPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.mainnavpanel', 
	id:'mainnavpanel',
	layout:'card',
	bodyBorder: true,
	border: false,
	autoShow: true, 
	ActiveIndex_:0,
	bodyStyle:'background-color:#f1f1f1;',	
	defaults:{
		layout:'fit'
	},
	initComponent : function(){ 
		var me=this;
		Ext.applyIf(me,{
			items:me.getItems()
		});
		this.callParent(arguments);
		//this.setTitle(this.getLayout().getActiveItem().desc);
	},
	getItems:function(){
		var array=[{
			desc: '基础设置',
			xtype:'erpBasicSetPanel',
			layout:'border'
		},{
			desc: '功能升级',
			type:'Upgrade'			
		},{
			desc: '知会消息',
			type:'Message'			
		}
		
		
		];
	return array;
	}
});