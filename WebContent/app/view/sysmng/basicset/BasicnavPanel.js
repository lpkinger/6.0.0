Ext.define('erp.view.sysmng.basicset.BasicnavPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.basicnavpanel', 
	id:'basicnavpanel',
	layout:'card',
	bodyBorder: true,
	border: false,
	autoShow: true, 
	ActiveIndex_:0,
	bodyStyle:'background-color:#f1f1f1;',	
	initComponent : function(){ 
		var me=this;
		Ext.applyIf(me,{
			items:me.getItems()
		});
		this.callParent(arguments);
	},
	changeCard:function(panel,direction,index){
		var layout = panel.getLayout();	
		var a=index;
		layout.setActiveItem(index);
		this.ActiveIndex_=index;
	    activeItem=layout.getActiveItem();
		//Ext.getCmp('basicnavpanel').setTitle(activeItem.desc);
	},
	getItems:function(){
		var array=[{
			desc: '冻结字段',
			xtype:'erpFixedPanel'
		},{
			desc: '数据字典',
			xtype:'erpDictionaryPanel'			
		}];
	return array;
	}
});