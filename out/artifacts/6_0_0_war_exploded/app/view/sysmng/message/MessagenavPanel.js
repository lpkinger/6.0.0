Ext.define('erp.view.sysmng.message.MessagenavPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.messagenavpanel', 
	id:'messagenavpanel',
	layout:'card',
	bodyBorder: true,
	border: false,
	autoShow: true, 
	ActiveIndex_:0,
	bodyStyle:'background-color:#f1f1f1;',	
	requires : ['erp.view.sysmng.message.MessageGridPanel','erp.view.sysmng.message.MessageAddPanel'],
	initComponent : function(){ 
		var me=this;
		me.FormUtil=Ext.create('erp.util.FormUtil');
    	me.GridUtil=Ext.create('erp.util.GridUtil');
    	me.BaseUtil=Ext.create('erp.util.BaseUtil');		
		Ext.applyIf(me,{
			items:me.getItems()
		});
		this.callParent(arguments);
	},
	changeCard:function(panel,direction,index){
		var layout = panel.getLayout();
		console.log(index)
		if(index==0){
			layout.setActiveItem(index);
			var iframe=Ext.getCmp('iframe');
			var mgp=Ext.getCmp('content-panel');
			mgp.setActiveTab(iframe); 
		}else{
			this.FormUtil.onAdd('addMessageDetail', '新增', 'jsps/sysmng/messagedetail.jsp');				
		}

	},
	getItems:function(){
		var array=[{
			desc: '设置',
			xtype:'messagegrigpanel'
		},
		{
			desc: '新增',
			xtype:'messageaddpanel'
		},
		
		];
	return array;
	}
});