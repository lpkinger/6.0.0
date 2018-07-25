Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.LoadedPlans', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : ['core.grid.Panel2','fs.loaded.LoadedPlans'],
	init : function() {
		var me = this;
		this.control({
			'tabpanel':{
				afterrender: function(tab){
					var main = parent.Ext.getCmp("content-panel");
					if(main){
						var panel = main.getActiveTab(); 
						if(panel){
							panel.on('titlechange',function(panel , newtitle , oldtitle){
								if(panel.tabConfig.tooltip!=newtitle){
									panel.setTitle(panel.tabConfig.tooltip);
								}
							});
						}
					}
				}
			}
		})
	}
});