Ext.define('erp.view.core.button.LoadVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadVendorButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'loadV',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	text: $I18N.common.button.erpLoadVendorButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100, 
        listeners : {
        	click : function(btn){
        		warnMsg('重新加载供应商，会清除以前的供应商分配比例的数据，是否要继续?', function(btn1){
				if(btn1 == 'yes'){										
        		var prodcode=Ext.getCmp('pr_code').value;    	
        		console.log(basePath +'scm/purchase/loadProductVendor.action');
        		Ext.Ajax.request({
		   		url : basePath +'scm/purchase/loadProductVendor.action',
		   		params: {
    	    			  prodcode: prodcode,
    	    			  caller: caller
    	    			 },
		   		method : 'post',
		   		timeout: 6000000,
		   		callback : function(options,success,response){
		   			var res = new Ext.decode(response.responseText);		   			
		   			var data = res.data;
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        					pv_vendcode: d.PV_VENDOR,
        					pv_vendname : d.VE_NAME,
        					pv_currency :d.PV_CURRENCY ,
                			pv_price : d.PV_PRICE,
                			pv_taxrate:d.PV_TAXRATE,
                			pv_prodcode:d.PPD_PRODCODE,
                			pv_prodid:d.PR_ID
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        	   }
		      	}
        		}
             );
             } else {
						return;
					}
				});
        	}
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});