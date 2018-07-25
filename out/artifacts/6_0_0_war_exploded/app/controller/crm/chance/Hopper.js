Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.Hopper', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['crm.chance.Hopper','crm.chance.HopperDraw','crm.chance.ChanceGrid'],
	init:function(){
		var me = this;
		this.control({
			'button[id=query]': {
				click: function(btn) {
					me.showFilterPanel(btn);
				}
			},
			'chancegrid':{
				itemclick:function(view,record,item){
					var grid = Ext.getCmp('chanceprocess');
					grid.getStore().load({params:{
						id:record.get('bc_id')
					}});
					grid.expand();
				}
			}
		
		});
	}
});