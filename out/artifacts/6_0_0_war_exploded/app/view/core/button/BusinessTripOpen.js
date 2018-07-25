/**
 * 差旅开通账号
 */	
Ext.define('erp.view.core.button.BusinessTripOpen',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBusinessTripOpenButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'businessTripOpenButton',
    	text: '差旅开通账号',
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var grid = Ext.getCmp('batchDealGridPanel');
		    var items = grid.selModel.getSelection();
		    var emcodes='';
	        if(items.length>0){
	        	Ext.Array.each(items,function(i){
	        		emcodes+=i.data.em_code+',';
	        	});
	          emcodes = emcodes.substring(0,emcodes.length-1);
	    	  Ext.Ajax.request({
	    		 url:basePath+'common/form/BusinessTripOpen.action',
	    		 params:{
	    			 emcodes:emcodes
	    		 },
	    		 callback : function(options,success,response){
	 			   	var localJson = new Ext.decode(response.responseText);
	 			   	if(localJson.exceptionInfo){
	 			   		showMessage("提示",localJson.exceptionInfo);
	 			   	}
	 		    	if(localJson.success){
	 		    		showMessage("提示","开通成功，请查看人员资料操作日志");
	 			   	}
	 			 }
	    	  });
	       }else{
			  showError("没有勾选需要行,请勾选");
		   }
		}
	});