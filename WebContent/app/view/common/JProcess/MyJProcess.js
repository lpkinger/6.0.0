Ext.define('erp.view.common.JProcess.MyJProcess',{ 
	extend: 'Ext.Viewport', 
	/*layout: 'fit', */
	hideBorders: true, 
	layout:'anchor',
	initComponent : function(){ 
		var me = this; 
		/*var em_code = 'A013';*/
		/*console.log(em_code);*/
	/*	Ext.Ajax.request({
		    url: basePath +'common/findMyGroupTasks.action',
		   params: {
		        em_code: em_code
		    },
		    callback : function(options,success,response){
		    	console.log("hshshshshs");
		        var text =  Ext.decode(response.responseText);
		        console.log(text);
		        if(text.success){
		        	console.log("hahahahha");
		        	
			    }
		     
		   }
		    
		});*/
		Ext.apply(me, { 
			items: [{ 
					id:'myJprocess', 
					title:'',
					anchor: '100% 100%',
					html :'<iframe id="iframe_maindetail_" src="../../jsps/common/datalist.jsp?whoami=JProcess&urlcondition=JP_NODEDEALMAN=\''+em_code+'\' AND jp_flag = 1" height="100%" width="100%" frameborder="0"></iframe>',
				}]
		});
		 me.callParent(arguments); 
	}
});