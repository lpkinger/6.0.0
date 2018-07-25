/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author shenj
 * @date 2015-05-05 16:26
 */	
Ext.define('erp.view.core.button.CustSendToProdInout',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCustSendToProdInoutButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'CustSendToProdInout',
    	tooltip: '转出库',    	
    	text: $I18N.common.button.erpCustSendToProdInoutButton,		
		width: 120,
		 menu: [{
				iconCls: 'main-msg',
		        text: $I18N.common.button.erpToSaleoutButton,
		        listeners: {
		        	click: function(btn){
		        		var form= Ext.getCmp("form");
		        		var id = Ext.getCmp("ss_id").value; 
						form.setLoading(true);
						warnMsg('确定要出库吗?', function(btn){
							if (btn == 'yes') {
								Ext.Ajax.request({
									url:basePath + "b2b/product/CustSendToSaleInout.action",
									params:{									
										formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
									},
									method:'post',
									callback:function(options,success,response){
									    form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											var id = res.id;
    	    		    					var url = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!Sale&formCondition=pi_id=" + id + "&gridCondition=pd_piid=" + id;
    	    		    					showError('<a href="javascript:openUrl(\''+url+'\');">' + '出货单'+id + '</a>');    	    		    					
										}
										if(res.exceptionInfo){
    	    			   				showError(res.exceptionInfo);
    	    			   			    }
									}
								});
							} else {
								return;
							}
						});
		        	}
		        }
		    },{
		    	iconCls: 'main-msg',
		        text: $I18N.common.button.erpToOtheroutButton,
		        listeners: {
		        	click: function(btn){
		        		var form= Ext.getCmp("form");
		        		var id = Ext.getCmp("ss_id").value; 
						form.setLoading(true);					
						warnMsg('确定要出库吗?', function(btn){
							if (btn == 'yes') {
								Ext.Ajax.request({
									url:basePath + "b2b/product/CustSendToProdInout.action",
									params:{									
										formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
									},
									method:'post',
									callback:function(options,success,response){
									    form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											var id = res.id;
    	    		    					var url = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!OtherOut&formCondition=pi_id=" + id + "&gridCondition=pd_piid=" + id;
    	    		    					showError('<a href="javascript:openUrl(\''+url+'\');">' + '其它出库单'+id + '</a>');  
										}
										if(res.exceptionInfo){
    	    			   				  showError(res.exceptionInfo);
    	    			   			    }
									}
								});
							} else {
								return;
							}
						});
		        	
		        	}
		        }
		    }],
			initComponent : function(){ 
				this.callParent(arguments); 
			},
		handler: function(){
		}
	});