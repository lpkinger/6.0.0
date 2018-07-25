Ext.define('erp.view.fs.loaded.LoadedPlans',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'fs/loaded/getLoadedPlans.action',
        	params: {
        		pCaller: pCaller,
        		pid: pid,
        		type: type
        	},
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}else if(res.success){
        			if(res.plans.length>0){
        				var items = new Array();
        				Ext.Array.each(res.plans,function(plan){
        					var formCondition = '';
 							if(plan.id){
 								formCondition = '&formCondition=pt_idIS'+plan.id;
 							}
        					var url = 'jsps/fs/loaded/loadedPlan.jsp?whoami='+plan.caller+formCondition+'&title='+plan.title+'&psid='+plan.psid;
        					items.push({ 
								title : plan.title,
								tag : 'iframe',
								tabConfig:{tooltip:plan.title},
								border : false,
								layout : 'fit',
								html : '<iframe id="iframe_add_manage" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
							});
							Ext.apply(me, { 
								items: [{
									xtype:'tabpanel',
									id:'plans',
									items:items
								}]
							}); 
        				});
        			}
        		}
        	}
		});
		
		this.callParent(arguments); 
	}
});