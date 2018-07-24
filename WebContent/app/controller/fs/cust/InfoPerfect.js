Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.InfoPerfect', {
	extend : 'Ext.app.Controller',
	views : ['fs.cust.InfoPerfect'],
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