Ext.QuickTips.init();
Ext.define('erp.controller.common.DeskTop', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    views: [
    		'common.DeskTop.InfoRemindPortal',
    		'common.DeskTop.ViewPort', 'common.DeskTop.DeskPortal',
            'common.DeskTop.FlowPortal','common.DeskTop.PortalColumn','common.DeskTop.Portlet','common.DeskTop.CommonUsePortal','common.DeskTop.TaskPortal',
            'common.DeskTop.InfoPortal','common.DeskTop.CallPortal','common.DeskTop.KpiBillPortal','common.DeskTop.CommonPortal','common.DeskTop.Grid','common.DeskTop.InvitePortal'],
    init: function() {  
    	var me=this;
        this.control({
        	'deskportal':{
        		drop :function(node,data,overmodel){
                    me.modifyDesk(node);
        		}
        	},
        	'gridpanel':{
        		itemmousedown:function(){
        			return false;
        		}
        	}
        });
    },
    modifyDesk:function(node){
    	var columns=node.portal.items.items,nodes=new Array();
    	Ext.Array.each(columns,function(item,colIndex){
    		Ext.Array.each(item.items.items,function(portal,rowIndex){
    			nodes.push({
    				xtype_:portal.xtype_,
    				detno_:2*(rowIndex+1)-(colIndex+1)%2
    			})
    		});
    	});
       Ext.Ajax.request({
    	   url:basePath+'common/desktop/setDetno.action',
    	   params:{
    		 nodes:Ext.encode(nodes)
    	   }   
       });
    }
});