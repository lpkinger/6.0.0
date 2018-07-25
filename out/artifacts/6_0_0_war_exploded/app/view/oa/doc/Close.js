Ext.define('erp.view.oa.doc.Close',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpCloseButton',	
	id:'docclose',
	cls:'x-btn-close',
	text:'关闭',
	style: {
		marginLeft: '10px'
	},
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	handler: function(btn){
		var modal=parent.Ext.getCmp('modalwindow');
		if(modal){
			var history=modal.historyMaster;
			Ext.Ajax.request({
				url: basePath + 'common/changeMaster.action',
				params: {
					to: history
				},
				callback: function(opt, s, r) {
					if (s) {
						modal.close();
					} else {
						alert('切换到原账套失败!');
					}
				}
			});
		} else {
			var main = parent.Ext.getCmp("content-panel"); 
			if(main){
				main.getActiveTab().close();
			} else {
				var win = parent.Ext.ComponentQuery.query('window');
				if(win){
					Ext.each(win, function(){
						this.close();
					});
				} else {
					window.close();
				}
			}
		}
	}
});	