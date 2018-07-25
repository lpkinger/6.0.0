Ext.QuickTips.init();
Ext.define('erp.controller.ma.DBfindSetUI', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.DBfindSetUI','ma.DBfindSetUIForm','ma.DBfindSetUIGrid','core.trigger.DbfindTrigger','core.trigger.AddDbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	
    	this.control({
    		
    		'#DBfindSetUI':{
    			beforerender:function(){
    				var _copyConf=getUrlParam('_copyConf');
    				if (_copyConf) {
    					id=Ext.decode(_copyConf).keyValue;
    					ds_id=id;
    				}else{
	    				formCondition = getUrlParam('formCondition');		
						id = formCondition != null ? formCondition.replace(/IS/g, '=') : null;					
						ds_id = (id != null && id !="")? id.split('=')[1].replace(/'/g, ''):"";	    					
    				}
								
					if(ds_id!=null&&ds_id!='')
					{
						var localJson= this.getDbFindSetUI(ds_id);
					if(localJson!=null && localJson.griddata!=null){
						gridData=localJson.griddata;
						formData=localJson.formdata;
						findData=localJson.fields;
					}
					Ext.getCmp('DBfindSetUIForm').getForm().setValues(formData);
					Ext.getCmp('DBfindSetUIGrid').getStore().loadData(gridData);						
					}
					else {
						gridData=new Array();
						for(var i=0;i<10;i++){
							var o = new Object();
							gridData.push(o);
						}
													
					Ext.getCmp('DBfindSetUIGrid').getStore().loadData(gridData);		
				};					  				
    		}    		
    	}   	
    	});
    },
      
    getDbFindSetUI:function(id){
		var localJson=null;		
		Ext.Ajax.request({
			url : basePath +'ma/dbfindsetui/getData.action',
			params: {	
				id:id
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);				
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);
					return;
				}
				if(res.success){
					localJson=res;			
				}

			} 

		});
		return localJson;
	}   
});