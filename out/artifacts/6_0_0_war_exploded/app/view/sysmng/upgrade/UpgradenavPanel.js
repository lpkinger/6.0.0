Ext.define('erp.view.sysmng.upgrade.UpgradenavPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.upgradenavpanel', 
	id:'upgradenavpanel',
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
		//Ext.getCmp('upgradenavpanel').setTitle(activeItem.desc);
	},
	getItems:function(){
		var array=[{
			desc: '功能标识',
			xtype:'VersionSetPanel'
		},{
			desc: '升级SQL',
			xtype:'upgradSqlTab'			
		}];
	return array;
	}
});