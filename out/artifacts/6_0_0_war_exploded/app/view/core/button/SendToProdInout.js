/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.SendToProdInout',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSendToProdInoutButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'SendToProdInout',
    	tooltip: '关联认定入库',    	
    	text: $I18N.common.button.erpSendToProdInoutButton,		
		width: 120,
		 menu: [{
				iconCls: 'main-msg',
		        text: $I18N.common.button.erpToPurInoutButton,
		        listeners: {
		        	click: function(btn){
		        		var form= Ext.getCmp("form");
		        		var id = Ext.getCmp("ss_id").value; 
						form.setLoading(true);
						warnMsg('确定要关联入库吗?', function(btn){
							if (btn == 'yes') {
								Ext.Ajax.request({
									url:basePath + "scm/product/SendToPurInout.action",
									params:{									
										formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
									},
									method:'post',
									callback:function(options,success,response){
									    form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											var id = res.id;
    	    		    					var url = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckin&formCondition=pi_id=" + id + "&gridCondition=pd_piid=" + id;
    	    		    					showError('<a href="javascript:openUrl(\''+url+'\');">' + '采购验收单'+id + '</a>');    	    		    					
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
		        text: $I18N.common.button.erpToOtherInoutButton,
		        listeners: {
		        	click: function(btn){
		        		var form= Ext.getCmp("form");
		        		var id = Ext.getCmp("ss_id").value; 
						form.setLoading(true);					
						warnMsg('确定要关联入库吗?', function(btn){
							if (btn == 'yes') {
								Ext.Ajax.request({
									url:basePath + "scm/product/SendToProdInout.action",
									params:{									
										formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
									},
									method:'post',
									callback:function(options,success,response){
									    form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											var id = res.id;
    	    		    					var url = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!OtherIn&formCondition=pi_id=" + id + "&gridCondition=pd_piid=" + id;
    	    		    					showError('<a href="javascript:openUrl(\''+url+'\');">' + '其它入库单'+id + '</a>');  
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