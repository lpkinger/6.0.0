Ext.QuickTips.init();
Ext.define('erp.controller.oa.mail.Delete', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.mail.Delete','oa.mail.DeleteGroupGrid','oa.mail.MailPaging'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGroupGrid': {
    			select: function(selModel, record){
    				var id = record.data['ma_id'];
    				if(id != null && id != ''){
    		        	var panel = Ext.getCmp("mail" + id); 
    		        	var main = parent.Ext.getCmp("content-panel");
    		        	if(!panel){ 
    		        		var title = "";
    		        		var from = record.data['ma_from'];
    		    	    	if (from.toString().length>4) {
    		    	    		 title = from.toString().substring(0, 4);	
    		    	    	} else {
    		    	    		title = from;
    		    	    	}
    		    	    	panel = { 
    		    	    			title : title,
    		    	    			tag : 'iframe',
    		    	    			tabConfig:{tooltip: record.data['ma_subject']},
    		    	    			frame : true,
    		    	    			border : false,
    		    	    			layout : 'fit',
    		    	    			iconCls : 'x-tree-icon-tab-tab1',
    		    	    			html : '<iframe id="iframe_mail_' + id + '" src="' + basePath + "jsps/oa/mail/mailDetail.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    		    	    			closable : true,
    		    	    			listeners : {
    		    	    				close : function(){
    		    	    			    	main.setActiveTab(main.getActiveTab().id); 
    		    	    				}
    		    	    			} 
    		    	    	};
    		    	    	me.FormUtil.openTab(panel, "mail" + id); 
    		        	}else{ 
    		    	    	main.setActiveTab(panel); 
    		        	} 
    		    	}
    			}
    		},
    		'button[id=restore]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var records = grid.selModel.getSelection();
    				var id = new Array();
    				Ext.each(records, function(record){
    					id.push(record.data['ma_id']);
    				});
    				me.upadteMailStatus(id, 2);
    			}
    		}
    	});
    },
    upadteMailStatus: function(id, status){
    	Ext.Ajax.request({
        	url : basePath + 'oa/mail/updateMailStatus.action',
        	params: {
        		id: id,
        		status: status
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		Ext.getCmp('grid').getGroupData();
        	}
        });
    }
});