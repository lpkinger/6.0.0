Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectDoc', {
    extend: 'Ext.app.Controller',
    views:['plm.project.ProjectDoc','plm.project.ProjectDocTree'],
        init:function(){
        	var me = this;
        	this.control({      	
        		'erpProjectFileTree' : {
					beforeitemclick:function(tree,record,item,index,e,eOpts){
						if(e.target.className.indexOf('x-tree-expander')>-1){
							return true;
						}
						return false;
					}
        		}
        	});
        }
});