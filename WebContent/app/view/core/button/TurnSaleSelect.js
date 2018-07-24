/**
 * 转出货按钮
 */	
Ext.define('erp.view.core.button.TurnSaleSelect',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnSaleSelectButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'turnout',
    	text: $I18N.common.button.erpTurnSaleButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		menu: [{//转销售
			iconCls: 'main-msg',
	        text: $I18N.common.button.erpTurnSaleButton,
	        listeners: {
	        	click: function(m){

    				warnMsg("确定要转入销售单吗?", function(btn){
    					if(btn == 'yes'){
    						
    						Ext.getCmp('turnout').turn( 'sale',Ext.getCmp('ps_id').value);
    					}
    					
    				});
    			
	        		
	        		
	        	}
	        }
	    },{//转销售预售
			iconCls: 'main-msg',
	        text: $I18N.common.button.erpTurnSaleYSButton,
	        listeners: {
	        	click: function(m){

    				warnMsg("确定要转入销售预售吗?", function(btn){
    					if(btn == 'yes'){
    						
    						Ext.getCmp('turnout').turn( 'ycsale',Ext.getCmp('ps_id').value);	    
    					}
    					
    				});
    			
	        		    		
	        	}
	        }
	    },{//转非正常销售订单
	    	iconCls: 'main-msg',
	        text: $I18N.common.button.erpTurnNonSaleButton,
	        listeners: {
	        	click: function(m){

    				warnMsg("确定要转入非正常销售单吗?", function(btn){
    					if(btn == 'yes'){
    						
    						Ext.getCmp('turnout').turn( 'nonsale',Ext.getCmp('ps_id').value);
    					}
    					
    				});
    			
	        		
	        	}
	        }
	    }],
        turn: function(type,id, url){
            Ext.Ajax.request({
                url: basePath + 'scm/sale/turnPreSaleToSale.action',
                params: {
                	type:type,
                	ps_id:id
                },
                waitMsg: '转单中...',
                method: 'post',
                callback: function(options, success, response) {
                    var localJson = new Ext.decode(response.responseText);
                    if (localJson.success) {
                    	
                    	turnSuccess(function() {
                            //add成功后刷新页面进入可编辑的页面 
//                            this.loadSplitData(originaldetno, said, record);
                        	if(localJson.clickurl){
                        		showError(localJson.clickurl);
                        		window.location.reload();
                        	}
                        });
                    } else if (localJson.exceptionInfo) {
                		showError(localJson.exceptionInfo);
                    } else {
                        saveFailure();
                    }
                }
            });
       }
	});