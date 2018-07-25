Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.B2BOutSource', {
    extend: 'Ext.app.Controller',
    views:['scm.sale.B2BOutSource'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'panel': {
    			activate: function(p){
    				if(p.anchorCls=='x-tip-anchor-top'){
    					return;
    				}
    				var iframe = p.getEl().down('iframe').dom;
					var win = iframe.contentWindow;
					if(win == null || win.Ext === undefined) {
						return;
					}
					if(contains(iframe.src, 'common/datalist.jsp', false)) {//列表
						var grid = win.Ext.getCmp("grid");
						if(grid){
							grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
							grid.getCount();
						}
					} 
    			}
    		}
    	});
    }
});